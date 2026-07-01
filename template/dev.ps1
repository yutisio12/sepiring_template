param(
    [ValidateSet("dev", "build-only")]
    [string]$Mode = "dev"
)

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $root

switch ($Mode) {
    "dev" {
        Write-Host "`n[dev.ps1] Starting continuous build (background) + bootRun (foreground)..." -ForegroundColor Cyan
        $job = Start-Job -Name "gradle-build" -ScriptBlock {
            param($dir)
            Set-Location $dir
            $p = Start-Process -FilePath ".\gradlew" -ArgumentList "build --continuous" -NoNewWindow -PassThru -RedirectStandardOutput "$dir\build\gradle-continuous.log" -RedirectStandardError "$dir\build\gradle-continuous.err"
            $p.WaitForExit()
        } -ArgumentList $root

        try {
            .\gradlew bootRun
        } finally {
            Write-Host "`n[dev.ps1] Stopping continuous build..." -ForegroundColor Yellow
            Stop-Job $job -ErrorAction SilentlyContinue
            Remove-Job $job -ErrorAction SilentlyContinue
        }
    }
    "build-only" {
        Write-Host "`n[dev.ps1] Running build --continuous..." -ForegroundColor Cyan
        .\gradlew build --continuous
    }
}
