package jiguang.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.LinearLayout;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.activity.historyfile.adapter.ImageFileAdapter;
import jiguang.chat.activity.historyfile.grideviewheader.StickyGridHeadersGridView;
import jiguang.chat.adapter.ImageAdapter;


public class SendImageView extends LinearLayout {

    private GridView mImageGV;
    private StickyGridHeadersGridView mHistroyImage;

    public SendImageView(Context context) {
        super(context);
    }

    public SendImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initModule() {
        mImageGV = (GridView) findViewById(R.id.album_grid_view);
    }

    public GridView initFileViewModule() {
        mHistroyImage = (StickyGridHeadersGridView) findViewById(R.id.asset_grid);
        return mHistroyImage;
    }

    public void setAdapter(ImageAdapter adapter) {
        mImageGV.setAdapter(adapter);
    }

    public void setFileAdapter(ImageFileAdapter adapter) {
        mHistroyImage.setAdapter(adapter);
    }


}
