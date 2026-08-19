param(
  [Parameter(Mandatory = $true)]
  [string]$BackupDirectory
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($env:BACKUP_ENCRYPTION_PASSWORD) -or $env:BACKUP_ENCRYPTION_PASSWORD.Length -lt 20) {
  throw "BACKUP_ENCRYPTION_PASSWORD en az 20 karakter olarak tanimlanmali."
}

$resolvedDirectory = (Resolve-Path -LiteralPath $BackupDirectory).Path
$backupRoot = (Resolve-Path -LiteralPath "backups").Path
if (-not $resolvedDirectory.StartsWith("$backupRoot\", [System.StringComparison]::OrdinalIgnoreCase)) {
  throw "Yalnizca backups klasoru altindaki yedekler sifrelenebilir."
}

$archivePath = "$resolvedDirectory.zip"
$encryptedPath = "$archivePath.enc"
$verificationPath = "$archivePath.verify"

if (Test-Path -LiteralPath $archivePath) { throw "Gecici arsiv zaten var: $archivePath" }
if (Test-Path -LiteralPath $encryptedPath) { throw "Sifreli arsiv zaten var: $encryptedPath" }
if (Test-Path -LiteralPath $verificationPath) { throw "Dogrulama arsivi zaten var: $verificationPath" }

Compress-Archive -Path (Join-Path $resolvedDirectory "*") -DestinationPath $archivePath -CompressionLevel Optimal
& node scripts/encrypt-backup.mjs $archivePath $encryptedPath
if ($LASTEXITCODE -ne 0) { throw "Yedek sifrelenemedi." }

& node scripts/decrypt-backup.mjs $encryptedPath $verificationPath
if ($LASTEXITCODE -ne 0) { throw "Sifreli yedek geri acilamadi." }

$archiveHash = (Get-FileHash -LiteralPath $archivePath -Algorithm SHA256).Hash
$verificationHash = (Get-FileHash -LiteralPath $verificationPath -Algorithm SHA256).Hash
if ($archiveHash -ne $verificationHash) { throw "Sifreleme dogrulamasi basarisiz: arsiv hashleri farkli." }

Remove-Item -LiteralPath $verificationPath -Force
Remove-Item -LiteralPath $archivePath -Force

$encryptedHash = Get-FileHash -LiteralPath $encryptedPath -Algorithm SHA256
[ordered]@{
  file = (Split-Path -Leaf $encryptedPath)
  bytes = (Get-Item -LiteralPath $encryptedPath).Length
  sha256 = $encryptedHash.Hash.ToLowerInvariant()
  verified_at = (Get-Date).ToUniversalTime().ToString("o")
} | ConvertTo-Json | Set-Content -LiteralPath "$encryptedPath.manifest.json" -Encoding utf8

Write-Host "Sifreli yedek dogrulandi: $encryptedPath"
Write-Warning "Sifreli kopyayi ayri bir konuma tasidiktan sonra acik yedek klasorunu guvenli sekilde silin."
