package com.geely.geely_client;

import android.app.Activity;

import android.content.Intent;

import android.graphics.Color;

import android.graphics.Paint.Align;

import android.os.Bundle;

import android.view.View;

import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;


public class MesInfoDetailActivity extends Activity {
    private GraphicalView mChartView;
    private List<String> xLableTexts;
    private double[] datas;
    private double maxValue;
    private String lb;
    private TextView dateText;
    private TextView lbText;
    private String startTime;
    private String endTime;
    private String result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        lb = intent.getStringExtra("lb");
        startTime = intent.getStringExtra("startTime");
        endTime = intent.getStringExtra("endTime");
        result = intent.getStringExtra("result");
        setContentView(R.layout.layout_mesinfo_detail);
        dateText = (TextView) findViewById(R.id.mesdetailinfo_date);
        dateText.setText(startTime + "至" + endTime);
        lbText = (TextView) findViewById(R.id.mesdetailinfo_lb);
        lbText.setText(lb + "生产趋势明细");
        initData();
        mChartView = createIntent();

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.mesdetail_graph);
        rl.addView(mChartView);

        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    finish();
                }
            });
    }

    private void initData() {
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

    public GraphicalView createIntent() {
        String[] titles = new String[] { "Amount" };
        List<double[]> values = new ArrayList<double[]>();

        values.add(datas);

        int[] colors = new int[] { Color.RED };
        PointStyle[] styles = new PointStyle[] { PointStyle.SQUARE };
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
            renderer.addXTextLabel(i+1, xLableTexts.get(i));
        }

        renderer.setXLabelsAngle(45);
        renderer.setXLabelsAlign(Align.LEFT);

        int length = renderer.getSeriesRendererCount();

        for (int i = 0; i < length; i++) {
            SimpleSeriesRenderer seriesRenderer = renderer.getSeriesRendererAt(i);
            seriesRenderer.setDisplayChartValues(true);
        }

        final GraphicalView grfv = ChartFactory.getLineChartView(MesInfoDetailActivity.this,
                buildBarDataset(titles, values, 0), renderer);

        return grfv;
    }

    protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors,
        PointStyle[] styles) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setBarSpacing(20.0);
        renderer.setMarginsColor(Color.parseColor("#EEEDED"));
        renderer.setXLabelsColor(Color.BLACK);
        renderer.setYLabelsColor(0, Color.BLACK);
        renderer.setXLabelsPadding(10);

        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.parseColor("#FBFBFC"));

        int length = colors.length;

        renderer.setMargins(new int[] { 20, 30, 15, 20 });

        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors[i]);
            r.setPointStyle(styles[i]);
            r.setShowLegendItem(false);
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
                series.add(k+1, yV[k]);
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
        //        renderer.setYAxisMin(10, 1);
        //        renderer.setYAxisMax(100, 1);
        renderer.setMargins(new int[] { 10, 35, 10, 15 });
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
    }
}
