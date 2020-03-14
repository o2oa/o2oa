(function(){
    debugger;
    if (Browser.iecomp){
        // if (!document.body.addEvent){
        //     document.body.addEvent = function(type, fn){
        //         if (window.attachEvent && !window.addEventListener){
        //             collected[Slick.uidOf(this)] = this;
        //         }
        //         if (this.addEventListener) this.addEventListener(type, fn, !!arguments[2]);
        //         else this.attachEvent('on' + type, fn);
        //         return this;
        //     };
        // }
        if (!Element.prototype.addEvent){
            Element.prototype.addEvent = function(type, fn){
                if (window.attachEvent && !window.addEventListener){
                    collected[Slick.uidOf(this)] = this;
                }
                if (this.addEventListener) this.addEventListener(type, fn, !!arguments[2]);
                else this.attachEvent('on' + type, fn);
                return this;
            };
        }

        var setStyleFun = Element.prototype.setStyle || null;
        Element.implement({
            "get": function(prop){
                var property = Element.Properties[prop];
                var p = (property && property.get) ? property.get.apply(this) : this.getProperty(prop);
                if (!p){
                    property = Element.Properties[prop.toString().toLowerCase()];
                    p = (property && property.get) ? property.get.apply(this) : this.getProperty(prop.toString().toLowerCase());
                }
                return p;
            }.overloadGetter(),
            "setStyle": function(property, value){
                if (setStyleFun){
                    try{
                        setStyleFun.apply(this, [property, value]);
                    }catch(e){}
                }
                return this;
            },
            // "addEventListener": function(e, listener, capture){
            //     this.addEvent(e, listener, capture);
            // }
        });
        // var insertRow = HTMLTableElement.prototype.insertRow;
        // var insertCell = HTMLTableRowElement.prototype.insertCell;
        // HTMLTableElement.prototype.insertRow = function(n){ return $(insertRow.call(this,n))};
        // HTMLTableRowElement.prototype.insertCell = function(n){ return $(insertCell.call(this,n))};

        // if (!HTMLGenericElement.prototype.addEventListener){
        //     HTMLGenericElement.prototype.addEventListener = function(e, listener, capture){
        //         this.attachEvent("on"+e, listener);
        //         //$(this).addEvent(e, listener, capture)
        //     }
        // }

        if (!Object.create){
            Object.create = function(o){
                return Object.clone(o);
            }
        }
        if (!document.createElementNS){
            document.createElementNS = function(uri, tag){
                var o = document.createElement(tag);
                o.setAttribute("xmlns", uri);
                return o;
            }
        }
        if (!Array.isArray){
            Array.isArray = function(o){
                return (typeOf(o)==="array");
            }
        }


        FormData = new Class({
            initialize: function(){
                this.items = [];
                this.type = "o2_formdata";
            },
            append: function(key, d){
                var o = {
                    "name": key,
                    "value": d
                };
                this.items.push(o);
            }
        });
        FormData.expiredIE = true;

        if (!Object.defineProperties){
            Object.defineProperties = MWF.defineProperties;
        }

        if (!Object.defineProperty || (Browser.name=="ie" && Browser.version==8)){
            Object.defineProperty = MWF.defineProperty;
        }
        o2.defineProperties(HTMLInputElement.prototype, {"files": {
                "get": function(){
                    o2.require("o2.widget.contentType", null, false);
                    var v = this.value;
                    var tmpv = v.replace(/\\/g, "/");
                    var i = tmpv.lastIndexOf("/");
                    var name = (i===-1) ? v : v.substr(i+1, tmpv.length-i);
                    var el = this;
                    var data = {
                        "path": v,
                        "name": name,
                        "size": 0,
                        "type": MWFContentType.get(v),
                        "el": el
                    };
                    return {
                        "length": (v) ? 1 : 0,
                        "data": data,
                        "item": function(){ return this.data; }
                    };
                }
            }});

        WebSocket = window.WebSocket || function(){};
        WebSocket.prototype = {
            'readyState': 0,
            'close': function(){}
        };
        HTMLCanvasElement = window.HTMLCanvasElement||function(){};

    }

})();
//debugger;

// (function(){
//
// })();
//
//
//
// if (Browser.iecomp){
//
// }