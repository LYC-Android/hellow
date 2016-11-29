#ifndef __SGFILTER_H__
#define __SGFILTER_H__

#include <stdlib.h>
#include <string.h>
#ifdef __cplusplus
extern "C" {
#endif
struct matrix_instance//����ṹ��
{
	int numRows;     /**< number of rows of the matrix.     */
	int numCols;     /**< number of columns of the matrix.  */
	double *pData;     /**< points to the data of the matrix. */
};
//struct matrix_instance matrix_instance;
//extern void mat_mul(const matrix_instance *a, const matrix_instance *b, matrix_instance *c);//������ˣ�cΪ���

extern void mat_trans(struct matrix_instance *pSrc, struct matrix_instance *pDst);//����ת��

extern double dot_prod(double * pSrcA, double * pSrcB, int blockSize);//���������blockSizeΪ��������

extern void sgFilter(struct matrix_instance *x, const struct matrix_instance *B);//SG�˲���
#ifdef __cplusplus
}
#endif
#endif