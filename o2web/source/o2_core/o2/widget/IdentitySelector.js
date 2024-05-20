o2.widget = o2.widget || {};
o2.require("o2.widget.Common", null, false);
o2.require("MWF.xDesktop.Dialog", null, false);
o2.widget.IdentitySelector = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "dialogStyle": "user",
        "title": o2.LP.widget.selectIdentity,
        "person": null,
        "identityList": null
    },
    initialize: function(container, options){
        this.setOptions(options);

        this.path = o2.session.path+"/widget/$IdentitySelector/";
        this.cssPath = o2.session.path+"/widget/$IdentitySelector/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.container = $(container);
        this.load();
    },
    load: function(){
        Promise.resolve( this.listIdentity() ).then(function (identityList){
             if( identityList.length < 2 ){
                 this.fireEvent( 'complete', identityList );
                 return;
             }else{
                this.openDialog();
             }
        }.bind(this));
    },
    listIdentity: function (){
        this.person = this.options.person || layout.session.user.id;
        var p = o2.Actions.load('x_organization_assemble_control').PersonAction.get(this.person).then(function (json){
            this.personData = json.data;
            return json.data.woIdentityList || [];
        }.bind(this));

      return Promise.resolve(p).then(function ( identitys ){
          this.identityList = identitys;
          if (this.identityList.length){
              if (this.options.identityList){
                  var identityList = typeOf( this.options.identityList ) === "array" ? this.options.identityList : [this.options.identityList];
                  this.identityList = this.identityList.filter(function(id){
                      for( var i=0; i<identityList.length; i++ ){
                          var identity = identityList[i] || "";
                          var dn = (typeOf(identity)==="string") ? identity : identity.distinguishedName;
                          id.index = i;
                          if( id.distinguishedName===dn )return true;
                      }
                      return false;
                  }.bind(this));

                  this.identityList.sort(function(a, b){
                      return a.index - b.index;
                  });
              }
          }
          return this.identityList;
      }.bind(this));
    },

    openDialog: function(){
        this.loadIdentityNodes();
        var size = layout.mobile ? this.container.getSize() : {x:'auto', y:'auto'};
        var _self = this;
        this.dialog = o2.DL.open({
            "title": this.options.title,
            "style": this.options.dialogStyle,
            "isResize": false,
            'content': this.identityArea,
            "maskNode": this.container,
            "width": size.x,
            "height": size.y,
            "onQueryClose": function(){

            }.bind(this),
            "onPostLoad": function () {
                if(layout.mobile)return;
                var dlg = this;
                if (!dlg || !dlg.node) return;
                dlg.node.setStyle("display", "block");
                var size = _self.identityArea.getSize();
                dlg.content.setStyles({
                    "height": size.y,
                    "width": size.x
                });

                var s = dlg.setContentSize();
                dlg.reCenter();
            }
        });
    },
    close: function (){
        this.fireEvent('close');
        this.dialog.close();
    },
    loadIdentityNodes: function(){
        this.identityArea = new Element('div', { styles: this.css.identityArea }).inject(document.body);
        this.identityList.each(function(item){
            new o2.widget.IdentitySelector.Identity(this.identityArea, item, this, this.css);
        }.bind(this));
    }
});

o2.widget.IdentitySelector.Identity = new Class({
    initialize: function(container, data, selector, style){
        this.container = $(container);
        this.data = data;
        this.selector = selector;
        this.style = style;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {
            "styles": (layout.mobile) ? this.style.identityNode_mobile : this.style.identityNode
        }).inject(this.container);

        this.nameNode = new Element("div", {
            "styles": this.style.identityInforNameNode
        }).inject(this.node);

        var url = MWF.Actions.get("x_organization_assemble_control").getPersonIcon(this.selector.personData.id);
        var img = "<img width='50' height='50' border='0' src='"+url+"'></img>";

        this.picNode = new Element("div", {
            "styles": this.style.identityInforPicNode,
            "html": img
        }).inject(this.nameNode);
        this.nameTextNode = new Element("div", {
            "styles": this.style.identityInforNameTextNode,
            "text": this.data.name
        }).inject(this.nameNode);

        this.unitNode = new Element("div", {"styles": this.style.identityDepartmentNode}).inject(this.node);
        this.unitTitleNode = new Element("div", {
            "styles": this.style.identityTitleNode,
            "text": o2.LP.widget.unit
        }).inject(this.unitNode);
        this.unitTextNode = new Element("div", {"styles": this.style.identityTextNode}).inject(this.unitNode);
        if (this.data.woUnit) this.unitTextNode.set({"text": this.data.woUnit.levelName, "title": this.data.woUnit.levelName});

        this.dutyNode = new Element("div", {"styles": this.style.identityDutyNode}).inject(this.node);
        this.dutyTitleNode = new Element("div", {
            "styles": this.style.identityTitleNode,
            "text": o2.LP.widget.duty
        }).inject(this.dutyNode);
        this.dutyTextNode = new Element("div", {"styles": this.style.identityTextNode}).inject(this.dutyNode);
        var dutyTextList = [];
        var dutyTitleList = [];
        this.data.woUnitDutyList.each(function(duty){
            dutyTextList.push(duty.name);
            if (duty.woUnit) dutyTitleList.push(duty.name+"("+duty.woUnit.levelName+")");
        }.bind(this));
        this.dutyTextNode.set({"text": dutyTextList.join(", "), "title": dutyTitleList.join(", ")});

        this.unitTextNode = new Element("div.mainColor_color", {
            "styles": this.style.identityInforUnitTextNode,
            "text": "【"+this.data.woUnit.name+"】"
        }).inject(this.node);

        this.setEvent();
    },
    setEvent: function (){
        this.node.addEvents({
            "mouseover": function(){
                this.node.addClass("mainColor_border");
                this.node.setStyles(this.style.identityNode_over);
                this.nameTextNode.setStyles(this.style.identityInforNameTextNode_over);
                this.unitTitleNode.setStyles(this.style.identityTitleNode_over);
                this.dutyTitleNode.setStyles(this.style.identityTitleNode_over);
            }.bind(this),
            "mouseout": function(){
                this.node.removeClass("mainColor_border");
                this.node.setStyles((layout.mobile) ? this.style.identityNode_mobile : this.style.identityNode);
                this.nameTextNode.setStyles(this.style.identityInforNameTextNode);
                this.unitTitleNode.setStyles(this.style.identityTitleNode);
                this.dutyTitleNode.setStyles(this.style.identityTitleNode);
            }.bind(this),
            "click": function(){
                this.selector.fireEvent('complete', [this.data]);
                this.selector.close();
            }.bind(this),
        });
    }
});