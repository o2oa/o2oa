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
        processOptions: {},
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

        this.navi = [];
        if( this.processEnable )this.navi.push({ key: "process", label: this.lp.flowActions.process });
        if( this.addTaskEnable )this.navi.push({ key: "addTask", label: this.lp.flowActions.addTask });
        if( this.resetEnable )this.navi.push({ key: "reset", label: this.lp.flowActions.reset });

        var url = this.path+this.options.style+"/main.html";
        this.container.loadHtml(url, {"bind": {"lp": this.lp, "navi": this.navi}, "module": this}, function(){
            debugger;
            this.changeAction( this.navi[0].key );
            this.loadQuickSelect();
            //this.checkLoadEvent();
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
            // var nodeSize = this.node.getSize();
            // this.container.setStyles({
            //     "height": nodeSize.y,
            //     "width": nodeSize.x
            // });
            if( this.currentAction === "process" ){
                if( this.processor.getMaxOrgLength() > 1 ){
                    this.node.addClass("o2flow-node-wide").removeClass("o2flow-node")
                }
            }
            this.fireEvent("load");
        }
    },
    changeAction: function( action ){
        if( this.currentAction ){
            this[ this.currentAction+"ContentNode" ].hide();
            this[ this.currentAction+"TitleNode" ].removeClass("mainColor_color").removeClass("mainColor_border");
        }

        this[ action+"ContentNode" ].show();
        this[ action+"TitleNode" ].addClass("mainColor_color").addClass("mainColor_border");

        this.currentAction = action;

        switch (action) {
            case "process":
                this.loadProcessor();
                break;
            case "addTask":
                this.loadAddTask();
                break;
            case "reset":
                this.loadReset();
                break;
        }
    },
    loadProcessor: function () {
        if( this.processor ){
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
    },
    loadReset: function(){
        if( this.reset ){
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
    },
    loadAddTask: function(){
        if( this.addTask ){
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
    },
    loadQuickSelect: function(){
        this.quickSelector = new MWF.ProcessFlow.widget.QuickSelect(
            this.form.app ? this.form.app.content : $(document.body),
            this.quickSelectNode, this.form.app, {}
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
        }
    },
    destroy: function () {
        if( this.processor )this.processor.destroy();
        if( this.reset )this.reset.destroy();
        if( this.addTask )this.addTask.destroy();
        if(this.quickSelector)this.quickSelector.destroy();
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
    }
});

MWF.ProcessFlow.Reset = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options:{
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
        this.load();
    },
    load: function(){
        this.container.loadHtml(this.getUrl(), {"bind": {"lp": this.lp}, "module": this}, function(){
            this.loadOpinion();
            this.loadOrg();
            this.afterLoad();
            this.fireEvent("load");
        }.bind(this));
    },
    getUrl: function(){
        return this.flow.path+this.flow.options.style+"/reset.html";
    },
    show: function(){

    },
    afterLoad: function () {
        debugger;
        this.keepOption = new MWF.ProcessFlow.widget.Radio(this.keepOptionArea, this.flow, {
            optionList: [{
                text: this.lp.keepTask,
                value: true
            }]
        });
        this.keepOption.load();
    },
    loadOpinion: function(){
        var opt = Object.clone(this.flow.options.opinionOptions);
        opt.isHandwriting = this.options.isHandwriting;
        this.opinion = new MWF.ProcessFlow.widget.Opinion( this.opinionContent, this.flow, opt );
        this.opinion.load();
    },
    loadOrg: function(){
        this.getSelOptions( function (options) {
            this.selector = new MWF.O2Selector(this.orgsArea, options)
        }.bind(this) );
    },
    getSelOptions: function( callback ){
        o2.Actions.get("x_processplatform_assemble_surface").listTaskByWork(this.businessData.work.id, function(json){
            var identityList = [];
            json.data.each(function(task){
                identityList.push(task.identity);
            });
            this._getSelOptions(identityList, callback);
        }.bind(this))
    },
    _getSelOptions: function (exclude, callback) {
        var options = this.getSelDefaultOptions();

        var range = this.businessData.activity.resetRange || "department";
        switch (range) {
            case "unit":
                options.units = this.businessData.task.unit ? [this.businessData.task.unit] : [];
                options.exclude = exclude;
                callback( options );
                break;
            case "topUnit":
                MWF.require("MWF.xScript.Actions.UnitActions", function () {
                    orgActions = new MWF.xScript.Actions.UnitActions();
                    var data = { "unitList": [this.businessData.task.unit] };
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
                    var resetRange = this.Macro.exec(activityJson.data.activity.resetRangeScriptText, this);
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
        var defaultOpt;
        if (layout.mobile) {
            defaultOpt = {
                "type": "identity",
                "style": "default",
                "zIndex": 3000,
                "count": this.businessData.activity.resetCount || 0
            };
        } else {
            defaultOpt = {
                "type": "identity",
                "style": "flow",
                "width": "auto",
                "height": MWF.ProcessFlow_ORG_HEIGHT,
                "count": this.businessData.activity.resetCount || 0,
                "embedded": true,
                "hasLetter": false, //字母
                "hasTop": true //可选、已选的标题
            };
        }
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
            this.flow.noticeError(MWF.xApplication.process.Xform.LP.inputResetPeople);
            return false;
        }
        var opinion = this.opinion.getValue();
        var keep = this.keepOption.getValue();

        var nameText = [];
        names.each(function (n) { nameText.push(MWF.name.cn(n)); });
        if (!opinion) {
            opinion = MWF.xApplication.process.Xform.LP.resetTo + ": " + nameText.join(", ");
        }
        this.fireEvent("submit", [names, opinion, keep]);
    },
    destroy: function () {
        if (this.orgItem && this.orgItem.clearTooltip){
            this.orgItem.clearTooltip();
        }
        if (this.node) this.node.empty();
        // delete this.task;
        // delete this.node;
    },
});

MWF.ProcessFlow.AddTask = new Class({
    Extends: MWF.ProcessFlow.Reset,
    getUrl: function(){
        return this.flow.path+this.flow.options.style+"/addTask.html";
    },
    afterLoad: function () {
        // this.keepOption = new MWF.ProcessFlow.widget.Radio(this.keepOptionArea, this, {
        //     optionsList: [{
        //         text: this.lp.keepTask,
        //         value: true
        //     }]
        // });
        // this.keepOption.load();
    },
    // load: function(){
    //     this.content = this.container;
    //
    //     this.opinionTitle = new Element("div", {
    //         "styles": this.css.opinionTitle,
    //         "text": "加签意见"
    //     }).inject(this.content);
    //     this.opinionArea = new Element("div", {"styles": this.css.opinionArea}).inject(this.content);
    //
    //     this.setOpinion();
    //
    //     this.orgsArea = new Element("div", {"styles": this.css.orgsArea}).inject(this.content);
    //     this.orgsTitle = new Element("div", {
    //         "styles": this.css.orgsTitle,
    //         "text": "加签人"
    //     }).inject(this.orgsArea);
    //
    //     this.loadOrg();
    //
    //
    //     var _self = this;
    //     ["前加签","后加签"].each(function (text) {
    //         var routeNode = new Element("div", {
    //             "styles": this.css.routeNode,
    //             "text": text
    //         }).inject(this.content);
    //
    //         routeNode.addEvents({
    //             "mouseover": function (e) {
    //                 _self.overRoute(this);
    //             },
    //             "mouseout": function (e) {
    //                 _self.outRoute(this);
    //             },
    //             "click": function (e) {
    //                 _self.selectRoute(this);
    //             }
    //         });
    //     }.bind(this))
    //     this.fireEvent("load");
    // },
})

MWF.ProcessFlow.Processor = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
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
        this.load();
    },
    load: function(){
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
    loadOpinion: function(){
        var opt = Object.clone(this.flow.options.opinionOptions);
        opt.isHandwriting = this.options.isHandwriting;
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
            var aIdx = parseInt(this.splitByStartNumber(a).order || "9999999");
            var bIdx = parseInt(this.splitByStartNumber(b).order || "9999999");
            return aIdx - bIdx;
        }.bind(this));

        var routeGroupNames =  keys.map(function (k) { return this.splitByStartNumber(k).name; }.bind(this));

        var defaultValue;
        if( keys.length === 1 ){
            defaultValue = routeGroupNames[0];
        }else if( this.options.defaultRoute ){
            routeGroupNames.each(function (routeGroupName) {
                if( defaultValue )return;
                var routeList = this.routeGroupObject[routeGroupName];
                matchRoutes = routeList.filter(function(r){ return r.id === this.options.defaultRoute || r.name === this.options.defaultRoute; }.bind(this));
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
                    _self.loadOrgs(0);
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
                    "z-index": 600
                }
            });


            var array = [routeName, opinion, medias, appandTaskIdentityList, orgItems, function () {
                if (appendTaskOrgItem) appendTaskOrgItem.setData([]);
            }];

            this.fireEvent("submit", array);
        }.bind(this));
    },
    destroy: function () {
        Object.values( this.routeOrgMap ).each(function (orgList) {
            Object.values( orgList.orgMap ).each(function( org ){
                if(org.clearTooltip)org.clearTooltip();
            });
        });
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
            if (routeList[i].id === routeId) {
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
    getOffsetY: function (node) {
        return (node.getStyle("margin-top").toInt() || 0) +
            (node.getStyle("margin-bottom").toInt() || 0) +
            (node.getStyle("padding-top").toInt() || 0) +
            (node.getStyle("padding-bottom").toInt() || 0) +
            (node.getStyle("border-top-width").toInt() || 0) +
            (node.getStyle("border-bottom-width").toInt() || 0);
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
    loadOrgs: function () {
        var lastDom, configVisable = this.getVisableOrgConfig(), len = configVisable.length, lineNode;
        configVisable.each(function (config, i) {
            var dom, cfgId = config.id;
            if( i % 2 === 0 )lineNode = new Element("div.o2flow-org-line").inject( this.node );
            if( this.domMap[cfgId] ){
                dom = this.domMap[cfgId].show().inject( lineNode );
                this.orgVisableItems.push( this.orgMap[cfgId] );
            }else{
                //dom = new Element("div" ).inject( lastDom || this.node, lastDom ? "after" : "bottom" );
                dom = new Element("div" ).inject( lineNode );
                this.domMap[cfgId] = dom;
                this.loadOrg(dom, config );
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
    loadOrg: function (container, json, ignoreOldData) {
        var titleNode = new Element("div.o2flow-selector-title").inject(container);
        new Element("div.o2flow-selector-titletext", { "text": json.title }).inject(titleNode);
        var errorNode = new Element("div.o2flow-selector-errornode").inject(titleNode);

        var contentNode = new Element("div.o2flow-selector-content").inject(container);
        contentNode.setStyle( "height", MWF.ProcessFlow_ORG_HEIGHT + "px" );

        var org = new MWF.ProcessFlow.Processor.Org(contentNode, this.form, json, this);
        org.ignoreOldData = ignoreOldData;
        org.errContainer = errorNode;
        org.load();
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
        debugger;
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
        var flag = false;
        this.needCheckEmpowerOrg = [];
        this.orgVisableItems.each(function (item) {
            if (item.hasEmpowerIdentity()) {
                this.needCheckEmpowerOrg.push(item);
                flag = true;
            }
        }.bind(this));
        return flag;
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
        var currentRoute = this.selectedRouteNode ? this.selectedRouteNode.retrieve("route") : "";

        var visableOrg = this.getVisableOrgConfig( currentRoute || this.selectedRouteId || "" );
        var needOrgLength = visableOrg.length;

        var loadedOrgLength = 0;
        if ( this.orgVisableItems && this.orgVisableItems.length)loadedOrgLength = this.orgVisableItems.length;

        if( needOrgLength !== loadedOrgLength ){
            MWF.xDesktop.notice(
                "error",
                {"x": "center", "y": "center"},
                MWF.xApplication.process.Work.LP.loadedOrgCountUnexpected,
                this.node,
                {"x": 0, "y": 30},
                {"closeOnBoxClick": true, "closeOnBodyClick": true, "fixed": true, "delayClose": 6000}
            );
            return false;
        }

        if (!this.orgVisableItems || !this.orgVisableItems.length) {
            if (callback) callback();
            return true;
        }
        if (!this.validationOrgs()) return false;

        if (!this.isOrgsHasEmpower()) {
            if (callback) callback();
            return true;
        }
        this.showEmpowerDlg(callback);
    },
    showEmpowerDlg: function (callback) {

        var empowerNode = new Element("div.empowerNode", {"styles": this.css.empowerNode});
        var empowerTitleNode = new Element("div", {
            text: MWF.xApplication.process.Xform.LP.empowerDlgText,
            styles: this.css.empowerTitleNode
        }).inject(empowerNode);

        var orgs = this.needCheckEmpowerOrg;
        var len = orgs.length;
        var lines = ((len + 1) / 2).toInt();

        var empowerTable = new Element("table", {
            "cellspacing": 0, "cellpadding": 0, "border": 0, "width": "100%",
            "styles": this.css.empowerTable
        }).inject(empowerNode);

        for (var n = 0; n < lines; n++) {
            var tr = new Element("tr").inject(empowerTable);
            new Element("td", {"width": "50%", "styles": this.css.empowerOddTd}).inject(tr);
            new Element("td", {"width": "50%", "styles": this.css.empowerEvenTd}).inject(tr);
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

            var titleNode = new Element("div.empowerAreaTitle", {
                "styles": this.css.empowerAreaTitle
            }).inject(sNode);

            var titleTextNode = new Element("div.empowerAreaTitleText", {
                "text": org.json.title,
                "styles": this.css.empowerAreaTitleText
            }).inject(titleNode);

            var selectAllNode = new Element("div", {
                styles: {
                    float: "right"
                }
            }).inject(titleNode);

            var contentNode = new Element("div.empowerAreaContent", {
                "styles": this.css.empowerAreaContent
            }).inject(sNode);

            org.loadCheckEmpower(null, contentNode, selectAllNode);

        }.bind(this));

        empowerNode.setStyle("height", lines * MWF.ProcessFlow_ORG_HEIGHT + 20);
        var width = 840;
        empowerNode.setStyle("width", width + "px");

        this.node.getParent().mask({
            "style": this.css.mask
        });
        this.empowerDlg = o2.DL.open({
            "title": MWF.xApplication.process.Xform.LP.selectEmpower,
            "style": this.form.json.dialogStyle || "user",
            "isResize": false,
            "content": empowerNode,
            //"container" : this.node,
            "width": width + 40, //600,
            "height": "auto", //dlgHeight,
            "mark": false,
            "onPostLoad": function () {
                if (this.nodeWidth) {
                    this.node.setStyle("width", this.nodeWidth + "px");
                }
                if (this.nodeHeight) {
                    this.node.setStyle("height", this.nodeHeight + "px");
                }
            },
            "buttonList": [
                {
                    "type": "ok",
                    "text": MWF.LP.process.button.ok,
                    "action": function (d, e) {
                        //if (this.empowerDlg) this.empowerDlg.okButton.click();

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
    isSameArray: function (arr1, arr2) {
        if (arr1.length !== arr2.length) return false;
        for (var i = 0; i < arr1.length; i++) {
            if (arr1[i] !== arr2[i]) return false;
        }
        return true;
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
    load: function () {
        var options = this.getOptions();
        if (options) {
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
    selectOnComplete: function (items) { //移动端才执行
        var array = [];
        items.each(function (item) {
            array.push(item.data);
        }.bind(this));

        var simple = this.json.storeRange === "simple";

        this.checkEmpower(array, function (data) {
            var values = [];
            data.each(function (d) {
                values.push(MWF.org.parseOrgData(d, true, simple));
            }.bind(this));

            this.setData(values);

            //this.validationMode();
            //this.validation();

            this.container.empty();
            this.loadOrgWidget(values, this.container);

            this.selector = null;

            this.fireEvent("select", [items, values]);
        }.bind(this))
    },
    selectOnCancel: function () { //移动端才执行
        //this.validation();
    },
    selectOnLoad: function (selector) {
        //if (this.descriptionNode) this.descriptionNode.setStyle("display", "none");
        this.fireEvent("loadSelector", [selector])
    },
    selectOnClose: function () {
        var v = this._getBusinessData();
        //if (!v || !v.length) if (this.descriptionNode)  this.descriptionNode.setStyle("display", "block");
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
                        widget = new MWF.widget.O2Identity(copyData, node, {"style": "xform", "lazy": true});
                        break;
                    case "p":
                        widget = new MWF.widget.O2Person(copyData, node, {"style": "xform", "lazy": true});
                        break;
                    case "u":
                        widget = new MWF.widget.O2Unit(copyData, node, {"style": "xform", "lazy": true});
                        break;
                    case "g":
                        widget = new MWF.widget.O2Group(copyData, node, {"style": "xform", "lazy": true});
                        break;
                    default:
                        widget = new MWF.widget.O2Other(copyData, node, {"style": "xform", "lazy": true});
                }
                widget.field = this;
            }.bind(this));
        }
    },

    hasEmpowerIdentity: function () {
        var data = this.getData();
        if (!this.empowerChecker) this.empowerChecker = new MWF.ProcessFlow.Processor.EmpowerChecker(this.form, this.json, this.processor);
        return this.empowerChecker.hasEmpowerIdentity(data);
    },
    checkEmpower: function (data, callback, container, selectAllNode) {
        if (typeOf(data) === "array" && this.identityOptions && this.json.isCheckEmpower && this.json.identityResultType === "identity") {
            if (!this.empowerChecker) this.empowerChecker = new MWF.ProcessFlow.Processor.EmpowerChecker(this.form, this.json, this.processor);
            this.empowerChecker.selectAllNode = selectAllNode;
            this.empowerChecker.load(data, callback, container);
        } else {
            if (callback) callback(data);
        }
    },

    loadCheckEmpower: function (callback, container, selectAllNode) {
        this.checkEmpower(this.getData(), callback, container, selectAllNode)
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
            if (callback) callback(values)
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
        debugger;
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
    Extends: MWF.APPOrg.EmpowerChecker,
    initialize: function (form, json, processor) {
        this.form = form;
        this.json = json;
        this.processor = processor;
        this.css = this.processor.css;
        this.checkedAllItems = true;
    },
    load: function (data, callback, container) {
        if (typeOf(data) === "array" && this.json.isCheckEmpower && this.json.identityResultType === "identity") {
            var array = [];
            data.each(function (d) {
                if (d.distinguishedName) {
                    var flag = d.distinguishedName.substr(d.distinguishedName.length - 1, 1).toLowerCase();
                    if (flag === "i") {
                        array.push(d.distinguishedName)
                    }
                }
            }.bind(this));
            if (array.length > 0) {
                o2.Actions.get("x_organization_assemble_express").listEmpowerWithIdentity({
                    "application": (this.form.businessData.work || this.form.businessData.workCompleted).application,
                    "process": (this.form.businessData.work || this.form.businessData.workCompleted).process,
                    "work": (this.form.businessData.work || this.form.businessData.workCompleted).id,
                    "identityList": array
                }, function (json) {
                    var arr = [];
                    json.data.each(function (d) {
                        if (d.fromIdentity !== d.toIdentity) arr.push(d);
                    });
                    if (arr.length > 0) {
                        this.openSelectEmpower(arr, data, callback, container);
                    } else {
                        if (callback) callback(data);
                    }
                }.bind(this), function () {
                    if (callback) callback(data);
                }.bind(this))
            } else {
                if (callback) callback(data);
            }
        } else {
            if (callback) callback(data);
        }
    },
    hasEmpowerIdentity: function (data) {
        var flag = false;
        if (typeOf(data) === "array" && this.json.isCheckEmpower && this.json.identityResultType === "identity") {
            var array = [];
            data.each(function (d) {
                if (d.distinguishedName) {
                    var flag = d.distinguishedName.substr(d.distinguishedName.length - 1, 1).toLowerCase();
                    if (flag === "i") array.push(d.distinguishedName)
                }
            }.bind(this));
            if (array.length > 0) {
                o2.Actions.get("x_organization_assemble_express").listEmpowerWithIdentity({
                    "application": (this.form.businessData.work || this.form.businessData.workCompleted).application,
                    "process": (this.form.businessData.work || this.form.businessData.workCompleted).process,
                    "work": (this.form.businessData.work || this.form.businessData.workCompleted).id,
                    "identityList": array
                }, function (json) {
                    var arr = [];
                    json.data.each(function (d) {
                        if (d.fromIdentity !== d.toIdentity)
                            arr.push(d);
                    });
                    if (arr.length > 0) {
                        flag = true;
                    }
                }.bind(this), null, false)
            }
        }
        return flag;
    },
    openSelectEmpower: function (data, orgData, callback, container) {
        var node = new Element("div", {"styles": this.css.empowerAreaNode});
        //var html = "<div style=\"line-height: 30px; color: #333333; overflow: hidden\">"+MWF.xApplication.process.Xform.LP.empowerDlgText+"</div>";
        var html = "<div style=\"margin-bottom:10px; margin-top:10px; overflow-y:auto;\"></div>";
        node.set("html", html);
        var itemNode = node.getLast();
        this.getEmpowerItems(itemNode, data);
        node.inject(container || this.form.app.content);

        if (this.selectAllNode) {
            var selectNode = this.createSelectAllEmpowerNode();
            selectNode.inject(this.selectAllNode);
            if (this.checkedAllItems) {
                selectNode.store("isSelected", true);
                selectNode.setStyles(this.css.empowerSelectAllItemNode_selected);
            }
        }
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
    }
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
            "z-index" : "1001",
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
        var data = [
            {
                type: "process",
                text: "选择[送办理]，意见：请办理，处理人：张三、李四、张三、李四、张三、李四、张三、李四、张三、李四、张三、李四、张三、李四",
            },
            {
                type: "process",
                text: "选择[送核稿]，意见：请核稿，处理人：王五",
            },
            {
                type: "addTask",
                text:  "选择[前加签]，意见：请处理，加签人：赵六",
            },
            {
                type: "reset",
                text: "保留待办，意见：请处理，重置给：王五"
            }
        ];

        data.each( function (d) {
            var item = new Element("div.o2flow-quick-select-item", {
                events: {
                    mouseover: function () { this.addClass("mainColor_bg"); this.getFirst().addClass("mainColor_bg"); },
                    mouseout: function () { this.removeClass("mainColor_bg"); this.getFirst().removeClass("mainColor_bg"); }
                }
            }).inject( this.contentNode );
            var title = new Element("div.o2flow-quick-select-itemtitle", {
                text: this.flow.lp.flowActions[d.type]
            }).inject( item );
            title.addClass( "o2flow-"+d.type+"-color" );
            var content = new Element("div.o2flow-quick-select-itemtext", {
                text: "："+ d.text
            }).inject( item )
        }.bind(this))

        setTimeout( function () {
            if(callback)callback( data );
        }, 50);
    },
    filterOneKeyData: function(){
        var onekeyList = listData();
        onekeyList.filter(function (d) {
            var flag = (d.action === "process" && this.flow.processEnable) || (d.action === "reset" && this.flow.resetEnable) || (d.action === "addTask" && this.flow.addTaskEnable);
            if( !flag )return false;
            if( d.action === "process" && !this.flow.getRouteConfig(d.data.routeName) )return false;
            return true;
        }.bind(this));
        this.onekeyList = onekeyList;
    },
    listData: function () {
        return [
            {
                "process": "",
                "activity": "",
                "processName": "",
                "activityName": "",
                "person": "",
                "action": "",
                "data": {
                    "routeId": "",
                    "routeName": "",
                    "keepTask": true,
                    "idea": "",
                    "organizations": {}
                }
            }
        ];
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
                            this.opinionList = json.ideas;
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
        ev.target.addClass("mainColor_bg_opacity")
    },
    outItemNode: function (ev) {
        ev.target.removeClass("mainColor_bg_opacity")
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

        var h = this.handwritingSaveNode.getSize().y + this.flow.getOffsetY(this.handwritingSaveNode);
        this.handwritingAreaNode.setStyle("height", "" + ( y - h ) + "px");

        MWF.require("MWF.widget.Tablet", function () {
            var handWritingOptions = {
                "style": "default",
                "toolHidden": this.options.tabletToolHidden || [],
                "contentWidth": this.options.tabletWidth || 0,
                "contentHeight": this.options.tabletHeight || 0,
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

            this.tablet = new MWF.widget.Tablet(this.handwritingAreaNode, handWritingOptions, null);
            this.tablet.load();
        }.bind(this));
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
        el.getElement("i").removeClass("o2icon-icon_circle").addClass("o2icon-checkbox").addClass("o2flow-radio-icon");
        el.dataset["o2Checked"] = true;
        this.checkedItems.push(el);
        this.removeRequireStyle();
        this.fireEvent("check", [el, el.dataset["o2Value"]])
    },
    uncheck: function(el, isFire){
        el.removeClass("o2flow-radio-active");
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
    }
    // overItemNode: function (ev) {
    //     var el = this.flow.getEl(ev, "o2flow-radio");
    //     if( !el.dataset["o2Checked"] ){
    //         el.addClass("o2flow-radio-over")
    //     }
    // },
    // outItemNode: function (ev) {
    //     var el = this.flow.getEl(ev, "o2flow-radio");
    //     el.removeClass("o2flow-radio-over")
    // }
});

MWF.ProcessFlow.widget.Radio2 = new Class({
    Extends: MWF.ProcessFlow.widget.Radio,
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
        el.getElement("i").removeClass("o2icon-icon_circle").addClass("o2icon-radio-checked").addClass("o2flow-radio2-icon");
        el.dataset["o2Checked"] = true;
        this.checkedItems.push(el);
        this.removeRequireStyle();
        this.fireEvent("check", [el, el.dataset["o2Value"]])
    },
    uncheck: function(el, isFire){
        el.removeClass("o2flow-radio2-active");
        el.getElement("i").removeClass("o2icon-radio-checked").addClass("o2icon-icon_circle").removeClass("o2flow-radio2-icon");
        el.dataset["o2Checked"] = false;
        this.checkedItems.erase(el);
        if(isFire)this.fireEvent("uncheck", [el, el.dataset["o2Value"]])
    }
});

