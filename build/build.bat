@echo off
chcp 65001 >nul

@REM Скрипт для сборки проекта на Windows

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

echo Проверка наличия venv...
where python -m venv >nul 2>nul
set /a EXITCODE = %ERRORLEVEL%

if %EXITCODE% neq 0 (
    echo Модуль venv не установлен. Установка venv...
    python -m pip install virtualenv
    set /a EXITCODE = %ERRORLEVEL%

    if %EXITCODE% neq 0 (
        echo Ошибка при установке venv. Пожалуйста, установите модуль venv вручную.
        pause
        exit %EXITCODE%
    )
)

echo Создание виртуального окружения...
python -m venv .venv
call .venv\Scripts\activate.bat

if %ERRORLEVEL% neq 0 (
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

echo Установка JavaFX...
python -u .\build\install_jfx.py
set /a EXITCODE = %ERRORLEVEL%

if %EXITCODE% neq 0 (
    echo Ошибка при установке JavaFX.
    pause
    exit %EXITCODE%
)

echo Запуск build.py для сборки проекта...
python -u .\build\build.py
set /a EXITCODE = %ERRORLEVEL%

if %EXITCODE% neq 0 (
    echo Ошибка при сборке проекта.
    pause
    exit %EXITCODE%
)

echo Сборка проекта успешно завершена...

echo Деактивация виртуального окружения...
call .\.venv\Scripts\deactivate.bat
rmdir /s /q .venv

pause
exit 0

