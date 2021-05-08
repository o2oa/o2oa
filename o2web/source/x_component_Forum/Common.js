MWF.xApplication.Forum = MWF.xApplication.Forum || {};
window.MWFForum = MWF.xApplication.Forum;
MWFForum.getDateDiff = function (publishTime, justNowStr) {
    if(!publishTime)return "";
    var dateTimeStamp = Date.parse(publishTime.replace(/-/gi, "/"));
    var minute = 1000 * 60;
    var hour = minute * 60;
    var day = hour * 24;
    var halfamonth = day * 15;
    var month = day * 30;
    var year = month * 12;
    var now = new Date().getTime();
    var diffValue = now - dateTimeStamp;
    if (diffValue < 0) {
        //若日期不符则弹出窗口告之
        //alert("结束日期不能小于开始日期！");
    }
    var yesterday = new Date().decrement('day', 1);
    var beforYesterday = new Date().decrement('day', 2);
    var yearC = diffValue / year;
    var monthC = diffValue / month;
    var weekC = diffValue / (7 * day);
    var dayC = diffValue / day;
    var hourC = diffValue / hour;
    var minC = diffValue / minute;
    if (yesterday.getFullYear() == dateTimeStamp.getFullYear() && yesterday.getMonth() == dateTimeStamp.getMonth() && yesterday.getDate() == dateTimeStamp.getDate()) {
        result = MWF.xApplication.Forum.LP.yesterday + " " + dateTimeStamp.getHours() + ":" + dateTimeStamp.getMinutes();
    } else if (beforYesterday.getFullYear() == dateTimeStamp.getFullYear() && beforYesterday.getMonth() == dateTimeStamp.getMonth() && beforYesterday.getDate() == dateTimeStamp.getDate()) {
        result = MWF.xApplication.Forum.LP.twoDaysAgo + " " + dateTimeStamp.getHours() + ":" + dateTimeStamp.getMinutes();
    } else if (yearC > 1) {
        result = dateTimeStamp.getFullYear() + "-" + (dateTimeStamp.getMonth() + 1) + "-" + dateTimeStamp.getDate();
    } else if (monthC >= 1) {
        //result= parseInt(monthC) + "个月前";
        // s.getFullYear()+"年";
        result = dateTimeStamp.getFullYear() + "-" + (dateTimeStamp.getMonth() + 1) + "-" + dateTimeStamp.getDate();
    } else if (weekC >= 1) {
        result = parseInt(weekC) + MWF.xApplication.Forum.LP.weekAgo;
    } else if (dayC >= 1) {
        result = parseInt(dayC) + MWF.xApplication.Forum.LP.dayAgo;
    } else if (hourC >= 1) {
        result = parseInt(hourC) + MWF.xApplication.Forum.LP.hourAgo;
    } else if (minC >= 1) {
        result = parseInt(minC) + MWF.xApplication.Forum.LP.minuteAgo;
    } else
        result = justNowStr || MWF.xApplication.Forum.LP.publishJustNow;
    return result;
};

MWFForum.getDateDiff2 = function (publishTime) {
    return MWFForum.getDateDiff( publishTime, MWF.xApplication.Forum.LP.justNow );
};

MWFForum.BBS_LOGO_NAME = "BBS_LOGO_NAME";
MWFForum.BBS_SUBJECT_TYPECATAGORY = "BBS_SUBJECT_TYPECATAGORY";
MWFForum.BBS_TITLE_TAIL = "BBS_TITLE_TAIL";

MWFForum.getSystemConfig = function( code ){
    if( !MWFForum.SystemSetting )MWFForum.SystemSetting = {};

    if( typeOf( MWFForum.SystemSetting[code] ) !== "null" )return MWFForum.SystemSetting[code];

    o2.Actions.load("x_bbs_assemble_control").BBSConfigSettingAction.getByCode( {configCode : code }, function(json) {
        MWFForum.SystemSetting[code] = json.data;
    }.bind(this), function(){
        MWFForum.SystemSetting[code] = "";
    }, false );

    return MWFForum.SystemSetting[code];
};

MWFForum.getSystemConfigValue = function( code ){
    var config = MWFForum.getSystemConfig(code);
    if( config && config.configValue ){
        return config.configValue;
    }else{
        return "";
    }
};

MWFForum.getBBSName = function(){
    if( typeOf( MWFForum.BBSName ) !== "null" )return MWFForum.BBSName;

    o2.Actions.load("x_bbs_assemble_control").BBSConfigSettingAnonymousAction.getBBSName( function(json) {
        MWFForum.BBSName = (json.data || {}).configValue;
    }.bind(this), function(){
        MWFForum.BBSName = "";
    }, false );

    return MWFForum.BBSName;
};
