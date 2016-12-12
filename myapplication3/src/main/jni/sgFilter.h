#ifndef __SGFILTER_H__
#define __SGFILTER_H__

#include <stdlib.h>
#include <string.h>

#ifdef __cplusplus
extern "C" {
#endif

//#define d 1
#define N 7//15
struct matrix_instance//����ṹ��
{
	int numRows;     /**< number of rows of the matrix.     */
	int numCols;     /**< number of columns of the matrix.  */
	double *pData;     /**< points to the data of the matrix. */
};

//extern void mat_mul(struct matrix_instance *a, struct matrix_instance *b, struct matrix_instance *c);//������ˣ�cΪ���

extern void mat_trans(struct matrix_instance *pSrc, struct matrix_instance *pDst);//����ת��

extern double dot_prod(double * pSrcA, double * pSrcB, int blockSize);//�������blockSizeΪ��������

extern void sgFilter(struct matrix_instance *x, struct matrix_instance *B);//SG�˲���


#ifdef __cplusplus
}
#endif
#endif