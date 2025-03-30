import re
from configparser import ConfigParser
from pathlib import Path
import time
import pendulum
import pandas as pd
from pywinauto import mouse
from pywinauto.application import Application
from pywinauto.keyboard import send_keys

from pub.utils.ExportReportTools import closeTabUI
# from win32com.client.gencache import EnsureDispatch
# from win32com.client import constants
from pub.utils.ExcelUtils import print_file, setPrintOptionsForOpenpyxl
from pub.utils.XlsxSaver import XlsxSaver
import warnings
warnings.simplefilter('ignore')

config = ConfigParser()
config.read("D:\\stocktaking\\variableConfig.ini", "UTF-8")
username = config.get('Pub', "username")
password = config.get('Pub', "password")
mode = config.get('Pub', "selectMode")
regex = config.get('Pub', "regex")
localPrint = config.get('Pub', "localPrint")
wmsPath = config.get(mode, "wmsPath")
printerName = config.get(mode, "printerName")


def sc(string):
    return True if re.match(regex, string) else False


printWith = 0
while printWith != '1' and printWith != '2':
    printWith = input('直接输出至打印机(' + printerName + ')请输入1，打印至本地('+localPrint+')请输入2:')
if printWith == '2':
    printerName = localPrint

# """ 登录 WMS """
app = Application(backend='uia').start(wmsPath)
win = app.window(control_type="Window", class_name='SWT_Window0', title='FLUX WMS V4R3M1.112')
app.top_window().set_focus()
win.children()[0].children()[3].children()[1].type_keys(username, with_spaces=False, with_newlines=False)  # 用户名称：
# win.children()[0].children()[3].children()[1].type_keys(Wma['用户名'][0], with_spaces=False, with_newlines=False)  # 用户名称：
win.children()[0].children()[3].children()[3].type_keys(password, with_spaces=False, with_newlines=False)  # 用户密码：
win.children()[0].children()[3].children()[11].click_input()
win.children()[0].children()[3].children()[11].children()[0].children()[1].click_input()
win.children()[0].children()[4].children()[0].click()  # 登录
time.sleep(1)

# """ 判断是否有更新 """
win1 = app.window(control_type="Window", title='插件更新列表')
if win1.exists():
    win1.wait(wait_for='visible', timeout=80, retry_interval=0.3)
    win1.children()[0].children()[1].children()[2].click()
    win2 = win1.window(control_type="Window", title='更新')
    win2.wait(wait_for='visible', timeout=80, retry_interval=0.3)
    win2.children()[3].children()[0].click()

time.sleep(3)

# """ 库存管理 """
ac = 1
while ac == 1:
    try:
        win.wait(wait_for='ready', timeout=180, retry_interval=1)
        win.menu_select('库存管理')
        ac = 2
    except Exception:
        ac = 1

# """ 库存余量 """
ac1 = 1
while ac1 == 1:
    try:
        win.wait(wait_for='ready', timeout=60, retry_interval=1)
        win.children()[0].children()[1].children()[0].children()[1].children()[0].children()[0].children()[0].children()[0].children()[
            0].get_child('库存余量').click_input(double=True)
        ac1 = 2
    except IndexError:
        ac1 = 1

# 查询库存Button
ac2 = 1
while ac2 == 1:
    try:
        win.wait(wait_for='ready', timeout=60, retry_interval=1)
        win.child_window(title="客户", control_type="Edit").set_text('00028')
        ac2 = 2
    except IndexError:
        ac2 = 1

win.child_window(best_match='按批次／库位／跟踪号查询Button').click()

# 库存列表
ac3 = 1
dataList = win
while ac3 == 1:
    try:
        win.wait(wait_for='ready', timeout=60, retry_interval=1)
        dataList = win.children()[0].children()[1].children()[0].children()[0].children()[0].children()[0].children()[0].children()[0].children()[0].children()[0]
        dataList.click_input()
        ac3 = 2
    except IndexError:
        ac3 = 1

# 文件路径及名字
T2 = pendulum.now()
T3 = T2.strftime('%Y-%m-%d %H:%M')
newT3 = re.sub(r"[\n\t\r:]", "时", T3)
folderName = "D:\\盘点\\全盘\\"
excelName = '盘点' + newT3
excelFileName = folderName + excelName + '.xlsx'


try:
    # 右键列表-导出到Excel
    win.wait(wait_for='ready', timeout=60, retry_interval=1)
    rect = dataList.rectangle().mid_point()
    mouse.double_click(button='right', coords=(rect.x, rect.y))
    app["上下文"].wait(wait_for='ready', timeout=60, retry_interval=1)
    app["上下文"]['导出到Excel'].click_input()

    # 另存为
    app["另存为"].wait(wait_for='ready', timeout=60, retry_interval=1)
    addr = app["另存为"].child_window(best_match='上一个位置Button')
    addr.click_input()

    # 键入文件夹
    folderPath = Path(folderName)
    if not folderPath.exists():
        folderPath.mkdir(parents=True)
    send_keys(folderName)
    send_keys('{ENTER}')

    # 键入文件名
    app["另存为"].wait(wait_for='ready', timeout=60, retry_interval=1)
    fileName = app["另存为"].child_window(title='文件名:', control_type="Edit")
    fileName.set_text(excelName)
    saveType = app["另存为"].child_window(best_match='保存类型:ComboBox')
    saveType.click_input()
    saveType.select("*.xlsx")
    send_keys('%s')
    while not app['操作结果'].exists():
        time.sleep(2)
except Exception as e:
    print(print(f"错误信息: {e}"))
    time.sleep(120)

while not Path(excelFileName).exists():
    time.sleep(2)

# Excel处理
createSheetName = '筛选掉立库后'
dataFrame = pd.read_excel(excelFileName, engine='openpyxl', dtype={'库位': str})
reserve_columns = ['库位', '产品', '英文描述', '入库批号', '库存数量']
reserveDataFrame = dataFrame[reserve_columns]
renameDataFrame = reserveDataFrame.rename(columns={'产品': '项目号', '英文描述': '品名', '库存数量': '数量'}, inplace=False)
sortDataFrame = renameDataFrame.sort_values(by=['库位', '项目号'], inplace=False, ascending=True)
newDataFrame = sortDataFrame.loc[sortDataFrame['库位'].apply(sc)]
ind = excelFileName.rfind('.')
excelFileName = excelFileName[:ind] + 'Format' + excelFileName[ind:]
newDataFrame.to_excel(excelFileName, sheet_name=createSheetName, index=False)

# 格式化表格
df = pd.read_excel(excelFileName, sheet_name=createSheetName)
createSheetName += 'Format'
xlsx = XlsxSaver(df, excelFileName, createSheetName)  # 初始化一个对象, 设定保存后的文件名和表名
xlsx.set_width('库位', 13)  # 手动指定某列列宽
xlsx.set_width('项目号', 13)  # 手动指定某列列宽
xlsx.set_width('品名', 49)  # 手动指定某列列宽
xlsx.set_width('入库批号', 18)  # 手动指定某列列宽
xlsx.set_width('数量', 9)  # 手动指定某列列宽
xlsx.set_color_alignment_font_border('5B9BD5', 'DDEBF7', None, 12, 11, '微软雅黑')
xlsx.save()

# 设置打印属性
headerTitle = '盘点' + newT3 + '  盘点人:______ 差异:________'
setPrintOptionsForOpenpyxl(excelFileName, '筛选掉立库后'+'Format', headerTitle)
send_keys('{ENTER}')

# 关闭Tab选项卡
win.wait(wait_for='ready', timeout=60, retry_interval=1)
tabList = win.children()[0].children()[1].children()[0].children()[3]
closeTabUI(tabList, app, '库存余量')
print('盘点文件输出至' + excelFileName + '，请等待打印')

# 打印
print_file(excelFileName, createSheetName, printerName)
print('盘点文件已打印成功至打印机:'+printerName)
input('回车退出程序')
