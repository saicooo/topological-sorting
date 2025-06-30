@echo off
chcp 65001

@REM Скрипт для сборки проекта на Windows

echo Установка зависимостей из requirements.txt...
pip install -r requirements.txt
set /a EXITCODE = %ERRORLEVEL%

if %EXITCODE% neq 0 (
    echo Ошибка при установке зависимостей...
    pause
    exit %EXITCODE%
)

echo Установка JavaFX...
python -u .\build\install_jfx.py
set /a EXITCODE = %ERRORLEVEL%

if %EXITCODE% neq 0 (
    echo Ошибка при установке JavaFX...
    pause
    exit %EXITCODE%
)

echo Запуск build.py для сборки проекта...
python -u .\build\build.py
set /a EXITCODE = %ERRORLEVEL%

if %EXITCODE% neq 0 (
    echo Ошибка при сборке проекта...
    pause
    exit %EXITCODE%
)

echo Сборка проекта успешно завершена...
pause
exit 0

