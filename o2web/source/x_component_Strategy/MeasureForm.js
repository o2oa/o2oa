MWF.xApplication.Strategy = MWF.xApplication.Strategy || {};

//MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Strategy", "Template", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("Strategy","Attachment",null,false);

MWF.xApplication.Strategy.MeasureForm = new Class({
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
        "maxAction":true,
        "closeAction": true
    },
    initialize: function (explorer, actions, data, options) {
        this.setOptions(options);
        this.explorer = explorer;
        this.app = explorer.app;
        this.lp = this.app.lp.measure.popupForm;
        this.actions = this.app.restActions;
        this.path = "/x_component_Strategy/$MeasureForm/";
        this.cssPath = this.path + this.options.style + "/css.wcss";
        this._loadCss();

        this.options.title = this.lp.title;
        this.defaultYear = this.options.year;

        this.data = data || {};
        this.actions = actions;
        //alert("init="+this.data.parentid);

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
        //获取默认下一个序号
        if(this.data.parentid){
            this.actions.getMeasureMaxNumber(this.data.parentid,function(json){
                if(json.type=="success"){
                    if(json.data && json.data.value){
                        this.data.sequencenumber = json.data.value;
                    }
                }
            }.bind(this),null,false);
        }
        this.getData(
            function(){
                this.createTableInfo();
            }.bind(this)
        );
    },
    getData:function(callback){
        if(!this.options.isNew){
            if(this.data.id){
                this.id = this.data.id;
            }else if(this.options.id){
                this.id = this.options.id;
            }

            this.actions.getMeasureById(this.id,function(json){
                if(json.type=="success"){
                    this.data = json.data;
                    this.formTopTextNode.set("text",this.data.measuresinfotitle);
                    if(json.data.measuresinfoyear){
                        this.currentYear = json.data.measuresinfoyear;
                    }
                    if(callback)callback();
                }
            }.bind(this));

        }else{
            if(callback)callback()
        }
    },
    createTableInfo:function(){
        var html = "<table width='100%' border='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
            "<tr>" +
            "   <td styles='formTableTitle' lable='sequencenumber'></td>" +
            "   <td styles='formTableValue' item='sequencenumber'></td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableTitle' lable='measuresinfotitle'></td>" +
            "   <td styles='formTableValue' item='measuresinfotitle'></td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableTitle' lable='measuresyear'></td>" +
            "   <td styles='formTableYearValue' item='measuresyear'></td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableTitle' lable='deptlist'></td>" +
            "   <td styles='formTableValue' item='measuresdutydept'></td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableTitle' lable='measuresdutydept'></td>" +
            "   <td styles='formTableValue' item='deptlist'></td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableTitle' lable='measuressupportdepts'></td>" +
            "   <td styles='formTableValue' item='measuressupportdepts'></td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableTitle' lable='measuresinfoparentid'></td>" +
            "   <td styles='formTableValue'><div styles='keyWorkList' item='measuresinfoparentid' id='keyWorkList'></div></td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableTitle' lable='measuresinfodescribe'></td>" +
            "   <td styles='formTableValue' item='measuresinfodescribe'></td>" +
            "</tr>"+
            "<tr>" +
            "   <td styles='formTableTitle' lable='measuresinfotargetvalue'></td>" +
            "   <td styles='formTableValue' item='measuresinfotargetvalue'></td>" +
            "</tr>"+
            "</table>";
        this.formTableArea.set("html", html);

        if(this.options.isEdited || this.options.isNew){
            this.getKeyWorkList(this.currentYear || this.defaultYear || this.thisYear,
                function(){
                    this.loadForm();
                }.bind(this)
            );
            this.createActionBar();
        }else{
            this.loadForm();
        }

    },
    getKeyWorkList:function(year,callback){
        this.keyWorkListTitle = [];
        this.keyWorkListId = [];

        this.actions.getKeyWorkListNext("(0)",100,{"strategydeployyear":year||this.thisYear},function(json){
            if(json.type=="success"){
                json.data.each(function(d){
                    this.keyWorkListTitle.push(d.strategydeploytitle);
                    this.keyWorkListId.push(d.id);
                }.bind(this));

                if (callback)callback();
            }
        }.bind(this));
    },
    loadForm: function(){
        this.measureForm = new MForm(this.formTableArea, this.data, {
            style: "default",
            isEdited: this.isEdited || this.isNew,
            itemTemplate: this.getItemTemplate(this.lp )
        },this.app,this.css);

        //alert(this.currentYear || this.defaultYear || this.thisYear)
        this.measureForm.load();
        if(!(this.options.isEdited || this.options.isNew) || (this.options.from && this.options.from=="portal")){
            //处理样式及相关联id对应标题问题
            var obj = this.formTableArea.getElementById("keyWorkList");
            if(obj){
                obj.setStyles({"border":"0px","min-height":"0px"});
                obj.set("html","");
                this.keyWorkItem = new Element("div.keyWorkItem",{
                    name:"measuresinfoparentid",
                    styles:{"cursor":"pointer"}
                }).inject(obj);

                this.actions.getKeyWorkById(this.data.measuresinfoparentid,function(json){
                    if(json.type=="success" && json.data && json.data.strategydeploytitle){
                        this.keyWorkItem.set("text",json.data.strategydeploytitle);
                        this.keyWorkItem.addEvents({
                            "click":function(){
                                MWF.xDesktop.requireApp("Strategy", "KeyWorkForm", function(){
                                    var _width = this.options.width || "100%";
                                    var _height = this.options.height || "100%";
                                    //_width = parseInt(_width)-10;
                                    //_height = parseInt(_height)-10;

                                    this.KeyWorkForm = new MWF.xApplication.Strategy.KeyWorkForm(this, this.actions,{"id":this.data.measuresinfoparentid},{
                                        "isEdited":false,
                                        "width":isNaN(_width)?(parseInt(_width)-10)+"%":_width-50,
                                        "height":isNaN(_height)?(parseInt(_height)-10)+"%":_height-50
                                    } );
                                    this.KeyWorkForm.container = this.app.portalContainer || this.app.content;
                                    this.KeyWorkForm.load();

                                }.bind(this));
                            }.bind(this)
                        });
                    }
                }.bind(this));

            }

        }

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
                name:"sequencenumber",
                notEmpty:true
            },
            "measuresinfotitle":{
                text:lp.title+":",
                notEmpty:true
            },
            "measuresyear":{
                text:lp.year+":",
                notEmpty:true,
                type:this.options.isNew?"select":"innerText",
                value:this.currentYear || this.defaultYear || this.thisYear,
                attr : {style:"width:100%;height:30px;border-radius:3px;"},
                selectValue:lp.selectYears.split(","),
                selectText:lp.selectYears.split(","),
                //defaultValue:this.currentYear || this.defaultYear || this.thisYear,
                event:{
                    "change":function(item){
                        var year = item.getValue();
                        _self.currentYear = year;

                        _self.getKeyWorkList(year,
                            function(){
                                _self.loadForm()
                            }
                        );
                    }
                }
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
            "measuresdutydept":{
                text:lp.resDepartment+":",
                notEmpty:true,
                type: "org",
                orgType:"unit",
                name:"measuresdutydept",
                count: 1,
                attr : {readonly:true}
            },
            "measuressupportdepts":{
                text:lp.measuressupportdepts+":",
                notEmpty:true,
                type: "text",
                name:"measuressupportdepts",
                attr : {}
            },
            "measuresinfoparentid":{
                text:lp.keyWork+":",
                isEdited:this.options.from == "portal"?false:true,
                name:"measuresinfoparentid",
                notEmpty:true,
                type:"radio",
                style:{"height":"25px"},
                event:{
                    "click":function(item){
                        var t = typeof item["getValue"];
                        if(t == "function"){
                            var id = item.getValue();
                            if(id){
                                _self.actions.getMeasureMaxNumber(id,function(json){
                                    if(json.type=="success"){
                                        if(json.data && json.data.value){
                                            var o =_self.formTableArea.getElements("[name='sequencenumber']");
                                            o[0].set("value",json.data.value);
                                        }
                                    }
                                }.bind(this));
                            }
                        }

                        //_self.currentYear = year;
                        //
                        //_self.getKeyWorkList(year,
                        //    function(){
                        //        _self.loadForm()
                        //    }
                        //);
                    }
                },
                selectText:this.keyWorkListTitle?this.keyWorkListTitle.join("##").split("##"):"",
                selectValue:this.keyWorkListId?this.keyWorkListId.join("##").split("##"):""
            },
            "measuresinfodescribe":{
                type:"textarea",
                attr:{style:"height:100px"},
                text:lp.description+":"
            },
            "measuresinfotargetvalue":{
                type:"textarea",
                attr:{style:"height:100px"},
                text:lp.measuresinfotargetvalue+":"
            }
        };

    },
    loadAttachment: function( area ){

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
        var _measuresinfoparentid = this.data.measuresinfoparentid;
        var data = this.measureForm.getResult(true, ",", true, false, true);

        //return;
        if(data){
            this.app.createShade();
            if(this.options.from == "portal"){
                data.measuresinfoparentid = this.data.measuresinfoparentid || _measuresinfoparentid;
            }
            data.deptlist = data.deptlist.split(",");
            data.measuresinfoyear = data.measuresyear;
            this.actions.saveMeasure(data,function(json){
                if(json.type == "success"){
                    this.close();
                    this.fireEvent("postSave", json);
                }else if(json.type == "error"){
                    this.app.notice(json.message,"error")
                }
                this.app.destroyShade();
                if(callback)callback()
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

    },



    aa:function(){
        var val = "";if(d.configValue && d.configValue!='') {var v = d.configValue.split(',');for (i = 0; i < v.length; i++) {if (val == '') {val = v[i].split('@')[0];} else {val = val + ',' + v[i].split('@')[0];};}} return val


    }

});


