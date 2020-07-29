MWF.xApplication.process.FormDesigner.widget = MWF.xApplication.process.FormDesigner.widget || {};
MWF.require("MWF.widget.UUID", null, false);
MWF.require("MWF.widget.O2Identity", null, false);
MWF.xApplication.process.FormDesigner.widget.ScriptIncluder = new Class({
	Implements: [Options, Events],
	Extends: MWF.widget.Common,
	options: {
		"style": "default",
		"maxObj": document.body
	},
	initialize: function(node, designer, options){
		this.setOptions(options);
		this.node = $(node);
        this.designer = designer;
		
		this.path = "../x_component_process_FormDesigner/widget/$ScriptIncluder/";
		this.cssPath = "../x_component_process_FormDesigner/widget/$ScriptIncluder/"+this.options.style+"/css.wcss";
		this._loadCss();
		this.lp = this.designer.lp.scriptIncluder;
		
		this.items = [];
	},
    load: function(data){
		this.editorNode = new Element("div", {"styles": this.css.editorNode}).inject(this.node);
        this.actionNode = new Element("div", {"styles": this.css.actionNode}).inject(this.node);
        this.listNode = new Element("div", {"styles": this.css.listNode}).inject(this.node);
        this.loadEditorNode();
        this.loadActionNode();
        this.loadListNode(data);
	},
    loadEditorNode: function(){
	    debugger;
        var html = "<table width='100%' border='0' cellpadding='5' cellspacing='0' class='editTable'>" +
            "<tr><td style='width: 60px; '>"+this.lp.asyncLoad+"</td><td align='left'>"+
            "<input type='radio' name='"+(this.designer.appId||"")+"asyncLoadScript' value='true' checked/>"+this.lp.yes+
            "<input type='radio' name='"+(this.designer.appId||"")+"asyncLoadScript' value='false'/>"+this.lp.no+
            "</td></tr><tr><td>"+this.lp.selectScript+"</td><td><div class='scriptSelectorArea'></div></td></tr></table>";
        this.editorNode.set("html", html);
        var tds = this.editorNode.getElements("td").setStyles(this.css.editTableTdValue);
        this.asyncLoadScript = this.editorNode.getElements("[type='radio']");
        this.scriptSelectorArea = this.editorNode.getElement(".scriptSelectorArea");
        this.loadScriptSelector();
    },
    loadScriptSelector: function( data ){
        MWF.xDesktop.requireApp("process.ProcessDesigner", "widget.PersonSelector", function(){
            var _self = this;
            if( !data )data = [];
            this.scriptSelector = new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(this.scriptSelectorArea, this.designer, {
                "type": "Script",
                "count": 0,
                "names": data,
                "onChange": function(ids){
                    var value = [];
                    ids.each( function (id) {
                        var d = id.data;
                        value.push({
                            "type" : "script",
                            "name": d.name,
                            "alias": d.alias,
                            "id": d.id,
                            "appName" : d.appName || d.applicationName,
                            "appId": d.appId || d.application,
                            "appType" : d.appType
                        });
                    })
                    this.currentSelectScripts = value;
                }.bind(this)
            });
        }.bind(this));
    },
    loadActionNode: function(){
        this.actionAreaNode = new Element("div", {"styles": this.css.actionAreaNode}).inject(this.actionNode);
        this.addAction = new Element("div", {"styles": this.css.addAction, "text": this.designer.lp.validation.add}).inject(this.actionAreaNode);
        this.modifyAction = new Element("div", {"styles": this.css.modifyAction_disabled, "text": this.designer.lp.validation.modify}).inject(this.actionAreaNode);

        this.addAction.addEvent("click", function(){
            this.add();
        }.bind(this));
        this.modifyAction.addEvent("click", function(){
            this.modify();
        }.bind(this));
    },
    getCurrentData: function(){
	    var async = true;
        this.asyncLoadScript.each( function (el) {
            if( el.checked ){
                async = el.get("value") === "true";
            }
        });

        return {
            "async": async,
            "scriptList": this.currentSelectScripts || []
        };
    },
    add: function(){
        this.hideErrorNode();
        var data = this.getCurrentData();

        if ( data.scriptList.length === 0 ){
            this.showErrorNode(this.lp.selectScriptNotice);
            return false;
        }
        for( var i=0; i<this.items.length; i++ ){
            var scriptList = this.items[i].data.scriptList;
            for( var j=0; j<scriptList.length; j++ ){
                for( var k=0; k< data.scriptList.length; k++ )
                if( scriptList[j].id === data.scriptList[i].id ){
                    this.showErrorNode(this.lp.repeatAddScriptNotice);
                    return false;
                }
            }
        }
        var item = new MWF.xApplication.process.FormDesigner.widget.ScriptIncluder.Item(data, this);
        this.items.push(item);
        item.selected();
        this.empty();
        this.fireEvent("change");
    },
    empty: function(){
        this.asyncLoadScript.each( function (el) {
            if( el.get("value") === "true" ){
                el.set("checked", true)
            }
        });
        if(this.scriptSelector)this.scriptSelector.setData( [] );
        this.currentSelectScripts = [];
    },
    showErrorNode: function(text){
        this.errorNode = new Element("div", {"styles": this.css.errorNode}).inject(this.actionNode, "before");
        this.errorTextNode = new Element("div", {"styles": this.css.errorTextNode}).inject(this.errorNode);
        this.errorTextNode.set("text", text);
        this.errorNode.addEvent("click", function(){this.hideErrorNode();}.bind(this));
    },
    hideErrorNode: function(){
        if (this.errorNode) this.errorNode.destroy();
    },
    modify: function(){
        if (this.currentItem){
            this.hideErrorNode();

            var data = this.getCurrentData();

            if ( data.scriptList.length === 0 ){
                this.showErrorNode(this.lp.selectScriptNotice);
                return false;
            }
            for( var i=0; i<this.items.length; i++ ){
                if( this.currentItem !== this.items[i] ){
                    var scriptList = this.items[i].data.scriptList;
                    for( var j=0; j< scriptList.length; j++ ){
                        for( var k=0; k< data.scriptList.length; k++ )
                            if( scriptList[j].id === data.scriptList[i].id ){
                                this.showErrorNode(this.lp.repeatAddScriptNotice);
                                return false;
                            }
                    }
                }
            }

            this.currentItem.reload(data);
            this.currentItem.unSelected();
            this.disabledModify();
            this.empty();
            this.fireEvent("change");
        }
    },
    loadListNode: function(data){
        if (data){
            if (data.length){
                data.each(function(itemData){
                    var item = new MWF.xApplication.process.FormDesigner.widget.ScriptIncluder.Item(itemData, this);
                    this.items.push(item);
                }.bind(this));
            }
        }
    },
    enabledModify: function(){
        this.modifyAction.setStyles(this.css.modifyAction);
    },
    disabledModify: function(){
        this.modifyAction.setStyles(this.css.modifyAction_disabled);
    },
    setData: function(data){
        this.asyncLoadScript.each( function (el) {
            if( el.get("value") === "true" && data.async ){
                el.set("checked", true)
            }else if( el.get("value") === "false" && !data.async ){
                el.set("checked", true)
            }
        });
        if( !this.scriptSelector ){
            this.loadScriptSelector( data.scriptList );
        }else{
            this.scriptSelector.setData( data.scriptList );
        }
        this.currentSelectScripts = data.scriptList;
    },

    deleteItem: function(item){
        if (this.currentItem == item) item.unSelected();
        this.items.erase(item);
        item.node.destroy();
        MWF.release(item);
        this.fireEvent("change");
    },
    getData: function(){
        var data = [];
        this.items.each(function(item){
            data.push(item.data);
        });
        return data;
    }

});
MWF.xApplication.process.FormDesigner.widget.ScriptIncluder.Item = new Class({
    initialize: function(data, editor){
        this.data = data;
        this.editor = editor;
        this.container = this.editor.listNode;
        this.css = this.editor.css;
        this.lp = this.editor.designer.lp;
        this.load();
    },
    load: function(){
        debugger;
        this.node = new Element("div", {"styles": this.css.itemNode}).inject(this.container);
        this.deleteNode = new Element("div", {"styles": this.css.itemDeleteNode}).inject(this.node);
        this.contentNode = new Element("div", {"styles": this.css.itemContentNode}).inject(this.node);

        this.asyncNode = new Element("div", {"styles": {}}).inject(this.contentNode);
        this.asyncNode.set({
            "text": this.data.async ? this.lp.scriptIncluder.asyncLoadScript : this.lp.scriptIncluder.syncLoadScript
        });
        this.scriptNode = new Element("div", {
            styles : this.css.scriptNode
        }).inject(this.contentNode);
        this.data.scriptList.each( function (scipt) {
            new MWF.widget.O2Script(scipt, this.scriptNode)
        }.bind(this));

        this.contentNode.addEvent("click", function(){
            this.selected();
        }.bind(this));

        this.deleteNode.addEvent("click", function(e){
            this.deleteItem(e);
        }.bind(this));
    },
    reload: function(data){
        this.data = data;
        this.asyncNode.set({
            "text": this.data.async ? this.lp.scriptIncluder.asyncLoadScript : this.lp.scriptIncluder.syncLoadScript
        });
        this.scriptNode.empty();
        this.data.scriptList.each( function (scipt) {
            new MWF.widget.O2Script(scipt, this.scriptNode)
        }.bind(this));
    },
    selected: function(){
        if (this.editor.currentItem) this.editor.currentItem.unSelected();
        this.node.setStyles(this.css.itemNode_current);
        this.editor.currentItem = this;
        this.editor.setData(this.data);
        this.editor.enabledModify();
    },
    unSelected: function(){
        this.node.setStyles(this.css.itemNode);
        this.editor.currentItem = this;
        //this.editor.modifyValidation();
        this.editor.disabledModify();
    },
    deleteItem: function(e){
        var _self = this;
        this.editor.designer.confirm("warn", e, this.lp.scriptIncluder.delete_title, this.lp.scriptIncluder.delete_text, 300, 120, function(){
            _self.destroy();
            this.close();
        }, function(){
            this.close();
        });
    },
    destroy: function(){
        this.editor.deleteItem(this);
    }

});