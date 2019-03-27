/*
 * resources.getEntityManagerContainer() // 实体管理容器.
 * resources.getContext() //上下文根.
 * resources.getOrganization() //组织访问接口.
 * requestText //请求内容.
 * request //请求对象.
 */




/*
 {
 "action": "add",
 "forceFlag" : "yes", //如果add的时候，人员已存在，是否强制更新
 "genderType": "m",
 "signature": "",
 "description": "",
 "name": "",
 "employee": "",
 "unique": "",
 "distinguishedName": "",
 "orderNumber": "",
 "controllerList": "",
 "superior": "",
 "mail": "",
 "weixin": "",
 "qq": "",
 "mobile": "",
 "officePhone": "",
 "boardDate": "",
 "birthday": "",
 "age": "",
 "dingdingId": "",
 "dingdingHash": "",
 "attributeList": [
 {
 "name": "",
 "value": "",
 "description": "",
 "orderNumber": ""
 }
 ],
 "unitList": [
 {
 "flag": "",
 "orderNumber": "",
 "description": "",
 "duty": "",
 "position": ""
 }
 ]
 }
 */

print("运行组织同步接口");
var File = Java.type('java.io.File');

var Config = {
    localPath : File.separator  + "data" + File.separator + "OrganizationSyncRequest" + File.separator + "unit" + File.separator
};

var applications = resources.getContext().applications();
//var archiveFlag = false; //如果系统内相关的人（上级组织，职务人员等）不在，保存在本地

var Utils = {
    getUnitFlag : function( json ){
        return json.flag || json.unique || json.distinguishedName  || json.id || json.name;
    },
    getKeyEqualObjFromArray : function( sourceArray, sourceKey, value ){
        for( var i=0; i<sourceArray.length; i++ ){
            if( sourceArray[i][sourceKey] === value ){
                return sourceArray[i];
            }
        }
        return null;
    },
    parseResp : function( resp ){
        if( !resp || resp === null ){
            return {
                "type": "error",
                message : "服务响应是null，需要管理员查看后台日志"
            }
        }else{
            var json = JSON.parse( resp.toString() );
            return json;
        }
    },
    getFailText : function( json ){
        //{
        //    "type": "error",
        //    "message": "手机号错误:15268803358, 手机号已有值重复.",
        //    "date": "2018-08-05 02:51:35",
        //    "spent": 5,
        //    "size": -1,
        //    "count": 0,
        //    "position": 0,
        //    "prompt": "com.x.organization.assemble.control.jaxrs.person.ExceptionMobileDuplicate"
        //}
        var text;
        if( json.message ){
            text = json.message + ( json.prompt ? "("+json.prompt + ")" : "" );
        }else if( json.prompt ){
            text = json.prompt;
        }else{
            text = "未知异常";
        }
        print(text);
        return text;
    },
    processError : function( e, text ){
        e.printStackTrace();
        var errorText = text + " " + e.name + ": " + e.message;
        print(errorText);
        return errorText;
    },
    arrayIndexOf : function( array, target ){
        for( var i=0; i<array.length; i++ ){
            if( array[i] == target )return i;
        }
        return -1;
    },
    objectClone : function (obj) {
        if (null == obj || "object" != typeof obj) return obj;

        if ( typeof obj.length==='number'){ //数组
            var copy = [];
            for (var i = 0, len = obj.length; i < len; ++i) {
                copy[i] = Utils.objectClone(obj[i]);
            }
            return copy;
        }else{
            var copy = {};
            for (var attr in obj) {
                copy[attr] = Utils.objectClone(obj[attr]);
            }
            return copy;
        }
    },
    saveToLocal : function( json ){
        if( json.saveFlag && json.saveFlag == "no" ){
            print( "不保存文件" );
            return;
        }
        print( "保存文件开始" );
        if( File == null )File = Java.type('java.io.File');
        var dir = new File( Config.localPath );
        if (!dir.exists()) {
            if(!dir.mkdirs()){
                print( "创建文件夹失败："+ recordPath );
                return null;
            }
        }

        //var Date = Java.type( "java.util.Date" );
        //var now = new Date();
        //var nowStr = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(now);
        var name = json.name ? json.name : Utils.getUnitFlag(json);
        var unique = json.unique ? json.unique : "";
        var path = Config.localPath + name + "_" + unique + '.json';
        print( "path="+path );

        var file = new File(path);
        if (file.exists()) { // 如果已存在,删除旧文件
            file.delete();
        }
        if(!file.createNewFile()){
            print("不能创建文件:"+path);
            return null;
        }

        var pw = new java.io.PrintWriter(file, "GBK");
        pw.write( JSON.stringify(json) );
        pw.close();
        print( "保存文件结束 path="+path );
    }
};

var AttributeAction = {
    add :function( flag, data ){
        //"description": "组织属性描述",
        //    "name": "组织属性",
        //    "unique": "",
        //    "distinguishedName": "",
        //    "orderNumber": "12345",
        //    "value": "值1"
        data.unit = flag;
        data.attributeList = typeof( data.value ) == "string" ? [data.value] : data.value;
        try {
            var resp = applications.postQuery('x_organization_assemble_control', 'unitattribute', JSON.stringify( data ));
            var json = Utils.parseResp( resp );
            if( json.type && json.type == "success" ){
                return "";
            }else{
                return Utils.getFailText( json );
            }
        }catch(e){
            return Utils.processError( e, "新增组织属性AttributeAction.add() 出错: " );
        }
    },
    remove : function( data ){
        try {
            var resp = applications.deleteQuery('x_organization_assemble_control', 'unitattribute/'+data.id );
            var json = Utils.parseResp( resp );
            if( json.type && json.type == "success" ){
                return "";
            }else{
                return Utils.getFailText( json );
            }
        }catch(e){
            return Utils.processError( e, "删除组织属性AttributeAction.remove() 出错: " );
        }
    },
    update : function( flag, data_new, data_old ){
        for( var key in data_new ){
            if( key != "distinguishedName" ){
                data_old[key] = data_new[key];
            }
        }
        data_old.unit = flag;
        data_old.attributeList = typeof( data_new.value  ) == "string" ? [data_new.value] : data_new.value;
        try {
            var resp = applications.putQuery('x_organization_assemble_control', 'unitattribute/'+data_old.id, JSON.stringify(data_old) );
            var json = Utils.parseResp( resp );
            if( json.type && json.type == "success" ){
                return "";
            }else{
                return Utils.getFailText( json );
            }
        }catch(e){
            return Utils.processError( e, "修改组织属性AttributeAction.update() 出错: " );
        }
    },
    compare : function( json ){
        var attribute_new = json.attributeList; //传入的组织属性
        var errorText = "";
        var flag = Utils.getUnitFlag( json );
        if( flag ){
            try{
                var resp = applications.getQuery('x_organization_assemble_control', 'unitattribute/list/unit/'+flag );
                var json_attribute_old = Utils.parseResp( resp );
                var attribute_old;
                if( json_attribute_old.type && json_attribute_old.type == "success" ){
                    attribute_old = json_attribute_old.data;
                }else{
                    attribute_old = [];
                    //return Utils.getFailText(json_attribute_old);
                }
                for( var i=0; i<attribute_old.length; i++ ){
                    var obj_new = Utils.getKeyEqualObjFromArray( attribute_new, "name", attribute_old[i].name );
                    if( obj_new == null ){ //老的不在了，要删除
                        errorText = AttributeAction.remove( attribute_old[i] )
                    }else{ //已经存在，要修改
                        errorText = AttributeAction.update( flag, obj_new, attribute_old[i] );
                    }
                }
                for( var i=0; i<attribute_new.length; i++ ){
                    var obj_old = Utils.getKeyEqualObjFromArray( attribute_old, "name", attribute_new[i].name );
                    if( obj_old == null ){ //需要新增
                        errorText = AttributeAction.add( flag, attribute_new[i] );
                    }
                }
                return errorText;
            }catch(e){
                return Utils.processError( e, "修改组织属性AttributeAction.compare() 出错: " );
            }
        }
    }
};


//var DutyAction = {
//    getIndntityByPerson: function( person, unit ){
//        try{
//            var resp = applications.getQuery('x_organization_assemble_control', 'identity/list/person/'+person);
//            var json = Utils.parseResp( resp );
//            var identity;
//            if( json.type && json.type == "success" ){
//                if(  json.data && json.data.length > 0 ){
//                    var identityList = json.data;
//                    for( var i=0; i<identityList.length; i++ ){
//
//                    }
//                }
//                if( !identity ){
//                    return "根据人员"+person+"不能获取身份DutyAction.getIndntityByPerson()"
//                }
//            }else{
//                return Utils.getFailText( json );
//            }
//        }catch(e){
//            return Utils.processError( e, "根据人员获取身份DutyAction.getIndntityByPerson() 出错: " );
//        }
//    },
//    add :function( flag, data ){
//        //description:"", //描述.
//        //    name:"", //名称,同一组织下不可重名.
//        //    unique:"", //唯一标识,不可重复,为空则使用自动填充值
//        //    distinguishedName:"", //识别名,自动填充,@UD结尾.
//        //    orderNumber:"", //排序号,升序排列,为空在最后
//        //    value:"", //组织职务身份成员,多值.
//        data.unit = flag;
//        data.identityList = typeof( data.value ) == "string" ? [data.value] : data.value;
//        try {
//            var resp = applications.postQuery('x_organization_assemble_control', 'unitduty', JSON.stringify( data ));
//            var json = Utils.parseResp( resp );
//            if( json.type && json.type == "success" ){
//                return "";
//            }else{
//                return Utils.getFailText( json );
//            }
//        }catch(e){
//            return Utils.processError( e, "新增组织职务Duty.add() 出错: " );
//        }
//    },
//    remove : function( data ){
//        try {
//            var resp = applications.deleteQuery('x_organization_assemble_control', 'unitduty/'+data.id );
//            var json = Utils.parseResp( resp );
//            if( json.type && json.type == "success" ){
//                return "";
//            }else{
//                return Utils.getFailText( json );
//            }
//        }catch(e){
//            return Utils.processError( e, "删除组织职务Duty.remove() 出错: " );
//        }
//    },
//    update : function( flag, data_new, data_old ){
//        for( var key in data_new ){
//            if( key != "distinguishedName" ){
//                data_old[key] = data_new[key];
//            }
//        }
//        data_old.unit = flag;
//        data_old.attributeList = typeof( data_new.value  ) == "string" ? [data_new.value] : data_new.value;
//        try {
//            var resp = applications.putQuery('x_organization_assemble_control', 'unitduty/'+data_old.id, JSON.stringify(data_old) );
//            var json = Utils.parseResp( resp );
//            if( json.type && json.type == "success" ){
//                return "";
//            }else{
//                return Utils.getFailText( json );
//            }
//        }catch(e){
//            return Utils.processError( e, "修改组织职务Duty.update() 出错: " );
//        }
//    },
//    compare : function( json ){
//        var attribute_new = json.attributeList; //传入的组织属性
//        var errorText = "";
//        var flag = Utils.getUnitFlag( json );
//        if( flag ){
//            try{
//                var resp = applications.getQuery('x_organization_assemble_control', 'unitduty/list/unit/'+flag );
//                var json_attribute_old = Utils.parseResp( resp );
//                var attribute_old;
//                if( json_attribute_old.type && json_attribute_old.type == "success" ){
//                    attribute_old = json_attribute_old.data;
//                }else{
//                    attribute_old = [];
//                    //return Utils.getFailText(json_attribute_old);
//                }
//                for( var i=0; i<attribute_old.length; i++ ){
//                    var obj_new = Utils.getKeyEqualObjFromArray( attribute_new, "name", attribute_old[i].name );
//                    if( obj_new == null ){ //老的不在了，要删除
//                        errorText = AttributeAction.remove( attribute_old[i] )
//                    }else{ //已经存在，要修改
//                        errorText = AttributeAction.update( flag, obj_new, attribute_old[i] );
//                    }
//                }
//                for( var i=0; i<attribute_new.length; i++ ){
//                    var obj_old = Utils.getKeyEqualObjFromArray( attribute_old, "name", attribute_new[i].name );
//                    if( obj_old == null ){ //需要新增
//                        errorText = AttributeAction.add( flag, attribute_new[i] );
//                    }
//                }
//                return errorText;
//            }catch(e){
//                return Utils.processError( e, "修改组织职务Duty.compare() 出错: " );
//            }
//        }
//    }
//};


function get( json ){
    var errorText = "";
    var flag = Utils.getUnitFlag(json);
    var resp;
    var unit_old;
    if( flag ){
        try{
            resp = applications.getQuery('x_organization_assemble_control', "unit/"+flag ); //先获取人员信息
            var json_old = Utils.parseResp( resp );
            if( json_old.type && json_old.type == "success" ){
                unit_old = json_old.data;
            }else{
                return Utils.getFailText( json );
            }
        }catch(e){
            return Utils.processError( e, "get() 出错: " );
        }

        delete unit_old.woSupDirectUnit;
        delete unit_old.woSubDirectIdentityList;
        delete unit_old.woUnitAttributeList;
        delete unit_old.woUnitDutyList;
        //delete person_old.control;
        //delete person_old.controllerList;

        return unit_old;
        //applications.putQuery('x_organization_assemble_control', "person/"+json.unique, json );
    }else{
        errorText = "参数中没有组织标志：distinguishedName , unique, name 或 id, 不能获取用户";
        print(errorText);
        return errorText;
    }
}

function add( requestJson ){
    print("添加组织");
    var json = Utils.objectClone(requestJson);
    var errorText;
    var response;
    var resp;
    try{
        if( json.superior && json.superior != "" ){ //判断上层组织在不在
            var superiorData = get({ flag : json.superior });
            if( typeof( superiorData ) != "object" ){ //不存在，需要新建
                //archiveFlag = true;
                //delete json.superior;
                var superiorUnitData = { name : json.superior, unique : json.superior };
                resp = applications.postQuery( "x_organization_assemble_control", 'unit', JSON.stringify(superiorUnitData));
            }
        }

        var unitData = get(json);
        if( typeof(unitData) == "object" ){
            // if( json.forceFlag && json.forceFlag == "yes" ){
            print( "组织已存在，强制保存" );
            for( var key in json){
                if( key !== "action" && key !== "attributeList" && key !== "dutyList" ){
                    if( key == "orderNumber" && (!json[key] || json[key]=="") ) {
                    }else if( key == "typeList" && (!json[key] || json[key]==""  || json[key].length==0) ){
                    }else if( key == "id" || key == "unique" || key == "distinguishedName" ){
                    }else{
                        unitData[key] = json[key];
                    }
                }
            }
            if( !unitData.controllerList || unitData.controllerList == null )unitData.controllerList = [];
            if( !unitData.typeList ){
                unitData.typeList = [];
            }else{
                if( typeof( unitData.typeList ) == "string" )unitData.typeList = [ unitData.typeList ];
            }
            var id = unitData.id;
            //delete unitData.id;
            print("unitData="+JSON.stringify(unitData));
            resp = applications.putQuery('x_organization_assemble_control', "unit/"+id, JSON.stringify(unitData) );
            // }else{
            //     errorText = "组织“"+ Utils.getUnitFlag(json) +"”已经在系统内存在";
            // }
        }else{
            if( !json.controllerList ){
                json.controllerList = [];
            }else{
                if( typeof( json.controllerList ) == "string" )json.controllerList = [ json.controllerList ];
            }
            if( !json.typeList ){
                json.typeList = [];
            }else{
                if( typeof( json.typeList ) == "string" )json.typeList = [ json.typeList ];
            }

            var data = Utils.objectClone(json);
            if(data.attributeList)delete data.attributeList;
            if(data.dutyList)delete data.dutyList;
            resp = applications.postQuery( "x_organization_assemble_control", 'unit', JSON.stringify(data));
        }

        if( resp ){
            var response = Utils.parseResp( resp );
            if( response.type && response.type == "success" ){
                if( json.attributeList  ){
                    errorText = AttributeAction.compare( json );
                }
                //if( json.dutyList ){
                //    errorText = DutyAction.compare( json );
                //}
            }else{
                errorText = Utils.getFailText( response );
            }
        }
        // if( archiveFlag ){
        //     Utils.saveToLocal( requestJson ); //保存到本地
        // }
    }catch(e){
        errorText = Utils.processError( e, "添加组织 add() 出错：" );
    }finally{
        var result = {
            "result" : errorText ? "error" : "success",
            "description" : errorText || ""
        };
        if( response && response.data ){
            result.id = response.data.id;
        }
        return result;
    }
}

function update(requestJson){
    print("修改组织");
    var json = Utils.objectClone( requestJson );
    var errorText;
    var response;
    try{
        if( json.superior && json.superior != ""  ){ //判断上层组织在不在
            var superiorData = get({ flag : json.superior });
            if( typeof( superiorData ) != "object" ){ //不存在则新建
                //archiveFlag = true;
                //delete json.superior;
                var superiorUnitData = { name : json.superior, unique : json.superior };
                resp = applications.postQuery( "x_organization_assemble_control", 'unit', JSON.stringify(superiorUnitData));
            }
        }

        var unitData = get(json);
        if( typeof unitData == "object" ){
            for( var key in json){
                if( key !== "action" && key !== "attributeList" && key !== "dutyList" ){
                    if( key == "orderNumber" && (!json[key] || json[key]=="") ) {
                    }else if( key == "typeList" && (!json[key] || json[key]==""  || json[key].length==0) ){
                    }else if( key == "id" || key == "unique" || key == "distinguishedName" ){
                    }else{
                        unitData[key] = json[key];
                    }
                }
            }
            if( !unitData.controllerList || unitData.controllerList == null )unitData.controllerList = [];
            if( !unitData.typeList ){
                unitData.typeList = [];
            }else{
                if( typeof( unitData.typeList ) == "string" )unitData.typeList = [ unitData.typeList ];
            }
            var resp = applications.putQuery('x_organization_assemble_control', "unit/"+unitData.id, JSON.stringify(unitData) );
            var response = Utils.parseResp( resp );
            if( response.type && response.type == "success" ){
                if( !json.attributeList  )json.attributeList = [];
                errorText = AttributeAction.compare( json );

                //if( !json.dutyList )json.dutyList = [];
                //errorText = DutyAction.compare( json );
            }else{
                errorText = Utils.getFailText( response );
            }
        }else{
            errorText = unitData;
        }
    }catch(e){
        errorText = Utils.processError( e, "修改组织 update() 出错：" );
    }finally{
        // if( archiveFlag ){
        //     Utils.saveToLocal( requestJson ); //保存到本地
        // }
        var result = {
            "result" : errorText ? "error" : "success",
            "description" : errorText || ""
        };
        if( response && response.data ){
            result.id = response.data.id;
        }
        return result;
    }
}


function remove(json){
    print("删除组织");
    var errorText;
    var response;
    try{
        var unit = get(json);
        if( typeof(unit) == "object" ){
            var resp = applications.deleteQuery('x_organization_assemble_control', "unit/"+unit.id ); //s人员信息
            response = Utils.parseResp( resp );
            if( response.type && response.type == "success" ){
            }else{
                errorText = Utils.getFailText( response );
            }
        }else{
            errorText = unit;
        }
    }catch(e){
        errorText = Utils.processError( e, "删除组织 remove() 出错：" );
    }finally{
        var result = {
            "result" : errorText ? "error" : "success",
            "description" : errorText || ""
        };
        if( response && response.data ){
            result.id = response.data.id;
        }
        return result;
    }
}


function init(){
    var result ="";
    var responseText = "";
    try{
        print( "requestText="+requestText );

        var requestJson = JSON.parse(requestText);

        print( "type of requestJson = " + typeof( requestJson ));

        if( typeof(requestJson) === "string" ){
            requestJson = JSON.parse(requestJson);
        }

        var action = requestJson.action;
        print("action="+action);
        switch( action ){
            case "add":
                result = add( requestJson );
                break;
            case "update":
                result = update( requestJson );
                break;
            case "delete" :
                result = remove( requestJson );
                break;
            default :
                result = {
                    "result" : "error",
                    "description" : "requestText未设置action，不执行操作"
                };
                break;
        }


    }catch(e){
        e.printStackTrace(); 
        result = {
            "result" : "error",
            "description" : e.name + ": " + e.message
        };
    }finally{
        print("responseText="+JSON.stringify(result));
        return result;
    }
}

init();
