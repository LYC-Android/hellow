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

// edited by John on 2016/11/30

// edited by John on 2016/12/8

// edited by John on 2016/12/9

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
const int fs = 8000;//采样�?
const int samptime = 8;//sample time
//const double Vpp = 2.91;//ADC full amplitude
const double Re = 0.0000440375371938658732;//ADC Resolution
const double gain = 1000;// circuit's gain

/******************Resample & FIR Filter****************************************/
//全变量要定义成常�?
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
//主函�?
JNIEXPORT void JNICALL Java_mrcheng_myapplication_MyThread_getStringFromNative

        (JNIEnv *env, jobject jObj,jshortArray jshortArray1,jdoubleArray jdoubleArray1)
{
    jshort* jshort1=(*env)->GetShortArrayElements(env,jshortArray1,NULL);
    jdouble* jdouble1=(*env)->GetDoubleArrayElements(env,jdoubleArray1,NULL);
    jsize myLength=(*env)->GetArrayLength(env,jdoubleArray1);

/*************************Sampling**********************************************/
    int samples = fs*samptime;

    emxArray_real_T *X;
    emxArray_real_T *Y;
    X = emxCreate_real_T(1, samples);
    Y = emxCreate_real_T(1, 65536);

    double *pTemp1 = (double*)malloc(sizeof(double)*samples);

    int i;
    for ( i = 0; i < myLength; i++) {
        pTemp1[i]=jshort1[i] *Re;
    }

/********************FIRfilter_BP***********************************************/
    int numBlocks3 = samples / blockSize3;//这里必须整除，否则画图出�?
    static double firState3[blockSize3 + 20 - 1];
    fir_instance S3;
    fir_init(&S3, bp_BL, (double*)bp_B, firState3, blockSize3);

    for (i = 0; i < numBlocks3; i++)
    {
        fir_filter(&S3, pTemp1 + (i * blockSize3), X->data + (i * blockSize3), blockSize3);
    }

    free(pTemp1);

/********************FM Demodulation********************************************/
    FMdemodHilber(X, (double)fc, (double)fs, (double)kf, Y);//FM解调函数
    emxDestroyArray_real_T(X);

    int length2 = Y->size[1];

/****************************IIR Filter*****************************************/
    for (i = 0; i < length2; i++)
    {
        Y->data[i] = iir_biquad(Y->data[i], pIIRCoeff);//Low pass filtered data with inverted over time
    }

/*****************************Resample******************************************/
    int fs2 = length2 / samptime*L / M;
    int samples2 = fs2*samptime;

    static double firState0[BLOCK_SIZE + 27 - 1];
    fir_instance S0;
    static double firState1[BLOCK_SIZE + 33 - 1];
    fir_instance S1;
    static double firState2[BLOCK_SIZE + 27 - 1];
    fir_instance S2;
    fir_init(&S0, re_BL0, (double*)re_B0, firState0, BLOCK_SIZE);
    fir_init(&S1, re_BL1, (double*)re_B1, firState1, BLOCK_SIZE);
    fir_init(&S2, re_BL2, (double*)re_B2, firState2, BLOCK_SIZE);

    double *pTemp2 = (double*)malloc(sizeof(double)*samples2);

    if (!resample8192to500(Y->data, pTemp2, length2, &S0, &S1, &S2))
    {
//        LOGE("Parameters' wrong! Check it...\n");
        exit(0);
    }
    emxDestroyArray_real_T(Y);

/**********************Savitzky-Golay Filter*************************************/

    struct matrix_instance instance1={ 1, samples2, pTemp2};//SG滤波器的初始�?
    struct matrix_instance instance2={ N, N, &Coeff1[0][0] };
    //sgFilter(&instance1, &instance2);

    /************************IIR notch filter****************************************/
    static double w[Delay + 1];
    for (i = 0; i <samples2; i++) {
        jdouble1[i] = Notch50Hz(pTemp2[i], w) / gain;
    }

    free(pTemp2);

    (*env)->ReleaseDoubleArrayElements(env,jdoubleArray1,jdouble1,0);
    (*env)->ReleaseShortArrayElements(env,jshortArray1,jshort1,0);


}
#ifdef __cplusplus

}

#endif

#endif
