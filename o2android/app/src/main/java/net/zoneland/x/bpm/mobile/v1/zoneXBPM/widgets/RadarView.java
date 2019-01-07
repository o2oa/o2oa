package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

/**
 * Created by FancyLou on 2016/11/9.
 */

public class RadarView extends View {

    private Context mContext;
    private boolean isSearching = false;// 标识是否处于扫描状态,默认为不在扫描状态
    private Paint mPaint;// 画笔
    private Bitmap mScanBmp;// 执行扫描运动的图片
    private int mOffsetArgs = 0;// 扫描运动偏移量参数
    private int mWidth, mHeight;// 宽高
    int mOutWidth;// 外圆宽度(w/4/5*2=w/10)
    int mCx, mCy;// x、y轴中心点
    int mOutsideRadius, mInsideRadius;// 外、内圆半径


    public RadarView(Context context) {
        super(context);
        init(context);
    }

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        this.mContext = context;

    }

    /**
     * 测量视图及其内容,以确定所测量的宽度和高度(测量获取控件尺寸).
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取控件区域宽高
        if (mWidth == 0 || mHeight == 0) {
            final int minimumWidth = getSuggestedMinimumWidth();
            final int minimumHeight = getSuggestedMinimumHeight();
            mWidth = resolveMeasured(widthMeasureSpec, minimumWidth);
            mHeight = resolveMeasured(heightMeasureSpec, minimumHeight);
            mScanBmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                    mContext.getResources(), R.mipmap.radar_scan), mWidth
                    - mOutWidth, mWidth - mOutWidth, false);

            // 获取x/y轴中心点
            mCx = mWidth / 2;
            mCy = mHeight / 2;

            // 获取外圆宽度
            mOutWidth = mWidth / 10;

            // 计算内、外半径
            mOutsideRadius = mWidth / 2;// 外圆的半径
            mInsideRadius = (mWidth - mOutWidth) / 4 / 2;// 内圆的半径,除最外层,其它圆的半径=层数*insideRadius
        }
    }

    /**
     * 绘制视图--从外部向内部绘制
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int innerColor = mContext.getResources().getColor(R.color.load_blue);
        int outColor = mContext.getResources().getColor(R.color.icon_light_blue);
        // 开始绘制最外层的圆
        mPaint.setAntiAlias(true);// 设置抗锯齿
        mPaint.setStyle(Paint.Style.FILL);// 设置填充样式
        mPaint.setColor(outColor);// 设置画笔颜色
        // 1.开始绘制圆形
        canvas.drawCircle(mCx, mCy, mOutsideRadius, mPaint);

        // 开始绘制内4圆
        mPaint.setColor(innerColor);
        canvas.drawCircle(mCx, mCy, mInsideRadius * 4, mPaint);

        /**
        // 开始绘制内3圆
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0xffe7e9fd);//网格线颜色
        canvas.drawCircle(mCx, mCy, mInsideRadius * 3, mPaint);

        // 开始绘制内2圆
        canvas.drawCircle(mCx, mCy, mInsideRadius * 2, mPaint);

        // 开始绘制内1圆
        canvas.drawCircle(mCx, mCy, mInsideRadius * 1, mPaint);

        // 2.开始绘制对角线
        canvas.drawLine(mOutWidth / 2, mCy, mWidth - mOutWidth / 2, mCy, mPaint);// 绘制0°~180°对角线
        canvas.drawLine(mCx, mHeight - mOutWidth / 2, mCx, mOutWidth / 2,
                mPaint);// 绘制90°~270°对角线

        // 根据角度绘制对角线
        int startX, startY, endX, endY;
        double radian;

        // 绘制45°~225°对角线
        // 计算开始位置x/y坐标点
        radian = Math.toRadians((double) 45);// 将角度转换为弧度
        startX = (int) (mCx + mInsideRadius * 4 * Math.cos(radian));// 通过圆心坐标、半径和当前角度计算当前圆周的某点横坐标
        startY = (int) (mCy + mInsideRadius * 4 * Math.sin(radian));// 通过圆心坐标、半径和当前角度计算当前圆周的某点纵坐标
        // 计算结束位置x/y坐标点
        radian = Math.toRadians((double) 45 + 180);
        endX = (int) (mCx + mInsideRadius * 4 * Math.cos(radian));
        endY = (int) (mCy + mInsideRadius * 4 * Math.sin(radian));
        canvas.drawLine(startX, startY, endX, endY, mPaint);

        // 绘制135°~315°对角线
        // 计算开始位置x/y坐标点
        radian = Math.toRadians((double) 135);
        startX = (int) (mCx + mInsideRadius * 4 * Math.cos(radian));
        startY = (int) (mCy + mInsideRadius * 4 * Math.sin(radian));
        // 计算结束位置x/y坐标点
        radian = Math.toRadians((double) 135 + 180);
        endX = (int) (mCx + mInsideRadius * 4 * Math.cos(radian));
        endY = (int) (mCy + mInsideRadius * 4 * Math.sin(radian));
        canvas.drawLine(startX, startY, endX, endY, mPaint);

         **/
        // 3.绘制扫描扇形图
        canvas.save();// 用来保存Canvas的状态.save之后，可以调用Canvas的平移、放缩、旋转、错切、裁剪等操作.

        if (isSearching) {// 判断是否处于扫描
            canvas.rotate(mOffsetArgs, mCx, mCy);// 绘制旋转角度,参数一：角度;参数二：x中心;参数三：y中心.
            canvas.drawBitmap(mScanBmp, mCx - mScanBmp.getWidth() / 2, mCy
                    - mScanBmp.getHeight() / 2, null);// 绘制Bitmap扫描图片效果
            mOffsetArgs += 3;
        } else {
//            canvas.drawBitmap(mScanBmp, mCx - mScanBmp.getWidth() / 2, mCy
//                    - mScanBmp.getHeight() / 2, null);
        }

        // 4.开始绘制动态点
        canvas.restore();// 用来恢复Canvas之前保存的状态.防止save后对Canvas执行的操作对后续的绘制有影响.

        if (isSearching)
            this.invalidate();
    }

    /**
     * TODO<设置扫描状态>
     *
     * @return void
     */
    public void setSearching(boolean status) {
        this.isSearching = status;
        this.invalidate();
    }

    public boolean isSearching() {
        return isSearching;
    }

    /**
     * TODO<解析获取控件宽高>
     *
     * @return int
     */
    private int resolveMeasured(int measureSpec, int desired) {
        int result = 0;
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                result = desired;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(specSize, desired);
                break;
            case MeasureSpec.EXACTLY:
            default:
                result = specSize;
        }
        return result;
    }

}
