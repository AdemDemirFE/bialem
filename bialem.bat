@echo off
setlocal enabledelayedexpansion

set "ROOT=%~dp0"
if "%ROOT:~-1%"=="\" set "ROOT=%ROOT:~0,-1%"
cd /d "%ROOT%"

:menu
cls
echo.
echo ============================================================
echo   BIALEM - Tek menu (kurulum / web / mobil / apk)
echo ============================================================
echo   Dizin: %CD%
echo.
echo   1  Kurulum          npm install + env + gorseller
echo   B  Veritabani       Docker Postgres + JDL/Liquibase sema
echo   2  Web (hepsi)      Backend + Admin + Mobil web
echo   3  Admin web        http://localhost:3000
echo   4  Mobil web        http://localhost:5173
echo   5  Mobil (Capacitor) Android Studio / Xcode
echo   6  Android APK      Capacitor native derleme
echo   7  Temiz kurulum    node_modules sil + yeniden kur
echo   8  Env duzenle      .env dosyalarini Notepad ile ac
echo   9  Durdur           Acik servisleri kapat
echo   0  Cikis
echo.
set /p CHOICE="Seciminiz (0-9): "

if "%CHOICE%"=="1" goto setup
if /I "%CHOICE%"=="B" goto create_db
if "%CHOICE%"=="2" goto web_all
if "%CHOICE%"=="3" goto web_admin
if "%CHOICE%"=="4" goto web_mobile
if "%CHOICE%"=="5" goto mobile_phone
if "%CHOICE%"=="6" goto build_apk
if "%CHOICE%"=="7" goto clean_install
if "%CHOICE%"=="8" goto edit_env
if "%CHOICE%"=="9" goto stop_all
if "%CHOICE%"=="0" exit /b 0
goto menu

:setup
call :check_node || goto menu_pause
echo.
echo === KURULUM ===
call :ensure_deps || goto menu_pause
call scripts\bootstrap-mobile-assets.cmd
call node scripts\bootstrap-local-dev.mjs
call :ensure_logo_alias
call :check_env_warn
echo.
echo Kurulum tamam.
goto menu_pause

:create_db
echo.
echo === VERITABANI (JDL / Liquibase) ===
cd /d "%ROOT%\backend"
call create-db.cmd
cd /d "%ROOT%"
goto menu_pause

:web_all
call :check_node || goto menu_pause
call :ensure_deps || goto menu_pause
call :ensure_logo_alias
echo.
echo Admin  : http://localhost:3000
echo Mobil  : http://localhost:5173
echo API    : http://localhost:8080
start "Bialem Backend" cmd /k "cd /d %ROOT%\backend && mvnw"
timeout /t 2 /nobreak >nul
start "Bialem Admin" cmd /k "cd /d %ROOT% && node scripts\run-next-dev.mjs dev"
timeout /t 2 /nobreak >nul
start "Bialem Mobile Web" cmd /k "cd /d %ROOT% && node scripts\run-expo.mjs start --web"
echo Uc pencere acildi. Durdurmak icin menu 9.
goto menu_pause

:web_admin
call :check_node || goto menu_pause
call :ensure_deps || goto menu_pause
echo.
echo Admin: http://localhost:3000
start "Bialem Admin" cmd /k "cd /d %ROOT% && node scripts\run-next-dev.mjs dev"
goto menu_pause

:web_mobile
call :check_node || goto menu_pause
call :ensure_deps || goto menu_pause
call :ensure_logo_alias
echo.
echo Mobil web: http://localhost:5173
start "Bialem Mobile Web" cmd /k "cd /d %ROOT% && node scripts\run-expo.mjs start --web"
goto menu_pause

:mobile_phone
call :check_node || goto menu_pause
call :ensure_deps || goto menu_pause
echo.
echo Capacitor canli gelistirme: once mobil web'i acin, sonra native projeyi senkronlayin.
start "Bialem Mobile Web" cmd /k "cd /d %ROOT% && node scripts\run-expo.mjs start --web"
goto menu_pause

:build_apk
call :check_node || goto menu_pause
call :ensure_deps || goto menu_pause
call :ensure_logo_alias
echo.
echo === ANDROID (Capacitor) ===
cd mobile
call npm.cmd run android
cd /d "%ROOT%"
goto menu_pause

:clean_install
call :check_node || goto menu_pause
echo.
echo node_modules siliniyor...
if exist node_modules rmdir /s /q node_modules
if exist admin\node_modules rmdir /s /q admin\node_modules
if exist mobile\node_modules rmdir /s /q mobile\node_modules
call npm.cmd install
if errorlevel 1 (
  echo npm install basarisiz.
  goto menu_pause
)
call scripts\bootstrap-mobile-assets.cmd
call node scripts\bootstrap-local-dev.mjs
call :ensure_logo_alias
echo Temiz kurulum tamam.
goto menu_pause

:edit_env
echo.
if not exist mobile\.env copy mobile\.env.template mobile\.env >nul 2>&1
if not exist admin\.env.local copy admin\.env.local.template admin\.env.local >nul 2>&1
start notepad "%ROOT%\mobile\.env"
start notepad "%ROOT%\admin\.env.local"
goto menu_pause

:stop_all
echo.
taskkill /FI "WINDOWTITLE eq Bialem Admin*" /T /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq Bialem Mobile*" /T /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq Bialem Backend*" /T /F >nul 2>&1
for %%P in (3000 5173 8080 4173) do call :kill_port %%P
echo Tamam.
goto menu_pause

:ensure_logo_alias
if exist "mobile\assets\bialem-logo.png" exit /b 0
if exist "mobile\assets\app-icon.png" (
  copy /y "mobile\assets\app-icon.png" "mobile\assets\bialem-logo.png" >nul
  echo Logo olusturuldu: bialem-logo.png
)
exit /b 0

:menu_pause
echo.
pause
goto menu

:check_node
where node >nul 2>&1
if errorlevel 1 (
  echo HATA: Node.js yok.
  exit /b 1
)
exit /b 0

:ensure_deps
set "OK_NEXT=0"
set "OK_VITE=0"
if exist admin\node_modules\next\dist\server\require-hook.js set "OK_NEXT=1"
if exist node_modules\next\dist\server\require-hook.js set "OK_NEXT=1"
if exist mobile\node_modules\vite\bin\vite.js set "OK_VITE=1"
if exist node_modules\vite\bin\vite.js set "OK_VITE=1"
if "%OK_NEXT%"=="1" if "%OK_VITE%"=="1" exit /b 0
echo Bagimliliklar kuruluyor...
call npm.cmd install
if errorlevel 1 exit /b 1
exit /b 0

:check_env_warn
if not exist mobile\.env (
  echo [UYARI] mobile\.env yok - menu 8
  exit /b 0
)
findstr /i /c:"your-project" /c:"your-publishable" mobile\.env >nul 2>&1
if not errorlevel 1 echo [UYARI] mobile\.env ornek degerler iceriyor - menu 8
exit /b 0

:kill_port
for /f "tokens=5" %%a in ('netstat -ano ^| findstr "LISTENING" ^| findstr ":%~1"') do (
  if not "%%a"=="0" taskkill /F /PID %%a >nul 2>&1
)
exit /b 0
