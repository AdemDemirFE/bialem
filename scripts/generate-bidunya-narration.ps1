param(
  [Parameter(Mandatory = $true)]
  [string]$OutputDirectory
)

$ErrorActionPreference = "Stop"
New-Item -ItemType Directory -Force -Path $OutputDirectory | Out-Null

$lines = @(
  "Her gün binlerce insan görüyoruz...",
  "Ama giderek daha az insan tanıyoruz.",
  "Aynı şehirde yaşıyoruz.",
  "Aynı sokaklarda yürüyoruz.",
  "Belki de aynı şeyleri seviyoruz.",
  "Ama birbirimizi hiç bulamıyoruz."
)

for ($index = 0; $index -lt $lines.Count; $index++) {
  $voice = New-Object -ComObject SAPI.SpVoice
  $voice.Rate = -2
  $voice.Volume = 100
  $stream = New-Object -ComObject SAPI.SpFileStream
  # The legacy SAPI file stream cannot reliably open paths containing
  # non-ASCII characters, so synthesize in the system temp directory first.
  $tempPath = Join-Path $env:TEMP ("bialem-voice-{0}.wav" -f ($index + 1))
  $path = Join-Path $OutputDirectory ("voice-{0}.wav" -f ($index + 1))
  $stream.Open($tempPath, 3, $false)
  $voice.AudioOutputStream = $stream
  [void]$voice.Speak($lines[$index])
  $stream.Close()
  Copy-Item -LiteralPath $tempPath -Destination $path -Force
}
