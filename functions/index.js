"use strict";

const {onDocumentCreated} = require("firebase-functions/v2/firestore");
const {logger} = require("firebase-functions");
const {initializeApp} = require("firebase-admin/app");
const {getFirestore, FieldValue} = require("firebase-admin/firestore");
const {getMessaging} = require("firebase-admin/messaging");
const {shouldDeliver} = require("./notification-policy");

initializeApp();

const db = getFirestore();
const INVALID_RECIPIENT_CODES = new Set([
  "messaging/registration-token-not-registered",
  "messaging/invalid-registration-token",
  "messaging/invalid-recipient",
]);

function cleanText(value, maxLength) {
  return typeof value === "string" ? value.trim().slice(0, maxLength) : "";
}

async function recordResult(reference, status, details = {}) {
  const update = {
    "push.status": status,
    "push.processedAt": FieldValue.serverTimestamp(),
  };
  Object.entries(details).forEach(([key, value]) => {
    update[`push.${key}`] = value;
  });
  await reference.update(update);
}

exports.sendPushNotification = onDocumentCreated({
  document: "notifications/{notificationId}",
  memory: "256MiB",
  timeoutSeconds: 60,
  maxInstances: 10,
  retry: false,
}, async (event) => {
  const snapshot = event.data;
  if (!snapshot) return;

  const eventId = event.id || event.params.notificationId;
  const claimed = await db.runTransaction(async (transaction) => {
    const current = await transaction.get(snapshot.ref);
    if (!current.exists || current.get("push.eventId")) return false;
    transaction.set(snapshot.ref, {
      push: {
        eventId,
        status: "PROCESSING",
        claimedAt: FieldValue.serverTimestamp(),
      },
    }, {merge: true});
    return true;
  });
  if (!claimed) {
    logger.info("Skipping duplicate FCM event", {eventId});
    return;
  }

  const notification = snapshot.data();
  const notificationId = event.params.notificationId;
  const userId = cleanText(notification.userId, 128);
  const type = cleanText(notification.type, 32).toUpperCase();
  const title = cleanText(notification.title, 120);
  const body = cleanText(notification.body, 500);

  if (!userId || !title || !body || !shouldDeliver(type)) {
    logger.warn("Skipping malformed or unsupported notification", {notificationId, type});
    await recordResult(snapshot.ref, "SKIPPED", {reason: "INVALID_NOTIFICATION"});
    return;
  }

  const userSnapshot = await db.collection("users").doc(userId).get();
  const settings = userSnapshot.data()?.settings || {};
  if (!shouldDeliver(type, settings)) {
    await recordResult(snapshot.ref, "SKIPPED", {reason: "DISABLED_BY_USER"});
    return;
  }

  const devicesSnapshot = await db.collection("users").doc(userId)
      .collection("devices").where("enabled", "==", true).get();
  const targets = devicesSnapshot.docs.map((device) => {
    const data = device.data();
    const token = cleanText(data.token, 4096);
    if (token) return {reference: device.ref, messageTarget: {token}};
    return null;
  }).filter(Boolean);

  if (targets.length === 0) {
    await recordResult(snapshot.ref, "SKIPPED", {reason: "NO_REGISTERED_DEVICE"});
    return;
  }

  let successCount = 0;
  let failureCount = 0;
  const invalidReferences = [];
  for (let offset = 0; offset < targets.length; offset += 500) {
    const batch = targets.slice(offset, offset + 500);
    const messages = batch.map((target) => ({
      ...target.messageTarget,
      data: {
        notificationId,
        userId,
        type,
        title,
        body,
        matchId: cleanText(notification.matchId, 128),
      },
      android: {
        priority: "high",
      },
    }));
    const response = await getMessaging().sendEach(messages);
    successCount += response.successCount;
    failureCount += response.failureCount;
    response.responses.forEach((result, index) => {
      if (!result.success && INVALID_RECIPIENT_CODES.has(result.error?.code)) {
        invalidReferences.push(batch[index].reference);
      }
    });
  }

  await Promise.all(invalidReferences.map((reference) => reference.delete()));
  await recordResult(snapshot.ref, successCount > 0 ? "SENT" : "FAILED", {
    attempted: targets.length,
    successCount,
    failureCount,
  });
  logger.info("FCM notification processed", {
    notificationId,
    type,
    attempted: targets.length,
    successCount,
    failureCount,
  });
});
