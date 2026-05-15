import subprocess
import re
from os_platform.base import PlatformMonitor


class MacOSMonitor(PlatformMonitor):
    def __init__(self):
        self._accessibility_available = True

    def _get_front_asn(self):
        try:
            output = subprocess.check_output(
                ['lsappinfo', 'front'],
                timeout=3
            ).decode('utf-8').strip()
            match = re.search(r'ASN:(\S+)', output)
            return match.group(1) if match else None
        except Exception:
            return None

    def _get_app_info(self, asn):
        try:
            output = subprocess.check_output(
                ['lsappinfo', 'info', '-only', 'name', 'ASN:' + asn],
                timeout=3
            ).decode('utf-8').strip()
            match = re.search(r'"LSDisplayName"="([^"]+)"', output)
            return match.group(1) if match else "Unknown"
        except Exception:
            return "Unknown"

    def _get_window_title(self):
        if not self._accessibility_available:
            return ""
        try:
            script = '''
            tell application "System Events"
                set frontApp to first application process whose frontmost is true
                try
                    set winTitle to name of front window of frontApp
                    return winTitle
                on error
                    return ""
                end try
            end tell
            '''
            output = subprocess.check_output(
                ['osascript', '-e', script],
                timeout=3,
                stderr=subprocess.DEVNULL
            ).decode('utf-8').strip()
            return output if output else ""
        except Exception:
            self._accessibility_available = False
            return ""

    def get_foreground_window_title(self) -> str:
        asn = self._get_front_asn()
        app_name = self._get_app_info(asn) if asn else "Unknown"
        win_title = self._get_window_title()
        if win_title:
            return app_name + " — " + win_title
        return app_name

    def get_foreground_process_name(self) -> str:
        asn = self._get_front_asn()
        if asn:
            return self._get_app_info(asn)
        return "Unknown"
