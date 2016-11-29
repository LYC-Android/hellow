#pragma once
#include <stdlib.h>
#include <string.h>
#ifdef __cplusplus
extern "C" {
#endif
//const int Delay = 10;//�˲�����ʱ��λ
#define Delay 10
const double G = 0.980785548687;//  0.9807855487;//�˲�������
const double a = 0.9615710377693;// 0.9615710378;//����ϵ��

void delay(int D, double *w);//�ӳٺ���
double Notch50Hz(double x, double *w);//��Ƶ�˲�������뱣֤�źŲ�����Ϊ50������

#ifdef __cplusplus
}
#endif