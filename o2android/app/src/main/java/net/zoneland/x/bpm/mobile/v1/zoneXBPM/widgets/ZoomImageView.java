package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog;

/**
 * 支持图片缩放 移动 的预览效果
 *
 * 例：
 * <net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.ZoomImageView
 *      android:layout_width="match_parent"
 *      android:layout_height="match_parent"
 *      android:scaleType="matrix"
 *      android:src="@mipmap/scarlett"/>
 *
 * Created by FancyLou on 2015/12/4.
 */
@RequiresApi(api = Build.VERSION_CODES.FROYO)
public class ZoomImageView extends ImageView implements ScaleGestureDetector.OnScaleGestureListener,
        View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {


    /**
     * 最大缩放比例
     */
    private static final float SCALE_MAX = 4.0f;
    private static final float SCALE_MID = 2.0f;
    /**
     * 初始化时的缩放比例，如果图片宽或高大于屏幕，此值将小于0
     */
    private float initScale = 1.0f;

    /**
     * 用于存放矩阵的9个值
     */
    private final float[] matrixValues = new float[9];

    /**
     * 初始化
     */
    private boolean once = true;

    /**
     * 缩放的手势检测
     */
    private ScaleGestureDetector mScaleGestureDetector = null;

    /**
     * 三维矩阵
     */
    private final Matrix mScaleMatrix = new Matrix();




    /**
     * 用于双击检测
     * 它可以捕获双击事件
     */
    private GestureDetector mGestureDetector;
    private boolean isAutoScale;

    private int mTouchSlop;

    private float mLastX;
    private float mLastY;

    private boolean isCanDrag;
    private int lastPointerCount;

    private boolean isCheckTopAndBottom = true;
    private boolean isCheckLeftAndRight = true;


    public ZoomImageView(Context context) {
        super(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setScaleType(ScaleType.MATRIX);
       /* 我们只需要onDoubleTap这个回调，所以我们准备使用它的一个内部类SimpleOnGestureListener，
            对接口的其他方法实现了空实现。*/
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener()
                {
                    @Override
                    public boolean onDoubleTap(MotionEvent e)
                    {
                        if (isAutoScale == true)
                            return true;

                        float x = e.getX();
                        float y = e.getY();
                        XLog.debug("DoubleTap"+ getScale() + " , " + initScale);
                        if (getScale() < SCALE_MID)
                        {
                            ZoomImageView.this.postDelayed(
                                    new AutoScaleRunnable(SCALE_MID, x, y), 16);
                            isAutoScale = true;
                        } else if (getScale() >= SCALE_MID
                                && getScale() < SCALE_MAX)
                        {
                            ZoomImageView.this.postDelayed(
                                    new AutoScaleRunnable(SCALE_MAX, x, y), 16);
                            isAutoScale = true;
                        } else
                        {
                            ZoomImageView.this.postDelayed(
                                    new AutoScaleRunnable(initScale, x, y), 16);
                            isAutoScale = true;
                        }

                        return true;
                    }
                });
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        this.setOnTouchListener(this);
    }

    /**
     * 自动缩放的任务
     *
     * 根据当前的缩放值，如果是小于2的，我们双击直接到变为原图的2倍；
     * 如果是2,4之间的，我们双击直接为原图的4倍；
     * 其他状态也就是4倍，双击后还原到最初的尺寸
     *
     */
    private class AutoScaleRunnable implements Runnable
    {
        static final float BIGGER = 1.07f;
        static final float SMALLER = 0.93f;
        private float mTargetScale;
        private float tmpScale;

        /**
         * 缩放的中心
         */
        private float x;
        private float y;

        /**
         * 传入目标缩放值，根据目标值与当前值，判断应该放大还是缩小
         *
         * @param targetScale
         */
        public AutoScaleRunnable(float targetScale, float x, float y)
        {
            this.mTargetScale = targetScale;
            this.x = x;
            this.y = y;
            if (getScale() < mTargetScale)
            {
                tmpScale = BIGGER;
            } else
            {
                tmpScale = SMALLER;
            }

        }

        @Override
        public void run()
        {
            // 进行缩放
            mScaleMatrix.postScale(tmpScale, tmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);

            final float currentScale = getScale();
            // 如果值在合法范围内，继续缩放
            if (((tmpScale > 1f) && (currentScale < mTargetScale))
                    || ((tmpScale < 1f) && (mTargetScale < currentScale)))
            {
                ZoomImageView.this.postDelayed(this, 16);
            } else
            // 设置为目标的缩放比例
            {
                final float deltaScale = mTargetScale / currentScale;
                mScaleMatrix.postScale(deltaScale, deltaScale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }

        }
    }


    /**
     * 我们在onGlobalLayout的回调中，
     * 根据图片的宽和高以及屏幕的宽和高，
     * 对图片进行缩放以及移动至屏幕的中心。
     * 如果图片很小，那就正常显示，不放大了~
     */
    @Override
    public void onGlobalLayout() {
        if (once){
            Drawable d = getDrawable();
            if (d == null)
                return;

            int width = getWidth();
            int height = getHeight();
            // 拿到图片的宽和高
            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();
            XLog.debug( "onGlobalLayout: dw="+ d.getIntrinsicWidth() + " , dh=" + d.getIntrinsicHeight()+", width="+width+", height="+height);
            float scale = 1.0f;
            // 如果图片的宽或者高大于屏幕，则缩放至屏幕的宽或者高
            if (dw > width && dh <= height)
            {
                scale = width * 1.0f / dw;
            }
            if (dh > height && dw <= width)
            {
                scale = height * 1.0f / dh;
            }
            // 如果宽和高都大于屏幕，则让其按按比例适应屏幕大小
            if (dw > width && dh > height)
            {
                scale = Math.min(width * 1.0f /dw, height * 1.0f / dh);
            }
            initScale = scale;
            // 图片移动至屏幕中心
            mScaleMatrix.postTranslate((width - dw) / 2, (height - dh) / 2);
            mScaleMatrix
                    .postScale(scale, scale, getWidth() / 2, getHeight() / 2);
            setImageMatrix(mScaleMatrix);
            once = false;
        }
    }

    /**
     * 在onScale的回调中对图片进行缩放的控制，首先进行缩放范围的判断，然后设置mScaleMatrix的scale值
     * 
     * @param detector
     * @return
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();
        if (getDrawable() == null) {
            return true;
        }

        /**
         * 缩放的范围控制
         */
        if ((scale < SCALE_MAX && scaleFactor > 1.0f)
                || (scale > initScale && scaleFactor < 1.0f))
        {
            /**
             * 最大值最小值判断
             */
            if (scaleFactor * scale < initScale)
            {
                scaleFactor = initScale / scale;
            }
            if (scaleFactor * scale > SCALE_MAX)
            {
                scaleFactor = SCALE_MAX / scale;
            }
            /**
             * 设置缩放比例
             */
//            mScaleMatrix.postScale(scaleFactor, scaleFactor, getWidth() / 2,
//                    getHeight() / 2);//只从屏幕中间缩放

            mScaleMatrix.postScale(scaleFactor, scaleFactor,
                    detector.getFocusX(), detector.getFocusY());//根据手指缩放的位置 作为中心点
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    /**
     * 我们让OnTouchListener的MotionEvent交给ScaleGestureDetector进行处理
     *
     * 关于图片移动的代码
     *
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //需要把我们的event传给它 检测双击事件
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        mScaleGestureDetector.onTouchEvent(event);

        //图片大于屏幕的时候才可以移动  下面是自由移动的代码
        float x=0, y=0;
        //拿到触摸点的个数
        final int pointerCount = event.getPointerCount();
        for (int i=0; i<pointerCount; i++){
            x += event.getX(i);
            y += event.getY(i);
        }

        x = x / pointerCount;
        y = y / pointerCount;

        /*
        * 每当触摸点发生变化时， 重置mLasx， mLasy
        * */
        if(pointerCount != lastPointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }

        lastPointerCount= pointerCount;
        RectF rectF = getMatrixRectF();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (rectF.width() > getWidth() || rectF.height() > getHeight())
                {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (rectF.width() > getWidth() || rectF.height() > getHeight())
                {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                XLog.debug( "onTouch: ACTION_MOVE");
                float dx = x - mLastX;
                float dy = y - mLastY;

                if (!isCanDrag)
                {
                    isCanDrag = isCanDrag(dx, dy);
                }
                if (isCanDrag)
                {
                    if (getDrawable() != null)
                    {
                        ////////////////到达边际的时候继续会释放给viewpager///////////////////////////
                         if (getMatrixRectF().left == 0 && dx > 0)
                         {
                            getParent().requestDisallowInterceptTouchEvent(false);
                         }
                         if (getMatrixRectF().right == getWidth() && dx < 0)
                         {
                            getParent().requestDisallowInterceptTouchEvent(false);
                         }
                        //////////////////////////////////////////////////////////////////
                        isCheckLeftAndRight = isCheckTopAndBottom = true;
                        // 如果宽度小于屏幕宽度，则禁止左右移动
                        if (rectF.width() < getWidth())
                        {
                            dx = 0;
                            isCheckLeftAndRight = false;
                        }
                        // 如果高度小于屏幕高度，则禁止上下移动
                        if (rectF.height() < getHeight())
                        {
                            dy = 0;
                            isCheckTopAndBottom = false;
                        }
                        mScaleMatrix.postTranslate(dx, dy);
                        checkMatrixBounds();
                        setImageMatrix(mScaleMatrix);
                    }
                }
                mLastX = x;
                mLastY = y;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                XLog.debug( "onTouch: ACTION_UP");
                lastPointerCount = 0;
                break;
        }

        return true;
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }


    /**
     * 获得当前的缩放比例
     *
     * @return
     */
    public final float getScale()
    {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    /**
     * 移动时，进行边界判断，主要判断宽或高大于屏幕的
     */
    private void checkMatrixBounds()
    {
        RectF rect = getMatrixRectF();

        float deltaX = 0, deltaY = 0;
        final float viewWidth = getWidth();
        final float viewHeight = getHeight();
        // 判断移动或缩放后，图片显示是否超出屏幕边界
        if (rect.top > 0 && isCheckTopAndBottom)
        {
            deltaY = -rect.top;
        }
        if (rect.bottom < viewHeight && isCheckTopAndBottom)
        {
            deltaY = viewHeight - rect.bottom;
        }
        if (rect.left > 0 && isCheckLeftAndRight) {
            deltaX = -rect.left;
        }
        if (rect.right < viewWidth && isCheckLeftAndRight)
        {
            deltaX = viewWidth - rect.right;
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 是否是推动行为
     *
     * @param dx
     * @param dy
     * @return
     */
    private boolean isCanDrag(float dx, float dy)
    {
        return Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
    }


    /**
     * 随意的中心点放大、缩小，会导致图片的位置的变化，最终导致，图片宽高大于屏幕时，图片与屏幕间出现白边；图片小于屏幕，但是不居中
     * 所以要控制范围
     *
     * 在缩放时，进行图片显示范围的控制
     */
    private void checkBorderAndCenterWhenScale()
    {

        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        // 如果宽或高大于屏幕，则控制范围
        if (rect.width() >= width)
        {
            if (rect.left > 0)
            {
                deltaX = -rect.left;
            }
            if (rect.right < width)
            {
                deltaX = width - rect.right;
            }
        }
        if (rect.height() >= height)
        {
            if (rect.top > 0)
            {
                deltaY = -rect.top;
            }
            if (rect.bottom < height)
            {
                deltaY = height - rect.bottom;
            }
        }
        // 如果宽或高小于屏幕，则让其居中
        if (rect.width() < width)
        {
            deltaX = width * 0.5f - rect.right + 0.5f * rect.width();
        }
        if (rect.height() < height)
        {
            deltaY = height * 0.5f - rect.bottom + 0.5f * rect.height();
        }
        XLog.debug( "checkBorderAndCenterWhenScale： deltaX = " + deltaX + " , deltaY = " + deltaY);

        mScaleMatrix.postTranslate(deltaX, deltaY);

    }

    /**
     * 根据当前图片的Matrix获得图片的范围
     *
     * @return
     */
    private RectF getMatrixRectF()
    {
        Matrix matrix = mScaleMatrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d)
        {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }
}
