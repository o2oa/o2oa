/*
 DOMElement管理类
 var item = new MDomItem( containerNode, {
     name : "demo",
     type : "checkbox",
     selectValue: [opt1,opt2,opt3]
     selectText : function( callback , options ){ //异步方法的写法，需要一个callback，并且把该option值传给callback执行,这个function不能bind
         this.app.actions.XXX( id, function( json ){
         var optText = [];
         ....
         callback( optText )
     })
 },
 value : function(){ var val = .... ; return val; }
 } , null, app, css);
 item.load();
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

//MWF.xDesktop.requireApp("Template", "lp." + MWF.language, null, false);
var MDomItem_ClassType = {
    "text" : "Text",
    "textarea" : "Textarea",
    "hidden" : "Hidden",
    "password" : "Password",
    "radio" : "Radio",
    "checkbox" : "Checkbox",
    "select" : "Select",
    "multiselect" : "Multiselect",
    "innertext" : "Innertext",
    "innerhtml" : "Innerhtml",
    "img" : "Img",
    "button" : "Button",
    "mselector" : "MSelector",
    "imageclipper" : "ImageClipper",
    "rtf" : "Rtf",
    "org" : "Org",
    "a" : "A"
};

var MDomItem = new Class({
    Implements: [Options, Events],
    options : {
        name : "",		//生成的对象的name属性

        value : "",		//对象的值
        text : "",		//对应的中文名称
        type : "",  //可以为 text,innertext, radio,checkbox,select,multiselect,img,button,hidden,rtf,imageClipper, org, mSelector

        isEdited : true,

        tType : "", //type 为text时候有效，可以为 number,date,time, datetime,person、unit、identity,如果是组织混合选择用数组,如["person"、"unit"、"identity"]

        orgType : "", //person、unit、identity, process， duty,如果是混合选择用数组，如["person"、"unit"、"identity"]
        unitType : "", //如果orgType包含unit，则可以指定组织类型
        count : 1, //如果是多选，多选的上限值，0表示无限制，默认为1,
        units : [], //orgType 为 identity、unit时的，部门选择范围
        groups : [], //orgType 为 person 时的选择访问
        exclude : [], //选择时的排除项目
        expandSubEnable : true, //orgType 为 identity、unit时是否展开下级选择范围
        orgStyle : "", //显示类型，比如default, xform 等

        unsetDefaultEvent : false, //tType 或 orgType 有值时，是否取消默认事件

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
        defaultValueAsEmpty: false, //检查空值时，默认值是否为空，并且获取值得时候，如果是默认值，返回空值
        emptyTip : null, //为空时的提示，可以不设置

        disable : false, //为false,则load失效;

        //alert 或者 batch 或者 single，单个提醒或者批量提醒
        warningType : "batch",

        validImmediately : false,

        //可以传入校验类型或自定义方法，如
        //{email : true , url : true, date : true, dateISO : true, number : true, digits : true,
        // maxlength:5, minlength:10, rangelength:[5,10], max:5, min:10 ,range:[5,10], extension: ["xls","xlsx"],fun : function(){ return true }
        // }
        validRule : null,

        //validMessage和validRule对应，出错时提示的信息，如 {email : "请输入正确格式的电子邮件", fun : "请输入正确的密码"}，如果不设置，默认如下：
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

        RTFConfig : null, //CKEditor 的设置项
        mSelectorOptions : null, //自定义下拉组件设置项
        calendarOptions : null, //日期选择器的设置项
        orgWidgetOptions : null //org组件的选项
    },
    initialize: function (container, options , parent, app, css ) {
        this.form = parent;
        this.tr = parent;
        this.parent = parent;
        this.app = app;
        this.container = $(container);
        this.css = css;

        //for(var o in options ){	//允许使用 function 来计算设置, on开头的属性被留作 fireEvent
        //    if( o != "validRule" && o!="validMessage" && o.substr(0,2)!="on" && typeOf( options[o] )== "function" ){
        //        options[o] = options[o].call();
        //    }
        //}
        //this.setOptions(options);

        this.valSeparator = /,|;|\^\^|\|/; //如果是多值对象，作为用户选择的多个值的分隔符
        this.mElement = this.container;	//容器
        this.items = [];

        this.setOptionList( options );
    },
    setOptionList : function( options ){  //目的是使用options里的function异步方法通过 function(callback){ ...获取value; callback( value );  } 来回调设置option
        var callbackNameList = [];
        for(var o in options ){	//允许使用 function 来计算设置, on开头的属性被留作 fireEvent
            if( o != "validRule" && o!="validMessage" && o.substr(0,2)!="on" && typeOf( options[o] )== "function" ){
                var fun = options[o];
                if( fun.length && /\(\s*([\s\S]*?)\s*\)/.exec(fun)[1].split(/\s*,\s*/)[0] == "callback" ){ //如果有行参(fun.length!=0),并且第一形参是callback，注意，funciont不能bind(this),否则不能判断
                    callbackNameList.push( o );
                }else{
                    options[o] = fun( options ); //执行fun
                }
            }
        }
        this.setFunOption( options, callbackNameList, true ); //递归执行回调设置options
    },
    setFunOption : function( options, callbackNameList, isFirst ){
        this.optionsReady = false;
        if( callbackNameList.length == 0 ){
            this.setOptions( options );
            this.optionsReady = true;
            if( this.loadFunctionCalled ){ //如果外部程序已经执行过load，但是由于options没有设置完成而中断，需要再调用一下load
                this.load();
            }
        }else{
            if( isFirst )options = Object.merge( {}, options ); //避免外部程序对options的修改
            var name = callbackNameList.shift(); //返回第一个元素，然后在callbackNameList删除第一元素
            var fun = options[name];  //对应的参数，是一个function
            fun( function( val ){  //执行function
                options[name] = val; //在回调内部给option赋值
                this.setFunOption( options, callbackNameList, false );  //继续执行下一个回调
            }.bind(this), options );
        }
    },
    load: function () {
        if( !this.optionsReady ){ //如果options没有设置完成
            this.loadFunctionCalled = true;
            return;
        }
        if( this.options.disable )return;
        if( ! this.options.type ){
            this.options.type = this.options.orgType ? "org" : "text";
        }
        this.options.type = this.options.type.toLowerCase();

        this.fireEvent("queryLoad");

        this.createElement();

        this.fireEvent("postLoad", [this]);
    },
    editMode : function(){
        this.options.isEdited = true;
        this.dispose();
        this.load();
    },
    save : function(){
        this.options.value = this.getValue();
    },
    readMode : function(){
        this.options.isEdited = false;
        this.dispose();
        this.load();
    },
    enable : function(){
        this.options.disable = false;
        this.dispose();
        this.load();
    },
    disable : function(){
        this.options.disable = true;
        this.dispose();
    },
    createElement:function(){
        if( this.options.disable )return;
        var clazzName = MDomItem_ClassType[ this.options.type ];
        if( clazzName ){
            this.dom = new MDomItem[ clazzName ]( this );
            this.dom.load();
        }
        return this.container;
    },
    get: function( vort  ){	//value 和 text
        if( this.options.disable ){
            return {
                text : "",
                value : ""
            };
        }
        if( this.dom )return this.dom.get( vort );
    },
    getValue : function( separator, name ){
        var result = this.get( null , name ).value;
        if( separator && typeOf( result ) == "array" ){
            return result.join( separator );
        }else{
            return result;
        }
    },
    getText : function( separator, name ){
        var result = this.get( null , name ).text;
        if( separator && typeOf( result ) == "array" ){
            return result.join( separator );
        }else{
            return result;
        }
    },
    getModifiedValue : function( separator ){
        var value = this.getValue( separator );
        return value == this.options.value ? null : value ;
    },
    getModifiedText : function(){
        var value = this.getText();
        return text == this.options.text ? null : text ;
    },
    getVaildValue : function(verify, separator, isHiddenWarming, onlyModified ) {
        if ( !verify || this.verify(!isHiddenWarming)) {
            return onlyModified ? this.getModifiedValue( separator ) : this.getValue( separator );
        } else {
            return false;
        }
    },
    set : function( type, valueOrText ){
        this.setValue( valueOrText )
    },
    resetItemOptions : function( selectValue, selectText, isForce ){
        if( this.options.disable ){
            if( isForce ){
                this.options.disable = false;
            }else{
                return;
            }
        }
        var availTypes = "radio,checkbox,select,multiselect".split( "," );
        if( !availTypes.contains( this.options.type )  )return;
        this.dispose();
        this.options.selectValue = selectValue;
        this.options.selectText = selectText;
        this.createElement();
    },
    reset: function(){
        this.setValue( this.options.defaultValue || "" );
    },
    setValue :function(value){
        if( this.dom )this.dom.setValue(value);
    },
    setStyles : function( styles ){
        if( this.options.disable )return;
        this.items.each( function( item ){
            item.setStyles( styles )
        })
    },
    getElements : function(){
        if( this.options.disable )return null;
        return this.mElement.getElements("[name='"+this.options.name+"']");
    },
    dispose : function(){
        this.container.empty();
    },
    verify : function( isShowWarning ){
        var flag = true;
        if( !this.options.isEdited )return flag;
        if( this.options.disable )return flag;

        if( this.options.warningType == "batch" ){
            if( !this.isNotEmpty(isShowWarning) ) flag = false;
            if( !this.checkValid(isShowWarning) ) flag = false;
        }else{
            if( !this.isNotEmpty(isShowWarning) || !this.checkValid(isShowWarning) ){
                return false;
            }
        }
        return flag;
    },
    isNotEmpty: function( isShowWarning ){
        if( !this.options.isEdited )return true;
        if( this.options.disable )return true;
        if( this.options.notEmpty == true || this.options.notEmpty == "yes" ){
            if( !this.checkNotEmpty( isShowWarning ) ){
                return false;
            }
        }
        return true;
    },
    checkNotEmpty:function( isShowWarning ){
        if( this.options.disable )return true;
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
        try{
            warningText = this.options.emptyTip ||  (this.dom && this.dom.getErrorText()) || MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}",text);
            if( this.options.warningType == "batch" ) {
                this.setWarning(warningText, "empty");
            }else if( this.options.warningType == "single" ){
                this.setWarning(warningText, "empty");
            }else{
                if( this.app && this.app.notice ){

                    if (!this.container.isIntoView()){
                        var pNode = this.container.getParent();
                        while (pNode && ((pNode.getScrollSize().y-(pNode.getComputedSize().height+1)<=0) || pNode.getStyle("overflow")==="visible")) pNode = pNode.getParent();
                        if (!pNode) pNode = document.body;
                        pNode.scrollToNode(this.container, "bottom");
                    }
                    var y = this.container.getSize().y;
                    this.app.notice(warningText,"error",this.container, {"x": "right", "y": "top"}, { x : 10, y : y });
                }
                if( !this.options.validImmediately ){
                    if( ["text","password","textarea","select","multiselect"].contains( this.options.type ) ){
                        items[0].focus();
                    }
                }
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
        var div;
        if( type == "empty" ){
            if( this.tipNode ){
                this.setedEmpty = true;
                div = this.tipNode;
                div.set("html", "");
            }else if( this.warningEmptyNode ){
                div = this.warningEmptyNode;
                div.set("html", "");
            }else{
                div = this.warningEmptyNode = new Element("div");
                div.inject( this.container ) ;
            }
        }else{
            if( this.tipNode ){
                this.setedEmpty = true;
                div = this.tipNode;
                div.set("html", "");
            }else if( this.warningInvalidNode ){
                div = this.warningInvalidNode;
                div.set("html", "");
            }else{
                div = this.warningInvalidNode = new Element("div");
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
        if( this.options.disable )return true;
        var value = this.getValue();
        var rules = this.options.validRule;
        if( !rules )return true;

        var msgs = [];
        var flag = true;

        //if( value && value != "" && value != " " ){
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
        //}

        if( msgs.length > 0 ){
            if( this.options.warningType == "batch" ) {
                this.setWarning(msgs, "invaild");
            }else if( this.options.warningType == "single" ){
                this.setWarning(msgs, "invaild");
            }else{
                if( this.app && this.app.notice ) {
                    if (!this.container.isIntoView()) {
                        var pNode = this.container.getParent();
                        while (pNode && ((pNode.getScrollSize().y - (pNode.getComputedSize().height + 1) <= 0) || pNode.getStyle("overflow") === "visible")) pNode = pNode.getParent();
                        if (!pNode) pNode = document.body;
                        pNode.scrollToNode(this.container, "bottom");
                    }
                    var y = this.container.getSize().y;
                    this.app.notice(msgs.join("\n"), "error", this.container, {"x": "right", "y": "top"}, { x : 10, y : y });
                }
            }
            this.fireEvent("empty", this);
        }else{
            if( this.warningInvalidNode ){
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
        var lp = MWF.xApplication.Template.LP.MDomItem;
        switch( type ){
            case "email":
                return lp.emailTip;
            case "url":
                return lp.urlTip;
            case "phoneNumber" :
                return lp.phoneNumberTip;
            case "date":
                return lp.dateTip;
            case "dateISO":
                return lp.dateISOTip;
            case "number":
                return lp.numberTip;
            case "digits":
                return lp.digitsTip;
            case "maxlength":
                return lp.maxlengthTip.replace("{n}",param );
            case "minlength":
                return lp.minlengthTip.replace("{n}",param );
            case "rangelength":
                return lp.rangelengthTip.replace("{n0}",param[0] ).replace("{n1}",param[1] ) ;
            case "range":
                return lp.rangeTip.replace("{n0}",param[0] ).replace("{n1}",param[1] ) ;
            case "min":
                return lp.minTip.replace("{n}",param );
            case "max":
                return lp.maxTip.replace("{n}",param );
            case "extension":
                return lp.extensionTip.replace("{text}",param );
            default :
                return lp.defaultTip.replace("{text}",this.options.text );
        }
    },
    destroy: function(){
        if( this.dom && this.dom.OrgWidgetList ){
            this.dom.OrgWidgetList.each( function( widget ){
                widget.destroy();
            })
        }
        if( this.mElement ){
            this.mElement.empty();
        }
        MWF.release( this );
    }
});

MDomItem.Util = {
    selectCalendar : function( target, container, options, callback ){
        var type = options.type;
        var calendarOptions = {
            "style" : "xform",
            "isTime":  type == "time" || type.toLowerCase() == "datetime",
            "timeOnly": type == "time",
            "target": container,
            "onComplate" : function( dateString ,date ){
                if( callback )callback( dateString, date );
            }.bind(this)
        };
        if( options.calendarOptions ){
            calendarOptions = Object.merge( calendarOptions, options.calendarOptions )
        }
        var calendar;
        MWF.require("MWF.widget.Calendar", function(){
            calendar = new MWF.widget.Calendar( target, calendarOptions);
            calendar.show();
        }.bind(this), false);
        return calendar;
    },
    selectPerson: function( container, options, callback  ){
        MWF.xDesktop.requireApp("Selector", "package", null, false);

        var selectType = "", selectTypeList = [];
        var type = options.type;
        if( typeOf( type ) == "array" ){
            if( type.length > 1 ){
                selectTypeList = type;
            }else if( type.length == 0 ) {
                selectType = "person";
            }else{
                selectType = type[0] || "person";
            }
        }else{
            selectType = type || "person";
        }

        var opt  = {
            "type": selectType,
            "types" : selectTypeList,
            "title": options.title,
            "count" : options.count,
            "values": options.selectedValues || [],
            "units" : options.units,
            "unitType" : options.unitType,
            "groups" : options.groups,
            "expand": options.expand,
            "exclude" : options.exclude || [],
            "expandSubEnable" : options.expandSubEnable,
            "onComplete": function( array ){
                if( callback )callback( array );
            }.bind(this)
        };
        if( opt.types.length === 0 )opt.types = null;
        var selector = new MWF.O2Selector(container, opt );
    },
    replaceText : function( value, selectValue, selectText, separator ){
        if( typeOf( value ) == "number" )value = [ value ];
        if( typeOf( selectValue ) == "number" )selectValue = [ selectValue ];
        if( typeOf( selectText ) == "number" )selectText = [ selectText ];
        var vals = typeOf( value ) == "array" ? value : value.split( separator );
        var selectValues = typeOf( selectValue ) == "array" ? selectValue : selectValue.split( separator );
        var selectTexts =  typeOf( selectText ) == "array" ? selectText : selectText.split( separator );
        for( var i=0 ;i<vals.length; i++ ){
            for( var j= 0; j<selectValues.length; j++){
                if( vals[i] == selectValues[j] ){
                    vals[i] = selectTexts[j]
                }
            }
        }
        return vals;
    },
    getEvents : function( events ){
        if( !events || events == "" || events == "$none" )return;
        if( typeof events == "string" ){
            if( events.indexOf("^^") > -1 ){
                var eventsArr = events.split("##");
                if( eventsArr[0].split("^^").length != 2 )return;
                events = {};
                for(var i=0;i<eventsArr.length;i++){
                    var ename = eventsArr[i].split("^^")[0];
                    var efunction = eventsArr[i].split("^^")[1];
                    events[ ename ] = eval( "(function(){ return "+ efunction +" })()" );  //字符串变对象或function，方法1
                }
            }else{
                //字符串变对象或function，方法2
                eval( "var events = " + events );
            }
        }
        return events;
    },
    bindEvent: function( obj, item, events){
        events = MDomItem.Util.getEvents( events );
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
                    this.fun.call( ev ? ev.target : null, obj.module || obj, ev );
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
    }
};

MDomItem.Text = new Class({
    initialize: function ( module ) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
    },
    load : function(){
        if( this.options.isEdited ){
            this.loadEdit()
        }else{
            this.loadRead();
        }
    },
    loadEdit : function(){
        var module = this.module;
        var options = this.options;
        var item;
        var value ;
        if( typeOf( options.value ) === "boolean" ){
            value = options.value.toString();
        }else{
            value = options.value || options.defaultValue
        }
        var parent = module.container ;
        var className = this.getClassName();
        item = new Element( "input", {
            "type" : "text",
            "name" : options.name,
            "value" : value
        });
        item.set( options.attr || {} );
        if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
        item.setStyles( options.style || {} );

        this.bindDefaultEvent( item );
        MDomItem.Util.bindEvent( this, item, options.event);
        if(parent)item.inject(parent);
        this.items.push( item );
    },
    loadRead : function(){
        var module = this.module;
        var options = this.options;
        var item;
        var value;
        var className = this.getClassName();
        var parent = module.container ;
        if( typeOf( options.value ) === "boolean" ){
            value = options.value.toString();
        }else{
            value = options.value || options.defaultValue
        }
        item = new Element( "span", {
            "name" : options.name,
            "text" : value
        });
        item.set( options.attr );
        if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
        item.setStyles( options.style || {}  );

        if(parent)item.inject(parent);
        this.items.push( item );
    },
    get : function( vort ){	//value 和 text 或 空
        if( this.options.disable ){
            return ( vort == "value" || vort == "text" ) ? null : {
                value : null,
                text : null
            };
        }
        var value;
        var name = this.options.name;
        var item = this.mElement.getElement("[name='"+name+"']");
        if( this.options.isEdited ){
            value = item.get("value");
        }else{
            value = item.get("text");
        }
        if( vort == "value" )return value;
        if( vort == "text")return value;
        return {
            value : value,
            text : value
        };
    },
    setValue : function( value ){
        if( this.options.disable ){
            return;
        }
        var item = this.mElement.getElement("[name='"+ this.options.name + "']");
        if( this.options.isEdited ){
            item.set( "value", value );
        }else{
            item.set("text", value );
        }
    },
    getErrorText : function(){
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}",this.options.text);
    },
    getClassName : function(){
        var tType = this.options.tType;
        var className = null ;
        if( this.options.className == "none" ){
        }else if( this.options.className != "") {
            className = this.options.className
        }else if( !this.options.isEdited ){
        }else {
            if (typeOf(tType) == "array") {
                if (tType.contains("identity") || tType.contains("person") || tType.contains("unit")) {
                    className = "inputPerson";
                } else {
                    className = "inputText";
                }
            } else {
                if (!tType) {
                    className = "inputText";
                } else if (tType == "number") {
                    className = "inputText";
                } else if (tType == "time" || tType == "date" || tType.toLowerCase() == "datetime") {
                    className = "inputTime";
                } else if (tType == "identity" || tType == "person" || tType.toLowerCase() == "unit") {
                    className = "inputPerson";
                } else {
                    className = "inputText";
                }
            }
        }
        return className;
    },
    bindDefaultEvent : function( item ){
        if( this.options.unsetDefaultEvent )return;
        var tType = this.options.tType;
        var type = "text";
        if( typeOf( tType ) == "array" || ( tType == "identity" || tType.toLowerCase() == "person" || tType == "unit" ) ){
            item.addEvent( "click" , function(ev){
                this.module.fireEvent("querySelect", this.module );
                var options = this.options;
                var opt = {
                    type : tType,
                    title : options.text,
                    count : options.count,
                    selectedValues : this.get("value").split(","),
                    units : options.units,
                    unitType : options.unitType,
                    groups : options.groups,
                    expand : options.expand
                };
                MDomItem.Util.selectPerson( this.app.content, opt, function( array ){
                    item.empty();
                    this.orgData = this.module.orgData = [];
                    this.orgObject = this.module.orgObject = array;
                    array.each(function( it ){
                        this.orgData.push( it.data.distinguishedName || it.data.name );
                    }.bind(this));
                    item.set("value",this.orgData.join(","));
                    this.items[0].fireEvent("change", [this.module, ev]);
                    if( this.options.validImmediately )this.module.verify( true );
                }.bind(this))
            }.bind(this) );
        }else{
            if( tType == "number" ){
                item.addEvent( "keyup" , function(){
                    this.value=this.value.replace(/[^\d.]/g,'');
                });
                if( this.options.validImmediately ){
                    item.addEvent("blur", function(){ this.module.verify( true ); }.bind(this))
                }
            }else if( tType == "time" || tType.toLowerCase() == "datetime" || tType == "date" ){
                item.addEvent( "click" , function(ev){
                    this.module.fireEvent("querySelect", this.module );
                    if( this.calendarSelector ){
                        this.calendarSelector.show();
                    }else{
                        this.calendarSelector = MDomItem.Util.selectCalendar( item, this.app.content, {
                            calendarOptions : this.options.calendarOptions,
                            type : tType
                        }, function( dateString, date ){
                            this.items[0].fireEvent("change", [this.module, ev]);
                            if( this.options.validImmediately )this.module.verify( true );
                        }.bind(this) )
                    }
                }.bind(this) );
            }else{
                if( this.options.validImmediately ){
                    item.addEvent("blur", function(){ this.module.verify( true ); }.bind(this))
                }
            }
        }
    }
});

MDomItem.Textarea = new Class({
    initialize: function ( module ) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
    },
    load : function(){
        if( this.options.isEdited ){
            this.loadEdit()
        }else{
            this.loadRead();
        }
    },
    loadEdit : function(){
        var module = this.module;
        var options = this.options;
        var item;
        var value ;
        if( typeOf( options.value ) === "boolean" ){
            value = options.value.toString();
        }else{
            value = options.value || options.defaultValue
        }
        var parent = module.container ;
        var className = this.getClassName();
        item = new Element( "textarea", {
            "name" : options.name,
            "value" : value
        });
        item.set( options.attr || {} );
        if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
        item.setStyles( options.style || {} );
        this.bindDefaultEvent( item );
        MDomItem.Util.bindEvent( this, item, options.event);
        if(parent)item.inject(parent);
        this.items.push( item );
    },
    loadRead : function(){
        var module = this.module;
        var options = this.options;
        var item;
        var value;
        var className = this.getClassName();
        var parent = module.container ;
        if( typeOf( options.value ) === "boolean" ){
            value = options.value.toString();
        }else{
            value = options.value || options.defaultValue
        }
        item = new Element( "span", {
            "name" : options.name,
            "text" : value
        });
        item.set( options.attr || {} );
        if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
        item.setStyles( options.style || {} );

        if(parent)item.inject(parent);
        this.items.push( item );
    },
    bindDefaultEvent : function( item ){
        if( this.options.unsetDefaultEvent )return;
        if( this.options.validImmediately ){
            item.addEvent("blur", function(){ this.module.verify( true ); }.bind(this))
        }
    },
    get : function( vort ){
        if( this.options.disable ){
            return ( vort == "value" || vort == "text" ) ? null : {
                value : null,
                text : null
            };
        }
        var value;
        var name = this.options.name;
        var item = this.mElement.getElement("[name='"+name+"']");
        if( this.options.isEdited ){
            value = item.get("value");
        }else{
            value = item.get("text");
        }
        if( vort == "value" )return value;
        if( vort == "text")return value;
        return {
            value : value,
            text : value
        };
    },
    setValue : function( value ){
        if( this.options.disable ){
            return;
        }
        var item = this.mElement.getElement("[name='"+ this.options.name + "']");
        if( this.options.isEdited ){
            item.set( "value", value );
        }else{
            item.set("text", value );
        }
    },
    getErrorText : function(){
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}",this.options.text);
    },
    getClassName : function(){
        var tType = this.options.tType;
        var className = null ;
        if( this.options.className == "none" ){
        }else if( this.options.className != "") {
            className = this.options.className
        }else if( !this.options.isEdited ){
        }else {
            className = "inputTextarea"
        }
        return className;
    }
});

MDomItem.Hidden = new Class({
    initialize: function ( module ) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
    },
    load : function(){
        var parent = this.container;
        var item = new Element( "input", {
            "type" : "hidden",
            "name" : this.options.name,
            "value" : this.options.value
        });
        item.set( this.options.attr || {} );
        //this.bindEvent(item,event,type);
        if(parent)item.inject(parent);
        this.items.push( item );
    },
    get : function( vort ){
        if( this.options.disable ){
            return ( vort == "value" || vort == "text" ) ? null : {
                value : null,
                text : null
            };
        }
        var value;
        var name = this.options.name;
        var item = this.mElement.getElement("[name='"+name+"']");
        if( this.options.isEdited ){
            value = item.get("value");
        }else{
            value = item.get("text");
        }
        if( vort == "value" )return value;
        if( vort == "text")return value;
        return {
            value : value,
            text : value
        };
    },
    setValue : function( value ){
        if( this.options.disable ){
            return;
        }
        var item = this.mElement.getElement("[name='"+ this.options.name + "']");
        item.set( "value", value );
    },
    getErrorText : function(){
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}",this.options.text);
    }
});

MDomItem.Password = new Class({
    initialize: function ( module ) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
    },
    load : function(){
        if( this.options.isEdited ){
            this.loadEdit()
        }else{
        }
    },
    loadEdit : function(){
        var module = this.module;
        var options = this.options;
        var item;
        var value ;
        if( typeOf( options.value ) === "boolean" ){
            value = options.value.toString();
        }else{
            value = options.value || options.defaultValue
        }
        var parent = module.container ;
        var className = this.getClassName();
        item = new Element( "input", {
            "type" : "password",
            "name" : options.name,
            "value" : value
        });
        item.set( options.attr || {} );
        if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
        item.setStyles( options.style || {} );
        this.bindDefaultEvent( item );
        MDomItem.Util.bindEvent( this, item, options.event);
        if(parent)item.inject(parent);
        this.items.push( item );
    },
    loadRead : function(){

    },
    bindDefaultEvent : function( item ){
        if( this.options.unsetDefaultEvent )return;
        if( this.options.validImmediately ){
            item.addEvent("blur", function(){ this.module.verify( true ); }.bind(this))
        }
    },
    get : function( vort ){
        if( this.options.disable ){
            return ( vort == "value" || vort == "text" ) ? null : {
                value : null,
                text : null
            };
        }
        var value;
        var name = this.options.name;
        var item = this.mElement.getElement("[name='"+name+"']");
        if( this.options.isEdited ){
            value = item.get("value");
        }else{
            value = options.value || options.defaultValue
        }
        if( vort == "value" )return value;
        if( vort == "text")return value;
        return {
            value : value,
            text : value
        };
    },
    setValue : function( value ){
        if( this.options.disable ){
            return;
        }
        if( this.options.isEdited ){
            var item = this.mElement.getElement("[name='"+ this.options.name + "']");
            item.set( "value", value );
        }else{
            this.options.value = value;
        }
    },
    getErrorText : function(){
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}",this.options.text);
    },
    getClassName : function(){
        var tType = this.options.tType;
        var className = null ;
        if( this.options.className == "none" ){
        }else if( this.options.className != "") {
            className = this.options.className
        }else if( !this.options.isEdited ){
        }else {
            className = "inputPassword"
        }
        return className;
    }
});

MDomItem.Radio = new Class({
    initialize: function ( module ) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
        this.valSeparator = module.valSeparator;
    },
    load : function(){
        if( this.options.isEdited ){
            this.loadEdit();
        }else{
            this.loadRead();
        }
    },
    loadEdit : function(){
        var _self = this;
        var item;
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
        var parent = this.container ;
        var className = this.getClassName() ;
        var selectValues = typeOf( selectValue ) == "array" ? selectValue : selectValue.split( this.valSeparator );
        var selectTexts =  typeOf( selectText ) == "array" ? selectText : selectText.split(this.valSeparator);
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
            textNode.addEvent("click", function( ev ){
                this.input.checked = ! this.input.checked;
                var envents = MDomItem.Util.getEvents( _self.options.event );
                if( typeOf( envents ) == "object" ){
                    if( envents.change ){
                        envents.change.call( this.input, _self.module, ev );
                    }
                    if( envents.click ){
                        envents.click.call( this.input, _self.module, ev );
                    }
                }
                if( _self.options.validImmediately ){
                    _self.module.verify( true );
                }
            }.bind( {input : input} ) );

            if( this.options.validImmediately ){
                input.addEvent( "click", function(){ this.module.verify( true )}.bind(this) );
            }
            MDomItem.Util.bindEvent( this, item, event ); //? input or item
            if(parent)item.inject(parent);
            this.items.push( item );
        }
    },
    loadRead : function(){
        var item;
        var name = this.options.name;
        var value ;
        if( typeOf( this.options.value ) === "boolean" ){
            value = this.options.value.toString();
        }else{
            value = this.options.value || this.options.defaultValue
        }
        var selectValue = this.options.selectValue || this.options.selectText;
        var selectText = this.options.selectText || this.options.selectValue ;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.container ;
        var className = this.getClassName() ;
        if( selectValue && selectText ){
            value = MDomItem.Util.replaceText( value, selectValue , selectText, this.valSeparator  );
            value = value.join(",");
        }
        item = new Element( "span", {
            "name" : name,
            "text" : value
        });
        item.set( attr );
        if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
        item.setStyles( styles );

        if(parent)item.inject(parent);
        this.items.push( item );
    },
    get : function( vort ){
        if( this.options.disable ){
            return ( vort == "value" || vort == "text" ) ? null : {
                value : null,
                text : null
            };
        }
        var value;
        var text;
        var items;
        var name = this.options.name;
        items = this.mElement.getElements("[name='"+name+"']");
        if( this.options.isEdited ){
            items.each(function( el ){
                if( el.checked ){
                    value = el.get("value");
                    text = el.getParent().get("text").trim();
                }
            });
        }else{
            text = items[0].get("text");
            if( this.options.selectValue && this.options.selectText ){
                value = MDomItem.Util.replaceText( text, this.options.selectText , this.options.selectValue , this.valSeparator).join();
            }else{
                value = text;
            }
        }
        if( !value )value="";
        if( !text )text = value;
        if( vort == "value" )return value;
        if( vort == "text")return text;
        var result = {};
        result.value = value;
        result.text = text;
        return result;
    },
    setValue : function( value ){
        if( this.options.disable ){
            return;
        }
        var items= this.mElement.getElements("[name='"+ this.options.name + "']");
        if( this.options.isEdited ){
            items.each(function( el ){
                if( el.get("value") == value ) el.checked = true;
            });
        }else{
            value = MDomItem.Util.replaceText( value, this.options.selectValue , this.options.selectText, this.valSeparator  );
            value = value.join(",");
            items[0].set("text", value );
        }
    },
    getErrorText : function(){
        return MWF.xApplication.Template.LP.MDomItem.selectTip.replace("{text}",this.options.text);
    },
    getClassName : function(){
        var className = null ;
        if( this.options.className == "none" ){
        }else if( this.options.className != "") {
            className = this.options.className
        }else if( !this.options.isEdited ){
        }else {
            className = "inputRadio"
        }
        return className;
    }
});

MDomItem.Checkbox = new Class({
    initialize: function ( module ) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
        this.valSeparator = module.valSeparator;
    },
    load : function(){
        if( this.options.isEdited ){
            this.loadEdit();
        }else{
            this.loadRead();
        }
    },
    loadEdit : function(){
        var _self = this;
        var item;
        var values;
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
        var className = this.getClassName();
        values = typeOf( value ) == "string" ? value.split(this.valSeparator) : value ;
        values = typeOf( value ) == "array" ? value : [value];
        var selectValues = typeOf( selectValue ) == "array" ? selectValue : selectValue.split( this.valSeparator );
        var selectTexts =  typeOf( selectText ) == "array" ? selectText : selectText.split(this.valSeparator);
        for( var i=0;i<selectValues.length;i++){

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
            }).inject(item);
            textNode.addEvent("click", function( ev ){
                this.input.checked = ! this.input.checked;
                var envents = MDomItem.Util.getEvents( _self.options.event );
                if( typeOf( envents ) == "object" ){
                    if( envents.change ){
                        envents.change.call( this.input, _self.module, ev );
                    }
                    if( envents.click ){
                        envents.click.call( this.input, _self.module, ev );
                    }
                }
                if( _self.options.validImmediately ){
                    _self.module.verify( true );
                }
            }.bind( {input : input} ) );

            if( this.options.validImmediately ){
                item.addEvent("click", function(){ this.module.verify( true ); }.bind(this))
            }
            MDomItem.Util.bindEvent( this, item, event); // ? input or item
            if(parent)item.inject(parent);
            this.items.push( item );
        }
    },
    loadRead : function(){
        var item;
        var name = this.options.name;
        var value ;
        if( typeOf( this.options.value ) === "boolean" ){
            value = this.options.value.toString();
        }else{
            value = this.options.value || this.options.defaultValue
        }
        var selectValue = this.options.selectValue || this.options.selectText;
        var selectText = this.options.selectText || this.options.selectValue ;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.mElement = this.container ;
        var className = this.getClassName();
        if( selectValue && selectText ){
            value = MDomItem.Util.replaceText( value, selectValue , selectText, this.valSeparator  );
            value = value.join(",");
        }
        item = new Element( "span", {
            "name" : name,
            "text" : value
        });
        item.set( attr );
        if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
        item.setStyles( styles );

        if(parent)item.inject(parent);
        this.items.push( item );
    },
    get : function( vort ){
        if( this.options.disable ){
            return ( vort == "value" || vort == "text" ) ? null : {
                value : null,
                text : null
            };
        }
        var value;
        var text;
        var items;
        var name = this.options.name;
        items = this.mElement.getElements("[name='"+name+"']");
        if( this.options.isEdited ){
            value = [];
            text = [];
            items.each(function( el ){
                if( el.checked ){
                    value.push(el.get("value"));
                    text.push( el.getParent().get("text").trim() )
                }
            });
        }else{
            text = items[0].get("text");
            if( this.options.selectValue && this.options.selectText ){
                value = MDomItem.Util.replaceText( text, this.options.selectText , this.options.selectValue, this.valSeparator  );
            }else{
                value = text;
            }
        }
        if( !value )value="";
        if( !text )text = value;
        if( vort == "value" )return value;
        if( vort == "text")return text;
        var result = {};
        result.value = value;
        result.text = text;
        return result;
    },
    setValue : function( value ){
        if( this.options.disable ){
            return;
        }
        var items= this.mElement.getElements("[name='"+ this.options.name + "']");
        if( this.options.isEdited ){
            var values = typeOf( value ) == "array" ? value : value.split("^^");
            items.each(function( el ){
                if( values.contains( el.get("value") ) ){
                    el.checked = true;
                }else{
                    el.checked = false;
                }
            });
        }else{
            value = MDomItem.Util.replaceText( value, this.options.selectValue , this.options.selectText, this.valSeparator  );
            value = value.join(",");
            items[0].set("text", value );
        }
    },
    getErrorText : function(){
        return MWF.xApplication.Template.LP.MDomItem.selectTip.replace("{text}",this.options.text);
    },
    getClassName : function(){
        var tType = this.options.tType;
        var className = null ;
        if( this.options.className == "none" ){
        }else if( this.options.className != "") {
            className = this.options.className
        }else if( !this.options.isEdited ){
        }else {
            className = "inputCheckbox"
        }
        return className;
    }
});

MDomItem.Select = new Class({
    initialize: function ( module ) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
        this.valSeparator = module.valSeparator;
    },
    load : function(){
        if( this.options.disable )return;
        if( this.options.isEdited ){
            this.loadEdit();
        }else{
            this.loadRead();
        }
    },
    loadEdit : function(){
        var item;
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
        var parent = this.container ;
        var className = this.getClassName() ;
        item = new Element( "select" , {
            "name" : name
        });
        item.set( attr );
        if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
        item.setStyles( styles );

        var selectValues = typeOf( selectValue ) == "array" ? selectValue : selectValue.split( this.valSeparator );
        var selectTexts =  typeOf( selectText ) == "array" ? selectText : selectText.split(this.valSeparator);

        for( i=0;i<selectValues.length;i++){
            new Element("option" , {
                "value" : selectValues[i],
                "selected" : selectValues[i] == value,
                "text" : selectTexts[i]
            }).inject(item)
        }

        if( this.options.validImmediately ){
            item.addEvent("change", function(){ this.module.verify( true ); }.bind(this))
        }
        MDomItem.Util.bindEvent( this, item, event);
        if(parent)item.inject(parent);
        this.items.push( item );
    },
    loadRead : function(){
        var item;
        var name = this.options.name;
        var value ;
        if( typeOf( this.options.value ) === "boolean" ){
            value = this.options.value.toString();
        }else{
            value = this.options.value || this.options.defaultValue
        }
        var selectValue = this.options.selectValue || this.options.selectText;
        var selectText = this.options.selectText || this.options.selectValue ;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.mElement = this.container ;
        var className = this.getClassName();
        if( selectValue && selectText ){
            value = MDomItem.Util.replaceText( value, selectValue , selectText, this.valSeparator  );
            value = value.join(",");
        }
        item = new Element( "span", {
            "name" : name,
            "text" : value
        });
        item.set( attr );
        if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
        item.setStyles( styles );

        if(parent)item.inject(parent);
        this.items.push( item );
    },
    get : function( vort ){
        if( this.options.disable ){
            return ( vort == "value" || vort == "text" ) ? null : {
                value : null,
                text : null
            };
        }
        var value;
        var text;
        var items;
        var name = this.options.name;
        items = this.mElement.getElements("[name='"+name+"']");
        if( this.options.isEdited ){
            items[0].getElements("option").each(function(el){
                if( el.selected ){
                    value = el.get("value");
                    text = el.get("text").trim();
                }
            });
        }else{
            text = items[0].get("text");
            if( this.options.selectValue && this.options.selectText ){
                value = MDomItem.Util.replaceText( text, this.options.selectText , this.options.selectValue , this.valSeparator).join();
            }else{
                value = text;
            }
        }
        if( !value )value="";
        if( !text )text = value;
        if( vort == "value" )return value;
        if( vort == "text")return text;
        var result = {};
        result.value = value;
        result.text = text;
        return result;
    },
    setValue : function( value ){
        if( this.options.disable ){
            return;
        }
        var items= this.mElement.getElements("[name='"+ this.options.name + "']");
        if( this.options.isEdited ){
            items[0].getElements("option").each(function( el ){
                if( el.get("value") == value ) el.selected = true;
            });
        }else{
            value = MDomItem.Util.replaceText( value, this.options.selectValue , this.options.selectText, this.valSeparator  );
            value = value.join(",");
            items[0].set("text", value );
        }
    },
    getErrorText : function(){
        return MWF.xApplication.Template.LP.MDomItem.selectTip.replace("{text}",this.options.text);
    },
    getClassName : function(){
        var tType = this.options.tType;
        var className = null ;
        if( this.options.className == "none" ){
        }else if( this.options.className != "") {
            className = this.options.className
        }else if( !this.options.isEdited ){
        }else {
            className = "inputSelect"
        }
        return className;
    }
});

MDomItem.Multiselect = new Class({
    initialize: function ( module ) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
        this.valSeparator = module.valSeparator;
    },
    load : function(){
        if( this.options.disable )return;
        if( this.options.isEdited ){
            this.loadEdit();
        }else{
            this.loadRead();
        }
    },
    loadEdit : function(){
        var item;
        var values;
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
        var parent = this.container ;
        var className = this.getClassName() ;

        values = typeOf( value ) == "string" ? value.split(this.valSeparator) : value ;
        values = typeOf( value ) == "array" ? value : [value];

        item = new Element( "select" , {
            "name" : name,
            "multiple" : true
        });
        item.set( attr );
        if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
        item.setStyles( styles );

        var selectValues = typeOf( selectValue ) == "array" ? selectValue : selectValue.split( this.valSeparator );
        var selectTexts =  typeOf( selectText ) == "array" ? selectText : selectText.split(this.valSeparator);
        for( i=0;i<selectValues.length;i++){
            new Element("option" , {
                "value" : selectValues[i],
                "selected" : values.contains( selectValues[i] ),
                "text" : selectTexts[i]
            }).inject(item)
        }
        if( this.options.validImmediately ){
            item.addEvent("change", function(){ this.module.verify( true ); }.bind(this))
        }
        MDomItem.Util.bindEvent( this, item, event);
        if(parent)item.inject(parent);
        this.items.push( item );
    },
    loadRead : function(){
        var item;
        var name = this.options.name;
        var value ;
        if( typeOf( this.options.value ) === "boolean" ){
            value = this.options.value.toString();
        }else{
            value = this.options.value || this.options.defaultValue
        }
        var selectValue = this.options.selectValue || this.options.selectText;
        var selectText = this.options.selectText || this.options.selectValue ;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.container ;
        var className = this.getClassName();
        if( selectValue && selectText ){
            value = MDomItem.Util.replaceText( value, selectValue , selectText, this.valSeparator  );
            value = value.join(",");
        }
        item = new Element( "span", {
            "name" : name,
            "text" : value
        });
        item.set( attr );
        if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
        item.setStyles( styles );

        if(parent)item.inject(parent);
        this.items.push( item );
    },
    get : function( vort ){
        if( this.options.disable ){
            return ( vort == "value" || vort == "text" ) ? null : {
                value : null,
                text : null
            };
        }
        var value;
        var text;
        var items;
        var name = this.options.name;
        items = this.mElement.getElements("[name='"+name+"']");
        if( this.options.isEdited ){
            value = [];
            text = [];
            items[0].getElements("option").each(function(el){
                if( el.selected ){
                    value.push( el.get("value") );
                    text.push( el.get("text").trim() );
                }
            });
        }else{
            text = items[0].get("text");
            if( this.options.selectValue && this.options.selectText ){
                value = MDomItem.Util.replaceText( text, this.options.selectText , this.options.selectValue , this.valSeparator );
            }else{
                value = text;
            }
        }
        if( !value )value="";
        if( !text )text = value;
        if( vort == "value" )return value;
        if( vort == "text")return text;
        var result = {};
        result.value = value;
        result.text = text;
        return result;
    },
    setValue : function( value ){
        if( this.options.disable ){
            return;
        }
        var items= this.mElement.getElements("[name='"+ this.options.name + "']");
        if( this.options.isEdited ){
            var values = typeOf( value ) == "array" ? value : value.split("^^");
            items[0].getElements("option").each(function( el ){
                if( values.contains( el.get("value") ) ){
                    el.selected = true;
                }else{
                    el.selected = false;
                }
            })
        }else{
            value = MDomItem.Util.replaceText( value, this.options.selectValue , this.options.selectText, this.valSeparator  );
            value = value.join(",");
            items[0].set("text", value );
        }
    },
    getErrorText : function(){
        return MWF.xApplication.Template.LP.MDomItem.selectTip.replace("{text}",this.options.text);
    },
    getClassName : function(){
        var tType = this.options.tType;
        var className = null ;
        if( this.options.className == "none" ){
        }else if( this.options.className != "") {
            className = this.options.className
        }else if( !this.options.isEdited ){
        }else {
            className = "inputMultiselect"
        }
        return className;
    }
});

MDomItem.Innertext = new Class({
    initialize: function ( module ) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
        this.valSeparator = module.valSeparator;
    },
    load : function(){
        if( this.options.disable )return;
        var item;
        var name = this.options.name;
        var value ;
        if( typeOf( this.options.value ) === "boolean" ){
            value = this.options.value.toString();
        }else{
            value = this.options.value || this.options.defaultValue
        }
        var selectValue = this.options.selectValue || this.options.selectText;
        var selectText = this.options.selectText || this.options.selectValue ;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.mElement = this.container ;
        var className = this.getClassName();
        if( selectValue && selectText ){
            value = MDomItem.Util.replaceText( value, selectValue , selectText, this.valSeparator  );
            value = value.join(",");
        }
        item = new Element( "span", {
            "name" : name,
            "text" : value
        });
        item.set( attr );
        if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
        item.setStyles( styles );
        if( this.options.isEdited ){
            MDomItem.Util.bindEvent( this, item, this.options.event);
        }
        if(parent)item.inject(parent);
        this.items.push( item );
    },
    get : function( vort ){
        if( this.options.disable ){
            return ( vort == "value" || vort == "text" ) ? null : {
                value : null,
                text : null
            };
        }
        var value;
        var text;
        var name = this.options.name;
        var item = this.mElement.getElement("[name='"+name+"']");
        text = item.get("text");
        if( this.options.selectValue && this.options.selectText ){
            value = MDomItem.Util.replaceText( text, this.options.selectText , this.options.selectValue , this.valSeparator);
        }else{
            value = text;
        }
        if( !value )value="";
        if( !text )text = value;
        if( vort == "value" )return value;
        if( vort == "text")return text;
        var result = {};
        result.value = value;
        result.text = text;
        return result;
    },
    setValue : function( value ){
        if( this.options.disable ){
            return;
        }
        value = MDomItem.Util.replaceText( value, this.options.selectValue , this.options.selectText, this.valSeparator  );
        value = value.join(",");
        var item = this.mElement.getElement("[name='"+ this.options.name + "']");
        item.set("text", value );
    },
    getErrorText : function(){
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}",this.options.text);
    },
    getClassName : function(){
        var tType = this.options.tType;
        var className = null ;
        if( this.options.className == "none" ){
        }else if( this.options.className != "") {
            className = this.options.className
        }else if( !this.options.isEdited ){
        }else {
        }
        return className;
    }
});

MDomItem.Innerhtml = new Class({
    initialize: function ( module ) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
        this.valSeparator = module.valSeparator;
    },
    load : function(){
        if( this.options.disable )return;
        var item;
        var name = this.options.name;
        var value ;
        if( typeOf( this.options.value ) === "boolean" ){
            value = this.options.value.toString();
        }else{
            value = this.options.value || this.options.defaultValue
        }
        var selectValue = this.options.selectValue || this.options.selectText;
        var selectText = this.options.selectText || this.options.selectValue ;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.mElement = this.container ;
        var className = this.getClassName();
        if( selectValue && selectText ){
            value = MDomItem.Util.replaceText( value, selectValue , selectText, this.valSeparator  );
            value = value.join(",");
        }
        item = new Element( "span", {
            "name" : name,
            "html" : value
        });
        item.set( attr );
        if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
        item.setStyles( styles );
        if( this.options.isEdited ){
            MDomItem.Util.bindEvent( this, item, this.options.event);
        }
        if(parent)item.inject(parent);
        this.items.push( item );
    },
    get : function( vort ){
        if( this.options.disable ){
            return ( vort == "value" || vort == "text" ) ? null : {
                value : null,
                text : null
            };
        }
        var value;
        var text;
        var name = this.options.name;
        var item = this.mElement.getElement("[name='"+name+"']");
        text = item.get("html");
        if( this.options.selectValue && this.options.selectText ){
            value = MDomItem.Util.replaceText( text, this.options.selectText , this.options.selectValue , this.valSeparator );
        }else{
            value = text;
        }
        if( !value )value="";
        if( !text )text = value;
        if( vort == "value" )return value;
        if( vort == "text")return text;
        var result = {};
        result.value = value;
        result.text = text;
        return result;
    },
    setValue : function( value ){
        if( this.options.disable ){
            return;
        }
        value = MDomItem.Util.replaceText( value, this.options.selectValue , this.options.selectText, this.valSeparator  );
        value = value.join(",");
        var item = this.mElement.getElement("[name='"+ this.options.name + "']");
        item.set("html", value );
    },
    getErrorText : function(){
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}",this.options.text);
    },
    getClassName : function(){
        var tType = this.options.tType;
        var className = null ;
        if( this.options.className == "none" ){
        }else if( this.options.className != "") {
            className = this.options.className
        }else if( !this.options.isEdited ){
        }else {
        }
        return className;
    }
});

MDomItem.Img = new Class({
    initialize: function ( module ) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.items = module.items;
        this.container = this.mElement = module.container;
    },
    load : function(){
        if( this.options.disable )return;

        var item;
        var name = this.options.name;
        var value = this.options.value || this.options.defaultValue ;
        var event = this.options.event;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.container ;
        var className = this.getClassName();

        item = new Element( "img", {
            "name" : name,
            "src" : value
        });
        item.set( attr );
        if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
        item.setStyles( styles );

        MDomItem.Util.bindEvent( this, item, event);
        if(parent)item.inject(parent);
        this.items.push( item );
    },
    get : function( vort ){
        if( this.options.disable ){
            return ( vort == "value" || vort == "text" ) ? null : {
                value : null,
                text : null
            };
        }
        var value;
        var text;
        var name = this.options.name;
        var item = this.mElement.getElement("[name='"+name+"']");
        value = item.get("src");
        if( !value )value="";
        if( !text )text = value;
        if( vort == "value" )return value;
        if( vort == "text")return text;
        var result = {};
        result.value = value;
        result.text = text;
        return result;
    },
    setValue : function( value ){
        if( this.options.disable ){
            return;
        }
        var item = this.mElement.getElement("[name='"+ this.options.name + "']");
        item.set("src",value);
    },
    getErrorText : function(){
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}",this.options.text);
    },
    getClassName : function(){
        var tType = this.options.tType;
        var className = null ;
        if( this.options.className == "none" ){
        }else if( this.options.className != "") {
            className = this.options.className
        }else if( !this.options.isEdited ){
        }else {
        }
        return className;
    }
});

MDomItem.Button = new Class({
    initialize: function ( module ) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.items = module.items;
        this.container = this.mElement = module.container;
    },
    load : function(){
        if( this.options.disable )return;

        var item;
        var name = this.options.name;
        var value ;
        if( typeOf( this.options.value ) === "boolean" ){
            value = this.options.value.toString();
        }else{
            value = this.options.value || this.options.defaultValue
        }
        var event = this.options.event;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.container ;
        var className = this.getClassName() ;

        item = new Element( "button", {
            "type" : "button",
            "name" : name,
            "value" : value,
            "text" : value
        });
        item.set( attr );
        if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
        item.setStyles( styles );
        MDomItem.Util.bindEvent( this, item, event);
        if(parent)item.inject(parent);
        this.items.push( item );
    },
    get : function( vort ){
        if( this.options.disable ){
            return ( vort == "value" || vort == "text" ) ? null : {
                value : null,
                text : null
            };
        }
        var value;
        var text;
        var name = this.options.name;
        var item = this.mElement.getElement("[name='"+name+"']");
        value = item.get("value");
        if( !value )value="";
        if( !text )text = value;
        if( vort == "value" )return value;
        if( vort == "text")return text;
        var result = {};
        result.value = value;
        result.text = text;
        return result;
    },
    setValue : function( value ){
        if( this.options.disable ){
            return;
        }
        var item= this.mElement.getElement("[name='"+ this.options.name + "']");
        item.set( "value", value );
    },
    getErrorText : function(){
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}",this.options.text);
    },
    getClassName : function(){
        var className = null ;
        if( this.options.className == "none" ){
        }else if( this.options.className != "") {
            className = this.options.className
        }else if( !this.options.isEdited ){
        }else {
            className = "inputButton"
        }
        return className;
    }
});

MDomItem.A = new Class({
    initialize: function ( module ) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.items = module.items;
        this.container = this.mElement = module.container;
    },
    load : function(){
        if( this.options.disable )return;

        var item;
        var name = this.options.name;
        var value ;
        if( typeOf( this.options.value ) === "boolean" ){
            value = this.options.value.toString();
        }else{
            value = this.options.value || this.options.defaultValue
        }
        var event = this.options.event;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.container ;
        var className = this.getClassName() ;

        item = new Element( "a", {
            "name" : name,
            "value" : value,
            "text" : value
        });
        item.set( attr );
        if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
        item.setStyles( styles );
        MDomItem.Util.bindEvent( this, item, event);
        if(parent)item.inject(parent);
        this.items.push( item );
    },
    get : function( vort ){
        if( this.options.disable ){
            return ( vort == "value" || vort == "text" ) ? null : {
                value : null,
                text : null
            };
        }
        var value;
        var text;
        var name = this.options.name;
        var item = this.mElement.getElement("[name='"+name+"']");
        value = item.get("value");
        if( !value )value="";
        if( !text )text = value;
        if( vort == "value" )return value;
        if( vort == "text")return text;
        var result = {};
        result.value = value;
        result.text = text;
        return result;
    },
    setValue : function( value ){
        if( this.options.disable ){
            return;
        }
        var item= this.mElement.getElement("[name='"+ this.options.name + "']");
        item.set( "value", value );
    },
    getErrorText : function(){
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}",this.options.text);
    },
    getClassName : function(){
        var className = null ;
        if( this.options.className == "none" ){
        }else if( this.options.className != "") {
            className = this.options.className
        }else if( !this.options.isEdited ){
        }else {
            className = "inputA"
        }
        return className;
    }
});

MDomItem.MSelector = new Class({
    initialize: function ( module ) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
        this.valSeparator = module.valSeparator;
    },
    load : function(){
        if( this.options.disable )return;
        MWF.xDesktop.requireApp("Template", "MSelector",null,false);

        var value ;
        if( typeOf( this.options.value ) === "boolean" ){
            value = this.options.value.toString();
        }else{
            value = this.options.value || this.options.defaultValue
        }
        var selectValue = this.options.selectValue || this.options.selectText;
        var selectText = this.options.selectText || this.options.selectValue ;

        this.mSelectorOptions = {
            "style": "default",
            "width": "230px",
            "height": "30px",
            "defaultOptionLp" : MWF.xApplication.Template.LP.MDomItem.defaultOption,
            "trigger" : "delay", //immediately
            "isSetSelectedValue" : true,
            "inputEnable" : false,
            "isCreateReadNode" : false, //适应给MDomItem的做法

            "textField" : "",
            "valueField" : "",

            "value" : value,
            "text" : "",
            "defaultVaue" : this.options.defaultValue,
            "selectValue" : selectValue,
            "selectText" : selectText,

            "isEdited" : this.options.isEdited

            //"onSelectItem" : function( itemNode, itemData ){}.bind(this)
            //"onLoadData" :function( callback ){}.bind(this)
        };
        if( this.options.mSelectorOptions ){
            this.mSelectorOptions = Object.merge( this.mSelectorOptions, this.options.mSelectorOptions );
        }
        this.mSelectorOptions.value = value;

        if( !this.options.isEdited ){
            var name = this.options.name;
            var item;
            var attr = this.options.attr || {};
            var className = this.getClassName();
            var styles = this.options.style || {};
            var parent = this.container;
            this.mSelectorOptions.onLoadReadNode = function( text ){
                if( this.items.length > 0 ){
                    parent.empty();
                }
                item = new Element( "span", {
                    "name" : name,
                    "text" : text
                });
                item.set( attr );
                if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
                item.setStyles( styles );

                if(parent)item.inject(parent);
                this.items.push( item );
            }.bind(this);
        }

        this.mSelector = new MSelector(this.container, this.mSelectorOptions , this.app , this.css);
        this.mSelector.load();
    },
    get : function( vort ){
        if( this.options.disable ){
            return ( vort == "value" || vort == "text" ) ? null : {
                value : null,
                text : null
            };
        }
        if( vort == "value" )return this.mSelector.getValue();
        if( vort == "text")return this.mSelector.getText();
        return this.mSelector.get();
    },
    setValue : function( value ){
        this.mSelector.setValue( value );
    },
    getErrorText : function(){
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}",this.options.text);
    },
    getClassName : function(){
        var className = null ;
        if( this.options.className == "none" ){
        }else if( this.options.className != "") {
            className = this.options.className
        }else if( !this.options.isEdited ){
        }else {
        }
        return className;
    }
});

MDomItem.ImageClipper = new Class({
    initialize: function ( module ) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
        this.valSeparator = module.valSeparator;
    },
    load : function(){
        if( this.options.disable )return;
        if( this.options.isEdited ){
            this.loadEdit();
        }else{
            this.loadRead();
        }
    },
    loadEdit : function(){
        var item;
        var values;
        var name = this.options.name;
        var value ;
        if( typeOf( this.options.value ) === "boolean" ){
            value = this.options.value.toString();
        }else{
            value = this.options.value || this.options.defaultValue
        }
        var styles = this.options.style || {};
        var parent = this.container ;
        this.imageId = this.module.imageId = value;
        if( value && parent ){
            if( styles.imageWrapStyle ){
                this.imageWrap = new Element("div", { styles : styles.imageWrapStyle}).inject( parent )
            }
            this.image = new Element("img", {
                "src" : MWF.xDesktop.getImageSrc( value )
            }).inject( this.imageWrap || parent );
            this.image.addEvent("click",function(){
                window.open( o2.filterUrl(MWF.xDesktop.getImageSrc( this.imageId )), "_blank" );
            }.bind(this));
            if( styles.imageStyle )this.image.setStyles( styles.imageStyle );
        }
        var action = new Element("button",{
            "text" : MWF.xApplication.Template.LP.MDomItem.setPicture
        }).inject( parent );
        //if( this.css && this.css["inputButton"] )action.setStyles( this.css["inputButton"] );
        if( styles.actionStyle )action.setStyles( styles.actionStyle );
        action.addEvents({
            "click": function(){
                MWF.xDesktop.requireApp("Template", "widget.ImageClipper",null,false);
                this.clipper = new MWF.xApplication.Template.widget.ImageClipper(this.app, {
                    "imageUrl": value ? MWF.xDesktop.getImageSrc( value ) : "",
                    "aspectRatio": this.options.aspectRatio || 0,
                    "ratioAdjustedEnable" : this.options.ratioAdjustedEnable || false,
                    "reference": this.options.reference,
                    "referenceType": this.options.referenceType,
                    "onChange": function () {
                        if( this.image )this.image.destroy();
                        if(this.imageWrap)this.imageWrap.destroy();
                        if( styles.imageWrapStyle ){
                            this.imageWrap = new Element("div", { styles : styles.imageWrapStyle}).inject( parent, "top" )
                        }
                        this.image = new Element("img", {
                            "src" : this.clipper.imageSrc
                        }).inject( this.imageWrap || parent, "top" );
                        if( styles.imageStyle )this.image.setStyles( styles.imageStyle );
                        this.image.addEvent("click",function(){
                            window.open( o2.filterUrl(MWF.xDesktop.getImageSrc( this.imageId )), "_blank" );
                        }.bind(this));
                        this.imageId = this.module.imageId = this.clipper.imageId;
                        if( this.options.validImmediately ){
                            this.module.verify( true )
                        }
                    }.bind(this)
                });
                this.clipper.load();
            }.bind(this)
        });
    },
    loadRead : function(){
        var value = this.options.value || this.options.defaultValue ;
        var parent = this.container ;
        this.imageId = this.module.imageId = value;
        if( value && parent ){
            this.image = new Element("img", {
                "src" : MWF.xDesktop.getImageSrc( value )
            }).inject( parent );
            var styles = this.options.style || {};
            if( styles.imageStyle )this.image.setStyles( styles.imageStyle );
        }
    },
    get : function( vort ){
        if( this.options.disable ){
            return ( vort == "value" || vort == "text" ) ? null : {
                value : null,
                text : null
            };
        }
        var items;
        var value = this.imageId;
        if( vort == "value" )return value;
        if( vort == "text")return value;
        var result = {};
        result.value = value;
        result.text = value;
        return result;
    },
    setValue : function( value ){
        if( this.options.disable ){
            return;
        }
        var styles = this.options.style || {};
        this.imageId = this.module.imageId = value;
        if( value ){
            if( this.image ){
                this.image.set("src", MWF.xDesktop.getImageSrc( value ))
            }else{
                if( styles.imageWrapStyle ){
                    this.imageWrap = new Element("div", { styles : styles.imageWrapStyle}).inject( this.container )
                }
                this.image = new Element("img", {
                    "src" : MWF.xDesktop.getImageSrc( value )
                }).inject( this.imageWrap || this.container );
                if( styles.imageStyle )this.image.setStyles( styles.imageStyle );
            }
        }
    },
    getErrorText : function(){
        return MWF.xApplication.Template.LP.MDomItem.uploadPictureNotice+"："+this.options.text ;
    }
});

MDomItem.Rtf = new Class({
    initialize: function ( module ) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
        this.valSeparator = module.valSeparator;
    },
    load : function(){
        if( this.options.disable )return;
        if( this.options.isEdited ){
            this.loadEdit();
        }else{
            this.loadRead();
        }
    },
    loadEdit : function(){
        var _self = this;
        var item;
        var name = this.options.name;
        var value = this.options.value || this.options.defaultValue ;
        var attr = this.options.attr || {};
        var parent = this.container ;
        COMMON.AjaxModule.load("ckeditor", function(){
            CKEDITOR.disableAutoInline = true;
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
                "readOnly": false,
                "language": MWF.language || "zh-cn",
                "extraAllowedContent " : "img[onerror,data-id]"
            };
            if( this.options.RTFConfig ){
                editorConfig = Object.merge( editorConfig, this.options.RTFConfig )
            }
            if( editorConfig.skin )editorConfig.skin = "moono-lisa";
            if( !editorConfig.filebrowserFilesImage && !editorConfig.cloudFileDisable ){
                editorConfig.filebrowserFilesImage = function( e, callback ){
                    MWF.xDesktop.requireApp("File", "FileSelector", function(){
                        _self.selector_cloud = new MWF.xApplication.File.FileSelector( document.body ,{
                            "style" : "default",
                            "title": MWF.xApplication.Template.LP.MDomItem.selectCoundPicture,
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
            this.editor = this.module.editor = CKEDITOR.replace(item, editorConfig);

            var imgSrc = MWF.xDesktop.getImageSrc();
            var imgHost = imgSrc.split("/x_file_assemble_control/")[0];
            debugger;
            this.editor.on("instanceReady", function(e){
                debugger;
                var editable = e.editor.editable && e.editor.editable();
                if(!editable)return;
               var imgs = editable.find("img");
                for( var i=0; i<imgs.count(); i++ ){
                    var img = imgs.getItem(i);
                    var src = img.getAttribute("src");
                    if( src && src.indexOf("/x_file_assemble_control/") > -1 ){
                        if( imgHost !== src.split("/x_file_assemble_control/")[0] ){
                            var id = img.getAttribute("data-id");
                            if( id ){
                                var newSrc = MWF.xDesktop.getImageSrc(id);
                                if(newSrc){
                                    img.setAttribute("src" , newSrc );
                                    img.setAttribute("data-cke-saved-src" , newSrc );
                                }
                            }
                        }
                    }
                }
            });
            this.items.push( this.editor );
        }.bind(this));
    },
    loadRead : function(){var _self = this;
        var item;
        var name = this.options.name;
        var value = this.options.value || this.options.defaultValue ;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.container ;
        var className = null ;
        item = new Element( "span", {
            "name" : name,
            "html" : value
        });
        item.set( attr );
        if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
        item.setStyles( styles );

        if(parent)item.inject(parent);
        this.items.push( item );
    },
    get : function( vort ){
        if( this.options.disable ){
            return ( vort == "value" || vort == "text" ) ? null : {
                value : null,
                text : null
            };
        }
        var value;
        var text;
       if( this.options.isEdited ){
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
       }else{
           var item = this.mElement.getElement("[name='"+name+"']");
           value = item.get("html");
       }
        if( !value )value="";
        if( !text )text = value;
        if( vort == "value" )return value;
        if( vort == "text")return text;
        var result = {};
        result.value = value;
        result.text = text;
        return result;
    },
    setValue : function( value ){
        if( this.options.disable ){
            return;
        }
        if( this.options.isEdited ){
            this.editor.setData(value);
        }else{
            var item = this.mElement.getElement("[name='"+ this.options.name + "']");
            item.set("html", value );
        }
    },
    getErrorText : function(){
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}",this.options.text);
    },
    getClassName : function(){
        var className = null ;
        if( this.options.className == "none" ){
        }else if( this.options.className != "") {
            className = this.options.className
        }else if( !this.options.isEdited ){
        }else {
        }
        return className;
    }
});

MDomItem.Org = new Class({
    initialize: function ( module ) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
        this.valSeparator = module.valSeparator;
    },
    load : function(){
        if( this.options.disable )return;
        if( this.options.isEdited ){
            this.loadEdit();
        }else{
            this.loadRead();
        }
    },
    loadEdit : function(){
        var item;
        var name = this.options.name;
        var value = this.options.value || this.options.defaultValue ;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent =  this.container ;
        var className = this.getClassName() ;
        if( !value ){
            this.orgData = [];
        }else if( typeOf( value ) == "array" ){
            this.orgData = value;
        }else if( typeOf( value ) == "string" ){
            this.orgData = value.split( this.valSeparator )
        }else if( typeOf( value ) == "object" ){
            this.orgData = [value]
        }else{
            this.orgData = [];
        }
        item = new Element( "div", {
            "name" : name
        });
        item.set( attr );
        if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
        item.setStyles( styles );
        this.loadOrgWidget( this.orgData, item, true );
        this.bindDefaultEvent( item );
        MDomItem.Util.bindEvent( this,  item, this.options.event );
        if(parent)item.inject(parent);
        this.items.push( item );
    },
    loadRead : function(){
        var item;
        var name = this.options.name;
        var value  = this.options.value || this.options.defaultValue;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent =  this.container ;
        var className = this.getClassName();
        if( !value ){
            this.orgData = [];
        }else if( typeOf( value ) == "array" ){
            this.orgData = value;
        }else if( typeOf( value ) == "string" ){
            this.orgData = value.split( this.valSeparator )
        }else{
            this.orgData = [];
        }
        this.module.orgData = this.orgData;
        item = new Element( "div", {
            "name" : name
        });
        item.set( attr );
        if( className && this.css && this.css[className] )item.setStyles( this.css[className] );
        item.setStyles( styles );
        this.loadOrgWidget( this.orgData, item , false);

        if(parent)item.inject(parent);
        this.items.push( item );
    },
    getData : function( parse ){
        var data = [];
        this.OrgWidgetList.each( function( widget ){
            data.push( parse ? MWF.org.parseOrgData(widget.data, true) : widget.data );
        }.bind(this));
        return data;
    },
    get : function( vort ){
        if( this.options.disable ){
            return ( vort == "value" || vort == "text" ) ? null : {
                value : null,
                text : null
            };
        }
        var value = this.orgData;
        if( vort == "value" )return value;
        if( vort == "text")return value;
        var result = {};
        result.value = value;
        result.text = value;
        return result;
    },
    setValue : function( value ){
        if( this.options.disable ){
            return;
        }
        var item = this.mElement.getElement("[name='"+ this.options.name + "']");
        if( !value ){
            this.orgData = [];
        }else if( typeOf( value ) == "array" ){
            this.orgData = value;
        }else if( typeOf( value ) == "string" ){
            this.orgData = value.split( this.valSeparator );
        }else{
            this.orgData = [];
        }
        item.empty();
        this.loadOrgWidget( this.orgData, item, this.options.isEdited );
        this.module.orgData = this.orgData
    },
    getErrorText : function(){
        return MWF.xApplication.Template.LP.MDomItem.selectTip.replace("{text}",this.options.text);
    },
    bindDefaultEvent : function( item ){
        if( this.options.unsetDefaultEvent )return;
        item.addEvent( "click" , function( ev ){
            debugger;
            this.module.fireEvent("querySelect", this.module );
            var options = this.options;
            var opt = {
                type : options.orgType,
                title : options.text,
                count : options.count,
                selectedValues : this.orgObjData || this.orgData,
                units : options.units,
                unitType : options.unitType,
                groups : options.groups,
                expand : options.expand,
                exclude : options.exclude,
                expandSubEnable : options.expandSubEnable
            };
            MDomItem.Util.selectPerson( this.app.content, opt, function( array ){
                item.empty();
                this.orgData = this.module.orgData = [];
                this.orgObjData = [];
                this.orgObject = this.module.orgObject = array;
                array.each(function( it ){
                    this.orgData.push( it.data.distinguishedName || it.data.name );
                    this.orgObjData.push( it.data );
                }.bind(this));
                this.OrgWidgetList = [];
                this.loadOrgWidget( this.orgObjData, item, true );
                this.modified = true;
                this.items[0].fireEvent("change", [this.module, ev]);
                if( this.options.validImmediately )this.module.verify( true );
            }.bind(this))
        }.bind(this) );
    },
    getValueByType : function( type ){
        var types = typeOf( type ) == "string" ? type.split(",") : type;
        types = types.map(  function( item, index ){
            switch (item.toLowerCase()) {
                case "person":  return "p";
                case "identity": return "i";
                case "unit": return "u";
                case "group": return "g";
                case "role":  return "r";
                default: return item.toLowerCase();
            }
        });
        var value = [];
        this.get("value").each( function( v ){
            var flag = v.substr(v.length-1, 1);
            if( types.contains( flag.toLowerCase() ) )value.push( v );
        });
        return value;
    },
    loadOrgWidget: function(value, node, canRemove){
        this.OrgWidgetList = this.OrgWidgetList || [];
        MWF.require("MWF.widget.O2Identity", null, false);
        var options = { "style": this.options.orgStyle || "xform", "canRemove": canRemove , "onRemove" : this.removeOrgItem };
        if( this.options.orgWidgetOptions ){
            options = Object.merge( options, this.options.orgWidgetOptions );
        }
        value.each(function( v ){
            var distinguishedName;
            if( typeOf(v) === "string" ){
                distinguishedName = v;
            }else{
                distinguishedName = v.distinguishedName || v.name || ""
            }
            var flag = distinguishedName.substr(distinguishedName.length-1, 1);
            var data = { "name" : distinguishedName };
            switch (flag.toLowerCase()){
                case "i":
                    var widget = new MWF.widget.O2Identity( data, node, options );
                    break;
                case "p":
                    var widget = new MWF.widget.O2Person(data, node, options);
                    break;
                case "u":
                    var widget = new MWF.widget.O2Unit(data, node, options);
                    break;
                case "g":
                    var widget = new MWF.widget.O2Group(data, node, options);
                    break;
                //case "d":
                //    var widget = new MWF.widget.O2Duty(data, node, options);
                //    break;
                default:
                    var orgType = this.options.orgType;
                    var t = ( typeOf( orgType ) == "array" && orgType.length == 1 ) ? orgType[0] : orgType;
                    t = typeOf( t ) == "string" ? t.toLowerCase() : "";
                    if( t == "identity" ){
                        var widget = new MWF.widget.O2Identity( data, node, options );
                    }else if( t == "person" ){
                        var widget = new MWF.widget.O2Person(data, node, options);
                    }else if( t == "unit" ){
                        var widget = new MWF.widget.O2Unit(data, node, options);
                    }else if( t == "group" ){
                        var widget = new MWF.widget.O2Group(data, node, options);
                    }else if( t == "process" ){
                        var d = { id : distinguishedName };
                        var widget = new MWF.widget.O2Process(d, node, options);
                        //}else if( t == "duty" ){
                        //    var widget = new MWF.widget.O2Duty(data, node, options);
                    }else if( t == "CMSView" ){
                        var d = { id : distinguishedName };
                        var widget = new MWF.widget.O2CMSView(d, node, options);
                        //}else if( t == "duty" ){
                        //    var widget = new MWF.widget.O2Duty(data, node, options);
                    }else{
                        var widget = new MWF.widget.O2Other( data, node, options);
                    }
            }
            widget.field = this;
            this.OrgWidgetList.push( widget );
        }.bind(this));
    },
    removeOrgItem : function( widget, ev ){
        //this 是 MWF.widget.O2Identity 之类的对象
        var _self = this.field; //这个才是MDomItem 对象
        var dn = widget.data.distinguishedName || widget.data.name;
        var data = [];
        var index;
        _self.orgData.each( function ( d , i){
            if( d != dn )data.push( d )
        });
        _self.orgData = data;

        if( _self.orgObject ){
            data = [];
            _self.orgObject.each( function( d ){
                if( d.distinguishedName ){
                    if( d.distinguishedName != dn )data.push( d );
                }else{
                    if( d.name != dn )data.push( d );
                }
            });
            _self.orgObject = data;
        }
        this.node.destroy();
        _self.items[0].fireEvent("change");
        ev.stopPropagation();
    },
    getClassName : function(){
        var className = null ;
        if( this.options.className == "none" ){
        }else if( this.options.className != "") {
            className = this.options.className
        }else if( !this.options.isEdited ){
        }else {
            className = "inputPerson"
        }
        return className;
    }
});

MDomItem.File = new Class({
    initialize: function ( module ) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
        this.valSeparator = module.valSeparator;
    },
    load : function(){
        if( this.options.disable )return;
        if( this.options.isEdited ){
            this.loadEdit();
        }else{
            this.loadRead();
        }
    },
    loadEdit : function(){

    },
    loadRead : function(){

    },
    get : function( vort ){

    },
    setValue : function( value ){

    },
    getErrorText : function(){

    }
});
