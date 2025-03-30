import pendulum


def diffNowTime(startTimeStr):
    dt = pendulum.parse(startTimeStr, tz='Asia/Shanghai')
    dtNow = pendulum.now(tz='Asia/Shanghai')
    diffTime = dtNow.diff(dt)
    minutesDiff = diffTime.in_minutes()
    return minutesDiff


def nowTimeSecond():
    nowTime = pendulum.now()
    nowTime = nowTime.strftime('%Y-%m-%d %H时%M分%S')
    # nowTime = re.sub(r"[\n\t\r:]", "时", nowTime)
    return nowTime


def nowTime():
    nowTime = pendulum.now()
    nowTime = nowTime.strftime('%Y-%m-%d %H时%M分%S')
    # nowTime = re.sub(r"[\n\t\r:]", "时", nowTime)
    return nowTime



def nowDate():
    nowDate = pendulum.now()
    nowDate = nowDate.strftime('%m月%d日')
    return nowDate
