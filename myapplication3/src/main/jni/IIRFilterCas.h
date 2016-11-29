#ifndef __IIRFILTERCAS_H__
#define __IIRFILTERCAS_H__
#include<math.h>
#include"fdacoefsIIR_LP.h"
#ifdef __cplusplus
extern "C" {
#endif
#define KORD   2   //���˲����Ľ���
#define KCAS   4   //�˲���������

extern double iir_biquad(double x, const double* *IIRCoeff);
#ifdef __cplusplus
}
#endif
#endif