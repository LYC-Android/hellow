#include "Notch50Hz.h"

void delay(int D, double *w)
{
	int i;
	for (i = D; i >= 1; i--)
	{
		*(w + i) = *(w + i - 1);
	}
}

double Notch50Hz(double x, double *w)
{
	delay(Delay, w);
	*w = a**(w + 10) + G*x;
	return *w - *(w + 10);
}

