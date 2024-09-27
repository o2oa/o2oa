MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Work = MWF.xApplication.process.Work || {};
MWF.xDesktop.requireApp("process.Work", "lp." + MWF.language, null, false);
MWF.ProcessFlow_ORG_HEIGHT = 275;
MWF.xApplication.process.Work.Flow  = MWF.ProcessFlow = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        style: "default",
        processEnable: true,
        addTaskEnable: true,
        resetEnable: true,
        goBackEnable: true,
        processOptions: {},
        mainColorEnable: true,
        zIndex: 20001 //比正文编辑器痕迹窗口多1
    },
    initialize: function (container, task, options, form) {
        this.setOptions(options);

        this.path = "../x_component_process_Work/$Flow/";
        // this.cssPath = "../x_component_process_Work/$Flow/" + this.options.style + "/css.wcss";
        // this._loadCss();

        this.lp = MWF.xApplication.process.Work.LP;

        this.task = task;
        this.container = $(container);

        this.form = form;
        this.businessData = this.form.businessData;

        this.load();
    },
    load: function () {
        this.container.loadCss( this.path + this.options.style + "/style.css", null, function () {
            this.cssLoaded = true;
            this.checkLoadEvent();
        }.bind(this));

        this.processEnable = this.options.processEnable && this.businessData.control["allowProcessing"];
        this.addTaskEnable = this.options.addTaskEnable && this.businessData.control["allowAddTask"];
        this.resetEnable = this.options.resetEnable && this.businessData.control["allowReset"];
        this.goBackEnable = this.options.goBackEnable && this.businessData.control["allowGoBack"];

        this.navi = [];
        if( this.processEnable )this.navi.push({ key: "process", label: this.lp.flowActions.process });
        if( this.addTaskEnable )this.navi.push({ key: "addTask", label: this.lp.flowActions.addTask });
        if( this.resetEnable )this.navi.push({ key: "reset", label: this.lp.flowActions.reset });
        if( this.goBackEnable )this.navi.push({ key: "goBack", label: this.lp.flowActions.goBack });

        var url = this.path+this.options.style+"/main.html";
        this.container.loadHtml(url, {"bind": {"lp": this.lp, "navi": this.navi}, "module": this}, function(){
            this.changeAction( this.navi[0].key );
            if( this.processEnable || this.resetEnable || this.addTaskEnable ){
                this.loadQuickSelect();
            }else{
                this.quickSelectNode.hide();
            }
        }.bind(this));
    },
    getSize: function(){
        return {
            y: this.contentWraper.getSize().y + this.getMarginY(this.contentWraper) + this.getOffsetY(this.node) + 1,
            x: this.node.getSize().x + this.getMarginY(this.node)
        };
    },
    checkLoadEvent: function(){
        if( this.cssLoaded && this.firstActionLoaded){
            if( this.currentAction === "process" ){
                if( this.processor.getMaxOrgLength() > 1 ){
                    this.node.addClass("o2flow-node-wide").removeClass("o2flow-node");
                }
            }
            this.fireEvent("load");
        }
    },
    changeAction: function( action, quickData ){
        debugger;
        if( this.currentAction ){
            this[ this.currentAction+"ContentNode" ].hide();
            this[ this.currentAction+"TitleNode" ].removeClass("o2flow-navi-item-active");
            if( this.options.mainColorEnable )this[ this.currentAction+"TitleNode" ].removeClass("mainColor_bg");
        }

        this[ action+"ContentNode" ].show();
        this[ action+"TitleNode" ].addClass("o2flow-navi-item-active");
        if( this.options.mainColorEnable )this[ action+"TitleNode" ].addClass("mainColor_bg");

        this.currentAction = action;

        switch (action) {
            case "process":
                this.loadProcessor( quickData );
                break;
            case "addTask":
                this.loadAddTask( quickData );
                break;
            case "reset":
                this.loadReset( quickData );
                break;
            case "goBack":
                this.loadGoBack();
                break;
        }
    },
    loadProcessor: function ( quickData ) {
        if( this.processor ){
            if( quickData )this.processor.setQuickData( quickData );
            this.resize();
            return;
        }
        this.processor = new MWF.ProcessFlow.Processor(
            this.processContentNode,
            this,
            Object.merge( this.options.processOptions, {
                onLoad: function () {
                    if( this.firstActionLoaded ){
                        this.resize();
                    }else{
                        this.firstActionLoaded = true;
                        this.checkLoadEvent();
                    }
                }.bind(this)
            })
        );
        this.processor.load( quickData );
    },
    loadReset: function( quickData ){
        if( this.reset ){
            if( quickData )this.reset.setQuickData( quickData );
            this.resize();
            return;
        }
        this.reset = new MWF.ProcessFlow.Reset(
            this.resetContentNode,
            this,
            Object.merge( this.options.resetOptions, {
                onLoad: function () {
                    if( this.firstActionLoaded ){
                        this.resize();
                    }else{
                        this.firstActionLoaded = true;
                        this.checkLoadEvent();
                    }
                }.bind(this)
            })
        );
        this.reset.load( quickData );
    },
    loadAddTask: function( quickData ){
        if( this.addTask ){
            if( quickData )this.addTask.setQuickData( quickData );
            this.resize();
            return;
        }
        this.addTask = new MWF.ProcessFlow.AddTask(
            this.addTaskContentNode,
            this,
            Object.merge( this.options.addTaskOptions, {
                onLoad: function () {
                    if( this.firstActionLoaded ){
                        this.resize();
                    }else{
                        this.firstActionLoaded = true;
                        this.checkLoadEvent();
                    }
                }.bind(this)
            })
        );
        this.addTask.load( quickData );
    },
    loadGoBack: function(){
        if( this.goBack ){
            //if( quickData )this.goBack.setQuickData( quickData );
            this.resize();
            return;
        }
        this.goBack = new MWF.ProcessFlow.GoBack(
            this.goBackContentNode,
            this,
            Object.merge( this.options.goBackOptions, {
                onLoad: function () {
                    if( this.firstActionLoaded ){
                        this.resize();
                    }else{
                        this.firstActionLoaded = true;
                        this.checkLoadEvent();
                    }
                }.bind(this)
            })
        );
        this.goBack.load();
    },
    loadQuickSelect: function(){
        if( !this.addTaskEnable && !this.resetEnable && !this.processEnable ){
            if(this.quickSelector)this.quickSelector.hide();
            return;
        }
        this.quickSelector = new MWF.ProcessFlow.widget.QuickSelect(
            this.form.app ? this.form.app.content : $(document.body),
            this.quickSelectNode,
            this.form.app,
            {},
            {
                nodeStyles: {
                    "z-index" : this.options.zIndex + 1
                }
            }
         );
        this.quickSelector.flow = this;
        this.contentScrollNode.addEvent("scroll", function () {
            if(this.quickSelector.status === "display")this.quickSelector.hide();
        }.bind(this))
    },
    submit: function(){
        switch ( this.currentAction ) {
            case "process":
                this.processor.submit();
                break;
            case "addTask":
                this.addTask.submit();
                break;
            case "reset":
                this.reset.submit();
                break;
            case "goBack":
                this.goBack.submit();
                break;
        }
    },
    destroy: function () {
        if( this.processor )this.processor.destroy();
        if( this.reset )this.reset.destroy();
        if( this.addTask )this.addTask.destroy();
        if( this.goBack )this.goBack.destroy();
        if(this.quickSelector)this.quickSelector.destroy();
        this.fireEvent("cancel");
    },
    getMarginY : function(node){
        return (node.getStyle("margin-top").toInt() || 0 ) +
            (node.getStyle("margin-bottom").toInt() || 0 );
    },
    getMarginX : function(node){
        return (node.getStyle("margin-left").toInt() || 0 ) +
            (node.getStyle("margin-right").toInt() || 0 );
    },
    getOffsetY : function(node){
        return (node.getStyle("margin-top").toInt() || 0 ) +
            (node.getStyle("margin-bottom").toInt() || 0 ) +
            (node.getStyle("padding-top").toInt() || 0 ) +
            (node.getStyle("padding-bottom").toInt() || 0 )+
            (node.getStyle("border-top-width").toInt() || 0 ) +
            (node.getStyle("border-bottom-width").toInt() || 0 );
    },
    getOffsetX : function(node){
        return (node.getStyle("margin-left").toInt() || 0 ) +
            (node.getStyle("margin-right").toInt() || 0 ) +
            (node.getStyle("padding-left").toInt() || 0 ) +
            (node.getStyle("padding-right").toInt() || 0 )+
            (node.getStyle("border-left-width").toInt() || 0 ) +
            (node.getStyle("border-right-width").toInt() || 0 );
    },
    getEl: function( ev, className ){
        var node = ev.target;
        while (node && !node.hasClass(className)){ node = node.getParent();}
        return node;
    },
    noticeError: function( text, node, offset, position ){
        MWF.xDesktop.notice(
            "error",
            position || {"x": "center", "y": "center"},
            text,
            node || this.node,
            offset === null ? null : ( offset || {"x": 0, "y": 30} ),
            {"closeOnBoxClick": true, "closeOnBodyClick": true, "fixed": true, "delayClose": 6000}
        );
    },
    resize: function () {
        this.fireEvent("resize");
    },
    getRouteGroupConfig: function () {
        if (this.routeGroupObject) return this.routeGroupObject;
        this.routeGroupObject = {};
        this.routeGroupNameList = [];
        this.hasRouteGroup = false;
        var routeList = this.getRouteConfigList();
        routeList.each(function (route, i) {

            if (route.hiddenScriptText && this.form && this.form.Macro) { //如果隐藏路由，返回
                if (this.form.Macro.exec(route.hiddenScriptText, this).toString() === "true") return;
            }

            if (route.displayNameScriptText && this.form && this.form.Macro) { //如果有显示名称公式
                route.displayName = this.form.Macro.exec(route.displayNameScriptText, this);
            } else {
                route.displayName = route.name;
            }

            if (route.decisionOpinion) {
                this.hasRouteGroup = true;
                route.decisionOpinion.split("#").each(function (rg) {
                    this.routeGroupNameList.combine([rg]);
                    var d = this.splitByStartNumber(rg);
                    if (!this.routeGroupObject[d.name]) this.routeGroupObject[d.name] = [];
                    this.routeGroupObject[d.name].push(route);
                }.bind(this))
            } else {
                var defaultName = MWF.xApplication.process.Work.LP.defaultDecisionOpinionName;
                this.routeGroupNameList.combine([defaultName]);
                if (!this.routeGroupObject[defaultName]) this.routeGroupObject[defaultName] = [];
                this.routeGroupObject[defaultName].push(route);
            }
        }.bind(this));
        return this.routeGroupObject;
    },
    getRouteConfigList: function () {
        if(this.routeConfigList)return this.routeConfigList;

        if (this.task.routeNameDisable){
            this.routeConfigList = [{
                "id": o2.uuid(),
                "asyncSupported": false,
                "soleDirect": false,
                "name": "继续流转",
                "alias": "",
                "selectConfigList": []
            }];
            return this.routeConfigList;
        }

        if( this.form && this.form.businessData && this.form.businessData.routeList ){
            this.form.businessData.routeList.sort( function(a, b){
                var aIdx = parseInt(a.orderNumber || "9999999");
                var bIdx = parseInt(b.orderNumber || "9999999");
                return aIdx - bIdx;
            }.bind(this));
            this.form.businessData.routeList.each( function(d){
                d.selectConfigList = JSON.parse(d.selectConfig || "[]");
            }.bind(this));
            this.routeConfigList = this.form.businessData.routeList;
        }
        if (!this.routeConfigList) {
            o2.Actions.get("x_processplatform_assemble_surface").listRoute({"valueList": this.task.routeList}, function (json) {
                json.data.sort(function(a, b){
                    var aIdx = parseInt(a.orderNumber || "9999999");
                    var bIdx = parseInt(b.orderNumber || "9999999");
                    return aIdx - bIdx;
                }.bind(this));
                json.data.each(function (d) {
                    d.selectConfigList = JSON.parse(d.selectConfig || "[]");
                }.bind(this));
                this.routeConfigList = json.data;
            }.bind(this), null, false);
        }
        return this.routeConfigList;
    },
    getRouteConfig: function (routeId) {
        var routeList = this.getRouteConfigList();
        for (var i = 0; i < routeList.length; i++) {
            if (routeList[i].id === routeId || routeList[i].name === routeId) {
                return routeList[i];
            }
        }
    },
    getOrgConfig: function (routeId) {
        var routeList = this.getRouteConfigList();
        for (var i = 0; i < routeList.length; i++) {
            if (routeList[i].id === routeId) {
                return routeList[i].selectConfigList;
            }
        }
    },
    getVisableOrgConfig: function (routeId) {
        var selectConfigList = this.getOrgConfig(routeId);
        var list = [];
        (selectConfigList || []).each(function (config) {
            if (!this.isOrgHidden(config)) {
                list.push(config);
            }
        }.bind(this));
        return list;
    },
    getSingleOrgConfig: function( routeId, orgName ){
        var orgList = this.getOrgConfig( routeId );
        for (var i = 0; i < orgList.length; i++) {
            if (orgList[i].name === orgName) {
                return orgList[i];
            }
        }
    },
    isOrgHidden: function (d) {
        if (d.hiddenScript && d.hiddenScript.code) { //如果隐藏路由，返回
            var hidden = this.form.Macro.exec(d.hiddenScript.code, this);
            if (hidden && hidden.toString() === "true") return true;
        }
        return false;
    },
    getMaxOrgLength: function () {
        var routeList = this.getRouteConfigList();
        var length = 0;
        routeList.each(function (route) {
            if (route.hiddenScriptText) { //如果隐藏路由，返回
                if (this.form.Macro.exec(route.hiddenScriptText, this).toString() === "true") return;
            }
            length = Math.max(length, this.getVisableOrgConfig( route.id ).length);
        }.bind(this));
        return length;
    },
    splitByStartNumber: function (str) {
        var obj = {
            name: "",
            order: ""
        };
        for (var i = 0; i < str.length; i++) {
            if (parseInt(str.substr(i, 1)).toString() !== "NaN") {
                obj.order = obj.order + str.substr(i, 1);
            } else {
                obj.name = str.substr(i, str.length);
                break;
            }
        }
        return obj;
    },
});

MWF.ProcessFlow.Reset = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options:{
        data: null,
        style: "default"
    },
    initialize: function (container, flow, options) {
        this.setOptions(options);
        this.container = $(container);
        this.flow = flow;
        this.task = flow.task;
        this.form = flow.form;
        this.lp = flow.lp;
        this.businessData = this.form.businessData;
    },
    load: function( quickData ){
        this.quickData = quickData || {};
        this.container.loadHtml(this.getUrl(), {"bind": {"lp": this.lp}, "module": this}, function(){
            this.loadOpinion();
            this.loadOrg();
            this.afterLoad();
            this.fireEvent("load");
        }.bind(this));
    },
    setQuickData: function( data ){
        //if(this.keepOption)this.keepOption.setValue( data.keepTask ? "true" : null );
        if(this.opinion)this.opinion.setValue( data.opinion || "" );
        if(this.selector && this.selector.selector)this.selector.selector.setValues( data.organizations ? (data.organizations.default || []) : [] );
    },
    getUrl: function(){
        return this.flow.path+this.flow.options.style+"/reset.html";
    },
    show: function(){

    },
    afterLoad: function () {
        // this.keepOption = new MWF.ProcessFlow.widget.Radio(this.keepOptionArea, this.flow, {
        //     optionList: [{
        //         text: this.lp.keepTask,
        //         value: "true"
        //     }],
        //     value: (this.quickData.keepTask) ? "true" : null
        // });
        // this.keepOption.load();
    },
    loadOpinion: function(){
        var opt = Object.clone(this.flow.options.opinionOptions);
        opt.isHandwriting = this.options.isHandwriting;
        if( this.quickData.opinion ){
            opt.opinion = this.quickData.opinion;
            delete this.quickData.opinion;
        }
        this.opinion = new MWF.ProcessFlow.widget.Opinion( this.opinionContent, this.flow, opt );
        this.opinion.load();
    },
    loadOrg: function(){
        this.getSelOptions( function (options) {
            options.values =  this.quickData.organizations ? (this.quickData.organizations.default || []) : [];
            if(this.quickData.organizations)delete this.quickData.organizations.default;
            this.selector = new MWF.O2Selector(this.orgsArea, options);
        }.bind(this) );
    },
    getSelOptions: function( callback ){
        // o2.Actions.get("x_processplatform_assemble_surface").listTaskByWork(this.businessData.work.id, function(json){
        //     var identityList = [];
        //     json.data.each(function(task){
        //         identityList.push(task.identity);
        //     });
        //     this._getSelOptions(identityList, callback);
        // }.bind(this))
        this._getSelOptions([], callback);
    },
    _getSelOptions: function (exclude, callback) {
        var options = this.getSelDefaultOptions();

        var range = this.businessData.activity.resetRange || "department";
        switch (range) {
            case "unit":
                options.units = this.businessData.task.unitDn ? [this.businessData.task.unitDn] : [];
                options.exclude = exclude;
                callback( options );
                break;
            case "topUnit":
                MWF.require("MWF.xScript.Actions.UnitActions", function () {
                    orgActions = new MWF.xScript.Actions.UnitActions();
                    var data = { "unitList": [this.businessData.task.unitDn] };
                    orgActions.listUnitSupNested(data, function (json) {
                        options.units = json.data[0] ? [json.data[0]]: [];
                        options.exclude = exclude;
                        callback( options );
                    }.bind(this));
                }.bind(this));
                break;
            case "script":
                o2.Actions.load("x_processplatform_assemble_surface").ProcessAction.getActivity(this.businessData.work.activity, "manual", function (activityJson) {
                    var scriptText = activityJson.data.activity.resetRangeScriptText;
                    if (!scriptText) return;
                    var resetRange = this.form.Macro.exec(activityJson.data.activity.resetRangeScriptText, this);
                    options.noUnit = true;
                    options.include = typeOf(resetRange) === "array" ? resetRange : [resetRange];
                    options.exclude = exclude;
                    callback( options );
                }.bind(this));
                break;
            default:
                callback( options );
        }
    },
    getSelDefaultOptions: function(){
        var defaultOpt = {
            "type": "identity",
            "mainColorEnable": this.flow.options.mainColorEnable,
            "style": "flow",
            "width": "auto",
            "height": MWF.ProcessFlow_ORG_HEIGHT,
            "count": this.businessData.activity.resetCount || 0,
            "embedded": true,
            "hasLetter": false, //字母
            "hasTop": true //可选、已选的标题
        };
        if (this.form.json.selectorStyle) {
            defaultOpt = Object.merge(Object.clone(this.form.json.selectorStyle), defaultOpt);
            if (this.form.json.selectorStyle.style) defaultOpt.style = this.form.json.selectorStyle.style;
        }
        return defaultOpt;
    },
    getSelOrgData: function () {
        var data;
        if (this.selector && this.selector.selector) {
            data = this.selector.selector.selectedItems.map(function (item) {
                return item.data.distinguishedName;
                // return MWF.org.parseOrgData(item.data, true, simple);
            })
        }
        return data || [];
    },
    submit: function () {
        var names = this.getSelOrgData();
        if (!names.length) {
            this.flow.noticeError(MWF.xApplication.process.Xform.LP.inputResetPeople, this.orgsArea);
            return false;
        }
        var opinion = this.opinion.getValue();
        // var keep = this.keepOption.getValue() === "true";

        var leftText = MWF.xApplication.process.Xform.LP.resetTo;

        var nameText = [];
        names.each(function (n) { nameText.push(MWF.name.cn(n)); });
        if (!opinion) {
            opinion = leftText + ": " + nameText.join(", ");
        }

        var n = nameText.length > 3 ? (nameText[0]+"、"+nameText[1]+"、"+nameText[2]+"...") : nameText.join(", ");
        var routeName = leftText+":"+n;

        this.flow.quickSelector.saveData();
        this.fireEvent("submit", [names, opinion, routeName, this.opinion.getValue()]);
    },
    getQuickData: function(){
        return {
            routeId: "reset",
            opinion: this.opinion.getValue(),
            //keepTask: this.keepOption.getValue() === "true",
            organizations: {
                default: this.getSelOrgData()
            }
        }
    },
    destroy: function () {
        if (this.orgItem && this.orgItem.clearTooltip){
            this.orgItem.clearTooltip();
        }
        if (this.node) this.node.empty();
    },
});

MWF.ProcessFlow.AddTask = new Class({
    Extends: MWF.ProcessFlow.Reset,
    getUrl: function(){
        return this.flow.path+this.flow.options.style+"/addTask.html";
    },
    afterLoad: function () {
        this.mode = new MWF.ProcessFlow.widget.Radio(this.modeArea, this.flow, {
            optionList: [{
                text: this.lp.single,
                value: "single"
            },{
                text: this.lp.queue,
                value: "queue"
            },{
                text: this.lp.parallel,
                value: "parallel"
            }],
            value: this.quickData.mode || "single" //默认为单人
        });
        this.mode.load();

        var position = "";
        if( this.quickData.routeId ){
            position = (this.quickData.routeId === "before") ? "true" : "false";
        }else{
            position = "false"; //默认为后加签
        }
        this.position = new MWF.ProcessFlow.widget.Radio(this.positionArea, this.flow, {
            optionList: [{
                text: this.lp.addTaskBefore,
                value: "true"
            },
            {
                text: this.lp.addTaskAfter,
                value: "false"
            }],
            value: position
        });
        this.position.load();
    },
    submit: function () {
        var opinion = this.opinion.getValue();

        var before = this.position.getValue() === "true";
        if (!this.position.getValue()) {
            this.flow.noticeError(MWF.xApplication.process.Work.LP.inputAddTaskType, this.positionArea);
            return false;
        }

        var mode = this.mode.getValue();
        if (!mode) {
            this.flow.noticeError(MWF.xApplication.process.Work.LP.inputModeType, this.modeArea);
            return false;
        }

        var names = this.getSelOrgData();
        if (!names.length) {
            this.flow.noticeError(MWF.xApplication.process.Work.LP.inputAddTaskPeople, this.orgsArea);
            return false;
        }

        var leftText = (before ? this.lp.addTaskBefore : this.lp.addTaskAfter) + this.lp[mode];

        var nameText = names.map(function (n) { return MWF.name.cn(n); });
        if (!opinion) {
            opinion = leftText + ": " + nameText.join(", ");
        }

        var n = nameText.length > 3 ? (nameText[0]+"、"+nameText[1]+"、"+nameText[2]+"...") : nameText.join(", ");
        var routeName = leftText+":"+n;

        this.flow.quickSelector.saveData();
        this.fireEvent("submit", [names, opinion, mode, before, routeName, this.opinion.getValue()]);
    },
    setQuickData: function( data ){
        if(this.position)this.position.setValue( data.routeId === "before" ? "true" : null ); //前后加签存在 routeId
        if(this.mode)this.mode.setValue( data.routeGroup || null ); //单人、并行、串行存在 routeGroup
        if(this.opinion)this.opinion.setValue( data.opinion || "" );
        if(this.selector && this.selector.selector)this.selector.selector.setValues( data.organizations ? (data.organizations.default || []) : [] );
    },
    getQuickData: function(){
        var names = this.getSelOrgData();
        // var nameText = names.map(function (n) { return MWF.name.cn(n); });
        // var routeName = o2.xApplication.process.Xform.LP.form.addTask+":"+nameText.join(", ");

        return {
            routeId: this.position.getValue() === "true" ? "before" : "after",
            routeGroup: this.mode.getValue(),
            opinion: this.opinion.getValue(),
            //before: this.position.getValue() === "true",
            routeName: this.position.getValue() === "true" ? "before" : "after",
            organizations: {
                default: names
            }
        }
    },
});

MWF.ProcessFlow.GoBack = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options:{
        data: null,
        style: "default"
    },
    initialize: function (container, flow, options) {
        this.setOptions(options);
        this.container = $(container);
        this.flow = flow;
        this.task = flow.task;
        this.form = flow.form;
        this.lp = flow.lp;
        this.businessData = this.form.businessData;
        this.checkedItems = [];
    },
    load: function(){
        this.container.loadHtml(this.getUrl(), {"bind": {"lp": this.lp}, "module": this}, function(){
            this.loadOpinion();
            this.loadActivitys();
        }.bind(this));
    },
    getUrl: function(){
        return this.flow.path+this.flow.options.style+"/goBack.html";
    },
    loadActivitys: function(){
        o2.Actions.load('x_processplatform_assemble_surface').WorkAction.V2ListActivityGoBack(this.task.work, function(json) {
            this.activitys = json.data.map(function (d) {
                var ids = o2.name.cns(d.lastIdentityList);
                d.identityNameString = (ids.length>8) ? ids.slice(0,8).join(', ')+' ...' : ids.join(', ');
                d.identityNameTitle = ids.join(',');
                return d;
            })

            // var act2 = Array.clone(this.activitys);
            // act2[0].way = "custom";
            // var act3 = Array.clone(this.activitys);
            // act3[0].way = "custom";
            // var act4 = Array.clone(this.activitys);
            // this.activitys = this.activitys.concat(act2, act3, act4);
            this.activitysArea.loadHtml(this.flow.path+this.flow.options.style+"/widget/gobackActivity.html",
                {"bind": {"lp": this.lp, "activityList":this.activitys}, "module": this},
                function(){
                    if( this.activitys.length === 1 ){
                        var el = this.activitysArea.getElement(".o2flow-radio2");
                        this.check( el );
                    }
                    this.afterLoad();
                    this.fireEvent("load");
                }.bind(this));
        }.bind(this));
    },
    loadWayRadio: function(ev, activityData){
        var wayRadio = new MWF.ProcessFlow.widget.Radio(ev.target, this.flow, {
            optionList: [{
                text: this.lp.goBackActivityWayStep,
                value: "step"
            },{
                text: this.lp.goBackActivityWayJump,
                value: "jump"
            }],
            value: "step", //默认为单人
            onLoad: function () {
                this.container.getElement(".o2flow-radio-area").setStyle("display","flex");
                this.container.getElements(".o2flow-radio").setStyles({
                    "padding-top":"0px",
                    "padding-bottom":"0px"
                })
            }
        });
        wayRadio.load();
        var parentNode = ev.target.getParent(".o2flow-radio2");
        parentNode.store("wayRadio", wayRadio);
        ev.target.hide();
    },
    toggle: function( ev ){
        var el = this.flow.getEl(ev, "o2flow-radio2");
        if( this.checkedItems.contains( el ) ){
            //if( this.options.cancelEnable )this.uncheck( el, true )
        }else{
            this.check( el );
        }
    },
    showWayRadio: function(el){
        var parentNode = this.flow.getEl({target: el}, "o2flow-radio2");
        var wayRadio = parentNode.retrieve("wayRadio");
        if(wayRadio)wayRadio.container.show();
    },
    hideWayRadio: function(el){
        var parentNode = this.flow.getEl({target: el}, "o2flow-radio2");
        var wayRadio = parentNode.retrieve("wayRadio");
        if(wayRadio)wayRadio.container.hide();
    },
    check: function(el){
        while( this.checkedItems.length ){
            this.uncheck( this.checkedItems[0] );
        }
        el.addClass("o2flow-radio2-active");
        if( this.flow.options.mainColorEnable )el.addClass("mainColor_color");
        el.getElement("i").removeClass("o2icon-icon_circle").addClass("o2icon-radio-checked").addClass("o2flow-radio2-icon");
        if( this.flow.options.mainColorEnable )el.getElement("i").addClass("mainColor_color");
        el.dataset["o2Checked"] = true;
        this.activitysArea.removeClass("o2flow-invalid-bg");
        this.checkedItems.push(el);
        this.showWayRadio(el);
        this.fireEvent("check", [el, el.dataset["o2Value"]])
    },
    uncheck: function(el, isFire){
        el.removeClass("o2flow-radio2-active");
        if( this.flow.options.mainColorEnable )el.removeClass("mainColor_color");
        el.getElement("i").removeClass("o2icon-radio-checked").addClass("o2icon-icon_circle").removeClass("o2flow-radio2-icon");
        if( this.flow.options.mainColorEnable )el.getElement("i").removeClass("mainColor_color");
        el.dataset["o2Checked"] = false;
        this.checkedItems.erase(el);
        this.hideWayRadio(el);
        if(isFire)this.fireEvent("uncheck", [el, el.dataset["o2Value"]])
    },
    show: function(){

    },
    afterLoad: function () {

    },
    loadOpinion: function(){
        var opt = Object.clone(this.flow.options.opinionOptions);
        opt.isHandwriting = this.options.isHandwriting;
        this.opinion = new MWF.ProcessFlow.widget.Opinion( this.opinionContent, this.flow, opt );
        this.opinion.load();
    },

    submit: function () {
        if (!this.checkedItems.length) {
            this.activitysArea.addClass("o2flow-invalid-bg");
            this.flow.noticeError(
                MWF.xApplication.process.Work.LP.selectGoBackActivity,
                this.activitysArea,
                null,
                {"x": "center", "y": "top"}
            );
            return false;
        }

        var itemNode = this.checkedItems[0];
        var activity = itemNode.dataset["o2Value"];
        var activityName = itemNode.dataset["o2Text"];
        var way;
        var wayRadio = itemNode.retrieve("wayRadio");
        if( wayRadio ){
            way = wayRadio.getValue();
        }else{
            way = itemNode.dataset["o2Way"];
        }

        var opinion = this.opinion.getValue();
        if(!opinion)opinion = this.lp.goBackTo+activityName;

        var routeName = this.lp.goBackTo+activityName;
        debugger;
        this.fireEvent("submit", [opinion, routeName, activity, way, this.opinion.getValue()]);
    },
    // getQuickData: function(){
    //     return {
    //         routeId: "goBack",
    //         opinion: this.opinion.getValue(),
    //         //keepTask: this.keepOption.getValue() === "true",
    //         organizations: {
    //             default: this.getSelOrgData()
    //         }
    //     }
    // },
    destroy: function () {
        if (this.node) this.node.empty();
    },
});

MWF.ProcessFlow.Processor = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "defaultRouteGroup": "",
        "defaultRoute": ""
    },

    initialize: function (container, flow, options) {
        this.setOptions(options);
        this.container = $(container);
        this.node = this.container;
        this.flow = flow;
        this.task = flow.task;
        this.form = flow.form;
        this.lp = flow.lp;
        this.businessData = this.form.businessData;
        this.routeOrgMap = {};
    },
    load: function( quickData ){
        this.quickData = quickData;
        if( quickData )quickData.rId = quickData.routeId;
        if( quickData && (quickData.routeId || quickData.routeName) ){
            this.options.defaultRoute = quickData.routeId || quickData.routeName;
            delete quickData.routeId;
            delete quickData.routeName;
            this.options.defaultRouteGroup = quickData.routeGroup;
            delete quickData.routeGroup;
        }
        this.container.loadHtml(this.flow.path+this.flow.options.style+"/process.html", {"bind": {"lp": this.lp}, "module": this}, function(){
            this.loadOpinion();
            this.loadRouteOrGroupList();
            //this.loadOrg();
            //this.afterLoad();
        }.bind(this));
    },
    checkLoadEvent: function(){
        if( !this.loadEventFired ){
            this.fireEvent("load");
            this.loadEventFired = true;
        }
    },
    setQuickData: function( quickData ){
        this.quickData = quickData;
        if(quickData)quickData.rId = quickData.routeId;
        if( this.routeGroupRadio && quickData.routeGroup ){
            if( quickData.routeId || quickData.routeName ){
                this.options.defaultRoute = quickData.routeId || quickData.routeName;
                delete quickData.routeId;
                delete quickData.routeName;
            }
            this.routeGroupRadio.setValue( quickData.routeGroup );
            delete quickData.routeGroup;
        }else{
            this.routeRadio.setValue( quickData.routeId );
            delete quickData.routeId;
        }
        if(this.opinion){
            this.opinion.setValue( quickData.opinion || "" );
            delete quickData.opinion;
        }
        //if(this.selector && this.selector.selector)this.selector.selector.setValues( data.organizations ? (data.organizations.default || []) : [] );
    },
    loadOpinion: function(){
        var opt = Object.clone(this.flow.options.opinionOptions);
        opt.isHandwriting = this.options.isHandwriting;
        if( this.quickData && this.quickData.opinion ){
            opt.opinion = this.quickData.opinion;
            delete this.quickData.opinion;
        }
        this.opinion = new MWF.ProcessFlow.widget.Opinion( this.opinionContent, this.flow, opt );
        this.opinion.load();
    },
    loadRouteOrGroupList: function () {
        this.getRouteGroupConfig();
        if (this.hasRouteGroup) {
            this.loadRouteGroupList();
        } else {
            this.routeGroupWraper.destroy();
            this.routeWraper.removeClass("o2flow-route-wraper").addClass("o2flow-route-fullsize-wraper");
            this.routeArea.removeClass("o2flow-route-area").addClass("o2flow-route-fullsize-area");
            this.loadRouteList();
        }
    },
    loadRouteGroupList: function(){
        var keys = this.routeGroupNameList;
        keys.sort(function (a, b) {
            var aIdx = parseInt(this.flow.splitByStartNumber(a).order || "9999999");
            var bIdx = parseInt(this.flow.splitByStartNumber(b).order || "9999999");
            return aIdx - bIdx;
        }.bind(this));

        var routeGroupNames =  keys.map(function (k) { return this.flow.splitByStartNumber(k).name; }.bind(this));

        var defaultValue;
        if( keys.length === 1 ) {
            defaultValue = routeGroupNames[0];
        }else if( this.opinion.defaultRouteGroup ){
            defaultValue = this.opinion.defaultRouteGroup;
            this.opinion.defaultRouteGroup = "";
        }else if( this.options.defaultRoute ){
            routeGroupNames.each(function (routeGroupName) {
                if( defaultValue )return;
                var routeList = this.routeGroupObject[routeGroupName];
                var matchRoutes = routeList.filter(function(r){ return r.id === this.options.defaultRoute || r.name === this.options.defaultRoute; }.bind(this));
                if( matchRoutes.length )defaultValue = routeGroupName;
            }.bind(this));
        }

        var optionList = routeGroupNames.map(function (routeGroupName) {
            return {
                text: routeGroupName,
                value: routeGroupName,
                data: this.routeGroupObject[routeGroupName]
            };
        }.bind(this));

        var _self = this;
        this.routeGroupRadio = new MWF.ProcessFlow.widget.Radio2(this.routeGroupArea, this.flow, {
            optionList: optionList,
            cancelEnable: false,
            onCheck: function(el, value){
                var routeConfigList = this.getData().data;
                _self.loadRouteList( routeConfigList );
            },
            onLoad: function(){
                if( defaultValue ){
                    this.setValue( defaultValue );
                }else{
                    _self.loadOrgs();
                }
            }
        });
        this.routeGroupRadio.load();
    },
    loadRouteList: function (routeConfigList) {
        var _self = this;
        this.routeArea.empty();

        if (!routeConfigList) routeConfigList = this.getRouteConfigList();

        var optionList = [], isSelectedDefault = false, defaultRoute;
        routeConfigList.each(function (route, i) {
            if ( route.hiddenScriptText ) {
                if (this.form.Macro.exec(route.hiddenScriptText, this).toString() === "true") return;
            }
            var routeName = route.name;
            if (route.displayNameScriptText && this.form && this.form.Macro) {
                routeName = this.form.Macro.exec(route.displayNameScriptText, this);
            }
            optionList.push({
                value: route.id,
                text: routeName,
                data: route
            });

            if( route.id === this.options.defaultRoute || route.name === this.options.defaultRoute) {
                defaultRoute = route.id;
                this.options.defaultRoute = "";
                isSelectedDefault = true;
            }else if ( !isSelectedDefault && (routeConfigList.length == 1 || route.sole )) { //sole表示优先路由
                defaultRoute = route.id;
            }
        }.bind(this));

        this.routeRadio = new MWF.ProcessFlow.widget.Radio(this.routeArea, this.flow, {
            optionList: optionList,
            onCheck: function(el, value){
                var routeConfig = this.getData().data;
                var checkOpinion = function(){
                    var op = _self.opinion.getValue();
                    if( op === (_self.lastDefaultOpinion || "") || op === "" ){
                        _self.lastDefaultOpinion = routeConfig.opinion || "";
                        _self.opinion.setValue( _self.lastDefaultOpinion );
                    }
                };
                if( _self.opinion.loaded ){
                    checkOpinion()
                }else{
                    _self.opinion.addEvent("load", checkOpinion);
                }
                _self.loadOrgs( routeConfig );
            },
            onUncheck: function(el, value){
                var routeConfig = this.getData( value ).data;
                var op = _self.opinion.getValue();
                if (op === (routeConfig.opinion || "") || op === "" ) {
                    _self.lastDefaultOpinion = "";
                    _self.opinion.setValue("");
                }
                _self.loadOrgs();
            },
            onLoad: function(){
                if( defaultRoute ){
                    this.setValue( defaultRoute );
                }else{
                    _self.loadOrgs();
                }
            }
        });
        this.routeRadio.load();
    },
    loadOrgs: function( routeConfig ){
        for( var r in this.routeOrgMap ){
            this.routeOrgMap[r].hide();
        }
        var notFireResize = !this.loadEventFired;
        if( routeConfig ){
            var routeId = routeConfig.id;
            var orgList = this.routeOrgMap[routeId];
            if( orgList ){
                orgList.load( notFireResize );
            }else{
                orgList = new MWF.ProcessFlow.Processor.OrgList(this, {
                    routeId: routeId
                });
                this.routeOrgMap[routeId] = orgList;
                orgList.load( notFireResize );
            }
            this.currentOrgList = orgList;
        }else{
            this.orgsWraper.hide();
            //this.setSize(0);
            if( !notFireResize )this.flow.resize();
            this.currentOrgList = null;
        }
        this.checkLoadEvent();
    },
    cancel: function(){
        this.destroy();
        this.fireEvent("cancel");
    },
    submit: function (ev) {
        if (this.hasRouteGroup && !this.routeGroupRadio.getValue() ) {
            this.routeGroupRadio.setRequireStyle();
            this.flow.noticeError(
                MWF.xApplication.process.Work.LP.mustSelectRouteGroup,
                this.routeGroupArea,
                null,
                {"x": "center", "y": "top"}
            );
            return false;
        }

        if (!this.routeRadio.getValue()) {
            this.routeRadio.setRequireStyle();
            this.flow.noticeError(
                MWF.xApplication.process.Work.LP.mustSelectRoute,
                this.routeArea,
                null,  //{"x": 0, "y": 30}
                {"x": "center", "y": "top"}
            );
            return false;
        }

        var routeName = this.routeRadio.getData().data.name;
        var opinion = this.opinion.getValue();
        var medias = [];
        if (this.opinion.handwritingFile) medias.push(this.opinion.handwritingFile);

        var currentRouteId = this.routeRadio.getValue();
        var routeConfig = this.getRouteConfig(currentRouteId);

        if (!opinion && medias.length === 0) {
            if (routeConfig.opinionRequired === true) {
                this.opinion.setRequireStyle();
                this.flow.noticeError(
                    MWF.xApplication.process.Work.LP.opinionRequired,
                    this.opinion.opinionTextarea,
                    null,
                    {"x": "center", "y": "top"}
                );
                return false;
            }
        }

        var orgItems = this.currentOrgList ? this.currentOrgList.orgVisableItems : [];
        var appendTaskOrgItem = "";
        if (routeConfig.type === "appendTask" && routeConfig.appendTaskIdentityType === "select") {
            if (!orgItems || orgItems.length === 0) {
                this.flow.noticeError(
                    MWF.xApplication.process.Work.LP.noAppendTaskIdentityConfig,
                    this.node,
                    null
                );
                return false;
            } else {
                appendTaskOrgItem = orgItems[0];
            }
        }

        this.currentOrgList.saveOrgsWithCheckEmpower(function () {
            var appandTaskIdentityList;
            if (appendTaskOrgItem) {
                appandTaskIdentityList = appendTaskOrgItem.getData();
                if (!appandTaskIdentityList || appandTaskIdentityList.length === 0) {
                    this.flow.noticeError(
                        MWF.xApplication.process.Work.LP.selectAppendTaskIdentityNotice,
                        this.node
                    );
                    return;
                }
            }

            if (routeConfig.validationScriptText) {
                var validation = this.form.Macro.exec(routeConfig.validationScriptText, this);
                if (!validation || validation.toString() !== "true") {
                    if (typeOf(validation) === "string") {
                        this.flow.noticeError( validation, this.node );
                        return false;
                    } else {
                        //"路由校验失败"
                        this.flow.noticeError(
                            MWF.xApplication.process.Work.LP.routeValidFailure,
                            this.node
                        );
                        return false;
                    }
                }
            }

            this.node.mask({
                "inject": {"where": "bottom", "target": this.node},
                "destroyOnHide": true,
                "style": {
                    "background-color": "#999",
                    "opacity": 0.3,
                    "z-index": this.flow.options.zIndex+1
                }
            });


            var array = [routeName, opinion, medias, appandTaskIdentityList, orgItems, function () {
                if (appendTaskOrgItem) appendTaskOrgItem.setData([]);
            }];

            this.flow.quickSelector.saveData();
            this.fireEvent("submit", array);
        }.bind(this));
    },
    getQuickData: function(){
        var organizations = {};
        var orgItems = this.currentOrgList ? this.currentOrgList.orgVisableItems : [];
        orgItems.each(function (org) {
            var vs = org.getValue();
            organizations[org.json.name] = vs.map(function (o) {
                return typeOf(o) === "string" ? o : o.distinguishedName;
            })
        });

        var route = this.routeRadio.getData().data;
        return {
            routeGroup: this.routeGroupRadio ? this.routeGroupRadio.getValue() : "",
            routeId: route.id,
            routeName: route.name,
            opinion: this.opinion.getValue(),
            organizations: organizations
        }
    },

    destroy: function () {
        Object.values( this.routeOrgMap ).each(function (orgList) {
            Object.values( orgList.orgMap ).each(function( org ){
                if(org.clearTooltip)org.clearTooltip();
            });
        });
    },
    getRouteGroupConfig: function () {
        var config = this.flow.getRouteGroupConfig();
        this.routeGroupObject = this.flow.routeGroupObject;
        this.routeGroupNameList = this.flow.routeGroupNameList;
        this.hasRouteGroup = this.flow.hasRouteGroup;
        return config;
    },
    getRouteConfigList: function () {
        return this.flow.getRouteConfigList();
    },
    getRouteConfig: function (routeId) {
        return this.flow.getRouteConfig( routeId );
    },
    getOrgConfig: function (routeId) {
        return this.flow.getOrgConfig( routeId );
    },
    getVisableOrgConfig: function (routeId) {
        return this.flow.getVisableOrgConfig(routeId);
    },
    isOrgHidden: function (d) {
        return this.flow.isOrgHidden(d);
    },
    getMaxOrgLength: function () {
        return this.flow.getMaxOrgLength();
    },
    getOffsetY: function (node) {
        return (node.getStyle("margin-top").toInt() || 0) +
            (node.getStyle("margin-bottom").toInt() || 0) +
            (node.getStyle("padding-top").toInt() || 0) +
            (node.getStyle("padding-bottom").toInt() || 0) +
            (node.getStyle("border-top-width").toInt() || 0) +
            (node.getStyle("border-bottom-width").toInt() || 0);
    },
    // setSize: function (currentOrgLength, flag) {
    //     this.flow.resize()
    //     return;
    // }
});

MWF.xDesktop.requireApp("process.Xform", "Org", null, false);

MWF.ProcessFlow.Processor.OrgList = new Class({
    Implements: [Options, Events],
    options: {
        routeId: ""
    },
    initialize: function (processor, options) {
        this.processor = processor;
        this.form = processor.form;
        this.flow = processor.flow;
        this.businessData = this.form.businessData;
        this.wraper = processor.orgsWraper;
        this.container = processor.orgsArea;
        this.node = new Element("div").inject( processor.orgsArea );
        this.setOptions(options);

        this.orgs = [];
        this.orgMap = {};
        this.domMap = {};
    },
    load: function ( notFireResize ) {
        this.orgVisableItems = [];
        var configVisable = this.getVisableOrgConfig();
        if (configVisable.length) {
            this.wraper.show();
            this.node.show();
            this.loadOrgs();
            //this.processor.setSize(configVisable.length);
            if(!notFireResize)this.flow.resize();
        } else {
            this.orgMap = {};
            this.domMap = {};
            this.node.hide();
            this.wraper.hide();
            //this.processor.setSize(0);
            if(!notFireResize)this.flow.resize();
        }
    },
    hide: function(){
        this.node.hide();
    },
    getVisableOrgConfig: function(){
        return this.processor.getVisableOrgConfig( this.options.routeId );
    },
    getQuickOrgData: function( orgConfig ){
        if( !this.processor.quickData )return;
        if( this.processor.quickData.rId !== this.options.routeId )return;
        if( !this.processor.quickData.organizations )return;
        var d = this.processor.quickData.organizations[orgConfig.name];
        delete this.processor.quickData.organizations[orgConfig.name];
        return d;
    },
    loadOrgs: function () {
        var lastDom, configVisable = this.getVisableOrgConfig(), len = configVisable.length, lineNode;
        configVisable.each(function (config, i) {
            var dom, cfgId = config.id, quickOrgData = this.getQuickOrgData( config );
            if( i % 2 === 0 )lineNode = new Element("div.o2flow-org-line").inject( this.node );
            if( this.domMap[cfgId] ){
                dom = this.domMap[cfgId].show().inject( lineNode );
                if( quickOrgData && this.orgMap[cfgId]){
                    var org = this.orgMap[cfgId];
                    org.setData( quickOrgData );
                    org.resetSelectorData();
                }
                this.orgVisableItems.push( this.orgMap[cfgId] );
            }else{
                //dom = new Element("div" ).inject( lastDom || this.node, lastDom ? "after" : "bottom" );
                dom = new Element("div" ).inject( lineNode );
                this.domMap[cfgId] = dom;
                this.loadOrg(dom, config, false, quickOrgData );
            }

            if( (i + 1 === len) && (len % 2 === 1) ){ //fullsize
                dom.addClass("o2flow-org-fullsize-node").removeClass("o2flow-org-left-node").removeClass("o2flow-org-right-node");
            }else{
                if( i % 2 === 0 ){ //left
                    dom.removeClass("o2flow-org-fullsize-node").addClass("o2flow-org-left-node").removeClass("o2flow-org-right-node");
                }else{
                    dom.removeClass("o2flow-org-fullsize-node").removeClass("o2flow-org-left-node").addClass("o2flow-org-right-node");
                }
            }
            //lastDom = dom;
        }.bind(this));
    },
    loadOrg: function (container, json, ignoreOldData, quickOrgData) {
        var titleNode = new Element("div.o2flow-selector-title").inject(container);
        new Element("div.o2flow-selector-titletext", { "text": json.title }).inject(titleNode);
        var errorNode = new Element("div.o2flow-selector-errornode").inject(titleNode);

        var contentNode = new Element("div.o2flow-selector-content").inject(container);
        contentNode.setStyle( "height", MWF.ProcessFlow_ORG_HEIGHT + "px" );

        var org = new MWF.ProcessFlow.Processor.Org(contentNode, this.form, json, this);
        org.ignoreOldData = ignoreOldData;
        org.errContainer = errorNode;
        org.load( quickOrgData );
        this.orgVisableItems.push(org);
        this.orgMap[json.id] = org;
    },
    clearAllOrgs: function () {
        //清空组织所选人
        Object.values(this.orgMap).each(function (org) {
            org.setDataToOriginal();
        })
        this.orgMap = {};
        this.domMap = {};
        this.orgVisableItems = [];
        this.node.empty();
    },
    getSelectedData: function (filedName) {
        var data = [];
        for (var i = 0; i < this.orgVisableItems.length; i++) {
            var org = this.orgVisableItems[i];
            if (org.json.name === filedName) {
                var selector = org.selector.selector;
                selector.selectedItems.each(function (item) {
                    data.push(item.data)
                })
            }
        }
        return data;
    },

    isErrorHeightOverflow: function () {
        var hasOverflow = false;
        (this.orgVisableItems || []).each(function (item) {
            if (item.errorHeightOverflow) {
                hasOverflow = true;
            }
        }.bind(this));
        return hasOverflow;
    },
    checkErrorHeightOverflow: function (force) {
        if (force || this.isErrorHeightOverflow()) {
            //this.processor.setSize(this.orgVisableItems.length, true);
            this.flow.resize();
        }
    },
    errorHeightChange: function () {
        this.checkErrorHeightOverflow(true)
    },
    validationOrgs: function () {
        if (!this.orgVisableItems || !this.orgVisableItems.length) return true;
        var flag = true;
        this.orgVisableItems.each(function (item) {
            if (!item.validation()) flag = false;
        }.bind(this));
        this.checkErrorHeightOverflow();
        return flag;
    },
    isOrgsHasEmpower: function () {
        if (!this.orgVisableItems || !this.orgVisableItems.length) return true;
        this.needCheckEmpowerOrg = [];
        var ps = this.orgVisableItems.map(function (item) {
            return Promise.resolve( item.hasEmpowerIdentity() ).then(function ( flag ) {
                if( flag )return item;
            }.bind(this));
        }.bind(this));
        return Promise.all( ps ).then(function ( array ) {
            this.needCheckEmpowerOrg = array.clean();
            return this.needCheckEmpowerOrg.length > 0;
        }.bind(this));
    },
    saveOrgs: function (keepSilent) {
        if (!this.orgVisableItems || !this.orgVisableItems.length) return true;
        var flag = true;
        this.orgVisableItems.each(function (item) {
            if (!item.save(!keepSilent)) flag = false;
        }.bind(this));
        return flag;
    },
    saveOrgsWithCheckEmpower: function (callback) {
        var visableOrg = this.getVisableOrgConfig();
        var needOrgLength = visableOrg.length;

        var loadedOrgLength = 0;
        if ( this.orgVisableItems && this.orgVisableItems.length)loadedOrgLength = this.orgVisableItems.length;

        if( needOrgLength !== loadedOrgLength ){
            this.flow.noticeError(
                MWF.xApplication.process.Work.LP.loadedOrgCountUnexpected,
                this.node
            );
            return false;
        }

        if (!this.orgVisableItems || !this.orgVisableItems.length) {
            if (callback) callback();
            return true;
        }
        if (!this.validationOrgs()) return false;

        Promise.resolve( this.isOrgsHasEmpower() ).then(function (flag) {
            if( flag ){
                this.showEmpowerDlg(callback);
            }else{
                if (callback) callback();
                return true;
            }
        }.bind(this));
    },
    showEmpowerDlg: function (callback) {
        var empowerNode = new Element("div.o2flow-empower-node");
        empowerNode.loadCss( this.flow.path + this.flow.options.style + "/style.css" );
        var empowerTitleNode = new Element("div.o2flow-empower-titleNode", {
            text: MWF.xApplication.process.Xform.LP.empowerDlgText
        }).inject(empowerNode);

        var orgs = this.needCheckEmpowerOrg;
        var len = orgs.length;
        var lines = ((len + 1) / 2).toInt();

        var empowerTable = new Element("table.o2flow-empower-table", {
            "cellspacing": 0, "cellpadding": 0, "border": 0, "width": "100%"
        }).inject(empowerNode);

        for (var n = 0; n < lines; n++) {
            var tr = new Element("tr").inject(empowerTable);
            new Element("td.o2flow-empower-oddTd").inject(tr);
            new Element("td.o2flow-empower-evenTd").inject(tr);
        }

        var trs = empowerTable.getElements("tr");
        orgs.each(function (org, i) {
            var sNode;
            var width;
            if (i + 1 == len && (len % 2 === 1)) {
                sNode = trs[trs.length - 1].getFirst("td");
                sNode.set("colspan", 2);
                trs[trs.length - 1].getLast("td").destroy();
                width = "50%";
            } else {
                var row = ((i + 2) / 2).toInt();
                var tr = trs[row - 1];
                sNode = (i % 2 === 0) ? tr.getFirst("td") : tr.getLast("td");
            }

            org.loadCheckEmpower(sNode);

        }.bind(this));

        var width = 840;
        empowerNode.setStyle("height", lines * MWF.ProcessFlow_ORG_HEIGHT + 20);
        empowerNode.setStyle("width", width + "px");
        empowerNode.setStyle("opacity", 0);

        this.node.getParent().mask({
            "opacity": 0.7,
            "background-color": "#eee",
            "z-index": this.flow.options.zIndex + 2
        });
        this.empowerDlg = o2.DL.open({
            "title": MWF.xApplication.process.Xform.LP.selectEmpower,
            "style": this.form.json.dialogStyle || "user",
            "isResize": false,
            "content": empowerNode,
            //"container" : this.node,
            "width": width + 40, //600,
            "height": "auto", //dlgHeight,
            "mark": true,
            "zindex": this.flow.options.zIndex + 3,
            "onPostShow": function () {
                if (this.nodeWidth)this.node.setStyle("width", this.nodeWidth + "px");
                if (this.nodeHeight)this.node.setStyle("height", this.nodeHeight + "px");
                empowerNode.setStyle("opacity", 1);
            },
            "buttonList": [
                {
                    "type": "ok",
                    "text": MWF.LP.process.button.ok,
                    "action": function (d, e) {
                        orgs.each(function (org, i) {
                            org.saveCheckedEmpowerData(function () {
                                if (i === orgs.length - 1) {
                                    if (callback) callback();
                                    this.node.getParent().unmask();
                                    this.empowerDlg.close();
                                }
                            }.bind(this))
                        }.bind(this))
                    }.bind(this)
                },
                {
                    "type": "cancel",
                    "text": MWF.LP.process.button.cancel,
                    "action": function () {
                        this.node.getParent().unmask();
                        this.empowerDlg.close();
                    }.bind(this)
                }
            ]
        });
    },
});

MWF.ProcessFlow.Processor.Org = new Class({
    Implements: [Options, Events],
    options: {
        moduleEvents: ["queryLoadSelector", "postLoadSelector", "postLoadContent", "queryLoadCategory", "postLoadCategory",
            "selectCategory", "unselectCategory", "queryLoadItem", "postLoadItem", "selectItem", "unselectItem", "change"]
    },
    initialize: function (container, form, json, processor, options) {
        this.form = form;
        this.json = json;
        this.processor = processor;
        this.container = $(container);
        this.orgAction = MWF.Actions.get("x_organization_assemble_control");
        this.setOptions(options);
    },
    load: function ( quickData ) {
        var options = this.getOptions();
        if (options) {
            if( quickData )options.values = quickData;
            this.selector = new MWF.O2Selector(this.container, options);
        }
    },
    clearTooltip: function(){
        if( this.selector && this.selector.selector && this.selector.selector.clearTooltip ){
            this.selector.selector.clearTooltip();
        }
    },
    _getOrgOptions: function () {
        this.selectTypeList = typeOf(this.json.selectType) == "array" ? this.json.selectType : [this.json.selectType];
        if (this.selectTypeList.contains("identity")) {
            this.identityOptions = new MWF.ProcessFlow.Processor.IdentityOptions(this.form, this.json);
        }
        if (this.selectTypeList.contains("unit")) {
            this.unitOptions = new MWF.ProcessFlow.Processor.UnitOptions(this.form, this.json);
        }
        if( this.selectTypeList.contains( "group" ) ){
            this.groupOptions = new MWF.ProcessFlow.Processor.GroupOptions( this.form, this.json );
        }
    },
    getDefaultOptions: function(){
        return {
            "style": "flow",
            "mainColorEnable": this.processor.flow.options.mainColorEnable,
            "width": "auto",
            "height": MWF.ProcessFlow_ORG_HEIGHT,
            "embedded": true,
            "hasLetter": false, //字母
            "hasTop": true //可选、已选的标题
        };
    },
    getOptionEvents: function(){
        return {};
    },
    getOptions: function () {
        var _self = this;
        this._getOrgOptions();
        if (this.selectTypeList.length === 0) return false;
        var exclude = [];
        if (this.json.exclude) {
            var v = this.form.Macro.exec(this.json.exclude.code, this);
            exclude = typeOf(v) === "array" ? v : [v];
        }

        var identityOpt;
        if (this.identityOptions) {
            identityOpt = this.identityOptions.getOptions();
            if (this.json.identityRange !== "all") {
                if (!identityOpt.noUnit && (!identityOpt.units || !identityOpt.units.length)) {
                    this.form.notice(MWF.xApplication.process.Xform.LP.noIdentitySelectRange, "error", this.node);
                    identityOpt.disabled = true;
                    // return false;
                }
            }
            if (!identityOpt.noUnit && this.json.dutyRange && this.json.dutyRange !== "all") {
                if (!identityOpt.dutys || !identityOpt.dutys.length) {
                    this.form.notice(MWF.xApplication.process.Xform.LP.noIdentityDutySelectRange, "error", this.node);
                    identityOpt.disabled = true;
                    // return false;
                }
            }
            if (this.ignoreOldData) {
                identityOpt.values = this._computeValue() || [];
            } else {
                identityOpt.values = this.getValue() || [];
            }
            identityOpt.exclude = exclude;
        }

        var unitOpt;
        if (this.unitOptions) {
            unitOpt = this.unitOptions.getOptions();
            if (this.json.unitRange !== "all") {
                if (!unitOpt.units || !unitOpt.units.length) {
                    this.form.notice(MWF.xApplication.process.Xform.LP.noUnitSelectRange, "error", this.node);
                    unitOpt.disabled = true;
                    // return false;
                }
            }
            if (this.ignoreOldData) {
                unitOpt.values = this._computeValue() || [];
            } else {
                unitOpt.values = this.getValue() || [];
            }
            unitOpt.exclude = exclude;
        }

        var groupOpt;
        if( this.groupOptions ){
            groupOpt = this.groupOptions.getOptions();
            if (this.ignoreOldData) {
                groupOpt.values = this._computeValue() || [];
            } else {
                groupOpt.values = this.getValue() || [];
            }
            groupOpt.exclude = exclude;
        }

        var defaultOpt = this.getDefaultOptions();

        if (this.json.events && typeOf(this.json.events) === "object") {
            Object.each(this.json.events, function (e, key) {
                if (e.code) {
                    if (this.options.moduleEvents.indexOf(key) !== -1) {
                        if (key === "postLoadSelector") {
                            this.addEvent("loadSelector", function (selector) {
                                return this.form.Macro.fire(e.code, selector);
                            }.bind(this))
                        } else if (key === "queryLoadSelector") {
                            defaultOpt["onQueryLoad"] = function (target) {
                                return this.form.Macro.fire(e.code, target);
                            }.bind(this)
                        } else {
                            defaultOpt["on" + key.capitalize()] = function (target) {
                                return this.form.Macro.fire(e.code, target);
                            }.bind(this)
                        }
                    }
                }
            }.bind(this));
        }

        if (this.needValid()) {
            defaultOpt["onValid"] = function (selector) {
                this.validOnSelect();
            }.bind(this);
        }

        if (this.form.json.selectorStyle) {
            defaultOpt = Object.merge(Object.clone(this.form.json.selectorStyle), defaultOpt);
            if (this.form.json.selectorStyle.style) defaultOpt.style = this.form.json.selectorStyle.style;
        }

        var events = this.getOptionEvents();

        if (this.selectTypeList.length === 1) {
            var opts = Object.merge(
                defaultOpt,
                {
                    "type": this.selectTypeList[0],
                    "onLoad": function () {
                        //this 为 selector
                        _self.selectOnLoad(this, this.selector)
                    }
                },
                events,
                identityOpt || unitOpt || groupOpt
            )
            return this.filterOptionValues( opts, this.selectTypeList[0] );
        } else if (this.selectTypeList.length > 1) {
            var options = {
                "type": "",
                "types": this.selectTypeList,
                "onLoad": function () {
                    //this 为 selector
                    _self.selectOnLoad(this)
                }
            };
            if (identityOpt) {
                options.identityOptions = Object.merge(
                    defaultOpt, events, identityOpt
                );
            }
            if (unitOpt) {
                options.unitOptions = Object.merge(
                    defaultOpt,  events, unitOpt
                );
            }
            if (groupOpt) {
                options.groupOptions = Object.merge(
                    defaultOpt, events, groupOpt
                );
            }
            return options;
        }
    },
    filterOptionValues: function( options, type ){
        var suffix;
        switch (type) {
            case "identity": suffix = "I"; break;
            case "unit": suffix = "U"; break;
            case "group": suffix = "G"; break;
        }
        options.values = (options.values || []).filter(function (v) {
            if( typeOf(v) === "string" ){
                if( v.contains("@") ){
                    return v.split("@").getLast().toUpperCase() === suffix;
                }else{
                    return true;
                }
            }else if( typeOf(v) === "object" ){
                if( v.distinguishedName ){
                    return v.distinguishedName.split("@").getLast().toUpperCase() === suffix;
                }else{
                    return false;
                }
            }
            return false;
        }.bind(this));
        return options;
    },
    selectOnLoad: function (selector) {
        //if (this.descriptionNode) this.descriptionNode.setStyle("display", "none");
        this.fireEvent("loadSelector", [selector])
    },
    loadOrgWidget: function (value, node) {
        var height = node.getStyle("height").toInt();
        if (node.getStyle("overflow") === "visible" && !height) node.setStyle("overflow", "hidden");
        if (value && value.length) {
            value.each(function (data) {
                if( typeOf(data) === "string" ){
                    data = { distinguishedName : data, name : o2.name.cn(data) };
                }
                var flag = data.distinguishedName.substr(data.distinguishedName.length - 1, 1);
                var copyData = Object.clone(data);
                if (this.json.displayTextScript && this.json.displayTextScript.code) {
                    this.currentData = copyData;
                    var displayName = this.form.Macro.exec(this.json.displayTextScript.code, this);
                    if (displayName) {
                        copyData.displayName = displayName;
                    }
                    this.currentData = null;
                }

                var widget;
                switch (flag.toLowerCase()) {
                    case "i":
                        widget = new MWF.widget.O2Identity(copyData, node, this.getOrgWidgetOption());
                        break;
                    case "p":
                        widget = new MWF.widget.O2Person(copyData, node, this.getOrgWidgetOption());
                        break;
                    case "u":
                        widget = new MWF.widget.O2Unit(copyData, node, this.getOrgWidgetOption());
                        break;
                    case "g":
                        widget = new MWF.widget.O2Group(copyData, node, this.getOrgWidgetOption());
                        break;
                    default:
                        widget = new MWF.widget.O2Other(copyData, node, this.getOrgWidgetOption());
                }
                widget.field = this;
                widget.load();
            }.bind(this));
        }
    },
    getOrgWidgetOption: function(){
        return {"style": "xform", "lazy": true, "delay":true}
    },

    hasEmpowerIdentity: function () {
        var data = this.getData();
        if (!this.empowerChecker){
            this.empowerChecker = new MWF.ProcessFlow.Processor.EmpowerChecker(this);
        }
        return this.empowerChecker.hasEmpowerIdentity(data);
    },
    checkEmpower: function (data, callback, container) {
        if (typeOf(data) === "array" && this.identityOptions && this.json.isCheckEmpower && this.json.identityResultType === "identity") {
            if (!this.empowerChecker){
                this.empowerChecker = new MWF.ProcessFlow.Processor.EmpowerChecker(this);
            }
            this.empowerChecker.load(data, callback, container);
        } else {
            if (callback) callback(data);
        }
    },
    loadCheckEmpower: function (container, callback) {
        this.checkEmpower(this.getData(), callback, container);
    },
    saveCheckedEmpowerData: function (callback) {
        var data = this.getData();
        var simple = this.json.storeRange === "simple";
        //this.empowerChecker.replaceEmpowerIdentity(data, function( newData ){
        this.empowerChecker.setIgnoreEmpowerFlag(data, function (newData) {
            var values = [];
            newData.each(function (d) {
                values.push(MWF.org.parseOrgData(d, true, simple));
            }.bind(this));
            this.setData(values);
            if (callback) callback(values);
        }.bind(this))
    },

    save: function (isValid) {
        if (isValid) {
            if (this.validation()) {
                return true;
            } else {
                this.processor.checkErrorHeightOverflow();
                return false;
            }
        } else {
            this.setData(this.getData());
            return true;
        }
    },

    resetSelectorData: function () {
        if (this.selector && this.selector.selector) {
            this.selector.selector.emptySelectedItems();
            this.selector.selector.options.values = this.getValue() || [];
            this.selector.selector.setSelectedItem();
        }
    },
    setDataToOriginal: function () {
        var v = this._computeValue();
        this.setData(v || "");
    },
    resetData: function () {
        var v = this.getValue() || [];
        //this.setData((v) ? v.join(", ") : "");
        this.setData(v);
    },
    getData: function () {
        if (this.selector) {
            return this.getSelectedData();
        } else {
            return this.getValue();
        }
    },
    getSelectedData: function () {
        var simple = this.json.storeRange === "simple";
        var data = [];
        if (this.selector && this.selector.selector) {
            this.selector.selector.selectedItems.each(function (item) {
                data.push(MWF.org.parseOrgData(item.data, true, simple));
            })
        }
        return data;
    },
    getValue: function () {
        var value = this._getBusinessData();
        if (!value) value = this._computeValue();
        return value || "";
    },
    _computeValue: function () {
        var values = [];
        if (this.json.identityValue) {
            this.json.identityValue.each(function (v) {
                if (v) values.push(v)
            });
        }
        if (this.json.unitValue) {
            this.json.unitValue.each(function (v) {
                if (v) values.push(v)
            });
        }
        // if (this.json.groupValue) {
        //     this.json.groupValue.each(function (v) {
        //         if (v) values.push(v)
        //     });
        // }
        if (this.json.dutyValue) {
            var dutys = JSON.decode(this.json.dutyValue);
            var par;
            if (dutys.length) {
                dutys.each(function (duty) {
                    if (duty.code) par = this.form.Macro.exec(duty.code, this);
                    var code = "return this.org.getDuty(\"" + duty.name + "\", \"" + par + "\")";

                    var d = this.form.Macro.exec(code, this);
                    if (typeOf(d) !== "array") d = (d) ? [d.toString()] : [];
                    d.each(function (dd) {
                        if (dd) values.push(dd);
                    });

                }.bind(this));
            }
        }
        if (this.json.defaultValue && this.json.defaultValue.code) {
            var fd = this.form.Macro.exec(this.json.defaultValue.code, this);
            if (typeOf(fd) !== "array") fd = (fd) ? [fd] : [];
            fd.each(function (fdd) {
                if (fdd) {
                    if (typeOf(fdd) === "string") {
                        var data;
                        this.getOrgAction()[this.getValueMethod(fdd)](function (json) {
                            data = json.data
                        }.bind(this), null, fdd, false);
                        values.push(data);
                    } else {
                        values.push(fdd);
                    }
                }
            }.bind(this));
        }
        if (this.json.count > 0) {
            return values.slice(0, this.json.count);
        }
        return values;
        //return (this.json.defaultValue.code) ? this.form.Macro.exec(this.json.defaultValue.code, this): (value || "");
    },
    getOrgAction: function () {
        if (!this.orgAction) this.orgAction = MWF.Actions.get("x_organization_assemble_control");
        //if (!this.orgAction) this.orgAction = new MWF.xApplication.Selector.Actions.RestActions();
        return this.orgAction;
    },
    setData: function (value) {

        if (!value) return false;
        var oldValues = this.getValue();
        var values = [];

        var simple = this.json.storeRange === "simple";

        var type = typeOf(value);
        if (type === "array") {
            value.each(function (v) {
                var vtype = typeOf(v);
                var data = null;
                if (vtype === "string") {
                    this.getOrgAction()[this.getValueMethod(v)](function (json) {
                        data = MWF.org.parseOrgData(json.data, true, simple);
                    }.bind(this), null, v, false);
                }
                if (vtype === "object") {
                    data = MWF.org.parseOrgData(v, true, simple);
                    if (data.woPerson) delete data.woPerson;
                }
                if (data) values.push(data);
            }.bind(this));
        }
        if (type === "string") {
            var vData;
            this.getOrgAction()[this.getValueMethod(value)](function (json) {
                vData = MWF.org.parseOrgData(json.data, true, simple);
            }.bind(this), null, value, false);
            if (vData) values.push(vData);
        }
        if (type === "object") {
            var vData = MWF.org.parseOrgData(value, true, simple);
            if (vData.woPerson) delete vData.woPerson;
            values.push(vData);
        }

        var change = false;
        if (oldValues.length && values.length) {
            if (oldValues.length === values.length) {
                for (var i = 0; i < oldValues.length; i++) {
                    if ((oldValues[i].distinguishedName !== values[i].distinguishedName) || (oldValues[i].name !== values[i].name) || (oldValues[i].unique !== values[i].unique)) {
                        change = true;
                        break;
                    }
                }
            } else {
                change = true;
            }
        } else if (values.length || oldValues.length) {
            change = true;
        }
        this._setBusinessData(values);
        if (change) this.fireEvent("change");
    },

    getValueMethod: function (value) {
        if (value) {
            var flag = value.substr(value.length - 1, 1);
            switch (flag.toLowerCase()) {
                case "i":
                    return "getIdentity";
                case "p":
                    return "getPerson";
                case "u":
                    return "getUnit";
                case "g":
                    return "getGroup";
                default:
                    return (this.json.selectType === "unit") ? "getUnit" : "getIdentity";
            }
        }
        return (this.json.selectType === "unit") ? "getUnit" : "getIdentity";
    },

    _getBusinessData: function () {
        if (this.json.section == "yes") {
            return this._getBusinessSectionData();
        } else {
            if (this.json.type === "Opinion") {
                return this._getBusinessSectionDataByPerson();
            } else {
                return this.form.businessData.data[this.json.name] || "";
            }
        }
    },
    _getBusinessSectionData: function () {
        switch (this.json.sectionBy) {
            case "person":
                return this._getBusinessSectionDataByPerson();
            case "unit":
                return this._getBusinessSectionDataByUnit();
            case "activity":
                return this._getBusinessSectionDataByActivity();
            case "splitValue":
                return this._getBusinessSectionDataBySplitValue();
            case "script":
                return this._getBusinessSectionDataByScript(this.json.sectionByScript.code);
            default:
                return this.form.businessData.data[this.json.name] || "";
        }
    },
    _getBusinessSectionDataByPerson: function () {
        this.form.sectionListObj[this.json.name] = layout.desktop.session.user.id;
        var dataObj = this.form.businessData.data[this.json.name];
        return (dataObj) ? (dataObj[layout.desktop.session.user.id] || "") : "";
    },
    _getBusinessSectionDataByUnit: function () {
        this.form.sectionListObj[this.json.name] = "";
        var key = (this.form.businessData.task) ? this.form.businessData.task.unit : "";
        if (key) this.form.sectionListObj[this.json.name] = key;
        var dataObj = this.form.businessData.data[this.json.name];
        if (!dataObj) return "";
        return (key) ? (dataObj[key] || "") : "";
    },
    _getBusinessSectionDataByActivity: function () {
        this.form.sectionListObj[this.json.name] = "";
        var key = (this.form.businessData.work) ? this.form.businessData.work.activity : "";
        if (key) this.form.sectionListObj[this.json.name] = key;
        var dataObj = this.form.businessData.data[this.json.name];
        if (!dataObj) return "";
        return (key) ? (dataObj[key] || "") : "";
    },
    _getBusinessSectionDataBySplitValue: function () {
        this.form.sectionListObj[this.json.name] = "";
        var key = (this.form.businessData.work) ? this.form.businessData.work.splitValue : "";
        if (key) this.form.sectionListObj[this.json.name] = key;
        var dataObj = this.form.businessData.data[this.json.name];
        if (!dataObj) return "";
        return (key) ? (dataObj[key] || "") : "";
    },
    _getBusinessSectionDataByScript: function (code) {
        this.form.sectionListObj[this.json.name] = "";
        var dataObj = this.form.businessData.data[this.json.name];
        if (!dataObj) return "";
        var key = this.form.Macro.exec(code, this);
        if (key) this.form.sectionListObj[this.json.name] = key;
        return (key) ? (dataObj[key] || "") : "";
    },

    loadPathData: function (path) {
        var data = null;
        this.form.workAction.getJobDataByPath(this.form.businessData.work.job, path, function (json) {
            data = json.data || null;
        }, null, false);
        return data;
    },

    _setBusinessData: function (v) {
        if (this.json.section == "yes") {
            // var d = this.loadPathData(this.json.name);
            // if (d) this.form.businessData.data[this.json.name] = d;
            this._setBusinessSectionData(v);
        } else {
            if (this.json.type === "Opinion") {
                // var d = this.loadPathData(this.json.name);
                // if (d) this.form.businessData.data[this.json.name] = d;
                this._setBusinessSectionDataByPerson(v);
            } else {
                if (this.form.businessData.data[this.json.name]) {
                    this.form.businessData.data[this.json.name] = v;
                } else {
                    this.form.businessData.data[this.json.name] = v;
                    this.form.Macro.environment.setData(this.form.businessData.data);
                }
                if (this.json.isTitle) this.form.businessData.work.title = v;
            }
        }
    },
    _setBusinessSectionData: function (v) {
        switch (this.json.sectionBy) {
            case "person":
                this._setBusinessSectionDataByPerson(v);
                break;
            case "unit":
                this._setBusinessSectionDataByUnit(v);
                break;
            case "activity":
                this._setBusinessSectionDataByActivity(v);
                break;
            case "splitValue":
                this._setBusinessSectionDataBySplitValue(v);
                break;
            case "script":
                this._setBusinessSectionDataByScript(this.json.sectionByScript.code, v);
                break;
            default:
                if (this.form.businessData.data[this.json.name]) {
                    this.form.businessData.data[this.json.name] = v;
                } else {
                    this.form.businessData.data[this.json.name] = v;
                    this.form.Macro.environment.setData(this.form.businessData.data);
                }
        }
    },
    _setBusinessSectionDataByPerson: function (v) {
        var resetData = false;
        var key = layout.desktop.session.user.id;
        this.form.sectionListObj[this.json.name] = key;

        var dataObj = this.form.businessData.data[this.json.name];
        if (!dataObj) {
            dataObj = {};
            this.form.businessData.data[this.json.name] = dataObj;
            resetData = true;
        }
        if (!dataObj[key]) resetData = true;
        dataObj[key] = v;

        if (resetData) this.form.Macro.environment.setData(this.form.businessData.data);
    },
    _setBusinessSectionDataByUnit: function (v) {
        var resetData = false;
        var key = (this.form.businessData.task) ? this.form.businessData.task.unit : "";

        if (key) {
            this.form.sectionListObj[this.json.name] = key;
            var dataObj = this.form.businessData.data[this.json.name];
            if (!dataObj) {
                dataObj = {};
                this.form.businessData.data[this.json.name] = dataObj;
                resetData = true;
            }
            if (!dataObj[key]) resetData = true;
            dataObj[key] = v;
        }

        if (resetData) this.form.Macro.environment.setData(this.form.businessData.data);
    },
    _setBusinessSectionDataByActivity: function (v) {
        var resetData = false;
        var key = (this.form.businessData.work) ? this.form.businessData.work.activity : "";

        if (key) {
            this.form.sectionListObj[this.json.name] = key;
            var dataObj = this.form.businessData.data[this.json.name];
            if (!dataObj) {
                dataObj = {};
                this.form.businessData.data[this.json.name] = dataObj;
                resetData = true;
            }
            if (!dataObj[key]) resetData = true;
            dataObj[key] = v;
        }

        if (resetData) this.form.Macro.environment.setData(this.form.businessData.data);
    },
    _setBusinessSectionDataBySplitValue: function (v) {
        var resetData = false;
        var key = (this.form.businessData.work) ? this.form.businessData.work.splitValue : "";

        if (key) {
            this.form.sectionListObj[this.json.name] = key;
            var dataObj = this.form.businessData.data[this.json.name];
            if (!dataObj) {
                dataObj = {};
                this.form.businessData.data[this.json.name] = dataObj;
                resetData = true;
            }
            if (!dataObj[key]) resetData = true;
            dataObj[key] = v;
        }

        if (resetData) this.form.Macro.environment.setData(this.form.businessData.data);
    },
    _setBusinessSectionDataByScript: function (code, v) {
        var resetData = false;
        var key = this.form.Macro.exec(code, this);

        if (key) {
            this.form.sectionListObj[this.json.name] = key;
            var dataObj = this.form.businessData.data[this.json.name];
            if (!dataObj) {
                dataObj = {};
                this.form.businessData.data[this.json.name] = dataObj;
                resetData = true;
            }
            if (!dataObj[key]) resetData = true;
            dataObj[key] = v;
        }

        if (resetData) this.form.Macro.environment.setData(this.form.businessData.data);
    },

    createErrorNode: function (text) {
        var _self = this;
        var node;
        if (this.processor.css && this.processor.css.errorContentNode) {
            node = new Element("div", {
                "styles": this.processor.css.errorContentNode,
                "text": text
            });
            if (this.processor.css.errorCloseNode) {
                var closeNode = new Element("div", {
                    "styles": this.processor.css.errorCloseNode,
                    "events": {
                        "click": function () {
                            this.destroy();
                            if (_self.errorHeightOverflow) {
                                _self.errorHeightOverflow = false;
                                _self.processor.errorHeightChange();
                            }
                        }.bind(node)
                    }
                }).inject(node);
            }
        } else {
            node = new Element("div");
            var iconNode = new Element("div", {
                "styles": {
                    "width": "20px",
                    "height": "20px",
                    "float": "left",
                    "background": "url(" + "../x_component_process_Xform/$Form/default/icon/error.png) center center no-repeat"
                }
            }).inject(node);
            var textNode = new Element("div", {
                "styles": {
                    "height": "auto",
                    "min-height": "20px",
                    "line-height": "20px",
                    "margin-left": "20px",
                    "color": "red",
                    "word-break": "break-all"
                },
                "text": text
            }).inject(node);
        }
        return node;
    },
    notValidationMode: function (text) {
        if (!this.isNotValidationMode) {
            //this.isNotValidationMode = true;
            //this.node.store("borderStyle", this.node.getStyles("border-left", "border-right", "border-top", "border-bottom"));
            //this.node.setStyle("border-color", "red");

            this.errNode = this.createErrorNode(text);
            if (this.errContainer) {
                this.errContainer.empty();
                this.errNode.inject(this.errContainer);
            } else {
                this.errNode.inject(this.container, "after");
            }
            if (!this.errNode.isIntoView()) this.errNode.scrollIntoView(false);
            var errorSize = this.errNode.getSize();
            if (!layout.mobile && errorSize.y > 26) {
                this.errorHeightOverflow = true;
            }
        }
    },
    needValid: function () {
        return ((this.json.validationCount && typeOf(this.json.validationCount.toInt()) === "number") ||
            (this.json.validation && this.json.validation.code));
    },
    validOnSelect: function () {
        if (!this.errNode) return true;
        var flag = true;
        if (this.json.validationCount && typeOf(this.json.validationCount.toInt()) === "number") {
            if (this.selector.selector.selectedItems.length < this.json.validationCount.toInt()) {
                flag = MWF.xApplication.process.Xform.LP.selectItemCountNotice.replace("{count}", this.json.validationCount);
            }
        }
        if (flag === true) {
            if (this.json.validation && this.json.validation.code) {
                var data = this.getData();
                this.setData(data);
                flag = this.form.Macro.exec(this.json.validation.code, this);
                if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
            }
        }
        if (flag.toString() != "true") {
            this.notValidationMode(flag);
            this.processor.errorHeightChange();
            return false;
        } else if (this.errNode) {
            this.errNode.destroy();
            this.errNode = null;
            if (this.errorHeightOverflow) {
                this.errorHeightOverflow = false;
                this.processor.errorHeightChange();
            }
        }
        return true;
    },
    validation: function () {
        var data = this.getData();
        this.setData(data);
        var flag = true;
        if (this.json.validationCount && typeOf(this.json.validationCount.toInt()) === "number") {
            if (data.length < this.json.validationCount.toInt()) {
                //"请至少选择" + this.json.validationCount + "项"
                flag = MWF.xApplication.process.Xform.LP.selectItemCountNotice.replace("{count}", this.json.validationCount);
            }
        }

        if (flag === true) {
            if (this.json.validation && this.json.validation.code) {
                flag = this.form.Macro.exec(this.json.validation.code, this);
                if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
            }
        }

        if (flag.toString() != "true") {
            this.notValidationMode(flag);
            return false;
        } else if (this.errNode) {
            this.errNode.destroy();
            this.errNode = null;
        }
        return true;
    }
});

MWF.ProcessFlow.Processor.EmpowerChecker = new Class({
    initialize: function (org) {
        this.org = org;
        this.form = org.form;
        this.json = org.json;
        this.processor = org.processor;
        this.flow = org.processor.flow;
    },
    load: function (data, callback, container) {
        var p = this.getEmpowerData( data );
        return Promise.resolve(p).then(function ( eArr ) {
            if (eArr.length > 0) {
                this.loadDom(eArr, data, container);
            } else {
                if (callback) callback(data);
            }
        }.bind(this));
    },
    loadDom: function(eArr, data, container){
        var _self = this;
        var titleNode = new Element("div.o2flow-empower-areaTitle").inject(container);
        new Element("div.o2flow-empower-areaTitleText", { text: this.json.title }).inject(titleNode);
        var selectAllNode = new Element("div.o2flow-empower-selectAllNode").inject( titleNode );
        var contentNode = new Element("div.o2flow-empower-areaContent").inject(container);

        this.selectAllRadio = new MWF.ProcessFlow.widget.Radio2(selectAllNode, this.flow, {
            optionList: [{
                value: "true", text: this.flow.lp.selectAll
            }],
            value: "true",
            onCheck: function(el, value){
                _self.empowerCheckbox.checkAll();
            },
            onUncheck: function(el, value){
                _self.empowerCheckbox.uncheckAll();
            },
        });
        this.selectAllRadio.load();

        this.empowerCheckbox = new MWF.ProcessFlow.widget.Checkbox(contentNode, this.flow, {
            optionList: eArr.map(function (e) {
                return {
                    value: e.id,
                    text: e.fromIdentity.split("@")[0] + " " + this.flow.lp.empowerTo + " " + e.toIdentity.split("@")[0],
                    data: e
                };
            }.bind(this)),
            values: eArr.map(function (e) {
                return e.id;
            })
        });
        this.empowerCheckbox.load();
    },
    hasEmpowerIdentity: function (data) {
        var p = this.getEmpowerData( data );
        return Promise.resolve(p).then(function ( arr ) {
            return arr.length > 0;
        });
    },
    isNeedCheck: function(data){
        return typeOf(data) === "array" && this.json.isCheckEmpower && this.json.identityResultType === "identity";
    },
    getIdentityDn: function( data ){
        return data.filter(function (d) {
            return d.distinguishedName && d.distinguishedName.substr(d.distinguishedName.length - 1, 1).toLowerCase() === "i";
        }).map(function (d) {
            return d.distinguishedName;
        });
    },
    getEmpowerData: function(data){
        if( !this.isNeedCheck(data) )return [];
        var array = this.getIdentityDn( data );
        if( array.length < 1)return [];
        return o2.Actions.get("x_organization_assemble_express").listEmpowerWithIdentity({
            "application": (this.form.businessData.work || this.form.businessData.workCompleted).application,
            "process": (this.form.businessData.work || this.form.businessData.workCompleted).process,
            "work": (this.form.businessData.work || this.form.businessData.workCompleted).id,
            "identityList": array
        }, function (json) {
            return json.data.filter(function (d) {
                return d.fromIdentity !== d.toIdentity;
            });
        }.bind(this), function () {
            return [];
        })
    },
    getSelectedData: function (callback) {
        var json = {};
        this.empowerSelectNodes.each(function (node) {
            if (node.retrieve("isSelected")) {
                var d = node.retrieve("data");
                json[d.fromIdentity] = d;
            }
        }.bind(this));
        if (callback) callback(json);
    },
    setIgnoreEmpowerFlag : function(data, callback){
        var ignoreList = this.empowerCheckbox.getUncheckData().map(function (d) {
            return d.data.fromIdentity;
        });
        for( var i=0; i<data.length; i++ ){
            var d = data[i];
            if( ignoreList.indexOf( d.distinguishedName ) > -1 ){
                d.ignoreEmpower = true;
            }else if( d.ignoreEmpower ){
                delete  d.ignoreEmpower;
            }
        }
        if( callback )callback( data );
    },
});

MWF.ProcessFlow.Processor.UnitOptions = new Class({
    Extends: MWF.APPOrg.UnitOptions
});

MWF.ProcessFlow.Processor.IdentityOptions = new Class({
    Extends: MWF.APPOrg.IdentityOptions
});

MWF.ProcessFlow.Processor.GroupOptions = new Class({
    Extends: MWF.APPOrg.GroupOptions
});

MWF.ProcessFlow.widget = {};

MWF.xDesktop.requireApp("Template", "MTooltips", null, false);
MWF.ProcessFlow.widget.QuickSelect = new Class({
    Extends: MTooltips,
    options: {
        event : "click",
        hasArrow : false,
        hideByClickBody : true,
        offset : {
            x : 0,
            y : 5
        },
        nodeStyles: {
            "font-size" : "13px",
            "position" : "absolute",
            "max-width" : "500px",
            "min-width" : "260px",
            "z-index" : 20002,
            "background-color" : "#fff",
            "padding" : "10px 0px 10px 0px",
            "border-radius" : "8px",
            "box-shadow": "0px 0px 8px 0px rgba(0,0,0,0.25)",
            "-webkit-user-select": "text",
            "-moz-user-select": "text"
        }
    },
    _loadCustom : function( callback ){
        this.node.loadCss( this.flow.path + this.flow.options.style + "/style.css" );
        var width = this.target.getSize().x ;
        this.node.setStyles({
            "max-width": width+"px",
            "min-width": width+"px"
        });

        var work = this.flow.form.businessData.work;
        var d = {
            process: work.process,
            activity: work.activity,
            activityAlias: work.activityAlias,
            activityName: work.activityName
        };
        // if( work.activityAlias ){
        //     d.activityAlias = work.activityAlias
        // }else{
        //     d.activityName = work.activityName;
        // }
        var p = o2.Actions.load("x_processplatform_assemble_surface").TaskProcessModeAction.listMode( d );
        Promise.resolve(p).then(function (json) {
            debugger;
             var list = this.filterData(json.data);
             var data = list.map(function (d) {
                 return {
                     type: d.action,
                     text: this.getText( d ),
                     data: d
                 }
             }.bind(this));
            if( !data || !data.length ){
                new Element("div.o2flow-quick-select-item", {
                    text: this.flow.lp.noQuickSelectDataNote
                }).inject( this.contentNode );
            }else{
                this.loadItems(data)
            }

            //setTimeout( function () {
            if(callback)callback( data );
            //}, 50);
        }.bind(this));
    },
    loadItems: function(data){
        var _self = this;
        data.each( function (d) {
            var item = new Element("div.o2flow-quick-select-item", {
                events: {
                    mouseover: function () {
                        this.addClass("o2flow-quick-select-item-active");
                        this.getFirst().addClass("o2flow-quick-select-item-contnet-active");
                        if( _self.flow.options.mainColorEnable ){
                            this.addClass("mainColor_bg");
                            this.getFirst().addClass("mainColor_bg");
                        }
                    },
                    mouseout: function () {
                        this.removeClass("o2flow-quick-select-item-active");
                        this.getFirst().removeClass("o2flow-quick-select-item-contnet-active");
                        if( _self.flow.options.mainColorEnable ) {
                            this.removeClass("mainColor_bg");
                            this.getFirst().removeClass("mainColor_bg");
                        }
                    },
                    click: function () {
                        var d = Object.clone( item.retrieve("data"));
                        _self.flow.changeAction( d.type, d.data );
                        _self.hide();
                    }
                }
            }).inject( this.contentNode );
            item.store( "data", d );
            var title = new Element("div.o2flow-quick-select-itemtitle", {
                text: this.flow.lp.flowActions[d.type]
            }).inject( item );
            title.addClass( "o2flow-"+d.type+"-color" );
            var content = new Element("div.o2flow-quick-select-itemtext", {
                text: "："+ d.text
            }).inject( item )
        }.bind(this))
    },
    filterData: function( data ){
        //var onekeyList = listData();
        return data.filter(function (d) {
            var flag = (d.action === "process" && this.flow.processEnable) || (d.action === "reset" && this.flow.resetEnable) || (d.action === "addTask" && this.flow.addTaskEnable);
            if( !flag )return false;
            if( d.action === "process" && !this.flow.getRouteConfig(d.routeName) )return false;
            return true;
        }.bind(this));
    },
    getText: function( d ){
        //选择[送办理]，意见：请办理，处理人：张三、李四、张三、李四、张三、李四、张三、李四、张三、李四、张三、李四、张三、李四
        //选择[前加签]，意见：请处理，加签人：赵六
        //保留待办，意见：请处理，重置给：王五
        var text, orgtexts = [], lp = this.flow.lp;
        switch (d.action) {
            case "process":
                Object.each( d.organizations, function (value, key) {
                    if( value && value.length ){
                        orgtexts.push( ( this.flow.getSingleOrgConfig( d.routeId , key ) ).title + "：" + value.clean().map(function(v){ return v.split("@")[0]; }).join("、"));
                    }
                }.bind(this));
                text = lp.submitQuickText.replace("{route}", d.routeName ).replace("{opinion}", d.opinion);
                if( !orgtexts.length ){
                    return text.replace("{org}", "");
                }else{
                    return text.replace("{org}", "，" + orgtexts.join("，") );
                }
            case "addTask":
                if( d.organizations && d.organizations.default ){
                    orgtexts.push( d.organizations.default.clean().map(function(v){ return v.split("@")[0]; }).join("、") );
                }
                var route = d.routeId === "before" ? lp.addTaskBefore : lp.addTaskAfter;
                var mode = lp[ d.routeGroup ] || "";
                return lp.addTaskQuickText.replace("{route}", route ).replace("{mode}", mode).replace("{opinion}", d.opinion).replace("{org}",  orgtexts.join("，") );
            case "reset":
                if( d.organizations && d.organizations.default ){
                    orgtexts.push( d.organizations.default.clean().map(function(v){ return v.split("@")[0]; }).join("、") );
                }
                return lp.resetQuickText.replace("{opinion}", d.opinion).replace("{org}", orgtexts.join("，") );
                //return lp.resetQuickText.replace("{flag}", d.keepTask ? "" : lp.not ).replace("{opinion}", d.opinion).replace("{org}", orgtexts.join("，") );
        }

    },
    saveData: function () {
        debugger;
        var work = this.flow.form.businessData.work || this.flow.form.businessData.workCompleted;
        var d = {
            process: work.process,
            processName: work.processName,
            activity: work.activity,
            activityName: work.activityName,
            activityAlias: work.activityAlias,
            action: this.flow.currentAction
        };
        // routeId: "",
        // routeName: "",
        // routeGroup:决策组.
        // keepTask:是否保留待办.
        // opinion:意见.
        // organizations:人员组织列表.
        var quickData = {};
        switch ( this.flow.currentAction ) {
            case "process":
                quickData = this.flow.processor.getQuickData();
                break;
            case "addTask":
                quickData = this.flow.addTask.getQuickData();
                break;
            case "reset":
                quickData = this.flow.reset.getQuickData();
                break;
        }
        d = Object.merge(d, quickData);
        o2.Actions.load("x_processplatform_assemble_surface").TaskProcessModeAction.saveMode( d, null, null, false );
    },
});

MWF.ProcessFlow.widget.Opinion = new Class({
    Implements: [Options, Events],
    options: {
        isHandwriting: false,
        opinion: "",
        tabletToolHidden: [],
        tabletWidth: 0,
        tabletHeight: 0
    },
    initialize: function( container, flow, options ){
        this.setOptions( options );
        this.container = container;
        this.flow = flow;
        this.lp = flow.lp;
    },
    load: function(){
        this.loadData( function () {
            this.loadDom();
        }.bind(this));
    },
    loadData: function ( callback ) {
        MWF.require("MWF.widget.UUID", function () {
            MWF.UD.getDataJson("idea", function (json) {
                if (json) {
                    if (json.ideas) {
                        this.opinionList = json.ideas;
                        if(callback)callback();
                    }else{
                        this.opinionList = [];
                        if(callback)callback();
                    }
                } else {
                    MWF.UD.getPublicData("idea", function (pjson) {
                        if (pjson && pjson.ideas) {
                            this.opinionList = pjson.ideas;
                            if(callback)callback();
                        }else{
                            this.opinionList = [];
                            if(callback)callback();
                        }
                    }.bind(this));
                }
            }.bind(this));
        }.bind(this));
    },
    loadDom: function(){
        var url = this.flow.path+this.flow.options.style+"/widget/opinion.html";
        this.container.loadHtml(url, {
                "bind": {
                    "lp": this.lp, "opinionList": this.opinionList, "opinion": this.options.opinion || ""
                },
                "module": this
            }, function(){
                if( this.options.isHandwriting )this.handwritingButton.show();
                this.opinionTextarea.addEvent("change", function () {
                    this.removeRequireStyle();
                }.bind(this));
                this.loaded = true;
                this.fireEvent("load");
            }.bind(this)
        );
    },
    setRequireStyle: function(){
        this.opinionTextarea.addClass("o2flow-invalid-bg");
    },
    removeRequireStyle: function(){
        if(this.opinionTextarea.hasClass("o2flow-invalid-bg")){
            this.opinionTextarea.removeClass("o2flow-invalid-bg");
        }
    },
    getValue: function(){
        return this.opinionTextarea.get("value");
    },
    setValue: function( value ){
        this.opinionTextarea.set("value", value);
    },
    selectOpinion: function( opinion ){
        var op = this.opinionTextarea.get("value");
        if ( !op ) {
            this.opinionTextarea.set("value", opinion );
        } else {
            this.opinionTextarea.set("value", op + ", " + opinion );
        }
        this.removeRequireStyle();
    },
    overItemNode: function (ev) {
        ev.target.addClass("o2flow-bg-opacity");
        if( this.flow.options.mainColorEnable ){
            ev.target.addClass("mainColor_bg_opacity");
        }
    },
    outItemNode: function (ev) {
        ev.target.removeClass("o2flow-bg-opacity");
        if( this.flow.options.mainColorEnable ) {
            ev.target.removeClass("mainColor_bg_opacity");
        }
    },

    handwriting: function () {
        if( !this.tablet )this.createHandwriting();
        this.handwritingMask.show();
        this.handwritingNode.show();
        this.handwritingNode.position({
            "relativeTo": this.flow.node,
            "position": "center",
            "edge": "center"
        });
    },
    createHandwriting: function () {
        this.handwritingMask.inject( this.flow.node );
        this.handwritingNode.show().inject(this.flow.node, "after");
        //兼容以前的默认高宽
        var x = 600;
        var y = 320;

        x = Math.max(this.options.tabletWidth || x, 600);
        this.options.tabletWidth = x;
        y = Math.max(this.options.tabletHeight ? (parseInt(this.options.tabletHeight) + 110) : y, 320);

        var zidx = this.flow.node.getStyle("z-index");
        this.handwritingNode.setStyles({
            "height": "" + y + "px",
            "width": "" + x + "px",
            "z-index": zidx + 1
        });
        this.handwritingNode.position({
            "relativeTo": this.flow.node,
            "position": "center",
            "edge": "center"
        });

        if( this.flow.options.mainColorEnable )this.handwritingSaveNode.addClass("mainColor_bg");
        var h = this.handwritingSaveNode.getSize().y + this.flow.getOffsetY(this.handwritingSaveNode);
        this.handwritingAreaNode.setStyle("height", "" + ( y - h ) + "px");

        MWF.require("MWF.widget.Tablet", function () {
            var handWritingOptions = this.getHandWritingOptions();

            this.tablet = new MWF.widget.Tablet(this.handwritingAreaNode, handWritingOptions, null);
            this.tablet.load();
        }.bind(this));
    },
    getHandWritingOptions: function(){
        return {
            "style": "default",
            "toolHidden": this.options.tabletToolHidden || [],
            "contentWidth": this.options.tabletWidth || 0,
            "contentHeight": this.options.tabletHeight || 0,
            "iconfontEnable": true,
            "mainColorEnable": this.flow.options.mainColorEnable,
            "onSave": function (base64code, base64Image, imageFile) {
                if( !this.tablet.isBlank() ){
                    this.handwritingFile = imageFile;
                    this.handwritingButton.getElement("i").removeClass("o2icon-edit2").
                    addClass("o2icon-checkbox").addClass("o2flow-handwriting-buttonok");
                    this.removeRequireStyle();
                }else{
                    this.handwritingFile = null;
                    this.handwritingButton.getElement("i").addClass("o2icon-edit2").
                    removeClass("o2icon-checkbox").removeClass("o2flow-handwriting-buttonok");
                }
                this.handwritingNode.hide();
                this.handwritingMask.hide();

            }.bind(this),
            "onCancel": function () {
                this.handwritingFile = null;
                this.handwritingButton.getElement("i").addClass("o2icon-edit2").
                removeClass("o2icon-checkbox").removeClass("o2flow-handwriting-buttonok");
                this.handwritingNode.hide();
                this.handwritingMask.hide();
            }.bind(this)
        };
    },
    saveTablet: function () {
        if (this.tablet) this.tablet.save();
    }
});

MWF.ProcessFlow.widget.Radio = new Class({
    Implements: [Options, Events],
    options: {
        optionList: [],
        value: null,
        cancelEnable: true
    },
    initialize: function( container, flow, options ){
        this.container = container;
        this.flow = flow;
        this.lp = flow.lp;
        this.setOptions( options );
    },
    load: function(){
        this.checkedItems = [];
        this.container.loadHtml(this.getUrl(), {"bind": {"lp": this.lp, "optionList": this.options.optionList}, "module": this}, function(){
            if( this.options.value !== null )this.setValue( this.options.value );
            this.fireEvent("load");
        }.bind(this));
    },
    getUrl: function(){
        return this.flow.path+this.flow.options.style+"/widget/radio.html";
    },
    setValue: function( values ){
        while( this.checkedItems.length ){
            this.uncheck( this.checkedItems[0] );
        }
        if( typeOf( values ) === "null" )return;
        values = typeOf( values ) === "array" ? values : [values];
        this.container.getElements(".o2flow-radio").each(function (el) {
            if(values.contains( el.dataset["o2Value"] )){
                this.check(el)
            }
        }.bind(this))
    },
    getData: function( value ){
        if( !value )value = this.getValue();
        var arr = this.options.optionList.filter(function (o) {
           return o.value === value;
        });
        return arr[0] || null;
    },
    getValue: function(){
        var data = this.checkedItems.map(function (item) {
            return item.dataset["o2Value"];
        });
        return data[0] || null;
    },
    getText: function(){
        var data = this.checkedItems.map(function (item) {
            return item.dataset["o2Text"];
        });
        return data[0] || null;
    },
    toggle: function( ev ){
        var el = this.flow.getEl(ev, "o2flow-radio");
        if( this.checkedItems.contains( el ) ){
            if( this.options.cancelEnable )this.uncheck( el, true )
        }else{
            this.check( el )
        }
    },
    check: function(el){
        while( this.checkedItems.length ){
            this.uncheck( this.checkedItems[0] );
        }
        el.addClass("o2flow-radio-active");
        if( this.flow.options.mainColorEnable )el.addClass("mainColor_bg");
        el.getElement("i").removeClass("o2icon-icon_circle").addClass("o2icon-checkbox").addClass("o2flow-radio-icon");
        el.dataset["o2Checked"] = true;
        this.checkedItems.push(el);
        this.removeRequireStyle();
        this.fireEvent("check", [el, el.dataset["o2Value"]])
    },
    uncheck: function(el, isFire){
        el.removeClass("o2flow-radio-active");
        if( this.flow.options.mainColorEnable )el.removeClass("mainColor_bg");
        el.getElement("i").removeClass("o2icon-checkbox").addClass("o2icon-icon_circle").removeClass("o2flow-radio-icon");
        el.dataset["o2Checked"] = false;
        this.checkedItems.erase(el);
        if(isFire)this.fireEvent("uncheck", [el, el.dataset["o2Value"]])
    },
    setRequireStyle: function(){
        this.container.addClass("o2flow-invalid-bg");
    },
    removeRequireStyle: function(){
        if(this.container.hasClass("o2flow-invalid-bg")){
            this.container.removeClass("o2flow-invalid-bg");
        }
    },
    // over: function (ev) {
    //     var el = this.flow.getEl(ev, "o2flow-radio");
    //     if( !el.dataset["o2Checked"] ){
    //         el.getElement("i").addClass("o2flow-radio-over");
    //     }
    // },
    // out: function (ev) {
    //     var el = this.flow.getEl(ev, "o2flow-radio");
    //     el.getElement("i").removeClass("o2flow-radio-over")
    // }
});

MWF.ProcessFlow.widget.Radio2 = new Class({
    Extends: MWF.ProcessFlow.widget.Radio,
    options: {
        optionList: [],
        value: null,
        cancelEnable: true,
        activeIcon: "o2icon-radio-checked"
    },
    getUrl: function(){
        return this.flow.path+this.flow.options.style+"/widget/radio2.html";
    },
    setValue: function( values ){
        while( this.checkedItems.length ){
            this.uncheck( this.checkedItems[0] );
        }
        values = typeOf( values ) === "array" ? values : [values];
        this.container.getElements(".o2flow-radio2").each(function (el) {
            if(values.contains( el.dataset["o2Value"] ))this.check(el)
        }.bind(this))
    },
    toggle: function( ev ){
        var el = this.flow.getEl(ev, "o2flow-radio2");
        if( this.checkedItems.contains( el ) ){
            if( this.options.cancelEnable )this.uncheck( el, true )
        }else{
            this.check( el )
        }
    },
    check: function(el){
        while( this.checkedItems.length ){
            this.uncheck( this.checkedItems[0] );
        }
        el.addClass("o2flow-radio2-active");
        if( this.flow.options.mainColorEnable )el.addClass("mainColor_color");
        el.getElement("i").removeClass("o2icon-icon_circle").addClass(this.options.activeIcon).addClass("o2flow-radio2-icon");
        if( this.flow.options.mainColorEnable )el.getElement("i").addClass("mainColor_color");
        el.dataset["o2Checked"] = true;
        this.checkedItems.push(el);
        this.removeRequireStyle();
        this.fireEvent("check", [el, el.dataset["o2Value"]])
    },
    uncheck: function(el, isFire){
        el.removeClass("o2flow-radio2-active");
        if( this.flow.options.mainColorEnable )el.removeClass("mainColor_color");
        el.getElement("i").removeClass(this.options.activeIcon).addClass("o2icon-icon_circle").removeClass("o2flow-radio2-icon");
        if( this.flow.options.mainColorEnable )el.getElement("i").removeClass("mainColor_color");
        el.dataset["o2Checked"] = false;
        this.checkedItems.erase(el);
        if(isFire)this.fireEvent("uncheck", [el, el.dataset["o2Value"]])
    }
});

MWF.ProcessFlow.widget.Checkbox = new Class({
    Implements: [Options, Events],
    options: {
        optionList: [],
        values: []
    },
    initialize: function( container, flow, options ){
        this.container = container;
        this.flow = flow;
        this.lp = flow.lp;
        this.setOptions( options );
    },
    load: function(){
        this.checkedItems = [];
        this.container.loadHtml(this.getUrl(), {"bind": {"lp": this.lp, "optionList": this.options.optionList}, "module": this}, function(){
            if( this.options.values.length )this.setValue( this.options.values );
            this.fireEvent("load");
        }.bind(this));
    },
    getUrl: function(){
        return this.flow.path+this.flow.options.style+"/widget/checkbox.html";
    },
    setValue: function( values ){
        while( this.checkedItems.length ){
            this.uncheck( this.checkedItems[0] );
        }
        values = typeOf( values ) === "array" ? values : [values];
        this.container.getElements(".o2flow-radio").each(function (el) {
            if(values.contains( el.dataset["o2Value"] )){
                this.check(el)
            }
        }.bind(this))
    },
    getData: function( value ){
        if( !value )value = this.getValue();
        value = typeOf( value ) === "array" ? value : [value];
        var arr = this.options.optionList.filter(function (o) {
            return value.contains(o.value);
        });
        return arr;
    },
    getValue: function(){
        return this.checkedItems.map(function (item) {
            return item.dataset["o2Value"];
        });
    },
    getText: function(){
        return this.checkedItems.map(function (item) {
            return item.dataset["o2Text"];
        });
    },
    toggle: function( ev ){
        var el = this.flow.getEl(ev, "o2flow-radio");
        if( this.checkedItems.contains( el ) ){
            this.uncheck( el, true )
        }else{
            this.check( el )
        }
    },
    check: function(el){
        el.addClass("o2flow-radio-active");
        if( this.flow.options.mainColorEnable )el.addClass("mainColor_bg");
        el.getElement("i").removeClass("o2icon-icon_circle").addClass("o2icon-checkbox").addClass("o2flow-radio-icon");
        el.dataset["o2Checked"] = true;
        this.checkedItems.push(el);
        this.removeRequireStyle();
        this.fireEvent("check", [el, el.dataset["o2Value"]])
    },
    uncheck: function(el, isFire){
        el.removeClass("o2flow-radio-active");
        if( this.flow.options.mainColorEnable )el.removeClass("mainColor_bg");
        el.getElement("i").removeClass("o2icon-checkbox").addClass("o2icon-icon_circle").removeClass("o2flow-radio-icon");
        el.dataset["o2Checked"] = false;
        this.checkedItems.erase(el);
        if(isFire)this.fireEvent("uncheck", [el, el.dataset["o2Value"]])
    },
    checkAll: function(){
        this.container.getElements(".o2flow-radio").each(function (el) {
            this.check(el)
        }.bind(this))
    },
    uncheckAll: function(){
        this.container.getElements(".o2flow-radio").each(function (el) {
            this.uncheck(el)
        }.bind(this))
    },
    getUncheckData: function(){
        var value = this.getValue();
        return this.options.optionList.filter(function (o) {
            return !value.contains(o.value);
        });
    },
    setRequireStyle: function(){
        this.container.addClass("o2flow-invalid-bg");
    },
    removeRequireStyle: function(){
        if(this.container.hasClass("o2flow-invalid-bg")){
            this.container.removeClass("o2flow-invalid-bg");
        }
    }
});
