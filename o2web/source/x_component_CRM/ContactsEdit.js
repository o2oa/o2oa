MWF.xApplication.CRM.ContactsEdit = new Class({
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
        this.path = "/x_component_CRM/$ContactEdit/";
        this.cssPath = this.path + this.options.style + "/css.wcss";
        this._loadCss();

        this.options.title = this.lp.title;

        this.data = data || {};
        this.actions = actions;
    },
    load: function () {
        this.loadResource(function(){
            this.appArea = jQuery("body").children(":first");
            this.createForm();
        }.bind(this))

    },
    loadResource: function ( callback ) {
        if(callback)callback();
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
                        if(callback)callback();
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
    createForm:function(){
        _self = this;
        jQuery(_self.appArea).next().attr("style","");
        jQuery(_self.appArea).next().attr("class","mask");
        var section_header = '<div class="section-header"><div class="section-mark" style="border-left-color: rgb(70, 205, 207);"></div> '+
            '<div data-v-ec8f8850="" class="section-title">基本信息</div></div>';
        var itemTemplateObject = _self.lp;
        var section_conent = '<div class="section-conent">';
        debugger
        for ( i in itemTemplateObject){
            var stype = itemTemplateObject[i].type;
            var notEmpty = itemTemplateObject[i].notEmpty?itemTemplateObject[i].notEmpty:"false";
            var innerHtml = '<input type="text" class="inline-input" name="'+i+'" id="'+i+'" notEmpty="'+notEmpty+'" stype="'+stype+'">';
            if(stype=="textarea"){
                innerHtml =  '<textarea rows="6" class="el-textarea__inner"  id="'+i+'" notEmpty="'+notEmpty+'" stype="'+stype+'"  style="resize: none; min-height: 30.6px;"></textarea>';
            }
            if(stype=="select" || stype=="hide"){
                innerHtml = '<div class="inline-input" style="display: inline-block;cursor:pointer;"  id="'+i+'" notEmpty="'+notEmpty+'" stype="'+stype+'" ></div><div class="el-icon-arrow-down el-icon--right" style="margin-left: -20px; display: inline-block;"><img src="/x_component_CRM/$Clue/default/icons/arrow.png"></div>';
            }
            if(i=="customername"){
                innerHtml = '<input type="text" class="inline-input"  readonly="true" style="background-color: #e2ebf9;cursor:pointer;" name="'+i+'" id="'+i+'" notEmpty="'+notEmpty+'" stype="'+stype+'">';
            }
            section_conent = section_conent+'<div class="conent-inline"><div class="conent-title" lable="'+i+'">'+itemTemplateObject[i].text+'</div>' +
                '<div class="conent-value">'+innerHtml+'</div></div>';
        }
        section_conent = section_conent + '</div>';
        var section_button = '<div class="section_button"><div><button class="el-button handle-button el-button-cancle"><span>取消</span></button>'+
            '<button class="el-button handle-button el-button-primary"><span>保存</span></button></div></div>';
        var htmlstr = section_header+section_conent+section_button;

        jQuery(".headMoreImg").notifyMe(
            'left',
            'default',
            "新建联系人",
            '',
            '',
            htmlstr,
            'notifyEdit',
            50
        );
        jQuery(".conent-value").each(function(index,element){
                var cobj = jQuery(element).children().eq(0)
                var stype = jQuery(cobj).attr("stype");
                if(stype=="datetime"){
                    _self.loadTimeContainer(jQuery(cobj).attr("id"));
                }
                if(stype=="openSelect"){
                    jQuery(element).click(function(){
                        _sself = _self.lp;
                        _sself.cancel = "关闭";
                        _sself.ok = "确定";
                        _self.selectCustomer =  new  MWF.xApplication.CRM.ContactsEdit.selectForm (null,{},null, {
                                app: _self,
                                container: jQuery("#notifyEdit")[0],
                                lp: _sself,
                                actions: _self.actions,
                                css: {},
                            }
                        );
                        _self.selectCustomer.create();
                    });
                }
                if(stype=="select" || stype=="hide"){
                    var selectObjects = _self.app.lp.contact;
                    for ( j in selectObjects){
                        if(j==jQuery(cobj).attr("id")){
                            var clp = itemTemplateObject[j];
                            var valueList = clp.value;
                            var valueArr = valueList.split(",");
                            if(valueArr.length>0){
                                var selectHtml = '<ul class="el-dropdown-type" style="display: none;" tid="'+jQuery(cobj).attr("id")+'">'
                                for(var n=0;n<valueArr.length;n++){
                                    selectHtml = selectHtml+'<li class="el-dropdown-menu__item">'+valueArr[n]+'</li>'
                                }
                                jQuery(".notify-content").append(selectHtml+'<div class="popper__arrow"></div></ul>');
                                jQuery(cobj).click(function(){
                                    jQuery("[tid='"+jQuery(cobj).attr("id")+"']").css({"left":jQuery(cobj).offset().left-50,"top":jQuery(cobj).offset().top+30,"width":282})
                                    jQuery("[tid='"+jQuery(cobj).attr("id")+"']").toggle(100);
                                });
                                jQuery("[tid='"+jQuery(cobj).attr("id")+"']").children().click(function(){
                                    debugger
                                    jQuery(cobj).text(jQuery(this).text());
                                    jQuery("[tid='"+jQuery(cobj).attr("id")+"']").toggle(100);
                                });
                            }
                        }
                    }

                }
            }
        );

        jQuery('.el-button-cancle').click(function(){
            setTimeout(function(){
                jQuery("#notifyEdit").remove();
                if(jQuery(".mask").length>0){
                    jQuery(".mask").attr("style",'left: 0px; top: 0px; width: 100%; overflow: hidden; position: absolute; z-index: 500000; background-color: rgb(255, 255, 255)');
                    jQuery(".mask").attr("class","");
                }
            },200);
        });
        jQuery('.el-button-primary').click(function(){
            var sflag = true;
            jQuery(".inline-input[notempty='true']").each(function(index,element){
                if(jQuery(element).val()=="" && jQuery(element).text()==""){
                    sflag = false;
                    var nameStr = jQuery(element).parent().prev().text()+'不能为空';
                    if(jQuery(element).nextAll(".empError").length>0)jQuery(element).nextAll(".empError").remove();
                    jQuery(element).parent().append('<div class="empError" style="color:#f56c6c;padding: 0;line-height: 1;">'+nameStr+'</div>');
                }else{
                    if(jQuery(element).nextAll(".empError").length>0)jQuery(element).nextAll(".empError").remove();
                }
            });
            if(sflag){
                var filter = {};
                filter = {
                    contactsname:jQuery('div[lable="contactsname"]').next().children().eq(0).val(),
                    customerid:jQuery('div[lable="customername"]').next().children().eq(0).attr("cid"),
                    telephone:jQuery('div[lable="telephone"]').next().children().eq(0).val(),
                    cellphone:jQuery('div[lable="cellphone"]').next().children().eq(0).val(),
                    email:jQuery('div[lable="email"]').next().children().eq(0).val(),
                    decision:jQuery('div[lable="decision"]').next().children().eq(0).text(),
                    post:jQuery('div[lable="post"]').next().children().eq(0).val(),
                    sex:jQuery('div[lable="sex"]').next().children().eq(0).text(),
                    detailaddress:jQuery('div[lable="detailaddress"]').next().children().eq(0).val(),
                    nexttime:jQuery('div[lable="nexttime"]').next().children().eq(0).val(),
                    remark:jQuery('div[lable="remark"]').next().children().eq(0).val()
                };
                _self.actions.saveContacts( filter, function (json) {
                    if(json.type=="success"){
                        Showbo.Msg.alert('保存成功!');
                    }
                    setTimeout(function(){
                        jQuery("#notifyEdit").remove();
                        if(jQuery(".mask").length>0){
                            jQuery(".mask").attr("style",'left: 0px; top: 0px; width: 100%; overflow: hidden; position: absolute; z-index: 500000; background-color: rgb(255, 255, 255)');
                            jQuery(".mask").attr("class","");
                        }
                    },200);
                }.bind(_self));
            }

        });
        jQuery(".inline-input[notempty='true']").blur( function () {
            if(jQuery(this).attr("stype")!="datetime" || jQuery(this).attr("stype")!="openSelect"){
                if(jQuery(this).val()=="" && jQuery(this).text()==""){
                    var nameStr = jQuery(this).parent().prev().text()+'不能为空';
                    if(jQuery(this).nextAll(".empError").length>0)jQuery(this).nextAll(".empError").remove();
                    jQuery(this).parent().append('<div class="empError" style="color:#f56c6c;padding: 0;line-height: 1;">'+nameStr+'</div>');
                }else{
                    if(jQuery(this).nextAll(".empError").length>0)jQuery(this).nextAll(".empError").remove();
                }
            }
        });
    },
    loadTimeContainer: function(stime){
        jQuery("#"+stime).ymdateplugin({
            showTimePanel: true
        });
    },
    getItemTemplate: function( lp ){
        _self = this;
        return {
            contactsname:{
                type: "text",
                notEmpty:true,
                text: lp.contactsname,
                value:this.customerData && this.customerData.customername?this.customerData.customername:""
            },
            customername: {
                text: lp.customername,
                notEmpty:true,
                type: "openSelect"
            },
            cellphone: {
                text:lp.cellphone,
                notEmpty:true,
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
                notEmpty:true,
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
                notEmpty:true,
                attr : {id:"nexttime"},
                type: "datetime"
            },
            remark: {
                text:lp.remark,
                type: "textarea"
            }
        }
    }


});

MWF.xApplication.CRM.ContactsEdit.selectForm = new Class({
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
            console.log("you can do something when is ok");

        }.bind(this)
    },
    load: function(){
        //console.log(this.container);
        //this.lp = this.lp.chanceEdit||{};
        this.cssPath = "/x_component_CRM/$CustomerEdit/"+this.options.style+"/opencss.wcss";
        this.path = "/x_component_CRM/$CustomerEdit/";
        this.type ={};
        this._loadCss();
        this.loadData();

    },
    _createTableContent: function () {
        //this.formTableArea.set("html", this.getHtml());
        var templateUrl = this.path+"customerSelect.json";
        var filter = {};
        this.form =  new  MWF.xApplication.CRM.ContactsEdit.SelectCustomer(
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
            debugger
            //this.container.getElement("#customername").setAttribute("value",data.customername);
            this.container.getElement("#customername").setProperty("value",data.customername);
            this.container.getElement("#customername").setProperty("cid",data.id);
            //jQuery("#customername").val(data.customername);
            debugger
            this.app.Customer = data;
            if( this.formMaskNode )this.formMaskNode.destroy();
            if( this.formAreaNode )this.formAreaNode.destroy();
            this.fireEvent("postOk");
        }
    },
    loadData:function(){

    }
});
MWF.xApplication.CRM.ContactsEdit.SelectCustomer = new Class({
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