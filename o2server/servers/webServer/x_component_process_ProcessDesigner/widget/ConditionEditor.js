MWF.xApplication.process.ProcessDesigner.widget = MWF.xApplication.process.ProcessDesigner.widget || {};
MWF.xApplication.process.ProcessDesigner.widget.ConditionEditor = new Class({
	Implements: [Options, Events],
	Extends: MWF.widget.Common,
	options: {
		"style": "default",
		"count": 0,
		"height": 500,
		"width": 500,
		"top": -1,
		"left": -1
	},
	initialize: function(node, options){
		this.setOptions(options);
		this.node = $(node);
		
		this.path = "/x_component_process_ProcessDesigner/widget/$ConditionEditor/";
		this.cssPath = "/x_component_process_ProcessDesigner/widget/$ConditionEditor/"+this.options.style+"/css.wcss";
		this._loadCss();

        this.loadConditionArea();
		this.loadConditionRouter();
		
		this.scripts = {};
	},
    loadConditionArea: function(){
        this.table = new Element("table",{
            "border": "0",
            "cellspacing": "0",
            "cellpadding": "0",
            "styles": this.css.conditionTable
        }).inject(this.node);
    },



	setScriptItem: function(data){
		if (typeOf(data).toLowerCase()=="array"){
			data.each(function(d){
				this.scripts[d.id] = new MWF.process.widget.ScriptEditor.Script(this, d);
			}.bind(this));
		}else{
			this.scripts[data.id] = new MWF.process.widget.ScriptEditor.Script(this, data);
		}
	},
	createActionAreaNode: function(){
		this.actionAreaNode = new Element("div", {
			"styles": this.css.actionAreaNode
		}).inject(this.node);
	},
	createContentAreaNode: function(){
		this.contentAreaNode = new Element("div", {
			"styles": this.css.contentAreaNode
		}).inject(this.node);
	},
	
	loadAction: function(){
		this.addScriptActionNode = new Element("div", {
			"styles": this.css.addScriptActionNode
		}).inject(this.actionAreaNode);
		this.addScriptActionNode.addEvent("click", function(){
			this.addNewScript();
		}.bind(this));
	},
	addNewScript: function(){
		if (!this.scriptEditNode){
			this.createNewScriptNode();
			this.openEditPanl();
		} 
	},
	
	editScript: function(script){

		if (!this.scriptEditNode){
			this.createNewScriptNode(script.data);
			this.openEditPanl(script.data);
		}
	},
	deleteScript: function(script, e){
		layout.confirm("warn", e, MWF.LP.process.notice.deleteScriptTitle, MWF.LP.process.notice.deleteScript, 300, 120, function(){
			var data = script.data;
			if (this.scripts[data.id]){
				this.fireEvent("queryDelete", [script]);
				this.scripts[data.id] = null;
				delete this.scripts[data.id];
				script.destroy();
				this.fireEvent("postDelete");
				this.close();
			}
		}.bind(this), function(){
			this.close();
		}, null);
	},
	
	openEditPanl: function(data){
		MWF.require("MWF.widget.Panel", function(){
			if ((this.options.top==-1) || (this.options.left==-1)){
				var position = MWF.getCenter({"x": this.options.width, "y": this.options.height});
				this.options.top = position.y;
				this.options.left = position.x;
			}
			
			this.scriptPanel = new MWF.widget.Panel(this.scriptEditNode, {
				"title": "script",
				"isClose": true,
				"height": this.options.height,
				"width": this.options.width,
				"left": this.options.left,
				"top": this.options.top,
				"onResize": function(){
					this.setPanelSize(this.panelModulePercent);
					this.options.width = this.scriptPanel.options.width;
					this.options.height = this.scriptPanel.options.height;
				}.bind(this),
				"onCompleteMove": function(){
					this.options.left = this.scriptPanel.options.left;
					this.options.top = this.scriptPanel.options.top;
				}.bind(this),
				"onPostClose": function(){
					this.scriptPanel = null;
					this.scriptEditNode = null;
				}.bind(this)
			});
			this.scriptPanel.load();
			this.setPanelSize();
			var codeContent = data ? data.html : "";
			this.scriptEditor.load(codeContent);

		}.bind(this));
	},
	
	setPanelSize: function(){
		var size = this.scriptPanel.content.getSize();
		var paddingTopSize = this.scriptPanel.content.getStyle("padding-top").toFloat();
		var paddingBottomSize = this.scriptPanel.content.getStyle("padding-bottom").toFloat();
		var borderTopSize = this.scriptPanel.content.getStyle("border-top").toFloat();
		var borderBottomSize = this.scriptPanel.content.getStyle("border-bottom").toFloat();
		
		var baseNode = this.scriptEditNode.getFirst();
		var codeNode = this.scriptEditNode.getLast();
		
		var baseSize = baseNode.getSize();
		var baseMarginTop = baseNode.getStyle("margin-top").toFloat();
		var baseMarginBottom = baseNode.getStyle("margin-bottom").toFloat();
		var baseBorderTop = baseNode.getStyle("border-top").toFloat();
		var baseBorderBottom = baseNode.getStyle("border-bottom").toFloat();
		
		var codeMarginTop = codeNode.getStyle("margin-top").toFloat();
		var codeMarginBottom = codeNode.getStyle("margin-bottom").toFloat();
		var codePaddingTop = codeNode.getStyle("padding-top").toFloat();
		var codePaddingBottom = codeNode.getStyle("padding-bottom").toFloat();
		var codeBorderTop = codeNode.getStyle("border-top-width").toFloat();
		var codeBorderBottom = codeNode.getStyle("border-bottom-width").toFloat();
		
		var codeNodeHeight = size.y - 2 - paddingTopSize - paddingBottomSize - baseSize.y - baseMarginTop - baseMarginBottom - codeMarginTop - codeMarginBottom - codePaddingTop - codePaddingBottom;
		codeNodeHeight = codeNodeHeight - borderTopSize - borderBottomSize - baseBorderTop - baseBorderBottom - codeBorderTop - codeBorderBottom;
		
		codeNode.setStyle("height", codeNodeHeight);
		codeNode.getPrevious().setStyle("height", codeNodeHeight);
	},
	createNewScriptNode: function(data){
		this.scriptEditNode = new Element("div", {
			"styles": this.css.scriptEditNode
		});
		this.setCreateNewScriptBaseNode(data);
		this.setCreateNewScriptCodeHelpNode();
		this.setCreateNewScriptCodeNode();
	},
	setCreateNewScriptBaseNode: function(data){
		var baseNode = new Element("div", {
			"styles": this.css.newScriptBaseNode
		}).inject(this.scriptEditNode);

		var name = (data) ? data.name: "";
		var description = (data) ? data.description: "";
		if (data) this.scriptEditNode.store("scriptId", data.id);
		
		var html = "<table width=\"100%\" border=\"0\" cellpadding=\"5\" cellspacing=\"0\">";
		html += "<tr><td style=\"width: 30px; font-size:12px;\">"+MWF.LP.name+"</td><td><input id=\"inputScriptName\" value=\""+name+"\" type=\"text\" style=\"width:98%; border: 1px solid #DDD\"/></td></tr>";
		html += "<tr><td style=\"width: 30px; font-size:12px;\">"+MWF.LP.description+"</td><td><input id=\"inputScriptDescription\" value=\""+description+"\" type=\"text\" style=\"width:98%; border: 1px solid #DDD\"/></td></tr>";
		html += "</table>";
		baseNode.set("html", html);
	},
	setCreateNewScriptCodeNode: function(){
		var codeNode = new Element("div", {
			"styles": this.css.newScriptCodeNode
		}).inject(this.scriptEditNode);
		
		MWF.require("MWF.widget.ScriptEditor", null, false);
		this.scriptEditor = new MWF.widget.ScriptEditor(codeNode, {"style": "process"});
	},
	setCreateNewScriptCodeHelpNode: function(){
		var codeToolbarNode = new Element("div", {
			"styles": this.css.newScriptCodeToolbarNode
		}).inject(this.scriptEditNode);
		
		var saveNode = new Element("div", {
			"styles": this.css.saveScriptNode,
			"events": {
				"click": this.saveScript.bind(this)
			}
		}).inject(codeToolbarNode);
		var helpNode = new Element("div", {
			"styles": this.css.helpScriptNode
		}).inject(codeToolbarNode);
		var insertNode = new Element("div", {
			"styles": this.css.insertScriptNode
		}).inject(codeToolbarNode);
		var checkNode = new Element("div", {
			"styles": this.css.checkScriptNode
		}).inject(codeToolbarNode);
	},
	saveScript: function(e){
		if (this.scriptEditNode){
			var name = $("inputScriptName").get("value");
			if (!name){
				
				//输入验证。。。。。。。。。。。
				
				layout.notice("notice", {x: "right", y:"top"}, MWF.LP.process.notice.inputScriptName, this.scriptPanel.content);
				$("inputScriptName").focus();
				return false;
			}
			
			this.fireEvent("querySave");
			
			var scriptId = this.scriptEditNode.retrieve("scriptId");
			if (!scriptId){
				MWF.require("MWF.widget.UUID",function(){
					scriptId = (new MWF.widget.UUID()).toString();
				}, false);
			} 
			
			var data = {
				"id": scriptId,
				"name": name,
				"description": $("inputScriptDescription").get("value"),
				"code": this.scriptEditor.toCode(),  //要计算出格式的text
				"html": this.scriptEditor.toHTML(),
			};
			if (!this.scripts[scriptId]){
				this.scripts[scriptId] = new MWF.process.widget.ScriptEditor.Script(this, data);
			}else{
				this.scripts[scriptId].setData(data);
			}
			
			this.fireEvent("postSave", [this.scripts[scriptId]]);
		}
		this.scriptPanel.closePanel();
	}
});

MWF.process.widget.ScriptEditor.Script = new Class({
	initialize: function(editor, data){
		this.editor = editor;
		this.data = data;
		this.createArea();
	},
	setData: function(data){
		this.data.name = data.name;
		this.data.description = data.description;
		this.data.code = data.code;
		this.data.html = data.html;
		this.summaryNode.set("text", this.data.name);
	},
	createArea: function(){
		this.node = new Element("div", {
			"styles": this.editor.css.scriptItemNode
		}).inject(this.editor.contentAreaNode);
		
		this.actionNode = new Element("div", {
			"styles": this.editor.css.scriptItemActionNode
		}).inject(this.node);
		this.summaryNode = new Element("div", {
			"styles": this.editor.css.scriptItemSummaryNode,
			"text": this.data.name
		}).inject(this.node);
		
		this.editNode = new Element("div", {
			"styles": this.editor.css.scriptItemEditActionNode,
			"events": {
				"click": this.editScript.bind(this)
			}
		}).inject(this.actionNode);
		
		this.deleteNode = new Element("div", {
			"styles": this.editor.css.scriptItemDeleteActionNode,
			"events": {
				"click": this.deleteScript.bind(this)
			}
		}).inject(this.actionNode); 
		
	},
	editScript: function(){
		this.editor.editScript(this);
	},
	deleteScript: function(e){
		this.editor.deleteScript(this, e);
	},
	destroy: function(){
		this.node.destroy();
		this.data = null;
	}
});

