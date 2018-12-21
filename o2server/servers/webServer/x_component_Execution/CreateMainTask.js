MWF.xApplication.Execution = MWF.xApplication.Execution || {};

MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);

MWF.xApplication.Execution.CreateMainTask = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "95%",
        "height": "95%",
        "hasTop": true,
        "hasIcon": false,
        "hasBottom": true,
        "title": "",
        "draggable": true,
        "closeAction": true,
        "isNew": false,
        "isEdited": false
    },
    initialize: function (explorer, actions, data, options) {
        this.setOptions(options);
        this.explorer = explorer;
        this.app = explorer.app;
        this.lp = this.app.lp;
        this.path = "/x_component_Execution/$CreateMainTask/";
        this.cssPath = this.path + this.options.style + "/css.wcss";
        this._loadCss();

        this.options.title = "";

        this.data = data || {};

        this.actions = actions;
    },
    load: function () {
        if (this.options.isNew) {
            this.create();
        } else if (this.options.isEdited) {
            this.edit();
        } else {
            this.open();
        }


        var vh = this.formContentNode.getSize().y-this.mainTaskDiv.getSize().y
        vh = vh-30
        //this.subTaskDiv.setStyles({"height":vh+"px"});

    },
    _open : function(){
        this.formMaskNode = new Element("div.formMaskNode", {
            "styles": this.css.formMaskNode,
            "events": {
                "mouseover": function(e){e.stopPropagation();},
                "mouseout": function(e){e.stopPropagation();}
            }
        }).inject(this.app.content );

        this.formAreaNode = new Element("div.formAreaNode", {
            "styles": this.css.formAreaNode
        });

        this.createFormNode();

        this.formAreaNode.inject(this.formMaskNode, "after");
        this.formAreaNode.fade("in");

        this.setFormNodeSize();
        this.setFormNodeSizeFun = this.setFormNodeSize.bind(this);
        this.addEvent("resize", this.setFormNodeSizeFun);

        if( this.options.draggable && this.formTopNode ){
            var size = this.app.content.getSize();
            var nodeSize = this.formAreaNode.getSize();
            this.formAreaNode.makeDraggable({
                "handle": this.formTopNode,
                "limit": {
                    "x": [0, size.x-nodeSize.x],
                    "y": [0, size.y-nodeSize.y]
                }
            });
        }

    },
    createTopNode: function () {
        var htmlStr="";
        if (!this.formTopNode) {
            this.formTopNode = new Element("div.formTopNode", {
                "styles": this.css.formTopNode
            }).inject(this.formNode);



            //this.formTopTextNode = new Element("div", {
            //    "styles": this.css.formTopTextNode,
            //    "text": this.options.title
            //}).inject(this.formTopNode)

            if (this.options.closeAction) {
                this.formTopCloseActionNode = new Element("div.formTopCloseActionNode", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
                this.formTopCloseActionNode.addEvent("click", function () {
                    this.close()
                }.bind(this))
            }

            this.formTopContentNode = new Element("div", {
                "styles": this.css.formTopContentNode
            }).inject(this.formTopNode)

            this._createTopContent();

        }
    },
    _createTopContent: function () {

        this.topTitleLi = new Element("li.topTitleLi", {
            "styles": this.css.topTitleLi
        }).inject(this.formTopContentNode);
        htmlStr = "<img class='topTitleImg' style='width:25px; height:25px;margin-top:10px;' src='/x_component_Execution/$Main/default/icon/Document-104.png' />";
        htmlStr += "<span class='topTitleSpan' style='position:absolute;margin-top:0px;'>"+MWF.xApplication.Execution.LP.createMainTask.topTitle+"</span>";
        this.topTitleLi.set("html",htmlStr);


        this.user = layout.desktop.session.user.name;
        this.userGender = layout.desktop.session.user.genderType;
        this.department="";
        this.restActions = new MWF.xApplication.Execution.Actions.RestActions();
        this.restActions.listDepartmentByPerson( function( json ){
            this.department = json["data"][0]["display"];
        }.bind(this), null, layout.desktop.session.user.name, false);
        var nowTime = new Date();
        var nowFormat = nowTime.getFullYear()+"-"+(nowTime.getMonth()+1)+"-"+nowTime.getDay()+" "+nowTime.getHours()+":"+nowTime.getMinutes()+":"+nowTime.getSeconds();

        this.topInforLi = new Element("li.topInforLi",{
            "styles":this.css.topInforLi
        }).inject(this.formTopContentNode);
        this.topInforDrafterSpan = new Element("span.topInforDrafterSpan",{
            "styles":this.css.topInforSpan,
            "text":MWF.xApplication.Execution.LP.createMainTask.drafter+this.user
        }).inject(this.topInforLi);
        this.topInforDeptSpan = new Element("span.topInforDeptSpan",{
            "styles":this.css.topInforSpan,
            "text":MWF.xApplication.Execution.LP.createMainTask.drafterDept+this.department
        }).inject(this.topInforLi);
        this.topInforDateSpan = new Element("span.topInforDateSpan",{
            "styles":this.css.topInforSpan,
            "text":MWF.xApplication.Execution.LP.createMainTask.drafterDate+nowFormat
        }).inject(this.topInforLi);

    },
    _createTableContent: function () {
        this.mainTaskDiv = new Element("div.mainTaskDiv", {
            "styles": this.css.mainTaskDiv
        }).inject(this.formTableArea);

        this.createMainTask();
        this.createTaskList();
        //this.createBottomAction();
    },
    reloadMainTask: function( data ){
        this.mainTaskDiv.empty();
        this.createMainTask( data );
    },
    createMainTask: function( data ) {
        if(data){
            this.topInforDrafterSpan.set("text",MWF.xApplication.Execution.LP.createMainTask.drafter+data.creatorName)
            this.topInforDeptSpan.set("text",MWF.xApplication.Execution.LP.createMainTask.drafterDept+data.creatorOrganizationName)
            this.topInforDateSpan.set("text",MWF.xApplication.Execution.LP.createMainTask.drafterDate+data.createTime)
        }
        data = data || {
                processIdentity : this.app.identity,
                deployerName : this.app.user,
                creatorName : this.app.user
            }
        if(data.id){
            this.centerWorkId = data.id;
        }
        if(this.data.id){
            this.centerWorkId = this.data.id;
            this.getMainTask(this.centerWorkId, function( json ){
                data = json.data
            }.bind(this));

        }

        //alert(JSON.stringify(data))
        this.mainTaskTitleDiv = new Element("div.mainTaskTitleDiv", {
            "styles": this.css.mainTaskTitleDiv,
            "text": this.lp.createMainTask.mainTask.topTitle
        }).inject(this.mainTaskDiv);

        this.mainTaskContentDiv = new Element("div.mainTaskContentDiv").inject(this.mainTaskDiv);

        var html = "<table width='100%' border='0'  styles=''>" +
            "<tr><td styles='mainTaskTitle' lable='mainTaskTitle'></td>" +
            "    <td styles='mainTaskTitleValue' colspan='5' item='mainTaskTitle'></td></tr>" +
            "<tr>" +
                "<td styles='mainTaskCategory' lable='mainTaskCategory'></td>" +
                "<td styles='mainTaskCategoryValue' item='mainTaskCategory'></td>" +
                "<td styles='mainTaskLevel' lable='mainTaskLevel'></td>" +
                "<td styles='mainTaskLevelValue' item='mainTaskLevel'></td>" +
                "<td styles='mainTaskLimit' lable='mainTaskLimit'></td>" +
                "<td styles='mainTaskLimitValue' item='mainTaskLimit'></td>" +
            "</tr>" +

            "<tr><td styles='mainTaskDescription' lable='mainTaskDescription'></td>" +
            "    <td styles='mainTaskDescriptionValue' item='mainTaskDescription' colspan='5'></td></tr>" +
            "</table>"


        this.mainTaskContentDiv.set("html", html);

        var form=this.form = new MForm(this.mainTaskContentDiv, data, {
            style : "execution",
            isEdited: this.options.isEdited,
            itemTemplate: {
                //deployerName:{
                //    defaultValue:"ffff"
                //},
                mainTaskTitle: {
                    text: this.lp.createMainTask.mainTask.title + "：", type: "text",
                    style:{"color":"#999999"},
                    name : "title",
                    notEmpty:true
                },
                mainTaskCategory: {
                    text: this.lp.createMainTask.mainTask.category+"：",
                    style : {"width":"90%","height":"30px","color":"#999999","border-radius":"1px","box-shadow": "0px 0px 1px #CCC"},
                    type: "select",
                    selectValue : this.lp.createMainTask.mainTask.categoryValue,
                    name : "defaultWorkType",
                    notEmpty:true
                },
                mainTaskLevel: {
                    text: this.lp.createMainTask.mainTask.level + "：",
                    type: "select",
                    name : "defaultWorkLevel",
                    selectValue : this.lp.createMainTask.mainTask.level,
                    style : {"width":"90%","height":"30px","color":"#999999","border-radius":"1px","box-shadow": "0px 0px 1px #CCC"},
                    notEmpty:true
                },
                mainTaskLimit: {
                    text: this.lp.createMainTask.mainTask.limit + "：",
                    type: "text",
                    name : "defaultCompleteDateLimitStr",
                    style:{"width":"90%","color":"#999999"},tType:"date",
                    attr:{"readonly":true},
                    notEmpty:true,
                    event:{

                    }
                },
                mainTaskDescription: {
                    text: this.lp.createMainTask.mainTask.description + "：",
                    type: "textarea",
                    name:"description",
                    notEmpty:true,
                    style:{"height":"60px","color":"#999999"}
                }
            }
        }, this.app, this.css);
        form.load();
    },
    createTaskList : function(){
        this.subTaskDiv = new Element("div.subTaskDiv", {
            "styles": this.css.subTaskDiv
        }).inject(this.formTableArea);

        this.subTaskTitleDiv = new Element("div.subTaskTitleDiv",{
            "styles":this.css.subTaskTitleDiv,
            "text":this.lp.createMainTask.subTask.topTitle
        }).inject(this.subTaskDiv);
        this.subTaskContentDiv = new Element("div.subTaskContentDiv",{
            "styles":this.css.subTaskContentDiv
        }).inject(this.subTaskDiv);
        this.loadBaseWork();

    },
    createBottomNode : function(){
        this.formBottomNode = new Element("div.formBottomNode", {
            "styles": this.css.formBottomNode
        }).inject(this.formNode);

        this.createBottomAction()
    },
    createBottomAction:function(){
        this.bottomDiv = new Element("div.bottomDiv", {
            "styles": this.css.bottomDiv
        }).inject(this.formBottomNode);

        this.bottomActionLi = new Element("div.bottomActionLi",{
            "styles":this.css.bottomActionLi
        }).inject(this.bottomDiv);
        this.bottomNewWorkSpan = new Element("span.bottomNewWorkSpan",{
            "styles":this.css.bottomNewWorkSpan,
            "text": this.lp.createMainTask.action.newWork
        }).inject(this.bottomActionLi)
            .addEvents({
                "click":function(){
                    var r = this.form.getResult(true,",",true,false,true)
                    if( !r ){
                        return
                    }
                    this.saveMainTask( r, function( json ){
                        if( json.data.status === "ERROR"){
                            this.app.notice(json.data.message, "error")
                        }else{
                            this.getMainTask(json.data.message, function( data ){
                                this.reloadMainTask( data.data )
                                this.openWorkForm( data.data );
                            }.bind(this))
                        }
                    }.bind(this) )
                }.bind(this)
            });
        //this.bottomDraftWorkSpan = new Element("span.bottomDraftWorkSpan",{
        //    "styles":this.css.bottomDraftWorkSpan,
        //    "text": this.lp.createMainTask.action.draftWork
        //}).inject(this.bottomActionLi);
        this.bottomDoWork = new Element("span.bottomDoWork",{
            "styles":this.css.bottomDoWork,
            "text": this.lp.createMainTask.action.doWork
        }).inject(this.bottomActionLi)
            .addEvents({
                "click":function(){
                    var ids = [];
                    this.actions.getUserBaseWork(this.centerWorkId,  function (json) {

                        Array.each(json.data,function(item,index){
                            ids.push(item.id)
                        })

                    }.bind(this),null,false)

                    var sendData = {};
                    sendData.deployerIdentity = "";
                    sendData.workIds = ids

                    this.actions.deployBaseWork(sendData, function (json) {

                    }.bind(this),null,false);
                    if(this.app.workTask.contentDiv)  this.app.workTask.contentDiv.destroy()
                    this.app.workTask.createContentDiv();

                    this.close();
                }.bind(this)
            })

        this.bottomIconLi = new Element("div.bottomIconLi",{
            "styles":this.css.bottomIconLi
        }).inject(this.bottomDiv);
        this.bottomIconImg = new Element("img.bottomIconImg",{
            "styles":this.css.bottomIconImg,
            "src":"/x_component_Execution/$Main/default/icon/okr.png"
        }).inject(this.bottomIconLi);
    },
    saveMainTask: function(data, callback){
        this.app.restActions.saveMainTask( data, function(json){
            if( callback )callback(json);
        }.bind(this));
    },
    getMainTask: function(id, callback){
        this.app.restActions.getMainTask( id, function(json){
            if( callback )callback(json);
        }.bind(this),null,false);
    },
    openWorkForm : function( data ){

            this.view._create( data );

    },
    loadBaseWork:function(){
        //alert("id="+this.centerWorkId);

        this.subTaskContentDiv.empty();

        //if(this.centerWorkId && this.centerWorkId!=""){
            this.view =  new  MWF.xApplication.Execution.CreateMainTask.BaseWorkView(this.subTaskContentDiv, this.app, this,
                this.css, this.lp.baseWorkView, this.actions, {
                    templateUrl : this.path+"listItem.json", centerWorkId: this.centerWorkId
                } )
            this.view.load();
        //}

    }
})

MWF.xApplication.Execution.CreateMainTask.BaseWorkView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    initialize: function (container, app, explorer, css, lp, actions, options) {
        this.container = container;
        this.app = app;
        this.explorer = explorer;
        this.css = css;
        this.lp = lp
        this.actions = actions;
        if (!options.templateUrl) {
            options.templateUrl = this.explorer.path + "listItem.json"
        } else if (options.templateUrl.indexOf("/") == -1) {
            options.templateUrl = this.explorer.path + options.templateUrl;
        }
        this.setOptions(options);

    },
    _createDocument: function(data){
        return new MWF.xApplication.Execution.CreateMainTask.BaseWorkDocument(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){

        this.centerWorkId = this.options.centerWorkId;
        this.actions.getUserBaseWork(this.centerWorkId,  function (json) {
            //alert(JSON.stringify(json));
            if (callback)callback(json);
        }.bind(this),null,false)

    },
    _removeDocument: function(documentData, all){
        this.actions.deleteBaseWork(documentData.id, function(json){
            this.reload();
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _create: function( data ){
        MWF.xDesktop.requireApp("Execution", "WorkForm", function(){
            this.workform = new MWF.xApplication.Execution.WorkForm(this, this.app.restActions,{"centerWorkId": data.id || this.options.centerWorkId },{
                "isNew": true,
                "isEdited": false
            });
            //alert("cccc="+this.centerWorkId)

            this.workform.load();
        }.bind(this));
    },
    _openDocument: function( documentData ){
        //this.workForm = new MWF.xApplication.Execution.WorkForm(this, this.actions, documentData, {
        //    "isNew": false,
        //    "isEdited": false
        //})
        //this.workForm.load();
        //MWF.xDesktop.requireApp("Execution", "CreateMainTask", function(){
        //    this.centerWork = new MWF.xApplication.Execution.CreateMainTask(this, this.actions,{"id":documentData.id,"name":"fff"},{} );
        //    this.centerWork.load();
        //}.bind(this))


    },
    _queryCreateViewNode: function(){

    },
    _postCreateViewNode: function( viewNode ){

    },
    _queryCreateViewHead:function(){

    },
    _postCreateViewHead: function( headNode ){

    }

})

MWF.xApplication.Execution.CreateMainTask.BaseWorkDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    action_delete : function(){

        this.view.app.restActions.deleteBaseWork( this.data.id, function(json){
            //alert(JSON.stringify(json))
            this.view.app.notice(this.app.lp.deleteDocumentOK, "success");
            this.view.reload();
        }.bind(this));

    },
    action_edit:function(){
        //alert("edit");
        //alert(JSON.stringify(this.data))
        MWF.xDesktop.requireApp("Execution", "WorkForm", function(){
            var workform = new MWF.xApplication.Execution.WorkForm(this.view, this.view.app.restActions,this.data,{
                "isNew": false,
                "isEdited": true
            });

            workform.load();
        }.bind(this));

        //MWF.xDesktop.requireApp("Execution", "CreateMainTask", function(){
        //    //this.clearContent();
        //    //this.explorerContent = new Element("div", {
        //    //	"styles": this.css.rightContentNode
        //    //}).inject(this.node);
        //    this.centerWork = new MWF.xApplication.Execution.CreateMainTask(this.view, this.view.app.RestActions,{"id":this.data.id,"name":"fff"},{} );
        //    this.centerWork.load();
        //}.bind(this))



    },
    _queryCreateDocumentNode:function( itemData ){

    },
    _postCreateDocumentNode: function( itemNode, itemData ){

    }
    //open: function(){
    //    alert("open")
    //}
})