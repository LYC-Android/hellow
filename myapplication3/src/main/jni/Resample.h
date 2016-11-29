#pragma once
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include "FIRFilter.h"
#include "InterpolCoefsFIR.h"
#ifdef __cplusplus
extern "C" {
#endif

	bool resample8192to500(double * pSrc, double * pDst, int samples, fir_instance *S0, fir_instance *S1, fir_instance *S2);

#ifdef __cplusplus
}
#endif

