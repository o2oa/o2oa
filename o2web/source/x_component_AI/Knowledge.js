MWF.require("MWF.widget.ScriptArea", null, false);
MWF.xApplication.AI.Knowledge = new Class({
    Extends: MWF.xApplication.AI.Setting,
    Implements: [Options, Events],
    options: {
        "view" : "knowledge.html",
        "style": "default",
        "itemHeight": 40
    },
    initialize: function(app, container, options){
        this.setOptions(options);
        this.app = app;
        this.container = container;
        this.viewPath = this.app.path+this.app.options.style+"/view/"+this.options.view;
        this.size = 15;
        this.page = 1;
        this.lp = this.app.lp;
        this.action = o2.Actions.load("x_ai_assemble_control");
        this.load();
    },
    load: async function(){

        const config = await this.action.ConfigAction.getConfig();
        this.config = config.data;
        this.action.ConfigAction.listModelPaging(1,100, function( json ){
            this.container.loadHtml(this.viewPath, {"bind": {"lp": this.app.lp,"config":this.app.config}, "module": this}, function(){
                this.loadList();
            }.bind(this));
        }.bind(this));

    },
    loadList: function(){


        var _self = this;
        this.listTempleteUrl = this.app.path + this.app.options.style + "/view/kn_list.html";
        this.loadData().then(function(data){
            _self.hide();
            _self.loadPage();
            _self.loadItems(data);
        });

    },

    hide: function(){
        if (this.node) this.node.destroy();
    },
    loadItems: function(data){
        this.dataList = data;
        this.listContentNode.loadHtml(this.listTempleteUrl, {"bind": {"lp": this.lp,  "data": data}, "module": this}, function(){
            this.node = this.listContentNode.getFirst();
            this.container.loadCss(this.app.path + this.app.options.style + "/list.css");
        }.bind(this));
    },
    refresh: function(){
        this.hide();
        this.loadList();
    },
    hide: function(){
        if (this.node) this.node.destroy();
    },
    loadPage: function(){
        var totalCount = this.total;
        var pages = totalCount/this.size;

        var pageCount = pages.toInt();
        if (pages !== pageCount) pageCount = pageCount+1;
        this.pageCount = pageCount;
        var size = this.listBottomNode.getSize();
        var maxPageSize = 500;//size.x*0.8;
        maxPageSize = maxPageSize - 80*2-24*2-10*3;
        var maxPageCount = (maxPageSize/34).toInt();

        this.loadPageNode(pageCount, maxPageCount);
    },
    loadPageNode: function(pageCount, maxPageCount){
        var pageStart = 1;
        var pageEnd = pageCount;
        if (pageCount>maxPageCount){
            var halfCount = (maxPageCount/2).toInt();
            pageStart = Math.max(this.page-halfCount, 1);
            pageEnd = pageStart+maxPageCount-1;
            pageEnd = Math.min(pageEnd, pageCount);
            pageStart = pageEnd - maxPageCount+1;
        }
        this.pageNumberAreaNode.empty();
        var _self = this;
        for (var i=pageStart; i<=pageEnd; i++){
            var node = new Element("div.pageItem", {
                "text": i,
                "events": { "click": function(){_self.gotoPage(this.get("text"));} }
            }).inject(this.pageNumberAreaNode);
            if (i==this.page) node.addClass("mainColor_bg");
        }
    },
    nextPage: function(){
        this.page++;
        if (this.page>this.pageCount) this.page = this.pageCount;
        this.gotoPage(this.page);
    },
    prevPage: function(){
        this.page--;
        if (this.page<1) this.page = 1;
        this.gotoPage(this.page);
    },
    firstPage: function(){
        this.gotoPage(1);
    },
    lastPage: function(){
        this.gotoPage(this.pageCount);
    },
    gotoPage: function(page){
        this.page = page;
        this.hide();
        this.loadList();
    },
    loadData: function(){
        var _self = this;

        return this.action.IndexAction.listIndexPaging(this.page, this.size).then(function(json){
            _self.fireEvent("loadData");
            _self.total = json.count;
            return _self._fixData(json.data);
        }.bind(this));
    },
    _fixData : function (dataList){
        dataList.each(function (data){
            data.createPerson = data.referenceCreatorPerson.split("@")[0];
        }.bind(this));
        return dataList;
    },
    remove : function (id,ev){
        $OOUI.confirm.warn(this.lp.common.removetitle, this.lp.common.removeconfirm, null, ev.target, 'left top').then(({ dlg, status }) => {
            if (status === 'ok') {
                this.action.IndexAction.deleteDocIndex(id, function( json ){
                    this.refresh();
                    dlg.close();
                }.bind(this));
            }
        });

    },
    open : function (work,workCompleted,jobId){
        var options = {
            "workId": work,
            "workCompletedId": workCompleted,
            "appId":  "process.Work" + work
        };
        if(o2.typeOf(jobId)=="string"){
            options.jobId = jobId;
        }

        layout.desktop.openApplication(null, "process.Work", options);

    }
});
