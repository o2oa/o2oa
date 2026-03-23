    MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
    MWF.xDesktop.requireApp("TeamWork", "Common", null, false);
    
    MWF.xApplication.TeamWork.MobileProjectCreate = new Class({
        Extends: MWF.widget.Common,
        Implements: [Options, Events],
        options: {
            "style": "default"
        },
        initialize: function (container, app, mobile, options ) {
            this.setOptions(options);
            this.container = container;
    
            this.app = app;
            this.mobile = mobile;
            this.lp = this.app.lp.mobile.projectCreate;
    
            this.rootActions = this.app.rootActions;
            this.actions = this.rootActions.ProjectAction;
    
            this.data = {};
            this.mobile.setRouter("projectCreate");
            this.load();

        },
        load: function () {
            this.container.empty();
            
            this.loadTop();
            this.content = new Element("div.tw-np-content").inject(this.container);
            this.bottom = new Element("div.tw-common-bottom").inject(this.container);
            this.loadContent();
            this.loadBottom();
        },
        
        loadContent:function(){
            //项目名称
            var nameNode = new Element('div.tw-np-item').inject(this.content);
            new Element("div.tw-np-item-title",{text:this.lp.name}).inject(nameNode);
            var nameValueNode = new Element("div.tw-np-item-value").inject(nameNode);
            this.projectName = new Element("input",{placeHolder:this.lp.namePlaceHolder}).inject(nameValueNode);

            //项目来源
            var sourceNode = new Element('div.tw-np-item').inject(this.content);
            new Element("div.tw-np-item-title",{text:this.lp.source}).inject(sourceNode);
            var sourceValueNode = new Element("div.tw-np-item-value").inject(sourceNode);
            this.projectSource = new Element("input",{placeHolder:this.lp.sourcePlaceHolder}).inject(sourceValueNode);

            //项目目标
            var objectiveNode = new Element('div.tw-np-item.tw-np-item-mult').inject(this.content);
            new Element("div.tw-np-item-title-mult",{text:this.lp.objective}).inject(objectiveNode);
            var objectiveValueNode = new Element("div.tw-np-item-value-mult").inject(objectiveNode);
            this.projectObjective = new Element("textarea",{placeHolder:this.lp.objectivePlaceHolder}).inject(objectiveValueNode);
            this.projectObjective.addEvent('input',function(){ 
                var v = this.projectObjective.get("value");
                this.mobile.setMobileHeightAuto(this.projectObjective,v,30,20);
            }.bind(this));
            
            //项目背景与问题
            var descriptionNode = new Element('div.tw-np-item.tw-np-item-mult').inject(this.content);
            new Element("div.tw-np-item-title-mult",{text:this.lp.description}).inject(descriptionNode);
            var descriptionValueNode = new Element("div.tw-np-item-value-mult").inject(descriptionNode);
            this.projectDescription= new Element("textarea",{placeHolder:this.lp.descriptionPlaceHolder}).inject(descriptionValueNode);
            this.projectDescription.addEvent('input',function(){ 
                var v = this.projectDescription.get("value");
                this.mobile.setMobileHeightAuto(this.projectDescription,v,25,20);
            }.bind(this));

            //项目成员
            var participantListNode = new Element('div.tw-np-item.tw-np-item-mult').inject(this.content);
            new Element("div.tw-np-item-title-mult",{text:this.lp.cyz}).inject(participantListNode);
            var participantListValueNode = new Element("div.tw-np-item-value-mult").inject(participantListNode);
            this.projectParticipantList = new Element("textarea",{placeHolder:this.lp.cyzPlaceHolder,readonly:"readonly"}).inject(participantListValueNode);

            
            this.projectParticipantList.addEvent("click",function(){ 
                this.app.selectPerson("identity",0,this.data.participantList,function(personList){
                    this.data.participantList = personList;
                    var _person = [];
                    this.data.participantList.forEach(function(p){
                        _person.push(p.split("@")[0])
                    })
                    this.projectParticipantList.set("text",_person.join(','));
                }.bind(this))
            }.bind(this));

            //项目周期
            var cycleNode = new Element('div.tw-np-item').inject(this.content);
            new Element("div.tw-np-item-title",{text:this.lp.cycle}).inject(cycleNode);
            var cycleValueNode = new Element("div.tw-np-item-value").inject(cycleNode);
            var timeNode = new Element("div.tw-np-item-cycle").inject(cycleValueNode);
            this.projectStartTime = new Element("input.tw-np-item-time",{placeHolder:this.lp.startDate,readonly:"readonly"}).inject(timeNode);
            new Element("div",{text:" - "}).inject(timeNode);
            this.projectEndTime = new Element("input.tw-np-item-time",{placeHolder:this.lp.endDate,readonly:"readonly"}).inject(timeNode);
            new Element("div.tw-np-item-icon.o2-tw-riqi").inject(cycleValueNode);

            var now = new Date();
            var startTime = this.app.formatDateV2(now,"date");
            this.projectStartTime.set("value",startTime);
            this.data.startTime = startTime + " 00:00:00";
            this.projectEndTime.addEvent('click',function(){
                var opt = { type:"date" };
                this.app.selectCalendar(this.projectEndTime,this.container,opt,function(json){
                    if(json.action == "ok"){
                        this.projectEndTime.set("value",json.dateString);
                        this.data.endTime = json.dateString + " 23:59:59"
                    }else if(json.action == "clear"){
                        this.projectEndTime.set("value","");
                        this.data.endTime = ''
                    }
                }.bind(this))
            }.bind(this))
        },
        loadTop:function(){
            var node = new Element("div.tw-common-top").inject(this.container);
            var back = new Element("div.tw-common-top-back.o2-tw-jiantou-you").inject(node);
            new Element("div.tw-common-top-title",{text:this.lp.create}).inject(node);
            new Element("div.tw-common-top-right").inject(node);

            back.addEvent('click',function(){
                //window.history.back()
                this.mobile.back()
            }.bind(this))
        },
        loadBottom:function(){
            var confirm = new Element("div.tw-common-bottom-item-ok.tw-common-bottom-item-two",{text:this.lp.ok}).inject(this.bottom);
            var close = new Element("div.tw-common-bottom-item-close.tw-common-bottom-item-two",{text:this.lp.close}).inject(this.bottom);

            confirm.addEvent('click',function(){
                var title = this.projectName.get("value");
                var source = this.projectSource.get("value")||"";
                var objective = this.projectObjective.get("value")||"";
                var description = this.projectDescription.get("value")||"";
                var participantList = this.data.participantList||[];
                var startTime = this.data.startTime||"";
                var endTime = this.data.endTime||"";
                
                if(title ==""){
                    this.app.notice( this.lp.nameNotEmpty,"error");
                    return;
                }

                var data = {
                    title:title,
                    source:source,
                    objective:objective,
                    description:description,
                    participantList:participantList,
                    startTime:startTime,
                    endTime:endTime
                }
                
                this.mobile.setMobileCover();
                this.rootActions.ProjectAction.save(data,function(json){ 
                    this.mobile.clearMobileCover();
                    MWF.xDesktop.requireApp("TeamWork", "MobileProject", function(){ 
                        new MWF.xApplication.TeamWork.MobileProject(this.container,this.app,this.mobile,{"id":json.data.id});
                    }.bind(this))
                }.bind(this));

            }.bind(this));

            close.addEvent('click',function(){
                this.mobile.back()
            }.bind(this));
        }

    
    });
