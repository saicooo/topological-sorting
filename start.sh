#!/bin/bash

# Скрипт для запуска проекта на Linux/macOS
declare -i EXITCODE

if [ $# -lt 1 ]; then
    echo "Использование: $0 <MainClass>"
    exit 1
fi

MAIN_CLASS="$1"

python3 -u ./build/start.py "$MAIN_CLASS"
EXITCODE=$?

if [ $EXITCODE -ne 0 ]; then
    echo "Ошибка при запуске проекта..."
    exit $EXITCODE
fi

