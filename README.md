# hellow
/****************John 11/27/2016***********************/<br>
/*Version: JNI.1.1.3.161015_Mr.Cheng<br>
（因为这份程序只用于JNI代码调试，所以主版本号保持不变为1；如果JNI部分增加了新的DSP功能，那么子版本号加1，修正版本号归零；如果只是修正了某部分的代码或BUG，那么修正版号加1；日期格式为年月日；最后为本代码的创建者缩写。）<br>

1.为了对JNI代码进行断点调试，在build.gradle文件内添加了某些命令语句，都用//Added或/*Added*//*End*/与原来的代码区分开来。在IDE的工具栏运行按钮的左边有一个下拉菜单，可以选择myapplication3-native，然后点击爬虫按钮进行断点调试。<br>
2.MyThread.java中的某些代码被修改的部分都用//Modified进行区分。读取的wav文件修改为FMECG8s.wav。<br>
3.以后的代码修改和调试过程都把必要的说明和提示按照格式写在下面，以方便后来者继续修改和完善本代码。<br>


/****************John 11/27/2016***********************/<br>
/*Version: JNI.1.2.0.161127_Mr.Cheng<br>
（因为这份程序只用于JNI代码调试，所以主版本号保持不变为1；如果JNI部分增加了新的DSP功能，那么子版本号加1，修正版本号归零；如果只是修正了某部分的代码或BUG，那么修正版号加1；日期格式为年月日；最后为本代码的创建者缩写。）<br>

1.JNI代码已经调试完毕，输出数据采样率为500，数据量为500*8=4000。<br>
2.为了对JNI代码进行断点调试，在build.gradle文件内添加了某些命令语句，都用//Added或/*Added*//*End*/与原来的代码区分开来。在IDE的工具栏运行按钮的左边有一个下拉菜单，可以选择myapplication3-native，然后点击爬虫按钮进行断点调试。<br>
3.MyThread.java中的某些代码被修改的部分都用//Modified进行区分。读取的wav文件修改为FMECG8s.wav。<br>
4.画图过程有未解决的问题，只能画出ECG数据的前一部分，这个问题交由你们解决。<br>
5.以后的代码修改和调试过程都把必要的说明和提示按照格式写在下面，以方便后来者继续修改和完善本代码。<br>

/****************Mr.Cheng 11/28/2016***********************/<br>
/*Version: JNI.1.2.1.161128_Mr.Cheng<br>
1.更改好必须读取本地文件的问题，现在可以直接读取res/raw/fmsignal.wav文件进行画图。<br>
2.已经更改好画图的各种细节问题。<br>
3.心率问题，可能是读取的本地文件有问题。好像又好了，当我读取res/raw/fmsignal.wav的时候<br>

/****************Mr.Cheng 11/28/2016***********************/<br>
/*Version: JNI.1.2.2.161130_Mr.Cheng<br>
1.已经集成MPAndroidChart，现在看到的Activity是TestActivity.java，读取JNI的是ReadFile.java<br>
2.默认画3次之后就会停止绘图<br>
3.画一次点的时间为8000/1200毫秒<br>

/****************Mr.Cheng 11/28/2016***********************/<br>
/*Version: JNI.1.2.3.161130_Mr.Cheng<br>
修改ReadMe.md格式使其美观。