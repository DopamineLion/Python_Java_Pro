import gc
import os
import pandas as pd
import re
import time
import glob
import traceback
from pub.utils.ConfigUtils import getConfigValue, getDefaultConfigValue
from pub.utils.ExcelManage import jianHuoA4, waiXiangA4, jianHuoJasperSourcesExcel, waiXiangJasperSourcesExcel
from pub.utils.ExportReportTools import closeTabUI, exportReport
from pub.utils.FileUtils import mkDir2
from pub.utils.JasperUtils import processingReportJapser
# from pub.utils.JasperUtils import processingReportJapser
from pub.utils.ExcelToJson import excelToJson
from pub.utils.LoginUtils import loginWMS
import warnings
import logging

from pub.utils.TimeUtils import nowTime, nowDate, diffNowTime, nowTimeSecond

# 配置文件路径
configPath = "D:\\stocktaking\\variableConfig.ini"

# 配置logging模块
logDir = 'D:\\stocktaking\\JianHuoQianLog\\' + nowDate() + '\\'
logFileName = nowTime() + 'logFile'
mkDir2(logDir)
logging.basicConfig(filename=logDir + '\\' + logFileName + '.txt', level=logging.DEBUG,
                    format='%(asctime)s - %(levelname)s - %(message)s')

# 忽略警告信息
warnings.simplefilter('ignore')

# 定时器时间（默认5分钟）
sleepMinutes = getDefaultConfigValue(configPath, 'JianHuo', 'sleepMinutes', 5)

if type(sleepMinutes) == str:
    sleepMinutes = int(float(sleepMinutes))


def startTimer():
    timeSleep = 60 * sleepMinutes
    print('定时器执行中:' + str(sleepMinutes) + '分钟')
    logging.info(nowTime() + '定时器执行中' + str(sleepMinutes) + '分钟')
    time.sleep(timeSleep)


# 录入要查询的波次信息
logging.info(nowTime() + '请输入需要打印拣货签的波次号：')
inputWaveNo = input('请输入需要打印拣货签的波次号：').strip()
logging.info(nowTime() + inputWaveNo)
# 是否正确的波次号
isRealWave = False
# 输入的波次号是否打印过一次
notPrintInputWave = True
if inputWaveNo != '' and re.match(r'.*WAVE|wave.*', inputWaveNo) and len(inputWaveNo) == 13:
    isRealWave = True
else:
    print('您未输入正确的波次号')
    logging.info(nowTime() + '您未输入正确的波次号')

# 登录
tupleData = loginWMS('报表')
dlg = tupleData[0]
app = tupleData[1]

# 开启定时器循环查询
try:
    while True:
        sleepMinutes = getDefaultConfigValue(configPath, 'JianHuo', 'sleepMinutes', 5)
        if type(sleepMinutes) == str:
            sleepMinutes = int(float(sleepMinutes))
        dlg.wait(wait_for='ready', timeout=180, retry_interval=1)
        dlg.child_window(best_match='自定义SQL查询').click_input(double=True)
        # ac1 = 1
        # while ac1 == 1:
        #     try:
        #         dlg.child_window(best_match='自定义SQL查询').click_input(double=True)
        #         ac1 = 2
        #     except IndexError:
        #         ac1 = 1

        # SQL文本框
        dlg.wait(wait_for='ready', timeout=180, retry_interval=1)
        edit_sql = dlg.children()[0].children()[1].children()[0].children()[0].children()[0].children()[0].children()[
            0].children()[0].children()[0].children()[0]
        edit_sql.click_input()

        # 设置SQL语句
        sqlConfigValue = getConfigValue(configPath, 'JianHuo', 'SQL')
        sqlWaveNo = sqlConfigValue
        # if inputWaveNo != '' and re.match(r'.*WAVE.*', inputWaveNo, re.I):
        #     sqlWaveNo += " or t.waveno='" + inputWaveNo.strip() + "'"
        if isRealWave and notPrintInputWave:
            sqlWaveNo += " or t.waveno='" + inputWaveNo.strip() + "'"
        notPrintInputWave = False
        edit_sql.set_text(sqlWaveNo)

        # 查询按钮
        dlg.wait(wait_for='ready', timeout=180, retry_interval=1)
        btn_sql = dlg.children()[0].children()[1].children()[0].children()[0].children()[0].children()[0].children()[
            0].children()[0].children()[1].children()[0].children()[0]
        btn_sql.click()

        # SQL查询结果列表
        dlg.wait(wait_for='ready', timeout=180, retry_interval=1)
        waveDataList = \
            dlg.children()[0].children()[1].children()[0].children()[0].children()[0].children()[0].children()[
                1].children()[0].children()[0].children()[0].children()[0].children()[0]
        waveNoList = []

        for i in range(len(waveDataList.children())):
            waveEle = waveDataList.children()[i]
            waveEleName = waveEle.element_info.name
            no = waveEle.children()[0].element_info.name
            descr = waveEle.children()[1].element_info.name
            editTime = waveEle.children()[2].element_info.name
            if waveEleName.find('WAVE') >= 0:
                minutesDiff = diffNowTime(editTime)
                waveNoList.append({'waveno': no, 'descr': descr, 'minutesDiff': minutesDiff})

        # Tab标签-关闭
        tabList = dlg.children()[0].children()[1].children()[0].children()[3]
        closeTabUI(tabList, app, '自定义SQL查询')

        # 没有分配完成的波次
        if len(waveNoList) == 0:
            print('时间:' + nowTime() + '   未监测到分配完成的波次')
            logging.info(nowTime() + '时间:' + nowTime() + '   未监测到分配完成的波次')
            startTimer()
            continue

        # 遍历波次
        notHasNeedPrintWave = 0
        for waEle in waveNoList:
            waNo = waEle.get('waveno')
            waDescr = waEle.get('descr')
            waMinutesDiff = waEle.get('minutesDiff')

            waDescr = re.sub(r"[\n\t\r:]", "*", waDescr)
            exportFolder = "D:\\拣货\\" + nowDate() + '\\' + waNo + '-' + waDescr + "\\" + '导出时间' + nowTime() + '\\'

            # 如果是特意查询的波次，就允许多次执行查询
            if inputWaveNo != waNo:
                id_file = 'D:\\拣货\\**\\*' + waNo + '*'
                files = glob.glob(id_file, recursive=True)
                if len(files) > 0:
                    if int(waMinutesDiff) > sleepMinutes:
                        notHasNeedPrintWave += 1
                        if notHasNeedPrintWave == len(waveNoList):
                            print('本地都已存在这些波次的拣货签文件，将执行定时器:' + str(sleepMinutes) + '分钟')
                            logging.info(nowTime() + '本地都已存在这些波次的拣货签文件，将执行定时器:' + str(
                                sleepMinutes) + '分钟')
                        continue
                    else:
                        print(
                            waNo + ' ' + waDescr + '波次为第N次分配成功，程序将继续打签，请注意：以最新时间拣货签为主！！！')
                        logging.info(
                            nowTime() + waNo + ' ' + waDescr + '波次为第N次分配成功，程序将继续打签，请注意：以最新时间拣货签为主！！！')

            strImport = '''TYPENAME:
06-物流出库
REPORTNAME:
波次分配明细查询
AND(^-^)波次号(^-^)=(^-^)''' + waNo + '''
AND((^-^)库位(^-^)like(^-^)LZ%
OR(^-^)库位(^-^)in ((^-^)'28','29'))
Field:
COLUMNNAME1,COLUMNNAME2,COLUMNNAME3,COLUMNNAME4,COLUMNNAME5,COLUMNNAME6,COLUMNNAME7,COLUMNNAME8,COLUMNNAME9,COLUMNNAME10,COLUMNNAME11,COLUMNNAME12,COLUMNNAME13,COLUMNNAME14,COLUMNNAME15,COLUMNNAME16,COLUMNNAME17,COLUMNNAME18,COLUMNNAME19,COLUMNNAME20,COLUMNNAME21,COLUMNNAME22,COLUMNNAME23,COLUMNNAME24,COLUMNNAME25
'''

            importFileName = "分配明细查询条件.txt"
            exportFileName = waNo + '-' + waDescr
            exportReport(app, dlg, waNo, waDescr, strImport, importFileName, exportFolder, exportFileName)

            # 导出拣货签（分配明细）Excel
            jianHuoStrImport = '''TYPENAME:
06-物流出库
REPORTNAME:
合资拣货标签补打印
AND(^-^)波次号(^-^)=(^-^)''' + waNo + '''
AND((^-^)LOCATION(^-^)like(^-^)LZ%
OR(^-^)LOCATION(^-^)in ((^-^)'28','29'))
Field:
COLUMNNAME1,COLUMNNAME2,COLUMNNAME3,COLUMNNAME4,COLUMNNAME5,COLUMNNAME7,COLUMNNAME8,COLUMNNAME9,COLUMNNAME10,COLUMNNAME11,COLUMNNAME12,COLUMNNAME13,COLUMNNAME14,COLUMNNAME15,COLUMNNAME16,COLUMNNAME17,COLUMNNAME18,COLUMNNAME19,COLUMNNAME20,COLUMNNAME21,COLUMNNAME22,COLUMNNAME23,COLUMNNAME24,COLUMNNAME25,COLUMNNAME26,COLUMNNAME27,COLUMNNAME28,COLUMNNAME29,COLUMNNAME30,COLUMNNAME31,COLUMNNAME32,COLUMNNAME33,COLUMNNAME34,COLUMNNAME35,COLUMNNAME36,COLUMNNAME37,COLUMNNAME38,COLUMNNAME39,COLUMNNAME40,COLUMNNAME41,COLUMNNAME42,COLUMNNAME43,COLUMNNAME44,COLUMNNAME45,COLUMNNAME46,COLUMNNAME47,COLUMNNAME48,COLUMNNAME49,COLUMNNAME50,COLUMNNAME51,COLUMNNAME52,COLUMNNAME53,COLUMNNAME54,COLUMNNAME55,COLUMNNAME56,COLUMNNAME57,COLUMNNAME58,COLUMNNAME59,COLUMNNAME60,COLUMNNAME61,COLUMNNAME62,COLUMNNAME63,COLUMNNAME64,COLUMNNAME65
'''
            importFileName = "合资拣货补打签查询条件LZ28.txt"
            exportFileNameDel = waNo + '-' + waDescr + 'LZ28外箱签(打签时间' + nowTime() + ')'
            exportReport(app, dlg, waNo, waDescr, jianHuoStrImport, importFileName, exportFolder, exportFileNameDel)

            # 自定义报表
            dlg.wait(wait_for='ready', timeout=180, retry_interval=1)
            dlg.child_window(best_match='自定义报表').click_input(double=True)

            # 合并两张Excel
            exportPath = exportFolder + exportFileName + '.xlsx'
            exportPathDel = exportFolder + exportFileNameDel + '.xlsx'
            df1 = pd.read_excel(exportPath, sheet_name='sheet1', dtype=str)
            df2 = pd.read_excel(exportPathDel, sheet_name='sheet1', dtype=str)
            # 合并两张Excel
            jianHuoSheetName = '拣货签'
            waiXiangSheetName = '外箱签'
            with pd.ExcelWriter(exportPath, engine='openpyxl', mode='w') as writer:
                df1.to_excel(writer, sheet_name=jianHuoSheetName, index=False)
                df2.to_excel(writer, sheet_name=waiXiangSheetName, index=False)
            # 删除外箱签Excel
            os.remove(exportPathDel)

            diaoBo = re.match(r'.*调拨.*', waDescr)
            # 输出拣货签-A4纸
            jianHuoA4(diaoBo, exportPath, jianHuoSheetName, waNo, waDescr)

            # 输出外箱签A4纸
            if not diaoBo:
                waiXiangA4(diaoBo, exportPath, waiXiangSheetName, waNo, waDescr)

                # Json配置文件输出路径
                outputJsonFilePath = 'D:\\stocktaking\\reports\\'

                # 输出拣货签（80*60外箱签）PDF格式
                df = jianHuoJasperSourcesExcel(exportPath, jianHuoSheetName, waDescr)
                # 输出Json
                jsonName = 'JianHuoQian.json'
                excelToJson(outputJsonFilePath + jsonName, df)
                pdfFileName = exportFileName + '拣货签.pdf'
                jrxmlName = 'JianHuoQian.jrxml'
                jrxmlDir = getDefaultConfigValue(configPath, 'Jasper', 'jrxmlDir', 'D:/stocktaking/reports/')
                # jasperPrint = JasperPrint()
                processingReportJapser(jrxmlDir, jrxmlName, outputJsonFilePath + jsonName, exportFolder + pdfFileName)
                # processingJasper(exportFolder, pdfFileName, outputJsonFilePath, jrxmlName, jsonName)

                print('输出拣货签(PDF格式)' + exportFolder + pdfFileName + '完成')
                logging.info(nowTime() + '输出拣货签(PDF格式)' + exportFolder + pdfFileName + '完成')

                # 输出外箱签（100*100外箱签）PDF格式
                dfWai = waiXiangJasperSourcesExcel(exportPath, waiXiangSheetName)
                # 输出Json
                jsonName = 'WaiXiangQian.json'
                excelToJson(outputJsonFilePath + jsonName, dfWai)
                pdfFileName = exportFileName + '外箱签.pdf'
                jrxmlName = 'WaiXiangQian.jrxml'
                processingReportJapser(jrxmlDir, jrxmlName, outputJsonFilePath + jsonName, exportFolder + pdfFileName)

                # processingJasper(exportFolder, pdfFileName, outputJsonFilePath, jrxmlName, jsonName)
                print('输出外箱签(PDF格式)' + exportFolder + pdfFileName + '完成')
                logging.info(nowTime() + '输出外箱签(PDF格式)' + exportFolder + pdfFileName + '完成')
            closeTabUI(tabList, app, '自定义查询')

        # 打印
        # if re.match(r'京东|海参|POP', waDescr):
        # printerName = ''
        # print_file(excelFileName, sheetNameFormat, printerName)
        # 执行完后暂停
        closeTabUI(tabList, app, '自定义查询')
        startTimer()
except Exception as e:
    print(e)
    print(f"错误信息: {e}")
    logging.error(nowTimeSecond())
    # logging.error(f'错误信息: {e}')
    logging.error("An error occurred:", exc_info=True)
    traceback.print_exc()  # 打印完整的堆栈跟踪信息
    input('回车退出程序')
