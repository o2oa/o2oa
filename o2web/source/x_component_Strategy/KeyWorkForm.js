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
        this.defaultYear = this.options.year;

        this.data = data || {};
        this.actions = actions;
        this.orgActions = MWF.Actions.get("x_organization_assemble_control");
    },
    load: function () {
        var now = new Date();
        this.thisYear = now.getFullYear();

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
                "text": this.data.title ?  this.data.title  : this.lp.addTitle
            }).inject(this.formTopNode);

            if (this.options.closeAction) {
                this.formTopCloseActionNode = new Element("div", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
                this.formTopCloseActionNode.addEvent("click", function () {
                    this.close();
                }.bind(this))
            }

            this._createTopContent();
        }
    },
    _createTopContent: function () {

    },
    _createTableContent: function () {
        this.getData(function(){
            this.createTableInfo();
        }.bind(this));
    },
    getData:function(callback){
        if(!this.options.isNew){
            if(this.data.id){
                this.id = this.data.id;
            }else if(this.options.id){
                this.id = this.options.id;
            }

            this.actions.getKeyWorkById(this.id,function(json){
                this.data = json.data;
                this.formTopTextNode.set("text",this.data.strategydeploytitle);
                if(callback)callback();
            }.bind(this));

        }else{
            this.orgActions.listTopUnit(function(json){
                if(json.type == "success"){
                    if(json.data && json.data.length>0){
                        this.data.deptlist = json.data[0].distinguishedName;
                        if(callback)callback();
                    }
                }
            }.bind(this))

        }
    },
    createTableInfo:function(){
        //var html = "<table width='100%' border='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
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
            "   <td styles='formTableValue' item='strategydeployyear'></td>" +
            "</tr>"+
            "<tr style='display:none'>" +
            "   <td styles='formTableTitle' lable='deptlist'></td>" +
            "   <td styles='formTableValue' item='deptlist'></td>" +
            "</tr>"+
            //"<tr>" +
            //"   <td styles='formTableTitle' lable='strategydeploydescribe'></td>" +
            //"   <td styles='formTableValue' item='strategydeploydescribe'></td>" +
            //"</tr>"+
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
                type:this.options.isNew?"select":"innerText",
                value:this.defaultYear||this.thisYear,
                attr : {style:"width:100%;height:30px;border-radius:3px;"},
                selectValue:lp.selectYears.split(","),
                selecTtext:lp.selectYears.split(",")
            },
            "deptlist":{
                text:lp.department+":",
                isEdited:false,
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
            });
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
                    this.app.notice(json.message,"error");
                }
                this.app.destroyShade();
                if(callback)callback();
            }.bind(this),function(xhr,text,error){
                this.app.showErrorMessage(xhr,text,error);
                this.app.destroyShade();
            }.bind(this));
        }
    },

    createShade: function(o,txtInfo){
        var defaultObj = this.content;
        var obj = o || defaultObj;
        var txt = txtInfo || "loading...";
        if(this.shadeDiv){ this.shadeDiv.destroy()}
        if(this["shadeTxtDiv"])  this["shadeTxtDiv"].destroy();
        this.shadeDiv = new Element("div.shadeDiv").inject(obj);
        this.inforDiv = new Element("div.inforDiv",{
            styles:{"height":"16px","display":"inline-block","position":"absolute","background-color":"#000000","border-radius":"3px","padding":"5px 10px"}
        }).inject(this.shadeDiv);
        this.loadImg = new Element("img.loadImg",{
            styles:{"width":"16px","height":"16px","float":"left"},
            src:this.path+"default/icon/loading.gif"
        }).inject(this.inforDiv);

        this.shadeTxtSpan = new Element("span.shadeTxtSpan").inject(this.inforDiv);
        this.shadeTxtSpan.set("text",txt);
        this.shadeDiv.setStyles({
            "width":"100%","height":"100%","position":"absolute","opacity":"0.6","background-color":"#cccccc","z-index":"999"
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
        if(obj.getStyle("position")=="absolute"){
            this.shadeDiv.setStyles({
                "left":"0px",
                "top":"0px"
            })
        }
        this.inforDiv.setStyles({
            "left":(x/2)+"px",
            "top":(y/2)+"px"
        })
    },
    destroyShade : function(){
        if(this.shadeDiv) this.shadeDiv.destroy();
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


