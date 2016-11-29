#ifndef __DECIMATE_H__
#define __DECIMATE_H__
#include <string.h>
#ifdef __cplusplus
extern "C" {
#endif
	const int BDecimateL = 11;
	const double BDecimate[11] = {//�ֶ��޸ĳ�double��
		0.0065949346189877623,
		0,
		-0.050931092033252794,
		0,
		0.29433828743913626,
		0.5,
		0.29433828743913626,
		0,
		-0.050931092033252794,
		0,
		0.0065949346189877623,
	};
	/**
	* @brief Instance structure for the FIR filter.
	*/
	typedef struct
	{
		int M;
		int numTaps;
		double *pCoeffs;
		double *pState;
	} decimate_instance;
	/**
	* @brief  Initialization function for the floating-point FIR decimator.
	* @param[in,out] *S points to an instance of the floating-point FIR decimator structure.
	* @param[in] numTaps  number of coefficients in the filter.
	* @param[in] M  decimation factor.
	* @param[in] *pCoeffs points to the filter coefficients.
	* @param[in] *pState points to the state buffer.
	* @param[in] blockSize number of input samples to process per call.
	* @return    The function returns ARM_MATH_SUCCESS if initialization was successful or ARM_MATH_LENGTH_ERROR if
	* <code>blockSize</code> is not a multiple of <code>M</code>.
	*
	* <b>Description:</b>
	* \par
	* <code>pCoeffs</code> points to the array of filter coefficients stored in time reversed order:
	* <pre>
	*    {b[numTaps-1], b[numTaps-2], b[N-2], ..., b[1], b[0]}
	* </pre>
	* \par
	* <code>pState</code> points to the array of state variables.
	* <code>pState</code> is of length <code>numTaps+blockSize-1</code> words where <code>blockSize</code> is the number of input samples passed to <code>arm_fir_decimate_f32()</code>.
	* <code>M</code> is the decimation factor.
	*/

    char decimate_init(decimate_instance * S, int numTaps, int M, double * pCoeffs, double * pState, int blockSize);

	/**
	* @brief Processing function for the floating-point FIR decimator.
	* @param[in] *S        points to an instance of the floating-point FIR decimator structure.
	* @param[in] *pSrc     points to the block of input data.
	* @param[out] *pDst    points to the block of output data.
	* @param[in] blockSize number of input samples to process per call.
	* @return none.
	*/
	void decimate(const decimate_instance * S, double * pSrc, double * pDst, int blockSize);
#ifdef __cplusplus
}
#endif

#endif