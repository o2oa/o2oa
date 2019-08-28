/*
 * resources.getEntityManagerContainer() // 实体管理容器.
 * resources.getContext() //上下文根.
 * resources.getOrganization() //组织访问接口.
 * requestText //请求内容.
 * request //请求对象.
 */




//////////////////////////////////// config ////////////////////////////////////////////////
var Config = {
    //soap请求地址
    "soapUrl": "http://10.134.73.35:8080/haSms/SendSms", //接口地址
    "userName": "snrlmh", //用户名
    "password" : "snrlmh1904" //密码
};

var applications = resources.getContext().applications();

//返回字符串转json对象
function parseResp(resp) {
    if (!resp || resp === null) {
        return {
            "type": "error",
            message: "服务响应是null"
        }
    } else {
        var json = JSON.parse(resp.toString());
        return json;
    }
}


//webserivce请求对象组装 
function getSoapReqData( mobile, smsBody, appointTime) {
    var str = '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sms="http://sms.ha.mocha.com/">'+
        '   <soapenv:Header/>'+
        '   <soapenv:Body>'+
        '      <sms:sendSms>'+
        '         <arg0>'+ Config.userName +'</arg0>'+ //登陆平台账号
        '         <arg1>'+ Config.password +'</arg1>'+ //登录密码
        '         <arg2>'+ mobile +'</arg2>'+ //要发送的目的手机号
        '         <arg3>'+ smsBody +'</arg3>'+ //短信内容
        '         <arg4>'+ (appointTime || null) +'</arg4>'+ //定时发送时间,如不需要可以传null
        '      </sms:sendSms>'+
        '   </soapenv:Body>'+
        '</soapenv:Envelope>'
    return str;
}

//解析xml
// function loadXML(xmlString) {
//   var DocumentBuilderFactory = Java.type('javax.xml.parsers.DocumentBuilderFactory');
//   var StringReader = Java.type('java.io.StringReader');
//   var InputSource = Java.type('org.xml.sax.InputSource');
//   var dbf = DocumentBuilderFactory.newInstance();
//   var db = dbf.newDocumentBuilder();
//   var sr = new StringReader(xmlString);
//   var is = new InputSource(sr);
//   var doc = db.parse(is);
//   var root = doc.getDocumentElement();
//   return root;
// }

function getXmlText(root, tag) {
    var list = root.getElementsByTagName(tag);
    if (list == null) return null;
    var node = list.item(0);
    if (node == null) return null;
    return node.getTextContent();
}

function parseResult( xmlStr, requestJson ){
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

        // var faultcode = getXmlText(root, "faultcode");
        // var faultstring = getXmlText(root, "faultstring");

        //返回标识代表了平台是否成功接收到了信息。如返回标识为0，后面则跟着“R”和20位的唯一ID(sid)，
        //如果返回标识为1，后面则跟着“R”和异常编码。
        //例如成功为“0R201101220945324SI000”，失败则为“1R001”，按照异常表查询为身份认证失败。
        var result = getXmlText(root, "soap:Body");
        var errorText = '';
        print( "sms返回结果:"+result )
        if( result.substr(0,1) === "0" ){ //成功
            requestJson.resultCode = "success";
            requestJson.result = "成功:" + result;
            return {
                "result" : "success",
                "description" : "返回结果：" + result
            };
        }else{ //失败
            var errorCode = result.substr(2,3);
            switch (errorCode) {
                case '100':
                    errorText = '平台服务异常';
                    break;
                case '001':
                    errorText = '身份认证失败';
                    break;
                case '002':
                    errorText = '手机号不合法';
                    break;
                case '003':
                    errorText = '信息内容大小超过允许范围';
                    break;
                case '009':
                    errorText = '定时时间早于当前时间';
                    break;
                case '011':
                    errorText = '密码为空或者账号为空或手机号为空';
                    break;

                default:
                // code
            }
            requestJson.resultCode = "error";
            requestJson.result = errorText + ":" + result;
            print( "错误信息：" + errorText );
            return {
                "result" : "error",
                "description" : "返回结果：" + result + " ,错误信息" + errorCode
            };
        }
    }catch(e){
        print("解析返回结果报错：");
        requestJson.resultCode = "run time error";
        requestJson.result = "解析返回结果报错：" + e.name + ": " + e.message;
        print(  e.printStackTrace() );
        return {
            "result" : "error",
            "description" : "解析返回结果报错：" + e.name + ": " + e.message
        }
    }
}

// <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
//   <soap:Body>
//       <ns1:sendSmsResponse xmlns:ns1="http://sms.ha.mocha.com/">
//          <return>0R190521174853520SI000</return>
//       </ns1:sendSmsResponse>
//   </soap:Body>
// </soap:Envelope>
function sendRequest( xml, requestJson ){
    try{
        print("发起请求:"+xml);
        var ArrayList = Java.type('java.util.ArrayList');
        var heads = new ArrayList();
        var NameValuePair = Java.type('com.x.base.core.project.bean.NameValuePair');
        var p1 = new NameValuePair('Content-Type', 'text/xml; charset=utf-8');
        heads.add(p1);
        var HttpConnectionClass = Java.type('com.x.base.core.project.connection.HttpConnection');
        var resp = HttpConnectionClass.postAsString(Config.soapUrl, heads, xml);
        // resp = '<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">'+
        //     '<soap:Body>'+
        //         '<ns1:sendSmsResponse xmlns:ns1="http://sms.ha.mocha.com/">'+
        //             '<return>0R190521174853520SI000</return>'+
        //         '</ns1:sendSmsResponse>'+
        //     '</soap:Body>'+
        // '</soap:Envelope>'
        print( "SMS 返回:"+resp );
        return parseResult( resp, requestJson );
    }catch(e){
        print("发送请求出错：");
        requestJson.resultCode = "run time error";
        requestJson.result = "发送请求出错：" + e.name + ": " + e.message;
        print(  e.printStackTrace() );
        return {
            "result" : "error",
            "description" : "发送请求出错：" + e.name + ": " + e.message
        }
    }
}

function saveLog( json ){
    var resp = applications.postQuery("x_query_assemble_surface","table/hrMarketSMSLog/row", JSON.stringify(json));
    var d = parseResp( resp );
    if( d && d.type == "error" ){
        print( "save sms log error :" + d.message );
    }
}

//接口调用参数
// {
//     employeeCode : "处理的人员编码，比如合同到期人员、离岗学习到期人员、转岗到期人员", 
//     person : "处理的人员名称，比如合同到期人员、离岗学习到期人员、转岗到期人员",
//     typeName : "类型，合同到期人员、离岗学习到期人员、转岗到期人员",
//     typeCode : "类型，contractNotice、levelToLearn、internalChange",
//     receiveMobile : "接收人员手机号",
//     receivePerson : "接收人员",
//     content : "短信内容",
//     resultCode : "短信发送结果编码",
//     result : "短信发送结果",
//     appointTime : "定时发送时间"
// }
function init(){
    var result ="";
    var responseText = "";
    var requestJson;
    try{
        print( "requestText="+requestText );

        requestJson = JSON.parse(requestText);

        print( "type of requestJson = " + typeof( requestJson ));

        if( typeof(requestJson) === "string" ){
            requestJson = JSON.parse(requestJson);
        }

        var mobile = requestJson.receiveMobile;
        var content = requestJson.content;

        var xml = getSoapReqData( mobile, content );
        result = sendRequest( xml, requestJson );
    }catch(e){
        e.printStackTrace();
        if( requestJson ){
            requestJson.resultCode = "run time error";
            requestJson.result = e.name + ": " + e.message;
        }
        result = {
            "result" : "error",
            "description" : e.name + ": " + e.message
        };
    }finally{
        if( requestJson ){
            saveLog( requestJson );
        }
        print("responseText="+JSON.stringify(result));
        return result;
    }
}

init();
