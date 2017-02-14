MWF.require("MWF.widget.Common", null, false);
MWF.xApplication.cms.Xform.$Module = MWF.CMS$Module =  new Class({
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
                        return this.form.CMSMacro.fire(e.code, this, event);
                    }.bind(this));
                }else{
                    this.node.addEvent(key, function(event){
                        return this.form.CMSMacro.fire(e.code, this, event);
                    }.bind(this));
                }
			}
		}.bind(this));
	},
	_getBusinessData: function(){
		return this.form.businessData.data[this.json.id];
	},
    _setBusinessData: function(v){
        if (this.form.businessData.data[this.json.id]){
            this.form.businessData.data[this.json.id] = v;
        }else{
            this.form.businessData.data[this.json.id] = v;
            this.form.CMSMacro.environment.setData(this.form.businessData.data);
        }

    },

    _queryLoaded: function(){},
	_afterLoaded: function(){},
	
	setValue: function(){
	},
	focus: function(){
		this.node.focus();
	}
	
});
