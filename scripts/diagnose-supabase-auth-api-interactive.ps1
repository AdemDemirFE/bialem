param(
  [Parameter(Mandatory = $true)]
  [ValidatePattern('^[a-z]{20}$')]
  [string]$ProjectRef
)

$ErrorActionPreference = "Stop"
$productionProjectRef = "tvaatpmlqlcnyjsvzlcy"

if ($ProjectRef -eq $productionProjectRef) {
  throw "Guvenlik engeli: production Auth hedefinde tani calistirilamaz."
}

$secretKey = Read-Host "Staging projesinin service_role/secret key degerini girin" -AsSecureString
$testEmail = Read-Host "Geri yuklenen test kullanicisinin e-posta adresini girin"
$keyPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secretKey)

try {
  $env:TARGET_SUPABASE_PROJECT_REF = $ProjectRef
  $env:TARGET_SUPABASE_URL = "https://$ProjectRef.supabase.co"
  $env:TARGET_SUPABASE_SECRET_KEY = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($keyPointer)
  $env:TARGET_TEST_EMAIL = $testEmail

  & node scripts/diagnose-supabase-auth-api.mjs
  if ($LASTEXITCODE -eq 2) {
    throw "Auth API tani sonucu eksik: e-posta saglayicisi veya kullanici gorunurlugu dogrulanamadi."
  }
  if ($LASTEXITCODE -ne 0) {
    throw "Staging Auth API tani testi basarisiz oldu."
  }
} finally {
  Remove-Item Env:TARGET_SUPABASE_PROJECT_REF -ErrorAction SilentlyContinue
  Remove-Item Env:TARGET_SUPABASE_URL -ErrorAction SilentlyContinue
  Remove-Item Env:TARGET_SUPABASE_SECRET_KEY -ErrorAction SilentlyContinue
  Remove-Item Env:TARGET_TEST_EMAIL -ErrorAction SilentlyContinue
  if ($keyPointer -ne [IntPtr]::Zero) {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($keyPointer)
  }
  $secretKey = $null
}
