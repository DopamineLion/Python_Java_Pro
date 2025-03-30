import re


def is_date(string):
    # 匹配格式 YYYY-MM-DD
    pattern = r'^\d{4}-\d{2}-\d{2}$'
    return bool(re.match(pattern, string))
