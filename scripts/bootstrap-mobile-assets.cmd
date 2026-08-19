@echo off
setlocal

set "ROOT=%~dp0.."
set "ASSETS=%ROOT%\mobile\assets"
set "COVERS=%ASSETS%\community-covers"
set "PNG=%~dp0assets\placeholder.png.b64"
set "JPG=%~dp0assets\placeholder.jpg.b64"

if not exist "%ASSETS%" mkdir "%ASSETS%"
if not exist "%COVERS%" mkdir "%COVERS%"

call :decode "%ASSETS%\app-icon.png" "%PNG%"
call :decode "%ASSETS%\adaptive-icon.png" "%PNG%"
call :decode "%ASSETS%\bialem-logo.png" "%PNG%"
call :decode "%ASSETS%\onboarding-worlds.png" "%PNG%"
call :decode "%COVERS%\nature-outdoor.jpg" "%JPG%"
call :decode "%COVERS%\culture-art.jpg" "%JPG%"
call :decode "%COVERS%\sports-competition.jpg" "%JPG%"
call :decode "%COVERS%\tabletop-games.jpg" "%JPG%"
call :decode "%COVERS%\evening-entertainment.png" "%PNG%"
call :decode "%COVERS%\kiz-nesesi.png" "%PNG%"
exit /b 0

:decode
if exist "%~1" exit /b 0
certutil -decode "%~2" "%~1" >nul 2>&1
if errorlevel 1 exit /b 1
echo   Olusturuldu: %~1
exit /b 0
