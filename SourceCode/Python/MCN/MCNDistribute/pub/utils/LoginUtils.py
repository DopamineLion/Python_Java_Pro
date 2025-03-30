from configparser import ConfigParser
import time
# import xlwings
# from openpyxl.workbook import Workbook
# from openpyxl.worksheet.page import PageMargins
from pywinauto.application import Application
# from pywinauto.findwindows import find_elements
# from win32com.client.gencache import EnsureDispatch
# from win32com.client import constants
# from styleframe import StyleFrame, Styler, utils


def loginWMS(menu):
    config = ConfigParser()
    config.read("D:\\stocktaking\\variableConfig.ini", "UTF-8")
    mode = config.get('Pub', "selectMode")
    username = config.get('Pub', "username")
    password = config.get('Pub', "password")
    wmsPath = config.get(mode, "wmsPath")


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

    win.wait(wait_for='ready', timeout=80, retry_interval=1)
    # """ 报表 """
    ac = 1
    while ac == 1:
        try:
            win.wait(wait_for='ready', timeout=180, retry_interval=1)
            win.menu_select(menu)
            ac = 2
        except Exception:
            ac = 1

    # dlg = app.window(control_type="Window", class_name='SWT_Window0', title='FLUX WMS V4R3M1.112')
    return win, app