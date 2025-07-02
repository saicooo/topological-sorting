@echo off
chcp 65001 >nul

REM Скрипт для запуска проекта на Windows

if "%~1"=="" (
    echo Использование: %~nx0 MainClass
    pause
    exit 1
)

echo Проверка наличия Python...
where python >nul 2>nul
set /a EXITCODE = %ERRORLEVEL%

if %EXITCODE% neq 0 (
    echo Python не найден. Пожалуйста, установите Python и добавьте его в PATH.
    pause
    exit %EXITCODE%
)

echo Проверка наличия pip...
where pip >nul 2>nul
set /a EXITCODE = %ERRORLEVEL%

if %EXITCODE% neq 0 (
    echo pip не найден. Пожалуйста, установите pip и добавьте его в PATH.
    pause
    exit %EXITCODE%
)

echo Проверка наличия виртуального окружения...
if not exist ".venv" (
    echo Виртуальное окружение не найдено. Пожалуйста, выполните ./setup.bat для его создания.
    pause
    exit 1
)

echo Активация виртуального окружения...
call .\.venv\Scripts\activate.bat
set /a EXITCODE=%ERRORLEVEL%

if %EXITCODE% neq 0 (
    echo Ошибка при активации виртуального окружения.
    pause
)

echo Установка зависимостей из requirements.txt...
pip install -r requirements.txt
set /a EXITCODE = %ERRORLEVEL%

if %EXITCODE% neq 0 (
    echo Ошибка при установке зависимостей.
    pause
    exit %EXITCODE%
)

set MAIN_CLASS=%1

python -u .\build\start.py %MAIN_CLASS%
set /a EXITCODE=%ERRORLEVEL%

if %EXITCODE% neq 0 (
    echo Ошибка при запуске проекта...
    exit %EXITCODE%
)

echo Деактивация виртуального окружения...
call .\.venv\Scripts\deactivate.bat

pause
exit 0
