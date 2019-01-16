package jiguang.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.adapter.VideoAdapter;


public class SendVideoView extends LinearLayout {

    private ListView mListView;

    public SendVideoView(Context context) {
        super(context);
    }

    public SendVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initModule() {
        mListView = (ListView) findViewById(R.id.video_list_view);
    }

    public void setAdapter(VideoAdapter adapter) {
        mListView.setAdapter(adapter);
    }
}
