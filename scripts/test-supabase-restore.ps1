param(
  [Parameter(Mandatory = $true)]
  [string]$BackupDirectory,

  [Parameter(Mandatory = $true)]
  [string]$TargetDbUrl
)

$ErrorActionPreference = "Stop"
$productionProjectRef = "tvaatpmlqlcnyjsvzlcy"

if ($TargetDbUrl.Contains($productionProjectRef, [System.StringComparison]::OrdinalIgnoreCase)) {
  throw "Guvenlik engeli: production Supabase projesine restore testi yapilamaz."
}

if ($env:RESTORE_TEST_CONFIRM -ne "BIALEM_STAGING_RESTORE") {
  throw "Devam etmek icin RESTORE_TEST_CONFIRM=BIALEM_STAGING_RESTORE tanimlanmali."
}

if (-not (Get-Command psql -ErrorAction SilentlyContinue)) {
  throw "psql bulunamadi. PostgreSQL istemcisini kurup PATH'e ekleyin."
}

$rolesPath = Join-Path $BackupDirectory "roles.sql"
$schemaPath = Join-Path $BackupDirectory "schema.sql"
$dataPath = Join-Path $BackupDirectory "data.sql"

foreach ($file in @($rolesPath, $schemaPath, $dataPath)) {
  if (-not (Test-Path -LiteralPath $file)) {
    throw "Restore dosyasi bulunamadi: $file"
  }
}

Write-Host "Yedek yalnizca staging/test veritabanina geri yukleniyor..."
& psql `
  --single-transaction `
  --variable ON_ERROR_STOP=1 `
  --file $rolesPath `
  --file $schemaPath `
  --command "SET session_replication_role = replica" `
  --file $dataPath `
  --dbname $TargetDbUrl

if ($LASTEXITCODE -ne 0) {
  throw "Restore testi basarisiz oldu."
}

Write-Host "Restore testi tamamlandi. Tablo sayilari ve kritik akislari staging projesinde dogrulayin."
