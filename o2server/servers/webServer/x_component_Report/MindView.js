MWF.xApplication.Report = MWF.xApplication.Report || {};
MWF.xDesktop.requireApp("Template", "MDomItem", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MSelector", null, false);

MWF.require("MWF.widget.Identity", null,false);

MWF.xApplication.Report.MindView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },

    initialize: function (node, app, actions, options) {
        this.setOptions(options);
        this.app = app;
        this.lp = app.lp.keyWorkList;
        this.path = "/x_component_Report/$MindView/";
        this.loadCss();

        this.actions = actions;
        this.node = $(node);
    },
    loadCss: function () {
        this.cssPath = this.path + this.options.style + "/css.wcss";
        this._loadCss();
    },
    load: function () {
        this.createMiddleContent();
        //this.app.addEvent("resize", function(){
        //    this.resizeContent();
        //}.bind(this));
    },
    reload:function(year){
        this.currentYear = year;
        this.createMiddleContent();
    },
    createMiddleContent : function(){
        this.node.empty();
        this.middleContent = new Element("div.middleContent",{"styles":this.css.middleContent}).inject(this.node);
        this.viewNode = new Element("div.viewNode" ).inject(this.node);
        this.createNavi();
    },
    createNavi: function(){
        this.naviContent = new Element("div.naviContent",{"styles":this.css.naviContent}).inject(this.middleContent);

        this.naviRightContent = new Element("div.naviRightContent",{"styles":this.css.naviRightContent}).inject(this.middleContent);

        this.keyWorkNavi = new Element("div.naviNode",{
            "styles":this.css.naviNode,
            "text" : "按公司工作重点"
        }).inject(this.naviContent).addEvent("click", function() {
                this.departmentNavi.setStyles( this.css.naviNode );
                this.keyWorkNavi.setStyles( this.css.naviNode_current );
                this.toKeyWorkView();
            }.bind(this));

        this.departmentNavi = new Element("div.naviNode",{
            "styles":this.css.naviNode,
            "text" : "按责任部门"
        }).inject(this.naviContent).addEvent("click", function() {
                this.departmentNavi.setStyles( this.css.naviNode_current );
                this.keyWorkNavi.setStyles( this.css.naviNode );
                this.toDepartmentView();
            }.bind(this));

        this.keyWorkNavi.click();
    },
    toDepartmentView : function(){
        if(this.view)this.view.destroy();
        this.view = new MWF.xApplication.Report.MindView.DepartmentView(this.naviRightContent, this.viewNode, this);
        this.view.load();
    },
    toKeyWorkView : function(){
        if(this.view)this.view.destroy();
        this.view = new MWF.xApplication.Report.MindView.KeyWorkView(this.naviRightContent, this.viewNode, this);
        this.view.load();
    },
    destroy : function(){
        if(this.view){
            this.view.destroy();
        }
        this.node.empty();
    }

});

MWF.xApplication.Report.MindView.DepartmentView = new Class({
    initialize: function (actionNode, viewNode, explorer) {
        this.explorer = explorer;
        this.app = explorer.app;
        this.lp = explorer.lp;
        this.css = explorer.css;

        this.actions = explorer.actions;
        this.actionNode = actionNode;
        this.viewNode = viewNode;
    },
    load: function () {
        this.resizeContentFun = this.resizeContent.bind(this);
        this.createYearContent(
            this.resizeContentFun
        );
        this.app.addEvent("resize", this.resizeContentFun);
    },
    createYearContent:function(callback){
        this.yearContentList = new Element("div.yearContentList",{"styles":this.css.yearContentList}).inject(this.actionNode);
        this.app.strategyActions.listYearHasMeasure(function(json){
            if(json.type=="success"){
                if(json.data && json.data.valueList){
                    this.yearList = json.data.valueList;
                    this.yearList.each(function(d,i){
                        if(i<3){
                            new Element("div.year",{
                                "styles":this.css.year,
                                "value":d,
                                "name":d,
                                "text":d
                            }).inject(this.yearContentList).
                                addEvents({
                                    "click":function(){
                                        this.changeYearSelected(d);
                                        this.openList(d)
                                    }.bind(this)
                                })
                        }
                    }.bind(this));
                    if(this.yearList.length>3){
                        new Element("div.yearMore",{"styles":this.css.year,"id":"yearMore"}).inject(this.yearContentList).
                            setStyles({"width":"30px"}).set({"text":"..."}).
                            addEvents({
                                "click":function(){
                                    this.expandYears()
                                }.bind(this)
                            })
                    }

                    if(this.currentYear){
                        if(this.yearContentList.getElements("div[name='"+this.currentYear+"']").length>0){
                            this.yearContentList.getElements("div[name='"+this.currentYear+"']")[0].click();
                        }
                    }else{
                        if(this.yearContentList.getElements("div").length>0){
                            this.yearContentList.getElements("div")[0].click();
                        }
                    }


                    if(callback)callback()
                }
            }
        }.bind(this));
    },
    expandYears:function(){
        this.yearContentList.getElementById("yearMore").destroy();
        this.yearList.each(function(d,i){
            if(i>2){
                new Element("div.year",{
                    "styles":this.css.year,
                    "value":d,
                    "name":d,
                    "text":d
                }).inject(this.yearContentList).
                    addEvents({
                        "click":function(){
                            this.changeYearSelected(d);
                            this.openList(d)
                        }.bind(this)
                    })
            }
        }.bind(this));
        this.expend();
        this.yearContentList.addEvents({
            mouseenter : function(){
                this.expend();
            }.bind(this),
            mouseleave: function(){
                this.collapse();
            }.bind(this)
        })
    },
    expend: function(){
        this.actionNode.setStyles({
            "position" : "relative"
        });
        this.yearContentList.setStyles({
            "padding-bottom" : "15px",
            "height" : this.yearContentList.getScrollSize().y + "px",
            "position" : "absolute",
            "top" : "0px",
            "left" : "0px",
            "box-shadow": "0 0 8px 0 rgba(0,0,0,0.25)",
            "overflow" : "visible"
        });
    },
    collapse: function(){
        this.actionNode.setStyles({
            "position" : "static"
            //"overflow" : "hidden"
        });
        this.yearContentList.setStyles({
            "padding-bottom" : "0px",
            "height" : "60px",
            "position" : "static",
            "box-shadow": "none",
            "overflow" : "hidden"
        });
    },
    openList:function(v){
        this.currentYear = v;
        this.createViewContent();
        this.resizeContent();


    },
    changeYearSelected:function(str){
        this.yearContentList.getElements("div").each(function(d){
            if(d.get("text") == str){
                d.setStyles({"background-color":"#4990E2","color":"#FFFFFF"})
            }else{
                d.setStyles({"background-color":"","color":"#666666"})
            }
        }.bind(this))

    },
    //视图
    createViewContent:function(searchObj){
        var _self = this;
        if( this.view )this.view.destroy();
        if(this.viewContent)this.viewContent.destroy();
        this.viewContent = new Element("div.viewContent",{"styles":this.css.viewContent}).inject(this.viewNode);
        this.viewContentList = new Element("div.departmentContentList",{"styles":this.css.departmentContentList}).inject(this.viewContent);

        this.app.strategyActions.listMeasureDepartmentByYear( this.currentYear, function( json ){
           if( json.data && json.data.valueList ){
               json.data.valueList.each( function( v ){
                   var div = new Element("div", {
                       text : v.split("@")[0],
                       styles : this.css.departmentNode
                   }).inject( this.viewContentList );
                   div.addEvents({
                       mouseover : function(){
                           this.node.setStyles(_self.css.departmentNode_over)
                       }.bind({ node : div }),
                       mouseout : function(){
                           this.node.setStyles(_self.css.departmentNode )
                       }.bind({ node : div }),
                       click : function(){
                            _self.openMind( this.department );
                       }.bind({ department : v })
                   })
               }.bind(this))
           }
        }.bind(this));

        this.resizeContent();

    },
    openMind: function( department ){
        var appId = "ReportMinder"+department;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(null, "ReportMinder", {
                "mindType" : "department",
                "department" : department,
                "year" : this.currentYear,
                "appId": appId
            });
        }
    },
    resizeContent : function(){
        var size = this.explorer.node.getSize();
        if(this.yearContentList.getElements("div").length>0){
            this.yearContentList.setStyles({
                "width" : (size.x - 260 )+"px"
            });
            if( !this.app.inContainer ){
                this.viewContent.setStyles({
                    "height":(size.y-this.actionNode.getHeight() - 50 )+"px"
                });
                this.viewContentList.setStyles({
                    "height":(this.viewContent.getHeight()-20)+"px",
                    "width":(this.viewContent.getWidth-40)+"px"
                });
            }
        }

    },
    destroy: function(){
        this.app.removeEvent("resize", this.resizeContentFun );
        this.actionNode.empty();
        this.viewNode.empty();
    }
});

MWF.xApplication.Report.MindView.KeyWorkView = new Class({
    initialize: function (actionNode, viewNode, explorer) {
        this.explorer = explorer;
        this.app = explorer.app;
        this.lp = explorer.lp;
        this.css = explorer.css;

        this.actions = explorer.actions;
        this.actionNode = actionNode;
        this.viewNode = viewNode;
    },
    load: function () {
        this.resizeContentFun = this.resizeContent.bind(this);
        this.createYearContent(
            this.resizeContentFun
        );
        this.app.addEvent("resize", this.resizeContentFun );
    },

    createYearContent:function(callback){
        this.yearContentList = new Element("div.yearContentList",{"styles":this.css.yearContentList}).inject(this.actionNode);
        this.app.strategyActions.getKeyWorkListYear(function(json){
            if(json.type=="success"){
                if(json.data && json.data.valueList){
                    this.yearList = json.data.valueList;
                    this.yearList.each(function(d,i){
                        if(i<3){
                            new Element("div.year",{
                                "styles":this.css.year,
                                "value":d,
                                "name":d,
                                "text":d
                            }).inject(this.yearContentList).
                                addEvents({
                                    "click":function(){
                                        this.changeYearSelected(d);
                                        this.openList(d)
                                    }.bind(this)
                                })
                        }
                    }.bind(this));
                    if(this.yearList.length>3){
                        new Element("div.yearMore",{"styles":this.css.year,"id":"yearMore"}).inject(this.yearContentList).
                            setStyles({"width":"30px"}).set({"text":"..."}).
                            addEvents({
                                "click":function(){
                                    this.expandYears()
                                }.bind(this)
                            })
                    }

                    if(this.currentYear){
                        if(this.yearContentList.getElements("div[name='"+this.currentYear+"']").length>0){
                            this.yearContentList.getElements("div[name='"+this.currentYear+"']")[0].click();
                        }
                    }else{
                        if(this.yearContentList.getElements("div").length>0){
                            this.yearContentList.getElements("div")[0].click();
                        }
                    }


                    if(callback)callback()
                }
            }
        }.bind(this));
    },
    expandYears:function(){
        this.yearContentList.getElementById("yearMore").destroy();
        this.yearList.each(function(d,i){
            if(i>2){
                new Element("div.year",{
                    "styles":this.css.year,
                    "value":d,
                    "name":d,
                    "text":d
                }).inject(this.yearContentList).
                    addEvents({
                        "click":function(){
                            this.changeYearSelected(d);
                            this.openList(d)
                        }.bind(this)
                    })
            }
        }.bind(this));
        this.expend();
        this.yearContentList.addEvents({
            mouseenter : function(){
                this.expend();
            }.bind(this),
            mouseleave: function(){
                this.collapse();
            }.bind(this)
        })
    },
    expend: function(){
        this.actionNode.setStyles({
            "position" : "relative"
        });
        this.yearContentList.setStyles({
            "padding-bottom" : "15px",
            "height" : this.yearContentList.getScrollSize().y + "px",
            "position" : "absolute",
            "top" : "0px",
            "left" : "0px",
            "box-shadow": "0 0 8px 0 rgba(0,0,0,0.25)",
            "overflow" : "visible"
        });
    },
    collapse: function(){
        this.actionNode.setStyles({
            "position" : "static"
            //"overflow" : "hidden"
        });
        this.yearContentList.setStyles({
            "padding-bottom" : "0px",
            "height" : "60px",
            "position" : "static",
            "box-shadow": "none",
            "overflow" : "hidden"
        });
    },
    openList:function(v){
        this.currentYear = v;
        this.createSearch();
        this.createViewContent();
        this.resizeContent();


    },
    changeYearSelected:function(str){
        this.yearContentList.getElements("div").each(function(d){
            if(d.get("text") == str){
                d.setStyles({"background-color":"#4990E2","color":"#FFFFFF"})
            }else{
                d.setStyles({"background-color":"","color":"#666666"})
            }
        }.bind(this))

    },
    createSearch:function(){
        if(this.searchContent) this.searchContent.destroy();
        this.searchContent = new Element("div.searchContent",{"styles":this.css.searchContent}).inject(this.actionNode);
        this.searchBar = new Element("div.searchBar",{"styles":this.css.searchBar}).inject(this.searchContent);
        this.searchIn = new Element("input.searchIn",{
            "styles":this.css.searchIn,
            "placeholder":this.lp.defaultSearchIn
        }).inject(this.searchBar).
            addEvents({
                "keydown":function(e){
                    if(this.searchIn.get("value")!="" && e.event.keyCode=="13"){
                        this.searchReset.setStyles({"display":""});
                        this.createViewContent({"strategydeploytitle":this.searchIn.get("value")});
                    }
                }.bind(this)
            });
        this.searchImg = new Element("div.searchImg",{"styles":this.css.searchImg}).inject(this.searchBar);
        this.searchImg.addEvents({
            "click":function(){
                if(this.searchIn.get("value")!=""){
                    this.searchReset.setStyles({"display":""});
                    this.createViewContent({"strategydeploytitle":this.searchIn.get("value")})
                }
            }.bind(this)
        });
        this.searchReset = new Element("div.searchReset",{"styles":this.css.searchReset}).inject(this.searchBar).
            addEvents({
                "click":function(){
                    this.searchIn.set("value","");
                    this.searchReset.setStyles({"display":"none"});
                    this.createViewContent();
                }.bind(this)
            });

        this.searchDeptBar = new Element("div.searchDeptBar",{"styles":this.css.searchDeptBar}).inject(this.searchContent);
        //this.searchDeptLabel = new Element("div.searchDeptBar",{
        //    "styles":this.css.searchDeptLabel,
        //    "text":this.lp.deptList
        //}).inject(this.searchDeptBar);



        //部门列表
        this.searchDeptList = new Element("div.searchDeptList",{"styles":this.css.searchDeptList}).inject(this.searchDeptBar);

        var selector = new MWF.xApplication.Report.MindView.DepartmentSelect(this.searchDeptList, {
            "currentYear" : this.currentYear,
            "onSelectItem" : function( itemNode, itemData ){
                this.createViewContent( itemData.value ? { "deptlist":[itemData.value]} : {})
            }.bind(this)
        } , this.app );
        selector.load();

    },

    //视图
    createViewContent:function(searchObj){
        if( this.view )this.view.destroy();
        if(this.viewContent)this.viewContent.destroy();
        this.viewContent = new Element("div.viewContent",{"styles":this.css.viewContent}).inject(this.viewNode);
        this.viewContentList = new Element("div.viewContentList",{"styles":this.css.viewContentList}).inject(this.viewContent);

        this.filter = {
            "strategydeployyear":this.currentYear,
            "ordersymbol":"ASC"
        };
        for(var item in searchObj){
            if(searchObj[item]!=this.app.lp.defaultSelect){
                this.filter[item] = searchObj[item];
            }
        }
        var templateUrl = this.explorer.path + "KeyWork.json";
        this.view =  new  MWF.xApplication.Report.MindView.View(this.viewContentList, this.app,
            {lp : this.lp.view, css : this.css, actions : this.actions},
            { templateUrl : templateUrl, filterData:this.filter, year : this.currentYear }
        );

        this.view.load();
        this.resizeContent();
        //this.viewContentList.getElementById("tabList").getElementsByTagName("tr").
        //    addEvents({
        //        "move":function(e){
        //            alert(1)
        //        }.bind(this)
        //    })

    },
    resizeContent : function(){
        var size = this.explorer.node.getSize();
        if(this.yearContentList.getElements("div").length>0){
            var searchContentSize = this.searchContent.getSize();
            //var viewContentSize = this.viewContent.getSize();
            //this.searchBar.setStyles({"width":(searchContentSize.x - this.searchDeptBar.getWidth()-100)+"px"});
            this.yearContentList.setStyles({
                "width" : (size.x - 570 - 260 )+"px"
            });
            if( !this.app.inContainer ){
                this.viewContent.setStyles({
                    "height":(size.y-this.actionNode.getHeight() - 50 )+"px"
                });
                this.viewContentList.setStyles({
                    "height":(this.viewContent.getHeight()-20)+"px",
                    "width":(this.viewContent.getWidth-40)+"px"
                });
            }
        }

    },
    destroy: function(){
        if( this.view ){
            this.view.destroy()
        }
        this.app.removeEvent("resize", this.resizeContentFun );
        this.actionNode.empty();
        this.viewNode.empty();
    }
});

MWF.xApplication.Report.MindView.View = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    Implements: [Options, Events],
    options : {
        "scrollEnable" : true,
        "scrollType" : "window"
    },
    _createDocument: function(data){
        return new MWF.xApplication.Report.MindView.Document(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        //var category = this.options.category;
        if (!count)count = 100;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";

        //if(id=="(0)")this.app.createShade();

        var filter = this.options.filterData || {};
        //filter.maxCharacterNumber = "-1";

        this.app.strategyActions.getKeyWorkListNext(id,count,filter,function(json){
            if (callback)callback(json);
            //this.app.destroyShade();
        }.bind(this));


    },
    _removeDocument: function(documentData){

    },
    _create: function(){

    },
    _openDocument: function( documentData ){
        var appId = "ReportMinder"+documentData.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(null, "ReportMinder", {
                "id" : documentData.id,
                "year" : this.options.year,
                "appId": appId
            });
        }
    },
    _queryCreateViewNode: function(){

    },
    _postCreateViewNode: function(  ){

    },
    _queryCreateViewHead:function(){

    },
    _postCreateViewHead: function( headNode ){

    },

    destroyScroll: function(){
        if( this.scrollContainerFun ){
            var scrollNode = this.app.scrollNode ? this.app.scrollNode : this.container;
            scrollNode.removeEvent("scroll", this.scrollContainerFun );
            this.scrollContainerFun = null;
        }
    },
    setScroll: function(){
        var scrollNode = this.app.scrollNode ? this.app.scrollNode : this.container;
        scrollNode.setStyle("overflow","auto");
        this.scrollContainerFun = function(){
            if( !this.options.pagingEnable ){
                var scrollSize = scrollNode.getScrollSize();
                var clientSize = scrollNode.getSize();
                var scrollHeight = scrollSize.y - clientSize.y;
                if (scrollNode.scrollTop + 150 > scrollHeight ) {
                    if (! this.isItemsLoaded) this.loadElementList();
                }
            }
        }.bind(this);
        scrollNode.addEvent("scroll", this.scrollContainerFun )
    }

});

MWF.xApplication.Report.MindView.Document = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,

    _postCreateDocumentNode: function( itemNode, itemData ){


    }
});

MWF.xApplication.Report.MindView.DepartmentSelect = new Class({
    Extends: MSelector,
    options : {
        "style": "default",
        "width": "230px",
        "height": "30px",
        "defaultOptionLp" : "请选择部门列表",
        "textField" : "text",
        "valueField" : "value",
        "currentYear" : ""
    },
    _selectItem : function( itemNode, itemData ){

    },
    _loadData : function( callback ){
        this.app.strategyActions.getKeyWorkDepartmentByYear(this.options.currentYear,function(json){
            if(json.type=="success" && json.data.valueList){
                var arr = [];
                json.data.valueList.each( function(v){
                    arr.push({
                        text : v.split("@")[0],
                        value : v
                    })
                });
                if(callback)callback( arr );
            }
        }.bind(this));
    }
});