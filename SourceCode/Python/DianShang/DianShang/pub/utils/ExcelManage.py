import logging

import pandas as pd

from pub.utils.ConfigUtils import getDefaultConfigValue
from pub.utils.ExcelUtils import setPrintOptionsForOpenpyxl, setPrintOptionsForOpenpyxl100X100
from pub.utils.TimeUtils import nowTime
from pub.utils.XlsxSaver import XlsxSaver


def waiXiangA4(diaoBo, exportPath, waiXiangSheetName, waNo, waDescr):
    dataFrame = pd.read_excel(exportPath, sheet_name=waiXiangSheetName, dtype=str)
    dataFrame = dataFrame[(dataFrame['LOCATION'].str.match('^LZ28.*$|28')) & (dataFrame['QTY_EACH_TEXT'] == dataFrame['数量'])]
    reserve_columns = ['LOCATION', 'SKU', 'SKUDESCR_E', 'QTY', 'LOTATT04']
    if diaoBo:
        reserve_columns.insert(0, 'ORDERNO')
    reserveDataFrame = dataFrame[reserve_columns]
    # 分组求和
    reserve_columns.remove('QTY')
    reserveDataFrame['QTY'] = reserveDataFrame['QTY'].astype(float)
    reserveDataFrame = reserveDataFrame.fillna(0).groupby(reserve_columns).agg({'QTY': 'sum'})
    # 重置索引
    reserveDataFrame = reserveDataFrame.reset_index()
    orderNo = 'ORDERNO'
    if diaoBo:
        orderNo = '订单号'
    renameDataFrame = reserveDataFrame.rename(columns={'LOCATION': '库位',
                                                       'SKU': '项目号',
                                                       'SKUDESCR_E': '品名',
                                                       'QTY': '数量',
                                                       'ORDERNO': orderNo,
                                                       'LOTATT04': '批号'
                                                       }, inplace=False)
    configPath = "D:\\stocktaking\\variableConfig.ini"
    # sortList = ['库位', '项目号', '批号']
    sortListStr = getDefaultConfigValue(configPath, 'JianHuo', 'sortList', '库位,项目号,批号')
    sortList = sortListStr.split(',')
    if diaoBo:
        sortList.insert(0, '订单号')
    sortDataFrame = renameDataFrame.sort_values(by=sortList, inplace=False, ascending=True)
    sheetNameA4 = '外箱签A4纸'
    # df_existing = pd.concat([dataFrame, sortDataFrame], ignore_index=True)
    # sortDataFrame.to_excel(writer, sheet_name=sheetNameA4, index=False)
    with pd.ExcelWriter(exportPath, mode='a') as writer:
        sortDataFrame.to_excel(writer, sheet_name=sheetNameA4, index=False)

    # 格式化表格
    df = pd.read_excel(exportPath, sheet_name=sheetNameA4, engine='openpyxl', dtype=str)
    xlsx = XlsxSaver(df, exportPath, sheetNameA4)  # 初始化一个对象, 设定保存后的文件名和表名
    if diaoBo:
        xlsx.set_width('订单号', 17)  # 手动指定某列列宽
    xlsx.set_width('库位', 12)  # 手动指定某列列宽
    xlsx.set_width('项目号', 12)  # 手动指定某列列宽
    xlsx.set_width('品名', 25)  # 手动指定某列列宽
    xlsx.set_width('批号', 12.5)  # 手动指定某列列宽
    xlsx.set_width('数量', 6)  # 手动指定某列列宽
    xlsx.set_color_alignment_font_border('5B9BD5', 'DDEBF7', None, 10, 10, '微软雅黑')
    xlsx.save()
    # 设置打印属性
    headerTitle = waNo + ' ' + waDescr + '外箱签汇总'
    setPrintOptionsForOpenpyxl100X100(exportPath, sheetNameA4, headerTitle)
    print('输出外箱签(A4纸)' + exportPath + '完成')
    logging.info(nowTime() + '输出外箱签(A4纸)' + exportPath + '完成')


def jianHuoA4(diaoBo, exportPath, jianHuoSheetName, waNo, waDescr):
    dataFrame = pd.read_excel(exportPath, sheet_name=jianHuoSheetName, dtype=str)
    reserve_columns = ['库位', '项目号', '名称', '规格', '最小数量', '批号']
    if diaoBo:
        reserve_columns.insert(0, 'WMS单号')
    reserveDataFrame = dataFrame[reserve_columns]
    # 分组求和
    reserve_columns.remove('最小数量')
    reserveDataFrame['最小数量'] = reserveDataFrame['最小数量'].astype(float)
    reserveDataFrame = reserveDataFrame.fillna(0).groupby(reserve_columns).agg({'最小数量': 'sum'})
    # 重置索引
    reserveDataFrame = reserveDataFrame.reset_index()
    renameDataFrame = reserveDataFrame.rename(columns={'最小数量': '数量'}, inplace=False)
    sortList = []
    if diaoBo:
        sortList = ['WMS单号', '库位', '项目号', '批号', '数量']
    else:
        sortList = ['库位', '项目号', '批号', '数量']
    sortDataFrame = renameDataFrame.sort_values(by=sortList, inplace=False, ascending=True)
    sheetNameA4 = '拣货签A4纸'
    # df_existing = pd.concat([dataFrame, sortDataFrame], ignore_index=True)
    # sortDataFrame.to_excel(writer, sheet_name=sheetNameA4, index=False)
    with pd.ExcelWriter(exportPath, mode='a') as writer:
        sortDataFrame.to_excel(writer, sheet_name=sheetNameA4, index=False)

    # 格式化表格
    df = pd.read_excel(exportPath, sheet_name=sheetNameA4, engine='openpyxl', dtype=str)
    xlsx = XlsxSaver(df, exportPath, sheetNameA4)  # 初始化一个对象, 设定保存后的文件名和表名
    if diaoBo:
        xlsx.set_width('WMS单号', 17)  # 手动指定某列列宽
    xlsx.set_width('库位', 12)  # 手动指定某列列宽
    xlsx.set_width('项目号', 14)  # 手动指定某列列宽
    xlsx.set_width('名称', 22)  # 手动指定某列列宽
    xlsx.set_width('规格', 21)  # 手动指定某列列宽
    xlsx.set_width('批号', 17)  # 手动指定某列列宽
    xlsx.set_width('数量', 9)  # 手动指定某列列宽
    xlsx.set_color_alignment_font_border('5B9BD5', 'DDEBF7', None, 12, 11, '微软雅黑')
    xlsx.save()
    # 设置打印属性
    headerTitle = waNo + ' ' + waDescr + '拣货签汇总'
    setPrintOptionsForOpenpyxl(exportPath, sheetNameA4, headerTitle)
    print('输出拣货签(A4纸)' + exportPath + '完成')
    logging.info(nowTime() + '输出拣货签(A4纸)' + exportPath + '完成')


def jianHuoJasperSourcesExcel(fileName, sheetName, waDescr):
    # 读取excel的数据
    xls = pd.read_excel(fileName, sheet_name=sheetName, dtype=str, engine='openpyxl')
    xlsSelectivity = xls[~((xls['库位'].str.match('^LZ28.*$|28')) & (xls['单位'] == 'CS'))]
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
    # renameDataFrame['COLUMNNAME33'] = renameDataFrame['COLUMNNAME34']
    renameDataFrame['COLUMNNAME34'] = waDescr
    with pd.ExcelWriter(fileName, mode='a') as writer:
        renameDataFrame.to_excel(writer, sheet_name='拣货签数据源', index=False)
    return renameDataFrame


def waiXiangJasperSourcesExcel(fileName, sheetName):
    # 读取excel的数据
    xls = pd.read_excel(fileName, sheet_name=sheetName, dtype=str, engine='openpyxl')
    xls = xls[(xls['LOCATION'].str.match('^LZ28.*$|28')) & (xls['QTY_EACH_TEXT'] == xls['数量'])]
    reserve_columns = ['LOCATION', '波次号', '订单号', '客户号', 'SKU', '收货人名字', 'SKUDESCR_C', '规格', 'UOM_NAME', 'QTY', 'LOTATT04', 'LOTATT01', '托盘号', '标签号', '打印时间']
    dataFrame = xls.loc[:, reserve_columns]
    configPath = "D:\\stocktaking\\variableConfig.ini"
    sortSourcesListStr = getDefaultConfigValue(configPath, 'JianHuo', 'sortSourcesList', '收货人名字,LOCATION,SKU,LOTATT04')
    sortSourcesList = sortSourcesListStr.split(',')
    sortDataFrame = dataFrame.sort_values(by=sortSourcesList, inplace=False, ascending=True)
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
    with pd.ExcelWriter(fileName, mode='a') as writer:
        renameDataFrame.to_excel(writer, sheet_name='外箱签数据源', index=False)
    return renameDataFrame
