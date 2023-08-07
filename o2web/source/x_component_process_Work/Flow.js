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
    destroy: function () {
        if( this.processor )this.processor.destroy();
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

