APP_PATTERNS = {
    'VS Code': ['Visual Studio Code', 'VSCode', 'code.exe'],
    'IDEA': ['IntelliJ IDEA', 'idea64.exe'],
    'Chrome': ['Google Chrome', 'chrome.exe'],
    'Edge': ['Microsoft Edge', 'msedge.exe'],
    'Firefox': ['Firefox', 'firefox.exe'],
    '原神': ['原神', 'Genshin Impact', 'YuanShen.exe'],
    'Steam': ['Steam', 'steam.exe'],
    'WeChat': ['微信', 'WeChat', 'WeChat.exe'],
    'QQ': ['QQ', 'TIM.exe'],
    'Discord': ['Discord'],
    'Spotify': ['Spotify'],
    'Notion': ['Notion'],
    'Obsidian': ['Obsidian'],
    'Terminal': ['Windows Terminal', 'cmd.exe', 'PowerShell'],
    'Explorer': ['文件资源管理器', 'explorer.exe'],
}


def identify_app(window_title):
    window_title_lower = window_title.lower() if window_title else ''
    
    for app_name, patterns in APP_PATTERNS.items():
        for pattern in patterns:
            if pattern.lower() in window_title_lower:
                return {
                    'app_id': app_name.lower().replace(' ', '_'),
                    'app_name': app_name,
                    'matched_pattern': pattern
                }
    
    return {
        'app_id': 'unknown',
        'app_name': 'Unknown',
        'matched_pattern': None
    }


def add_app_pattern(app_name, patterns):
    if app_name not in APP_PATTERNS:
        APP_PATTERNS[app_name] = patterns
    else:
        APP_PATTERNS[app_name].extend(patterns)


def get_all_patterns():
    return APP_PATTERNS.copy()
