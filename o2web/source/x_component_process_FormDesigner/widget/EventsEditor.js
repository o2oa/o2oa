MWF.xApplication.process.FormDesigner.widget = MWF.xApplication.process.FormDesigner.widget || {};
MWF.require("MWF.widget.ScriptArea", null, false);
MWF.xApplication.process.FormDesigner.widget.EventsEditor = new Class({
	Implements: [Options, Events],
	Extends: MWF.widget.Common,
	options: {
		"style": "default",
		"maxObj": document.body
	},
	initialize: function(node, designer, options){

		this.setOptions(options);
		this.node = $(node);
        this.app = designer;
		
		this.path = "/x_component_process_FormDesigner/widget/$EventsEditor/";
		this.cssPath = "/x_component_process_FormDesigner/widget/$EventsEditor/"+this.options.style+"/css.wcss";
		this._loadCss();
		
		this.items = [];
		this.currentEditItem = null;
		
		this.createEventsAreaNode();
	},
	createEventsAreaNode: function(){
		this.eventsContainer = new Element("div", {
			"styles": this.css.eventsContainer
		}).inject(this.node);
		
		var size = this.node.getUsefulSize();
	//	this.eventsContainer.setStyle("height", size.y);
	},
	
	load: function(data, module, path){
		this.data = data;
        this.module = module;
        this.scriptPath = path;
		Object.each(data, function(obj, key){
			var item = new MWF.xApplication.process.FormDesigner.widget.EventsEditor.Item(this);
			item.load(key, obj);
			this.items.push(item);
		}.bind(this));
	},

	deleteItem: function(item){
		this.items.erase(item);

		if (this.data[item.event]){
			this.data[item.event].code = "";
			this.data[item.event].html = "";
			
			delete this.data[item.event];
		}

        item.deleteScriptDesignerItem();
		
		if (item.container){
			item.container.destroy();
		}
	},
	addItem: function(item){
		this.data[item.event] = item.data;
		this.items.push(item);
	},
	addEvent: function(){
		var item = new MWF.xApplication.process.FormDesigner.widget.EventsEditor.Item(this);
		item.load("", "");
	}
	
});

MWF.xApplication.process.FormDesigner.widget.EventsEditor.Item = new Class({
	initialize: function(editor){
		this.editor = editor;
	},
	load: function(event, data){
		if (!event){
			this.create();
		}else{
			this.event = event;
			this.data = data;
			this.createContainer();
			this.createActions();
		}
	},
	create: function(){
		this.event = "";
		this.data = {"code": "", "html": ""};
		this.createContainerTitle();
		this.createInput = new Element("input", {
			"styles": this.editor.css.createInput
		}).inject(this.textNode);
		this.createInput.focus();
		
		this.createInput.addEvents({
			"keydown": function(e){

				if (e.code==13){
					this.checkCreate();
				}
			}.bind(this),
			"blur": function(){
				this.checkCreate();
			}.bind(this)
		});
	},
	checkCreate: function(){
		var event = this.createInput.get("value");
		if (!event){
			this.editor.deleteItem(this);
			return false;
		}

		if (this.editor.data[event]){
			this.iconNode.setStyle("background", "url("+this.editor.path+this.editor.options.style+"/icon/error.png) center center no-repeat");
			this.iconNode.title = MWF.LP.process.repetitionsEvent;
			this.createInput.focus();
		}else{
			this.event = event;
			this.container.destroy();
			this.load(this.event, this.data);
			this.editCode();
			this.editor.addItem(this);

			this.bindScriptDesigner();

		}
	},
    bindScriptDesigner: function(){
        var form = this.editor.app.form || this.editor.app.page;
        if (form.scriptDesigner) form.scriptDesigner.addScriptItem(this.data, "code", this.editor.module, this.editor.scriptPath+"."+this.event);
	},
    deleteScriptDesignerItem: function(){
        var form = this.editor.app.form || this.editor.app.page;
        if (form.scriptDesigner){
            form.scriptDesigner.deleteScriptItem(this.editor.module, this.editor.scriptPath+"."+this.event);
        }
    },

	createContainerTitle: function(){
		this.container = new Element("div", {
			"styles": this.editor.css.itemContainer
		}).inject(this.editor.eventsContainer);
		
		
		this.titleContainer = new Element("div", {
			"styles": this.editor.css.itemTitleContainer
		}).inject(this.container);
		
		this.iconNode = new Element("div", {
			"styles": this.editor.css.iconNode
		}).inject(this.titleContainer);
		
		this.actionNode = new Element("div", {
			"styles": this.editor.css.actionNode
		}).inject(this.titleContainer);
		
		this.textNode = new Element("div", {
			"styles": this.editor.css.textNode,
			"text": this.event
		}).inject(this.titleContainer);
	},
	createContainer: function(){
		this.createContainerTitle();
		
		this.codeNode = new Element("div", {
			"styles": this.editor.css.codeNode
		}).inject(this.container);
		
		this.titleContainer.addEvents({
			"mouseover": function(){
				this.actionNode.fade("in");
			}.bind(this),
			"mouseout": function(){
				this.actionNode.fade("out");
			}.bind(this)
		});
		
		this.textNode.addEvent("click", function(){
			this.editCode();
		}.bind(this));
		
		this.checkIcon();
		
	},
	editCode: function(){
		if (this.editor.currentEditItem){
			if (this.editor.currentEditItem!=this) this.editor.currentEditItem.editCodeComplete();
		} 
		if (this.editor.currentEditItem!=this){
			if (!this.codeEditor){
				this.codeEditor = new MWF.widget.ScriptArea(this.codeNode, {
					"style": "event",
					"title": "on"+this.event+" (S)",
					"maxObj": this.editor.options.maxObj,
					"onChange": function(){
						var json = this.codeEditor.toJson();
						this.data.code = json.code;
						this.data.html = json.html;
						this.checkIcon();
					}.bind(this),
                    "onSave": function(){
                        var json = this.codeEditor.toJson();
                        this.data.code = json.code;
                        this.data.html = json.html;
                        this.editor.app.saveForm();
                    }.bind(this)
				});
				this.codeEditor.load(this.data);
			}
			
			if (!this.morph){
				this.morph = new Fx.Morph(this.codeNode, {duration: 200});
			}
			this.codeNode.setStyle("display", "block");
			this.morph.start({"height": [0,300]}).chain(function(){
				this.codeEditor.resizeContentNodeSize();
				this.codeEditor.focus();
			//	this.fireEvent("postShow");
			}.bind(this));
			this.editor.currentEditItem = this;
		}else{
			this.editCodeComplete();
		}
	},
	editCodeComplete: function(){
		if (this.codeEditor){
			var json = this.codeEditor.toJson();
			this.data.code = json.code;
			this.data.html = json.html;
			this.checkIcon();
		}
		if (!this.morph){
			this.morph = new Fx.Morph(this.codeNode, {duration: 200});
		}
		this.morph.start({"height": [300,0]}).chain(function(){
			this.codeNode.setStyle("display", "none");
		//	this.fireEvent("postHide");
		}.bind(this));
		this.editor.currentEditItem = null;
	},
	
	
	createActions: function(){
		var deleteAction = new Element("div", {
			"styles": this.editor.css.actionNodeDelete
		}).inject(this.actionNode);
		
		deleteAction.addEvent("click", function(e){
			var item = this;
            this.editor.app.confirm("warn", e, this.editor.app.lp.notice.deleteEventTitle, this.editor.app.lp.notice.deleteEvent, 300, 120, function(){
				item.editor.deleteItem(item);
	    		this.close();
			}, function(){
				this.close();
			}, null);
		}.bind(this));
		
		
		var addAction = new Element("div", {
			"styles": this.editor.css.actionNodeAdd
		}).inject(this.actionNode);
		
		addAction.addEvent("click", function(e){
			this.editor.addEvent();
		}.bind(this));
		
	},
	checkIcon: function(){
		if (this.data.code){
			this.iconNode.setStyle("background", "url("+this.editor.path+this.editor.options.style+"/icon/code.png) center center no-repeat");
		}else{
			this.iconNode.setStyle("background", "url("+this.editor.path+this.editor.options.style+"/icon/code_empty.png) center center no-repeat");
		}
	}
	
	
	
});

