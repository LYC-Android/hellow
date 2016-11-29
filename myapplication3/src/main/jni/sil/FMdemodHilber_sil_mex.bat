@echo off
set MATLAB=D:\MATLAB~1
set MATLAB_ARCH=win64
set MATLAB_BIN="D:\Matlab2014b\bin"
set ENTRYPOINT=mexFunction
set OUTDIR=.\
set LIB_NAME=FMdemodHilber_sil
set MEX_NAME=FMdemodHilber_sil
set MEX_EXT=.mexw64
call setEnv.bat
echo # Make settings for FMdemodHilber_sil > FMdemodHilber_sil_mex.mki
echo COMPILER=%COMPILER%>> FMdemodHilber_sil_mex.mki
echo COMPFLAGS=%COMPFLAGS%>> FMdemodHilber_sil_mex.mki
echo OPTIMFLAGS=%OPTIMFLAGS%>> FMdemodHilber_sil_mex.mki
echo DEBUGFLAGS=%DEBUGFLAGS%>> FMdemodHilber_sil_mex.mki
echo LINKER=%LINKER%>> FMdemodHilber_sil_mex.mki
echo LINKFLAGS=%LINKFLAGS%>> FMdemodHilber_sil_mex.mki
echo LINKOPTIMFLAGS=%LINKOPTIMFLAGS%>> FMdemodHilber_sil_mex.mki
echo LINKDEBUGFLAGS=%LINKDEBUGFLAGS%>> FMdemodHilber_sil_mex.mki
echo MATLAB_ARCH=%MATLAB_ARCH%>> FMdemodHilber_sil_mex.mki
echo BORLAND=%BORLAND%>> FMdemodHilber_sil_mex.mki
echo OMPFLAGS= >> FMdemodHilber_sil_mex.mki
echo OMPLINKFLAGS= >> FMdemodHilber_sil_mex.mki
echo EMC_COMPILER=msvc100>> FMdemodHilber_sil_mex.mki
echo EMC_CONFIG=debug>> FMdemodHilber_sil_mex.mki
"D:\Matlab2014b\bin\win64\gmake" -B -f FMdemodHilber_sil_mex.mk
