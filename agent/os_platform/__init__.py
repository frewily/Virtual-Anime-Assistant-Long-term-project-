import sys
from os_platform.base import PlatformMonitor
from os_platform.windows import WindowsMonitor
from os_platform.macos import MacOSMonitor


def create_monitor():
    if sys.platform == 'win32':
        return WindowsMonitor()
    elif sys.platform == 'darwin':
        return MacOSMonitor()
    else:
        raise RuntimeError(f"Unsupported platform: {sys.platform}")