o2.widget = o2.widget || {};
o2.widget.MWFRaphael = MWFRaphael = {
	load: function(callback){
        if (window.Raphael){
            if (callback) callback();
        }else{
            COMMON.AjaxModule.loadDom("raphael", function(){
                this.expandRaphael();
                if (callback) callback();
            }.bind(this), true, true);
        }
	},
	expandRaphael: function(){
		Raphael.fn.diamond = function(x, y, width, height, r1, r2){
			return MWFRaphael.diamond.call(this, x, y, width, height, r1, r2);
		};
		Raphael.fn.rectPath = function(x, y, width, height, r){
			return MWFRaphael.rectPath.call(this, x, y, width, height, r);
		};
		Raphael.fn.circlePath = function(x, y, r){
			return MWFRaphael.circlePath.call(this, x, y, r);
		};
		Raphael.fn.arrow = function(beginPoint, endPoint, l1, l2, aj){
			return MWFRaphael.arrow.call(this, beginPoint, endPoint, l1, l2, aj);
		};
	},
	rectPath: function(x, y, width, height, r){
		var path = MWFRaphael.getRectPath(x, y, width, height, r);
		return this.path(path);
	},
	getRectPath: function(x, y, width, height, r){
		x = x.toFloat();
		y = y.toFloat();
		width = width.toFloat();
		height = height.toFloat();
		r = r.toFloat();
		
		
		var beginPoint = {
			x: x+width-r,
			y: y+height-r
		};
		var arc_t_h = height-r-r;
		var arc_t_w = width-r-r;
		
		//先得到圆角path------------------------------------------------------
		//    |
		//----------  这个位置的圆角
		//    |  *
		var arcPath1 = Raphael.parsePathString(MWFRaphael.getArcPath(beginPoint.x, beginPoint.y, r));
		
		var r_c_p_x = beginPoint.x;
		var r_c_p_y = beginPoint.y;
		
		//	  |  *
		//----------  这个位置的圆角
		//    |  
		var arcPath2 = Raphael.transformPath(arcPath1, "r-90,"+r_c_p_x+","+r_c_p_y+"T0,-"+arc_t_h);
		//var arcPath2 = Raphael.transformPath(arcPath1, "r-90,"+r_c_p_x+","+r_c_p_y);
		
		
		//	* |  
		//----------  这个位置的圆角
		//    |  
		r_c_p_y = r_c_p_y - arc_t_h;
		var arcPath3 = Raphael.transformPath(arcPath2, "r-90,"+r_c_p_x+","+r_c_p_y+"T-"+arc_t_w+",0");
		
		//    |  
		//----------  这个位置的圆角
		//  * |  
		r_c_p_x = r_c_p_x - arc_t_w;
		var arcPath4 = Raphael.transformPath(arcPath3, "r-90,"+r_c_p_x+","+r_c_p_y+"T0,"+arc_t_h);
		//先得到圆角path------------------------------------------------------
		
		//得到四个边的path----------------------------------------------------
		//      |   | 
		//----------|----   右边
		//      |   |
		var h_l_f_x = x+width;
		var h_l_f_y = y+height-r;
		var h_l_t_x = x+width;
		var h_l_t_y = y+r;
		var hLine1 = Raphael.parsePathString("M"+h_l_f_x+","+h_l_f_y+"L"+h_l_t_x+","+h_l_t_y);
		
		//  ————|———— 
		//--------------   上边
		//      |  
		var w_l_f_x = x+width-r;
		var w_l_f_y = y;
		var w_l_t_x = x+r;
		var w_l_t_y = y;
		var wLine1 = Raphael.parsePathString("M"+w_l_f_x+","+w_l_f_y+"L"+w_l_t_x+","+w_l_t_y);
		
		//  |   |    
		//--|-----------   左边
		//  |   |   
		var hLine2 = Raphael.transformPath(hLine1, "t-"+width+",0"+"R180");
		
		//      | 
		//--------------   下边
		//  ————|————
		var wLine2 = Raphael.transformPath(wLine1, "t0,"+height+"R180");
		//得到四个边的path----------------------------------------------------
		
		//return ""+arcPath1+hLine1+arcPath2+wLine1+arcPath3+hLine2+arcPath4+wLine2+"Z";
		
		//return ""+arcPath1+hLine1+arcPath2+wLine1+arcPath3+hLine2+arcPath4+wLine2+"Z";
		return ""+arcPath1+hLine1[1]+arcPath2[1]+wLine1[1]+arcPath3[1]+hLine2[1]+arcPath4[1]+wLine2[1]+"Z";
	},
	circlePath: function(x, y, r){
		var path = MWFRaphael.getCirclePath(x, y, r);
		return this.path(path);
	},
	getArcPath: function(x, y, r){
		x = x.toFloat();
		y = y.toFloat();
		r = r.toFloat();
		var x0 = x;
		var y0 = y+r;
		
		var x1;
		var y1 = y0;
		
		var x2 = x+r;
		var y2;
		
		var x3 = x+r;
		var y3 = y;
		
		//三次贝塞尔曲线公式
		//B(t)=P0(1-t)*(1-t)*(1-t)+3P1t(1-t)*(1-t)+3P2t*t(1-t)+P3t*t*t;  t属于[0,1]
		//假设t=0.5
		var b = Math.sqrt((r*r)/2);
		var Bx = x0+b;
		var By = y3+b;
		x1 = (Bx-0.125*x0-0.375*x2-0.125*x3)/0.375;
		y2 = (By-0.125*y0-0.375*y1-0.125*y3)/0.375;
		
		return "M"+x0+","+y0+"C"+x1+","+y1+" "+x2+","+y2+" "+x3+","+y3;
	},
	getCirclePath: function(x, y, r){
		var path1 = MWFRaphael.getArcPath(x, y, r);
		var path2 = Raphael.transformPath(path1, "R-90,"+x+","+y);
		var path3 = Raphael.transformPath(path2, "R-90,"+x+","+y);
		var path4 = Raphael.transformPath(path3, "R-90,"+x+","+y);
		var path = path1+path2[1]+path3[1]+path4[1]+"z";
		return path;
	},
	diamond: function(x, y, width, height, r1, r2){
		var path = MWFRaphael.getDiamondPath(x, y, width, height, r1, r2);
		return this.path(path);
	},
	getDiamondPath: function(x, y, width, height, r1, r2){
		var leftPoint = {
			"x": x,
			"y": y+height/2
		};				
		var topPoint = {
			"x": x+width/2,
			"y": y
		};				
		var rightPoint = {
			"x": x+width,
			"y": y+height/2
		};				
		var bottomPoint = {
			"x": x+width/2,
			"y": y+height
		};	

		var leftRightMinus = MWFRaphael.getMinus(width/2, height/2, r1);
		var topBottomMinus = MWFRaphael.getMinus(width/2, height/2, r2);
		
		var line1StartPoint = {
			"x": leftPoint.x+(leftRightMinus.x),
			"y": leftPoint.y+(leftRightMinus.y)
		};
		var line1Point = {
			"x": bottomPoint.x-(topBottomMinus.x),
			"y": bottomPoint.y-(topBottomMinus.y)
		};
		var line2StartPoint = {
			"x": bottomPoint.x+(topBottomMinus.x),
			"y": bottomPoint.y-(topBottomMinus.y)
		};
		var line2Point = {
			"x": rightPoint.x-(leftRightMinus.x),
			"y": rightPoint.y+(leftRightMinus.y)
		};
		var line3StartPoint = {
			"x": rightPoint.x-(leftRightMinus.x),
			"y": rightPoint.y-(leftRightMinus.y)
		};
		var line3Point = {
			"x": topPoint.x+(topBottomMinus.x),
			"y": topPoint.y+(topBottomMinus.y)
		};
		var line4StartPoint = {
			"x": topPoint.x-(topBottomMinus.x),
			"y": topPoint.y+(topBottomMinus.y)
		};
		var line4Point = {
			"x": leftPoint.x+(leftRightMinus.x),
			"y": leftPoint.y-(leftRightMinus.y)
		};
		path = "M"+line4Point.x+","+line4Point.y;
		path += "Q"+leftPoint.x+","+leftPoint.y+","+line1StartPoint.x+","+line1StartPoint.y;
		path += "L"+line1Point.x+","+line1Point.y;
		path += "Q"+bottomPoint.x+","+bottomPoint.y+","+line2StartPoint.x+","+line2StartPoint.y;
		path += "L"+line2Point.x+","+line2Point.y;
		path += "Q"+rightPoint.x+","+rightPoint.y+","+line3StartPoint.x+","+line3StartPoint.y;
		path += "L"+line3Point.x+","+line3Point.y;
		path += "Q"+topPoint.x+","+topPoint.y+","+line4StartPoint.x+","+line4StartPoint.y;
		path += "Z";
		
		return path;
	},
	getMinus: function(w,h,r){
		var c = Math.sqrt(w*w + h*h);
		var angle = h/(c/90);
		var y = (r/90)*angle;
		var x = Math.sqrt(r*r-y*y);
		return {x:x, y:y};
	},
	getPointDistance: function(a,b){
		var tmp1 = Math.abs(a.x-b.x);
		var tmp2 = Math.abs(a.y-b.y);
		
		return Math.sqrt((tmp1*tmp1)+(tmp2*tmp2));
	},
	getAngles: function(a,b,c){
		var cosA=(b*b+c*c-a*a)/(2*b*c);
		var cosB=(a*a+c*c-b*b)/(2*a*c);
		var cosC=(a*a+b*b-c*c)/(2*a*b);

		var A = Math.acos(cosA);
		var B = Math.acos(cosB);
		var C = Math.acos(cosC);

		return {A:A, B:B, C:C};
	},
	getMinDistance: function(p1, p2, p3){
		var lineA = this.getPointDistance(p1,p2);
		var lineB = this.getPointDistance(p1,p3);
		var lineC = this.getPointDistance(p2,p3);
		
		var angle = this.getAngles(lineA,lineB,lineC);
		var angleA = angle.A;
		var angleB = angle.B;
		var angleC = angle.C;
		
		var h = lineA*Math.sin(angleB);
		
		var pointLineC = Math.sqrt(lineA*lineA-h*h);
		
		var x = p3.x-p2.x;
		var y = p3.y-p2.y;
		var sinY = y*(1/lineC);
		var offy = sinY*pointLineC;
		var sinX = x*(1/lineC);
		var offx = sinX*pointLineC;
	//	var offx = Math.sqrt(pointLineC*pointLineC-offy*offy);

		return {"h": h, "p": {"x": p2.x+offx, "y": p2.y+offy}};
	},

	arrow: function(beginPoint, endPoint, l1, l2, aj){
		var path = MWFRaphael.getArrowPath(beginPoint, endPoint, l1, l2, aj);
		return this.path(path);
	},
	getArrowPath: function(beginPoint, endPoint, l1, l2, aj){
		var endX = endPoint.x;
		var endY = endPoint.y;
		var beginX = beginPoint.x;
		var beginY = beginPoint.y;
//			var l1 = l1;
//			var l2 = l2;
//			var aj = aj;
		
		var p1 = endX - beginX;
		var p2 = endY - beginY;
		if (p1==0 && p2==0){
			return "";
		}
		
		var y = (p2/Math.sqrt((p1*p1)+(p2*p2)))*l1;
		var x = (p1/Math.sqrt((p1*p1)+(p2*p2)))*l1;
		
		var ag = (Math.asin(p2/Math.sqrt((p1*p1)+(p2*p2)))/Math.PI)*180;
		var ag1 = (Math.asin(p1/Math.sqrt((p1*p1)+(p2*p2)))/Math.PI)*180;

		if ((p1<=0) & (p2<=0)) {
			var x1 = Math.sin((270-(ag-aj))*(Math.PI/180))*l2;
			var y1 = Math.sin((ag-aj)*(Math.PI/180))*l2;
			var y2 = Math.sin((270-(ag1-aj))*(Math.PI/180))*l2;
			var x2 = Math.sin((ag1-aj)*(Math.PI/180))*l2;
		}else if((p1>0) & (p2<0)){
			var x1 = Math.sin((90-(ag+aj))*(Math.PI/180))*l2;
			var y1 = Math.sin((ag+aj)*(Math.PI/180))*l2;
			var y2 = Math.sin((ag-aj)*(Math.PI/180))*l2;
			var x2 = Math.sin((ag1-aj)*(Math.PI/180))*l2;
		}else if((p1<0) & (p2>0)){
			var x1 = Math.sin(((ag1+aj))*(Math.PI/180))*l2;
			var y1 = Math.sin((ag+aj)*(Math.PI/180))*l2;
			var y2 = Math.sin((90-(ag1-aj))*(Math.PI/180))*l2;
			var x2 = Math.sin((ag1-aj)*(Math.PI/180))*l2;
		}else{
			var x1 = Math.sin((90-(ag-aj))*(Math.PI/180))*l2;
			var y1 = Math.sin((ag-aj)*(Math.PI/180))*l2;
			var y2 = Math.sin((90-(ag1-aj))*(Math.PI/180))*l2;
			var x2 = Math.sin((ag1-aj)*(Math.PI/180))*l2;
		}
		
		x = (p1-x) + beginX;
		y = (p2-y) + beginY;
		x1 = (p1-x1) + beginX;
		y1 = (p2-y1) + beginY;
		x2 = (p1-x2) + beginX;
		y2 = (p2-y2) + beginY;
		
		return "M"+x+","+y+"L"+x1+","+y1+"L"+endX+","+endY+"L"+x2+","+y2+"Z";
	}
};


