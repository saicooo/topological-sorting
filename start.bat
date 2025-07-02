@echo off
chcp 65001 >nul

if "%~1"=="" (
    echo Использование: %~nx0 MainClass
    exit /b 1
)

start cmd /k ".\build\start_app.bat %~1"

