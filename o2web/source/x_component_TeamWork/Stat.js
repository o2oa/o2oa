MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};

MWF.xApplication.TeamWork.Stat = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function (container, app) {
        
        this.container = container;

        this.app = app; 
        this.lp = this.app.lp.stat;
        this.rootActions = this.app.rootActions;
        this.actions = this.rootActions.StatAction;

        this.path = "../x_component_TeamWork/$Stat/";
        this.cssPath = this.path+this.options.style+"/css.wcss";
        this._loadCss();

        // if (this.options.mvcStyle) this.stylePath = this.path + this.options.style + "/" + this.options.mvcStyle;
/*
* 蓝色 #4a90e2
* 灰色 #666666
*/
        //this.load();
    },
    load: function () { 
        var _self = this;
        this.container.empty(); 
        this.filterData = {};

        this.layout = new Element("div.statlayout",{styles:this.css.layout}).inject(this.container);
        this.naviLayout = new Element("div.statnaviLayout",{styles:this.css.naviLayout}).inject(this.layout);
        this.naviMyTask = new Element("div.statnaviMyTask",{styles:this.css.naviItem,text:this.lp.myTask}).inject(this.naviLayout);
        this.naviMyManagerTask = new Element("div.statnaviMyTask",{styles:this.css.naviItem,text:this.lp.myManagerTask}).inject(this.naviLayout);

        this.naviMyTask.addEvents({
            "click":function(){ 
                this.setStyles({
                    "background-color":"#ffffff"
                });
                _self.naviMyManagerTask.setStyles({
                    "background-color":""
                })
                _self.changeTab("my");
            }
        })
        this.naviMyManagerTask.addEvents({
            "click":function(){
                this.setStyles({
                    "background-color":"#ffffff"
                });
                _self.naviMyTask.setStyles({
                    "background-color":""
                })
                _self.changeTab("myManager")
            }
        })

        this.statContent = new Element("div.statContent",{styles:this.css.statContent}).inject(this.layout);
        
        var _html = this.path + this.options.style+"/view.html";
		this.cssFile = this.path + this.options.style  + "/style.css";
        this.statContent.loadAll({"html":_html,css:this.cssFile},{"bind": {"lp": this.lp}, "module": this},function(){ 
            
			//时间
            var nowTime = new Date();
            var lastTime = new Date(nowTime - 1000 * 60 * 60 * 24 * 30);
            this.startTime.set("text",this.app.formatDateV2(lastTime,"date"));
            this.filterData.startTime = this.app.formatDateV2(lastTime,"date");
            this.startTime.addEvents({
                click:function(){ 
                    var opt = {
                        type:"date"
                    };
                    this.app.selectCalendar(this.startTime,this.container,opt,function(json){
                        if(json.action == "ok"){
                            this.startTime.set("text",json.dateString);
                            this.filterData.startTime = json.dateString+" 00:00:00"
                        }else if(json.action == "clear"){
                            this.startTime.set("text","");
                            this.filterData.startTime = ''
                        }
                    }.bind(this))
                }.bind(this)
            })
            this.endTime.set("text",this.app.formatDateV2(nowTime,"date"));
            this.filterData.endTime = this.app.formatDateV2(nowTime,"date");
            this.endTime.addEvents({
                click:function(){ 
                    var opt = {
                        type:"date"
                    };
                    this.app.selectCalendar(this.endTime,this.container,opt,function(json){
                        if(json.action == "ok"){
                            this.endTime.set("text",json.dateString);
                            this.filterData.endTime = json.dateString+" 23:59:59"
                        }else if(json.action == "clear"){
                            this.endTime.set("text","");
                            this.filterData.endTime = ''
                        }
                    }.bind(this))
                }.bind(this)
            })
            
            this.naviMyTask.click();

            //图标
            //this.loadChat()
			
        }.bind(this))

    },
    changeTab:function(t){ 
        //切换标签，初始化过滤条件
        var nowTime = new Date();
        var lastTime = new Date(nowTime - 1000 * 60 * 60 * 24 * 30);
        this.startTime.set("text",this.app.formatDateV2(lastTime,"date"));
        this.filterData.startTime = this.app.formatDateV2(lastTime,"date") + " 00:00:00";

        this.endTime.set("text",this.app.formatDateV2(nowTime,"date"));
        this.filterData.endTime = this.app.formatDateV2(nowTime,"date") + " 23:59:59";
        
        this.filterProject.empty();
        this.filterData.projectList = [];
        
        if(t == "myManager"){
            this.filterData.mode = "1"
        }else if(t == "my"){
            this.filterData.mode = "2"
        }
        
        this.stat();
       
    },
    stat:function(){
        this.rootActions.StatAction.statTask(this.filterData,function(json){
            var data = json.data;
            this.taskTotalData = data.allCount; //任务总数
            this.taskTotal.set("text",this.taskTotalData);

            this.taskCompletedData = data.completedNormalCount; //已完成任务数-按时完成
            this.taskCompleted.set("text",this.taskCompletedData);

            this.taskCompleted_overtimeData = data.completedOverTimeCount; //已完成任务数-逾期完成
            this.taskCompleted_overtime.set("text",this.taskCompleted_overtimeData);
            
            this.taskFlowData = data.processingCount; //进行中的任务
            this.taskFlow.set("text",this.taskFlowData);

            this.taskDelayData = data.delayCount; //已搁置任务数
            this.taskDelay.set("text",this.taskDelayData);

            this.taskCanceledData = data.deleteCount; //已取消的任务数
            this.taskCanceled.set("text",this.taskCanceledData);

            this.taskOvertimeData = data.overTimeCount; //逾期总数

            
            if((parseInt(this.taskTotalData) - parseInt(this.taskCanceledData)) == 0){
                this.closeRateData = '';
                this.closeRate.set('text',this.closeRateData);
                this.closeRateOKData = '';
                this.closeOKRate.set('text',this.closeRateOKData);
                this.overtimeRateData = '';
                this.overtimeRate.set('text',this.overtimeRateData);
            }else{
                //关闭率 = 已完成任务按时+已完成逾期任务/总任务-已取消的任务
                this.closeRateData = (parseInt(this.taskCompletedData) + parseInt(this.taskCompleted_overtimeData)) /(parseInt(this.taskTotalData) - parseInt(this.taskCanceledData));
                this.closeRateData = this.closeRateData * 100;
                this.closeRateData = Math.round(this.closeRateData) + "%";
                this.closeRate.set('text',this.closeRateData);
                //关闭及时率 = 已完成任务按时/总任务-已取消的任务
                this.closeRateOKData = (parseInt(this.taskCompletedData) ) /(parseInt(this.taskTotalData) - parseInt(this.taskCanceledData));
                this.closeRateOKData = this.closeRateOKData * 100;
                this.closeRateOKData = Math.round(this.closeRateOKData) + "%";
                this.closeOKRate.set('text',this.closeRateOKData);
                //逾期率 = 逾期任务总数/总任务-已取消的任务
                this.overtimeRateData = (parseInt(this.taskOvertimeData) ) /(parseInt(this.taskTotalData) - parseInt(this.taskCanceledData));
                this.overtimeRateData = this.overtimeRateData * 100;
                this.overtimeRateData = Math.round(this.overtimeRateData) + "%";
                this.overtimeRate.set('text',this.overtimeRateData);
            }
            

            this.chatData = [
                this.taskTotalData,
                this.taskCompletedData,
                this.taskCompleted_overtimeData,
                this.taskFlowData,
                this.taskDelayData,
                this.taskCanceledData
            ];

            this.loadChat()

        }.bind(this))
    },
    loadChat:function(){ 
        var _self = this;
        this.statChatContent.empty();
        var chatContainer = new Element("div",{styles:this.css.chatContainer}).inject(this.statChatContent);
        o2.load("../o2_lib/echarts/echarts.min.js",function(){
            var _nameList = [
                this.lp.taskTotal,
                this.lp.taskCompleted,
                this.lp.taskCompleted_overtime,
                this.lp.taskFlow,
                this.lp.taskDelay,
                this.lp.taskCanceled,
            ];
            _nameList.reverse();
            var chatData = this.chatData.reverse();
            var bar = echarts.init(chatContainer);
            var options = {
                title: {
                    text: '任务统计清单',
                    textStyle:{
                        "color":"#666666",
                        "font-size":"20px",
                        "text-align":"center"
                    },
                    left:"50%"
                  },
                  tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                      type: 'shadow'
                    }
                  },
                  legend: {},
                  grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                  },
                  xAxis: {
                    type: 'value',
                    boundaryGap: [0, 0.01]
                  },
                  yAxis: {
                    type: 'category',
                    axisLabel: {
                        textStyle: {
                          show:true,
                            fontSize: '16',
                        },                          
                    },
                    data: _nameList
                  },
                  series: [
                    {
                        name: '',
                        type: 'bar',
                        data: chatData,
                        
                        itemStyle: {
                            normal: {
                                //这里是颜色
                                color: function(params) {  
                                    if(params.dataIndex == 5){
                                        return "#01B0F1"
                                    }else{
                                        return "#5E97E8"
                                    }
                                    
                                },
                                label:{
                                    formatter:function(params){
                                        var res = params.value;
                                        if(params.name == _self.lp.taskCompleted){
                                            //关闭及时率
                                            res = res + " ; " + _self.closeRateOKData
                                        }
                                        return res
                                    },
                                    show:true,
                                    fontSize:18,
                                    position:"right"
                                }
                            }
                        }
                    }
                  ]
            }
            bar.setOption(options);
        }.bind(this))
    },
    selectProject:function(){
        var opt = {
            "onPostClose":function(dd){ 
                if(!dd) return;
                this.loadProjectSelected(dd.value)
                this.stat()
            }.bind(this)
        }
        var ps = new MWF.xApplication.TeamWork.Stat.ProjectSelect(this,{},opt);
        ps.open();
    },
    loadProjectSelected:function(data){ 
        var _self = this;
        this.filterProject.empty();
        data.forEach(function(d){
            var pitem = new Element("div",{styles:this.css.projectSelectItem,id:d.id,title:d.title}).inject(this.filterProject);
            new Element('span',{styles:this.css.projectSelectItemTitle,text:d.title}).inject(pitem);
            pitem.addEvents({
                click:function(e){
                    this.destroy();

                    var index = _self.filterData.projectList.indexOf(d.id);
                    _self.filterData.projectList.splice(index, 1);
                    _self.stat()
                    e.stopPropagation()
                }
                
            })
            this.filterData.projectList.push(d.id)
        }.bind(this))
    },
    test:function(){
        alert("")
    }


});


MWF.xApplication.TeamWork.Stat.ProjectSelect = new Class({
    Extends: MWF.xApplication.TeamWork.Common.Popup,
    options:{
        "width": 500,
        "height": 600,
        "closeByClickMask" : false
    },
    open: function (e) {
        //设置css 和 lp等
        var css = this.css;
        this.cssPath = "../x_component_TeamWork/$Stat/"+this.options.style+"/css.wcss";
        this._loadCss();
        if(css) this.css = Object.merge(  css, this.css );

        this.path = "../x_component_TeamWork/$Stat/";


        this.rootActions = this.app.rootActions;
        this.actions = this.rootActions.ProjectAction;

        this.lp = this.app.lp.stat;

        this.fireEvent("queryOpen");
        this.isNew = false;
        this.isEdited = false;
        this._open();
        this.fireEvent("postOpen");
    },
    _createTableContent: function () {
        //this.explorer
        //debugger
        var _self = this;
        this.formTableArea.setStyles({"width":"100%","height":"100%"});
        this.psContent = new Element("div.psContent",{styles:this.css.psContent}).inject(this.formTableArea);
        this.psContent.addEvents({
            scroll:function(){
                var clientHeight = this.clientHeight;
                var scrollTop = this.scrollTop;
                var scrollHeight = this.scrollHeight;
                
                if (clientHeight + scrollTop >= (scrollHeight - 10) && !_self.listStatus) { 
                    _self.listStatus = false;
                    _self.loadProjectListNext()
                }

            }
        })
        this.loadProjectList();
        
        this.psAction = new Element("div.psAction",{styles:this.css.psAction}).inject(this.formTableArea);
        var confirm = new Element("div",{styles:this.css.psActionButton,text:this.app.lp.common.ok}).inject(this.psAction);
        confirm.addEvents({
            "click":function(){
                var values = [];
                var selectList = this.psContent.getElements("input:checked");
                selectList.forEach(function(item){
                    values.push({
                        "id":item.get("value"),
                        "title":item.getNext().get("text")
                    })
                });
                this.close({"value":values})
            }.bind(this)
        })
        var cancel = new Element("div",{styles:this.css.psActionButton,text:this.app.lp.common.cancel}).inject(this.psAction);
        cancel.addEvents({
            "click":function(){
                this.close();
            }.bind(this)
        })
    },
    loadProjectList:function(){ 
        this.curPage = 1;
        this.pageSize = 20;
        this.filterData = {};
        this.htmlFile = this.path+this.options.style+"/ps.html";
        this.cssFile = this.path + this.options.style  + "/style.css";
        this.actions.listPageWithFilter(this.curPage,this.pageSize,this.filterData,function(json){
            var data = json.data;
            this.pageTotal = json.count; //总数
            
            this.psContent.loadAll({"html":this.htmlFile,"css":this.cssFile},{"bind":{"lp":this.lp,"data":data},"module":this},function(){
                
                if(this.curPage * this.pageSize > this.pageTotal) this.listStatus = true;
                else this.listStatus = false;
                
            }.bind(this))

        }.bind(this))
    },
    loadProjectListNext:function(){
        this.listStatus = true;
        this.curPage ++;
        this.actions.listPageWithFilter(this.curPage,this.pageSize,this.filterData,function(json){
            var data = json.data;
            
            this.psContent.loadAll({"html":this.htmlFile,"css":this.cssFile},{"bind":{"lp":this.lp,"data":data},"module":this},function(){
                
                if(this.curPage * this.pageSize > this.pageTotal) this.listStatus = true;
                else this.listStatus = false;
            }.bind(this))

        }.bind(this))
    }

});