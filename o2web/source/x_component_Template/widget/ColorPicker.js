MWF.xApplication.Template = MWF.xApplication.Template || {};
MWF.xApplication.Template.widget = MWF.xApplication.Template.widget || {};
MWF.xDesktop.requireApp("Template", "MTooltips", null, false);
MWF.xApplication.Template.widget.ColorPicker = new Class({
    Implements: [Options, Events],
    Extends: MTooltips,
    options: {
        style : "default",
        axis: "y",      //箭头在x轴还是y轴上展现
        position : { //node 固定的位置
            x : "auto", //x 轴上left center right, auto 系统自动计算
            y : "auto" //y轴上top middle bottom,  auto 系统自动计算
        },
        //event : "click", //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
        nodeStyles : {
            "min-width" : "200px"
        }
    },
    initialize : function( container, target, app, data, options, targetCoordinates ){
        //可以传入target 或者 targetCoordinates，两种选一
        //传入target,表示触发tooltip的节点，本类根据 this.options.event 自动绑定target的事件
        //传入targetCoordinates，表示 出发tooltip的位置，本类不绑定触发事件
        if( options ){
            this.setOptions(options);
        }
        this.container = container;
        this.target = target;
        this.targetCoordinates = targetCoordinates;
        this.app = app;
        if(app)this.lp = app.lp;
        this.data = data;

        this.path = "/x_component_Template/widget/$ColorPicker/";
        this.cssPath = "/x_component_Template/widget/$ColorPicker/"+this.options.style+"/css.wcss";
        this._loadCss();

        if( this.target ){
            this.setTargetEvents();
        }
    },
    _loadCss: function(reload){
        var key = encodeURIComponent(this.cssPath);
        if (!reload && MWF.widget.css[key]){
            this.css = MWF.widget.css[key];
        }else{
            this.cssPath = (this.cssPath.indexOf("?")!=-1) ? this.cssPath+"&v="+COMMON.version : this.cssPath+"?v="+COMMON.version;
            var r = new Request.JSON({
                url: this.cssPath,
                secure: false,
                async: false,
                method: "get",
                noCache: false,
                onSuccess: function(responseJSON, responseText){
                    this.css = responseJSON;
                    MWF.widget.css[key] = responseJSON;
                }.bind(this),
                onError: function(text, error){
                    alert(error + text);
                }
            });
            r.send();
        }
    },
    _customNode : function( node ){
        MWF.UD.getDataJson("colorPicker", function(json) {

            this.contentNode = new Element("div").inject( this.node );

            this.previewArea = new Element("div", {styles : this.css.previewArea }).inject( this.contentNode );
            this.previewNode = new Element("div", {styles : this.css.previewNode }).inject( this.previewArea );
            this.previewValueNode = new Element("div", {styles : this.css.previewValueNode }).inject( this.previewArea );

            if( json ){
                this.laterPickedColor = json.laterPickedColor || [];
                this.createLaterPickedNode( this.laterPickedColor );
            }
            this.themeArea = new Element("div").inject( this.contentNode );
            new Element("div", {styles : this.css.titleNode, text : "主题颜色" }).inject( this.themeArea );

            this.themeNode = new Element("div", {styles : this.css.colorContainer }).inject( this.themeArea );
            var themeColorList = [
                "#ffffff","#000000","#eeece1","#1f497d","#4f81bd","#c0504d","#9bbb59","#8064a2","#4bacc6","#f79646"
            ];
            for( var i=0; i<themeColorList.length; i++ ){
                this.createColorNode( themeColorList[i], this.themeNode );
            }

            this.theme2Node = new Element("div", {styles : this.css.colorContainer }).inject( this.themeArea );
            this.theme2Node.setStyle( "margin-bootom" , "5px" );
            var themeColorList2 = [
                "#f2f2f2","#808080","#ddd8c2","#c6d9f1","#dbe5f1","#f2dbdb","#eaf1dd","#e5dfec","#daeef3","#fde9d9",
                "#d9d9d9","#595959","#c4bc96","#8db3e2","#b8cce4","#e5b8b7","#d6e3bc","#ccc0d9","#b6dde8","#fbd4b4",
                "#bfbfbf","#404040","#938953","#548dd4","#95b3d7","#d99594","#c2d69b","#b2a1c7","#92cddc","#fabf8f",
                "#a6a6a6","#262626","#4a442a","#17365d","#365f91","#943634","#76923c","#5f497a","#31849b","#e36c0a",
                "#7f7f7f","#0d0d0d","#1c1a10","#0f243e","#243f60","#622423","#4e6128","#3f3151","#205867","#974706"
            ];
            for( var i=0; i<themeColorList2.length; i++ ){
                this.createColorNode( themeColorList2[i], this.theme2Node );
            }

            this.standardArea = new Element("div").inject( this.contentNode );
            new Element("div", {styles : this.css.titleNode, text : "标准颜色" }).inject( this.standardArea );
            this.standardNode = new Element("div", {styles : this.css.colorContainer }).inject( this.standardArea );
            var standardColorList = [
                "#c00000", "#ff0000","#ffc000","#ffff00","#92d050","#00b050","#00b0f0","#0070c0","#002060","#7030a0"
            ];
            for( var i=0; i<standardColorList.length; i++ ){
                this.createColorNode( standardColorList[i], this.standardNode );
            }

        }.bind(this));


        //this.resultInput = new Element("input").inject(this.contentNode);
    },
    createColorNode : function( color, container ){
        var _self = this;
        var colorNode = new Element("div", { styles : {
            border : "1px solid "+color,
            "background-color" : color
        }, events : {
            mouseover : function(){
                _self.previewNode.setStyle("background-color",this);
                _self.previewValueNode.set("text",this);
            }.bind( color ),
            click : function(){
                _self.clickColorNode( this );
                _self._select( this );
                _self.fireEvent("select",[this]);
                _self.hide();
            }.bind(color)
        }}).inject( container );
        colorNode.setStyles( this.css.colorNode )
    },
    clickColorNode : function( color ){
        var list = this.laterPickedColor = this.laterPickedColor || [];
        list.unshift( color );
        list = this.unique( list );
        list = list.slice(0, 10);
        MWF.UD.putData("colorPicker", {
            "laterPickedColor": list
        }, null, false);
        this.createLaterPickedNode( list );
    },
    createLaterPickedNode : function( colorList ){
        if( this.laterPickedArea ){
            this.laterPickedNode.empty();
        }else{
            this.laterPickedArea = new Element("div").inject( this.contentNode );
            new Element("div", {styles : this.css.titleNode, text : "最近使用" }).inject( this.laterPickedArea );
            this.laterPickedNode = new Element("div", {styles : this.css.colorContainer }).inject( this.laterPickedArea );
        }
        for( var i=0; i< colorList.length; i++ ){
            this.createColorNode( colorList[i], this.laterPickedNode );
        }
    },
    unique : function( array ){
        var res = [];
        var json = {};
        for(var i = 0; i < array.length; i++){
            if(!json[array[i]]){
                res.push(array[i]);
                json[array[i]] = 1;
            }
        }
        return res;
    },
    _select: function( color ){

    }
});

