MWF.xDesktop.requireApp("process.FormDesigner", "widget.EventsEditor", null, false);
MWF.xApplication.process.FormDesigner.widget.ParameterEditor = new Class({
	Implements: [Options, Events],
	Extends: MWF.xApplication.process.FormDesigner.widget.EventsEditor,
	options: {
		"style": "default",
		"maxObj": document.body
	},
	load: function(data, module, path){
		this.data = data || {};
        this.module = module;
        this.scriptPath = path;
        //if (!Object.getLength(this.data)){
            this.addNewItemAction = new Element("div", {"styles": this.css.addNewItemAction, "text": "+"}).inject(this.node);
            this.addNewItemAction.addEvent("click", function(){
                this.addEvent();
                this.addNewItemAction.setStyle("display", "none");
            }.bind(this));
        //}
		Object.each(data, function(obj, key){
			var item = new MWF.xApplication.process.FormDesigner.widget.ParameterEditor.Item(this);
			item.load(key, obj);
			this.items.push(item);
		}.bind(this));
	},
	addItem: function(item){
		this.data[item.event] = item.data;
		this.items.push(item);
	},
	addEvent: function(){
		var item = new MWF.xApplication.process.FormDesigner.widget.ParameterEditor.Item(this);
		item.load("", "");
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
        if (!Object.getLength(this.data)){
            if (this.addNewItemAction) this.addNewItemAction.setStyle("display", "block");
        }
    }
	
});

MWF.xApplication.process.FormDesigner.widget.ParameterEditor.Item = new Class({
    Extends: MWF.xApplication.process.FormDesigner.widget.EventsEditor.Item,
    createContainerTitle: function(){
        this.container = new Element("div", {
            "styles": this.editor.css.itemContainer
        }).inject(this.editor.eventsContainer);


        this.titleContainer = new Element("div", {
            "styles": this.editor.css.itemTitleContainer
        }).inject(this.container);

        this.iconNode = new Element("div", {
            "styles": this.editor.css.parIconNode
        }).inject(this.titleContainer);

        this.actionNode = new Element("div", {
            "styles": this.editor.css.actionNode
        }).inject(this.titleContainer);

        this.textNode = new Element("div", {
            "styles": this.editor.css.textNode,
            "text": this.event
        }).inject(this.titleContainer);
    },
	editCode: function(){
		if (this.editor.currentEditItem){
			if (this.editor.currentEditItem!=this) this.editor.currentEditItem.editCodeComplete();
		} 
		if (this.editor.currentEditItem!=this){
			if (!this.codeEditor){
				this.codeEditor = new MWF.widget.ScriptArea(this.codeNode, {
					"style": "event",
					"title": this.event+" (S)",
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
                        this.checkIcon();
                        this.editor.app.savePage();
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
    bindScriptDesigner: function(){
        var form = this.editor.app.form || this.editor.app.page;
        if (form.scriptDesigner) form.scriptDesigner.addScriptItem(this.data, "code", this.editor.module, this.editor.scriptPath, this.event);
    },
    deleteScriptDesignerItem: function(){
        var form = this.editor.app.form || this.editor.app.page;
        if (form.scriptDesigner){
            form.scriptDesigner.deleteScriptItem(this.editor.module, this.editor.scriptPath, this.event);
        }
    },
	checkIcon: function(){
		if (this.data.code){
			this.iconNode.setStyle("background", "url("+this.editor.path+this.editor.options.style+"/icon/codePar.png) center center no-repeat");
		}else{
			this.iconNode.setStyle("background", "url("+this.editor.path+this.editor.options.style+"/icon/codePar_empty.png) center center no-repeat");
		}
	}
});

