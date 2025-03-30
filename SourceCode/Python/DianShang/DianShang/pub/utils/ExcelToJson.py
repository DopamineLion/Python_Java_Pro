# 导入pandas库
import os
import pandas as pd
# 导入json库的两个工具loads将字符器转成json对象，dumps工具用于格式化
from json import loads, dumps

def excelToJson(outputJsonFilePath, renameDataFrame):
    # 将excel数据转成json字符串
    json_str = renameDataFrame.to_json(orient='records')
    # json_str = xls.to_json(orient='index')
    # 将json字符器转成json对象
    parsed = loads(json_str)
    # 格式化json对象，所谓格式化就是有缩进换行等格式化的东西
    format_data = dumps(parsed, indent=4)
    # 将格式化后的数据写入book.json文件
    with open(outputJsonFilePath, 'w') as f:
        f.write(format_data)



# def excelToJsonJi(fileName, sheetName):
#     # 读取excel的数据
#     xls = pd.read_excel(fileName, sheet_name=sheetName, dtype={'库位': str}, engine='openpyxl')
#     xlsSelectivity = xls[~((xls['库位'].str.contains('LZ28')) & (xls['单位'] == 'CS'))]
#     reserve_columns = ['目标跟踪号', '波次号', '门店号', 'WMS单号', '项目号', '名称', '规格', '库位', '最小数量', '批号']
#     dataFrame = xlsSelectivity.loc[:, reserve_columns]
#     sortDataFrame = dataFrame.sort_values(by='库位', inplace=False, ascending=True)
#     renameDataFrame = sortDataFrame.rename(columns={'目标跟踪号': 'COLUMNNAME11',
#                                                 '波次号': 'COLUMNNAME45',
#                                                 '门店号': 'COLUMNNAME34',
#                                                 'WMS单号': 'COLUMNNAME1',
#                                                 '项目号': 'COLUMNNAME7',
#                                                 '名称': 'COLUMNNAME36',
#                                                 '规格': 'COLUMNNAME22',
#                                                 '库位': 'COLUMNNAME10',
#                                                 '最小数量': 'COLUMNNAME15',
#                                                 '批号': 'COLUMNNAME20'
#                                                 }, inplace=False)
#     renameDataFrame['COLUMNNAME33'] = renameDataFrame['COLUMNNAME34']
#     with pd.ExcelWriter(fileName, mode='a') as writer:
#         renameDataFrame.to_excel(writer, sheet_name='打80X60签数据源', index=False)
#
#     # 将excel数据转成json字符串
#     json_str = renameDataFrame.to_json(orient='records')
#     # json_str = xls.to_json(orient='index')
#     # 将json字符器转成json对象
#     parsed = loads(json_str)
#     # 格式化json对象，所谓格式化就是有缩进换行等格式化的东西
#     format_data = dumps(parsed, indent=4)
#     # 目录路径
#     dirs = 'D:\\stocktaking\\JianHuoQian.json'
#     # 检查目录是否存在
#     # if not os.path.exists(dirs):
#     #     # 如果目录不存在，则创建它
#     #     os.makedirs(dirs)
#     # 将格式化后的数据写入book.json文件
#     with open(dirs, 'w') as f:
#         f.write(format_data)
#
#
# def excelToJsonBox(fileName, sheetName):
#     # 读取excel的数据
#     xls = pd.read_excel(fileName, sheet_name=sheetName, dtype={'库位': str}, engine='openpyxl')
#     xls = xls[xls['QTY_EACH_TEXT'] == xls['数量']]
#     reserve_columns = ['LOCATION', '订单号', '客户号', 'SKU', '收货人名字', 'SKUDESCR_C', '规格', 'UOM_NAME', 'QTY', 'LOTATT04', 'LOTATT01', '托盘号', '标签号', '打印时间']
#     dataFrame = xls.loc[:, reserve_columns]
#     sortDataFrame = dataFrame.sort_values(by=['SKU', 'LOCATION'], inplace=False, ascending=True)
#     renameDataFrame = sortDataFrame.rename(columns={'订单号': 'FIELDVALUE61',
#                                                 '客户号': 'FIELDVALUE50',
#                                                 'SKU': 'FIELDVALUE6',
#                                                 '收货人名字': 'FIELDVALUE62',
#                                                 'SKUDESCR_C': 'FIELDVALUE35',
#                                                 '规格': 'FIELDVALUE21',
#                                                 'UOM_NAME': 'FIELDVALUE11',
#                                                 'QTY': 'FIELDVALUE13',
#                                                 'LOTATT04': 'FIELDVALUE19',
#                                                 'LOTATT01': 'FIELDVALUE16',
#                                                 'LOCATION': 'FIELDVALUE58',
#                                                 '标签号': 'FIELDVALUE59',
#                                                 '打印时间': 'FIELDVALUE56'
#                                                 }, inplace=False)
#     with pd.ExcelWriter(fileName, mode='a') as writer:
#         renameDataFrame.to_excel(writer, sheet_name='打100X100签数据源', index=False)
#
#     # 将excel数据转成json字符串
#     json_str = renameDataFrame.to_json(orient='records')
#     # json_str = xls.to_json(orient='index')
#     # 将json字符器转成json对象
#     parsed = loads(json_str)
#     # 格式化json对象，所谓格式化就是有缩进换行等格式化的东西
#     format_data = dumps(parsed, indent=4)
#     # 目录路径
#     dirs = 'D:\\stocktaking\\WaiXiangQian.json'
#     # 检查目录是否存在
#     # if not os.path.exists(dirs):
#     #     # 如果目录不存在，则创建它
#     #     os.makedirs(dirs)
#     # 将格式化后的数据写入book.json文件
#     with open(dirs, 'w') as f:
#         f.write(format_data)

