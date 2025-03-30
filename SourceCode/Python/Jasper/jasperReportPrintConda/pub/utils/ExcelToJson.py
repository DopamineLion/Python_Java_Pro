# 导入pandas库
# 导入json库的两个工具loads将字符器转成json对象，dumps工具用于格式化
from json import loads, dumps

import pandas as pd

from pub.utils.FileUtils import mkDir2


def excelToJson(outputJsonFilePath, dataFrame):
    # 将excel数据转成json字符串
    json_str = dataFrame.to_json(orient='records')
    # json_str = xls.to_json(orient='index')
    # 将json字符器转成json对象
    parsed = loads(json_str)
    # 格式化json对象，所谓格式化就是有缩进换行等格式化的东西
    format_data = dumps(parsed, indent=4)
    # 将格式化后的数据写入book.json文件
    with open(outputJsonFilePath, 'w') as f:
        f.write(format_data)


