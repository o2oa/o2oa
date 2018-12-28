o2.widget = o2.widget || {};
o2.widget.chart = o2.widget.chart || {};
o2.require("o2.widget.Common", null, false);
o2.require("o2.widget.chart.d3", null, false);
o2.require("o2.widget.UUID", null, false);
o2.widget.chart.Dashboard = new Class({
    Implements: [Options, Events],
    Extends: o2.widget.Common,
    options: {
        "style": "default",
        "margin": "10px",
        "sort": "ASC", //ASC, DESC
        "red":.3,
        "green":.3,
        "borderWidth": 6,
        "arcWidth": 15,
        "arcMargin": 22,
        "ticks": 7,
        "tickLong": 6,
        "miniTicks": 31,
        "miniTickLong": 3,
        "centerWidth": 6,
        "format": "",
        "formatdData": "",
        "text": "",

        "domain": []

    },
    initialize: function(node, data, options){
        this.setOptions(options);
        this.container = $(node);
        this.data = data;
        this.classId = new o2.widget.UUID();

        this.path = o2.session.path+"/widget/chart/$Dashboard/";
        this.cssPath = o2.session.path+"/widget/chart/$Dashboard/"+this.options.style+"/css.wcss";
        this._loadCss();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.node}).inject(this.container);
        this.setNodeSize();

        o2.widget.chart.d3.load(function(){
            if (this.fireEvent("queryLoad")){
                this.svg = d3.select(this.node).append("svg");
                this.setSvgSize();

                this.showData = this.data;
                var max = d3.max(this.options.domain);
                var min = d3.min(this.options.domain);
                if (this.data>max) this.showData = max;
                if (this.data<min) this.showData = min;

                //this.group = this.svg.append("g");

                this.drawCircleOutline();

                this.drawArc();
                this.drawMiniTicks();
                this.drawTicks();
                this.drawText();
                this.drawPointer();

                //
                //this.loadScales();
                //this.loadXaes();
                //this.loadBars();
                //
                //this.setStyles();
                //this.setEvents();
            }


        }.bind(this));
        this.fireEvent("postLoad");

    },
    setNodeSize: function(){
        var margin = this.options.margin.toInt();
        var size = this.container.getSize();
        var width = size.x-(margin*2);
        var height = size.y-(margin*2);
        this.size = {"x": width, "y": height, "m": margin};
    },
    setSvgSize: function(){
        this.svg.attr("width", "100%").attr("height", "100%");
    },
    drawCircleOutline: function(){
        var r = this.size.x/2;
        var x = this.size.x/2 + this.size.m;
        this.circleBig = this.svg.append("circle")
            .attr("cx", x)
            .attr("cy", x)
            .attr("r", r);
        Object.each(this.css.circleBig, function(v,k){this.circleBig.attr(k, v);}.bind(this));

        r = r - this.options.borderWidth;
        this.circleSmall = this.svg.append("circle")
            .attr("cx", x)
            .attr("cy", x)
            .attr("r", r);
        Object.each(this.css.circleSmall, function(v,k){this.circleSmall.attr(k, v);}.bind(this));

        this.circleCenter = this.svg.append("circle")
            .attr("cx", x)
            .attr("cy", x)
            .attr("r", this.options.centerWidth);
        Object.each(this.css.circleCenter, function(v,k){this.circleCenter.attr(k, v);}.bind(this));

        this.setDefs("circleBig_defs", this.circleBig);
        this.setDefs("circleSmall_defs", this.circleSmall);
        this.setDefs("circleCenter_defs", this.circleCenter);
    },
    setDefs: function(name, selection){
        if (this.css[name]){
            var defs = this.svg.append("defs");
            this.createDefs(defs, this.css[name]);
            defs.select(function(){ return this.getFirst(); }).attr("id", this.classId+"_"+name);
            selection.attr(this.css[name].urlAttr, "url(#"+this.classId+"_"+name+")");
        }
    },
    createDefs: function(node, data){
        var svgNode = node.append(data.tag);
        Object.each(data.attrs, function(v,k){svgNode.attr(k,v);});
        if (data.subs) {
            data.subs.each(function(v){
                this.createDefs(svgNode, v);
            }.bind(this));
        }
    },

    drawArc: function(){
        //this.angleScale = d3.scaleBand().domain(this.data.map(function(d){ return d[this.item];}.bind(this)))
        //    .rangeRound(this.getXScaleRange()).paddingOuter(0.1).paddingInner(0.1);
        this.angleScale = d3.scaleLinear().domain(this.options.domain)
            .range([0-0.75*Math.PI, 0.75*Math.PI]);

        var greenAngle = this.angleScale(this.options.green*d3.max(this.options.domain));
        var redAngle = this.angleScale((1-this.options.red)*d3.max(this.options.domain));

        var green = {"startAngle": 0-0.75*Math.PI,"endAngle": greenAngle};
        var yellow = {"startAngle": greenAngle,"endAngle": redAngle};
        var red = {"startAngle": redAngle,"endAngle": 0.75*Math.PI};

        var or = this.size.x/2-this.options.arcMargin-this.options.borderWidth;
        var ir = this.size.x/2-this.options.arcMargin-this.options.arcWidth-this.options.borderWidth;
        var x = this.size.x/2 + this.size.m;

        var arcPath = d3.arc().innerRadius(ir).outerRadius(or);

        this.greenArc = this.svg.append("path").attr("d", arcPath(green)).attr("transform", "translate("+x+", "+x+")");
        this.yellowArc = this.svg.append("path").attr("d", arcPath(yellow)).attr("transform", "translate("+x+", "+x+")");
        this.redArc = this.svg.append("path").attr("d", arcPath(red)).attr("transform", "translate("+x+", "+x+")");

        Object.each(this.css.greenArc, function(v,k){this.greenArc.attr(k, v);}.bind(this));
        Object.each(this.css.yellowArc, function(v,k){this.yellowArc.attr(k, v);}.bind(this));
        Object.each(this.css.redArc, function(v,k){this.redArc.attr(k, v);}.bind(this));

        this.setDefs("greenArc_defs", this.greenArc);
        this.setDefs("yellowArc_defs", this.yellowArc);
        this.setDefs("redArc_defs", this.redArc);
    },

    drawTicks: function(){
        var per = d3.max(this.options.domain)/(this.options.ticks-1);
        var or = this.size.x/2-this.options.borderWidth;
        var ir = this.size.x/2-this.options.tickLong-this.options.borderWidth;
        var x = this.size.x/2 + this.size.m;
        var arcPath = d3.arc().innerRadius(ir).outerRadius(or);
        var arcTextPath = d3.arc().innerRadius(ir-16).outerRadius(ir);

        for (var i=0; i<this.options.ticks; i++){
            var arcAngle = this.angleScale((per*i));
            var angle = {"startAngle": arcAngle,"endAngle": arcAngle};
            var arc = this.svg.append("path").attr("d", arcPath(angle)).attr("transform", "translate("+x+", "+x+")");
            Object.each(this.css.tick, function(v,k){arc.attr(k, v);}.bind(this));

            var position = arcTextPath.centroid(angle);
            var text = Math.round(per*i);
            if (text>1000){
                text = text/1000;
                text = d3.format("0.3r")(text);
                text = text+"k";
            }

            this.svg.append("text")
                .attr("transform", "translate("+x+", "+x+") "+"translate("+position[0]+", "+position[1]+")")
                .attr("text-anchor", "middle")
                .attr("dy", "4")
                .attr("font-size", "10px")
                .text(text);
        }

    },
    drawMiniTicks: function(){
        var miniPer = d3.max(this.options.domain)/(this.options.miniTicks-1);
        var or = this.size.x/2-this.options.borderWidth;
        var ir = this.size.x/2-this.options.miniTickLong-this.options.borderWidth;
        var x = this.size.x/2 + this.size.m;
        var arcPath = d3.arc().innerRadius(ir).outerRadius(or);

        for (var i=0; i<this.options.miniTicks; i++){
            var arcAngle = this.angleScale((miniPer*i));
            var angle = {"startAngle": arcAngle,"endAngle": arcAngle};
            var arc = this.svg.append("path").attr("d", arcPath(angle)).attr("transform", "translate("+x+", "+x+")");
            Object.each(this.css.miniTick, function(v,k){arc.attr(k, v);}.bind(this));
        }
    },

    getPointerPath: function(data){
        var x = this.size.x/2 + this.size.m;
        var ir = this.size.x/2-this.options.tickLong-this.options.borderWidth;
        var arcTextPath = d3.arc().innerRadius(ir-24).outerRadius(ir);

        var arcAngle = this.angleScale(data);
        var angle = {"startAngle": arcAngle,"endAngle": arcAngle};
        var to = arcTextPath.centroid(angle);
        var lx = to[0]+x;
        var ly = to[1]+x;


        var arcAngleStart = this.angleScale(d3.min(this.options.domain));

        var x1 = 0-to[0];
        var y1 = 0-to[1];
        var l = Math.sqrt(Math.pow(x1, 2)+Math.pow(y1,2));

        var r = this.options.centerWidth-4;
        var tmpA1 = Math.asin(y1/l);
        var tmpA2 = Math.asin(r/l);
        var a1, a2;
        if (x1>=0){
            a1 = (tmpA1-tmpA2)+(Math.PI/2);
            a2 = (tmpA1+tmpA2)+(Math.PI/2);
        }else{
            a2 = (Math.PI*2) - ((tmpA1-tmpA2)+(Math.PI/2));
            a1 = (Math.PI*2) - ((tmpA1+tmpA2)+(Math.PI/2));
        }


        if (!this.arcPointerPath) this.arcPointerPath = d3.arc().innerRadius(0).outerRadius(l+10);
        var angle = {"startAngle": a1,"endAngle": a2};
        var d = this.arcPointerPath(angle);

        return {"d": d, "lx": lx, "ly": ly};
    },
    drawPointer: function(){
        //var path = this.getPointerPath(this.showData);
        var pathStart = this.getPointerPath(d3.min(this.options.domain));

        this.pointer = this.svg.append("path").attr("d", pathStart.d).attr("transform", "translate("+pathStart.lx+", "+pathStart.ly+")");
        Object.each(this.css.pointer, function(v,k){this.pointer.attr(k, v);}.bind(this));
        this.setDefs("pointer_defs", this.pointer);

        this.transition();
    },
    transition: function(){
        var pathStart = this.getPointerPath(d3.min(this.options.domain));
        var lx = pathStart.lx;
        var ly = pathStart.ly;
        this.pointer.attr("d", pathStart.d).attr("transform", "translate("+pathStart.lx+", "+pathStart.ly+")");

        this.pointer.transition().delay(200)
            .duration(3000)
            .ease(d3.easeElasticOut)
            .attrTween("d", function(d, i, v){
                return function(t){
                    var p = this.getPointerPath(this.showData*t);
                    lx = p.lx;
                    ly = p.ly
                    return p.d
                }.bind(this)
            }.bind(this))
            .attrTween("transform", function(d, i, v){
                return function(t){
                    return "translate("+lx+", "+ly+")"
                }.bind(this)
            }.bind(this));
    },

    drawText: function(){
        var r = this.size.x/2-this.options.arcMargin-this.options.borderWidth;
        var x = this.size.x/2 + this.size.m;
        var y = this.size.x/2 + this.size.m + r;

        this.dataText = this.svg.append("text")
            .attr("transform", "translate("+x+", "+y+") ")
            .attr("text-anchor", "middle")
            .attr("dy", "4")
            .attr("font-size", "10px")
            .text((this.options.formatData) ? d3.format(this.options.formatData)(this.options.text || this.data) : (this.options.text || this.data));

        Object.each(this.css.dataText, function(v,k){this.dataText.attr(k, v);}.bind(this));
        this.setDefs("dataText_defs", this.dataText);
    },

    hide: function(){
        this.node.setStyle("display", "none");
    },
    show: function(){
        this.node.setStyle("display", "block");
        this.transition();
    },
    destroy: function(){
        this.node.destroy();
        d3.interrupt(this.pointer);
        o2.release(this);
    }

});