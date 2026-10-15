param(
    [string]$AvdName = "Pixel_10a"
)

$ErrorActionPreference = "Stop"

$sdkRoot = if ($env:ANDROID_HOME) {
    $env:ANDROID_HOME
} else {
    Join-Path $env:LOCALAPPDATA "Android\Sdk"
}

$adb = Join-Path $sdkRoot "platform-tools\adb.exe"
$emulator = Join-Path $sdkRoot "emulator\emulator.exe"
$apk = Join-Path $PSScriptRoot "..\app\build\outputs\apk\debug\app-debug.apk"
$gradleScript = Join-Path $PSScriptRoot "invoke-gradle.ps1"

if (-not (Test-Path -LiteralPath $adb)) {
    throw "No se encontro adb en $adb. Revisa la instalacion del Android SDK."
}

if (-not (Test-Path -LiteralPath $emulator)) {
    throw "No se encontro el emulador en $emulator. Revisa la instalacion del Android SDK."
}

$connectedDevice = & $adb devices |
    Where-Object { $_ -match "\sdevice$" } |
    Select-Object -First 1

if (-not $connectedDevice) {
    $availableAvds = & $emulator -list-avds
    if ($AvdName -notin $availableAvds) {
        throw "El emulador '$AvdName' no existe. Disponibles: $($availableAvds -join ', ')"
    }

    Write-Host "Iniciando emulador $AvdName..." -ForegroundColor Cyan
    Start-Process -FilePath $emulator -ArgumentList "-avd", $AvdName, "-no-snapshot-save"
    & $adb wait-for-device

    $bootCompleted = $false
    for ($attempt = 0; $attempt -lt 90; $attempt++) {
        if ((& $adb shell getprop sys.boot_completed).Trim() -eq "1") {
            $bootCompleted = $true
            break
        }
        Start-Sleep -Seconds 2
    }

    if (-not $bootCompleted) {
        throw "El emulador no termino de iniciar dentro del tiempo esperado."
    }
}

Write-Host "Compilando TutorLink..." -ForegroundColor Cyan
$env:ANDROID_HOME = $sdkRoot
& $gradleScript -Task assembleDebug
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

if (-not (Test-Path -LiteralPath $apk)) {
    throw "La compilacion termino sin generar el APK esperado en $apk"
}

Write-Host "Instalando APK..." -ForegroundColor Cyan
& $adb install -r $apk
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

Write-Host "Abriendo TutorLink..." -ForegroundColor Green
& $adb shell am start -n "com.example.app_tutorias/.MainActivity"
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

Write-Host "TutorLink esta ejecutandose en Android." -ForegroundColor Green
