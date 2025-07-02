"""Скрипт для сборки проекта

Этот скрипт компилирует Java-файлы из папки src, учитывает зависимости из lib (jar-файлы),
и сохраняет результат в папку out. При очистке out файл README.md сохраняется.
"""

from loguru import logger as log
from pathlib import Path
from utils import find_java_files, clean_out_dir, get_classpath

import sys
import subprocess

from dotenv import find_dotenv, dotenv_values
config = dotenv_values(find_dotenv())

SRC_DIR = Path(config.get("SRC_DIR"))
LIB_DIR = Path(config.get("LIB_DIR"))
OUT_DIR = Path(config.get("OUT_DIR"))
JFX_VERSION = config.get("JFX_VERSION", "21.0.2")
JFX_MODULES = config.get("JFX_MODULES", "javafx.controls,javafx.fxml")


def build():
    """
    Выполняет сборку Java-проекта:
    - Проверяет наличие директории с исходниками.
    - Находит все java-файлы для компиляции.
    - Проверяет наличие директории с библиотеками и ищет jar-файлы.
    - Очищает директорию out, сохраняя README.md.
    - Формирует classpath из out и найденных jar-файлов.
    - Собирает команду для компиляции с помощью javac.
    - Запускает компиляцию и обрабатывает возможные ошибки.

    Исключения:
        RuntimeError: если не найдена директория исходников или произошла ошибка компиляции.
    """
    log.info("Начало сборки...")

    if not SRC_DIR.exists():
        raise RuntimeError(f"Исходная директория {SRC_DIR} не найдена.")

    java_files = find_java_files(SRC_DIR)

    if not java_files:
        log.warning("Нет файлов для компиляции.")
        return
    
    if not LIB_DIR.exists():
        log.warning(f"Директория {LIB_DIR} не найдена. Будут скомпилированы только Java файлы без зависимостей.")
    
    clean_out_dir(OUT_DIR)

    classpath = get_classpath(OUT_DIR, LIB_DIR)

    if classpath == str(OUT_DIR):
        log.debug(f"В директории {LIB_DIR} не найдено jar-архивов. Будут скомпилированы только Java файлы без зависимостей.")

    javac_cmd = [
        "javac", 
        "--module-path", str(LIB_DIR / f"javafx-sdk-{JFX_VERSION}" / "lib"),
        "--add-modules", JFX_MODULES,
        "-d", str(OUT_DIR), 
        "-classpath", classpath
    ] + java_files
    
    log.info("Начало компиляции...")
    log.debug(" ".join(javac_cmd))

    result = subprocess.run(javac_cmd, text=True, capture_output=True)

    if result.returncode != 0:
        raise RuntimeError(f"Ошибка компиляции Java файлов: {result.stdout}\n{result.stderr}")
    
    log.success("Сборка завершена успешно.")
    log.debug(f"Скомпилированные файлы находятся в директории: {OUT_DIR}")


def main():
    """
    Точка входа в скрипт. Запускает сборку и обрабатывает возможные ошибки.
    """
    try:
        build()
    except RuntimeError as e:
        log.error(f"{e}")
        sys.exit(1)
    except Exception as e:
        log.critical(f"Неожиданная ошибка: {e}")
        sys.exit(1)
    except KeyboardInterrupt:
        log.warning(f"Скрипт прерван пользователем.")
        sys.exit(1)


if __name__ == '__main__':
    main()

