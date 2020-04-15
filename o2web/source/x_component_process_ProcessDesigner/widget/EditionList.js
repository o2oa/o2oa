MWF.xApplication.process.ProcessDesigner.widget = MWF.xApplication.process.ProcessDesigner.widget || {};
MWF.xApplication.process.ProcessDesigner.widget.EditionList = new Class({
	Implements: [Options, Events],
	Extends: MWF.widget.Common,
	options: {
		"style": "default"
	},
	initialize: function(application, edition, process, options){
		this.setOptions(options);
		this.application = application;
        this.edition = edition;
        this.process = process;
		this.path = "/x_component_process_ProcessDesigner/widget/$EditionList/";
		this.cssPath = "/x_component_process_ProcessDesigner/widget/$EditionList/"+this.options.style+"/css.wcss";
		this._loadCss();
        this.currentItem = null;
        this.items = [];
        this.lp = MWF.xApplication.process.ProcessDesigner.LP;
	},
    load: function(){
        o2.Actions.load("x_processplatform_assemble_designer").ProcessAction.listEdition(this.application, this.edition, function(json){
            this.editionList = json.data;
            this.listEditionDlg();
        }.bind(this));

        this.node = new Element("div", {"styles": this.css.node});
        this.leftNode = new Element("div", {"styles": this.css.leftNode}).inject(this.node);
        this.rightNode = new Element("div", {"styles": this.css.rightNode}).inject(this.node);

        this.listNode = new Element("div", {"styles": this.css.listNode}).inject(this.leftNode);

        this.resizeNode = new Element("div", {"styles": this.css.resizeNode}).inject(this.rightNode);
        this.diffNode = new Element("div", {"styles": this.css.diffNode}).inject(this.rightNode);

        this.createListTable();
        this.show();
    },
    createListTable: function(){
        var tableHtml = "<table width='100%' cellspacing='0' cellpadding='3'><tr>" +
            "<th></th>" +
            "<th>"+this.lp.edition_list.number+"</th>" +
            "<th>"+this.lp.edition_list.update+"</th>" +
            "<th>"+this.lp.edition_list.updatePerson+"</th>" +
            "<th>"+this.lp.edition_list.enabled+"</th>" +
            "<th>"+this.lp.edition_list.description+"</th>" +
            "<th>"+this.lp.edition_list.action+"</th>" +
            "</tr></table>";
        this.listNode.set("html", tableHtml);
        this.listTable = this.listNode.getElement("table");
        this.listTable.setStyles(this.css.listTable);
        var ths = this.listNode.getElements("th").setStyles(this.css.listTable_th);
        ths[ths.length-1].setStyles(this.css.listTable_td_right);
    },
    reloadList: function(){
	    debugger;
        this.items = [];
        this.listNode.empty();
        this.diffNode.empty();

        this.createListTable();
        o2.Actions.load("x_processplatform_assemble_designer").ProcessAction.listEdition(this.application, this.edition, function(json){
            this.editionList = json.data;
            this.listEditionDlg();
        }.bind(this));
    },
    show: function(){
	    if (!this.dlg){
	        this.dlg = o2.DL.open({
                "title": this.lp.edition_list.editionList,
                "content": this.node,
                "offset": {"y": -100},
                "isMax": true,
                "width": 900,
                "height": 500,
                "buttonList": [
                    {
                        "text": this.lp.edition_list.open,
                        "action": function(){ this.openCurrentEdition(); this.dlg.close();}.bind(this),
                        "title": this.lp.edition_list.openInfor
                    },
                    {
                        "text": MWF.xApplication.process.ProcessDesigner.LP.close,
                        "action": function(){ this.close(); }
                    }
                ],
                "onPostShow": function(){
                    this.setEvent();
                }.bind(this)
            });
        }
    },
    listEditionDlg: function(){
        this.editionList.each(function(edition){
            var item = new MWF.xApplication.process.ProcessDesigner.widget.EditionList.Item(this, edition);
            this.items.push(item);
        }.bind(this));
    },
    setEvent: function(){
        var buttons = this.dlg.button.getElements("input");
	    this.openCurrentEditionButton = buttons[0];
	    var display = this.openCurrentEditionButton.getStyle("display");
        this.openCurrentEditionButton.store("dsp", display);
        this.openCurrentEditionButton.setStyle("display", "none");
        var size, leftSize;
	    var drag = new Drag(this.resizeNode, {
            "onSnap": function(el){
                el.setStyle("background", "#cccccc");
            },
            "onStart": function(el, e){
                size = this.node.getSize();
                leftSize = this.leftNode.getSize();
                drag.x = e.event.x;
            }.bind(this),
            "onComplete": function(el){
                el.setStyle("background-color", "transparent");
            },
	        "onDrag": function(el, e){
                var x = drag.x - e.event.x;
                var w = leftSize.x-x;
                w = (w/size.x)*100;
                if (w<30) w = 30;
                if (w>70) w = 70;
                this.leftNode.setStyle("width", ""+w+"%");
                w = 100-w;
                this.rightNode.setStyle("width", ""+w+"%");
            }.bind(this)
        });
    },
    openCurrentEdition: function(){
	    if (this.currentItem && this.currentItem.edition.fullProcess.id != this.process.process.id){
	        this.process.save(function(){
                this.process.reload(this.currentItem.edition.fullProcess);
            }.bind(this));
        }
    },
    checkButtonDisable: function(){
	    if (this.currentItem && this.currentItem.edition.fullProcess.id != this.process.process.id){
            this.openCurrentEditionButton.setStyle("display", this.openCurrentEditionButton.retrieve("dsp"));
        }else{
            this.openCurrentEditionButton.setStyle("display", "none");
        }
    }
});

MWF.xApplication.process.ProcessDesigner.widget.EditionList.Item = new Class({
    initialize: function(list, edition){
        this.list = list;
        this.edition = edition;
        this.table = this.list.listTable;
        this.css = this.list.css;
        this.lp = this.list.lp;
        this.isCurrentEdition = (this.list.process.process.id == this.edition.id);
        this.load();
    },
    load: function(){
        this.node = new Element("tr").inject(this.table);
        var html = "<td></td>" +
            "<td>"+this.edition.editionNumber+"</td>" +
            "<td>"+this.edition.updateTime+"</td>" +
            "<td>"+o2.name.cn(this.edition.lastUpdatePerson)+"</td>" +
            "<td>"+(this.edition.editionEnable ? this.lp.edition_list.yes : this.lp.edition_list.no)+"</td>"+
            "<td>"+(this.edition.editionDes || "")+"</td>"+
            "<td></td>";
        this.node.set("html", html);

        var tds = this.node.getElements("td").setStyles((this.isCurrentEdition) ? this.css.listTable_td_current : this.css.listTable_td);
        tds[tds.length-1].setStyles(this.css.listTable_td_right);
        this.iconTd = tds[0].setStyles(this.css.listTable_td_icon);
        this.selectIconNode = new Element("div", {"styles": this.css.unselectIcon}).inject(this.iconTd);

        this.actionTd = tds[tds.length-1].setStyles(this.css.listTable_td_action);
        this.createActions();

        this.setEvent();
    },
    createActions: function(){
        if (!this.edition.editionEnable){
            this.enableAction = new Element("div.mainColor_bg", {"styles": this.css.enableAction, "text": this.lp.edition_list.enable}).inject(this.actionTd);
            var text = this.lp.edition_list.enableInfor.replace(/{v}/, this.edition.editionNumber);
            this.enableAction.set("title", text);
        }
        if (!this.isCurrentEdition && !this.edition.editionEnable){
            this.delAction = new Element("div", {"styles": this.css.delAction, "text": this.lp.edition_list.del}).inject(this.actionTd);
            text = this.lp.edition_list.delInfor.replace(/{v}/, this.edition.editionNumber);
            this.delAction.set("title", text);
        }
    },
    setEvent: function(){
        this.node.addEvent("click", function(){
            this.selected();
        }.bind(this));

        if (this.enableAction) this.enableAction.addEvents({
            "click": function(e){ this.enable(e); e.stopPropagation();}.bind(this),
        });

        if (this.delAction) this.delAction.addEvents({
            "click": function(e){ this.del(e); e.stopPropagation();}.bind(this),
        });
    },
    enable: function(e){
        var actions = o2.Actions.load("x_processplatform_assemble_designer").ProcessAction;
        var _self = this;
        this.list.process.designer.confirm("infor", e, this.lp.edition_list.enabledProcessTitle, {"html": this.lp.edition_list.enabledProcessInfor}, 600, 120, function(){
            _self.list.process.save(function(){
                actions.enableProcess(this.edition.id, function(json){
                    this.list.reloadList();
                    actions.get(this.list.process.process.id, function(processJson){
                        this.list.process.reload(processJson.data);
                    }.bind(this));
                }.bind(this));
            }.bind(_self));
            this.close();
        },function(){this.close();})
    },
    del: function(e){
        var _self = this;
        var infor = this.lp.edition_list.deleteEditionInfor.replace(/{v}/g, this.edition.editionNumber);
        this.list.process.designer.confirm("warn", e, this.lp.edition_list.deleteEditionTitle, infor, 460, 120, function(){
            _self.deleteEdition();
            this.close();
        }, function(){
            this.close();
        });
    },
    deleteEdition: function(callback){
        o2.Actions.load("x_processplatform_assemble_designer").ProcessAction["delete"](this.edition.id, "true", function(){
            this.unSelected();
            this.node.destroy();
            if (callback) callback();
        }.bind(this));
    },
    selected: function(){
        if (this.list.currentItem) this.list.currentItem.unSelected();
        this.node.setStyles(this.css.itemTr_selected).addClass("lightColor_bg");
        this.selectIconNode.setStyles(this.css.selectIcon).addClass("mainColor_bg");
        this.list.currentItem = this;
        this.checkDiff();
        this.list.checkButtonDisable();
    },
    unSelected: function(){
        this.node.setStyles(this.css.itemTr).removeClass("lightColor_bg");
        this.selectIconNode.setStyles(this.css.unselectIcon).removeClass("mainColor_bg");
        this.list.currentItem = null;
        this.list.diffNode.empty();
    },
    checkDiff: function(){
        this.getFullProcess(function(){
            var prevItem  = this.getPrevItem();
            if (prevItem){
                prevItem.getFullProcess();
                var diffs = this.getDiffWithProcess(prevItem.edition.fullProcess);
                if (diffs.length){
                    this.appendDiffLine(this.lp.edition_list.hasDiffs);
                    //for (var i=0; i<10; i++){
                        diffs.each(function(v){
                            this.appendDiffLine(v);
                        }.bind(this));
                    //}
                }else{
                    this.appendDiffLine(this.lp.edition_list.noDiffs);
                }
            }else{
                this.appendDiffLine(this.lp.edition_list.newProcess);
                this.appendDiffLine(this.getNewProcessInfor());
            }
        }.bind(this));

    },
    appendDiffLine: function(text){
        new Element("div", {"styles": this.css.diffLine, "html": text}).inject(this.list.diffNode);
    },
    getDiffWithProcess: function(process){
        debugger;
        var diffs = [];
        var notDiffFields = ["id", "editionName", "editionEnable", "editionNumber", "createTime", "updateTime", "creatorPerson", "lastUpdateTime", "lastUpdatePerson"];
        Object.each(process, function(v, k){
            var t = o2.typeOf(v);
            if (t!="array" && t!="object"){
                if (this.edition.fullProcess[k]!=v){
                    if (notDiffFields.indexOf(k)==-1){
                        var infor = this.lp.edition_list.modifyProcess;
                        var oldV = (v.length>60) ? v.substring(0,60)+" ..." : v;
                        var newV = (this.edition.fullProcess[k].length>60) ? this.edition.fullProcess[k].substring(0,60)+" ..." : this.edition.fullProcess[k];
                        infor = infor.replace(/\{field\}/, k).replace(/\{old\}/, oldV).replace(/\{new\}/, newV);
                        diffs.push(infor);
                    }
                }
            }
        }.bind(this));

        diffs = diffs.concat(this.getDiffActivityListCount(process, diffs));

        return diffs;
    },
    getDiffActivityListCount: function(process){
        var diffs = [];
        diffs = diffs.concat(this.getDiffActivityCount(process.endList, this.edition.fullProcess.endList));
        diffs = diffs.concat(this.getDiffActivityCount(process.manualList, this.edition.fullProcess.manualList));
        return diffs;
    },
    getDiffActivityCount: function(prevList, currentList){
        var diffs = [];
        var prevNames = prevList.map(function(item){ return item.name; });
        var currentNames = currentList.map(function(item){ return item.name; });

        var deleteNames = prevNames.filter(function(name){
            var i = currentNames.indexOf(name);
            if (i!=-1){
                currentNames.splice(i, 1);
                return false;
            }
            return true;
        });
        currentNames.each(function(name){
            var infor = this.lp.edition_list.addActivity;
            infor = infor.replace(/\{name\}/, name);
            diffs.push(infor);
        }.bind(this));
        deleteNames.each(function(name){
            var infor = this.lp.edition_list.deleteActivity;
            infor = infor.replace(/\{name\}/, name);
            diffs.push(infor);
        }.bind(this));
        return diffs;
    },

    getNewProcessInfor: function(){
        //this.getFullProcess(function(){
            var process = this.edition.fullProcess;
            var activityInfor = "";
            var an = this.lp.edition_list.an;
            activityInfor = "1 "+an+this.lp.menu.newActivityType.begin;
            if (process.endList && process.endList.length) activityInfor += ", "+process.endList.length + " "+an +this.lp.menu.newActivityType.end;
            if (process.agentList && process.agentList.length) activityInfor += ", "+process.agentList.length + " "+an +this.lp.menu.newActivityType.agent;
            if (process.manualList && process.manualList.length) activityInfor += ", "+process.manualList.length + " "+an +this.lp.menu.newActivityType.manual;
            if (process.conditionList && process.conditionList.length) activityInfor += ", "+process.conditionList.length + " "+an +this.lp.menu.newActivityType.condition;
            if (process.choiceList && process.choiceList.length) activityInfor += ", "+process.choiceList.length + " "+an +this.lp.menu.newActivityType.choice;
            if (process.parallelList && process.parallelList.length) activityInfor += ", "+process.parallelList.length + " "+an +this.lp.menu.newActivityType.parallel;
            if (process.splitList && process.splitList.length) activityInfor += ", "+process.splitList.length + " "+an +this.lp.menu.newActivityType.split;
            if (process.mergeList && process.mergeList.length) activityInfor += ", "+process.mergeList.length + " "+an +this.lp.menu.newActivityType.merge;
            if (process.embedList && process.embedList.length) activityInfor += ", "+process.embedList.length + " "+an +this.lp.menu.newActivityType.embed;
            if (process.invokeList && process.invokeList.length) activityInfor += ", "+process.invokeList.length + " "+an +this.lp.menu.newActivityType.invoke;
            if (process.cancelList && process.cancelList.length) activityInfor += ", "+process.cancelList.length + " "+an +this.lp.menu.newActivityType.cancel;
            if (process.delayList && process.delayList.length) activityInfor += ", "+process.delayList.length + " "+an +this.lp.menu.newActivityType.delay;
            if (process.messageList && process.messageList.length) activityInfor += ", "+process.messageList.length + " "+an +this.lp.menu.newActivityType.message;
            if (process.serviceList && process.serviceList.length) activityInfor += ", "+process.serviceList.length + " "+an +this.lp.menu.newActivityType.service;

            return activityInfor;
        //}.bind(this));
    },
    getFullProcess: function(callback, async){
        if (this.edition.fullProcess){
            if (callback) callback();
        }else{
            var asyncGet = !!async;
            o2.Actions.load("x_processplatform_assemble_designer").ProcessAction.get(this.edition.id, function(json){
                this.edition.fullProcess = json.data;
                if (callback) callback();
            }.bind(this), null, asyncGet);
        }
    },
    getPrevItem: function(){
        var idx = this.list.items.indexOf(this);
        idx++;
        if (idx<this.list.items.length) return this.list.items[idx];
        return null;
    }
});