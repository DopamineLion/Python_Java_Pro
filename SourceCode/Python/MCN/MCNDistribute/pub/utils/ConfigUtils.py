from configparser import ConfigParser


def getConfigValue(path, group, key):
    config = ConfigParser()
    config.read(path, "UTF-8")
    return config.get(group, key)


def getDefaultConfigValue(path, group, key, default):
    config = ConfigParser()
    config.read(path, "UTF-8")
    return config.get(group, key, fallback=default)
