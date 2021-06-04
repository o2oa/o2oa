MWF.xApplication.Homepage.InforContent  = new Class({
    Extends: MWF.xApplication.Homepage.TaskContent,
    Implements: [Options, Events],
    options: {
        "view": "inforContent.html"
    },
    load: function(){
        this.tabs = {};
        this.container.loadHtml(this.viewPath, {"bind": {"lp": this.app.lp}, "module": this}, function(){
            this.initSize();
            this.loadInfor(function(){
                this.fireEvent("load");
            }.bind(this));
            // this.loadTask(function(){
            // 	this.fireEvent("load");
            // }.bind(this));
            //
            // //是否需要定时自动刷新 @todo
            // this.startProcessAction.addEvent("click", this.startProcess.bind(this));

            //this.moreInforAction.addEvent("click", this.moreInfor.bind(this));
        }.bind(this));
    },
    openInfor: function(e){
        layout.openApplication(e, "cms.Index");
    },
    setContentSize: function(){
        var total = this.container.getSize().y;
        var titleHeight = this.taskTitleNode.getSize().y+this.taskTitleNode.getEdgeHeight();
        var thisHeight = this.contentNode.getEdgeHeight();
        var contentHeight = total-titleHeight-thisHeight;
        this.contentNode.setStyle("height", ""+contentHeight+"px");
        this.contentHeight = contentHeight;
        //this.pageSize = (this.options.itemHeight/this.contentHeight).toInt();

        if (this.noItemNode){
            var m;
            if (this.currentTab && this.currentTab.itemsContainer){
                m = (this.currentTab.itemsContainer.getSize().y- this.noItemNode.getSize().y)/2;
            }else{
                m = (this.contentHeight- this.noItemNode.getSize().y)/2;
            }
            this.noItemNode.setStyle("margin-top", ""+m+"px");
        }

        if (this.currentTab){
            if (this.currentTab.hotpicDocAreaNode){
                var pageHeight = this.currentTab.hotpicPageAreaNode.getSize().y+this.currentTab.hotpicPageAreaNode.getEdgeHeight();
                var height = this.contentHeight-pageHeight;
                this.currentTab.hotpicDocAreaNode.setStyle("height", ""+height+"px");
            }

            if (this.currentTab.itemsContainer){
                var itemPageHeight = this.pageAreaNode.getSize().y+this.pageAreaNode.getEdgeHeight();
                var itemHeight = this.contentHeight-itemPageHeight;
                this.currentTab.itemsContainer.setStyle("height", ""+itemHeight+"px");
            }
        }
    },

    loadInfor: function(callback){
        this.loadInforApps();
        this.loadAllInfor(callback);
    },
    loadInforApps: function(){
        o2.Actions.load("x_cms_assemble_control").AppInfoAction.listWhatICanView_Article(function(json){
            if (json.data) json.data.each(function(app){
                var tabNode = new Element("div.o2_homepage_title_tab", {"text": app.appName}).inject(this.tabAreaNode);
                tabNode.store("app", app.id);
                tabNode.addEvents({
                    "mouseover": function(e){ this.tabover(e.event); }.bind(this),
                    "mouseout": function(e){ this.tabout(e.event); }.bind(this),
                    "click": function(e){
                        var id = e.target.retrieve("app");
                        this.loadList(id, (e.target || e.currentTarget));
                    }.bind(this),
                });
            }.bind(this));
        }.bind(this));
    },
    loadList: function(id, tabNode){
        if (!this.isLoading) {
            if (!this.tabs[id]){
                this.tabs[id] = new MWF.xApplication.Homepage.InforContent.Infor(this, tabNode, id);
            }else{
                this.tabs[id].load();
            }
            this.currentTab = this.tabs[id];
        }
    },
    loadAllInfor: function(callback){
        if (!this.isLoading) {
            if (!this.allInforContentTab){
                this.allInforContentTab = new MWF.xApplication.Homepage.InforContent.AllInfor(this, this.allInforTab, {
                    "onLoad": function(){ if (callback) callback(); }
                });
            }else{
                this.allInforContentTab.load();
            }
            this.currentTab = this.allInforContentTab;
        }
    }

});


MWF.xApplication.Homepage.InforContent.AllInfor = new Class({
    Extends: MWF.xApplication.Homepage.TaskContent.Task,
    Implements: [Options, Events],
    options: {
        "type": "allInfor",
        "switchTime": 5000
    },
    initialize: function(content, tab, options){
        this.setOptions(options);
        this.content = content;
        this.app = this.content.app;
        this.container = this.content.contentNode;
        this.tab = tab;
        this.load();
    },
    //this.container = this.content.itemContentNode;
    load: function(){
        this.container.empty();
        this.content.isLoading = true;
        if (this.content.currentTab) this.content.currentTab.hideTab();
        this.loadItemsNode(function(){
            this.beginLoadContent();
            this.showTab();
            this.initSize();
            this.initItemCount(this.page);
            this.loadItemsRes();
        }.bind(this));
    },
    reload: function(){
        this.load();
        // if (!this.content.isLoading){
        // 	this.initSize();
        // 	this.beginLoadContent();
        // 	this.showTab();
        // 	this.initItemCount(this.page);
        // 	this.loadItemsRes();
        // }
    },

    reloadPic: function(){
        this.hotpicDocArea.empty();
        this.hotpicPageContent.empty();
        this.loadItemsRes_pic();
    },
    reloadItem: function(){
        this.beginItemLoadContent();
        this.initItemCount(this.page);
        this.loadItemsRes_item();
    },

    initItemCount: function(count){
        this.page = count || 1;
        this.pageSize = (this.itemContentArea.getSize().y/this.options.itemHeight).toInt();
    },
    loadItemsNode: function(callback){
        var url = this.app.path+this.app.options.style+"/allInfor.html";
        this.container.loadHtml(url, {"module": this}, callback.bind(this));
    },

    initSize: function(){
        this.resizeFun = this.resize.bind(this);
        this.resize();
        this.app.addEvent("resize", this.resizeFun);
    },
    resize: function(){
        var mHeight = this.hotpicDocArea.getEdgeHeight();
        var pageHeight = this.hotpicPageArea.getSize().y+this.hotpicPageArea.getEdgeHeight();
        var h = this.content.contentHeight - mHeight - pageHeight;
        this.hotpicDocArea.setStyle("height", ""+h+"px");

        mHeight = this.itemContentArea.getEdgeHeight();
        pageHeight = this.itemPageArea.getSize().y+this.itemPageArea.getEdgeHeight();
        h = this.content.contentHeight - mHeight - pageHeight;
        this.itemContentArea.setStyle("height", ""+h+"px");

        if (this.hotpics && this.hotpics.length) this.hotpics.each(function(picNode){
            this.resizePicNode(picNode);
        }.bind(this));

        if (this.noHotpicNode){
            var m = (this.hotpicDocArea.getSize().y- this.noHotpicNode.getSize().y)/2;
            this.noHotpicNode.setStyle("margin-top", ""+m+"px");
        }
        if (this.noItemNode){
            var m = (this.itemContentArea.getSize().y- this.noItemNode.getSize().y)/2;
            this.noItemNode.setStyle("margin-top", ""+m+"px");
        }

    },
    destroy: function(){
        this.container.empty();
        if (this.switchId) window.clearInterval(this.switchId);
        this.switchId = null;
        if (this.resizeFun) this.app.removeEvent("resize", this.resizeFun);
        o2.release(this);
    },
    hideTab: function(){
        this.container.empty();
        if (this.switchId) window.clearInterval(this.switchId);
        this.switchId = null;
        if (this.resizeFun) this.app.removeEvent("resize", this.resizeFun);
        this.content.currentTab = null;
        this.tab.removeClass("mainColor_color").removeClass("mainColor_border").removeClass("o2_homepage_title_tab_current").removeClass("o2_homepage_title_tab_over");
        // if (this.destroy) this.destroy();
        // o2.release(this);
    },
    beginLoadContent: function(){
        this.beginPicLoadContent();
        this.beginItemLoadContent();
        this.noItemNode = null;
        this.hotpics = [];
        this.content.isLoading = true;
    },
    beginPicLoadContent: function(){
        this.picLoading = true;
        this.hotpicDocArea.empty();
        this.hotpicDocArea.addClass("o2_homepage_area_content_loading").addClass("icon_loading");
        this.hotpicPageContent.empty();
    },
    beginItemLoadContent: function(){
        this.itemLoading = true;
        this.itemContentArea.empty();
        this.itemContentArea.addClass("o2_homepage_area_content_loading").addClass("icon_loading");
        this.itemPageArea.empty();
    },
    endLoadContent: function(){
        this.endPicLoadContent();
        this.endItemLoadContent();
        this.content.isLoading = false;
    },
    endPicLoadContent: function(){
        if (this.noHotpicNode){
            this.noHotpicNode.destroy();
            this.noHotpicNode = null;
        }
        this.hotpicDocArea.removeClass("o2_homepage_area_content_loading").removeClass("icon_loading");
        this.picLoading = false;
        if (!this.itemLoading && !this.picLoading){
            this.content.isLoading = false;
            this.fireEvent("load");
        }
    },
    endItemLoadContent: function(){
        if (this.noItemNode){
            this.noItemNode.destroy();
            this.noItemNode = null;
        }
        this.itemContentArea.removeClass("o2_homepage_area_content_loading").removeClass("icon_loading");
        this.itemLoading = false;
        if (!this.itemLoading && !this.picLoading){
            this.content.isLoading = false;
            this.fireEvent("load");
        }
    },

    loadItemsRes: function(){
        //this.loadHotpic();
        this.loadItemsRes_pic();
        this.loadItemsRes_item();
    },
    loadItemsRes_pic:function(){
        this.hotpicCount = 6;
        o2.Actions.load("x_hotpic_assemble_control").HotPictureInfoAction.listForPage(1, this.hotpicCount, {}, function(json){
            this.hotpicCount = json.data.length;
            if (this.hotpicCount) {
                var url = this.app.path+this.app.options.style+"/hotpic.html";
                o2.loadHtml(url, function(loaded){
                    this.endPicLoadContent();
                    var html = loaded[0].data;
                    json.data.each(function(doc){
                        this.loadHotpic(doc, html);
                    }.bind(this));

                    this.loadPicPage();
                }.bind(this));
            }else{
                this.endPicLoadContent();
                this.noHotpicNode = new Element("div.o2_homepage_hotpic_area_content_empty_node", {"text": this.app.lp.noHotpic}).inject(this.hotpicDocArea);
                var m = (this.hotpicDocArea.getSize().y- this.noHotpicNode.getSize().y)/2;
                this.noHotpicNode.setStyle("margin-top", ""+m+"px");
            }
        }.bind(this));
    },
    loadItemsRes_item:function(){
        o2.Actions.load("x_cms_assemble_control").DocumentAction.query_listWithFilterPaging(this.page, this.pageSize, {
            "orderField": "publishTime",
            "orderType": "DESC",
            "readFlag": "UNREAD"
        }, function(json){
            this.itemCount = json.count;
            if (json.data && json.data.length){
                this.loadItems(json.data);
                this.checkLoadPage();
            }else{
                this.endItemLoadContent();
                this.noItemNode = new Element("div.o2_homepage_hotpic_area_content_empty_node", {"text": this.app.lp.noInforItem}).inject(this.itemContentArea);
                var m = (this.itemContentArea.getSize().y- this.noItemNode.getSize().y)/2;
                this.noItemNode.setStyle("margin-top", ""+m+"px");
            }
        }.bind(this));
    },

    checkLoadPage: function(){
        if (this.itemCount){
            this.getPageCount();
            this.loadPage(this.itemPageArea);
        }
    },
    getPageCount:function(){
        var n = this.itemCount/this.pageSize;
        var nInt = n.toInt();
        this.pages = (nInt===n) ? nInt : nInt+1;
    },

    loadItems: function(data){
        var table = new Element("table", { "width": "100%", "border": 0, "cellpadding": 0, "cellspacing": 0 }).inject(this.itemContentArea);
        data.each(function(d, i){
            this.loadItem(table, d, i);
        }.bind(this));
        this.endItemLoadContent();
    },
    loadItemRow: function(table, d, i){
        var row = table.insertRow(-1).addClass("o2_homepage_task_item_row");
        var idx = (this.page-1)*this.pageSize+i+1;
        var idxShow = (idx>99) ? "···" : idx;

        var cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_number");
        var numberNode = new Element("div.o2_homepage_task_item_number", {"text": idxShow, "title":idx}).inject(cell);

        var subject = o2.txt(d.title || this.app.lp.noSubject);
        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_subject");
        new Element("div.o2_homepage_task_item_subject", {"html": subject, "title":subject}).inject(cell);

        var time = (d.publishTime || d.createTime).substr(0,10);
        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_time");
        new Element("div.o2_homepage_task_item_time", {"text": time, "title":this.app.lp.publishTime + ": " + time}).inject(cell);

        return row;
    },


    resizePicNode: function(picNode){
        var size = this.hotpicDocArea.getSize();
        var imgHeight = size.x/2;
        picNode.getFirst().setStyle("height", ""+imgHeight+"px");
    },
    loadHotpic: function(doc, html, i){
        var picNode = new Element("div.o2_homepage_infor_hotpicNode").inject(this.hotpicDocArea, "top");
        var bind = {
            "title": doc.title,
            "summary": doc.summary || doc.title,
            "createTime": doc.createTime.toString().substr(0, 10),
            "url": MWF.xDesktop.getImageSrc(doc.picId)
        }
        picNode.set("html", html.bindJson(bind));
        picNode.store("data", doc);
        var _self = this;
        picNode.addEvent("click", function(e){
            var d = this.retrieve("data");
            _self.open(e, d);
        });
        if (!this.hotpics) this.hotpics = [];
        this.resizePicNode(picNode);
        this.hotpics.push(picNode);
    },
    loadPicPage: function(){
        if (this.hotpics && this.hotpics.length){

            this.hotpics.each(function(picNode, i){
                var pageNode = new Element("div.o2_homepage_infor_hotpicArea_page_item").inject(this.hotpicPageContent);
                pageNode.store("picNode", picNode);
                pageNode.addEvent("click", function(e){
                    this.showPic(e.target);
                    this.beginSwitchPic();
                }.bind(this));
                if (i==0) this.showPic(pageNode);
            }.bind(this));
            this.beginSwitchPic();

        }else{
            this.endPicLoadContent();
            this.noHotpicNode = new Element("div.o2_homepage_hotpic_area_content_empty_node", {"text": this.app.lp.noHotpic}).inject(this.hotpicDocArea);
            var m = (this.hotpicDocArea.getSize().y- this.noHotpicNode.getSize().y)/2;
            this.noHotpicNode.setStyle("margin-top", ""+m+"px");
        }
    },
    beginSwitchPic: function(){
        if (this.switchId) window.clearInterval(this.switchId);
        this.switchId = window.setInterval(function(){
            var currentPageNode = this.hotpicPageContent.getElement(".o2_homepage_infor_hotpicArea_page_item_current");
            var nextPageNode = null;
            if (!currentPageNode){
                nextPageNode = this.hotpicPageContent.getFirst();
            }else{
                nextPageNode = currentPageNode.getNext();
                if (!nextPageNode) nextPageNode = this.hotpicPageContent.getFirst();
            }
            if (nextPageNode) this.showPic(nextPageNode);
        }.bind(this), this.options.switchTime)
    },


    showPic: function(pageNode){
        var picNode = pageNode.retrieve("picNode");
        if (picNode){
            if (this.currentPageNode) this.hidePic(this.currentPageNode);
            picNode.fade("in");
            picNode.addClass("o2_homepage_infor_hotpicNode_current");
            pageNode.addClass("o2_homepage_infor_hotpicArea_page_item_current");
            this.currentPageNode = pageNode;
        }
    },
    hidePic: function(pageNode){
        var picNode = pageNode.retrieve("picNode");
        if (picNode){
            picNode.fade("out");
            picNode.removeClass("o2_homepage_infor_hotpicNode_current");
            pageNode.removeClass("o2_homepage_infor_hotpicArea_page_item_current");
            this.currentPageNode = null;
        }
    },

    open: function(e, data){

        var id = data.infoId || data.id;
        // var options = {"documentId": id, "docTitle": d.title, "appId": "cms.Document"+id};
        // layout.openApplication(e, "cms.Document", options);

        if( data.application == "BBS" ){
            var appId = "ForumDocument"+data.infoId;
            if (this.app.desktop.apps[appId]){
                this.app.desktop.apps[appId].setCurrent();
            }else {
                layout.openApplication(null, "ForumDocument", {
                    "id" : id,
                    "appId": appId,
                    // "docTitle": data.title,
                    "isEdited" : false,
                    "isNew" : false
                });
            }
        }else{
            var appId = "cms.Document"+data.infoId;
            if (this.app.desktop.apps[appId]){
                this.app.desktop.apps[appId].setCurrent();
            }else {
                layout.openApplication(null, "cms.Document", {
                    "documentId" : id,
                    // "docTitle": data.title,
                    "appId": appId,
                    "readonly" : true
                });
            }
        }
    },
    prevPage: function(){
        if (this.page>1){
            this.page--;
            //this.loadPageNumber();
            this.reloadItem();
        }
    },
    nextPage: function(){
        if (this.page<this.pages){
            this.page++;
            //this.loadPageNumber();
            this.reloadItem();
        }
    },
    gotoPage: function(i){
        this.page = i.toInt();
        //this.loadPageNumber();
        this.reloadItem();
    },
    reversePage: function(){
        var range = this.getCurrentPageRange();
        var endNumber = range.beginNumber-1;
        var beginNumber = endNumber-(this.options.showPages-1);
        if (beginNumber<1) beginNumber = 1;
        this.page = beginNumber+((this.options.showPages/2).toInt());
        this.reloadItem();
    },
    forwardPage: function(){
        var range = this.getCurrentPageRange();
        var beginNumber = range.endNumber+1;
        var endNumber = beginNumber+(this.options.showPages-1);
        if (beginNumber>=this.pages) endNumber = this.pages;
        this.page = endNumber-((this.options.showPages/2).toInt());
        this.reloadItem();
    }
});
MWF.xApplication.Homepage.InforContent.Infor  = new Class({
    Extends: MWF.xApplication.Homepage.TaskContent.Task,
    Implements: [Options, Events],
    options: {
        "type": "infor",
    },
    initialize: function(content, tab, id, options){
        this.setOptions(options);
        this.content = content;
        this.app = this.content.app;
        this.container = this.content.contentNode;
        this.tab = tab;
        this.id = id;

        this.load();
    },
    load: function(){
        this.container = this.content.contentNode;
        this.container.empty();
        this.beginLoadContent();
        this.showTab();
        this.initItemCount();

        this.loadItemsRes();
    },
    reload: function(){
        if (!this.content.isLoading) {
            this.container = this.content.contentNode;
            this.container.empty();

            this.beginLoadContent();
            this.showTab();
            this.initItemCount(this.page);
            this.loadItemsRes();
        }
    },

    showTab: function(){
        this.container.removeClass("o2_homepage_area_content_loading").removeClass("icon_loading");
        this.itemsContainer = new Element("div.o2_homepage_task_area_content").inject(this.container);
        var pageAreaNode = new Element("div.o2_homepage_infor_area_action").inject(this.container);
        this.container = this.itemsContainer;
        this.content.pageAreaNode = pageAreaNode;
        this.container.addClass("o2_homepage_area_content_loading").addClass("icon_loading");

        var itemPageHeight = pageAreaNode.getSize().y+pageAreaNode.getEdgeHeight();
        var itemHeight = this.content.contentHeight-itemPageHeight;
        this.container.setStyle("height", ""+itemHeight+"px");

        this.content.currentTab = this;
        this.tab.addClass("mainColor_color").addClass("mainColor_border").addClass("o2_homepage_title_tab_current").removeClass("o2_homepage_title_tab_over");
    },
    loadItemsRes: function(){
        o2.Actions.load("x_cms_assemble_control").DocumentAction.query_listWithFilterPaging(this.page, this.pageSize, {
            "orderField": "publishTime",
            "orderType": "DESC",
            "readFlag": "UNREAD",
            "appIdList": [this.id]
        }, function(json){
            this.itemCount = json.count;
            if (json.data && json.data.length){
                this.loadItems(json.data);
                this.checkLoadPage();
            }else{
                this.emptyLoadContent();
            }
        }.bind(this));
    },
    emptyLoadContent: function(){
        this.container.empty();
        this.container.removeClass("o2_homepage_area_content_loading").removeClass("icon_loading");
        this.content.pageAreaNode.empty();
        //this.itemContentNode.addClass("o2_homepage_task_area_content_empty").addClass("icon_notask");
        this.content.noItemNode = new Element("div.o2_homepage_hotpic_area_content_empty_node", {"text": this.app.lp.noInforItem}).inject(this.container);
        var m = (this.container.getSize().y- this.content.noItemNode.getSize().y)/2;
        this.content.noItemNode.setStyle("margin-top", ""+m+"px");

        this.content.isLoading = false;
    },

    loadItemRow: function(table, d, i){
        var row = table.insertRow(-1).addClass("o2_homepage_task_item_row");
        var idx = (this.page-1)*this.pageSize+i+1;
        var idxShow = (idx>99) ? "···" : idx;

        var cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_number");
        var numberNode = new Element("div.o2_homepage_task_item_number", {"text": idxShow, "title":idx}).inject(cell);

        //var subject = "<span>["+d.processName+"]</span> "+(d.title || this.app.lp.noSubject);
        var subject = o2.txt(d.title || this.app.lp.noSubject);
        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_subject");
        new Element("div.o2_homepage_task_item_subject", {"html": subject, "title":subject}).inject(cell);

        var creatorPerson = o2.name.cn(d.creatorPerson)
        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_creator");
        new Element("div.o2_homepage_task_item_creator", {"text": creatorPerson, "title": this.app.lp.publishPerson+": "+creatorPerson}).inject(cell);

        var time = (d.publishTime || d.createTime).substr(0,10);
        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_time");
        new Element("div.o2_homepage_task_item_time", {"text": time, "title": this.app.lp.publishTime+": "+time}).inject(cell);

        return row;
    },

    checkLoadPage: function(){
        if (this.itemCount){
            this.getPageCount();
            this.loadPage(this.itemPageArea);
        }
    },
    getPageCount:function(){
        var n = this.itemCount/this.pageSize;
        var nInt = n.toInt();
        this.pages = (nInt===n) ? nInt : nInt+1;
    },
    open: function(e, d){
        var id = d.infoId || d.id;
        var options = {"documentId": id, "docTitle": d.title, "appId": "cms.Document"+id};
        layout.openApplication(e, "cms.Document", options);
    },
});
