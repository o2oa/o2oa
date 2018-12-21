/*
 * resources.getEntityManagerContainer() // 实体管理容器.
 * resources.getContext() //上下文根.
 * resources.getOrganization() //组织访问接口.
 * requestText //请求内容.
 * request //请求对象.
 */


load("nashorn:mozilla_compat.js");

print("----发送待办开始---------");

//var Todo_Service_URL = 'http://10.11.198.224:9083/UnifiedWorkbench/ProcessTaskService';
//WSDL http://10.11.198.224:9083/UnifiedWorkbench/ProcessTaskService/ProcessTaskService.wsdl
//var Read_Service_URL ='http://10.11.198.224:9083/UnifiedWorkbench/ProcessReadService';
//WSDL http://10.11.198.224:9083/UnifiedWorkbench/ProcessReadService/ProcessReadService.wsdl

var Todo_Service_URL = 'http://10.11.198.209:9083/UnifiedWorkbench/ProcessTaskService';
var Read_Service_URL ='http://10.11.198.209:9083/UnifiedWorkbench/ProcessReadService';

var Project_Name = '安徽智和';
var Project_Password = 'ahzh';

function getServerHost(){
    return "http://zhtest.ah.unicom.local";
}

function getServerPort(){
    var server_port = "8080";
    return (server_port == "" || server_port == "80") ? "" : (":"+server_port);
}

function init(){
    var resp = resources.getContext().applications().getQuery(com.x.base.core.project.x_message_assemble_communicate.class, 'consume/list/sync2todo/count/30');

    var text = resp.getData().toString();

    var list = JSON.parse(text);

    for (var i = 0; i < list.length; i++) {
        var item = list[i];
        switch (item.type) {
            case 'task_create':
                var task = JSON.parse(item.body);
                send_task_create(item.id, task);
                break;
            case 'taskCompleted_create':
                var task = JSON.parse(item.body);
                send_taskCompleted_create(item.id, task);
                break;
            case 'taskCompleted_delete':
                var task = JSON.parse(item.body);
                send_task_delete(item.id, task);
                break;
            case 'read_create':
                var read = JSON.parse(item.body);
                send_read_create(item.id, read);
                break;

            case 'readCompleted_create':
                var read = JSON.parse(item.body);
                send_readCompleted_create(item.id,read);
                break;

            case 'task_delete':
                var task = JSON.parse(item.body);
                send_task_delete(item.id, task);
                break;

            case 'read_delete':
                var read = JSON.parse(item.body);
                send_read_delete(item.id, read);
                break;

            default:
                break;
        }

    }
}

function getPersonFeature( person ){
    return "wangyj733";
}

function getPersonAttribute(userid , attributeName ){
    var org = resources.getOrganization();
    var _list = org.personAttribute().listAttributeWithPersonWithName(userid, attributeName);
    if(_list.length===0){
        return userid;
    } else {
        if(_list[0]&&_list[0]!=""){
            return _list[0];
        } else {
            return userid;
        }
    }
}

function concatUrl(task) {
    return getServerHost() + getServerPort() + "/x_desktop/work.html?workid=" + task.work;
}

function concatTaskUrl(task){
    return getServerHost() + getServerPort() + "/x_desktop/work.html?taskid=" + task.id;
}

function concatTaskCompletedUrl(taskcompleted){
    return getServerHost() + getServerPort() + "/x_desktop/work.html?taskcompletedid=" + taskcompleted.id;
}

function concatReadUrl(read){
    return getServerHost() + getServerPort() + "/x_desktop/work.html?readid=" + read.id;
}

function concatReadCompletedUrl(readcompleted){
    return getServerHost() + getServerPort() + "/x_desktop/work.html?readcompletedid=" + readcompleted.id;
}

function getXMLText(root, tag){
    var list = root.getElementsByTagName(tag);
    if( list == null )return null;
    var node = list.item(0);
    if( node == null )return null;
    return node.getTextContent();
}

function updateConsume( id ){
    try {
        resources.getContext().applications().getQuery(com.x.base.core.project.x_message_assemble_communicate.class, 'consume/' + id + '/type/sync2todo');
    }catch(e){
        print("更新消息,将消息标志为已处理出错，id="+id + "：");
        print(  e.printStackTrace() );
        return false;
    }
}

function isRespSuccess( xmlStr ){
    try {
        var DocumentBuilderFactory = Java.type('javax.xml.parsers.DocumentBuilderFactory');
        var StringReader = Java.type('java.io.StringReader');
        var InputSource = Java.type('org.xml.sax.InputSource');

        var dbf = DocumentBuilderFactory.newInstance();
        var db = dbf.newDocumentBuilder();
        var sr = new StringReader(xmlStr);
        var is = new InputSource(sr);
        var document = db.parse(is);
        var root = document.getDocumentElement();

        var faultcode = getXMLText(root, "faultcode");
        var faultstring = getXMLText(root, "faultstring");

        if (faultcode) {
            return false
        } else {
            return true
        }
    }catch(e){
        print("解析返回结果报错：");
        print(  e.printStackTrace() );
        return false
    }
}


function sendRequest_task( xml ){
    try{
        print("发起请求:"+xml);
        var ArrayList = Java.type('java.util.ArrayList');
        var heads = new ArrayList();
        var NameValuePair = Java.type('com.x.base.core.project.bean.NameValuePair');
        var p1 = new NameValuePair('Content-Type', 'text/xml; charset=utf-8');
        heads.add(p1);
        var HttpConnectionClass = Java.type('com.x.base.core.project.connection.HttpConnection');
        var resp = HttpConnectionClass.postAsString(Todo_Service_URL, heads, xml);
        print( "统一待办返回:"+resp );
        return isRespSuccess( resp );
    }catch(e){
        print("发送请求出错：");
        print(  e.printStackTrace() );
    }
}

function get_task_create_xml( task ){
    var xml = '';
    xml += '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:proc="http://processtask.service.webservice.unifiedworkbench.zoneland.net/">';
    xml +=      '<soapenv:Header/>';
    xml +=      '<soapenv:Body>';
    xml +=          '<proc:update>';
    xml +=              '<arg0>'+ Project_Name +'</arg0>'; //项目名称projectName，每个应用都分开
    xml +=              '<arg1>'+ Project_Password +'</arg1>'; //接入密码key，每个应用都分开
    xml +=              '<arg2>'+ getPersonFeature(task.person) +'</arg2>'; //targets, 办理人
    xml +=              '<arg3>'+ '' +'</arg3>'; //processor, 上一个环节的处理人, 如果只生成待办不为处理人生成已办，那么发送人可不填写
    xml +=              '<arg4>'+ getPersonFeature( task.creatorPerson) +'</arg4>'; //creator， 流程工单拟稿人
    xml +=              '<arg5>'+ task.opinion +'</arg5>'; //opinion， 流程处理意见
    xml +=              '<arg6>'+ task.title +'</arg6>'; //title， 工单标题
    xml +=              '<arg7>'+ task.work +'</arg7>'; //unid， 应用系统的待办唯一ID
    xml +=              '<arg8>'+ task.job +'</arg8>'; //additionalUnid 上级流程实例（工单）的唯一标识，如果有多层，可以用逗号分隔，主流程在第一位
    xml +=              '<arg9>'+ task.id +'</arg9>'; //feature， 流程实例（工单）唯一标识, 任务ID
    xml +=              '<arg10>'+ task.routeName +'</arg10>';  //reserve， 决策名称：送办理|归档|送审批等等
    xml +=              '<arg11>'+ task.startTime +'</arg11>'; //composeTime， 数据生成时间：2011-08-30 12:00:00
    xml +=              '<arg12>'+ task.processName +'</arg12>'; //process， 直接写流程名称
    xml +=              '<arg13>'+ task.processName +'</arg13>'; //fileType，文档类型，直接写流程名称
    xml +=              '<arg14>'+ task.activityName +'</arg14>'; //activity， 当前环节名称
    xml +=              '<arg15>'+ 'false' +'</arg15>'; //newly， 是否是草稿
    xml +=              '<arg16>'+ task.processName +'</arg16>'; //category，分类名称，直接写流程名称
    xml +=              '<arg17>'+ '' +'</arg17>'; //urgency，紧急程度：请默认“一般”
    xml +=              '<arg18>'+ '' +'</arg18>'; //secrecy，密级：请默认“普通文件”
    xml +=              '<arg19>'+ task.serial +'</arg19>'; //serial，文号：对公文必须，可以为空
    xml +=              '<arg20>'+ 0 +'</arg20>'; //expirationMinutes， 不要求，固定为0
    xml +=              '<arg21>'+ '' +'</arg21>'; //expirationTime，不要求，固定为""
    xml +=              '<arg22>'+ false +'</arg22>'; //hastenEnable，不要求，固定为false
    xml +=              '<arg23>'+ 0 +'</arg23>'; //hastenMinutes，不要求，固定为0
    xml +=              '<arg24>'+ '' +'</arg24>'; //hastenTime  不要求，催办时间，可以为空
    xml +=              '<arg25>'+ 'false' +'</arg25>'; //enableRemind|remindEnable，不要求，固定为false
    xml +=              '<arg26>'+ 'false' +'</arg26>'; //remindForceFlag|remindForce, 不要求，固定为false
    xml +=              '<arg27>'+ concatTaskUrl( task ) +'</arg27>'; //link, 访问链接
    xml +=              '<arg28>'+ '' +'</arg28>'; //linkView, 配合流程监控平台，江西移动环境接入为拟稿时间
    xml +=              '<arg29>'+ '' +'</arg29>'; //linkFlow, 办理链接, 可以为空
    xml +=              '<arg30>'+ '' +'</arg30>'; //linkRemove, 删除链接, 可以为空
    xml +=              '<arg31>'+ '' +'</arg31>'; //linkSupervise, 督办链接, 可以为空
    xml +=              '<arg32>'+ '' +'</arg32>'; //linkSuspend, 挂起链接, 可以为空
    xml +=              '<arg33>'+ '' +'</arg33>'; //linkReset, 重置链接, 可以为空
    xml +=              '<arg34>'+ '' +'</arg34>';  //linkReroute, 调度链接, 可以为空
    xml +=          '</proc:update>';
    xml +=      '</soapenv:Body>';
    xml += '</soapenv:Envelope>';
    return xml;
}

function send_task_create(id, task) {
    print("======send_task_create start=========" + ", consume id  : " + id);
    var xml = get_task_create_xml( task );
    if( sendRequest_task( xml ) ){
        updateConsume( id );
    }
    print("======send_task_create end=========" + ", consume id : " + id);
}

function get_task_delete_xml( task ){
    var xml = '';
    xml += '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:proc="http://processtask.service.webservice.unifiedworkbench.zoneland.net/">';
    xml +=      '<soapenv:Header/>';
    xml +=      '<soapenv:Body>';
    xml +=          '<proc:removeTaskAsFeature>';
    xml +=              '<arg0>'+ Project_Name +'</arg0>'; //项目名称projectName，每个应用都分开
    xml +=              '<arg1>'+ Project_Password +'</arg1>'; //接入密码key，每个应用都分开
    xml +=              '<arg2>'+ task.id +'</arg2>'; //需要删除的 Feature
    xml +=          '</proc:removeTaskAsFeature>';
    xml +=      '</soapenv:Body>';
    xml += '</soapenv:Envelope>';
    return xml;
}



function send_task_delete(id, task) {
    print("--------send_task_delete start------------" + ", consume id : " + id);
    var xml = get_task_delete_xml( task );
    if( sendRequest_task( xml ) ){
        updateConsume( id );
    }
    print("--------send_task_delete end------------" + ", consume id : " + id);
}

function get_taskCompleted_create_xml( task ){
    var xml = '';
    xml += '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:proc="http://processtask.service.webservice.unifiedworkbench.zoneland.net/">';
    xml +=      '<soapenv:Header/>';
    xml +=      '<soapenv:Body>';
    xml +=          '<proc:update>';
    xml +=              '<arg0>'+ Project_Name +'</arg0>'; //项目名称projectName，每个应用都分开
    xml +=              '<arg1>'+ Project_Password +'</arg1>'; //接入密码key，每个应用都分开
    xml +=              '<arg2>'+ '' +'</arg2>'; //targets, 办理人
    xml +=              '<arg3>'+ getPersonFeature( task.person ) +'</arg3>'; //processor, 上一个环节的处理人, 如果只生成待办不为处理人生成已办，那么发送人可不填写
    xml +=              '<arg4>'+ getPersonFeature( task.creatorPerson ) +'</arg4>'; //creator， 流程工单拟稿人
    xml +=              '<arg5>'+ task.opinion +'</arg5>'; //opinion， 流程处理意见
    xml +=              '<arg6>'+ task.title +'</arg6>'; //title， 工单标题
    xml +=              '<arg7>'+ task.work +'</arg7>'; //unid， 应用系统的待办唯一ID
    xml +=              '<arg8>'+ task.job +'</arg8>'; //additionalUnid 上级流程实例（工单）的唯一标识，如果有多层，可以用逗号分隔，主流程在第一位
    xml +=              '<arg9>'+ task.id +'</arg9>'; //feature， 流程实例（工单）唯一标识, 任务ID
    xml +=              '<arg10>'+ task.routeName +'</arg10>';  //reserve， 决策名称：送办理|归档|送审批等等
    xml +=              '<arg11>'+ task.startTime +'</arg11>'; //composeTime， 数据生成时间：2011-08-30 12:00:00
    xml +=              '<arg12>'+ task.processName +'</arg12>'; //process， 直接写流程名称
    xml +=              '<arg13>'+ task.processName +'</arg13>'; //fileType，文档类型，直接写流程名称
    xml +=              '<arg14>'+ task.activityName +'</arg14>'; //activity， 当前环节名称
    xml +=              '<arg15>'+ 'false' +'</arg15>'; //newly， 是否是草稿
    xml +=              '<arg16>'+ task.processName +'</arg16>'; //category，分类名称，直接写流程名称
    xml +=              '<arg17>'+ '' +'</arg17>'; //urgency，紧急程度：请默认“一般”
    xml +=              '<arg18>'+ '' +'</arg18>'; //secrecy，密级：请默认“普通文件”
    xml +=              '<arg19>'+ task.serial +'</arg19>'; //serial，文号：对公文必须，可以为空
    xml +=              '<arg20>'+ 0 +'</arg20>'; //expirationMinutes， 不要求，固定为0
    xml +=              '<arg21>'+ '' +'</arg21>'; //expirationTime，不要求，固定为""
    xml +=              '<arg22>'+ false +'</arg22>'; //hastenEnable，不要求，固定为false
    xml +=              '<arg23>'+ 0 +'</arg23>'; //hastenMinutes，不要求，固定为0
    xml +=              '<arg24>'+ '' +'</arg24>'; //hastenTime  不要求，催办时间，可以为空
    xml +=              '<arg25>'+ 'false' +'</arg25>'; //enableRemind|remindEnable，不要求，固定为false
    xml +=              '<arg26>'+ 'false' +'</arg26>'; //remindForceFlag|remindForce, 不要求，固定为false
    xml +=              '<arg27>'+ concatTaskCompletedUrl( task ) +'</arg27>'; //link, 访问链接
    xml +=              '<arg28>'+ '' +'</arg28>'; //linkView, 配合流程监控平台，江西移动环境接入为拟稿时间
    xml +=              '<arg29>'+ '' +'</arg29>'; //linkFlow, 办理链接, 可以为空
    xml +=              '<arg30>'+ '' +'</arg30>'; //linkRemove, 删除链接, 可以为空
    xml +=              '<arg31>'+ '' +'</arg31>'; //linkSupervise, 督办链接, 可以为空
    xml +=              '<arg32>'+ '' +'</arg32>'; //linkSuspend, 挂起链接, 可以为空
    xml +=              '<arg33>'+ '' +'</arg33>'; //linkReset, 重置链接, 可以为空
    xml +=              '<arg34>'+ '' +'</arg34>';  //linkReroute, 调度链接, 可以为空
    xml +=          '</proc:update>';
    xml +=      '</soapenv:Body>';
    xml += '</soapenv:Envelope>';
    return xml;
}

function send_taskCompleted_create(id, task) {
    print("--------send_taskCompleted_create start------------" + ", consume id : " + id);
    var xml = get_taskCompleted_create_xml( task );
    if( sendRequest_task( xml ) ){
        updateConsume( id );
    }
    print("--------send_taskCompleted_create end------------" + ", consume id  : " + id);
}

function sendRequest_read(xml){
    try{
        print("发起请求:"+xml);
        var ArrayList = Java.type('java.util.ArrayList');
        var heads = new ArrayList();
        var NameValuePair = Java.type('com.x.base.core.project.bean.NameValuePair');
        var p1 = new NameValuePair('Content-Type', 'text/xml; charset=utf-8');
        heads.add(p1);
        var HttpConnectionClass = Java.type('com.x.base.core.project.connection.HttpConnection');
        var resp = HttpConnectionClass.postAsString(Read_Service_URL, heads, xml);
        print( "统一待办返回:"+resp );
        return isRespSuccess( resp );
    }catch(e){
        print("发送请求出错：");
        print(  e.printStackTrace() );
    }
}

function get_read_create_xml( read ){
    var xml = '';
    xml += '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:proc="http://processread.service.webservice.unifiedworkbench.zoneland.net/">';
    xml +=      '<soapenv:Header/>';
    xml +=      '<soapenv:Body>';
    xml +=          '<proc:update>';
    xml +=              '<arg0>'+ Project_Name +'</arg0>'; //项目名称projectName，每个应用都分开
    xml +=              '<arg1>'+ Project_Password +'</arg1>'; //接入密码key，每个应用都分开
    xml +=              '<arg2>'+ getPersonFeature( read.person ) +'</arg2>'; //targets, 待阅人
    xml +=              '<arg3>'+ '' +'</arg3>'; //processor, 上一个环节的处理人, 如果只生成待办不为处理人生成已办，那么发送人可不填写
    xml +=              '<arg4>'+ getPersonFeature( read.creatorPerson ) +'</arg4>'; //creator， 流程工单拟稿人
    xml +=              '<arg5>'+ read.title +'</arg5>'; //title， 工单标题
    xml +=              '<arg6>'+ read.work +'</arg6>'; //unid， 应用系统的待办唯一ID
    xml +=              '<arg7>'+ read.job +'</arg7>'; //additionalUnid 上级流程实例（工单）的唯一标识，如果有多层，可以用逗号分隔，主流程在第一位
    xml +=              '<arg8>'+ read.processName +'</arg8>'; //process， 直接写流程名称
    xml +=              '<arg9>'+ read.processName +'</arg9>'; //fileType，文档类型，直接写流程名称
    xml +=              '<arg10>'+ read.activityName +'</arg10>'; //activity， 当前环节名称
    xml +=              '<arg11>'+ read.processName +'</arg11>'; //category，分类名称，直接写流程名称
    xml +=              '<arg12>'+ '' +'</arg12>'; //urgency，紧急程度：请默认“一般”
    xml +=              '<arg13>'+ '' +'</arg13>'; //secrecy，密级：请默认“普通文件”
    xml +=              '<arg14>'+ read.serial +'</arg14>'; //serial，文号：对公文必须，可以为空
    xml +=              '<arg15>'+ read.id +'</arg15>'; //feature， 流程实例（工单）唯一标识, 任务ID
    xml +=              '<arg16>'+ '' +'</arg16>';  //reserve， 决策名称：送办理|归档|送审批等等
    xml +=              '<arg17>'+ 'false' +'</arg17>'; //enableRemind|remindEnable，不要求，固定为false
    xml +=              '<arg18>'+ 'false' +'</arg18>'; //remindForceFlag|remindForce, 不要求，固定为false
    xml +=              '<arg19>'+ read.startTime +'</arg19>'; //composeTime， 数据生成时间：2011-08-30 12:00:00
    xml +=              '<arg20>'+ '' +'</arg20>'; //expirationTime，不要求，固定为""
    xml +=              '<arg21>'+ '' +'</arg21>'; //hastenTime  不要求，催办时间，可以为空
    xml +=              '<arg22>'+ concatReadUrl( read ) +'</arg22>'; //link, 访问链接
    xml +=              '<arg23>'+ '' +'</arg23>'; //linkView, 配合流程监控平台，江西移动环境接入为拟稿时间
    xml +=              '<arg24>'+ '' +'</arg24>'; //linkFlow, 办理链接, 可以为空
    xml +=              '<arg25>'+ '' +'</arg25>'; //linkRemove, 删除链接, 可以为空
    xml +=              '<arg26>'+ '' +'</arg26>'; //linkSupervise, 督办链接, 可以为空
    xml +=              '<arg27>'+ '' +'</arg27>'; //linkSuspend, 挂起链接, 可以为空
    xml +=              '<arg28>'+ '' +'</arg28>'; //linkReset, 重置链接, 可以为空
    xml +=              '<arg29>'+ '' +'</arg29>';  //linkReroute, 调度链接, 可以为空
    xml +=          '</proc:update>';
    xml +=      '</soapenv:Body>';
    xml += '</soapenv:Envelope>';
    return xml;
}

function send_read_create(id, read){
    print("======send_read_create start=========" + ", consume id   : " + id);
    var xml = get_read_create_xml( read );
    if( sendRequest_read( xml ) ){
        updateConsume( id );
    }
    print("======send_read_create end=========" + ", consume id : " + id);
}


function get_read_delete_xml( read ){
    var xml = '';
    xml += '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:proc="http://processread.service.webservice.unifiedworkbench.zoneland.net/">';
    xml +=      '<soapenv:Header/>';
    xml +=      '<soapenv:Body>';
    xml +=          '<proc:removeReadAsFeature>';
    xml +=              '<arg0>'+ Project_Name +'</arg0>'; //项目名称projectName，每个应用都分开
    xml +=              '<arg1>'+ Project_Password +'</arg1>'; //接入密码key，每个应用都分开
    xml +=              '<arg2>'+ read.id +'</arg2>'; //需要删除的 Feature
    xml +=          '</proc:removeReadAsFeature>';
    xml +=      '</soapenv:Body>';
    xml += '</soapenv:Envelope>';
    return xml;
}


function send_read_delete(id, read ) {
    print("--------send_read_delete start------------" + ", consume id : " + id);
    var xml = get_read_delete_xml( read );
    if( sendRequest_read( xml ) ){
        updateConsume( id );
    }
    print("--------send_read_delete end------------" + ", consume id : " + id);
}

function get_read_create_xml( read ){
    var xml = '';
    xml += '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:proc="http://processread.service.webservice.unifiedworkbench.zoneland.net/">';
    xml +=      '<soapenv:Header/>';
    xml +=      '<soapenv:Body>';
    xml +=          '<proc:update>';
    xml +=              '<arg0>'+ Project_Name +'</arg0>'; //项目名称projectName，每个应用都分开
    xml +=              '<arg1>'+ Project_Password +'</arg1>'; //接入密码key，每个应用都分开
    xml +=              '<arg2>'+ '' +'</arg2>'; //targets, 待阅人
    xml +=              '<arg3>'+ getPersonFeature(read.person) +'</arg3>'; //processor, 上一个环节的处理人, 如果只生成待办不为处理人生成已办，那么发送人可不填写
    xml +=              '<arg4>'+ getPersonFeature(read.creatorPerson) +'</arg4>'; //creator， 流程工单拟稿人
    xml +=              '<arg5>'+ read.title +'</arg5>'; //title， 工单标题
    xml +=              '<arg6>'+ read.work +'</arg6>'; //unid， 应用系统的待办唯一ID
    xml +=              '<arg7>'+ read.job +'</arg7>'; //additionalUnid 上级流程实例（工单）的唯一标识，如果有多层，可以用逗号分隔，主流程在第一位
    xml +=              '<arg8>'+ read.processName +'</arg8>'; //process， 直接写流程名称
    xml +=              '<arg9>'+ read.processName +'</arg9>'; //fileType，文档类型，直接写流程名称
    xml +=              '<arg10>'+ read.activityName +'</arg10>'; //activity， 当前环节名称
    xml +=              '<arg11>'+ read.processName +'</arg11>'; //category，分类名称，直接写流程名称
    xml +=              '<arg12>'+ '' +'</arg12>'; //urgency，紧急程度：请默认“一般”
    xml +=              '<arg13>'+ '' +'</arg13>'; //secrecy，密级：请默认“普通文件”
    xml +=              '<arg14>'+ read.serial +'</arg14>'; //serial，文号：对公文必须，可以为空
    xml +=              '<arg15>'+ read.id +'</arg15>'; //feature， 流程实例（工单）唯一标识, 任务ID
    xml +=              '<arg16>'+ '' +'</arg16>';  //reserve， 决策名称：送办理|归档|送审批等等
    xml +=              '<arg17>'+ 'false' +'</arg17>'; //enableRemind|remindEnable，不要求，固定为false
    xml +=              '<arg18>'+ 'false' +'</arg18>'; //remindForceFlag|remindForce, 不要求，固定为false
    xml +=              '<arg19>'+ read.startTime +'</arg19>'; //composeTime， 数据生成时间：2011-08-30 12:00:00
    xml +=              '<arg20>'+ '' +'</arg20>'; //expirationTime，不要求，固定为""
    xml +=              '<arg21>'+ '' +'</arg21>'; //hastenTime  不要求，催办时间，可以为空
    xml +=              '<arg22>'+ concatReadCompletedUrl( read ) +'</arg22>'; //link, 访问链接
    xml +=              '<arg23>'+ '' +'</arg23>'; //linkView, 配合流程监控平台，江西移动环境接入为拟稿时间
    xml +=              '<arg24>'+ '' +'</arg24>'; //linkFlow, 办理链接, 可以为空
    xml +=              '<arg25>'+ '' +'</arg25>'; //linkRemove, 删除链接, 可以为空
    xml +=              '<arg26>'+ '' +'</arg26>'; //linkSupervise, 督办链接, 可以为空
    xml +=              '<arg27>'+ '' +'</arg27>'; //linkSuspend, 挂起链接, 可以为空
    xml +=              '<arg28>'+ '' +'</arg28>'; //linkReset, 重置链接, 可以为空
    xml +=              '<arg29>'+ '' +'</arg29>';  //linkReroute, 调度链接, 可以为空
    xml +=          '</proc:update>';
    xml +=      '</soapenv:Body>';
    xml += '</soapenv:Envelope>';
    return xml;
}


function send_readCompleted_create(id, read) {
    print("--------send_readCompleted_create start------------" + ", consume id : " + id);
    var xml = get_readCompleted_create_xml( read );
    if( sendRequest_read( xml ) ){
        updateConsume( id );
    }
    print("--------send_readCompleted_create end------------" + ", consume id  : " + id);
}

init();

print("----发送待办结束---------");