import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import type { CapacitorConfig } from '@capacitor/cli';

const root = path.dirname(fileURLToPath(import.meta.url));
const androidTestFlag = path.join(root, '.capacitor-android-test');

/**
 * Android VPS test: Origin must be http://localhost (not https://localhost)
 * so fetch to http://191.215.36.29:8184 is not mixed content.
 *
 * Enabled when:
 * - BIALEM_CAP_ENV=android-test, or
 * - mobile/.capacitor-android-test exists (written by `vite build --mode android-test`)
 *
 * Production `vite build` deletes that flag so default https://localhost returns.
 */
const isAndroidTest =
  process.env.BIALEM_CAP_ENV === 'android-test' || fs.existsSync(androidTestFlag);

const config: CapacitorConfig = {
  appId: 'com.bialem.mobile',
  appName: 'BiAlem',
  webDir: 'dist',
  ...(isAndroidTest
    ? {
        server: {
          hostname: 'localhost',
          androidScheme: 'http',
          cleartext: true
        },
        android: {
          allowMixedContent: true
        }
      }
    : {}),
  // Custom scheme used by password-reset email bridge (bialem://reset-password?key=...)
  // Intent filters live in AndroidManifest.xml; HTTPS links open the public web bridge when the app is absent.
};

export default config;
