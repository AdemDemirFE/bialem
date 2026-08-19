param(
  [Parameter(Mandatory = $true)]
  [ValidatePattern('^[a-z]{20}$')]
  [string]$ProjectRef
)

$ErrorActionPreference = "Stop"
$productionProjectRef = "tvaatpmlqlcnyjsvzlcy"

if ($ProjectRef -eq $productionProjectRef) {
  throw "Guvenlik engeli: production Auth hedefinde tani sorgusu calistirilamaz."
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
$securePassword = Read-Host "Staging Database Password degerini girin" -AsSecureString
$passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)

$diagnosticSql = @'
select
  count(*) = 1 as user_exists,
  count(*) filter (where encrypted_password is not null and encrypted_password <> '') = 1 as password_hash_exists,
  count(*) filter (where email_confirmed_at is not null) = 1 as email_confirmed,
  count(*) filter (where deleted_at is null and (banned_until is null or banned_until < now())) = 1 as account_usable
from auth.users
where lower(email) = lower(:'test_email');

select
  exists (
    select 1
    from auth.identities identity_record
    join auth.users user_record on user_record.id = identity_record.user_id
    where lower(user_record.email) = lower(:'test_email')
      and identity_record.provider = 'email'
  ) as email_identity_exists;
'@

try {
  $env:PGPASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)

  $diagnosticSql | & docker run --rm -i `
    --env PGPASSWORD `
    postgres:17-alpine `
    psql `
    --host $databaseHost `
    --port $databasePort `
    --username $databaseUser `
    --dbname $databaseName `
    --set ON_ERROR_STOP=1 `
    --set "test_email=$testEmail" `
    --no-psqlrc

  if ($LASTEXITCODE -ne 0) {
    throw "Staging Auth tani sorgusu basarisiz oldu."
  }
} finally {
  Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue
  if ($passwordPointer -ne [IntPtr]::Zero) {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
  }
  $securePassword = $null
  $testEmail = $null
}
