package com.x.meeting.assemble.control.service;

import com.google.gson.JsonElement;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ShaTools;
import com.x.meeting.core.entity.MeetingConfigProperties;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sword
 * @date 2023/04/19 16:47
 **/
public class HstService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HstService.class);
    private static final String SUCCESS_CODE = "0";
    private static final String CREATE_MEETING_API = "/api/v1/room/addRoomInfo";
    public static final String MEETING_WEB_URL = "/launch/toEnterMeeting.do?roomID=";

    public static String createMeeting(String roomName, MeetingConfigProperties config){
        String roomId = "";
        try {
            Map<String, Object> map = new HashMap<>(3);
            map.put("roomName", roomName);
            map.put("verifyMode", "1");
            map.put("maxUserCount", 300);
            String url = config.getOnlineConfig().getHstUrl() + CREATE_MEETING_API;
            String token = ShaTools.getToken(config.getOnlineConfig().getHstKey(), config.getOnlineConfig().getHstSecret());
            List<NameValuePair> header = new ArrayList<>();
            header.add(new NameValuePair("Authorization", token));
            String res = HttpConnection.postAsString(url, header, XGsonBuilder.toJson(map));
            if(StringUtils.isNotBlank(res)){
                ResObj resObj = XGsonBuilder.instance().fromJson(res, ResObj.class);
                if(SUCCESS_CODE.equals(resObj.getCode())){
                    roomId = resObj.getData(HstMeeting.class).getRoomId();
                }else{
                    LOGGER.warn("好视通创建会议：{}，失败：{}", roomName, res);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return roomId;
    }

    public static class ResObj{

        private String code;
        private String msg;
        private JsonElement data;

        public<T> T getData(Class<T> c){
            if(data != null) {
                T t = XGsonBuilder.instance().fromJson(data, c);
                return t;
            }else{
                return null;
            }
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public JsonElement getData() {
            return data;
        }

        public void setData(JsonElement data) {
            this.data = data;
        }
    }

    public class HstMeeting{
        private String roomId;

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }
    }
}
