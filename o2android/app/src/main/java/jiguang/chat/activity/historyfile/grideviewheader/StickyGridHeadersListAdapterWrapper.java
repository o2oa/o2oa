
package jiguang.chat.activity.historyfile.grideviewheader;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

public class StickyGridHeadersListAdapterWrapper extends BaseAdapter implements
        StickyGridHeadersBaseAdapter {
    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            notifyDataSetInvalidated();
        }
    };

    private ListAdapter mDelegate;

    public StickyGridHeadersListAdapterWrapper(ListAdapter adapter) {
        mDelegate = adapter;
        if (adapter != null) {
            adapter.registerDataSetObserver(mDataSetObserver);
        }
    }

    @Override
    public int getCount() {
        if (mDelegate == null) {
            return 0;
        }
        return mDelegate.getCount();
    }

    @Override
    public int getCountForHeader(int header) {
        return 0;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public Object getItem(int position) {
        if (mDelegate == null) {
            return null;
        }
        return mDelegate.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return mDelegate.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return mDelegate.getItemViewType(position);
    }

    @Override
    public int getNumHeaders() {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mDelegate.getView(position, convertView, parent);
    }

    @Override
    public int getViewTypeCount() {
        return mDelegate.getViewTypeCount();
    }

    @Override
    public boolean hasStableIds() {
        return mDelegate.hasStableIds();
    }

}
