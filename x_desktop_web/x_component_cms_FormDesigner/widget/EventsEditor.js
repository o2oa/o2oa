MWF.xApplication.cms.FormDesigner.widget = MWF.xApplication.cms.FormDesigner.widget || {};
MWF.xDesktop.requireApp("process.FormDesigner", "widget.EventsEditor", null, false);
MWF.xApplication.cms.FormDesigner.widget.EventsEditor = new Class({
	Extends: MWF.xApplication.process.FormDesigner.widget.EventsEditor,
	load: function(data){
		this.data = data;
		Object.each(data, function(obj, key){
			var item = new MWF.xApplication.cms.FormDesigner.widget.EventsEditor.Item(this);
			item.load(key, obj);
			this.items.push(item);
		}.bind(this));
	},
	addEvent: function(){
		var item = new MWF.xApplication.cms.FormDesigner.widget.EventsEditor.Item(this);
		item.load("", "");
	}
	
});

MWF.xApplication.cms.FormDesigner.widget.EventsEditor.Item = new Class({
	Extends: MWF.xApplication.process.FormDesigner.widget.EventsEditor.Item,
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
                    }.bind(this),
					"helpStyle" : "cms"
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
	}
});

