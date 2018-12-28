o2.widget = o2.widget || {};
o2.widget.UUID = new Class({
	initialize: function(){
		this.id = this.createUUID();
	},
	valueOf: function() {  
		return this.id;  
	},
	toString: function() {  
		return this.id;  
	},
	createUUID: function(){
		//
		// Loose interpretation of the specification DCE 1.1: Remote Procedure Call
		// described at
		// http://www.opengroup.org/onlinepubs/009629399/apdxa.htm#tagtcjh_37
		// since JavaScript doesn't allow access to internal systems, the last 48
		// bits
		// of the node section is made up using a series of random numbers (6 octets
		// long).
		//  
		var dg = new Date(1582, 10, 15, 0, 0, 0, 0);
		var dc = new Date();
		var t = dc.getTime() - dg.getTime();
		var tl = this.getIntegerBits(t, 0, 31);
		var tm = this.getIntegerBits(t, 32, 47);
		var thv = this.getIntegerBits(t, 48, 59) + '1'; // version 1, security version is 2
		var csar = this.getIntegerBits(this.rand(4095), 0, 7);
		var csl = this.getIntegerBits(this.rand(4095), 0, 7);

		// since detection of anything about the machine/browser is far to buggy,
		// include some more random numbers here
		// if NIC or an IP can be obtained reliably, that should be put in
		// here instead.
		var n = this.getIntegerBits(this.rand(8191), 0, 7)
				+ this.getIntegerBits(this.rand(8191), 8, 15)
				+ this.getIntegerBits(this.rand(8191), 0, 7)
				+ this.getIntegerBits(this.rand(8191), 8, 15)
				+ this.getIntegerBits(this.rand(8191), 0, 15); // this last number is two octets long
		return tl + tm + thv + csar + csl + n;
	},
	getIntegerBits: function(val, start, end){
		var base16 = this.returnBase(val, 16);
		var quadArray = new Array();
		var quadString = '';
		var i = 0;
		for (i = 0; i < base16.length; i++) {
			quadArray.push(base16.substring(i, i + 1));
		}
		for (i = Math.floor(start / 4); i <= Math.floor(end / 4); i++) {
			if (!quadArray[i] || quadArray[i] == '')
				quadString += '0';
			else
				quadString += quadArray[i];
		}
		return quadString;
	},
	returnBase: function(number, base) {
		return (number).toString(base).toUpperCase();
	},
	rand: function(max) {
		return Math.floor(Math.random() * (max + 1));
	}
	
});