import logging
from logging import getLogger, StreamHandler


log = getLogger("test")
handler = StreamHandler()
formatter = logging.Formatter("%(levelname)s:     %(message)s")
handler.setFormatter(formatter)
log.addHandler(handler)
log.setLevel(3)
