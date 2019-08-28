/*
 * resources.getEntityManagerContainer() // 实体管理容器.
 * resources.getContext() //上下文根.
 * resources.getOrganization() //组织访问接口.
 * requestText //请求内容.
 * request //请求对象.
 */


var _self = this;
var applications = resources.getContext().applications();
var departmentLevel_1 = 3; //2级部门所在层级

function typeOf( item ){
    if (item === null) return 'null';
    if( !item ){
        return typeof item;
    }
    if (item.$family != null) return item.$family();
    if (item.constructor == Array) return 'array';

    if (item.nodeName){
        if (item.nodeType == 1) return 'element';
        if (item.nodeType == 3) return (/\S/).test(item.nodeValue) ? 'textnode' : 'whitespace';
    } else if (typeof item.length == 'number'){
        if (item.callee) return 'arguments';
        //if ('item' in item) return 'collection';
    }

    return typeof item;
}

function objectClone(obj) {
    if (null == obj || "object" != typeof obj) return obj;

    if ( typeof obj.length==='number'){ //数组
        var copy = [];
        for (var i = 0, len = obj.length; i < len; ++i) {
            copy[i] = objectClone(obj[i]);
        }
        return copy;
    }else{
        var copy = {};
        for (var attr in obj) {
            copy[attr] = objectClone(obj[attr]);
        }
        return copy;
    }
}

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

function getPerson( employee ){
    var list = typeOf(employee) === "array" ? employee : [employee];
    var filter = {"personList":list}
    var resp = applications.postQuery("x_organization_assemble_express","person/list/object", JSON.stringify(filter));
    var json = parseResp( resp );
    if( json && json.type !== "error" && json.data && json.data.length ){
        return json.data[0]
    }else{
        return ""
    }
}

function getPhone( employee ){
    var list = typeOf(employee) === "array" ? employee : [employee];
    var filter = {"personList":list}
    var resp = applications.postQuery("x_organization_assemble_express","person/list/object", JSON.stringify(filter));
    var json = parseResp( resp );
    var mobile = [];
    if( json && json.type !== "error" && json.data && json.data.length ){
        for( var k=0; k<json.data.length; k++ ){
            var d = json.data[k];
            mobile.push( d.mobile );
        }
        return mobile
    }else{
        return ""
    }
}

function getPhoneByIdentityDn( identityDn ){
    var list = typeOf(identityDn) === "array" ? identityDn : [identityDn];
    var filter = {"identityList":list};
    var resp = applications.postQuery("x_organization_assemble_express","person/list/identity/object", JSON.stringify(filter));
    var json = parseResp( resp );
    var mobile = [];
    if( json && json.type !== "error" && json.data && json.data.length ){
        for( var k=0; k<json.data.length; k++ ){
            var d = json.data[k];
            mobile.push( d.mobile );
        }
        return mobile
    }else{
        return ""
    }
}

function getDepartmentLevel_1( employee ){
    var list = typeOf(employee) === "array" ? employee : [employee];
    var filter = {"personList":list}
    var resp = applications.postQuery("x_organization_assemble_express","unit/list/person/sup/nested/object", JSON.stringify(filter));
    var json = parseResp( resp );
    if( json && json.type !== "error" && json.data && json.data.length ){
        for( var i=0; i<json.data.length; i++ ){
            var d = json.data[i];
            if( d.level == 3 ){
                return d.distinguishedName;
            }
        }
    }else{
        return ""
    }
}

function getCompany( employee ){
    var list = typeOf(employee) === "array" ? employee : [employee];
    var filter = {"personList":list}
    var resp = applications.postQuery("x_organization_assemble_express","unit/list/person/sup/nested/object", JSON.stringify(filter));
    var json = parseResp( resp );
    if( json && json.type !== "error" && json.data && json.data.length ){
        for( var i=0; i<json.data.length; i++ ){
            var d = json.data[i];
            if( d.level == 2 ){
                return d.distinguishedName;
            }
        }
    }else{
        return ""
    }
}

//获取各流程人力资源部处理人
function getActivityOwner( company, processAlias, activityAlias ){
    var where = "o.companyDn='"+company+"' and " + "o.processAlias='"+processAlias+"' and o.activityAlias='"+activityAlias+"'";
    where = where.replace(/\s/g,"%20");
    var resp = applications.getQuery("x_query_assemble_surface","table/list/hrMarketSetting_ActivityOwner/row/select/where/"+where);
    var json = parseResp( resp );
    if( json && json.type !== "error" && json.data && json.data.length ){
        return json.data[0].identityDn
    }else{
        return null;
    }
}

//获取人才市场人力管理员
function getHrManagerIdentityList( company ){
    if( !company )return [];
    var resp = applications.postQuery("x_organization_assemble_express","unitduty/list/identity/unit/name",JSON.stringify({
        "name":"人才市场人力管理员","unit":company
    }));
    var json = parseResp( resp );
    if( json && json.type !== "error" && json.data ){
        return json.data.identityList
    }else{
        return [];
    }
}


//获取人才市场部门管理员
function getDepartmentManagerIdentityList( department ){
    if( !department )return [];
    var resp = applications.postQuery("x_organization_assemble_express","unitduty/list/identity/unit/name",JSON.stringify({
        "name":"人才市场部门管理员","unit":department
    }));
    var json = parseResp( resp );
    if( json && json.type !== "error" && json.data ){
        return json.data.identityList
    }else{
        return [];
    }
}

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
var CipherConnectionAction = Java.type('com.x.base.core.project.connection.CipherConnectionAction');
var Config = Java.type('com.x.base.core.project.config.Config');
//调动本系统的接口，发送短信
function sendSMS( json ){
    if( !json )return;
    if( !json.receiveMobile )return;
    if( !json.content )return;

    //var resp = CipherConnectionAction.post(false, Config.x_program_centerUrlRoot() + "invoke/sendSMS/execute", JSON.stringify(json));
    //print( "mobile=" + mobile + ", content=" + content );

    var mobileList = typeOf(json.receiveMobile) === "array" ? json.receiveMobile : [json.receiveMobile];
    var personList = typeOf(json.receivePerson) === "array" ? json.receivePerson : [json.receivePerson];
    for( var i=0; i<mobileList.length; i++ ){
        var d = objectClone( json );
        d.receiveMobile = mobileList[i];
        d.receivePerson = personList[i] ? personList[i] : "";
        var resp = CipherConnectionAction.post(false, Config.x_program_centerUrlRoot() + "invoke/sendSMS/execute", JSON.stringify(d));
        //print( "mobile=" + mobile + ", content=" + content );
    }
}


//离岗学习期限三个月，需提前一周给员工部门、人力资源部发送考核提醒
//提醒内容：员工张某的离岗学习期还有一周即将结束，请相关部门尽快完成考核。
function sendSMS_levelToLearn(){
    print( "发离岗学习到期短信" );
    var getTime = function(){
        var Calendar = Java.type("java.util.Calendar");
        var formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");//格式化为2017-10

        var calendar = Calendar.getInstance();//得到Calendar实例
        calendar.add(Calendar.MONTH, -3);//把月份减三个月
        calendar.add( Calendar.DAY_OF_YEAR, 7 ); //周加1
        var starDate = calendar.getTime();//得到时间赋给Data
        return formatter.format(starDate);//使用格式化Data
    }
    //今天往前三个月减一周的日期
    var timeLimit = getTime();

    var where = "(o.sendedSMSDate is null) and o.status='离岗学习员工' and o.createTime<'" + timeLimit +"'"; //Date.format('%Y-%m-%d %H:%M:%S');
    where = where.replace(/\s/g,"%20")
    print(where);
    var resp = applications.getQuery("x_query_assemble_surface","table/list/hrMarketResourcePool/row/select/where/"+where);
    var json = parseResp( resp );
    if( json && json.type !== "error" && json.data && json.data.length ){
        for( var k=0; k<json.data.length; k++ ){
            var d = json.data[k];
            var content = "员工"+( d.name || "" ).split("@")[0]+"的离岗学习期还有一周即将结束，请相关部门尽快完成考核。";
            var phone;
            //获取各流程人力资源部处理人设置中试用考核流程人力资源部起草节点的人员
            var identityDn = getActivityOwner( d.company, "probationCheck", "hrManager" );
            if( identityDn ){
                print( identityDn );
                phone = getPhoneByIdentityDn( identityDn );

                if( phone )sendSMS({
                    employeeCode : d.employeeCode,
                    person : d.name,
                    typeName : "离岗学习员工",
                    typeCode : "levelToLearn",
                    receiveMobile : phone,
                    receivePerson : identityDn,
                    content : content
                })


            }else{ //获取人力资源部管理员
                var list = getHrManagerIdentityList( d.company );
                if( list.length ){
                    phone = getPhoneByIdentityDn( list );
                    if( phone )sendSMS({
                        employeeCode : d.employeeCode,
                        person : d.name,
                        typeName : "离岗学习员工",
                        typeCode : "levelToLearn",
                        receiveMobile : phone,
                        receivePerson : list,
                        content : content
                    })
                }
            }


            //获取离岗学习流程的转岗部门，给部门人力管理员发短信
            var transferDepartment = d.transferDepartment;
            if( transferDepartment ){
                var list = getDepartmentManagerIdentityList( transferDepartment );
                if( list.length ){
                    print( list );
                    phone = getPhoneByIdentityDn( list );
                    if( phone )sendSMS( {
                        employeeCode : d.employeeCode,
                        person : d.name,
                        typeName : "离岗学习员工",
                        typeCode : "levelToLearn",
                        receiveMobile : phone,
                        receivePerson : list,
                        content : content
                    });
                }
            }

            formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化为2017-10
            d.sendedSMSDate = formatter.format(new java.util.Date())
            //保存会资源池
            applications.putQuery("x_query_assemble_surface","table/hrMarketResourcePool/row/"+d.id, JSON.stringify(d));
        }
    }
}

//sendSMS_levelToLearn();


//内部转岗期不得超过1个月
//内部转岗到期限前，提前一周给人力资源部发送到期提醒。
//提醒内容：员工张某的内部转岗期还有一周即将到期。
function sendSMS_internalChange(){
    print( "发送内部转岗短信" );
    var getTime = function(){
        var Calendar = Java.type("java.util.Calendar");
        var formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");//格式化为2017-10

        var calendar = Calendar.getInstance();//得到Calendar实例
        calendar.add(Calendar.MONTH, -1);//把月份减一个月
        calendar.add( Calendar.DAY_OF_YEAR, 7 ); //周加1
        var starDate = calendar.getTime();//得到时间赋给Data
        return formatter.format(starDate);//使用格式化Data
    }
    //今天往前三个月减一周的日期
    var timeLimit = getTime();

    var where = "(o.sendedSMSDate is null) and o.status='内部转岗员工' and o.createTime<'" + timeLimit +"'"; //Date.format('%Y-%m-%d %H:%M:%S');
    where = where.replace(/\s/g,"%20")
    print(where);
    var resp = applications.getQuery("x_query_assemble_surface","table/list/hrMarketResourcePool/row/select/where/"+where);
    var json = parseResp( resp );
    if( json && json.type !== "error" && json.data && json.data.length ){
        for( var k=0; k<json.data.length; k++ ){
            var d = json.data[k];
            var content = "员工"+( d.name || "" ).split("@")[0]+"的内部转岗期还有一周即将到期。";
            var phone;
            //获取各流程人力资源部处理人设置中内部转岗人力资源部起草节点的人员
            var identityDn = getActivityOwner( d.company, "internalWaiting", "hrManager2" );
            if( identityDn ){
                print( identityDn );
                phone = getPhoneByIdentityDn( identityDn );
                if( phone )sendSMS( {
                    employeeCode : d.employeeCode,
                    person : d.name,
                    typeName : "内部转岗员工",
                    typeCode : "internalChange",
                    receiveMobile : phone,
                    receivePerson : identityDn,
                    content : content
                })
            }else{ //获取人力资源部管理员
                var list = getHrManagerIdentityList( d.company );
                if( list.length ){
                    print( list )
                    phone = getPhoneByIdentityDn( list );
                    if( phone )sendSMS({
                        employeeCode : d.employeeCode,
                        person : d.name,
                        typeName : "内部转岗员工",
                        typeCode : "internalChange",
                        receiveMobile : phone,
                        receivePerson : list,
                        content : content
                    })
                }
            }

            formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化为2017-10
            d.sendedSMSDate = formatter.format(new java.util.Date())
            //保存会资源池
            applications.putQuery("x_query_assemble_surface","table/hrMarketResourcePool/row/"+d.id, JSON.stringify(d));
        }
    }
}


//合同到期检查是否已经发送过
function checkSended_contractNotice( EMPLOYEE_NUMBER ){
    var where = "o.employeeCode='" + EMPLOYEE_NUMBER + "' and o.typeCode='contractNotice'";
    where = where.replace(/\s/g,"%20")
    print(where);
    var resp = applications.getQuery("x_query_assemble_surface","table/list/hrMarketSMSLog/row/select/where/"+where);
    var json = parseResp( resp );
    if( json && json.type !== "error" && json.data && json.data.length ){
        return true;
    }else{
        return false;
    }
}



//合同到期前两个月要给人力资源部、员工、员工所在部门发送合同到期预警。
function sendSMS_contractNotice(){
    print( "发送合同续签短信" );
    var getTime = function(){
        var Calendar = Java.type("java.util.Calendar");
        var formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");//格式化为2017-10

        var calendar = Calendar.getInstance();//得到Calendar实例
        calendar.add(Calendar.MONTH, -2);//把月份减2个月
        var starDate = calendar.getTime();//得到时间赋给Data
        return formatter.format(starDate);//使用格式化Data
    }
    //今天往前三个月减一周的日期
    var timeLimit = getTime();

    var where = "o.CONTRACT_END='" + timeLimit + "'"; //Date.format('%Y-%m-%d %H:%M:%S');
    where = where.replace(/\s/g,"%20")
    print(where);
    var resp = applications.getQuery("x_query_assemble_surface","table/list/hrSyncPersonalContract/row/select/where/"+where);
    var json = parseResp( resp );
    if( json && json.type !== "error" && json.data && json.data.length ){
        for( var k=0; k<json.data.length; k++ ){
            var d = json.data[k];


            if(  checkSended_contractNotice(d.EMPLOYEE_NUMBER) ){
                continue;
            }


            var person = getPerson(d.EMPLOYEE_NUMBER);
            if( !person )return;
            //给员工本人发送短信
            if( person.mobile ){
                print( person.mobile );
                sendSMS({
                    employeeCode : d.EMPLOYEE_NUMBER,
                    person : person.distinguishedName,
                    typeName : "合同到期员工",
                    typeCode : "contractNotice",
                    receiveMobile : person.mobile,
                    receivePerson : person.distinguishedName,
                    content : ( person.name || "" )+"您好，您的合同还有一周即将到期。"
                })
            }

            var phone;
            var content = "员工"+( person.name || "" ).split("@")[0]+"的合同还有一周即将到期。";
            //给部门人力管理员发短信
            var department = getDepartmentLevel_1(d.EMPLOYEE_NUMBER);
            if( department ){
                var list = getDepartmentManagerIdentityList( department );
                if( list && list.length ){
                    print( list );
                    phone = getPhoneByIdentityDn( list );
                    //if( phone )sendSMS( phone,  content );
                    if( phone ){
                        sendSMS({
                            employeeCode : d.EMPLOYEE_NUMBER,
                            person : person.distinguishedName,
                            typeName : "合同到期员工",
                            typeCode : "contractNotice",
                            receiveMobile : phone,
                            receivePerson : list,
                            content : content
                        })
                    }
                }
            }

            //人力资源部
            var company = getCompany( d.EMPLOYEE_NUMBER )
            if(company){
                //获取各流程人力资源部处理人设置中合同续签流程人力资源部起草节点的人员
                var identityDn = getActivityOwner( company, "contractRenewal", "hrManager" );
                if( identityDn ){
                    print( identityDn )
                    phone = getPhoneByIdentityDn( identityDn );
                    if( phone )sendSMS( {
                        employeeCode : d.EMPLOYEE_NUMBER,
                        person : person.distinguishedName,
                        typeName : "合同到期员工",
                        typeCode : "contractNotice",
                        receiveMobile : phone,
                        receivePerson : identityDn,
                        content : content
                    });
                }else{ //获取人力资源部管理员
                    var list = getHrManagerIdentityList( company );
                    if( list.length ){
                        print( list )
                        phone = getPhoneByIdentityDn( list );
                        if( phone )sendSMS({
                            employeeCode : d.EMPLOYEE_NUMBER,
                            person : person.distinguishedName,
                            typeName : "合同到期员工",
                            typeCode : "contractNotice",
                            receiveMobile : phone,
                            receivePerson : list,
                            content : content
                        })
                    }
                }
            }
        }
    }
}

sendSMS_levelToLearn();
sendSMS_internalChange();
sendSMS_contractNotice();