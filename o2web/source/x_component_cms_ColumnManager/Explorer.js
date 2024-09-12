MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.ColumnManager = MWF.xApplication.cms.ColumnManager || {};

MWF.xDesktop.requireApp("cms.ColumnManager", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("cms.ColumnManager", "package", null, false);

MWF.xApplication.cms.ColumnManager.Explorer = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default",
        "title" : "",
		"tooltip": {
            "create": MWF.CMSCM.LP.category.create,
            "search": MWF.CMSCM.LP.category.search,
            "searchText": MWF.CMSCM.LP.category.searchText,
            "noElement": MWF.CMSCM.LP.category.noCategoryNoticeText
		},
        "topEnable": true,
        "itemStyle": "line",
        "sortKeys": ['name', 'alias', 'createTime', 'updateTime'],
        "sortKey": '',
        "name": 'cms.FormExplorer'
	},
	
	initialize: function(node, actions, options){
		this.setOptions(options);
		this.setTooltip();
		this.path = "../x_component_cms_ColumnManager/$Explorer/";
		this.cssPath = "../x_component_cms_ColumnManager/$Explorer/"+this.options.style+"/css.wcss";

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
        this.itemArray = [];
        this.itemObject = {};
        this.deleteMarkItems = [];
        this.selectMarkItems = [];
	},
    reload: function(){
        if( !this.node )return;
        if( this.itemArray && this.itemArray.length ){
            this.itemArray.each(function (item){
                if(item.destroy)item.destroy();
            });
        }
        this.initData();
        this.node.empty();
        this.load();
    },
    load: function(){
        this.getUd( function (){
            this.loadToolbar();
            this.loadContentNode();

            this.setNodeScroll();
            this.loadElementList();
        }.bind(this));
    },
    getUd: function ( callback ){
        MWF.UD.getDataJson(this.options.name+ "_" + this.app.options.column.id, function (data){
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
        MWF.UD.putData(this.options.name+ "_" + this.app.options.column.id, data);
    },
    loadToolbar: function(){
        this.toolbarNode = new Element("div", {"styles": this.css.toolbarNode});
        this.createTitleElementNode();
        this.createCreateElementNode();

        if(this.options.topEnable){
            this.createItemStyleNode();
            this.createSortNode();
            this.createSearchNode();
        }

        //this.createSearchElementNode();
        this.toolbarNode.inject(this.node);
    },
    createCreateElementNode: function(){
        this.createElementNode = new Element("div", {
            "styles": this.css.createElementNode,
            "title": this.options.tooltip.create
        }).inject(this.toolbarNode);
        this.createElementNode.addEvents({
            "click": function(e){ this._createElement(e); }.bind(this),
            "mouseover" : function(e){
                this.createElementNode.setStyles(this.css.createElementNode_over);
            }.bind(this),
            "mouseout" : function(ev){
                this.createElementNode.setStyles(this.css.createElementNode);
            }.bind(this)
        });
    },
    createTitleElementNode: function() {
        this.titleElementNode = new Element("div", {
            "styles": this.css.titleElementNode,
            "text": this.options.title
        }).inject(this.toolbarNode);
    },
    createSearchElementNode: function(){
        this.searchElementNode = new Element("div", {
            "styles": this.css.searchElementNode
        }).inject(this.toolbarNode);

        //@todo
        // return false;
        //
        // this.searchElementButtonNode = new Element("div", {
        //     "styles": this.css.searchElementButtonNode,
        //     "title": this.options.tooltip.search
        // }).inject(this.searchElementNode);
        //
        // this.searchElementInputAreaNode = new Element("div", {
        //     "styles": this.css.searchElementInputAreaNode
        // }).inject(this.searchElementNode);
        //
        // this.searchElementInputBoxNode = new Element("div", {
        //     "styles": this.css.searchElementInputBoxNode
        // }).inject(this.searchElementInputAreaNode);
        //
        // this.searchElementInputNode = new Element("input", {
        //     "type": "text",
        //     "value": this.options.tooltip.searchText,
        //     "styles": this.css.searchElementInputNode,
        //     "x-webkit-speech": "1"
        // }).inject(this.searchElementInputBoxNode);
        // var _self = this;
        // this.searchElementInputNode.addEvents({
        //     "focus": function(){
        //         if (this.value==_self.options.tooltip.searchText) this.set("value", "");
        //     },
        //     "blur": function(){if (!this.value) this.set("value", _self.options.tooltip.searchText);},
        //     "keydown": function(e){
        //         if (e.code==13){
        //             this.searchElement();
        //             e.preventDefault();
        //         }
        //     }.bind(this),
        //     "selectstart": function(e){
        //         e.preventDefault();
        //     }
        // });
        // this.searchElementButtonNode.addEvent("click", function(){this.searchElement();}.bind(this));
    },
    searchElement: function(){
        //-----------------------------------------
        //-----------------------------------------
        //-----search category---------------------
        //-----------------------------------------
        //-----------------------------------------
        //alert("search Element");
    },

    loadContentNode: function(){
        this.elementContentNode = new Element("div", {
            "styles": this.css.elementContentNode
        }).inject(this.node);

        this.elementContentListNode = new Element("div", {
            "styles": this.css.elementContentListNode
        }).inject(this.elementContentNode);

        this.setContentSize();
        this.app.addEvent("resize", function(){this.setContentSize();}.bind(this));
    },
    setContentSize: function(){
        var toolbarSize = this.toolbarNode ? this.toolbarNode.getSize() : { x : 0 , y : 0 };
        var nodeSize = this.node.getSize();
        var pt = this.elementContentNode.getStyle("padding-top").toFloat();
        var pb = this.elementContentNode.getStyle("padding-bottom").toFloat();

        var height = nodeSize.y-toolbarSize.y-pt-pb;
        this.elementContentNode.setStyle("height", ""+height+"px");

        var contentSize = this.app.content.getSize();
        var menuSize = this.app.leftContentNode.getSize();
        var width = contentSize.x - menuSize.x;
        this.elementContentNode.setStyle("width", ""+(width-10)+"px");

        if (this.options.noCreate) this.createElementNode.destroy();
        //var count = (nodeSize.x/282).toInt();
        //var x = count*282;
        //var m = (nodeSize.x-x)/2-10;
        //
        //this.elementContentListNode.setStyles({
        //    "width": ""+x+"px",
        //    "margin-left": "" + m + "px"
        //});
    },
    setNodeScroll: function(){
        MWF.require("MWF.widget.DragScroll", function(){
            new MWF.widget.DragScroll(this.elementContentNode);
        }.bind(this));
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.elementContentNode, {"indent": false});
        }.bind(this));
    },

    loadElementList: function( callback ){
        this._loadItemDataList(function(json){
            if (json.data.length){
                this.checkSort(json.data);
                json.data.each(function(item){
                    var itemObj = this._getItemObject(item, this.itemArray.length + 1 );
                    itemObj.load();
                    this.checkShow(itemObj);
                    this.itemObject[ item.id ] = itemObj;
                    this.itemArray.push( itemObj );
                }.bind(this));
                if( callback )callback();
            }else{
                var noElementNode = this.noElementNode = new Element("div", {
                    "styles": this.css.noElementNode,
                    "text": this.options.tooltip.noElement
                }).inject(this.elementContentListNode);
                noElementNode.addEvent("click", function(e){
                    this._createElement(e);
                }.bind(this));
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
    createItemStyleNode: function (){
        this.itemStyleSwitchNode = new Element("div.itemStyleSwitchNode", {
            styles: this.css.itemStyleSwitchNode
        }).inject(this.toolbarNode);
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
        }).inject(this.toolbarNode);
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
        }).inject(this.toolbarNode);

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
                relativeTo: this.elementContentListNode
            });
            this.deleteItemsAction.setStyle("top","50%");
            this.deleteItemsAction.addEvent("click", function(){
                var _self = this;
                this.app.confirm("warn", this.deleteItemsAction, MWF.CMSCM.LP.deleteElementTitle, MWF.CMSCM.LP.deleteElement, 300, 120, function(){
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
        this.itemArray.each(function (i){
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
        this.app.restActions.listCategory(this.app.options.column.id,callback);
    },
    _getItemObject: function(item, index){
        return MWF.xApplication.cms.ColumnManager.Explorer.Item(this, item, {index : index })
    },
    destroy: function(){
        if( this.itemArray && this.itemArray.length ){
            this.itemArray.each(function (item){
                if(item.destroy)item.destroy();
            });
        }
        this.node.destroy();
        o2.release(this);
    }
});

MWF.xApplication.cms.ColumnManager.Explorer.Item = new Class({

    Implements: [Options],
    options: {
        "where": "bottom",
        "index" : 1
    },

    initialize: function(explorer, item, options ){
        this.setOptions(options);
        this.explorer = explorer;
        this.data = item;
        this.container = this.explorer.elementContentListNode;

        this.css = this.explorer.css;

        this.icon = this._getIcon();
    },

    load: function(){
        if( this.explorer.options.itemStyle === 'card' ){
            this.itemNodeCss = this.css.itemNode_card;
        }else{
            this.itemNodeCss = this.options.index % 2 === 0 ? this.css.itemNode_even : this.css.itemNode;
        }

        this.node = new Element("div", {
            "styles": this.itemNodeCss,
            "events": {
                "click": function(e){this._open(e);e.stopPropagation();}.bind(this),
                "mouseover": function(){
                    if( !this.isSelected )this.node.setStyles( this.css.itemNode_over );

                    if(this.deleteActionNode)this.deleteActionNode.fade("in");
                    if (this.saveasActionNode) this.saveasActionNode.fade("in");
                }.bind(this),
                "mouseout": function(){
                    if( !this.isSelected )this.node.setStyles( this.itemNodeCss );

                    if(this.deleteActionNode)this.deleteActionNode.fade("out");
                    if (this.saveasActionNode) this.saveasActionNode.fade("out");
                }.bind(this)
            }
        }).inject(this.container,this.options.where);


        if (this.data.icon) this.icon = this.data.icon;
        var iconUrl = this.explorer.path+""+this.explorer.options.style+"/processIcon/"+this.icon;

        var itemIconNode = new Element("div", {
            "styles": this.explorer.options.itemStyle === 'card' ? this.css.itemIconNode_card : this.css.itemIconNode
        }).inject(this.node);
        itemIconNode.setStyle("background", "url("+iconUrl+") center center no-repeat");
        //new Element("img", {
        //    "src": iconUrl, "border": "0"
        //}).inject(itemIconNode);

        itemIconNode.makeLnk({
            "par": this._getLnkPar()
        });

        itemIconNode.addEvent("click", function(e){
            this.toggleSelected();
            e.stopPropagation();
        }.bind(this));

        this.actionsArea = new Element("div.actionsArea",{
            styles : this.explorer.css.actionsArea
        }).inject(this.node);

        this.saveasActionNode = new Element("div", {
            "styles": this.explorer.css.saveasActionNode,
            "title": this.explorer.app.lp.copy
        }).inject(this.actionsArea);
        this.saveasActionNode.addEvent("click", function(e){
            this.saveas(e);
            e.stopPropagation();
        }.bind(this));
        // this.saveasActionNode.addEvents({
        //     "mouseover" : function(ev){
        //         this.saveasActionNode.setStyles( this.explorer.css.saveasActionNode_over )
        //     }.bind(this),
        //     "mouseout" : function(ev){
        //         this.saveasActionNode.setStyles( this.explorer.css.saveasActionNode )
        //     }.bind(this)
        // });

        if (!this.explorer.options.noDelete){
            this.deleteActionNode = new Element("div.deleteAction", {
                "styles": this.explorer.css.deleteAction
            }).inject(this.actionsArea);
            this.deleteActionNode.addEvent("click", function(e){
                this.deleteItem(e);
                e.stopPropagation();
            }.bind(this));
            // this.deleteActionNode.addEvents({
            //     "mouseover" : function(ev){
            //         this.deleteActionNode.setStyles( this.explorer.css.deleteAction_over )
            //     }.bind(this),
            //     "mouseout" : function(ev){
            //         this.deleteActionNode.setStyles( this.explorer.css.deleteAction )
            //     }.bind(this)
            // })
        }

        this.explorer.options.itemStyle === 'card' ? this.createTextNodes_card() : this.createTextNodes();

        this._isNew();

        this.createInforNode();

    },
    createTextNodes_card: function(){
        new Element("div", {
            "styles": this.css.itemTextTitleNode_card,
            "text": this.data.name,
            "title": this.data.name,
            "events": {
                "click": function(e){this._open(e);}.bind(this)
            }
        }).inject(this.node);

        new Element("div", {
            "styles": this.css.itemTextDescriptionNode_card,
            "text": this.data.description || "",
            "title": this.data.description || ""
        }).inject(this.node);

        new Element("div", {
            "styles": this.css.itemTextDateNode_card,
            "text": (this.data.updateTime || "")
        }).inject(this.node);
    },
    createTextNodes: function(){
        var inforNode = new Element("div.itemInforNode", {
            "styles": this.explorer.css.itemInforNode
        }).inject(this.node);
        var inforBaseNode = new Element("div.itemInforBaseNode", {
            "styles": this.explorer.css.itemInforBaseNode
        }).inject(inforNode);

        new Element("div.itemTextTitleNode", {
            "styles": this.explorer.css.itemTextTitleNode,
            "text": this.data.name,
            "title": this.data.name
        }).inject(inforBaseNode);

        new Element("div.itemTextAliasNode", {
            "styles": this.explorer.css.itemTextAliasNode,
            "text": this.data.alias,
            "title": this.data.alias
        }).inject(inforBaseNode);
        new Element("div.itemTextDateNode", {
            "styles": this.explorer.css.itemTextDateNode,
            "text": (this.data.updateTime || "")
        }).inject(inforBaseNode);

        new Element("div.itemTextDescriptionNode", {
            "styles": this.explorer.css.itemTextDescriptionNode,
            "text": this.data.description || "",
            "title": this.data.description || ""
        }).inject(inforBaseNode);

        //new Element("div", {
        //    "styles": this.explorer.css.itemTextTitleNode,
        //    "text": this.data.name,
        //    "title": this.data.name,
        //    "events": {
        //        "click": function(e){this._open(e);}.bind(this)
        //    }
        //}).inject(this.node);
        //
        //new Element("div", {
        //    "styles": this.explorer.css.itemTextDescriptionNode,
        //    "text": this.data.description || "",
        //    "title": this.data.description || ""
        //}).inject(this.node);
        //
        //new Element("div", {
        //    "styles": this.explorer.css.itemTextDateNode,
        //    "text": (this.data.updateTime || "")
        //}).inject(this.node);
    },
    deleteItem: function(){
        if (!this.deleteMode){
            this.deleteMode = true;
            this.node.setStyle("background-color", "#ffb7b7");
            this.deleteActionNode.setStyle("background-image", "url("+"../x_component_cms_ColumnManager/$Explorer/default/processIcon/deleteProcess_red1.png)");
            this.deleteActionNode.removeEvents("mouseover");
            this.deleteActionNode.removeEvents("mouseout");
            this.node.removeEvents("mouseover");
            this.node.removeEvents("mouseout");
            this.explorer.deleteMarkItems.push(this);
        }else{
            this.deleteMode = false;
            this.node.setStyle("background", "#FFF");
            this.deleteActionNode.setStyles( this.explorer.css.deleteAction );
            this.deleteActionNode.addEvents({
                "mouseover" : function(ev){
                    this.deleteActionNode.setStyles( this.explorer.css.deleteAction_over )
                }.bind(this),
                "mouseout" : function(ev){
                    this.deleteActionNode.setStyles( this.explorer.css.deleteAction )
                }.bind(this)
            });
            this.node.addEvents({"mouseover": function(){
                this.node.setStyles( this.explorer.css.itemNode_over )
            }.bind(this),
                "mouseout": function(){
                    this.node.setStyles( this.itemNodeCss )
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
                this.column = _self.explorer.app.options.column;
                this.application = _self.explorer.app.options.column;
            }
        };
        this.explorer.app.desktop.openApplication(e, "cms.ProcessDesigner", options);
    },
    _getIcon: function(){
        var x = (Math.random()*33).toInt();
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
                    "styles": this.explorer.options.itemStyle === 'card' ? this.css.itemNewNode_card : this.css.itemNewNode
                }).inject(this.node);
                this.newNode.addEvent("click", function(e){
                    this.toggleSelected();
                    e.stopPropagation();
                }.bind(this));
            }
        }
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
        this.node.setStyles(this.explorer.css.itemNode_selected);
        this.explorer.selectMarkItems.push(this);

        this.checkShowCopyInfor();
    },
    unSelected: function(){
        this.isSelected = false;
        this.node.setStyles(this.explorer.css.itemNode);
        this.explorer.selectMarkItems.erase(this);
    },
    saveas: function(){
        MWF.xDesktop.requireApp("Selector", "package", function(){
            var app = this.explorer.app.options.application;
            app.name = app.appName;
            var selector = new MWF.O2Selector(this.explorer.app.content, {
                "title": this.explorer.app.lp.copyto,
                "type": "CMSApplication",
                "values": [app],
                "onComplete": function(items){
                    items.each(function(item){
                        this.saveItemAs(item.data);
                    }.bind(this));
                }.bind(this),
            });
        }.bind(this));
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

        this.tooltip = new MWF.xApplication.cms.ColumnManager.Explorer.Item.Tooltip(this.explorer.app.content, this.node, this.explorer.app, {}, {
            axis : "y",
            hiddenDelay : 300,
            displayDelay : 200
        });
        this.tooltip.inforNode = this.inforNode;
    },
});


MWF.xDesktop.requireApp("Template", "MTooltips", null, false);
MWF.xApplication.cms.ColumnManager.Explorer.Item.Tooltip = new Class({
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

