MWF.xApplication.AppMarketV2.RecommendContent = new Class({
    Implements: [Options, Events],
    options: {
        "view": "recommendContent.html"
    },
    initialize: function(app, container, options){
        this.setOptions(options);
        this.app = app;
        this.container = container;
        this.viewPath = this.app.path+this.app.options.style+"/"+this.options.view;
        debugger;
        this.load();
    },
    load: function(){
        debugger;
        this.container.loadHtml(this.viewPath, {"bind": {"lp": this.app.lp}, "module": this}, function(){
            this.loadRecommend(function(){
                this.fireEvent("load");
            }.bind(this));
        }.bind(this));
    },    

    loadRecommend: function(callback){
        debugger;
        if (!this.isLoading){
            if (!this.topRecommendContentV){
                this.topRecommendContentV = new MWF.xApplication.AppMarketV2.RecommendContent.Recommend(this, {
                    "onLoad": function(){ if (callback) callback(); }
                });
            }else{
                this.topRecommendContentV.load();
            }
        }
    }
});

MWF.xApplication.AppMarketV2.RecommendContent.Recommend= new Class({
    Implements: [Options, Events],
    options: {
        "type": "recommend"
    },
    initialize: function(content, options){
        this.setOptions(options);
        this.content = content;
        this.app = this.content.app;
        this.actions = this.app.actions;
        this.container = this.content.container;
        this.page = 1;
        this.pageSize = 3;
        this.querydata = {"orderBy":"recommend","isAsc":"true"};
        this.load();
        
    },
    load: function(){
        debugger;
        this.loadItemsRes();
    },
    loadItemsRes: function(){
        debugger;
        this.actions.MarketAction.listPaging(this.page, this.pageSize, this.querydata,function(json){
            if (json.data && json.data.length){
                this.loadItems(json.data);
            }else{
                this.emptyLoadContent();
            }
            this.fireEvent("load");
        }.bind(this));
    },
    reload: function(){
        if (!this.content.isLoading) {
            this.loadItemsRes();
        }
    },
    emptyLoadContent: function(){
        this.container.empty();
        this.container.removeClass("o2_homepage_area_content_loading").removeClass("icon_loading");
        //this.itemContentNode.addClass("o2_homepage_task_area_content_empty").addClass("icon_notask");
        this.content.noItemNode = new Element("div.o2_appMarket_content_empty_node", {"text": this.app.lp.noRecommend}).inject(this.container);
        var m = (this.content.contentHeight- this.content.noItemNode.getSize().y)/2;
        this.content.noItemNode.setStyle("margin-top", ""+m+"px");

        this.content.isLoading = false;
    },
    loadItems: function(data){
        debugger;
        var recommendWidth = this.content.topRecommendContent.clientWidth;
        var recommendLeftWidth = (recommendWidth - 80-10)*0.65;
        var recommendRightWidth = (recommendWidth - 80-10)*0.35;
        this.content.leftCoverNode.setStyle("width",recommendLeftWidth+"px");
        this.content.rightCoverNode.setStyle("width",recommendRightWidth+"px");
        data.each(function(d, i){
            this.loadItem(d, i);
        }.bind(this));
    },
    loadItem: function(d, i){
        var app;
        var apppar;
        debugger;
        if (i==0){
            app = this.content.recommendBiggestPic;
            apptext = this.content.recommendBiggestTitle;
            apppar = this.content.leftCoverNode;
        }
        if (i==1){
            app = this.content.recommendRightTopPic;
            apptext = this.content.recommendRightTopTitle;
            apppar = this.content.rightTopCoverNode;
        }
        if (i==2){
            app = this.content.recommendRightBottomPic;
            apptext = this.content.recommendRightBottomTitle;
            apppar = this.content.rightBottomCoverNode;
        }
        //获取对应indexPic图片
        this.actions.MarketAction.getCoverPic(d.id,function(json){
            debugger;
            if (json.data && json.data.value){
                app.setStyle("background-image", "url(data:image/png;base64,"+json.data.value+")");
            }
        }.bind(this));
        apptext.set("text",d.name);
       

        var _self = this;
        apppar.store("data", d);
        apppar.addEvents({
            "mouseover": function(){
            },
            "mouseout": function(){
            },
            "click": function(e){
                var d = this.retrieve("data");
                if (d) {
                    _self.open(e, d);
                }
            }
        })
    },
    open: function(e, d){
        debugger;
        var apppar = {};
        apppar["appid"] = d.id;
        layout.openApplication(e, "AppMarketV2.Application", apppar);
    },

});