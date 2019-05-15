MWF.xApplication.ExeManager = MWF.xApplication.ExeManager || {};
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("Execution","Attachment",null,false);

MWF.require("MWF.widget.Identity", null,false);

MWF.xApplication.ExeManager.CenterWorkList = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },

    initialize: function (node, app, actions, options) {
        this.setOptions(options);
        this.app = app;
        this.lp = app.lp;
        this.path = "/x_component_ExeManager/$CenterWorkList/";
        this.loadCss();

        this.actions = actions;
        this.node = $(node);
    },
    loadCss: function () {
        this.cssPath = "/x_component_ExeManager/$CenterWorkList/" + this.options.style + "/css.wcss";
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
            "text": this.lp.centerWorkList.remove
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
            "text": this.lp.centerWorkList.searchAction
        }).inject(this.toolBarSearchDiv);
        this.toolBarSearchActionBtn.addEvents({
            "click":function(e){
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
        this.toolBarStatusAllDiv = new Element("div.toolBarStatusAllDiv",{styles:this.css.toolBarStatusAllDiv}).inject(this.toolBarStatusDiv)
        this.toolBarStatusPercentDiv = new Element("div.toolBarStatusPercentDiv",{styles:this.css.toolBarStatusPercentDiv}).inject(this.toolBarStatusDiv)
    },
    removeDocument:function(e) {
        var _self = this;
        var flag = true;
        this.app.confirm("warn",e,this.lp.centerWorkList.warnTitle,this.lp.centerWorkList.warnContent,300,120,function(){

            _self.toolBarStatusDiv.setStyle("display","");
            //removeDoc(_self.checkedDoc)
            __self = this;
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
                        _self.actions.deleteCenterWork(removeDocs[0].data.id,function(json){

                        }.bind(_self),function(xhr,error,text){
                            _self.app.notice(_self.lp.centerWorkList.removeResult.failure+":"+removeDocs[0].data.title,"error");
                            flag = false
                        }.bind(_self),false)
                    }

                    removeDocs = removeDocs.slice(1,removeDocs.length);
                }
            }.bind(_self),10)

        },function(){
            this.close();
        })
    },
    createContentDiv: function(key){
        if(this.contentDiv) this.contentDiv.destroy();
        this.contentDiv = new Element("div.contentDiv",{"styles":this.css.contentDiv}).inject(this.middleContent);

        if(this.scrollBar && this.scrollBar.scrollVAreaNode){
            this.scrollBar.scrollVAreaNode.destroy()
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
        templateUrl = this.path+"listItem.json";

        var filter = {
            filterLikeContent:key
        };

        if(this.view) delete this.view;
        this.view =  new  MWF.xApplication.ExeManager.CenterWorkList.View(this.contentDiv, this.app, {explorer:this,lp : this.lp.centerWorkList, css : this.css, actions : this.actions }, { templateUrl : templateUrl,category:"",filterData:filter } );
        this.view.load();



    },

    searchView: function(key){
        this.createContentDiv(key);
        this.resizeWindow();
    },















    showErrorMsg: function(xhr,text,error){
        var errorText = error;
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







MWF.xApplication.ExeManager.CenterWorkList.View = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data){
        return new MWF.xApplication.ExeManager.CenterWorkList.Document(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        if (!count)count = 20;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        var filter = this.options.filterData || {};
        if(id=="(0)")this.app.createShade();
        this.actions.getCenterWorkListNext(id, count, filter, function (json) {
            if (callback)callback(json);
            this.app.destroyShade();
        }.bind(this),function(xhr,error,text){

        }.bind(this))

    },
    _create: function(){

    },
    _openDocument: function( documentData ){
        this.workForm = new MWF.xApplication.ExeManager.CenterWorkList.WorkForm(this.explorer.explorer, this.actions, documentData, {
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

MWF.xApplication.ExeManager.CenterWorkList.Document = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,

    action_remove: function(e){
        var _self = this;
        this.app.confirm("warn",e,this.lp.warnTitle,this.lp.warnContent,300,120,function(){
            _self.actions.deleteCenterWork(_self.data.id,function(json){
                _self.view.explorer.explorer.reload();
                _self.view.explorer.explorer.resizeWindow();
            }.bind(_self),function(xhr,error,text){
                _self.view.explorer.showErrorMsg(xhr,error,text);
            }.bind(_self),false);

            _self.app.notice(_self.lp.removeResult.success, "success");
            this.close();
        },function(){
            this.close();
        })
    }
});


MWF.xApplication.ExeManager.CenterWorkList.WorkForm = new Class({
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
        this.lp = this.app.lp.centerWorkForm;
        this.actions = this.app.restActions;
        this.path = "/x_component_ExeManager/$CenterWorkList/";
        this.cssPath = this.path + this.options.style + "/centerWorkForm.wcss";

        this._loadCss();

        this.options.title = this.lp.title;

        this.data = data || {};

        this.actions = actions;
    },
    load: function () {
        //if(this.data.title) this.data.centerWorkTitle = this.data.title

        //this.data.centerWorkTitle = "ttttttttttttttttttttt"
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
                "text": this.options.title + ( this.data.title ? ("-" + this.data.title ) : "" )
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
        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
            "<tr>" +
            "   <td styles='formTableTitle' lable='centerWorkTitle'></td>" +
            "   <td colspan='3' styles='formTableValue' item='centerWorkTitle'></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='checkLeader'></td>" +
            "   <td colspan='3' styles='formTableValue' item='checkLeader'></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='workType'></td>" +
            "   <td styles='formTableValue' item='workType'></td>" +
            "   <td styles='formTableTitle' lable='defaultCompleteDateLimitStr'></td>" +
            "   <td styles='formTableValue' item='defaultCompleteDateLimitStr'></td>" +
            "</tr><tr>" +
            "   <td styles='formTableTitle' lable='workDescription'></td>" +
            "   <td styles='formTableValue' item='workDescription' colspan='3'></td>" +
            "</tr>"+
            "</table>";
        this.formTableArea.set("html", html);

        this.loadForm();
    },
    loadForm: function(){

        this.form = new MForm(this.formTableArea, this.data, {
            style: "execution",
            isEdited: this.isEdited || this.isNew,
            itemTemplate: this.getItemTemplate(this.lp )
        },this.app);
        this.form.load();

        //this.attachmentArea = this.formTableArea.getElement("[item='attachments']");
        //this.loadAttachment( this.attachmentArea );

    },
    getItemTemplate: function( lp ){
        _self = this;
        return {
            centerWorkTitle: {text: lp.centerWorkTitle + ":",name:"title"},
            checkLeader:{text:lp.checkLeader+":",name:"reportAuditLeaderIdentity"},
            workType:{text:lp.workType+":",name:"defaultWorkType"},
            defaultCompleteDateLimitStr:{text:lp.defaultCompleteDateLimitStr+":",name:"defaultCompleteDateLimitStr"},
            workDescription:{text:lp.workDescription+":",name:"description"}
        }
    },
    loadAttachment: function( area ){
        this.attachment = new MWF.xApplication.Execution.Attachment( area, this.app, this.actions, this.app.lp, {
            documentId : this.data.id,
            isNew : this.options.isNew,
            isEdited : this.options.isEdited,
            onQueryUploadAttachment : function(){
                this.attachment.isQueryUploadSuccess = true;
                if( !this.data.id || this.data.id=="" ){
                    var data = this.form.getResult(true, ",", true, false, true);
                    if( !data ){
                        this.attachment.isQueryUploadSuccess = false;
                        return;
                    }
                    if(this.options.isNew){
                        data.title = data.workDetail;
                        data.deployerName = this.app.user;
                        data.creatorName = this.app.user;
                        data.centerId = this.data.centerWorkId || this.data.centerId ;
                    }
                    this.app.restActions.saveTask(data, function(json){
                        if(json.type && json.type == "success"){
                            if(json.data && json.data.id) {
                                this.attachment.options.documentId = json.data.id;
                                this.data.id = json.data.id;
                                //this.options.isNew = false;
                            }
                        }
                    }.bind(this),null,false)
                }
            }.bind(this)
        });
        this.attachment.load();
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