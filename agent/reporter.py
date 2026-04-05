import requests
import json
import time

BACKEND_URL = 'http://localhost:8080/api/report'


class Reporter:
    def __init__(self, backend_url=None):
        self.backend_url = backend_url or BACKEND_URL
        self.session = requests.Session()
        
    def report_window(self, data):
        url = f"{self.backend_url}/window"
        
        try:
            response = self.session.post(url, json=data, timeout=5)
            
            if response.status_code == 200:
                return True
            else:
                print(f"Report failed: {response.status_code}")
                return False
                
        except requests.exceptions.RequestException as e:
            print(f"Report error: {e}")
            return False
            
    def report_status(self, data):
        pass
        
    def health_check(self):
        try:
            response = self.session.get(
                self.backend_url.replace('/report', '/status'),
                timeout=5
            )
            return response.status_code == 200
        except:
            return False
