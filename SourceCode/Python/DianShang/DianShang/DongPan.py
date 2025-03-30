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
from pub.utils.ExcelUtils import print_file, setPrintOptionsForOpenpyxl
# from win32com.client.gencache import EnsureDispatch
# from win32com.client import constants
from pub.utils.XlsxSaver import XlsxSaver
import warnings
warnings.simplefilter('ignore')

config = ConfigParser()
config.read("D:\\stocktaking\\variableConfig.ini", "UTF-8")
mode = config.get('Pub', "selectMode")
username = config.get('Pub', "username")
password = config.get('Pub', "password")
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

T1 = input('输入起始时间 年 月 日 时 < 2025-01-01 00:01 >:')
T2 = pendulum.now()
T3 = T2.strftime('%Y-%m-%d %H:%M')
if T1:
    # dt = pendulum.from_format(T1, "YYYY-MM-DD HH:mm")
    T5 = T1
else:
    T4 = T2.strftime('%Y-%m-%d')
    T5 = T4 + ' 00:00'


# """ 登录 WMS """
app = Application(backend='uia').start(wmsPath)
win = app.window(control_type="Window", class_name='SWT_Window0', title='FLUX WMS V4R3M1.112')
app.top_window().set_focus()
win.children()[0].children()[3].children()[1].type_keys(username, with_spaces=False, with_newlines=False)  # 用户名称：
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

# win.child_window(title="业务仓库：", auto_id="264608", control_type="ComboBox").click_input()
# print(ele.element_info.enabled)
# ele.type_keys("1+2=")
# ele.send_keystrokes("Ctrl+A")
# print(item1.get_properties())

# """ 库存管理 """
ac = 1
while ac == 1:
    try:
        win.wait(wait_for='ready', timeout=60, retry_interval=1)
        win.menu_select('库存管理')
        ac = 2
    except Exception:
        ac = 1

# """ 库存盘点 """
dlg = app.window(control_type="Window", class_name='SWT_Window0', title='FLUX WMS V4R3M1.112')
ac1 = 1
while ac1 == 1:
    try:
        dlg.wait(wait_for='ready', timeout=60, retry_interval=1)
        dlg.child_window(best_match='库存盘点').click_input(double=True)
        ac1 = 2
    except IndexError:
        ac1 = 1

# """ 明细Tab """
temp1 = 1
while temp1 == 1:
    try:
        dlg.wait(wait_for='ready', timeout=60, retry_interval=1)
        dlg.child_window(title="明细", control_type="TabItem").click_input()
        temp1 = 2
    except IndexError:
        temp1 = 1


# """ 配置选项 """
dlg.wait(wait_for='ready', timeout=60, retry_interval=1)
com_ele1 = dlg.child_window(title="交易状态", control_type="ComboBox")
com_ele1.click_input()
com_ele1.child_window(best_match="盘点申请").click_input()
com_ele2 = dlg.child_window(title="类型", control_type="ComboBox")
com_ele2.click_input()
com_ele2.child_window(best_match="普通盘点").click_input()
dlg.child_window(title="客户", control_type="Edit").set_text('00028')


# """ 配置时间 """
dlg.child_window(title="申请时间", control_type="Edit").set_text(T3)
dlg.child_window(title="开始时间", control_type="Edit").set_text(T5)
dlg.child_window(title="结束时间", control_type="Edit").set_text(T3)


# """ 保存 """
dlg.child_window(title="保存", control_type="Button").click_input()
dlg.wait(wait_for='ready', timeout=60, retry_interval=1)


# """ 获取盘点编号 """
edit1 = dlg.child_window(title="盘点编号", control_type="Edit")
num = edit1.get_value()


# """ 点击列表中盘点编号项 """
item1 = dlg.child_window(title=num, control_type="ListItem")
item1.click_input()
dlg.wait(wait_for='ready', timeout=60, retry_interval=1)


# """ 获取坐标 """
rect = item1.rectangle().mid_point()
mouse.double_click(button='right', coords=(rect.x, rect.y))
dlg.wait(wait_for='ready', timeout=60, retry_interval=1)


# 选择右击出现的上下文窗口
app["上下文"]['生成盘点任务'].click_input()
dlg.wait(wait_for="ready", timeout=120, retry_interval=3)
# wt = 1
# while wt == 1:
#     try:
#         Application(backend='uia').connect(control_type="Window", class_name='SWT_Window0', title_re="FLUX WMS V4R3M1.112")
#         wt = 2
#     except Exception:
#         wt = 1
#         print("尝试连接失败")
#         time.sleep(3)
# print("重新响应")


# 文件导出
folder_name = "D:\\盘点\\动态盘点\\"
excelNameEdit = '动态盘点' + num
excelFileName = folder_name + excelNameEdit + '.xlsx'
try:
    # 文件导出窗口
    mouse.double_click(button='right', coords=(rect.x, rect.y))
    app["上下文"].wait(wait_for='ready', timeout=60, retry_interval=1)
    app["上下文"]['导出到Excel'].click_input()
    app["文件导出"].wait(wait_for='ready', timeout=60, retry_interval=1)
    xlsl = app["文件导出"].child_window(title_re='文件类型', control_type="ComboBox")
    xlsl.click_input()
    xlsl.select(".XLSX")
    app["文件导出"].child_window(title='选择路径', control_type="Button").click_input()

    # 另存为窗口
    app["另存为"].wait(wait_for='ready', timeout=60, retry_interval=1)
    addr = app["另存为"].child_window(best_match='上一个位置Button')
    addr.click_input()
    # 是否存在文件夹并创建
    folder_Path = Path(folder_name)
    if not folder_Path.exists():
        folder_Path.mkdir(parents=True)
    # 键入文件的地址
    send_keys(folder_name)
    send_keys('{ENTER}')
    # 键入文件的名字
    fileName = app["另存为"].child_window(title='文件名:', control_type="Edit")
    fileName.set_text(excelNameEdit)
    send_keys('%s')

    # 文件导出窗口确认导出文件
    app["文件导出"].wait(wait_for='ready', timeout=60, retry_interval=1)
    app["文件导出"].child_window(title='确定', control_type="Button").wait(wait_for='enabled', timeout=60, retry_interval=1)
    app["文件导出"].child_window(title='确定', control_type="Button").click_input()
    # openFolder = app["文件导出"].child_window(title='打开文件夹', control_type="Button")
except Exception as e:
    print(print(f"错误信息: {e}"))
    time.sleep(120)


time.sleep(2)
while not Path(excelFileName).exists():
    time.sleep(2)
app["文件导出"].child_window(title='确定', control_type="Button").wait(wait_for='enabled', timeout=60, retry_interval=1)


# Excel处理
createSheetName = '筛选掉立库后'
dataFrame = pd.read_excel(excelFileName, engine='openpyxl', dtype={'库位': str})
reserve_columns = ['库位', '产品', '自定义1', '入库批号', '库存数量']
reserveDataFrame = dataFrame[reserve_columns]
renameDataFrame = reserveDataFrame.rename(columns={'产品': '项目号', '自定义1': '品名', '库存数量': '数量'}, inplace=False)
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
headerTitle = '动盘' + num + ' ' + T5 + '至' + T3 + '盘点人:__'
setPrintOptionsForOpenpyxl(excelFileName, '筛选掉立库后'+'Format', headerTitle)

# 文件导出窗口关闭
app["文件导出"].wait(wait_for='ready', timeout=60, retry_interval=1)
app["文件导出"].child_window(title='确定', control_type="Button").wait(wait_for='enabled', timeout=60, retry_interval=1)
app["文件导出"].child_window(title='确定', control_type="Button").click_input()

# 关闭Tab选项卡
tabList = dlg.children()[0].children()[1].children()[0].children()[3]
closeTabUI(tabList, app, '库存盘点')
print('盘点文件输出至' + excelFileName + '，请等待打印')

# 打开excel文件
# workBook = xlwings.Book(excelFileName)
# workBook.sheets[0].activate()
# 打印
print_file(excelFileName, createSheetName, printerName)
print('打印成功，输出至打印机:'+printerName)
input('回车退出程序')
# pyinstaller --onefile ./main.py
