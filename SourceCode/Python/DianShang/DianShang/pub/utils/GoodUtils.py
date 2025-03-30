import re

from pywinauto import mouse


def updateFileForWaveNo(waveInputFileName, updateWaveNoStr):
    # 打开文件用于读取和写入
    # with open(waveInputFileName, 'r', encoding='UTF-8') as file:
    #     # 读取文件内容
    #     content = file.read()
    #     # 修改内容，例如将所有的"old"替换为"new"
    #     result1 = re.search(r'WAVE[0-9]{9}', content, re.I)  # 不区分大小写
    #     print("匹配字符串：", result1.group())
    #     waveStr = result1.group()
    #     content = content.replace(waveStr, updateWaveNoStr)
    with open(waveInputFileName, 'w', encoding='UTF-8') as file:
        # 将文件指针移动到开始位置，以便覆盖原有内容
        file.seek(0)
        # 写入修改后的内容
        str1 = '''TYPENAME:
06-物流出库
REPORTNAME:
波次分配明细查询
AND(^-^)波次号(^-^)=(^-^)''' + updateWaveNoStr + '''
AND((^-^)库位(^-^)like(^-^)LZ%
OR(^-^)库位(^-^)in ((^-^)'28','29'))
Field:
COLUMNNAME1,COLUMNNAME2,COLUMNNAME3,COLUMNNAME4,COLUMNNAME5,COLUMNNAME6,COLUMNNAME7,COLUMNNAME8,COLUMNNAME9,COLUMNNAME10,COLUMNNAME11,COLUMNNAME12,COLUMNNAME13,COLUMNNAME14,COLUMNNAME15,COLUMNNAME16,COLUMNNAME17,COLUMNNAME18,COLUMNNAME19,COLUMNNAME20,COLUMNNAME21,COLUMNNAME22,COLUMNNAME23,COLUMNNAME24,COLUMNNAME25
'''
        str2 = '''TYPENAME:
06-物流出库
REPORTNAME:
合资拣货标签补打印
AND(^-^)波次号(^-^)=(^-^)''' + updateWaveNoStr + '''
AND(^-^)LOCATION(^-^)like(^-^)LZ28%
Field:
COLUMNNAME1,COLUMNNAME2,COLUMNNAME3,COLUMNNAME4,COLUMNNAME5,COLUMNNAME7,COLUMNNAME8,COLUMNNAME9,COLUMNNAME10,COLUMNNAME11,COLUMNNAME12,COLUMNNAME13,COLUMNNAME14,COLUMNNAME15,COLUMNNAME16,COLUMNNAME17,COLUMNNAME18,COLUMNNAME19,COLUMNNAME20,COLUMNNAME21,COLUMNNAME22,COLUMNNAME23,COLUMNNAME24,COLUMNNAME25,COLUMNNAME26,COLUMNNAME27,COLUMNNAME28,COLUMNNAME29,COLUMNNAME30,COLUMNNAME31,COLUMNNAME32,COLUMNNAME33,COLUMNNAME34,COLUMNNAME35,COLUMNNAME36,COLUMNNAME37,COLUMNNAME38,COLUMNNAME39,COLUMNNAME40,COLUMNNAME41,COLUMNNAME42,COLUMNNAME43,COLUMNNAME44,COLUMNNAME45,COLUMNNAME46,COLUMNNAME47,COLUMNNAME48,COLUMNNAME49,COLUMNNAME50,COLUMNNAME51,COLUMNNAME52,COLUMNNAME53,COLUMNNAME54,COLUMNNAME55,COLUMNNAME56,COLUMNNAME57,COLUMNNAME58,COLUMNNAME59,COLUMNNAME60,COLUMNNAME61,COLUMNNAME62,COLUMNNAME63,COLUMNNAME64,COLUMNNAME65
'''

        file.write(str1)

def updateFileForWaveNoBox(waveInputFileName, updateWaveNoStr):
    # 打开文件用于读取和写入
    # with open(waveInputFileName, 'r', encoding='UTF-8') as file:
    #     # 读取文件内容
    #     content = file.read()
    #     # 修改内容，例如将所有的"old"替换为"new"
    #     result1 = re.search(r'WAVE[0-9]{9}', content, re.I)  # 不区分大小写
    #     print("匹配字符串：", result1.group())
    #     waveStr = result1.group()
    #     content = content.replace(waveStr, updateWaveNoStr)
    with open(waveInputFileName, 'w', encoding='UTF-8') as file:
        # 将文件指针移动到开始位置，以便覆盖原有内容
        file.seek(0)
        # 写入修改后的内容
        str1 = '''TYPENAME:
06-物流出库
REPORTNAME:
合资拣货标签补打印
AND(^-^)波次号(^-^)=(^-^)''' + updateWaveNoStr + '''
AND(^-^)LOCATION(^-^)like(^-^)LZ28%
Field:
COLUMNNAME1,COLUMNNAME2,COLUMNNAME3,COLUMNNAME4,COLUMNNAME5,COLUMNNAME7,COLUMNNAME8,COLUMNNAME9,COLUMNNAME10,COLUMNNAME11,COLUMNNAME12,COLUMNNAME13,COLUMNNAME14,COLUMNNAME15,COLUMNNAME16,COLUMNNAME17,COLUMNNAME18,COLUMNNAME19,COLUMNNAME20,COLUMNNAME21,COLUMNNAME22,COLUMNNAME23,COLUMNNAME24,COLUMNNAME25,COLUMNNAME26,COLUMNNAME27,COLUMNNAME28,COLUMNNAME29,COLUMNNAME30,COLUMNNAME31,COLUMNNAME32,COLUMNNAME33,COLUMNNAME34,COLUMNNAME35,COLUMNNAME36,COLUMNNAME37,COLUMNNAME38,COLUMNNAME39,COLUMNNAME40,COLUMNNAME41,COLUMNNAME42,COLUMNNAME43,COLUMNNAME44,COLUMNNAME45,COLUMNNAME46,COLUMNNAME47,COLUMNNAME48,COLUMNNAME49,COLUMNNAME50,COLUMNNAME51,COLUMNNAME52,COLUMNNAME53,COLUMNNAME54,COLUMNNAME55,COLUMNNAME56,COLUMNNAME57,COLUMNNAME58,COLUMNNAME59,COLUMNNAME60,COLUMNNAME61,COLUMNNAME62,COLUMNNAME63,COLUMNNAME64,COLUMNNAME65
'''

        file.write(str1)



