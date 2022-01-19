import matplotlib.pyplot as plt

# #首先选定一个初始区间1，计算一阶差分最大值处作为qrs波,记录峰值，将峰值按一个比例2作为阈值，将超过该阈值的波均定义为qrs波。
# 初始区间长度
pri_length = 1000
# 认为的qrs波最长长度
qrs_length = 50
# qrs波峰值阈值
qrs_threshold_ratio = 0.5
# qrs波峰值,初始为0
qrs_max = 0
qrs_min = 0
qrs_threshold = 0
qrs_average=0

# 随后将两个qrs波的时间距离范围定义为标准rr间期1,获取初始区间内心电图的最高最低值，并定义一个距离的权重2，利用公式
# 分数=-距离权重*【（（当前时间-上次qrs时间）-标准rr间期）/标准】^2/(标准^2)+1+（心电强度-最低强度）/（最高强度-最低强度）
# 计算各个点的分数，分数高于初始分数*阈值3则认为是qrs波。
# 标准rr间期
rr_interval_standard = 0
# 间期权重
rr_weight = 0.4
# 上次qrs时间,初始为0
last_qrs_time = 0
# 分数阈值
score_threshold_ratio = 0.9
score_threshold = 0
have_buttom = False

# 一段区间长度1后更新qrs最小最大值，以学习率2更新标准rr间期
# qrs更新区间长度
interval_length = 500
# 标准rr间期学习率
learning_rate = 0.6

# 为了加速识别，设定rr间期的最小值,每次准确识别qrs波后跳过该长度
rr_min = round(rr_interval_standard / 10)


# 当多倍1标准qrs区间后


# 打开文件，返回float型list
def load_txt(name):
    f = open(name)
    data = []
    while 1:
        data_temp = f.readline()
        if len(data_temp) == 0 or not data_temp:
            break
        if (len(data_temp) > 2):
            data.append(float(data_temp))
    return data


# 绘制心电图
def draw_plot(linelist, pointlist=None, st=0, en=0):
    if en == 0:
        en = len(linelist)
    xlist = list(range(st, en))
    plt.plot(xlist, linelist[st:en])
    if pointlist != None:
        xlist = []
        ylist = []
        for p in pointlist:
            if p >= st and p <= en:
                xlist.append(p)
                ylist.append(linelist[p])
        plt.scatter(xlist, ylist, c='r')
    plt.show()


# 确定心电图峰值
def get_max_min(list):
    max1 = max2 = -10
    min1 = min2 = 10
    for l in list:
        if l < min2:
            if l < min1:
                min2 = min1
                min1 = l
            else:
                min2 = l
        if l > max2:
            if l > max1:
                max2 = max1
                max1 = l
            else:
                max2 = l
    return max2, min2


# 已知大致的qrs时间，确定qrs波准确时间
def get_qrs_time(starttime):
    d = data[starttime:starttime + qrs_length]
    return starttime + d.index(max(d))


# 计算某一时刻的分数
def get_score(time):
    # 分数=距离权重*{1-【（（当前时间-上次qrs时间）-标准rr间期）】^2/(标准^2)}+（心电强度-最低强度）/（最高强度-最低强度）
    rrtime = time - last_qrs_time
    rr_score = 1 - ((rrtime - rr_interval_standard) ** 2) / (rr_interval_standard ** 2)
    thre_score = (data[time] - qrs_min) / (qrs_max - qrs_min)
    # print(rr_score,thre_score,last_qrs_time,time,rr_interval_standard)
    return rr_score * rr_weight + thre_score, rr_score, thre_score


def check_buttom(time):
    global have_buttom
    if data[time]<(1 - qrs_threshold_ratio) * qrs_average + qrs_threshold_ratio * qrs_min:
        have_buttom=True


# 计算差分
def get_df(list):
    last = list[0]
    data = []
    for i in list[1:]:
        data.append(i - last)
        last = i
    return data


def sum_with_nan(list):
    sum = 0
    for l in list:
        if l is int:
            sum += l
    return sum


def get_max2_time(list):
    max1 = max2 = -10
    pos1 = pos2 = 0
    for i, l in enumerate(list):
        if l > max2:
            if l > max1:
                max2 = max1
                max1 = l
                pos2 = pos1
                pos1 = i
            else:
                max2 = l
                pos2 = i
    return pos2


# 初始rr间期标准值获取
def get_pri_rrtime(starttime):
    global qrs_threshold, rr_interval_standard, last_qrs_time, score_threshold, qrs_max, qrs_min, rr_min,qrs_average
    qrs_max, qrs_min = get_max_min(data[starttime:starttime + pri_length])
    ddata = get_df(data[starttime:starttime + pri_length])
    max_df_time = get_max2_time(ddata) + starttime
    qrs_time = get_qrs_time(max_df_time)
    qrs_average = sum_with_nan(data[starttime:starttime + pri_length]) / len(data[starttime:starttime + pri_length])
    qrs_threshold = (1 - qrs_threshold_ratio) * qrs_average + qrs_threshold_ratio * data[qrs_time]
    t = starttime
    rr_times = []
    rr_intervals = []
    scores = []
    m = min(len(data), starttime + pri_length)
    while t < m:
        if (data[t] >= qrs_threshold):
            rr_times.append(get_qrs_time(t))
            if (len(rr_times) != 1):
                rr_intervals.append(rr_times[-1] - rr_times[-2])
            t = rr_times[-1]+qrs_length
        t += 1
    if (len(rr_intervals) == 0):
        # print(starttime, qrs_threshold)
        return None, None
    rr_interval_standard = sum(rr_intervals) / len(rr_intervals)
    rr_min = round(rr_interval_standard / 15)
    for i, t in enumerate(rr_times):
        if i == 0:
            continue
        last_qrs_time = rr_times[i - 1]
        scores.append(get_score(rr_times[i])[0])
    score_threshold = (sum(scores) / len(scores)) * score_threshold_ratio

    return rr_intervals, rr_times


rr_intervals = []
rr_times = []
filename = 'cu02.txt'
data = load_txt(filename)
# 获取初始化数据
temp_intervals, temp_times = get_pri_rrtime(0)
rr_times.append(temp_times[0])

#draw_plot(data, temp_times, 126120, 127120)

# 程序开始
redraw_pos=[]

last_qrs_time = rr_times[0]
time = last_qrs_time + rr_min
last_update_time = time  # 更新qrs波最大最小值
while time < len(data):
    if have_buttom == False and time - last_qrs_time <= 3 * rr_interval_standard:
        check_buttom(time)
        time += 1
        continue
    score, dis_score, strenth_score = get_score(time)
    # print(time,last_qrs_time,rr_interval_standard)
    if (time - last_qrs_time > 3 * rr_interval_standard):
        redraw_pos.append(time)
        temp_intervals, temp_times = get_pri_rrtime(time)
        if temp_intervals == None:
            time += 10
            continue
        rr_times.append(temp_times[0])
        last_qrs_time = temp_times[0]
        time = last_qrs_time + rr_min
        last_update_time = time  # 更新qrs波最大最小值
        have_buttom = False
        continue
    if (score >= score_threshold):
        rr_times.append(get_qrs_time(time))
        temp_intervals = rr_times[-1] - rr_times[-2]
        rr_intervals.append(temp_intervals)
        rr_interval_standard = rr_interval_standard * (1 - learning_rate) + temp_intervals * learning_rate
        rr_min = max(round(rr_interval_standard / 15), 1)
        last_qrs_time = rr_times[-1]
        time = last_qrs_time + rr_min - 1
        have_buttom = False
    time += 1
    if (time > last_update_time + interval_length):
        qrs_max, qrs_min = get_max_min(data[last_update_time:time])
        qrs_average = sum_with_nan(data[last_update_time:time]) / len(data[last_update_time:time])
        last_update_time = time

draw_plot(data, rr_times, 105000, 110000)
draw_plot(data,redraw_pos, 0, 0)
print(rr_intervals)
