o2.widget = o2.widget || {};
o2.widget.chart = o2.widget.chart || {};
o2.require("o2.widget.Common", null, false);
o2.require("o2.widget.chart.d3", null, false);
o2.widget.chart.Bar = new Class({
    Implements: [Options, Events],
    Extends: o2.widget.Common,
    options: {
        "style": "default",
        "xais": true,
        "direction": {"x": "bottom", "y": "left"}, //{"x": "bottom", "y": "right"}, {"x": "left", "y": "bottom"}, {"x": "top", "y": "left"}, {"x": "top", "y": "right"}...
        "ticks": 10,
        "xText": "",
        "yText": "text",
        "marginLeft": 40,
        "marginRight": 0,
        "marginTop": 20,
        "marginBottom": 46,
        "tickFormat": "",
        "dataFormat": "",
        "delay": 80,
        "duration": 200,
        "transition": Fx.Transitions.Back.easeIn
    },
    initialize: function(node, data, item, options){
        this.setOptions(options);
        this.bars = [];
        this.item = item;
        this.node = $(node);
        this.data = data;

        this.path = o2.session.path+"/widget/chart/$Bar/";
        this.cssPath = o2.session.path+"/widget/chart/$Bar/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.colors = this.css.colors || d3.schemeCategory10;
    },
    setItem: function(item){
        this.item = item;
    },
    addBar: function(data, text){
        var d = {"data": data, "text": (text || data)};
        //this.bars.push(data);
        this.bars.push(d);
    },
    load: function(){

        this.svgNode = new Element("div", {"styles": this.css.svgNode}).inject(this.node);
        if (this.fireEvent("queryLoad")){
            this.size = this.node.getSize();
            o2.widget.chart.d3.load(function(){
                //this.data = d3.map(this.jsonData, function(d, i){return i;});

                this.svg = d3.select(this.svgNode).append("svg");
                this.group = this.svg.append("g");
                this.setSvgSize();

                this.loadScales();
                this.loadXaes();
                this.loadChart();

                this.setStyles();
                this.setEvents();

            }.bind(this));
            this.fireEvent("postLoad");
        }
    },
    loadScales: function(){
        this.xScale = d3.scaleBand().domain(this.data.map(function(d){ return d[this.item];}.bind(this)))
            .rangeRound(this.getXScaleRange()).paddingOuter(0.3).paddingInner(0.3);

        this.barsData = [];
        this.bars.each(function(bar, i){
            this.barsData.push(
                this.data.map(function(d, idx) {
                    return {"name": d[this.item], "data": ((typeOf(bar.data)==="function") ? bar.data(d, i) : d[bar.data]), "text": ((typeOf(bar.text)==="function") ? bar.text(d, i) : d[bar.text])}
                }.bind(this))
            );
        }.bind(this));
        var max = d3.max(this.barsData, function(d){ return d3.max(d, function(d){return d.data}); });
        var min = d3.min(this.barsData, function(d){ return d3.min(d, function(d){return d.data}); });

        this.yScale = d3.scaleLinear().domain([min*0.9, max*1.1])
            .range(this.getYScaleRange());
    },

    loadChart: function(){
        this.rectClass = Math.round(Math.random()*100);
        var barWidth = this.xScale.bandwidth()/this.barsData.length;
        this.rectCluster = [];
        this.textCluster = [];
        this.barsData.each(function(bar, i){
            //var rects = this.group.selectAll(".MWFBar_"+this.rectClass+"_"+i)
            //    .data(bar)
            //    .enter().append("rect")
            //    .attr("class", ".MWFBar_"+this.rectClass+"_"+i)
            //    .attr("x", function(d) { return this.xScale(d.name)+(i*barWidth); }.bind(this))
            //    .attr("width", barWidth)
            //    .attr("y", function(d) { return this.yScale(d.data); }.bind(this))
            //    .attr("height", function(d) { return this.size.y - this.yScale(d.data); }.bind(this))
            //    .attr("fill", colors[i]);
            var rects = this.group.selectAll(".MWFBar_"+this.rectClass+"_"+i)
                .data(bar)
                .enter().append("rect")
                .attr("class", ".MWFBar_"+this.rectClass+"_"+i)
                .attr("x", function(d) { return this.xScale(d.name)+(i*barWidth); }.bind(this))
                .attr("width", barWidth)
                .attr("y", this.size.y)
                .attr("height", 0)
                .attr("fill", this.colors[i]);
            this.rectCluster.push(rects);

            var texts = this.group.selectAll(".MWFBarText_"+this.rectClass+"_"+i)
                .data(bar)
                .enter().append("text")
                .text(function(d){ return (this.options.dataFormat) ? d3.format(this.options.dataFormat)(d.data) : d.text;}.bind(this))
                .attr("x", function(d) {
                    var x = o2.getTextSize((this.options.dataFormat) ? d3.format(this.options.dataFormat)(d.data) : d.text).x;
                    return (x<barWidth) ? this.xScale(d.name)+(i*barWidth)+barWidth/2-x/2 : this.xScale(d.name)+(i*barWidth);
                }.bind(this))
                .attr("y", function(d) { return this.yScale(d.data)-5; }.bind(this));
            this.textCluster.push(texts);
                //.attr("width", function(d){ return o2.getTextSize((this.options.dataFormat) ? d3.format(this.options.dataFormat)(d.data) : d.data).x}.bind(this));
        }.bind(this));
        this.transition();
    },
    transition: function(rects){
        if (!rects) rects = this.group.selectAll("rect");
        rects.attr("y", this.size.y).attr("height", 0);

        //var max = this.yScale.domain()[1];

        rects.transition().delay(function(d, i) { return i * this.options.delay;}.bind(this))
            //.duration(function(d, i){ var v = d.data || 0; return (v/max)*20000; })
            .duration(this.options.duration)
            .ease(d3.easeExpOut)
            .attr("height", function(d) { return this.size.y - this.yScale(d.data); }.bind(this))
            .attr("y", function(d) { return this.yScale(d.data); }.bind(this));
    },

    loadXaes: function(){
        var xAxisMethod = ("axis-"+this.options.direction.x).camelCase();
        this.xAxis = d3[xAxisMethod]().scale(this.xScale);


        var yAxisMethod = ("axis-"+this.options.direction.y).camelCase();
        this.yAxis = d3[yAxisMethod]()
            .scale(this.yScale)
            .ticks(this.options.ticks);
        if (this.options.tickFormat){
            this.yAxis.tickFormat(d3.format(this.options.tickFormat));
        }


        this.xais_x_group = this.svg.append("g")
            .attr("transform", "translate("+this.options.marginLeft+"," + (this.size.y+this.options.marginTop) + ")")
            .call(this.xAxis);

        if (this.options.xais){
            this.xais_y_group = this.svg.append("g")
                .attr("transform", "translate("+this.options.marginLeft+","+this.options.marginTop+")");
            this.xais_y_group.call(this.yAxis);

                //.append("text")
                //.attr("transform", "rotate(-90)")
                //.attr("y", 6)
                //.attr("dy", ".71em")
                //.style("text-anchor", "end")
                //.text(this.options.xText);
        }

    },
    getXScaleRange: function(){
        return [0, this.size.x];
    },
    getYScaleRange: function(){
        return [this.size.y, 0];
    },
    setSvgSize: function(){
        var x = this.size.x-(this.options.marginLeft+this.options.marginRight);
        var y = this.size.y-(this.options.marginTop+this.options.marginBottom);
        //this.svgNode.setStyles({
        //    "margin": ""+this.options.margin+"px "+this.options.margin+"px "+this.options.marginX+"px "+this.options.marginY+"px",
        //    "width": ""+x+"px",
        //    "height": ""+y+"px"
        //});
        //var m = this.svgNode.getStyles("margin-top", "margin-bottom", "margin-left", "margin-right");
        //
        //var w = this.size.x-m["margin-left"]-m["margin-right"];
        //var h = this.size.y-m["margin-top"]-m["margin-bottom"];
        //this.svgNode.setStyles({
        //    "width": ""+w+"px",
        //    "height": ""+h+"px"
        //});
        //this.svg.attr("width", x).attr("height", y);
        this.svg.attr("width", this.size.x).attr("height", this.size.y);

        this.group.attr("width", x).attr("height", y)
            .attr("transform", "translate("+this.options.marginLeft+","+this.options.marginTop+")");
        this.size = {"x": x, "y": y};
    },

    setStyles: function(){
        var _self = this;
        var texts = this.xais_x_group.selectAll("text");
        var paths = this.xais_x_group.selectAll("path");
        var lines = this.xais_x_group.selectAll("line");
        Object.each(this.css.xxais_text, function(v,k){texts.attr(k, function(d){ return (typeOf(v)=="function") ? v.apply(_self, [d, this]) : v; });}.bind(this));
        Object.each(this.css.xxais_path, function(v,k){paths.attr(k, v);}.bind(this));
        Object.each(this.css.xxais_line, function(v,k){lines.attr(k, v);}.bind(this))
        this.setDefs("xxais_text_defs", texts);
        this.setDefs("xxais_path_defs", paths);
        this.setDefs("xxais_line_defs", lines);

        if (this.xais_y_group){
            texts = this.xais_y_group.selectAll("text");
            paths = this.xais_y_group.selectAll("path");
            lines = this.xais_y_group.selectAll("line");
            Object.each(this.css.yxais_text, function(v,k){texts.attr(k, v);}.bind(this));
            Object.each(this.css.yxais_path, function(v,k){paths.attr(k, v);}.bind(this));
            Object.each(this.css.yxais_line, function(v,k){lines.attr(k, v);}.bind(this));
            this.setDefs("yxais_text_defs", texts);
            this.setDefs("yxais_path_defs", paths);
            this.setDefs("yxais_line_defs", lines);
        }

        var rects = this.group.selectAll("rect");
        texts = this.group.selectAll("text");
        Object.each(this.css.rect, function(v,k){rects.attr(k, v);}.bind(this));
        Object.each(this.css.rectText, function(v,k){texts.attr(k, function(d){ return (typeOf(v)=="function") ? v.apply(_self, [d, this]) : v; });}.bind(this));
        this.setDefs("rect_defs", rects);
        this.setDefs("rectText_defs", texts);

    },
    setEvents: function(){
        var rects = this.group.selectAll("rect");
        var texts = this.group.selectAll("text");
        rects.on("mouseover", function(d, i, nodeList){
            this.fireEvent("mouseover", [rects, texts, d, i]);
        }.bind(this)).on("mouseout", function(d, i, nodeList){
            this.fireEvent("mouseout", [rects, texts, d, i]);
        }.bind(this)).on("click", function(d, i, nodeList){
            this.fireEvent("click", [rects, texts, d, i]);
        }.bind(this));
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
    destroy: function(){
        this.svgNode.destroy();
        o2.release(this);
    }
});
