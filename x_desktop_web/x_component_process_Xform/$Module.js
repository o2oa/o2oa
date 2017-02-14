MWF.require("MWF.widget.Common", null, false);
MWF.xApplication.process.Xform.$Module = MWF.APP$Module =  new Class({
	Implements: [Events],
    options: {
        "moduleEvents": ["load"]
    },
	
	initialize: function(node, json, form, options){

		this.node = $(node);
        this.node.store("module", this);
		this.json = json;
		this.form = form;
	},
	load: function(){

		if (this.fireEvent("queryLoad")){
            this._queryLoaded();
			this._loadUserInterface();
			this._loadStyles();
			this._loadEvents();
			
			this._afterLoaded();
			this.fireEvent("postLoad");
            this.fireEvent("load");
		}
	},
	_loadUserInterface: function(){
	//	this.node = this.node;
	},
	
	_loadStyles: function(){
		if (this.json.styles) this.node.setStyles(this.json.styles);
	},
	_loadEvents: function(){
		Object.each(this.json.events, function(e, key){
			if (e.code){
                if (this.options.moduleEvents.indexOf(key)!=-1){
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
	_getBusinessData: function(){
        if (this.json.section=="yes"){
            return this._getBusinessSectionData();
        }else {
            return this.form.businessData.data[this.json.id] || "";
        }
	},
    _getBusinessSectionData: function(){
        switch (this.json.sectionBy){
            case "person":
                return this._getBusinessSectionDataByPerson();
                break;
            case "department":
                return this._getBusinessSectionDataByDepartment();
                break;
            case "activity":
                return this._getBusinessSectionDataByActivity();
                break;
            case "script":
                return this._getBusinessSectionDataByScript(this.json.sectionByScript.code);
                break;
            default:
                return this.form.businessData.data[this.json.id] || "";
        }
    },
    _getBusinessSectionDataByPerson: function(){
        var dataObj = this.form.businessData.data[this.json.id];
        return (dataObj) ? (dataObj[layout.desktop.session.user.id] || "") : "";
    },
    _getBusinessSectionDataByDepartment: function(){
        var dataObj = this.form.businessData.data[this.json.id];
        if (!dataObj) return "";
        var key = (this.form.businessData.task) ? this.form.businessData.task.department : "";
        return (key) ? (dataObj[key] || "") : "";
    },
    _getBusinessSectionDataByActivity: function(){
        var dataObj = this.form.businessData.data[this.json.id];
        if (!dataObj) return "";
        var key = (this.form.businessData.work) ? this.form.businessData.work.activity : "";
        return (key) ? (dataObj[key] || "") : "";
    },
    _getBusinessSectionDataByScript: function(code){
        var dataObj = this.form.businessData.data[this.json.id];
        if (!dataObj) return "";
        var key = this.form.Macro.exec(code, this);
        return (key) ? (dataObj[key] || "") : "";
    },

    _setBusinessData: function(v){
        if (this.json.section=="yes"){
            this._setBusinessSectionData(v);
        }else {
            if (this.form.businessData.data[this.json.id]){
                this.form.businessData.data[this.json.id] = v;
            }else{
                this.form.businessData.data[this.json.id] = v;
                this.form.Macro.environment.setData(this.form.businessData.data);
            }
            if (this.json.isTitle) this.form.businessData.work.title = v;
        }
    },
    _setBusinessSectionData: function(v){
        switch (this.json.sectionBy){
            case "person":
                this._setBusinessSectionDataByPerson(v);
                break;
            case "department":
                this._setBusinessSectionDataByDepartment(v);
                break;
            case "activity":
                this._setBusinessSectionDataByActivity(v);
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
    _setBusinessSectionDataByDepartment: function(v){
        var resetData = false;
        var key = (this.form.businessData.task) ? this.form.businessData.task.department : "";

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
