MWF.require("o2.widget.Paging", null, false);
MWF.xApplication.SearchDocument.SearchView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "query": ""
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "../x_component_SearchDocument/$SearchView/";

        this.app = app;
        this.container = $(node);
        this.load();
    },
    load: function(){

        this.pageSize = 20;
        this.currentKey = "";

        this.workPageNum = 1;
        this.workTotal = 0;
        this.workList = [];

        this.docPageNum = 1;
        this.docTotal = 0;
        this.docList = [];

        var url = this.path+this.options.style+"/view.html";
        this.container.loadHtml(url, {
            "bind": {
                "lp": this.app.lp,
                "data": {"query": this.options.query || ""}
            },
            "module": this
        }, function(){
            this.loadTabTitles();
            this.search();
        }.bind(this));
    },
    reload: function(){
        this.container.empty();
        this.load();
    },
    destroy : function(){
        // this.app.removeEvent("resize", this.resetNodeSizeFun );
        this.container.empty();
    },

    searchKeydown: function(e){
        if( e.keyCode === 13 ){
            this.search();
        }
    },
    getQuery: function(){
        return this.searchInput.get("value") || "";
    },
    search: function(){
        this.currentKey = this.searchInput.get("value") || "";
        if( this.currentKey ){
            o2.Actions.load("x_general_assemble_control").SearchAction.search({
                query: this.currentKey,
                page: 1,
                size: this.pageSize,
                forceDefaultSearch: true
            }).then(function(json){
                this.docPageNum = 1;
                this.workPageNum = 1;

                json.data.processPlatform.data.map(function (d) {
                    d.unitName = ( d.creatorUnit || "" ).split("@")[0];
                    d.identityName = ( d.creatorIdentity || "" ).split("@")[0];
                    return d;
                });
                this.workList = json.data.processPlatform.data;
                this.workTotal = json.data.processPlatform.count;

                this.workTotalNode.set("text", this.workTotal);
                this.loadWorkList(this.workList);
                this.loadWorkPagination();

                json.data.cms.data.map(function (d) {
                    d.unitName = ( d.creatorUnitName || "" ).split("@")[0];
                    d.identityName = ( d.creatorPerson || "" ).split("@")[0];
                    return d;
                });
                this.docList = json.data.cms.data;
                this.docTotal = json.data.cms.count;

                this.docTotalNode.set("text", this.docTotal);
                this.loadDocList(this.docList);
                this.loadDocPagination();
            }.bind(this));
        }else{
            this.workList = [];
            this.workTotal = 0;
            this.workTotalNode.set("text", this.workTotal);
            this.loadWorkList();
            this.loadWorkPagination();

            this.docList = [];
            this.docTotal = 0;
            this.docTotalNode.set("text", this.docTotal);
            this.loadDocList();
            this.loadDocPagination();

        }
    },


    openWork: function(workCompleted, work, event, row){
        o2.api.page.openWork(workCompleted || work);
    },
    listWorkPaging: function () {
        o2.Actions.load("x_processplatform_assemble_surface").ReviewAction.v2Search({
            query: this.currentKey,
            page: this.workPageNum,
            size: this.pageSize
        }).then(function(json){
            json.data.map(function (d) {
                d.unitName = ( d.creatorUnit || "" ).split("@")[0];
                d.identityName = ( d.creatorIdentity || "" ).split("@")[0];
                return d;
            });
            this.workList = json.data;
            this.workTotal = json.count;

            this.workTotalNode.set("text", this.workTotal);
            this.loadWorkList(this.workList);
        }.bind(this));
    },
    loadWorkList: function(data){
        this.workListNode.empty();
        debugger;
        this.workListNode.loadHtml(this.path+this.options.style+"/workList.html",
            {
                "bind": {"lp": this.app.lp, "data": data},
                "module": this,
                "reload": true
            },
            function(){

            }.bind(this)
        );
    },
    loadWorkPagination: function(){
        this.workPaginationNode.empty();
        this.workPaging = new o2.widget.Paging(this.workPaginationNode, {
            style: "blue_round",
            countPerPage: this.pageSize,
            visiblePages: 9,
            currentPage: 1,
            itemSize: this.workTotal,
            useMainColor: true,
            text: {
                firstPage: this.app.lp.firstPage,
                lastPage: this.app.lp.lastPage
            },
            // pageSize: pageSize,
            onJumpingPage: function (pageNum) {
                this.workPageNum = pageNum;
                this.listWorkPaging(pageNum);
            }.bind(this)
        });
        this.workPaging.load();
    },

    openDoc: function(docId, event, row){
        debugger;
        o2.api.page.openDocument(docId);
    },
    listDocPaging: function() {
        o2.Actions.load("x_cms_assemble_control").ReviewAction.v2Search({
            query: this.currentKey,
            page: this.docPageNum,
            size: this.pageSize
        }).then(function(json){
            json.data.map(function (d) {
                // d.hightlineTitle = brightenKeyword(d.title || "", currentKey.value);
                d.unitName = ( d.creatorUnitName || "" ).split("@")[0];
                d.identityName = ( d.creatorPerson || "" ).split("@")[0];
                return d;
            });
            this.docList = json.data;
            this.docTotal = json.count;

            this.docTotalNode.set("text", this.docTotal);
            this.loadDocList(this.docList);
        }.bind(this));
    },
    loadDocList: function(data){
        this.docListNode.empty();
        this.docListNode.loadHtml(this.path+this.options.style+"/docList.html",
            {
                "bind": {"lp": this.app.lp, "data": data},
                "module": this,
                "reload": true
            },
            function(){

            }.bind(this)
        );
    },
    loadDocPagination: function(){
        this.docPaginationNode.empty();
        this.docPaging = new o2.widget.Paging(this.docPaginationNode, {
            style: "blue_round",
            countPerPage: this.pageSize,
            visiblePages: 9,
            currentPage: 1,
            itemSize: this.docTotal,
            useMainColor: true,
            text: {
                firstPage: this.app.lp.firstPage,
                lastPage: this.app.lp.lastPage
            },
            // pageSize: pageSize,
            onJumpingPage: function (pageNum) {
                this.docPageNum = pageNum;
                this.listDocPaging(pageNum);
            }.bind(this)
        });
        this.docPaging.load();
    },

    loadTabTitles: function(){
        this.tabTitles.getElements(".tabTitle").each(function (tab, i) {
            tab.addEvent("click", function (e) {
                if( this.currentTab === tab )return;
                if( this.currentTab ){
                    this.currentTab.removeClass("mainColor_bg");
                    this.currentTab.removeClass("tabTitleCurrent");
                }
                tab.addClass("mainColor_bg");
                tab.addClass("tabTitleCurrent");
                this.currentTab = tab;

                if( this.currentTabContent ){
                    this.currentTabContent.hide();
                }
                var tabContentName = tab.get("data-o2-tab-content");
                this.currentTabContent = this[tabContentName];
                if( this.currentTabContent )this.currentTabContent.show();


            }.bind(this))
            if( i === 0 )tab.click();
        }.bind(this));
    },
    iconOver: function(e){
        e.target.addClass('mainColor_color');
    },
    iconOut: function(e){
        e.target.removeClass('mainColor_color');
    },

    inputOver: function(e){
        this.app.getEventTarget(e, "view_inputArea").addClass('mainColor_border');
    },
    inputOut: function(e){
        this.app.getEventTarget(e, "view_inputArea").removeClass('mainColor_border');
    },

    rowOver: function(e){
        this.app.getEventTarget(e, "row").addClass('mainColor_bg_opacity');
    },
    rowOut: function(e){
        this.app.getEventTarget(e, "row").removeClass('mainColor_bg_opacity');
    },
    recordStatus: function(){
        return {
            "query": this.getQuery()
        };
    }
});