const { withAndroidManifest } = require("expo/config-plugins");

const blockedPermissions = new Set([
  "android.permission.RECORD_AUDIO",
  "android.permission.READ_EXTERNAL_STORAGE",
  "android.permission.WRITE_EXTERNAL_STORAGE",
  "android.permission.SYSTEM_ALERT_WINDOW"
]);

module.exports = function withBlockedAndroidPermissions(config) {
  return withAndroidManifest(config, (androidConfig) => {
    const manifest = androidConfig.modResults.manifest;
    manifest["uses-permission"] = (manifest["uses-permission"] || []).filter(
      (permission) => !blockedPermissions.has(permission?.$?.["android:name"])
    );

    return androidConfig;
  });
};
