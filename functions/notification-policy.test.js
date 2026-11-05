"use strict";

const test = require("node:test");
const assert = require("node:assert/strict");
const {shouldDeliver} = require("./notification-policy");

test("master switch disables all pushes", () => {
  assert.equal(shouldDeliver("MESSAGE", {pushNotifications: false}), false);
  assert.equal(shouldDeliver("MATCH", {pushNotifications: false}), false);
});

test("message and match preferences are independent", () => {
  assert.equal(shouldDeliver("MESSAGE", {messageAlerts: false}), false);
  assert.equal(shouldDeliver("MATCH", {messageAlerts: false}), true);
  assert.equal(shouldDeliver("INTERVIEW", {matchAlerts: false}), false);
  assert.equal(shouldDeliver("STAGE", {matchAlerts: false}), false);
});

test("unknown notification types are rejected", () => {
  assert.equal(shouldDeliver("UNTRUSTED", {}), false);
});
