MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xApplication.CRM.ChanceEdit = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "800",
        "height": "600",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "draggable": true,
        "maxAction" : true,
        "closeAction": true,
        "isFull" : false,
        "startTime" : null,
        "endTime" : null,
        "isWholeday" : false,
        "title" : "新建商机",
        "defaultCalendarId" : ""
    },
    load: function(){
        this.lp = this.lp.chanceEdit||{};
        this.cssPath = "/x_component_CRM/$ChanceEdit/"+this.options.style+"/css.wcss";
        this.path = "/x_component_CRM/$ChanceEdit/";
        this.type ={};
        this._loadCss();
        this.loadData();

    },
    create: function () {
        this.fireEvent("queryCreate");
        this.isNew = true;
        this.loadResource(function(){
            this._open();
        }.bind(this));
        this.fireEvent("postCreate");
    },
    edit: function(){
        this.fireEvent("queryCreate");
        this.isEdited = true;
        this.loadResource(function(){
            this._open();
        }.bind(this));
        this.fireEvent("postCreate");
    },
    loadData: function(){
        this.Customer = this.data&&this.data.customer?this.data.customer:{};
        if(!this.types){
            this.actions.getTypes(function(json){
                this.types=json.data;
            }.bind(this));
            if(this.types&&this.types.length){
                //var self = this;
                for(i = 0 ; i < this.types.length ; i ++){
                    this.actions.getStatusByTypeid(this.types[i].id,function (json) {
                        //this.statusid[type.id]=json.data;
                        this.types[i].statusid = json.data;
                       // console.log("this is complete statusid===>>>>",this.data.statusid,"======",json.data.id)
                        json.data.forEach(function(e,i){
                            if(this.data.statusid==e.id){
                                this.statusid = e;
                            }
                        }.bind(this))

                    }.bind(this))
                    if(this.data.typeid==this.types[i].id){
                        this.type=this.types[i];
                    }

                }
            }
        }
        //console.log(this.types);
    },
    loadResource: function ( callback ) {
        callback();
        /*var baseUrls = [
            "/x_component_CRM/$Template/assets/js/jquery.min.js",
        ];
        var fullcalendarUrl = "/x_component_CRM/$Template/assets/js/notifyme.js";
        var confirmUrl = "/x_component_CRM/$Template/assets/js/showBo.js";
        //var confirmUrl = "/x_component_CRM/$Main/laydate/laydate.js";
        COMMON.AjaxModule.loadCss("/x_component_CRM/$Template/assets/css/notifyme.css",function(){
            COMMON.AjaxModule.load(baseUrls, function(){
                jQuery.noConflict();
                COMMON.AjaxModule.load(fullcalendarUrl, function(){
                    COMMON.AjaxModule.load(confirmUrl, function(){
                        //if(callback)callback();
                    }.bind(this))
                }.bind(this));
            }.bind(this))
        }.bind(this));
        COMMON.AjaxModule.loadCss("/x_component_CRM/$Template/date/css/jquery-ui.css",function(){
            COMMON.AjaxModule.load(baseUrls, function(){
                jQuery.noConflict();
                COMMON.AjaxModule.load("/x_component_CRM/$Template/date/jquery-ym-datePlugin-0.1.js", function(){
                    if(callback)callback();
                    /!* COMMON.AjaxModule.load("/x_component_CRM/$Template/date/js/datepicker.all.js", function(){
                     if(callback)callback();
                     }.bind(this))*!/
                }.bind(this));
            }.bind(this))
        }.bind(this));*/

    },
    _createTableContent: function(){

        this.formTableArea.set("html", this.getHtml());
        //this.formTableArea.setClassName("notify-content");
        this.formTableArea.getElements(".conent-value").forEach(function(e,i){

            var cobj = e.getChildren()[0];
            var stype = cobj.getAttribute("stype");
            var sid = cobj.getAttribute("id");
            if(stype=="datetime"&&(this.isNew||this.isEdited)){
                this.loadTimeContainer(cobj);
            }
            if(sid=="typeid"&&(this.isNew||this.isEdited)){
                var self = this;
                var valueArr = this.types;
                if(valueArr.length>0){
                    var selectHtml = '<ul class="el-dropdown-type" style="display: none;" tid="'+sid+'">'
                    for(var n=0;n<valueArr.length;n++){
                        selectHtml = selectHtml+'<li class="el-dropdown-menu__item" index="'+n+'">'+valueArr[n].opportunitytypename+'</li>'
                    }
                    jQuery(self.formTableArea).append(selectHtml+'<div class="popper__arrow"></div></ul>');
                    jQuery(cobj).click(function(){

                        jQuery(self.formTableArea.getElement("[tid="+sid+"]")).css({"left":jQuery(cobj).offset().left,"top":jQuery(cobj).offset().top+30})
                        jQuery(self.formTableArea.getElement("[tid="+sid+"]")).toggle(100);
                    }.bind(this));
                    jQuery(self.formTableArea.getElement("[tid="+sid+"]")).children().click(function(){
                        debugger
                        jQuery(cobj).text(jQuery(this).text());
                        jQuery(self.formTableArea.getElement("[tid="+sid+"]")).toggle(100);
                        var newTypeid = self.types[jQuery(this).attr("index")];
                        if(!self.type||newTypeid.id!=self.type.id){
                            self.type = newTypeid;
                            self.statusid = {};
                            self.changeType();
                        }
                    });
                }
            }else if(sid=="statusid"&&(this.isNew||this.isEdited)){
                this.changeType = function(){
                    var cobj = this.formTableArea.getElement("#statusid");
                    var self = this;
                   // console.log("this is statusid=====>>>",this.statusid);
                    var valueArr = this.type.statusid;
                    jQuery("[tid=statusid]").remove();

                    if(!self.statusid||!self.statusid.id){
                        jQuery(cobj).text("");
                    }
                    if(valueArr&&valueArr.length>0){

                        var selectHtml = '<ul class="el-dropdown-type" style="display: none;" tid="statusid">'
                        for(var n=0;n<valueArr.length;n++){
                            selectHtml = selectHtml+'<li class="el-dropdown-menu__item" index="'+n+'">'+valueArr[n].opportunitystatusname+'</li>'
                        }

                        jQuery(self.formTableArea).append(selectHtml+'<div class="popper__arrow"></div></ul>');
                        jQuery(cobj).unbind().click(function(){
                            jQuery(self.formTableArea.getElement("[tid="+sid+"]")).css({"left":jQuery(cobj).offset().left,"top":jQuery(cobj).offset().top+30})
                            jQuery(self.formTableArea.getElement("[tid="+sid+"]")).toggle(100);
                        });
                        jQuery(self.formTableArea.getElement("[tid="+sid+"]")).children().click(function(){
                            var newStatusid = self.type.statusid[jQuery(this).attr("index")];
                            self.statusid = newStatusid;
                            jQuery(cobj).text(jQuery(this).text());
                            jQuery(self.formTableArea.getElement("[tid="+sid+"]")).toggle(100);
                        });
                    }
                }.bind(this);
                this.changeType();

            }else if(sid=="customer"&&(this.isNew||this.isEdited)){
                cobj.setAttribute("readOnly","true");

                jQuery(cobj).click(function(){

                    this.selectCustomer =  new  MWF.xApplication.CRM.ChanceEdit.selectForm (null,{},null, {
                            app: this,
                            container: this.formTableArea,
                            lp: this.lp,
                            actions: this.actions,
                            css: {},
                        }
                    );
                    this.selectCustomer.create();
                }.bind(this));
            }
        }.bind(this));

    },
    getHtml: function(){
        var section_header = '<div class="section-header"><div class="section-mark" style="border-left-color: rgb(70, 205, 207);"></div> '+
            '<div data-v-ec8f8850="" class="section-title">基本信息</div></div>';
        var itemTemplateObject = this.getItemTemplate(this.lp );
        var section_conent = '<div class="section-conent">';
        for ( i in itemTemplateObject){
            var stype = itemTemplateObject[i].type;
            var innerHtml = '<input type="text" class="inline-input" '+(this.isEdited||this.isNew?'':'readOnly')+' value="'+itemTemplateObject[i].value+'"  name="'+i+'" id="'+i+'" stype="'+stype+'">';
            if(stype=="textarea"){
                innerHtml =  '<textarea rows="6" class="el-textarea__inner"  '+(this.isEdited||this.isNew?'':'readOnly')+' id="'+i+'" stype="'+stype+'"  style="resize: none; min-height: 30.6px;">'+itemTemplateObject[i].value+'</textarea>';
            }
            if(stype=="select"){
                innerHtml = '<div class="inline-input" style="display: inline-block;cursor:pointer;" '+(this.isEdited||this.isNew?'':'readOnly')+' id="'+i+'" stype="'+stype+'" >'+itemTemplateObject[i].value+'</div><div class="el-icon-arrow-down el-icon--right" style="margin-left: -20px; display: inline-block;"><img src="/x_component_CRM/$ChanceEdit/default/icons/arrow.png"></div>';
            }
            section_conent = section_conent+'<div class="conent-inline"><div class="conent-title" lable="'+i+'">'+itemTemplateObject[i].text+'</div>' +
                '<div class="conent-value">'+innerHtml+'</div></div>';
        }
        section_conent = section_conent + '</div>';
        var section_button = '<div class="section_button"><button class="el-button handle-button el-button-cancle"><span>取消</span></button>'+
            '<button class="el-button handle-button el-button-primary"><span>保存</span></button></div>';
        var htmlstr = section_header+section_conent;
        return htmlstr;
    },
    loadTimeContainer: function(obj){
        jQuery(obj).ymdateplugin({
            showTimePanel: true
        });
    },
    getItemTemplate: function( lp ){
        _self = this;
        return {
            name: {
                text: lp.name,
                type: "text",
                //attr : {placeholder:lp.name},
                notEmpty:true,
                value:this.data&&this.data.opportunityname?this.data.opportunityname:""
            },
            customer:{
                type: "text",
                text: lp.customer,
                value:this.data && this.data.customer&&this.data.customer.customername?this.data.customer.customername:""
            },
            typeid: {
                type: "select",
                text: lp.typeid,
                value:this.data && this.data.opportunityType&&this.data.opportunityType.opportunitytypename?this.data.opportunityType.opportunitytypename:""
            },
            statusid: {
                type: "select",
                text: lp.statusid,
                value:this.data && this.data.opportunityStatus&&this.data.opportunityStatus.opportunitystatusname?this.data.opportunityStatus.opportunitystatusname:""
            },
            money:{
                type: "text",
                text: lp.money,
                value: this.data && this.data.money?this.data.money:""
            },
            dealdate: {
                text: lp.dealdate,
                name:"dealdate",
                attr : {id:"dealdate"},
                type: "datetime",
                value:this.data && this.data.dealdate?this.data.dealdate:""
            },
            remark: {
                text: lp.remark,
                name:"remark",
                type: "textarea",
                value:this.data && this.data.remark?this.data.remark:""
            }
        }
    },
    ok: function(){
        this.fireEvent("queryOk");
        var data = this.getResult();
        //console.log("this is change create data:::",data)
        if (data) {
            this._ok(data, function (json) {
                if (json.type == "error") {
                    if( this.app && this.app.notice )this.app.notice(json.message, "error");
                } else {
                    if( this.formMaskNode )this.formMaskNode.destroy();
                    if( this.formAreaNode )this.formAreaNode.destroy();
                    if (this.explorer && this.explorer.view)this.explorer.view.reload();
                    if( this.app && this.app.notice)this.app.notice(this.isNew ? this.lp.createSuccess : this.lp.updateSuccess, "success");
                    this.fireEvent("postOk");
                }
            }.bind(this))
        }
    },
    getResult: function(){
        var json = {};
        var data = {};
        json.type = "success";
        //console.log(this);
        try{
            data.opportunityname = this.formTableArea.getElement("#name").get("value");
            data.customerid = this.Customer.id;
            data.typeid = this.type.id;
            data.statusid = this.statusid.id;
            data.money = this.formTableArea.getElement("#money").get("value");
            data.dealdate = this.formTableArea.getElement("#dealdate").get("value");
            data.remark = this.formTableArea.getElement("#remark").get("value");
            if(this.isEdited){
                data.id = this.data.id;
            }
            //data.remark = this.formTableArea.getElement("#remark").get("value");
        }catch (e) {
            json.type="error";
            data = {};
            data.reson = e;
        }
        json.data = data;

        return json;
    },
    _ok: function (data, callback) {
        if(data.type="success") {
            if(this.isNew){
                this.actions.createChance(data.data, function (json) {
                    //console.log("this is createChange result:::",json);
                    callback(json);
                })
            }else{

                this.actions.updateChance(data.id,data.data, function (json) {
                    //console.log("this is createChange result:::",json);
                    callback(json);
                })
            }

        }else{
           // console.log("这里需要提示，请填完表单!!",data);
        }

    }
});

MWF.xApplication.CRM.ChanceEdit.selectForm = new Class({
    Extends : MPopupForm,
    options: {
        "style": "default",
        "width": "700",
        "height": "400",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "draggable": true,
        "maxAction" : true,
        "closeAction": true,
        "isFull" : false,
        "startTime" : null,
        "endTime" : null,
        "isWholeday" : false,
        "title" : "选择客户",
        "defaultCalendarId" : "",
        "callback": function(){
           // console.log("you can do something when is ok");

        }.bind(this)
    },
    load: function(){
        //console.log(this.container);
        //this.lp = this.lp.chanceEdit||{};
        this.cssPath = "/x_component_CRM/$ChanceEdit/"+this.options.style+"/css.wcss";
        this.path = "/x_component_CRM/$ChanceEdit/";
        this.type ={};
        this._loadCss();
        this.loadData();

    },
    _createTableContent: function () {
        //this.formTableArea.set("html", this.getHtml());
        var templateUrl = this.path+"customerSelect.json";
        var filter = {};
        this.form =  new  MWF.xApplication.CRM.ChanceEdit.SelectCustomer(
            this.formTableArea,
            null,
            this.app,
            this,
            { templateUrl : templateUrl,filterData:filter,listPageName:"getCustomerListPage"},
            {
                lp:{}
            }
        )
        this.form.load();
    },
    _ok: function (data, callback) {
        if(!data){

        }else{
            if(typeof this.options.callback == "function"){
                this.options.callback();

            }
            this.container.getElement("#customer").setAttribute("value",data.customername);
            this.app.Customer = data;
            if( this.formMaskNode )this.formMaskNode.destroy();
            if( this.formAreaNode )this.formAreaNode.destroy();
            this.fireEvent("postOk");
        }
    },
    loadData:function(){

    }
});
MWF.xApplication.CRM.ChanceEdit.SelectCustomer = new Class({
    Extends: MWF.xApplication.CRM.Template.SelectForm,
    //一般需要重写分页方法
    _getCurrentPageData: function(callback, count, page, searchText){
        var category = this.category = this.options.category;
        if (!count)count = 10;
        if (!page)page = 1;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";

        //if(id=="(0)")this.app.createShade();
        var filter = this.options.filterData || {};
        if(searchText){
            filter = {
                key:searchText
            };
        }
        this.actions.getCustomerListPage(page, count, filter, function (json) {
            if (callback)callback(json);
        }.bind(this));

    }
});