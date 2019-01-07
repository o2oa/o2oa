package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DensityUtil;

/**
 * 圆形内部显示文字的控件
 *
 */
public class CircleTextView extends View {

    private Bitmap mSrc;
    private int mWidth;
    private int mHeight;
    private int inColor;
    private int outColor;
    private int outStrokeWidth;
    private String mText;
    private int mTextColor;
    private float mTextSize;

    public CircleTextView(Context context) {
        this(context, null);
    }

    public CircleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleTextView);
        mSrc = BitmapFactory.decodeResource(getResources(), array.getResourceId(R.styleable.CircleTextView_c_icon, -1));
        if(mSrc==null){
            inColor = array.getColor(R.styleable.CircleTextView_c_inColor, -1);
        }
        outColor = array.getColor(R.styleable.CircleTextView_c_outColor, -1);
        outStrokeWidth = array.getDimensionPixelSize(R.styleable.CircleTextView_c_stroke, 0);
        mWidth = array.getDimensionPixelSize(R.styleable.CircleTextView_c_width, 0);
        mHeight = array.getDimensionPixelSize(R.styleable.CircleTextView_c_height, 0);
        mText = array.getString(R.styleable.CircleTextView_c_text);
        mTextSize = array.getDimensionPixelSize(R.styleable.CircleTextView_c_textSize, DensityUtil.sp2px(context, 18));
        mTextColor = array.getColor(R.styleable.CircleTextView_c_textColor, -1);
        array.recycle();
//        Log.d("CircleTextView", "mTextColor："+mTextColor+", mTextSize:"+mTextSize+", mText:"+mText+", inColor:"+inColor+", mWidth:"+mWidth);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int min = Math.min(mWidth, mHeight);
        if (mSrc != null) {
            mSrc = Bitmap.createScaledBitmap(mSrc, min, min, false);
            canvas.drawBitmap(createCircleImage(mSrc, min), 0, 0, null);
        }else{
            canvas.drawBitmap(createCircleImage(null, min), 0, 0, null);
        }
    }

    private Bitmap createCircleImage(Bitmap source, int min) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(min / 2, min / 2, min / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        if (source != null){
            canvas.drawBitmap(source, 0, 0, paint);
        }else{
            paint.setColor(inColor);
            canvas.drawCircle(min / 2, min / 2, min / 2, paint);
        }
        if (outColor != -1) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(outStrokeWidth);
            paint.setColor(outColor);
            canvas.drawCircle(min / 2, min / 2, min / 2, paint);
        }
        if(mText != null){
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(1);
            if(mTextColor != 0 ){
                paint.setColor(mTextColor);
            }
            if(mTextSize != 0 ){
                paint.setTextSize(mTextSize);
            }
            paint.setTextAlign(Paint.Align.CENTER);
            Paint.FontMetrics fm = paint.getFontMetrics();
            float fFontHeight = (float)Math.ceil(fm.descent - fm.ascent);
            canvas.drawText(mText, mWidth/2, mHeight/2+fFontHeight/4, paint);
        }

        return target;
    }

    public void setText(String string) {
        mText = string;
        invalidate();
        requestLayout();
    }


    public void setTextAndCircleColor(String text, int color) {
        mText = text;
        mSrc = null;
        inColor = color;
        postInvalidate();
        requestLayout();
    }

    public void setInColor(int color) {
        mSrc = null;
        inColor = color;
        postInvalidate();
        requestLayout();
    }

    @Override
    public String toString() {
        return "this is CircleTextView";
    }
}
