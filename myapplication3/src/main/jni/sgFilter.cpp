#include "sgFilter.h"

double Coeff1[N][N] = {
        0.761904761904762,	0.357142857142857,	0.0714285714285714,	-0.0952380952380952,-0.142857142857143,	-0.0714285714285715,0.119047619047619,
        0.357142857142857,	0.285714285714286,	0.214285714285714,	0.142857142857143,	0.0714285714285714,	0,	                -0.0714285714285714,
        0.0714285714285715,	0.214285714285714,	0.285714285714286,	0.285714285714286,	0.214285714285714,	0.0714285714285714,	-0.142857142857143,
        -0.0952380952380952,0.142857142857143,	0.285714285714286,	0.333333333333333,	0.285714285714286,	0.142857142857143,	-0.0952380952380952,
        -0.142857142857143,	0.0714285714285714,	0.214285714285714,	0.285714285714286,	0.285714285714286,	0.214285714285714,	0.0714285714285715,
        -0.0714285714285714,0,	                0.0714285714285714,	0.142857142857143,	0.214285714285714,	0.285714285714286,	0.357142857142857,
        0.119047619047619,	-0.0714285714285715,-0.142857142857143,	-0.0952380952380952,0.0714285714285714,	0.357142857142857,	0.761904761904762

};
//		0.241666666666667, 0.216666666666667, 0.191666666666667, 0.166666666666667, 0.141666666666667, 0.116666666666667, 0.0916666666666667, 0.0666666666666667, 0.0416666666666667, 0.0166666666666667, -0.00833333333333333, -0.0333333333333333, -0.0583333333333333, -0.0833333333333333, -0.108333333333333,
//	0.216666666666667, 0.195238095238095, 0.173809523809524, 0.152380952380952, 0.130952380952381, 0.10952380952381, 0.0880952380952381, 0.0666666666666667, 0.0452380952380952, 0.0238095238095238, 0.00238095238095239, -0.0190476190476191, -0.0404761904761905, -0.0619047619047619, -0.0833333333333333,
//	0.191666666666667, 0.173809523809524, 0.155952380952381, 0.138095238095238, 0.120238095238095, 0.102380952380952, 0.0845238095238095, 0.0666666666666667, 0.0488095238095238, 0.030952380952381, 0.0130952380952381, -0.00476190476190476, -0.0226190476190476, -0.0404761904761905, -0.0583333333333333,
//	0.166666666666667, 0.152380952380952, 0.138095238095238, 0.123809523809524, 0.10952380952381, 0.0952380952380952, 0.080952380952381, 0.0666666666666667, 0.0523809523809524, 0.0380952380952381, 0.0238095238095238, 0.00952380952380953, -0.00476190476190476, -0.0190476190476191, -0.0333333333333333,
//	0.141666666666667, 0.130952380952381, 0.120238095238095, 0.10952380952381, 0.0988095238095238, 0.0880952380952381, 0.0773809523809524, 0.0666666666666667, 0.055952380952381, 0.0452380952380952, 0.0345238095238095, 0.0238095238095238, 0.0130952380952381, 0.00238095238095239, -0.00833333333333333,
//	0.116666666666667, 0.10952380952381, 0.102380952380952, 0.0952380952380952, 0.0880952380952381, 0.080952380952381, 0.0738095238095238, 0.0666666666666667, 0.0595238095238095, 0.0523809523809524, 0.0452380952380952, 0.0380952380952381, 0.030952380952381, 0.0238095238095238, 0.0166666666666667,
//	0.0916666666666667, 0.0880952380952381, 0.0845238095238095, 0.080952380952381, 0.0773809523809524, 0.0738095238095238, 0.0702380952380952, 0.0666666666666667, 0.0630952380952381, 0.0595238095238095, 0.055952380952381, 0.0523809523809524, 0.0488095238095238, 0.0452380952380952, 0.0416666666666667,
//	0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667, 0.0666666666666667,
//	0.0416666666666667, 0.0452380952380952, 0.0488095238095238, 0.0523809523809524, 0.055952380952381, 0.0595238095238095, 0.0630952380952381, 0.0666666666666667, 0.0702380952380952, 0.0738095238095238, 0.0773809523809524, 0.080952380952381, 0.0845238095238095, 0.0880952380952381, 0.0916666666666667,
//	0.0166666666666667, 0.0238095238095238, 0.030952380952381, 0.0380952380952381, 0.0452380952380952, 0.0523809523809524, 0.0595238095238095, 0.0666666666666667, 0.0738095238095238, 0.080952380952381, 0.0880952380952381, 0.0952380952380952, 0.102380952380952, 0.10952380952381, 0.116666666666667,
//	-0.00833333333333333, 0.00238095238095239, 0.0130952380952381, 0.0238095238095238, 0.0345238095238095, 0.0452380952380952, 0.055952380952381, 0.0666666666666667, 0.0773809523809524, 0.0880952380952381, 0.0988095238095238, 0.10952380952381, 0.120238095238095, 0.130952380952381, 0.141666666666667,
//	-0.0333333333333333, -0.0190476190476191, -0.00476190476190476, 0.00952380952380953, 0.0238095238095238, 0.0380952380952381, 0.0523809523809524, 0.0666666666666667, 0.080952380952381, 0.0952380952380952, 0.10952380952381, 0.123809523809524, 0.138095238095238, 0.152380952380952, 0.166666666666667,
//	-0.0583333333333333, -0.0404761904761905, -0.0226190476190476, -0.00476190476190476, 0.0130952380952381, 0.030952380952381, 0.0488095238095238, 0.0666666666666667, 0.0845238095238095, 0.102380952380952, 0.120238095238095, 0.138095238095238, 0.155952380952381, 0.173809523809524, 0.191666666666667,
//	-0.0833333333333333, -0.0619047619047619, -0.0404761904761905, -0.0190476190476191, 0.00238095238095239, 0.0238095238095238, 0.0452380952380952, 0.0666666666666667, 0.0880952380952381, 0.10952380952381, 0.130952380952381, 0.152380952380952, 0.173809523809524, 0.195238095238095, 0.216666666666667,
//	-0.108333333333333, -0.0833333333333333, -0.0583333333333333, -0.0333333333333333, -0.00833333333333333, 0.0166666666666667, 0.0416666666666667, 0.0666666666666667, 0.0916666666666667, 0.116666666666667, 0.141666666666667, 0.166666666666667, 0.191666666666667, 0.216666666666667, 0.241666666666667
//};
//void mat_mul(struct matrix_instance *a, struct matrix_instance *b, struct matrix_instance *c)//������ˣ�cΪ���
//{
//	int u = 0;
//	int i, j, l;
//	for (i = 0; i <= a->numRows - 1; ++i)
//	{
//		for (j = 0; j <= b->numCols - 1; ++j)
//		{
//			u = i*b->numCols + j;
//			for (l = 0; l <= a->numCols - 1; ++l)
//			{
//				*(c->pData + u) += *(a->pData + i * a->numCols + l) * *(b->pData + l * b->numCols + j);
//			}
//		}
//	}
//}



void mat_trans(struct matrix_instance *pSrc, struct matrix_instance *pDst)//����ת��
{
	double *pIn = pSrc->pData;                  /* input data matrix pointer */
	double *pOut = pDst->pData;                 /* output data matrix pointer */
	double *px;                                 /* Temporary output data matrix pointer */
	int nRows = pSrc->numRows;                /* number of rows */
	int nColumns = pSrc->numCols;             /* number of columns */
	int blkCnt, i = 0u, row = nRows;          /* loop counters */
	/* Matrix transpose by exchanging the rows with columns */
	/* row loop     */
	if ((pSrc->numRows != pDst->numCols) || (pSrc->numCols != pDst->numRows))
	{
	}
	else
	{
		do
		{
			/* Loop Unrolling */
			blkCnt = nColumns >> 2;
			/* The pointer px is set to starting address of the column being processed */
			px = pOut + i;
			/* First part of the processing with loop unrolling.  Compute 4 outputs at a time.
			** a second loop below computes the remaining 1 to 3 samples. */
			while (blkCnt > 0u)        /* column loop */
			{
				/* Read and store the input element in the destination */
				*px = *pIn++;
				/* Update the pointer px to point to the next row of the transposed matrix */
				px += nRows;
				/* Read and store the input element in the destination */
				*px = *pIn++;
				/* Update the pointer px to point to the next row of the transposed matrix */
				px += nRows;
				/* Read and store the input element in the destination */
				*px = *pIn++;
				/* Update the pointer px to point to the next row of the transposed matrix */
				px += nRows;
				/* Read and store the input element in the destination */
				*px = *pIn++;
				/* Update the pointer px to point to the next row of the transposed matrix */
				px += nRows;
				/* Decrement the column loop counter */
				blkCnt--;
			}
			/* Perform matrix transpose for last 3 samples here. */
			blkCnt = nColumns % 0x4u;
			while (blkCnt > 0u)
			{
				/* Read and store the input element in the destination */
				*px = *pIn++;
				/* Update the pointer px to point to the next row of the transposed matrix */
				px += nRows;
				/* Decrement the column loop counter */
				blkCnt--;
			}
			i++;
			/* Decrement the row loop counter */
			row--;
		} while (row > 0u);          /* row loop end  */
	}

}


double dot_prod(double * pSrcA, double * pSrcB, int blockSize)//�������blockSizeΪ��������
{
	double sum = 0.0f;                          /* Temporary result storage */
	int blkCnt;                               /* loop counter */
	/*loop Unrolling */
	blkCnt = blockSize >> 2u;
	/* First part of the processing with loop unrolling.  Compute 4 outputs at a time.
	** a second loop below computes the remaining 1 to 3 samples. */
	while (blkCnt > 0u)
	{
		/* C = A[0]* B[0] + A[1]* B[1] + A[2]* B[2] + .....+ A[blockSize-1]* B[blockSize-1] */
		/* Calculate dot product and then store the result in a temporary buffer */
		sum += (*pSrcA++) * (*pSrcB++);
		sum += (*pSrcA++) * (*pSrcB++);
		sum += (*pSrcA++) * (*pSrcB++);
		sum += (*pSrcA++) * (*pSrcB++);
		/* Decrement the loop counter */
		blkCnt--;
	}
	/* If the blockSize is not a multiple of 4, compute any remaining output samples here.
	** No loop unrolling is used. */
	blkCnt = blockSize % 0x4u;
	while (blkCnt > 0u)
	{
		/* C = A[0]* B[0] + A[1]* B[1] + A[2]* B[2] + .....+ A[blockSize-1]* B[blockSize-1] */
		/* Calculate dot product and then store the result in a temporary buffer. */
		sum += (*pSrcA++) * (*pSrcB++);
		/* Decrement the loop counter */
		blkCnt--;
	}
	/* Store the result back in the destination buffer */
	return sum;
}

void sgFilter(struct matrix_instance *x, struct matrix_instance *B)//xΪ��������Ľṹ�壬BΪ�˲�ϵ��Ľṹ��
{
	if (x->numCols <= 8192 && x->numCols > B->numCols)//�ź���ݳ���С��1024�Ҵ����˲���ϵ���
	{
		if (B->numCols <= 35 && B->numCols > 0)//�˲���ϵ���С��17*2+1=35���������ϵ��ߵ���MΪ17
		{
			double **BT = (double**)malloc(sizeof(double*)*B->numCols);//�����ά�����ڴ�ռ������ݴ�ת�ú�ľ���
			int m;
			for (m = 0; m < B->numCols; ++m)//�Ծ����е�ÿһ�����������ڴ�ռ�
			{
				BT[m] = (double*)calloc(B->numCols, sizeof(double));
				memset(BT[m], 0, sizeof(double)*B->numCols);//ͬʱ������������Ԫ�س�ʼ��Ϊ��
			}
			matrix_instance *B2 = (matrix_instance*)malloc(sizeof(matrix_instance*));//���½��Ľṹ������ڴ�
			B2->numCols = B->numCols;
			B2->numRows = B->numCols;
			B2->pData = &BT[0][0];//��ʼ���ṹ�壬ʹָ��ָ���ά����Ŀ�ͷ

			mat_trans(B, B2);//��B�еľ���ת�ò��ݴ���B2��
			int M = (B2->numCols - 1) / 2;//MΪ��ϵ��ߵ���
			double *y = (double*)malloc(sizeof(double)*x->numCols);//yΪ�˲���ݵ��ݴ�����
			memset(y, 0, sizeof(double)*x->numCols);//��ʼ��Ϊ��
			int i, j, k;
			for (i = 0; i < M + 1; ++i)//input-on transients
			{
				*(y + i) = dot_prod(B2->pData + i * B2->numCols, x->pData, B2->numCols);
			}
			k = M + 1;
			for (j = 1; j < x->numCols - B2->numCols; ++j)//steady-state
			{
				*(y + k) = dot_prod(B2->pData + M * B2->numCols, x->pData + j, B2->numCols);
				k++;
			}
			for (i = 0; i <= M; ++i)//input-off transients
			{
				*(y + x->numCols - 1 - M + i) = dot_prod(B2->pData + (M + i) * B2->numCols, x->pData + x->numCols - 1 - B2->numCols + 1, B2->numCols);
			}

			bool flag = true;//d=0ʱϵ�����B��Ԫ��ȫ����ȣ���ʱflagΪ�棬����ȫ�����Ϊ��
			for (i = 0; i < B->numCols; ++i)//�жϾ���B��Ԫ���Ƿ�ȫ�����
			{
				for (j = 0; j < B->numCols; ++j)
				{
					if (*(B->pData + i*B->numCols + j) != *(B->pData))
					{
						flag = false;
						break;
					}
				}
				if (!flag)
					break;
			}
			if (flag)//������Ԫ�ض������˵���˲�������d=0��SG�˲����˻�Ϊ����ƽ���˲������˲�ֵҪ��ƽ��ֵ
			{
				for (i = 0; i < x->numCols; ++i)
				{
					*(y + i) /= x->numCols;
				}
			}
			memcpy(x->pData, y, sizeof(double)* x->numCols);//���˲���ݸ��ǵ�ԭ�������
			free(y);
			free(BT);
		}
	}
}