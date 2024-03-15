MWF.xDesktop.requireApp("process.Work", "Flow", null, false);
MWF.xApplication.process.Work.FlowMobile  = MWF.ProcessFlowMobile = new Class({
    Extends: MWF.ProcessFlow,
    Implements: [Options, Events],
    options: {
        mainColorEnable: true
    },
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
        this.goBackEnable = this.options.goBackEnable && this.businessData.control["allowGoBack"];

        this.navi = [];
        if( this.processEnable )this.navi.push({ key: "process", label: this.lp.flowActions.process });
        if( this.addTaskEnable )this.navi.push({ key: "addTask", label: this.lp.flowActions.addTask });
        if( this.resetEnable )this.navi.push({ key: "reset", label: this.lp.flowActions.reset });
        if( this.goBackEnable )this.navi.push({ key: "goBack", label: this.lp.flowActions.goBack });

        var url = this.path+this.options.style+"/main.html";
        this.container.loadHtml(url, {"bind": {"lp": this.lp, "navi": this.navi}, "module": this}, function(){
            if( this.navi.length < 2 )this.naviNode.hide();
            if( this.options.mainColorEnable ){
                this.okButton.addClass("mainColor_bg");
            }
            this.node.getParent().setStyle("height", "100%");
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
        this.processor = new MWF.ProcessFlow.ProcessorMobile(
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
        this.reset = new MWF.ProcessFlow.ResetMobile(
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
        this.addTask = new MWF.ProcessFlow.AddTaskMobile(
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
            this.resize();
            return;
        }
        this.goBack = new MWF.ProcessFlow.GoBackMobile(
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
        this.quickSelector = new MWF.ProcessFlow.widget.QuickSelectMobile(
            $(document.body), //this.form.app ? this.form.app.content :
            null, this.form.app, {}, {
                onPostLoad: function() {
                    this.maskNode.setStyles({
                        "z-index": 1001,
                        "opacity": 0.5,
                        "position": "absolute",
                        "background-color": "#ccc"
                    });
                }
            }
         );
        this.quickSelector.flow = this;
        this.quickSelectNode.addEvent("click", function () {
            this.quickSelector.load()
        }.bind(this))
    },
    cancel: function () {
        this.fireEvent("cancel");
    }
});

MWF.ProcessFlow.ResetMobile = new Class({
    Extends: MWF.ProcessFlow.Reset,
    setQuickData: function( data ){
        // if(this.keepOption)this.keepOption.setValue( data.keepTask ? "true" : null );
        if(this.opinion)this.opinion.setValue( data.opinion || "" );
        this.orgData = data.organizations ? (data.organizations.default || []) : [];
        this.orgContent.empty();
        this.loadOrgWidget(this.orgData, this.orgContent);
    },
    afterLoad: function () {
        if( this.flow.options.mainColorEnable ){
            this.orgIcon.addClass("mainColor_color");
            this.orgIcon.addClass("mainColor_border");
        }
        // this.keepOption = new MWF.ProcessFlow.widget.Radio2(this.keepOptionArea, this.flow, {
        //     activeIcon: "o2icon-checkbox",
        //     optionList: [{
        //         text: this.lp.keepTask,
        //         value: "true"
        //     }],
        //     value: (this.quickData.keepTask) ? "true" : null
        // });
        // this.keepOption.load();
    },
    destroy: function () {
        // if (this.orgItem && this.orgItem.clearTooltip){
        //     this.orgItem.clearTooltip();
        // }
        if (this.node) this.node.empty();
    },
    loadOrg: function(){
        var quickData = this.quickData.organizations ? (this.quickData.organizations.default || [] ) : [];
        if( quickData.length ){
            this.orgData = quickData;
            this.orgContent.empty();
            this.loadOrgWidget(this.orgData, this.orgContent);
        }
        if(this.quickData.organizations)delete this.quickData.organizations.default;

        this.orgNode.addEvent("click", function () {
            this.getSelOptions( function (options) {
                options.values = this.orgData || [] ;
                setTimeout(function () {
                    this.selector = new MWF.O2Selector($(document.body), options);
                }.bind(this), 100);
            }.bind(this) );
        }.bind(this));

    },
    getSelDefaultOptions: function(){
        var defaultOpt = {
            "type": "identity",
            "style": "default",
            "zIndex": 3000,
            "count": this.businessData.activity.resetCount || 0,
            "onComplete": function (items) {
                this.selectOnComplete(items);
            }.bind(this)
        };
        if (this.form.json.selectorStyle) {
            defaultOpt = Object.merge(Object.clone(this.form.json.selectorStyle), defaultOpt);
            if (this.form.json.selectorStyle.style) defaultOpt.style = this.form.json.selectorStyle.style;
        }
        return defaultOpt;
    },
    getSelOrgData: function () {
        return this.orgData ? this.orgData.map(function (item) {
            return typeOf( item ) === "string" ? item : item.distinguishedName;
        }) : [];
    },
    selectOnComplete: function (items) { //移动端才执行
        // var array = items.map(function (item) {
        //     return item.data;
        // });

        // this.checkEmpower(array, function (data) {
            var values = items.map(function (item) {
                return MWF.org.parseOrgData(item.data, true, true)
            });

            this.orgData = values;

            this.orgContent.empty();
            this.loadOrgWidget(values, this.orgContent);

            this.selector = null;

            this.fireEvent("select", [items, values]);
        // }.bind(this))
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
                var widget;
                switch (flag.toLowerCase()) {
                    case "i":
                        widget = new MWF.ProcessFlow.widget.O2IdentityMobile(data, node, this.getOrgWidgetOption());
                        break;
                    case "p":
                        widget = new MWF.ProcessFlow.widget.O2PersonMobile(data, node, this.getOrgWidgetOption());
                        break;
                    case "u":
                        widget = new MWF.ProcessFlow.widget.O2UnitMobile(data, node, this.getOrgWidgetOption());
                        break;
                    case "g":
                        widget = new MWF.ProcessFlow.widget.O2GroupMobile(data, node, this.getOrgWidgetOption());
                        break;
                    default:
                        widget = new MWF.ProcessFlow.widget.O2OtherMobile(data, node, this.getOrgWidgetOption());
                }
                widget.field = this;
                widget.load();
            }.bind(this));
        }
    },
    getOrgWidgetOption: function(){
        return {
            "mainColorEnable": this.flow.options.mainColorEnable,
            "removeByClick": true,
            "style": "flowmobile",
            "canRemove": false,
            "lazy": true,
            "disableInfor" : true,
            "onRemove" : this.removeWidgetItem,
            // "onPostLoad": this.loadWidgetItem,
            "delay": true
        };
    },
    // loadWidgetItem: function(){
    //     //this 是 MWF.widget.O2Identity 之类的对象
    //     var _self = this.field; //这个才是field
    //     var dn = this.data.distinguishedName;
    //     if( _self.flow.options.mainColorEnable ){
    //         this.node.addClass("mainColor_bg");
    //     }
    // },
    removeWidgetItem : function( widget, ev ){
        debugger;
        //this 是 MWF.widget.O2Identity 之类的对象
        var _self = this.field; //这个才是field
        var dn = this.data.distinguishedName;
        var data = _self.orgData;
        var index;
        _self.orgData.each( function ( d , i){
            if( d.distinguishedName == dn ){
                index = i;
            }
        });
        _self.orgData.splice( index, 1 );
        this.node.destroy();
        ev.stopPropagation();
    },
});

MWF.ProcessFlow.AddTaskMobile = new Class({
    Extends: MWF.ProcessFlow.AddTask,
    getUrl: function(){
        return this.flow.path+this.flow.options.style+"/addTask.html";
    },
    setQuickData: function( data ){
        if(this.position)this.position.setValue( data.routeId === "before" ? "true" : null ); //前后加签存在 routeId
        if(this.mode)this.mode.setValue( data.routeGroup || null ); //单人、并行、串行存在 routeGroup
        if(this.opinion)this.opinion.setValue( data.opinion || "" );
        this.orgData = data.organizations ? (data.organizations.default || []) : [];
        this.orgContent.empty();
        this.loadOrgWidget(this.orgData, this.orgContent);
    },
    afterLoad: function () {
        if( this.flow.options.mainColorEnable ){
            this.orgIcon.addClass("mainColor_color");
            this.orgIcon.addClass("mainColor_border");
        }
        this.mode = new MWF.ProcessFlow.widget.Radio2(this.modeArea, this.flow, {
            activeIcon: "o2icon-checkbox",
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
            value: this.quickData.mode || "single" //默认单人
        });
        this.mode.load();

        var position = "";
        if( this.quickData.routeId ){
            position = (this.quickData.routeId === "before") ? "true" : "false"
        }else{
            position = "false"; //默认为后加签
        }
        this.position = new MWF.ProcessFlow.widget.Radio2(this.positionArea, this.flow, {
            activeIcon: "o2icon-checkbox",
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
    destroy: function () {
        if (this.node) this.node.empty();
    },
    loadOrg: function(){
        var quickData = this.quickData.organizations ? (this.quickData.organizations.default || [] ) : [];
        if( quickData.length ){
            this.orgData = quickData;
            this.orgContent.empty();
            this.loadOrgWidget(this.orgData, this.orgContent);
        }
        if(this.quickData.organizations)delete this.quickData.organizations.default;

        this.orgNode.addEvent("click", function () {
            this.getSelOptions( function (options) {
                options.values = this.orgData || [] ;
                setTimeout(function () {
                    this.selector = new MWF.O2Selector($(document.body), options);
                }.bind(this), 100);
            }.bind(this) );
        }.bind(this));

    },
    getSelDefaultOptions: function(){
        var defaultOpt = {
            "type": "identity",
            "style": "default",
            "zIndex": 3000,
            "count": this.businessData.activity.resetCount || 0,
            "onComplete": function (items) {
                this.selectOnComplete(items);
            }.bind(this)
        };
        if (this.form.json.selectorStyle) {
            defaultOpt = Object.merge(Object.clone(this.form.json.selectorStyle), defaultOpt);
            if (this.form.json.selectorStyle.style) defaultOpt.style = this.form.json.selectorStyle.style;
        }
        return defaultOpt;
    },
    getSelOrgData: function () {
        return this.orgData ? this.orgData.map(function (item) {
            return typeOf( item ) === "string" ? item : item.distinguishedName;
        }) : [];
    },
    selectOnComplete: function (items) { //移动端才执行
        // var array = items.map(function (item) {
        //     return item.data;
        // });

        // this.checkEmpower(array, function (data) {
        var values = items.map(function (item) {
            return MWF.org.parseOrgData(item.data, true, true)
        });

        this.orgData = values;

        this.orgContent.empty();
        this.loadOrgWidget(values, this.orgContent);

        this.selector = null;

        this.fireEvent("select", [items, values]);
        // }.bind(this))
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
                var widget;
                switch (flag.toLowerCase()) {
                    case "i":
                        widget = new MWF.ProcessFlow.widget.O2IdentityMobile(data, node, this.getOrgWidgetOption());
                        break;
                    case "p":
                        widget = MWF.ProcessFlow.widget.O2PersonMobile(data, node, this.getOrgWidgetOption());
                        break;
                    case "u":
                        widget = new MWF.ProcessFlow.widget.O2UnitMobile(data, node, this.getOrgWidgetOption());
                        break;
                    case "g":
                        widget = new MWF.ProcessFlow.widget.O2GroupMobile(data, node, this.getOrgWidgetOption());
                        break;
                    default:
                        widget = new MWF.ProcessFlow.widget.O2OtherMobile(data, node, this.getOrgWidgetOption());
                }
                widget.field = this;
                widget.load();
            }.bind(this));
        }
    },
    getOrgWidgetOption: function(){
        return {
            "mainColorEnable": this.flow.options.mainColorEnable,
            "removeByClick": true,
            "style": "flowmobile",
            "canRemove": false,
            "lazy": true,
            "disableInfor" : true,
            "onRemove" : this.removeWidgetItem,
            // "onPostLoad": this.loadWidgetItem,
            "delay": true
        };
    },
    // loadWidgetItem: function(){
    //     //this 是 MWF.widget.O2Identity 之类的对象
    //     var _self = this.field; //这个才是field
    //     var dn = this.data.distinguishedName;
    //     if( _self.flow.options.mainColorEnable ){
    //         this.node.addClass("mainColor_bg");
    //     }
    // },
    removeWidgetItem : function( widget, ev ){
        debugger;
        //this 是 MWF.widget.O2Identity 之类的对象
        var _self = this.field; //这个才是field
        var dn = this.data.distinguishedName;
        var data = _self.orgData;
        var index;
        _self.orgData.each( function ( d , i){
            if( d.distinguishedName == dn ){
                index = i;
            }
        });
        _self.orgData.splice( index, 1 );
        this.node.destroy();
        ev.stopPropagation();
    },
});

MWF.ProcessFlow.GoBackMobile = new Class({
    Extends: MWF.ProcessFlow.GoBack,
    Implements: [Options, Events],
    options:{
        data: null,
        style: "default"
    },
    loadActivitys: function(){
        o2.Actions.load('x_processplatform_assemble_surface').WorkAction.V2ListActivityGoBack(this.task.work, function(json) {
            this.activitys = json.data;
            // this.activitys[0].lastIdentityList = this.activitys[0].lastIdentityList.concat(this.activitys[0].lastIdentityList);
            // this.activitys[0].lastIdentityList = this.activitys[0].lastIdentityList.concat(this.activitys[0].lastIdentityList);
            // this.activitys[0].lastIdentityList = this.activitys[0].lastIdentityList.concat(this.activitys[0].lastIdentityList);
            // this.activitys[0].lastIdentityList = this.activitys[0].lastIdentityList.concat(this.activitys[0].lastIdentityList);
            //
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
                        var el = this.activitysArea.getElement(".o2flow-section");
                        this.check( el );
                    }
                    this.afterLoad();
                    this.fireEvent("load");
                }.bind(this));
        }.bind(this));
    },
    loadWayRadio: function(ev, activityData){
        var wayRadio = new MWF.ProcessFlow.widget.Radio2(ev.target, this.flow, {
            activeIcon: "o2icon-checkbox",
            optionList: [{
                text: this.lp.goBackActivityWayStep,
                value: "step"
            },{
                text: this.lp.goBackActivityWayJump,
                value: "jump"
            }],
            value: "step", //默认为单人
            onLoad: function () {}
        });
        wayRadio.load();
        var parentNode = ev.target.getParent(".o2flow-section");
        parentNode.store("wayRadio", wayRadio);
        ev.target.hide();
    },
    loadIdentity: function(ev, activityData){
        var ids = activityData.lastIdentityList;
        if(ids.length>8){
            this.loadOrgWidget(ids.slice(0,8), ev.target);
            new Element("div.o2flow-identity-more", {
                text: "..."
            }).inject(ev.target);
        }else{
            this.loadOrgWidget(ids, ev.target);
        }
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
                var widget;
                switch (flag.toLowerCase()) {
                    case "i":
                        widget = new MWF.ProcessFlow.widget.O2IdentityMobile(data, node, this.getOrgWidgetOption());
                        break;
                    case "p":
                        widget = MWF.ProcessFlow.widget.O2PersonMobile(data, node, this.getOrgWidgetOption());
                        break;
                    case "u":
                        widget = new MWF.ProcessFlow.widget.O2UnitMobile(data, node, this.getOrgWidgetOption());
                        break;
                    case "g":
                        widget = new MWF.ProcessFlow.widget.O2GroupMobile(data, node, this.getOrgWidgetOption());
                        break;
                    default:
                        widget = new MWF.ProcessFlow.widget.O2OtherMobile(data, node, this.getOrgWidgetOption());
                }
                widget.field = this;
                widget.load();
            }.bind(this));
        }
    },
    getOrgWidgetOption: function(){
        return {
            "mainColorEnable": this.flow.options.mainColorEnable,
            "removeByClick": false,
            "style": "flowmobile",
            "canRemove": false,
            "lazy": true,
            "disableInfor" : true,
            // "onPostLoad": this.loadWidgetItem,
            "delay": true
        };
    },
    showWayRadio: function(el){
        var parentNode = this.flow.getEl({target: el}, "o2flow-section");
        var wayRadio = parentNode.retrieve("wayRadio");
        if(wayRadio)wayRadio.container.show();
    },
    hideWayRadio: function(el){
        var parentNode = this.flow.getEl({target: el}, "o2flow-section");
        var wayRadio = parentNode.retrieve("wayRadio");
        if(wayRadio)wayRadio.container.hide();
    },
    toggle: function( ev ){
        var el = this.flow.getEl(ev, "o2flow-section");
        if( this.checkedItems.contains( el ) ){
            //if( this.options.cancelEnable )this.uncheck( el, true )
        }else{
            this.check( el );
        }
    },
    check: function(el){
        while( this.checkedItems.length ){
            this.uncheck( this.checkedItems[0] );
        }
        el.addClass("o2flow-section-active");
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
        el.removeClass("o2flow-section-active");
        if( this.flow.options.mainColorEnable )el.removeClass("mainColor_color");
        el.getElement("i").removeClass("o2icon-radio-checked").addClass("o2icon-icon_circle").removeClass("o2flow-radio2-icon");
        if( this.flow.options.mainColorEnable )el.getElement("i").removeClass("mainColor_color");
        el.dataset["o2Checked"] = false;
        this.checkedItems.erase(el);
        this.hideWayRadio(el);
        if(isFire)this.fireEvent("uncheck", [el, el.dataset["o2Value"]])
    },
});

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
        this.opinion = new MWF.ProcessFlow.widget.OpinionMobile( this.opinionContent, this.flow, opt );
        this.opinion.load();
    },
    loadRouteOrGroupList: function () {
        this.getRouteGroupConfig();
        if (this.hasRouteGroup) {
            this.loadRouteGroupList();
        } else {
            this.routeGroupWraper.destroy();
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

        this.routeRadio = new MWF.ProcessFlow.widget.Radio2(this.routeArea, this.flow, {
            optionList: optionList,
            activeIcon: "o2icon-checkbox",
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
                orgList = new MWF.ProcessFlow.Processor.OrgListMobile(this, {
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
        var configVisable = this.getVisableOrgConfig(), len = configVisable.length, lineNode;
        configVisable.each(function (config, i) {
            var dom, cfgId = config.id, quickOrgData = this.getQuickOrgData( config );
            //if( i % 2 === 0 )lineNode = new Element("div.o2flow-org-line").inject( this.node );
            if( this.domMap[cfgId] ){
                dom = this.domMap[cfgId].show().inject( this.node );
                if( quickOrgData && this.orgMap[cfgId]){
                    var org = this.orgMap[cfgId];
                    org.container.empty();
                    org.setData( quickOrgData );
                    org.loadOrgWidget(quickOrgData, org.container);
                    //org.resetSelectorData();
                }
                this.orgVisableItems.push( this.orgMap[cfgId] );
            }else{
                dom = new Element("div.o2flow-org-node" ).inject( this.node );
                this.domMap[cfgId] = dom;
                this.loadOrg(dom, config, false, quickOrgData );
            }
            //lastDom = dom;
        }.bind(this));
    },
    loadOrg: function (container, json, ignoreOldData, quickOrgData) {
        var titleNode = new Element("div.o2flow-selector-title").inject(container);
        new Element("div.o2flow-selector-titletext", { "text": json.title }).inject(titleNode);
        var iconNode = new Element("div.o2flow-selector-icon").inject(titleNode);
        new Element("i.o2icon-choose_people").inject(iconNode);

        if( this.flow.options.mainColorEnable ){
            iconNode.addClass("mainColor_color");
            iconNode.addClass("mainColor_border");
        }

        var contentNode = new Element("div.o2flow-selector-content").inject(container);

        var errorNode = new Element("div.o2flow-selector-errornode").inject(container);

        var org = new MWF.ProcessFlow.Processor.OrgMobile(contentNode, this.form, json, this);
        org.ignoreOldData = ignoreOldData;
        org.errContainer = errorNode;
        this.orgVisableItems.push(org);
        this.orgMap[json.id] = org;

        container.addEvent("click", function () {
            org.load( quickOrgData );
        });
        var defaultValue = quickOrgData || org.getValue();
        org.loadOrgWidget(defaultValue, contentNode);
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
                        widget = new MWF.ProcessFlow.widget.O2IdentityMobile(copyData, node, this.getOrgWidgetOption());
                        break;
                    case "p":
                        widget = new MWF.ProcessFlow.widget.O2PersonMobile(copyData, node, this.getOrgWidgetOption());
                        break;
                    case "u":
                        widget = new MWF.ProcessFlow.widget.O2UnitMobile(copyData, node, this.getOrgWidgetOption());
                        break;
                    case "g":
                        widget = new MWF.ProcessFlow.widget.O2GroupMobile(copyData, node, this.getOrgWidgetOption());
                        break;
                    default:
                        widget = new MWF.ProcessFlow.widget.O2OtherMobile(copyData, node, this.getOrgWidgetOption());
                }
                widget.field = this;
                widget.load();
            }.bind(this));
        }
    },
    getOrgWidgetOption: function(){
        return {
            "mainColorEnable": this.processor.flow.options.mainColorEnable,
            "removeByClick": true,
            "style": "flowmobile",
            "canRemove": false,
            "lazy": true,
            "disableInfor" : true,
            "onRemove" : this.removeWidgetItem,
            //"onPostLoad": this.loadWidgetItem,
            "delay": true
        };
    },
    // loadWidgetItem: function(){
    //     //this 是 MWF.widget.O2Identity 之类的对象
    //     var _self = this.field; //这个才是field
    //     var dn = this.data.distinguishedName;
    //     if( _self.processor.flow.options.mainColorEnable ){
    //         this.node.addClass("mainColor_bg");
    //     }
    // },
    removeWidgetItem : function( widget, ev ){
        debugger;
        //this 是 MWF.widget.O2Identity 之类的对象
        var _self = this.field; //这个才是field
        var dn = this.data.distinguishedName;
        var data = _self._getBusinessData();
        var index;
        data.each( function ( d , i){
            if( d.distinguishedName == dn ){
                index = i
            }
        });
        data.splice( index, 1 );
        _self._setBusinessData( data );
        this.node.destroy();
        ev.stopPropagation();
    },
});

MWF.ProcessFlow.widget.QuickSelectMobile = new Class({
    Extends: MWF.ProcessFlow.widget.QuickSelect,
    options: {
        event : "click",
        hasArrow : false,
        hideByClickBody : true,
        offset : {
            x : 0,
            y : 5
        },
        nodeStyles: {
            "bottom": "0px",
           "left": "0px",
            "font-size" : "14px",
            "position" : "absolute",
            "max-width" : "100%",
            "min-width" : "100%",
            "height": "80%",
            "z-index" : "1001",
            "background-color" : "#fff",
            "padding" : "0px",
            "border-radius" : "15px 15px 0px 0px",
            // "box-shadow": "0px 0px 8px 0px rgba(0,0,0,0.25)",
            // "-webkit-user-select": "text",
            // "-moz-user-select": "text"
        }
    },
    _loadCustom : function( callback ){
        this.node.loadCss( this.flow.path + this.flow.options.style + "/style.css" );
        // var width = this.target.getSize().x ;
        // this.node.setStyles({
        //     "max-width": width+"px",
        //     "min-width": width+"px"
        // });

        new Element("div.o2flow-quick-title", {
            text: this.flow.lp.quickSelect
        }).inject(this.contentNode);
        var scrollNode = new Element("div.o2flow-quick-scroll").inject(this.contentNode);
        this.quickNode = new Element("div.o2flow-quick-node").inject(scrollNode);

        debugger;
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
                }).inject( this.quickNode );
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
            }).inject( this.quickNode );
            item.store( "data", d );
            var title = new Element("div.o2flow-quick-select-itemtitle", {
                text: this.flow.lp.flowActions[d.type]
            }).inject( item );
            title.addClass( "o2flow-"+d.type+"-color" );
            var content = new Element("div.o2flow-quick-select-itemtext", {
                text: "："+ d.text
            }).inject( item )
        }.bind(this))
    }
});

MWF.ProcessFlow.widget.OpinionMobile = new Class({
    Extends: MWF.ProcessFlow.widget.Opinion,
    Implements: [Options, Events],
    handwriting: function(){
        window.setTimeout(function () {
            this._handwriting();
        }.bind(this), 100)
    },
    _handwriting: function () {
        if( !this.tablet )this.createHandwriting();
        this.handwritingMask.show();
        this.handwritingNode.show();
        this.handwritingNode.setStyles({
            "top": "0px",
            "left": "0px"
        });
    },
    createHandwriting: function () {
        this.handwritingMask.inject( $(document.body) );
        this.handwritingNode.show().inject(this.handwritingMask, "after");
        //兼容以前的默认高宽
        var bodySize = $(document.body).getSize();
        var x = bodySize.x;
        var y = bodySize.y;
        this.options.tabletWidth = 0;
        this.options.tabletHeight = 0;

        var zidx = this.flow.node.getStyle("z-index");
        this.handwritingNode.setStyles({
            "height": "" + y + "px",
            "width": "" + x + "px",
            "z-index": zidx + 1
        });

        this.handwritingNode.addEvent('touchmove', function (e) {
            e.preventDefault();
        });
        this.handwritingNode.setStyles({
            "top": "0px",
            "left": "0px"
        });

        this.handwritingAreaNode.setStyle("height", "" + y + "px");

        MWF.require("MWF.widget.Tablet", function () {
            var handWritingOptions = this.getHandWritingOptions();

            handWritingOptions.tools = [
                    "undo",
                    "redo", "|",
                    "reset", "|",
                    "size",
                    "cancel"
                ];

            this.tablet = new MWF.widget.Tablet(this.handwritingAreaNode, handWritingOptions, null);
            this.tablet.load();
        }.bind(this));
    }
});

MWF.ProcessFlow.widget.O2IdentityMobile = new Class({
    Extends: o2.widget.O2Identity,
    setText: function(){
        var disply;
        if( this.data.displayName ){
            disply = this.data.displayName;
        }else{
            disply = this.data.name || o2.name.cn(this.data.distinguishedName)
            // var name = this.data.name || o2.name.cn(this.data.distinguishedName);
            // var unit;
            // if(this.data.unitName){
            //     unit = this.data.unitName;
            // }else if( this.data.unitLevelName ){
            //     var list = this.data.unitLevelName.split("/");
            //     unit = list[ list.length - 1 ];
            // }
            // disply = name + (unit ? "("+unit+")" : "")
        }
        this.textNode.set("text", disply );
    },
    load: function(){
        this.fireEvent("queryLoad");

        if (!this.options.lazy && !this.options.disableInfor) this.getPersonData();
        this.node = new Element("div.o2flow-identity").inject(this.container);
        this.iconNode = new Element("img.o2flow-identity-icon", {
            src: o2.filterUrl(o2.Actions.get("x_organization_assemble_control").getPersonIcon(this.data.distinguishedName))
        }).inject(this.node);
        this.textNode = new Element("div.o2flow-identity-text").inject(this.node);

        this.setText();

        if( this.options.removeByClick ){
            this.node.addEvent("click", function(e){
                this.fireEvent("remove", [this, e]);
                e.stopPropagation();
            }.bind(this));
        }

        if (this.options.canRemove){
            this.removeNode = new Element("div", {"styles": this.style.identityRemoveNode}).inject(this.node);
            this.removeNode.addEvent("click", function(e){
                this.fireEvent("remove", [this, e]);
                e.stopPropagation();
            }.bind(this));
        }

        this.setEvent();

        this.fireEvent("postLoad");
    },
});

MWF.ProcessFlow.widget.O2PersonMobile = new Class({
    Extends: MWF.ProcessFlow.widget.O2IdentityMobile
})

MWF.ProcessFlow.widget.O2UnitMobile = new Class({
    Extends: MWF.ProcessFlow.widget.O2IdentityMobile,
    load: function(){
        this.fireEvent("queryLoad");

        this.getPersonData( function () {
            this.node = new Element("div.o2flow-unit").inject(this.container);
            this.iconNode = new Element("div.o2flow-unit-icon", {
                text: this.data.name.substr(0,1)
            }).inject(this.node);
            if( this.options.mainColorEnable )this.iconNode.addClass("mainColor_bg");

            this.textNode = new Element("div.o2flow-unit-text").inject(this.node);
            this.nameNode = new Element("div.o2flow-unit-name").inject(this.textNode);
            this.levelNameNode = new Element("div.o2flow-unit-levelname").inject(this.textNode);

            this.setText();

            if( this.options.removeByClick ){
                this.node.addEvent("click", function(e){
                    this.fireEvent("remove", [this, e]);
                    e.stopPropagation();
                }.bind(this));
            }

            if (this.options.canRemove){
                this.removeNode = new Element("div", {"styles": this.style.identityRemoveNode}).inject(this.node);
                this.removeNode.addEvent("click", function(e){
                    this.fireEvent("remove", [this, e]);
                    e.stopPropagation();
                }.bind(this));
            }

            this.setEvent();

            this.fireEvent("postLoad");
        }.bind(this));

    },
    getPersonData: function( callback ){
        if (!this.data.distinguishedName || !this.data.levelName){
            this.action.actions = {"getUnit": {"uri": "/jaxrs/unit/{id}"}};
            this.action.invoke({"name": "getUnit", "async": false, "parameter": {"id": (this.data.id || this.data.distinguishedName || this.data.name)}, "success": function(json){
                this.data = json.data;
                if(callback)callback();
            }.bind(this)});
        }else{
            if(callback)callback();
        }
    },
    setText: function(){
        var disply;
        if( this.data.displayName ){
            disply = this.data.displayName;
        }else{
            disply = this.data.name || o2.name.cn(this.data.distinguishedName)
        }
        this.nameNode.set("text", disply );
        this.levelNameNode.set("text", this.data.levelName);
    },
})


MWF.ProcessFlow.widget.O2GroupMobile = new Class({
    Extends: MWF.ProcessFlow.widget.O2UnitMobile,
    load: function(){
        this.fireEvent("queryLoad");

        this.getPersonData( function () {
            this.node = new Element("div.o2flow-unit").inject(this.container);
            this.iconNode = new Element("div.o2flow-unit-icon", {
                text: this.data.name.substr(0,1)
            }).inject(this.node);
            if( this.options.mainColorEnable )this.iconNode.addClass("mainColor_bg");

            this.textNode = new Element("div.o2flow-group-text").inject(this.node);
            this.nameNode = new Element("div.o2flow-unit-name").inject(this.textNode);
            //this.levelNameNode = new Element("div.o2flow-unit-levelname").inject(this.textNode);

            this.setText();

            if( this.options.removeByClick ){
                this.node.addEvent("click", function(e){
                    this.fireEvent("remove", [this, e]);
                    e.stopPropagation();
                }.bind(this));
            }

            if (this.options.canRemove){
                this.removeNode = new Element("div", {"styles": this.style.identityRemoveNode}).inject(this.node);
                this.removeNode.addEvent("click", function(e){
                    this.fireEvent("remove", [this, e]);
                    e.stopPropagation();
                }.bind(this));
            }

            this.setEvent();

            this.fireEvent("postLoad");
        }.bind(this));

    },
    getPersonData: function(callback){
        if (!this.data.distinguishedName){
            this.action.actions = {"getGroup": {"uri": "/jaxrs/group/{id}"}};
            this.action.invoke({"name": "getGroup", "async": false, "parameter": {"id": (this.data.id || this.data.name)}, "success": function(json){
                    this.data = json.data;
                    if(callback)callback();
                }.bind(this)});
        }else{
            if(callback)callback();
        }
    },
    setText: function(){
        var disply;
        if( this.data.displayName ){
            disply = this.data.displayName;
        }else{
            disply = this.data.name || o2.name.cn(this.data.distinguishedName)
        }
        this.nameNode.set("text", disply );
        //this.levelNameNode.set("text", this.data.levelName);
    },
})

MWF.ProcessFlow.widget.O2OtherMobile = new Class({
    Extends: MWF.ProcessFlow.widget.O2GroupMobile,
    getPersonData: function(){
        return this.data;
    }
});