param(
  [string]$BackupDirectory = "backups/20260802-165133",
  [Parameter(Mandatory = $true)]
  [ValidatePattern('^[a-z]{20}$')]
  [string]$ProjectRef
)

$ErrorActionPreference = "Stop"
$productionProjectRef = "tvaatpmlqlcnyjsvzlcy"

if ($ProjectRef -eq $productionProjectRef) {
  throw "Guvenlik engeli: production Storage hedefine restore yapilamaz."
}

$resolvedBackup = (Resolve-Path -LiteralPath $BackupDirectory).Path
if (-not (Test-Path -LiteralPath (Join-Path $resolvedBackup "storage-manifest.json"))) {
  throw "Storage manifest bulunamadi."
}

$secureServiceKey = Read-Host "Staging projesinin service_role/secret key degerini girin" -AsSecureString
$keyPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secureServiceKey)

try {
  $env:TARGET_SUPABASE_PROJECT_REF = $ProjectRef
  $env:TARGET_SUPABASE_URL = "https://$ProjectRef.supabase.co"
  $env:TARGET_SUPABASE_SERVICE_ROLE_KEY = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($keyPointer)

  & node scripts/restore-supabase-storage.mjs $resolvedBackup
  if ($LASTEXITCODE -ne 0) {
    throw "Storage restore testi basarisiz oldu."
  }
} finally {
  Remove-Item Env:TARGET_SUPABASE_PROJECT_REF -ErrorAction SilentlyContinue
  Remove-Item Env:TARGET_SUPABASE_URL -ErrorAction SilentlyContinue
  Remove-Item Env:TARGET_SUPABASE_SERVICE_ROLE_KEY -ErrorAction SilentlyContinue
  if ($keyPointer -ne [IntPtr]::Zero) {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($keyPointer)
  }
  $secureServiceKey = $null
}
