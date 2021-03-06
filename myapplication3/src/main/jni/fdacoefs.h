/*
 * Filter Coefficients (C Source) generated by the Filter Design and Analysis Tool
 * Generated by MATLAB(R) 8.4 and the Signal Processing Toolbox 6.22.
 * Generated on: 20-Aug-2016 11:29:26
 */

/*
 * Discrete-Time IIR Filter (real)
 * -------------------------------
 * Filter Structure    : Direct-Form II, Second-Order Sections
 * Number of Sections  : 4
 * Stable              : Yes
 * Linear Phase        : No
 */

/* General type conversion for MATLAB generated C-code  */
//#include "tmwtypes.h"//注释掉该头文件
/* 
 * Expected path to tmwtypes.h 
 * D:\Matlab2014b\extern\include\tmwtypes.h 
 */
#define MWSPT_NSEC 9
const int NL[MWSPT_NSEC] = { 1,3,1,3,1,3,1,3,1 };
const double NUM[MWSPT_NSEC][3] = {//要手工改成double型
  {
   0.001666564958305,                 0,                 0 
  },
  {
                   1,                 2,                 1 
  },
  {
   0.001619322588659,                 0,                 0 
  },
  {
                   1,                 2,                 1 
  },
  {
   0.001584935866693,                 0,                 0 
  },
  {
                   1,                 2,                 1 
  },
  {
   0.001566928047731,                 0,                 0 
  },
  {
                   1,                 2,                 1 
  },
  {
                   1,                 0,                 0 
  }
};
const int DL[MWSPT_NSEC] = { 1,3,1,3,1,3,1,3,1 };
const double DEN[MWSPT_NSEC][3] = {//要手工改成double型
  {
                   1,                 0,                 0 
  },
  {
                   1,   -1.961755861376,   0.9684221212092 
  },
  {
                   1,                 0,                 0 
  },
  {
                   1,   -1.906145670428,   0.9126229607831 
  },
  {
                   1,                 0,                 0 
  },
  {
                   1,   -1.865668188267,   0.8720079317334 
  },
  {
                   1,                 0,                 0 
  },
  {
                   1,   -1.844470728052,   0.8507384402431 
  },
  {
                   1,                 0,                 0 
  }
};
