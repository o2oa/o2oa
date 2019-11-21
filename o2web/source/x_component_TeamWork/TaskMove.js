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
        this.actions = this.explorer.actions || this.app.actions || this.app.rectActions;

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



        this.formTableArea
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
        this.searchDiv = new Element("div.searchDiv",{styles:this.css.searchDiv}).inject(this.contentLayout);
        this.searchInput = new Element("input.searchInput",{styles:this.css.searchInput,placeHolder:this.lp.searchPlace}).inject(this.searchDiv);
        this.searchInput.addEvents({
            keyup:function(e){
                var keycode = (e.event.keyCode ? e.event.keyCode : e.event.which);
                var key = this.searchInput.get("value").trim();
                if(keycode == 13 && key !=""){
                    this.total = 0;
                    this.curCount = 0;
                    this.searchReset.show();
                    this.taskListLayout.empty();
                    this.loadTaskList(null,key);
                    this.createBottomLayout();
                    delete this.selectedItem;
                }
            }.bind(this)
        });
        this.searchReset = new Element("div.searchReset",{styles:this.css.searchReset}).inject(this.searchDiv);
        this.searchReset.addEvents({
            mouseover:function(){ this.setStyles({"background-image":"url(/x_component_TeamWork/$TaskSub/default/icon/icon_off_click.png)"}) },
            mouseout:function(){ this.setStyles({"background-image":"url(/x_component_TeamWork/$TaskSub/default/icon/icon_off.png)"}) },
            click:function(){
                this.total = 0;
                this.curCount = 0;
                this.searchInput.set("value","");
                this.searchReset.hide();
                this.taskListLayout.empty();
                this.loadTaskList();
                this.createBottomLayout();
                delete this.selectedItem;
            }.bind(this)
        });
        this.taskListLayout = new Element("div.taskListLayout",{styles:this.css.taskListLayout}).inject(this.contentLayout);
        this.taskListLayout.addEvents({
            scroll:function(){
                var stop = this.taskListLayout.getScrollTop();
                var cheight= this.taskListLayout.getSize().y;
                var sheight = this.taskListLayout.getScrollHeight();
                var borderWidth = this.taskListLayout.getBorder()["border-top-width"].toInt()+this.taskListLayout.getBorder()["border-bottom-width"].toInt();
                if(sheight == stop + cheight-borderWidth && this.isLoaded && this.curCount < this.total){
                    this.loadTaskList(this.listId);
                }
            }.bind(this)
        });
        this.loadTaskList()
    },
    loadTaskList:function(id,key){
        var tmploading = new Element("div.loading",{styles:{"width":"500px"}}).inject(this.taskListLayout);
        this.app.setLoading(tmploading);
        this.taskListLayout.scrollTo(0,this.taskListLayout.getScrollSize().y);
        var id = this.listId = id||"(0)";
        var count=10;
        var filter = {
            project:this.data.data.project
        };
        if(key && key!=""){
            filter.title = key
        }
        this.total = this.total || 0;
        this.curCount = this.curCount || 0;
        this.isLoaded = false;
        //alert("curcount="+this.curCount+"total="+this.total);alert(id)
        this.actions.taskListNext(id,count,filter,function(json){
            this.total = json.count;
            this.taskListData = json.data;
            tmploading.destroy();
            this.taskListData.each(function(d,i){
                this.loadTaskItem(d);
                id = d.id;
                this.listId = d.id;
                this.curCount = this.curCount + 1;
                this.isLoaded = true;

            }.bind(this));
        }.bind(this))

    },
    loadTaskItem:function(data){
        var _self = this;
        var taskItem = new Element("div.taskItem",{styles:this.css.taskItem,id:data.id}).inject(this.taskListLayout);
        taskItem.addEvents({
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
                    _self.selectedItem.getElements(".taskName").setStyles({"color":"#666666"});
                }
                this.setStyles({"background-color":"#3da8f5"});
                this.getElements(".taskName").setStyles({"color":"#ffffff"});
                _self.okAction.setStyles({
                    "cursor":"pointer",
                    "background-color":"#4A90E2"
                })
                _self.selectedItem = this;
            }
        });
        var taskName = new Element("div.taskName",{styles:this.css.taskName,text:data.name}).inject(taskItem);
        var n = data.executor.split("@")[0];
        n = n.substr(0,1);

        var taskPerson = new Element("div.taskPerson",{styles:this.css.taskPerson,text:n}).inject(taskItem);
    },
    createBottomLayout:function(){
        this.bottomLayout.empty();
        this.okAction = new Element("div.okAction",{styles:this.css.okAction,text:this.lp.ok}).inject(this.bottomLayout);
        this.okAction.addEvents({
            click:function(){
                if(this.selectedItem){
                    var data = {
                        parent : this.selectedItem.get("id"),
                        id:this.data.data.id
                    }
                    this.actions.taskSave(data,function(json){
                        this.explorer._createTableContent();
                        this.close();
                    }.bind(this))
                }
            }.bind(this)
        })
    }


});