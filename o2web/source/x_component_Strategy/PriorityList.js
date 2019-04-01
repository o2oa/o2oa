MWF.xApplication.Strategy = MWF.xApplication.Strategy || {};
MWF.xDesktop.requireApp("Template", "MDomItem", null, false);
MWF.xDesktop.requireApp("Strategy", "Template", null, false);

MWF.require("MWF.widget.Identity", null,false);

MWF.xApplication.Strategy.PriorityList = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },

    initialize: function (node, app, actions, options) {
        this.setOptions(options);
        //alert(JSON.stringify(app.lp.priority))
        this.app = app;
        this.lp = app.lp.priority;
        this.path = "/x_component_Strategy/$PriorityList/";
        this.loadCss();

        this.actions = actions;
        this.node = $(node);
    },
    loadCss: function () {
        this.cssPath = this.path + this.options.style + "/css.wcss";
        this._loadCss();
    },
    load: function () {

        if(this.options.style == "portal"){
            this.node.setStyles({
                "background-color":"#ffffff"
            });
        }
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

        this.node.empty();
        this.createDepartmentNavi();


        //this.createYearContent(function(){
        //    this.resizeContent();
        //}.bind(this));
        this.app.addEvent("resize", function(){
            this.resizeContent();
        }.bind(this));
    },
    reload:function(year){
        //this.currentYear = year;
        //this.createYearContent();
    },
    createDepartmentNavi:function(){
        this.departmentNavi = new Element("div.departmentNavi",{"styles":this.css.departmentNavi}).inject(this.node);
        this.actions.getPriorityDepartments(function(json){
            if(json.type == "success"){
                var _self = this;
                json.data.each(function(d){
                    new Element("div.departmentItem",{
                        "styles":_self.css.departmentItem,
                        "distinguishedName": d.distinguishedName,
                        "text": d.name
                    }).inject(_self.departmentNavi).
                        addEvents({
                            "click":function(){
                                _self.openDepartment(this.get("distinguishedName"))
                            },
                            "mouseover":function(){
                                if(_self.currentDistinguishedName != this.get("distinguishedName")){
                                    this.setStyles({"background-color":"#D9EBFF","color":"#4990E2"})
                                }
                            },
                            "mouseout":function(){
                                if(_self.currentDistinguishedName != this.get("distinguishedName")){
                                    this.setStyles({"background-color":"","color":""})
                                }
                            }
                        })
                }.bind(_self));
                this.departmentNavi.getElements(".departmentItem")[0].click();
            }
        }.bind(this));

    },

    openDepartment:function(dn){
        if(!dn) return;
        this.changeDepartmentSelect(dn);
        this.createYearContent(function(){
            this.resizeContent();
        }.bind(this));
    },

    changeDepartmentSelect:function(dn){
        if(!dn) return;
        this.currentDistinguishedName = dn;
        var allDepartments = this.departmentNavi.getElements(".departmentItem");
        allDepartments.each(function(d){
            d.setStyles({"background-color":"","color":""});
            if(dn == d.get("distinguishedName")){
                d.setStyles({"background-color":"#D9EBFF","color":"#4990E2"});
            }
        }.bind(this))
    },




    createYearContent:function(callback){
        if(this.rightContent) this.rightContent.destroy();
        this.rightContent = new Element("div.rightContent",{"styles":this.css.rightContent}).inject(this.node);
        this.rightContent.setStyles({
            "width":(this.node.getWidth()-this.departmentNavi.getWidth())+"px"
        });
        this.yearContent = new Element("div.yearContent",{"styles":this.css.yearContent}).inject(this.rightContent);
        this.yearContentList = new Element("div.yearContentList",{"styles":this.css.yearContentList}).inject(this.yearContent);
        var data = {
            "keyworkunit":this.currentDistinguishedName
        };
        this.actions.getYearsByDepartment(data,function(json){
            if(json.type == "success"){
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

        //新增
        this.actions.getPriorityAddAuthorize(
            function(json) {
                if (json.type == "success" && json.data && json.data.value) {
                    this.addContent = new Element("div.addContent",{"styles":this.css.addContent}).inject(this.yearContent).
                        addEvents({
                            "click":function(){
                                MWF.xDesktop.requireApp("Strategy", "PriorityForm", function(){
                                    //function (explorer, data, options, para) {

                                    this.Priorityform = new MWF.xApplication.Strategy.PriorityForm(this,{},{
                                            "isNew":true,
                                            "onPostSave" : function(){
                                                //this.openList(this.currentYear)
                                                //this.reload(this.currentYear);
                                                this.openDepartment(this.currentDistinguishedName);
                                            }.bind(this),
                                            "width":"90%",
                                            "height":"100%",
                                            "maxAction":true,
                                            "year":this.currentYear||"",
                                            "department":this.currentDistinguishedName,
                                            "container":this.app.portalContainer || this.app.content
                                        },{

                                            //"onPostSave" : function(){
                                            //    //this.openList(this.currentYear)
                                            //    //this.reload(this.currentYear);
                                            //    this.openDepartment(this.currentDistinguishedName);
                                            //}.bind(this),
                                            "container":this.app.portalContainer || this.app.content
                                    });

                                    this.Priorityform.load();
                                    //this.Priorityform = new MWF.xApplication.Strategy.PriorityForm(this, this.app.actions,{},{
                                    //    "year":this.currentYear||"",
                                    //    "department":this.currentDistinguishedName,
                                    //    "isNew": true,
                                    //    "onPostSave" : function(){
                                    //        //this.openList(this.currentYear)
                                    //        //this.reload(this.currentYear);
                                    //        this.openDepartment(this.currentDistinguishedName);
                                    //    }.bind(this),
                                    //    "container":this.app.portalContainer || this.app.content
                                    //});
                                    //this.Priorityform.container = this.app.portalContainer || this.app.content;
                                    //this.Priorityform.load();
                                }.bind(this));
                            }.bind(this)
                        });
                    this.addContentImg = new Element("div.addContentImg",{"styles":this.css.addContentImg}).inject(this.addContent);
                    this.addContentLabel = new Element("div.addContentLabel",{
                        "styles":this.css.addContentLabel,
                        "text":this.lp.add
                    }).inject(this.addContent);
                }
            }.bind(this)
        );

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
        this.searchContent = new Element("div.searchContent",{"styles":this.css.searchContent}).inject(this.rightContent);
        this.searchBar = new Element("div.searchBar",{"styles":this.css.searchBar}).inject(this.searchContent);
        this.searchIn = new Element("input.searchIn",{
            "styles":this.css.searchIn,
            "placeholder":this.lp.defaultSearchIn
        }).inject(this.searchBar).
            addEvents({
                "keydown":function(e){
                    if(this.searchIn.get("value")!="" && e.event.keyCode=="13"){
                        this.searchReset.setStyles({"display":""});
                        this.createViewContent({"keyworktitle":this.searchIn.get("value")});
                    }
                }.bind(this)
            });
        this.searchImg = new Element("div.searchImg",{"styles":this.css.searchImg}).inject(this.searchBar);
        this.searchImg.addEvents({
            "click":function(){
                if(this.searchIn.get("value")!=""){
                    this.searchReset.setStyles({"display":""});
                    this.createViewContent({"keyworktitle":this.searchIn.get("value")})
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

    },

    //视图
    createViewContent:function(searchObj){
        if(this.viewContent)this.viewContent.destroy();
        this.viewContent = new Element("div.viewContent",{"styles":this.css.viewContent}).inject(this.rightContent);
        this.viewContentList = new Element("div.viewContentList",{"styles":this.css.viewContentList}).inject(this.viewContent);

        this.filter = {
            "keyworkyear":this.currentYear,
            "keyworkunit":this.currentDistinguishedName,
            "ordersymbol":"ASC"
        };
        for(var item in searchObj){
            if(searchObj[item]!=this.app.lp.template.defaultSelect){
                this.filter[item] = searchObj[item];
            }
        }
        var templateUrl = this.path + "Priority.json";
        this.view =  new  MWF.xApplication.Strategy.PriorityList.View(this.viewContentList, this.app, {explorer:this,lp : this.lp.view, css : this.css, actions : this.actions }, { templateUrl : templateUrl,filterData:this.filter} );

        this.view.load();
        this.resizeContent();


    },
    //分页
    createPageContent:function(){
        if(this.pageContent)this.pageContent.destroy();
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
                    var dataIds = dragSort.serialize();
                    //alert(cardIdStr)

                }.bind(this)
            }

        )

    },

    resizeContent : function(){
        var size = this.node.getSize();
        var leftNode = this.departmentNavi;
        this.rightContent.setStyles({
            "width":(size.x-leftNode.getWidth())+"px",
            "height":(size.y)+"px"
        });
        var yearNode = $(this.yearContent);
        var searchNode = $(this.searchContent);
        var viewNode = $(this.viewContent);
        var viewList = $(this.viewContentList);
        if(searchNode && viewNode){
            $(viewNode).setStyles({
                "height":(size.y - yearNode.getHeight() - searchNode.getHeight())+"px"
            });
        }
        if(viewList){
            $(viewList).setStyles({
                "height":(viewNode.getHeight()-10)+"px"
            })
        }
    },
    createShade: function(o,txtInfo){
        var defaultObj = this.node;
        var obj = o || defaultObj;
        var txt = txtInfo || "loading...";
        if(this.shadeDiv){ $(this.shadeDiv).destroy()}
        if(this["shadeTxtDiv"])  this["shadeTxtDiv"].destroy();
        this.shadeDiv = new Element("div.shadeDiv").inject(obj);
        this.inforDiv = new Element("div.inforDiv",{
            styles:{
                "height":"16px",
                "width":"200px",
                "display":"line-block",
                "position":"relative",
                "background-color":"#000000","border-radius":"3px","padding":"5px 10px"}
        }).inject(this.shadeDiv);
        this.loadImg = new Element("img.loadImg",{
            styles:{"width":"16px","height":"16px","float":"left"},
            //src:this.path+"default/icon/loading.gif"
            src:"/x_component_Strategy/$Main/default/icon/loading.gif"
        }).inject(this.inforDiv);

        this.shadeTxtSpan = new Element("span.shadeTxtSpan").inject(this.inforDiv);
        this.shadeTxtSpan.set("text",txt);
        this.shadeDiv.setStyles({
            "width":"100%",
            "height":"100%",
            //"position":"absolute",
            "opacity":"0.6",
            "background-color":"#cccccc","z-index":"999"
        });
        this.shadeTxtSpan.setStyles({"color":"#ffffff","font-size":"12px","display":"inline-block","line-height":"16px","padding-left":"5px"});

        var x = obj.getSize().x;
        var y = obj.getSize().y;
        this.shadeDiv.setStyles({
            "left":(obj.getLeft()-defaultObj.getLeft())+"px",
            "top":(obj.getTop()-defaultObj.getTop())+"px",
            "width":x+"px",
            "height":y+"px"
        });
        if(obj.getStyle("position")=="position"){
            this.shadeDiv.setStyles({
                "left":"0px",
                "top":"0px"
            })
        }
        this.inforDiv.setStyles({
            "left":(x/2)+"px",
            "top":(y/2)+"px"
        });
        this.inforDiv.setStyles({"display":"none"})
    },
    destroyShade : function(){
        if(this.shadeDiv) $(this.shadeDiv).destroy();
        //if(this.shadeDiv) this.shadeDiv.destroy()
    },
    showErrorMessage:function(xhr,text,error){
        var errorText = error;
        var errorMessage;
        if (xhr) errorMessage = xhr.responseText;
        if(errorMessage!=""){
            var e = JSON.parse(errorMessage);
            if(e.message){
                this.notice( e.message,"error");
            }else{
                this.notice( errorText,"error");
            }
        }else{
            this.notice(errorText,"error");
        }

    }

});

MWF.xApplication.Strategy.PriorityList.View = new Class({
    Extends: MWF.xApplication.Strategy.Template.view,

    _createDocument: function(data){
        return new MWF.xApplication.Strategy.PriorityList.Document(this.viewBodyNode, data, this.explorer, this);
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
                    //如果不是门户，最后一条记录画好后，执行拖动方案
                    if(this.explorer.explorer.options.style!="portal"){
                        if(!(this.explorer.explorer.filter.keyworktitle)){
                            this.explorer.explorer.dragItemData();
                        }
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

        if(id=="(0)") {debugger;
            this.explorer.explorer.createShade();
        }

        var filter = this.options.filterData || {};
        //filter.maxCharacterNumber = "-1";

        this.actions.getPriorityListNext(id,count,filter,function(json){
            if (callback)callback(json);
            this.explorer.explorer.destroyShade();
        }.bind(this));


    },
    _removeDocument: function(documentData){

    },
    _create: function(){

    },
    _openDocument: function( documentData ){
        MWF.xDesktop.requireApp("Strategy", "PriorityForm", function(){
            this.priorityForm = new MWF.xApplication.Strategy.PriorityForm(this, this.actions,
                {
                    "id":documentData.id
                    //"width":1000,
                    //"height":500
                },{
                "isEdited":false,
                "container":this.app.portalContainer || this.app.content
            } );
            this.priorityForm.load();

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

MWF.xApplication.Strategy.PriorityList.Document = new Class({
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
        MWF.xDesktop.requireApp("Strategy", "PriorityForm", function(){
            this.PriorityForm = new MWF.xApplication.Strategy.PriorityForm(this, this.actions,{"id":this.data.id},{
                "isEdited":false
            } );
            this.PriorityForm.load();

        }.bind(this));
    },
    action_edit:function(){
        MWF.xDesktop.requireApp("Strategy", "PriorityForm", function(){
            this.Priorityform = new MWF.xApplication.Strategy.PriorityForm(this, this.app.actions,{
                "id":this.data.id,
                "isNew":false,
                "isEdited": true,
                "width":1000,
                "height":500,
                "year":this.data.keyworkyear,
                "onPostSave" : function(){
                    //this.app.priorityList.openDepartment(this.app.priorityList.currentDistinguishedName);
                    this.explorer.explorer.openDepartment(this.explorer.explorer.currentDistinguishedName);
                }.bind(this)
            },{
                "container":this.app.portalContainer || this.app.content
            });

            this.Priorityform.load();
        }.bind(this));
    },
    action_delete:function(e){
        var _self = this;
        _self.view.app.confirm("warn",e,_self.explorer.explorer.app.lp.priority.submitWarn.title,_self.explorer.explorer.app.lp.priority.submitWarn.content.deleted,300,120,function(){
            _self.actions.deletePriority(_self.data.id, function(json){
                if(json.type && json.type=="success"){
                    this.app.notice(_self.explorer.explorer.app.lp.prompt.priority.deleteOK, "success");
                    _self.explorer.explorer.openDepartment(_self.explorer.explorer.currentDistinguishedName);
                }
            }.bind(_self));

            this.close()


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
