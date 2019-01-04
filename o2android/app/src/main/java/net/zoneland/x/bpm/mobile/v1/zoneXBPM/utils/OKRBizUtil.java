package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

/**
 * Created by FancyLou on 2016/12/28.
 */

public class OKRBizUtil {


    public static int getImageResourceByWorkStatus(String status) {
        if("AUTHORIZE".equalsIgnoreCase(status)) {
            return R.mipmap.authorize;
        }else if ("TACKBACK".equalsIgnoreCase(status)) {
            return R.mipmap.authorize;
        }else if ("AUTHORIZECANCEL".equalsIgnoreCase(status)) {
            return R.mipmap.authorize;
        }else if ("RESPONSIBILITY".equalsIgnoreCase(status)) {
            return R.mipmap.responsibility;
        }else if ("COOPERATE".equalsIgnoreCase(status)) {
            return R.mipmap.cooperate;
        }else if ("READ".equalsIgnoreCase(status)) {
            return R.mipmap.read;
        }else if ("DEPLOY".equalsIgnoreCase(status)) {
            return R.mipmap.deploy;
        }else if ("VIEW".equalsIgnoreCase(status)) {
            return R.mipmap.view;
        }
        return -1;
    }

    /**
     * okr 操作
     * @param key
     * @return
     */
    public static String getWorkOperationName(String key) {
        if ("view".equalsIgnoreCase(key)) {
            return OPERATION_VIEW;
        }else if ("edit".equalsIgnoreCase(key)) {
            return OPERATION_EDIT;
        }else if ("split".equalsIgnoreCase(key)) {
            return OPERATION_SPLIT;
        }else if ("authorize".equalsIgnoreCase(key)) {
            return OPERATION_AUTHORIZE;
        }else if ("tackBack".equalsIgnoreCase(key)) {
            return OPERATION_TACKBACK;
        }else if ("report".equalsIgnoreCase(key)) {
            return OPERATION_REPORT;
        }else if ("delete".equalsIgnoreCase(key)) {
            return OPERATION_DELETE;
        }else if ("CREATEWORK".equalsIgnoreCase(key)) {
            return OPERATION_FORM_CREATEWORK;
        }else if ("DEPLOY".equalsIgnoreCase(key)) {
            return OPERATION_FORM_DEPLOY;
        }else if ("ARCHIVE".equalsIgnoreCase(key)) {
            return OPERATION_FORM_ARCHIVE;
        }
        return null;
    }


    public static final String OPERATION_VIEW = "查看";
    public static final String OPERATION_EDIT = "编辑";
    public static final String OPERATION_SPLIT = "拆解";
    public static final String OPERATION_AUTHORIZE = "委派";
    public static final String OPERATION_TACKBACK = "收回";
    public static final String OPERATION_REPORT = "汇报";
    public static final String OPERATION_DELETE = "删除";
    public static final String OPERATION_FORM_CREATEWORK = "创建工作";
    public static final String OPERATION_FORM_DEPLOY = "部署";
    public static final String OPERATION_FORM_ARCHIVE = "归档";
}
