package jiguang.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.adapter.AudioAdapter;


public class SendAudioView extends LinearLayout {

    private ListView mListView;

    public SendAudioView(Context context) {
        super(context);
    }

    public SendAudioView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initModule() {
        mListView = (ListView) findViewById(R.id.audio_list_view);
    }

    public void setAdapter(AudioAdapter adapter) {
        mListView.setAdapter(adapter);
    }
}
