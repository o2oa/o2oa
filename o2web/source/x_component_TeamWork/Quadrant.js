/*
    this.data //project的数据
 */

MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xDesktop.requireApp("TeamWork", "Common", null, false);

MWF.xApplication.TeamWork.Quadrant = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
		"mvcStyle": "style.css"

    },
    initialize: function (container, app, data, options) {
        this.setOptions(options);
        this.container = container;

        this.app = app;
        this.lp = this.app.lp.Quadrant;
        this.rootActions = this.app.rootActions;
        this.actions = this.rootActions.ProjectAction;
        //this.taskActions = this.rootActions.TaskAction;

        this.path = "../x_component_TeamWork/$Quadrant/";
        this.cssPath = this.path+this.options.style+"/css.wcss";
        this._loadCss();

        this.data = data;
    },
    load: function () {
        
        this.container.empty();
        var content = new Element("div",{styles:{
            "width":"100%",
            "height":"100%"
        }}).inject(this.container);
        var url = this.path+this.options.style+"/view.html";
        content.loadAll({"html":url,css:this.path + this.options.style  + "/" + "style.css"},{"bind": {"lp": this.lp}, "module": this},function(){ 
            this.loadTaskList("first");
            this.loadTaskList("second");
            this.loadTaskList("third");
            this.loadTaskList("fourth");
            
        }.bind(this))
		        
    },
    loadTaskListAll:function(category){
        var _category = category || "all";
        var index = _category == "all" ? 1 : 0;
        this.categoryContainer_first.getElements("div").removeClass("categoryItemCur");
        this.categoryContainer_first.getElements("div")[index].addClass('categoryItemCur');

        this.categoryContainer_second.getElements("div").removeClass("categoryItemCur");
        this.categoryContainer_second.getElements("div")[index].addClass('categoryItemCur');

        this.categoryContainer_third.getElements("div").removeClass("categoryItemCur");
        this.categoryContainer_third.getElements("div")[index].addClass('categoryItemCur');

        this.categoryContainer_fourth.getElements("div").removeClass("categoryItemCur");
        this.categoryContainer_fourth.getElements("div")[index].addClass('categoryItemCur');


        this.loadTaskList('first',category);
        this.loadTaskList('second',category);
        this.loadTaskList('third',category);
        this.loadTaskList('fourth',category);
    },
    loadTaskList:function(quadrant,category){ 
        var container = this.firstQuadrantContainer;
        if(quadrant == 'first'){
            container = this.firstQuadrantContainer;
        }else if(quadrant == 'second'){
            container = this.secondQuadrantContainer;
        }else if(quadrant == 'third'){
            container = this.thirdQuadrantContainer;
        }else if(quadrant == 'fourth'){
            container = this.fourthQuadrantContainer;
        }
        container.empty();

        // if(e){
        //     var _parent = e.currentTarget.getParent();
        //     _parent.getElements("div").removeClass("categoryItemCur");
        //     e.currentTarget.addClass('categoryItemCur')
        // }

        var _category = category || "all";

        this.app.setLoading(container);
        this.getTaskList(quadrant,_category,function(json){
            container.empty();
            json.forEach(function(d){
                this.createTaskItem(container,d)
            }.bind(this))
        }.bind(this))
    },
    createTaskItem:function(container,data){
        var taskItem = new Element("div.taskItem").inject(container);
        new Element("div.taskTitle",{text:data.name}).inject(taskItem);
        var _status = this.app.lp.task.status;
        new Element("div.taskStatus",{text:_status[data.workStatus]}).inject(taskItem);
        taskItem.addEvent("click",function(){
            this.openTask(data.id)
        }.bind(this))
        
    },
    getTaskList:function(quadrant,category,callback){
        var _high = this.lp.high;
        var _low = this.lp.low;

        var _important = _high;
        var _urgency = _high;
        if(quadrant == "second"){
            _urgency = _low
        }else if(quadrant == 'third'){
            _important = _low
        }else if(quadrant == 'fourth'){
            _important = _low;
            _urgency = _low;
        }
        
        var _type = false;
        if(category == 'my') _type = true;

        this.rootActions.TaskAction.listFourQuadrantTask(this.data.id,_important,_urgency,200,_type,function(json){
            if(callback)callback(json.data)
        })
    },
    openTask:function(id,e,data){ 
        var data = {
            taskId:id
        };
        var opt = {
            "onPostClose":function(){ 
                //刷新
                this.reload();
            }.bind(this)
        };
        MWF.xDesktop.requireApp("TeamWork", "Tasks", function(){
            var task = new MWF.xApplication.TeamWork.Tasks(this,data,opt,{projectObj:this});
            task.open();
        }.bind(this));
    },
    reload:function(){ 
        this.container.empty();
        this.load();
    }
    
});

