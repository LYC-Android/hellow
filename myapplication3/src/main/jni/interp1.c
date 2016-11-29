/*
 * File: interp1.c
 *
 * MATLAB Coder version            : 2.7
 * C/C++ source code generated on  : 30-Jul-2016 22:10:00
 */

/* Include Files */
#include "rt_nonfinite.h"
#include "FMdemodHilber.h"
#include "interp1.h"
#include "FMdemodHilber_emxutil.h"

/* Function Definitions */

/*
 * Arguments    : const emxArray_real_T *varargin_1
 *                const emxArray_real_T *varargin_2
 *                const emxArray_real_T *varargin_3
 *                emxArray_real_T *Vq
 * Return Type  : void
 */
void interp1(const emxArray_real_T *varargin_1, const emxArray_real_T
             *varargin_2, const emxArray_real_T *varargin_3, emxArray_real_T *Vq)
{
  emxArray_real_T *y;
  int low_ip1;
  int nd2;
  emxArray_real_T *x;
  int nx;
  unsigned int outsize[2];
  int k;
  int32_T exitg1;
  int mid_i;
  double xtmp;
  emxInit_real_T(&y, 2);
  low_ip1 = y->size[0] * y->size[1];
  y->size[0] = 1;
  y->size[1] = varargin_2->size[1];
  emxEnsureCapacity((emxArray__common *)y, low_ip1, (int)sizeof(double));
  nd2 = varargin_2->size[0] * varargin_2->size[1];
  for (low_ip1 = 0; low_ip1 < nd2; low_ip1++) {
    y->data[low_ip1] = varargin_2->data[low_ip1];
  }

  emxInit_real_T(&x, 2);
  low_ip1 = x->size[0] * x->size[1];
  x->size[0] = 1;
  x->size[1] = varargin_1->size[1];
  emxEnsureCapacity((emxArray__common *)x, low_ip1, (int)sizeof(double));
  nd2 = varargin_1->size[0] * varargin_1->size[1];
  for (low_ip1 = 0; low_ip1 < nd2; low_ip1++) {
    x->data[low_ip1] = varargin_1->data[low_ip1];
  }

  nx = varargin_1->size[1];
  for (low_ip1 = 0; low_ip1 < 2; low_ip1++) {
    outsize[low_ip1] = (unsigned int)varargin_3->size[low_ip1];
  }

  low_ip1 = Vq->size[0] * Vq->size[1];
  Vq->size[0] = 1;
  emxEnsureCapacity((emxArray__common *)Vq, low_ip1, (int)sizeof(double));
  low_ip1 = Vq->size[0] * Vq->size[1];
  Vq->size[1] = (int)outsize[1];
  emxEnsureCapacity((emxArray__common *)Vq, low_ip1, (int)sizeof(double));
  nd2 = (int)outsize[1];
  for (low_ip1 = 0; low_ip1 < nd2; low_ip1++) {
    Vq->data[low_ip1] = 0.0;
  }

  if (varargin_3->size[1] == 0) {
  } else {
    k = 1;
    do {
      exitg1 = 0;
      if (k <= nx) {
        if (rtIsNaN(varargin_1->data[k - 1])) {
          exitg1 = 1;
        } else {
          k++;
        }
      } else {
        if (varargin_1->data[1] < varargin_1->data[0]) {
          low_ip1 = nx >> 1;
          for (mid_i = 1; mid_i <= low_ip1; mid_i++) {
            xtmp = x->data[mid_i - 1];
            x->data[mid_i - 1] = x->data[nx - mid_i];
            x->data[nx - mid_i] = xtmp;
          }

          nd2 = varargin_2->size[1] >> 1;
          for (mid_i = 1; mid_i <= nd2; mid_i++) {
            low_ip1 = varargin_2->size[1] - mid_i;
            xtmp = y->data[mid_i - 1];
            y->data[mid_i - 1] = y->data[low_ip1];
            y->data[low_ip1] = xtmp;
          }
        }

        for (k = 0; k + 1 <= varargin_3->size[1]; k++) {
          if (rtIsNaN(varargin_3->data[k])) {
            Vq->data[k] = rtNaN;
          } else if ((varargin_3->data[k] > x->data[x->size[1] - 1]) ||
                     (varargin_3->data[k] < x->data[0])) {
          } else {
            nd2 = 1;
            low_ip1 = 2;
            nx = x->size[1];
            while (nx > low_ip1) {
              mid_i = (nd2 >> 1) + (nx >> 1);
              if (((nd2 & 1) == 1) && ((nx & 1) == 1)) {
                mid_i++;
              }

              if (varargin_3->data[k] >= x->data[mid_i - 1]) {
                nd2 = mid_i;
                low_ip1 = mid_i + 1;
              } else {
                nx = mid_i;
              }
            }

            if (varargin_3->data[k] >= (x->data[nd2] + x->data[nd2 - 1]) / 2.0)
            {
              nd2++;
            }

            if (nd2 > 0) {
              Vq->data[k] = y->data[nd2 - 1];
            }
          }
        }

        exitg1 = 1;
      }
    } while (exitg1 == 0);
  }

  emxFree_real_T(&x);
  emxFree_real_T(&y);
}

/*
 * File trailer for interp1.c
 *
 * [EOF]
 */
