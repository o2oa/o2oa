MWF.xDesktop.requireApp("portal.PageDesigner", "Module.Table$Td", null, false);
MWF.xApplication.portal.PageDesigner.Module.Table = MWF.PCTable = new Class({
	Extends: MWF.FCTable,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_portal_PageDesigner/Module/Table/table.html",
        "propertyMultiPath": "/x_component_portal_PageDesigner/Module/Table$Td/table$tdMulti.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_portal_PageDesigner/Module/Table/";
		this.cssPath = "/x_component_portal_PageDesigner/Module/Table/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "component";
		this.moduleName = "table";

		this.form = form;
		this.container = null;
		this.containerNode = null;
		this.containers = [];
		this.elements = [];
		this.selectedMultiTds = [];
	},
    _getContainers: function(){
        //var tds = this.node.getElements("td");
        var tds = this._getTds();
        this.form.getTemplateData("Table$Td", function(data){
            tds.each(function(td){
                var json = this.form.getDomjson(td);
                var tdContainer = null;
                if (!json){
                    var moduleData = Object.clone(data);
                    tdContainer = new MWF.PCTable$Td(this.form);
                    tdContainer.table = this;
                    tdContainer.load(moduleData, td, this);
                }else{
                    tdContainer = new MWF.PCTable$Td(this.form);
                    tdContainer.table = this;
                    tdContainer.load(json, td, this);
                }
                this.containers.push(tdContainer);
            }.bind(this));
        }.bind(this));
    },
    _getElements: function(){
        //	this.elements.push(this);
        var captions = this.node.getElements("caption");
        captions.each(function(caption){
            var json = this.form.getDomjson(caption);
            var el = null;
            if (!json){
                this.form.getTemplateData("Common", function(data){
                    var moduleData = Object.clone(data);
                    el = new MWF.PCCommon(this.form);
                    el.table = this;
                    el.load(moduleData, caption, this);
                }.bind(this));
            }else{
                el = new MWF.PCCommon(this.form);
                el.table = this;
                el.load(json, caption, this);
            }
            this.elements.push(el);
        }.bind(this));
    },
});