/** ***** BEGIN LICENSE BLOCK *****
 * |------------------------------------------------------------------------------|
 * | O2OA 活力办公 创意无限    o2.core.js                                            |
 * |------------------------------------------------------------------------------|
 * | Distributed under the AGPL license:                                          |
 * |------------------------------------------------------------------------------|
 * | Copyright © 2018, o2oa.net, o2server.io O2 Team                              |
 * | All rights reserved.                                                         |
 * |------------------------------------------------------------------------------|
 *
 *  This file is part of O2OA.
 *
 *  O2OA is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  O2OA is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ******/

(function (){
    o2.getCenterPosition = function(el, width, height){
        var elPositon = $(el).getPosition();
        var elSize = $(el).getSize();
        var node = $("layout");
        var size = (node) ? $(node).getSize() : $(document.body).getSize();

        var top = (elPositon.y+elSize.y)/2 - (height/2);
        var left = (elPositon.x+elSize.x)/2-(width/2);
        if ((left+width)>size.x){
            left = size.x-width-10;
        }
        if ((top+height)>size.y){
            top = size.y-height-10;
        }

        return {"x": left, "y": top};
    };
    o2.getMarkSize = function(node){
        var size;
        if (!node){
            size = $(document.body).getSize();
            var winSize = $(window).getSize();

            var height = size.y;
            var width = size.x;

            if (height<winSize.y) height = winSize.y;
            if (width<winSize.x) width = winSize.x;

            return {x: size.x, y: height};
        }else{
            size = $(node).getSize();
            return {x: size.x, y: size.y};
        }
    };
    o2.json = function(jsonString, fun){
        var obj = JSON.decode(jsonString);
        var p = fun.split(".");
        var tmp = obj;
        p.each(function(item){
            if (item.indexOf("[")!==-1){
                var x = item.split("[");
                var i = parseInt(x[1].substr(0, x[1].indexOf("]")));
                tmp = tmp[x[0]][i];
            }else{
                tmp = tmp[item];
            }
        });
        return tmp;
    };
    o2.getHTMLTemplate = function(url, callback, async){
        var loadAsync = (async !== false);
        var res = new Request.HTML({
            url: url,
            async: loadAsync,
            method: "get",
            onSuccess: function(responseTree, responseElements, responseHTML, responseJavaScript){
                o2.runCallback(callback, "success", [responseTree, responseElements, responseHTML, responseJavaScript]);
            }.bind(this),
            onFailure: function(xhr){
                o2.runCallback(callback, "requestFailure", [xhr]);
            }
        });
        res.send();
    };

    o2.getRequestText = function(url, callback, async){
        var loadAsync = (async !== false);

        url = (url.indexOf("?")!==-1) ? url+"&v="+o2.version.v : url+"?v="+o2.version.v;
        var res = new Request({
            url: url,
            async: loadAsync,
            method: "get",
            onSuccess: function(responseText, responseXML){
                o2.runCallback(callback, "success",[responseText, responseXML]);
            }.bind(this),
            onFailure: function(xhr){
                o2.runCallback(callback, "requestFailure",[xhr]);
            }
        });
        res.send();
    };

    o2.encodeJsonString = function(str){
        var tmp = [str];
        var dataStr = (JSON.encode(tmp));
        return dataStr.substr(2, dataStr.length-4);
    };
    o2.decodeJsonString = function(str){
        var tmp = "[\""+str+"\"]";
        var dataObj = (JSON.decode(tmp));
        return dataObj[0];
    };

    o2.getTextSize = function(text, styles){
        var tmpSpan = new Element("span", {
            "text": text,
            "styles": styles
        }).inject($(document.body));
        var size = tmpSpan.getSize();
        tmpSpan.destroy();
        return size;
    };
    o2.getCenter = function(size, target, offset){
        if (!target) target = document.body;
        var targetSize = target.getSize();
        var targetPosition = target.getPosition(offset);
        var targetScroll = target.getScroll();

        var x = targetSize.x/2;
        var y = targetSize.y/2;

        x = x-(size.x/2);
        y = y-(size.y/2);
        x = x+targetPosition.x;
        y = y+targetPosition.y;
        x = x+targetScroll.x;
        y = y+targetScroll.y;

        return {"x": x, "y": y};
    };
    o2.getEPointer = function(e){
        var x = 0;
        var y = 0;
        if (typeOf(e)=="element"){
            var position = e.getPosition(this.content);
            x = position.x;
            y = position.y;
        }else{
            if (Browser.name=="firefox"){
                x = parseFloat(e.event.clientX || e.event.x);
                y = parseFloat(e.event.clientY || e.event.y);
            }else{
                x = parseFloat(e.event.x);
                y = parseFloat(e.event.y);
            }

            if (e.target){
                var position = e.target.getPosition(this.content);
                x = position.x;
                y = position.y;
            }
            //    }
        }
        return {"x": x, "y": y};
    };
    o2.getParent = function(node, tag){
        var pNode = node.parentElement;
        while(pNode && pNode.tagName.toString().toLowerCase() !== tag.toString().toLowerCase()){
            pNode = pNode.parentElement;
        }
        return pNode;
    };
    o2.getOffset = function(evt){
        if (Browser.name==="firefox"){
            return {
                "offsetX": evt.layerX,
                "offsetY": evt.layerY
            };
        }else{
            return {
                "offsetX": evt.offsetX,
                "offsetY": evt.offsetY
            }
        }
    };

    if (String.implement) String.implement({
        "getAllIndexOf": function(str){
            var idxs= [];
            var idx = this.indexOf(str);
            while (idx !== -1){
                idxs.push(idx);
                idx = this.indexOf(str, idx+1);
            }
            return idxs;
        }
    });
    if (Array.implement) Array.implement({
        "trim": function(){
            var arr = [];
            this.each(function(v){
                if (v) arr.push(v);
            });
            return arr;
        },
        "isIntersect": function(arr){
            return this.some(function(item){ return (arr.indexOf(item)!==-1); })
        }
    });
    if (window.Element && Element.implement) Element.implement({
        "isIntoView": function() {
            // var pNode = this.getParent();
            // while (pNode && ((pNode.getScrollSize().y-(pNode.getComputedSize().height+1)<=0) || pNode.getStyle("overflow")==="visible")) pNode = pNode.getParent();
            //
            var pNode = this.getParentSrcollNode();

            if (!pNode) pNode = document.body;
            var size = pNode.getSize();
            var srcoll = pNode.getScroll();
            var p = (pNode == window) ? {"x":0, "y": 0} : this.getPosition(pNode);
            var nodeSize = this.getSize();
            //return (p.x-srcoll.x>=0 && p.y-srcoll.y>=0) && (p.x+nodeSize.x<size.x+srcoll.x && p.y+nodeSize.y<size.y+srcoll.y);
            return (p.x-srcoll.x>=0 && p.y>=0) && (p.x+nodeSize.x<size.x+srcoll.x && p.y+nodeSize.y<size.y)
        },
        "appendHTML": function(html, where){
            if (this.insertAdjacentHTML){
                var whereText = "beforeEnd";
                if (where==="before") whereText = "beforeBegin";
                if (where==="after") whereText = "afterEnd";
                if (where==="bottom") whereText = "beforeEnd";
                if (where==="top") whereText = "afterBegin";
                this.insertAdjacentHTML(whereText, html);
            }else {
                if (where==="bottom") this.innerHTML = this.innerHTML+html;
                if (where==="top") this.innerHTML = html+this.innerHTML;
            }
        },
        "positionTo": function(x,y){
            var left = x.toFloat();
            var top = y.toFloat();
            var offsetNode = this.getOffsetParent();
            if (offsetNode){
                var offsetPosition = offsetNode.getPosition();
                left = left-offsetPosition.x;
                top = top-offsetPosition.y;
            }
            this.setStyles({"top": top, "left": left});
            return this;
        },
        "getBorder": function(){
            var positions = ["top", "left", "right", "bottom"];
            var styles = ["color", "style", "width"];

            var obj = {};
            positions.each(function (position){
                styles.each(function(style){
                    var key = "border-"+position+"-"+style;
                    obj[key] = this.getStyle(key);
                }.bind(this));
            }.bind(this));

            return obj;
        },
        "isOutside": function(e){
            var elementCoords = this.getCoordinates();
            var targetCoords  = this.getCoordinates();
            if(((e.page.x < elementCoords.left || e.page.x > (elementCoords.left + elementCoords.width)) ||
                    (e.page.y < elementCoords.top || e.page.y > (elementCoords.top + elementCoords.height))) &&
                ((e.page.x < targetCoords.left || e.page.x > (targetCoords.left + targetCoords.width)) ||
                    (e.page.y < targetCoords.top || e.page.y > (targetCoords.top + targetCoords.height))) ) return true;

            return false;
        },
        "getAbsolutePosition":function(){
            var styleLeft = 0;
            var styleTop = 0;
            var node = this;

            styleLeft = node.offsetLeft;
            styleTop = node.offsetTop;

            node = node.parentElement;

            while (node && node.tagName.toString().toLowerCase()!=="body"){
                styleLeft += node.offsetLeft;
                styleTop += node.offsetTop;
                node = node.offsetParent;
            }
            return {x: styleLeft, y: styleTop};
        },
        "tweenScroll": function(to, time){
            if (!this.tweenScrollQueue){
                this.tweenScrollQueue = [];
            }
            if (this.tweenScrollQueue.length){
                this.tweenScrollQueue.push(to);
            }else{
                this.tweenScrollQueue.push(to);
                this.doTweenScrollQueue(time);
            }
        },
        "doTweenScrollQueue": function(time){
            if (this.tweenScrollQueue.length){
                var i = this.tweenScrollQueue.length;
                var to = this.tweenScrollQueue[this.tweenScrollQueue.length-1];

                var scroll = this.getScroll();
                var dy = to - scroll.y;
                var step = dy/time;
                var count = 0;
                var move = 0;

                var id = window.setInterval(function(){

                    this.scrollTo(0, scroll.y+count*step);
                    count++;
                    if (count>time){
                        window.clearInterval(id);
                        for (var x=1; x<=i; x++) this.tweenScrollQueue.shift();
                        if (this.tweenScrollQueue.length) this.doTweenScrollQueue(time);
                    }
                }.bind(this), 1);
            }
        },
        "isPointIn": function(px, py, offX, offY, el){
            if (!offX) offX = 0;
            if (!offY) offY = 0;
            var position = this.getPosition(el);
            var size = this.getSize();
            return (position.x-offX<=px && position.x+size.x+offX>=px && position.y-offY<=py && position.y+size.y+offY>=py);
        },
        "isInPointInRect": function(sx, sy, ex, ey){
            var position = this.getPosition();
            var size = this.getSize();

            var p1 = {"x": position.x, "y": position.y};
            var p2 = {"x": position.x+size.x, "y": position.y};
            var p3 = {"x": position.x+size.x, "y": position.y+size.y};
            var p4 = {"x": position.x, "y": position.y+size.y};

            var sp = {"x": Math.min(sx, ex), "y": Math.min(sy, ey)};
            var ep = {"x": Math.max(sx, ex), "y": Math.max(sy, ey)};

            if (p1.x>=sp.x && p1.y>=sp.y && p1.x<=ep.x && p1.y<=ep.y) return true;
            if (p2.x>=sp.x && p2.y>=sp.y && p2.x<=ep.x && p2.y<=ep.y) return true;
            if (p3.x>=sp.x && p3.y>=sp.y && p3.x<=ep.x && p3.y<=ep.y) return true;
            if (p4.x>=sp.x && p4.y>=sp.y && p4.x<=ep.x && p4.y<=ep.y) return true;
            if (p3.x>=sp.x && p3.y>=sp.y && p1.x<=sp.x && p1.y<=sp.y) return true;
            if (p3.x>=ep.x && p3.y>=ep.y && p1.x<=ep.x && p1.y<=ep.y) return true;
            if (p1.x<=sp.x && p2.x>=sp.x && p1.y>=sp.y && p4.y<=ep.y) return true;
            if (p1.y<=sp.y && p4.y>=sp.y && p1.x>=sp.x && p2.x<=ep.x) return true;

            return false;
        },
        "isOverlap": function(node){
            var p = node.getPosition();
            var s = node.getSize();
            return this.isInPointInRect(p.x, p.y, p.x+s.x, p.y+s.y);
        },

        "getUsefulSize": function(){
            var size = this.getSize();
            var borderLeft = this.getStyle("border-left").toInt();
            var borderBottom = this.getStyle("border-bottom").toInt();
            var borderTop = this.getStyle("border-top").toInt();
            var borderRight = this.getStyle("border-right").toInt();

            var paddingLeft = this.getStyle("padding-left").toInt();
            var paddingBottom = this.getStyle("padding-bottom").toInt();
            var paddingTop = this.getStyle("padding-top").toInt();
            var paddingRight = this.getStyle("padding-right").toInt();

            var x = size.x-paddingLeft-paddingRight;
            var y = size.y-paddingTop-paddingBottom;

            return {"x": x, "y": y};
        },
        "clearStyles": function(isChild){
            this.removeProperty("style");
            if (isChild){
                var subNode = this.getFirst();
                while (subNode){
                    subNode.clearStyles(isChild);
                    subNode = subNode.getNext();
                }
            }
        },
        "maskIf": function(styles, click){
            var style = {
                "background-color": "#666666",
                "opacity": 0.4,
                "z-index":100
            };
            if (styles){
                style = Object.merge(style, styles);
            }
            var position = this.getPosition(this.getOffsetParent());
            this.mask({
                "destroyOnHide": true,
                "style": style,
                "useIframeShim": true,
                "iframeShimOptions": {"browsers": true},
                "onShow": function(){
                    this.shim.shim.setStyles({
                        "opacity": 0,
                        "top": ""+position.y+"px",
                        "left": ""+position.x+"px"
                    });
                },
                "onClick": click
            });
        },
        "scrollIn": function(where){
            var wh = (where) ? where.toString().toLowerCase() : "center";

            if (Browser.name=="ie" || Browser.name=="safari"){
                var scrollNode = this.getParentSrcollNode();
                var scrollFx = new Fx.Scroll(scrollNode);
                var scroll = scrollNode.getScroll();
                var size = scrollNode.getSize();
                var thisSize = this.getComputedSize();
                var p = this.getPosition(scrollNode);

                if (wh=="start"){
                    var top = 0;
                    scrollFx.start(scroll.x, p.y-top+scroll.y);
                }else if (wh=="end"){
                    var bottom = size.y-thisSize.totalHeight;
                    scrollFx.start(scroll.x, p.y-bottom+scroll.y);
                }else{
                    var center = size.y/2-thisSize.totalHeight/2;
                    scrollFx.start(scroll.x, p.y-center+scroll.y);
                }
            }else{
                if (wh!=="start" && wh!=="end") wh = "center"
                this.scrollIntoView({"behavior": "smooth", "block": wh, "inline": "nearest"});
            }
        },
        scrollToNode: function(el, where){
            var scrollSize = this.getScrollSize();
            if (!scrollSize.y) return true;
            var wh = (where) ? where.toString().toLowerCase() : "bottom";
            var node = $(el);
            var size = node.getComputedSize();
            var p = node.getPosition(this);
            var thisSize = this.getComputedSize();
            var scroll = this.getScroll();
            if (wh==="top"){
                var n = (p.y-thisSize.computedTop);
                if (n<0) this.scrollTo(scroll.x, scroll.y+n);
                n = (size.totalHeight+p.y-thisSize.computedTop)-thisSize.height;
                if (n>0) this.scrollTo(scroll.x, scroll.y+n);

            }else{
                var n = (size.totalHeight+p.y-thisSize.computedTop)-thisSize.height;
                if (n>0) this.scrollTo(scroll.x, scroll.y+n);
                n = p.y-thisSize.computedTop;
                if (n<0) this.scrollTo(scroll.x, scroll.y+n);
            }
        },
        "getInnerStyles": function(){
            var styles = {};
            style = this.get("style");
            if (style){
                var styleArr = style.split(/\s*\;\s*/g);
                styleArr.each(function(s){
                    if (s){
                        var sarr = s.split(/\s*\:\s*/g);
                        styles[sarr[0]] = (sarr.length>1) ? sarr[1]: ""
                    }
                }.bind(this));
            }
            return styles;
        },
        "getInnerProperties": function(){
            var properties = {};
            if (this.attributes.length){
                for (var i=0; i<this.attributes.length; i++){
                    properties[this.attributes[i].nodeName] = this.attributes[i].nodeValue;
                }
            }
            return properties;
        },
        "getZIndex": function(){
            var n = this;
            var i=0;
            while (n){
                if (n.getStyle("position")==="absolute"){
                    var idx = n.getStyle("z-index");
                    i = (idx && idx.toFloat()>i) ? idx.toFloat()+1 : 0;
                    break;
                }
                n = n.getParent();
            }
            return i;
        },
        "getParentSrcollNode": function(){
            var node = this.getParent();
            while (node && (node.getScrollSize().y<=node.getSize().y || (node.getStyle("overflow")!=="auto" &&  node.getStyle("overflow-y")!=="auto"))){
                node = node.getParent();
            }
            return node || null;
        },
        "getEdgeHeight": function(notMargin){
            var h = 0;
            h += (this.getStyle("border-top-width").toFloat() || 0)+ (this.getStyle("border-bottom-width").toFloat() || 0);
            h += (this.getStyle("padding-top").toFloat() || 0)+ (this.getStyle("padding-bottom").toFloat() || 0);
            if (!notMargin) h += (this.getStyle("margin-top").toFloat() || 0)+ (this.getStyle("margin-bottom").toFloat() || 0);
            return h;
        },
        "getEdgeWidth": function(notMargin){
            var h = 0;
            h += (this.getStyle("border-left-width").toFloat() || 0)+ (this.getStyle("border-right-width").toFloat() || 0);
            h += (this.getStyle("padding-left").toFloat() || 0)+ (this.getStyle("padding-right").toFloat() || 0);
            if (!notMargin) h += (this.getStyle("margin-left").toFloat() || 0)+ (this.getStyle("margin-right").toFloat() || 0);
            return h;
        }
    });
    Object.copy = function(from, to){
        Object.each(from, function(value, key){
            switch (typeOf(value)){
                case "object":
                    if (!to[key]) to[key]={};
                    Object.copy(value, to[key]);
                    break;
                default:
                    to[key] = value;
            }
        });
    };

    if (window.JSON) JSON.format = JSON.encode;

    if (window.Slick) {
        Slick.definePseudo('src', function (value) {
            return Element.get(this, "src").indexOf(value) !== -1;
        });
        Slick.definePseudo('srcarr', function (value) {
            var vList = value.split(",");
            var src = Element.get(this, "src");
            var flag = false;
            for (var i = 0; i < vList.length; i++) {
                if (src.indexOf(vList[i]) !== -1) {
                    flag = true;
                    break;
                }
            }
            return flag;
        });
        Slick.definePseudo('ahref', function (value) {
            var href = Element.get(this, "href");
            if (!href) href = "";
            href = href.toString().toLowerCase();
            return (href.indexOf(value) !== -1);
        });

        Slick.definePseudo('rowspanBefore', function (line) {
            var tr = MWF.getParent(this, "tr");
            var rowspan = this.get("rowspan").toInt() || 1;
            var currentRowIndex = tr.rowIndex.toInt();

            return rowspan > 1 && currentRowIndex < line.toInt() && currentRowIndex + rowspan - 1 >= line;
        });
        Slick.definePseudo('rowspan', function () {
            var rowspan = this.get("rowspan").toInt() || 1;
            return rowspan > 1;
        });

        Slick.definePseudo('colspanBefore', function (col) {
            var tr = MWF.getParent(this, "tr");
            var colspan = this.get("colspan").toInt() || 1;
            var currentColIndex = this.cellIndex.toInt();

            return colspan > 1 && currentColIndex < col.toInt() && currentColIndex + colspan - 1 >= col.toInt();
        });

        Slick.definePseudo('colspan', function () {
            var colspan = this.get("colspan").toInt() || 1;
            return colspan > 1;
        });
    }

    o2.common = o2.common || {};

    o2.common.encodeHtml = function(str){
        str = str.toString();
        str = str.replace(/\&/g, "&amp;");
        str = str.replace(/>/g, "&gt;");
        str = str.replace(/</g, "&lt;");
        return str.replace(/\"/g, "&quot;");
    };

    o2.common.getResponseTextPost = function(path, body, contentType){
        var returnText = "";
        var options = {
            url: path,
            async: false,
            data: body,
            method: "post",
            onSuccess: function(esponseTree, responseElements, responseHTML, responseJavaScript){
                returnText = responseHTML;
            }
        };
        var r = new Request.HTML(options);
        r.send();
        return returnText;
    };
    o2.common.getResponseText = function(path){
        var returnText = "";
        var options = {
            url: path,
            async: false,
            method: "get",
            onSuccess: function(esponseTree, responseElements, responseHTML, responseJavaScript){
                returnText = responseHTML;
            }
        };
        var r = new Request.HTML(options);
        r.send();
        return returnText;
    };
    o2.common.toDate = function(str){
        var tmpArr = str.split(" ");
        if (!tmpArr[1]) tmpArr.push("0:0:0");
        var dateArr = tmpArr[0].split("-");
        var timeArr = tmpArr[1].split(":");
        return new Date(dateArr[0],parseInt(dateArr[1])-1,dateArr[2],timeArr[0],timeArr[1],timeArr[2]);
    };

    o2.common.toDate = function(str){
        var tmpArr = str.split(" ");
        if (!tmpArr[1]) tmpArr.push("0:0:0");
        var dateArr = tmpArr[0].split("-");
        var timeArr = tmpArr[1].split(":");
        return new Date(dateArr[0],parseInt(dateArr[1])-1,dateArr[2],timeArr[0],timeArr[1],timeArr[2]);
    };

    o2.grayscale = function(src, width, height, callback){
        try {
            var canvas = document.createElement('canvas');
            var ctx = canvas.getContext('2d');
            var imgObj = new Image();
            imgObj.src = src;
            canvas.width = width || imgObj.width;
            canvas.height = height || imgObj.height;
            ctx.drawImage(imgObj, 0, 0);

            var imgPixels = ctx.getImageData(0, 0, canvas.width, canvas.height);
            for(var y = 0; y < imgPixels.height; y++){
                for(var x = 0; x < imgPixels.width; x++){
                    var i = (y * 4) * imgPixels.width + x * 4;
                    var avg = (imgPixels.data[i] + imgPixels.data[i + 1] + imgPixels.data[i + 2]) / 3;
                    imgPixels.data[i] = avg;
                    imgPixels.data[i + 1] = avg;
                    imgPixels.data[i + 2] = avg;
                }
            }
            ctx.putImageData(imgPixels, 0, 0, 0, 0, imgPixels.width, imgPixels.height);
            var src1 = canvas.toDataURL();
            //var blob = canvas.toBlob();
            canvas.destroy();
            return {"status": "success", "src": src1};
        }catch(e){
            return {"status": "error", "src": src}
        }
    };
    o2.eventPosition = function(e){
        var x = 0;
        var y = 0;
        if (Browser.name=="firefox"){
            x = parseFloat(e.event.clientX || e.event.x);
            y = parseFloat(e.event.clientY || e.event.y);
        }else{
            x = parseFloat(e.event.x);
            y = parseFloat(e.event.y);
        }
        return {"x": x, "y": y};
    };

    if (window.Browser){
        if (Browser.name==="ie" && Browser.version<9){
            Browser.ieuns = true;
        }else if(Browser.name==="ie" && Browser.version<10){
            Browser.iecomp = true;
        }
        if (Browser.iecomp){
            o2.load("ie_adapter", null, false);
            o2.session.isDebugger = true;
            //layout["debugger"] = true;
        }
        o2.session.isMobile = (["mac", "win", "linux"].indexOf(Browser.Platform.name)===-1);
    }
})();
o2.more = true;
