"""Скрипт для установки JavaFX SDK на любую платформу любой архитектуры (если они поддерживаются JavaFX)"""

from pathlib import Path
from loguru import logger as log
from urllib.error import URLError
from zipfile import ZipFile, BadZipFile
from utils import get_platform_info

import os
import sys
import urllib.request

from dotenv import find_dotenv, dotenv_values
config = dotenv_values(find_dotenv())

JFX_VERSION = config.get("JFX_VERSION")
LIB_DIR = Path(config.get("LIB_DIR"))
TMP_DIR = Path(config.get("TMP_DIR"))


def download_javafx(os_name: str, arch: str) -> Path:
    """
    Скачивает и распаковывает JavaFX SDK для заданной платформы и архитектуры.

    :param os_name: Имя операционной системы ("windows", "linux", "mac").
    :param arch: Архитектура ("x64" или "aarch64").
    :return: Путь к скачанному zip-файлу с JavaFX SDK.
    :raises RuntimeError: при ошибках скачивания или распаковки архива.
    """
    url = f"https://download2.gluonhq.com/openjfx/{JFX_VERSION}/openjfx-{JFX_VERSION}_{os_name}-{arch}_bin-sdk.zip"
    log.debug(f"Скачивание JavaFX SDK с URL: {url}")

    zip_path = TMP_DIR / Path(f"javafx-sdk-{JFX_VERSION}-{os_name}-{arch}.zip")
    if not TMP_DIR.exists():
        log.debug(f"Создание временной директории: {TMP_DIR}")
        TMP_DIR.mkdir(parents=True, exist_ok=True)

    log.debug(f"Временный файл для скачивания: {zip_path}")

    if not LIB_DIR.exists():
        log.debug(f"Создание директории для библиотек: {LIB_DIR}")
        LIB_DIR.mkdir(parents=True, exist_ok=True)

    try:
        urllib.request.urlretrieve(url, zip_path)
    except URLError as e:
        raise RuntimeError(f"Ошибка при скачивании JavaFX SDK: {e}") from e
    else:
        log.debug(f"JavaFX SDK успешно скачан: {zip_path}")
    
    return zip_path


def extract_javafx(zip_path: Path) -> None:
    """
    Извлекает JavaFX SDK из zip-архива в директорию LIB_DIR.
    :param zip_path: Путь к zip-файлу с JavaFX SDK.
    :raises RuntimeError: при ошибках распаковки архива.
    """
    sdk_dir = LIB_DIR
    log.debug(f"Директория для установки JavaFX: {sdk_dir}")

    log.debug(f"Распаковка JavaFX SDK из {zip_path} в {sdk_dir}")
    try:
        with ZipFile(zip_path, "r") as zip_ref:
            zip_ref.extractall(sdk_dir)
    except FileNotFoundError as e:
        raise RuntimeError(f"Файл {zip_path} не найден при распаковке JavaFX SDK: {e}") from e
    except BadZipFile as e:
        raise RuntimeError(f"Ошибка при распаковке JavaFX SDK: {e}") from e
    else:
        log.debug(f"JavaFX SDK успешно распакован в {sdk_dir}")
        os.remove(zip_path)
    

def install_javafx():
    """
    Основная функция установки JavaFX:
    - Определяет платформу и архитектуру.
    - Скачивает и распаковывает JavaFX SDK в папку LIB_DIR.
    - Логирует процесс установки.
    :raises RuntimeError: при ошибках установки.
    """
    log.info("Установка JavaFX...")

    if (LIB_DIR / f"javafx-sdk-{JFX_VERSION}" / "lib" / "javafx.base.jar").exists():
        log.info(f"JavaFX SDK уже установлен в {LIB_DIR}. Пропуск установки.")
        return
    
    os_name, arch = get_platform_info()
    log.debug(f"Определены ОС: {os_name}, архитектура: {arch}")

    zip_path = download_javafx(os_name, arch)
    extract_javafx(zip_path)
    log.info("Установка JavaFX завершена.")


def main():
    """
    Точка входа в скрипт. Запускает установку JavaFX и обрабатывает возможные ошибки.
    """
    try:
        install_javafx()
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
        log.success("Установка завершена успешно.")
        log.debug(f"Установленная jfx-{JFX_VERSION} находится в директории {LIB_DIR}")


if __name__ == "__main__":
    main()

