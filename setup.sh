#!/bin/bash

# Скрипт для сборки проекта на Linux/macOS

declare -i EXITCODE

echo "Проверка наличия Python 3..."
if ! command -v python3 &> /dev/null; then
    echo "Python 3 не установлен. Пожалуйста, установите Python 3."
    exit 1
fi

echo "Проверка наличия pip..."
if ! command -v pip3 &> /dev/null; then
    echo "pip не установлен. Пожалуйста, установите pip."
    exit 1
fi

echo "Проверка наличия venv..."
if ! python3 -m venv --help &> /dev/null; then
    echo "Модуль venv не установлен. Установка..."
    pip3 install --break-system-packages virtualenv
    EXITCODE=$?

    if [ $EXITCODE -ne 0 ]; then
        echo "Ошибка при установке venv. Пожалуйста, установите модуль venv вручную."
        exit $EXITCODE
    fi

fi

echo "Создание виртуального окружения..."
python3 -m venv .venv
source .venv/bin/activate
EXITCODE=$?

if [ $EXITCODE -ne 0 ]; then
    echo "Ошибка при активации виртуального окружения."
    read -p "Нажмите Enter для продолжения..."
fi

echo "Установка зависимостей из requirements.txt..."
pip3 install -r requirements.txt
EXITCODE=$?

if [ $EXITCODE -ne 0 ]; then
    echo "Ошибка при установке зависимостей..."
    exit $EXITCODE
fi

echo "Установка JavaFX..."
python3 -u ./build/install_jfx.py
EXITCODE=$?

if [ $EXITCODE -ne 0 ]; then
    echo "Ошибка при установке JavaFX..."
    exit $EXITCODE
fi

echo "Запуск build.py для сборки проекта..."
python3 -u ./build/build.py
EXITCODE=$?

if [ $EXITCODE -ne 0 ]; then
    echo "Ошибка при сборке проекта..."
    exit $EXITCODE
fi

echo "Сборка проекта успешно завершена..."

echo "Деактивация виртуального окружения..."
source .venv/bin/deactivate
rm -r .venv

