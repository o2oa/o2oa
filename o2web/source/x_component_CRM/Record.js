MWF.require("MWF.widget.O2Identity", null, false);
MWF.xApplication.CRM.Chance={};
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.xApplication.CRM.Record = new Class({
    Extends: MWF.xApplication.CRM.Template.PopupForm,
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
        "closeAction": true
    },

    initialize: function (explorer, actions, data, options) {
        this.setOptions(options);
        this.explorer = explorer;
        this.app = explorer.app;
        this.lp = this.app.lp.contact.contactEdit;
        this.path = "/x_component_CRM/$ClueEdit/";
        this.cssPath = this.path + this.options.style + "/css.wcss";
        this._loadCss();

        this.options.title = this.lp.title;

        this.data = data || {};
        this.actions = actions;
        this.configData = [];
    },
    load: function () {
        that = this;
        this.loadResource(function(){
            this.createForm();
            this.loadEvent();
        }.bind(this))
    },
    loadResource: function ( callback ) {
        if(callback)callback();
    },
    createForm:function(){
        var clueName = this.options.openName;
        var buttonHtml = '';
        var moreHtml = '';
        jQuery(".openDiv").notifyMe(
            'right',
            'default',
            clueName,
            buttonHtml,
            moreHtml,
            '',
            'notify',
            500,
            {"right":"0px","top":"0px"}
        );
        this.createContentHtml();

    },
    createContentHtml: function () {
        that = this;
        var tabConent ="<div class='panes'><div class='pane' id='tab-follow' style='display:block;'><p>First tab content</p></div><div></div>";

        jQuery(".notify-content").html(tabConent);
        var indexContentHtml = '<div class="log-cont"><div class="log-inner1"><div class="log-inner2"><div class="log-items">'+
            '<div class="load"><button  type="button" class="el-button el-button--text"></button></div></div>'+
            '<div class="empty-mask" style="display: none;"><div class="empty-content"><img src="/x_component_CRM/$Template/empty.png" class="empty-icon"> <p class="empty-text">没有找到数据</p></div></div></div>'+
            '<div class="el-loading-mask" style="display: none;"><div class="el-loading-spinner"><svg viewBox="25 25 50 50" class="circular"><circle cx="50" cy="50" r="20" fill="none" class="path"></circle></svg></div></div>'+
            '</div></div>';
        jQuery("#tab-follow").html(indexContentHtml);
        this.actions.listByTypesAndTimeRange(this.options.openId,this.options.filter, function (json) {
            if(json.type=="success"){
                var recordDatas = json.data;
                jQuery(".fl-c").remove();
                var logItemHtml = "";
                for ( i in recordDatas){
                    if(i<recordDatas.length){
                        var recordData = recordDatas[i];
                        var personImg = '/x_component_CRM/$Template/portrait.png';
                        if(recordData.ICONBase64 && recordData.ICONBase64!=""){
                            personImg = recordData.ICONBase64;
                        }
                        var attHtml = '';
                        var relationHtml = '';
                        if(recordData.attachmentListPreview.length>0){
                            attHtml = attHtml+'<div class="vux-flexbox fl-b-images vux-flex-row" style="flex-wrap: wrap;"></div>'
                        }
                        if(recordData.attachmentList.length>0){
                            attHtml = attHtml+'<div class="fl-b-files">';
                            var attList = recordData.attachmentList;
                            for(j in attList){
                                if(j<attList.length){
                                    var attData = attList[j];
                                    attHtml = attHtml+'<div class="vux-flexbox cell vux-flex-row">'+
                                        '<img  src="/x_component_CRM/$Record/default/icons/att.png" class="cell-head"> <div class="cell-body">'+attData.name+'<span  style="color: rgb(204, 204, 204);">（'+that.toDecimal(attData.length)+'KB）</span></div>'+
                                        '<button  type="button" class="el-button el-button--primary aname" aid="'+attData.id+'" wcrm="'+attData.wcrm+'"><img  src="/x_component_CRM/$Record/default/icons/down.png" style="margin-bottom:-3px;"><span>下载</span></button></div>'
                                }
                            }
                            attHtml = attHtml+'</div>';
                        }
                        var types = recordData.types;
                        var atype = "";
                        var aid = "";
                        if(types=="customer"){
                            if(recordData.customer){
                                atype = recordData.customer.customername;
                                aid = recordData.customer.id;
                            }
                        }
                        if(types=="leads"){
                            if(recordData.leads){
                                atype = recordData.leads.name;
                                aid = recordData.leads.id;
                            }
                        }
                        if(types=="contacts"){
                            if(recordData.contacts){
                                atype = recordData.contacts.contactsname;
                                aid = recordData.contacts.id;
                            }
                        }
                        if(types=="opportunity"){
                            if(recordData.opportunity){
                                atype = recordData.opportunity.name;
                                aid = recordData.opportunity.id;
                            }
                        }
                        if(aid!=""){
                            relationHtml = '<div class="vux-flexbox relate-cell vux-flex-row" aid="'+aid+'" atype="'+types+'"><img  src="/x_component_CRM/$Record/default/icons/'+types+'.png" class="cell-head">'+
                                '<div  class="relate-cell-body" style="color: rgb(99, 148, 229); cursor: pointer;">'+atype+'</div></div>';
                        }

                        logItemHtml = logItemHtml+'<div class="fl-c"><div class="vux-flexbox fl-h vux-flex-row">'+
                            '<img class="div-photo fl-h-img"  src="data:image/png;base64,'+personImg+'" lazy="loaded"></img> '+
                            '<div class="fl-h-b"><div class="fl-h-name">'+recordData.person.name+'</div><div class="fl-h-time">'+recordData.updateTime+'</div></div></div>'+
                            '<div class="fl-b"><div class="fl-b-content">'+recordData.content+'</div>'+attHtml+
                            '<div class="follow"><span class="follow-info">'+recordData.category+'</span></div></div>'+relationHtml+
                            '<div  class="full-container" style="display: none;"></div></div>'
                    }
                }
                if(logItemHtml!=""){
                    jQuery(".load").before(logItemHtml);
                }
                if(recordDatas.length<1){
                    jQuery(".empty-mask").show();
                }
                jQuery(".aname").click(function(){
                    var attUrl = 'http://172.16.92.55:20020/x_wcrm_assemble_control/jaxrs/attachment/download/'+jQuery(this).attr("aid")+'/work/'+jQuery(this).attr("wcrm")
                    window.open(attUrl);
                    /*that.actions.downloadAttachment(jQuery(this).attr("aid"),jQuery(this).attr("wcrm"), function (wjson) {

                    }.bind(that));*/
                });
                jQuery(".relate-cell").click(function(){
                    var openid = jQuery(this).attr("aid");
                    var openName = jQuery(this).find(".relate-cell-body").text();

                    if(jQuery(this).attr("atype")=="customer"){
                        MWF.xDesktop.requireApp("CRM", "CustomerOpen", function(){
                            that.customer = new MWF.xApplication.CRM.CustomerOpen(that, that.actions,{},{
                                "openId":openid,
                                "openName":openName,
                                "openStyle":{"right":"0px","top":"0px"},
                                "onReloadView" : function(){
                                }.bind(that)
                            });
                            that.customer.load();
                        }.bind(that))
                    }
                    if(jQuery(this).attr("atype")=="leads"){
                        MWF.xDesktop.requireApp("CRM", "ClueOpen", function(){
                            that.customer = new MWF.xApplication.CRM.ClueOpen(that, that.actions,{},{
                                "clueId":openid,
                                "clueName":openName,
                                "openStyle":{"right":"0px","top":"0px"},
                                "onReloadView" : function(){
                                }.bind(that)
                            });
                            that.customer.load();
                        }.bind(that))
                    }
                    if(jQuery(this).attr("atype")=="contacts"){
                        MWF.xDesktop.requireApp("CRM", "ContactsOpen", function(){
                            that.customer = new MWF.xApplication.CRM.ContactsOpen(that, that.actions,{},{
                                "openId":openid,
                                "openName":openName,
                                "openStyle":{"right":"0px","top":"0px"},
                                "onReloadView" : function(){
                                }.bind(that)
                            });
                            that.customer.load();
                        }.bind(that))
                    }

                });
            }

        }.bind(this));
        jQuery(".panes").css("height",jQuery("body").children(":first").height()-jQuery(".headNode").height());
    },
    loadEvent: function(){
        that = this;
        jQuery('.tabPanel div').click(function(){
            jQuery(this).addClass('hit').siblings().removeClass('hit');
            jQuery('.panes>div:eq('+jQuery(this).index()+')').show().siblings().hide();
            that.createTypeHtml();
        });
        jQuery('.headMoreBottonDiv').click(function(){
            jQuery(".el-dropdown-menu").toggle(100);
        });
        jQuery('.el-dropdown-menu__item').click(function(){

            if(jQuery(this).text()=="删除"){

            }
            //---for记录类型
            if(jQuery(this).parent().attr("tid")=="recordType"){
                jQuery(".se-select-name").text(jQuery(this).text());
                jQuery(this).parent().toggle(100);
            }

        });
        jQuery('.headMoveBottonDiv').click(function(){
            that.transfer();
        });
        jQuery('.headEditBottonDiv').click(function(){
            that.contactsEdit();
        });
        jQuery('#bar-file').change(function(event) {
            var files = event.target.files;
            debugger
            jQuery('.fileList').empty();
            if (files && files.length > 0) {
                // 获取目前上传的文件
                var fileListHtml = '<div class="fileList">';
                for(var i=0;i<files.length;i++){
                    var file = files[i];
                    var fsize = file.size/1024;
                    var lastModifiedDate = file.lastModifiedDate;
                    fileListHtml = fileListHtml+'<div class="fileItem"><div class="fname">'+file.name+'</div><div class="fsize">'+that.toDecimal(fsize)+'kb</div><div class="ftime">'+that.getFormateTime(lastModifiedDate)+'</div></div>';

                }
                    fileListHtml = fileListHtml+'</div>';
                    jQuery('.mix-container').append(fileListHtml);
                /*
                file = files[0];
                // 来在控制台看看到底这个对象是什么
                console.log(file);
                var filter = {};
                filter = {
                    file:file,
                    fileName:file.name
                };
                debugger
                var formdata=new FormData();
                formdata.append("fileName",file.name);
                formdata.append("file",file);
                that.actions.updateAttachment(that.options.clueId, "leads", formdata,file, function (json) {
                    debugger
                    if(json.type=="success"){
                        Showbo.Msg.alert('附件上传成功!');
                    }
                }.bind(that));
                /!*!// 那么我们可以做一下诸如文件大小校验的动作
                 if(file.size > 1024 * 1024 * 2) {
                 alert('图片大小不能超过 2MB!');
                 return false;
                 }*!/

                // 下面是关键的关键，通过这个 file 对象生成一个可用的图像 URL
                // 获取 window 的 URL 工具
                var URL = window.URL || window.webkitURL;
                // 通过 file 生成目标 url
                var imgURL = URL.createObjectURL(file);
                // 用这个 URL 产生一个 <img> 将其显示出来
                //jQuery('.fbpj .container').prev().find("img").attr('src', imgURL);
                jQuery('.mix-container').append('<div><img src="'+imgURL+'"></div>');
                // 使用下面这句可以在内存中释放对此 url 的伺服，跑了之后那个 URL 就无效了
                //URL.revokeObjectURL(imgURL);
                */
            }
        });
        jQuery('.se-send').click(function(){
            that.sendRecord();
        });
        jQuery('.el-dropdown-selfdefine').click(function(){
            jQuery("[tid='recordType']").toggle(100);
        });

    },
    sendRecord: function () {
        that = this;
        //var objFile = document.getElementById("bar-file");
        var objFile = jQuery('#bar-file')[0].files;
        jQuery(objFile).each(function(index,file){
                var filter = {};
                filter = {
                    file:file,
                    fileName:file.name
                };
                debugger
                var formdata=new FormData();
                formdata.append("fileName",file.name);
                formdata.append("file",file);
                that.actions.updateAttachment(that.options.openId, "record", formdata,file, function (json) {
                    debugger
                    /*if(json.type=="success"){
                        Showbo.Msg.alert('附件上传成功!');
                    }*/
                }.bind(that));
            }
        );

        var filters = {};
        filters = {
            types:"contacts",
            typesid:that.options.openId,
            content:jQuery('.el-textarea__inner').val(),
            category:jQuery('.se-select-name').text(),
            nexttime:jQuery('.el-input__inner').text(),
            businessids:"",
            contactsids:"",
            createuser:""
        };
        that.actions.createRecord(filters,function(json){
            if(json.type=="success"){
                Showbo.Msg.alert('跟进记录发布成功!');
                that.loadRecord();
            }
        }.bind(that),function(xhr,text,error){

        }.bind(that));
    },
    loadRecord: function(){
        //that = this;
        this.actions.getRecord(this.options.openId, function (json) {
            if(json.type=="success"){
                var recordDatas = json.data;
                jQuery(".fl-c").remove();
                var logItemHtml = "";
                debugger
                for ( i in recordDatas){
                    if(i<recordDatas.length){
                        var recordData = recordDatas[i];
                        var personImg = 'http://172.16.92.55/x_component_CRM/$Template/portrait.png';
                        logItemHtml = logItemHtml+'<div class="fl-c"><div class="vux-flexbox fl-h vux-flex-row">'+
                            '<div class="div-photo fl-h-img"  style="background-image: url(&quot;'+personImg+'&quot;);" lazy="loaded"></div> '+
                            '<div class="fl-h-b"><div class="fl-h-name">'+recordData.person.name+'</div><div class="fl-h-time">'+recordData.updateTime+'</div></div>'+
                            //'<div class="vux-flexbox fl-h-mark vux-flex-row"><img src="" class="fl-h-mark-img"><div class="fl-h-mark-name">跟进记录</div></div>'+
                            //'<div class="el-dropdown"><i class="el-icon-arrow-down el-icon-more el-dropdown-selfdefine" style="color: rgb(205, 205, 205); margin-left: 8px;" aria-haspopup="list" aria-controls="dropdown-menu-4704" role="button" tabindex="0"></i> <ul class="el-dropdown-menu el-popper" style="display: none;" id="dropdown-menu-4704"><li data-v-e36820dc="" tabindex="-1" class="el-dropdown-menu__item">删除</li></ul></div>'+
                            '</div>'+
                            '<div class="fl-b"><div class="fl-b-content">'+recordData.content+'</div><div class="follow"><span class="follow-info">'+recordData.category+'</span></div></div>'+
                            '<div  class="full-container" style="display: none;"></div></div>'
                    }
                }
                if(logItemHtml!=""){
                    jQuery(".load").before(logItemHtml);
                }
                if(recordDatas.length<1){
                    jQuery(".load").hide();
                    jQuery(".empty-mask").show();
                }
            }
        }.bind(this));
    },
    toDecimal: function(x){
        if(x==""){
            return "";
        }else{
            var f = parseFloat(x);
            if (isNaN(f)) {
                return x;
            }
            f = Math.round(x*100)/100;
            return f;
        }
    },
    open: function (e) {
        this.fireEvent("queryOpen");
        this._open();
        this.fireEvent("postOpen");
    },
    create: function () {
        this.fireEvent("queryCreate");
        this.isNew = true;
        this._open();
        this.fireEvent("postCreate");
    },
    edit: function () {
        this.fireEvent("queryEdit");
        this.isEdited = true;
        this._open();
        this.fireEvent("postEdit");
    },
    _open: function () {
        if( this.options.hasMask ){
            this.formMaskNode = new Element("div.formMaskNode", {
                "styles": this.css.formMaskNode,
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
            }).inject( this.container || this.app.content);
        }

        this.formAreaNode = new Element("div.formAreaNode", {
            "styles": this.css.formAreaNode
        });

        this.createFormNode();

        this.formAreaNode.inject(this.formMaskNode || this.container || this.app.content, "after");
        this.formAreaNode.fade("in");

        this.setFormNodeSize();
        this.setFormNodeSizeFun = this.setFormNodeSize.bind(this);
        if( this.app )this.app.addEvent("resize", this.setFormNodeSizeFun);

        if (this.options.draggable && this.formTopNode) {
            var size = (this.container || this.app.content).getSize();
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
    createFormNode: function () {
        var _self = this;

        this.formNode = new Element("div.formNode", {
            "styles": this.css.formNode
        }).inject(this.formAreaNode);

        if (this.options.hasTop) {
            this.createTopNode();
        }

        if (this.options.hasIcon) {
            this.formIconNode = new Element("div.formIconNode", {
                "styles": this.isNew ? this.css.formNewNode : this.css.formIconNode
            }).inject(this.formNode);
        }

        this.createContent();
        //formContentNode.set("html", html);

        if (this.options.hasBottom) {
            this.createBottomNode();
        }

        this._setCustom();

        if( this.options.hasScroll ){
            //this.setScrollBar(this.formTableContainer)
            MWF.require("MWF.widget.ScrollBar", function () {
                new MWF.widget.ScrollBar(this.formTableContainer, {
                    "indent": false,
                    "style": "default",
                    "where": "before",
                    "distance": 30,
                    "friction": 4,
                    "axis": {"x": false, "y": true},
                    "onScroll": function (y) {
                        //var scrollSize = _self.viewContainerNode.getScrollSize();
                        //var clientSize = _self.viewContainerNode.getSize();
                        //var scrollHeight = scrollSize.y - clientSize.y;
                        //if (y + 200 > scrollHeight && _self.view && _self.view.loadElementList) {
                        //    if (!_self.view.isItemsLoaded) _self.view.loadElementList();
                        //}
                    }
                });
            }.bind(this));
        }
    },
    createContent: function () {
        this.formContentNode = new Element("div.formContentNode", {
            "styles": this.css.formContentNode
        }).inject(this.formNode);

        this.formTableContainer = new Element("div.formTableContainer", {
            "styles": this.css.formTableContainer
        }).inject(this.formContentNode);

        this.formTableArea = new Element("div.formTableArea", {
            "styles": this.css.formTableArea,
            "text":"loading..."
        }).inject(this.formTableContainer);


        this._createTableContent();
    },
    createBottomNode: function () {
        this.formBottomNode = new Element("div.formBottomNode", {
            "styles": this.css.formBottomNode
        }).inject(this.formNode);

        this._createBottomContent()
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
                    this.close();
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
        this.loadFormData();
        /*
         var Ttype = "clue";
         this.actions.getProfiles(Ttype,function(json){
         this.profileData = json.data;
         if(this.data.id){
         this.actions.getCustomerInfo(this.data.id,function(json){
         this.customerData = json.data;
         this.loadFormData();
         this.createCustomBottom();
         }.bind(this));
         }else{
         this.loadFormData();
         this.createCustomBottom();
         }
         }.bind(this));
         */
    },
    _createBottomContent: function () {
        this.cancelActionNode = new Element("div.formCancelActionNode", {
            "styles": this.css.formCancelActionNode,
            "text": this.lp.actionCancel
        }).inject(this.formBottomNode);

        if (this.options.isNew || this.options.isEdited) {
            //this.ok();
            this.okActionNode = new Element("div.formOkActionNode", {
                "styles": this.css.formOkActionNode,
                "text": this.lp.actionConfirm
            }).inject(this.formBottomNode);
            this.okActionNode.addEvent("click", function (e) {
                this.ok(e);
            }.bind(this));
        }
        this.cancelActionNode.addEvent("click", function (e) {
            this.cancel(e);
        }.bind(this));

    },
    loadFormData:function(){
        var tmpData={};
        this.loadForm();

        /*
         var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
         "<tr>" +
         "   <td styles='formTableTitle'><span lable='TCustomerName'></span><span style='color:#f00'>*</span></td>" +
         "   <td styles='formTableValue' item='TCustomerName'></td>" +
         "</tr><tr>" +
         "   <td styles='formTableTitle' lable='TCustomerType'></td>" +
         "   <td styles='formTableValue'><div id='TCustomerType'></div></td>" +
         "</tr><tr>" +
         "   <td styles='formTableTitle' lable='TCustomerLevel'></td>" +
         "   <td styles='formTableValue'><div id='TCustomerLevel'></div></td>" +
         "</tr><tr>" +
         "   <td styles='formTableTitle' lable='TSource'></td>" +
         "   <td styles='formTableValue'><div id='TSource'></div></td>" +
         "</tr><tr>" +
         "   <td styles='formTableTitle' lable='TIndustryFirst'></td>" +
         "   <td styles='formTableValue'><div id='TIndustryFirst'></div><div id='TIndustrySecond'></div></td>" +
         "</tr><tr>" +
         "   <td styles='formTableTitle' lable='TDistrict'></td>" +
         "   <td styles='formTableValue'><div id='TProvince'></div><div id='TCity'></div><div id='TArea'></div></td>" +
         "</tr><tr>" +
         "   <td styles='formTableTitle' lable='TStreet'></td>" +
         "   <td styles='formTableValue' item='TStreet'></td>" +
         "</tr><tr>" +
         "   <td styles='formTableTitle' lable='TLocation'></td>" +
         "   <td styles='formTableValue'><div style='width:100%;height:30px;'><input type='text' placeholder='"+this.lp.TLocationNotice+"' id='mapLocation' disabled/></div></td>" +
         "</tr><tr>" +
         "   <td styles='formTableTitle'></td>" +
         "   <td styles='formTableValue'><div id='mapDiv' styles='mapDiv'></div></td>" +
         "</tr><tr>" +
         "   <td styles='formTableTitle' lable='TTelphone'></td>" +
         "   <td styles='formTableValue' item='TTelphone'></td>" +
         "</tr><tr>" +
         "   <td styles='formTableTitle' lable='TFax'></td>" +
         "   <td styles='formTableValue' item='TFax'></td>" +
         "</tr><tr>" +
         "   <td styles='formTableTitle' lable='TRemark'></td>" +
         "   <td styles='formTableValue' item='TRemark'></td>" +
         "</tr><tr>" +
         "   <td styles='formTableTitle' lable='TWebSite'></td>" +
         "   <td styles='formTableValue' item='TWebSite'></td>" +
         "</tr><tr>" +
         "   <td styles='formTableTitle' lable='TEmail'></td>" +
         "   <td styles='formTableValue' item='TEmail'></td>" +
         "</tr><tr>" +
         "   <td styles='formTableTitle' lable='TCustomerStatus'></td>" +
         "   <td styles='formTableValue'><div id='TCustomerStatus'></div></td>" +
         "</tr><tr>" +
         "   <td styles='formTableTitle' lable='TCustomerGrade'></td>" +
         "   <td styles='formTableValue'><div id='TCustomerGrade'></div></td>" +
         "</tr>" +
         "</table>"
         this.formTableArea.set("html", html);


         this.TCustomerType = this.formTableArea.getElement("#TCustomerType");
         this.TCustomerLevel = this.formTableArea.getElement("#TCustomerLevel");
         this.TSource = this.formTableArea.getElement("#TSource");
         this.TIndustryFirst = this.formTableArea.getElement("#TIndustryFirst");
         this.TIndustrySecond = this.formTableArea.getElement("#TIndustrySecond");
         this.TProvince = this.formTableArea.getElement("#TProvince");
         this.TCity = this.formTableArea.getElement("#TCity");
         this.TArea = this.formTableArea.getElement("#TArea");
         this.TCustomerStatus = this.formTableArea.getElement("#TCustomerStatus");
         this.TCustomerGrade = this.formTableArea.getElement("#TCustomerGrade");

         var size = {"width":230,"height":30};

         this.TIndustryFirst.setStyles({"float":"left"});
         this.TIndustrySecond.setStyles({"float":"left","margin-left":"10px"});



         //客户类型
         this.TCustomerTypeSelector =  new MWF.xApplication.CRM.Template.Select(this.TCustomerType,this, this.actions, size);
         this.TCustomerTypeSelector.load();
         alert(JSON.stringify(this.profileData.customertype_config))
         this.TCustomerTypeSelector.setList(this.profileData.customertype_config);
         if(this.customerData && this.customerData.customertype){
         this.TCustomerTypeSelector.selectValueDiv.set({"text":this.customerData.customertype});
         this.TCustomerTypeSelector.node.set("value",this.customerData.customertype);
         }
         //客户级别
         this.TCustomerLevelSelector =  new MWF.xApplication.CRM.Template.Select(this.TCustomerLevel,this, this.actions, size);
         this.TCustomerLevelSelector.load();
         this.TCustomerLevelSelector.setList(this.profileData.level_config);
         if(this.customerData && this.customerData.level){
         this.TCustomerLevelSelector.selectValueDiv.set({"text":this.customerData.level});
         this.TCustomerLevelSelector.node.set("value",this.customerData.level);
         }
         //来源
         this.TSourceSelector =  new MWF.xApplication.CRM.Template.Select(this.TSource,this, this.actions, size);
         this.TSourceSelector.load();
         this.TSourceSelector.setList(this.profileData.source_config);
         if(this.customerData && this.customerData.source){
         this.TSourceSelector.selectValueDiv.set({"text":this.customerData.source});
         this.TSourceSelector.node.set("value",this.customerData.source);
         }
         //行业
         this.TIndustryFirstSelector =  new MWF.xApplication.CRM.Template.Select(this.TIndustryFirst,this, this.actions, {"width":230,"height":30});
         this.TIndustrySecondSelector =  new MWF.xApplication.CRM.Template.Select(this.TIndustrySecond,this, this.actions, {"width":230,"height":30,"available":"no"});
         this.TIndustrySecondSelector.load();
         this.TIndustryFirstSelector.load();
         if(this.customerData && this.customerData.industryfirst){
         this.TIndustryFirstSelector.selectValueDiv.set({"text":this.customerData.industryfirst});
         this.TIndustryFirstSelector.node.set("value",this.customerData.industryfirst);


         this.profileData.industry_config.childNodes.each(function(d){
         if(d.configname == this.customerData.industryfirst){
         tmpData = d;
         }
         }.bind(this));
         this.TIndustrySecondSelector.setList(tmpData);
         this.TIndustrySecond.set("available","yes");
         this.TIndustrySecond.setStyles({"background-color":""});

         }
         if(this.customerData && this.customerData.industrysecond){
         this.TIndustrySecondSelector.selectValueDiv.set({"text":this.customerData.industrysecond});
         this.TIndustrySecondSelector.node.set("value",this.customerData.industrysecond);

         this.profileData.industry_config.childNodes.each(function(d){
         if(d.configname == this.customerData.industryfirst){
         tmpData = d;
         }
         }.bind(this));
         this.TIndustrySecondSelector.setList(tmpData);
         this.TIndustrySecond.set("available","yes");
         this.TIndustrySecond.setStyles({"background-color":""});

         }
         this.TIndustryFirstSelector.setList(this.profileData.industry_config,function(d){
         if(this.TIndustryFirst.get("value") == this.lp.defaultSelect){
         this.TIndustrySecondSelector.createDefault();
         this.TIndustrySecondSelector.setList();
         this.TIndustrySecond.set("available","no");
         this.TIndustrySecond.setStyles({"background-color":"#eeeeee"})
         }else{
         this.TIndustrySecondSelector.createDefault();
         this.TIndustrySecondSelector.setList(d);
         this.TIndustrySecond.set("available","yes");
         this.TIndustrySecond.setStyles({"background-color":""});
         }
         }.bind(this));



         //省、市、区
         this.TProvinceSelector =  new MWF.xApplication.CRM.Template.Select(this.TProvince,this, this.actions, {"width":150,"height":30});
         this.TProvinceSelector.load({},function(){
         this.actions.getProvinceList(function(json){
         this.TProvinceSelector.setAddress(json.data,function(d){
         //city
         if(this.TProvince.get("value") == this.lp.defaultSelect){
         this.TCitySelector.createDefault();
         this.TCitySelector.setAddress();
         this.TCity.set("available","no");
         this.TCity.setStyles({"background-color":"#eeeeee"});
         }else{
         this.actions.getCityList({pid: d.cityid},function(json){
         this.TCitySelector.createDefault();
         this.TCitySelector.setAddress(json.data,function(dd){
         //area
         if(this.TCity.get("value") == this.lp.defaultSelect){
         this.TAreaSelector.createDefault();
         this.TAreaSelector.setAddress();
         this.TArea.set("available","no");
         this.TArea.setStyles({"background-color":"#eeeeee"});
         }else{
         this.actions.getAreaList({pid:dd.cityid},function(json){
         this.TAreaSelector.createDefault();
         this.TAreaSelector.setAddress(json.data);
         this.TArea.set("available","yes");
         this.TArea.setStyles({"background-color":""});
         }.bind(this));
         }

         }.bind(this));
         this.TCity.set("available","yes");
         this.TCity.setStyles({"background-color":""});
         }.bind(this));

         }

         this.TAreaSelector.createDefault();
         this.TAreaSelector.setAddress();
         this.TArea.set("available","no");
         this.TArea.setStyles({"background-color":"#eeeeee"});
         }.bind(this))
         }.bind(this))
         }.bind(this));
         this.TCitySelector =  new MWF.xApplication.CRM.Template.Select(this.TCity,this, this.actions, {"width":150,"height":30,"available":"no"});
         this.TCitySelector.load();
         this.TAreaSelector =  new MWF.xApplication.CRM.Template.Select(this.TArea,this, this.actions, {"width":150,"height":30,"available":"no"});
         this.TAreaSelector.load();

         if(this.customerData && this.customerData.province){ //省
         this.TProvinceSelector.selectValueDiv.set({"text":this.customerData.province});
         this.TProvinceSelector.node.set("value",this.customerData.province);
         }
         if(this.customerData && this.customerData.city){ //市
         if(this.customerData && this.customerData.province){
         this.actions.getCityListByName({"regionname":this.customerData.province},
         function(json){
         this.TCitySelector.setAddress(json.data,function(dd){
         //area
         if(this.TCity.get("value") == this.lp.defaultSelect){
         this.TAreaSelector.createDefault();
         this.TAreaSelector.setAddress();
         this.TArea.set("available","no");
         this.TArea.setStyles({"background-color":"#eeeeee"});
         }else{
         this.actions.getAreaList({pid:dd.cityid},function(json){
         this.TAreaSelector.createDefault();
         this.TAreaSelector.setAddress(json.data);
         this.TArea.set("available","yes");
         this.TArea.setStyles({"background-color":""});
         }.bind(this));
         }

         }.bind(this));
         }.bind(this));
         }
         this.TCitySelector.selectValueDiv.set({"text":this.customerData.city});
         this.TCitySelector.node.set("value",this.customerData.city);
         this.TCity.set("available","yes");
         this.TCity.setStyles({"background-color":""});


         }
         if(this.customerData && this.customerData.county){ //区
         if(this.customerData && this.customerData.city){
         this.actions.getAreaListByName({"regionname":this.customerData.city},
         function(json){
         this.TAreaSelector.setAddress(json.data);
         }.bind(this));
         }
         this.TAreaSelector.selectValueDiv.set({"text":this.customerData.county});
         this.TAreaSelector.node.set("value",this.customerData.county);
         this.TArea.set("available","yes");
         this.TArea.setStyles({"background-color":""});
         }

         this.TProvince.setStyles({"float":"left"});
         this.TCity.setStyles({"float":"left","margin-left":"10px"});
         this.TArea.setStyles({"float":"left","margin-left":"10px"});

         this.TCustomerStatusSelector =  new MWF.xApplication.CRM.Template.Select(this.TCustomerStatus,this, this.actions, size);
         this.TCustomerStatusSelector.load();
         this.TCustomerStatusSelector.setList(this.profileData.state_config);
         if(this.customerData && this.customerData.state){
         this.TCustomerStatusSelector.selectValueDiv.set({"text":this.customerData.state});
         this.TCustomerStatusSelector.node.set("value",this.customerData.state);
         }
         this.TCustomerGradeSelector = new MWF.xApplication.CRM.Template.Select(this.TCustomerGrade,this, this.actions, size);
         this.TCustomerGradeSelector.load();
         this.TCustomerGradeSelector.setList(this.profileData.customerrank_config);

         this.TMap = this.formTableArea.getElement("#mapDiv");
         this.TMap.addEvents({
         "mousewheel":function(e){
         e.stopPropagation();
         }
         });
         this.mapLocation = this.formTableArea.getElement("#mapLocation");
         this.mapLocation.setStyles({
         "width": "99%",
         "text-indent":"5px",
         "border":"1px solid #999",
         "background-color":"#eee",
         "border-radius": "3px",
         "box-shadow": "0px 0px 6px #eee",
         "height": "26px"
         });


         MWF.xDesktop.requireApp("CRM", "BaiduMap", function(){
         this.bMap = new MWF.xApplication.CRM.BaiduMap(this.TMap,this.app,this,this.actions,{"from":"newCustomer"});
         var mapData = {};
         if(this.customerData && this.customerData.addresslatitude){
         mapData.latitude = this.customerData.addresslatitude
         }
         if(this.customerData && this.customerData.addresslongitude){
         mapData.longitude = this.customerData.addresslongitude
         }
         this.bMap.load(mapData);
         }.bind(this));
         */
    },
    ok: function (e) {
        this.fireEvent("queryOk");
        var data = this.form.getResult(true, ",", true, false, true);
        debugger;
        if (data) {
            this._ok(data, function (json) {
                if (json.type == "error") {
                    if( this.app )this.app.notice(json.message, "error");
                } else {
                    if( this.formMaskNode )this.formMaskNode.destroy();
                    this.formAreaNode.destroy();
                    if (this.explorer && this.explorer.view)this.explorer.view.reload();
                    if( this.app )this.app.notice(this.isNew ? this.lp.createSuccess : this.lp.updateSuccess, "success");
                    this.fireEvent("postOk");
                }
            }.bind(this))
        }
    },
    loadForm: function(){
        _self = this;
        this.form = new MForm(this.formTableArea, this.data, {
            style: "default",
            isEdited: this.isEdited || this.isNew,
            itemTemplate: this.getItemTemplate(this.lp )
        },this.app,this.css);
        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>";
        var itemTemplateObject = this.form.itemTemplate;
        debugger
        for ( i in itemTemplateObject){
            html = html+"<tr>" +
                "   <td styles='formTableTitle'><span lable='"+i+"'>"+itemTemplateObject[i].text+"</td>" +
                "   <td styles='formTableValue' item='"+i+"'></td>" +
                "</tr>";
        }
        html = html+"</table>";
        debugger
        this.formTableArea.set("html", html);
        this.form.load();

        if(!this.isNew){
            this.actions.getCustomerInfo(this.options.openId, function (json) {
                debugger
                var jsonObj = json.data;

                var spanObject = this.formTableArea.getElements("span");
                for ( j in spanObject){
                    if(j < spanObject.length){
                        debugger
                        if(spanObject[j].get("name")!=null){
                            var fieldName = spanObject[j].get("name");
                            for (var prop in jsonObj){
                                if(prop == fieldName){
                                    spanObject[j].set("text",jsonObj[prop]);
                                }
                            }
                        }

                    }
                }
            }.bind(this));
        }


        //this.nexttime = this.formTableArea.getElement("#nexttime");
        //this.nexttime.addEvent("click",function(){
        //_self.selectCalendar(this);
        //});
        this.formTableArea.getElements("textarea").setStyles({"height":"100px","overflow":"auto","color":"#666666"});
        this.formTableArea.getElements("input").setStyles({"color":"#666666"});
    },
    getItemTemplate: function( lp ){
        _self = this;
        return {
            contactsname:{
                type: "text",
                text: lp.contactsname,
                value:this.customerData && this.customerData.customername?this.customerData.customername:""
            },
            customername: {
                text: lp.customername,
                type: "openSelect"
            },
            cellphone: {
                text:lp.cellphone,
                type: "text"
            },
            telephone:{
                type: "text",
                text: lp.telephone
            },
            email: {
                text:lp.email,
                type: "text"
            },
            decision: {
                type: "select",
                text: lp.decision,
                value:_self.app.lp.contact.decision.value
            },
            post: {
                text:lp.post,
                type: "text"
            },
            sex: {
                type: "select",
                text: lp.sex,
                value:_self.app.lp.contact.sex.value
            },
            detailaddress: {
                text:lp.detailaddress,
                type: "text"
            },
            nexttime: {
                text:lp.nexttime,
                attr : {id:"nexttime"},
                type: "datetime"
            },
            remark: {
                text:lp.remark,
                type: "textarea"
            }
        }
    },
    getContactTemplate: function( lp ){
        _self = this;
        return {
            contactsname: {
                text: lp.contactsname,
                type: "text",
                notEmpty:true
            },
            customername: {
                text: lp.customername,
                type: "readonly",
                notEmpty:true
            },
            telephone:{
                type: "text",
                text: lp.telephone,
            },
            cellphone: {
                text:lp.cellphone,
                type: "text"
            },
            email:{
                type: "text",
                text: lp.email
            },
            decision: {
                type: "select",
                text: lp.decision,
                value:this.app.lp.contact.decision.value
            },
            post: {
                text:lp.post,
                type: "text"
            },
            sex: {
                type: "select",
                text: lp.sex,
                value:this.app.lp.contact.sex.value
            },
            detailaddress: {
                text:lp.detailaddress,
                type: "text"
            },
            nexttime: {
                text:lp.nexttime,
                attr : {id:"nexttime"},
                type: "datetime"
            },
            remark: {
                text:lp.remark,
                type: "textarea"
            }
        }
    },
    selectPerson: function (showContainer,nameId,fullNameId,count) {
        var options = {
            "type" : "",
            "types": ["person"],
            "values": this.configData,
            "count": count,
            "zIndex": 50000,
            "onComplete": function(items){
                MWF.require("MWF.widget.O2Identity", function(){
                    var invitePersonList = [];
                    var fullPersonList = [];
                    this.configData = [];
                    this.process = null;
                    items.each(function(item){
                        var _self = this;
                        if( item.data.distinguishedName.split("@").getLast().toLowerCase() == "i" ){
                            var person = new MWF.widget.O2Identity(item.data, it.form.getItem("invitePersonList").container, {"style": "room"});
                            invitePersonList.push( item.data.distinguishedName );
                        }else{
                            //var person = new MWF.widget.O2Person(item.data, it.form.getItem("invitePersonList").container, {"style": "room"});
                            invitePersonList.push(item.data.name);
                            fullPersonList.push(item.data.distinguishedName);
                            var personJson = {
                                "name": item.data.name,
                                "distinguishedName": item.data.distinguishedName,
                                "employee":item.data.employee
                            }
                            debugger
                            this.configData.push(personJson);
                        }
                    }.bind(this));
                    if(items.length==0){
                        document.getElementById(nameId).innerHTML = "+点击选择"
                    }else{
                        document.getElementById(nameId).innerHTML = invitePersonList.join(",");
                        if(fullNameId!=""){
                            document.getElementById(fullNameId).innerHTML = fullPersonList.join(",");
                        }
                    }

                }.bind(this));
            }.bind(this)
        };
        var selector = new MWF.O2Selector(showContainer, options);
    },
    getFormateTime: function( timeStr ){
        _self = this;
        var date= new Date(timeStr);
        return date.getFullYear()+'-'+_self.checkTime(date.getMonth()+1)+'-'+_self.checkTime(date.getDate())+ ' ' + _self.checkTime(date.getHours()) + ':' + _self.checkTime(date.getMinutes()) + ':' + _self.checkTime(date.getSeconds());
    },
    checkTime: function( i ){
        if(i<10){
            i = '0'+i
        }
        return i;
    },
    toDecimal: function(x){
        if(x==""){
            return "";
        }else{
            var f = parseFloat(x);
            if (isNaN(f)) {
                return x;
            }
            f = Math.round(x*100)/100;
            return f;
        }
    },

    createCustomBottom:function(){
        this.okActionNode = new Element("div.formOkActionNode", {
            "styles": this.css.formOkActionNode,
            "text": this.lp.actionConfirm
        }).inject(this.formBottomNode);

        this.okActionNode.addEvent("click", function (e) {
            this.ok(e);
        }.bind(this));
    },

    _ok: function (data, callback) {

        var saveDataStr = "";
        for ( i in this.data){
            saveDataStr = saveDataStr+"'"+i+"':'"+this.data[i]+"',";
        }
        debugger;
        saveDataStr =  "'{"+saveDataStr.replace(/'/g, '"')+"}'";
        debugger;
        //var saveData = JSON.parse(saveDataStr);
        var saveData = eval('(' + saveDataStr.substring(1,saveDataStr.length - 1) + ')');
        debugger;
        //alert(JSON.stringify(saveData))
        this.app.createShade();
        this.actions.saveClue(saveData,function(json){
            this.app.destroyShade();
            this.app.notice(this.lp.saveSuccess,"success");
            this.close();
            this.fireEvent("reloadView",json);
        }.bind(this),function(xhr,text,error){
            this.app.showErrorMessage(xhr,text,error);
            this.app.destroyShade();
        }.bind(this));
    }
});

