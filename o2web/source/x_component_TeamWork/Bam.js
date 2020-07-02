
MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xDesktop.requireApp("TeamWork", "Common", null, false);

MWF.xApplication.TeamWork.Bam = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function (container, app, data, options) {
        this.setOptions(options);
        this.container = container;

        this.app = app;
        this.lp = this.app.lp.bam;
        this.rootActions = this.app.rootActions;
        this.actions = this.rootActions.ProjectTemplateAction;

        this.path = "/x_component_TeamWork/$Bam/";
        this.cssPath = this.path+this.options.style+"/css.wcss";
        this._loadCss();

        this.data = data;
    },
    load: function () {
        this.container.empty();
        this.createTopBarLayout();
        this.createContainerLayout();
    },
    createTopBarLayout:function(){
        var _self = this;
        this.topBarLayout = new Element("div.topBarLayout",{styles:this.css.topBarLayout}).inject(this.container);
        this.topBarBackContainer = new Element("div.topBarBackContainer",{styles:this.css.topBarBackContainer}).inject(this.topBarLayout);
        this.topBarBackHomeIcon = new Element("div.topBarBackHomeIcon",{styles:this.css.topBarBackHomeIcon}).inject(this.topBarBackContainer);
        this.topBarBackHomeIcon.addEvents({
            click:function(){
                var pl = new MWF.xApplication.TeamWork.ProjectList(this.container,this.app,this.actions,{});
                pl.load();
            }.bind(this),
            mouseover:function(){
                var opt={
                    axis: "y"      //箭头在x轴还是y轴上展现
                };
                this.app.showTips(this.topBarBackHomeIcon,{_html:"<div style='margin:2px 5px;'>"+this.lp.backProject+"</div>"},opt);
                //this.app.tips(this.topBarBackHomeIcon,this.lp.backProject);
            }.bind(this)
        });
        this.topBarBackHomeNext = new Element("div.topBarBackHomeNext",{styles:this.css.topBarBackHomeNext}).inject(this.topBarBackContainer);
        this.bamTitle = new Element("div.bamTitle",{styles:this.css.bamTitle,text:this.lp.title}).inject(this.topBarBackContainer);
    },
    createContainerLayout: function(){
        this.containerLayout = new Element("div.containerLayout",{styles:this.css.containerLayout}).inject(this.container);
        this.createNaviLayout();
        this.createContentLayout();
        this.templateDiv.click();
    },

    createNaviLayout:function(){
        var _self = this;
        this.naviLayout = new Element("div.naviLayout",{styles:this.css.naviLayout}).inject(this.containerLayout);

        new Element("div.naviMenu",{styles:this.css.naviMenu, text:this.lp.base}).inject(this.naviLayout);

        //模板管理
        this.templateDiv = new Element("div.templateDiv",{styles:this.css.naviItem}).inject(this.naviLayout);
        this.templateDiv.addEvents({
            mouseenter:function(){
                if(this.curNavi == this.templateDiv) return;
                this.templateDiv.setStyles({"border-left":"2px solid #1b9aee","color":"#000000"});
            }.bind(this),
            mouseleave:function(){
                if(this.curNavi == this.templateDiv) return;
                this.templateDiv.setStyles({"border-left":"2px solid #ffffff","color":"#595959"});
            }.bind(this),
            click:function(){
                if(this.curNavi)this.curNavi.setStyles({"border-left":"2px solid #ffffff","color":"#595959"});
                this.curNavi = this.templateDiv;
                this.curNavi.setStyles({"border-left":"2px solid #0171c2","color":"#000000"});
                this.createTemplateLayout();
            }.bind(this)
        });
        new Element("div.templateIcon",{ styles: this.css.templateIcon }).inject(this.templateDiv);
        new Element("div.templateText",{styles: this.css.templateText, text: this.lp.navi.template}).inject(this.templateDiv);

        //优先级设置
        this.priorityDiv = new Element("div.priorityDiv",{styles:this.css.naviItem}).inject(this.naviLayout);
        this.priorityDiv.addEvents({
            mouseenter:function(){
                if(this.curNavi == this.priorityDiv) return;
                this.priorityDiv.setStyles({"border-left":"2px solid #1b9aee","color":"#000000"});
            }.bind(this),
            mouseleave:function(){
                if(this.curNavi == this.priorityDiv) return;
                this.priorityDiv.setStyles({"border-left":"2px solid #ffffff","color":"#595959"});
            }.bind(this),
            click:function(){
                if(this.curNavi)this.curNavi.setStyles({"border-left":"2px solid #ffffff","color":"#595959"});
                this.curNavi = this.priorityDiv;
                this.curNavi.setStyles({"border-left":"2px solid #0171c2","color":"#000000"});
                this.createPriorityLayout();

            }.bind(this)
        });
        new Element("div.priorityIcon",{ styles: this.css.priorityIcon }).inject(this.priorityDiv);
        new Element("div.priorityText",{styles: this.css.priorityText, text: this.lp.navi.priority}).inject(this.priorityDiv);

    /*
        //自定义字段
        this.fieldDiv = new Element("div.fieldDiv",{styles:this.css.naviItem}).inject(this.naviLayout);
        this.fieldDiv.addEvents({
            mouseenter:function(){
                if(this.curNavi == this.fieldDiv) return;
                this.fieldDiv.setStyles({"border-left":"2px solid #1b9aee","color":"#000000"});
            }.bind(this),
            mouseleave:function(){
                if(this.curNavi == this.fieldDiv) return;
                this.fieldDiv.setStyles({"border-left":"2px solid #ffffff","color":"#595959"});
            }.bind(this),
            click:function(){
                if(this.curNavi)this.curNavi.setStyles({"border-left":"2px solid #ffffff","color":"#595959"});
                this.curNavi = this.fieldDiv;
                this.curNavi.setStyles({"border-left":"2px solid #0171c2","color":"#000000"});

            }.bind(this)
        });
        new Element("div.fieldIcon",{ styles: this.css.fieldIcon }).inject(this.fieldDiv);
        new Element("div.fieldText",{styles: this.css.fieldText, text: this.lp.navi.extField}).inject(this.fieldDiv);

        //权限设置
        this.accessDiv = new Element("div.accessDiv",{styles:this.css.naviItem}).inject(this.naviLayout);
        this.accessDiv.addEvents({
            mouseenter:function(){
                if(this.curNavi == this.accessDiv) return;
                this.accessDiv.setStyles({"border-left":"2px solid #1b9aee","color":"#000000"});
            }.bind(this),
            mouseleave:function(){
                if(this.curNavi == this.accessDiv) return;
                this.accessDiv.setStyles({"border-left":"2px solid #ffffff","color":"#595959"});
            }.bind(this),
            click:function(){
                if(this.curNavi)this.curNavi.setStyles({"border-left":"2px solid #ffffff","color":"#595959"});
                this.curNavi = this.accessDiv;
                this.curNavi.setStyles({"border-left":"2px solid #0171c2","color":"#000000"});

            }.bind(this)
        });
        new Element("div.accessIcon",{ styles: this.css.accessIcon }).inject(this.accessDiv);
        new Element("div.accessText",{styles: this.css.accessText, text: this.lp.navi.access}).inject(this.accessDiv);
    */
    },
    createContentLayout:function(){
        this.contentLayout = new Element("div.contentLayout",{styles:this.css.contentLayout}).inject(this.containerLayout);
    },
    createPriorityLayout:function(){
        var _self = this;
        this.contentLayout.empty();

        var priorityTop = new Element("div.priorityTop",{styles:this.css.priorityTop}).inject(this.contentLayout);
        var priorityTopContent = new Element("div.priorityTopContent",{styles:this.css.priorityTopContent}).inject(priorityTop);
        var priorityTopTitle = new Element("div.priorityTopTitle",{styles:this.css.priorityTopTitle,text:this.lp.priority.title}).inject(priorityTopContent);
        var priorityTopDes = new Element("div.priorityTopDes",{styles:this.css.priorityTopDes,text:this.lp.priority.tips}).inject(priorityTopContent);

        // var templateTopAddContent = new Element("div.templateTopAddContent",{styles:this.css.templateTopAddContent}).inject(templateTop);
        // var templateTopAdd = new Element("div.templateTopAdd",{styles:this.css.templateTopAdd,text:this.lp.template.add}).inject(templateTopAddContent);
        // templateTopAdd.addEvents({
        //     mouseover:function(){
        //         this.setStyles({"color":"#0171c2"})
        //     },
        //     mouseout:function(){
        //         this.setStyles({"color":"#1b9aee"})
        //     },
        //     click:function(){
        //         _self.openTemplate();
        //     }
        // });

        var priorityContainer = new Element("div.priorityContainer",{styles:this.css.priorityContainer}).inject(this.contentLayout);
        this.priorityItemContent = new Element("div.priorityItemContent",{styles:this.css.priorityItemContent}).inject(priorityContainer);
        this.app.setLoading(this.priorityItemContent);
        this.rootActions.GlobalAction.priorityList(function(json){
            this.priorityItemContent.empty();
            json.data.each(function(data){
                this.createPriorityItem(data);
            }.bind(this))
        }.bind(this))

        var addPriorityContainer = new Element("div.addPriorityContainer",{styles:this.css.addPriorityContainer}).inject(priorityContainer,"bottom");
        var addPriorityIcon = new Element("div.addPriorityIcon",{styles:this.css.addPriorityIcon}).inject(addPriorityContainer);
        var addPriorityTxt = new Element("div.addPriorityTxt",{styles:this.css.addPriorityTxt, text: this.lp.priority.add}).inject(addPriorityContainer);
        addPriorityContainer.addEvents({
            mouseenter:function(){
                addPriorityIcon.setStyles({"background-image":"url(/x_component_TeamWork/$Bam/default/icon/icon_add_click.png)"});
                addPriorityTxt.setStyles({"color":"#13227a"})
            },
            mouseleave:function(){
                addPriorityIcon.setStyles({"background-image":"url(/x_component_TeamWork/$Bam/default/icon/icon_add.png)"});
                addPriorityTxt.setStyles({"color":"#1296db"})
            },
            click:function(){ //fffffffff
                this.createPriorityItem();
            }.bind(this)
        })
    },
    createPriorityColorItem:function(content,data,vColor,bColor){

        var priorityColorItem = new Element("div.priorityColorItem",{styles:this.css.priorityColorItemContainer}).inject(content);
        var priorityColor = new Element("div.priorityColor",{styles:this.css.priorityColor}).inject(priorityColorItem);
        priorityColor.setStyles({"background-color":vColor});

        if(data && data.priorityColor.toUpperCase() == bColor.toUpperCase()){
            priorityColor.setStyles({
                "width":"18px",
                "height":"18px",
                "background-color":bColor,
                "border":"3px solid " + vColor + " "
            });
            priorityColor.set("name","active");
        }

        priorityColor.addEvents({
            mouseover:function(){
                if(this.get("name")=="active") return;
                this.setStyles({"background-color":bColor ,"width":"18px","height":"18px"});
            },
            mouseout:function(){
                if(this.get("name")=="active") return;
                this.setStyles({"background-color":vColor,"width":"14px","height":"14px"});
            },
            click:function(){
                if(this.get("name")=="active") return;
                var actName = content.getElements("div[name='active']");
                if(actName.length>0){
                    actName[0].removeProperty("name");
                    var color = actName[0].getStyle("border-left-color");
                    actName[0].setStyles({
                        "border":"0px",
                        "width":"14px",
                        "height":"14px",
                        "background-color":color
                    });
                }

                this.set("name","active");
                this.setStyles({
                    "width":"18px",
                    "height":"18px",
                    "background-color": bColor,
                    "border":"3px solid " + vColor
                });

            }
        });

    },
    createPriorityItem:function(data){
        var _self = this;
        var id = data ? data.id : "";
        var priorityItemContainer = new Element("div.priorityItemContainer",{styles:this.css.priorityItemContainer,index:data ? data.order:""}).inject(this.priorityItemContent);
        //var priorityItemMove = new Element("div.priorityItemMove",{styles:this.css.priorityItemMove}).inject(priorityItemContainer);

        var priorityValueContainer = new Element("div.priorityValueContainer",{styles:this.css.priorityValueContainer}).inject(priorityItemContainer);
        var priorityValue = new Element("input",{styles:this.css.priorityValue,type:"input",value:data?data.priority:""}).inject(priorityValueContainer);
        priorityValue.addEvents({
            blur:function(){
                if(this.get("value").trim()=="") this.setStyles({"border":"1px solid #ff0000"});
                else this.setStyles({"border":"1px solid #cccccc"});
            },
            focus:function(){
                this.setStyles({"border":"1px solid #1296db"})
            },
            keyup:function(){
                var v = this.get("value").trim();
                if(v=="") this.setStyles({"border":"1px solid #ff0000"})
                else this.setStyles({"border":"1px solid #1296db"})
            }
        });
        var priorityColorContainer = new Element("div.priorityColorContainer",{styles:this.css.priorityColorContainer}).inject(priorityItemContainer);

        // red
        this.createPriorityColorItem(priorityColorContainer, data,"#FFCCCC", "#E62412");
        // orange
        this.createPriorityColorItem(priorityColorContainer, data,"#FFD591", "#FA8C15");
        // green
        this.createPriorityColorItem(priorityColorContainer, data,"#CAFAC8", "#15AD31");
        // blue
        this.createPriorityColorItem(priorityColorContainer, data,"#CCECFF", "#1B9AEE");
        // grey
        this.createPriorityColorItem(priorityColorContainer, data,"#E5E5E5", "#8C8C8C");


        //actions
        var priorityActionContainer = new Element("div.priorityActionContainer",{styles:this.css.priorityActionContainer}).inject(priorityItemContainer);
        var priorityActionOK = new Element("div.priorityActionOK",{styles:this.css.priorityActionOK}).inject(priorityActionContainer);
        priorityActionOK.addEvents({
            mouseover:function(){
                this.setStyles({"background-image":"url(/x_component_TeamWork/$Bam/default/icon/icon_ok_click.png)"})
            },
            mouseout:function(){
                this.setStyles({"background-image":"url(/x_component_TeamWork/$Bam/default/icon/icon_ok.png)"})
            },
            click:function(){
                var colorObj = priorityColorContainer.getElements("div[name='active']");
                if(priorityValue.get("value").trim()==""){
                    priorityValue.setStyles({"border":"1px solid #ff0000"});
                    window.setTimeout(function(){
                        priorityValue.setStyles({"border":"1px solid #cccccc"});
                        window.setTimeout(function(){
                            priorityValue.setStyles({"border":"1px solid #ff0000"});
                        },200)
                    },200);
                    return;
                }
                if(colorObj.length == 0){
                    //priorityColorContainer.setStyles({"border":"1px solid #ff0000"});
                    var objs = priorityColorContainer.getElements(".priorityColorItem");
                    objs.each(function(obj,i){
                        var time = (i + 1) * 50;
                        window.setTimeout(function(){
                            //obj.setStyles({"width":"18px","height":"18px"});
                            obj.setStyles({"background-color":"#ff0000"});
                            window.setTimeout(function(){
                                //obj.setStyles({"width":"14px","height":"14px"});
                                obj.setStyles({"background-color":""});
                            },50);
                        },time)
                    })
                    return;
                }

                var data = {
                    id:id,
                    priority:priorityValue.get("value").trim(),
                    priorityColor:colorObj[0].getStyle("background-color"),
                    order:priorityItemContainer.get("index")
                };

                this.rootActions.GlobalAction.prioritySave(data,function(json){
                    id = json.data.id
                    this.app.notice(this.lp.priority.success,"success")
                }.bind(this))

            }.bind(this)
        });
        var priorityActionRemove = new Element("div.priorityActionRemove",{styles:this.css.priorityActionRemove}).inject(priorityActionContainer);
        priorityActionRemove.addEvents({
            mouseover:function(){
                this.setStyles({"background-image":"url(/x_component_TeamWork/$Bam/default/icon/icon_close_click.png)"})
            },
            mouseout:function(){
                this.setStyles({"background-image":"url(/x_component_TeamWork/$Bam/default/icon/icon_close.png)"})
            },
            click:function(e){
                if(id==""){
                    var fx = new Fx.Tween(priorityItemContainer,{duration:200});
                    fx.start(["height"] ,"60px", "0px").chain(function(){
                        priorityItemContainer.destroy();
                    }.bind(this));
                    //priorityItemContainer.destroy();
                }else{
                    _self.app.confirm("warn",e,_self.app.lp.common.confirm.removeTitle,_self.app.lp.common.confirm.removeContent,300,120,function(){
                        _self.rootActions.GlobalAction.priorityDelete(id,function(){
                            var fx = new Fx.Tween(priorityItemContainer,{duration:200});
                            fx.start(["height"] ,"60px", "0px").chain(function(){
                                priorityItemContainer.destroy();
                                this.close();
                            }.bind(this));


                            //priorityItemContainer.destroy();
                            //this.close();
                        }.bind(this))
                    },function(){
                        this.close();
                    });
                }
            }
        });


        return;







        //
        //
        //
        //
        // this.priorityColorRedItem = new Element("div.priorityColorRedItem",{styles:this.css.priorityColorItemContainer}).inject(priorityColorContainer);
        // var priorityColorRed = new Element("div.priorityColorRed",{styles:this.css.priorityColorRed}).inject(this.priorityColorRedItem);
        // if(data.priorityColor == "#FFCCCC"){
        //     priorityColorRed.setStyles({
        //         "width":"18px",
        //         "height":"18px",
        //         "background-color":"#E62412",
        //         "border":"3px solid #FFCCCC"
        //     });
        //     priorityColorRed.set("name","active");
        // }else{
        //     priorityColorRed.addEvents({
        //         mouseover:function(){
        //             if(this.get("name")=="active") return;
        //             this.setStyles({"background-color":"#E62412","width":"18px","height":"18px"});
        //         },
        //         mouseout:function(){
        //             if(this.get("name")=="active") return;
        //             this.setStyles({"background-color":"#FFCCCC","width":"14px","height":"14px"});
        //         },
        //         click:function(){
        //             if(this.get("name")=="active") return;
        //             var actName = priorityColorContainer.getElements("div[name='active']");
        //             if(actName.length>0){
        //                 actName[0].removeProperty("name");
        //                 var color = actName[0].getStyle("border-left-color");
        //                 actName[0].setStyles({
        //                     "border":"0px",
        //                     "width":"14px",
        //                     "height":"14px",
        //                     "background-color":color
        //                 });
        //             }
        //
        //             this.set("name","active");
        //             this.setStyles({
        //                 "width":"18px",
        //                 "height":"18px",
        //                 "background-color":"#E62412",
        //                 "border":"3px solid #FFCCCC"
        //             });
        //
        //         }
        //     });
        //
        // }
        //
        //
        //
        // this.priorityColorOrangeItem = new Element("div.priorityColorOrangeItem",{styles:this.css.priorityColorItemContainer}).inject(priorityColorContainer);
        // var priorityColorOrange = new Element("div.priorityColorOrange",{styles:this.css.priorityColorOrange}).inject(this.priorityColorOrangeItem);
        // if(data.priorityColor == "#FFD591"){
        //     priorityColorOrange.setStyles({
        //         "width":"18px",
        //         "height":"18px",
        //         "background-color":"#FA8C15",
        //         "border":"3px solid #FFD591"
        //     });
        //     priorityColorOrange.set("name","active");
        // }else{
        //     priorityColorOrange.addEvents({
        //         mouseover:function(){
        //             if(this.get("name")=="active") return;
        //             this.setStyles({"background-color":"#FA8C15","width":"18px","height":"18px"});
        //         },
        //         mouseout:function(){
        //             if(this.get("name")=="active") return;
        //             this.setStyles({"background-color":"#FFD591","width":"14px","height":"14px"});
        //         },
        //         click:function(){
        //             if(this.get("name")=="active") return;
        //             var actName = priorityColorContainer.getElements("div[name='active']");
        //             if(actName.length>0){
        //                 actName[0].removeProperty("name");
        //                 var color = actName[0].getStyle("border-left-color");
        //                 actName[0].setStyles({
        //                     "border":"0px",
        //                     "width":"14px",
        //                     "height":"14px",
        //                     "background-color":color
        //                 });
        //             }
        //
        //             this.set("name","active");
        //             this.setStyles({
        //                 "width":"18px",
        //                 "height":"18px",
        //                 "background-color":"#FA8C15",
        //                 "border":"3px solid #FFD591"
        //             });
        //
        //         }
        //     });
        // }
        //
        //
        //
        // this.priorityColorGreenItem = new Element("div.priorityColorGreenItem",{styles:this.css.priorityColorItemContainer}).inject(priorityColorContainer);
        // var priorityColorGreen = new Element("div.priorityColorGreen",{styles:this.css.priorityColorGreen}).inject(this.priorityColorGreenItem);
        // if(data.priorityColor == "#CAFAC8"){
        //     priorityColorGreen.setStyles({
        //         "width":"18px",
        //         "height":"18px",
        //         "background-color":"#15AD31",
        //         "border":"3px solid #CAFAC8"
        //     });
        //     priorityColorGreen.set("name","active");
        // }else{
        //     priorityColorGreen.addEvents({
        //         mouseover:function(){
        //             this.setStyles({"background-color":"#15AD31","width":"18px","height":"18px"});
        //         },
        //         mouseout:function(){
        //             this.setStyles({"background-color":"#CAFAC8","width":"14px","height":"14px"});
        //         }
        //     });
        // }
        //
        //
        // this.priorityColorBlueItem = new Element("div.priorityColorBlueItem",{styles:this.css.priorityColorItemContainer}).inject(priorityColorContainer);
        // var priorityColorBlue = new Element("div.priorityColorBlue",{styles:this.css.priorityColorBlue}).inject(this.priorityColorBlueItem);
        // if(data.priorityColor == "#CCECFF"){
        //     priorityColorBlue.setStyles({
        //         "width":"18px",
        //         "height":"18px",
        //         "background-color":"#1B9AEE",
        //         "border":"3px solid #CCECFF"
        //     });
        //     priorityColorBlue.set("name","active");
        // }else{
        //     priorityColorBlue.addEvents({
        //         mouseover:function(){
        //             this.setStyles({"background-color":"#1B9AEE","width":"18px","height":"18px"});
        //         },
        //         mouseout:function(){
        //             this.setStyles({"background-color":"#CCECFF","width":"14px","height":"14px"});
        //         }
        //     });
        // }
        //
        //
        // this.priorityColorGreyItem = new Element("div.priorityColorGreyItem",{styles:this.css.priorityColorItemContainer}).inject(priorityColorContainer);
        // var priorityColorGrey = new Element("div.priorityColorGrey",{styles:this.css.priorityColorGrey}).inject(this.priorityColorGreyItem);
        // if(data.priorityColor == "#E5E5E5"){
        //     priorityColorGrey.setStyles({
        //         "width":"18px",
        //         "height":"18px",
        //         "background-color":"#8C8C8C",
        //         "border":"3px solid #E5E5E5"
        //     });
        //     priorityColorGrey.set("name","active");
        // }else{
        //     priorityColorGrey.addEvents({
        //         mouseover:function(){
        //             this.setStyles({"background-color":"#8C8C8C","width":"18px","height":"18px"});
        //         },
        //         mouseout:function(){
        //             this.setStyles({"background-color":"#E5E5E5","width":"14px","height":"14px"});
        //         }
        //     });
        // }


    },
    createTemplateLayout:function(){
        var _self = this;
        this.contentLayout.empty();
        var templateTop = new Element("div.templateTop",{styles:this.css.templateTop}).inject(this.contentLayout);
        var templateTopContent = new Element("div.templateTopContent",{styles:this.css.templateTopContent}).inject(templateTop);
        var templateTopTitle = new Element("div.templateTopTitle",{styles:this.css.templateTopTitle,text:this.lp.template.title}).inject(templateTopContent);
        var templateTopDes = new Element("div.templateTopDes",{styles:this.css.templateTopDes,text:this.lp.template.tips}).inject(templateTopContent);

        var templateTopAddContent = new Element("div.templateTopAddContent",{styles:this.css.templateTopAddContent}).inject(templateTop);
        var templateTopAdd = new Element("div.templateTopAdd",{styles:this.css.templateTopAdd,text:this.lp.template.add}).inject(templateTopAddContent);
        templateTopAdd.addEvents({
            mouseover:function(){
                this.setStyles({"color":"#0171c2"})
            },
            mouseout:function(){
                this.setStyles({"color":"#1b9aee"})
            },
            click:function(){
                _self.openTemplate();
            }
        });

        this.templateContainer = new Element("div.templateContainer",{styles:this.css.templateContainer}).inject(this.contentLayout);
        this.app.setLoading(this.templateContainer);
        this.rootActions.ProjectTemplateAction.listNextWithFilter("(0)",100,{},function(json){
            this.templateContainer.empty();
            json.data.each(function(data){
                this.createTemplateItem(data);
            }.bind(this))
        }.bind(this))
    },
    createTemplateItem:function(data){
        var _self = this;
        var templateItemContainer = new Element("div.templateItemContainer",{ styles:this.css.templateItemContainer }).inject(this.templateContainer);
        templateItemContainer.addEvents({
            mouseenter:function(){
                templateItemContainer.setStyles({"background-color":"rgb(242,245,247)"});
            }.bind(this),
            mouseleave:function(){
                templateItemContainer.setStyles({"background-color":""});
            }.bind(this),
            click:function(){
                // this.openTemplate(data.id)
            }.bind(this)
        });
        var templateItemContent = new Element("div.templateItemContent",{styles:this.css.templateItemContent}).inject(templateItemContainer);
        var templateItemTitle = new Element("div.templateItemTitle",{styles:this.css.templateItemTitle,text:data.title}).inject(templateItemContent);
        var templateItemDes = new Element("div.templateItemDes",{styles:this.css.templateItemDes,text:data.description==""?"无":data.description}).inject(templateItemContent);

        var templateItemLane = new Element("div.templateItemLane",{styles:this.css.templateItemLane}).inject(templateItemContainer);
        var templateItemLaneTxt = new Element("div.templateItemLaneTxt",{styles:this.css.templateItemLaneTxt,text:data.taskList.join(",")}).inject(templateItemLane);
        var templateItemOwner = new Element("div.templateItemOwner",{styles:this.css.templateItemOwner,text:data.owner.split("@")[0]}).inject(templateItemContainer);
        var templateItemDate = new Element("div.templateItemDate",{styles:this.css.templateItemDate,text:data.updateTime.split(" ")[0]}).inject(templateItemContainer);

        var templateItemActionContainer = new Element("div.templateItemActionContainer",{styles:this.css.templateItemActionContainer}).inject(templateItemContainer);
        var templateItemEdit = new Element("div.templateItemEdit",{ styles:this.css.templateItemEdit,text:this.lp.template.edit }).inject(templateItemActionContainer);
        templateItemEdit.addEvents({
            click:function(){
                this.openTemplate(data.id)
            }.bind(this)
        });
        var templateItemRemove = new Element("div.templateItemRemove",{ styles:this.css.templateItemRemove,text:this.lp.template.remove }).inject(templateItemActionContainer);
        templateItemRemove.addEvents({
            click:function(e){
                _self.app.confirm("warn",e,_self.app.lp.common.confirm.removeTitle,_self.app.lp.common.confirm.removeContent,300,120,function(){
                    _self.rootActions.ProjectTemplateAction.delete(data.id,function(){
                        _self.createTemplateLayout();
                        this.close();
                    }.bind(this))
                },function(){
                    this.close();
                });
            }
        });
    },
    openTemplate:function(id){
        var data = {
            id:id || ""
        }
        MWF.xDesktop.requireApp("TeamWork", "ProjectTemplate", function(){
            this.np = new MWF.xApplication.TeamWork.ProjectTemplate(this,data,
                {"width": 500,"height": 400,
                    onPostOpen:function(){
                        this.np.formAreaNode.setStyles({"top":"10px"});
                        var fx = new Fx.Tween(this.np.formAreaNode,{duration:200});
                        fx.start(["top"] ,"10px", "100px");

                    }.bind(this),
                    onPostClose:function(json){
                        if(json){
                            this.createTemplateLayout();
                        }
                    }.bind(this)
                }
            );
            this.np.open();
        }.bind(this));

    }
});
