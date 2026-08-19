param(
  [Parameter(Mandatory = $true)]
  [ValidatePattern('^[a-z]{20}$')]
  [string]$ProjectRef
)

$ErrorActionPreference = "Stop"
$productionProjectRef = "tvaatpmlqlcnyjsvzlcy"

if ($ProjectRef -eq $productionProjectRef) {
  throw "Guvenlik engeli: production Auth hedefinde test yapilamaz."
}

$publishableKey = Read-Host "Staging projesinin publishable/anon key degerini girin" -AsSecureString
$testEmail = Read-Host "Geri yuklenen test kullanicisinin e-posta adresini girin"
$testPassword = Read-Host "Test kullanicisinin mevcut parolasini girin" -AsSecureString
$keyPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($publishableKey)
$passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($testPassword)

try {
  $env:TARGET_SUPABASE_PROJECT_REF = $ProjectRef
  $env:TARGET_SUPABASE_URL = "https://$ProjectRef.supabase.co"
  $env:TARGET_SUPABASE_PUBLISHABLE_KEY = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($keyPointer)
  $env:TARGET_TEST_EMAIL = $testEmail
  $env:TARGET_TEST_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)

  & node scripts/verify-supabase-auth.mjs
  if ($LASTEXITCODE -ne 0) {
    throw "Staging Auth testi basarisiz oldu."
  }
} finally {
  Remove-Item Env:TARGET_SUPABASE_PROJECT_REF -ErrorAction SilentlyContinue
  Remove-Item Env:TARGET_SUPABASE_URL -ErrorAction SilentlyContinue
  Remove-Item Env:TARGET_SUPABASE_PUBLISHABLE_KEY -ErrorAction SilentlyContinue
  Remove-Item Env:TARGET_TEST_EMAIL -ErrorAction SilentlyContinue
  Remove-Item Env:TARGET_TEST_PASSWORD -ErrorAction SilentlyContinue
  if ($keyPointer -ne [IntPtr]::Zero) {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($keyPointer)
  }
  if ($passwordPointer -ne [IntPtr]::Zero) {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
  }
  $publishableKey = $null
  $testPassword = $null
}
