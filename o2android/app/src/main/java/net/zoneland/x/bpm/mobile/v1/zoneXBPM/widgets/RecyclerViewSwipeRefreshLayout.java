package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.SwipeRefreshCommonRecyclerViewAdapter;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog;

/**
 * Created by FancyLou on 2015/12/23.
 */
public class RecyclerViewSwipeRefreshLayout extends SwipeRefreshLayout {

    /**
     * 滑动到最下面时的上拉操作
     */
    private int touchSlop;

    private RecyclerView recyclerView;

    /**
     * 上拉加载的时候判断是否加载的一个数据量
     * 翻页的时候每页的数据量
     * 这个数据小于0 不控制上拉加载
     * 如果recyclerView的item数量小于这个number 就不上拉
     */
    private  int recyclerViewPageNumber = -1;

    /**
     * 上拉监听器, 到了最底部的上拉加载操作
     */
    private OnLoadMoreListener onLoadMoreListener;

    /**
     * 上拉加载的时候底部显示的View
     */
    private View footerView;

    /**
     * 按下时的y坐标
     */
    private int mYDown;
    /**
     * 抬起时的y坐标, 与mYDown一起用于滑动到底部时判断是上拉还是下拉
     */
    private int mLastY;
    /**
     * 是否在加载中 ( 上拉加载更多 )
     */
    private boolean isLoading = false;

    /**
     * 控制下拉距离
     */
    private float mInitialDownY;


    public RecyclerViewSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public RecyclerViewSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();//是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件
        footerView = LayoutInflater.from(context).inflate(R.layout.recycler_view_swipe_refresh_footer, null, false);
    }

    public int getRecyclerViewPageNumber() {
        return recyclerViewPageNumber;
    }

    public void setRecyclerViewPageNumber(int recyclerViewPageNumber) {
        this.recyclerViewPageNumber = recyclerViewPageNumber;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 初始化recyclerView对象
        if (recyclerView == null) {
            int childSize = getChildCount();
            if (childSize>0){
                View view = getChildAt(0);
                if (view instanceof RecyclerView){
                    recyclerView = (RecyclerView) view;
                    recyclerView.setOnScrollListener(new OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            // 滚动时到了最底部也可以加载更多
                            if (canLoad()) {
                                XLog.debug("canLoad.........");
                                loadData();
                            }else {
                                XLog.debug("canLoad....false.....");
                            }
                        }
                    });

                }
            }
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInitialDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float yDiff = ev.getY() - mInitialDownY;
                XLog.debug( "yDiff:"+yDiff+",touchslop:"+getTouchSlop());
                if (yDiff<0){//上滑 负数 数字变大
                    if (yDiff > -getTouchSlop()){
                        return false;
                    }
                }else{//下拉 数字变大
                    if (yDiff < getTouchSlop()) {
                        return false;
                    }
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 按下
                mYDown = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 移动
                mLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                // 抬起
                if (canLoad()) {
                    loadData();
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }


    /**
     * @return 返回灵敏度数值
     */
    public int getTouchSlop() {
        return touchSlop;
    }

    /**
     * 设置下拉灵敏度
     *
     * @param mTouchSlop dip值
     */
    public void setTouchSlop(int mTouchSlop) {
        this.touchSlop = mTouchSlop;
    }
    /**
     * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
     */
    private void loadData() {
        if (onLoadMoreListener != null) {
            // 设置状态
            setLoading(true);
            // 加载数据
            onLoadMoreListener.onLoad();
        }
    }

    /**
     * @param loading
     */
    public void setLoading(boolean loading) {
        isLoading = loading;
        SwipeRefreshCommonRecyclerViewAdapter adapter =  null;
        if (recyclerView==null) {//被回收
            return;
        }
        if (recyclerView.getAdapter() instanceof SwipeRefreshCommonRecyclerViewAdapter){
            adapter = (SwipeRefreshCommonRecyclerViewAdapter) recyclerView.getAdapter();
        }
        if (isLoading) {
            if (adapter!=null){
                adapter.addFooter(footerView);
            }
        } else {
            if (adapter!=null){
                adapter.removeFooter(footerView);
            }
            mYDown = 0;
            mLastY = 0;
        }
    }


    /**
     * 是否能加载更多
     * 到了底部、 不在加载中、 上拉操作
     * @return
     */
    private boolean canLoad(){
        XLog.debug( "isBottom:"+isBottom()+", isLoading:"+isLoading+", isPull:"+isPullUp());
        if (recyclerView == null) {
            return false;
        }
        int itemCount = recyclerView.getAdapter().getItemCount();
        XLog.debug("item size :"+ itemCount);
        boolean isMore = true;
        if (recyclerViewPageNumber>0) {
            if (itemCount < recyclerViewPageNumber){
                isMore = false;
            }
        }
        return isBottom() && !isLoading && isPullUp() && isMore;
    }

    private boolean isBottom() {
        if (recyclerView!=null && recyclerView.getAdapter()!=null){
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if(layoutManager instanceof LinearLayoutManager){
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                int lastPosition = recyclerView.getAdapter().getItemCount() - 1;
                return lastVisiblePosition == lastPosition;
            }else if(layoutManager instanceof GridLayoutManager){
                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                int lastVisiblePosition = gridLayoutManager.findLastCompletelyVisibleItemPosition();
                int lastPosition = recyclerView.getAdapter().getItemCount() - 1;
                return lastVisiblePosition == lastPosition;
            }else{
                return false;
            }

        }
        return false;
    }


    /**
     * 是否是上拉操作
     *
     * @return
     */
    private boolean isPullUp() {
        return (mYDown - mLastY) >= touchSlop;
    }

    /**
     * 加载更多的监听器
     *
     */
    public interface OnLoadMoreListener {
        public void onLoad();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }
}
