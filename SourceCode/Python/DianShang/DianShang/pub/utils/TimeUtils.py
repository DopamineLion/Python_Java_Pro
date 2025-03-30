import pendulum

from pub.utils.ReUtils import is_date


def diffBothTime(startTimeStr, endTimeStr):
    if startTimeStr == '' or startTimeStr is None or not is_date(startTimeStr):
        return ''
    if endTimeStr == '' or endTimeStr is None or not is_date(endTimeStr):
        return ''
    dt = pendulum.parse(startTimeStr, tz='Asia/Shanghai')
    dtNow = pendulum.parse(endTimeStr, tz='Asia/Shanghai')
    diffDayBoth = dtNow.diff(dt)
    diffDay = diffDayBoth.in_days()
    diffDay = int(diffDay) + 1
    return diffDay






def diffNowTime(startTimeStr):
    dt = pendulum.parse(startTimeStr, tz='Asia/Shanghai')
    dtNow = pendulum.now(tz='Asia/Shanghai')
    diffTime = dtNow.diff(dt)
    minutesDiff = diffTime.in_minutes()
    return minutesDiff

def nowTime():
    nowTime = pendulum.now()
    nowTime = nowTime.strftime('%Y-%m-%d %H时%M')
    # nowTime = re.sub(r"[\n\t\r:]", "时", nowTime)
    return nowTime


def nowTimeSecond():
    nowTime = pendulum.now()
    nowTime = nowTime.strftime('%Y-%m-%d %H时%M分%S')
    # nowTime = re.sub(r"[\n\t\r:]", "时", nowTime)
    return nowTime


def nowDate():
    nowDate = pendulum.now()
    nowDate = nowDate.strftime('%m月%d日')
    return nowDate
