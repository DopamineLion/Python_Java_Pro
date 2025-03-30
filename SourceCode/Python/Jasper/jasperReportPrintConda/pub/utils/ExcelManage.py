import pandas as pd


def waiXiangJasperSourcesExcel(fileName, sheetName):
    # 读取excel的数据
    xls = pd.read_excel(fileName, sheet_name=sheetName, dtype={'LOCATION': str}, engine='openpyxl')
    reserve_columns = ['LOCATION', '波次号', '订单号', '客户号', 'SKU', '收货人名字', 'SKUDESCR_C', '规格', 'UOM_NAME', 'QTY', 'LOTATT04', 'LOTATT01', '托盘号', '标签号', '打印时间']
    dataFrame = xls.loc[:, reserve_columns]
    sortDataFrame = dataFrame.sort_values(by=['LOCATION', 'SKU', 'LOTATT04', 'QTY'], inplace=False, ascending=True)
    renameDataFrame = sortDataFrame.rename(columns={'订单号': 'FIELDVALUE61',
                                                    '客户号': 'FIELDVALUE50',
                                                    'SKU': 'FIELDVALUE6',
                                                    '收货人名字': 'FIELDVALUE62',
                                                    'SKUDESCR_C': 'FIELDVALUE35',
                                                    '规格': 'FIELDVALUE21',
                                                    'UOM_NAME': 'FIELDVALUE11',
                                                    'QTY': 'FIELDVALUE13',
                                                    'LOTATT04': 'FIELDVALUE19',
                                                    'LOTATT01': 'FIELDVALUE16',
                                                    'LOCATION': 'FIELDVALUE58',
                                                    '波次号': 'FIELDVALUE65',
                                                    '标签号': 'FIELDVALUE59',
                                                    '打印时间': 'FIELDVALUE56'
                                                    }, inplace=False)
    return renameDataFrame


def jianHuoJasperSourcesExcel(fileName, sheetName):
    # 读取excel的数据
    xls = pd.read_excel(fileName, sheet_name=sheetName, dtype={'库位': str}, engine='openpyxl')
    xlsSelectivity = xls[~((xls['库位'].str.contains('^LZ28.*$|28')) & (xls['单位'] == 'CS'))]
    reserve_columns = ['目标跟踪号', '波次号', '门店号', 'WMS单号', '项目号', '名称', '规格', '库位', '最小数量', '批号']
    dataFrame = xlsSelectivity.loc[:, reserve_columns]
    sortDataFrame = dataFrame.sort_values(by='库位', inplace=False, ascending=True)
    renameDataFrame = sortDataFrame.rename(columns={'目标跟踪号': 'COLUMNNAME11',
                                                    '波次号': 'COLUMNNAME45',
                                                    '门店号': 'COLUMNNAME33',
                                                    'WMS单号': 'COLUMNNAME1',
                                                    '项目号': 'COLUMNNAME7',
                                                    '名称': 'COLUMNNAME36',
                                                    '规格': 'COLUMNNAME22',
                                                    '库位': 'COLUMNNAME10',
                                                    '最小数量': 'COLUMNNAME15',
                                                    '批号': 'COLUMNNAME20'
                                                    }, inplace=False)
    renameDataFrame['COLUMNNAME34'] = renameDataFrame['COLUMNNAME33']
    return renameDataFrame
