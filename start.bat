@echo off
chcp 65001 >nul

REM Скрипт для запуска проекта на Windows

if "%~1"=="" (
    echo Использование: %~nx0 MainClass
    exit /b 1
)

set MAIN_CLASS=%1

python -u .\build\start.py %MAIN_CLASS%
set /a EXITCODE=%ERRORLEVEL%

if %EXITCODE% neq 0 (
    echo Ошибка при запуске проекта...
    exit /b %EXITCODE%
)

