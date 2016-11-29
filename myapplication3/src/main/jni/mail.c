//
// Created by mr.cheng on 2016/8/5.
//
// edited by liu on 2016/9/6
//
// edited by liu on 2016/10/15
//
#include "mail.h"
#include <jni.h>
#include "FMdemodHilber.h"
#include <android/log.h>
#include "FMdemodHilber_emxAPI.h"
//#include "fdacoefs.h"
#include "IIRFilterCas.h"
#include "FIRFilter.h"
#include "Decimate.h"
#include "sgFilter.h"
//调用c的dll或者lib（我也看不懂）
#ifndef LOG_TAG

#define LOG_TAG "ANDROID_LAB"

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#endif
#ifndef _Included_mrcheng_myapplication_MyThread

#define _Included_mrcheng_myapplication_MyThread
#ifdef __cplusplus

extern "C" {

#endif

const double* pIIRCoeff[KCAS*KORD * 2] = {     //IIR 滤波器系数
        *(NUM + 0) + 0, *(DEN + 1) + 1, *(DEN + 1) + 2, *(NUM + 1) + 1,
        *(NUM + 2) + 0, *(DEN + 3) + 1, *(DEN + 3) + 2, *(NUM + 3) + 1,
        *(NUM + 4) + 0, *(DEN + 5) + 1, *(DEN + 5) + 2, *(NUM + 5) + 1,
        *(NUM + 6) + 0, *(DEN + 7) + 1, *(DEN + 7) + 2, *(NUM + 7) + 1,
};
/////////////////////////////////////////////////////////////////////////////////////

JNIEXPORT void JNICALL Java_mrcheng_myapplication_MyThread_getStringFromNative

        (JNIEnv *env, jobject jObj,jshortArray jshortArray1,jdoubleArray jdoubleArray1){
    jshort* jshort1=(*env)->GetShortArrayElements(env,jshortArray1,NULL);
    jdouble* jdouble1=(*env)->GetDoubleArrayElements(env,jdoubleArray1,NULL);
    jsize myLength=(*env)->GetArrayLength(env,jdoubleArray1);

    int fc=1000;
    int fs=8000;
    int kf=300;   //调频系数
    emxArray_real_T *X;
    emxArray_real_T *Y;
    int length1=64000;
    int length2=65536;
    int samptime = 8;//sample time
    int samples = fs*samptime;//total points of this signal
    double samptime2 = (double)samptime;
    double fs3 = length2 / samptime2;
    int factor=8;
    double fs4 = fs3 / factor;
    double samples2 = fs4*samptime2;
////////////////////FIRFilter_BP///////////////////////////////////////////////////////
#define BLOCK_SIZE3 1000
    int blockSize3 = BLOCK_SIZE3;
    static double firState3[BLOCK_SIZE3 + 20 - 1];
    int numBlocks3 = length1 / BLOCK_SIZE3;//这里必须整除，否则画图出错
    fir_instance S3;
///////////////////////Decimate////////////////////////////////
#define BLOCK_SIZE 1024
    int m=16;
    int numBlocks = pow(2.0, m) / BLOCK_SIZE;
    int blockSize=BLOCK_SIZE;
    int M = 8;//decimation factor
    static  double decimateState[BLOCK_SIZE + 11 - 1];//state buffer
    decimate_instance dS;//Instance structure
/////////////////////////Savitzky-Golay Filter/////////////////////////////
    const int d = 1;
    const int N = 15;
    double Coeff1[15][15] = {
            0.241666666666667, 0.216666666666667, 0.191666666666667, 0.166666666666667, 0.141666666666667, 0.116666666666667, 0.0916666666666667, 0.0666666666666667, 0.0416666666666667, 0.0166666666666667, -0.00833333333333333, -0.0333333333333333, -0.0583333333333333, -0.0833333333333333, -0.108333333333333,
            0.216666666666667, 0.195238095238095, 0.173809523809524, 0.152380952380952, 0.130952380952381, 0.10952380952381, 0.0880952380952381, 0.0666666666666667, 0.0452380952380952, 0.0238095238095238, 0.00238095238095239, -0.0190476190476191, -0.0404761904761905, -0.0619047619047619, -0.0833333333333333,
            0.191666666666667, 0.173809523809524, 0.155952380952381, 0.138095238095238, 0.120238095238095, 0.102380952380952, 0.0845238095238095, 0.0666666666666667, 0.0488095238095238, 0.030952380952381, 0.0130952380952381, -0.00476190476190476, -0.0226190476190476, -0.0404761904761905, -0.0583333333333333,
            0.166666666666667, 0.152380952380952, 0.138095238095238, 0.123809523809524, 0.10952380952381, 0.0952380952380952, 0.080952380952381, 0.0666666666666667, 0.0523809523809524, 0.0380952380952381, 0.0238095238095238, 0.00952380952380953, -0.00476190476190476, -0.0190476190476191, -0.0333333333333333,
            0.141666666666667, 0.130952380952381, 0.120238095238095, 0.10952380952381, 0.0988095238095238, 0.0880952380952381, 0.0773809523809524, 0.0666666666666667, 0.055952380952381, 0.0452380952380952, 0.0345238095238095, 0.0238095238095238, 0.0130952380952381, 0.00238095238095239, -0.00833333333333333,
            0.116666666666667, 0.10952380952381, 0.102380952380952, 0.0952380952380952, 0.0880952380952381, 0.080952380952381, 0.0738095238095238, 0.0666666666666667, 0.0595238095238095, 0.0523809523809524, 0.0452380952380952, 0.0380952380952381, 0.030952380952381, 0.0238095238095238, 0.0166666666666667,
            0.0916666666666667, 0.0880952380952381, 0.0845238095238095, 0.080952380952381, 0.0773809523809524, 0.0738095238095238, 0.0702380952380952, 0.0666666666666667, 0.0630952380952381, 0.0595238095238095, 0.055952380952381, 0.0523809523809524, 0.0488095238095238, 0.0452380952380952, 0.0416666666666667,
            0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667,
            0.0416666666666667, 0.0452380952380952, 0.0488095238095238, 0.0523809523809524, 0.055952380952381, 0.0595238095238095, 0.0630952380952381, 0.0666666666666667, 0.0702380952380952, 0.0738095238095238, 0.0773809523809524, 0.080952380952381, 0.0845238095238095, 0.0880952380952381, 0.0916666666666667,
            0.0166666666666667, 0.0238095238095238, 0.030952380952381, 0.0380952380952381, 0.0452380952380952, 0.0523809523809524, 0.0595238095238095, 0.0666666666666667, 0.0738095238095238, 0.080952380952381, 0.0880952380952381, 0.0952380952380952, 0.102380952380952, 0.10952380952381, 0.116666666666667,
            -0.00833333333333333, 0.00238095238095239, 0.0130952380952381, 0.0238095238095238, 0.0345238095238095, 0.0452380952380952, 0.055952380952381, 0.0666666666666667, 0.0773809523809524, 0.0880952380952381, 0.0988095238095238, 0.10952380952381, 0.120238095238095, 0.130952380952381, 0.141666666666667,
            -0.0333333333333333, -0.0190476190476191, -0.00476190476190476, 0.00952380952380953, 0.0238095238095238, 0.0380952380952381, 0.0523809523809524, 0.0666666666666667, 0.080952380952381, 0.0952380952380952, 0.10952380952381, 0.123809523809524, 0.138095238095238, 0.152380952380952, 0.166666666666667,
            -0.0583333333333333, -0.0404761904761905, -0.0226190476190476, -0.00476190476190476, 0.0130952380952381, 0.030952380952381, 0.0488095238095238, 0.0666666666666667, 0.0845238095238095, 0.102380952380952, 0.120238095238095, 0.138095238095238, 0.155952380952381, 0.173809523809524, 0.191666666666667,
            -0.0833333333333333, -0.0619047619047619, -0.0404761904761905, -0.0190476190476191, 0.00238095238095239, 0.0238095238095238, 0.0452380952380952, 0.0666666666666667, 0.0880952380952381, 0.10952380952381, 0.130952380952381, 0.152380952380952, 0.173809523809524, 0.195238095238095, 0.216666666666667,
            -0.108333333333333, -0.0833333333333333, -0.0583333333333333, -0.0333333333333333, -0.00833333333333333, 0.0166666666666667, 0.0416666666666667, 0.0666666666666667, 0.0916666666666667, 0.116666666666667, 0.141666666666667, 0.166666666666667, 0.191666666666667, 0.216666666666667, 0.241666666666667
    };


////////////////////////////////////////////////////////////////////////////
    X=emxCreate_real_T(1,length1);
    Y=emxCreate_real_T(1,length2);
//    double *pTemp = (double *)malloc(sizeof(double)*length1);
//    int k;
//    for ( k = 0; k < myLength; k++) {
//        pTemp[k]=jshort1[k];//打印这里
//    }

    int i = 0;
    for ( i = 0; i < myLength; i++) {
        X->data[i]=jshort1[i]/32768.0;
    }
//    for (i = 0; i <15 ; ++i) {
//        LOGE("%lf",X->data[i]);
//    }
    //  free(pTemp);
/////////////////FIRFilter_BP////////////////////////////////////////////
    double *pTemp1 = (double *)malloc(sizeof(double)*length1);
    fir_init(&S3, BL3, (double*)B3, firState3, blockSize3);
    //Call the FIR process function for every blockSize samples
    for (i = 0; i < numBlocks3; i++)
    {
        fir_filter(&S3,X->data + (i * blockSize3), pTemp1 + (i * blockSize3), blockSize3);
    }
    for(i=0;i<length1;i++)
    {
        X->data[i]=pTemp1[i];
    }
////////////////////FMdemodHilber////////////////////////////////////////////////////
    FMdemodHilber(X,(double)fc,(double)fs,(double)kf,Y);
//    for (i = 0; i <15 ; ++i) {
//        LOGE("%lf",Y->data[i]);
//    }
    double *pTemp2=(double*)malloc(sizeof(double)*Y->size[1]);

    int j = 0;
    for (j = 0; j < Y->size[1]; j++) {
        pTemp2[j]=Y->data[j];
    }
    double *pTemp3 = (double *)malloc(sizeof(double)*Y->size[1]);
    for ( j = 0; j <Y->size[1] ; ++j) {
        pTemp3[j]=iir_biquad(pTemp2[j],pIIRCoeff);
    }
//    for (j = 0; j < 10; ++j) {
//        LOGE("%e",pTemp3[j]);
//    }
//    LOGE("%d",Y->size[1]);
///////////////////////Decimating//////////////////////////////////////////////////
    double *pTemp4 = (double *)malloc(sizeof(double)*( Y->size[1] / M));
    decimate_init(&dS, BDecimateL, M, (double*)BDecimate, decimateState, blockSize);
    for (i = 0; i < numBlocks; i++)
    {
        decimate(&dS, pTemp3 + (i * blockSize), pTemp4 + (i * blockSize / M), blockSize);
    }


////////////////////////Savitzky-Golay Filter////////////////////////////////////////
    struct matrix_instance instance1={ 1, samples2, pTemp4 };
    struct  matrix_instance *p1 = &instance1;
//	const matrix_instance *p2 = new matrix_instance{ N, N, &Coeff1[0][0] };
    struct matrix_instance instance2={ N, N, &Coeff1[0][0] };
    struct  matrix_instance *p2 = &instance2;
    sgFilter(p1, p2);
    emxDestroyArray_real_T(X);
    emxDestroyArray_real_T(Y);
    for (i = 0; i <myLength/8 ;i++) {
        jdouble1[i]=pTemp4[i];
    }
    free(pTemp1);     //释放内存
    free(pTemp2);
    free(pTemp3);
    free(pTemp4);
    (*env)->ReleaseDoubleArrayElements(env,jdoubleArray1,jdouble1,0);
    (*env)->ReleaseShortArrayElements(env,jshortArray1,jshort1,0);


}
#ifdef __cplusplus

}

#endif

#endif
