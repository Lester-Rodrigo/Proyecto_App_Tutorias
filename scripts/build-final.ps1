param(
    [switch]$ValidateOnly,
    [string]$JdkPath = $env:JAVA_HOME
)

$ErrorActionPreference = 'Stop'
$sourceRoot = [IO.Path]::GetFullPath((Split-Path $PSScriptRoot -Parent))
if (-not $ValidateOnly -and -not (Test-Path -LiteralPath (Join-Path $sourceRoot 'keystore.properties'))) {
    throw 'Configura keystore.properties con la clave original antes de generar el release.'
}
if ($JdkPath) { $env:JAVA_HOME = $JdkPath }
$env:ANDROID_HOME = if ($env:ANDROID_HOME) { $env:ANDROID_HOME } else {
    Join-Path $env:LOCALAPPDATA 'Android\Sdk'
}
$buildRoot = Join-Path ([IO.Path]::GetTempPath()) ('TutorLink-distribution-' + [Guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $buildRoot | Out-Null
# Se conserva la copia local y sus informes para poder inspeccionar fallos.
& robocopy $sourceRoot $buildRoot /E /XD .git .gradle .idea .kotlin build `
    /XF local.properties RESUMEN_PROYECTO.md /NFL /NDL /NJH /NJS /NP
if ($LASTEXITCODE -gt 7) { throw "No se pudo preparar la compilacion: $LASTEXITCODE" }

$tasks = @('testDebugUnitTest', 'assembleDebug', 'assembleDebugAndroidTest', 'lintDebug')
if (-not $ValidateOnly) { $tasks += @('assembleRelease', 'lintRelease') }
$arguments = @('--console=plain', '--no-daemon', '--no-configuration-cache')
if ($JdkPath) {
    $arguments += @('-Porg.gradle.java.installations.auto-detect=false',
        '-Porg.gradle.java.installations.auto-download=false',
        "-Porg.gradle.java.installations.paths=$JdkPath")
}
Push-Location $buildRoot
try {
    & .\gradlew.bat @tasks @arguments
    if ($LASTEXITCODE -ne 0) { throw "Fallo Gradle. Informes en $buildRoot\app\build\reports" }
    if (-not $ValidateOnly) {
        $apk = Join-Path $buildRoot 'app\build\outputs\apk\release\app-release.apk'
        if (-not (Test-Path -LiteralPath $apk)) { throw 'No se genero el APK firmado esperado.' }
        $apkSigner = Join-Path $env:ANDROID_HOME 'build-tools\37.0.0\apksigner.bat'
        if (-not (Test-Path -LiteralPath $apkSigner)) { throw 'No se encontro apksigner en Build Tools 37.0.0.' }
        & $apkSigner verify --verbose --print-certs $apk
        if ($LASTEXITCODE -ne 0) { throw 'No se pudo verificar la firma del APK.' }
        $outputDirectory = Join-Path $sourceRoot 'app\build\outputs\final'
        New-Item -ItemType Directory -Path $outputDirectory -Force | Out-Null
        Copy-Item -LiteralPath $apk -Destination (Join-Path $outputDirectory 'TutorLink-1.2-3.apk') -Force
        Get-FileHash -LiteralPath (Join-Path $outputDirectory 'TutorLink-1.2-3.apk') -Algorithm SHA256
    }
    Write-Host "Compilacion e informes disponibles en $buildRoot"
} finally {
    Pop-Location
}
