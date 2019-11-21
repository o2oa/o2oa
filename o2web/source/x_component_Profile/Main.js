MWF.xApplication.Profile.options.multitask = false;
MWF.xApplication.Profile.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "Profile",
        "icon": "icon.png",
        "width": "800",
        "height": "600",
        "isResize": false,
        "isMax": false,
        "mvcStyle": "style.css",
        "title": MWF.xApplication.Profile.LP.title
    },
    _loadCss: function(){},
    loadCss: function(file, callback){
        var path = (file && typeOf(file)==="string") ? file : "style.css";
        var cb = (file && typeOf(file)==="function") ? file : callback;
        var cssPath = this.path+this.options.style+"/"+path;
        this.content.loadCss(cssPath, cb);
    },
    onQueryLoad: function(){
        this.lp = MWF.xApplication.Profile.LP;
        //console.log(this.lp);
        this.action = MWF.Actions.get("x_organization_assemble_personal");
    },
    loadApplication: function(callback){
        this.action.getPerson(function(json){
            this.personData = json.data;
            this.personData.personIcon = this.action.getPersonIcon();

            this.content.loadHtml(this.path+this.options.style+"/"+((this.inBrowser)? "viewBrowser": "view")+".html", {"bind": {"data": this.personData, "lp": this.lp}}, function(){
                this.loadContent()
            }.bind(this));
        }.bind(this));
        //this.loadCss();

        // this.loadTitle();
        // this.loadContent();
        if (callback) callback();
    },

    loadContent: function(){
        var pageConfigNodes = this.content.getElements(".o2_profile_configNode");

        this.contentNode = this.content.getElement(".o2_profile_contentNode");
        MWF.require("MWF.widget.Tab", function(){
            this.tab = new MWF.widget.Tab(this.contentNode, {"style": "profile"});
            this.tab.load();

            pageConfigNodes.each(function(node){
                this.tab.addTab(node, node.get("title"));
            }.bind(this));

            if (this.options.tab){
                this.tab.pages[this.options.tab].showIm();
            }else{
                this.tab.pages[0].showIm();
            }

            this.loadInforConfigActions();
            if (!this.inBrowser){
                this.loadLayoutConfigActions();
            }else{

            }

            this.loadIdeaConfigActions();
            this.loadEmPowerConfigAction();
            this.loadPasswordConfigActions();
            this.loadSSOConfigAction();

        }.bind(this));
    },
    loadInforConfigActions: function(){
        this.contentImgNode = this.content.getElement(".o2_profile_inforIconContentImg");
        this.content.getElement(".o2_profile_inforIconChange").addEvent("click", function(){
            this.changeIcon();
        }.bind(this));

        var inputs = this.tab.pages[0].contentNode.getElements("input");
        this.mailInputNode = inputs[0];
        this.mobileInputNode = inputs[1];
        this.officePhoneInputNode = inputs[2];
        this.weixinInputNode = inputs[3];
        this.qqInputNode = inputs[4];
        this.signatureInputNode = inputs[5];

        this.content.getElement(".o2_profile_saveInforAction").addEvent("click", function(){
            this.savePersonInfor();
        }.bind(this));
    },

    loadLayoutConfigActions: function(){
        var buttons = this.tab.pages[1].contentNode.getElements(".o2_profile_layoutClearDataAction");
        this.clearDataAction = buttons[0];
        this.defaultDataAction = (buttons.length>1) ? buttons[1]: null;
        this.clearDefaultDataAction = (buttons.length>2) ? buttons[2]: null;
        this.forceDataAction = (buttons.length>3) ? buttons[3]: null;
        this.deleteForceDataAction = (buttons.length>4) ? buttons[4]: null;

        this.clearDataAction.addEvent("click", function(){
            MWF.require("MWF.widget.UUID", function(){
                MWF.UD.deleteData("layout", function(){
                    this.notice(this.lp.clearok, "success");
                    this.desktop.notRecordStatus = true;
                }.bind(this));
            }.bind(this));
        }.bind(this));

        if( MWF.AC.isAdministrator() ){
            this.defaultDataAction.addEvent("click", function(){
                MWF.require("MWF.widget.UUID", function(){
                    var text = this.lp.setDefaultOk;
                    this.close();
                    var status = layout.desktop.getLayoutStatusData();
                    MWF.UD.putPublicData("defaultLayout", status, function(){
                        MWF.xDesktop.notice("success", {"x": "right", "y": "top"}, text, layout.desktop.desktopNode);
                    }.bind(this));
                }.bind(this));
            }.bind(this));

            this.clearDefaultDataAction.addEvent("click", function(){
                MWF.require("MWF.widget.UUID", function(){
                    MWF.UD.deletePublicData("defaultLayout", function(){
                        this.notice(this.lp.clearok, "success");
                        this.desktop.notRecordStatus = true;
                    }.bind(this));
                }.bind(this));
            }.bind(this));

            this.forceDataAction.addEvent("click", function(){
                MWF.require("MWF.widget.UUID", function(){
                    var text = this.lp.setForceOk;
                    this.close();
                    var status = layout.desktop.getLayoutStatusData();
                    MWF.UD.putPublicData("forceLayout", status, function(){
                        MWF.xDesktop.notice("success", {"x": "right", "y": "top"}, text, layout.desktop.desktopNode);
                    }.bind(this));
                }.bind(this));
            }.bind(this));

            this.deleteForceDataAction.addEvent("click", function(){
                MWF.require("MWF.widget.UUID", function(){
                    MWF.UD.deletePublicData("forceLayout", function(){
                        this.notice(this.lp.clearok, "success");
                        this.desktop.notRecordStatus = true;
                    }.bind(this));
                }.bind(this));
            }.bind(this));
        }


        var UINode = this.tab.pages[1].contentNode.getLast();
        this.loadDesktopBackground(UINode);
    },

    loadIdeaConfigActions: function(){
        var i = (this.inBrowser)? 1 : 2;
        this.ideasArea = this.tab.pages[i].contentNode.getElement("textarea");
        this.ideasSaveAction = this.ideasArea.getNext();
        this.ideasSaveDefaultAction = this.ideasSaveAction.getNext() || null;

        if (MWF.AC.isAdministrator()){
            this.ideasSaveDefaultAction.addEvent("click", function(){
                MWF.require("MWF.widget.UUID", function(){
                    var data = {};
                    data.ideas = this.ideasArea.get("value").split("\n");
                    MWF.UD.putPublicData("idea", data, function(){
                        this.notice(this.lp.ideaSaveOk, "success");
                    }.bind(this));
                }.bind(this));
            }.bind(this))
        }

        MWF.require("MWF.widget.UUID", function(){
            MWF.UD.getDataJson("idea", function(json){
                if (json){
                    if (json.ideas) this.ideasArea.set("value", json.ideas.join("\n"));
                }
            }.bind(this));
        }.bind(this));

        this.ideasSaveAction.addEvent("click", function(){
            MWF.require("MWF.widget.UUID", function(){
                var data = {};
                data.ideas = this.ideasArea.get("value").split("\n");
                MWF.UD.putData("idea", data, function(){
                    this.notice(this.lp.ideaSaveOk, "success");
                }.bind(this));
            }.bind(this));
        }.bind(this))
    },
    loadEmPowerConfigAction: function(){
        var i = (this.inBrowser)? 2 : 3;
        //console.log(this.tab.pages[i].contentNode);
        this.tab.pages[i].contentNode.setStyle("overflow","auto")
        /*this.addEmpower = this.tab.pages[i].contentNode.getElement(".o2_profile_emPower_Add");
        this.addEmpower.setStyles({
            "height":"30px",
                "line-height":"30px",
                "text-align":"center",
                "width":"50px",
                "border-radius":"5px",
                "background-color":"#5f6b80",
                "border":"1px solid #FFF",
                "color":"#FFF",
                "cursor":"pointer",
                "margin":"0px 0px"
        });

        this.addEmpower.addEvent("click",function(){

            var popForm = new MWF.xApplication.Profile.emPowerPopupForm(null, {}, {
                "style": "empower",
                "width": "800",
                "height": "500",
                "hasTop": true,
                "hasIcon": false,
                "hasTopIcon" : false,
                "hasTopContent" : false,
                "draggable": true,
                "maxAction" : true,
                "closeAction": true,
                "isFull" : false,
                "startTime" : null,
                "endTime" : null,
                "isWholeday" : false,
                "title" : "新建外出授权",
                "defaultCalendarId" : ""
            }, {
                app: this,
                container : this.content,
                lp : this.lp,
                actions : this.action,
                css : {}
            });

            popForm.create();
        }.bind(this));
        */


        var tabEmpowerNodes = this.tab.pages[i].contentNode.getElements("div.o2_profile_emPower_tab");
        this.emPowerContentNode = this.tab.pages[i].contentNode.getElement("div.o2_profile_emPower_content");

        MWF.require("MWF.widget.Tab", function() {
            this.tabEmpower = new MWF.widget.Tab(this.emPowerContentNode, {"style": "profile"});
            this.tabEmpower.load();
            //console.log(this.tabEmpower);
            tabEmpowerNodes.each(function(node){
                this.tabEmpower.addTab(node, node.get("title"));

            }.bind(this));

            if (this.options.tabEmpower){
                this.tabEmpower.pages[this.options.tab].showIm();
            }else{
                this.tabEmpower.pages[0].showIm();
            }
        }.bind(this));

        this.loadMyEmPower();
        this.loadReceiveEmPower();
        this.loadMyEmPowerLog();
        this.loadReceiveEmPowerLog();
    },
    loadMyEmPower: function(){

        this.myEmPower = this.tabEmpower.pages[0].contentNode;
        //this.myEmPowerContent = new Element("div.myEmPowerContent",{text:"hhhhhh"}).inject(this.myEmPower);
        this.getAction( function( ){
            this.action.getMyEmPower(function(json){
                var data = json.data;
                this.getEmPowerTable("myEmPower",data,this.myEmPower);
            }.bind(this));
        }.bind(this));

    },
    loadReceiveEmPower: function(){
        this.receiveEmPower =  this.tabEmpower.pages[1].contentNode;
        //this.myEmPowerContent = new Element("div.myEmPowerContent",{text:"hhhhhh"}).inject(this.myEmPower);
        this.getAction( function( ){
            this.action.getReceiveEmPower(function(json){
                var data = json.data;
                this.getEmPowerTable("receiveEmPower",data,this.receiveEmPower);
            }.bind(this));
        }.bind(this) );
    },
    loadMyEmPowerLog: function(){
        this.myEmPowerLog =  this.tabEmpower.pages[2].contentNode;
        //this.myEmPowerContent = new Element("div.myEmPowerContent",{text:"hhhhhh"}).inject(this.myEmPower);
        this.getAction( function( ){
            this.action.getMyEmPowerLog(function(json){
                var data = json.data;
                this.getEmPowerLogTable("myEmPowerLog",data,this.myEmPowerLog);
            }.bind(this));
        }.bind(this) );
    },
    loadReceiveEmPowerLog: function(){
        this.receiveEmPowerLog =  this.tabEmpower.pages[3].contentNode;
        //this.myEmPowerContent = new Element("div.myEmPowerContent",{text:"hhhhhh"}).inject(this.myEmPower);
        this.getAction( function( ){
            this.action.getReceiveEmPowerLog(function(json){
                var data = json.data;
                this.getEmPowerLogTable("receiveEmPowerLog",data,this.receiveEmPowerLog);
            }.bind(this));
        }.bind(this) );
    },
    getEmPowerLogTable: function(type,data,content){
        var table = content.getElement("table.emPowerTable");
        if(!table){
            //console.log("table no define");
            table = new Element("table.emPowerTable",{
                width:"100%",
            }).setStyles(
                {"text-align":"center"}
            ).inject(content);
        }
        this.userName = layout.desktop.session.user.distinguishedName;
        var th = new Element("tr");
        th.adopt(new Element("th",{width:"30%"}).adopt(new Element("div",{text:this.lp.empower.title})));
        if(type=="myEmPowerLog"){
            th.adopt(new Element("th",{width:"10%"}).adopt(new Element("div",{text:this.lp.empower.toPerson})));
        }else{
            th.adopt(new Element("th",{width:"10%"}).adopt(new Element("div",{text:this.lp.empower.fromPerson})));
        }
        th.adopt(new Element("th",{width:"15%"}).adopt(new Element("div",{text:this.lp.empower.applicationName})));
        th.adopt(new Element("th",{width:"15%"}).adopt(new Element("div",{text:this.lp.empower.processName})));
        th.adopt(new Element("th",{width:"15%"}).adopt(new Element("div",{text:this.lp.empower.activityName})));
        th.adopt(new Element("th",{width:"15%"}).adopt(new Element("div",{text:this.lp.empower.createTime})));

        table.adopt(th);
        data.forEach(function(item){
            //console.log(item);
            var tr = new Element("tr",{title:item.title});
            var td = new Element("td");

            var tds = [];
            var _self = this;
            tds.push(td.cloneNode().adopt(new Element("div",{name:"title",text:item.title}).setStyles({
                "cursor":"pointer"
            }).addEvent(
                "click",
                function(e){
                    var options = {"workId": this.work, "appId": "process.Work"+this.work};
                    _self.desktop.openApplication(e, "process.Work", options);
                }.bind(item)
            )));
            if(type=="myEmPowerLog"){
                tds.push(td.cloneNode().adopt(new Element("div",{name:"toPerson",text:item.toPerson.split("@")[0]})));
            }else{
                tds.push(td.cloneNode().adopt(new Element("div",{name:"fromPerson",text:item.fromPerson.split("@")[0]})));
            }
            tds.push(td.cloneNode().adopt(new Element("div",{name:"applicationName",text:item.applicationName||"-"})));
            tds.push(td.cloneNode().adopt(new Element("div",{name:"processName",text:item.processName||"-"})));
            tds.push(td.cloneNode().adopt(new Element("div",{name:"completedTime",text:item.activityName})));
            tds.push(td.cloneNode().adopt(new Element("div",{name:"startTime",text:item.trustTime})));

            tr.adopt(tds);
            table.adopt(tr);
        }.bind(this));

        if(!this[type+"NoDataDiv"]){
            this[type+"NoDataDiv"] = new Element("div",{text:this.lp.empower.noData}).setStyles({
                "border": "1px solid rgb(204, 204, 204)",
                "border-radius": "4px",
                "margin": "25px auto",
                "width": "250px",
                "height": "100px",
                "line-height": "100px",
                "text-align": "center",
                "display":"none",
                "color": "rgb(153, 153, 153)",
                "background-color": "rgb(255, 255, 255)"
            }).inject(content);
        }
        if(table.getElements("tr").length==1){
            this[type+"NoDataDiv"].setStyle("display","");
            //table.adopt(new Element("tr").adopt(new Element("td")).adopt()));
        }else{
            if(this[type+"NoDataDiv"]){
                this[type+"NoDataDiv"].setStyle("display","none");
            }
            //this.myEmPowerTable.adopt(trs);
        }
    },
    getEmPowerTable: function(type,data,content){
        var table = content.getElement("table.emPowerTable");
        if(!table){
            //console.log("table no define");
            table = new Element("table.emPowerTable",{
                width:"100%",
            }).setStyles(
                {"text-align":"center"}
            ).inject(content);
        }
        this.userName = layout.desktop.session.user.distinguishedName;
        var th = new Element("tr");
        if(type=="myEmPower"){
            //新增按钮
            var addEmPower = new Element("div.o2_profile_emPower_Add",{text:this.lp.empower.addEmPower}).setStyles({
                "height":"20px",
                "line-height":"20px",
                "text-align":"center",
                "width":"35px",
                "border-radius":"5px",
                "background-color":"#5f6b80",
                "border":"1px solid #FFF",
                "color":"#FFF",
                "cursor":"pointer",
                "margin-right":"15px",
                "float":"left"
            }).addEvent("click",function(){
                var popForm = new MWF.xApplication.Profile.emPowerPopupForm(null, {}, {
                    "style": "empower",
                    "width": "800",
                    "height": "460",
                    "hasTop": true,
                    "hasIcon": false,
                    "hasTopIcon" : false,
                    "hasTopContent" : false,
                    "draggable": true,
                    "maxAction" : true,
                    "closeAction": true,
                    "isFull" : false,
                    "startTime" : null,
                    "endTime" : null,
                    "isWholeday" : false,
                    "title" : "新建外出授权",
                    "defaultCalendarId" : ""
                }, {
                    app: this,
                    container : this.content,
                    lp : this.lp,
                    actions : this.action,
                    css : {}
                });
                popForm.create();
            }.bind(this));
            //撤回按钮
            var withDraw = new Element("div",{
                text:this.lp.empower.withdraw
            }).setStyles({
                "height":"20px",
                "line-height":"20px",
                "text-align":"center",
                "width":"35px",
                "border-radius":"5px",
                "background-color":"#5f6b80",
                "border":"1px solid #FFF",
                "color":"#FFF",
                "cursor":"pointer",
                "float":"left"
            }).addEvent("click",function(){
                var checkElement = table.getElements("input[name=id]:checked");
                var editCount = 0;

                checkElement.forEach(function(item){
                    this.action.deleteEmPower(item.get("value"),function(json){
                        if(json.type=="success"){
                            editCount += 1;
                        }
                        item.getParent().getParent().destroy();
                    }.bind(this),null,false);
                    /*enable为false时无法再次委托相同内容，又因为目前没做授权的编辑，所以撤回直接调用delete接口
                    var idata = this.emPowerData[item.get("value")];
                    idata.enable = false;
                    this.action.editEmPower(item.get("value"),idata,function(json){
                        if(json.type=="success"){
                            editCount += 1;
                        }
                        item.getParent().getParent().destroy();
                    }.bind(this),null,false);*/
                }.bind(this));
                //console.log(editCount,checkElement.length);
                if(checkElement.length==0){
                    this.notice(this.lp.empower.selectEmPower, "error");
                }else if(checkElement.length&&editCount==checkElement.length){
                    this.notice(this.lp.empower.withdrawOk, "success");
                }else{
                    //this.notice(this.lp.empower.withdrawOk, "success");
                }
                //console.log(this.myEmPower.getElements("tr").length);
                if(this.myEmPower.getElements("tr").length==1){
                    this["myEmPowerNoDataDiv"].setStyle("display","");
                }

            }.bind(this));
            this.emPowerData={};
            th.adopt(new Element("th",{width:"15%"}).adopt(new Element("div").setStyles({"margin":"auto","display":"inline-block"}).adopt(addEmPower,withDraw)));

            th.adopt(new Element("th",{width:"8%"}).adopt(new Element("div",{text:this.lp.empower.toPerson})));
        }else{
            th.adopt(new Element("th",{width:"20%"}).adopt(new Element("div",{text:this.lp.empower.fromPerson})));
        }

        th.adopt(new Element("th",{width:"7%"}).adopt(new Element("div",{text:this.lp.empower.type})));
        th.adopt(new Element("th",{width:"15%"}).adopt(new Element("div",{text:this.lp.empower.applicationName+"/"+this.lp.empower.processName})));
        th.adopt(new Element("th",{width:"20%"}).adopt(new Element("div",{text:this.lp.empower.startTime})));
        th.adopt(new Element("th",{width:"20%"}).adopt(new Element("div",{text:this.lp.empower.completedTime})));
        /*后续添加操作列
        if(type=="myEmPower"&&this.userName=="纪桂丹"){
            th.adopt(new Element("th",{width:"20%"}).adopt(new Element("div",{text:"操作"})));
        }*/
        //console.log(this.userName);
        if(type=="myEmPower"){
            th.adopt(new Element("th",{width:"15%"}).adopt(new Element("div",{text:"操作"})));
        }
        table.adopt(th);
        data.forEach(function(item){
            //console.log(item);
            if(true||item.enable==true){
                var tr = new Element("tr");
                var td = new Element("td");


                var checkbox = new Element("input",{
                    name:"id",
                    value:item.id,
                    type:"checkbox"
                });
                var tds = [];

                if(type=="myEmPower"){
                    this.emPowerData[item.id]=item;
                    tds.push(td.cloneNode().adopt(checkbox));
                    tds.push(td.cloneNode().adopt(new Element("div",{name:"toPerson",text:item.toPerson.split("@")[0]})));
                }else{
                    tds.push(td.cloneNode().adopt(new Element("div",{name:"fromPerson",text:item.fromPerson.split("@")[0]})));
                }

                tds.push(td.cloneNode().adopt(new Element("div",{name:"type",text:this.lp.empower["type_"+item.type]})));
                tds.push(td.cloneNode().adopt(new Element("div",{name:"typeName",text:item.applicationName||item.processName||"-"})));
                tds.push(td.cloneNode().adopt(new Element("div",{name:"startTime",text:item.startTime})));
                tds.push(td.cloneNode().adopt(new Element("div",{name:"completedTime",text:item.completedTime})));
                //后续添加编辑/删除/撤回按钮
                var _self = this;
               // console.log(this);
                if(type=="myEmPower"){
                    //启用、禁用按钮

                    var endTime = new Date(item.completedTime);
                    var startTime= new Date(item.startTime);
                    var nowTime = new Date();
                    var isEnable = new Element("div",{name:"isEnable",text:item.enable?_self.lp.empower.disable:_self.lp.empower.enable}).setStyles({
                        "height":"20px",
                        "line-height":"20px",
                        "text-align":"center",
                        "width":"35px",
                        "border-radius":"5px",
                        "background-color":"#5f6b80",
                        "border":"1px solid #FFF",
                        "color":"#FFF",
                        "cursor":"pointer",
                        "float":"left"
                    }).addEvent("click",function(){
                        this.enable = !this.enable;
                        _self.action.editEmPower(this.id,this,function(json){
                            //submitCount++;
                            //console.log(this);
                            this.notice(this.lp.empower.saveOk,json.type);
                            var content = this.content.getElement(".o2_profile_emPower_tab");
                            content.getElement("table").empty();
                            this.loadMyEmPower();
                        }.bind(_self),null,false);
                    }.bind(item));
                    tds.push(td.cloneNode().setStyles({
                        "margin":"auto",
                        "display":"inline-block"
                    }).adopt(
                        new Element("div",{name:"edit",text:this.lp.empower.edit}).setStyles({
                            "height":"20px",
                            "line-height":"20px",
                            "text-align":"center",
                            "width":"35px",
                            "border-radius":"5px",
                            "background-color":"#5f6b80",
                            "border":"1px solid #FFF",
                            "color":"#FFF",
                            "cursor":"pointer",
                            "float":"left"
                        }).addEvent("click",function(e){
                            //console.log(e);
                            var _data = this;
                            _data.fromPerson = _data.fromIdentity;
                            _data.toPerson = _data.toIdentity;
                            _data.startTime = _data.startTime;
                            _data.endTime = _data.completedTime;

                             var editPopForm = new MWF.xApplication.Profile.emPowerPopupForm(null, _data, {
                                "style": "empower",
                                "width": "800",
                                "height": "460",
                                "hasTop": true,
                                "hasIcon": false,
                                "hasTopIcon" : false,
                                "hasTopContent" : false,
                                "draggable": true,
                                "maxAction" : true,
                                "closeAction": true,
                                "isFull" : false,
                                "startTime" : null,
                                "endTime" : null,
                                "isWholeday" : false,
                                "title" : _self.lp.empower.editEmpower,
                                "configData":{Process:(_data.type=="process"?[{name:_data.processName,id:_data.process}]:[]),Application:(_data.type=="application"?[{name:_data.applicationName,id:_data.application}]:[])},
                                "defaultCalendarId" : ""
                            }, {
                                app: _self,
                                container : _self.content,
                                lp : _self.lp,
                                actions : _self.action,
                                css : {}
                            });
                            editPopForm.edit();
                        }.bind(item)),
                        (nowTime<startTime||nowTime>endTime)&&false?"":isEnable,
                        new Element("div",{name:"withDraw",empowerid:item.id,text:_self.lp.empower.withdraw}).setStyles({
                            "height":"20px",
                            "line-height":"20px",
                            "text-align":"center",
                            "width":"35px",
                            "border-radius":"5px",
                            "background-color":"#5f6b80",
                            "border":"1px solid #FFF",
                            "color":"#FFF",
                            "cursor":"pointer",
                            "float":"left"
                        }).addEvent("click",function(){
                            //console.log(this);
                            var _this = this;

                            _self.action.deleteEmPower(_this.get("empowerid"),function(json){
                                this.notice(this.lp.empower.withdrawOk,json.type);
                                 _this.getParent().getParent().destroy();
                            }.bind(_self),null,false);
                        })
                        ));

                }/**/
                tr.adopt(tds);
                table.adopt(tr);
            }
        }.bind(this));
        if(!this[type+"NoDataDiv"]){
            this[type+"NoDataDiv"] = new Element("div",{text:this.lp.empower.noData}).setStyles({
                "border": "1px solid rgb(204, 204, 204)",
                "border-radius": "4px",
                "margin": "25px auto",
                "width": "250px",
                "height": "100px",
                "line-height": "100px",
                "text-align": "center",
                "display":"none",
                "color": "rgb(153, 153, 153)",
                "background-color": "rgb(255, 255, 255)"
            }).inject(content);
        }
        if(table.getElements("tr").length==1){
            this[type+"NoDataDiv"].setStyle("display","");
            //table.adopt(new Element("tr").adopt(new Element("td")).adopt()));
        }else{
            if(this[type+"NoDataDiv"]){
                this[type+"NoDataDiv"].setStyle("display","none");
            }
            //this.myEmPowerTable.adopt(trs);
        }
    },
    /*
    //外出授权
      "getMyEmPower": {"uri": "/jaxrs/empower/list/currentperson", "method": "GET"},
      "getReceiveEmPower": {"uri": "/jaxrs/empower/list/to", "method": "GET"},
      "createEmPower": {"uri": "/jaxrs/empower", "method": "POST"},
      "editEmPower": {"uri": "/jaxrs/empower", "method": "PUT"},
    * */


    loadPasswordConfigActions: function(){
        var i = (this.inBrowser)? 3 : 4;
        var inputs = this.tab.pages[i].contentNode.getElements("input");
        this.oldPasswordInputNode = inputs[0];
        this.passwordInputNode = inputs[1];
        this.morePasswordInputNode = inputs[2];
        this.savePasswordAction = this.tab.pages[i].contentNode.getElement(".o2_profile_savePasswordAction");

        this.oldPasswordInputNode.addEvents({
            "blur": function(){this.removeClass("o2_profile_inforContentInput_focus");},
            "focus": function(){this.addClass("o2_profile_inforContentInput_focus");}
        });
        this.passwordInputNode.addEvents({
            "blur": function(){this.removeClass("o2_profile_inforContentInput_focus");},
            "focus": function(){this.addClass("o2_profile_inforContentInput_focus");}
            //"blur" : function(){ this.checkPassowrdStrength(  this.passwordInputNode.get("value"), true ) }.bind(this)
        });
        this.morePasswordInputNode.addEvents({
            "blur": function(){this.removeClass("o2_profile_inforContentInput_focus");},
            "focus": function(){this.addClass("o2_profile_inforContentInput_focus");}
        });
        this.savePasswordAction.addEvent("click", function(){
            this.changePassword();
        }.bind(this));
        this.checkPassowrdStrength("1");
    },
    loadSSOConfigAction: function(){
        var i = (this.inBrowser)? 4 : 5;
        this.ssoConfigAreaNode = this.tab.pages[i].contentNode.getElement(".o2_profile_ssoConfigArea");
        MWF.Actions.get("x_organization_assemble_authentication").listOauthServer(function(json){
            json.data.each(function(d){
                var node = new Element("a", {
                    "styles": {"font-size": "14px", "display": "block", "margin-bottom": "10px"},
                    "text": d.displayName,
                    "target": "_blank",
                    "href": "/x_desktop/oauth.html?oauth="+encodeURIComponent(d.name)+"&redirect="+"&method=oauthBind"
                }).inject(this.ssoConfigAreaNode)
            }.bind(this));
        }.bind(this));
    },

    changeIcon: function(){
        var options = {};
        var width = "668";
        var height = "510";
        width = width.toInt();
        height = height.toInt();

        var size = this.content.getSize();
        var x = (size.x-width)/2;
        var y = (size.y-height)/2;
        if (x<0) x = 0;
        if (y<0) y = 0;
        if (layout.mobile){
            x = 20;
            y = 0;
        }

        var _self = this;
        MWF.require("MWF.xDesktop.Dialog", function() {
            MWF.require("MWF.widget.ImageClipper", function(){
                var dlg = new MWF.xDesktop.Dialog({
                    "title": this.lp.changePersonIcon,
                    "style": "image",
                    "top": y,
                    "left": x - 20,
                    "fromTop": y,
                    "fromLeft": x - 20,
                    "width": width,
                    "height": height,
                    "html": "<div></div>",
                    "maskNode": this.content,
                    "container": this.content,
                    "buttonList": [
                        {
                            "text": MWF.LP.process.button.ok,
                            "action": function () {
                                //_self.uploadPersonIcon();
                                _self.image.uploadImage( function( json ){
                                    _self.action.getPerson(function(json){
                                        if (json.data){
                                            this.personData = json.data;
                                            _self.contentImgNode.set("src", _self.action.getPersonIcon());
                                        }
                                        this.close();
                                    }.bind(this));
                                }.bind(this), null );
                            }
                        },
                        {
                            "text": MWF.LP.process.button.cancel,
                            "action": function () {
                                _self.image = null;
                                this.close();
                            }
                        }
                    ]
                });
                dlg.show();

                this.image = new MWF.widget.ImageClipper(dlg.content.getFirst(), {
                    "aspectRatio": 1,
                    "description" : "",
                    "imageUrl" : this.action.getPersonIcon(),
                    "resetEnable" : false,

                    "data": null,
                    "parameter": null,
                    "action": this.action.action,
                    "method": "changeIcon"
                });
                this.image.load();
            }.bind(this));
        }.bind(this))
    },
    uploadPersonIcon: function(){
        if (this.image){
            if( this.image.getResizedImage() ){

                this.action.changeIcon(function(){
                    this.action.getPerson(function(json){
                        if (json.data){
                            this.personData = json.data;
                            //if (this.personData.icon){
                            this.contentImgNode.set("src", this.action.getPersonIcon());
                            //}
                        }
                    }.bind(this))
                }.bind(this), null, this.image.getFormData(), this.image.resizedImage);
            }
        }
    },

    savePersonInfor: function(){
        this.personData.officePhone = this.officePhoneInputNode.get("value");
        this.personData.mail = this.mailInputNode.get("value");
        this.personData.mobile = this.mobileInputNode.get("value");
        this.personData.weixin = this.weixinInputNode.get("value");
        this.personData.qq = this.qqInputNode.get("value");
        this.personData.signature = this.signatureInputNode.get("value");
        this.action.updatePerson(this.personData, function(){
            this.notice(this.lp.saveInforOk, "success");
        }.bind(this));
    },

    loadDesktopBackground: function(UINode){
        var currentSrc = layout.desktop.options.style;
        MWF.UD.getDataJson("layoutDesktop", function(json){
            if (json) currentSrc = json.src;
        }.bind(this), false);

        MWF.getJSON(layout.desktop.path+"styles.json", function(json){
            json.each(function(style){
                var img = MWF.defaultPath+"/xDesktop/$Layout/"+style.style+"/preview.jpg";
                //var dskImg = MWF.defaultPath+"/xDesktop/$Layout/"+style.style+"/desktop.jpg";
                var imgArea = new Element("div.o2_profile_previewBackground").inject(UINode);
                if (currentSrc==style.style){
                    imgArea.setStyles({"border": "4px solid #ffea00"});
                }
                new Element("img", {"src": img}).inject(imgArea);

                imgArea.store("dskimg", style.style);
                var _self = this;
                imgArea.addEvent("click", function(){ _self.selectDesktopImg(this, UINode); });
            }.bind(this));
        }.bind(this));
    },
    selectDesktopImg: function(item, UINode){
        var desktopImg = item.retrieve("dskimg");
        MWF.UD.putData("layoutDesktop", {"src": desktopImg}, function(){
            UINode.getChildren().each(function(node){
                node.setStyles({"border": "4px solid #eeeeee"});
            }.bind(this));
            item.setStyles({"border": "4px solid #ffea00"});

            var dskImg = MWF.defaultPath+"/xDesktop/$Layout/"+desktopImg+"/desktop.jpg";
            layout.desktop.node.setStyle("background-image", "url("+dskImg+")");
        }.bind(this));
    },
    changePassword: function(){
        var oldPassword = this.oldPasswordInputNode.get("value");
        var password = this.passwordInputNode.get("value");
        var morePassword = this.morePasswordInputNode.get("value");

        if( !this.checkPassowrdStrength( password, true ) ){
            return false;
        }
        if (password!=morePassword){
            this.notice(this.lp.passwordNotMatch, "error");
            this.passwordInputNode.setStyles(this.css.inforContentInputNode_error);
            this.morePasswordInputNode.setStyles(this.css.inforContentInputNode_error);
        }else{
            this.action.changePassword(oldPassword, password, morePassword, function(){
                this.oldPasswordInputNode.set("value", "");
                this.passwordInputNode.set("value", "");
                this.morePasswordInputNode.set("value", "");
                this.notice(this.lp.changePasswordOk, "success");
            }.bind(this));

            if (layout.config.mail){
                var url = "http://"+layout.config.mail+"//names.nsf?changepassword&password="+encodeURIComponent(oldPassword)+"&passwordnew="+encodeURIComponent(password)+"&passwordconfirm="+encodeURIComponent(password);
                var iframe = new Element("iframe", {"styles": {"display": "none"}}).inject(this.desktop.desktopNode);
                iframe.set("src", url);
                window.setTimeout(function(){
                    iframe.destroy();
                }.bind(this), 2000);
            }
        }
    },

    getAction: function(callback){
        if (!this.acrion){
            this.action = MWF.Actions.get("x_organization_assemble_personal");
            if (callback) callback();
        }else{
            if (callback) callback();
        }
    },
    checkPassowrdStrength : function( password, notice ){
        var str = "";
        this.getAction( function( ){
            this.action.checkPassword( password, function( json ){
                str = json.data.value || "";
            }.bind(this), null, false );
        }.bind(this) );
        if( str ){
            var areaNode = this.content.getElement(".o2_profile_passwordStrengthArea");
            areaNode.show();
            var passwordStrengthNode = this.content.getElement(".o2_profile_passwordRemindNode");
            passwordStrengthNode.set("text", this.lp.passwordRule + ":" + str);
            if(notice)this.notice( this.lp.passwordRule + ":" + str, "error");
            return false;
        }else{
            //var areaNode = this.content.getElement(".o2_profile_passwordStrengthArea");
            //areaNode.hide();
        }
        return true;
    }
    //createPasswordStrengthNode : function(){
    //    var passwordStrengthArea = this.passwordRemindContainer;
    //
    //    var lowNode = new Element( "div", {styles : this.css.passwordStrengthNode }).inject( passwordStrengthArea );
    //    this.lowColorNode = new Element( "div", {styles : this.css.passwordStrengthColor }).inject( lowNode );
    //    this.lowTextNode = new Element( "div", {styles : this.css.passwordStrengthText, text : this.lp.weak }).inject( lowNode );
    //
    //    var middleNode = new Element( "div" , {styles : this.css.passwordStrengthNode }).inject( passwordStrengthArea );
    //    this.middleColorNode = new Element( "div", {styles : this.css.passwordStrengthColor }).inject( middleNode );
    //    this.middleTextNode = new Element( "div", {styles : this.css.passwordStrengthText, text : this.lp.middle }).inject( middleNode );
    //
    //    var highNode = new Element("div", {styles : this.css.passwordStrengthNode }).inject( passwordStrengthArea );
    //    this.highColorNode = new Element( "div", {styles : this.css.passwordStrengthColor }).inject( highNode );
    //    this.highTextNode = new Element( "div", {styles : this.css.passwordStrengthText, text : this.lp.high }).inject( highNode );
    //},
    //getPasswordLevel: function( password, callback ){
    //    /*Level（级别）
    //     •0-3 : [easy]
    //     •4-6 : [midium]
    //     •7-9 : [strong]
    //     •10-12 : [very strong]
    //     •>12 : [extremely strong]
    //     */
    //    this.getAction( function( ){
    //        this.action.checkPassword( password, function( json ){
    //            if(callback)callback( json.data.value );
    //        }.bind(this), null, false );
    //    }.bind(this) );
    //},
    //checkPassowrdStrength: function(pwd){
    //    var i = (this.inBrowser)? 2 : 3;
    //    var passwordStrengthNode = this.tab.pages[i].contentNode.getElement(".o2_profile_passwordStrengthArea");
    //    var nodes = passwordStrengthNode.getElements(".o2_profile_passwordStrengthColor");
    //    var lowColorNode = nodes[0];
    //    var middleColorNode = nodes[1];
    //    var highColorNode = nodes[2];
    //    nodes = passwordStrengthNode.getElements(".o2_profile_passwordStrengthText");
    //    var lowTextNode = nodes[0];
    //    var middleTextNode = nodes[1];
    //    var highTextNode = nodes[2];
    //
    //    lowColorNode.removeClass("o2_profile_passwordStrengthColor_low");
    //    middleColorNode.removeClass("o2_profile_passwordStrengthColor_middle");
    //    highColorNode.removeClass("o2_profile_passwordStrengthColor_high");
    //    lowTextNode.removeClass("o2_profile_passwordStrengthText_current");
    //    middleTextNode.removeClass("o2_profile_passwordStrengthText_current");
    //    highTextNode.removeClass("o2_profile_passwordStrengthText_current");
    //
    //    if (pwd==null||pwd==''){
    //    }else{
    //        this.getPasswordLevel( pwd, function( level ){
    //            switch(level) {
    //                case 0:
    //                case 1:
    //                case 2:
    //                case 3:
    //                    lowColorNode.addClass("o2_profile_passwordStrengthColor_low");
    //                    lowTextNode.addClass("o2_profile_passwordStrengthText_current");
    //                    break;
    //                case 4:
    //                case 5:
    //                case 6:
    //                    middleColorNode.addClass("o2_profile_passwordStrengthColor_middle");
    //                    middleTextNode.addClass("o2_profile_passwordStrengthText_current");
    //                    break;
    //                default:
    //                    highColorNode.addClass("o2_profile_passwordStrengthColor_high");
    //                    highTextNode.addClass("o2_profile_passwordStrengthText_current");
    //            }
    //        }.bind(this) )
    //
    //    }
    //}
});
/*----2019年8月30日---外出授权---表单*/
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xApplication.Profile.emPowerPopupForm = new Class({
    Extends : MPopupForm,
    options: {
        "style": "empower",
        "width": "800",
        "height": "500",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "draggable": true,
        "maxAction" : true,
        "closeAction": true,
        "isFull" : false,
        "startTime" : null,
        "endTime" : null,
        "isWholeday" : false,
        "defaultCalendarId" : ""
    },
    load: function () {
        //重新指定css文件，由于临时用，所以尽可能写一个文件里
        this.cssPath = "/x_component_Profile/$Main/"+this.options.style+"/css.wcss";
        this._loadCss();
       // console.log(this.css);
        this.configData = this.options.configData||{};
        this.lp = this.lp.empower;
        //this.addEvent("queryOk",this.queryOk());

    },
    _createTableContent : function() {

        this.userName = layout.desktop.session.user.distinguishedName;
        this.userId = layout.desktop.session.user.id;
        this.userIdentityList = layout.desktop.session.user.identityList;
        var identityTextList = [];
        var identityList =[];
        this.userIdentityList.each(function(it){
            identityTextList.include(it.name+"("+it.unitName+")");
            identityList.include((it.distinguishedName));
        });

        this.formTableContainer.setStyle("width","80%");

        var startTime, endTime, defaultStartDate, defaultStartTime, defaultEndDate, defaultEndTime;
        if( this.data.startTime && this.data.endTime ){
            startTime= this.date = typeOf( this.data.startTime )=="string" ? Date.parse( this.data.startTime ) : this.data.startTime;
            endTime= typeOf( this.data.endTime )=="string" ? Date.parse( this.data.endTime ) : this.data.endTime;
            defaultStartDate = startTime.format("%Y-%m-%d");
            defaultStartTime = startTime.format("%H:%M");
            defaultEndDate = endTime.format("%Y-%m-%d");
            defaultEndTime = endTime.format("%H:%M");
        }else{
            startTime = this.date = new Date().increment("hour",1);
            endTime = startTime.clone().increment("hour",1);
            defaultStartDate = startTime.format("%Y-%m-%d");
            defaultStartTime = startTime.format("%H") + ":00";
            defaultEndDate = endTime.format("%Y-%m-%d");
            defaultEndTime = endTime.format("%H") + ":00";
        }
        var data={};
        this.formTableArea.set("html", this.getHtml());

        MWF.xDesktop.requireApp("Template", "MForm", function () {

                this.form = new MForm(this.formTableArea, data, {
                    isEdited: this.isEdited || this.isNew,
                    style : "profile",
                    itemTemplate: {
                        fromPerson: { text: this.lp.fromPerson,type: "select", isEdited : (identityTextList.length>1),
                            selectText: identityTextList,
                            selectValue: identityList,
                            defaultValue: identityList[0],
                            style:{
                                "background-color": "rgb(238, 238, 238)",
                                "box-shadow": "rgb(204, 204, 204) 0px 0px 6px",
                                "min-height": "30px",
                                "overflow":" hidden"
                            }
                            },
                        toPerson: { text: this.lp.toPerson,type: "org", isEdited : this.isEdited || this.isNew, orgType: ["identity"], count : 1, orgWidgetOptions : {
                                "onLoadedInfor": function(item){
                                   // this.loadAcceptAndReject( item );
                                }.bind(this)
                            },defaultValue:this.data.toPerson},
                        startDateInput: {
                            text: this.lp.startTime,
                            tType: "date",
                            defaultValue: defaultStartDate,
                            notEmpty: true
                        },
                        startTimeInput: {
                            tType: "time",
                            defaultValue: defaultStartTime,
                            className: ((this.isNew || this.isEdited || 1) ? "inputTimeUnformatWidth" : ""),
                            disable: data.isAllDayEvent
                        },
                        endDateInput: {
                            text: this.lp.completedTime,
                            tType: "date",
                            defaultValue: defaultEndDate,
                            notEmpty: true
                        },
                        endTimeInput: {
                            tType: "time",
                            defaultValue: defaultEndTime,
                            className: ((this.isNew || this.isEdited || 1) ? "inputTimeUnformatWidth" : ""),
                            disable: data.isAllDayEvent
                        },
                        type: {
                            text: this.lp.type, type: "radio",
                            selectText: [this.lp.type_all, this.lp.type_application, this.lp.type_process],
                            //selectText: ["全部", "应用", "流程"],
                            selectValue: ["all", "application", "process"],
                            defaultValue: this.data.type||"all",
                            event :{
                                "click":function(){
                                    //console.log(this);
                                    var type = this.form.getItem("type").getValue();
                                    this.formTableArea.getElement("td[item=application]").parentNode.setStyle("display","none");
                                    this.formTableArea.getElement("td[item=process]").parentNode.setStyle("display","none");
                                    if(type=="all"){
                                        //this.formTableArea.getElement("td[item=application]").parentNode.setStyle("display","");
                                        //this.formTableArea.getElement("td[item=process]").parentNode.setStyle("display","");
                                    }else{
                                        this.formTableArea.getElement("td[item="+type+"]").parentNode.setStyle("display","");
                                    }
                                }.bind(this)
                            }
                        }
                    }
                },this.app,this.css);
            this.form.load();
            //console.log("this.form.cssPath=====",this.form.css);
        }.bind(this));

        this.applicationNode = this.formTableArea.getElement("td[item=application]");
        //this.applicationNode.empty();
        /*console.log("222222222---2222222222");
        console.log(this.selectApplicationNode);*/
        this.selectApplicationNode = new Element("div.selectNode", {
            width: "30",
            height: "30",
        }).inject(this.applicationNode).setStyles(this.css.selectNode);
        this.showApplicationNode = new Element("div.showNode", {
            width: "200",
            height: "30",
           // text: "选择应用"
        }).inject(this.applicationNode).setStyles(this.css.showNode);
        this.createApplicationSelect(this.showApplicationNode,this.selectApplicationNode,"Application");
        this.processNode = this.formTableArea.getElement("td[item=process]");

        this.selectProcessNode = new Element("div.selectNode", {
            width: "30",
            height: "30",
        }).inject(this.processNode).setStyles(this.css.selectNode);
        this.showProcessNode = new Element("div.showNode", {
            width: "200",
            height: "30",
            //text: "选择流程"
        }).inject(this.processNode).setStyles(this.css.showNode);
        this.createApplicationSelect(this.showProcessNode,this.selectProcessNode,"Process");

    },
    _ok: function (data, callback) {
        //data 是表单的数据， callback 是正确的回调
        //data.
        //console.log(data,this.data);
        var submitData = [];
        //数据处理
        var sdata = {};
        sdata.fromIdentity = data.fromPerson;
        sdata.fromPerson = data.fromPerson.split("@")[0];
        sdata.toIdentity = data.toPerson;
        sdata.toPerson = data.toPerson.split("@")[0];
        sdata.startTime = data.startDateInput+" "+data.startTimeInput+":00";
        sdata.completedTime = data.endDateInput+" "+data.endTimeInput+":00";
        sdata.enable = true;

        if(data.type=="all"){
            sdata.type = data.type;
            submitData.push(sdata);
        }else if(data.type=="application"){
            this.configData["Application"].forEach(function(item){
                var subData = JSON.parse(JSON.stringify(sdata));
                subData["application"] = item.id;
                subData["applicationName"] = item.name;
                subData["applicationAlias"] = item.alias;
                subData.type = "application";
                submitData.push(subData);
            });
        }else if(data.type=="process"){

            this.configData["Process"].forEach(function(item){
                var subData =  JSON.parse(JSON.stringify(sdata));
               // console.log(subData);
                subData["process"] = item.id;
                subData["processName"] = item.name;
                subData["processAlias"] = item.alias;
                subData.type = "process";
                submitData.push(subData);
            });
        }
        //console.log(submitData);
        /*this.saveConfigData=[];
        this.saveConfigCount = submitData.length;*/
        var submitCount=0;
        submitData.forEach(function(item){
            if(this.data.id&&submitCount==0){
                this.actions.editEmPower(this.data.id,item,function(json){
                    submitCount++;
                }.bind(this),null,false);
            }else{
                this.actions.createEmPower(item,function(json){
                    submitCount++;
                }.bind(this),null,false);
            }

        }.bind(this));
        if(!submitData.length){
            this.app.notice( this.lp.alert1 ,"error");
        }

        if(submitCount>0){
            var content = this.container.getElement(".o2_profile_emPower_tab");
            content.getElement("table").empty();
            this.app.loadMyEmPower();
        }
        //console.log(submitCount,submitData.length);
        if(submitData.length&&submitCount==submitData.length){
            //this.close();
            this.app.notice(this.lp.saveOk,"success");
            this.close();
        }else if(submitCount>0){
            this.app.notice(this.lp.saveNotAll,"error");
            this.close();
        }
    },
    createApplicationSelect : function(node,selectNode,type){
        //console.log(this);
        if (this.configData[type]&&this.configData[type].length){
            MWF.require("MWF.widget.O2Identity", function(){
                var p = new MWF.widget.O2Process(this.configData[type][0], node);
            }.bind(this));
        }else{
            this.configData[type]=[];
        }
        selectNode.addEvent("click", function(){
            MWF.xDesktop.requireApp("Selector", "package", function(){
                //console.log(this.configData.process);
                var options = {
                    "type": type,
                    "values": this.configData[type],
                    "count": 0,
                    "onComplete": function (items) {
                        node.empty();
                        this[type] = {};
                        this.configData[type] = [];
                        //console.log(type);
                        //console.log(this.configData[type]);
                        items.forEach(function(item,indext){

                            MWF.require("MWF.widget.O2Identity", function(){
                                var p = new MWF.widget.O2Process(item.data, node);
                                this[type] = {
                                    "name": item.data.name,
                                    "id": item.data.id,
                                    "application": item.data.application,
                                    "applicationName": item.data.applicationName,
                                    "alias": item.data.alias
                                };
                                this.configData[type].include(this[type]);
                                //console.log(this.configData.process);
                            }.bind(this));
                        }.bind(this));
                        //console.log(this.configData[type]);
                    }.bind(this)
                };

                var selector = new MWF.O2Selector(this.container, options);

            }.bind(this));
        }.bind(this));
    },
    getHtml : function(){
        return  "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable'>" +
            //"<tr><td colspan='2' styles='formTableHead'>申诉处理单</td></tr>" +

            "<tr style='display:"+ (this.userIdentityList.length>1?"":"none")+"'><td styles='formTableTitleRight' lable='fromPerson'></td>" +
            "    <td styles='formTableValue' colspan='2'>" +
            "       <div item='fromPerson'></div>" +
            "   </td>" +
            "</tr>" +
            "<tr><td styles='formTableTitleRight' lable='toPerson'></td>" +
            "    <td styles='formTableValue' colspan='2'>" +
            "       <div item='toPerson'></div>" +
            //"       <div item='selecttoPerson'></div>" +
            "   </td>" +
            "</tr>" +
            "<tr><td styles='formTableTitleRight' width='100' lable='startDateInput'></td>" +
            "    <td styles='formTableValue' item='startDateInput' width='300'></td>" +
            "    <td styles='formTableValue' item='startTimeInput'></td>" +
            "</tr>" +
            "<tr><td styles='formTableTitleRight' lable='endDateInput'></td>" +
            "    <td styles='formTableValue' item='endDateInput'></td>" +
            "    <td styles='formTableValue' item='endTimeInput'></td>" +
            "</tr>" +
            "<tr><td styles='formTableTitleRight' ></td>" +
            "    <td styles='formTableValue' item='type' colspan='2'></td>" +
            "</tr>" +
            "<tr style='display:"+(this.data.type=="application"?"":"none")+"'><td styles='formTableTitleRight' lable='application'>"+this.lp.application+"</td>" +
            "    <td styles='formTableValue1' item='application' colspan='2'></td>" +
            "</tr>" +
            "<tr style='display:"+(this.data.type=="process"?"":"none")+"'><td styles='formTableTitleRight' lable='process'>"+this.lp.process+"</td>" +
            "    <td styles='formTableValue1' item='process' colspan='2'></td>" +
            "</tr>" +
            "</table>";
    }
});