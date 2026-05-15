APP_PATTERNS = {
    'VS Code': ['Visual Studio Code', 'VSCode', 'code.exe', 'Code'],
    'Trae': ['Trae CN', 'Trae', 'trae'],
    'IDEA': ['IntelliJ IDEA', 'idea64.exe', 'IntelliJ IDEA'],
    'Chrome': ['Google Chrome', 'chrome.exe', 'Google Chrome'],
    'Edge': ['Microsoft Edge', 'msedge.exe', 'Microsoft Edge'],
    'Firefox': ['Firefox', 'firefox.exe'],
    '原神': ['原神', 'Genshin Impact', 'YuanShen.exe'],
    'Steam': ['Steam', 'steam.exe'],
    'WeChat': ['微信', 'WeChat', 'WeChat.exe'],
    'QQ': ['QQ', 'TIM.exe'],
    'Discord': ['Discord'],
    'Spotify': ['Spotify'],
    'Notion': ['Notion'],
    'Obsidian': ['Obsidian'],
    'Terminal': ['Terminal', 'Windows Terminal', 'cmd.exe', 'PowerShell', 'iTerm', 'iTerm2', 'Warp'],
    'Explorer': ['文件资源管理器', 'explorer.exe', 'Finder'],
    'Xcode': ['Xcode', 'Xcode.exe'],
    'PyCharm': ['PyCharm', 'pycharm'],
    'Slack': ['Slack', 'slack.exe'],
    'Postman': ['Postman'],
    'Figma': ['Figma'],
}


def identify_app(window_title, process_name=''):
    window_title_lower = window_title.lower() if window_title else ''
    process_name_lower = process_name.lower() if process_name else ''

    search_texts = [window_title_lower]
    if process_name_lower:
        search_texts.append(process_name_lower)

    for app_name, patterns in APP_PATTERNS.items():
        for pattern in patterns:
            pattern_lower = pattern.lower()
            for text in search_texts:
                if pattern_lower in text:
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
