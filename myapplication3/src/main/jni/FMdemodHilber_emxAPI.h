/*
 * File: FMdemodHilber_emxAPI.h
 *
 * MATLAB Coder version            : 2.7
 * C/C++ source code generated on  : 30-Jul-2016 22:10:00
 */

#ifndef __FMDEMODHILBER_EMXAPI_H__
#define __FMDEMODHILBER_EMXAPI_H__

/* Include Files */
#include <math.h>
#include <stddef.h>
#include <stdlib.h>
#include <string.h>
#include "rt_defines.h"
#include "rt_nonfinite.h"
#include "rtwtypes.h"
#include "FMdemodHilber_types.h"

/* Function Declarations */
extern emxArray_real_T *emxCreateND_real_T(int numDimensions, int *size);
extern emxArray_real_T *emxCreateWrapperND_real_T(double *data, int
  numDimensions, int *size);
extern emxArray_real_T *emxCreateWrapper_real_T(double *data, int rows, int cols);
extern emxArray_real_T *emxCreate_real_T(int rows, int cols);
extern void emxDestroyArray_real_T(emxArray_real_T *emxArray);

#endif

/*
 * File trailer for FMdemodHilber_emxAPI.h
 *
 * [EOF]
 */
