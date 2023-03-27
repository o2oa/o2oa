MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.service = MWF.xApplication.service || {};
MWF.xApplication.service.ScriptDesigner = MWF.xApplication.service.ScriptDesigner || {};
MWF.xDesktop.requireApp("process.ScriptDesigner", "Script", null, false);
MWF.xApplication.service.ScriptDesigner.Script = new Class({
    Extends: MWF.xApplication.process.ScriptDesigner.Script,
    initialize: function(designer, data, options){
        this.setOptions(options);

        this.path = "../x_component_process_ScriptDesigner/$Script/";
        this.cssPath = "../x_component_process_ScriptDesigner/$Script/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.isChanged = false;

        this.designer = designer;
        this.data = data;
        if (!this.data.text) this.data.text = "";
        this.node = this.designer.designNode;
        this.tab = this.designer.scriptTab;

        this.areaNode = new Element("div", {"styles": {"overflow": "hidden", "height": "700px"}});
        this.propertyIncludeNode = this.designer.propertyDomArea;
        this.propertyNode = this.designer.propertyContentArea;

        this.isNewScript = (this.data.id) ? false : true;
    //    this.createProperty();

        this.autoSave();
        this.designer.addEvent("queryClose", function(){
            if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
        }.bind(this));
    },


    save: function(callback){
        if (!this.isSave){
            var validated = this.editor.validated();

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
            this.data.text = this.editor.getValue();

            this.isSave = true;
            var successCallback = function (json) {
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
            }.bind(this);
            var failCallback = function(xhr, text, error){
                this.isSave = false;

                var errorText = error+":"+text;
                if (xhr) errorText = xhr.responseText;
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
            }.bind(this);

            if (!this.data.id){
                this.designer.actions.addScript( this.data, successCallback, failCallback );
            }else{
                this.designer.actions.updateScript( this.data.id, this.data, successCallback, failCallback );
            }
        }else{
            MWF.xDesktop.notice("info", {x: "right", y:"top"}, this.designer.lp.isSave);
        }

    },
    saveSilence: function(callback){
        if (!this.isSave){

            var validated = this.editor.validated();
            if( this.designer.currentScript == this ) {
                var name = this.designer.propertyNameNode.get("value");
                var alias = this.designer.propertyAliasNode.get("value");
                var description = this.designer.propertyDescriptionNode.get("value");
                if (!name) {
                    this.designer.notice(this.designer.lp.notice.inputName, "error");
                    return false;
                }
                this.data.name = name;
                this.data.alias = alias;
                this.data.description = description;
                this.data.validated = validated;
            }
            this.data.text = this.editor.getValue();

            this.isSave = true;
            var successCallback = function(json){
                this.isSave = false;
                this.data.isNewScript = false;
                this.isChanged = false;
                this.page.textNode.set("text", this.data.name);
                if (this.lisNode) {
                    this.lisNode.getLast().set("text", this.data.name);
                }
                this.data.id = json.data.id;
                if (callback) callback();
            }.bind(this);
            var failCallback = function(xhr, text, error){
                this.isSave = false;
            }.bind(this);
            if (!this.data.id){
                this.designer.actions.addScript( this.data, successCallback, failCallback );
            }else{
                this.designer.actions.updateScript( this.data.id, this.data, successCallback, failCallback );
            }
        }else{
            MWF.xDesktop.notice("info", {x: "right", y:"top"}, this.designer.lp.isSave);
        }
    }
});
