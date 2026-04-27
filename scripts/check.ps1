$ErrorActionPreference = 'Stop'

$Root = Split-Path -Parent $PSScriptRoot
$Pass = $true

function Get-JavaMajorVersion {
    try {
        $VersionOutput = & java -version 2>&1 | Out-String
    } catch {
        return 0
    }

    if ($VersionOutput -match 'version\s+[^0-9]*(\d+)(?:\.(\d+))?') {
        if ($Matches[1] -eq '1' -and $Matches[2]) {
            return [int] $Matches[2]
        }

        return [int] $Matches[1]
    }

    return 0
}

function Use-CompatibleJdk {
    $Major = Get-JavaMajorVersion
    if ($Major -ge 17) {
        return
    }

    $IdeaJdk = Join-Path $env:USERPROFILE '.jdks\ms-21.0.10'
    $IdeaJava = Join-Path $IdeaJdk 'bin\java.exe'
    if (Test-Path $IdeaJava) {
        $env:JAVA_HOME = $IdeaJdk
        $env:Path = "${env:JAVA_HOME}\bin;${env:Path}"
    }
}

function Invoke-Step {
    param(
        [string] $Name,
        [string] $WorkingDirectory,
        [string] $CommandText
    )

    Write-Host ''
    Write-Host ">>> $Name"
    Write-Host $CommandText

    Push-Location $WorkingDirectory
    try {
        Invoke-Expression $CommandText
        Write-Host "[OK] $Name"
    } catch {
        Write-Host "[FAIL] $Name"
        Write-Host $_.Exception.Message
        $script:Pass = $false
    } finally {
        Pop-Location
    }
}

Write-Host '========================================'
Write-Host 'Quality gate checks'
Write-Host '========================================'

Use-CompatibleJdk

$Backend = Join-Path $Root 'backend'
if (Test-Path (Join-Path $Backend 'pom.xml')) {
    Invoke-Step 'Backend tests' $Backend 'mvn test'
} else {
    Write-Host '[SKIP] backend/pom.xml not found'
}

$Frontend = Join-Path $Root 'frontend'
if (Test-Path (Join-Path $Frontend 'package.json')) {
    Invoke-Step 'Frontend type check' $Frontend 'pnpm type-check'
    Invoke-Step 'Frontend lint' $Frontend 'pnpm lint'
    Invoke-Step 'Frontend coverage' $Frontend 'pnpm coverage'
    Invoke-Step 'Frontend build' $Frontend 'pnpm build'
} else {
    Write-Host '[SKIP] frontend/package.json not found'
}

Write-Host ''
Write-Host '========================================'
if ($Pass) {
    Write-Host 'Quality gate passed'
    Write-Host '========================================'
    exit 0
}

Write-Host 'Quality gate failed'
Write-Host '========================================'
exit 1
