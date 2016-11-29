/*
 * File: ipermute.c
 *
 * MATLAB Coder version            : 2.7
 * C/C++ source code generated on  : 30-Jul-2016 22:10:00
 */

/* Include Files */
#include "rt_nonfinite.h"
#include "FMdemodHilber.h"
#include "ipermute.h"
#include "FMdemodHilber_emxutil.h"

/* Function Definitions */

/*
 * Arguments    : const emxArray_creal_T *b
 *                emxArray_creal_T *a
 * Return Type  : void
 */
void ipermute(const emxArray_creal_T *b, emxArray_creal_T *a)
{
  int i0;
  int loop_ub;
  i0 = a->size[0] * a->size[1];
  a->size[0] = 1;
  a->size[1] = b->size[0];
  emxEnsureCapacity((emxArray__common *)a, i0, (int)sizeof(creal_T));
  loop_ub = b->size[0];
  for (i0 = 0; i0 < loop_ub; i0++) {
    a->data[a->size[0] * i0] = b->data[i0];
  }
}

/*
 * File trailer for ipermute.c
 *
 * [EOF]
 */
