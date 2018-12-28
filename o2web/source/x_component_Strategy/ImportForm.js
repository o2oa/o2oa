MWF.xApplication.Strategy = MWF.xApplication.Strategy || {};

MWF.xDesktop.requireApp("Strategy", "Template", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("Template", "MDomItem", null, false);
MWF.xDesktop.requireApp("Strategy","Attachment",null,false);

MWF.xApplication.Strategy.ImportForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "500",
        "height": "200",
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

        this.app = explorer.app;
        this.lp = this.app.lp.importForm;
        this.actions = this.app.restActions;
        this.path = "/x_component_Strategy/$ImportForm/";
        this.cssPath = this.path + this.options.style + "/css.wcss";
        if(this.options.from){
            this.cssPath = this.path + this.options.style + "/css_portal.wcss";
        }
        this._loadCss();

        this.options.title = this.lp.title;

        this.data = data || {};
        if(para) this.para = para;
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

            this.formTopIconNode = new Element("div.formTopIconNode", {
                "styles": this.css.formTopIconNode
            }).inject(this.formTopNode);

            this.formTopTextNode = new Element("div", {
                "styles": this.css.formTopTextNode,
                "text": this.data.title ?  this.data.title  : this.lp.title
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
        //this.getData(
        //    function(){
        //        this.createTableInfo()
        //    }.bind(this)
        //);
        this.createTableInfo();
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
            if(callback)callback();
        }
    },
    createTableInfo:function(){
        this.templateDiv = new Element("div.templateDiv",{
            "styles":this.css.templateDiv
        }).inject(this.formTableArea);
        this.templateText = new Element("span.templateText",{
            "styles":this.css.templateText,
            "text":this.lp.template
        }).inject(this.templateDiv);
        this.templateText.addEvents({
            "click":function(){

            }.bind(this)
        });

        this.inputDiv = new Element("div.inputDiv",{
            "styles":this.css.inputDiv
        }).inject(this.formTableArea);

        this.input = new Element("input.input",{
            "styles":this.css.input,
            "type":"file",
            "name":"file"
        }).inject(this.inputDiv);

        this.sheetDiv = new Element("div.sheetDiv",{
            "styles":this.css.sheetDiv
        }).inject(this.formTableArea);

        this.sheetSel = new MDomItem(this.sheetDiv, {
            "text":"sheet页",
            "name":"sheet",
            "type":"MSelector",
            selectValue : "1,2,3,4,5,6,7,8,9,10",
            selectText : "1,2,3,4,5,6,7,8,9,10",
            "mSelectorOptions":{
                "width":"150px",
                "defaultOptionLp" : "请选择Sheet",
                "tooltipsOptions" : {
                    axis: "y",      //箭头在x轴还是y轴上展现
                    position : { //node 固定的位置
                        x : "auto", //x轴上left center right,  auto 系统自动计算
                        y : "auto" //y 轴上top middle bottom, auto 系统自动计算
                    },
                    event : "click", //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
                    hiddenDelay : 200, //ms  , 有target 且 事件类型为 mouseenter 时有效
                    displayDelay : 0   //ms , 有target 且事件类型为 mouseenter 时有效
                }
            }
        } , null, this.app, this.css);
        this.sheetSel.load();

        this.importAction = new Element("div.importAction",{
            "styles":this.css.importAction,
            "text":this.lp.importAction
        }).inject(this.formTableArea);
        this.importAction.addEvents({
            "click":function(){
                var fileNode = this.inputDiv.getFirst();
                var files = fileNode.files;
                if(files.length) {
                    for (var i = 0; i < files.length; i++) {
                        var file = files.item(i);
                        var formData = new FormData();
                        formData.append('file', file);
                        formData.append("year",this.data.year||"");
                        formData.append("parentid",this.data.parentid||"");
                        formData.append("sheetsequence",this.sheetSel.get("value")||"");


                        this.actions.importMeasure(function(json) {
                            if (json.data.isPersist){
                                this.app.notice(json.data.describe, "success");
                                this.fireEvent("importSave", json);
                                this.close();
                            }else{
                                this.app.notice(json.data.describe, "error");
                                var address = this.actions.action.address;
                                var url = address+"/jaxrs/measuresimport/result/flag/"+json.data.flag;
                                window.open(url);
                            }

                        }.bind(this),function(xhr,text,error){
                            this.app.notice("导入失败","error");
                            this.close();
                        }.bind(this),formData,file);


                    }
                }else{
                    this.app.notice(this.lp.notice.fileEmpty,"error");
                }

                if(this.sheetSel.get("value")==""){
                    this.app.notice(this.lp.notice.sheetEmpty,"error");
                    //return;
                }
            }.bind(this)
        });

        this.closeAction = new Element("div.closeAction",{
            "styles":this.css.closeAction,
            "text":this.lp.importClose
        }).inject(this.formTableArea);
        this.closeAction.addEvents({
            "click":function(){
                this.close();
            }.bind(this)
        });











        /*if(this.uploadFileAreaNode) this.uploadFileAreaNode.destroy();
        this.uploadFileAreaNode = new Element("div");
        var html = "<input name=\"file\" type=\"file\" />";
        this.uploadFileAreaNode.set("html", html);

        this.fileUploadNode = this.uploadFileAreaNode.getFirst();
        this.fileUploadNode.addEvent("change", function(){

            var files = fileNode.files;
            if (files.length){
                for (var i = 0; i < files.length; i++) {
                    var file = files.item(i);
                    var tmp = file.name.split(".");
                    this.uploadFileName = file.name;
                    if( tmp[tmp.length-1].toLowerCase() != "xls" && tmp[tmp.length-1].toLowerCase() != "xlsx" ){
                        this.app.notice("请导入excel文件！","error");
                        return;
                    }
                    var formData = new FormData();
                    formData.append('file', file);

                    this.app.createShade(null,"正在导入，请稍后.....");

                    this.actions.importBaseWork(centerId,function(json){
                        this.reloadTableContent(centerId);
                        this.app.destroyShade()
                    }.bind(this),function(xhr,text,error){
                        this.showErrorMessage(xhr,text,error);
                        this.app.destroyShade()
                    }.bind(this),formData,file);


                    //this.actions.importBaseWork(centerId,function(json){
                    //    this.reloadTableContent(centerId);
                    //    this.app.destroyShade()
                    //}.bind(this),function(xhr,text,error){
                    //    this.showErrorMessage(xhr,text,error);
                    //    this.app.destroyShade()
                    //}.bind(this),formData,file)

                }
            }
        }.bind(this));
        var fileNode = this.uploadFileAreaNode.getFirst();
        //alert(13)
        debugger;
        //alert(this.uploadFileAreaNode.get("html"))
        fileNode.click();
        //this.uploadFileAreaNode.destroy();*/







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
        var val = "";if(d.configValue && d.configValue!='') {var v = d.configValue.split(',');for (var i = 0; i < v.length; i++) {if ( val == '') {val = v[i].split('@')[0];} else {val = val + ',' + v[i].split('@')[0];}}} return val


    }

});


