print( "积分分发：------------------------------------------" );
var Config = {
    personPointCategoryAlias : "积分-个人积分",
    unitPointCategoryAlias : "积分-部门积分",
    pointLogCategoryAlias : "积分-积分记录",
    query : "pointManagerQuery",
    unitPointView : "unitPointView",
    companyLevel : 1,
    departmentLevel_1 : 2
};

var _self = this;
var applications = this.Action.applications;

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
                message : "服务响应是null"
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
    typeOf : function( obj ){
        if (item == null) return 'null';
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

        //if( obj === null )return "null";
        //if( "object" !== typeof obj )return typeof obj;
        //return typeof obj.length==='number' ? 'array' : 'object';
    }
};

var Org = {
    getSuperUnitByLevel : function(name, level){
        var unitList = _self.org.listSupUnit(name, true);
        var result;
        if(unitList){
            unitList.each( function(u){
                if( u.level == level ){
                    result = u.distinguishedName
                }
            })
        }
        return result;
    }
};

var Calendar = {
    getLastMonth : function( date ) {
        var Date = Java.type("java.util.Date");
        if( !date ){
            date = new Date();
        }
        var sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        var array = sdf.format( date ).split("-");

        var date_lastMonth = sdf.parse( array[0] + "-" + array[1] + "-" + "01");

        var Calendar = Java.type("java.util.Calendar");
        var c = Calendar.getInstance();
        c.setTime(date_lastMonth);
        c.add(Calendar.MONTH, -1);

        return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1);
    }
};

//个人积分分发 start：------------------------------------------
print( "个人门积分分发 start：------------------------------------------" );
var PersonPointDispatch = function( identity ){
    if( Utils.typeOf( identity ) === "object" ){
        this.identityDn = identity.distinguishedName;
        this.personDn = identity.person;
    }else{
        this.identityDn = identity;
        var personList = _self.org.listPersonWithIdentity( identity );
        if( personList && Utils.typeOf( personList ) == "array" )this.personDn = personList[0].distinguishedName;
    }
};
var PPD = PersonPointDispatch;
PPD.prototype.getCategory = function(){
    if( !_self.personPointCategory ){
        var o = applications.putQuery('x_cms_assemble_control', "categoryinfo/alias/" +  Config.personPointCategoryAlias );
        var json = Utils.parseResp( o );
        _self.personPointCategory = json.data;
    }
    return _self.personPointCategory;
};
PPD.prototype.getProfile = function(){
    if( !this.profileId ){
        var uri = "document/filter/list/(0)/next/1";
        var filter = {
            categoryAliasList : [ Config.personPointCategoryAlias ],
            publisherList : [ this.personDn ]
        };
        var o = applications.putQuery('x_cms_assemble_control',uri, JSON.stringify(filter) );
        var json = Utils.parseResp( o );
        if( json.data.length === 0 ){
            this.profileId = null;
            this.profile = null;
        }else{
            this.profileId = json.data[0].id;
        }
    }
    //print( "积分分发：" + JSON.stringify( json.data ) );
    if( this.profileId ){
        var o1 = applications.getQuery('x_cms_assemble_control', "data/document/"+this.profileId ); //只获取数据
        var json1 = Utils.parseResp( o1 );
        this.profile = json1.data;
    }
    return this.profile;
};
PPD.prototype.saveProfile = function( data ){
    // data = {
    //     pointType
    //     getedPoint
    //     projectText
    // }
    this.getProfile();
    if( !this.profile ){
        this.createProfile( data );
    }else{
        this.updateProfile( data );
    }
};
PPD.prototype.createProfile = function( data ){
    var company = _self.org.getUnitByIdentity( this.identityDn, Config.companyLevel );
    var person = _self.org.getPerson( this.personDn );
    var categoryData = this.getCategory();
    var doc = {
        //"id" : this.documentAction.getUUID(),
        "isNewDocument" : true,
        "title": this.personDn.split("@")[0]+"个人积分",
        "creatorIdentity": this.identityDn,
        "appId" : categoryData.appId,
        "attachmentList" : [],
        "form" : categoryData.formId,
        "formName" :categoryData.formName,
        //"docStatus" : "draft",
        "categoryName" : categoryData.name || categoryData.categoryName,
        "categoryId" : categoryData.id,
        "categoryAlias" : Config.personPointCategoryAlias,
        "docData" : {
            "person" : Utils.typeOf( person ) == "array" ? person[0] : person,
            "personDn" : this.personDn,
            "annualPoint" : data.getedPoint,
            "permanentPoint" : data.getedPoint,
            "convertiblePoint" : data.getedPoint,
            "convertedPoint" : "0",
            "currentYear" : new Date().getFullYear(),
            "companyDn" : Utils.typeOf( company ) == "array" ? company[0].distinguishedName : company.distinguishedName
        }
    };
    this.updatePointTypeData( doc.docData, data, true );
    var o = applications.putQuery('x_cms_assemble_control', "document/publish/content", JSON.stringify(doc) );
    var json = Utils.parseResp( o );
    this.profileId = json.data.id;
};
PPD.prototype.updateProfile = function( data, operation ){ //只保存数据
    var doc = this.profile;
    if( operation === "add" ){ //新增积分

        doc.permanentPoint = ( doc.permanentPoint ? parseInt( doc.permanentPoint ) : 0 ) + parseInt( data.getedPoint );

        var year = new Date().getFullYear();
        if( parseInt(doc.currentYear) !== year ){
            doc.annualPoint = data.getedPoint;
            doc.convertiblePoint = data.getedPoint;
            this.updatePointTypeData( doc, data, false );
            doc.currentYear = year;
        }else{
            doc.annualPoint = ( doc.annualPoint ? parseInt( doc.annualPoint ) : 0 ) + parseInt( data.getedPoint );
            doc.convertiblePoint = ( doc.convertiblePoint ? parseInt( doc.convertiblePoint ) : 0 ) + parseInt( data.getedPoint );
            this.updatePointTypeData( doc, data, true );
        }
    }
    var o = applications.putQuery('x_cms_assemble_control', "data/document/"+this.profileId, JSON.stringify(doc) );
    var json = Utils.parseResp( o );
};
PPD.prototype.updatePointTypeData = function( docData, data, isCurrentYear ){
    var typeJson = docData[ "typeJson" ] = docData[ "typeJson" ] || {};
    var typeData = typeJson[ data.pointType ] = typeJson[ data.pointType ] || {};
    this.updatePoint( typeData, data.getedPoint, isCurrentYear );
    this.updateProjectData( typeData, data, isCurrentYear  )
};
PPD.prototype.updateProjectData = function( typeData, data, isCurrentYear ){
    var projectJson = typeData[ "projectJson" ] = typeData[ "projectJson" ] || {};
    var projectData = projectJson[ data.projectText ] = projectJson[ data.projectText ] || {};
    this.updatePoint( projectData, data.getedPoint, isCurrentYear );
};
PPD.prototype.updatePoint = function( d, getedPoint, isCurrentYear ){
    var point = parseInt(data.getedPoint);
    if( !isCurrentYear ){
        d.annualPoint = point;
    }else{
        d.annualPoint = ( d.annualPoint ? parseInt( d.annualPoint ) : 0 ) + point;
    }
    d.permanentPoint = ( d.permanentPoint ? parseInt( d.permanentPoint ) : 0 ) + point;
};
//个人积分分发 end：------------------------------------------
print( "个人积分分发 end：------------------------------------------" );


print( "部门积分分发 start：------------------------------------------" );
//部门积分分发 start：------------------------------------------"
var UnitPointDispatch = function( identity, unit ){
    var identityDn = this.identityDn = Utils.typeOf( identity ) === "string" ? identity : identity.distinguishedName;
    if( unit ){
        if( Utils.typeOf( unit ) === "object" ){
            this.unit = unit;
            this.unitDn = unit.distinguishedName
        }else{
            this.unit = _self.org.getUnit( unit )[0];
            this.unitDn = unit;
        }
    }else{
        var unitList = _self.org.getUnitByIdentity( identityDn, Config.departmentLevel_1 );
        if( unitList && Utils.typeOf( unitList ) === "array" ){
            this.unit = unitList[0];
            this.unitDn = this.unit.distinguishedName
        }
    }
    var Date = Java.type( "java.util.Date" );
    var date = new Date();
    //this.currentDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format( date );
    this.currentMonth = new java.text.SimpleDateFormat("yyyy-MM").format( date );
    this.currentYear = new java.text.SimpleDateFormat("yyyy").format( date );
    this.preMonth = Calendar.getLastMonth( date );
};
var UPD = UnitPointDispatch;
UPD.prototype.getCategory = function(){
    if( !_self.unitPointCategory ){
        var o = applications.putQuery('x_cms_assemble_control', "categoryinfo/alias/" +  Config.unitPointCategoryAlias );
        var json = Utils.parseResp( o );
        _self.unitPointCategory = json.data;
    }
    return _self.unitPointCategory;
};
UPD.prototype.getProfile = function(){
    if( !this.profileId ){
        var uri = "view/flag/"+ Config.unitPointView +"/query/" + Config.query + "/execute";
        var filterList = {"filterList": [{
            "logic":"and",
            "path": "unitDn",
            "comparison":"equals",
            "value": this.unitDn,
            "formatType":"textValue"
        }]};
        var o = applications.putQuery('x_query_assemble_surface', uri, JSON.stringify( filterList ) );
        var json = Utils.parseResp( o );
        if( json && json.data && json.data.grid && json.data.grid.length > 0 ){
            this.profileId = json.data.grid[0].bundle;
        }else{
            this.profileId = null;
            this.profile = null;
        }
    }
    if( this.profileId ){
        var o1 = applications.getQuery('x_cms_assemble_control', "data/document/"+this.profileId ); //只获取数据
        var json1 = Utils.parseResp( o1 );
        this.profile = json1.data;
    }
    return this.profile;
};
UPD.prototype.saveProfile = function( data ){
    // data = {
    //     pointType
    //     getedPoint
    //     projectText
    // }
    this.getProfile();
    if( !this.profile ){
        this.createProfile( data );
    }else{
        this.updateProfile( data );
    }
};
UPD.prototype.createProfile = function( data ){
    var categoryData = this.getCategory();
    var doc = {
        //"id" : this.documentAction.getUUID(),
        "isNewDocument" : true,
        "title": this.unitDn.split("@")[0]+"部门积分",
        "creatorIdentity": this.identityDn,
        "appId" : categoryData.appId,
        "attachmentList" : [],
        "form" : categoryData.formId,
        "formName" :categoryData.formName,
        //"docStatus" : "draft",
        "categoryName" : categoryData.name || categoryData.categoryName,
        "categoryId" : categoryData.id,
        "categoryAlias" : Config.unitPointCategoryAlias,
        "docData" : {
            "unit" : this.unit,
            "unitDn" : this.unitDn,
            "annualPoint" : data.getedPoint,
            "permanentPoint" : data.getedPoint,
            "currentYear" : this.currentYear,
            "currentMonth" : this.currentMonth,
            "companyDn" : Org.getSuperUnitByLevel( this.unitDn, Config.companyLevel )
        }
    };
    this.updatePointTypeData( doc.docData, data, true, true );
    var o = applications.putQuery('x_cms_assemble_control', "document/publish/content", JSON.stringify(doc) );
    var json = Utils.parseResp( o );
    this.profileId = json.data.id;
};
UPD.prototype.updateProfile = function( data, operation ){ //只保存数据
    var doc = this.profile;
    var isCurrentMonth = doc.currentMonth === this.currentMonth;
    if( !isCurrentMonth ){
        doc.annualPoint_preMonth = doc.annualPoint || 0;
        doc.permanentPoint_preMonth = doc.permanentPoint || 0;
        doc.currentMonth = this.currentMonth;
        doc.preMonth = this.preMonth;
    }

    doc.permanentPoint = ( doc.permanentPoint ? parseInt( doc.permanentPoint ) : 0 ) + parseInt( data.getedPoint );
    if( parseInt(doc.currentYear) !== this.currentYear ){
        doc.annualPoint = data.getedPoint;
        this.updatePointTypeData( doc, data, false, isCurrentMonth );
        doc.currentYear = this.currentYear;
    }else{
        doc.annualPoint = ( doc.annualPoint ? parseInt( doc.annualPoint ) : 0 ) + parseInt( data.getedPoint );
        this.updatePointTypeData( doc, data, true, isCurrentMonth );
    }
    var o = applications.putQuery('x_cms_assemble_control', "data/document/"+this.profileId, JSON.stringify(doc) );
    var json = Utils.parseResp( o );
};
UPD.prototype.updatePointTypeData = function( docData, data, isCurrentYear, isCurrentMonth ){
    var typeJson = docData[ "typeJson" ] = docData[ "typeJson" ] || {};
    var typeData = typeJson[ data.pointType ] = typeJson[ data.pointType ] || {};
    this.updatePoint( typeData, data.getedPoint, isCurrentYear, isCurrentMonth );
    this.updateProjectData( typeData, data, isCurrentYear, isCurrentMonth  )
};
UPD.prototype.updateProjectData = function( typeData, data, isCurrentYear, isCurrentMonth ){
    var projectJson = typeData[ "projectJson" ] = typeData[ "projectJson" ] || {};
    var projectData = projectJson[ data.projectText ] = projectJson[ data.projectText ] || {};
    this.updatePoint( projectData, data.getedPoint, isCurrentYear, isCurrentMonth );
};
UPD.prototype.updatePoint = function( d, getedPoint, isCurrentYear, isCurrentMonth ){
    var point = parseInt(data.getedPoint);
    if( !isCurrentMonth ){
        //if( !isCurrentYear ){
        //    d.annualPoint_preMonth = d.annualPoint || 0;
        //}else{
        //    d.annualPoint_preMonth = 0;
        //}
        d.annualPoint_preMonth = d.annualPoint || 0;
        d.permanentPoint_preMonth = d.permanentPoint || 0;
    }
    if( !isCurrentYear ){
        d.annualPoint = point;
    }else{
        d.annualPoint = ( d.annualPoint ? parseInt( d.annualPoint ) : 0 ) + point;
    }
    d.permanentPoint = ( d.permanentPoint ? parseInt( d.permanentPoint ) : 0 ) + point;
};
//部门积分分发 end：------------------------------------------"
print( "部门积分分发 end：------------------------------------------" );


print( "积分记录 start：------------------------------------------" );
//"积分记录 start：------------------------------------------"
var PointLog = {
    getCategory : function(){
        if( !_self.unitPointCategory ){
            var o = applications.putQuery('x_cms_assemble_control', "categoryinfo/alias/" +  Config.pointLogCategoryAlias );
            var json = Utils.parseResp( o );
            _self.unitPointCategory = json.data;
        }
        return _self.unitPointCategory;
    },
    saveDoc : function( data, identity, unit, company ){
        var identityDn = Utils.typeOf( identity ) === "string" ? identity : identity.distinguishedName;
        var personDn, unitDn, companyDn;

        if( Utils.typeOf( identity ) === "object" )personDn = identity.person;
        if( !personDn ){
            var personList = _self.org.listPersonWithIdentity( identity );
            if( personList && personList.length > 0 )personDn = personList[0].distinguishedName;
        }

        if( unit ){
            unitDn = Utils.typeOf( unit ) === "string" ? unit : unit.distinguishedName;
        }else{
            unit = _self.org.getUnitByIdentity( identityDn, Config.departmentLevel_1 );
            if(unit && unit.length > 0)unitDn = unit[0].distinguishedName
        }

        if( company ) {
            companyDn = Utils.typeOf(company) === "string" ? company : company.distinguishedName;
        }else{
            company = _self.org.getUnitByIdentity( identityDn, Config.companyLevel );
            if(company && company.length > 0)companyDn = company[0].distinguishedName;
        }

        var Date = Java.type( "java.util.Date" );
        var nowStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());

        var categoryData = PointLog.getCategory();
        var doc = {
            //"id" : this.documentAction.getUUID(),
            "isNewDocument" : true,
            "title": "积分记录",
            "creatorIdentity": identityDn,
            "appId" : categoryData.appId,
            "attachmentList" : [],
            "form" : categoryData.formId,
            "formName" :categoryData.formName,
            //"docStatus" : "draft",
            "categoryName" : categoryData.name || categoryData.categoryName,
            "categoryId" : categoryData.id,
            "categoryAlias" : Config.unitPointCategoryAlias,
            "docData" : {
                "personDn" : personDn,
                "unitDn" : unitDn,
                "companyDn" : companyDn,
                "projectText" : data.projectText,
                "projectName" : data.projectName,
                "getedPoint" : "getedPoint",
                "date" : nowStr
            }
        };
        var o = applications.putQuery('x_cms_assemble_control', "document/publish/content", JSON.stringify(doc) );
        var json = Utils.parseResp( o );
        this.profileId = json.data.id;
    }
};
//"积分记录 end：------------------------------------------"
print( "积分记录 end：------------------------------------------" )

this.define("dispachPersonFlow", function(){
    var identityDn = this.workContext.getWork().creatorIdentity;

    var ppd = new PersonPointDispatch( identityDn );
    ppd.saveProfile( {
        pointType : this.data.pointType,
        projectText : this.data.projectText,
        getedPoint : this.data.getedPoint
    });

    var upd = new UnitPointDispatch( identityDn );
    upd.saveProfile({
        pointType : this.data.pointType,
        projectText : this.data.projectText,
        getedPoint : this.data.getedPoint
    });

    PointLog.saveDoc({
        pointType : this.data.pointType,
        projectText : this.data.projectText,
        getedPoint : this.data.getedPoint
    }, identityDn )
}.bind(this));