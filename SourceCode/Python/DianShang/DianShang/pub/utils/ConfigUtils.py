import logging
from configparser import ConfigParser

from pub.utils.FileUtils import mkDir2
from pub.utils.TimeUtils import nowTime, nowDate


def getConfigValue(path, group, key):
    config = ConfigParser()
    config.read(path, "UTF-8")
    return config.get(group, key)


def getDefaultConfigValue(path, group, key, default):
    config = ConfigParser()
    config.read(path, "UTF-8")
    return config.get(group, key, fallback=default)


def configLog(logDir):
    # 配置logging模块
    logDir = 'D:\\stocktaking\\' + logDir + '\\' + nowDate() + '\\'
    logFileName = nowTime() + 'logFile'
    mkDir2(logDir)
    logging.basicConfig(filename=logDir + '\\' + logFileName + '.txt', level=logging.DEBUG,
                        format='%(asctime)s - %(levelname)s - %(message)s')
