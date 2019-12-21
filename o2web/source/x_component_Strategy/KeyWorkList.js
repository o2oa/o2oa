MWF.xApplication.Strategy = MWF.xApplication.Strategy || {};
MWF.xDesktop.requireApp("Template", "MDomItem", null, false);
MWF.xDesktop.requireApp("Strategy", "Template", null, false);

MWF.require("MWF.widget.Identity", null,false);

MWF.xApplication.Strategy.KeyWorkList = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },

    initialize: function (node, app, actions, options) {
        this.setOptions(options);
        this.app = app;
        this.lp = app.lp.keyWork;
        this.path = "/x_component_Strategy/$KeyWorkList/";
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
                    //delete this.listContentDiv;
                    //try{
                        $(this.listContentDiv).destroy();
                    //}catch(e){}
                }
                if(this.allArrowArr.length>0){
                    this.allArrowArr.each(function(d){
                        $(d).setStyles({
                            "background":"url(/x_component_Strategy/$Template/default/icons/arrow.png) no-repeat center"
                        });
                    }.bind(this));
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
        this.actions.getKeyWorkListYear(function(json){
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
                                });
                        }
                    }.bind(this));
                    if(this.yearList.length>3){
                        new Element("div.yearMore",{"styles":this.css.year,"id":"yearMore"}).inject(this.yearContentList).
                            setStyles({"width":"30px"}).set({"text":"..."}).
                            addEvents({
                                "click":function(){
                                    this.expandYears()
                                }.bind(this)
                            });
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


                    if(callback)callback();
                }
            }
        }.bind(this));

        //*******************************************
        // 去掉新建等按钮 在应用中不操作
        //*******************************************
        //新增
        // this.actions.getKeyWorkAddAuthorize(
        //    function(json){
        //        if(json.type=="success" && json.data && json.data.value){
        //            this.addContent = new Element("div.addContent",{"styles":this.css.addContent}).inject(this.yearContent).
        //                addEvents({
        //                    "click":function(){
        //                        MWF.xDesktop.requireApp("Strategy", "KeyWorkForm", function(){
        //                            this.keyWorkform = new MWF.xApplication.Strategy.KeyWorkForm(this, this.app.actions,{},{
        //                                "year":this.currentYear||"",
        //                                "isNew": true,
        //                                "onPostSave" : function(){
        //                                    //this.openList(this.currentYear)
        //                                    this.reload(this.currentYear);
        //                                }.bind(this)
        //                            });
        //
        //                            this.keyWorkform.load();
        //                        }.bind(this));
        //                    }.bind(this)
        //                });
        //            this.addContentImg = new Element("div.addContentImg",{"styles":this.css.addContentImg}).inject(this.addContent);
        //            this.addContentLabel = new Element("div.addContentLabel",{
        //                "styles":this.css.addContentLabel,
        //                "text":this.lp.add
        //            }).inject(this.addContent);
        //
        //            //导入
        //            this.importContent = new Element("div.importContent",{"styles":this.css.importContent}).inject(this.yearContent).
        //                addEvents({
        //                    "click":function(){
        //                        //导入
        //                        MWF.xDesktop.requireApp("Strategy", "ImportForm", function(){
        //                            this.importform = new MWF.xApplication.Strategy.ImportForm(this,
        //                                {
        //                                    "id":"idddddddddddddddd"
        //                                },
        //                                {
        //                                    "year":this.currentYear||"",
        //                                    "isNew": true,
        //                                    "onPostSave" : function(){
        //                                        //this.openList(this.currentYear)
        //                                        this.reload(this.currentYear);
        //                                    }.bind(this)
        //                                }
        //                            );
        //
        //                            this.importform.load();
        //                        }.bind(this));
        //
        //                    }.bind(this)
        //                });
        //            this.importContentImg = new Element("div.importContentImg",{"styles":this.css.importContentImg}).inject(this.importContent);
        //            this.importContentLabel = new Element("div.importContentLabel",{
        //                "styles":this.css.importContentLabel,
        //                "text":this.lp.imported
        //            }).inject(this.importContent);
        //
        //
        //        }
        //    }.bind(this)
        // );
    },
    expandYears:function(){
        this.yearContentList.getElementById("yearMore").destroy();
        this.yearList.each(function(d,i){
            if(i>2){
            //    new Element("div.year",{
            //        "styles":this.css.year,
            //        "value":d,
            //        "name":d,
            //        "text":d
            //    }).inject(this.yearContentList).
            //        addEvents({
            //            "click":function(){
            //                this.changeYearSelected(d);
            //                this.openList(d)
            //            }.bind(this)
            //})
        }
        }.bind(this));
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
        if(this.searchContent) {this.searchContent.destroy();}
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
        this.searchDeptLabel = new Element("div.searchDeptBar",{
            "styles":this.css.searchDeptLabel,
            "text":this.lp.deptList
        }).inject(this.searchDeptBar);



        //部门列表
        this.searchDeptList = new Element("div.searchDeptList",{"styles":this.css.searchDeptList}).inject(this.searchDeptBar);
        var size = {"width":230,"height":30};
        this.searchDeptSelector =  new MWF.xApplication.Strategy.Template.Select(this.searchDeptList,this, this.actions, size);
        this.searchDeptSelector.load();
        this.actions.getKeyWorkDepartmentByYear(this.currentYear,function(json){
            if(json.type=="success" && json.data.valueList){
                this.searchDeptSelector.setDeptList(json.data.valueList,function(d){
                    this.createViewContent({"deptlist":[d]})
                }.bind(this));
            }
        }.bind(this));



    },

    //视图
    createViewContent:function(searchObj){
        if(this.viewContent)this.viewContent.destroy();
        this.viewContent = new Element("div.viewContent",{"styles":this.css.viewContent}).inject(this.node);
        this.viewContentList = new Element("div.viewContentList",{"styles":this.css.viewContentList}).inject(this.viewContent);

        this.filter = {
            "strategydeployyear":this.currentYear,
            "ordersymbol":"ASC"
        };
        for(var item in searchObj){
            if(searchObj[item]!=this.app.lp.template.defaultSelect){
                this.filter[item] = searchObj[item];
            }

        }
        var templateUrl = this.path + "KeyWork.json";
        this.view =  new  MWF.xApplication.Strategy.KeyWorkList.View(this.viewContentList, this.app, {lp : this.lp.view, css : this.css, actions : this.actions }, { templateUrl : templateUrl,filterData:this.filter} );

        this.view.load();
        this.resizeContent();
        //this.viewContentList.getElementById("tabList").getElementsByTagName("tr").
        //    addEvents({
        //        "move":function(e){
        //            alert(1)
        //        }.bind(this)
        //    })

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
                    this.actions.changeKeyWorkPosition(submitData,function(){
                        this.createViewContent();
                        //if(json.type=="success"){

                        //}else{

                        //}
                    }.bind(this));

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
                "height":(this.viewContent.getHeight()-20)+"px",
                "width":(this.viewContent.getWidth-60)+"px"
            });
        }

    }
//
});

MWF.xApplication.Strategy.KeyWorkList.View = new Class({
    Extends: MWF.xApplication.Strategy.Template.view,

    _createDocument: function(data){
        return new MWF.xApplication.Strategy.KeyWorkList.Document(this.viewBodyNode, data, this.explorer, this);
    },
    loadScrollElementList : function( count ){
        if (!this.isItemsLoaded) {
            if (!this.isItemLoadding) {
                this.isItemLoadding = true;
                this._getCurrentPageData(function (json) {
                    var length = this.dataCount = json.count;  //|| json.data.length;
                    if (length <= this.items.length) {
                        this.isItemsLoaded = true;
                    }
                    if( json.data && typeOf( json.data )=="array" ){
                        json.data.each(function (data ) {
                            var key = data[ this.options.documentKeyWord || "id" ];
                            if (!this.documents[key]) {
                                var item = this._createDocument(data, this.items.length);
                                this.items.push(item);
                                this.documents[key] = item;
                            }
                        }.bind(this));
                    }
                    this.isItemLoadding = false;
                    //最后一条记录画好后，执行拖动方案
                    if(!(this.app.keyWorkList.filter.deptlist || this.app.keyWorkList.filter.strategydeploytitle)){
                        this.app.keyWorkList.dragItemData();
                    }

                    if (this.loadItemQueue > 0) {
                        this.loadItemQueue--;
                        this.loadElementList();
                    }
                }.bind(this), count);
            } else {
                this.loadItemQueue++;
            }
        }
    },

    _getCurrentPageData: function(callback, count){
        //var category = this.options.category;

        if (!count)count = 100;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";

        if(id=="(0)")this.app.createShade();

        var filter = this.options.filterData || {};
        //filter.maxCharacterNumber = "-1";

        this.actions.getKeyWorkListNext(id,count,filter,function(json){
            if (callback)callback(json);
            this.app.destroyShade();
        }.bind(this));


    },
    _removeDocument: function(documentData){

    },
    _create: function(){

    },
    _openDocument: function( documentData ){
        MWF.xDesktop.requireApp("Strategy", "KeyWorkForm", function(){
            this.KeyWorkForm = new MWF.xApplication.Strategy.KeyWorkForm(this, this.actions,{"id":documentData.id},{
                "isEdited":false
            } );
            this.KeyWorkForm.load();

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

MWF.xApplication.Strategy.KeyWorkList.Document = new Class({
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
        MWF.xDesktop.requireApp("Strategy", "KeyWorkForm", function(){
            this.keyWorkform = new MWF.xApplication.Strategy.KeyWorkForm(this, this.actions,{"id":this.data.id},{
                "isEdited":false
            } );
            this.keyWorkform.load();

        }.bind(this));
    },
    action_edit:function(){
        MWF.xDesktop.requireApp("Strategy", "KeyWorkForm", function(){
            this.keyWorkform = new MWF.xApplication.Strategy.KeyWorkForm(this, this.app.actions,{"id":this.data.id},{
                "isNew":false,
                "isEdited": true,
                "onPostSave" : function(){
                    this.app.keyWorkList.openList(this.app.keyWorkList.currentYear)
                }.bind(this)
            });

            this.keyWorkform.load();
        }.bind(this));
    },
    action_delete:function(e){
        var _self = this;
        _self.view.app.confirm(
            "warn",
            e,
            _self.view.app.lp.keyWork.submitWarn.title,
            _self.view.app.lp.keyWork.submitWarn.content.deleted,
            300,
            120,
            function(){
                _self.actions.deleteKeyWork(_self.data.id, function(json){
                    if(json.type && json.type=="success"){
                        this.app.notice(_self.view.app.lp.prompt.keyWork.deleteOK, "success");
                        _self.app.keyWorkList.openList(this.app.keyWorkList.currentYear);
                    }
                }.bind(_self));

                this.close();


            },
            function(){
                this.close();
            }
        );

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



        //if(!(this.app.keyWorkList.filter.deptList && this.app.keyWorkList.filter.strategydeploytitle)){//alert("dr")
         //   this.app.keyWorkList.dragItemData();
       // }


    }
});