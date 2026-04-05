import ctypes
import time
import threading
from app_identifier import identify_app
from reporter import Reporter

user32 = ctypes.windll.user32

class WindowMonitor:
    def __init__(self, report_interval=5):
        self.report_interval = report_interval
        self.reporter = Reporter()
        self.running = False
        self.last_title = None
        
    def get_foreground_window_title(self):
        hwnd = user32.GetForegroundWindow()
        length = user32.GetWindowTextLengthW(hwnd)
        buff = ctypes.create_unicode_buffer(length + 1)
        user32.GetWindowTextW(hwnd, buff, length + 1)
        return buff.value
    
    def start(self):
        self.running = True
        thread = threading.Thread(target=self._monitor_loop)
        thread.daemon = True
        thread.start()
        
    def stop(self):
        self.running = False
        
    def _monitor_loop(self):
        while self.running:
            try:
                title = self.get_foreground_window_title()
                
                if title and title != self.last_title:
                    app_info = identify_app(title)
                    
                    self.reporter.report_window({
                        'appId': app_info['app_id'],
                        'windowTitle': title,
                        'appName': app_info['app_name']
                    })
                    
                    print(f"[Window Changed] {app_info['app_name']}: {title}")
                    self.last_title = title
                    
            except Exception as e:
                print(f"Error monitoring window: {e}")
                
            time.sleep(self.report_interval)


def main():
    monitor = WindowMonitor(report_interval=5)
    monitor.start()
    
    print("Window monitor started. Press Ctrl+C to stop.")
    
    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        monitor.stop()
        print("\nWindow monitor stopped.")


if __name__ == '__main__':
    main()
