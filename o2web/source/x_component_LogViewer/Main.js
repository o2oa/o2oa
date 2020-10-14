MWF.xApplication.LogViewer.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "LogViewer",
        "icon": "icon.png",
        "width": "800",
        "height": "600",
        "title": MWF.xApplication.LogViewer.LP.title
    },
    onQueryLoad: function(){
        this.lp = MWF.xApplication.LogViewer.LP;
        this.tagId = o2.uuid();
    },
    onQueryClose: function () {
        $clear(this.timeDo);
    },
    loadApplication: function(callback){
        if (!this.options.isRefresh){
            this.maxSize(function(){
                this.doLog();
            }.bind(this));
        }else{
            this.doLog();
        }
        if (callback) callback();
    },
    doLog: function(){
        this.status = "systemLog";
        this.actions = MWF.Actions.get("x_program_center");

        this.node = new Element("div", {"styles": this.css.contentNode}).inject(this.content);

        this.toolbarNode = new Element("div", {"styles": this.css.toolbarNode}).inject(this.node);
        this.screenNode = new Element("div", {"styles": this.css.screenNode}).inject(this.node);
        this.bottomNode = new Element("div", {"styles": this.css.bottomNode}).inject(this.node);

        this.loadToolbar();
        this.loadScreen();

        this.loadBottom();

        this.initLog();
        this.loadLog();
    },
    initLog: function(appendFlag){
        if(!appendFlag){
            this.screenInforAreaNode.empty();
        }
        this.date = this.dateSelect.options[this.dateSelect.selectedIndex];
        this.method = "listPromptErrorLog";
        this.count = 20;
        this.currentId = "(0)";

        //定时器
    },
    loadBottom: function(){

        this.nodeSelect = new Element("select", {"styles": this.css.toolbarNodeSelect}).inject(this.bottomNode);
        o2.Actions.load("x_program_center").CommandAction.getNodeInfoList(
            function( json ){
                var nodeList = json.data.nodeList;
                if(nodeList.length>1){
                    new Element("option", {"value": "*", "text": "*"}).inject(this.nodeSelect);
                }
                nodeList.each(function (node) {
                    new Element("option", {
                        "value": node.node.nodeAgentPort,
                        "text": node.nodeAddress
                    }).inject(this.nodeSelect);
                }.bind(this));
            }.bind(this),null, false
        );

        this.commandNode = new Element("input",{"styles":this.css.commandNode}).inject(this.bottomNode);
        this.commandBtnNode = new Element("button",{"text":"submit","styles":this.css.commandBtnNode}).inject(this.bottomNode);


        this.commandNode.addEvent('keyup', function(e) {
            if(e.key==="enter"){
                this.executeCommand();
            }
        }.bind(this));

        this.commandBtnNode.addEvent("click",function () {
            this.executeCommand();
        }.bind(this));
    },
    executeCommand : function(){
        if(this.commandNode.get("value")!==""){
            var data = {};
            data["ctl"] = this.commandNode.get("value");
            data["nodeName"] = this.nodeSelect.getElement("option:selected").get("text");
            data["nodePort"] = this.nodeSelect.getElement("option:selected").get("value");
            o2.Actions.load("x_program_center").CommandAction.executeCommand(data, function( json ){
                this.commandNode.set("value","");
            }.bind(this),null, false);
        }
    },
    loadToolbar: function(){

        this.systemLogButton = this.createTopButton("SystemLog", "systemLog.png", "systemLog");

        this.promptErrorButton = this.createTopButton("PromptError", "prompt.png", "prompt");
        this.unexpectedErrorButton = this.createTopButton("UnexpectedError", "unexpected.png", "unexpected");
        this.warnErrorButton = this.createTopButton("Warn", "warn.png", "warn");

        this.dateSelect = new Element("select", {"styles": this.css.toolbarDateSelect}).inject(this.toolbarNode);
        new Element("option", {
            "value": "all",
            "selected": true,
            "text": this.lp.current
        }).inject(this.dateSelect);
        var d = new Date();
        var t = d.format("%Y-%m-%d");
        new Element("option", { "value": t, "text": t }).inject(this.dateSelect);

        for (var i=0; i<=7; i++){
            d = d.decrement("day", 1);
            t = d.format("%Y-%m-%d");
            new Element("option", { "value": t, "text": t }).inject(this.dateSelect);
        }

        this.dateSelect.addEvent("change", function(){
            this.initLog();
            this.loadLog();
        }.bind(this));
        // this.promptErrorButton.addEvent("click", function(){
        //     this.showTypeLog("prompt");
        // }.bind(this));
        // this.unexpectedErrorButton.addEvent("click", function(){
        //     this.showTypeLog("unexpected");
        // }.bind(this));
        // this.warnErrorButton.addEvent("click", function(){
        //     this.showTypeLog("warn");
        // }.bind(this));

        this.clearBtn = new Element("button",{"text":"clear","style":"margin:10px;float:right"}).inject(this.toolbarNode);
        this.clearBtn.addEvent("click",function () {
            this.screenInforAreaNode.empty();
            //this.tagId = o2.uuid();
        }.bind(this));
        this.stopBtn = new Element("button",{"text":"stop","style":"margin:10px;float:right"}).inject(this.toolbarNode);
        this.startBtn = new Element("button",{"text":"start","style":"margin:10px;display:none;;float:right"}).inject(this.toolbarNode);

        this.stopBtn.addEvent("click",function () {

            this.startBtn.show();
            this.stopBtn.hide();
            if(this.method==="listSystemLog"){
                $clear(this.timeDo);
                this.timeDo = null;
            }else {
                this.initLog();
                this.loadLog();
            }
        }.bind(this));

        this.startBtn.addEvent("click",function () {
            this.startBtn.hide();
            this.stopBtn.show();
            this.initLog(true);
            this.loadLog();
        }.bind(this));
    },
    showTypeLog: function(status){

        $clear(this.timeDo);
        this.timeDo = null;

        if (this.status!==status){
            switch (this.status){
                case "prompt":
                    this.promptErrorButton.setStyles(this.css.toolbarButton);
                    break;
                case "unexpected":
                    this.unexpectedErrorButton.setStyles(this.css.toolbarButton);
                    break;
                case "warn":
                    this.warnErrorButton.setStyles(this.css.toolbarButton);
                    break;
                case "systemLog":
                    this.systemLogButton.setStyles(this.css.toolbarButton);
                    break;
            }
            switch (status){
                case "prompt":
                    this.promptErrorButton.setStyles(this.css.toolbarButton_down);
                    this.status="prompt";
                    break;
                case "unexpected":
                    this.unexpectedErrorButton.setStyles(this.css.toolbarButton_down);
                    this.status="unexpected";
                    break;
                case "warn":
                    this.warnErrorButton.setStyles(this.css.toolbarButton_down);
                    this.status="warn";
                    break;
                case "systemLog":
                    this.systemLogButton.setStyles(this.css.toolbarButton_down);
                    this.status="systemLog";
                    break;
                default:
                    this.promptErrorButton.setStyles(this.css.toolbarButton_down);
                    this.status="prompt";
            }
            this.initLog();
            this.loadLog();
        }
    },

    createTopButton: function(text, img, status){
        var node = new Element("div", {"styles": this.css.toolbarButton}).inject(this.toolbarNode);
        var iconNode = new Element("div", {"styles": this.css.toolbarIconButton}).inject(node);
        var textNode = new Element("div", {"styles": this.css.toolbarTextButton, "text": text}).inject(node);
        iconNode.setStyle("background-image", "url("+"../x_component_LogViewer/$Main/default/"+img+")");
        if (status==this.status) node.setStyles(this.css.toolbarButton_down);

        var _self = this;
        node.addEvents({
            "mouseover": function(){if (_self.status != status) this.setStyles(_self.css.toolbarButton_over);},
            "mousedown": function(){if (_self.status != status) this.setStyles(_self.css.toolbarButton_down);},
            "mouseup": function(){if (_self.status != status) this.setStyles(_self.css.toolbarButton_over);},
            "mouseout": function(){if (_self.status != status) this.setStyles(_self.css.toolbarButton);},
            "click": function(){_self.showTypeLog(status);}.bind(this)
        });
        return node;
    },

    loadLog: function(){

        this.date = this.dateSelect.options[this.dateSelect.selectedIndex].value;

        switch (this.status){
            case "prompt":
                this.method = (this.date==="all") ? "listPromptErrorLog" : "listPromptErrorLogWithDate";
                break;
            case "unexpected":
                this.method = (this.date==="all") ? "listUnexpectedErrorLog" : "listUnexpectedErrorLogWithDate";
                break;
            case "warn":
                this.method = (this.date==="all") ? "listWarnLog" : "listWarnLogWithDate";
                break;
            case "systemLog":
                this.method = "listSystemLog";
                break;
            default:
                //this.method = (this.date==="all") ? "listPromptErrorLog" : "listPromptErrorLogWithDate";
                this.method = "listSystemLog";
        }
        if(this.method==="listSystemLog"){
            this.actions[this.method](this.tagId, function(json){
                this.showSystemLog(json.data);
            }.bind(this));

            //添加定时器
            this.timeDo = this.tiemShowSystemLog.periodical(1000,this);

        }else{
            if (this.date==="all"){
                this.actions[this.method](this.currentId, this.count, function(json){
                    this.showLog(json.data);
                }.bind(this));
            }else{
                var d = new Date().parse(this.date).format("%Y-%m-%d");
                this.actions[this.method](this.currentId, this.count, d, function(json){
                    this.showLog(json.data);
                }.bind(this));
            }
        }
    },
    showSystemLog : function(data){
        this.logFinish = true;
        data.each(function (log) {
            var node = new Element("div", {"styles": this.css.logItemNode}).inject(this.screenInforAreaNode);

            if(log.logLevel){
                var lineLog = log.lineLog;
                var logTime = log.logTime.split("#")[0];
                lineLog = lineLog.replace(logTime,"");
                lineLog = lineLog.replace(log.logLevel,"");
                var typeNode =   new Element("div",{
                    "html" : log.logLevel,
                    "title" : log.node,
                    "style" : "float:left;margin:0 10px;width:50px;font-weight:500;"
                }).inject(node);

                var color = "#FF0000";
                switch (log.logLevel) {

                    case "INFO" :
                        color = "#5a86ff";
                        break;
                    case  "PRINT" :
                        color = "#ffd192";
                        break;
                    case  "DEBUG" :
                        color = "#d7ff3d";
                        break;
                    default :

                }
                typeNode.setStyle("color",color);

                var timeNode = new Element("div",{
                    "html" : logTime,
                    "style" : "float:left;margin:0 10px;width:180px;color:#6BC5FC"
                }).inject(node);
            }
            var contentNode = new Element("div",{
                "text" : log.lineLog,
                "style" : "margin-left:270px"
            }).inject(node);
        }.bind(this));

    },
    tiemShowSystemLog : function(){
        this.actions[this.method](this.tagId, function(json){
            this.showSystemLog(json.data);
            this.screenInforAreaNode.scrollTop = this.screenInforAreaNode.scrollHeight;
        }.bind(this));
    },
    showLog: function(data){
        if (data.length){
            var last = data[data.length-1];
            this.currentId = last.id;

            data.each(function(log){
                new MWF.xApplication.LogViewer.Log(log, this);
            }.bind(this));

            // switch (this.status){
            //     case "prompt":
            //         this.showPromptLog(data);
            //         break;
            //     case "unexpected":
            //         this.showUnexpectedLog(data);
            //         break;
            //     case "warn":
            //         this.showWarnLog(data);
            //         break;
            //     default:
            //         this.showPromptLog(data);
            // }
        }else{
            this.logFinish = true;
        }
        this.checkLoadNext();
    },
    checkLoadNext: function(){
        if (!this.logFinish){
            var s = this.screenInforAreaNode.getScroll();
            var ssize = this.screenInforAreaNode.getScrollSize();
            var size = this.screenInforAreaNode.getSize();
            if (ssize.y-s.y-size.y<200){
                this.loadLog();
            }
        }
    },


    // showPromptLog: function(data){
    //     data.each(function(log){
    //         new MWF.xApplication.LogViewer.Log(log, this);
    //     }.bind(this));
    // },

    begin: function(){
        this.beginButton.setStyle("background-image", "url("+"../x_component_Console/$Main/default/play_gray.png)");
        this.stopButton.setStyle("background-image", "url("+"../x_component_Console/$Main/default/stop.png)");
        this.status = "begin";
    },
    stop: function(){
        this.beginButton.setStyle("background-image", "url("+"../x_component_Console/$Main/default/play.png)");
        this.stopButton.setStyle("background-image", "url("+"../x_component_Console/$Main/default/stop_gray.png)");
        this.status = "stop";
    },

    loadScreen: function(){
        this.screenInforAreaNode = new Element("div", {"styles": this.css.screenInforAreaNode}).inject(this.screenNode);
        this.screenInforAreaNode.addEvent("scroll", function(){
            this.checkLoadNext();
        }.bind(this));
        //this.screenInforAreaNode = new Element("div", {"styles": this.css.screenInforAreaNode}).inject(this.screenScrollNode);

        // MWF.require("MWF.widget.ScrollBar", function(){
        //     new MWF.widget.ScrollBar(this.screenScrollNode, {
        //         "style":"xApp_console", "where": "before", "indent": false, "distance": 50, "friction": 6,	"axis": {"x": false, "y": true},
        //         "onScroll": function(y, x){
        //             // var scrollSize = _self.listScrollAreaNode.getScrollSize();
        //             // var clientSize = _self.listScrollAreaNode.getSize();
        //             // var scrollHeight = scrollSize.y-clientSize.y;
        //             // if (y+200>scrollHeight) {
        //             //     if (!_self.isElementLoaded) _self.listItemNext();
        //             // }
        //         }
        //     });
        // }.bind(this));

        this.setScreenHeight();
        this.addEvent("resize", this.setScreenHeight.bind(this));
    },

    setScreenHeight: function(){
        var size = this.node.getSize();
        var toolbarSize = this.toolbarNode.getSize();
        var bottomSize = this.bottomNode.getSize();
        var y = size.y-toolbarSize.y-bottomSize.y;
        this.screenNode.setStyle("height", ""+y+"px");
    }

});

MWF.xApplication.LogViewer.Log = new Class({
    initialize: function(log, app){
        this.log = log;
        this.app = app;
        this.css = this.app.css;
        this.lp = this.app.lp;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.logItemNode}).inject(this.app.screenInforAreaNode);

        if(!this.log) return;
        var m = this.log.message.substr(0, this.log.message.indexOf("\n"));
        var message = m + ((this.log.person) ? "&nbsp;("+this.log.person+")": "");
        var html = "<pre><span  class='MWFLogCheckbox' style='cursor: pointer;float: left; height: 20px; width: 30px; background: url(../x_component_LogViewer/$Main/default/check.png) no-repeat center center'></span>" +
            "<span style='float: left;font-size: 14px; font-weight: bold; width: 160px; text-align: right'>"+this.log.occurTime+"</span>" +
            "<span style='float:left'>\t</span>" +
            "<span style='font-size: 14px; font-weight: bold;'>"+o2.common.encodeHtml(message)+"</span><br/>";


        if (this.log.exceptionClass){
            html += "<span style='float: left; width: 190px; text-align: right; color: #6BC5FC;'>ExceptionClass: </span>" +
                "<span style='float:left'>\t</span>" +
                "<span>"+o2.common.encodeHtml(this.log.exceptionClass)+"</span><br/>";
        }
        if (this.log.loggerName){
            html += "<span style='float: left; width: 190px; text-align: right; color: #6BC5FC;'>LoggerName: </span>" +
                "<span style='float:left'>\t</span>" +
                "<span>"+o2.common.encodeHtml(this.log.loggerName)+"</span><br/>";
        }

        if (this.log.stackTrace){
            var traces = this.log.stackTrace.split(/[\r\n\t]/g);
            html += "<span class='MWFLogStackTrace'><span style='float: left; width: 190px; text-align: right; color: #6BC5FC;'>StackTrace: </span>" +
                "<span style='float:left'>\t</span>";
            if (traces.length>1) {
                html += "<span  class='MWFLogStackTraceAction' style='float: left; cursor: pointer; height: 20px; width: 16px; background: url(../x_component_LogViewer/$Main/default/right.png) no-repeat left center'></span>";
            }
            html += "<span>"+o2.common.encodeHtml(traces[0])+"</span></span><br/>";

            // traces.each(function(trace, i){
            //     if (i!==0){
            //         html += "<span style='float: left; width: 190px; text-align: right; color: #6BC5FC;'>&nbsp;</span>" +
            //             "<span>&nbsp;&nbsp;</span>" +
            //             "<span>\t"+trace+"</span><br/>";
            //     }
            // }.bind(this));
        }

        if (this.log.requestUrl){
            var request = ((this.log.requestMethod) ? this.log.requestMethod+"&nbsp;": "")+
                this.log.requestUrl+
                ((this.log.requestRemoteAddr) ? "&nbsp; From &nbsp;"+this.log.requestRemoteAddr : "");
            html += "<span  class='MWFLogRequest'><span style='float: left; width: 190px; text-align: right; color: #6BC5FC;'>RequestInfor: </span>" +
                "<span style='float:left'>\t</span>";
            html += "<span  class='MWFLogRequestAction' style='float: left;cursor: pointer; height: 20px; width: 16px; background: url(../x_component_LogViewer/$Main/default/right.png) no-repeat left center'></span>";
            html += "<span>"+o2.common.encodeHtml(request)+"</span></span><br/>";
        }

        html += "</pre>";
        this.node.set("html", html);

        this.checkbox = this.node.getElement(".MWFLogCheckbox");
        this.traceNode = this.node.getElement(".MWFLogStackTrace");
        this.requestNode = this.node.getElement(".MWFLogRequest");
        this.traceActionNode = this.node.getElement(".MWFLogStackTraceAction");
        this.requestActionNode = this.node.getElement(".MWFLogRequestAction");


        this.setEvent();
    },
    setEvent: function(){
        this.node.addEvents({
            "mouseover": function(){ this.node.setStyles(this.css.logItemNode_over); }.bind(this),
            "mouseout": function(){ this.node.setStyles(this.css.logItemNode); }.bind(this)
        });

        if (this.checkbox){
            this.checkbox.addEvent("click", function(e){
                this.checkSelected();
                e.stopPropagation();
            }.bind(this))
        }
        if (this.traceActionNode){
            this.traceActionNode.addEvent("click", function(e){
                this.expandOrCollapseTrace();
                e.stopPropagation();
            }.bind(this))
        }
        if (this.requestActionNode){
            this.requestActionNode.addEvent("click", function(e){
                this.expandOrCollapseRequest();
                e.stopPropagation();
            }.bind(this))
        }

    },

    checkSelected: function(){
        var range = document.createRange();
        range.selectNode(this.node);
        var s = window.getSelection();
        s.selectAllChildren(this.node);
    },
    expandOrCollapseTrace: function(){
        if (this.log.stackTrace){
            if (!this.isTraceExpanded){
                this.expandedTrace();
            }else{
                this.collapseTrace();
            }
        }
    },
    expandedTrace: function(){
        if (!this.traceAllNode) this.createTraceAllNode();
        this.traceAllNode.setStyle("display", "inline");
        this.traceActionNode.setStyle("background-image", "url(../x_component_LogViewer/$Main/default/down.png)");
        this.isTraceExpanded = true;
    },
    collapseTrace: function(){
        if (this.traceAllNode){
            this.traceAllNode.destroy();
            this.traceAllNode = null;
        }
        this.traceActionNode.setStyle("background-image", "url(../x_component_LogViewer/$Main/default/right.png)");
        this.isTraceExpanded = false;
    },
    createTraceAllNode: function(){
        var brNode = this.traceNode.getNext();
        this.traceAllNode = new Element("span").inject(brNode, "after");
        var traces = this.log.stackTrace.split(/[\r\n\t]/g);
        var html = "";
        traces.each(function(t, i){
            if (i!==0){
                html += "<span style='float: left; width: 190px;'>\t</span>" +
                    "<span>\t</span>" +
                    "<span>\t"+t+"</span><br/>";
            }
        }.bind(this));
        this.traceAllNode.set("html", html);
    },

    expandOrCollapseRequest: function(){
        if (this.log.requestUrl){
            if (!this.isRequestExpanded){
                this.expandedRequest();
            }else{
                this.collapseRequest();
            }
        }
    },
    expandedRequest: function(){
        if (!this.requestAllNode) this.createRequestAllNode();
        this.requestAllNode.setStyle("display", "inline");
        this.requestActionNode.setStyle("background-image", "url(../x_component_LogViewer/$Main/default/down.png)");
        this.isRequestExpanded = true;
    },
    collapseRequest: function(){
        if (this.requestAllNode){
            this.requestAllNode.destroy();
            this.requestAllNode = null;
        }
        this.requestActionNode.setStyle("background-image", "url(../x_component_LogViewer/$Main/default/right.png)");
        this.isRequestExpanded = false;
    },
    createRequestAllNode: function(){
        var brNode = this.requestNode.getNext();
        this.requestAllNode = new Element("span").inject(brNode, "after");

        var html = "";
        html += "<span style='float: left; width: 190px;'>\t</span>" +
            "<span style='float: left;'>\t</span>" +
            "<span style='color: #6BC5FC; width: 108px;'>requestRemoteAddr: </span><span>"+(this.log.requestRemoteAddr || "&nbsp;")+"</span>&nbsp;&nbsp;" +
            "<span style='color: #6BC5FC; width: 108px;'>requestRemoteHost: </span><span>"+(this.log.requestRemoteHost || "&nbsp;")+"</span>&nbsp;&nbsp;" +
            "<span style='color: #6BC5FC; width: 108px;'>requestBodyLength: </span><span>"+(this.log.requestBodyLength || "&nbsp;")+"</span>" +
            "<br/>";

        // if (this.log.requestHead) html += "<span style='float: left; width: 190px;'>\t</span>" +
        //     "<span style='float: left;'>\t</span>" +
        //     "<span style='float: left; color: #6BC5FC; width: 108px;'>requestHead: </span><span style='word-break:break-all;'>"+this.log.requestHead+"</span><br/>";

        var headers = this.log.requestHead.split(/\n/g);
        headers.each(function(head, i){
            if (i===0){
                html += "<span style='float: left; width: 190px;'>\t</span>" +
                    "<span style='float: left;'>\t</span>" +
                    "<span style='float: left; color: #6BC5FC; width: 108px;'>requestHead: </span><span>"+head+"</span><br/>";
            }else{
                html += "<span style='float: left; width: 190px;'>\t</span>" +
                    "<span style='float: left;'>\t</span>" +
                    "<span style='float: left; color: #6BC5FC; width: 108px;'>\t</span><span>"+head+"</span><br/>";
            }
        }.bind(this));


        if (this.log.requestBody){
            var bodys = this.log.requestBody.split(/\n/g);
            bodys.each(function(body, i){
                if (i===0){
                    html += "<span style='float: left; width: 190px;'>\t</span>" +
                        "<span style='float: left;'>\t</span>" +
                        "<span style='float: left; color: #6BC5FC; width: 108px;'>requestBody: </span><span>"+body+"</span><br/>";
                }else{
                    html += "<span style='float: left; width: 190px;'>\t</span>" +
                        "<span style='float: left;'>\t</span>" +
                        "<span style='float: left; color: #6BC5FC; width: 108px;'>\t</span><span>"+body+"</span><br/>";
                }
            }.bind(this));
        }

        // requestHead
        // requestRemoteAddr
        // requestRemoteHost
        // requestBodyLength
        // requestBody
        this.requestAllNode.set("html", html);
    }
});
