MWF.xDesktop.requireApp("process.ProcessManager", "Explorer", null, false);
MWF.xApplication.process.ProcessManager.ProcessExplorer = new Class({
	Extends: MWF.xApplication.process.ProcessManager.Explorer,
	Implements: [Options, Events],

    keyCopy: function(e){
        if (this.selectMarkItems.length){
            var items = [];
            var i = 0;

            var checkItems = function(e){
                if (i>=this.selectMarkItems.length){
                    if (items.length){
                        var str = JSON.encode(items);
                        if (e){
                            e.clipboardData.setData('text/plain', str);
                        }else {
                            window.clipboardData.setData("Text", str);
                        }
                        this.app.notice(this.app.lp.copyed, "success");
                    }
                }
            }.bind(this);

            this.selectMarkItems.each(function(item){
                this.app.restActions.getProcess(item.data.id, function(json){
                    json.data.elementType = "process";
                    items.push(json.data);
                    i++;
                    checkItems(e);
                }.bind(this), null, false)
            }.bind(this));

            if (e) e.preventDefault();
        }
    },
    keyPaste: function(e){
        var dataStr = "";
        if (e){
            dataStr = e.clipboardData.getData('text/plain');
        }else{
            dataStr = window.clipboardData.getData("Text");
        }
        var data = JSON.decode(dataStr);
        this.pasteItem(data, 0);

        // data.each(function(item){
        //     if (item.elementType==="process"){
        //         this.saveItemAs(this.app.options.application, item);
        //     }
        // }.bind(this));
    },
    pasteItem: function(data, i){
        if (i<data.length){
            var item = data[i];
            if (item.elementType==="process"){
                this.saveItemAs(item, function(){
                    i++;
                    this.pasteItem(data, i);
                }.bind(this), function(){
                    i++;
                    this.pasteItem(data, i);
                }.bind(this), function(){
                    this.reload();
                }.bind(this));
            }else{
                i++;
                this.pasteItem(data, i);
            }
        }else{
            this.reload();
        }
    },

    saveItemAs: function(data, success, failure, cancel){
        this.app.restActions.listProcess(this.app.options.application.id, function(dJson){
            var i=1;
            var someItems = dJson.data.filter(function(d){ return d.id===data.id });
            if (someItems.length){
                var someItem = someItems[0];
                var lp = this.app.lp;
                var _self = this;

                var d1 = new Date().parse(data.lastUpdateTime);
                var d2 = new Date().parse(someItem.lastUpdateTime);
                var html = "<div>"+lp.copyConfirmInfor+"</div>";
                html += "<div style='overflow: hidden; margin: 10px 0px; padding: 5px 10px; background-color: #ffffff; border-radius: 6px;'><div style='font-weight: bold; font-size:14px;'>"+lp.copySource+" "+someItem.name+"</div>";
                html += "<div style='font-size:12px; color: #666666; float: left'>"+someItem.lastUpdateTime+"</div>" +
                    "<div style='font-size:12px; color: #666666; float: left; margin-left: 20px;'>"+MWF.name.cn(someItem.lastUpdatePerson)+"</div>" +
                    "<div style='color: red; float: right;'>"+((d1>=d2) ? "": lp.copynew)+"</div></div>";
                html += "<div style='overflow: hidden; margin: 10px 0px; padding: 5px 10px; background-color: #ffffff; border-radius: 6px;'><div style='clear: both;font-weight: bold; font-size:14px;'>"+lp.copyTarget+" "+data.name+"</div>";
                html += "<div style='font-size:12px; color: #666666; float: left;'>"+data.lastUpdateTime+"</div>" +
                    "<div style='font-size:12px; color: #666666; float: left; margin-left: 20px;'>"+MWF.name.cn(data.lastUpdatePerson)+"</div>" +
                    "<div style='color: red; float: right;'>"+((d1<=d2) ? "": lp.copynew)+"</div></div>";
//                html += "<>"
                this.app.dlg("inofr", null, this.app.lp.copyConfirmTitle, {"html": html}, 500, 290, [
                    {
                        "text": lp.copyConfirm_overwrite,
                        "action": function(){_self.saveItemAsUpdate(someItem, data, success, failure);this.close();}
                    },
                    {
                        "text": lp.copyConfirm_new,
                        "action": function(){_self.saveItemAsNew(dJson, data, success, failure);this.close();}
                    },
                    {
                        "text": lp.copyConfirm_skip,
                        "action": function(){/*nothing*/ this.close(); if (success) success();}
                    },
                    {
                        "text": lp.copyConfirm_cancel,
                        "action": function(){this.close(); if (cancel) cancel();}
                    }
                ]);
            }else{
                this.saveItemAsNew(dJson, data, success, failure)
            }
        }.bind(this), function(){if (failure) failure();}.bind(this));
    },
    saveItemAsUpdate: function(someItem, process, success, failure){
        process.id = someItem.id;
        process.name = someItem.name;
        process.alias = someItem.alias;
        process.application = someItem.application;
        process.applicationName = someItem.applicationName;
        process.isNewProcess = false;

        if (process.begin) process.begin.process = process.id;
        if (process.endList) process.endList.each(function(a){a.process = process.id;});
        if (process.agentList) process.agentList.each(function(a){a.process = process.id;});
        if (process.manualList) process.manualList.each(function(a){a.process = process.id;});
        if (process.conditionList) process.conditionList.each(function(a){a.process = process.id;});
        if (process.choiceList) process.choiceList.each(function(a){a.process = process.id;});
        if (process.parallelList) process.parallelList.each(function(a){a.process = process.id;});
        if (process.splitList) process.splitList.each(function(a){a.process = process.id;});
        if (process.mergeList) process.mergeList.each(function(a){a.process = process.id;});
        if (process.embedList) process.embedList.each(function(a){a.process = process.id;});
        if (process.invokeList) process.invokeList.each(function(a){a.process = process.id;});
        if (process.cancelList) process.cancelList.each(function(a){a.process = process.id;});
        if (process.delayList) process.delayList.each(function(a){a.process = process.id;});
        if (process.messageList) process.messageList.each(function(a){a.process = process.id;});
        if (process.serviceList) process.serviceList.each(function(a){a.process = process.id;});
        if (process.routeList) process.routeList.each(function(a){a.process = process.id;});

        this.app.restActions.saveProcess(process, function(){
            if (success) success();
        }.bind(this), function(){
            if (failure) failure();
        }.bind(this));
    },
    saveItemAsNew: function(processJson, process, success, failure){
	    debugger;
        var item = this.app.options.application;
        var id = item.id;
        var name = item.name;

        process.alias = "";
        var oldName = process.name;

        var i=1;
        while (processJson.data.some(function(d){ return d.name==process.name })){
            process.name = oldName+"_copy"+i;
            i++;
        }
        process.application = id;
        process.applicationName = name;

        var oldIds = [];
        oldIds.push(process.id);
        if (process.begin) oldIds.push(process.begin.id);
        if (process.endList) process.endList.each(function(a){oldIds.push(a.id);});
        if (process.agentList) process.agentList.each(function(a){oldIds.push(a.id);});
        if (process.manualList) process.manualList.each(function(a){oldIds.push(a.id);});
        if (process.conditionList) process.conditionList.each(function(a){oldIds.push(a.id);});
        if (process.choiceList) process.choiceList.each(function(a){oldIds.push(a.id);});
        if (process.parallelList) process.parallelList.each(function(a){oldIds.push(a.id);});
        if (process.splitList) process.splitList.each(function(a){oldIds.push(a.id);});
        if (process.mergeList) process.mergeList.each(function(a){oldIds.push(a.id);});
        if (process.embedList) process.embedList.each(function(a){oldIds.push(a.id);});
        if (process.invokeList) process.invokeList.each(function(a){oldIds.push(a.id);});
        if (process.cancelList) process.cancelList.each(function(a){oldIds.push(a.id);});
        if (process.delayList) process.delayList.each(function(a){oldIds.push(a.id);});
        if (process.messageList) process.messageList.each(function(a){oldIds.push(a.id);});
        if (process.serviceList) process.serviceList.each(function(a){oldIds.push(a.id);});
        if (process.routeList) process.routeList.each(function(a){oldIds.push(a.id);});

        this.app.restActions.getId(oldIds.length, function(ids) {
            var checkUUIDs = ids.data;
            var processStr = JSON.encode(process);
            oldIds.each(function(oid, i){
                var reg = new RegExp(oid, "ig");
                processStr = processStr.replace(reg, checkUUIDs[i].id);
            }.bind(this));

            process = JSON.decode(processStr);
            process.isNewProcess = true;
            this.app.restActions.saveProcess(process, function(){
                if (success) success();
            }.bind(this), function(){
                if (failure) failure();
            }.bind(this));

        }.bind(this));
    },

    // saveItemAs: function(item, process){
    //     var id = item.id;
    //     var name = item.name;
    //
    //     process.alias = "";
    //     var oldName = process.name;
    //     this.app.restActions.listProcess(id, function(processJson){
    //
    //     }.bind(this));
    // },

    _createElement: function(e){
        var createProcess = function(e, template){
            var options = {
                "template": template,
                "onQueryLoad": function(){
                    this.actions = _self.app.restActions;
                    this.application = _self.app.options.application;
                }
            };
            layout.desktop.openApplication(e, "process.ProcessDesigner", options);
        };

        var createTemplateMaskNode = new Element("div", {"styles": this.css.createTemplateMaskNode}).inject(this.app.content);
        var createTemplateAreaNode = new Element("div", {"styles": this.css.createTemplateAreaNode}).inject(this.app.content);
        createTemplateAreaNode.fade("in");

        var createTemplateScrollNode = new Element("div", {"styles": this.css.createTemplateScrollNode}).inject(createTemplateAreaNode);
        var createTemplateContentNode = new Element("div", {"styles": this.css.createTemplateContentNode}).inject(createTemplateScrollNode);
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(createTemplateScrollNode, {"indent": false});
        }.bind(this));

        var _self = this;
        var url = "/x_component_process_ProcessDesigner/$Process/template/templates.json";
        MWF.getJSON(url, function(json){
            json.each(function(template){
                var templateNode = new Element("div", {"styles": this.css.templateNode}).inject(createTemplateContentNode);
                var templateIconNode = new Element("div", {"styles": this.css.templateIconNode}).inject(templateNode);
                var templateTitleNode = new Element("div", {"styles": this.css.templateTitleNode, "text": template.title}).inject(templateNode);
                templateNode.store("template", template.name);

                var templateIconImgNode = new Element("img", {"styles": this.css.templateIconImgNode}).inject(templateIconNode);
                templateIconImgNode.set("src", "/x_component_process_ProcessDesigner/$Process/template/"+template.icon);

                templateNode.addEvents({
                    "mouseover": function(){this.setStyles(_self.css.templateNode_over)},
                    "mouseout": function(){this.setStyles(_self.css.templateNode)},
                    "mousedown": function(){this.setStyles(_self.css.templateNode_down)},
                    "mouseup": function(){this.setStyles(_self.css.templateNode_over)},
                    "click": function(e){
                        createProcess(e, this.retrieve("template"));
                        createTemplateAreaNode.destroy();
                        createTemplateMaskNode.destroy();
                    }
                });

            }.bind(this))

        }.bind(this));

        createTemplateMaskNode.addEvent("click", function(){
            createTemplateAreaNode.destroy();
            createTemplateMaskNode.destroy();
        });

        var size = this.app.content.getSize();
        var y = (size.y - 262)/2;
        var x = (size.x - 828)/2;
        if (y<0) y=0;
        if (x<0) x=0;
        createTemplateAreaNode.setStyles({
            "top": ""+y+"px",
            "left": ""+x+"px"
        });
    },
    _loadItemDataList: function(callback){
        this.app.restActions.listProcess(this.app.options.application.id,callback);
    },
    _getItemObject: function(item){
        return new MWF.xApplication.process.ProcessManager.ProcessExplorer.Process(this, item)
    },
    deleteItems: function(){
        this.hideDeleteAction();
        while (this.deleteMarkItems.length){
            var item = this.deleteMarkItems.shift();
            if (this.deleteMarkItems.length){
                item.deleteProcess();
            }else{
                item.deleteProcess(function(){
                    //    this.reloadItems();
                    //this.hideDeleteAction();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.process.ProcessManager.ProcessExplorer.Process = new Class({
	Extends: MWF.xApplication.process.ProcessManager.Explorer.Item,
	
	_open: function(e){
        var _self = this;
        var options = {
            "appId": "process.ProcessDesigner"+_self.data.id,
            "onQueryLoad": function(){
                this.actions = _self.explorer.actions;
                this.category = _self;
                this.options.id = _self.data.id;
                this.application = _self.explorer.app.options.application;
            }
        };
        this.explorer.app.desktop.openApplication(e, "process.ProcessDesigner", options);
	},
	_getIcon: function(){
		var x = (Math.random()*49).toInt();
		return "process_icon_"+x+".png";
	},
	_getLnkPar: function(){
		return {
			"icon": this.explorer.path+this.explorer.options.style+"/processIcon/lnk.png",
			"title": this.data.name,
			"par": "process.ProcessDesigner#{\"id\": \""+this.data.id+"\"}"
		};
	},
//	deleteItem: function(e){
//		var _self = this;
//		this.explorer.app.confirm("info", e, this.explorer.app.lp.process.deleteProcessTitle, this.explorer.app.lp.process.deleteProcess, 320, 110, function(){
//			_self.deleteProcess();
//			this.close();
//		},function(){
//			this.close();
//		});
//	},
	deleteProcess: function(callback){
		this.explorer.actions.deleteProcess(this.data.id, function(){
			this.node.destroy();
			if (callback) callback();
		}.bind(this));
	},

    saveItemAs: function(item){
        var id = item.id;
        var name = item.name;
        this.explorer.app.restActions.getProcess(this.data.id, function(json){
            var process = json.data;
            process.alias = "";
            var oldName = process.name;
            this.explorer.app.restActions.listProcess(id, function(processJson){
                var i=1;
                while (processJson.data.some(function(d){ return d.name==process.name })){
                    process.name = oldName+"_copy"+i;
                    i++;
                }
                process.application = id;
                process.applicationName = name;

                var oldIds = [];
                oldIds.push(process.id);
                if (process.begin) oldIds.push(process.begin.id);
                if (process.endList) process.endList.each(function(a){oldIds.push(a.id);});
                if (process.agentList) process.agentList.each(function(a){oldIds.push(a.id);});
                if (process.manualList) process.manualList.each(function(a){oldIds.push(a.id);});
                if (process.conditionList) process.conditionList.each(function(a){oldIds.push(a.id);});
                if (process.choiceList) process.choiceList.each(function(a){oldIds.push(a.id);});
                if (process.parallelList) process.parallelList.each(function(a){oldIds.push(a.id);});
                if (process.splitList) process.splitList.each(function(a){oldIds.push(a.id);});
                if (process.mergeList) process.mergeList.each(function(a){oldIds.push(a.id);});
                if (process.embedList) process.embedList.each(function(a){oldIds.push(a.id);});
                if (process.invokeList) process.invokeList.each(function(a){oldIds.push(a.id);});
                if (process.cancelList) process.cancelList.each(function(a){oldIds.push(a.id);});
                if (process.delayList) process.delayList.each(function(a){oldIds.push(a.id);});
                if (process.messageList) process.messageList.each(function(a){oldIds.push(a.id);});
                if (process.serviceList) process.serviceList.each(function(a){oldIds.push(a.id);});
                if (process.routeList) process.routeList.each(function(a){oldIds.push(a.id);});

                this.explorer.app.restActions.getId(oldIds.length, function(ids) {
                    var checkUUIDs = ids.data;
                    var processStr = JSON.encode(process);
                    oldIds.each(function(oid, i){
                        var reg = new RegExp(oid, "ig");
                        processStr = processStr.replace(reg, checkUUIDs[i].id);
                    }.bind(this));

                    process = JSON.decode(processStr);
                    process.isNewProcess = true;
                    this.explorer.app.restActions.saveProcess(process, function(){
                        if (id == this.explorer.app.options.application.id) this.explorer.reload();
                    }.bind(this));

                }.bind(this));
            }.bind(this));
        }.bind(this));
    }
});
