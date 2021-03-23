MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.service = MWF.xApplication.service || {};
MWF.xApplication.service.InvokeDesigner = MWF.xApplication.service.InvokeDesigner || {};
MWF.SRVID = MWF.xApplication.service.InvokeDesigner;
MWF.require("MWF.widget.Common", null, false);
MWF.xDesktop.requireApp("service.InvokeDesigner", "lp."+MWF.language, null, false);
MWF.require("MWF.widget.JavascriptEditor", null, false);
MWF.xApplication.service.InvokeDesigner.Invoke = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "showTab": true
    },

    initialize: function(designer, data, options){
        this.setOptions(options);

        this.path = "../x_component_service_InvokeDesigner/$Invoke/";
        this.cssPath = "../x_component_service_InvokeDesigner/$Invoke/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.isChanged = false;

        this.designer = designer;
        this.data = data;
        if (!this.data.text) this.data.text = "";
        this.node = this.designer.designNode;
        this.tab = this.designer.invokeTab;

        this.areaNode = new Element("div", {"styles": {"overflow": "hidden", "height": "700px"}});
        //this.propertyIncludeNode = this.designer.propertyDomArea;
        this.propertyNode = this.designer.propertyContentArea;

        this.isNewInvoke = (this.data.id) ? false : true;
    //    this.createProperty();

        this.autoSave();
        this.designer.addEvent("queryClose", function(){
            if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
        }.bind(this));
    },
    autoSave: function(){
        this.autoSaveTimerID = window.setInterval(function(){
            if (!this.autoSaveCheckNode) this.autoSaveCheckNode = this.designer.contentToolbarNode.getElement("#MWFInvokeAutoSaveCheck");
            if (this.autoSaveCheckNode){
                if (this.autoSaveCheckNode.get("checked")){
                    if (this.isChanged) this.saveSilence();
                }
            }
        }.bind(this), 60000);

    },

    //createProperty: function(){
    //    this.invokePropertyNode = new Element("div", {"styles": this.css.invokePropertyNode}).inject(this.propertyNode);
    //},

    load : function(){
        this.setAreaNodeSize();
        this.designer.addEvent("resize", this.setAreaNodeSize.bind(this));

        this.page = this.tab.addTab(this.areaNode, this.data.name || this.designer.lp.newInvoke, (!this.data.isNewInvoke && this.data.id!=this.designer.options.id));
        this.page.invoke = this;
        this.page.addEvent("show", function(){
            this.designer.invokeListAreaNode.getChildren().each(function(node){
                var scrtip = node.retrieve("invoke");
                if (scrtip.id==this.data.id){
                    if (this.designer.currentListInvokeItem){
                        this.designer.currentListInvokeItem.setStyles(this.designer.css.listInvokeItem);
                    }
                    node.setStyles(this.designer.css.listInvokeItem_current);
                    this.designer.currentListInvokeItem = node;
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
            if (this.lisNode) this.lisNode.setStyles(this.designer.css.listInvokeItem);
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
                // defaultText += "requestText//请求正文\n";
                // defaultText += "request//请求\n";
                // defaultText += "effectivePerson//当前用户\n";
                // defaultText += "********************/\n";

                var lp = this.designer.lp.comment;

                var defaultText = "/********************\n";
                defaultText += "this.entityManager; //"+lp.entityManager+"\n";
                defaultText += "this.applications; //"+lp.applications+"\n";
                defaultText += "this.requestText//"+lp.requestText+"\n";
                defaultText += "this.request//"+lp.request+"\n";
                defaultText += "this.currentPerson//"+lp.currentPerson+"\n";
                defaultText += "this.response//"+lp.response+"\n";
                defaultText += "this.organization; //"+lp.organization+"\n";
                defaultText += "this.org; //"+lp.org+"\n";
                defaultText += "this.service; //"+lp.service+"\n";
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
            //this.editor.addEvent("reference", function(editor, e, e1){
                // if (!this.invokeReferenceMenu){
                //     MWF.require("MWF.widget.ScriptHelp", function(){
                //         this.invokeReferenceMenu = new MWF.widget.ScriptHelp(null, this.editor.editor, {
                //             "onPostLoad": function(){
                //                 this.showReferenceMenu();
                //             }.bind(this)
                //         });
                //         this.invokeReferenceMenu.getEditor = function(){return this.editor.editor;}.bind(this)
                //     }.bind(this));
                // }else{
                //     this.showReferenceMenu();
                // }
            //}.bind(this));

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
        this.invokeReferenceMenu.menu.showIm(e);
    },
    setIncludeNode: function(){
        this.designer.propertyIncludeListArea.empty();
        this.data.dependInvokeList.each(function(name){
            this.designer.addIncludeToList(name);
        }.bind(this));
    },
    setPropertyContent: function(){
        this.designer.propertyIdNode.set("text", this.data.id || "");
        this.designer.propertyNameNode.set("value", this.data.name || "");
        this.designer.propertyAliasNode.set("value", this.data.alias || "");

        this.designer.propertyEnableTokenNode.getElement("option[value='"+ this.data.enableToken +"']").set("selected", true );
        this.designer.propertyEnableNode.getElement("option[value='"+ this.data.enable +"']").set("selected", true );

        this.designer.propertyRemoteAddrRegexNode.set("value", this.data.remoteAddrRegex || "");

        this.designer.propertyLastStartTimeNode.set("text", this.data.lastStartTime || "");
        this.designer.propertyLastEndTimeNode.set("text", this.data.lastEndTime || "");

        this.designer.propertyDescriptionNode.set("value", this.data.description || "");

        this.setInvokeUrlText();
        this.designer.propertyEnableTokenNode.addEvent("change", this.setInvokeUrlText.bind(this));
    },
    setInvokeUrlText: function(){
        debugger
        var action = this.designer.actions.action;
        var url;

        var enableToken = true;
        this.designer.propertyEnableTokenNode.getElements("option").each( function(option){
            if( option.selected ){
                enableToken =  (option.value == "true");
            }
        });

        if (enableToken===true){
            uri = action.address + action.actions.executeToken.uri ;
            uri = uri.replace("{flag}", this.data.alias || this.data.name || this.data.id );
        }else{
            var uri = action.address + action.actions.executeInvoke.uri ;
            uri = uri.replace("{flag}", this.data.alias || this.data.name || this.data.id );
        }
        this.designer.propertyInvokeUriNode.set("text", uri);
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

    saveInvoke: function (data, success, failure) {
        if (data.isNewInvoke) {
            this.designer.actions.createInvoke(data, success, failure);
        } else {
            this.designer.actions.updateInvoke(data.id, data, success, failure);
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
            var remoteAddrRegex = this.designer.propertyRemoteAddrRegexNode.get("value");
            var enable = true;
            this.designer.propertyEnableNode.getElements("option").each( function(option){
                if( option.selected ){
                    enable =  (option.value == "true");
                }
            });

            var enableToken = true;
            this.designer.propertyEnableTokenNode.getElements("option").each( function(option){
                if( option.selected ){
                    enableToken =  (option.value == "true");
                }
            });


            if (!name){
                this.designer.notice(this.designer.lp.notice.inputName, "error");
                return false;
            }
            //if(!remoteAddrRegex){
            //    this.designer.notice(this.designer.lp.notice.inputRemoteAddrRegex, "error");
            //    return false;
            //}
            this.data.name = name;
            this.data.alias = alias;
            this.data.description = description;
            this.data.remoteAddrRegex = remoteAddrRegex;
            this.data.validated = validated;
            this.data.text = this.editor.editor.getValue();
            this.data.enable = enable;
            this.data.enableToken = enableToken;

            this.isSave = true;
            this.saveInvoke(this.data, function(json){
                this.isSave = false;
                if( this.data.isNewInvoke ){
                    this.data.isNewInvoke = false;
                    //this.setButton();
                }
                this.isChanged = false;
                this.page.textNode.set("text", this.data.name);
                if (this.lisNode) {
                    this.lisNode.getLast().set("text", this.data.name);
                }
                this.designer.notice(this.designer.lp.notice.save_success, "success", this.node, {"x": "left", "y": "bottom"});
                this.data.id = json.data.id;
                this.designer.propertyIdNode.set("text", this.data.id );


                this.setInvokeUrlText()
                // var action = this.designer.actions.action;
                // var uri = action.address + action.actions.executeInvoke.uri ;
                // uri = uri.replace("{flag}", this.data.alias || this.data.name || this.data.id );
                // this.designer.propertyInvokeUriNode.set("text", uri);

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
                var remoteAddrRegex = this.designer.propertyRemoteAddrRegexNode.get("value");
                var enable = true;
                this.designer.propertyEnableNode.getElements("option").each( function(option){
                    if( option.selected ){
                        enable =  (option.value == "true");
                    }
                });

                if (!name){
                    this.designer.notice(this.designer.lp.notice.inputName, "error");
                    return false;
                }
                //if(!remoteAddrRegex){
                //    this.designer.notice(this.designer.lp.notice.inputRemoteAddrRegex, "error");
                //    return false;
                //}
                this.data.name = name;
                this.data.alias = alias;
                this.data.description = description;
                this.data.remoteAddrRegex = remoteAddrRegex;
                this.data.validated = validated;
                this.data.enable = enable;
            }
            this.data.text = this.editor.editor.getValue();

            this.isSave = true;
            this.saveInvoke(this.data, function(json){
                this.isSave = false;
                if( this.data.isNewInvoke ){
                    this.data.isNewInvoke = false;
                    //this.setButton();
                }
                this.data.isNewInvoke = false;
                this.isChanged = false;
                this.page.textNode.set("text", this.data.name);
                if (this.lisNode) {
                    this.lisNode.getLast().set("text", this.data.name);
                }
                this.data.id = json.data.id;

                if( this.designer.currentScript == this ){
                    this.designer.propertyIdNode.set("text", this.data.id );

                    this.setInvokeUrlText();
                    // var action = this.designer.actions.action;
                    // var uri = action.address + action.actions.executeInvoke.uri ;
                    // uri = uri.replace("{flag}", this.data.alias || this.data.name || this.data.id );
                    // this.designer.propertyInvokeUriNode.set("text", uri);
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
