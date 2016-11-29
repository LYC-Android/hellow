#ifndef __IIRFILTERCAS_H__
#define __IIRFILTERCAS_H__
#include<math.h>
#include"fdacoefs.h"
#ifdef __cplusplus
extern "C" {
#endif
#define KORD   2   //���˲����Ľ���
#define KCAS   4   //�˲���������

/*ӳ����matlab��ɵ��˲���ϵ��?ֻ������8��4��ֱ��2�͵ļ�����IIR�˲����ṹ*/
/**������ı���Ƶ�Ҫ�����˲��������C�ļ���ͷ��Ϊȫ�����鶨��
const double* pIIRCoeff[KCAS*KORD * 2] = {
	*(NUM + 0) + 0, *(DEN + 1) + 1, *(DEN + 1) + 2, *(NUM + 1) + 1,
	*(NUM + 2) + 0, *(DEN + 3) + 1, *(DEN + 3) + 2, *(NUM + 3) + 1,
	*(NUM + 4) + 0, *(DEN + 5) + 1, *(DEN + 5) + 2, *(NUM + 5) + 1,
	*(NUM + 6) + 0, *(DEN + 7) + 1, *(DEN + 7) + 2, *(NUM + 7) + 1,
};*/
extern double iir_biquad(double x, const double* *IIRCoeff);
#ifdef __cplusplus
}
#endif
#endif