MWF.xApplication.Strategy = MWF.xApplication.Strategy || {};

//MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Strategy", "Template", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("Strategy","Attachment",null,false);

MWF.xApplication.Strategy.KeyWorkForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "90%",
        "height": "100%",
        "hasTop": true,
        "hasIcon": false,
        "hasBottom": false,
        "title": "",
        "draggable": false,
        "closeAction": true
    },
    initialize: function (explorer, actions, data, options) {
        this.setOptions(options);
        this.explorer = explorer;
        this.app = explorer.app;
        this.lp = this.app.lp.keyWork.popupForm;
        this.actions = this.app.restActions;
        this.path = "/x_component_Strategy/$KeyWorkForm/";
        this.cssPath = this.path + this.options.style + "/css.wcss";
        this._loadCss();

        this.options.title = this.lp.title;
        this.defaultYear = this.options.year || "";

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
    },
    createTopNode: function () {
        if (!this.formTopNode) {
            this.formTopNode = new Element("div.formTopNode", {
                "styles": this.css.formTopNode
            }).inject(this.formNode);

            this.formTopTextNode = new Element("div", {
                "styles": this.css.formTopTextNode,
                "text": this.data.title ?  this.data.title  : "增加工作"
            }).inject(this.formTopNode);

            if (this.options.closeAction) {
                this.formTopCloseActionNode = new Element("div", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
                this.formTopCloseActionNode.addEvent("click", function () {
                    this.close()
                }.bind(this))
            }

            this._createTopContent();
        }
    },
    _createTopContent: function () {

    },
    _createTableContent: function () {
        this.getData(function(){this.createTableInfo()}.bind(this));
    },
    getData:function(callback){
        if(!this.options.isNew){
            if(this.data.id){
                this.id = this.data.id
            }else if(this.options.id){
                this.id = this.options.id
            }

            this.actions.getKeyWorkById(this.id,function(json){
                this.data = json.data;
                if(callback)callback();
            }.bind(this));

        }else{
            if(callback)callback();
        }
    },
    createTableInfo:function(){
        var html = "<table styles='formTable'>" +
            "<tr>" +
            "   <td styles='formTableTitle' lable='sequencenumber'></td>" +
            "   <td styles='formTableValue' item='sequencenumber'></td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableTitle' lable='strategydeploytitle'></td>" +
            "   <td styles='formTableValue' item='strategydeploytitle'></td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableTitle' lable='strategydeployyear'></td>" +
            "   <td styles='formTableYearValue' item='strategydeployyear'></td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableTitle' lable='deptlist'></td>" +
            "   <td styles='formTableValue' item='deptlist'></td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableTitle' lable='strategydeploydescribe'></td>" +
            "   <td styles='formTableValue' item='strategydeploydescribe'></td>" +
            "</tr>"+
            "</table>";
        this.formTableArea.set("html", html);

        this.loadForm();

        if(this.options.isNew || this.options.isEdited){
            this.createActionBar();
        }
    },
    loadForm: function(){
        this.keyWorkForm = new MForm(this.formTableArea, this.data, {
            style: "default",
            isEdited: this.isEdited || this.isNew,
            itemTemplate: this.getItemTemplate(this.lp )
        },this.app,this.css);
        this.keyWorkForm.load();
        var taObj = this.formTableArea.getElements("textarea");
        taObj.setStyles({height:"100px"});

        //this.attachmentArea = this.formTableArea.getElement("[item='attachments']");

        //this.loadAttachment( this.attachmentArea );

    },
    getItemTemplate: function( lp ){
        _self = this;
        return {
            "sequencenumber":{
                text:lp.sequencenumber+":",
                notEmpty:true
            },
            "strategydeploytitle":{
                text:lp.title+":",
                notEmpty:true
            },
            "strategydeployyear":{
                text:lp.year+":",
                notEmpty:true,
                type:"select",
                value:this.defaultYear,
                attr : {style:"width:100%;height:30px;border-radius:3px;"},
                selectValue:lp.selectYears.split(","),
                selecTtext:lp.selectYears.split(",")
            },
            "deptlist":{
                text:lp.department+":",
                notEmpty:true,
                type: "org",
                orgType:"unit",
                name:"deptlist",
                count: 0,
                attr : {readonly:true}
            },
            "strategydeploydescribe":{
                type:"textarea",
                attr:{style:"height:100px"},
                text:lp.description+":"
            }
        };

    },
    loadAttachment: function( area ){
        //this.attachment = new MWF.xApplication.Execution.Attachment( area, this.app, this.actions, this.app.lp, {
        //    documentId : this.data.id,
        //    isNew : this.options.isNew,
        //    isEdited : this.options.isEdited,
        //    onQueryUploadAttachment : function(){
        //        this.attachment.isQueryUploadSuccess = true;
        //        if( !this.data.id || this.data.id=="" ){
        //            var data = this.form.getResult(true, ",", true, false, true);
        //            if( !data ){
        //                this.attachment.isQueryUploadSuccess = false;
        //                return;
        //            }
        //            if(this.options.isNew){
        //                data.centerId = this.options.centerWorkId || this.data.centerWorkId || this.data.centerId ;
        //            }
        //            this.app.restActions.saveTask(data, function(json){
        //                if(json.type && json.type == "success"){
        //                    if(json.data.id) {
        //                        this.attachment.options.documentId = json.data.id;
        //                        this.data.id = json.data.id;
        //                        //this.options.isNew = false;
        //                    }
        //                }
        //            }.bind(this), function(xhr,text,error){
        //                this.showErrorMessage(xhr,text,error)
        //            }.bind(this),false)
        //        }
        //    }.bind(this)
        //});
        //
        //this.attachment.load();
    },
    createActionBar:function(){
        this.actionContent = new Element("div.actionContent",{"styles":this.css.actionContent}).inject(this.formTableContainer);
        this.actionBar = new Element("div.actionBar",{"styles":this.css.actionBar}).inject(this.actionContent);
        this.saveAction = new Element("div.saveAction",{
            "styles":this.css.saveAction,
            "text":this.lp.saveAction
        }).inject(this.actionBar).
            addEvents({
                "click":function(){
                    this.save();
                }.bind(this)
            });
        this.cancelAction = new Element("div.cancelAction",{
            "styles":this.css.cancelAction,
            "text":this.lp.cancelAction
        }).inject(this.actionBar).
            addEvents({
                "click":function(){
                    this.close();
                }.bind(this)
            })
    },
    save:function(callback){
        var data = this.keyWorkForm.getResult(true, ",", true, false, true);

        if(data){
            this.app.createShade();
            data.deptlist = data.deptlist.split(",");
            this.actions.saveKeyWork(data,function(json){
                if(json.type == "success"){
                    this.close();
                    this.fireEvent("postSave", json);
                }else if(json.type == "error"){
                    this.app.notice(json.message,"error")
                }
                this.app.destroyShade();
                if(callback)callback();
            }.bind(this));
        }

    }
});


