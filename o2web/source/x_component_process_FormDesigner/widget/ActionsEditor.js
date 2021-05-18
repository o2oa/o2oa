MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.FormDesigner = MWF.xApplication.process.FormDesigner || {};
MWF.xApplication.process.FormDesigner.widget = MWF.xApplication.process.FormDesigner.widget || {};
MWF.require("MWF.widget.ScriptArea", null, false);
MWF.require("MWF.widget.Maplist", null, false);
MWF.xApplication.process.FormDesigner.widget.ActionsEditor = new Class({
	Implements: [Options, Events],
	Extends: MWF.widget.Common,
	options: {
		"style": "default",
		"maxObj": document.body,
        "isSystemTool" : false,
        "noCreate": false,
        "noDelete": false,
        "noCode": false,
        "noHide": false,
        "systemToolsAddress" : "../x_component_process_FormDesigner/Module/Actionbar/toolbars.json"
	},
	initialize: function(node, designer, module, options){
		this.setOptions(options);
		this.node = $(node);
		this.module = module;
        this.designer = designer;
		
		this.path = "../x_component_process_FormDesigner/widget/$ActionsEditor/";
		this.cssPath = "../x_component_process_FormDesigner/widget/$ActionsEditor/"+this.options.style+"/css.wcss";
		this._loadCss();
		
		this.actions = [];
		this.currentEditItem = null;
		
		this.createActionsAreaNode();
	},
    createActionsAreaNode: function(){
		this.actionsContainer = new Element("div", {
			"styles": this.css.actionsContainer
		}).inject(this.node);
		
	//	var size = this.node.getUsefulSize();
	//	this.eventsContainer.setStyle("height", size.y);
	},
	
	load: function(data){
        this.loadActionsArea();
        if (!this.options.noCreate) this.loadCreateActionButton();

		this.data = data;
        if ( !this.data || typeOf(this.data)!="array") this.data = [];
        this.loadRestoreActionButton();

        this.data.each(function(actionData, idx){
            if (actionData.type!="MWFToolBarSeparator"){
                var action = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor.ButtonAction(this);
                action.load(actionData);
                this.actions.push(action);
            }
        }.bind(this));

	},

    loadActionsArea: function(){
        if (!this.options.noCreate){
            this.actionTitleArea = new Element("div", {
                "styles": this.css.actionTitleArea
            }).inject(this.actionsContainer);
        }
        this.actionArea = new Element("div", {
            "styles": this.css.actionArea
        }).inject(this.actionsContainer);

    },
    loadCreateActionButton: function(){
        this.createActionButtonButton = new Element("div", {
            "styles": this.css.createActionButton,
            "title" : this.designer.lp.actionbar.addCustomTool,
            "text": "+"
        }).inject(this.actionTitleArea);

        this.createActionButtonButton.addEvent("click", function(){
            this.addButtonAction();
        }.bind(this));
    },
    loadRestoreActionButton : function(){
        if( this.options.isSystemTool ){
            if( !this.actionTitleArea ){
                this.actionTitleArea = new Element("div", { "styles": this.css.actionTitleArea }).inject(this.actionArea,"before");
            }
            this.restoreActionButtonButton = new Element("div", {
                "styles": this.css.restoreActionButton,
                "title" : this.designer.lp.actionbar.restoreDefaultTool
            }).inject(this.actionTitleArea);

            this.restoreActionButtonButton.addEvent("click", function(){
                debugger;
                this.restoreButtonAction();
            }.bind(this));
        }
    },
    listRemovedSystemTool : function(){
        var list = [];
        if( !this.defaultTools ){
            MWF.getJSON( this.options.systemToolsAddress, function(tools){
                this.defaultTools = tools;
                if( this.options.target && this.options.target === "mobileForm" ){
                    this.defaultTools.push({
                        "type": "MWFToolBarButton",
                        "img": "read.png",
                        "title": this.designer.lp.actionbar.setReaded,
                        "action": "readedWork",
                        "text": this.designer.lp.actionbar.readed,
                        "id": "action_readed",
                        "control": "allowReadProcessing",
                        "condition": "",
                        "read": true
                    });
                }
            }.bind(this), false);
        }
        this.defaultTools.each( function( tool ){
            var flag = true;
            for( var i=0; i<(this.data || []).length; i++ ){
                if( this.data[i].id === tool.id ){
                    flag = false;
                    break;
                }
            }
            if(flag)list.push(tool);
        }.bind(this));
        return list;
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
		var action = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor.ButtonAction(this);
        action.load(o);
        this.data.push(o);
        this.actions.push(action);

        this.fireEvent("change");

	},
    restoreButtonAction : function(){
        var list = this.listRemovedSystemTool();
        if( !list.length )return;
        var selectableItems = [];
        list.each( function(d){
            var title = "";
            if( d.text && this.designer.lp.actionBar ){
                title = this.designer.lp.actionBar[ d.text.split(".").getLast().replace("}}","") ] || "";
                title = title ? ("  (" + title + ")") : "";
            }
            selectableItems.push( {
                name : d.text + title,
                id : d.id
            })
        }.bind(this));
        MWF.xDesktop.requireApp("Template", "Selector.Custom", null, false);
        var opt  = {
            "count": 0,
            "title": this.designer.lp.actionbar.selectDefaultTool,
            "selectableItems" : selectableItems,
            "values": [],
            "onComplete": function( array ){
                if( !array || array.length == 0 )return;
                array.each( function(tool){
                    for( var i=0; i<list.length; i++ ){
                        if( list[i].id === tool.data.id ){
                            list[i].system = true;
                            this.data.push( list[i] );
                            var action = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor.ButtonAction(this);
                            action.load(list[i]);
                            this.actions.push(action);
                            break;
                        }
                    }
                }.bind(this));
                this.fireEvent("change");
            }.bind(this)
        };
        var selector = new MWF.xApplication.Template.Selector.Custom(this.options.maxObj, opt );
        selector.load();
    }

});

MWF.xApplication.process.FormDesigner.widget.ActionsEditor.ButtonAction = new Class({
    initialize: function (editor) {
        this.editor = editor;
        this.css = this.editor.css;
        this.container = this.editor.actionArea
    },
    load: function (data) {
        this.data = data;
        this.loadNode();

        var form = this.editor.designer.form || this.editor.designer.page || this.editor.designer.view;
        if (form && form.scriptDesigner){
            this.scriptItem = form.scriptDesigner.addScriptItem(this.data, "actionScript", this.editor.module, "action.tools", this.data.text);
        }

        var text = this.data.text;
        Object.defineProperty(this.data, "text", {
            configurable : true,
            enumerable : true,
            "get": function(){return text;},
            "set": function(v){
               if (this.scriptItem){
                   this.scriptItem.par = v;
                   this.scriptItem.resetText();
               }
               text = v;

            }.bind(this)
        });
    },
    loadNode: function () {
        this.node = new Element("div", {"styles": this.css.actionNode}).inject(this.container);

        this.titleNode = new Element("div", {"styles": this.css.actionTitleNode}).inject(this.node);

        this.iconNode = new Element("div", {"styles": this.css.actionIconNode}).inject(this.titleNode);
        this.textNode = new Element("div", {"styles": this.css.actionTextNode, "text": this.data.text}).inject(this.titleNode);

        if( this.data.text && this.editor.designer.lp.actionBar){
            var title = this.editor.designer.lp.actionBar[ this.data.text.split(".").getLast().replace("}}","") ] || "";
            this.textNode.set("title", title || "");
        }

        this.upButton = new Element("div", {"styles": this.css.actionUpButtonNode, "title": this.editor.designer.lp.actionbar.up}).inject(this.titleNode);

        this.propertiesButton = new Element("div", {"styles": this.css.actionPropertiesButtonNode, "title": this.editor.designer.lp.actionbar.property}).inject(this.titleNode);
        if (!this.data.properties || Object.keys(this.data.properties).length === 0 ){
            this.propertiesButton.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/property_empty.png)");
        }else{
            this.propertiesButton.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/property.png)");
        }
        this.propertiesNode = new Element("div", {"styles": this.css.actionScriptNode}).inject(this.node);
        this.propertiesArea = new MWF.widget.Maplist(this.propertiesNode, {
            "title": this.editor.designer.lp.actionbar.setProperties,
            "collapse": false,
            "onChange": function(){
                this.data.properties = this.propertiesArea.toJson();
                if (!this.data.properties || Object.keys(this.data.properties).length === 0 ){
                    this.propertiesButton.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/property_empty.png)");
                }else{
                    this.propertiesButton.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/property.png)");
                }
                this.editor.fireEvent("change");
            }.bind(this)
        });
        this.propertiesArea.load( this.data.properties || {});

        if (!this.editor.options.noDelete) this.delButton = new Element("div", {
            "styles": this.css.actionDelButtonNode,
            "text": "-",
            "title" : this.editor.designer.lp.actionbar.delete
        }).inject(this.titleNode);

        this.conditionButton = new Element("div", {"styles": this.css.actionConditionButtonNode, "title": this.editor.designer.lp.actionbar.hideCondition}).inject(this.titleNode);
        if (this.data.condition){
            this.conditionButton.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/code.png)");
        }else{
            this.conditionButton.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/code_empty.png)");
        }

        if (!this.editor.options.noEditShow && !this.data.system){
            this.editButton = new Element("div", {"styles": this.css.actionEditButtonNode, "title": this.editor.designer.lp.actionbar.edithide}).inject(this.titleNode);
            if (this.data.editShow){
                this.editButton.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/edit.png)");
            }else{
                this.editButton.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/edit_hide.png)");
            }
        }

        if (!this.editor.options.noReadShow && !this.data.system){
            this.readButton = new Element("div", {"styles": this.css.actionReadButtonNode, "title": this.editor.designer.lp.actionbar.readhide}).inject(this.titleNode);
            if (this.data.readShow){
                this.readButton.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/read.png)");
            }else{
                this.readButton.setStyle("background-image", "url("+this.editor.path+this.editor.options.style+"/icon/read_hide.png)");
            }
        }

        var icon = this.editor.path+this.editor.options.style+"/tools/"+this.data.img;
        this.iconNode.setStyle("background-image", "url("+icon+")");

        if (!this.editor.options.noCode && !this.data.system ){
            this.scriptNode = new Element("div", {"styles": this.css.actionScriptNode}).inject(this.node);
            this.scriptArea = new MWF.widget.ScriptArea(this.scriptNode, {
                "title": this.editor.designer.lp.actionbar.editScript,
                "maxObj": this.editor.designer.formContentNode,
                "key": "actionScript",
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
                }.bind(this)
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
            }.bind(this)
        });
        this.conditionArea.load({"code": this.data.condition});

        this.setEvent();


        //this.loadEditNode();
        //this.loadChildNode();
        //this.setTitleNode();
        //this.setEditNode();
    },
    setEvent: function(){
        this.iconMenu = new MWF.widget.Menu(this.iconNode, {
            "event": "click",
            "style": "actionbarIcon",
            "onPostShow" : function (ev) {
                ev.stopPropagation();
            }
        });
        this.iconMenu.load();
        var _self = this;
        for (var i=1; i<=136; i++){
            var icon = this.editor.path+this.editor.options.style+"/tools/"+i+".png";
            var item = this.iconMenu.addMenuItem("", "click", function(ev){
                var src = this.item.getElement("img").get("src");
                _self.data.img = src.substr(src.lastIndexOf("/")+1, src.length);
                _self.iconNode.setStyle("background-image", "url("+src+")");
                _self.editor.fireEvent("change");
                ev.stopPropagation();
            }, icon);
            item.iconName = i+".png";
        }

        this.upButton.addEvent("click", function(e){
            var actions = this.editor.actions;
            var dataList = this.editor.data;

            var dataIndex = dataList.indexOf( this.data );
            var index = actions.indexOf( this );

            if( index === 0 || dataIndex === 0 ){
                e.stopPropagation();
                return;
            }

            var index_before = index-1;
            var action = actions[index_before];
            this.node.inject(action.node, "before");

            actions[index_before] = actions.splice(index, 1, actions[index_before])[0];
            this.editor.actions = actions;

            var dataIndex_before = dataIndex - 1;
            dataList[dataIndex_before] = dataList.splice(dataIndex, 1, dataList[dataIndex_before])[0];
            this.editor.data = dataList;

            this.editor.fireEvent("change");
            e.stopPropagation();
        }.bind(this));

        this.propertiesButton.addEvent("click", function(e){
            var dis = this.propertiesNode.getStyle("display");
            if (dis=="none"){
                this.propertiesNode.setStyle("display", "block");
            }else{
                this.propertiesNode.setStyle("display", "none");
            }
            e.stopPropagation();
        }.bind(this));

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

        if (this.editButton) this.editButton.addEvent("click", function(e){
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
        if (this.readButton) this.readButton.addEvent("click", function(e){
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
    },

    editCondition: function(){
        var dis = this.conditionNode.getStyle("display");
        if (dis=="none"){
            this.conditionNode.setStyle("display", "block");
            if (this.conditionArea){
                this.conditionArea.resizeContentNodeSize();
                if (this.conditionArea.editor) this.conditionArea.editor.resize();

                if (!this.conditionArea.jsEditor){
                    this.conditionArea.contentNode.empty();
                    this.conditionArea.loadEditor({"code": this.data.condition});
                }
            }
        }else{
            this.conditionNode.setStyle("display", "none");
        }
    },

    destroy: function(){

        var form = this.editor.designer.form || this.editor.designer.page || this.editor.designer.view;
        if ( form && form.scriptDesigner){
            form.scriptDesigner.deleteScriptItem(this.editor.module, "action.tools", this.data.text);
        }

        this.editor.data.erase(this.data);
        this.editor.actions.erase(this);
        this.editor.fireEvent("change");

        this.node.destroy();

        MWF.release(this.scriptArea);
        MWF.release(this);
    },

    editTitleComplete: function(el){
        this.data.text = el.get("value");
        this.data.title = el.get("value");
        el.destroy();
        this.textNode.empty();
        this.textNode.set("text", this.data.text);
        this.editor.fireEvent("change");
    }
});