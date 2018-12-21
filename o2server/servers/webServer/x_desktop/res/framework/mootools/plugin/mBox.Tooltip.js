/*
---
description: The mBox.Tooltip class extends mBox.Core, helping you to attach tooltips to any DOM element.

authors: Stephan Wagner

license: MIT-style

requires:
 - mBox
 - core/1.4.5: '*'
 - more/Element.Measure

provides: [mBox.Tooltip]

documentation: http://htmltweaks.com/mBox/Documentation/Tooltip
...
*/
 
mBox.Tooltip = new Class({
	
	Extends: mBox,
	
	options: {
		
		target: null,
		
		event: 'mouseenter',
		
		position: {
			x: ['center'],
			y: ['top', 'outside']
		},
		pointer: 'center',
		
		fixed: false,
		
		delayOpenOnce: true
	},
	
	// initialize parent
	initialize: function(options) {
		this.defaultInOut = 'outside';
		this.defaultTheme = 'Tooltip';
		
		this.parent(options);
	}
});