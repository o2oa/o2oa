/*
 DOMElement管理类
 外部方法：
 get( textOrValue ) 参数"text"或"value" 获取文本或值
 getValue() 获取值
 getText() 获取文本
 getModifiedValue() 获取修改过的值，如果没有修改过返回null
 getModifiedText() 获取修改过的文本，如果没有修改过返回null
 setValue() 设置对象的值
 resetType( type ) 修改类型
 resetItemOptions( selectValue[, selectText ]) 修改可选项，selectValue可项值,selectText可选文本
 dispose() 清空对象
 verify( isShowWarning ) 参数isShowWarning,校验不通过的是否提示用户。根据isShowWarning参数和options的notEmpty、warningType、validRule属性校验对象，返回boolean
 */
var MDomItem = new Class({
    Implements: [Options, Events],
    options : {
        existItem : null, //已经存在的对象
        existName : null,	//已经存在的字段，按 name 取
        existId : null,	//已经存在的字段，按 id 取
        /*
         attachment : [
         { name : aaa.doc , size : 333 , id : xxxx },
         { name : bbb.doc , size : 444 , id : xxxx },
         ]
         */
        objectId : "",
        name : "",		//生成的对象的name属性

        value : "",		//对象的值
        text : "",		//对应的中文名称
        type : "text",  //可以为 text,innertext, radio,checkbox,select,multiselect,img,button,hidden,rtf

        isEdited : true,

        tType : "", //type 为text时候有效，可以为 number,date,time, datetime,person,company,department, identity,
        count : 1, //如果是多选，多选的上限值，0表示无限制，默认为1,
        departments : [], //tType 为 identity、person、department时的，部门选择范围,
        companys : [], //tType 为 identity、person、department时的，公司选择范围


        //可以传入json 如 ：  { change : function(){alert("change " + this.name );}, click : function(){alert("click " + this.name)  } }
        //或 字符串  ：                  "{ change : function(){alert('change ' + this.name );}, click : function(){alert('click ' + this.name)  } }"
        //或字符串：                     "change^^function(){alert('change ' + this.name );##click^^function(obj){alert('click ' + this.name);}"
        event : null,	//需要绑定的事件

        selectValue : "",	//选择性控件的可选值
        selectText : "",	//选择型控件的可选文本
        defaultValue : "",	//默认值
        className : "",		//类
        style : {},			//样式
        attr : {},	//其他参数， 比如 " {readonly : true, size : '20' }"

        notEmpty : false, //是否允许为空，默认允许
        defaultValueAsEmpty: false, //检查空值时，默认值是否为空
        emptyTip : null, //为空时的提示，可以不设置

        //alert 或者 batch 或者 single，单个提醒或者批量提醒
        warningType : "batch",

        //可以传入校验类型或自定义方法，如
        //{email : true , url : true, date : true, dateISO : true, number : true, digits : true,
        // maxlength:5, minlength:10, rangelength:[5,10], max:5, min:10 ,range:[5,10], extension: ["xls","xlsx"],fun : function(){}
        // }
        validRule : null,

        //validMessage和validRule的对应，出错是提示的信息，如 {email : "请输入正确格式的电子邮件", fun : "请输入正确的密码"}，如果不设置，默认如下：
        // {  email: "请输入正确格式的电子邮件",
        // url: "请输入合法的网址",
        // date: "请输入合法的日期",
        // dateISO: "请输入合法的日期 (ISO).",
        // number: "请输入合法的数字",
        // digits: "只能输入整数",
        // maxlength: this.format("请输入一个 长度最多是 {0} 的字符串"),
        // minlength: this.format("请输入一个 长度最少是 {0} 的字符串"),
        // rangelength: this.format("请输入 一个长度介于 {0} 和 {1} 之间的字符串"),
        // range: this.format("请输入一个介于 {0} 和 {1} 之间的值"),
        // max: this.format("请输入一个最大为{0} 的值"),
        // min: this.format("请输入一个最小为{0} 的值"),
        // fun : "请输入正确的"+ this.options.text
        // }
        validMessage : null,

        RTFConfig : null //CKEditor 的设置项
    },
    initialize: function (container, options , form, app, css ) {
        this.form = form
        this.app = app;
        this.container = $(container);
        this.css = css;

        for(var o in options ){	//允许使用 function 来计算设置, on开头的属性被留作 fireEvent
            if( o != "validRule" && o!="validMessage" && o.substr(0,2)!="on" && typeOf( options[o] )== "function" ){
                options[o] = options[o].call();
            }
        }
        this.setOptions(options);

        this.unsetClassType = ["radio","checkbox","select","multiselect","img","button","hidden"];
        this.valSeparator = /,|;|\^\^|\|/; //如果是多值对象，作为用户选择的多个值的分隔符
        //this.parent = parent;
        this.mElement = null;	//容器
        this.items = [];
    },
    load: function () {
        this.fireEvent("queryLoad");

        if( this.options.existItem || this.options.existName || this.options.existId ){
            this.processExist();
        }else{
            this.createElement();
        }

        this.fireEvent("postLoad");
    },
    processExist: function(){
        var self = this;
        var items , type;
        if( this.options.existItem ){
            items = $$( this.options.existItem );
        }else if( this.options.existName ){
            items = $$( "[name='"+this.options.existName+"']" );
        }else if( this.options.existId ){
            items = $$( "#"+this.options.existId );
        }
        if( !this.container ){
            this.mElement = this.container = items[0].getParent();
        }
        this.options.type = this.options.type || this.getType( item[0] );
        this.options.name = this.options.name || item[0].get("name");
        this.options.value = this.options.value || this.getValue( this.options.name );
        items.each(function(item){
            if( this.options.className ){
                item.setStyles( this.options.className );
            }
            if( this.options.style && typeof this.options.style == "object"){
                item.setStyles( this.options.style );
            }
            var event = this.options.event;
            this.bindEvent(item, event, type);
        }.bind(this))
        this.items = items;
    },
    createElement:function(){
        var _self = this;
        var item;
        var values;
        var type = this.options.type;
        if( !type || type=="" )type="text";
        var tType = this.options.tType;
        var name = this.options.name;
        var value ;
        if( typeOf( this.options.value ) === "boolean" ){
            value = this.options.value.toString();
        }else{
            value = this.options.value || this.options.defaultValue
        }
        var selectValue = this.options.selectValue || this.options.selectText;
        var selectText = this.options.selectText || this.options.selectValue ;
        var event = this.options.event;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var isEdited = this.options.isEdited;
        var parent = this.mElement = this.container ;
        var className = null ;
        if( this.options.className == "none" ){
        }else if( this.options.className != "") {
            className = this.options.className
        }else if( !isEdited ){
        }else{
            switch( type.toLowerCase() ){
                case "text" :
                    if( !tType ){
                        className = "inputText";
                    }else if( tType == "number" ){
                        className = "inputText";
                    }else if( tType == "time" || tType == "date" || tType.toLowerCase() == "datetime" ){
                        className = "inputTime";
                    }else if( tType == "person" || tType == "department"  || tType == "company" || tType == "identity"){
                        className = "inputPerson";
                    }else{
                        className = "inputText";
                    }
                    break;
                case "number" : className = "inputText";
                    break;
                case "password" : className = "inputPassword";
                    break;
                case "radio" : className = "inputRadio";
                    break
                case "checkbox" : className = "inputCheckbox";
                    break
                case "select" : className = "inputSelect";
                    break;
                case "multiselect" : className = "inputMultiselect";
                    break;
                case "img" :
                    break;
                case "textarea" : className = "inputTextarea";
                    break;
                case "button": className = "inputButton";
                    break;
                case "hidden":
                    break;
                case "innertext":
                    break;
                case "rtf":
                    break;
                case "file": className = "inputFile";
                    break;
                case "attachment": className = "inputFile";
                    break;
                default : className = "inputText";
            }
        }
        if( isEdited ){
            switch( type.toLowerCase() ){
                case "text" :
                    item = new Element( "input", {
                        "type" : "text",
                        "name" : name,
                        "value" : value
                    });
                    item.set( attr );
                    if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
                    item.setStyles( styles );

                    this.bindEvent(item,event,type,tType);
                    if(parent)item.inject(parent);
                    this.items.push( item );
                    break;
                case "password" :
                    item = new Element( "input", {
                        "type" : "password",
                        "name" : name,
                        "value" : value
                    });
                    item.set( attr );
                    if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
                    item.setStyles( styles );

                    this.bindEvent(item,event,type,tType);
                    if(parent)item.inject(parent);
                    this.items.push( item );
                    break;
                case "radio" :
                    selectValues = typeOf( selectValue ) == "array" ? selectValue : selectValue.split( this.valSeparator );
                    selectTexts =  typeOf( selectText ) == "array" ? selectText : selectText.split(this.valSeparator);
                    for( i=0;i<selectValues.length;i++){

                        item = new Element( "div");
                        if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
                        item.setStyles( styles );

                        var input = new Element( "input", {
                            "type" : "radio",
                            "name" : name,
                            "value" : selectValues[i],
                            "checked" : selectValues[i] == value
                        }).inject( item );
                        input.set( attr );

                        var textNode = new Element( "span", {
                            "text" : selectTexts[i]
                        }).inject(item);
                        textNode.addEvent("click", function(){
                            this.input.checked = ! this.input.checked;
                        }.bind( {input : input} ) )

                        this.bindEvent(item,event,type);
                        if(parent)item.inject(parent);
                        this.items.push( item );
                    }
                    break;
                case "checkbox" :
                    values = typeOf( value ) == "string" ? value.split(this.valSeparator) : value ;
                    values = typeOf( value ) == "array" ? value : [value];
                    selectValues = typeOf( selectValue ) == "array" ? selectValue : selectValue.split( this.valSeparator );
                    selectTexts =  typeOf( selectText ) == "array" ? selectText : selectText.split(this.valSeparator);
                    for( i=0;i<selectValues.length;i++){

                        item = new Element( "div");
                        if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
                        item.setStyles( styles );

                        var input = new Element( "input", {
                            "type" : "checkbox",
                            "name" : name,
                            "value" : selectValues[i],
                            "checked" : values.contains( selectValues[i] )
                        }).inject( item );
                        input.set( attr );

                        var textNode = new Element( "span", {
                            "text" : selectTexts[i]
                        }).inject(item)
                        textNode.addEvent("click", function(){
                            this.input.checked = ! this.input.checked;
                        }.bind( {input : input} ) )

                        this.bindEvent(item,event,type);
                        if(parent)item.inject(parent);
                        this.items.push( item );
                    }
                    break;
                case "select" :
                    item = new Element( "select" , {
                        "name" : name
                    });
                    item.set( attr );
                    if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
                    item.setStyles( styles );

                    selectValues = typeOf( selectValue ) == "array" ? selectValue : selectValue.split( this.valSeparator );
                    selectTexts =  typeOf( selectText ) == "array" ? selectText : selectText.split(this.valSeparator);

                    for( i=0;i<selectValues.length;i++){
                        new Element("option" , {
                            "value" : selectValues[i],
                            "selected" : selectValues[i] == value,
                            "text" : selectTexts[i]
                        }).inject(item)
                    }

                    this.bindEvent(item,event,type);
                    if(parent)item.inject(parent);
                    this.items.push( item );
                    break;
                case "multiselect" :
                    values = typeOf( value ) == "string" ? value.split(this.valSeparator) : value ;
                    values = typeOf( value ) == "array" ? value : [value];

                    item = new Element( "select" , {
                        "name" : name,
                        "multiple" : true
                    });
                    item.set( attr );
                    if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
                    item.setStyles( styles );

                    selectValues = typeOf( selectValue ) == "array" ? selectValue : selectValue.split( this.valSeparator );
                    selectTexts =  typeOf( selectText ) == "array" ? selectText : selectText.split(this.valSeparator);
                    for( i=0;i<selectValues.length;i++){
                        new Element("option" , {
                            "value" : selectValues[i],
                            "selected" : values.contains( selectValues[i] ),
                            "text" : selectTexts[i]
                        }).inject(item)
                    }
                    this.bindEvent(item,event,type);
                    if(parent)item.inject(parent);
                    this.items.push( item );
                    break;
                case "img" :
                    item = new Element( "img", {
                        "name" : name,
                        "src" : value
                    })
                    item.set( attr );
                    if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
                    item.setStyles( styles );

                    this.bindEvent(item,event,type);
                    if(parent)item.inject(parent);
                    this.items.push( item );
                    break;
                case "textarea" :
                    item = new Element( "textarea", {
                        "name" : name,
                        "value" : value
                    })
                    item.set( attr );
                    if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
                    item.setStyles( styles );

                    this.bindEvent(item,event,type);
                    if(parent)item.inject(parent);
                    this.items.push( item );
                    break;
                case "button":
                    item = new Element( "button", {
                        "name" : name,
                        "value" : value,
                        "text" : value
                    })
                    item.set( attr );
                    if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
                    item.setStyles( styles );
                    this.bindEvent(item,event,type);
                    if(parent)item.inject(parent);
                    this.items.push( item );
                    break;
                case "hidden":
                    item = new Element( "input", {
                        "type" : "hidden",
                        "name" : name,
                        "value" : value,
                        "styles" : styles
                    })
                    item.set( attr );
                    //this.bindEvent(item,event,type);
                    if(parent)item.inject(parent);
                    this.items.push( item );
                    break;
                case "innertext":
                    if( this.options.selectValue && this.options.selectText ){
                        value = this.replaceText( value, this.options.selectValue , this.options.selectText  );
                        value = value.join(",");
                    }
                    item = new Element( "span", {
                        "name" : name,
                        "text" : value
                    })
                    item.set( attr );
                    if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
                    item.setStyles( styles );
                    this.bindEvent(item,event,type);
                    if(parent)item.inject(parent);
                    this.items.push( item );
                    break;
                case "rtf":
                    COMMON.AjaxModule.load("ckeditor", function(){
                        var item = new Element("div",{
                            "name" : name,
                            "id" : name
                        });
                        item.set( attr );
                        if(parent)item.inject(parent);
                        if( value )item.set("html", value);

                        var editorConfig = {
                            //"autoGrow_maxHeight": 400,
                            //"autoGrow_minHeight": 300,
                            "resize_enabled": true,
                            //"resize_maxHeight": "3000",
                            //"resize_minHeight": "200",
                            "autoParagraph": true,
                            "autoUpdateElement": true,
                            "enterMode": 1,
                            //"height": "200",
                            //"width": "",
                            "readOnly": false
                        };
                        if( this.options.RTFConfig ){
                            editorConfig = Object.merge( editorConfig, this.options.RTFConfig )
                        }
                        if( !editorConfig.filebrowserFilesImage && !editorConfig.cloudFileDisable ){
                            editorConfig.filebrowserFilesImage = function( e, callback ){
                                MWF.xDesktop.requireApp("File", "FileSelector", function(){
                                    _self.selector_cloud = new MWF.xApplication.File.FileSelector( document.body ,{
                                        "style" : "default",
                                        "title": "选择云文件图片",
                                        "toBase64" : true,
                                        "listStyle": "preview",
                                        "selectType" : "images",
                                        "onPostSelectAttachment" : function(url, base64File){
                                            if(callback)callback(url, base64File);
                                        }
                                    });
                                    _self.selector_cloud.load();
                                }, true);
                            }
                        }
                        this.editor = CKEDITOR.replace(item, editorConfig);

                        this.items.push( this.editor );
                    }.bind(this));
                    break;
                case "file" :
                    item = new Element( "input", {
                        "type" : "file",
                        "name" : name,
                        "id" : name,
                        "value" : value
                    })
                    item.set( attr );
                    if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
                    item.setStyles( styles );
                    this.bindEvent(item,event,type);
                    if(parent)item.inject(parent);
                    this.items.push( item );
                    break;
                default :
                    item = new Element( "input", {
                        "type" : "text",
                        "name" : name,
                        "value" : value
                    })
                    item.set( attr );
                    if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
                    item.setStyles( styles );
                    this.bindEvent(item,event,type);
                    if(parent)item.inject(parent);
                    this.items.push( item );
            }

        }else{
            switch( type.toLowerCase() ){
                case "radio" :
                case "checkbox" :
                case "select" :
                case "multiselect" :
                case "innertext":
                    if( this.options.selectValue && this.options.selectText ){
                        value = this.replaceText( value, this.options.selectValue , this.options.selectText  );
                        value = value.join(",");
                    }
                    item = new Element( "span", {
                        "name" : name,
                        "text" : value
                    })
                    item.set( attr );
                    if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
                    item.setStyles( styles );

                    if(parent)item.inject(parent);
                    this.items.push( item );
                    break;
                case "img" :
                    item = new Element( "img", {
                        "name" : name,
                        "src" : value
                    })
                    item.set( attr );
                    if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
                    item.setStyles( styles );

                    this.bindEvent(item,event,type);
                    if(parent)item.inject(parent);
                    this.items.push( item );
                    break;
                case "button":
                    item = new Element( "button", {
                        "name" : name,
                        "value" : value,
                        "text" : value
                    })
                    item.set( attr );
                    if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
                    item.setStyles( styles );
                    this.bindEvent(item,event,type);
                    if(parent)item.inject(parent);
                    this.items.push( item );
                    break;
                case "hidden":
                    item = new Element( "input", {
                        "type" : "hidden",
                        "name" : name,
                        "value" : value,
                        "styles" : styles
                    })
                    item.set( attr );
                    //this.bindEvent(item,event,type);
                    if(parent)item.inject(parent);
                    this.items.push( item );
                    break;
                case "rtf":
                    item = new Element( "span", {
                        "name" : name,
                        "html" : value
                    })
                    item.set( attr );
                    if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
                    item.setStyles( styles );

                    if(parent)item.inject(parent);
                    this.items.push( item );
                    break;
                case "file" :
                    item = new Element( "input", {
                        "type" : "file",
                        "name" : name,
                        "id" : name,
                        "value" : value
                    });
                    item.set( attr );
                    if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
                    item.setStyles( styles );
                    this.bindEvent(item,event,type);
                    if(parent)item.inject(parent);
                    this.items.push( item );
                    break;
                case "text" :
                case "textarea" :
                default :
                    item = new Element( "span", {
                        "name" : name,
                        "text" : value
                    });
                    item.set( attr );
                    if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
                    item.setStyles( styles );

                    if(parent)item.inject(parent);
                    this.items.push( item );
                    break;
            }
        }
        //this.mElement = parent;
        // this.mElement.inject( this.container );
        return parent;
    },
    replaceText : function( value, selectValue, selectText ){
        var vals = typeOf( value ) == "array" ? value : value.split(this.valSeparator);
        var selectValues = typeOf( selectValue ) == "array" ? selectValue : selectValue.split( this.valSeparator );
        var selectTexts =  typeOf( selectText ) == "array" ? selectText : selectText.split(this.valSeparator);
        for( var i=0 ;i<vals.length; i++ ){
            for( var j= 0; j<selectValues.length; j++){
                if( vals[i] == selectValues[j] ){
                    vals[i] = selectTexts[j]
                }
            }
        }
        return vals;
    },
    bindEvent: function(item,events,type,tType){

        if( tType ) this.bindTTypeEvent( item,tType );

        if( !events || events == "" || events == "$none" )return;
        var _self = this;
        if( typeof events == "string" ){
            if( events.indexOf("^^") > -1 ){
                var eventsArr = events.split("##");
                if( eventsArr[0].split("^^").length != 2 )return;
                events = {};
                for(var i=0;i<eventsArr.length;i++){
                    var ename = eventsArr[i].split("^^")[0];
                    var efunction = eventsArr[i].split("^^")[1];
                    events[ ename ] = eval( "(function(){ return "+ efunction +" })()" )  //字符串变对象或function，方法1
                }
            }else{
                //字符串变对象或function，方法2
                eval( "var events = " + events );
            }
        }
        if( typeOf(events) == "object" ){

            for( var e in events ){
                //jquery的写法
                //item.bind( e, { fun : events[e] }, function( event ){
                //this 是触发事件的对象，self是当前jDomItem对象
                //event.data.fun.call( this, _self );
                //})

                //方法固定，把参数作为this指正传给方法，需要在方法体里通过this获取参数
                //item.addEvent( e, events[e].bind({"item": item, "_self":_self}));

                //参数固定，把方法传入到function中，可以在回调方法中直接获取，和jquery的写法一样
                item.addEvent( e, function(ev){
                    this.fun.call( ev ? ev.target : null, _self, ev );
                }.bind({fun : events[e]}));

                //不一定行
                //item.addEvent( e, (function(){
                //	return function(){
                //		events[e].call(item,_self);
                //	}
                //})(e));
            }

            //            for( var e in events ){
            //                if( type && (e=="dblclick" || e=="click") ){
            //                    if( jQuery.inArray( type , this.unsetClassType) == -1 ){
            //                        if( !item.attr("title") || item.attr("title") == "" ){
            //                            item.attr( "title", e=="dblclick" ? "双击选择"+this.options.text : "单击选择"+this.options.text );
            //                        }
            //                        item.removeClass("inputtext").addClass("inputclick");
            //                        break;
            //                    }
            //                }
            //            }
        }
    },
    bindTTypeEvent : function( item, tType ){
        if( tType == "number" ){
            item.addEvent( "keyup" , function(){
                this.value=this.value.replace(/[^\d.]/g,'')
            } );
        }else if( tType == "time" || tType.toLowerCase() == "datetime" || tType == "date" ){
            item.addEvent( "click" , function(){
                this.selectCalendar( item, tType )
            }.bind(this) );
        }else if( tType == "person" || tType == "department" || tType == "company" || tType == "identity" ){
            item.addEvent( "click" , function(){
                this.selectPerson( item, tType )
            }.bind(this) );
        }
    },
    selectCalendar : function( item, type ){
        this.fireEvent("querySelect", this);
        MWF.require("MWF.widget.Calendar", function(){
            var calendar = new MWF.widget.Calendar( item, {
                "style" : "xform",
                "isTime":  type == "time" || type.toLowerCase() == "datetime",
                "timeOnly": type == "time",
                "target": this.app.content
            });
            calendar.show();
        }.bind(this));
    },
    selectPerson: function( item, type ){
        MWF.xDesktop.requireApp("Organization", "Selector.package",null, false);
        this.fireEvent("querySelect", this);
        var value = item.get("value").split( this.valSeparator );
        var options = {
            "type": type,
            "title": this.options.text,
            "count" : this.options.count,
            "names": value || [],
            "departments" : this.options.departments,
            "companys" : this.options.companys,
            "onComplete": function(items){
                var arr = [];
                items.each(function(item){
                    arr.push(item.data.name);
                }.bind(this));
                item.set("value",arr.join(","));
                this.items[0].fireEvent("change");
            }.bind(this)
        };
        var selector = new MWF.OrgSelector(this.app.content, options);
    },
    getType : function( item ){
        var tag = item.tagName.toLowerCase();
        if( tag == "input" ){
            type = item.get("type") ? item.get("type").toLowerCase() : "text" ;
        }else if( tag == "select" ){
            if( item.get("multiple") ){
                type = "multiselect";
            }else if( item.get("type") && item.get("type").toLowerCase() == "select-multiple" ){
                type = "multiselect";
            }else{
                type = "select";
            }
        }else{
            type = tag;
        }
    },
    get: function( vort , name ){	//value 和 text
        var value;
        var text;
        var type;
        var items;
        if( !name ){
            name = this.options.name;
            items = this.mElement.getElements("[name='"+name+"']");
            type = this.options.type;
        }else{
            items = $$( "[name='"+name+"']" );
            type = this.getType( items[0] );
        }
        if( this.options.isEdited ){
            switch( type.toLowerCase() ){
                case "text":
                    value = items[0].get("value");
                    break;
                case "password":
                    value = items[0].get("value");
                    break;
                case "hidden":
                    value = items[0].get("value");
                    break;
                case "innertext":
                    text = items[0].get("text");
                    if( this.options.selectValue && this.options.selectText ){
                        value = this.replaceText( text, this.options.selectText , this.options.selectValue  );
                    }else{
                        value = text;
                    }
                    break;
                case "radio":
                    items.each(function( el ){
                        if( el.checked ){
                            value = el.get("value");
                            text = el.getParent().get("text").trim();
                        }
                    });
                    break;
                case "checkbox":
                    value = [];
                    text = [];
                    items.each(function( el ){
                        if( el.checked ){
                            value.push(el.get("value"));
                            text.push( el.getParent().get("text").trim() )
                        }
                    });
                    break;
                case "textarea":
                    value = items[0].get("value");
                    break;
                case "select":
                    items[0].getElements("option").each(function(el){
                        if( el.selected ){
                            value = el.get("value");
                            text = el.get("text").trim();
                        }
                    });
                    break;
                case "multiselect":
                    value = [];
                    text = [];
                    items[0].getElements("option").each(function(el){
                        if( el.selected ){
                            value.push( el.get("value") );
                            text.push( el.get("text").trim() );
                        }
                    });
                    break;
                case "rtf":
                    if( this.options.RTFConfig && this.options.RTFConfig.isSetImageMaxWidth ){
                        var div = new Element( "div" , {
                            "styles" : { "display" : "none" },
                            "html" : this.editor.getData()
                        } ).inject( this.container );
                        div.getElements( "img").each( function( el ){
                            el.setStyle( "max-width" , "100%" );
                        });
                        value = div.get("html");
                        div.destroy();
                    }else{
                        value = this.editor.getData();
                    }
                    break;
                case "file":
                    value = items[0].get("value");
                    break;
                default:
                    value = items[0].get("value");
            }
        }else{
            switch( type.toLowerCase() ){
                case "text":
                case "textarea":
                    value = items[0].get("text");
                    break;
                case "hidden":
                    value = items[0].get("value");
                    break;
                case "innertext":
                case "radio":
                case "checkbox":
                case "select":
                case "multiselect":
                    text = items[0].get("text");
                    if( this.options.selectValue && this.options.selectText ){
                        value = this.replaceText( text, this.options.selectText , this.options.selectValue  );
                    }else{
                        value = text;
                    }
                    break;
                case "rtf":
                    value = items[0].get("html");
                    break;
                case "file":
                    value = items[0].get("value");
                    break;
                default:
                    value = items[0].get("text");
            }
        }
        if( !value )value="";
        if( !text )text = value;
        var result = {};
        result.value = value;
        result.text = text;
        if( vort == "value" )return value;
        if( vort == "text")return text;
        return result;
    },
    getValue : function( name ){
        return this.get( null , name ).value;
    },
    getText : function( name ){
        return this.get( null, name ).text;
    },
    getModifiedValue : function(){
        var value = this.getValue();
        return value == this.options.value ? null : value ;
    },
    getModifiedText : function(){
        var value = this.getText();
        return text == this.options.text ? null : text ;
    },
    set : function( type, valueOrText ){
        this.setValue( valueOrText )
    },
    resetItemOptions : function( selectValue, selectText ){
        var availTypes = "radio,checkbox,select,multiselect".split( "," );
        if( !availTypes.contains( this.options.type )  )return;
        this.dispose();
        this.options.selectValue = selectValue;
        this.options.selectText = selectText;
        this.createElement();
    },
    setValue :function(value){
        var items= this.mElement.getElements("[name='"+ this.options.name + "']");
        if( this.options.isEdited ){
            switch( this.options.type.toLowerCase() ){
                case "text":
                    items[0].set( "value", value );
                    break;
                case "password":
                    items[0].set( "value", value );
                    break;
                case "hidden":
                    items[0].set( "value", value );
                    break;
                case "innertext":
                    items[0].set("text", value );
                    break;
                case "radio":
                    items.each(function( el ){
                        if( el.get("value") == value ) this.checked = true;
                    });
                    break;
                case "checkbox":
                    values = typeOf( value ) == "array" ? value : value.split("^^");
                    items.each(function( el ){
                        if( values.contains( el.get("value") ) != -1 ){
                            this.checked = true;
                        }else{
                            this.checked = false;
                        }
                    });
                    break;
                case "textarea":
                    items[0].set( "value", value );
                    break;
                case "rtf":
                    this.editor.setData(value);
                    break;
                case "select":
                    items[0].getElements("option").each(function( el ){
                        if( el.get("value") == value ) this.selected = true;
                    });
                    break;
                case "multiselect":
                    values = typeOf( value ) == "array" ? value : value.split("^^");
                    items[0].getElements("option").each(function( el ){
                        if( values.contains( el.get("value") ) != -1 ){
                            this.selected = true;
                        }else{
                            this.selected = false;
                        }
                    })
                    break;
                default :
                    items[0].set( "value", value );
            }
        }else{
            if( this.options.type.toLowerCase() == "rtf" ){
                items[0].set("html", value );
            }else{
                items[0].set("text", value );
            }
        }
    },
    setStyles : function( styles ){
        this.items.each( function( item ){
            item.setStyles( styles )
        })
    },
    getElements : function(){
        return this.mElement.getElements("[name='"+this.options.name+"']");
    },
    dispose : function(){
        this.container.empty();
    },
    verify : function( isShowWarning ){
        var flag = true
        if( !this.options.isEdited )return flag;

        if( this.options.warningType == "batch" ){
            if( !this.isNotEmpty(isShowWarning) ) flag = false;
            if( !this.checkValid(isShowWarning) ) flag = false;
        }else{
            if( !this.isNotEmpty(isShowWarning) || !this.checkValid(isShowWarning) ){
                return false;
            };
        }
        return flag;
    },
    isNotEmpty: function( isShowWarning ){
        if( !this.options.isEdited )return true;
        if( this.options.notEmpty == true || this.options.notEmpty == "yes" ){
            if( !this.checkNotEmpty( isShowWarning ) ){
                return false;
            }
        }
        return true;
    },
    checkNotEmpty:function( isShowWarning ){
        var value = this.getValue();
        var isEmpty = ( typeOf(value) === "array" ? ( value.length == 0 ) : ( value == "" || value == " ") );
        if( !isEmpty && this.options.defaultValueAsEmpty ){
            isEmpty = ( typeOf(value) === "array" ? ( value.length == 1 && value[0] == this.options.defaultValue ) : ( value == this.options.defaultValue ) );
        }
        if( !isEmpty ){
            this.clearWarning("empty");
            return true;
        }
        if( !isShowWarning )return false;
        var text = this.options.text;
        var items = this.mElement.getElements("[name='"+ this.options.name + "']");
        var warningText = "";
        var focus = false;
        var focusObj = null;
        try{
            switch( this.options.type.toLowerCase() ){
                case "text":
                    warningText = this.options.emptyTip || (text+"不能为空");
                    focus = false;
                    break;
                case "password":
                    warningText = this.options.emptyTip || (text+"不能为空");
                    focus = false;
                    break;
                case "hidden":
                    warningText = this.options.emptyTip || (text+"不能为空");
                    break;
                case "radio":
                    warningText = this.options.emptyTip || ("请先选择"+text);
                    break;
                case "checkbox":
                    warningText = this.options.emptyTip || ("请先选择"+text);
                    break;
                case "textarea":
                    warningText = this.options.emptyTip || (text+"不能为空");
                    focus = false;
                    break;
                case "select":
                    warningText = this.options.emptyTip || ("请先选择"+text);
                    focus = false;
                    break;
                case "multiselect":
                    warningText = this.options.emptyTip || ("请先选择"+text);
                    focus = false;
                    break;
                case "file":
                    warningText = this.options.emptyTip || ("请先上传"+text);
                    break;
                case "attachment":
                    warningText = this.options.emptyTip || ("请先上传"+text);
                    break;
                default :
                    warningText = this.options.emptyTip || (text+"不能为空");
                    focus = false;
            }
            if( this.options.warningType == "batch" ) {
                this.setWarning(warningText, "empty");
            }else if( this.options.warningType == "single" ){
                this.setWarning(warningText, "empty");
            }else{
                this.app.notice(warningText,"error");
                items[0].focus();
            }
            this.fireEvent("empty", this);
        }catch( e ){
        }
        return false;
    },
    clearWarning : function( type ){
        if( this.tipNode && this.setedEmpty ){
            this.fireEvent("unempty", this);
            this.tipNode.empty();
            this.setedEmpty = false;
        }
        if( type == "empty" ){
            if( this.warningEmptyNode ){
                this.fireEvent("unempty", this);
                this.warningEmptyNode.destroy();
                this.warningEmptyNode = null;
            }
        }else{
            if( this.warningInvalidNode ){
                this.fireEvent("unempty", this);
                this.warningInvalidNode.destroy();
                this.warningInvalidNode = null;
            }
        }
        this.warningStatus = false;
    },
    setWarning : function( msg, type ){

        if( type == "empty" ){
            if( this.tipNode ){
                this.setedEmpty = true;
                var div = this.tipNode;
                div.set("html", "");
            }else if( this.warningEmptyNode ){
                var div = this.warningEmptyNode;
                div.set("html", "");
            }else{
                var div = this.warningEmptyNode = new Element("div");
                div.inject( this.container ) ;
            }
        }else{
            if( this.tipNode ){
                this.setedEmpty = true;
                var div = this.tipNode;
                div.set("html", "");
            }else if( this.warningInvalidNode ){
                var div = this.warningInvalidNode;
                div.set("html", "");
            }else{
                var div = this.warningInvalidNode = new Element("div");
                div.inject( this.container ) ;
            }
        }

        this.warningStatus = true;

        if( typeOf(msg) != "array" ){
            msg = [msg];
        }

        msg.each( function(m){
            //var html = "<table style=\"margin-top:3px;\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">";
            //html += "<tr valign=\"middle\"><td><img src=\"./img/exclamation.png\" /></td>";
            //html += "<td style=\"width:3px;\"></td><td><div style=\"color:#FF0000; margin-top:2px;\">"+m+"</div></td></tr>";
            //html += "</table>";
            var node = new Element("div",{
                "text" : m,
                "styles" : this.css.warningMessageNode
            }).inject(div)
        }.bind(this))

    },
    checkValid : function( isShowWarning ){

        var value = this.getValue();
        var rules = this.options.validRule;
        if( !rules )return true;

        var msgs = [];
        var flag = true;

        if( value && value != "" && value != " " ){
            for(var r in rules ){
                var valid = true;
                var rule = rules[r];

                if( typeof rule == "function"){
                    valid = rule.call( this, value, this );
                }else if( this.validMethod[r] ){
                    var method = this.validMethod[r];
                    valid = method.call(this, value, rule, this );
                }

                if( !valid && isShowWarning ){
                    var msg = this.getValidMessage( r, rule );
                    if( msg != "" )msgs.push( msg );
                }

                if( !valid )flag = false;
            }
        }

        if( msgs.length > 0 ){
            if( this.options.warningType == "batch" ) {
                this.setWarning(msgs, "invaild");
            }else if( this.options.warningType == "single" ){
                this.setWarning(msgs, "invaild");
            }else{
                this.app.notice(msgs.join("\n"),"error");
            }
            this.fireEvent("empty", this);
        }else{
            if( this.warningInvalidNode && this.warningInvalidNode.length ){
                this.warningInvalidNode.destroy();
                this.warningInvalidNode = null;
            }
            this.fireEvent("unempty", this);
        }

        return flag;

    },
    validMethod : {
        email: function( value ) {
            return /^[a-zA-Z0-9.!#$%&'*+\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/.test( value );
        },
        url: function( value ) {
            return /^(https?|s?ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i.test( value );
        },
        phoneNumber: function( value ){
            return /^0?1[0-9]\d{9}$/.test( value );
        },
        date: function( value ) {
            return !/Invalid|NaN/.test( new Date( value ).toString() );
        },
        dateISO: function( value ) {
            return /^\d{4}[\/\-](0?[1-9]|1[012])[\/\-](0?[1-9]|[12][0-9]|3[01])$/.test( value );
        },
        number: function( value ) {
            return /^-?(?:\d+|\d{1,3}(?:,\d{3})+)?(?:\.\d+)?$/.test( value );
        },
        digits: function( value ) {
            return /^\d+$/.test( value );
        },
        minlength: function( value, param ) {
            return value.length >= param;
        },
        maxlength: function( value, param ) {
            return value.length <= param;
        },
        rangelength: function( value, param ) {
            return ( value.length >= param[ 0 ] && value.length <= param[ 1 ] );
        },
        min: function( value, param ) {
            return value >= param;
        },
        max: function( value, param ) {
            return value <= param;
        },
        range: function( value, param ) {
            return ( value >= param[ 0 ] && value <= param[ 1 ] );
        },
        extension: function( value, param ){
            param = typeOf( param ) == "array" ?  param.join("|") : param.replace(/,/g, "|"); //"png|jpe?g|gif";
            return value.match(new RegExp(".(" + param + ")$", "i"));
        }
    },
    getValidMessage : function( type, param ){
        var msg = this.options.validMessage;
        if( msg && typeOf(msg) == "object"  ){
            if( msg[type] ){
                if( typeof msg[type] == "function" ){
                    return (msg[type]).call(this);
                }else{
                    return msg[type];
                }
            }
        }
        switch( type ){
            case "email":
                return "请输入正确格式的电子邮件";
            case "url":
                return "请输入合法的网址";
            case "phoneNumber" :
                return "请输入正确的手机号码"
            case "date":
                return "请输入合法的日期";
            case "dateISO":
                return "请输入合法的日期 .";
            case "number":
                return "请输入合法的数字";
            case "digits":
                return "只能输入整数";
            case "maxlength":
                return "长度不能超过"+ param ;
            case "minlength":
                return "长度不能小于"+ param ;
            case "rangelength":
                return "长度不能要介于"+ param[0] + "和" + param[1] + "之间" ;
            case "range":
                return "请输入一个介于"+ param[0] + " 和 "+ param[1] + "之间的值" ;
            case "min":
                return "请输入一个最小为"+ param +" 的值" ;
            case "max":
                return "请输入一个最大为"+ param +"的值" ;
            case "extension":
                return "请上传" + param + "格式的附件" ;
            default :
                return "请输入正确的"+ this.options.text ;
        }
    }
});