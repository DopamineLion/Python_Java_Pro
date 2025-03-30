from openpyxl.styles import Alignment, Font, Side, Border


def set_alignment_font_border(cell, ali, fon):
    cell.alignment = Alignment(horizontal=ali, vertical=ali)
    # 创建一个字体对象并设置字体大小
    font = Font(size=fon, bold=True)  # 设置字体大小为20
    # 创建边框样式
    thin_border = Side(style="thin", color="000000")  # 定义边框样式和颜色
    border = Border(left=thin_border, right=thin_border, top=thin_border, bottom=thin_border)
    # 将字体应用到单元格A1
    cell.font = font
    cell.border = border
