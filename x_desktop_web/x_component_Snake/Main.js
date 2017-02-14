MWF.xApplication.Snake.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "Snake",
		"icon": "icon.png",
		"width": "400",
		"height": "500",
		"isResize": false,
		"isMax": false,
		"title": MWF.xApplication.Snake.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Snake.LP;
	},
	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		this.node = new Element("div", {
			"styles": this.css.contentNode
		}).inject(this.content);
	},
	loadApplication: function(callback){
		this.createNode();
		var logoNode = new  Element("div", {
			"styles": this.css.logoNode
		});
		logoNode.inject(this.node);
		
		this.beginNode = new  Element("div", {
			"styles": this.css.beginNode,
			"text": "Begin"
		}).inject(this.node);
		this.beginNode.addEvent("click", function(){
			this.beginGame();
		}.bind(this));
		
		this.addEvents({
			"uncurrent": function(){
				if (this.interval){
					window.clearInterval(this.interval);
					delete this.interval;
					this.createPauseNode();
				}
			}.bind(this)
		});
	},
	createPauseNode: function(){
		this.pauseMarkNode = new Element("div", {
			"styles": this.css.pauseMarkNode
		}).inject(this.gameNode);
		this.playNode = new Element("div", {
			"styles": this.css.playNode
		}).inject(this.pauseMarkNode);
		this.playNode.position({
			relativeTo: this.gameNode,
			position: 'center',
			edge: 'center'
		});
		this.playNode.addEvents({
			"click": function(){
				this.replay();
			}.bind(this),
			"mouseover": function(e){
				this.setStyle("background-image", "url("+"/x_component_Snake/$Main/default/play.png)");
			},
			"mouseout": function(e){
				this.setStyle("background-image", "url("+"/x_component_Snake/$Main/default/pause.png)");
			}
		});
	},
	replay: function(){
		if (this.pauseMarkNode){
			this.pauseMarkNode.destroy();
			delete this.pauseMarkNode;
		}
		if (this.playNode){
			this.playNode.destroy();
			delete this.playNode;
		}
		this.interval = window.setInterval(function(){
			this.move();
		}.bind(this), this.speed);
	},
	beginGameTimer: function(){
		var timerNode = new Element("div", {
			"styles": this.css.timerNode
		}).inject(this.gameNode);
		timerNode.position({
			relativeTo: this.gameNode,
			position: 'center',
			edge: 'center'
		});
		
		var i=3;
		timerNode.set("text", i);
		window.setTimeout(function(){
			this.setTimerNodeText(i, timerNode);
		}.bind(this), 1000);
	},
	setTimerNodeText: function(i, timerNode){
		i--;
		if (i>0){
			timerNode.set("text", i);
			window.setTimeout(function(){
				this.setTimerNodeText(i, timerNode);
			}.bind(this), 1000);
		}else{
			this.begin();
		}
	},
	beginGame: function(){
		this.node.empty();
		this.createInforNode();
		this.createGameNode();
		this.beginGameTimer();
		this.responseKeyboard();
	},
	createInforNode: function(){
		this.inforNode = new Element("div", {
			"styles": this.css.inforNode
		}).inject(this.node);
		
		this.scoreNode = new Element("div", {
			"styles": this.css.scoreNode
		}).inject(this.inforNode);
		this.scoreNode.set("text", "score: 0");
		
		this.levelNode = new Element("div", {
			"styles": this.css.levelNode
		}).inject(this.inforNode);
		this.levelNode.set("text", "level: 1");
	},
	responseKeyboard: function(){
		this.keyboardEvents = new Keyboard({
			defaultEventType: 'keyup',
			events: {
				'up': function(){this.turn("up");}.bind(this),
				'down': function(){this.turn("down");}.bind(this),
				'left': function(){this.turn("left");}.bind(this),
				'right': function(){this.turn("right");}.bind(this)
			}
		});
		this.keyboardEvents.activate();
	},
	turn: function(direction){
		this.turns.push(direction);
//		var snakeHead = this.snakePoints[0];
//		switch (direction) {
//			case "right":
//				if (snakeHead.direction=="left") return false;
//				break;
//			case "left":
//				if (snakeHead.direction=="right") return false;
//				break;
//			case "up":
//				if (snakeHead.direction=="down") return false;
//				break;
//			case "down":
//				if (snakeHead.direction=="up") return false;
//				break;
//		}
//		this.snakePoints.each(function(point, idx){
//		//	if (idx==0){
//		//		point.direction = direction;
//		//	}else{
//				point.turns.push({"position": Object.clone(snakeHead.point), "direction": direction});
//		//	}
//		}.bind(this));
	},
	createGameNode: function(){
		this.gameNode = new Element("div", {
			"styles": this.css.gameNode
		}).inject(this.node);
	},
	begin: function(){
		this.gameNode.empty();
		this.initGameParameters();
		this.drawEatPoint();
		
		this.beginMove();
	},
	
	beginMove: function(){
		this.interval = window.setInterval(function(){
			this.move();
		}.bind(this), this.speed);
	},
	move: function(){
		this.snakePoints.each(function(point){
			point.move();
		}.bind(this));
	},
	drawEatPoint: function(){
		var left = this.eatPoint.x*10;
		var top = this.eatPoint.y*10;
		this.eatPointNode = new Element("div", {
			"styles": this.css.eatPointNode
		}).inject(this.gameNode);
		this.eatPointNode.setPosition({
			"x": this.relativePosition.x+left+this.border.x,
			"y": this.relativePosition.y+top+this.border.y
		});
	},
	initGameParameters: function(){
		this.score = 0;
		this.level = 1;
		this.levelUpCount = 8;
		this.direction = "right";
		this.speed = 200;
		this.maxX = 35;
		this.maxY = 41;
		this.turns = [];
		this.relativePosition = this.gameNode.getPosition(this.gameNode.getOffsetParent());
		this.border = {"x": this.gameNode.getStyle("border-left-width").toInt(), "y": this.gameNode.getStyle("border-top-width").toInt()};
		//this.eatPoint = {"x": 1, "y": 1};
		this.snakePoints = [new MWF.xApplication.SnakePoint({"x": 8,"y": 0}, 0, "right", this),
		                    new MWF.xApplication.SnakePoint({"x": 7,"y": 0}, 1, "right", this),
		                    new MWF.xApplication.SnakePoint({"x": 6,"y": 0}, 2, "right", this),
		                    new MWF.xApplication.SnakePoint({"x": 5,"y": 0}, 3, "right", this),
		                    new MWF.xApplication.SnakePoint({"x": 4,"y": 0}, 4, "right", this),
		                    new MWF.xApplication.SnakePoint({"x": 3,"y": 0}, 5, "right", this),
		                    new MWF.xApplication.SnakePoint({"x": 2,"y": 0}, 6, "right", this),
		                    new MWF.xApplication.SnakePoint({"x": 1,"y": 0}, 7, "right", this),
		                    new MWF.xApplication.SnakePoint({"x": 0,"y": 0}, 8, "right", this)];
		this.eatPoint = this.getEatPoint();
	},
	getEatPoint: function(){
		var x = (Math.random()*this.maxX).toInt();
		var y = (Math.random()*this.maxY).toInt();
		
		var isAvailable = true;
		for (var i=0; i<this.snakePoints.length; i++){
			var point = this.snakePoints[i];
			if (x == point.point.x && y == point.point.y){
				isAvailable = false;
				break;
			};
		}
		if (isAvailable){
			return {"x": x, "y": y};
		}else{
			return this.getEatPoint();
		}
	},
	lengthen: function(){
		var index = this.snakePoints.length;
		var tail = this.snakePoints[this.snakePoints.length-1];
		var direction = tail.direction;
		var point = Object.clone(tail.point);
//		switch (tail.direction){
//			case "right":
//				point.x--;
//				if (point.x<0) point.x = this.snake.maxX;
//				break;
//			case "left":
//				point.x++;
//				if (point.x>this.snake.maxX) point.x = 0;
//				break;
//			case "up":
//				point.y++;
//				if (point.y>this.snake.maxY) point.y = 0;
//				break;
//			case "down":
//				point.y--;
//				if (point.y<0) point.x = this.snake.maxY;
//				break;
//		}
		var newPoint = new MWF.xApplication.SnakePoint(point, index, direction, this);
		tail.turns.each(function(turn){
			var newTurn = {
				"position": {
					"x": turn.position.x,
					"y": turn.position.y
				},
				"direction": turn.direction
			};
			newPoint.turns.push(newTurn);
		});
		this.snakePoints.push(newPoint);
		this.eatPointNode.destroy();
		this.eatPoint = this.getEatPoint();
		this.drawEatPoint();
		
		this.score = this.score+(100*this.level);
		
		this.scoreNode.set("text", "score: "+this.score);
		
		if (this.level<10){
			this.levelUpCount--;
			if (this.levelUpCount<=0) this.levelUp();
		}
	},
	levelUp: function(){
		this.levelUpCount = 8;
		this.level++;
		this.speed = this.speed-16;
		this.levelNode.set("text", "level: "+this.level);
		
		window.clearInterval(this.interval);
		this.interval = window.setInterval(function(){
			this.move();
		}.bind(this), this.speed);
	},
	gameOver: function(){
		window.clearInterval(this.interval);
		delete this.interval;
		this.createGameOverNode();
	},
	createGameOverNode: function(){
		this.gemeOverMarkNode = new Element("div", {
			"styles": this.css.gemeOverMarkNode
		}).inject(this.gameNode);
		this.gameOverNode = new Element("div", {
			"styles": this.css.gameOverNode,
			"text": "Game Over"
		}).inject(this.gameNode);
		this.gameOverNode.position({
			relativeTo: this.gameNode,
			position: 'center',
			edge: 'center',
			offset: {x: 0, y:-80}
		});
		this.replayNode = new  Element("div", {
			"styles": this.css.replayNode,
			"text": "Replay"
		}).inject(this.gameNode);
		
		this.replayNode.position({
			relativeTo: this.gameNode,
			position: 'center',
			edge: 'center',
			offset: {x: 0, y:40}
		});
		this.replayNode.addEvent("click", function(){
			this.beginGame();
		}.bind(this));
	}
});
MWF.xApplication.SnakePoint = new Class({
	initialize: function(point, idx, direction, snake){
		this.snake = snake;
		this.point = point;
		this.index = idx;
		this.direction = direction;
		this.turns = [];
		
		this.draw();
	},
	draw: function(){
		this.node = new Element("div", {
			"styles": this.snake.css.snakePointNode
		}).inject(this.snake.gameNode);
		
		var left = this.point.x*10;
		var top = this.point.y*10;
		
		this.node.setPosition({
			"x": this.snake.relativePosition.x+left+this.snake.border.x,
			"y": this.snake.relativePosition.y+top+this.snake.border.y
		});
	},
	checkPointDirection: function(){
		turn = this.turns[0];
		if (this.point.x==turn.position.x && this.point.y==turn.position.y){
			this.direction = turn.direction;
			this.turns.shift();
		}
	},
	checkSnakeDirection: function(){
		if (this.snake.turns.length){
			var direction = this.snake.turns.shift();
			switch (direction){
				case "right":
					if (this.direction=="right" || this.direction=="left") return false;
					break;
				case "left":
					if (this.direction=="right" || this.direction=="left") return false;
					break;
				case "up":
					if (this.direction=="up" || this.direction=="down") return false;
					break;
				case "down":
					if (this.direction=="up" || this.direction=="down") return false;
					break;
			}
			this.direction = direction;
			this.snake.snakePoints.each(function(point){
				if (point!=this){
					point.turns.push({"position": Object.clone(this.point), "direction": this.direction});
				}
			}.bind(this));
		}
	},
	move: function(){
		if (this.turns.length){
			this.checkPointDirection();
		}else{
			this.checkSnakeDirection();
		}
		switch (this.direction) {
			case "right":
				this.point.x++;
				if (this.point.x>this.snake.maxX) this.point.x = 0;
				break;
			case "left":
				this.point.x--;
				if (this.point.x<0) this.point.x = this.snake.maxX;
				break;
			case "up":
				this.point.y--;
				if (this.point.y<0) this.point.y = this.snake.maxY;
				break;
			case "down":
				this.point.y++;
				if (this.point.y>this.snake.maxY) this.point.y = 0;
				break;
		}
		if (this.checkOver()){
			this.checkLengthen();
			this.reDraw();
		}else{
			this.snake.gameOver();
		}
	},
	checkLengthen: function(){
		if (this.index==0){
			if (this.point.x == this.snake.eatPoint.x && this.point.y == this.snake.eatPoint.y){
				this.snake.lengthen();
			}
		}
	},
	checkOver: function(){
		if (this.index!=0) return true;
		for (var i=1; i<this.snake.snakePoints.length; i++){
			var point = this.snake.snakePoints[i];
			if (this.point.x == point.point.x && this.point.y == point.point.y) return false;
		}
		return true;
	},
	reDraw: function(){
		var left = this.point.x*10;
		var top = this.point.y*10;
		this.node.setPosition({
			"x": this.snake.relativePosition.x+left+this.snake.border.x,
			"y": this.snake.relativePosition.y+top+this.snake.border.y
		});
	}
//	turn: function(direction){
//		this.turns.push({
//			"times": this.index,
//			"direction": direction
//		});
//	}
});
