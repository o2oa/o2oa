MWF.xApplication.process.ProcessDesigner.Property = new Class({
	Implements: [Options, Events],

	load: function(){
        if (!this.process.options.isView){
            if (this.fireEvent("queryLoad")){
                MWF.getRequestText(this.htmlPath, function(responseText, responseXML){
                    this.htmlString = responseText;
                    MWF.require("MWF.widget.JsonTemplate", function(){
                        this.fireEvent("postLoad");
                    }.bind(this));
                }.bind(this));
            }
            this.propertyContent = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.process.propertyListNode);
            this.process.propertyListNode.addEvent("keydown", function(e){e.stopPropagation();});
        }
	},
	editProperty: function(td){
	},
	show: function(){
        if (!this.process.options.isView){
            this.process.panel.propertyTabPage.showTabIm();
            this.JsonTemplate = new MWF.widget.JsonTemplate(this.data, this.htmlString);
            this.process.propertyContent.set("html", this.JsonTemplate.load());
            this.process.panel.data = this.data;

            this.setEditNodeEvent();
            this.setEditNodeStyles(this.process.propertyListNode);
            this.loadPropertyTab();
            this.loadPersonInput();
            this.loadScriptInput();
            this.loadScriptText();
            //this.loadConditionInput();
            this.loadFormSelect();

        //    this.process.isFocus = true;
        }
	},
    //hide: function(){
    //    if (!this.process.options.isView) {
    //        this.JsonTemplate = null;
    //
    //        this.scriptTexts.each(function(script){
    //            MWF.release(script);
    //        }.bind(this));
    //        this.scriptTexts = null;
    //
    //        this.process.propertyListNode.set("html", "");
    //    }
    //},
    hide: function(){
        if (!this.process.options.isView) {
            this.JsonTemplate = null;
        }
    },
	
	loadPropertyTab: function(){
		var tabNodes = this.process.propertyListNode.getElements(".MWFTab");
		if (tabNodes.length){
			var tmpNode = this.process.propertyListNode.getFirst();
			var tabAreaNode = new Element("div", {
				"styles": this.process.css.propertyTabNode
			}).inject(tmpNode, "before");

			MWF.require("MWF.widget.Tab", function(){
				var tab = new MWF.widget.Tab(tabAreaNode, {"style": "moduleList"});
				tab.load();
				var tabPages = [];
				tabNodes.each(function(node){
					var tabPage = tab.addTab(node, node.get("title"), false);
					tabPages.push(tabPage);
				}.bind(this));
				tabPages[0].showTab();
			}.bind(this));
		}
	},
	
	setEditNodeEvent: function(){
		var property = this;
	//	var inputs = this.process.propertyListNode.getElements(".editTableInput");
		var inputs = this.process.propertyListNode.getElements("input");
		inputs.each(function(input){
			
			var jsondata = input.get("name");
			if (jsondata){
				var inputType = input.get("type").toLowerCase();
				switch (inputType){
					case "radio":
						input.addEvent("change", function(e){
							property.setRadioValue(jsondata, this);
						});
						input.addEvent("blur", function(e){
							property.setRadioValue(jsondata, this);
						});
                        input.addEvent("keydown", function(e){
                            e.stopPropagation();
                        });
						break;
					case "checkbox":
                        input.addEvent("keydown", function(e){
                            e.stopPropagation();
                        });

						break;
					default:
						input.addEvent("change", function(e){
							property.setValue(jsondata, this.value);
						});
						input.addEvent("blur", function(e){
							property.setValue(jsondata, this.value);
						});
						input.addEvent("keydown", function(e){
							if (e.code==13){
								property.setValue(jsondata, this.value);
							}
                            e.stopPropagation();
						});
				}
			}
		}.bind(this));

        var selects = this.process.propertyListNode.getElements("select");
        selects.each(function(select){
            var jsondata = select.get("name");
            if (jsondata){
                select.addEvent("change", function(e){
                    property.setSelectValue(jsondata, this);
                });
            }
        });
		
		var textareas = this.process.propertyListNode.getElements("textarea");
		textareas.each(function(input){
			var jsondata = input.get("name");
			if (jsondata){
				input.addEvent("change", function(e){
					property.setValue(jsondata, this.value);
				});
				input.addEvent("blur", function(e){
					property.setValue(jsondata, this.value);
				});
                input.addEvent("keydown", function(e){
                    e.stopPropagation();
                });
			}
		}.bind(this));
		
	},
    setSelectValue: function(name, select){
        var idx = select.selectedIndex;
        var options = select.getElements("option");
        var value = "";
        if (options[idx]){
            value = options[idx].get("value");
        }
        this.data[name] = value;
    },
	setRadioValue: function(name, input){
		if (input.checked){
			var value = input.value;
			if (value=="false") c = false;
			if (value=="true") c = true;
			this.data[name] = value;
		}
	},
	setValue: function(name, value){
		this.data[name] = value;
		if (name=="name"){
			if (!value) this.data[name] = MWF.APPPD.LP.unnamed;
		//	this.activity.redraw();
		}
	},
	setEditNodeStyles: function(node){
		var nodes = node.getChildren();
		if (nodes.length){
			nodes.each(function(el){
				var cName = el.get("class");
				if (cName){
					if (this.process.css[cName]) el.setStyles(this.process.css[cName]);
				}
				this.setEditNodeStyles(el);
			}.bind(this));
		}
	},
    loadScriptText: function(){
        this.scriptTexts = [];
        var scriptNodes = this.process.propertyListNode.getElements(".MWFScriptText");
        MWF.require("MWF.xApplication.process.ProcessDesigner.widget.ScriptText", function(){
            var _self = this;
            scriptNodes.each(function(node){
                var script = new MWF.xApplication.process.ProcessDesigner.widget.ScriptText(node, this.data[node.get("name")], this.process.designer, {
                    "maskNode": this.process.designer.content,
                    "maxObj": this.process.designer.paperNode,
                    "onChange": function(code){
                        _self.data[node.get("name")] = code;
                    }
                });
                this.scriptTexts.push(script);
                //this.setScriptItems(script, node);
            }.bind(this));
        }.bind(this));
    },
	loadScriptInput: function(){
		var scriptNodes = this.process.propertyListNode.getElements(".MWFScript");
        MWF.require("MWF.xApplication.process.ProcessDesigner.widget.ScriptSelector", function(){
            var _self = this;
            scriptNodes.each(function(node){
                var script = new MWF.xApplication.process.ProcessDesigner.widget.ScriptSelector(node, this.data[node.get("name")], this.process.designer, {
                    "maskNode": this.process.designer.content,
                    "onSelected": function(scriptData){
                        _self.data[node.get("name")] = scriptData.name;
                    },
                    "onDelete": function(){
                        _self.data[node.get("name")] = "";
                        node.empty();
                    }
                    //"onPostSave": function(script){
                    //    this.saveScriptItem(node, script);
                    //}.bind(this),
                    //"onQueryDelete": function(script){
                    //    this.deleteScriptItem(node, script);
                    //}.bind(this)
                });
                //this.setScriptItems(script, node);
            }.bind(this));
        }.bind(this));
	},

    loadPersonInput: function(){
        var personIdentityNodes = this.process.propertyListNode.getElements(".MWFPersonIdentity");
        var personDepartmentNodes = this.process.propertyListNode.getElements(".MWFPersonDepartment");
        var personCompanyNodes = this.process.propertyListNode.getElements(".MWFPersonCompany");
        MWF.require("MWF.xApplication.process.ProcessDesigner.widget.PersonSelector", function(){
            personIdentityNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.process.designer, {
                    "type": "identity",
                    "names": this.data[node.get("name")],
                    "onChange": function(ids){this.savePersonItem(node, ids);}.bind(this)
                });
            }.bind(this));

            personDepartmentNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.process.designer, {
                    "type": "department",
                    "names": this.data[node.get("name")],
                    "onChange": function(ids){this.savePersonItem(node, ids);}.bind(this)
                });
            }.bind(this));
            personCompanyNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.process.designer, {
                    "type": "company",
                    "names": this.data[node.get("name")],
                    "onChange": function(ids){this.savePersonItem(node, ids);}.bind(this)
                });
            }.bind(this));
        }.bind(this));

        //personNodes.each(function(node){
        //    MWF.require("MWF.xApplication.process.ProcessDesigner.widget.ScriptEditor", function(){
        //        var script = new MWF.xApplication.process.ProcessDesigner.widget.ScriptEditor(node, {
        //            "onPostSave": function(script){
        //                this.saveScriptItem(node, script);
        //            }.bind(this),
        //            "onQueryDelete": function(script){
        //                this.deleteScriptItem(node, script);
        //            }.bind(this)
        //        });
        //        this.setScriptItems(script, node);
        //    }.bind(this));
        //}.bind(this));
    },
    savePersonItem: function(node, ids){

        var values = [];
        ids.each(function(id){
            values.push(id.data.name);
        }.bind(this));
        this.data[node.get("name")] = values;
    },
    loadConditionInput: function(){
        var conditionNodes = this.process.propertyListNode.getElements(".MWFCondition");
        conditionNodes.each(function(node){
            MWF.require("MWF.xApplication.process.ProcessDesigner.widget.ConditionEditor", function(){
                var script = new MWF.xApplication.process.ProcessDesigner.widget.ConditionEditor(node, {
                    "onPostSave": function(script){
                        this.saveScriptItem(node, script);
                    }.bind(this),
                    "onQueryDelete": function(script){
                        this.deleteScriptItem(node, script);
                    }.bind(this)
                });
                this.setScriptItems(script, node);
            }.bind(this));
        }.bind(this));
    },
    loadFormSelect: function(){
        var formNodes = this.process.propertyListNode.getElements(".MWFFormSelect");
        if (formNodes.length){
            this.getFormList(function(){
                formNodes.each(function(node){
                    var select = new Element("select").inject(node);
                    var option = new Element("option", {"text": "none"}).inject(select);
                    select.addEvent("change", function(e){
                        this.setValue(e.target.getParent("div").get("name"), e.target.options[e.target.selectedIndex].value);
                    }.bind(this));

                    var name = node.get("name");
                    this.forms.each(function(form){
                        var option = new Element("option", {
                            "text": form.name,
                            "value": form.id,
                            "selected": (this.data[name]==form.id)
                        }).inject(select);
                    }.bind(this));
                }.bind(this));
            }.bind(this));
        }
    },
    getFormList: function(callback){
        if (!this.forms){
            this.process.designer.actions.listForm(this.process.designer.application.id, function(json){
                this.forms = json.data;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

	deleteScriptItem: function(node, script){
		var jsondata = node.get("name");
		this.data[jsondata].erase(script.data.id);
		this.process.scripts[script.data.id] = null;
		delete this.process.scripts[script.data.id];
		this.process.process.scriptList.erase(script.data);
	},
	saveScriptItem: function(node, script){
		var jsondata = node.get("name");
		var scriptList = this.data[jsondata];
		
		var data = script.data;
		var scriptData = this.process.scripts[script.data.id];
		if (!scriptData){
			this.process.process.scriptList.push(data);
			this.process.scripts[script.data.id] = data;
		}
		if (scriptList.indexOf(data.id) == -1){
			this.data[jsondata].push(data.id);
		}
	},
	setScriptItems: function(script, node){
		var jsondata = node.get("name");
		var scriptList = this.data[jsondata];
		if (scriptList){
			scriptList.each(function(id){
				if (id){
					var data = this.process.scripts[id];
					if (data) script.setScriptItem(data);
				}
			}.bind(this));
		}
	},
	
	showMultiActivity: function(activitys){
		this.hide();
		var multiActivityTable = new HtmlTable({
		    "properties": this.process.css.activityListTable
		}).inject(this.process.propertyListNode);
		
		activitys.each(function(activity){
			this.row = multiActivityTable.push([
				{	
			    	"content": " ",
			    	"properties": {
			    		"styles": activity.style.listIcon
			        }
			    },
			    {	
			    	"content": activity.data.name,
			    	"properties": {
			    		"width": "80px",
			    		"styles": this.process.css.list.listText
			        }
			    },
			    {	
			    	"content": " "+activity.data.description,
			    	"properties": {
			    		"styles": this.process.css.list.listTextDescription
			        }
			    }
			]);
		}.bind(this));
	}
});