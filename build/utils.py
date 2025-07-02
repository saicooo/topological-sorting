from pathlib import Path
import os
import shutil
import platform


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


def get_classpath(out_dir: Path, lib_dir: Path) -> str:
    """
    Формирует classpath для компиляции Java-файлов.

    :param out_dir: Путь к директории для скомпилированных классов.
    :param lib_dir: Путь к директории с библиотеками.
    :return: Строка с classpath.
    """
    cp_sep = os.pathsep
    classpath = str(out_dir)
    jars = find_jars(lib_dir)

    if jars:
        classpath += f"{cp_sep}{cp_sep.join(jars)}"
    
    return classpath


def get_platform_info() -> tuple[str, str]:
    """
    Определяет операционную систему и архитектуру текущей машины.

    :return: Кортеж (os_name, arch), где os_name — строка ("windows", "linux", "mac"),
             arch — строка ("x64" или "aarch64").
    :raises RuntimeError: если ОС или архитектура не поддерживаются JavaFX.
    """
    system = platform.system().lower()
    arch = platform.machine().lower()

    match system:
        case "windows":
            os_name = "windows"
        case "linux":
            os_name = "linux"
        case "darwin":
            os_name = "mac"
        case _:
            raise RuntimeError(f"Unsupported OS: {system}")

    match arch:
        case "x86_64" | "amd64":
            arch = "x64"
        case "aarch64" | "arm64":
            arch = "aarch64"
        case _:
            raise RuntimeError(f"Unsupported architecture: {arch}")

    return os_name, arch

