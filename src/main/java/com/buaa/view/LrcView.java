package com.buaa.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.buaa.bean.Lyric;
import com.buaa.bean.LyricLine;
import com.buaa.utils.L;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Window10 on 2016/3/17.
 */
public class LrcView extends android.widget.TextView {
    private float width;        //歌词视图宽度
    private float height;       //歌词视图高度
    private float middleHeight;
    private static final int DY = 50;
    private Paint currentPaint; //当前画笔对象
    private Paint notCurrentPaint;  //非当前画笔对象
    private float textHeight = 30;  //文本高度
    private float textSizeCurrent = 40;
    private float textSizeNormal = 30;//文本大小
    private LyricLine lyricLine;
    private static final int TOTLE_LINE = 14;
    private boolean clean;

    private Lyric lyric;

    public void setmLrcList(Lyric lyric, LyricLine lyricLine) {
        this.lyric = lyric;
        this.lyricLine = lyricLine;
    }

    public LrcView(Context context) {
        super(context);
        init();
    }

    public LrcView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LrcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setClean(boolean clean) {
        this.clean = clean;
    }

    private void init() {
//        setFocusable(true);     //设置可对焦
//
//        //高亮部分
//        currentPaint = new Paint();
//        currentPaint.setAntiAlias(true);    //设置抗锯齿，让文字美观饱满
//        currentPaint.setTextAlign(Paint.Align.CENTER);//设置文本对齐方式
//
//        //非高亮部分
//        notCurrentPaint = new Paint();
//        notCurrentPaint.setAntiAlias(true);
//        notCurrentPaint.setTextAlign(Paint.Align.CENTER);
        setFocusable(true);
        notCurrentPaint = new Paint();
        notCurrentPaint.setAntiAlias(true);
        notCurrentPaint.setTextSize(textSizeNormal);
        notCurrentPaint.setColor(Color.WHITE);
        notCurrentPaint.setTypeface(Typeface.SERIF);

        currentPaint = new Paint();
        currentPaint.setAntiAlias(true);
        currentPaint.setColor(Color.YELLOW);
        currentPaint.setTextSize(textSizeCurrent);
        currentPaint.setTypeface(Typeface.SANS_SERIF);
    }

    /**
     * 绘画歌词
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if(clean){
            super.onDraw(canvas);
            setText("");
            clean = !clean;
            return;
        }

//        super.onDraw(canvas);
//        if (canvas == null) {
//            return;
//        }
//
//        currentPaint.setColor(Color.argb(255, 255, 0, 0));
//        notCurrentPaint.setColor(Color.argb(255, 255, 0, 0));
//
//        currentPaint.setTextSize(24);
//        currentPaint.setTypeface(Typeface.SERIF);
//
//        notCurrentPaint.setTextSize(textSize);
//        notCurrentPaint.setTypeface(Typeface.DEFAULT);
//
//        try {
//            setText("");
//            canvas.drawText(lyricLine.getStr(), width / 2, height / 2, currentPaint);
//            float tempY = height / 2;
//            //画出本句之前的句子
//            Set<LyricLine> front = lyric.getFrount();
//            //front.remove(lyricLine);
//            Object[] objects = front.toArray();
//            //L.i("frontsArr:"+objects.length);
//            if(objects.length>=2){
//                for (int i = objects.length - 2; i >= 0; i--) {
//                    //向上推移
//                    tempY = tempY - textHeight;
//                    canvas.drawText(((LyricLine)objects[i]).getStr(), width / 2, tempY, notCurrentPaint);
//                }
//            }
//            tempY = height / 2;
//            //画出本句之后的句子
//            Set<LyricLine> after = lyric.getAfter();
//            Iterator<LyricLine> iterator2 = after.iterator();
//            while (iterator2.hasNext()) {
//                LyricLine line = iterator2.next();
//                //向上推移
//                tempY = tempY + textHeight;
//                canvas.drawText(line.getStr(), width / 2, tempY, notCurrentPaint);
//            }
//        } catch (Exception e) {
//            setText("...木有歌词文件，赶紧去下载...");
//        }
        super.onDraw(canvas);

        if (lyricLine == null) {
            return;
        }
        if(lyric.getAfter().size()==0){
            super.onDraw(canvas);
            setText("");
            return;
        }
        setText("");
        // canvas.drawColor(Color.BLACK);
        Paint p = notCurrentPaint;
        p.setTextAlign(Paint.Align.CENTER);
        Paint p2 = currentPaint;
        p2.setTextAlign(Paint.Align.CENTER);

        canvas.drawText(lyricLine.getStr(), width, middleHeight, p2);


        int alphaValue = 25;
        float tempY = middleHeight;
        //画出本句之前的句子
        Set<LyricLine> front = lyric.getFrount();
        //front.remove(lyricLine);
        Object[] objects = front.toArray();
        //L.i("frontsArr:"+objects.length);
        int totle = 0;
        if (objects.length >= 2) {
            for (int i = objects.length - 2; i >= 0; i--) {
                totle++;
                if (totle > TOTLE_LINE / 2) {
                    break;
                }
                tempY -= DY;
                if (tempY < 0) {
                    break;
                }
                p.setColor(Color.argb(255 - alphaValue, 245, 245, 245));
                canvas.drawText(((LyricLine) objects[i]).getStr(), width, tempY, p);
                alphaValue += 25;
            }
        }
        alphaValue = 25;
        tempY = middleHeight;
        //画出本句之后的句子
        totle = 0;
        Set<LyricLine> after = lyric.getAfter();
        Iterator<LyricLine> iterator2 = after.iterator();
        while (iterator2.hasNext()) {
            totle++;
            if (totle > TOTLE_LINE / 2) {
                break;
            }
            LyricLine line = iterator2.next();
            tempY += DY;
            if (tempY > height) {
                break;
            }
            p.setColor(Color.argb(255 - alphaValue, 245, 245, 245));
            canvas.drawText(line.getStr(), width, tempY, p);
            alphaValue += 25;
        }

    }


    /**
     * 当view大小改变的时候调用的方法
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        this.width = w;
//        this.height = h;
        super.onSizeChanged(w, h, oldw, oldh);
        width = w * 0.5f;
        height = h;
        middleHeight = h * 0.4f;
    }


}
