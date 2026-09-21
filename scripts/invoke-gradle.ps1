param(
    [Parameter(Mandatory = $true)]
    [ValidateSet("assembleDebug", "testDebugUnitTest")]
    [string]$Task
)

$ErrorActionPreference = "Stop"

$sourceRoot = [System.IO.Path]::GetFullPath((Split-Path $PSScriptRoot -Parent))
$systemTemp = [System.IO.Path]::GetFullPath([System.IO.Path]::GetTempPath())
$buildRoot = Join-Path $systemTemp ("TutorLink-vscode-" + [Guid]::NewGuid().ToString("N"))

if (-not $buildRoot.StartsWith($systemTemp, [System.StringComparison]::OrdinalIgnoreCase)) {
    throw "La carpeta temporal calculada no esta dentro del directorio temporal del sistema."
}

New-Item -ItemType Directory -Path $buildRoot | Out-Null

try {
    Write-Host "Preparando compilacion fuera de OneDrive..." -ForegroundColor Cyan
    & robocopy $sourceRoot $buildRoot /E `
        /XD "$sourceRoot\.git" "$sourceRoot\.gradle" "$sourceRoot\.idea" `
            "$sourceRoot\.kotlin" "$sourceRoot\build" "$sourceRoot\app\build" `
        /XF local.properties /NFL /NDL /NJH /NJS /NP

    if ($LASTEXITCODE -gt 7) {
        throw "No se pudo preparar el proyecto temporal. Robocopy: $LASTEXITCODE"
    }

    $sdkRoot = if ($env:ANDROID_HOME) {
        $env:ANDROID_HOME
    } else {
        Join-Path $env:LOCALAPPDATA "Android\Sdk"
    }
    $env:ANDROID_HOME = $sdkRoot

    Push-Location $buildRoot
    try {
        & ".\gradlew.bat" $Task --console=plain
        $gradleExitCode = $LASTEXITCODE
    } finally {
        Pop-Location
    }

    if ($gradleExitCode -ne 0) {
        exit $gradleExitCode
    }

    if ($Task -eq "assembleDebug") {
        $generatedApk = Join-Path $buildRoot "app\build\outputs\apk\debug\app-debug.apk"
        $workspaceApk = Join-Path $sourceRoot "app\build\outputs\apk\debug\app-debug.apk"

        if (-not (Test-Path -LiteralPath $generatedApk)) {
            throw "Gradle no genero el APK esperado en $generatedApk"
        }

        $workspaceApkDirectory = Split-Path $workspaceApk -Parent
        if (-not (Test-Path -LiteralPath $workspaceApkDirectory)) {
            New-Item -ItemType Directory -Path $workspaceApkDirectory | Out-Null
        }
        Copy-Item -LiteralPath $generatedApk -Destination $workspaceApk -Force
        Write-Host "APK disponible en $workspaceApk" -ForegroundColor Green
    }
} finally {
    $resolvedBuildRoot = [System.IO.Path]::GetFullPath($buildRoot)
    if (
        (Test-Path -LiteralPath $resolvedBuildRoot) -and
        $resolvedBuildRoot.StartsWith($systemTemp, [System.StringComparison]::OrdinalIgnoreCase) -and
        (Split-Path $resolvedBuildRoot -Leaf).StartsWith("TutorLink-vscode-")
    ) {
        Remove-Item -LiteralPath $resolvedBuildRoot -Recurse -Force
    }
}
