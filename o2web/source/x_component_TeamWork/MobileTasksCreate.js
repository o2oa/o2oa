    MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
    MWF.xDesktop.requireApp("TeamWork", "Common", null, false);
    
    MWF.xApplication.TeamWork.MobileTasksCreate = new Class({
        Extends: MWF.widget.Common,
        Implements: [Options, Events],
        options: {
            "style": "default"
        },
        initialize: function (container, app, mobile, data,options ) { 
            this.setOptions(options);
            this.container = container;
    
            this.app = app;
            this.mobile = mobile;
            this.lp = this.app.lp.mobile.taskCreate;
    
            this.rootActions = this.app.rootActions;
            this.actions = this.rootActions.ProjectAction;
    
            this.parentData = data;
            this.mobile.setRouter("taskCreate");
            this.load();
        },
        load: function () {
            this.data = {
                project:this.parentData.project,
                parent:this.parentData.id,
                taskGroupId:this.parentData.taskGroupId,
                important:"高",
                urgency:"高",
            };



            this.container.empty();
            
            this.loadTop();
            this.content = new Element("div.tw-np-content").inject(this.container);
            this.bottom = new Element("div.tw-common-bottom").inject(this.container);
            this.loadContent();
            this.loadBottom();
        },
        
        loadContent:function(){
            //任务名称
            var nameNode = new Element('div.tw-nt-item').inject(this.content);
            new Element("div.tw-nt-item-title",{text:this.lp.name}).inject(nameNode);
            var nameValueNode = new Element("div.tw-nt-item-value").inject(nameNode);
            this.taskName = new Element("input",{placeHolder:this.lp.namePlacHolder}).inject(nameValueNode);
            new Element('div.tw-nt-item-icon').inject(nameNode);

            //结束日期
            var endTimeNode = new Element('div.tw-nt-item').inject(this.content);
            new Element("div.tw-nt-item-title",{text:this.lp.endDate}).inject(endTimeNode);
            var endTimeValueNode = new Element("div.tw-nt-item-value").inject(endTimeNode);
            this.taskEndTime = new Element("input",{placeHolder:this.lp.endDatePlaceHolder,readonly:"readonly"}).inject(endTimeValueNode);
            new Element('div.tw-nt-item-icon.o2-tw-riqi').inject(endTimeNode);
            endTimeNode.addEvent('click',function(){
                var opt = { type:"date" };
                this.mobile.selectCalendar(this.taskEndTime,opt,function(json){
                    this.data.endTime = json.dateString ||"";
                }.bind(this))
            }.bind(this));

            //负责人
            var executorNode = new Element('div.tw-nt-item.tw-np-item').inject(this.content);
            new Element("div.tw-nt-item-title",{text:this.lp.executor}).inject(executorNode);
            var executortValueNode = new Element("div.tw-nt-item-value").inject(executorNode);
            this.taskExecutor = new Element("div",{text:this.lp.executorPlaceHolder,styles:{"color":"#777777"}}).inject(executortValueNode);
            new Element('div.tw-nt-item-icon.o2-tw-user-plus',{styles:{"color":"#4A90E2"}}).inject(executorNode);

            executorNode.addEvent("click",function(){
                var idList = this.mobile.getIdentityByPerson([this.data.executor]);
                this.mobile.selectPerson("identity",1,idList,function(personList){
                    if(personList.length>0){
                        this.taskExecutor.empty();
                        this.data.executor = personList[0];
                        var user = personList[0].split("@")[0];
                        var userLayout = new Element("div.tw-common-user-layout").inject(this.taskExecutor);
                        new Element("div.tw-common-user-icon",{text:user.substring(0,1)}).inject(userLayout);
                        new Element("div",{text:user}).inject(userLayout);
                    }
                }.bind(this));
            }.bind(this));

        },
        loadTop:function(){
            var node = new Element("div.tw-common-top").inject(this.container);
            var back = new Element("div.tw-common-top-back.o2-tw-jiantou-you").inject(node);
            new Element("div.tw-common-top-title",{text:this.lp.createSubTask}).inject(node);
            new Element("div.tw-common-top-right").inject(node);

            back.addEvent('click',function(){
                //window.history.back()
                this.mobile.back()
            }.bind(this))
        },
        loadBottom:function(){
            var confirm = new Element("div.tw-common-bottom-item-ok.tw-common-bottom-item-two",{text:this.app.lp.mobile.common.ok}).inject(this.bottom);
            var close = new Element("div.tw-common-bottom-item-close.tw-common-bottom-item-two",{text:this.app.lp.mobile.common.cancel}).inject(this.bottom);

            confirm.addEvent('click',function(){



                var name = this.taskName.get("value").trim();
                
                if(name ==""){
                    this.app.notice( this.lp.nameNotEmpty,"error");
                    return;
                }
                this.data.name = name;
                this.mobile.setMobileCover();
                this.rootActions.TaskAction.save(this.data,function(json){ 
                    this.mobile.clearMobileCover();
                    this.mobile.back();
                }.bind(this))

            }.bind(this));

            close.addEvent('click',function(){
                this.mobile.back()
            }.bind(this));
        }

    
    });
