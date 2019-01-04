package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

/**
 * Created by fancy on 2017/2/22.
 */

public class ChangeColorIconWithText extends View {

    public static final int DEFAULT_COLOR = 0xFF666666;
    public static final int DEFAULT_HIGHLIGHT_COLOR = 0xFFFB4747;


    private int mColor = DEFAULT_HIGHLIGHT_COLOR;
    private Bitmap mIconBitmap;
    private Bitmap mIconCompletedBitmap;
    private String mText = "菜单";
    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());


    /**
     * 绘图过程使用的变量
     */
    private Canvas mCanvas;
    private Bitmap mBitmap;
    private Paint mPaint;
    private float mAlpha;//透明度
    private Rect mIconRect;//
    private Rect mTextBound;
    private Paint mTextPaint;


    public ChangeColorIconWithText(Context context) {
        this(context, null);
    }

    public ChangeColorIconWithText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChangeColorIconWithText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChangeColorIconWithText);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.ChangeColorIconWithText_icon:
                    BitmapDrawable drawable = (BitmapDrawable) a.getDrawable(attr);
                    mIconBitmap = drawable.getBitmap();
                    break;
                case R.styleable.ChangeColorIconWithText_iconCompleted: {
                    BitmapDrawable drawableCompleted = (BitmapDrawable) a.getDrawable(attr);
                    mIconCompletedBitmap = drawableCompleted.getBitmap();
                     break;
                }
                case R.styleable.ChangeColorIconWithText_color:
                    mColor = a.getColor(attr, DEFAULT_HIGHLIGHT_COLOR);
                    break;
                case R.styleable.ChangeColorIconWithText_text:
                    mText = a.getString(attr);
                    break;
                case R.styleable.ChangeColorIconWithText_textSize:
                    mTextSize = (int) a.getDimension(attr, TypedValue
                            .applyDimension(TypedValue.COMPLEX_UNIT_SP, 12,
                                    getResources().getDisplayMetrics()));
                    break;
            }

        }
        a.recycle();

        //在构造里先获取文本的bound 是为了后面onMeasure函数中使用计算icon高度的
        mTextBound = new Rect();
        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(DEFAULT_COLOR);//默认灰色
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //icon是正方形 通过view的高宽 比较 小的那个作为icon的宽高
        int mIconWidth = Math.min(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - mTextBound.height());
        //绘制icon的left 和 top
        int left = getMeasuredWidth() / 2 - mIconWidth / 2;
        int top = (getMeasuredHeight() - mTextBound.height()) / 2 - mIconWidth / 2;

        mIconRect = new Rect(left, top, left + mIconWidth, top + mIconWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int alpha = (int) Math.ceil(255 * mAlpha);//paint 使用的alpha是 0-255

        if (mIconCompletedBitmap!=null && alpha > 250) {
            //绘制icon
            canvas.drawBitmap(mIconCompletedBitmap, null, mIconRect, null);
            drawSourceText(canvas, alpha);
            drawTargetText(canvas, alpha);
        }else {
            //绘制icon
            canvas.drawBitmap(mIconBitmap, null, mIconRect, null);
            //mBitmap setAlpha 设置背景纯色 xfermode 图标覆盖
            setupTargetBitmap(alpha);
            drawSourceText(canvas, alpha);
            drawTargetText(canvas, alpha);
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    private static final String INSTANCE_STATUS = "instance_status";//存储上级保存的 state
    private static final String STATUS_ALPHA = "status_alpha"; //存储alpha

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState());
        bundle.putFloat(STATUS_ALPHA, mAlpha);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mAlpha = bundle.getFloat(STATUS_ALPHA);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    private void drawTargetText(Canvas canvas, int alpha) {
        mTextPaint.setColor(mColor);
        mTextPaint.setAlpha(alpha);
        int x = getMeasuredWidth() / 2 - mTextBound.width() / 2;
        int y = mIconRect.bottom + mTextBound.height();
        canvas.drawText(mText, x, y, mTextPaint);
    }

    /**
     * 绘制原文本
     *
     * @param canvas
     * @param alpha
     */
    private void drawSourceText(Canvas canvas, int alpha) {
        mTextPaint.setColor(DEFAULT_COLOR);
        mTextPaint.setAlpha(255 - alpha);
        int x = getMeasuredWidth() / 2 - mTextBound.width() / 2;
        int y = mIconRect.bottom + mTextBound.height() + 4;//多加4个像素
        canvas.drawText(mText, x, y, mTextPaint);
    }

    private void setupTargetBitmap(int alpha) {
        mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setAlpha(alpha);
        //设置纯色
        mCanvas.drawRect(mIconRect, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setAlpha(255);
        mCanvas.drawBitmap(mIconBitmap, null, mIconRect, mPaint);
    }


    /**
     * 动态设置颜色变化
     *
     * @param alpha
     */
    public void setIconAlpha(float alpha) {
        this.mAlpha = alpha;
        invalidateView();
    }

    /**
     * 设置文字颜色
     * @param color
     */
    public void setMColor(@ColorInt int color) {
        if (color==-1) {
            return;
        }
        this.mColor = color;
        invalidateView();
    }

    /**
     * 设置图标
     * @param bitmap
     */
    public void setMIconBitmap(@NonNull Bitmap bitmap) {
        this.mIconBitmap = bitmap;
        invalidateView();
    }

    /**
     * 设置完成后的图标
     * @param bitmap
     */
    public void setMIconCompletedBitmap(@NonNull Bitmap bitmap) {
        this.mIconCompletedBitmap = bitmap;
        invalidateView();
    }

    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) { //是否在UI下
            invalidate();
        } else {
            postInvalidate();
        }

    }
}
