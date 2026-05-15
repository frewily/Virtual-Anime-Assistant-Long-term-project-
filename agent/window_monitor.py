import time
import threading
from app_identifier import identify_app
from reporter import Reporter
from os_platform import create_monitor


class WindowMonitor:
    def __init__(self, report_interval=5, backend_url=None):
        self.report_interval = report_interval
        self.reporter = Reporter(backend_url)
        self.monitor = create_monitor()
        self.running = False
        self.last_title = None

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
                title = self.monitor.get_foreground_window_title()

                if title and title != self.last_title:
                    process_name = self.monitor.get_foreground_process_name()
                    app_info = identify_app(title, process_name)

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
    import os
    backend_url = os.environ.get('ASSISTANT_BACKEND_URL', 'http://localhost:8080/api/report')
    monitor = WindowMonitor(report_interval=5, backend_url=backend_url)
    monitor.start()

    print(f"Window monitor started. Backend: {backend_url}")
    print("Press Ctrl+C to stop.")

    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        monitor.stop()
        print("\nWindow monitor stopped.")


if __name__ == '__main__':
    main()
