MWF.xApplication.Search.options.multitask = false;
MWF.xApplication.Search.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style1": "default",
		"style": "default",
		"name": "Search",
		"icon": "icon.png",
		"width": "1200",
		"height": "700",
		"isResize": true,
		"isMax": true,
        "pageCount": 15,
        "key": "",
		"title": MWF.xApplication.Search.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Search.LP;
        if (this.status && this.status.key) this.options.key = this.status.key;
	},
    initPage: function(){
        this.pageCount = this.options.pageCount;
        this.pages = 1;
        this.currentPage = 1;
    },
    recordStatus: function(){
        if (this.input){
            var v = this.input.getValue();
            return {"key": v};
        }
        return {};
    },
	loadApplication: function(callback){
        this.result = [];
        this.items = [];
        this.initPage();
        this.createLayout();
        if (callback) callback();
	},
    createLayout: function(){
		this.searchArea = new Element("div", {"styles": this.css.searchArea}).inject(this.content);
		this.resultArea = new Element("div", {"styles": this.css.resultArea}).inject(this.content);
		this.setSearchAreaSize();
		this.setSearchAreaSizeFun = this.setSearchAreaSize.bind(this);
        this.addEvent("resize", this.setSearchAreaSizeFun);
		this.createResultInfor();
        this.createResultContent();
        this.createResultPageArea();
        this.createSearchBar();
	},
    setSearchAreaSize: function(){
        var searchSize = this.searchArea.getSize();
        var contentSize = this.content.getSize();
        var y = contentSize.y-searchSize.y;
        this.resultArea.setStyle("height", ""+y+"px");
    },
    createSearchBar: function(){
        this.searchBarNode = new Element("div", {"styles": this.css.searchBarNode}).inject(this.searchArea);
        this.logoNode = new Element("div", {"styles": this.css.logoNode}).inject(this.searchBarNode);
        this.searchInputActionArea = new Element("div", {"styles": this.css.searchInputActionArea}).inject(this.searchBarNode);

        MWF.require("MWF.widget.SearchInput", function(){
            this.input = new MWF.widget.SearchInput({
                "onSearch": function(key){
                    this.search(key)
                }.bind(this)
            });
            this.input.inject(this.searchInputActionArea);

            if (this.options.key){
                this.input.setValue(this.options.key);
                this.input.doSearch();
            }
        }.bind(this));
	},

    createResultInfor: function(){
        this.resultInfor = new Element("div", {
            "styles": this.css.resultInfor,
            "text": this.lp.infor
        }).inject(this.resultArea);
    },
    createResultContent: function(){
        this.resultContent = new Element("div", {
            "styles": this.css.resultContent
        }).inject(this.resultArea);
    },
    createResultPageArea: function(){
        this.resultPageArea = new Element("div", {
            "styles": this.css.resultPageArea
        }).inject(this.resultArea);
    },

    search: function(key){
        var startDate = new Date();
        MWF.Actions.get("x_query_assemble_surface").search(key, function(json){
            var endDate = new Date();
            var t = endDate.getTime()-startDate.getTime();
            t = ((t/1000)*100).toInt()/100;
            var text = this.lp.searchInfor;
            text = text.replace("{count}", json.data.count||0);
            text = text.replace("{time}", t);
            this.resultInfor.set("text", text);
            this.resultInfor.setStyles(this.css.searchResultInfor);
            this.resultPageArea.empty();
            this.resultContent.empty();
            this.result = json.data.valueList;
            if (json.data.count){
                this.createPages();
                this.showResult();
            }
        }.bind(this));
    },
    createPages: function(){
        this.initPage();
        this.resultPageArea.empty();
        if (this.result.length){
            var v = this.result.length/this.pageCount;
            this.pages = (v>v.toInt()) ? v.toInt()+1 : v.toInt();
            this.currentPage = 1;
            var _self = this;
            for (var i=1; i<=this.pages; i++){
                var node = new Element("div", {"styles": this.css.pageItem, "text": i}).inject(this.resultPageArea);
                node.addEvent("click", function(){
                    _self.resultPageArea.getElement(":nth-child("+_self.currentPage+")").setStyles(_self.css.pageItem);
                    _self.gotoPage(this.get("text"));

                });
            }
        }
    },
    gotoPage: function(i){
        this.currentPage = i;
        this.showResult();
        this.resultArea.scrollTop = 0;
    },
    showResult: function(){
        var startIdx = (this.currentPage-1)*this.pageCount;
        var endIdx = this.currentPage*this.pageCount-1;
        this.resultPageArea.getElement(":nth-child("+this.currentPage+")").setStyles(this.css.pageItem_current);

        this.resultContent.empty();
        var n = Math.min(this.result.length-1, endIdx);

        var ids = this.result.slice(startIdx, n+1);
        MWF.Actions.get("x_query_assemble_surface").listSearchEntry({
            "entryList": ids
        }, function(json){
            var datas = json.data;
            datas.each(function(d){
                new MWF.xApplication.Search.ResaultItem(this, d);
            }.bind(this));
        }.bind(this));

        // for (var i=startIdx; i<=n; i++){
        //     var d = this.result[i];
        //     new MWF.xApplication.Search.ResaultItem(this, d);
        // }
    }
});

MWF.xApplication.Search.ResaultItem = new Class({
    initialize: function(app, data){
        this.app = app;
        this.content = this.app.resultContent;
        this.lp = this.app.lp;
        this.css = this.app.css;
        this.data = data;
        this.checkPermission(function(){
            this.load();
        }.bind(this));
    },
    checkPermission: function(callback){
        if (!this.data.permission){
            if (this.data.type==="work"){
                MWF.Actions.get("x_processplatform_assemble_surface").getWorkControl(this.data.reference, function(){
                    this.data.permission = "y";
                    if (callback) callback();
                }.bind(this), function(){
                    this.data.permission = "n";
                    if (callback) callback();
                }.bind(this))
            }
            if (this.data.type==="workCompleted"){
                MWF.Actions.get("x_processplatform_assemble_surface").getWorkControl(this.data.reference, function(){
                    this.data.permission = "y";
                    if (callback) callback();
                }.bind(this), function(){
                    this.data.permission = "n";
                    if (callback) callback();
                }.bind(this))
            }
            if (this.data.type==="cms"){
                //getDocumentControl
                MWF.Actions.get("x_cms_assemble_control").getDocumentControl(this.data.reference, function(json){
                    if (json.data.control.allowVisit){
                        this.data.permission = "y";
                    }else{
                        this.data.permission = "n";
                    }
                    //this.data.permission = "y";
                    if (callback) callback();
                }.bind(this), function(){
                    this.data.permission = "n";
                    if (callback) callback();
                }.bind(this))
            }
        }else{
            if (callback) callback();
        }
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.resaultItemNode}).inject(this.content);
        this.titleNode = new Element("div", {"styles": this.css.resaultItemTitleNode}).inject(this.node);
        this.summaryNode = new Element("div", {"styles": this.css.resaultItemSummaryNode}).inject(this.node);
        this.inforNode = new Element("div", {"styles": this.css.resaultItemInforNode}).inject(this.node);
        this.loadTitle();
        this.loadSummary();
        this.loadInfor();
    },
    loadTitle: function(){
        if (this.data.permission==="n"){
            this.titleNode.setStyles(this.css.resaultItemTitleNode_gray);
            this.titleNode.set("text", (this.data.title) ? this.data.title+" ("+this.lp.refuse+")" : this.lp.nonamed+" ("+this.lp.refuse+")");
        }else{
            this.titleNode.set("text", this.data.title || this.lp.nonamed);
            this.titleNode.addEvents({
                "mouseover": function(){this.setStyle("text-decoration", "underline")},
                "mouseout": function(){this.setStyle("text-decoration", "none");},
                "click": function(e){
                    this.openItem(e);
                }.bind(this)
            });
        }
    },
    openItem: function(e){
        if (this.data.type==="work"){
            layout.desktop.openApplication(e, "process.Work", {"workId": this.data.reference, "appId": this.data.reference, "docTitle": this.data.title || this.lp.nonamed});
        }
        if (this.data.type==="workCompleted"){
            layout.desktop.openApplication(e, "process.Work", {"workCompletedId": this.data.reference, "appId": this.data.reference, "docTitle": this.data.title || this.lp.nonamed});
        }
        if (this.data.type==="cms"){
            layout.desktop.openApplication(e, "cms.Document", {"documentId": this.data.reference, "appId": this.data.reference, "docTitle": this.data.title || this.lp.nonamed});
        }
    },
    loadSummary: function(){
        this.summaryNode.set("text", this.data.summary);
    },
    loadInfor: function(){
        var html = "";
        if (this.data.applicationName){
            html+="<span style='color:#006d21'>"+this.lp.processApplication+"</span><span>"+this.data.applicationName+"</span><span>&nbsp;&nbsp;&nbsp;&nbsp;</span>";
        }
        if (this.data.processName){
            html+="<span style='color:#006d21'>"+this.lp.process+"</span><span>"+this.data.processName+"</span><span>&nbsp;&nbsp;&nbsp;&nbsp;</span>";
        }
        if (this.data.appName){
            html+="<span style='color:#006d21'>"+this.lp.cmsApplication+"</span><span>"+this.data.appName+"</span><span>&nbsp;&nbsp;&nbsp;&nbsp;</span>";
        }
        if (this.data.categoryName){
            html+="<span style='color:#006d21'>"+this.lp.category+"</span><span>"+this.data.categoryName+"</span><span>&nbsp;&nbsp;&nbsp;&nbsp;</span>";
        }

        if (this.data.creatorPerson){
            html+="<span style='color:#006d21'>"+this.lp.creatorPerson+"</span><span>"+MWF.name.cn(this.data.creatorPerson)+"</span><span>&nbsp;&nbsp;&nbsp;&nbsp;</span>";
        }
        if (this.data.creatorUnit){
            html+="<span style='color:#006d21'>"+this.lp.unit+"</span><span>"+MWF.name.cn(this.data.creatorUnit)+"</span><span>&nbsp;&nbsp;&nbsp;&nbsp;</span>";
        }
        if (this.data.type==="workCompleted"){
            html+="<span style='color: #f27b5f'>"+this.lp.completed+"</span><span>&nbsp;&nbsp;&nbsp;&nbsp;</span>";
        }
        if (this.data.lastUpdateTime){
            html+="<span>"+this.data.lastUpdateTime+"</span><span>&nbsp;&nbsp;&nbsp;&nbsp;</span>";
        }
        this.inforNode.set("html", html);
    }
});
