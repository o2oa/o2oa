MWF.require("MWF.widget.Common", null, false);
MWF.xApplication.process.Xform.$Module = MWF.APP$Module =  new Class({
    Implements: [Events],
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"]
    },

    initialize: function(node, json, form, options){

        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
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
    hide: function(){
        var dsp = this.node.getStyle("display");
        if (dsp!=="none") this.node.store("mwf_display", dsp);
        this.node.setStyle("display", "none");
        if (this.iconNode) this.iconNode.setStyle("display", "none");
    },
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
        if (this.json.section=="yes"){
            return this._getBusinessSectionData();
        }else {
            if (this.json.type==="Opinion"){
                return this._getBusinessSectionDataByPerson();
            }else{
                return this.form.businessData.data[this.json.id] || "";
            }
        }
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
                return this._getBusinessSectionDataByScript(this.json.sectionByScript.code);
            default:
                return this.form.businessData.data[this.json.id] || "";
        }
    },
    _getBusinessSectionDataByPerson: function(){
        this.form.sectionListObj[this.json.id] = layout.desktop.session.user.id;
        var dataObj = this.form.businessData.data[this.json.id];
        return (dataObj) ? (dataObj[layout.desktop.session.user.id] || "") : "";
    },
    _getBusinessSectionDataByUnit: function(){
        this.form.sectionListObj[this.json.id] = "";
        var dataObj = this.form.businessData.data[this.json.id];
        if (!dataObj) return "";
        var key = (this.form.businessData.task) ? this.form.businessData.task.unit : "";
        if (key) this.form.sectionListObj[this.json.id] = key;
        return (key) ? (dataObj[key] || "") : "";
    },
    _getBusinessSectionDataByActivity: function(){
        this.form.sectionListObj[this.json.id] = "";
        var dataObj = this.form.businessData.data[this.json.id];
        if (!dataObj) return "";
        var key = (this.form.businessData.work) ? this.form.businessData.work.activity : "";
        if (key) this.form.sectionListObj[this.json.id] = key;
        return (key) ? (dataObj[key] || "") : "";
    },
    _getBusinessSectionDataBySplitValue: function(){
        this.form.sectionListObj[this.json.id] = "";
        var dataObj = this.form.businessData.data[this.json.id];
        if (!dataObj) return "";
        var key = (this.form.businessData.work) ? this.form.businessData.work.splitValue : "";
        if (key) this.form.sectionListObj[this.json.id] = key;
        return (key) ? (dataObj[key] || "") : "";
    },
    _getBusinessSectionDataByScript: function(code){
        this.form.sectionListObj[this.json.id] = "";
        var dataObj = this.form.businessData.data[this.json.id];
        if (!dataObj) return "";
        var key = this.form.Macro.exec(code, this);
        if (key) this.form.sectionListObj[this.json.id] = key;
        return (key) ? (dataObj[key] || "") : "";
    },

    _setBusinessData: function(v){
        if (this.json.section=="yes"){
            this._setBusinessSectionData(v);
        }else {
            if (this.json.type==="Opinion"){
                this._setBusinessSectionDataByPerson(v);
            }else{
                if (this.form.businessData.data[this.json.id]){
                    this.form.businessData.data[this.json.id] = v;
                }else{
                    this.form.businessData.data[this.json.id] = v;
                    this.form.Macro.environment.setData(this.form.businessData.data);
                }
                if (this.json.isTitle) this.form.businessData.work.title = v;
            }
        }
    },
    _setBusinessSectionData: function(v){
        debugger;
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
                    this.form.businessData.data[this.json.id] = v;
                }else{
                    this.form.businessData.data[this.json.id] = v;
                    this.form.Macro.environment.setData(this.form.businessData.data);
                }
        }
    },
    _setBusinessSectionDataByPerson: function(v){
        var resetData = false;
        var key = layout.desktop.session.user.id;

        var dataObj = this.form.businessData.data[this.json.id];
        if (!dataObj){
            dataObj = {};
            this.form.businessData.data[this.json.id] = dataObj;
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
            var dataObj = this.form.businessData.data[this.json.id];
            if (!dataObj){
                dataObj = {};
                this.form.businessData.data[this.json.id] = dataObj;
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
            var dataObj = this.form.businessData.data[this.json.id];
            if (!dataObj){
                dataObj = {};
                this.form.businessData.data[this.json.id] = dataObj;
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
            var dataObj = this.form.businessData.data[this.json.id];
            if (!dataObj){
                dataObj = {};
                this.form.businessData.data[this.json.id] = dataObj;
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
            var dataObj = this.form.businessData.data[this.json.id];
            if (!dataObj){
                dataObj = {};
                this.form.businessData.data[this.json.id] = dataObj;
                resetData = true;
            }
            if (!dataObj[key]) resetData = true;
            dataObj[key] = v;
        }

        if (resetData) this.form.Macro.environment.setData(this.form.businessData.data);
    },

    _queryLoaded: function(){},
    _afterLoaded: function(){},

    setValue: function(){
    },
    focus: function(){
        this.node.focus();
    }

});
