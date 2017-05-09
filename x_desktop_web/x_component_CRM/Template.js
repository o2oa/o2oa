MWF.xApplication.CRM = MWF.xApplication.CRM || {};
MWF.xApplication.CRM.Template = MWF.xApplication.CRM.Template || {};

MWF.xApplication.CRM.Template.Select = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": "800",
        "height": "100%"
    },
    initialize: function (node ,explorer, actions, options) {
        this.setOptions(options);
        this.app = explorer.app;
        this.explorer = explorer;
        this.lp = this.app.lp.template;
        this.actions = this.app.restActions;
        this.path = "/x_component_CRM/Template/";
        this.loadCss();

        this.node = $(node);
        this.actions = actions;
    },
    loadCss: function () {
        this.cssPath = "/x_component_CRM/$Template/" + this.options.style + "/css.wcss";
        this._loadCss();
    },

    load:function(data,callback){
        this.data = data || {};
        var _width = this.options.width?this.options.width:230;
        var _height = this.options.height?this.options.height:30;
        var _available = this.options.available?this.options.available:false;

        if(this.node)this.node.empty();
        this.selectValueDiv = new Element("div.selectValueDiv",{
            "styles":this.css.selectValueDiv,
            "id":this.node.get("id")+"Value",
            "text":this.lp.defaultSelect
        }).inject(this.node);

        this.selectArrowDiv = new Element("div.selectArrowDiv",{
            "styles":this.css.selectArrowDiv
        }).inject(this.node);
        this.selectArrowDiv.setStyles({
            "width":_height+"px",
            "height":_height+"px"
        });

        this.node.setStyles(this.css.selectDiv);
        this.node.setStyles({
            "width":_width+"px",
            "height":_height+"px",
            "background-color":_available?"#999999":""
        });
        this.node.set("available",_available);

        this.selectValueDiv.setStyles({
            "width":(_width-_height-10)+"px",
            "height":_height+"px",
            "line-height":_height+"px"
        });
        this.explorer.allArrowArr.push(this.selectArrowDiv);

        this.setList(this.data,callback);
    },
    setList:function(data,callback){
        data = data || {};
        var _self = this;
        this.node.addEvents({
            "click":function(e){
                if(_self.node.get("available")=="true") return false;
                _self.selectArrowDiv.setStyles({
                    "background":"url(/x_component_CRM/$Template/default/icons/arrow-up.png) no-repeat center"
                });
                if(_self.explorer.listContentDiv)_self.explorer.listContentDiv.destroy();
                if(_self.explorer.listDiv)_self.explorer.listDiv.destroy();
                _self.explorer.listContentDiv = new Element("div.listContentDiv",{"styles":_self.css.listContentDiv,"id":"listContentDiv"}).inject(_self.node);
                _self.explorer.listContentDiv.setStyles({
                    "width":_self.node.getSize().x+"px",
                    "margin-top":(_self.node.getSize().y)+"px",
                    "z-index":"300"
                });

                _self.listDiv = new Element("div.listDiv",{"styles":_self.css.listDiv}).inject(_self.explorer.listContentDiv);
                _self.app.setScrollBar(_self.listDiv);

                data.childNodes.unshift({
                    "configname":_self.lp.defaultSelect
                });













                data.childNodes.each(function(d){
                    var listLi = new Element("li.listLi",{
                        "styles":_self.css.listLi,
                        "text": d.configname
                    }).inject(_self.listDiv);
                    listLi.setStyles({
                        "color":_self.selectValueDiv.get("text")==listLi.get("text")?"#ffffff":"",
                        "background-color":_self.selectValueDiv.get("text")==listLi.get("text")?"#3d77c1":""
                    });
                    listLi.addEvents({
                        "click":function(ev){
                            _self.selectValueDiv.set({"text":this.get("text")});
                            _self.node.set("value",this.get("text"));
                            _self.explorer.listContentDiv.destroy();
                            _self.selectArrowDiv.setStyles({"background":"url(/x_component_CRM/$Template/default/icons/arrow.png) no-repeat center"});
                            if(callback)callback();
                            ev.stopPropagation();
                        },
                        "mouseover":function(){
                            if(this.get("text") != _self.selectValueDiv.get("text")){
                                this.setStyles({
                                    "background-color":"#ccc",
                                    "color":"#ffffff"
                                });
                            }
                        },
                        "mouseout":function(){
                            if(this.get("text") != _self.selectValueDiv.get("text")){
                                this.setStyles({
                                    "background-color":"",
                                    "color":""
                                });
                            }
                        }
                    });
                }.bind(_self));

                data.childNodes.splice(0,1);

                e.stopPropagation();

            }.bind(_self)
        })

    }
});