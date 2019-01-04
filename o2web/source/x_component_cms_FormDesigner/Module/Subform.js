MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Subform", null, false);
MWF.xApplication.cms.FormDesigner.Module.Subform = MWF.CMSFCSubform = new Class({
	Extends: MWF.FCSubform,
    Implements : [MWF.CMSFCMI],

    openSubform: function(e){
        if (this.json.subformSelected && this.json.subformSelected!=="none" && this.json.subformType!=="script"){
            layout.desktop.openApplication(e, "cms.FormDesigner", {"id": this.json.subformSelected, "appId": "FormDesigner"+this.json.subformSelected});
        }
    },

    refreshSubform: function(){
        if (this.json.subformSelected && this.json.subformSelected!=="none" && this.json.subformType!=="script"){
            MWF.Actions.get("x_cms_assemble_control").getForm(this.json.subformSelected, function(json){
                if (this.subformData.updateTime!==json.data.updateTime){
                    var select = null;
                    if (this.property){
                        select = $(this.property.data.pid+"selectSubform").getElement("select");
                    }
                    this.clearSubformList(this.json.subformSelected);
                    this.reloadSubform(json.data, select, "");
                }
            }.bind(this));
        }
    },

    redoSelectedSubform: function(name, input, oldValue){
        if (this.json.subformSelected==="none") this.json.subformSelected="";
        if (this.json.subformSelected && this.json.subformSelected!=="none"){
            if (this.form.subformList && this.form.subformList[this.json.subformSelected]){
                //var p = (input) ? input.getPosition() : this.node.getPosition();
                var p = this.node.getPosition(document.bosy);
                this.form.designer.alert("error", {
                    "event": {
                        "x": p.x+150,
                        "y": p.y+80
                    }
                }, this.form.designer.lp.subformConflictTitle, this.form.designer.lp.subformConflictInfor, 400, 120);
                this.json.subformSelected = oldValue;
                if (input){
                    for (var i=0; i<input.options.length; i++){
                        if (input.options[i].value===oldValue){
                            input.options[i].set("selected", true);
                            break;
                        }
                    }
                }
                this.node.empty();
                this.loadIcon();
            }else{
                MWF.Actions.get("x_cms_assemble_control").getForm(this.json.subformSelected, function(json){
                    this.reloadSubform(json.data, input, oldValue);
                }.bind(this));
            }
        }else{
            this.subformData = null;
            this.clearSubformList(oldValue);
            this.node.empty();
            this.loadIcon();
        }
    },


    regetSubformData: function(){
	    var flag = false;
        if (this.json.subformSelected && this.json.subformSelected!=="none" && this.json.subformType!=="script"){
            MWF.Actions.get("x_cms_assemble_control").getForm(this.json.subformSelected, function(json){
                if (!this.subformData || this.subformData.updateTime!==json.data.updateTime){
                    this.getSubformData(json.data);
                    flag = true;
                }
            }.bind(this), null, false);
        }
        return flag;
    },

    checkSubform: function(data, input){
	    var moduleNames = this.getConflictFields();

        if (moduleNames.length){
            var txt = this.form.designer.lp.subformNameConflictInfor;
            txt = txt.replace("{name}", moduleNames.join(", "));

            this.form.designer.notice(txt, "error", this.node);

            return false;
        }
		return true;
    },
	loadSubform: function(data) {
        this.subformData.json.style = this.form.json.style;
        this.subformData.json.properties = this.form.json.properties;
        this.subformData.json.jsheader = {"code": "", "html": ""};
        this.subformData.json.events = {};
        this.subformData.json.formStyleType = this.form.json.formStyleType;
        //this.subformData.json.id = this.json.id;

        this.subformModule = new MWF.CMSFCSubform.Form(this.form, this.node);
        this.subformModule.load(this.subformData);

       //this.createRefreshNode();
    }
});

MWF.xApplication.cms.FormDesigner.Module.Subform.Form = new Class({
    Extends: MWF.CMSFCForm,
    initialize: function(form, container, options){
    	this.parentform = form;
        this.css = this.parentform.css;

        this.container = container;
        this.form = this;
        this.isSubform = true;
        this.moduleType = "subform";

        this.moduleList = [];
        this.moduleNodeList = [];

        this.moduleContainerNodeList = [];
        this.moduleElementNodeList = [];
        this.moduleComponentNodeList = [];

        //	this.moduleContainerList = [];
        this.dataTemplate = {};

        this.designer = this.parentform.designer;
        this.selectedModules = [];
    },
    load : function(data){
        this.data = data;
        this.json = data.json;
        this.html = data.html;
        this.json.mode = this.options.mode;

        this.container.set("html", this.html);

        this.loadDomModules();
        //this.setCustomStyles();
        //this.node.setProperties(this.json.properties);
        //this.setNodeEvents();
        if (this.options.mode==="Mobile"){
            if (oldStyleValue) this._setEditStyle("formStyleType", null, oldStyleValue);
        }
    },
    loadDomModules: function(){
        this.node = this.container.getFirst();
        this.node.set("id", this.json.id);
        this.node.setStyles((this.options.mode==="Mobile") ? this.css.formMobileNode : this.css.formNode);
        this.node.store("module", this);
        this.loadDomTree();
    },
    loadDomTree: function(){
        this.createFormTreeNode();
        this.parseModules(this, this.node);
    },
    createFormTreeNode: function(){
        this.treeNode = {
            "insertChild": function(){return this;},
            "appendChild": function(){return this;},
            "selectNode": function(){},
            "node": null,
            "parentNode": {}
        };
        this.treeNode.module = this;
    }
});