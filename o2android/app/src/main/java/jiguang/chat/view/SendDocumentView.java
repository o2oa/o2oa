package jiguang.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.adapter.DocumentAdapter;


public class SendDocumentView extends LinearLayout {

    private ListView mListVew;


    public SendDocumentView(Context context) {
        super(context);
    }

    public SendDocumentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initModule() {
        mListVew = (ListView) findViewById(R.id.file_list_view);
    }

    public void setAdapter(DocumentAdapter adapter) {
        mListVew.setAdapter(adapter);
    }
}
