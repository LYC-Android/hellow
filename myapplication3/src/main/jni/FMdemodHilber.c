/*
 * File: FMdemodHilber.c
 *
 * MATLAB Coder version            : 2.7
 * C/C++ source code generated on  : 30-Jul-2016 22:10:00
 */

/* Include Files */
#include "rt_nonfinite.h"
#include "FMdemodHilber.h"
#include "FMdemodHilber_emxutil.h"
#include "ipermute.h"
#include "interp1.h"

/* Function Declarations */
static void b_eml_fft(const emxArray_creal_T *x, int n, emxArray_creal_T *y);
static int div_s32(int numerator, int denominator);
static void eml_fft(const emxArray_real_T *x, int n, emxArray_creal_T *y);
static double rt_atan2d_snf(double u0, double u1);
static double rt_roundd_snf(double u);

/* Function Definitions */

/*
 * Arguments    : const emxArray_creal_T *x
 *                int n
 *                emxArray_creal_T *y
 * Return Type  : void
 */
static void b_eml_fft(const emxArray_creal_T *x, int n, emxArray_creal_T *y)
{
  int ju;
  int u1;
  int nd2;
  int ixDelta;
  emxArray_real_T *costab1q;
  int nRowsD2;
  int nRowsD4;
  int lastChan;
  double e;
  int k;
  emxArray_real_T *costab;
  emxArray_real_T *sintab;
  int b_n;
  int ix;
  int chanStart;
  int i;
  boolean_T tst;
  double temp_re;
  double temp_im;
  int iDelta2;
  int iheight;
  int ihi;
  double twid_im;
  ju = y->size[0];
  y->size[0] = n;
  emxEnsureCapacity((emxArray__common *)y, ju, (int)sizeof(creal_T));
  if (n > x->size[0]) {
    ju = y->size[0];
    y->size[0] = n;
    emxEnsureCapacity((emxArray__common *)y, ju, (int)sizeof(creal_T));
    for (ju = 0; ju < n; ju++) {
      y->data[ju].re = 0.0;
      y->data[ju].im = 0.0;
    }
  }

  if (x->size[0] == 0) {
  } else {
    u1 = x->size[0];
    if (n <= u1) {
      u1 = n;
    }

    nd2 = (x->size[0] - u1) + 1;
    if (1 >= nd2) {
      ixDelta = 1;
    } else {
      ixDelta = nd2;
    }

    emxInit_real_T(&costab1q, 2);
    nRowsD2 = n / 2;
    nRowsD4 = nRowsD2 / 2;
    lastChan = n * (div_s32(x->size[0], x->size[0]) - 1);
    e = 6.2831853071795862 / (double)n;
    ju = costab1q->size[0] * costab1q->size[1];
    costab1q->size[0] = 1;
    costab1q->size[1] = nRowsD4 + 1;
    emxEnsureCapacity((emxArray__common *)costab1q, ju, (int)sizeof(double));
    costab1q->data[0] = 1.0;
    nd2 = nRowsD4 / 2;
    for (k = 1; k <= nd2; k++) {
      costab1q->data[k] = cos(e * (double)k);
    }

    for (k = nd2 + 1; k < nRowsD4; k++) {
      costab1q->data[k] = sin(e * (double)(nRowsD4 - k));
    }

    emxInit_real_T(&costab, 2);
    emxInit_real_T(&sintab, 2);
    costab1q->data[nRowsD4] = 0.0;
    b_n = costab1q->size[1] - 1;
    nd2 = (costab1q->size[1] - 1) << 1;
    ju = costab->size[0] * costab->size[1];
    costab->size[0] = 1;
    costab->size[1] = nd2 + 1;
    emxEnsureCapacity((emxArray__common *)costab, ju, (int)sizeof(double));
    ju = sintab->size[0] * sintab->size[1];
    sintab->size[0] = 1;
    sintab->size[1] = nd2 + 1;
    emxEnsureCapacity((emxArray__common *)sintab, ju, (int)sizeof(double));
    costab->data[0] = 1.0;
    sintab->data[0] = 0.0;
    for (k = 1; k <= b_n; k++) {
      costab->data[k] = costab1q->data[k];
      sintab->data[k] = costab1q->data[b_n - k];
    }

    for (k = costab1q->size[1]; k <= nd2; k++) {
      costab->data[k] = -costab1q->data[nd2 - k];
      sintab->data[k] = costab1q->data[k - b_n];
    }

    emxFree_real_T(&costab1q);
    ix = 0;
    chanStart = 0;
    while ((n > 0) && (chanStart <= lastChan)) {
      ju = 0;
      nd2 = chanStart;
      for (i = 1; i < u1; i++) {
        y->data[nd2] = x->data[ix];
        b_n = n;
        tst = true;
        while (tst) {
          b_n >>= 1;
          ju ^= b_n;
          tst = ((ju & b_n) == 0);
        }

        nd2 = chanStart + ju;
        ix++;
      }

      y->data[nd2] = x->data[ix];
      ix += ixDelta;
      nd2 = (chanStart + n) - 2;
      if (n > 1) {
        for (i = chanStart; i <= nd2; i += 2) {
          temp_re = y->data[i + 1].re;
          temp_im = y->data[i + 1].im;
          y->data[i + 1].re = y->data[i].re - y->data[i + 1].re;
          y->data[i + 1].im = y->data[i].im - y->data[i + 1].im;
          y->data[i].re += temp_re;
          y->data[i].im += temp_im;
        }
      }

      b_n = 2;
      iDelta2 = 4;
      k = nRowsD4;
      iheight = 1 + ((nRowsD4 - 1) << 2);
      while (k > 0) {
        i = chanStart;
        ihi = chanStart + iheight;
        while (i < ihi) {
          nd2 = i + b_n;
          temp_re = y->data[nd2].re;
          temp_im = y->data[nd2].im;
          y->data[i + b_n].re = y->data[i].re - y->data[nd2].re;
          y->data[i + b_n].im = y->data[i].im - y->data[nd2].im;
          y->data[i].re += temp_re;
          y->data[i].im += temp_im;
          i += iDelta2;
        }

        nd2 = chanStart + 1;
        for (ju = k; ju < nRowsD2; ju += k) {
          e = costab->data[ju];
          twid_im = sintab->data[ju];
          i = nd2;
          ihi = nd2 + iheight;
          while (i < ihi) {
            temp_re = e * y->data[i + b_n].re - twid_im * y->data[i + b_n].im;
            temp_im = e * y->data[i + b_n].im + twid_im * y->data[i + b_n].re;
            y->data[i + b_n].re = y->data[i].re - temp_re;
            y->data[i + b_n].im = y->data[i].im - temp_im;
            y->data[i].re += temp_re;
            y->data[i].im += temp_im;
            i += iDelta2;
          }

          nd2++;
        }

        k /= 2;
        b_n = iDelta2;
        iDelta2 <<= 1;
        iheight -= b_n;
      }

      chanStart += n;
    }

    emxFree_real_T(&sintab);
    emxFree_real_T(&costab);
    if (y->size[0] > 1) {
      e = 1.0 / (double)y->size[0];
      ju = y->size[0];
      emxEnsureCapacity((emxArray__common *)y, ju, (int)sizeof(creal_T));
      nd2 = y->size[0];
      for (ju = 0; ju < nd2; ju++) {
        y->data[ju].re *= e;
        y->data[ju].im *= e;
      }
    }
  }
}

/*
 * Arguments    : int numerator
 *                int denominator
 * Return Type  : int
 */
static int div_s32(int numerator, int denominator)
{
  int quotient;
  unsigned int absNumerator;
  unsigned int absDenominator;
  boolean_T quotientNeedsNegation;
  if (denominator == 0) {
    if (numerator >= 0) {
      quotient = MAX_int32_T;
    } else {
      quotient = MIN_int32_T;
    }
  } else {
    if (numerator >= 0) {
      absNumerator = (unsigned int)numerator;
    } else {
      absNumerator = (unsigned int)-numerator;
    }

    if (denominator >= 0) {
      absDenominator = (unsigned int)denominator;
    } else {
      absDenominator = (unsigned int)-denominator;
    }

    quotientNeedsNegation = ((numerator < 0) != (denominator < 0));
    absNumerator /= absDenominator;
    if (quotientNeedsNegation) {
      quotient = -(int)absNumerator;
    } else {
      quotient = (int)absNumerator;
    }
  }

  return quotient;
}

/*
 * Arguments    : const emxArray_real_T *x
 *                int n
 *                emxArray_creal_T *y
 * Return Type  : void
 */
static void eml_fft(const emxArray_real_T *x, int n, emxArray_creal_T *y)
{
  int nd2;
  int u1;
  int ixDelta;
  emxArray_real_T *costab1q;
  int nRowsD2;
  int nRowsD4;
  int lastChan;
  double e;
  int k;
  emxArray_real_T *costab;
  emxArray_real_T *sintab;
  int b_n;
  int n2;
  int ix;
  int chanStart;
  int i;
  boolean_T tst;
  double temp_re;
  double temp_im;
  int iDelta2;
  int iheight;
  int ihi;
  double twid_im;
  nd2 = y->size[0];
  y->size[0] = n;
  emxEnsureCapacity((emxArray__common *)y, nd2, (int)sizeof(creal_T));
  if (n > x->size[0]) {
    nd2 = y->size[0];
    y->size[0] = n;
    emxEnsureCapacity((emxArray__common *)y, nd2, (int)sizeof(creal_T));
    for (nd2 = 0; nd2 < n; nd2++) {
      y->data[nd2].re = 0.0;
      y->data[nd2].im = 0.0;
    }
  }

  if (x->size[0] == 0) {
  } else {
    u1 = x->size[0];
    if (n <= u1) {
      u1 = n;
    }

    nd2 = (x->size[0] - u1) + 1;
    if (1 >= nd2) {
      ixDelta = 1;
    } else {
      ixDelta = nd2;
    }

    emxInit_real_T(&costab1q, 2);
    nRowsD2 = n / 2;
    nRowsD4 = nRowsD2 / 2;
    lastChan = n * (div_s32(x->size[0], x->size[0]) - 1);
    e = 6.2831853071795862 / (double)n;
    nd2 = costab1q->size[0] * costab1q->size[1];
    costab1q->size[0] = 1;
    costab1q->size[1] = nRowsD4 + 1;
    emxEnsureCapacity((emxArray__common *)costab1q, nd2, (int)sizeof(double));
    costab1q->data[0] = 1.0;
    nd2 = nRowsD4 / 2;
    for (k = 1; k <= nd2; k++) {
      costab1q->data[k] = cos(e * (double)k);
    }

    for (k = nd2 + 1; k < nRowsD4; k++) {
      costab1q->data[k] = sin(e * (double)(nRowsD4 - k));
    }

    emxInit_real_T(&costab, 2);
    emxInit_real_T(&sintab, 2);
    costab1q->data[nRowsD4] = 0.0;
    b_n = costab1q->size[1] - 1;
    n2 = (costab1q->size[1] - 1) << 1;
    nd2 = costab->size[0] * costab->size[1];
    costab->size[0] = 1;
    costab->size[1] = n2 + 1;
    emxEnsureCapacity((emxArray__common *)costab, nd2, (int)sizeof(double));
    nd2 = sintab->size[0] * sintab->size[1];
    sintab->size[0] = 1;
    sintab->size[1] = n2 + 1;
    emxEnsureCapacity((emxArray__common *)sintab, nd2, (int)sizeof(double));
    costab->data[0] = 1.0;
    sintab->data[0] = 0.0;
    for (k = 1; k <= b_n; k++) {
      costab->data[k] = costab1q->data[k];
      sintab->data[k] = -costab1q->data[b_n - k];
    }

    for (k = costab1q->size[1]; k <= n2; k++) {
      costab->data[k] = -costab1q->data[n2 - k];
      sintab->data[k] = -costab1q->data[k - b_n];
    }

    emxFree_real_T(&costab1q);
    ix = 0;
    chanStart = 0;
    while ((n > 0) && (chanStart <= lastChan)) {
      n2 = 0;
      nd2 = chanStart;
      for (i = 1; i < u1; i++) {
        y->data[nd2].re = x->data[ix];
        y->data[nd2].im = 0.0;
        b_n = n;
        tst = true;
        while (tst) {
          b_n >>= 1;
          n2 ^= b_n;
          tst = ((n2 & b_n) == 0);
        }

        nd2 = chanStart + n2;
        ix++;
      }

      y->data[nd2].re = x->data[ix];
      y->data[nd2].im = 0.0;
      ix += ixDelta;
      nd2 = (chanStart + n) - 2;
      if (n > 1) {
        for (i = chanStart; i <= nd2; i += 2) {
          temp_re = y->data[i + 1].re;
          temp_im = y->data[i + 1].im;
          y->data[i + 1].re = y->data[i].re - y->data[i + 1].re;
          y->data[i + 1].im = y->data[i].im - y->data[i + 1].im;
          y->data[i].re += temp_re;
          y->data[i].im += temp_im;
        }
      }

      b_n = 2;
      iDelta2 = 4;
      k = nRowsD4;
      iheight = 1 + ((nRowsD4 - 1) << 2);
      while (k > 0) {
        i = chanStart;
        ihi = chanStart + iheight;
        while (i < ihi) {
          nd2 = i + b_n;
          temp_re = y->data[nd2].re;
          temp_im = y->data[nd2].im;
          y->data[i + b_n].re = y->data[i].re - y->data[nd2].re;
          y->data[i + b_n].im = y->data[i].im - y->data[nd2].im;
          y->data[i].re += temp_re;
          y->data[i].im += temp_im;
          i += iDelta2;
        }

        nd2 = chanStart + 1;
        for (n2 = k; n2 < nRowsD2; n2 += k) {
          e = costab->data[n2];
          twid_im = sintab->data[n2];
          i = nd2;
          ihi = nd2 + iheight;
          while (i < ihi) {
            temp_re = e * y->data[i + b_n].re - twid_im * y->data[i + b_n].im;
            temp_im = e * y->data[i + b_n].im + twid_im * y->data[i + b_n].re;
            y->data[i + b_n].re = y->data[i].re - temp_re;
            y->data[i + b_n].im = y->data[i].im - temp_im;
            y->data[i].re += temp_re;
            y->data[i].im += temp_im;
            i += iDelta2;
          }

          nd2++;
        }

        k /= 2;
        b_n = iDelta2;
        iDelta2 <<= 1;
        iheight -= b_n;
      }

      chanStart += n;
    }

    emxFree_real_T(&sintab);
    emxFree_real_T(&costab);
  }
}

/*
 * Arguments    : double u0
 *                double u1
 * Return Type  : double
 */
static double rt_atan2d_snf(double u0, double u1)
{
  double y;
  int b_u0;
  int b_u1;
  if (rtIsNaN(u0) || rtIsNaN(u1)) {
    y = rtNaN;
  } else if (rtIsInf(u0) && rtIsInf(u1)) {
    if (u0 > 0.0) {
      b_u0 = 1;
    } else {
      b_u0 = -1;
    }

    if (u1 > 0.0) {
      b_u1 = 1;
    } else {
      b_u1 = -1;
    }

    y = atan2(b_u0, b_u1);
  } else if (u1 == 0.0) {
    if (u0 > 0.0) {
      y = RT_PI / 2.0;
    } else if (u0 < 0.0) {
      y = -(double)(RT_PI / 2.0);
    } else {
      y = 0.0;
    }
  } else {
    y = atan2(u0, u1);
  }

  return y;
}

/*
 * Arguments    : double u
 * Return Type  : double
 */
static double rt_roundd_snf(double u)
{
  double y;
  if (fabs(u) < 4.503599627370496E+15) {
    if (u >= 0.5) {
      y = floor(u + 0.5);
    } else if (u > -0.5) {
      y = u * 0.0;
    } else {
      y = ceil(u - 0.5);
    }
  } else {
    y = u;
  }

  return y;
}

/*
 * %
 * 检验输入参数的维度是否越界
 * Arguments    : const emxArray_real_T *x
 *                double fc
 *                double fs
 *                double kf
 *                emxArray_real_T *y
 * Return Type  : void
 */
void FMdemodHilber(const emxArray_real_T *x, double fc, double fs, double kf,
                   emxArray_real_T *y)
{
  double samptime;
  int m;
  double apnd;
  int ndbl;
  double cdiff;
  int iyLead;
  double dt;
  double t_re;
  double dps;
  emxArray_real_T *t2;
  emxArray_real_T *b_x;
  emxArray_real_T *c_x;
  emxArray_creal_T *b_y1;
  emxArray_creal_T *c_y1;
  emxArray_creal_T *d_y1;
  emxArray_creal_T *r0;
  unsigned int uv0[2];
  emxArray_real_T *vwork;
  int vlen;
  unsigned int k;
  int32_T exitg1;
  emxArray_real_T *b_y;

  /* % */
  if (fs == 8000.0) {
    /* 要求传入必须是8K采样率的信号 */
    samptime = (double)x->size[1] / 8000.0;

    /* 求出这一帧信号的长度以及持续时间 */
    if (samptime - 0.000125 < 0.0) {
      m = -1;
      apnd = samptime - 0.000125;
    } else {
      ndbl = (int)floor((samptime - 0.000125) / 0.000125 + 0.5);
      apnd = (double)ndbl * 0.000125;
      cdiff = apnd - (samptime - 0.000125);
      if (fabs(cdiff) < 4.4408920985006262E-16 * fabs(samptime - 0.000125)) {
        ndbl++;
        apnd = samptime - 0.000125;
      } else if (cdiff > 0.0) {
        apnd = ((double)ndbl - 1.0) * 0.000125;
      } else {
        ndbl++;
      }

      if (ndbl >= 0) {
        m = ndbl - 1;
      } else {
        m = -1;
      }
    }

    iyLead = y->size[0] * y->size[1];
    y->size[0] = 1;
    y->size[1] = m + 1;
    emxEnsureCapacity((emxArray__common *)y, iyLead, (int)sizeof(double));
    if (m + 1 > 0) {
      y->data[0] = 0.0;
      if (m + 1 > 1) {
        y->data[m] = apnd;
        ndbl = m / 2;
        for (iyLead = 1; iyLead < ndbl; iyLead++) {
          cdiff = (double)iyLead * 0.000125;
          y->data[iyLead] = cdiff;
          y->data[m - iyLead] = apnd - cdiff;
        }

        if (ndbl << 1 == m) {
          y->data[ndbl] = apnd / 2.0;
        } else {
          cdiff = (double)ndbl * 0.000125;
          y->data[ndbl] = cdiff;
          y->data[ndbl + 1] = apnd - cdiff;
        }
      }
    }

    /* 求出原信号的时间向量 */
    /* %    */
    /* 插值 */
    fs = 65536.0 / samptime;

    /* 求出新的采样率 */
    dt = 1.0 / fs;
    t_re = samptime - dt;
    if (rtIsNaN(dt) || rtIsNaN(t_re)) {
      m = 0;
      dps = rtNaN;
      apnd = t_re;
    } else if ((dt == 0.0) || ((0.0 < t_re) && (dt < 0.0)) || ((t_re < 0.0) &&
                (dt > 0.0))) {
      m = -1;
      dps = 0.0;
      apnd = t_re;
    } else if (rtIsInf(t_re)) {
      m = 0;
      dps = rtNaN;
      apnd = t_re;
    } else if (rtIsInf(dt)) {
      m = 0;
      dps = 0.0;
      apnd = t_re;
    } else {
      dps = 0.0;
      samptime = floor(t_re / dt + 0.5);
      apnd = samptime * dt;
      if (dt > 0.0) {
        cdiff = apnd - t_re;
      } else {
        cdiff = t_re - apnd;
      }

      if (fabs(cdiff) < 4.4408920985006262E-16 * fabs(t_re)) {
        samptime++;
        apnd = t_re;
      } else if (cdiff > 0.0) {
        apnd = (samptime - 1.0) * dt;
      } else {
        samptime++;
      }

      if (samptime >= 0.0) {
        m = (int)samptime - 1;
      } else {
        m = -1;
      }
    }

    emxInit_real_T(&t2, 2);
    iyLead = t2->size[0] * t2->size[1];
    t2->size[0] = 1;
    t2->size[1] = m + 1;
    emxEnsureCapacity((emxArray__common *)t2, iyLead, (int)sizeof(double));
    if (m + 1 > 0) {
      t2->data[0] = dps;
      if (m + 1 > 1) {
        t2->data[m] = apnd;
        ndbl = m / 2;
        for (iyLead = 1; iyLead < ndbl; iyLead++) {
          cdiff = (double)iyLead * dt;
          t2->data[iyLead] = dps + cdiff;
          t2->data[m - iyLead] = apnd - cdiff;
        }

        if (ndbl << 1 == m) {
          t2->data[ndbl] = (dps + apnd) / 2.0;
        } else {
          cdiff = (double)ndbl * dt;
          t2->data[ndbl] = dps + cdiff;
          t2->data[ndbl + 1] = apnd - cdiff;
        }
      }
    }

    emxInit_real_T(&b_x, 2);
    b_emxInit_real_T(&c_x, 1);

    /* 根据新的采样率求出对应的时间向量 */
    interp1(y, x, t2, b_x);

    /* 对原信号进行邻近插值，空余部分补零，得到2的整数幂长度的信号 */
    /* % */
    /* 利用FFT进行希尔伯特变换 */
    m = b_x->size[1] - 2;
    samptime = (double)b_x->size[1] / 2.0;
    iyLead = c_x->size[0];
    c_x->size[0] = b_x->size[1];
    emxEnsureCapacity((emxArray__common *)c_x, iyLead, (int)sizeof(double));
    ndbl = b_x->size[1];
    for (iyLead = 0; iyLead < ndbl; iyLead++) {
      c_x->data[iyLead] = b_x->data[b_x->size[0] * iyLead];
    }

    emxInit_creal_T(&b_y1, 2);
    b_emxInit_creal_T(&c_y1, 1);
    eml_fft(c_x, b_x->size[1], c_y1);
    ipermute(c_y1, b_y1);
    ndbl = 1;
    emxFree_real_T(&c_x);
    while (ndbl - 1 <= (int)((samptime - 1.0) + -1.0) - 1) {
      t_re = b_y1->data[ndbl].re;
      cdiff = b_y1->data[ndbl].im;
      b_y1->data[ndbl] = b_y1->data[m - ndbl];
      b_y1->data[m - ndbl].re = -t_re;
      b_y1->data[m - ndbl].im = -cdiff;
      ndbl++;
    }

    b_emxInit_creal_T(&d_y1, 1);
    b_y1->data[0].re = 0.0;
    b_y1->data[0].im = 0.0;
    b_y1->data[(int)samptime - 1].re = 0.0;
    b_y1->data[(int)samptime - 1].im = 0.0;
    iyLead = d_y1->size[0];
    d_y1->size[0] = b_y1->size[1];
    emxEnsureCapacity((emxArray__common *)d_y1, iyLead, (int)sizeof(creal_T));
    ndbl = b_y1->size[1];
    for (iyLead = 0; iyLead < ndbl; iyLead++) {
      d_y1->data[iyLead] = b_y1->data[b_y1->size[0] * iyLead];
    }

    b_eml_fft(d_y1, b_x->size[1], c_y1);

    /* % */
    /* 后续的解调计算 */
    t_re = fc * -0.0;
    cdiff = fc * -6.2831853071795862;
    iyLead = b_y1->size[0] * b_y1->size[1];
    b_y1->size[0] = 1;
    b_y1->size[1] = t2->size[1];
    emxEnsureCapacity((emxArray__common *)b_y1, iyLead, (int)sizeof(creal_T));
    ndbl = t2->size[0] * t2->size[1];
    emxFree_creal_T(&d_y1);
    emxFree_real_T(&b_x);
    for (iyLead = 0; iyLead < ndbl; iyLead++) {
      b_y1->data[iyLead].re = t2->data[iyLead] * t_re;
      b_y1->data[iyLead].im = t2->data[iyLead] * cdiff;
    }

    emxInit_creal_T(&r0, 2);
    iyLead = r0->size[0] * r0->size[1];
    r0->size[0] = 1;
    r0->size[1] = b_y1->size[1];
    emxEnsureCapacity((emxArray__common *)r0, iyLead, (int)sizeof(creal_T));
    ndbl = b_y1->size[0] * b_y1->size[1];
    for (iyLead = 0; iyLead < ndbl; iyLead++) {
      r0->data[iyLead] = b_y1->data[iyLead];
    }

    for (iyLead = 0; iyLead < b_y1->size[1]; iyLead++) {
      if (rtIsInf(r0->data[iyLead].im) && rtIsInf(r0->data[iyLead].re) &&
          (r0->data[iyLead].re < 0.0)) {
        t_re = 0.0;
        cdiff = 0.0;
      } else {
        samptime = exp(r0->data[iyLead].re / 2.0);
        t_re = samptime * (samptime * cos(r0->data[iyLead].im));
        cdiff = samptime * (samptime * sin(r0->data[iyLead].im));
      }

      r0->data[iyLead].re = t_re;
      r0->data[iyLead].im = cdiff;
    }

    ipermute(c_y1, b_y1);
    iyLead = b_y1->size[0] * b_y1->size[1];
    b_y1->size[0] = 1;
    emxEnsureCapacity((emxArray__common *)b_y1, iyLead, (int)sizeof(creal_T));
    ndbl = b_y1->size[0];
    iyLead = b_y1->size[1];
    ndbl *= iyLead;
    emxFree_creal_T(&c_y1);
    for (iyLead = 0; iyLead < ndbl; iyLead++) {
      samptime = b_y1->data[iyLead].re;
      cdiff = b_y1->data[iyLead].im;
      t_re = r0->data[iyLead].re;
      dps = r0->data[iyLead].im;
      b_y1->data[iyLead].re = samptime * t_re - cdiff * dps;
      b_y1->data[iyLead].im = samptime * dps + cdiff * t_re;
    }

    emxFree_creal_T(&r0);
    apnd = 1.0 / (6.2831853071795862 * kf);
    for (iyLead = 0; iyLead < 2; iyLead++) {
      uv0[iyLead] = (unsigned int)b_y1->size[iyLead];
    }

    iyLead = y->size[0] * y->size[1];
    y->size[0] = 1;
    emxEnsureCapacity((emxArray__common *)y, iyLead, (int)sizeof(double));
    iyLead = y->size[0] * y->size[1];
    y->size[1] = (int)uv0[1];
    emxEnsureCapacity((emxArray__common *)y, iyLead, (int)sizeof(double));
    ndbl = (int)uv0[1];
    for (iyLead = 0; iyLead < ndbl; iyLead++) {
      y->data[iyLead] = 0.0;
    }

    for (iyLead = 0; iyLead < b_y1->size[1]; iyLead++) {
      y->data[iyLead] = rt_atan2d_snf(b_y1->data[iyLead].im, b_y1->data[iyLead].
        re);
    }

    emxFree_creal_T(&b_y1);
    b_emxInit_real_T(&vwork, 1);
    vlen = y->size[1] - 1;
    uv0[0] = (unsigned int)y->size[1];
    iyLead = vwork->size[0];
    vwork->size[0] = (int)uv0[0];
    emxEnsureCapacity((emxArray__common *)vwork, iyLead, (int)sizeof(double));
    ndbl = 0;
    for (iyLead = 0; iyLead <= vlen; iyLead++) {
      vwork->data[iyLead] = y->data[ndbl];
      ndbl++;
    }

    m = vwork->size[0];
    cdiff = 0.0;
    k = 1U;
    while (((int)k < m) && (!((!rtIsInf(vwork->data[(int)k - 1])) && (!rtIsNaN
              (vwork->data[(int)k - 1]))))) {
      k = (unsigned int)((int)k + 1);
    }

    if ((int)k < vwork->size[0]) {
      samptime = vwork->data[(int)k - 1];
      do {
        exitg1 = 0;
        k++;
        while ((k <= (unsigned int)m) && (!((!rtIsInf(vwork->data[(int)k - 1])) &&
                 (!rtIsNaN(vwork->data[(int)k - 1]))))) {
          k++;
        }

        if (k > (unsigned int)m) {
          exitg1 = 1;
        } else {
          t_re = vwork->data[(int)k - 1] - samptime;
          samptime = (t_re + 3.1415926535897931) / 6.2831853071795862;
          if (fabs(samptime - rt_roundd_snf(samptime)) <= 2.2204460492503131E-16
              * fabs(samptime)) {
            samptime = 0.0;
          } else {
            samptime = (samptime - floor(samptime)) * 6.2831853071795862;
          }

          dps = samptime - 3.1415926535897931;
          if ((samptime - 3.1415926535897931 == -3.1415926535897931) && (t_re >
               0.0)) {
            dps = 3.1415926535897931;
          }

          if (fabs(t_re) >= 3.1415926535897931) {
            cdiff += dps - t_re;
          }

          samptime = vwork->data[(int)k - 1];
          vwork->data[(int)k - 1] += cdiff;
        }
      } while (exitg1 == 0);
    }

    ndbl = 0;
    for (iyLead = 0; iyLead <= vlen; iyLead++) {
      y->data[ndbl] = vwork->data[iyLead];
      ndbl++;
    }

    emxFree_real_T(&vwork);
    if (y->size[1] == 0) {
      iyLead = t2->size[0] * t2->size[1];
      t2->size[0] = 1;
      t2->size[1] = 0;
      emxEnsureCapacity((emxArray__common *)t2, iyLead, (int)sizeof(double));
    } else {
      if (y->size[1] - 1 <= 1) {
        ndbl = y->size[1] - 1;
      } else {
        ndbl = 1;
      }

      if (ndbl < 1) {
        iyLead = t2->size[0] * t2->size[1];
        t2->size[0] = 1;
        t2->size[1] = 0;
        emxEnsureCapacity((emxArray__common *)t2, iyLead, (int)sizeof(double));
      } else {
        iyLead = t2->size[0] * t2->size[1];
        t2->size[0] = 1;
        t2->size[1] = y->size[1] - 1;
        emxEnsureCapacity((emxArray__common *)t2, iyLead, (int)sizeof(double));
        ndbl = y->size[1] - 1;
        if (!(ndbl == 0)) {
          ndbl = 1;
          iyLead = 0;
          samptime = y->data[0];
          for (m = 2; m <= y->size[1]; m++) {
            cdiff = y->data[ndbl];
            t_re = samptime;
            samptime = cdiff;
            cdiff -= t_re;
            ndbl++;
            t2->data[iyLead] = cdiff;
            iyLead++;
          }
        }
      }
    }

    emxInit_real_T(&b_y, 2);
    iyLead = b_y->size[0] * b_y->size[1];
    b_y->size[0] = 1;
    b_y->size[1] = t2->size[1] + 1;
    emxEnsureCapacity((emxArray__common *)b_y, iyLead, (int)sizeof(double));
    ndbl = t2->size[1];
    for (iyLead = 0; iyLead < ndbl; iyLead++) {
      b_y->data[b_y->size[0] * iyLead] = apnd * t2->data[t2->size[0] * iyLead];
    }

    b_y->data[b_y->size[0] * t2->size[1]] = 0.0;
    iyLead = y->size[0] * y->size[1];
    y->size[0] = 1;
    y->size[1] = b_y->size[1];
    emxEnsureCapacity((emxArray__common *)y, iyLead, (int)sizeof(double));
    ndbl = b_y->size[1];
    emxFree_real_T(&t2);
    for (iyLead = 0; iyLead < ndbl; iyLead++) {
      y->data[y->size[0] * iyLead] = b_y->data[b_y->size[0] * iyLead] * fs;
    }

    emxFree_real_T(&b_y);
  } else {
    iyLead = y->size[0] * y->size[1];
    y->size[0] = 1;
    y->size[1] = 1;
    emxEnsureCapacity((emxArray__common *)y, iyLead, (int)sizeof(double));
    y->data[0] = 0.0;

    /* 如果返回0，则代表输入信号不满足8K采样率的要求 */
  }
}

/*
 * File trailer for FMdemodHilber.c
 *
 * [EOF]
 */
