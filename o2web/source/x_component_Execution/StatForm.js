MWF.xApplication.Execution = MWF.xApplication.Execution || {};

MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("Execution", "WorkForm", null, false);

MWF.xApplication.Execution.StatForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        //"centerWorkId" : "fc44be47-7271-469f-8f04-deebdb71d3e6",
        "style": "default",
        "width": "90%",
        "height": "90%",
        "hasTop": true,
        "hasIcon": false,
        "hasBottom": true,
        "title": "",
        "draggable": false,
        "closeAction": true,
        "isNew": false,
        "isEdited": true
    },
    initialize: function (explorer, actions, data, options) {
        this.setOptions(options);
        this.explorer = explorer;
        this.app = explorer.app;
        this.lp = this.app.lp.statForm;
        this.actions = this.app.restActions;
        this.path = "/x_component_Execution/$StatForm/";
        this.cssPath = this.path + this.options.style + "/css.wcss";
        this._loadCss();

        this.options.title = this.lp.title;

        this.data = data || {};

        this.actions = actions;
    },
    load: function () {
        this.centerWorkId = this.options.centerWorkId;
        this.centerWorkData = {};
        this.getCenterWork(this.centerWorkId);

        if (this.options.isNew) {
            this.create();
        } else if (this.options.isEdited) {
            this.edit();
        } else {
            this.open();
        }
        this.resizeWindow();
        this.app.addEvent("resize", function(){
            this.resizeWindow();
        }.bind(this));
    },
    resizeWindow: function(){
        var size = this.formNode.getSize();
        var rW = this.formTopContentNode.getWidth();
        this.formTopTextNode.setStyles({"width":(size.x-rW-200)+"px"});

    },
    //*****************common function**************************************
    getCenterWork: function(centerWorkId){
        this.actions.getMainTask(centerWorkId,function(json){
            this.centerWorkData = json.data;
        }.bind(this),function(xhr,text,error){
            this.showErrorMsg(xhr,text,error)
        }.bind(this),false)
    },
    //*****************common function**************************************

    createTopNode: function () {
        if (!this.formTopNode) {
            this.formTopNode = new Element("div.formTopNode", {
                "styles": this.css.formTopNode
            }).inject(this.formNode);

            this.formTopIconNode = new Element("div", {
                "styles": this.css.formTopIconNode
            }).inject(this.formTopNode);

            this.formTopTextNode = new Element("div.formTopTextNode", {
                "styles": this.css.formTopTextNode,
                "text": this.centerWorkData.title ? this.centerWorkData.title: this.options.title
            }).inject(this.formTopNode);

            if (this.options.closeAction) {
                this.formTopCloseActionNode = new Element("div.formTopCloseActionNode", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
                this.formTopCloseActionNode.addEvent("click", function () {
                    this.close()
                }.bind(this))
            }

            this.formTopContentNode = new Element("div.formTopContentNode", {
                "styles": this.css.formTopContentNode
            }).inject(this.formTopNode);

            this._createTopContent();

        }
    },
    _createTopContent: function () {
        if(this.centerWorkData.title){
            var html = "<span style='font-size:14px;'>"+this.lp.topTitle.drafter+":</span>" +
                "    <span style='font-size:14px;'>"+this.centerWorkData.creatorName.split("@")[0]+"</span>"+
                "   <span style='font-size:14px;margin-left:10px;'>"+this.lp.topTitle.department+":</span>"+
                "   <span style='font-size:14px;'>"+this.centerWorkData.creatorUnitName.split("@")[0]+"</span>"+
                "   <span style='font-size:14px;margin-left:10px;'>"+this.lp.topTitle.createTime+":</span>"+
                "   <span style='font-size:14px;'>"+this.centerWorkData.createTime.split(" ")[0]+"</span>";

            this.formTopContentNode.set("html", html);
        }

    },
    _createTableContent: function (data) {

        this.createWeeklyList();
        //this.createStatView();

    },
    createWeeklyList: function(){
        this.weeklyContentDiv = new Element("div.weeklyContentDiv", {
            "styles": this.css.weeklyContentDiv
        }).inject(this.formTableArea);


        var weeklyTitleDiv = new Element("div.weeklyTitleDiv", {
            "styles": this.css.weeklyTitleDiv,
            "text": this.lp.weeklyTitle
        }).inject(this.weeklyContentDiv);

        this.weeklyListDiv = new Element("div.weeklyListDiv", {
            "styles": this.css.weeklyListDiv
        }).inject(this.weeklyContentDiv);

        this.loadWeeklyList();
    },

    loadWeeklyList: function(){
        var filterData = {
            "centerId":this.centerWorkData.id,
            "order":"DESC"
        };
        this.actions.getStatDateList(filterData,function(json){
                if(json.type == "success"){
                    this.weeklyData = json.data
                }
            }.bind(this),
            function(xhr,text,error){
                this.showErrorMsg(xhr,text,error)
            }.bind(this),false
        );

        if(!this.weeklyData || this.weeklyData.length==0){
            var weeklyListLi = new Element("li.weeklyListLi",{
                "styles":this.css.weeklyListLi,
                "text": "未生成汇报统计"
            }).inject(this.weeklyListDiv);
            return false;
        }
        this.weeklyData.each(function(d,i){
            var weeklyListLi = new Element("li.weeklyListLi",{
                "styles":this.css.weeklyListLi,
                "text": d.datetime,
                "title": d.reportCycle
            }).inject(this.weeklyListDiv);
            weeklyListLi.addEvents({
                "click":function(){
                    this.weeklyListDiv.getElements("li").setStyles({"background-color":"","color":""});
                    weeklyListLi.setStyles({"background-color":"#3c76c1","color":"#ffffff"});
                    this.displayDateStat(d);
                    this.currentWeeklyLi = weeklyListLi;
                    this.currentWeeklyData = d;
                }.bind(this),
                "mouseover":function(){
                    weeklyListLi.setStyles({"border":"1px solid #3c76c1"})
                }.bind(this),
                "mouseout":function(){
                    weeklyListLi.setStyles({"border":""})
                }.bind(this)
            });
            if(i==0){
                this.currentWeeklyLi = weeklyListLi;
            }
        }.bind(this));






        //var sendData = {}
        //sendData.centerId = this.centerWorkId
        //sendData.reportCycle = "每周统计"
        //this.actions.getStatListForCenterWork(sendData,function(json){
        //    if(json.data){
        //        this.weeklyData = json.data;
        //    }
        //}.bind(this),function(xhr,text,error) {
        //    this.showErrorMsg(xhr,text,error)
        //}.bind(this),false)

        //if(!this.weeklyData) return false;
        //this.weeklyData.each(function(d,index){
        //    //alert(JSON.stringify(d))
        //    if(d.week){
        //        var statDate = d.statisticTime.split(" ")[0]
        //        var weeklyListLi = new Element("li.weeklyListLi",{
        //            "styles":this.css.weeklyListLi,
        //            "text": statDate
        //        }).inject(this.weeklyListDiv);
        //        weeklyListLi.addEvents({
        //            "click":function(){
        //                this.weeklyListDiv.getElements("li").setStyles({"background-color":"","color":""})
        //                weeklyListLi.setStyles({"background-color":"#3c76c1","color":"#ffffff"})
        //                this.createStatView(d.id);
        //                this.currentWeeklyLi = weeklyListLi
        //                this.currentWeeklyData = d;
        //            }.bind(this),
        //            "mouseover":function(){
        //                weeklyListLi.setStyles({"border":"1px solid #3c76c1"})
        //            }.bind(this),
        //            "mouseout":function(){
        //                weeklyListLi.setStyles({"border":""})
        //            }.bind(this)
        //        })
        //        if(index==0){
        //            this.currentWeeklyLi = weeklyListLi;
        //        }
        //    }
        //}.bind(this))

        if(this.currentWeeklyLi) this.currentWeeklyLi.click()
    },
    createStatView : function(d){

        if(this.statViewDiv) this.statViewDiv.destroy();
        this.statViewDiv = new Element("div.statViewDiv", {
            "styles": this.css.statViewDiv
        }).inject(this.formTableArea);

        var statViewTitleDiv = new Element("div.statViewTitleDiv", {
            "styles": this.css.statViewTitleDiv,
            "text": this.lp.statViewTitle
        }).inject(this.statViewDiv);

        this.statViewListDiv = new Element("div.statViewListDiv", {
            "styles": this.css.statViewListDiv
        }).inject(this.statViewDiv);

        this.loadStatView(d)

    },

    displayDateStat: function(d,id){
        if(this.statViewListDiv) this.statViewListDiv.empty();
        if(d){
            var filterData = {
                "statisticTimeFlag": d.datetime,
                "centerId":this.centerWorkData.id
            };
            if(this.statViewListDiv) this.statViewListDiv.set("text","loading...");

            this.actions.getStatDate(filterData,function(json){
                    if(json.type == "success"){
                        this.dateStatData = json.data;
                        this.displayDateStatTable()
                    }
                }.bind(this),
                function(xhr,text,error){
                    this.showErrorMsg(xhr,text,error)
                }.bind(this),true
            )
        }
    },
    displayDateStatTable:function(){
        if(this.dateStatData){
            if(this.statViewListDiv) this.statViewListDiv.destroy();
            this.statViewListDiv = new Element("div.statViewListDiv", {
                "styles": this.css.statViewListDiv
            }).inject(this.formTableArea);

            //var y = this.formTableArea.getSize().y - this.weeklyContentDiv.getSize().y
            //alert(y)
            //this.statViewListDiv.setStyles({"height":(y-50)+"px"})

            this.statTable = new Element("table.statTable",{
                "styles":this.css.statTable
            }).inject(this.statViewListDiv);
            //this.statTable.set("border","1")

            this.statHeadTr = new Element("tr.statHeadTr",{
                "styles":this.css.statHeadTr
            }).inject(this.statTable);


            for(var o in this.lp.statTable){
                var statHeadTd = new Element("td.statHeadTd",{
                    "styles": this.css.statHeadTd,
                    "text":this.lp.statTable[o]
                }).inject(this.statHeadTr);
            }

            this.dateStatData.each(function(d,i){
                if(d.contents && d.contents.length>0){
                    d.contents.each(function(dd,ii){
                        var baseTr = new Element("tr.baseTr").inject(this.statTable);

                        for(var o in this.lp.statTable){
                            //if(o!="order"){
                            var val = "";
                            if(o=="opinions") {
                                if(dd[o]){
                                    dd[o].each(function(ddd){
                                        val = val + ddd.processorName.split("@")[0] + "：\n"+ ddd.opinion +"\n"
                                    })
                                }
                            }else if(o == "responsibilityUnitName"){
                                val = dd[o];
                                val = val.split("@")[0];
                            }else{
                                if(dd[o])val = dd[o]
                            }
                            var baseTd = new Element("td.dateStatBaseTd",{
                                "styles": this.css.dateStatBaseTd,
                                "html": val.length>50?val.substring(0,50)+"...":val,
                                "title":val
                            }).inject(baseTr);
                            if(o=="serialNumber"){
                                baseTd.setStyles({"width":"35px","text-align":"center"})
                            }
                            if(o=="responsibilityUnitName"){
                                baseTd.setStyles({"width":"87px"});
                                val = val.split("@")[0]
                            }
                            if(o=="workDetail"){

                            }
                        }


                    }.bind(this))
                }
            }.bind(this))
        }
    },










    loadStatView: function(d){
        this.statTable = new Element("table.statTable",{
            "styles":this.css.statTable
        }).inject(this.statViewListDiv);
        //this.statTable.set("border","1")

        this.statHeadTr = new Element("tr.statHeadTr",{
            "styles":this.css.statHeadTr
        }).inject(this.statTable);


        for(var o in this.lp.statTable){
            var statHeadTd = new Element("td.statHeadTd",{
                "styles": this.css.statHeadTd,
                "text":this.lp.statTable[o]
            }).inject(this.statHeadTr);
        }

        if(!parentWorkId) parentWorkId = "(0)";
        this.actions.getStatByWorkId(id,parentWorkId,function(json){
            json.data.each(function(d,index){
                var statBodyTr = new Element("tr.statBodyTr",{
                    "styles": this.css.statBodyTr
                }).inject(this.statTable);

                var statBodyTd = new Element("td.statBodyTd",{
                    "styles": this.css.statBodyTd,
                    "text":(index+1),
                    "id": d.workId
                }).inject(statBodyTr);
                statBodyTd.setStyles({"width":"35px","text-align":"center"});
                for(var o in this.lp.statTable){
                    if(o!="order"){
                        var val = "";
                        if(o=="opinions") {
                            if(d[o]){
                                d[o].each(function(dd){
                                    val = val + dd.processorName.split("@")[0] + "：<br>"+ dd.opinion +"<br>"
                                })
                            }
                        }else{
                            val = d[o]
                        }
                        statBodyTd = new Element("td.statBodyTd",{
                            "styles": this.css.statBodyTd,
                            "html": val.length>50?val.substring(0,50)+"...":val,
                            "title":val
                        }).inject(statBodyTr);
                        if(o=="organizationName"){
                            statBodyTd.setStyles({"width":"87px"})
                        }
                        if(o=="workDetail"){
                            //statBodyTd.setStyles({"cursor":"pointer","color":"#3d77c1","text-decoration":"underline"})
                            statBodyTd.addEvents({
                                "click":function(){
                                    this.loadSubStat(id, d.workId,statBodyTr);
                                    //this.actions.getStatByWorkId(id, d.workId,function(json){
                                    //
                                    //}.bind(this),function(xhr,text,error){
                                    //    this.showErrorMsg(xhr,text,error)
                                    //}.bind(this),false)
                                }.bind(this)
                            })
                        }
                    }
                }


            }.bind(this))
        }.bind(this),function(xhr,text,error){
            this.showErrorMsg(xhr,text,error)
        }.bind(this),false)
    },
    loadSubStat:function(id,parentWorkId,position){
        var curPosition = position.getElementById(parentWorkId).get("text");

        this.actions.getStatByWorkId(id,parentWorkId,function(json){
                this.subStatData = json.data;
                json.data.each(function(d,index){
                    if(document.getElementById(d.workId)) {
                        return false;
                    }

                    var statBodyTr = new Element("tr.statBodyTr",{
                        "styles": this.css.statBodyTr
                    }).inject(position,"after");

                    var statBodyTd = new Element("td.statBodyTd",{
                        "styles": this.css.statBodyTd,
                        "text":curPosition + "."+(this.subStatData.length-index),
                        "id": d.workId
                    }).inject(statBodyTr);

                    for(var o in this.lp.statTable){
                        if(o!="order"){
                            var val = "";
                            if(o=="opinions") {
                                if(d[o]){
                                    d[o].each(function(dd){
                                        val = val + dd.processorName.split("@")[0] + "：<br>"+ dd.opinion +"<br>"
                                    })
                                }
                            }else{
                                val = d[o]
                            }
                            statBodyTd = new Element("td.statBodyTd",{
                                "styles": this.css.statBodyTd,
                                "html": val.length>50?val.substring(1,50)+"...":val,
                                "title":val
                            }).inject(statBodyTr);
                            if(o=="workDetail"){
                                //statBodyTd.setStyles({"cursor":"pointer","color":"#3d77c1","text-decoration":"underline"})
                                statBodyTd.addEvents({
                                    "click":function(){
                                        this.loadSubStat(id, d.workId);
                                    }.bind(this)
                                })
                            }
                        }
                    }


                }.bind(this))
        }.bind(this),function(xhr,text,error){
                this.showErrorMsg(xhr,text,error)
            }.bind(this),false
        )
    },


    _createBottomContent: function () {
        if(this.formBottomNode) this.formBottomNode.empty();
        this.closeActionNode = new Element("div.closeActionNode", {
            "styles": this.css.formActionNode,
            "text": this.lp.actions.close
        }).inject(this.formBottomNode);

        this.closeActionNode.addEvents({
            click:function(){
                this.close()
            }.bind(this)
        });

        this.exportActionNode = new Element("div.exportActionNode", {
            "styles": this.css.formActionNode,
            "text": this.lp.actions.export
        }).inject(this.formBottomNode);

        this.exportActionNode.addEvents({
            click:function(){
                var sendData = {};
                sendData.centerId = this.centerWorkId;
                sendData.statisticTimeFlag = this.currentWeeklyData.datetime;
                sendData.reportCycle = this.currentWeeklyData.reportCycle;
                this.actions.exportByCenterWork(sendData,function(json){
                        if(json.data && json.data.id){
                            var address = this.actions.action.address;

                            var url = address + "/jaxrs/export/statisticreportcontent/"+json.data.id+"/stream"
                            window.open(url)
                        }
                    }.bind(this),
                    function(xhr,text,error){
                        this.showErrorMsg(xhr,text,error)
                    }.bind(this),false)

            }.bind(this)
        })

    },
    showErrorMsg: function(xhr,text,error){
        var errorText = error;
        if (xhr) errorMessage = xhr.responseText;
        var e = JSON.parse(errorMessage);
        if (e.message) {
            this.app.notice(e.message, "error");
        } else {
            this.app.notice(errorText, "error");
        }
    }
});
