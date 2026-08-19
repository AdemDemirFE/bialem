param(
  [Parameter(Mandatory = $true)]
  [ValidatePattern('^[a-z]{20}$')]
  [string]$ProjectRef
)

$ErrorActionPreference = "Stop"
$productionProjectRef = "tvaatpmlqlcnyjsvzlcy"

if ($ProjectRef -eq $productionProjectRef) {
  throw "Guvenlik engeli: production projesinde restore dogrulamasi calistirilamaz."
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

$securePassword = Read-Host "Staging Database Password degerini girin" -AsSecureString
$passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)

$verificationSql = @'
select 'auth.users' as item, count(*)::bigint as actual, 8::bigint as expected from auth.users
union all select 'public.profiles', count(*), 8 from public.profiles
union all select 'public.communities', count(*), 27 from public.communities
union all select 'public.community_members', count(*), 52 from public.community_members
union all select 'public.events', count(*), 1 from public.events
union all select 'public.event_participants', count(*), 1 from public.event_participants
union all select 'public.follows', count(*), 9 from public.follows
union all select 'public.notifications', count(*), 34 from public.notifications
union all select 'public.push_tokens', count(*), 7 from public.push_tokens
union all select 'public.partner_venues', count(*), 1 from public.partner_venues
union all select 'public.partner_offers', count(*), 1 from public.partner_offers
order by item;

select
  count(*) filter (where relrowsecurity) as rls_enabled_tables,
  count(*) as public_tables
from pg_class c
join pg_namespace n on n.oid = c.relnamespace
where n.nspname = 'public' and c.relkind = 'r';

select count(*) as public_rls_policies
from pg_policies
where schemaname = 'public';

select count(*) as public_functions
from pg_proc p
join pg_namespace n on n.oid = p.pronamespace
where n.nspname = 'public';
'@

try {
  $env:PGPASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)

  Write-Host "Salt okunur staging dogrulamasi calistiriliyor..."
  $verificationSql | & docker run --rm -i `
    --env PGPASSWORD `
    postgres:17-alpine `
    psql `
    --host $databaseHost `
    --port $databasePort `
    --username $databaseUser `
    --dbname $databaseName `
    --set ON_ERROR_STOP=1 `
    --no-psqlrc

  if ($LASTEXITCODE -ne 0) {
    throw "Restore dogrulamasi basarisiz oldu."
  }

  Write-Host "Salt okunur restore dogrulamasi tamamlandi."
} finally {
  Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue
  if ($passwordPointer -ne [IntPtr]::Zero) {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
  }
  $securePassword = $null
}
