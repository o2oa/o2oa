MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Log = MWF.APPLog =  new Class({
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
        if (this.json.mode==="table"){
            this.loadWorkLogTable();
        }else if (this.json.mode==="text"){
            this.loadWorkLogText();
        }else{
            this.loadWorkLogDefault();
        }
    },
    loadWorkLogTable: function(){
        this.workLog.each(function(log, idx){
            if (this.checkShow(log)) this.loadWorkLogLine_table(log, idx);
        }.bind(this));
    },
    loadWorkLogLine_table: function(log, idx){
        if (log.taskCompletedList.length || (this.json.isTask && log.taskList.length)){
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
        td.set("text", task.routeName || "");
        td = tr.insertCell(5).setStyles(this.form.css[style]);
        td.set("text", task.opinion || "");

    },



    loadWorkLogText: function(){
        this.lineClass = "logTaskNode";
        this.workLog.each(function(log, idx){
            if (this.checkShow(log)) this.loadWorkLogLine_text(log, idx);
        }.bind(this));
    },
    loadWorkLogLine_text: function(log, idx){
        log.taskCompletedList.each(function(taskCompleted){
            if (this.checkListShow(log, taskCompleted)) this.loadTaskLine_text(taskCompleted, this.node, log, false);
        }.bind(this));

        if (this.json.isTask){
            log.taskList.each(function(task){
                if (this.checkListShow(log, task)) this.loadTaskLine_text(task, this.node, log, true);
            }.bind(this));
        }
    },
    loadTaskLine_text: function(task, node, log, isTask){
        this.loadTaskLine_default(task, node, log, isTask, "0px", false);
    },

    checkShow: function(log){
        var flag = true;
        if (this.json.filterScript && this.json.filterScript.code){
            this.form.Macro.environment.log = log;
            this.form.Macro.environment.list = null;
            flag = this.form.Macro.exec(this.json.filterScript.code, this);
        }else{
            if (this.json.filterActivity.length){
                filterActivitys = this.json.filterActivity;
                flag = (filterActivitys.indexOf(log.fromActivityName)!==-1);
            }
            if (this.json.filterActivityAlias){
                if (this.json.filterActivityAlias.length){
                    filterActivityAlias = this.json.filterActivityAlias;
                    flag = ((log.fromActivityAlias) && filterActivityAlias.indexOf(log.fromActivityAlias)!==-1);
                }
            }
            if (this.json.filterPerson.length){
                flag = false;
                filterPersons = this.json.filterPerson;
                var tmpTaskCompletedList = [];
                log.taskCompletedList.each(function(taskCompleted){
                    if ((filterPersons.indexOf(taskCompleted.person)!==-1) || (filterPersons.indexOf(taskCompleted.identity)!==-1)){
                        tmpTaskCompletedList.push(taskCompleted);
                    }
                }.bind(this));
                if (tmpTaskCompletedList.length){
                    //log.taskCompletedList = [];
                    //log.taskCompletedList = tmpTaskCompletedList;
                    flag = true;
                }
            }
            if (this.json.filterRoute.length){
                filterRoutes = this.json.filterRoute;
                flag = (filterRoutes.indexOf(log.routeName)!==-1);
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
            if (this.json.filterPerson.length){
                flag = ((filterPersons.indexOf(list.person)!==-1)|| (filterPersons.indexOf(list.identity)!==-1));
            }
        }
        return flag;
    },

    loadWorkLogDefault: function(){
        //var text = this.json.textStyle;
        this.workLog.each(function(log, idx){
            if (this.checkShow(log)) this.loadWorkLogLine_default(log, idx);
        }.bind(this));
    },
    loadWorkLogLine_default: function(log, idx){
        if (log.taskCompletedList.length || (this.json.isTask && log.taskList.length)) {
            var logActivityNode = new Element("div", {"styles": this.form.css.logActivityNode}).inject(this.node);
            var titleNode = new Element("div", {"styles": this.form.css.logActivityTitleNode}).inject(logActivityNode);
            var childNode = new Element("div", {"styles": this.form.css.logActivityChildNode}).inject(logActivityNode);

            var iconNode = new Element("div", {"styles": this.form.css.logActivityIconNode}).inject(titleNode);
            var fromAvtivityNode = new Element("div", {"styles": this.form.css.logActivityFromNode}).inject(titleNode);
            var arrowNode = new Element("div", {"styles": this.form.css.logActivityArrowNode}).inject(titleNode);
            var arrivedAvtivityNode = new Element("div", {"styles": this.form.css.logActivityArrivedNode}).inject(titleNode);

            var timeNode = new Element("div", {"styles": this.form.css.logActivityTimeNode}).inject(titleNode);

            if (log.connected) {
                iconNode.setStyle("background-image", "url(/x_component_process_Xform/$Form/" + this.form.options.style + "/icon/ok14.png)");
            } else {
                iconNode.setStyle("background-image", "url(/x_component_process_Xform/$Form/" + this.form.options.style + "/icon/rightRed.png)");
            }
            fromAvtivityNode.set("html", "<b>" + log.fromActivityName + "</b>");
            if (log.arrivedActivityName) {
                arrowNode.setStyle("background-image", "url(/x_component_process_Xform/$Form/" + this.form.options.style + "/icon/right.png)");
                arrivedAvtivityNode.set("html", "<b>" + log.arrivedActivityName + "</b>");
                timeNode.set("html", "<b>" + MWF.xApplication.process.Xform.LP.begin + ": </b>" + log.fromTime + "<br/><b>" + MWF.xApplication.process.Xform.LP.end + ": </b>" + log.arrivedTime)

            } else {
                timeNode.set("html", "<b>" + MWF.xApplication.process.Xform.LP.begin + ": </b>" + log.fromTime)
            }

            if ((idx % 2) === 0) {
                logActivityNode.setStyles(this.form.css.logActivityNode_even);
                titleNode.setStyles(this.form.css.logActivityTitleNode_even);
            }

            log.taskCompletedList.each(function (taskCompleted) {
                if (this.checkListShow(log, taskCompleted)) this.loadTaskLine_default(taskCompleted, childNode, log, false);
            }.bind(this));

            if (this.json.isTask) {
                log.taskList.each(function (task) {
                    if (this.checkListShow(log, task)) this.loadTaskLine_default(task, childNode, log, true);
                }.bind(this));
            }
        }
    },
    loadTaskLine_default: function(task, node, log, isTask, margin, isZebra, nodeStyle){
        var style = "logTaskNode";
        if (nodeStyle) style = "logTaskTextNode";
        var logTaskNode = new Element("div", {"styles": this.form.css[style]}).inject(node);
        var iconNode = new Element("div", {"styles": this.form.css.logTaskIconNode}).inject(logTaskNode);
        var textNode = new Element("div", {"styles": this.form.css.logTaskTextNode}).inject(logTaskNode);

        if (isZebra){
            logTaskNode.setStyles(this.form.css[this.lineClass]);
            if (this.lineClass === "logTaskNode"){
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
        var html;
        var company = "";
        if (!isTask){
            company = (task.unitList) ? task.unitList[task.unitList.length-1] : "";
            html = this.json.textStyle;
            html = html.replace(/\{person\}/g, task.person.substring(0, task.person.indexOf("@")));
            html = html.replace(/\{department\}/g, task.unit.substring(0, task.unit.indexOf("@")));
            html = html.replace(/\{route\}/g, task.routeName);
            html = html.replace(/\{time\}/g, task.completedTime);
            html = html.replace(/\{date\}/g, new Date().parse(task.completedTime).format("Y-M-d"));
            html = html.replace(/\{opinion\}/g, task.opinion);
            html = html.replace(/\{company\}/g, company.substring(0, company.indexOf("@")));
            html = html.replace(/\{startTime\}/g, task.startTime);
            html = html.replace(/\{startDate\}/g, new Date().parse(task.startTime).format("Y-M-d"));
            html = html.replace(/\{activity\}/g, log.fromActivityName);
            html = html.replace(/\{arrivedActivity\}/g, task.arrivedActivityName);
            //var html = MWF.xApplication.process.Xform.LP.nextUser + task.person+"("+task.department+")" +", "+
            //    MWF.xApplication.process.Xform.LP.selectRoute + ": [" + task.routeName + "], " +
            //    MWF.xApplication.process.Xform.LP.submitAt + ": " + task.completedTime+ ", " +
            //    MWF.xApplication.process.Xform.LP.idea + ": <font style=\"color: #00F\">" + (task.opinion || "")+"</font>";
            textNode.set("html", html);
        }else{
            //company = task.unitList[task.unitList.length-1];
            html = task.person.substring(0, task.person.indexOf("@"))+"("+task.unit.substring(0, task.unit.indexOf("@"))+")" + MWF.xApplication.process.Xform.LP.processing+", "+
                MWF.xApplication.process.Xform.LP.comeTime + ": " + task.startTime;
            textNode.set("html", html);
            iconNode.setStyle("background-image", "url("+"/x_component_process_Xform/$Form/"+this.form.options.style+"/icon/rightRed.png)");
        }

    }
}); 