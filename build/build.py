"""Скрипт для сборки проекта"""

from loguru import logger as log
from pathlib import Path
import os
import sys
import shutil
import subprocess


SRC_DIR = Path("src")
LIB_DIR = Path("lib")
OUT_DIR = Path("out")


def find_java_files(src_dir):
    return [str(p) for p in src_dir.rglob("*.java")]


def find_jars(lib_dir):
    return [str(jar) for jar in lib_dir.rglob("*.jar")]


def clean_out_dir(out_dir):
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

