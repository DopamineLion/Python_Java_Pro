from tkinter import *
import tkinter
import warnings
from pathlib import Path
from typing import Optional
import tkinter as tk
from tkinter import filedialog, messagebox, LEFT, RIGHT
from MCNOrderDistribute import exportOrderDetailExcel, exportDistributeResult

# 忽略警告信息
warnings.simplefilter('ignore')

order_file = None
right_file = None
export_file = None


# 选择订单文件
def select_file() -> Optional[Path]:
    """打开文件选择对话框，让用户选择Excel文件，并在tkinter界面中显示其路径"""
    file_path = filedialog.askopenfilename(
        title="选择订单Excel文件",
        filetypes=[("Excel files", "*.xlsx")]
        # filetypes=[("Excel files", "*.xlsx *.xls")]
    )
    if file_path:
        # 在界面上显示文件路径
        select_path_label.config(text=f"订单文件路径: {file_path}")
    else:
        # 如果用户未选择文件，则显示提示信息
        messagebox.showwarning("警告", "未选择任何文件")
    global order_file
    order_file = file_path


# 选择库存文件
def select_right_file():
    """打开文件选择对话框，让用户选择Excel文件，并在tkinter界面中显示其路径"""
    file_path = filedialog.askopenfilename(
        title="选择库存Excel文件",
        filetypes=[("Excel files", "*.xlsx")]
        # filetypes=[("Excel files", "*.xlsx *.xls")]
    )
    if file_path:
        # 在界面上显示文件路径
        select_right_path_label.config(text=f"库存文件路径: {file_path}")
    else:
        # 如果用户未选择文件，则显示提示信息
        messagebox.showwarning("警告", "未选择任何文件")
    global right_file
    right_file = file_path


# 保存输出结果文件
def save_file():
    filepath = filedialog.asksaveasfilename(
        title="选择保存文件路径",
        defaultextension=".txt",
        filetypes=[("Excel files", "*.xlsx")]
    )
    if filepath:
        # 在界面上显示文件路径
        save_path_label.config(text=f"保存文件路径: {filepath}")
    else:
        # 如果用户未选择文件，则显示提示信息
        messagebox.showwarning("警告", "未选择保存路径")
    selected_path = Path(filepath)
    global export_file
    export_file = filepath
    return selected_path


# 执行一条龙
def submit(type):
    if order_file:
        if right_file:
            if export_file:
                # # 合并订单明细，汇总订单项目号等信息
                # tag = exportOrderDetailExcel(order_file, export_file)
                # if tag:
                #     # 导出分配明细
                #     res = exportDistributeResult(right_file, export_file)
                #     if res:
                #         submit_label.config(text=f"成功")
                #         messagebox.showinfo("提示", "导出成功")
                #     else:
                #         submit_label.config(text=f"失败")
                #         messagebox.showinfo("提示", "导出失败")
                # else:
                #     messagebox.showinfo("提示", "修改Excel并保存后重新执行即可")
                tag = False
                if type == 1:
                    # 合并订单明细，汇总订单项目号等信息
                    tag = exportOrderDetailExcel(order_file, export_file)
                if type == 2:
                    # 导出分配明细
                    tag = exportDistributeResult(right_file, export_file)
                if type == 3:
                    # 合并订单明细，汇总订单项目号等信息
                    tag = exportOrderDetailExcel(order_file, export_file)
                    if tag:
                        # 导出分配明细
                        tag = exportDistributeResult(right_file, export_file)
                if tag:
                    submit_label.config(text=f"成功")
                    messagebox.showinfo("提示", "导出成功")
                else:
                    submit_label.config(text=f"失败")
                    messagebox.showinfo("提示", "导出失败")
            else:
                messagebox.showwarning("警告", "未选择保存路径")
        else:
            messagebox.showwarning("警告", "未选择库存文件")
    else:
        messagebox.showwarning("警告", "未选择订单文件")


if __name__ == "__main__":
    root = tkinter.Tk()

    group = tkinter.LabelFrame(root, text='最好的脚本语言是？', padx=5, pady=5)
    group.pack(padx=10, pady=10)
    root.title('MCN分配订单Demo')
    root.geometry("800x800")  # 设置窗口大小

    # 选择订单表
    select_path_label = tk.Label(root, text="", font=("Helvetica", 12), width=300)
    select_path_label.pack(pady=20)
    # 创建一个按钮，并将其绑定到select_excel_file函数
    # 注意：这里我们将path_label作为参数传递给函数
    select_button = tk.Button(root, text="选择订单Excel", width=13, height=2,
                              command=lambda: select_file())
    select_button.pack(pady=7)

    # 选择库存表
    select_right_path_label = tk.Label(root, text="", font=("Helvetica", 12))
    select_right_path_label.pack(pady=20)
    select_right_button = tk.Button(root, text="选择库存Excel", width=13, height=2,
                                    command=lambda: select_right_file())
    select_right_button.pack(pady=7)

    # 保存
    save_path_label = tk.Label(root, text="", font=("Helvetica", 12))
    save_path_label.pack(pady=7)
    save_button = tk.Button(root, text="Excel保存位置", width=13, height=2,
                            command=lambda: save_file())
    save_button.pack(pady=7)

    # 执行
    submit_label = tk.Label(root, text="", font=("Helvetica", 12))
    submit_label.pack(pady=7)
    submit_button = tk.Button(root, text="1", width=13, height=2,
                              command=lambda: submit(1))
    submit_button.pack(pady=7)
    submit_button = tk.Button(root, text="2", width=13, height=2,
                              command=lambda: submit(2))
    submit_button.pack(pady=7)
    submit_button = tk.Button(root, text="1+2", width=13, height=2,
                              command=lambda: submit(3))
    submit_button.pack(pady=7)

    # 5. 显示窗口并进入GUI事件循环
    root.mainloop()

    input('回车退出程序')
