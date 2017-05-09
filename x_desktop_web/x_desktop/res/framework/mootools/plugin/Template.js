/*
---
script: template.js
license: MIT-style license.
description: Template - context aware template engine with conditional replacement, iterations and filters.
copyright: Copyright (c) 2011 Thierry Bela
authors: [Thierry Bela]

requires: 
  core:1.3: 
  - Object
  - Array
  - Elements.from
  
provides: [Template]
...
*/

!function (window, undef) {

"use strict";

	var log = (function () { return window.console && console.log ? function () { console.log.apply(console, arguments) } : function () { } })(),
		cache = {}, 
		Object = window.Object,
		Elements = window.Elements,
		append = 'append',
		filters = 'filters',
		indexOf = 'indexOf',
		join = 'join',
		modifiers = 'modifiers',
		push = 'push',
		replace = 'replace',
		shift = 'shift',
		split = 'split',
		substring = 'substring',
		Template = window.Template = function (options) { 
		
			reset(this);
			this.initialize(options); 
			return this
		},
		UID = 0,
		esc = {
		
			'&': '&amp;',
			'<': '&lt;',
			'>': '&gt;',
			'"': '&quot;',
			"'": '&apos;'
		};
		
	// from mootools
	function reset(object) {
	
		for (var key in object) {
		
			var value = object[key];
			
			if(value instanceof Array) object[key] = value.clone(); 
			else if(typeof value == 'object') {
		
				var F = function(){};
				F.prototype = value;
				object[key] = reset(new F);
			}
		}
		
		return object
	}

	Template.prototype = {

		options: {

	/*
			begin: '{',
			end: '}',
			debug: false,
			// parse: function () {},
			escape: false, // escape strings by default
			quote: false // escape quotes
	*/
		},
		filters: {},
		modifiers: {

			/* explicitely escape string */
			escape: function (context, property) {

				return property.indexOf('.') == -1 ? evaluate(context, property, true) : nestedeval(context, property.split('.'), true)
			},
			/* do not escape string */
			raw: function (context, property) {

				return property.indexOf('.') == -1 ? evaluate(context, property) : nestedeval(context, property.split('.'))
			}
		},		
		/* UID: 0, */
		initialize: function (options) { 
		
			this.options = Object[append]({}, options);
			this.UID = ++UID + '';
			cache[this.UID] = {}
		},		
		setOptions: function (options) {
		
			if(options) {
			
				Object[append](this.options, options);
				cache[this.UID] = {};
			} 
			
			return this
		},
		addFilter: function (name, fn) {
		
			if(typeof name == 'object') Object[append](this[filters], name);
			else this[filters][name] = fn;
			
			cache[this.UID] = {};
			
			return this
		},		
		addModifier: function (name, fn) {
		
			if(typeof name == 'object') Object[append](this[modifiers], name);
			else this[modifiers][name] = fn;
			
			cache[this.UID] = {};
			
			return this
		},		
		html: function (template, data) { return Elements.from(this.substitute(template, data)) },	
		compile: function (template, options) {
		
			if(options && options != this.options) this.setOptions(options);
			if(cache[this.UID][template] && this.fn) return this.fn;
			
			this.fn = compile(template, Object[append]({}, this.options, {filters: this[filters], modifiers: this[modifiers]}), this.UID);
			return this.fn
		},
		substitute: function (template, data) {
		
			if(!cache[this.UID][template]) this.compile(template);
			return this.fn(data)
		}	
	};

	function escapeHTML(string) {
	
		return ('' + string).replace(/[&<>'"]/g , function (c) { return esc[c] })
	}
	
	function compile(template, options, UID) {

		if(cache[UID][template]) return cache[UID][template];
		
		cache[UID][template] = parse(template, options, UID);
		
		return cache[UID][template]
	}
	
	function parse(template, options, UID) {
	
		var state = {}, fn, result = inline(template, options, UID, [], [], state);
			
		if(!state.iterable) {
		
			// log('Template: return ' + result[join]('+\n')[replace](/html\+=/g, ''))
			fn = new Function('data,options,evaluate,nestedeval,tmp,undef', 'return data==undef?"": ' + result[join]('+')[replace](/html\+=/g, ''));
			return function (data, html) { return html ? Elements.from(fn(data, options, evaluate, nestedeval)) : fn(data, options, evaluate, nestedeval) }
		} 
	
		//log(('var html = "";' + result[join](';') + ';return html')[replace]('html = "";html+="', 'html = "'));
		fn = new Function('data,options,compile,test,evaluate,nestedeval,log,stack,buffer,filters,tmp,undef', ('var html = "";' + result[join](';') + ';return html')[replace]('html = "";html+="', 'html = "'));
		
		return function (data, html) {
		
			return html ? Elements.from(fn(data,options,compile,test,evaluate,nestedeval,log,[],[],[])) : fn(data,options,compile,test,evaluate,nestedeval,log,[],[],[])
		}
	}
	
	function inline(template, options, UID, stack, buffer, state) {
	
		var level = stack.length,
			index,
			cIndex,
			cTagIndex,
			string = '',
			match,
			oTag,
			cTag,
			tag,
			name,
			_filters,
			substr,
			escape = options.escape,
			original = template,
			begin = options.begin || '{',
			end = options.end || '}',
			length = begin.length,
			_modifiers = options[modifiers] || {};
		
		do {
		
			_filters = [];
			index = template[indexOf](begin);
			cIndex = template[indexOf](end, index);
			
			if(index != -1 && cIndex != -1) {
			
				if(template.charAt(index - 1) == '\\') {
				
					string = template[substring](0, cIndex + length);
					
					buffer[push]('html+=' + quote(template[substring](0, index - 1) + template[substring](index, cIndex + length)));
					template = template[replace](string, '');
					continue;
				}
				
				oTag = template[substring](index, cIndex + length);
				string = template[substring](0, index);
				match = template[substring](index + length, cIndex);
				
				if(match[indexOf](':') == -1) {
				
					name = match;
					if(match[indexOf](' ') != -1) {
						
						_filters = match[split](' ');
						name = _filters[shift]();
					}
					
					match = quote(name);
					stack[append](name == '.' || name[indexOf]('.') == -1 ? [name] : name[split]('.'));
					
					buffer[push]('html+=' + quote(string));
					
					if(_modifiers[name]) buffer[push]('html+=' + '((tmp=options.modifiers[' + match + '](data' + (_filters && _filters.length > 0 ? ',' + quote(_filters) : '') + '))||tmp!=undef?tmp:"")');
					else buffer[push]('html+=' + (stack.length <= 1 ? 'evaluate' : 'nestedeval') + '(data,' + (stack.length <= 1 ? quote(stack) : '[' + quote(stack) + ']') + (escape ? ',true' : '') + ')');
					
					template = template[replace](string + oTag, '');
					stack = stack.slice(0, level)
				}
				
				else {
				
					buffer[push]('html+=' + quote(string));
					
					_filters = match[split](':');
					tag = _filters[shift]();

					if(_filters[0].charAt(0) == ' ') {
					
						name = '';
						_filters = _filters[join](' ')[split](' ');
					} 
					else {
					
						_filters = _filters[join](' ')[split](' ');
						name = _filters[shift]();
					}
					
					if(_filters.length > 0) _filters = _filters.filter(function (filter) { return filter !== '' });
					
					match = quote(name);
					
					cTag = begin + '/' + tag + ':' + name + end;
					
					cTagIndex = template[indexOf](cTag);
					
					if(cTagIndex == -1) {
					
						// log or throw an error ?
						log('token ' + oTag  + ' is not closed properly: "' + template[substring](0, cIndex + 1) + '"\n' + original);
						template = template[replace](oTag, '');
						continue;
					}
			
					state.iterable = true;
					substr = template[substring](index + oTag.length, cTagIndex);
					template = template[replace](template[substring](0, cTagIndex + cTag.length), '');
					
					var context = template[indexOf](begin) != -1;
							
					switch(tag) {
					
						case 'if':
						case 'defined':
						case 'empty':
						case 'not-empty':

							var elseif = begin + 'else:' + name + end;
							name = name[split]('.');
								
							buffer[push]('stack.push(data)',
										'data=' + (name.length <= 1 ? 'evaluate(data,' + quote(name)  + (escape ? ',true' : ',false') + ',true)' : 'nestedeval(data,[' + quote(name) + ']' + (escape ? ',true' : ',false') + ',true)'),
										'filters=[' + quote(_filters) + ']',
										'var templates=[' + quote(substr[split](elseif)) + '],t=test(' + quote(tag) + ',data),i,swap=' + (tag == 'if' ? 'typeof data == "object"' : 'false') + 
										',context=stack[stack.length-1],value=templates.length==2?(t&&swap?data:context):(!t?undef:(swap?data:context))'
									);
									
							if(_filters.length >= 1) buffer.push('for(i=0;i<filters.length;i++)value=options[' + quote(filters) + '][filters[i]](value)');
										
							buffer.push(
										'if(templates.length==2) html+=compile(templates[!t?1:0],options,' + quote(UID) + ')(value);' +
										'else if(t)html+=compile(templates[0],options,' + quote(UID) + ')(value)',
										'if(' + (!!context) + ') data=stack.pop()'
									);
							
							break;
							
						case 'loop':
						case 'repeat':
						
							if(tag == 'repeat') {
								
								name = name[split]('.');
								
								// save the context
								if(context) buffer[push]('stack.push(data)');
							
								buffer[push]('data=' + (name.length <= 1 ? 'evaluate(data,' + quote(name) + (escape ? ',true' : '') + ')' : 'nestedeval(data,[' + quote(name) + ']' + (escape ? ',true' : '') + ')'));
							}
						
							if(_filters.length > 0) buffer[push](
							
								'filters=[' + quote(_filters) + ']', 
								'for(tmp=0;tmp<filters.length;tmp++)if(options.filters[filters[tmp]]!=undef)data=options.filters[filters[tmp]](data)'
							);
							
							buffer[push]('if(typeof data == "object") {' +

								'var render = compile(' + quote(substr) + ',options, ' + quote(UID) + '),key',
								'if(data instanceof Array) for(key = 0; key < data.length; key++) html+=render(evaluate(data,key' + (escape ? ',true' : '') + '))',
								'else for(key in data) if(data.hasOwnProperty(key)) html+=render(evaluate(data,key' + (escape ? ',true' : '') + '))' +
	
							'}');
							
							if(tag == 'repeat') buffer[push]('if(' + (!!context) + ') data=stack.pop()');
							break;
							
						default:
						
							buffer[push]('tmp=options.parse!=undef?options.parse(' + quote(tag, name, substr) + ',data,options,filters) : ' + quote(substr),
										'if(options.parse==undef) log("unknown tag: ",' + quote(tag, name, substr, original) + ')',
										'html+=tmp==undef?"":tmp'
									);
							break;
					}
				}
			}
		}
		
		while(index != -1 && cIndex != -1);
		
		if(template !== '') buffer[push]('html+=' + quote(template));
		
		return buffer.filter(function (string) { return string !== '' && string != '""' && string != 'html+=""' });
	}
	
	function evaluate (object, property, escape, raw) {
 
		var value;

		if(property == '.' || property === '') value = object;
		else value = typeof object[property] == 'function' ? object[property]() : object[property];
		
		if(raw) return value;
		if(value != undef) return escape ? escapeHTML(value) : value;
		
		return raw ? object : ''
	}
	
	function nestedeval (object, paths, escape, raw) {
	
		var value = object, key, i;
		
		for(i = 0; i < paths.length; i++) {
		
			key = paths[i];
			
			if(value[key] == undef) return raw ? undef : '';
			
			value = typeof value[key] == 'function' ? value[key]() : value[key]
		}
			
		if(raw) return value;
		if(value != undef) return escape ? escapeHTML(value) : value;
		
		return raw ? object : ''
	}
		
	function test (tag, value) {

		switch(tag) {
		
			case 'if':
			case 'not-empty':
					return value != false && value != undef;
					
			case 'defined':
					return value != undef;
			case 'empty':
					return value == false || value == undef;
					
			case 'loop':
			case 'repeat':
					return typeof value == 'object';
		}
		
		return true
	}
	
	function quote() { 
	
		return Array.flatten(arguments).map(function (string) { 
			return '"' + ('' + string)[replace](/(["\\])/g, '\\$1')[replace](/\n/g, '\\n') + '"' 
		})
	}
	
}(this, null);
