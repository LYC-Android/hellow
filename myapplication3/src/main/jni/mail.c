//
// Created by mr.cheng on 2016/8/5.
//
// edited by liu on 2016/9/6
//
// edited by liu on 2016/10/15
//
// edited by liu on 2016/11/23

// edited by John on 2016/11/24

// edited by John on 2016/11/25

// edited by John on 2016/11/26

//#include "mail.h"
#include <jni.h>
#include <android/log.h>
#include "FMdemodHilber_emxAPI.h"
#include "FMdemodHilber.h"
#include "IIRFilterCas.h"
#include "FIRFilter.h"
#include "Notch50Hz.h"
#include "Resample.h"
#include "sgFilter.h"

#ifndef LOG_TAG

#define LOG_TAG "ANDROID_LAB"

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#endif
#ifndef _Included_mrcheng_myapplication_MyThread
#define _Included_mrcheng_myapplication_MyThread

#ifdef __cplusplus
extern "C" {
#endif
/******************Modulation Parameters****************************************/
const int fc = 1000;//载波频率
const int kf = 300;//调频系数
const int fs = 8000;//采样率
const int samptime = 8;//sample time
const int m = 16;//2的幂，用于指定解调后的数据长度
const double Vpp = 2;//ADC full amplitude
const double gain = 1;//gain of circuit

/******************Resample & FIR Filter****************************************/
//全变量要定义成常量
const int M = 2048;//插值倍数和抽取倍数
const int L = 125;//总插值倍数

/**********************Savitzky-Golay Filter************************************/
extern double Coeff1[N][N];//SG滤波器系数表

/**********************IIR Filter************************************/
const double* pIIRCoeff[KCAS*KORD * 2] = {
        *(lp_NUM + 0) + 0, *(lp_DEN + 1) + 1, *(lp_DEN + 1) + 2, *(lp_NUM + 1) + 1,
        *(lp_NUM + 2) + 0, *(lp_DEN + 3) + 1, *(lp_DEN + 3) + 2, *(lp_NUM + 3) + 1,
        *(lp_NUM + 4) + 0, *(lp_DEN + 5) + 1, *(lp_DEN + 5) + 2, *(lp_NUM + 5) + 1,
        *(lp_NUM + 6) + 0, *(lp_DEN + 7) + 1, *(lp_DEN + 7) + 2, *(lp_NUM + 7) + 1,
};

/////////////////////////////////////////////////////////////
//主函数
JNIEXPORT void JNICALL Java_mrcheng_myapplication_MyThread_getStringFromNative

        (JNIEnv *env, jobject jObj,jshortArray jshortArray1,jdoubleArray jdoubleArray1)
{
    jshort* jshort1=(*env)->GetShortArrayElements(env,jshortArray1,NULL);
    jdouble* jdouble1=(*env)->GetDoubleArrayElements(env,jdoubleArray1,NULL);
    jsize myLength=(*env)->GetArrayLength(env,jdoubleArray1);

/*************************Sampling**********************************************/
    int samples = fs*samptime;//total points of this signal
    double Re = Vpp / (pow(2.0, m) - 1);//ADC Resolution

    emxArray_real_T *X;
    emxArray_real_T *Y;
    X = emxCreate_real_T(1, samples);
    Y = emxCreate_real_T(1, pow(2.0, m));//Y是解调后的数据结构

    int i;
    for ( i = 0; i < myLength; i++) {
        X->data[i]=jshort1[i]/32768.0;
    }
/********************FIRfilter_BP***********************************************/
    int numBlocks3 = samples / blockSize3;//这里必须整除，否则画图出错
    static double firState3[blockSize3 + 20 - 1];
    fir_instance S3;
    fir_init(&S3, bp_BL, (double*)bp_B, firState3, blockSize3);
    double *pTemp1 = (double*)malloc(sizeof(double)*samples);

    for (i = 0; i < numBlocks3; i++)
    {
        fir_filter(&S3, X-> data+ (i * blockSize3), pTemp1 + (i * blockSize3), blockSize3);
    }

    for (i = 0; i < X->size[1]; i++)//结构X中的成员size数组，第一个元素为矩阵空间的行数，第二个对应列数
    {
        X->data[i] = pTemp1[i];//将double型数据赋值给X结构中的double型数组
    }
    free(pTemp1);
/********************FM Demodulation********************************************/
    FMdemodHilber(X, (double)fc, (double)fs, (double)kf, Y);//FM解调函数
    emxDestroyArray_real_T(X);//释放矩阵内存

    int length2 = Y->size[1];//取得解调后数据的长度
/****************************IIR Filter*****************************************/
    double *pTemp2 = (double*)malloc(sizeof(double)*length2);

    for (i = 0; i < length2; i++)//解调后的低通滤波，同时按时间反转数据
    {
        pTemp2[i] = iir_biquad(Y->data[i], pIIRCoeff);//Lowpass filter
    }
    emxDestroyArray_real_T(Y);
/*****************************Resample******************************************/
    int fs2 = length2 / samptime*L / M;//转换为500Hz采样率
    int samples2 = fs2*samptime;

//每一级FIR滤波器的状态变量和结构体初始化
    static double firState0[BLOCK_SIZE + 27 - 1];//静态变量定义成全局变量
    fir_instance S0;
    static double firState1[BLOCK_SIZE + 33 - 1];
    fir_instance S1;
    static double firState2[BLOCK_SIZE + 27 - 1];
    fir_instance S2;
    fir_init(&S0, re_BL0, (double*)re_B0, firState0, BLOCK_SIZE);
    fir_init(&S1, re_BL1, (double*)re_B1, firState1, BLOCK_SIZE);
    fir_init(&S2, re_BL2, (double*)re_B2, firState2, BLOCK_SIZE);

    double *pTemp3 = (double*)malloc(sizeof(double)*samples2);

    if (!resample8192to500(pTemp2, pTemp3, length2, &S0, &S1, &S2))
    {
//        LOGE("Parameters' wrong! Check it...\n");
        exit(0);
    }
    free(pTemp2);
/*IIR notch filter***************************************************************/
    static double w[Delay + 1];//陷波器的状态变量
    for (i = 0; i < samples2; i++)
    {
        pTemp3[i] = Notch50Hz(pTemp3[i], w);//Notch Filter
    }

/**********************Savitzky-Golay Filter*************************************/

    struct matrix_instance instance1={ 1, samples2, pTemp3};//SG滤波器的初始化
    struct matrix_instance instance2={ N, N, &Coeff1[0][0] };
    sgFilter(&instance1, &instance2);
    int j = samples2-1;
    for (i = 0; i <samples2; i++) {//某些DSP流程节点上的乘除法计算有可能会导致计算溢出
        jdouble1[j--]=pTemp3[i]*32768.0*Re / gain;//datas inverted over time
//        if(i%100==0)
//            LOGE("%e\n",jdouble1[i]);
    }

    free(pTemp3);
    (*env)->ReleaseDoubleArrayElements(env,jdoubleArray1,jdouble1,0);
    (*env)->ReleaseShortArrayElements(env,jshortArray1,jshort1,0);


}
#ifdef __cplusplus

}

#endif

#endif
