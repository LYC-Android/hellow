package util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by mr.cheng on 2016/9/26.
 */
public class MToast {
    private static Toast toast;
    public static void showToast(Context context,String content){
        if (toast==null){
            toast= Toast.makeText(context, content, Toast.LENGTH_SHORT);
        }else {
            toast.setText(content);
        }
        toast.show();
    }
}
