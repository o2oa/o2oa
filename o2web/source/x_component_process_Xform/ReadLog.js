MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);

/** @class ReadLog 阅读记录组件。
 * @o2cn 阅读记录组件
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var log = this.form.get("name"); //获取组件
 * //方法2
 * var log = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.ReadLog = MWF.APPReadLog =  new Class(
    /** @lends MWF.xApplication.process.Xform.ReadLog# */
    {
        Extends: MWF.APP$Module,
        options: {
            /**
             * 加载数据后事件。
             * @event MWF.xApplication.process.Xform.ReadLog#postLoadData
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             * @example
             * //触发该事件的时候可以获取到流程数据ReadLog
             * var readLog = this.target.readLog;
             * //可以修改readLog达到定制化流程记录的效果
             * do something
             */

            /**
             * 加载每行流程信息以后触发，可以通过this.event获得下列信息：
             * <pre><code>
             *{
             * "data" : {}, //当前行记录信息，如果是已阅未单条记录（对象），如果是待阅有多条记录（数组）
             * "node" : node, //当前节点
             * "log" : object, //指向阅读记录
             * "type" : "readCompleted"  //"read"表示待阅，"readCompleted"表示已阅
             *}
             </code></pre>
             * @event MWF.xApplication.process.Xform.ReadLog#postLoadLine
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "moduleEvents": ["load", "queryLoad", "postLoad", "postLoadData", "postLoadLine"]
        },
        load: function(){
            this.node.empty();
            if (!this.json.isDelay){
                this.active();
            }
        },
        /**
         * 激活阅读记录。设置了延迟加载的时候，可以通过这个方法来激活
         * @example
         * this.form.get("fieldId").active();
         */
        active: function(){
            this._loadModuleEvents();
            if (this.fireEvent("queryLoad")){
                this._queryLoaded();
                this._loadUserInterface();
                this._loadStyles();
                this._loadDomEvents();
                //this._loadEvents();

                this._afterLoaded();
                this.fireEvent("postLoad");
                this.fireEvent("load");
            }
        },
        _loadUserInterface: function(){
            this.node.setStyle("-webkit-user-select", "text");
            this.node.setStyles(this.form.css.logActivityNode_record);

            this.form.app.action.getReadRecord(this.form.businessData.work.id, function(json){
                this.readLog = json.data;
                this.fireEvent("postLoadData");
                this.loadReadLog();
            }.bind(this));
        },
        loadReadLog: function(){
            switch (this.json.mode){
                case "notShow":
                    return "";
                case "script":
                    this.loadReadLogByScript();
                    break;
                default:
                    this.loadReadLogDefault();
            }
        },
        loadReadLogByScript: function(){
            if (this.json.displayScript && this.json.displayScript.code){
                var code = this.json.displayScript.code;
                this.readLog.each(function(log){
                    if (!this.json.isShowRead && log.type === "read"){
                        //不显示待阅
                    }else{
                        this.form.Macro.environment.log = log;
                        this.form.Macro.environment.list = null;
                        var r = this.form.Macro.exec(code, this);
                        var t = o2.typeOf(r);
                        var lineNode;
                        if (t==="string"){
                            lineNode = new Element("div", {
                                html: r
                            }).inject(this.node);
                            //this.node.appendHTML(r);
                        }else if (t==="element"){
                            lineNode = this.node.appendChild(r);
                        }
                        this.fireEvent("postLoadLine",[{
                            "data" : log,
                            "node" : lineNode,
                            "log" : this,
                            "type" : "readCompleted"
                        }]);
                    }

                }.bind(this));
            }
        },
        loadReadLogDefault: function(){
            var text = this.json.textStyle;
            var readPersons = [];
            var readLogs = [];
            this.lineClass = "logTaskNode";
            this.readLog.each(function(log, i){

                if (this.json.textStyleScript && this.json.textStyleScript.code){
                    this.form.Macro.environment.log = log;
                    this.form.Macro.environment.list = null;
                    text = this.form.Macro.exec(this.json.textStyleScript.code, this);
                }

                if (log.type == "readCompleted"){
                    var div = new Element("div", {styles: this.form.css[this.lineClass]}).inject(this.node);
                    var leftDiv = new Element("div", {styles: this.form.css.logTaskIconNode_record}).inject(div);
                    var rightDiv = new Element("div", {styles: this.form.css.logTaskTextNode}).inject(div);
                    var html = text.replace(/{person}/g, o2.name.cn(log.person || ""))
                        .replace(/{datetime}/g, log.completedTime)
                        .replace(/{date}/g, log.completedTime.substring(0.10))
                        .replace(/{startDatetime}/g, log.startTime)
                        .replace(/{startDate}/g, log.startTime.substring(0.10))
                        .replace(/{startDatetime}/g, log.startTime)
                        .replace(/{unit}/g, o2.name.cn(log.unit))
                        .replace(/{department}/g, o2.name.cn(log.unit))
                        .replace(/{identity}/g, o2.name.cn(log.identity))
                        .replace(/{activity}/g, o2.name.cn(log.activityName))
                        .replace(/{title}/g, log.title || "")
                        .replace(/{opinion}/g, log.opinion || "");
                    rightDiv.appendHTML(html);

                    if (this.lineClass === "logTaskNode"){
                        this.lineClass = "logTaskNode_even";
                    }else{
                        this.lineClass = "logTaskNode";
                    }

                    this.fireEvent("postLoadLine",[{
                        "data" : log,
                        "node" : div,
                        "log" : this,
                        "type" : "readCompleted"
                    }]);
                }
                if (!!this.json.isShowRead && log.type == "read"){
                    readPersons.push(o2.name.cn(log.person)+"("+o2.name.cn(log.unit)+")");
                    readLogs.push(log);
                }
            }.bind(this));
            if (readPersons.length){
                var div = new Element("div", {styles: this.form.css[this.lineClass]}).inject(this.node);
                var leftDiv = new Element("div", {styles: this.form.css.logTaskIconNode_record}).inject(div);
                var rightDiv = new Element("div", {styles: this.form.css.logTaskTextNode}).inject(div);
                leftDiv.setStyle("background-image", "url("+"../x_component_process_Xform/$Form/"+this.form.options.style+"/icon/rightRed.png)");
                rightDiv.appendHTML("<div><font style='font-weight: bold'>"+MWF.xApplication.process.Xform.LP.showReadTitle+": </font><font style='color: #00F'>"+readPersons.join(", ")+"</font></div>");
                this.fireEvent("postLoadLine",[{
                    "data" : readLogs,
                    "node" : div,
                    "log" : this,
                    "type" : "read"
                }]);
            }
        }
    });
