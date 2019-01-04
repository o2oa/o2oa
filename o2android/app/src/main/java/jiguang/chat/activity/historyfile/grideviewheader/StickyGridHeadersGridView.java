/*
 Copyright 2013 Tonic Artos

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package jiguang.chat.activity.historyfile.grideviewheader;


import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.ListAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * GridView that displays items in sections with headers that stick to the top
 * of the view.
 * 
 */
public class StickyGridHeadersGridView extends GridView implements OnScrollListener,
        OnItemClickListener, OnItemSelectedListener, OnItemLongClickListener {
    private static final String ERROR_PLATFORM = "Error supporting platform "
            + Build.VERSION.SDK_INT + ".";

    private static final int MATCHED_STICKIED_HEADER = -2;

    private static final int NO_MATCHED_HEADER = -1;

    protected static final int TOUCH_MODE_DONE_WAITING = 2;

    protected static final int TOUCH_MODE_DOWN = 0;

    protected static final int TOUCH_MODE_FINISHED_LONG_PRESS = -2;

    protected static final int TOUCH_MODE_REST = -1;

    protected static final int TOUCH_MODE_TAP = 1;

    static final String TAG = StickyGridHeadersGridView.class.getSimpleName();

    private static MotionEvent.PointerCoords[] getPointerCoords(MotionEvent e) {
        int n = e.getPointerCount();
        MotionEvent.PointerCoords[] r = new MotionEvent.PointerCoords[n];
        for (int i = 0; i < n; i++) {
            r[i] = new MotionEvent.PointerCoords();
            e.getPointerCoords(i, r[i]);
        }
        return r;
    }

    private static int[] getPointerIds(MotionEvent e) {
        int n = e.getPointerCount();
        int[] r = new int[n];
        for (int i = 0; i < n; i++) {
            r[i] = e.getPointerId(i);
        }
        return r;
    }

    public CheckForHeaderLongPress mPendingCheckForLongPress;

    public CheckForHeaderTap mPendingCheckForTap;

    private boolean mAreHeadersSticky = true;

    private final Rect mClippingRect = new Rect();

    private boolean mClippingToPadding;

    private boolean mClipToPaddingHasBeenSet;

    private int mColumnWidth;

    private long mCurrentHeaderId = -1;

    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            reset();
        }

        @Override
        public void onInvalidated() {
            reset();
        }
    };

    private int mHeaderBottomPosition;

    private boolean mHeadersIgnorePadding;

    private int mHorizontalSpacing;

    private boolean mMaskStickyHeaderRegion = true;

    private float mMotionY;

    /**
     * Must be set from the wrapped GridView in the constructor.
     */
    private int mNumColumns;

    private boolean mNumColumnsSet;

    private int mNumMeasuredColumns = 1;

    private OnHeaderClickListener mOnHeaderClickListener;

    private OnHeaderLongClickListener mOnHeaderLongClickListener;

    private OnItemClickListener mOnItemClickListener;

    private OnItemLongClickListener mOnItemLongClickListener;

    private OnItemSelectedListener mOnItemSelectedListener;

    private PerformHeaderClick mPerformHeaderClick;

    private OnScrollListener mScrollListener;

    private int mScrollState = SCROLL_STATE_IDLE;

    private View mStickiedHeader;

    private Runnable mTouchModeReset;

    private int mTouchSlop;

    private int mVerticalSpacing;

    protected StickyGridHeadersBaseAdapterWrapper mAdapter;

    protected boolean mDataChanged;

    protected int mMotionHeaderPosition;

    protected int mTouchMode;

    boolean mHeaderChildBeingPressed = false;

    public StickyGridHeadersGridView(Context context) {
        this(context, null);
    }

    public StickyGridHeadersGridView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.gridViewStyle);
    }

    public StickyGridHeadersGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setOnScrollListener(this);
        setVerticalFadingEdgeEnabled(false);

        if (!mNumColumnsSet) {
            mNumColumns = AUTO_FIT;
        }

        ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
    }

    public boolean areHeadersSticky() {
        return mAreHeadersSticky;
    }

    /**
     * Gets the header at an item position. However, the position must be that
     * of a HeaderFiller.
     * 
     * @param position Position of HeaderFiller.
     * @return Header View wrapped in HeaderFiller or null if no header was
     *         found.
     */
    public View getHeaderAt(int position) {
        if (position == MATCHED_STICKIED_HEADER) {
            return mStickiedHeader;
        }

        try {
            return (View)getChildAt(position).getTag();
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Get the currently stickied header.
     * 
     * @return Current stickied header.
     */
    public View getStickiedHeader() {
        return mStickiedHeader;
    }

    public boolean getStickyHeaderIsTranscluent() {
        return !mMaskStickyHeaderRegion;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mOnItemClickListener.onItemClick(parent, view,
                mAdapter.translatePosition(position).mPosition, id);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return mOnItemLongClickListener.onItemLongClick(parent, view,
                mAdapter.translatePosition(position).mPosition, id);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mOnItemSelectedListener.onItemSelected(parent, view,
                mAdapter.translatePosition(position).mPosition, id);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        mOnItemSelectedListener.onNothingSelected(parent);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState)state;

        super.onRestoreInstanceState(ss.getSuperState());
        mAreHeadersSticky = ss.areHeadersSticky;

        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        ss.areHeadersSticky = mAreHeadersSticky;
        return ss;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            scrollChanged(firstVisibleItem);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }

        mScrollState = scrollState;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        boolean wasHeaderChildBeingPressed = mHeaderChildBeingPressed;
        if (mHeaderChildBeingPressed) {
            final View tempHeader = getHeaderAt(mMotionHeaderPosition);
            final View headerHolder = mMotionHeaderPosition == MATCHED_STICKIED_HEADER ?
                    tempHeader : getChildAt(mMotionHeaderPosition);
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                mHeaderChildBeingPressed = false;
            }
            if (tempHeader != null) {
                tempHeader.dispatchTouchEvent(transformEvent(ev, mMotionHeaderPosition));
                tempHeader.invalidate();
                tempHeader.postDelayed(new Runnable() {
                    public void run() {
                        invalidate(0, headerHolder.getTop(), getWidth(), headerHolder.getTop()+headerHolder.getHeight());
                    }
                }, ViewConfiguration.getPressedStateDuration());
                invalidate(0, headerHolder.getTop(), getWidth(), headerHolder.getTop()+headerHolder.getHeight());
            }
        }

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (mPendingCheckForTap == null) {
                    mPendingCheckForTap = new CheckForHeaderTap();
                }
                postDelayed(mPendingCheckForTap, ViewConfiguration.getTapTimeout());

                final int y = (int)ev.getY();
                mMotionY = y;
                mMotionHeaderPosition = findMotionHeader(y);
                if (mMotionHeaderPosition == NO_MATCHED_HEADER
                        || mScrollState == SCROLL_STATE_FLING) {
                    // Don't consume the event and pass it to super because we
                    // can't handle it yet.
                    break;
                } else {
                    View tempHeader = getHeaderAt(mMotionHeaderPosition);
                    if (tempHeader != null) {
                        if (tempHeader.dispatchTouchEvent(transformEvent(ev, mMotionHeaderPosition))) {
                            mHeaderChildBeingPressed = true;
                            tempHeader.setPressed(true);
                        }
                        tempHeader.invalidate();
                        if (mMotionHeaderPosition != MATCHED_STICKIED_HEADER) {
                            tempHeader = getChildAt(mMotionHeaderPosition);
                        }
                        invalidate(0, tempHeader.getTop(), getWidth(), tempHeader.getTop()+tempHeader.getHeight());
                    }
                }
                mTouchMode = TOUCH_MODE_DOWN;
                return true;
            case MotionEvent.ACTION_MOVE:
                if (mMotionHeaderPosition != NO_MATCHED_HEADER
                        && Math.abs(ev.getY() - mMotionY) > mTouchSlop) {
                    // Detected scroll initiation so cancel touch completion on
                    // header.
                    mTouchMode = TOUCH_MODE_REST;
                    // if (!mHeaderChildBeingPressed) {
                    final View header = getHeaderAt(mMotionHeaderPosition);
                    if (header != null) {
                        header.setPressed(false);
                        header.invalidate();
                    }
                    final Handler handler = getHandler();
                    if (handler != null) {
                        handler.removeCallbacks(mPendingCheckForLongPress);
                    }
                    mMotionHeaderPosition = NO_MATCHED_HEADER;
                    // }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mTouchMode == TOUCH_MODE_FINISHED_LONG_PRESS) {
                    mTouchMode = TOUCH_MODE_REST;
                    return true;
                }
                if (mTouchMode == TOUCH_MODE_REST || mMotionHeaderPosition == NO_MATCHED_HEADER) {
                    break;
                }

                final View header = getHeaderAt(mMotionHeaderPosition);
                if (!wasHeaderChildBeingPressed) {
                    if (header != null) {
                        if (mTouchMode != TOUCH_MODE_DOWN) {
                            header.setPressed(false);
                        }

                        if (mPerformHeaderClick == null) {
                            mPerformHeaderClick = new PerformHeaderClick();
                        }

                        final PerformHeaderClick performHeaderClick = mPerformHeaderClick;
                        performHeaderClick.mClickMotionPosition = mMotionHeaderPosition;
                        performHeaderClick.rememberWindowAttachCount();

                        if (mTouchMode == TOUCH_MODE_DOWN || mTouchMode == TOUCH_MODE_TAP) {
                            final Handler handler = getHandler();
                            if (handler != null) {
                                handler.removeCallbacks(mTouchMode == TOUCH_MODE_DOWN ? mPendingCheckForTap
                                        : mPendingCheckForLongPress);
                            }

                            if (!mDataChanged) {
                                /*
                                 * Got here so must be a tap. The long press
                                 * would have triggered on the callback handler.
                                 */
                                mTouchMode = TOUCH_MODE_TAP;
                                header.setPressed(true);
                                setPressed(true);
                                if (mTouchModeReset != null) {
                                    removeCallbacks(mTouchModeReset);
                                }
                                mTouchModeReset = new Runnable() {
                                    @Override
                                    public void run() {
                                        mMotionHeaderPosition = NO_MATCHED_HEADER;
                                        mTouchModeReset = null;
                                        mTouchMode = TOUCH_MODE_REST;
                                        header.setPressed(false);
                                        setPressed(false);
                                        header.invalidate();
                                        invalidate(0, header.getTop(), getWidth(),
                                                header.getHeight());
                                        if (!mDataChanged) {
                                            performHeaderClick.run();
                                        }
                                    }
                                };
                                postDelayed(mTouchModeReset,
                                        ViewConfiguration.getPressedStateDuration());
                            } else {
                                mTouchMode = TOUCH_MODE_REST;
                            }
                        } else if (!mDataChanged) {
                            performHeaderClick.run();
                        }
                    }
                }
                mTouchMode = TOUCH_MODE_REST;
                return true;
        }
        return super.onTouchEvent(ev);
    }

    public boolean performHeaderClick(View view, long id) {
        if (mOnHeaderClickListener != null) {
            playSoundEffect(SoundEffectConstants.CLICK);
            if (view != null) {
                view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
            }
            mOnHeaderClickListener.onHeaderClick(this, view, id);
            return true;
        }

        return false;
    }

    public boolean performHeaderLongPress(View view, long id) {
        boolean handled = false;
        if (mOnHeaderLongClickListener != null) {
            handled = mOnHeaderLongClickListener.onHeaderLongClick(this, view, id);
        }

        if (handled) {
            if (view != null) {
                view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_LONG_CLICKED);
            }
            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }

        return handled;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        if (!mClipToPaddingHasBeenSet) {
            mClippingToPadding = true;
        }

        StickyGridHeadersBaseAdapter baseAdapter;
        if (adapter instanceof StickyGridHeadersBaseAdapter) {
            baseAdapter = (StickyGridHeadersBaseAdapter)adapter;
        } else if (adapter instanceof StickyGridHeadersSimpleAdapter) {
            // Wrap up simple adapter to auto-generate the data we need.
            baseAdapter = new StickyGridHeadersSimpleAdapterWrapper(
                    (StickyGridHeadersSimpleAdapter)adapter);
        } else {
            // Wrap up a list adapter so it is an adapter with zero headers.
            baseAdapter = new StickyGridHeadersListAdapterWrapper(adapter);
        }

        this.mAdapter = new StickyGridHeadersBaseAdapterWrapper(getContext(), this, baseAdapter);
        this.mAdapter.registerDataSetObserver(mDataSetObserver);
        reset();
        super.setAdapter(this.mAdapter);
    }

    public void setAreHeadersSticky(boolean useStickyHeaders) {
        if (useStickyHeaders != mAreHeadersSticky) {
            mAreHeadersSticky = useStickyHeaders;
            requestLayout();
        }
    }

    @Override
    public void setClipToPadding(boolean clipToPadding) {
        super.setClipToPadding(clipToPadding);
        mClippingToPadding = clipToPadding;
        mClipToPaddingHasBeenSet = true;
    }

    @Override
    public void setColumnWidth(int columnWidth) {
        super.setColumnWidth(columnWidth);
        mColumnWidth = columnWidth;
    }

    /**
     * If set to true, headers will ignore horizontal padding.
     * 
     * @param b if true, horizontal padding is ignored by headers
     */
    public void setHeadersIgnorePadding(boolean b) {
        mHeadersIgnorePadding = b;
    }

    @Override
    public void setHorizontalSpacing(int horizontalSpacing) {
        super.setHorizontalSpacing(horizontalSpacing);
        mHorizontalSpacing = horizontalSpacing;
    }

    @Override
    public void setNumColumns(int numColumns) {
        super.setNumColumns(numColumns);
        mNumColumnsSet = true;
        this.mNumColumns = numColumns;
        if (numColumns != AUTO_FIT && mAdapter != null) {
            mAdapter.setNumColumns(numColumns);
        }
    }

    public void setOnHeaderClickListener(OnHeaderClickListener listener) {
        mOnHeaderClickListener = listener;
    }

    public void setOnHeaderLongClickListener(OnHeaderLongClickListener listener) {
        if (!isLongClickable()) {
            setLongClickable(true);
        }
        mOnHeaderLongClickListener = listener;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
        super.setOnItemClickListener(this);
    }

    @Override
    public void setOnItemLongClickListener(
            OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
        super.setOnItemLongClickListener(this);
    }

    @Override
    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.mOnItemSelectedListener = listener;
        super.setOnItemSelectedListener(this);
    }

    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        this.mScrollListener = listener;
    }

    public void setStickyHeaderIsTranscluent(boolean isTranscluent) {
        mMaskStickyHeaderRegion = !isTranscluent;
    }

    @Override
    public void setVerticalSpacing(int verticalSpacing) {
        super.setVerticalSpacing(verticalSpacing);
        mVerticalSpacing = verticalSpacing;
    }

    private int findMotionHeader(float y) {
        if (mStickiedHeader != null && y <= mHeaderBottomPosition) {
            return MATCHED_STICKIED_HEADER;
        }

        int vi = 0;
        for (int i = getFirstVisiblePosition(); i <= getLastVisiblePosition();) {
            long id = getItemIdAtPosition(i);
            if (id == StickyGridHeadersBaseAdapterWrapper.ID_HEADER) {
                View headerWrapper = getChildAt(vi);

                int bottom = headerWrapper.getBottom();
                int top = headerWrapper.getTop();
                if (y <= bottom && y >= top) {
                    return vi;
                }
            }
            i += mNumMeasuredColumns;
            vi += mNumMeasuredColumns;
        }

        return NO_MATCHED_HEADER;
    }

    private int getHeaderHeight() {
        if (mStickiedHeader != null) {
            return mStickiedHeader.getMeasuredHeight();
        }
        return 0;
    }

    private long headerViewPositionToId(int pos) {
        if (pos == MATCHED_STICKIED_HEADER) {
            return mCurrentHeaderId;
        }
        return mAdapter.getHeaderId(getFirstVisiblePosition() + pos);
    }

    private void measureHeader() {
        if (mStickiedHeader == null) {
            return;
        }

        int widthMeasureSpec;
        if (mHeadersIgnorePadding) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY);
        } else {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(getWidth() - getPaddingLeft()
                    - getPaddingRight(), MeasureSpec.EXACTLY);
        }

        int heightMeasureSpec = 0;

        ViewGroup.LayoutParams params = mStickiedHeader.getLayoutParams();
        if (params != null && params.height > 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
        } else {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        mStickiedHeader.measure(MeasureSpec.makeMeasureSpec(0,0), MeasureSpec.makeMeasureSpec(0,0));
        mStickiedHeader.measure(widthMeasureSpec, heightMeasureSpec);

        if (mHeadersIgnorePadding) {
            mStickiedHeader.layout(getLeft(), 0, getRight(), mStickiedHeader.getMeasuredHeight());
        } else {
            mStickiedHeader.layout(getLeft() + getPaddingLeft(), 0, getRight() - getPaddingRight(),
                    mStickiedHeader.getMeasuredHeight());
        }
    }

    private void reset() {
        mHeaderBottomPosition = 0;
        swapStickiedHeader(null);
        mCurrentHeaderId = INVALID_ROW_ID;
    }

    private void scrollChanged(int firstVisibleItem) {
        if (mAdapter == null || mAdapter.getCount() == 0 || !mAreHeadersSticky) {
            return;
        }

        View firstItem = getChildAt(0);
        if (firstItem == null) {
            return;
        }

        long newHeaderId;
        int selectedHeaderPosition = firstVisibleItem;

        int beforeRowPosition = firstVisibleItem - mNumMeasuredColumns;
        if (beforeRowPosition < 0) {
            beforeRowPosition = firstVisibleItem;
        }

        int secondRowPosition = firstVisibleItem + mNumMeasuredColumns;
        if (secondRowPosition >= mAdapter.getCount()) {
            secondRowPosition = firstVisibleItem;
        }

        if (mVerticalSpacing == 0) {
            newHeaderId = mAdapter.getHeaderId(firstVisibleItem);
        } else if (mVerticalSpacing < 0) {
            newHeaderId = mAdapter.getHeaderId(firstVisibleItem);
            View firstSecondRowView = getChildAt(mNumMeasuredColumns);
            if (firstSecondRowView.getTop() <= 0) {
                newHeaderId = mAdapter.getHeaderId(secondRowPosition);
                selectedHeaderPosition = secondRowPosition;
            } else {
                newHeaderId = mAdapter.getHeaderId(firstVisibleItem);
            }
        } else {
            int margin = getChildAt(0).getTop();
            if (0 < margin && margin < mVerticalSpacing) {
                newHeaderId = mAdapter.getHeaderId(beforeRowPosition);
                selectedHeaderPosition = beforeRowPosition;
            } else {
                newHeaderId = mAdapter.getHeaderId(firstVisibleItem);
            }
        }

        if (mCurrentHeaderId != newHeaderId) {
            swapStickiedHeader(mAdapter
                    .getHeaderView(selectedHeaderPosition, mStickiedHeader, this));
            measureHeader();
            mCurrentHeaderId = newHeaderId;
        }

        final int childCount = getChildCount();
        if (childCount != 0) {
            View viewToWatch = null;
            int watchingChildDistance = 99999;

            // Find the next header after the stickied one.
            for (int i = 0; i < childCount; i += mNumMeasuredColumns) {
                View child = super.getChildAt(i);

                int childDistance;
                if (mClippingToPadding) {
                    childDistance = child.getTop() - getPaddingTop();
                } else {
                    childDistance = child.getTop();
                }

                if (childDistance < 0) {
                    continue;
                }

                if (mAdapter.getItemId(getPositionForView(child)) == StickyGridHeadersBaseAdapterWrapper.ID_HEADER
                        && childDistance < watchingChildDistance) {
                    viewToWatch = child;
                    watchingChildDistance = childDistance;
                }
            }

            int headerHeight = getHeaderHeight();

            // Work out where to draw stickied header using synchronised
            // scrolling.
            if (viewToWatch != null) {
                if (firstVisibleItem == 0 && super.getChildAt(0).getTop() > 0
                        && !mClippingToPadding) {
                    mHeaderBottomPosition = 0;
                } else {
                    if (mClippingToPadding) {
                        mHeaderBottomPosition = Math.min(viewToWatch.getTop(), headerHeight
                                + getPaddingTop());
                        mHeaderBottomPosition = mHeaderBottomPosition < getPaddingTop() ? headerHeight
                                + getPaddingTop()
                                : mHeaderBottomPosition;
                    } else {
                        mHeaderBottomPosition = Math.min(viewToWatch.getTop(), headerHeight);
                        mHeaderBottomPosition = mHeaderBottomPosition < 0 ? headerHeight
                                : mHeaderBottomPosition;
                    }
                }
            } else {
                mHeaderBottomPosition = headerHeight;
                if (mClippingToPadding) {
                    mHeaderBottomPosition += getPaddingTop();
                }
            }
        }
    }

    private void swapStickiedHeader(View newStickiedHeader) {
        detachHeader(mStickiedHeader);
        attachHeader(newStickiedHeader);
        mStickiedHeader = newStickiedHeader;
    }

    private MotionEvent transformEvent(MotionEvent e, int headerPosition) {
        if (headerPosition == MATCHED_STICKIED_HEADER) {
            return e;
        }

        long downTime = e.getDownTime();
        long eventTime = e.getEventTime();
        int action = e.getAction();
        int pointerCount = e.getPointerCount();
        int[] pointerIds = getPointerIds(e);
        MotionEvent.PointerCoords[] pointerCoords = getPointerCoords(e);
        int metaState = e.getMetaState();
        float xPrecision = e.getXPrecision();
        float yPrecision = e.getYPrecision();
        int deviceId = e.getDeviceId();
        int edgeFlags = e.getEdgeFlags();
        int source = e.getSource();
        int flags = e.getFlags();

        View headerHolder = getChildAt(headerPosition);
        for (int i = 0; i < pointerCount;i++) {
            pointerCoords[i].y -= headerHolder.getTop();
        }
        MotionEvent n = MotionEvent.obtain(downTime, eventTime, action,
                pointerCount, pointerIds, pointerCoords, metaState, xPrecision,
                yPrecision, deviceId, edgeFlags, source, flags);
        return n;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            scrollChanged(getFirstVisiblePosition());
        }

        boolean drawStickiedHeader = mStickiedHeader != null && mAreHeadersSticky
                && mStickiedHeader.getVisibility() == View.VISIBLE;
        int headerHeight = getHeaderHeight();
        int top = mHeaderBottomPosition - headerHeight;

        // Mask the region where we will draw the header later, but only if we
        // will draw a header and masking is requested.
        if (drawStickiedHeader && mMaskStickyHeaderRegion) {
            if (mHeadersIgnorePadding) {
                mClippingRect.left = 0;
                mClippingRect.right = getWidth();
            } else {
                mClippingRect.left = getPaddingLeft();
                mClippingRect.right = getWidth() - getPaddingRight();
            }
            mClippingRect.top = mHeaderBottomPosition;
            mClippingRect.bottom = getHeight();

            canvas.save();
            canvas.clipRect(mClippingRect);
        }

        // ...and draw the grid view.
        super.dispatchDraw(canvas);

        // Find headers.
        List<Integer> headerPositions = new ArrayList<Integer>();
        int vi = 0;
        for (int i = getFirstVisiblePosition(); i <= getLastVisiblePosition();) {
            long id = getItemIdAtPosition(i);
            if (id == StickyGridHeadersBaseAdapterWrapper.ID_HEADER) {
                headerPositions.add(vi);
            }
            i += mNumMeasuredColumns;
            vi += mNumMeasuredColumns;
        }

        // Draw headers in list.
        for (int i = 0; i < headerPositions.size(); i++) {
            View frame = getChildAt(headerPositions.get(i));
            View header;
            try {
                header = (View)frame.getTag();
            } catch (Exception e) {
                return;
            }

            boolean headerIsStickied = ((StickyGridHeadersBaseAdapterWrapper.HeaderFillerView)frame).getHeaderId() == mCurrentHeaderId
                    && frame.getTop() < 0 && mAreHeadersSticky;
            if (header.getVisibility() != View.VISIBLE || headerIsStickied) {
                continue;
            }

            int widthMeasureSpec;
            if (mHeadersIgnorePadding) {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY);
            } else {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(getWidth() - getPaddingLeft()
                        - getPaddingRight(), MeasureSpec.EXACTLY);
            }

            int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            header.measure(MeasureSpec.makeMeasureSpec(0,0),MeasureSpec.makeMeasureSpec(0,0));
            header.measure(widthMeasureSpec, heightMeasureSpec);

            if (mHeadersIgnorePadding) {
                header.layout(getLeft(), 0, getRight(), frame.getHeight());
            } else {
                header.layout(getLeft() + getPaddingLeft(), 0, getRight() - getPaddingRight(),
                        frame.getHeight());
            }

            if (mHeadersIgnorePadding) {
                mClippingRect.left = 0;
                mClippingRect.right = getWidth();
            } else {
                mClippingRect.left = getPaddingLeft();
                mClippingRect.right = getWidth() - getPaddingRight();
            }

            mClippingRect.bottom = frame.getBottom();
            mClippingRect.top = frame.getTop();
            canvas.save();
            canvas.clipRect(mClippingRect);
            if (mHeadersIgnorePadding) {
                canvas.translate(0, frame.getTop());
            } else {
                canvas.translate(getPaddingLeft(), frame.getTop());
            }
            header.draw(canvas);
            canvas.restore();
        }

        if (drawStickiedHeader && mMaskStickyHeaderRegion) {
            canvas.restore();
        } else if (!drawStickiedHeader) {
            // Done.
            return;
        }

        // Draw stickied header.
        int wantedWidth;
        if (mHeadersIgnorePadding) {
            wantedWidth = getWidth();
        } else {
            wantedWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        }
        if (mStickiedHeader.getWidth() != wantedWidth) {
            int widthMeasureSpec;
            if (mHeadersIgnorePadding) {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY);
            } else {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(getWidth() - getPaddingLeft()
                        - getPaddingRight(), MeasureSpec.EXACTLY); // Bug here
            }
            int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            mStickiedHeader.measure(MeasureSpec.makeMeasureSpec(0,0),MeasureSpec.makeMeasureSpec(0,0));
            mStickiedHeader.measure(widthMeasureSpec, heightMeasureSpec);
            if (mHeadersIgnorePadding) {
                mStickiedHeader.layout(getLeft(), 0, getRight(), mStickiedHeader.getHeight());
            } else {
                mStickiedHeader.layout(getLeft() + getPaddingLeft(), 0, getRight()
                        - getPaddingRight(), mStickiedHeader.getHeight());
            }
        }

        if (mHeadersIgnorePadding) {
            mClippingRect.left = 0;
            mClippingRect.right = getWidth();
        } else {
            mClippingRect.left = getPaddingLeft();
            mClippingRect.right = getWidth() - getPaddingRight();
        }
        mClippingRect.bottom = top + headerHeight;
        if (mClippingToPadding) {
            mClippingRect.top = getPaddingTop();
        } else {
            mClippingRect.top = 0;
        }

        canvas.save();
        canvas.clipRect(mClippingRect);

        if (mHeadersIgnorePadding) {
            canvas.translate(0, top);
        } else {
            canvas.translate(getPaddingLeft(), top);
        }

        if (mHeaderBottomPosition != headerHeight) {
            canvas.saveLayerAlpha(0, 0, canvas.getWidth(), canvas.getHeight(), 255
                    * mHeaderBottomPosition / headerHeight, Canvas.ALL_SAVE_FLAG);
        }

        mStickiedHeader.draw(canvas);

        if (mHeaderBottomPosition != headerHeight) {
            canvas.restore();
        }
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mNumColumns == AUTO_FIT) {
            int numFittedColumns;
            if (mColumnWidth > 0) {
                int gridWidth = Math.max(MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()
                        - getPaddingRight(), 0);
                numFittedColumns = gridWidth / mColumnWidth;
                // Calculate measured columns accounting for requested grid
                // spacing.
                if (numFittedColumns > 0) {
                    while (numFittedColumns != 1) {
                        if (numFittedColumns * mColumnWidth + (numFittedColumns - 1)
                                * mHorizontalSpacing > gridWidth) {
                            numFittedColumns--;
                        } else {
                            break;
                        }
                    }
                } else {
                    // Could not fit any columns in grid width, so default to a
                    // single column.
                    numFittedColumns = 1;
                }
            } else {
                // Mimic vanilla GridView behaviour where there is not enough
                // information to auto-fit columns.
                numFittedColumns = 2;
            }
            mNumMeasuredColumns = numFittedColumns;
        } else {
            // There were some number of columns requested so we will try to
            // fulfil the request.
            mNumMeasuredColumns = mNumColumns;
        }

        // Update adapter with number of columns.
        if (mAdapter != null) {
            mAdapter.setNumColumns(mNumMeasuredColumns);
        }

        measureHeader();

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    void attachHeader(View header) {
        if (header == null) {
            return;
        }

        try {
            Field attachInfoField = View.class.getDeclaredField("mAttachInfo");
            attachInfoField.setAccessible(true);
            Method method = View.class.getDeclaredMethod("dispatchAttachedToWindow",
                    Class.forName("android.view.View$AttachInfo"), Integer.TYPE);
            method.setAccessible(true);
            method.invoke(header, attachInfoField.get(this), View.GONE);
        } catch (NoSuchMethodException e) {
            throw new RuntimePlatformSupportException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimePlatformSupportException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimePlatformSupportException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimePlatformSupportException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimePlatformSupportException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimePlatformSupportException(e);
        }
    }

    void detachHeader(View header) {
        if (header == null) {
            return;
        }

        try {
            Method method = View.class.getDeclaredMethod("dispatchDetachedFromWindow");
            method.setAccessible(true);
            method.invoke(header);
        } catch (NoSuchMethodException e) {
            throw new RuntimePlatformSupportException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimePlatformSupportException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimePlatformSupportException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimePlatformSupportException(e);
        }
    }

    public interface OnHeaderClickListener {
        void onHeaderClick(AdapterView<?> parent, View view, long id);
    }

    public interface OnHeaderLongClickListener {
        boolean onHeaderLongClick(AdapterView<?> parent, View view, long id);
    }

    private class CheckForHeaderLongPress extends WindowRunnable implements Runnable {
        @Override
        public void run() {
            final View child = getHeaderAt(mMotionHeaderPosition);
            if (child != null) {
                final long longPressId = headerViewPositionToId(mMotionHeaderPosition);

                boolean handled = false;
                if (sameWindow() && !mDataChanged) {
                    handled = performHeaderLongPress(child, longPressId);
                }
                if (handled) {
                    mTouchMode = TOUCH_MODE_FINISHED_LONG_PRESS;
                    setPressed(false);
                    child.setPressed(false);
                } else {
                    mTouchMode = TOUCH_MODE_DONE_WAITING;
                }
            }
        }
    }

    private class PerformHeaderClick extends WindowRunnable implements Runnable {
        int mClickMotionPosition;

        @Override
        public void run() {
            // The data has changed since we posted this action to the event
            // queue, bail out before bad things happen.
            if (mDataChanged)
                return;

            if (mAdapter != null && mAdapter.getCount() > 0
                    && mClickMotionPosition != INVALID_POSITION
                    && mClickMotionPosition < mAdapter.getCount() && sameWindow()) {
                final View view = getHeaderAt(mClickMotionPosition);
                // If there is no view then something bad happened, the view
                // probably scrolled off the screen, and we should cancel the
                // click.
                if (view != null) {
                    performHeaderClick(view, headerViewPositionToId(mClickMotionPosition));
                }
            }
        }
    }

    /**
     * A base class for Runnables that will check that their view is still
     * attached to the original window as when the Runnable was created.
     */
    private class WindowRunnable {
        private int mOriginalAttachCount;

        public void rememberWindowAttachCount() {
            mOriginalAttachCount = getWindowAttachCount();
        }

        public boolean sameWindow() {
            return hasWindowFocus() && getWindowAttachCount() == mOriginalAttachCount;
        }
    }

    final class CheckForHeaderTap implements Runnable {
        @Override
        public void run() {
            if (mTouchMode == TOUCH_MODE_DOWN) {
                mTouchMode = TOUCH_MODE_TAP;
                final View header = getHeaderAt(mMotionHeaderPosition);
                if (header != null && !mHeaderChildBeingPressed) {
                    if (!mDataChanged) {
                        header.setPressed(true);
                        setPressed(true);
                        refreshDrawableState();

                        final int longPressTimeout = ViewConfiguration.getLongPressTimeout();
                        final boolean longClickable = isLongClickable();

                        if (longClickable) {
                            if (mPendingCheckForLongPress == null) {
                                mPendingCheckForLongPress = new CheckForHeaderLongPress();
                            }
                            mPendingCheckForLongPress.rememberWindowAttachCount();
                            postDelayed(mPendingCheckForLongPress, longPressTimeout);
                        } else {
                            mTouchMode = TOUCH_MODE_DONE_WAITING;
                        }
                    } else {
                        mTouchMode = TOUCH_MODE_DONE_WAITING;
                    }
                }
            }
        }
    }

    class RuntimePlatformSupportException extends RuntimeException {
        private static final long serialVersionUID = -6512098808936536538L;

        public RuntimePlatformSupportException(Exception e) {
            super(ERROR_PLATFORM, e);
        }
    }

    /**
     * Constructor called from {@link #CREATOR}
     */
    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        boolean areHeadersSticky;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            areHeadersSticky = in.readByte() != 0;
        }

        @Override
        public String toString() {
            return "StickyGridHeadersGridView.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this)) + " areHeadersSticky="
                    + areHeadersSticky + "}";
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeByte((byte)(areHeadersSticky ? 1 : 0));
        }
    }
}
