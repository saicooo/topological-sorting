"""Скрипт для сборки проекта

Этот скрипт компилирует Java-файлы из папки src, учитывает зависимости из lib (jar-файлы),
и сохраняет результат в папку out. При очистке out файл README.md сохраняется.
"""

from loguru import logger as log
from pathlib import Path

import os
import sys
import shutil
import subprocess

from dotenv import find_dotenv, dotenv_values
config = dotenv_values(find_dotenv())

SRC_DIR = Path(config.get("SRC_DIR"))
LIB_DIR = Path(config.get("LIB_DIR"))
OUT_DIR = Path(config.get("OUT_DIR"))


def find_java_files(src_dir: Path) -> list[str]:
    """
    Рекурсивно ищет все Java-файлы (*.java) в указанной директории и её поддиректориях.

    :param src_dir: Путь к директории с исходными Java-файлами.
    :return: Список строковых путей к найденным Java-файлам.
    """
    return [str(p) for p in src_dir.rglob("*.java")]


def find_jars(lib_dir: Path) -> list[str]:
    """
    Рекурсивно ищет все jar-файлы (*.jar) в указанной директории и её поддиректориях.

    :param lib_dir: Путь к директории с библиотеками.
    :return: Список строковых путей к найденным jar-файлам.
    """
    return [str(jar) for jar in lib_dir.rglob("*.jar")]


def clean_out_dir(out_dir: Path) -> None:
    """
    Очищает директорию out_dir, при этом сохраняет файл README.md, если он был.
    После очистки README.md возвращается на место.

    :param out_dir: Путь к директории для очистки.
    """
    readme_path = out_dir / "README.md"
    readme_content = None

    if readme_path.exists():
        with open(readme_path, "rb") as f:
            readme_content = f.read()

    if out_dir.exists():
        shutil.rmtree(out_dir)
    out_dir.mkdir(parents=True, exist_ok=True)
    
    if readme_content is not None:
        with open(readme_path, "wb") as f:
            f.write(readme_content)


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
    
    jars = find_jars(LIB_DIR)
    clean_out_dir(OUT_DIR)

    cp_sep = os.pathsep
    classpath = str(OUT_DIR)

    if jars:
        classpath += f"{cp_sep}{cp_sep.join(jars)}"
    else:
        log.debug(f"В директория {LIB_DIR} не найдено jar-архивов. Будут скомпилированы только Java файлы без зависимостей.")

    javac_cmd = [
        "javac", "-d", str(OUT_DIR),
    ]

    if classpath:
        javac_cmd += ["-classpath", classpath]

    javac_cmd += java_files

    log.info("Начало компиляции...")
    log.debug(" ".join(javac_cmd))

    result = subprocess.run(javac_cmd)

    if result.returncode != 0:
        raise RuntimeError("Ошибка компиляции Java файлов.")


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
    else:
        log.success("Сборка завершена успешно.")
        log.debug(f"Скомпилированные файлы находятся в директории: {OUT_DIR}")


if __name__ == '__main__':
    main()

