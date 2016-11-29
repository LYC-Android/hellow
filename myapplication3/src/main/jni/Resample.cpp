#include "Resample.h"

bool resample8192to500(double * pSrc, double * pDst, int samples, fir_instance *S0, fir_instance *S1, fir_instance *S2)
{
	if (samples % 8192 !=0 || samples < 8192)
	{
		return false;
	}
	int L0 = 5; int L1 = 5; int L2 = 5; int M = 2048;//��ֵ����ͳ�ȡ����
	int L = L0*L1*L2;
	int block_size = BLOCK_SIZE;
	/************************************************************************/
	/* 1st stage of interplotation                                          */
	/************************************************************************/
	double *pTemp0 = (double *)malloc(sizeof(double)*samples*L0);
	double *pY0 = (double *)malloc(sizeof(double)*samples*L0);
	memset(pTemp0, 0, samples*L0 * sizeof(double));
	int n = 0;
	int j = 0;
	while (n < samples*L0)
	{
		if (n % 5 == 0)
		{
			*(pTemp0 + n) = *(pSrc + j++);
		}
		n++;
	}
	int i;
	for (i = 0; i < samples*L0 / block_size; i++)
	{
		fir_filter(S0, pTemp0 + (i * block_size), pY0 + (i * block_size), block_size);
	}
	free(pTemp0);
	/************************************************************************/
	/* 2nd stage of interplotation                                          */
	/************************************************************************/
	double *pTemp1 = (double *)malloc(sizeof(double)*samples*L0*L1);
	double *pY1 = (double *)malloc(sizeof(double)*samples*L0*L1);
	memset(pTemp1, 0, samples*L0*L1 * sizeof(double));
	n = 0;
	j = 0;
	while (n < samples*L0*L1)
	{
		if (n % 5 == 0)
		{
			*(pTemp1 + n) = *(pY0 + j++);
		}
		n++;
	}
	free(pY0);
	for (i = 0; i < samples*L0*L1 / block_size; i++)
	{
		fir_filter(S1, pTemp1 + (i * block_size), pY1 + (i * block_size), block_size);
	}
	free(pTemp1);
	/************************************************************************/
	/* 3rd stage of interplotation                                          */
	/************************************************************************/
	double *pTemp2 = (double *)malloc(sizeof(double)*samples*L0*L1*L2);
	double *pY2 = (double *)malloc(sizeof(double)*samples*L0*L1*L2);
	memset(pTemp2, 0, samples*L0*L1*L2 * sizeof(double));
	n = 0;
	j = 0;
	while (n < samples*L0*L1*L2)
	{
		if (n % 5 == 0)
		{
			*(pTemp2 + n) = *(pY1 + j++);
		}
		n++;
	}
	free(pY1);
	for (i = 0; i < samples*L0*L1*L2 / block_size; i++)
	{
		fir_filter(S2, pTemp2 + (i * block_size), pY2 + (i * block_size), block_size);
	}
	free(pTemp2);
	/************************************************************************/
	/* Decimation stage                                                     */
	/************************************************************************/
	double *pY3 = (double *)malloc(sizeof(double)*samples*L / M);
	n = 0;
	j = 0;
	while (n < samples*L)
	{
		if (n % M == 0)
		{
			*(pY3 + j++) = *(pY2 + n++);
			continue;
		}
		n++;
	}
	free(pY2);
	memcpy(pDst, pY3, sizeof(double)*samples*L / M);
	free(pY3);
	return true;
}