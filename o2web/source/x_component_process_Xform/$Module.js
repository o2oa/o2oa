MWF.require("MWF.widget.Common", null, false);
/** @classdesc $Module 组件类，此类为所有组件的父类。
 * @class
 * @o2category FormComponents
 * @hideconstructor
 * */
MWF.xApplication.process.Xform.$Module = MWF.APP$Module =  new Class(
    /** @lends MWF.xApplication.process.Xform.$Module# */
    {
    Implements: [Events],
    options: {
        /**
         * 组件加载前触发。
         * @event MWF.xApplication.process.Xform.$Module#queryLoad
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 组件加载时触发.
         * @event MWF.xApplication.process.Xform.$Module#load
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 组件加载后触发.
         * @event MWF.xApplication.process.Xform.$Module#postLoad
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        "moduleEvents": ["load", "queryLoad", "postLoad"]
    },
    initialize: function(node, json, form, options){
        /**
         * @summary 组件的节点，mootools封装过的Dom对象，可以直接使用原生的js和moootools方法访问和操作该对象。
         * @see https://mootools.net/core/docs/1.6.0/Element/Element
         * @member {Element}
         * @example
         *  //可以在脚本中获取该组件
         * var field = this.form.get("fieldId"); //获取组件对象
         * field.node.setStyle("font-size","12px"); //给节点设置样式
         */
        this.node = $(node);
        this.node.store("module", this);

        /**
         * @summary 组件的配置信息，比如id,类型,是否只读等等。可以在组件的queryLoad事件里修改该配置来对组件做一些改变。
         * @member {JsonObject}
         * @example
         *  //可以在脚本中获取该组件
         * var json = this.form.get("fieldId").json; //获取组件对象
         * var id = json.id; //获取组件的id
         * var type = json.type; //获取组件的类型，如Textfield 为文本输入组件，Select为下拉组件
         * json.isReadonly = true; //设置组件为只读。
         */
        this.json = json;

        /**
         * @summary 组件的所在表单对象.
         * @member {MWF.xApplication.process.Xform.Form}
         * @example
         * var form = this.form.get("fieldId").form; //获取组件所在表单对象
         * var container = form.container; //获取表单容器
         */
        this.form = form;
    },
    _getSource: function(){
        var parent = this.node.getParent();
        while(parent && (
            parent.get("MWFtype")!="source" &&
            parent.get("MWFtype")!="subSource" &&
            parent.get("MWFtype")!="subSourceItem"
        )) parent = parent.getParent();
        return (parent) ? parent.retrieve("module") : null;
    },
    /**
     * @summary 隐藏组件.
     * @example
     * this.form.get("fieldId").hide(); //隐藏组件
     */
    hide: function(){
        var dsp = this.node.getStyle("display");
        if (dsp!=="none") this.node.store("mwf_display", dsp);
        this.node.setStyle("display", "none");
        if (this.iconNode) this.iconNode.setStyle("display", "none");
    },
    /**
     * @summary 显示组件.
     * @example
     * this.form.get("fieldId").show(); //显示组件
     */
    show: function(){
        var dsp = this.node.retrieve("mwf_display", dsp);
        this.node.setStyle("display", dsp);
        if (this.iconNode) this.iconNode.setStyle("display", "block");
    },
    load: function(){
        this._loadModuleEvents();
        if (this.fireEvent("queryLoad")){
            this._queryLoaded();
            this._loadUserInterface();
            this._loadStyles();
            this._loadDomEvents();
            //this._loadEvents();

            this._afterLoaded();
            this.fireEvent("postLoad");
            this.fireEvent("load");
        }
    },
    _loadUserInterface: function(){
        //	this.node = this.node;
    },

    _loadStyles: function(){
        if (this.json.styles) Object.each(this.json.styles, function(value, key){
            if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1 || value.indexOf("x_cms_assemble_control")!=-1)){
                var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
                var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
                var host3 = MWF.Actions.getHost("x_cms_assemble_control");
                if (value.indexOf("/x_processplatform_assemble_surface")!==-1){
                    value = value.replace("/x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
                }else if (value.indexOf("x_processplatform_assemble_surface")!==-1){
                    value = value.replace("x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
                }
                if (value.indexOf("/x_portal_assemble_surface")!==-1){
                    value = value.replace("/x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }else if (value.indexOf("x_portal_assemble_surface")!==-1){
                    value = value.replace("x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }
                if (value.indexOf("/x_cms_assemble_control")!==-1){
                    value = value.replace("/x_cms_assemble_control", host3+"/x_cms_assemble_control");
                }else if (value.indexOf("x_cms_assemble_control")!==-1){
                    value = value.replace("x_cms_assemble_control", host3+"/x_cms_assemble_control");
                }
                value = o2.filterUrl(value);
            }
            this.node.setStyle(key, value);
        }.bind(this));

        // if (["x_processplatform_assemble_surface", "x_portal_assemble_surface"].indexOf(root.toLowerCase())!==-1){
        //     var host = MWF.Actions.getHost(root);
        //     return (flag==="/") ? host+this.json.template : host+"/"+this.json.template
        // }
        //if (this.json.styles) this.node.setStyles(this.json.styles);
    },
    _loadModuleEvents : function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)!==-1){
                    this.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    _loadDomEvents: function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)===-1){
                    this.node.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    _loadEvents: function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)!==-1){
                    this.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }else{
                    this.node.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    addModuleEvent: function(key, fun){
        if (this.options.moduleEvents.indexOf(key)!==-1){
            this.addEvent(key, function(event){
                return (fun) ? fun(this, event) : null;
            }.bind(this));
        }else{
            this.node.addEvent(key, function(event){
                return (fun) ? fun(this, event) : null;
            }.bind(this));
        }
    },
    _getBusinessData: function(){
        var v;
        if (this.json.section=="yes"){
            v = this._getBusinessSectionData();
        }else {
            if (this.json.type==="Opinion"){
                v = this._getBusinessSectionDataByPerson();
            }else{
                // return this.form.businessData.data[this.json.id] || "";
                return this.getBusinessDataById() || "";
            }
        }
        //if (o2.typeOf(v)==="string") v = o2.dtxt(v);
        return v;
    },
    _getBusinessSectionData: function(){
        switch (this.json.sectionBy){
            case "person":
                return this._getBusinessSectionDataByPerson();
            case "unit":
                return this._getBusinessSectionDataByUnit();
            case "activity":
                return this._getBusinessSectionDataByActivity();
            case "splitValue":
                return this._getBusinessSectionDataBySplitValue();
            case "script":
                return this._getBusinessSectionDataByScript(((this.json.sectionByScript) ? this.json.sectionByScript.code : ""));
            default:
                // return this.form.businessData.data[this.json.id] || "";
                return this.getBusinessDataById() || "";
        }
    },
    _getBusinessSectionDataByPerson: function(){
        this.form.sectionListObj[this.json.id] = layout.desktop.session.user.id;
        // var dataObj = this.form.businessData.data[this.json.id];
        var dataObj = this.getBusinessDataById();
        return (dataObj) ? (dataObj[layout.desktop.session.user.id] || "") : "";
    },
    _getBusinessSectionDataByUnit: function(){
        this.form.sectionListObj[this.json.id] = "";
        // var dataObj = this.form.businessData.data[this.json.id];
        var dataObj = this.getBusinessDataById();
        if (!dataObj) return "";
        var key = (this.form.businessData.task) ? this.form.businessData.task.unit : "";
        if (key) this.form.sectionListObj[this.json.id] = key;
        return (key) ? (dataObj[key] || "") : "";
    },
    _getBusinessSectionDataByActivity: function(){
        this.form.sectionListObj[this.json.id] = "";
        // var dataObj = this.form.businessData.data[this.json.id];
        var dataObj = this.getBusinessDataById();
        if (!dataObj) return "";
        var key = (this.form.businessData.work) ? this.form.businessData.work.activity : "";
        if (key) this.form.sectionListObj[this.json.id] = key;
        return (key) ? (dataObj[key] || "") : "";
    },
    _getBusinessSectionDataBySplitValue: function(){
        this.form.sectionListObj[this.json.id] = "";
        // var dataObj = this.form.businessData.data[this.json.id];
        var dataObj = this.getBusinessDataById();
        if (!dataObj) return "";
        var key = (this.form.businessData.work) ? this.form.businessData.work.splitValue : "";
        if (key) this.form.sectionListObj[this.json.id] = key;
        return (key) ? (dataObj[key] || "") : "";
    },
    _getBusinessSectionDataByScript: function(code){
        this.form.sectionListObj[this.json.id] = "";
        // var dataObj = this.form.businessData.data[this.json.id];
        var dataObj = this.getBusinessDataById();
        if (!dataObj) return "";
        var key = this.form.Macro.exec(code, this);
        if (key) this.form.sectionListObj[this.json.id] = key;
        return (key) ? (dataObj[key] || "") : "";
    },

    _setBusinessData: function(v){
        //if (o2.typeOf(v)==="string") v = o2.txt(v);
        if (this.json.section=="yes"){
            this._setBusinessSectionData(v);
        }else {
            if (this.json.type==="Opinion"){
                this._setBusinessSectionDataByPerson(v);
            }else{
                if (this.form.businessData.data[this.json.id]){
                    // this.form.businessData.data[this.json.id] = v;
                    this.setBusinessDataById(v);
                }else{
                    // this.form.businessData.data[this.json.id] = v;
                    this.setBusinessDataById(v);
                    this.form.Macro.environment.setData(this.form.businessData.data);
                }
                if (this.json.isTitle) this.form.businessData.work.title = v;
            }
        }
    },
    _setBusinessSectionData: function(v){
        switch (this.json.sectionBy){
            case "person":
                this._setBusinessSectionDataByPerson(v);
                break;
            case "unit":
                this._setBusinessSectionDataByUnit(v);
                break;
            case "activity":
                this._setBusinessSectionDataByActivity(v);
                break;
            case "splitValue":
                this._setBusinessSectionDataBySplitValue(v);
                break;
            case "script":
                this._setBusinessSectionDataByScript(this.json.sectionByScript.code, v);
                break;
            default:
                if (this.form.businessData.data[this.json.id]){
                    // this.form.businessData.data[this.json.id] = v;
                    this.setBusinessDataById(v)
                }else{
                    // this.form.businessData.data[this.json.id] = v;
                    this.setBusinessDataById(v);
                    this.form.Macro.environment.setData(this.form.businessData.data);
                }
        }
    },
    _setBusinessSectionDataByPerson: function(v){
        var resetData = false;
        var key = layout.desktop.session.user.id;

        // var dataObj = this.form.businessData.data[this.json.id];
        var dataObj = this.getBusinessDataById();
        if (!dataObj){
            dataObj = {};
            // this.form.businessData.data[this.json.id] = dataObj;
            this.setBusinessDataById(dataObj);
            resetData = true;
        }
        if (!dataObj[key]) resetData = true;
        dataObj[key] = v;

        if (resetData) this.form.Macro.environment.setData(this.form.businessData.data);
    },
    _setBusinessSectionDataByUnit: function(v){
        var resetData = false;
        var key = (this.form.businessData.task) ? this.form.businessData.task.unit : "";

        if (key){
            // var dataObj = this.form.businessData.data[this.json.id];
            var dataObj = this.getBusinessDataById();
            if (!dataObj){
                dataObj = {};
                // this.form.businessData.data[this.json.id] = dataObj;
                this.setBusinessDataById(dataObj);
                resetData = true;
            }
            if (!dataObj[key]) resetData = true;
            dataObj[key] = v;
        }

        if (resetData) this.form.Macro.environment.setData(this.form.businessData.data);
    },
    _setBusinessSectionDataByActivity: function(v){
        var resetData = false;
        var key = (this.form.businessData.work) ? this.form.businessData.work.activity : "";

        if (key){
            // var dataObj = this.form.businessData.data[this.json.id];
            var dataObj = this.getBusinessDataById();
            if (!dataObj){
                dataObj = {};
                // this.form.businessData.data[this.json.id] = dataObj;
                this.setBusinessDataById(dataObj);
                resetData = true;
            }
            if (!dataObj[key]) resetData = true;
            dataObj[key] = v;
        }

        if (resetData) this.form.Macro.environment.setData(this.form.businessData.data);
    },
    _setBusinessSectionDataBySplitValue: function(v){
        var resetData = false;
        var key = (this.form.businessData.work) ? this.form.businessData.work.splitValue : "";

        if (key){
            // var dataObj = this.form.businessData.data[this.json.id];
            var dataObj = this.getBusinessDataById();
            if (!dataObj){
                dataObj = {};
                // this.form.businessData.data[this.json.id] = dataObj;
                this.setBusinessDataById(dataObj);
                resetData = true;
            }
            if (!dataObj[key]) resetData = true;
            dataObj[key] = v;
        }

        if (resetData) this.form.Macro.environment.setData(this.form.businessData.data);
    },

    _setBusinessSectionDataByScript: function(code, v){
        var resetData = false;
        var key = this.form.Macro.exec(code, this);

        if (key){
            // var dataObj = this.form.businessData.data[this.json.id];
            var dataObj = this.getBusinessDataById();
            if (!dataObj){
                dataObj = {};
                // this.form.businessData.data[this.json.id] = dataObj;
                this.setBusinessDataById(dataObj);
                resetData = true;
            }
            if (!dataObj[key]) resetData = true;
            dataObj[key] = v;
        }

        if (resetData) this.form.Macro.environment.setData(this.form.businessData.data);
    },
    getBusinessDataById: function(){
        //对id类似于 xx..0..xx 的字段进行拆分
        if(this.json.id.indexOf("..") < 1){
            return this.form.businessData.data[this.json.id];
        }else{
            var idList = this.json.id.split("..");
            idList = idList.map( function(d){ return d.test(/^\d+$/) ? d.toInt() : d; });

            var data = this.form.businessData.data;
            var lastIndex = idList.length - 1;

            for(var i=0; i<=lastIndex; i++){
                var id = idList[i];
                if( !id && id !== 0 )return null;
                if( ["object","array"].contains(o2.typeOf(data)) ){
                    if( i === lastIndex ){
                        return data[id];
                    }else{
                        data = data[id];
                    }
                }else{
                    return null;
                }
            }
        }
    },
    setBusinessDataById: function(v){
        //对id类似于 xx..0..xx 的字段进行拆分
        if(this.json.id.indexOf("..") < 1){
            this.form.businessData.data[this.json.id] = v;
        }else{
            var idList = this.json.id.split("..");
            idList = idList.map( function(d){ return d.test(/^\d+$/) ? d.toInt() : d; });

            var data = this.form.businessData.data;
            var lastIndex = idList.length - 1;

            for(var i=0; i<=lastIndex; i++){
                var id = idList[i];
                if( !id && id !== 0 )return;

                if( i === lastIndex ){
                    data[id] = v;
                }else{
                    var nexId = idList[i+1];
                    if(o2.typeOf(nexId) === "number"){ //下一个ID是数字
                        if( !data[id] && o2.typeOf(data[id]) !== "array" )data[id] = [];
                        if( nexId > data[id].length ){ //超过了最大下标，丢弃
                            return;
                        }
                    }else{ //下一个ID是字符串
                        if( !data[id] || o2.typeOf(data[id]) !== "object")data[id] = {};
                    }
                    data = data[id];
                }
            }
        }
    },

    _queryLoaded: function(){},
    _afterLoaded: function(){},

    setValue: function(){
    },
    focus: function(){
        this.node.focus();
    },

    _getModuleByPath: function( path ){
        /*
        注: 系统的数据中允许多层路径，id上通过..来区分层次：
        1、单层或者是最外层，填"fieldId"，表示表单上的直接组件。
        2、如果有多层数据模板，"./fieldId"表示和当前组件id同层次的组件，"../fieldId"表示和上一层组件同层次的组件，以此类推。
        3、如果有多层数据模板，也可通过"datatemplateId.*.datatemplateId2.*.fieldId"来表示全层次路径。datatemplateId表示第一层数据模板的id，datatemplateId2表示第二层的id。
         */
        if(!path)return;
        var idList = this.json.id.split("..");
        if( path.contains("*") ){ //允许path中包含*，替代当前path的层次
            var paths = path.split(".");
            for( var i=0; i<paths.length; i++ ){
                if( paths[i].contains("*") && idList[i] ){
                    var key = paths[i].replace("*", idList[i]);
                    key = this.form.Macro.exec("return "+key, this);
                    paths[i] = (key||"").toString();
                }
            }
            path = paths.join("..");
        }else if( path.contains("./") ){

            var lastName = path.substring(path.indexOf("./")+2, path.length);
            var level = (path.substring(0, path.indexOf("./"))+".").split(".").length-1; // /前面有几个.

            var idList_copy = Array.clone(idList);
            if( idList_copy.length > level*2 ){
                for( var i=0; i<level; i++ ){
                    idList_copy.pop();
                    if( i > 0)idList_copy.pop();
                }
                path = idList_copy.join("..")+".."+lastName;
            }else{
                idList_copy[idList_copy.length-1] = lastName;
                path = idList_copy.join("..")
            }
        }

        return this.form.all[path];
    }

});
