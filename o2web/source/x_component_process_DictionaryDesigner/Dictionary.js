MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.DictionaryDesigner = MWF.xApplication.process.DictionaryDesigner || {};
MWF.APPDD = MWF.xApplication.process.DictionaryDesigner;
MWF.require("MWF.widget.Common", null, false);
MWF.xDesktop.requireApp("process.DictionaryDesigner", "lp."+MWF.language, null, false);
MWF.require("MWF.widget.JavascriptEditor", null, false);
MWF.xApplication.process.DictionaryDesigner.Dictionary = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isView": false,
        "showTab": true,
        "types": ["object", "array", "string", "number", "boolean"]
    },

    initialize: function(designer, data, options){
        this.setOptions(options);

        this.path = "../x_component_process_DictionaryDesigner/$Dictionary/";
        this.cssPath = "../x_component_process_DictionaryDesigner/$Dictionary/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.designer = designer;
        this.data = data;
        if (!this.data.data) this.data.data = {};
        this.node = this.designer.designNode;
        this.tab = this.designer.tab;

        this.areaNode = new Element("div.areaNode");

        //MWF.require("MWF.widget.ScrollBar", function(){
        //    new MWF.widget.ScrollBar(this.areaNode, {"distance": 100});
        //}.bind(this));


        this.propertyListNode = this.designer.propertyDomArea;
        //this.propertyNode = this.designer.propertyContentArea;

        if(this.designer.application) this.data.applicationName = this.designer.application.name;
        if(this.designer.application) this.data.application = this.designer.application.id;

        this.isNewDictionary = (this.data.id) ? false : true;

        this.items = [];

        this.autoSave();
        this.designer.addEvent("queryClose", function(){
            if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
        }.bind(this));
    },
    loadTab: function(callback){
        var _self = this;
        MWF.require("MWF.widget.Tab", null, false);
        this.designTabNode = new Element("div").inject(this.areaNode);
        this.designTab = new MWF.widget.Tab(this.designTabNode, {"style": "design"});
        this.designTab.load();
        this.designTabPageAreaNode = Element("div");

        this.designNode = new Element("div", {"styles": {"overflow": "auto","background-color":"#fff"}}).inject(this.designTabPageAreaNode);

        this.designTabScriptAreaNode = Element("div", {"styles": { "height": "100%" }});
        this.scriptNode = new Element("div.scriptNode", {"styles": {"background-color":"#fff"}}).inject(this.designTabScriptAreaNode);

        this.designPage = this.designTab.addTab(this.designTabPageAreaNode, this.designer.lp.design);
        this.scriptPage = this.designTab.addTab(this.designTabScriptAreaNode, "JSON");
        this.designPage.showTabIm = function(callback){
            debugger;
            if( _self.scriptEditor && _self.isChanged){
                if( _self.getEditorValidData() !== false ){
                    if (!this.isShow){
                        this.tab.pages.each(function(page){
                            if (page.isShow) page.hideIm();
                        });
                        this.showIm(callback);
                    }
                }
            }else{
                if (!this.isShow){
                    this.tab.pages.each(function(page){
                        if (page.isShow) page.hideIm();
                    });
                    this.showIm(callback);
                }
            }
        }

        // this.setScriptPageEvent();
        this.designPage.showTabIm();
        this.scriptPage.addEvent("postShow", function(){
            if (this.scriptEditor){
                var value = JSON.stringify(this.data.data, null, "\t");
                if (value) this.scriptEditor.setValue(value);
                this.scriptEditor.focus();
            }else{
                this.loadScriptEditor();
            }
            this.fireEvent("resize");
        }.bind(this));
        this.designPage.addEvent("postShow", function(){
            if( this.scriptEditor && this.isChanged){
                var data = this.getEditorValidData();
                if( data !== false ){
                    this.data.data = data;
                    this.reload();
                    this.isChanged = false;
                }
            }
            this.fireEvent("resize");
        }.bind(this));
    },
    getEditorValidData : function( silence ){
        if( !this.scriptEditor.validated() ){
            if(!silence)this.designer.notice( this.designer.lp.notice.editorNotValidated, "error", this.node, {"x": "left", "y": "bottom"});
            return false;
        }
        try{
            var value = this.scriptEditor.getValue();
            var v = JSON.parse(value);
            if( !this.checkValid(v, silence) ){
                return false;
            }
            return v;
        }catch (e) {
            if(!silence)this.designer.notice( this.designer.lp.notice.jsonParseError, "error", this.node, {"x": "left", "y": "bottom"});
            return false;
        }
    },
    checkValid: function( obj, silence ){
        if( typeOf(obj) !== "object" ){
            return true;
        }
        for (var key in obj) {
            if( !key || key.trim() === "" ){
                if(!silence)this.designer.notice(this.designer.lp.notice.emptyObjectKey, "error", this.node, {"x": "left", "y": "bottom"});
                return false;
            }
            if (!isNaN(parseFloat(key))){
                if(!silence)this.designer.notice(this.designer.lp.notice.numberObjectKey, "error", this.node, {"x": "left", "y": "bottom"});
                return false;
            }

            if( typeOf(obj[key]) === "object" ){
                if( !this.checkValid( obj[key] ) )return false;
            }
        }
        return true;
    },
    loadScriptEditor:function(){
        var value = JSON.stringify(this.data.data, null, "\t");
        this.scriptEditor = new MWF.widget.JavascriptEditor(this.scriptNode, {"option": {"value": value, "mode" : "json" }});
        this.scriptEditor.load(function(){

            if (value) this.scriptEditor.setValue(value);

            this.scriptEditor.addEditorEvent("change", function(e){
                if (!this.isChanged){
                    this.isChanged = true;
                }
            }.bind(this));
        }.bind(this));
    },
    autoSave: function(){
        this.autoSaveTimerID = window.setInterval(function(){
            if (!this.autoSaveCheckNode) this.autoSaveCheckNode = this.designer.contentToolbarNode.getElement("#MWFDictionaryAutoSaveCheck");
            if (this.autoSaveCheckNode){
                if (this.autoSaveCheckNode.get("checked")){
                    this.save();
                }
            }
        }.bind(this), 60000);
    },
    createTitle: function(){
        this.itemsNode = new Element("div", {"styles": this.css.itemsNode}).inject(this.designNode);
        this.typesNode = new Element("div", {"styles": this.css.typesNode}).inject(this.designNode);
        this.valuesNode = new Element("div", {"styles": this.css.valuesNode}).inject(this.designNode);

        this.itemTitleNode = new Element("div", {"styles": this.css.itemTitleNode}).inject(this.itemsNode);
        this.typeTitleNode = new Element("div", {"styles": this.css.typeTitleNode}).inject(this.typesNode);
        this.valueTitleNode = new Element("div", {"styles": this.css.valueTitleNode}).inject(this.valuesNode);

        this.itemResizeNode = new Element("div", {"styles": this.css.itemResizeNode}).inject(this.itemTitleNode);
        this.typeResizeNode = new Element("div", {"styles": this.css.typeResizeNode}).inject(this.typeTitleNode);

//        this.addTopItemNode = new Element("div", {"styles": this.css.addTopItemNode}).inject(this.itemTitleNode);

        this.itemTitleTextNode = new Element("div", {"styles": this.css.itemTitleTextNode, "text": this.designer.lp.item}).inject(this.itemTitleNode);
        this.typeTitleTextNode = new Element("div", {"styles": this.css.typeTitleTextNode, "text": this.designer.lp.type}).inject(this.typeTitleNode);
        this.valueTitleTextNode = new Element("div", {"styles": this.css.valueTitleTextNode, "text": this.designer.lp.value}).inject(this.valueTitleNode);

//        this.addTopItemNode.addEvent("click", this.addTopItem.bind(this));
    },

    load : function(){
        this.loadTab();
        this.setAreaNodeSize();
        this.designer.addEvent("resize", this.setAreaNodeSize.bind(this));

        this.page = this.tab.addTab(this.areaNode, this.data.name || this.designer.lp.newDictionary, (!this.data.isNewDictionary && this.data.id!=this.designer.options.id));
        this.page.dictionary = this;

        this.page.addEvent("show", function(){
            this.designer.dictionaryListAreaNode.getChildren().each(function(node){
                var dictionary = node.retrieve("dictionary");
                if (dictionary.id==this.data.id){
                    if (this.designer.currentListDictionaryItem){
                        this.designer.currentListDictionaryItem.setStyles(this.designer.css.listDictionaryItem);
                    }
                    node.setStyles(this.designer.css.listDictionaryItem_current);
                    this.designer.currentListDictionaryItem = node;
                    this.lisNode = node;
                }
            }.bind(this));

            this.setPropertyContent();

        }.bind(this));
        this.page.addEvent("queryClose", function(){
            if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
            this.saveSilence();
            if (this.lisNode) this.lisNode.setStyles(this.designer.css.listScriptItem);
        }.bind(this));
        this.page.tabNode.addEvent("dblclick", this.designer.maxOrReturnEditor.bind(this.designer));

        this.createTitle();

        this.createRootItem();

        if (this.options.showTab) this.page.showTabIm();
    },
    setPropertyContent: function(){
        this.designer.propertyIdNode.set("text", this.data.id);
        this.designer.propertyNameNode.set("value", this.data.name);
        this.designer.propertyAliasNode.set("value", this.data.alias);
        this.designer.propertyDescriptionNode.set("value", this.data.description);

        this.designer.jsonDomNode.empty();
        MWF.require("MWF.widget.JsonParse", function(){
            this.jsonParse = new MWF.widget.JsonParse(this.data.data, this.designer.jsonDomNode, this.designer.jsonTextAreaNode);
            window.setTimeout(function(){
                this.jsonParse.load();
            }.bind(this), 1);
        }.bind(this));
    },
    setAreaNodeSize: function(){
        var size = this.node.getSize();
        var tabSize = this.tab.tabNodeContainer.getSize();
        var searchY = 0;
        if (this.searchNode) searchY = this.searchNode.getSize().y;
        var y = size.y - tabSize.y - searchY;
        this.areaNode.setStyle("height", ""+y+"px");
        this.designNode.setStyle("height", ""+(y-18)+"px");
        this.scriptNode.setStyle("height", ""+(y-18)+"px");
        if (this.scriptEditor) if (this.scriptEditor.editor) this.scriptEditor.editor.resize();
    },

    reload : function(){
        this.items = [];
        this.designNode.empty();
        this.createTitle();
        this.createRootItem();
    },
    createRootItem: function() {
        this.items.push(new MWF.xApplication.process.DictionaryDesigner.Dictionary.item("ROOT", this.data.data, null, 0, this, true));
    },

    saveSilence: function(){
        if (!this.isSave){

            if( this.scriptPage.isShow ){
                if( this.scriptEditor ){

                    var data = this.getEditorValidData( true );
                    if( data !== false ){
                        this.data.data = data;
                    }else{
                        return false;
                    }
                }
            }

            var name = this.designer.propertyNameNode.get("value");
            var alias = this.designer.propertyAliasNode.get("value");
            var description = this.designer.propertyDescriptionNode.get("value");
            if (!name){
                this.designer.notice(this.designer.lp.notice.inputName, "error");
                return false;
            }
            this.data.name = name;
            this.data.alias = alias;
            this.data.description = description;

            this.isSave = true;

            this.designer.actions.saveDictionary(this.data, function(json){
                this.isSave = false;
                this.data.id = json.data.id;
                if (callback) callback();
            }.bind(this), function(xhr, text, error){
                this.isSave = false;
                //
                //var errorText = error+":"+text;
                //if (xhr) errorText = xhr.responseText;
                //MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
            }.bind(this));
        }
    },
    save: function(callback){
        if (!this.isSave){
            if (this.designer.tab.showPage==this.page){

                if( this.scriptPage.isShow ){
                    if( this.scriptEditor ){
                        var data = this.getEditorValidData();
                        if( data !== false ){
                            this.data.data = data;
                        }else{
                            return false;
                        }
                    }
                }

                var name = this.designer.propertyNameNode.get("value");
                var alias = this.designer.propertyAliasNode.get("value");
                var description = this.designer.propertyDescriptionNode.get("value");

                if (!name || !alias){
                    this.designer.notice(this.designer.lp.notice.inputName, "error");
                    return false;
                }
                this.data.name = name;
                this.data.alias = alias;
                this.data.description = description;
            }

            this.isSave = true;
            this.designer.actions.saveDictionary(this.data, function(json){
                this.isSave = false;
                this.designer.notice(this.designer.lp.notice.save_success, "success", this.node, {"x": "left", "y": "bottom"});
                this.data.isNewDictionary = false;
                this.isNewDictionary = false;

                this.data.id = json.data.id;
                this.page.textNode.set("text", this.data.name);
                if (this.lisNode) {
                    this.lisNode.getLast().set("text", this.data.name+"("+this.data.alias+")");
                }
                if (callback) callback();
            }.bind(this), function(xhr, text, error){
                this.isSave = false;

                var errorText = error+":"+text;
                if (xhr) errorText = xhr.responseText;
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
            }.bind(this));
        }else{
            MWF.xDesktop.notice("info", {x: "right", y:"top"}, this.designer.lp.isSave);
        }

    },
    loadSearch: function(){
        if (!this.searchNode){
            this.createSearchNode();
        }else{
            if (this.searchNode.getStyle("display")=="none"){
                this.searchNode.setStyle("display", "block");
            }else{
                this.searchNode.setStyle("display", "none");
            }
        }
        this.setAreaNodeSize();
    },
    createSearchNode: function(){
        this.searchNode = new Element("div", {"styles": this.css.searchNode}).inject(this.designNode, "before");
        this.searchInputNode = new Element("div", {"styles": this.css.searchInputNode}).inject(this.searchNode);
        this.searchInput = new Element("input", {"styles": this.css.searchInput}).inject(this.searchInputNode);

        this.searchAction = new Element("div", {"styles": this.css.searchAction, "text": this.designer.lp.search}).inject(this.searchNode);
        var lineNode = new Element("div", {"styles": this.css.searchLineNode,}).inject(this.searchNode);
        this.nextAction = new Element("div", {"styles": this.css.searchNextAction, "text": this.designer.lp.next}).inject(this.searchNode);


        this.searchAction.addEvent("click", function(){
            this.searchDictionary();
        }.bind(this));

        this.nextAction.addEvent("click", function(){
            this.searchDictionaryNext();
        }.bind(this));
    },
    searchDictionary: function(){
        var key = this.searchInput.get("value");
        if (key){
            this.currentSearchItem = null;
            if (!this.getSearchItem(key)){

            }
        }
    },
    searchDictionaryNext: function(){
        var key = this.searchInput.get("value");
        if (key){
            if (!this.getSearchItem(key, null, this.currentSearchItem)){
                if (!this.getSearchItem(key)){

                }
            }
        }
    },
    getSearchItem: function(key, rootItem, item){
        var fromItem = rootItem || this.items[0];
        var flag = true;
        if (item){
            if (item.type=="object"){
                if (!item.exp) item.expOrColChildren();
                if (this.getSearchItem(key, item)) return true;
            }
            fromItem = item.parent;
            flag = false;
        }
        if (fromItem.type=="object") {
            if (!fromItem.exp) fromItem.expOrColChildren();
            for (var i=0; i<fromItem.children.length; i++){
                var child = fromItem.children[i];
                if (flag){
                    if (child.key.indexOf(key)!=-1){
                        child.selected();
                        this.currentSearchItem = child;
                        new Fx.Scroll(this.designNode).toElement(child.itemNode);
                        return true;
                    }else{
                        if (child.type=="object"){
                            if (!child.exp) child.expOrColChildren();
                            if (child.children.length){
                                if (this.getSearchItem(key, child)) return true;
                            }
                        }
                    }
                }
                if (item) if (child==item) flag = true;
            }
        }else{
            if (fromItem.key.indexOf(key)!=-1){
                fromItem.selected();
                this.currentSearchItem = fromItem;
                return true;
            }
        }
        while ((fromItem) && fromItem.key!="ROOT"){
            if (fromItem.nextSibling){
                fromItem = fromItem.nextSibling;
            }else{
                fromItem = fromItem.parent;
                if (fromItem) fromItem = fromItem.nextSibling;
            }
            if (fromItem){
                if (this.getSearchItem(key, fromItem)) return true;
            }
        }

        return false;
    },
    saveAs: function(){},
    explode: function(){},
    implode: function(){}

});


MWF.xApplication.process.DictionaryDesigner.Dictionary.item = new Class({
    initialize: function(key, value, parent, level, dictionary, exp, nextSibling){
        this.key = key;
        this.value = value;
        this.parent = parent;
        this.level = level;
        this.dictionary = dictionary;
        this.exp = exp || false;
        this.nextSibling = nextSibling;

        this.children = [];
        this.childrenItemCreated = false;
        this.css = this.dictionary.css;
        this.type = typeOf(this.value);
        //       if (this.parent) this.parent.children.push(this);

        this.load();
    },
    load: function(){
        this.createNodes();
        this.setNodeText();
        this.setEvent();
        if (this.exp) this.createChildrenItems();
    },
    createNodes: function() {
        this.itemNode = new Element("div", {"styles": this.css.itemNode});
        this.typeNode = new Element("div", {"styles": this.css.typeNode});
        this.valueNode = new Element("div", {"styles": this.css.valueNode});

        //var left = this.itemNode.getStyle("padding-left").toFloat();
        var left = 10;
        left = left + (this.level*20);
        this.itemNode.setStyle("padding-left", ""+left+"px");

        this.itemActionsAreaNode = new Element("div", {"styles": this.css.itemActionsAreaNode}).inject(this.itemNode);

        if (this.type=="array" || this.type=="object" || (this.parent && this.parent.type=="array")){
            this.itemAddActionNode = new Element("div", {"styles": this.css.itemAddActionNode}).inject(this.itemActionsAreaNode);
        }
        if (this.parent) this.itemDelActionNode = new Element("div", {"styles": this.css.itemDelActionNode}).inject(this.itemActionsAreaNode);

        if (this.type=="array" || this.type=="object"){
            this.itemExpColActionNode = new Element("div", {"styles": this.css.itemExpColActionNode}).inject(this.itemNode);
            if (this.exp){
                this.itemExpColActionNode.setStyle("background-image", "url("+"../x_component_process_DictionaryDesigner/$Dictionary/default/icon/col.png)");
            }else{
                this.itemExpColActionNode.setStyle("background-image", "url("+"../x_component_process_DictionaryDesigner/$Dictionary/default/icon/exp.png)");
            }
        }

        this.typeActionsAreaNode = new Element("div", {"styles": this.css.typeActionsAreaNode}).inject(this.typeNode);
        this.typeSelActionNode = new Element("div", {"styles": this.css.typeSelActionNode}).inject(this.typeActionsAreaNode);

        this.valueActionsAreaNode = new Element("div", {"styles": this.css.valueActionsAreaNode}).inject(this.valueNode);
        if (this.type=="boolean") this.valueSelActionNode = new Element("div", {"styles": this.css.valueSelActionNode}).inject(this.valueActionsAreaNode);

        this.itemTextNode = new Element("div", {"styles": this.css.itemTextNode}).inject(this.itemNode);
        this.typeTextNode = new Element("div", {"styles": this.css.typeTextNode}).inject(this.typeNode);
        this.valueTextNode = new Element("div", {"styles": this.css.valueTextNode}).inject(this.valueNode);

        if (this.nextSibling){
            this.itemNode.inject(this.nextSibling.itemNode, "before");
            this.typeNode.inject(this.nextSibling.typeNode, "before");
            this.valueNode.inject(this.nextSibling.valueNode, "before");
        }else{
            if (this.parent){
                if (this.parent.children.length){
                    var injectItem = this.parent.children.getLast();
                    this.itemNode.inject(injectItem.itemNode, "after");
                    this.typeNode.inject(injectItem.typeNode, "after");
                    this.valueNode.inject(injectItem.valueNode, "after");
                }else{
                    this.itemNode.inject(this.parent.itemNode, "after");
                    this.typeNode.inject(this.parent.typeNode, "after");
                    this.valueNode.inject(this.parent.valueNode, "after");
                }
            }else{
                this.itemNode.inject(this.dictionary.itemsNode);
                this.typeNode.inject(this.dictionary.typesNode);
                this.valueNode.inject(this.dictionary.valuesNode);
            }
        }

    },
    resetNodes: function(){
        this.itemTextNode.removeEvents("mousedown");
        this.valueTextNode.removeEvents("mousedown");
        if (this.type=="array" || this.type=="object"){
            if (!this.itemExpColActionNode){
                this.itemExpColActionNode = new Element("div", {"styles": this.css.itemExpColActionNode}).inject(this.itemTextNode, "before");
                if (this.exp){
                    this.itemExpColActionNode.setStyle("background-image", "url("+"../x_component_process_DictionaryDesigner/$Dictionary/default/icon/col.png)");
                }else{
                    this.itemExpColActionNode.setStyle("background-image", "url("+"../x_component_process_DictionaryDesigner/$Dictionary/default/icon/exp.png)");
                }
                this.itemExpColActionNode.addEvents({
                    "click": function(){this.expOrColChildren();}.bind(this)
                });
            }
        }else{
            if (this.itemExpColActionNode){
                this.itemExpColActionNode.destroy();
                this.itemExpColActionNode = null;
            }
            if (this.type!="boolean") this.valueTextNode.addEvent("mousedown", function(e){this.editValue();}.bind(this));
        }
        if (this.type=="array" || this.type=="object" || (this.parent && this.parent.type=="array")){
            if (!this.itemAddActionNode){
                this.itemAddActionNode = new Element("div", {"styles": this.css.itemAddActionNode}).inject(this.itemActionsAreaNode);
                this.itemAddActionNode.addEvent("click", function(e){this.addItem(e);}.bind(this));
            }
        }else{
            if (this.itemAddActionNode) this.itemAddActionNode.destroy();
            this.itemAddActionNode = null;
        }

        if (this.type=="boolean"){
            if (!this.valueSelActionNode) this.valueSelActionNode = new Element("div", {"styles": this.css.valueSelActionNode}).inject(this.valueActionsAreaNode);
            this.valueSelActionNode.addEvent("click", function(){this.selectBooleanValue();}.bind(this));
            this.valueTextNode.addEvent("click", function(){this.selectBooleanValue();}.bind(this));
        }

        if (this.parent){
            if (this.parent.type!="array"){
                this.itemTextNode.addEvent("mousedown", function(e){this.editKey();}.bind(this));
            }
        }

    },
    setNodeText: function(){
        var text = this.key;
        if (this.parent) if (this.parent.type=="array") text = "["+text+"]";
        this.itemTextNode.set("text", text);
        this.typeTextNode.set("text", this.type);

        switch(this.type){
            case "array":
                this.valueTextNode.setStyles(this.css.valueTextNode);
                this.valueTextNode.set("text", ""+this.value.length+" Items");
                break;
            case "object":
                var i=0;
                Object.each(this.value, function(){i++;});
                this.valueTextNode.setStyles(this.css.valueTextNode);
                this.valueTextNode.set("text", ""+i+" Items");
                break;
            default:
                this.valueTextNode.setStyles(this.css.valueTextNode_edit);
                this.valueTextNode.set("text", this.value);
                break;
        }
    },

    setEvent: function(){
        this.itemNode.addEvent("click", function(e){this.selected();}.bind(this));
        this.typeNode.addEvent("click", function(e){this.selected();}.bind(this));
        this.valueNode.addEvent("click", function(e){this.selected();}.bind(this));
        this.typeSelActionNode.addEvent("click", function(e){this.selectType();}.bind(this));
        this.typeTextNode.addEvent("click", function(e){this.selectType();}.bind(this));

        this.itemNode.addEvents({
            "mouseover": function(){this.itemActionsAreaNode.fade("in");}.bind(this),
            "mouseout": function(){this.itemActionsAreaNode.fade("out");}.bind(this)
        });

        if (this.itemAddActionNode) this.itemAddActionNode.addEvent("click", function(e){this.addItem(e);}.bind(this));
        if (this.itemDelActionNode) this.itemDelActionNode.addEvent("click", function(e){this.delItem(e);}.bind(this));

        if (this.type=="array" || this.type=="object"){
            this.itemExpColActionNode.addEvents({
                "click": function(){this.expOrColChildren();}.bind(this)
            });
        }else{
            if (this.type!="boolean") this.valueTextNode.addEvent("mousedown", function(e){this.editValue();}.bind(this));
        }
        if (this.parent){
            if (this.parent.type!="array"){
                this.itemTextNode.addEvent("mousedown", function(e){this.editKey();}.bind(this));
            }
        }

        if (this.type=="boolean"){
            this.valueSelActionNode.addEvent("click", function(){this.selectBooleanValue();}.bind(this));
            this.valueTextNode.addEvent("click", function(){this.selectBooleanValue();}.bind(this));
        }
    },
    expOrColChildren: function(){
        if (this.exp){
            this.colChildren();
            this.itemExpColActionNode.setStyle("background-image", "url("+"../x_component_process_DictionaryDesigner/$Dictionary/default/icon/exp.png)");
            this.exp = false;
        }else{
            this.expChildren();
            this.itemExpColActionNode.setStyle("background-image", "url("+"../x_component_process_DictionaryDesigner/$Dictionary/default/icon/col.png)");
            this.exp = true;
        }
    },
    colChildren: function(){
        this.children.each(function(item){
            item.colChildren();
            item.colChildrenNode();
        });
    },
    expChildren: function(){
        this.createChildrenItems();
        this.children.each(function(item){
            if (item.exp) item.expChildren();
            item.expChildrenNode();
        });
    },
    colChildrenNode: function(){
        this.itemNode.setStyle("display", "none");
        this.typeNode.setStyle("display", "none");
        this.valueNode.setStyle("display", "none");
    },
    expChildrenNode: function(){
        this.itemNode.setStyle("display", "block");
        this.typeNode.setStyle("display", "block");
        this.valueNode.setStyle("display", "block");
    },


    unSelected: function(){
        this.itemNode.setStyles(this.css.itemNode);
        this.typeNode.setStyles(this.css.typeNode);
        this.valueNode.setStyles(this.css.valueNode);
        this.dictionary.currentSelectedItem = null;
    },
    selected: function(){
        if (this.dictionary.currentSelectedItem!=this){
            if (this.dictionary.currentSelectedItem) this.dictionary.currentSelectedItem.unSelected();
            this.itemNode.setStyles(this.css.itemNode_selected);
            this.typeNode.setStyles(this.css.typeNode_selected);
            this.valueNode.setStyles(this.css.valueNode_selected);
            this.listDataItems();

            this.dictionary.currentSelectedItem = this;
        }
    },
    listDataItems: function(){
        this.dictionary.propertyListNode.empty();
        switch(this.type){
            case "array":
                this.value.each(function(v, idx){
                    this.createDataListItem("["+idx+"]", v.toString());
                }.bind(this));
                break;
            case "object":
                Object.each(this.value, function(v, key){
                    this.createDataListItem(key, v.toString());
                }.bind(this));
                break;
        }
    },
    createDataListItem: function(key, v){
        var node = new Element("div", {"styles": this.css.dataListItemNode}).inject(this.dictionary.propertyListNode);
        var keyNode = new Element("div", {"styles": this.css.dataListItemKeyNode, "text": key, "title": key}).inject(node);
        var vNode = new Element("div", {"styles": this.css.dataListItemValueNode, "text": v, "title": v}).inject(node);
    },

    createNewItem: function(key, value, parent, level, dictionary, exp, nextSibling){
        return new MWF.xApplication.process.DictionaryDesigner.Dictionary.item(key, value, parent, level, dictionary, exp, nextSibling);
    },
    createChildrenItems: function(){
        if (!this.childrenItemCreated){
            switch(this.type){
                case "array":
                    this.value.each(function(v, idx){
                        var item = this.createNewItem(idx, v, this, this.level+1, this.dictionary, false);
                        if (this.children.length) this.children[this.children.length-1].nextSibling = item;
                        this.children.push(item);
                    }.bind(this));
                    break;
                case "object":
                    Object.each(this.value, function(v, key){
                        var item = this.createNewItem(key, v, this, this.level+1, this.dictionary, false);
                        if (this.children.length) this.children[this.children.length-1].nextSibling = item;
                        this.children.push(item);
                    }.bind(this));
                    break;
                default:
                    //nothing
                    break;
            }
            this.childrenItemCreated = true;
        }
    },

    addItem: function(e){
        if (!this.parent){
            this.createChildrenItems();
            this.addChild();
        }else{
            if (this.exp){
                this.addChild();
            }else{
                this.addSibling();
            }
        }
        this.dictionary.jsonParse.loadObjectTree();
    },
    addChild: function(){
        var item;
        if (this.type=="array"){
            var idx = this.value.length;
            var arrayValue = "New Element Value";
            this.value.push(arrayValue);
            item = this.createNewItem(idx, arrayValue, this, this.level+1, this.dictionary, false);
        }
        if (this.type=="object") {
            var key = "NewItem";
            var i = 0;

            while (this.value[key] !== undefined) {
                i++;
                key = "NewItem" + i;
            }
            var objValue1 = "New Item Value";

            this.value[key] = objValue1;
            item = this.createNewItem(key, objValue1, this, this.level + 1, this.dictionary, false)
        }
        if (this.children.length) this.children[this.children.length-1].nextSibling = item;
        this.children.push(item);
    },
    addSibling: function(){
        var item = null;
        var idx;
        if (this.parent.type=="array"){
            idx = this.key;
            var value = "New Element Value";
            this.parent.value.splice(this.key, 0, value);
            for (var i=this.key; i<this.parent.children.length; i++){
                var item = this.parent.children[i];
                item.key = item.key+1;
                item.setNodeText();
            }
            item = this.createNewItem(idx, value, this.parent, this.level, this.dictionary, false, this);
        }else{
            var key = "NewItem";
            var i = 0;
            while (this.parent.value[key] != undefined) {
                i++;
                key = "NewItem" + i;
            }
            var value = "New Item Value";
            this.parent.value[key] = value;
            var item = this.createNewItem(key, value, this.parent, this.level, this.dictionary, false, this);
            idx = this.parent.children.indexOf(this);
        }
        if (idx) this.parent.children[idx-1].nextSibling = item;
        this.parent.children.splice(idx, 0, item);
    },

    delItem: function(e){
        var _self = this;
        this.dictionary.designer.shortcut = false;
        this.dictionary.designer.confirm("warn", e, this.dictionary.designer.lp.notice.deleteDataTitle, this.dictionary.designer.lp.notice.deleteData, 300, 120, function(){
            _self.destroy();
            _self.dictionary.jsonParse.loadObjectTree();
            _self.dictionary.designer.shortcut = true;
            this.close();
        }, function(){
            _self.dictionary.designer.shortcut = true;
            this.close();
        });
    },
    destroy: function(){
        var idx = this.parent.children.indexOf(this);
        if (idx) this.parent.children[idx-1].nextSibling = this.nextSibling;

        this.destroyAllNodes();
        this.parent.children.erase(this);
        if (this.parent.type=="object"){
            delete this.parent.value[this.key];
            delete this;
        }
        if (this.parent.type=="array"){
            this.parent.value.splice(this.key, 1);
            for (var i=this.key; i<this.parent.children.length; i++){
                this.parent.children[i].key = this.parent.children[i].key-1;
                this.parent.children[i].setNodeText();
            }
        }
        this.dictionary.jsonParse.loadObjectTree();
    },
    destroyAllNodes: function(){
        this.children.each(function(item){
            item.destroyAllNodes();
        });
        this.itemNode.destroy();
        this.typeNode.destroy();
        this.valueNode.destroy();
        if (this.typeSelectNode) this.typeSelectNode.destroy();
    },

    selectType: function(){
        if (!this.typeSelectNode) this.createTypeSelectNode();
        this.typeSelectNode.setStyle("display", "block");
        var size = this.dictionary.node.getSize();
        var selSize = this.typeSelectNode.getSize();
        var itemNodes = this.typeSelectNode.getChildren();
        for (var i=0; i<itemNodes.length; i++){
            if (itemNodes[i].get("text")==this.type){
                itemNodes[i].setStyles(this.css.typeSelectItemNode_over);
            }else{
                itemNodes[i].setStyles(this.css.typeSelectItemNode);
            }
        };

        this.typeSelectNode.position({
            relativeTo: this.typeNode,
            position: 'upperLeft',
            edge: 'upperLeft'
        });

        var p = this.typeSelectNode.getPosition(this.typeSelectNode.getOffsetParent());
        if ((p.y+selSize.y)>size.y){
            this.typeSelectNode.position({
                relativeTo: this.typeNode,
                position: 'bottomLeft',
                edge: 'bottomLeft'
            });
        };

        this.closeTypeSelectNodeFun = this.closeTypeSelectNode.bind(this);
        $(document.body).addEvent("mousedown", this.closeTypeSelectNodeFun);
    },
    closeTypeSelectNode: function(){
        this.typeSelectNode.setStyle("display", "none");
        $(document.body).removeEvent("mousedown", this.closeTypeSelectNodeFun);
    },

    createTypeSelectNode: function(){
        var _self = this;
        this.typeSelectNode = new Element("div", {"styles": this.css.typeSelectNode});
        var types = this.dictionary.options.types;
        if (!this.parent){
            types = ["object", "array"];
            this.typeSelectNode.setStyle("height", "50px");
        }

        types.each(function(type){
            var itemNode = new Element("div", {"styles": this.css.typeSelectItemNode}).inject(this.typeSelectNode);
            itemNode.set("text", type);
            if (this.type==type) itemNode.setStyles(this.css.typeSelectItemNode_over);
            itemNode.addEvents({
                "mouseover": function(){this.setStyles(_self.css.typeSelectItemNode_over);},
                "mouseout": function(){this.setStyles(_self.css.typeSelectItemNode);},
                "mousedown": function(e){_self.selectedType(this, e);}
            })
        }.bind(this));
        this.typeSelectNode.inject(this.dictionary.node);
    },

    selectedType: function(itemNode, e){
        e.target = null;
        var type = itemNode.get("text");
        if (this.type!=type){
            var _self = this;
            switch(type){
                case "array":
                    if (this.value!="New Item Value" && this.value!="New Element Value"){
                        this.dictionary.designer.confirm("warn", e, this.dictionary.designer.lp.notice.changeTypeTitle, this.dictionary.designer.lp.notice.changeType, 300, 120, function(){
                            if (_self.type=="object"){
                                _self.changeTypeObjectToArray(type);
                            }else{
                                _self.changeTypePrimitiveToArray(type);
                            }
                            this.close();
                            _self.dictionary.jsonParse.loadObjectTree();
                        }, function(){
                            this.close();
                        });
                    }else{
                        if (this.type=="object"){
                            this.changeTypeObjectToArray(type);
                        }else{
                            this.changeTypePrimitiveToArray(type);
                        }
                        this.dictionary.jsonParse.loadObjectTree();
                    }

                    break;
                case "object":
                    if (this.value!="New Item Value" && this.value!="New Element Value") {
                        this.dictionary.designer.confirm("warn", e, this.dictionary.designer.lp.notice.changeTypeTitle, this.dictionary.designer.lp.notice.changeType, 300, 120, function () {
                            if (_self.type == "array") {
                                _self.changeTypeArrayToObject(type);
                            } else {
                                _self.changeTypePrimitiveToObject(type);
                            }
                            this.close();
                            _self.dictionary.jsonParse.loadObjectTree();
                        }, function () {
                            this.close();
                        });
                    }else{
                        if (this.type == "array") {
                            this.changeTypeArrayToObject(type);
                        } else {
                            this.changeTypePrimitiveToObject(type);
                        }
                        this.dictionary.jsonParse.loadObjectTree();
                    }

                    break;
                default:
                    if (this.value!="New Item Value" && this.value!="New Element Value") {
                        this.dictionary.designer.confirm("warn", e, this.dictionary.designer.lp.notice.changeTypeTitle, this.dictionary.designer.lp.notice.changeTypeDeleteChildren, 300, 120, function () {
                            if (_self.type == "array") {
                                _self.changeTypeArrayToPrimitive(type);
                            } else if (_self.type == "object") {
                                _self.changeTypeObjectToPrimitive(type);
                            } else {
                                _self.changeTypePrimitiveToPrimitive(type);
                            }
                            this.close();
                            _self.dictionary.jsonParse.loadObjectTree();
                        }, function () {
                            this.close();
                        });
                    }else{
                        if (this.type == "array") {
                            this.changeTypeArrayToPrimitive(type);
                        } else if (_self.type == "object") {
                            this.changeTypeObjectToPrimitive(type);
                        } else {
                            this.changeTypePrimitiveToPrimitive(type);
                        }
                        this.dictionary.jsonParse.loadObjectTree();
                    }
                    break;
            }
        }
    },

    deleteAllChildren: function(){
        this.children.each(function(item){
            item.destroyAllNodes();
        });
    },
    changeTypeObjectToPrimitive: function(type){
        this.deleteAllChildren();
        this.children = [];
        this.childrenItemCreated = false;

        var value;
        switch(type){
            case "string":
                value = "";
                break;
            case "number":
                value = 0;
                break;
            case "boolean":
                value = true;
                break;
        }
        delete this.parent.value[this.key];
        this.parent.value[this.key] = value;
        this.value = value;
        this.type = type;
        this.exp = false;
        this.setNodeText();
        this.resetNodes();
    },
    changeTypeArrayToPrimitive: function(type){
        this.changeTypeObjectToPrimitive(type);
    },
    changeTypePrimitiveToPrimitive: function(type){
        switch(type){
            case "string":
                value = this.value.toString();
                break;
            case "number":
                value = this.value.toFloat();
                if (isNaN(value)) value = 0
                break;
            case "boolean":
                value = true;
                if (this.value=="false") value = false;
                break;
        }
        delete this.parent.value[this.key];
        this.parent.value[this.key] = value;

        this.value = value;
        this.type = type;
        this.exp = false;
        this.setNodeText();
        this.resetNodes();
    },

    changeTypePrimitiveToObject: function(type){
        value = {};
        delete this.parent.value[this.key];
        this.parent.value[this.key] = value;
        this.value = value;
        this.type = type;
        this.exp = false;
        this.setNodeText();
        this.resetNodes();
    },
    changeTypeArrayToObject: function(type){
        this.deleteAllChildren();
        this.children = [];
        this.childrenItemCreated = false;

        var value = {};
        this.value.each(function(v, idx){
            value["ITEM"+idx] = v;
        });

        if (this.parent){
            delete this.parent.value[this.key];
            this.parent.value[this.key] = value;
            this.value = value;
            this.type = type;
            this.setNodeText();
            this.resetNodes();
            if (this.exp) this.createChildrenItems();
        }else{
            this.dictionary.data.data = value;
            this.dictionary.jsonParse.json = value;
            this.value = value;
            this.type = type;
            this.setNodeText();
            this.resetNodes();
            if (this.exp) this.createChildrenItems();
        }

    },
    changeTypePrimitiveToArray: function(type) {
        value = [];
        delete this.parent.value[this.key];
        this.parent.value[this.key] = value;
        this.value = value;
        this.type = type;
        this.exp = false;
        this.setNodeText();
        this.resetNodes();
    },
    changeTypeObjectToArray: function(type){
        this.deleteAllChildren();
        this.children = [];
        this.childrenItemCreated = false;

        var value = [];
        Object.each(this.value, function(v, idx){
            value.push(v);
        });

        if (this.parent){
            delete this.parent.value[this.key];
            this.parent.value[this.key] = value;
            this.value = value;
            this.type = type;
            this.childrenItemCreated = false;
            this.setNodeText();
            this.resetNodes();
            if (this.exp) this.createChildrenItems();
        }else{
            this.dictionary.data.data = value;
            this.dictionary.jsonParse.json = value;
            this.value = value;
            this.type = type;
            this.childrenItemCreated = false;
            this.setNodeText();
            this.resetNodes();
            if (this.exp) this.createChildrenItems();
        }

    },

    editValue: function(){
        //this.inEdit
        this.valueTextNode.empty();
        //    this.valueTextNode.removeEvents("mousedown");
        this.editValueNode = new Element("input", {"styles": this.css.itemEditValueNode}).inject(this.valueTextNode);
        this.editValueNode.set("value", this.value);

        window.setTimeout(function(){
            this.editValueNode.focus();
            this.editValueNode.select();

            this.editValueNode.addEvents({
                "blur": function(e){
                    this.editValueConfirm(e);
                }.bind(this),
                "keydown": function(e){
                    if (e.code==13){
                        this.editValueConfirm(e);
                        if (this.nextSibling){
                            this.nextSibling.editKey();
                        }
                    }
                    e.stopPropagation();
                }.bind(this),
                "mousedown": function(e){e.stopPropagation();}
            });
        }.bind(this), 10)


    },
    editValueConfirm: function(e){
        var value = this.editValueNode.get("Value");
        if (this.type=="number"){
            if (isNaN(parseFloat(value))){
                this.dictionary.designer.notice(this.dictionary.designer.lp.notice.inputTypeError, "error", this.editValueNode, {"x": "left", "y": "bottom"}, {"x": 0, "y": 24});
                this.editValueNode.setStyles(this.css.itemEditValueNode_error);
                this.editValueNode.select();
                e.preventDefault();
                return false;
            }
            value = value.toFloat();
        }
        this.value = value;
        this.parent.value[this.key] = this.value;

        this.editValueNode.destroy();
        this.editValueNode = null;

        this.setNodeText();
        this.dictionary.jsonParse.loadObjectTree();
        //    if (this.type!="boolean") this.valueTextNode.addEvent("mousedown", function(e){this.editValue();}.bind(this));
    },

    editKey: function(){
        this.itemTextNode.empty();
        //    this.itemTextNode.removeEvents("mousedown");
        this.editKeyNode = new Element("input", {"styles": this.css.itemEditValueNode, "type": "text"}).inject(this.itemTextNode);
        this.editKeyNode.set("value", this.key);
        window.setTimeout(function(){
            this.editKeyNode.focus();
            this.editKeyNode.select();
            //this.editKeyNode.setSelectionRange(1,this.key.length-1);

            this.editKeyNode.addEvents({
                "blur": function(e){this.editKeyConfirm(e);}.bind(this),
                "keydown": function(e){
                    if (e.code==13){
                        this.editKeyConfirm(e);
                        if (this.type!="array" && this.type!="object" && this.type!="boolean") {
                            this.editValue();
                        }else{
                            if (this.nextSibling){
                                this.nextSibling.editKey();
                            }
                        }
                    }
                    e.stopPropagation();
                }.bind(this),
                "mousedown": function(e){e.stopPropagation();}
            });
        }.bind(this), 10);
    },
    editKeyConfirm: function(e){
        var key = this.editKeyNode.get("Value");
        if (key!=this.key){
            if (this.parent.value[key]){
                this.dictionary.designer.notice(this.dictionary.designer.lp.notice.sameKey, "error", this.editKeyNode, {"x": "left", "y": "bottom"}, {"x": 0, "y": 24});
                this.editKeyNode.setStyles(this.css.itemEditValueNode_error);
                this.editKeyNode.select();
                e.preventDefault();
                return false;
            }
            if (!isNaN(parseFloat(key))){
                this.dictionary.designer.notice(this.dictionary.designer.lp.notice.numberKey, "error", this.editKeyNode, {"x": "left", "y": "bottom"}, {"x": 0, "y": 24});
                this.editKeyNode.setStyles(this.css.itemEditValueNode_error);
                this.editKeyNode.select();
                e.preventDefault();
                return false;
            }
            if (!key){
                this.dictionary.designer.notice(this.dictionary.designer.lp.notice.emptyKey, "error", this.editKeyNode, {"x": "left", "y": "bottom"}, {"x": 0, "y": 24});
                this.editKeyNode.setStyles(this.css.itemEditValueNode_error);
                this.editKeyNode.select();
                e.preventDefault();
                return false;
            }

            delete this.parent.value[this.key];
            this.parent.value[key] = this.value;
            this.key = key;
        }

        this.editKeyNode.destroy();
        this.editKeyNode = null;

        this.setNodeText();
        this.dictionary.jsonParse.loadObjectTree();
        //if (this.parent){
        //    if (this.parent.type!="array"){
        //        this.itemTextNode.addEvent("mousedown", function(e){this.editKey();}.bind(this));
        //    }
        //}
    },

    selectBooleanValue: function(){
        if (!this.booleanSelectNode) this.createBooleanSelectNode();
        var size = this.dictionary.node.getSize();
        this.booleanSelectNode.setStyle("display", "block");
        var selSize = this.booleanSelectNode.getSize();
        var itemNodes = this.booleanSelectNode.getChildren();
        for (var i=0; i<itemNodes.length; i++){
            if (itemNodes[i].get("text")==this.value.toString()){
                itemNodes[i].setStyles(this.css.typeSelectItemNode_over);
            }else{
                itemNodes[i].setStyles(this.css.typeSelectItemNode);
            }
        };

        this.booleanSelectNode.position({
            relativeTo: this.valueNode,
            position: 'upperLeft',
            edge: 'upperLeft'
        });

        var p = this.booleanSelectNode.getPosition(this.booleanSelectNode.getOffsetParent());
        if ((p.y+selSize.y)>size.y){
            this.booleanSelectNode.position({
                relativeTo: this.valueNode,
                position: 'bottomLeft',
                edge: 'bottomLeft'
            });
        };

        this.closeBooleanSelectNodeFun = this.closeBooleanSelectNode.bind(this);
        $(document.body).addEvent("mousedown", this.closeBooleanSelectNodeFun);
    },
    closeBooleanSelectNode: function(){
        this.booleanSelectNode.setStyle("display", "none");
        $(document.body).removeEvent("mousedown", this.closeBooleanSelectNodeFun);
    },

    createBooleanSelectNode: function(){
        var _self = this;
        this.booleanSelectNode = new Element("div", {"styles": this.css.booleanSelectNode});

        ["true", "false"].each(function(type){
            var itemNode = new Element("div", {"styles": this.css.typeSelectItemNode}).inject(this.booleanSelectNode);
            itemNode.set("text", type);
            if (this.value.toString()==type) itemNode.setStyles(this.css.typeSelectItemNode_over);
            itemNode.addEvents({
                "mouseover": function(){this.setStyles(_self.css.typeSelectItemNode_over);},
                "mouseout": function(){this.setStyles(_self.css.typeSelectItemNode);},
                "mousedown": function(e){_self.selectedBoolean(this, e);}
            })
        }.bind(this));
        this.booleanSelectNode.inject(this.dictionary.node);
    },
    selectedBoolean: function(item, e){
        var text = item.get("text");
        var value = (text=="false") ? false : true;
        this.value = value;
        this.parent.value[this.key] = value;

        this.setNodeText();
        this.dictionary.jsonParse.loadObjectTree();
    }
});
MWF.xApplication.process.DictionaryDesigner.DictionaryReader = new Class({
    Extends: MWF.xApplication.process.DictionaryDesigner.Dictionary,

    autoSave: function(){},
    createRootItem: function() {
        this.items.push(new MWF.xApplication.process.DictionaryDesigner.Dictionary.ItemReader("ROOT", this.data.data, null, 0, this, true));
    },
});
MWF.xApplication.process.DictionaryDesigner.Dictionary.ItemReader= new Class({
    Extends: MWF.xApplication.process.DictionaryDesigner.Dictionary.item,

    createNewItem: function(key, value, parent, level, dictionary, exp, nextSibling){
        return new MWF.xApplication.process.DictionaryDesigner.Dictionary.ItemReader(key, value, parent, level, dictionary, exp, nextSibling);
    },
    setEvent: function(){
        this.itemNode.addEvent("click", function(e){this.selected();}.bind(this));
        this.typeNode.addEvent("click", function(e){this.selected();}.bind(this));
        this.valueNode.addEvent("click", function(e){this.selected();}.bind(this));
        //this.typeSelActionNode.addEvent("click", function(e){this.selectType();}.bind(this));
        //this.typeTextNode.addEvent("click", function(e){this.selectType();}.bind(this));

        //this.itemNode.addEvents({
        //    "mouseover": function(){this.itemActionsAreaNode.fade("in");}.bind(this),
        //    "mouseout": function(){this.itemActionsAreaNode.fade("out");}.bind(this)
        //});

        //if (this.itemAddActionNode) this.itemAddActionNode.addEvent("click", function(e){this.addItem(e);}.bind(this));
        //if (this.itemDelActionNode) this.itemDelActionNode.addEvent("click", function(e){this.delItem(e);}.bind(this));

        if (this.type=="array" || this.type=="object"){
            this.itemExpColActionNode.addEvents({
                "click": function(){this.expOrColChildren();}.bind(this)
            });
        }else{
            //if (this.type!="boolean") this.valueTextNode.addEvent("mousedown", function(e){this.editValue();}.bind(this));
        }
        //if (this.parent){
        //    if (this.parent.type!="array"){
        //        this.itemTextNode.addEvent("mousedown", function(e){this.editKey();}.bind(this));
        //    }
        //}

        //if (this.type=="boolean"){
        //    this.valueSelActionNode.addEvent("click", function(){this.selectBooleanValue();}.bind(this));
        //    this.valueTextNode.addEvent("click", function(){this.selectBooleanValue();}.bind(this));
        //}
    },
});

//MWF.xApplication.process.ProcessDesigner.Process.Property = new Class({
//	Implements: [Options, Events],
//	Extends: MWF.APPPD.Property,
//	initialize: function(process, options){
//		this.setOptions(options);
//		this.process = process;
//		this.paper = this.process.paper;
//		this.data = process.process;
//		this.htmlPath = "../x_component_process_ProcessDesigner//$Process/process.html";
//	}
//});
