import ctypes
from os_platform.base import PlatformMonitor


class WindowsMonitor(PlatformMonitor):
    def __init__(self):
        self.user32 = ctypes.windll.user32

    def get_foreground_window_title(self) -> str:
        hwnd = self.user32.GetForegroundWindow()
        length = self.user32.GetWindowTextLengthW(hwnd)
        buff = ctypes.create_unicode_buffer(length + 1)
        self.user32.GetWindowTextW(hwnd, buff, length + 1)
        return buff.value

    def get_foreground_process_name(self) -> str:
        hwnd = self.user32.GetForegroundWindow()
        pid = ctypes.c_ulong()
        self.user32.GetWindowThreadProcessId(hwnd, ctypes.byref(pid))
        try:
            kernel32 = ctypes.windll.kernel32
            handle = kernel32.OpenProcess(0x0400 | 0x0010, False, pid.value)
            filename = ctypes.create_unicode_buffer(1024)
            psapi = ctypes.windll.psapi
            psapi.GetModuleBaseNameW(handle, None, filename, 1024)
            kernel32.CloseHandle(handle)
            return filename.value
        except Exception:
            return ''