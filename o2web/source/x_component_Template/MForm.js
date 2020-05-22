/*
外部方法
 getResult(verify, separator, isAlert, onlyModified, keepAllData )
 返回：如果不校验或者通过了校验返回表单上的数据，如果不能通过校验返回null
 参数：
 verify 是否校验，
 separator 多值域的分割符，
 isAlert 如果verify为true则是否提醒用户校验未通过，
 onlyModified只获取修改过的结果，
 keepAllData获取所有MDOMItem对象的值并保留未生成对象的data

 getItem( name )
 根据name返回MDOMItem对象

 verify(isAlert)
 校验用户是否已经填写了必填对象
 */
MWF.xDesktop.requireApp("Template", "MDomItem", null, false);
var MForm = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options : {
        style : "default",
        isNew : false,
        isEdited : false,
        emptyItemContainer : true,
        showNotEmptyFlag : false,

        //batch，所有item的错误都校验，每个item所有错误一起校验，错误显示在Item的字段后或指定区域；
        //batchSingle, 所有item的错误都校验，每个item只校验一个错误，错误显示在Item的字段后或指定区域；
        //alert，验证时遇到一个不成功的Item用app.notice()弹出消息后中断,
        //single，验证时遇到一个不成功的Item在字段后面或指定区域显示错误信息后中断
        verifyType : "alert",
        itemTemplateUrl : "",
        containerHtml : null,
        itemTemplate : null,
        hasColon : false
    },
    initialize: function (container, data, options , app, css ) {

        this.setOptions(options);

        this.path = "../x_component_Template/$MForm/";
        this.cssPath = "../x_component_Template/$MForm/"+this.options.style+"/css.wcss";
        this._loadCss();
        if( css ){
            this.css = Object.merge( this.css, css )
        }

        this.app = app;
        this.container = $(container);

        this.data = data;

        this.isSourceDataEmpty = false;
        if( !this.data || this.data == "" ){
            this.isSourceDataEmpty = true;
            this.data = [{}];
        }

        this.itemTemplateUrl = this.options.itemTemplateUrl;
        this.itemTemplate = this.options.itemTemplate;
        this.items = null;

        this.labelContainers = null;
        this.itemContainers = null;

        this.valSeparator = /,|;|\^\^|\|/g; //如果是多值对象，作为用户选择的多个值的分隔符
    },
    load: function () {
        this.fireEvent("queryLoad");

        this.loadTemplate( function(){
            //如果itemTemplate没有name赋值Key
            for( var it in this.itemTemplate ){
                if( !this.itemTemplate[it]["name"] ){
                    this.itemTemplate[it]["name"] = it;
                }
                this.itemTemplate[it]["key"] = it;
            }

            //如果itemTemplate没有name和Key不一致，那么根据name赋值itemTemplate
            var json = {};
            for( var it in this.itemTemplate ){
                if( it != this.itemTemplate[it]["name"] ){
                    json[ this.itemTemplate[it]["name"] ] = this.itemTemplate[it];
                }
            }
            for( var it in json ){
                this.itemTemplate[it] = json[it];
            }

            this.items = {};
            this.itemsByKey = {};

            if( this.options.containerHtml ){
                this.container.set("html",this.options.containerHtml);
            }
            this.labelContainers = this.container.getElements("[lable]");
            this.itemContainers = this.container.getElements("[item]");
            this.formatStyles();

            (this.options.isEdited || this.options.isNew) ? this.loadEdit() : this.loadRead();

            this.fireEvent("postLoad");
        }.bind(this) )
    },
    formatStyles: function(){
        this.container.getElements("[styles]").each(function(el){
            var styles = el.get("styles");
            if( styles && this.css[styles] ){
                el.setStyles( this.css[styles] )
            }
        }.bind(this));
        this.container.getElements("[class]").each(function(el){
            var className = el.get("class");
            if( className && this.css[className] ){
                el.setStyles( this.css[className] )
            }
        }.bind(this))
    },
    loadTemplate : function( callback ){
        if ( !this.itemTemplate && this.itemTemplateUrl) {
            MWF.getJSON(this.itemTemplateUrl, function(json){
                this.itemTemplate = json;
                if( callback )callback();
            }.bind(this));
        }else{
            if( callback )callback();
        }
    },
    loadEdit : function() {
        if (this.options.isNew) {
            this.formatEdit(this.itemTemplate, true);
        } else if (!this.isSourceDataEmpty) {
            if (typeOf(this.data) != "array") {
                this.data = [this.data]
            }
            for (var i = 0; i < this.data.length; i++) {
                var d = this.data[i];
                var items = this.itemTemplate;
                for (var it in d ) {
                    if (items[it]) {
                        items[it].value = d[it];
                    }
                }
                this.formatEdit(items, false);
            }
            for (var it in this.itemTemplate ) {
                this.itemTemplate[it].value = "";
            }
        } else {
            this.formatEdit(this.itemTemplate, true);
        }
    },
    loadRead : function() {
        if (this.options.isNew) {
            this.formatRead(this.itemTemplate);
        } else if (!this.isSourceDataEmpty) {
            if ( typeOf(this.data) != "array") {
                this.data = [this.data]
            }
            for (var i = 0; i < this.data.length; i++) {
                var d = this.data[i];
                var items = this.itemTemplate;
                for (var it in d ) {
                    if (items[it]) {
                        items[it].value = d[it];
                    }
                }
                this.formatRead(items);
            }
            for (var it in this.itemTemplate ) {
                this.itemTemplate[it].value = "";
            }
        }
    },
    //formatRead : function(itemData) {
    //    var self = this;
    //
    //    this.labelContainers.each(function( el ) {
    //        var obj = itemData[el.get("lable")];
    //        if (!obj)
    //            return;
    //        el.set("text",obj.text);
    //    });
    //
    //    this.itemContainers.each(function( el ) {
    //        var obj = itemData[el.get("item")];
    //        if (!obj)
    //            return;
    //
    //        if( obj.style ){
    //            el.setStyles( obj.style )
    //        }
    //
    //        if( typeOf( obj.value ) == "function" ) {
    //            var value = obj.value();
    //        }else if( typeOf( obj.value ) == "boolean" ){
    //                var value = obj.value.toString();
    //        }else{
    //            var value = (obj.value ? obj.value : "").toString();
    //        }
    //        if( obj.type == "hidden" ) {
    //            el.hide();
    //            el.set("html",value.replace( this.valSeparator, ",").replace(/\n/g,"<br/>") )
    //        }else if( obj.type == "radio" || obj.type == "select" || obj.type == "checkbox" || obj.type == "multiselect" ) {
    //            var values = value.split( this.valSeparator );
    //            if( obj.selectText && obj.selectValue ){
    //                var selectValues = this._getSelectOpt( obj.selectValue );
    //                var selectTexts = this._getSelectOpt( obj.selectText );
    //                var result = [];
    //                for( i=0;i<selectValues.length;i++){
    //                    if( values.contains( selectValues[i] ) ){
    //                        result.push( selectTexts[i] )
    //                    }
    //                }
    //                el.set("html",result.join(","));
    //            }else{
    //                el.set("html",value.replace( this.valSeparator, ",").replace(/\n/g,"<br/>") )
    //            }
    //        }else if( obj.type == "rtf"){
    //            el.set("html",value )
    //        } else {
    //            el.set("html",value.replace( this.valSeparator, ",").replace(/\n/g,"<br/>") )
    //        }
    //    }.bind(this))
    //},
    formatRead : function(itemData) {
        var self = this;

        this.labelContainers.each(function( el ) {
            var obj = itemData[el.get("lable")];
            if (!obj)
                return;
            el.set("text",obj.text + (self.options.hasColon ? "：" : "") );
        });
        this.itemContainers.each(function( el ) {
            var obj = itemData[el.get("item")];
            if (!obj)
                return;
            if (self.options.emptyItemContainer) {
                el.set("html","");
            }
            obj.isEdited = false;
            self.loadItem(obj, el);
        })
    },
    _getSelectOpt: function( option ){
        var opt = option;
        if( typeOf( opt ) == "function" ){
            opt = opt.call();
        }
        return typeOf( opt ) == "array" ? opt : opt.split( this.valSeparator );
    },
    formatEdit : function(itemData, isNew, unid) {
        var self = this;

        this.labelContainers.each(function( el ) {
            var obj = itemData[el.get("lable")];
            if (!obj)
                return;
            var text = obj.text + (self.options.hasColon ? "：" : "");
            if (self.options.showNotEmptyFlag && obj.notEmpty) {
                el.set("html", text + "<span style='color:red;'>*</span>")
            } else {
                el.set("text",text);
            }
        });
        this.itemContainers.each(function( el ) {
            var obj = itemData[el.get("item")];
            if (!obj)
                return;
            if (self.options.emptyItemContainer) {
                el.set("html","");
            }
            self.loadItem(obj, el);
        })
    },
    loadItem : function(template, container) {
        //if( template.disable )return;

        template.objectId = template.name;
        var item = new MDomItem(container, template, this, this.app, this.css ); 
        if( this.options.verifyType == "batchSingle" ){
            item.options.warningType = "single";
        }else{
            item.options.warningType = this.options.verifyType;
        }
        //item.parent = this;
        item.load();

        this.items[template.objectId] = item;
        this.itemsByKey[template.key] = item;
    },
    enableItem: function( itemName ){
        if( itemName && this.items[itemName] ){
            var item = this.items[itemName];
            if( item.options.disable ){
                item.enable();
            }
        }
    },
    disableItem: function( itemName ){
        if( itemName && this.items[itemName] ){
            var item = this.items[itemName];
            if( !item.options.disable ){
                item.disable();
            }
        }
    },
    verify : function(isShowWarming) {
        var flag = true;
        for (var it in this.items ) {
            if (!this.items[it].verify(isShowWarming)) {
                if (this.options.verifyType == "batch" || this.options.verifyType == "batchSingle") {
                    flag = false;
                } else {
                    return false;
                }
            }
        }
        return flag;
    },
    getItemsKeyValue : function(separator , onlyModified ) {
        //separator 多值合并分隔符
        var key_value = {};
        for (var it in this.items ) {
            var item = this.items[it];
            var value = onlyModified ? item.getModifiedValue() : item.getValue();
            if( value != null ){
                if (typeOf(value) === "array") {
                    key_value[item.options.objectId] = ( typeOf(separator) == "string" ? value.join(separator) : value );
                } else {
                    key_value[item.options.objectId] = value;
                }
            }
        }
        return key_value;
    },
    getResult : function(verify, separator, isShowWarming, onlyModified, keepAllData ) {
        if ( !verify || this.verify(isShowWarming)) {
            if( keepAllData ){
                var result = this.data[0];
                var keyValue = this.getItemsKeyValue(separator, onlyModified);
                for( var key in keyValue ){
                    result[ key ] = keyValue[ key ];
                }
                return result;
            }else{
                return this.getItemsKeyValue(separator, onlyModified);
            }
        } else {
            return false;
        }
    },
    getItem : function( name ){
        return this.items[name] || this.itemsByKey[name];
    },
    clearWarning: function( type ){
        for (var it in this.items ) {
            var item = this.items[it];
            if( !type ){
                item.clearWarning( "empty" );
                item.clearWarning( "invalid" );
            }else{
                item.clearWarning( type )
            }
        }
    },
    reset: function(){
        for (var it in this.items ) {
            var item = this.items[it];
            item.reset();
        }
    },
    destroy : function(){
        Object.each(this.items, function(item){
            item.destroy();
        }.bind(this));
        this.container.empty();
        MWF.release(this);
    }
});