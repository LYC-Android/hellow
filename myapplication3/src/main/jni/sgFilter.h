#ifndef __SGFILTER_H__
#define __SGFILTER_H__

#include <stdlib.h>
#include <string.h>
#ifdef __cplusplus
extern "C" {
#endif
struct matrix_instance//矩阵结构体
{
	int numRows;     /**< number of rows of the matrix.     */
	int numCols;     /**< number of columns of the matrix.  */
	double *pData;     /**< points to the data of the matrix. */
};
//struct matrix_instance matrix_instance;
//extern void mat_mul(const matrix_instance *a, const matrix_instance *b, matrix_instance *c);//矩阵相乘，c为输出

extern void mat_trans(struct matrix_instance *pSrc, struct matrix_instance *pDst);//矩阵转置

extern double dot_prod(double * pSrcA, double * pSrcB, int blockSize);//向量点积，blockSize为向量长度

extern void sgFilter(struct matrix_instance *x, const struct matrix_instance *B);//SG滤波器
#ifdef __cplusplus
}
#endif
#endif