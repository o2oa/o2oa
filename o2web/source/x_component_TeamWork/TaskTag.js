MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xApplication.TeamWork.TaskTag = new Class({
    Extends: MWF.xApplication.TeamWork.Common.ToolTips,
    options : {
        // displayDelay : 300,
        hasArrow:false,
        event:"click"
    },
    initialize : function( container, target, app, data, options, targetCoordinates, explorer ){
        //可以传入target 或者 targetCoordinates，两种选一
        //传入target,表示触发tooltip的节点，本类根据 this.options.event 自动绑定target的事件
        //传入targetCoordinates，表示 出发tooltip的位置，本类不绑定触发事件
        if( options ){
            this.setOptions(options);
        }
        if(explorer) this.explorer = explorer;
        this.container = container;
        this.target = target;
        this.targetCoordinates = targetCoordinates;
        this.app = app;
        //if(app)this.lp = app.lp;
        this.lp = this.app.lp.taskTag;
        this.data = data;
        this.actions = this.app.restActions;

        this.path = "/x_component_TeamWork/$TaskTag/";
        if(options.path) this.path = options.path;

        if( this.target ){
            this.setTargetEvents();
        }
        this.fireEvent("postInitialize",[this]);
    },
    _loadCustom : function( callback ){
        //this.data
        //this.contentNode

        var _self = this;
        this.status = "list";
        if(this.selectContainer) this.selectContainer.destroy();
        this.selectContainer = new Element("div.selectContainer",{styles:this.css.selectContainer}).inject(this.contentNode);
        //选择div
        this.selectTop = new Element("div.selectTop",{styles:this.css.selectTop}).inject(this.selectContainer);
        this.searchIn = new Element("input.searchIn",{styles:this.css.searchIn,placeholder:this.lp.taskTagSearch}).inject(this.selectTop);
        this.searchIn.addEvents({
            keyup:function(){
                var key = this.searchIn.get("value").trim();
                if(this.status == "list"){
                    this.selectList.show();
                    if(this.addContainer) this.addContainer.destroy();
                    var flag = false;
                    this.selectListContainer.empty();
                    this.tagData.each(function(data){
                        if(data.tag.indexOf(key)>-1){
                            flag = true;
                            this.loadTagItem(data);
                        }
                    }.bind(this));
                    if(!flag && key!=""){
                        this.selectList.hide();
                        this.createTag()
                    }

                }

            }.bind(this)
        });
        //this.addTag = new Element("div.addTag",{styles:this.css.addTag,title:this.lp.search}).inject(this.selectTop);
        this.selectList = new Element("div.selectList",{styles:this.css.selectList}).inject(this.selectContainer);
        this.selectListContainer = new Element("div.selectListContainer",{styles:this.css.selectListContainer}).inject(this.selectList);
        this.app.setScrollBar(this.selectListContainer);
        this.app.setLoading(this.selectListContainer);
        this.loadTag(function(){
            this.selectListContainer.empty();
            if(this.tagData.length==0){
                var tagNoneContainer = new Element("div.tagNoneContainer",{styles:this.css.tagNoneContainer}).inject(this.selectListContainer);
                new Element("div.tagNoneIcon",{styles:this.css.tagNoneIcon}).inject(tagNoneContainer);
                new Element("div.tagNoneText",{styles:this.css.tagNoneText,text:this.lp.taskTagNone}).inject(tagNoneContainer);
            }else{
                this.tagData.each(function(data){
                    this.loadTagItem(data)
                }.bind(this))
            }
        }.bind(this))

        if(callback)callback();
    },
    loadTagItem:function(data){
        var _self = this;
        var tagItem = new Element("div.tagItem",{styles:this.css.tagItem,id:data.id}).inject(this.selectListContainer);
        tagItem.addEvents({
            mouseenter:function(){
                this.setStyles({"background-color":"#F7F7F7"});
                this.getElement(".tagEdit").show();
                var tagText = this.getElement(".tagText");
                tagText.setStyles({"width":(tagText.getWidth()-this.getElement(".tagEdit").getWidth() - this.getElement(".tagEdit").getStyle("margin-left").toInt())+"px"})
            },
            mouseleave:function(){
                this.setStyles({"background-color":""});
                var tagText = this.getElement(".tagText");
                tagText.setStyles({"width":(tagText.getWidth()+this.getElement(".tagEdit").getWidth()+this.getElement(".tagEdit").getStyle("margin-left").toInt())+"px"});
                this.getElement(".tagEdit").hide();
            },
            click:function(){
                json={
                    tagId:data.id,
                    taskId:_self.data.taskId
                };

                //alert(tagText.getStyle("width"))
                if(this.getElement(".tagSelect").isDisplayed()){
                    var tagText = this.getElement(".tagText");
                    tagText.setStyles({"width":(tagText.getWidth()+this.getElement(".tagSelect").getWidth()+2)+"px"});
                    this.getElement(".tagSelect").hide();
                    json.action = "remove"
                }else{
                    var tagText = this.getElement(".tagText");
                    this.getElement(".tagSelect").show();
                    tagText.setStyles({"width":(tagText.getWidth()-this.getElement(".tagSelect").getWidth()-2)+"px"});
                    json.action = "add"
                }
                _self.loadTaskTag(json,function(d){
                    if(d.data.dynamics){
                        d.data.dynamics.each(function(dd){
                            _self.explorer.loadDynamicItem(dd,"bottom");
                            _self.explorer.dynamicContent.scrollTo(0,_self.explorer.dynamicContent.getScrollSize().y);
                            if(json.action == "add"){
                                if(_self.explorer.taskTagAddText.isDisplayed()){
                                    _self.explorer.taskTagAddText.hide();
                                    _self.explorer.taskTagAdd.show();
                                    _self.target = _self.explorer.taskTagAdd;
                                }
                                if(_self.explorer.taskData.tags){
                                    _self.explorer.taskData.tags.push(data);
                                }else{
                                    _self.explorer.taskData.tags = [];
                                    _self.explorer.taskData.tags.push(data);
                                }
                                _self.explorer.loadTagItem(data);
                            }else if(json.action == "remove"){
                                if(_self.explorer.taskTagLayout.getElementById(tagItem.get("id"))){
                                    _self.explorer.taskData.tags.each(function(ddd){
                                        if(ddd.id == data.id){
                                            _self.explorer.taskData.tags.erase(ddd)
                                        }
                                    })
                                    _self.explorer.taskTagLayout.getElementById(tagItem.get("id")).destroy()
                                }
                            }
                            _self.setCoondinates();
                        })
                    }

                }.bind(this));
            }
        });
        var tagColor = new Element("div.tagColor",{styles:this.css.tagColor}).inject(tagItem);
        tagColor.setStyles({"background-color":data.tagColor||"#3DA8F5"});
        var tagText = new Element("div.tagText",{styles:this.css.tagText,text:data.tag}).inject(tagItem);
        var tagSelect = new Element("div.tagSelect",{styles:this.css.tagSelect}).inject(tagItem);
        var tagEdit = new Element("div.tagEdit",{styles:this.css.tagEdit}).inject(tagItem);
        tagEdit.addEvents({
            mouseover:function(){this.setStyles({"background-image":"url(/x_component_TeamWork/$TaskTag/default/icon/icon_edit_click.png)"})},
            mouseout:function(){this.setStyles({"background-image":"url(/x_component_TeamWork/$TaskTag/default/icon/icon_edit.png)"})},
            click:function(e){
                this.selectList.hide();
                this.createTag(data);
                e.stopPropagation()
            }.bind(this)
        })
        tagSelect.hide();
        tagEdit.hide();

        if(this.data.taskTags){
            this.data.taskTags.each(function(dd){
                var _width = tagText.getWidth();
                if(dd.id == data.id) {
                    tagSelect.show();
                    tagText.setStyles({"width":(_width-tagSelect.getWidth())+"px"})
                }
            })
        }
    },
    createTag:function(data){
        var _self = this;
        if(this.addContainer) this.addContainer.destroy();
        this.addContainer = new Element("div.addContainer",{styles:this.css.addContainer}).inject(this.selectContainer);
        this.addColorContainer = new Element("div.addColorContainer",{styles:this.css.addColorContainer}).inject(this.addContainer);

        var colors = ["#3DA8F5","#75C940","#2FBDB3","#797EC9","#FFAF38","#FF4F3E"];
        for(var i=0;i<colors.length;i++){
            var colorItem = new Element("div.colorItem",{styles:this.css.colorItem}).inject(this.addColorContainer);
            colorItem.setStyles({"background-color":colors[i]})
            var colorGet = new Element("div.colorGet",{styles:this.css.colorGet}).inject(colorItem);
            if(i==0){
                colorGet.setStyles({"background-image":"url(/x_component_TeamWork/$TaskTag/default/icon/icon_dagou_white.png)"});
                this.curColor = colors[i];
            }
            colorItem.addEvents({
                click:function(){
                    _self.addColorContainer.getElements(".colorGet").setStyles({"background-image":""});
                    this.getChildren().setStyles({"background-image":"url(/x_component_TeamWork/$TaskTag/default/icon/icon_dagou_white.png)"});
                    _self.curColor = this.getStyle("background-color");
                }
            })
        }

        this.tagAction = new Element("div.tagAction",{styles:this.css.tagAction}).inject(this.addContainer);

        if(data){
            this.curTag = data;
            this.status = "edit";
            this.searchIn.set("value",data.tag);
            var items = this.addColorContainer.getElements(".colorItem");
            items.each(function(d){
                if(d.getStyle("background-color") == data.tagColor){
                    d.getChildren().setStyles({"background-image":"url(/x_component_TeamWork/$TaskTag/default/icon/icon_dagou_white.png)"})
                }else{
                    d.getChildren().setStyles({"background-image":""})
                }
            }.bind(this));
            this.tagRemove = new Element("div.tagRemove",{styles:this.css.tagRemove,text:this.lp.tagRemove}).inject(this.tagAction);
            this.tagSave = new Element("div.tagSave",{styles:this.css.tagSave,text:this.lp.tagSave}).inject(this.tagAction);
            this.tagRemove.addEvents({
                click:function(e){
                    this.app.confirm("warn",e,_self.app.lp.common.confirm.removeTitle,_self.app.lp.common.confirm.removeContent,300,120,function(){
                        _self.actions.taskTagRemove(data.id,function(){
                            _self.curTag = data;
                            _self.status = "edit";
                            _self._loadCustom();
                            this.close()
                        }.bind(this))
                    },function(){
                        this.close();
                    });
                }.bind(this)
            });
            this.tagSave.addEvents({
                click:function(){
                    this.saveTag(function(){
                        this.curTag = data;
                        this.status = "edit";
                        this._loadCustom()
                    }.bind(this))
                }.bind(this)
            })
        }else{
            this.tagAdd = new Element("div.tagAdd",{styles:this.css.tagAdd,text:this.lp.tagAdd}).inject(this.tagAction);
            this.tagAdd.addEvents({
                click:function(){
                    this.saveTag(function(){
                        this._loadCustom()
                    }.bind(this))
                }.bind(this)
            })
        }

    },
    saveTag:function(callback){
        if(this.searchIn.get("value").trim()==""){
            return;
        }
        var data = {
            id:this.curTag ? this.curTag.id : "",
            project:this.data.projectId,
            tag:this.searchIn.get("value").trim(),
            tagColor:this.curColor
        };

        this.actions.taskTagSave(data,function(json){
            if(callback)callback(json)
        }.bind(this))
    },
    loadTag:function(callback){
        this.actions.taskTagListByProjectId(this.data.projectId,function(json){
            this.tagData = json.data;
            if(callback)callback(json.data)
        }.bind(this))
    },
    loadTaskTag:function(data,callback){
        if(data.action == "add"){
            this.actions.addTagToTask(data.taskId,data.tagId,function(json){
                if(callback)callback(json)
            }.bind(this),false)
        }else if(data.action == "remove"){
            this.actions.removeTagToTask(data.taskId,data.tagId,function(json){
                if(callback)callback(json)
            }.bind(this),false)
        }
        //刷新task的tag内容，增加动态

    }

});