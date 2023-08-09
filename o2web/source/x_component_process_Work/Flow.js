MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Work = MWF.xApplication.process.Work || {};
MWF.xDesktop.requireApp("process.Work", "lp." + MWF.language, null, false);

MWF.xApplication.process.Work.Flow = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        style: "default",
        processOptions: {},
    },
    initialize: function (container, task, options, form) {
        this.setOptions(options);

        this.path = "../x_component_process_Work/$Flow/";
        this.cssPath = "../x_component_process_Work/$Flow/" + this.options.style + "/css.wcss";
        this._loadCss();

        this.task = task;
        this.container = $(container);
        this.selectedRoute = null;

        this.form = form;
        this.businessData = this.form.businessData;

        this.load();
    },
    load: function () {
        this.node = new Element("div.node", {
            styles: {
                overflow: "hidden"
            }
        }).inject(this.container);

        this.naviNode = new Element("div", {
            styles: {
                "float": "left",
                "width": "100px"
            }
        }).inject(this.node);
        this.contentNode = new Element("div.contentNode", {
            styles: {
                "margin-left": "100px",
                "width": "900px"
            }
        }).inject(this.node);

        if( this.businessData.control["allowProcessing"] ){
            this.processorTitleNode = new Element("div", {
                text: "提交"
            }).inject( this.naviNode );
            this.processorContentNode = new Element("div").inject( this.contentNode );
            this.loadProcessor();
        }
        if( this.businessData.control["allowAddTask"] ){
            this.addTaskTitleNode = new Element("div", {
                text: "加签"
            }).inject( this.naviNode );
            this.addTaskContentNode = new Element("div").inject( this.contentNode );
        }
        if( this.businessData.control["allowReset"] ){
            this.resetTitleNode = new Element("div", {
                text: "重置"
            }).inject( this.naviNode );
            this.resetContentNode = new Element("div").inject( this.contentNode );
            this.loadReset();
        }
    },
    loadProcessor: function () {
        var processOptions = this.options.processOptions;
        processOptions.onResize = function () {
            var size = this.processorContentNode.getSize();
            var naviSize = this.naviNode.getSize();
            this.container.setStyles({
                "height": size.y,
                "width": size.x + naviSize.x + this.getOffsetX( this.naviNode )
            });
            debugger;
            this.fireEvent("resize");
        }.bind(this);
        processOptions.inFlow = true;
        this.processor = new MWF.xApplication.process.Work.Flow.Processor(
            this.processorContentNode,
            this.task,
            processOptions,
            this.form
        );
    },
    loadReset: function(){
        this.reset = new MWF.xApplication.process.Work.Flow.Reset(
            this.resetContentNode,
            this.task,
            {},
            this.form
        );
    },
    destroy: function () {
        if( this.processor )this.processor.destroy();
        if( this.reset )this.reset.destroy();
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
    }
});

MWF.xApplication.process.Work.Flow.Processor = new Class({
    Extends: MWF.xApplication.process.Work.Processor
});

MWF.xApplication.process.Work.Flow.Reset = new Class({
    Implements: [Options, Events],
    options:{},
    initialize: function (container, task, options, form) {
        this.setOptions(options);

        this.task = task;
        this.container = $(container);

        this.form = form;
        this.businessData = this.form.businessData;

        this.load();
    },
    load: function(){

    },
    getSelectorOptions: function( callback ){
        o2.Actions.get("x_processplatform_assemble_surface").listTaskByWork(this.businessData.work.id, function(json){
            var identityList = [];
            json.data.each(function(task){
                identityList.push(task.identity);
            });
            this._getSelectorOptions(identityList, callback);
        }.bind(this))
    },
    _getSelectorOptions: function (exclude, callback) {
        var options = this.getDefaultOptions();

        var range = this.businessData.activity.resetRange || "department";
        var selectorOptions;
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
                }.bind(this))
                break;
            default:
                callback( options );
        }
    },
    getDefaultOptions: function(){
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
                "style": "process",
                "width": "auto",
                "height": "240",
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


    doResetWork: function () {
        var names = this.identityList || [];
        if (!names.length) {
            this.app.notice(MWF.xApplication.process.Xform.LP.inputResetPeople, "error", this.node);
            return false;
        }
        var opinion = $("resetWork_opinion").get("value");
        var checkbox = this.content.getElement(".resetWork_keepOption");
        var keep = (checkbox.checked);

        var nameText = [];
        names.each(function (n) { nameText.push(MWF.name.cn(n)); });
        if (!opinion) {
            opinion = MWF.xApplication.process.Xform.LP.resetTo + ": " + nameText.join(", ");
        }

        MWF.require("MWF.widget.Mask", function () {
            this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
            this.mask.loadNode(this.app.content);

            this.fireEvent("beforeReset");
            if (this.app && this.app.fireEvent) this.app.fireEvent("beforeReset");

            this.resetWorkToPeson(names, opinion, keep, function (workJson) {
                //this.workAction.loadWork(function (workJson) {
                this.fireEvent("afterReset");
                if (this.app && this.app.fireEvent) this.app.fireEvent("afterReset");
                this.addResetMessage(workJson.data);
                //this.app.notice(MWF.xApplication.process.Xform.LP.resetOk + ": " + MWF.name.cns(names).join(", "), "success");
                if (!this.app.inBrowser) this.app.close();
                //}.bind(this), null, this.businessData.work.id);
                dlg.close();
                if (this.mask) { this.mask.hide(); this.mask = null; }
            }.bind(this), function (xhr, text, error) {
                var errorText = error + ":" + text;
                if (xhr) errorText = xhr.responseText;
                this.app.notice("request json error: " + errorText, "error", dlg.node);
                if (this.mask) { this.mask.hide(); this.mask = null; }
            }.bind(this));
        }.bind(this));
    },
    resetWorkToPeson: function (identityList, opinion, keep, success, failure) {
        var data = {
            "opinion": opinion,
            "routeName": MWF.xApplication.process.Xform.LP.reset,
            "identityList": identityList,
            "keep": !!keep
        };
        this.saveFormData(
            function (json) {
                o2.Actions.load("x_processplatform_assemble_surface").TaskAction.V2Reset(
                    //this.workAction.resetWork(
                    function (json) {
                        if (success) success(json);
                    }.bind(this),
                    function (xhr, text, error) {
                        if (failure) failure(xhr, text, error);
                    },
                    this.businessData.task.id, data
                );
            }.bind(this),
            function (xhr, text, error) {
                if (failure) failure(xhr, text, error);
            }, true, null, true
        );
    },
    destroy: function () {
        if (this.orgItem && this.orgItem.clearTooltip){
            this.orgItem.clearTooltip();
        }
        if (this.node) this.node.empty();
        // delete this.task;
        // delete this.node;
    },
})

