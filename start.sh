#!/bin/bash

# Скрипт для запуска проекта на Linux/macOS
declare -i EXITCODE

if [ $# -lt 1 ]; then
    echo "Использование: $0 <MainClass>"
    exit 1
fi

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

echo "Проверка наличия виртуального окружения..."
if [ ! -d ".venv" ]; then
    echo "Виртуальное окружение не найдено. Пожалуйста, выполните ./setup.sh для его создания."
    exit 1
fi

echo "Активация виртуального окружения..."
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

declare MAIN_CLASS="$1"

python3 -u ./build/start.py "$MAIN_CLASS"
EXITCODE=$?

if [ $EXITCODE -ne 0 ]; then
    echo "Ошибка при запуске проекта..."
    exit $EXITCODE
fi

echo "Деактивация виртуального окружения..."
deactivate

