from ttkbootstrap.dialogs import MessageDialog


def yesno(message, title=" ", alert=False, parent=None, **kwargs):
    dialog = MessageDialog(
        title=title,
        message=message,
        parent=parent,
        buttons=["否", "是:primary"],
        alert=alert,
        padding=(50, 50),
        # localize=True,
        **kwargs,
    )
    if "position" in kwargs:
        position = kwargs.pop("position")
    else:
        position = None
    dialog.show(position)
    return dialog.result


temp = yesno('导出成功!是否要打开导出的文件', '提示')
