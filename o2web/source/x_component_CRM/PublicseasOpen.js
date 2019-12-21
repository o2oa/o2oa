MWF.xApplication.CRM.AddressExplorer={};
MWF.require("MWF.widget.O2Identity", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.xApplication.CRM.PublicseasOpen = new Class({
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
        this.lp = this.app.lp.publicseas.customerEdit;
        this.path = "/x_component_CRM/$ClueEdit/";
        this.cssPath = this.path + this.options.style + "/css.wcss";
        this._loadCss();

        this.options.title = this.lp.title;

        this.data = data || {};
        this.actions = actions;
        this.configData = [];
        debugger
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
        /*this.createContentHtml(this.options.clueId);*/
        var buttonHtml = '<div class = "headBottonDiv">' +
            '<div class="headMoreBottonDiv"><span>更多</span><img class="headMoreImg" src="/x_component_CRM/$Clue/default/icons/arrow.png"></div></div>';
        var moreHtml = '<ul class="el-dropdown-menu"><li class="el-dropdown-menu__item">分配</li>'+
                '<li class="el-dropdown-menu__item">领取</li>'+
            '<li class="el-dropdown-menu__item">删除</li><div class="popper__arrow"></div></ul>';

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
        that.createContentHtml(sectionId);

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
        debugger
        this.actions.getCustomerInfo(this.options.openId, function (json) {
            var jsonObj = json.data;
            var owneruser = (jsonObj.owneruser=="" || (typeof(jsonObj.owneruser)=="undefined"))?"--":(jsonObj.owneruser).split("@")[0];
            var briefDiv = "<div class = 'briefdiv'>"+
                "<div class='div-inline'><div class='div-title'>客户级别</div><div class='div-value'>"+jsonObj.level+"</div></div>"+
                "<div class='div-inline'><div class='div-title'>成交状态</div><div class='div-value'>"+jsonObj.dealstatus+"</div></div>"+
                "<div class='div-inline'><div class='div-title'>负责人</div><div class='div-value'>"+owneruser+"</div></div>"+
                "<div class='div-inline'><div class='div-title'>更新时间</div><div class='div-value'>"+jsonObj.updateTime+"</div></div>"+
                "</div>"

            var tabPanel = "<div class='tabPanel'><div class='hit'>跟进记录</div><div style='width:30px'></div><div>基本信息</div><div style='width:30px'></div>"+
                    "<div>联系人</div><div style='width:30px'></div><div>相关团队</div><div style='width:30px'></div><div>商机</div><div style='width:30px'></div>"+
                    "<div>附件</div><div style='width:30px'></div><div>操作记录</div></div>"

            var tabConent ="<div class='panes'><div class='pane' id='tab-follow' style='display:block;'><p>First tab content</p></div><div></div>"+
                "<div class='pane' id='tab-basicinfo'><p>Secend tab content</p></div><div></div>"+
                "<div class='pane' id='tab-contacts'><p>contacts tab content</p></div><div></div>"+
                "<div class='pane' id='tab-team'><p>team tab content</p></div><div></div>"+
                "<div class='pane' id='tab-business'><p>business tab content</p></div><div></div>"+
                "<div class='pane' id='tab-att'><p>Third tab content</p></div><div></div>"+
                "<div class='pane' id='tab-options'><p>Four tab content</p></div></div>"

            //jQuery("#"+sectionId).find(".notify-content").html( briefDiv+tabPanel+tabConent);
            this.sectionArea.getElement(".notify-content").set("html",briefDiv+tabPanel+tabConent);
            var size = this.sectionArea.getSize();
            this.sectionArea.getElement(".panes").setStyles({"height":(size.y-250)+"px"});
            var indexHtml = '';
            var indexSendHtml = '';


            var indexContentHtml = '<div class="log-cont"><div class="log-inner1"><div class="log-inner2"><div class="log-items">'+
                    '<div class="load"><button  type="button" class="el-button el-button--text"><span>没有更多了</span></button></div></div>'+
                    '<div class="empty-mask" style="display: none;"><div class="empty-content"><img src="/x_component_CRM/$Template/empty.png" class="empty-icon"> <p class="empty-text">没有找到数据</p></div></div></div>'+
                    '<div class="el-loading-mask" style="display: none;"><div class="el-loading-spinner"><svg viewBox="25 25 50 50" class="circular"><circle cx="50" cy="50" r="20" fill="none" class="path"></circle></svg></div></div>'+
                    '</div></div>';

            //jQuery("#"+sectionId).find("#tab-follow").html(indexHtml+indexSendHtml+indexContentHtml);
            this.sectionArea.getElement("#tab-follow").set("html",indexHtml+indexSendHtml+indexContentHtml);
            that.loadTimeContainer('stime');
            that.loadRecord();
        }.bind(this));
    },
    createTypeHtml: function () {
        var createType = jQuery(this.sectionArea).find(".hit").text();
        debugger
        if(createType == "基本信息") {
            this.getPublicseasInfo();
        }
        if(createType == "联系人") {
            this.loadContacts();
        }
        if(createType == "相关团队") {
            this.loadTeam();
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
    getPublicseasInfo: function () {
        _self = this;
        this.actions.getCustomerInfo(this.options.openId, function (json) {
            var jsonObj = json.data;
            var section_header = '<div class="section-header"><div class="section-mark" style="border-left-color: rgb(70, 205, 207);"></div> '+
                '<div data-v-ec8f8850="" class="section-title">基本信息</div></div>'

            var itemTemplateObject = _self.getItemTemplate(_self.lp );
            var section_conent = '<div class="section-conent">';
            for ( i in itemTemplateObject){
                section_conent = section_conent+'<div class="conent-inline"><div class="conent-title">'+itemTemplateObject[i].text+'</div>' +
                    '<div class="conent-value">'+((typeof(jsonObj[i])=="undefined")?"" : jsonObj[i])+'</div></div>';

            }
            section_conent = section_conent + '</div>';
            //jQuery("#tab-basicinfo").html(section_header+section_conent);
            jQuery(_self.sectionArea).find("#tab-basicinfo").html(section_header+section_conent);
            //this.contentHtml =  briefDiv+tabPanel+tabConent;
        }.bind(this));
    },
    loadAttachment: function() {
        that = this;
        var attHtml = '<div class="rc-cont">';
        var attButton = '';
        var attHeader = '<div  class="el-table el-table--fit el-table--striped el-table--enable-row-hover" header-align="center" style="width: 100%; border: 1px solid rgb(230, 230, 230); height: 500px;" align="center">'+
                '<div class="el-table__header-wrapper"><table class="el-table__header" style="width: 100%;" cellspacing="0" cellpadding="0" border="0"><thead class="has-gutter">'+
                '<tr class=""><th colspan="1" rowspan="1" class="el-table_3_column_27     is-leaf" style="width: 40%;height:40px;"><div class="cell">附件名称</div></th>'+
                '<th colspan="1" rowspan="1" class="el-table_3_column_28     is-leaf" style="width: 10%;"><div class="cell">附件大小</div></th>'+
                '<th colspan="1" rowspan="1" class="el-table_3_column_29     is-leaf" style="width: 20%;"><div class="cell">上传人</div></th>'+
                '<th colspan="1" rowspan="1" class="el-table_3_column_30     is-leaf" style="width: 20%;"><div class="cell">上传时间</div></th></tr></thead></table></div>';

        var attBody = '<div class="el-table__body-wrapper is-scrolling-none" style="height: 450px;">'+
                '<table class="el-table__body" style="width: 100%;" cellspacing="0" cellpadding="0" border="0"><tbody></tbody></table>'+
                '<div class="el-table__empty-block" style="width: 100%;"><span class="el-table__empty-text">暂无数据</span></div></div></div>';
        attHtml = attHtml+attButton+attHeader+attBody+'</div>';
        jQuery(that.sectionArea).find("#tab-att").html(attHtml);

        jQuery(that.sectionArea).find('#afile').change(function(event) {
            var objFile =  jQuery(that.sectionArea).find('#afile')[0].files;
            jQuery(objFile).each(function(index,file){
                    var filter = {};
                    filter = {
                        file:file,
                        fileName:file.name
                    };
                    var formdata=new FormData();
                    formdata.append("fileName",file.name);
                    formdata.append("file",file);
                    that.actions.updateAttachment("att", that.options.openId, "customer", formdata,file, function (json) {
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
                                '<td style="width: 20%;">'+lastUpdatePerson+'</td><td style="width: 20%;">'+attData.updateTime+'</td></tr>';
                    }
                }
                if(tbodyHtml!=""){
                    jQuery(that.sectionArea).find(".el-table__body").children().html(tbodyHtml);
                    jQuery(that.sectionArea).find(".el-table__empty-block").hide();

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
    loadContacts: function() {
        that = this;
        var attHtml = '<div class="rc-cont">';
        var attButton = '';
        var attHeader = '<div  class="el-table el-table--fit el-table--striped el-table--enable-row-hover" header-align="center" style="width: 100%; border: 1px solid rgb(230, 230, 230); height: 500px;" align="center">'+
            '<div class="el-table__header-wrapper"><table class="el-table__header" style="width: 100%;" cellspacing="0" cellpadding="0" border="0"><thead class="has-gutter">'+
            '<tr class=""><th colspan="1" rowspan="1" class="el-table_3_column_27     is-leaf" style="width: 33%;height:40px;"><div class="cell">姓名</div></th>'+
            '<th colspan="1" rowspan="1" class="el-table_3_column_29     is-leaf" style="width: 34%;"><div class="cell">手机</div></th>'+
            '<th colspan="1" rowspan="1" class="el-table_3_column_26     is-leaf" style="width: 33%;"><div class="cell">职务</div></th></tr></thead></table></div>';

        var attBody = '<div class="el-table__body-wrapper is-scrolling-none" style="height: 450px;">'+
            '<table class="el-table__body" style="width: 100%;" cellspacing="0" cellpadding="0" border="0"><tbody></tbody></table>'+
            '<div class="el-table__empty-block" style="width: 100%;"><span class="el-table__empty-text">暂无数据</span></div></div></div>';
        attHtml = attHtml+attButton+attHeader+attBody+'</div>';
        jQuery(that.sectionArea).find("#tab-contacts").html(attHtml);

        this.actions.getContacts(this.options.openId, function (json) {
            if(json.type=="success"){
                var attDatas = json.data;
                var tbodyHtml = "";
                for ( i in attDatas){
                    if(i<attDatas.length){
                        var attData = attDatas[i];
                        tbodyHtml = tbodyHtml+'<tr><td style="width: 34%;height:40px;" class="aname" aid="'+attData.id+'">'+attData.contactsname+'</td>'+
                            '<td style="width: 33%;">'+attData.cellphone+'</td><td style="width: 33%;">'+attData.post+'</td></tr>';
                    }
                }
                if(tbodyHtml!=""){
                    jQuery(that.sectionArea).find(".el-table__body").children().html(tbodyHtml);
                    jQuery(that.sectionArea).find(".el-table__empty-block").hide();
                }
            }
        }.bind(that));
        jQuery(that.sectionArea).find("#tab-contacts").find(".rc-head-item").click(function(){
            that.contactsCreate();
        });
        jQuery(that.sectionArea).find("#tab-contacts").find(".aname").click(function(){
            //that.contactsEdit(jQuery(this).attr("aid"));
        });
    },
    loadTeam: function() {
        that = this;
        var attHtml = '<div class="rc-cont">';
        var attButton = '';
        var attHeader = '<div  class="el-table el-table--fit el-table--striped el-table--enable-row-hover" header-align="center" style="width: 100%; border: 1px solid rgb(230, 230, 230); height: 500px;" align="center">'+
            '<div class="el-table__header-wrapper"><table class="el-table__header" style="width: 100%;" cellspacing="0" cellpadding="0" border="0"><thead class="has-gutter">'+
            '<tr class=""><th colspan="1" rowspan="1" class="el-table_3_column_27"><div class="cell">'+
            '<input class="inp-cbx" id="all" type="checkbox" style="display: none;"/><label class="cbx cbxx" for="all"><span><svg width="12px" height="10px"><use xlink:href="#check"></use></svg></span><span></span></label></div></th>'+
            '<th colspan="1" rowspan="1" class="el-table_3_column_27     is-leaf" style="width: 30%;height:40px;"><div class="cell">姓名</div></th>'+
            '<th colspan="1" rowspan="1" class="el-table_3_column_29     is-leaf" style="width: 20%;"><div class="cell">职位</div></th>'+
            '<th colspan="1" rowspan="1" class="el-table_3_column_29     is-leaf" style="width: 20%;"><div class="cell">团队角色</div></th>'+
            '<th colspan="1" rowspan="1" class="el-table_3_column_26     is-leaf" style="width: 20%;"><div class="cell">权限</div></th></tr></thead></table></div>'+
            '<svg class="inline-svg"><symbol id="check" viewbox="0 0 12 10"><polyline points="1.5 6 4.5 9 10.5 1" ></polyline></symbol></svg>';

        var attBody = '<div class="el-table__body-wrapper is-scrolling-none" style="height: 450px;">'+
            '<table class="el-table__body" style="width: 100%;" cellspacing="0" cellpadding="0" border="0"><tbody></tbody></table>'+
            '<div class="el-table__empty-block" style="width: 100%;"><span class="el-table__empty-text">暂无数据</span></div></div></div>';
        attHtml = attHtml+attButton+attHeader+attBody+'</div>';
        jQuery(that.sectionArea).find("#tab-team").html(attHtml);

        this.actions.getTeamMemberListById(this.options.openId, function (json) {
            if(json.type=="success"){
                var attDatas = json.data;
                var tbodyHtml = "";

                for ( i in attDatas){
                    if(i<attDatas.length){
                        var attData = attDatas[i];
                        var unit = (attData.units.length>0)?((attData.units[0]).split("@")[0]):"";
                        var id = 'all'+i;
                        var cdiv = '<div class="cell">'+
                            '<input class="inp-cbx" id="'+id+'" type="checkbox" style="display: none;"/><label class="cbx cbxx" for="'+id+'"><span><svg width="12px" height="10px"><use xlink:href="#check"></use></svg></span><span></span></label></div>'
                        if(attData.teamRole && attData.teamRole=="负责人"){
                            cdiv = '<div class="cell">'+
                            '<input class="inp-cbx" id="'+id+'" type="checkbox" style="display: none;"/><label class="cbx" for="xx"><span style="background: #e6e6e6;"><svg width="12px" height="0px"><use xlink:href="#check"></use></svg></span><span></span></label></div>'
                        }
                        if(attData.teamRole && attData.teamRole!=""){
                            tbodyHtml = tbodyHtml+'<tr><td style="width: 10%;height:40px;">'+cdiv+'</td><td style="width: 30%;" class="aname" aid="'+attData.person.distinguishedName+'">'+attData.person.name+
                                '</td><td style="width: 20%;">'+unit+'</td><td style="width: 20%;">'+attData.teamRole+'</td><td style="width: 20%;">'+attData.dispaly_permission+'</td></tr>';
                        }

                    }
                }
                if(tbodyHtml!=""){
                    jQuery(that.sectionArea).find(".el-table__body").children().html(tbodyHtml);
                    jQuery(that.sectionArea).find(".el-table__empty-block").hide();
                }
            }
        }.bind(that));
        jQuery("[for='all']").click(function(){
            //var cobj =  jQuery("[for='all1']");
            var cobj =  jQuery(that.sectionArea).find(".cbxx").not("[for='all']");
            if (jQuery(this).prev().is(':checked')) {
                cobj.each(function(index,element){
                    jQuery(element).prev().prop("checked", false);
                });

            } else {
                cobj.each(function(index,element){
                    jQuery(element).prev().prop("checked", true);
                });
            }
        });

    },
    loadBusiness: function() {
        that = this;
        var attHtml = '<div class="rc-cont">';
        var attButton = '';
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

        this.actions.getOpportunityListByCustomerId(this.options.openId, function (json) {
            if(json.type=="success"){
                var attDatas = json.data;
                var tbodyHtml = "";
                for ( i in attDatas){
                    if(i<attDatas.length){
                        var attData = attDatas[i];
                        tbodyHtml = tbodyHtml+'<tr><td style="width: 30%;height:40px;" class="aname" aid="'+attData.id+'">'+attData.opportunityname+
                            '</td><td style="width: 20%;">'+attData.money+'</td><td style="width: 30%;">'+attData.customer.customername+
                            '</td><td style="width: 20%;">'+attData.opportunityType.opportunitytypename+'</td><td style="width: 20%;">'+attData.opportunityStatus.opportunitystatusname+'</td></tr>';
                    }
                }
                if(tbodyHtml!=""){
                    jQuery(that.sectionArea).find(".el-table__body").children().html(tbodyHtml);
                    jQuery(that.sectionArea).find(".el-table__empty-block").hide();
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
                for ( i in recordDatas){
                    if(i<recordDatas.length){
                        var recordData = recordDatas[i];
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
    receiveCustomer: function () {
        _self = this;
        var contentHtml = '确定要领取此客户吗？领取后将在公海移除。'
        Showbo.Msg.confirm('提示',contentHtml,function(){
            _self.confirmReceiveCustomer();
        },function(){
        });
        jQuery('.handle-item-content').click(function(){
            jQuery(".ct").find(".el-dropdown-confirm").toggle(100);
        });
    },
    confirmReceiveCustomer: function () {
        this.actions.receiveCustomer(this.options.openId, function (json) {
            if(json.type == "success"){
                Showbo.Msg.alert('操作成功!');
            }
        }.bind(this));

    },
    distributeCustomer: function () {
        //转移负责人
        _self = this;
        var contentHtml = '<div  class="vux-flexbox handle-item vux-flex-row" style="align-items: stretch;padding: 30px 20px 0px 20px;line-height:30px;">'+
            '<div class="handle-item-name" style="margin-top: 8px;width:130px;">分配给：</div><div class="el-select handle-item-content" style="margin-top: 8px;"><div class="se-select-name" id="selectName" style="display: inline-block;">+点击选择</div><div id="selectId" style="display:none;"></div></div></div>'+
            '<div  class="vux-flexbox handle-item vux-flex-row" style="align-items: stretch;padding: 10px 20px 80px 20px;line-height:30px;"></div></div>'



        Showbo.Msg.confirm('公海分配',contentHtml,function(){
            //_self.confirmCustomerDealstatus();
            var transferType = "";
            var readOrWrite = "";
            var checkFlag = true;
            var relationTypeList = [];


            if(jQuery("#selectId").text() == ""){
                checkFlag = false;
                Showbo.Msg.alert('请选择负责人!');
            }

            if(checkFlag){
                var filter = {};
                filter = {
                    distinguishName: jQuery('#selectId').text()
                }
                debugger
                _self.actions.distributeCustomer(_self.options.openId, filter, function (json) {
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
    loadEvent: function(){
        that = this;
        jQuery(that.sectionArea).find('.tabPanel div').click(function(){
            debugger
            jQuery(this).addClass('hit').siblings().removeClass('hit');
            jQuery(that.sectionArea).find('.panes>div:eq('+jQuery(this).index()+')').show().siblings().hide();
            that.createTypeHtml();
        });
        jQuery(that.sectionArea).find('.headMoreBottonDiv').click(function(){
            jQuery(that.sectionArea).find(".el-dropdown-menu").toggle(100);
        });
        jQuery(that.sectionArea).find('.el-dropdown-menu__item').click(function(){
            debugger
            if(jQuery(this).text()=="分配"){
                that.distributeCustomer();
            }
            if(jQuery(this).text()=="领取"){
                that.receiveCustomer();
            }
            if(jQuery(this).text()=="删除"){

            }
            //---for记录类型
            if(jQuery(this).parent().attr("tid")=="recordType"){
                jQuery(that.sectionArea).find(".se-select-name").text(jQuery(this).text());
                jQuery(this).parent().toggle(100);
            }

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

        jQuery(that.sectionArea).find('.el-dropdown-selfdefine').click(function(){
            jQuery(that.sectionArea).find("[tid='recordType']").toggle(100);
        });

    },

    loadRecord: function(){
        that = this;
        this.actions.getRecord(this.options.openId, function (json) {
            if(json.type=="success"){
                var recordDatas = json.data;
                jQuery(that.sectionArea).find(".fl-c").remove();
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
         this.actions.getPublicseasInfo(this.data.id,function(json){
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
            this.actions.geCustomerInfo(this.options.openId, function (json) {
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
    loadMap: function(){
        debugger
        _self = this;
        this.mapDiv = jQuery("#setMap")[0];
        jQuery(".section-conent").css("height","700px");
        if(this.mapDiv)this.mapDiv.empty();
        if(this.addressModule) delete this.addressModule;
        MWF.xDesktop.requireApp("CRM", "AddressExplorer", function(){
            this.addressModule = new MWF.xApplication.CRM.AddressExplorer(this.mapDiv,this,this.actions,{});
            this.addressModule.load();
        }.bind(this))
    },
    getItemTemplate: function( lp ){
        _self = this;
        return {
            customername: {
                text: lp.customername,
                type: "text",
                //attr : {placeholder:lp.name},
                notEmpty:true,
                value:this.customerData && this.customerData.customername?this.customerData.customername:""
            },
            level:{
                type: "select",
                text: lp.level,
                value:this.app.lp.customer.level.value
            },
            industry: {
                type: "select",
                text: lp.industry,
                value:this.app.lp.customer.industry.value
            },
            source: {
                type: "select",
                text: lp.source,
                value:this.app.lp.customer.source.value
            },
            /*dealstatus:{
                type: "hide",
                text: lp.dealstatus,
                value:this.app.lp.customer.dealstatus.value
            },*/
            telephone:{
                type: "text",
                text: lp.telephone,
                value:this.app.lp.clue.level.value
            },
            website: {
                text:lp.website,
                type: "text"
            },
            nexttime: {
                text:lp.nexttime,
                attr : {id:"nexttime"},
                type: "datetime"
            },
            cellphone: {
                text:lp.cellphone,
                type: "text"
            },
            detailaddress: {
                text:lp.detailaddress,
                type: "text"
            },
            remark: {
                text:lp.remark,
                type: "textarea"
            },
            location: {
                text:lp.location,
                type: "map"
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
    /*selectCalendar : function( calendarNode ){
     MWF.require("MWF.widget.Calendar", function(){
     var calendar = new MWF.widget.Calendar( calendarNode, {
     "style": "xform",
     "isTime": false
     "target": this.app.content
     });
     calendar.show();
     }.bind(this));
     },*/
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