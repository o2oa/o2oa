MWF.xDesktop.requireApp("process.Xform", "Actionbar", null, false);
MWF.xApplication.cms.Xform.ProcessActionbar = MWF.CMSProcessActionbar =  new Class({
    Extends: MWF.APPActionbar,
    reload : function(){
        this._loadUserInterface();
    },
    _loadUserInterface: function(){
        this.toolbarNode = this.node.getFirst("div");
        if(!this.toolbarNode)return;

        this.toolbarNode.empty();

        MWF.require("MWF.widget.Toolbar", function(){
            this.toolbarWidget = new MWF.widget.Toolbar(this.toolbarNode, {
                "style": this.json.style,
                "onPostLoad" : function(){
                    this.fireEvent("afterLoad");
                }.bind(this)
            }, this);
            if (this.json.actionStyles) this.toolbarWidget.css = this.json.actionStyles;
            //alert(this.readonly)


            var CMSActions = [
                {
                    "type": "MWFToolBarButton",
                    "img": "close.png",
                    "title": MWF.xApplication.cms.Xform.LP.closeTitle,
                    "action": "closeDocument",
                    "text": MWF.xApplication.cms.Xform.LP.close,
                    "id": "action_close",
                    "read": true,
                    "edit" : true
                }
                // {
                //     "type": "MWFToolBarButton",
                //     "img": "edit.png",
                //     "title": MWF.xApplication.cms.Xform.LP.editTitle,
                //     "action": "editDocument",
                //     "id": "action_edit",
                //     "text": MWF.xApplication.cms.Xform.LP.edit,
                //     "control": "allowEditDocument",
                //     "read": true,
                //     "edit" : false
                // },
                // {
                //     "type": "MWFToolBarButton",
                //     "img": "save.png",
                //     "title": MWF.xApplication.cms.Xform.LP.saveTitle,
                //     "action": "saveDocument",
                //     "text": MWF.xApplication.cms.Xform.LP.save,
                //     "id": "action_saveData",
                //     "control": "allowSave",
                //     "condition": "",
                //     "read": false,
                //     "edit" : true
                // }
            ];

            if (!this.json.hideSystemTools){
                this.setToolbars(CMSActions, this.toolbarNode, this.readonly);
            }

            if( this.json.multiTools ){ //自定义操作和系统操作混合的情况，用 system : true 来区分系统和自定义
                var jsonStr = JSON.stringify(this.json.multiTools);
                jsonStr = o2.bindJson(jsonStr, {"lp": MWF.xApplication.process.Xform.LP.form});
                this.json.multiTools = JSON.parse(jsonStr);
                this.json.multiTools.each( function (tool) {
                    if( tool.system ){
                    }else{
                        this.setCustomToolbars([tool], this.toolbarNode);
                    }
                }.bind(this));
                this.toolbarWidget.load();
            }else{
                this.setCustomToolbars(this.json.tools || [], this.toolbarNode);
                this.toolbarWidget.load();
            }
        }.bind(this));
        // }
    },
    setToolbars: function(tools, node, readonly, noCondition){
        tools.each(function(tool){
            var flag = true;
            if (tool.control){
                flag = this.form.businessData.control[tool.control];
            }
            if (!noCondition) if (tool.condition){
                var hideFlag = this.form.Macro.exec(tool.condition, this);
                flag = flag && (!hideFlag);
            }
            if (readonly){
                if (!tool.read) flag = false;
            }else{
                if (!tool.edit) flag = false;
            }
            if (flag){
                var actionNode = new Element("div", {
                    "id": tool.id,
                    "MWFnodetype": tool.type,
                    "MWFButtonImage": "../x_component_process_FormDesigner/Module/Actionbar/"+(this.options.style||"default") +"/tools/"+ (this.json.style || "default") +"/"+tool.img,
                    "title": tool.title,
                    "MWFButtonAction": tool.action,
                    "MWFButtonText": tool.text
                }).inject(node);
                if (tool.sub){
                    var subNode = node.getLast();
                    this.setToolbars(tool.sub, subNode);
                }
            }
        }.bind(this));
    },
    runCustomAction: function(bt){
        var script = bt.node.retrieve("script");
        this.form.Macro.exec(script, this);
    },
    saveDocument: function(){
        this.form.saveDocument();
    },
    saveDraftDocument: function(){
        this.form.saveDocument();
    },
    closeDocument: function(){
        this.form.closeDocument();
    },
    publishDocument: function(){
        this.form.publishDocument();
    },
    publishDocumentDelayed: function(){
        this.form.publishDocumentDelayed();
    },
    archiveDocument: function(){
        this.form.archiveDocument();
    },
    redraftDocument: function(){
        this.form.redraftDocument();
    },
    deleteDocument: function(){
        this.form.deleteDocument();
    },
    editDocument: function(){
        this.form.editDocument();
    },
    setPopularDocument: function(){
        this.form.setPopularDocument();
    },
    printDocument: function(){
        this.form.printDocument();
    },
    setTop: function(){
        this.form.setTop();
    },
    cancelTop: function(){
        this.form.cancelTop();
    }
}); 