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

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StickyGridHeadersSimpleAdapterWrapper extends BaseAdapter implements
        StickyGridHeadersBaseAdapter {
    private StickyGridHeadersSimpleAdapter mDelegate;

    private HeaderData[] mHeaders;

    public StickyGridHeadersSimpleAdapterWrapper(StickyGridHeadersSimpleAdapter adapter) {
        mDelegate = adapter;
        adapter.registerDataSetObserver(new DataSetObserverExtension());
        mHeaders = generateHeaderList(adapter);
    }

    @Override
    public int getCount() {
        return mDelegate.getCount();
    }

    @Override
    public int getCountForHeader(int position) {
        return mHeaders[position].getCount();
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        return mDelegate.getHeaderView(mHeaders[position].getRefPosition(), convertView, parent);
    }

    @Override
    public Object getItem(int position) {
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
        return mHeaders.length;
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

    protected HeaderData[] generateHeaderList(StickyGridHeadersSimpleAdapter adapter) {
        Map<Long, HeaderData> mapping = new HashMap<Long, HeaderData>();
        List<HeaderData> headers = new ArrayList<HeaderData>();

        for (int i = 0; i < adapter.getCount(); i++) {
            long headerId = adapter.getHeaderId(i);
            HeaderData headerData = mapping.get(headerId);
            if (headerData == null) {
                headerData = new HeaderData(i);
                headers.add(headerData);
            }
            headerData.incrementCount();
            mapping.put(headerId, headerData);
        }

        return headers.toArray(new HeaderData[headers.size()]);
    }

    private final class DataSetObserverExtension extends DataSetObserver {
        @Override
        public void onChanged() {
            mHeaders = generateHeaderList(mDelegate);
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            mHeaders = generateHeaderList(mDelegate);
            notifyDataSetInvalidated();
        }
    }

    private class HeaderData {
        private int mCount;

        private int mRefPosition;

        public HeaderData(int refPosition) {
            mRefPosition = refPosition;
            mCount = 0;
        }

        public int getCount() {
            return mCount;
        }

        public int getRefPosition() {
            return mRefPosition;
        }

        public void incrementCount() {
            mCount++;
        }
    }
}
