MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Work = MWF.xApplication.process.Work || {};
MWF.xDesktop.requireApp("process.Work", "lp." + MWF.language, null, false);

MWF.xApplication.process.Work.Handler = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        style: "default",
        processOptions: {},
    },
    initialize: function (container, task, options, form) {
        this.setOptions(options);

        this.path = "../x_component_process_Work/$Handler/";
        this.cssPath = "../x_component_process_Work/$Handler/" + this.options.style + "/css.wcss";
        this._loadCss();

        this.task = task;
        this.container = $(container);
        this.selectedRoute = null;

        this.form = form;
        this.businessData = this.form.businessData;

        this.load();
    },
    load: function () {
        this.node = new Element("div", {
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
        this.contentNode = new Element("div", {
            styles: {
                "margin-left": "100px",
                "width": "1000px"
            }
        }).inject(this.node);

        if( this.businessData.control["allowProcessing"] ){
            this.processorTitleNode = new Element("div", {
                text: "继续流转"
            }).inject( this.naviNode );
            this.processorContentNode = new Element("div").inject( this.contentNode );
            this.loadProcessor();
        }
        if( this.businessData.control["allowAddTask"] ){
            this.addTaskTitleNode = new Element("div", {
                text: "加签"
            }).inject( this.naviNode );
        }
        if( this.businessData.control["allowReset"] ){
            this.resetTitleNode = new Element("div", {
                text: "重置处理人"
            }).inject( this.naviNode );
        }
    },
    loadProcessor: function () {
        this.processor = new MWF.xApplication.process.Work.Handler.Processor(this.processorContentNode, this.task, this.options.processOptions, this.form);
    },
    destroy: function () {
        if( this.processor )this.processor.destroy();
    }
});

MWF.xApplication.process.Work.Handler.Processor = new Class({
    Extends: MWF.xApplication.process.Work.Processor
});

