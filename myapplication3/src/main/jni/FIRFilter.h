#ifndef __FIRFILTER_H__
#define __FIRFILTER_H__
#include <string.h>
#include "fdacoefsFIR_LP.h"
#include "fdacoefsFIR_BP.h"
#ifdef __cplusplus
extern "C" {
#endif

	/**
	* @brief Instance structure for the FIR filter.
	*/
	typedef struct
	{
		int numTaps;     /**< number of filter coefficients in the filter. */
		double *pState;    /**< points to the state variable array. The array is of length numTaps+blockSize-1. */
		double *pCoeffs;   /**< points to the coefficient array. The array is of length numTaps. */
	} fir_instance;


	/**
	* @brief Processing function for the FIR filter.
	* @param[in] *S points to an instance of the FIR structure.
	* @param[in] *pSrc points to the block of input data.
	* @param[out] *pDst points to the block of output data.
	* @param[in] blockSize number of samples to process.
	* @return none.
	*/
	void fir_filter(const fir_instance * S, double * pSrc, double * pDst, int blockSize);

	/**
	* @brief  Initialization function for the FIR filter.
	* @param[in,out] *S points to an instance of the FIR filter structure.
	* @param[in] 	numTaps  Number of filter coefficients in the filter.
	* @param[in] 	*pCoeffs points to the filter coefficients.
	* @param[in] 	*pState points to the state buffer.
	* @param[in] 	blockSize number of samples that are processed at a time.
	* @return    	none.
	*/
	void fir_init(fir_instance * S, int numTaps, double * pCoeffs, double * pState, int blockSize);
#ifdef __cplusplus
}
#endif

#endif