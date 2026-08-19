param(
  [string]$BackupDirectory = "backups/20260802-165133",
  [Parameter(Mandatory = $true)]
  [ValidatePattern('^[a-z]{20}$')]
  [string]$ProjectRef
)

$ErrorActionPreference = "Stop"
$productionProjectRef = "tvaatpmlqlcnyjsvzlcy"

if ($ProjectRef -eq $productionProjectRef) {
  throw "Guvenlik engeli: production Supabase projesine restore testi yapilamaz."
}

$resolvedBackup = (Resolve-Path -LiteralPath $BackupDirectory).Path
foreach ($fileName in @("roles.sql", "schema.sql", "data.sql")) {
  $filePath = Join-Path $resolvedBackup $fileName
  if (-not (Test-Path -LiteralPath $filePath)) {
    throw "Restore dosyasi bulunamadi: $filePath"
  }
}

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
  throw "Docker bulunamadi. Docker Desktop'i acip yeniden deneyin."
}

$connectionString = Read-Host "Session pooler baglanti adresini [YOUR-PASSWORD] degismeden yapistirin"
$parseableConnectionString = $connectionString.Trim().Replace("[YOUR-PASSWORD]", "placeholder")

try {
  $connectionUri = [Uri]$parseableConnectionString
} catch {
  throw "Baglanti adresi okunamadi. Supabase Connect ekranindaki Session pooler URI degerini kullanin."
}

$databaseUser = [Uri]::UnescapeDataString(($connectionUri.UserInfo -split ':', 2)[0])
$databaseHost = $connectionUri.Host
$databasePort = $connectionUri.Port
$databaseName = $connectionUri.AbsolutePath.TrimStart('/')

if ($databaseUser -ne "postgres.$ProjectRef") {
  throw "Guvenlik engeli: baglanti adresi beklenen staging Project Ref ile eslesmiyor."
}

if ($databaseHost -notlike "*.pooler.supabase.com") {
  throw "Guvenlik engeli: Session pooler sunucusu bulunamadi."
}

if ($databasePort -ne 5432) {
  throw "Guvenlik engeli: Session pooler portu 5432 olmali. Transaction pooler (6543) kullanmayin."
}

if ($databaseName -ne "postgres") {
  throw "Guvenlik engeli: hedef veritabani postgres olmali."
}

$securePassword = Read-Host "Yeni staging projesinin Database Password degerini girin" -AsSecureString
$passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)

try {
  $env:PGPASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)
  $mount = "type=bind,source=$resolvedBackup,target=/backup,readonly"

  Write-Host "Hedef dogrulandi: $ProjectRef (staging)"
  Write-Host "Yedek yalnizca gecici Supabase projesine geri yukleniyor..."

  & docker run --rm `
    --env PGPASSWORD `
    --mount $mount `
    postgres:17-alpine `
    psql `
    --host $databaseHost `
    --port $databasePort `
    --username $databaseUser `
    --dbname $databaseName `
    --set ON_ERROR_STOP=1 `
    --single-transaction `
    --file /backup/roles.sql `
    --file /backup/schema.sql `
    --command "SET session_replication_role = replica" `
    --file /backup/data.sql

  if ($LASTEXITCODE -ne 0) {
    throw "Restore testi basarisiz oldu. Hata ayrintisini kaydedin; production'a gecmeyin."
  }

  Write-Host "Restore tamamlandi. Simdi tablo sayimlari ve staging girisi dogrulanabilir."
} finally {
  Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue
  if ($passwordPointer -ne [IntPtr]::Zero) {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
  }
  $securePassword = $null
}
