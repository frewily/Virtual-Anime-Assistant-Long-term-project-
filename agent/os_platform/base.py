from abc import ABC, abstractmethod


class PlatformMonitor(ABC):

    @abstractmethod
    def get_foreground_window_title(self) -> str:
        pass

    @abstractmethod
    def get_foreground_process_name(self) -> str:
        pass