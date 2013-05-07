package com.geely.geely_client;

import android.app.DatePickerDialog;

import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Color;

import android.graphics.Paint.Align;

import android.os.Bundle;

import android.view.View;

import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geely.data.GetData;

import com.geely.util.StringUtil;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;

import org.achartengine.chart.PointStyle;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;

import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;
import org.achartengine.renderer.XYSeriesRenderer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MisInfoActivity extends BaseActivity {
    private GraphicalView mChartView;
    private List<String> xLableTexts;
    private String misType;
    private double maxValue;
    private String[] titles;
    private List<double[]> datas;
    private String result;
    LinearLayout rl;
    private TextView startText;
    private TextView endText;
    private Button queryButton;
    private Calendar calendar = Calendar.getInstance();
    private String currentText = "startTime";
    private int mYear;
    private int mMonth;
    private int mDay;
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                int dayOfMonth) {
                mYear = year;

                mMonth = monthOfYear;

                mDay = dayOfMonth;

                updateDisplay();
            }
        };

    private void updateDisplay() {
        if ("startText".equals(currentText)) {
            startText.setText(new StringBuilder().append(mYear)
                                                 .append(((mMonth + 1) < 10)
                    ? ("0" + (mMonth + 1)) : (mMonth + 1))
                                                 .append((mDay < 10)
                    ? ("0" + mDay) : mDay));
        } else if ("endText".equals(currentText)) {
            endText.setText(new StringBuilder().append(mYear)
                                               .append(((mMonth + 1) < 10)
                    ? ("0" + (mMonth + 1)) : (mMonth + 1))
                                               .append((mDay < 10) ? ("0" +
                    mDay) : mDay));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_misinfo);

        Intent intent = getIntent();
        misType = intent.getStringExtra("misType");
        result = intent.getStringExtra("result");
        initData();
        mChartView = createIntent();

        rl = (LinearLayout) findViewById(R.id.mesinfo_graph);
        rl.addView(mChartView);

        DateOnClickListner dateOnClickListner = new DateOnClickListner();
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        date = calendar.getTime();

        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        String now = format.format(date);
        startText = (TextView) findViewById(R.id.mesinfo_date_start);
        startText.setTag("startText");
        startText.setText(now);
        startText.setOnClickListener(dateOnClickListner);
        endText = (TextView) findViewById(R.id.mesinfo_date_end);
        endText.setTag("endText");
        endText.setText(now);
        endText.setOnClickListener(dateOnClickListner);
        queryButton = (Button) findViewById(R.id.mesinfo_button_query);
        queryButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MisInfoActivity.this,
                            LoadingActivity.class);
                    intent.putExtra("keys",
                        new String[] { "startTime", "endTime", "misType" });
                    intent.putExtra("values",
                        new String[] {
                            startText.getText().toString(),
                            endText.getText().toString(), misType
                        });
                    intent.putExtra("url",
                        GetData.SERVER_IP + GetData.GET_MIS_INFO_URL);
                    intent.putExtra("msg", "数据加载...");
                    startActivityForResult(intent, 0);
                }
            });

        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    finish();
                }
            });
    }

    private void initData() {
        datas = new ArrayList<double[]>();
        xLableTexts = new ArrayList<String>();

        try {
            JSONObject obj = new JSONObject(result);

            JSONArray array = obj.getJSONArray("dataList");
            int arrayLength = array.length();
            titles = new String[arrayLength];

            double[] tempDatas;

            for (int i = 0; i < arrayLength; i++) {
                JSONObject arrayObj = array.getJSONObject(i);
                titles[i] = arrayObj.getString("ZZMC");

                JSONArray dataArray = arrayObj.getJSONArray("misList");
                int dataLength = dataArray.length();
                tempDatas = new double[dataLength];

                for (int k = 0; k < dataLength; k++) {
                    JSONObject dataObj = dataArray.getJSONObject(k);
                    tempDatas[k] = dataObj.getDouble("sjz");

                    if (tempDatas[k] > maxValue) {
                        maxValue = tempDatas[k];
                    }

                    if (i == 0) {
                        xLableTexts.add(dataObj.getString("rq"));
                    }
                }

                datas.add(tempDatas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GraphicalView createIntent() {
        int[] colors = new int[] { Color.GREEN, Color.RED };
        PointStyle[] styles = new PointStyle[] {
                PointStyle.SQUARE, PointStyle.SQUARE
            };
        XYMultipleSeriesRenderer renderer = buildBarRenderer(colors, styles);
        renderer.setOrientation(Orientation.HORIZONTAL);
        setChartSettings(renderer, "", "", "", 0.5, 24.5, 0, 46, Color.BLACK,
            Color.BLACK);
        renderer.setXLabels(0);
        renderer.setYLabels(20);
        renderer.setShowGrid(true);
        renderer.setPanEnabled(true, false);
        renderer.setZoomEnabled(true, false);

        for (int i = 0; i < xLableTexts.size(); i++) {
            renderer.addXTextLabel(i + 1, xLableTexts.get(i));
        }

        renderer.setXLabelsAngle(45);
        renderer.setXLabelsAlign(Align.LEFT);

        int length = renderer.getSeriesRendererCount();

        for (int i = 0; i < length; i++) {
            SimpleSeriesRenderer seriesRenderer = renderer.getSeriesRendererAt(i);
            seriesRenderer.setDisplayChartValues(true);
        }

        final GraphicalView grfv = ChartFactory.getLineChartView(this,
                buildBarDataset(titles, datas, 0), renderer);

        return grfv;
    }

    protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors,
        PointStyle[] styles) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(20);
        renderer.setBarSpacing(20.0);
        renderer.setMarginsColor(Color.parseColor("#EEEDED"));
        renderer.setXLabelsColor(Color.BLACK);
        renderer.setYLabelsColor(0, Color.BLACK);
        renderer.setXLabelsPadding(10);

        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.parseColor("#FBFBFC"));

        int length = colors.length;

        renderer.setMargins(new int[] { 20, 30, 35, 20 });

        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors[i]);
            r.setPointStyle(styles[i]);
            r.setShowLegendItem(true);
            r.setChartValuesTextSize(16);
            r.setFillPoints(true);
            r.setLineWidth(2);
            r.setDisplayChartValuesDistance(1);
            renderer.addSeriesRenderer(r);
        }

        return renderer;
    }

    protected XYMultipleSeriesDataset buildBarDataset(String[] titles,
        List<double[]> yValues, int scale) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        int length = titles.length;

        for (int i = 0; i < length; i++) {
            XYSeries series = new XYSeries(titles[i], scale);
            double[] yV = yValues.get(i);
            int seriesLength = yV.length;

            for (int k = 0; k < seriesLength; k++) {
                series.add(k + 1, yV[k]);
            }

            dataset.addSeries(series);
        }

        return dataset;
    }

    protected void setChartSettings(XYMultipleSeriesRenderer renderer,
        String title, String xTitle, String yTitle, double xMin, double xMax,
        double yMin, double yMax, int axesColor, int labelsColor) {
        renderer.setChartTitle(title);
        renderer.setYLabelsAlign(Align.RIGHT);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(10);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(maxValue * 1.2);
        renderer.setMargins(new int[] { 10, 35, 10, 15 });
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            switch (requestCode) {
            case 0:
                result = data.getStringExtra("result");

                if (StringUtil.isEmpty(result)) {
                    Toast.makeText(this, "网络异常", Toast.LENGTH_SHORT).show();
                } else {
                    initData();
                    rl.removeAllViews();
                    mChartView = createIntent();
                    rl.addView(mChartView);
                }

                break;
            }
        }
    }

    /**
    *
    * <p>Title: G-MCS</p>
    * <p>Description:日期框打击事件</p>
    * <p>创建日期:2013-4-21</p>
    * @author ZhouChao
    * @version 1.0
    * <p>湖南家校圈科技有限公司</p>
    */
    public class DateOnClickListner implements OnClickListener {
        @Override
        public void onClick(View view) {
            currentText = view.getTag().toString();

            String value = ((TextView) view).getText().toString();
            calendar.set(Integer.valueOf(value.substring(0, 4)),
                Integer.valueOf(value.substring(4, 6)) - 1,
                Integer.valueOf(value.substring(6, 8)));

            DatePickerDialog dateDialog = new DatePickerDialog(MisInfoActivity.this,
                    mDateSetListener, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dateDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
            dateDialog.show();
        }
    }
}
