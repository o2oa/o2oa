MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.portal = MWF.xApplication.portal || {};
MWF.xApplication.portal.ScriptDesigner = MWF.xApplication.portal.ScriptDesigner || {};
MWF.require("MWF.widget.Common", null, false);
MWF.xDesktop.requireApp("portal.ScriptDesigner", "lp."+MWF.language, null, false);
MWF.require("MWF.widget.JavascriptEditor", null, false);
MWF.xApplication.portal.ScriptDesigner.Script = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "showTab": true
    },

    initialize: function(designer, data, options){
        this.setOptions(options);

        this.path = "/x_component_portal_ScriptDesigner/$Script/";
        this.cssPath = "/x_component_portal_ScriptDesigner/$Script/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.isChanged = false;

        this.designer = designer;
        this.data = data;
        if (!this.data.text) this.data.text = "";
        this.node = this.designer.designNode;
        this.tab = this.designer.scriptTab;

        this.areaNode = new Element("div", {"styles": {"overflow": "hidden", "height": "700px"}});
        this.propertyIncludeNode = this.designer.propertyDomArea;
        this.propertyNode = this.designer.propertyContentArea

        if(this.designer.application) this.data.applicationName = this.designer.application.name;
        if(this.designer.application) this.data.application = this.designer.application.id;

        this.isNewScript = (this.data.id) ? false : true;
    //    this.createProperty();

        this.autoSave();
        this.designer.addEvent("queryClose", function(){
            if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
        }.bind(this));
    },
    autoSave: function(){
        this.autoSaveTimerID = window.setInterval(function(){
            if (!this.autoSaveCheckNode) this.autoSaveCheckNode = this.designer.contentToolbarNode.getElement("#MWFScriptAutoSaveCheck");
            if (this.autoSaveCheckNode){
                if (this.autoSaveCheckNode.get("checked")){
                    if (this.isChanged) this.saveSilence();
                }
            }
        }.bind(this), 60000);

    },

    //createProperty: function(){
    //    this.scriptPropertyNode = new Element("div", {"styles": this.css.scriptPropertyNode}).inject(this.propertyNode);
    //},

    load : function(){
        this.setAreaNodeSize();
        this.designer.addEvent("resize", this.setAreaNodeSize.bind(this));

        this.page = this.tab.addTab(this.areaNode, this.data.name || this.designer.lp.newScript, (!this.data.isNewScript && this.data.id!=this.designer.options.id));
        this.page.script = this;
        this.page.addEvent("show", function(){
            this.designer.scriptListAreaNode.getChildren().each(function(node){
                var scrtip = node.retrieve("script");
                if (scrtip.id==this.data.id){
                    if (this.designer.currentListScriptItem){
                        this.designer.currentListScriptItem.setStyles(this.designer.css.listScriptItem);
                    }
                    node.setStyles(this.designer.css.listScriptItem_current);
                    this.designer.currentListScriptItem = node;
                    this.lisNode = node;
                }
            }.bind(this));

            this.designer.currentScript = this;

            this.setPropertyContent();
            this.setIncludeNode();

            if (this.editor.editor){
                this.editor.editor.focus();
                //this.editor.editor.navigateFileStart();
            }
        }.bind(this));
        this.page.addEvent("queryClose", function(){
            if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
            //if (this.isChanged) this.saveSilence();
            if (this.lisNode) this.lisNode.setStyles(this.designer.css.listScriptItem);
        }.bind(this));
        this.page.tabNode.addEvent("dblclick", this.designer.maxOrReturnEditor.bind(this.designer));



        this.editor = new MWF.widget.JavascriptEditor(this.areaNode);
        this.editor.load(function(){
            if (this.data.text){
                this.editor.editor.setValue(this.data.text);
            }
            this.editor.editor.on("change", function(e){
                if (!this.isChanged){
                    this.isChanged = true;
                    this.page.textNode.set("text", " * "+this.page.textNode.get("text"));
                }
            }.bind(this));
            this.editor.addEvent("save", function(){
                this.save();
            }.bind(this));
            this.editor.addEvent("reference", function(editor, e, e1){
                if (!this.scriptReferenceMenu){
                    MWF.require("MWF.widget.ScriptHelp", function(){
                        this.scriptReferenceMenu = new MWF.widget.ScriptHelp(null, this.editor.editor, {
                            "onPostLoad": function(){
                                this.showReferenceMenu();
                            }.bind(this)
                        });
                        this.scriptReferenceMenu.getEditor = function(){return this.editor.editor;}.bind(this)
                    }.bind(this));
                }else{
                    this.showReferenceMenu();
                }
            }.bind(this));

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
        }.bind(this));

        if (this.options.showTab) this.page.showTabIm();
    },
    showReferenceMenu: function(){
        var pos = this.editor.getCursorPixelPosition();
        var e = {"page": {}};
        e.page.x = pos.left;
        e.page.y = pos.top;
        this.scriptReferenceMenu.menu.showIm(e);
    },
    setIncludeNode: function(){
        this.designer.propertyIncludeListArea.empty();
        this.data.dependScriptList.each(function(name){
            this.designer.addIncludeToList(name);
        }.bind(this));
    },
    setPropertyContent: function(){
        this.designer.propertyIdNode.set("text", this.data.id || "");
        this.designer.propertyNameNode.set("value", this.data.name || "");
        this.designer.propertyAliasNode.set("value", this.data.alias || "");
        this.designer.propertyDescriptionNode.set("value", this.data.description || "");
    },
    setAreaNodeSize: function(){
        var size = this.node.getSize();
        var tabSize = this.tab.tabNodeContainer.getSize();
        var y = size.y - tabSize.y;
        this.areaNode.setStyle("height", ""+y+"px");
        if (this.editor) if (this.editor.editor) this.editor.editor.resize();
    },

    addInclude: function(){

    },


    save: function(callback){
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

            var name = this.designer.propertyNameNode.get("value");
            var alias = this.designer.propertyAliasNode.get("value");
            var description = this.designer.propertyDescriptionNode.get("value");
            if (!name){
                this.designer.notice(this.designer.lp.notice.inputName, "error");
                return false;
            }
            this.data.name = name;
            this.data.alias = alias;
            this.data.description = description;
            this.data.validated = validated;
            this.data.text = this.editor.editor.getValue();

            this.isSave = true;
            this.designer.actions.saveScript(this.data, function(json){
                this.isSave = false;
                this.data.isNewScript = false;
                this.isChanged = false;
                this.page.textNode.set("text", this.data.name);
                if (this.lisNode) {
                    this.lisNode.getLast().set("text", this.data.name);
                }
                this.designer.notice(this.designer.lp.notice.save_success, "success", this.node, {"x": "left", "y": "bottom"});
                this.data.id = json.data.id;
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
                if (!name){
                    this.designer.notice(this.designer.lp.notice.inputName, "error");
                    return false;
                }
                this.data.name = name;
                this.data.alias = alias;
                this.data.description = description;
                this.data.validated = validated;
            }
            this.data.text = this.editor.editor.getValue();

            this.isSave = true;
            this.designer.actions.saveScript(this.data, function(json){
                this.isSave = false;
                this.data.isNewScript = false;
                this.isChanged = false;
                this.page.textNode.set("text", this.data.name);
                if (this.lisNode) {
                    this.lisNode.getLast().set("text", this.data.name);
                }
                this.data.id = json.data.id;
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