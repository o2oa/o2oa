package jiguang.chat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${chenyn} on 2017/7/13.
 */

public class TipView extends View {

    private static final int STATUS_DOWN = 1;
    private static final int STATUS_UP = 2;
    // 初始窗口的Item显示在上方
    private int mStatus = STATUS_UP;
    // 分隔Item之间的线
    private int mSeparateLineColor;
    // 最边上两个方块的边角半径大小
    private int mCorner = dip2px(6);
    private Paint mPaint; // 画方块和文字的画笔
    private Paint doPaint; // 画三角的画笔
    private Path mPath; // 绘制的路径
    // 提示窗口与屏幕（根布局）的最小距离
    private int mBorderMargin = dip2px(5);
    // 窗口Item的宽度
    private int mItemWidth = dip2px(50);
    // 窗口Item的高度
    private int mItemHeight = dip2px(38);
    // 三角小标的顶点
    private int mTriangleTop = dip2px(40);
    // 三角小标的半宽
    private int mHalfTriangleWidth = dip2px(6);
    // 三角小标与窗口Item的临界
    private int mItemBorder;
    // 窗口的left值
    private int realLeft;
    // 存储每个Item的信息
    private List<TipItem> mItemList = new ArrayList<>();
    // 存储每个方块
    private List<Rect> mItemRectList = new ArrayList<>();
    // Item点击接口
    private OnItemClickListener onItemClickListener;
    // 是否有Item被按下，是为Item的序号，否为-1
    private int choose = -1;
    // 外界出入的x坐标
    private int x;
    // 外界传入的y坐标
    private int y;

    public TipView(Context context, ViewGroup rootView, int x, int y, List<TipItem> mItemList) {
        super(context);
        // 设置传入过来的x轴坐标
        this.x = x;
        // 设置传入过来的y轴坐标
        this.y = y - mItemHeight - mBorderMargin; // x和y决定了三角下标的位置
        // 初始化画笔
        initPaint();
        // 初始化Item集合，并对Item的字符串长度进行处理
        setTipItemList(mItemList);
        // 将TipView实例添加到传入的rootView中。
        addView(rootView);
        // 根据传入的点击坐标x、y进行状态判断和最左位置的处理
        initView();
    }

    private void initPaint() {
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(sp2px(14));

        doPaint = new Paint();
        doPaint.setAntiAlias(true);
        doPaint.setStyle(Paint.Style.FILL);
        doPaint.setColor(Color.DKGRAY);

    }

    private void initView() {
        // 获取屏幕宽度
        int mScreenWidth = getResources().getDisplayMetrics().widthPixels;

        // 点击范围比较小,弹出框显示在下面
        if (y / 2 < mItemHeight) {
            // 令窗口在下方显示
            mStatus = STATUS_DOWN;
            // 设置三角下标的顶点
            mTriangleTop = y + dip2px(6);
            // 设置三角下标和窗口方块的交界处
            mItemBorder = mTriangleTop + dip2px(7);
        } else {
            mStatus = STATUS_UP;   // 同理如上
            mTriangleTop = y - dip2px(6);
            mItemBorder = mTriangleTop - dip2px(7);
        }

        // 获取当Item数量两边对称时，窗口的Item值：
        realLeft = x - (mItemWidth * mItemList.size()) / 2;
        if (realLeft < 0) {
            // 跑出屏幕左边的话,令值为距离值
            realLeft = mBorderMargin;
            // 防止三角下标与方块分离
            if (x - mCorner <= realLeft) x = realLeft + mCorner * 2;
        } else if (realLeft + (mItemWidth * mItemList.size()) > mScreenWidth) {
            // 跑出屏幕右边的话，则减去溢出的宽度，再减去距离值
            realLeft -= realLeft + (mItemWidth * mItemList.size()) - mScreenWidth + mBorderMargin;
            // 防止三角下标与方块分离
            if (x + mCorner >= realLeft + mItemWidth * mItemList.size())
                x = realLeft + mItemWidth * mItemList.size() - mCorner * 2;
        }
    }

    private void addView(ViewGroup rootView) {
        rootView.addView(this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 令背景为透明
        drawBackground(canvas);
        switch (mStatus) {
            case STATUS_DOWN:
                // 向下绘制窗口
                drawItemDown(canvas);
                break;
            case STATUS_UP:
                // 向上绘制窗口
                drawItemUp(canvas);
                break;
            default:
                break;
        }
    }

    private void drawItemDown(Canvas canvas) {
        // 清理方块集合
        mItemRectList.clear();
        mPath.reset(); // 清理路径
        // 绘制三角下标：根据是否有Item被点击绘制不同颜色
        if (choose != -1)
            doPaint.setColor(Color.DKGRAY);
        else
            doPaint.setColor(Color.BLACK);

        mPath.moveTo(x, mTriangleTop);
        mPath.lineTo(x - mHalfTriangleWidth, mItemBorder);
        mPath.lineTo(x + mHalfTriangleWidth, mItemBorder);
        canvas.drawPath(mPath, doPaint);

        for (int i = 0; i < mItemList.size(); i++) {
            // 当有方块Item被点击时，绘制不同颜色
            if (choose == i) {
                mPaint.setColor(Color.DKGRAY);
            } else {
                mPaint.setColor(Color.BLACK);
            }

            // 绘制第一个方块
            if (i == 0) {
                mPath.reset();
                mPath.moveTo(realLeft + mItemWidth, mItemBorder);
                mPath.lineTo(realLeft + mCorner, mItemBorder);
                mPath.quadTo(realLeft, mItemBorder
                        , realLeft, mItemBorder + mCorner);
                mPath.lineTo(realLeft, mItemBorder + mItemHeight - mCorner);
                mPath.quadTo(realLeft, mItemBorder + mItemHeight
                        , realLeft + mCorner, mItemBorder + mItemHeight);
                mPath.lineTo(realLeft + mItemWidth, mItemBorder + mItemHeight);
                canvas.drawPath(mPath, mPaint);
                // 绘制第一条分隔线
                mPaint.setColor(mSeparateLineColor);
                canvas.drawLine(realLeft + mItemWidth, mItemBorder
                        , realLeft + mItemWidth, mItemBorder + mItemHeight, mPaint);

            } else if (i == mItemList.size() - 1) {
                // 绘制最后一个方块
                mPath.reset();
                mPath.moveTo(realLeft + mItemWidth * (mItemList.size() - 1), mItemBorder);
                mPath.lineTo(realLeft + mItemWidth * (mItemList.size() - 1) + mItemWidth - mCorner, mItemBorder);
                mPath.quadTo(realLeft + mItemWidth * (mItemList.size() - 1) + +mItemWidth, mItemBorder
                        , realLeft + mItemWidth * (mItemList.size() - 1) + mItemWidth, mItemBorder + mCorner);
                mPath.lineTo(realLeft + mItemWidth * (mItemList.size() - 1) + mItemWidth, mItemBorder + mItemHeight - mCorner);
                mPath.quadTo(realLeft + mItemWidth * (mItemList.size() - 1) + mItemWidth, mItemBorder + mItemHeight,
                        realLeft + mItemWidth * (mItemList.size() - 1) + mItemWidth - mCorner, mItemBorder + mItemHeight);
                mPath.lineTo(realLeft + mItemWidth * (mItemList.size() - 1), mItemBorder + mItemHeight);
                canvas.drawPath(mPath, mPaint);
                // 最后一个方块不需要绘制分割线
            } else {

                // 绘制中间方块
                canvas.drawRect(realLeft + mItemWidth * (mItemList.size() - 1) - (mItemList.size() - (i + 1)) * mItemWidth, mItemBorder
                        , realLeft + mItemWidth * (mItemList.size() - 1) - (mItemList.size() - (i + 1)) * mItemWidth + mItemWidth, mItemBorder + mItemHeight, mPaint);
                // 绘制其他分隔线
                mPaint.setColor(mSeparateLineColor);
                canvas.drawLine(realLeft + mItemWidth * (mItemList.size() - 1) - (mItemList.size() - (i + 1)) * mItemWidth + mItemWidth, mItemBorder
                        , realLeft + mItemWidth * (mItemList.size() - 1) - (mItemList.size() - (i + 1)) * mItemWidth + mItemWidth, mItemBorder + mItemHeight, mPaint);
            }
            // 添加方块到方块集合
            mItemRectList.add(new Rect(realLeft + mItemWidth * (mItemList.size() - 1) - (mItemList.size() - (i + 1)) * mItemWidth, mItemBorder, realLeft + mItemWidth * (mItemList.size() - 1) - (mItemList.size() - (i + 1)) * mItemWidth + mItemWidth, mItemBorder + mItemHeight));
        }

        // 绘制方块上文字
        drawTitle(canvas);
    }


    public void clean() {
        mItemRectList.clear();
        mPath.reset();
        doPaint.reset();
        x = 0;
        y = 0;
    }

    // 可参考drawItemDown
    private void drawItemUp(Canvas canvas) {
        mItemRectList.clear();

        mPath.reset();
        if (choose != -1)
            doPaint.setColor(Color.DKGRAY);
        else
            doPaint.setColor(Color.BLACK);

        mPath.moveTo(x, mTriangleTop);
        mPath.lineTo(x - mHalfTriangleWidth, mItemBorder);
        mPath.lineTo(x + mHalfTriangleWidth, mItemBorder);
        canvas.drawPath(mPath, doPaint);

        for (int i = 0; i < mItemList.size(); i++) {

            if (choose == i) {
                mPaint.setColor(Color.DKGRAY);
            } else {
                mPaint.setColor(Color.BLACK);
            }


            if (i == 0) {
                mPath.reset();
                mPath.moveTo(realLeft + mItemWidth, mItemBorder - mItemHeight);
                mPath.lineTo(realLeft + mCorner, mItemBorder - mItemHeight);
                mPath.quadTo(realLeft, mItemBorder - mItemHeight
                        , realLeft, mItemBorder - mItemHeight + mCorner);
                mPath.lineTo(realLeft, mItemBorder - mCorner);
                mPath.quadTo(realLeft, mItemBorder
                        , realLeft + mCorner, mItemBorder);
                mPath.lineTo(realLeft + mItemWidth, mItemBorder);
                canvas.drawPath(mPath, mPaint);

                mPaint.setColor(mSeparateLineColor);
                canvas.drawLine(realLeft + mItemWidth, mItemBorder - mItemHeight
                        , realLeft + mItemWidth, mItemBorder, mPaint);

            } else if (i == mItemList.size() - 1) {
                mPath.reset();
                mPath.moveTo(realLeft + mItemWidth * (mItemList.size() - 1), mItemBorder - mItemHeight);
                mPath.lineTo(realLeft + mItemWidth * (mItemList.size() - 1) + mItemWidth - mCorner, mItemBorder - mItemHeight);
                mPath.quadTo(realLeft + mItemWidth * (mItemList.size() - 1) + +mItemWidth, mItemBorder - mItemHeight
                        , realLeft + mItemWidth * (mItemList.size() - 1) + mItemWidth, mItemBorder - mItemHeight + mCorner);
                mPath.lineTo(realLeft + mItemWidth * (mItemList.size() - 1) + mItemWidth, mItemBorder - mCorner);
                mPath.quadTo(realLeft + mItemWidth * (mItemList.size() - 1) + mItemWidth, mItemBorder,
                        realLeft + mItemWidth * (mItemList.size() - 1) + mItemWidth - mCorner, mItemBorder);
                mPath.lineTo(realLeft + mItemWidth * (mItemList.size() - 1), mItemBorder);
                canvas.drawPath(mPath, mPaint);
            } else {
                canvas.drawRect(realLeft + mItemWidth * (mItemList.size() - 1) - (mItemList.size() - (i + 1)) * mItemWidth, mItemBorder - mItemHeight
                        , realLeft + mItemWidth * (mItemList.size() - 1) - (mItemList.size() - (i + 1)) * mItemWidth + mItemWidth, mItemBorder, mPaint);
                mPaint.setColor(mSeparateLineColor);
                canvas.drawLine(realLeft + mItemWidth * (mItemList.size() - 1) - (mItemList.size() - (i + 1)) * mItemWidth + mItemWidth, mItemBorder - mItemHeight
                        , realLeft + mItemWidth * (mItemList.size() - 1) - (mItemList.size() - (i + 1)) * mItemWidth + mItemWidth, mItemBorder, mPaint);
            }
            mItemRectList.add(new Rect(realLeft + mItemWidth * (mItemList.size() - 1) - (mItemList.size() - (i + 1)) * mItemWidth, mItemBorder - mItemHeight, realLeft + mItemWidth * (mItemList.size() - 1) - (mItemList.size() - (i + 1)) * mItemWidth + mItemWidth, mItemBorder));
        }
        drawTitle(canvas);
    }

    // 绘制窗口上的文字
    private void drawTitle(Canvas canvas) {
        TipItem tipItem;
        for (int i = 0; i < mItemRectList.size(); i++) {
            tipItem = mItemList.get(i);
            mPaint.setColor(tipItem.getTextColor());
            if (mStatus == STATUS_UP) {
                canvas.drawText(tipItem.getTitle(), mItemRectList.get(i).left + mItemWidth / 2 - getTextWidth(tipItem.getTitle(), mPaint) / 2, mItemBorder - mItemHeight / 2 + getTextHeight(mPaint) / 2, mPaint);
            } else if (mStatus == STATUS_DOWN) {
                canvas.drawText(tipItem.getTitle(), mItemRectList.get(i).left + mItemWidth / 2 - getTextWidth(tipItem.getTitle(), mPaint) / 2, mItemRectList.get(i).bottom - mItemHeight / 2 + getTextHeight(mPaint) / 2, mPaint);
            }
        }

    }

    private void drawBackground(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
    }


    public void setTipItemList(final List<TipItem> list) {
        mItemList.clear();
        List<TipItem> items = list;
        for (TipItem item : items) {
            if (!TextUtils.isEmpty(item.getTitle())) {
                item.setTitle(updateTitle(item.getTitle()));
            } else {
                item.setTitle("");
            }

            mItemList.add(item);
        }
    }

    private String updateTitle(String title) {
        int textLength = title.length();
        String suffix = "";
        while (getTextWidth(title.substring(0, textLength) + "...", mPaint) > mItemWidth - dip2px(10)) {
            textLength--;
            suffix = "...";
        }
        return title.substring(0, textLength) + suffix;
    }

    public void setSeparateLineColor(int separateLineColor) {
        mSeparateLineColor = separateLineColor;
    }

    private int dip2px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    private float getTextHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return fontMetrics.bottom - fontMetrics.descent - fontMetrics.ascent;
    }


    private float getTextWidth(String text, Paint paint) {
        return paint.measureText(text);
    }

    private int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < mItemRectList.size(); i++) {
                    if (onItemClickListener != null && isPointInRect(new PointF(event.getX(), event.getY()), mItemRectList.get(i))) {
                        // 被按下时，choose值为当前方块Item序号
                        choose = i;
                        // 更新视图
                        postInvalidate(mItemRectList.get(i).left, mItemRectList.get(i).top, mItemRectList.get(i).right, mItemRectList.get(i).bottom);
                    }
                }
                return true;
            case MotionEvent.ACTION_UP:
                for (int i = 0; i < mItemRectList.size(); i++) {
                    if (onItemClickListener != null && isPointInRect(new PointF(event.getX(), event.getY()), mItemRectList.get(i))) {
                        // 触发方法，传入两个参数
                        onItemClickListener.onItemClick(mItemList.get(i).getTitle(), i);
                        choose = -1;
                    }
                }

                if (onItemClickListener != null) {
                    clean();
                    onItemClickListener.dismiss(); // 触发方法
                }

                removeView(); // 移除TipView

                return true;
        }
        return true;
    }

    private void removeView() {
        ViewGroup vg = (ViewGroup) this.getParent();
        if (vg != null) {
            vg.removeView(this);
        }
    }

    private boolean isPointInRect(PointF pointF, Rect targetRect) {
        if (pointF.x < targetRect.left) {
            return false;
        }
        if (pointF.x > targetRect.right) {
            return false;
        }
        if (pointF.y < targetRect.top) {
            return false;
        }
        if (pointF.y > targetRect.bottom) {
            return false;
        }
        return true;
    }

    private void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(String name, int position);

        void dismiss();
    }

    public static class Builder {

        private OnItemClickListener onItemClickListener;
        private Context mContext;
        private ViewGroup mRootView;
        private List<TipItem> mTipItemList = new ArrayList<>();
        private int mSeparateLineColor = Color.WHITE;
        private int x, y;

        public Builder(Context context, ViewGroup rootView, int x, int y) {
            mContext = context;
            mRootView = rootView;
            this.x = x;
            this.y = y;
        }

        public Builder addItem(TipItem tipItem) {
            mTipItemList.add(tipItem);
            return this;
        }

        public Builder addItems(List<TipItem> list) {
            mTipItemList.addAll(list);
            return this;
        }

        public Builder setSeparateLineColor(int color) {
            mSeparateLineColor = color;
            return this;
        }

        public Builder setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
            return this;
        }

        public TipView create() {
            TipView flipShare = new TipView(mContext, mRootView, x, y, mTipItemList);
            flipShare.setOnItemClickListener(onItemClickListener);
            flipShare.setSeparateLineColor(mSeparateLineColor);
            return flipShare;
        }
    }
}
