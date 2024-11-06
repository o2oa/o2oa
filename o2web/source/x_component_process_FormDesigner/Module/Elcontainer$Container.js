MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Container", null, false);
MWF.xApplication.process.FormDesigner.Module.Elcontainer$Main = MWF.FCElcontainer$Main = new Class({
	Extends: MWF.FC$Container,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Elcontainer$Container/elcontainer$Container.html",
        "actions": [
            {
                "name": "selectedContainer",
                "icon": "select.png",
                "event": "click",
                "action": "selectedContainer",
                "title": MWF.APPFD.LP.formAction["selectedContainer"]
            }
            // {
            //     "name": "delete",
            //     "icon": "delete1.png",
            //     "event": "click",
            //     "action": "delete",
            //     "title": MWF.APPFD.LP.formAction["delete"]
            // }
        ],
        "injectActions" : [
            {
                "name" : "top",
                "styles" : "injectActionTop",
                "event" : "click",
                "action" : "injectTop",
                "title": MWF.APPFD.LP.formAction["insertTop"]
            },
            {
                "name" : "bottom",
                "styles" : "injectActionBottom",
                "event" : "click",
                "action" : "injectBottom",
                "title": MWF.APPFD.LP.formAction["insertBottom"]
            }
        ]
	},
    _initModuleType: function(){
        this.className = "Elcontainer$Main"
        this.moduleType = "container";
        this.moduleName = "elcontainer$Main";
        this.cssName = "css_main";
    },
	initialize: function(form, options){
		this.setOptions(options);

		this._initModuleType();
		this.path = "../x_component_process_FormDesigner/Module/Elcontainer$Container/";
		this.cssPath = "../x_component_process_FormDesigner/Module/Elcontainer$Container/"+this.options.style+"/"+this.cssName+".wcss";

		this._loadCss();
		this._initModuleType();
		
		this.Node = null;
		this.form = form;
	},
    load : function(json, node, parent){
        this.json = json;
        this.node= node;
        this.node.store("module", this);
        this.node.setStyles(this.css.moduleNode);

        this._loadNodeStyles();
        this._loadNodeCustomStyles();

        this._initModule();
        this._loadTreeNode(parent);
        if (!this.json.id){
            var id = this._getNewId(((parent) ? parent.json.id : null));
            this.json.id = id;
        }
        if (!this.form.json.moduleList[this.json.id]){
            this.form.json.moduleList[this.json.id] = this.json;
        }

        this.parseModules();

        this.parentContainer = this.treeNode.parentNode.module;
        this._setEditStyle_custom("id");
        this.json.moduleName = this.moduleName;
    },
    _dragInLikeElement: function(module){
        return false;
    },
    destroy: function(){
        this.container.containers.erase(this);
        var modules = this._getSubModule();
        modules.each(function(module){
            //module._deleteModule();
            module.destroy();
        });
        this._deleteModule();
    }
});

MWF.xApplication.process.FormDesigner.Module.Elcontainer$Aside = MWF.FCElcontainer$Aside = new Class({
    Extends: MWF.FCElcontainer$Main,
    Implements: [Options, Events],
    _initModuleType: function(){
        this.className = "Elcontainer$Aside";
        this.moduleType = "container";
        this.moduleName = "elcontainer$Aside";
        this.cssName = "css_aside";
    }
});
MWF.xApplication.process.FormDesigner.Module.Elcontainer$Footer = MWF.FCElcontainer$Footer = new Class({
    Extends: MWF.FCElcontainer$Main,
    Implements: [Options, Events],
    _initModuleType: function(){
        this.className = "Elcontainer$Footer";
        this.moduleType = "container";
        this.moduleName = "elcontainer$Footer";
        this.cssName = "css_footer";
    }
});
MWF.xApplication.process.FormDesigner.Module.Elcontainer$Header = MWF.FCElcontainer$Header = new Class({
    Extends: MWF.FCElcontainer$Main,
    Implements: [Options, Events],
    _initModuleType: function(){
        this.className = "Elcontainer$Header";
        this.moduleType = "container";
        this.moduleName = "elcontainer$Header";
        this.cssName = "css_header";
    }
});
