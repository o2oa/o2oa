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

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

/**
 * Adapter interface for StickyGridHeadersGridView. The adapter expects two sets
 * of data, items, and headers. Implement this interface to provide an optimised
 * method for generating the header data set.
 * 
 * The is a second interface
 *
 */
public interface StickyGridHeadersSimpleAdapter extends ListAdapter {
    /**
     * Get the header id associated with the specified position in the list.
     * 
     * @param position
     *            The position of the item within the adapter's data set whose
     *            header id we want.
     * @return The id of the header at the specified position.
     */
    long getHeaderId(int position);

    /**
     * Get a View that displays the header data at the specified position in the
     * set. You can either create a View manually or inflate it from an XML
     * layout file.
     * 
     * @param position
     *            The position of the header within the adapter's header data
     *            set.
     * @param convertView
     *            The old view to reuse, if possible. Note: You should check
     *            that this view is non-null and of an appropriate type before
     *            using. If it is not possible to convert this view to display
     *            the correct data, this method can create a new view.
     * @param parent
     *            The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    View getHeaderView(int position, View convertView, ViewGroup parent);
}
