MWF.xApplication.Execution = MWF.xApplication.Execution || {};
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Execution", "WorkMinder", null, false);
MWF.require("MWF.widget.Identity", null,false);

MWF.xApplication.Execution.MinderExplorer = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },

    initialize: function (node, app, actions, options) {
        this.app = app;
        this.lp = app.lp;
        this.path = "/x_component_Execution/$MinderExplorer/";

        this.actions = actions;
        this.options.style = this.getViewStyle();
        this.setOptions(options);

        this.loadCss();
        this.node = $(node);
    },
    loadCss: function () {
        this.cssPath = "/x_component_Execution/$MinderExplorer/" + this.options.style + "/css.wcss";
        this._loadCss();
    },
    load: function () {
        this.middleContent = this.app.middleContent;
        //this.middleContent.setStyles({"margin-top":"0px","border":"0px solid #f00"});
        this.createNaviContent();
        //this.createContentDiv();


        this.resizeWindow();
        this.app.addEvent("resize", function(){
            this.resizeWindow();
        }.bind(this));
    },
    resizeWindow: function(){
        var size = this.app.middleContent.getSize();
        this.naviDiv.setStyles({"height":(size.y-40)+"px"});
        this.naviContentDiv.setStyles({"height":(size.y-180)+"px"});
        this.contentDiv.setStyles({"height":(size.y-40)+"px"});
        //this.viewContainer.setStyles({"height":(size.y-90)+"px"});
    },
    createNaviContent: function(){
        this.naviDiv = new Element("div.naviDiv",{
            "styles":this.css.naviDiv
        }).inject(this.middleContent);

        this.naviTitleDiv = new Element("div.naviTitleDiv",{
            "styles":this.css.naviTitleDiv,
            "text": this.lp.minderExplorerTitle
        }).inject(this.naviDiv);
        this.naviContentDiv = new Element("div.naviContentDiv",{"styles":this.css.naviContentDiv}).inject(this.naviDiv);
        this.naviBottomDiv = new Element("div.naviBottomDiv",{"styles":this.css.naviBottomDiv}).inject(this.naviDiv);

        this.createContentDiv();

        var jsonUrl = this.path+"navi.json";
        MWF.getJSON(jsonUrl, function(json){
            json.each(function(data, i){
                var naviContentLi = new Element("li.naviContentLi",{"styles":this.css.naviContentLi}).inject(this.naviContentDiv);
                naviContentLi.addEvents({
                    "mouseover" : function(ev){
                        if(this.bindObj.currentNaviItem != this.node)this.node.setStyles( this.styles )
                    }.bind({"styles": this.css.naviContentLi_over, "node":naviContentLi, "bindObj": this }) ,
                    "mouseout" : function(ev){
                        if(this.bindObj.currentNaviItem != this.node)this.node.setStyles( this.styles )
                    }.bind({"styles": this.css.naviContentLi, "node":naviContentLi, "bindObj": this }) ,
                    "click" : function(ev){
                        if( this.bindObj.currentNaviItem )this.bindObj.currentNaviItem.setStyles( this.bindObj.css.naviContentLi );
                        this.node.setStyles( this.styles );
                        this.bindObj.currentNaviItem = this.node;
                        if( this.action && this.bindObj[this.action] ){
                            this.bindObj[this.action]();
                        }
                    }.bind({"styles": this.css.naviContentLi_current, "node":naviContentLi, "bindObj": this, "action" : data.action })
                });
                var naviContentImg = new Element("img.naviContentImg",{
                    "styles":this.css.naviContentImg,
                    "src":"/x_component_Execution/$Main/default/icon/"+data.icon
                }).inject(naviContentLi);
                var naviContentSpan = new Element("span.naviContentSpan",{
                    "styles":this.css.naviContentSpan,
                    "text":data.title
                }).inject(naviContentLi);
                if( i == 0 ){
                    naviContentLi.click();
                }
            }.bind(this));
        }.bind(this));
    },
    createContentDiv: function(){
        this.contentDiv = new Element("div.contentDiv",{"styles":this.css.contentDiv}).inject(this.middleContent);

    },
    openCenterWork: function(){
        this.contentDiv.empty();
        this.loadCategoryBar();
        this.loadToolbar();
        //this.loadView();
    },
    loadCategoryBar : function(){
        var _self = this;
        this.categoryBar = new Element("div.categoryBar",{"styles":this.css.categoryBar}).inject(this.contentDiv);

        this.allCategoryNode = new Element("li.allCategoryNode", {
            "styles": this.css.categoryNode,
            "text" : "全部"
        }).inject(this.categoryBar);
        this.allCategoryNode.addEvents({
            "mouseover" : function(){ if( this.currentCategoryNode != this.allCategoryNode)this.allCategoryNode.setStyles(this.css.categoryNode_over) }.bind(this),
            "mouseout" : function(){ if( this.currentCategoryNode != this.allCategoryNode)this.allCategoryNode.setStyles(this.css.categoryNode) }.bind(this),
            "click":function(){
                if( this.currentCategoryNode )this.currentCategoryNode.setStyles(this.css.categoryNode);
                this.currentCategoryNode = this.allCategoryNode;
                this.allCategoryNode.setStyles(this.css.categoryNode_current);
                this.loadView(  )
            }.bind(this)
        });
        this.actions.getCategoryCountAll( function( json ){
                json.data = json.data || [];
                json.data.each( function( d ){
                    var categoryNode = new Element("li.categoryNode", {
                        "styles": this.css.categoryNode,
                        "text" : d.workTypeName + "(" + d.centerCount +")"
                    }).inject(this.categoryBar);
                    categoryNode.store( "workTypeName" , d.workTypeName );
                    categoryNode.addEvents({
                        "mouseover" : function(){ if( _self.currentCategoryNode != this.node)this.node.setStyles(_self.css.categoryNode_over) }.bind({node : categoryNode }),
                        "mouseout" : function(){ if( _self.currentCategoryNode != this.node)this.node.setStyles(_self.css.categoryNode) }.bind({node : categoryNode }),
                        "click":function(){
                            if( _self.currentCategoryNode )_self.currentCategoryNode.setStyles(_self.css.categoryNode);
                            _self.currentCategoryNode = this.node;
                            this.node.setStyles(_self.css.categoryNode_current);
                            _self.loadView(  )
                        }.bind({ name : d.workTypeName, node : categoryNode })
                    })
                }.bind(this))
            }.bind(this), null, false
        );
        this.allCategoryNode.click();
    },
    loadToolbar: function(){
        this.toolbar = new Element("div",{
            styles : this.css.toolbar
        }).inject(this.categoryBar);

        //this.toolbarTextNode = new Element("div",{
        //    styles : this.css.toolbarTextNode,
        //    text: this.lp.workTask.centerWork,
        //}).inject(this.toolbar);

        this.fileterNode = new Element("div",{
            styles : this.css.fileterNode
        }).inject(this.toolbar);

        this.loadFilter();
    },
    loadFilter: function () {
        var _self = this;
        var html = "<table bordr='0' cellpadding='5' cellspacing='0' styles='filterTable'>" +
            "<tr>" +
                //"    <td styles='filterTableTitle' lable='year'></td>" +
                //"    <td styles='filterTableValue' item='year'></td>" +
                //"    <td styles='filterTableTitle' lable='workLevel'></td>" +
                //"    <td styles='filterTableValue' item='workLevel'></td>" +
                //"    <td styles='filterTableTitle' lable='workType'></td>" +
                //"    <td styles='filterTableValue' item='workType'></td>" +
                //"    <td styles='filterTableTitle' lable='star'></td>" +
                //"    <td styles='filterTableValue' item='star'></td>" +
            "    <td styles='filterTableValue' item='workTitle'></td>" +
            "    <td styles='filterTableValue' item='searchAction'></td>" +
            "    <td styles='filterTableValue' item='returnAction' style='display:none;'></td>" +
            "</tr>" +
            "</table>";
        this.fileterNode.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.filter = new MForm(this.fileterNode, {}, {
                style: "execution",
                isEdited: true,
                itemTemplate: {
                    year: {
                        "text": this.lp.yearCount +":", "type": "select", "className": "inputSelectUnformatWidth",
                        "selectValue": function () {
                            var years = [], year = new Date().getFullYear();
                            for (var i = 0; i < 6; i++) years.push(year--);
                            return years;
                        }
                    },
                    workLevel: {
                        "text": this.lp.level +":", "type": "select","className": "inputSelectUnformatWidth",
                        "selectValue": this.lp.workForm.workLevelValue.split(",")
                    },
                    workType: {
                        "text": this.lp.type +":","type": "select","className": "inputSelectUnformatWidth",
                        "selectValue": this.lp.workForm.workTypeValue.split(",")
                    },
                    star: {"text": this.lp.starWork +":", "type": "select", "className": "inputSelectUnformatWidth", "selectValue": this.lp.starWorkText.split(",")},
                    workTitle: { "style":this.css.filterTitle , defaultValue : this.lp.searchText, "event" : {
                        focus : function( item ){ if(item.get("value")==_self.lp.searchText)item.setValue("") },
                        blur : function( item ){ if(item.get("value").trim()=="")item.setValue(_self.lp.searchText) },
                        keydown: function( item, ev){
                            if (ev.code == 13){  //回车，搜索
                                _self.fileterNode.getElements("[item='returnAction']").setStyle("display","");
                                _self.loadView(  );
                            }
                        }.bind(this)
                    }},
                    searchAction: {
                        "type": "button", "value": this.lp.search, "style": this.css.filterButton,
                        "event": {
                            "click": function () {
                                _self.fileterNode.getElements("[item='returnAction']").setStyle("display","");
                                _self.loadView(  );
                            }
                        }
                    },
                    returnAction : {
                        "type": "button", "value": this.lp.return, "style": this.css.filterButton,
                        "event": {
                            "click": function () {
                                _self.filter.getItem("workTitle").setValue( _self.lp.searchText );
                                _self.fileterNode.getElements("[item='returnAction']").setStyle("display","none");
                                _self.loadView();
                            }
                        }
                    }
                }
            }, this.app, this.css);
            this.filter.load();
        }.bind(this), true);
    },
    loadView : function(  ){
        var filterData = {};
        if( this.currentCategoryNode ){
            var value = this.currentCategoryNode.retrieve("workTypeName");
            if( value && value != "" ){
                filterData.workTypes = [value];
            }
        }
        if( this.filter ){
            var fd = this.filter.getResult(true, ",", true, true, true);
            //fd.title = fd.title.replace(this.lp.searchText,"");
            fd.workTitle = fd.workTitle.replace(this.lp.searchText,"");
            fd.maxCharacterNumber = "-1";
            for( var key in fd ){
                if( fd[key] != "" ){
                    filterData[key] = fd[key];
                }
            }
        }

        var flag = false;
        if( this.viewContainer ){
            flag = true;
            this.viewContainer.destroy();
        }
        this.viewContainer = Element("div",{
            "styles" : this.css.viewContainer
        }).inject(this.contentDiv);

        this.setViewSize();
        if( !flag ){
            this.setViewSizeFun = this.setViewSize.bind(this);
            this.app.addEvent("resize", this.setViewSizeFun );
        }

        if( this.view ){
            this.view.destroy();
        }
        this.getViewStyle();
        this.view = new MWF.xApplication.Execution.MinderExplorer.WorkView( this.viewContainer, this.app, this, {
            templateUrl : this.path+ ( this.getViewStyle() == "default" ? "listItem.json" : "listItem_graph.json" ),
            "scrollEnable" : true
        }, {
            lp : this.lp.centerWorkView
        });
        if( filterData )this.view.filterData = filterData;
        this.view.load();
    },
    getViewStyle : function(){
        if( this.viewStyle ) return this.viewStyle;
        this.actions.getProfileByCode( { "configCode" : "MIND_LISTSTYLE"} ,function( json ){
            if( json.data ){
                this.viewStyle = ( json.data.configValue == "ICON" ? "graph" : "default");
            }else{
                this.viewStyle = "default";
            }
        }.bind(this), function(){
            this.viewStyle = "default";
        }.bind(this), false );
        return this.viewStyle || "default";
    },
    setViewSize: function(){
        var size = this.app.middleContent.getSize();
        var categoryBarSzie = this.categoryBar ? this.categoryBar.getSize() : {x:0, y:0};
        this.viewContainer.setStyles({"height":(size.y - categoryBarSzie.y - 56 )+"px"});
    }
});



MWF.xApplication.Execution.MinderExplorer.WorkView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.Execution.MinderExplorer.WorkDocument(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        if (!count)count = 20;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        if(id=="(0)")this.app.createShade();
        var filter = this.filterData || {};
        this.actions.getCenterWorkListNext(id, count, filter, function (json) {
            if (callback)callback(json);
            this.app.destroyShade();
        }.bind(this))
    },
    _removeDocument: function(documentData, all){
        //this.actions.deleteSchedule(documentData.id, function(json){
        //    this.reload();
        //    this.app.notice(this.app.lp.deleteDocumentOK, "success");
        //}.bind(this));
    },
    _create: function(){

    },
    _openDocument: function( documentData ){
        var workMinder = new MWF.xApplication.Execution.WorkMinder( this.explorer, documentData, {});
        workMinder.load();
    },
    _queryCreateViewNode: function(){

    },
    _postCreateViewNode: function( viewNode ){

    },
    _queryCreateViewHead:function(){

    },
    _postCreateViewHead: function( headNode ){

    }

});

MWF.xApplication.Execution.MinderExplorer.WorkDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    mouseoverDocument : function(){
        this.node.getElements("[styles='documentItemTitleNode']").setStyles(this.css["documentItemTitleNode_over"]);
        this.node.getElements("[styles='documentItemIconNode']").setStyles(this.css["documentItemIconNode_over"]);
        this.node.getElements("[styles='documentItemStatNode']").setStyles(this.css["documentItemStatNode_over"]);
    },
    mouseoutDocument : function(){
        this.node.getElements("[styles='documentItemTitleNode']").setStyles(this.css["documentItemTitleNode"]);
        this.node.getElements("[styles='documentItemIconNode']").setStyles(this.css["documentItemIconNode"]);
        this.node.getElements("[styles='documentItemStatNode']").setStyles(this.css["documentItemStatNode"]);
    },
    _queryCreateDocumentNode:function( itemData ){
    },
    _postCreateDocumentNode: function( itemNode, itemData ){

    },
    removeCenterWork : function(itemData){
        //如果是管理员有删除部署的中心工作的权限
        //if(isAdmin){
        //    return true;
        //}
        return false;
    }
});

