@echo off
setlocal EnableExtensions
cd /d "%~dp0.."
echo === vite android-test build ===
call npm run build:android:test
if errorlevel 1 exit /b 1
echo === cap sync android ===
call npx cap sync android
if errorlevel 1 exit /b 1
echo === verify capacitor.config.json ===
if not exist android\app\src\main\assets\capacitor.config.json (
  echo ERROR: capacitor.config.json missing
  exit /b 1
)
findstr /C:"\"androidScheme\": \"http\"" android\app\src\main\assets\capacitor.config.json >nul
if errorlevel 1 (
  echo ERROR: androidScheme is not http
  type android\app\src\main\assets\capacitor.config.json
  exit /b 1
)
findstr /C:"\"cleartext\": true" android\app\src\main\assets\capacitor.config.json >nul
if errorlevel 1 (
  echo ERROR: cleartext is not true
  type android\app\src\main\assets\capacitor.config.json
  exit /b 1
)
echo capacitor.config.json OK
type android\app\src\main\assets\capacitor.config.json
echo === gradle assembleDebug ===
cd android
call gradlew.bat clean assembleDebug
exit /b %ERRORLEVEL%
