MWF.xApplication.process.TaskCenter.List = new Class({
    initialize: function(container, app, filterData){
        this.container = $(container);
        this.app = app;
        this.css = this.app.css;
        this.currentPageData = [];
        this.nextPageData = [];
        this.prevPageData = [];

        this.initData();
        this.filterData = null;
        if (filterData) this.filterData = filterData;

        this.load();
    },
    initData: function(){
        this.count = 0;
        this.isCountShow = false;
        this.currentPage = 1;
        this.pageCount = 20;
        this.pages = 0;

        this.items = [];
        this.isItemsLoaded = false;
        this.isItemLoadding = false;
        this.loadItemQueue = 0;

        this.filterApplication = "";
        this.currentFilterNode = null;
        this.filterListNode = null;
    },
    load: function(){
        this.mask = new MWF.widget.Mask({"style": "desktop"});
        this.mask.loadNode(this.app.content);

        this.contentNode = new Element("div", {"styles": this.css.listContentNode}).inject(this.container);

        this.createActionBarNode();
        this.createListAreaNode();

        this.resetListAreaHeight();
        this.app.addEvent("resize", this.resetListAreaHeight.bind(this));

        //this.setAppContentSize();

        //    this.listItemNext();
    },
    refresh: function(){
        this.mask = new MWF.widget.Mask({"style": "desktop"});
        this.mask.loadNode(this.app.content);
        this.initData();
        this.filterData = null;
        this.applicationFilterAreaNode.empty();
        this.createAppFilterNodes();
        this.listAreaNode.empty();
        this.resetListAreaHeight();
        this.app.getWorkCounts();
    },
    refilter: function(){
        this.mask = new MWF.widget.Mask({"style": "desktop"});
        this.mask.loadNode(this.app.content);
        this.initData();
        this.applicationFilterAreaNode.empty();
        this.createAppFilterNodes();
        this.listAreaNode.empty();
        this.resetListAreaHeight();
    },
    listItemNext: function(count){
        if (!this.isItemsLoaded){
            if (!this.isItemLoadding){
                this.isItemLoadding = true;
                this._getCurrentPageData(function(json){
                    this.count = json.count;
                    if (!this.isCountShow){
                        this.currentFilterNode.getFirst("span").set("text", "("+this.count+")");
                        this.isCountShow = true;
                    }
                    if (json.count<=this.items.length){
                        this.isItemsLoaded = true;
                    }
                    json.data.each(function(task){
                        this.items.push(this._createItem(task));
                    }.bind(this));

                    this.isItemLoadding = false;

                    if (this.loadItemQueue>0){
                        this.loadItemQueue--;
                        this.listItemNext();
                    }

                    this.mask.hide();
                }.bind(this), count);
            }else{
                this.loadItemQueue++;
            }
        }
    },
    createActionBarNode: function(){
        this.actionBarNode = new Element("div", {"styles": this.css.actionBarNode}).inject(this.contentNode);

        this.isFilterOpen = false;
        this.filterActionNode = new Element("div", {
            "styles": this.css.filterActionNode,
            "text": this.app.lp.filter
        }).inject(this.actionBarNode);
        this.filterActionNode.addEvents({
            "click": function (e){this.showOrHideFilter();e.stopPropagation();}.bind(this)
        });

        this.applicationFilterAreaNode = new Element("div", {"styles": this.css.applicationFilterAreaNode}).inject(this.actionBarNode);

        this.createAppFilterNodes();
    },
    showOrHideFilter: function(){
        if (!this.isFilterOpen){
            if (!this.filterAreaMorph || !this.filterAreaMorph.isRunning()) this.showFilter();
        }else{
            if (this.filterAreaMorph || !this.filterAreaMorph.isRunning()) this.hideFilter();
        }
    },
    showFilter: function(){
        this.filterActionNode.setStyles(this.css.filterActionNode_check);
        if (!this.filterAreaNode) this.createFilterAreaNode();

        this.filterAreaTipNode.setStyle("display", "block");
        this.filterAreaNode.setStyle("display", "block");
        this.resizeFilterAreaNode();
        var size = this.app.content.getSize();
        var toStyle = {
            "width": (!layout.mobile) ? "460px" : ""+size.x+"px",
            "height": (!layout.mobile) ? "500px" : ""+size.y+"px"
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
            }.bind(this));

            $(document.body).removeEvent("click", this.hideFilterFun);
        }
    },


    createFilterAreaContent: function(){
        var contentListNode = new Element("div", {"styles": this.css.applicationFilterAreaContentListNode}).inject(this.filterAreaNode);
        var contentScrollNode = new Element("div", {"styles": this.css.applicationFilterAreaContentScrollNode}).inject(contentListNode);
        var contentNode = new Element("div", {"styles": {"overflow": "hidden", "padding-bottom": "20px"}}).inject(contentScrollNode);

        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(contentScrollNode, {
                "style":"default", "distance": 30, "friction": 4,	"axis": {"x": false, "y": true}
            });
        }.bind(this));

        var _self = this;
        this.app.getAction(function(){
            this._getFilterCount(function(json){
                var obj = json.data;
                Object.each(obj, function(v, key){
                    var categoryNode = new Element("div", {"styles": this.css.applicationFilterCategoryNode}).inject(contentNode);
                    categoryNode.set("text", this.app.lp[key]);
                    var itemAreaNode = new Element("div", {"styles": this.css.applicationFilterItemAreaNode}).inject(contentNode);

                    //             for (var x=0; x<10; x++){
                    v.each(function(item){
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
                        if (this.filterData){
                            if (this.filterData[key]){
                                if (item.value === this.filterData[key].value){
                                    this.selectedFilterItem(itemNode);
                                }
                            }
                        }


                    }.bind(this));
                    //           }


                }.bind(this));
            }.bind(this));
        }.bind(this));
    },
    _getFilterCount: function(callback){
        this.app.action.listTaskCompletedFilterCount(function(json){
            if (callback) callback(json);
        });
    },
    unSelectedFilterItem: function(item){
        if (item.hasClass("applicationFilterItemNode_over")){
            // var value = item.retrieve("value");
            // var name = item.retrieve("textname");
            var key = item.retrieve("key");

            item.setStyles(this.css.applicationFilterItemNode);
            item.removeClass("applicationFilterItemNode_over");
            item.addClass("applicationFilterItemNode");

            if (!this.filterData) this.filterData = {};
            this.filterData[key] = null;
            delete this.filterData[key];

            item.getParent().eliminate("current");
        }
    },
    selectedFilterItem: function(item){
        if (!item.hasClass("applicationFilterItemNode_over")){
            var current = item.getParent().retrieve("current");
            if (current) this.unSelectedFilterItem(current);

            var value = item.retrieve("value");
            var key = item.retrieve("key");
            var name = item.retrieve("textname");

            item.setStyles(this.css.applicationFilterItemNode_over);
            item.removeClass("applicationFilterItemNode");
            item.addClass("applicationFilterItemNode_over");

            if (!this.filterData) this.filterData = {};
            this.filterData[key] = {"value": value, "name": name};

            item.getParent().store("current", item);
        }
    },

    createFilterAreaTitle: function(){
        var titleNode = new Element("div", {"styles": this.app.css.filterAreaTitleNode}).inject(this.filterAreaNode);
        var okNode = new Element("div", {"styles": this.app.css.filterAreaTitleActionOkNode, "text": this.app.lp.ok}).inject(titleNode);
        var clearNode = new Element("div", {"styles": this.app.css.filterAreaTitleActionClearNode, "text": this.app.lp.clear}).inject(titleNode);
        clearNode.addEvent("click", function(){
            this.filterAreaNode.getElements(".filterItem").each(function(el){
                this.unSelectedFilterItem(el);
            }.bind(this));
            var input = this.filterAreaNode.retrieve("input");
            input.set("value", "");
            this.filterData = null;

            this.hideFilter();
            this.refilter();
        }.bind(this));
        okNode.addEvent("click", function(){
            var input = this.filterAreaNode.retrieve("input");
            if (!this.filterData) this.filterData = {};
            var key = input.get("value");
            if (key && key!==this.app.lp.searchKey){
                this.filterData.key = key;
            }else{
                this.filterData.key = "";
                delete this.filterData.key
            }

            this.hideFilter();
            this.refilter();
        }.bind(this));

        var searchNode = new Element("div", {"styles": this.app.css.filterAreaTitleSearchNode}).inject(titleNode);
        var searchIconNode = new Element("div", {"styles": this.app.css.filterAreaTitleSearchIconNode}).inject(searchNode);
        var searchInputAreaNode = new Element("div", {"styles": this.app.css.filterAreaTitleSearchInputAreaNode}).inject(searchNode);
        var searchInputNode = new Element("input", {"styles": this.app.css.filterAreaTitleSearchInputNode, "value": this.app.lp.searchKey}).inject(searchInputAreaNode);
        if (this.filterData){
            if (this.filterData.key) searchInputNode.set("value", this.filterData.key);
        }
        this.filterAreaNode.store("input", searchInputNode);

        var key = this.app.lp.searchKey;
        searchInputNode.addEvents({
            "blur": function(){if (!this.get("value")) this.set("value", key)},
            "focus": function(){if (this.get("value")===key) this.set("value", "")}
        });
    },

    createFilterAreaNode: function(){
        this.filterAreaNode = new Element("div", {"styles": this.app.css.filterAreaNode}).inject(this.container);
        this.filterAreaNode.addEvent("click", function(e){e.stopPropagation();});

        this.filterAreaTipNode = new Element("div", {"styles": this.app.css.filterAreaTipNode}).inject(this.container);
        //var size = this.filterActionNode.getSize();
        this.filterAreaNode.setStyles({
            "width": (!layout.mobile) ? "460px" : ""+this.app.content.getSize().x+"px",
            "height": "0px"
        });
        this.filterAreaNode.position({
            relativeTo: this.filterActionNode,
            position: 'bottomRight',
            edge: 'upperRight',
            offset: {x:10, y: 7}
        });
        this.filterAreaTipNode.position({
            relativeTo: this.filterAreaNode,
            position: 'topRight',
            edge: 'bottomRight',
            offset: {x:-26, y: 1}
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
                relativeTo: this.filterActionNode,
                position: 'bottomRight',
                edge: 'upperRight',
                offset: {x:10, y: 7}
            });
            if (this.filterAreaTipNode){
                this.filterAreaTipNode.position({
                    relativeTo: this.filterAreaNode,
                    position: 'topRight',
                    edge: 'bottomRight',
                    offset: {x:-26, y: 1}
                });
            }
        }
    },

    createAppFilterNodes: function(){
        var allApp = {"name": this.app.lp.all, "application": "", "count": 0};
        this.allAppFilterNode = this.createAppFilterNode(allApp, "appFilterNode_current");
        this.currentFilterNode = this.allAppFilterNode;

        this._getApplicationCount(function(json){
            json.data.each(function(app){
                this.createAppFilterNode(app);
            }.bind(this));
        }.bind(this));
    },

    createAppFilterNode: function(app, style){
        style = style || "appFilterNode";
        var node = new Element("div", {"styles": this.app.css[style]}).inject(this.applicationFilterAreaNode);
        var text = (app.count) ? app.name+"<span>("+app.count+")</span>" : app.name+"<span></span>";
        node.set({"html": text, "id": app.value});
        var _self = this;
        node.addEvent("click", function(e){
            _self.filterByApplication(this);
        });
        return node;
    },
    filterByApplication: function(node){
        var id = node.get("id");
        if (!id){
            this.refresh();
        }else{
            if (this.currentFilterNode) this.currentFilterNode.setStyles(this.app.css.appFilterNode);

            this.initData();
            this.filterApplication = id;
            this.listAreaNode.empty();
            this.resetListAreaHeight();

            this.currentFilterNode = node;
            this.currentFilterNode.setStyles(this.app.css.appFilterNode_current);
        }
    },

    createListAreaNode: function(){
        this.listScrollAreaNode = new Element("div", {"styles": this.css.listScrollAreaNode}).inject(this.contentNode);
        this.listAreaNode = new Element("div", {"styles": this.css.listAreaNode}).inject(this.listScrollAreaNode);
        var _self = this;
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.listScrollAreaNode, {
                "style":"xApp_TaskList", "where": "before", "distance": 30, "friction": 4,	"axis": {"x": false, "y": true},
                "onScroll": function(y, x){
                    //new Element("div", {"text": "sss-"+y}).inject(_self.listScrollAreaNode, "before");
                    // _self.app.taskTitleTextNode.set("text", "start");
                    // _self.app.taskTitleTextNode.set("text", y);
                    // if (!detail){
                    var scrollSize = _self.listScrollAreaNode.getScrollSize();
                    var clientSize = _self.listScrollAreaNode.getSize();
                    var scrollHeight = scrollSize.y-clientSize.y;
                    if (y+200>scrollHeight) {
                        if (!_self.isElementLoaded) _self.listItemNext();
                    }
                    // }else{
                    //     _self.app.taskTitleTextNode.set("text", y);
                    // }
                }
            });
        }.bind(this));
    },
    // setAppContentSize: function(){
    //     var size = this.app.contentNode.getSize();
    //     var x = this.container.getSize().x+size.x;
    //     this.container.setStyle("width", ""+x+"px");
    // },
    resetListAreaHeight: function(){
        if (layout.mobile){
            this.resetListAreaHeight_mobile();
        }else{
            this.resetListAreaHeight_pc();
        }
    },
    resetListAreaHeight_mobile: function(){
        var contentSize = this.app.contentNode.getSize();
        this.contentNode.setStyle("width", ""+contentSize.x+"px");

        var size = this.contentNode.getSize();
        var barSize = this.actionBarNode.getSize();
        var y = size.y - barSize.y;

        this.listScrollAreaNode.setStyle("height", ""+y+"px");
        if (this.listAreaNode){
            // var count = (size.x/402).toInt();
            // var x = 402 * count;
            // var m = (size.x-x)/2;
            // this.listAreaNode.setStyles({
            //     "width": ""+x+"px",
            //     "margin-left": ""+m+"px"
            // });
            //
            // if (this.actionBarNode) this.actionBarNode.setStyles({
            //     "width": ""+x+"px",
            //     "margin-left": ""+m+"px"
            // });

            // var hCount = (y/102).toInt()+1;
            // this.pageCount = count*hCount;
            this.pageCount = 50;
            if (this.items.length<this.pageCount){
                this.listItemNext(this.pageCount-this.items.length);
            }
            //this.listAreaNode
        }
    },
    resetListAreaHeight_pc: function(){
        var contentSize = this.app.contentNode.getSize();
        this.contentNode.setStyle("width", ""+contentSize.x+"px");

        var size = this.contentNode.getSize();
        var barSize = this.actionBarNode.getSize();
        var y = size.y - barSize.y;

        this.listScrollAreaNode.setStyle("height", ""+y+"px");

        if (this.listAreaNode){
            var count = (size.x/350).toInt();
            var x = 350 * count;
            var m = (size.x-x)/2;
            this.listAreaNode.setStyles({
                "width": ""+x+"px",
                "margin-left": ""+m+"px"
            });

            if (this.actionBarNode) this.actionBarNode.setStyles({
                "width": ""+x+"px",
                "margin-left": ""+m+"px"
            });

            var hCount = (y/182).toInt()+1;
            this.pageCount = count*hCount;

            if (this.items.length<this.pageCount){
                this.listItemNext(this.pageCount-this.items.length);
            }
            //this.listAreaNode
        }

    },

    show: function(){
        //if (!this.app.contentScroll){
        //    this.app.contentScroll = new Fx.Scroll(this.app.contentNode, {"wheelStops": false});
        //}
        //this.app.contentScroll.toElement(this.contentNode, "x");

        if (this.app.currentList) this.app.currentList.hide();
        this.app.currentList = this;
        this.contentNode.setStyle("display", "block");
        //this.refresh();
    },
    hide: function(){
        this.contentNode.setStyle("display", "none");
    },

    _getCurrentPageData: function(callback, count){
        this.app.getAction(function(){
            var id;
            if (this.filterApplication){
                id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
                this.app.action.listTaskNextByApp(function(json){
                    if (callback) callback(json);
                }, null, id, count || this.pageCount, this.filterApplication);
            }else{
                id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
                this.app.action.listTaskNext(function(json){
                    if (callback) callback(json);
                }, null, id, count || this.pageCount);
            }
        }.bind(this));
    },
    _getApplicationCount: function(callback){
        this.app.getAction(function(){
            this.app.action.listTaskApplication(function(json){
                if (callback) callback(json);
            }.bind(this));
        }.bind(this));
    },
    _createItem: function(task){
        return new MWF.xApplication.process.TaskCenter.TaskList.Item(task, this)
    },

    showDatch: function(){
        if (!this.datchActionNode){
            this.datchActionNode = new Element("div", {"styles": this.css.datchActionNode, "text": this.app.lp.datch}).inject(this.app.content);
            this.datchActionNode.position({
                relativeTo: this.actionBarNode,
                position: "centerbottom",
                edge: "centerbottom"
            });
            this.datchActionNode.addEvent("click", function(e){
                this.datchProcess();
            }.bind(this));
        }
    },
    hideDatch: function(){
        if (this.datchActionNode){
            this.datchActionNode.destroy();
            this.datchActionNode = null;
        }
    },
    datchProcess: function(){
        var width = "580";
        var height = "260";
        size = this.app.content.getSize();
        var x = (size.x-width)/2;
        var y = (size.y-height)/2;
        if (x<0) x = 0;
        if (y<0) y = 0;

        var _self = this;
        MWF.require("MWF.xDesktop.Dialog", function() {
            var dlg = new MWF.xDesktop.Dialog({
                "title": this.app.lp.datch,
                "style": "datch",
                "top": y - 20,
                "left": x,
                "fromTop": y - 20,
                "fromLeft": x,
                "width": width,
                "height": height,
                "html": "",
                "maskNode": this.app.content,
                "container": this.app.content,
                // "buttonList": [
                //     {
                //         "text": MWF.LP.process.button.ok,
                //         "action": function(){
                //             //if (callback) callback(_self.view.selectedItems);
                //             if (callback) callback(_self.view.getData());
                //             this.close();
                //         }
                //     },
                //     {
                //         "text": MWF.LP.process.button.cancel,
                //         "action": function(){this.close();}
                //     }
                // ]
            });
            dlg.show();

            MWF.xDesktop.requireApp("process.Work", "Processor", function(){
                new MWF.xApplication.process.Work.Processor(dlg.content, this.selectedTask[0].data, {
                    "style": "task",
                    "onCancel": function(){
                        dlg.close();
                        delete this;
                    },
                    "onSubmit": function(routeName, opinion, medias){
                        _self.submitDatchTask(routeName, opinion, medias, this);
                        dlg.close();
                        delete this;
                    }
                })
            }.bind(this));

        }.bind(this));
    },
    submitDatchTask: function(routeName, opinion, medias, processor){
        this.hideDatch();
        this.selectedTask.each(function(item){
            item.submitTask(routeName, opinion, medias, processor, true);
        }.bind(this));
        this.selectedTask = [];
    }

});

MWF.xApplication.process.TaskCenter.List.Item = new Class({
    initialize: function(data, list){
        this.data = data;
        this.list = list;
        this.container = this.list.listAreaNode;

        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.list.css.itemNode}).inject(this.container);
        if (layout.mobile){
            this.node.setStyles(this.list.css.itemNode_mobile);
        }
        this.mainContentNode = new Element("div", {"styles": this.list.css.itemMainContentAreaNode}).inject(this.node);
        this.timeContentNode = new Element("div", {"styles": this.list.css.itemTimeContentAreaNode}).inject(this.node);

        this.leftContentNode = new Element("div", {"styles": this.list.css.itemMainLeftContentAreaNode}).inject(this.mainContentNode);
        this.applicationIconAreaNode = new Element("div", {"styles": this.list.css.itemApplicationIconAreaNode, "title": this.data.applicationName}).inject(this.leftContentNode);
        this.timeIconNode = new Element("div", {"styles": this.list.css.itemTimeIconNode}).inject(this.leftContentNode);
        this.applicationIconNode = new Element("div", {"styles": this.list.css.itemApplicationIconNode}).inject(this.applicationIconAreaNode);

        this.actionContentNode = new Element("div", {"styles": this.list.css.itemMainActionContentAreaNode}).inject(this.mainContentNode);

        this.rightContentNode = new Element("div", {"styles": this.list.css.itemMainRightContentAreaNode}).inject(this.mainContentNode);
        this.applicationTitleNode = new Element("div", {"styles": this.list.css.itemApplicationTitleNode}).inject(this.rightContentNode);
        this.titleNode = new Element("div", {"styles": this.list.css.itemTitleNode}).inject(this.rightContentNode);
        this.activityNode = new Element("div", {"styles": this.list.css.itemActivityNode}).inject(this.rightContentNode);


        // this.contentNode = new Element("div", {"styles": this.list.css.itemContentNode}).inject(this.rightContentNode);
        // this.inforNode = new Element("div", {"styles": this.list.css.itemInforNode}).inject(this.contentNode);
        this.newIconNode = new Element("div", {"styles": this.list.css.itemNewIconNode}).inject(this.node);

        this.getApplicationIcon(function(icon){
            var pic = "/x_component_process_ApplicationExplorer/$Main/default/icon/application.png";
            if (icon.icon){
                pic = "data:image/png;base64,"+icon.icon;
            }
            this.applicationIconNode.makeLnk({
                "par": this._getLnkPar(pic)
            });
        }.bind(this));

        this.setContent();
        this.setNewIcon();
        this.setEvent();
        this.setTimeIcon();

        this.node.fade("in");
    },
    _getLnkPar: function(icon){
        return {
            "icon": icon,
            "title": "["+this.data.processName+"]"+(this.data.title || this.list.app.lp.unnamed),
            "par": (this.data.workCompleted) ? "process.Work#{\"workCompletedId\":\""+this.data.workCompleted+"\"}" : "process.Work#{\"workId\":\""+this.data.work+"\"}"
        };
    },
    setTimeIcon: function(){
        //this.data.expireTime = "2017-08-31 19:00";
        if (this.data.completed){
            this.newIconNode.setStyle("background-image", "url("+"/x_component_process_TaskCenter/$Main/default/time/pic_ok.png)");
            return true;
        }
        this.timeIconNode.empty();
        if (this.data.expireTime){
            var d1 = Date.parse(this.data.expireTime);
            var d2 = Date.parse(this.data.createTime);
            var now = new Date();
            var time1 = d2.diff(now, "second");
            var time2 = now.diff(d1, "second");
            var time3 = d2.diff(d1, "second");
            var n = time1/time3;

            var img = "";
            var text = this.list.app.lp.expire1;
            text = text.replace(/{time}/g, this.data.expireTime);
            if (n<0.5){
                img = "1.png";
            }else if (n<0.75){
                img = "2.png";
            }else if (n<1){
                text = this.list.app.lp.expire2.replace(/{time}/g, this.data.expireTime);
                img = "3.png";
                this.newIconNode.setStyle("background-image", "url("+"/x_component_process_TaskCenter/$Main/default/time/pic_jichao.png)");
            }else if (n<2){
                text = this.list.app.lp.expire3.replace(/{time}/g, this.data.expireTime);
                img = "4.png";
                this.newIconNode.setStyle("background-image", "url("+"/x_component_process_TaskCenter/$Main/default/time/pic_yichao.png)");
            }else{
                text = this.list.app.lp.expire3.replace(/{time}/g, this.data.expireTime);
                img = "5.png";
                this.newIconNode.setStyle("background-image", "url("+"/x_component_process_TaskCenter/$Main/default/time/pic_yanchao.png)");
            }
            this.timeIconNode.setStyle("background-image", "url("+this.list.app.path+this.list.app.options.style+"/time/"+img+")");
            this.timeIconNode.set("title", text);
        }
    },

    setEvent: function(){
        this.node.addEvents({
            "mouseover": function(){this.showAction();}.bind(this),
            "mouseout": function(){this.hideAction();}.bind(this)
        });

        if (this.editNode){
            this.editNode.addEvent("click", function(e){
                this.editTask();
            }.bind(this));
        }
        if (this.closeNode){
            this.closeNode.addEvent("click", function(e){
                this.closeEditTask();
            }.bind(this));
        }

        if (this.rightContentNode){
            this.rightContentNode.addEvent("click", function(e){
                this.openTask(e);
            }.bind(this));
        }
        if (this.applicationIconNode){
            this.applicationIconNode.addEvent("click", function(e){
                this.selectTask(e);
            }.bind(this));
        }
    },
    selectTask: function(){
        if (!this.list.selectedTask) this.list.selectedTask = [];
        if (!this.selectedStatus){
            this.list.selectedTask.push(this);
            this.mainContentNodeBorder = this.mainContentNode.getStyle("border");
            this.mainContentNode.setStyles(this.list.css.itemMainContentAreaNode_selected);
            this.selectedStatus = true;
        }else{
            this.list.selectedTask.erase(this);
            this.mainContentNode.setStyles(this.list.css.itemMainContentAreaNode);
            this.mainContentNode.setStyle("border", this.mainContentNodeBorder);
            this.selectedStatus = false;
        }
        if (this.checkProcess()){
            this.list.showDatch();
        }else{
            this.list.hideDatch();
        }
    },
    checkProcess: function(){
        var flag = true;
        var activity = "";
        if (this.list.selectedTask.length){
            for (var i=0; i<this.list.selectedTask.length; i++){
                var item = this.list.selectedTask[i];
                if (!item.data.allowRapid) return false;
                if (!activity){
                    activity = item.data.activity;
                }else{
                    if ((activity != item.data.activity) || (!item.data.allowRapid)) return false;
                }
            }
        }else{
            return false;
        }
        return true;
    },


    showAction: function(){
        // if (this.editNode) this.editNode.fade("in");
        // if (this.closeNode) this.closeNode.fade("in");
    },
    hideAction: function(){
        // if (this.editNode) this.editNode.fade("out");
        // if (this.closeNode) this.closeNode.fade("out");
    },

    openTask: function(e){
        //     this._getJobByTask(function(data){
        var options = {"workId": this.data.work, "appId": "process.Work"+this.data.work};
        this.list.app.desktop.openApplication(e, "process.Work", options);
        //     }.bind(this));
    },

    closeEditTask: function(callback){
        this.closeNode.setStyle("display", "none");

        this.flowInforLeftNode.destroy();
        this.flowInforRightNode.destroy();
        this.flowInforContentNode.destroy();
        this.flowInforScrollNode.destroy();

        this.flowInforNode.destroy();
        this.processNode.destroy();

        this.flowInforScrollFx = null;
        this.flowInforLeftNode = null;
        this.flowInforRightNode = null;
        this.flowInforScrollNode = null;
        this.flowInforContentNode = null;

        delete this.flowInforScrollFx;
        delete this.flowInforLeftNode;
        delete this.flowInforRightNode;
        delete this.flowInforScrollNode;
        delete this.flowInforContentNode;
        delete this.flowInforNode;
        delete this.processNode;

        var p = this.node.getPosition(this.list.app.content);
        this.list.css.itemNode_edit_from.top = ""+ p.y+"px";
        this.list.css.itemNode_edit_from.left = ""+ p.x+"px";

        var morph = new Fx.Morph(this.mainContentNode, {
            "duration": 200,
            "transition": Fx.Transitions.Expo.easeIn,
            "onComplete": function(){
                this.nodeClone.destroy();
                this.nodeClone = null;
                this.list.app.content.unmask();
                document.body.setStyle("-webkit-overflow-scrolling", "touch");
                this.mainContentNode.setStyles(this.list.css.itemMainContentAreaNode);
                this.mainContentNode.setStyle("opacity", 1);
                this.list.app.removeEvent("resize", this.resizeEditNodeFun);

                this.editNode.setStyle("display", "block");
                if (callback) callback();
            }.bind(this)
        });
        morph.start(this.list.css.itemNode_edit_from);
    },
    editTask: function(){
        if (layout.mobile){
            this.editTask_mobile();
        }else{
            this.editTask_pc();
        }
    },
    editTask_mobile: function(){
        if (!this.nodeClone){
            this._getJobByTask(function(data){
                this.nodeClone = this.mainContentNode.clone(false);
                this.nodeClone.inject(this.mainContentNode, "after");
                this.mainContentNode.setStyles(this.list.css.itemNode_edit_from_mobile);
                this.mainContentNode.position({
                    relativeTo: this.nodeClone,
                    position: "topleft",
                    edge: "topleft"
                });

                this.showEditNode(data);
            }.bind(this));
        }
    },
    editTask_pc: function(){
        this.list.app.content.mask({
            "destroyOnHide": true,
            "id": "mask_"+this.data.id,
            "style": this.list.css.maskNode
        });

        this._getJobByTask(function(data){
            this.nodeClone = this.mainContentNode.clone(false);
            this.nodeClone.inject(this.mainContentNode, "after");
            this.mainContentNode.setStyles(this.list.css.itemNode_edit_from);
            this.mainContentNode.position({
                relativeTo: this.nodeClone,
                position: "topleft",
                edge: "topleft"
            });


            // this.nodeClone = this.node.clone(false);
            // this.nodeClone.inject(this.node, "after");
            // this.node.setStyles(this.list.css.itemNode_edit_from);
            // this.node.position({
            //     relativeTo: this.nodeClone,
            //     position: "topleft",
            //     edge: "topleft"
            // });

            this.showEditNode(data);
        }.bind(this));
    },
    setEditTaskNodes: function(data){
        this.flowInforNode = new Element("div", {"styles": this.list.css.flowInforNode}).inject(this.mainContentNode);
        this.processNode = new Element("div", {"styles": this.list.css.processNode}).inject(this.mainContentNode);
        this.setFlowInfor(data);
        this.setProcessor();
    },
    setFlowChart: function(data){
        var idx = 0;
        data.workLogList.each(function(worklog){
            if (!worklog.taskCompletedList) worklog.taskCompletedList = [];
            if (!worklog.taskList) worklog.taskList = [];
            if (worklog.taskCompletedList.length || worklog.taskList.length){
                this.createFlowInforWorklogNode(worklog.fromActivityName, worklog.taskCompletedList, worklog.taskList || [], idx, worklog.fromActivityToken == data.task.activityToken);
                idx++;
            }
        }.bind(this));
        return idx;
    },
    setFlowInfor: function(data){
        this.flowInforLeftNode = new Element("div", {"styles": this.list.css.flowInforLeftNode}).inject(this.flowInforNode);
        this.flowInforRightNode = new Element("div", {"styles": this.list.css.flowInforRightNode}).inject(this.flowInforNode);
        this.flowInforScrollNode = new Element("div", {"styles": this.list.css.flowInforScrollNode}).inject(this.flowInforNode);
        this.flowInforContentNode = new Element("div", {"styles": this.list.css.flowInforContentNode}).inject(this.flowInforScrollNode);

        var idx = this.setFlowChart(data);

        var x = (idx*40)+((idx-1)*16);
        this.flowInforContentNode.setStyle("width", ""+x+"px");

        this.setFlowInforScroll();
    },
    toFlowInforLeft: function(){
        var size = this.flowInforScrollNode.getSize();
        var scrollSize = this.flowInforScrollNode.getScrollSize();
        var scroll = this.flowInforScrollNode.getScroll();

        if (scroll.x>0){
            var scrollX = scroll.x-size.x;
            if (scrollX<0) scrollX = 0;

            if (scrollX>0){
                //    this.flowInforLeftNode.setStyle("background-image", "url("+"/x_component_process_TaskCenter/$Main/default/processor/left.png)");
            }else{
                this.flowInforLeftNode.setStyle("background-image", "");
            }
            if (scrollX+size.x<scrollSize.x){
                this.flowInforRightNode.setStyle("background-image", "url("+"/x_component_process_TaskCenter/$Main/default/processor/right.png)");
            }else{
                //    this.flowInforRightNode.setStyle("background-image", "");
            }

            this.flowInforScrollFx.start(scrollX);
        }


    },
    toFlowInforRight: function(){
        var size = this.flowInforScrollNode.getSize();
        var scrollSize = this.flowInforScrollNode.getScrollSize();
        var scroll = this.flowInforScrollNode.getScroll();

        if (scroll.x+size.x<scrollSize.x){
            var scrollX = scroll.x+size.x;
            if (scrollX>scrollSize.x) scrollX = scrollSize.x;

            if (scrollX>0){
                this.flowInforLeftNode.setStyle("background-image", "url("+"/x_component_process_TaskCenter/$Main/default/processor/left.png)");
            }else{
                //this.flowInforLeftNode.setStyle("background-image", "");
            }
            if (scrollX+size.x<scrollSize.x){
                //this.flowInforRightNode.setStyle("background-image", "url("+"/x_component_process_TaskCenter/$Main/default/processor/right.png)");
            }else{
                this.flowInforRightNode.setStyle("background-image", "");
            }

            this.flowInforScrollFx.start(scrollX);
        }

    },
    setFlowInforScroll: function(){
        var size = this.flowInforScrollNode.getSize();
        var scrollSize = this.flowInforScrollNode.getScrollSize();
        var scroll = this.flowInforScrollNode.getScroll();

        if (scrollSize.x>size.x){
            if (!this.flowInforScrollFx) this.flowInforScrollFx = new Fx.Scroll(this.flowInforScrollNode, {"wheelStops": false});
            this.flowInforScrollFx.toRight();
            this.flowInforLeftNode.setStyle("background-image", "url("+"/x_component_process_TaskCenter/$Main/default/processor/left.png)");
            this.flowInforLeftNode.addEvent("click", function(){this.toFlowInforLeft();}.bind(this));
            this.flowInforRightNode.addEvent("click", function(){this.toFlowInforRight();}.bind(this));
        }
    },
    createFlowInforWorklogNode: function(activityName, taskCompleteList, taskList, idx, isCurrent){
        if (idx!=0) var logLineNode = new Element("div", {"styles": this.list.css.logLineNode}).inject(this.flowInforContentNode);
        var logActivityNode = new Element("div", {"styles": this.list.css.logActivityNode}).inject(this.flowInforContentNode);
        var logActivityIconNode = new Element("div", {"styles": this.list.css.logActivityIconNode}).inject(logActivityNode);
        var logActivityTextNode = new Element("div", {"styles": this.list.css.logActivityTextNode, "text": activityName}).inject(logActivityNode);

        var iconName = "user";
        var iconSuffix = "";
        if ((taskCompleteList.length+taskList.length)>1) iconName = "users";
        if (isCurrent) iconSuffix = "_red";


        var inforNode = new Element("div", {"styles": this.list.css.logInforNode});
        taskCompleteList.each(function(route){
            var routeNode = new Element("div", {"styles": this.list.css.logRouteNode}).inject(inforNode);
            routeNode.set("text", route.person.substring(0, route.person.indexOf("@"))+": ");
            var opinionNode = new Element("div", {"styles": this.list.css.logOpinionNode}).inject(inforNode);
            if (!route.opinion) route.opinion = "";
            opinionNode.set("text", "["+route.routeName+"] "+route.opinion);
            var timeNode = new Element("div", {"styles": this.list.css.logTimeNode}).inject(inforNode);
            timeNode.set("text", route.completedTime);

            if (this.list.app.desktop.session.user.distinguishedName === route.person) if (!iconSuffix) iconSuffix = "_yellow";
        }.bind(this));

        taskList.each(function(task){
            var taskTextNode = new Element("div", {"styles": this.list.css.taskTextNode}).inject(inforNode);
            taskTextNode.set("text", task.person.substring(0, task.person.indexOf("@"))+" "+this.list.app.lp.processing);
        }.bind(this));

        var icon = "url("+"/x_component_process_TaskCenter/$Main/default/processor/"+iconName+iconSuffix+".png)";
        logActivityIconNode.setStyle("background-image", icon);

        if (taskList.length){
            var countNode = new Element("div", {"styles": this.list.css.logTaskCountNode}).inject(logActivityNode);
            //var text = (taskList.length>99) ? "99+" : taskList.length;
            var text = taskList.length;
            countNode.set("text", text);
        }
        new mBox.Tooltip({
            content: inforNode,
            setStyles: {content: {padding: 10, lineHeight: 20}},
            attach: logActivityNode,
            transition: 'flyin',
            offset: {
                x: this.list.app.contentNode.getScroll().x,
                y: this.list.listScrollAreaNode.getScroll().y
            }
        });
    },

    setProcessor: function(){
        var _self = this;
        MWF.xDesktop.requireApp("process.Work", "Processor", function(){
            new MWF.xApplication.process.Work.Processor(this.processNode, this.data, {
                "style": "task",
                "mediaNode": this.mainContentNode,
                "onCancel": function(){
                    _self.closeEditTask();
                    delete this;
                },
                "onSubmit": function(routeName, opinion, medias){
                    _self.submitTask(routeName, opinion, medias, this);
                    delete this;
                }
            })
        }.bind(this));
    },
    addMessage: function(data){
        if (layout.desktop.message){
            var content = "";

            if (data.length){
                data.each(function(work){
                    var users = [];
                    work.taskList.each(function(task){
                        users.push(task.person+"("+task.department+")");
                    }.bind(this));

                    content += "<div><b>"+this.list.app.lp.nextActivity+"<font style=\"color: #ea621f\">"+work.fromActivityName+"</font>, "+this.list.app.lp.nextUser+"<font style=\"color: #ea621f\">"+users.join(", ")+"</font></b></div>"
                }.bind(this));
            }else{
                content += this.list.app.lp.workCompleted;
            }
            var msg = {
                "subject": this.list.app.lp.taskProcessed,
                "content": "<div>"+this.list.app.lp.taskProcessedMessage+"“"+this.data.title+"”</div>"+content
            };

            layout.desktop.message.addTooltip(msg);
            layout.desktop.message.addMessage(msg);
        }
    },
    submitTask: function(routeName, opinion, medias, processor, flag){
        if (!opinion) opinion = routeName;

        this.data.routeName = routeName;
        this.data.opinion = opinion;

        var mediaIds = [];
        if (medias.length){
            medias.each(function(file){
                var formData = new FormData();
                formData.append("file", file);
                //formData.append("fileName", (new MWF.UUID()).toString()+".wav");
                formData.append("site", "$mediaOpinion");
                this.list.app.action.uploadAttachment(this.data.work, formData, file, function(json){
                    mediaIds.push(json.data.id);
                }.bind(this), null, false);
            }.bind(this));
        }
        if (mediaIds.length) this.data.mediaOpinion = mediaIds.join(",");

        this.list.app.action.processTask(function(json){
            //    this.list.app.notice(this.list.app.lp.taskProcessed, "success");
            if (processor) processor.destroy();
            if (!flag){
                this.closeEditTask(function(){
                    this.node.destroy();
                    this.list.refresh();
                    this.addMessage(json.data);
                    delete this;
                }.bind(this));
            }else{
                this.node.destroy();
                this.list.refresh();
                this.addMessage(json.data);
                delete this;
            }

        }.bind(this), null, this.data.id, this.data);
    },

    resizeEditNode: function(){
        var p = this.getEditNodePosition();
        var size = this.list.app.content.getSize();
        var maskNode = this.list.app.window.node.getElement("#mask_"+this.data.id);

        if (maskNode) maskNode.setStyles({"width": ""+size.x+"px", "height": ""+size.y+"px"});

        this.mainContentNode.setStyles({"top": ""+ p.y+"px", "left": ""+ p.x +"px"});
    },

    getEditNodePosition: function(){
        var size = this.list.app.content.getSize();

        var p = this.node.getPosition(this.list.app.content);
        var top = p.y;
        var left = p.x;

        var w = this.list.css.itemNode_edit.width.toInt();
        var h = this.list.css.itemNode_edit.height.toInt();
        if (top+h>size.y){
            top = size.y-h;
            if (top<0) top=0;
        }
        if (left+w>size.x){
            left = size.x-w;
            if (left<0) left=0;
        }
        // var top = size.y/2-230;
        // var left = size.x/2-260;
        // if (top<0) top = 0;
        return {"x": left, "y": top};
    },

    showEditNode: function(data, callback){
        if (layout.mobile){
            this.showEditNode_mobile(data, callback);
        }else{
            this.showEditNode_pc(data, callback);
        }
    },
    showEditNode_mobile: function(data, callback){
        var p = this.list.app.tabAreaNode.getPosition(this.list.app.content);
        var contentSize = this.list.app.contentNode.getSize();
        var tabSize = this.list.app.tabAreaNode.getSize();
        var y = contentSize.y+tabSize.y;

        this.list.css.itemNode_edit.top = ""+ p.y+"px";
        this.list.css.itemNode_edit.left = ""+ p.x+"px";
        this.list.css.itemNode_edit.width = ""+ contentSize.x+"px";
        this.list.css.itemNode_edit.height = ""+ y+"px";

        document.body.setStyle("-webkit-overflow-scrolling", "auto");

        this.editNode.setStyle("display", "none");
        var morph = new Fx.Morph(this.mainContentNode, {
            "duration": 200,
            "transition": Fx.Transitions.Expo.easeOut,
            "onComplete": function(){
                this.resizeEditNodeFun = this.resizeEditNode.bind(this);
                this.list.app.addEvent("resize", this.resizeEditNodeFun);
                this.setEditTaskNodes(data);

                this.closeNode.setStyle("display", "block");

                if (callback) callback();
            }.bind(this)
        });
        morph.start(this.list.css.itemNode_edit);
    },
    showEditNode_pc: function(data, callback){
        var p = this.getEditNodePosition();
        this.list.css.itemNode_edit.top = ""+ p.y+"px";
        this.list.css.itemNode_edit.left = ""+ p.x+"px";

        this.editNode.setStyle("display", "none");

        var morph = new Fx.Morph(this.mainContentNode, {
            "duration": 200,
            "transition": Fx.Transitions.Expo.easeOut,
            "onComplete": function(){
                this.resizeEditNodeFun = this.resizeEditNode.bind(this);
                this.list.app.addEvent("resize", this.resizeEditNodeFun);
                this.setEditTaskNodes(data);

                this.closeNode.setStyle("display", "block");

                if (callback) callback();
            }.bind(this)
        });
        morph.start(this.list.css.itemNode_edit);
    },

    _getJobByTask: function(callback){
        this.list.app.action.getSimpleJobByTask(function(json){
            if (callback) callback(json.data);
        }.bind(this), null, this.data.id);
    },

    setContent: function(){
        this.applicationTitleNode.set("text", this.data.applicationName);
        this.titleNode.set("html", "<font style=\"color: #333;\">["+this.data.processName+"]&nbsp;&nbsp;</font>"+this.data.title);
        this.titleNode.set("title", this.data.title);
        this.activityNode.set("text", "( "+(this.data.activityName || this.list.app.lp.completed)+" )");

        this.timeContentNode.set("text", this.list.app.lp.list_comedate+": "+this.data.startTime);

        this.loadActions();
        this.loadApplicationIcon();
        //this.setTimeIconNode();
    },
    loadActions: function(){
        if (this.data.allowRapid){
            this.editNode = new Element("div", {"styles": this.list.css.titleActionEditNode}).inject(this.actionContentNode);
            this.closeNode = new Element("div", {"styles": this.list.css.titleActionCloseNode}).inject(this.actionContentNode);
        }
    },
    loadApplicationIcon: function(){
        this.getApplicationIcon(function(icon){
            if (icon.icon){
                //this.mainContentNode.setStyle("border-top", "4px solid "+icon.iconHue);
                this.applicationIconNode.setStyle("background-image", "url(data:image/png;base64,"+icon.icon+")");
            }else{
                this.mainContentNode.setStyle("border-top", "4px solid #4e82bd");
                this.applicationIconNode.setStyle("background-image", "url("+"/x_component_process_ApplicationExplorer/$Main/default/icon/application.png)");
            }
        }.bind(this));
    },
    getApplicationIcon: function(callback){
        var icon = this.list.app.appIcons[this.data.application];
        if (!icon) {
            this.list.app.action.getApplicationIcon(function (json) {
                if (json.data){
                    if (json.data){
                        this.list.app.appIcons[this.data.application] = json.data;
                        if (callback) callback(json.data);
                    }
                }else{
                    this.invalidItem = true;
                    if (callback) callback({
                        "icon": "iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAEgElEQVR4Xu1aPXMTSRB9vUaGQqs6iCgi4Bfgq7I2lqWrSwgQMQHyPzAJIeguvOT4BxbBxYjgkquTrFiiCvkXYCKKCFMSFEjs9tWsrEKWvTtfuyvXrTbd6ZnuN69fz842IecP5Tx+rAFYMyDnCKxTYBUE4MrWta9UuLu49hWeHlJveJy1P6kzQAT7eWPzPgN1MFeI6FpckMx8DKIeAe2iP3mVNiipADALuvAIQAOgLbtd5SGAVtGfvkgDjMQB+Fz1ngXgPdlO64IimOGAnhe7/d90bePGJwbAuOY9AqMJwu0kHTwzF+MIhKbb6b9IYh1rAATdxxub+yRyPMOHgbbrT3Zt08IKgHGlvIUN7NvnuSlyPISPXbc3EDph9BgDMPplu4KAXiad67pRhFXD4Qelf1/3dG3FeCMARPDEzoHJgmnZMAU7JiBoAyBozw4OVr3zy0AKJlCAHd100ALgpL4frC7nZfzhYdGf7ugIoxYAo5r3Mmu1l4V8hglAu9TpP1C1UwZgXC03QLSvOvFKxzHvut1BS8UHDQC8t6kfclQ8VhnDOHK7/TsqQ5UAGFW9JhGeqUy4PIZu3AR/eG9iChtbcPDY7b5+LltYCkB40nMKb01U/9Kv93D5yVN8++N3fP/nb5kvp97b2IqJRFVwg+kdmSBKARhXt/dAzp9a3gOYBzC30wHBxvaUnwoskANQK7/RLXvLAeiAYGN7dpN46HYGP8dtXiwAJ5cZH3V2X+Tt1b/akSZxTIgKfj7Zl4d1bT0p+pPrcWkQC4Bp6ZMFch4IJjZKGyMpibEAjGpem4D7SgstDdIJSGesri8MvCp1+pGf6vEAVMsfTdR/7qRKYGKsqBRRj454njeHqAal7uB61PzxKVDzWBfx5fEyEOLmtw1+Prfb6UfGGfnCRACjgjEBIanghU9GACT9za8DQpLBh4eimLuCSAYkDYBwRAWEpINfA3BRGKCy+zonRh1xNkqB3IugQHic5zIoABjVyscE+kmHbotjZbQXgpf6QQj8qdQZRP6QXR+F43Y39x9DJkL4v/ocDoWw6g1BONXNIdMEm0sNG9szfjEO3W4/tj9BfiOU9yux2e/vwpFJNbC52LSxDY+/4E+uP71tfSkalsM8X4vP82pc9URnxi1Z/l+I94x3brev1Kki1YAfAOT819jsZGh+R5gVM2R3gMt+KDMgFBbR/uZs9nTLYlbBg3FYDCYVmfAt+qMFQHguEA0SG+iZVIU0gRCqTz4qqTZIzANI47bIFpzMWmQWQQBTe9VMEDsP4rpJf5CIRTsFFncqbJNzqLUyTWAcIuCGLu2tNGCZqieNki3TP0im1Bdq7/qTho7gnbeWFQNOsUG00IBEq2y6hyXGO4Cbqi0wMoATA+DHgWl7j4maSWtDqPIsApd3fciCTjQFzltsdl641ACchrU+iDxH0CoG31u2dE81BaJQn4FRqDNRXRylZMwIVR3UI+Z2MZi20wg6dQaoUDDsNV54TMuYylpxYxLXAFuHsrZfA5A14hdtvTUDLtqOZO1P7hnwH8CljF98DV13AAAAAElFTkSuQmCC",
                        "iconHue": "#4e82bd"
                    });
                }
            }.bind(this), function(){
                this.invalidItem = true;
                if (callback) callback({
                    "icon": "iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAEgElEQVR4Xu1aPXMTSRB9vUaGQqs6iCgi4Bfgq7I2lqWrSwgQMQHyPzAJIeguvOT4BxbBxYjgkquTrFiiCvkXYCKKCFMSFEjs9tWsrEKWvTtfuyvXrTbd6ZnuN69fz842IecP5Tx+rAFYMyDnCKxTYBUE4MrWta9UuLu49hWeHlJveJy1P6kzQAT7eWPzPgN1MFeI6FpckMx8DKIeAe2iP3mVNiipADALuvAIQAOgLbtd5SGAVtGfvkgDjMQB+Fz1ngXgPdlO64IimOGAnhe7/d90bePGJwbAuOY9AqMJwu0kHTwzF+MIhKbb6b9IYh1rAATdxxub+yRyPMOHgbbrT3Zt08IKgHGlvIUN7NvnuSlyPISPXbc3EDph9BgDMPplu4KAXiad67pRhFXD4Qelf1/3dG3FeCMARPDEzoHJgmnZMAU7JiBoAyBozw4OVr3zy0AKJlCAHd100ALgpL4frC7nZfzhYdGf7ugIoxYAo5r3Mmu1l4V8hglAu9TpP1C1UwZgXC03QLSvOvFKxzHvut1BS8UHDQC8t6kfclQ8VhnDOHK7/TsqQ5UAGFW9JhGeqUy4PIZu3AR/eG9iChtbcPDY7b5+LltYCkB40nMKb01U/9Kv93D5yVN8++N3fP/nb5kvp97b2IqJRFVwg+kdmSBKARhXt/dAzp9a3gOYBzC30wHBxvaUnwoskANQK7/RLXvLAeiAYGN7dpN46HYGP8dtXiwAJ5cZH3V2X+Tt1b/akSZxTIgKfj7Zl4d1bT0p+pPrcWkQC4Bp6ZMFch4IJjZKGyMpibEAjGpem4D7SgstDdIJSGesri8MvCp1+pGf6vEAVMsfTdR/7qRKYGKsqBRRj454njeHqAal7uB61PzxKVDzWBfx5fEyEOLmtw1+Prfb6UfGGfnCRACjgjEBIanghU9GACT9za8DQpLBh4eimLuCSAYkDYBwRAWEpINfA3BRGKCy+zonRh1xNkqB3IugQHic5zIoABjVyscE+kmHbotjZbQXgpf6QQj8qdQZRP6QXR+F43Y39x9DJkL4v/ocDoWw6g1BONXNIdMEm0sNG9szfjEO3W4/tj9BfiOU9yux2e/vwpFJNbC52LSxDY+/4E+uP71tfSkalsM8X4vP82pc9URnxi1Z/l+I94x3brev1Kki1YAfAOT819jsZGh+R5gVM2R3gMt+KDMgFBbR/uZs9nTLYlbBg3FYDCYVmfAt+qMFQHguEA0SG+iZVIU0gRCqTz4qqTZIzANI47bIFpzMWmQWQQBTe9VMEDsP4rpJf5CIRTsFFncqbJNzqLUyTWAcIuCGLu2tNGCZqieNki3TP0im1Bdq7/qTho7gnbeWFQNOsUG00IBEq2y6hyXGO4Cbqi0wMoATA+DHgWl7j4maSWtDqPIsApd3fciCTjQFzltsdl641ACchrU+iDxH0CoG31u2dE81BaJQn4FRqDNRXRylZMwIVR3UI+Z2MZi20wg6dQaoUDDsNV54TMuYylpxYxLXAFuHsrZfA5A14hdtvTUDLtqOZO1P7hnwH8CljF98DV13AAAAAElFTkSuQmCC",
                    "iconHue": "#f44336"
                });
            }.bind(this), this.data.application);
        }else{
            if (callback) callback(icon);
        }
    },
    setNewIcon: function(){
        var start = new Date().parse(this.data.startTime);
        var now = new Date();
        if (now.getTime()-start.getTime()<86400000){
            this.newIconNode.setStyle("background-image", "url("+"/x_component_process_TaskCenter/$Main/default/time/new.png)");
        }
    },

    setTimeIconNode: function(){
        var colors = ["#FF0000", "#00d400", "#f6ff0c"];
        var idx = (Math.random()*3).toInt();
        var color = colors[idx];
        this.timeIconNode.setStyle("background-color", color);

        //timeIconNode
    }


    //load: function(){
    //    this.node = this.table.insertRow(this.table.rows.length || 0);
    //    this.node.setStyles(this.list.css.itemLine);
    //
    //    this.appNode = new Element("td", {"styles": this.list.css.itemCell, "text": this.data.applicationName}).inject(this.node);
    //    this.titleNode = new Element("td", {"styles": this.list.css.itemCell, "text": this.data.title}).inject(this.node);
    //    this.processNode = new Element("td", {"styles": this.list.css.itemCell, "text": this.data.processName}).inject(this.node);
    //    this.activityNode = new Element("td", {"styles": this.list.css.itemCell, "text": this.data.activityName}).inject(this.node);
    //    this.dateNode = new Element("td", {"styles": this.list.css.itemCell, "text": this.data.updateTime}).inject(this.node);
    //
    //
    //    //this.titleNode = this.node.insertCell(this.node.cells.length || 0);
    //    //this.titleNode.setStyles(this.list.css.itemCell);
    //    //this.titleNode.set("text", this.data.title);
    //}
});