package com.x.meeting.assemble.control.service;

import com.google.gson.JsonElement;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.*;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.core.entity.Meeting;
import com.x.meeting.core.entity.MeetingConfigProperties;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author sword
 * @date 2023/04/19 16:47
 **/
public class HstService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HstService.class);
    private static final String SUCCESS_CODE = "0";
    private static final String CREATE_MEETING_API = "/api/v1/room/addRoomInfo";
    private static final String DELETE_MEETING_API = "/api/v1/room/delRoomInfo";
    private static final String APPEND_MEETING_USER_API = "/api/v1/room/authUser";
    private static final String RESERVE_MEETING_API = "/api/v1/room/reservation";
    private static final String FIXED_MEETING_API = "/api/v1/room/fixed";
    public static final String MEETING_WEB_URL = "/launch/toEnterMeeting.do?roomID=";
    public static final String CREATE_USER_API = "/api/v1/user/add";
    public static final String FIND_USER_API = "/api/v1/user/list";

    /**
     * 创建好视通在线会议
     * @param meeting
     * @param config
     * @return
     */
    public static boolean createMeeting(Meeting meeting, MeetingConfigProperties config){
        try {
            Map<String, Object> map = new HashMap<>(3);
            map.put("roomName", meeting.getSubject());
            if(BooleanUtils.isTrue(config.getOnlineConfig().getHstAuth())) {
                map.put("verifyMode", "1");
            }else{
                map.put("verifyMode", "3");
            }
            map.put("maxUserCount", 300);
            String url = config.getOnlineConfig().getHstUrl() + CREATE_MEETING_API;
            String token = ShaTools.getToken(config.getOnlineConfig().getHstKey(), config.getOnlineConfig().getHstSecret());
            List<NameValuePair> header = new ArrayList<>();
            header.add(new NameValuePair("Authorization", token));
            String res = HttpConnection.postAsString(url, header, XGsonBuilder.toJson(map));
            LOGGER.info("创建好视通会议：{}，返回：{}", meeting.getSubject(), res);
            if(StringUtils.isNotBlank(res)){
                ResObj resObj = XGsonBuilder.instance().fromJson(res, ResObj.class);
                if(SUCCESS_CODE.equals(resObj.getCode())){
                    String roomId = resObj.getData(HstMeeting.class).getRoomId();
                    meeting.setRoomId(roomId);
                    if(BooleanUtils.isTrue(config.getOnlineConfig().getHstAuth())) {
                        meeting.setRoomLink(config.getOnlineConfig().getHstUrl() + MEETING_WEB_URL + roomId);
                    }else{
                        meeting.setRoomLink(config.getOnlineConfig().getHstUrl() + MEETING_WEB_URL + roomId + "&userType=0");
                    }
                    appendMeetingUser(meeting, config);
                    reserveMeeting(meeting, config);
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return false;
    }

    /**
     * 好视通会议用户授权
     * 授权者名单格式：’用户名,权限值‘ 或 ’会议室ID,权限值‘ 多个用‘#’号隔开， 例：
     * paul,0#Mike,1#king,2 ;权限值定义：2 参会人 3 管理员，4初始管理员；权限值为0时，表示取消该用户会议室权限；会议ID为0时，则授予全部会议室。
     * @param meeting
     * @param config
     * @return
     */
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
            String userId = PropertyTools.getOrElse(person, config.getOnlineConfig().getO2ToHstUid(), String.class, person.getUnique());
            userId = StringUtils.isNoneBlank(userId) ? userId : person.getUnique();
            userList.add(userId+",3");
            if(StringUtils.isNotBlank(meeting.getHostPerson()) && !meeting.getHostPerson().equals(meeting.getApplicant())){
                person = business.organization().person().getObject(meeting.getHostPerson());
                if(person != null) {
                    userId = PropertyTools.getOrElse(person, config.getOnlineConfig().getO2ToHstUid(), String.class, person.getUnique());
                    userId = StringUtils.isNoneBlank(userId) ? userId : person.getUnique();
                    userList.add(userId + ",3");
                }
            }

            for(String user : meeting.getInvitePersonList()){
                if(!user.equals(meeting.getApplicant()) && !user.equals(meeting.getHostPerson())) {
                    person = business.organization().person().getObject(user);
                    if(person != null) {
                        userId = PropertyTools.getOrElse(person, config.getOnlineConfig().getO2ToHstUid(), String.class, person.getUnique());
                        userId = StringUtils.isNoneBlank(userId) ? userId : person.getUnique();
                        if(existUser(userId, config)) {
                            userList.add(userId + ",2");
                        }
                    }
                }
            }
            if(ListTools.isNotEmpty(meeting.getInviteDelPersonList())){
                for(String user : meeting.getInviteDelPersonList()){
                    if(!user.equals(meeting.getApplicant()) && !user.equals(meeting.getHostPerson())) {
                        person = business.organization().person().getObject(user);
                        if(person != null) {
                            userId = PropertyTools.getOrElse(person, config.getOnlineConfig().getO2ToHstUid(), String.class, person.getUnique());
                            userId = StringUtils.isNoneBlank(userId) ? userId : person.getUnique();
                            if(existUser(userId, config)) {
                                userList.add(userId + ",0");
                            }
                        }
                    }
                }
            }

            map.put("roomUserStr", StringUtils.join(userList, "#"));
            String url = config.getOnlineConfig().getHstUrl() + APPEND_MEETING_USER_API;
            String token = ShaTools.getToken(config.getOnlineConfig().getHstKey(), config.getOnlineConfig().getHstSecret());
            List<NameValuePair> header = new ArrayList<>();
            header.add(new NameValuePair("Authorization", token));
            String res = HttpConnection.postAsString(url, header, XGsonBuilder.toJson(map));
            LOGGER.info("好视通会议用户授权 request:{}-----resp:{}",XGsonBuilder.toJson(map), res);
            if(StringUtils.isNotBlank(res)){
                ResObj resObj = XGsonBuilder.instance().fromJson(res, ResObj.class);
                if(SUCCESS_CODE.equals(resObj.getCode())){
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return false;
    }

    /**
     * 修改会议时间
     * @param meeting
     * @param config
     * @return
     */
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

            String url = config.getOnlineConfig().getHstUrl() + FIXED_MEETING_API;
            if(!DateTools.beforeNowMinutesNullIsTrue(meeting.getStartTime(), -30)) {
                map.put("hopeStartTime", DateTools.format(DateTools.getAdjustTimeDay(meeting.getStartTime(), 0, 0, -30, 0)));
                map.put("hopeEndTime", DateTools.format(meeting.getCompletedTime()));
                url = config.getOnlineConfig().getHstUrl() + RESERVE_MEETING_API;
            }
            String token = ShaTools.getToken(config.getOnlineConfig().getHstKey(), config.getOnlineConfig().getHstSecret());
            List<NameValuePair> header = new ArrayList<>();
            header.add(new NameValuePair("Authorization", token));
            String res = HttpConnection.postAsString(url, header, XGsonBuilder.toJson(map));
            if(StringUtils.isNotBlank(res)){
                LOGGER.info("修改会议预约时间 request:{}-----resp:{}",XGsonBuilder.toJson(map), res);
                ResObj resObj = XGsonBuilder.instance().fromJson(res, ResObj.class);
                if(SUCCESS_CODE.equals(resObj.getCode())){
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return false;
    }

    /**
     * 取消会议
     * @param meeting
     * @param config
     * @return
     */
    public static boolean deleteMeeting(Meeting meeting, MeetingConfigProperties config){
        if(StringUtils.isBlank(meeting.getRoomId())){
            return false;
        }
        try {
            Map<String, Object> map = new HashMap<>(1);
            map.put("roomId", meeting.getRoomId());
            String url = config.getOnlineConfig().getHstUrl() + DELETE_MEETING_API;
            String token = ShaTools.getToken(config.getOnlineConfig().getHstKey(), config.getOnlineConfig().getHstSecret());
            List<NameValuePair> header = new ArrayList<>();
            header.add(new NameValuePair("Authorization", token));
            String res = HttpConnection.postAsString(url, header, XGsonBuilder.toJson(map));
            if(StringUtils.isNotBlank(res)){
                LOGGER.info("取消会议:{}-----返回:{}",meeting.getRoomId(), res);
                ResObj resObj = XGsonBuilder.instance().fromJson(res, ResObj.class);
                if(SUCCESS_CODE.equals(resObj.getCode())){
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return false;
    }

    public static boolean existUser(String userId, MeetingConfigProperties config){
        try {
            Map<String, Object> map = new HashMap<>(2);
            map.put("searchKey", userId);
            map.put("searchType", "1");
            String url = config.getOnlineConfig().getHstUrl() + FIND_USER_API;
            String token = ShaTools.getToken(config.getOnlineConfig().getHstKey(), config.getOnlineConfig().getHstSecret());
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
