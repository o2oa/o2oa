MWF.xDesktop.requireApp("process.Work", "Flow", null, false);
MWF.xApplication.process.Work.FlowMobile  = MWF.ProcessFlowMobile = new Class({
    Extends: MWF.ProcessFlow,
    Implements: [Options, Events],
    initialize: function (container, task, options, form) {
        this.setOptions(options);

        this.path = "../x_component_process_Work/$FlowMobile/";

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
            this.changeAction( this.navi[0].key );
            this.loadQuickSelect();
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
        if( this.currentAction ){
            this[ this.currentAction+"ContentNode" ].hide();
            this[ this.currentAction+"TitleNode" ].removeClass("o2flow-navi-item-active");
            if( this.options.mainColorEnable )this[ this.currentAction+"TitleNode" ].removeClass("mainColor_color").removeClass("mainColor_border");
        }

        this[ action+"ContentNode" ].show();
        this[ action+"TitleNode" ].addClass("o2flow-navi-item-active");
        if( this.options.mainColorEnable )this[ action+"TitleNode" ].addClass("mainColor_color").addClass("mainColor_border");

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
    loadQuickSelect: function(){
        this.quickSelector = new MWF.ProcessFlow.widget.QuickSelect(
            this.form.app ? this.form.app.content : $(document.body),
            this.quickSelectNode, this.form.app, {}
         );
        this.quickSelector.flow = this;
        this.contentScrollNode.addEvent("scroll", function () {
            if(this.quickSelector.status === "display")this.quickSelector.hide();
        }.bind(this))
    }
});

MWF.ProcessFlow.ResetMobile = new Class({
    Extends: MWF.ProcessFlow.Reset,
    setQuickData: function( data ){
        if(this.keepOption)this.keepOption.setValue( data.keepTask ? "true" : null );
        if(this.opinion)this.opinion.setValue( data.opinion || "" );
        if(this.selector && this.selector.selector)this.selector.selector.setValues( data.organizations ? (data.organizations.default || []) : [] );
    },
    loadOrg: function(){
        this.getSelOptions( function (options) {
            options.values =  this.quickData.organizations ? (this.quickData.organizations.default || []) : [];
            if(this.quickData.organizations)delete this.quickData.organizations.default;
            this.selector = new MWF.O2Selector(this.orgsArea, options);
        }.bind(this) );
    },
    getSelDefaultOptions: function(){
        var defaultOpt = {
            "type": "identity",
            "style": "default",
            "zIndex": 3000,
            "count": this.businessData.activity.resetCount || 0
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
    destroy: function () {
        if (this.orgItem && this.orgItem.clearTooltip){
            this.orgItem.clearTooltip();
        }
        if (this.node) this.node.empty();
    },
});

MWF.ProcessFlow.AddTaskMobile = new Class({
    Extends: MWF.ProcessFlow.ResetMobile,
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
})

MWF.ProcessFlow.ProcessorMobile = new Class({
    Extends: MWF.ProcessFlow.Processor,
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
            var aIdx = parseInt(this.splitByStartNumber(a).order || "9999999");
            var bIdx = parseInt(this.splitByStartNumber(b).order || "9999999");
            return aIdx - bIdx;
        }.bind(this));

        var routeGroupNames =  keys.map(function (k) { return this.splitByStartNumber(k).name; }.bind(this));

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
    }
});

MWF.ProcessFlow.Processor.OrgListMobile = new Class({
    Extends: MWF.ProcessFlow.Processor.OrgList,
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
    getSelectedData: function (filedName) {
        var data = [];
        for (var i = 0; i < this.orgVisableItems.length; i++) {
            var org = this.orgVisableItems[i];
            if (org.json.name === filedName) {
                data.push( org.getData() );
            }
        }
        return data;
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

        if (callback) callback();
    }
});

MWF.ProcessFlow.Processor.OrgMobile = new Class({
    Extends: MWF.ProcessFlow.Processor.Org,
    load: function ( quickData ) {
        var options = this.getOptions();
        if (options) {
            if( quickData )options.values = quickData;
            setTimeout(function () {
                this.selector = new MWF.O2Selector($(document.body), options);
            }.bind(this), 100);
        }
    },
    getDefaultOptions: function(){
        return {
            "style": "default",
            "zIndex": 3000
        };
    },
    getOptionEvents: function(){
        return {
            "onComplete": function (items) {
                this.selectOnComplete(items);
            }.bind(this),
            "onCancel": this.selectOnCancel.bind(this),
            "onClose": this.selectOnClose.bind(this)
        };
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

            this.container.empty();
            this.loadOrgWidget(values, this.container);

            this.selector = null;

            this.fireEvent("select", [items, values]);
        }.bind(this))
    },
    selectOnCancel: function () { //移动端才执行
        //this.validation();
    },
    selectOnClose: function () {
        var v = this._getBusinessData();
        //if (!v || !v.length) if (this.descriptionNode)  this.descriptionNode.setStyle("display", "block");
    },
    getData: function () {
        return this.getValue();
    },
    getSelectedData: function () {
        return this.getValue();
    },
    getValue: function () {
        var value = this._getBusinessData();
        if (!value) value = this._computeValue();
        return value || "";
    }
});

