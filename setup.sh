#!/bin/bash

# Скрипт для сборки проекта на Linux/macOS

echo "Установка зависимостей из requirements.txt..."
declare -i EXITCODE

pip3 install -r requirements.txt
EXITCODE=$?

if [ $EXITCODE -ne 0 ]; then
    echo "Ошибка при установке зависимостей..."
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

