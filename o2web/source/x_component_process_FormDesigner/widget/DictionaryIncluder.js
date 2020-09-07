MWF.xApplication.process.FormDesigner.widget = MWF.xApplication.process.FormDesigner.widget || {};
MWF.require("MWF.widget.UUID", null, false);
MWF.require("MWF.widget.O2Identity", null, false);
MWF.xApplication.process.FormDesigner.widget.DictionaryIncluder = new Class({
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
		
		this.path = "../x_component_process_FormDesigner/widget/$DictionaryIncluder/";
		this.cssPath = "../x_component_process_FormDesigner/widget/$DictionaryIncluder/"+this.options.style+"/css.wcss";
		this._loadCss();
		this.lp = this.designer.lp.dictionaryIncluder;
		
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
            "<tr><td>"+this.lp.selectDictionary+"</td><td><div class='dictionarySelectorArea'></div></td></tr>" +
            "<tr><td>"+this.lp.path+"</td><td><input type='text' style='width:90%'/></td></tr>"+
            "</table>";
        this.editorNode.set("html", html);
        var tds = this.editorNode.getElements("td").setStyles(this.css.editTableTdValue);
        this.dictionarySelectorArea = this.editorNode.getElement(".dictionarySelectorArea");
        this.pathField = this.editorNode.getElement("input[type='text']");
        this.loadDictionarySelector();
    },
    loadDictionarySelector: function( data ){
        MWF.xDesktop.requireApp("process.ProcessDesigner", "widget.PersonSelector", function(){
            var _self = this;
            if( !data )data = [];
            this.dictionarySelector = new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(this.dictionarySelectorArea, this.designer, {
                "type": "Dictionary",
                "count": 1,
                "names": data,
                "onChange": function(ids){
                    var json;
                    if( ids.length ){
                        var d = ids[0].data;
                        json = {
                            "type" : "dictionary",
                            "name": d.name,
                            "alias": d.alias,
                            "id": d.id,
                            "appName" : d.appName || d.applicationName,
                            "appId": d.appId || d.application,
                            "appAilas": d.appAilas || d.applicationAilas,
                            "appType" : d.appType
                        };
                    }
                    this.currentSelectDictionary = json;
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
        return {
            "path": this.pathField.get("value"),
            "dictionary": this.currentSelectDictionary || null
        };
    },
    add: function(){
	    debugger;
        this.hideErrorNode();
        var data = this.getCurrentData();

        if ( !data.dictionary ){
            this.showErrorNode(this.lp.selectDictionaryNotice);
            return false;
        }
        for( var i=0; i<this.items.length; i++ ){
            var d = this.items[i].data;
            if( d.dictionary.id === data.dictionary.id ){
                if( d.path === data.path ){
                    this.showErrorNode(this.lp.repeatAddDictionaryNotice);
                    return false;
                }else if( !d.path || d.path === "root" ){
                    this.showErrorNode(this.lp.rootDictionaryExistNotice);
                    return false;
                }else if( !data.path || data.path === "root" ){
                    this.showErrorNode(this.lp.subDictionaryExistNotice);
                    return false;
                }else if( d.path.indexOf( data.path + "." ) === 0 ){
                    this.showErrorNode(this.lp.subDictionaryExistNotice);
                    return false;
                }else if( data.path.indexOf( d.path + "." ) === 0 ){
                    this.showErrorNode(this.lp.parentDictionaryExistNotice);
                    return false;
                }
            }
        }
        var item = new MWF.xApplication.process.FormDesigner.widget.DictionaryIncluder.Item(data, this);
        this.items.push(item);
        item.selected();
        this.empty();
        this.fireEvent("change");
    },
    empty: function(){
        this.pathField.set("value","");
        if(this.dictionarySelector)this.dictionarySelector.setData( [] );
        this.currentSelectDictionary = null;
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
	    debugger;
        if (this.currentItem){
            this.hideErrorNode();

            var data = this.getCurrentData();

            if ( !data.dictionary ){
                this.showErrorNode(this.lp.selectDictionaryNotice);
                return false;
            }
            for( var i=0; i<this.items.length; i++ ){
                if( this.items[i] === this.currentItem )continue;
                var d = this.items[i].data;
                if( d.dictionary.id === data.dictionary.id ){
                    if( d.path === data.path ){
                        this.showErrorNode(this.lp.repeatAddDictionaryNotice);
                        return false;
                    }else if( !d.path || d.path === "root" ){
                        this.showErrorNode(this.lp.rootDictionaryExistNotice);
                        return false;
                    }else if( !data.path || data.path === "root" ){
                        this.showErrorNode(this.lp.subDictionaryExistNotice);
                        return false;
                    }else if( d.path.indexOf( data.path + "." ) === 0 ){
                        this.showErrorNode(this.lp.subDictionaryExistNotice);
                        return false;
                    }else if( data.path.indexOf( d.path + "." ) === 0 ){
                        this.showErrorNode(this.lp.parentDictionaryExistNotice);
                        return false;
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
                    var item = new MWF.xApplication.process.FormDesigner.widget.DictionaryIncluder.Item(itemData, this);
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
        this.pathField.set( "value", data.path || "");
        if( !this.dictionarySelector ){
            this.loadDictionarySelector( data.dictionary ? [data.dictionary] : [] );
        }else{
            this.dictionarySelector.setData( data.dictionary ? [data.dictionary] : [] );
        }
        this.currentSelectDictionary = data.dictionary;
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
MWF.xApplication.process.FormDesigner.widget.DictionaryIncluder.Item = new Class({
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

        this.dictionaryNode = new Element("div", {
            styles : this.css.dictionaryNode
        }).inject(this.contentNode);
        new MWF.widget.O2Dictionary(this.data.dictionary, this.dictionaryNode);

        this.pathNode = new Element("div", {"styles": {"padding-left": "5px","padding-top": "5px"}}).inject(this.contentNode);
        this.pathNode.set({
            "text": this.lp.dictionaryIncluder.path + (this.data.path || "root")
        });


        this.contentNode.addEvent("click", function(){
            this.selected();
        }.bind(this));

        this.deleteNode.addEvent("click", function(e){
            this.deleteItem(e);
        }.bind(this));
    },
    reload: function(data){
        this.data = data;
        this.pathNode.set({
            "text": this.lp.dictionaryIncluder.path + (this.data.path || "root")
        });
        this.dictionaryNode.empty();
        new MWF.widget.O2Dictionary(data.dictionary, this.dictionaryNode)
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
        this.editor.currentItem = null;
        //this.editor.modifyValidation();
        this.editor.disabledModify();
    },
    deleteItem: function(e){
        var _self = this;
        this.editor.designer.confirm("warn", e, this.lp.dictionaryIncluder.delete_title, this.lp.dictionaryIncluder.delete_text, 300, 120, function(){
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