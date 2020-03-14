MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.ProcessDesigner = MWF.xApplication.process.ProcessDesigner || {};
MWF.xApplication.process.ProcessDesigner.widget = MWF.xApplication.process.ProcessDesigner.widget || {};
MWF.require("MWF.xAction.org.express.RestActions", null,false);
MWF.require("MWF.widget.Identity", null, false);

MWF.xApplication.process.ProcessDesigner.widget.PersonSelector = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "default",
        "type": "identity",
        "name": []
    },
    initialize: function(node, app, options){

        this.setOptions(options);
        this.node = $(node);
        this.app = app;

        this.path = "/x_component_process_ProcessDesigner/widget/$PersonSelector/";
        this.cssPath = "/x_component_process_ProcessDesigner/widget/$PersonSelector/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.identitys = [];
        this.restActions = new MWF.xAction.org.express.RestActions();

        this.name = this.node.get("name");
        this.load();

    },
    load: function(data){

        this.node.setStyles(this.css.node);

        this.createAddNode();
        this.loadIdentitys();
    },
    loadIdentitys: function(){
        var explorer = {
            "actions": this.restActions,
            "app": {
                "lp": this.app.lp
            }
        }
        if (this.options.names){
            this.options.names.each(function(name){
                MWF.require("MWF.widget.Identity", function(){
                    if (this.options.type.toLowerCase()=="identity") this.identitys.push(new MWF.widget.Identity({"name": name}, this.node, explorer));
                    if (this.options.type.toLowerCase()=="department") this.identitys.push(new MWF.widget.Department({"name": name}, this.node, explorer));
                    if (this.options.type.toLowerCase()=="company") this.identitys.push(new MWF.widget.Company({"name": name}, this.node, explorer));
                }.bind(this));
            }.bind(this));
        }

    },
    createAddNode: function(){
        this.addNode = new Element("div", {"styles": this.css.addPersonNode}).inject(this.node, "before");
        this.addNode.addEvent("click", function(e){

            var selecteds = [];
            this.identitys.each(function(id){selecteds.push(id.data.name)});

            var explorer = {
                "actions": this.restActions,
                "app": {
                    "lp": this.app.lp
                }
            }

            var options = {
                "type": this.options.type,
                "count": (this.options.type.toLowerCase()=="duty")? 1: 0,
                "names": selecteds,
                "zIndex": 20000,
                "onComplete": function(items){
                    this.identitys = [];
                    if (this.options.type.toLowerCase()!="duty") this.node.empty();

                    MWF.require("MWF.widget.Identity", function(){
                        items.each(function(item){
                            if (this.options.type.toLowerCase()=="identity") this.identitys.push(new MWF.widget.Identity(item.data, this.node, explorer));
                            if (this.options.type.toLowerCase()=="department") this.identitys.push(new MWF.widget.Department(item.data, this.node, explorer));
                            if (this.options.type.toLowerCase()=="company") this.identitys.push(new MWF.widget.Company(item.data, this.node, explorer));
                        }.bind(this));
                        if (this.options.type.toLowerCase()=="duty") {
                            items.each(function(item){
                                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector.DutyInput(this, item.data, this.node, explorer, 20000);
                            }.bind(this));


                            //var _self = this;
                            //items.each(function(item){
                            //    item.data.id = new MWF.widget.UUID().toString();
                            //    this.identitys.push(new MWF.widget.Duty(item.data, this.node, explorer, true, function(){
                            //        _self.fireEvent("removeDuty", [this]);
                            //    }));
                            //}.bind(this));
                        }

                        this.fireEvent("change", [this.identitys]);
                    }.bind(this));
                }.bind(this)
            };

            var selector = new MWF.OrgSelector(this.app.content, options);
        }.bind(this));
    }
});
MWF.xApplication.process.ProcessDesigner.widget.PersonSelector.DutyInput = Class({
    Implements: [Events],
    initialize: function(selector, data, node, explorer, zIndex){
        this.itemNode = $(node);
        this.data = data;
        this.isNew = false;

        this.selector = selector;
        this.css = this.selector.css;
        this.app = this.selector.app;
        this.explorer = explorer;
        this.zIndex = zIndex;

        this.selector.identitys = [];
        this.load();
    },
    load: function(){
        this.css.dutyMaskNode["z-index"] = this.zIndex;
        this.app.content.mask({
            "destroyOnHide": true,
            "style": this.css.dutyMaskNode,
        });
        this.node = new Element("div", {
            "styles": this.css.dutyInputArea
        });

        this.titleNode = new Element("div", {
            "styles": this.css.dutyTitleNode
        }).inject(this.node);

        this.titleActionNode = new Element("div", {
            "styles": this.css.dutyTitleActionNode
        }).inject(this.titleNode);
        this.titleTextNode = new Element("div", {
            "styles": this.css.dutyTitleTextNode,
            "text": this.app.lp.dutyInputTitle
        }).inject(this.titleNode);

        this.contentNode = new Element("div", {
            "styles": this.css.dutyContentNode
        }).inject(this.node);
        this.loadContent();

        this.actionNode = new Element("div", {
            "styles": this.css.dutyActionNode
        }).inject(this.node);
        this.actionNode.setStyle("text-align", "center");
        this.loadAction();

        this.node.setStyle("z-index", this.zIndex.toInt()+1);
        this.node.inject(this.app.content);
        this.node.position({
            relativeTo: this.app.content,
            position: "center",
            edge: "center"
        });

        var size = this.app.content.getSize();
        var nodeSize = this.node.getSize();
        this.node.makeDraggable({
            "handle": this.titleNode,
            "limit": {
                "x": [0, size.x-nodeSize.x],
                "y": [0, size.y-nodeSize.y]
            }
        });

        this.setEvent();
    },
    setEvent: function(){
        if (this.titleActionNode){
            this.titleActionNode.addEvent("click", function(){this.selector.fireEvent("cancel"); this.close();}.bind(this));
        }
        this.okActionNode.addEvent("click", function(){
            this.selectDuty();
            this.close();
        }.bind(this));
        this.cancelActionNode.addEvent("click", function(){this.selector.fireEvent("cancel"); this.close();}.bind(this));
    },
    selectDuty: function(){
        var code = this.scriptEditor.editor.editor.getValue();
        this.data.code = code;
        if (!this.item){
            var dutyItem = new MWF.widget.Duty(this.data, this.itemNode, this.explorer, true, function(e){
                var _self = this;
                var text = this.selector.app.lp.deleteDutyText.replace(/{duty}/g, this.data.name);
                this.selector.app.confirm("warm", e, this.selector.app.lp.deleteDutyTitle, text, 300, 120, function(){
                    _self.selector.fireEvent("removeDuty", [_self]);
                    this.close();
                }, function(){
                    this.close();
                });
                e.stopPropagation();
            });
            dutyItem.selector = this.selector;
            dutyItem.explorer = this.explorer;

            this.selector.identitys.push(dutyItem);
            this.selector.fireEvent("change", [this.selector.identitys]);
        }else{
            this.selector.identitys.push(this.item);
            this.selector.fireEvent("change", [this.selector.identitys]);
        }
    },
    "close": function(){
        this.node.destroy();
        this.app.content.unmask();
        MWF.release(this);
        delete this;
    },
    loadAction: function(){
        this.okActionNode = new Element("button", {
            "styles": this.css.dutyOkActionNode,
            "text": "确　定"
        }).inject(this.actionNode);
        this.cancelActionNode = new Element("button", {
            "styles": this.css.dutyCancelActionNode,
            "text": "取 消"
        }).inject(this.actionNode);
    },
    loadContent: function(){
        this.contentAreaNode= new Element("div", {"styles": this.css.dutyContentAreaNode}).inject(this.contentNode);
        var text = this.app.lp.dutyInput.replace(/{duty}/g, this.data.name);
        this.textNode = new Element("div", {"styles": this.css.dutyTextNode, "text": text}).inject(this.contentAreaNode);
        this.referenceAreaNode = new Element("div", {"styles": this.css.dutyReferenceAreaNode}).inject(this.contentAreaNode);
        this.scriptAreaNode = new Element("div", {"styles": this.css.dutyScriptAreaNode}).inject(this.contentAreaNode);
        this.createScriptNode();

        this.createReference(this.app.lp.creatorCompany, "return this.workContext.getWork().creatorCompany");
        this.createReference(this.app.lp.creatorDepartment, "return this.workContext.getWork().creatorDepartment");
        this.createReference(this.app.lp.currentCompany, "return this.workContext.getTask().company");
        this.createReference(this.app.lp.currentDepartment, "return this.workContext.getTask().department");

    },
    createScriptNode: function(){
        this.scriptNode = new Element("div", {"styles": this.css.dutyScriptNode}).inject(this.scriptAreaNode);
        MWF.xDesktop.requireApp("process.ProcessDesigner", "widget.ScriptText", function(){
            this.scriptEditor = new MWF.xApplication.process.ProcessDesigner.widget.ScriptText(this.scriptNode, "", this.app, {
                "height": 316,
                "maskNode": this.app.content,
                "maxObj": this.app.content
                //"onChange": function(code){
                //    _self.data[node.get("name")] = code;
                //}
            });
            this.scriptEditor.loadEditor();
            if (this.data.code) this.scriptEditor.editor.editor.setValue(this.data.code);
        }.bind(this));
    },
    createReference: function(text, code){
        var node = new Element("div", {"styles": this.css.dutyReferenceItemNode}).inject(this.referenceAreaNode);
        node.set("text", text);
        node.store("code", code);
        var css = this.css.dutyReferenceItemNode;
        var overcss = this.css.dutyReferenceItemNode_over;
        var downcss = this.css.dutyReferenceItemNode_down;
        var _self = this;
        node.addEvents({
            "mouseover": function(){this.setStyles(overcss);},
            "mouseout": function(){this.setStyles(css);},
            "mousedown": function(){this.setStyles(downcss);},
            "mouseup": function(){this.setStyles(overcss);},
            "click": function(){
                var code = this.retrieve("code");
                var value = _self.scriptEditor.editor.editor.getValue();
                if (!value){
                    _self.scriptEditor.editor.editor.setValue(code);
                }else{
                    value = value + "\n" +code;
                    _self.scriptEditor.editor.editor.setValue(value);
                }
            }
        });
    }
});

MWF.widget.Duty = new Class({
    Extends: MWF.widget.Department,
    setEvent: function(){
        this.node.addEvent("click", function(){
            this.modifyDuty();
        }.bind(this));
    },
    modifyDuty: function(){
        var dutyInput = new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector.DutyInput(this.selector, this.data, this.selector.node, this.explorer, 20000);
        dutyInput.item = this;
    }
});
