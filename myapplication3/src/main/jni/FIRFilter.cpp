#include "FIRFilter.h"

/**
*
* @param[in]  *S points to an instance of the floating-point FIR filter structure.
* @param[in]  *pSrc points to the block of input data.
* @param[out] *pDst points to the block of output data.
* @param[in]  blockSize number of samples to process per call.
* @return     none.
*
*/

void fir_filter(const fir_instance * S, double * pSrc, double * pDst, int blockSize)
{

	double *pState = S->pState;                 /* State pointer */
	double *pCoeffs = S->pCoeffs;               /* Coefficient pointer */
	double *pStateCurnt;                        /* Points to the current sample of the state */
	double *px, *pb;                            /* Temporary pointers for state and coefficient buffers */
	int numTaps = S->numTaps;                 /* Number of filter coefficients in the filter */
	int i, tapCnt, blkCnt;                    /* Loop counters */


	/* Run the below code for Cortex-M4 and Cortex-M3 */

	double acc0, acc1, acc2, acc3;              /* Accumulators */
	double x0, x1, x2, x3, c0;                  /* Temporary variables to hold state and coefficient values */


	/* S->pState points to state array which contains previous frame (numTaps - 1) samples */
	/* pStateCurnt points to the location where the new input data should be written */
	pStateCurnt = &(S->pState[(numTaps - 1u)]);

	/* Apply loop unrolling and compute 4 output values simultaneously.
	* The variables acc0 ... acc3 hold output values that are being computed:
	*
	*    acc0 =  b[numTaps-1] * x[n-numTaps-1] + b[numTaps-2] * x[n-numTaps-2] + b[numTaps-3] * x[n-numTaps-3] +...+ b[0] * x[0]
	*    acc1 =  b[numTaps-1] * x[n-numTaps] +   b[numTaps-2] * x[n-numTaps-1] + b[numTaps-3] * x[n-numTaps-2] +...+ b[0] * x[1]
	*    acc2 =  b[numTaps-1] * x[n-numTaps+1] + b[numTaps-2] * x[n-numTaps] +   b[numTaps-3] * x[n-numTaps-1] +...+ b[0] * x[2]
	*    acc3 =  b[numTaps-1] * x[n-numTaps+2] + b[numTaps-2] * x[n-numTaps+1] + b[numTaps-3] * x[n-numTaps]   +...+ b[0] * x[3]
	*/
	blkCnt = blockSize >> 2;

	/* First part of the processing with loop unrolling.  Compute 4 outputs at a time.
	** a second loop below computes the remaining 1 to 3 samples. */
	while (blkCnt > 0u)
	{
		/* Copy four new input samples into the state buffer */
		*pStateCurnt++ = *pSrc++;
		*pStateCurnt++ = *pSrc++;
		*pStateCurnt++ = *pSrc++;
		*pStateCurnt++ = *pSrc++;

		/* Set all accumulators to zero */
		acc0 = 0.0;
		acc1 = 0.0;
		acc2 = 0.0;
		acc3 = 0.0;

		/* Initialize state pointer */
		px = pState;

		/* Initialize coeff pointer */
		pb = (pCoeffs);

		/* Read the first three samples from the state buffer:  x[n-numTaps], x[n-numTaps-1], x[n-numTaps-2] */
		x0 = *px++;
		x1 = *px++;
		x2 = *px++;

		/* Loop unrolling.  Process 4 taps at a time. */
		tapCnt = numTaps >> 2u;

		/* Loop over the number of taps.  Unroll by a factor of 4.
		** Repeat until we've computed numTaps-4 coefficients. */
		while (tapCnt > 0u)
		{
			/* Read the b[numTaps-1] coefficient */
			c0 = *(pb++);

			/* Read x[n-numTaps-3] sample */
			x3 = *(px++);

			/* acc0 +=  b[numTaps-1] * x[n-numTaps] */
			acc0 += x0 * c0;

			/* acc1 +=  b[numTaps-1] * x[n-numTaps-1] */
			acc1 += x1 * c0;

			/* acc2 +=  b[numTaps-1] * x[n-numTaps-2] */
			acc2 += x2 * c0;

			/* acc3 +=  b[numTaps-1] * x[n-numTaps-3] */
			acc3 += x3 * c0;

			/* Read the b[numTaps-2] coefficient */
			c0 = *(pb++);

			/* Read x[n-numTaps-4] sample */
			x0 = *(px++);

			/* Perform the multiply-accumulate */
			acc0 += x1 * c0;
			acc1 += x2 * c0;
			acc2 += x3 * c0;
			acc3 += x0 * c0;

			/* Read the b[numTaps-3] coefficient */
			c0 = *(pb++);

			/* Read x[n-numTaps-5] sample */
			x1 = *(px++);

			/* Perform the multiply-accumulates */
			acc0 += x2 * c0;
			acc1 += x3 * c0;
			acc2 += x0 * c0;
			acc3 += x1 * c0;

			/* Read the b[numTaps-4] coefficient */
			c0 = *(pb++);

			/* Read x[n-numTaps-6] sample */
			x2 = *(px++);

			/* Perform the multiply-accumulates */
			acc0 += x3 * c0;
			acc1 += x0 * c0;
			acc2 += x1 * c0;
			acc3 += x2 * c0;

			tapCnt--;
		}

		/* If the filter length is not a multiple of 4, compute the remaining filter taps */
		tapCnt = numTaps % 0x4u;

		while (tapCnt > 0u)
		{
			/* Read coefficients */
			c0 = *(pb++);

			/* Fetch 1 state variable */
			x3 = *(px++);

			/* Perform the multiply-accumulates */
			acc0 += x0 * c0;
			acc1 += x1 * c0;
			acc2 += x2 * c0;
			acc3 += x3 * c0;

			/* Reuse the present sample states for next sample */
			x0 = x1;
			x1 = x2;
			x2 = x3;

			/* Decrement the loop counter */
			tapCnt--;
		}

		/* Advance the state pointer by 4 to process the next group of 4 samples */
		pState = pState + 4;

		/* The results in the 4 accumulators, store in the destination buffer. */
		*pDst++ = acc0;
		*pDst++ = acc1;
		*pDst++ = acc2;
		*pDst++ = acc3;

		blkCnt--;
	}

	/* If the blockSize is not a multiple of 4, compute any remaining output samples here.
	** No loop unrolling is used. */
	blkCnt = blockSize % 0x4u;

	while (blkCnt > 0u)
	{
		/* Copy one sample at a time into state buffer */
		*pStateCurnt++ = *pSrc++;

		/* Set the accumulator to zero */
		acc0 = 0.0;

		/* Initialize state pointer */
		px = pState;

		/* Initialize Coefficient pointer */
		pb = (pCoeffs);

		i = numTaps;

		/* Perform the multiply-accumulates */
		do
		{
			acc0 += *px++ * *pb++;
			i--;

		} while (i > 0u);

		/* The result is store in the destination buffer. */
		*pDst++ = acc0;

		/* Advance state pointer by 1 for the next sample */
		pState = pState + 1;

		blkCnt--;
	}

	/* Processing is complete.
	** Now copy the last numTaps - 1 samples to the satrt of the state buffer.
	** This prepares the state buffer for the next function call. */

	/* Points to the start of the state buffer */
	pStateCurnt = S->pState;

	tapCnt = (numTaps - 1u) >> 2u;

	/* copy data */
	while (tapCnt > 0u)
	{
		*pStateCurnt++ = *pState++;
		*pStateCurnt++ = *pState++;
		*pStateCurnt++ = *pState++;
		*pStateCurnt++ = *pState++;

		/* Decrement the loop counter */
		tapCnt--;
	}

	/* Calculate remaining number of copies */
	tapCnt = (numTaps - 1u) % 0x4u;

	/* Copy the remaining q31_t data */
	while (tapCnt > 0u)
	{
		*pStateCurnt++ = *pState++;

		/* Decrement the loop counter */
		tapCnt--;
	}

}
/**
* @details
*
* @param[in,out] *S points to an instance of the floating-point FIR filter structure.
* @param[in] 	  numTaps  Number of filter coefficients in the filter.
* @param[in]     *pCoeffs points to the filter coefficients buffer.
* @param[in]     *pState points to the state buffer.
* @param[in] 	  blockSize number of samples that are processed per call.
* @return 		  none.
*
* <b>Description:</b>
* \par
* <code>pCoeffs</code> points to the array of filter coefficients stored in time reversed order:
* <pre>
*    {b[numTaps-1], b[numTaps-2], b[N-2], ..., b[1], b[0]}
* </pre>
* \par
* <code>pState</code> points to the array of state variables.
* <code>pState</code> is of length <code>numTaps+blockSize-1</code> samples, where <code>blockSize</code> is the number of input samples processed by each call to <code>arm_fir_f32()</code>.
*/

void fir_init(fir_instance * S, int numTaps, double * pCoeffs, double * pState, int blockSize)
{
	/* Assign filter taps */
	S->numTaps = numTaps;

	/* Assign coefficient pointer */
	S->pCoeffs = pCoeffs;

	/* Clear state buffer and the size of state buffer is (blockSize + numTaps - 1) */
	memset(pState, 0, (numTaps + (blockSize - 1u)) * sizeof(double));

	/* Assign state pointer */
	S->pState = pState;

}