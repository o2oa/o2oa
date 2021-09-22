MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.SmartBI = MWF.FCSmartBI = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/SmartBI/smartbi.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/SmartBI/";
		this.cssPath = "../x_component_process_FormDesigner/Module/SmartBI/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "container";
		this.moduleName = "SmartBI";
		
		this.Node = null;
		this.form = form;
	},
    	
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "div",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
	},
    _setEditStyle: function(name){ 
        if (name=="smartbiresource"){
            var value = this.json.smartbiresource; 
			if(value =="" || value == "none"){
				this.setNodeContainer()
			}else{
				var SmartBIAction = o2.Actions.load("x_custom_smartbi_assemble_control");
				var address = SmartBIAction.ResourceAction.action.getAddress();
				var uri = SmartBIAction.ResourceAction.action.actions.open.uri;
				var url = uri.replace("{id}", encodeURIComponent(value));
				
				url = o2.filterUrl(address+url);
				this.node.empty(); 
				
				new Element("iframe",{src:url,styles:{"width":"100%","height":"100%","min-height":"500px"},frameborder:"0",scrolling:"auto"}).inject(this.node);
			}
        }
    },
	_createNode: function(){
        
		this.node = this.moveNode.clone(true, true);
		this.node.setStyles(this.css.moduleNode);
		this.node.set("id", this.json.id);
		this.node.addEvent("selectstart", function(){
			return false;
		});

        this.setNodeContainer();

	},
	setNodeContainer:function(){
		this.node.empty();
		this.nodeContainer = new Element("div",{styles:this.css.nodeContainer}).inject(this.node);
		this.nodeIcon = new Element("div", {
			"styles": this.css.nodeIcon
		}).inject(this.nodeContainer);

		if(!layout.serviceAddressList["x_custom_smartbi_assemble_control"]){
            new Element("div",{styles:this.css.nodeTxt,text:"请先安装SmartBI应用"}).inject(this.nodeContainer);
		}else{
            var t = new Element("div",{styles:this.css.nodeTxt,text:"SmartBI报表"}).inject(this.nodeContainer);
            t.setStyles({"color":"#409EFF"});
        }
	}
});
