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

print("运行人员同步接口");

var File = Java.type('java.io.File');
var Config = {
    localPath : File.separator  + "data" + File.separator + "OrganizationSyncRequest" + File.separator + "person" + File.separator
};

var applications = resources.getContext().applications();
var archiveFlag = false;

var Utils = {
    getUserFlag : function( json ){
        return json.flag || json.distinguishedName || json.unique || json.employee || json.mobile || json.id;
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
        var name = json.name ? json.name : Utils.getUserFlag(json);
        var unique = json.unique ? json.unique : "";
        var path = Config.localPath + name + '_' + unique + '.json';

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
        //name  	string	single	属性名称	级别
        //description  	string	single	属性描述	级别描述
        //value  	string/array	multi	属性值	1 / [ "1" ]
        //orderNumber 	string	single	排序号,升序排列,为空在最后	18315158
        data.person = flag;
        var valueList = typeof( data.value ) == "string" ? [data.value] : data.value;
        for( var i=0; i<valueList.length; i++ ){
            valueList[i] = valueList[i].replace("@","-");
        }
        data.attributeList = valueList;
        try {
            var resp = applications.postQuery('x_organization_assemble_control', 'personattribute', JSON.stringify( data ));
            var json = Utils.parseResp( resp );
            if( json.type && json.type == "success" ){
                return "";
            }else{
                return Utils.getFailText( json );
            }
        }catch(e){
            return Utils.processError( e, "新增用户属性AttributeAction.add() 出错: " );
        }
    },
    remove : function( data ){
        try {
            var resp = applications.deleteQuery('x_organization_assemble_control', 'personattribute/'+data.id );
            var json = Utils.parseResp( resp );
            if( json.type && json.type == "success" ){
                return "";
            }else{
                return Utils.getFailText( json );
            }
        }catch(e){
            return Utils.processError( e, "删除用户属性AttributeAction.remove() 出错: " );
        }
    },
    update : function( flag, data_new, data_old ){
        for( var key in data_new ){
            if( key != "distinguishedName" ){
                data_old[key] = data_new[key];
            }
        }
        data_old.person = flag;
        data_old.attributeList = typeof( data_new.value ) == "string" ? [data_new.value] : data_new.value;
        try {
            var resp = applications.putQuery('x_organization_assemble_control', 'personattribute/'+data_old.id, JSON.stringify(data_old) );
            var json = Utils.parseResp( resp );
            if( json.type && json.type == "success" ){
                return "";
            }else{
                return Utils.getFailText( json );
            }
        }catch(e){
            return Utils.processError( e, "修改用户属性AttributeAction.update() 出错: " );
        }
    },
    compare : function( json ){
        var attribute_new = json.attributeList; //传入的用户属性
        var errorText = "";
        var flag = Utils.getUserFlag( json );
        if( flag ){
            try{
                var resp = applications.getQuery('x_organization_assemble_control', 'personattribute/list/person/'+flag );
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
                return Utils.processError( e, "修改用户属性AttributeAction.compare() 出错: " );
            }
        }
    }
};

var IdentityAction = {
    add :function( flag, unit, personName, ignoreFlag ){
        //flag: "",	//组织唯一编码unique/组织的distinguishedName/组织id
        //    orderNumber: "",	//在组织里的排序号,升序排列,为空在最后
        //    description: "",	//描述
        //    duty : "",			//用户在该组织的职务
        //    position : ""		//用户在该组织的岗位
        var data = {
            name : personName,
            person : flag,
            unit : unit.flag,
            description : unit.description
        };
        try {
            var resp = applications.postQuery('x_organization_assemble_control', 'identity', JSON.stringify( data ));
            var json = Utils.parseResp( resp );
            if( json.type === "success" ){
                return "";
            }else{
                if( ignoreFlag != "no" ){
                    archiveFlag = true;
                }else{
                    return Utils.getFailText( json );
                }
                //if( json.prompt == "com.x.organization.assemble.control.jaxrs.identity.ExceptionUnitNotExist" ){ //组织不存在，创建组织
                //var u_data = { name : unit.flag, unique : unit.flag };
                //var json = Utils.parseResp( applications.postQuery('x_organization_assemble_control', 'unit', JSON.stringify( u_data )));
                //if( json.type == "success" ){
                //    return IdentityAction.add( flag, unit );
                //}
                //}else{
                //    return Utils.getFailText( json );
                //}
            }
            return "";
        }catch(e){
            return Utils.processError( e, "新增用户身份IdentityAction.add() 出错: " );
        }
    },
    remove : function( data ){
        try {
            var resp = applications.deleteQuery('x_organization_assemble_control', 'identity/'+data.id );
            var json = Utils.parseResp( resp );
            if( json.type && json.type == "success" ){
                return "";
            }else{
                return Utils.getFailText( json );
            }
        }catch(e){
            return Utils.processError( e, "删除用户身份IdentityAction.remove() 出错: " );
        }
    },
    update : function( identity, unit_new, unit_old ){
        //flag: "",	//组织唯一编码unique/组织的distinguishedName/组织id
        //    orderNumber: "",	//在组织里的排序号,升序排列,为空在最后
        //    description: "",	//描述
        //    duty : "",			//用户在该组织的职务
        //    position : ""		//用户在该组织的岗位
        if( identity.orderNumber != unit_new.orderNumber || identity.description != unit_new.description ){
            identity.orderNumber = unit_new.orderNumber;
            identity.description = unit_new.description;
            try {
                var resp = applications.putQuery('x_organization_assemble_control', 'identity/'+identity.id, JSON.stringify(identity) );
                var json = Utils.parseResp( resp );
                if( json.type && json.type == "success" ){
                    return "";
                }else{
                    return Utils.getFailText( json );
                }
            }catch(e){
                return Utils.processError( e, "修改用户身份IdentityAction.update() 出错: " );
            }
        }

    },
    compare : function( json ){
        var errorText = "";
        var unit_new = json.unitList; //传入的用户所在部门
        var flag = Utils.getUserFlag( json );
        if( !flag )return "修改用户身份IdentityAction.compare() 出错: 未能找到用户标志";
        try{
            var resp = applications.getQuery('x_organization_assemble_control', 'identity/list/person/'+flag );
            var json_identityList = Utils.parseResp( resp );
            var identityList;
            if( json_identityList.type == "success" ){
                identityList = json_identityList.data || [];
            }else{
                identityList = [];
                //return Utils.getFailText( json_identityList );
            }

            var req = {"personList":[flag]};
            var unitResq = applications.postQuery('x_organization_assemble_express', 'unit/list/person/object', JSON.stringify( req ) );
            var json_unit_old = Utils.parseResp( unitResq );
            var unit_old;
            if( json_unit_old.type == "success" ){
                unit_old = json_unit_old.data || [];
            }else{
                return Utils.getFailText( json_unit_old );
            }

            for( var i=0; i<unit_old.length; i++ ){
                var obj_new = IdentityAction.getEqualUnitFromArray( unit_new, unit_old[i] );
                if( obj_new == null ){ //老的不在了，要删除
                    print("删除身份");
                    var identity = IdentityAction.getIdentityByUnit( identityList, unit_old[i] );
                    errorText = IdentityAction.remove( identity )
                }else{ //已经存在，要修改
                    print("修改身份");
                    var identity = IdentityAction.getIdentityByUnit( identityList, unit_old[i] );
                    errorText = IdentityAction.update( identity, obj_new, unit_old[i] );
                }
            }
            for( var i=0; i<unit_new.length; i++ ){
                if( !unit_new[i].flag  || unit_new[i].flag == "" )continue;
                var obj_old = IdentityAction.getEqualUnitFromArray( unit_old, unit_new[i] );
                if( obj_old == null ){ //需要新增
                    print("新增身份");
                    errorText = IdentityAction.add( flag, unit_new[i], json.name, json.ignoreFlag );
                }
            }
            return errorText;
        }catch(e){
            return Utils.processError( e, "修改用户身份IdentityAction.compare() 出错: " );
        }
    },
    getIdentityByUnit: function(identityList, unit){
        for( var i = 0; i<identityList.length; i++ ){
            var identity = identityList[i];
            if( identity.unitLevelName === unit.levelName ){
                return identity;
            }
        }
    },
    getEqualUnitFromArray : function( sourceUnitArray, targetUnit ){
        for( var i=0; i<sourceUnitArray.length; i++ ){
            if( IdentityAction.unitEquals( sourceUnitArray[i], targetUnit ) ){
                return sourceUnitArray[i];
            }
        }
        return null;
    },
    unitEquals: function( source, target ) {
        if (target.flag && (Utils.arrayIndexOf([source.flag, source.unique, source.distinguishedName, source.levelName, source.id], target.flag) > -1))return true;
        if (source.flag && (Utils.arrayIndexOf([target.flag, target.unique, target.distinguishedName, target.levelName, target.id], source.flag) > -1))return true;
        if (target.id && target.id == source.id)return true;
        if (target.unique && target.unique == source.unique)return true;
        if (target.distinguishedName && target.distinguishedName == source.distinguishedName)return true;
        if (target.levelName && target.levelName == source.levelName)return true;
        return false;
    }
};

function get( json ){
    var errorText = "";
    var flag = Utils.getUserFlag(json);
    var resp;
    var person_old;
    if( flag ){
        try{
            resp = applications.getQuery('x_organization_assemble_control', "person/"+flag ); //先获取人员信息
            var json_old = Utils.parseResp( resp );
            if( json_old.type && json_old.type == "success" ){
                person_old = json_old.data;
            }else{
                return Utils.getFailText( json_old );
            }
        }catch(e){
            return Utils.processError( e, "get() 出错: " );
        }

        delete person_old.woIdentityList;
        delete person_old.woRoleList;
        delete person_old.woGroupList;
        delete person_old.woPersonAttributeList;
        //delete person_old.control;
        //delete person_old.controllerList;

        return person_old;
        //applications.putQuery('x_organization_assemble_control', "person/"+json.unique, json );
    }else{
        errorText = "参数中没有个人标志：distinguishedName , unique, employee, mobile 或 id, 不能获取用户";
        print(errorText);
        return errorText;
    }
}

function add( requestJson ){
    print("添加个人");
    var json = Utils.objectClone(requestJson);
    var errorText;
    var response;
    var resp;
    try{
        if( json.superior && json.superior != "" && json.ignoreFlag != "no" ){ //判断汇报对象在不在
            var superiorData = get({ flag : json.superior });
            if( typeof( superiorData ) != "object" ){ //不在则新建
                archiveFlag = true;
                delete json.superior;
            }
        }

        var personData = get(json);
        if( typeof personData == "object" ){
            if( json.forceFlag && json.forceFlag == "yes" ){
                for( var key in json){
                    if( key !== "action" && key !== "attributeList" && key !== "unitList" ){
                        if( key == "orderNumber" && (!json[key] || json[key]=="") ){
                        }else if( key == "id" || key == "unique" || key == "distinguishedName" || key == "changePasswordTime"){
                        }else{
                            personData[key] = json[key];
                        }
                    }
                }
                if( !personData.controllerList || personData.controllerList == null )personData.controllerList = [];
                resp = applications.putQuery('x_organization_assemble_control', "person/"+personData.id, JSON.stringify(personData) )
            }else{
                errorText = "人员“"+ Utils.getUserFlag(json) +"”已经在系统内存在";
            }
        }else{
            if( json.controllerList === null )json.controllerList = [];
            var data = Utils.objectClone(json);
            if(data.attributeList)delete data.attributeList;
            if(data.unitList)delete data.unitList;
            data.controllerList = [];
            resp = applications.postQuery( "x_organization_assemble_control", 'person', JSON.stringify(data));
        }
        if( resp && !errorText ){
            response = Utils.parseResp( resp );
            if( response.type && response.type == "success" ){
                if( !json.attributeList  )json.attributeList = [];
                errorText = AttributeAction.compare( json );

                if( !json.unitList )json.unitList = [];
                errorText = IdentityAction.compare( json );
            }else{
                errorText = Utils.getFailText( response );
            }
        }
        if( archiveFlag && !errorText ){
            Utils.saveToLocal( requestJson ); //保存到本地
        }
    }catch(e){
        errorText = Utils.processError( e, "添加个人 add() 出错：" );
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
    print("修改个人");
    var json = Utils.objectClone(requestJson);
    var errorText;
    var response;
    try{
        if( json.superior && json.superior != "" && json.ignoreFlag != "no" ){ //判断汇报对象在不在
            var superiorData = get({ flag : json.superior });
            if( typeof( superiorData ) != "object" ){ //不在则新建
                archiveFlag = true;
                delete json.superior;
            }
        }

        var personData = get(json);
        if( typeof personData == "object" ){
            for( var key in json){
                if( key !== "action" && key !== "attributeList" && key !== "unitList" ){
                    if( key == "orderNumber" && (!json[key] || json[key]=="") ){
                    }else if( key == "id" || key == "unique" || key == "distinguishedName" || key == "changePasswordTime"){
                    }else{
                        personData[key] = json[key];
                    }
                }
            }
            if( !personData.controllerList || personData.controllerList == null )personData.controllerList = [];
            var resp = applications.putQuery('x_organization_assemble_control', "person/"+personData.id, JSON.stringify(personData) );
            var response = Utils.parseResp( resp );
            if( response.type && response.type == "success" ){
                if( !json.attributeList  )json.attributeList = [];
                errorText = AttributeAction.compare( json );

                if( !json.unitList )json.unitList = [];
                errorText = IdentityAction.compare( json );
            }else{
                errorText = Utils.getFailText( response );
            }
        }else{
            errorText = personData;
        }
        if( archiveFlag && !errorText){
            Utils.saveToLocal( requestJson ); //保存到本地
        }
    }catch(e){
        errorText = Utils.processError( e, "修改个人 update() 出错：" );
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

function updatePassword(json){
    print("修改用户密码");
    var errorText = "";
    var response;
    var flag = Utils.getUserFlag(json);
    if( flag ){
        try{
            var data = { "value": json.password };
            var resp = applications.putQuery('x_organization_assemble_control', "person/"+flag+"/set/password", JSON.stringify(data) ); //修改密码
            response = Utils.parseResp( resp );
            if( response.type && response.type == "success" ){
            }else{
                errorText = Utils.getFailText( response );
            }
        }catch(e){
            errorText = Utils.processError( e, "修改用户密码出错 " + flag + " updatePassword() 出错：" );
        }
    }else{
        errorText = "参数中没有个人标志：distinguishedName , unique, employee, mobile 或 id, 不能获取用户";
        print(errorText);
    }
    var result = {
        "result" : errorText ? "error" : "success",
        "description" : errorText || ""
    };
    //if( response && response.data ){
    //    result.id = response.data.id;
    //}
    return result;
}


function updateSuperior(json){
    print("修改个人汇报对象");
    var errorText;
    var response;
    try{
        var personData = get(json);
        if( typeof personData == "object" ){
            personData.superior = json.superior;
            if( !personData.controllerList )personData.controllerList = [];
            var resp = applications.putQuery('x_organization_assemble_control', "person/"+personData.id, JSON.stringify(personData) );
            response = Utils.parseResp( resp );
            if( response.type && response.type == "success" ){
            }else{
                errorText = Utils.getFailText( response );
            }
        }else{
            errorText = personData;
        }
    }catch(e){
        errorText = Utils.processError( e, "修改个人 updateSuperior() 出错：" );
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

function remove(json){
    print("删除个人");
    var errorText;
    var response;
    try{
        var person = get(json);
        if( typeof person == "object" ){
            var resp = applications.deleteQuery('x_organization_assemble_control', "person/"+person.id ); //s人员信息
            response = Utils.parseResp( resp );
            if( response.type && response.type == "success" ){
            }else{
                errorText = Utils.getFailText( response );
            }
        }else{
            errorText = person;
        }
    }catch(e){
        errorText = Utils.processError( e, "删除个人 remove() 出错：" );
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
            case "updatepwd":
                result = updatePassword( requestJson );
                break;
            case "updateSuperior":
                result = updateSuperior( requestJson );
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
