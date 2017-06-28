package com.lxw.customviewproject;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;


/**
 * 自定义View 优惠券控件
 * Created by Zion on 2017/6/27.
 */

public class CouponView extends View {

    // 自定义属性
    protected String couponText;// 标题文案
    protected int couponRadiusSize;// 勾选圆圈的半径
    protected int couponCircleWidthSize;// 不勾选圆环的宽
    protected int couponCircleColor;// 不勾选圆环的颜色
    protected int couponBackgroundRadiusSize;// 背景圆角的半径
    protected int couponMarginSpace;// 勾选圆圈和标题文案的间距
    protected int couponBackgroundColor;// 优惠券不勾选状态的背景
    protected int couponSelectBackgroundColor;// 优惠券勾选状态的背景
    protected int couponTextColor;// 标题的颜色
    protected int couponTextSize;// 标题字体大小
    protected boolean couponSelect;// 是否勾选

    // 自定义属性默认值
    protected static final int DEFAULT_COUPON_RADIUS_SIZE = 20;// 默认勾选圆圈的半径
    protected static final int DEFAULT_COUPON_MARGIN_SPACE = 10;// 默认勾选圆圈和标题文案的间距
    protected static final int DEFAULT_COUPON_BACKGROUND_COLOR = Color.GRAY;// 默认不勾选优惠券的背景
    protected static final int DEFAULT_COUPON_SELECT_BACKGROUND_COLOR = Color.RED;// 默认不勾选优惠券的背景
    protected static final int DEFAULT_COUPON_TEXT_COLOR = Color.WHITE;// 默认标题的颜色
    protected static final int DEFAULT_COUPON_TEXT_SIZE = 16;// 默认标题字体大小
    protected static final int DEFAULT_COUPON_BACKGROUND_RADIUS_SIZE = 5;// 默认背景圆角的半径
    protected static final int DEFAULT_COUPON_CIRCLE_COLOR = Color.BLACK;// 默认圆环颜色
    protected static final int DEFAULT_COUPON_CIRCLE_WIDTH_SIZE = 3;// 默认圆环宽度

    protected int width;// 优惠券控件的宽
    protected int height;// 优惠券的高
    protected Context context;// 上下文
    protected Paint paint;// 画笔
    protected Paint circlePaint;// 画笔
    protected Path path;// 画勾选状态的勾
    protected Rect textBound;// 矩形区域
    protected OnClickListener listener;// 矩形区域

    public CouponView(Context context) {
        this(context, null);
    }

    public CouponView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CouponView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CouponView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CouponView, 0, 0);
        couponText = a.getString(R.styleable.CouponView_couponText);
        couponRadiusSize = a.getDimensionPixelSize(R.styleable.CouponView_couponRadiusSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_COUPON_RADIUS_SIZE, getResources().getDisplayMetrics()));
        couponBackgroundRadiusSize = a.getDimensionPixelSize(R.styleable.CouponView_couponBackgroundRadiusSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_COUPON_BACKGROUND_RADIUS_SIZE, getResources().getDisplayMetrics()));
        couponCircleWidthSize = a.getDimensionPixelSize(R.styleable.CouponView_couponCircleWidthSise, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_COUPON_CIRCLE_WIDTH_SIZE, getResources().getDisplayMetrics()));
        couponMarginSpace = a.getDimensionPixelSize(R.styleable.CouponView_couponMarginSpace, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_COUPON_MARGIN_SPACE, getResources().getDisplayMetrics()));
        couponTextSize = a.getDimensionPixelSize(R.styleable.CouponView_couponTextSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                DEFAULT_COUPON_TEXT_SIZE, getResources().getDisplayMetrics()));
        couponBackgroundColor = a.getColor(R.styleable.CouponView_couponBackgroundColor, DEFAULT_COUPON_BACKGROUND_COLOR);
        couponSelectBackgroundColor = a.getColor(R.styleable.CouponView_couponSelectBackgroundColor, DEFAULT_COUPON_SELECT_BACKGROUND_COLOR);
        couponTextColor = a.getColor(R.styleable.CouponView_couponTextColor, DEFAULT_COUPON_TEXT_COLOR);
        couponCircleColor = a.getColor(R.styleable.CouponView_couponCircleColor, DEFAULT_COUPON_CIRCLE_COLOR);
        couponSelect = a.getBoolean(R.styleable.CouponView_couponSelect, false);
        a.recycle();
        if (TextUtils.isEmpty(couponText)) {
            couponText = "";
        }
        // 用于测量文案的宽高
        paint = new Paint();
        path = new Path();
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(ValueUtil.dip2px(context, DEFAULT_COUPON_CIRCLE_WIDTH_SIZE));
        textBound = new Rect();
        paint.setTextSize(couponTextSize);
        // 点击事件绑定
        listener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                couponSelect = !couponSelect;
                invalidate();
            }
        };
        this.setOnClickListener(listener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 设置宽度
         */
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            width = specSize;
        } else {
            // 自适应宽
            int desire = getPaddingLeft() + getPaddingRight() + couponRadiusSize * 2 +
                    couponMarginSpace + textBound.width();
            if (specMode == MeasureSpec.AT_MOST) {
                width = Math.min(desire, specSize);
            }
        }

        /***
         * 设置高度
         */

        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            height = specSize;
        } else {
            // 自适应高
            int desire = getPaddingTop() + getPaddingBottom() + Math.max(couponRadiusSize * 2,
                    textBound.height());
            if (specMode == MeasureSpec.AT_MOST) {
                height = Math.min(desire, specSize);
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制背景
        if(couponSelect){
            paint.setColor(couponSelectBackgroundColor);
        }else{
            paint.setColor(couponBackgroundColor);
        }
        RectF backgroundRect = new RectF(0, 0, width, height);
        canvas.drawRoundRect(backgroundRect, couponBackgroundRadiusSize, couponBackgroundRadiusSize, paint);

        // 绘制勾选效果
        if (couponSelect) {
            // 绘制实心圆
            paint.setColor(couponTextColor);
            canvas.drawCircle(getPaddingLeft() + couponRadiusSize, height / 2, couponRadiusSize, paint);
            //画第一根线
            circlePaint.setColor(couponSelectBackgroundColor);
            int line1StartX = getPaddingLeft() + couponRadiusSize * 4 / 9;
            int line1StartY = height / 2;
            int line1EndX = getPaddingLeft() + couponRadiusSize;
            int line1EndY = getPaddingTop() + (height - getPaddingTop() - getPaddingBottom() - couponRadiusSize * 2) / 2 + couponRadiusSize * 3 / 2;
//            canvas.drawLine(line1StartX , line1StartY, line1EndX, line1EndY, circlePaint);
            //画第二根线
            int line2EndX = getPaddingLeft() + couponRadiusSize * 5 / 3;
            int line2EndY = getPaddingTop() + (height - getPaddingTop() - getPaddingBottom() - couponRadiusSize * 2) / 2 + couponRadiusSize * 2 / 3;
//            canvas.drawLine(line1EndX - ValueUtil.dip2px(context, DEFAULT_COUPON_CIRCLE_WIDTH_SIZE) / 2 , line1EndY + ValueUtil.dip2px(context, DEFAULT_COUPON_CIRCLE_WIDTH_SIZE) / 2, line2EndX, line2EndY, circlePaint);
            //path的起始位置
            path.moveTo(line1StartX, line1StartY);
            //从起始位置划线到坐标
            path.lineTo(line1EndX, line1EndY);
            //从起始位置划线到坐标
            path.lineTo(line2EndX, line2EndY);
            //绘制path路径
            canvas.drawPath(path, circlePaint);
        } else {
            // 绘制圆环
            circlePaint.setColor(couponCircleColor);
            int left = getPaddingLeft();
            int right = getPaddingLeft() + couponRadiusSize * 2;
            int top = getPaddingTop() + (height - getPaddingTop() - getPaddingBottom() - couponRadiusSize * 2) / 2;
            int bottom = height - getPaddingBottom() - (height - getPaddingTop() - getPaddingBottom() - couponRadiusSize * 2) / 2;
            canvas.drawArc(new RectF(left, top, right, bottom), 0, 360, false, circlePaint);
        }

        // 绘制标题
        // 计算标题的基准线
        paint.setColor(couponTextColor);
        paint.setStyle(Paint.Style.FILL);
        paint.getTextBounds(couponText, 0, couponText.length(), textBound);
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(couponText, getPaddingLeft() + couponRadiusSize * 2 +
                couponMarginSpace, baseline, paint);
    }

    public String getCouponText() {
        return couponText;
    }

    public void setCouponText(String couponText) {
        this.couponText = couponText;
        invalidate();
    }

    public int getCouponRadiusSize() {
        return couponRadiusSize;
    }

    public void setCouponRadiusSize(int couponRadiusSize) {
        this.couponRadiusSize = couponRadiusSize;
        invalidate();
    }

    public int getCouponBackgroundRadiusSize() {
        return couponBackgroundRadiusSize;
    }

    public void setCouponBackgroundRadiusSize(int couponBackgroundRadiusSize) {
        this.couponBackgroundRadiusSize = couponBackgroundRadiusSize;
        invalidate();
    }

    public int getCouponMarginSpace() {
        return couponMarginSpace;
    }

    public void setCouponMarginSpace(int couponMarginSpace) {
        this.couponMarginSpace = couponMarginSpace;
        invalidate();
    }

    public int getCouponBackgroundColor() {
        return couponBackgroundColor;
    }

    public void setCouponBackgroundColor(int couponBackgroundColor) {
        this.couponBackgroundColor = couponBackgroundColor;
        invalidate();
    }

    public int getCouponTextColor() {
        return couponTextColor;
    }

    public void setCouponTextColor(int couponTextColor) {
        this.couponTextColor = couponTextColor;
        invalidate();
    }

    public int getCouponTextSize() {
        return couponTextSize;
    }

    public void setCouponTextSize(int couponTextSize) {
        this.couponTextSize = couponTextSize;
        invalidate();
    }

    public boolean isCouponSelect() {
        return couponSelect;
    }

    public void setCouponSelect(boolean couponSelect) {
        this.couponSelect = couponSelect;
        invalidate();
    }

    public int getCouponCircleWidthSize() {
        return couponCircleWidthSize;
    }

    public void setCouponCircleWidthSize(int couponCircleWidthSize) {
        this.couponCircleWidthSize = couponCircleWidthSize;
        invalidate();
    }

    public int getCouponCircleColor() {
        return couponCircleColor;
    }

    public void setCouponCircleColor(int couponCircleColor) {
        this.couponCircleColor = couponCircleColor;
        invalidate();
    }

    public int getCouponSelectBackgroundColor() {
        return couponSelectBackgroundColor;
    }

    public void setCouponSelectBackgroundColor(int couponSelectBackgroundColor) {
        this.couponSelectBackgroundColor = couponSelectBackgroundColor;
        invalidate();
    }

    public OnClickListener getListener() {
        return listener;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
        this.setOnClickListener(listener);
    }
}
