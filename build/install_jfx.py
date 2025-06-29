"""Скрипт для установки JavaFX SDK на любую платформу любой архитектуры (если они поддерживаются JavaFX)"""

from pathlib import Path
from loguru import logger as log
# TODO: logs
# TODO: docs

import os
import platform
import sys
import urllib.request
import zipfile

from dotenv import find_dotenv, dotenv_values
config = dotenv_values(find_dotenv())

JFX_VERSION = config.get("JFX_VERSION")
JFX_MODULES = config.get("JFX_MODULES")
LIB_DIR = Path(config.get("LIB_DIR"))
TMP_DIR = Path(config.get("TMP_DIR"))


def get_platform_info():
    """Определяет ОС и архитектуру."""
    system = platform.system().lower()
    arch = platform.machine().lower()

    if system == "windows":
        os_name = "windows"
    elif system == "linux":
        os_name = "linux"
    elif system == "darwin":
        os_name = "mac"
    else:
        raise RuntimeError(f"Unsupported OS: {system}")

    if arch in ("x86_64", "amd64"):
        arch = "x64"
    elif arch in ("aarch64", "arm64"):
        arch = "aarch64"
    else:
        raise RuntimeError(f"Unsupported architecture: {arch}")

    return os_name, arch


def download_and_extract_javafx(os_name, arch):
    """Скачивает и распаковывает JavaFX SDK."""
    url = f"https://download2.gluonhq.com/openjfx/{JFX_VERSION}/openjfx-{JFX_VERSION}_{os_name}-{arch}_bin-sdk.zip"
    zip_path = TMP_DIR / Path("javafx-sdk.zip")
    sdk_dir = LIB_DIR

    try:
        # TODO: split 
        urllib.request.urlretrieve(url, zip_path)

        with zipfile.ZipFile(zip_path, "r") as zip_ref:
            zip_ref.extractall(sdk_dir)
        os.remove(zip_path)

    except Exception as e:
        print(f"❌ Error: {e}")
        sys.exit(1)



def install_javafx():
    os_name, arch = get_platform_info()
    download_and_extract_javafx(os_name, arch)

    # java --module-path {module_path} --add-modules {JFX_MODULES} -cp \"{classpath}\" YourMainClass


def main():
    """
    Точка входа в скрипт. Запускает сборку и обрабатывает возможные ошибки.
    """

    # TODO: norm exceptions 
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

