import re
import pandas as pd
from pub.utils.ExcelUtils import dfTable

excelFilePath = "C:\\Users\\Administrator\\Documents\\Data\\MCN\\Test.xlsx"
importExcelPath = 'D:\\Test2.xlsx'
importInventoryExcelPath = 'D:\\库存.xlsx'

exportExcelPath = 'D:\\所有仓Excel.xlsx'
exportSheet = '订单汇总'


# 总表
sumDataDf = pd.read_excel(importExcelPath, sheet_name='合计发货数量', dtype=str, skiprows=1)

# 8仓表
xls = pd.ExcelFile(importExcelPath)
sheetNames = xls.sheet_names
sheetNamesList = []
for sheetName in sheetNames:
    if re.match(r'.*北京|上海|广州|成都|武汉|沈阳|西安|德州.*', sheetName):
        sheetNamesList.append(sheetName)
print('共获取' + str(len(sheetNamesList)) + '个仓的数据' + sheetNamesList.__str__())

# 合并
df = pd.concat([pd.read_excel(importExcelPath, ls)for ls in sheetNamesList])
df.to_excel(exportExcelPath, sheet_name=exportSheet, index=False)

# 过滤
allDataDf = pd.read_excel(exportExcelPath, sheet_name=exportSheet, dtype=str, skiprows=1)
allDataDf.fillna('', inplace=True)
allDataDf = allDataDf[~allDataDf['采购单号'].str.contains('合计|入库交接|采购单号')]
dfLeft = pd.merge(allDataDf, sumDataDf[['项目号', '备注贴码', '商品编号', '系数']], left_on='商品编号', right_on='商品编号', how='left', indicator=True)
# 设置系数的默认值
dfLeft['系数'] = dfLeft['系数'].fillna(1)
dfLeft.to_excel(exportExcelPath, sheet_name=exportSheet, index=False)

# 未匹配到的商品编码
columnsLeftOnly = dfLeft[dfLeft['_merge'] == 'left_only']
if len(columnsLeftOnly) > 0:
    print('共有'+str(len(columnsLeftOnly))+'条数据，在8仓与总表联查过程中匹配失败。详细信息：')
    dfTable(columnsLeftOnly)


# 过滤数据和合并sheet
# allData = []
# for sheet in sheetNamesList:
#     tempDf = pd.read_excel(excelFilePath, sheet_name=sheet, dtype=str, skiprows=1)
#     columns = tempDf.columns
#     # for i in tempDF.loc[:, columns[0]]:
#     #     # if re.match(r'\d*', i):
#     #     if re.match(r'((?!合计).)\d*((?!合计).)', i):
#     #         print(i)
#     filterDf = tempDf[~tempDf[columns[0]].str.contains('合计')]
#     allData.append(tempDf)
# mergedDf = pd.concat(allData, ignore_index=True)
# print(mergedDf)
# mergedDf.to_excel('D://ee.xlsx', sheet_name='sheet1', index=False)


