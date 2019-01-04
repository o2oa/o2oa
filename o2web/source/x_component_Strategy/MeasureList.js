MWF.xApplication.Strategy = MWF.xApplication.Strategy || {};
MWF.xDesktop.requireApp("Template", "MDomItem", null, false);
MWF.xDesktop.requireApp("Strategy", "Template", null, false);

MWF.require("MWF.widget.Identity", null,false);

MWF.xApplication.Strategy.MeasureList = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },

    initialize: function (node, app, actions, options) {
        this.setOptions(options);
        this.app = app;
        this.lp = app.lp.measure;
        this.path = "/x_component_Strategy/$MeasureList/";
        this.loadCss();

        this.actions = actions;
        this.node = $(node);
    },
    loadCss: function () {
        this.cssPath = this.path + this.options.style + "/css.wcss";
        this._loadCss();
    },
    load: function () {
        this.allArrowArr = [];
        this.node.addEvents({
            "click": function () {
                if(this.listContentDiv){
                    $(this.listContentDiv).destroy();
                }
                if(this.allArrowArr.length>0){
                    this.allArrowArr.each(function(d){
                        $(d).setStyles({
                            "background":"url(/x_component_Strategy/$Template/default/icons/arrow.png) no-repeat center"
                        });
                    }.bind(this))
                }

            }.bind(this)
        });

        this.createYearContent(function(){
            this.resizeContent();
        }.bind(this));
        this.app.addEvent("resize", function(){
            this.resizeContent();
        }.bind(this));
    },
    reload:function(year){
        this.currentYear = year;
        this.createYearContent();
    },
    createYearContent:function(callback){
        this.node.empty();
        this.yearContent = new Element("div.yearContent",{"styles":this.css.yearContent}).inject(this.node);
        this.yearContentList = new Element("div.yearContentList",{"styles":this.css.yearContentList}).inject(this.yearContent);
        this.actions.getMeasureListYear(function(json){
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
                                        this.openList(d);
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

        //*******************************************
        // 去掉新建等按钮 在应用中不操作
        //*******************************************

        ////新增
        //this.actions.getMeasureAddAuthorize(
        //    function(json){
        //        if(json.type=="success" && json.data && json.data.value){
        //            this.addContent = new Element("div.addContent",{"styles":this.css.addContent}).inject(this.yearContent).
        //                addEvents({
        //                    "click":function(){
        //                        MWF.xDesktop.requireApp("Strategy", "MeasureForm", function(){
        //                            this.measureform = new MWF.xApplication.Strategy.MeasureForm(this, this.app.actions,{},{
        //                                "year":this.currentYear||"",
        //                                "isNew": true,
        //                                "onPostSave" : function(){
        //                                    //this.openList(this.currentYear)
        //                                    this.reload(this.currentYear);
        //                                }.bind(this)
        //                            });
        //
        //                            this.measureform.load();
        //                        }.bind(this));
        //                    }.bind(this)
        //                });
        //            this.addContentImg = new Element("div.addContentImg",{"styles":this.css.addContentImg}).inject(this.addContent);
        //            this.addContentLabel = new Element("div.addContentLabel",{
        //                "styles":this.css.addContentLabel,
        //                "text":this.lp.add
        //            }).inject(this.addContent);
        //        }
        //    }.bind(this)
        //);
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
        }.bind(this))
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
        this.searchContent = new Element("div.searchContent",{"styles":this.css.searchContent}).inject(this.node);
        this.searchBar = new Element("div.searchBar",{"styles":this.css.searchBar}).inject(this.searchContent);
        this.searchIn = new Element("input.searchIn",{
            "styles":this.css.searchIn,
            "placeholder":this.lp.defaultSearchIn
        }).inject(this.searchBar).
            addEvents({
                "keydown":function(e){
                    if(this.searchIn.get("value")!="" && e.event.keyCode=="13"){
                        this.searchReset.setStyles({"display":""});
                        this.createViewContent({"measuresinfotitle":this.searchIn.get("value")});
                    }
                }.bind(this)
            });
        this.searchImg = new Element("div.searchImg",{"styles":this.css.searchImg}).inject(this.searchBar);
        this.searchImg.addEvents({
            "click":function(){
                if(this.searchIn.get("value")!=""){
                    this.searchReset.setStyles({"display":""});
                    this.createViewContent({"measuresinfotitle":this.searchIn.get("value")})
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
        this.searchDeptLabel = new Element("div.searchDeptBar",{
            "styles":this.css.searchDeptLabel,
            "text":this.lp.deptList
        }).inject(this.searchDeptBar);



        //部门列表
        this.searchDeptList = new Element("div.searchDeptList",{"styles":this.css.searchDeptList}).inject(this.searchDeptBar);
        var size = {"width":230,"height":30};
        this.searchDeptSelector =  new MWF.xApplication.Strategy.Template.Select(this.searchDeptList,this, this.actions, size);
        this.searchDeptSelector.load();
        this.actions.getMeasureDepartmentByYear(this.currentYear,function(json){
            if(json.type=="success" && json.data.valueList){
                this.searchDeptSelector.setDeptList(json.data.valueList,function(d){
                    this.createViewContent({"deptlist":[d]},this.currentCountPerPage)
                }.bind(this));
            }
        }.bind(this));
        //this.TCustomerTypeSelector.setList(["abc","eeee","eeee2"]);
        //if(this.customerData && this.customerData.customertype){
        //    this.TCustomerTypeSelector.selectValueDiv.set(["abc","eeee","eeee2"]);
        //    this.TCustomerTypeSelector.node.set("value",["abc","eeee","eeee2"]);
        //}

    },

    //视图
    createViewContent:function(searchObj,perPage,curPage){
        this.searchObj = searchObj;
        this.perPageJson = {
            "text":this.app.lp.template.paging.perPageText.split(","),
            "value":this.app.lp.template.paging.perPageValue.split(",")
        };
        this.currentCountPerPage = perPage || this.perPageJson.value[0];
        if(this.viewContent)this.viewContent.destroy();
        this.viewContent = new Element("div.viewContent",{"styles":this.css.viewContent}).inject(this.node);
        this.viewContentList = new Element("div.viewContentList",{"styles":this.css.viewContentList}).inject(this.viewContent);
        this.createPageContent();

        this.filter = {
            "measuresinfoyear":this.currentYear,
            "ordersymbol":"ASC"
        };
        //if(!perPage){
            for(var item in searchObj){
                if(searchObj[item]!=this.app.lp.template.defaultSelect){
                    this.filter[item] = searchObj[item];
                }
            }
        //}

        var templateUrl = this.path + "Measure.json";

        this.view =  new  MWF.xApplication.Strategy.MeasureList.View(this.viewContentList, this.app,
            { lp : this.lp.view, css : this.css, actions : this.actions },
            {
                pagingEnable : true,
                pagingPar : {
                    currentPage : curPage || this.options.viewPageNum,
                    countPerPage : this.currentCountPerPage || this.perPageJson.value[0],
                    hasJumper:false,
                    hasNextPage:false,
                    hasReturn:false,
                    position : [  "bottom" ],
                    hiddenWithDisable : false,
                    text : {
                        prePage : this.app.lp.template.paging.prePage,
                        nextPage : this.app.lp.template.paging.nextPage,
                        firstPage : this.app.lp.template.paging.firstPage,
                        lastPage : this.app.lp.template.paging.lastPage
                    },
                    onPostLoad:function(){
                        //显示每页几条select
                        if(this.view && this.view.paging){
                            //var size = {"width":70,"height":24};

                            this.perPageChangeContent = new Element("div.perPageChangeContent",{"styles":this.css.perPageChangeContent}).inject(this.view.paging.node);
                            this.countPerPageItem = new MDomItem(this.perPageChangeContent,{
                                "name":"countPerPageSelect",
                                "type":"select",
                                "selectValue":this.perPageJson.value,
                                "selectText":this.perPageJson.text,
                                "defaultValue":this.currentCountPerPage || this.perPageJson.value[0],
                                "attr":{"style":"border-radius:2px;border:1px solid #cccccc;height:24px"},
                                "event":{
                                    change: function (item, ev) {
                                        if(item.get("value")!=""){
                                            this.currentCountPerPage = item.get("value");
                                            this.createViewContent(this.searchObj,item.get("value"))
                                        }
                                    }.bind(this)
                                }
                            } , this, this.app, this.css);
                            this.countPerPageItem.load();

                        }

                    }.bind(this)
                },
                templateUrl : templateUrl,
                filterData:this.filter
            }
        );
        this.view.pagingContainerBottom = this.pageContent;
        this.view.load();

        this.resizeContent();


    },
    //分页
    createPageContent:function(){
        if(this.pageContent)$(this.pageContent).destroy();
        this.pageContent = new Element("div.pageContent",{"styles":this.css.pageContent}).inject(this.viewContent);
    },
    dragItemData:function(){

        var dragSort = new Sortables("tabBody",
            {
                clone:true,
                opacity:0.3,
                //handle  : ".dragTr",
                onStart : function (element, clone) {
                    clone.setStyles({
                        "position":"absolute",
                        "margin-top":(160-this.viewContentList.getScrollTop())+"px",
                        "border":"1px dotted #000","width":(this.viewContentList.getWidth()-10)+"px",
                        "height":element.getHeight()+"px",
                        "overflow":"hidden",
                        "max-height":element.getHeight()+"px"
                    })
                }.bind(this),
                onSort : function (element, clone) {
                    //element.setStyle("background-color","#4990E2");
                    //clone.setStyle('background-color', '#4990E2');
                }.bind(this),
                onComplete : function (element) {

                    var id = element.get("id");
                    var idStr = dragSort.serialize();
                    var submitData = {
                        "ordersymbol":this.filter.ordersymbol,
                        "ids":idStr
                    };
                    //this.actions.changeKeyWorkPosition(submitData,function(json){
                    //    this.createViewContent();
                    //    //if(json.type=="success"){
                    //
                    //    //}else{
                    //
                    //    //}
                    //}.bind(this));




                }.bind(this)
            }

        )

    },
    resizeContent : function(){
        var size = this.node.getSize();
        if(this.yearContentList.getElements("div").length>0){
            var searchContentSize = this.searchContent.getSize();
            //var viewContentSize = this.viewContent.getSize();
            this.searchBar.setStyles({"width":(searchContentSize.x - this.searchDeptBar.getWidth()-100)+"px"});
            this.viewContent.setStyles({
                "height":(size.y-this.yearContent.getHeight()-this.searchContent.getHeight())+"px"
            });
            this.viewContentList.setStyles({
                "height":(this.viewContent.getHeight()-this.pageContent.getHeight()-20)+"px",
                "width":(this.viewContent.getWidth-60)+"px"
            });
        }
    }

});

MWF.xApplication.Strategy.MeasureList.View = new Class({
    Extends: MWF.xApplication.Strategy.Template.view,

    _createDocument: function(data){
        return new MWF.xApplication.Strategy.MeasureList.Document(this.viewBodyNode, data, this.explorer, this);
    },
    _getCurrentPageData: function(callback, count, pageNum){
        this.clearBody();
        if (!count)count = 10;
        //var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        //if(id=="(0)")this.app.createShade();
        if(!pageNum)pageNum = 1;

        var filter = this.options.filterData || {};
        //filter.maxCharacterNumber = "-1";

        this.actions.getMeasureListPage(pageNum,count,filter,function(json){
            if (callback)callback(json);
            //执行拖动方案
            if(!(this.app.measureList.filter.deptlist || this.app.measureList.filter.measuresinfotitle)){
                this.app.measureList.dragItemData();
            }
            this.app.destroyShade();
        }.bind(this));


    },
    _removeDocument: function(documentData){

    },
    _create: function(){

    },
    _openDocument: function( documentData ){
        MWF.xDesktop.requireApp("Strategy", "MeasureForm", function(){
            this.MeasureForm = new MWF.xApplication.Strategy.MeasureForm(this, this.actions,{
                "id":documentData.id,
                "maxAction":true
            },{
                "isEdited":false
            } );
            this.MeasureForm.load();

        }.bind(this));
    },
    _queryCreateViewNode: function(){

    },
    _postCreateViewNode: function(  ){

    },
    _queryCreateViewHead:function(){

    },
    _postCreateViewHead: function( headNode ){

    }

});

MWF.xApplication.Strategy.MeasureList.Document = new Class({
    Extends: MWF.xApplication.Strategy.Template.Document,

    openActionReturn:function(d) {
        var ret = false;
        if(d.actions && d.actions.length==1){
            ret = true;
        }
        return ret;
    },
    editActionReturn:function(d) {
        var ret = false;
        if (d.actions && d.actions.indexOf("EDIT")>-1)ret = true;
        return ret;
    },
    deleteActionReturn:function(d) {
        var ret = false;
        if (d.actions && d.actions.indexOf("DELETE")>-1)ret = true;
        return ret;
    },

    action_open:function(){
        MWF.xDesktop.requireApp("Strategy", "MeasureForm", function(){
            this.MeasureForm = new MWF.xApplication.Strategy.MeasureForm(this, this.actions,{"id":this.data.id},{
                "isEdited":false
            } );
            this.MeasureForm.load();

        }.bind(this));
    },
    action_edit:function(){
        MWF.xDesktop.requireApp("Strategy", "MeasureForm", function(){
            this.Measureform = new MWF.xApplication.Strategy.MeasureForm(this, this.app.actions,{"id":this.data.id},{
                "isNew":false,
                "isEdited": true,
                "onPostSave" : function(){
                    var _parent = this.app.measureList;
                    var searchObj = {};
                    var searchIn = _parent.searchIn.get("value");
                    if(searchIn!=""){
                        searchObj.measuresinfotitle = searchIn
                    }
                    var searchDept = _parent.searchDeptList.get("unit");
                    if(searchDept){
                        searchObj.deptlist = [searchDept]
                    }

                    _parent.createViewContent(searchObj,_parent.currentCountPerPage,_parent.view.currentPage);
                }.bind(this)
            });

            this.Measureform.load();
        }.bind(this));
    },
    action_delete:function(e){
        var _self = this;
        _self.view.app.confirm("warn",e,_self.view.app.lp.measure.submitWarn.title,_self.view.app.lp.measure.submitWarn.content.deleted,300,120,function(){
            _self.actions.deleteMeasure(_self.data.id, function(json){
                if(json.type && json.type=="success"){
                    this.app.notice(_self.view.app.lp.prompt.measure.deleteOK, "success");
                    _self.app.measureList.openList(this.app.measureList.currentYear)
                }
            }.bind(_self));

            this.close();


        },function(){
            this.close();
        })

    },
    _postCreateDocumentNode: function( itemNode, itemData ){
        itemNode.set("id",itemData.id);
        if(!this.openActionReturn(itemData)){
            itemNode.getElements("[item='action_open']").destroy();
        }
        if(!this.editActionReturn(itemData)){
            itemNode.getElements("[item='action_edit']").destroy();
        }
        if(!this.deleteActionReturn(itemData)){
            itemNode.getElements("[item='action_delete']").destroy();
        }


    }
});
