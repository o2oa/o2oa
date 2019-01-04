package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.clip;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DensityUtil;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

/**
 * 图片裁剪控件
 *
 * Created by FancyLou on 2015/12/5.
 */
public class ClipImageLayout extends RelativeLayout {

    private ClipZoomImageView mZoomImageView;
    private ClipImageBorderView mClipImageView;

    /**
     * 这里测试，直接写死了大小，真正使用过程中，可以提取为自定义属性
     */
    private int mHorizontalPadding = 20;
    private Bitmap mSrc;


    public ClipImageLayout(Context context) {
        this(context, null);
    }

    public ClipImageLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ClipImageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ClipImageViewLayout);

        mZoomImageView = new ClipZoomImageView(context);
        mClipImageView = new ClipImageBorderView(context);

        mSrc = BitmapFactory.decodeResource(getResources(), array.getResourceId(R.styleable.ClipImageViewLayout_image_src, -1));
        mHorizontalPadding = array.getDimensionPixelSize(R.styleable.ClipImageViewLayout_horizontal_padding, 20);

        mZoomImageView.setImageBitmap(mSrc);

        add2View(mZoomImageView, mClipImageView);

        // 计算padding的px
        mHorizontalPadding = DensityUtil.dip2px(context, mHorizontalPadding);
        mZoomImageView.setHorizontalPadding(mHorizontalPadding);
        mClipImageView.setHorizontalPadding(mHorizontalPadding);

        array.recycle();
    }

    /**
     * 对外公布设置边距的方法,单位为dp
     *
     * @param mHorizontalPadding
     */
    public void setHorizontalPadding(int mHorizontalPadding)
    {
        this.mHorizontalPadding = mHorizontalPadding;
    }

    /**
     * 动态设置src
     *
     * @param bitmap
     */
    public void setSrc(Bitmap bitmap) {
        this.removeAllViews();
        this.post(new RefreshSrcRunnable(bitmap));
    }


    class RefreshSrcRunnable implements  Runnable {
        private Bitmap bitmap;
        public RefreshSrcRunnable(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        public void run() {
            mSrc = bitmap;
            mZoomImageView.setImageBitmap(bitmap);
            add2View(mZoomImageView, mClipImageView);
        }
    }

    private void add2View(ClipZoomImageView mZoomImageView, ClipImageBorderView mClipImageView) {
        android.view.ViewGroup.LayoutParams lp = new LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mZoomImageView, lp);
        this.addView(mClipImageView, lp);
    }

    /**
     * 裁切图片
     *
     * @return
     */
    public Bitmap clip()
    {
        return mZoomImageView.clip();
    }

}
