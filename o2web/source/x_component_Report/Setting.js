MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xApplication.Report.SettingForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "report",
        "width": "1000",
        "height": "600",
        "minWidth" : 700,
        "minHeight" : 320,
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "maxAction" : true,
        "hasBottom": true,
        "draggable": true,
        "resizeable": true,
        "closeAction": true
    },
    createTab: function(){
        var _self = this;

        this.tabContainer = new Element("div.formTabContainer",{
            styles : this.css.formTabContainer
        }).inject(this.formNode);

        var tabNode = new Element("div.formTabNode", {
            "styles": this.css.formTabNode,
            "text" : this.lp.systemSetting
        }).inject(this.tabContainer);
        tabNode.addEvents({
            "mouseover" : function(){ if( _self.currentTabNode != this.node)this.node.setStyles(_self.css.formTabNode_over) }.bind({node : tabNode }),
            "mouseout" : function(){ if( _self.currentTabNode != this.node)this.node.setStyles(_self.css.formTabNode) }.bind({node : tabNode }),
            "click":function(){
                if( _self.currentTabNode )_self.currentTabNode.setStyles(_self.css.formTabNode);
                _self.currentTabNode = this.node;
                this.node.setStyles(_self.css.formTabNode_current);
                _self.sysContainer.setStyle("display","");
                _self.personContainer.setStyle("display","none");
            }.bind({ node : tabNode })
        });
        tabNode.setStyles( this.css.formTabNode_current );
        _self.currentTabNode = tabNode;

        var tabNode = new Element("div.tabNode", {
            "styles": this.css.formTabNode,
            "text" : this.lp.personSetting
        }).inject(this.tabContainer);
        tabNode.addEvents({
            "mouseover" : function(){ if( _self.currentTabNode != this.node)this.node.setStyles(_self.css.formTabNode_over) }.bind({node : tabNode }),
            "mouseout" : function(){ if( _self.currentTabNode != this.node)this.node.setStyles(_self.css.formTabNode) }.bind({node : tabNode }),
            "click":function(){
                if( _self.currentTabNode )_self.currentTabNode.setStyles(_self.css.formTabNode);
                _self.currentTabNode = this.node;
                this.node.setStyles(_self.css.formTabNode_current);
                _self.sysContainer.setStyle("display","none");
                _self.personContainer.setStyle("display","");
            }.bind({ node : tabNode })
        })
    },
    createContent: function () {
        this.formTopTextNode.set( "text", "工作汇报配置" );

        this.isAdmin = this.app.common.isAdmin();
        //if( this.isAdmin ) {
        //    this.createTab();
        //}

        this.formContentNode = new Element("div.formContentNode", {
            "styles": this.css.formContentNode
        }).inject(this.formNode);

        this.formTableContainer = new Element("div.formTableContainer", {
            "styles": this.css.formTableContainer
        }).inject(this.formContentNode);

        this.formTableArea = new Element("div.formTableArea", {
            "styles": this.css.formTableArea
        }).inject(this.formTableContainer);

        this._createTableContent();
    },
    _createTableContent: function(){
        if( this.isAdmin ){
            this.sysContainer = new Element("div.sysContainer").inject(this.formTableArea);
            this.loadSysSetting( this.sysContainer );

            //this.personContainer = new Element("div.personContainer", { styles : {"display":"none"} }).inject( this.formTableArea );
            //this.loadPersonSetting( this.personContainer );
        }else{
            //this.personContainer = new Element("div.personContainer", { styles : {"display":""} }).inject( this.formTableArea );
            //this.loadPersonSetting( this.personContainer );
        }
    },
    loadPersonSetting: function( container ){
        var boxStyle = (this.isEdited || this.isNew) ? "border:1px solid #ccc; border-radius: 4px;overflow: hidden;padding:8px;" : "";

        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top:40px;'>" +
            "<tr><td styles='formTableTitle' lable='defaultView' width='20%'></td>" +
            "    <td styles='formTableValue' colspan='3' width='80%'>" +
            "       <div item='defaultView' style='"+ boxStyle +"'></div>" +
            "   </td>" +
            "</tr>" +
        "</table>";
        container.set("html", html);

        MWF.UD.getDataJson("reportConfig", function (json) {
            MWF.xDesktop.requireApp("Template", "MForm", function () {
                this.personform = new MForm(container, json, {
                    usesNewVersion : true,
                    isEdited: this.isEdited || this.isNew,
                    style : "meeting",
                    hasColon : true,
                    itemTemplate: {
                        defaultView : {
                            //text : "默认视图", type : "radio", selectValue : ["toList", "toMonth","toDay"], selectText:["列表","月","日"]
                            text : "默认视图", type : "radio", selectValue : ["toList","toDay"], selectText:["列表","日"]
                        }
                    }
                }, this.app);
                this.personform.load();

            }.bind(this), true);
        }.bind(this));

    },
    loadSysSetting: function ( container ) {
        this.getSysData();

        var boxStyle = (this.isEdited || this.isNew) ? "border:1px solid #ccc; border-radius: 4px;overflow: hidden;padding:8px;" : "";

        //var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin:20px 0px;'>" +

            //"<tr><td styles='formTableTitle' colspan='4'>基本设置</td></tr>" +

            //"<tr><td lable='REPORT_ENABLE' width='20%'></td>" +
            //"    <td styles='formTableValue14' item='REPORT_ENABLE' colspan='3' width='80%'></td></tr>" +

            //"<tr><td lable='WEEKEND_IGNORE'></td>" +
            //"    <td styles='formTableValue14' item='WEEKEND_IGNORE' colspan='3'></td></tr>" +

            //"<tr><td lable='HOLIDAY_IGNORE'></td>" +
            //"    <td styles='formTableValue14' item='HOLIDAY_IGNORE' colspan='3' ></td></tr>" +

            //"<tr><td lable='PERSONREPORT_REMOVE_DUTY'></td>" +
            //"    <td styles='formTableValue14' item='PERSONREPORT_REMOVE_DUTY' colspan='3' ></td></tr>" +

            //"<tr><td lable='UNITREPORT_DUTY'></td>" +
            //"    <td styles='formTableValue14' item='UNITREPORT_DUTY' colspan='3' ></td></tr>" +
            //
            //"<tr><td lable='createDate'></td>" +
            //"    <td styles='formTableValue14' colspan='3' >" +
            //"       <span item='createDate' ></span>"+
            //"       <span item='createReport' ></span>"+
            //"</td></tr>" +
            //
            //"<tr><td colspan='4'><div style='border-bottom: 1px dashed #ccc; margin: 5px 0px;width: 100%;'></div></td></tr>" +
            //
            //"<tr><td styles='formTableTitle' colspan='4'>月报设置</td></tr>" +
            //
            //"<tr><td>是否启用：</td>" +
            //"    <td styles='formTableValue14' colspan='3' item='MONTHREPORT_ENABLE'></td></tr>" +

            //"<tr><td>发起时间：</td>" +
            //"    <td styles='formTableValue14' colspan='3' >"+
            //"       <span item='REPORT_MONTH_DAYTYPE'></span>" +
            //"       <span item='REPORT_MONTH_DAY'></span>" +
            //"       <span item='REPORT_MONTH_TIME'></span>" +
            //"   </td></tr>" +

            //"<tr><td>自动启动时间类型：</td>" +
            //"    <td styles='formTableValue14' colspan='3' item='AUTOCREATE_TYPE'>"+
            //"</td></tr>" +
            //
            //"<tr item='CRON_EXPRESSION_TR' style='display: "+( this.data.AUTOCREATE_TYPE == 'CUSTOMDATELIST' ? "none" : "" )+"'>" +
            //"   <td>自动启动时间表达式：<a styles='helpNode' href='/x_component_Report/$Setting/cron_express_description.html' target='_blank'></a></td>" +
            //"    <td styles='formTableValue14' colspan='3' item='CRON_EXPRESSION'>"+
            //"</td></tr>" +
            //
            //"<tr item='CUSTOM_DATELIST_TR' style='display: "+( this.data.AUTOCREATE_TYPE != 'CUSTOMDATELIST' ? "none" : "" )+"'><td>自动启动时间列表：</td>" +
            //"    <td styles='formTableValue14' colspan='3'>"+
            //"       <div item='CUSTOM_DATELIST' style='" + boxStyle +"'></div>" +
            //"</td></tr>" +
            //
            //"<tr><td>参与应用：</td>" +
            //"    <td styles='formTableValue14' colspan='3'>"+
            //"       <div item='REPORT_MONTH_MODULE' style='"+ boxStyle +"'></div>" +
            //"</td></tr>" +


            //"<tr><td>个人关联流程：</td>" +
            //"    <td styles='formTableValue14' colspan='3' item='PERSONMONTH_REPORT_WORKFLOW'>"+
            //"</td></tr>" +
            //"<tr><td>组织关联流程：</td>" +
            //"    <td styles='formTableValue14' colspan='3' item='UNITMONTH_REPORT_WORKFLOW'>"+
            //"</td></tr>" +

            //"<tr><td colspan='4'><div style='border-bottom: 1px dashed #ccc; margin: 5px 0px;width: 100%;'></div></td></tr>" +
            //
            //"<tr><td styles='formTableTitle' colspan='4'>周报设置</td></tr>" +
            //
            //"<tr><td>是否启用：</td>" +
            //"    <td styles='formTableValue14' colspan='3' item='WEEKREPORT_ENABLE'></td></tr>" +

            //"<tr><td styles=''>发起时间：</td>" +
            //"    <td styles='formTableValue14' colspan='3' >"+
            //"       <span item='REPORT_WEEK_DAYTYPE'></span>" +
            //"       <span item='REPORT_WEEK_DAY'></span>" +
            //"       <span item='REPORT_WEEK_TIME'></span>" +
            //"   </td></tr>" +

            //"<tr><td>参与应用：</td>" +
            //"    <td styles='formTableValue14' colspan='3'>"+
            //"       <div item='REPORT_WEEK_MODULE' style='"+ boxStyle +"'></div>" +
            //"</td></tr>" +

            //"<tr><td>个人关联流程：</td>" +
            //"    <td styles='formTableValue14' colspan='3' item='PERSONWEEK_REPORT_WORKFLOW'>"+
            //"</td></tr>" +
            //"<tr><td>组织关联流程：</td>" +
            //"    <td styles='formTableValue14' colspan='3' item='UNITWEEK_REPORT_WORKFLOW'>"+
            //"</td></tr>" +
            //
            //"<tr><td colspan='4'><div style='border-bottom: 1px dashed #ccc; margin: 5px 0px;width: 100%;'></div></td></tr>" +
            //
            //"<tr><td styles='formTableTitle' colspan='4'>日报设置</td></tr>" +
            //
            //"<tr><td>是否启用：</td>" +
            //"    <td styles='formTableValue14' colspan='3' item='DAYREPORT_ENABLE'></td></tr>" +

            //"<tr><td>发起时间：</td>" +
            //"    <td styles='formTableValue14' colspan='3' >"+
            //"       <span item='REPORT_DAY_DAYTYPE'></span>" +
            //"       <span item='REPORT_DAY_TIME'></span>" +
            //"   </td></tr>" +

            //"<tr><td>参与应用：</td>" +
            //"    <td styles='formTableValue14' colspan='3'>"+
            //"       <div item='REPORT_DAY_MODULE' style='"+ boxStyle +"'></div>" +
            //"</td></tr>" +

            //"<tr><td>个人关联流程：</td>" +
            //"    <td styles='formTableValue14' colspan='3' item='PERSONDAY_REPORT_WORKFLOW'>"+
            //"</td></tr>" +
            //"<tr><td>组织关联流程：</td>" +
            //"    <td styles='formTableValue14' colspan='3' item='UNITDAY_REPORT_WORKFLOW'>"+
            //"</td></tr>" +

            //"</table>";
        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin:20px 0px;'>" +

            "<tr><td>是否启用：</td>" +
            "    <td styles='formTableValue14' colspan='3' item='MONTHREPORT_ENABLE'></td></tr>" +

            "<tr><td>自动启动时间类型：</td>" +
            "    <td styles='formTableValue14' colspan='3' item='AUTOCREATE_TYPE'>"+
            "</td></tr>" +

            "<tr item='CRON_EXPRESSION_TR' style='display: "+( this.data.AUTOCREATE_TYPE == 'CUSTOMDATELIST' ? "none" : "" )+"'>" +
            "   <td>自动启动时间表达式：<a styles='helpNode' href='/x_component_Report/$Setting/cron_express_description.html' target='_blank'></a></td>" +
            "    <td styles='formTableValue14' colspan='3' item='CRON_EXPRESSION'>"+
            "</td></tr>" +

            "<tr item='CUSTOM_DATELIST_TR' style='display: "+( this.data.AUTOCREATE_TYPE != 'CUSTOMDATELIST' ? "none" : "" )+"'><td>自动启动时间列表：</td>" +
            "    <td styles='formTableValue14' colspan='3'>"+
            "       <div item='CUSTOM_DATELIST' style='" + boxStyle +"'></div>" +
            "</td></tr>" +

            "<tr><td lable='WEEKEND_IGNORE'></td>" +
            "    <td styles='formTableValue14' item='WEEKEND_IGNORE' colspan='3'></td></tr>" +

            "<tr><td lable='HOLIDAY_IGNORE'></td>" +
            "    <td styles='formTableValue14' item='HOLIDAY_IGNORE' colspan='3' ></td></tr>" +


            "<tr><td lable='UNITREPORT_DUTY'></td>" +
            "    <td styles='formTableValue14' item='UNITREPORT_DUTY' colspan='3' ></td></tr>" +

            "<tr><td>参与应用：</td>" +
            "    <td styles='formTableValue14' colspan='3'>"+
            "       <div item='REPORT_MONTH_MODULE' style='"+ boxStyle +"'></div>" +
            "</td></tr>" +

            "<tr><td>关联流程：</td>" +
            "    <td styles='formTableValue14' colspan='3' item='UNITMONTH_REPORT_WORKFLOW'>"+
            "</td></tr>" +

            "<tr><td lable='createDate'></td>" +
            "    <td styles='formTableValue14' colspan='3' >" +
            "       <span item='createDate' ></span>"+
            "       <span item='createReport' ></span>"+
            "</td></tr>" +

            "</table>";

        container.set("html", html);

        this.itemTemplate = {
            //REPORT_ENABLE: { text : "开启汇报", type : "select", selectValue:["true", "false"], selectText : ["是","否"], notEmpty : true, style : { width : "100px" } },

            MONTHREPORT_ENABLE: { text : "开启月汇报", type : "select", selectValue:["true", "false"], selectText : ["是","否"], defaultValue : "false", style : { width : "100px" } },
            WEEKREPORT_ENABLE: { text : "开启周汇报", type : "select", selectValue:["true", "false"], selectText : ["是","否"], defaultValue : "false", style : { width : "100px" } },
            DAYREPORT_ENABLE: { text : "开启日汇报", type : "select", selectValue:["true", "false"], selectText : ["是","否"], defaultValue : "false", style : { width : "100px" } },

            createDate: {  text: "发起汇报", tType: "date", style: {width: "200px"} },
            createReport: { "text": "手工发起汇报", "value": "手工发起汇报", type: "button", event: {
                    click: function (item, ev) {
                        var date = this.sysform.getItem("createDate").getValue();
                        if( date == "" ){
                            this.app.notice("请先选择时间","error");
                        }else{
                            this.actions.createImmediately( {"date":date}, function(){
                                this.app.notice("发起汇报成功！");
                                this.sysform.getItem("createDate").setValue("");
                            }.bind(this))
                        }
                    }.bind(this)}
            },

            REPORT_MONTH_DAYTYPE: { text : "月报发起时间", type : "select", selectValue:["THIS_MONTH","NEXT_MONTH"], selectText:["当月","下月"], style : { width : "100px" }},
            REPORT_MONTH_DAY : { text : "每月汇报发起日期", type : "select", selectValue : function(){
                var arr = [];
                for( var i=1; i<=31; i++ )arr.push( i.toString() );
                return arr;
            }, selectText : function(){
                var arr = [];
                for( var i=1; i<=31; i++ )arr.push( i.toString() + "号" );
                return arr;
            }, style : { width : "100px" }},
            REPORT_MONTH_TIME : {  text : "每月汇报发起时间", tType : "time",  style : { width : "200px" }},

            REPORT_WEEK_DAYTYPE: { text : "周报", type : "select", selectValue:["THIS_WEEK","NEXT_WEEK"], selectText:["本周","下周"], style : { width : "100px" }},
            REPORT_WEEK_DAY: {text : "每周汇报发起日期", type : "select", selectValue : ["0","1","2","3","4","5","6"], selectValue : ["周日","周一","周二","周三","周四","周五","周六"], style : { width : "100px" }},
            REPORT_WEEK_TIME : {  text : "每周汇报发起时间", tType : "time",  style : { width : "200px" } },

            REPORT_DAY_DAYTYPE: { text : "日报", type : "select", selectValue:["NONE","TODAY","TOMORROW"], selectText:["无","当天","下一天"], style : { width : "100px" }},
            REPORT_DAY_TIME : {  text : "每日汇报发起时间", tType : "time", style : { width : "200px" } },

            WEEKEND_IGNORE :  { text : "忽略周末", type : "select", selectValue:["true", "false"], selectText : ["是","否"], notEmpty : true, style : { width : "100px" } },
            HOLIDAY_IGNORE :  { text : "忽略假日", type : "select", selectValue:["true", "false"], selectText : ["是","否"], notEmpty : true, style : { width : "100px" } },

            REPORT_MONTH_MODULE : { text : "参与月报的应用", type : "checkbox",
                selectValue : ["CMS","BBS","OKR","WORKFLOW","MEETTING","ATTENDANCE","STRATEGY"],
                selectText : ["内容管理","论坛","OKR","流程应用","会议管理","考勤","战略"]
            },
            REPORT_WEEK_MODULE : { text : "参与周报的应用", type : "checkbox",
                selectValue : ["CMS","BBS","OKR","WORKFLOW","MEETTING","ATTENDANCE","STRATEGY"],
                selectText : ["内容管理","论坛","OKR","流程应用","会议管理","考勤","战略"]
            },
            REPORT_DAY_MODULE : { text : "参与周报的应用", type : "checkbox",
                selectValue : ["CMS","BBS","OKR","WORKFLOW","MEETTING","ATTENDANCE","STRATEGY"], //"NONE",
                selectText : ["内容管理","论坛","OKR","流程应用","会议管理","考勤","战略"]
            },

            //MONTH_REPORT_WORKFLOW : { text : "月报流程", type : "org", orgType : "Process", className : "inputPlus" },
            PERSONMONTH_REPORT_WORKFLOW : { text : "个人月报流程", type : "org", orgType : "Process", className : "inputPlus" },
            UNITMONTH_REPORT_WORKFLOW : { text : "个人月报流程", type : "org", orgType : "Process", className : "inputPlus" },

            //WEEK_REPORT_WORKFLOW : { text : "周报流程", type : "org", orgType : "Process", className : "inputPlus" },
            PERSONWEEK_REPORT_WORKFLOW : { text : "个人周报流程", type : "org", orgType : "Process", className : "inputPlus" },
            UNITWEEK_REPORT_WORKFLOW : { text : "个人周报流程", type : "org", orgType : "Process", className : "inputPlus" },

            //DAY_REPORT_WORKFLOW : { text : "日报流程", type : "org", orgType : "Process", className : "inputPlus" },
            PERSONDAY_REPORT_WORKFLOW : { text : "个人日报流程", type : "org", orgType : "Process", className : "inputPlus" },
            UNITDAY_REPORT_WORKFLOW : { text : "组织日报流程", type : "org", orgType : "Process", className : "inputPlus" },

            PERSONREPORT_REMOVE_DUTY : { text : "个人汇报排除职务", type : "org", orgType : "Duty", count:0, className : "inputPlus" },
            UNITREPORT_DUTY : { text : "组织汇报指定职务", type : "org", orgType : "Duty", count:0, className : "inputPlus" },

            AUTOCREATE_TYPE : { text : "汇报自动生成时间类型", type : "select", selectValue:["EXPRESSION", "CUSTOMDATELIST"], selectText : ["时间表达式","时间列表"],
                notEmpty : true, style : { width : "100px" }, event :{
                    change : function( item, ev ){
                        if( item.getValue() == "EXPRESSION" ){
                            container.getElement("[item='CRON_EXPRESSION_TR']").setStyle("display","");
                            container.getElement("[item='CUSTOM_DATELIST_TR']").setStyle("display","none");
                        }else{
                            container.getElement("[item='CRON_EXPRESSION_TR']").setStyle("display","none");
                            container.getElement("[item='CUSTOM_DATELIST_TR']").setStyle("display","");
                        }
                    }.bind(this)
                }},
            CRON_EXPRESSION : { text : "时间表达式", validRule : {
                isNotEmpty : function( value , item ){
                    if( item.form.getItem("AUTOCREATE_TYPE").getValue() == "EXPRESSION" && !value)return false;
                    return true;
                }.bind(this) }, validMessage : { isNotEmpty : "时间表达式不能为空" }, onPostLoad : function( item ){
                    MWF.xDesktop.requireApp("Template", "widget.CronPicker", null, false);
                    new MWF.xApplication.Template.widget.CronPicker( this.app.content, item.container, this.app, {}, {
                        position : { //node 固定的位置
                            x : "right",
                            y : "auto"
                        },
                        onSelect : function( value ){
                            item.setValue(value);
                        }
                    } );
                }
            }
            //CUSTOM_DATELIST : { text : "时间列表" }

        };

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.sysform = new MForm(container, this.data, {
                isEdited: this.isEdited || this.isNew,
                style : "report",
                hasColon : true,
                itemTemplate: this.itemTemplate
            }, this.app);
            this.sysform.load();
        }.bind(this), true);

        var dateList = [];
        if( this.data  ){
            this.data.CUSTOM_DATELIST.split(",").each( function(d ){
                dateList.push( { CUSTOM_DATELIST : d } )
            })
        }
        MWF.xDesktop.requireApp("Template", "MGrid", function () {
            this.dateGrid = new MGrid( container.getElement("[item='CUSTOM_DATELIST']"), dateList || null , {
                style: "report",
                isEdited:  this.isEdited || this.isNew,
                hasOperation : true,
                minTrCount : 1,
                tableAttributes : { width : "550px", border : "0" , cellpadding : "5", cellspacing : "0" },
                itemTemplate: {
                    CUSTOM_DATELIST: {
                        tType : "datetime",
                        defaultValue: "请选择时间",
                        defaultValueAsEmpty: true,
                        event: {
                            focus: function (item, ev) {
                                if (item.getValue() == "请选择时间")item.setValue("")
                            }.bind(this),
                            blur: function (item, ev) {
                                if (item.getValue() == "")item.setValue("请选择时间")
                            }.bind(this)
                        }
                    }
                }
            }, this.app );
            this.dateGrid.setThTemplate("<tr><th style='text-align: center;font-size:14px;font-weight: normal;'>序号</th><th style='width:360px;text-align: center;font-size:14px;font-weight: normal;'>选择时间</th><th button_add></th></tr>");
            this.dateGrid.setTrTemplate( "<tr><td sequence style='text-align: center;vertical-align: top;padding-top:10px;'></td><td><div item='CUSTOM_DATELIST' style='padding-top:5px'></div></td><td button_remove style='vertical-align: top;padding-top:10px;'></td></tr>" );
            this.dateGrid.load();
        }.bind(this), true);
    },
    _createBottomContent: function () {

        if (this.isNew || this.isEdited) {

            this.okActionNode = new Element("button.inputOkButton", {
                "styles": this.css.inputOkButton,
                "text": this.lp.save
            }).inject(this.formBottomNode);

            this.okActionNode.addEvent("click", function (e) {
                this.save(e);
            }.bind(this));
        }

        this.cancelActionNode = new Element("button.inputCancelButton", {
            "styles": (this.isEdited || this.isNew ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
            "text": this.lp.close
        }).inject(this.formBottomNode);

        this.cancelActionNode.addEvent("click", function (e) {
            this.close(e);
        }.bind(this));

    },

    getSysData: function(){
        this.actions.listSetting( function( json ){
            this.decodeSysData( json.data );
        }.bind(this), null, false)
    },
    decodeSysData : function( data ){
        this.data = {};
        this.dataJson = {};
        data.each( function(d){
            var value = d.configValue;
            if( typeOf(value) == "string"){
                if( value == "NONE" )value = ""
            }else if( typeOf(value) == "array" ){
                for( var i=0; i<value.length-1; i++ ){
                    if( value[i] == "NONE" )value[i] = ""
                }
            }
            this.data[d.configCode] = value;
            this.dataJson[d.configCode] = d;
        }.bind(this));
    },
    encodeSysData : function( data ){
        var arr = [];
        for( var d in data ){
            if( this.dataJson[d] ){
                var result = Object.clone( this.dataJson[d] );
                var value = data[d];
                if( value == "" && (d == "REPORT_WEEK_MODULE" || d == "REPORT_MONTH_MODULE" || d=="REPORT_DAY_MODULE")  ){
                    value = "NONE";
                }
                var flowArray = [
                    "PERSONMONTH_REPORT_WORKFLOW","UNITMONTH_REPORT_WORKFLOW","MONTH_REPORT_WORKFLOW",
                    "PERSONWEEK_REPORT_WORKFLOW","UNITWEEK_REPORT_WORKFLOW","WEEK_REPORT_WORKFLOW",
                    "PERSONDAY_REPORT_WORKFLOW","UNITDAY_REPORT_WORKFLOW","DAY_REPORT_WORKFLOW"
                ];
                if( flowArray.contains( d ) ){
                    if( value == "" ){
                        value = "NONE";
                    }else{
                        if( this.sysform.getItem(d) && this.sysform.getItem(d).orgObject ){
                            value = this.sysform.getItem(d).orgObject[0].data.id;
                        }
                    }
                }
                if( value == "" && (d == "REPORT_WEEK_TIME" || d == "REPORT_MONTH_TIME" || d=="REPORT_DAY_TIME") ){
                    value = "--无--";
                }
                result.configValue = value;
                arr.push( result );
            }
        }
        // alert(JSON.stringify(arr))
        return arr;
    },
    save: function(e){
        if( this.personform ){
            var pdata = this.personform.getResult(true,null,true,false,false);
            if( pdata){
                MWF.UD.putData("reportConfig", pdata, function(){
                    if( this.sysform ){
                        this.saveSysData();
                    }else{
                        this.app.notice(this.lp.save_success, "success");
                    }
                }.bind(this), false);
            }
        }else if( this.sysform ){
            this.saveSysData();
        }

    },
    saveSysData: function(){
        var sdata = this.sysform.getResult(true,"|",true,false,false);
        if( sdata){
            var dateList = this.dateGrid.getResult(true,",",true,false,false);

            var dates = [];
            var f = true;
            dateList.each( function( d ){
                if( ( !d.CUSTOM_DATELIST || d.CUSTOM_DATELIST == "请选择时间" ) && sdata.AUTOCREATE_TYPE == "CUSTOMDATELIST" ){
                    f = flag;
                }
                dates.push( d.CUSTOM_DATELIST )
            });
            if( !f || dates.length == 0 ){
                this.app.notice("请选择启动时间", "error");
                return;
            }
            sdata.CUSTOM_DATELIST = dates.join(",");

            var arr = this.encodeSysData( sdata );
            var flag = true;
            arr.each( function( d ){
                this.app.restActions.saveSetting( d, function(json){
                }.bind(this), function( response ){
                    var json = JSON.decode( response.responseText );
                    this.app.notice( this.lp.save_fail + ":" + json.message, "error");
                    flag = false;
                }.bind(this), false);
            }.bind(this));
            if( flag ){
                this.app.notice(this.lp.save_success, "success");
            }else{
                //this.app.notice(this.lp.save_fail, "error");
            }
        }
    },
    setFormNodeSize: function (width, height, top, left) {
        if (!width)width = this.options.width ? this.options.width : "50%";
        if (!height)height = this.options.height ? this.options.height : "50%";
        if (!top) top = this.options.top ? this.options.top : 0;
        if (!left) left = this.options.left ? this.options.left : 0;

        var containerSize = this.container.getSize();
        if( containerSize.x < width )width = containerSize.x;
        if( containerSize.y < height )height = containerSize.y;

        var allSize = this.app.content.getSize();
        var limitWidth = allSize.x; //window.screen.width
        var limitHeight = allSize.y; //window.screen.height

        "string" == typeof width && (1 < width.length && "%" == width.substr(width.length - 1, 1)) && (width = parseInt(limitWidth * parseInt(width, 10) / 100, 10));
        "string" == typeof height && (1 < height.length && "%" == height.substr(height.length - 1, 1)) && (height = parseInt(limitHeight * parseInt(height, 10) / 100, 10));
        300 > width && (width = 300);
        220 > height && (height = 220);

        top = top || parseInt((limitHeight - height) / 2, 10); //+appTitleSize.y);
        left = left || parseInt((limitWidth - width) / 2, 10);

        this.formAreaNode.setStyles({
            "width": "" + width + "px",
            "height": "" + height + "px",
            "top": "" + top + "px",
            "left": "" + left + "px"
        });

        this.formNode.setStyles({
            "width": "" + width + "px",
            "height": "" + height + "px"
        });

        var iconSize = this.formIconNode ? this.formIconNode.getSize() : {x: 0, y: 0};
        var topSize = this.formTopNode ? this.formTopNode.getSize() : {x: 0, y: 0};
        var bottomSize = this.formBottomNode ? this.formBottomNode.getSize() : {x: 0, y: 0};
        var tabSize = this.tabContainer ? this.tabContainer.getSize() : {x: 0, y: 0};

        var contentHeight = height - iconSize.y - topSize.y - bottomSize.y - tabSize.y;
        //var formMargin = formHeight -iconSize.y;
        this.formContentNode.setStyles({
            "height": "" + contentHeight + "px"
        });
        this.formTableContainer.setStyles({
            "height": "" + contentHeight + "px"
        });
    }
});