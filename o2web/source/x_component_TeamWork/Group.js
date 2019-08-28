MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xApplication.TeamWork.Group = new Class({
    Extends: MWF.xApplication.TeamWork.Common.ToolTips,
    options : {
        // displayDelay : 300,
        hasArrow:false,
        event:"click"
    },
    _loadCustom : function( callback ){
        this.topBar = new Element("div.topBar",{styles:this.css.tooltip.group.topBar}).inject(this.contentNode);
        var tt = this.lp.group.topText;
        if(this.data.do == "edit"){
            tt = this.lp.group.topTextEdit;
        }

        this.topBarText = new Element("div.topBarText",{styles:this.css.tooltip.group.topBarText,text:tt}).inject(this.topBar);
        this.topBarClose = new Element("div.topBarClose",{styles:this.css.tooltip.group.topBarClose}).inject(this.topBar);
        this.topBarClose.addEvents({
            click:function(){this.hide()}.bind(this)
        });
        this.groupInContainer = new Element("div.groupInContainer",{styles:this.css.tooltip.group.groupInContainer}).inject(this.contentNode);
        this.groupIn = new Element("input.groupIn",{styles:this.css.tooltip.group.groupIn,type:"text",placeholder:this.lp.group.groupIn}).inject(this.groupInContainer);
        if(this.data.do == "edit"){
            if(this.data.name) this.groupIn.set("value",this.data.name)
        }
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

        this.groupAdd = new Element("div.groupAdd",{styles:this.css.tooltip.group.groupAdd,text:this.lp.group.groupAdd}).inject(this.contentNode);
        if(this.data.do == "edit"){
            this.groupAdd.setStyles({
                "cursor":"pointer",
                "background-color":"#4A90E2",
                "color":"#FFFFFF"
            })
        }
        this.groupAdd.addEvents({
            click:function(){
                if(this.groupIn.get("value").trim()=="") return;
                //var json = {
                //  "do":this.data.do,
                //  "title":  this.groupIn.get("value").trim()
                //};

                var data = {
                    "name":this.groupIn.get("value").trim()
                };
                if(this.data.do == "edit"){
                    if(this.data.id){
                        data.id = this.data.id;
                    }
                }

                this.actions.groupSave(data,function(json){
                    this.close(json.data);
                }.bind(this),function(){
                    //alert("err")
                }.bind(this));

                //this.hide();
            }.bind(this)
        });

        if(callback)callback();
    }


});