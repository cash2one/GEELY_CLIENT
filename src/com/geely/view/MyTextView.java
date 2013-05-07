package com.geely.view;

import android.app.Activity;

import android.content.Context;

import android.content.res.Resources;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;

import android.util.AttributeSet;
import android.util.TypedValue;

import android.view.WindowManager;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Vector;


public class MyTextView extends TextView {
    private final String namespace = "http://schemas.android.com/apk/res/android";
    private Paint mPaint = new Paint();
    private float y;
    private float textSize = 15;
    private int lines = 0;
    private String txt = "";
    private LinearLayout lin = null;

    public MyTextView(Context context, AttributeSet attrs, LinearLayout lin) {
        super(context, attrs);
        this.lin = lin;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        txt = attrs.getAttributeValue(namespace, "text");
        txt = (txt == null) ? "" : txt;

        String textColor = attrs.getAttributeValue(namespace, "textColor");
        textColor = (textColor == null) ? "#000000" : textColor;

        String textSizeStr = attrs.getAttributeValue(namespace, "textSize");
        textSizeStr = (textSizeStr == null) ? "15" : textSizeStr;

        if (textSizeStr.toLowerCase().indexOf("sp") != -1) {
            textSize = getRawSize(TypedValue.COMPLEX_UNIT_SP,
                    Float.parseFloat(textSizeStr.substring(0,
                            textSizeStr.length() - 2)));
        } else {
            textSize = 15;
        }

        mPaint.setTextSize(textSize);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor(textColor));
        mPaint.setStyle(Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // view.draw()绘制了控件的背景
        // 控件的绘制操作及顺序：

        // 可以绘制内容和滚动条。
        // draw backgroud
        // canvas.drawColor(Color.WHITE);
        // draw text
        FontMetrics fm = mPaint.getFontMetrics();
        float baseline = fm.descent - fm.ascent;
        float x = 0;
        y = baseline; // 由于系统基于字体的底部来绘制文本，所有需要加上字体的高度。
                      // 文本自动换行

        String[] texts = autoSplit(txt, mPaint, getWidth() - textSize);
        System.out.printf("line indexs: %s\n", Arrays.toString(texts));

        for (String text : texts) {
            if (text != null) {
                canvas.drawText(text, x, y, mPaint); // 坐标以控件左上角为原点
                y += (baseline + fm.leading); // 添加字体行间距
            }
        }

        try {
            android.widget.LinearLayout.LayoutParams param = (android.widget.LinearLayout.LayoutParams) lin.getLayoutParams();
            param.height = (int) (y);
            lin.setLayoutParams(param);
        } catch (Exception e) {
            try {
                RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) lin.getLayoutParams();
                param.height = (int) (y);
                lin.setLayoutParams(param);
            } catch (Exception ex) {
            }
        }
    }

    private String[] autoSplit(String content, Paint p, float width) {
        int length = content.length();
        float textWidth = p.measureText(content);

        if (textWidth <= width) {
            return new String[] { content };
        }

        int start = 0;
        int end = 1;
        int i = 0;
        lines = (int) Math.ceil(textWidth / width); // 计算行数

        String[] lineTexts = new String[lines];

        while (start < length) {
            if (p.measureText(content, start, end) > width) { // 文本宽度超出控件宽度时
                lineTexts[i++] = (String) content.subSequence(start, end);
                start = end;
            }

            if (end == length) { // 不足一行的文本
                lineTexts[i] = (String) content.subSequence(start, end);

                break;
            }

            end += 1;
        }

        return lineTexts;
    }

    public float getRawSize(int unit, float size) {
        Context c = getContext();
        Resources r;

        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }

        return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
    }
}
