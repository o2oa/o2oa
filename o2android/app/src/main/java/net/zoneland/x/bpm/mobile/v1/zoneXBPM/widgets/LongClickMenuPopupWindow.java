package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonAdapter;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.ViewHolder;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FancyLou on 2016/9/22.
 */
public class LongClickMenuPopupWindow extends PopupWindow {

    private int width;
    private int height;
    private View mConvertView;
    private ListView itemMenuListView;

    private List<String> menuList;
    private CommonAdapter<String> adapter;
    private MenuItemClickListener listener;

    public interface MenuItemClickListener  {
        void itemClick(int position);
    }

    public LongClickMenuPopupWindow(Context context) {
        super(context);
        mConvertView = LayoutInflater.from(context).inflate(R.layout.pop_person_avatar_menu, null);
        itemMenuListView = (ListView) mConvertView.findViewById(R.id.person_info_avatar_menu_list_id);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        menuList = new ArrayList<>();
        adapter = new CommonAdapter<String>(context, menuList, R.layout.item_person_mobile_menu) {
            @Override
            public void convert(ViewHolder holder, String s) {
                holder.setText(R.id.person_info_mobile_menu_text_id, s);
            }
        };
        itemMenuListView.setAdapter(adapter);
        itemMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener!=null) {
                    listener.itemClick(position);
                }
                dismiss();
            }
        });

    }

    /**
     * 初始化菜单列表
     * @param list
     */
    public void initMenu(List<String> list) {
        if (menuList==null) {
            menuList = new ArrayList<>();
        }
        menuList.clear();
        menuList.addAll(list);
        adapter.notifyDataSetChanged();
    }




    public MenuItemClickListener getListener() {
        return listener;
    }

    public void setListener(MenuItemClickListener listener) {
        this.listener = listener;
    }
}
