#include"IIRFilterCas.h"

double iir_biquad(double x, const double* *IIRCoeff)
{
	double temp = 0;
	static double w1_2 = 0, w1_1 = 0, w1;
	static double w2_2 = 0, w2_1 = 0, w2;
	static double w3_2 = 0, w3_1 = 0, w3;
	static double w4_2 = 0, w4_1 = 0, w4;

	w1 = *IIRCoeff[0] * x - *IIRCoeff[1] * w1_1 - *IIRCoeff[2] * w1_2;
	temp = (w1 + *IIRCoeff[3] * w1_1 + w1_2);
	w1_2 = w1_1;
	w1_1 = w1;

	w2 = *IIRCoeff[4] * temp - *IIRCoeff[5] * w2_1 - *IIRCoeff[6] * w2_2;
	temp = (w2 + *IIRCoeff[7] * w2_1 + w2_2);
	w2_2 = w2_1;
	w2_1 = w2;

	w3 = *IIRCoeff[8] * temp - *IIRCoeff[9] * w3_1 - *IIRCoeff[10] * w3_2;
	temp = (w3 + *IIRCoeff[11] * w3_1 + w3_2);
	w3_2 = w3_1;
	w3_1 = w3;

	w4 = *IIRCoeff[12] * temp - *IIRCoeff[13] * w4_1 - *IIRCoeff[14] * w4_2;
	temp = (w4 + *IIRCoeff[15] * w4_1 + w4_2);
	w4_2 = w4_1;
	w4_1 = w4;

	return temp;
}