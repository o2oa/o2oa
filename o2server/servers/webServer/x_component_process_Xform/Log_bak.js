MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Log_bak = MWF.APPLog =  new Class({
	Extends: MWF.APP$Module,

	_loadUserInterface: function(){
		this.node.empty();
        this.node.setStyle("-webkit-user-select", "text");
        if (this.form.businessData){
            if (this.form.businessData.workLogList){
                this.workLog = this.form.businessData.workLogList;
                this.loadWorkLog();
            }
        }
	},

    loadWorkLog: function(){
        if (this.json.mode=="table"){
            this.loadWorkLogTable();
        }else if (this.json.mode=="text"){
            this.loadWorkLogText();
        }else{
            this.loadWorkLogDefault();
        }
    },
    loadWorkLogTable: function(){
        this.table = new Element("table", {
            "styles": this.form.css.logTable,
            "border": "0",
            "cellSpacing": "0",
            "cellpadding": "3px",
            "width": "100%"
        }).inject(this.node);

        this.workLog.each(function(log, idx){
            this.loadWorkLogLine_table(log, idx);
        }.bind(this));
    },
    loadWorkLogLine_table: function(log, idx){
        var tr = this.table.insertRow((this.table.rows) ? this.table.rows.length : 0).setStyles(this.form.css.logTableTr);
        var iconTd = tr.insertCell(0).setStyles(this.form.css.logTableIconTd);
        var activityTd = tr.insertCell(1).setStyles(this.form.css.logTableActivityTd);
        var taskTd = tr.insertCell(2).setStyles(this.form.css.logTableTaskTd);

        if (log.connected){
            iconTd.setStyle("background-image", "url("+"/x_component_process_Xform/$Form/"+this.form.options.style+"/icon/ok14.png)");
        }else{
            iconTd.setStyle("background-image", "url("+"/x_component_process_Xform/$Form/"+this.form.options.style+"/icon/rightRed.png)");
        }

        var avtivityNode = new Element("div", {"styles": this.form.css.logTableActivityNode}).inject(activityTd);
        //var fromAvtivityNode = new Element("div", {"styles": this.form.css.logTableActivityFromNode}).inject(tmpDiv);
        //var arrowNode = new Element("div", {"styles": this.form.css.logTableActivityArrowNode}).inject(tmpDiv);
        //var arrivedAvtivityNode = new Element("div", {"styles": this.form.css.logTableActivityArrivedNode}).inject(tmpDiv);

        var timeNode = new Element("div", {"styles": this.form.css.logTableActivityTimeNode}).inject(activityTd);

        if (log.arrivedActivityName){
            var html = "<b>"+log.fromActivityName+"</b> -> <b>"+log.arrivedActivityName+"</b>";
            avtivityNode.set("html", html);

            timeNode.set("html", "<b>"+MWF.xApplication.process.Xform.LP.begin+": </b>"+log.fromTime+"<br/><b>"+MWF.xApplication.process.Xform.LP.end+": </b>"+log.arrivedTime)

        }else{
            var html = "<b>"+log.fromActivityName+"</b>";
            avtivityNode.set("html", html);
            timeNode.set("html", "<b>"+MWF.xApplication.process.Xform.LP.begin+": </b>"+log.fromTime)
        }

        if (log.taskCompletedList.length || (this.json.isTask!="false" && log.taskList.length)){
            var taskTable = new Element("table", {
                "styles": this.form.css.logTableTask,
                "border": "0",
                "cellSpacing": "0",
                "cellpadding": "3px",
                "width": "100%"
            }).inject(taskTd);
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
                this.loadTaskLine_table(taskCompleted, taskTable, log, false);
            }.bind(this));

            if (this.json.isTask!="false"){
                log.taskList.each(function(task){
                    this.loadTaskLine_table(task, taskTable, log, true);
                }.bind(this));
            }
        }

    },
    loadTaskLine_table: function(task, table, log, isTask){
        var style = "logTableTaskLine";
        if (isTask) style = "logTableTaskLine_task";
        var tr = table.insertRow(table.rows.length);
        var td = tr.insertCell(0).setStyles(this.form.css[style]);
        td.set("text", task.person || "");
        td = tr.insertCell(1).setStyles(this.form.css[style]);
        td.set("text", task.department || "");
        td = tr.insertCell(2).setStyles(this.form.css[style]);
        td.set("text", task.startTime || "");
        td = tr.insertCell(3).setStyles(this.form.css[style]);
        td.set("text", task.completedTime || "");
        td = tr.insertCell(4).setStyles(this.form.css[style]);
        td.set("text", task.routeName || "");
        td = tr.insertCell(5).setStyles(this.form.css[style]);
        td.set("text", task.opinion || "");
    },



    loadWorkLogText: function(){
        this.lineClass = "logTaskNode";
        this.workLog.each(function(log, idx){
            this.loadWorkLogLine_text(log, idx);
        }.bind(this));
    },
    loadWorkLogLine_text: function(log, idx){
        log.taskCompletedList.each(function(taskCompleted){
            this.loadTaskLine_text(taskCompleted, this.node, log, false);
        }.bind(this));

        if (this.json.isTask!="false"){
            log.taskList.each(function(task){
                this.loadTaskLine_text(task, this.node, log, true);
            }.bind(this));
        }
    },
    loadTaskLine_text: function(task, node, log, isTask){
        this.loadTaskLine_default(task, node, log, isTask, "0px", true);
    },

    loadWorkLogDefault: function(){
        //var text = this.json.textStyle;
        this.workLog.each(function(log, idx){
            this.loadWorkLogLine_default(log, idx);
        }.bind(this));
    },
    loadWorkLogLine_default: function(log, idx){
        var logActivityNode = new Element("div", {"styles": this.form.css.logActivityNode}).inject(this.node);
        var titleNode = new Element("div", {"styles": this.form.css.logActivityTitleNode}).inject(logActivityNode);
        var childNode = new Element("div", {"styles": this.form.css.logActivityChildNode}).inject(logActivityNode);

        var iconNode = new Element("div", {"styles": this.form.css.logActivityIconNode}).inject(titleNode);
        var fromAvtivityNode = new Element("div", {"styles": this.form.css.logActivityFromNode}).inject(titleNode);
        var arrowNode = new Element("div", {"styles": this.form.css.logActivityArrowNode}).inject(titleNode);
        var arrivedAvtivityNode = new Element("div", {"styles": this.form.css.logActivityArrivedNode}).inject(titleNode);

        var timeNode = new Element("div", {"styles": this.form.css.logActivityTimeNode}).inject(titleNode);

        if (log.connected){
            iconNode.setStyle("background-image", "url("+"/x_component_process_Xform/$Form/"+this.form.options.style+"/icon/ok14.png)");
        }else{
            iconNode.setStyle("background-image", "url("+"/x_component_process_Xform/$Form/"+this.form.options.style+"/icon/rightRed.png)");
        }
        fromAvtivityNode.set("html", "<b>"+log.fromActivityName+"</b>");
        if (log.arrivedActivityName){
            arrowNode.setStyle("background-image", "url("+"/x_component_process_Xform/$Form/"+this.form.options.style+"/icon/right.png)");
            arrivedAvtivityNode.set("html", "<b>"+log.arrivedActivityName+"</b>");
            timeNode.set("html", "<b>"+MWF.xApplication.process.Xform.LP.begin+": </b>"+log.fromTime+"<br/><b>"+MWF.xApplication.process.Xform.LP.end+": </b>"+log.arrivedTime)

        }else{
            timeNode.set("html", "<b>"+MWF.xApplication.process.Xform.LP.begin+": </b>"+log.fromTime)
        }

        if ((idx % 2)==0){
            logActivityNode.setStyles(this.form.css.logActivityNode_even);
            titleNode.setStyles(this.form.css.logActivityTitleNode_even);
        }

        log.taskCompletedList.each(function(taskCompleted){
            this.loadTaskLine_default(taskCompleted, childNode, log, false);
        }.bind(this));

        if (this.json.isTask!="false"){
            log.taskList.each(function(task){
                this.loadTaskLine_default(task, childNode, log, true);
            }.bind(this));
        }
    },
    loadTaskLine_default: function(task, node, log, isTask, margin, isZebra){
        var logTaskNode = new Element("div", {"styles": this.form.css.logTaskNode}).inject(node);
        var iconNode = new Element("div", {"styles": this.form.css.logTaskIconNode}).inject(logTaskNode);
        var textNode = new Element("div", {"styles": this.form.css.logTaskTextNode}).inject(logTaskNode);

        if (isZebra){
            logTaskNode.setStyles(this.form.css[this.lineClass]);
            if (this.lineClass == "logTaskNode"){
                this.lineClass = "logTaskNode_even";
            }else{
                this.lineClass = "logTaskNode";
            }
        }

        if (margin) iconNode.setStyle("margin-left", margin);
        var left = iconNode.getStyle("margin-left").toInt();
        left = left + 28;
        textNode.setStyle("margin-left", ""+left+"px");

        //this.textStyle
        if (!isTask){
            var html = this.json.textStyle;
            html = html.replace(/\{person\}/g, task.person);
            html = html.replace(/\{department\}/g, task.department);
            html = html.replace(/\{route\}/g, task.routeName);
            html = html.replace(/\{time\}/g, task.completedTime);
            html = html.replace(/\{opinion\}/g, task.opinion);
            html = html.replace(/\{company\}/g, task.company);
            html = html.replace(/\{startTime\}/g, task.startTime);
            html = html.replace(/\{activity\}/g, log.fromActivityName);
            html = html.replace(/\{arrivedActivity\}/g, task.arrivedActivityName);
            //var html = MWF.xApplication.process.Xform.LP.nextUser + task.person+"("+task.department+")" +", "+
            //    MWF.xApplication.process.Xform.LP.selectRoute + ": [" + task.routeName + "], " +
            //    MWF.xApplication.process.Xform.LP.submitAt + ": " + task.completedTime+ ", " +
            //    MWF.xApplication.process.Xform.LP.idea + ": <font style=\"color: #00F\">" + (task.opinion || "")+"</font>";
            textNode.set("html", html);
        }else{
            var html = task.person+"("+task.department+")" + MWF.xApplication.process.Xform.LP.processing+", "+
                MWF.xApplication.process.Xform.LP.comeTime + ": " + task.startTime;
            textNode.set("html", html);
            iconNode.setStyle("background-image", "url("+"/x_component_process_Xform/$Form/"+this.form.options.style+"/icon/rightRed.png)");
        }

    }
}); 