import os
import pathlib
import warnings
from queue import Queue
from tkinter import messagebox
from tkinter.filedialog import askdirectory, askopenfilename, asksaveasfilename
import ttkbootstrap as ttk
from ttkbootstrap.constants import *
from ttkbootstrap.dialogs import MessageDialog

from MCNOrderDistribute import exportOrderDetailExcel, exportDistributeResult


# python - m ttkbootstrap

# 忽略警告信息
warnings.simplefilter('ignore')


class FileSearchEngine(ttk.Frame):

    queue = Queue()
    searching = False

    def __init__(self, master):
        super().__init__(master, padding=24)
        self.pack(fill=BOTH, expand=YES)

        # application variables
        _path = pathlib.Path().absolute().as_posix()
        self.order_path_var = ttk.StringVar(value="")
        self.inventory_path_var = ttk.StringVar(value="")
        self.save_var = ttk.StringVar(value='')

        # header and labelframe option container
        option_text = "For_MCN"
        self.option_lf = ttk.Labelframe(self, text=option_text, padding=55)
        self.option_lf.pack(fill=X, expand=YES, anchor=N)

        self.create_order_path_row()
        self.create_inventory_path_row()
        self.create_save_path_row()
        self.create_submit_one_btn()
        self.create_submit_all_btn()

    def create_order_path_row(self):
        """Add path row to labelframe"""
        path_row = ttk.Frame(self.option_lf)
        path_row.pack(fill=X, expand=YES)
        path_lbl = ttk.Label(path_row, text="订单Excel路径", width=13)
        path_lbl.pack(side=LEFT, padx=(15, 0))
        path_ent = ttk.Entry(path_row, textvariable=self.order_path_var, width=80)
        path_ent.pack(side=LEFT, fill=X, expand=YES, padx=5)
        browse_btn = ttk.Button(
            master=path_row,
            text="选择文件",
            command=self.on_browse_order,
            bootstyle=OUTLINE,
            width=8
        )
        browse_btn.pack(side=LEFT, padx=5)

    def on_browse_order(self):
        order_path = askopenfilename(
            title="选择订单Excel文件",
            defaultextension=".xlsx",
            filetypes=[("Excel files", "*.xlsx")]
        )
        if not order_path:
            return
        with open(order_path, encoding='utf-8') as f:
            self.order_path_var.set(order_path)

    def create_save_path_row(self):
        """Add path row to labelframe"""
        path_row = ttk.Frame(self.option_lf)
        path_row.pack(fill=X, expand=YES)
        path_lbl = ttk.Label(path_row, text="保存结果路径", width=13)
        path_lbl.pack(side=LEFT, padx=(15, 0))
        path_ent = ttk.Entry(path_row, textvariable=self.save_var, width=80)
        path_ent.pack(side=LEFT, fill=X, expand=YES, padx=5)
        browse_btn = ttk.Button(
            master=path_row,
            text="选择路径",
            command=self.on_browse_save,
            bootstyle=OUTLINE,
            width=8
        )
        browse_btn.pack(side=LEFT, padx=5)

    def on_browse_save(self):
        save_path = asksaveasfilename(
            title="选择保存路径",
            defaultextension=".xlsx",
            filetypes=[("Excel files", "*.xlsx")]
        )
        self.save_var.set(save_path)

    def create_inventory_path_row(self):
        """Add path row to labelframe"""
        path_row = ttk.Frame(self.option_lf)
        path_row.pack(fill=X, expand=YES, pady=15)
        path_lbl = ttk.Label(path_row, text="库存Excel路径", width=13)
        path_lbl.pack(side=LEFT, padx=(15, 0))
        path_ent = ttk.Entry(path_row, textvariable=self.inventory_path_var, width=80)
        path_ent.pack(side=LEFT, fill=X, expand=YES, padx=5)
        browse_btn = ttk.Button(
            master=path_row,
            text="选择文件",
            command=self.on_inventory_order,
            bootstyle=OUTLINE,
            width=8
        )
        browse_btn.pack(side=LEFT, padx=5)

    def on_inventory_order(self):
        order_path = askopenfilename(
            title="选择库存Excel文件",
            defaultextension=".xlsx",
            filetypes=[("Excel files", "*.xlsx")]
        )
        if not order_path:
            return
        with open(order_path, encoding='utf-8') as f:
            self.inventory_path_var.set(order_path)

    def create_submit_one_btn(self):
        path_row = ttk.Frame(self.option_lf)
        path_row.pack(fill=X, expand=YES, pady=35)
        browse_btn = ttk.Button(
            master=path_row,
            text="步骤1(合并订单详情表)",
            command=self.on_submit_one,
            bootstyle=SUCCESS,
            width=20
        )
        browse_btn.pack(side=LEFT, padx=5)
        browse_btn = ttk.Button(
            master=path_row,
            text="步骤2(分配库存)",
            command=self.on_submit_two,
            bootstyle=SUCCESS,
            width=20
        )
        browse_btn.pack(side=LEFT, padx=5)

    def on_submit_one(self):
        # 合并订单明细，汇总订单项目号等信息
        tag = exportOrderDetailExcel(self.order_path_var.get(), self.save_var.get())
        if tag:
            # messagebox.showinfo("提示", "导出成功")
            temp = self.yesno('导出成功!是否要打开导出的文件', '提示')
            if temp == '是':
                os.system(self.save_var.get())
        else:
            messagebox.showinfo("提示", "导出失败")

    def on_submit_two(self):
        # 导出分配明细
        tag = exportDistributeResult(self.inventory_path_var.get(), self.save_var.get())
        if tag:
            # messagebox.showinfo("提示", "导出成功")
            temp = self.yesno('导出成功!是否要打开导出的文件', '提示')
            if temp == '是':
                os.system(self.save_var.get())
        else:
            messagebox.showinfo("提示", "导出失败")

    def create_submit_all_btn(self):
        path_row = ttk.Frame(self.option_lf)
        path_row.pack(fill=X, expand=YES, pady=5)
        browse_btn = ttk.Button(
            master=path_row,
            text="全部导出",
            command=self.on_submit_all,
            bootstyle=INFO,
            width=20
        )
        browse_btn.pack(side=LEFT, padx=5)

    def on_submit_all(self):
        # 合并订单明细，汇总订单项目号等信息
        tag = exportOrderDetailExcel(self.order_path_var.get(), self.save_var.get())
        if tag:
            # 导出分配明细
            tag = exportDistributeResult(self.inventory_path_var.get(), self.save_var.get())
        if tag:
            # messagebox.showinfo("提示", "导出成功")
            temp = self.yesno('导出成功!是否要打开导出的文件', '提示')
            if temp == '是':
                os.system(self.save_var.get())
        else:
            messagebox.showinfo("提示", "导出失败")


    @staticmethod
    def yesno(message, title=" ", alert=False, parent=None, **kwargs):
        dialog = MessageDialog(
            title=title,
            message=message,
            parent=parent,
            padding=(50, 50),
            buttons=["否", "是:primary"],
            alert=alert,
            localize=True,
            **kwargs,
        )
        if "position" in kwargs:
            position = kwargs.pop("position")
        else:
            position = None
        dialog.show(position)
        return dialog.result


if __name__ == '__main__':
    app = ttk.Window("MCN分配库存Project", "journal")
    FileSearchEngine(app)
    app.mainloop()
