package mrcheng.myapplication;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by mr.cheng on 2016/11/22.
 */
public class TestActivity extends Activity {
    @InjectView(R.id.chart1)
    LineChart mChart;
    @InjectView(R.id.xinlv)
    TextView mXinlv;
    private Thread thread;
    private int index;
    private static final String TAG = "TestActivity";

    static {
        System.loadLibrary("myNativeLib");
    }

    private ReadFile readFile;
    private XAxis xl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_test);
        ButterKnife.inject(this);
        readFile = new ReadFile(TestActivity.this);
        initView();
    }

    private void initView() {
        // enable description text
        mChart.getDescription().setEnabled(false);


        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);//#########这里background

        // if disabled, scaling can be done on x- and y-axis separately
        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);


        LineData data = new LineData();
//        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);
        mChart.setExtraRightOffset(50);
        //还有轴没有设置
        Legend l = mChart.getLegend();
        l.setEnabled(false);
        xl = mChart.getXAxis();
        xl.setDrawGridLines(false);
        xl.setGranularity(1f);
        xl.setLabelCount(8, true);
        xl.setTextSize(16);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xl.setAvoidFirstLastClipping(true);
        YAxis leftAxis = mChart.getAxisLeft();
        //反转轴值
        leftAxis.setInverted(true);
        leftAxis.setEnabled(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        feedMultiple();


    }


    private void feedMultiple() {
        if (thread != null)
            thread.interrupt();
        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                addEntry();

            }
        };
        //这里设置了画3次之后停止画图
        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    index = 0;
                    for (int j = 0; j < readFile.getResult().size(); j++) {
                        runOnUiThread(runnable);
                        try {
                            Thread.sleep(8000 / 1200);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    // Don't generate garbage runnables inside the loop.


                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TestActivity.this, "画完3次了", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        xl.setValueFormatter(new MyXFormatter(0));

        thread.start();
    }

    private void addEntry() {
        LineData data = mChart.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            data.addEntry(new Entry(set.getEntryCount(), (float) (readFile.getResult().get(index) + 0f)), 0);
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.setVisibleXRangeMaximum(1200);
            mChart.setVisibleXRangeMinimum(1200);
            mChart.moveViewToX(data.getEntryCount());
            index++;
            if (index % 150 == 0) {
                int xinlv[] = readFile.getXinlvdatas();
                mXinlv.setText(xinlv[(index / 150)-1] + "/min");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, null);
        set.setDrawCircleHole(false);
        set.setDrawHighlightIndicators(false);
        set.setLineWidth(3f);
        set.setDrawCircles(false);
        set.setDrawValues(false);
        set.setColor(Color.BLACK);
        return set;
    }


}
