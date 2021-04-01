MWF.xApplication.AppMarketV2.ApplicationsContent = new Class({
    Implements: [Options, Events],
    options: {
        "view": "applicationsContent.html"
    },
    initialize: function(app, container, options){
        this.setOptions(options);
        this.app = app;
        this.container = container;
        this.viewPath = this.app.path+this.app.options.style+"/"+this.options.view;
        debugger;
        this.querydata = {};
        this.currentcategory = {"name": this.app.lp.all,"count":0};
        this.load();
    },
    load: function(){
        debugger;
        this.container.loadHtml(this.viewPath, {"bind": {"lp": this.app.lp}, "module": this}, function(){
            this.loadApplication(function(){
                this.fireEvent("load");
            }.bind(this));
        }.bind(this));
    },    

    loadApplication: function(callback){
        debugger;
        if (!this.isLoading){
            if (!this.applicationsContentV){
                this.applicationsContentV = new MWF.xApplication.AppMarketV2.ApplicationsContent.Applications(this, {
                    "onLoad": function(){ if (callback) callback(); }
                });
            }else{
                this.applicationsContentV.load();
            }
        }
    },
    focusAppSearch: function(){
        this.searchAppNode.addClass("layout_content_taskbar_area_search_box_focus");
        this.searchAppNode.addClass("mainColor_border");
        this.searchAppIconNode.addClass("icon_search_focus");
    },
    blurAppSearch: function(){
        this.searchAppNode.removeClass("layout_content_taskbar_area_search_box_focus");
        this.searchAppNode.removeClass("mainColor_border");
        this.searchAppIconNode.removeClass("icon_search_focus");
    },
    searchAppInputKeyDown: function(e){
        if (this.searchAppInputNode.get("value")){
            this.searchAppClearNode.addClass("icon_clear");
        }else{
            this.searchAppClearNode.removeClass("icon_clear");
        }
        if (e.keyCode===13) this.doAppSearch();
    },
    clearAppSearch: function(){
        this.searchAppInputNode.set("value", "");
        this.searchAppClearNode.removeClass("icon_clear");
        this.clearSearchResult();
    },
    doAppSearch: function(){
        var key = this.searchAppInputNode.get("value");
        if (key){
            this.querydata["name"]=key;  
            if (!this.applicationsContentV){
                this.applicationsContentV = new MWF.xApplication.AppMarketV2.ApplicationsContent.Applications(this, {
                    "onLoad": function(){ if (callback) callback(); }
                });
            }else{
                this.applicationsContentV.load();
            }
        }else{
            this.clearSearchResult();
        }
    },
    clearSearchResult: function(){
        this.querydata["name"]="";  
        if (!this.applicationsContentV){
                this.applicationsContentV = new MWF.xApplication.AppMarketV2.ApplicationsContent.Applications(this, {
                    "onLoad": function(){ if (callback) callback(); }
                });
        }else{
                this.applicationsContentV.load();
        }
    }
});

MWF.xApplication.AppMarketV2.ApplicationsContent.Applications= new Class({
    Implements: [Options, Events],
    options: {
        "type": "applications"
    },
    initialize: function(content, options){
        this.setOptions(options);
        this.content = content;
        this.app = this.content.app;
        this.actions = this.app.actions;
        this.container = this.content.container;
        this.page = 1;
        this.pageSize = 100;
        this.load();
        
    },
    load: function(){
        this.loadAppCategorys();
        this.loadApplications();
    },
    loadAppCategorys: function(){
        this.actions.MarketAction.listCategory(function(json){
            if (json.data && json.data.valueList){
                this.showCategorys(json.data.valueList);
            }
            this.fireEvent("load");
        }.bind(this));
    },
    loadApplications: function(){
        this.emptyLoadContent();
        this.actions.MarketAction.listPaging(this.page, this.pageSize, this.content.querydata,function(json){
            if (json.data && json.data.length){
                this.content.currentcategory["name"] = this.content.querydata.category==""||!(this.content.querydata.category)? this.app.lp.all :this.content.querydata.category;
                this.content.currentcategory["count"] = json.count;
                this.showApplications(json.data);
            }
            this.fireEvent("load");
        }.bind(this));
    },
    reload: function(){
        if (!this.content.isLoading) {
            this.loadAppCategorys();
            this.loadApplications();
        }
    },
    emptyLoadContent: function(){
        this.content.appList.empty();
        //this.container.removeClass("o2_homepage_area_content_loading").removeClass("icon_loading");
        //this.content.noItemNode = new Element("div.o2_appMarket_content_empty_node", {"text": this.app.lp.noRecommend}).inject(this.container);
        //var m = (this.content.contentHeight- this.content.noItemNode.getSize().y)/2;
        //this.content.noItemNode.setStyle("margin-top", ""+m+"px");

        this.content.isLoading = false;
    },
    showCategorys:function(data){
        var categorysdiv = this.content.appCategory;
        categorysdiv.empty();
        this.loadCertainCategory(categorysdiv, this.app.lp.all );
        data.each(function(d,i){
            this.loadCertainCategory(categorysdiv,d)
        }.bind(this))

    },
    loadCertainCategory:function(categorysdiv,d){
        var _self = this;
        
        var categorydiv = new Element("span",{"text":d,"class":"o2_appmarket_appcategory"}).inject(categorysdiv);
        categorydiv.store("data",d);
        if (_self.content.querydata["category"]){
            if ((_self.content.querydata["category"]=="" && d==_self.app.lp.all )||(_self.content.querydata["category"]==d)){
                categorydiv.removeClass("o2_appmarket_appcategory").addClass("mainColor_color").addClass("o2_appmarket_appcategory_current");
            }
        }
        categorydiv.addEvents({
            "mouseover":function(){
                this.addClass("o2_appmarket_appcategory_tab_over");
            },
            "mouseout":function(){
                this.removeClass("o2_appmarket_appcategory_tab_over");
            },
            "click":function(e){
                var d = this.retrieve("data");
                this.getParent().getElements(".o2_appmarket_appcategory_current").removeClass("mainColor_color").removeClass("o2_appmarket_appcategory_current").addClass("o2_appmarket_appcategory");
                this.removeClass("o2_appmarket_appcategory").addClass("mainColor_color").addClass("o2_appmarket_appcategory_current");
                if (d){
                    if ( d== _self.app.lp.all ){
                        _self.content.querydata["category"]="";                        
                    }else{
                        _self.content.querydata["category"]=d;
                    }
                    var key = _self.content.searchAppInputNode.get("value");
                    if (key==undefined) key="";
                    _self.content.querydata["name"]=key;     
                    _self.loadApplications();
                }
            }
        })
    },
    showApplications: function(data){
        //show category count
        //this.content.appCategory_count.empty();
        //new Element("div",{"text":this.content.currentcategory.name+"("+this.content.currentcategory.count+")"}).inject(this.content.appCategory_count);        
        var appsdiv = this.content.appList;
        var appsdivwidth= appsdiv.clientWidth-40;
        //appwidth = (appsdivwidth-200)/7;
        rowappnum = parseInt(appsdivwidth/240);
        rowappmargin = (appsdivwidth/240-rowappnum)  * 240  / (rowappnum-1);
        if (rowappmargin<10){
            rowappnum = rowappnum -1;
            rowappmargin = (appsdivwidth/240-rowappnum)  * 240  / (rowappnum-1)
        }
        //appsdiv.setStyle("width","calc("+appwidth+"px)");
        //appsdiv.setStyle("margin-left","10px");
        data.each(function(d, i){
            this.loadCertainApplication(appsdiv, d, i,rowappnum,rowappmargin);
        }.bind(this));
    },
    loadCertainApplication: function(appsdiv, d, i,rowappnum,rowappmargin){
        //app 排列 begin
       var applicationdiv = new Element("div",{"class":"o2_appmarket_application"}).inject(appsdiv);
 
       if ((i+1)%rowappnum!=0){
            applicationdiv.setStyle("margin-right",rowappmargin+"px");
       }else{
            applicationdiv.setStyle("margin-right","30px");
       }
       var applicationicon = new Element("div",{"class":"o2_appmarket_application_icon"}).inject(applicationdiv);
       applicationicon.setStyle("background-image", "url(data:image/png;base64,"+d.icon+")");
       var applicationinfo = new Element("div",{"class":"o2_appmarket_application_info"}).inject(applicationdiv);
       var applicationinfo_name = new Element("div",{"text":d.name,"class":"o2_appmarket_application_info_name"}).inject(applicationinfo);
       //var applicationinfo_recommend = new Element("div",{"text":d.recommend,"class":"o2_appmarket_application_info_recommend"}).inject(applicationinfo);
       //推荐指数改为显示评星
       var applicationinfo_star = new Element("div",{"class":"o2_appmarket_application_info_recommend"}).inject(applicationinfo);
       var grade = d.grade;
	   var intgrade = parseInt(grade);
	   var dotgrade = grade - intgrade;
	   for (var tmpnum=0;tmpnum<intgrade;tmpnum++){
			new Element("img",{"src":this.app.iconPath+"blackfiveangular.png","class":"o2_appmarket_application_info_starpic"}).inject(applicationinfo_star)
	   }
	  if (dotgrade>=0.5){
			new Element("img",{"src":this.app.iconPath+"halffiveangular.png","class":"o2_appmarket_application_info_starpic"}).inject(applicationinfo_star);
			intgrade++;
	   }
	   for (var tmpnum=0;tmpnum<5-intgrade;tmpnum++){
			new Element("img",{"src":this.app.iconPath+"whitefiveangular.png","class":"o2_appmarket_application_info_starpic"}).inject(applicationinfo_star);
		}
       var applicationinfo_category = new Element("div",{"text":d.category,"class":"o2_appmarket_application_info_category"}).inject(applicationinfo);
       var applicationinfo_bottom = new Element("div",{"class":"o2_appmarket_application_info_bottom"}).inject(applicationinfo);
       var applicationinfo_bottom_free = new Element("div",{"text":d.price==0?"Free":d.price+"","class":"o2_appmarket_application_info_bottom_free"}).inject(applicationinfo_bottom);
       var applicationinfo_bottom_open = new Element("div",{"class":"o2_appmarket_application_info_bottom_button mainColor_bg"}).inject(applicationinfo_bottom);
       var bottomtext =this.app.lp.setup;
       if (d.installedVersion && d.installedVersion!=""){
           if (d.installedVersion==d.version){
                bottomtext = this.app.lp.setupDone;
           }else{
                bottomtext = this.app.lp.update;
           }
       }
       var applicationinfo_bottom_open_text = new Element("div",{"text":bottomtext,"class":"o2_appmarket_application_info_bottom_button_text"}).inject(applicationinfo_bottom_open);
        var _self = this;
        applicationicon.store("data", d);
        applicationicon.addEvents({
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

        applicationinfo_bottom_open.store("data",d);
        applicationinfo_bottom_open.addEvents({
            "click":function(e){
                var d = this.retrieve("data");
                if (d){
                    _self.installapp(e,d);
                }
            }
        })
    },
    installapp:function(e,d){
            var p = e.target.getPosition();
            var tmpe = {"event": {"x": p.x+40, "y": p.y}};
            var confirmtitle = d.installedVersion==""?this.app.lp.confirmsetupTitle:this.app.lp.confirmupdateTitle;
            var confirmcontent = d.installedVersion==""?this.app.lp.confirmsetupContent:this.app.lp.confirmupdateContent;
            var _self = this;
            _self.app.confirm("warn", tmpe, confirmtitle, confirmcontent, 300, 120, function(){
                _self.app.mask();
                //this.createLoading(this.container,true);  
                //alert("after createLoading")          
                _self.actions.MarketAction.installOrUpdate(
                    d.id,
                function( json ){ 
                    data = json.data; 
                    _self.app.notice(d.name+" "+_self.app.lp.setupSuccess, "success");
                    _self.app.unmask();
                    //this.clearLoading()
                }.bind(_self),
                function( json ){ 
                    data = json.data; 
                    _self.app.unmask();
                    //this.clearLoading()
                }.bind(_self),
                    true
                );
                this.close();
            }, function(){
                this.close();
            }, null, null, "o2");        
    },
    open: function(e, d){
        var apppar = {};
        apppar["appid"] = d.id;
        apppar["appname"] = d.name;
        layout.openApplication(e, "AppMarketV2.Application", apppar);
    },
    createLoading: function(node,mask){
        //alert("createloading")
        this.app.content.mask({
            "destroyOnHide": true,
			"style": {
				"opacity": 0.7,
				"background-color": "#999"
            },
            "loading": true
            
		});
        //if (mask) this.mask.loadNode(node);
    },
    clearLoading: function(){
        /*
        if (this.loadingAreaNode){
            this.loadingAreaNode.destroy();
            this.loadingAreaNode = null;
        }
        */
       this.app.content.unmask();
       // this.dlg.button.setStyle("display", "block");
    }
});