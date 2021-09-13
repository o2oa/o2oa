MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$ElComponent", null, false);
MWF.xApplication.process.FormDesigner.Module.Elcontainer = MWF.FCElcontainer = new Class({
	Extends: MWF.FC$Component,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Elcontainer/elcontainer.html",
		"layoutTemplates": {
			"side-header-main-footer":"<el-container>\n" +
				                      "  <el-aside></el-aside>\n" +
				                      "  <el-container>\n" +
				                      "    <el-header></el-header>\n" +
				                      "    <el-main></el-main>\n" +
				                      "    <el-footer></el-footer>\n" +
				                      "  </el-container>\n" +
				                      "</el-container>",

			"header-main":            "<el-container>\n" +
				                      "  <el-header></el-header>\n" +
				                      "  <el-main></el-main>\n" +
				                      "</el-container>",

			"header-main-footer":      "<el-container>\n" +
				                      "  <el-header></el-header>\n" +
				                      "  <el-main></el-main>\n" +
				                      "  <el-footer></el-footer>\n" +
				                      "</el-container>",

			"header-side-main":       "<el-container>\n" +
				                      "  <el-header></el-header>\n" +
				                      "  <el-container>\n" +
				                      "    <el-aside></el-aside>\n" +
				                      "    <el-main></el-main>\n" +
				                      "  </el-container>\n" +
				                      "</el-container>",

			"header-side-main-footer":"<el-container>\n" +
				                      "  <el-header></el-header>\n" +
				                      "  <el-container>\n" +
				                      "    <el-aside></el-aside>\n" +
				                      "    <el-container>\n" +
				                      "      <el-main></el-main>\n" +
				                      "      <el-footer></el-footer>\n" +
				                      "    </el-container>\n" +
				                      "  </el-container>\n" +
				                      "</el-container>",

			"header-footer-side-main":"<el-container>\n" +
				                      "  <el-header></el-header>\n" +
				                      "  <el-container>\n" +
				                      "    <el-aside></el-aside>\n" +
				                      "    <el-container>\n" +
				                      "      <el-main></el-main>\n" +
				                      "    </el-container>\n" +
				                      "  </el-container>\n" +
				                      "  <el-footer></el-footer>\n" +
				                      "</el-container>",

			"footer-side-header-main":"<el-container>\n" +
				                      "  <el-container>\n" +
				                      "    <el-aside></el-aside>\n" +
				                      "    <el-container>\n" +
				                      "      <el-header></el-header>\n" +
				                      "      <el-main></el-main>\n" +
				                      "    </el-container>\n" +
				                      "  </el-container>\n" +
				                      "  <el-footer></el-footer>\n" +
				                      "</el-container>",

			"side-header-main":       "<el-container>\n" +
				                      "  <el-aside></el-aside>\n" +
				                      "  <el-container>\n" +
				                      "    <el-header></el-header>\n" +
				                      "    <el-main></el-main>\n" +
				                      "  </el-container>\n" +
				                      "</el-container>"

		}
	},

	_initModuleType: function(){
		this.className = "Elcontainer"
		this.moduleType = "component";
		this.moduleName = "elcontainer";
	},

	initialize: function(form, options){
		this.setOptions(options);
		this._initModuleType();
		this.path = "../x_component_process_FormDesigner/Module/"+this.className+"/";
		this.cssPath = "../x_component_process_FormDesigner/Module/"+this.className+"/"+this.options.style+"/css.wcss";
		this._loadCss();
		this.form = form;
		this.container = null;
		this.containerNode = null;
		this.isPropertyLoaded = false;
		this.containers = [];
		this.elements = [];

		//this._dragMoveComplete = this._dragComplete;
	},
	_dragMoveComplete: MWF.FC$Component.prototype._dragComplete,
	_createMoveNode: function(){
		var html = "<section class=\"el-container\" style='height: 100%'>";
		html += "<aside class=\"el-aside\" style=\"width: 40px; background-color: #D3DCE6\"></aside>";
		html += "<section class=\"el-container is-vertical\">";
		html += "<header class=\"el-header\" style=\"height: 20px; background-color: #B3C0D1\"></header>";
		html += "<main class=\"el-main\"></main>";
		html += "<footer class=\"el-footer\" style=\"height: 20px; background-color: #B3C0D1\"></footer>";
		html += "</section>";
		html += "</section>";


		// var tableHTML = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"2\" width=\"100%\" align=\"center\">";
		// tableHTML += "<tr><td></td><td></td><td></td></tr>";
		// tableHTML += "<tr><td></td><td></td><td></td></tr>";
		// tableHTML += "<tr><td></td><td></td><td></td></tr>";
		// tableHTML += "</table>";
		this.moveNode = new Element("div", {
			"html": html
		}).inject(this.form.container);
//		this.moveNode = divNode.getFirst();
//		this.moveNode.inject(divNode, "after");
//		divNode.destroy();

		this.moveNode.setStyles(this.css.moduleNodeMove);
	},

	_createElementHtml: function(){
		//var html = "<el-container>";
		var html = "";
		html +=    "	<el-aside></el-aside>";
		html +=    "	<el-container>";
		html +=    "		<el-header></el-header>";
		html +=    "		<el-main></el-main>";
		html +=    "		<el-footer></el-footer>";
		html +=    "	</el-container>";
		//html +=    "</el-container>";

		return html;
	},
	_createNode: function(callback){
		var module = this;
		var url = this.path+"elcontainerCreate.html";
		MWF.require("MWF.widget.Dialog", function(){
			var size = $(document.body).getSize();
			var x = size.x/2-395;
			var y = size.y/2-280;

			var dlg = new MWF.DL({
				"title": "Create Elcontainer",
				"style": "user",
				"top": y,
				"left": x-40,
				"fromTop":size.y/2-65,
				"fromLeft": size.x/2,
				"width": 790,
				"height": 560,
				"isResize": false,
				"url": url,
				"lp": MWF.xApplication.process.FormDesigner.LP.propertyTemplate,
				"container": layout.desktop.node,
				"buttonList": [
					{
						"text": MWF.APPFD.LP.button.ok,
						"action": function(){
							var value = this.editor.getValue();
							module._createElcontainerNode(callback, value);
							this.close();
						}
					},
					{
						"text": MWF.APPFD.LP.button.cancel,
						"type": "cancel",
						"action": function(){
							module._dragCancel();
							this.close();
						}
					}
				],
				"onPostShow": function(){
					module._loadCreateHtmlEditor(this);
					module._loadCreateLayoutSelect(this);
				}
			});
			dlg.show();
		});
	},

	_loadCreateHtmlEditor: function(dlg){
		var codeNode = dlg.content.getElement(".MWFVueCode");
		o2.require("o2.widget.JavascriptEditor", function(){
			dlg.editor = new o2.widget.JavascriptEditor(codeNode, {
				"option": {
					value: this.options.layoutTemplates["side-header-main-footer"],
					mode: "html",
					"lineNumbers": false
				},
			});
			dlg.editor.load();
		}.bind(this));
	},
	_loadCreateLayoutSelect: function(dlg){
		var imgs = dlg.content.getElements("img");
		imgs.each(function(img){
			img.addEvent("click", function(){
				imgs.removeClass("mainColor_border");
				imgs.setStyle("border-color", "#ffffff");
				img.addClass("mainColor_border");
				img.setStyle("border-color", "#4A90E2");

				dlg.editor.setValue(this.options.layoutTemplates[img.get("name")]);

			}.bind(this));
		}.bind(this));
	},

	_createElcontainerNode: function(callback, value){
		var node = new Element("div", {
			"html": value
		});
		this.node = node.getFirst();
		this.node.dispose();
		node.destroy();
		this.node.set({
			"MWFType": this.moduleName,
			"id": this.json.id,
			"styles": this.css.moduleNode,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		});

		this._loadVue(function(){
			this._mountVueApp(callback);
		}.bind(this));
	},
	_loadVue: function(callback){
		if (!window.Vue){
			o2.load(["vue", "elementui"], { "sequence": true }, callback);
		}else{
			if (callback) callback();
		}
	},
	_mountVueApp: function(callback){
		if (!this.vueApp) this.vueApp = this._createVueExtend(callback);
		try{
			this.vm = new Vue(this.vueApp);
			this.vm.$o2module = this;
			this.vm.$o2callback = callback;

			this.vm.$mount(this.node);
		}catch(e){
			this.node.store("module", this);
			this._loadVueCss();
			if (callback) callback();
		}
	},
	_createVueExtend: function(callback){
		var _self = this;
		return {
			data: this._createVueData(),
			mounted: function(){
				_self._afterMounted(this.$el);
				if (callback) callback();
			}
		};
	},
	_createVueData: function(callback){
		return {};
	},
	_afterMounted: function(el){
		this.node = el;
		this.node.store("module", this);
	},

	_dragComplete: function(){
		if (!this.node){
			this._createNode(function(){
				this._dragMoveComplete();
			}.bind(this));
		}else{
			this._dragMoveComplete();
		}
	},

	_getElements: function(){
		this.elements = [];
	},
	_getContainerDoms: function(dom){
		var node = dom.getFirst();
		while (node){
			var tag = node.tagName.toString().toLowerCase();
			if (tag!=="section" || !node.get("mwftype")){
				switch (tag){
					case "aside":
						this.asideNodes.push(node);
						break;
					case "header":
						this.headerNodes.push(node);
						break;
					case "main":
						this.mainNodes.push(node);
						break;
					case "footer":
						this.footerNodes.push(node);
						break;
					default:
						this._getContainerDoms(node);
				}
			}
			node = node.getNext();
		}
	},
	_getContainers: function(){
		if (!this.containers || !this.containers.length)this.containers = [];

		this.asideNodes = [];
		this.headerNodes = [];
		this.mainNodes = [];
		this.footerNodes = [];
		this._getContainerDoms(this.node);

		// var asides = this.node.getElements("aside");
		// var headers = this.node.getElements("header");
		// var mains = this.node.getElements("main");
		// var footers = this.node.getElements("footer");

		this.form.getTemplateData("Elcontainer$Container", function(data){
			this.asideNodes.each(function(aside){
				this._createContainer(aside, data, "Elcontainer$Aside");
			}.bind(this));

			this.headerNodes.each(function(header){
				this._createContainer(header, data, "Elcontainer$Header");
			}.bind(this));

			this.mainNodes.each(function(main){
				this._createContainer(main, data, "Elcontainer$Main");
			}.bind(this));

			this.footerNodes.each(function(footer){
				this._createContainer(footer, data, "Elcontainer$Footer");
			}.bind(this));
		}.bind(this));
	},
	_createContainer: function(node, data, className){
		data.type = className;
		var json = this.form.getDomjson(node);
		var container = null;
		if (!json){
			var moduleData = Object.clone(data);
			container = new MWF["FC"+className](this.form);
			container.container = this;
			container.load(moduleData, node, this);
			container.node.set({
				"MWFType": container.moduleName,
				"id": container.json.id
			});
		}else{
			var moduleData = Object.clone(data);
			Object.merge(moduleData, json);
			Object.merge(json, moduleData);
			container = new MWF["FC"+className](this.form);
			container.container = this;
			container.load(json, node, this);
			container.node.set({
				"MWFType": container.moduleName,
				"id": container.json.id
			});
		}
		this.containers.push(container);
	}
});
