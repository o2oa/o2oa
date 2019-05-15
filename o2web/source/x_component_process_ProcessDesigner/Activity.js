MWF.xDesktop.requireApp("process.ProcessDesigner", "Property", null, false);
MWF.xApplication.process.ProcessDesigner.Activity = new Class({

    initialize: function(data, process){
        this.data = data;
        this.process = process;
        this.paper = this.process.paper;

        this.routes = [];
        this.fromRoutes = [];

//		this.decisions = [];
//		this.fromDecisions = [];
//		this.fromRoutes = [];

        this.setType();
        this.setStyle();
        this.setSize();
        this.getPoint();

        this.selectedMultiStatus = false;
        this.positionLoaded = false;
    },
    setType: function(){
        this.type = "activity";
        this.data.type = "activity";
    },
    setStyle: function(){
        this.style = this.process.css.activity["default"];
    },
    setSize: function(){
        var size = MWF.getTextSize(this.data.name, this.style.text);

        this.width = size.x+38;
        this.width = this.width<100 ? 100: this.width;
        this.height = 40;
    },
    getPoint: function(){
        this.paperSize = $(this.paper.canvas).getParent().getSize();
        this.paperOriginalSize = {x: this.paperSize.x*0.99, y: this.paperSize.y*0.99};

        var height = this.paperSize.y;
        var width = this.paperSize.x;

        this.top = height/2;
        this.left = width/2;

        this.center = {x: this.left, y: this.top};
        this.point = {x: this.left-(this.width/2), y: this.top-(this.height/2)};
    },
    resetPoint: function(){
        this.center = {x: this.point.x + this.width/2, y: this.point.y + this.height/2};
    },

    checkUUID: function(){
        for (var i=0; i<this.routes.length; i++){
            delete this.process.routes[this.routes[i].data.id];
            delete this.process.routeDatas[this.routes[i].data.id];

            this.routes[i].data.id = this.process.checkUUIDs.pop().id;
            this.routes[i].data.process = this.process.process.id;
            this.setRouteData(this.routes[i].data.id, i);
            //this.data.routeList[i] = this.routes[i].data.id;
            this.routes[i].data.activity = this.routes[i].toActivity.data.id;

            this.process.routes[this.routes[i].data.id] = this.routes[i];
            this.process.routeDatas[this.routes[i].data.id] = this.routes[i].data;
        }
    },

    loaded: function(callback){
        if (this.positionLoaded){
            if (callback) callback();
        }
    },
    load : function(callback){
        if (this.process.isNewProcess){
            this.data.createTime = new Date().format('db');
            this.data.updateTime = new Date().format('db');
        }
        this.draw();

        this.setActivityPosition(function(){
            this.positionLoaded = true;
            this.loaded(callback);
        }.bind(this));

        this.setEvent();

        if (this.process.activityListNode) this.addToActivityList();

        //	this.setPosition(100,100);
    },
    addToActivityList: function(){
        this.listItem = new MWF.APPPD.Activity.List(this);
        this.listItem.load();
    },
    create: function(point){
        this.point = point || {x: 100, y: 100};
        this.center = {"x": this.point.x+(this.width/2), "y": this.point.y+(this.height/2)};
        //this.center = center || {x: 100, y: 100};
        //this.point = {x: this.center.x-(this.width/2), y: this.center.y-(this.height/2)};

        this.draw();

        this.setEvent();

        if (this.process.activityListNode) this.addToActivityList();
    },
    setEvent: function(){
        if (!this.process.options.isView){
            this.set.drag(
                function(dx, dy, x, y, e){
                    if (!e.rightClick){
                        if (this.process.selectedActivitys.length){
                            this.noselected = true;
                            this.process.selectedActivitys.each(function(activity){
                                activity.activityMove(dx, dy, x, y);
                            });
                        }else{
                            this.activityMove(dx, dy, x, y);
                        }
                    }
                }.bind(this),
                function(x, y, e){
                    if (!e.rightClick){
                        if (this.process.selectedActivitys.length){
                            this.process.selectedActivitys.each(function(activity){
                                activity.activityMoveStart();
                            });
                        }else{
                            this.activityMoveStart();
                        }
                    }
                }.bind(this),
                function(e){
                    if (!e.rightClick){
                        if (this.process.selectedActivitys.length){
                            this.process.selectedActivitys.each(function(activity){
                                activity.activityMoveEnd();
                            });

                        }else{
                            this.activityMoveEnd();
                        }
                    }
                }.bind(this)
            );
            //this.set.drag(
            //    function(dx, dy, x, y, e){}.bind(this),
            //    function(x, y, e){}.bind(this),
            //    function(e){}.bind(this)
            //);


            this.set.click(function(e){
                if (this.process.isCreateRoute){
                    this.checkCreateRoute();
                }else{
                    if (this.process.selectedActivitys.length){
                        if (!this.noselected){
                            if (this.process.currentSelected != this){
                                this.selected();
                            }
                        }
                        this.noselected = false;
                    }
                }
                e.stopPropagation();
            }.bind(this));

            this.set.dblclick(function(){
                this.quickCreateRoute();
            }.bind(this));

//		this.set.mouseup(function(e){

//		}.bind(this));
            this.set.mousedown(function(e){
                if (this.process.isCreateRoute){
                    this.checkCreateRoute();
                }else{
                    if (!this.process.selectedActivitys.length){
                        if (this.process.currentSelected != this){
                            this.selected();
                        }
                    }
                }
                //this.checkCreateRoute();
                e.stopPropagation();
            }.bind(this));

//		this.shap.node.mousedown(function(e){
//			e.stop();
//		});
//		this.text.node.mousedown(function(e){
//			e.stop();
//		});
//		this.icon.node.mousedown(function(e){
//			e.stop();
//		});


            this.set.hover(this.mouseHover.bind(this), function(){
                if (this.process.isCreateRoute || this.process.isCopyRoute || this.process.isChangeRouteTo || this.process.isChangeRouteFrom){
                    this.shap.attr(this.style.shap);
                }
            }.bind(this));

            this.set.mouseup(function(){
                this.mouseUp();
            }.bind(this));
        }
    },
    mouseHover: function(){
        if (this.process.isCreateRoute || this.process.isCopyRoute || this.process.isChangeRouteTo || this.process.isChangeRouteFrom){
            this.shap.attr({"stroke-width": "2"});
        }
    },
    mouseUp: function(){
        if (this.process.isChangeRouteTo){
            this.process.currentChangeRoute.setActivity(this, null);
            this.process.currentChangeRoute.isBack = false;
            this.process.currentChangeRoute.positionPoints = this.process.currentChangeRoute.getRoutePoint();
            this.process.currentChangeRoute.reload();
            this.process.isChangeRouteTo = false;
            this.process.currentChangeRoute = null;
        }
        if (this.process.isChangeRouteFrom){
            this.process.currentChangeRoute.setActivity(null, this);
            this.process.currentChangeRoute.isBack = false;
            this.process.currentChangeRoute.positionPoints = this.process.currentChangeRoute.getRoutePoint();
            this.process.currentChangeRoute.reload();
            this.process.isChangeRouteFrom = false;
            this.process.currentChangeRoute = null;
        }
        this.shap.attr(this.style.shap);
    },
    quickCreateRoute: function(){
        this.process.createRoute();
        this.process.routeCreateFromActivity(this);
    },
    checkCreateRoute: function(){
        if (this.process.isCreateRoute){
            var route = this.process.currentCreateRoute;
            if (!route.fromActivity){
                this.process.routeCreateFromActivity(this);
            }else{
                this.process.routeCreateToActivity(this);
            }
        }
        if (this.process.isCopyRoute){
            this.process.routeAddFromActivity(this);
        }
    },
    draw: function(){
        this.set = this.paper.set();
        this.shap = this.createShap();
        //	this.shadow = this.careteShadow();
        this.text = this.createText();
        this.icon = this.createIcon();
        this.set.push(this.shadow, this.shap, this.text, this.icon);
    },
    redraw: function(){
        this.setSize();
        this.resetPoint();
        if (this.shap) this.redrawShap();
        if (this.text) this.redrawText();
        if (this.icon) this.redrawIcon();
        if (this.bBox) this.redrawBox();
        if (this.listItem) this.redrawListItem();
        this.redrawRoute();
    },
    careteShadow: function(){
        var shadow;
        shadow = this.shap.glow({
            "width": 10,
            "fill": true,
            "opacity": 0.2,
            "offsetx": 0,
            "offsety": 0,
            "color": "#aaa"
        });

        shadow.data("bind", this);
        return shadow;
    },
    createShap: function(){
        var shap;
        shap = this.paper.rectPath(this.point.x, this.point.y, this.width, this.height, this.style.shap.radius);
        shap.attr(this.style.shap);
        shap.data("bind", this);

        return shap;
    },
    createText: function(){
        var atts = this.getTextIconPoint();
        text = this.paper.text(atts.tatt.x, atts.tatt.y, this.data.name);
        text.attr(this.style.text);
        if (this.style.text.display=="none"){
            text.hide();
        }
        return text;
    },
    createIcon: function(){
        var atts = this.getTextIconPoint();
        var icon = this.paper.image(this.style.icon.src, atts.iatt.x, atts.iatt.y, 16, 16);
        icon.attr(this.style.icon.attr);
        return icon;
    },

    redrawShap: function(){
        var shapPath = MWFRaphael.getRectPath(this.point.x, this.point.y, this.width, this.height, this.style.shap.radius);
        this.shap.attr("path", shapPath);
    },
    redrawText: function(){
        var atts = this.getTextIconPoint();
        this.text.attr({
            "x": atts.tatt.x,
            "y": atts.tatt.y,
            "text": this.data.name
        });
    },
    redrawIcon: function(){
        var atts = this.getTextIconPoint();
        this.icon.attr({
            "x": atts.iatt.x,
            "y": atts.iatt.y
        });
    },
    redrawBox: function(){
        this.set.exclude(this.bBox);
        this.bBox.remove();
        this.bBox = null;

        var bBox = this.set.getBBox();
        this.bBox = this.paper.rectPath(bBox.x-3, bBox.y-3, bBox.width+6, bBox.height+6, 0);
        this.bBox.attr(this.process.css.activity.box);
        this.set.push(this.bBox);

//		var bBox = this.set.getBBox();
//		var boxPath = MWFRaphael.getRectPath(bBox.x-3, bBox.y-3, bBox.width+6, bBox.height+6, 0);
//		this.bBox.attr("path", boxPath);
    },
    redrawListItem: function(){
        var nameTd = this.listItem.row.tds[1];
        nameTd.set("text", this.data.name);
    },

    loadRoutes: function(){
        var routeIds = this.getRoutedata();
        routeIds.each(function(id){
            if (this.process.routes[id]){
                this.routes.push(this.process.routes[id]);
                this.process.routes[id].setActivity(null, this);
                this.process.routes[id].load();
            }
        }.bind(this));
    },
    getRoutedata: function(){
        if (!this.data.routeList) this.data.routeList = [];
        this.data.routeList = this.data.routeList.clean();
        return this.data.routeList;
    },
    setRouteData: function(id, i){
        var index = (i || i==0) ? i : null;
        if (!this.data.routeList) this.data.routeList = [];

        if (index!=null && index<=this.data.routeList.length){
            var idx = this.data.routeList.indexOf(id);
            if (idx==-1) this.data.routeList[index] = id;
        }else{
            if (this.data.routeList.indexOf(id)==-1) this.data.routeList.push(id);
        }
    },
    removeRouteData: function(id){
        if (!this.data.routeList) this.data.routeList = [];
        this.data.routeList.erase(id);
    },

//	loadDecisions: function(){
//		if (this.data.decisionIdList){
//			if (typeOf(this.data.decisionIdList).toLowerCase() == "array"){
//				this.data.decisionIdList.each(function(id){
//					var item = this.process.decisionDatas[id];
//					var decision = new MWF.PCDecision(item, this.process, this);
//					this.decisions.push(decision);
//					decision.load();
//				}.bind(this));
//			}else{
//				var item = this.process.decisionDatas[this.data.decisionIdList];
//				var decision = new MWF.PCDecision(item, this.process, this);
//				this.decisions.push(decision);
//				decision.load();
//			}
//		};
//	},
    redrawRoute: function(path){
        this.routes.each(function(route){
            route.reload(path,null);
        });
        this.fromRoutes.each(function(route){
            route.reload(null,path);
        });
    },
    activityMove: function(dx, dy, tox, toy){
        //var moveSet = this.set.clone();
        if (!this.set.isFront){
            this.set.toFront();
            this.set.isFront = true;
        }
        //this.paperSize = $(this.paper.canvas).getParent().getSize();

        if ((this.set.ox+dx)<0)	dx = 0 - this.set.ox;
        if ((this.set.ox+dx+this.width)>this.paperSize.x){
            this.paperSize.x = this.set.ox+dx+this.width;
            this.paper.setSize(this.paperSize.x, this.paperSize.y);
            //alert("set width");
            $(this.paper.canvas).getParent().setStyle("width", ""+this.paperSize.x+"px");
        }
        //if ((this.set.ox+dx+this.width)<this.paperOriginalSize.x){
        //    if (this.paperSize.x > this.paperOriginalSize.x){
        //        this.paperSize.x = this.paperOriginalSize.x;
        //        this.paper.setSize(this.paperOriginalSize.x, this.paperSize.y);
        //        //alert("set width");
        //        $(this.paper.canvas).getParent().setStyle("width", ""+this.paperSize.x+"px");
        //    }
        //}

        if ((this.set.oy + dy)<0) dy = 0 - this.set.oy;
        if ((this.set.oy + dy+this.height)>this.paperSize.y){
            this.paperSize.y = this.set.oy + dy+this.height;
            this.paper.setSize(this.paperSize.x, this.paperSize.y);
            //alert("set height");
            $(this.paper.canvas).getParent().setStyle("height", ""+this.paperSize.y+"px");
        }
        //if ((this.set.oy + dy+this.height)<this.paperOriginalSize.y){
        //    if (this.paperSize.y > this.paperOriginalSize.y){
        //        this.paperSize.y = this.paperOriginalSize.y;
        //        this.paper.setSize(this.paperSize.x, this.paperSize.y);
        //        $(this.paper.canvas).getParent().setStyle("height", ""+this.paperSize.y+"px");
        //    }
        //}

//		if (this.process.isGrid){
        var toX = Raphael.snapTo(10, this.set.ox + dx + this.width/2, 10);
        var toY = Raphael.snapTo(10, this.set.oy + dy + this.height/2, 10);

        dx = toX - this.set.ox - this.width/2;
        dy = toY - this.set.oy - this.height/2;
//		}


        //	this.shap.transform("t"+dx+","+dy);
        //	if (this.bBox) this.bBox.transform("t"+dx+","+dy);

        //	var path = this.shap.attr("path");
        path = Raphael.transformPath(this.set.shapPath, "t"+dx+","+dy);
        this.shap.attr("path", path);

        //this.cloneMoveShap.attr("path", path);
        //
        //return ;
        //	this.shap.transform("t0,0");

        if (this.bBox){
            //		var boxPath = this.bBox.attr("path");
            var bBox = this.shap.getBBox();
            boxPath = MWFRaphael.getRectPath(bBox.x-3, bBox.y-3, bBox.width+6, bBox.height+6, 0);

            //boxPath = Raphael.transformPath(this.set.boxPath, "t"+dx+","+dy);
            this.bBox.attr("path", boxPath);
            //		this.bBox.transform("t0,0");
        }

        this.point = {x: this.set.ox + dx, y: this.set.oy + dy};
        this.center = {x: this.set.ox + dx + this.width/2, y: this.set.oy + dy + this.height/2};

        var atts = this.getMoveTextIconPoint();
        this.text.attr(atts.tatt);
        this.icon.attr(atts.iatt);

        this.routes.each(function(route){
            if (this.process.selectedActivitys.indexOf(route.toActivity) != -1){
                route.corners.each(function(corner, idx){
                    //	var p = this.positionPoints[idx];
                    var p = corner.data("point");
                    var cx = p.x.toFloat()+dx.toFloat();
                    var cy = p.y.toFloat()+dy.toFloat();
                    var cornerPath = MWFRaphael.getRectPath(cx-2.5,cy-2.5, 5, 5, 0);
                    corner.attr("path", cornerPath);
                    route.positionPoints[idx] = {"x": cx, "y": cy};

                }.bind(this));
            }
        }.bind(this));



        this.set.movex = dx;
        this.set.movey = dy;

        //	var path = this.shap.attr("path");
        //	path = Raphael.transformPath(path, "t"+this.set.movex+","+this.set.movey);
        //	this.redrawRoute(path);
        this.redrawRoute();

        this.paper.safari();
    },

    activityMoveStart: function(){
        this.set.isFront = false;
        var box = this.shap.getBBox();
        this.set.ox = box.x;
        this.set.oy = box.y;

        this.set.movex = 0;
        this.set.movey = 0;

        this.set.animate({
                "fill-opacity": .5
            },
            500
        );
        this.set.shapPath = this.shap.attr("path");
        if (this.bBox) this.set.boxPath = this.bBox.attr("path")

        //this.cloneMoveShap = this.shap.clone();
        //
        //this.cloneMoveShap.animate({
        //        "fill-opacity": .3
        //    },
        //    200
        //);

        this.routes.each(function(route){
            if (this.process.selectedActivitys.indexOf(route.toActivity) != -1){
                route.corners.each(function(corner, idx){
                    var p = route.positionPoints[idx];
                    corner.data("point", p);
                }.bind(this));
            }
        }.bind(this));
    },
    activityMoveEnd: function(){
        this.center.x = this.center.x.toInt();
        this.center.y = this.center.y.toInt();
        this.data.position = this.center.x+","+this.center.y;


        this.set.animate({"fill-opacity": 1}, 500);
  //      this.reSizePaper(this.center.x, this.center.y);
        //    this.cloneMoveShap.remove();
    },
    setActivityPosition: function(callback){
        var x;
        var y;
        if (this.data.position){
            var tmp = this.data.position.split(/(?:,\s*){1}|(?:;\s*){1}/g);
            x = tmp[0];
            y = tmp[1];
        }else{
            //	this.paperSize.x;
            //	this.paperSize.y;
            var xi = Math.random();
            x = ((this.paperSize.x-300) * xi).toInt();

            var yi = Math.random();
            y = ((this.paperSize.y-300) * yi).toInt();

            this.data.position = x+","+y;
        }
        this.reSizePaper(x, y);

        this.setPosition(x.toFloat(), y.toFloat(), null, callback);
    },
    reSizePaper: function(x, y){
        //this.paperSize = $(this.paper.canvas).getParent().getSize();

//		if (x>this.paperSize.x) this.process.designer.paperNode.setStyle("height", x);
//		if (y>this.paperSize.y) this.process.designer.paperNode.setStyle("width", y);
        y = y.toFloat()+this.height.toFloat();
        x = x.toFloat()+this.width.toFloat();

        if (x>this.paperSize.x){
            this.paper.setSize(x*0.99, this.paperSize.y);
            $(this.paper.canvas).getParent().setStyle("width", ""+x+"px");
            this.paperSize.x = x;
        }
        if (y>this.paperSize.y){
            this.paper.setSize(this.paperSize.x*0.99, y);
            $(this.paper.canvas).getParent().setStyle("height", ""+y+"px");
            this.paperSize.y = y;
        }
    },
    setPosition: function(x, y, time, callback){
        x = x.toFloat();
        y = y.toFloat();

        if (!time) time = 300;

        var mx = x-(this.center.x.toFloat());
        var my = y-(this.center.y.toFloat());

        this.center = {x: x, y: y};
        this.point = {x: x-(this.width/2), y: y-(this.height/2)};

        var path = this.shap.attr("path");
        var path = Raphael.transformPath(path, "t"+mx+","+my);

        var atts = this.getTextIconPoint();

        this.shap.animate({"path": path}, time, "<>", callback);
        this.text.animate(atts.tatt, time, "<>");
        this.icon.animate(atts.iatt, time, "<>");
        //	this.animateLine(time);
    },
    getTextIconPoint: function(){
        var t_att = {x: this.center.x+10, y: this.center.y};
        var i_att = {x: this.center.x-(this.width/2)+8, y: this.center.y-8};
        return {"tatt": t_att, "iatt": i_att};
    },
    getMoveTextIconPoint: function(){
        return this.getTextIconPoint();
    },
    showProperty: function(){
        if (!this.property){
            this.property = new MWF.APPPD.Activity.Property(this, {
                "onPostLoad": function(){
                    this.property.show();
                }.bind(this)
            });
            this.property.load();
        }else{
            this.property.show();
        }
    },
    selectedMulti: function(){
        if (!this.bBox){
            var bBox = this.shap.getBBox();
            this.bBox = this.paper.rectPath(bBox.x-3, bBox.y-3, bBox.width+6, bBox.height+6, 0);
            this.bBox.attr(this.process.css.activity.box);
            this.set.push(this.bBox);
        }
        this.process.selectedActivitys.push(this);
        this.process.selectedActivityDatas.push(this.data);

        if (this.listItem) this.listItem.listUnSelected();
        if (this.property) this.property.hide();

        this.selectedMultiStatus = true;
    },
    unSelectedMulti: function(){
        if (this.bBox){
            this.set.exclude(this.bBox);
            this.bBox.remove();
            this.bBox = null;
        }
        this.selectedMultiStatus = false;
    },
    selected: function(){
        //	if (this.process.jsonParse) {
        //		window.setTimeout(function(){this.process.jsonParse.stopParseJson = true;}.bind(this), 1);
        //	}
        this.activitySelected();
        if (this.listItem) this.listItem.listSelected();
        //	if (this.process.property) this.process.property.hide();
        window.setTimeout(function(){this.showProperty()}.bind(this), 10);
        //    this.showQuickAction();
    },
    unSelected: function(){
        this.activityUnSelected();
        if (this.listItem) this.listItem.listUnSelected();
        if (this.property) this.property.hide();
    },
    showQuickAction: function(){
        this.getQuickActionJson(function(){
            this.quickActionJson.each(function(action, idx){
                var y = this.point.y-20;
                var x = this.point.x + (20*idx);
                var actionNode = this.paper.image(this.process.path+this.process.options.style+"/quickAction/"+action.icon, x, y, 16, 16);
                actionNode.toFront();

                //var actionNode = new Element("div", {
                //    "styles": {
                //        "top":this.center.y,
                //        "left": this.center.x,
                //        "height": "24px",
                //        "width": "24px",
                //        "position": "absolute",
                //        "background-color": "red",
                //        "z-index": "50000"
                //    }
                //}).inject(this.process.designer.paperNode)
            }.bind(this));
        }.bind(this));
    },
    getQuickActionJson: function(callback){
        if (!this.quickActionJson){
            MWF.getJSON(this.process.path+"action.json", {
                "onSuccess": function(obj){
                    var defaultActions = obj["default"];
                    var myActions = obj[this.type];
                    this.quickActionJson = defaultActions.concat(myActions);

                    if (callback) callback();

                }.bind(this),
                "onerror": function(text){
                    this.notice(text, "error");
                }.bind(this),
                "onRequestFailure": function(xhr){
                    this.notice(xhr.responseText, "error");
                }.bind(this)
            });
        }else{
            if (callback) callback();
        }
    },

    activitySelected: function(){
        //if (this.process.currentSelected) this.process.currentSelected.unSelected();
        this.process.unSelectedAll();
        if (!this.bBox){
            var bBox = this.shap.getBBox();
            this.bBox = this.paper.rectPath(bBox.x-3, bBox.y-3, bBox.width+6, bBox.height+6, 0);
            this.bBox.attr(this.process.css.activity.box);
            this.set.push(this.bBox);
        }
        this.process.currentSelected = this;
    },
    activityUnSelected: function(){
        this.process.currentSelected = null;
        if (this.bBox){
            this.set.exclude(this.bBox);
            this.bBox.remove();
            this.bBox = null;
        }
    },
    addRouteData: function(id){
        if (this.data.routeList.indexOf(id)==-1) this.data.routeList.push(id);
    },

    destroy: function(){
        if (this.type=="begin"){
            this.process.begin = null;
            this.process.process.begin = null;
        }

        this.routes.each(function(r){
            r.destroy();
        });
        this.fromRoutes.each(function(r){
            r.destroy();
        });

        if (this.listItem){
            this.listItem.row.tr.destroy();
        }

        var activitys = {};
        for (aid in this.process[this.type+"s"]){
            if (aid!=this.data.id){
                activitys[aid] = this.process[this.type+"s"][aid];
            }else{
                this.process[this.type+"s"][aid] = null;
            }
        }
        this.process[this.type+"s"] = null;
        this.process[this.type+"s"] = activitys;

        this.process.activitys.erase(this);

        if (this.process.process[this.type+"List"]) this.process.process[this.type+"List"].erase(this.data);

        this.set.remove();
    }

});
MWF.APPPD.Activity.SingleRouter = new Class({
    Extends: MWF.APPPD.Activity,
    getRoutedata: function(){
        if (!this.data.route) this.data.route = "";
        return [this.data.route];
    },
    setRouteData: function(id){
        if (!this.data.route) this.data.route = "";
        this.data.route = id;
    },
    removeRouteData: function(id){
        this.data.route = "";
    },
    quickCreateRoute: function(){
        if (!this.routes.length){
            this.process.createRoute();
            this.process.routeCreateFromActivity(this);
        }
    },
    checkCreateRoute: function(){
        if (this.process.isCreateRoute){
            var route = this.process.currentCreateRoute;
            if (!route.fromActivity){
                if (!this.routes.length) this.process.routeCreateFromActivity(this);
            }else{
                this.process.routeCreateToActivity(this);
            }
        }
        if (this.process.isCopyRoute){
            if (!this.routes.length) this.process.routeAddFromActivity(this);
        }
    },
    mouseHover: function(){
        if (this.process.isCreateRoute){
            var route = this.process.currentCreateRoute;
            if (!route.fromActivity){
                if (!this.routes.length) this.shap.attr({"stroke-width": "2"});
            }else{
                this.shap.attr({"stroke-width": "2"});
            }
        }
        if (this.process.isChangeRouteTo){
            this.shap.attr({"stroke-width": "2"});
        }
        if (this.process.isChangeRouteFrom || this.process.isCopyRoute){
            if (!this.routes.length) this.shap.attr({"stroke-width": "2"});
        }
    },
    mouseUp: function(){
        if (this.process.isChangeRouteTo){
            this.process.currentChangeRoute.setActivity(this, null);
            this.process.currentChangeRoute.isBack = false;
            this.process.currentChangeRoute.positionPoints = this.process.currentChangeRoute.getRoutePoint();
            this.process.currentChangeRoute.reload();
            this.process.isChangeRouteTo = false;
            this.process.currentChangeRoute = null;
        }
        if (this.process.isChangeRouteFrom){
            if (!this.routes.length){
                this.process.currentChangeRoute.setActivity(null, this);
                this.process.currentChangeRoute.isBack = false;
                this.process.currentChangeRoute.positionPoints = this.process.currentChangeRoute.getRoutePoint();
                this.process.currentChangeRoute.reload();
                this.process.isChangeRouteFrom = false;
                this.process.currentChangeRoute = null;
            }
        }
        this.shap.attr(this.style.shap);
    },
    addRouteData: function(id){
        if (!this.data.route) this.data.route = id;
    }
});

MWF.APPPD.Activity.Circle = new Class({
    Extends: MWF.APPPD.Activity,

    setSize: function(){
        if (this.style.text.display=="none"){
            this.width = this.style.shap.width;
            this.height = this.style.shap.height;
            this.radius = this.style.shap.radius;
        }else{
            var size = MWF.getTextSize(this.data.name, this.style.text);
            this.radius = size.x/2+10;
            this.radius = this.radius<20 ? 20 : this.radius;
            this.width = this.height = this.radius*2;
        }
    },
    reSizePaper: function(x, y){

        //this.paperSize = $(this.paper.canvas).getParent().getSize();

//		if (x>this.paperSize.x) this.process.designer.paperNode.setStyle("height", x);
//		if (y>this.paperSize.y) this.process.designer.paperNode.setStyle("width", y);
        y = y.toFloat()+this.height.toFloat()/2;
        x = x.toFloat()+this.width.toFloat()/2;

        if (x>this.paperSize.x){
            this.paper.setSize(x*0.99, this.paperSize.y);
            $(this.paper.canvas).getParent().setStyle("width", ""+x+"px");
            this.paperSize.x = x;
        }
        if (y>this.paperSize.y){
            this.paper.setSize(this.paperSize.x*0.99, y);
            $(this.paper.canvas).getParent().setStyle("height", ""+y+"px");
            this.paperSize.y = y;
        }
    },
    resetPoint: function(){
        this.point = {x: this.center.x-this.width/2, y: this.center.y-this.height/2};
    },
    createShap: function(){
        var shap;
        shap = this.paper.circlePath(this.center.x, this.center.y, this.radius);
        shap.attr(this.style.shap);
        shap.data("bind", this);
        return shap;
    },
    redrawText: function(){
        return true;
    },
    redrawShap: function(){
        var shapPath = MWFRaphael.getCirclePath(this.center.x, this.center.y, this.radius);
        this.shap.attr("path", shapPath);
    },
    getOffset: function(){
        return {"tx": 6, "ty": 0, "ix": -8, "iy": -8};
    },
    getTextIconPoint: function(){
        var off = this.getOffset();
        if (this.style.text.display=="none"){
            var t_att = {x: this.center.x+off.tx, y: this.center.y+off.ty};
            var i_att = {x: this.center.x+off.ix, y: this.center.y+off.iy};
            return {"tatt": t_att, "iatt": i_att};
        }else{
            var t_att = {x: this.center.x+6, y: this.center.y};
            var i_att = {x: this.center.x-(this.width/2), y: this.center.y-8};
            return {"tatt": t_att, "iatt": i_att};
        }
    },
    getMoveOffset: function(){
        return {"tx": 6, "ty": 0, "ix": -9, "iy": -9};
    },
    getMoveTextIconPoint: function(){
        var off = this.getMoveOffset();
        if (this.style.text.display=="none"){
            var t_att = {x: this.center.x+off.tx, y: this.center.y+off.ty};
            var i_att = {x: this.center.x+off.ix, y: this.center.y+off.iy};
            return {"tatt": t_att, "iatt": i_att};
        }else{
            var t_att = {x: this.center.x+6, y: this.center.y};
            var i_att = {x: this.center.x-(this.width/2), y: this.center.y-8};
            return {"tatt": t_att, "iatt": i_att};
        }
    }

});

MWF.APPPD.Activity.Circle.SingleRouter = new Class({
    Extends: MWF.APPPD.Activity.Circle,
    getRoutedata: function(){
        if (!this.data.route) this.data.route = "";
        return [this.data.route];
    },
    setRouteData: function(id){
        if (!this.data.route) this.data.route = "";
        this.data.route = id;
    },
    removeRouteData: function(id){
        this.data.route = "";
    },
    quickCreateRoute: function(){
        if (!this.routes.length){
            this.process.createRoute();
            this.process.routeCreateFromActivity(this);
        }
    },
    checkCreateRoute: function(){
        if (this.process.isCreateRoute){
            var route = this.process.currentCreateRoute;
            if (!route.fromActivity){
                if (!this.routes.length) this.process.routeCreateFromActivity(this);
            }else{
                this.process.routeCreateToActivity(this);
            }
        }
        if (this.process.isCopyRoute){
            if (!this.routes.length) this.process.routeAddFromActivity(this);
        }
    },
    mouseHover: function(){
        if (this.process.isCreateRoute){
            var route = this.process.currentCreateRoute;
            if (!route.fromActivity){
                if (!this.routes.length) this.shap.attr({"stroke-width": "2"});
            }else{
                this.shap.attr({"stroke-width": "2"});
            }
        }
        if (this.process.isChangeRouteTo){
            this.shap.attr({"stroke-width": "2"});
        }
        if (this.process.isChangeRouteFrom || this.process.isCopyRoute){
            if (!this.routes.length) this.shap.attr({"stroke-width": "2"});
        }
    },
    mouseUp: function(){
        if (this.process.isChangeRouteTo){
            this.process.currentChangeRoute.setActivity(this, null);
            this.process.currentChangeRoute.isBack = false;
            this.process.currentChangeRoute.positionPoints = this.process.currentChangeRoute.getRoutePoint();
            this.process.currentChangeRoute.reload();
            this.process.isChangeRouteTo = false;
            this.process.currentChangeRoute = null;
        }
        if (this.process.isChangeRouteFrom){
            if (!this.routes.length){
                this.process.currentChangeRoute.setActivity(null, this);
                this.process.currentChangeRoute.isBack = false;
                this.process.currentChangeRoute.positionPoints = this.process.currentChangeRoute.getRoutePoint();
                this.process.currentChangeRoute.reload();
                this.process.isChangeRouteFrom = false;
                this.process.currentChangeRoute = null;
            }
        }
        this.shap.attr(this.style.shap);
    },
    addRouteData: function(id){
        if (!this.data.route) this.data.route = id;
    }
});
MWF.APPPD.Activity.Circle.NoRouter = new Class({
    Extends: MWF.APPPD.Activity.Circle,
    quickCreateRoute: function(){
        return false;
    },
    checkCreateRoute: function(){
        if (this.process.isCreateRoute){
            var route = this.process.currentCreateRoute;
            if (route.fromActivity){
                this.process.routeCreateToActivity(this);
            }
        }
    },
    mouseHover: function(){
        if (this.process.isCreateRoute){
            var route = this.process.currentCreateRoute;
            if (route.fromActivity){
                this.shap.attr({"stroke-width": "2"});
            }
        }
        if (this.process.isChangeRouteTo){
            this.shap.attr({"stroke-width": "2"});
        }
    },
    mouseUp: function(){
        if (this.process.isChangeRouteTo){
            this.process.currentChangeRoute.setActivity(this, null);
            this.process.currentChangeRoute.isBack = false;
            this.process.currentChangeRoute.positionPoints = this.process.currentChangeRoute.getRoutePoint();
            this.process.currentChangeRoute.reload();
            this.process.isChangeRouteTo = false;
            this.process.currentChangeRoute = null;
        }
        this.shap.attr(this.style.shap);
    },
    addRouteData: function(id){}
});
MWF.APPPD.Activity.Diamond = new Class({
    Extends: MWF.APPPD.Activity,
    setSize: function(){
        var size = MWF.getTextSize(this.data.name, this.style.text);

        this.width = size.x + 80;
        this.width = this.width<120 ? 120: this.width;
        this.height = this.width*0.4;
    },
    createShap: function(){
        var shap;

        shap = this.paper.diamond(this.point.x, this.point.y, this.width, this.height, this.style.shap.radiusX, this.style.shap.radiusY);
        shap.attr(this.style.shap);
        shap.data("bind", this);
        return shap;
    },
    redrawShap: function(){
        var shapPath = MWFRaphael.getDiamondPath(this.point.x, this.point.y, this.width, this.height, this.style.shap.radiusX, this.style.shap.radiusY);
        this.shap.attr("path", shapPath);
    },
    getTextIconPoint: function(){
        var t_att = {x: this.center.x+6, y: this.center.y+1};
        var i_att = {x: this.center.x-(this.width/2)+28, y: this.center.y-9};
        return {"tatt": t_att, "iatt": i_att};
    }
});

(function (){
    var Activity = function(extendClass, name){
        return new Class({
            Extends: extendClass,
            setType: function(){
                this.type = name;
                this.data.type = name;
            },
            setStyle: function(){
                this.style = this.process.css.activity[name];
            }
        });
    };

    MWF.APPPD.Activity.Begin = new Activity(MWF.APPPD.Activity.Circle.SingleRouter, "begin");
    MWF.APPPD.Activity.End = new Activity(MWF.APPPD.Activity.Circle.NoRouter, "end");
    MWF.APPPD.Activity.Cancel = new Activity(MWF.APPPD.Activity.Circle.NoRouter, "cancel");

    MWF.APPPD.Activity.Manual = new Activity(MWF.APPPD.Activity, "manual");

    MWF.APPPD.Activity.Condition = new Activity(MWF.APPPD.Activity.Diamond, "condition");
    MWF.APPPD.Activity.Choice = new Activity(MWF.APPPD.Activity.Diamond, "choice");

    MWF.APPPD.Activity.Split = new Activity(MWF.APPPD.Activity.Circle.SingleRouter, "split");
    MWF.APPPD.Activity.Parallel = new Activity(MWF.APPPD.Activity.Circle, "parallel");
    MWF.APPPD.Activity.Merge = new Activity(MWF.APPPD.Activity.Circle.SingleRouter, "merge");
    MWF.APPPD.Activity.Embed = new Activity(MWF.APPPD.Activity.SingleRouter, "embed");

    MWF.APPPD.Activity.Delay = new Activity(MWF.APPPD.Activity.Circle.SingleRouter, "delay");
    MWF.APPPD.Activity.Invoke = new Activity(MWF.APPPD.Activity.SingleRouter, "invoke");
    MWF.APPPD.Activity.Service = new Activity(MWF.APPPD.Activity.SingleRouter, "service");
    MWF.APPPD.Activity.Agent = new Activity(MWF.APPPD.Activity.SingleRouter, "agent");
    MWF.APPPD.Activity.Message = new Activity(MWF.APPPD.Activity.SingleRouter, "message");
})();

MWF.xApplication.process.ProcessDesigner.Activity.List = new Class({
    initialize: function(activity){
        this.activity = activity;
        this.process = activity.process;
        this.paper = this.activity.process.paper;
    },
    load: function(){
        //this.activity.process.activityListNode
        //	this.listActivityNode = new Element("div",{
        //		"styles": this.process.css.listActivityNode
        //	});
        //	this.activityTable = new HtmlTable({
        //	    "properties": {
        //	        "width": "100%"
        //	    }
        //	}).inject(this.listActivityNode);

        var actionIcon = (this.activity.type=="begin") ? " " : "<img src=\""+"/x_component_process_ProcessDesigner/$Process/default/icon/copy.png"+"\" />";
        this.row = this.process.activityTable.push([
                {
                    "content": " ",
                    "properties": {
                        "styles": this.activity.style.listIcon
                    }
                },
                {
                    "content": this.activity.data.name,
                    "properties": {
                        "styles": this.process.css.list.listText
                    }
                },
                {
                    "content": actionIcon,
                    "properties": {
                        "styles": this.process.css.list.listIcon,
                        "events": {
                            "click": this.copyActivity.bind(this)
                        }
                    }
                },
                {
                    "content": "<img src=\""+"/x_component_process_ProcessDesigner/$Process/default/icon/delete.png"+"\" />",
                    "properties": {
                        "styles": this.process.css.list.listIcon,
                        "events": {
                            "click": this.deleteActivity.bind(this)
                        }
                    }
                }

            ]
        );
        this.row.tr.addEvent("click", function(){
            this.activity.selected();
        }.bind(this));
        //this.listActivityNode.inject(this.activity.process.activityListNode);
        //var activityTable = new Element("table").inject(this.listActivityNode);
    },
    copyActivity: function(){
        this.process.copyActivity(this.activity);
    },
    deleteActivity: function(e){
        this.process.deleteActivity(e, this.activity);
    },
    listSelected: function(){
        if (this.process.currentListSelected) this.process.currentListSelected.listUnSelected();
        this.row.tr.setStyles(this.process.css.list.listRowSelected);
        this.process.currentListSelected = this;

        //alert(this.process.currentSelected!=this.activity)
        //if (this.process.currentSelected!=this.activity) this.activity.selected();
    },
    listUnSelected: function(){
        this.process.currentListSelected = null;
        this.row.tr.setStyles(this.process.css.list.listRow);
        //if (this.process.currentSelected==this.activity) this.activity.unSelected();
    }
});

MWF.xApplication.process.ProcessDesigner.Activity.Property = new Class({
    Implements: [Options, Events],
    Extends: MWF.APPPD.Property,

    initialize: function(activity, options){
        this.setOptions(options);

        this.activity = activity;
        this.process = activity.process;
        this.paper = this.activity.process.paper;
        this.data = activity.data;
        this.htmlPath = "/x_component_process_ProcessDesigner/$Process/"+this.activity.type+".html";
    },
    setValue: function(name, value){
        this.data[name] = value;
        if (name=="name"){
            if (!value) this.data[name] = MWF.APPPD.LP.unnamed;
            this.activity.redraw();
        }
    }
});
