import tkinter
from pathlib import Path
from typing import Optional
import tkinter as tk
from tkinter import ttk
from tkinter import filedialog, messagebox, scrolledtext
from jasperReport import exportReport

import_file = None
export_path = None

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
    global import_file
    import_file = file_path


# 保存输出结果文件
def save_file():
    filepath = filedialog.askdirectory(
        title="选择保存文件路径"
    )
    if filepath:
        # 在界面上显示文件路径
        save_path_label.config(text=f"保存文件路径: {filepath}")
    else:
        # 如果用户未选择文件，则显示提示信息
        messagebox.showwarning("警告", "未选择保存路径")
    selected_path = Path(filepath)
    global export_path
    export_path = filepath
    return selected_path


# 执行
def submit():
    if import_file:
        if export_path:
            select_combobox = combo.get()
            select_combobox_index = None
            if select_combobox == '拣货签80X60':
                select_combobox_index = '1'
            elif select_combobox == '外箱签100X100':
                select_combobox_index = '2'
            exportReport(select_combobox_index, import_file, export_path)
            submit_label.config(text=f"成功")
            messagebox.showinfo("提示", "导出成功")
        else:
            messagebox.showwarning("警告", "未选择保存路径")
    else:
        messagebox.showwarning("警告", "未选择数据源文件")


# def get_value():
#     selected_value = combobox.get()
#     return selected_value
    # print(f"选中的值是: {selected_value}")


# def year_changed(event):
#     messagebox.showinfo(title='结果', message=f'你选择了 {selected_year.get()}!')

if __name__ == "__main__":
    root = tkinter.Tk()
    root.title('电商补打100X100外箱签或80X60的拣货签')
    root.geometry("800x600")  # 设置窗口大小

    label = tk.Label(root, text="请点击下拉框选择：")
    label.pack()
    selected_year = tk.StringVar()
    combo = ttk.Combobox(root)
    combo['values'] = ['拣货签80X60', '外箱签100X100']
    combo['state'] = 'readonly'
    combo.current(0)  # 设置默认选中的项，索引从0开始计数
    combo.pack(pady=10)


    # label = tk.Label(text="请选择年份：")
    # label.pack(fill=tk.X, padx=5, pady=5)
    # selected_year = tk.StringVar()
    # combobox1 = ttk.Combobox(root, textvariable=selected_year)
    # combobox1.pack(padx=5, pady=5)
    # combobox1.bind('<<ComboboxSelected>>', year_changed)

    # 选择数据源表
    select_path_label = tk.Label(root, text="", font=("Helvetica", 12), width=300)
    select_path_label.pack(pady=20)
    select_button = tk.Button(root, text="选择数据源Excel", width=15, height=2,
                              command=lambda: select_file())
    select_button.pack(pady=10)

    # 保存
    save_path_label = tk.Label(root, text="", font=("Helvetica", 12))
    save_path_label.pack(pady=20)
    save_button = tk.Button(root, text="Excel保存位置", width=13, height=2,
                            command=lambda: save_file())
    save_button.pack(pady=10)

    # 执行
    submit_label = tk.Label(root, text="", font=("Helvetica", 12))
    submit_label.pack(pady=20)
    submit_button = tk.Button(root, text="执行", width=13, height=2,
                              command=lambda: submit())
    submit_button.pack(pady=10)

    # 5. 显示窗口并进入GUI事件循环
    root.mainloop()
