package com.x.meeting.assemble.control.service;

import com.google.gson.JsonElement;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ShaTools;
import com.x.base.core.project.tools.SslTools;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.core.entity.Meeting;
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
    private static final String APPEND_MEETING_USER_API = "/api/v1/room/authUser";
    private static final String RESERVE_MEETING_API = "/api/v1/room/reservation";
    public static final String MEETING_WEB_URL = "/launch/toEnterMeeting.do?roomID=";
    public static final String CREATE_USER_API = "/api/v1/user/add";
    public static final String FIND_USER_API = "/api/v1/user/list";

    public static boolean createMeeting(Meeting meeting, MeetingConfigProperties config){
        try {
            Map<String, Object> map = new HashMap<>(3);
            map.put("roomName", meeting.getSubject());
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
                    String roomId = resObj.getData(HstMeeting.class).getRoomId();
                    meeting.setRoomId(roomId);
                    meeting.setRoomLink(config.getOnlineConfig().getHstUrl() + MEETING_WEB_URL + roomId);

                    appendMeetingUser(meeting, config);
//                    reserveMeeting(meeting, config);
                    return true;
                }else{
                    LOGGER.warn("好视通创建会议：{}，失败：{}", meeting.getSubject(), res);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return false;
    }

    public static boolean appendMeetingUser(Meeting meeting, MeetingConfigProperties config){
        if(StringUtils.isBlank(meeting.getRoomId())){
            return false;
        }
        try {
            Business business = new Business(null);
            Map<String, Object> map = new HashMap<>(3);
            map.put("roomId", meeting.getRoomId());
            List<String> userList = new ArrayList<>();
            Person person = business.organization().person().getObject(meeting.getApplicant());
            String userId = StringUtils.isNoneBlank(person.getEmployee()) ? person.getEmployee() : person.getUnique();
            userList.add(userId+",3");
            if(StringUtils.isNotBlank(meeting.getHostPerson()) && !meeting.getHostPerson().equals(meeting.getApplicant())){
                person = business.organization().person().getObject(meeting.getHostPerson());
                if(person != null) {
                    userId = StringUtils.isNoneBlank(person.getEmployee()) ? person.getEmployee() : person.getUnique();
                    userList.add(userId + ",3");
                }
            }
            for(String user : meeting.getInvitePersonList()){
                if(!user.equals(meeting.getApplicant()) && !user.equals(meeting.getHostPerson())) {
                    person = business.organization().person().getObject(user);
                    if(person != null) {
                        userId = StringUtils.isNoneBlank(person.getEmployee()) ? person.getEmployee() : person.getUnique();
                        userList.add(userId + ",2");
                    }
                }
            }
            map.put("roomUserStr", StringUtils.join(userList, "#"));
            String url = config.getOnlineConfig().getHstUrl() + APPEND_MEETING_USER_API;
            String token = ShaTools.getToken(config.getOnlineConfig().getHstKey(), config.getOnlineConfig().getHstSecret());
            List<NameValuePair> header = new ArrayList<>();
            header.add(new NameValuePair("Authorization", token));
            String res = HttpConnection.postAsString(url, header, XGsonBuilder.toJson(map));
            if(StringUtils.isNotBlank(res)){
                LOGGER.info("request:{}-----resp:{}",XGsonBuilder.toJson(map), res);
                ResObj resObj = XGsonBuilder.instance().fromJson(res, ResObj.class);
                if(SUCCESS_CODE.equals(resObj.getCode())){
                    return true;
                }else{
                    LOGGER.warn("给好视通会议室：{}授权用户失败：{}", meeting.getRoomId(), res);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return false;
    }

    public static boolean reserveMeeting(Meeting meeting, MeetingConfigProperties config){
        if(StringUtils.isBlank(meeting.getRoomId())){
            return false;
        }
        if(meeting.getStartTime() == null || meeting.getCompletedTime() == null){
            return false;
        }
        try {
            Map<String, Object> map = new HashMap<>(3);
            map.put("roomId", meeting.getRoomId());
            map.put("hopeStartTime", DateTools.format(meeting.getStartTime()));
            map.put("hopeEndTime", DateTools.format(meeting.getCompletedTime()));
            String url = config.getOnlineConfig().getHstUrl() + RESERVE_MEETING_API;
            String token = ShaTools.getToken(config.getOnlineConfig().getHstKey(), config.getOnlineConfig().getHstSecret());
            List<NameValuePair> header = new ArrayList<>();
            header.add(new NameValuePair("Authorization", token));
            String res = HttpConnection.postAsString(url, header, XGsonBuilder.toJson(map));
            if(StringUtils.isNotBlank(res)){
                LOGGER.info("request:{}-----resp:{}",XGsonBuilder.toJson(map), res);
                ResObj resObj = XGsonBuilder.instance().fromJson(res, ResObj.class);
                if(SUCCESS_CODE.equals(resObj.getCode())){
                    return true;
                }else{
                    LOGGER.warn("给好视通会议室：{}授权用户失败：{}", meeting.getRoomId(), res);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return false;
    }

    public static boolean createPerson(){
        try {
            Map<String, Object> map = new HashMap<>(4);
            map.put("userName", "caixiangyi");
            map.put("mobile", "15268803358");
            map.put("nickName", "蔡祥熠");
            map.put("password", "o2oa@2022");
            String url = "https://117.133.7.109:8443" + CREATE_USER_API;
            String token = ShaTools.getToken("4QY08Kyh", "HpQi5csSMrufkM)b&#YWVlr7o*wWUG3G");
            List<NameValuePair> header = new ArrayList<>();
            header.add(new NameValuePair("Authorization", token));
            String res = HttpConnection.postAsString(url, header, XGsonBuilder.toJson(map));
            if(StringUtils.isNotBlank(res)){
                ResObj resObj = XGsonBuilder.instance().fromJson(res, ResObj.class);
                if(SUCCESS_CODE.equals(resObj.getCode())){
                    return true;
                }else {
                    LOGGER.info("创建用户：{},失败：{}", "", res);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return false;
    }

    public static boolean existUser(String userId){
        try {
            Map<String, Object> map = new HashMap<>(2);
            map.put("searchKey", userId);
            map.put("searchType", "1");
            String url = "https://117.133.7.109:8443" + FIND_USER_API;
            String token = ShaTools.getToken("4QY08Kyh", "HpQi5csSMrufkM)b&#YWVlr7o*wWUG3G");
            List<NameValuePair> header = new ArrayList<>();
            header.add(new NameValuePair("Authorization", token));
            String res = HttpConnection.postAsString(url, header, XGsonBuilder.toJson(map));
            if(StringUtils.isNotBlank(res)){
                ResObj resObj = XGsonBuilder.instance().fromJson(res, ResObj.class);
                if(SUCCESS_CODE.equals(resObj.getCode())){
                    Integer total = resObj.getData(HstUserRes.class).getTotal();
                    if(total > 0) {
                        return true;
                    }
                }else{
                    LOGGER.warn("查询用户：{},失败：{}", userId, res);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }

        return false;
    }

    public static void main(String[] args) throws Exception{
        SslTools.ignoreSsl();
        try {
            Map<String, Object> map = new HashMap<>(3);
            map.put("roomId", "10015");
            List<String> userList = new ArrayList<>();
            userList.add("chengjian"+",3");
            map.put("roomUserStr", StringUtils.join(userList, "#"));
            String url = "https://117.133.7.109:8443" + APPEND_MEETING_USER_API;
            String token = ShaTools.getToken("4QY08Kyh", "HpQi5csSMrufkM)b&#YWVlr7o*wWUG3G");
            List<NameValuePair> header = new ArrayList<>();
            header.add(new NameValuePair("Authorization", token));
            String res = HttpConnection.postAsString(url, header, XGsonBuilder.toJson(map));
            if(StringUtils.isNotBlank(res)){
                ResObj resObj = XGsonBuilder.instance().fromJson(res, ResObj.class);
                LOGGER.info(res);
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
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

    public static class HstMeeting{
        private String roomId;

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }
    }

    public static class HstUserRes{
        private Integer total;

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }
    }
}
