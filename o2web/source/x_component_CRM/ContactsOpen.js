MWF.require("MWF.widget.O2Identity", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.xApplication.CRM.ContactsOpen = new Class({
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
            this.appArea = jQuery("body").children(":first");
            this.createForm();
            this.loadEvent();
        }.bind(this))
    },
    loadResource: function ( callback ) {
        if(callback)callback();
    },
    createForm:function(){
        that = this;
        var clueName = this.options.openName;
        var buttonHtml = '<div class = "headBottonDiv"><div class="headMoveBottonDiv">转移</div><div class="headEditBottonDiv">编辑</div>' +
            '<div class="headMoreBottonDiv"><span>更多</span><img class="headMoreImg" src="/x_component_CRM/$Clue/default/icons/arrow.png"></div></div>';
        var moreHtml = '<ul class="el-dropdown-menu"><li class="el-dropdown-menu__item">删除</li><div class="popper__arrow"></div></ul>'
        var sectionId = that.getNotifyMax();
        var sjson = this.options.openStyle?this.options.openStyle:this.xxx;
        jQuery(".headMoreImg").notifyMe(
            'right',
            'default',
            clueName,
            buttonHtml,
            moreHtml,
            '',
            sectionId,
            500,
            sjson
        );
        this.createContentHtml(sectionId);
    },
    getNotifyMax : function () {
        var sectionId = 'notify';
        var sectionNum = 0;
        if(!(this.options.openType) || (this.options.openType !="single")) {
            jQuery(".notify").each(function (index, element) {
                var notifyId = jQuery(element).attr("id");
                var notifyNum = 0;
                if (notifyId.indexOf("_") > 0) {
                    notifyNum = parseInt(notifyId.split("_")[1]);
                    if (notifyNum > sectionNum) {
                        sectionNum = notifyNum;
                    }
                } else {
                    if (sectionNum == 0) {
                        sectionNum = 1;
                    }
                }
            });
            if (sectionNum > 0) {
                sectionId = sectionId + '_' + sectionNum;
            }
        }
        return sectionId;
    },
    createContentHtml: function (sectionId) {
        that = this;
        this.sectionArea = jQuery("body")[0].getElement("#"+sectionId);
        this.actions.getContactsInfo(this.options.openId, function (json) {
            var jsonObj = json.data;
            that.options.openCustomerName = jsonObj.customer.customername;
            that.options.openCustomerId = jsonObj.customer.id;
            //var owneruser = (jsonObj.owneruser=="" || (typeof(jsonObj.owneruser)=="undefined"))?"--":(jsonObj.owneruser).split("@")[0];
            var briefDiv = "<div class = 'briefdiv'>"+
                "<div class='div-inline'><div class='div-title'>客户名称</div><div class='div-value'>"+jsonObj.customer.customername+"</div></div>"+
                "<div class='div-inline'><div class='div-title'>职务</div><div class='div-value'>"+jsonObj.post+"</div></div>"+
                "<div class='div-inline'><div class='div-title'>手机</div><div class='div-value'>"+jsonObj.cellphone+"</div></div>"+
                "<div class='div-inline'><div class='div-title'>更新时间</div><div class='div-value'>"+jsonObj.updateTime+"</div></div>"+
                "</div>"

            var tabPanel = "<div class='tabPanel'><div class='hit'>跟进记录</div><div style='width:30px'></div><div>基本信息</div><div style='width:30px'></div>"+
                    "<div>商机</div><div style='width:30px'></div>"+
                    "<div>附件</div><div style='width:30px'></div><div>操作记录</div></div>"

            var tabConent ="<div class='panes'><div class='pane' id='tab-follow' style='display:block;'><p>First tab content</p></div><div></div>"+
                "<div class='pane' id='tab-basicinfo'><p>Secend tab content</p></div><div></div>"+
                "<div class='pane' id='tab-business'><p>business tab content</p></div><div></div>"+
                "<div class='pane' id='tab-att'><p>Third tab content</p></div><div></div>"+
                "<div class='pane' id='tab-options'><p>Four tab content</p></div></div>"

            //jQuery(".notify-content").html( briefDiv+tabPanel+tabConent);
            this.sectionArea.getElement(".notify-content").set("html",briefDiv+tabPanel+tabConent);
            var size = this.sectionArea.getSize();
            this.sectionArea.getElement(".panes").setStyles({"height":(size.y-250)+"px"});
            var indexHtml = '<div ><div class="mix-container"><div class="i-cont"><div class="el-textarea el-input--suffix">'+
                    '<textarea autocomplete="off" placeholder="请输入内容" class="el-textarea__inner" style="resize: none; min-height: 57px; height: 57px;"></textarea></div></div>'+
                    '<div class="vux-flexbox bar-cont vux-flex-row">'+
                    '<div class="vux-flexbox bar-item vux-flex-row"><input type="file" accept="image/*" multiple="multiple" class="bar-input"><img src="/x_component_CRM/$Template/img.png" class="bar-img"><div class="bar-title">图片</div></div>'+
                    '<div class="vux-flexbox bar-item vux-flex-row"><input type="file" id="bar-file" accept="*.*" multiple="multiple" class="bar-input"><img src="/x_component_CRM/$Template/file.png" class="bar-img"><div class="bar-title">附件</div></div></div>'+
                    '</div>';
            var indexSendHtml = '<div  class="vux-flexbox se-section vux-flex-row"><div class="se-name">记录类型</div>'+
                    '<div class="el-dropdown" style="margin-right: 20px;"><div class="vux-flexbox se-select vux-flex-row el-dropdown-selfdefine " ><div class="se-select-name">邮箱</div> <div class="el-icon-arrow-down el-icon--right"><img src="/x_component_CRM/$Clue/default/icons/arrow.png"></div></div> </div>'+
                    '<div class="se-name">下次联系时间</div><div class="el-date-editor se-datepicker el-input el-input--prefix el-input--suffix el-date-editor--datetime"><input type="text"  autocomplete="off" name="" placeholder="选择日期" class="el-input__inner" id="stime" readonly="readonly"><span class="el-input__prefix"><i class="el-input__icon el-icon-time"></i></span><span class="el-input__suffix"><span class="el-input__suffix-inner"><i class="el-input__icon"></i></span></span></div>'+
                    '<button type="button" class="el-button se-send el-button--primary"><span>发布</span></button></div>'+
                    '<ul class="el-dropdown-type" style="display: none;" tid = "recordType"><li class="el-dropdown-menu__item">邮箱</li><li class="el-dropdown-menu__item">电话</li>'+
                    '<li class="el-dropdown-menu__item">地址</li><li class="el-dropdown-menu__item">微信</li><div class="popper__arrow"></div></ul>';


            var indexContentHtml = '<div class="log-cont"><div class="log-inner1"><div class="log-inner2"><div class="log-items">'+
                    '<div class="load"><button  type="button" class="el-button el-button--text"><span>没有更多了</span></button></div></div>'+
                    '<div class="empty-mask" style="display: none;"><div class="empty-content"><img src="/x_component_CRM/$Template/empty.png" class="empty-icon"> <p class="empty-text">没有找到数据</p></div></div></div>'+
                    '<div class="el-loading-mask" style="display: none;"><div class="el-loading-spinner"><svg viewBox="25 25 50 50" class="circular"><circle cx="50" cy="50" r="20" fill="none" class="path"></circle></svg></div></div>'+
                    '</div></div>';

            //jQuery("#tab-follow").html(indexHtml+indexSendHtml+indexContentHtml);
            that.sectionArea.getElement("#tab-follow").set("html",indexHtml+indexSendHtml+indexContentHtml);
            that.loadTimeContainer('stime');
            that.loadRecord();
        }.bind(this));
    },
    createTypeHtml: function () {
        var createType = jQuery(this.sectionArea).find(".hit").text();
        if(createType == "基本信息") {
            this.getContactsInfo();
        }
        if(createType == "商机") {
            this.loadBusiness();
        }
        if(createType == "附件") {
            this.loadAttachment();
        }
        if(createType == "操作记录") {
            this.loadOptions();
        }
    },
    loadTimeContainer: function(stime){
        jQuery("#"+stime).ymdateplugin({
            showTimePanel: true
        });
    },
    getContactsInfo: function () {
        _self = this;
        this.actions.getContactsInfo(this.options.openId, function (json) {
            var jsonObj = json.data;
            var section_header = '<div class="section-header"><div class="section-mark" style="border-left-color: rgb(70, 205, 207);"></div> '+
                '<div data-v-ec8f8850="" class="section-title">基本信息</div></div>'

            var itemTemplateObject = _self.lp;
            var section_conent = '<div class="section-conent">';
            for ( i in itemTemplateObject){
                var valueStr = "";
                if(i=="customername"){
                    valueStr = ((typeof(jsonObj.customer)=="undefined")?"" : jsonObj.customer.customername);
                }else{
                    valueStr = ((typeof(jsonObj[i])=="undefined")?"" : jsonObj[i]);
                }
                section_conent = section_conent+'<div class="conent-inline"><div class="conent-title">'+itemTemplateObject[i].text+'</div>' +
                    '<div class="conent-value">'+valueStr+'</div></div>';

            }
            section_conent = section_conent + '</div>';
            jQuery(_self.sectionArea).find("#tab-basicinfo").html(section_header+section_conent);
            //this.contentHtml =  briefDiv+tabPanel+tabConent;
        }.bind(this));
    },
    loadAttachment: function() {
        that = this;
        var attHtml = '<div class="rc-cont">';
        var attButton = '<div class="vux-flexbox rc-head vux-flex-row" style="flex-direction: row-reverse;"><div class="rc-head-item"><span>上传附件</span></div> <input  type="file" id="afile" accept="*/*" multiple="multiple" class="rc-head-file"></div>';
        var attHeader = '<div  class="el-table el-table--fit el-table--striped el-table--enable-row-hover" header-align="center" style="width: 100%; border: 1px solid rgb(230, 230, 230); height: 500px;" align="center">'+
                '<div class="el-table__header-wrapper"><table class="el-table__header" style="width: 100%;" cellspacing="0" cellpadding="0" border="0"><thead class="has-gutter">'+
                '<tr class=""><th colspan="1" rowspan="1" class="el-table_3_column_27     is-leaf" style="width: 40%;height:40px;"><div class="cell">附件名称</div></th>'+
                '<th colspan="1" rowspan="1" class="el-table_3_column_28     is-leaf" style="width: 10%;"><div class="cell">附件大小</div></th>'+
                '<th colspan="1" rowspan="1" class="el-table_3_column_29     is-leaf" style="width: 20%;"><div class="cell">上传人</div></th>'+
                '<th colspan="1" rowspan="1" class="el-table_3_column_30     is-leaf" style="width: 20%;"><div class="cell">上传时间</div></th>'+
                '<th colspan="1" rowspan="1" class="el-table_3_column_26     is-leaf" style="width: 10%;"><div class="cell">操作</div></th></tr></thead></table></div>';

        var attBody = '<div class="el-table__body-wrapper is-scrolling-none" style="height: 450px;">'+
                '<table class="el-table__body" style="width: 100%;" cellspacing="0" cellpadding="0" border="0"><tbody></tbody></table>'+
                '<div class="el-table__empty-block" style="width: 100%;"><span class="el-table__empty-text">暂无数据</span></div></div></div>';
        attHtml = attHtml+attButton+attHeader+attBody+'</div>';
        jQuery(that.sectionArea).find("#tab-att").html(attHtml);

        jQuery(that.sectionArea).find('#afile').change(function(event) {
            var objFile = jQuery(that.sectionArea).find('#afile')[0].files;
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
                    that.actions.updateAttachment("att",that.options.openId, "contacts", formdata,file, function (json) {
                        if(json.type=="success"){
                         //Showbo.Msg.alert('附件上传成功!');
                            that.getAttachment();
                         }
                    }.bind(that));
                }
            );
        });
        that.getAttachment();
    },
    getAttachment: function(){
        that = this;
        this.actions.getAttachment(this.options.openId, function (json) {
            debugger
            if(json.type=="success"){
                var attDatas = json.data;
                var tbodyHtml = "";
                for ( i in attDatas){
                    if(i<attDatas.length){
                        var attData = attDatas[i];
                        var fsize = attData.length/1024;
                        var lastUpdatePerson = attData.lastUpdatePerson;
                        lastUpdatePerson = lastUpdatePerson.split("@")[0];
                        tbodyHtml = tbodyHtml+'<tr><td style="width: 40%;height:40px;" class="aname" aid="'+attData.id+'" wcrm="'+attData.wcrm+'">'+attData.name+'</td><td style="width: 10%;">'+that.toDecimal(fsize)+'kb</td>'+
                                '<td style="width: 20%;">'+lastUpdatePerson+'</td><td style="width: 20%;">'+attData.updateTime+'</td><td class="attOption">删除</td></tr>';
                    }
                }
                if(tbodyHtml!=""){
                    jQuery(that.sectionArea).find(".el-table__body").children().html(tbodyHtml);
                    jQuery(that.sectionArea).find(".el-table__empty-block").hide();

                    jQuery(that.sectionArea).find(".attOption").click(function(){
                        self = this;
                        Showbo.Msg.confirm('提示','确定删除该附件吗?',function(){
                            var aid = jQuery(self).parent().children(":first").attr("aid");
                            if(aid && aid !=""){
                                that.actions.delAttachment(aid, function (ajson) {
                                    if(ajson.type=="success"){
                                        that.getAttachment();
                                    }
                                }.bind(self));
                            }
                        },function(){
                        });
                    });
                    jQuery(that.sectionArea).find(".aname").click(function(){
                        var attUrl = 'http://172.16.92.55:20020/x_wcrm_assemble_control/jaxrs/attachment/download/'+jQuery(this).attr("aid")+'/work/'+jQuery(this).attr("wcrm")
                        window.open(attUrl);
                        /*that.actions.downloadAttachment(jQuery(this).attr("aid"),jQuery(this).attr("wcrm"), function (wjson) {

                        }.bind(that));*/
                    });
                }
            }
        }.bind(that));
    },
    loadBusiness: function() {
        that = this;
        var attHtml = '<div class="rc-cont">';
        var attButton = '<div class="vux-flexbox rc-head vux-flex-row" style="flex-direction: row-reverse;"><div class="rc-head-item" style="width: 75px;"><span>新建商机</span></div></div>';
        var attHeader = '<div  class="el-table el-table--fit el-table--striped el-table--enable-row-hover" header-align="center" style="width: 100%; border: 1px solid rgb(230, 230, 230); height: 500px;" align="center">'+
            '<div class="el-table__header-wrapper"><table class="el-table__header" style="width: 100%;" cellspacing="0" cellpadding="0" border="0"><thead class="has-gutter">'+
            '<tr class=""><th colspan="1" rowspan="1" class="el-table_3_column_27     is-leaf" style="width: 30%;height:40px;"><div class="cell">商机名称</div></th>'+
            '<th colspan="1" rowspan="1" class="el-table_3_column_29     is-leaf" style="width: 20%;"><div class="cell">商品金额</div></th>'+
            '<th colspan="1" rowspan="1" class="el-table_3_column_29     is-leaf" style="width: 30%;"><div class="cell">客户名称</div></th>'+
            '<th colspan="1" rowspan="1" class="el-table_3_column_29     is-leaf" style="width: 20%;"><div class="cell">商机状态组</div></th>'+
            '<th colspan="1" rowspan="1" class="el-table_3_column_26     is-leaf" style="width: 20%;"><div class="cell">状态</div></th></tr></thead></table></div>';

        var attBody = '<div class="el-table__body-wrapper is-scrolling-none" style="height: 450px;">'+
            '<table class="el-table__body" style="width: 100%;" cellspacing="0" cellpadding="0" border="0"><tbody></tbody></table>'+
            '<div class="el-table__empty-block" style="width: 100%;"><span class="el-table__empty-text">暂无数据</span></div></div></div>';
        attHtml = attHtml+attButton+attHeader+attBody+'</div>';
        jQuery(that.sectionArea).find("#tab-business").html(attHtml);

        this.actions.listOpportunityByContactsId(this.options.openId, function (json) {
            if(json.type=="success"){
                var attDatas = json.data;
                debugger
                var tbodyHtml = "";
                for ( i in attDatas){
                    if(i<attDatas.length){
                        var attData = attDatas[i];
                        tbodyHtml = tbodyHtml+'<tr><td style="width: 30%;height:40px;" class="aname" aid="'+attData.id+'">'+attData.opportunityname+
                            '</td><td style="width: 20%;">'+attData.money+'</td><td style="width: 30%;">'+attData.customername+
                            '</td><td style="width: 20%;">'+attData.opportunitytypename+'</td><td style="width: 20%;">'+attData.opportunitystatusname+'</td></tr>';
                    }
                }
                if(tbodyHtml!=""){
                    jQuery(".el-table__body").children().html(tbodyHtml);
                    jQuery(".el-table__empty-block").hide();
                }
            }
        }.bind(that));
        jQuery(that.sectionArea).find("#tab-business").find(".rc-head-item").click(function(){
            that.businessCreate();
        });
        jQuery(that.sectionArea).find("#tab-business").find(".aname").click(function(){
            //that.contactsEdit(jQuery(this).attr("aid"));
        });
    },
    loadOptions: function() {
        that = this;
        var optionsHtml = '<div class="rc-cont">'+
            '<div class="empty-mask" style="display:none;height:370px;"><div class="empty-content" style="margin-top:0px;"><img src="/x_component_CRM/$Template/empty.png" class="empty-icon"> <p class="empty-text">没有找到数据</p></div></div></div>'
        jQuery(that.sectionArea).find("#tab-options").html(optionsHtml);

        this.actions.getOptionsRecord(this.options.openId, function (json) {
            if(json.type=="success"){
                var recordDatas = json.data;
                jQuery(that.sectionArea).find(".rc-cont").find(".vux-flexbox").remove();
                var logItemHtml = "";
                debugger
                for ( i in recordDatas){
                    if(i<recordDatas.length){
                        var recordData = recordDatas[i];
                        debugger
                        var personImg = 'http://172.16.92.55/x_component_CRM/$Template/portrait.png';
                        var updatetime = recordData.updateTime;
                        logItemHtml = logItemHtml+'<div class="vux-flexbox ha-cont vux-flex-row" style="justify-content: flex-start; align-items: stretch;">'+
                            '<div class="ha-week">'+recordData.DateCN+'</div>'+
                            '<div class="ha-circle"></div> '+
                            '<div class="ha-time">'+updatetime.substring(11,16)+'</div>'+
                            '<div class="div-photo ha-img xs-photo-parent--relative" style="background-image: url(&quot;'+personImg+'&quot;);" lazy="error"><div class="photo-wrap"></div></div>' +
                            '<div class="ha-name">'+recordData.person.name+'</div>'+
                            '<div class="ha-content">'+recordData.content+'</div>'+
                            '<div class="ha-line"></div></div>'
                    }
                }
                if(logItemHtml!=""){
                    jQuery(that.sectionArea).find(".rc-cont").append(logItemHtml);
                }
                if(recordDatas.length<1){
                    jQuery(that.sectionArea).find(".empty-mask").show();
                }
            }
        }.bind(this));
    },
    transfer: function () {
        //转移负责人
        _self = this;
        var contentHtml = '<div  class="vux-flexbox handle-item vux-flex-row" style="align-items: stretch;padding: 30px 20px 80px 20px;line-height:30px;"><div class="handle-item-name" style="margin-top: 8px;width:100px;">变更负责人为：</div><div class="el-select handle-item-content" style="margin-top: 8px;">'+
            '<div class="se-select-name" id="selectName" style="display: inline-block;">+点击选择</div><div id="selectId" style="display:none;"></div></div></div>'

        Showbo.Msg.confirm('联系人转移',contentHtml,function(){
            if(jQuery("#selectId").text() == ""){
                Showbo.Msg.alert('请选择负责人!');
            }else{
                var filter = {};
                filter = {
                    distinguishName: jQuery('#selectId').text(),
                    transferType:"1"
                }
                debugger
                _self.actions.contactsTransfer(_self.options.openId, filter, function (json) {
                    if(json.type=="success"){
                        Showbo.Msg.alert('操作成功!');
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

        },function(){
        });
        jQuery(".ct").find(".se-select-name").click(function(){
            _self.selectPerson(jQuery(_self.appArea)[0],"selectName","selectId",0);
        });

    },
    contactsEdit: function () {
        //编辑联系人
        _self = this;
        this.actions.getContactsInfo(this.options.openId, function (json) {
            jQuery(_self.appArea).next().attr("style","");
            jQuery(_self.appArea).next().attr("class","mask");
            var jsonObj = json.data;
            var section_header = '<div class="section-header"><div class="section-mark" style="border-left-color: rgb(70, 205, 207);"></div> '+
                '<div data-v-ec8f8850="" class="section-title">基本信息</div></div>'

            var itemTemplateObject = _self.lp;
            debugger
            var section_conent = '<div class="section-conent">';
            for ( i in itemTemplateObject){
                var stype = itemTemplateObject[i].type;
                var notEmpty = itemTemplateObject[i].notEmpty?itemTemplateObject[i].notEmpty:"false";
                var innerHtml = '<input type="text" class="inline-input" name="'+i+'" id="'+i+'" notEmpty="'+notEmpty+'" stype="'+stype+'" value="'+((typeof(jsonObj[i])=="undefined")?"" : jsonObj[i])+'">';
                if(stype=="textarea"){
                    innerHtml =  '<textarea rows="6" class="el-textarea__inner"  id="'+i+'" notEmpty="'+notEmpty+'" stype="'+stype+'"  style="resize: none; min-height: 30.6px;">'+jsonObj[i]+'</textarea>';
                }
                if(stype=="select"){
                    innerHtml = '<div class="inline-input" style="display: inline-block;cursor:pointer;"  id="'+i+'" notEmpty="'+notEmpty+'" stype="'+stype+'" >'+((typeof(jsonObj[i])=="undefined")?"" : jsonObj[i])+'</div><div class="el-icon-arrow-down el-icon--right" style="margin-left: -20px; display: inline-block;"><img src="/x_component_CRM/$Clue/default/icons/arrow.png"></div>';
                }
                if(i=="customername"){
                    var tempname = ((typeof(jsonObj.customer)=="undefined")?"" : jsonObj.customer.customername);
                    var tempid = ((typeof(jsonObj.customer)=="undefined")?"" : jsonObj.customer.id);
                    innerHtml = '<input type="text" class="inline-input"  readonly="true" style="background-color: #e2ebf9;cursor:pointer;" name="'+i+'" id="'+i+'" notEmpty="'+notEmpty+'" stype="'+stype+'" cid = "'+tempid+'" value="'+tempname+'">';
                }
                if(stype!="hide"){
                    section_conent = section_conent+'<div class="conent-inline"><div class="conent-title" lable="'+i+'">'+itemTemplateObject[i].text+'</div>' +
                        '<div class="conent-value">'+innerHtml+'</div></div>';
                }

            }
            section_conent = section_conent + '</div>';
            var section_button = '<div class="section_button"><div><button class="el-button handle-button el-button-cancle"><span>取消</span></button>'+
                    '<button class="el-button handle-button el-button-primary"><span>保存</span></button></div></div>';
            var htmlstr = section_header+section_conent+section_button;

            jQuery(".headMoreImg").notifyMe(
                'left',
                'default',
                '编辑联系人',
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
                        debugger
                        //jQuery("#customername").click(function(){
                        jQuery(element).click(function(){
                            _sself = _self.lp;
                            _sself.cancel = "关闭";
                            _sself.ok = "确定";
                            _self.selectCustomer =  new  MWF.xApplication.CRM.ContactsOpen.selectForm (null,{},null, {
                                    app: _self,
                                    container: jQuery("#notifyEdit")[0],
                                    lp: _sself,
                                    actions: _self.actions,
                                    css: {},
                                }
                            );
                            debugger
                            _self.selectCustomer.create();
                        });
                    }
                    if(stype=="select"){
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
                        pinyin:((typeof(jsonObj["pinyin"])=="undefined")?"" : jsonObj["pinyin"]),
                        pinyinInitial:((typeof(jsonObj["pinyinInitial"])=="undefined")?"" : jsonObj["pinyinInitial"]),
                        telephone:jQuery('div[lable="telephone"]').next().children().eq(0).val(),
                        cellphone:jQuery('div[lable="cellphone"]').next().children().eq(0).val(),
                        email:jQuery('div[lable="email"]').next().children().eq(0).val(),
                        decision:jQuery('div[lable="decision"]').next().children().eq(0).text(),
                        post:jQuery('div[lable="post"]').next().children().eq(0).val(),
                        sex:jQuery('div[lable="sex"]').next().children().eq(0).text(),
                        detailaddress:jQuery('div[lable="detailaddress"]').next().children().eq(0).val(),
                        nexttime:jQuery('div[lable="nexttime"]').next().children().eq(0).val(),
                        remark:jQuery('div[lable="remark"]').next().children().eq(0).val(),

                        readerUserIds:((typeof(jsonObj["readerUserIds"])=="undefined")?"" : jsonObj["readerUserIds"]),
                        writerUserIds:((typeof(jsonObj["writerUserIds"])=="undefined")?"" : jsonObj["writerUserIds"]),
                        createuser:((typeof(jsonObj["createuser"])=="undefined")?"" : jsonObj["createuser"]),
                        owneruser:((typeof(jsonObj["owneruser"])=="undefined")?"" : jsonObj["owneruser"])
                    };
                    _self.actions.updateContacts(_self.options.openId, filter, function (json) {
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
        }.bind(this));
    },
    contactsCreate: function () {
        //编辑联系人
        _self = this;
        var htmlstr = "";

        jQuery(_self.appArea).next().attr("style","");
        jQuery(_self.appArea).next().attr("class","mask");
        var section_header = '<div class="section-header"><div class="section-mark" style="border-left-color: rgb(70, 205, 207);"></div> '+
            '<div data-v-ec8f8850="" class="section-title">基本信息</div></div>';
        var itemTemplateObject = _self.app.lp.contact.contactEdit;
        var section_conent = '<div class="section-conent">';
        for ( i in itemTemplateObject){
            var stype = itemTemplateObject[i].type;
            var innerHtml = '<input type="text" class="inline-input" name="'+i+'" id="'+i+'" stype="'+stype+'">';
            if(stype=="textarea"){
                innerHtml =  '<textarea rows="6" class="el-textarea__inner"  id="'+i+'" stype="'+stype+'"  style="resize: none; min-height: 30.6px;"></textarea>';
            }
            if(stype=="select"){
                innerHtml = '<div class="inline-input" style="display: inline-block;cursor:pointer;"  id="'+i+'" stype="'+stype+'" ></div><div class="el-icon-arrow-down el-icon--right" style="margin-left: -20px; display: inline-block;"><img src="/x_component_CRM/$Clue/default/icons/arrow.png"></div>';
            }
            if(stype=="readonly"){
                innerHtml =  '<input type="text" class="inline-input" disabled="disabled" style="background-color:#e6e6e6;" name="'+i+'" id="'+i+'" value="'+jQuery("#notify").find("h3").text()+'" cid="'+_self.options.openId+'" stype="'+stype+'">';
            }
            section_conent = section_conent+'<div class="conent-inline"><div class="conent-title" lable="'+i+'">'+itemTemplateObject[i].text+'</div>' +
                '<div class="conent-value">'+innerHtml+'</div></div>';
        }
        section_conent = section_conent + '</div>';
        var section_button = '<div class="section_button"><div><button class="el-button handle-button el-button-cancle"><span>取消</span></button>'+
            '<button class="el-button handle-button el-button-primary"><span>保存</span></button></div></div>';
        htmlstr = section_header+section_conent+section_button;

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
                if(stype=="select"){
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

                var filter = {};
                filter = {
                    contactsname:jQuery('div[lable="contactsname"]').next().children().eq(0).val(),
                    customerid:jQuery('#customername').attr("cid"),
                    telephone:jQuery('div[lable="telephone"]').next().children().eq(0).val(),
                    cellphone:jQuery('div[lable="cellphone"]').next().children().eq(0).val(),
                    email:jQuery('div[lable="email"]').next().children().eq(0).val(),
                    decision:jQuery('div[lable="decision"]').next().children().eq(0).text(),
                    sex:jQuery('div[lable="sex"]').next().children().eq(0).text(),
                    post:jQuery('div[lable="post"]').next().children().eq(0).val(),
                    detailaddress:jQuery('div[lable="detailaddress"]').next().children().eq(0).val(),
                    nexttime:jQuery('div[lable="nexttime"]').next().children().eq(0).val(),
                    remark:jQuery('div[lable="remark"]').next().children().eq(0).val()
                };
            debugger
            _self.actions.saveContacts(filter, function (json) {
                debugger
                if(json.type=="success"){
                    Showbo.Msg.alert('保存成功!');
                    _self.loadContacts();
                }
                setTimeout(function(){
                    jQuery("#notifyEdit").remove();
                    if(jQuery(".mask").length>0){
                        jQuery(".mask").attr("style",'left: 0px; top: 0px; width: 100%; overflow: hidden; position: absolute; z-index: 500000; background-color: rgb(255, 255, 255)');
                        jQuery(".mask").attr("class","");
                    }
                },200);
            }.bind(_self));

        });
    },
    editTeam: function(nameList){
        _self = this;
        var contentHtml = '<div  class="vux-flexbox handle-item vux-flex-row" style="align-items: stretch;padding: 10px 20px 50px 20px;line-height:30px;font-size:16px;">'+
            '<div class="handle-item-name" style="margin-top: 8px;width:60px;">权限：</div><div  role="radiogroup" class="el-radio-group">'+
            '<label  role="radio" tabindex="0" class="el-radio"><span class="el-radio__input"><span class="el-radio__inner"></span><input type="radio" checked aria-hidden="true" tabindex="-1" class="el-radio__original" value="1"></span><span class="el-radio__label">只读</span></label>'+
            '<label role="radio" tabindex="-1" class="el-radio"><span class="el-radio__input"><span class="el-radio__inner"></span><input type="radio" aria-hidden="true" tabindex="-1" class="el-radio__original" value="2"></span><span class="el-radio__label">读写</span></label></div></div></div>'



        Showbo.Msg.confirm('编辑权限',contentHtml,function(){
                var filter = {};
                filter = {
                    distinguishName: nameList,
                }
                var val=jQuery(".ct").find('input:radio:checked').val();
                if(val==1){
                    _self.actions.setTeamReader(_self.options.openId, filter, function (json) {
                           if(json.type=="success"){
                               Showbo.Msg.alert('操作成功!');
                               _self.loadTeam();
                           }
                           setTimeout(function(){
                               jQuery("#notifyEdit").remove();
                               if(jQuery(".mask").length>0){
                                   jQuery(".mask").attr("style",'left: 0px; top: 0px; width: 100%; overflow: hidden; position: absolute; z-index: 500000; background-color: rgb(255, 255, 255)');
                                   jQuery(".mask").attr("class","");
                               }
                           },200);
                       }.bind(_self));
                }else{
                    _self.actions.setTeamWriter(_self.options.openId, filter, function (json) {
                        if(json.type=="success"){
                            Showbo.Msg.alert('操作成功!');
                            _self.loadTeam();
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

        },function(){
        });
        jQuery(".el-radio").click(function(){
            var robj = jQuery(this).find("input");
            jQuery(this).siblings().find("input[type=radio]").removeAttr('checked');
        });
    },
    addTeam: function () {
        //添加team成员
        _self = this;
        var contentHtml = '<div  class="vux-flexbox handle-item vux-flex-row" style="align-items: stretch;padding: 30px 20px 0px 20px;line-height:30px;">'+
            '<div class="handle-item-name" style="margin-top: 8px;width:100px;">选择团队成员：</div><div class="el-select handle-item-content" style="margin-top: 8px;"><div class="se-select-name" id="selectName" style="display: inline-block;">+点击选择</div><div id="selectId" style="display:none;"></div></div></div>'+
            '<div  class="vux-flexbox handle-item vux-flex-row" style="align-items: stretch;padding: 10px 20px 0px 20px;line-height:30px;">'+
            '<div class="handle-item-name" style="margin-top: 8px;width:100px;">权限：</div><div  role="radiogroup" class="el-radio-group">'+
            '<label  role="radio" aria-checked="true" tabindex="0" class="el-radio is-checked"><span class="el-radio__input is-checked"><span class="el-radio__inner"></span><input type="radio" aria-hidden="true" tabindex="-1" class="el-radio__original" value="1"></span><span class="el-radio__label">只读</span></label>'+
            '<label role="radio" tabindex="-1" class="el-radio"><span class="el-radio__input"><span class="el-radio__inner"></span><input type="radio" aria-hidden="true" tabindex="-1" class="el-radio__original" value="2"></span><span class="el-radio__label">读写</span></label></div></div></div>'+
            '<div  class="vux-flexbox handle-item vux-flex-row" style="align-items: stretch;padding: 10px 20px 80px 20px;line-height:30px;">'+
            '<div class="handle-item-name" style="margin-top: 8px;width:100px;">同时添加至：</div><div role="group" aria-label="checkbox-group" class="el-checkbox-group">'+
            '<label role="checkbox" class="el-checkbox"><span aria-checked="mixed" class="el-checkbox__input"><span class="el-checkbox__inner"></span><input type="checkbox" aria-hidden="true" class="el-checkbox__original" value="opportunity"></span><span class="el-checkbox__label">商机</span></label>'+
            '</div></div></div>'


        Showbo.Msg.confirm('添加团队成员',contentHtml,function(){
            //_self.confirmCustomerDealstatus();
            var readOrWrite = "";
            var checkFlag = true;
            var relationTypeList = [];
            jQuery(".ct").find(".is-checked").each(function(i,obj){
                var typeStr = jQuery(obj).children(".el-radio__label").text();

                if(typeStr=="只读"){
                    readOrWrite = "read"
                }
                if(typeStr=="读写"){
                    readOrWrite = "write"
                }
            })
            jQuery(".ct").find(".el-checkbox__original").each(function(i,obj){
                if(jQuery(obj).is(':checked')){
                    relationTypeList.push(jQuery(obj).val());
                }
            })

            if(jQuery("#selectId").text() == ""){
                checkFlag = false;
                Showbo.Msg.alert('选择团队成员!');
            }
            if(readOrWrite == ""){
                checkFlag = false;
                Showbo.Msg.alert('请权限!');
            }
            if(checkFlag){
                var filter = {};
                filter = {
                    personList: (jQuery('#selectId').text()).split(","),
                    relationTypeList: relationTypeList
                }
                _self.actions.addRelevantPerson(_self.options.openId,readOrWrite, filter, function (json) {
                    if(json.type=="success"){
                        Showbo.Msg.alert('操作成功!');
                        _self.loadTeam();
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
        },function(){
        });
        jQuery(".ct").find(".se-select-name").click(function(){
            _self.selectPerson(jQuery(_self.appArea)[0],"selectName","selectId",0);
        });
        jQuery(".ct").find(".el-radio").click(function(){
            jQuery(this).siblings().attr("class","el-radio");
            jQuery(this).siblings().find("input:radio").attr("checked",false)

            jQuery(this).attr("class","el-radio is-checked");
            jQuery(this).find("input:radio").attr("checked",true);
            var typeName = jQuery(this).find(".el-radio__label").text();


        });

    },
    deleteTeam: function(){
        _self = this;
        Showbo.Msg.confirm('提示','此操作将移除这些团队成员是否继续?',function(){
            var nameList = [];
            jQuery(".el-table__body").find("input:checked").each(function(index,element){
                var nobj = jQuery(element).parent().parent().next();
                nameList.push(jQuery(nobj).attr("aid"));
            });
            if(nameList.length<1){
                Showbo.Msg.alert('请选择要删除的相关团队成员!');
            }else{
                var filter = {};
                filter = {
                    distinguishName: nameList
                }
                _self.actions.removeTeamMember(_self.options.openId,filter, function (json) {
                    if(json.type=="success"){
                        Showbo.Msg.alert('操作成功!');
                        _self.loadTeam();
                    }
                }.bind(self));
            }
            setTimeout(function(){
                jQuery("#notifyEdit").remove();
                if(jQuery(".mask").length>0){
                    jQuery(".mask").attr("style",'left: 0px; top: 0px; width: 100%; overflow: hidden; position: absolute; z-index: 500000; background-color: rgb(255, 255, 255)');
                    jQuery(".mask").attr("class","");
                }
            },200);
        },function(){
        });
    },
    businessCreate: function(){
        that = this;
        MWF.xDesktop.requireApp("CRM", "ChanceEdit", function(){
            //console.log("this.lp",this.lp);
            var editForm = new MWF.xApplication.CRM.ChanceEdit(null,{},null, {
                app: this.app,
                container : this.app.content,
                lp : this.app.lp.chance,
                actions : this.actions,
                css : {},
                customer :{"id":that.options.openCustomerId},
                callback : function () {
                    //editForm.create();
                }
            });
            editForm.create();
        }.bind(this));
        that.app.content.getElement("#customer").setProperty("value",that.options.openCustomerName);
    },
    loadEvent: function(){
        that = this;
        jQuery(that.sectionArea).find('.tabPanel div').click(function(){
            jQuery(this).addClass('hit').siblings().removeClass('hit');
            jQuery(that.sectionArea).find('.panes>div:eq('+jQuery(this).index()+')').show().siblings().hide();
            that.createTypeHtml();
        });
        jQuery(that.sectionArea).find('.headMoreBottonDiv').click(function(){
            jQuery(that.sectionArea).find(".el-dropdown-menu").toggle(100);
        });
        jQuery(that.sectionArea).find('.el-dropdown-menu__item').click(function(){

            if(jQuery(this).text()=="删除"){

            }
            //---for记录类型
            if(jQuery(this).parent().attr("tid")=="recordType"){
                jQuery(that.sectionArea).find(".se-select-name").text(jQuery(this).text());
                jQuery(this).parent().toggle(100);
            }

        });
        jQuery(that.sectionArea).find('.headMoveBottonDiv').click(function(){
            that.transfer();
        });
        jQuery(that.sectionArea).find('.headEditBottonDiv').click(function(){
            that.contactsEdit();
        });
        jQuery(that.sectionArea).find('#bar-file').change(function(event) {
            var files = event.target.files;
            debugger
            jQuery(that.sectionArea).find('.fileList').empty();
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
                jQuery(that.sectionArea).find('.mix-container').append(fileListHtml);
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
        jQuery(that.sectionArea).find('.se-send').click(function(){
            that.sendRecord();
        });
        jQuery(that.sectionArea).find('.el-dropdown-selfdefine').click(function(){
            jQuery("[tid='recordType']").toggle(100);
        });

    },
    sendRecord: function () {
        that = this;
        //var objFile = document.getElementById("bar-file");


        var filters = {};
        filters = {
            types:"contacts",
            typesid:that.options.openId,
            content:jQuery(that.sectionArea).find('.el-textarea__inner').val(),
            category:jQuery(that.sectionArea).find('.se-select-name').text(),
            nexttime:jQuery(that.sectionArea).find('.hasDatepicker').val(),
            businessids:"",
            contactsids:"",
            createuser:""
        };
        that.actions.createRecord(filters,function(json){
            if(json.type=="success"){
                Showbo.Msg.alert('跟进记录发布成功!');
                var objFile = jQuery(that.sectionArea).find('#bar-file')[0].files;
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
                        that.actions.updateAttachment("att",json.data.id, "record", formdata,file, function (attjson) {
                            /*if(json.type=="success"){
                                Showbo.Msg.alert('附件上传成功!');
                            }*/
                        }.bind(that));
                    }
                );
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
                jQuery(that.sectionArea).find(".fl-c").remove();
                var logItemHtml = "";
                debugger
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

                        logItemHtml = logItemHtml+'<div class="fl-c"><div class="vux-flexbox fl-h vux-flex-row">'+
                            '<div class="div-photo fl-h-img"  style="background-image: url(&quot;'+personImg+'&quot;);" lazy="loaded"></div> '+
                            '<div class="fl-h-b"><div class="fl-h-name">'+recordData.person.name+'</div><div class="fl-h-time">'+recordData.updateTime+'</div></div></div>'+
                            '<div class="fl-b"><div class="fl-b-content">'+recordData.content+'</div>'+attHtml+
                            '<div class="follow"><span class="follow-info">'+recordData.category+'</span></div></div>'+
                            '<div  class="full-container" style="display: none;"></div></div>'
                    }
                }
                if(logItemHtml!=""){
                    jQuery(that.sectionArea).find(".load").before(logItemHtml);
                }
                if(recordDatas.length<1){
                    jQuery(that.sectionArea).find(".load").hide();
                    jQuery(that.sectionArea).find(".empty-mask").show();
                }
                jQuery(".aname").click(function(){
                    var attUrl = 'http://172.16.92.55:20020/x_wcrm_assemble_control/jaxrs/attachment/download/'+jQuery(this).attr("aid")+'/work/'+jQuery(this).attr("wcrm")
                    window.open(attUrl);
                });
            }
        }.bind(this));
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

MWF.xApplication.CRM.ContactsOpen.selectForm = new Class({
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
        this.form =  new  MWF.xApplication.CRM.ContactsOpen.SelectCustomer(
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
MWF.xApplication.CRM.ContactsOpen.SelectCustomer = new Class({
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