MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);

/** @class Log 流程记录组件。
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
MWF.xApplication.process.Xform.Log = MWF.APPLog =  new Class(
    /** @lends MWF.xApplication.process.Xform.Log# */
    {
	Extends: MWF.APP$Module,
    options: {
        /**
         * 加载数据后事件。
         * @event MWF.xApplication.process.Xform.Log#postLoadData
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         * @example
         * //触发该事件的时候可以获取到流程数据workLog
         * var workLog = this.target.workLog;
         * //可以修改workLog达到定制化流程记录的效果
         * do something
         */
        /**
         * 加载每行流程信息以后触发，可以通过this.event获得下列信息：
         * <pre><code>
         {
            "data" : {}, //当前行流程信息
            "node" : logTaskNode, //当前节点
            "log" : object, //指向流程记录
            "type" : "task"  //"task"表示待办，"taskCompleted"表示已办
        }
         </code></pre>
         * @event MWF.xApplication.process.Xform.Log#postLoadLine
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        "moduleEvents": ["load", "queryLoad", "postLoad", "postLoadData", "postLoadLine"]
    },

	_loadUserInterface: function(){
		this.node.empty();
        this.node.setStyle("-webkit-user-select", "text");
        if (this.form.businessData){
            if (this.json.logType!=="record"){
                if (this.form.businessData.workLogList){
                    this.workLog = Array.clone(this.form.businessData.workLogList);
                    this.fireEvent("postLoadData");
                    this.loadWorkLog();
                }
            }else{
                if (this.form.businessData.recordList){
                    this.workLog = Array.clone(this.form.businessData.recordList);
                    this.fireEvent("postLoadData");
                    this.loadRecordLog();
                }
            }

        }
	},
    loadRecordLog: function(){
        if( !this.json.category || this.json.category === "none" ){
            if (this.json.mode==="table"){
                this.loadRecordLogTable();
            }else if (this.json.mode==="text"){
                this.loadRecordLogText();
            }else if (this.json.mode==="media"){
                this.loadRecordLogMedia();
            }else{
                this.loadRecordLogDefault();
            }
        }else{
            this.loadCategoryList("_loadRecordCategoryList", "_loadRecordCategoryList");
            if( !this.categoryList.length )return;
            this.expandCount = 0;
            if( this.json.expand && this.json.expand === "enable" ){
                this.expandCount = parseInt( this.json.expandCount );
            }
            this.table = new Element("table", this.json.tableProperties).inject( this.node );
            this.categoryList.each( function( key, idx ){
                var list = this.categoryJson[key];
                if( list && list.length ){

                    var tr = new Element("tr").inject( this.table );
                    if( this.expandCount && (idx + 1) > this.expandCount ){
                        tr.setStyle("display","none");
                    }
                    var text = key;
                    if( this.json.category === "unit" ){
                        text = key.split("@")[0];
                    }
                    new Element("td", {
                        styles : this.json.titleTdStyles,
                        text : text
                    }).inject( tr );

                    var td = new Element("td", {
                        styles : this.json.contentTdStyles
                    }).inject( tr );

                    var div = new Element("div",{
                        styles : this.json.contentDivStyles || {}
                    }).inject( td );


                    if( this.json.sortTypeInCategory === "completedTimeAsc" || this.json.sortTypeInCategory === "completedTimeDesc" ){
                        list.sort(function(a, b){
                            if( this.json.sortTypeInCategory === "completedTimeAsc" ) {
                                return Date.parse(a.recordTime) - Date.parse(b.recordTime);
                            }else if( this.json.sortTypeInCategory === "completedTimeDesc" ) {
                                return Date.parse(b.recordTime) - Date.parse(a.recordTime);
                            }
                        }.bind(this));


                        // sortedList = [];
                        // list.each( function( log ){
                        //     if (log.taskCompletedList.length) {
                        //         log.taskCompletedList.each(function (t) {
                        //             var copyLog = Object.clone( log );
                        //             copyLog.readList = [];
                        //             copyLog.readCompletedList = [];
                        //             copyLog.taskList = [];
                        //             copyLog.taskCompletedList = [t];
                        //             sortedList.push( copyLog );
                        //         }.bind(this));
                        //     }
                        // }.bind(this));
                        // sortedList.sort(function(a, b){
                        //     if( this.json.sortTypeInCategory === "completedTimeAsc" ) {
                        //         return Date.parse(a.taskCompletedList[0].completedTime) - Date.parse(b.taskCompletedList[0].completedTime);
                        //     }else if( this.json.sortTypeInCategory === "completedTimeDesc" ) {
                        //         return Date.parse(b.taskCompletedList[0].completedTime) - Date.parse(a.taskCompletedList[0].completedTime);
                        //     }
                        // }.bind(this));
                        // list.each( function( log ) {
                        //     if (log.taskList.length) {
                        //         log.taskList.each(function (t) {
                        //             var copyLog = Object.clone(log);
                        //             copyLog.readList = [];
                        //             copyLog.readCompletedList = [];
                        //             copyLog.taskCompletedList = [];
                        //             copyLog.taskList = [t];
                        //             sortedList.push(copyLog);
                        //         }.bind(this));
                        //     }
                        // })
                    }

                    if (this.json.mode==="table"){
                        this.loadRecordLogTable( list, div );
                    }else if (this.json.mode==="text"){
                        this.loadRecordLogText( list, div );
                    }else if (this.json.mode==="media"){
                        this.loadRecordLogMedia( list, div );
                    }else{
                        this.loadRecordLogDefault( list, div );
                    }

                    this._loadTableStyles();
                }
            }.bind(this));
            if( this.categoryList.length > this.expandCount ){
                this.loadExpandCollapseNode();
            }
        }
    },

    _loadRecordCategoryList : function( category ){
        this.categoryList = [];
        this.categoryJson = {};
        if( category === "fromOpinionGroup" ){
            this.workLog.sort( function( a, b ){
                if( a.properties.fromOpinionGroup && b.properties.fromOpinionGroup ) {
                    var array1 = a.properties.fromOpinionGroup.split("#");
                    var array2 = b.properties.fromOpinionGroup.split("#");
                    for (var i = 0; i < array1.length; i++) {
                        var value1 = array1[i];
                        var value2 = array2[i];
                        if (this.isNumber(value1) && this.isNumber(value2)) {
                            if (parseFloat(value1) !== parseFloat(value2)) {
                                return parseFloat(value1) - parseFloat(value2);
                            }else{
                                if( this.json.sortTypeInCategory === "none" ){
                                    return 0;
                                }else{
                                    return Date.parse(a.recordTime) - Date.parse(b.recordTime);
                                }
                            }
                        } else if (!this.isNumber(value1) && !this.isNumber(value2)) {
                            if( this.json.sortTypeInCategory === "none" ){
                                return 0;
                            }else{
                                return Date.parse(a.recordTime) - Date.parse(b.recordTime);
                            }
                        } else {
                            return this.isNumber(value1) ? -1 : 1;
                        }
                    }
                    return Date.parse(a.recordTime) - Date.parse(b.recordTime);
                }else if( a.properties.fromOpinionGroup || b.properties.fromOpinionGroup ){
                    return a.properties.fromOpinionGroup ? -1 : 1;
                }else{
                    if( this.json.sortTypeInCategory === "none" ){
                        return 0;
                    }else{
                        return Date.parse(a.recordTime) - Date.parse(b.recordTime);
                    }
                }
            }.bind(this))
        }
        this.workLog.each( function(log, idx){
            var key;
            if( this.json.category === "activityGroup" ){
                if( log.properties.fromOpinionGroup ){
                    var arr = log.properties.fromOpinionGroup.split("#");
                    key = arr[arr.length-1]
                }else{
                    key = log.fromActivityName;
                }
            }else{
                key = log[category];
            }

            if( key && this.checkRecordShow(log)){
                if( this.categoryList.indexOf( key ) === -1 ){
                    this.categoryList.push( key );
                }
                if( !this.categoryJson[key] )this.categoryJson[key] = [];
                this.categoryJson[key].push( log );
            }
        }.bind(this))
    },

    loadRecordLogTable: function( list, container ){
        var taskTable = new Element("table", {
            "styles": this.form.css.logTableTask,
            "border": "0",
            "cellSpacing": "0",
            "cellpadding": "3px",
            "width": "100%"
        }).inject(container || this.node);
        var tr = taskTable.insertRow(0).setStyles(this.form.css.logTableTaskTitleLine);

        var td = tr.insertCell(0).setStyles(this.form.css.logTableTaskTitle);
        td.set("text", MWF.xApplication.process.Xform.LP.person);
        td = tr.insertCell(1).setStyles(this.form.css.logTableTaskTitle);
        td.set("text", MWF.xApplication.process.Xform.LP.activity);
        td = tr.insertCell(2).setStyles(this.form.css.logTableTaskTitle);
        td.set("text", MWF.xApplication.process.Xform.LP.department);
        td = tr.insertCell(3).setStyles(this.form.css.logTableTaskTitle);
        td.set("text", MWF.xApplication.process.Xform.LP.startTime);
        td = tr.insertCell(4).setStyles(this.form.css.logTableTaskTitle);
        td.set("text", MWF.xApplication.process.Xform.LP.completedTime);
        td = tr.insertCell(5).setStyles(this.form.css.logTableTaskTitle);
        td.set("text", MWF.xApplication.process.Xform.LP.route);
        td = tr.insertCell(6).setStyles(this.form.css.logTableTaskTitle);
        td.set("text", MWF.xApplication.process.Xform.LP.opinion);

        td = tr.insertCell(7).setStyles(this.form.css.logTableTaskTitle);
        td.set("text", MWF.xApplication.process.Xform.LP.arrivedActivitys);
        td = tr.insertCell(8).setStyles(this.form.css.logTableTaskTitle);
        td.set("text", MWF.xApplication.process.Xform.LP.arrivedUsers);

        if( list ){
            list.each(function(log, idx){
                this.loadRecordLogLine_table(log, idx, taskTable );
            }.bind(this));
        }else{
            this.workLog.each(function(log, idx){
                if (this.checkRecordShow(log)) this.loadRecordLogLine_table(log, idx, taskTable );
            }.bind(this));
        }
    },
    checkRecordShow: function(log){
        var flag = true;
        if (this.json.filterScript && this.json.filterScript.code){
            this.form.Macro.environment.log = log;
            this.form.Macro.environment.list = null;
            flag = this.form.Macro.exec(this.json.filterScript.code, this);
        }else{
            var isExactMatch = function (property) {
                return property && o2.typeOf(property)==="array" && property.length && property[0] === "yes";
            };
            if (this.json.filterActivity && this.json.filterActivity.length){
                filterActivitys = this.json.filterActivity;
                if(isExactMatch(this.json.filterActivityExactMatch)){ //精确匹配
                    flag = filterActivitys.split(/[;|,|\n]/gm).contains(log.fromActivityName); //用;,和空格分隔成数组
                }else{
                    flag = (filterActivitys.indexOf(log.fromActivityName)!==-1);
                }
            }

            if (this.json.filterActivityAlias){
                if (this.json.filterActivityAlias.length){
                    filterActivityAlias = this.json.filterActivityAlias;
                    //flag = ((log.fromActivityAlias) && filterActivityAlias.indexOf(log.fromActivityAlias)!==-1);
                    if(log.fromActivityAlias){
                        if(isExactMatch(this.json.filterActivityAliasExactMatch)){ //精确匹配
                            flag = filterActivityAlias.split(/[;|,|\n]/gm).contains(log.fromActivityAlias); //用;,和空格分隔成数组
                        }else{
                            flag = (filterActivityAlias.indexOf(log.fromActivityAlias)!==-1);
                        }
                    }
                }
            }
            if (this.json.filterPerson && this.json.filterPerson.length){
                if(isExactMatch(this.json.filterPersonExactMatch)) { //精确匹配
                    flag = false;
                    var filterPersonsList = this.json.filterPerson.split(/[;|,|\n]/gm);
                    var matchArr = log.person.split("@").concat([log.person]);
                    for(var i=0; i < filterPersonsList.length; i++){
                        if( matchArr.contains(filterPersonsList[i]) ){
                            flag = true;
                            break;
                        }
                    }
                }else{
                    flag = (this.json.filterPerson.indexOf(log.person)!==-1);
                    if (!flag) flag = (this.json.filterPerson.indexOf(o2.name.cn(log.person))!==-1);
                }
            }

            //if (this.json.filterRoute && this.json.filterRoute.length){
            //    filterRoutes = this.json.filterRoute;
            //    flag = (filterRoutes.indexOf(log.properties.routeName)!==-1);
            //}
            if (this.json.filterRoute && this.json.filterRoute.length){
                filterRoutes = this.json.filterRoute;
                if(isExactMatch(this.json.filterRouteExactMatch)){ //精确匹配
                    flag = filterRoutes.split(/[;|,|\n]/gm).contains(log.properties.routeName); //用;,和空格分隔成数组
                }else{
                    flag = (filterRoutes.indexOf(log.properties.routeName)!==-1);
                }
            }
        }
        return flag;
    },

    loadRecordLogLine_table: function(log, idx, taskTable){
        var style = ((idx % 2)===0) ? "logTableTaskLine" : "logTableTaskLine_even";
        if (log.type==="currentTask"){
            if (this.json.isTask) this.loadRecordTaskLine_table(log, taskTable, true, style);
        }else{
            this.loadRecordTaskLine_table(log, taskTable, false, style);
        }
    },
    loadRecordTaskLine_table: function(task, table, isTask, style){
        // var style = "logTableTaskLine";
        // "logTableTaskLine_even"

        css = (isTask) ? Object.merge(this.form.css["logTableTaskLine_task"], this.form.css[style] ): this.form.css[style];


        if (isTask) style = "logTableTaskLine_task";
        var tr = table.insertRow(table.rows.length);
        var td = tr.insertCell(0).setStyles(css);

        var person = (task.person) ? task.person.substring(0, task.person.indexOf("@")) : "";
        if (task.properties.empowerFromIdentity){
            var ep = o2.name.cn(task.properties.empowerFromIdentity);
            person = person + " "+MWF.xApplication.process.Xform.LP.replace+" " + ep;
        }
        if (task.type === "empower") task.properties.startTime = task.recordTime;
        td.set("text", person);
        td = tr.insertCell(1).setStyles(css);
        td.set("text", task.fromActivityName || "");
        td = tr.insertCell(2).setStyles(css);
        td.set("text", (task.unit) ? task.unit.substring(0, task.unit.indexOf("@")) : "");
        td = tr.insertCell(3).setStyles(css);
        td.set("text", task.properties.startTime || "");
        td = tr.insertCell(4).setStyles(css);
        td.set("text", task.recordTime || "");

        var router, opinion, arrivedActivitys, arrivedUsers;
        arrivedActivitys = task.properties.nextManualList.map(function(o){
            return o.activityName;
        }).join(",");
        arrivedUsers = (task.properties.nextManualTaskIdentityList && task.properties.nextManualTaskIdentityList.length) ? o2.name.cns(task.properties.nextManualTaskIdentityList).join(",") : "";

        switch (task.type) {
            case "empower":
                router = MWF.xApplication.process.Xform.LP.empower;
                var empowerTo = (task.properties.nextManualTaskIdentityList && task.properties.nextManualTaskIdentityList.length) ? o2.name.cns(task.properties.nextManualTaskIdentityList).join(",") : "";
                opinion = MWF.xApplication.process.Xform.LP.empowerTo + empowerTo;
                task.properties.startTime = task.recordTime;
                break;
            case "retract":
                router = MWF.xApplication.process.Xform.LP.retract;
                opinion = MWF.xApplication.process.Xform.LP.retract;
                break;
            case "reroute":
                router = task.properties.routeName || MWF.xApplication.process.Xform.LP.reroute;
                opinion = task.properties.opinion || MWF.xApplication.process.Xform.LP.rerouteTo+": "+arrivedActivitys;
                break;
            case "rollback":
                router = task.properties.routeName || MWF.xApplication.process.Xform.LP.rollback;
                opinion = task.properties.opinion || MWF.xApplication.process.Xform.LP.rollbackTo+": "+task.arrivedActivityName;
                break;
            case "reset":
                var resetUser = task.properties.nextManualTaskIdentityList.erase(task.identity);
                resetUserText = o2.name.cns(resetUser).join(",");
                router = MWF.xApplication.process.Xform.LP.resetTo+":"+resetUserText;
                opinion = task.properties.opinion || ""
                break;
            case "appendTask":
            case "back":
            case "addSplit":
            case "urge":
            case "expire":
            case "read":
            default:
                router = task.properties.routeName || "";
                opinion = task.properties.opinion || "";
        }


        td = tr.insertCell(5).setStyles(css);
        td.set("text",router);
        var opinionTd = tr.insertCell(6).setStyles(css);
        opinionTd.set("html", "<div style='display: inline-block; float: left'>"+opinion+"</div>");
        td = tr.insertCell(7).setStyles(css);
        td.set("text",arrivedActivitys);
        td = tr.insertCell(8).setStyles(css);
        td.set("html", arrivedUsers);

        var atts = [];
        if (task.properties.mediaOpinion){
            var mediaIds = task.properties.mediaOpinion.split(",");
            if (this.form.businessData.attachmentList){
                this.form.businessData.attachmentList.each(function(att){
                    if (att.site==="$mediaOpinion"){
                        if (mediaIds.indexOf(att.id)!==-1) atts.push(att);
                    }
                }.bind(this));
            }
            if (atts.length) this.loadMediaOpinion(atts, opinionTd.getFirst(), "table");
        }

        this.fireEvent("postLoadLine",[{
            "data" : task,
            "node" : tr,
            "atts" : atts,
            "log" : this,
            "type" : isTask ? "task" : "taskCompleted"
        }]);
    },
    loadRecordLogDefault: function(list, container){
        var logActivityNode = new Element("div", {"styles": this.form.css.logActivityNode_record}).inject(container || this.node);
        if( list ){
            list.each(function(log, idx){
                this.loadRecordLogLine_default(log, idx, logActivityNode);
            }.bind(this));
        }else{
            this.workLog.each(function(log, idx){
                if (this.checkRecordShow(log)) this.loadRecordLogLine_default(log, idx, logActivityNode);
            }.bind(this));
        }
    },
    loadRecordLogLine_default: function(log, idx, container){
        //var style = ((idx % 2)===0) ? "logTableTaskLine_even" : "logTableTaskLine";
        var style = ((idx % 2)===0) ? "logActivityChildRecordNode" : "logActivityChildRecordNode_even";

        var childNode = new Element("div", {"styles": this.form.css[style]}).inject((container || this.node));
        if (log.type==="currentTask"){
            if (this.json.isTask) this.loadRecordTaskLine_default(log, childNode, true);
        }else{
            this.loadRecordTaskLine_default(log, childNode, false);
        }
    },
    loadRecordTaskLine_default: function(task, node, isTask, margin, isZebra, nodeStyle, noIconNode){

        var style = "logTaskNode";
        var textStyle = "logTaskFloatTextNode";
        if (nodeStyle){
            style = "logTaskTextNode";
            textStyle = "logTaskTextNode";
        }
        var logTaskNode = new Element("div", {"styles": this.form.css[style]}).inject(node);
        var iconNode;
        if( !noIconNode ){
            iconNode = new Element("div", {"styles": this.form.css.logTaskIconNode_record}).inject(logTaskNode);
        }
        var textNode = new Element("div", {"styles": this.form.css[textStyle]}).inject(logTaskNode);

        if (isZebra){
            logTaskNode.setStyles(this.form.css[this.lineClass]);
            if (this.lineClass === "logTaskNode"){
                this.lineClass = "logTaskNode_even";
            }else{
                this.lineClass = "logTaskNode";
            }
        }

        var left = 0;
        if( iconNode ){
            if (margin) iconNode.setStyle("margin-left", margin);
            left = iconNode.getStyle("margin-left").toInt();
            left = left + 28;
        }
        if( !nodeStyle ){
            textNode.setStyle("margin-left",left+"px");
        }
        var html;
        var company = "";
        var atts = [];
        if (!isTask){
            company = (task.unitList) ? task.unitList[task.unitList.length-1] : "";
            html = this.json.textStyle;

            var lp = MWF.xApplication.process.Xform.LP;
            if (task.type=="empower") html = "<font style='color:#ff5400;'>{person}</font>（{department}）"+lp.in+"【{activity}】"+lp.activity+"，"+lp.empowerTo +"<font style='color:#ff5400;'>{empowerTo}</font>。（{time}）</font>";

            var nextTaskText = (task.properties.nextManualTaskIdentityList && task.properties.nextManualTaskIdentityList.length) ? o2.name.cns(task.properties.nextManualTaskIdentityList).join(",") : "";

            if (this.json.textStyleScript && this.json.textStyleScript.code){
                this.form.Macro.environment.log = task;
                this.form.Macro.environment.list = null;
                html = this.form.Macro.exec(this.json.textStyleScript.code, this);
            }

            // var person = (task.person) ? task.person.substring(0, task.person.indexOf("@")) : "";
            // if( task.type !== "empowerTask" && task.empowerFromIdentity){
            //     person = person+" "+lp.replace+" "+o2.name.cn(task.empowerFromIdentity||"");
            // }

            var person = (task.person) ? task.person.substring(0, task.person.indexOf("@")) : "";
            if (task.properties.empowerFromIdentity){
                var ep = o2.name.cn(task.properties.empowerFromIdentity);
                person = person + " "+MWF.xApplication.process.Xform.LP.replace+" " + ep;
            }

            var router, opinion, arrivedActivitys, arrivedUsers;
            arrivedActivitys = task.properties.nextManualList.map(function(o){
                return o.activityName;
            }).join(",");
            arrivedUsers = (task.properties.nextManualTaskIdentityList && task.properties.nextManualTaskIdentityList.length) ? o2.name.cns(task.properties.nextManualTaskIdentityList).join(",") : "";

            switch (task.type) {
                case "empower":

                    router = MWF.xApplication.process.Xform.LP.empower;
                    var empowerTo = (task.properties.nextManualTaskIdentityList && task.properties.nextManualTaskIdentityList.length) ? o2.name.cns(task.properties.nextManualTaskIdentityList).join(",") : "";
                    opinion = MWF.xApplication.process.Xform.LP.empowerTo + empowerTo;
                    task.properties.startTime = task.recordTime;
                    break;
                case "retract":
                    router = MWF.xApplication.process.Xform.LP.retract;
                    opinion = MWF.xApplication.process.Xform.LP.retract;
                    break;
                case "reroute":
                    router = task.properties.routeName || MWF.xApplication.process.Xform.LP.reroute;
                    opinion = task.properties.opinion || MWF.xApplication.process.Xform.LP.rerouteTo+": "+arrivedActivitys;
                    break;
                case "rollback":
                    router = task.properties.routeName || MWF.xApplication.process.Xform.LP.rollback;
                    opinion = task.properties.opinion || MWF.xApplication.process.Xform.LP.rollbackTo+": "+task.arrivedActivityName;
                    break;
                case "reset":
                    var resetUser = task.properties.nextManualTaskIdentityList.erase(task.identity);
                    resetUserText = o2.name.cns(resetUser).join(",");
                    router = MWF.xApplication.process.Xform.LP.resetTo+":"+resetUserText;
                    opinion = task.properties.opinion || ""
                    break;
                case "appendTask":
                case "back":
                case "addSplit":
                case "urge":
                case "expire":
                case "read":
                case "task":
                case "empowerTask":
                default:
                    router = task.properties.routeName || "";
                    opinion = task.properties.opinion || "";
            }

            html = html.replace(/\{person\}/g, person );
            html = html.replace(/\{department\}/g, (task.unit) ? task.unit.substring(0, task.unit.indexOf("@")) : "");
            html = html.replace(/\{route\}/g, router);
            html = html.replace(/\{time\}/g, task.recordTime);
            html = html.replace(/\{date\}/g, new Date().parse(task.recordTime).format("%Y-%m-%d"));
            html = html.replace(/\{opinion\}/g, opinion);
            html = html.replace(/\{company\}/g, company.substring(0, company.indexOf("@")));
            html = html.replace(/\{startTime\}/g, task.properties.startTime);
            html = html.replace(/\{startDate\}/g, new Date().parse(task.properties.startTime).format("%Y-%m-%d"));
            html = html.replace(/\{activity\}/g, task.fromActivityName);
            html = html.replace(/\{arrivedActivity\}/g, arrivedActivitys);
            html = html.replace(/\{img\}/g, "<span class='mwf_log_img'></span>");
            html = html.replace(/\{empowerTo\}/g, arrivedUsers);
            html = html.replace(/\{next\}/g, nextTaskText);

            //var html = MWF.xApplication.process.Xform.LP.nextUser + task.person+"("+task.department+")" +", "+
            //    MWF.xApplication.process.Xform.LP.selectRoute + ": [" + task.routeName + "], " +
            //    MWF.xApplication.process.Xform.LP.submitAt + ": " + task.completedTime+ ", " +
            //    MWF.xApplication.process.Xform.LP.idea + ": <font style=\"color: #00F\">" + (task.opinion || "")+"</font>";

            textNode.set("html", html);
            var imgNode = textNode.getElement(".mwf_log_img");
            if (task.properties.mediaOpinion){
                var mediaIds = task.properties.mediaOpinion.split(",");
                // var atts = [];
                if (this.form.businessData.attachmentList){
                    this.form.businessData.attachmentList.each(function(att){
                        if (att.site==="$mediaOpinion"){
                            if (mediaIds.indexOf(att.id)!==-1) atts.push(att);
                        }
                    }.bind(this));
                }
                if (atts.length){
                    if (imgNode){
                        this.loadMediaOpinion_show(atts, task, imgNode, true);
                        // atts.each(function(att){
                        //     this.loadMediaOpinion_image_show(att, task, imgNode);
                        // }.bind(this));

                    }else{
                        this.loadMediaOpinion(atts, textNode, "default");
                    }
                }
            }
        }else{
            var person = (task.person) ? task.person.substring(0, task.person.indexOf("@")) : "";
            if (task.properties.empowerFromIdentity){
                var ep = o2.name.cn(task.properties.empowerFromIdentity);
                person = person + " "+MWF.xApplication.process.Xform.LP.replace+" " + ep;
            }
            html = person+"("+task.unit.substring(0, task.unit.indexOf("@"))+"), 【"+task.fromActivityName+"】" + MWF.xApplication.process.Xform.LP.processing+", "+
                MWF.xApplication.process.Xform.LP.comeTime + ": " + task.properties.startTime;
            textNode.set("html", html);
            if(iconNode)iconNode.setStyle("background-image", "url("+"../x_component_process_Xform/$Form/"+this.form.options.style+"/icon/rightRed.png)");
        }
        this.fireEvent("postLoadLine",[{
            "data" : task,
            "atts" : atts,
            "node" : logTaskNode,
            "log" : this,
            "type" : isTask ? "task" : "taskCompleted"
        }]);
    },


    loadRecordLogText: function(list, container){
        this.lineClass = "logTaskNode";
        if( list ){
            list.each(function(log, idx){
                this.loadRecordLogLine_text(log, idx, container);
            }.bind(this));
        }else{
            this.workLog.each(function(log, idx){
                if (this.checkRecordShow(log)) this.loadRecordLogLine_text(log, idx, container);
            }.bind(this));
        }
    },
    loadRecordLogLine_text: function(log, idx, container){
        if (log.type==="currentTask"){
            if (this.json.isTask) this.loadRecordTaskLine_text(log, container || this.node, true);
        }else{
            this.loadRecordTaskLine_text(log, container || this.node, false);
        }
    },
    loadRecordTaskLine_text: function(task, node, isTask){
        this.loadRecordTaskLine_default(task, node, isTask, "0px", false, true, true);
    },

    loadRecordLogMedia: function(list, container){
        if( list ){
            list.each(function(log, idx){
                this.loadRecordLogLine_media(log, idx, container);
            }.bind(this));
        }else{
            this.workLog.each(function(log, idx){
                if (this.checkRecordShow(log)) this.loadRecordLogLine_media(log, idx, container);
            }.bind(this));
        }

    },
    loadRecordLogLine_media: function(log, idx, container){
        this.loadRecordTaskLine_media(log, container);
    },
    loadRecordTaskLine_media: function(task, container){
        if (task.properties.mediaOpinion){
            var mediaIds = task.properties.mediaOpinion.split(",");
            var atts = [];
            if (this.form.businessData.attachmentList){
                this.form.businessData.attachmentList.each(function(att){
                    if (att.site==="$mediaOpinion"){
                        if (mediaIds.indexOf(att.id)!==-1) atts.push(att);
                    }
                }.bind(this));
            }
            var isCompleted = !!task.recordTime;
            task.completedTime = task.recordTime;
            var node = new Element("div").inject( container || this.node );
            if (atts.length) this.loadMediaOpinion_show(atts, task, node);

            this.fireEvent("postLoadLine",[{
                "data" : task,
                "atts" : atts,
                "node" : node,
                "log" : this,
                "type" : isCompleted ? "taskCompleted" : "task"
            }]);
        }
    },

    //worklog -----------------------------------------------------------------------------

    loadWorkLog: function(){
        if( !this.json.category || this.json.category === "none" ){
            if (this.json.mode==="table"){
                this.loadWorkLogTable();
            }else if (this.json.mode==="text"){
                   this.loadWorkLogText();
            }else if (this.json.mode==="media"){
                this.loadWorkLogMedia();
            }else{
                this.loadWorkLogDefault();
            }
        }else{
            this.loadCategoryList();
            if( !this.categoryList.length )return;
            this.expandCount = 0;
            if( this.json.expand && this.json.expand === "enable" ){
                this.expandCount = parseInt( this.json.expandCount || 0 );
            }
            this.table = new Element("table", this.json.tableProperties).inject( this.node );
            this.categoryList.each( function( key, idx ){
                var list = this.categoryJson[key];
                if( list && list.length ){

                    var tr = new Element("tr").inject( this.table );
                    if( this.expandCount && (idx + 1) > this.expandCount ){
                        tr.setStyle("display","none");
                    }
                    var text = key;
                    if( this.json.category === "unit" ){
                        text = key.split("@")[0];
                    }
                    new Element("td", {
                        styles : this.json.titleTdStyles,
                        text : text
                    }).inject( tr );

                    var td = new Element("td", {
                        styles : this.json.contentTdStyles
                    }).inject( tr );

                    var div = new Element("div",{
                        styles : this.json.contentDivStyles || {}
                    }).inject( td );

                    var sortedList = list;
                    if( this.json.sortTypeInCategory === "completedTimeAsc" || this.json.sortTypeInCategory === "completedTimeDesc" ){
                        sortedList = [];
                        list.each( function( log ){
                            if (log.taskCompletedList.length) {
                                log.taskCompletedList.each(function (t) {
                                    var copyLog = Object.clone( log );
                                    copyLog.readList = [];
                                    copyLog.readCompletedList = [];
                                    copyLog.taskList = [];
                                    copyLog.taskCompletedList = [t];
                                    sortedList.push( copyLog );
                                }.bind(this));
                            }
                        }.bind(this));
                        sortedList.sort(function(a, b){
                            if( this.json.sortTypeInCategory === "completedTimeAsc" ) {
                                return Date.parse(a.taskCompletedList[0].completedTime) - Date.parse(b.taskCompletedList[0].completedTime);
                            }else if( this.json.sortTypeInCategory === "completedTimeDesc" ) {
                                return Date.parse(b.taskCompletedList[0].completedTime) - Date.parse(a.taskCompletedList[0].completedTime);
                            }
                        }.bind(this));
                        list.each( function( log ) {
                            if (log.taskList.length) {
                                log.taskList.each(function (t) {
                                    var copyLog = Object.clone(log);
                                    copyLog.readList = [];
                                    copyLog.readCompletedList = [];
                                    copyLog.taskCompletedList = [];
                                    copyLog.taskList = [t];
                                    sortedList.push(copyLog);
                                }.bind(this));
                            }
                        })
                    }

                    if (this.json.mode==="table"){
                        this.loadWorkLogTable( sortedList, div );
                    }else if (this.json.mode==="text"){
                        this.loadWorkLogText( sortedList, div );
                    }else if (this.json.mode==="media"){
                        this.loadWorkLogMedia( sortedList, div );
                    }else{
                        this.loadWorkLogDefault( sortedList, div );
                    }

                    this._loadTableStyles();
                }
            }.bind(this));
            if( this.categoryList.length > this.expandCount ){
                this.loadExpandCollapseNode();
            }
        }
    },
    loadCategoryList : function(m1, m2){
        var category;
        if( this.json.category === "activity" ){
            category = "fromActivityName";
            this[m1 || "_loadCategoryList"]( category );
        }else if( this.json.category === "unit" ){
            category = "unit";
            this[m2 || "_loadCategoryLitBySubData"]( category );
        }else if( this.json.category === "activityGroup" ){
            category = "fromOpinionGroup";
            this[m1 || "_loadCategoryList"]( category );
        }else{
            category = this.json.category;
            this[m1 || "_loadCategoryList"]( category );
        }
    },
    // isNumber : function( d ){
    //     return parseFloat(d).toString() !== "NaN"
    // },
    _loadCategoryList : function( category ){
        this.categoryList = [];
        this.categoryJson = {};
        if( category === "fromOpinionGroup" ){
            this.workLog.sort( function( a, b ){
              if( a.fromOpinionGroup && b.fromOpinionGroup ) {
                  var array1 = a.fromOpinionGroup.split("#");
                  var array2 = b.fromOpinionGroup.split("#");
                  for (var i = 0; i < array1.length; i++) {
                      var value1 = array1[i];
                      var value2 = array2[i];
                      if (this.isNumber(value1) && this.isNumber(value2)) {
                          if (parseFloat(value1) !== parseFloat(value2)) {
                              return parseFloat(value1) - parseFloat(value2);
                          }else{
                                if( this.json.sortTypeInCategory === "none" ){
                                    return 0;
                                }else{
                                    return Date.parse(a.fromTime) - Date.parse(b.fromTime);
                                }
                            }
                      } else if (!this.isNumber(value1) && !this.isNumber(value2)) {
                          if( this.json.sortTypeInCategory === "none" ){
                              return 0;
                          }else{
                              return Date.parse(a.fromTime) - Date.parse(b.fromTime);
                          }
                      } else {
                          return this.isNumber(value1) ? -1 : 1;
                      }
                  }
                  return Date.parse(a.fromTime) - Date.parse(b.fromTime);
              }else if( a.fromOpinionGroup || b.fromOpinionGroup ){
                  return a.fromOpinionGroup ? -1 : 1;
              }else{
                  if( this.json.sortTypeInCategory === "none" ){
                      return 0;
                  }else{
                      return Date.parse(a.fromTime) - Date.parse(b.fromTime);
                  }
              }
            }.bind(this))
        }
        this.workLog.each( function(log, idx){
            var key;
            if( this.json.category === "activityGroup" ){

                if( log.fromOpinionGroup ){
                    var arr = log.fromOpinionGroup.split("#");
                    key = arr[arr.length-1]
                }else{
                    key = log.fromActivityName;
                }
            }else{
                key = log[category];
            }

            if( key && this.checkShow(log)){
                var flag = false;

                for( var i=0; i< log.taskCompletedList.length; i++ ){
                    var taskCompleted = log.taskCompletedList[i];
                    flag = this.checkListShow(log, taskCompleted);
                    if( flag )break;
                }
                if (!flag && this.json.isTask){
                    for( var i=0; i< this.json.isTask.length; i++ ){
                        var task = log.taskList[i];
                        flag = this.checkListShow(log, task);
                        if( flag )break;
                    }
                }
                if(flag){
                    if( this.categoryList.indexOf( key ) === -1 ){
                        this.categoryList.push( key );
                    }
                    if( !this.categoryJson[key] )this.categoryJson[key] = [];
                    this.categoryJson[key].push( log );
                }
            }
        }.bind(this))
    },
    _loadCategoryLitBySubData : function( category ){
        this.categoryList = [];
        this.categoryJson = {};
        this.workLog.each( function(log, idx){
            var key = log[category];

            if( this.checkShow(log) ){
                var flag = false;

                for( var i=0; i< log.taskCompletedList.length; i++ ){
                    var taskCompleted = log.taskCompletedList[i];
                    flag = this.checkListShow(log, taskCompleted);
                    if( flag )break;
                }
                if (!flag && this.json.isTask){
                    for( var i=0; i< this.json.isTask.length; i++ ){
                        var task = log.taskList[i];
                        flag = this.checkListShow(log, task);
                        if( flag )break;
                    }
                }
                if(flag){
                    var log_copy = Object.clone(log);
                    log_copy.taskCompletedList = [];
                    log_copy.taskList = [];
                    log_copy.readCompletedList = [];
                    log_copy.readList = [];

                    var sub_categoryList = [];
                    var sub_categoryJson = {};

                    for( var i=0; i< log.taskCompletedList.length; i++ ){
                        var d = log.taskCompletedList[i];
                        var key = d[category];
                        if( key ){
                            if( sub_categoryList.indexOf( key ) === -1 ){
                                sub_categoryList.push( key );
                            }
                            if( !sub_categoryJson[key] )sub_categoryJson[key] = Object.clone(log_copy);
                            sub_categoryJson[key].taskCompletedList.push( d );
                        }
                    }

                    for( var i=0; i< log.taskList.length; i++ ){
                        var d = log.taskList[i];
                        var key = d[category];
                        if( key ){
                            if( sub_categoryList.indexOf( key ) === -1 ){
                                sub_categoryList.push( key );
                            }
                            if( !sub_categoryJson[key] )sub_categoryJson[key] = Object.clone(log_copy);
                            sub_categoryJson[key].taskList.push( d );
                        }
                    }

                    for( var i=0; i< log.readCompletedList.length; i++ ){
                        var d = log.readCompletedList[i];
                        var key = d[category];
                        if( key ){
                            if( sub_categoryList.indexOf( key ) === -1 ){
                                sub_categoryList.push( key );
                            }
                            if( !sub_categoryJson[key] )sub_categoryJson[key] = Object.clone(log_copy);
                            sub_categoryJson[key].readCompletedList.push( d );
                        }
                    }

                    for( var i=0; i< log.readList.length; i++ ){
                        var d = log.readList[i];
                        var key = d[category];
                        if( key ){
                            if( sub_categoryList.indexOf( key ) === -1 ){
                                sub_categoryList.push( key );
                            }
                            if( !sub_categoryJson[key] )sub_categoryJson[key] = Object.clone(log_copy);
                            sub_categoryJson[key].readList.push( d );
                        }
                    }

                    sub_categoryList.each( function(key){
                        if( this.categoryList.indexOf( key ) === -1 ){
                            this.categoryList.push( key );
                        }
                    }.bind(this));

                    for( var key in sub_categoryJson ){
                        if( !this.categoryJson[key] )this.categoryJson[key] = [];
                        this.categoryJson[key].push( sub_categoryJson[key] );
                    }
                }
            }

        }.bind(this))
    },
    _loadTableBorderStyle: function(){
        if (this.json.tableStyles && this.json.tableStyles.border){
            this.table.set("cellspacing", "0");
            this.table.setStyles({
                "border-top": this.json.tableStyles.border,
                "border-left": this.json.tableStyles.border
            });
            var ths = this.table.getElements("th");
            if( ths && ths.length ){
                ths.setStyles({
                    "border-bottom": this.json.tableStyles.border,
                    "border-right": this.json.tableStyles.border
                });
            }

            var tds = this.table.getElements("td");
            if( tds && tds.length ) {
                tds.setStyles({
                    "border-bottom": this.json.tableStyles.border,
                    "border-right": this.json.tableStyles.border
                });
            }
        }
    },
    _loadTableStyles: function(){
        Object.each(this.json.tableStyles || {}, function(value, key){
            var reg = /^border\w*/ig;
            if (!key.test(reg)){
                this.table.setStyle(key, value);
            }
        }.bind(this));
        this._loadTableBorderStyle();
    },
    loadExpandCollapseNode : function(){
        if( this.json.expand && this.json.expand === "enable" ){
            if( this.json.expandHTML ){
                this.expandNode = new Element("div",{
                    "html" : this.json.expandHTML
                }).inject( this.node, "bottom" );
                this.expandNode.addEvent("click", function(){
                    if( !this.json.category || this.json.category === "none" ){

                    }else{
                        this.table.getElements("tr").setStyle("display","");
                        this.expandNode.setStyle("display","none");
                        this.collapseNode.setStyle("display","");
                    }
                }.bind(this))
            }
            if( this.json.collapseHTML ){
                this.collapseNode = new Element("div",{
                    "html" : this.json.collapseHTML,
                    "styles" : { "display" : "none" }
                }).inject( this.node, "bottom" );
                this.collapseNode.addEvent("click", function(){
                    if( !this.json.category || this.json.category === "none" ){

                    }else{
                        var trs = this.table.getElements("tr");
                        for( var i=0; i<trs.length; i++ ){
                            if( i >= this.expandCount ){
                                trs[i].setStyle("display","none");
                            }
                        }
                        this.expandNode.setStyle("display","");
                        this.collapseNode.setStyle("display","none");
                    }
                }.bind(this))
            }
        }
    },


    loadWorkLogMedia: function(list, container){
        if( list ){
            list.each(function(log, idx){
                this.loadWorkLogLine_media(log, idx, container);
            }.bind(this));
        }else{
            this.workLog.each(function(log, idx){
                if (this.checkShow(log)) this.loadWorkLogLine_media(log, idx, container);
            }.bind(this));
        }

    },
    loadWorkLogLine_media: function(log, idx, container){
        if (log.taskCompletedList.length){
            log.taskCompletedList.each(function(taskCompleted){
                if (this.checkListShow(log, taskCompleted)) this.loadTaskLine_media(taskCompleted, log, container);
            }.bind(this));
        }
    },
    loadTaskLine_media: function(task, log, container){
        if (task.mediaOpinion){
            var mediaIds = task.mediaOpinion.split(",");
            var atts = [];
            if (this.form.businessData.attachmentList){
                this.form.businessData.attachmentList.each(function(att){
                    if (att.site==="$mediaOpinion"){
                        if (mediaIds.indexOf(att.id)!==-1) atts.push(att);
                    }
                }.bind(this));
            }
            var node = new Element("div").inject( container || this.node );
            if (atts.length) this.loadMediaOpinion_show(atts, task, node);

            this.fireEvent("postLoadLine",[{
                "data" : task,
                "atts" : atts,
                "node" : node,
                "log" : this,
                "type" : !!task.completedTime ? "taskCompleted" : "task"
            }]);
        }
    },
    loadMediaOpinion_show: function(atts, task, container, noName){
        atts.each(function(att){
            //if (!att.contentType) att.contentType = "image";
            if (att.type){
                if (att.type.indexOf("image")!==-1){
                    this.loadMediaOpinion_image_show(att, task, container, noName);
                }else if(att.type.indexOf("video")!==-1){
                    this.loadMediaOpinion_video_show(att, task, container);
                }else if(att.type.indexOf("audio")!==-1){
                    this.loadMediaOpinion_voice_show(att, task, container);
                }else{
                    this.loadMediaOpinion_voice_show(att, task, container);
                }
            }
        }.bind(this));
    },
    loadMediaOpinion_image_show: function(att, task, container, noName){
        var url = this.getMediaOpinionUrl(att);
        var node = new Element("div", {"styles": {"overflow": "hidden"}}).inject( container || this.node);
        if (!noName){
            var textNode = new Element("div", {
                "styles": {
                    "line-height": "28px",
                    "height": "28px"
                },
                "text": task.person.substring(0, task.person.indexOf("@"))+"("+task.completedTime+")"
            }).inject(node);
        }

        //var img = new Element("img", {"src": url, "styles": {"background-color": "#ffffff"}}).inject(node);
        //
        //var height = 200;
        //var width = 300;
        //if (layout.mobile){
        //    var size = img.getSize();
        //    width = 200;
        //    height = 200*(size.y/size.x);
        //}
        //img.setStyles({"width": ""+width+"px", "height": ""+height+"px"});


        var imgNode = new Element("div").inject(node);
        var width;
        if (layout.mobile){
            width = 200;
        }else{
            var pNode = node.getParent();
            var offset = imgNode.getPosition( pNode );
            //width = Math.min( pNode.getSize().x - offset.x - 2, 800 );
            width = pNode.getSize().x - offset.x - 42;
        }

        var img = new Element("img", {
            "src": url,
            "styles" : { width : width+"px" },
            "events" : {
                load : function(ev){
                    var nh=ev.target.naturalHeight;
                    var nw = ev.target.naturalWidth;
                    if( !layout.mobile && ( this.isNumber( this.json.handwritingWidth ) || this.isNumber( this.json.handwritingHeight ) ) ){
                        var size = this.getImageSize( nw, nh );
                        img.setStyles(size);
                        imgNode.setStyles(size);
                    }else{
                        var x = Math.min(nw, width);
                        img.setStyles({"width": ""+ x +"px"});
                        imgNode.setStyles({"width": ""+ x +"px"});
                    }
                }.bind(this)
            }
        }).inject(imgNode);

        // var size = img.getSize();
        // var x_y = size.x/size.y;
        // if (size.y>260){
        //     var y = 260;
        //     var x = 260*x_y;
        //     img.setStyles({"width": ""+x+"px", "height": ""+y+"px"})
        // }
    },
    isNumber : function( d ){
        return parseFloat(d).toString() !== "NaN"
    },
    getImageSize : function(naturalWidth, naturalHeight ){
        var ww = this.json.handwritingWidth;
        var wh = this.json.handwritingHeight;
        if( this.isNumber(ww)  && !this.isNumber(wh) ){
            return {
                width : Math.min( naturalWidth, parseInt( ww )  ) + "px",
                height : "auto"
            }
        }else if( !this.isNumber(ww)  && this.isNumber(wh) ){
            return {
                width : "auto",
                height : Math.min( naturalHeight, parseInt( wh )  ) + "px"
            }
        }else if( this.isNumber(ww)  && this.isNumber(wh) ){
            var flag = ( naturalWidth / parseInt(ww) ) > ( naturalHeight / parseInt(wh) );
            if( flag ){
                return {
                    width : Math.min( naturalWidth, parseInt( ww )  ) + "px",
                    height : "auto"
                }
            }else{
                return {
                    width : "auto",
                    height : Math.min( naturalHeight, parseInt( wh )  ) + "px"
                }
            }
        }
    },
    loadMediaOpinion_video_show: function(att, task, container){

    },
    loadMediaOpinion_voice_show: function(att, task, container){
        //var node = new Element("audio").inject(this.node);
        var url = this.getMediaOpinionUrl(att);
        var div = new Element("div", {"styles": {"overflow": "hidden"}}).inject(container || this.node);
        var textNode = new Element("div", {
            "styles": {
                "line-height": "28px",
                "height": "28px"
            },
            "text": task.person.substring(0, task.person.indexOf("@"))+"("+task.completedTime+")"
        }).inject(div);

        var node = new Element("audio", {"loop": false, "controls": true}).inject(div);
        node.set("src", url);
        //this.audioNode.play();
    },

    loadWorkLogTable: function( list, container ){
        if( list ){
            list.each(function(log, idx){
                this.loadWorkLogLine_table(log, idx, container );
            }.bind(this));
        }else{
            this.workLog.each(function(log, idx){
                if (this.checkShow(log)) this.loadWorkLogLine_table(log, idx, container );
            }.bind(this));
        }
    },
    loadWorkLogLine_table: function(log, idx, container){
        if (!log.readList) log.readList = [];
        if (!log.readCompletedList) log.readCompletedList = [];
        if (log.taskCompletedList.length || log.readList.length || log.readCompletedList.length || (this.json.isTask && log.taskList.length)){
            var logActivityNode = new Element("div", {"styles": this.form.css.logActivityNode}).inject( container || this.node);
            var titleNode = new Element("div", {"styles": this.form.css.logActivityTitleNode}).inject(logActivityNode);
            var childNode = new Element("div", {"styles": this.form.css.logActivityChildNode}).inject(logActivityNode);

            var iconNode = new Element("div", {"styles": this.form.css.logActivityIconNode}).inject(titleNode);
            var fromAvtivityNode = new Element("div", {"styles": this.form.css.logActivityFromNode}).inject(titleNode);
            var arrowNode = new Element("div", {"styles": this.form.css.logActivityArrowNode}).inject(titleNode);
            var arrivedAvtivityNode = new Element("div", {"styles": this.form.css.logActivityArrivedNode}).inject(titleNode);

            var timeNode = new Element("div", {"styles": this.form.css.logActivityTimeNode}).inject(titleNode);

            if (log.connected){
                iconNode.setStyle("background-image", "url("+"../x_component_process_Xform/$Form/"+this.form.options.style+"/icon/ok14.png)");
            }else{
                iconNode.setStyle("background-image", "url("+"../x_component_process_Xform/$Form/"+this.form.options.style+"/icon/rightRed.png)");
            }
            fromAvtivityNode.set("html", "<b>"+log.fromActivityName+"</b>");
            if (log.arrivedActivityName){
                arrowNode.setStyle("background-image", "url("+"../x_component_process_Xform/$Form/"+this.form.options.style+"/icon/right.png)");
                arrivedAvtivityNode.set("html", "<b>"+log.arrivedActivityName+"</b>");
                timeNode.set("html", "<b>"+MWF.xApplication.process.Xform.LP.begin+": </b>"+log.fromTime+"<br/><b>"+MWF.xApplication.process.Xform.LP.end+": </b>"+log.arrivedTime)

            }else{
                timeNode.set("html", "<b>"+MWF.xApplication.process.Xform.LP.begin+": </b>"+log.fromTime)
            }

            if ((idx % 2)===0){
                logActivityNode.setStyles(this.form.css.logActivityNode_even);
                titleNode.setStyles(this.form.css.logActivityTitleNode_even);
            }


            var taskTable = new Element("table", {
                "styles": this.form.css.logTableTask,
                "border": "0",
                "cellSpacing": "0",
                "cellpadding": "3px",
                "width": "100%"
            }).inject(childNode);
            var tr = taskTable.insertRow(0).setStyles(this.form.css.logTableTaskTitleLine);

            var td = tr.insertCell(0).setStyles(this.form.css.logTableTaskTitle);
            td.set("text", MWF.xApplication.process.Xform.LP.person);
            td = tr.insertCell(1).setStyles(this.form.css.logTableTaskTitle);
            td.set("text", MWF.xApplication.process.Xform.LP.department);
            td = tr.insertCell(2).setStyles(this.form.css.logTableTaskTitle);
            td.set("text", MWF.xApplication.process.Xform.LP.startTime);
            td = tr.insertCell(3).setStyles(this.form.css.logTableTaskTitle);
            td.set("text", MWF.xApplication.process.Xform.LP.completedTime);
            td = tr.insertCell(4).setStyles(this.form.css.logTableTaskTitle);
            td.set("text", MWF.xApplication.process.Xform.LP.route);
            td = tr.insertCell(5).setStyles(this.form.css.logTableTaskTitle);
            td.set("text", MWF.xApplication.process.Xform.LP.opinion);

            log.taskCompletedList.each(function(taskCompleted){
                if (this.checkListShow(log, taskCompleted)) this.loadTaskLine_table(taskCompleted, taskTable, log, false);
            }.bind(this));

            if (this.json.isShowRead!==false){
                var readNames = [];
                var readCompletedNames = [];
                if (log.readList && log.readList.length){
                    log.readList.each(function(read){
                        readNames.push(MWF.name.cn(read.person));
                    });
                }
                if (log.readCompletedList && log.readCompletedList.length){
                    log.readCompletedList.each(function(read){
                        readCompletedNames.push(MWF.name.cn(read.person));
                    });
                }
                this.loadReadLine_default(readNames, readCompletedNames, childNode);
            }

            if (this.json.isTask){
                log.taskList.each(function(task){
                    if (this.checkListShow(log, task)) this.loadTaskLine_table(task, taskTable, log, true);
                }.bind(this));
            }
        }

    },
    loadTaskLine_table: function(task, table, log, isTask){
        var style = "logTableTaskLine";
        if (isTask) style = "logTableTaskLine_task";
        var tr = table.insertRow(table.rows.length);
        var td = tr.insertCell(0).setStyles(this.form.css[style]);
        td.set("text", task.person.substring(0, task.person.indexOf("@")) || "");
        td = tr.insertCell(1).setStyles(this.form.css[style]);
        td.set("text", task.unit.substring(0, task.unit.indexOf("@")) || "");
        td = tr.insertCell(2).setStyles(this.form.css[style]);
        td.set("text", task.startTime || "");
        td = tr.insertCell(3).setStyles(this.form.css[style]);
        td.set("text", task.completedTime || "");
        td = tr.insertCell(4).setStyles(this.form.css[style]);
        td.set("text", (task.processingType=="empower") ? MWF.xApplication.process.Xform.LP.empower : task.routeName || "");
        td = tr.insertCell(5).setStyles(this.form.css[style]);
        opinion = (task.processingType=="empower") ? MWF.xApplication.process.Xform.LP.empowerTo + o2.name.cn(task.empowerToIdentity || "") : "<div style='line-height: 28px; float:left'>" + (task.opinion || "")+"</div>";
        td.set("html", opinion);


        var atts = [];
        if (task.mediaOpinion){
            var mediaIds = task.mediaOpinion.split(",");
            if (this.form.businessData.attachmentList){
                this.form.businessData.attachmentList.each(function(att){
                    if (att.site==="$mediaOpinion"){
                        if (mediaIds.indexOf(att.id)!==-1) atts.push(att);
                    }
                }.bind(this));
            }
            if (atts.length) this.loadMediaOpinion(atts, td.getFirst(), "table");
        }
        this.fireEvent("postLoadLine",[{
            "data" : task,
            "atts" : atts,
            "node" : tr,
            "log" : this,
            "type" : isTask ? "task" : "taskCompleted"
        }]);
    },



    loadWorkLogText: function(list, container){
        this.lineClass = "logTaskNode";
        if( list ){
            list.each(function(log, idx){
                this.loadWorkLogLine_text(log, idx, container);
            }.bind(this));
        }else{
            this.workLog.each(function(log, idx){
                if (this.checkShow(log)) this.loadWorkLogLine_text(log, idx, container);
            }.bind(this));
        }
    },
    loadWorkLogLine_text: function(log, idx, container){
        log.taskCompletedList.each(function(taskCompleted){
            if (this.checkListShow(log, taskCompleted)) this.loadTaskLine_text(taskCompleted, container || this.node, log, false);
        }.bind(this));

        if (this.json.isTask){
            log.taskList.each(function(task){
                if (this.checkListShow(log, task)) this.loadTaskLine_text(task, container || this.node, log, true);
            }.bind(this));
        }
    },
    loadTaskLine_text: function(task, node, log, isTask){
        this.loadTaskLine_default(task, node, log, isTask, "0px", false, true, true);
    },

    checkShow: function(log){
        var flag = true;
        if (this.json.filterScript && this.json.filterScript.code){
            this.form.Macro.environment.log = log;
            this.form.Macro.environment.list = null;
            flag = this.form.Macro.exec(this.json.filterScript.code, this);
        }else{
            debugger;
            var isExactMatch = function (property) {
                return property && o2.typeOf(property)==="array" && property.length && property[0] === "yes";
            };
            if (this.json.filterActivity && this.json.filterActivity.length){
                filterActivitys = this.json.filterActivity;
                if(isExactMatch(this.json.filterActivityExactMatch)){ //精确匹配
                    flag = filterActivitys.split(/[;|,|\n]/gm).contains(log.fromActivityName); //用;,和空格分隔成数组
                }else{
                    flag = (filterActivitys.indexOf(log.fromActivityName)!==-1);
                }
            }
            if (this.json.filterActivityAlias){
                if (this.json.filterActivityAlias.length){
                    filterActivityAlias = this.json.filterActivityAlias;
                    if(log.fromActivityAlias){
                        if(isExactMatch(this.json.filterActivityAliasExactMatch)){ //精确匹配
                            flag = filterActivityAlias.split(/[;|,|\n]/gm).contains(log.fromActivityAlias); //用;,和空格分隔成数组
                        }else{
                            flag = (filterActivityAlias.indexOf(log.fromActivityAlias)!==-1);
                        }
                    }
                }
            }
            if (this.json.filterPerson && this.json.filterPerson.length){
                flag = false;
                filterPersons = this.json.filterPerson;

                var tmpTaskCompletedList = [];
                if(isExactMatch(this.json.filterPersonExactMatch)) { //精确匹配
                    var filterPersonsList = filterPersons.split(/[;|,|\n]/gm);
                    log.taskCompletedList.each(function(taskCompleted){
                        var matchArr = (taskCompleted.person + "@" + taskCompleted.identity).split("@").concat([taskCompleted.person, taskCompleted.identity]);
                        for(var i=0; i < filterPersonsList.length; i++){
                            if( matchArr.contains(filterPersonsList[i]) ){
                                tmpTaskCompletedList.push(taskCompleted);
                                break;
                            }
                        }
                    }.bind(this));
                }else{
                    log.taskCompletedList.each(function(taskCompleted){
                        if ((filterPersons.indexOf(taskCompleted.person)!==-1) || (filterPersons.indexOf(taskCompleted.identity)!==-1)){
                            tmpTaskCompletedList.push(taskCompleted);
                        }
                    }.bind(this));
                }
                if (tmpTaskCompletedList.length){
                    //log.taskCompletedList = [];
                    //log.taskCompletedList = tmpTaskCompletedList;
                    flag = true;
                }
            }
            if (this.json.filterRoute && this.json.filterRoute.length){
                filterRoutes = this.json.filterRoute;
                if(isExactMatch(this.json.filterRouteExactMatch)){ //精确匹配
                    flag = filterRoutes.split(/[;|,|\n]/gm).contains(log.routeName); //用;,和空格分隔成数组
                }else{
                    flag = (filterRoutes.indexOf(log.routeName)!==-1);
                }
            }
        }
        return flag;
    },
    checkListShow: function(log, list){
        var flag = true;
        if (this.json.filterScript && this.json.filterScript.code){
            this.form.Macro.environment.log = log;
            this.form.Macro.environment.list = list;
            flag = this.form.Macro.exec(this.json.filterScript.code, this);
        }else{
            var isExactMatch = function (property) {
                return property && o2.typeOf(property)==="array" && property.length && property[0] === "yes";
            };
            if (this.json.filterPerson && this.json.filterPerson.length){
                var filterPerson = this.json.filterPerson;
                flag = false;
                if(isExactMatch(this.json.filterPersonExactMatch)) { //精确匹配
                    var filterPersonsList = filterPerson.split(/[;|,|\n]/gm);
                    var matchArr = (list.person + "@" + list.identity).split("@").concat([list.person, list.identity]);
                    for(var i=0; i < filterPersonsList.length; i++){
                        if( matchArr.contains(filterPersonsList[i]) ){
                            flag = true;
                            break;
                        }
                    }
                }else{
                    flag = ((filterPersons.indexOf(list.person)!==-1)|| (filterPersons.indexOf(list.identity)!==-1));
                }
            }
            // if (this.json.filterPerson && this.json.filterPerson.length){
            //     flag = ((filterPersons.indexOf(list.person)!==-1)|| (filterPersons.indexOf(list.identity)!==-1));
            // }
        }
        return flag;
    },

    loadWorkLogDefault: function(list, container){
        //var text = this.json.textStyle;
        if( list ){
            list.each(function(log, idx){
                this.loadWorkLogLine_default(log, idx, container);
            }.bind(this));
        }else{
            this.workLog.each(function(log, idx){
                if (this.checkShow(log)) this.loadWorkLogLine_default(log, idx, container);
            }.bind(this));
        }
    },
    loadWorkLogLine_default: function(log, idx, container){
        if (!log.readList) log.readList = [];
        if (!log.readCompletedList) log.readCompletedList = [];
        if (log.taskCompletedList.length || log.readList.length || log.readCompletedList.length || (this.json.isTask && log.taskList.length)) {
            var logActivityNode = new Element("div", {"styles": this.form.css.logActivityNode}).inject(container || this.node);
            var titleNode = new Element("div", {"styles": this.form.css.logActivityTitleNode}).inject(logActivityNode);
            var childNode = new Element("div", {"styles": this.form.css.logActivityChildNode}).inject(logActivityNode);

            var iconNode = new Element("div", {"styles": this.form.css.logActivityIconNode}).inject(titleNode);
            var fromAvtivityNode = new Element("div", {"styles": this.form.css.logActivityFromNode}).inject(titleNode);
            var arrowNode = new Element("div", {"styles": this.form.css.logActivityArrowNode}).inject(titleNode);
            var arrivedAvtivityNode = new Element("div", {"styles": this.form.css.logActivityArrivedNode}).inject(titleNode);

            var readActionNode = new Element("div", {"styles": this.form.css.logActivityReadActionNode}).inject(titleNode);

            var timeNode = new Element("div", {"styles": this.form.css.logActivityTimeNode}).inject(titleNode);

            if (log.connected) {
                iconNode.setStyle("background-image", "url(../x_component_process_Xform/$Form/" + this.form.options.style + "/icon/ok14.png)");
            } else {
                iconNode.setStyle("background-image", "url(../x_component_process_Xform/$Form/" + this.form.options.style + "/icon/rightRed.png)");
            }
            fromAvtivityNode.set("html", "<b>" + log.fromActivityName + "</b>");
            if (log.arrivedActivityName) {
                arrowNode.setStyle("background-image", "url(../x_component_process_Xform/$Form/" + this.form.options.style + "/icon/right.png)");
                arrivedAvtivityNode.set("html", "<b>" + log.arrivedActivityName + "</b>");
                timeNode.set("html", "<b>" + MWF.xApplication.process.Xform.LP.begin + ": </b>" + log.fromTime + "<br/><b>" + MWF.xApplication.process.Xform.LP.end + ": </b>" + log.arrivedTime)

            } else {
                timeNode.set("html", "<b>" + MWF.xApplication.process.Xform.LP.begin + ": </b>" + log.fromTime)
            }

            // if ((log.readList && log.readList.length) || (log.readCompletedList && log.readCompletedList.length)){
            //     readActionNode.set("text", MWF.xApplication.process.Xform.LP.worklogRead);
            // }

            if ((idx % 2) === 0) {
                logActivityNode.setStyles(this.form.css.logActivityNode_even);
                titleNode.setStyles(this.form.css.logActivityTitleNode_even);
            }

            log.taskCompletedList.each(function (taskCompleted) {
                if (this.checkListShow(log, taskCompleted)) this.loadTaskLine_default(taskCompleted, childNode, log, false);
            }.bind(this));

            if (this.json.isShowRead!==false){
                var readNames = [];
                var readCompletedNames = [];
                if (log.readList && log.readList.length){
                    log.readList.each(function(read){
                        readNames.push(MWF.name.cn(read.person));
                    });
                }
                if (log.readCompletedList && log.readCompletedList.length){
                    log.readCompletedList.each(function(read){
                        readCompletedNames.push(MWF.name.cn(read.person));
                    });
                }
                //if (readCompletedNames.length)
                this.loadReadLine_default(readNames, readCompletedNames, childNode);
            }

            if (this.json.isTask) {
                log.taskList.each(function (task) {
                    if (this.checkListShow(log, task)) this.loadTaskLine_default(task, childNode, log, true);
                }.bind(this));
            }
        }
    },
    loadReadLine_default: function(readNames, readCompletedNames, node){
        var html = "";
        var logReadNode = new Element("div", {"styles": this.form.css.logReadTextNode}).inject(node);
        if (readNames.length){
            var readStrTitle = readNames.join(", ");
            var readStr = (readNames.length>20) ? (readNames.slice(0,20).join(", ") + MWF.xApplication.process.Xform.LP.andSoForth ): readStrTitle;
            html = "<span style='color: #0000ff'>"+(this.json.showReadTitle || MWF.xApplication.process.Xform.LP.showReadTitle)+": </span>"+readStr+"<span>&nbsp;&nbsp;&nbsp;&nbsp;</span>";

            //var logReadPersonNode = new Element("div", {"styles": this.form.css.logReadTextNode}).inject(logReadNode);
        }
        if (readCompletedNames.length){
            var readCompletedStrTitle = readCompletedNames.join(", ");
            var readCompletedStr = (readCompletedNames.length>20) ? (readCompletedNames.slice(0,20).join(", ") + MWF.xApplication.process.Xform.LP.andSoForth ): readCompletedStrTitle;
            html += "<span style='color: #0000ff'>"+(this.json.showReadCompletedTitle || MWF.xApplication.process.Xform.LP.showReadCompletedTitle)+": </span>"+readCompletedStr+"<span>&nbsp;&nbsp;&nbsp;&nbsp;</span>";
        }
        if (html) logReadNode.set("html", html);
    },
    loadTaskLine_default: function(task, node, log, isTask, margin, isZebra, nodeStyle, noIconNode){
        var style = "logTaskNode";
        var textStyle = "logTaskFloatTextNode";
        if (nodeStyle){
            style = "logTaskTextNode";
            textStyle = "logTaskTextNode";
        }
        var logTaskNode = new Element("div", {"styles": this.form.css[style]}).inject(node);
        var iconNode;
        if( !noIconNode ){
            iconNode = new Element("div", {"styles": this.form.css.logTaskIconNode}).inject(logTaskNode);
        }
        var textNode = new Element("div", {"styles": this.form.css[textStyle]}).inject(logTaskNode);

        if (isZebra){
            logTaskNode.setStyles(this.form.css[this.lineClass]);
            if (this.lineClass === "logTaskNode"){
                this.lineClass = "logTaskNode_even";
            }else{
                this.lineClass = "logTaskNode";
            }
        }

        var left = 28;
        if( iconNode ){
            if (margin) iconNode.setStyle("margin-left", margin);
            left = iconNode.getStyle("margin-left").toInt();
            left = left + 28;
        }
        if( !nodeStyle ){
            textNode.setStyle("margin-left",left+"px");
        }
        var html;
        var company = "";
        var atts = [];
        if (!isTask){
            company = (task.unitList) ? task.unitList[task.unitList.length-1] : "";
            var html = this.json.textStyle;
            if (task.processingType=="empower") html = MWF.xApplication.process.Xform.LP.empowerToHtml;

            var nextTaskText = "";
            if (task.processingType=="empower"){
                var nextTaskParts = [];
                if (task.nextTaskIdentityListText){
                    var nestIds = task.nextTaskIdentityListText.split(",");
                    nextTaskParts = o2.name.cns(nestIds);
                }else{
                    // var nextTasks = o2.name.cns(log.nextTaskIdentityList).join(", ");
                    // var nextTaskCompleteds = o2.name.cns(log.nextTaskCompletedIdentityList).join(", ");
                    var nextTasks = (log.nextTaskIdentityList && log.nextTaskIdentityList.length) ? o2.name.cns(log.nextTaskIdentityList).join(", ") : "";
                    var nextTaskCompleteds = (log.nextTaskCompletedIdentityList && log.nextTaskCompletedIdentityList.length) ? o2.name.cns(log.nextTaskCompletedIdentityList).join(", ") : "";

                    if (nextTasks) nextTaskParts.push(nextTasks);
                    if (nextTaskCompleteds) nextTaskParts.push(nextTaskCompleteds);
                }
                nextTaskText = nextTaskParts.join(", ");
            }else{
                nextTaskText = (task.empowerToIdentity) ? o2.name.cn(task.empowerToIdentity) : "";
            }



            if (this.json.textStyleScript && this.json.textStyleScript.code){
                this.form.Macro.environment.log = log;
                this.form.Macro.environment.list = null;
                html = this.form.Macro.exec(this.json.textStyleScript.code, this);
            }

            var person = task.person.substring(0, task.person.indexOf("@"));
            if( task.processingType !== "empower" && task.empowerFromIdentity){
                person = person+" 代 "+o2.name.cn(task.empowerFromIdentity||"");
            }
            html = html.replace(/\{person\}/g, person );
            html = html.replace(/\{department\}/g, task.unit.substring(0, task.unit.indexOf("@")));
            html = html.replace(/\{route\}/g, task.routeName);
            html = html.replace(/\{time\}/g, task.completedTime);
            html = html.replace(/\{date\}/g, new Date().parse(task.completedTime).format("%Y-%m-%d"));
            html = html.replace(/\{opinion\}/g, task.opinion || "");
            html = html.replace(/\{company\}/g, company.substring(0, company.indexOf("@")));
            html = html.replace(/\{startTime\}/g, task.startTime);
            html = html.replace(/\{startDate\}/g, new Date().parse(task.startTime).format("%Y-%m-%d"));
            html = html.replace(/\{activity\}/g, log.fromActivityName);
            html = html.replace(/\{arrivedActivity\}/g, log.arrivedActivityName);
            html = html.replace(/\{img\}/g, "<span class='mwf_log_img'></span>");
            html = html.replace(/\{empowerTo\}/g, ((task.empowerToIdentity) ? o2.name.cn(task.empowerToIdentity) : ""));

            html = html.replace(/\{nextTask\}/g, nextTasks);
            html = html.replace(/\{nextTaskCompleted\}/g, nextTaskCompleteds);
            html = html.replace(/\{next\}/g, nextTaskText);

            //var html = MWF.xApplication.process.Xform.LP.nextUser + task.person+"("+task.department+")" +", "+
            //    MWF.xApplication.process.Xform.LP.selectRoute + ": [" + task.routeName + "], " +
            //    MWF.xApplication.process.Xform.LP.submitAt + ": " + task.completedTime+ ", " +
            //    MWF.xApplication.process.Xform.LP.idea + ": <font style=\"color: #00F\">" + (task.opinion || "")+"</font>";

            textNode.set("html", html);
            var imgNode = textNode.getElement(".mwf_log_img");
            if (task.mediaOpinion){
                var mediaIds = task.mediaOpinion.split(",");
                // var atts = [];
                if (this.form.businessData.attachmentList){
                    this.form.businessData.attachmentList.each(function(att){
                        if (att.site==="$mediaOpinion"){
                            if (mediaIds.indexOf(att.id)!==-1) atts.push(att);
                        }
                    }.bind(this));
                }
                if (atts.length){
                    if (imgNode){
                        this.loadMediaOpinion_show(atts, task, imgNode, true);
                        // atts.each(function(att){
                        //     this.loadMediaOpinion_image_show(att, task, imgNode);
                        // }.bind(this));

                    }else{
                        this.loadMediaOpinion(atts, textNode, "default");
                    }
                }
            }
        }else{
            //company = task.unitList[task.unitList.length-1];
            html = task.person.substring(0, task.person.indexOf("@"))+"("+task.unit.substring(0, task.unit.indexOf("@"))+")" + MWF.xApplication.process.Xform.LP.processing+", "+
                MWF.xApplication.process.Xform.LP.comeTime + ": " + task.startTime;
            textNode.set("html", html);
            if(iconNode)iconNode.setStyle("background-image", "url("+"../x_component_process_Xform/$Form/"+this.form.options.style+"/icon/rightRed.png)");
        }
        this.fireEvent("postLoadLine",[{
            "data" : task,
            "node" : logTaskNode,
            "atts" : atts,
            "log" : this,
            "type" : isTask ? "task" : "taskCompleted"
        }]);
    },
    loadMediaOpinion: function(atts, node, type){
        atts.each(function(att){
            if (!att.type) att.type = "image";
            if (att.type.indexOf("image")!==-1){
                if( this.json.handwritingExpanded ){
                    this.loadMediaOpinion_image_show(att, null, node, true)
                }else if( type === "table" && !layout.mobile ){
                    this.loadMediaOpinion_image_tooltip(att, node);
                }else{
                    this.loadMediaOpinion_image(att, node);
                }
            }else if(att.type.indexOf("video")!==-1){
                this.loadMediaOpinion_video(att, node);
            }else if(att.type.indexOf("audio")!==-1){
                this.loadMediaOpinion_voice(att, node);
            }else {
                if (this.json.handwritingExpanded){
                    this.loadMediaOpinion_image_show(att, null, node, true)
                }else if( type === "table" && !layout.mobile ){
                    this.loadMediaOpinion_image_tooltip(att, node);
                }else{
                    this.loadMediaOpinion_image(att, node);
                }
            }
        }.bind(this));
    },
    getMediaOpinionUrl: function(att){
        var action = MWF.Actions.get("x_processplatform_assemble_surface");
        var url = action.action.actions["getAttachmentStream"].uri;
        if (this.form.businessData.workCompleted){
            url = action.action.actions["getWorkcompletedAttachmentStream"].uri;
            url = url.replace("{id}", att.id);
            url = url.replace("{workCompletedId}", this.form.businessData.workCompleted.id);
        }else{
            url = url.replace("{id}", att.id);
            url = url.replace("{workid}", this.form.businessData.work.id);
        }
        url = action.action.address+url;
        return o2.filterUrl(url);
    },
    loadMediaOpinion_image_tooltip : function(att, node){
        var iconNode = new Element("div", {"styles": this.form.css.logMediaIcon}).inject(node.getParent());
        iconNode.setStyle("background-image", "url('"+this.form.path+this.form.options.style+"/icon/image.png')");
        iconNode.set("title", MWF.xApplication.process.Xform.LP.mediaOpinion_image);
        if( !this.MTooltipsLoaded )o2.xDesktop.requireApp("Template", "MTooltips", null, false);
        this.MTooltipsLoaded = true;
        var tooltip = new MTooltips(this.form.app.content, iconNode, this.form.app, null, {
            axis : "y", "delay" : 350,
            nodeStyles : {
                "max-width" : "800px"
            }
        });
        tooltip.contentNode = new Element("div",{
            styles : { width : "100%" , height : "100%" }
        });
        var img = new Element("img", {
            "src": this.getMediaOpinionUrl(att),
            "events" : {
                load : function(ev){
                    var nh = ev.target.naturalHeight;
                    var nw = ev.target.naturalWidth;
                    if( this.isNumber( this.json.handwritingWidth ) || this.isNumber( this.json.handwritingHeight ) ){
                        var size = this.getImageSize( nw, nh );
                        ev.target.setStyles(size);
                    }else{
                        var x = Math.min(nw, 800);
                        ev.target.setStyles({"width": ""+ x +"px"});
                    }
                }.bind(this)
            }
        }).inject(tooltip.contentNode);
    },
    loadMediaOpinion_image: function(att, node){
        var iconNode = new Element("div", {"styles": this.form.css.logMediaIcon}).inject(node.getParent());
        iconNode.setStyle("background-image", "url('"+this.form.path+this.form.options.style+"/icon/image.png')");
        iconNode.set("title", MWF.xApplication.process.Xform.LP.mediaOpinion_image);
        iconNode.addEvents({
            "click": function(e){
                if (e.target.mediaOpinionContentNode) return "";
                var url = this.getMediaOpinionUrl(att);
                // if (this.mediaOpinionContentNode){
                //     this.mediaOpinionContentNode.destroy();
                //     this.mediaOpinionContentNode = null;
                // }

                var imgNode = new Element("div", {"styles": this.form.css.logMediaOpinionContent}).inject(node.getParent(),"after");
                if (!layout.mobile) imgNode.setStyle("margin-left", "40px");
                //this.mediaOpinionContentNode = imgNode;
                e.target.mediaOpinionContentNode = imgNode;

                var imgCloseNode = new Element("div", {"styles": this.form.css.logMediaOpinionContentClose}).inject(imgNode);
                imgCloseNode.addEvent("click", function(){ imgNode.destroy(); e.target.mediaOpinionContentNode = "";});

                var imgAreaNode = new Element("div", {"styles": this.form.css.logMediaOpinionContentArea}).inject(imgNode);

                // var height = 260;
                // var width = 390;
                var width;
                //var height;
                if (layout.mobile){
                    width = node.getParent().getParent().getSize().x-2;
                    //height = width*2/3;
                }else{
                    var pNode = node.getParent().getParent();
                    var offset = imgNode.getPosition( pNode );
                    width = Math.min( pNode.getSize().x - offset.x - 42, 800 );
                }

                var img = new Element("img", {
                    "src": url,
                    "styles" : { width : width+"px" },
                    "events" : {
                        load : function(ev){
                            var nh = ev.target.naturalHeight;
                            var nw = ev.target.naturalWidth;
                            if( !layout.mobile && ( this.isNumber( this.json.handwritingWidth ) || this.isNumber( this.json.handwritingHeight ) ) ){
                                var size = this.getImageSize( nw, nh );
                                img.setStyles(size);
                                imgNode.setStyles(size);
                            }else{
                                var x = Math.min(nw, width);
                                img.setStyles({"width": ""+ x +"px"});
                                imgNode.setStyles({"width": ""+ x +"px"});
                            }
                        }.bind(this)
                    }
                }).inject(imgAreaNode);

                //img.setStyles({"width": ""+width+"px"});
                //imgNode.setStyles({"width": ""+width+"px"});

                //img.setStyles({"width": ""+width+"px", "height": ""+height+"px"});
                //imgNode.setStyles({"width": ""+width+"px", "height": ""+height+"px"});


                // var size = img.getSize();
                // var x_y = size.x/size.y;
                //if (size.y>260){

                //}

                // var p = iconNode.getPosition(this.form.app.content);
                // var s = iconNode.getSize();
                // var size = (layout.mobile) ? {"x": width, "y": height}: imgNode.getSize();
                // var contentSize = this.form.app.content.getSize();
                // var contentScroll = (layout.mobile) ? document.body.getFirst().getScroll() : {"x": 0, "y": 0};
                // var y = p.y-size.y;
                // var x = p.x+s.x/2-size.x/2;
                //
                // if (x<10) x = 10;
                // if (x+size.x>contentSize.x-10) x = contentSize.x-size.x-20;
                // if (y+size.y>contentSize.y-10) y = contentSize.y-size.y-20;
                // if (y<10) y = 10;
                // y=y+contentScroll.y;
                //
                // if (layout.mobile){
                //     x = 0;
                // }
                // imgNode.setStyles({
                //     "top": ""+y+"px",
                //     "left": ""+x+"px"
                // });



                // this.hideMediaOpinionNodeFun = this.hideMediaOpinionNode.bind(this);
                // this.form.node.addEvent("mousedown", this.hideMediaOpinionNodeFun);

                e.stopPropagation();
            }.bind(this)
        });
    },
    hideMediaOpinionNode: function(){
        if (this.mediaOpinionContentNode){
            this.mediaOpinionContentNode.destroy();
            this.mediaOpinionContentNode = null;
        }
        // if (this.hideMediaOpinionNodeFun) this.form.node.removeEvent("click", this.hideMediaOpinionNodeFun);
    },
    loadMediaOpinion_video: function(att, node){
        var iconNode = new Element("div", {"styles": this.form.css.logMediaIcon}).inject(node.getParent());
        iconNode.setStyle("background-image", "url('"+this.form.path+this.form.options.style+"/icon/video.png')");
        iconNode.set("title", MWF.xApplication.process.Xform.LP.mediaOpinion_video);
    },
    loadMediaOpinion_voice: function(att, node){
        // this.form.css.logMediaIcon.width = "60px";
        // var iconNode = new Element("audio", {
        //     "styles": this.form.css.logMediaIcon,
        //     "controls": true,
        //     "html": "<source src='"+url+"' type='audio/wav'></source>"
        // }).inject(node.getParent());

        var iconNode = new Element("div", {"styles": this.form.css.logMediaIcon}).inject(node.getParent());
        iconNode.setStyle("background-image", "url('"+this.form.path+this.form.options.style+"/icon/voice.png')");
        iconNode.set("title", MWF.xApplication.process.Xform.LP.mediaOpinion_voice);

        iconNode.addEvents({
            "click": function(e){
                var url = this.getMediaOpinionUrl(att);

                this.audioNode = new Element("audio", {"loop": false});
                this.audioNode.set("src", url);
                this.audioNode.play();

                e.stopPropagation();
            }.bind(this)
        });
    }
});
