MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xApplication.TeamWork.ProjectTemplate = new Class({
    Extends: MWF.xApplication.TeamWork.Common.Popup,
    options: {
        "closeByClickMask": false
    },
    open: function (e) {
        //设置css 和 lp等
        var css = this.css;
        this.cssPath = "/x_component_TeamWork/$ProjectTemplate/" + this.options.style + "/css.wcss";
        this._loadCss();
        if (css) this.css = Object.merge(css, this.css);

        this.lp = this.app.lp.bam.template;


        this.fireEvent("queryOpen");
        this.isNew = false;
        this.isEdited = false;
        this._open();
        this.fireEvent("postOpen");
    },

    _createTableContent: function () {
        if(this.data.id && this.data.id!=""){
            this.isEdited = true
        }else{
            this.isEdited = false;
        }

        var topTitleContainer = new Element("div.topTitleContainer",{styles:this.css.topTitleContainer}).inject(this.formTableArea);
        var topTitle = new Element("div.topTitle",{styles:this.css.topTitle,text:this.lp.title}).inject(topTitleContainer);

        var templateNameTxt = new Element("div.templateNameTxt",{styles:this.css.templateNameTxt,text:this.lp.name}).inject(this.formTableArea);
        var templateNameContainer = new Element("div.templateNameContainer",{styles:this.css.templateNameContainer}).inject(this.formTableArea);
        this.templateNameInput = new Element("input",{type:"text",styles:this.css.templateNameInput,value:""}).inject(templateNameContainer);
        this.templateNameInput.addEvents({
            focus:function(){
                this.setStyles({"border":"1px solid #1b9aee"});
            },
            blur:function(){
                if(this.get("value").trim()==""){
                    this.setStyles({"border":"1px solid #ff0000"});
                }else{
                    this.setStyles({"border":"1px solid #cccccc"});
                }
            }
        })

        var templateDesTxt = new Element("div.templateDesTxt",{styles:this.css.templateDesTxt,text:this.lp.description}).inject(this.formTableArea);
        var templateDesContainer = new Element("div.templateDesContainer",{styles:this.css.templateDesContainer}).inject(this.formTableArea);
        this.templateDesInput = new Element("textarea.templateDesInput",{styles:this.css.templateDesInput}).inject(templateDesContainer);
        this.templateDesInput.addEvents({
            focus:function(){
                this.setStyles({"border":"1px solid #1b9aee"});
            }
        })
        var templateLaneTxt = new Element("div.templateLaneTxt",{styles:this.css.templateLaneTxt,text:this.lp.lane+"("+this.lp.laneTip+")"}).inject(this.formTableArea);
        var templateLaneContainer = new Element("div.templateLaneContainer",{styles:this.css.templateLaneContainer}).inject(this.formTableArea);
        this.templateLaneInput = new Element("input",{type:"text",styles:this.css.templateLaneInput,value:"",placeholder:this.lp.laneTip}).inject(templateLaneContainer);
        this.templateLaneInput.addEvents({
            focus:function(){
                this.setStyles({"border":"1px solid #1b9aee"});
            },
            blur:function(){
                if(this.get("value").trim()==""){
                    this.setStyles({"border":"1px solid #ff0000"});
                }else{
                    this.setStyles({"border":"1px solid #cccccc"});
                }
            }
        })

        var templateActionContainer = new Element("div.templateActionContainer",{styles:this.css.templateActionContainer}).inject(this.formTableArea);
        this.closeAction = new Element("div.okAction",{styles:this.css.closeAction,text:this.lp.close}).inject(templateActionContainer);
        this.closeAction.addEvent("click",function(){ this.close(); }.bind(this))
        this.okAction = new Element("div.okAction",{styles:this.css.okAction,text:this.lp.ok}).inject(templateActionContainer);
        this.okAction.addEvents({
            click:function(){
                var flag = true;
                if(this.templateNameInput.get("value").trim()==""){
                    this.templateNameInput.setStyles({"border":"1px solid #ff0000"});
                    flag = false;
                }
                if(this.templateLaneInput.get("value").trim()==""){
                    this.templateLaneInput.setStyles({"border":"1px solid #ff0000"});
                    flag = false;
                }

                if(flag){
                    var data = {}
                    if(this.isEdited) data.id = this.data.id;
                    data.title = this.templateNameInput.get("value").trim();
                    data.description = this.templateDesInput.get("value").trim();
                    data.taskList = this.templateLaneInput.get("value").split(",");

                    this.rootActions.ProjectTemplateAction.save(data,function(json){
                        this.close(json);
                    }.bind(this))
                }
            }.bind(this)
        })

        if(this.isEdited){
            this.getTemplate(this.data.id,function(json){
                this.templateNameInput.set("value",json.title);
                this.templateDesInput.set("value",json.description);
                this.templateLaneInput.set("value",json.taskList.join(","));
            }.bind(this))
        }

    },
    getTemplate:function(id,callback){
        this.rootActions.ProjectTemplateAction.get(id,function(json){
            callback(json.data)
        }.bind(this));
    }

});
