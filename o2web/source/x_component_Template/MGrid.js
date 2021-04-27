MWF.xDesktop.requireApp("Template", "MDomItem", null, false);
//MWF.xDesktop.requireApp("Template", "lp." + MWF.language, null, false);
var MGrid = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        style: "default",
        isNew: false,
        isEdited: false,
        showNotEmptyFlag : false,
        verifyType : "batch",	//batch一起校验，或alert弹出
        //batch，所有item的错误都校验，每个item所有错误一起校验，错误显示在Item的字段后或指定区域；
        //batchSingle, 所有item的错误都校验，每个item只校验一个错误，错误显示在Item的字段后或指定区域；
        //alert，验证时遇到一个不成功的Item用app.notice()弹出消息后中断,
        //single，验证时遇到一个不成功的Item在字段后面或指定区域显示错误信息后中断

        itemTemplate : null,

        objectId : "",
        hasSequence  : true,
        hasOperation : false,
        isCreateTh : true,
        containerIsTable : false,
        isCreateTrOnNull : true,
        minTrCount : 0,
        maxTrCount : 0,

        tableAttributes : null,
        thAttributes : null,
        tdAttributes : null,

        textOnly : false,

        tableClass : "formTable",
        thClass : "formTableTitle",
        tdClass : "formTableValue",
        thAlign : "center",
        tdAlign : "left",
        sequenceClass : "formTableSequence",
        addActionTdClass : "formTableAddTd",
        removeActionTdClass : "formTableRemoveTd",
        lp : {
            remove : "",
            add : ""
        }

    },
    initialize: function (container, data, options, app, css) {

        this.setOptions(options);

        this.path = "../x_component_Template/$MGrid/";
        this.cssPath = "../x_component_Template/$MGrid/" + this.options.style + "/css.wcss";
        this._loadCss();
        if (css) {
            this.css = Object.merge(this.css, css)
        }

        this.app = app;
        this.container = $(container);

        this.data = data;

        this.isSourceDataEmpty = false;
        if (!this.data || this.data == "") {
            this.isSourceDataEmpty = true;
            this.data = [{}];
        }

        this.itemTemplate = this.options.itemTemplate;
        this.items = null;

        this.th = null;
        this.trIndex = 0;

        this.trList = [];
        this.trObjs = null;
        this.trObjs_removed = null;
        this.trObjs_new = null;

        this.thTemplate = null;	//属性  lable button_add styles class
        this.trTemplate = null; //属性 item sequence button_remove lable styles class

        this.valSeparator = /,|;|\^\^|\|/g; //如果是多值对象，作为用户选择的多个值的分隔符
    },
    load: function () {
        this.fireEvent("queryLoad");


        //如果itemTemplate没有name赋值Key
        for (var it in this.itemTemplate) {
            if (!this.itemTemplate[it]["name"]) {
                this.itemTemplate[it]["name"] = it;
            }
            this.itemTemplate[it]["key"] = it;
        }

        //如果itemTemplate没有name和Key不一致，那么根据name赋值itemTemplate
        var json = {};
        for (var it in this.itemTemplate) {
            if (it != this.itemTemplate[it]["name"]) {
                json[this.itemTemplate[it]["name"]] = this.itemTemplate[it]
            }
        }
        for (var it in json) {
            this.itemTemplate[it] = json[it];
        }
        this.createTable( this.itemTemplate );
        this.createHead( this.itemTemplate );
        this.trObjs = {};
        this.trObjs_removed = {};
        this.trObjs_new = {};
        this.trList = [];
        if( this.options.textOnly ){
            this.loadTextOnly();
        }else{
            (this.options.isEdited || this.options.isNew) ? this.loadEdit() : this.loadRead();
        }
        this.fireEvent("postLoad", [this]);

    },
    setTrTemplate: function( template ){
        if( typeOf( template ) == "string"){
            this.trTemplate = $(this.string2DOM( template )[0]);
        }else{
            this.trTemplate = $(template);
        }
        this.formatStyles( this.trTemplate );
    },
    setThTemplate : function( template ){
        if( typeOf( template ) == "string"){
            this.thTemplate = $(this.string2DOM( template )[0])
        }else{
            this.thTemplate = $(template);
        }
        this.formatStyles( this.thTemplate );
    },
    loadEdit:function(){
        if( !this.isSourceDataEmpty ){
            if( typeOf( this.data ) != "array" ){
                this.data = [ this.data ]
            }
            for( var i=0; i<this.data.length; i++ ){
                var d = this.data[i];
                var items = this.itemTemplate;
                for (var it in d ){
                    if ( items[ it ] ){
                        items[ it ].value  = d[it];
                    }
                }
                this.createTr( items, false, null, d );
            }
            for( var it in this.itemTemplate ){
                this.itemTemplate[it].value = "";
            }
        }else if( this.options.isCreateTrOnNull ){
            this.createTr( this.itemTemplate, true );
        }
    },
    loadRead:function(){
        if( !this.isSourceDataEmpty ){
            if( typeOf( this.data ) != "array" ){
                this.data = [ this.data ]
            }
            for( var i=0; i<this.data.length; i++ ){
                var d = this.data[i];
                var items = this.itemTemplate;
                for (var it in d ){
                    if ( items[ it ] ){
                        items[ it ].value  = d[it];
                    }
                }
                this.createTr( items, false, null, d );
            }
            for( var it in this.itemTemplate ){
                this.itemTemplate[it].value = "";
            }
        }else if( this.options.isCreateTrOnNull ){
            this.createTr( this.itemTemplate, true );
        }
    },
    loadTextOnly : function(){
        if( !this.isSourceDataEmpty ){
            if( typeOf( this.data ) != "array" ){
                this.data = [ this.data ]
            }
            for( var i=0; i<this.data.length; i++ ){
                var d = this.data[i];
                var items = this.itemTemplate;
                for (var it in d ){
                    if ( items[ it ] ){
                        items[ it ].value  = d[it];
                    }
                }
                //this.createTr( items, false );
                this.createTr_textOnly( items );
            }
        }else if( this.options.isCreateTrOnNull ){
            this.createTr_textOnly( this.itemTemplate );
        }
    },
    createTable : function( itemData ){
        if( this.options.containerIsTable ){
            this.table = this.container;
            var labelContainers = this.table.getElements("[lable]");
            labelContainers.each(function( el ) {
                var obj = itemData[el.get("lable")];
                if (!obj)return;
                if(obj.text)el.set("text",obj.text);
            });
            this.fireEvent( "postCreateTable", [this] );
        }else{
            var styles = {};
            if( this.options.tableClass && this.css[this.options.tableClass] )styles = this.css[this.options.tableClass];
            var tableAttr =  this.options.tableAttributes || {};
            this.table = new Element( "table", {
                styles : styles
            }).inject(this.container);
            this.table.set( tableAttr );
            this.fireEvent( "postCreateTable", [this] );
        }
    },
    createHead : function( itemData ){
        if( !this.options.isCreateTh )return;
        if( this.thTemplate ){
            this.createHead_byTemplate( itemData );
        }else{
            this.createHead_noTemplate( itemData );
        }
    },
    createHead_byTemplate : function( itemData ){
        var showNotEmptyFlag = this.options.showNotEmptyFlag;
        var isEdited = this.options.isEdited;
        var th = this.tableHead = this.thTemplate;
        var labelContainers = th.getElements("[lable]");
        labelContainers.each(function(el) {
            var lable = el.get("lable");
            var obj = itemData[lable];
            if (!obj)return;
            if(obj.text)el.set("text", obj.text);
            if( showNotEmptyFlag && itemData[lable].notEmpty && isEdited ){
                new Element( "span" , { styles : { color : "red" }, text : "*" }).inject( el )
            }
        });
        if( this.options.hasOperation && isEdited ){
            var add_button = th.getElement("[button_add]");
            if( add_button )this.createAddButton( add_button );
        }
        th.inject( this.table );
    },
    createHead_noTemplate : function( itemData ){
        var tr = this.tableHead = new Element("tr");

        var align = this.options.thAlign == "" ? {} : { align : this.options.thAlign  };
        var styles = (this.options.thClass && this.css[this.options.thClass]) ? this.css[this.options.thClass] : {};

        if( this.options.hasSequence  ){
            var th = new Element("th", { text : MWF.xApplication.Template.LP.MGrid.sequence }).inject( tr );
            th.set( align );
            th.setStyles( styles );
        }

        var idx = 1;
        for (var it in this.itemTemplate){
            var thAttr = {};
            if(this.options.thAttributes && this.options.thAttributes["_"+idx] ){
                thAttr = this.options.thAttributes["_"+idx];
            }

            var th = new Element("th").inject(tr);
            if( this.options.showNotEmptyFlag && this.itemTemplate[it].notEmpty && this.options.isEdited ){
                new Element( "span" , { styles : { color : "red", text : "*" } }).inject( th )
            }
            th.set( align );
            th.setStyles( styles );
            if(this.itemTemplate[it].text)th.set("text", this.itemTemplate[it].text );
            idx++;
        }
        if( this.options.hasOperation && this.options.isEdited ){
            var th = new Element("th", { align : "center", styles : { width : "24px", styles : {"text-align" : "center" }} } ).inject(tr);
            if( this.options.addActionTdClass && this.css[this.options.addActionTdClass] )th.setStyles(this.css[this.options.addActionTdClass]);
            this.createAddButton( th );
        }
        tr.inject( this.table );
        this.fireEvent("postCreateHead", [tr] );
    },
    createAddButton : function( container ){
        var button = new Element("div", { title : MWF.xApplication.Template.LP.MGrid.add }).inject( container );
        if( this.options.lp.add )button.set("text",this.options.lp.add );
        if( this.css.actionAdd )button.setStyles( this.css.actionAdd );
        button.addEvent("click", function( e ){
            this.fireEvent("queryAddTr");
            this.createTr( this.itemTemplate, true );
            this.fireEvent("postAddTr");
        }.bind(this));
        if( this.css.actionAdd && this.css.actionAdd_over){
            button.addEvents({
                "mouseover" : function( e ){ this.node.setStyles( this.obj.css.actionAdd_over ) }.bind({ node : button, obj : this }),
                "mouseout" : function( e ){ this.node.setStyles( this.obj.css.actionAdd ) }.bind({ node : button, obj : this })
            })
        }
        return button;
    },
    addTrs : function( count ){
        if( 100 < count )count = 100;
        for( var i=0 ; i<count; i++ ){
            this.createTr( this.itemTemplate, true );
        }
    },
    appendTr : function( d, isNew, unid, sourceData ){
        var items = this.itemTemplate;
        for (var it in d ){
            if ( items[ it ] ){
                items[ it ].value  = d[it];
            }
        }
        this.createTr( items, isNew, unid, sourceData );
        for( var it in this.itemTemplate ){
            this.itemTemplate[it].value = "";
        }
    },
    getTrCounts : function(){
        return  this.trList.length;
    },
    createTr : function( itemData, isNew, unid, sourceData ){
        if( this.options.maxTrCount  ){
            if( this.getTrCounts() < this.options.maxTrCount ){
                this._createTr( itemData, isNew, unid, sourceData )
            }else{
                if( this.app && this.app.notice ){
                    var text = MWF.xApplication.Template.LP.MGrid.addMaxLimitText.replace("{count}", this.options.maxTrCount);
                    this.app.notice(text,"error");
                }
            }
        }else{
            this._createTr( itemData, isNew, unid, sourceData )
        }
    },
    _createTr : function( itemData, isNew, unid, sourceData ){
        this.fireEvent("queryCreateTr", [this]);

        var d;
        if( isNew ){
            this.fireEvent("newData", [this, function( data ){
                d = data;
            }.bind(this) ]);
            if( d ){
                //itemData, isNew, unid, sourceData
                itemData = Object.clone( this.itemTemplate );
                for (var it in d ){
                    if ( itemData[ it ] ){
                        itemData[ it ].value  = d[it];
                    }
                }
                isNew = false;
                sourceData = d;
            }
        }

        this.trIndex ++;
        var trOptions = {
            objectId : unid ? unid : "_"+this.trIndex,
            isEdited : this.options.isEdited,
            id : "_"+this.trIndex,
            index : this.trIndex,
            indexText : (this.maxIndexText ? this.maxIndexText++ : this.trIndex),
            hasSequence : this.options.hasSequence,
            hasOperation : this.options.hasOperation,
            align : this.options.tdAlign,
            isNew : isNew,
            className : this.options.tdClass,
            tdAttributes : this.options.tdAttributes
        };
        var template = null;
        if( this.trTemplate ){
            template = this.trTemplate.clone();
        }
        var trObj = new MGridTr(this.table, trOptions, itemData, this, template, sourceData );
        trObj.load();

        this.trObjs[ trOptions.objectId ] = trObj;
        this.trList.push( trObj );

        if( isNew ){
            this.trObjs_new[ trOptions.objectId ] = trObj;
        }

        this.fireEvent("postCreateTr",[this, trObj]);
    },
    replaceTr : function( oldTrObjOr_Index, data, isNew, unid, sourceData ){
        var oldTrObj;
        if( typeof oldTrObjOr_Index == "string" ){ //如果传入的是  _index
            oldTrObj = this.trObjs[ oldTrObjOr_Index ];
        }else{
            oldTrObj = oldTrObjOr_Index;
        }
        var itemData = this.itemTemplate;
        for (var it in data ){
            if ( itemData[ it ] ){
                itemData[ it ].value  = data[it];
            }
        }
        var trIndex = oldTrObj.options.index;
        var trOptions = {
            objectId : unid ? unid : "_"+trIndex,
            isEdited : this.options.isEdited,
            id : "_"+trIndex,
            index : trIndex,
            indexText : trIndex,
            hasSequence : this.options.hasSequence,
            hasOperation : this.options.hasOperation,
            align : this.options.tdAlign,
            isNew : isNew,
            className : this.options.tdClass,
            tdAttributes : this.options.tdAttributes
        };
        var template = null;
        if( this.trTemplate ){
            template = this.trTemplate.clone();
        }
        var trObj = new MGridTr(this.table, trOptions, itemData, this, template, sourceData );
        trObj.load();

        //oldTrObj.mElement.replaceWith( trObj.mElement );
        trObj.mElement.inject( oldTrObj.mElement, "before" );

        var idx = this.trList.indexOf( oldTrObj );
        this.trList[idx] = trObj;

        this.trObjs[ trOptions.objectId ] = trObj;


        if( oldTrObj.options.isNew  ){
            this.trObjs_new[ oldTrObj.options.objectId ] = null;
        }else{
            this.trObjs_removed[ oldTrObj.options.objectId ] = oldTrObj;
        }

        if( isNew ){
            this.trObjs_new[ trOptions.objectId ] = trObj;
        }

        oldTrObj.mElement.destroy();

        for( var it in this.itemTemplate ){
            this.itemTemplate[it].value = "";
        }
    },
    createRemoveButton : function( trObj, container ){
        var button = new Element("div", { title : MWF.xApplication.Template.LP.MGrid.delete }).inject( container );
        if( this.options.lp.remove )button.set("text",this.options.lp.remove );
        if( this.css.actionRemove )button.setStyles( this.css.actionRemove );
        button.addEvents( {
            "click": function( e ){ this.removeTr( e, e.target, trObj ); }.bind(this)
        });
        if( this.css.actionRemove && this.css.actionRemove_over){
            button.addEvents({
                "mouseover" : function( e ){ this.node.setStyles( this.obj.css.actionRemove_over ) }.bind({ node : button, obj : this }),
                "mouseout" : function( e ){ this.node.setStyles( this.obj.css.actionRemove ) }.bind({ node : button, obj : this })
            })
        }
        return button
    },
    removeTr : function(e, el, trObj ){
        this.fireEvent("queryRemoveTr", [e, el, trObj]);

        var s = trObj.options;
        var id = s.objectId;

        if( ! s.isNew ){
            this.trObjs_removed[ id ] = this.trObjs[ id ];
        }

        this.trList.erase( trObj );

        this.trObjs[ id ] = null;
        //delete this.trObjs[ id ];

        if( this.trObjs_new[ id ] ){
            this.trObjs_new[ id ]= null;
            //delete this.trObjs_new[ id ];
        }

        //this.fireEvent("queryRemoveTr", [e, el.getParent("tr")]);

        trObj.destroy();

        this.adjustSequenceText();
        this.fireEvent("postRemoveTr", [e, el, trObj ]);
    },
    replaceText : function( value, selectValue, selectText ){
        var vals = typeOf( value ) == "array" ? value : value.split( this.valSeparator );
        var selectValues = typeOf( selectValue ) == "array" ? selectValue : selectValue.split( this.valSeparator );
        var selectTexts = typeOf( selectText ) == "array" ? selectText : selectText.split(this.valSeparator);
        for( var i=0 ;i<vals.length; i++ ){
            for( var j= 0; j<selectValues.length; j++){
                if( vals[i] == selectValues[j] ){
                    vals[i] = selectTexts[j]
                }
            }
        }
        return vals;
    },


    createTr_textOnly : function( itemData ){
        var self = this;
        this.trIndex ++;
        if( this.trTemplate ){
            this.createTr_textOnly_byTemplate( itemData );
        }else{
            this.createTr_textOnly_noTemplate( itemData );
        }
    },
    createTr_textOnly_byTemplate : function( itemData ){
        var tr = this.trTemplate.clone();
        tr.set( "data-id", "_"+ this.trIndex );
        var labelContainers = tr.getElements("[lable]");
        var itemContainers = tr.getElements("[item]");
        var sequenceContainers = tr.getElements( "[sequence]" );

        labelContainers.each(function( el ) {
            var obj = itemData[el.get("lable")];
            if (!obj)return;
            if(obj.text)el.set("text", obj.text);
        });

        itemContainers.each(function( el ) {
            var obj = itemData[el.get("item")];
            if (!obj)return;
            var val = obj.value ? obj.value : "";
            var valtype = typeOf( val );
            if( valtype === "string" )val = val.replace(/\n/g,"<br/>");
            if( obj.selectValue && obj.selectText ){
                var vals = this.replaceText( val, obj.selectValue, obj.selectText );
                val = vals.join(",");
            }else{
                if( valtype === "string" )val = val.replace( this.valSeparator,"," );
            }
            el.set("html", val );
        }.bind(this));

        sequenceContainers.set("text", this.trIndex );

        tr.inject( this.table );
    },
    createTr_textOnly_noTemplate : function( itemData ){
        var tr = new Element("tr" , { "data-id" : "_"+this.trIndex });
        if( this.options.hasSequence  ){
            var td = new Element("td", { align : "center", text : this.trIndex }).inject( tr );
            if( this.options.sequenceClass && this.css[this.options.sequenceClass] )td.setStyles(this.css[this.options.sequenceClass]);
        }

        var attr = {};
        if( this.options.tdAlign )attr.align = this.options.tdAlign;

        var idx = 1;
        for (var it in itemData ){
            var tdAttributes = {};
            if(this.options.tdAttributes && this.options.tdAttributes["_"+idx] ){
                tdAttributes = this.options.tdAttributes["_"+idx];
            }
            var obj = itemData[it];
            var val = obj.value || "";
            var valtype = typeOf( val );
            if( valtype === "string" )val = val.replace(/\n/g,"<br/>");
            if( obj.selectValue && obj.selectText ){
                var vals = this.replaceText( val, obj.selectValue, obj.selectText );
                val = vals.join(",");
            }else{
                if( valtype === "string" )val = val.replace( this.valSeparator,"," )
            }
            var td = new Element("td", tdAttributes).inject( tr );
            td.set( "text", val );
            if( this.options.tdAlign )td.set( "align" , this.options.tdAlign);
            if( this.options.tdClass && this.css[this.options.tdClass] )td.setStyles(this.css[this.options.tdClass]);
            idx ++;
        }
        tr.inject( this.table );

    },

    getResult : function( verify, separator, isAlert, onlyModified, keepAllData ){
        var result = [];
        var trObjs = this.trObjs;
        var flag = true;
        for( var tr in trObjs ){
            if( tr && trObjs[tr] ){
                var data = trObjs[tr].getResult( verify, separator, isAlert, onlyModified , keepAllData );
                if( !data ){
                    if( this.options.verifyType == "batch" ){
                        flag= false;
                    }else{
                        return false;
                    }
                }else{
                    result.push( data )
                }
            }
        }
        if( flag ){
            return result;
        }else{
            return false ;
        }
    },
    enableItem : function( itemName ){
        for( var tr in this.trObjs ){
            if( tr && this.trObjs[tr] ){
                this.trObjs[tr].enableItem( itemName );
            }
        }
    },
    disableItem : function( itemName ){
        for( var tr in this.trObjs  ){
            if( tr && this.trObjs[tr] ){
                this.trObjs[tr].disableItem( itemName );
            }
        }
    },
    adjustSequenceText : function(){
        var array = [];
        for( var tr in this.trObjs  ){
            if( tr && this.trObjs[tr] ){
                array.push( this.trObjs[tr] )
            }
        }
        array.sort( function( a, b ){
            return a.options.index - b.options.index
        });
        array.each( function( o, index ){
            o.setSequenceText(index + 1 );
            this.maxIndexText = index + 2;
        }.bind(this))
    },
    string2DOM: function( str, container, callback ){
        var wrapper =	str.test('^<the|^<tf|^<tb|^<colg|^<ca') && ['<table>', '</table>', 1] ||
            str.test('^<col') && ['<table><colgroup>', '</colgroup><tbody></tbody></table>',2] ||
            str.test('^<tr') && ['<table><tbody>', '</tbody></table>', 2] ||
            str.test('^<th|^<td') && ['<table><tbody><tr>', '</tr></tbody></table>', 3] ||
            str.test('^<li') && ['<ul>', '</ul>', 1] ||
            str.test('^<dt|^<dd') && ['<dl>', '</dl>', 1] ||
            str.test('^<le') && ['<fieldset>', '</fieldset>', 1] ||
            str.test('^<opt') && ['<select multiple="multiple">', '</select>', 1] ||
            ['', '', 0];
        if( container ){
            var el = new Element('div', {html: wrapper[0] + str + wrapper[1]}).getChildren();
            while(wrapper[2]--) el = el[0].getChildren();
            el.inject( container );
            if( callback )callback( container );
            return el;
        }else{
            var div = new Element('div', {html: wrapper[0] + str + wrapper[1]});
            div.setStyle("display","none").inject( $(document.body) );
            if( callback )callback( div );
            var el = div.getChildren();
            while(wrapper[2]--) el = el[0].getChildren();
            div.dispose();
            return el;
        }
    },
    formatStyles: function( container ){
        container.getElements("[styles]").each(function(el){
            var styles = el.get("styles");
            if( styles && this.css[styles] ){
                el.setStyles( this.css[styles] )
            }
        }.bind(this));
        container.getElements("[class]").each(function(el){
            var className = el.get("class");
            if( className && this.css[className] ){
                el.setStyles( this.css[className] )
            }
        }.bind(this))
    },
});

var MGridTr = new Class({
    Implements: [Options, Events],
    options: {
        objectId : "",
        isEdited : true,
        id : "",
        index : 0,
        indexText : "0",
        hasSequence  : true,
        hasOperation : true,
        align : "left",
        isNew : true,
        className : "",
        tdAttributes : null
    },
    initialize: function (container,options,itemData, parent, template, sourceData) {

        this.setOptions(options);

        this.container = container;
        this.mElement = null;
        this.items = {};
        this.itemData = itemData;
        this.parent = parent;
        this.template = template;
        this.sourceData = sourceData;
        this.css = this.parent.css || {};
        this.app = this.parent.app;
    },
    load:function(){
        //if( this.options.isEdited ){
        //    this.create_Edit();
        //}else{
        //    this.create_Read();
        //}
        this.create();
    },
    reload : function( data ){
        if( !data )data = this.getResult(false, null, false, false, true );
        this.parent.replaceTr( this, data, this.options.isNew, this.options.objectId, this.sourceData )
    },
    //create_Read : function(){
    //    var tr = this.mElement = new Element("tr", { "data-id" : this.options.id }).inject(this.container);
    //
    //    var attr = {};
    //    if( this.options.align )attr.align = this.options.align;
    //
    //    var styles = {};
    //    if( this.options.className && this.css[this.options.className] )styles = this.css[this.options.className];
    //
    //    if( this.options.hasSequence  ){
    //        var td = this.sequenceTd = new Element("td", { align : "center", text : ( this.options.indexText || this.options.index) }).inject( tr );
    //        if( this.parent.options.sequenceClass && this.css[this.parent.options.sequenceClass] )td.setStyles(this.css[this.parent.options.sequenceClass]);
    //    }
    //
    //    var idx = 1;
    //    for (var it in this.itemData ){
    //        var tdAttr = this.options.tdAttributes && this.options.tdAttributes["_"+idx] ? this.options.tdAttributes["_"+idx] : {};
    //        var td = new Element("td", { "text" : this.itemData[it].value }).inject( tr );
    //        td.set( attr );
    //        td.set( tdAttr );
    //        td.setStyle( styles );
    //        idx++;
    //    }
    //},
    create : function(e, el){
        if( this.template ){
            this.create_byTemplate( e, el );
        }else{
            this.create_noTemplate( e, el );
        }
    },
    setSequenceText : function( text ){
        if(this.sequenceTd)this.sequenceTd.set("text",text);
    },
    create_byTemplate : function(){
        this.mElement = this.template;
        this.mElement.set("data-id", this.options.id );

        if( this.options.hasSequence  ){
            this.sequenceTd = this.mElement.getElement( "[sequence]" );
            if(this.sequenceTd)this.sequenceTd.set( "text" , ( this.options.indexText || this.options.index) );
        }

        this.mElement.getElements("[lable]").each(function( el ) {
            var itData = this.itemData[el.get("lable")];
            if (!itData)return;
            if(itData.text)el.set("text", itData.text);
        }.bind(this));

        this.mElement.getElements("[item]").each(function( el ) {
            var itData = this.itemData[el.get("item")];
            if (!itData)return;
            this.createItem( el, itData );
        }.bind(this));

        if( this.options.hasOperation && this.options.isEdited ){
            if( this.parent.options.minTrCount && this.options.index > this.parent.options.minTrCount ){
                var removeContainer = this.mElement.getElement("[button_remove]");
                this.parent.createRemoveButton( this, removeContainer );
            }
        }
        this.mElement.setStyle("display","");
        this.mElement.inject( this.container );
    },
    create_noTemplate : function(){
        this.mElement = new Element("tr", { "data-id" : this.options.id });

        var attr = {};
        if( this.options.align )attr.align = this.options.align;
        var styles = {};
        if( this.options.className && this.css[this.options.className] )styles = this.css[this.options.className];


        if( this.options.hasSequence  ){
            var td = this.sequenceTd = new Element("td", { align : "center", text : ( this.options.indexText || this.options.index) }).inject( this.mElement );
            if( this.parent.options.sequenceClass && this.css[this.parent.options.sequenceClass] )td.setStyles(this.css[this.parent.options.sequenceClass]);
        }

        var idx = 1;
        for (var it in this.itemData){
            var tdAttr = this.options.tdAttributes && this.options.tdAttributes["_"+idx] ? this.options.tdAttributes["_"+idx] : {};
            var td = new Element("td").inject( this.mElement );
            td.set(attr);
            td.set( tdAttr );
            td.setStyles( styles );
            var itData = this.itemData[it];
            this.createItem( td, itData );
            idx++;
        }
        if( this.options.hasOperation && this.options.isEdited ){
            if( this.parent.options.minTrCount && this.options.index > this.parent.options.minTrCount ) {
                var t = new Element("td", {align: "center", style: {width: "30px"}}).inject(this.mElement);
                var className = this.parent.options.removeActionTdClass;
                if( className && this.css[className] )t.setStyles(this.css[className]);
                this.parent.createRemoveButton(this, t);
            }else{
                var t = new Element("td").inject(this.mElement);
                var className = this.parent.options.removeActionTdClass;
                if( className && this.css[className] )t.setStyles(this.css[className]);
            }
        }
        this.mElement.inject( this.container );
    },
    createItem : function( container, itData ) {
        //if( itData.disable )return;
        itData.isEdited = this.options.isEdited;
        itData.objectId = itData.name;
        var item = new MDomItem(container, itData, this, this.app, this.css);
        if (this.options.isEdited){
            if (this.parent.options.verifyType == "batchSingle") {
                item.options.warningType = "single";
            } else {
                item.options.warningType = this.parent.options.verifyType;
            }
        }
        item.options.name = itData.name + "_" + this.options.index;
        item.index = this.options.index;
        item.parent = this;
        item.load();

        this.items[itData.objectId] = item;
    },
    //remove : function(){
    //    this.mElement.destroy();
    //},
    destroy : function(){
        Object.each(this.items, function(item){
            item.destroy();
        }.bind(this));
        this.mElement.destroy();
        MWF.release(this);
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
    getPrevSilbing : function(){
        var idx = this.parent.trList.indexOf( this );
        if( idx > 0 ){
            return this.parent.trList[ idx -1 ];
        }else{
            return null;
        }
    },
    getNextSilbing : function(){
        var idx = this.parent.trList.indexOf( this );
        if( idx!=-1 && idx < this.parent.trList.length - 1 ){
            return this.parent.trList[ idx + 1 ];
        }else{
            return null;
        }
    },
    verify : function(isShowWarming) {
        var flag = true;
        for (var it in this.items ) {
            if (!this.items[it].verify(isShowWarming)) {
                if (this.parent.options.verifyType == "batch" || this.parent.options.verifyType == "batchSingle") {
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
                    key_value[item.options.objectId] = (typeOf(separator) == "string" ? value.join(separator) : value );
                } else {
                    key_value[item.options.objectId] = value;
                }
            }
        }
        return key_value;
    },

    getResult : function(verify, separator, isShowWarming, onlyModified, keepAllData ) {
        if ( !verify || this.verify(isShowWarming)) {
            if( keepAllData && this.sourceData ){
                var result = this.sourceData;
                var map = this.getItemsKeyValue(separator, onlyModified);
                for( var key in map ){
                    result[key] = map[key];
                }
                return result;
            }else{
                return this.getItemsKeyValue(separator, onlyModified);
            }

        } else {
            return false;
        }
    }
});
