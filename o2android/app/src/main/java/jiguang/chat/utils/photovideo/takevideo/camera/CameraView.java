package jiguang.chat.utils.photovideo.takevideo.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;


public class CameraView extends View {
    /**
     * 动画时长
     */
    private static final int ANIM_MILS = 600;
    /**
     * 动画每多久刷新一次
     */
    private static final int ANIM_UPDATE = 30;
    /**
     * focus paint
     */
    private Paint paint, clearPaint;

    private int paintColor = Color.GREEN;
    /**
     * 进度订阅
     */
    private Subscription subscription;
    /**
     * focus rectf
     */
    private RectF rectF = new RectF();
    /**
     * focus size
     */
    private int focusSize = 120;

    private int lineSize = focusSize / 4;
    /**
     * 上一次两指距离
     */
    private float oldDist = 1f;
    /**
     * 画笔宽
     */
    private float paintWidth = 6.0f;
    /**
     * s
     */
    private float scale;

    public CameraView(Context context) {
        super(context);
        init();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(paintColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(paintWidth);

        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        if (event.getPointerCount() == 1 && action == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            setFoucsPoint(x, y);
            if (listener != null) {
                listener.handleFocus(x, y);
            }
        } else if (event.getPointerCount() >= 2){
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = getFingerSpacing(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float newDist = getFingerSpacing(event);
                    if (newDist > oldDist) {
                        if (this.listener != null) {
                            this.listener.handleZoom(true);
                        }
                    } else if (newDist < oldDist) {
                        if (this.listener != null) {
                            this.listener.handleZoom(false);
                        }
                    }
                    oldDist = newDist;
                    break;
            }
        }
        return true;
    }

    /**
     * 计算两点触控距离
     * @param event
     * @return
     */
    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 设置坐标点(坐标为rawX, rawY)
     */
    public void setFoucsPoint(PointF pointF) {
        PointF transPointF = transPointF(pointF, this);
        setFoucsPoint(transPointF.x, transPointF.y);
    }

    /**
     * 设置当前触摸点
     * @param x
     * @param y
     */
    private void setFoucsPoint(float x, float y) {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        rectF.set(x - focusSize, y - focusSize, x + focusSize, y + focusSize);
        final int count = ANIM_MILS / ANIM_UPDATE;
        subscription = Observable.interval(ANIM_UPDATE, TimeUnit.MILLISECONDS).take(count).subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                scale = 0;
                postInvalidate();
            }

            @Override
            public void onError(Throwable e) {
                scale = 0;
                postInvalidate();
            }

            @Override
            public void onNext(Long aLong) {
                float current = aLong== null ? 0 : aLong.longValue();
                scale = 1 - current / count;
                if (scale <= 0.5f) {
                    scale = 0.5f;
                }
                postInvalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (scale != 0) {
            float centerX = rectF.centerX();
            float centerY = rectF.centerY();
            canvas.scale(scale, scale, centerX, centerY);
            canvas.drawRect(rectF, paint);
            canvas.drawLine(rectF.left, centerY, rectF.left + lineSize, centerY, paint);
            canvas.drawLine(rectF.right, centerY, rectF.right - lineSize, centerY, paint);
            canvas.drawLine(centerX, rectF.top, centerX, rectF.top + lineSize, paint);
            canvas.drawLine(centerX, rectF.bottom, centerX, rectF.bottom - lineSize, paint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    /**
     * 根据raw坐标转换成屏幕中所在的坐标
     * @param pointF
     * @return
     */
    private PointF transPointF(PointF pointF, View view) {
        pointF.x -= view.getX();
        pointF.y -= view.getY();
        ViewParent parent = view.getParent();
        if (parent instanceof View) {
            return transPointF(pointF, (View) parent);
        } else {
            return pointF;
        }
    }

    private OnViewTouchListener listener;

    public void setOnViewTouchListener(OnViewTouchListener listener) {
        this.listener = listener;
    }

    public void removeOnZoomListener() {
        this.listener = null;
    }

    public interface OnViewTouchListener {
        /**
         * 对焦
         * @param x
         * @param y
         */
        void handleFocus(float x, float y);

        /**
         * 缩放
         * @param zoom true放大反之
         */
        void handleZoom(boolean zoom);

    }

}
