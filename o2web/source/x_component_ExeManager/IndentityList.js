MWF.xApplication.ExeManager = MWF.xApplication.ExeManager || {};
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("ExeManager","Attachment",null,false);

MWF.require("MWF.widget.Identity", null,false);

MWF.xApplication.ExeManager.IndentityList = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },

    initialize: function (node, app, actions, options) {
        this.setOptions(options);
        this.app = app;
        this.lp = app.lp;
        this.path = "/x_component_ExeManager/$IndentityList/";
        this.loadCss();

        this.actions = actions;
        this.node = $(node);
    },
    loadCss: function () {
        this.cssPath = "/x_component_ExeManager/$IndentityList/" + this.options.style + "/css.wcss";
        this._loadCss();
    },
    load: function () {
        this.middleContent = this.app.middleContent;
        this.middleContent.setStyles({"margin-top":"0px","border":"0px solid #f00","background-color":"#ffffff"});
        this.createToolBarContent();
        this.createContentDiv();

        this.resizeWindow();
        this.app.addEvent("resize", function(){
            this.resizeWindow();
        }.bind(this));

    },
    reload: function(){
        this.createToolBarContent();
        this.createContentDiv();
    },
    resizeWindow: function(){

        var size = this.app.middleContent.getSize();
        this.contentDiv.setStyles({"height":(size.y-110)+"px"});
    },
    createToolBarContent: function(){
        if(this.toolBarDiv) this.toolBarDiv.destroy();
        this.toolBarDiv = new Element("div.toolBarDiv",{"styles":this.css.toolBarDiv}).inject(this.middleContent);
        this.toolBarActionDiv = new Element("div.toolBarActionDiv",{"styles":this.css.toolBarActionDiv}).inject(this.toolBarDiv);

        this.toolBarSearchDiv = new Element("div.toolBarSearchDiv",{"styles":this.css.toolBarSearchDiv}).inject(this.toolBarDiv);
        this.toolBarSearchInput = new Element("input.toolBarSearchInput",{
            "styles":this.css.toolBarSearchInput
        }).inject(this.toolBarSearchDiv);
        this.toolBarSearchInput.addEvents({
            "keyup":function(e){
                if(e.code == 13){
                    this.searchView(this.toolBarSearchInput.get("value"))
                }
            }.bind(this)
        });
        this.toolBarSearchActionBtn = new Element("div.toolBarSearchBtn",{
            "styles":this.css.toolBarSearchBtn,
            "text": this.lp.IndentityList.searchAction
        }).inject(this.toolBarSearchDiv);
        this.toolBarSearchActionBtn.addEvents({
            "click":function(){
                this.searchView(this.toolBarSearchInput.get("value"))
            }.bind(this)
        });
        //this.toolBarSearchResetBtn = new Element("div.toolBarSearchBtn",{
        //    "styles":this.css.toolBarSearchBtn,
        //    "text":"重置"
        //}).inject(this.toolBarSearchDiv);

        this.toolBarStatusDiv = new Element("div.toolBarStatusDiv",{
            "styles":this.css.toolBarStatusDiv
        }).inject(this.toolBarDiv);
        this.toolBarStatusDiv.setStyle("display","none");
        this.toolBarStatusAllDiv = new Element("div.toolBarStatusAllDiv",{styles:this.css.toolBarStatusAllDiv}).inject(this.toolBarStatusDiv);
        this.toolBarStatusPercentDiv = new Element("div.toolBarStatusPercentDiv",{styles:this.css.toolBarStatusPercentDiv}).inject(this.toolBarStatusDiv);


    },

    createContentDiv: function(key){
        if(this.contentDiv) this.contentDiv.destroy();
        this.contentDiv = new Element("div.contentDiv",{"styles":this.css.contentDiv}).inject(this.middleContent);

        if(this.scrollBar && this.scrollBar.scrollVAreaNode){
            this.scrollBar.scrollVAreaNode.destroy();
        }
        MWF.require("MWF.widget.ScrollBar", function () {
            this.scrollBar =  new MWF.widget.ScrollBar(this.contentDiv, {
                "indent": false,
                "style": "xApp_TaskList",
                "where": "before",
                "distance": 30,
                "friction": 4,
                "axis": {"x": false, "y": true},
                "onScroll": function (y) {
                    var scrollSize = this.contentDiv.getScrollSize();
                    var clientSize = this.contentDiv.getSize();
                    var scrollHeight = scrollSize.y - clientSize.y;
                    var view = this.view;
                    if (y + 200 > scrollHeight && view && view.loadElementList) {
                        if (! view.isItemsLoaded) view.loadElementList();
                    }
                }.bind(this)
            });
        }.bind(this),false);
        var templateUrl = this.path+"listItem.json";

        var filter = {
            identity:key
        };

        if(this.view) delete this.view;
        this.view =  new  MWF.xApplication.ExeManager.IndentityList.View(this.contentDiv, this.app, {explorer:this,lp : this.lp.IndentityList, css : this.css, actions : this.actions }, { templateUrl : templateUrl,category:"",filterData:filter } );
        this.view.load();



    },

    searchView: function(key){
        this.createContentDiv(key);
        this.resizeWindow();
    },



    showErrorMsg: function(xhr,text,error){
        var errorText = error;
        var errorMessage;
        if (xhr) errorMessage = xhr.responseText;
        try{
            var e = JSON.parse(errorMessage);
            if (e && e.message) {
                this.app.notice(e.message, "error");
            } else {
                this.app.notice(errorText, "error");
            }
        }catch(e){
            this.app.notice("failure", "error");
        }

    }
});



MWF.xApplication.ExeManager.IndentityList.View = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.ExeManager.IndentityList.Document(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        if (!count)count = 20;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        var filter = this.options.filterData || {};
        if(id=="(0)")this.app.createShade();

        this.actions.getErrorIndentitytNext(id, count, filter, function (json) {
            if (callback)callback(json);
            this.app.destroyShade();
        }.bind(this),function(xhr,error,text){
            this.explorer.explorer.showErrorMsg(xhr,error,text)
        }.bind(this))

    },
    _create: function(){

    },
    _openDocument: function( documentData ){
        this.indentityForm = new MWF.xApplication.ExeManager.IndentityList.IndentityForm(this.explorer.explorer, this.actions, documentData, {
            "isNew": false,
            "isEdited": false,
            "onReloadView" : function( data ){
                this.app.topBarContent.getElements(".topBarLi").each(function(d){
                    if(d.get("id") == "topIndentity"){
                        d.click()
                    }
                }.bind(this));
            }.bind(this)
        });
        this.indentityForm.load();


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

MWF.xApplication.ExeManager.IndentityList.Document = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,

    action_open: function(e){

    }
});


MWF.xApplication.ExeManager.IndentityList.IndentityForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "90%",
        "height": "100%",
        "top" : 0,
        "left" : 0,
        "hasTop": true,
        "hasIcon": false,
        "hasBottom": true,
        "title": "",
        "draggable": false,
        "closeAction": true,
        "isNew": false,
        "isEdited": false
    },
    initialize: function (explorer, actions, data, options) {
        this.setOptions(options);
        this.explorer = explorer;
        this.app = explorer.app;
        this.lp = this.app.lp.indentityForm;
        this.actions = this.app.restActions;
        this.path = "/x_component_ExeManager/$IndentityList/";
        this.cssPath = this.path + this.options.style + "/indentityForm.wcss";

        this._loadCss();

        this.options.title = this.lp.title;

        this.data = data || {};

        this.actions = actions;
    },
    load: function () {
        if(this.data.id){
            //var data = {identity:"史敬(市场部)"}
            var data = {identity:this.data.identity};
            this.actions.getErrorIdentityDetail(data,function(json){
                if(json.data) this.detailsData = json.data;
            }.bind(this),null,false)
        }
        if (this.options.isNew) {
            this.create();
        } else if (this.options.isEdited) {
            this.edit();
        } else {
            this.open();
        }
    },
    _open: function () {
        this.formMarkNode = new Element("div.formMarkNode", {
            "styles": this.css.formMarkNode,
            "events": {
                "mouseover": function (e) {
                    e.stopPropagation();
                },
                "mouseout": function (e) {
                    e.stopPropagation();
                },
                "click": function (e) {
                    e.stopPropagation();
                }
            }
        }).inject(this.app.content);

        this.formAreaNode = new Element("div.formAreaNode", {
            "styles": this.css.formAreaNode
        });

        this.createFormNode();

        this.formAreaNode.inject(this.formMarkNode, "after");
        this.formAreaNode.fade("in");

        this.setFormNodeSize();
        this.setFormNodeSizeFun = this.setFormNodeSize.bind(this);
        this.app.addEvent("resize", this.setFormNodeSizeFun);

        if (this.options.draggable && this.formTopNode) {
            var size = this.app.content.getSize();
            var nodeSize = this.formAreaNode.getSize();
            this.formAreaNode.makeDraggable({
                "handle": this.formTopNode,
                "limit": {
                    "x": [0, size.x - nodeSize.x],
                    "y": [0, size.y - nodeSize.y]
                }
            });
        }

    },
    _close: function(){
        if(this.formMarkNode)this.formMarkNode.destroy()
    },
    createTopNode: function () {
        if (!this.formTopNode) {
            this.formTopNode = new Element("div.formTopNode", {
                "styles": this.css.formTopNode
            }).inject(this.formNode);

            this.formTopIconNode = new Element("div", {
                "styles": this.css.formTopIconNode
            }).inject(this.formTopNode);

            this.formTopTextNode = new Element("div", {
                "styles": this.css.formTopTextNode,
                "text": this.options.title
            }).inject(this.formTopNode);

            if (this.options.closeAction) {
                this.formTopCloseActionNode = new Element("div", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
                this.formTopCloseActionNode.addEvent("click", function () {
                    this.close()
                }.bind(this))
            }

            this.formTopContentNode = new Element("div", {
                "styles": this.css.formTopContentNode
            }).inject(this.formTopNode);

            this._createTopContent();

        }
    },
    _createTopContent: function () {

    },
    _createTableContent: function () {
       if(this.formTableArea) this.formTableArea.empty();
        this.modifyDiv = new Element("div.modifyDiv",{"styles":this.css.modifyDiv}).inject(this.formTableArea);
        this.oldIdentitySpan = new Element("span.oldIdentitySpan",{"styles":this.css.oldIdentitySpan,"text":this.lp.oldIdentity+": "+this.data.identity}).inject(this.modifyDiv);
        this.newIdentitySpan = new Element("span.newIdentitySpan",{"styles":this.css.newIdentitySpan,"text":this.lp.newIdentity+": "}).inject(this.modifyDiv);
        this.newIdentityInput = new Element("input.newIdentityInput",{
            "styles":this.css.newIdentityInput,
            "readonly":true,
            "type":"text"
        }).inject(this.modifyDiv);
        this.newIdentityInput.addEvents({
            "click":function(){
                this.selectPerson(this.newIdentityInput,"identity");
            }.bind(this)
        });
        this.modifyBottonDiv = new Element("div.modifyBottonDiv",{
            "styles":this.css.modifyBottonDiv,
            "text":this.lp.modify
        }).inject(this.modifyDiv);

        this.modifyBottonDiv.addEvents({
            "click":function(e){
                if(this.newIdentityInput.get("value") == ""){
                    this.app.notice(this.lp.newIdentityEmpty,"error");
                }else{
                    this.replaceIdentity(e);
                }
            }.bind(this)
        });
        this.contentDiv = new Element("div.contentDiv",{"styles":this.css.contentDiv}).inject(this.formTableArea);
        this.naviTabDiv = new Element("div.naviTabDiv",{"styles":this.css.naviTabDiv}).inject(this.contentDiv);

        if(this.detailsData){
            var _self = this;
            this.detailsData.each(function(d){
                var categoryLi = new Element("li.categoryLi",{
                    "styles" : this.css.categoryLi,
                    "text": d.recordType
                }).inject(this.naviTabDiv);
                categoryLi.addEvents({
                    "click":function(){
                        _self.loadList(this);
                    }
                });
            }.bind(_self));
            _self.naviTabDiv.getElements("li")[0].click();
        }

    },
    _createBottomContent: function () {
        this.cancelActionNode = new Element("div.formCancelActionNode", {
            "styles": this.css.formCancelActionNode,
            "text": this.lp.close
        }).inject(this.formBottomNode);


        this.cancelActionNode.addEvent("click", function (e) {
            this.cancel(e);
        }.bind(this));

    },
    loadList:function(o){
        this.naviTabDiv.getElements("li").setStyles({"border-bottom":""});
        o.setStyles({"border-bottom":"2px solid #124c93"});
        if(this.contentList) this.contentList.destroy();
        this.contentList = new Element("div.contentList",{"styles":this.css.contentList}).inject(this.formTableArea);
        if(this.table) this.table.destroy();
        this.table = new Element("table.table",{"styles":{"width":"100%"}}).inject(this.contentList);
        var tr = new Element("tr.tr").inject(this.table);
        var td = new Element("td.td",{"text":this.lp.tableTitle,"align":"left","styles":{"padding-left":"200px","font-weight":"bold"}}).inject(tr);
        this.detailsData.each(function(d){
            if(d.recordType == o.get("text")){
                this.listData = d.errorRecords;
            }
        }.bind(this));

        this.listData.each(function(d,i){
            tr = new Element("tr.tr").inject(this.table);
            var color = i%2 == 0?"#f1f1f1":"#fff";
            tr.setStyles({"background-color":color});
            td = new Element("td.td",{
                "styles":this.css.tableTd,
                "text": d.title
            }).inject(tr)
        }.bind(this))



    },
    replaceIdentity:function(e){
        var _self = this;
        this.app.confirm("warn",e,this.lp.warn.warnTitle,this.lp.warn.warnContent,300,120,function(){
            var submitData = {
                oldIdentity:_self.data.identity,
                newIdentity :_self.newIdentityInput.get("value"),
                recordType:"all",
                recordId:"all",
                tableName:"all"
            };
            _self.app.createShade();
            _self.actions.replaceErrorIdentity(submitData, function(json){
                _self.app.notice(_self.lp.modifySuccess,"success");
                _self.app.destroyShade();
                _self.close();
                _self.fireEvent("reloadView");

            }.bind(_self),function(xhr,text,error){
                _self.app.showErrorMessage(xhr,text,error);
                _self.app.destroyShade();
            }.bind(_self));


            this.close();

        },function(){
            this.close();
        })
    },
    selectPerson: function( item, type ){
        MWF.xDesktop.requireApp("Organization", "Selector.package",null, false);
        this.fireEvent("querySelect", this);
        var value = item.get("value").split( this.valSeparator );
        var options = {
            "type": type,
            "title": "select",
            "count" : 1,
            "names": value || [],
            //"departments" : this.options.departments,
            //"companys" : this.options.companys,
            "onComplete": function(items){
                var arr = [];
                items.each(function(item){
                    arr.push(item.data.name);
                }.bind(this));
                item.set("value",arr.join(","));
                //this.items[0].fireEvent("change");
            }.bind(this)
        };
        var selector = new MWF.OrgSelector(this.app.content, options);
    }
});