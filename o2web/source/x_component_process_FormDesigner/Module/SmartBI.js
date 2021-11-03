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
		this.BIOptions = {
			"showtoolbar":this.json.smartbidisplaytoolbar||false,  //showtoolbar对应SmartBI系统中的参数名
			"showLeftTree":this.json.smartbidisplaylefttree||false //即席查询是否显示左侧树形
		}

        if (name=="smartbiresource"){ 
            var value = this.json.smartbiresource||"none"; 
			if(value =="" || value == "none"){
				this.setNodeContainer()
			}else{
				this.createIframeNode(value,this.BIOptions)
			}
        }
		if(name=="smartbidisplaytoolbar"){
			if (this.json.smartbiresource && this.json.smartbiresource!=="none"){
				this.createIframeNode(this.json.smartbiresource,this.BIOptions)
			}
		}
		if(name=="smartbidisplaylefttree"){
			if (this.json.smartbiresource && this.json.smartbiresource!=="none"){
				this.createIframeNode(this.json.smartbiresource,this.BIOptions)
			}
		}
    },
	_loadNodeStyles: function(){
		var _iframe = this.node.getElements("iframe");
		if(_iframe.length>0){
			_iframe[0].setStyles(this.css.iframe)
		}
	},
	createIframeNode:function(id,options){
		var value = id;
		var SmartBIAction = o2.Actions.load("x_custom_smartbi_assemble_control");
		var address = SmartBIAction.ResourceAction.action.getAddress();
		var uri = SmartBIAction.ResourceAction.action.actions.open.uri;
		var url = uri.replace("{id}", encodeURIComponent(value));

		if(options){ 
			var paraString = "";
			for(var key in options){
				paraString = paraString + "&" + key + "=" + options[key]
			}
			url = url + "?"+paraString
		}
		
		url = o2.filterUrl(address+url);
		this.node.empty(); 
		
		new Element("iframe",{src:url,styles:this.css.iframe,frameborder:"0",scrolling:"auto"}).inject(this.node);
	},
	_createNode: function(){
		this.node = this.moveNode.clone(true, true);
		this.node.setStyles(this.css.moduleNode);
		this.node.set("id", this.json.id);
		this.node.addEvent("selectstart", function(){
			return false;
		});
	},
	_resetModuleDomNode: function(){
		this.setNodeContainer();
	},
	setNodeContainer:function(){
		
		this.node.empty();
		this.nodeContainer = new Element("div",{styles:this.css.nodeContainer}).inject(this.node);
		this.nodeIcon = new Element("div", {
			"styles": this.css.nodeIcon
		}).inject(this.nodeContainer);

		if(!layout.serviceAddressList["x_custom_smartbi_assemble_control"]){
            new Element("div",{styles:this.css.nodeTxt,text:MWF.APPFD.LP.smartbi.nosetup}).inject(this.nodeContainer);
		}else{
            var t = new Element("div",{styles:this.css.nodeTxt,text:MWF.APPFD.LP.smartbi.txt}).inject(this.nodeContainer);
            t.setStyles({"color":"#409EFF"});

        }
	}
});
