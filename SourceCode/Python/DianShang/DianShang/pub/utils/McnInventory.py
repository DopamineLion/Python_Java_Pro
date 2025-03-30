# 库存表
import os

import pandas as pd


from pub.utils.ExcelUtils import tableList, dfTable
from pub.utils.TimeUtils import nowTimeSecond

exportExcelPath = 'D:\\所有仓Excel.xlsx'
orderDf = pd.read_excel(exportExcelPath, dtype=str)
orderDf['订单盒数'] = orderDf['原始采购数量'].astype(int) * orderDf['系数'].astype(int)

importInventoryExcelPath = 'D:\\库存.xlsx'
inventoryDf = pd.read_excel(importInventoryExcelPath, dtype=str)
inventoryDf['减账'] = 0
inventoryDf['减账后库存'] = inventoryDf['数量']
inventoryDf['分配明细'] = ''
inventoryDf.sort_values(by=['项目号', '生产日期'], ascending=[True, False], inplace=True)
# 首先将pandas读取的数据转化为array
# data_array = np.array(orderDf)
# 然后转化为list形式
# data_list = data_array.tolist()

# try:
# 订单结果
resultOrderList = []
resultOrderColumns = orderDf.columns.tolist()
resultOrderColumns.append('生产日期')
resultOrderColumns.append('失效日期')
resultOrderColumns.append('批号')
resultOrderColumns.append('分配数量')
resultOrderList.append(resultOrderColumns)
# resultInventoryList = []
# resultInventoryColumns = inventoryDf.columns.tolist()
# resultInventoryList.append(resultInventoryColumns)

# for index, orderRow in orderDf[['项目号', '原始采购数量', '系数']].iterrows():
for index, orderRow in orderDf.iterrows():
    inventoryDfEleCopy = inventoryDf.loc[inventoryDf['项目号'] == orderRow['项目号']]
    # 同一个单号不超过2个批号的标记
    tag = 2
    msg = None
    for indexI, inventoryRow in inventoryDfEleCopy.iterrows():
        if tag <= 0:
            msg = '批号超过2个了'
            print(msg)
            break
        resultOrderRowList = orderRow.tolist()
        over = False
        # 系数
        coe = int(orderRow['系数'])
        # 订单数
        # orderNum = int(orderRow['原始采购数量']) * int(orderRow['系数'])
        orderNum = orderRow['订单盒数']
        # 库存数
        inventoryNum = int(inventoryRow['减账后库存'])
        # 减值
        minus = None
        # 库存余量
        remainder = None
        # 如果库存大于订单数
        if inventoryNum >= orderNum:
            # 库存余数
            remainder = inventoryNum - orderNum
            # 减值
            minus = orderNum
            over = True
        # 单一日期库存不足
        else:
            # 套数
            tao = inventoryNum // coe
            # 减值
            minus = tao * coe
            # 库存余数
            remainder = inventoryNum - minus
        # 库存减账数量
        # inventoryDf['减账'].iloc[indexI] += minus
        inventoryDf.loc[indexI, '减账后库存'] = remainder
        inventoryDf.loc[indexI, '减账'] += minus
        oStr = orderDf.loc[index, '采购单号'] + ':' + orderDf.loc[index, '商品编号'] + ':' + str(minus) + ' '
        inventoryDf.loc[indexI, '分配明细'] += oStr
        # 订单余数
        # orderDf['订单盒数'].iloc[index] = orderNum - minus
        orderDf.loc[index, '订单盒数'] = orderNum - minus
        resultOrderRowList.append(inventoryRow['生产日期'])
        resultOrderRowList.append(inventoryRow['失效日期'])
        resultOrderRowList.append(inventoryRow['批号'])
        resultOrderRowList.append(minus)
        resultOrderList.append(resultOrderRowList)
        tag -= 1
        if over:
            break
    # 总库存不够
    # allDifference = orderDf['订单盒数'].iloc[index]
    allDifference = orderDf.loc[index, '订单盒数']
    if orderDf['订单盒数'].iloc[index] > 0:
        resultOrderRowList = orderRow.tolist()
        if msg is not None:
            resultOrderRowList.append('总库存不足')
            resultOrderRowList.append('总库存不足')
        else:
            resultOrderRowList.append(msg)
            resultOrderRowList.append(msg)
        resultOrderRowList.append(allDifference)
        resultOrderList.append(resultOrderRowList)

resultOrderDf = pd.DataFrame(resultOrderList[1:], columns=resultOrderList[0])

exportResultPath = 'D://MCN订单分配结果' + nowTimeSecond() + '.xlsx'
if not os.path.exists(exportResultPath):
    resultOrderDf.to_excel(exportResultPath, sheet_name='订单表', index=False)
    with pd.ExcelWriter(exportResultPath, engine='openpyxl', mode='a', if_sheet_exists='overlay') as writer:
        inventoryDf.to_excel(writer, sheet_name='库存表', index=False)
else:
    with pd.ExcelWriter(exportResultPath, engine='openpyxl', mode='a', if_sheet_exists='overlay') as writer:
        resultOrderDf.to_excel(writer, sheet_name='订单表', index=False)
        inventoryDf.to_excel(writer, sheet_name='库存表', index=False)


# tableList(resultOrderList)
# print('xx')
# print('xx')
# print('xx')
# print('xx')
# dfTable(inventoryDf)

# except Exception as e:
#     print('分配库存数量时出错')
