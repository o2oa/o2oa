MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.ProcessDesigner = MWF.xApplication.process.ProcessDesigner || {};
MWF.xApplication.process.ProcessDesigner.widget = MWF.xApplication.process.ProcessDesigner.widget || {};
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.require("MWF.widget.O2Identity", null, false);

MWF.xApplication.process.ProcessDesigner.widget.PersonSelector = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "default",
        "type": "identity",
        "count": 0,
        "names": []
    },
    initialize: function(node, app, options){
        this.setOptions(options);
        this.node = $(node);
        this.app = app;

        debugger;

        this.path = "../x_component_process_ProcessDesigner/widget/$PersonSelector/";
        this.cssPath = "../x_component_process_ProcessDesigner/widget/$PersonSelector/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.identitys = [];
        this.restActions = MWF.Actions.get("x_organization_assemble_control");
        //this.restActions = new MWF.xApplication.Selector.Actions.RestActions();

        this.name = this.node.get("name");
        this.load();

    },
    load: function(){
        this.node.setStyles(this.css.node);
        this.createAddNode();
        this.loadIdentitys();
    },
    setData: function( data ){
        this.node.empty();
        this.identitys = [];
        this.options.names = data;
        this.loadIdentitys();
    },
    loadIdentitys: function(){
        if (this.options.names){
            if (this.options.type.toLowerCase()==="duty"){
                var dutys = JSON.decode(this.options.names);
                dutys.each(function(d){
                    var dutyItem = new MWF.widget.O2Duty(d, this.node, {
                        "canRemove": true,
                        "onRemove": function(item, e){
                            var _self = this;
                            var text = this.app.lp.deleteDutyText.replace(/{duty}/g, item.data.name);
                            this.app.confirm("warm", e, this.app.lp.deleteDutyTitle, text, 300, 120, function(){
                                _self.identitys.erase(item);
                                _self.fireEvent("removeDuty", [item]);
                                this.close();
                            }, function(){
                                this.close();
                            });
                            e.stopPropagation();
                        }.bind(this)
                    });
                    dutyItem.selector = this;
                    this.identitys.push(dutyItem);
                }.bind(this));
            }else{
                var names = this.options.names;
                if( typeOf(names) === "string" ){
                    names = JSON.parse( names );
                }
                names.each(function(name){
                    debugger;
                    if (name){
                        var data = (typeOf(name)==="string") ? {"name": name, "id": name}: name;
                        MWF.require("MWF.widget.O2Identity", function(){
                            var type = this.options.type.toLowerCase();
                            if (type==="identity") this.identitys.push(new MWF.widget.O2Identity(data, this.node));
                            if (type==="unit") this.identitys.push(new MWF.widget.O2Unit(data, this.node));
                            if (type==="group") this.identitys.push(new MWF.widget.O2Group(data, this.node));
                            if (type==="person") this.identitys.push(new MWF.widget.O2Person(data, this.node));

                            if (type==="application") this.identitys.push(new MWF.widget.O2Application(data, this.node));
                            if (type==="process") this.identitys.push(new MWF.widget.O2Process(data, this.node));
                            if (type==="formfield") this.identitys.push(new MWF.widget.O2FormField(data, this.node));
                            if (type==="view") this.identitys.push(new MWF.widget.O2View(data, this.node));
                            if (type==="cmsview") this.identitys.push(new MWF.widget.O2CMSView(data, this.node));
                            if (type==="queryview") this.identitys.push(new MWF.widget.O2QueryView(data, this.node));
                            if (type==="querystatement") this.identitys.push(new MWF.widget.O2QueryStatement(data, this.node));
                            if (type==="querystat") this.identitys.push(new MWF.widget.O2QueryStat(data, this.node));
                            if (type==="querytable") this.identitys.push(new MWF.widget.O2QueryTable(data, this.node));
                            if (type==="queryimportmodel") this.identitys.push(new MWF.widget.O2QueryImportModel(data, this.node));
                            if (type==="dutyname") this.identitys.push(new MWF.widget.O2Duty(data, this.node));
                            if (type==="cmsapplication") this.identitys.push(new MWF.widget.O2CMSApplication(data, this.node));
                            if (type==="cmscategory") this.identitys.push(new MWF.widget.O2CMSCategory(data, this.node));

                            if (type==="portalfile") this.identitys.push(new MWF.widget.O2File(data, this.node));
                            if (type==="processfile") this.identitys.push(new MWF.widget.O2File(data, this.node));

                            if (type==="dictionary") this.identitys.push(new MWF.widget.O2Dictionary(data, this.node));
                            if (type==="script") this.identitys.push(new MWF.widget.O2Script(data, this.node));
                            if (type==="formstyle") this.identitys.push(new MWF.widget.O2FormStyle(data, this.node));
                        }.bind(this));
                    }
                }.bind(this));
            }
        }

    },
    createAddNode: function(){
        this.addNode = new Element("div", {"styles": this.css.addPersonNode}).inject(this.node, "before");
        this.addNode.addEvent("click", function(e){
            debugger;

            var include = [];
            if( this.options.type.toLowerCase()==="formfield" ){
                if( this.app.process && this.app.process.routes ){
                    Object.each( this.app.process.routes, function(route){
                        if(route.data.selectConfig){
                            var array = JSON.parse( route.data.selectConfig );
                            ( array || [] ).each( function( d ){
                                include.push( { name : d.name, id : d.id, form: "routeSelectConfig" } );
                            })
                        }
                    });
                }
            }

            var selecteds = [];
            this.identitys.each(function(id){selecteds.push(id.data)});
            var options = {
                "type": (this.options.type.toLowerCase()==="dutyname") ? "duty" : this.options.type,
                "application": this.options.application,
                "fieldType": this.options.fieldType,
                "count": (this.options.type.toLowerCase()==="duty")? 1: this.options.count,
                "values": selecteds,
                "zIndex": 20000,
                "isImage": this.options.isImage,
                "include" : include,
                "onComplete": function(items){
                    if( typeOf(this.options.validFun)==="function" && !this.options.validFun( items ) ){
                        return;
                    }
                    this.identitys = [];
                    if (this.options.type.toLowerCase()!=="duty") this.node.empty();
                    var type = this.options.type.toLowerCase();
                    MWF.require("MWF.widget.O2Identity", function(){
                        items.each(function(item){
                            if (type==="identity") this.identitys.push(new MWF.widget.O2Identity(item.data, this.node));
                            if (type==="person") this.identitys.push(new MWF.widget.O2Person(item.data, this.node));
                            if (type==="unit") this.identitys.push(new MWF.widget.O2Unit(item.data, this.node));
                            if (type==="group") this.identitys.push(new MWF.widget.O2Group(item.data, this.node));

                            if (type==="application") this.identitys.push(new MWF.widget.O2Application(item.data, this.node));
                            if (type==="process") this.identitys.push(new MWF.widget.O2Process(item.data, this.node));
                            if (type==="cmsapplication") this.identitys.push(new MWF.widget.O2CMSApplication(item.data, this.node));
                            if (type==="cmscategory") this.identitys.push(new MWF.widget.O2CMSCategory(item.data, this.node));

                            if (type==="formfield") this.identitys.push(new MWF.widget.O2FormField(item.data, this.node));
                            if (type==="view") this.identitys.push(new MWF.widget.O2View(item.data, this.node));
                            if (type==="cmsview") this.identitys.push(new MWF.widget.O2CMSView(item.data, this.node));
                            if (type==="queryview") this.identitys.push(new MWF.widget.O2QueryView(item.data, this.node));
                            if (type==="querystatement") this.identitys.push(new MWF.widget.O2QueryStatement(item.data, this.node));
                            if (type==="querystat") this.identitys.push(new MWF.widget.O2QueryStat(item.data, this.node));
                            if (type==="querytable") this.identitys.push(new MWF.widget.O2QueryTable(item.data, this.node));
                            if (type==="queryimportmodel") this.identitys.push(new MWF.widget.O2QueryImportModel(item.data, this.node));
                            if (type==="dutyname") this.identitys.push(new MWF.widget.O2Duty(item.data, this.node));
                            if (type==="portalfile") this.identitys.push(new MWF.widget.O2File(item.data, this.node));
                            if (type==="processfile") this.identitys.push(new MWF.widget.O2File(item.data, this.node));

                            if (type==="dictionary") this.identitys.push(new MWF.widget.O2Dictionary(item.data, this.node));
                            if (type==="script") this.identitys.push(new MWF.widget.O2Script(item.data, this.node));
                            if (type==="formstyle") this.identitys.push(new MWF.widget.O2FormStyle(item.data, this.node));
                        }.bind(this));
                        if (type==="duty") {
                            items.each(function(item){
                                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector.DutyInput(this, item.data, this.node, 20000);
                            }.bind(this));
                        }
                        this.fireEvent("change", [this.identitys]);
                    }.bind(this));
                }.bind(this)
            };
            if( this.options.title )options.title = this.options.title;
            if( this.options.selectorOptions ){
                options = Object.merge(options, this.options.selectorOptions );
            }
            var selector = new MWF.O2Selector(this.app.content, options);
        }.bind(this));
    }
});
MWF.xApplication.process.ProcessDesigner.widget.PersonSelector.DutyInput = Class({
    Implements: [Events],
    initialize: function(selector, data, node, zIndex){
        this.itemNode = $(node);
        this.data = data;
        this.isNew = false;

        this.selector = selector;
        this.css = this.selector.css;
        this.app = this.selector.app;
        this.zIndex = zIndex;

        this.selector.identitys = [];
        this.referenceList = [];
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
            var dutyItem = new MWF.widget.O2Duty(this.data, this.itemNode, {
                "canRemove": true,
                "onRemove": function(item, e){
                    var _self = item;
                    var text = item.selector.app.lp.deleteDutyText.replace(/{duty}/g, item.data.name);
                    item.selector.app.confirm("warm", e, item.selector.app.lp.deleteDutyTitle, text, 300, 120, function(){
                        _self.selector.identitys.erase(item);
                        _self.selector.fireEvent("removeDuty", [item]);
                        this.close();
                    }, function(){
                        this.close();
                    });
                    e.stopPropagation();
                }.bind(this)
            });
            dutyItem.selector = this.selector;

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
            "text": this.app.lp.selectorButton.ok
        }).inject(this.actionNode);
        this.cancelActionNode = new Element("button", {
            "styles": this.css.dutyCancelActionNode,
            "text": this.app.lp.selectorButton.cancel
        }).inject(this.actionNode);
    },
    loadContent: function(){
        this.contentAreaNode= new Element("div", {"styles": this.css.dutyContentAreaNode}).inject(this.contentNode);
        var text = this.app.lp.dutyInput.replace(/{duty}/g, this.data.name);
        this.textNode = new Element("div", {"styles": this.css.dutyTextNode, "text": text}).inject(this.contentAreaNode);
        this.referenceAreaNode = new Element("div", {"styles": this.css.dutyReferenceAreaNode}).inject(this.contentAreaNode);
        this.scriptAreaNode = new Element("div", {"styles": this.css.dutyScriptAreaNode}).inject(this.contentAreaNode);
        this.createScriptNode();
        // this.areaNode = new Element("div", {"styles": this.css.dutyAreaNode}).inject(this.contentAreaNode);
        // this.referenceAreaNode = new Element("div", {"styles": this.css.dutyReferenceAreaNode}).inject(this.areaNode);
        // this.contentAreaNode = new Element("div", {"styles": this.css.dutyContentAreaNode}).inject(this.areaNode);

        this.createReference(this.app.lp.creatorUnit, "return this.workContext.getWork().creatorUnitDn || this.workContext.getWork().creatorUnit;");
        this.createReference(this.app.lp.currentUnit, "return this.workContext.getTask().unitDn || this.workContext.getTask().unit;");
        this.createReference(this.app.lp.selectUnit, "", function(){
            var options = {
                "type": "unit",
                "count": 0,
                "onComplete": function(items){
                    var arr = [];
                    items.each(function (item) {
                        arr.push("\"" + item.data.distinguishedName + "\"");
                    })
                    if(arr.length>1){
                        this.scriptEditor.editor.editor.setValue("return ["+arr.join(",")+"];");
                    }else{
                        this.scriptEditor.editor.editor.setValue("return "+arr.join()+";");
                    }
                }.bind(this)
            };
            new MWF.O2Selector(this.node, options);
        }.bind(this));
        // this.createReference(this.app.lp.scriptUnit, "return this.workContext.getWork().creatorDepartment;");

        // this.createReference(this.app.lp.creatorCompany, "return this.workContext.getWork().creatorCompany");
        // this.createReference(this.app.lp.creatorDepartment, "return this.workContext.getWork().creatorDepartment");
        // this.createReference(this.app.lp.currentCompany, "return this.workContext.getTask().company");
        // this.createReference(this.app.lp.currentDepartment, "return this.workContext.getTask().department");

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
            this.scriptEditor.loadEditor(function(){
                if (this.data.code) this.scriptEditor.editor.editor.setValue(this.data.code);
            }.bind(this));
        }.bind(this));
    },
    createReference: function(text, code, action){
        var node = new Element("div", {"styles": this.css.dutyReferenceItemNode}).inject(this.referenceAreaNode);
        node.set("text", text);
        node.store("code", code);
        node.store("action", action);

        var css = this.css.dutyReferenceItemNode;
        var overcss = this.css.dutyReferenceItemNode_over;
        var downcss = this.css.dutyReferenceItemNode_down;
        var _self = this;
        node.addEvents({
            "mouseover": function(){if (!this.retrieve("checked")) this.setStyles(overcss);},
            "mouseout": function(){if (!this.retrieve("checked")) this.setStyles(css);},
            "mousedown": function(){if (!this.retrieve("checked")) this.setStyles(downcss);},
            "mouseup": function(){if (!this.retrieve("checked")) this.setStyles(overcss);},
            "click": function(){
                // _self.checkedReference(this);
                var action = node.retrieve("action");
                if (action){
                    action();
                }else{
                    var code = this.retrieve("code");
                    var value = _self.scriptEditor.editor.editor.getValue();
                    if (!value){
                        _self.scriptEditor.editor.editor.setValue(code);
                    }else{
                        value = value + "\n" +code;
                        _self.scriptEditor.editor.editor.setValue(value);
                    }
                }
            }
        });
        //this.referenceList.push(node);
    },
    // checkedReference: function(node){
    //     this.referenceList.each(function(node){
    //         this.unCheckedReference(node);
    //     }.bind(this));
    //     node.setStyles(this.css.dutyReferenceItemNode_down);
    //     node.store("checked", true);
    //     action = node.retrieve("action");
    //     if (action) action();
    // },
    // unCheckedReference: function(node){
    //     node.setStyles(this.css.dutyReferenceItemNode);
    //     node.store("checked", false);
    // }
});

MWF.widget.O2Duty = new Class({
    Extends: MWF.widget.O2Group,
    getPersonData: function(){
    },
    setEvent: function(){
        this.node.addEvent("click", function(){
            this.modifyDuty();
        }.bind(this));
    },
    modifyDuty: function(){
        var dutyInput = new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector.DutyInput(this.selector, this.data, this.selector.node, 20000);
        dutyInput.item = this;
    }
});
