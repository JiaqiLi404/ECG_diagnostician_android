import pandas as pd 
from pandas  import DataFrame
import os
import pickle
from pandas  import DataFrame
import numpy as np
import wfdb
import xlsxwriter
from wfdb import processing
path = []
path.append(r'E:\大三项目数据\BIDMC充血性心力衰竭数据库\files')
path.append(r'E:\大三项目数据\恶性心室异常数据库\mit-bih-malignant-ventricular-ectopy-database-1.0.0\mit-bih-malignant-ventricular-ectopy-database-1.0.0')
path.append(r'E:\大三项目数据\房颤(含长期与阵发)\长期房颤\long-term-af-database-1.0.0\files')
path.append(r'E:\大三项目数据\房颤(含长期与阵发)\阵发性房颤')
path.append(r'E:\大三项目数据\室上性心律失常\mit-bih-supraventricular-arrhythmia-database-1.0.0\mit-bih-supraventricular-arrhythmia-database-1.0.0')
path.append(r'E:\大三项目数据\室速性心律失常（持续性室性心动过速，心室扑动和心室颤动）\cu-ventricular-tachyarrhythmia-database-1.0.0\cu-ventricular-tachyarrhythmia-database-1.0.0')
path.append(r"E:\大三项目数据\PAF预测挑战数据库\paf-prediction-challenge-database-1.0.0\paf-prediction-challenge-database-1.0.0")
path.append(r"E:\大三项目数据\房颤(含长期与阵发)\阵发性房颤\old")
path.append(r'E:\大三项目数据\突发性心脏骤停\sudden-cardiac-death-holter-database-1.0.0\sudden-cardiac-death-holter-database-1.0.0')

path.append(r'E:\大三项目数据\txt文件')
def GetString(path):#将文件的序号都抽取出来
    files = os.listdir(path)
    string = [f[:-4] for f in files if f.endswith('dat')]
    return string

#下面利用RR间期数据计算出RR间期坐标
path_RR_data = r'E:\大三项目数据\txt文件\RR间期数据'
dirname = os.path.dirname(path_RR_data)
listd = os.listdir(dirname)#在第0个以及第1个之间进行操作
path_RR_loction = dirname+'\\'+listd[0]    

name_loc = os.listdir(path_RR_loction)
#读取注释
name_data = os.listdir(path_RR_data)
t = [6,4,5,1,2,3]#path[t[i]]就是所有文件
save_path = r"E:\大三项目数据\txt文件\RR间期对应的注释"
npsym = np.array([])
npRR = np.array([])
for  i in  range(4,5):
    full_p_data = path_RR_data+"\\"+name_data[i]
    full_p_loc = path_RR_loction+"\\"+name_loc[i]
    loca_name = os.listdir(full_p_loc)
    # print(full_p_data)
    # print(path[t[i]]) 
    for j in range(39,len(loca_name)):#len(loca_name)
        #把名字利用.分割取了出来
        name = loca_name[j].split(".")[0]
        path_name = path[t[i]]+"\\"+name  
        sym = np.array([])  
        
        if i == 4:
            file_loc = np.loadtxt(full_p_loc+"\\"+name+".txt",delimiter="\n").astype(int)
            signal_annotation1 = wfdb.rdann(path_name, "atr", sampfrom=0)
            signal_annotation2 = wfdb.rdann(path_name, "qrs", sampfrom=0)
            sy1_1 = np.array(signal_annotation1.symbol)#对应的sym
            sy1_2 = np.array(signal_annotation1.sample)#loc
            sy2_1 = np.array(signal_annotation2.symbol)#对应的sym
            sy2_2 = np.array(signal_annotation2.sample)#loc
            print(1)
            #将sym对应的坐标换成RR坐标的
            
            for q  in  range(len(sy1_2)):
                min_array = abs(np.subtract(file_loc,sy1_2[q]))
                min_loc = np.where(min_array == min(min_array))
                sy1_2[q] = file_loc[min_loc[0][0]]
                print("len sy22",len(sy2_2),"len sy12",len(sy1_2))
                print("q =",q)
            # #将重复的数据变成0
            # loc,counts= np.unique(sy1_2,return_counts=True) 
            # ind = np.where(counts>1)
            # repeat_data = loc[ind[0]]#重复的数据
            # for q in range(len(repeat_data)):
            #     repeat_loc = np.where(sy1_2==repeat_data[q])#重复的数据坐标
            #     sy1_2[repeat_loc[0][1:]] = 0
            #     print("q =",q)
            # print(2)

            #将sym对应的坐标换成RR坐标的
            
            for q  in  range(len(sy2_2)):
                min_array = abs(np.subtract(file_loc,sy2_2[q]))
                min_loc = np.where(min_array == min(min_array))
                sy2_2[q] = file_loc[min_loc[0][0]]
                print("p =",q)
                print("len sy22",len(sy2_2),"len sy12",len(sy1_2))
            #将重复的数据变成0
            # loc,counts= np.unique(sy2_2,return_counts=True) 
            # ind = np.where(counts>1)
            # repeat_data = loc[ind[0]]#重复的数据
            # print(3)
            # for q in range(len(repeat_data)):
            #     repeat_loc = np.where(sy2_2==repeat_data[q])#重复的数据坐标
            #     sy2_2[repeat_loc[0][1:]] = 0
            for k in range(len(file_loc)):#len(file_loc)
                
                l1 = np.where(sy1_2 == file_loc[k])
                l2 = np.where(sy2_2 == file_loc[k])
                print("k",k)
                if len(l1[0]) == 0 and len(l2[0]) == 0 :
                    npsym = np.append(npsym,0)
                   
                elif len(l1[0]) == 0 :
                    npsym = np.append(npsym,"$".join(sy2_1[l2]))
                     
                elif len(l2[0]) == 0:
                    npsym = np.append(npsym,"$".join(sy1_1[l1]))
                   
                else:
                    npsym = np.append(npsym,"$".join(sy2_1[l2][0])+'$'+"$".join(sy1_1[l1][0]))
            print("ok")      
            # np.savetxt(save_path+"\\长期房颤\\"+name+"npsym"+".txt",npsym,fmt = "%s")
            pf_end_loc = np.where(npsym == "T")
            pf_wy_loc = np.where(npsym == "|")
            for r in range(len(pf_end_loc[0])):
                npsym[pf_end_loc[0][r]] += "$T_end"
            for r in range(len(pf_wy_loc[0])):
                npsym[pf_wy_loc[0][r]] += "$wy"
            print(5)
            np.savetxt(save_path+"\\长期房颤\\"+name+".txt",npsym,fmt = "%s")
            print(len(npsym) , len(file_loc))   
            npsym = np.array([])
        
        # if i == 5:
        #     file_loc = np.loadtxt(full_p_loc+"\\"+name+".txt",delimiter="\n").astype(int)
        #     signal_annotation1 = wfdb.rdann(path_name, "atr", sampfrom=0)
        #     signal_annotation2 = wfdb.rdann(path_name, "qrs", sampfrom=0)
        #     sy1_1 = np.array(signal_annotation1.symbol)#对应的sym
        #     sy1_2 = np.array(signal_annotation1.sample)#loc
        #     sy2_1 = np.array(signal_annotation2.symbol)#对应的sym
        #     sy2_2 = np.array(signal_annotation2.sample)#loc
        #     print(1)
        #     #将sym对应的坐标换成RR坐标的
        #     for q  in  range(len(sy1_2)):
        #         min_array = abs(np.subtract(file_loc,sy1_2[q]))
        #         min_loc = np.where(min_array == min(min_array))
        #         sy1_2[q] = file_loc[min_loc[0][0]]
        #     #将重复的数据变成0
        #     loc,counts= np.unique(sy1_2,return_counts=True) 
        #     ind = np.where(counts>1)
        #     repeat_data = loc[ind[0]]#重复的数据
        #     for q in range(len(repeat_data)):
        #         repeat_loc = np.where(sy1_2==repeat_data[q])#重复的数据坐标
        #         sy1_2[repeat_loc[0][1:]] = 0
        #         print("q =",q,i)

        #     print(2)
        #     #将sym对应的坐标换成RR坐标的
        #     for q  in  range(len(sy2_2)):
        #         min_array = abs(np.subtract(file_loc,sy2_2[q]))
        #         min_loc = np.where(min_array == min(min_array))
        #         sy2_2[q] = file_loc[min_loc[0][0]]
        #         print("q =",q,i)
        #     #将重复的数据变成0
        #     loc,counts= np.unique(sy2_2,return_counts=True) 
        #     ind = np.where(counts>1)
        #     repeat_data = loc[ind[0]]#重复的数据
            
        #     for q in range(len(repeat_data)):
        #         repeat_loc = np.where(sy2_2==repeat_data[q])#重复的数据坐标
        #         sy2_2[repeat_loc[0][1:]] = 0
        #         print("q =",q,i)
        #     for k in range(len(file_loc)):#len(file_loc)
        #         l1 = np.where(sy1_2 == file_loc[k])
        #         l2 = np.where(sy2_2 == file_loc[k])
        #         print(k)
        #         if len(l1[0]) == 0 and len(l2[0]) == 0 :
        #             npsym = np.append(npsym,0)
                   
        #         elif len(l1[0]) == 0 :
        #             npsym = np.append(npsym,sy2_1[l2])
                    
        #         elif len(l2[0]) == 0:
        #             npsym = np.append(npsym,sy1_1[l1])
                   
        #         else:
        #             npsym = np.append(npsym,sy2_1[l2][0]+'$'+sy1_1[l1][0])
                    

        #     np.savetxt(save_path+"\\阵发性房颤\\"+name+".txt",npsym,fmt = "%s")
        #     print(len(npsym) , len(file_loc))   
        #     npsym = np.array([])
            
        



       