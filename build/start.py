"""
Модуль для запуска Java-приложения из собранных классов.

Этот скрипт формирует classpath, собирает команду для запуска указанного Java-класса с методом main
и выполняет её, обрабатывая возможные ошибки. Используется как универсальный стартер для проекта.
"""

from utils import get_classpath
from loguru import logger as log
from pathlib import Path

import subprocess
import sys

from dotenv import find_dotenv, dotenv_values
config = dotenv_values(find_dotenv())

OUT_DIR = Path(config.get("OUT_DIR"))
LIB_DIR = Path(config.get("LIB_DIR"))
JFX_VERSION = config.get("JFX_VERSION", "21.0.2")
JFX_MODULES = config.get("JFX_MODULES", "javafx.controls,javafx.fxml")


def start(main_class: str | None = None):
    """
    Запускает Java-приложение, выполняя указанный класс с методом main.

    :param main_class: Имя главного Java-класса (с пакетом, если требуется), который содержит метод main.
    :raises RuntimeError: если выполнение Java-приложения завершилось с ошибкой.
    """
    if main_class is None:
        main_class = "Main"
    
    log.info(f"Запуск приложения с главным классом: {main_class}")
    
    classpath = get_classpath(OUT_DIR, LIB_DIR)
    command = f"java --module-path {LIB_DIR / f'javafx-sdk-{JFX_VERSION}' / 'lib'} --add-modules javafx.controls,javafx.fxml -cp {classpath} {main_class}"
    
    log.debug(f"Команда для запуска: {command}")
    result = subprocess.run(command, shell=True, text=True, capture_output=True)
    
    if result.returncode != 0:
        raise RuntimeError(f"Ошибка при запуске приложения: {result.stdout}\n{result.stderr}")

    log.success("Запуск завершен успешно.")
    log.debug(f"Вывод приложения: {result.stdout}")


def main():
    """
    Точка входа в скрипт. Получает имя главного класса из аргументов командной строки (если передано),
    запускает приложение и обрабатывает возможные ошибки.
    """
    main_class = sys.argv[1] if len(sys.argv) > 1 else "Main"
    try:
        start(main_class)
    except RuntimeError as e:
        log.error(f"{e}")
        sys.exit(1)
    except Exception as e:
        log.critical(f"Неожиданная ошибка: {e}")
        sys.exit(1)
    except KeyboardInterrupt:
        log.warning(f"Скрипт прерван пользователем.")
        sys.exit(1)


if __name__ == "__main__":
    main()

