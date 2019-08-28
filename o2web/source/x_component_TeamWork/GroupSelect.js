MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xApplication.TeamWork.GroupSelect = new Class({
    Extends: MWF.xApplication.TeamWork.Common.ToolTips,
    options : {
        // displayDelay : 300,
        hasArrow:false,
        event:"click"
    },

    _loadCustom : function( callback ){
        this.css = this.css.tooltip.groupSelect;
        this.lp = this.lp.groupSelect;
        this.createFirstPage();

        if(callback)callback();
    },
    createFirstPage:function(data){ //alert(JSON.stringify(this.data))
        var _self = this;
        if(this.contentNode)this.contentNode.empty();
        this.topBar = new Element("div.topBar",{styles:this.css.topBar}).inject(this.contentNode);
        this.topBarText = new Element("div.topBarText",{styles:this.css.topBarText,text:this.lp.name}).inject(this.topBar);
        this.topBarClose = new Element("div.topBarClose",{styles:this.css.topBarClose}).inject(this.topBar);
        this.topBarClose.addEvents({
            click:function(){this.hide()}.bind(this)
        });
        // this.groupInContainer = new Element("div.groupInContainer",{styles:this.css.groupInContainer}).inject(this.contentNode);
        // this.groupIn = new Element("input.groupIn",{styles:this.css.groupIn,type:"text",placeholder:this.lp.groupIn}).inject(this.groupInContainer);
        // this.groupIn.addEvents({
        //     keyup:function(){
        //         var v = this.groupIn.get("value");
        //         //this.allProjectGroup
        //         var searchRes = [];
        //         this.allProjectGroup.each(function(d){
        //             if(d.title.indexOf(v)>-1) searchRes.push(d);
        //         });
        //
        //     }.bind(this)
        // });

        this.dynamicLayout = new Element("div.dynamicLayout",{styles:this.css.dynamicLayout}).inject(this.contentNode);
        this.dynamicLayout.setStyle("display","none");

        this.commonGroup = new Element("div.commonGroup",{styles:this.css.commonGroup,text:this.lp.select}).inject(this.contentNode);

        this.commonGroupContainer = new Element("div.commonGroupContainer",{styles:this.css.commonGroupContainer}).inject(this.contentNode);
        this.app.setScrollBar(this.commonGroupContainer);
        this.createCommonGroup();

        this.newGroupContainer = new Element("div.newGroupContainer",{styles:this.css.newGroupContainer}).inject(this.contentNode);
        this.newGroupContainer.addEvents({
            click:function(){
                this.createNewGroup();
            }.bind(this)
        });
        this.newGroupIcon = new Element("div.newGroupIcon",{styles:this.css.newGroupIcon}).inject(this.newGroupContainer);
        this.newGroupText = new Element("div.newGroupText",{styles:this.css.newGroupText,text:this.lp.add}).inject(this.newGroupContainer);

        this.groupAdd = new Element("div.groupAdd",{styles:this.css.groupAdd,text:this.lp.confirm}).inject(this.contentNode);
        this.groupAdd.addEvents({
            click:function(){
                var dd = [];
                this.commonGroupContainer.getElements(".groupItemIcon").each(function(d){
                    // if(d.get("cc")=="yes") dd.push($(d).getNext().get("text"))
                    if(d.get("cc")=="yes") dd.push($(d).get("id"))
                });

                if(this.dynamicGroupIcon && this.dynamicGroupIcon.get("cc")=="yes") dd.push(this.dynamicGroupIcon.get("id"));
                this.close(dd)
                //if(dd.length>0)this.close(dd);
            }.bind(this)
        });

        //加载可能新建的组
        if(data){
            this.dynamicLayout.setStyle("display","");
            this.dynamicGroupIcon = new Element("div.dynamicGroupIcon",{styles:this.css.dynamicGroupIcon,id:data.id}).inject(this.dynamicLayout);
            this.dynamicGroupIcon.addEvents({
                click:function(){
                    _self.selectGroupIcon(this);
                }
            });
            var dynamicGroupText = new Element("div.dynamicGroupText",{styles:this.css.dynamicGroupText,text:data.name}).inject(this.dynamicLayout);
            this.dynamicGroupIcon.click();
        }
    },
    createSecondPage:function(){
        if(this.contentNode)this.contentNode.empty();
        this.topBar = new Element("div.topBar",{styles:this.css.topBar}).inject(this.contentNode);
        this.topBarBack = new Element("div.topBarBack",{styles:this.css.topBarBack}).inject(this.topBar);
        this.topBarBack.addEvent("click",function(){this.secondPageClose()}.bind(this));
        this.topBarText = new Element("div.topBarText",{styles:this.css.topBarText,text:this.lp.newGroup}).inject(this.topBar);
        this.topBarClose = new Element("div.topBarClose",{styles:this.css.topBarClose}).inject(this.topBar);
        this.topBarClose.addEvent("click",function(){this.secondPageClose()}.bind(this));

        this.groupInContainer = new Element("div.groupInContainer",{styles:this.css.groupInContainer}).inject(this.contentNode);
        this.groupIn = new Element("input.groupIn",{styles:this.css.groupIn,type:"text",placeholder:this.lp.groupTextTip}).inject(this.groupInContainer);
        this.groupIn.addEvents({
            keyup:function(){
                var v = this.groupIn.get("value");
                if(v.trim()==""){
                    this.groupAdd.setStyles({
                        "cursor":"",
                        "background-color":"#F0F0F0",
                        "color":"#666666"
                    })
                }else{
                    this.groupAdd.setStyles({
                        "cursor":"pointer",
                        "background-color":"#4A90E2",
                        "color":"#FFFFFF"
                    })
                }
            }.bind(this)
        });

        this.groupAdd = new Element("div.groupAdd",{styles:this.css.groupAdd,text:this.lp.confirm}).inject(this.contentNode);
        this.groupAdd.addEvents({
            click:function(){
                if(this.groupIn.get("value").trim()=="") return;
                //创建新分组
                this.secondPageClose();
            }.bind(this)
        });
    },
    secondPageClose:function(){

        var data = null;
        if(this.groupIn.get("value").trim()!=""){
            data = {
                "name":this.groupIn.get("value").trim()
            };
        }

        if(!data){
            this.createFirstPage();
            return;
        }
        this.actions.groupSave(data,function(json){
            if(json.data && json.data.id){
                this.actions.groupList(function(resData){ debugger;
                    var resD = null;
                    resData.data.each(function(dd){
                        if(dd.id == json.data.id) resD = dd;
                    }.bind(this));
                    if(resD)this.createFirstPage(resD);
                    else this.createFirstPage();

                }.bind(this))
            }else{
                this.createFirstPage();
            }
        }.bind(this));

    },

    createCommonGroup:function(){
        var _self = this;
        this.app.setLoading(this.commonGroupContainer);
        this.actions.groupList(function(json){
            this.commonGroupContainer.empty();
            var data = json.data;
            this.allProjectGroup = json.data;
            if(data){
                data.each(function (d,i) {
                    if(i<100){
                        var groupItemContainer = new Element("div.groupItemContainer",{styles:this.css.groupItemContainer}).inject(this.commonGroupContainer);
                        var groupItemIcon = new Element("div.groupItemIcon",{styles:this.css.groupItemIcon,id:d.id}).inject(groupItemContainer);
                        groupItemIcon.addEvents({
                            click:function(){
                                _self.selectGroupIcon(this);

                            }
                        });
                        var groupItemText = new Element("div.groupItemText",{styles:this.css.groupItemText,text:d.name}).inject(groupItemContainer);
                        if(_self.getIdInArr(d.id)){
                            groupItemIcon.setStyle("background-image",groupItemIcon.getStyle("background-image").replace("icon_circle.png","icon_renwu_ywc_click.png"));
                            groupItemIcon.set("cc","yes");
                        }
                    }

                }.bind(this))
            }

        }.bind(this));

    },
    selectGroupIcon:function(obj){
        var _this = obj;
        var url = _this.getStyle("background-image");
        if(url.indexOf("icon_circle.png")>0){
            _this.setStyle("background-image",url.replace("icon_circle.png","icon_renwu_ywc_click.png"));
            _this.set("cc","yes")
        }else{
            _this.setStyle("background-image",url.replace("icon_renwu_ywc_click.png","icon_circle.png"));
            _this.set("cc","no")
        }

        //循环常用分组+新建的分组
        var flag = false;
        this.commonGroupContainer.getElements(".groupItemIcon").each(function(d){
            if(d.get("cc")=="yes") flag = true;
        });

        if(!flag){
            if(this.dynamicGroupIcon && this.dynamicGroupIcon.get("cc")=="yes") flag = true;
        }

        if(flag){
            this.groupAdd.setStyles({"color":"#ffffff","background-color":"#4A90E2","cursor":"pointer"});
        }else{
            this.groupAdd.setStyles({"color":"#666666","background-color":"#F0F0F0","cursor":""});
        }
    },
    createNewGroup:function(){
        this.createSecondPage();
    },
    getIdInArr:function(id){
        var res = false;
        if(this.data.groups){
            this.data.groups.each(function(data){
                if(data.id == id) res = true;
            });
        }

        return res;
    }

});