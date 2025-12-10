o2.widget = o2.widget || {};
o2.require("o2.widget.Menu", null, false);
MWF.widget.IconMenu = new Class({
    Implements: [Options, Events],
    Extends: o2.widget.Common,
    options: {
        "zIndex": '',
        "iconType": "font",
        "pngIconPath": "../x_component_process_FormDesigner/widget/$ActionsEditor/default/tools/{index}.png",
        'pngIconCount': 136
    },
    initialize: function(options){
        this.setOptions(options);
    },
    load: function(target, container){
        var input;
        var _self = this;
        const iconMenu = new MWF.widget.Menu(target, {
            "event": "click",
            "style": "actionbarIcon",
            "container": container,
            "onPostShow" : function (ev) {
                this.node.setStyles({
                    'max-height': '98%',
                    'overflow': 'auto'
                });
                if(_self.options.zIndex){
                    this.node.setStyle( 'z-index', _self.options.zIndex);
                }
                ev.stopPropagation();
            },
            "onPostHide": function(){
                if(input && input.value){
                    input.value = '';
                    iconMenu.items.forEach(function(i){
                        i.item.setStyle( 'display', '' );
                    });
                }
            }
        });
        this.iconMenu = iconMenu;
        iconMenu.load();
        var _self = this;
        if (this.options.iconType==='font'){

            input = new Element('oo-input', {
                'right-icon': 'search',
                'style': 'display:block;padding:0.3rem 0.5rem;',
                'placeholder': '输入关键字搜索',
                'autocomplete': "off"
            }).inject( iconMenu.node );
            input.addEvent('click', (ev)=>{
                ev.stopPropagation();
            });
            input.addEvent('mousedown', (ev)=>{
                ev.stopPropagation();
            });
            input.addEvent('input', (ev)=>{
                iconMenu.items.forEach(function(i){
                    if( !input.value ){
                        i.item.setStyle( 'display', '' );
                    }else{
                        i.item.setStyle( 'display', i.item.iconName.indexOf(input.value)>-1 ? '' : 'none' );
                    }
                })
                ev.stopPropagation();
            });

            o2.JSON.get("../x_desktop/css/v10/ooicon.json", function(json){
                const icons = json.glyphs;

                icons.forEach(function(i){
                    var item = iconMenu.addMenuItem("", "click", function(ev){
                        var icon = this.item.iconName;
                        _self.fireEvent('click', [ev, icon]);
                        ev.stopPropagation();
                    });
                    item.item.addClass("ooicon-"+i.font_class);
                    item.item.setStyles({
                        "text-align": "center",
                        "line-height": "28px",
                        "font-size": "16px"
                    });
                    item.item.set('title', i.name);
                    item.item.iconName = i.font_class;

                }.bind(this));
            }.bind(this));
        }else{
            for (var i=1; i<=this.options.pngIconCount; i++){
                var icon = this.options.pngIconPath.replace('{index}', i);
                var item = iconMenu.addMenuItem("", "click", function(ev){
                    var src = this.item.getElement("img").get("src");
                    _self.fireEvent('click', [ev, src]);
                    ev.stopPropagation();
                }, icon);
                item.iconName = i+".png";
            }
        }
    },
    hide: function(){
        if(this.iconMenu){
            this.iconMenu.hide();
        }
    }
});