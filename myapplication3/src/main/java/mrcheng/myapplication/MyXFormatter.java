package mrcheng.myapplication;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Created by mr.cheng on 2016/11/23.
 */
public class MyXFormatter implements IAxisValueFormatter {
    private static final String TAG = "MyXFormatter";
    private final long miles;
    /**
     * decimalformat for formatting
     */
    protected DecimalFormat mFormat;
    private SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm:ss");
    /**
     * the number of decimal digits this formatter uses
     */
    protected int digits = 0;

    /**
     * Constructor that specifies to how many digits the value should be
     * formatted.
     *
     * @param digits
     */
    public MyXFormatter(int digits) {
        this.digits = digits;
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < digits; i++) {
            if (i == 0)
                b.append(".");
            b.append("0");
        }
        miles = System.currentTimeMillis();
        mFormat = new DecimalFormat("###,###,###,##0" + b.toString());
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // avoid memory allocations here (for performance)
//        Log.d(TAG, "getFormattedValue() called with: " + "value = [" + value + "]");
        long result=miles+ (long) (8000/1280*value);
        return mTimeFormat.format(result);
    }

    /**
     * Returns the number of decimal digits this formatter uses or -1, if unspecified.
     *
     * @return
     */
    public int getDecimalDigits() {
        return digits;
    }
}
