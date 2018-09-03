MWF.xApplication.cms.FormDesigner.widget = MWF.xApplication.cms.FormDesigner.widget || {};
MWF.xDesktop.requireApp("process.FormDesigner", "widget.ActionsEditor", null, false);
MWF.xApplication.cms.FormDesigner.widget.ActionsEditor = new Class({
	Extends: MWF.xApplication.process.FormDesigner.widget.ActionsEditor,
	load: function(data){
        this.loadActionsArea();
        this.loadCreateActionButton();

		this.data = data;
        if (this.data){
            if (typeOf(this.data)!="array") this.data = [];
            this.data.each(function(actionData, idx){
                var action = new MWF.xApplication.cms.FormDesigner.widget.ActionsEditor.ButtonAction(this);
                action.load(actionData);
                this.actions.push(action);
            }.bind(this));
        }

	},

	addButtonAction: function(){
        var o = {
            "type": "MWFToolBarButton",
            "img": "4.png",
            "title": "",
            "action": "",
            "text": "Unnamed",
            "actionScript" : "",
            "condition": "",
            "editShow": true,
            "readShow": true
        };
		var action = new MWF.xApplication.cms.FormDesigner.widget.ActionsEditor.ButtonAction(this);
        action.load(o);
        this.data.push(o);
        this.actions.push(action);
        this.fireEvent("change");
	}

});

MWF.xApplication.cms.FormDesigner.widget.ActionsEditor.ButtonAction = new Class({
    Extends: MWF.xApplication.process.FormDesigner.widget.ActionsEditor.ButtonAction,
    loadNode: function () {
        this.node = new Element("div", {"styles": this.css.actionNode}).inject(this.container);

        this.titleNode = new Element("div", {"styles": this.css.actionTitleNode}).inject(this.node);

        this.iconNode = new Element("div", {"styles": this.css.actionIconNode}).inject(this.titleNode);
        this.textNode = new Element("div", {"styles": this.css.actionTextNode, "text": this.data.text}).inject(this.titleNode);

        this.delButton = new Element("div", {"styles": this.css.actionDelButtonNode, "text": "-"}).inject(this.titleNode);

        this.conditionButton = new Element("div", {"styles": this.css.actionConditionButtonNode, "title": this.editor.designer.lp.actionbar.hideCondition}).inject(this.titleNode);
        if (this.data.condition){
            this.conditionButton.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/code.png)");
        }else{
            this.conditionButton.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/code_empty.png)");
        }

        this.editButton = new Element("div", {"styles": this.css.actionEditButtonNode, "title": this.editor.designer.lp.actionbar.edithide}).inject(this.titleNode);
        if (this.data.editShow){
            this.editButton.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/edit.png)");
        }else{
            this.editButton.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/edit_hide.png)");
        }

        this.readButton = new Element("div", {"styles": this.css.actionReadButtonNode, "title": this.editor.designer.lp.actionbar.readhide}).inject(this.titleNode);
        if (this.data.readShow){
            this.readButton.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/read.png)");
        }else{
            this.readButton.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/read_hide.png)");
        }



        var icon = this.editor.path+this.editor.options.style+"/tools/"+this.data.img;
        this.iconNode.setStyle("background-image", "url("+icon+")");

        this.scriptNode = new Element("div", {"styles": this.css.actionScriptNode}).inject(this.node);
        this.scriptArea = new MWF.widget.ScriptArea(this.scriptNode, {
            "title": this.editor.designer.lp.actionbar.editScript,
            "maxObj": this.editor.designer.formContentNode,
            "onChange": function(){
                this.data.actionScript = this.scriptArea.editor.getValue();
                this.editor.fireEvent("change");
            }.bind(this),
            "onSave": function(){
                this.data.actionScript = this.scriptArea.editor.getValue();
                this.editor.fireEvent("change");
                this.editor.designer.saveForm();
            }.bind(this),
            "onPostLoad": function(){
             //   this.scriptNode.setStyle("display", "none");
            }.bind(this),
            "helpStyle" : "cms"
        });
        this.scriptArea.load({"code": this.data.actionScript});

        this.conditionNode = new Element("div", {"styles": this.css.actionScriptNode}).inject(this.node);
        this.conditionArea = new MWF.widget.ScriptArea(this.conditionNode, {
            "title": this.editor.designer.lp.actionbar.editCondition,
            "maxObj": this.editor.designer.formContentNode,
            "onChange": function(){
                this.data.condition = this.conditionArea.editor.getValue();
                if (this.data.condition){
                    this.conditionButton.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/code.png)");
                }else{
                    this.conditionButton.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/code_empty.png)");
                }
                this.editor.fireEvent("change");
            }.bind(this),
            "onSave": function(){
                this.data.condition = this.conditionArea.editor.getValue();
                this.editor.fireEvent("change");
                this.editor.designer.saveForm();
            }.bind(this),
            "onPostLoad": function(){
             //   this.conditionNode.setStyle("display", "none");
            }.bind(this),
            "helpStyle" : "cms"
        });
        this.conditionArea.load({"code": this.data.condition});

        this.setEvent();


        //this.loadEditNode();
        //this.loadChildNode();
        //this.setTitleNode();
        //this.setEditNode();
    }
});