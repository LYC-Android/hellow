#include"Decimate.h"

char decimate_init(decimate_instance * S, int numTaps, int M, double * pCoeffs, double * pState, int blockSize)
{
	/* The size of the input block must be a multiple of the decimation factor */
	if ((blockSize % M) != 0u)
	{
		return 1;
	}
	else
	{
		/* Assign filter taps */
		S->numTaps = numTaps;

		/* Assign coefficient pointer */
		S->pCoeffs = pCoeffs;

		/* Clear state buffer and size is always (blockSize + numTaps - 1) */
		memset(pState, 0, (numTaps + (blockSize - 1u)) * sizeof(double));

		/* Assign state pointer */
		S->pState = pState;

		/* Assign Decimation Factor */
		S->M = M;
	}
	return 0;
}

void decimate(const decimate_instance * S, double * pSrc, double * pDst, int blockSize)
{
	double *pState = S->pState;                 /* State pointer */
	double *pCoeffs = S->pCoeffs;               /* Coefficient pointer */
	double *pStateCurnt;                        /* Points to the current sample of the state */
	double *px, *pb;                            /* Temporary pointers for state and coefficient buffers */
	double sum0;                                /* Accumulator */
	double x0, c0;                              /* Temporary variables to hold state and coefficient values */
	int numTaps = S->numTaps;                 /* Number of filter coefficients in the filter */
	int i, tapCnt, blkCnt, outBlockSize = blockSize / S->M;  /* Loop counters */

	/* S->pState buffer contains previous frame (numTaps - 1) samples */
	/* pStateCurnt points to the location where the new input data should be written */
	pStateCurnt = S->pState + (numTaps - 1u);

	/* Total number of output samples to be computed */
	blkCnt = outBlockSize;

	while (blkCnt > 0u)
	{
		/* Copy decimation factor number of new input samples into the state buffer */
		i = S->M;

		do
		{
			*pStateCurnt++ = *pSrc++;

		} while (--i);

		/* Set accumulator to zero */
		sum0 = 0.0f;

		/* Initialize state pointer */
		px = pState;

		/* Initialize coeff pointer */
		pb = pCoeffs;

		/* Loop unrolling.  Process 4 taps at a time. */
		tapCnt = numTaps >> 2;

		/* Loop over the number of taps.  Unroll by a factor of 4.
		** Repeat until we've computed numTaps-4 coefficients. */
		while (tapCnt > 0u)
		{
			/* Read the b[numTaps-1] coefficient */
			c0 = *(pb++);

			/* Read x[n-numTaps-1] sample */
			x0 = *(px++);

			/* Perform the multiply-accumulate */
			sum0 += x0 * c0;

			/* Read the b[numTaps-2] coefficient */
			c0 = *(pb++);

			/* Read x[n-numTaps-2] sample */
			x0 = *(px++);

			/* Perform the multiply-accumulate */
			sum0 += x0 * c0;

			/* Read the b[numTaps-3] coefficient */
			c0 = *(pb++);

			/* Read x[n-numTaps-3] sample */
			x0 = *(px++);

			/* Perform the multiply-accumulate */
			sum0 += x0 * c0;

			/* Read the b[numTaps-4] coefficient */
			c0 = *(pb++);

			/* Read x[n-numTaps-4] sample */
			x0 = *(px++);

			/* Perform the multiply-accumulate */
			sum0 += x0 * c0;

			/* Decrement the loop counter */
			tapCnt--;
		}

		/* If the filter length is not a multiple of 4, compute the remaining filter taps */
		tapCnt = numTaps % 0x4u;

		while (tapCnt > 0u)
		{
			/* Read coefficients */
			c0 = *(pb++);

			/* Fetch 1 state variable */
			x0 = *(px++);

			/* Perform the multiply-accumulate */
			sum0 += x0 * c0;

			/* Decrement the loop counter */
			tapCnt--;
		}

		/* Advance the state pointer by the decimation factor
		* to process the next group of decimation factor number samples */
		pState = pState + S->M;

		/* The result is in the accumulator, store in the destination buffer. */
		*pDst++ = sum0;

		/* Decrement the loop counter */
		blkCnt--;
	}

	/* Processing is complete.
	** Now copy the last numTaps - 1 samples to the satrt of the state buffer.
	** This prepares the state buffer for the next function call. */

	/* Points to the start of the state buffer */
	pStateCurnt = S->pState;

	i = (numTaps - 1u) >> 2;

	/* copy data */
	while (i > 0u)
	{
		*pStateCurnt++ = *pState++;
		*pStateCurnt++ = *pState++;
		*pStateCurnt++ = *pState++;
		*pStateCurnt++ = *pState++;

		/* Decrement the loop counter */
		i--;
	}

	i = (numTaps - 1u) % 0x04u;

	/* copy data */
	while (i > 0u)
	{
		*pStateCurnt++ = *pState++;

		/* Decrement the loop counter */
		i--;
	}
}