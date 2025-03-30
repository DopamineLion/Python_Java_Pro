import re
import pendulum
from pywinauto import mouse
from pywinauto.keyboard import send_keys

from pub.utils.FileUtils import mkDir2, updateImportFile


def closeTabUI(tabList, appWin, str):
    for tab in tabList.children():
        if tab.element_info.name.find(str) >= 0:
            rectTab = tab.rectangle().mid_point()
            mouse.double_click(button='right', coords=(rectTab.x, rectTab.y))
            appWin["上下文"]['Close'].click_input()
            break


def exportReport(app, dlg, waNo, waDescr, strImport, importFileName, exportFolder, excelName):
    tabList = dlg.children()[0].children()[1].children()[0].children()[3]
    bestMatchName = importFileName + 'ListItem'
    # 导入文件路径
    importFileFolder = "D:\\stocktaking\\importTxt\\"
    importFilePath = importFileFolder + importFileName

    # exportFileName = exportFolder + excelName + '.xlsx'
    mkDir2(exportFolder)
    mkDir2(importFileFolder)
    updateImportFile(importFilePath, strImport)

    # 自定义报表
    dlg.wait(wait_for='ready', timeout=180, retry_interval=1)
    dlg.child_window(best_match='自定义报表').click_input(double=True)

    # 点击导入按钮
    dlg.wait(wait_for='ready', timeout=180, retry_interval=1)
    importBta = dlg.children()[0].children()[1].children()[0].children()[0].children()[0].children()[0].children()[0].children()[0].children()[2].children()[0].children()[0].children()[2]
    importBta.click()

    # 打开窗口-选择要导入的模板
    app["打开"].wait(wait_for='ready', timeout=180, retry_interval=1)
    addr = app["打开"].child_window(best_match='上一个位置Button')
    addr.click_input()
    # 键入文件路径
    send_keys(importFileFolder)
    send_keys('{ENTER}')
    app["打开"].wait(wait_for='ready', timeout=180, retry_interval=1)

    # 判断文件如果没有后缀的情况
    if app["打开"].child_window(best_match=bestMatchName):
        app["打开"].child_window(best_match=bestMatchName).click_input()
    else:
        bestMatchName = ".".join(bestMatchName.split(".")[:-1])
        app["打开"].child_window(best_match=bestMatchName).click_input()
    app["打开"].child_window(best_match='打开(O)Button').click()

    # 点击搜索
    dlg.wait(wait_for='ready', timeout=180, retry_interval=1)
    searchBta = dlg.children()[0].children()[1].children()[0].children()[0].children()[0].children()[0].children()[0].children()[0].children()[2].children()[0].children()[0].children()[0]
    searchBta.click()

    # 查询结果
    dlg.wait(wait_for='ready', timeout=180, retry_interval=1)
    dataList = dlg.children()[0].children()[1].children()[0].children()[0].children()[0].children()[0].children()[0].children()[0].children()[0].children()[0].children()[0]

    nothingData = True
    if len(dataList.children()) > 2:
        nothingData = False
    if nothingData:
        # closeTabUI(tabList, app, '自定义查询')
        pass
        # return False


    # # 补打签
    # nothingData = True
    # if len(dataList.children()) > 2:
    #     nothingData = False
    # if nothingData:
    #     print(waNo + waDescr + '不包含任何LZ28库位零捡签')
    #     closeTabUI(tabList, app, '自定义查询')
    #     return
    #
    # # 拣货
    # nothingData = True
    # for i in range(len(dataList.children())):
    #     waveEle = dataList.children()[i].element_info.name
    #     if waveEle.find('WAVE') >= 0:
    #         nothingData = False
    #         break
    # if nothingData:
    #     print(waNo + waDescr + '不包含任何零捡签')
    #     closeTabUI(tabList, app, '自定义查询')

    # 右键导出
    rect = dataList.rectangle().mid_point()
    mouse.double_click(button='right', coords=(rect.x, rect.y))
    app["上下文"].wait(wait_for='ready', timeout=180, retry_interval=1)
    app["上下文"]['导出'].click_input()

    # 文件导出窗口
    app["文件导出"].wait(wait_for='ready', timeout=180, retry_interval=1)
    app["文件导出"].child_window(title='选择路径', control_type="Button").click_input()

    # 另存为窗口
    addr = app["另存为"].child_window(best_match='上一个位置Button')
    addr.click_input()

    # 键入文件夹
    exportFolder = re.sub(r" ", "{SPACE}", exportFolder)
    send_keys(exportFolder)
    send_keys('{ENTER}')

    # 键入文件名
    fileNameEdit = app["另存为"].child_window(title='文件名:', control_type="Edit")
    fileNameEdit.set_text(excelName)
    app["另存为"].child_window(title='保存类型:', control_type="ComboBox").select('*.xlsx')
    send_keys('%s')

    # 确定导出
    app["文件导出"].wait(wait_for='ready', timeout=180, retry_interval=1)
    app["文件导出"].child_window(title='确定', control_type="Button").click_input()

    # 判断本地是否导出成功
    # time.sleep(2)
    # while not Path(exportFileName).exists():
    #     time.sleep(2)

    # 再次点确定 退出
    app["文件导出"].child_window(title='确定', control_type="Button").wait(wait_for='enabled', timeout=180, retry_interval=1)
    app["文件导出"].child_window(title='确定', control_type="Button").click_input()

    closeTabUI(tabList, app, '自定义查询')
    return True
