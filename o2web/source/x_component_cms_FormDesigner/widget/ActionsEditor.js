MWF.xApplication.cms.FormDesigner.widget = MWF.xApplication.cms.FormDesigner.widget || {};
MWF.xDesktop.requireApp("process.FormDesigner", "widget.ActionsEditor", null, false);
MWF.xApplication.cms.FormDesigner.widget.ActionsEditor = new Class({
	Extends: MWF.xApplication.process.FormDesigner.widget.ActionsEditor,
	load: function(data){
        this.loadActionsArea();
        if (!this.options.noCreate) this.loadCreateActionButton();

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

        if (!this.editor.options.noDelete)this.delButton = new Element("div", {"styles": this.css.actionDelButtonNode, "text": "-"}).inject(this.titleNode);

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

        if (!this.editor.options.noCode) {
            this.scriptNode = new Element("div", {"styles": this.css.actionScriptNode}).inject(this.node);
            this.scriptArea = new MWF.widget.ScriptArea(this.scriptNode, {
                "title": this.editor.designer.lp.actionbar.editScript,
                "maxObj": this.editor.designer.formContentNode,
                "onChange": function () {
                    this.data.actionScript = this.scriptArea.editor.getValue();
                    this.editor.fireEvent("change");
                }.bind(this),
                "onSave": function () {
                    this.data.actionScript = this.scriptArea.editor.getValue();
                    this.editor.fireEvent("change");
                    this.editor.designer.saveForm();
                }.bind(this),
                "onPostLoad": function () {
                    //   this.scriptNode.setStyle("display", "none");
                }.bind(this),
                "helpStyle": "cms"
            });
            this.scriptArea.load({"code": this.data.actionScript});
        }

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
    },
    setEvent: function(){
        this.iconMenu = new MWF.widget.Menu(this.iconNode, {"event": "click", "style": "actionbarIcon"});
        this.iconMenu.load();
        var _self = this;
        for (var i=-6; i<0; i++){
            //var icon = this.editor.path+this.editor.options.style+"/tools/"+i+".png";
            var icon = "/x_component_cms_FormDesigner/Module/Actionbar/"+this.editor.options.style+"/custom/"+i+".png";
            var item = this.iconMenu.addMenuItem("", "click", function(){
                var src = this.item.getElement("img").get("src");
                _self.data.img = src.substr(src.lastIndexOf("/")+1, src.length);
                _self.iconNode.setStyle("background-image", "url("+src+")");
                _self.editor.fireEvent("change");
            }, icon);
            item.iconName = i+".png";
        }
        for (var i=1; i<=121; i++){
            //var icon = this.editor.path+this.editor.options.style+"/tools/"+i+".png";
            var icon = "/x_component_cms_FormDesigner/Module/Actionbar/"+this.editor.options.style+"/custom/"+i+".png";
            var item = this.iconMenu.addMenuItem("", "click", function(){
                var src = this.item.getElement("img").get("src");
                _self.data.img = src.substr(src.lastIndexOf("/")+1, src.length);
                _self.iconNode.setStyle("background-image", "url("+src+")");
                _self.editor.fireEvent("change");
            }, icon);
            item.iconName = i+".png";
        }

        this.textNode.addEvent("click", function(e){
            this.textNode.empty();
            var editTitleNode = new Element("input", {"styles": this.css.actionEditTextNode, "type": "text", "value": this.data.text}).inject(this.textNode);
            editTitleNode.focus();
            editTitleNode.select();
            var _self = this;
            editTitleNode.addEvent("blur", function(){_self.editTitleComplete(this);});
            editTitleNode.addEvent("keydown:keys(enter)", function(){_self.editTitleComplete(this);});
            editTitleNode.addEvent("click", function(e){e.stopPropagation()});
            e.stopPropagation();
        }.bind(this));
        this.conditionButton.addEvent("click", function(e){
            e.stopPropagation();
            this.editCondition();
        }.bind(this));

        this.editButton.addEvent("click", function(e){
            if (this.data.editShow){
                this.editButton.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/edit_hide.png)");
                this.data.editShow = false;
            }else{
                this.editButton.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/edit.png)");
                this.data.editShow = true;
            }
            this.editor.fireEvent("change");
            e.stopPropagation();
        }.bind(this));
        this.readButton.addEvent("click", function(e){
            if (this.data.readShow){
                this.readButton.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/read_hide.png)");
                this.data.readShow = false;
            }else{
                this.readButton.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/read.png)");
                this.data.readShow = true;
            }
            this.editor.fireEvent("change");
            e.stopPropagation();
        }.bind(this));

        this.titleNode.addEvent("click", function(){
            if (this.scriptNode){
                var dis = this.scriptNode.getStyle("display");
                if (dis=="none"){
                    this.scriptNode.setStyle("display", "block");
                    if (this.scriptArea){
                        this.scriptArea.resizeContentNodeSize();
                        if (this.scriptArea.editor) this.scriptArea.editor.resize();

                        if (!this.scriptArea.jsEditor){
                            this.scriptArea.contentNode.empty();
                            this.scriptArea.loadEditor({"code": this.data.actionScript});
                        }
                    }
                }else{
                    this.scriptNode.setStyle("display", "none");
                }
            }
        }.bind(this));

        if (this.delButton) this.delButton.addEvent("click", function(e){
            var _self = this;
            this.editor.designer.confirm("warn", this.delButton, MWF.APPFD.LP.notice.deleteButtonTitle, MWF.APPFD.LP.notice.deleteButton, 300, 120, function(){
                _self.destroy();

                this.close();
            }, function(){
                this.close();
            }, null);
            e.stopPropagation();
        }.bind(this));
    }
});