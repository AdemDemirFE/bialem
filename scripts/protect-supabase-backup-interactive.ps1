param(
  [Parameter(Mandatory = $true)]
  [string]$BackupDirectory
)

$ErrorActionPreference = "Stop"
$securePassword = Read-Host "En az 20 karakterlik yedek parolasini girin" -AsSecureString
$passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)

try {
  $env:BACKUP_ENCRYPTION_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)
  if ($env:BACKUP_ENCRYPTION_PASSWORD.Length -lt 20) {
    throw "Yedek parolasi en az 20 karakter olmali."
  }

  & powershell -ExecutionPolicy Bypass -File scripts/protect-supabase-backup.ps1 `
    -BackupDirectory $BackupDirectory
  if ($LASTEXITCODE -ne 0) { throw "Yedek koruma islemi basarisiz oldu." }
} finally {
  Remove-Item Env:BACKUP_ENCRYPTION_PASSWORD -ErrorAction SilentlyContinue
  [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
}
