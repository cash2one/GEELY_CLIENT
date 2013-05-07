package com.geely.geely_client;

import android.app.ActivityGroup;
import android.app.DatePickerDialog;

import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Color;

import android.graphics.Paint.Align;

import android.os.Bundle;

import android.view.GestureDetector;
import android.view.View;

import android.view.View.OnClickListener;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.geely.data.GetData;

import com.geely.util.StringUtil;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;

import org.achartengine.GraphicalView.ClickListener;

import org.achartengine.chart.BarChart.Type;

import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;

import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 *
 * <p>Title: G-MCS</p>
 * <p>Description:吉利MES生产统计界面</p>
 * <p>创建日期:2013-4-15</p>
 * @author ZhouChao
 * @version 1.0
 * <p>湖南家校圈科技有限公司</p>
 */
public class MesInfoActivity extends BaseActivity implements ClickListener {
    public static ActivityGroup group;
    private GraphicalView mChartView;
    private List<String> xLableTexts;
    GestureDetector detector;
    XYMultipleSeriesRenderer renderer;
    LinearLayout rl;
    private DatePickerDialog dateDialog;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int[] colors;
    private TextView startText;
    private TextView endText;
    private double[] datas;
    private double maxValue = 0;
    private String result;
    private Button queryButton;
    private Calendar calendar = Calendar.getInstance();
    private String currentText = "startTime";
    private String lb;
    private Spinner spinner;
    private String currentType = "综合查询";
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                int dayOfMonth) {
                mYear = year;

                mMonth = monthOfYear;

                mDay = dayOfMonth;

                updateDisplay();
            }
        };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mesinfo);
        result = getIntent().getStringExtra("result");
        initColors();
        initData();
        mChartView = createIntent();
        mChartView.setClickListener(this);
        rl = (LinearLayout) findViewById(R.id.mesinfo_graph);
        rl.addView(mChartView);
        spinner = (Spinner) findViewById(R.id.spinner_type);

        // 建立数据源  
        String[] mItems = getResources().getStringArray(R.array.spinnername);

        // 建立Adapter并且绑定数据源  
        ArrayAdapter<String> _Adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mItems);
        //绑定 Adapter到控件  
        spinner.setAdapter(_Adapter);

        DateOnClickListner dateOnClickListner = new DateOnClickListner();
        Date date = new Date();
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
                    Intent intent = new Intent(MesInfoActivity.this,
                            LoadingActivity.class);
                    String type = spinner.getSelectedItem().toString();
                    currentType = type;

                    if ("综合查询".equals(type)) {
                        intent.putExtra("keys",
                            new String[] { "startTime", "endTime" });
                        intent.putExtra("values",
                            new String[] {
                                startText.getText().toString(),
                                endText.getText().toString()
                            });
                        intent.putExtra("url",
                            GetData.SERVER_IP + GetData.GET_MES_INFO_URL);
                    } else {
                        intent.putExtra("keys",
                            new String[] { "startTime", "endTime", "lb" });
                        intent.putExtra("values",
                            new String[] {
                                startText.getText().toString(),
                                endText.getText().toString(), type
                            });
                        intent.putExtra("url",
                            GetData.SERVER_IP +
                            GetData.GET_MES_DETAIL_INFO_URL);
                    }

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

    public void initColors() {
        colors = new int[] {
                Color.parseColor("#044F77"), Color.parseColor("#BCDDE2"),
                Color.parseColor("#77BFCE"), Color.parseColor("#EA8102"),
                Color.parseColor("#D4B07C"), Color.parseColor("#105610"),
                Color.parseColor("#CC6699"), Color.parseColor("#DE2121"),
                Color.parseColor("#FF9900"), Color.parseColor("#31B33A"),
                Color.parseColor("#6AA914"), Color.parseColor("#7B3EB3")
            };
    }

    public void initData() {
        xLableTexts = new ArrayList<String>();

        try {
            JSONObject obj = new JSONObject(result);

            JSONArray array = obj.getJSONArray("dataList");
            datas = new double[array.length()];

            for (int i = 0; i < array.length(); i++) {
                datas[i] = array.getJSONObject(i).getDouble("QTYS");
                xLableTexts.add(array.getJSONObject(i).getString("CAPTION"));

                if (datas[i] > maxValue) {
                    maxValue = datas[i];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public GraphicalView createIntent() {
        String[] titles = new String[] { "Amount" };
        List<double[]> values = new ArrayList<double[]>();

        values.add(datas);

        int[] colors = new int[] { Color.RED };
        renderer = buildBarRenderer(colors, this.colors);
        renderer.setOrientation(Orientation.HORIZONTAL);
        setChartSettings(renderer, "", "", "", 0.5, 12.5, 0, 280, Color.BLACK,
            Color.BLACK);
        renderer.setXLabels(1);
        renderer.setYLabels(20);
        renderer.setShowGrid(true);

        for (int i = 0; i < xLableTexts.size(); i++) {
            renderer.addXTextLabel(i + 1, xLableTexts.get(i));
        }

        renderer.setXLabelsAngle(45);
        renderer.setXLabelsAlign(Align.LEFT);
        //        renderer.setClickEnabled(true);
        renderer.setSelectableBuffer(20);

        int length = renderer.getSeriesRendererCount();

        for (int i = 0; i < length; i++) {
            SimpleSeriesRenderer seriesRenderer = renderer.getSeriesRendererAt(i);
            seriesRenderer.setDisplayChartValues(true);
        }

        final GraphicalView grfv = ChartFactory.getBarChartView(this,
                buildBarDataset(titles, values), renderer, Type.DEFAULT);

        return grfv;
    }

    protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors,
        int[] myColors) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setBarSpacing(0.2);
        renderer.setPanEnabled(true, false);
        //        renderer.setZoomEnabled(false);
        renderer.setZoomEnabled(true, false);
        renderer.setMarginsColor(Color.parseColor("#EEEDED"));
        renderer.setXLabelsColor(Color.BLACK);
        renderer.setYLabelsColor(0, Color.BLACK);

        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.parseColor("#FBFBFC"));

        int length = colors.length;

        for (int i = 0; i < length; i++) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setMultipleColorsEnabled(true);
            r.setColors(myColors);
            r.setColor(colors[i]);
            r.setShowLegendItem(false);
            //              r.setChartvalueAngle(-90);
            r.setChartValuesSpacing(15);
            renderer.addSeriesRenderer(r);
        }

        return renderer;
    }

    protected XYMultipleSeriesDataset buildBarDataset(String[] titles,
        List<double[]> values) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        int length = titles.length;

        for (int i = 0; i < length; i++) {
            CategorySeries series = new CategorySeries(titles[i]);
            double[] v = values.get(i);
            int seriesLength = v.length;

            for (int k = 0; k < seriesLength; k++) {
                series.add(v[k]);
            }

            dataset.addSeries(series.toXYSeries());
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
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(maxValue * 1.2);
        renderer.setMargins(new int[] { 10, 65, 10, 15 });
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
    }

    @Override
    public void click() {
        SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();

        if (seriesSelection != null) {
            int x = (int) seriesSelection.getXValue();
            lb = xLableTexts.get(x - 1);

            Intent intent = new Intent(this, LoadingActivity.class);
            intent.putExtra("keys",
                new String[] { "startTime", "endTime", "lb" });
            intent.putExtra("values",
                new String[] {
                    startText.getText().toString(), endText.getText().toString(),
                    lb
                });
            intent.putExtra("url",
                GetData.SERVER_IP + GetData.GET_MES_DETAIL_INFO_URL);
            intent.putExtra("msg", "数据加载...");
            startActivityForResult(intent, 2);
        }
    }

    public GraphicalView update() {
        String[] titles = new String[] { "Amount" };
        List<double[]> values = new ArrayList<double[]>();

        values.add(new double[] { 46, 30, 10, 10, 19, 13, 62, 57, 266, 22, 132 });

        int[] colors = new int[] { Color.RED };
        int[] myColors = new int[] {
                Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN, Color.RED,
                Color.BLUE, Color.YELLOW, Color.GREEN, Color.RED, Color.BLUE,
                Color.YELLOW, Color.GREEN
            };
        renderer = buildBarRenderer(colors, myColors);
        renderer.setOrientation(Orientation.HORIZONTAL);
        setChartSettings(renderer, "", "", "", 0.5, 12.5, 0, 280, Color.BLACK,
            Color.BLACK);
        renderer.setXLabels(1);
        renderer.setYLabels(20);
        renderer.setShowGrid(true);
        renderer.addXTextLabel(1, "焊装下线");
        renderer.addXTextLabel(2, "前处理下线");
        renderer.addXTextLabel(3, "面漆上线");
        renderer.addXTextLabel(4, "涂装下线");
        renderer.addXTextLabel(5, "总装上线");
        renderer.addXTextLabel(6, "总装下线");
        renderer.addXTextLabel(7, "调试结存");
        renderer.addXTextLabel(8, "检测线");
        renderer.addXTextLabel(9, "商检结存");
        renderer.addXTextLabel(10, "预入库");
        renderer.addXTextLabel(11, "整车入库");
        renderer.setXLabelsAngle(45);
        renderer.setXLabelsAlign(Align.LEFT);
        renderer.setSelectableBuffer(20);

        int length = renderer.getSeriesRendererCount();

        for (int i = 0; i < length; i++) {
            SimpleSeriesRenderer seriesRenderer = renderer.getSeriesRendererAt(i);
            seriesRenderer.setDisplayChartValues(true);
        }

        final GraphicalView grfv = ChartFactory.getBarChartView(this,
                buildBarDataset(titles, values), renderer, Type.DEFAULT);

        return grfv;
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

                    if ("综合查询".equals(currentType)) {
                        mChartView.setClickListener(this);
                    }

                    rl.addView(mChartView);
                }

                break;

            case 2:
                result = data.getStringExtra("result");

                if (StringUtil.isEmpty(result)) {
                    Toast.makeText(this, "网络异常", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject obj = null;

                    try {
                        obj = new JSONObject(result);

                        if ("200".equals(obj.getString("RESULTCODE"))) {
                            Intent intent = new Intent(this,
                                    MesInfoDetailActivity.class);
                            intent.putExtra("result", result);
                            intent.putExtra("startTime",
                                startText.getText().toString());
                            intent.putExtra("endTime",
                                endText.getText().toString());
                            intent.putExtra("lb", lb);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "服务器异常", Toast.LENGTH_SHORT)
                                 .show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
            dateDialog = new DatePickerDialog(MesInfoActivity.this,
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
