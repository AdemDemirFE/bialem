@echo off
setlocal
cd /d "%~dp0"

echo.
echo === Bialem DB (JDL -^> Liquibase -^> PostgreSQL) ===
echo JDL yeniden import edilmez. Mevcut changelog'lar uygulanir.
echo Host: localhost:15432   DB/user: bialem
echo.

where docker >nul 2>&1
if errorlevel 1 (
  echo HATA: Docker yok. Docker Desktop'i acip tekrar deneyin.
  exit /b 1
)

echo [1/3] PostgreSQL konteyneri baslatiliyor...
docker compose -f src\main\docker\postgresql.yml up -d
if errorlevel 1 (
  echo HATA: docker compose basarisiz.
  exit /b 1
)

echo [2/3] Postgres hazir olana kadar bekleniyor...
set /a _n=0
:wait_pg
docker compose -f src\main\docker\postgresql.yml exec -T postgresql pg_isready -U bialem -d bialem >nul 2>&1
if not errorlevel 1 goto pg_ok
set /a _n+=1
if %_n% GEQ 30 (
  echo HATA: Postgres 60 sn icinde hazir olmadi. Docker Desktop calisiyor mu?
  exit /b 1
)
timeout /t 2 /nobreak >nul
goto wait_pg

:pg_ok
echo Postgres ayakta.

echo [3/3] Liquibase semasi uygulanıyor (JDL entity tablolari)...
call mvnw.cmd -Pdev liquibase:update
if errorlevel 1 (
  echo.
  echo Liquibase Maven hedefi basarisiz oldu.
  echo Alternatif: backend'i bir kez calistirin, Spring ayni changelog'lari uygular:
  echo   mvnw.cmd
  exit /b 1
)

echo.
echo Tablolar:
docker compose -f src\main\docker\postgresql.yml exec -T postgresql psql -U bialem -d bialem -c "\dt"
echo.
echo Tamam. JDBC: jdbc:postgresql://localhost:15432/bialem
exit /b 0
