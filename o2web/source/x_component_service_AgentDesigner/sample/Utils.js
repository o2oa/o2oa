var _self = this;

var applications = this.Action.applications; //流程事件中
//var applications = resources.getContext().applications(); //代理和接口中

var Utils = {
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
    arrayErase : function(array, target){
        for (var i = array.length; i--;){
            if (array[i] === target) array.splice(i, 1);
        }
        return array;
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
    typeOf : function( item ){
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

        //if( obj === null )return "null";
        //if( "object" !== typeof obj )return typeof obj;
        //return typeof obj.length==='number' ? 'array' : 'object';
    }
};

var Org = {
    getUserFlag : function( json ){
        return json.flag || json.distinguishedName || json.unique || json.employee || json.mobile || json.id;
    },
    getSuperUnitByUnitByLevel : function(unit, level){
        var unitList = _self.org.listSupUnit(unit, true);
        var result;
        if(unitList){
            unitList.each( function(u){
                if( u.level == level ){
                    result = u.distinguishedName
                }
            })
        }
        return result;
    },
    getSuperUnitByIdentityByLevel : function( identity, level ){
        var unitList = _self.org.listAllSupUnitWithIdentity( identity );
        var result;
        if(unitList){
            unitList.each( function(u){
                if( u.level == level ){
                    result = u.distinguishedName
                }
            })
        }
        return result;
    },
    getSuperObjectUnitByIdentityByLevel : function( identity, level ){
        var unitList = _self.org.listAllSupUnitWithIdentity( identity );
        var result;
        if(unitList){
            unitList.each( function(u){
                if( u.level == level ){
                    result = u;
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

var CMSPathData = {
    get : function( id, pathList ){
        var o = applications.getQuery('x_cms_assemble_control', CMSPathData.getUri(id, pathList) );
        var json = Utils.parseResp( o );
        return json.data;
    }.bind(this),
    create : function( id, pathList, data ){
        var o = applications.postQuery('x_cms_assemble_control', CMSPathData.getUri(id, pathList), JSON.stringify(data) );
        var json = Utils.parseResp( o );
        return json.data;
    }.bind(this),
    update : function( id, pathList, data ){
        var o = applications.putQuery('x_cms_assemble_control', CMSPathData.getUri(id, pathList), JSON.stringify(data) );
        var json = Utils.parseResp( o );
        return json.data;
    },
    delete : function( id, pathList ){
        var o = applications.deleteQuery('x_cms_assemble_control', CMSPathData.getUri(id, pathList) );
        var json = Utils.parseResp( o );
        return json.data;
    },
    getUri : function( id, pathList ){
        var uri = "data/document/"+id;
        if( pathList && Utils.typeOf(pathList) === "array" && pathList.length > 0 ){
            for( var i=0 ;i< pathList.length; i++ ){
                uri = uri + "/" + encodeURIComponent( pathList[i] );
            }
        }
        return uri;
    }
};

(function(context) {
    'use strict';

    var Timer = Java.type('java.util.Timer');
    var Phaser = Java.type('java.util.concurrent.Phaser');

    var timer = new Timer('jsEventLoop', false);
    var phaser = new Phaser();

    var timeoutStack = 0;
    function pushTimeout() {
        timeoutStack++;
    }
    function popTimeout() {
        timeoutStack--;
        if (timeoutStack > 0) {
            return;
        }
        timer.cancel();
        phaser.forceTermination();
    }

    var onTaskFinished = function() {
        phaser.arriveAndDeregister();
    };

    context.setTimeout = function(fn, millis /* [, args...] */) {
        var args = [].slice.call(arguments, 2, arguments.length);

        var phase = phaser.register();
        var canceled = false;
        timer.schedule(function() {
            if (canceled) {
                return;
            }

            try {
                fn.apply(context, args);
            } catch (e) {
                print(e);
            } finally {
                onTaskFinished();
                popTimeout();
            }
        }, millis);

        pushTimeout();

        return function() {
            onTaskFinished();
            canceled = true;
            popTimeout();
        };
    };

    context.clearTimeout = function(cancel) {
        cancel();
    };

    context.setInterval = function(fn, delay /* [, args...] */) {
        var args = [].slice.call(arguments, 2, arguments.length);

        var cancel = null;

        var loop = function() {
            cancel = context.setTimeout(loop, delay);
            fn.apply(context, args);
        };

        cancel = context.setTimeout(loop, delay);
        return function() {
            cancel();
        };
    };

    context.clearInterval = function(cancel) {
        cancel();
    };

})(this);