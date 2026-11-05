"use strict";

const SUPPORTED_TYPES = new Set(["MATCH", "MESSAGE", "INTERVIEW", "STAGE"]);

function shouldDeliver(type, settings = {}) {
  if (!SUPPORTED_TYPES.has(type)) return false;
  if (settings.pushNotifications === false) return false;
  if (type === "MESSAGE") return settings.messageAlerts !== false;
  return settings.matchAlerts !== false;
}

module.exports = {shouldDeliver, SUPPORTED_TYPES};
