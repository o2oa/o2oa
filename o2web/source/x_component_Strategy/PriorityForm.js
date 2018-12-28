MWF.xApplication.Strategy = MWF.xApplication.Strategy || {};

//MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Strategy", "Template", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("Strategy","PriorityAttachment",null,false);

MWF.xApplication.Strategy.PriorityForm = new Class({
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
        "draggable": true,
        "maxAction":true,
        "closeAction": true
    },
    initialize: function (explorer, data, options, para) {
        this.setOptions(options);
        this.explorer = explorer;
        if( para ){
            if( this.options.relativeToApp ){
                this.app = para.app || this.explorer.app;
                this.container = para.container || this.app.content;
                this.lp = para.lp || this.explorer.lp || this.app.lp;
                this.css = para.css || this.explorer.css || this.app.css;
                this.actions = para.actions || this.explorer.actions || this.app.actions || this.app.restActions;
            }else{
                this.container = para.container;
                this.lp = para.lp || this.explorer.lp;
                this.css = para.css || this.explorer.css;
                this.actions = para.actions || this.explorer.actions;
            }
        }else{
            if( this.options.relativeToApp ){
                this.app = this.explorer.app;
                this.container = this.app.content;
                this.lp = this.explorer.lp || this.app.lp;
                this.css = this.explorer.css || this.app.css;
                this.actions = this.explorer.actions || this.app.actions || this.app.restActions;
            }else{
                this.container = window.document.body;
                this.lp = this.explorer.lp;
                this.css = this.explorer.css;
                this.actions = this.explorer.actions;
            }
        }
        this.data = data || {};
    },

    load: function () {
        this.lp = this.app.lp.priority.popupForm;

        this.path = "/x_component_Strategy/$PriorityForm/";
        this.cssPath = this.path + this.options.style + "/css.wcss";
        this._loadCss();

        this.options.title = this.lp.title;
        this.defaultYear = this.options.year;
        this.currentYear = this.defaultYear;

        this.currentDepartment = this.options.department;

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

        this.fireEvent("queryCreateTop");
        if (!this.formTopNode) {
            this.formTopNode = new Element("div.formTopNode", {
                "styles": this.css.formTopNode
            }).inject(this.formNode);

            if(this.options.hasTopIcon){
                this.formTopIconNode = new Element("div", {
                    "styles": this.css.formTopIconNode
                }).inject(this.formTopNode);
            }

            this.formTopTextNode = new Element("div", {
                "styles": this.css.formTopTextNode,
                "text": this.options.title
            }).inject(this.formTopNode);

            if (this.options.closeAction) {
                this.formTopCloseActionNode = new Element("div", {
                    "styles": this.css.formTopCloseActionNode,
                    "title" : "关闭"
                }).inject(this.formTopNode);
                this.formTopCloseActionNode.addEvent("click", function ( ev ) {
                    this.close();
                    ev.stopPropagation();
                }.bind(this));
            }

            if( this.options.maxAction ){
                this.formTopMaxActionNode = new Element("div", {
                    "styles": this.css.formTopMaxActionNode,
                    "title" : "最大化"
                }).inject(this.formTopNode);
                this.formTopMaxActionNode.addEvent("click", function () {
                    this.maxSize();
                }.bind(this));

                this.formTopRestoreActionNode = new Element("div", {
                    "styles": this.css.formTopRestoreActionNode,
                    "title" : "还原"
                }).inject(this.formTopNode);
                this.formTopRestoreActionNode.addEvent("click", function () {
                    this.restoreSize();
                }.bind(this));

                this.formTopNode.addEvent("dblclick", function(){
                    this.switchMax();
                }.bind(this));
            }

            if(this.options.hasTopContent){
                this.formTopContentNode = new Element("div.formTopContentNode", {
                    "styles": this.css.formTopContentNode
                }).inject(this.formTopNode);

                this._createTopContent();
            }

        }

        this.fireEvent("postCreateTop");

        //if (!this.formTopNode) {
        //    this.formTopNode = new Element("div.formTopNode", {
        //        "styles": this.css.formTopNode,
        //        "text": this.options.title
        //    }).inject(this.formNode);
        //
        //    this._createTopContent();
        //
        //    if (this.options.closeAction) {
        //        this.formTopCloseActionNode = new Element("div.formTopCloseActionNode", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
        //        this.formTopCloseActionNode.addEvent("click", function () {
        //            this.close()
        //        }.bind(this))
        //    }
        //}
    },

    //createTopNode: function () {
    //    if (!this.formTopNode) {
    //        this.formTopNode = new Element("div.formTopNode", {
    //            "styles": this.css.formTopNode
    //        }).inject(this.formNode);
    //
    //        this.formTopTextNode = new Element("div", {
    //            "styles": this.css.formTopTextNode,
    //            "text": this.data.title ?  this.data.title  : this.lp.addTitle
    //        }).inject(this.formTopNode);
    //
    //        if (this.options.closeAction) {
    //            this.formTopCloseActionNode = new Element("div", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
    //            this.formTopCloseActionNode.addEvent("click", function () {
    //                this.close()
    //            }.bind(this))
    //        }
    //
    //        this._createTopContent();
    //    }
    //},
    _createTopContent: function () {

    },
    _createTableContent: function () {
        this.getData(function(){this.createTableInfo()}.bind(this));
    },
    getData:function(callback){
        if(!this.options.isNew){
            if(this.data.id){
                this.id = this.data.id;
            }else if(this.options.id){
                this.id = this.options.id;
            }

            this.actions.getPriorityById(this.id,function(json){
                this.data = json.data;
                this.formTopTextNode.set("text",this.data.keyworktitle);
                this.currentDepartment = json.data.keyworkunit;
                if(callback)callback()
            }.bind(this));

        }else{
            if(callback)callback();
        }
    },
    createTableInfo:function(){
        var html = "<table width='100%' border='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
            "<tr>" +
            "   <td styles='formTableTitle' lable='sequencenumber'></td>" +
            "   <td styles='formTableValue' item='sequencenumber'></td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableTitle' lable='keyworktitle'></td>" +
            "   <td styles='formTableValue' item='keyworktitle'></td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableTitle' lable='keyworkyear'></td>" +
            "   <td styles='formTableYearValue' item='keyworkyear'></td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableTitle' lable='deptlist'></td>" +
            "   <td styles='formTableValue' item='keyworkunit'></td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableTitle' lable='validDate'></td>" +
            "   <td styles='formTableValue'>"+
            "       <div styles='formTableDate' item='keyworkbegindate'></div>"+
            "       <div styles='formTableDate' lable='validDateMonth'></div><div styles='formTableDate' lable='validDateConnect'></div>"+
            "       <div styles='formTableDate' item='keyworkenddate'></div><div styles='formTableDate' lable='validDateMonth'></div>"+
            "   </td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableTitle' lable='measureslist'></td>" +
            "   <td styles='formTableValue'><div styles='measureList' item='measureslist' id='measureList'></div></td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableTitle' lable='keyworkdescribe'></td>" +
            "   <td styles='formTableValue' item='keyworkdescribe'></td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableTitle' lable='attachments'></td>"+
            "   <td styles='formTableValue'>"+
                    "<div styles='formTableValueDiv' item='attachments'></div>"+
            "   </td>" +
            "</tr>"+
            "</table>";
        this.formTableArea.set("html", html);

        if(this.options.isNew || this.options.isEdited){

            this.getMeasureList(this.currentYear || this.thisYear,
                function(){
                    this.loadForm();
                }.bind(this)
            );
            this.createActionBar();
        }else{
            this.loadForm();
        }
    },
    loadForm: function(){
        this.priorityForm = new MForm(this.formTableArea, this.data, {
            style: "default",
            isEdited: this.isEdited || this.isNew,
            itemTemplate: this.getItemTemplate(this.lp )
        },this.app,this.css);

        this.priorityForm.load();
        if(!(this.options.isEdited || this.options.isNew)){
            this.formTableArea.getElementById("measureList").setStyles({"border":"0px","min-height":"0px"});
        }
        var taObj = this.formTableArea.getElements("textarea");
        taObj.setStyles({height:"100px"});

        if(!(this.options.isEdited || this.options.isNew)){
            //处理样式及相关联id对应标题问题
            var obj = this.formTableArea.getElementById("measureList");
            if(obj){
                obj.setStyles({"border":"0px","min-height":"0px"});
                obj.set("html","");
                if(this.data.measureslist){
                    this.data.measureslist.each(function(d){
                        this.actions.getMeasureById(d,function(json){
                            if(json.type == "success"){
                                new Element("div.measureItem",{
                                    "styles":this.css.measureItem,
                                    "text":json.data.measuresinfotitle
                                }).inject(obj).
                                    addEvents({
                                        "click":function(){
                                            var _width = this.options.width || "100%";
                                            var _height = this.options.height || "100%";

                                            //_width = parseInt(_width)-10;
                                            //_height = parseInt(_height)-10;

                                            MWF.xDesktop.requireApp("Strategy", "MeasureForm", function(){
                                                this.measureForm = new MWF.xApplication.Strategy.MeasureForm(this, this.actions,{"id":d},{
                                                    "isEdited":false,
                                                    "width":isNaN(_width)?(parseInt(_width)-10)+"%":_width-50,
                                                    "height":isNaN(_height)?(parseInt(_height)-10)+"%":_height-50

                                                } );
                                                this.measureForm.container = this.app.portalContainer || this.app.content;
                                                this.measureForm.load();

                                            }.bind(this));
                                        }.bind(this)
                                    });
                            }
                        }.bind(this));
                    }.bind(this));
                }

            }

        }

        this.attachmentArea = this.formTableArea.getElement("[item='attachments']");
        this.loadAttachment( this.attachmentArea );

    },
    loadAttachment: function( area ){
        this.attachment = new MWF.xApplication.Strategy.PriorityAttachment( area, this.app, this.actions, this.app.lp.attachment.priority, {
            workId : this.data.id,
            isNew : this.options.isNew,
            isEdited : this.options.isEdited,
            onQueryUploadAttachment : function(){
                this.attachment.isQueryUploadSuccess = true;
                if( !this.data.id || this.data.id=="" ){

                    var data = this.priorityForm.getResult(true, ",", true, false, true);
                    if(data && this.currentDepartment && this.currentDepartment!=""){
                        data.keyworkyear = this.currentYear || this.thisYear;
                        data.keyworkunit = this.currentDepartment;
                        data.measureslist = data.measureslist.split(",");

                        this.actions.savePriority(data,function(json){
                            if(json.type == "success"){
                                if(json.data.id) {
                                    this.data = json.data;
                                    this.id = json.data.id;
                                    this.attachment.options.workId = json.data.id;
                                    //this.options.isNew = false;
                                }
                            }

                        }.bind(this),function(xhr,error,text){
                            this.app.showErrorMessage(xhr,error,text);
                            this.attachment.isQueryUploadSuccess = false;
                            //return;
                        }.bind(this),false);
                    }else{
                        this.attachment.isQueryUploadSuccess = false;
                        //return;
                    }

                }
            }.bind(this)
        });

        this.attachment.load();
    },
    getMeasureList:function(year,callback){
        this.measureListTitle = [];
        this.measureListId = [];
        var data = {
            "measuresinfoyear" : year||this.thisYear,
            "deptlist":[this.currentDepartment]
        };

        this.actions.getMeasureListNext("(0)",100,data,function(json){
            if(json.type=="success"){
                json.data.each(function(d){
                    this.measureListTitle.push(d.measuresinfotitle);
                    this.measureListId.push(d.id);
                }.bind(this));
                if (callback)callback();
            }
        }.bind(this));
    },
    getItemTemplate: function( lp ){
        _self = this;
        return {
            "sequencenumber":{
                text:lp.sequencenumber+":",
                style:{"text-indent":"3px"},
                notEmpty:true
            },
            "keyworktitle":{
                text:lp.title+":",
                style:{"text-indent":"3px"},
                notEmpty:true
            },
            "keyworkyear":{
                text:lp.year+":",
                notEmpty:true,
                type:this.options.isNew?"select":"innerText",
                value:this.currentYear||this.thisYear,
                attr : {style:"width:100%;height:30px;border-radius:3px;"},
                selectValue:lp.selectYears.split(","),
                selecTtext:lp.selectYears.split(","),
                event:{
                    "change":function(item){
                        var year = item.getValue();
                        _self.currentYear = year;

                        _self.getMeasureList(year,
                            function(){
                                _self.loadForm()
                            }
                        );
                    }
                }
            },
            "keyworkunit":{
                isEdited:false,
                text:lp.department+":",
                notEmpty:true,
                type: "org",
                value:this.options.department,
                orgType:"unit",
                name:"deptlist",
                count: 0,
                attr : {readonly:true,unformatWidth:true}
            },
            "validDate":{
                text:lp.validDate+":"
            },
            "keyworkbegindate":{
                text:lp.keyworkbegindate,
                type: "select",
                name:"keyworkbegindate",
                notEmpty:true,
                //selectValue: !this.data.keyworkbegindate?lp.selectMonth.split(","):this.data.keyworkbegindate.toString(), //lp.weekDayValue.split(","),
                //selectText:  !this.data.keyworkbegindate?lp.selectMonth.split(","):this.data.keyworkbegindate.toString(),
                selectText:  lp.selectMonth.split(","),
                style:{"width":"50px","height":"20px","text-indent":"3px"},
                attr : {readonly:true}
            },
            "validDateConnect":{
                text:lp.validDateConnect
            },
            "validDateMonth":{
                text:lp.validDateMonth,
                style:{"margin-left":"10px","margin-right":"10px","text-indent":"3px"}
            },
            "keyworkenddate":{
                text:lp.keyworkenddate,
                type: "select",
                name:"keyworkenddate",
                notEmpty:true,
                //selectValue: !this.data.keyworkenddate?lp.selectMonth.split(","):this.data.keyworkenddate.toString(), //lp.weekDayValue.split(","),
                //selectText:  !this.data.keyworkenddate?lp.selectMonth.split(","):this.data.keyworkenddate.toString(),
                selectText:  lp.selectMonth.split(","),
                style:{"width":"50px","height":"20px","text-indent":"3px"},
                attr : {readonly:true}
            },
            "measureslist":{
                text:lp.measurelist+":",
                type:"checkbox",
                notEmpty:true,
                selectText:this.measureListTitle?this.measureListTitle.join(",").split(","):"",
                selectValue:this.measureListId?this.measureListId.join(",").split(","):""
            },
            "keyworkdescribe":{
                type:"textarea",
                style:{"text-indent":"3px","height":"100px"},
                text:lp.description+":"
            },
            "attachments":{
                text:lp.attachments,
                type:"innertext"
            }
        };

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
        var data = this.priorityForm.getResult(true, ",", true, false, true);

        if(data && this.currentDepartment && this.currentDepartment!=""){
            this.createShade();

            data.keyworkyear = this.currentYear || this.thisYear;
            data.keyworkunit = this.currentDepartment;
            data.measureslist = data.measureslist.split(",");
            data.id = this.id || this.data.id;

            this.actions.savePriority(data,function(json){
                if(json.type == "success"){
                    this.close();
                    this.fireEvent("postSave", json);
                }else if(json.type == "error"){
                    this.app.notice(json.message,"error")
                }
                this.destroyShade();
                if(callback)callback()
            }.bind(this),function(xhr,text,error){
                this.showErrorMessage(xhr,text,error);
                this.destroyShade();
            }.bind(this));
        }

    },
    createShade: function(o,txtInfo){
        var defaultObj = this.container||this.app;
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


