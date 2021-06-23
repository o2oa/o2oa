//MWF.require("MWF.xAction.org.express.RestActions", null,false);
MWF.require("MWF.widget.Mask", null, false);
MWF.require("MWF.widget.O2Identity", null,false);
//MWF.xDesktop.requireApp("Organization", "Selector.package", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.xDesktop.requireApp("process.ProcessManager", "DictionaryExplorer", null, false);
MWF.xApplication.process.Application.WorkExplorer = new Class({
	Extends: MWF.xApplication.process.ProcessManager.DictionaryExplorer,
	Implements: [Options, Events],

    initialize: function(node, actions, options){
        this.setOptions(options);
        this.setTooltip();

        this.path = "../x_component_process_Application/$WorkExplorer/";
        this.cssPath = "../x_component_process_Application/$WorkExplorer/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.actions = actions;
        this.node = $(node);

        this.items=[];
        this.works = {};

        this.filter = null;

        this.isItemsLoaded = false;
        this.isItemLoadding = false;
        this.loadItemQueue = 0;
        this.pageCount = 20;

        //if (!this.personActions) this.personActions = new MWF.xApplication.Org.Actions.RestActions();

        this.initData();
    },
    load: function(){
        this.loadToolbar();
        this.loadFilterNode();
        this.loadFilterConditionNode();
        this.loadContentNode();

        this.setNodeScroll();

        this.mask = new MWF.widget.Mask({"style": "desktop"});
        this.mask.loadNode(this.node);

        this.loadElementList();
    },
    createSearchElementNode: function(){
        this.toCompletedNode = new Element("div", {
            "styles": this.css.toCompletedNode,
            "text": this.app.lp.toCompleted
        }).inject(this.toolbarNode);
        this.toCompletedNode.addEvents({
            "mouseover": function(){
                this.toCompletedNode.setStyles(this.css.toCompletedNode_over);
            }.bind(this),
            "mouseout": function(){
                this.toCompletedNode.setStyles(this.css.toCompletedNode);
            }.bind(this),
            "click": function(){
                this.app.workCompletedConfig();
            }.bind(this)
        });
    },
    clearWorks: function(){
        MWF.release(this.items);
        this.isCountShow = false;
        this.works = null;
        this.items=[];
        this.works = {};
        this.elementContentListNode.empty();
        this.isItemsLoaded = false;
        this.isItemLoadding = false;
        this.loadItemQueue = 0;
    },
    loadFilterNode: function(){
        this.filterNode = new Element("div", {"styles": this.css.filterNode}).inject(this.node);
        this.filterAllProcessNode = new Element("div", {"styles": this.css.filterAllProcessNode, "text": this.app.lp.all}).inject(this.filterNode);
        new Element("span", {"styles": this.css.filterAllProcessCountNode}).inject(this.filterAllProcessNode);
        this.filterAllProcessNode.addEvent("click", function(){
            if (this.currentProcessNode) this.currentProcessNode.setStyles(this.css.filterProcessNode);
            this.currentProcessNode = null;
            this.filter = null;
            this.reloadWorks();
        }.bind(this));

        this.filterActionAreaNode = new Element("div", {"styles": this.css.filterActionAreaNode}).inject(this.filterNode);
        this.filterActionNode = new Element("div", {"styles": this.css.filterActionNode, "text": this.app.lp.filter}).inject(this.filterActionAreaNode);
        this.filterActionNode.addEvent("click", function(e){
            this.showOrHideFilter();e.stopPropagation();
        }.bind(this));

        this.filterProcessAreaNode = new Element("div", {"styles": this.css.filterProcessAreaNode}).inject(this.filterNode);
        this.filterProcessListNode = new Element("div", {"styles": this.css.filterProcessListNode}).inject(this.filterProcessAreaNode);

        this.loadProcess();
    },
    showOrHideFilter: function(){
        if (!this.isFilterOpen){
            if (!this.filterAreaMorph || !this.filterAreaMorph.isRunning()) this.showFilter();
        }else{
            if (this.filterAreaMorph || !this.filterAreaMorph.isRunning()) this.hideFilter();
        }
    },
    showFilter: function(){
        this.filterActionNode.setStyles(this.css.filterActionNode_over);
        if (!this.filterAreaNode) this.createFilterAreaNode();

        this.filterAreaTipNode.setStyle("display", "block");
        this.filterAreaNode.setStyle("display", "block");
        this.resizeFilterAreaNode();
        var toStyle = {
            "width": "460px",
            "height": "500px"
        };

        this.isFilterOpen = true;

        this.filterAreaMorph.start(toStyle).chain(function(){
            this.createFilterAreaTitle();
            this.createFilterAreaContent();

            this.hideFilterFun = this.hideFilter.bind(this);
            $(document.body).addEvent("click", this.hideFilterFun);
        }.bind(this));
    },
    hideFilter: function(){
        if (this.filterAreaNode){
            var toStyle = {
                "width": "460px",
                "height": "0px"
            };
            this.filterAreaNode.empty();
            this.isFilterOpen = false;
            this.filterAreaMorph.start(toStyle).chain(function(){
                this.filterAreaNode.eliminate("input");
                this.filterAreaNode.setStyle("display", "none");
                this.filterAreaTipNode.setStyle("display", "none");
                this.filterActionNode.setStyles(this.css.filterActionNode);
                $(document.body).removeEvent("click", this.hideFilterFun);
            }.bind(this));

            $(document.body).removeEvent("click", this.hideFilterFun);
        }
    },
    createFilterAreaNode: function(){
        this.filterAreaNode = new Element("div", {"styles": this.css.filterAreaNode}).inject(this.app.content);
        this.filterAreaNode.addEvent("click", function(e){e.stopPropagation();});

        this.filterAreaTipNode = new Element("div", {"styles": this.css.filterAreaTipNode}).inject(this.app.content);
        //var size = this.filterActionNode.getSize();
        this.filterAreaNode.setStyles({
            "width": "460px",
            "height": "0px"
        });
        this.filterAreaNode.position({
            relativeTo: this.filterNode,
            position: 'bottomRight',
            edge: 'upperRight',
            offset: {x:-20, y: -1}
        });
        this.filterAreaTipNode.position({
            relativeTo: this.filterNode,
            position: 'bottomRight',
            edge: 'bottomRight',
            offset: {x:-38, y: 0}
        });
        this.app.addEvent("resize", function(){
            this.resizeFilterAreaNode();
        }.bind(this));

        this.filterAreaMorph = new Fx.Morph(this.filterAreaNode, {
            duration: '100',
            transition: Fx.Transitions.Sine.easeInOut
        });
    },
    resizeFilterAreaNode: function(){
        if (this.filterAreaNode){
            this.filterAreaNode.position({
                relativeTo: this.filterNode,
                position: 'bottomRight',
                edge: 'upperRight',
                offset: {x:-20, y: -1}
            });
            if (this.filterAreaTipNode){
                this.filterAreaTipNode.position({
                    relativeTo: this.filterNode,
                    position: 'bottomRight',
                    edge: 'bottomRight',
                    offset: {x:-38, y: 0}
                });
            }
        }
    },
    createFilterAreaTitle: function(){
        var titleNode = new Element("div", {"styles": this.css.filterAreaTitleNode}).inject(this.filterAreaNode);
        var okNode = new Element("div", {"styles": this.css.filterAreaTitleActionOkNode, "text": this.app.lp.ok}).inject(titleNode);
        var clearNode = new Element("div", {"styles": this.css.filterAreaTitleActionClearNode, "text": this.app.lp.clear}).inject(titleNode);
        clearNode.addEvent("click", function(){
            this.filterAreaNode.getElements(".filterItem").each(function(el){
                this.unSelectedFilterItem(el);
            }.bind(this));
            var input = this.filterAreaNode.retrieve("input");
            input.set("value", "");
            this.filter = null;
            this.hideFilter();

            this.reloadWorks();
        }.bind(this));
        okNode.addEvent("click", function(){
            var input = this.filterAreaNode.retrieve("input");
            if (!this.filter) this.filter = {};
            var key = input.get("value");
            if (key && key!=this.app.lp.searchKey){
                this.filter.key = key;
            }else{
                this.filter.key = "";
                delete this.filter.key
            }

            this.hideFilter();
            this.reloadWorks();
        }.bind(this));

        var searchNode = new Element("div", {"styles": this.css.filterAreaTitleSearchNode}).inject(titleNode);
        var searchIconNode = new Element("div", {"styles": this.css.filterAreaTitleSearchIconNode}).inject(searchNode);
        var searchInputAreaNode = new Element("div", {"styles": this.css.filterAreaTitleSearchInputAreaNode}).inject(searchNode);
        var searchInputNode = new Element("input", {"styles": this.css.filterAreaTitleSearchInputNode, "value": this.app.lp.searchKey}).inject(searchInputAreaNode);
        if (this.filter){
            if (this.filter.key) searchInputNode.set("value", this.filter.key);
        }
        this.filterAreaNode.store("input", searchInputNode);

        var key = this.app.lp.searchKey;
        searchInputNode.addEvents({
            "blur": function(){if (!this.get("value")) this.set("value", key)},
            "focus": function(){if (this.get("value")==key) this.set("value", "")},
            "keydown": function(e){
                if (e.code==13){
                    var input = this.filterAreaNode.retrieve("input");
                    if (!this.filter) this.filter = {};
                    var key = input.get("value");
                    if (key && key!=this.app.lp.searchKey){
                        this.filter.key = key;
                    }else{
                        this.filter.key = "";
                        delete this.filter.key
                    }

                    this.hideFilter();
                    this.reloadWorks();
                }
            }.bind(this)
        });
    },

    createFilterAreaContent: function(){
        var contentScrollNode = new Element("div", {"styles": this.css.applicationFilterAreaContentScrollNode}).inject(this.filterAreaNode);
        var contentNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(contentScrollNode);

        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(contentScrollNode, {
                "style":"xApp_filter", "where": "after", "distance": 30, "friction": 4,	"axis": {"x": false, "y": true}
            });
        }.bind(this));

        var _self = this;
        this._getFilterCount(function(json){
            var obj = json.data;
            Object.each(obj, function(v, key){
                var categoryNode = new Element("div", {"styles": this.css.applicationFilterCategoryNode}).inject(contentNode);
                categoryNode.set("text", this.app.lp[key]);
                var itemAreaNode = new Element("div", {"styles": this.css.applicationFilterItemAreaNode}).inject(contentNode);

                //             for (var x=0; x<10; x++){
                v.each(function(item){
                    if (item.value){
                        var itemNode = new Element("div", {"styles": this.css.applicationFilterItemNode}).inject(itemAreaNode);
                        //itemNode.set("text", item.name+"("+item.count+")");
                        itemNode.set("text", item.name);
                        itemNode.store("value", item.value);
                        itemNode.store("textname", item.name);
                        itemNode.store("key", key);

                        itemNode.addEvent("click", function(){
                            if (this.hasClass("applicationFilterItemNode_over")){
                                _self.unSelectedFilterItem(this);
                            }else{
                                _self.selectedFilterItem(this);
                            }
                        });
                        if (this.filter){
                            if (this.filter[key]){
                                if (this.filter[key].some(function(o){return o.value===item.value;})){
                                    this.selectedFilterItem(itemNode);
                                }
                            }
                        }
                    }
                }.bind(this));
                //           }


            }.bind(this));
        }.bind(this));
    },
    _getFilterCount: function(callback){
        this.actions.listFilterAttributeManage(this.app.options.id, function(json){
            if (callback) callback(json);
        });
    },
    unSelectedFilterItem: function(item){
        if (item.hasClass("applicationFilterItemNode_over")){
            var value = item.retrieve("value");
            var name = item.retrieve("textname");
            var key = item.retrieve("key");

            item.setStyles(this.css.applicationFilterItemNode);
            item.removeClass("applicationFilterItemNode_over");
            item.addClass("applicationFilterItemNode");

            if (!this.filter){
                this.filter = {};
            }else{
                if (this.filter[key]){
                    this.filter[key] = this.filter[key].filter(function(o){
                        return o.value!=value;
                    });
                    if (!this.filter[key].length) delete this.filter[key];
                }
            }
            // this.filter[key] = null;
            // delete this.filter[key];

            item.getParent().eliminate("current");
        }
    },
    selectedFilterItem: function(item){
        if (!item.hasClass("applicationFilterItemNode_over")){
            var current = item.getParent().retrieve("current");
            //if (current) this.unSelectedFilterItem(current);

            var value = item.retrieve("value");
            var key = item.retrieve("key");
            var name = item.retrieve("textname");

            item.setStyles(this.css.applicationFilterItemNode_over);
            item.removeClass("applicationFilterItemNode");
            item.addClass("applicationFilterItemNode_over");

            if (!this.filter) this.filter = {};
            if (!this.filter[key]) this.filter[key] = [];
            if (!this.filter[key].some(function(o){return o.value===value;})){
                this.filter[key].push({"value": value, "name": name});
            }

            //this.filter[key] = [value];

            item.getParent().store("current", item);
        }
    },
    searchElement: function(){
        if (!this.filter) this.filter = {};
        var key = this.searchElementInputNode.get("value");
        if (key && key!=this.app.lp.searchKey){
            this.filter.key = key;
            this.hideFilter();
            this.reloadWorks();
        }
    },
    loadFilterConditionNode: function(){
        this.filterConditionNode = new Element("div", {"styles": this.css.filterConditionNode}).inject(this.node);
        this.setFilterConditions();
    },
    setFilterConditions: function(){
        this.filterConditionNode.empty();
        if (this.filter && Object.keys(this.filter).length){
            Object.each(this.filter, function(v, key){
                if (key!=="key"){
                    v.each(function(i){
                        this.createFilterItemNode(key, i);
                    }.bind(this));
                }
            }.bind(this));
            if (this.filter.key){
                this.createFilterItemNode("key", {"name": this.filter.key});
            }
            this.filterProcessListNode.setStyle("display", "none");
        }else{
            this.filterProcessListNode.setStyle("display", "block");
        }
    },
    createFilterItemNode: function(key, v){
        var _self = this;

        var node = new Element("div", {"styles": this.css.filterListItemNode}).inject(this.filterConditionNode);
        var actionNode = new Element("div", {"styles": this.css.filterListItemActionNode}).inject(node);
        var textNode = new Element("div", {"styles": this.css.filterListItemTextNode}).inject(node);
        textNode.set("text", this.app.lp[key]+": "+ v.name);

        actionNode.store("key", key);
        actionNode.store("value", v.name);
        actionNode.store("valueId", v.value);
        node.addEvents({
            "mouseover": function(){
                this.setStyles(_self.css.filterListItemNode_over);
                this.getLast().setStyles(_self.css.filterListItemTextNode_over);
                this.getFirst().setStyles(_self.css.filterListItemActionNode_over);
            },
            "mouseout": function(){
                this.setStyles(_self.css.filterListItemNode);
                this.getLast().setStyles(_self.css.filterListItemTextNode);
                this.getFirst().setStyles(_self.css.filterListItemActionNode);
            }
        });
        actionNode.addEvent("click", function(){
            var key = this.retrieve("key");
            var value = this.retrieve("value");
            var valueId = this.retrieve("valueId");

            if (_self.filter[key]){
                _self.filter[key] = _self.filter[key].filter(function(o){
                    return o.value!==valueId;
                });
                if (!_self.filter[key].length) delete _self.filter[key];
            }

            // if (_self.filter[key]) _self.filter[key] = null;
            // delete _self.filter[key];
            this.destroy();
            _self.reloadWorks();
        });
    },

    loadProcess: function(){
        this.actions.listProcessCountManage(this.app.options.id, function(json){
            json.data.each(function(process){
                this.loadProcessNode(process);
            }.bind(this));
        }.bind(this));
    },
    loadProcessNode: function(process){
        var filterProcessNode = new Element("div", {"styles": this.css.filterProcessNode}).inject(this.filterProcessListNode);
        filterProcessNode.set("text", process.name+"("+process.count+")");
        filterProcessNode.store("process", process);
        var _self = this;
        filterProcessNode.addEvent("click", function(){
            _self.setCurrentProcess(this);
            _self.reloadWorks();
        });
    },
    setCurrentProcess: function(node){
        if (this.currentProcessNode) this.currentProcessNode.setStyles(this.css.filterProcessNode);
        node.setStyles(this.css.filterProcessNode_current);
        this.currentProcessNode = node;

        var process = node.retrieve("process");
        this.filter = {"processList": [{"name": process.name, "value": process.value}]};
    },
    reloadWorks: function(){
        this.clearWorks();
        this.setFilterConditions();
        this.createWorkListHead();
        this.loadElementList();
    },

    loadContentNode: function(){
        this.elementContentNode = new Element("div", {
            "styles": this.css.elementContentNode
        }).inject(this.node);

        this.elementContentListNode = new Element("div", {
            "styles": this.css.elementContentListNode
        }).inject(this.elementContentNode);

        this.createWorkListHead();
        this.setContentSize();
        this.setContentSizeFun = this.setContentSize.bind(this);
        this.app.addEvent("resize", this.setContentSizeFun);
    },
    setNodeScroll: function(){
        //MWF.require("MWF.widget.DragScroll", function(){
        //    new MWF.widget.DragScroll(this.elementContentNode);
        //}.bind(this));
        //MWF.require("MWF.widget.ScrollBar", function(){
        //    new MWF.widget.ScrollBar(this.elementContentNode, {"indent": false});
        //}.bind(this));
        var _self = this;
        this.elementContentNode.addEvent("scroll", function(){
            var y = _self.elementContentNode.scrollTop.toFloat();
            var scrollSize = _self.elementContentNode.getScrollSize();
            var clientSize = _self.elementContentNode.getSize();
            var scrollHeight = scrollSize.y-clientSize.y;
            if (y+200>scrollHeight) {
                if (!_self.isItemsLoaded) _self.loadElementList();
            }
        });
        //
        //var _self = this;
        //MWF.require("MWF.widget.ScrollBar", function(){
        //    new MWF.widget.ScrollBar(this.elementContentNode, {
        //        "indent": false,"style":"xApp_TaskList", "where": "before", "distance": 30, "friction": 4,	"axis": {"x": false, "y": true},
        //        "onScroll": function(y){
        //            var scrollSize = _self.elementContentNode.getScrollSize();
        //            var clientSize = _self.elementContentNode.getSize();
        //            var scrollHeight = scrollSize.y-clientSize.y;
        //            if (y+200>scrollHeight) {
        //                if (!_self.isItemsLoaded) _self.loadElementList();
        //            }
        //        }
        //    });
        //}.bind(this));
    },

    createWorkListHead: function(){
        var headNode = new Element("div", {"styles": this.css.workItemHeadNode}).inject(this.elementContentListNode);
        var html = "<div class='headArea1Node'><div class='checkAreaHeadNode'></div><div class='iconAreaHeadNode'></div><div class='titleAreaHeadNode'>"+this.app.lp.workTitle+"</div></div>" +
            "<div class='headArea2Node'><div class='statusAreaHeadNode'>"+this.app.lp.status+"</div><div class='activityAreaHeadNode'>"+this.app.lp.activity+"</div></div>" +
            "<div class='headArea3Node'><div class='actionAreaHeadNode'>"+this.app.lp.action+"</div><div class='expireAreaHeadNode'>"+this.app.lp.expire+"</div><div class='personAreaHeadNode'>"+this.app.lp.person+"</div></div>";
        headNode.set("html", html);

        headNode.getElement(".headArea1Node").setStyles(this.css.headArea1Node);
        headNode.getElement(".headArea2Node").setStyles(this.css.headArea2Node);
        headNode.getElement(".headArea3Node").setStyles(this.css.headArea3Node);

        headNode.getElement(".checkAreaHeadNode").setStyles(this.css.checkAreaHeadNode);
        headNode.getElement(".iconAreaHeadNode").setStyles(this.css.iconAreaHeadNode);
        headNode.getElement(".titleAreaHeadNode").setStyles(this.css.titleAreaHeadNode);
        headNode.getElement(".statusAreaHeadNode").setStyles(this.css.statusAreaHeadNode);
        headNode.getElement(".activityAreaHeadNode").setStyles(this.css.activityAreaHeadNode);
        headNode.getElement(".personAreaHeadNode").setStyles(this.css.personAreaHeadNode);
        headNode.getElement(".actionAreaHeadNode").setStyles(this.css.actionAreaHeadNode);
        headNode.getElement(".expireAreaHeadNode").setStyles(this.css.expireAreaHeadNode);

    },

    createCreateElementNode: function(){},
    setContentSize: function(){
        var toolbarSize = this.toolbarNode.getSize();
        var nodeSize = this.node.getSize();
        var pt = this.elementContentNode.getStyle("padding-top").toFloat();
        var pb = this.elementContentNode.getStyle("padding-bottom").toFloat();
        var filterSize = this.filterNode.getSize();
        var filterConditionSize = this.filterConditionNode.getSize();

        var height = nodeSize.y-toolbarSize.y-pt-pb-filterSize.y-filterConditionSize.y;
        this.elementContentNode.setStyle("height", ""+height+"px");

        this.pageCount = (height/40).toInt()+5;

        if (this.items.length<this.pageCount){
            this.loadElementList(this.pageCount-this.items.length);
        }

        if (this.options.noCreate) this.createElementNode.destroy();
    },

    _getCurrentPageData: function(callback, count){
        var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
        if (this.filter){
            var filterData = {};
            Object.each(this.filter, function(v, k){
                if (k!="key"){
                    if (!filterData[k]) filterData[k] = [];
                    v.each(function(o){
                        filterData[k].push(o.value);
                    });
                }else{
                    filterData[k] = v;
                }
            });
            this.actions.listWorkFilterManage(id, count || this.pageCount, this.app.options.id, filterData, function(json){
                if (callback) callback(json);
            });
        }else{
            this.actions.listWorkNextManage(id, count || this.pageCount, this.app.options.id, function(json){
                if (callback) callback(json);
            });
        }
    },
    loadElementList: function(count){
        if (!this.isItemsLoaded){
            if (!this.isItemLoadding){
                this.isItemLoadding = true;
                this._getCurrentPageData(function(json){
                    this.count = json.count;

                    if (!this.isCountShow){
                        this.filterAllProcessNode.getFirst("span").set("text", "("+this.count+")");
                        this.isCountShow = true;
                    }
                    if (json.count<=this.items.length){
                        this.isItemsLoaded = true;
                    }
                    json.data.each(function(data){
                        if (!this.works[data.id]){
                            var item = this._createItem(data);
                            this.items.push(item);
                            this.works[data.id] = item;
                        }
                    }.bind(this));

                    this.isItemLoadding = false;

                    if (this.loadItemQueue>0){
                        this.loadItemQueue--;
                        this.loadElementList();
                    }

                    this.mask.hide();
                }.bind(this), count);
            }else{
                this.loadItemQueue++;
            }
        }
    },
    _createItem: function(data){
        return new MWF.xApplication.process.Application.WorkExplorer.Work(data, this);
    },
    removeWork: function(work, all){
        this.actions.removeWork(work.data.id, this.app.options.id, all, function(json){
            json.data.each(function(item){
                this.works[item.id].destroy();
                this.items.erase(this.works[item.id]);
                MWF.release(this.works[item.id]);
                delete this.works[item.id];
            }.bind(this));
        }.bind(this));
    }
});

MWF.xApplication.process.Application.WorkExplorer.Work = new Class({
    initialize: function(data, explorer, relative){
        this.explorer = explorer;
        this.data = data;
        this.container = this.explorer.elementContentListNode;
        this.relative = relative;

        this.css = this.explorer.css;

        this.tasks = [];
        this.dones = [];
        this.reads = [];
        this.readeds = [];

        this.taskChildren = {};
        this.doneChildren = {};
        this.readChildren = {};
        this.readedChildren = {};

        this.relativeWorks = [];

        this.load();
    },

    load: function(){
        this.node = new Element("div", {"styles": this.css.workItemNode});

        if (this.relative){
            this.node.inject(this.relative.node, "after");
        }else{
            this.node.inject(this.container);
        }

        this.workAreaNode =  new Element("div", {"styles": this.css.workItemWorkNode}).inject(this.node);
        //this.otherWorkAreaNode =  new Element("div", {"styles": this.css.workItemWorkNode}).inject(this.node);

        var html = "<div class='area1Node'><div class='checkAreaNode'></div><div class='iconAreaNode'></div><div class='titleAreaNode'><div class='titleAreaTextNode'>"+( this.data.title || "" )+"</div></div></div>" +
            "<div class='area2Node'><div class='statusAreaNode'></div><div class='activityAreaNode'><div class='activityAreaTextNode'>"+this.data.activityName+"</div></div></div>" +
            "<div class='area3Node'><div class='actionAreaNode'></div><div class='expireAreaNode'></div><div class='personAreaNode'></div></div>";
        this.workAreaNode.set("html", html);

        this.workAreaNode.getElement(".area1Node").setStyles(this.css.headArea1Node);
        this.workAreaNode.getElement(".area2Node").setStyles(this.css.headArea2Node);
        this.workAreaNode.getElement(".area3Node").setStyles(this.css.headArea3Node);

        this.checkAreaNode = this.workAreaNode.getElement(".checkAreaNode").setStyles(this.css.checkAreaHeadNode);
        this.iconAreaNode = this.workAreaNode.getElement(".iconAreaNode").setStyles(this.css.iconAreaHeadNode);
        this.titleAreaNode = this.workAreaNode.getElement(".titleAreaNode").setStyles(this.css.titleAreaHeadNode);
        this.statusAreaNode = this.workAreaNode.getElement(".statusAreaNode").setStyles(this.css.statusAreaHeadNode);
        this.activityAreaNode = this.workAreaNode.getElement(".activityAreaNode").setStyles(this.css.activityAreaHeadNode);
        this.personAreaNode = this.workAreaNode.getElement(".personAreaNode").setStyles(this.css.personAreaHeadNode);
        this.actionAreaNode = this.workAreaNode.getElement(".actionAreaNode").setStyles(this.css.actionAreaHeadNode);
        this.expireAreaNode = this.workAreaNode.getElement(".expireAreaNode").setStyles(this.css.expireAreaHeadNode);

        this.activityAreaTextNode = this.workAreaNode.getElement(".activityAreaTextNode").setStyles(this.css.activityAreaTextNode);
        this.titleAreaTextNode = this.workAreaNode.getElement(".titleAreaTextNode").setStyles(this.css.titleAreaTextNode);

        //expireAreaNode
        if (this.data.expireTime){
            var d = Date.parse(this.data.expireTime);
            var today = new Date();
            if (today.diff(d, "second")<0){
                this.expireAreaNode.setStyle("color", "#FF0000");
            }
            this.expireAreaNode.set("text", this.data.expireTime);
        }else{
            this.expireAreaNode.set("text", this.explorer.app.lp.noExpire);
        }
        this.expireAreaNode.setStyles(this.css.expireAreaNode);
        //这是一段测试----------------------
        //if (this.data.rank==1){
        //    this.checkAreaNode.setStyles({
        //        "min-height": "40px",
        //        "height": "100%",
        //        "background": "url("+"../x_component_process_Application/$WorkExplorer/default/icon/groupTop.png) no-repeat center bottom",
        //    });
        //}
        //if (this.data.rank==2 || this.data.rank==3){
        //    this.checkAreaNode.setStyles({
        //        "min-height": "40px",
        //        "height": "100%",
        //        "background": "url("+"../x_component_process_Application/$WorkExplorer/default/icon/groupCenter.png) repeat center top",
        //    });
        //}
        //if (this.data.rank==4){
        //    this.checkAreaNode.setStyles({
        //        "min-height": "40px",
        //        "height": "100%",
        //        "background": "url("+"../x_component_process_Application/$WorkExplorer/default/icon/groupBottom.png) no-repeat center top",
        //    });
        //}
        //这是一段测试----------------------

         if (!this.data.control.allowVisit){
            this.node.setStyles(this.css.workItemNode_noread);
            this.checkAreaNode.setStyles(this.css.actionStopWorkNode);
            this.actionAreaNode.setStyles(this.css.actionStopWorkActionNode);
        }

        this.iconAreaNode.setStyles(this.css.iconWorkNode);
        this.titleAreaNode.setStyles(this.css.titleWorkNode);
        this.setPersonData();
        this.setStatusData();

        this.setActions();

        this.setEvents();

        if (!this.relative) this.listRelatives();
    },
    reload: function(callback){
        if (this.relative){
            this.relative.reload();
        }else{
            this.explorer.actions.getWork(this.data.id, function(json){
                this.data = json.data;
                this.titleAreaTextNode.set("text", this.data.title || "");
                this.activityAreaTextNode.set("text", this.data.activityName);

                this.statusAreaNode.empty();
                this.setStatusData();

                if (this.relativeWorks.length){
                    this.relativeWorks.each(function(work){
                        work.destroy();
                    });
                }
                this.relativeWorks = [];
                this.listRelatives();

                if (callback) callback();
            }.bind(this));
        }
    },
    listRelatives: function() {
        if (this.data.splitting){
            this.explorer.actions.listRelatives(this.data.id, function(json){
                if (json.data){
                    if (json.data.length) this.loadRelativeWorks(json.data);
                }
            }.bind(this));
        }
    },
    loadRelativeWorks: function(list){
        this.checkAreaNode.setStyles(this.css.relativeTop);
        this.workAreaNode.setStyle("height", "40px");
        for (var idx=list.length-1; idx>=0; idx--){
            var data = list[idx];
            var work = this.explorer.works[data.id];
            if (work){
                work.node.inject(this.node, "after");
                work.relative = this;
            }else{
                var work = new MWF.xApplication.process.Application.WorkExplorer.Work(data, this.explorer, this);
                this.explorer.works[data.id] = work
            }
            work.workAreaNode.setStyle("height", "40px");
            if (idx == (list.length-1)){
                work.checkAreaNode.setStyles(this.css.relativeBottom);
            }else{
                work.checkAreaNode.setStyles(this.css.relativeCenter);
            }
            this.relativeWorks.push(work);
        }
    },

    setEvents: function(){
        if (this.openNode){
            this.openNode.addEvents({
                "mouseover": function(){this.openNode.setStyles(this.css.actionOpenNode_over);}.bind(this),
                "mouseout": function(){this.openNode.setStyles(this.css.actionOpenNode);}.bind(this),
                "mousedown": function(){this.openNode.setStyles(this.css.actionOpenNode_down);}.bind(this),
                "mouseup": function(){this.openNode.setStyles(this.css.actionOpenNode_over);}.bind(this),
                "click": function(e){
                    this.openWork(e);
                }.bind(this)
            });
        }
        if (this.processNode){
            this.processNode.addEvents({
                "mouseover": function(){this.processNode.setStyles(this.css.actionProcessNode_over);}.bind(this),
                "mouseout": function(){this.processNode.setStyles(this.css.actionProcessNode);}.bind(this),
                "mousedown": function(){this.processNode.setStyles(this.css.actionProcessNode_down);}.bind(this),
                "mouseup": function(){this.processNode.setStyles(this.css.actionProcessNode_over);}.bind(this),
                "click": function(e){
                    this.processWork(e);
                }.bind(this)
            });
        }
        if (this.rerouteNode){
            this.rerouteNode.addEvents({
                "mouseover": function(){this.rerouteNode.setStyles(this.css.actionRerouteNode_over);}.bind(this),
                "mouseout": function(){this.rerouteNode.setStyles(this.css.actionRerouteNode);}.bind(this),
                "mousedown": function(){this.rerouteNode.setStyles(this.css.actionRerouteNode_down);}.bind(this),
                "mouseup": function(){this.rerouteNode.setStyles(this.css.actionRerouteNode_over);}.bind(this),
                "click": function(e){
                    this.rerouteWork(e);
                }.bind(this)
            });
        }
        if (this.deleteNode){
            this.deleteNode.addEvents({
                "mouseover": function(){this.deleteNode.setStyles(this.css.actionDeleteNode_over);}.bind(this),
                "mouseout": function(){this.deleteNode.setStyles(this.css.actionDeleteNode);}.bind(this),
                "mousedown": function(){this.deleteNode.setStyles(this.css.actionDeleteNode_down);}.bind(this),
                "mouseup": function(){this.deleteNode.setStyles(this.css.actionDeleteNode_over);}.bind(this),
                "click": function(e){
                    this.remove(e);
                }.bind(this)
            });
        }
        if (this.data.control.allowVisit) {
            this.workAreaNode.addEvents({
                "mouseover": function(){if (!this.readyRemove && !this.readyFlow) this.workAreaNode.setStyles(this.css.workItemWorkNode_over);}.bind(this),
                "mouseout": function(){if (!this.readyRemove && !this.readyFlow) this.workAreaNode.setStyles(this.css.workItemWorkNode);}.bind(this)
            });
            this.titleAreaNode.addEvent("click", function(){
                this.loadChild();
            }.bind(this));
        }
    },
    processWork: function(e){
        var lp = this.explorer.app.lp;
        var text = lp.flowWork.replace(/{title}/g, this.data.title || "");
        var _self = this;
        this.workAreaNode.setStyles(this.css.workItemWorkNode_action);
        this.readyFlow = true;

        this.explorer.app.confirm("warn", e, lp.flowWorkTitle, text, 380, 130, function(){
            debugger;
            _self.explorer.actions.flowWork(_self.data.id, {}, function(json){
                if (json.data.id){
                    _self.explorer.actions.getWork(_self.data.id, function(workJson){
                        _self.workAreaNode.setStyles(_self.css.workItemWorkNode);
                        _self.readyFlow = false;
                        _self.reload();
                    }.bind(this));
                }else{
                    _self.reload();
                }
                this.close();
            }.bind(this), function(){
                _self.workAreaNode.setStyles(_self.css.workItemWorkNode);
                _self.readyFlow = false;
                _self.reload();
                this.close();
            }.bind(this));
        }, function(){
            _self.workAreaNode.setStyles(_self.css.workItemWorkNode);
            _self.readyFlow = false;
            this.close();
        });
    },
    rerouteWork: function(){
        var lp = this.explorer.app.lp;
        this.workAreaNode.setStyles(this.css.workItemWorkNode_action);
        this.readyReroute = true;

        MWF.require("MWF.xDesktop.Dialog", function(){
            var width = 560;
            var height = 260;
            var p = MWF.getCenterPosition(this.explorer.app.content, width, height);

            var _self = this;
            var dlg = new MWF.xDesktop.Dialog({
                "title": lp.reroute,
                "style": "work",
                "top": p.y-100,
                "left": p.x,
                "fromTop": p.y-100,
                "fromLeft": p.x,
                "width": width,
                "height": height,
                "url": this.explorer.app.path+"reroute.html",
                "container": this.explorer.app.content,
                "isClose": true,
                "onPostShow": function(){
                    $("rerouteWork_okButton").addEvent("click", function(){
                        _self.doRerouteWork(this);
                    }.bind(this));
                    $("rerouteWork_cancelButton").addEvent("click", function(){
                        this.close();
                    }.bind(this));

                    var select = $("rerouteWork_selectActivity");
                    _self.explorer.actions.getRerouteTo(_self.data.process, function(json){
                        if (json.data.agentList) json.data.agentList.each(function(activity){
                            new Element("option", {
                                "value": activity.id+"#agent",
                                "text": activity.name
                            }).inject(select);
                        }.bind(_self));

                        if (json.data.cancelList) json.data.cancelList.each(function(activity){
                            new Element("option", {
                                "value": activity.id+"#cancel",
                                "text": activity.name
                            }).inject(select);
                        }.bind(_self));

                        if (json.data.choiceList) json.data.choiceList.each(function(activity){
                            new Element("option", {
                                "value": activity.id+"#choice",
                                "text": activity.name
                            }).inject(select);
                        }.bind(_self));

                        if (json.data.conditionList) json.data.conditionList.each(function(activity){
                            new Element("option", {
                                "value": activity.id+"#condition",
                                "text": activity.name
                            }).inject(select);
                        }.bind(_self));

                        if (json.data.delayList) json.data.delayList.each(function(activity){
                            new Element("option", {
                                "value": activity.id+"#delay",
                                "text": activity.name
                            }).inject(select);
                        }.bind(_self));

                        if (json.data.embedList) json.data.embedList.each(function(activity){
                            new Element("option", {
                                "value": activity.id+"#embed",
                                "text": activity.name
                            }).inject(select);
                        }.bind(_self));

                        if (json.data.endList) json.data.endList.each(function(activity){
                            new Element("option", {
                                "value": activity.id+"#end",
                                "text": activity.name
                            }).inject(select);
                        }.bind(_self));

                        if (json.data.invokeList) json.data.invokeList.each(function(activity){
                            new Element("option", {
                                "value": activity.id+"#invoke",
                                "text": activity.name
                            }).inject(select);
                        }.bind(_self));

                        if (json.data.manualList) json.data.manualList.each(function(activity){
                            new Element("option", {
                                "value": activity.id+"#manual",
                                "text": activity.name
                            }).inject(select);
                        }.bind(_self));

                        if (json.data.mergeList) json.data.mergeList.each(function(activity){
                            new Element("option", {
                                "value": activity.id+"#merge",
                                "text": activity.name
                            }).inject(select);
                        }.bind(_self));

                        if (json.data.messageList) json.data.messageList.each(function(activity){
                            new Element("option", {
                                "value": activity.id+"#message",
                                "text": activity.name
                            }).inject(select);
                        }.bind(_self));

                        if (json.data.parallelList) json.data.parallelList.each(function(activity){
                            new Element("option", {
                                "value": activity.id+"#parallel",
                                "text": activity.name
                            }).inject(select);
                        }.bind(_self));

                        if (json.data.serviceList) json.data.serviceList.each(function(activity){
                            new Element("option", {
                                "value": activity.id+"#service",
                                "text": activity.name
                            }).inject(select);
                        }.bind(_self));

                        if (json.data.splitList) json.data.splitList.each(function(activity){
                            new Element("option", {
                                "value": activity.id+"#split",
                                "text": activity.name
                            }).inject(select);
                        }.bind(_self));
                    }.bind(_self));

                    var selPeopleButton = this.content.getElement(".rerouteWork_selPeopleButton");
                    selPeopleButton.addEvent("click", function () {
                        _self.selectReroutePeople(this);
                    }.bind(this));
                }
            });
            dlg.show();
        }.bind(this));
    },
    selectReroutePeople: function(dlg){
        var names = dlg.identityList || [];
        var areaNode = dlg.content.getElement(".rerouteWork_selPeopleArea");
        var options = {
            "values": names,
            "type": "identity",
            "count": 0,
            "title": this.explorer.app.lp.reroute,
            "onComplete": function (items) {
                areaNode.empty();
                var identityList = [];
                items.each(function (item) {
                    new MWF.widget.O2Identity(item.data, areaNode, { "style": "reset" });
                    identityList.push(item.data.distinguishedName);
                }.bind(this));
                dlg.identityList = identityList;
            }.bind(this)
        };
        MWF.xDesktop.requireApp("Selector", "package", function () {
            var selector = new MWF.O2Selector(this.explorer.app.content, options);
        }.bind(this));
    },
    doRerouteWork: function(dlg){
        var opinion = $("rerouteWork_opinion").get("value");
        var select = $("rerouteWork_selectActivity");
        var activity = select.options[select.selectedIndex].get("value");
        var activityName = select.options[select.selectedIndex].get("text");
        var tmp = activity.split("#");
        activity = tmp[0];
        var type = tmp[1];

        var nameArr = [];
        var names = dlg.identityList || [];
        names.each(function (n) { nameArr.push(n); });

        MWF.require("MWF.widget.Mask", function(){
            this.mask = new MWF.widget.Mask({"style": "desktop", "zIndex": 50000});
            this.mask.loadNode(this.explorer.app.content);

            this.rerouteWorkToActivity(activity, type, opinion, nameArr, function(){
                this.explorer.actions.getWork(this.data.id, function(workJson){
                    this.data = workJson.data;
                    this.workAreaNode.setStyles(this.css.workItemWorkNode);
                    this.readyReroute = false;
                    this.reload();
                }.bind(this));
                dlg.close();
                if (this.mask) {this.mask.hide(); this.mask = null;}
            }.bind(this), function(xhr, text, error){
                var errorText = error+":"+text;
                if (xhr) errorText = xhr.responseText;
                this.app.notice("request json error: "+errorText, "error", dlg.node);
                if (this.mask) {this.mask.hide(); this.mask = null;}
            }.bind(this));
        }.bind(this));
    },
    rerouteWorkToActivity: function(activity, type, opinion, nameArr, success, failure){
        var body = {
            "activity": activity,
            "activityType": type,
            "mergeWork": false,
            "manualForceTaskIdentityList": nameArr
        };
        o2.Actions.load("x_processplatform_assemble_surface").WorkAction.V2Reroute(this.data.id, body, function(){
            if (success) success();
        }.bind(this), function (xhr, text, error) {
            if (failure) failure(xhr, text, error);
        });

        // this.explorer.actions.rerouteWork(this.data.id, activity, type, null, function(json){
        //     if (success) success();
        // }.bind(this), function(xhr, text, error){
        //     if (failure) failure(xhr, text, error);
        // });
    },
    openWork: function(e){
        var options = {"workId": this.data.id, "isControl": this.explorer.app.options.application.allowControl};
        this.explorer.app.desktop.openApplication(e, "process.Work", options);
    },
    remove: function(e){
        var lp = this.explorer.app.lp;
        var text = lp.deleteWork.replace(/{title}/g, this.data.title || "");
        var _self = this;
        this.workAreaNode.setStyles(this.css.workItemWorkNode_remove);
        this.readyRemove = true;
        this.explorer.app.confirm("warn", e, lp.deleteWorkTitle, text, 350, 120, function(){
            this.close();
            _self.explorer.app.confirm("warn", e, lp.deleteWorkTitle, {"html": lp.deleteAllWork}, 400, 220, function(){
                var inputs = this.content.getElements("input");
                var flag = "";
                for (var i=0; i<inputs.length; i++){
                    if (inputs[i].checked){
                        flag = inputs[i].get("value");
                        break;
                    }
                }
                if (flag){
                    if (flag=="all"){
                        _self.explorer.removeWork(_self, true);
                    }else{
                        _self.explorer.removeWork(_self, false);
                    }
                    this.close();
                }else{
                    this.content.getElement("#deleteWork_checkInfor").set("text", lp.deleteAllWorkCheck).setStyle("color", "red");
                }
            }, function(){
                //_self.explorer.removeWork(_self, false);
                _self.workAreaNode.setStyles(_self.css.workItemWorkNode);
                this.close();
            });
        }, function(){
            _self.workAreaNode.setStyles(_self.css.workItemWorkNode);
            _self.readyRemove = false;
            this.close();
        });
    },
    destroy: function(){
        this.node.destroy();
    },
    loadChild: function(){
        if (!this.childNode){
            this.createChildNode();
            this.listAssignments();
        }else{
            if (this.childNode.getStyle("display")=="none"){
                this.childNode.setStyle("display", "block");
            }else{
                this.childNode.setStyle("display", "none");
            }
        }
    },
    createChildNode: function(){
        this.childNode =  new Element("div", {"styles": this.css.workItemChildNode}).inject(this.node);
        this.taskAreaNode =  new Element("div", {"styles": this.css.workItemTaskNode}).inject(this.childNode);
        this.doneAreaNode =  new Element("div", {"styles": this.css.workItemDonwNode}).inject(this.childNode);
        this.readAreaNode =  new Element("div", {"styles": this.css.workItemReadNode}).inject(this.childNode);
        this.readedAreaNode =  new Element("div", {"styles": this.css.workItemReadedNode}).inject(this.childNode);

        this.taskAreaTitleAreaNode =  new Element("div", {"styles": this.css.workItemListTitleNode}).inject(this.taskAreaNode);
        this.doneAreaTitleAreaNode =  new Element("div", {"styles": this.css.workItemListTitleNode}).inject(this.doneAreaNode);
        this.readAreaTitleAreaNode =  new Element("div", {"styles": this.css.workItemListTitleNode}).inject(this.readAreaNode);
        this.readedAreaTitleAreaNode =  new Element("div", {"styles": this.css.workItemListTitleNode}).inject(this.readedAreaNode);

        var taskAreaTitleNode =  new Element("div", {"styles": this.css.workItemTaskTitleNode, "text": this.explorer.app.lp.task}).inject(this.taskAreaTitleAreaNode);
        var doneAreaTitleNode =  new Element("div", {"styles": this.css.workItemDoneTitleNode, "text": this.explorer.app.lp.done}).inject(this.doneAreaTitleAreaNode);
        var readAreaTitleNode =  new Element("div", {"styles": this.css.workItemReadTitleNode, "text": this.explorer.app.lp.read}).inject(this.readAreaTitleAreaNode);
        var readedAreaTitleNode =  new Element("div", {"styles": this.css.workItemReadedTitleNode, "text": this.explorer.app.lp.readed}).inject(this.readedAreaTitleAreaNode);

        this.taskAreaContentNode =  new Element("div", {"styles": this.css.taskAreaContentNode}).inject(this.taskAreaNode);
        this.doneAreaContentNode =  new Element("div", {"styles": this.css.doneAreaContentNode}).inject(this.doneAreaNode);
        this.readAreaContentNode =  new Element("div", {"styles": this.css.readAreaContentNode}).inject(this.readAreaNode);
        this.readedAreaContentNode =  new Element("div", {"styles": this.css.readedAreaContentNode}).inject(this.readedAreaNode);

    },
    listAssignments: function(){
        this.explorer.actions.listAssignments(this.data.id, function(json){
            this.listTasks(json.data.taskList);
            this.listDones(json.data.taskCompletedList);
            this.listReads(json.data.readList);
            this.listReadeds(json.data.readCompletedList);
        }.bind(this));
    },
    listTasks: function(list){
        if (list.length){
            list.each(function(data){
                var item = new MWF.xApplication.process.Application.WorkExplorer.Task(data, this.taskAreaContentNode, this.explorer, this);
                this.tasks.push(item);
                this.taskChildren[data.id] = item;
            }.bind(this));
        }else{
            new Element("div", {"styles": this.css.noListText, "text": "［"+this.explorer.app.lp.noTask+"］"}).inject(this.taskAreaTitleAreaNode);
        }
    },
    listDones: function(list){
        if (list.length){
            list.each(function(data){
                var item = new MWF.xApplication.process.Application.WorkExplorer.Done(data, this.doneAreaContentNode, this.explorer, this);
                this.dones.push(item);
                this.doneChildren[data.id] = item;
            }.bind(this));
        }else{
            new Element("div", {"styles": this.css.noListText, "text": "［"+this.explorer.app.lp.noDone+"］"}).inject(this.doneAreaTitleAreaNode);
        }
    },
    listReads: function(list){
        if (list.length){
            list.each(function(data){
                var item = new MWF.xApplication.process.Application.WorkExplorer.Read(data, this.readAreaContentNode, this.explorer, this);
                this.reads.push(item);
                this.readChildren[data.id] = item;
            }.bind(this));
        }else{
            new Element("div", {"styles": this.css.noListText, "text": "［"+this.explorer.app.lp.noRead+"］"}).inject(this.readAreaTitleAreaNode);
        }
    },
    listReadeds: function(list){
        if (list.length){
            list.each(function(data){
                var item = new MWF.xApplication.process.Application.WorkExplorer.Readed(data, this.readedAreaContentNode, this.explorer, this);
                this.readeds.push(item);
                this.readedChildren[data.id] = item;
            }.bind(this));
        }else{
            new Element("div", {"styles": this.css.noListText, "text": "［"+this.explorer.app.lp.noReaded+"］"}).inject(this.readedAreaTitleAreaNode);
        }
    },

    setPersonData: function(){
        // var data = {
        //     "name": MWF.name.cn(this.data.creatorIdentity),
        //     "unitName": (this.data.creatorUnit) ? MWF.name.cn(this.data.creatorUnit) : "",
        //     "id": this.data.creatorIdentity
        // };
        this.personAreaNode.set("text", MWF.name.cn(this.data.creatorIdentity)+"("+((this.data.creatorUnit) ? MWF.name.cn(this.data.creatorUnit) : "")+")");
        //new MWF.widget.O2Identity(data, this.personAreaNode, {"style": "work", "lazy": true});
    },
    setStatusData: function(){
        this.createStatusIcon("start");
        this.createStatusIcon("processing");
        this.createStatusIcon("hanging");
        this.statusAreaNode.set("title", this.explorer.app.lp[this.data.workStatus]);
    },
    createStatusIcon: function(name){
        var node = new Element("div", {"styles": this.css.statusIconNode}).inject(this.statusAreaNode);
        var iconName = (this.data.workStatus==name) ? "status_"+name+"_red.png" : "status_"+name+".png";
        var icon = "url("+"../x_component_process_Application/$WorkExplorer/"+this.explorer.options.style+"/icon/"+iconName+")"
        node.setStyle("background-image", icon);
    },

    setActions: function(){
        if (this.data.control.allowVisit){
            this.openNode = new Element("div", {"styles": this.css.actionOpenNode, "title": this.explorer.app.lp.open}).inject(this.actionAreaNode);
        }
        if (this.data.control.allowReroute){
            this.processNode = new Element("div", {"styles": this.css.actionProcessNode, "title": this.explorer.app.lp.flowManager}).inject(this.actionAreaNode);
        }
        if (this.data.control.allowReroute){
            this.rerouteNode = new Element("div", {"styles": this.css.actionRerouteNode, "title": this.explorer.app.lp.reroute}).inject(this.actionAreaNode);
        }
        //if (this.explorer.app.options.application.allowControl){
        if (this.data.control.allowDelete){
            this.deleteNode = new Element("div", {"styles": this.css.actionDeleteNode, "title": this.explorer.app.lp.delete}).inject(this.actionAreaNode);
        }
    },
    removeTask: function(task){
        var id = task.data.id;
        this.explorer.actions.removeTask(id, function(json){
            this.tasks.erase(this.taskChildren[id]);
            this.taskChildren[id].destroy();
            MWF.release(this.taskChildren[id]);
            delete this.taskChildren[id];
        }.bind(this));
    },
    removeDone: function(done){
        var id = done.data.id;
        this.explorer.actions.removeDone(id, function(json){
            this.dones.erase(this.doneChildren[id]);
            this.doneChildren[id].destroy();
            MWF.release(this.doneChildren[id]);
            delete this.doneChildren[id];
        }.bind(this));
    },
    removeRead: function(read){
        var id = read.data.id
        this.explorer.actions.removeRead(id, function(json){
            this.reads.erase(this.readChildren[id]);
            this.readChildren[id].destroy();
            MWF.release(this.readChildren[id]);
            delete this.readChildren[id];
        }.bind(this));
    },
    removeReaded: function(readed){
        var id = readed.data.id
        this.explorer.actions.removeReaded(id, function(json){
            this.readeds.erase(this.readedChildren[id]);
            this.readedChildren[id].destroy();
            MWF.release(this.readedChildren[id]);
            delete this.readedChildren[id];
        }.bind(this));
    }
});
MWF.xApplication.process.Application.WorkExplorer.Task = new Class({
    initialize: function (data, container, explorer, work) {
        this.explorer = explorer;
        this.work = work;
        this.data = data;
        this.container = container;
        this.css = this.explorer.css;
        this.load();
    },
    load: function () {
        this.node = new Element("div", {"styles": this.css.taskItemNode}).inject(this.container);

        var taskArea1Node = new Element("div", {"styles": this.css.taskArea1Node}).inject(this.node);
        var taskArea2Node = new Element("div", {"styles": this.css.taskArea2Node}).inject(this.node);
        var taskArea3Node = new Element("div", {"styles": this.css.taskArea3Node}).inject(this.node);

        this.iconAreaNode = new Element("div", {"styles": this.css.taskItemIconAreaNode}).inject(taskArea1Node);
        this.personAreaNode = new Element("div", {"styles": this.css.taskItemPersonAreaNode}).inject(taskArea1Node);
        this.timeAreaNode = new Element("div", {"styles": this.css.taskItemTimeAreaNode}).inject(taskArea2Node);

        this.actionAreaNode = new Element("div", {"styles": this.css.taskItemActionNode}).inject(taskArea3Node);
        this.activityAreaNode = new Element("div", {"styles": this.css.taskItemActivityNode}).inject(taskArea3Node);


        this.setTaskContent();
        this.setActions();

        this.setEvents();
    },
    setTaskContent: function () {
        debugger;
        //var time = new Date().parse(this.data.startTime).format("%Y-%m-%d %H:%M");
        this.personAreaNode.set("text", o2.name.cn(this.data.identity)+"("+o2.name.cn(this.data.unit)+")");
        //new MWF.widget.O2Identity({"name": this.data.identity}, this.personAreaNode, {"style": "task"});

        var time = new Date().parse(this.data.startTime).format("%Y-%m-%d %H:%M");
        this.timeAreaNode.set("text", time);

        this.activityAreaNode.set("text", this.data.activityName);
    },
    setActions: function () {
        //if (this.explorer.app.options.application.allowControl || this.explorer.app.desktop.session.user.name == this.data.person) {
        if ((this.data.control || this.work.data.control).allowReset){
            this.resetNode = new Element("div", {
                "styles": this.css.taskActionResetNode,
                "title": this.explorer.app.lp.reset
            }).inject(this.actionAreaNode);
        }
        //if (this.explorer.app.options.application.allowControl) {
        if ((this.data.control || this.work.data.control).allowProcessing) {
            this.flowNode = new Element("div", {
                "styles": this.css.taskActionFlowNode,
                "title": this.explorer.app.lp.flow
            }).inject(this.actionAreaNode);
        }
        if ((this.data.control || this.work.data.control).allowDelete){
            this.deleteNode = new Element("div", {
                "styles": this.css.taskActionDeleteNode,
                "title": this.explorer.app.lp.delete
            }).inject(this.actionAreaNode);
        }
    },
    setEvents: function () {
        if (this.resetNode) {
            this.resetNode.addEvents({
                "mouseover": function () {
                    this.resetNode.setStyles(this.css.taskActionResetNode_over);
                }.bind(this),
                "mouseout": function () {
                    this.resetNode.setStyles(this.css.taskActionResetNode);
                }.bind(this),
                "mousedown": function () {
                    this.resetNode.setStyles(this.css.taskActionResetNode_down);
                }.bind(this),
                "mouseup": function () {
                    this.resetNode.setStyles(this.css.taskActionResetNode_over);
                }.bind(this),
                "click": function () {
                    this.reset();
                }.bind(this)
            });
        }
        if (this.flowNode) {
            this.flowNode.addEvents({
                "mouseover": function () {
                    this.flowNode.setStyles(this.css.taskActionFlowNode_over);
                }.bind(this),
                "mouseout": function () {
                    this.flowNode.setStyles(this.css.taskActionFlowNode);
                }.bind(this),
                "mousedown": function () {
                    this.flowNode.setStyles(this.css.taskActionFlowNode_down);
                }.bind(this),
                "mouseup": function () {
                    this.flowNode.setStyles(this.css.taskActionFlowNode_over);
                }.bind(this),
                "click": function (e) {
                    this.flow(e);
                }.bind(this)
            });
        }
        if (this.deleteNode) {
            this.deleteNode.addEvents({
                "mouseover": function () {
                    this.deleteNode.setStyles(this.css.taskActionDeleteNode_over);
                }.bind(this),
                "mouseout": function () {
                    this.deleteNode.setStyles(this.css.taskActionDeleteNode);
                }.bind(this),
                "mousedown": function () {
                    this.deleteNode.setStyles(this.css.taskActionDeleteNode_down);
                }.bind(this),
                "mouseup": function () {
                    this.deleteNode.setStyles(this.css.taskActionDeleteNode_over);
                }.bind(this),
                "click": function (e) {
                    this.remove(e);
                }.bind(this)
            });
        }
    },
    reset: function () {
        this.node.setStyles(this.css.taskItemNode_action);
        var options = {
            "type": "identity",
            "title": this.explorer.app.lp.reset,
            "onComplete": function (items) {
                if (items.length) {
                    this.resetPeople(items);
                } else {
                    this.node.setStyles(this.css.taskItemNode);
                }

            }.bind(this),
            "onCancel": function () {
                this.node.setStyles(this.css.taskItemNode);
            }.bind(this)
        };

        var selector = new MWF.O2Selector(this.explorer.app.content, options);
    },
    resetPeople: function (items) {
        var nameList = [];
        var nameShortList = [];
        items.each(function(item){
            nameList.push(item.data.distinguishedName);
            nameShortList.push(item.data.name);
        });
        var data = {
            "routeName": this.explorer.app.lp.reset,
            "opinion": this.explorer.app.lp.reset+": "+nameShortList.join(", "),
            "identityList": nameList
        };
        this.explorer.actions.resetTask(this.data.id, data, function(){
            this.work.childNode.destroy();
            this.work.childNode = null;
            this.work.loadChild();
        }.bind(this));
    },
    flow: function(e){
        this.node.setStyles(this.css.taskItemNode_action);
        this.processNode = new Element("div", {"styles": this.css.taskItemFlowNode}).inject(this.explorer.app.content);
        this.processNode.setStyles({"overflow":"auto"});

        MWF.require("MWF.xDesktop.Dialog", function(){
            var width = 560;
            var height = 400;
            var size = this.explorer.app.content.getSize();
            var x = size.x/2-width/2;
            var y = size.y/2-height/2;

            if (x+parseFloat(width)>size.x){
                x = x-parseFloat(width);
            }
            if (x<0) x = 0;
            if (y+parseFloat(height)>size.y){
                y = y-parseFloat(height);
            }
            if (y<0) y = 0;

            var dlg = new MWF.xDesktop.Dialog({
                "title": "",
                "style": "application",
                "top": y,
                "left": x-20,
                "fromTop":y,
                "fromLeft": x-20,
                "width": width,
                "height": height,
                "content": this.processNode,
                "container": this.explorer.app.content,
                "onPostShow": function(){
                    var _self = this;
                    MWF.xDesktop.requireApp("process.Work", "Processor", function(){
                        new MWF.xApplication.process.Work.Processor(this.processNode, this.data, {
                            "style": "task",
                            "isManagerProcess" : true,
                            "onCancel": function(){
                                dlg.close();
                                _self.node.setStyles(_self.css.taskItemNode);
                                delete this;
                            },
                            "onSubmit": function(routeName, opinion){
                                _self.submitTask(routeName, opinion, this, dlg);
                                delete this;
                            },
                            "onResize": function () {
                                var processNodeSize = this.node.getSize();

                                if (!dlg || !dlg.node) return;
                                dlg.node.setStyle("display", "block");
                                //var size = dlg.node.getSize();
                                //dlg.options.contentHeight = processNodeSize.y;
                                dlg.content.setStyles({
                                    "height": processNodeSize.y,
                                    "width": processNodeSize.x
                                });
                                var s = dlg.setContentSize("auto",null);
                                //alert( JSON.stringify(s) )
                                if (dlg.content.getStyle("overflow-y") === "auto" && dlg.content.getStyle("overflow-x") !== "auto") {
                                    dlg.node.setStyle("width", dlg.node.getStyle("width").toInt() + 20 + "px");
                                    dlg.content.setStyle("width", dlg.content.getStyle("width").toInt() + 20 + "px");
                                }
                                dlg.reCenter();
                            }
                        })
                    }.bind(this));
                }.bind(this)
            });
            dlg.show();
        }.bind(this));
    },
    submitTask: function(routeName, opinion, processer, dlg){
        var data = {
            "routeName": routeName,
            "opinion": opinion
        }
        this.explorer.actions.flowTask(this.data.id, data, function(){
            processer.destroy();
            dlg.close();

            this.work.childNode.destroy();
            this.work.childNode = null;
            this.work.reload(function(){
                this.work.loadChild();
            }.bind(this));

        }.bind(this));
    },
    remove: function(e){
        var lp = this.explorer.app.lp;
        var text = lp.deleteTask.replace(/{people}/g, this.data.person);
        var _self = this;
        this.node.setStyles(this.css.taskItemNode_remove);

        this.explorer.app.confirm("warn", e, lp.deleteTaskTitle, text, 350, 120, function(){
            this.close();
            _self.work.removeTask(_self);
        }, function(){
            _self.node.setStyles(_self.css.taskItemNode);
            this.close();
        });
    },
    destroy: function(){
        this.node.destroy();
    }
});

MWF.xApplication.process.Application.WorkExplorer.Done = new Class({
    Extends: MWF.xApplication.process.Application.WorkExplorer.Task,
    setActions: function(){
        //if (this.explorer.app.options.application.allowControl){
        if ((this.data.control || this.work.data.control).allowDelete){
            this.deleteNode = new Element("div", {"styles": this.css.taskActionDeleteNode, "title": this.explorer.app.lp.delete}).inject(this.actionAreaNode);
        }
    },
    setEvents: function(){
        if (this.deleteNode){
            this.deleteNode.addEvents({
                "mouseover": function(){this.deleteNode.setStyles(this.css.taskActionDeleteNode_over);}.bind(this),
                "mouseout": function(){this.deleteNode.setStyles(this.css.taskActionDeleteNode);}.bind(this),
                "mousedown": function(){this.deleteNode.setStyles(this.css.taskActionDeleteNode_down);}.bind(this),
                "mouseup": function(){this.deleteNode.setStyles(this.css.taskActionDeleteNode_over);}.bind(this),
                "click": function(e){
                    this.remove(e);
                }.bind(this)
            });
        }
    },
    remove: function(e){
        var lp = this.explorer.app.lp;
        var text = lp.deleteDone.replace(/{people}/g, this.data.person);
        var _self = this;
        this.node.setStyles(this.css.taskItemNode_remove);

        this.explorer.app.confirm("warn", e, lp.deleteDoneTitle, text, 350, 120, function(){
            this.close();
            _self.work.removeDone(_self);
        }, function(){
            _self.node.setStyles(_self.css.taskItemNode);
            this.close();
        });
    }
});

MWF.xApplication.process.Application.WorkExplorer.Read = new Class({
    Extends: MWF.xApplication.process.Application.WorkExplorer.Task,
    setActions: function(){
        //if (this.explorer.app.options.application.allowControl || this.explorer.app.desktop.session.user.name==this.data.person){
        if ((this.data.control || this.work.data.control).allowReadReset){
            this.resetNode = new Element("div", {"styles": this.css.taskActionResetNode, "title": this.explorer.app.lp.reset}).inject(this.actionAreaNode);
        }
        if ((this.data.control || this.work.data.control).allowRead){
            this.flagNode = new Element("div", {"styles": this.css.readActionFlagNode, "title": this.explorer.app.lp.flag}).inject(this.actionAreaNode);
        }
        //if (this.explorer.app.options.application.allowControl){
        if ( (this.data.control || this.work.data.control).allowDelete){
            this.deleteNode = new Element("div", {"styles": this.css.taskActionDeleteNode, "title": this.explorer.app.lp.delete}).inject(this.actionAreaNode);
        }
    },
    setEvents: function(){
        if (this.resetNode){
            this.resetNode.addEvents({
                "mouseover": function(){this.resetNode.setStyles(this.css.taskActionResetNode_over);}.bind(this),
                "mouseout": function(){this.resetNode.setStyles(this.css.taskActionResetNode);}.bind(this),
                "mousedown": function(){this.resetNode.setStyles(this.css.taskActionResetNode_down);}.bind(this),
                "mouseup": function(){this.resetNode.setStyles(this.css.taskActionResetNode_over);}.bind(this),
                "click": function(){
                    this.reset();
                }.bind(this)
            });
        }
        if (this.flagNode){
            this.flagNode.addEvents({
                "mouseover": function(){this.flagNode.setStyles(this.css.readActionFlagNode_over);}.bind(this),
                "mouseout": function(){this.flagNode.setStyles(this.css.readActionFlagNode);}.bind(this),
                "mousedown": function(){this.flagNode.setStyles(this.css.readActionFlagNode_down);}.bind(this),
                "mouseup": function(){this.flagNode.setStyles(this.css.readActionFlagNode_over);}.bind(this),
                "click": function(){
                    this.flagRead();
                }.bind(this)
            });
        }
        if (this.deleteNode){
            this.deleteNode.addEvents({
                "mouseover": function(){this.deleteNode.setStyles(this.css.taskActionDeleteNode_over);}.bind(this),
                "mouseout": function(){this.deleteNode.setStyles(this.css.taskActionDeleteNode);}.bind(this),
                "mousedown": function(){this.deleteNode.setStyles(this.css.taskActionDeleteNode_down);}.bind(this),
                "mouseup": function(){this.deleteNode.setStyles(this.css.taskActionDeleteNode_over);}.bind(this),
                "click": function(e){
                    this.remove(e);
                }.bind(this)
            });
        }
    },
    reset: function () {
        this.node.setStyles(this.css.taskItemNode_action);
        var options = {
            "type": "identity",
            "title": this.explorer.app.lp.reset,
            "onComplete": function (items) {
                if (items.length) {
                    this.resetPeople(items);
                } else {
                    this.node.setStyles(this.css.taskItemNode);
                }

            }.bind(this),
            "onCancel": function () {
                this.node.setStyles(this.css.taskItemNode);
            }.bind(this)
        };

        var selector = new MWF.O2Selector(this.explorer.app.content, options);
    },
    resetPeople: function (items) {
        var nameList = [];
        items.each(function(item){
            nameList.push(item.data.distinguishedName);
        });
        var data = {
            "routeName": this.explorer.app.lp.resetTo,
            "opinion": this.explorer.app.lp.resetTo+": "+nameList.join(", "),
            "identityList": nameList
        };
        this.explorer.actions.resetRead(this.data.id, data, function(){
            this.work.childNode.destroy();
            this.work.childNode = null;
            this.work.loadChild();
        }.bind(this));
    },
    remove: function(e){
        var lp = this.explorer.app.lp;
        var text = lp.deleteRead.replace(/{people}/g, this.data.person);
        var _self = this;
        this.node.setStyles(this.css.taskItemNode_remove);

        this.explorer.app.confirm("warn", e, lp.deleteReadTitle, text, 350, 120, function(){
            this.close();
            _self.work.removeRead(_self);
        }, function(){
            _self.node.setStyles(_self.css.taskItemNode);
            this.close();
        });
    },
    flagRead: function(){
        var lp = this.explorer.app.lp;
        var text = lp.flagRead.replace(/{people}/g, this.data.person);
        var _self = this;
        this.node.setStyles(this.css.taskItemNode_action);
        this.explorer.app.confirm("warn", e, lp.flagReadTitle, text, 350, 180, function(){
            this.close();
            _self.doFlagRead();
        }, function(){
            _self.node.setStyles(_self.css.taskItemNode);
            this.close();
        });
    },
    doFlagRead: function(){
        this.explorer.actions.flagRead(this.data.id, {}, function(){
            this.work.childNode.destroy();
            this.work.childNode = null;
            this.work.loadChild();
        }.bind(this));
    }
});

MWF.xApplication.process.Application.WorkExplorer.Readed = new Class({
    Extends: MWF.xApplication.process.Application.WorkExplorer.Done,
    remove: function(e){
        var lp = this.explorer.app.lp;
        var text = lp.deleteReaded.replace(/{people}/g, this.data.person);
        var _self = this;
        this.node.setStyles(this.css.taskItemNode_remove);

        this.explorer.app.confirm("warn", e, lp.deleteReadedTitle, text, 350, 120, function(){
            this.close();
            _self.work.removeReaded(_self);
        }, function(){
            _self.node.setStyles(_self.css.taskItemNode);
            this.close();
        });
    }
});
