MWF.xApplication.ExeManager = MWF.xApplication.ExeManager || {};
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("ExeManager","Attachment",null,false);

MWF.require("MWF.widget.Identity", null,false);

MWF.xApplication.ExeManager.WorkReportList = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },

    initialize: function (node, app, actions, options) {
        this.setOptions(options);
        this.app = app;
        this.lp = app.lp;
        this.path = "/x_component_ExeManager/$WorkReportList/";
        this.loadCss();

        this.actions = actions;
        this.node = $(node);
    },
    loadCss: function () {
        this.cssPath = "/x_component_ExeManager/$WorkReportList/" + this.options.style + "/css.wcss";
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
        this.toolBarActionBRemoveBtn = new Element("div.toolBarActionBRemoveBtn",{
            "styles":this.css.toolBarActionBtn,
            "text": this.lp.baseWorkList.remove
        }).inject(this.toolBarActionDiv);
        this.toolBarActionBRemoveBtn.addEvents({
            "click":function(e){
                this.checkedDoc = this.view.getCheckedItems();
                this.removeDocument(e);
            }.bind(this)
        });
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
            "text": this.lp.baseWorkList.searchAction
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
    removeDocument:function(e) {
        var _self = this;
        var flag = true;
        this.app.confirm("warn",e,this.lp.baseWorkList.warnTitle,this.lp.baseWorkList.warnContent,300,120,function(){
            _self.toolBarStatusDiv.setStyle("display","");
            //removeDoc(_self.checkedDoc)
            var __self = this;
            var removeDocs = _self.checkedDoc;
            var removeDocsLen = removeDocs.length;
            var removeDocCurLen = 0;

            var timeInt = window.setInterval(function(){
                if(removeDocs.length==0 || !flag){
                    clearInterval(timeInt);
                    __self.close();
                    _self.reload();
                    _self.resizeWindow();
                }else{
                    removeDocCurLen ++;
                    var _width = removeDocCurLen / removeDocsLen;
                    _width = _width  * _self.toolBarStatusAllDiv.getSize().x;
                    _self.toolBarStatusPercentDiv.set("text",removeDocCurLen+"/"+removeDocsLen);
                    _self.toolBarStatusPercentDiv.setStyles({"width":_width+"px"});
                    if( flag && removeDocs[0].data && removeDocs[0].data.id){
                        _self.actions.deleteWorkReport(removeDocs[0].data.id,function(json){

                        }.bind(_self),function(){
                            _self.app.notice(_self.lp.baseWorkList.removeResult.failure+":"+removeDocs[0].data.title,"error");
                            flag = false
                        }.bind(_self),false)
                    }

                    removeDocs = removeDocs.slice(1,removeDocs.length);
                }

            }.bind(_self),10);

        },function(){
            this.close();
        })

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
            filterLikeContent:key
        };

        if(this.view) delete this.view;
        this.view =  new  MWF.xApplication.ExeManager.WorkReportList.View(this.contentDiv, this.app, {explorer:this,lp : this.lp.workReportList, css : this.css, actions : this.actions }, { templateUrl : templateUrl,category:"",filterData:filter } );
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




MWF.xApplication.ExeManager.WorkReportList.View = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.ExeManager.WorkReportList.Document(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        if (!count)count = 20;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        var filter = this.options.filterData || {};
        if(id=="(0)")this.app.createShade();

        this.actions.getReportWorkListNext(id, count, filter, function (json) {
            if (callback)callback(json);
            this.app.destroyShade();
        }.bind(this),function(xhr,error,text){
            this.explorer.explorer.showErrorMsg(xhr,error,text)
        }.bind(this))

    },
    _create: function(){

    },
    _openDocument: function( documentData ){
        this.workForm = new MWF.xApplication.ExeManager.WorkReportList.WorkReportForm(this.explorer.explorer, this.actions, documentData, {
            "isNew": false,
            "isEdited": false,
            "onPostSave" : function(){
                this.view.explorer.contentChanged = true;
            }.bind(this)
        });
        this.workForm.load();


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

MWF.xApplication.ExeManager.WorkReportList.Document = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,

    action_remove: function(e){
        var _self = this;
        this.app.confirm("warn",e,this.lp.warnTitle,this.lp.warnContent,300,120,function(){
            _self.actions.deleteWorkReport(_self.data.id,function(){
                _self.app.notice(_self.lp.removeResult.success, "success");
                _self.view.explorer.explorer.reload();
                _self.view.explorer.explorer.resizeWindow();
            }.bind(_self),function(xhr,error,text){
                _self.view.explorer.explorer.showErrorMsg(xhr,error,text);
            }.bind(_self),false);

            this.close();
        },function(){
            this.close();
        })
    }
});


MWF.xApplication.ExeManager.WorkReportList.WorkReportForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "800",
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
        this.lp = this.app.lp.workReportForm;
        this.actions = this.app.restActions;
        this.path = "/x_component_ExeManager/$WorkReportList/";
        this.cssPath = this.path + this.options.style + "/workReportForm.wcss";

        this._loadCss();

        this.options.title = this.lp.title;

        this.data = data || {};

        this.actions = actions;
    },
    load: function () {
        if(this.data.id){
            this.actions.getWorkReport(this.data.id, function (json) {
                this.workReportData = json.data;
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
                "text": this.options.title + ( this.workReportData.title ? ("-" + this.workReportData.title ) : "" )
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
        this.contentDiv = new Element("div.contentDiv",{"styles":this.css.contentDiv}).inject(this.formTableArea);
        var titleDiv = new Element("div.titleDiv",{"styles":this.css.titleDiv,"text":this.lp.title}).inject(this.contentDiv);
        var valueDiv = new Element("div.valueDiv",{"styles":this.css.valueDiv,"text":this.workReportData.title}).inject(this.contentDiv);

        titleDiv = new Element("div.titleDiv",{"styles":this.css.titleDiv,"text":this.lp.times}).inject(this.contentDiv);
        valueDiv = new Element("div.valueDiv",{"styles":this.css.valueDiv,"text":this.workReportData.shortTitle}).inject(this.contentDiv);
        titleDiv = new Element("div.titleDiv",{"styles":this.css.titleDiv,"text":this.lp.progressDescription}).inject(this.contentDiv);
        valueDiv = new Element("div.valueDiv",{"styles":this.css.valueDiv,"text":this.workReportData.progressDescription}).inject(this.contentDiv);
        titleDiv = new Element("div.titleDiv",{"styles":this.css.titleDiv,"text":this.lp.workPlan}).inject(this.contentDiv);
        valueDiv = new Element("div.valueDiv",{"styles":this.css.valueDiv,"text":this.workReportData.workPlan}).inject(this.contentDiv);
    },
    _createBottomContent: function () {
        this.cancelActionNode = new Element("div.formCancelActionNode", {
            "styles": this.css.formCancelActionNode,
            "text": this.lp.close
        }).inject(this.formBottomNode);


        this.cancelActionNode.addEvent("click", function (e) {
            this.cancel(e);
        }.bind(this));

    }
});