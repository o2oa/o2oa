MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xApplication.TeamWork.TaskMove = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": 600,
        "height": 450,
        "top": 100,
        "left": null,
        "bottom" : null,
        "right" : null,
        "minWidth" : 300,
        "minHeight" : 220,

        "isLimitSize": true,
        "ifFade": false,
        "hasTop": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "hasIcon": false,
        "hasBottom": false,
        "hasMask" : true,
        "closeByClickMask" : true,
        "hasScroll" : false,
        "scrollType" : "",

        "title": "",
        "draggable": false,
        "resizeable" : false,
        "maxAction" : false,
        "closeAction": false,

        "relativeToApp" : true,
        "sizeRelateTo" : "app", //desktop
        "resultSeparator" : ","
    },
    initialize: function (explorer, data, options, para) {
        this.setOptions(options);
        this.explorer = explorer;
        this.app = this.explorer.app;
        this.container = this.app.content;
        this.lp = this.app.lp.taskMove;
        //this.actions = this.explorer.actions || this.app.actions || this.app.rectActions;
        this.rootActions = this.app.rootActions;
        this.actions = this.rootActions.TaskAction;

        this.projectObj = this.explorer;
        if(para){
            if(para.projectObj) this.projectObj = para.projectObj;
        }

        this.data = data || {};
        this.cssPath = "/x_component_TeamWork/$TaskMove/"+this.options.style+"/css.wcss";

        this.load();
    },
    _setFormNodeSize:function(){
        var pos = this.formAreaNode.getPosition();
        var _height = this.formAreaNode.getHeight().toInt();
        var _width = this.formAreaNode.getWidth().toInt();
        var _top = pos.y+_height/2;
        var _left = pos.x + _width/2;
        this.formAreaNode.setStyles({"width":"0px","height":"0px","left":_left+"px","top":_top+"px","overflow":"hidden"});


        var time = 250;
        var rad = _width/_height;
        var fx = new Fx.Tween(this.formAreaNode,{duration:time});
        fx.start(["width"] ,"0px", _width+"px");
        var fx1 = new Fx.Tween(this.formAreaNode,{duration:time});
        fx1.start(["height"] ,"0px", _height+"px");
        var fx2 = new Fx.Tween(this.formAreaNode,{duration:time});
        fx2.start(["left"] ,_left+"px", pos.x+"px");
        var fx3 = new Fx.Tween(this.formAreaNode,{duration:time});
        fx3.start(["top"] ,_top+"px", pos.y+"px");

    },
    _createTableContent: function () {

        // this.formTableArea
        this.topLayout = new Element("div.topLayout",{styles:this.css.topLayout}).inject(this.formTableArea);
        this.createTopLayout();
        this.contentLayout = new Element("div.contentLayout",{styles:this.css.contentLayout}).inject(this.formTableArea);
        this.createContentLayout();
        this.bottomLayout = new Element("div.bottomLayout",{styles:this.css.bottomLayout}).inject(this.formTableArea);
        this.createBottomLayout();
    },
    createTopLayout:function(){
        this.topLayout.empty();
        this.topLayout.set("text",this.lp.title)
    },
    createContentLayout:function(){
        this.contentLayout.empty();

        this.taskGroupLayout = new Element("div.taskGroupLayout",{styles:this.css.taskGroupLayout}).inject(this.contentLayout);
        this.taskGroupLayout.addEvents({
            scroll:function(){
                var stop = this.taskGroupLayout.getScrollTop();
                var cheight= this.taskGroupLayout.getSize().y;
                var sheight = this.taskGroupLayout.getScrollHeight();
                var borderWidth = this.taskGroupLayout.getBorder()["border-top-width"].toInt()+this.taskGroupLayout.getBorder()["border-bottom-width"].toInt();
                if(sheight == stop + cheight-borderWidth && this.isLoaded && this.curCount < this.total){
                    this.loadTaskGroup(this.listId);
                }
            }.bind(this)
        });
        this.loadTaskGroup()
    },
    loadTaskGroup:function(id,key){
        var tmploading = new Element("div.loading",{styles:{"width":"500px"}}).inject(this.taskGroupLayout);
        this.app.setLoading(tmploading);
        this.taskGroupLayout.scrollTo(0,this.taskGroupLayout.getScrollSize().y);
        var id = this.groupId = id||"(0)";
        var count=10;
        var filter = {
            project:this.data.data.project
        };
        if(key && key!=""){
            filter.title = key;
        }
        this.total = this.total || 0;
        this.curCount = this.curCount || 0;
        this.isLoaded = false;

        //this.data.data.project
        this.rootActions.TaskListAction.listWithTaskGroup(this.data.data.taskGroupId,function(json){
            tmploading.destroy();
            json.data.each(function(d){
                this.loadGroupItem(d);
            }.bind(this))
        }.bind(this));
    },

    loadGroupItem:function(data){
        var _self = this;
        var groupItem = new Element("div.groupItem",{styles:this.css.groupItem,id:data.id}).inject(this.taskGroupLayout);
        groupItem.addEvents({
            mouseover:function(){
                if(_self.selectedItem == this)return;
                this.setStyles({"background-color":"#f2f5f7"})
            },
            mouseout:function(){
                if(_self.selectedItem == this)return;
                this.setStyles({"background-color":""})
            },
            click:function(){
                if(_self.selectedItem){
                    _self.selectedItem.setStyles({"background-color":""});
                    _self.selectedItem.getElements(".groupName").setStyles({"color":"#666666"});
                }
                this.setStyles({"background-color":"#3da8f5"});
                this.getElements(".groupName").setStyles({"color":"#ffffff"});
                _self.okAction.setStyles({
                    "cursor":"pointer",
                    "background-color":"#4A90E2"
                });
                _self.selectedItem = this;
            }
        });
        var groupName = new Element("div.groupName",{styles:this.css.groupName,text:data.name}).inject(groupItem);
        // var n = data.executor.split("@")[0];
        // n = n.substr(0,1);
        //
        // var taskPerson = new Element("div.taskPerson",{styles:this.css.taskPerson,text:n}).inject(taskItem);
    },
    createBottomLayout:function(){

        this.bottomLayout.empty();
        this.okAction = new Element("div.okAction",{styles:this.css.okAction,text:this.lp.ok}).inject(this.bottomLayout);
        this.okAction.addEvents({
            click:function(){
                if(this.selectedItem){
                    if(this.selectedItem.get("id")==this.data.data.taskListId){
                        this.app.notice(this.lp.moveToSelf,"info");
                        return;
                    }

                    var data = {
                        taskId:this.data.data.id
                    }

                    this.rootActions.TaskListAction.addTask2ListWithBehindTask(this.selectedItem.get("id"),data,function(json){
                        if(json.data.id){
                            //this.app.notice(json.data.message,"success");
                            this.projectObj.reloadTaskGroup(json.data.id);  //reload to list
                            this.projectObj.reloadTaskGroup(this.data.data.taskListId); //reload from list

                            //this.explorer.projectObj.reloadTaskGroup(json.data.id);
                            //this.explorer.projectObj.reloadTaskGroup(this.data.data.taskListId);
                            // if(this.explorer.explorer){
                            //     this.explorer.explorer.reloadTaskGroup(json.data.id);
                            //     this.explorer.explorer.reloadTaskGroup(this.data.data.taskListId);
                            // }
                        }else{
                            this.app.notice("不允许转移到未分类列表","error")
                        }
                        this.close();
                    }.bind(this))
                }
            }.bind(this)
        })
    }


});