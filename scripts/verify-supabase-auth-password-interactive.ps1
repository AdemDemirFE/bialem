param(
  [Parameter(Mandatory = $true)]
  [ValidatePattern('^[a-z]{20}$')]
  [string]$ProjectRef
)

$ErrorActionPreference = "Stop"
$productionProjectRef = "tvaatpmlqlcnyjsvzlcy"

if ($ProjectRef -eq $productionProjectRef) {
  throw "Guvenlik engeli: production Auth hedefinde parola dogrulamasi calistirilamaz."
}

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
  throw "Docker bulunamadi. Docker Desktop'i acip yeniden deneyin."
}

$connectionString = Read-Host "Session pooler baglanti adresini [YOUR-PASSWORD] degismeden yapistirin"
$parseableConnectionString = $connectionString.Trim().Replace("[YOUR-PASSWORD]", "placeholder")

try {
  $connectionUri = [Uri]$parseableConnectionString
} catch {
  throw "Baglanti adresi okunamadi. Session pooler URI degerini kullanin."
}

$databaseUser = [Uri]::UnescapeDataString(($connectionUri.UserInfo -split ':', 2)[0])
$databaseHost = $connectionUri.Host
$databasePort = $connectionUri.Port
$databaseName = $connectionUri.AbsolutePath.TrimStart('/')

if ($databaseUser -ne "postgres.$ProjectRef" -or
    $databaseHost -notlike "*.pooler.supabase.com" -or
    $databasePort -ne 5432 -or
    $databaseName -ne "postgres") {
  throw "Guvenlik engeli: staging Session pooler bilgileri beklenen hedefle eslesmiyor."
}

$testEmail = Read-Host "Giris testi yapilan e-posta adresini girin"
$secureTestPassword = Read-Host "Giris testinde kullandiginiz kullanici parolasini girin" -AsSecureString
$secureDatabasePassword = Read-Host "Staging Database Password degerini girin" -AsSecureString
$testPasswordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secureTestPassword)
$databasePasswordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secureDatabasePassword)

try {
  $plainTestPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($testPasswordPointer)
  $plainDatabasePassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($databasePasswordPointer)
  if ([string]::IsNullOrEmpty($plainTestPassword)) {
    throw "Kullanici parolasi bos olamaz."
  }
  $escapedEmail = $testEmail.Replace("'", "''")
  $escapedTestPassword = $plainTestPassword.Replace("'", "''")
  $env:PGPASSWORD = $plainDatabasePassword

  $verificationSql = @"
select
  encrypted_password = extensions.crypt('$escapedTestPassword', encrypted_password) as password_matches_backup
from auth.users
where lower(email) = lower('$escapedEmail');
"@

  $queryOutput = $verificationSql | & docker run --rm -i `
    --env PGPASSWORD `
    postgres:17-alpine `
    psql `
    --host $databaseHost `
    --port $databasePort `
    --username $databaseUser `
    --dbname $databaseName `
    --set ON_ERROR_STOP=1 `
    --no-psqlrc 2>&1

  $queryExitCode = $LASTEXITCODE
  $queryOutput | ForEach-Object {
    $_.ToString().Replace($plainTestPassword, "[REDACTED]").Replace($escapedTestPassword, "[REDACTED]")
  } | Write-Host

  if ($queryExitCode -ne 0) {
    throw "Staging parola hash dogrulamasi basarisiz oldu."
  }
} finally {
  Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue
  $plainTestPassword = $null
  $plainDatabasePassword = $null
  $escapedTestPassword = $null
  if ($testPasswordPointer -ne [IntPtr]::Zero) {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($testPasswordPointer)
  }
  if ($databasePasswordPointer -ne [IntPtr]::Zero) {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($databasePasswordPointer)
  }
  $secureTestPassword = $null
  $secureDatabasePassword = $null
  $testEmail = $null
}
