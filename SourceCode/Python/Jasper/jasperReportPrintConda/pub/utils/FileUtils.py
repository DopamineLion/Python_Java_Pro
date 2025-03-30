import os
import shutil
from pathlib import Path


# 创建目录
def mkDir(path):
    if os.path.isdir(path):
        # print('文件夹已存在')
        pass
    else:
        os.makedirs(path)


# 判断文件夹是否存在,并创建
def mkDir2(folder):
    folder_Path = Path(folder)
    if not folder_Path.exists():
        folder_Path.mkdir(parents=True)


# 创建txt文件
def fileCreate(path, msg):  # path是指定文件路径，msg是写入的文件内容
    if os.path.isfile(path):
        # print('文件已存在')
        pass
    else:
        txt_file = open(path, 'w')
        txt_file.write(msg)


# 自定义报表的查询、导出
def updateImportFile(importFilePath, strImport):
    with open(importFilePath, 'w', encoding='UTF-8') as file:
        # 将文件指针移动到开始位置，以便覆盖原有内容
        file.seek(0)
        # 写入修改后的内容
        file.write(strImport)


# 复制文件
def copyPath(sourcesPath, tagPath):
    if os.path.isdir(sourcesPath):
        shutil.copytree(sourcesPath, tagPath)
    elif os.path.isfile(sourcesPath):
        shutil.copy(sourcesPath, tagPath)
    else:
        print('输入的地址有误')


class FileCopy:
    def copy_dir(self, dir1, dir2):
        dlist = os.listdir(dir1)
        if not os.path.exists(dir2):
            os.mkdir(dir2)
        for f in dlist:
            file1 = os.path.join(dir1, f)  # 源文件
            file2 = os.path.join(dir2, f)  # 目标文件
            if os.path.isfile(file1):
                shutil.copyfile(file1, file2)
            if os.path.isdir(file1):
                self.copy_dir(file1, file2)

# # 删除目录
# # 第一种
# import os
# os.rmdir('D:\\ss\\s') # 删除目录 如果该目录非空则不能删除
# import shutil
# # 第二种
# shutil.rmtree(path) # 删除目录 如果该目录非空也能删除

# 删除文件
# os.remove('D:\\ss\\s\\1.txt') # 删除文件
