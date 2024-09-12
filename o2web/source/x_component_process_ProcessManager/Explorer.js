MWF.xApplication.process = MWF.xApplication.process || {};
MWF.APPPM = MWF.xApplication.process.ProcessManager = MWF.xApplication.process.ProcessManager || {};

MWF.xDesktop.requireApp("process.ProcessManager", "lp."+MWF.language, null, false);
MWF.xApplication.process.ProcessManager.Explorer = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "tooltip": {
            "create": MWF.APPPM.LP.process.create,
            "search": MWF.APPPM.LP.process.search,
            "searchText": MWF.APPPM.LP.process.searchText,
            "noElement": MWF.APPPM.LP.process.noProcessNoticeText
        },
        "topEnable": true,
        "categoryEnable": true,
        "itemStyle": "card",
        "sortKeys": ['name', 'alias', 'createTime', 'updateTime'],
        "sortKey": '',
        "name": 'process.processExplorer'
    },

    initialize: function(node, actions, options){
        this.setOptions(options);
        this.setTooltip();

        this.path = "../x_component_process_ProcessManager/$Explorer/";
        this.cssPath = "../x_component_process_ProcessManager/$Explorer/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.actions = actions;
        this.node = $(node);
        this.initData();
    },
    setTooltip: function(tooltip){
        if (tooltip) this.options.tooltip = Object.merge(this.options.tooltip, tooltip);
    },
    initData: function(){
        //this.categoryLoadFirst = true;
        //this.isLoaddingCategory = false;
        //this.categoryLoaded = false;
        //this.categorys = [];
        //this.dragItem = false;
        //this.dragCategory = false;
        //this.currentCategory = null;
        //this.loadCategoryQueue = 0;
        this.categoryList = [];
        this.deleteMarkItems = [];
        this.selectMarkItems = [];
    },
    reload: function(){
        if (this.app && this.app.content){
            if( this.itemList && this.itemList.length ){
                this.itemList.each(function (item){
                    if(item.destroy)item.destroy();
                });
            }
            this.node.empty();
            this.isSetContentSize = false;
            this.load();
        }
    },
    load: function(){
        this.loadToolbar();
        this.loadContentNode();

        this.setNodeScroll();

        this.getUd( function (){
            this.loadElementList();
        }.bind(this));
    },
    getUd: function ( callback ){
        var id = (this.app.options && this.app.options.application) ? this.app.options.application.id : "";
        MWF.UD.getDataJson(this.options.name + "_" + id, function (data){
            if( data ){
                this.options.itemStyle = data.itemStyle;
                this.options.sortKey = data.sortKey;
            }
            callback();
        }.bind(this));
    },
    setUd: function (){
        var data = {
            itemStyle: this.options.itemStyle,
            sortKey: this.options.sortKey,
        };
        var id = (this.app.options && this.app.options.application) ? this.app.options.application.id : "";
        MWF.UD.putData(this.options.name+ "_" + id, data);
    },

    loadToolbar: function(){
        this.toolbarNode = new Element("div", {"styles": this.css.toolbarNode});
        this.createCreateElementNode();
        this.createIconElementNode();

        this.createTitleElementNode();
        this.createSearchElementNode();

        this.toolbarNode.inject(this.node);
        this.createCategoryElementNode();
    },
    createIconElementNode: function(){
        this.iconElementNode = new Element("div", {
            "styles": this.css.iconElementNode
        }).inject(this.toolbarNode);

        if (this.app.options && this.app.options.application){
            if (this.app.options.application.icon){
                this.iconElementNode.setStyle("background-image", "url(data:image/png;base64,"+this.app.options.application.icon+")");
            }else{
                this.iconElementNode.setStyle("background-image", "url("+"../x_component_process_ApplicationExplorer/$Main/default/icon/application.png)");
            }
        }
    },
    createCreateElementNode: function(){
        this.createElementNode = new Element("div", {
            "styles": this.css.createElementNode,
            "title": this.options.tooltip.create
        }).inject(this.toolbarNode);
        this.createElementNode.addEvent("click", function(e){
            this._createElement(e);
        }.bind(this));
    },
    createTitleElementNode: function() {
        this.titleElementNode = new Element("div", {
            "styles": this.css.titleElementNode,
            "text": this.app.options.application.name
        }).inject(this.toolbarNode);
    },
    openFindDesigner: function(){
        this.app.options.application.moduleType = "processPlatform";
        var options = {
            "filter": {
                "moduleList": ["processPlatform"],
                "appList": [this.app.options.application]
            }
        };
        layout.openApplication(null, "FindDesigner", options);
    },
    createSearchElementNode: function(){
        debugger
        this.searchElementNode = new Element("div.searchElementNode", {
            "styles": this.css.searchElementNode,
            "title": this.app.lp.findDesigner
        }).inject(this.toolbarNode);

        this.searchElementNode.addEvent("click", function(){
            this.openFindDesigner();
        }.bind(this));

        //@todo
        return false;

        //this.searchElementButtonNode = new Element("div", {"styles": this.css.searchElementButtonNode,"title": this.options.tooltip.search}).inject(this.searchElementNode);
        //
        //this.searchElementInputAreaNode = new Element("div", {
        //    "styles": this.css.searchElementInputAreaNode
        //}).inject(this.searchElementNode);
        //
        //this.searchElementInputBoxNode = new Element("div", {
        //    "styles": this.css.searchElementInputBoxNode
        //}).inject(this.searchElementInputAreaNode);
        //
        //this.searchElementInputNode = new Element("input", {
        //    "type": "text",
        //    "value": this.options.tooltip.searchText,
        //    "styles": this.css.searchElementInputNode,
        //    "x-webkit-speech": "1"
        //}).inject(this.searchElementInputBoxNode);
        //var _self = this;
        //this.searchElementInputNode.addEvents({
        //    "focus": function(){
        //        if (this.value==_self.options.tooltip.searchText) this.set("value", "");
        //    },
        //    "blur": function(){if (!this.value) this.set("value", _self.options.tooltip.searchText);},
        //    "keydown": function(e){
        //        if (e.code==13){
        //            this.searchElement();
        //            e.preventDefault();
        //        }
        //    }.bind(this),
        //    "selectstart": function(e){
        //        e.preventDefault();
        //    }
        //});
        //this.searchElementButtonNode.addEvent("click", function(){this.searchElement();}.bind(this));
    },
    createCategoryElementNode: function(){
        this.categoryElementNode = new Element("div", {
            "styles": this.css.categoryElementNode
        }).inject(this.node);
    },
    searchElement: function(){
        //-----------------------------------------
        //-----------------------------------------
        //-----search category---------------------
        //-----------------------------------------
        //-----------------------------------------
        alert("search Element");
    },

    loadContentNode: function(){
        this.elementContentNode = new Element("div", {
            "styles": this.css.elementContentNode
        }).inject(this.node);

        this.elementContentNode.addEvent("click", function(){
            while (this.selectMarkItems.length){
                this.selectMarkItems[0].unSelected();
            }
        }.bind(this));

        this.elementContentListNode = new Element("div", {
            "styles": this.css.elementContentListNode
        }).inject(this.elementContentNode);

        if( !this.setContentSizeFun ){
            this.setContentSizeFun = this.setContentSize.bind(this);
            this.app.addEvent("resize", this.setContentSizeFun);
            this.app.addEvent("close", function(){
                if (this.setContentSizeFun){
                    this.app.removeEvent("resize", this.setContentSizeFun);
                    this.setContentSizeFun = null;
                }
            }.bind(this));
        }
    },
    setContentSize: function(){
        if (this.elementContentListNode){
            var toolbarSize = (this.toolbarNode) ? this.toolbarNode.getSize() : {"x": 0, "y": 0};
            var nodeSize = (this.node) ? this.node.getSize() : {"x": 0, "y": 0};
            var categorySize = this.categoryElementNode ? this.categoryElementNode.getSize() : {"x": 0, "y": 0};

            var pt = this.elementContentNode.getStyle("padding-top").toFloat();
            var pb = this.elementContentNode.getStyle("padding-bottom").toFloat();

            var height = nodeSize.y-toolbarSize.y-categorySize.y-pt-pb;
            this.elementContentNode.setStyle("height", ""+height+"px");

            if( this.options.itemStyle === 'card' ){
                var count = (nodeSize.x/282).toInt();
                var x = count*282;
                var m = (nodeSize.x-x)/2-10;

                this.elementContentListNode.setStyles({
                    "width": ""+x+"px",
                    "margin-left": "" + m + "px"
                });
            }else{
                this.elementContentListNode.setStyles({
                    "margin-right": "10px"
                });
            }

        }
    },
    setNodeScroll: function(){
        MWF.require("MWF.widget.DragScroll", function(){
            new MWF.widget.DragScroll(this.elementContentNode);
        }.bind(this));
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.elementContentNode, {"indent": false});
        }.bind(this));
    },

    loadElementList: function(){
        this.itemList = [];
        this._loadItemDataList(function(json){
            if (json.data.length){
                this.checkSort(json.data);
                json.data.each(function(item){
                    if (this.categoryList.indexOf(item.category) === -1) if (item.category) this.categoryList.push(item.category);
                    if (!this.elementCategory || (item.category === this.elementCategory)){
                        var itemObj = this._getItemObject(item);
                        itemObj.load();
                        this.checkShow(itemObj);
                        this.itemList.push(itemObj);
                    }

                }.bind(this));
            }else{
                var noElementNode = new Element("div.noElementNode", {
                    "styles": this.css.noElementNode,
                    "text": this.options.tooltip.noElement
                }).inject(this.elementContentListNode);
                noElementNode.addEvent("click", function(e){
                    this._createElement(e);
                }.bind(this));
            }
            if(this.options.topEnable)this.loadTopNode();
            if( !this.isSetContentSize ){
                this.setContentSize();
                this.isSetContentSize = true;
            }
        }.bind(this));
    },
    checkSort: function (data){
        if( !!this.options.sortKey ){
            var sortKey = this.options.sortKey.split("-");
            var key = sortKey[0], isDesc = sortKey[1] === 'desc';
            data.sort(function (a, b){
                var av = a[key];
                var bv = b[key];
                if( typeOf(av) === 'string' && typeOf(bv) === 'string' ){
                    var isLetterA = /^[a-zA-Z0-9]/.test(av);
                    var isLetterB = /^[a-zA-Z0-9]/.test(bv);

                    if (isLetterA && !isLetterB) return isDesc ? 1 : -1; // a是字母，b不是，a排在前面
                    if (!isLetterA && isLetterB) return isDesc ? -1 : 1;  // a不是字母，b是，b排在前面

                    return isDesc ?  bv.localeCompare(av) : av.localeCompare(bv);
                }
                return isDesc ? (bv - av) : (av - bv);
            }.bind(this));
        }
    },
    checkShow: function (i){
        if( this.options.searchKey ){
            var v = this.options.searchKey;
            if( i.data.name.contains(v) || (i.data.alias || "").contains(v) || i.data.id.contains(v) ){
                //i.node.setStyle("display", "");
            }else{
                i.node.setStyle("display", "none");
            }
        }
    },
    loadTopNode: function (){
        if(this.options.categoryEnable)this.loadCategoryList();
        this.createItemStyleNode();
        this.createSortNode();
        this.createSearchNode();
    },
    loadCategoryList: function(){
        this.categoryElementNode.empty();
        var node = new Element("div", {"styles": this.css.categoryElementItemAllNode, "text": MWF.xApplication.process.ProcessManager.LP.all}).inject(this.categoryElementNode);
        if (!this.elementCategory) node.setStyles(this.css.categoryElementItemAllNode_current);
        this.categoryList.each(function(category){
            if( category && category !== "null" ){
                node = new Element("div", {"styles": this.css.categoryElementItemNode, "text": category}).inject(this.categoryElementNode);
                if (this.elementCategory===category){
                    node.setStyles(this.css.categoryElementItemNode_current);
                }
            }
        }.bind(this));
        var categoryItems = this.categoryElementNode.getChildren();
        categoryItems.addEvent("click", function(e){
            var text = e.target.get("text");
            this.elementCategory = (text===MWF.xApplication.process.ProcessManager.LP.all) ? "" : text;
            // categoryItems.setStyles(this.css.categoryElementItemNode);
            // this.categoryElementNode.getFirst().setStyles(this.css.categoryElementItemAllNode);
            // e.target.setStyles((this.elementCategory) ? this.css.categoryElementItemNode_current : this.css.categoryElementItemAllNode_current);
            this.isSetContentSize = false;
            this.reload();
        }.bind(this));
    },
    createItemStyleNode: function (){
        this.itemStyleSwitchNode = new Element("div.itemStyleSwitchNode", {
            styles: this.css.itemStyleSwitchNode
        }).inject(this.categoryElementNode);
        ['line','card'].each(function(style){
            var el = new Element("div", {
                styles: this.css.itemStyleSwitchItemNode
            }).inject(this.itemStyleSwitchNode);
            el.setStyle('background-image',"url('../x_component_process_ProcessManager/$Explorer/default/icon/"+style+".png')");
            el.store("sty", style);
            el.addEvent("click", function(e){
                this.switchItemStyle(el, style);
            }.bind(this));
            if( style === this.options.itemStyle )el.click();
        }.bind(this));
    },
    createSortNode: function(){
        this.itemSortArea = new Element("div.itemStyleSwitchNode", {
            styles: this.css.itemSortArea
        }).inject(this.categoryElementNode);
        this.itemSortSelect = new Element('select.itemSortSelect', {
            styles: this.css.itemSortSelect,
            events: {
                change: function(){
                    this.options.sortKey = this.itemSortSelect[ this.itemSortSelect.selectedIndex ].value;
                    this.setUd();
                    this.reload();
                }.bind(this)
            }
        }).inject(this.itemSortArea);
        new Element('option',{ 'text': this.app.lp.sorkKeyNote, 'value': "" }).inject(this.itemSortSelect);
        this.options.sortKeys.each(function (key){
            var opt = new Element('option',{ 'text': this.app.lp[key] + " " + this.app.lp.asc, 'value': key+"-asc" }).inject(this.itemSortSelect);
            if( this.options.sortKey === opt.get('value') )opt.set('selected', true);
            opt = new Element('option',{ 'text': this.app.lp[key] + " " + this.app.lp.desc, 'value': key+"-desc" }).inject(this.itemSortSelect);
            if( this.options.sortKey === opt.get('value') )opt.set('selected', true);
        }.bind(this));
    },
    createSearchNode: function (){
        this.searchNode = new Element("div.searchNode", {
            "styles": this.css.searchArea
        }).inject(this.categoryElementNode);

        this.searchInput = new Element("input.searchInput", {
            "styles": this.css.searchInput,
            "placeholder": this.app.lp.searchPlacholder,
            "value": this.options.searchKey || ""
        }).inject(this.searchNode);

        this.searchButton = new Element("i", {
            "styles": this.css.searchButton
        }).inject(this.searchNode);

        this.searchCancelButton = new Element("i", {
            "styles": this.css.searchCancelButton
        }).inject(this.searchNode);

        this.searchInput.addEvents({
            focus: function(){
                this.searchNode.addClass("mainColor_border");
                this.searchButton.addClass("mainColor_color");
            }.bind(this),
            blur: function () {
                this.searchNode.removeClass("mainColor_border");
                this.searchButton.removeClass("mainColor_color");
            }.bind(this),
            keydown: function (e) {
                if( (e.keyCode || e.code) === 13 ){
                    this.search();
                }
            }.bind(this),
            keyup: function (e){
                this.searchCancelButton.setStyle('display', this.searchInput.get('value') ? '' : 'none');
            }.bind(this)
        });

        this.searchCancelButton.addEvent("click", function (e) {
            this.searchInput.set("value", "");
            this.searchCancelButton.hide();
            this.search();
        }.bind(this));

        this.searchButton.addEvent("click", function (e) {
            this.search();
        }.bind(this));
    },
    switchItemStyle: function(el){
        if( this.currentItemStyleSwitchItemNode ){
            var cur = this.currentItemStyleSwitchItemNode;
            cur.setStyle('background-image',"url('../x_component_process_ProcessManager/$Explorer/default/icon/"+cur.retrieve('sty')+".png')");
        }
        this.currentItemStyleSwitchItemNode = el;
        el.setStyle('background-image',"url('../x_component_process_ProcessManager/$Explorer/default/icon/"+el.retrieve('sty')+"_active.png')");

        if( el.retrieve('sty') !== this.options.itemStyle ){
            this.options.itemStyle = el.retrieve('sty');
            this.setUd();
            this.reload();
        }

    },
    showDeleteAction: function(){
        if (!this.deleteItemsAction){
            this.deleteItemsAction = new Element("div", {
                "styles": this.css.deleteItemsAction,
                "text": this.app.lp.deleteItems
            }).inject(this.node);
            this.deleteItemsAction.fade("in");
            this.deleteItemsAction.position({
                relativeTo: this.elementContentListNode,
                position: 'centerTop',
                edge: 'centerTop',
                "offset": {"y": this.elementContentNode.getScroll().y}
            });
            this.deleteItemsAction.addEvent("click", function(){
                var _self = this;
                this.app.confirm("warn", this.deleteItemsAction, MWF.APPPM.LP.deleteElementTitle, MWF.APPPM.LP.deleteElement, 300, 120, function(){
                    _self.deleteItems();
                    this.close();
                }, function(){
                    this.close();
                });
            }.bind(this));
        }
    },
    search: function (){
       var v = this.searchInput.get("value");
       this.options.searchKey = v;
        this.itemList.each(function (i){
            if( !v ){
                i.node.setStyle("display", "");
            }else if( i.data.name.contains(v) || (i.data.alias || "").contains(v) || i.data.id.contains(v) ){
                i.node.setStyle("display", "");
            }else{
                i.node.setStyle("display", "none");
            }
        }.bind(this));
    },
    hideDeleteAction: function(){
        if (this.deleteItemsAction) this.deleteItemsAction.destroy();
        delete this.deleteItemsAction;
    },

    _createElement: function(e){

    },
    _loadItemDataList: function(callback){
        this.app.restActions.listProcess(this.app.options.application.id,callback);
    },
    _getItemObject: function(item){
        return MWF.xApplication.process.ProcessManager.Explorer.Item(this, item)
    },
    destroy: function(){
        if( this.itemList && this.itemList.length ){
            this.itemList.each(function (item){
                if(item.destroy)item.destroy();
            });
        }
        this.node.destroy();
        o2.release(this);
    }
});

MWF.xApplication.process.ProcessManager.Explorer.Item = new Class({
    initialize: function(explorer, item){
        this.explorer = explorer;
        this.data = item;
        this.container = this.explorer.elementContentListNode;
        this.css = this.explorer.css;

        this.icon = this._getIcon();
    },

    load: function(){
        this.createNode();
        this.createIconNode();
        //this.createDeleteNode();
        this.createActionNode();
        this.explorer.options.itemStyle === 'line' ? this.createTextNodes_line() : this.createTextNodes();
        this._isNew();
        this.createInforNode();
    },
    destroy: function (){
        if( this.tooltip )this.tooltip.destroy();
        this.node.destroy();
    },
    createNode: function(){
        this.node = new Element("div", {
            "styles": this.explorer.options.itemStyle === 'line' ? this.css.itemNode_line : this.css.itemNode,
            "events": {
                "mouseover": function(){
                    this.deleteActionNode.fade("in");
                    if (this.saveasActionNode) this.saveasActionNode.fade("in");
                }.bind(this),
                "mouseout": function(){
                    this.deleteActionNode.fade("out");
                    if (this.saveasActionNode) this.saveasActionNode.fade("out");
                }.bind(this)
            }
        }).inject(this.container);
    },
    createIconNode: function(){
        if (this.data.icon) this.icon = this.data.icon.substr(this.data.icon.lastIndexOf("/")+1, this.data.icon.length);
        //if (this.data.name.icon) this.icon = this.data.name.icon;
        var iconUrl = this.explorer.path+""+this.explorer.options.style+"/processIcon/"+this.icon;

        var itemIconNode = new Element("div", {
            "styles": this.explorer.options.itemStyle === 'line' ? this.css.itemIconNode_line : this.css.itemIconNode
        }).inject(this.node);
        itemIconNode.setStyle("background", "url("+iconUrl+") center center no-repeat");

        itemIconNode.addEvent("click", function(e){
            this.toggleSelected();
            e.stopPropagation();
        }.bind(this));

        itemIconNode.makeLnk({
            "par": this._getLnkPar()
        });
    },
    toggleSelected: function(){
        if (this.isSelected){
            this.unSelected();
        }else{
            this.selected();
        }
    },
    checkShowCopyInfor: function(){
        if (this.explorer.selectMarkItems.length===1){
            this.explorer.app.notice(this.explorer.app.lp.copyInfor, "infor");
        }
    },
    selected: function(){
        if (this.deleteMode) this.deleteItem();
        this.isSelected = true;
        this.node.setStyles(this.css.itemNode_selected);
        this.explorer.selectMarkItems.push(this);

        this.checkShowCopyInfor();
    },
    unSelected: function(){
        this.isSelected = false;
        this.node.setStyles(this.explorer.options.itemStyle === 'line' ? this.css.itemNode_line : this.css.itemNode);
        this.explorer.selectMarkItems.erase(this);
    },
    createActionNode: function(){
        this.deleteActionNode = new Element("div", {
            "styles": this.css.deleteActionNode
        }).inject(this.node);
        this.deleteActionNode.addEvent("click", function(e){
            this.deleteItem(e);
        }.bind(this));

        this.saveasActionNode = new Element("div", {
            "styles": this.css.saveasActionNode,
            "title": this.explorer.app.lp.copy
        }).inject(this.node);
        this.saveasActionNode.addEvent("click", function(e){
            this.saveas(e);
        }.bind(this));
    },
    // createDeleteNode: function(){
    //     this.deleteActionNode = new Element("div", {
    //         "styles": this.css.deleteActionNode
    //     }).inject(this.node);
    //     this.deleteActionNode.addEvent("click", function(e){
    //         this.deleteItem(e);
    //     }.bind(this));
    // },
    createTextNodes: function(){
        new Element("div", {
            "styles": this.css.itemTextTitleNode,
            "text": this.data.name,
            "title": this.data.name,
            "events": {
                "click": function(e){this._open(e);}.bind(this)
            }
        }).inject(this.node);

        new Element("div", {
            "styles": this.css.itemTextDescriptionNode,
            "text": this.data.description || "",
            "title": this.data.description || ""
        }).inject(this.node);

        new Element("div", {
            "styles": this.css.itemTextDateNode,
            "text": (this.data.updateTime || "")
        }).inject(this.node);
    },
    createTextNodes_line: function(){
        var inforNode = new Element("div", {
            "styles": this.explorer.css.itemInforNode_line
        }).inject(this.node);
        var inforBaseNode = new Element("div", {
            "styles": this.explorer.css.itemInforBaseNode_line
        }).inject(inforNode);

        new Element("div", {
            "styles": this.explorer.css.itemTextTitleNode_line,
            "text": this.data.name,
            "title": this.data.name,
            "events": {
                "click": function(e){this._open(e);e.stopPropagation();}.bind(this)
            }
        }).inject(inforBaseNode);

        new Element("div", {
            "styles": this.explorer.css.itemTextAliasNode_line,
            "text": this.data.alias,
            "title": this.data.alias
        }).inject(inforBaseNode);
        new Element("div", {
            "styles": this.explorer.css.itemTextDateNode_line,
            "text": (this.data.updateTime || "")
        }).inject(inforBaseNode);

        new Element("div", {
            "styles": this.explorer.css.itemTextDescriptionNode_line,
            "text": this.data.description || "",
            "title": this.data.description || ""
        }).inject(inforNode);
    },
    saveas: function(){
        MWF.xDesktop.requireApp("Selector", "package", function(){
            var selector = new MWF.O2Selector(this.explorer.app.content, {
                "title": this.explorer.app.lp.copyto,
                "type": "Application",
                "values": [this.explorer.app.options.application],
                "onComplete": function(items){
                    items.each(function(item){
                        this.saveItemAs(item.data);
                    }.bind(this));
                }.bind(this),
            });
        }.bind(this));
    },
    // saveItemAs: function(item){
    //
    // },
    deleteItem: function(){
        if (this.isSelected) this.unSelected();
        if (!this.deleteMode){
            this.deleteMode = true;
            this.node.setStyle("background-color", "#ffb7b7");
            this.deleteActionNode.setStyle("background-image", "url("+"../x_component_process_ProcessManager/$Explorer/default/processIcon/deleteProcess_red1.png)");
            this.node.removeEvents("mouseover");
            this.node.removeEvents("mouseout");
            if (this.saveasActionNode) this.saveasActionNode.fade("out");

            this.explorer.deleteMarkItems.push(this);
        }else{
            this.deleteMode = false;
            this.node.setStyle("background", "#FFF");
            this.deleteActionNode.setStyle("background-image", "url("+"../x_component_process_ProcessManager/$Explorer/default/processIcon/deleteProcess.png)");
            if (this.saveasActionNode) this.saveasActionNode.fade("in");
            this.node.addEvents({
                "mouseover": function(){
                    this.deleteActionNode.fade("in");
                    if (this.saveasActionNode) this.saveasActionNode.fade("in");
                }.bind(this),
                "mouseout": function(){
                    this.deleteActionNode.fade("out");
                    if (this.saveasActionNode) this.saveasActionNode.fade("out");
                }.bind(this)
            });
            this.explorer.deleteMarkItems.erase(this);
        }
        if (this.explorer.deleteMarkItems.length){
            this.explorer.showDeleteAction();
        }else{
            this.explorer.hideDeleteAction();
        }
    },
    deleteItems: function(){},
    _open: function(e){
        var _self = this;
        var options = {
            "onQueryLoad": function(){
                this.actions = _self.explorer.actions;
                this.category = _self;
                this.options.id = _self.data.id;
                this.application = _self.explorer.app.options.application;
            }
        };
        this.explorer.app.desktop.openApplication(e, "process.ProcessDesigner", options);
    },
    _getIcon: function(){
        var x = (Math.random()*49).toInt();
        return "process_icon_"+x+".png";
    },
    _getLnkPar: function(){
        return {
            "icon": this.explorer.path+this.explorer.options.style+"/processIcon/lnk.png",
            "title": this.data.name,
            "par": "ProcessDesigner#{\"id\": \""+this.data.id+"\"}"
        };
    },
    _isNew: function(){
        if (this.data.updateTime){
            var createDate = Date.parse(this.data.updateTime);
            var currentDate = new Date();
            if (createDate.diff(currentDate, "hour")<12) {
                this.newNode = new Element("div", {
                    "styles": this.explorer.options.itemStyle === 'line' ? this.css.itemNewNode_line : this.css.itemNewNode
                }).inject(this.node);
                this.newNode.addEvent("click", function(e){
                    this.toggleSelected();
                    e.stopPropagation();
                }.bind(this));
            }
        }
    },
    createInforNode: function(callback){
        var lp = this.explorer.app.lp;
        this.inforNode = new Element("div");
        var wrapNode = new Element("div", {
            style: 'display: grid; grid-template-columns: 60px auto; line-height:20px;'
        }).inject(this.inforNode);
        var html = "<div style='grid-column: 1 / -1; font-weight: bold'>"+this.data.name+"</div>";
        html += "<div style='font-weight: bold'>"+lp.alias+": </div><div style='margin-left:10px'>"+( this.data.alias || "" )+"</div>";
        html += "<div style='font-weight: bold'>"+lp.createTime+": </div><div style='margin-left:10px'>"+(this.data.createTime || "")+"</div>";
        html += "<div style='font-weight: bold'>"+lp.updateTime+": </div><div style='float:left; margin-left:10px'>"+(this.data.updateTime||"")+"</div>";
        html += "<div style='font-weight: bold'>"+lp.description+": </div><div style='float:left; margin-left:10px'>"+(this.data.description||"")+"</div>";
        wrapNode.set("html", html);

        this.tooltip = new MWF.xApplication.process.ProcessManager.Explorer.Item.Tooltip(this.explorer.app.content, this.node, this.explorer.app, {}, {
            axis : "y",
            hiddenDelay : 300,
            displayDelay : 200
        });
        this.tooltip.inforNode = this.inforNode;
    },
});

MWF.xDesktop.requireApp("Template", "MTooltips", null, false);
MWF.xApplication.process.ProcessManager.Explorer.Item.Tooltip = new Class({
    Extends: MTooltips,
    options:{
        nodeStyles: {
            "font-size" : "12px",
            "position" : "absolute",
            "max-width" : "500px",
            "min-width" : "180px",
            "z-index" : "1001",
            "background-color" : "#fff",
            "padding" : "10px",
            "border-radius" : "8px",
            "box-shadow": "0 0 12px 0 #999999",
            "-webkit-user-select": "text",
            "-moz-user-select": "text"
        },
        isFitToContainer : true,
        overflow : "scroll"
    },
    _loadCustom : function( callback ){
        if(callback)callback();
    },
    _customNode : function( node, contentNode ){
        this.inforNode.inject(contentNode);
        if( this.inforNode.getSize().y > 300 ){
            this.inforNode.setStyle("padding-bottom", "15px");
        }
        this.fireEvent("customContent", [contentNode, node]);
    }
});

