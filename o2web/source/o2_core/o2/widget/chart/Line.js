o2.widget = o2.widget || {};
o2.widget.chart = o2.widget.chart || {};
o2.require("o2.widget.Common", null, false);
o2.require("o2.widget.chart.d3", null, false);
o2.require("o2.widget.chart.Bar", null, false);
o2.widget.chart.Line = new Class({
    Implements: [Options, Events],
    Extends: o2.widget.chart.Bar,

    loadChart: function(){
        this.rectClass = Math.round(Math.random()*100);
        var barWidth = this.xScale.bandwidth()/this.barsData.length;
        this.pointCluster = [];
        this.textCluster = [];
        this.lineCluster = [];

        this.lineGroup = this.group.append("g");

        this.linePoint = [];
        this.barsData.each(function(bar, i){

            var lines = d3.line()
                .x(function(d) { return this.xScale(d.name)+this.xScale.bandwidth()/2; }.bind(this))
                .y(function(d) { return this.yScale(d.data); }.bind(this))
                .curve(d3.curveCardinal.tension(0.25));

            var line = this.lineGroup.selectAll(".MWFLine_"+this.rectClass+"_"+i)
                .data([bar])
                .enter().append("path")
                .attr("d", lines(bar))
                .attr("stroke", this.colors[i])
                .attr("fill", "none");
            this.lineCluster.push(line);

            var point = this.group.selectAll(".MWFBar_"+this.rectClass+"_"+i)
                .data(bar)
                .enter().append("circle")
                .attr("r", 5)
                .attr("class", ".MWFBar_"+this.rectClass+"_"+i)
                .attr("cx", function(d) { return this.xScale(d.name)+this.xScale.bandwidth()/2; }.bind(this))
                .attr("cy", function(d) { return this.yScale(d.data); }.bind(this))
                .attr("stroke", this.colors[i])
                .attr("fill", "#ffffff");
            this.pointCluster.push(point);

            var texts = this.group.selectAll(".MWFLineText_"+this.rectClass+"_"+i)
                .data(bar)
                .enter().append("text")
                .text(function(d){ return (this.options.dataFormat) ? d3.format(this.options.dataFormat)(d.data) : d.text;}.bind(this))
                .attr("x", function(d) {
                    var x = o2.getTextSize((this.options.dataFormat) ? d3.format(this.options.dataFormat)(d.data) : d.text).x;
                    return this.xScale(d.name)+this.xScale.bandwidth()/2-x/2;
                }.bind(this))
                .attr("y", function(d) { return this.yScale(d.data)-10; }.bind(this));
            this.textCluster.push(texts);
                //.attr("width", function(d){ return o2.getTextSize((this.options.dataFormat) ? d3.format(this.options.dataFormat)(d.data) : d.data).x}.bind(this));
        }.bind(this));
        //this.transition();
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
    setEvents: function(){
        var rects = this.group.selectAll("circle");
        var texts = this.group.selectAll("text");
        var _self = this;
        rects.on("mouseover", function(d, i, nodeList){
            _self.highlight(rects, texts, d, i);
            this.fireEvent("mouseover", [rects, texts, d, i]);
        }.bind(this)).on("mouseout", function(d, i, nodeList){
            _self.normal(rects, texts, d, i);
            this.fireEvent("mouseout", [rects, texts, d, i]);
        }.bind(this)).on("click", function(d, i, nodeList){
            this.fireEvent("click", [rects, texts, d, i]);
        }.bind(this));
    },
    highlight: function(shape, texts, d, i){
        texts.filter(function(data, idx){return (idx==i);}).attr("display", "block");
        var rect = shape.filter(function(data, idx){return (idx==i);});
        var color = rect.attr("fill");
        rect.node().store("color", color);
        rect.attr("r", "6");
        rect.attr("stroke-width", "3");
    },
    normal: function(shape, texts, d, i){
        texts.filter(function(data, idx){return (idx==i);}).attr("display", "none");
        var rect = shape.filter(function(data, idx){return (idx==i);});
        var color = rect.node().retrieve("color");
        rect.attr("fill", color);
        rect.attr("r", "6");
        rect.attr("stroke-width", "1");
    },

    transition: function(rects){
        if (!rects) rects = this.group.selectAll("path");
        //rects.attr("y", this.size.y).attr("height", 0);

        //var max = this.yScale.domain()[1];
        var barWidth = this.xScale.bandwidth()/this.barsData.length;
        var lines = d3.line()
            .x(function(d, i) { return this.xScale(d.name)+(i*barWidth)+this.xScale.bandwidth()/2; }.bind(this))
            .y(function(d) { return this.yScale(d.data); }.bind(this))
            .curve(d3.curveCardinal.tension(0));


        rects.transition().delay(function(d, i) { return i * this.options.delay;}.bind(this))
            //.duration(function(d, i){ var v = d.data || 0; return (v/max)*20000; })
            .duration(this.options.duration)
            //.duration(5000)
            .ease(d3.easeExpOut)
            // .attrTween("d", function(d,i){
            //     return function(t){
            //         var a = d.endAngle-d.startAngle;
            //         var tmp={"startAngle": d.startAngle, "endAngle": d.startAngle+(a*t)};
            //         return this.arc(tmp);
            //     }.bind(this)
            // }.bind(this))
            // .on("end", function(d,i){
            //     var r = (this.size.x>this.size.y) ? this.size.y : this.size.x;
            //     r = r/2 - this.options.margin;
            //     var tmpArc = d3.arc().innerRadius(0).outerRadius(r+20);
            //     var p = tmpArc.centroid(d);
            //
            //     var text = d3.select(groups.nodes()[i]).append("text")
            //         .attr("transform", "translate("+(p[0])+", "+(p[1])+")")
            //         .text(d.data.percent || d3.format(this.options.dataFormat)(d.data.value));
            //     Object.each(this.css.text, function(v,k){text.attr(k, function(d){ return (typeOf(v)=="function") ? v.apply(_self, [d, this]) : v; });}.bind(this));
            //     this.setDefs("text_defs", text);
            // }.bind(this));
            //
            // .attr("height", function(d) { return this.size.y - this.yScale(d.data); }.bind(this))
            // .attr("y", function(d) { return this.yScale(d.data); }.bind(this));
            .attr("d", function(d) { return lines(d) }.bind(this));
    }
});
