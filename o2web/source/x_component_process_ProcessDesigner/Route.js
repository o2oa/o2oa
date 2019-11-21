MWF.xDesktop.requireApp("process.ProcessDesigner", "Property", null, false);
MWF.xApplication.process.ProcessDesigner.Route = new Class({

    initialize: function (data, process) {
        this.data = data;
        this.process = process;
        this.paper = this.process.paper;

        this.loaded = false;

        this.l1 = 8;
        this.l2 = 12;
        this.aj = 25;

        this.corners = [];

        this.toSelf = false;

        this.isLineEvent = false;
        this.isTextEvent = false;
        this.isSetEvent = false;
        this.isArrowEvent = false;
        this.isPointEvent = false;

        this.checked = false;
        this.isBack = false;
        this.isBrokenLine = false;

        this.tmpbeginPoint = null;
        this.tmpEndPoint = null;

        this.toActivity = this.getNextActivity();
        this.positionPoints = this.getRoutePoint();

        if (!this.process.options.isView) {
            this.listItem = new MWF.APPPD.Route.List(this);
            this.listItem.load();
        }

    },
    getRoutePoint: function () {
        var points = [];
        if (this.data.track) {
            var pointArr = this.data.track.split(/\s+/g);
            pointArr.each(function (p) {
                var pArr = p.split(/(?:,\s*){1}|(?:;\s*){1}/g);
                points.push({"x": pArr[0], "y": pArr[1]});
            }.bind(this));
        }
        return points;
    },
    reload: function (fromPath, toPath) {
        if (this.toActivity && this.fromActivity) {
            if (this.toActivity.data.id == this.fromActivity.data.id) {
                this.toSelf = true;
            } else {
                this.toSelf = false;
            }
        }
        //	this.positionPoints = this.getRoutePoint();
        this.getPoint(fromPath, toPath);
        this.redraw();
    },
    load: function (fromActivity) {
        if (this.process.isNewProcess) {
            this.data.createTime = new Date().format('db');
            this.data.updateTime = new Date().format('db');
        }
        if (fromActivity) this.fromActivity = fromActivity;
        if (this.toActivity && this.fromActivity) {
            if (this.toActivity.data.id == this.fromActivity.data.id) {
                this.toSelf = true;
            } else {
                this.toSelf = false;
            }
        }

        this.getPoint();
        this.draw();

        this.loaded = true;
    },
    setActivity: function (toActivity, fromActivity) {
        if (toActivity) {
            if (this.toActivity) {
                this.toActivity.fromRoutes.erase(this);
            }
            this.toActivity = toActivity;
            this.data.activity = this.toActivity.data.id;
            this.data.activityType = this.toActivity.data.type;
            if (this.toActivity.fromRoutes.indexOf(this) == -1) this.toActivity.fromRoutes.push(this);
        }
        if (fromActivity) {
            if (this.fromActivity) {
                this.fromActivity.routes.erase(this);
                this.fromActivity.removeRouteData(this.data.id);
                //	this.fromActivity.data.routeList.erase(this.data.id);
            }
            this.fromActivity = fromActivity;
            if (this.data.id) this.fromActivity.addRouteData(this.data.id);
            if (this.fromActivity.routes.indexOf(this) == -1) this.fromActivity.routes.push(this);
        }
    },
    getNextActivity: function () {
        var id = this.data.activity;
        var type = this.data.activityType;

        if (type) {
            if (type.toLowerCase() == "begin") {
                if (this.process.begin.data.id == id) {
                    this.process.begin.fromRoutes.push(this);
                    return this.process.begin;
                }
            } else {
                var activity = this.process[type + "s"][id];
                if (activity) {
                    if (activity.fromRoutes.indexOf(this) == -1) activity.fromRoutes.push(this);
                    return activity;
                }
            }
        }
        return null;
    },
    redraw: function () {
        if (this.beginPoint && this.endPoint) {
            if (this.set) {
                //this.set.show();
                //this.set.clear();
            } else {
                this.set = this.paper.set();
            }

            if (this.point) {
                this.point.show();
                this.point.attr("path", MWFRaphael.getCirclePath(this.beginPoint.x, this.beginPoint.y, 3));
            } else {
                this.point = this.paper.circlePath(this.beginPoint.x, this.beginPoint.y, 3);
                this.point.attr(this.process.css.route.decision.normal);
                this.set.push(this.point);
            }

            if (this.line) {
                this.line.show();
                this.line.attr("path", this.getLinePath());
            } else {
                this.line = this.paper.path(this.getLinePath());
                this.line.toBack();
                this.line.attr(this.process.css.route.line.normal);
                this.set.push(this.line);
            }
            this.line.toBack();

            if (this.text) {
                this.text.show();
                var p = this.getTextPoint();
                this.text.attr({
                    "text": this.data.name || MWF.APPPD.LP.unnamed,
                    "x": p.x,
                    "y": p.y
                });
            } else {
                this.text = this.createText();
                this.set.push(this.text);
            }

            if (this.arrow) {
                this.arrow.show();
                var beginPoint = (this.positionPoints.length) ? this.positionPoints[this.positionPoints.length - 1] : this.beginPoint;
                this.arrow.attr("path", MWFRaphael.getArrowPath(beginPoint, this.endPoint, this.l1, this.l2, this.aj));
            } else {
                this.arrow = this.createArrow();
                this.arrow.attr(this.process.css.route.arrow.normal);
                this.set.push(this.arrow);
            }

            if (this.set) {
                this.set.attr({"transform": ""});
                if (this.checked) {
                    if (this.process.currentSelected == this) {
                        this.point.attr(this.process.css.route.decision.selected);
                        this.line.attr(this.process.css.route.line.selected);
                        this.arrow.attr(this.process.css.route.arrow.selected);
                        this.text.attr(this.process.css.route.linetext.selected);
                    } else {
                        this.point.attr(this.process.css.route.decision.normal);
                        this.line.attr(this.process.css.route.line.normal);
                        this.arrow.attr(this.process.css.route.arrow.normal);
                        this.text.attr(this.process.css.route.linetext.normal);
                    }
                } else {
                    this.point.attr(this.process.css.route.decision["no-checked"]);
                    this.line.attr(this.process.css.route.line["no-checked"]);
                    this.arrow.attr(this.process.css.route.arrow["no-checked"]);
                    this.text.attr(this.process.css.route.linetext["no-checked"]);
                }
                if (this.isBack) {
                    this.set.toBack();
                } else {
                    this.set.toFront();
                }
            }
        } else {
            if (this.point) this.point.hide();
            if (this.line) this.line.hide();
            if (this.arrow) this.arrow.hide();
            if (this.set) this.set.hide();
        }
        this.setListItemData();
        this.setEvent();
    },
    draw: function () {
        if (this.beginPoint && this.endPoint) {
            this.point = this.paper.circlePath(this.beginPoint.x, this.beginPoint.y, 3);
            this.point.attr(this.process.css.route.decision.normal);

            this.line = this.paper.path(this.getLinePath());
            this.line.toBack();
            this.line.attr(this.process.css.route.line.normal);

            this.text = this.createText();

            this.arrow = this.createArrow();
            this.arrow.attr(this.process.css.route.arrow.normal);

            this.set = this.paper.set();
            this.set.push(this.point, this.line, this.arrow, this.text);

            this.point.data("bind", this);
            this.line.data("bind", this);
            this.arrow.data("bind", this);
            this.text.data("bind", this);

            if (!this.toSelf) {
                this.positionPoints.each(function (p, idx) {
                    this.createCorner(p, idx);
                }.bind(this));
            }

            if (this.checked) {
                if (this.process.currentSelected == this) {
                    this.point.attr(this.process.css.route.decision.selected);
                    this.line.attr(this.process.css.route.line.selected);
                    this.arrow.attr(this.process.css.route.arrow.selected);
                    this.text.attr(this.process.css.route.linetext.selected);
                } else {
                    this.point.attr(this.process.css.route.decision.normal);
                    this.line.attr(this.process.css.route.line.normal);
                    this.arrow.attr(this.process.css.route.arrow.normal);
                    this.text.attr(this.process.css.route.linetext.normal);
                }
            } else {
                this.point.attr(this.process.css.route.decision["no-checked"]);
                this.line.attr(this.process.css.route.line["no-checked"]);
                this.arrow.attr(this.process.css.route.arrow["no-checked"]);
                this.text.attr(this.process.css.route.linetext["no-checked"]);
            }
            if (this.isBack) {
                this.set.toBack();
            } else {
                this.set.toFront();
            }
        } else {
            if (this.point) this.point.hide();
            if (this.line) this.line.hide();
            if (this.arrow) this.arrow.hide();
            if (this.text) this.text.hide();
            if (this.set) this.set.hide();
        }
        this.setEvent();
    },
    setEvent: function () {
        if (this.set) {
            if (!this.isSetEvent) {
                this.set.mousedown(function (e) {
                    this.selected();
                    //this.process.unSelectedEvent = false;
                    e.stopPropagation();
                }.bind(this));

                this.set.click(function (e) {
                    e.stopPropagation();
                }.bind(this));

                this.isSetEvent = true;
            }
            ;
        }

        if (this.line) {
            if (!this.isLineEvent) {
                this.line.mousedown(function (e) {
                    if (!this.toSelf) {
                        var offsetP = MWF.getOffset(e);
                        this.checkBrokenLineBegin(offsetP.offsetX, offsetP.offsetY);
                        //this.checkBrokenLineBegin(e.clientX, e.clientY);
                    }
                }.bind(this));
                this.isLineEvent = true;
            }
            ;
        }
        ;
        if (this.arrow) {
            if (!this.isArrowEvent) {
                this.arrow.drag(
                    function (dx, dy, x, y, e) {
                        this.arrowMove(dx, dy, x, y.e);
                    }.bind(this),
                    function () {
                        this.arrowMoveStart();
                    }.bind(this),
                    function () {
                        this.arrowMoveEnd();
                    }.bind(this)
                );
                this.arrow.hover(function () {
                    var beginPoint = this.beginPoint;
                    if (this.positionPoints.length) {
                        beginPoint = this.positionPoints[this.positionPoints.length - 1];
                    }
                    beginPoint.x = beginPoint.x.toFloat();
                    beginPoint.y = beginPoint.y.toFloat();

                    var path = MWFRaphael.getArrowPath(beginPoint, this.endPoint, 20, 30, this.aj);
                    this.arrow.attr("path", path);
                }.bind(this), function () {
                    var beginPoint = this.beginPoint;
                    if (this.positionPoints.length) {
                        beginPoint = this.positionPoints[this.positionPoints.length - 1];
                    }
                    beginPoint.x = beginPoint.x.toFloat();
                    beginPoint.y = beginPoint.y.toFloat();

                    var path = MWFRaphael.getArrowPath(beginPoint, this.endPoint, this.l1, this.l2, this.aj);
                    this.arrow.attr("path", path);
                }.bind(this));
            }
            this.isArrowEvent = true;
        }
        ;
        if (this.point) {
            if (!this.isPointEvent) {
                this.point.drag(
                    function (dx, dy, x, y, e) {
                        this.pointMove(dx, dy, x, y.e);
                    }.bind(this),
                    function () {
                        this.pointMoveStart();
                    }.bind(this),
                    function () {
                        this.pointMoveEnd();
                    }.bind(this)
                );
                this.point.hover(function () {
                    var path = MWFRaphael.getCirclePath(this.beginPoint.x, this.beginPoint.y, 8);
                    this.point.attr("path", path);
                }.bind(this), function () {
                    var path = MWFRaphael.getCirclePath(this.beginPoint.x, this.beginPoint.y, 3);
                    this.point.attr("path", path);
                }.bind(this));
            }
            this.isPointEvent = true;
        }
        ;

        if (this.text) {
            if (!this.isTextEvent) {
                this.text.drag(
                    function (dx, dy, x, y) {
                        this.textMove(dx, dy, x, y);
                    }.bind(this),
                    function () {
                        this.textMoveStart();
                    }.bind(this),
                    function () {
                        this.textMoveEnd();
                    }.bind(this)
                );
                this.isTextEvent = true;
            }
        }
        ;
    },

    arrowMoveStart: function () {
        this.arrow.data("originalPoint", {
            "x": this.endPoint.x,
            "y": this.endPoint.y
        });
        //	this.arrow.toBack();
        //	if (this.line) this.line.toBack();

        this.process.isChangeRouteTo = true;
        this.process.currentChangeRoute = this;
    },
    arrowMove: function (dx, dy, x, y) {
        this.isBack = true;
        var p = this.arrow.data("originalPoint");
        this.endPoint = {
            "x": p.x + dx - 6,
            "y": p.y + dy - 6
        };
        this.redraw();
    },
    arrowMoveEnd: function () {
        this.isBack = false;
        if (this.process.isChangeRouteTo) {
            this.endPoint = this.arrow.data("originalPoint");

            this.redraw();
            this.process.isChangeRouteTo = false;
            this.process.currentChangeRoute = null;
        }
        //this.arrow.toFront();
        if (this.line) this.line.toFront();
    },
    pointMoveStart: function () {
        this.point.data("originalPoint", {
            "x": this.beginPoint.x,
            "y": this.beginPoint.y
        });
        //this.point.toBack();
        //if (this.line) this.line.toBack();

        this.process.isChangeRouteFrom = true;
        this.process.currentChangeRoute = this;
    },
    pointMove: function (dx, dy, x, y) {
        if (dx > 10 || dy > 10) this.isBack = true;
        var p = this.point.data("originalPoint");
        this.beginPoint = {
            "x": p.x + dx - 4,
            "y": p.y + dy - 4
        };
        this.redraw();
    },
    pointMoveEnd: function () {
        this.isBack = false;
        if (this.process.isChangeRouteFrom) {
            this.beginPoint = this.point.data("originalPoint");
            this.redraw();
            this.process.isChangeRouteFrom = false;
            this.process.currentChangeRoute = null;
        }
        //this.point.toFront();
        if (this.line) this.line.toFront();
    },

    cornerBrokenLineBegin: function (e, corner) {
        if (!this.process.isCreateRoute) {
            //	var x = e.layerX;
            //	var y = e.layerY;
            var offsetP = MWF.getOffset(e);
            var x = offsetP.offsetX;
            var y = offsetP.offsetY;

            var idx = this.corners.indexOf(corner);
            if (idx != -1) {
                this.process.brokenLineBeginMousemoveBind = function (e) {
                    this.doBeginBrokenLine(e, x, y, idx - 1, corner, true);
                }.bind(this);

                this.process.brokenLineMouseupBind = function () {
                    this.endBrokenLine();
                }.bind(this);

                this.paper.canvas.addEvent("mousemove", this.process.brokenLineBeginMousemoveBind);
                this.paper.canvas.addEvent("mouseup", this.process.brokenLineMouseupBind);
            }
        }
    },

    checkBrokenLineBegin: function (x, y) {
        if (!this.process.isCreateRoute) {

            var movePointIndex = this.getNearIndex(this.positionPoints, {"x": x, "y": y});
            if (movePointIndex === null) {
                var idx = this.getCornerPointIndex(x, y);

                this.process.brokenLineBeginMousemoveBind = function (e) {
                    this.doBeginBrokenLine(e, x, y, idx);
                }.bind(this);

                this.process.brokenLineMouseupBind = function () {
                    this.endBrokenLine();
                }.bind(this);

                this.paper.canvas.addEvent("mousemove", this.process.brokenLineBeginMousemoveBind);
                this.paper.canvas.addEvent("mouseup", this.process.brokenLineMouseupBind);
            } else {

            }
        }
        ;
    },
    getCornerPointIndex: function (x, y) {
        cornerPointIndex = -1;
        var tmpLong = 500000;
        if (this.positionPoints.length) {
            var tmpArr = this.positionPoints.concat(this.endPoint);
            tmpArr.unshift(this.beginPoint);

            for (var i = 0; i < tmpArr.length - 1; i++) {
                var p1 = tmpArr[i];
                var p2 = tmpArr[i + 1];

                var n = MWFRaphael.getMinDistance({"x": x, "y": y}, p1, p2).h;
                if (n < tmpLong) {
                    tmpLong = n;
                    cornerPointIndex = i - 1;
                }
            }
            ;
        }
        ;
        return cornerPointIndex;
    },
    getNearIndex: function (pList, p) {
        for (var i = 0; i < pList.length; i++) {
            var tmpp = pList[i];
            var lineP = {"x": tmpp.x.toFloat(), "y": tmpp.y.toFloat()};
            var tmp = MWFRaphael.getPointDistance(p, lineP);
            if (tmp <= 8) {
                return i;
            }
        }
        return null;
    },
    endBrokenLine: function () {
        this.process.isBrokenLine = false;
        if (this.removeCorner) {
            this.corners.splice(this.removeCorner.idx + 1, 1);
            this.removeCorner.corner.remove();

            this.positionPoints.splice(this.removeCorner.idx + 1, 1);
            this.removeCorner = null;
        }
        this.isBrokenLine = false;
        this.data.track = this.positionPointsToString();

        this.paper.canvas.removeEvent("mouseup", this.process.brokenLineMouseupBind);
        this.paper.canvas.removeEvent("mousemove", this.process.brokenLineBeginMousemoveBind);
        this.paper.canvas.removeEvent("mousemove", this.process.brokenLineMousemoveBind);

    },
    positionPointsToString: function () {
        var arr = [];
        this.positionPoints.each(function (p) {
            arr.push(p.x + "," + p.y);
        });
        return arr.join(" ");
    },
    doBeginBrokenLine: function (e, x, y, idx, corner, noCreateP) {
        var p1 = this.positionPoints[idx] || this.beginPoint;
        var p2 = this.positionPoints[idx + 2] || this.endPoint;

        var offsetP = MWF.getOffset(e.event);

        var n = MWFRaphael.getMinDistance({
            "x": offsetP.offsetX,
            "y": offsetP.offsetY
        }, p1, p2).h;

        if (n > 6) {
            this.process.isBrokenLine = true;
            this.isBrokenLine = true;

            if (!corner || this.removeCorner) {
                if (!noCreateP) this.positionPoints.splice(idx + 1, 0, {
                    "x": offsetP.offsetX,
                    "y": offsetP.offsetY
                });
                if (this.removeCorner) {
                    corner = this.removeCorner.corner;
                    this.removeCorner = null;
                } else {
                    corner = this.createCorner(this.positionPoints[idx + 1], idx + 1);
                }
            }
            corner.attr(this.process.css.route.corner["default"]);

            this.paper.canvas.removeEvent("mousemove", this.process.brokenLineBeginMousemoveBind);
            this.reload();

            this.process.brokenLineMousemoveBind = function (e) {
                this.doBrokenLine(e, idx, corner);
            }.bind(this);

            this.paper.canvas.addEvent("mousemove", this.process.brokenLineMousemoveBind);
        }
        ;
    },
    doBrokenLine: function (e, idx, corner) {
        var offsetP = MWF.getOffset(e.event);

        var toX = offsetP.offsetX;
        var toY = offsetP.offsetY;

        //	if (this.process.isGrid){
        toX = Raphael.snapTo(10, toX, 10);
        toY = Raphael.snapTo(10, toY, 10);
        //	}

        var p1 = this.positionPoints[idx] || this.beginPoint;
        var p2 = this.positionPoints[idx + 2] || this.endPoint;
        var offset = MWFRaphael.getMinDistance({"x": toX, "y": toY}, p1, p2);
        var off = offset.h;

        if (off < 6) {
            this.removeCorner = {"corner": corner, "idx": idx};

            corner.attr("path", MWFRaphael.getRectPath((offset.p.x.toFloat()) - 2.5, (offset.p.y.toFloat()) - 2.5, 5, 5, 0));

            if (this.positionPoints[idx + 1]) this.positionPoints[idx + 1].x = offset.p.x;
            if (this.positionPoints[idx + 1]) this.positionPoints[idx + 1].y = offset.p.y;

            this.reload();

            this.process.brokenLineBeginMousemoveBind = function (e) {
                this.doBeginBrokenLine(e, toX, toY, idx, null, true);
            }.bind(this);

            this.paper.canvas.removeEvent("mousemove", this.process.brokenLineMousemoveBind);
            this.paper.canvas.addEvent("mousemove", this.process.brokenLineBeginMousemoveBind);
        } else {
            if (Math.abs(p1.x - toX) < 5) toX = p1.x;
            if (Math.abs(p1.y - toY) < 5) toY = p1.y;
            if (Math.abs(p2.x - toX) < 5) toX = p2.x;
            if (Math.abs(p2.y - toY) < 5) toY = p2.y;

            var path = MWFRaphael.getRectPath(toX - 2.5, toY - 2.5, 5, 5, 0);
            corner.attr("path", path);

            if (this.positionPoints[idx + 1]) this.positionPoints[idx + 1].x = toX;
            if (this.positionPoints[idx + 1]) this.positionPoints[idx + 1].y = toY;
            this.reload();
        }
    },

    selected: function () {
        this.process.unSelectedAll();
        if (this.line) this.line.attr(this.process.css.route.line.selected);
        if (this.point) this.point.attr(this.process.css.route.decision.selected);
        if (this.arrow) this.arrow.attr(this.process.css.route.arrow.selected);
        if (this.text) this.text.attr(this.process.css.route.linetext.selected);
        this.corners.each(function (corner) {
            corner.show();
        });

        this.process.currentSelected = this;

        if (this.listItem) this.listItem.selected();

        this.set.toFront();

        this.showProperty();
    },
    unSelected: function () {
        if (this.checked) {
            this.point.attr(this.process.css.route.decision.normal);
            this.line.attr(this.process.css.route.line.normal);
            this.arrow.attr(this.process.css.route.arrow.normal);
            this.text.attr(this.process.css.route.linetext.normal);
        } else {
            this.point.attr(this.process.css.route.decision["no-checked"]);
            this.line.attr(this.process.css.route.line["no-checked"]);
            this.arrow.attr(this.process.css.route.arrow["no-checked"]);
            this.text.attr(this.process.css.route.linetext["no-checked"]);
        }
        this.corners.each(function (corner) {
            corner.hide();
        });
        this.process.currentSelected = null;

        if (this.listItem) this.listItem.unSelected();

        if (this.property) this.property.hide();
    },

    textMove: function (dx, dy, x, y) {
        var x = (this.text.moveX.toFloat()) + parseFloat(dx);
        var y = (this.text.moveY.toFloat()) + parseFloat(dy);

        var dp = this.getDefaultTextPoint();
        var d = MWFRaphael.getPointDistance(dp, {"x": x, "y": y});

        if (d < 5) {
            this.text.attr({
                "x": dp.x,
                "y": dp.y
            });
        } else {
            this.text.attr({
                "x": x,
                "y": y
            });
        }
    },
    textMoveStart: function () {
        this.text.moveX = this.text.attr("x");
        this.text.moveY = this.text.attr("y");
    },
    textMoveEnd: function () {
        var x = this.text.attr("x");
        var y = this.text.attr("y");
        var dp = this.getDefaultTextPoint();
        var d = MWFRaphael.getPointDistance(dp, {"x": x, "y": y});
        if (d < 5) {
            this.data.position = "";
        } else {
            x = x.toInt();
            y = y.toInt();
            this.data.position = x + "," + y;
        }
    },

    getLinePath: function () {
        var path = "";
        if (this.beginPoint && this.endPoint) {
            path = "M" + this.beginPoint.x + "," + this.beginPoint.y;
            this.positionPoints.each(function (p, idx) {
                var p0;
                var p2;
                if (idx == 0) {
                    p0 = this.beginPoint;
                } else {
                    p0 = this.positionPoints[idx - 1];
                }
                if (this.positionPoints[idx + 1]) {
                    p2 = this.positionPoints[idx + 1];
                } else {
                    p2 = this.endPoint;
                }
                p.x = p.x.toFloat();
                p.y = p.y.toFloat();
                var minus1 = MWFRaphael.getMinus(Math.abs(p.x - p0.x), Math.abs(p.y - p0.y), 12);
                var minus2 = MWFRaphael.getMinus(Math.abs(p.x - p2.x), Math.abs(p.y - p2.y), 12);
                var qp0 = null;
                var qp2 = null;
                if (p.x >= p0.x && p.y >= p0.y) {
                    qp0 = {"x": p.x - minus1.x, "y": p.y - minus1.y};
                } else if (p.x <= p0.x && p.y <= p0.y) {
                    qp0 = {"x": p.x + minus1.x, "y": p.y + minus1.y};
                } else if (p.x >= p0.x && p.y <= p0.y) {
                    qp0 = {"x": p.x - minus1.x, "y": p.y + minus1.y};
                } else if (p.x <= p0.x && p.y >= p0.y) {
                    qp0 = {"x": p.x + minus1.x, "y": p.y - minus1.y};
                }
                if (p.x >= p2.x && p.y >= p2.y) {
                    qp2 = {"x": p.x - minus2.x, "y": p.y - minus2.y};
                } else if (p.x <= p2.x && p.y <= p2.y) {
                    qp2 = {"x": p.x + minus2.x, "y": p.y + minus2.y};
                } else if (p.x >= p2.x && p.y <= p2.y) {
                    qp2 = {"x": p.x - minus2.x, "y": p.y + minus2.y};
                } else if (p.x <= p2.x && p.y >= p2.y) {
                    qp2 = {"x": p.x + minus2.x, "y": p.y - minus2.y};
                }

                path += "L" + qp0.x + "," + qp0.y;
                path += "Q" + p.x + ", " + p.y + ", " + qp2.x + "," + qp2.y;

            }.bind(this));
            path += "L" + this.endPoint.x + "," + this.endPoint.y;
        }
        return path;
    },
    getDefaultTextPoint: function () {
        var x = "";
        var y = "";
        if (this.toSelf) {
            x = this.positionPoints[2].x + (this.positionPoints[1].x - this.positionPoints[2].x) / 2;
            y = this.positionPoints[2].y - 8;
        } else {
            var p1 = this.beginPoint;
            var p2 = this.endPoint;
            if (this.positionPoints[0]) p2 = this.positionPoints[0];

            var xoff = (p2.x.toFloat() - p1.x.toFloat()) * 0.4;
            var yoff = (p2.y.toFloat() - p1.y.toFloat()) * 0.4;

            x = p1.x.toFloat() + xoff;
            y = p1.y.toFloat() + yoff;
        }
        return {"x": x, "y": y};
    },
    getTextPoint: function () {
        var x = "";
        var y = "";
        if (this.data.position) {
            var pArr = this.data.position.split(/(?:,\s*){1}|(?:;\s*){1}/g);
            x = pArr[0];
            y = pArr[1];
        } else {
            var p = this.getDefaultTextPoint();
            x = p.x;
            y = p.y;
        }
        return {"x": x, "y": y};
    },
    createText: function () {
        var text = null;
        if (this.beginPoint && this.endPoint) {

            var p = this.getTextPoint();

            text = this.paper.text(p.x, p.y, this.data.name || MWF.APPPD.LP.unnamed);
            text.attr(this.process.css.route.linetext.normal);
            return text;
        }
    },
    createArrow: function () {
        var beginPoint = this.beginPoint;
        if (this.positionPoints.length) {
            beginPoint = this.positionPoints[this.positionPoints.length - 1];
        }
        beginPoint.x = beginPoint.x.toFloat();
        beginPoint.y = beginPoint.y.toFloat();
        return this.paper.arrow(beginPoint, this.endPoint, this.l1, this.l2, this.aj);
    },
    createCorner: function (p, idx) {
        var corner = this.paper.rectPath((p.x.toInt()) - 2.5, (p.y.toInt()) - 2.5, 5, 5, 0);
        corner.data("position", p);
        corner.attr(this.process.css.route.corner["default"]);
        corner.hide();

        this.corners.splice(idx, 0, corner);

        corner.mousedown(function (e) {
            this.cornerBrokenLineBegin(e, corner);
        }.bind(this));

        this.set.push(corner);
        return corner;
    },

    getPoint: function (fromPath, toPath) {
        var fromActivityPath = fromPath;
        if (this.fromActivity) {
            if (!fromActivityPath) fromActivityPath = this.fromActivity.shap.attr("path");
        }
        var toActivityPath = toPath;
        if (this.toActivity) {
            if (!toActivityPath) toActivityPath = this.toActivity.shap.attr("path");
        }
        if (fromActivityPath && toActivityPath) {
            this.checked = true;
            if (this.toSelf) {
                var p1x = this.fromActivity.center.x + this.fromActivity.width;
                var p1y = this.fromActivity.center.y;

                var p2x = this.fromActivity.center.x + this.fromActivity.width;
                var p2y = this.fromActivity.center.y - this.fromActivity.height;

                var p3x = this.fromActivity.center.x;
                var p3y = this.fromActivity.center.y - this.fromActivity.height;

                this.positionPoints = [];
                this.positionPoints.push({"x": p1x, "y": p1y});
                this.positionPoints.push({"x": p2x, "y": p2y});
                this.positionPoints.push({"x": p3x, "y": p3y});

                this.beginPoint = {
                    "x": this.fromActivity.center.x + this.fromActivity.width / 2,
                    "y": this.fromActivity.center.y
                };
                this.endPoint = {
                    "x": this.fromActivity.center.x,
                    "y": this.fromActivity.center.y - this.fromActivity.height / 2
                };
            } else {
                var beginLinePath;
                var endLinePath;
                if (this.positionPoints[0]) {
                    beginLinePath = "M" + this.fromActivity.center.x + "," + this.fromActivity.center.y + "L" + this.positionPoints[0].x + "," + this.positionPoints[0].y;

                    var p = this.positionPoints[this.positionPoints.length - 1];
                    endLinePath = "M" + p.x + "," + p.y + "L" + this.toActivity.center.x + "," + this.toActivity.center.y;
                } else {
                    beginLinePath = "M" + this.fromActivity.center.x + "," + this.fromActivity.center.y + "L" + this.toActivity.center.x + "," + this.toActivity.center.y;
                    endLinePath = "M" + this.fromActivity.center.x + "," + this.fromActivity.center.y + "L" + this.toActivity.center.x + "," + this.toActivity.center.y;
                }
                var decisionPoints = Raphael.pathIntersection(beginLinePath, fromActivityPath);
                var endPoints = Raphael.pathIntersection(endLinePath, toActivityPath);

                this.beginPoint = decisionPoints[0];
                this.endPoint = endPoints[0];
            }
        }
        if (!fromActivityPath && toActivityPath) {
            this.checked = false;

            var decisionY;
            var decisionX;
            if (this.tmpBeginPoint) {
                decisionY = this.tmpBeginPoint.y.toFloat();
                decisionX = this.tmpBeginPoint.x.toFloat();
            } else {
                decisionY = this.toActivity.center.y.toFloat() - 500;
                decisionX = this.toActivity.center.x.toFloat();
            }

            var endLinePath = "M" + decisionX + "," + decisionY + "L" + this.toActivity.center.x + "," + this.toActivity.center.y;
            var endPoints = Raphael.pathIntersection(endLinePath, toActivityPath);
            this.endPoint = endPoints[0];

            this.beginPoint = this.tmpBeginPoint || {"x": this.endPoint.x, "y": this.endPoint.y - 30};
            this.tmpBeginPoint = null;
        }
        if (fromActivityPath && !toActivityPath) {
            this.checked = false;

            var endY;
            var endX;
            if (this.tmpEndPoint) {
                endY = this.tmpEndPoint.y.toFloat();
                endX = this.tmpEndPoint.x.toFloat();
            } else {
                endY = this.fromActivity.center.y.toFloat() + 500;
                endX = this.fromActivity.center.x.toFloat();
            }

            var beginLinePath = "M" + this.fromActivity.center.x + "," + this.fromActivity.center.y + "L" + endX + "," + endY;
            var decisionPoints = Raphael.pathIntersection(beginLinePath, fromActivityPath);
            this.beginPoint = decisionPoints[0];

            this.endPoint = this.tmpEndPoint || {"x": this.beginPoint.x, "y": this.beginPoint.y + 30};
            this.tmpEndPoint = null;
        }
        if (!fromActivityPath && !toActivityPath) {
            this.checked = false;
            this.beginPoint = {"x": 10, "y": 10};
            this.endPoint = {"x": 10, "y": 30};
        }
    },

    setListItemData: function () {
        if (this.listItem) {
            var routeName = this.data.name || MWF.APPPD.LP.unnamed;
            var name = "";
            if (this.toActivity) {
                name = this.toActivity.data.name;
                if (!name) name = MWF.APPPD.LP.unnamed;
            } else {
                name = MWF.APPPD.LP.unknow;
            }
            this.listItem.row.tds[1].set("text", routeName + " (to " + name + ")");
        }
    },
    destroy: function () {
        if (this.fromActivity) {
            this.fromActivity.removeRouteData(this.data.id);
//			if (this.fromActivity.data.routeList){
//				this.fromActivity.data.routeList.erase(this.data.id);
//			}
            this.fromActivity.routes.erase(this);
        }

        if (this.listItem) {
            this.listItem.row.tr.destroy();
        }

        var routes = {};
        var routeDatas = {};
        for (rid in this.process.routes) {
            if (rid != this.data.id) {
                routes[rid] = this.process.routes[rid];
                routeDatas[rid] = this.process.routeDatas[rid];
            } else {
                this.process.routes[rid] = null;
                this.process.routeDatas[rid] = null;
            }
        }
        this.process.routes = null;
        this.process.routeDatas = null;
        this.process.routes = routes;
        this.process.routeDatas = routeDatas;
        this.process.process.routeList.erase(this.data);

        this.set.remove();
    },
    showProperty: function () {
        if (!this.property) {
            this.property = new MWF.APPPD.Route.Property(this, {
                "onPostLoad": function () {
                    this.property.show();
                }.bind(this)
            });
            this.property.load();
        } else {
            this.property.show();
        }
    },
    _setEditProperty: function (name, input, oldValue) {
        debugger;
        if (name === "passExpired" || name === "passSameTarget"  || name === "sole") {
            if (this.data[name]) {
                if (this.fromActivity) {
                    this.fromActivity.routes.each(function(route){
                        if (route.data.id !== this.data.id) {
                            if (route.data[name]) {
                                route.data[name] = false;
                                if (route.property){
                                    var node = route.property.propertyContent.getElementById(route.data.id+name);
                                    if (node) node.getElements("input")[1].set("checked", true);
                                }
                            }
                        }
                    }.bind(this));
                }
            }
        }
    }
});

MWF.xApplication.process.ProcessDesigner.Route.List = new Class({
	initialize: function(route){
		this.route = route;
		this.process = route.process;
		this.paper = this.route.paper;
	},
	load: function(){
		var routeName = this.route.data.name || MWF.APPPD.LP.unnamed;
		var name = "";
		if (this.route.toActivity){
			name = this.route.toActivity.data.name;
			if (!name) name = MWF.APPPD.LP.unnamed;
		} else {
			name = MWF.APPPD.LP.unknow;
		}
		this.row = this.process.routeTable.push([
			    {	
			    	"content": " ",
			    	"properties": {
			    		"styles": this.process.css.route.icon
			        }
			    },
			    {	
			    	"content": routeName+" (to "+name+")",
			    	"properties": {
			    		"styles": this.process.css.list.listText
			        }
			    },
			    {	
			    	"content": "<img src=\""+"/x_component_process_ProcessDesigner/$Process/default/icon/copy.png"+"\" />",
			    	"properties": {
			    		"styles": this.process.css.list.listIcon,
			    		"events": {
			    			"click": this.copyRoute.bind(this)
			    		}
			        }
			    },
			    {	
			    	"content": "<img src=\""+"/x_component_process_ProcessDesigner/$Process/default/icon/delete.png"+"\" />",
			    	"properties": {
			    		"styles": this.process.css.list.listIcon,
			    		"events": {
			    			"click": this.deleteRoute.bind(this)
			    		}
			        }
			    }
			
			]
		);
		this.row.tr.addEvent("click", function(){
			this.listSelected();
		}.bind(this));
	},
	copyRoute: function(){
		this.process.copyRoute(this.route);
	},
	deleteRoute: function(e){
		this.process.deleteRoute(e, this.route);
	},
	selected: function(){
		if (this.process.currentListSelected) this.process.currentListSelected.listUnSelected();
		this.row.tr.setStyles(this.process.css.list.listRowSelected);
		this.process.currentListSelected = this;
	},
	unSelected: function(){
		this.process.currentListSelected = null;
		this.row.tr.setStyles(this.process.css.list.listRow);
	},
	listSelected: function(){
		this.route.selected();
	},
	listUnSelected: function(){
		this.route.unSelected();
	}
});

MWF.xApplication.process.ProcessDesigner.Route.Property = new Class({
	Implements: [Options, Events],
	Extends: MWF.APPPD.Property,
	initialize: function(route, options){
		this.setOptions(options);
		
		this.route = route;
		this.process = route.process;
		this.paper = this.process.paper;
		this.data = route.data;
		this.htmlPath = "/x_component_process_ProcessDesigner/$Process/route.html";
	},
	setValue: function(name, value){
		this.data[name] = value;
		if (name=="name"){
			if (!value) this.data[name] = MWF.APPPD.LP.unnamed;
			this.route.reload();
		}
	},
    show: function(){
        if (!this.process.options.isView){
            if (!this.propertyContent){
                this.propertyContent = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.process.propertyListNode);
                this.process.panel.propertyTabPage.showTabIm();
                this.JsonTemplate = new MWF.widget.JsonTemplate(this.data, this.htmlString);
                this.propertyContent.set("html", this.JsonTemplate.load());
                this.process.panel.data = this.data;

                this.loadRouteCondition();

                this.setEditNodeEvent();
                this.setEditNodeStyles(this.propertyContent);
                this.loadPropertyTab();
                this.loadPersonInput();
                this.loadScriptInput();
                this.loadScriptText();
                this.loadConditionInput();
                this.loadFormSelect();
                this.loadOrgEditor();
            }else{
                this.propertyContent.setStyle("display", "block");
            }
        }
    },
    loadRouteCondition: function(){
        var routeConditionNode = this.propertyContent.getElement(".MWFRouteCondition");
        var type = this.route.fromActivity.type;
        if (type=="choice" || type=="condition" || type=="parallel"){
            if (!routeConditionNode){
                routeConditionNode = new Element("div.MWFTab", {
                    "title": MWF.APPPD.LP.condition,
                    "html": "<div class=\"MWFScriptText\" name=\"scriptText\"></div>"
                }).inject(this.propertyContent.getFirst());
            }
        }else{
            if (routeConditionNode) routeConditionNode.destroy();
        }
    }
    //loadScriptText: function(){
    //    var node = this.propertyContent.getElement(".MWFScriptText");
    //    if (node){
    //        MWF.require("MWF.xApplication.process.ProcessDesigner.widget.ScriptText", function(){
    //            var _self = this;
    //            //        scriptNodes.each(function(node){
    //            var data = (_self.route.fromActivity.data.extension) ? JSON.decode(_self.route.fromActivity.data.extension) : {};
    //            var code = (data[_self.route.data.id]) || "";
    //
    //            var script = new MWF.xApplication.process.ProcessDesigner.widget.ScriptText(node, code, this.process.designer, {
    //                "maskNode": this.process.designer.content,
    //                "onChange": function(code){
    //                    var id = _self.route.data.id;
    //                    data[id] = code;
    //                    var jsonString = JSON.encode(data);
    //                    _self.route.fromActivity.data.extension = jsonString;
    //                    _self.route.fromActivity.data.scriptText = "return this.library.choiceRoute('"+encodeURIComponent(jsonString)+"');";
    //                }
    //            });
    //            //this.setScriptItems(script, node);
    //            //        }.bind(this));
    //        }.bind(this));
    //    }
    //}
});