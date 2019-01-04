package jiguang.chat.pickerimage.view;


import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;


public class ToolBarOptions {
    /**
     * toolbar的title资源id
     */
    public int titleId = 0;
    /**
     * toolbar的title
     */
    public String titleString;
    /**
     * toolbar的返回按钮资源id，默认开启的资源
     */
    public int navigateId = R.drawable.back;
    /**
     * toolbar的返回按钮，默认开启
     */
    public boolean isNeedNavigate = true;
}
