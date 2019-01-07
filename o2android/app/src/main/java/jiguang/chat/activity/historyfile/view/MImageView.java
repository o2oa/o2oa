package jiguang.chat.activity.historyfile.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MImageView extends ImageView {
	private OnMeasureListener onMeasureListener;
	
	public void setOnMeasureListener(OnMeasureListener onMeasureListener) {
		this.onMeasureListener = onMeasureListener;
	}

	public MImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		if(onMeasureListener != null){
			onMeasureListener.onMeasureSize(getMeasuredWidth(), getMeasuredHeight());
		}
	}

	public interface OnMeasureListener{
		public void onMeasureSize(int width, int height);
	}
	
}
