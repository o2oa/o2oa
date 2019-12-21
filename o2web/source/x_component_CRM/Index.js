MWF.xApplication.CRM = MWF.xApplication.CRM || {};

MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("CRM", "Template", null,false);
MWF.xDesktop.requireApp("Template", "Explorer", null,false);
MWF.require("MWF.widget.O2Identity", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.xDesktop.requireApp("CRM", "ClueEdit", null,false);
MWF.xDesktop.requireApp("CRM", "Customer", null,false);
MWF.xDesktop.requireApp("CRM", "CustomerEdit", null,false);
MWF.xDesktop.requireApp("CRM", "Contacts", null,false);
MWF.xDesktop.requireApp("CRM", "ContactsEdit", null,false);
MWF.xDesktop.requireApp("CRM", "Chance", null,false);
MWF.xDesktop.requireApp("CRM", "ChanceEdit", null,false);
MWF.xApplication.CRM.AddressExplorer={};
/*MWF.xApplication.CRM.Chance={};*/

/*MWF.require("MWF.widget.Identity", null,false);
MWF.xDesktop.requireApp("Forum", "Actions.RestActions", null, false);*/

MWF.xApplication.CRM.Index = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },

    initialize: function (node, app, actions, options) {
        this.setOptions(options);
        this.app = app;
        this.lp = app.lp.index;
        this.path = "/x_component_CRM/$Index/";
        this.loadCss();

        this.actions = actions;
        //this.node = $(node);
        this.node = node;
        this.configData = [];
        var now = new Date(); //当前日期
        var nowYear = now.getYear();
        nowYear += (nowYear < 2000) ? 1900 : 0; //
        this.nowDayOfWeek = now.getDay(); //今天本周的第几天
        this.nowDay = now.getDate(); //当前日
        this.nowMonth = now.getMonth(); //当前月
        this.nowYear = nowYear;//当前年
        this.now = now;
    },
    loadCss: function () {
        this.cssPath = "/x_component_CRM/$Index/" + this.options.style + "/css.wcss";
        this._loadCss();
    },
    load: function () {
        //this.testActions = new MWF.xApplication.Forum.Actions.RestActions();
        if(this.formContentArr)this.formContentArr.empty();
        this.formContentArr = [];
        if(this.formMarkArr)this.formMarkArr.empty();
        this.formMarkArr = [];
       /*MWF.xDesktop.requireApp("CRM", "$Chance.default."+o2.language, {
            "failure": function(){
                MWF.xDesktop.requireApp("CRM", "$Chance.default.zh-cn", null, false);
            }.bind(this)
        }, false);*/
        this.rightContentDiv = this.rightContentDiv || this.app.rightContentDiv;

        this.loadResource(function(){
            this.appArea = jQuery("body").children(":first");
            this.createHeadContent();
            this.createIndexContent();
        }.bind(this))
        /*this.resizeWindow();
        console.log("x7777");
        this.app.addEvent("resize", function(){
            this.resizeWindow();
        }.bind(this));*/
    },
    reload:function(){
        this.createIndexContent();
        //this.resizeWindow();
    },
    loadResource: function ( callback ) {
        var baseUrls = [
            "/x_component_CRM/$Template/plugins/jquery.min.js",
        ];
        var fullcalendarUrl = "/x_component_CRM/$Template/plugins/layui/layui.js";
        var langUrl =  "/x_component_CRM/$Template/plugins/table2/table2.js";
        COMMON.AjaxModule.loadCss("/x_component_CRM/$Template/plugins/table2/css/table2.css",function(){
            COMMON.AjaxModule.load(baseUrls, function(){
                jQuery.noConflict();
                COMMON.AjaxModule.load(fullcalendarUrl, function(){
                    COMMON.AjaxModule.load(langUrl, function(){
                        COMMON.AjaxModule.loadCss("/x_component_CRM/$Template/assets/css/notifyme.css", function(){
                            COMMON.AjaxModule.loadCss("/x_component_CRM/$Template/date/css/jquery-ui.css", function(){
                                COMMON.AjaxModule.load("/x_component_CRM/$Template/date/jquery-ym-datePlugin-0.1.js", function(){
                                    COMMON.AjaxModule.load("/x_component_CRM/$Template/assets/js/notifyme.js", function(){
                                        COMMON.AjaxModule.load("/x_component_CRM/$Template/assets/js/showBo.js", function(){
                                            COMMON.AjaxModule.load("/x_component_CRM/$Template/assets/js/echarts.min.js", function(){
                                                COMMON.AjaxModule.load("/x_component_CRM/$Template/assets/js/china.js", function(){
                                                    if(callback)callback();
                                                }.bind(this));
                                            }.bind(this));
                                        }.bind(this));
                                    }.bind(this));
                                }.bind(this));
                            }.bind(this))
                        }.bind(this))
                    }.bind(this));
                }.bind(this));
            }.bind(this))
        }.bind(this))
    },

    createHeadContent:function(){
        _self = this;
        if(jQuery(".openDiv").length > 0) jQuery(".openDiv").remove();
        jQuery(_self.appArea).after("<div class='openDiv'></div>")
        jQuery(".openDiv").before("<div></div>");
        if(jQuery("#layout").length > 0)jQuery(_self.appArea).find(".MWF_dialod_title_action").find("div").eq(1).hide();
        if(this.headContentDiv) this.headContentDiv.destroy();
        this.headContentDiv = new Element("div.headContentDiv",{"styles":this.css.headContentDiv}).inject(this.rightContentDiv);
        this.headHomeDiv = new Element("div.headHomeDiv",{"styles":this.css.headHomeDiv}).inject(this.headContentDiv);
        this.headHomeImg = new Element("img.headHomeImg",{
            "styles":this.css.headHomeImg,
            "src": this.path+"default/icons/homePage.png"
        }).inject(this.headHomeDiv);

        this.headTitleDiv = new Element("div.headTitleDiv",{
            "styles":this.css.headTitleDiv,
        }).inject(this.headContentDiv);
        this.headTitleinDiv = new Element("div.headTitleinDiv",{
            "styles":this.css.mark_header,
            "text":this.lp.head.headTitle
        }).inject(this.headTitleDiv);

        this.user_lineDiv = new Element("div.user_line",{
            "styles":this.css.user_line
        }).inject(this.headContentDiv);
        this.user_changeDiv = new Element("div.user_change",{
            "styles":this.css.user_change,
            "text":"切换"
        }).inject(this.headContentDiv);
        //select
        this.selectDiv = new Element("div.se-select",{"styles":this.css.selectDiv}).inject(this.headContentDiv);
        this.selectnameDiv = new Element("div.se-select-name",{
            "styles":this.css.selectname,
            "text":"本年"
        }).inject(this.selectDiv);
        this.arrowdownDiv = new Element("div.el-icon-arrow-down", {"styles":this.css.arrowdown}).inject(this.selectDiv);
        this.selectImg = new Element("img.selectImg",{
            "styles":this.css.selectImg,
            "src": this.path+"default/icons/arrow.png"
        }).inject(this.arrowdownDiv);

        //var selecthtml = '<ul class="el-dropdown-type" style="display: none;top:115px;left:398px;width:220px;" id="searchDate"></ul>'
        var selecthtml = '<ul class="el-dropdown-type" style="display: none;width:220px;" id="searchDate"></ul>'
        jQuery(".headContentDiv").after(selecthtml);
        var ulHtml = "";
        var valueList = this.lp.searchDate.value;
        var valueArr = valueList.split(",");
        for(var n=0;n<valueArr.length;n++){
            ulHtml = ulHtml+'<li class="el-dropdown-menu__item" style="font-size:15px;">'+valueArr[n]+'</li>'
        }
        jQuery("#searchDate").append(ulHtml+'<div class="popper__arrow" style="left:99px;box-sizing: border-box !important;"></div>');
        this.dropdown = jQuery("body")[0].getElement(".el-dropdown-type");
        if(this.dropdown) this.dropdown.setStyles({"top":(jQuery(".user_change").offset().top+52)+"px","left":jQuery(".user_change").offset().left+2});

        jQuery(".headTitleDiv").append('<div id="selectheadName" style="display: none;"></div><div id="selectheadId" personList="" departList="" style="display: none;"></div>');
        jQuery(".se-select").click(function(){
            jQuery(".el-dropdown-type").toggle(100);
        });
        jQuery('.el-dropdown-menu__item').click(function(){
            if(jQuery(this).text()=="自定义"){

                var dateHtml = '<div class="timeDiv"><div class="timeItemDiv"><input name="starttime" class="inputtime" id="starttime" type="text" readonly="readonly"></div>'+
                    '<div class="timeItemDiv"><input name="endtime" class="inputtime" id="endtime" type="text" readonly="readonly"></div>'+
                    '<div class="timeOk">确定</div></div>';
                if(jQuery(".timeDiv").length>0){
                    jQuery(".inputtime").val("");
                    jQuery(".timeDiv").toggle(100);
                }else{
                    jQuery(this).parent().append(dateHtml);
                }
                _self.loadTimeContainer("starttime");
                _self.loadTimeContainer("endtime");
                jQuery('.timeOk').click(function(){
                     if(jQuery("#starttime").val()!="" && jQuery("#endtime").val()!=""){
                         jQuery(_self.rightContentDiv).find(".se-select-name").text(jQuery("#starttime").val()+"--"+jQuery("#endtime").val());
                         _self.loadDataContent();
                         jQuery(this).parent().parent().hide();
                     }else{
                         Showbo.Msg.alert('请选择开始日期和结束日期!');
                     }

                });
            }else{
                jQuery(_self.rightContentDiv).find(".se-select-name").text(jQuery(this).text());
                _self.loadDataContent();
                jQuery(this).parent().toggle(100);
            }
        });
        jQuery(".user_change").click(function(){
                _self.selectPerson(jQuery(_self.appArea)[0],"selectheadName","selectheadId",0);
        });
         //if(jQuery(".quickNew").length > 0) jQuery(".quickNew").remove();

         if(jQuery(".quickNew").length < 1){
             var quickNewHtml='<div class="quickNew"><div class="quickNew_conent">'+
                 '<div class="quickNew_item"><span class="sImg"><img class="naviItemImg" src="/x_component_CRM/$Main/default/icons/clue-fill.png"></span><span class="sName" stype="clue">线索</span></div>'+
                 '<div class="quickNew_item"><span class="sImg"><img class="naviItemImg" src="/x_component_CRM/$Main/default/icons/customer-fill.png"></span><span class="sName" stype="customer">客户</span></div>'+
                 '<div class="quickNew_item"><span class="sImg"><img class="naviItemImg" src="/x_component_CRM/$Main/default/icons/contact-fill.png"></span><span class="sName" stype="contact">联系人</span></div>'+
                 '<div class="quickNew_item"><span class="sImg"><img class="naviItemImg" src="/x_component_CRM/$Main/default/icons/chance-fill.png"></span><span class="sName" stype="chance">商机</span></div></div></div>';
             jQuery(".middleContentDiv").append(quickNewHtml);
             jQuery(".quickStartDiv").click(function(){
                 jQuery(".quickNew").toggle(100);
             });
             jQuery(".quickNew").mouseleave(function(){
                 jQuery(this).toggle(100);
             });
             _thatnew = _self;
             jQuery(".quickNew_item").click(function(){
                 _thatnew.openNew(jQuery(this).children().eq(1).attr("stype"));
             });
         }

        jQuery("body").click(function(e){
            if (jQuery(e.target).closest(".se-select").length <1){
                if(jQuery(".el-dropdown-type").css('display')=='block'){
                    jQuery(".el-dropdown-type").hide();
                }
            }
        });
        _self.app.leftContentDiv.getElements(".naviItemLi").addEvents({
            "click":function(){
                jQuery(".notify").remove();
            }.bind(this)
        });

    },
    createToolBarContent:function(){

    },
    createIndexContent:function(){
        debugger
        if(this.contentListDiv) this.contentListDiv.destroy();
        this.contentListDiv = new Element("div.contentListDiv",{"styles":this.css.contentListDiv}).inject(this.rightContentDiv);

        if(this.contentListInDiv) this.contentListInDiv.destroy();
        this.contentListInDiv = new Element("div.contentListInDiv",{"styles":this.css.contentListInDiv}).inject(this.contentListDiv);
        //var size = this.rightContentDiv.getSize();
        //if(this.contentListDiv)this.contentListDiv.setStyles({"height":(size.y-this.headContentDiv.getHeight()-8)+"px"});
        //if(this.contentListInDiv)this.contentListInDiv.setStyles({"height":this.contentListDiv.getHeight()+"px","width":"100%"});

        this.loadDataContent();
    },
    loadDataContent:function(){
        _self = this;
        if(this.contentListInDiv) this.contentListInDiv.destroy();
        this.contentListInDiv = new Element("div.contentListInDiv",{"styles":this.css.contentListInDiv}).inject(this.contentListDiv);

        this.vux_flexbox_item = new Element("div.vux_flexbox_item",{
            "id":"jianbao",
            "styles":this.css.vux_flexbox_item
        }).inject(this.contentListInDiv);
        this.cardDiv = new Element("div.cardDiv",{"styles":this.css.cardDiv}).inject(this.vux_flexbox_item);
        this.mark_headerDiv = new Element("div.mark_header",{
            "styles":this.css.mark_header,
            "text":"销售简报"
        }).inject(this.cardDiv);
        this.markImg = new Element("img.img_mark",{
            "styles":this.css.img_mark,
            "src": this.path+"default/icons/jianbao.png"
        }).inject(this.mark_headerDiv);
        this.vux_flex_rowDiv = new Element("div.vux_flex_row",{"styles":this.css.vux_flex_row}).inject(this.cardDiv);

        this.vux_flexbox_item_right = new Element("div.vux_flexbox_item_right",{
            "id":"fenbu",
            "styles":this.css.vux_flexbox_item_right
        }).inject(this.contentListInDiv);
        this.cardDiv = new Element("div.cardDiv",{"styles":this.css.cardDiv}).inject(this.vux_flexbox_item_right);
        this.mark_headerDiv = new Element("div.mark_header",{
            "styles":this.css.mark_header,
            "text":"客户分布"
        }).inject(this.cardDiv);
        this.markImg = new Element("img.img_mark",{
            "styles":this.css.img_mark,
            "src": this.path+"default/icons/fenbu.png"
        }).inject(this.mark_headerDiv);
        this.vux_flex_rowDiv = new Element("div.vux_flex_row",{
            "id":"cityMap",
            "styles":this.css.vux_flex_rowDiv
        }).inject(this.cardDiv);

        this.vux_flexbox_item2 = new Element("div.vux_flexbox_item",{
            "id":"loudou",
            "styles":this.css.vux_flexbox_item
        }).inject(this.contentListInDiv);
        this.cardDiv3 = new Element("div.cardDiv",{"styles":this.css.cardDiv}).inject(this.vux_flexbox_item2);
        this.mark_headerDiv = new Element("div.mark_header",{
            "styles":this.css.mark_header,
            "text":"客户总量"
        }).inject(this.cardDiv3);
        this.markImg = new Element("img.img_mark",{
            "styles":this.css.img_mark,
            "src": this.path+"default/icons/loudou.png"
        }).inject(this.mark_headerDiv);
        this.vux_flex_rowDiv = new Element("div.vux_flex_row",{
            "styles":this.css.vux_flex_rowDiv,
            "id":"costomerCount"
        }).inject(this.cardDiv3);

        this.vux_flexbox_item_right2 = new Element("div.vux_flexbox_item_right",{
            "id":"qushi",
            "styles":this.css.vux_flexbox_item_right
        }).inject(this.contentListInDiv);
        this.cardDiv = new Element("div.cardDiv",{"styles":this.css.cardDiv}).inject(this.vux_flexbox_item_right2);
        this.mark_headerDiv = new Element("div.mark_header",{
            "styles":this.css.mark_header,
            "text":"客户行业"
        }).inject(this.cardDiv);
        this.markImg = new Element("img.img_mark",{
            "styles":this.css.img_mark,
            "src": this.path+"default/icons/qushi.png"
        }).inject(this.mark_headerDiv);
        this.vux_flex_rowDiv = new Element("div.vux_flex_row",{
            "styles":this.css.vux_flex_rowDiv,
            "id":"costomerIndustryCount"
        }).inject(this.cardDiv);

        var personList = jQuery("#selectheadId").attr("personList")==""?[]:jQuery("#selectheadId").attr("personList").split(",");
        var departList = jQuery("#selectheadId").attr("departList")==""?[]:jQuery("#selectheadId").attr("departList").split(",");
        var dateList = this.getDateList(jQuery(_self.rightContentDiv).find(".se-select-name").text()).split(",");
        var filter = {};
        filter = {
            begintime:dateList[0]+" 00:00:00",
            endtime:dateList[1]+" 23:59:59",
            key:"",
            personNameList:personList,
            unitList:departList
        };
        this.getJianbaoList(filter);
        this.getFenBuMap();
        this.getCustomerCount(filter);
        this.getCustomerByIndustry(filter);
    },
    openNew: function(stype){
        _self = this;
        if(stype=="clue"){
            MWF.xDesktop.requireApp("CRM", "ClueEdit", function(){
                _self.clueModule = new MWF.xApplication.CRM.ClueEdit(_self, _self.actions,{},{
                    "isEdited":true,
                    "isNew":true,
                    "onReloadView" : function(){
                        //this.load();
                    }.bind(_self)
                });
                _self.clueModule.load();
            }.bind(_self))
        }
        if(stype=="customer"){
            MWF.xDesktop.requireApp("CRM", "CustomerEdit", function(){
                _self.customerModule = new MWF.xApplication.CRM.CustomerEdit(_self, _self.actions,{},{
                    "isEdited":true,
                    "isNew":true,
                    "onReloadView" : function(){
                        //this.load();
                    }.bind(_self)
                });
                _self.customerModule.load();
            }.bind(_self))
        }
        if(stype=="contact"){
            MWF.xDesktop.requireApp("CRM", "ContactsEdit", function(){
                _self.contactModule = new MWF.xApplication.CRM.ContactsEdit(_self, _self.actions,{},{
                    "isEdited":true,
                    "isNew":true,
                    "onReloadView" : function(){
                        //this.load();
                    }.bind(_self)
                });
                _self.contactModule.load();
            }.bind(_self))
        }
        if(stype=="chance"){
            MWF.xDesktop.requireApp("CRM", "ChanceEdit", function(){
                var editForm = new MWF.xApplication.CRM.ChanceEdit(null,{},null, {
                    app: _self.app,
                    container : _self.app.content,
                    lp : _self.app.lp.chance,
                    actions : _self.actions,
                    css : {},
                    /*customer :{"id":that.options.openCustomerId},*/
                    callback : function () {
                        //editForm.create();
                    }
                });
                editForm.create();
            }.bind(_self));
        }
    },
    getJianbaoList: function(filter){
        _self = this;
        //获取简报数字列表
        _self.actions.countLike( filter, function (json) {
            debugger
            if(json.type=="success"){
                var customerHtml ='';
                json.data.each(function (data ) {
                    var moduleId = data.moduleId
                    var count = data.count
                    var moduleName = "";
                    switch (moduleId) {
                        case "customer":
                            moduleName = "新增客户";
                            break;
                        case "contacts":
                            moduleName = "新增联系人";
                            break;
                        case "opportunity":
                            moduleName = "新增商机";
                            break;
                        case "record":
                            moduleName = "跟进记录";
                            break;
                    }
                    customerHtml = customerHtml+ '<div class="vux-flexbox-item" id="'+moduleId+'"><div class="vux-flexbox jianbao-icon-content" style="cursor: pointer;">'+
                        '<img  src="/x_component_CRM/$Index/default/icons/'+moduleId+'.png" class="jianbao-icon"><div class="jianbao-title">'+moduleName+'</div><div class="jianbao-value">'+count+'</div></div></div>'
                }.bind(this));

                jQuery("#jianbao").find(".vux_flex_row").append(customerHtml);
                jQuery("#jianbao").find(".vux-flexbox-item").each(function(index,element){
                    jQuery(this).click(function(){
                        var mid = jQuery(this).attr("id");
                        /*var filter = {};*/
                        if(jQuery(".openDiv").length > 0){
                            jQuery(".openDiv").empty();
                        }
                        switch (mid) {
                            case "customer":
                                if(_self.index_customerView) delete _self.index_customerView;
                                /*filter = {
                                    begintime:dateList[0],
                                    endtime:dateList[1],
                                    key:"",
                                    personNameList:personList,
                                    unitList:departList
                                };*/
                                _self.index_customerView =  new MWF.xApplication.CRM.Index.View(
                                    jQuery(".openDiv")[0],
                                    _self.app,
                                    _self.app,
                                    _self,
                                    { templateUrl : "/x_component_CRM/$Customer/customerView.json",filterData:{}},
                                    {
                                        lp:_self.app.lp.customerView,
                                        isAdmin:_self.options.isAdmin
                                    }
                                );
                                _self.index_customerView.load();
                                jQuery(".openDiv").show();
                                break;
                            case "contacts":
                                if(_self.index_contactsView) delete _self.index_contactsView;
                                _self.index_contactsView =  new MWF.xApplication.CRM.Index.ContactsView(
                                    jQuery(".openDiv")[0],
                                    _self.app,
                                    _self.app,
                                    _self,
                                    { templateUrl : "/x_component_CRM/$Contacts/contactsView.json",filterData:{}},
                                    {
                                        lp:_self.app.lp.contactsView,
                                        isAdmin:_self.options.isAdmin
                                    }
                                );
                                _self.index_contactsView.load();
                                jQuery(".openDiv").show();
                                break;
                            case "opportunity":
                                if(_self.index_opportunityView) delete _self.index_opportunityView;
                                _self.index_opportunityView =  new MWF.xApplication.CRM.Index.ChanceView(
                                    jQuery(".openDiv")[0],
                                    {},
                                    _self.app,
                                    _self,
                                    { templateUrl : "/x_component_CRM/$Chance/chanceView.json",filterData:{}},
                                    {
                                        lp:_self.app.lp.chance,
                                        isAdmin:_self.options.isAdmin
                                    }
                                );
                                _self.index_opportunityView.load();
                                jQuery(".openDiv").show();
                                break;
                            case "record":
                                _self.getRecordCount(filter);
                                break;
                        }
                    });
                });
            }
        }.bind(_self));
    },
    getFenBuMap: function(){
        var myChart = echarts.init(document.getElementById('cityMap'));
        debugger
        var provinces = ['shanghai', 'hebei', 'shanxi', 'neimenggu', 'liaoning', 'jilin', 'heilongjiang', 'jiangsu', 'zhejiang', 'anhui', 'fujian', 'jiangxi', 'shandong', 'henan', 'hubei', 'hunan', 'guangdong', 'guangxi', 'hainan', 'sichuan', 'guizhou', 'yunnan', 'xizang', 'shanxi1', 'gansu', 'qinghai', 'ningxia', 'xinjiang', 'beijing', 'tianjin', 'chongqing', 'xianggang', 'aomen'];
        var provincesText = ['上海', '河北', '山西', '内蒙古', '辽宁', '吉林', '黑龙江', '江苏', '浙江', '安徽', '福建', '江西', '山东', '河南', '湖北', '湖南', '广东', '广西', '海南', '四川', '贵州', '云南', '西藏', '陕西', '甘肃', '青海', '宁夏', '新疆', '北京', '天津', '重庆', '香港', '澳门'];
        // 全国省份数据
        var toolTipData =[];
        /*var toolTipData = [{
            "provinceName": "北京",
            "count": 58
        }, {
            "provinceName": "内蒙古",
            "count": 0
        }, {
            "provinceName": "宁夏",
            "count": 14
        }, {
            "provinceName": "新疆",
            "count": 4
        }]*/
        this.actions.countCustomerByProvince(function (json) {
            if(json.type=="success"){
                toolTipData = json.data;
            }
        });
        var seriesData = [];
        for (var j = 0; j < provincesText.length; j++) {
            var provinceName = provincesText[j];
            var pcount = 0;
            for (var i = 0; i < toolTipData.length; i++) {
                if(provinceName==toolTipData[i].provinceName){
                    pcount = toolTipData[i].count;
                }
            }
            seriesData[j] = {};
            seriesData[j].name = provinceName;
            seriesData[j].value = pcount;
        }



        var max = Math.max.apply(Math, seriesData.map(function(o) {
                return o.value
            })),
            min = 0; // 侧边最大值最小值
        var maxSize4Pin = 40,
            minSize4Pin = 30;
        var pName = "china";
        var Chinese_ = "中国";
        var tmpSeriesData = pName === "china" ? seriesData : seriesData;
        //var tmp = pName === "china" ? toolTipData : provinceData;
        var tmp = pName === "china" ? seriesData : toolTipData;
        var option = {
            /*title: {
                text: Chinese_ || pName,
                left: 'center'
            },*/
            tooltip: {
                trigger: 'item',
                formatter: function(params) { // 鼠标滑过显示的数据
                    if (pName === "china") {
                        var toolTiphtml = ''
                        for (var i = 0; i < tmp.length; i++) {
                            if (params.name == tmp[i].name) {
                                toolTiphtml += tmp[i].name +  '<br>客户数：' + tmp[i].value;
                            }
                        }
                        return toolTiphtml;
                    } else {
                        var toolTiphtml = ''
                        for (var i = 0; i < tmp.length; i++) {
                            if (params.name == tmp[i].cityName) {
                                toolTiphtml += tmp[i].cityName +  '<br>客户数：' + tmp[i].value;
                            }
                        }
                        return toolTiphtml;
                    }
                }
            },
            visualMap: { //视觉映射组件
                show: true,
                min: min,
                max: max, // 侧边滑动的最大值，从数据中获取
                left: '5%',
                top: '70%',
                inverse: true, //是否反转 visualMap 组件
                // itemHeight:200,  //图形的高度，即长条的高度
                text: ['高', '低'], // 文本，默认为数值文本
                calculable: false, //是否显示拖拽用的手柄（手柄能拖拽调整选中范围）
                seriesIndex: 1, //指定取哪个系列的数据，即哪个系列的 series.data,默认取所有系列
                orient: "horizontal",
                inRange: {
                    color: ['#dbfefe', '#1066d5'] // 蓝绿
                }
            },
            geo: {
                show: true,
                map: pName,
                roam: false,
                top:"0%",
                //aspectScale: 0.75,       //长宽比
                label: {
                    normal: {
                        show: false
                    },
                    emphasis: {
                        show: false,
                    }
                },
                itemStyle: {
                    normal: {
                        areaColor: '#f5f7fa', // 没有值得时候颜色
                        borderColor: '#097bba',
                    },
                    emphasis: {
                        areaColor: '#fbd456', // 鼠标滑过选中的颜色
                    }
                }
            },
            series: [
               {
                name: '散点',
                type: 'scatter',
                coordinateSystem: 'geo',
                data: tmpSeriesData,
                symbolSize: '1',
                label: {
                    normal: {
                        show: true,
                        formatter: '{b}',
                        position: 'right'
                    },
                    emphasis: {
                        show: true
                    }
                },
                itemStyle: {
                    normal: {
                        color: '#895139' // 字体颜色
                    }
                }
            },
                {
                    name: Chinese_ || pName,
                    type: 'map',
                    mapType: pName,
                    roam: false, //是否开启鼠标缩放和平移漫游
                    data: tmpSeriesData,
                    top: "0%",//组件距离容器的距离
                    // geoIndex: 0,
                     //aspectScale: 0.75,       //长宽比
                    // showLegendSymbol: false, // 存在legend时显示
                    selectedMode: 'single',
                    label: {
                        normal: {
                            show: true, //显示省份标签
                            textStyle: {
                                color: "#895139"
                            } //省份标签字体颜色
                        },
                        emphasis: { //对应的鼠标悬浮效果
                            show: true,
                            textStyle: {
                                color: "#323232"
                            }
                        }
                    },
                    itemStyle: {
                        normal: {
                            borderWidth: .5, //区域边框宽度
                            borderColor: '#0550c3', //区域边框颜色
                            areaColor: "#0b7e9e", //区域颜色
                        },
                        emphasis: {
                            borderWidth: .5,
                            borderColor: '#4b0082',
                            areaColor: "#ece39e",
                        }
                    }
                },
            ]
        };

        myChart.setOption(option);
        /* 响应式 */
        jQuery(window).resize(function() {
            myChart.resize();
        });

        myChart.off("click");
    },

    getCustomerCount: function(filter){
        _self = this;
        var xdata = [];
        var ydata = [];
        _self.actions.countCustomerByMonth( filter, function (json) {
            debugger
            if(json.type=="success"){
                json.data.each(function (data ) {
                    xdata.push(data.month);
                    ydata.push(data.count);

                });
            }
        });
        var myChart = echarts.init(jQuery("#costomerCount")[0]);
        var option = {
            calculable: true,
            grid: {
                y: 115,
                y2: 115
            },
            //  ------  X轴 ------
            xAxis: {
                show: true,  // 是否显示
                position: 'bottom',  // x轴的位置
                offset: 0, // x轴相对于默认位置的偏移
                type: 'category',   // 轴类型， 默认为 'category'
                //name: '月份',    // 轴名称
                nameLocation: 'end',  // 轴名称相对位置
                nameTextStyle: {   // 坐标轴名称样式
                    color: '#333',
                    padding: [5, 0, 0, -5]
                },
                nameGap: 5, // 坐标轴名称与轴线之间的距离
                nameRotate: 0,  // 坐标轴名字旋转
                axisLine: {       // 坐标轴 轴线
                    show: true,  // 是否显示
                    // ------   线 ---------
                    lineStyle: {
                        color: '#333',//blue
                        width: 1,
                        type: 'solid'
                    }
                },
                axisTick: {    // 坐标轴 刻度
                    show: false,  // 是否显示
                    inside: true,  // 是否朝内
                    length: 3,     // 长度
                    lineStyle: {   // 默认取轴线的样式
                        color: '#333',//red
                        width: 1,
                        type: 'solid'
                    }
                },
                axisLabel: {    // 坐标轴标签
                    show: true,  // 是否显示
                    inside: false, // 是否朝内
                    rotate: 0, // 旋转角度
                    margin: 5, // 刻度标签与轴线之间的距离
                    color: '#333'  // 默认取轴线的颜色red
                },
                splitLine: {    // gird区域中的分割线
                    show: false,  // 是否显示
                    lineStyle: {
                        // color: 'red',
                        // width: 1,
                        // type: 'solid'
                    }
                },
                splitArea: {    // 网格区域
                    show: false  // 是否显示，默认为false
                },
                data:xdata
                //data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
            },
            //   ------   y轴  ----------
            yAxis: {
                show: true,  // 是否显示
                position: 'left', // y轴位置
                offset: 0, // y轴相对于默认位置的偏移
                type: 'value',  // 轴类型，默认为 ‘category’
                name: '客户数',   // 轴名称
                nameLocation: 'end', // 轴名称相对位置value
                nameTextStyle: {    // 坐标轴名称样式
                    color: '#333',
                    padding: [5, 0, 0, 5]  // 坐标轴名称相对位置
                },
                nameGap: 15, // 坐标轴名称与轴线之间的距离
                nameRotate: 360,  // 坐标轴名字旋转

                axisLine: {    // 坐标轴 轴线
                    show: true,  // 是否显示

                    // ----- 线 -------
                    lineStyle: {
                        color: '#333',
                        width: 1,
                        type: 'solid'
                    }
                },
                axisTick: {      // 坐标轴的刻度
                    show: false,    // 是否显示
                    inside: true,  // 是否朝内
                    length: 3,      // 长度
                    lineStyle: {
                        color: '#333',  // 默认取轴线的颜色
                        width: 1,
                        type: 'solid'
                    }
                },
                axisLabel: {      // 坐标轴的标签
                    show: true,    // 是否显示
                    inside: false,  // 是否朝内
                    rotate: 0,     // 旋转角度
                    margin: 8,     // 刻度标签与轴线之间的距离
                    color: '#333',  // 默认轴线的颜色
                },
                splitLine: {    // gird 区域中的分割线
                    show: false,   // 是否显示
                    lineStyle: {
                        color: '#666',
                        width: 1,
                        type: 'dashed'
                    }
                },
                splitArea: {     // 网格区域
                    show: false   // 是否显示，默认为false
                }
            },
            //  -------   内容数据 -------
            series: [
                {
                    name: '客户数',      // 序列名称
                    type: 'bar',      //类型
                    legendHoverLink: true,  // 是否启用图列 hover 时的联动高亮
                    label: {   // 图形上的文本标签
                        show: false,
                        position: 'insideTop', // 相对位置
                        rotate: 0,  // 旋转角度
                        color: '#eee'
                    },
                    itemStyle: {    // 图形的形状
                        color: '#A3C7F7',
                        barBorderRadius: [0, 0, 0 ,0]
                    },
                    barWidth: 20,  // 柱形的宽度
                    barCategoryGap: '20%',  // 柱形的间距
                    data: ydata
                    //data: [3000, 4000, 4200, 4500, 6000, 5600, 8500, 5020, 4500, 5400, 4300, 1200]
                }
            ]
        };
        myChart.setOption(option);
    },
    getCustomerByIndustry: function(filter){
        _self = this;
        var xdata = [];
        var ydata = [];
        var typeList = this.app.lp.customer.industry.value;
        var typeArr = typeList.split(",");
        var otherCount = 0;
        _self.actions.countCustomerByIndustry( filter, function (json) {
            debugger
            if(json.type=="success"){
                json.data.each(function (data ) {
                    var industry = data.industry;
                    if(typeArr.contains(industry)){
                        xdata.push(industry);
                        ydata.push({value:data.count,name:industry})
                    }else{
                        otherCount = otherCount+(data.count).toInt();
                    }
                });
                if(otherCount>0){
                    xdata.push('未知');
                    ydata.push({value:otherCount,name:'未知'})
                }
            }
        });
        var myChart = echarts.init(jQuery("#costomerIndustryCount")[0]);
        var option = {
           /* title: {//标题组件
                text: '故障',
                left:'50px',//标题的位置 默认是left，其余还有center、right属性
                textStyle: {
                    color: "#436EEE",
                    fontSize: 17,
                }
            },*/
            tooltip : { //提示框组件
                trigger: 'item', //触发类型(饼状图片就是用这个)
                //formatter: "{a} <br/>{b} : {c} ({d}%)" //提示框浮层内容格式器
                formatter: "{b} : {c}" //提示框浮层内容格式器
            },
            color:['#48cda6','#ffa500','#11abff','#ffdf6f','#968ade','#C9C9C9','#7CFC00','#BF600D','#96E2F5'],  //手动设置每个图例的颜色
            legend: {  //图例组件
                //right:100,  //图例组件离右边的距离
                orient : 'horizontal',  //布局  纵向布局 图例标记居文字的左边 vertical则反之
                width:40,      //图行例组件的宽度,默认自适应
                x : 'right',   //图例显示在右边
                y: 'center',   //图例在垂直方向上面显示居中
                itemWidth:10,  //图例标记的图形宽度
                itemHeight:10, //图例标记的图形高度
                //data:['正常','一般','提示','较急','特急'],
                data:xdata,
                textStyle:{    //图例文字的样式
                    color:'#333',  //文字颜色
                    fontSize:12    //文字大小
                }
            },
            series : [ //系列列表
                {
                    name:'设备状态',  //系列名称
                    type:'pie',   //类型 pie表示饼图
                    center:['35%','45%'], //设置饼的原心坐标 不设置就会默认在中心的位置
                    radius : ['40%', '55%'],  //饼图的半径,第一项是内半径,第二项是外半径,内半径为0就是真的饼,不是环形
                    itemStyle : {  //图形样式
                        normal : { //normal 是图形在默认状态下的样式；emphasis 是图形在高亮状态下的样式，比如在鼠标悬浮或者图例联动高亮时。
                            label : {  //饼图图形上的文本标签
                                show : true,  //平常不显示
                                textStyle:{color:'#3c4858',fontSize:"18"},
                                formatter:function(val){
                                    return '{b|' + val.name + '}'
                                },
                                rich: {
                                    b: {fontSize: 15}
                                }
                            },
                            labelLine:{
                                show:true
                                //lineStyle:{color:'#45bcf2'}
                            }
                        },
                        emphasis : {   //normal 是图形在默认状态下的样式；emphasis 是图形在高亮状态下的样式，比如在鼠标悬浮或者图例联动高亮时。
                            label : {  //饼图图形上的文本标签
                                show : true,
                                position : 'center',
                                textStyle : {
                                    fontSize : '10',
                                    fontWeight : 'bold'
                                }
                            }
                        }
                    },
                    data:ydata
                    /*data:[
                        {value:1, name:'正常'},
                        {value:10, name:'一般'},
                        {value:30, name:'提示'},
                        {value:20, name:'较急'},
                        {value:25, name:'特急'}
                    ]*/
                }
            ]
        };
        myChart.setOption(option);
    },

    getDateList: function(stime){
        _self = this;
        var dateList = "";
        switch (stime) {
            case "今天":
                dateList = this.formatDate(this.now)+","+this.formatDate(this.now);
                break;
            case "本周":
                dateList = this.getWeekStartDate()+","+this.getWeekEndDate();
                break;
            case "本月":
                dateList = this.getMonthStartDate()+","+this.getMonthEndDate();
                break;
            case "本季度":
                dateList = this.getQuarterStartDate()+","+this.getQuarterEndDate();
                break;
            case "本年":
                dateList = this.nowYear+"-01-01,"+this.nowYear+"-12-31";
                break;
            default:
                dateList = jQuery(_self.rightContentDiv).find(".se-select-name").text().replace("--",",");
        }
        return dateList;
    },
    //格局化日期：yyyy-MM-dd
    formatDate:function(date) {
        var myyear = date.getFullYear();
        var mymonth = date.getMonth()+1;
        var myweekday = date.getDate();

        if(mymonth < 10){
            mymonth = "0" + mymonth;
        }
        if(myweekday < 10){
            myweekday = "0" + myweekday;
        }
        return (myyear+"-"+mymonth + "-" + myweekday);
    },
    //获得某月的天数
    getMonthDays:function(myMonth){
        var monthStartDate = new Date(this.nowYear, myMonth, 1);
        var monthEndDate = new Date(this.nowYear, myMonth + 1, 1);
        var days = (monthEndDate - monthStartDate)/(1000 * 60 * 60 * 24);
        return days;
    },
    //获得本季度的开端月份
    getQuarterStartMonth:function(){
        var quarterStartMonth = 0;
        if(this.nowMonth<3){
            quarterStartMonth = 0;
        }
        if(2<this.nowMonth && this.nowMonth<6){
            quarterStartMonth = 3;
        }
        if(5<this.nowMonth && this.nowMonth<9){
            quarterStartMonth = 6;
        }
        if(this.nowMonth>8){
            quarterStartMonth = 9;
        }
        return quarterStartMonth;
    },
    //获得本周的开端日期
    getWeekStartDate:function() {
        var weekStartDate = new Date(this.nowYear, this.nowMonth, (this.nowDay - this.nowDayOfWeek)+1);
        return this.formatDate(weekStartDate);
    },
    //获得本周的停止日期
    getWeekEndDate:function() {
        var weekEndDate = new Date(this.nowYear, this.nowMonth, this.nowDay + (7 - this.nowDayOfWeek));
        return this.formatDate(weekEndDate);
    },
    //获得本月的开端日期
    getMonthStartDate:function(){
        var monthStartDate = new Date(this.nowYear, this.nowMonth, 1);
        return this.formatDate(monthStartDate);
    },
    //获得本月的停止日期
    getMonthEndDate:function(){
        var monthEndDate = new Date(this.nowYear, this.nowMonth, this.getMonthDays(this.nowMonth));
        return this.formatDate(monthEndDate);
    },
    //获得本季度的开端日期
    getQuarterStartDate:function(){

        var quarterStartDate = new Date(this.nowYear, this.getQuarterStartMonth(), 1);
        return this.formatDate(quarterStartDate);
    },
    //或的本季度的停止日期
    getQuarterEndDate:function(){
        var quarterEndMonth = this.getQuarterStartMonth() + 2;
        var quarterStartDate = new Date(this.nowYear, quarterEndMonth, this.getMonthDays(quarterEndMonth));
        return this.formatDate(quarterStartDate);
    },

    loadTimeContainer: function(stime){
        jQuery("#"+stime).ymdateplugin({
            showTimePanel: false
        });
    },
    selectPerson: function (showContainer,nameId,fullNameId,count) {
        var options = {
            "type" : "",
            "types": ["person","unit"],
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
                            this.configData.push(personJson);
                        }
                    }.bind(this));
                    if(items.length==0){
                        //document.getElementById(nameId).innerHTML = "+点击选择"
                        if(jQuery(".headTitleinDiv").text()!="本人及下属"){
                            document.getElementById(nameId).innerHTML = "";
                            document.getElementById(fullNameId).innerHTML = "";
                            jQuery(".headTitleinDiv").text("本人及下属");
                            _self.loadDataContent();
                        }
                    }else{
                        document.getElementById(nameId).innerHTML = invitePersonList.join(",");
                        if(fullNameId!=""){
                            document.getElementById(fullNameId).innerHTML = fullPersonList.join(",");
                            var personCount = [];
                            var departCount = [];
                            var puhtml = "";
                            for(var n=0;n<fullPersonList.length;n++){
                                if(fullPersonList[n].contains("@P")){
                                    personCount.push(fullPersonList[n])
                                }
                                if(fullPersonList[n].contains("@U")){
                                    departCount.push(fullPersonList[n])
                                }
                            }
                            if(personCount.length>0){
                                puhtml = personCount.length+"个员工";
                                jQuery("#"+fullNameId).attr("personList",personCount.join(","));
                            }
                            if(departCount.length>0){
                                if(personCount.length>0){
                                    puhtml = puhtml+","+departCount.length+"个部门"
                                }else{
                                    puhtml = departCount.length+"个部门"
                                }
                                jQuery("#"+fullNameId).attr("departList",departCount.join(","));
                            }
                            if(puhtml!=""){
                                jQuery(".headTitleinDiv").text(puhtml);
                                _self.loadDataContent();
                                if(_self.dropdown) _self.dropdown.setStyles({"top":(jQuery(".user_change").offset().top+52)+"px","left":jQuery(".user_change").offset().left+2});
                            }

                        }
                    }

                }.bind(this));
            }.bind(this)
        };
        var selector = new MWF.O2Selector(showContainer, options);
    },
    getRecordCount:function(filter){
        _self = this;
        jQuery(".openDiv").show();
        jQuery(".openDiv").append('<div class="headNode"><span class="title">销售简报-新增跟进记录</span><img class="close" src="/x_component_CRM/$Template/close.png"></div>');

        var listCountHtml = '<div class="lsitBody"><table class="el-table__bd" style="width:100%;border-collapse: collapse;"><tbody><tr class="el-table__row current-row">'+
            '<td class="firstCol"><div class="cell">模块</div></td><td><div class="cell">新增跟进记录</div></td></tr></tbody></table></div>'
        jQuery(".openDiv").append(listCountHtml);
        var addTR = '';
        _self.actions.countGroupByTypes( filter, function (json) {
            if(json.type=="success"){
                json.data.each(function (data ) {
                    var typesname = data.typesname;
                    if(typesname!=""){
                        addTR = addTR+'<tr class="el-table__row"><td class="firstCol"><div class="cell">'+typesname+'</div></td><td mid="'+data.types+'"><div class="cell el-tooltip">'+data.count+'</div></td></tr>'
                    }
                });

            }
        });

        jQuery(".lsitBody").find("tbody").append(addTR);
        jQuery(".headNode").find(".close").click(function(){
            jQuery(".openDiv").empty();
            jQuery(".openDiv").hide();
        });
        jQuery("td[mid]").css("cursor","pointer");
        jQuery("td[mid]").click(function(){
            var openId = jQuery(this).attr("mid");
            if(jQuery(".notify").length > 0){
                jQuery(".notify").remove();
            }
            MWF.xDesktop.requireApp("CRM", "Record", function(){
                _self.explorer = new MWF.xApplication.CRM.Record(_self, _self.actions,{},{
                    "openId":openId,
                    "openName":"跟进记录",
                    "filter":filter,
                    "onReloadView" : function(){
                    }.bind(_self)
                });
                _self.explorer.load();
            }.bind(_self))
        });
    },
    resizeWindow:function(){
        var size = this.rightContentDiv.getSize();
        /*var rSize = this.headTitleDiv.getSize();
        var lSize = this.headBottonDiv.getSize();
        if(this.headSearchDiv){
            var x = this.headSearchDiv.getSize().x;
            this.headSearchDiv.setStyles({"margin-left":(size.x-rSize.x-lSize.x)/2-(x/2)+"px"});
        }*/
        //if(this.contentListDiv)this.contentListDiv.setStyles({"height":(size.y-this.headContentDiv.getHeight()-8)+"px"});
        //if(this.contentListInDiv)this.contentListInDiv.setStyles({"height":this.contentListDiv.getHeight()+"px"});

    }

});


MWF.xApplication.CRM.Index.View = new Class({
    Extends: MWF.xApplication.CRM.Template.ComplexViewOpen,

    _getCurrentPageData: function(callback, count, page, searchText,searchType){
        //var category = this.category = this.options.category;
        if (!count)count = 15;
        if (!page)page = 1;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";

        //if(id=="(0)")this.app.createShade();
        var filter = this.options.filterData || {};
        filter={key: searchText?searchText.trim():"",
            orderFieldName: "updateTime",
            orderType: "desc"
        };
        if (!searchType)searchType = "全部客户";
        if(!this.isAdmin){
            debugger
            if(searchType=="我负责的客户"){
                this.actions.ListMyDuty_customer(page, count, filter, function (json) {
                    if (callback)callback(json);
                }.bind(this));
            }
            if(searchType=="下属负责的客户"){
                this.actions.ListNestedSubPerson_customer(page, count, filter, function (json) {
                    if (callback)callback(json);
                }.bind(this));
            }
            if(searchType=="我参与的客户"){
                this.actions.ListMyParticipate_customer(page, count, filter, function (json) {
                    if (callback)callback(json);
                }.bind(this));
            }
            if(searchType=="全部客户"){
                this.actions.ListAllMy_customer(page, count, filter, function (json) {
                    if (callback)callback(json);
                }.bind(this));
            }
        }else{
            debugger
            this.actions.getCustomerListPage(page, count, filter, function (json) {
                if (callback)callback(json);
            }.bind(this));
        }


    },
    _create: function(){

    },
    _openDocument: function(openId ,openName){
        /*MWF.xDesktop.requireApp("CRM", "ClueEdit", function(){
         this.explorer = new MWF.xApplication.CRM.ClueEdit(this, this.actions,{},{
         "clueId":clueId,
         "onReloadView" : function(  ){
         //alert(JSON.stringify(data))
         this.reload();
         }.bind(this)
         });
         this.explorer.load();
         }.bind(this))*/
        MWF.xDesktop.requireApp("CRM", "CustomerOpen", function(){
            this.explorer = new MWF.xApplication.CRM.CustomerOpen(this, this.actions,{},{
                "openId":openId,
                "openName":openName,
                "onReloadView" : function(  ){
                    //alert(JSON.stringify(data))
                    this.reload();
                }.bind(this)
            });
            this.explorer.load();
        }.bind(this))
    },
    _queryCreateViewNode: function(){

    },
    _postCreateViewNode: function( viewNode ){

    },
    _queryCreateViewHead:function(){

    },
    _postCreateViewHead: function( headNode ){

    }

});

MWF.xApplication.CRM.Index.ContactsView = new Class({
    Extends: MWF.xApplication.CRM.Template.ComplexViewOpen,

    _createDocument: function(data){
        return new MWF.xApplication.CRM.Clue.Document(this.viewNode, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count, page, searchText){
        var category = this.category = this.options.category;
        if (!count)count = 15;
        if (!page)page = 1;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";

        //if(id=="(0)")this.app.createShade();
        var filter = this.options.filterData || {};
        if(searchText){
            filter = {
                key:searchText
            };
        }
        debugger
        this.actions.getContactsListPage(page, count, filter, function (json) {
            debugger
            if (callback)callback(json);
        }.bind(this));

    },
    _create: function(){

    },
    _openDocument: function(openId ,openName){
        /*MWF.xDesktop.requireApp("CRM", "ClueEdit", function(){
         this.explorer = new MWF.xApplication.CRM.ClueEdit(this, this.actions,{},{
         "clueId":clueId,
         "onReloadView" : function(  ){
         //alert(JSON.stringify(data))
         this.reload();
         }.bind(this)
         });
         this.explorer.load();
         }.bind(this))*/
        MWF.xDesktop.requireApp("CRM", "ContactsOpen", function(){
            this.explorer = new MWF.xApplication.CRM.ContactsOpen(this, this.actions,{},{
                "openId":openId,
                "openName":openName,
                "onReloadView" : function(  ){
                    //alert(JSON.stringify(data))
                    this.reload();
                }.bind(this)
            });
            this.explorer.load();
        }.bind(this))
    },
    _openOtherDocument: function(openId ,openName){

        MWF.xDesktop.requireApp("CRM", "CustomerOpen", function(){
            this.explorer = new MWF.xApplication.CRM.CustomerOpen(this, this.actions,{},{
                "openId":openId,
                "openName":openName,
                "onReloadView" : function(  ){
                    //alert(JSON.stringify(data))
                    this.reload();
                }.bind(this)
            });
            this.explorer.load();
        }.bind(this))
    },
    _queryCreateViewNode: function(){

    },
    _postCreateViewNode: function( viewNode ){

    },
    _queryCreateViewHead:function(){

    },
    _postCreateViewHead: function( headNode ){

    }

});

MWF.xApplication.CRM.Index.ChanceView = new Class({
    Extends: MWF.xApplication.CRM.Template.ComplexViewOpen,
    initialize: function (container, data, app, explorer, options, para) {
        this.container = container;
        this.data = data||{};
        this.explorer = explorer;
        if( para ){
            this.app = app || para.app || this.explorer.app;
            this.lp = para.lp || this.explorer.lp || this.app.lp;
            this.css = para.css || this.explorer.css || this.app.css;
            this.actions = para.actions || this.explorer.actions || this.app.actions || this.app.restActions;
        }else{
            this.app = app || this.explorer.app;
            this.lp = this.explorer.lp || this.app.lp;
            this.css = this.explorer.css || this.app.css;
            this.actions = this.explorer.actions || this.app.actions || this.app.restActions;
        }

        /*template 外边传进来*/
        if (!options.templateUrl) {
            options.templateUrl = this.explorer.path + "listItem.json"
        } else if (options.templateUrl.indexOf("/") == -1) {
            options.templateUrl = this.explorer.path + options.templateUrl;
        }
        this.setOptions(options);

    },
    _createDocument: function(data){
        return new MWF.xApplication.CRM.Chance.Document(this.viewNode, data, this.explorer, this);
    },
    ayalyseTemplate: function () {
        MWF.getJSON(this.options.templateUrl, function (json) {
            this.template = json;
            console.log("this is template,",json);
        }.bind(this), false)
    },
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
        this.actions.getChanceByPage(page, count, filter, function (json) {
            if (callback)callback(json);
            //this.app.destroyShade();
        }.bind(this));

    },
    useTablePlugins: function (cpage,searchText) {
        console.log("this is useTablePlugins  page:"+cpage+";;;;text:"+searchText);
        if(jQuery(".laytable-box").length > 0) jQuery(".laytable-box").remove();
        var that = this;
        var cdata = [];
        var cols = [];
        var col = [];
        var sortField = "";
        var sortType = "";
        var chanceViewObject = this.template;
        var count = 10;
        sortField = chanceViewObject.sortField;
        sortType = chanceViewObject.sortType;
        if (!cpage)cpage = 1;

        /*for ( i in chanceListObject){
            cols.push(chanceListObject[i]);
        }*/
        cols = chanceViewObject.field;
        //cols.push(col);
        this._getCurrentPageData(function (json) {
            /*json.data.each(function (data ) {

                this.actions.getCustomerInfo(data.customerid, function(customer){
                    var newData = data;
                    newData.customername = customer.data.customername;
                    cdata.push(newData);
                }, function(){
                    cdata.push(data);
                }, true)
            }.bind(this));*/
            cdata = json.data;
            layui.config({
                base: '/x_component_CRM/$Template/plugins/table2/'
            }).use(['table2', "table2"], function () {
                var table = layui.table2;
                console.log(cols);
                var tableIns = table.render({
                    elem: "#contentTable",
                    data: cdata,
                    height: 600,
                    width: '100%',
                    page: {
                        align: 'right',
                        groups: 5,//显示连续页码数量
                        curr:1,
                        count: json.count,//总条数
                        limit:10,
                        limits:[10, 20, 30, 40, 50, 60, 70, 80, 90]
                    },
                    initSort: {
                        sortField: sortField,
                        sortType: sortType
                    },
                    cols:[cols]
                });

                that.container.getElements(".chanceId").forEach(
                    function (e,i) {
                        //console.log("this is chanceId click fun:::::",e,i);
                        e.addEvent("click",function(){
                            console.log(this);
                            that._openDocument(this.get("id"),this.text);
                        }.bind(e))
                    }
                );

                that.container.getElements(".customerId").forEach(
                    function (e,i) {
                        //console.log("this is customerid click fun:::::",e,i);
                        e.addEvent("click",function(){
                            console.log(this);
                            that._openCustomer(this.get("id"),this.text);
                        }.bind(e))
                    }
                );
                jQuery(".laytable-page-pagination").find("a").each(function(index,element){
                        jQuery(element).on("click", function () {
                            //cpage = parseInt(jQuery(element).attr("value"))+cpage;
                            var topage = 1;
                            if(jQuery(element).attr("value")=="-1" || jQuery(element).attr("value")=="+1"){
                                topage = parseInt(jQuery(element).attr("value"))+topage;
                            }else{
                                topage = parseInt(jQuery(element).text());
                            }

                            if(jQuery(element).attr("class")!="page-item page-last rayui-disabled" && jQuery(element).attr("class")!="page-item page-prev rayui-disabled"){
                                //that.useTablePlugins(topage);
                                var searchText = jQuery(".headSearchInput").val();
                                if(searchText!=""){
                                    that.useTablePlugins(topage,searchText);
                                }else{
                                    that.useTablePlugins(topage);
                                }
                            }

                        });
                    }
                );

                jQuery(".laytable-page-btnok").on("click", function () {
                    var cpage = parseInt(jQuery(".laytable-page-input").val());
                    var searchText = jQuery(".headSearchInput").val();
                    if(searchText!=""){
                        that.useTablePlugins(cpage,searchText);
                    }else{
                        that.useTablePlugins(cpage);
                    }
                    //that.useTablePlugins(cpage);
                });


                jQuery(".page-item").each(function(index,element){
                    if(jQuery(element).attr("value")==(cpage+"")){
                        jQuery(element).attr("class","page-item page-active");
                    }else{
                        if(jQuery(element).attr("value")!="-1" &&  jQuery(element).attr("value")!="+1"){
                            jQuery(element).attr("class","page-item");
                        }
                    }
                });
                var cCount = jQuery(".page-active").attr("value");
                var firstObj = jQuery(".page-prev").parent().next().find("a")[0];
                var lastObj = jQuery(".page-last").parent().prev().find("a")[0];
                if(parseInt(cCount)>parseInt(jQuery(firstObj).attr("value"))){
                    jQuery(".page-prev").attr("class","page-item page-prev");
                }else{
                    jQuery(".page-prev").attr("class","page-item page-prev rayui-disabled");
                }
                if(parseInt(cCount)==parseInt(jQuery(lastObj).attr("value"))){
                    jQuery(".page-last").attr("class","page-item page-last rayui-disabled");/////
                }else{
                    jQuery(".page-last").attr("class","page-item page-last");
                }
                jQuery(".laytable-page-input").attr("value",cpage+"");

            });


        }.bind(this),count, cpage,searchText);

    },
    _openDocument: function( id , name ){

        //if(this.customerRead){
        //    this.customerRead.load(documentData)
        //}else{
        MWF.xDesktop.requireApp("CRM", "ChanceOpen", function(){
            this.explorer = new MWF.xApplication.CRM.ChanceOpen(this, this.actions,{},{
                "openId":id,
                "openName":name,
                "lp":this.lp,
                "onReloadView" : function(  ){
                    //alert(JSON.stringify(data))
                    this.reload();
                }.bind(this)
            });
            this.explorer.load();
        }.bind(this))

    },
    _openCustomer: function(openId ,openName){

        MWF.xDesktop.requireApp("CRM", "CustomerOpen", function(){
            this.explorer = new MWF.xApplication.CRM.CustomerOpen(this, this.actions,{},{
                "openId":openId,
                "openName":openName,
                "onReloadView" : function(  ){
                    //alert(JSON.stringify(data))
                    this.reload();
                }.bind(this)
            });
            this.explorer.load();
        }.bind(this))
    },

});