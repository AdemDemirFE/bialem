param(
  [string]$OutputRoot = "backups",
  [switch]$Linked
)

$ErrorActionPreference = "Stop"

if (-not $Linked -and [string]::IsNullOrWhiteSpace($env:SUPABASE_DB_URL)) {
  throw "SUPABASE_DB_URL tanimli degil. Supabase Connect ekranindaki Session pooler URL'sini bu ortam degiskenine ekleyin."
}

$connectionArgs = if ($Linked) {
  @("--linked")
} else {
  @("--db-url", $env:SUPABASE_DB_URL)
}

$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$backupDirectory = Join-Path $OutputRoot $timestamp
New-Item -ItemType Directory -Path $backupDirectory -Force | Out-Null

$rolesPath = Join-Path $backupDirectory "roles.sql"
$schemaPath = Join-Path $backupDirectory "schema.sql"
$dataPath = Join-Path $backupDirectory "data.sql"

Write-Host "Supabase roller yedekleniyor..."
& npx.cmd supabase db dump @connectionArgs -f $rolesPath --role-only
if ($LASTEXITCODE -ne 0) { throw "Rol yedegi olusturulamadi." }

Write-Host "Supabase semasi yedekleniyor..."
& npx.cmd supabase db dump @connectionArgs -f $schemaPath
if ($LASTEXITCODE -ne 0) { throw "Sema yedegi olusturulamadi." }

Write-Host "Supabase verileri yedekleniyor..."
& npx.cmd supabase db dump `
  @connectionArgs `
  -f $dataPath `
  --use-copy `
  --data-only `
  -x "storage.buckets_vectors" `
  -x "storage.vector_indexes"
if ($LASTEXITCODE -ne 0) { throw "Veri yedegi olusturulamadi." }

$files = @($rolesPath, $schemaPath, $dataPath)
foreach ($file in $files) {
  if (-not (Test-Path -LiteralPath $file) -or (Get-Item -LiteralPath $file).Length -eq 0) {
    throw "Yedek dosyasi eksik veya bos: $file"
  }
}

$checksums = $files | ForEach-Object {
  $item = Get-Item -LiteralPath $_
  $hash = Get-FileHash -LiteralPath $_ -Algorithm SHA256
  [ordered]@{
    file = $item.Name
    bytes = $item.Length
    sha256 = $hash.Hash.ToLowerInvariant()
  }
}

[ordered]@{
  created_at = (Get-Date).ToUniversalTime().ToString("o")
  files = $checksums
} | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath (Join-Path $backupDirectory "manifest.json") -Encoding utf8

Write-Host "Veritabani yedegi hazir: $backupDirectory"
Write-Warning "Bu yedek Storage nesnelerinin gercek dosyalarini icermez. Storage yedegini runbook'a gore ayrica alin."
