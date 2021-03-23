MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.service = MWF.xApplication.service || {};
MWF.xApplication.service.AgentDesigner = MWF.xApplication.service.AgentDesigner || {};
MWF.SRVAD = MWF.xApplication.service.AgentDesigner;
MWF.require("MWF.widget.Common", null, false);
MWF.xDesktop.requireApp("service.AgentDesigner", "lp."+MWF.language, null, false);
MWF.require("MWF.widget.JavascriptEditor", null, false);
MWF.xApplication.service.AgentDesigner.Agent = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "showTab": true
    },

    initialize: function(designer, data, options){
        this.setOptions(options);

        this.path = "../x_component_service_AgentDesigner/$Agent/";
        this.cssPath = "../x_component_service_AgentDesigner/$Agent/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.isChanged = false;

        this.designer = designer;
        this.data = data;
        if (!this.data.text) this.data.text = "";
        this.node = this.designer.designNode;
        this.tab = this.designer.agentTab;

        this.areaNode = new Element("div", {"styles": {"overflow": "hidden", "height": "700px"}});
        //this.propertyIncludeNode = this.designer.propertyDomArea;
        this.propertyNode = this.designer.propertyContentArea;

        this.isNewAgent = (this.data.id) ? false : true;
    //    this.createProperty();

        this.autoSave();
        this.designer.addEvent("queryClose", function(){
            if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
        }.bind(this));
    },
    autoSave: function(){
        this.autoSaveTimerID = window.setInterval(function(){
            if (!this.autoSaveCheckNode) this.autoSaveCheckNode = this.designer.contentToolbarNode.getElement("#MWFAgentAutoSaveCheck");
            if (this.autoSaveCheckNode){
                if (this.autoSaveCheckNode.get("checked")){
                    if (this.isChanged) this.saveSilence();
                }
            }
        }.bind(this), 60000);

    },

    //createProperty: function(){
    //    this.agentPropertyNode = new Element("div", {"styles": this.css.agentPropertyNode}).inject(this.propertyNode);
    //},

    load : function(){
        this.setAreaNodeSize();
        this.designer.addEvent("resize", this.setAreaNodeSize.bind(this));

        this.page = this.tab.addTab(this.areaNode, this.data.name || this.designer.lp.newAgent, (!this.data.isNewAgent && this.data.id!=this.designer.options.id));
        this.page.agent = this;
        this.page.addEvent("show", function(){
            this.designer.agentListAreaNode.getChildren().each(function(node){
                var scrtip = node.retrieve("agent");
                if (scrtip.id==this.data.id){
                    if (this.designer.currentListAgentItem){
                        this.designer.currentListAgentItem.setStyles(this.designer.css.listAgentItem);
                    }
                    node.setStyles(this.designer.css.listAgentItem_current);
                    this.designer.currentListAgentItem = node;
                    this.lisNode = node;
                }
            }.bind(this));

            this.designer.currentScript = this;

            this.setPropertyContent();
            //this.setIncludeNode();


            if (this.editor.editor){
                this.editor.editor.focus();
                //this.editor.editor.navigateFileStart();
            }
        }.bind(this));
        this.page.addEvent("queryClose", function(){
            if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
            //this.saveSilence();
            if (this.lisNode) this.lisNode.setStyles(this.designer.css.listAgentItem);
        }.bind(this));
        this.page.tabNode.addEvent("dblclick", this.designer.maxOrReturnEditor.bind(this.designer));



        this.editor = new MWF.widget.JavascriptEditor(this.areaNode, {"runtime": "service"});
        this.editor.load(function(){
            if (this.data.text){
                this.editor.editor.setValue(this.data.text);
            }else{
                // var defaultText = "/********************\n";
                // defaultText += "resources.getEntityManagerContainer(); //实体管理器\n";
                // defaultText += "resources.getContext(); //上下文根\n";
                // defaultText += "resources.getOrganization(); //组织访问\n";
                // defaultText += "resources.getWebservicesClient();//webSerivces客户端\n";
                // defaultText += "********************/\n";
                var lp = this.designer.lp.comment;
                var defaultText = "/********************\n";
                defaultText += "this.entityManager; //"+lp.entityManager+"\n";
                defaultText += "this.applications; //"+lp.applications+"\n";
                defaultText += "this.organization; //"+lp.organization+"\n";
                defaultText += "this.org; //"+lp.org+"\n";
                defaultText += "this.service; ///"+lp.service+"\n";
                defaultText += "********************/\n";
                this.editor.editor.setValue(defaultText);
            }
            this.editor.addEditorEvent("change", function(){
                if (!this.isChanged){
                    this.isChanged = true;
                    this.page.textNode.set("text", " * "+this.page.textNode.get("text"));
                }
            }.bind(this));

            // this.editor.editor.on("change", function(e){
            //     if (!this.isChanged){
            //         this.isChanged = true;
            //         this.page.textNode.set("text", " * "+this.page.textNode.get("text"));
            //     }
            // }.bind(this));
            this.editor.addEvent("save", function(){
                this.save();
            }.bind(this));
            // this.editor.addEvent("reference", function(editor, e, e1){
            //     if (!this.agentReferenceMenu){
            //         MWF.require("MWF.widget.ScriptHelp", function(){
            //             this.agentReferenceMenu = new MWF.widget.ScriptHelp(null, this.editor.editor, {
            //                 "onPostLoad": function(){
            //                     this.showReferenceMenu();
            //                 }.bind(this)
            //             });
            //             this.agentReferenceMenu.getEditor = function(){return this.editor.editor;}.bind(this)
            //         }.bind(this));
            //     }else{
            //         this.showReferenceMenu();
            //     }
            // }.bind(this));

            var options = this.designer.styleSelectNode.options;
            for (var i=0; i<options.length; i++){
                    var option = options[i];
                if (option.value==this.editor.theme){
                    option.set("selected", true);
                    break;
                }
            }

            var options = this.designer.fontsizeSelectNode.options;
            for (var i=0; i<options.length; i++){
                var option = options[i];
                if (option.value==this.editor.fontSize){
                    option.set("selected", true);
                    break;
                }
            }

            options = this.designer.editorSelectNode.options;
            for (var i=0; i<options.length; i++){
                var option = options[i];
                if (option.value==this.editor.options.type){
                    option.set("selected", true);
                    break;
                }
            }
            options = this.designer.monacoStyleSelectNode.options;
            for (var i=0; i<options.length; i++){
                var option = options[i];
                if (option.value==this.editor.theme){
                    option.set("selected", true);
                    break;
                }
            }

            if (this.editor.options.type=="ace"){
                this.designer.monacoStyleSelectNode.hide();
                this.designer.styleSelectNode.show();
            }else{
                this.designer.monacoStyleSelectNode.show();
                this.designer.styleSelectNode.hide();
            }
        }.bind(this));

        if (this.options.showTab) this.page.showTabIm();
    },
    showReferenceMenu: function(){
        var pos = this.editor.getCursorPixelPosition();
        var e = {"page": {}};
        e.page.x = pos.left;
        e.page.y = pos.top;
        this.agentReferenceMenu.menu.showIm(e);
    },
    setIncludeNode: function(){
        this.designer.propertyIncludeListArea.empty();
        this.data.dependAgentList.each(function(name){
            this.designer.addIncludeToList(name);
        }.bind(this));
    },
    setPropertyContent: function(){
        this.designer.propertyIdNode.set("text", this.data.id || "");
        this.designer.propertyNameNode.set("value", this.data.name || "");
        this.designer.propertyAliasNode.set("value", this.data.alias || "");

        this.designer.propertyEnableNode.set("text", this.data.enable ?  this.designer.lp.true : this.designer.lp.false );
        this.designer.propertyCronNode.set("value", this.data.cron || "");
        this.designer.cronValue = this.data.cron || "";
        //this.designer.cronPicker.setCronValue( this.data.cron || "" );

        this.designer.propertyLastStartTimeNode.set("text", this.data.lastStartTime || "");
        this.designer.propertyLastEndTimeNode.set("text", this.data.lastEndTime || "");
        //this.designer.propertyAppointmentTimeNode.set("text", this.data.appointmentTime || "");

        this.designer.propertyDescriptionNode.set("value", this.data.description || "");

        this.setButton()
    },
    setButton : function(){
        this.designer.propertyEnableButton.store("id", this.data.id);
        this.designer.propertyDisableButton.store("id", this.data.id);
        if( this.data.enable ){
            this.designer.propertyEnableButton.setStyle("display","none");
            this.designer.propertyDisableButton.setStyle("display", this.data.isNewAgent ? "none" : "" );
        }else{
            this.designer.propertyEnableButton.setStyle("display",this.data.isNewAgent ? "none" : "");
            this.designer.propertyDisableButton.setStyle("display", "none" );
        }
    },
    setAreaNodeSize: function(){
        var size = this.node.getSize();
        var tabSize = this.tab.tabNodeContainer.getSize();
        var y = size.y - tabSize.y;
        this.areaNode.setStyle("height", ""+y+"px");
        //if (this.editor) if (this.editor.editor) this.editor.editor.resize();
        if (this.editor) this.editor.resize(y);
    },

    addInclude: function(){

    },

    saveAgent: function (data, success, failure) {
        if (data.isNewAgent) {
            this.designer.actions.createAgent(data, success, failure);
        } else {
            this.designer.actions.updateAgent(data.id, data, success, failure);
        }
    },
    save: function(callback){
        if (!this.isSave){
            /*var session = this.editor.editor.getSession();
            var annotations = session.getAnnotations();
            var validated = true;
            for (var i=0; i<annotations.length; i++){
                if (annotations[i].type=="error"){
                    validated = false;
                    break;
                }
            }*/
            var validated = this.editor.validated();
            var name = this.designer.propertyNameNode.get("value");
            var alias = this.designer.propertyAliasNode.get("value");
            var description = this.designer.propertyDescriptionNode.get("value");
            var cron = this.designer.propertyCronNode.get("value");

            if (!name){
                this.designer.notice(this.designer.lp.notice.inputName, "error");
                return false;
            }
            if(!cron){
                this.designer.notice(this.designer.lp.notice.inputCron, "error");
                return false;
            }
            this.data.name = name;
            this.data.alias = alias;
            this.data.description = description;
            this.data.cron = cron;
            this.data.validated = validated;
            this.data.text = this.editor.editor.getValue();

            this.isSave = true;
            this.saveAgent(this.data, function(json){
                this.isSave = false;
                if( this.data.isNewAgent ){
                    this.data.isNewAgent = false;
                    this.setButton();
                }
                this.isChanged = false;
                this.page.textNode.set("text", this.data.name);
                if (this.lisNode) {
                    this.lisNode.getLast().set("text", this.data.name);
                }
                this.designer.notice(this.designer.lp.notice.save_success, "success", this.node, {"x": "left", "y": "bottom"});
                this.data.id = json.data.id;
                this.designer.propertyIdNode.set("text", this.data.id );
                if (callback) callback();
            }.bind(this), function(xhr, text, error){
                this.isSave = false;

                var errorText = error+":"+text;
                if (xhr) errorText = xhr.responseText;
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
            }.bind(this));
        }else{
            MWF.xDesktop.notice("info", {x: "right", y:"top"}, this.designer.lp.isSave);
        }

    },
    saveSilence: function(callback){
        if (!this.isSave){
            var session = this.editor.editor.getSession();
            var annotations = session.getAnnotations();
            var validated = true;
            for (var i=0; i<annotations.length; i++){
                if (annotations[i].type=="error"){
                    validated = false;
                    break;
                }
            }

            if( this.designer.currentScript == this ){
                var name = this.designer.propertyNameNode.get("value");
                var alias = this.designer.propertyAliasNode.get("value");
                var description = this.designer.propertyDescriptionNode.get("value");
                var cron = this.designer.propertyCronNode.get("value");
                if (!name){
                    this.designer.notice(this.designer.lp.notice.inputName, "error");
                    return false;
                }
                if(!cron){
                    this.designer.notice(this.designer.lp.notice.inputCron, "error");
                    return false;
                }
                this.data.name = name;
                this.data.alias = alias;
                this.data.description = description;
                this.data.cron = cron;
                this.data.validated = validated;
            }
            this.data.text = this.editor.editor.getValue();

            this.isSave = true;
            this.saveAgent(this.data, function(json){
                this.isSave = false;
                if( this.data.isNewAgent ){
                    this.data.isNewAgent = false;
                    if( this.designer.currentScript == this ) {
                        this.setButton();
                    }
                }
                this.data.isNewAgent = false;
                this.isChanged = false;
                this.page.textNode.set("text", this.data.name);
                if (this.lisNode) {
                    this.lisNode.getLast().set("text", this.data.name);
                }
                this.data.id = json.data.id;

                if( this.designer.currentScript == this ) {
                    this.designer.propertyIdNode.set("text", this.data.id);
                }
                if (callback) callback();
            }.bind(this), function(xhr, text, error){
                this.isSave = false;
                //
                //var errorText = error+":"+text;
                //if (xhr) errorText = xhr.responseText;
                //MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
            }.bind(this));
        }else{
            MWF.xDesktop.notice("info", {x: "right", y:"top"}, this.designer.lp.isSave);
        }
    },

    saveAs: function(){},
    explode: function(){},
    implode: function(){}

});
