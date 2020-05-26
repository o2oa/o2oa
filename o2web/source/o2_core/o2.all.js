
/** ***** BEGIN LICENSE BLOCK *****
 * |------------------------------------------------------------------------------|
 * | O2OA 活力办公 创意无限    o2.js                                                 |
 * |------------------------------------------------------------------------------|
 * | Distributed under the AGPL license:                                          |
 * |------------------------------------------------------------------------------|
 * | Copyright © 2018, o2oa.net, o2server.io O2 Team                              |
 * | All rights reserved.                                                         |
 * |------------------------------------------------------------------------------|
 *
 *  This file is part of O2OA.
 *
 *  O2OA is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  O2OA is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ******/

/* MooTools: the javascript framework. license: MIT-style license. copyright: Copyright (c) 2006-2016 [Valerio Proietti](http://mad4milk.net/).*/
/*!
Web Build: http://mootools.net/more/builder/444dd8f9b276db91102332672b694443
*/
/*
---

name: Core

description: The heart of MooTools.

license: MIT-style license.

copyright: Copyright (c) 2006-2015 [Valerio Proietti](http://mad4milk.net/).

authors: The MooTools production team (http://mootools.net/developers/)

inspiration:
  - Class implementation inspired by [Base.js](http://dean.edwards.name/weblog/2006/03/base/) Copyright (c) 2006 Dean Edwards, [GNU Lesser General Public License](http://opensource.org/licenses/lgpl-license.php)
  - Some functionality inspired by [Prototype.js](http://prototypejs.org) Copyright (c) 2005-2007 Sam Stephenson, [MIT License](http://opensource.org/licenses/mit-license.php)

provides: [Core, MooTools, Type, typeOf, instanceOf, Native]

...
*/
/*! MooTools: the javascript framework. license: MIT-style license. copyright: Copyright (c) 2006-2015 [Valerio Proietti](http://mad4milk.net/).*/
(function(){

    this.MooTools = {
        version: '1.6.0',
        build: '529422872adfff401b901b8b6c7ca5114ee95e2b'
    };

// typeOf, instanceOf

    var typeOf = this.typeOf = function(item){
        if (item == null) return 'null';
        if (item.$family != null) return item.$family();

        if (item.nodeName){
            if (item.nodeType == 1) return 'element';
            if (item.nodeType == 3) return (/\S/).test(item.nodeValue) ? 'textnode' : 'whitespace';
        } else if (typeof item.length == 'number'){
            if ('callee' in item) return 'arguments';
            if ('item' in item) return 'collection';
        }

        return typeof item;
    };

    var instanceOf = this.instanceOf = function(item, object){
        if (item == null) return false;
        var constructor = item.$constructor || item.constructor;
        while (constructor){
            if (constructor === object) return true;
            constructor = constructor.parent;
        }
        /*<ltIE8>*/
        if (!item.hasOwnProperty) return false;
        /*</ltIE8>*/
        return item instanceof object;
    };

    var hasOwnProperty = Object.prototype.hasOwnProperty;

    /*<ltIE8>*/
    var enumerables = true;
    for (var i in {toString: 1}) enumerables = null;
    if (enumerables) enumerables = ['hasOwnProperty', 'valueOf', 'isPrototypeOf', 'propertyIsEnumerable', 'toLocaleString', 'toString', 'constructor'];
    function forEachObjectEnumberableKey(object, fn, bind){
        if (enumerables) for (var i = enumerables.length; i--;){
            var k = enumerables[i];
            // signature has key-value, so overloadSetter can directly pass the
            // method function, without swapping arguments.
            if (hasOwnProperty.call(object, k)) fn.call(bind, k, object[k]);
        }
    }
    /*</ltIE8>*/

// Function overloading

    var Function = this.Function;

    Function.prototype.overloadSetter = function(usePlural){
        var self = this;
        return function(a, b){
            if (a == null) return this;
            if (usePlural || typeof a != 'string'){
                for (var k in a) self.call(this, k, a[k]);
                /*<ltIE8>*/
                forEachObjectEnumberableKey(a, self, this);
                /*</ltIE8>*/
            } else {
                self.call(this, a, b);
            }
            return this;
        };
    };

    Function.prototype.overloadGetter = function(usePlural){
        var self = this;
        return function(a){
            var args, result;
            if (typeof a != 'string') args = a;
            else if (arguments.length > 1) args = arguments;
            else if (usePlural) args = [a];
            if (args){
                result = {};
                for (var i = 0; i < args.length; i++) result[args[i]] = self.call(this, args[i]);
            } else {
                result = self.call(this, a);
            }
            return result;
        };
    };

    Function.prototype.extend = function(key, value){
        this[key] = value;
    }.overloadSetter();

    Function.prototype.implement = function(key, value){
        this.prototype[key] = value;
    }.overloadSetter();

// From

    var slice = Array.prototype.slice;

    Array.convert = function(item){
        if (item == null) return [];
        return (Type.isEnumerable(item) && typeof item != 'string') ? (typeOf(item) == 'array') ? item : slice.call(item) : [item];
    };

    Function.convert = function(item){
        return (typeOf(item) == 'function') ? item : function(){
            return item;
        };
    };


    Number.convert = function(item){
        var number = parseFloat(item);
        return isFinite(number) ? number : null;
    };

    String.convert = function(item){
        return item + '';
    };

    /*<1.5compat>*/
    Array.from = Array.convert;
    /*</1.5compat>*/

    Function.from = Function.convert;
    Number.from = Number.convert;
    String.from = String.convert;

// hide, protect

    Function.implement({

        hide: function(){
            this.$hidden = true;
            return this;
        },

        protect: function(){
            this.$protected = true;
            return this;
        }

    });

// Type

    var Type = this.Type = function(name, object){
        if (name){
            var lower = name.toLowerCase();
            var typeCheck = function(item){
                return (typeOf(item) == lower);
            };

            Type['is' + name] = typeCheck;
            if (object != null){
                object.prototype.$family = (function(){
                    return lower;
                }).hide();
                //<1.2compat>
                object.type = typeCheck;
                //</1.2compat>
            }
        }

        if (object == null) return null;

        object.extend(this);
        object.$constructor = Type;
        object.prototype.$constructor = object;

        return object;
    };

    var toString = Object.prototype.toString;

    Type.isEnumerable = function(item){
        return (item != null && typeof item.length == 'number' && toString.call(item) != '[object Function]' );
    };

    var hooks = {};

    var hooksOf = function(object){
        var type = typeOf(object.prototype);
        return hooks[type] || (hooks[type] = []);
    };

    var implement = function(name, method){
        if (method && method.$hidden) return;

        var hooks = hooksOf(this);

        for (var i = 0; i < hooks.length; i++){
            var hook = hooks[i];
            if (typeOf(hook) == 'type') implement.call(hook, name, method);
            else hook.call(this, name, method);
        }

        var previous = this.prototype[name];
        if (previous == null || !previous.$protected) this.prototype[name] = method;

        if (this[name] == null && typeOf(method) == 'function') extend.call(this, name, function(item){
            return method.apply(item, slice.call(arguments, 1));
        });
    };

    var extend = function(name, method){
        if (method && method.$hidden) return;
        var previous = this[name];
        if (previous == null || !previous.$protected) this[name] = method;
    };

    Type.implement({

        implement: implement.overloadSetter(),

        extend: extend.overloadSetter(),

        alias: function(name, existing){
            implement.call(this, name, this.prototype[existing]);
        }.overloadSetter(),

        mirror: function(hook){
            hooksOf(this).push(hook);
            return this;
        }

    });

    new Type('Type', Type);

// Default Types

    var force = function(name, object, methods){
        var isType = (object != Object),
            prototype = object.prototype;

        if (isType) object = new Type(name, object);

        for (var i = 0, l = methods.length; i < l; i++){
            var key = methods[i],
                generic = object[key],
                proto = prototype[key];

            if (generic) generic.protect();
            if (isType && proto) object.implement(key, proto.protect());
        }

        if (isType){
            var methodsEnumerable = prototype.propertyIsEnumerable(methods[0]);
            object.forEachMethod = function(fn){
                if (!methodsEnumerable) for (var i = 0, l = methods.length; i < l; i++){
                    fn.call(prototype, prototype[methods[i]], methods[i]);
                }
                for (var key in prototype) fn.call(prototype, prototype[key], key);
            };
        }

        return force;
    };

    force('String', String, [
        'charAt', 'charCodeAt', 'concat', 'contains', 'indexOf', 'lastIndexOf', 'match', 'quote', 'replace', 'search',
        'slice', 'split', 'substr', 'substring', 'trim', 'toLowerCase', 'toUpperCase'
    ])('Array', Array, [
        'pop', 'push', 'reverse', 'shift', 'sort', 'splice', 'unshift', 'concat', 'join', 'slice',
        'indexOf', 'lastIndexOf', 'filter', 'forEach', 'every', 'map', 'some', 'reduce', 'reduceRight', 'contains'
    ])('Number', Number, [
        'toExponential', 'toFixed', 'toLocaleString', 'toPrecision'
    ])('Function', Function, [
        'apply', 'call', 'bind'
    ])('RegExp', RegExp, [
        'exec', 'test'
    ])('Object', Object, [
        'create', 'defineProperty', 'defineProperties', 'keys',
        'getPrototypeOf', 'getOwnPropertyDescriptor', 'getOwnPropertyNames',
        'preventExtensions', 'isExtensible', 'seal', 'isSealed', 'freeze', 'isFrozen'
    ])('Date', Date, ['now']);

    Object.extend = extend.overloadSetter();

    Date.extend('now', function(){
        return +(new Date);
    });

    new Type('Boolean', Boolean);

// fixes NaN returning as Number

    Number.prototype.$family = function(){
        return isFinite(this) ? 'number' : 'null';
    }.hide();

// Number.random

    Number.extend('random', function(min, max){
        return Math.floor(Math.random() * (max - min + 1) + min);
    });

// forEach, each, keys

    Array.implement({

        /*<!ES5>*/
        forEach: function(fn, bind){
            for (var i = 0, l = this.length; i < l; i++){
                if (i in this) fn.call(bind, this[i], i, this);
            }
        },
        /*</!ES5>*/

        each: function(fn, bind){
            Array.forEach(this, fn, bind);
            return this;
        }

    });

    Object.extend({
        keys: function(object){
            var keys = [];
            for (var k in object){
                if (hasOwnProperty.call(object, k)) keys.push(k);
            }
            /*<ltIE8>*/
            forEachObjectEnumberableKey(object, function(k){
                keys.push(k);
            });
            /*</ltIE8>*/
            return keys;
        },

        forEach: function(object, fn, bind){
            if (object) Object.keys(object).forEach(function(key){
                fn.call(bind, object[key], key, object);
            });
        }

    });

    Object.each = Object.forEach;


// Array & Object cloning, Object merging and appending

    var cloneOf = function(item){
        switch (typeOf(item)){
            case 'array': return item.clone();
            case 'object': return Object.clone(item);
            default: return item;
        }
    };

    Array.implement('clone', function(){
        var i = this.length, clone = new Array(i);
        while (i--) clone[i] = cloneOf(this[i]);
        return clone;
    });

    var mergeOne = function(source, key, current){
        switch (typeOf(current)){
            case 'object':
                if (typeOf(source[key]) == 'object') Object.merge(source[key], current);
                else source[key] = Object.clone(current);
                break;
            case 'array': source[key] = current.clone(); break;
            default: source[key] = current;
        }
        return source;
    };

    Object.extend({

        merge: function(source, k, v){
            if (typeOf(k) == 'string') return mergeOne(source, k, v);
            for (var i = 1, l = arguments.length; i < l; i++){
                var object = arguments[i];
                for (var key in object) mergeOne(source, key, object[key]);
            }
            return source;
        },

        clone: function(object){
            var clone = {};
            for (var key in object) clone[key] = cloneOf(object[key]);
            return clone;
        },

        append: function(original){
            for (var i = 1, l = arguments.length; i < l; i++){
                var extended = arguments[i] || {};
                for (var key in extended) original[key] = extended[key];
            }
            return original;
        }

    });

// Object-less types

    ['Object', 'WhiteSpace', 'TextNode', 'Collection', 'Arguments'].each(function(name){
        new Type(name);
    });

// Unique ID

    var UID = Date.now();

    String.extend('uniqueID', function(){
        return (UID++).toString(36);
    });

//<1.2compat>

    var Hash = this.Hash = new Type('Hash', function(object){
        if (typeOf(object) == 'hash') object = Object.clone(object.getClean());
        for (var key in object) this[key] = object[key];
        return this;
    });

    Hash.implement({

        forEach: function(fn, bind){
            Object.forEach(this, fn, bind);
        },

        getClean: function(){
            var clean = {};
            for (var key in this){
                if (this.hasOwnProperty(key)) clean[key] = this[key];
            }
            return clean;
        },

        getLength: function(){
            var length = 0;
            for (var key in this){
                if (this.hasOwnProperty(key)) length++;
            }
            return length;
        }

    });

    Hash.alias('each', 'forEach');

    Object.type = Type.isObject;

    var Native = this.Native = function(properties){
        return new Type(properties.name, properties.initialize);
    };

    Native.type = Type.type;

    Native.implement = function(objects, methods){
        for (var i = 0; i < objects.length; i++) objects[i].implement(methods);
        return Native;
    };

    var arrayType = Array.type;
    Array.type = function(item){
        return instanceOf(item, Array) || arrayType(item);
    };

    this.$A = function(item){
        return Array.convert(item).slice();
    };

    this.$arguments = function(i){
        return function(){
            return arguments[i];
        };
    };

    this.$chk = function(obj){
        return !!(obj || obj === 0);
    };

    this.$clear = function(timer){
        clearTimeout(timer);
        clearInterval(timer);
        return null;
    };

    this.$defined = function(obj){
        return (obj != null);
    };

    this.$each = function(iterable, fn, bind){
        var type = typeOf(iterable);
        ((type == 'arguments' || type == 'collection' || type == 'array' || type == 'elements') ? Array : Object).each(iterable, fn, bind);
    };

    this.$empty = function(){};

    this.$extend = function(original, extended){
        return Object.append(original, extended);
    };

    this.$H = function(object){
        return new Hash(object);
    };

    this.$merge = function(){
        var args = Array.slice(arguments);
        args.unshift({});
        return Object.merge.apply(null, args);
    };

    this.$lambda = Function.convert;
    this.$mixin = Object.merge;
    this.$random = Number.random;
    this.$splat = Array.convert;
    this.$time = Date.now;

    this.$type = function(object){
        var type = typeOf(object);
        if (type == 'elements') return 'array';
        return (type == 'null') ? false : type;
    };

    this.$unlink = function(object){
        switch (typeOf(object)){
            case 'object': return Object.clone(object);
            case 'array': return Array.clone(object);
            case 'hash': return new Hash(object);
            default: return object;
        }
    };

//</1.2compat>

})();

/*
---

name: Array

description: Contains Array Prototypes like each, contains, and erase.

license: MIT-style license.

requires: [Type]

provides: Array

...
*/

Array.implement({

    /*<!ES5>*/
    every: function(fn, bind){
        for (var i = 0, l = this.length >>> 0; i < l; i++){
            if ((i in this) && !fn.call(bind, this[i], i, this)) return false;
        }
        return true;
    },

    filter: function(fn, bind){
        var results = [];
        for (var value, i = 0, l = this.length >>> 0; i < l; i++) if (i in this){
            value = this[i];
            if (fn.call(bind, value, i, this)) results.push(value);
        }
        return results;
    },

    indexOf: function(item, from){
        var length = this.length >>> 0;
        for (var i = (from < 0) ? Math.max(0, length + from) : from || 0; i < length; i++){
            if (this[i] === item) return i;
        }
        return -1;
    },

    map: function(fn, bind){
        var length = this.length >>> 0, results = Array(length);
        for (var i = 0; i < length; i++){
            if (i in this) results[i] = fn.call(bind, this[i], i, this);
        }
        return results;
    },

    some: function(fn, bind){
        for (var i = 0, l = this.length >>> 0; i < l; i++){
            if ((i in this) && fn.call(bind, this[i], i, this)) return true;
        }
        return false;
    },
    /*</!ES5>*/

    clean: function(){
        return this.filter(function(item){
            return item != null;
        });
    },

    invoke: function(methodName){
        var args = Array.slice(arguments, 1);
        return this.map(function(item){
            return item[methodName].apply(item, args);
        });
    },

    associate: function(keys){
        var obj = {}, length = Math.min(this.length, keys.length);
        for (var i = 0; i < length; i++) obj[keys[i]] = this[i];
        return obj;
    },

    link: function(object){
        var result = {};
        for (var i = 0, l = this.length; i < l; i++){
            for (var key in object){
                if (object[key](this[i])){
                    result[key] = this[i];
                    delete object[key];
                    break;
                }
            }
        }
        return result;
    },

    contains: function(item, from){
        return this.indexOf(item, from) != -1;
    },

    append: function(array){
        this.push.apply(this, array);
        return this;
    },

    getLast: function(){
        return (this.length) ? this[this.length - 1] : null;
    },

    getRandom: function(){
        return (this.length) ? this[Number.random(0, this.length - 1)] : null;
    },

    include: function(item){
        if (!this.contains(item)) this.push(item);
        return this;
    },

    combine: function(array){
        for (var i = 0, l = array.length; i < l; i++) this.include(array[i]);
        return this;
    },

    erase: function(item){
        for (var i = this.length; i--;){
            if (this[i] === item) this.splice(i, 1);
        }
        return this;
    },

    empty: function(){
        this.length = 0;
        return this;
    },

    flatten: function(){
        var array = [];
        for (var i = 0, l = this.length; i < l; i++){
            var type = typeOf(this[i]);
            if (type == 'null') continue;
            array = array.concat((type == 'array' || type == 'collection' || type == 'arguments' || instanceOf(this[i], Array)) ? Array.flatten(this[i]) : this[i]);
        }
        return array;
    },

    pick: function(){
        for (var i = 0, l = this.length; i < l; i++){
            if (this[i] != null) return this[i];
        }
        return null;
    },

    hexToRgb: function(array){
        if (this.length != 3) return null;
        var rgb = this.map(function(value){
            if (value.length == 1) value += value;
            return parseInt(value, 16);
        });
        return (array) ? rgb : 'rgb(' + rgb + ')';
    },

    rgbToHex: function(array){
        if (this.length < 3) return null;
        if (this.length == 4 && this[3] == 0 && !array) return 'transparent';
        var hex = [];
        for (var i = 0; i < 3; i++){
            var bit = (this[i] - 0).toString(16);
            hex.push((bit.length == 1) ? '0' + bit : bit);
        }
        return (array) ? hex : '#' + hex.join('');
    }

});

//<1.2compat>

Array.alias('extend', 'append');

var $pick = this.$pick = function(){
    return Array.convert(arguments).pick();
};

//</1.2compat>

/*
---

name: Function

description: Contains Function Prototypes like create, bind, pass, and delay.

license: MIT-style license.

requires: Type

provides: Function

...
*/

Function.extend({

    attempt: function(){
        for (var i = 0, l = arguments.length; i < l; i++){
            try {
                return arguments[i]();
            } catch (e){}
        }
        return null;
    }

});

Function.implement({

    attempt: function(args, bind){
        try {
            return this.apply(bind, Array.convert(args));
        } catch (e){}

        return null;
    },

    /*<!ES5-bind>*/
    bind: function(that){
        var self = this,
            args = arguments.length > 1 ? Array.slice(arguments, 1) : null,
            F = function(){};

        var bound = function(){
            var context = that, length = arguments.length;
            if (this instanceof bound){
                F.prototype = self.prototype;
                context = new F;
            }
            var result = (!args && !length)
                ? self.call(context)
                : self.apply(context, args && length ? args.concat(Array.slice(arguments)) : args || arguments);
            return context == that ? result : context;
        };
        return bound;
    },
    /*</!ES5-bind>*/

    pass: function(args, bind){
        var self = this;
        if (args != null) args = Array.convert(args);
        return function(){
            return self.apply(bind, args || arguments);
        };
    },

    delay: function(delay, bind, args){
        return setTimeout(this.pass((args == null ? [] : args), bind), delay);
    },

    periodical: function(periodical, bind, args){
        return setInterval(this.pass((args == null ? [] : args), bind), periodical);
    }

});

//<1.2compat>

//delete Function.prototype.bind;

Function.implement({

    create: function(options){
        var self = this;
        options = options || {};
        return function(event){
            var args = options.arguments;
            args = (args != null) ? Array.convert(args) : Array.slice(arguments, (options.event) ? 1 : 0);
            if (options.event) args.unshift(event || window.event);
            var returns = function(){
                return self.apply(options.bind || null, args);
            };
            if (options.delay) return setTimeout(returns, options.delay);
            if (options.periodical) return setInterval(returns, options.periodical);
            if (options.attempt) return Function.attempt(returns);
            return returns();
        };
    },

    // bind: function(bind, args){
    // 	var self = this;
    // 	if (args != null) args = Array.convert(args);
    // 	return function(){
    // 		return self.apply(bind, args || arguments);
    // 	};
    // },

    bindWithEvent: function(bind, args){
        var self = this;
        if (args != null) args = Array.convert(args);
        return function(event){
            return self.apply(bind, (args == null) ? arguments : [event].concat(args));
        };
    },

    run: function(args, bind){
        return this.apply(bind, Array.convert(args));
    }

});

if (Object.create == Function.prototype.create) Object.create = null;

var $try = Function.attempt;

//</1.2compat>

/*
---

name: Number

description: Contains Number Prototypes like limit, round, times, and ceil.

license: MIT-style license.

requires: Type

provides: Number

...
*/

Number.implement({

    limit: function(min, max){
        return Math.min(max, Math.max(min, this));
    },

    round: function(precision){
        precision = Math.pow(10, precision || 0).toFixed(precision < 0 ? -precision : 0);
        return Math.round(this * precision) / precision;
    },

    times: function(fn, bind){
        for (var i = 0; i < this; i++) fn.call(bind, i, this);
    },

    toFloat: function(){
        return parseFloat(this);
    },

    toInt: function(base){
        return parseInt(this, base || 10);
    }

});

Number.alias('each', 'times');

(function(math){

    var methods = {};

    math.each(function(name){
        if (!Number[name]) methods[name] = function(){
            return Math[name].apply(null, [this].concat(Array.convert(arguments)));
        };
    });

    Number.implement(methods);

})(['abs', 'acos', 'asin', 'atan', 'atan2', 'ceil', 'cos', 'exp', 'floor', 'log', 'max', 'min', 'pow', 'sin', 'sqrt', 'tan']);

/*
---

name: String

description: Contains String Prototypes like camelCase, capitalize, test, and toInt.

license: MIT-style license.

requires: [Type, Array]

provides: String

...
*/

String.implement({

    //<!ES6>
    contains: function(string, index){
        return (index ? String(this).slice(index) : String(this)).indexOf(string) > -1;
    },
    //</!ES6>

    test: function(regex, params){
        return ((typeOf(regex) == 'regexp') ? regex : new RegExp('' + regex, params)).test(this);
    },

    trim: function(){
        return String(this).replace(/^\s+|\s+$/g, '');
    },

    clean: function(){
        return String(this).replace(/\s+/g, ' ').trim();
    },

    camelCase: function(){
        return String(this).replace(/-\D/g, function(match){
            return match.charAt(1).toUpperCase();
        });
    },

    hyphenate: function(){
        return String(this).replace(/[A-Z]/g, function(match){
            return ('-' + match.charAt(0).toLowerCase());
        });
    },

    capitalize: function(){
        return String(this).replace(/\b[a-z]/g, function(match){
            return match.toUpperCase();
        });
    },

    escapeRegExp: function(){
        return String(this).replace(/([-.*+?^${}()|[\]\/\\])/g, '\\$1');
    },

    toInt: function(base){
        return parseInt(this, base || 10);
    },

    toFloat: function(){
        return parseFloat(this);
    },

    hexToRgb: function(array){
        var hex = String(this).match(/^#?(\w{1,2})(\w{1,2})(\w{1,2})$/);
        return (hex) ? hex.slice(1).hexToRgb(array) : null;
    },

    rgbToHex: function(array){
        var rgb = String(this).match(/\d{1,3}/g);
        return (rgb) ? rgb.rgbToHex(array) : null;
    },

    substitute: function(object, regexp){
        return String(this).replace(regexp || (/\\?\{([^{}]+)\}/g), function(match, name){
            if (match.charAt(0) == '\\') return match.slice(1);
            return (object[name] != null) ? object[name] : '';
        });
    }

});

//<1.4compat>
String.prototype.contains = function(string, separator){
    return (separator) ? (separator + this + separator).indexOf(separator + string + separator) > -1 : String(this).indexOf(string) > -1;
};
//</1.4compat>

/*
---

name: Browser

description: The Browser Object. Contains Browser initialization, Window and Document, and the Browser Hash.

license: MIT-style license.

requires: [Array, Function, Number, String]

provides: [Browser, Window, Document]

...
*/

(function(){

    var document = this.document;
    var window = document.window = this;

    var parse = function(ua, platform){
        ua = ua.toLowerCase();
        platform = (platform ? platform.toLowerCase() : '');

        // chrome is included in the edge UA, so need to check for edge first,
        // before checking if it's chrome.
        var UA = ua.match(/(edge)[\s\/:]([\w\d\.]+)/);
        if (!UA){
            UA = ua.match(/(opera|ie|firefox|chrome|trident|crios|version)[\s\/:]([\w\d\.]+)?.*?(safari|(?:rv[\s\/:]|version[\s\/:])([\w\d\.]+)|$)/) || [null, 'unknown', 0];
        }

        if (UA[1] == 'trident'){
            UA[1] = 'ie';
            if (UA[4]) UA[2] = UA[4];
        } else if (UA[1] == 'crios'){
            UA[1] = 'chrome';
        }

        platform = ua.match(/ip(?:ad|od|hone)/) ? 'ios' : (ua.match(/(?:webos|android)/) || ua.match(/mac|win|linux/) || ['other'])[0];
        if (platform == 'win') platform = 'windows';

        return {
            extend: Function.prototype.extend,
            name: (UA[1] == 'version') ? UA[3] : UA[1],
            version: parseFloat((UA[1] == 'opera' && UA[4]) ? UA[4] : UA[2]),
            platform: platform
        };
    };

    var Browser = this.Browser = parse(navigator.userAgent, navigator.platform);

    if (Browser.name == 'ie' && document.documentMode){
        Browser.version = document.documentMode;
    }

    Browser.extend({
        Features: {
            xpath: !!(document.evaluate),
            air: !!(window.runtime),
            query: !!(document.querySelector),
            json: !!(window.JSON)
        },
        parseUA: parse
    });

//<1.4compat>
    Browser[Browser.name] = true;
    Browser[Browser.name + parseInt(Browser.version, 10)] = true;

    if (Browser.name == 'ie' && Browser.version >= '11'){
        delete Browser.ie;
    }

    var platform = Browser.platform;
    if (platform == 'windows'){
        platform = 'win';
    }
    Browser.Platform = {
        name: platform
    };
    Browser.Platform[platform] = true;
//</1.4compat>

// Request

    Browser.Request = (function(){

        var XMLHTTP = function(){
            return new XMLHttpRequest();
        };

        var MSXML2 = function(){
            return new ActiveXObject('MSXML2.XMLHTTP');
        };

        var MSXML = function(){
            return new ActiveXObject('Microsoft.XMLHTTP');
        };

        return Function.attempt(function(){
            XMLHTTP();
            return XMLHTTP;
        }, function(){
            MSXML2();
            return MSXML2;
        }, function(){
            MSXML();
            return MSXML;
        });

    })();

    Browser.Features.xhr = !!(Browser.Request);

//<1.4compat>

// Flash detection

    var version = (Function.attempt(function(){
        return navigator.plugins['Shockwave Flash'].description;
    }, function(){
        return new ActiveXObject('ShockwaveFlash.ShockwaveFlash').GetVariable('$version');
    }) || '0 r0').match(/\d+/g);

    Browser.Plugins = {
        Flash: {
            version: Number(version[0] || '0.' + version[1]) || 0,
            build: Number(version[2]) || 0
        }
    };

//</1.4compat>

// String scripts

    Browser.exec = function(text){
        if (!text) return text;
        if (window.execScript){
            window.execScript(text);
        } else {
            var script = document.createElement('script');
            script.setAttribute('type', 'text/javascript');
            script.text = text;
            document.head.appendChild(script);
            document.head.removeChild(script);
        }
        return text;
    };

    String.implement('stripScripts', function(exec){
        var scripts = '';
        var text = this.replace(/<script[^>]*>([\s\S]*?)<\/script>/gi, function(all, code){
            scripts += code + '\n';
            return '';
        });
        if (exec === true) Browser.exec(scripts);
        else if (typeOf(exec) == 'function') exec(scripts, text);
        return text;
    });

// Window, Document

    Browser.extend({
        Document: this.Document,
        Window: this.Window,
        Element: this.Element,
        Event: this.Event
    });

    this.Window = this.$constructor = new Type('Window', function(){});

    this.$family = Function.convert('window').hide();

    Window.mirror(function(name, method){
        window[name] = method;
    });

    this.Document = document.$constructor = new Type('Document', function(){});

    document.$family = Function.convert('document').hide();

    Document.mirror(function(name, method){
        document[name] = method;
    });

    document.html = document.documentElement;
    if (!document.head) document.head = document.getElementsByTagName('head')[0];

    if (document.execCommand) try {
        document.execCommand('BackgroundImageCache', false, true);
    } catch (e){}

    /*<ltIE9>*/
    if (this.attachEvent && !this.addEventListener){
        var unloadEvent = function(){
            this.detachEvent('onunload', unloadEvent);
            document.head = document.html = document.window = null;
            window = this.Window = document = null;
        };
        this.attachEvent('onunload', unloadEvent);
    }

// IE fails on collections and <select>.options (refers to <select>)
    var arrayFrom = Array.convert;
    try {
        arrayFrom(document.html.childNodes);
    } catch (e){
        Array.convert = function(item){
            if (typeof item != 'string' && Type.isEnumerable(item) && typeOf(item) != 'array'){
                var i = item.length, array = new Array(i);
                while (i--) array[i] = item[i];
                return array;
            }
            return arrayFrom(item);
        };

        var prototype = Array.prototype,
            slice = prototype.slice;
        ['pop', 'push', 'reverse', 'shift', 'sort', 'splice', 'unshift', 'concat', 'join', 'slice'].each(function(name){
            var method = prototype[name];
            Array[name] = function(item){
                return method.apply(Array.convert(item), slice.call(arguments, 1));
            };
        });
    }
    /*</ltIE9>*/

//<1.2compat>

    if (Browser.Platform.ios) Browser.Platform.ipod = true;

    Browser.Engine = {};

    var setEngine = function(name, version){
        Browser.Engine.name = name;
        Browser.Engine[name + version] = true;
        Browser.Engine.version = version;
    };

    if (Browser.ie){
        Browser.Engine.trident = true;

        switch (Browser.version){
            case 6: setEngine('trident', 4); break;
            case 7: setEngine('trident', 5); break;
            case 8: setEngine('trident', 6);
        }
    }

    if (Browser.firefox){
        Browser.Engine.gecko = true;

        if (Browser.version >= 3) setEngine('gecko', 19);
        else setEngine('gecko', 18);
    }

    if (Browser.safari || Browser.chrome){
        Browser.Engine.webkit = true;

        switch (Browser.version){
            case 2: setEngine('webkit', 419); break;
            case 3: setEngine('webkit', 420); break;
            case 4: setEngine('webkit', 525);
        }
    }

    if (Browser.opera){
        Browser.Engine.presto = true;

        if (Browser.version >= 9.6) setEngine('presto', 960);
        else if (Browser.version >= 9.5) setEngine('presto', 950);
        else setEngine('presto', 925);
    }

    if (Browser.name == 'unknown'){
        switch ((navigator.userAgent.toLowerCase().match(/(?:webkit|khtml|gecko)/) || [])[0]){
            case 'webkit':
            case 'khtml':
                Browser.Engine.webkit = true;
                break;
            case 'gecko':
                Browser.Engine.gecko = true;
        }
    }

    this.$exec = Browser.exec;

//</1.2compat>

})();

/*
---

name: Class

description: Contains the Class Function for easily creating, extending, and implementing reusable Classes.

license: MIT-style license.

requires: [Array, String, Function, Number]

provides: Class

...
*/

(function(){

    var Class = this.Class = new Type('Class', function(params){
        if (instanceOf(params, Function)) params = {initialize: params};

        var newClass = function(){
            reset(this);
            if (newClass.$prototyping) return this;
            this.$caller = null;
            this.$family = null;
            var value = (this.initialize) ? this.initialize.apply(this, arguments) : this;
            this.$caller = this.caller = null;
            return value;
        }.extend(this).implement(params);

        newClass.$constructor = Class;
        newClass.prototype.$constructor = newClass;
        newClass.prototype.parent = parent;

        return newClass;
    });

    var parent = function(){
        if (!this.$caller) throw new Error('The method "parent" cannot be called.');
        var name = this.$caller.$name,
            parent = this.$caller.$owner.parent,
            previous = (parent) ? parent.prototype[name] : null;
        if (!previous) throw new Error('The method "' + name + '" has no parent.');
        return previous.apply(this, arguments);
    };

    var reset = function(object){
        for (var key in object){
            var value = object[key];
            switch (typeOf(value)){
                case 'object':
                    var F = function(){};
                    F.prototype = value;
                    object[key] = reset(new F);
                    break;
                case 'array': object[key] = value.clone(); break;
            }
        }
        return object;
    };

    var wrap = function(self, key, method){
        if (method.$origin) method = method.$origin;
        var wrapper = function(){
            if (method.$protected && this.$caller == null) throw new Error('The method "' + key + '" cannot be called.');
            var caller = this.caller, current = this.$caller;
            this.caller = current; this.$caller = wrapper;
            var result = method.apply(this, arguments);
            this.$caller = current; this.caller = caller;
            return result;
        }.extend({$owner: self, $origin: method, $name: key});
        return wrapper;
    };

    var implement = function(key, value, retain){
        if (Class.Mutators.hasOwnProperty(key)){
            value = Class.Mutators[key].call(this, value);
            if (value == null) return this;
        }

        if (typeOf(value) == 'function'){
            if (value.$hidden) return this;
            this.prototype[key] = (retain) ? value : wrap(this, key, value);
        } else {
            Object.merge(this.prototype, key, value);
        }

        return this;
    };

    var getInstance = function(klass){
        klass.$prototyping = true;
        var proto = new klass;
        delete klass.$prototyping;
        return proto;
    };

    Class.implement('implement', implement.overloadSetter());

    Class.Mutators = {

        Extends: function(parent){
            this.parent = parent;
            this.prototype = getInstance(parent);
        },

        Implements: function(items){
            Array.convert(items).each(function(item){
                var instance = new item;
                for (var key in instance) implement.call(this, key, instance[key], true);
            }, this);
        }
    };

})();

/*
---

name: Class.Extras

description: Contains Utility Classes that can be implemented into your own Classes to ease the execution of many common tasks.

license: MIT-style license.

requires: Class

provides: [Class.Extras, Chain, Events, Options]

...
*/

(function(){

    this.Chain = new Class({

        $chain: [],

        chain: function(){
            this.$chain.append(Array.flatten(arguments));
            return this;
        },

        callChain: function(){
            return (this.$chain.length) ? this.$chain.shift().apply(this, arguments) : false;
        },

        clearChain: function(){
            this.$chain.empty();
            return this;
        }

    });

    var removeOn = function(string){
        return string.replace(/^on([A-Z])/, function(full, first){
            return first.toLowerCase();
        });
    };

    this.Events = new Class({

        $events: {},

        addEvent: function(type, fn, internal){
            type = removeOn(type);

            /*<1.2compat>*/
            if (fn == $empty) return this;
            /*</1.2compat>*/

            this.$events[type] = (this.$events[type] || []).include(fn);
            if (internal) fn.internal = true;
            return this;
        },

        addEvents: function(events){
            for (var type in events) this.addEvent(type, events[type]);
            return this;
        },

        fireEvent: function(type, args, delay){
            type = removeOn(type);
            var events = this.$events[type];
            if (!events) return this;
            args = Array.convert(args);
            events.each(function(fn){
                if (delay) fn.delay(delay, this, args);
                else fn.apply(this, args);
            }, this);
            return this;
        },

        removeEvent: function(type, fn){
            type = removeOn(type);
            var events = this.$events[type];
            if (events && !fn.internal){
                var index = events.indexOf(fn);
                if (index != -1) delete events[index];
            }
            return this;
        },

        removeEvents: function(events){
            var type;
            if (typeOf(events) == 'object'){
                for (type in events) this.removeEvent(type, events[type]);
                return this;
            }
            if (events) events = removeOn(events);
            for (type in this.$events){
                if (events && events != type) continue;
                var fns = this.$events[type];
                for (var i = fns.length; i--;) if (i in fns){
                    this.removeEvent(type, fns[i]);
                }
            }
            return this;
        }

    });

    this.Options = new Class({

        setOptions: function(){
            var options = this.options = Object.merge.apply(null, [{}, this.options].append(arguments));
            if (this.addEvent) for (var option in options){
                if (typeOf(options[option]) != 'function' || !(/^on[A-Z]/).test(option)) continue;
                this.addEvent(option, options[option]);
                delete options[option];
            }
            return this;
        }

    });

})();

/*
---

name: Class.Thenable

description: Contains a Utility Class that can be implemented into your own Classes to make them "thenable".

license: MIT-style license.

requires: Class

provides: [Class.Thenable]

...
*/

(function(){

    var STATE_PENDING = 0,
        STATE_FULFILLED = 1,
        STATE_REJECTED = 2;

    var Thenable = Class.Thenable = new Class({

        $thenableState: STATE_PENDING,
        $thenableResult: null,
        $thenableReactions: [],

        resolve: function(value){
            resolve(this, value);
            return this;
        },

        reject: function(reason){
            reject(this, reason);
            return this;
        },

        getThenableState: function(){
            switch (this.$thenableState){
                case STATE_PENDING:
                    return 'pending';

                case STATE_FULFILLED:
                    return 'fulfilled';

                case STATE_REJECTED:
                    return 'rejected';
            }
        },

        resetThenable: function(reason){
            reject(this, reason);
            reset(this);
            return this;
        },

        then: function(onFulfilled, onRejected){
            if (typeof onFulfilled !== 'function') onFulfilled = 'Identity';
            if (typeof onRejected !== 'function') onRejected = 'Thrower';

            var thenable = new Thenable();

            this.$thenableReactions.push({
                thenable: thenable,
                fulfillHandler: onFulfilled,
                rejectHandler: onRejected
            });

            if (this.$thenableState !== STATE_PENDING){
                react(this);
            }

            return thenable;
        },

        'catch': function(onRejected){
            return this.then(null, onRejected);
        }

    });

    Thenable.extend({
        resolve: function(value){
            var thenable;
            if (value instanceof Thenable){
                thenable = value;
            } else {
                thenable = new Thenable();
                resolve(thenable, value);
            }
            return thenable;
        },
        reject: function(reason){
            var thenable = new Thenable();
            reject(thenable, reason);
            return thenable;
        }
    });

// Private functions

    function resolve(thenable, value){
        if (thenable.$thenableState === STATE_PENDING){
            if (thenable === value){
                reject(thenable, new TypeError('Tried to resolve a thenable with itself.'));
            } else if (value && (typeof value === 'object' || typeof value === 'function')){
                var then;
                try {
                    then = value.then;
                } catch (exception){
                    reject(thenable, exception);
                }
                if (typeof then === 'function'){
                    var resolved = false;
                    defer(function(){
                        try {
                            then.call(
                                value,
                                function(nextValue){
                                    if (!resolved){
                                        resolved = true;
                                        resolve(thenable, nextValue);
                                    }
                                },
                                function(reason){
                                    if (!resolved){
                                        resolved = true;
                                        reject(thenable, reason);
                                    }
                                }
                            );
                        } catch (exception){
                            if (!resolved){
                                resolved = true;
                                reject(thenable, exception);
                            }
                        }
                    });
                } else {
                    fulfill(thenable, value);
                }
            } else {
                fulfill(thenable, value);
            }
        }
    }

    function fulfill(thenable, value){
        if (thenable.$thenableState === STATE_PENDING){
            thenable.$thenableResult = value;
            thenable.$thenableState = STATE_FULFILLED;

            react(thenable);
        }
    }

    function reject(thenable, reason){
        if (thenable.$thenableState === STATE_PENDING){
            thenable.$thenableResult = reason;
            thenable.$thenableState = STATE_REJECTED;

            react(thenable);
        }
    }

    function reset(thenable){
        if (thenable.$thenableState !== STATE_PENDING){
            thenable.$thenableResult = null;
            thenable.$thenableState = STATE_PENDING;
        }
    }

    function react(thenable){
        var state = thenable.$thenableState,
            result = thenable.$thenableResult,
            reactions = thenable.$thenableReactions,
            type;

        if (state === STATE_FULFILLED){
            thenable.$thenableReactions = [];
            type = 'fulfillHandler';
        } else if (state == STATE_REJECTED){
            thenable.$thenableReactions = [];
            type = 'rejectHandler';
        }

        if (type){
            defer(handle.pass([result, reactions, type]));
        }
    }

    function handle(result, reactions, type){
        for (var i = 0, l = reactions.length; i < l; ++i){
            var reaction = reactions[i],
                handler = reaction[type];

            if (handler === 'Identity'){
                resolve(reaction.thenable, result);
            } else if (handler === 'Thrower'){
                reject(reaction.thenable, result);
            } else {
                try {
                    resolve(reaction.thenable, handler(result));
                } catch (exception){
                    reject(reaction.thenable, exception);
                }
            }
        }
    }

    var defer;
    if (typeof process !== 'undefined' && typeof process.nextTick === 'function'){
        defer = process.nextTick;
    } else if (typeof setImmediate !== 'undefined'){
        defer = setImmediate;
    } else {
        defer = function(fn){
            setTimeout(fn, 0);
        };
    }

})();

/*
---

name: Object

description: Object generic methods

license: MIT-style license.

requires: Type

provides: [Object, Hash]

...
*/

(function(){

    Object.extend({

        subset: function(object, keys){
            var results = {};
            for (var i = 0, l = keys.length; i < l; i++){
                var k = keys[i];
                if (k in object) results[k] = object[k];
            }
            return results;
        },

        map: function(object, fn, bind){
            var results = {};
            var keys = Object.keys(object);
            for (var i = 0; i < keys.length; i++){
                var key = keys[i];
                results[key] = fn.call(bind, object[key], key, object);
            }
            return results;
        },

        filter: function(object, fn, bind){
            var results = {};
            var keys = Object.keys(object);
            for (var i = 0; i < keys.length; i++){
                var key = keys[i], value = object[key];
                if (fn.call(bind, value, key, object)) results[key] = value;
            }
            return results;
        },

        every: function(object, fn, bind){
            var keys = Object.keys(object);
            for (var i = 0; i < keys.length; i++){
                var key = keys[i];
                if (!fn.call(bind, object[key], key)) return false;
            }
            return true;
        },

        some: function(object, fn, bind){
            var keys = Object.keys(object);
            for (var i = 0; i < keys.length; i++){
                var key = keys[i];
                if (fn.call(bind, object[key], key)) return true;
            }
            return false;
        },

        values: function(object){
            var values = [];
            var keys = Object.keys(object);
            for (var i = 0; i < keys.length; i++){
                var k = keys[i];
                values.push(object[k]);
            }
            return values;
        },

        getLength: function(object){
            return Object.keys(object).length;
        },

        keyOf: function(object, value){
            var keys = Object.keys(object);
            for (var i = 0; i < keys.length; i++){
                var key = keys[i];
                if (object[key] === value) return key;
            }
            return null;
        },

        contains: function(object, value){
            return Object.keyOf(object, value) != null;
        },

        toQueryString: function(object, base){
            var queryString = [];

            Object.each(object, function(value, key){
                if (base) key = base + '[' + key + ']';
                var result;
                switch (typeOf(value)){
                    case 'object': result = Object.toQueryString(value, key); break;
                    case 'array':
                        var qs = {};
                        value.each(function(val, i){
                            qs[i] = val;
                        });
                        result = Object.toQueryString(qs, key);
                        break;
                    default: result = key + '=' + encodeURIComponent(value);
                }
                if (value != null) queryString.push(result);
            });

            return queryString.join('&');
        }

    });

})();

//<1.2compat>

Hash.implement({

    has: Object.prototype.hasOwnProperty,

    keyOf: function(value){
        return Object.keyOf(this, value);
    },

    hasValue: function(value){
        return Object.contains(this, value);
    },

    extend: function(properties){
        Hash.each(properties || {}, function(value, key){
            Hash.set(this, key, value);
        }, this);
        return this;
    },

    combine: function(properties){
        Hash.each(properties || {}, function(value, key){
            Hash.include(this, key, value);
        }, this);
        return this;
    },

    erase: function(key){
        if (this.hasOwnProperty(key)) delete this[key];
        return this;
    },

    get: function(key){
        return (this.hasOwnProperty(key)) ? this[key] : null;
    },

    set: function(key, value){
        if (!this[key] || this.hasOwnProperty(key)) this[key] = value;
        return this;
    },

    empty: function(){
        Hash.each(this, function(value, key){
            delete this[key];
        }, this);
        return this;
    },

    include: function(key, value){
        if (this[key] == null) this[key] = value;
        return this;
    },

    map: function(fn, bind){
        return new Hash(Object.map(this, fn, bind));
    },

    filter: function(fn, bind){
        return new Hash(Object.filter(this, fn, bind));
    },

    every: function(fn, bind){
        return Object.every(this, fn, bind);
    },

    some: function(fn, bind){
        return Object.some(this, fn, bind);
    },

    getKeys: function(){
        return Object.keys(this);
    },

    getValues: function(){
        return Object.values(this);
    },

    toQueryString: function(base){
        return Object.toQueryString(this, base);
    }

});

Hash.extend = Object.append;

Hash.alias({indexOf: 'keyOf', contains: 'hasValue'});

//</1.2compat>

/*
---
name: Slick.Parser
description: Standalone CSS3 Selector parser
provides: Slick.Parser
...
*/

;(function(){

    var parsed,
        separatorIndex,
        combinatorIndex,
        reversed,
        cache = {},
        reverseCache = {},
        reUnescape = /\\/g;

    var parse = function(expression, isReversed){
        if (expression == null) return null;
        if (expression.Slick === true) return expression;
        expression = ('' + expression).replace(/^\s+|\s+$/g, '');
        reversed = !!isReversed;
        var currentCache = (reversed) ? reverseCache : cache;
        if (currentCache[expression]) return currentCache[expression];
        parsed = {
            Slick: true,
            expressions: [],
            raw: expression,
            reverse: function(){
                return parse(this.raw, true);
            }
        };
        separatorIndex = -1;
        while (expression != (expression = expression.replace(regexp, parser)));
        parsed.length = parsed.expressions.length;
        return currentCache[parsed.raw] = (reversed) ? reverse(parsed) : parsed;
    };

    var reverseCombinator = function(combinator){
        if (combinator === '!') return ' ';
        else if (combinator === ' ') return '!';
        else if ((/^!/).test(combinator)) return combinator.replace(/^!/, '');
        else return '!' + combinator;
    };

    var reverse = function(expression){
        var expressions = expression.expressions;
        for (var i = 0; i < expressions.length; i++){
            var exp = expressions[i];
            var last = {parts: [], tag: '*', combinator: reverseCombinator(exp[0].combinator)};

            for (var j = 0; j < exp.length; j++){
                var cexp = exp[j];
                if (!cexp.reverseCombinator) cexp.reverseCombinator = ' ';
                cexp.combinator = cexp.reverseCombinator;
                delete cexp.reverseCombinator;
            }

            exp.reverse().push(last);
        }
        return expression;
    };

    var escapeRegExp = function(string){// Credit: XRegExp 0.6.1 (c) 2007-2008 Steven Levithan <http://stevenlevithan.com/regex/xregexp/> MIT License
        return string.replace(/[-[\]{}()*+?.\\^$|,#\s]/g, function(match){
            return '\\' + match;
        });
    };

    var regexp = new RegExp(
        /*
#!/usr/bin/env ruby
puts "\t\t" + DATA.read.gsub(/\(\?x\)|\s+#.*$|\s+|\\$|\\n/,'')
__END__
	"(?x)^(?:\
	  \\s* ( , ) \\s*               # Separator          \n\
	| \\s* ( <combinator>+ ) \\s*   # Combinator         \n\
	|      ( \\s+ )                 # CombinatorChildren \n\
	|      ( <unicode>+ | \\* )     # Tag                \n\
	| \\#  ( <unicode>+       )     # ID                 \n\
	| \\.  ( <unicode>+       )     # ClassName          \n\
	|                               # Attribute          \n\
	\\[  \
		\\s* (<unicode1>+)  (?:  \
			\\s* ([*^$!~|]?=)  (?:  \
				\\s* (?:\
					([\"']?)(.*?)\\9 \
				)\
			)  \
		)?  \\s*  \
	\\](?!\\]) \n\
	|   :+ ( <unicode>+ )(?:\
	\\( (?:\
		(?:([\"'])([^\\12]*)\\12)|((?:\\([^)]+\\)|[^()]*)+)\
	) \\)\
	)?\
	)"
*/
        "^(?:\\s*(,)\\s*|\\s*(<combinator>+)\\s*|(\\s+)|(<unicode>+|\\*)|\\#(<unicode>+)|\\.(<unicode>+)|\\[\\s*(<unicode1>+)(?:\\s*([*^$!~|]?=)(?:\\s*(?:([\"']?)(.*?)\\9)))?\\s*\\](?!\\])|(:+)(<unicode>+)(?:\\((?:(?:([\"'])([^\\13]*)\\13)|((?:\\([^)]+\\)|[^()]*)+))\\))?)"
            .replace(/<combinator>/, '[' + escapeRegExp('>+~`!@$%^&={}\\;</') + ']')
            .replace(/<unicode>/g, '(?:[\\w\\u00a1-\\uFFFF-]|\\\\[^\\s0-9a-f])')
            .replace(/<unicode1>/g, '(?:[:\\w\\u00a1-\\uFFFF-]|\\\\[^\\s0-9a-f])')
    );

    function parser(
        rawMatch,

        separator,
        combinator,
        combinatorChildren,

        tagName,
        id,
        className,

        attributeKey,
        attributeOperator,
        attributeQuote,
        attributeValue,

        pseudoMarker,
        pseudoClass,
        pseudoQuote,
        pseudoClassQuotedValue,
        pseudoClassValue
    ){
        if (separator || separatorIndex === -1){
            parsed.expressions[++separatorIndex] = [];
            combinatorIndex = -1;
            if (separator) return '';
        }

        if (combinator || combinatorChildren || combinatorIndex === -1){
            combinator = combinator || ' ';
            var currentSeparator = parsed.expressions[separatorIndex];
            if (reversed && currentSeparator[combinatorIndex])
                currentSeparator[combinatorIndex].reverseCombinator = reverseCombinator(combinator);
            currentSeparator[++combinatorIndex] = {combinator: combinator, tag: '*'};
        }

        var currentParsed = parsed.expressions[separatorIndex][combinatorIndex];

        if (tagName){
            currentParsed.tag = tagName.replace(reUnescape, '');

        } else if (id){
            currentParsed.id = id.replace(reUnescape, '');

        } else if (className){
            className = className.replace(reUnescape, '');

            if (!currentParsed.classList) currentParsed.classList = [];
            if (!currentParsed.classes) currentParsed.classes = [];
            currentParsed.classList.push(className);
            currentParsed.classes.push({
                value: className,
                regexp: new RegExp('(^|\\s)' + escapeRegExp(className) + '(\\s|$)')
            });

        } else if (pseudoClass){
            pseudoClassValue = pseudoClassValue || pseudoClassQuotedValue;
            pseudoClassValue = pseudoClassValue ? pseudoClassValue.replace(reUnescape, '') : null;

            if (!currentParsed.pseudos) currentParsed.pseudos = [];
            currentParsed.pseudos.push({
                key: pseudoClass.replace(reUnescape, ''),
                value: pseudoClassValue,
                type: pseudoMarker.length == 1 ? 'class' : 'element'
            });

        } else if (attributeKey){
            attributeKey = attributeKey.replace(reUnescape, '');
            attributeValue = (attributeValue || '').replace(reUnescape, '');

            var test, regexp;

            switch (attributeOperator){
                case '^=' : regexp = new RegExp(       '^'+ escapeRegExp(attributeValue)            ); break;
                case '$=' : regexp = new RegExp(            escapeRegExp(attributeValue) +'$'       ); break;
                case '~=' : regexp = new RegExp( '(^|\\s)'+ escapeRegExp(attributeValue) +'(\\s|$)' ); break;
                case '|=' : regexp = new RegExp(       '^'+ escapeRegExp(attributeValue) +'(-|$)'   ); break;
                case  '=' : test = function(value){
                    return attributeValue == value;
                }; break;
                case '*=' : test = function(value){
                    return value && value.indexOf(attributeValue) > -1;
                }; break;
                case '!=' : test = function(value){
                    return attributeValue != value;
                }; break;
                default   : test = function(value){
                    return !!value;
                };
            }

            if (attributeValue == '' && (/^[*$^]=$/).test(attributeOperator)) test = function(){
                return false;
            };

            if (!test) test = function(value){
                return value && regexp.test(value);
            };

            if (!currentParsed.attributes) currentParsed.attributes = [];
            currentParsed.attributes.push({
                key: attributeKey,
                operator: attributeOperator,
                value: attributeValue,
                test: test
            });

        }

        return '';
    };

// Slick NS

    var Slick = (this.Slick || {});

    Slick.parse = function(expression){
        return parse(expression);
    };

    Slick.escapeRegExp = escapeRegExp;

    if (!this.Slick) this.Slick = Slick;

}).apply(/*<CommonJS>*/(typeof exports != 'undefined') ? exports : /*</CommonJS>*/this);

/*
---
name: Slick.Finder
description: The new, superfast css selector engine.
provides: Slick.Finder
requires: Slick.Parser
...
*/

;(function(){

    var local = {},
        featuresCache = {},
        toString = Object.prototype.toString;

// Feature / Bug detection

    local.isNativeCode = function(fn){
        return (/\{\s*\[native code\]\s*\}/).test('' + fn);
    };

    local.isXML = function(document){
        return (!!document.xmlVersion) || (!!document.xml) || (toString.call(document) == '[object XMLDocument]') ||
            (document.nodeType == 9 && document.documentElement.nodeName != 'HTML');
    };

    local.setDocument = function(document){

        // convert elements / window arguments to document. if document cannot be extrapolated, the function returns.
        var nodeType = document.nodeType;
        if (nodeType == 9); // document
        else if (nodeType) document = document.ownerDocument; // node
        else if (document.navigator) document = document.document; // window
        else return;

        // check if it's the old document

        if (this.document === document) return;
        this.document = document;

        // check if we have done feature detection on this document before

        var root = document.documentElement,
            rootUid = this.getUIDXML(root),
            features = featuresCache[rootUid],
            feature;

        if (features){
            for (feature in features){
                this[feature] = features[feature];
            }
            return;
        }

        features = featuresCache[rootUid] = {};

        features.root = root;
        features.isXMLDocument = this.isXML(document);

        features.brokenStarGEBTN
            = features.starSelectsClosedQSA
            = features.idGetsName
            = features.brokenMixedCaseQSA
            = features.brokenGEBCN
            = features.brokenCheckedQSA
            = features.brokenEmptyAttributeQSA
            = features.isHTMLDocument
            = features.nativeMatchesSelector
            = false;

        var starSelectsClosed, starSelectsComments,
            brokenSecondClassNameGEBCN, cachedGetElementsByClassName,
            brokenFormAttributeGetter;

        var selected, id = 'slick_uniqueid';
        var testNode = document.createElement('div');

        var testRoot = document.body || document.getElementsByTagName('body')[0] || root;
        testRoot.appendChild(testNode);

        // on non-HTML documents innerHTML and getElementsById doesnt work properly
        try {
            testNode.innerHTML = '<a id="'+id+'"></a>';
            features.isHTMLDocument = !!document.getElementById(id);
        } catch (e){}

        if (features.isHTMLDocument){

            testNode.style.display = 'none';

            // IE returns comment nodes for getElementsByTagName('*') for some documents
            testNode.appendChild(document.createComment(''));
            starSelectsComments = (testNode.getElementsByTagName('*').length > 1);

            // IE returns closed nodes (EG:"</foo>") for getElementsByTagName('*') for some documents
            try {
                testNode.innerHTML = 'foo</foo>';
                selected = testNode.getElementsByTagName('*');
                starSelectsClosed = (selected && !!selected.length && selected[0].nodeName.charAt(0) == '/');
            } catch (e){};

            features.brokenStarGEBTN = starSelectsComments || starSelectsClosed;

            // IE returns elements with the name instead of just id for getElementsById for some documents
            try {
                testNode.innerHTML = '<a name="'+ id +'"></a><b id="'+ id +'"></b>';
                features.idGetsName = document.getElementById(id) === testNode.firstChild;
            } catch (e){}

            if (testNode.getElementsByClassName){

                // Safari 3.2 getElementsByClassName caches results
                try {
                    testNode.innerHTML = '<a class="f"></a><a class="b"></a>';
                    testNode.getElementsByClassName('b').length;
                    testNode.firstChild.className = 'b';
                    cachedGetElementsByClassName = (testNode.getElementsByClassName('b').length != 2);
                } catch (e){};

                // Opera 9.6 getElementsByClassName doesnt detects the class if its not the first one
                try {
                    testNode.innerHTML = '<a class="a"></a><a class="f b a"></a>';
                    brokenSecondClassNameGEBCN = (testNode.getElementsByClassName('a').length != 2);
                } catch (e){}

                features.brokenGEBCN = cachedGetElementsByClassName || brokenSecondClassNameGEBCN;
            }

            if (testNode.querySelectorAll){
                // IE 8 returns closed nodes (EG:"</foo>") for querySelectorAll('*') for some documents
                try {
                    testNode.innerHTML = 'foo</foo>';
                    selected = testNode.querySelectorAll('*');
                    features.starSelectsClosedQSA = (selected && !!selected.length && selected[0].nodeName.charAt(0) == '/');
                } catch (e){}

                // Safari 3.2 querySelectorAll doesnt work with mixedcase on quirksmode
                try {
                    testNode.innerHTML = '<a class="MiX"></a>';
                    features.brokenMixedCaseQSA = !testNode.querySelectorAll('.MiX').length;
                } catch (e){}

                // Webkit and Opera dont return selected options on querySelectorAll
                try {
                    testNode.innerHTML = '<select><option selected="selected">a</option></select>';
                    features.brokenCheckedQSA = (testNode.querySelectorAll(':checked').length == 0);
                } catch (e){};

                // IE returns incorrect results for attr[*^$]="" selectors on querySelectorAll
                try {
                    testNode.innerHTML = '<a class=""></a>';
                    features.brokenEmptyAttributeQSA = (testNode.querySelectorAll('[class*=""]').length != 0);
                } catch (e){}

            }

            // IE6-7, if a form has an input of id x, form.getAttribute(x) returns a reference to the input
            try {
                testNode.innerHTML = '<form action="s"><input id="action"/></form>';
                brokenFormAttributeGetter = (testNode.firstChild.getAttribute('action') != 's');
            } catch (e){}

            // native matchesSelector function

            features.nativeMatchesSelector = root.matches || /*root.msMatchesSelector ||*/ root.mozMatchesSelector || root.webkitMatchesSelector;
            if (features.nativeMatchesSelector) try {
                // if matchesSelector trows errors on incorrect sintaxes we can use it
                features.nativeMatchesSelector.call(root, ':slick');
                features.nativeMatchesSelector = null;
            } catch (e){}

        }

        try {
            root.slick_expando = 1;
            delete root.slick_expando;
            features.getUID = this.getUIDHTML;
        } catch (e){
            features.getUID = this.getUIDXML;
        }

        testRoot.removeChild(testNode);
        testNode = selected = testRoot = null;

        // getAttribute

        features.getAttribute = (features.isHTMLDocument && brokenFormAttributeGetter) ? function(node, name){
            var method = this.attributeGetters[name];
            if (method) return method.call(node);
            var attributeNode = node.getAttributeNode(name);
            return (attributeNode) ? attributeNode.nodeValue : null;
        } : function(node, name){
            var method = this.attributeGetters[name];
            return (method) ? method.call(node) : node.getAttribute(name);
        };

        // hasAttribute

        features.hasAttribute = (root && this.isNativeCode(root.hasAttribute)) ? function(node, attribute){
            return node.hasAttribute(attribute);
        } : function(node, attribute){
            node = node.getAttributeNode(attribute);
            return !!(node && (node.specified || node.nodeValue));
        };

        // contains
        // FIXME: Add specs: local.contains should be different for xml and html documents?
        var nativeRootContains = root && this.isNativeCode(root.contains),
            nativeDocumentContains = document && this.isNativeCode(document.contains);

        features.contains = (nativeRootContains && nativeDocumentContains) ? function(context, node){
            return context.contains(node);
        } : (nativeRootContains && !nativeDocumentContains) ? function(context, node){
            // IE8 does not have .contains on document.
            return context === node || ((context === document) ? document.documentElement : context).contains(node);
        } : (root && root.compareDocumentPosition) ? function(context, node){
            return context === node || !!(context.compareDocumentPosition(node) & 16);
        } : function(context, node){
            if (node) do {
                if (node === context) return true;
            } while ((node = node.parentNode));
            return false;
        };

        // document order sorting
        // credits to Sizzle (http://sizzlejs.com/)

        features.documentSorter = (root.compareDocumentPosition) ? function(a, b){
            if (!a.compareDocumentPosition || !b.compareDocumentPosition) return 0;
            return a.compareDocumentPosition(b) & 4 ? -1 : a === b ? 0 : 1;
        } : ('sourceIndex' in root) ? function(a, b){
            if (!a.sourceIndex || !b.sourceIndex) return 0;
            return a.sourceIndex - b.sourceIndex;
        } : (document.createRange) ? function(a, b){
            if (!a.ownerDocument || !b.ownerDocument) return 0;
            var aRange = a.ownerDocument.createRange(), bRange = b.ownerDocument.createRange();
            aRange.setStart(a, 0);
            aRange.setEnd(a, 0);
            bRange.setStart(b, 0);
            bRange.setEnd(b, 0);
            return aRange.compareBoundaryPoints(Range.START_TO_END, bRange);
        } : null;

        root = null;

        for (feature in features){
            this[feature] = features[feature];
        }
    };

// Main Method

    var reSimpleSelector = /^([#.]?)((?:[\w-]+|\*))$/,
        reEmptyAttribute = /\[.+[*$^]=(?:""|'')?\]/,
        qsaFailExpCache = {};

    local.search = function(context, expression, append, first){

        var found = this.found = (first) ? null : (append || []);

        if (!context) return found;
        else if (context.navigator) context = context.document; // Convert the node from a window to a document
        else if (!context.nodeType) return found;

        // setup

        var parsed, i, node, nodes,
            uniques = this.uniques = {},
            hasOthers = !!(append && append.length),
            contextIsDocument = (context.nodeType == 9);

        if (this.document !== (contextIsDocument ? context : context.ownerDocument)) this.setDocument(context);

        // avoid duplicating items already in the append array
        if (hasOthers) for (i = found.length; i--;) uniques[this.getUID(found[i])] = true;

        // expression checks

        if (typeof expression == 'string'){ // expression is a string

            /*<simple-selectors-override>*/
            var simpleSelector = expression.match(reSimpleSelector);
            simpleSelectors: if (simpleSelector){

                var symbol = simpleSelector[1],
                    name = simpleSelector[2];

                if (!symbol){

                    if (name == '*' && this.brokenStarGEBTN) break simpleSelectors;
                    nodes = context.getElementsByTagName(name);
                    if (first) return nodes[0] || null;
                    for (i = 0; node = nodes[i++];){
                        if (!(hasOthers && uniques[this.getUID(node)])) found.push(node);
                    }

                } else if (symbol == '#'){

                    if (!this.isHTMLDocument || !contextIsDocument) break simpleSelectors;
                    node = context.getElementById(name);
                    if (!node) return found;
                    if (this.idGetsName && node.getAttributeNode('id').nodeValue != name) break simpleSelectors;
                    if (first) return node || null;
                    if (!(hasOthers && uniques[this.getUID(node)])) found.push(node);

                } else if (symbol == '.'){

                    if (!this.isHTMLDocument || ((!context.getElementsByClassName || this.brokenGEBCN) && context.querySelectorAll)) break simpleSelectors;
                    if (context.getElementsByClassName && !this.brokenGEBCN){
                        nodes = context.getElementsByClassName(name);
                        if (first) return nodes[0] || null;
                        for (i = 0; node = nodes[i++];){
                            if (!(hasOthers && uniques[this.getUID(node)])) found.push(node);
                        }
                    } else {
                        var matchClass = new RegExp('(^|\\s)'+ Slick.escapeRegExp(name) +'(\\s|$)');
                        nodes = context.getElementsByTagName('*');
                        for (i = 0; node = nodes[i++];){
                            className = node.className;
                            if (!(className && matchClass.test(className))) continue;
                            if (first) return node;
                            if (!(hasOthers && uniques[this.getUID(node)])) found.push(node);
                        }
                    }

                }

                if (hasOthers) this.sort(found);
                return (first) ? null : found;

            }
            /*</simple-selectors-override>*/

            /*<query-selector-override>*/
            querySelector: if (context.querySelectorAll){

                if (!this.isHTMLDocument
                    || qsaFailExpCache[expression]
                    //TODO: only skip when expression is actually mixed case
                    || this.brokenMixedCaseQSA
                    || (this.brokenCheckedQSA && expression.indexOf(':checked') > -1)
                    || (this.brokenEmptyAttributeQSA && reEmptyAttribute.test(expression))
                    || (!contextIsDocument //Abort when !contextIsDocument and...
                        //  there are multiple expressions in the selector
                        //  since we currently only fix non-document rooted QSA for single expression selectors
                        && expression.indexOf(',') > -1
                    )
                    || Slick.disableQSA
                ) break querySelector;

                var _expression = expression, _context = context, currentId;
                if (!contextIsDocument){
                    // non-document rooted QSA
                    // credits to Andrew Dupont
                    currentId = _context.getAttribute('id'), slickid = 'slickid__';
                    _context.setAttribute('id', slickid);
                    _expression = '#' + slickid + ' ' + _expression;
                    context = _context.parentNode;
                }

                try {
                    if (first) return context.querySelector(_expression) || null;
                    else nodes = context.querySelectorAll(_expression);
                } catch (e){
                    qsaFailExpCache[expression] = 1;
                    break querySelector;
                } finally {
                    if (!contextIsDocument){
                        if (currentId) _context.setAttribute('id', currentId);
                        else _context.removeAttribute('id');
                        context = _context;
                    }
                }

                if (this.starSelectsClosedQSA) for (i = 0; node = nodes[i++];){
                    if (node.nodeName > '@' && !(hasOthers && uniques[this.getUID(node)])) found.push(node);
                } else for (i = 0; node = nodes[i++];){
                    if (!(hasOthers && uniques[this.getUID(node)])) found.push(node);
                }

                if (hasOthers) this.sort(found);
                return found;

            }
            /*</query-selector-override>*/

            parsed = this.Slick.parse(expression);
            if (!parsed.length) return found;
        } else if (expression == null){ // there is no expression
            return found;
        } else if (expression.Slick){ // expression is a parsed Slick object
            parsed = expression;
        } else if (this.contains(context.documentElement || context, expression)){ // expression is a node
            (found) ? found.push(expression) : found = expression;
            return found;
        } else { // other junk
            return found;
        }

        /*<pseudo-selectors>*//*<nth-pseudo-selectors>*/

        // cache elements for the nth selectors

        this.posNTH = {};
        this.posNTHLast = {};
        this.posNTHType = {};
        this.posNTHTypeLast = {};

        /*</nth-pseudo-selectors>*//*</pseudo-selectors>*/

        // if append is null and there is only a single selector with one expression use pushArray, else use pushUID
        this.push = (!hasOthers && (first || (parsed.length == 1 && parsed.expressions[0].length == 1))) ? this.pushArray : this.pushUID;

        if (found == null) found = [];

        // default engine

        var j, m, n;
        var combinator, tag, id, classList, classes, attributes, pseudos;
        var currentItems, currentExpression, currentBit, lastBit, expressions = parsed.expressions;

        search: for (i = 0; (currentExpression = expressions[i]); i++) for (j = 0; (currentBit = currentExpression[j]); j++){

            combinator = 'combinator:' + currentBit.combinator;
            if (!this[combinator]) continue search;

            tag        = (this.isXMLDocument) ? currentBit.tag : currentBit.tag.toUpperCase();
            id         = currentBit.id;
            classList  = currentBit.classList;
            classes    = currentBit.classes;
            attributes = currentBit.attributes;
            pseudos    = currentBit.pseudos;
            lastBit    = (j === (currentExpression.length - 1));

            this.bitUniques = {};

            if (lastBit){
                this.uniques = uniques;
                this.found = found;
            } else {
                this.uniques = {};
                this.found = [];
            }

            if (j === 0){
                this[combinator](context, tag, id, classes, attributes, pseudos, classList);
                if (first && lastBit && found.length) break search;
            } else {
                if (first && lastBit) for (m = 0, n = currentItems.length; m < n; m++){
                    this[combinator](currentItems[m], tag, id, classes, attributes, pseudos, classList);
                    if (found.length) break search;
                } else for (m = 0, n = currentItems.length; m < n; m++) this[combinator](currentItems[m], tag, id, classes, attributes, pseudos, classList);
            }

            currentItems = this.found;
        }

        // should sort if there are nodes in append and if you pass multiple expressions.
        if (hasOthers || (parsed.expressions.length > 1)) this.sort(found);

        return (first) ? (found[0] || null) : found;
    };

// Utils

    local.uidx = 1;
    local.uidk = 'slick-uniqueid';

    local.getUIDXML = function(node){
        var uid = node.getAttribute(this.uidk);
        if (!uid){
            uid = this.uidx++;
            node.setAttribute(this.uidk, uid);
        }
        return uid;
    };

    local.getUIDHTML = function(node){
        return node.uniqueNumber || (node.uniqueNumber = this.uidx++);
    };

// sort based on the setDocument documentSorter method.

    local.sort = function(results){
        if (!this.documentSorter) return results;
        results.sort(this.documentSorter);
        return results;
    };

    /*<pseudo-selectors>*//*<nth-pseudo-selectors>*/

    local.cacheNTH = {};

    local.matchNTH = /^([+-]?\d*)?([a-z]+)?([+-]\d+)?$/;

    local.parseNTHArgument = function(argument){
        var parsed = argument.match(this.matchNTH);
        if (!parsed) return false;
        var special = parsed[2] || false;
        var a = parsed[1] || 1;
        if (a == '-') a = -1;
        var b = +parsed[3] || 0;
        parsed =
            (special == 'n')	? {a: a, b: b} :
                (special == 'odd')	? {a: 2, b: 1} :
                    (special == 'even')	? {a: 2, b: 0} : {a: 0, b: a};

        return (this.cacheNTH[argument] = parsed);
    };

    local.createNTHPseudo = function(child, sibling, positions, ofType){
        return function(node, argument){
            var uid = this.getUID(node);
            if (!this[positions][uid]){
                var parent = node.parentNode;
                if (!parent) return false;
                var el = parent[child], count = 1;
                if (ofType){
                    var nodeName = node.nodeName;
                    do {
                        if (el.nodeName != nodeName) continue;
                        this[positions][this.getUID(el)] = count++;
                    } while ((el = el[sibling]));
                } else {
                    do {
                        if (el.nodeType != 1) continue;
                        this[positions][this.getUID(el)] = count++;
                    } while ((el = el[sibling]));
                }
            }
            argument = argument || 'n';
            var parsed = this.cacheNTH[argument] || this.parseNTHArgument(argument);
            if (!parsed) return false;
            var a = parsed.a, b = parsed.b, pos = this[positions][uid];
            if (a == 0) return b == pos;
            if (a > 0){
                if (pos < b) return false;
            } else {
                if (b < pos) return false;
            }
            return ((pos - b) % a) == 0;
        };
    };

    /*</nth-pseudo-selectors>*//*</pseudo-selectors>*/

    local.pushArray = function(node, tag, id, classes, attributes, pseudos){
        if (this.matchSelector(node, tag, id, classes, attributes, pseudos)) this.found.push(node);
    };

    local.pushUID = function(node, tag, id, classes, attributes, pseudos){
        var uid = this.getUID(node);
        if (!this.uniques[uid] && this.matchSelector(node, tag, id, classes, attributes, pseudos)){
            this.uniques[uid] = true;
            this.found.push(node);
        }
    };

    local.matchNode = function(node, selector){
        if (this.isHTMLDocument && this.nativeMatchesSelector){
            try {
                return this.nativeMatchesSelector.call(node, selector.replace(/\[([^=]+)=\s*([^'"\]]+?)\s*\]/g, '[$1="$2"]'));
            } catch (matchError){}
        }

        var parsed = this.Slick.parse(selector);
        if (!parsed) return true;

        // simple (single) selectors
        var expressions = parsed.expressions, simpleExpCounter = 0, i, currentExpression;
        for (i = 0; (currentExpression = expressions[i]); i++){
            if (currentExpression.length == 1){
                var exp = currentExpression[0];
                if (this.matchSelector(node, (this.isXMLDocument) ? exp.tag : exp.tag.toUpperCase(), exp.id, exp.classes, exp.attributes, exp.pseudos)) return true;
                simpleExpCounter++;
            }
        }

        if (simpleExpCounter == parsed.length) return false;

        var nodes = this.search(this.document, parsed), item;
        for (i = 0; item = nodes[i++];){
            if (item === node) return true;
        }
        return false;
    };

    local.matchPseudo = function(node, name, argument){
        var pseudoName = 'pseudo:' + name;
        if (this[pseudoName]) return this[pseudoName](node, argument);
        var attribute = this.getAttribute(node, name);
        return (argument) ? argument == attribute : !!attribute;
    };

    local.matchSelector = function(node, tag, id, classes, attributes, pseudos){
        if (tag){
            var nodeName = (this.isXMLDocument) ? node.nodeName : node.nodeName.toUpperCase();
            if (tag == '*'){
                if (nodeName < '@') return false; // Fix for comment nodes and closed nodes
            } else {
                if (nodeName != tag) return false;
            }
        }

        if (id && node.getAttribute('id') != id) return false;

        var i, part, cls;
        if (classes) for (i = classes.length; i--;){
            cls = this.getAttribute(node, 'class');
            if (!(cls && classes[i].regexp.test(cls))) return false;
        }
        if (attributes) for (i = attributes.length; i--;){
            part = attributes[i];
            if (part.operator ? !part.test(this.getAttribute(node, part.key)) : !this.hasAttribute(node, part.key)) return false;
        }
        if (pseudos) for (i = pseudos.length; i--;){
            part = pseudos[i];
            if (!this.matchPseudo(node, part.key, part.value)) return false;
        }
        return true;
    };

    var combinators = {

        ' ': function(node, tag, id, classes, attributes, pseudos, classList){ // all child nodes, any level

            var i, item, children;

            if (this.isHTMLDocument){
                getById: if (id){
                    item = this.document.getElementById(id);
                    if ((!item && node.all) || (this.idGetsName && item && item.getAttributeNode('id').nodeValue != id)){
                        // all[id] returns all the elements with that name or id inside node
                        // if theres just one it will return the element, else it will be a collection
                        children = node.all[id];
                        if (!children) return;
                        if (!children[0]) children = [children];
                        for (i = 0; item = children[i++];){
                            var idNode = item.getAttributeNode('id');
                            if (idNode && idNode.nodeValue == id){
                                this.push(item, tag, null, classes, attributes, pseudos);
                                break;
                            }
                        }
                        return;
                    }
                    if (!item){
                        // if the context is in the dom we return, else we will try GEBTN, breaking the getById label
                        if (this.contains(this.root, node)) return;
                        else break getById;
                    } else if (this.document !== node && !this.contains(node, item)) return;
                    this.push(item, tag, null, classes, attributes, pseudos);
                    return;
                }
                getByClass: if (classes && node.getElementsByClassName && !this.brokenGEBCN){
                    children = node.getElementsByClassName(classList.join(' '));
                    if (!(children && children.length)) break getByClass;
                    for (i = 0; item = children[i++];) this.push(item, tag, id, null, attributes, pseudos);
                    return;
                }
            }
            getByTag: {
                children = node.getElementsByTagName(tag);
                if (!(children && children.length)) break getByTag;
                if (!this.brokenStarGEBTN) tag = null;
                for (i = 0; item = children[i++];) this.push(item, tag, id, classes, attributes, pseudos);
            }
        },

        '>': function(node, tag, id, classes, attributes, pseudos){ // direct children
            if ((node = node.firstChild)) do {
                if (node.nodeType == 1) this.push(node, tag, id, classes, attributes, pseudos);
            } while ((node = node.nextSibling));
        },

        '+': function(node, tag, id, classes, attributes, pseudos){ // next sibling
            while ((node = node.nextSibling)) if (node.nodeType == 1){
                this.push(node, tag, id, classes, attributes, pseudos);
                break;
            }
        },

        '^': function(node, tag, id, classes, attributes, pseudos){ // first child
            node = node.firstChild;
            if (node){
                if (node.nodeType == 1) this.push(node, tag, id, classes, attributes, pseudos);
                else this['combinator:+'](node, tag, id, classes, attributes, pseudos);
            }
        },

        '~': function(node, tag, id, classes, attributes, pseudos){ // next siblings
            while ((node = node.nextSibling)){
                if (node.nodeType != 1) continue;
                var uid = this.getUID(node);
                if (this.bitUniques[uid]) break;
                this.bitUniques[uid] = true;
                this.push(node, tag, id, classes, attributes, pseudos);
            }
        },

        '++': function(node, tag, id, classes, attributes, pseudos){ // next sibling and previous sibling
            this['combinator:+'](node, tag, id, classes, attributes, pseudos);
            this['combinator:!+'](node, tag, id, classes, attributes, pseudos);
        },

        '~~': function(node, tag, id, classes, attributes, pseudos){ // next siblings and previous siblings
            this['combinator:~'](node, tag, id, classes, attributes, pseudos);
            this['combinator:!~'](node, tag, id, classes, attributes, pseudos);
        },

        '!': function(node, tag, id, classes, attributes, pseudos){ // all parent nodes up to document
            while ((node = node.parentNode)) if (node !== this.document) this.push(node, tag, id, classes, attributes, pseudos);
        },

        '!>': function(node, tag, id, classes, attributes, pseudos){ // direct parent (one level)
            node = node.parentNode;
            if (node !== this.document) this.push(node, tag, id, classes, attributes, pseudos);
        },

        '!+': function(node, tag, id, classes, attributes, pseudos){ // previous sibling
            while ((node = node.previousSibling)) if (node.nodeType == 1){
                this.push(node, tag, id, classes, attributes, pseudos);
                break;
            }
        },

        '!^': function(node, tag, id, classes, attributes, pseudos){ // last child
            node = node.lastChild;
            if (node){
                if (node.nodeType == 1) this.push(node, tag, id, classes, attributes, pseudos);
                else this['combinator:!+'](node, tag, id, classes, attributes, pseudos);
            }
        },

        '!~': function(node, tag, id, classes, attributes, pseudos){ // previous siblings
            while ((node = node.previousSibling)){
                if (node.nodeType != 1) continue;
                var uid = this.getUID(node);
                if (this.bitUniques[uid]) break;
                this.bitUniques[uid] = true;
                this.push(node, tag, id, classes, attributes, pseudos);
            }
        }

    };

    for (var c in combinators) local['combinator:' + c] = combinators[c];

    var pseudos = {

        /*<pseudo-selectors>*/

        'empty': function(node){
            var child = node.firstChild;
            return !(child && child.nodeType == 1) && !(node.innerText || node.textContent || '').length;
        },

        'not': function(node, expression){
            return !this.matchNode(node, expression);
        },

        'contains': function(node, text){
            return (node.innerText || node.textContent || '').indexOf(text) > -1;
        },

        'first-child': function(node){
            while ((node = node.previousSibling)) if (node.nodeType == 1) return false;
            return true;
        },

        'last-child': function(node){
            while ((node = node.nextSibling)) if (node.nodeType == 1) return false;
            return true;
        },

        'only-child': function(node){
            var prev = node;
            while ((prev = prev.previousSibling)) if (prev.nodeType == 1) return false;
            var next = node;
            while ((next = next.nextSibling)) if (next.nodeType == 1) return false;
            return true;
        },

        /*<nth-pseudo-selectors>*/

        'nth-child': local.createNTHPseudo('firstChild', 'nextSibling', 'posNTH'),

        'nth-last-child': local.createNTHPseudo('lastChild', 'previousSibling', 'posNTHLast'),

        'nth-of-type': local.createNTHPseudo('firstChild', 'nextSibling', 'posNTHType', true),

        'nth-last-of-type': local.createNTHPseudo('lastChild', 'previousSibling', 'posNTHTypeLast', true),

        'index': function(node, index){
            return this['pseudo:nth-child'](node, '' + (index + 1));
        },

        'even': function(node){
            return this['pseudo:nth-child'](node, '2n');
        },

        'odd': function(node){
            return this['pseudo:nth-child'](node, '2n+1');
        },

        /*</nth-pseudo-selectors>*/

        /*<of-type-pseudo-selectors>*/

        'first-of-type': function(node){
            var nodeName = node.nodeName;
            while ((node = node.previousSibling)) if (node.nodeName == nodeName) return false;
            return true;
        },

        'last-of-type': function(node){
            var nodeName = node.nodeName;
            while ((node = node.nextSibling)) if (node.nodeName == nodeName) return false;
            return true;
        },

        'only-of-type': function(node){
            var prev = node, nodeName = node.nodeName;
            while ((prev = prev.previousSibling)) if (prev.nodeName == nodeName) return false;
            var next = node;
            while ((next = next.nextSibling)) if (next.nodeName == nodeName) return false;
            return true;
        },

        /*</of-type-pseudo-selectors>*/

        // custom pseudos

        'enabled': function(node){
            return !node.disabled;
        },

        'disabled': function(node){
            return node.disabled;
        },

        'checked': function(node){
            return node.checked || node.selected;
        },

        'focus': function(node){
            return this.isHTMLDocument && this.document.activeElement === node && (node.href || node.type || this.hasAttribute(node, 'tabindex'));
        },

        'root': function(node){
            return (node === this.root);
        },

        'selected': function(node){
            return node.selected;
        }

        /*</pseudo-selectors>*/
    };

    for (var p in pseudos) local['pseudo:' + p] = pseudos[p];

// attributes methods

    var attributeGetters = local.attributeGetters = {

        'for': function(){
            return ('htmlFor' in this) ? this.htmlFor : this.getAttribute('for');
        },

        'href': function(){
            return ('href' in this) ? this.getAttribute('href', 2) : this.getAttribute('href');
        },

        'style': function(){
            return (this.style) ? this.style.cssText : this.getAttribute('style');
        },

        'tabindex': function(){
            var attributeNode = this.getAttributeNode('tabindex');
            return (attributeNode && attributeNode.specified) ? attributeNode.nodeValue : null;
        },

        'type': function(){
            return this.getAttribute('type');
        },

        'maxlength': function(){
            var attributeNode = this.getAttributeNode('maxLength');
            return (attributeNode && attributeNode.specified) ? attributeNode.nodeValue : null;
        }

    };

    attributeGetters.MAXLENGTH = attributeGetters.maxLength = attributeGetters.maxlength;

// Slick

    var Slick = local.Slick = (this.Slick || {});

    Slick.version = '1.1.7';

// Slick finder

    Slick.search = function(context, expression, append){
        return local.search(context, expression, append);
    };

    Slick.find = function(context, expression){
        return local.search(context, expression, null, true);
    };

// Slick containment checker

    Slick.contains = function(container, node){
        local.setDocument(container);
        return local.contains(container, node);
    };

// Slick attribute getter

    Slick.getAttribute = function(node, name){
        local.setDocument(node);
        return local.getAttribute(node, name);
    };

    Slick.hasAttribute = function(node, name){
        local.setDocument(node);
        return local.hasAttribute(node, name);
    };

// Slick matcher

    Slick.match = function(node, selector){
        if (!(node && selector)) return false;
        if (!selector || selector === node) return true;
        local.setDocument(node);
        return local.matchNode(node, selector);
    };

// Slick attribute accessor

    Slick.defineAttributeGetter = function(name, fn){
        local.attributeGetters[name] = fn;
        return this;
    };

    Slick.lookupAttributeGetter = function(name){
        return local.attributeGetters[name];
    };

// Slick pseudo accessor

    Slick.definePseudo = function(name, fn){
        local['pseudo:' + name] = function(node, argument){
            return fn.call(node, argument);
        };
        return this;
    };

    Slick.lookupPseudo = function(name){
        var pseudo = local['pseudo:' + name];
        if (pseudo) return function(argument){
            return pseudo.call(this, argument);
        };
        return null;
    };

// Slick overrides accessor

    Slick.override = function(regexp, fn){
        local.override(regexp, fn);
        return this;
    };

    Slick.isXML = local.isXML;

    Slick.uidOf = function(node){
        return local.getUIDHTML(node);
    };

    if (!this.Slick) this.Slick = Slick;

}).apply(/*<CommonJS>*/(typeof exports != 'undefined') ? exports : /*</CommonJS>*/this);

/*
---

name: Element

description: One of the most important items in MooTools. Contains the dollar function, the dollars function, and an handful of cross-browser, time-saver methods to let you easily work with HTML Elements.

license: MIT-style license.

requires: [Window, Document, Array, String, Function, Object, Number, Slick.Parser, Slick.Finder]

provides: [Element, Elements, $, $$, IFrame, Selectors]

...
*/

var Element = this.Element = function(tag, props){
    var konstructor = Element.Constructors[tag];
    if (konstructor) return konstructor(props);
    if (typeof tag != 'string') return document.id(tag).set(props);

    if (!props) props = {};

    if (!(/^[\w-]+$/).test(tag)){
        var parsed = Slick.parse(tag).expressions[0][0];
        tag = (parsed.tag == '*') ? 'div' : parsed.tag;
        if (parsed.id && props.id == null) props.id = parsed.id;

        var attributes = parsed.attributes;
        if (attributes) for (var attr, i = 0, l = attributes.length; i < l; i++){
            attr = attributes[i];
            if (props[attr.key] != null) continue;

            if (attr.value != null && attr.operator == '=') props[attr.key] = attr.value;
            else if (!attr.value && !attr.operator) props[attr.key] = true;
        }

        if (parsed.classList && props['class'] == null) props['class'] = parsed.classList.join(' ');
    }

    return document.newElement(tag, props);
};


if (Browser.Element){
    Element.prototype = Browser.Element.prototype;
    // IE8 and IE9 require the wrapping.
    Element.prototype._fireEvent = (function(fireEvent){
        return function(type, event){
            return fireEvent.call(this, type, event);
        };
    })(Element.prototype.fireEvent);
}

new Type('Element', Element).mirror(function(name){
    if (Array.prototype[name]) return;

    var obj = {};
    obj[name] = function(){
        var results = [], args = arguments, elements = true;
        for (var i = 0, l = this.length; i < l; i++){
            var element = this[i], result = results[i] = element[name].apply(element, args);
            elements = (elements && typeOf(result) == 'element');
        }
        return (elements) ? new Elements(results) : results;
    };

    Elements.implement(obj);
});

if (!Browser.Element){
    Element.parent = Object;

    Element.Prototype = {
        '$constructor': Element,
        '$family': Function.convert('element').hide()
    };

    Element.mirror(function(name, method){
        Element.Prototype[name] = method;
    });
}

Element.Constructors = {};

//<1.2compat>

Element.Constructors = new Hash;

//</1.2compat>

var IFrame = new Type('IFrame', function(){
    var params = Array.link(arguments, {
        properties: Type.isObject,
        iframe: function(obj){
            return (obj != null);
        }
    });

    var props = params.properties || {}, iframe;
    if (params.iframe) iframe = document.id(params.iframe);
    var onload = props.onload || function(){};
    delete props.onload;
    props.id = props.name = [props.id, props.name, iframe ? (iframe.id || iframe.name) : 'IFrame_' + String.uniqueID()].pick();
    iframe = new Element(iframe || 'iframe', props);

    var onLoad = function(){
        onload.call(iframe.contentWindow);
    };

    if (window.frames[props.id]) onLoad();
    else iframe.addListener('load', onLoad);
    return iframe;
});

var Elements = this.Elements = function(nodes){
    if (nodes && nodes.length){
        var uniques = {}, node;
        for (var i = 0; node = nodes[i++];){
            var uid = Slick.uidOf(node);
            if (!uniques[uid]){
                uniques[uid] = true;
                this.push(node);
            }
        }
    }
};

Elements.prototype = {length: 0};
Elements.parent = Array;

new Type('Elements', Elements).implement({

    filter: function(filter, bind){
        if (!filter) return this;
        return new Elements(Array.filter(this, (typeOf(filter) == 'string') ? function(item){
            return item.match(filter);
        } : filter, bind));
    }.protect(),

    push: function(){
        var length = this.length;
        for (var i = 0, l = arguments.length; i < l; i++){
            var item = document.id(arguments[i]);
            if (item) this[length++] = item;
        }
        return (this.length = length);
    }.protect(),

    unshift: function(){
        var items = [];
        for (var i = 0, l = arguments.length; i < l; i++){
            var item = document.id(arguments[i]);
            if (item) items.push(item);
        }
        return Array.prototype.unshift.apply(this, items);
    }.protect(),

    concat: function(){
        var newElements = new Elements(this);
        for (var i = 0, l = arguments.length; i < l; i++){
            var item = arguments[i];
            if (Type.isEnumerable(item)) newElements.append(item);
            else newElements.push(item);
        }
        return newElements;
    }.protect(),

    append: function(collection){
        for (var i = 0, l = collection.length; i < l; i++) this.push(collection[i]);
        return this;
    }.protect(),

    empty: function(){
        while (this.length) delete this[--this.length];
        return this;
    }.protect()

});

//<1.2compat>

Elements.alias('extend', 'append');

//</1.2compat>

(function(){

// FF, IE
    var splice = Array.prototype.splice, object = {'0': 0, '1': 1, length: 2};

    splice.call(object, 1, 1);
    if (object[1] == 1) Elements.implement('splice', function(){
        var length = this.length;
        var result = splice.apply(this, arguments);
        while (length >= this.length) delete this[length--];
        return result;
    }.protect());

    Array.forEachMethod(function(method, name){
        Elements.implement(name, method);
    });

    Array.mirror(Elements);

    /*<ltIE8>*/
    var createElementAcceptsHTML;
    try {
        createElementAcceptsHTML = (document.createElement('<input name=x>').name == 'x');
    } catch (e){}

    var escapeQuotes = function(html){
        return ('' + html).replace(/&/g, '&amp;').replace(/"/g, '&quot;');
    };
    /*</ltIE8>*/

    /*<ltIE9>*/
// #2479 - IE8 Cannot set HTML of style element
    var canChangeStyleHTML = (function(){
        var div = document.createElement('style'),
            flag = false;
        try {
            div.innerHTML = '#justTesing{margin: 0px;}';
            flag = !!div.innerHTML;
        } catch (e){}
        return flag;
    })();
    /*</ltIE9>*/

    Document.implement({

        newElement: function(tag, props){
            if (props){
                if (props.checked != null) props.defaultChecked = props.checked;
                if ((props.type == 'checkbox' || props.type == 'radio') && props.value == null) props.value = 'on';
                /*<ltIE9>*/ // IE needs the type to be set before changing content of style element
                if (!canChangeStyleHTML && tag == 'style'){
                    var styleElement = document.createElement('style');
                    styleElement.setAttribute('type', 'text/css');
                    if (props.type) delete props.type;
                    return this.id(styleElement).set(props);
                }
                /*</ltIE9>*/
                /*<ltIE8>*/// Fix for readonly name and type properties in IE < 8
                if (createElementAcceptsHTML){
                    tag = '<' + tag;
                    if (props.name) tag += ' name="' + escapeQuotes(props.name) + '"';
                    if (props.type) tag += ' type="' + escapeQuotes(props.type) + '"';
                    tag += '>';
                    delete props.name;
                    delete props.type;
                }
                /*</ltIE8>*/
            }
            return this.id(this.createElement(tag)).set(props);
        }

    });

})();

(function(){

    Slick.uidOf(window);
    Slick.uidOf(document);

    Document.implement({

        newTextNode: function(text){
            return this.createTextNode(text);
        },

        getDocument: function(){
            return this;
        },

        getWindow: function(){
            return this.window;
        },

        id: (function(){

            var types = {

                string: function(id, nocash, doc){
                    id = Slick.find(doc, '#' + id.replace(/(\W)/g, '\\$1'));
                    return (id) ? types.element(id, nocash) : null;
                },

                element: function(el, nocash){
                    Slick.uidOf(el);
                    if (!nocash && !el.$family && !(/^(?:object|embed)$/i).test(el.tagName)){
                        var fireEvent = el.fireEvent;
                        // wrapping needed in IE7, or else crash
                        el._fireEvent = function(type, event){
                            return fireEvent(type, event);
                        };
                        Object.append(el, Element.Prototype);
                    }
                    return el;
                },

                object: function(obj, nocash, doc){
                    if (obj.toElement) return types.element(obj.toElement(doc), nocash);
                    return null;
                }

            };

            types.textnode = types.whitespace = types.window = types.document = function(zero){
                return zero;
            };

            return function(el, nocash, doc){
                if (el && el.$family && el.uniqueNumber) return el;
                var type = typeOf(el);
                return (types[type]) ? types[type](el, nocash, doc || document) : null;
            };

        })()

    });

    if (window.$ == null) Window.implement('$', function(el, nc){
        return document.id(el, nc, this.document);
    });

    Window.implement({

        getDocument: function(){
            return this.document;
        },

        getWindow: function(){
            return this;
        }

    });

    [Document, Element].invoke('implement', {

        getElements: function(expression){
            return Slick.search(this, expression, new Elements);
        },

        getElement: function(expression){
            return document.id(Slick.find(this, expression));
        }

    });

    var contains = {contains: function(element){
            return Slick.contains(this, element);
        }};

    if (!document.contains) Document.implement(contains);
    if (!document.createElement('div').contains) Element.implement(contains);

//<1.2compat>

    Element.implement('hasChild', function(element){
        return this !== element && this.contains(element);
    });

    (function(search, find, match){

        this.Selectors = {};
        var pseudos = this.Selectors.Pseudo = new Hash();

        var addSlickPseudos = function(){
            for (var name in pseudos) if (pseudos.hasOwnProperty(name)){
                Slick.definePseudo(name, pseudos[name]);
                delete pseudos[name];
            }
        };

        Slick.search = function(context, expression, append){
            addSlickPseudos();
            return search.call(this, context, expression, append);
        };

        Slick.find = function(context, expression){
            addSlickPseudos();
            return find.call(this, context, expression);
        };

        Slick.match = function(node, selector){
            addSlickPseudos();
            return match.call(this, node, selector);
        };

    })(Slick.search, Slick.find, Slick.match);

//</1.2compat>

// tree walking

    var injectCombinator = function(expression, combinator){
        if (!expression) return combinator;

        expression = Object.clone(Slick.parse(expression));

        var expressions = expression.expressions;
        for (var i = expressions.length; i--;)
            expressions[i][0].combinator = combinator;

        return expression;
    };

    Object.forEach({
        getNext: '~',
        getPrevious: '!~',
        getParent: '!'
    }, function(combinator, method){
        Element.implement(method, function(expression){
            return this.getElement(injectCombinator(expression, combinator));
        });
    });

    Object.forEach({
        getAllNext: '~',
        getAllPrevious: '!~',
        getSiblings: '~~',
        getChildren: '>',
        getParents: '!'
    }, function(combinator, method){
        Element.implement(method, function(expression){
            return this.getElements(injectCombinator(expression, combinator));
        });
    });

    Element.implement({

        getFirst: function(expression){
            return document.id(Slick.search(this, injectCombinator(expression, '>'))[0]);
        },

        getLast: function(expression){
            return document.id(Slick.search(this, injectCombinator(expression, '>')).getLast());
        },

        getWindow: function(){
            return this.ownerDocument.window;
        },

        getDocument: function(){
            return this.ownerDocument;
        },

        getElementById: function(id){
            return document.id(Slick.find(this, '#' + ('' + id).replace(/(\W)/g, '\\$1')));
        },

        match: function(expression){
            return !expression || Slick.match(this, expression);
        }

    });

//<1.2compat>

    if (window.$$ == null) Window.implement('$$', function(selector){
        var elements = new Elements;
        if (arguments.length == 1 && typeof selector == 'string') return Slick.search(this.document, selector, elements);
        var args = Array.flatten(arguments);
        for (var i = 0, l = args.length; i < l; i++){
            var item = args[i];
            switch (typeOf(item)){
                case 'element': elements.push(item); break;
                case 'string': Slick.search(this.document, item, elements);
            }
        }
        return elements;
    });

//</1.2compat>

    if (window.$$ == null) Window.implement('$$', function(selector){
        if (arguments.length == 1){
            if (typeof selector == 'string') return Slick.search(this.document, selector, new Elements);
            else if (Type.isEnumerable(selector)) return new Elements(selector);
        }
        return new Elements(arguments);
    });

// Inserters

    var inserters = {

        before: function(context, element){
            var parent = element.parentNode;
            if (parent) parent.insertBefore(context, element);
        },

        after: function(context, element){
            var parent = element.parentNode;
            if (parent) parent.insertBefore(context, element.nextSibling);
        },

        bottom: function(context, element){
            element.appendChild(context);
        },

        top: function(context, element){
            element.insertBefore(context, element.firstChild);
        }

    };

    inserters.inside = inserters.bottom;

//<1.2compat>

    Object.each(inserters, function(inserter, where){

        where = where.capitalize();

        var methods = {};

        methods['inject' + where] = function(el){
            inserter(this, document.id(el, true));
            return this;
        };

        methods['grab' + where] = function(el){
            inserter(document.id(el, true), this);
            return this;
        };

        Element.implement(methods);

    });

//</1.2compat>

// getProperty / setProperty

    var propertyGetters = {}, propertySetters = {};

// properties

    var properties = {};
    Array.forEach([
        'type', 'value', 'defaultValue', 'accessKey', 'cellPadding', 'cellSpacing', 'colSpan',
        'frameBorder', 'rowSpan', 'tabIndex', 'useMap'
    ], function(property){
        properties[property.toLowerCase()] = property;
    });

    properties.html = 'innerHTML';
    properties.text = (document.createElement('div').textContent == null) ? 'innerText': 'textContent';

    Object.forEach(properties, function(real, key){
        propertySetters[key] = function(node, value){
            node[real] = value;
        };
        propertyGetters[key] = function(node){
            return node[real];
        };
    });

    /*<ltIE9>*/
    propertySetters.text = (function(){
        return function(node, value){
            if (node.get('tag') == 'style') node.set('html', value);
            else node[properties.text] = value;
        };
    })(propertySetters.text);

    propertyGetters.text = (function(getter){
        return function(node){
            return (node.get('tag') == 'style') ? node.innerHTML : getter(node);
        };
    })(propertyGetters.text);
    /*</ltIE9>*/

// Booleans

    var bools = [
        'compact', 'nowrap', 'ismap', 'declare', 'noshade', 'checked',
        'disabled', 'readOnly', 'multiple', 'selected', 'noresize',
        'defer', 'defaultChecked', 'autofocus', 'controls', 'autoplay',
        'loop'
    ];

    var booleans = {};
    Array.forEach(bools, function(bool){
        var lower = bool.toLowerCase();
        booleans[lower] = bool;
        propertySetters[lower] = function(node, value){
            node[bool] = !!value;
        };
        propertyGetters[lower] = function(node){
            return !!node[bool];
        };
    });

// Special cases

    Object.append(propertySetters, {

        'class': function(node, value){
            ('className' in node) ? node.className = (value || '') : node.setAttribute('class', value);
        },

        'for': function(node, value){
            ('htmlFor' in node) ? node.htmlFor = value : node.setAttribute('for', value);
        },

        'style': function(node, value){
            (node.style) ? node.style.cssText = value : node.setAttribute('style', value);
        },

        'value': function(node, value){
            node.value = (value != null) ? value : '';
        }

    });

    propertyGetters['class'] = function(node){
        return ('className' in node) ? node.className || null : node.getAttribute('class');
    };

    /* <webkit> */
    var el = document.createElement('button');
// IE sets type as readonly and throws
    try { el.type = 'button'; } catch (e){}
    if (el.type != 'button') propertySetters.type = function(node, value){
        node.setAttribute('type', value);
    };
    el = null;
    /* </webkit> */

    /*<IE>*/

    /*<ltIE9>*/
// #2479 - IE8 Cannot set HTML of style element
    var canChangeStyleHTML = (function(){
        var div = document.createElement('style'),
            flag = false;
        try {
            div.innerHTML = '#justTesing{margin: 0px;}';
            flag = !!div.innerHTML;
        } catch (e){}
        return flag;
    })();
    /*</ltIE9>*/

    var input = document.createElement('input'), volatileInputValue, html5InputSupport;

// #2178
    input.value = 't';
    input.type = 'submit';
    volatileInputValue = input.value != 't';

// #2443 - IE throws "Invalid Argument" when trying to use html5 input types
    try {
        input.value = '';
        input.type = 'email';
        html5InputSupport = input.type == 'email';
    } catch (e){}

    input = null;

    if (volatileInputValue || !html5InputSupport) propertySetters.type = function(node, type){
        try {
            var value = node.value;
            node.type = type;
            node.value = value;
        } catch (e){}
    };
    /*</IE>*/

    /* getProperty, setProperty */

    /* <ltIE9> */
    var pollutesGetAttribute = (function(div){
        div.random = 'attribute';
        return (div.getAttribute('random') == 'attribute');
    })(document.createElement('div'));

    var hasCloneBug = (function(test){
        test.innerHTML = '<object><param name="should_fix" value="the unknown" /></object>';
        return test.cloneNode(true).firstChild.childNodes.length != 1;
    })(document.createElement('div'));
    /* </ltIE9> */

    var hasClassList = !!document.createElement('div').classList;

    var classes = function(className){
        var classNames = (className || '').clean().split(' '), uniques = {};
        return classNames.filter(function(className){
            if (className !== '' && !uniques[className]) return uniques[className] = className;
        });
    };

    var addToClassList = function(name){
        this.classList.add(name);
    };

    var removeFromClassList = function(name){
        this.classList.remove(name);
    };

    Element.implement({

        setProperty: function(name, value){
            var setter = propertySetters[name.toLowerCase()];
            if (setter){
                setter(this, value);
            } else {
                /* <ltIE9> */
                var attributeWhiteList;
                if (pollutesGetAttribute) attributeWhiteList = this.retrieve('$attributeWhiteList', {});
                /* </ltIE9> */

                if (value == null){
                    this.removeAttribute(name);
                    /* <ltIE9> */
                    if (pollutesGetAttribute) delete attributeWhiteList[name];
                    /* </ltIE9> */
                } else {
                    this.setAttribute(name, '' + value);
                    /* <ltIE9> */
                    if (pollutesGetAttribute) attributeWhiteList[name] = true;
                    /* </ltIE9> */
                }
            }
            return this;
        },

        setProperties: function(attributes){
            for (var attribute in attributes) this.setProperty(attribute, attributes[attribute]);
            return this;
        },

        getProperty: function(name){
            var getter = propertyGetters[name.toLowerCase()];
            if (getter) return getter(this);
            /* <ltIE9> */
            if (pollutesGetAttribute){
                var attr = this.getAttributeNode(name), attributeWhiteList = this.retrieve('$attributeWhiteList', {});
                if (!attr) return null;
                if (attr.expando && !attributeWhiteList[name]){
                    var outer = this.outerHTML;
                    // segment by the opening tag and find mention of attribute name
                    if (outer.substr(0, outer.search(/\/?['"]?>(?![^<]*<['"])/)).indexOf(name) < 0) return null;
                    attributeWhiteList[name] = true;
                }
            }
            /* </ltIE9> */
            var result = Slick.getAttribute(this, name);
            return (!result && !Slick.hasAttribute(this, name)) ? null : result;
        },

        getProperties: function(){
            var args = Array.convert(arguments);
            return args.map(this.getProperty, this).associate(args);
        },

        removeProperty: function(name){
            return this.setProperty(name, null);
        },

        removeProperties: function(){
            Array.each(arguments, this.removeProperty, this);
            return this;
        },

        set: function(prop, value){
            var property = Element.Properties[prop];
            (property && property.set) ? property.set.call(this, value) : this.setProperty(prop, value);
        }.overloadSetter(),

        get: function(prop){
            var property = Element.Properties[prop];
            return (property && property.get) ? property.get.apply(this) : this.getProperty(prop);
        }.overloadGetter(),

        erase: function(prop){
            var property = Element.Properties[prop];
            (property && property.erase) ? property.erase.apply(this) : this.removeProperty(prop);
            return this;
        },

        hasClass: hasClassList ? function(className){
            return this.classList.contains(className);
        } : function(className){
            return classes(this.className).contains(className);
        },

        addClass: hasClassList ? function(className){
            classes(className).forEach(addToClassList, this);
            return this;
        } : function(className){
            this.className = classes(className + ' ' + this.className).join(' ');
            return this;
        },

        removeClass: hasClassList ? function(className){
            classes(className).forEach(removeFromClassList, this);
            return this;
        } : function(className){
            var classNames = classes(this.className);
            classes(className).forEach(classNames.erase, classNames);
            this.className = classNames.join(' ');
            return this;
        },

        toggleClass: function(className, force){
            if (force == null) force = !this.hasClass(className);
            return (force) ? this.addClass(className) : this.removeClass(className);
        },

        adopt: function(){
            var parent = this, fragment, elements = Array.flatten(arguments), length = elements.length;
            if (length > 1) parent = fragment = document.createDocumentFragment();

            for (var i = 0; i < length; i++){
                var element = document.id(elements[i], true);
                if (element) parent.appendChild(element);
            }

            if (fragment) this.appendChild(fragment);

            return this;
        },

        appendText: function(text, where){
            return this.grab(this.getDocument().newTextNode(text), where);
        },

        grab: function(el, where){
            inserters[where || 'bottom'](document.id(el, true), this);
            return this;
        },

        inject: function(el, where){
            inserters[where || 'bottom'](this, document.id(el, true));
            return this;
        },

        replaces: function(el){
            el = document.id(el, true);
            el.parentNode.replaceChild(this, el);
            return this;
        },

        wraps: function(el, where){
            el = document.id(el, true);
            return this.replaces(el).grab(el, where);
        },

        getSelected: function(){
            this.selectedIndex; // Safari 3.2.1
            return new Elements(Array.convert(this.options).filter(function(option){
                return option.selected;
            }));
        },

        toQueryString: function(){
            var queryString = [];
            this.getElements('input, select, textarea').each(function(el){
                var type = el.type;
                if (!el.name || el.disabled || type == 'submit' || type == 'reset' || type == 'file' || type == 'image') return;

                var value = (el.get('tag') == 'select') ? el.getSelected().map(function(opt){
                    // IE
                    return document.id(opt).get('value');
                }) : ((type == 'radio' || type == 'checkbox') && !el.checked) ? null : el.get('value');

                Array.convert(value).each(function(val){
                    if (typeof val != 'undefined') queryString.push(encodeURIComponent(el.name) + '=' + encodeURIComponent(val));
                });
            });
            return queryString.join('&');
        }

    });


// appendHTML

    var appendInserters = {
        before: 'beforeBegin',
        after: 'afterEnd',
        bottom: 'beforeEnd',
        top: 'afterBegin',
        inside: 'beforeEnd'
    };

    Element.implement('appendHTML', ('insertAdjacentHTML' in document.createElement('div')) ? function(html, where){
        this.insertAdjacentHTML(appendInserters[where || 'bottom'], html);
        return this;
    } : function(html, where){
        var temp = new Element('div', {html: html}),
            children = temp.childNodes,
            fragment = temp.firstChild;

        if (!fragment) return this;
        if (children.length > 1){
            fragment = document.createDocumentFragment();
            for (var i = 0, l = children.length; i < l; i++){
                fragment.appendChild(children[i]);
            }
        }

        inserters[where || 'bottom'](fragment, this);
        return this;
    });

    var collected = {}, storage = {};

    var get = function(uid){
        return (storage[uid] || (storage[uid] = {}));
    };

    var clean = function(item){
        var uid = item.uniqueNumber;
        if (item.removeEvents) item.removeEvents();
        if (item.clearAttributes) item.clearAttributes();
        if (uid != null){
            delete collected[uid];
            delete storage[uid];
        }
        return item;
    };

    var formProps = {input: 'checked', option: 'selected', textarea: 'value'};

    Element.implement({

        destroy: function(){
            var children = clean(this).getElementsByTagName('*');
            Array.each(children, clean);
            Element.dispose(this);
            return null;
        },

        empty: function(){
            Array.convert(this.childNodes).each(Element.dispose);
            return this;
        },

        dispose: function(){
            return (this.parentNode) ? this.parentNode.removeChild(this) : this;
        },

        clone: function(contents, keepid){
            contents = contents !== false;
            var clone = this.cloneNode(contents), ce = [clone], te = [this], i;

            if (contents){
                ce.append(Array.convert(clone.getElementsByTagName('*')));
                te.append(Array.convert(this.getElementsByTagName('*')));
            }

            for (i = ce.length; i--;){
                var node = ce[i], element = te[i];
                if (!keepid) node.removeAttribute('id');
                /*<ltIE9>*/
                if (node.clearAttributes){
                    node.clearAttributes();
                    node.mergeAttributes(element);
                    node.removeAttribute('uniqueNumber');
                    if (node.options){
                        var no = node.options, eo = element.options;
                        for (var j = no.length; j--;) no[j].selected = eo[j].selected;
                    }
                }
                /*</ltIE9>*/
                var prop = formProps[element.tagName.toLowerCase()];
                if (prop && element[prop]) node[prop] = element[prop];
            }

            /*<ltIE9>*/
            if (hasCloneBug){
                var co = clone.getElementsByTagName('object'), to = this.getElementsByTagName('object');
                for (i = co.length; i--;) co[i].outerHTML = to[i].outerHTML;
            }
            /*</ltIE9>*/
            return document.id(clone);
        }

    });

    [Element, Window, Document].invoke('implement', {

        addListener: function(type, fn){
            if (window.attachEvent && !window.addEventListener){
                collected[Slick.uidOf(this)] = this;
            }
            if (this.addEventListener) this.addEventListener(type, fn, !!arguments[2]);
            else this.attachEvent('on' + type, fn);
            return this;
        },

        removeListener: function(type, fn){
            if (this.removeEventListener) this.removeEventListener(type, fn, !!arguments[2]);
            else this.detachEvent('on' + type, fn);
            return this;
        },

        retrieve: function(property, dflt){
            var storage = get(Slick.uidOf(this)), prop = storage[property];
            if (dflt != null && prop == null) prop = storage[property] = dflt;
            return prop != null ? prop : null;
        },

        store: function(property, value){
            var storage = get(Slick.uidOf(this));
            storage[property] = value;
            return this;
        },

        eliminate: function(property){
            var storage = get(Slick.uidOf(this));
            delete storage[property];
            return this;
        }

    });

    /*<ltIE9>*/
    if (window.attachEvent && !window.addEventListener){
        var gc = function(){
            Object.each(collected, clean);
            if (window.CollectGarbage) CollectGarbage();
            window.removeListener('unload', gc);
        };
        window.addListener('unload', gc);
    }
    /*</ltIE9>*/

    Element.Properties = {};

//<1.2compat>

    Element.Properties = new Hash;

//</1.2compat>

    Element.Properties.style = {

        set: function(style){
            this.style.cssText = style;
        },

        get: function(){
            return this.style.cssText;
        },

        erase: function(){
            this.style.cssText = '';
        }

    };

    Element.Properties.tag = {

        get: function(){
            return this.tagName.toLowerCase();
        }

    };

    Element.Properties.html = {

        set: function(html){
            if (html == null) html = '';
            else if (typeOf(html) == 'array') html = html.join('');

            /*<ltIE9>*/
            if (this.styleSheet && !canChangeStyleHTML) this.styleSheet.cssText = html;
            else /*</ltIE9>*/this.innerHTML = html;
        },
        erase: function(){
            this.set('html', '');
        }

    };

    var supportsHTML5Elements = true, supportsTableInnerHTML = true, supportsTRInnerHTML = true;

    /*<ltIE9>*/
// technique by jdbarlett - http://jdbartlett.com/innershiv/
    var div = document.createElement('div');
    var fragment;
    div.innerHTML = '<nav></nav>';
    supportsHTML5Elements = (div.childNodes.length == 1);
    if (!supportsHTML5Elements){
        var tags = 'abbr article aside audio canvas datalist details figcaption figure footer header hgroup mark meter nav output progress section summary time video'.split(' ');
        fragment = document.createDocumentFragment(), l = tags.length;
        while (l--) fragment.createElement(tags[l]);
    }
    div = null;
    /*</ltIE9>*/

    /*<IE>*/
    supportsTableInnerHTML = Function.attempt(function(){
        var table = document.createElement('table');
        table.innerHTML = '<tr><td></td></tr>';
        return true;
    });

    /*<ltFF4>*/
    var tr = document.createElement('tr'), html = '<td></td>';
    tr.innerHTML = html;
    supportsTRInnerHTML = (tr.innerHTML == html);
    tr = null;
    /*</ltFF4>*/

    if (!supportsTableInnerHTML || !supportsTRInnerHTML || !supportsHTML5Elements){

        Element.Properties.html.set = (function(set){

            var translations = {
                table: [1, '<table>', '</table>'],
                select: [1, '<select>', '</select>'],
                tbody: [2, '<table><tbody>', '</tbody></table>'],
                tr: [3, '<table><tbody><tr>', '</tr></tbody></table>']
            };

            translations.thead = translations.tfoot = translations.tbody;

            return function(html){

                /*<ltIE9>*/
                if (this.styleSheet) return set.call(this, html);
                /*</ltIE9>*/
                var wrap = translations[this.get('tag')];
                if (!wrap && !supportsHTML5Elements) wrap = [0, '', ''];
                if (!wrap) return set.call(this, html);

                var level = wrap[0], wrapper = document.createElement('div'), target = wrapper;
                if (!supportsHTML5Elements) fragment.appendChild(wrapper);
                wrapper.innerHTML = [wrap[1], html, wrap[2]].flatten().join('');
                while (level--) target = target.firstChild;
                this.empty().adopt(target.childNodes);
                if (!supportsHTML5Elements) fragment.removeChild(wrapper);
                wrapper = null;
            };

        })(Element.Properties.html.set);
    }
    /*</IE>*/

    /*<ltIE9>*/
    var testForm = document.createElement('form');
    testForm.innerHTML = '<select><option>s</option></select>';

    if (testForm.firstChild.value != 's') Element.Properties.value = {

        set: function(value){
            var tag = this.get('tag');
            if (tag != 'select') return this.setProperty('value', value);
            var options = this.getElements('option');
            value = String(value);
            for (var i = 0; i < options.length; i++){
                var option = options[i],
                    attr = option.getAttributeNode('value'),
                    optionValue = (attr && attr.specified) ? option.value : option.get('text');
                if (optionValue === value) return option.selected = true;
            }
        },

        get: function(){
            var option = this, tag = option.get('tag');

            if (tag != 'select' && tag != 'option') return this.getProperty('value');

            if (tag == 'select' && !(option = option.getSelected()[0])) return '';

            var attr = option.getAttributeNode('value');
            return (attr && attr.specified) ? option.value : option.get('text');
        }

    };
    testForm = null;
    /*</ltIE9>*/

    /*<IE>*/
    if (document.createElement('div').getAttributeNode('id')) Element.Properties.id = {
        set: function(id){
            this.id = this.getAttributeNode('id').value = id;
        },
        get: function(){
            return this.id || null;
        },
        erase: function(){
            this.id = this.getAttributeNode('id').value = '';
        }
    };
    /*</IE>*/

})();

/*
---

name: Event

description: Contains the Event Type, to make the event object cross-browser.

license: MIT-style license.

requires: [Window, Document, Array, Function, String, Object]

provides: Event

...
*/

(function(){

    var _keys = {};
    var normalizeWheelSpeed = function(event){
        var normalized;
        if (event.wheelDelta){
            normalized = event.wheelDelta % 120 == 0 ? event.wheelDelta / 120 : event.wheelDelta / 12;
        } else {
            var rawAmount = event.deltaY || event.detail || 0;
            normalized = -(rawAmount % 3 == 0 ? rawAmount / 3 : rawAmount * 10);
        }
        return normalized;
    };

    var DOMEvent = this.DOMEvent = new Type('DOMEvent', function(event, win){
        if (!win) win = window;
        event = event || win.event;
        if (event.$extended) return event;
        this.event = event;
        this.$extended = true;
        this.shift = event.shiftKey;
        this.control = event.ctrlKey;
        this.alt = event.altKey;
        this.meta = event.metaKey;
        var type = this.type = event.type;
        var target = event.target || event.srcElement;
        while (target && target.nodeType == 3) target = target.parentNode;
        this.target = document.id(target);

        if (type.indexOf('key') == 0){
            var code = this.code = (event.which || event.keyCode);
            if (!this.shift || type != 'keypress') this.key = _keys[code]/*<1.3compat>*/ || Object.keyOf(Event.Keys, code)/*</1.3compat>*/;
            if (type == 'keydown' || type == 'keyup'){
                if (code > 111 && code < 124) this.key = 'f' + (code - 111);
                else if (code > 95 && code < 106) this.key = code - 96;
            }
            if (this.key == null) this.key = String.fromCharCode(code).toLowerCase();
        } else if (type == 'click' || type == 'dblclick' || type == 'contextmenu' || type == 'wheel' || type == 'DOMMouseScroll' || type.indexOf('mouse') == 0){
            var doc = win.document;
            doc = (!doc.compatMode || doc.compatMode == 'CSS1Compat') ? doc.html : doc.body;
            this.page = {
                x: (event.pageX != null) ? event.pageX : event.clientX + doc.scrollLeft,
                y: (event.pageY != null) ? event.pageY : event.clientY + doc.scrollTop
            };
            this.client = {
                x: (event.pageX != null) ? event.pageX - win.pageXOffset : event.clientX,
                y: (event.pageY != null) ? event.pageY - win.pageYOffset : event.clientY
            };
            if (type == 'DOMMouseScroll' || type == 'wheel' || type == 'mousewheel') this.wheel = normalizeWheelSpeed(event);
            this.rightClick = (event.which == 3 || event.button == 2);
            if (type == 'mouseover' || type == 'mouseout' || type == 'mouseenter' || type == 'mouseleave'){
                var overTarget = type == 'mouseover' || type == 'mouseenter';
                var related = event.relatedTarget || event[(overTarget ? 'from' : 'to') + 'Element'];
                while (related && related.nodeType == 3) related = related.parentNode;
                this.relatedTarget = document.id(related);
            }
        } else if (type.indexOf('touch') == 0 || type.indexOf('gesture') == 0){
            this.rotation = event.rotation;
            this.scale = event.scale;
            this.targetTouches = event.targetTouches;
            this.changedTouches = event.changedTouches;
            var touches = this.touches = event.touches;
            if (touches && touches[0]){
                var touch = touches[0];
                this.page = {x: touch.pageX, y: touch.pageY};
                this.client = {x: touch.clientX, y: touch.clientY};
            }
        }

        if (!this.client) this.client = {};
        if (!this.page) this.page = {};
    });

    DOMEvent.implement({

        stop: function(){
            return this.preventDefault().stopPropagation();
        },

        stopPropagation: function(){
            if (this.event.stopPropagation) this.event.stopPropagation();
            else this.event.cancelBubble = true;
            return this;
        },

        preventDefault: function(){
            if (this.event.preventDefault) this.event.preventDefault();
            else this.event.returnValue = false;
            return this;
        }

    });

    DOMEvent.defineKey = function(code, key){
        _keys[code] = key;
        return this;
    };

    DOMEvent.defineKeys = DOMEvent.defineKey.overloadSetter(true);

    DOMEvent.defineKeys({
        '38': 'up', '40': 'down', '37': 'left', '39': 'right',
        '27': 'esc', '32': 'space', '8': 'backspace', '9': 'tab',
        '46': 'delete', '13': 'enter'
    });

})();

/*<1.3compat>*/
var Event = this.Event = DOMEvent;
Event.Keys = {};
/*</1.3compat>*/

/*<1.2compat>*/

Event.Keys = new Hash(Event.Keys);

/*</1.2compat>*/

/*
---

name: Element.Event

description: Contains Element methods for dealing with events. This file also includes mouseenter and mouseleave custom Element Events, if necessary.

license: MIT-style license.

requires: [Element, Event]

provides: Element.Event

...
*/

(function(){

    Element.Properties.events = {set: function(events){
            this.addEvents(events);
        }};

    [Element, Window, Document].invoke('implement', {

        addEvent: function(type, fn){
            var events = this.retrieve('events', {});
            if (!events[type]) events[type] = {keys: [], values: []};
            if (events[type].keys.contains(fn)) return this;
            events[type].keys.push(fn);
            var realType = type,
                custom = Element.Events[type],
                condition = fn,
                self = this;
            if (custom){
                if (custom.onAdd) custom.onAdd.call(this, fn, type);
                if (custom.condition){
                    condition = function(event){
                        if (custom.condition.call(this, event, type)) return fn.call(this, event);
                        return true;
                    };
                }
                if (custom.base) realType = Function.convert(custom.base).call(this, type);
            }
            var defn = function(){
                return fn.call(self);
            };
            var nativeEvent = Element.NativeEvents[realType];
            if (nativeEvent){
                if (nativeEvent == 2){
                    defn = function(event){
                        event = new DOMEvent(event, self.getWindow());
                        if (condition.call(self, event) === false) event.stop();
                    };
                }
                this.addListener(realType, defn, arguments[2]);
            }
            events[type].values.push(defn);
            return this;
        },

        removeEvent: function(type, fn){
            var events = this.retrieve('events');
            if (!events || !events[type]) return this;
            var list = events[type];
            var index = list.keys.indexOf(fn);
            if (index == -1) return this;
            var value = list.values[index];
            delete list.keys[index];
            delete list.values[index];
            var custom = Element.Events[type];
            if (custom){
                if (custom.onRemove) custom.onRemove.call(this, fn, type);
                if (custom.base) type = Function.convert(custom.base).call(this, type);
            }
            return (Element.NativeEvents[type]) ? this.removeListener(type, value, arguments[2]) : this;
        },

        addEvents: function(events){
            for (var event in events) this.addEvent(event, events[event]);
            return this;
        },

        removeEvents: function(events){
            var type;
            if (typeOf(events) == 'object'){
                for (type in events) this.removeEvent(type, events[type]);
                return this;
            }
            var attached = this.retrieve('events');
            if (!attached) return this;
            if (!events){
                for (type in attached) this.removeEvents(type);
                this.eliminate('events');
            } else if (attached[events]){
                attached[events].keys.each(function(fn){
                    this.removeEvent(events, fn);
                }, this);
                delete attached[events];
            }
            return this;
        },

        fireEvent: function(type, args, delay){
            var events = this.retrieve('events');
            if (!events || !events[type]) return this;
            args = Array.convert(args);

            events[type].keys.each(function(fn){
                if (delay) fn.delay(delay, this, args);
                else fn.apply(this, args);
            }, this);
            return this;
        },

        cloneEvents: function(from, type){
            from = document.id(from);
            var events = from.retrieve('events');
            if (!events) return this;
            if (!type){
                for (var eventType in events) this.cloneEvents(from, eventType);
            } else if (events[type]){
                events[type].keys.each(function(fn){
                    this.addEvent(type, fn);
                }, this);
            }
            return this;
        }

    });

    Element.NativeEvents = {
        click: 2, dblclick: 2, mouseup: 2, mousedown: 2, contextmenu: 2, //mouse buttons
        wheel: 2, mousewheel: 2, DOMMouseScroll: 2, //mouse wheel
        mouseover: 2, mouseout: 2, mousemove: 2, selectstart: 2, selectend: 2, //mouse movement
        keydown: 2, keypress: 2, keyup: 2, //keyboard
        orientationchange: 2, // mobile
        touchstart: 2, touchmove: 2, touchend: 2, touchcancel: 2, // touch
        gesturestart: 2, gesturechange: 2, gestureend: 2, // gesture
        focus: 2, blur: 2, change: 2, reset: 2, select: 2, submit: 2, paste: 2, input: 2, //form elements
        load: 2, unload: 1, beforeunload: 2, resize: 1, move: 1, DOMContentLoaded: 1, readystatechange: 1, //window
        hashchange: 1, popstate: 2, pageshow: 2, pagehide: 2, // history
        error: 1, abort: 1, scroll: 1, message: 2 //misc
    };

    Element.Events = {
        mousewheel: {
            base: 'onwheel' in document ? 'wheel' : 'onmousewheel' in document ? 'mousewheel' : 'DOMMouseScroll'
        }
    };

    var check = function(event){
        var related = event.relatedTarget;
        if (related == null) return true;
        if (!related) return false;
        return (related != this && related.prefix != 'xul' && typeOf(this) != 'document' && !this.contains(related));
    };

    if ('onmouseenter' in document.documentElement){
        Element.NativeEvents.mouseenter = Element.NativeEvents.mouseleave = 2;
        Element.MouseenterCheck = check;
    } else {
        Element.Events.mouseenter = {
            base: 'mouseover',
            condition: check
        };

        Element.Events.mouseleave = {
            base: 'mouseout',
            condition: check
        };
    }

    /*<ltIE9>*/
    if (!window.addEventListener){
        Element.NativeEvents.propertychange = 2;
        Element.Events.change = {
            base: function(){
                var type = this.type;
                return (this.get('tag') == 'input' && (type == 'radio' || type == 'checkbox')) ? 'propertychange' : 'change';
            },
            condition: function(event){
                return event.type != 'propertychange' || event.event.propertyName == 'checked';
            }
        };
    }
    /*</ltIE9>*/

//<1.2compat>

    Element.Events = new Hash(Element.Events);

//</1.2compat>

})();

/*
---

name: Element.Delegation

description: Extends the Element native object to include the delegate method for more efficient event management.

license: MIT-style license.

requires: [Element.Event]

provides: [Element.Delegation]

...
*/

(function(){

    var eventListenerSupport = !!window.addEventListener;

    Element.NativeEvents.focusin = Element.NativeEvents.focusout = 2;

    var bubbleUp = function(self, match, fn, event, target){
        while (target && target != self){
            if (match(target, event)) return fn.call(target, event, target);
            target = document.id(target.parentNode);
        }
    };

    var map = {
        mouseenter: {
            base: 'mouseover',
            condition: Element.MouseenterCheck
        },
        mouseleave: {
            base: 'mouseout',
            condition: Element.MouseenterCheck
        },
        focus: {
            base: 'focus' + (eventListenerSupport ? '' : 'in'),
            capture: true
        },
        blur: {
            base: eventListenerSupport ? 'blur' : 'focusout',
            capture: true
        }
    };

    /*<ltIE9>*/
    var _key = '$delegation:';
    var formObserver = function(type){

        return {

            base: 'focusin',

            remove: function(self, uid){
                var list = self.retrieve(_key + type + 'listeners', {})[uid];
                if (list && list.forms) for (var i = list.forms.length; i--;){
                    // the form may have been destroyed, so it won't have the
                    // removeEvent method anymore. In that case the event was
                    // removed as well.
                    if (list.forms[i].removeEvent) list.forms[i].removeEvent(type, list.fns[i]);
                }
            },

            listen: function(self, match, fn, event, target, uid){
                var form = (target.get('tag') == 'form') ? target : event.target.getParent('form');
                if (!form) return;

                var listeners = self.retrieve(_key + type + 'listeners', {}),
                    listener = listeners[uid] || {forms: [], fns: []},
                    forms = listener.forms, fns = listener.fns;

                if (forms.indexOf(form) != -1) return;
                forms.push(form);

                var _fn = function(event){
                    bubbleUp(self, match, fn, event, target);
                };
                form.addEvent(type, _fn);
                fns.push(_fn);

                listeners[uid] = listener;
                self.store(_key + type + 'listeners', listeners);
            }
        };
    };

    var inputObserver = function(type){
        return {
            base: 'focusin',
            listen: function(self, match, fn, event, target){
                var events = {blur: function(){
                        this.removeEvents(events);
                    }};
                events[type] = function(event){
                    bubbleUp(self, match, fn, event, target);
                };
                event.target.addEvents(events);
            }
        };
    };

    if (!eventListenerSupport) Object.append(map, {
        submit: formObserver('submit'),
        reset: formObserver('reset'),
        change: inputObserver('change'),
        select: inputObserver('select')
    });
    /*</ltIE9>*/

    var proto = Element.prototype,
        addEvent = proto.addEvent,
        removeEvent = proto.removeEvent;

    var relay = function(old, method){
        return function(type, fn, useCapture){
            if (type.indexOf(':relay') == -1) return old.call(this, type, fn, useCapture);
            var parsed = Slick.parse(type).expressions[0][0];
            if (parsed.pseudos[0].key != 'relay') return old.call(this, type, fn, useCapture);
            var newType = parsed.tag;
            parsed.pseudos.slice(1).each(function(pseudo){
                newType += ':' + pseudo.key + (pseudo.value ? '(' + pseudo.value + ')' : '');
            });
            old.call(this, type, fn);
            return method.call(this, newType, parsed.pseudos[0].value, fn);
        };
    };

    var delegation = {

        addEvent: function(type, match, fn){
            var storage = this.retrieve('$delegates', {}), stored = storage[type];
            if (stored) for (var _uid in stored){
                if (stored[_uid].fn == fn && stored[_uid].match == match) return this;
            }

            var _type = type, _match = match, _fn = fn, _map = map[type] || {};
            type = _map.base || _type;

            match = function(target){
                return Slick.match(target, _match);
            };

            var elementEvent = Element.Events[_type];
            if (_map.condition || elementEvent && elementEvent.condition){
                var __match = match, condition = _map.condition || elementEvent.condition;
                match = function(target, event){
                    return __match(target, event) && condition.call(target, event, type);
                };
            }

            var self = this, uid = String.uniqueID();
            var delegator = _map.listen ? function(event, target){
                if (!target && event && event.target) target = event.target;
                if (target) _map.listen(self, match, fn, event, target, uid);
            } : function(event, target){
                if (!target && event && event.target) target = event.target;
                if (target) bubbleUp(self, match, fn, event, target);
            };

            if (!stored) stored = {};
            stored[uid] = {
                match: _match,
                fn: _fn,
                delegator: delegator
            };
            storage[_type] = stored;
            return addEvent.call(this, type, delegator, _map.capture);
        },

        removeEvent: function(type, match, fn, _uid){
            var storage = this.retrieve('$delegates', {}), stored = storage[type];
            if (!stored) return this;

            if (_uid){
                var _type = type, delegator = stored[_uid].delegator, _map = map[type] || {};
                type = _map.base || _type;
                if (_map.remove) _map.remove(this, _uid);
                delete stored[_uid];
                storage[_type] = stored;
                return removeEvent.call(this, type, delegator, _map.capture);
            }

            var __uid, s;
            if (fn) for (__uid in stored){
                s = stored[__uid];
                if (s.match == match && s.fn == fn) return delegation.removeEvent.call(this, type, match, fn, __uid);
            } else for (__uid in stored){
                s = stored[__uid];
                if (s.match == match) delegation.removeEvent.call(this, type, match, s.fn, __uid);
            }
            return this;
        }

    };

    [Element, Window, Document].invoke('implement', {
        addEvent: relay(addEvent, delegation.addEvent),
        removeEvent: relay(removeEvent, delegation.removeEvent)
    });

})();

/*
---

name: Element.Style

description: Contains methods for interacting with the styles of Elements in a fashionable way.

license: MIT-style license.

requires: Element

provides: Element.Style

...
*/

(function(){

    var html = document.html, el;

//<ltIE9>
// Check for oldIE, which does not remove styles when they're set to null
    el = document.createElement('div');
    el.style.color = 'red';
    el.style.color = null;
    var doesNotRemoveStyles = el.style.color == 'red';

// check for oldIE, which returns border* shorthand styles in the wrong order (color-width-style instead of width-style-color)
    var border = '1px solid #123abc';
    el.style.border = border;
    var returnsBordersInWrongOrder = el.style.border != border;
    el = null;
//</ltIE9>

    var hasGetComputedStyle = !!window.getComputedStyle,
        supportBorderRadius = document.createElement('div').style.borderRadius != null;

    Element.Properties.styles = {set: function(styles){
            this.setStyles(styles);
        }};

    var hasOpacity = (html.style.opacity != null),
        hasFilter = (html.style.filter != null),
        reAlpha = /alpha\(opacity=([\d.]+)\)/i;

    var setVisibility = function(element, opacity){
        element.store('$opacity', opacity);
        element.style.visibility = opacity > 0 || opacity == null ? 'visible' : 'hidden';
    };

//<ltIE9>
    var setFilter = function(element, regexp, value){
        var style = element.style,
            filter = style.filter || element.getComputedStyle('filter') || '';
        style.filter = (regexp.test(filter) ? filter.replace(regexp, value) : filter + ' ' + value).trim();
        if (!style.filter) style.removeAttribute('filter');
    };
//</ltIE9>

    var setOpacity = (hasOpacity ? function(element, opacity){
        element.style.opacity = opacity;
    } : (hasFilter ? function(element, opacity){
        if (!element.currentStyle || !element.currentStyle.hasLayout) element.style.zoom = 1;
        if (opacity == null || opacity == 1){
            setFilter(element, reAlpha, '');
            if (opacity == 1 && getOpacity(element) != 1) setFilter(element, reAlpha, 'alpha(opacity=100)');
        } else {
            setFilter(element, reAlpha, 'alpha(opacity=' + (opacity * 100).limit(0, 100).round() + ')');
        }
    } : setVisibility));

    var getOpacity = (hasOpacity ? function(element){
        var opacity = element.style.opacity || element.getComputedStyle('opacity');
        return (opacity == '') ? 1 : opacity.toFloat();
    } : (hasFilter ? function(element){
        var filter = (element.style.filter || element.getComputedStyle('filter')),
            opacity;
        if (filter) opacity = filter.match(reAlpha);
        return (opacity == null || filter == null) ? 1 : (opacity[1] / 100);
    } : function(element){
        var opacity = element.retrieve('$opacity');
        if (opacity == null) opacity = (element.style.visibility == 'hidden' ? 0 : 1);
        return opacity;
    }));

    var floatName = (html.style.cssFloat == null) ? 'styleFloat' : 'cssFloat',
        namedPositions = {left: '0%', top: '0%', center: '50%', right: '100%', bottom: '100%'},
        hasBackgroundPositionXY = (html.style.backgroundPositionX != null),
        prefixPattern = /^-(ms)-/;

    var camelCase = function(property){
        return property.replace(prefixPattern, '$1-').camelCase();
    };

//<ltIE9>
    var removeStyle = function(style, property){
        if (property == 'backgroundPosition'){
            style.removeAttribute(property + 'X');
            property += 'Y';
        }
        style.removeAttribute(property);
    };
//</ltIE9>

    Element.implement({

        getComputedStyle: function(property){
            if (!hasGetComputedStyle && this.currentStyle) return this.currentStyle[camelCase(property)];
            var defaultView = Element.getDocument(this).defaultView,
                computed = defaultView ? defaultView.getComputedStyle(this, null) : null;
            return (computed) ? computed.getPropertyValue((property == floatName) ? 'float' : property.hyphenate()) : '';
        },

        setStyle: function(property, value){
            if (property == 'opacity'){
                if (value != null) value = parseFloat(value);
                setOpacity(this, value);
                return this;
            }
            property = camelCase(property == 'float' ? floatName : property);
            if (typeOf(value) != 'string'){
                var map = (Element.Styles[property] || '@').split(' ');
                value = Array.convert(value).map(function(val, i){
                    if (!map[i]) return '';
                    return (typeOf(val) == 'number') ? map[i].replace('@', Math.round(val)) : val;
                }).join(' ');
            } else if (value == String(Number(value))){
                value = Math.round(value);
            }
            this.style[property] = value;
            //<ltIE9>
            if ((value == '' || value == null) && doesNotRemoveStyles && this.style.removeAttribute){
                removeStyle(this.style, property);
            }
            //</ltIE9>
            return this;
        },

        getStyle: function(property){
            if (property == 'opacity') return getOpacity(this);
            property = camelCase(property == 'float' ? floatName : property);
            if (supportBorderRadius && property.indexOf('borderRadius') != -1){
                return ['borderTopLeftRadius', 'borderTopRightRadius', 'borderBottomRightRadius', 'borderBottomLeftRadius'].map(function(corner){
                    return this.style[corner] || '0px';
                }, this).join(' ');
            }
            var result = this.style[property];
            if (!result || property == 'zIndex'){
                if (Element.ShortStyles.hasOwnProperty(property)){
                    result = [];
                    for (var s in Element.ShortStyles[property]) result.push(this.getStyle(s));
                    return result.join(' ');
                }
                result = this.getComputedStyle(property);
            }
            if (hasBackgroundPositionXY && /^backgroundPosition[XY]?$/.test(property)){
                return result.replace(/(top|right|bottom|left)/g, function(position){
                    return namedPositions[position];
                }) || '0px';
            }
            if (!result && property == 'backgroundPosition') return '0px 0px';
            if (result){
                result = String(result);
                var color = result.match(/rgba?\([\d\s,]+\)/);
                if (color) result = result.replace(color[0], color[0].rgbToHex());
            }
            if (!hasGetComputedStyle && !this.style[property]){
                if ((/^(height|width)$/).test(property) && !(/px$/.test(result))){
                    var values = (property == 'width') ? ['left', 'right'] : ['top', 'bottom'], size = 0;
                    values.each(function(value){
                        size += this.getStyle('border-' + value + '-width').toInt() + this.getStyle('padding-' + value).toInt();
                    }, this);
                    return this['offset' + property.capitalize()] - size + 'px';
                }
                if ((/^border(.+)Width|margin|padding/).test(property) && isNaN(parseFloat(result))){
                    return '0px';
                }
            }
            //<ltIE9>
            if (returnsBordersInWrongOrder && /^border(Top|Right|Bottom|Left)?$/.test(property) && /^#/.test(result)){
                return result.replace(/^(.+)\s(.+)\s(.+)$/, '$2 $3 $1');
            }
            //</ltIE9>

            return result;
        },

        setStyles: function(styles){
            for (var style in styles) this.setStyle(style, styles[style]);
            return this;
        },

        getStyles: function(){
            var result = {};
            Array.flatten(arguments).each(function(key){
                result[key] = this.getStyle(key);
            }, this);
            return result;
        }

    });

    Element.Styles = {
        left: '@px', top: '@px', bottom: '@px', right: '@px',
        width: '@px', height: '@px', maxWidth: '@px', maxHeight: '@px', minWidth: '@px', minHeight: '@px',
        backgroundColor: 'rgb(@, @, @)', backgroundSize: '@px', backgroundPosition: '@px @px', color: 'rgb(@, @, @)',
        fontSize: '@px', letterSpacing: '@px', lineHeight: '@px', clip: 'rect(@px @px @px @px)',
        margin: '@px @px @px @px', padding: '@px @px @px @px', border: '@px @ rgb(@, @, @) @px @ rgb(@, @, @) @px @ rgb(@, @, @)',
        borderWidth: '@px @px @px @px', borderStyle: '@ @ @ @', borderColor: 'rgb(@, @, @) rgb(@, @, @) rgb(@, @, @) rgb(@, @, @)',
        zIndex: '@', 'zoom': '@', fontWeight: '@', textIndent: '@px', opacity: '@', borderRadius: '@px @px @px @px'
    };

//<1.3compat>

    Element.implement({

        setOpacity: function(value){
            setOpacity(this, value);
            return this;
        },

        getOpacity: function(){
            return getOpacity(this);
        }

    });

    Element.Properties.opacity = {

        set: function(opacity){
            setOpacity(this, opacity);
            setVisibility(this, opacity);
        },

        get: function(){
            return getOpacity(this);
        }

    };

//</1.3compat>

//<1.2compat>

    Element.Styles = new Hash(Element.Styles);

//</1.2compat>

    Element.ShortStyles = {margin: {}, padding: {}, border: {}, borderWidth: {}, borderStyle: {}, borderColor: {}};

    ['Top', 'Right', 'Bottom', 'Left'].each(function(direction){
        var Short = Element.ShortStyles;
        var All = Element.Styles;
        ['margin', 'padding'].each(function(style){
            var sd = style + direction;
            Short[style][sd] = All[sd] = '@px';
        });
        var bd = 'border' + direction;
        Short.border[bd] = All[bd] = '@px @ rgb(@, @, @)';
        var bdw = bd + 'Width', bds = bd + 'Style', bdc = bd + 'Color';
        Short[bd] = {};
        Short.borderWidth[bdw] = Short[bd][bdw] = All[bdw] = '@px';
        Short.borderStyle[bds] = Short[bd][bds] = All[bds] = '@';
        Short.borderColor[bdc] = Short[bd][bdc] = All[bdc] = 'rgb(@, @, @)';
    });

    if (hasBackgroundPositionXY) Element.ShortStyles.backgroundPosition = {backgroundPositionX: '@', backgroundPositionY: '@'};
})();

/*
---

name: Element.Dimensions

description: Contains methods to work with size, scroll, or positioning of Elements and the window object.

license: MIT-style license.

credits:
  - Element positioning based on the [qooxdoo](http://qooxdoo.org/) code and smart browser fixes, [LGPL License](http://www.gnu.org/licenses/lgpl.html).
  - Viewport dimensions based on [YUI](http://developer.yahoo.com/yui/) code, [BSD License](http://developer.yahoo.com/yui/license.html).

requires: [Element, Element.Style]

provides: [Element.Dimensions]

...
*/

(function(){

    var element = document.createElement('div'),
        child = document.createElement('div');
    element.style.height = '0';
    element.appendChild(child);
    var brokenOffsetParent = (child.offsetParent === element);
    element = child = null;

    var heightComponents = ['height', 'paddingTop', 'paddingBottom', 'borderTopWidth', 'borderBottomWidth'],
        widthComponents = ['width', 'paddingLeft', 'paddingRight', 'borderLeftWidth', 'borderRightWidth'];

    var svgCalculateSize = function(el){

        var gCS = window.getComputedStyle(el),
            bounds = {x: 0, y: 0};

        heightComponents.each(function(css){
            bounds.y += parseFloat(gCS[css]);
        });
        widthComponents.each(function(css){
            bounds.x += parseFloat(gCS[css]);
        });
        return bounds;
    };

    var isOffset = function(el){
        return styleString(el, 'position') != 'static' || isBody(el);
    };

    var isOffsetStatic = function(el){
        return isOffset(el) || (/^(?:table|td|th)$/i).test(el.tagName);
    };

    Element.implement({

        scrollTo: function(x, y){
            if (isBody(this)){
                this.getWindow().scrollTo(x, y);
            } else {
                this.scrollLeft = x;
                this.scrollTop = y;
            }
            return this;
        },

        getSize: function(){
            if (isBody(this)) return this.getWindow().getSize();

            //<ltIE9>
            // This if clause is because IE8- cannot calculate getBoundingClientRect of elements with visibility hidden.
            if (!window.getComputedStyle) return {x: this.offsetWidth, y: this.offsetHeight};
            //</ltIE9>

            // This svg section under, calling `svgCalculateSize()`, can be removed when FF fixed the svg size bug.
            // Bug info: https://bugzilla.mozilla.org/show_bug.cgi?id=530985
            if (this.get('tag') == 'svg') return svgCalculateSize(this);

            try {
                var bounds = this.getBoundingClientRect();
                return {x: bounds.width, y: bounds.height};
            } catch (e){
                return {x: 0, y: 0};
            }
        },

        getScrollSize: function(){
            if (isBody(this)) return this.getWindow().getScrollSize();
            return {x: this.scrollWidth, y: this.scrollHeight};
        },

        getScroll: function(){
            if (isBody(this)) return this.getWindow().getScroll();
            return {x: this.scrollLeft, y: this.scrollTop};
        },

        getScrolls: function(){
            var element = this.parentNode, position = {x: 0, y: 0};
            while (element && !isBody(element)){
                position.x += element.scrollLeft;
                position.y += element.scrollTop;
                element = element.parentNode;
            }
            return position;
        },

        getOffsetParent: brokenOffsetParent ? function(){
            var element = this;
            if (isBody(element) || styleString(element, 'position') == 'fixed') return null;

            var isOffsetCheck = (styleString(element, 'position') == 'static') ? isOffsetStatic : isOffset;
            while ((element = element.parentNode)){
                if (isOffsetCheck(element)) return element;
            }
            return null;
        } : function(){
            var element = this;
            if (isBody(element) || styleString(element, 'position') == 'fixed') return null;

            try {
                return element.offsetParent;
            } catch (e){}
            return null;
        },

        getOffsets: function(){
            var hasGetBoundingClientRect = this.getBoundingClientRect;
//<1.4compat>
            hasGetBoundingClientRect = hasGetBoundingClientRect && !Browser.Platform.ios;
//</1.4compat>
            if (hasGetBoundingClientRect){
                var bound = this.getBoundingClientRect(),
                    html = document.id(this.getDocument().documentElement),
                    htmlScroll = html.getScroll(),
                    elemScrolls = this.getScrolls(),
                    isFixed = (styleString(this, 'position') == 'fixed');

                return {
                    x: bound.left.toFloat() + elemScrolls.x + ((isFixed) ? 0 : htmlScroll.x) - html.clientLeft,
                    y: bound.top.toFloat() + elemScrolls.y + ((isFixed) ? 0 : htmlScroll.y) - html.clientTop
                };
            }

            var element = this, position = {x: 0, y: 0};
            if (isBody(this)) return position;

            while (element && !isBody(element)){
                position.x += element.offsetLeft;
                position.y += element.offsetTop;
//<1.4compat>
                if (Browser.firefox){
                    if (!borderBox(element)){
                        position.x += leftBorder(element);
                        position.y += topBorder(element);
                    }
                    var parent = element.parentNode;
                    if (parent && styleString(parent, 'overflow') != 'visible'){
                        position.x += leftBorder(parent);
                        position.y += topBorder(parent);
                    }
                } else if (element != this && Browser.safari){
                    position.x += leftBorder(element);
                    position.y += topBorder(element);
                }
//</1.4compat>
                element = element.offsetParent;
            }
//<1.4compat>
            if (Browser.firefox && !borderBox(this)){
                position.x -= leftBorder(this);
                position.y -= topBorder(this);
            }
//</1.4compat>
            return position;
        },

        getPosition: function(relative){
            var offset = this.getOffsets(),
                scroll = this.getScrolls();
            var position = {
                x: offset.x - scroll.x,
                y: offset.y - scroll.y
            };

            if (relative && (relative = document.id(relative))){
                var relativePosition = relative.getPosition();
                return {x: position.x - relativePosition.x - leftBorder(relative), y: position.y - relativePosition.y - topBorder(relative)};
            }
            return position;
        },

        getCoordinates: function(element){
            if (isBody(this)) return this.getWindow().getCoordinates();
            var position = this.getPosition(element),
                size = this.getSize();
            var obj = {
                left: position.x,
                top: position.y,
                width: size.x,
                height: size.y
            };
            obj.right = obj.left + obj.width;
            obj.bottom = obj.top + obj.height;
            return obj;
        },

        computePosition: function(obj){
            return {
                left: obj.x - styleNumber(this, 'margin-left'),
                top: obj.y - styleNumber(this, 'margin-top')
            };
        },

        setPosition: function(obj){
            return this.setStyles(this.computePosition(obj));
        }

    });


    [Document, Window].invoke('implement', {

        getSize: function(){
            var doc = getCompatElement(this);
            return {x: doc.clientWidth, y: doc.clientHeight};
        },

        getScroll: function(){
            var win = this.getWindow(), doc = getCompatElement(this);
            return {x: win.pageXOffset || doc.scrollLeft, y: win.pageYOffset || doc.scrollTop};
        },

        getScrollSize: function(){
            var doc = getCompatElement(this),
                min = this.getSize(),
                body = this.getDocument().body;

            return {x: Math.max(doc.scrollWidth, body.scrollWidth, min.x), y: Math.max(doc.scrollHeight, body.scrollHeight, min.y)};
        },

        getPosition: function(){
            return {x: 0, y: 0};
        },

        getCoordinates: function(){
            var size = this.getSize();
            return {top: 0, left: 0, bottom: size.y, right: size.x, height: size.y, width: size.x};
        }

    });

// private methods

    var styleString = Element.getComputedStyle;

    function styleNumber(element, style){
        return styleString(element, style).toInt() || 0;
    }

//<1.4compat>
    function borderBox(element){
        return styleString(element, '-moz-box-sizing') == 'border-box';
    }
//</1.4compat>

    function topBorder(element){
        return styleNumber(element, 'border-top-width');
    }

    function leftBorder(element){
        return styleNumber(element, 'border-left-width');
    }

    function isBody(element){
        return (/^(?:body|html)$/i).test(element.tagName);
    }

    function getCompatElement(element){
        var doc = element.getDocument();
        return (!doc.compatMode || doc.compatMode == 'CSS1Compat') ? doc.html : doc.body;
    }

})();

//aliases
Element.alias({position: 'setPosition'}); //compatability

[Window, Document, Element].invoke('implement', {

    getHeight: function(){
        return this.getSize().y;
    },

    getWidth: function(){
        return this.getSize().x;
    },

    getScrollTop: function(){
        return this.getScroll().y;
    },

    getScrollLeft: function(){
        return this.getScroll().x;
    },

    getScrollHeight: function(){
        return this.getScrollSize().y;
    },

    getScrollWidth: function(){
        return this.getScrollSize().x;
    },

    getTop: function(){
        return this.getPosition().y;
    },

    getLeft: function(){
        return this.getPosition().x;
    }

});

/*
---

name: Fx

description: Contains the basic animation logic to be extended by all other Fx Classes.

license: MIT-style license.

requires: [Chain, Events, Options, Class.Thenable]

provides: Fx

...
*/

(function(){

    var Fx = this.Fx = new Class({

        Implements: [Chain, Events, Options, Class.Thenable],

        options: {
            /*
		onStart: nil,
		onCancel: nil,
		onComplete: nil,
		*/
            fps: 60,
            unit: false,
            duration: 500,
            frames: null,
            frameSkip: true,
            link: 'ignore'
        },

        initialize: function(options){
            this.subject = this.subject || this;
            this.setOptions(options);
        },

        getTransition: function(){
            return function(p){
                return -(Math.cos(Math.PI * p) - 1) / 2;
            };
        },

        step: function(now){
            if (this.options.frameSkip){
                var diff = (this.time != null) ? (now - this.time) : 0, frames = diff / this.frameInterval;
                this.time = now;
                this.frame += frames;
            } else {
                this.frame++;
            }

            if (this.frame < this.frames){
                var delta = this.transition(this.frame / this.frames);
                this.set(this.compute(this.from, this.to, delta));
            } else {
                this.frame = this.frames;
                this.set(this.compute(this.from, this.to, 1));
                this.stop();
            }
        },

        set: function(now){
            return now;
        },

        compute: function(from, to, delta){
            return Fx.compute(from, to, delta);
        },

        check: function(){
            if (!this.isRunning()) return true;
            switch (this.options.link){
                case 'cancel': this.cancel(); return true;
                case 'chain': this.chain(this.caller.pass(arguments, this)); return false;
            }
            return false;
        },

        start: function(from, to){
            if (!this.check(from, to)) return this;
            this.from = from;
            this.to = to;
            this.frame = (this.options.frameSkip) ? 0 : -1;
            this.time = null;
            this.transition = this.getTransition();
            var frames = this.options.frames, fps = this.options.fps, duration = this.options.duration;
            this.duration = Fx.Durations[duration] || duration.toInt();
            this.frameInterval = 1000 / fps;
            this.frames = frames || Math.round(this.duration / this.frameInterval);
            if (this.getThenableState() !== 'pending'){
                this.resetThenable(this.subject);
            }
            this.fireEvent('start', this.subject);
            pushInstance.call(this, fps);
            return this;
        },

        stop: function(){
            if (this.isRunning()){
                this.time = null;
                pullInstance.call(this, this.options.fps);
                if (this.frames == this.frame){
                    this.fireEvent('complete', this.subject);
                    if (!this.callChain()) this.fireEvent('chainComplete', this.subject);
                } else {
                    this.fireEvent('stop', this.subject);
                }
                this.resolve(this.subject === this ? null : this.subject);
            }
            return this;
        },

        cancel: function(){
            if (this.isRunning()){
                this.time = null;
                pullInstance.call(this, this.options.fps);
                this.frame = this.frames;
                this.fireEvent('cancel', this.subject).clearChain();
                this.reject(this.subject);
            }
            return this;
        },

        pause: function(){
            if (this.isRunning()){
                this.time = null;
                pullInstance.call(this, this.options.fps);
            }
            return this;
        },

        resume: function(){
            if (this.isPaused()) pushInstance.call(this, this.options.fps);
            return this;
        },

        isRunning: function(){
            var list = instances[this.options.fps];
            return list && list.contains(this);
        },

        isPaused: function(){
            return (this.frame < this.frames) && !this.isRunning();
        }

    });

    Fx.compute = function(from, to, delta){
        return (to - from) * delta + from;
    };

    Fx.Durations = {'short': 250, 'normal': 500, 'long': 1000};

// global timers

    var instances = {}, timers = {};

    var loop = function(){
        var now = Date.now();
        for (var i = this.length; i--;){
            var instance = this[i];
            if (instance) instance.step(now);
        }
    };

    var pushInstance = function(fps){
        var list = instances[fps] || (instances[fps] = []);
        list.push(this);
        if (!timers[fps]) timers[fps] = loop.periodical(Math.round(1000 / fps), list);
    };

    var pullInstance = function(fps){
        var list = instances[fps];
        if (list){
            list.erase(this);
            if (!list.length && timers[fps]){
                delete instances[fps];
                timers[fps] = clearInterval(timers[fps]);
            }
        }
    };

})();

/*
---

name: Fx.CSS

description: Contains the CSS animation logic. Used by Fx.Tween, Fx.Morph, Fx.Elements.

license: MIT-style license.

requires: [Fx, Element.Style]

provides: Fx.CSS

...
*/

Fx.CSS = new Class({

    Extends: Fx,

    //prepares the base from/to object

    prepare: function(element, property, values){
        values = Array.convert(values);
        var from = values[0], to = values[1];
        if (to == null){
            to = from;
            from = element.getStyle(property);
            var unit = this.options.unit;
            // adapted from: https://github.com/ryanmorr/fx/blob/master/fx.js#L299
            if (unit && from && typeof from == 'string' && from.slice(-unit.length) != unit && parseFloat(from) != 0){
                element.setStyle(property, to + unit);
                var value = element.getComputedStyle(property);
                // IE and Opera support pixelLeft or pixelWidth
                if (!(/px$/.test(value))){
                    value = element.style[('pixel-' + property).camelCase()];
                    if (value == null){
                        // adapted from Dean Edwards' http://erik.eae.net/archives/2007/07/27/18.54.15/#comment-102291
                        var left = element.style.left;
                        element.style.left = to + unit;
                        value = element.style.pixelLeft;
                        element.style.left = left;
                    }
                }
                from = (to || 1) / (parseFloat(value) || 1) * (parseFloat(from) || 0);
                element.setStyle(property, from + unit);
            }
        }
        return {from: this.parse(from), to: this.parse(to)};
    },

    //parses a value into an array

    parse: function(value){
        value = Function.convert(value)();
        value = (typeof value == 'string') ? value.split(' ') : Array.convert(value);
        return value.map(function(val){
            val = String(val);
            var found = false;
            Object.each(Fx.CSS.Parsers, function(parser){
                if (found) return;
                var parsed = parser.parse(val);
                if (parsed || parsed === 0) found = {value: parsed, parser: parser};
            });
            found = found || {value: val, parser: Fx.CSS.Parsers.String};
            return found;
        });
    },

    //computes by a from and to prepared objects, using their parsers.

    compute: function(from, to, delta){
        var computed = [];
        (Math.min(from.length, to.length)).times(function(i){
            computed.push({value: from[i].parser.compute(from[i].value, to[i].value, delta), parser: from[i].parser});
        });
        computed.$family = Function.convert('fx:css:value');
        return computed;
    },

    //serves the value as settable

    serve: function(value, unit){
        if (typeOf(value) != 'fx:css:value') value = this.parse(value);
        var returned = [];
        value.each(function(bit){
            returned = returned.concat(bit.parser.serve(bit.value, unit));
        });
        return returned;
    },

    //renders the change to an element

    render: function(element, property, value, unit){
        element.setStyle(property, this.serve(value, unit));
    },

    //searches inside the page css to find the values for a selector

    search: function(selector){
        if (Fx.CSS.Cache[selector]) return Fx.CSS.Cache[selector];
        var to = {}, selectorTest = new RegExp('^' + selector.escapeRegExp() + '$');

        var searchStyles = function(rules){
            Array.each(rules, function(rule){
                if (rule.media){
                    searchStyles(rule.rules || rule.cssRules);
                    return;
                }
                if (!rule.style) return;
                var selectorText = (rule.selectorText) ? rule.selectorText.replace(/^\w+/, function(m){
                    return m.toLowerCase();
                }) : null;
                if (!selectorText || !selectorTest.test(selectorText)) return;
                Object.each(Element.Styles, function(value, style){
                    if (!rule.style[style] || Element.ShortStyles[style]) return;
                    value = String(rule.style[style]);
                    to[style] = ((/^rgb/).test(value)) ? value.rgbToHex() : value;
                });
            });
        };

        Array.each(document.styleSheets, function(sheet){
            var href = sheet.href;
            if (href && href.indexOf('://') > -1 && href.indexOf(document.domain) == -1) return;
            var rules = sheet.rules || sheet.cssRules;
            searchStyles(rules);
        });
        return Fx.CSS.Cache[selector] = to;
    }

});

Fx.CSS.Cache = {};

Fx.CSS.Parsers = {

    Color: {
        parse: function(value){
            if (value.match(/^#[0-9a-f]{3,6}$/i)) return value.hexToRgb(true);
            return ((value = value.match(/(\d+),\s*(\d+),\s*(\d+)/))) ? [value[1], value[2], value[3]] : false;
        },
        compute: function(from, to, delta){
            return from.map(function(value, i){
                return Math.round(Fx.compute(from[i], to[i], delta));
            });
        },
        serve: function(value){
            return value.map(Number);
        }
    },

    Number: {
        parse: parseFloat,
        compute: Fx.compute,
        serve: function(value, unit){
            return (unit) ? value + unit : value;
        }
    },

    String: {
        parse: Function.convert(false),
        compute: function(zero, one){
            return one;
        },
        serve: function(zero){
            return zero;
        }
    }

};

//<1.2compat>

Fx.CSS.Parsers = new Hash(Fx.CSS.Parsers);

//</1.2compat>

/*
---

name: Fx.Morph

description: Formerly Fx.Styles, effect to transition any number of CSS properties for an element using an object of rules, or CSS based selector rules.

license: MIT-style license.

requires: Fx.CSS

provides: Fx.Morph

...
*/

Fx.Morph = new Class({

    Extends: Fx.CSS,

    initialize: function(element, options){
        this.element = this.subject = document.id(element);
        this.parent(options);
    },

    set: function(now){
        if (typeof now == 'string') now = this.search(now);
        for (var p in now) this.render(this.element, p, now[p], this.options.unit);
        return this;
    },

    compute: function(from, to, delta){
        var now = {};
        for (var p in from) now[p] = this.parent(from[p], to[p], delta);
        return now;
    },

    start: function(properties){
        if (!this.check(properties)) return this;
        if (typeof properties == 'string') properties = this.search(properties);
        var from = {}, to = {};
        for (var p in properties){
            var parsed = this.prepare(this.element, p, properties[p]);
            from[p] = parsed.from;
            to[p] = parsed.to;
        }
        return this.parent(from, to);
    }

});

Element.Properties.morph = {

    set: function(options){
        this.get('morph').cancel().setOptions(options);
        return this;
    },

    get: function(){
        var morph = this.retrieve('morph');
        if (!morph){
            morph = new Fx.Morph(this, {link: 'cancel'});
            this.store('morph', morph);
        }
        return morph;
    }

};

Element.implement({

    morph: function(props){
        this.get('morph').start(props);
        return this;
    }

});

/*
---

name: Fx.Transitions

description: Contains a set of advanced transitions to be used with any of the Fx Classes.

license: MIT-style license.

credits:
  - Easing Equations by Robert Penner, <http://www.robertpenner.com/easing/>, modified and optimized to be used with MooTools.

requires: Fx

provides: Fx.Transitions

...
*/

Fx.implement({

    getTransition: function(){
        var trans = this.options.transition || Fx.Transitions.Sine.easeInOut;
        if (typeof trans == 'string'){
            var data = trans.split(':');
            trans = Fx.Transitions;
            trans = trans[data[0]] || trans[data[0].capitalize()];
            if (data[1]) trans = trans['ease' + data[1].capitalize() + (data[2] ? data[2].capitalize() : '')];
        }
        return trans;
    }

});

Fx.Transition = function(transition, params){
    params = Array.convert(params);
    var easeIn = function(pos){
        return transition(pos, params);
    };
    return Object.append(easeIn, {
        easeIn: easeIn,
        easeOut: function(pos){
            return 1 - transition(1 - pos, params);
        },
        easeInOut: function(pos){
            return (pos <= 0.5 ? transition(2 * pos, params) : (2 - transition(2 * (1 - pos), params))) / 2;
        }
    });
};

Fx.Transitions = {

    linear: function(zero){
        return zero;
    }

};

//<1.2compat>

Fx.Transitions = new Hash(Fx.Transitions);

//</1.2compat>

Fx.Transitions.extend = function(transitions){
    for (var transition in transitions) Fx.Transitions[transition] = new Fx.Transition(transitions[transition]);
};

Fx.Transitions.extend({

    Pow: function(p, x){
        return Math.pow(p, x && x[0] || 6);
    },

    Expo: function(p){
        return Math.pow(2, 8 * (p - 1));
    },

    Circ: function(p){
        return 1 - Math.sin(Math.acos(p));
    },

    Sine: function(p){
        return 1 - Math.cos(p * Math.PI / 2);
    },

    Back: function(p, x){
        x = x && x[0] || 1.618;
        return Math.pow(p, 2) * ((x + 1) * p - x);
    },

    Bounce: function(p){
        var value;
        for (var a = 0, b = 1; 1; a += b, b /= 2){
            if (p >= (7 - 4 * a) / 11){
                value = b * b - Math.pow((11 - 6 * a - 11 * p) / 4, 2);
                break;
            }
        }
        return value;
    },

    Elastic: function(p, x){
        return Math.pow(2, 10 * --p) * Math.cos(20 * p * Math.PI * (x && x[0] || 1) / 3);
    }

});

['Quad', 'Cubic', 'Quart', 'Quint'].each(function(transition, i){
    Fx.Transitions[transition] = new Fx.Transition(function(p){
        return Math.pow(p, i + 2);
    });
});

/*
---

name: Fx.Tween

description: Formerly Fx.Style, effect to transition any CSS property for an element.

license: MIT-style license.

requires: Fx.CSS

provides: [Fx.Tween, Element.fade, Element.highlight]

...
*/

Fx.Tween = new Class({

    Extends: Fx.CSS,

    initialize: function(element, options){
        this.element = this.subject = document.id(element);
        this.parent(options);
    },

    set: function(property, now){
        if (arguments.length == 1){
            now = property;
            property = this.property || this.options.property;
        }
        this.render(this.element, property, now, this.options.unit);
        return this;
    },

    start: function(property, from, to){
        if (!this.check(property, from, to)) return this;
        var args = Array.flatten(arguments);
        this.property = this.options.property || args.shift();
        var parsed = this.prepare(this.element, this.property, args);
        return this.parent(parsed.from, parsed.to);
    }

});

Element.Properties.tween = {

    set: function(options){
        this.get('tween').cancel().setOptions(options);
        return this;
    },

    get: function(){
        var tween = this.retrieve('tween');
        if (!tween){
            tween = new Fx.Tween(this, {link: 'cancel'});
            this.store('tween', tween);
        }
        return tween;
    }

};

Element.implement({

    tween: function(property, from, to){
        this.get('tween').start(property, from, to);
        return this;
    },

    fade: function(){
        var fade = this.get('tween'), method, args = ['opacity'].append(arguments), toggle;
        if (args[1] == null) args[1] = 'toggle';
        switch (args[1]){
            case 'in': method = 'start'; args[1] = 1; break;
            case 'out': method = 'start'; args[1] = 0; break;
            case 'show': method = 'set'; args[1] = 1; break;
            case 'hide': method = 'set'; args[1] = 0; break;
            case 'toggle':
                var flag = this.retrieve('fade:flag', this.getStyle('opacity') == 1);
                method = 'start';
                args[1] = flag ? 0 : 1;
                this.store('fade:flag', !flag);
                toggle = true;
                break;
            default: method = 'start';
        }
        if (!toggle) this.eliminate('fade:flag');
        fade[method].apply(fade, args);
        var to = args[args.length - 1];

        if (method == 'set'){
            this.setStyle('visibility', to == 0 ? 'hidden' : 'visible');
        } else if (to != 0){
            if (fade.$chain.length){
                fade.chain(function(){
                    this.element.setStyle('visibility', 'visible');
                    this.callChain();
                });
            } else {
                this.setStyle('visibility', 'visible');
            }
        } else {
            fade.chain(function(){
                if (this.element.getStyle('opacity')) return;
                this.element.setStyle('visibility', 'hidden');
                this.callChain();
            });
        }

        return this;
    },

    highlight: function(start, end){
        if (!end){
            end = this.retrieve('highlight:original', this.getStyle('background-color'));
            end = (end == 'transparent') ? '#fff' : end;
        }
        var tween = this.get('tween');
        tween.start('background-color', start || '#ffff88', end).chain(function(){
            this.setStyle('background-color', this.retrieve('highlight:original'));
            tween.callChain();
        }.bind(this));
        return this;
    }

});

/*
---

name: Request

description: Powerful all purpose Request Class. Uses XMLHTTPRequest.

license: MIT-style license.

requires: [Object, Element, Chain, Events, Options, Class.Thenable, Browser]

provides: Request

...
*/

(function(){

    var empty = function(){},
        progressSupport = ('onprogress' in new Browser.Request);

    var Request = this.Request = new Class({

        Implements: [Chain, Events, Options, Class.Thenable],

        options: {/*
		onRequest: function(){},
		onLoadstart: function(event, xhr){},
		onProgress: function(event, xhr){},
		onComplete: function(){},
		onCancel: function(){},
		onSuccess: function(responseText, responseXML){},
		onFailure: function(xhr){},
		onException: function(headerName, value){},
		onTimeout: function(){},
		user: '',
		password: '',
		withCredentials: false,*/
            url: '',
            data: '',
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                'Accept': 'text/javascript, text/html, application/xml, text/xml, */*'
            },
            async: true,
            format: false,
            method: 'post',
            link: 'ignore',
            isSuccess: null,
            emulation: true,
            urlEncoded: true,
            encoding: 'utf-8',
            evalScripts: false,
            evalResponse: false,
            timeout: 0,
            noCache: false
        },

        initialize: function(options){
            this.xhr = new Browser.Request();
            this.setOptions(options);
            this.headers = this.options.headers;
        },

        onStateChange: function(){
            var xhr = this.xhr;
            if (xhr.readyState != 4 || !this.running) return;
            this.running = false;
            this.status = 0;
            Function.attempt(function(){
                var status = xhr.status;
                this.status = (status == 1223) ? 204 : status;
            }.bind(this));
            xhr.onreadystatechange = empty;
            if (progressSupport) xhr.onprogress = xhr.onloadstart = empty;
            if (this.timer){
                clearTimeout(this.timer);
                delete this.timer;
            }

            this.response = {text: this.xhr.responseText || '', xml: this.xhr.responseXML};
            if (this.options.isSuccess.call(this, this.status))
                this.success(this.response.text, this.response.xml);
            else
                this.failure();
        },

        isSuccess: function(){
            var status = this.status;
            return (status >= 200 && status < 300);
        },

        isRunning: function(){
            return !!this.running;
        },

        processScripts: function(text){
            if (this.options.evalResponse || (/(ecma|java)script/).test(this.getHeader('Content-type'))) return Browser.exec(text);
            return text.stripScripts(this.options.evalScripts);
        },

        success: function(text, xml){
            this.onSuccess(this.processScripts(text), xml);
            this.resolve({text: text, xml: xml});
        },

        onSuccess: function(){
            this.fireEvent('complete', arguments).fireEvent('success', arguments).callChain();
        },

        failure: function(){
            this.onFailure();
            this.reject({reason: 'failure', xhr: this.xhr});
        },

        onFailure: function(){
            this.fireEvent('complete').fireEvent('failure', this.xhr);
        },

        loadstart: function(event){
            this.fireEvent('loadstart', [event, this.xhr]);
        },

        progress: function(event){
            this.fireEvent('progress', [event, this.xhr]);
        },

        timeout: function(){
            this.fireEvent('timeout', this.xhr);
            this.reject({reason: 'timeout', xhr: this.xhr});
        },

        setHeader: function(name, value){
            this.headers[name] = value;
            return this;
        },

        getHeader: function(name){
            return Function.attempt(function(){
                return this.xhr.getResponseHeader(name);
            }.bind(this));
        },

        check: function(){
            if (!this.running) return true;
            switch (this.options.link){
                case 'cancel': this.cancel(); return true;
                case 'chain': this.chain(this.caller.pass(arguments, this)); return false;
            }
            return false;
        },

        send: function(options){
            if (!this.check(options)) return this;

            this.options.isSuccess = this.options.isSuccess || this.isSuccess;
            this.running = true;

            var type = typeOf(options);
            if (type == 'string' || type == 'element') options = {data: options};

            var old = this.options;
            options = Object.append({data: old.data, url: old.url, method: old.method}, options);
            var data = options.data, url = String(options.url), method = options.method.toLowerCase();

            switch (typeOf(data)){
                case 'element': data = document.id(data).toQueryString(); break;
                case 'object': case 'hash': data = Object.toQueryString(data);
            }

            if (this.options.format){
                var format = 'format=' + this.options.format;
                data = (data) ? format + '&' + data : format;
            }

            if (this.options.emulation && !['get', 'post'].contains(method)){
                var _method = '_method=' + method;
                data = (data) ? _method + '&' + data : _method;
                method = 'post';
            }

            if (this.options.urlEncoded && ['post', 'put'].contains(method)){
                var encoding = (this.options.encoding) ? '; charset=' + this.options.encoding : '';
                //this.headers['Content-type'] = 'application/x-www-form-urlencoded' + encoding;  //mootools1.6.0
                if (!this.headers['Content-Type']) this.headers['Content-type'] = 'application/x-www-form-urlencoded' + encoding;  //modify by tommy
            }

            if (!url) url = document.location.pathname;

            var trimPosition = url.lastIndexOf('/');
            if (trimPosition > -1 && (trimPosition = url.indexOf('#')) > -1) url = url.substr(0, trimPosition);

            if (this.options.noCache)
                url += (url.indexOf('?') > -1 ? '&' : '?') + String.uniqueID();

            if (data && (method == 'get' || method == 'delete')){
                url += (url.indexOf('?') > -1 ? '&' : '?') + data;
                data = null;
            }

            var xhr = this.xhr;
            if (progressSupport){
                xhr.onloadstart = this.loadstart.bind(this);
                xhr.onprogress = this.progress.bind(this);
            }

            //modify by Tommy--
            var isWithCredentials = ((this.options.withCredentials && 'withCredentials' in xhr) || (this.options.user && 'withCredentials' in xhr) && (method.toUpperCase()=="POST" || method.toUpperCase()=="PUT"));
            var isWithCredentialsSync = isWithCredentials && !this.options.async;
            //-----------------

            xhr.open(method.toUpperCase(), url, this.options.async, this.options.user, this.options.password);
            if ((/*<1.4compat>*/this.options.user || /*</1.4compat>*/this.options.withCredentials) && 'withCredentials' in xhr) xhr.withCredentials = true;

            //modify by Tommy--
            if (Browser.name=="firefox"){
                if (this.options.async){
                    if (isWithCredentials) xhr.withCredentials = true;
                }else{
                    xhr.setRequestHeader("x-token", Cookie.read("x-token"));
                }
            }else{
                if (isWithCredentials) xhr.withCredentials = true;
            }
            //-----------------

            xhr.onreadystatechange = this.onStateChange.bind(this);

            Object.each(this.headers, function(value, key){
                try {
                    xhr.setRequestHeader(key, value);
                } catch (e){
                    this.fireEvent('exception', [key, value]);
                    this.reject({reason: 'exception', xhr: xhr, exception: e});
                }
            }, this);

            if (this.getThenableState() !== 'pending'){
                this.resetThenable({reason: 'send'});
            }
            if (url.indexOf("v=4.0")!=-1){
                debugger;
            }

            this.fireEvent('request');
            xhr.send(data);
            if (!this.options.async) this.onStateChange();
            else if (this.options.timeout) this.timer = this.timeout.delay(this.options.timeout, this);
            return this;
        },

        cancel: function(){
            if (!this.running) return this;
            this.running = false;
            var xhr = this.xhr;
            xhr.abort();
            if (this.timer){
                clearTimeout(this.timer);
                delete this.timer;
            }
            xhr.onreadystatechange = empty;
            if (progressSupport) xhr.onprogress = xhr.onloadstart = empty;
            this.xhr = new Browser.Request();
            this.fireEvent('cancel');
            this.reject({reason: 'cancel', xhr: xhr});
            return this;
        }

    });

    var methods = {};
    ['get', 'post', 'put', 'delete', 'patch', 'head', 'GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'HEAD'].each(function(method){
        methods[method] = function(data){
            var object = {
                method: method
            };
            if (data != null) object.data = data;
            return this.send(object);
        };
    });

    Request.implement(methods);

    Element.Properties.send = {

        set: function(options){
            var send = this.get('send').cancel();
            send.setOptions(options);
            return this;
        },

        get: function(){
            var send = this.retrieve('send');
            if (!send){
                send = new Request({
                    data: this, link: 'cancel', method: this.get('method') || 'post', url: this.get('action')
                });
                this.store('send', send);
            }
            return send;
        }

    };

    Element.implement({

        send: function(url){
            var sender = this.get('send');
            sender.send({data: this, url: url || sender.options.url});
            return this;
        }

    });

})();

/*
---

name: Request.HTML

description: Extends the basic Request Class with additional methods for interacting with HTML responses.

license: MIT-style license.

requires: [Element, Request]

provides: Request.HTML

...
*/

Request.HTML = new Class({

    Extends: Request,

    options: {
        update: false,
        append: false,
        evalScripts: true,
        filter: false,
        headers: {
            Accept: 'text/html, application/xml, text/xml, */*'
        }
    },

    success: function(text){
        var options = this.options, response = this.response;

        response.html = text.stripScripts(function(script){
            response.javascript = script;
        });

        var match = response.html.match(/<body[^>]*>([\s\S]*?)<\/body>/i);
        if (match) response.html = match[1];
        var temp = new Element('div').set('html', response.html);

        response.tree = temp.childNodes;
        response.elements = temp.getElements(options.filter || '*');

        if (options.filter) response.tree = response.elements;
        if (options.update){
            var update = document.id(options.update).empty();
            if (options.filter) update.adopt(response.elements);
            else update.set('html', response.html);
        } else if (options.append){
            var append = document.id(options.append);
            if (options.filter) response.elements.reverse().inject(append);
            else append.adopt(temp.getChildren());
        }
        if (options.evalScripts) Browser.exec(response.javascript);

        this.onSuccess(response.tree, response.elements, response.html, response.javascript);
        this.resolve({tree: response.tree, elements: response.elements, html: response.html, javascript: response.javascript});
    }

});

Element.Properties.load = {

    set: function(options){
        var load = this.get('load').cancel();
        load.setOptions(options);
        return this;
    },

    get: function(){
        var load = this.retrieve('load');
        if (!load){
            load = new Request.HTML({data: this, link: 'cancel', update: this, method: 'get'});
            this.store('load', load);
        }
        return load;
    }

};

Element.implement({

    load: function(){
        this.get('load').send(Array.link(arguments, {data: Type.isObject, url: Type.isString}));
        return this;
    }

});

/*
---

name: JSON

description: JSON encoder and decoder.

license: MIT-style license.

SeeAlso: <http://www.json.org/>

requires: [Array, String, Number, Function]

provides: JSON

...
*/

if (typeof JSON == 'undefined') this.JSON = {};

//<1.2compat>

JSON = new Hash({
    stringify: JSON.stringify,
    parse: JSON.parse
});

//</1.2compat>

(function(){

    var special = {'\b': '\\b', '\t': '\\t', '\n': '\\n', '\f': '\\f', '\r': '\\r', '"' : '\\"', '\\': '\\\\'};

    var escape = function(chr){
        return special[chr] || '\\u' + ('0000' + chr.charCodeAt(0).toString(16)).slice(-4);
    };

    JSON.validate = function(string){
        string = string.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@').
        replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']').
        replace(/(?:^|:|,)(?:\s*\[)+/g, '');

        return (/^[\],:{}\s]*$/).test(string);
    };

    JSON.encode = JSON.stringify ? function(obj){
        return JSON.stringify(obj);
    } : function(obj){
        if (obj && obj.toJSON) obj = obj.toJSON();

        switch (typeOf(obj)){
            case 'string':
                return '"' + obj.replace(/[\x00-\x1f\\"]/g, escape) + '"';
            case 'array':
                return '[' + obj.map(JSON.encode).clean() + ']';
            case 'object': case 'hash':
                var string = [];
                Object.each(obj, function(value, key){
                    var json = JSON.encode(value);
                    if (json) string.push(JSON.encode(key) + ':' + json);
                });
                return '{' + string + '}';
            case 'number': case 'boolean': return '' + obj;
            case 'null': return 'null';
        }

        return null;
    };

    JSON.secure = true;
//<1.4compat>
    JSON.secure = false;
//</1.4compat>

    JSON.decode = function(string, secure){
        if (!string || typeOf(string) != 'string') return null;

        if (secure == null) secure = JSON.secure;
        if (secure){
            if (JSON.parse) return JSON.parse(string);
            if (!JSON.validate(string)) throw new Error('JSON could not decode the input; security is enabled and the value is not secure.');
        }

        return eval('(' + string + ')');
    };

})();

/*
---

name: Request.JSON

description: Extends the basic Request Class with additional methods for sending and receiving JSON data.

license: MIT-style license.

requires: [Request, JSON]

provides: Request.JSON

...
*/

Request.JSON = new Class({

    Extends: Request,

    options: {
        /*onError: function(text, error){},*/
        secure: true
    },

    initialize: function(options){
        this.parent(options);
        Object.append(this.headers, {
            'Accept': 'application/json',
            //	'X-Request': 'JSON'
        });
    },

    success: function(text){
        var json;
        try {
            json = this.response.json = JSON.decode(text, this.options.secure);
        } catch (error){
            this.fireEvent('error', [text, error]);
            return;
        }
        if (json == null){
            this.failure();
        } else {
            this.onSuccess(json, text);
            this.resolve({json: json, text: text});
        }
    }

});

/*
---

name: Cookie

description: Class for creating, reading, and deleting browser Cookies.

license: MIT-style license.

credits:
  - Based on the functions by Peter-Paul Koch (http://quirksmode.org).

requires: [Options, Browser]

provides: Cookie

...
*/

var Cookie = new Class({

    Implements: Options,

    options: {
        path: '/',
        domain: false,
        duration: false,
        secure: false,
        document: document,
        encode: true,
        httpOnly: false
    },

    initialize: function(key, options){
        this.key = key;
        this.setOptions(options);
    },

    write: function(value){
        if (this.options.encode) value = encodeURIComponent(value);
        if (this.options.domain) value += '; domain=' + this.options.domain;
        if (this.options.path) value += '; path=' + this.options.path;
        if (this.options.duration){
            var date = new Date();
            date.setTime(date.getTime() + this.options.duration * 24 * 60 * 60 * 1000);
            value += '; expires=' + date.toGMTString();
        }
        if (this.options.secure) value += '; secure';
        if (this.options.httpOnly) value += '; HttpOnly';
        this.options.document.cookie = this.key + '=' + value;
        return this;
    },

    read: function(){
        var value = this.options.document.cookie.match('(?:^|;)\\s*' + this.key.escapeRegExp() + '=([^;]*)');
        return (value) ? decodeURIComponent(value[1]) : null;
    },

    dispose: function(){
        new Cookie(this.key, Object.merge({}, this.options, {duration: -1})).write('');
        return this;
    }

});

Cookie.write = function(key, value, options){
    return new Cookie(key, options).write(value);
};

Cookie.read = function(key){
    return new Cookie(key).read();
};

Cookie.dispose = function(key, options){
    return new Cookie(key, options).dispose();
};

/*
---

name: DOMReady

description: Contains the custom event domready.

license: MIT-style license.

requires: [Browser, Element, Element.Event]

provides: [DOMReady, DomReady]

...
*/

(function(window, document){

    var ready,
        loaded,
        checks = [],
        shouldPoll,
        timer,
        testElement = document.createElement('div');

    var domready = function(){
        clearTimeout(timer);
        if (!ready){
            Browser.loaded = ready = true;
            document.removeListener('DOMContentLoaded', domready).removeListener('readystatechange', check);
            document.fireEvent('domready');
            window.fireEvent('domready');
        }
        // cleanup scope vars
        document = window = testElement = null;
    };

    var check = function(){
        for (var i = checks.length; i--;) if (checks[i]()){
            domready();
            return true;
        }
        return false;
    };

    var poll = function(){
        clearTimeout(timer);
        if (!check()) timer = setTimeout(poll, 10);
    };

    document.addListener('DOMContentLoaded', domready);

    /*<ltIE8>*/
// doScroll technique by Diego Perini http://javascript.nwbox.com/IEContentLoaded/
// testElement.doScroll() throws when the DOM is not ready, only in the top window
    var doScrollWorks = function(){
        try {
            testElement.doScroll();
            return true;
        } catch (e){}
        return false;
    };
// If doScroll works already, it can't be used to determine domready
//   e.g. in an iframe
    if (testElement.doScroll && !doScrollWorks()){
        checks.push(doScrollWorks);
        shouldPoll = true;
    }
    /*</ltIE8>*/

    if (document.readyState) checks.push(function(){
        var state = document.readyState;
        return (state == 'loaded' || state == 'complete');
    });

    if ('onreadystatechange' in document) document.addListener('readystatechange', check);
    else shouldPoll = true;

    if (shouldPoll) poll();

    Element.Events.domready = {
        onAdd: function(fn){
            if (ready) fn.call(this);
        }
    };

// Make sure that domready fires before load
    Element.Events.load = {
        base: 'load',
        onAdd: function(fn){
            if (loaded && this == window) fn.call(this);
        },
        condition: function(){
            if (this == window){
                domready();
                delete Element.Events.load;
            }
            return true;
        }
    };

// This is based on the custom load event
    window.addEvent('load', function(){
        loaded = true;
    });

})(window, document);

/*
---

script: More.js

name: More

description: MooTools More

license: MIT-style license

authors:
  - Guillermo Rauch
  - Thomas Aylott
  - Scott Kyle
  - Arian Stolwijk
  - Tim Wienk
  - Christoph Pojer
  - Aaron Newton
  - Jacob Thornton

requires:
  - Core/MooTools

provides: [MooTools.More]

...
*/

MooTools.More = {
    version: '1.6.0',
    build: '45b71db70f879781a7e0b0d3fb3bb1307c2521eb'
};

/*
---

script: Chain.Wait.js

name: Chain.Wait

description: value, Adds a method to inject pauses between chained events.

license: MIT-style license.

authors:
  - Aaron Newton

requires:
  - Core/Chain
  - Core/Element
  - Core/Fx
  - MooTools.More

provides: [Chain.Wait]

...
*/

(function(){

    var wait = {
        wait: function(duration){
            return this.chain(function(){
                this.callChain.delay(duration == null ? 500 : duration, this);
                return this;
            }.bind(this));
        }
    };

    Chain.implement(wait);

    if (this.Fx) Fx.implement(wait);

    if (this.Element && Element.implement && this.Fx){
        Element.implement({

            chains: function(effects){
                Array.convert(effects || ['tween', 'morph', 'reveal']).each(function(effect){
                    effect = this.get(effect);
                    if (!effect) return;
                    effect.setOptions({
                        link:'chain'
                    });
                }, this);
                return this;
            },

            pauseFx: function(duration, effect){
                this.chains(effect).get(effect || 'tween').wait(duration);
                return this;
            }

        });
    }

})();

/*
---

script: Class.Binds.js

name: Class.Binds

description: Automagically binds specified methods in a class to the instance of the class.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Core/Class
  - MooTools.More

provides: [Class.Binds]

...
*/

Class.Mutators.Binds = function(binds){
    if (!this.prototype.initialize) this.implement('initialize', function(){});
    return Array.convert(binds).concat(this.prototype.Binds || []);
};

Class.Mutators.initialize = function(initialize){
    return function(){
        Array.convert(this.Binds).each(function(name){
            var original = this[name];
            if (original) this[name] = original.bind(this);
        }, this);
        return initialize.apply(this, arguments);
    };
};

/*
---

script: Class.Occlude.js

name: Class.Occlude

description: Prevents a class from being applied to a DOM element twice.

license: MIT-style license.

authors:
  - Aaron Newton

requires:
  - Core/Class
  - Core/Element
  - MooTools.More

provides: [Class.Occlude]

...
*/

Class.Occlude = new Class({

    occlude: function(property, element){
        element = document.id(element || this.element);
        var instance = element.retrieve(property || this.property);
        if (instance && !this.occluded)
            return (this.occluded = instance);

        this.occluded = false;
        element.store(property || this.property, this);
        return this.occluded;
    }

});

/*
---

script: Class.Refactor.js

name: Class.Refactor

description: Extends a class onto itself with new property, preserving any items attached to the class's namespace.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Core/Class
  - MooTools.More

# Some modules declare themselves dependent on Class.Refactor
provides: [Class.refactor, Class.Refactor]

...
*/

Class.refactor = function(original, refactors){

    Object.each(refactors, function(item, name){
        var origin = original.prototype[name];
        origin = (origin && origin.$origin) || origin || function(){};
        original.implement(name, (typeof item == 'function') ? function(){
            var old = this.previous;
            this.previous = origin;
            var value = item.apply(this, arguments);
            this.previous = old;
            return value;
        } : item);
    });

    return original;

};

/*
---

script: Class.Singleton.js

name: Class.Singleton

description: Always provides a single instance of a class

license: MIT-style license.

authors:
  - Hristo Chakarov

requires:
  - Core/Class

provides: [Class.Singleton]

...
*/

Class.Singleton = new Class({

    initialize : function(descriptor){
        // here we keep reference of the single instance
        var singleton;
        // create a regular Class
        var constructor = new Class(descriptor);
        // We return another constructor, because we need to make sure that we
        // always return the same one and only instance.
        return function(){
            if (singleton){
                return singleton;
            }
            // Obviously we instantiate that class for the first time.
            // Create brand new object & extend it with the prototype of the
            // original `constructor`.
            singleton = Object.append({}, constructor.prototype);
            singleton.constructor = constructor;
            // We also need to call the constructor as a function, passing the
            // arguments object.
            var returnValue = constructor.apply(singleton, arguments);
            // In case the `constructor` returns something other than `this` -
            // return that value; otherwise return the `singleton`.
            singleton = typeof returnValue == 'object' ? returnValue : singleton;
            return singleton;
        };
    }

});

/*
---

name: Events.Pseudos

description: Adds the functionality to add pseudo events

license: MIT-style license

authors:
  - Arian Stolwijk

requires: [Core/Class.Extras, Core/Slick.Parser, MooTools.More]

provides: [Events.Pseudos]

...
*/

(function(){

    Events.Pseudos = function(pseudos, addEvent, removeEvent){

        var storeKey = '_monitorEvents:';

        var storageOf = function(object){
            return {
                store: object.store ? function(key, value){
                    object.store(storeKey + key, value);
                } : function(key, value){
                    (object._monitorEvents || (object._monitorEvents = {}))[key] = value;
                },
                retrieve: object.retrieve ? function(key, dflt){
                    return object.retrieve(storeKey + key, dflt);
                } : function(key, dflt){
                    if (!object._monitorEvents) return dflt;
                    return object._monitorEvents[key] || dflt;
                }
            };
        };

        var splitType = function(type){
            if (type.indexOf(':') == -1 || !pseudos) return null;

            var parsed = Slick.parse(type).expressions[0][0],
                parsedPseudos = parsed.pseudos,
                l = parsedPseudos.length,
                splits = [];

            while (l--){
                var pseudo = parsedPseudos[l].key,
                    listener = pseudos[pseudo];
                if (listener != null) splits.push({
                    event: parsed.tag,
                    value: parsedPseudos[l].value,
                    pseudo: pseudo,
                    original: type,
                    listener: listener
                });
            }
            return splits.length ? splits : null;
        };

        return {

            addEvent: function(type, fn, internal){
                var split = splitType(type);
                if (!split) return addEvent.call(this, type, fn, internal);

                var storage = storageOf(this),
                    events = storage.retrieve(type, []),
                    eventType = split[0].event,
                    args = Array.slice(arguments, 2),
                    stack = fn,
                    self = this;

                split.each(function(item){
                    var listener = item.listener,
                        stackFn = stack;
                    if (listener == false) eventType += ':' + item.pseudo + '(' + item.value + ')';
                    else stack = function(){
                        listener.call(self, item, stackFn, arguments, stack);
                    };
                });

                events.include({type: eventType, event: fn, monitor: stack});
                storage.store(type, events);

                if (type != eventType) addEvent.apply(this, [type, fn].concat(args));
                return addEvent.apply(this, [eventType, stack].concat(args));
            },

            removeEvent: function(type, fn){
                var split = splitType(type);
                if (!split) return removeEvent.call(this, type, fn);

                var storage = storageOf(this),
                    events = storage.retrieve(type);
                if (!events) return this;

                var args = Array.slice(arguments, 2);

                removeEvent.apply(this, [type, fn].concat(args));
                events.each(function(monitor, i){
                    if (!fn || monitor.event == fn) removeEvent.apply(this, [monitor.type, monitor.monitor].concat(args));
                    delete events[i];
                }, this);

                storage.store(type, events);
                return this;
            }

        };

    };

    var pseudos = {

        once: function(split, fn, args, monitor){
            fn.apply(this, args);
            this.removeEvent(split.event, monitor)
                .removeEvent(split.original, fn);
        },

        throttle: function(split, fn, args){
            if (!fn._throttled){
                fn.apply(this, args);
                fn._throttled = setTimeout(function(){
                    fn._throttled = false;
                }, split.value || 250);
            }
        },

        pause: function(split, fn, args){
            clearTimeout(fn._pause);
            fn._pause = fn.delay(split.value || 250, this, args);
        }

    };

    Events.definePseudo = function(key, listener){
        pseudos[key] = listener;
        return this;
    };

    Events.lookupPseudo = function(key){
        return pseudos[key];
    };

    var proto = Events.prototype;
    Events.implement(Events.Pseudos(pseudos, proto.addEvent, proto.removeEvent));

    ['Request', 'Fx'].each(function(klass){
        if (this[klass]) this[klass].implement(Events.prototype);
    });

})();

/*
---

script: Drag.js

name: Drag

description: The base Drag Class. Can be used to drag and resize Elements using mouse events.

license: MIT-style license

authors:
  - Valerio Proietti
  - Tom Occhinno
  - Jan Kassens

requires:
  - Core/Events
  - Core/Options
  - Core/Element.Event
  - Core/Element.Style
  - Core/Element.Dimensions
  - MooTools.More

provides: [Drag]
...

*/
(function(){

    var Drag = this.Drag = new Class({

        Implements: [Events, Options],

        options: {/*
		onBeforeStart: function(thisElement){},
		onStart: function(thisElement, event){},
		onSnap: function(thisElement){},
		onDrag: function(thisElement, event){},
		onCancel: function(thisElement){},
		onComplete: function(thisElement, event){},*/
            snap: 6,
            unit: 'px',
            grid: false,
            style: true,
            limit: false,
            handle: false,
            invert: false,
            unDraggableTags: ['button', 'input', 'a', 'textarea', 'select', 'option'],
            preventDefault: false,
            stopPropagation: false,
            compensateScroll: false,
            modifiers: {x: 'left', y: 'top'}
        },

        initialize: function(){
            var params = Array.link(arguments, {
                'options': Type.isObject,
                'element': function(obj){
                    return obj != null;
                }
            });

            this.element = document.id(params.element);
            this.document = this.element.getDocument();
            this.setOptions(params.options || {});
            var htype = typeOf(this.options.handle);
            this.handles = ((htype == 'array' || htype == 'collection') ? $$(this.options.handle) : document.id(this.options.handle)) || this.element;
            this.mouse = {'now': {}, 'pos': {}};
            this.value = {'start': {}, 'now': {}};
            this.offsetParent = (function(el){
                var offsetParent = el.getOffsetParent();
                var isBody = !offsetParent || (/^(?:body|html)$/i).test(offsetParent.tagName);
                return isBody ? window : document.id(offsetParent);
            })(this.element);
            this.selection = 'selectstart' in document ? 'selectstart' : 'mousedown';

            this.compensateScroll = {start: {}, diff: {}, last: {}};

            if ('ondragstart' in document && !('FileReader' in window) && !Drag.ondragstartFixed){
                document.ondragstart = Function.convert(false);
                Drag.ondragstartFixed = true;
            }

            this.bound = {
                start: this.start.bind(this),
                check: this.check.bind(this),
                drag: this.drag.bind(this),
                stop: this.stop.bind(this),
                cancel: this.cancel.bind(this),
                eventStop: Function.convert(false),
                scrollListener: this.scrollListener.bind(this)
            };
            this.attach();
        },

        attach: function(){
            this.handles.addEvent('mousedown', this.bound.start);
            this.handles.addEvent('touchstart', this.bound.start);
            if (this.options.compensateScroll) this.offsetParent.addEvent('scroll', this.bound.scrollListener);
            return this;
        },

        detach: function(){
            this.handles.removeEvent('mousedown', this.bound.start);
            this.handles.removeEvent('touchstart', this.bound.start);
            if (this.options.compensateScroll) this.offsetParent.removeEvent('scroll', this.bound.scrollListener);
            return this;
        },

        scrollListener: function(){

            if (!this.mouse.start) return;
            var newScrollValue = this.offsetParent.getScroll();

            if (this.element.getStyle('position') == 'absolute'){
                var scrollDiff = this.sumValues(newScrollValue, this.compensateScroll.last, -1);
                this.mouse.now = this.sumValues(this.mouse.now, scrollDiff, 1);
            } else {
                this.compensateScroll.diff = this.sumValues(newScrollValue, this.compensateScroll.start, -1);
            }
            if (this.offsetParent != window) this.compensateScroll.diff = this.sumValues(this.compensateScroll.start, newScrollValue, -1);
            this.compensateScroll.last = newScrollValue;
            this.render(this.options);
        },

        sumValues: function(alpha, beta, op){
            var sum = {}, options = this.options;
            for (var z in options.modifiers){
                if (!options.modifiers[z]) continue;
                sum[z] = alpha[z] + beta[z] * op;
            }
            return sum;
        },

        start: function(event){
            if (this.options.unDraggableTags.contains(event.target.get('tag'))) return;

            var options = this.options;

            if (event.rightClick) return;

            if (options.preventDefault) event.preventDefault();
            if (options.stopPropagation) event.stopPropagation();
            this.compensateScroll.start = this.compensateScroll.last = this.offsetParent.getScroll();
            this.compensateScroll.diff = {x: 0, y: 0};
            this.mouse.start = event.page;
            this.fireEvent('beforeStart', this.element);

            var limit = options.limit;
            this.limit = {x: [], y: []};

            var z, coordinates, offsetParent = this.offsetParent == window ? null : this.offsetParent;
            for (z in options.modifiers){
                if (!options.modifiers[z]) continue;

                var style = this.element.getStyle(options.modifiers[z]);

                // Some browsers (IE and Opera) don't always return pixels.
                if (style && !style.match(/px$/)){
                    if (!coordinates) coordinates = this.element.getCoordinates(offsetParent);
                    style = coordinates[options.modifiers[z]];
                }

                if (options.style) this.value.now[z] = (style || 0).toInt();
                else this.value.now[z] = this.element[options.modifiers[z]];

                if (options.invert) this.value.now[z] *= -1;

                this.mouse.pos[z] = event.page[z] - this.value.now[z];

                if (limit && limit[z]){
                    var i = 2;
                    while (i--){
                        var limitZI = limit[z][i];
                        if (limitZI || limitZI === 0) this.limit[z][i] = (typeof limitZI == 'function') ? limitZI() : limitZI;
                    }
                }
            }

            if (typeOf(this.options.grid) == 'number') this.options.grid = {
                x: this.options.grid,
                y: this.options.grid
            };

            var events = {
                mousemove: this.bound.check,
                mouseup: this.bound.cancel,
                touchmove: this.bound.check,
                touchend: this.bound.cancel
            };
            events[this.selection] = this.bound.eventStop;
            this.document.addEvents(events);
        },

        check: function(event){
            if (this.options.preventDefault) event.preventDefault();
            var distance = Math.round(Math.sqrt(Math.pow(event.page.x - this.mouse.start.x, 2) + Math.pow(event.page.y - this.mouse.start.y, 2)));
            if (distance > this.options.snap){
                this.cancel();
                this.document.addEvents({
                    mousemove: this.bound.drag,
                    mouseup: this.bound.stop,
                    touchmove: this.bound.drag,
                    touchend: this.bound.stop
                });
                this.fireEvent('start', [this.element, event]).fireEvent('snap', this.element);
            }
        },

        drag: function(event){
            var options = this.options;
            if (options.preventDefault) event.preventDefault();
            this.mouse.now = this.sumValues(event.page, this.compensateScroll.diff, -1);

            this.render(options);
            this.fireEvent('drag', [this.element, event]);
        },

        render: function(options){
            for (var z in options.modifiers){
                if (!options.modifiers[z]) continue;
                this.value.now[z] = this.mouse.now[z] - this.mouse.pos[z];

                if (options.invert) this.value.now[z] *= -1;
                if (options.limit && this.limit[z]){
                    if ((this.limit[z][1] || this.limit[z][1] === 0) && (this.value.now[z] > this.limit[z][1])){
                        this.value.now[z] = this.limit[z][1];
                    } else if ((this.limit[z][0] || this.limit[z][0] === 0) && (this.value.now[z] < this.limit[z][0])){
                        this.value.now[z] = this.limit[z][0];
                    }
                }
                if (options.grid[z]) this.value.now[z] -= ((this.value.now[z] - (this.limit[z][0]||0)) % options.grid[z]);
                if (options.style) this.element.setStyle(options.modifiers[z], this.value.now[z] + options.unit);
                else this.element[options.modifiers[z]] = this.value.now[z];
            }
        },

        cancel: function(event){
            this.document.removeEvents({
                mousemove: this.bound.check,
                mouseup: this.bound.cancel,
                touchmove: this.bound.check,
                touchend: this.bound.cancel
            });
            if (event){
                this.document.removeEvent(this.selection, this.bound.eventStop);
                this.fireEvent('cancel', this.element);
            }
        },

        stop: function(event){
            var events = {
                mousemove: this.bound.drag,
                mouseup: this.bound.stop,
                touchmove: this.bound.drag,
                touchend: this.bound.stop
            };
            events[this.selection] = this.bound.eventStop;
            this.document.removeEvents(events);
            this.mouse.start = null;
            if (event) this.fireEvent('complete', [this.element, event]);
        }

    });

})();


Element.implement({

    makeResizable: function(options){
        var drag = new Drag(this, Object.merge({
            modifiers: {
                x: 'width',
                y: 'height'
            }
        }, options));

        this.store('resizer', drag);
        return drag.addEvent('drag', function(){
            this.fireEvent('resize', drag);
        }.bind(this));
    }

});

/*
---

script: Drag.Move.js

name: Drag.Move

description: A Drag extension that provides support for the constraining of draggables to containers and droppables.

license: MIT-style license

authors:
  - Valerio Proietti
  - Tom Occhinno
  - Jan Kassens
  - Aaron Newton
  - Scott Kyle

requires:
  - Core/Element.Dimensions
  - Drag

provides: [Drag.Move]

...
*/

Drag.Move = new Class({

    Extends: Drag,

    options: {/*
		onEnter: function(thisElement, overed){},
		onLeave: function(thisElement, overed){},
		onDrop: function(thisElement, overed, event){},*/
        droppables: [],
        container: false,
        precalculate: false,
        includeMargins: true,
        checkDroppables: true
    },

    initialize: function(element, options){
        this.parent(element, options);
        element = this.element;

        this.droppables = $$(this.options.droppables);
        this.setContainer(this.options.container);

        if (this.options.style){
            if (this.options.modifiers.x == 'left' && this.options.modifiers.y == 'top'){
                var parent = element.getOffsetParent(),
                    styles = element.getStyles('left', 'top');
                if (parent && (styles.left == 'auto' || styles.top == 'auto')){
                    element.setPosition(element.getPosition(parent));
                }
            }

            if (element.getStyle('position') == 'static') element.setStyle('position', 'absolute');
        }

        this.addEvent('start', this.checkDroppables, true);
        this.overed = null;
    },

    setContainer: function(container){
        this.container = document.id(container);
        if (this.container && typeOf(this.container) != 'element'){
            this.container = document.id(this.container.getDocument().body);
        }
    },

    start: function(event){
        if (this.container) this.options.limit = this.calculateLimit();

        if (this.options.precalculate){
            this.positions = this.droppables.map(function(el){
                return el.getCoordinates();
            });
        }

        this.parent(event);
    },

    calculateLimit: function(){
        var element = this.element,
            container = this.container,

            offsetParent = document.id(element.getOffsetParent()) || document.body,
            containerCoordinates = container.getCoordinates(offsetParent),
            elementMargin = {},
            elementBorder = {},
            containerMargin = {},
            containerBorder = {},
            offsetParentPadding = {},
            offsetScroll = offsetParent.getScroll();

        ['top', 'right', 'bottom', 'left'].each(function(pad){
            elementMargin[pad] = element.getStyle('margin-' + pad).toInt();
            elementBorder[pad] = element.getStyle('border-' + pad).toInt();
            containerMargin[pad] = container.getStyle('margin-' + pad).toInt();
            containerBorder[pad] = container.getStyle('border-' + pad).toInt();
            offsetParentPadding[pad] = offsetParent.getStyle('padding-' + pad).toInt();
        }, this);

        var width = element.offsetWidth + elementMargin.left + elementMargin.right,
            height = element.offsetHeight + elementMargin.top + elementMargin.bottom,
            left = 0 + offsetScroll.x,
            top = 0 + offsetScroll.y,
            right = containerCoordinates.right - containerBorder.right - width + offsetScroll.x,
            bottom = containerCoordinates.bottom - containerBorder.bottom - height + offsetScroll.y;

        if (this.options.includeMargins){
            left += elementMargin.left;
            top += elementMargin.top;
        } else {
            right += elementMargin.right;
            bottom += elementMargin.bottom;
        }

        if (element.getStyle('position') == 'relative'){
            var coords = element.getCoordinates(offsetParent);
            coords.left -= element.getStyle('left').toInt();
            coords.top -= element.getStyle('top').toInt();

            left -= coords.left;
            top -= coords.top;
            if (container.getStyle('position') != 'relative'){
                left += containerBorder.left;
                top += containerBorder.top;
            }
            right += elementMargin.left - coords.left;
            bottom += elementMargin.top - coords.top;

            if (container != offsetParent){
                left += containerMargin.left + offsetParentPadding.left;
                if (!offsetParentPadding.left && left < 0) left = 0;
                top += offsetParent == document.body ? 0 : containerMargin.top + offsetParentPadding.top;
                if (!offsetParentPadding.top && top < 0) top = 0;
            }
        } else {
            left -= elementMargin.left;
            top -= elementMargin.top;
            if (container != offsetParent){
                left += containerCoordinates.left + containerBorder.left;
                top += containerCoordinates.top + containerBorder.top;
            }
        }

        return {
            x: [left, right],
            y: [top, bottom]
        };
    },

    getDroppableCoordinates: function(element){
        var position = element.getCoordinates();
        if (element.getStyle('position') == 'fixed'){
            var scroll = window.getScroll();
            position.left += scroll.x;
            position.right += scroll.x;
            position.top += scroll.y;
            position.bottom += scroll.y;
        }
        return position;
    },
    //modify by Tommy
    checkDroppables:function(){
        var overedList = this.droppables.filter(function(el, i){
            el = this.positions ? this.positions[i] : this.getDroppableCoordinates(el);
            var now = this.mouse.now;
            return (now.x > el.left && now.x < el.right && now.y < el.bottom && now.y > el.top);
        }, this);
        var overed = overedList.getLast();
        if (overed) for (var x=0;x<overedList.length-1;x++) if(overed.contains(overedList[x])) overed=overedList[x];

        if(this.overed!=overed){
            if(this.overed) this.fireEvent("leave",[this.element,this.overed]);
            if(overed) this.fireEvent("enter",[this.element,overed]);
            this.overed=overed;
        }
    },
    //mootools....
    //checkDroppables: function(){
    //	var overed = this.droppables.filter(function(el, i){
    //		el = this.positions ? this.positions[i] : this.getDroppableCoordinates(el);
    //		var now = this.mouse.now;
    //		return (now.x > el.left && now.x < el.right && now.y < el.bottom && now.y > el.top);
    //	}, this).getLast();
    //
    //	if (this.overed != overed){
    //		if (this.overed) this.fireEvent('leave', [this.element, this.overed]);
    //		if (overed) this.fireEvent('enter', [this.element, overed]);
    //		this.overed = overed;
    //	}
    //},

    drag: function(event){
        this.parent(event);
        if (this.options.checkDroppables && this.droppables.length) this.checkDroppables();
    },

    stop: function(event){
        this.checkDroppables();
        this.fireEvent('drop', [this.element, this.overed, event]);
        this.overed = null;
        return this.parent(event);
    }

});

Element.implement({

    makeDraggable: function(options){
        var drag = new Drag.Move(this, options);
        this.store('dragger', drag);
        return drag;
    }

});

/*
---

script: Element.Measure.js

name: Element.Measure

description: Extends the Element native object to include methods useful in measuring dimensions.

credits: "Element.measure / .expose methods by Daniel Steigerwald License: MIT-style license. Copyright: Copyright (c) 2008 Daniel Steigerwald, daniel.steigerwald.cz"

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Core/Element.Style
  - Core/Element.Dimensions
  - MooTools.More

provides: [Element.Measure]

...
*/

(function(){

    var getStylesList = function(styles, planes){
        var list = [];
        Object.each(planes, function(directions){
            Object.each(directions, function(edge){
                styles.each(function(style){
                    list.push(style + '-' + edge + (style == 'border' ? '-width' : ''));
                });
            });
        });
        return list;
    };

    var calculateEdgeSize = function(edge, styles){
        var total = 0;
        Object.each(styles, function(value, style){
            if (style.test(edge)) total = total + value.toInt();
        });
        return total;
    };

    var isVisible = function(el){
        return !!(!el || el.offsetHeight || el.offsetWidth);
    };


    Element.implement({

        measure: function(fn){
            if (isVisible(this)) return fn.call(this);
            var parent = this.getParent(),
                toMeasure = [];
            while (!isVisible(parent) && parent != document.body){
                toMeasure.push(parent.expose());
                parent = parent.getParent();
            }
            var restore = this.expose(),
                result = fn.call(this);
            restore();
            toMeasure.each(function(restore){
                restore();
            });
            return result;
        },

        expose: function(){
            if (this.getStyle('display') != 'none') return function(){};
            var before = this.style.cssText;
            this.setStyles({
                display: 'block',
                position: 'absolute',
                visibility: 'hidden'
            });
            return function(){
                this.style.cssText = before;
            }.bind(this);
        },

        getDimensions: function(options){
            options = Object.merge({computeSize: false}, options);
            var dim = {x: 0, y: 0};

            var getSize = function(el, options){
                return (options.computeSize) ? el.getComputedSize(options) : el.getSize();
            };

            var parent = this.getParent('body');

            if (parent && this.getStyle('display') == 'none'){
                dim = this.measure(function(){
                    return getSize(this, options);
                });
            } else if (parent){
                try { //safari sometimes crashes here, so catch it
                    dim = getSize(this, options);
                } catch (e){}
            }

            return Object.append(dim, (dim.x || dim.x === 0) ? {
                width: dim.x,
                height: dim.y
            } : {
                x: dim.width,
                y: dim.height
            });
        },

        getComputedSize: function(options){
            //<1.2compat>
            //legacy support for my stupid spelling error
            if (options && options.plains) options.planes = options.plains;
            //</1.2compat>

            options = Object.merge({
                styles: ['padding','border'],
                planes: {
                    height: ['top','bottom'],
                    width: ['left','right']
                },
                mode: 'both'
            }, options);

            var styles = {},
                size = {width: 0, height: 0},
                dimensions;

            if (options.mode == 'vertical'){
                delete size.width;
                delete options.planes.width;
            } else if (options.mode == 'horizontal'){
                delete size.height;
                delete options.planes.height;
            }

            getStylesList(options.styles, options.planes).each(function(style){
                styles[style] = this.getStyle(style).toInt();
            }, this);

            Object.each(options.planes, function(edges, plane){

                var capitalized = plane.capitalize(),
                    style = this.getStyle(plane);

                if (style == 'auto' && !dimensions) dimensions = this.getDimensions();

                style = styles[plane] = (style == 'auto') ? dimensions[plane] : style.toInt();
                size['total' + capitalized] = style;

                edges.each(function(edge){
                    var edgesize = calculateEdgeSize(edge, styles);
                    size['computed' + edge.capitalize()] = edgesize;
                    size['total' + capitalized] += edgesize;
                });

            }, this);

            return Object.append(size, styles);
        }

    });

})();

/*
---

script: Slider.js

name: Slider

description: Class for creating horizontal and vertical slider controls.

license: MIT-style license

authors:
  - Valerio Proietti

requires:
  - Core/Element.Dimensions
  - Core/Number
  - Class.Binds
  - Drag
  - Element.Measure

provides: [Slider]

...
*/
(function(){

    var Slider = this.Slider = new Class({

        Implements: [Events, Options],

        Binds: ['clickedElement', 'draggedKnob', 'scrolledElement'],

        options: {/*
		onTick: function(intPosition){},
		onMove: function(){},
		onChange: function(intStep){},
		onComplete: function(strStep){},*/
            onTick: function(position){
                this.setKnobPosition(position);
            },
            initialStep: 0,
            snap: false,
            offset: 0,
            range: false,
            wheel: false,
            steps: 100,
            mode: 'horizontal'
        },

        initialize: function(element, knob, options){
            this.setOptions(options);
            options = this.options;
            this.element = document.id(element);
            knob = this.knob = document.id(knob);
            this.previousChange = this.previousEnd = this.step = options.initialStep ? options.initialStep : options.range ? options.range[0] : 0;

            var limit = {},
                modifiers = {x: false, y: false};

            switch (options.mode){
                case 'vertical':
                    this.axis = 'y';
                    this.property = 'top';
                    this.offset = 'offsetHeight';
                    break;
                case 'horizontal':
                    this.axis = 'x';
                    this.property = 'left';
                    this.offset = 'offsetWidth';
            }

            this.setSliderDimensions();
            this.setRange(options.range, null, true);

            if (knob.getStyle('position') == 'static') knob.setStyle('position', 'relative');
            knob.setStyle(this.property, -options.offset);
            modifiers[this.axis] = this.property;
            limit[this.axis] = [-options.offset, this.full - options.offset];

            var dragOptions = {
                snap: 0,
                limit: limit,
                modifiers: modifiers,
                onDrag: this.draggedKnob,
                onStart: this.draggedKnob,
                onBeforeStart: (function(){
                    this.isDragging = true;
                }).bind(this),
                onCancel: function(){
                    this.isDragging = false;
                }.bind(this),
                onComplete: function(){
                    this.isDragging = false;
                    this.draggedKnob();
                    this.end();
                }.bind(this)
            };
            if (options.snap) this.setSnap(dragOptions);

            this.drag = new Drag(knob, dragOptions);
            if (options.initialStep != null) this.set(options.initialStep, true);
            this.attach();
        },

        attach: function(){
            this.element.addEvent('mousedown', this.clickedElement);
            if (this.options.wheel) this.element.addEvent('mousewheel', this.scrolledElement);
            this.drag.attach();
            return this;
        },

        detach: function(){
            this.element.removeEvent('mousedown', this.clickedElement)
                .removeEvent('mousewheel', this.scrolledElement);
            this.drag.detach();
            return this;
        },

        autosize: function(){
            this.setSliderDimensions()
                .setKnobPosition(this.toPosition(this.step));
            this.drag.options.limit[this.axis] = [-this.options.offset, this.full - this.options.offset];
            if (this.options.snap) this.setSnap();
            return this;
        },

        setSnap: function(options){
            if (!options) options = this.drag.options;
            options.grid = Math.ceil(this.stepWidth);
            options.limit[this.axis][1] = this.element[this.offset];
            return this;
        },

        setKnobPosition: function(position){
            if (this.options.snap) position = this.toPosition(this.step);
            this.knob.setStyle(this.property, position);
            return this;
        },

        setSliderDimensions: function(){
            this.full = this.element.measure(function(){
                this.half = this.knob[this.offset] / 2;
                return this.element[this.offset] - this.knob[this.offset] + (this.options.offset * 2);
            }.bind(this));
            return this;
        },

        set: function(step, silently){
            if (!((this.range > 0) ^ (step < this.min))) step = this.min;
            if (!((this.range > 0) ^ (step > this.max))) step = this.max;

            this.step = (step).round(this.modulus.decimalLength);
            if (silently) this.checkStep().setKnobPosition(this.toPosition(this.step));
            else this.checkStep().fireEvent('tick', this.toPosition(this.step)).fireEvent('move').end();
            return this;
        },

        setRange: function(range, pos, silently){
            this.min = Array.pick([range[0], 0]);
            this.max = Array.pick([range[1], this.options.steps]);
            this.range = this.max - this.min;
            this.steps = this.options.steps || this.full;
            var stepSize = this.stepSize = Math.abs(this.range) / this.steps;
            this.stepWidth = this.stepSize * this.full / Math.abs(this.range);
            this.setModulus();

            if (range) this.set(Array.pick([pos, this.step]).limit(this.min,this.max), silently);
            return this;
        },

        setModulus: function(){
            var decimals = ((this.stepSize + '').split('.')[1] || []).length,
                modulus = 1 + '';
            while (decimals--) modulus += '0';
            this.modulus = {multiplier: (modulus).toInt(10), decimalLength: modulus.length - 1};
        },

        clickedElement: function(event){
            if (this.isDragging || event.target == this.knob) return;

            var dir = this.range < 0 ? -1 : 1,
                position = event.page[this.axis] - this.element.getPosition()[this.axis] - this.half;

            position = position.limit(-this.options.offset, this.full - this.options.offset);

            this.step = (this.min + dir * this.toStep(position)).round(this.modulus.decimalLength);

            this.checkStep()
                .fireEvent('tick', position)
                .fireEvent('move')
                .end();
        },

        scrolledElement: function(event){
            var mode = (this.options.mode == 'horizontal') ? (event.wheel < 0) : (event.wheel > 0);
            this.set(this.step + (mode ? -1 : 1) * this.stepSize);
            event.stop();
        },

        draggedKnob: function(){
            var dir = this.range < 0 ? -1 : 1,
                position = this.drag.value.now[this.axis];

            position = position.limit(-this.options.offset, this.full -this.options.offset);

            this.step = (this.min + dir * this.toStep(position)).round(this.modulus.decimalLength);
            this.checkStep();
            this.fireEvent('move');
        },

        checkStep: function(){
            var step = this.step;
            if (this.previousChange != step){
                this.previousChange = step;
                this.fireEvent('change', step);
            }
            return this;
        },

        end: function(){
            var step = this.step;
            if (this.previousEnd !== step){
                this.previousEnd = step;
                this.fireEvent('complete', step + '');
            }
            return this;
        },

        toStep: function(position){
            var step = (position + this.options.offset) * this.stepSize / this.full * this.steps;
            return this.options.steps ? (step - (step * this.modulus.multiplier) % (this.stepSize * this.modulus.multiplier) / this.modulus.multiplier).round(this.modulus.decimalLength) : step;
        },

        toPosition: function(step){
            return (this.full * Math.abs(this.min - step)) / (this.steps * this.stepSize) - this.options.offset || 0;
        }

    });

})();


/*
---

script: Sortables.js

name: Sortables

description: Class for creating a drag and drop sorting interface for lists of items.

license: MIT-style license

authors:
  - Tom Occhino

requires:
  - Core/Fx.Morph
  - Drag.Move

provides: [Sortables]

...
*/
(function(){

    var Sortables = this.Sortables = new Class({

        Implements: [Events, Options],

        options: {/*
		onSort: function(element, clone){},
		onStart: function(element, clone){},
		onComplete: function(element){},*/
            opacity: 1,
            clone: false,
            revert: false,
            handle: false,
            dragOptions: {},
            unDraggableTags: ['button', 'input', 'a', 'textarea', 'select', 'option']/*<1.2compat>*/,
            snap: 4,
            constrain: false,
            preventDefault: false
            /*</1.2compat>*/
        },

        initialize: function(lists, options){
            this.setOptions(options);

            this.elements = [];
            this.lists = [];
            this.idle = true;

            this.addLists($$(document.id(lists) || lists));

            if (!this.options.clone) this.options.revert = false;
            if (this.options.revert) this.effect = new Fx.Morph(null, Object.merge({
                duration: 250,
                link: 'cancel'
            }, this.options.revert));
        },

        attach: function(){
            this.addLists(this.lists);
            return this;
        },

        detach: function(){
            this.lists = this.removeLists(this.lists);
            return this;
        },

        addItems: function(){
            Array.flatten(arguments).each(function(element){
                this.elements.push(element);
                var start = element.retrieve('sortables:start', function(event){
                    this.start.call(this, event, element);
                }.bind(this));
                (this.options.handle ? element.getElement(this.options.handle) || element : element).addEvent('mousedown', start);
            }, this);
            return this;
        },

        addLists: function(){
            Array.flatten(arguments).each(function(list){
                this.lists.include(list);
                this.addItems(list.getChildren());
            }, this);
            return this;
        },

        removeItems: function(){
            return $$(Array.flatten(arguments).map(function(element){
                this.elements.erase(element);
                var start = element.retrieve('sortables:start');
                (this.options.handle ? element.getElement(this.options.handle) || element : element).removeEvent('mousedown', start);

                return element;
            }, this));
        },

        removeLists: function(){
            return $$(Array.flatten(arguments).map(function(list){
                this.lists.erase(list);
                this.removeItems(list.getChildren());

                return list;
            }, this));
        },

        getDroppableCoordinates: function(element){
            var offsetParent = element.getOffsetParent();
            var position = element.getPosition(offsetParent);
            var scroll = {
                w: window.getScroll(),
                offsetParent: offsetParent.getScroll()
            };
            position.x += scroll.offsetParent.x;
            position.y += scroll.offsetParent.y;

            if (offsetParent.getStyle('position') == 'fixed'){
                position.x -= scroll.w.x;
                position.y -= scroll.w.y;
            }

            return position;
        },

        getClone: function(event, element){
            if (!this.options.clone) return new Element(element.tagName).inject(document.body);
            if (typeOf(this.options.clone) == 'function') return this.options.clone.call(this, event, element, this.list);
            var clone = element.clone(true).setStyles({
                margin: 0,
                position: 'absolute',
                visibility: 'hidden',
                width: element.getStyle('width')
            }).addEvent('mousedown', function(event){
                element.fireEvent('mousedown', event);
            });
            //prevent the duplicated radio inputs from unchecking the real one
            if (clone.get('html').test('radio')){
                clone.getElements('input[type=radio]').each(function(input, i){
                    input.set('name', 'clone_' + i);
                    if (input.get('checked')) element.getElements('input[type=radio]')[i].set('checked', true);
                });
            }

            return clone.inject(this.list).setPosition(this.getDroppableCoordinates(this.element));
        },

        getDroppables: function(){
            var droppables = this.list.getChildren().erase(this.clone).erase(this.element);
            if (!this.options.constrain) droppables.append(this.lists).erase(this.list);
            return droppables;
        },

        insert: function(dragging, element){
            var where = 'inside';
            if (this.lists.contains(element)){
                this.list = element;
                this.drag.droppables = this.getDroppables();
            } else {
                where = this.element.getAllPrevious().contains(element) ? 'before' : 'after';
            }
            this.element.inject(element, where);
            this.fireEvent('sort', [this.element, this.clone]);
        },

        start: function(event, element){
            if (
                !this.idle ||
                event.rightClick ||
                (!this.options.handle && this.options.unDraggableTags.contains(event.target.get('tag')))
            ) return;

            this.idle = false;
            this.element = element;
            this.opacity = element.getStyle('opacity');
            this.list = element.getParent();
            this.clone = this.getClone(event, element);

            this.drag = new Drag.Move(this.clone, Object.merge({
                /*<1.2compat>*/
                preventDefault: this.options.preventDefault,
                snap: this.options.snap,
                container: this.options.constrain && this.element.getParent(),
                /*</1.2compat>*/
                droppables: this.getDroppables()
            }, this.options.dragOptions)).addEvents({
                onSnap: function(){
                    event.stop();
                    this.clone.setStyle('visibility', 'visible');
                    this.element.setStyle('opacity', this.options.opacity || 0);
                    this.fireEvent('start', [this.element, this.clone]);
                }.bind(this),
                onEnter: this.insert.bind(this),
                onCancel: this.end.bind(this),
                onComplete: this.end.bind(this)
            });

            this.clone.inject(this.element, 'before');
            this.drag.start(event);
        },

        end: function(){
            this.drag.detach();
            this.element.setStyle('opacity', this.opacity);
            var self = this;
            if (this.effect){
                var dim = this.element.getStyles('width', 'height'),
                    clone = this.clone,
                    pos = clone.computePosition(this.getDroppableCoordinates(clone));

                var destroy = function(){
                    this.removeEvent('cancel', destroy);
                    clone.destroy();
                    self.reset();
                };

                this.effect.element = clone;
                this.effect.start({
                    top: pos.top,
                    left: pos.left,
                    width: dim.width,
                    height: dim.height,
                    opacity: 0.25
                }).addEvent('cancel', destroy).chain(destroy);
            } else {
                this.clone.destroy();
                self.reset();
            }

        },

        reset: function(){
            this.idle = true;
            this.fireEvent('complete', this.element);
        },

        serialize: function(){
            var params = Array.link(arguments, {
                modifier: Type.isFunction,
                index: function(obj){
                    return obj != null;
                }
            });
            var serial = this.lists.map(function(list){
                return list.getChildren().map(params.modifier || function(element){
                    return element.get('id');
                }, this);
            }, this);

            var index = params.index;
            if (this.lists.length == 1) index = 0;
            return (index || index === 0) && index >= 0 && index < this.lists.length ? serial[index] : serial;
        }

    });

})();

/*
---

name: Element.Event.Pseudos

description: Adds the functionality to add pseudo events for Elements

license: MIT-style license

authors:
  - Arian Stolwijk

requires: [Core/Element.Event, Core/Element.Delegation, Events.Pseudos]

provides: [Element.Event.Pseudos, Element.Delegation.Pseudo]

...
*/

(function(){

    var pseudos = {relay: false},
        copyFromEvents = ['once', 'throttle', 'pause'],
        count = copyFromEvents.length;

    while (count--) pseudos[copyFromEvents[count]] = Events.lookupPseudo(copyFromEvents[count]);

    DOMEvent.definePseudo = function(key, listener){
        pseudos[key] = listener;
        return this;
    };

    var proto = Element.prototype;
    [Element, Window, Document].invoke('implement', Events.Pseudos(pseudos, proto.addEvent, proto.removeEvent));

})();

/*
---

name: Element.Event.Pseudos.Keys

description: Adds functionality fire events if certain keycombinations are pressed

license: MIT-style license

authors:
  - Arian Stolwijk

requires: [Element.Event.Pseudos]

provides: [Element.Event.Pseudos.Keys]

...
*/

(function(){

    var keysStoreKey = '$moo:keys-pressed',
        keysKeyupStoreKey = '$moo:keys-keyup';


    DOMEvent.definePseudo('keys', function(split, fn, args){

        var event = args[0],
            keys = [],
            pressed = this.retrieve(keysStoreKey, []),
            value = split.value;

        if (value != '+') keys.append(value.replace('++', function(){
            keys.push('+'); // shift++ and shift+++a
            return '';
        }).split('+'));
        else keys = ['+'];

        pressed.include(event.key);

        if (keys.every(function(key){
            return pressed.contains(key);
        })) fn.apply(this, args);

        this.store(keysStoreKey, pressed);

        if (!this.retrieve(keysKeyupStoreKey)){
            var keyup = function(event){
                (function(){
                    pressed = this.retrieve(keysStoreKey, []).erase(event.key);
                    this.store(keysStoreKey, pressed);
                }).delay(0, this); // Fix for IE
            };
            this.store(keysKeyupStoreKey, keyup).addEvent('keyup', keyup);
        }

    });

    DOMEvent.defineKeys({
        '16': 'shift',
        '17': 'control',
        '18': 'alt',
        '20': 'capslock',
        '33': 'pageup',
        '34': 'pagedown',
        '35': 'end',
        '36': 'home',
        '144': 'numlock',
        '145': 'scrolllock',
        '186': ';',
        '187': '=',
        '188': ',',
        '190': '.',
        '191': '/',
        '192': '`',
        '219': '[',
        '220': '\\',
        '221': ']',
        '222': "'",
        '107': '+',
        '109': '-', // subtract
        '189': '-'  // dash
    });

})();

/*
---

script: String.Extras.js

name: String.Extras

description: Extends the String native object to include methods useful in managing various kinds of strings (query strings, urls, html, etc).

license: MIT-style license

authors:
  - Aaron Newton
  - Guillermo Rauch
  - Christopher Pitt

requires:
  - Core/String
  - Core/Array
  - MooTools.More

provides: [String.Extras]

...
*/

(function(){

    var special = {
            'a': /[àáâãäåăą]/g,
            'A': /[ÀÁÂÃÄÅĂĄ]/g,
            'c': /[ćčç]/g,
            'C': /[ĆČÇ]/g,
            'd': /[ďđ]/g,
            'D': /[ĎÐ]/g,
            'e': /[èéêëěę]/g,
            'E': /[ÈÉÊËĚĘ]/g,
            'g': /[ğ]/g,
            'G': /[Ğ]/g,
            'i': /[ìíîï]/g,
            'I': /[ÌÍÎÏ]/g,
            'l': /[ĺľł]/g,
            'L': /[ĹĽŁ]/g,
            'n': /[ñňń]/g,
            'N': /[ÑŇŃ]/g,
            'o': /[òóôõöøő]/g,
            'O': /[ÒÓÔÕÖØ]/g,
            'r': /[řŕ]/g,
            'R': /[ŘŔ]/g,
            's': /[ššş]/g,
            'S': /[ŠŞŚ]/g,
            't': /[ťţ]/g,
            'T': /[ŤŢ]/g,
            'u': /[ùúûůüµ]/g,
            'U': /[ÙÚÛŮÜ]/g,
            'y': /[ÿý]/g,
            'Y': /[ŸÝ]/g,
            'z': /[žźż]/g,
            'Z': /[ŽŹŻ]/g,
            'th': /[þ]/g,
            'TH': /[Þ]/g,
            'dh': /[ð]/g,
            'DH': /[Ð]/g,
            'ss': /[ß]/g,
            'oe': /[œ]/g,
            'OE': /[Œ]/g,
            'ae': /[æ]/g,
            'AE': /[Æ]/g
        },

        tidy = {
            ' ': /[\xa0\u2002\u2003\u2009]/g,
            '*': /[\xb7]/g,
            '\'': /[\u2018\u2019]/g,
            '"': /[\u201c\u201d]/g,
            '...': /[\u2026]/g,
            '-': /[\u2013]/g,
            //	'--': /[\u2014]/g,
            '&raquo;': /[\uFFFD]/g
        },

        conversions = {
            ms: 1,
            s: 1000,
            m: 6e4,
            h: 36e5
        },

        findUnits = /(\d*.?\d+)([msh]+)/;

    var walk = function(string, replacements){
        var result = string, key;
        for (key in replacements) result = result.replace(replacements[key], key);
        return result;
    };

    var getRegexForTag = function(tag, contents){
        tag = tag || (contents ? '' : '\\w+');
        var regstr = contents ? '<' + tag + '(?!\\w)[^>]*>([\\s\\S]*?)<\/' + tag + '(?!\\w)>' : '<\/?' + tag + '\/?>|<' + tag + '[\\s|\/][^>]*>';
        return new RegExp(regstr, 'gi');
    };

    String.implement({

        standardize: function(){
            return walk(this, special);
        },

        repeat: function(times){
            return new Array(times + 1).join(this);
        },

        pad: function(length, str, direction){
            if (this.length >= length) return this;

            var pad = (str == null ? ' ' : '' + str)
                .repeat(length - this.length)
                .substr(0, length - this.length);

            if (!direction || direction == 'right') return this + pad;
            if (direction == 'left') return pad + this;

            return pad.substr(0, (pad.length / 2).floor()) + this + pad.substr(0, (pad.length / 2).ceil());
        },

        getTags: function(tag, contents){
            return this.match(getRegexForTag(tag, contents)) || [];
        },

        stripTags: function(tag, contents){
            return this.replace(getRegexForTag(tag, contents), '');
        },

        tidy: function(){
            return walk(this, tidy);
        },

        truncate: function(max, trail, atChar){
            var string = this;
            if (trail == null && arguments.length == 1) trail = '…';
            if (string.length > max){
                string = string.substring(0, max);
                if (atChar){
                    var index = string.lastIndexOf(atChar);
                    if (index != -1) string = string.substr(0, index);
                }
                if (trail) string += trail;
            }
            return string;
        },

        ms: function(){
            // "Borrowed" from https://gist.github.com/1503944
            var units = findUnits.exec(this);
            if (units == null) return Number(this);
            return Number(units[1]) * conversions[units[2]];
        }

    });

})();

/*
---

script: Element.Forms.js

name: Element.Forms

description: Extends the Element native object to include methods useful in managing inputs.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Core/Element
  - String.Extras
  - MooTools.More

provides: [Element.Forms]

...
*/

Element.implement({

    tidy: function(){
        this.set('value', this.get('value').tidy());
    },

    getTextInRange: function(start, end){
        return this.get('value').substring(start, end);
    },

    getSelectedText: function(){
        if (this.setSelectionRange) return this.getTextInRange(this.getSelectionStart(), this.getSelectionEnd());
        return document.selection.createRange().text;
    },

    getSelectedRange: function(){
        if (this.selectionStart != null){
            return {
                start: this.selectionStart,
                end: this.selectionEnd
            };
        }

        var pos = {
            start: 0,
            end: 0
        };
        var range = this.getDocument().selection.createRange();
        if (!range || range.parentElement() != this) return pos;
        var duplicate = range.duplicate();

        if (this.type == 'text'){
            pos.start = 0 - duplicate.moveStart('character', -100000);
            pos.end = pos.start + range.text.length;
        } else {
            var value = this.get('value');
            var offset = value.length;
            duplicate.moveToElementText(this);
            duplicate.setEndPoint('StartToEnd', range);
            if (duplicate.text.length) offset -= value.match(/[\n\r]*$/)[0].length;
            pos.end = offset - duplicate.text.length;
            duplicate.setEndPoint('StartToStart', range);
            pos.start = offset - duplicate.text.length;
        }
        return pos;
    },

    getSelectionStart: function(){
        return this.getSelectedRange().start;
    },

    getSelectionEnd: function(){
        return this.getSelectedRange().end;
    },

    setCaretPosition: function(pos){
        if (pos == 'end') pos = this.get('value').length;
        this.selectRange(pos, pos);
        return this;
    },

    getCaretPosition: function(){
        return this.getSelectedRange().start;
    },

    selectRange: function(start, end){
        if (this.setSelectionRange){
            this.focus();
            this.setSelectionRange(start, end);
        } else {
            var value = this.get('value');
            var diff = value.substr(start, end - start).replace(/\r/g, '').length;
            start = value.substr(0, start).replace(/\r/g, '').length;
            var range = this.createTextRange();
            range.collapse(true);
            range.moveEnd('character', start + diff);
            range.moveStart('character', start);
            range.select();
        }
        return this;
    },

    insertAtCursor: function(value, select){
        var pos = this.getSelectedRange();
        var text = this.get('value');
        this.set('value', text.substring(0, pos.start) + value + text.substring(pos.end, text.length));
        if (select !== false) this.selectRange(pos.start, pos.start + value.length);
        else this.setCaretPosition(pos.start + value.length);
        return this;
    },

    insertAroundCursor: function(options, select){
        options = Object.append({
            before: '',
            defaultMiddle: '',
            after: ''
        }, options);

        var value = this.getSelectedText() || options.defaultMiddle;
        var pos = this.getSelectedRange();
        var text = this.get('value');

        if (pos.start == pos.end){
            this.set('value', text.substring(0, pos.start) + options.before + value + options.after + text.substring(pos.end, text.length));
            this.selectRange(pos.start + options.before.length, pos.end + options.before.length + value.length);
        } else {
            var current = text.substring(pos.start, pos.end);
            this.set('value', text.substring(0, pos.start) + options.before + current + options.after + text.substring(pos.end, text.length));
            var selStart = pos.start + options.before.length;
            if (select !== false) this.selectRange(selStart, selStart + current.length);
            else this.setCaretPosition(selStart + text.length);
        }
        return this;
    }

});

/*
---

script: Element.Pin.js

name: Element.Pin

description: Extends the Element native object to include the pin method useful for fixed positioning for elements.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Core/Element.Event
  - Core/Element.Dimensions
  - Core/Element.Style
  - MooTools.More

provides: [Element.Pin]

...
*/

(function(){
    var supportsPositionFixed = false,
        supportTested = false;

    var testPositionFixed = function(){
        var test = new Element('div').setStyles({
            position: 'fixed',
            top: 0,
            right: 0
        }).inject(document.body);
        supportsPositionFixed = (test.offsetTop === 0);
        test.dispose();
        supportTested = true;
    };

    Element.implement({

        pin: function(enable, forceScroll){
            if (!supportTested) testPositionFixed();
            if (this.getStyle('display') == 'none') return this;

            var pinnedPosition,
                scroll = window.getScroll(),
                parent,
                scrollFixer;

            if (enable !== false){
                pinnedPosition = this.getPosition();
                if (!this.retrieve('pin:_pinned')){
                    var currentPosition = {
                        top: pinnedPosition.y - scroll.y,
                        left: pinnedPosition.x - scroll.x,
                        margin: '0px',
                        padding: '0px'
                    };

                    if (supportsPositionFixed && !forceScroll){
                        this.setStyle('position', 'fixed').setStyles(currentPosition);
                    } else {

                        parent = this.getOffsetParent();
                        var position = this.getPosition(parent),
                            styles = this.getStyles('left', 'top');

                        if (parent && styles.left == 'auto' || styles.top == 'auto') this.setPosition(position);
                        if (this.getStyle('position') == 'static') this.setStyle('position', 'absolute');

                        position = {
                            x: styles.left.toInt() - scroll.x,
                            y: styles.top.toInt() - scroll.y
                        };

                        scrollFixer = function(){
                            if (!this.retrieve('pin:_pinned')) return;
                            var scroll = window.getScroll();
                            this.setStyles({
                                left: position.x + scroll.x,
                                top: position.y + scroll.y
                            });
                        }.bind(this);

                        this.store('pin:_scrollFixer', scrollFixer);
                        window.addEvent('scroll', scrollFixer);
                    }
                    this.store('pin:_pinned', true);
                }

            } else {
                if (!this.retrieve('pin:_pinned')) return this;

                parent = this.getParent();
                var offsetParent = (parent.getComputedStyle('position') != 'static' ? parent : parent.getOffsetParent());

                pinnedPosition = this.getPosition();

                this.store('pin:_pinned', false);
                scrollFixer = this.retrieve('pin:_scrollFixer');
                if (!scrollFixer){
                    this.setStyles({
                        position: 'absolute',
                        top: pinnedPosition.y + scroll.y,
                        left: pinnedPosition.x + scroll.x
                    });
                } else {
                    this.store('pin:_scrollFixer', null);
                    window.removeEvent('scroll', scrollFixer);
                }
                this.removeClass('isPinned');
            }
            return this;
        },

        unpin: function(){
            return this.pin(false);
        },

        togglePin: function(){
            return this.pin(!this.retrieve('pin:_pinned'));
        }

    });

//<1.2compat>
    Element.alias('togglepin', 'togglePin');
//</1.2compat>

})();

/*
---

script: Element.Position.js

name: Element.Position

description: Extends the Element native object to include methods useful positioning elements relative to others.

license: MIT-style license

authors:
  - Aaron Newton
  - Jacob Thornton

requires:
  - Core/Options
  - Core/Element.Dimensions
  - Element.Measure

provides: [Element.Position]

...
*/

(function(original){

    var local = Element.Position = {

        options: {/*
		edge: false,
		returnPos: false,
		minimum: {x: 0, y: 0},
		maximum: {x: 0, y: 0},
		relFixedPosition: false,
		ignoreMargins: false,
		ignoreScroll: false,
		allowNegative: false,*/
            relativeTo: document.body,
            position: {
                x: 'center', //left, center, right
                y: 'center' //top, center, bottom
            },
            offset: {x: 0, y: 0}
        },

        getOptions: function(element, options){
            options = Object.merge({}, local.options, options);
            local.setPositionOption(options);
            local.setEdgeOption(options);
            local.setOffsetOption(element, options);
            local.setDimensionsOption(element, options);
            return options;
        },

        setPositionOption: function(options){
            options.position = local.getCoordinateFromValue(options.position);
        },

        setEdgeOption: function(options){
            var edgeOption = local.getCoordinateFromValue(options.edge);
            options.edge = edgeOption ? edgeOption :
                (options.position.x == 'center' && options.position.y == 'center') ? {x: 'center', y: 'center'} :
                    {x: 'left', y: 'top'};
        },

        setOffsetOption: function(element, options){
            var parentOffset = {x: 0, y: 0};
            var parentScroll = {x: 0, y: 0};
            var offsetParent = element.measure(function(){
                return document.id(this.getOffsetParent());
            });

            if (!offsetParent || offsetParent == element.getDocument().body) return;

            parentScroll = offsetParent.getScroll();
            parentOffset = offsetParent.measure(function(){
                var position = this.getPosition();
                if (this.getStyle('position') == 'fixed'){
                    var scroll = window.getScroll();
                    position.x += scroll.x;
                    position.y += scroll.y;
                }
                return position;
            });

            options.offset = {
                parentPositioned: offsetParent != document.id(options.relativeTo),
                x: options.offset.x - parentOffset.x + parentScroll.x,
                y: options.offset.y - parentOffset.y + parentScroll.y
            };
        },

        setDimensionsOption: function(element, options){
            options.dimensions = element.getDimensions({
                computeSize: true,
                styles: ['padding', 'border', 'margin']
            });
        },

        getPosition: function(element, options){
            var position = {};
            options = local.getOptions(element, options);
            var relativeTo = document.id(options.relativeTo) || document.body;

            local.setPositionCoordinates(options, position, relativeTo);
            if (options.edge) local.toEdge(position, options);

            var offset = options.offset;
            position.left = ((position.x >= 0 || offset.parentPositioned || options.allowNegative) ? position.x : 0).toInt();
            position.top = ((position.y >= 0 || offset.parentPositioned || options.allowNegative) ? position.y : 0).toInt();

            local.toMinMax(position, options);

            if (options.relFixedPosition || relativeTo.getStyle('position') == 'fixed') local.toRelFixedPosition(relativeTo, position);
            if (options.ignoreScroll) local.toIgnoreScroll(relativeTo, position);
            if (options.ignoreMargins) local.toIgnoreMargins(position, options);

            position.left = Math.ceil(position.left);
            position.top = Math.ceil(position.top);
            delete position.x;
            delete position.y;

            return position;
        },

        setPositionCoordinates: function(options, position, relativeTo){
            var offsetY = options.offset.y,
                offsetX = options.offset.x,
                calc = (relativeTo == document.body) ? window.getScroll() : relativeTo.getPosition(),
                top = calc.y,
                left = calc.x,
                winSize = window.getSize();

            switch (options.position.x){
                case 'left': position.x = left + offsetX; break;
                case 'right': position.x = left + offsetX + relativeTo.offsetWidth; break;
                default: position.x = left + ((relativeTo == document.body ? winSize.x : relativeTo.offsetWidth) / 2) + offsetX; break;
            }

            switch (options.position.y){
                case 'top': position.y = top + offsetY; break;
                case 'bottom': position.y = top + offsetY + relativeTo.offsetHeight; break;
                default: position.y = top + ((relativeTo == document.body ? winSize.y : relativeTo.offsetHeight) / 2) + offsetY; break;
            }
        },

        toMinMax: function(position, options){
            var xy = {left: 'x', top: 'y'}, value;
            ['minimum', 'maximum'].each(function(minmax){
                ['left', 'top'].each(function(lr){
                    value = options[minmax] ? options[minmax][xy[lr]] : null;
                    if (value != null && ((minmax == 'minimum') ? position[lr] < value : position[lr] > value)) position[lr] = value;
                });
            });
        },

        toRelFixedPosition: function(relativeTo, position){
            var winScroll = window.getScroll();
            position.top += winScroll.y;
            position.left += winScroll.x;
        },

        toIgnoreScroll: function(relativeTo, position){
            var relScroll = relativeTo.getScroll();
            position.top -= relScroll.y;
            position.left -= relScroll.x;
        },

        toIgnoreMargins: function(position, options){
            position.left += options.edge.x == 'right'
                ? options.dimensions['margin-right']
                : (options.edge.x != 'center'
                    ? -options.dimensions['margin-left']
                    : -options.dimensions['margin-left'] + ((options.dimensions['margin-right'] + options.dimensions['margin-left']) / 2));

            position.top += options.edge.y == 'bottom'
                ? options.dimensions['margin-bottom']
                : (options.edge.y != 'center'
                    ? -options.dimensions['margin-top']
                    : -options.dimensions['margin-top'] + ((options.dimensions['margin-bottom'] + options.dimensions['margin-top']) / 2));
        },

        toEdge: function(position, options){
            var edgeOffset = {},
                dimensions = options.dimensions,
                edge = options.edge;

            switch (edge.x){
                case 'left': edgeOffset.x = 0; break;
                case 'right': edgeOffset.x = -dimensions.x - dimensions.computedRight - dimensions.computedLeft; break;
                // center
                default: edgeOffset.x = -(Math.round(dimensions.totalWidth / 2)); break;
            }

            switch (edge.y){
                case 'top': edgeOffset.y = 0; break;
                case 'bottom': edgeOffset.y = -dimensions.y - dimensions.computedTop - dimensions.computedBottom; break;
                // center
                default: edgeOffset.y = -(Math.round(dimensions.totalHeight / 2)); break;
            }

            position.x += edgeOffset.x;
            position.y += edgeOffset.y;
        },

        getCoordinateFromValue: function(option){
            if (typeOf(option) != 'string') return option;
            option = option.toLowerCase();

            return {
                x: option.test('left') ? 'left'
                    : (option.test('right') ? 'right' : 'center'),
                y: option.test(/upper|top/) ? 'top'
                    : (option.test('bottom') ? 'bottom' : 'center')
            };
        }

    };

    Element.implement({

        position: function(options){
            if (options && (options.x != null || options.y != null)){
                return (original ? original.apply(this, arguments) : this);
            }
            var position = this.setStyle('position', 'absolute').calculatePosition(options);
            return (options && options.returnPos) ? position : this.setStyles(position);
        },

        calculatePosition: function(options){
            return local.getPosition(this, options);
        }

    });

})(Element.prototype.position);

/*
---

script: Element.Shortcuts.js

name: Element.Shortcuts

description: Extends the Element native object to include some shortcut methods.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Core/Element.Style
  - MooTools.More

provides: [Element.Shortcuts]

...
*/

Element.implement({

    isDisplayed: function(){
        return this.getStyle('display') != 'none';
    },

    isVisible: function(){
        var w = this.offsetWidth,
            h = this.offsetHeight;
        return (w == 0 && h == 0) ? false : (w > 0 && h > 0) ? true : this.style.display != 'none';
    },

    toggle: function(){
        return this[this.isDisplayed() ? 'hide' : 'show']();
    },

    hide: function(){
        var d;
        try {
            //IE fails here if the element is not in the dom
            d = this.getStyle('display');
        } catch (e){}
        if (d == 'none') return this;
        return this.store('element:_originalDisplay', d || '').setStyle('display', 'none');
    },

    show: function(display){
        if (!display && this.isDisplayed()) return this;
        display = display || this.retrieve('element:_originalDisplay') || 'block';
        return this.setStyle('display', (display == 'none') ? 'block' : display);
    },

    swapClass: function(remove, add){
        return this.removeClass(remove).addClass(add);
    }

});

Document.implement({

    clearSelection: function(){
        if (window.getSelection){
            var selection = window.getSelection();
            if (selection && selection.removeAllRanges) selection.removeAllRanges();
        } else if (document.selection && document.selection.empty){
            try {
                //IE fails here if selected element is not in dom
                document.selection.empty();
            } catch (e){}
        }
    }

});

/*
---

script: Elements.From.js

name: Elements.From

description: Returns a collection of elements from a string of html.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Core/String
  - Core/Element
  - MooTools.More

provides: [Elements.from, Elements.From]

...
*/

Elements.from = function(text, excludeScripts){
    if (excludeScripts || excludeScripts == null) text = text.stripScripts();

    var container, match = text.match(/^\s*(?:<!--.*?-->\s*)*<(t[dhr]|tbody|tfoot|thead)/i);

    if (match){
        container = new Element('table');
        var tag = match[1].toLowerCase();
        if (['td', 'th', 'tr'].contains(tag)){
            container = new Element('tbody').inject(container);
            if (tag != 'tr') container = new Element('tr').inject(container);
        }
    }

    return (container || new Element('div')).set('html', text).getChildren();
};

/*
---

script: IframeShim.js

name: IframeShim

description: Defines IframeShim, a class for obscuring select lists and flash objects in IE.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Core/Element.Event
  - Core/Element.Style
  - Core/Options
  - Core/Events
  - Element.Position
  - Class.Occlude

provides: [IframeShim]

...
*/

(function(){

    var browsers = false;
//<1.4compat>
    browsers = Browser.ie6 || (Browser.firefox && Browser.version < 3 && Browser.Platform.mac);
//</1.4compat>

    var IframeShim = this.IframeShim = new Class({

        Implements: [Options, Events, Class.Occlude],

        options: {
            className: 'iframeShim',
            src: 'javascript:false;document.write("");',
            display: false,
            zIndex: null,
            margin: 0,
            offset: {x: 0, y: 0},
            browsers: browsers
        },

        property: 'IframeShim',

        initialize: function(element, options){
            this.element = document.id(element);
            if (this.occlude()) return this.occluded;
            this.setOptions(options);
            this.makeShim();
            return this;
        },

        makeShim: function(){
            if (this.options.browsers){
                var zIndex = this.element.getStyle('zIndex').toInt();

                if (!zIndex){
                    zIndex = 1;
                    var pos = this.element.getStyle('position');
                    if (pos == 'static' || !pos) this.element.setStyle('position', 'relative');
                    this.element.setStyle('zIndex', zIndex);
                }
                zIndex = ((this.options.zIndex != null || this.options.zIndex === 0) && zIndex > this.options.zIndex) ? this.options.zIndex : zIndex - 1;
                if (zIndex < 0) zIndex = 1;
                this.shim = new Element('iframe', {
                    src: this.options.src,
                    scrolling: 'no',
                    frameborder: 0,
                    styles: {
                        zIndex: zIndex,
                        position: 'absolute',
                        border: 'none',
                        filter: 'progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)'
                    },
                    'class': this.options.className
                }).store('IframeShim', this);
                var inject = (function(){
                    this.shim.inject(this.element, 'after');
                    this[this.options.display ? 'show' : 'hide']();
                    this.fireEvent('inject');
                }).bind(this);
                if (!IframeShim.ready) window.addEvent('load', inject);
                else inject();
            } else {
                this.position = this.hide = this.show = this.dispose = Function.convert(this);
            }
        },

        position: function(){
            if (!IframeShim.ready || !this.shim) return this;
            var size = this.element.measure(function(){
                return this.getSize();
            });
            if (this.options.margin != undefined){
                size.x = size.x - (this.options.margin * 2);
                size.y = size.y - (this.options.margin * 2);
                this.options.offset.x += this.options.margin;
                this.options.offset.y += this.options.margin;
            }
            this.shim.set({width: size.x, height: size.y}).position({
                relativeTo: this.element,
                offset: this.options.offset
            });
            return this;
        },

        hide: function(){
            if (this.shim) this.shim.setStyle('display', 'none');
            return this;
        },

        show: function(){
            if (this.shim) this.shim.setStyle('display', 'block');
            return this.position();
        },

        dispose: function(){
            if (this.shim) this.shim.dispose();
            return this;
        },

        destroy: function(){
            if (this.shim) this.shim.destroy();
            return this;
        }

    });

})();

window.addEvent('load', function(){
    IframeShim.ready = true;
});

/*
---

script: Mask.js

name: Mask

description: Creates a mask element to cover another.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Core/Options
  - Core/Events
  - Core/Element.Event
  - Class.Binds
  - Element.Position
  - IframeShim

provides: [Mask]

...
*/
(function(){

    var Mask = this.Mask = new Class({

        Implements: [Options, Events],

        Binds: ['position'],

        options: {/*
		onShow: function(){},
		onHide: function(){},
		onDestroy: function(){},
		onClick: function(event){},
		inject: {
			where: 'after',
			target: null,
		},
		hideOnClick: false,
		id: null,
		destroyOnHide: false,*/
            style: {},
            'class': 'mask',
            maskMargins: false,
            useIframeShim: true,
            iframeShimOptions: {}
        },

        initialize: function(target, options){
            this.target = document.id(target) || document.id(document.body);
            this.target.store('mask', this);
            this.setOptions(options);
            this.render();
            this.inject();
        },

        render: function(){
            this.element = new Element('div', {
                'class': this.options['class'],
                id: this.options.id || 'mask-' + String.uniqueID(),
                styles: Object.merge({}, this.options.style, {
                    display: 'none'
                }),
                events: {
                    click: function(event){
                        this.fireEvent('click', event);
                        if (this.options.hideOnClick) this.hide();
                    }.bind(this)
                }
            });

            this.hidden = true;
        },

        toElement: function(){
            return this.element;
        },

        inject: function(target, where){
            where = where || (this.options.inject ? this.options.inject.where : '') || (this.target == document.body ? 'inside' : 'after');
            target = target || (this.options.inject && this.options.inject.target) || this.target;

            this.element.inject(target, where);

            if (this.options.useIframeShim){
                this.shim = new IframeShim(this.element, this.options.iframeShimOptions);

                this.addEvents({
                    show: this.shim.show.bind(this.shim),
                    hide: this.shim.hide.bind(this.shim),
                    destroy: this.shim.destroy.bind(this.shim)
                });
            }
        },

        position: function(){
            this.resize(this.options.width, this.options.height);

            this.element.position({
                relativeTo: this.target,
                position: 'topLeft',
                ignoreMargins: !this.options.maskMargins,
                ignoreScroll: this.target == document.body
            });

            return this;
        },

        resize: function(x, y){
            var opt = {
                styles: ['padding', 'border']
            };
            if (this.options.maskMargins) opt.styles.push('margin');

            var dim = this.target.getComputedSize(opt);
            var s = this.target.getSize();
            if (dim.totalHeight<s.y) dim.totalHeight = s.y;
            if (dim.totalWidth<s.x) dim.totalWidth = s.x;
            if (this.target == document.body){
                this.element.setStyles({width: 0, height: 0});
                var win = window.getScrollSize();
                if (dim.totalHeight < win.y) dim.totalHeight = win.y;
                if (dim.totalWidth < win.x) dim.totalWidth = win.x;
            }
            this.element.setStyles({
                width: Array.pick([x, dim.totalWidth, dim.x]),
                height: Array.pick([y, dim.totalHeight, dim.y])
            });

            return this;
        },

        show: function(){
            if (!this.hidden) return this;

            window.addEvent('resize', this.position);
            this.position();
            this.showMask.apply(this, arguments);

            return this;
        },

        showMask: function(){
            this.element.setStyle('display', 'block');
            this.hidden = false;
            this.fireEvent('show');
        },

        hide: function(){
            if (this.hidden) return this;

            window.removeEvent('resize', this.position);
            this.hideMask.apply(this, arguments);
            if (this.options.destroyOnHide) return this.destroy();

            return this;
        },

        hideMask: function(){
            this.element.setStyle('display', 'none');
            this.hidden = true;
            this.fireEvent('hide');
        },

        toggle: function(){
            this[this.hidden ? 'show' : 'hide']();
        },

        destroy: function(){
            this.hide();
            this.element.destroy();
            this.fireEvent('destroy');
            this.target.eliminate('mask');
        }

    });

})();


Element.Properties.mask = {

    set: function(options){
        var mask = this.retrieve('mask');
        if (mask) mask.destroy();
        return this.eliminate('mask').store('mask:options', options);
    },

    get: function(){
        var mask = this.retrieve('mask');
        if (!mask){
            mask = new Mask(this, this.retrieve('mask:options'));
            this.store('mask', mask);
        }
        return mask;
    }

};

Element.implement({

    mask: function(options){
        if (options) this.set('mask', options);
        this.get('mask').show();
        return this;
    },

    unmask: function(){
        this.get('mask').hide();
        return this;
    }

});

/*
---

script: Spinner.js

name: Spinner

description: Adds a semi-transparent overlay over a dom element with a spinnin ajax icon.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Core/Fx.Tween
  - Core/Request
  - Class.refactor
  - Mask

provides: [Spinner]

...
*/
(function(){

    var Spinner = this.Spinner = new Class({

        Extends: this.Mask,

        Implements: Chain,

        options: {/*
		message: false,*/
            'class': 'spinner',
            containerPosition: {},
            content: {
                'class': 'spinner-content'
            },
            messageContainer: {
                'class': 'spinner-msg'
            },
            img: {
                'class': 'spinner-img'
            },
            fxOptions: {
                link: 'chain'
            }
        },

        initialize: function(target, options){
            this.target = document.id(target) || document.id(document.body);
            this.target.store('spinner', this);
            this.setOptions(options);
            this.render();
            this.inject();

            // Add this to events for when noFx is true; parent methods handle hide/show.
            var deactivate = function(){ this.active = false; }.bind(this);
            this.addEvents({
                hide: deactivate,
                show: deactivate
            });
        },

        render: function(){
            this.parent();

            this.element.set('id', this.options.id || 'spinner-' + String.uniqueID());

            this.content = document.id(this.options.content) || new Element('div', this.options.content);
            this.content.inject(this.element);

            if (this.options.message){
                this.msg = document.id(this.options.message) || new Element('p', this.options.messageContainer).appendText(this.options.message);
                this.msg.inject(this.content);
            }

            if (this.options.img){
                this.img = document.id(this.options.img) || new Element('div', this.options.img);
                this.img.inject(this.content);
            }

            this.element.set('tween', this.options.fxOptions);
        },

        show: function(noFx){
            if (this.active) return this.chain(this.show.bind(this));
            if (!this.hidden){
                this.callChain.delay(20, this);
                return this;
            }

            this.target.set('aria-busy', 'true');
            this.active = true;

            return this.parent(noFx);
        },

        showMask: function(noFx){
            var pos = function(){
                this.content.position(Object.merge({
                    relativeTo: this.element
                }, this.options.containerPosition));
            }.bind(this);

            if (noFx){
                this.parent();
                pos();
            } else {
                if (!this.options.style.opacity) this.options.style.opacity = this.element.getStyle('opacity').toFloat();
                this.element.setStyles({
                    display: 'block',
                    opacity: 0
                }).tween('opacity', this.options.style.opacity);
                pos();
                this.hidden = false;
                this.fireEvent('show');
                this.callChain();
            }
        },

        hide: function(noFx){
            if (this.active) return this.chain(this.hide.bind(this));
            if (this.hidden){
                this.callChain.delay(20, this);
                return this;
            }

            this.target.set('aria-busy', 'false');
            this.active = true;

            return this.parent(noFx);
        },

        hideMask: function(noFx){
            if (noFx) return this.parent();
            this.element.tween('opacity', 0).get('tween').chain(function(){
                this.element.setStyle('display', 'none');
                this.hidden = true;
                this.fireEvent('hide');
                this.callChain();
            }.bind(this));
        },

        destroy: function(){
            this.content.destroy();
            this.parent();
            this.target.eliminate('spinner');
        }

    });

})();

Request = Class.refactor(Request, {

    options: {
        useSpinner: false,
        spinnerOptions: {},
        spinnerTarget: false
    },

    initialize: function(options){
        this._send = this.send;
        this.send = function(options){
            var spinner = this.getSpinner();
            if (spinner) spinner.chain(this._send.pass(options, this)).show();
            else this._send(options);
            return this;
        };
        this.previous(options);
    },

    getSpinner: function(){
        if (!this.spinner){
            var update = document.id(this.options.spinnerTarget) || document.id(this.options.update);
            if (this.options.useSpinner && update){
                update.set('spinner', this.options.spinnerOptions);
                var spinner = this.spinner = update.get('spinner');
                ['complete', 'exception', 'cancel'].each(function(event){
                    this.addEvent(event, spinner.hide.bind(spinner));
                }, this);
            }
        }
        return this.spinner;
    }

});

Element.Properties.spinner = {

    set: function(options){
        var spinner = this.retrieve('spinner');
        if (spinner) spinner.destroy();
        return this.eliminate('spinner').store('spinner:options', options);
    },

    get: function(){
        var spinner = this.retrieve('spinner');
        if (!spinner){
            spinner = new Spinner(this, this.retrieve('spinner:options'));
            this.store('spinner', spinner);
        }
        return spinner;
    }

};

Element.implement({

    spin: function(options){
        if (options) this.set('spinner', options);
        this.get('spinner').show();
        return this;
    },

    unspin: function(){
        this.get('spinner').hide();
        return this;
    }

});

/*
---

script: String.QueryString.js

name: String.QueryString

description: Methods for dealing with URI query strings.

license: MIT-style license

authors:
  - Sebastian Markbåge
  - Aaron Newton
  - Lennart Pilon
  - Valerio Proietti

requires:
  - Core/Array
  - Core/String
  - MooTools.More

provides: [String.QueryString]

...
*/

(function(){

    /**
     * decodeURIComponent doesn't do the correct thing with query parameter keys or
     * values. Specifically, it leaves '+' as '+' when it should be converting them
     * to spaces as that's the specification. When browsers submit HTML forms via
     * GET, the values are encoded using 'application/x-www-form-urlencoded'
     * which converts spaces to '+'.
     *
     * See: http://unixpapa.com/js/querystring.html for a description of the
     * problem.
     */
    var decodeComponent = function(str){
        return decodeURIComponent(str.replace(/\+/g, ' '));
    };

    String.implement({

        parseQueryString: function(decodeKeys, decodeValues){
            if (decodeKeys == null) decodeKeys = true;
            if (decodeValues == null) decodeValues = true;

            var vars = this.split(/[&;]/),
                object = {};
            if (!vars.length) return object;

            vars.each(function(val){
                var index = val.indexOf('=') + 1,
                    value = index ? val.substr(index) : '',
                    keys = index ? val.substr(0, index - 1).match(/([^\]\[]+|(\B)(?=\]))/g) : [val],
                    obj = object;
                if (!keys) return;
                if (decodeValues) value = decodeComponent(value);
                keys.each(function(key, i){
                    if (decodeKeys) key = decodeComponent(key);
                    var current = obj[key];

                    if (i < keys.length - 1) obj = obj[key] = current || {};
                    else if (typeOf(current) == 'array') current.push(value);
                    else obj[key] = current != null ? [current, value] : value;
                });
            });

            return object;
        },

        cleanQueryString: function(method){
            return this.split('&').filter(function(val){
                var index = val.indexOf('='),
                    key = index < 0 ? '' : val.substr(0, index),
                    value = val.substr(index + 1);

                return method ? method.call(null, key, value) : (value || value === 0);
            }).join('&');
        }

    });

})();

/*
---

script: Form.Request.js

name: Form.Request

description: Handles the basic functionality of submitting a form and updating a dom element with the result.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Core/Request.HTML
  - Class.Binds
  - Class.Occlude
  - Spinner
  - String.QueryString
  - Element.Delegation.Pseudo

provides: [Form.Request]

...
*/

if (!window.Form) window.Form = {};

(function(){

    Form.Request = new Class({

        Binds: ['onSubmit', 'onFormValidate'],

        Implements: [Options, Events, Class.Occlude],

        options: {/*
		onFailure: function(){},
		onSuccess: function(){}, // aliased to onComplete,
		onSend: function(){}*/
            requestOptions: {
                evalScripts: true,
                useSpinner: true,
                emulation: false,
                link: 'ignore'
            },
            sendButtonClicked: true,
            extraData: {},
            resetForm: true
        },

        property: 'form.request',

        initialize: function(form, target, options){
            this.element = document.id(form);
            if (this.occlude()) return this.occluded;
            this.setOptions(options)
                .setTarget(target)
                .attach();
        },

        setTarget: function(target){
            this.target = document.id(target);
            if (!this.request){
                this.makeRequest();
            } else {
                this.request.setOptions({
                    update: this.target
                });
            }
            return this;
        },

        toElement: function(){
            return this.element;
        },

        makeRequest: function(){
            var self = this;
            this.request = new Request.HTML(Object.merge({
                update: this.target,
                emulation: false,
                spinnerTarget: this.element,
                method: this.element.get('method') || 'post'
            }, this.options.requestOptions)).addEvents({
                success: function(tree, elements, html, javascript){
                    ['complete', 'success'].each(function(evt){
                        self.fireEvent(evt, [self.target, tree, elements, html, javascript]);
                    });
                },
                failure: function(){
                    self.fireEvent('complete', arguments).fireEvent('failure', arguments);
                },
                exception: function(){
                    self.fireEvent('failure', arguments);
                }
            });
            return this.attachReset();
        },

        attachReset: function(){
            if (!this.options.resetForm) return this;
            this.request.addEvent('success', function(){
                Function.attempt(function(){
                    this.element.reset();
                }.bind(this));
                if (window.OverText) OverText.update();
            }.bind(this));
            return this;
        },

        attach: function(attach){
            var method = (attach != false) ? 'addEvent' : 'removeEvent';
            this.element[method]('click:relay(button, input[type=submit])', this.saveClickedButton.bind(this));

            var fv = this.element.retrieve('validator');
            if (fv) fv[method]('onFormValidate', this.onFormValidate);
            else this.element[method]('submit', this.onSubmit);

            return this;
        },

        detach: function(){
            return this.attach(false);
        },

        //public method
        enable: function(){
            return this.attach();
        },

        //public method
        disable: function(){
            return this.detach();
        },

        onFormValidate: function(valid, form, event){
            //if there's no event, then this wasn't a submit event
            if (!event) return;
            var fv = this.element.retrieve('validator');
            if (valid || (fv && !fv.options.stopOnFailure)){
                event.stop();
                this.send();
            }
        },

        onSubmit: function(event){
            var fv = this.element.retrieve('validator');
            if (fv){
                //form validator was created after Form.Request
                this.element.removeEvent('submit', this.onSubmit);
                fv.addEvent('onFormValidate', this.onFormValidate);
                fv.validate(event);
                return;
            }
            if (event) event.stop();
            this.send();
        },

        saveClickedButton: function(event, target){
            var targetName = target.get('name');
            if (!targetName || !this.options.sendButtonClicked) return;
            this.options.extraData[targetName] = target.get('value') || true;
            this.clickedCleaner = function(){
                delete this.options.extraData[targetName];
                this.clickedCleaner = function(){};
            }.bind(this);
        },

        clickedCleaner: function(){},

        send: function(){
            var str = this.element.toQueryString().trim(),
                data = Object.toQueryString(this.options.extraData);

            if (str) str += '&' + data;
            else str = data;

            this.fireEvent('send', [this.element, str.parseQueryString()]);
            this.request.send({
                data: str,
                url: this.options.requestOptions.url || this.element.get('action')
            });
            this.clickedCleaner();
            return this;
        }

    });

    Element.implement('formUpdate', function(update, options){
        var fq = this.retrieve('form.request');
        if (!fq){
            fq = new Form.Request(this, update, options);
        } else {
            if (update) fq.setTarget(update);
            if (options) fq.setOptions(options).makeRequest();
        }
        fq.send();
        return this;
    });

})();

/*
---

script: Fx.Reveal.js

name: Fx.Reveal

description: Defines Fx.Reveal, a class that shows and hides elements with a transition.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Core/Fx.Morph
  - Element.Shortcuts
  - Element.Measure

provides: [Fx.Reveal]

...
*/

(function(){


    var hideTheseOf = function(object){
        var hideThese = object.options.hideInputs;
        if (window.OverText){
            var otClasses = [null];
            OverText.each(function(ot){
                otClasses.include('.' + ot.options.labelClass);
            });
            if (otClasses) hideThese += otClasses.join(', ');
        }
        return (hideThese) ? object.element.getElements(hideThese) : null;
    };


    Fx.Reveal = new Class({

        Extends: Fx.Morph,

        options: {/*
		onShow: function(thisElement){},
		onHide: function(thisElement){},
		onComplete: function(thisElement){},
		heightOverride: null,
		widthOverride: null,*/
            link: 'cancel',
            styles: ['padding', 'border', 'margin'],
            transitionOpacity: 'opacity' in document.documentElement,
            mode: 'vertical',
            display: function(){
                return this.element.get('tag') != 'tr' ? 'block' : 'table-row';
            },
            opacity: 1,
            hideInputs: !('opacity' in document.documentElement) ? 'select, input, textarea, object, embed' : null
        },

        dissolve: function(){
            if (!this.hiding && !this.showing){
                if (this.element.getStyle('display') != 'none'){
                    this.hiding = true;
                    this.showing = false;
                    this.hidden = true;
                    this.cssText = this.element.style.cssText;

                    var startStyles = this.element.getComputedSize({
                        styles: this.options.styles,
                        mode: this.options.mode
                    });
                    if (this.options.transitionOpacity) startStyles.opacity = this.options.opacity;

                    var zero = {};
                    Object.each(startStyles, function(style, name){
                        zero[name] = [style, 0];
                    });

                    this.element.setStyles({
                        display: Function.convert(this.options.display).call(this),
                        overflow: 'hidden'
                    });

                    var hideThese = hideTheseOf(this);
                    if (hideThese) hideThese.setStyle('visibility', 'hidden');

                    this.$chain.unshift(function(){
                        if (this.hidden){
                            this.hiding = false;
                            this.element.style.cssText = this.cssText;
                            this.element.setStyle('display', 'none');
                            if (hideThese) hideThese.setStyle('visibility', 'visible');
                        }
                        this.fireEvent('hide', this.element);
                        this.callChain();
                    }.bind(this));

                    this.start(zero);
                } else {
                    this.callChain.delay(10, this);
                    this.fireEvent('complete', this.element);
                    this.fireEvent('hide', this.element);
                }
            } else if (this.options.link == 'chain'){
                this.chain(this.dissolve.bind(this));
            } else if (this.options.link == 'cancel' && !this.hiding){
                this.cancel();
                this.dissolve();
            }
            return this;
        },

        reveal: function(){
            if (!this.showing && !this.hiding){
                if (this.element.getStyle('display') == 'none'){
                    this.hiding = false;
                    this.showing = true;
                    this.hidden = false;
                    this.cssText = this.element.style.cssText;

                    var startStyles;
                    this.element.measure(function(){
                        startStyles = this.element.getComputedSize({
                            styles: this.options.styles,
                            mode: this.options.mode
                        });
                    }.bind(this));
                    if (this.options.heightOverride != null) startStyles.height = this.options.heightOverride.toInt();
                    if (this.options.widthOverride != null) startStyles.width = this.options.widthOverride.toInt();
                    if (this.options.transitionOpacity){
                        this.element.setStyle('opacity', 0);
                        startStyles.opacity = this.options.opacity;
                    }

                    var zero = {
                        height: 0,
                        display: Function.convert(this.options.display).call(this)
                    };
                    Object.each(startStyles, function(style, name){
                        zero[name] = 0;
                    });
                    zero.overflow = 'hidden';

                    this.element.setStyles(zero);

                    var hideThese = hideTheseOf(this);
                    if (hideThese) hideThese.setStyle('visibility', 'hidden');

                    this.$chain.unshift(function(){
                        this.element.style.cssText = this.cssText;
                        this.element.setStyle('display', Function.convert(this.options.display).call(this));
                        if (!this.hidden) this.showing = false;
                        if (hideThese) hideThese.setStyle('visibility', 'visible');
                        this.callChain();
                        this.fireEvent('show', this.element);
                    }.bind(this));

                    this.start(startStyles);
                } else {
                    this.callChain();
                    this.fireEvent('complete', this.element);
                    this.fireEvent('show', this.element);
                }
            } else if (this.options.link == 'chain'){
                this.chain(this.reveal.bind(this));
            } else if (this.options.link == 'cancel' && !this.showing){
                this.cancel();
                this.reveal();
            }
            return this;
        },

        toggle: function(){
            if (this.element.getStyle('display') == 'none'){
                this.reveal();
            } else {
                this.dissolve();
            }
            return this;
        },

        cancel: function(){
            this.parent.apply(this, arguments);
            if (this.cssText != null) this.element.style.cssText = this.cssText;
            this.hiding = false;
            this.showing = false;
            return this;
        }

    });

    Element.Properties.reveal = {

        set: function(options){
            this.get('reveal').cancel().setOptions(options);
            return this;
        },

        get: function(){
            var reveal = this.retrieve('reveal');
            if (!reveal){
                reveal = new Fx.Reveal(this);
                this.store('reveal', reveal);
            }
            return reveal;
        }

    };

    Element.Properties.dissolve = Element.Properties.reveal;

    Element.implement({

        reveal: function(options){
            this.get('reveal').setOptions(options).reveal();
            return this;
        },

        dissolve: function(options){
            this.get('reveal').setOptions(options).dissolve();
            return this;
        },

        nix: function(options){
            var params = Array.link(arguments, {destroy: Type.isBoolean, options: Type.isObject});
            this.get('reveal').setOptions(options).dissolve().chain(function(){
                this[params.destroy ? 'destroy' : 'dispose']();
            }.bind(this));
            return this;
        },

        wink: function(){
            var params = Array.link(arguments, {duration: Type.isNumber, options: Type.isObject});
            var reveal = this.get('reveal').setOptions(params.options);
            reveal.reveal().chain(function(){
                (function(){
                    reveal.dissolve();
                }).delay(params.duration || 2000);
            });
        }

    });

})();

/*
---

script: Form.Request.Append.js

name: Form.Request.Append

description: Handles the basic functionality of submitting a form and updating a dom element with the result. The result is appended to the DOM element instead of replacing its contents.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Form.Request
  - Fx.Reveal
  - Elements.from

provides: [Form.Request.Append]

...
*/

Form.Request.Append = new Class({

    Extends: Form.Request,

    options: {
        //onBeforeEffect: function(){},
        useReveal: true,
        revealOptions: {},
        inject: 'bottom'
    },

    makeRequest: function(){
        this.request = new Request.HTML(Object.merge({
            url: this.element.get('action'),
            method: this.element.get('method') || 'post',
            spinnerTarget: this.element
        }, this.options.requestOptions, {
            evalScripts: false
        })).addEvents({
            success: function(tree, elements, html, javascript){
                var container;
                var kids = Elements.from(html);
                if (kids.length == 1){
                    container = kids[0];
                } else {
                    container = new Element('div', {
                        styles: {
                            display: 'none'
                        }
                    }).adopt(kids);
                }
                container.inject(this.target, this.options.inject);
                if (this.options.requestOptions.evalScripts) Browser.exec(javascript);
                this.fireEvent('beforeEffect', container);
                var finish = function(){
                    this.fireEvent('success', [container, this.target, tree, elements, html, javascript]);
                }.bind(this);
                if (this.options.useReveal){
                    container.set('reveal', this.options.revealOptions).get('reveal').chain(finish);
                    container.reveal();
                } else {
                    finish();
                }
            }.bind(this),
            failure: function(xhr){
                this.fireEvent('failure', xhr);
            }.bind(this)
        });
        this.attachReset();
    }

});

/*
---

script: Object.Extras.js

name: Object.Extras

description: Extra Object generics, like getFromPath which allows a path notation to child elements.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Core/Object
  - MooTools.More

provides: [Object.Extras]

...
*/

(function(){

    var defined = function(value){
        return value != null;
    };

    var hasOwnProperty = Object.prototype.hasOwnProperty;

    Object.extend({

        getFromPath: function(source, parts){
            if (typeof parts == 'string') parts = parts.split('.');
            for (var i = 0, l = parts.length; i < l; i++){
                if (hasOwnProperty.call(source, parts[i])) source = source[parts[i]];
                else return null;
            }
            return source;
        },

        cleanValues: function(object, method){
            method = method || defined;
            for (var key in object) if (!method(object[key])){
                delete object[key];
            }
            return object;
        },

        erase: function(object, key){
            if (hasOwnProperty.call(object, key)) delete object[key];
            return object;
        },

        run: function(object){
            var args = Array.slice(arguments, 1);
            for (var key in object) if (object[key].apply){
                object[key].apply(object, args);
            }
            return object;
        }

    });

})();

/*
---

script: Locale.js

name: Locale

description: Provides methods for localization.

license: MIT-style license

authors:
  - Aaron Newton
  - Arian Stolwijk

requires:
  - Core/Events
  - Object.Extras
  - MooTools.More

provides: [Locale, Lang]

...
*/

(function(){

    var current = null,
        locales = {},
        inherits = {};

    var getSet = function(set){
        if (instanceOf(set, Locale.Set)) return set;
        else return locales[set];
    };

    var Locale = this.Locale = {

        define: function(locale, set, key, value){
            var name;
            if (instanceOf(locale, Locale.Set)){
                name = locale.name;
                if (name) locales[name] = locale;
            } else {
                name = locale;
                if (!locales[name]) locales[name] = new Locale.Set(name);
                locale = locales[name];
            }

            if (set) locale.define(set, key, value);

            /*<1.2compat>*/
            if (set == 'cascade') return Locale.inherit(name, key);
            /*</1.2compat>*/

            if (!current) current = locale;

            return locale;
        },

        use: function(locale){
            locale = getSet(locale);

            if (locale){
                current = locale;

                this.fireEvent('change', locale);

                /*<1.2compat>*/
                this.fireEvent('langChange', locale.name);
                /*</1.2compat>*/
            }

            return this;
        },

        getCurrent: function(){
            return current;
        },

        get: function(key, args){
            return (current) ? current.get(key, args) : '';
        },

        inherit: function(locale, inherits, set){
            locale = getSet(locale);

            if (locale) locale.inherit(inherits, set);
            return this;
        },

        list: function(){
            return Object.keys(locales);
        }

    };

    Object.append(Locale, new Events);

    Locale.Set = new Class({

        sets: {},

        inherits: {
            locales: [],
            sets: {}
        },

        initialize: function(name){
            this.name = name || '';
        },

        define: function(set, key, value){
            var defineData = this.sets[set];
            if (!defineData) defineData = {};

            if (key){
                if (typeOf(key) == 'object') defineData = Object.merge(defineData, key);
                else defineData[key] = value;
            }
            this.sets[set] = defineData;

            return this;
        },

        get: function(key, args, _base){
            var value = Object.getFromPath(this.sets, key);
            if (value != null){
                var type = typeOf(value);
                if (type == 'function') value = value.apply(null, Array.convert(args));
                else if (type == 'object') value = Object.clone(value);
                return value;
            }

            // get value of inherited locales
            var index = key.indexOf('.'),
                set = index < 0 ? key : key.substr(0, index),
                names = (this.inherits.sets[set] || []).combine(this.inherits.locales).include('en-US');
            if (!_base) _base = [];

            for (var i = 0, l = names.length; i < l; i++){
                if (_base.contains(names[i])) continue;
                _base.include(names[i]);

                var locale = locales[names[i]];
                if (!locale) continue;

                value = locale.get(key, args, _base);
                if (value != null) return value;
            }

            return '';
        },

        inherit: function(names, set){
            names = Array.convert(names);

            if (set && !this.inherits.sets[set]) this.inherits.sets[set] = [];

            var l = names.length;
            while (l--) (set ? this.inherits.sets[set] : this.inherits.locales).unshift(names[l]);

            return this;
        }

    });

    /*<1.2compat>*/
    var lang = MooTools.lang = {};

    Object.append(lang, Locale, {
        setLanguage: Locale.use,
        getCurrentLanguage: function(){
            var current = Locale.getCurrent();
            return (current) ? current.name : null;
        },
        set: function(){
            Locale.define.apply(this, arguments);
            return this;
        },
        get: function(set, key, args){
            if (key) set += '.' + key;
            return Locale.get(set, args);
        }
    });
    /*</1.2compat>*/

})();

/*
---

name: Locale.en-US.Date

description: Date messages for US English.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Locale

provides: [Locale.en-US.Date]

...
*/

Locale.define('en-US', 'Date', {

    months: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
    months_abbr: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
    days: ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'],
    days_abbr: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],

    // Culture's date order: MM/DD/YYYY
    dateOrder: ['month', 'date', 'year'],
    shortDate: '%m/%d/%Y',
    shortTime: '%I:%M%p',
    AM: 'AM',
    PM: 'PM',
    firstDayOfWeek: 0,

    // Date.Extras
    ordinal: function(dayOfMonth){
        // 1st, 2nd, 3rd, etc.
        return (dayOfMonth > 3 && dayOfMonth < 21) ? 'th' : ['th', 'st', 'nd', 'rd', 'th'][Math.min(dayOfMonth % 10, 4)];
    },

    lessThanMinuteAgo: 'less than a minute ago',
    minuteAgo: 'about a minute ago',
    minutesAgo: '{delta} minutes ago',
    hourAgo: 'about an hour ago',
    hoursAgo: 'about {delta} hours ago',
    dayAgo: '1 day ago',
    daysAgo: '{delta} days ago',
    weekAgo: '1 week ago',
    weeksAgo: '{delta} weeks ago',
    monthAgo: '1 month ago',
    monthsAgo: '{delta} months ago',
    yearAgo: '1 year ago',
    yearsAgo: '{delta} years ago',

    lessThanMinuteUntil: 'less than a minute from now',
    minuteUntil: 'about a minute from now',
    minutesUntil: '{delta} minutes from now',
    hourUntil: 'about an hour from now',
    hoursUntil: 'about {delta} hours from now',
    dayUntil: '1 day from now',
    daysUntil: '{delta} days from now',
    weekUntil: '1 week from now',
    weeksUntil: '{delta} weeks from now',
    monthUntil: '1 month from now',
    monthsUntil: '{delta} months from now',
    yearUntil: '1 year from now',
    yearsUntil: '{delta} years from now'

});

/*
---

script: Date.js

name: Date

description: Extends the Date native object to include methods useful in managing dates.

license: MIT-style license

authors:
  - Aaron Newton
  - Nicholas Barthelemy - https://svn.nbarthelemy.com/date-js/
  - Harald Kirshner - mail [at] digitarald.de; http://digitarald.de
  - Scott Kyle - scott [at] appden.com; http://appden.com

requires:
  - Core/Array
  - Core/String
  - Core/Number
  - MooTools.More
  - Locale
  - Locale.en-US.Date

provides: [Date]

...
*/

(function(){

    var Date = this.Date;

    var DateMethods = Date.Methods = {
        ms: 'Milliseconds',
        year: 'FullYear',
        min: 'Minutes',
        mo: 'Month',
        sec: 'Seconds',
        hr: 'Hours'
    };

    [
        'Date', 'Day', 'FullYear', 'Hours', 'Milliseconds', 'Minutes', 'Month', 'Seconds', 'Time', 'TimezoneOffset',
        'Week', 'Timezone', 'GMTOffset', 'DayOfYear', 'LastMonth', 'LastDayOfMonth', 'UTCDate', 'UTCDay', 'UTCFullYear',
        'AMPM', 'Ordinal', 'UTCHours', 'UTCMilliseconds', 'UTCMinutes', 'UTCMonth', 'UTCSeconds', 'UTCMilliseconds'
    ].each(function(method){
        Date.Methods[method.toLowerCase()] = method;
    });

    var pad = function(n, digits, string){
        if (digits == 1) return n;
        return n < Math.pow(10, digits - 1) ? (string || '0') + pad(n, digits - 1, string) : n;
    };

    Date.implement({

        set: function(prop, value){
            prop = prop.toLowerCase();
            var method = DateMethods[prop] && 'set' + DateMethods[prop];
            if (method && this[method]) this[method](value);
            return this;
        }.overloadSetter(),

        get: function(prop){
            prop = prop.toLowerCase();
            var method = DateMethods[prop] && 'get' + DateMethods[prop];
            if (method && this[method]) return this[method]();
            return null;
        }.overloadGetter(),

        clone: function(){
            return new Date(this.get('time'));
        },

        increment: function(interval, times){
            interval = interval || 'day';
            times = times != null ? times : 1;

            switch (interval){
                case 'year':
                    return this.increment('month', times * 12);
                case 'month':
                    var d = this.get('date');
                    this.set('date', 1).set('mo', this.get('mo') + times);
                    return this.set('date', d.min(this.get('lastdayofmonth')));
                case 'week':
                    return this.increment('day', times * 7);
                case 'day':
                    return this.set('date', this.get('date') + times);
            }

            if (!Date.units[interval]) throw new Error(interval + ' is not a supported interval');

            return this.set('time', this.get('time') + times * Date.units[interval]());
        },

        decrement: function(interval, times){
            return this.increment(interval, -1 * (times != null ? times : 1));
        },

        isLeapYear: function(){
            return Date.isLeapYear(this.get('year'));
        },

        clearTime: function(){
            return this.set({hr: 0, min: 0, sec: 0, ms: 0});
        },

        diff: function(date, resolution){
            if (typeOf(date) == 'string') date = Date.parse(date);

            return ((date - this) / Date.units[resolution || 'day'](3, 3)).round(); // non-leap year, 30-day month
        },

        getLastDayOfMonth: function(){
            return Date.daysInMonth(this.get('mo'), this.get('year'));
        },

        getDayOfYear: function(){
            return (Date.UTC(this.get('year'), this.get('mo'), this.get('date') + 1)
                - Date.UTC(this.get('year'), 0, 1)) / Date.units.day();
        },

        setDay: function(day, firstDayOfWeek){
            if (firstDayOfWeek == null){
                firstDayOfWeek = Date.getMsg('firstDayOfWeek');
                if (firstDayOfWeek === '') firstDayOfWeek = 1;
            }

            day = (7 + Date.parseDay(day, true) - firstDayOfWeek) % 7;
            var currentDay = (7 + this.get('day') - firstDayOfWeek) % 7;

            return this.increment('day', day - currentDay);
        },

        getWeek: function(firstDayOfWeek){
            if (firstDayOfWeek == null){
                firstDayOfWeek = Date.getMsg('firstDayOfWeek');
                if (firstDayOfWeek === '') firstDayOfWeek = 1;
            }

            var date = this,
                dayOfWeek = (7 + date.get('day') - firstDayOfWeek) % 7,
                dividend = 0,
                firstDayOfYear;

            if (firstDayOfWeek == 1){
                // ISO-8601, week belongs to year that has the most days of the week (i.e. has the thursday of the week)
                var month = date.get('month'),
                    startOfWeek = date.get('date') - dayOfWeek;

                if (month == 11 && startOfWeek > 28) return 1; // Week 1 of next year

                if (month == 0 && startOfWeek < -2){
                    // Use a date from last year to determine the week
                    date = new Date(date).decrement('day', dayOfWeek);
                    dayOfWeek = 0;
                }

                firstDayOfYear = new Date(date.get('year'), 0, 1).get('day') || 7;
                if (firstDayOfYear > 4) dividend = -7; // First week of the year is not week 1
            } else {
                // In other cultures the first week of the year is always week 1 and the last week always 53 or 54.
                // Days in the same week can have a different weeknumber if the week spreads across two years.
                firstDayOfYear = new Date(date.get('year'), 0, 1).get('day');
            }

            dividend += date.get('dayofyear');
            dividend += 6 - dayOfWeek; // Add days so we calculate the current date's week as a full week
            dividend += (7 + firstDayOfYear - firstDayOfWeek) % 7; // Make up for first week of the year not being a full week

            return (dividend / 7);
        },

        getOrdinal: function(day){
            return Date.getMsg('ordinal', day || this.get('date'));
        },

        getTimezone: function(){
            return this.toString()
                .replace(/^.*? ([A-Z]{3}).[0-9]{4}.*$/, '$1')
                .replace(/^.*?\(([A-Z])[a-z]+ ([A-Z])[a-z]+ ([A-Z])[a-z]+\)$/, '$1$2$3');
        },

        getGMTOffset: function(){
            var off = this.get('timezoneOffset');
            return ((off > 0) ? '-' : '+') + pad((off.abs() / 60).floor(), 2) + pad(off % 60, 2);
        },

        setAMPM: function(ampm){
            ampm = ampm.toUpperCase();
            var hr = this.get('hr');
            if (hr > 11 && ampm == 'AM') return this.decrement('hour', 12);
            else if (hr < 12 && ampm == 'PM') return this.increment('hour', 12);
            return this;
        },

        getAMPM: function(){
            return (this.get('hr') < 12) ? 'AM' : 'PM';
        },

        parse: function(str){
            this.set('time', Date.parse(str));
            return this;
        },

        isValid: function(date){
            if (!date) date = this;
            return typeOf(date) == 'date' && !isNaN(date.valueOf());
        },

        format: function(format){
            if (!this.isValid()) return 'invalid date';

            if (!format) format = '%x %X';
            if (typeof format == 'string') format = formats[format.toLowerCase()] || format;
            if (typeof format == 'function') return format(this);

            var d = this;
            return format.replace(/%([a-z%])/gi,
                function($0, $1){
                    switch ($1){
                        case 'a': return Date.getMsg('days_abbr')[d.get('day')];
                        case 'A': return Date.getMsg('days')[d.get('day')];
                        case 'b': return Date.getMsg('months_abbr')[d.get('month')];
                        case 'B': return Date.getMsg('months')[d.get('month')];
                        case 'c': return d.format('%a %b %d %H:%M:%S %Y');
                        case 'd': return pad(d.get('date'), 2);
                        case 'e': return pad(d.get('date'), 2, ' ');
                        case 'H': return pad(d.get('hr'), 2);
                        case 'I': return pad((d.get('hr') % 12) || 12, 2);
                        case 'j': return pad(d.get('dayofyear'), 3);
                        case 'k': return pad(d.get('hr'), 2, ' ');
                        case 'l': return pad((d.get('hr') % 12) || 12, 2, ' ');
                        case 'L': return pad(d.get('ms'), 3);
                        case 'm': return pad((d.get('mo') + 1), 2);
                        case 'M': return pad(d.get('min'), 2);
                        case 'o': return d.get('ordinal');
                        case 'p': return Date.getMsg(d.get('ampm'));
                        case 's': return Math.round(d / 1000);
                        case 'S': return pad(d.get('seconds'), 2);
                        case 'T': return d.format('%H:%M:%S');
                        case 'U': return pad(d.get('week'), 2);
                        case 'w': return d.get('day');
                        case 'x': return d.format(Date.getMsg('shortDate'));
                        case 'X': return d.format(Date.getMsg('shortTime'));
                        case 'y': return d.get('year').toString().substr(2);
                        case 'Y': return d.get('year');
                        case 'z': return d.get('GMTOffset');
                        case 'Z': return d.get('Timezone');
                    }
                    return $1;
                }
            );
        },

        toISOString: function(){
            return this.format('iso8601');
        }

    }).alias({
        toJSON: 'toISOString',
        compare: 'diff',
        strftime: 'format'
    });

// The day and month abbreviations are standardized, so we cannot use simply %a and %b because they will get localized
    var rfcDayAbbr = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
        rfcMonthAbbr = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

    var formats = {
        db: '%Y-%m-%d %H:%M:%S',
        compact: '%Y%m%dT%H%M%S',
        'short': '%d %b %H:%M',
        'long': '%B %d, %Y %H:%M',
        rfc822: function(date){
            return rfcDayAbbr[date.get('day')] + date.format(', %d ') + rfcMonthAbbr[date.get('month')] + date.format(' %Y %H:%M:%S %Z');
        },
        rfc2822: function(date){
            return rfcDayAbbr[date.get('day')] + date.format(', %d ') + rfcMonthAbbr[date.get('month')] + date.format(' %Y %H:%M:%S %z');
        },
        iso8601: function(date){
            return (
                date.getUTCFullYear() + '-' +
                pad(date.getUTCMonth() + 1, 2) + '-' +
                pad(date.getUTCDate(), 2) + 'T' +
                pad(date.getUTCHours(), 2) + ':' +
                pad(date.getUTCMinutes(), 2) + ':' +
                pad(date.getUTCSeconds(), 2) + '.' +
                pad(date.getUTCMilliseconds(), 3) + 'Z'
            );
        }
    };

    var parsePatterns = [],
        nativeParse = Date.parse;

    var parseWord = function(type, word, num){
        var ret = -1,
            translated = Date.getMsg(type + 's');
        switch (typeOf(word)){
            case 'object':
                ret = translated[word.get(type)];
                break;
            case 'number':
                ret = translated[word];
                if (!ret) throw new Error('Invalid ' + type + ' index: ' + word);
                break;
            case 'string':
                var match = translated.filter(function(name){
                    return this.test(name);
                }, new RegExp('^' + word, 'i'));
                if (!match.length) throw new Error('Invalid ' + type + ' string');
                if (match.length > 1) throw new Error('Ambiguous ' + type);
                ret = match[0];
        }

        return (num) ? translated.indexOf(ret) : ret;
    };

    var startCentury = 1900,
        startYear = 70;

    Date.extend({

        getMsg: function(key, args){
            return Locale.get('Date.' + key, args);
        },

        units: {
            ms: Function.convert(1),
            second: Function.convert(1000),
            minute: Function.convert(60000),
            hour: Function.convert(3600000),
            day: Function.convert(86400000),
            week: Function.convert(608400000),
            month: function(month, year){
                var d = new Date;
                return Date.daysInMonth(month != null ? month : d.get('mo'), year != null ? year : d.get('year')) * 86400000;
            },
            year: function(year){
                year = year || new Date().get('year');
                return Date.isLeapYear(year) ? 31622400000 : 31536000000;
            }
        },

        daysInMonth: function(month, year){
            return [31, Date.isLeapYear(year) ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month];
        },

        isLeapYear: function(year){
            return ((year % 4 === 0) && (year % 100 !== 0)) || (year % 400 === 0);
        },

        parse: function(from){
            var t = typeOf(from);
            if (t == 'number') return new Date(from);
            if (t != 'string') return from;
            from = from.clean();
            if (!from.length) return null;

            var parsed;
            parsePatterns.some(function(pattern){
                var bits = pattern.re.exec(from);
                return (bits) ? (parsed = pattern.handler(bits)) : false;
            });

            if (!(parsed && parsed.isValid())){
                parsed = new Date(nativeParse(from));
                if (!(parsed && parsed.isValid())) parsed = new Date(from.toInt());
            }
            return parsed;
        },

        parseDay: function(day, num){
            return parseWord('day', day, num);
        },

        parseMonth: function(month, num){
            return parseWord('month', month, num);
        },

        parseUTC: function(value){
            var localDate = new Date(value);
            var utcSeconds = Date.UTC(
                localDate.get('year'),
                localDate.get('mo'),
                localDate.get('date'),
                localDate.get('hr'),
                localDate.get('min'),
                localDate.get('sec'),
                localDate.get('ms')
            );
            return new Date(utcSeconds);
        },

        orderIndex: function(unit){
            return Date.getMsg('dateOrder').indexOf(unit) + 1;
        },

        defineFormat: function(name, format){
            formats[name] = format;
            return this;
        },

        //<1.2compat>
        parsePatterns: parsePatterns,
        //</1.2compat>

        defineParser: function(pattern){
            parsePatterns.push((pattern.re && pattern.handler) ? pattern : build(pattern));
            return this;
        },

        defineParsers: function(){
            Array.flatten(arguments).each(Date.defineParser);
            return this;
        },

        define2DigitYearStart: function(year){
            startYear = year % 100;
            startCentury = year - startYear;
            return this;
        }

    }).extend({
        defineFormats: Date.defineFormat.overloadSetter()
    });

    var regexOf = function(type){
        return new RegExp('(?:' + Date.getMsg(type).map(function(name){
            return name.substr(0, 3);
        }).join('|') + ')[a-z]*');
    };

    var replacers = function(key){
        switch (key){
            case 'T':
                return '%H:%M:%S';
            case 'x': // iso8601 covers yyyy-mm-dd, so just check if month is first
                return ((Date.orderIndex('month') == 1) ? '%m[-./]%d' : '%d[-./]%m') + '([-./]%y)?';
            case 'X':
                return '%H([.:]%M)?([.:]%S([.:]%s)?)? ?%p? ?%z?';
        }
        return null;
    };

    var keys = {
        d: /[0-2]?[0-9]|3[01]/,
        H: /[01]?[0-9]|2[0-3]/,
        I: /0?[1-9]|1[0-2]/,
        M: /[0-5]?\d/,
        s: /\d+/,
        o: /[a-z]*/,
        p: /[ap]\.?m\.?/,
        y: /\d{2}|\d{4}/,
        Y: /\d{4}/,
        z: /Z|[+-]\d{2}(?::?\d{2})?/
    };

    keys.m = keys.I;
    keys.S = keys.M;

    var currentLanguage;

    var recompile = function(language){
        currentLanguage = language;

        keys.a = keys.A = regexOf('days');
        keys.b = keys.B = regexOf('months');

        parsePatterns.each(function(pattern, i){
            if (pattern.format) parsePatterns[i] = build(pattern.format);
        });
    };

    var build = function(format){
        if (!currentLanguage) return {format: format};

        var parsed = [];
        var re = (format.source || format) // allow format to be regex
            .replace(/%([a-z])/gi,
                function($0, $1){
                    return replacers($1) || $0;
                }
            ).replace(/\((?!\?)/g, '(?:') // make all groups non-capturing
            .replace(/ (?!\?|\*)/g, ',? ') // be forgiving with spaces and commas
            .replace(/%([a-z%])/gi,
                function($0, $1){
                    var p = keys[$1];
                    if (!p) return $1;
                    parsed.push($1);
                    return '(' + p.source + ')';
                }
            ).replace(/\[a-z\]/gi, '[a-z\\u00c0-\\uffff;\&]'); // handle unicode words

        return {
            format: format,
            re: new RegExp('^' + re + '$', 'i'),
            handler: function(bits){
                bits = bits.slice(1).associate(parsed);
                var date = new Date().clearTime(),
                    year = bits.y || bits.Y;

                if (year != null) handle.call(date, 'y', year); // need to start in the right year
                if ('d' in bits) handle.call(date, 'd', 1);
                if ('m' in bits || bits.b || bits.B) handle.call(date, 'm', 1);

                for (var key in bits) handle.call(date, key, bits[key]);
                return date;
            }
        };
    };

    var handle = function(key, value){
        if (!value) return this;

        switch (key){
            case 'a': case 'A': return this.set('day', Date.parseDay(value, true));
            case 'b': case 'B': return this.set('mo', Date.parseMonth(value, true));
            case 'd': return this.set('date', value);
            case 'H': case 'I': return this.set('hr', value);
            case 'm': return this.set('mo', value - 1);
            case 'M': return this.set('min', value);
            case 'p': return this.set('ampm', value.replace(/\./g, ''));
            case 'S': return this.set('sec', value);
            case 's': return this.set('ms', ('0.' + value) * 1000);
            case 'w': return this.set('day', value);
            case 'Y': return this.set('year', value);
            case 'y':
                value = +value;
                if (value < 100) value += startCentury + (value < startYear ? 100 : 0);
                return this.set('year', value);
            case 'z':
                if (value == 'Z') value = '+00';
                var offset = value.match(/([+-])(\d{2}):?(\d{2})?/);
                offset = (offset[1] + '1') * (offset[2] * 60 + (+offset[3] || 0)) + this.getTimezoneOffset();
                return this.set('time', this - offset * 60000);
        }

        return this;
    };

    Date.defineParsers(
        '%Y([-./]%m([-./]%d((T| )%X)?)?)?', // "1999-12-31", "1999-12-31 11:59pm", "1999-12-31 23:59:59", ISO8601
        '%Y%m%d(T%H(%M%S?)?)?', // "19991231", "19991231T1159", compact
        '%x( %X)?', // "12/31", "12.31.99", "12-31-1999", "12/31/2008 11:59 PM"
        '%d%o( %b( %Y)?)?( %X)?', // "31st", "31st December", "31 Dec 1999", "31 Dec 1999 11:59pm"
        '%b( %d%o)?( %Y)?( %X)?', // Same as above with month and day switched
        '%Y %b( %d%o( %X)?)?', // Same as above with year coming first
        '%o %b %d %X %z %Y', // "Thu Oct 22 08:11:23 +0000 2009"
        '%T', // %H:%M:%S
        '%H:%M( ?%p)?' // "11:05pm", "11:05 am" and "11:05"
    );

    Locale.addEvent('change', function(language){
        if (Locale.get('Date')) recompile(language);
    }).fireEvent('change', Locale.getCurrent());

})();

/*
---

name: Locale.en-US.Form.Validator

description: Form Validator messages for English.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Locale

provides: [Locale.en-US.Form.Validator]

...
*/

Locale.define('en-US', 'FormValidator', {

    required: 'This field is required.',
    length: 'Please enter {length} characters (you entered {elLength} characters)',
    minLength: 'Please enter at least {minLength} characters (you entered {length} characters).',
    maxLength: 'Please enter no more than {maxLength} characters (you entered {length} characters).',
    integer: 'Please enter an integer in this field. Numbers with decimals (e.g. 1.25) are not permitted.',
    numeric: 'Please enter only numeric values in this field (i.e. "1" or "1.1" or "-1" or "-1.1").',
    digits: 'Please use numbers and punctuation only in this field (for example, a phone number with dashes or dots is permitted).',
    alpha: 'Please use only letters (a-z) within this field. No spaces or other characters are allowed.',
    alphanum: 'Please use only letters (a-z) or numbers (0-9) in this field. No spaces or other characters are allowed.',
    dateSuchAs: 'Please enter a valid date such as {date}',
    dateInFormatMDY: 'Please enter a valid date such as MM/DD/YYYY (i.e. "12/31/1999")',
    email: 'Please enter a valid email address. For example "fred@domain.com".',
    url: 'Please enter a valid URL such as http://www.example.com.',
    currencyDollar: 'Please enter a valid $ amount. For example $100.00 .',
    oneRequired: 'Please enter something for at least one of these inputs.',
    errorPrefix: 'Error: ',
    warningPrefix: 'Warning: ',

    // Form.Validator.Extras
    noSpace: 'There can be no spaces in this input.',
    reqChkByNode: 'No items are selected.',
    requiredChk: 'This field is required.',
    reqChkByName: 'Please select a {label}.',
    match: 'This field needs to match the {matchName} field',
    startDate: 'the start date',
    endDate: 'the end date',
    currentDate: 'the current date',
    afterDate: 'The date should be the same or after {label}.',
    beforeDate: 'The date should be the same or before {label}.',
    startMonth: 'Please select a start month',
    sameMonth: 'These two dates must be in the same month - you must change one or the other.',
    creditcard: 'The credit card number entered is invalid. Please check the number and try again. {length} digits entered.'

});

/*
---

script: Form.Validator.js

name: Form.Validator

description: A css-class based form validation system.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Core/Options
  - Core/Events
  - Core/Element.Delegation
  - Core/Slick.Finder
  - Core/Element.Event
  - Core/Element.Style
  - Core/JSON
  - Locale
  - Class.Binds
  - Date
  - Element.Forms
  - Locale.en-US.Form.Validator
  - Element.Shortcuts

provides: [Form.Validator, InputValidator, FormValidator.BaseValidators]

...
*/
if (!window.Form) window.Form = {};

var InputValidator = this.InputValidator = new Class({

    Implements: [Options],

    options: {
        errorMsg: 'Validation failed.',
        test: Function.convert(true)
    },

    initialize: function(className, options){
        this.setOptions(options);
        this.className = className;
    },

    test: function(field, props){
        field = document.id(field);
        return (field) ? this.options.test(field, props || this.getProps(field)) : false;
    },

    getError: function(field, props){
        field = document.id(field);
        var err = this.options.errorMsg;
        if (typeOf(err) == 'function') err = err(field, props || this.getProps(field));
        return err;
    },

    getProps: function(field){
        field = document.id(field);
        return (field) ? field.get('validatorProps') : {};
    }

});

Element.Properties.validators = {

    get: function(){
        return (this.get('data-validators') || this.className).clean().replace(/'(\\.|[^'])*'|"(\\.|[^"])*"/g, function(match){
            return match.replace(' ', '\\x20');
        }).split(' ');
    }

};

Element.Properties.validatorProps = {

    set: function(props){
        return this.eliminate('$moo:validatorProps').store('$moo:validatorProps', props);
    },

    get: function(props){
        if (props) this.set(props);
        if (this.retrieve('$moo:validatorProps')) return this.retrieve('$moo:validatorProps');
        if (this.getProperty('data-validator-properties') || this.getProperty('validatorProps')){
            try {
                this.store('$moo:validatorProps', JSON.decode(this.getProperty('validatorProps') || this.getProperty('data-validator-properties'), false));
            } catch (e){
                return {};
            }
        } else {
            var vals = this.get('validators').filter(function(cls){
                return cls.test(':');
            });
            if (!vals.length){
                this.store('$moo:validatorProps', {});
            } else {
                props = {};
                vals.each(function(cls){
                    var split = cls.split(':');
                    if (split[1]){
                        try {
                            props[split[0]] = JSON.decode(split[1], false);
                        } catch (e){}
                    }
                });
                this.store('$moo:validatorProps', props);
            }
        }
        return this.retrieve('$moo:validatorProps');
    }

};

Form.Validator = new Class({

    Implements: [Options, Events],

    options: {/*
		onFormValidate: function(isValid, form, event){},
		onElementValidate: function(isValid, field, className, warn){},
		onElementPass: function(field){},
		onElementFail: function(field, validatorsFailed){}, */
        fieldSelectors: 'input, select, textarea',
        ignoreHidden: true,
        ignoreDisabled: true,
        useTitles: false,
        evaluateOnSubmit: true,
        evaluateFieldsOnBlur: true,
        evaluateFieldsOnChange: true,
        serial: true,
        stopOnFailure: true,
        warningPrefix: function(){
            return Form.Validator.getMsg('warningPrefix') || 'Warning: ';
        },
        errorPrefix: function(){
            return Form.Validator.getMsg('errorPrefix') || 'Error: ';
        }
    },

    initialize: function(form, options){
        this.setOptions(options);
        this.element = document.id(form);
        this.warningPrefix = Function.convert(this.options.warningPrefix)();
        this.errorPrefix = Function.convert(this.options.errorPrefix)();
        this._bound = {
            onSubmit: this.onSubmit.bind(this),
            blurOrChange: function(event, field){
                this.validationMonitor(field, true);
            }.bind(this)
        };
        this.enable();
    },

    toElement: function(){
        return this.element;
    },

    getFields: function(){
        return (this.fields = this.element.getElements(this.options.fieldSelectors));
    },

    enable: function(){
        this.element.store('validator', this);
        if (this.options.evaluateOnSubmit) this.element.addEvent('submit', this._bound.onSubmit);
        if (this.options.evaluateFieldsOnBlur){
            this.element.addEvent('blur:relay(input,select,textarea)', this._bound.blurOrChange);
        }
        if (this.options.evaluateFieldsOnChange){
            this.element.addEvent('change:relay(input,select,textarea)', this._bound.blurOrChange);
        }
    },

    disable: function(){
        this.element.eliminate('validator');
        this.element.removeEvents({
            submit: this._bound.onSubmit,
            'blur:relay(input,select,textarea)': this._bound.blurOrChange,
            'change:relay(input,select,textarea)': this._bound.blurOrChange
        });
    },

    validationMonitor: function(){
        clearTimeout(this.timer);
        this.timer = this.validateField.delay(50, this, arguments);
    },

    onSubmit: function(event){
        if (this.validate(event)) this.reset();
    },

    reset: function(){
        this.getFields().each(this.resetField, this);
        return this;
    },

    validate: function(event){
        var result = this.getFields().map(function(field){
            return this.validateField(field, true);
        }, this).every(function(v){
            return v;
        });
        this.fireEvent('formValidate', [result, this.element, event]);
        if (this.options.stopOnFailure && !result && event) event.preventDefault();
        return result;
    },

    validateField: function(field, force){
        if (this.paused) return true;
        field = document.id(field);
        var passed = !field.hasClass('validation-failed');
        var failed, warned;
        if (this.options.serial && !force){
            failed = this.element.getElement('.validation-failed');
            warned = this.element.getElement('.warning');
        }
        if (field && (!failed || force || field.hasClass('validation-failed') || (failed && !this.options.serial))){
            var validationTypes = field.get('validators');
            var validators = validationTypes.some(function(cn){
                return this.getValidator(cn);
            }, this);
            var validatorsFailed = [];
            validationTypes.each(function(className){
                if (className && !this.test(className, field)) validatorsFailed.include(className);
            }, this);
            passed = validatorsFailed.length === 0;
            if (validators && !this.hasValidator(field, 'warnOnly')){
                if (passed){
                    field.addClass('validation-passed').removeClass('validation-failed');
                    this.fireEvent('elementPass', [field]);
                } else {
                    field.addClass('validation-failed').removeClass('validation-passed');
                    this.fireEvent('elementFail', [field, validatorsFailed]);
                }
            }
            if (!warned){
                var warnings = validationTypes.some(function(cn){
                    if (cn.test('^warn'))
                        return this.getValidator(cn.replace(/^warn-/,''));
                    else return null;
                }, this);
                field.removeClass('warning');
                var warnResult = validationTypes.map(function(cn){
                    if (cn.test('^warn'))
                        return this.test(cn.replace(/^warn-/,''), field, true);
                    else return null;
                }, this);
            }
        }
        return passed;
    },

    test: function(className, field, warn){
        field = document.id(field);
        if ((this.options.ignoreHidden && !field.isVisible()) || (this.options.ignoreDisabled && field.get('disabled'))) return true;
        var validator = this.getValidator(className);
        if (warn != null) warn = false;
        if (this.hasValidator(field, 'warnOnly')) warn = true;
        var isValid = field.hasClass('ignoreValidation') || (validator ? validator.test(field) : true);
        if (validator) this.fireEvent('elementValidate', [isValid, field, className, warn]);
        if (warn) return true;
        return isValid;
    },

    hasValidator: function(field, value){
        return field.get('validators').contains(value);
    },

    resetField: function(field){
        field = document.id(field);
        if (field){
            field.get('validators').each(function(className){
                if (className.test('^warn-')) className = className.replace(/^warn-/, '');
                field.removeClass('validation-failed');
                field.removeClass('warning');
                field.removeClass('validation-passed');
            }, this);
        }
        return this;
    },

    stop: function(){
        this.paused = true;
        return this;
    },

    start: function(){
        this.paused = false;
        return this;
    },

    ignoreField: function(field, warn){
        field = document.id(field);
        if (field){
            this.enforceField(field);
            if (warn) field.addClass('warnOnly');
            else field.addClass('ignoreValidation');
        }
        return this;
    },

    enforceField: function(field){
        field = document.id(field);
        if (field) field.removeClass('warnOnly').removeClass('ignoreValidation');
        return this;
    }

});

Form.Validator.getMsg = function(key){
    return Locale.get('FormValidator.' + key);
};

Form.Validator.adders = {

    validators:{},

    add : function(className, options){
        this.validators[className] = new InputValidator(className, options);
        //if this is a class (this method is used by instances of Form.Validator and the Form.Validator namespace)
        //extend these validators into it
        //this allows validators to be global and/or per instance
        if (!this.initialize){
            this.implement({
                validators: this.validators
            });
        }
    },

    addAllThese : function(validators){
        Array.convert(validators).each(function(validator){
            this.add(validator[0], validator[1]);
        }, this);
    },

    getValidator: function(className){
        return this.validators[className.split(':')[0]];
    }

};

Object.append(Form.Validator, Form.Validator.adders);

Form.Validator.implement(Form.Validator.adders);

Form.Validator.add('IsEmpty', {

    errorMsg: false,
    test: function(element){
        if (element.type == 'select-one' || element.type == 'select')
            return !(element.selectedIndex >= 0 && element.options[element.selectedIndex].value != '');
        else
            return ((element.get('value') == null) || (element.get('value').length == 0));
    }

});

Form.Validator.addAllThese([

    ['required', {
        errorMsg: function(){
            return Form.Validator.getMsg('required');
        },
        test: function(element){
            return !Form.Validator.getValidator('IsEmpty').test(element);
        }
    }],

    ['length', {
        errorMsg: function(element, props){
            if (typeOf(props.length) != 'null')
                return Form.Validator.getMsg('length').substitute({length: props.length, elLength: element.get('value').length});
            else return '';
        },
        test: function(element, props){
            if (typeOf(props.length) != 'null') return (element.get('value').length == props.length || element.get('value').length == 0);
            else return true;
        }
    }],

    ['minLength', {
        errorMsg: function(element, props){
            if (typeOf(props.minLength) != 'null')
                return Form.Validator.getMsg('minLength').substitute({minLength: props.minLength, length: element.get('value').length});
            else return '';
        },
        test: function(element, props){
            if (typeOf(props.minLength) != 'null') return (element.get('value').length >= (props.minLength || 0));
            else return true;
        }
    }],

    ['maxLength', {
        errorMsg: function(element, props){
            //props is {maxLength:10}
            if (typeOf(props.maxLength) != 'null')
                return Form.Validator.getMsg('maxLength').substitute({maxLength: props.maxLength, length: element.get('value').length});
            else return '';
        },
        test: function(element, props){
            return element.get('value').length <= (props.maxLength || 10000);
        }
    }],

    ['validate-integer', {
        errorMsg: Form.Validator.getMsg.pass('integer'),
        test: function(element){
            return Form.Validator.getValidator('IsEmpty').test(element) || (/^(-?[1-9]\d*|0)$/).test(element.get('value'));
        }
    }],

    ['validate-numeric', {
        errorMsg: Form.Validator.getMsg.pass('numeric'),
        test: function(element){
            return Form.Validator.getValidator('IsEmpty').test(element) ||
                (/^-?(?:0$0(?=\d*\.)|[1-9]|0)\d*(\.\d+)?$/).test(element.get('value'));
        }
    }],

    ['validate-digits', {
        errorMsg: Form.Validator.getMsg.pass('digits'),
        test: function(element){
            return Form.Validator.getValidator('IsEmpty').test(element) || (/^[\d() .:\-\+#]+$/.test(element.get('value')));
        }
    }],

    ['validate-alpha', {
        errorMsg: Form.Validator.getMsg.pass('alpha'),
        test: function(element){
            return Form.Validator.getValidator('IsEmpty').test(element) || (/^[a-zA-Z]+$/).test(element.get('value'));
        }
    }],

    ['validate-alphanum', {
        errorMsg: Form.Validator.getMsg.pass('alphanum'),
        test: function(element){
            return Form.Validator.getValidator('IsEmpty').test(element) || !(/\W/).test(element.get('value'));
        }
    }],

    ['validate-date', {
        errorMsg: function(element, props){
            if (Date.parse){
                var format = props.dateFormat || '%x';
                return Form.Validator.getMsg('dateSuchAs').substitute({date: new Date().format(format)});
            } else {
                return Form.Validator.getMsg('dateInFormatMDY');
            }
        },
        test: function(element, props){
            if (Form.Validator.getValidator('IsEmpty').test(element)) return true;
            var dateLocale = Locale.get('Date'),
                dateNouns = new RegExp([dateLocale.days, dateLocale.days_abbr, dateLocale.months, dateLocale.months_abbr, dateLocale.AM, dateLocale.PM].flatten().join('|'), 'i'),
                value = element.get('value'),
                wordsInValue = value.match(/[a-z]+/gi);

            if (wordsInValue && !wordsInValue.every(dateNouns.exec, dateNouns)) return false;

            var date = Date.parse(value);
            if (!date) return false;

            var format = props.dateFormat || '%x',
                formatted = date.format(format);
            if (formatted != 'invalid date') element.set('value', formatted);
            return date.isValid();
        }
    }],

    ['validate-email', {
        errorMsg: Form.Validator.getMsg.pass('email'),
        test: function(element){
            /*
			var chars = "[a-z0-9!#$%&'*+/=?^_`{|}~-]",
				local = '(?:' + chars + '\\.?){0,63}' + chars,

				label = '[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?',
				hostname = '(?:' + label + '\\.)*' + label;

				octet = '(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)',
				ipv4 = '\\[(?:' + octet + '\\.){3}' + octet + '\\]',

				domain = '(?:' + hostname + '|' + ipv4 + ')';

			var regex = new RegExp('^' + local + '@' + domain + '$', 'i');
			*/
            return Form.Validator.getValidator('IsEmpty').test(element) || (/^(?:[a-z0-9!#$%&'*+\/=?^_`{|}~-]\.?){0,63}[a-z0-9!#$%&'*+\/=?^_`{|}~-]@(?:(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\.)*[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\])$/i).test(element.get('value'));
        }
    }],

    ['validate-url', {
        errorMsg: Form.Validator.getMsg.pass('url'),
        test: function(element){
            return Form.Validator.getValidator('IsEmpty').test(element) || (/^(https?|ftp|rmtp|mms):\/\/(([A-Z0-9][A-Z0-9_-]*)(\.[A-Z0-9][A-Z0-9_-]*)+)(:(\d+))?\/?/i).test(element.get('value'));
        }
    }],

    ['validate-currency-dollar', {
        errorMsg: Form.Validator.getMsg.pass('currencyDollar'),
        test: function(element){
            return Form.Validator.getValidator('IsEmpty').test(element) || (/^\$?\-?([1-9]{1}[0-9]{0,2}(\,[0-9]{3})*(\.[0-9]{0,2})?|[1-9]{1}\d*(\.[0-9]{0,2})?|0(\.[0-9]{0,2})?|(\.[0-9]{1,2})?)$/).test(element.get('value'));
        }
    }],

    ['validate-one-required', {
        errorMsg: Form.Validator.getMsg.pass('oneRequired'),
        test: function(element, props){
            var p = document.id(props['validate-one-required']) || element.getParent(props['validate-one-required']);
            return p.getElements('input').some(function(el){
                if (['checkbox', 'radio'].contains(el.get('type'))) return el.get('checked');
                return el.get('value');
            });
        }
    }]

]);

Element.Properties.validator = {

    set: function(options){
        this.get('validator').setOptions(options);
    },

    get: function(){
        var validator = this.retrieve('validator');
        if (!validator){
            validator = new Form.Validator(this);
            this.store('validator', validator);
        }
        return validator;
    }

};

Element.implement({

    validate: function(options){
        if (options) this.set('validator', options);
        return this.get('validator').validate();
    }

});


//<1.2compat>
//legacy
var FormValidator = Form.Validator;
//</1.2compat>



/*
---

script: Form.Validator.Extras.js

name: Form.Validator.Extras

description: Additional validators for the Form.Validator class.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Form.Validator

provides: [Form.Validator.Extras]

...
*/

(function(){

    function getItems(props, preference, children, cssSelector){
        if (preference && props[preference]) return props[preference];
        var el = document.id(props[children]);
        if (!el) return [];
        return el.getElements(cssSelector);
    }

    Form.Validator.addAllThese([

        ['validate-enforce-oncheck', {
            test: function(element, props){
                var fv = element.getParent('form').retrieve('validator');
                if (!fv) return true;
                getItems(props, 'toEnforce', 'enforceChildrenOf', 'input, select, textarea').each(function(item){
                    if (element.checked){
                        fv.enforceField(item);
                    } else {
                        fv.ignoreField(item);
                        fv.resetField(item);
                    }
                });
                return true;
            }
        }],

        ['validate-ignore-oncheck', {
            test: function(element, props){
                var fv = element.getParent('form').retrieve('validator');
                if (!fv) return true;
                getItems(props, 'toIgnore', 'ignoreChildrenOf', 'input, select, textarea').each(function(item){
                    if (element.checked){
                        fv.ignoreField(item);
                        fv.resetField(item);
                    } else {
                        fv.enforceField(item);
                    }
                });
                return true;
            }
        }],

        ['validate-enforce-onselect-value', {
            test: function(element, props){
                if (!props.value) return true;
                var fv = element.getParent('form').retrieve('validator');
                if (!fv) return true;
                getItems(props, 'toEnforce', 'enforceChildrenOf', 'input, select, textarea').each(function(item){
                    if (props.value == element.value){
                        fv.enforceField(item);
                    } else {
                        fv.ignoreField(item);
                        fv.resetField(item);
                    }
                });
                return true;
            }
        }],

        ['validate-nospace', {
            errorMsg: function(){
                return Form.Validator.getMsg('noSpace');
            },
            test: function(element, props){
                return !element.get('value').test(/\s/);
            }
        }],

        ['validate-toggle-oncheck', {
            test: function(element, props){
                var fv = element.getParent('form').retrieve('validator');
                if (!fv) return true;
                var eleArr = getItems(props, 'toToggle', 'toToggleChildrenOf', 'input, select, textarea');
                if (!element.checked){
                    eleArr.each(function(item){
                        fv.ignoreField(item);
                        fv.resetField(item);
                    });
                } else {
                    eleArr.each(function(item){
                        fv.enforceField(item);
                    });
                }
                return true;
            }
        }],

        ['validate-reqchk-bynode', {
            errorMsg: function(){
                return Form.Validator.getMsg('reqChkByNode');
            },
            test: function(element, props){
                return getItems(props, false, 'nodeId', props.selector || 'input[type=checkbox], input[type=radio]').some(function(item){
                    return item.checked;
                });
            }
        }],

        ['validate-required-check', {
            errorMsg: function(element, props){
                return props.useTitle ? element.get('title') : Form.Validator.getMsg('requiredChk');
            },
            test: function(element, props){
                return !!element.checked;
            }
        }],

        ['validate-reqchk-byname', {
            errorMsg: function(element, props){
                return Form.Validator.getMsg('reqChkByName').substitute({label: props.label || element.get('type')});
            },
            test: function(element, props){
                var grpName = props.groupName || element.get('name');
                var grpNameEls = $$('[name=' + grpName +']');
                var oneCheckedItem = grpNameEls.some(function(item, index){
                    return item.checked;
                });
                var fv = element.getParent('form').retrieve('validator');
                if (oneCheckedItem && fv){
                    grpNameEls.each(function(item, index){ fv.resetField(item); });
                }
                return oneCheckedItem;
            }
        }],

        ['validate-match', {
            errorMsg: function(element, props){
                return Form.Validator.getMsg('match').substitute({matchName: decodeURIComponent((props.matchName+'').replace(/\+/g, '%20')) || document.id(props.matchInput).get('name')});
            },
            test: function(element, props){
                var eleVal = element.get('value');
                var matchVal = document.id(props.matchInput) && document.id(props.matchInput).get('value');
                return eleVal && matchVal ? eleVal == matchVal : true;
            }
        }],

        ['validate-after-date', {
            errorMsg: function(element, props){
                return Form.Validator.getMsg('afterDate').substitute({
                    label: props.afterLabel || (props.afterElement ? Form.Validator.getMsg('startDate') : Form.Validator.getMsg('currentDate'))
                });
            },
            test: function(element, props){
                var start = document.id(props.afterElement) ? Date.parse(document.id(props.afterElement).get('value')) : new Date();
                var end = Date.parse(element.get('value'));
                return end && start ? end >= start : true;
            }
        }],

        ['validate-before-date', {
            errorMsg: function(element, props){
                return Form.Validator.getMsg('beforeDate').substitute({
                    label: props.beforeLabel || (props.beforeElement ? Form.Validator.getMsg('endDate') : Form.Validator.getMsg('currentDate'))
                });
            },
            test: function(element, props){
                var start = Date.parse(element.get('value'));
                var end = document.id(props.beforeElement) ? Date.parse(document.id(props.beforeElement).get('value')) : new Date();
                return end && start ? end >= start : true;
            }
        }],

        ['validate-custom-required', {
            errorMsg: function(){
                return Form.Validator.getMsg('required');
            },
            test: function(element, props){
                return element.get('value') != props.emptyValue;
            }
        }],

        ['validate-same-month', {
            errorMsg: function(element, props){
                var startMo = document.id(props.sameMonthAs) && document.id(props.sameMonthAs).get('value');
                var eleVal = element.get('value');
                if (eleVal != '') return Form.Validator.getMsg(startMo ? 'sameMonth' : 'startMonth');
            },
            test: function(element, props){
                var d1 = Date.parse(element.get('value'));
                var d2 = Date.parse(document.id(props.sameMonthAs) && document.id(props.sameMonthAs).get('value'));
                return d1 && d2 ? d1.format('%B') == d2.format('%B') : true;
            }
        }],


        ['validate-cc-num', {
            errorMsg: function(element){
                var ccNum = element.get('value').replace(/[^0-9]/g, '');
                return Form.Validator.getMsg('creditcard').substitute({length: ccNum.length});
            },
            test: function(element){
                // required is a different test
                if (Form.Validator.getValidator('IsEmpty').test(element)) return true;

                // Clean number value
                var ccNum = element.get('value');
                ccNum = ccNum.replace(/[^0-9]/g, '');

                var validType = false;

                if (ccNum.test(/^4[0-9]{12}([0-9]{3})?$/)) validType = 'Visa';
                else if (ccNum.test(/^5[1-5]([0-9]{14})$/)) validType = 'Master Card';
                else if (ccNum.test(/^3[47][0-9]{13}$/)) validType = 'American Express';
                else if (ccNum.test(/^6(?:011|5[0-9]{2})[0-9]{12}$/)) validType = 'Discover';
                else if (ccNum.test(/^3(?:0[0-5]|[68][0-9])[0-9]{11}$/)) validType = 'Diners Club';

                if (validType){
                    var sum = 0;
                    var cur = 0;

                    for (var i=ccNum.length-1; i>=0; --i){
                        cur = ccNum.charAt(i).toInt();
                        if (cur == 0) continue;

                        if ((ccNum.length-i) % 2 == 0) cur += cur;
                        if (cur > 9){
                            cur = cur.toString().charAt(0).toInt() + cur.toString().charAt(1).toInt();
                        }

                        sum += cur;
                    }
                    if ((sum % 10) == 0) return true;
                }

                var chunks = '';
                while (ccNum != ''){
                    chunks += ' ' + ccNum.substr(0,4);
                    ccNum = ccNum.substr(4);
                }

                element.getParent('form').retrieve('validator').ignoreField(element);
                element.set('value', chunks.clean());
                element.getParent('form').retrieve('validator').enforceField(element);
                return false;
            }
        }]

    ]);

})();

/*
---

script: Form.Validator.Inline.js

name: Form.Validator.Inline

description: Extends Form.Validator to add inline messages.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Form.Validator

provides: [Form.Validator.Inline]

...
*/

Form.Validator.Inline = new Class({

    Extends: Form.Validator,

    options: {
        showError: function(errorElement){
            if (errorElement.reveal) errorElement.reveal();
            else errorElement.setStyle('display', 'block');
        },
        hideError: function(errorElement){
            if (errorElement.dissolve) errorElement.dissolve();
            else errorElement.setStyle('display', 'none');
        },
        scrollToErrorsOnSubmit: true,
        scrollToErrorsOnBlur: false,
        scrollToErrorsOnChange: false,
        scrollFxOptions: {
            transition: 'quad:out',
            offset: {
                y: -20
            }
        }
    },

    initialize: function(form, options){
        this.parent(form, options);
        this.addEvent('onElementValidate', function(isValid, field, className, warn){
            var validator = this.getValidator(className);
            if (!isValid && validator.getError(field)){
                if (warn) field.addClass('warning');
                var advice = this.makeAdvice(className, field, validator.getError(field), warn);
                this.insertAdvice(advice, field);
                this.showAdvice(className, field);
            } else {
                this.hideAdvice(className, field);
            }
        });
    },

    makeAdvice: function(className, field, error, warn){
        var errorMsg = (warn) ? this.warningPrefix : this.errorPrefix;
        errorMsg += (this.options.useTitles) ? field.title || error:error;
        var cssClass = (warn) ? 'warning-advice' : 'validation-advice';
        var advice = this.getAdvice(className, field);
        if (advice){
            advice = advice.set('html', errorMsg);
        } else {
            advice = new Element('div', {
                html: errorMsg,
                styles: { display: 'none' },
                id: 'advice-' + className.split(':')[0] + '-' + this.getFieldId(field)
            }).addClass(cssClass);
        }
        field.store('$moo:advice-' + className, advice);
        return advice;
    },

    getFieldId : function(field){
        return field.id ? field.id : field.id = 'input_' + field.name;
    },

    showAdvice: function(className, field){
        var advice = this.getAdvice(className, field);
        if (
            advice &&
            !field.retrieve('$moo:' + this.getPropName(className)) &&
            (
                advice.getStyle('display') == 'none' ||
                advice.getStyle('visibility') == 'hidden' ||
                advice.getStyle('opacity') == 0
            )
        ){
            field.store('$moo:' + this.getPropName(className), true);
            this.options.showError(advice);
            this.fireEvent('showAdvice', [field, advice, className]);
        }
    },

    hideAdvice: function(className, field){
        var advice = this.getAdvice(className, field);
        if (advice && field.retrieve('$moo:' + this.getPropName(className))){
            field.store('$moo:' + this.getPropName(className), false);
            this.options.hideError(advice);
            this.fireEvent('hideAdvice', [field, advice, className]);
        }
    },

    getPropName: function(className){
        return 'advice' + className;
    },

    resetField: function(field){
        field = document.id(field);
        if (!field) return this;
        this.parent(field);
        field.get('validators').each(function(className){
            this.hideAdvice(className, field);
        }, this);
        return this;
    },

    getAllAdviceMessages: function(field, force){
        var advice = [];
        if (field.hasClass('ignoreValidation') && !force) return advice;
        var validators = field.get('validators').some(function(cn){
            var warner = cn.test('^warn-') || field.hasClass('warnOnly');
            if (warner) cn = cn.replace(/^warn-/, '');
            var validator = this.getValidator(cn);
            if (!validator) return;
            advice.push({
                message: validator.getError(field),
                warnOnly: warner,
                passed: validator.test(),
                validator: validator
            });
        }, this);
        return advice;
    },

    getAdvice: function(className, field){
        return field.retrieve('$moo:advice-' + className);
    },

    insertAdvice: function(advice, field){
        //Check for error position prop
        var props = field.get('validatorProps');
        //Build advice
        if (!props.msgPos || !document.id(props.msgPos)){
            if (field.type && field.type.toLowerCase() == 'radio') field.getParent().adopt(advice);
            else advice.inject(document.id(field), 'after');
        } else {
            document.id(props.msgPos).grab(advice);
        }
    },

    validateField: function(field, force, scroll){
        var result = this.parent(field, force);
        if (((this.options.scrollToErrorsOnSubmit && scroll == null) || scroll) && !result){
            var failed = document.id(this).getElement('.validation-failed');
            var par = document.id(this).getParent();
            while (par != document.body && par.getScrollSize().y == par.getSize().y){
                par = par.getParent();
            }
            var fx = par.retrieve('$moo:fvScroller');
            if (!fx && window.Fx && Fx.Scroll){
                fx = new Fx.Scroll(par, this.options.scrollFxOptions);
                par.store('$moo:fvScroller', fx);
            }
            if (failed){
                if (fx) fx.toElement(failed);
                else par.scrollTo(par.getScroll().x, failed.getPosition(par).y - 20);
            }
        }
        return result;
    },

    watchFields: function(fields){
        fields.each(function(el){
            if (this.options.evaluateFieldsOnBlur){
                el.addEvent('blur', this.validationMonitor.pass([el, false, this.options.scrollToErrorsOnBlur], this));
            }
            if (this.options.evaluateFieldsOnChange){
                el.addEvent('change', this.validationMonitor.pass([el, true, this.options.scrollToErrorsOnChange], this));
            }
        }, this);
    }

});

/*
---

script: OverText.js

name: OverText

description: Shows text over an input that disappears when the user clicks into it. The text remains hidden if the user adds a value.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Core/Options
  - Core/Events
  - Core/Element.Event
  - Class.Binds
  - Class.Occlude
  - Element.Position
  - Element.Shortcuts

provides: [OverText]

...
*/
(function(){

    var OverText = this.OverText = new Class({

        Implements: [Options, Events, Class.Occlude],

        Binds: ['reposition', 'assert', 'focus', 'hide'],

        options: {/*
		textOverride: null,
		onFocus: function(){},
		onTextHide: function(textEl, inputEl){},
		onTextShow: function(textEl, inputEl){}, */
            element: 'label',
            labelClass: 'overTxtLabel',
            positionOptions: {
                position: 'upperLeft',
                edge: 'upperLeft',
                offset: {
                    x: 4,
                    y: 2
                }
            },
            poll: false,
            pollInterval: 250,
            wrap: false
        },

        property: 'OverText',

        initialize: function(element, options){
            element = this.element = document.id(element);

            if (this.occlude()) return this.occluded;
            this.setOptions(options);

            this.attach(element);
            OverText.instances.push(this);

            if (this.options.poll) this.poll();
        },

        toElement: function(){
            return this.element;
        },

        attach: function(){
            var element = this.element,
                options = this.options,
                value = options.textOverride || element.get('alt') || element.get('title');

            if (!value) return this;

            var text = this.text = new Element(options.element, {
                'class': options.labelClass,
                styles: {
                    lineHeight: 'normal',
                    position: 'absolute',
                    cursor: 'text'
                },
                html: value,
                events: {
                    click: this.hide.pass(options.element == 'label', this)
                }
            }).inject(element, 'after');

            if (options.element == 'label'){
                if (!element.get('id')) element.set('id', 'input_' + String.uniqueID());
                text.set('for', element.get('id'));
            }

            if (options.wrap){
                this.textHolder = new Element('div.overTxtWrapper', {
                    styles: {
                        lineHeight: 'normal',
                        position: 'relative'
                    }
                }).grab(text).inject(element, 'before');
            }

            return this.enable();
        },

        destroy: function(){
            this.element.eliminate(this.property); // Class.Occlude storage
            this.disable();
            if (this.text) this.text.destroy();
            if (this.textHolder) this.textHolder.destroy();
            return this;
        },

        disable: function(){
            this.element.removeEvents({
                focus: this.focus,
                blur: this.assert,
                change: this.assert
            });
            window.removeEvent('resize', this.reposition);
            this.hide(true, true);
            return this;
        },

        enable: function(){
            this.element.addEvents({
                focus: this.focus,
                blur: this.assert,
                change: this.assert
            });
            window.addEvent('resize', this.reposition);
            this.reposition();
            return this;
        },

        wrap: function(){
            if (this.options.element == 'label'){
                if (!this.element.get('id')) this.element.set('id', 'input_' + String.uniqueID());
                this.text.set('for', this.element.get('id'));
            }
        },

        startPolling: function(){
            this.pollingPaused = false;
            return this.poll();
        },

        poll: function(stop){
            //start immediately
            //pause on focus
            //resumeon blur
            if (this.poller && !stop) return this;
            if (stop){
                clearInterval(this.poller);
            } else {
                this.poller = (function(){
                    if (!this.pollingPaused) this.assert(true);
                }).periodical(this.options.pollInterval, this);
            }

            return this;
        },

        stopPolling: function(){
            this.pollingPaused = true;
            return this.poll(true);
        },

        focus: function(){
            if (this.text && (!this.text.isDisplayed() || this.element.get('disabled'))) return this;
            return this.hide();
        },

        hide: function(suppressFocus, force){
            if (this.text && (this.text.isDisplayed() && (!this.element.get('disabled') || force))){
                this.text.hide();
                this.fireEvent('textHide', [this.text, this.element]);
                this.pollingPaused = true;
                if (!suppressFocus){
                    try {
                        this.element.fireEvent('focus');
                        this.element.focus();
                    } catch (e){} //IE barfs if you call focus on hidden elements
                }
            }
            return this;
        },

        show: function(){
            if (document.id(this.text) && !this.text.isDisplayed()){
                this.text.show();
                this.reposition();
                this.fireEvent('textShow', [this.text, this.element]);
                this.pollingPaused = false;
            }
            return this;
        },

        test: function(){
            return !this.element.get('value');
        },

        assert: function(suppressFocus){
            return this[this.test() ? 'show' : 'hide'](suppressFocus);
        },

        reposition: function(){
            this.assert(true);
            if (!this.element.isVisible()) return this.stopPolling().hide();
            if (this.text && this.test()){
                this.text.position(Object.merge(this.options.positionOptions, {
                    relativeTo: this.element
                }));
            }
            return this;
        }

    });

})();

OverText.instances = [];

Object.append(OverText, {

    each: function(fn){
        return OverText.instances.each(function(ot, i){
            if (ot.element && ot.text) fn.call(OverText, ot, i);
        });
    },

    update: function(){

        return OverText.each(function(ot){
            return ot.reposition();
        });

    },

    hideAll: function(){

        return OverText.each(function(ot){
            return ot.hide(true, true);
        });

    },

    showAll: function(){
        return OverText.each(function(ot){
            return ot.show();
        });
    }

});


/*
---

script: Fx.Elements.js

name: Fx.Elements

description: Effect to change any number of CSS properties of any number of Elements.

license: MIT-style license

authors:
  - Valerio Proietti

requires:
  - Core/Fx.CSS
  - MooTools.More

provides: [Fx.Elements]

...
*/

Fx.Elements = new Class({

    Extends: Fx.CSS,

    initialize: function(elements, options){
        this.elements = this.subject = $$(elements);
        this.parent(options);
    },

    compute: function(from, to, delta){
        var now = {};

        for (var i in from){
            var iFrom = from[i], iTo = to[i], iNow = now[i] = {};
            for (var p in iFrom) iNow[p] = this.parent(iFrom[p], iTo[p], delta);
        }

        return now;
    },

    set: function(now){
        for (var i in now){
            if (!this.elements[i]) continue;

            var iNow = now[i];
            for (var p in iNow) this.render(this.elements[i], p, iNow[p], this.options.unit);
        }

        return this;
    },

    start: function(obj){
        if (!this.check(obj)) return this;
        var from = {}, to = {};

        for (var i in obj){
            if (!this.elements[i]) continue;

            var iProps = obj[i], iFrom = from[i] = {}, iTo = to[i] = {};

            for (var p in iProps){
                var parsed = this.prepare(this.elements[i], p, iProps[p]);
                iFrom[p] = parsed.from;
                iTo[p] = parsed.to;
            }
        }

        return this.parent(from, to);
    }

});

/*
---

script: Fx.Accordion.js

name: Fx.Accordion

description: An Fx.Elements extension which allows you to easily create accordion type controls.

license: MIT-style license

authors:
  - Valerio Proietti

requires:
  - Core/Element.Event
  - Fx.Elements

provides: [Fx.Accordion]

...
*/

Fx.Accordion = new Class({

    Extends: Fx.Elements,

    options: {/*
		onActive: function(toggler, section){},
		onBackground: function(toggler, section){},*/
        fixedHeight: false,
        fixedWidth: false,
        display: 0,
        show: false,
        height: true,
        width: false,
        opacity: true,
        alwaysHide: false,
        trigger: 'click',
        initialDisplayFx: true,
        resetHeight: true,
        keepOpen: false
    },

    initialize: function(){
        var defined = function(obj){
            return obj != null;
        };

        var params = Array.link(arguments, {
            'container': Type.isElement, //deprecated
            'options': Type.isObject,
            'togglers': defined,
            'elements': defined
        });
        this.parent(params.elements, params.options);

        var options = this.options,
            togglers = this.togglers = $$(params.togglers);

        this.previous = -1;
        this.internalChain = new Chain();

        if (options.alwaysHide) this.options.link = 'chain';

        if (options.show || this.options.show === 0){
            options.display = false;
            this.previous = options.show;
        }

        if (options.start){
            options.display = false;
            options.show = false;
        }

        var effects = this.effects = {};

        if (options.opacity) effects.opacity = 'fullOpacity';
        if (options.width) effects.width = options.fixedWidth ? 'fullWidth' : 'offsetWidth';
        if (options.height) effects.height = options.fixedHeight ? 'fullHeight' : 'scrollHeight';

        for (var i = 0, l = togglers.length; i < l; i++) this.addSection(togglers[i], this.elements[i]);

        this.elements.each(function(el, i){
            if (options.show === i){
                this.fireEvent('active', [togglers[i], el]);
            } else {
                for (var fx in effects) el.setStyle(fx, 0);
            }
        }, this);

        if (options.display || options.display === 0 || options.initialDisplayFx === false){
            this.display(options.display, options.initialDisplayFx);
        }

        if (options.fixedHeight !== false) options.resetHeight = false;
        this.addEvent('complete', this.internalChain.callChain.bind(this.internalChain));
    },

    addSection: function(toggler, element){
        toggler = document.id(toggler);
        element = document.id(element);
        this.togglers.include(toggler);
        this.elements.include(element);

        var togglers = this.togglers,
            options = this.options,
            test = togglers.contains(toggler),
            idx = togglers.indexOf(toggler),
            displayer = this.display.pass(idx, this);

        toggler.store('accordion:display', displayer)
            .addEvent(options.trigger, displayer);

        if (options.height) element.setStyles({'padding-top': 0, 'border-top': 'none', 'padding-bottom': 0, 'border-bottom': 'none'});
        if (options.width) element.setStyles({'padding-left': 0, 'border-left': 'none', 'padding-right': 0, 'border-right': 'none'});

        element.fullOpacity = 1;
        if (options.fixedWidth) element.fullWidth = options.fixedWidth;
        if (options.fixedHeight) element.fullHeight = options.fixedHeight;
        element.setStyle('overflow', 'hidden');

        if (!test) for (var fx in this.effects){
            element.setStyle(fx, 0);
        }
        return this;
    },

    removeSection: function(toggler, displayIndex){
        var togglers = this.togglers,
            idx = togglers.indexOf(toggler),
            element = this.elements[idx];

        var remover = function(){
            togglers.erase(toggler);
            this.elements.erase(element);
            this.detach(toggler);
        }.bind(this);

        if (this.now == idx || displayIndex != null){
            this.display(displayIndex != null ? displayIndex : (idx - 1 >= 0 ? idx - 1 : 0)).chain(remover);
        } else {
            remover();
        }
        return this;
    },

    detach: function(toggler){
        var remove = function(toggler){
            toggler.removeEvent(this.options.trigger, toggler.retrieve('accordion:display'));
        }.bind(this);

        if (!toggler) this.togglers.each(remove);
        else remove(toggler);
        return this;
    },

    display: function(index, useFx){
        if (!this.check(index, useFx)) return this;

        var obj = {},
            elements = this.elements,
            options = this.options,
            effects = this.effects,
            keepOpen = options.keepOpen,
            alwaysHide = options.alwaysHide;

        if (useFx == null) useFx = true;
        if (typeOf(index) == 'element') index = elements.indexOf(index);
        if (index == this.current && !alwaysHide && !keepOpen) return this;

        if (options.resetHeight){
            var prev = elements[this.current];
            if (prev && !this.selfHidden){
                for (var fx in effects) prev.setStyle(fx, prev[effects[fx]]);
            }
        }

        if (this.timer && options.link == 'chain') return this;

        if (this.current != null) this.previous = this.current;
        this.current = index;
        this.selfHidden = false;

        elements.each(function(el, i){
            obj[i] = {};
            var hide, isOpen;
            if (!keepOpen || i == index){
                if (i == index) isOpen = (el.offsetHeight > 0 && options.height) || (el.offsetWidth > 0 && options.width);

                if (i != index){
                    hide = true;
                } else if ((alwaysHide || keepOpen) && isOpen){
                    hide = true;
                    this.selfHidden = true;
                }
                this.fireEvent(hide ? 'background' : 'active', [this.togglers[i], el]);
                for (var fx in effects) obj[i][fx] = hide ? 0 : el[effects[fx]];
                if (!useFx && !hide && options.resetHeight) obj[i].height = 'auto';
            }
        }, this);

        this.internalChain.clearChain();
        this.internalChain.chain(function(){
            if (options.resetHeight && !this.selfHidden){
                var el = elements[index];
                if (el) el.setStyle('height', 'auto');
            }
        }.bind(this));

        return useFx ? this.start(obj) : this.set(obj).internalChain.callChain();
    }

});

/*<1.2compat>*/
/*
	Compatibility with 1.2.0
*/
var Accordion = new Class({

    Extends: Fx.Accordion,

    initialize: function(){
        this.parent.apply(this, arguments);
        var params = Array.link(arguments, {'container': Type.isElement});
        this.container = params.container;
    },

    addSection: function(toggler, element, pos){
        toggler = document.id(toggler);
        element = document.id(element);

        var test = this.togglers.contains(toggler);
        var len = this.togglers.length;
        if (len && (!test || pos)){
            pos = pos != null ? pos : len - 1;
            toggler.inject(this.togglers[pos], 'before');
            element.inject(toggler, 'after');
        } else if (this.container && !test){
            toggler.inject(this.container);
            element.inject(this.container);
        }
        return this.parent.apply(this, arguments);
    }

});
/*</1.2compat>*/

/*
---

script: Fx.Move.js

name: Fx.Move

description: Defines Fx.Move, a class that works with Element.Position.js to transition an element from one location to another.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Core/Fx.Morph
  - Element.Position

provides: [Fx.Move]

...
*/

Fx.Move = new Class({

    Extends: Fx.Morph,

    options: {
        relativeTo: document.body,
        position: 'center',
        edge: false,
        offset: {x: 0, y: 0}
    },

    start: function(destination){
        var element = this.element,
            topLeft = element.getStyles('top', 'left');
        if (topLeft.top == 'auto' || topLeft.left == 'auto'){
            element.setPosition(element.getPosition(element.getOffsetParent()));
        }
        return this.parent(element.position(Object.merge({}, this.options, destination, {returnPos: true})));
    }

});

Element.Properties.move = {

    set: function(options){
        this.get('move').cancel().setOptions(options);
        return this;
    },

    get: function(){
        var move = this.retrieve('move');
        if (!move){
            move = new Fx.Move(this, {link: 'cancel'});
            this.store('move', move);
        }
        return move;
    }

};

Element.implement({

    move: function(options){
        this.get('move').start(options);
        return this;
    }

});

/*
---

script: Fx.Scroll.js

name: Fx.Scroll

description: Effect to smoothly scroll any element, including the window.

license: MIT-style license

authors:
  - Valerio Proietti

requires:
  - Core/Fx
  - Core/Element.Event
  - Core/Element.Dimensions
  - MooTools.More

provides: [Fx.Scroll]

...
*/

(function(){

    Fx.Scroll = new Class({

        Extends: Fx,

        options: {
            offset: {x: 0, y: 0},
            wheelStops: true
        },

        initialize: function(element, options){
            this.element = this.subject = document.id(element);
            this.parent(options);

            if (typeOf(this.element) != 'element') this.element = document.id(this.element.getDocument().body);

            if (this.options.wheelStops){
                var stopper = this.element,
                    cancel = this.cancel.pass(false, this);
                this.addEvent('start', function(){
                    stopper.addEvent('mousewheel', cancel);
                }, true);
                this.addEvent('complete', function(){
                    stopper.removeEvent('mousewheel', cancel);
                }, true);
            }
        },

        set: function(){
            var now = Array.flatten(arguments);
            this.element.scrollTo(now[0], now[1]);
            return this;
        },

        compute: function(from, to, delta){
            return [0, 1].map(function(i){
                return Fx.compute(from[i], to[i], delta);
            });
        },

        start: function(x, y){
            if (!this.check(x, y)) return this;
            var scroll = this.element.getScroll();
            return this.parent([scroll.x, scroll.y], [x, y]);
        },

        calculateScroll: function(x, y){
            var element = this.element,
                scrollSize = element.getScrollSize(),
                scroll = element.getScroll(),
                size = element.getSize(),
                offset = this.options.offset,
                values = {x: x, y: y};

            for (var z in values){
                if (!values[z] && values[z] !== 0) values[z] = scroll[z];
                if (typeOf(values[z]) != 'number') values[z] = scrollSize[z] - size[z];
                values[z] += offset[z];
            }

            return [values.x, values.y];
        },

        toTop: function(){
            return this.start.apply(this, this.calculateScroll(false, 0));
        },

        toLeft: function(){
            return this.start.apply(this, this.calculateScroll(0, false));
        },

        toRight: function(){
            return this.start.apply(this, this.calculateScroll('right', false));
        },

        toBottom: function(){
            return this.start.apply(this, this.calculateScroll(false, 'bottom'));
        },

        toElement: function(el, axes){
            axes = axes ? Array.convert(axes) : ['x', 'y'];
            var scroll = isBody(this.element) ? {x: 0, y: 0} : this.element.getScroll();
            var position = Object.map(document.id(el).getPosition(this.element), function(value, axis){
                return axes.contains(axis) ? value + scroll[axis] : false;
            });
            return this.start.apply(this, this.calculateScroll(position.x, position.y));
        },

        toElementEdge: function(el, axes, offset){
            axes = axes ? Array.convert(axes) : ['x', 'y'];
            el = document.id(el);
            var to = {},
                position = el.getPosition(this.element),
                size = el.getSize(),
                scroll = this.element.getScroll(),
                containerSize = this.element.getSize(),
                edge = {
                    x: position.x + size.x,
                    y: position.y + size.y
                };

            ['x', 'y'].each(function(axis){
                if (axes.contains(axis)){
                    if (edge[axis] > scroll[axis] + containerSize[axis]) to[axis] = edge[axis] - containerSize[axis];
                    if (position[axis] < scroll[axis]) to[axis] = position[axis];
                }
                if (to[axis] == null) to[axis] = scroll[axis];
                if (offset && offset[axis]) to[axis] = to[axis] + offset[axis];
            }, this);

            if (to.x != scroll.x || to.y != scroll.y) this.start(to.x, to.y);
            return this;
        },

        toElementCenter: function(el, axes, offset){
            axes = axes ? Array.convert(axes) : ['x', 'y'];
            el = document.id(el);
            var to = {},
                position = el.getPosition(this.element),
                size = el.getSize(),
                scroll = this.element.getScroll(),
                containerSize = this.element.getSize();

            ['x', 'y'].each(function(axis){
                if (axes.contains(axis)){
                    to[axis] = position[axis] - (containerSize[axis] - size[axis]) / 2;
                }
                if (to[axis] == null) to[axis] = scroll[axis];
                if (offset && offset[axis]) to[axis] = to[axis] + offset[axis];
            }, this);

            if (to.x != scroll.x || to.y != scroll.y) this.start(to.x, to.y);
            return this;
        }

    });

//<1.2compat>
    Fx.Scroll.implement({
        scrollToCenter: function(){
            return this.toElementCenter.apply(this, arguments);
        },
        scrollIntoView: function(){
            return this.toElementEdge.apply(this, arguments);
        }
    });
//</1.2compat>

    function isBody(element){
        return (/^(?:body|html)$/i).test(element.tagName);
    }

})();

/*
---

script: Fx.Slide.js

name: Fx.Slide

description: Effect to slide an element in and out of view.

license: MIT-style license

authors:
  - Valerio Proietti

requires:
  - Core/Fx
  - Core/Element.Style
  - MooTools.More

provides: [Fx.Slide]

...
*/

Fx.Slide = new Class({

    Extends: Fx,

    options: {
        mode: 'vertical',
        wrapper: false,
        hideOverflow: true,
        resetHeight: false
    },

    initialize: function(element, options){
        element = this.element = this.subject = document.id(element);
        this.parent(options);
        options = this.options;

        var wrapper = element.retrieve('wrapper'),
            styles = element.getStyles('margin', 'position', 'overflow');

        if (options.hideOverflow) styles = Object.append(styles, {overflow: 'hidden'});
        if (options.wrapper) wrapper = document.id(options.wrapper).setStyles(styles);

        if (!wrapper) wrapper = new Element('div', {
            styles: styles
        }).wraps(element);

        element.store('wrapper', wrapper).setStyle('margin', 0);
        if (element.getStyle('overflow') == 'visible') element.setStyle('overflow', 'hidden');

        this.now = [];
        this.open = true;
        this.wrapper = wrapper;

        this.addEvent('complete', function(){
            this.open = (wrapper['offset' + this.layout.capitalize()] != 0);
            if (this.open && this.options.resetHeight) wrapper.setStyle('height', '');
        }, true);
    },

    vertical: function(){
        this.margin = 'margin-top';
        this.layout = 'height';
        this.offset = this.element.offsetHeight;
    },

    horizontal: function(){
        this.margin = 'margin-left';
        this.layout = 'width';
        this.offset = this.element.offsetWidth;
    },

    set: function(now){
        this.element.setStyle(this.margin, now[0]);
        this.wrapper.setStyle(this.layout, now[1]);
        return this;
    },

    compute: function(from, to, delta){
        return [0, 1].map(function(i){
            return Fx.compute(from[i], to[i], delta);
        });
    },

    start: function(how, mode){
        if (!this.check(how, mode)) return this;
        this[mode || this.options.mode]();

        var margin = this.element.getStyle(this.margin).toInt(),
            layout = this.wrapper.getStyle(this.layout).toInt(),
            caseIn = [[margin, layout], [0, this.offset]],
            caseOut = [[margin, layout], [-this.offset, 0]],
            start;

        switch (how){
            case 'in': start = caseIn; break;
            case 'out': start = caseOut; break;
            case 'toggle': start = (layout == 0) ? caseIn : caseOut;
        }
        return this.parent(start[0], start[1]);
    },

    slideIn: function(mode){
        return this.start('in', mode);
    },

    slideOut: function(mode){
        return this.start('out', mode);
    },

    hide: function(mode){
        this[mode || this.options.mode]();
        this.open = false;
        return this.set([-this.offset, 0]);
    },

    show: function(mode){
        this[mode || this.options.mode]();
        this.open = true;
        return this.set([0, this.offset]);
    },

    toggle: function(mode){
        return this.start('toggle', mode);
    }

});

Element.Properties.slide = {

    set: function(options){
        this.get('slide').cancel().setOptions(options);
        return this;
    },

    get: function(){
        var slide = this.retrieve('slide');
        if (!slide){
            slide = new Fx.Slide(this, {link: 'cancel'});
            this.store('slide', slide);
        }
        return slide;
    }

};

Element.implement({

    slide: function(how, mode){
        how = how || 'toggle';
        var slide = this.get('slide'), toggle;
        switch (how){
            case 'hide': slide.hide(mode); break;
            case 'show': slide.show(mode); break;
            case 'toggle':
                var flag = this.retrieve('slide:flag', slide.open);
                slide[flag ? 'slideOut' : 'slideIn'](mode);
                this.store('slide:flag', !flag);
                toggle = true;
                break;
            default: slide.start(how, mode);
        }
        if (!toggle) this.eliminate('slide:flag');
        return this;
    }

});

/*
---

script: Fx.SmoothScroll.js

name: Fx.SmoothScroll

description: Class for creating a smooth scrolling effect to all internal links on the page.

license: MIT-style license

authors:
  - Valerio Proietti

requires:
  - Core/Slick.Finder
  - Fx.Scroll

provides: [Fx.SmoothScroll]

...
*/

/*<1.2compat>*/var SmoothScroll = /*</1.2compat>*/Fx.SmoothScroll = new Class({

    Extends: Fx.Scroll,

    options: {
        axes: ['x', 'y']
    },

    initialize: function(options, context){
        context = context || document;
        this.doc = context.getDocument();
        this.parent(this.doc, options);

        var win = context.getWindow(),
            location = win.location.href.match(/^[^#]*/)[0] + '#',
            links = $$(this.options.links || this.doc.links);

        links.each(function(link){
            if (link.href.indexOf(location) != 0) return;
            var anchor = link.href.substr(location.length);
            if (anchor) this.useLink(link, anchor);
        }, this);

        this.addEvent('complete', function(){
            win.location.hash = this.anchor;
            this.element.scrollTo(this.to[0], this.to[1]);
        }, true);
    },

    useLink: function(link, anchor){

        link.addEvent('click', function(event){
            var el = document.id(anchor) || this.doc.getElement('a[name=' + anchor + ']');
            if (!el) return;

            event.preventDefault();
            this.toElement(el, this.options.axes).chain(function(){
                this.fireEvent('scrolledTo', [link, el]);
            }.bind(this));

            this.anchor = anchor;

        }.bind(this));

        return this;
    }
});

/*
---

script: Fx.Sort.js

name: Fx.Sort

description: Defines Fx.Sort, a class that reorders lists with a transition.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Core/Element.Dimensions
  - Fx.Elements
  - Element.Measure

provides: [Fx.Sort]

...
*/

Fx.Sort = new Class({

    Extends: Fx.Elements,

    options: {
        mode: 'vertical'
    },

    initialize: function(elements, options){
        this.parent(elements, options);
        this.elements.each(function(el){
            if (el.getStyle('position') == 'static') el.setStyle('position', 'relative');
        });
        this.setDefaultOrder();
    },

    setDefaultOrder: function(){
        this.currentOrder = this.elements.map(function(el, index){
            return index;
        });
    },

    sort: function(){
        if (!this.check(arguments)) return this;
        var newOrder = Array.flatten(arguments);

        var top = 0,
            left = 0,
            next = {},
            zero = {},
            vert = this.options.mode == 'vertical';

        var current = this.elements.map(function(el, index){
            var size = el.getComputedSize({styles: ['border', 'padding', 'margin']});
            var val;
            if (vert){
                val = {
                    top: top,
                    margin: size['margin-top'],
                    height: size.totalHeight
                };
                top += val.height - size['margin-top'];
            } else {
                val = {
                    left: left,
                    margin: size['margin-left'],
                    width: size.totalWidth
                };
                left += val.width;
            }
            var plane = vert ? 'top' : 'left';
            zero[index] = {};
            var start = el.getStyle(plane).toInt();
            zero[index][plane] = start || 0;
            return val;
        }, this);

        this.set(zero);
        newOrder = newOrder.map(function(i){ return i.toInt(); });
        if (newOrder.length != this.elements.length){
            this.currentOrder.each(function(index){
                if (!newOrder.contains(index)) newOrder.push(index);
            });
            if (newOrder.length > this.elements.length)
                newOrder.splice(this.elements.length-1, newOrder.length - this.elements.length);
        }
        var margin = 0;
        top = left = 0;
        newOrder.each(function(item){
            var newPos = {};
            if (vert){
                newPos.top = top - current[item].top - margin;
                top += current[item].height;
            } else {
                newPos.left = left - current[item].left;
                left += current[item].width;
            }
            margin = margin + current[item].margin;
            next[item]=newPos;
        }, this);
        var mapped = {};
        Array.clone(newOrder).sort().each(function(index){
            mapped[index] = next[index];
        });
        this.start(mapped);
        this.currentOrder = newOrder;

        return this;
    },

    rearrangeDOM: function(newOrder){
        newOrder = newOrder || this.currentOrder;
        var parent = this.elements[0].getParent();
        var rearranged = [];
        this.elements.setStyle('opacity', 0);
        //move each element and store the new default order
        newOrder.each(function(index){
            rearranged.push(this.elements[index].inject(parent).setStyles({
                top: 0,
                left: 0
            }));
        }, this);
        this.elements.setStyle('opacity', 1);
        this.elements = $$(rearranged);
        this.setDefaultOrder();
        return this;
    },

    getDefaultOrder: function(){
        return this.elements.map(function(el, index){
            return index;
        });
    },

    getCurrentOrder: function(){
        return this.currentOrder;
    },

    forward: function(){
        return this.sort(this.getDefaultOrder());
    },

    backward: function(){
        return this.sort(this.getDefaultOrder().reverse());
    },

    reverse: function(){
        return this.sort(this.currentOrder.reverse());
    },

    sortByElements: function(elements){
        return this.sort(elements.map(function(el){
            return this.elements.indexOf(el);
        }, this));
    },

    swap: function(one, two){
        if (typeOf(one) == 'element') one = this.elements.indexOf(one);
        if (typeOf(two) == 'element') two = this.elements.indexOf(two);

        var newOrder = Array.clone(this.currentOrder);
        newOrder[this.currentOrder.indexOf(one)] = two;
        newOrder[this.currentOrder.indexOf(two)] = one;

        return this.sort(newOrder);
    }

});

/*
---

script: Keyboard.js

name: Keyboard

description: KeyboardEvents used to intercept events on a class for keyboard and format modifiers in a specific order so as to make alt+shift+c the same as shift+alt+c.

license: MIT-style license

authors:
  - Perrin Westrich
  - Aaron Newton
  - Scott Kyle

requires:
  - Core/Events
  - Core/Options
  - Core/Element.Event
  - Element.Event.Pseudos.Keys

provides: [Keyboard]

...
*/

(function(){

    var Keyboard = this.Keyboard = new Class({

        Extends: Events,

        Implements: [Options],

        options: {/*
		onActivate: function(){},
		onDeactivate: function(){},*/
            defaultEventType: 'keydown',
            active: false,
            manager: null,
            events: {},
            nonParsedEvents: ['activate', 'deactivate', 'onactivate', 'ondeactivate', 'changed', 'onchanged']
        },

        initialize: function(options){
            if (options && options.manager){
                this._manager = options.manager;
                delete options.manager;
            }
            this.setOptions(options);
            this._setup();
        },

        addEvent: function(type, fn, internal){
            return this.parent(Keyboard.parse(type, this.options.defaultEventType, this.options.nonParsedEvents), fn, internal);
        },

        removeEvent: function(type, fn){
            return this.parent(Keyboard.parse(type, this.options.defaultEventType, this.options.nonParsedEvents), fn);
        },

        toggleActive: function(){
            return this[this.isActive() ? 'deactivate' : 'activate']();
        },

        activate: function(instance){
            if (instance){
                if (instance.isActive()) return this;
                //if we're stealing focus, store the last keyboard to have it so the relinquish command works
                if (this._activeKB && instance != this._activeKB){
                    this.previous = this._activeKB;
                    this.previous.fireEvent('deactivate');
                }
                //if we're enabling a child, assign it so that events are now passed to it
                this._activeKB = instance.fireEvent('activate');
                Keyboard.manager.fireEvent('changed');
            } else if (this._manager){
                //else we're enabling ourselves, we must ask our parent to do it for us
                this._manager.activate(this);
            }
            return this;
        },

        isActive: function(){
            return this._manager ? (this._manager._activeKB == this) : (Keyboard.manager == this);
        },

        deactivate: function(instance){
            if (instance){
                if (instance === this._activeKB){
                    this._activeKB = null;
                    instance.fireEvent('deactivate');
                    Keyboard.manager.fireEvent('changed');
                }
            } else if (this._manager){
                this._manager.deactivate(this);
            }
            return this;
        },

        relinquish: function(){
            if (this.isActive() && this._manager && this._manager.previous) this._manager.activate(this._manager.previous);
            else this.deactivate();
            return this;
        },

        //management logic
        manage: function(instance){
            if (instance._manager) instance._manager.drop(instance);
            this._instances.push(instance);
            instance._manager = this;
            if (!this._activeKB) this.activate(instance);
            return this;
        },

        drop: function(instance){
            instance.relinquish();
            this._instances.erase(instance);
            if (this._activeKB == instance){
                if (this.previous && this._instances.contains(this.previous)) this.activate(this.previous);
                else this._activeKB = this._instances[0];
            }
            return this;
        },

        trace: function(){
            Keyboard.trace(this);
        },

        each: function(fn){
            Keyboard.each(this, fn);
        },

        /*
		PRIVATE METHODS
	*/

        _instances: [],

        _disable: function(instance){
            if (this._activeKB == instance) this._activeKB = null;
        },

        _setup: function(){
            this.addEvents(this.options.events);
            //if this is the root manager, nothing manages it
            if (Keyboard.manager && !this._manager) Keyboard.manager.manage(this);
            if (this.options.active) this.activate();
            else this.relinquish();
        },

        _handle: function(event, type){
            //Keyboard.stop(event) prevents key propagation
            if (event.preventKeyboardPropagation) return;

            var bubbles = !!this._manager;
            if (bubbles && this._activeKB){
                this._activeKB._handle(event, type);
                if (event.preventKeyboardPropagation) return;
            }
            this.fireEvent(type, event);

            if (!bubbles && this._activeKB) this._activeKB._handle(event, type);
        }

    });

    var parsed = {};
    var modifiers = ['shift', 'control', 'alt', 'meta'];
    var regex = /^(?:shift|control|ctrl|alt|meta)$/;

    Keyboard.parse = function(type, eventType, ignore){
        if (ignore && ignore.contains(type.toLowerCase())) return type;

        type = type.toLowerCase().replace(/^(keyup|keydown):/, function($0, $1){
            eventType = $1;
            return '';
        });

        if (!parsed[type]){
            if (type != '+'){
                var key, mods = {};
                type.split('+').each(function(part){
                    if (regex.test(part)) mods[part] = true;
                    else key = part;
                });

                mods.control = mods.control || mods.ctrl; // allow both control and ctrl

                var keys = [];
                modifiers.each(function(mod){
                    if (mods[mod]) keys.push(mod);
                });

                if (key) keys.push(key);
                parsed[type] = keys.join('+');
            } else {
                parsed[type] = type;
            }
        }

        return eventType + ':keys(' + parsed[type] + ')';
    };

    Keyboard.each = function(keyboard, fn){
        var current = keyboard || Keyboard.manager;
        while (current){
            fn(current);
            current = current._activeKB;
        }
    };

    Keyboard.stop = function(event){
        event.preventKeyboardPropagation = true;
    };

    Keyboard.manager = new Keyboard({
        active: true
    });

    Keyboard.trace = function(keyboard){
        keyboard = keyboard || Keyboard.manager;
        var hasConsole = window.console && console.log;
        if (hasConsole) console.log('the following items have focus: ');
        Keyboard.each(keyboard, function(current){
            if (hasConsole) console.log(document.id(current.widget) || current.wiget || current);
        });
    };

    var handler = function(event){
        var keys = [];
        modifiers.each(function(mod){
            if (event[mod]) keys.push(mod);
        });

        if (!regex.test(event.key)) keys.push(event.key);
        Keyboard.manager._handle(event, event.type + ':keys(' + keys.join('+') + ')');
    };

    document.addEvents({
        'keyup': handler,
        'keydown': handler
    });

})();

/*
---

script: Keyboard.Extras.js

name: Keyboard.Extras

description: Enhances Keyboard by adding the ability to name and describe keyboard shortcuts, and the ability to grab shortcuts by name and bind the shortcut to different keys.

license: MIT-style license

authors:
  - Perrin Westrich

requires:
  - Keyboard
  - MooTools.More

provides: [Keyboard.Extras]

...
*/
Keyboard.prototype.options.nonParsedEvents.combine(['rebound', 'onrebound']);

Keyboard.implement({

    /*
		shortcut should be in the format of:
		{
			'keys': 'shift+s', // the default to add as an event.
			'description': 'blah blah blah', // a brief description of the functionality.
			'handler': function(){} // the event handler to run when keys are pressed.
		}
	*/
    addShortcut: function(name, shortcut){
        this._shortcuts = this._shortcuts || [];
        this._shortcutIndex = this._shortcutIndex || {};

        shortcut.getKeyboard = Function.convert(this);
        shortcut.name = name;
        this._shortcutIndex[name] = shortcut;
        this._shortcuts.push(shortcut);
        if (shortcut.keys) this.addEvent(shortcut.keys, shortcut.handler);
        return this;
    },

    addShortcuts: function(obj){
        for (var name in obj) this.addShortcut(name, obj[name]);
        return this;
    },

    removeShortcut: function(name){
        var shortcut = this.getShortcut(name);
        if (shortcut && shortcut.keys){
            this.removeEvent(shortcut.keys, shortcut.handler);
            delete this._shortcutIndex[name];
            this._shortcuts.erase(shortcut);
        }
        return this;
    },

    removeShortcuts: function(names){
        names.each(this.removeShortcut, this);
        return this;
    },

    getShortcuts: function(){
        return this._shortcuts || [];
    },

    getShortcut: function(name){
        return (this._shortcutIndex || {})[name];
    }

});

Keyboard.rebind = function(newKeys, shortcuts){
    Array.convert(shortcuts).each(function(shortcut){
        shortcut.getKeyboard().removeEvent(shortcut.keys, shortcut.handler);
        shortcut.getKeyboard().addEvent(newKeys, shortcut.handler);
        shortcut.keys = newKeys;
        shortcut.getKeyboard().fireEvent('rebound');
    });
};


Keyboard.getActiveShortcuts = function(keyboard){
    var activeKBS = [], activeSCS = [];
    Keyboard.each(keyboard, [].push.bind(activeKBS));
    activeKBS.each(function(kb){ activeSCS.extend(kb.getShortcuts()); });
    return activeSCS;
};

Keyboard.getShortcut = function(name, keyboard, opts){
    opts = opts || {};
    var shortcuts = opts.many ? [] : null,
        set = opts.many ? function(kb){
            var shortcut = kb.getShortcut(name);
            if (shortcut) shortcuts.push(shortcut);
        } : function(kb){
            if (!shortcuts) shortcuts = kb.getShortcut(name);
        };
    Keyboard.each(keyboard, set);
    return shortcuts;
};

Keyboard.getShortcuts = function(name, keyboard){
    return Keyboard.getShortcut(name, keyboard, { many: true });
};

/*
---

script: HtmlTable.js

name: HtmlTable

description: Builds table elements with methods to add rows.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Core/Options
  - Core/Events
  - Class.Occlude

provides: [HtmlTable]

...
*/
(function(){

    var HtmlTable = this.HtmlTable = new Class({

        Implements: [Options, Events, Class.Occlude],

        options: {
            properties: {
                cellpadding: 0,
                cellspacing: 0,
                border: 0
            },
            rows: [],
            headers: [],
            footers: []
        },

        property: 'HtmlTable',

        initialize: function(){
            var params = Array.link(arguments, {options: Type.isObject, table: Type.isElement, id: Type.isString});
            this.setOptions(params.options);
            if (!params.table && params.id) params.table = document.id(params.id);
            this.element = params.table || new Element('table', this.options.properties);
            if (this.occlude()) return this.occluded;
            this.build();
        },

        build: function(){
            this.element.store('HtmlTable', this);

            this.body = document.id(this.element.tBodies[0]) || new Element('tbody').inject(this.element);
            $$(this.body.rows);

            if (this.options.headers.length) this.setHeaders(this.options.headers);
            else this.thead = document.id(this.element.tHead);

            if (this.thead) this.head = this.getHead();
            if (this.options.footers.length) this.setFooters(this.options.footers);

            this.tfoot = document.id(this.element.tFoot);
            if (this.tfoot) this.foot = document.id(this.tfoot.rows[0]);

            this.options.rows.each(function(row){
                this.push(row);
            }, this);
        },

        toElement: function(){
            return this.element;
        },

        empty: function(){
            this.body.empty();
            return this;
        },

        set: function(what, items){
            var target = (what == 'headers') ? 'tHead' : 'tFoot',
                lower = target.toLowerCase();

            this[lower] = (document.id(this.element[target]) || new Element(lower).inject(this.element, 'top')).empty();
            var data = this.push(items, {}, this[lower], what == 'headers' ? 'th' : 'td');

            if (what == 'headers') this.head = this.getHead();
            else this.foot = this.getHead();

            return data;
        },

        getHead: function(){
            var rows = this.thead.rows;
            return rows.length > 1 ? $$(rows) : rows.length ? document.id(rows[0]) : false;
        },

        setHeaders: function(headers){
            this.set('headers', headers);
            return this;
        },

        setFooters: function(footers){
            this.set('footers', footers);
            return this;
        },

        update: function(tr, row, tag){
            var tds = tr.getChildren(tag || 'td'), last = tds.length - 1;

            row.each(function(data, index){
                var td = tds[index] || new Element(tag || 'td').inject(tr),
                    content = ((data && Object.prototype.hasOwnProperty.call(data, 'content')) ? data.content : '') || data,
                    type = typeOf(content);

                if (data && Object.prototype.hasOwnProperty.call(data, 'properties')) td.set(data.properties);
                if (/(element(s?)|array|collection)/.test(type)) td.empty().adopt(content);
                else td.set('html', content);

                if (index > last) tds.push(td);
                else tds[index] = td;
            });

            return {
                tr: tr,
                tds: tds
            };
        },

        push: function(row, rowProperties, target, tag, where){
            if (typeOf(row) == 'element' && row.get('tag') == 'tr'){
                row.inject(target || this.body, where);
                return {
                    tr: row,
                    tds: row.getChildren('td')
                };
            }
            return this.update(new Element('tr', rowProperties).inject(target || this.body, where), row, tag);
        },

        pushMany: function(rows, rowProperties, target, tag, where){
            return rows.map(function(row){
                return this.push(row, rowProperties, target, tag, where);
            }, this);
        }

    });

})();


['adopt', 'inject', 'wraps', 'grab', 'replaces', 'dispose'].each(function(method){
    HtmlTable.implement(method, function(){
        this.element[method].apply(this.element, arguments);
        return this;
    });
});



/*
---

script: HtmlTable.Select.js

name: HtmlTable.Select

description: Builds a stripy, sortable table with methods to add rows. Rows can be selected with the mouse or keyboard navigation.

license: MIT-style license

authors:
  - Harald Kirschner
  - Aaron Newton

requires:
  - Keyboard
  - Keyboard.Extras
  - HtmlTable
  - Class.refactor
  - Element.Delegation.Pseudo
  - Element.Shortcuts

provides: [HtmlTable.Select]

...
*/

HtmlTable = Class.refactor(HtmlTable, {

    options: {
        /*onRowFocus: function(){},
		onRowUnfocus: function(){},*/
        useKeyboard: true,
        classRowSelected: 'table-tr-selected',
        classRowHovered: 'table-tr-hovered',
        classSelectable: 'table-selectable',
        shiftForMultiSelect: true,
        allowMultiSelect: true,
        selectable: false,
        selectHiddenRows: false
    },

    initialize: function(){
        this.previous.apply(this, arguments);
        if (this.occluded) return this.occluded;

        this.selectedRows = new Elements();

        if (!this.bound) this.bound = {};
        this.bound.mouseleave = this.mouseleave.bind(this);
        this.bound.clickRow = this.clickRow.bind(this);
        this.bound.activateKeyboard = function(){
            if (this.keyboard && this.selectEnabled) this.keyboard.activate();
        }.bind(this);

        if (this.options.selectable) this.enableSelect();
    },

    empty: function(){
        if (this.body.rows.length) this.selectNone();
        return this.previous();
    },

    enableSelect: function(){
        this.selectEnabled = true;
        this.attachSelects();
        this.element.addClass(this.options.classSelectable);
        return this;
    },

    disableSelect: function(){
        this.selectEnabled = false;
        this.attachSelects(false);
        this.element.removeClass(this.options.classSelectable);
        return this;
    },

    push: function(){
        var ret = this.previous.apply(this, arguments);
        this.updateSelects();
        return ret;
    },

    toggleRow: function(row){
        return this[(this.isSelected(row) ? 'de' : '') + 'selectRow'](row);
    },

    selectRow: function(row, _nocheck){
        //private variable _nocheck: boolean whether or not to confirm the row is in the table body
        //added here for optimization when selecting ranges
        if (this.isSelected(row) || (!_nocheck && !this.body.getChildren().contains(row))) return;
        if (!this.options.allowMultiSelect) this.selectNone();

        if (!this.isSelected(row)){
            this.selectedRows.push(row);
            row.addClass(this.options.classRowSelected);
            this.fireEvent('rowFocus', [row, this.selectedRows]);
            this.fireEvent('stateChanged');
        }

        this.focused = row;
        document.clearSelection();

        return this;
    },

    isSelected: function(row){
        return this.selectedRows.contains(row);
    },

    getSelected: function(){
        return this.selectedRows;
    },

    serialize: function(){
        var previousSerialization = this.previous.apply(this, arguments) || {};
        if (this.options.selectable){
            previousSerialization.selectedRows = this.selectedRows.map(function(row){
                return Array.indexOf(this.body.rows, row);
            }.bind(this));
        }
        return previousSerialization;
    },

    restore: function(tableState){
        if (this.options.selectable && tableState.selectedRows){
            tableState.selectedRows.each(function(index){
                this.selectRow(this.body.rows[index]);
            }.bind(this));
        }
        this.previous.apply(this, arguments);
    },

    deselectRow: function(row, _nocheck){
        if (!this.isSelected(row) || (!_nocheck && !this.body.getChildren().contains(row))) return;

        this.selectedRows = new Elements(Array.convert(this.selectedRows).erase(row));
        row.removeClass(this.options.classRowSelected);
        this.fireEvent('rowUnfocus', [row, this.selectedRows]);
        this.fireEvent('stateChanged');
        return this;
    },

    selectAll: function(selectNone){
        if (!selectNone && !this.options.allowMultiSelect) return;
        this.selectRange(0, this.body.rows.length, selectNone);
        return this;
    },

    selectNone: function(){
        return this.selectAll(true);
    },

    selectRange: function(startRow, endRow, _deselect){
        if (!this.options.allowMultiSelect && !_deselect) return;
        var method = _deselect ? 'deselectRow' : 'selectRow',
            rows = Array.clone(this.body.rows);

        if (typeOf(startRow) == 'element') startRow = rows.indexOf(startRow);
        if (typeOf(endRow) == 'element') endRow = rows.indexOf(endRow);
        endRow = endRow < rows.length - 1 ? endRow : rows.length - 1;

        if (endRow < startRow){
            var tmp = startRow;
            startRow = endRow;
            endRow = tmp;
        }

        for (var i = startRow; i <= endRow; i++){
            if (this.options.selectHiddenRows || rows[i].isDisplayed()) this[method](rows[i], true);
        }

        return this;
    },

    deselectRange: function(startRow, endRow){
        this.selectRange(startRow, endRow, true);
    },

    /*
	Private methods:
*/

    enterRow: function(row){
        if (this.hovered) this.hovered = this.leaveRow(this.hovered);
        this.hovered = row.addClass(this.options.classRowHovered);
    },

    leaveRow: function(row){
        row.removeClass(this.options.classRowHovered);
    },

    updateSelects: function(){
        Array.each(this.body.rows, function(row){
            var binders = row.retrieve('binders');
            if (!binders && !this.selectEnabled) return;
            if (!binders){
                binders = {
                    mouseenter: this.enterRow.pass([row], this),
                    mouseleave: this.leaveRow.pass([row], this)
                };
                row.store('binders', binders);
            }
            if (this.selectEnabled) row.addEvents(binders);
            else row.removeEvents(binders);
        }, this);
    },

    shiftFocus: function(offset, event){
        if (!this.focused) return this.selectRow(this.body.rows[0], event);
        var to = this.getRowByOffset(offset, this.options.selectHiddenRows);
        if (to === null || this.focused == this.body.rows[to]) return this;
        this.toggleRow(this.body.rows[to], event);
    },

    clickRow: function(event, row){
        var selecting = (event.shift || event.meta || event.control) && this.options.shiftForMultiSelect;
        if (!selecting && !(event.rightClick && this.isSelected(row) && this.options.allowMultiSelect)) this.selectNone();

        if (event.rightClick) this.selectRow(row);
        else this.toggleRow(row);

        if (event.shift){
            this.selectRange(this.rangeStart || this.body.rows[0], row, this.rangeStart ? !this.isSelected(row) : true);
            this.focused = row;
        }
        this.rangeStart = row;
    },

    getRowByOffset: function(offset, includeHiddenRows){
        if (!this.focused) return 0;
        var index = Array.indexOf(this.body.rows, this.focused);
        if ((index == 0 && offset < 0) || (index == this.body.rows.length -1 && offset > 0)) return null;
        if (includeHiddenRows){
            index += offset;
        } else {
            var limit = 0,
                count = 0;
            if (offset > 0){
                while (count < offset && index < this.body.rows.length -1){
                    if (this.body.rows[++index].isDisplayed()) count++;
                }
            } else {
                while (count > offset && index > 0){
                    if (this.body.rows[--index].isDisplayed()) count--;
                }
            }
        }
        return index;
    },

    attachSelects: function(attach){
        attach = attach != null ? attach : true;

        var method = attach ? 'addEvents' : 'removeEvents';
        this.element[method]({
            mouseleave: this.bound.mouseleave,
            click: this.bound.activateKeyboard
        });

        this.body[method]({
            'click:relay(tr)': this.bound.clickRow,
            'contextmenu:relay(tr)': this.bound.clickRow
        });

        if (this.options.useKeyboard || this.keyboard){
            if (!this.keyboard) this.keyboard = new Keyboard();
            if (!this.selectKeysDefined){
                this.selectKeysDefined = true;
                var timer, held;

                var move = function(offset){
                    var mover = function(e){
                        clearTimeout(timer);
                        e.preventDefault();
                        var to = this.body.rows[this.getRowByOffset(offset, this.options.selectHiddenRows)];
                        if (e.shift && to && this.isSelected(to)){
                            this.deselectRow(this.focused);
                            this.focused = to;
                        } else {
                            if (to && (!this.options.allowMultiSelect || !e.shift)){
                                this.selectNone();
                            }
                            this.shiftFocus(offset, e);
                        }

                        if (held){
                            timer = mover.delay(100, this, e);
                        } else {
                            timer = (function(){
                                held = true;
                                mover(e);
                            }).delay(400);
                        }
                    }.bind(this);
                    return mover;
                }.bind(this);

                var clear = function(){
                    clearTimeout(timer);
                    held = false;
                };

                this.keyboard.addEvents({
                    'keydown:shift+up': move(-1),
                    'keydown:shift+down': move(1),
                    'keyup:shift+up': clear,
                    'keyup:shift+down': clear,
                    'keyup:up': clear,
                    'keyup:down': clear
                });

                var shiftHint = '';
                if (this.options.allowMultiSelect && this.options.shiftForMultiSelect && this.options.useKeyboard){
                    shiftHint = ' (Shift multi-selects).';
                }

                this.keyboard.addShortcuts({
                    'Select Previous Row': {
                        keys: 'up',
                        shortcut: 'up arrow',
                        handler: move(-1),
                        description: 'Select the previous row in the table.' + shiftHint
                    },
                    'Select Next Row': {
                        keys: 'down',
                        shortcut: 'down arrow',
                        handler: move(1),
                        description: 'Select the next row in the table.' + shiftHint
                    }
                });

            }
            this.keyboard[attach ? 'activate' : 'deactivate']();
        }
        this.updateSelects();
    },

    mouseleave: function(){
        if (this.hovered) this.leaveRow(this.hovered);
    }

});

/*
---

script: HtmlTable.Sort.js

name: HtmlTable.Sort

description: Builds a stripy, sortable table with methods to add rows.

license: MIT-style license

authors:
  - Harald Kirschner
  - Aaron Newton
  - Jacob Thornton

requires:
  - Core/Hash
  - HtmlTable
  - Class.refactor
  - Element.Delegation.Pseudo
  - String.Extras
  - Date

provides: [HtmlTable.Sort]

...
*/
(function(){

    var readOnlyNess = document.createElement('table');
    try {
        readOnlyNess.innerHTML = '<tr><td></td></tr>';
        readOnlyNess = readOnlyNess.childNodes.length === 0;
    } catch (e){
        readOnlyNess = true;
    }

    HtmlTable = Class.refactor(HtmlTable, {

        options: {/*
		onSort: function(){}, */
            sortIndex: 0,
            sortReverse: false,
            parsers: [],
            defaultParser: 'string',
            classSortable: 'table-sortable',
            classHeadSort: 'table-th-sort',
            classHeadSortRev: 'table-th-sort-rev',
            classNoSort: 'table-th-nosort',
            classGroupHead: 'table-tr-group-head',
            classGroup: 'table-tr-group',
            classCellSort: 'table-td-sort',
            classSortSpan: 'table-th-sort-span',
            sortable: false,
            thSelector: 'th'
        },

        initialize: function(){
            this.previous.apply(this, arguments);
            if (this.occluded) return this.occluded;
            this.sorted = {index: null, dir: 1};
            if (!this.bound) this.bound = {};
            this.bound.headClick = this.headClick.bind(this);
            this.sortSpans = new Elements();
            if (this.options.sortable){
                this.enableSort();
                if (this.options.sortIndex != null) this.sort(this.options.sortIndex, this.options.sortReverse);
            }
        },

        attachSorts: function(attach){
            this.detachSorts();
            if (attach !== false) this.element.addEvent('click:relay(' + this.options.thSelector + ')', this.bound.headClick);
        },

        detachSorts: function(){
            this.element.removeEvents('click:relay(' + this.options.thSelector + ')');
        },

        setHeaders: function(){
            this.previous.apply(this, arguments);
            if (this.sortable) this.setParsers();
        },

        setParsers: function(){
            this.parsers = this.detectParsers();
        },

        detectParsers: function(){
            return this.head && this.head.getElements(this.options.thSelector).flatten().map(this.detectParser, this);
        },

        detectParser: function(cell, index){
            if (cell.hasClass(this.options.classNoSort) || cell.retrieve('htmltable-parser')) return cell.retrieve('htmltable-parser');
            var thDiv = new Element('div');
            thDiv.adopt(cell.childNodes).inject(cell);
            var sortSpan = new Element('span', {'class': this.options.classSortSpan}).inject(thDiv, 'top');
            this.sortSpans.push(sortSpan);
            var parser = this.options.parsers[index],
                rows = this.body.rows,
                cancel;
            switch (typeOf(parser)){
                case 'function': parser = {convert: parser}; cancel = true; break;
                case 'string': parser = parser; cancel = true; break;
            }
            if (!cancel){
                HtmlTable.ParserPriority.some(function(parserName){
                    var current = HtmlTable.Parsers[parserName],
                        match = current.match;
                    if (!match) return false;
                    for (var i = 0, j = rows.length; i < j; i++){
                        var cell = document.id(rows[i].cells[index]),
                            text = cell ? cell.get('html').clean() : '';
                        if (text && match.test(text)){
                            parser = current;
                            return true;
                        }
                    }
                });
            }
            if (!parser) parser = this.options.defaultParser;
            cell.store('htmltable-parser', parser);
            return parser;
        },

        headClick: function(event, el){
            if (!this.head || el.hasClass(this.options.classNoSort)) return;
            return this.sort(Array.indexOf(this.head.getElements(this.options.thSelector).flatten(), el) % this.body.rows[0].cells.length);
        },

        serialize: function(){
            var previousSerialization = this.previous.apply(this, arguments) || {};
            if (this.options.sortable){
                previousSerialization.sortIndex = this.sorted.index;
                previousSerialization.sortReverse = this.sorted.reverse;
            }
            return previousSerialization;
        },

        restore: function(tableState){
            if (this.options.sortable && tableState.sortIndex){
                this.sort(tableState.sortIndex, tableState.sortReverse);
            }
            this.previous.apply(this, arguments);
        },

        setSortedState: function(index, reverse){
            if (reverse != null) this.sorted.reverse = reverse;
            else if (this.sorted.index == index) this.sorted.reverse = !this.sorted.reverse;
            else this.sorted.reverse = this.sorted.index == null;

            if (index != null) this.sorted.index = index;
        },

        setHeadSort: function(sorted){
            var head = $$(!this.head.length ? this.head.cells[this.sorted.index] : this.head.map(function(row){
                return row.getElements(this.options.thSelector)[this.sorted.index];
            }, this).clean());
            if (!head.length) return;
            if (sorted){
                head.addClass(this.options.classHeadSort);
                if (this.sorted.reverse) head.addClass(this.options.classHeadSortRev);
                else head.removeClass(this.options.classHeadSortRev);
            } else {
                head.removeClass(this.options.classHeadSort).removeClass(this.options.classHeadSortRev);
            }
        },

        setRowSort: function(data, pre){
            var count = data.length,
                body = this.body,
                group,
                rowIndex;

            while (count){
                var item = data[--count],
                    position = item.position,
                    row = body.rows[position];

                if (row.disabled) continue;
                if (!pre){
                    group = this.setGroupSort(group, row, item);
                    this.setRowStyle(row, count);
                }
                body.appendChild(row);

                for (rowIndex = 0; rowIndex < count; rowIndex++){
                    if (data[rowIndex].position > position) data[rowIndex].position--;
                }
            }
        },

        setRowStyle: function(row, i){
            this.previous(row, i);
            row.cells[this.sorted.index].addClass(this.options.classCellSort);
        },

        setGroupSort: function(group, row, item){
            if (group == item.value) row.removeClass(this.options.classGroupHead).addClass(this.options.classGroup);
            else row.removeClass(this.options.classGroup).addClass(this.options.classGroupHead);
            return item.value;
        },

        getParser: function(){
            var parser = this.parsers[this.sorted.index];
            return typeOf(parser) == 'string' ? HtmlTable.Parsers[parser] : parser;
        },

        sort: function(index, reverse, pre, sortFunction){
            if (!this.head) return;

            if (!pre){
                this.clearSort();
                this.setSortedState(index, reverse);
                this.setHeadSort(true);
            }

            var parser = this.getParser();
            if (!parser) return;

            var rel;
            if (!readOnlyNess){
                rel = this.body.getParent();
                this.body.dispose();
            }

            var data = this.parseData(parser).sort(sortFunction ? sortFunction : function(a, b){
                if (a.value === b.value) return 0;
                return a.value > b.value ? 1 : -1;
            });

            var reversed = this.sorted.reverse == (parser == HtmlTable.Parsers['input-checked']);
            if (reversed) data.reverse(true);
            this.setRowSort(data, pre);

            if (rel) rel.grab(this.body);
            this.fireEvent('stateChanged');
            return this.fireEvent('sort', [this.body, this.sorted.index, reversed ? 'asc' : 'desc']);
        },

        parseData: function(parser){
            return Array.map(this.body.rows, function(row, i){
                var value = parser.convert.call(document.id(row.cells[this.sorted.index]));
                return {
                    position: i,
                    value: value
                };
            }, this);
        },

        clearSort: function(){
            this.setHeadSort(false);
            this.body.getElements('td').removeClass(this.options.classCellSort);
        },

        reSort: function(){
            if (this.sortable) this.sort.call(this, this.sorted.index, this.sorted.reverse);
            return this;
        },

        enableSort: function(){
            this.element.addClass(this.options.classSortable);
            this.attachSorts(true);
            this.setParsers();
            this.sortable = true;
            return this;
        },

        disableSort: function(){
            this.element.removeClass(this.options.classSortable);
            this.attachSorts(false);
            this.sortSpans.each(function(span){
                span.destroy();
            });
            this.sortSpans.empty();
            this.sortable = false;
            return this;
        }

    });

    HtmlTable.ParserPriority = ['date', 'input-checked', 'input-value', 'float', 'number'];

    HtmlTable.Parsers = {

        'date': {
            match: /^\d{2}[-\/ ]\d{2}[-\/ ]\d{2,4}$/,
            convert: function(){
                var d = Date.parse(this.get('text').stripTags());
                return (typeOf(d) == 'date') ? d.format('db') : '';
            },
            type: 'date'
        },
        'input-checked': {
            match: / type="(radio|checkbox)"/,
            convert: function(){
                return this.getElement('input').checked;
            }
        },
        'input-value': {
            match: /<input/,
            convert: function(){
                return this.getElement('input').value;
            }
        },
        'number': {
            match: /^\d+[^\d.,]*$/,
            convert: function(){
                return this.get('text').stripTags().toInt();
            },
            number: true
        },
        'numberLax': {
            match: /^[^\d]+\d+$/,
            convert: function(){
                return this.get('text').replace(/[^-?^0-9]/, '').stripTags().toInt();
            },
            number: true
        },
        'float': {
            match: /^[\d]+\.[\d]+/,
            convert: function(){
                return this.get('text').replace(/[^-?^\d.e]/, '').stripTags().toFloat();
            },
            number: true
        },
        'floatLax': {
            match: /^[^\d]+[\d]+\.[\d]+$/,
            convert: function(){
                return this.get('text').replace(/[^-?^\d.]/, '').stripTags().toFloat();
            },
            number: true
        },
        'string': {
            match: null,
            convert: function(){
                return this.get('text').stripTags().toLowerCase();
            }
        },
        'title': {
            match: null,
            convert: function(){
                return this.title;
            }
        }

    };

//<1.2compat>
    HtmlTable.Parsers = new Hash(HtmlTable.Parsers);
//</1.2compat>

    HtmlTable.defineParsers = function(parsers){
        HtmlTable.Parsers = Object.append(HtmlTable.Parsers, parsers);
        for (var parser in parsers){
            HtmlTable.ParserPriority.unshift(parser);
        }
    };

})();


/*
---

script: HtmlTable.Zebra.js

name: HtmlTable.Zebra

description: Builds a stripy table with methods to add rows.

license: MIT-style license

authors:
  - Harald Kirschner
  - Aaron Newton

requires:
  - HtmlTable
  - Element.Shortcuts
  - Class.refactor

provides: [HtmlTable.Zebra]

...
*/

HtmlTable = Class.refactor(HtmlTable, {

    options: {
        classZebra: 'table-tr-odd',
        zebra: true,
        zebraOnlyVisibleRows: true
    },

    initialize: function(){
        this.previous.apply(this, arguments);
        if (this.occluded) return this.occluded;
        if (this.options.zebra) this.updateZebras();
    },

    updateZebras: function(){
        var index = 0;
        Array.each(this.body.rows, function(row){
            if (!this.options.zebraOnlyVisibleRows || row.isDisplayed()){
                this.zebra(row, index++);
            }
        }, this);
    },

    setRowStyle: function(row, i){
        if (this.previous) this.previous(row, i);
        this.zebra(row, i);
    },

    zebra: function(row, i){
        return row[((i % 2) ? 'remove' : 'add')+'Class'](this.options.classZebra);
    },

    push: function(){
        var pushed = this.previous.apply(this, arguments);
        if (this.options.zebra) this.updateZebras();
        return pushed;
    }

});

/*
---

script: Scroller.js

name: Scroller

description: Class which scrolls the contents of any Element (including the window) when the mouse reaches the Element's boundaries.

license: MIT-style license

authors:
  - Valerio Proietti

requires:
  - Core/Events
  - Core/Options
  - Core/Element.Event
  - Core/Element.Dimensions
  - MooTools.More

provides: [Scroller]

...
*/
(function(){

    var Scroller = this.Scroller = new Class({

        Implements: [Events, Options],

        options: {
            area: 20,
            velocity: 1,
            onChange: function(x, y){
                this.element.scrollTo(x, y);
            },
            fps: 50
        },

        initialize: function(element, options){
            this.setOptions(options);
            this.element = document.id(element);
            this.docBody = document.id(this.element.getDocument().body);
            this.listener = (typeOf(this.element) != 'element') ? this.docBody : this.element;
            this.timer = null;
            this.bound = {
                attach: this.attach.bind(this),
                detach: this.detach.bind(this),
                getCoords: this.getCoords.bind(this)
            };
        },

        start: function(){
            this.listener.addEvents({
                mouseover: this.bound.attach,
                mouseleave: this.bound.detach
            });
            return this;
        },

        stop: function(){
            this.listener.removeEvents({
                mouseover: this.bound.attach,
                mouseleave: this.bound.detach
            });
            this.detach();
            this.timer = clearInterval(this.timer);
            return this;
        },

        attach: function(){
            this.listener.addEvent('mousemove', this.bound.getCoords);
        },

        detach: function(){
            this.listener.removeEvent('mousemove', this.bound.getCoords);
            this.timer = clearInterval(this.timer);
        },

        getCoords: function(event){
            this.page = (this.listener.get('tag') == 'body') ? event.client : event.page;
            if (!this.timer) this.timer = this.scroll.periodical(Math.round(1000 / this.options.fps), this);
        },

        scroll: function(){
            var size = this.element.getSize(),
                scroll = this.element.getScroll(),
                pos = ((this.element != this.docBody) && (this.element != window)) ? this.element.getOffsets() : {x: 0, y: 0},
                scrollSize = this.element.getScrollSize(),
                change = {x: 0, y: 0},
                top = this.options.area.top || this.options.area,
                bottom = this.options.area.bottom || this.options.area;
            for (var z in this.page){
                if (this.page[z] < (top + pos[z]) && scroll[z] != 0){
                    change[z] = (this.page[z] - top - pos[z]) * this.options.velocity;
                } else if (this.page[z] + bottom > (size[z] + pos[z]) && scroll[z] + size[z] != scrollSize[z]){
                    change[z] = (this.page[z] - size[z] + bottom - pos[z]) * this.options.velocity;
                }
                change[z] = change[z].round();
            }
            if (change.y || change.x) this.fireEvent('change', [scroll.x + change.x, scroll.y + change.y]);
        }

    });

})();


/*
---

script: Tips.js

name: Tips

description: Class for creating nice tips that follow the mouse cursor when hovering an element.

license: MIT-style license

authors:
  - Valerio Proietti
  - Christoph Pojer
  - Luis Merino

requires:
  - Core/Options
  - Core/Events
  - Core/Element.Event
  - Core/Element.Style
  - Core/Element.Dimensions
  - MooTools.More

provides: [Tips]

...
*/

(function(){

    var read = function(option, element){
        return (option) ? (typeOf(option) == 'function' ? option(element) : element.get(option)) : '';
    };

    var Tips = this.Tips = new Class({

        Implements: [Events, Options],

        options: {/*
		id: null,
		onAttach: function(element){},
		onDetach: function(element){},
		onBound: function(coords){},*/
            onShow: function(){
                this.tip.setStyle('display', 'block');
            },
            onHide: function(){
                this.tip.setStyle('display', 'none');
            },
            title: 'title',
            text: function(element){
                return element.get('rel') || element.get('href');
            },
            showDelay: 100,
            hideDelay: 100,
            className: 'tip-wrap',
            offset: {x: 16, y: 16},
            windowPadding: {x:0, y:0},
            fixed: false,
            waiAria: true,
            hideEmpty: false
        },

        initialize: function(){
            var params = Array.link(arguments, {
                options: Type.isObject,
                elements: function(obj){
                    return obj != null;
                }
            });
            this.setOptions(params.options);
            if (params.elements) this.attach(params.elements);
            this.container = new Element('div', {'class': 'tip'});

            if (this.options.id){
                this.container.set('id', this.options.id);
                if (this.options.waiAria) this.attachWaiAria();
            }
        },

        toElement: function(){
            if (this.tip) return this.tip;

            this.tip = new Element('div', {
                'class': this.options.className,
                styles: {
                    position: 'absolute',
                    top: 0,
                    left: 0,
                    display: 'none'
                }
            }).adopt(
                new Element('div', {'class': 'tip-top'}),
                this.container,
                new Element('div', {'class': 'tip-bottom'})
            );

            return this.tip;
        },

        attachWaiAria: function(){
            var id = this.options.id;
            this.container.set('role', 'tooltip');

            if (!this.waiAria){
                this.waiAria = {
                    show: function(element){
                        if (id) element.set('aria-describedby', id);
                        this.container.set('aria-hidden', 'false');
                    },
                    hide: function(element){
                        if (id) element.erase('aria-describedby');
                        this.container.set('aria-hidden', 'true');
                    }
                };
            }
            this.addEvents(this.waiAria);
        },

        detachWaiAria: function(){
            if (this.waiAria){
                this.container.erase('role');
                this.container.erase('aria-hidden');
                this.removeEvents(this.waiAria);
            }
        },

        attach: function(elements){
            $$(elements).each(function(element){
                var title = read(this.options.title, element),
                    text = read(this.options.text, element);

                element.set('title', '').store('tip:native', title).retrieve('tip:title', title);
                element.retrieve('tip:text', text);
                this.fireEvent('attach', [element]);

                var events = ['enter', 'leave'];
                if (!this.options.fixed) events.push('move');

                events.each(function(value){
                    var event = element.retrieve('tip:' + value);
                    if (!event) event = function(event){
                        this['element' + value.capitalize()].apply(this, [event, element]);
                    }.bind(this);

                    element.store('tip:' + value, event).addEvent('mouse' + value, event);
                }, this);
            }, this);

            return this;
        },

        detach: function(elements){
            $$(elements).each(function(element){
                ['enter', 'leave', 'move'].each(function(value){
                    element.removeEvent('mouse' + value, element.retrieve('tip:' + value)).eliminate('tip:' + value);
                });

                this.fireEvent('detach', [element]);

                if (this.options.title == 'title'){ // This is necessary to check if we can revert the title
                    var original = element.retrieve('tip:native');
                    if (original) element.set('title', original);
                }
            }, this);

            return this;
        },

        elementEnter: function(event, element){
            clearTimeout(this.timer);
            this.timer = (function(){
                this.container.empty();
                var showTip = !this.options.hideEmpty;
                ['title', 'text'].each(function(value){
                    var content = element.retrieve('tip:' + value);
                    var div = this['_' + value + 'Element'] = new Element('div', {
                        'class': 'tip-' + value
                    }).inject(this.container);
                    if (content){
                        this.fill(div, content);
                        showTip = true;
                    }
                }, this);
                if (showTip){
                    this.show(element);
                } else {
                    this.hide(element);
                }
                this.position((this.options.fixed) ? {page: element.getPosition()} : event);
            }).delay(this.options.showDelay, this);
        },

        elementLeave: function(event, element){
            clearTimeout(this.timer);
            this.timer = this.hide.delay(this.options.hideDelay, this, element);
            this.fireForParent(event, element);
        },

        setTitle: function(title){
            if (this._titleElement){
                this._titleElement.empty();
                this.fill(this._titleElement, title);
            }
            return this;
        },

        setText: function(text){
            if (this._textElement){
                this._textElement.empty();
                this.fill(this._textElement, text);
            }
            return this;
        },

        fireForParent: function(event, element){
            element = element.getParent();
            if (!element || element == document.body) return;
            if (element.retrieve('tip:enter')) element.fireEvent('mouseenter', event);
            else this.fireForParent(event, element);
        },

        elementMove: function(event, element){
            this.position(event);
        },

        position: function(event){
            if (!this.tip) document.id(this);

            var size = window.getSize(), scroll = window.getScroll(),
                tip = {x: this.tip.offsetWidth, y: this.tip.offsetHeight},
                props = {x: 'left', y: 'top'},
                bounds = {y: false, x2: false, y2: false, x: false},
                obj = {};

            for (var z in props){
                obj[props[z]] = event.page[z] + this.options.offset[z];
                if (obj[props[z]] < 0) bounds[z] = true;
                if ((obj[props[z]] + tip[z] - scroll[z]) > size[z] - this.options.windowPadding[z]){
                    obj[props[z]] = event.page[z] - this.options.offset[z] - tip[z];
                    bounds[z+'2'] = true;
                }
            }

            this.fireEvent('bound', bounds);
            this.tip.setStyles(obj);
        },

        fill: function(element, contents){
            if (typeof contents == 'string') element.set('html', contents);
            else element.adopt(contents);
        },

        show: function(element){
            if (!this.tip) document.id(this);
            if (!this.tip.getParent()) this.tip.inject(document.body);
            this.fireEvent('show', [this.tip, element]);
        },

        hide: function(element){
            if (!this.tip) document.id(this);
            this.fireEvent('hide', [this.tip, element]);
        }

    });

})();

/*
---
name: Locale.CH.Number
description: Number messages for Switzerland.
license: MIT-style license
authors:
  - Kim D. Jeker
requires:
  - Locale
provides: [Locale.CH.Number]
...
*/

Locale.define('CH', 'Number', {

    decimal: ',',
    group: '\'',

    currency: {
        decimal: '.',
        suffix: ' CHF'
    }

});

/*
---

name: Locale.EU.Number

description: Number messages for Europe.

license: MIT-style license

authors:
  - Arian Stolwijk

requires:
  - Locale

provides: [Locale.EU.Number]

...
*/

Locale.define('EU', 'Number', {

    decimal: ',',
    group: '.',

    currency: {
        prefix: '€ '
    }

});

/*
---

script: Locale.Set.From.js

name: Locale.Set.From

description: Provides an alternative way to create Locale.Set objects.

license: MIT-style license

authors:
  - Tim Wienk

requires:
  - Core/JSON
  - Locale

provides: Locale.Set.From

...
*/

(function(){

    var parsers = {
        'json': JSON.decode
    };

    Locale.Set.defineParser = function(name, fn){
        parsers[name] = fn;
    };

    Locale.Set.from = function(set, type){
        if (instanceOf(set, Locale.Set)) return set;

        if (!type && typeOf(set) == 'string') type = 'json';
        if (parsers[type]) set = parsers[type](set);

        var locale = new Locale.Set;

        locale.sets = set.sets || {};

        if (set.inherits){
            locale.inherits.locales = Array.convert(set.inherits.locales);
            locale.inherits.sets = set.inherits.sets || {};
        }

        return locale;
    };

})();

/*
---

name: Locale.ZA.Number

description: Number messages for ZA.

license: MIT-style license

authors:
  - Werner Mollentze

requires:
  - Locale

provides: [Locale.ZA.Number]

...
*/

Locale.define('ZA', 'Number', {

    decimal: '.',
    group: ',',

    currency: {
        prefix: 'R '
    }

});



/*
---

name: Locale.af-ZA.Date

description: Date messages for ZA Afrikaans.

license: MIT-style license

authors:
  - Werner Mollentze

requires:
  - Locale

provides: [Locale.af-ZA.Date]

...
*/

Locale.define('af-ZA', 'Date', {

    months: ['Januarie', 'Februarie', 'Maart', 'April', 'Mei', 'Junie', 'Julie', 'Augustus', 'September', 'Oktober', 'November', 'Desember'],
    months_abbr: ['Jan', 'Feb', 'Mrt', 'Apr', 'Mei', 'Jun', 'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Des'],
    days: ['Sondag', 'Maandag', 'Dinsdag', 'Woensdag', 'Donderdag', 'Vrydag', 'Saterdag'],
    days_abbr: ['Son', 'Maa', 'Din', 'Woe', 'Don', 'Vry', 'Sat'],

    // Culture's date order: MM/DD/YYYY
    dateOrder: ['date', 'month', 'year'],
    shortDate: '%d-%m-%Y',
    shortTime: '%H:%M',
    AM: 'VM',
    PM: 'NM',
    firstDayOfWeek: 1,

    // Date.Extras
    ordinal: function(dayOfMonth){
        return ((dayOfMonth > 1 && dayOfMonth < 20 && dayOfMonth != 8) || (dayOfMonth > 100 && dayOfMonth.toString().substr(-2, 1) == '1')) ? 'de' : 'ste';
    },

    lessThanMinuteAgo: 'minder as \'n minuut gelede',
    minuteAgo: 'ongeveer \'n minuut gelede',
    minutesAgo: '{delta} minute gelede',
    hourAgo: 'omtret \'n uur gelede',
    hoursAgo: 'ongeveer {delta} ure gelede',
    dayAgo: '1 dag gelede',
    daysAgo: '{delta} dae gelede',
    weekAgo: '1 week gelede',
    weeksAgo: '{delta} weke gelede',
    monthAgo: '1 maand gelede',
    monthsAgo: '{delta} maande gelede',
    yearAgo: '1 jaar gelede',
    yearsAgo: '{delta} jare gelede',

    lessThanMinuteUntil: 'oor minder as \'n minuut',
    minuteUntil: 'oor ongeveer \'n minuut',
    minutesUntil: 'oor {delta} minute',
    hourUntil: 'oor ongeveer \'n uur',
    hoursUntil: 'oor {delta} uur',
    dayUntil: 'oor ongeveer \'n dag',
    daysUntil: 'oor {delta} dae',
    weekUntil: 'oor \'n week',
    weeksUntil: 'oor {delta} weke',
    monthUntil: 'oor \'n maand',
    monthsUntil: 'oor {delta} maande',
    yearUntil: 'oor \'n jaar',
    yearsUntil: 'oor {delta} jaar'

});

/*
---

name: Locale.af-ZA.Form.Validator

description: Form Validator messages for Afrikaans.

license: MIT-style license

authors:
  - Werner Mollentze

requires:
  - Locale

provides: [Locale.af-ZA.Form.Validator]

...
*/

Locale.define('af-ZA', 'FormValidator', {

    required: 'Hierdie veld word vereis.',
    length: 'Voer asseblief {length} karakters in (u het {elLength} karakters ingevoer)',
    minLength: 'Voer asseblief ten minste {minLength} karakters in (u het {length} karakters ingevoer).',
    maxLength: 'Moet asseblief nie meer as {maxLength} karakters invoer nie (u het {length} karakters ingevoer).',
    integer: 'Voer asseblief \'n heelgetal in hierdie veld in. Getalle met desimale (bv. 1.25) word nie toegelaat nie.',
    numeric: 'Voer asseblief slegs numeriese waardes in hierdie veld in (bv. "1" of "1.1" of "-1" of "-1.1").',
    digits: 'Gebruik asseblief slegs nommers en punktuasie in hierdie veld. (by voorbeeld, \'n telefoon nommer wat koppeltekens en punte bevat is toelaatbaar).',
    alpha: 'Gebruik asseblief slegs letters (a-z) binne-in hierdie veld. Geen spasies of ander karakters word toegelaat nie.',
    alphanum: 'Gebruik asseblief slegs letters (a-z) en nommers (0-9) binne-in hierdie veld. Geen spasies of ander karakters word toegelaat nie.',
    dateSuchAs: 'Voer asseblief \'n geldige datum soos {date} in',
    dateInFormatMDY: 'Voer asseblief \'n geldige datum soos MM/DD/YYYY in (bv. "12/31/1999")',
    email: 'Voer asseblief \'n geldige e-pos adres in. Byvoorbeeld "fred@domain.com".',
    url: 'Voer asseblief \'n geldige bronadres (URL) soos http://www.example.com in.',
    currencyDollar: 'Voer asseblief \'n geldige $ bedrag in. Byvoorbeeld $100.00 .',
    oneRequired: 'Voer asseblief iets in vir ten minste een van hierdie velde.',
    errorPrefix: 'Fout: ',
    warningPrefix: 'Waarskuwing: ',

    // Form.Validator.Extras
    noSpace: 'Daar mag geen spasies in hierdie toevoer wees nie.',
    reqChkByNode: 'Geen items is gekies nie.',
    requiredChk: 'Hierdie veld word vereis.',
    reqChkByName: 'Kies asseblief \'n {label}.',
    match: 'Hierdie veld moet by die {matchName} veld pas',
    startDate: 'die begin datum',
    endDate: 'die eind datum',
    currentDate: 'die huidige datum',
    afterDate: 'Die datum moet dieselfde of na {label} wees.',
    beforeDate: 'Die datum moet dieselfde of voor {label} wees.',
    startMonth: 'Kies asseblief \'n begin maand',
    sameMonth: 'Hierdie twee datums moet in dieselfde maand wees - u moet een of beide verander.',
    creditcard: 'Die ingevoerde kredietkaart nommer is ongeldig. Bevestig asseblief die nommer en probeer weer. {length} syfers is ingevoer.'

});

/*
---

name: Locale.af-ZA.Number

description: Number messages for ZA Afrikaans.

license: MIT-style license

authors:
  - Werner Mollentze

requires:
  - Locale
  - Locale.ZA.Number

provides: [Locale.af-ZA.Number]

...
*/

Locale.define('af-ZA').inherit('ZA', 'Number');

/*
---

name: Locale.ar.Date

description: Date messages for Arabic.

license: MIT-style license

authors:
  - Chafik Barbar

requires:
  - Locale

provides: [Locale.ar.Date]

...
*/

Locale.define('ar', 'Date', {

    // Culture's date order: DD/MM/YYYY
    dateOrder: ['date', 'month', 'year'],
    shortDate: '%d/%m/%Y',
    shortTime: '%H:%M'

});

/*
---

name: Locale.ar.Form.Validator

description: Form Validator messages for Arabic.

license: MIT-style license

authors:
  - Chafik Barbar

requires:
  - Locale

provides: [Locale.ar.Form.Validator]

...
*/

Locale.define('ar', 'FormValidator', {

    required: 'هذا الحقل مطلوب.',
    minLength: 'رجاءً إدخال {minLength} أحرف على الأقل (تم إدخال {length} أحرف).',
    maxLength: 'الرجاء عدم إدخال أكثر من {maxLength} أحرف (تم إدخال {length} أحرف).',
    integer: 'الرجاء إدخال عدد صحيح في هذا الحقل. أي رقم ذو كسر عشري أو مئوي (مثال 1.25 ) غير مسموح.',
    numeric: 'الرجاء إدخال قيم رقمية في هذا الحقل (مثال "1" أو "1.1" أو "-1" أو "-1.1").',
    digits: 'الرجاء أستخدام قيم رقمية وعلامات ترقيمية فقط في هذا الحقل (مثال, رقم هاتف مع نقطة أو شحطة)',
    alpha: 'الرجاء أستخدام أحرف فقط (ا-ي) في هذا الحقل. أي فراغات أو علامات غير مسموحة.',
    alphanum: 'الرجاء أستخدام أحرف فقط (ا-ي) أو أرقام (0-9) فقط في هذا الحقل. أي فراغات أو علامات غير مسموحة.',
    dateSuchAs: 'الرجاء إدخال تاريخ صحيح كالتالي {date}',
    dateInFormatMDY: 'الرجاء إدخال تاريخ صحيح (مثال, 31-12-1999)',
    email: 'الرجاء إدخال بريد إلكتروني صحيح.',
    url: 'الرجاء إدخال عنوان إلكتروني صحيح مثل http://www.example.com',
    currencyDollar: 'الرجاء إدخال قيمة $ صحيحة. مثال, 100.00$',
    oneRequired: 'الرجاء إدخال قيمة في أحد هذه الحقول على الأقل.',
    errorPrefix: 'خطأ: ',
    warningPrefix: 'تحذير: '

});

/*
---

name: Locale.ca-CA.Date

description: Date messages for Catalan.

license: MIT-style license

authors:
  - Ãlfons Sanchez

requires:
  - Locale

provides: [Locale.ca-CA.Date]

...
*/

Locale.define('ca-CA', 'Date', {

    months: ['Gener', 'Febrer', 'Març', 'Abril', 'Maig', 'Juny', 'Juli', 'Agost', 'Setembre', 'Octubre', 'Novembre', 'Desembre'],
    months_abbr: ['gen.', 'febr.', 'març', 'abr.', 'maig', 'juny', 'jul.', 'ag.', 'set.', 'oct.', 'nov.', 'des.'],
    days: ['Diumenge', 'Dilluns', 'Dimarts', 'Dimecres', 'Dijous', 'Divendres', 'Dissabte'],
    days_abbr: ['dg', 'dl', 'dt', 'dc', 'dj', 'dv', 'ds'],

    // Culture's date order: DD/MM/YYYY
    dateOrder: ['date', 'month', 'year'],
    shortDate: '%d/%m/%Y',
    shortTime: '%H:%M',
    AM: 'AM',
    PM: 'PM',
    firstDayOfWeek: 0,

    // Date.Extras
    ordinal: '',

    lessThanMinuteAgo: 'fa menys d`un minut',
    minuteAgo: 'fa un minut',
    minutesAgo: 'fa {delta} minuts',
    hourAgo: 'fa un hora',
    hoursAgo: 'fa unes {delta} hores',
    dayAgo: 'fa un dia',
    daysAgo: 'fa {delta} dies',

    lessThanMinuteUntil: 'menys d`un minut des d`ara',
    minuteUntil: 'un minut des d`ara',
    minutesUntil: '{delta} minuts des d`ara',
    hourUntil: 'un hora des d`ara',
    hoursUntil: 'unes {delta} hores des d`ara',
    dayUntil: '1 dia des d`ara',
    daysUntil: '{delta} dies des d`ara'

});

/*
---

name: Locale.ca-CA.Form.Validator

description: Form Validator messages for Catalan.

license: MIT-style license

authors:
  - Miquel Hudin
  - Ãlfons Sanchez

requires:
  - Locale

provides: [Locale.ca-CA.Form.Validator]

...
*/

Locale.define('ca-CA', 'FormValidator', {

    required: 'Aquest camp es obligatori.',
    minLength: 'Per favor introdueix al menys {minLength} caracters (has introduit {length} caracters).',
    maxLength: 'Per favor introdueix no mes de {maxLength} caracters (has introduit {length} caracters).',
    integer: 'Per favor introdueix un nombre enter en aquest camp. Nombres amb decimals (p.e. 1,25) no estan permesos.',
    numeric: 'Per favor introdueix sols valors numerics en aquest camp (p.e. "1" o "1,1" o "-1" o "-1,1").',
    digits: 'Per favor usa sols numeros i puntuacio en aquest camp (per exemple, un nombre de telefon amb guions i punts no esta permes).',
    alpha: 'Per favor utilitza lletres nomes (a-z) en aquest camp. No s´admiteixen espais ni altres caracters.',
    alphanum: 'Per favor, utilitza nomes lletres (a-z) o numeros (0-9) en aquest camp. No s´admiteixen espais ni altres caracters.',
    dateSuchAs: 'Per favor introdueix una data valida com {date}',
    dateInFormatMDY: 'Per favor introdueix una data valida com DD/MM/YYYY (p.e. "31/12/1999")',
    email: 'Per favor, introdueix una adreça de correu electronic valida. Per exemple, "fred@domain.com".',
    url: 'Per favor introdueix una URL valida com http://www.example.com.',
    currencyDollar: 'Per favor introdueix una quantitat valida de €. Per exemple €100,00 .',
    oneRequired: 'Per favor introdueix alguna cosa per al menys una d´aquestes entrades.',
    errorPrefix: 'Error: ',
    warningPrefix: 'Avis: ',

    // Form.Validator.Extras
    noSpace: 'No poden haver espais en aquesta entrada.',
    reqChkByNode: 'No hi han elements seleccionats.',
    requiredChk: 'Aquest camp es obligatori.',
    reqChkByName: 'Per favor selecciona una {label}.',
    match: 'Aquest camp necessita coincidir amb el camp {matchName}',
    startDate: 'la data de inici',
    endDate: 'la data de fi',
    currentDate: 'la data actual',
    afterDate: 'La data deu ser igual o posterior a {label}.',
    beforeDate: 'La data deu ser igual o anterior a {label}.',
    startMonth: 'Per favor selecciona un mes d´orige',
    sameMonth: 'Aquestes dos dates deuen estar dins del mateix mes - deus canviar una o altra.'

});

/*
---

name: Locale.cs-CZ.Date

description: Date messages for Czech.

license: MIT-style license

authors:
  - Jan Černý chemiX
  - Christopher Zukowski

requires:
  - Locale

provides: [Locale.cs-CZ.Date]

...
*/
(function(){

// Czech language pluralization rules, see http://unicode.org/repos/cldr-tmp/trunk/diff/supplemental/language_plural_rules.html
// one -> n is 1;            1
// few -> n in 2..4;         2-4
// other -> everything else  0, 5-999, 1.31, 2.31, 5.31...
    var pluralize = function(n, one, few, other){
        if (n == 1) return one;
        else if (n == 2 || n == 3 || n == 4) return few;
        else return other;
    };

    Locale.define('cs-CZ', 'Date', {

        months: ['Leden', 'Únor', 'Březen', 'Duben', 'Květen', 'Červen', 'Červenec', 'Srpen', 'Září', 'Říjen', 'Listopad', 'Prosinec'],
        months_abbr: ['ledna', 'února', 'března', 'dubna', 'května', 'června', 'července', 'srpna', 'září', 'října', 'listopadu', 'prosince'],
        days: ['Neděle', 'Pondělí', 'Úterý', 'Středa', 'Čtvrtek', 'Pátek', 'Sobota'],
        days_abbr: ['ne', 'po', 'út', 'st', 'čt', 'pá', 'so'],

        // Culture's date order: DD.MM.YYYY
        dateOrder: ['date', 'month', 'year'],
        shortDate: '%d.%m.%Y',
        shortTime: '%H:%M',
        AM: 'dop.',
        PM: 'odp.',
        firstDayOfWeek: 1,

        // Date.Extras
        ordinal: '.',

        lessThanMinuteAgo: 'před chvílí',
        minuteAgo: 'přibližně před minutou',
        minutesAgo: function(delta){ return 'před {delta} ' + pluralize(delta, 'minutou', 'minutami', 'minutami'); },
        hourAgo: 'přibližně před hodinou',
        hoursAgo: function(delta){ return 'před {delta} ' + pluralize(delta, 'hodinou', 'hodinami', 'hodinami'); },
        dayAgo: 'před dnem',
        daysAgo: function(delta){ return 'před {delta} ' + pluralize(delta, 'dnem', 'dny', 'dny'); },
        weekAgo: 'před týdnem',
        weeksAgo: function(delta){ return 'před {delta} ' + pluralize(delta, 'týdnem', 'týdny', 'týdny'); },
        monthAgo: 'před měsícem',
        monthsAgo: function(delta){ return 'před {delta} ' + pluralize(delta, 'měsícem', 'měsíci', 'měsíci'); },
        yearAgo: 'před rokem',
        yearsAgo: function(delta){ return 'před {delta} ' + pluralize(delta, 'rokem', 'lety', 'lety'); },

        lessThanMinuteUntil: 'za chvíli',
        minuteUntil: 'přibližně za minutu',
        minutesUntil: function(delta){ return 'za {delta} ' + pluralize(delta, 'minutu', 'minuty', 'minut'); },
        hourUntil: 'přibližně za hodinu',
        hoursUntil: function(delta){ return 'za {delta} ' + pluralize(delta, 'hodinu', 'hodiny', 'hodin'); },
        dayUntil: 'za den',
        daysUntil: function(delta){ return 'za {delta} ' + pluralize(delta, 'den', 'dny', 'dnů'); },
        weekUntil: 'za týden',
        weeksUntil: function(delta){ return 'za {delta} ' + pluralize(delta, 'týden', 'týdny', 'týdnů'); },
        monthUntil: 'za měsíc',
        monthsUntil: function(delta){ return 'za {delta} ' + pluralize(delta, 'měsíc', 'měsíce', 'měsíců'); },
        yearUntil: 'za rok',
        yearsUntil: function(delta){ return 'za {delta} ' + pluralize(delta, 'rok', 'roky', 'let'); }
    });

})();

/*
---

name: Locale.cs-CZ.Form.Validator

description: Form Validator messages for Czech.

license: MIT-style license

authors:
  - Jan Černý chemiX

requires:
  - Locale

provides: [Locale.cs-CZ.Form.Validator]

...
*/

Locale.define('cs-CZ', 'FormValidator', {

    required: 'Tato položka je povinná.',
    minLength: 'Zadejte prosím alespoň {minLength} znaků (napsáno {length} znaků).',
    maxLength: 'Zadejte prosím méně než {maxLength} znaků (nápsáno {length} znaků).',
    integer: 'Zadejte prosím celé číslo. Desetinná čísla (např. 1.25) nejsou povolena.',
    numeric: 'Zadejte jen číselné hodnoty (tj. "1" nebo "1.1" nebo "-1" nebo "-1.1").',
    digits: 'Zadejte prosím pouze čísla a interpunkční znaménka(například telefonní číslo s pomlčkami nebo tečkami je povoleno).',
    alpha: 'Zadejte prosím pouze písmena (a-z). Mezery nebo jiné znaky nejsou povoleny.',
    alphanum: 'Zadejte prosím pouze písmena (a-z) nebo číslice (0-9). Mezery nebo jiné znaky nejsou povoleny.',
    dateSuchAs: 'Zadejte prosím platné datum jako {date}',
    dateInFormatMDY: 'Zadejte prosím platné datum jako MM / DD / RRRR (tj. "12/31/1999")',
    email: 'Zadejte prosím platnou e-mailovou adresu. Například "fred@domain.com".',
    url: 'Zadejte prosím platnou URL adresu jako http://www.example.com.',
    currencyDollar: 'Zadejte prosím platnou částku. Například $100.00.',
    oneRequired: 'Zadejte prosím alespoň jednu hodnotu pro tyto položky.',
    errorPrefix: 'Chyba: ',
    warningPrefix: 'Upozornění: ',

    // Form.Validator.Extras
    noSpace: 'V této položce nejsou povoleny mezery',
    reqChkByNode: 'Nejsou vybrány žádné položky.',
    requiredChk: 'Tato položka je vyžadována.',
    reqChkByName: 'Prosím vyberte {label}.',
    match: 'Tato položka se musí shodovat s položkou {matchName}',
    startDate: 'datum zahájení',
    endDate: 'datum ukončení',
    currentDate: 'aktuální datum',
    afterDate: 'Datum by mělo být stejné nebo větší než {label}.',
    beforeDate: 'Datum by mělo být stejné nebo menší než {label}.',
    startMonth: 'Vyberte počáteční měsíc.',
    sameMonth: 'Tyto dva datumy musí být ve stejném měsíci - změňte jeden z nich.',
    creditcard: 'Zadané číslo kreditní karty je neplatné. Prosím opravte ho. Bylo zadáno {length} čísel.'

});

/*
---

name: Locale.da-DK.Date

description: Date messages for Danish.

license: MIT-style license

authors:
  - Martin Overgaard
  - Henrik Hansen

requires:
  - Locale

provides: [Locale.da-DK.Date]

...
*/

Locale.define('da-DK', 'Date', {

    months: ['Januar', 'Februar', 'Marts', 'April', 'Maj', 'Juni', 'Juli', 'August', 'September', 'Oktober', 'November', 'December'],
    months_abbr: ['jan.', 'feb.', 'mar.', 'apr.', 'maj.', 'jun.', 'jul.', 'aug.', 'sep.', 'okt.', 'nov.', 'dec.'],
    days: ['Søndag', 'Mandag', 'Tirsdag', 'Onsdag', 'Torsdag', 'Fredag', 'Lørdag'],
    days_abbr: ['søn', 'man', 'tir', 'ons', 'tor', 'fre', 'lør'],

    // Culture's date order: DD-MM-YYYY
    dateOrder: ['date', 'month', 'year'],
    shortDate: '%d-%m-%Y',
    shortTime: '%H:%M',
    AM: 'AM',
    PM: 'PM',
    firstDayOfWeek: 1,

    // Date.Extras
    ordinal: '.',

    lessThanMinuteAgo: 'mindre end et minut siden',
    minuteAgo: 'omkring et minut siden',
    minutesAgo: '{delta} minutter siden',
    hourAgo: 'omkring en time siden',
    hoursAgo: 'omkring {delta} timer siden',
    dayAgo: '1 dag siden',
    daysAgo: '{delta} dage siden',
    weekAgo: '1 uge siden',
    weeksAgo: '{delta} uger siden',
    monthAgo: '1 måned siden',
    monthsAgo: '{delta} måneder siden',
    yearAgo: '1 år siden',
    yearsAgo: '{delta} år siden',

    lessThanMinuteUntil: 'mindre end et minut fra nu',
    minuteUntil: 'omkring et minut fra nu',
    minutesUntil: '{delta} minutter fra nu',
    hourUntil: 'omkring en time fra nu',
    hoursUntil: 'omkring {delta} timer fra nu',
    dayUntil: '1 dag fra nu',
    daysUntil: '{delta} dage fra nu',
    weekUntil: '1 uge fra nu',
    weeksUntil: '{delta} uger fra nu',
    monthUntil: '1 måned fra nu',
    monthsUntil: '{delta} måneder fra nu',
    yearUntil: '1 år fra nu',
    yearsUntil: '{delta} år fra nu'

});

/*
---

name: Locale.da-DK.Form.Validator

description: Form Validator messages for Danish.

license: MIT-style license

authors:
  - Martin Overgaard

requires:
  - Locale

provides: [Locale.da-DK.Form.Validator]

...
*/

Locale.define('da-DK', 'FormValidator', {

    required: 'Feltet skal udfyldes.',
    minLength: 'Skriv mindst {minLength} tegn (du skrev {length} tegn).',
    maxLength: 'Skriv maksimalt {maxLength} tegn (du skrev {length} tegn).',
    integer: 'Skriv et tal i dette felt. Decimal tal (f.eks. 1.25) er ikke tilladt.',
    numeric: 'Skriv kun tal i dette felt (i.e. "1" eller "1.1" eller "-1" eller "-1.1").',
    digits: 'Skriv kun tal og tegnsætning i dette felt (eksempel, et telefon nummer med bindestreg eller punktum er tilladt).',
    alpha: 'Skriv kun bogstaver (a-z) i dette felt. Mellemrum og andre tegn er ikke tilladt.',
    alphanum: 'Skriv kun bogstaver (a-z) eller tal (0-9) i dette felt. Mellemrum og andre tegn er ikke tilladt.',
    dateSuchAs: 'Skriv en gyldig dato som {date}',
    dateInFormatMDY: 'Skriv dato i formatet DD-MM-YYYY (f.eks. "31-12-1999")',
    email: 'Skriv en gyldig e-mail adresse. F.eks "fred@domain.com".',
    url: 'Skriv en gyldig URL adresse. F.eks "http://www.example.com".',
    currencyDollar: 'Skriv et gldigt beløb. F.eks Kr.100.00 .',
    oneRequired: 'Et eller flere af felterne i denne formular skal udfyldes.',
    errorPrefix: 'Fejl: ',
    warningPrefix: 'Advarsel: ',

    // Form.Validator.Extras
    noSpace: 'Der må ikke benyttes mellemrum i dette felt.',
    reqChkByNode: 'Foretag et valg.',
    requiredChk: 'Dette felt skal udfyldes.',
    reqChkByName: 'Vælg en {label}.',
    match: 'Dette felt skal matche {matchName} feltet',
    startDate: 'start dato',
    endDate: 'slut dato',
    currentDate: 'dags dato',
    afterDate: 'Datoen skal være større end eller lig med {label}.',
    beforeDate: 'Datoen skal være mindre end eller lig med {label}.',
    startMonth: 'Vælg en start måned',
    sameMonth: 'De valgte datoer skal være i samme måned - skift en af dem.'

});

/*
---

name: Locale.de-DE.Date

description: Date messages for German.

license: MIT-style license

authors:
  - Christoph Pojer
  - Frank Rossi
  - Ulrich Petri
  - Fabian Beiner

requires:
  - Locale

provides: [Locale.de-DE.Date]

...
*/

Locale.define('de-DE', 'Date', {

    months: ['Januar', 'Februar', 'März', 'April', 'Mai', 'Juni', 'Juli', 'August', 'September', 'Oktober', 'November', 'Dezember'],
    months_abbr: ['Jan', 'Feb', 'Mär', 'Apr', 'Mai', 'Jun', 'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Dez'],
    days: ['Sonntag', 'Montag', 'Dienstag', 'Mittwoch', 'Donnerstag', 'Freitag', 'Samstag'],
    days_abbr: ['So', 'Mo', 'Di', 'Mi', 'Do', 'Fr', 'Sa'],

    // Culture's date order: DD.MM.YYYY
    dateOrder: ['date', 'month', 'year'],
    shortDate: '%d.%m.%Y',
    shortTime: '%H:%M',
    AM: 'vormittags',
    PM: 'nachmittags',
    firstDayOfWeek: 1,

    // Date.Extras
    ordinal: '.',

    lessThanMinuteAgo: 'vor weniger als einer Minute',
    minuteAgo: 'vor einer Minute',
    minutesAgo: 'vor {delta} Minuten',
    hourAgo: 'vor einer Stunde',
    hoursAgo: 'vor {delta} Stunden',
    dayAgo: 'vor einem Tag',
    daysAgo: 'vor {delta} Tagen',
    weekAgo: 'vor einer Woche',
    weeksAgo: 'vor {delta} Wochen',
    monthAgo: 'vor einem Monat',
    monthsAgo: 'vor {delta} Monaten',
    yearAgo: 'vor einem Jahr',
    yearsAgo: 'vor {delta} Jahren',

    lessThanMinuteUntil: 'in weniger als einer Minute',
    minuteUntil: 'in einer Minute',
    minutesUntil: 'in {delta} Minuten',
    hourUntil: 'in ca. einer Stunde',
    hoursUntil: 'in ca. {delta} Stunden',
    dayUntil: 'in einem Tag',
    daysUntil: 'in {delta} Tagen',
    weekUntil: 'in einer Woche',
    weeksUntil: 'in {delta} Wochen',
    monthUntil: 'in einem Monat',
    monthsUntil: 'in {delta} Monaten',
    yearUntil: 'in einem Jahr',
    yearsUntil: 'in {delta} Jahren'

});

/*
---

name: Locale.de-CH.Date

description: Date messages for German (Switzerland).

license: MIT-style license

authors:
  - Michael van der Weg

requires:
  - Locale
  - Locale.de-DE.Date

provides: [Locale.de-CH.Date]

...
*/

Locale.define('de-CH').inherit('de-DE', 'Date');

/*
---

name: Locale.de-CH.Form.Validator

description: Form Validator messages for German (Switzerland).

license: MIT-style license

authors:
  - Michael van der Weg

requires:
  - Locale

provides: [Locale.de-CH.Form.Validator]

...
*/

Locale.define('de-CH', 'FormValidator', {

    required: 'Dieses Feld ist obligatorisch.',
    minLength: 'Geben Sie bitte mindestens {minLength} Zeichen ein (Sie haben {length} Zeichen eingegeben).',
    maxLength: 'Bitte geben Sie nicht mehr als {maxLength} Zeichen ein (Sie haben {length} Zeichen eingegeben).',
    integer: 'Geben Sie bitte eine ganze Zahl ein. Dezimalzahlen (z.B. 1.25) sind nicht erlaubt.',
    numeric: 'Geben Sie bitte nur Zahlenwerte in dieses Eingabefeld ein (z.B. &quot;1&quot;, &quot;1.1&quot;, &quot;-1&quot; oder &quot;-1.1&quot;).',
    digits: 'Benutzen Sie bitte nur Zahlen und Satzzeichen in diesem Eingabefeld (erlaubt ist z.B. eine Telefonnummer mit Bindestrichen und Punkten).',
    alpha: 'Benutzen Sie bitte nur Buchstaben (a-z) in diesem Feld. Leerzeichen und andere Zeichen sind nicht erlaubt.',
    alphanum: 'Benutzen Sie bitte nur Buchstaben (a-z) und Zahlen (0-9) in diesem Eingabefeld. Leerzeichen und andere Zeichen sind nicht erlaubt.',
    dateSuchAs: 'Geben Sie bitte ein g&uuml;ltiges Datum ein. Wie zum Beispiel {date}',
    dateInFormatMDY: 'Geben Sie bitte ein g&uuml;ltiges Datum ein. Wie zum Beispiel TT.MM.JJJJ (z.B. &quot;31.12.1999&quot;)',
    email: 'Geben Sie bitte eine g&uuml;ltige E-Mail Adresse ein. Wie zum Beispiel &quot;maria@bernasconi.ch&quot;.',
    url: 'Geben Sie bitte eine g&uuml;ltige URL ein. Wie zum Beispiel http://www.example.com.',
    currencyDollar: 'Geben Sie bitte einen g&uuml;ltigen Betrag in Schweizer Franken ein. Wie zum Beispiel 100.00 CHF .',
    oneRequired: 'Machen Sie f&uuml;r mindestens eines der Eingabefelder einen Eintrag.',
    errorPrefix: 'Fehler: ',
    warningPrefix: 'Warnung: ',

    // Form.Validator.Extras
    noSpace: 'In diesem Eingabefeld darf kein Leerzeichen sein.',
    reqChkByNode: 'Es wurden keine Elemente gew&auml;hlt.',
    requiredChk: 'Dieses Feld ist obligatorisch.',
    reqChkByName: 'Bitte w&auml;hlen Sie ein {label}.',
    match: 'Dieses Eingabefeld muss mit dem Feld {matchName} &uuml;bereinstimmen.',
    startDate: 'Das Anfangsdatum',
    endDate: 'Das Enddatum',
    currentDate: 'Das aktuelle Datum',
    afterDate: 'Das Datum sollte zur gleichen Zeit oder sp&auml;ter sein {label}.',
    beforeDate: 'Das Datum sollte zur gleichen Zeit oder fr&uuml;her sein {label}.',
    startMonth: 'W&auml;hlen Sie bitte einen Anfangsmonat',
    sameMonth: 'Diese zwei Datumsangaben m&uuml;ssen im selben Monat sein - Sie m&uuml;ssen eine von beiden ver&auml;ndern.',
    creditcard: 'Die eingegebene Kreditkartennummer ist ung&uuml;ltig. Bitte &uuml;berpr&uuml;fen Sie diese und versuchen Sie es erneut. {length} Zahlen eingegeben.'

});

/*
---
name: Locale.de-CH.Number
description: Number messages for Switzerland.
license: MIT-style license
authors:
  - Kim D. Jeker
requires:
  - Locale
  - Locale.CH.Number
provides: [Locale.de-CH.Number]
...
*/

Locale.define('de-CH').inherit('CH', 'Number');

/*
---

name: Locale.de-DE.Form.Validator

description: Form Validator messages for German.

license: MIT-style license

authors:
  - Frank Rossi
  - Ulrich Petri
  - Fabian Beiner

requires:
  - Locale

provides: [Locale.de-DE.Form.Validator]

...
*/

Locale.define('de-DE', 'FormValidator', {

    required: 'Dieses Eingabefeld muss ausgefüllt werden.',
    minLength: 'Geben Sie bitte mindestens {minLength} Zeichen ein (Sie haben nur {length} Zeichen eingegeben).',
    maxLength: 'Geben Sie bitte nicht mehr als {maxLength} Zeichen ein (Sie haben {length} Zeichen eingegeben).',
    integer: 'Geben Sie in diesem Eingabefeld bitte eine ganze Zahl ein. Dezimalzahlen (z.B. "1.25") sind nicht erlaubt.',
    numeric: 'Geben Sie in diesem Eingabefeld bitte nur Zahlenwerte (z.B. "1", "1.1", "-1" oder "-1.1") ein.',
    digits: 'Geben Sie in diesem Eingabefeld bitte nur Zahlen und Satzzeichen ein (z.B. eine Telefonnummer mit Bindestrichen und Punkten ist erlaubt).',
    alpha: 'Geben Sie in diesem Eingabefeld bitte nur Buchstaben (a-z) ein. Leerzeichen und andere Zeichen sind nicht erlaubt.',
    alphanum: 'Geben Sie in diesem Eingabefeld bitte nur Buchstaben (a-z) und Zahlen (0-9) ein. Leerzeichen oder andere Zeichen sind nicht erlaubt.',
    dateSuchAs: 'Geben Sie bitte ein gültiges Datum ein (z.B. "{date}").',
    dateInFormatMDY: 'Geben Sie bitte ein gültiges Datum im Format TT.MM.JJJJ ein (z.B. "31.12.1999").',
    email: 'Geben Sie bitte eine gültige E-Mail-Adresse ein (z.B. "max@mustermann.de").',
    url: 'Geben Sie bitte eine gültige URL ein (z.B. "http://www.example.com").',
    currencyDollar: 'Geben Sie bitte einen gültigen Betrag in EURO ein (z.B. 100.00€).',
    oneRequired: 'Bitte füllen Sie mindestens ein Eingabefeld aus.',
    errorPrefix: 'Fehler: ',
    warningPrefix: 'Warnung: ',

    // Form.Validator.Extras
    noSpace: 'Es darf kein Leerzeichen in diesem Eingabefeld sein.',
    reqChkByNode: 'Es wurden keine Elemente gewählt.',
    requiredChk: 'Dieses Feld muss ausgefüllt werden.',
    reqChkByName: 'Bitte wählen Sie ein {label}.',
    match: 'Dieses Eingabefeld muss mit dem {matchName} Eingabefeld übereinstimmen.',
    startDate: 'Das Anfangsdatum',
    endDate: 'Das Enddatum',
    currentDate: 'Das aktuelle Datum',
    afterDate: 'Das Datum sollte zur gleichen Zeit oder später sein als {label}.',
    beforeDate: 'Das Datum sollte zur gleichen Zeit oder früher sein als {label}.',
    startMonth: 'Wählen Sie bitte einen Anfangsmonat',
    sameMonth: 'Diese zwei Datumsangaben müssen im selben Monat sein - Sie müssen eines von beiden verändern.',
    creditcard: 'Die eingegebene Kreditkartennummer ist ungültig. Bitte überprüfen Sie diese und versuchen Sie es erneut. {length} Zahlen eingegeben.'

});

/*
---

name: Locale.de-DE.Number

description: Number messages for German.

license: MIT-style license

authors:
  - Christoph Pojer

requires:
  - Locale
  - Locale.EU.Number

provides: [Locale.de-DE.Number]

...
*/

Locale.define('de-DE').inherit('EU', 'Number');

/*
---

name: Locale.el-GR.Date

description: Date messages for Greek language.

license: MIT-style license

authors:
  - Periklis Argiriadis

requires:
  - Locale

provides: [Locale.el-GR.Date]

...
*/

Locale.define('el-GR', 'Date', {

    months: ['Ιανουάριος', 'Φεβρουάριος', 'Μάρτιος', 'Απρίλιος', 'Μάιος', 'Ιούνιος', 'Ιούλιος', 'Αύγουστος', 'Σεπτέμβριος', 'Οκτώβριος', 'Νοέμβριος', 'Δεκέμβριος'],
    months_abbr: ['Ιαν', 'Φεβ', 'Μαρ', 'Απρ', 'Μάι', 'Ιουν', 'Ιουλ', 'Αυγ', 'Σεπ', 'Οκτ', 'Νοε', 'Δεκ'],
    days: ['Κυριακή', 'Δευτέρα', 'Τρίτη', 'Τετάρτη', 'Πέμπτη', 'Παρασκευή', 'Σάββατο'],
    days_abbr: ['Κυρ', 'Δευ', 'Τρι', 'Τετ', 'Πεμ', 'Παρ', 'Σαβ'],

    // Culture's date order: DD/MM/YYYY
    dateOrder: ['date', 'month', 'year'],
    shortDate: '%d/%m/%Y',
    shortTime: '%I:%M%p',
    AM: 'πμ',
    PM: 'μμ',
    firstDayOfWeek: 1,

    // Date.Extras
    ordinal: function(dayOfMonth){
        // 1st, 2nd, 3rd, etc.
        return (dayOfMonth > 3 && dayOfMonth < 21) ? 'ος' : ['ος'][Math.min(dayOfMonth % 10, 4)];
    },

    lessThanMinuteAgo: 'λιγότερο από ένα λεπτό πριν',
    minuteAgo: 'περίπου ένα λεπτό πριν',
    minutesAgo: '{delta} λεπτά πριν',
    hourAgo: 'περίπου μια ώρα πριν',
    hoursAgo: 'περίπου {delta} ώρες πριν',
    dayAgo: '1 ημέρα πριν',
    daysAgo: '{delta} ημέρες πριν',
    weekAgo: '1 εβδομάδα πριν',
    weeksAgo: '{delta} εβδομάδες πριν',
    monthAgo: '1 μήνα πριν',
    monthsAgo: '{delta} μήνες πριν',
    yearAgo: '1 χρόνο πριν',
    yearsAgo: '{delta} χρόνια πριν',

    lessThanMinuteUntil: 'λιγότερο από λεπτό από τώρα',
    minuteUntil: 'περίπου ένα λεπτό από τώρα',
    minutesUntil: '{delta} λεπτά από τώρα',
    hourUntil: 'περίπου μια ώρα από τώρα',
    hoursUntil: 'περίπου {delta} ώρες από τώρα',
    dayUntil: '1 ημέρα από τώρα',
    daysUntil: '{delta} ημέρες από τώρα',
    weekUntil: '1 εβδομάδα από τώρα',
    weeksUntil: '{delta} εβδομάδες από τώρα',
    monthUntil: '1 μήνας από τώρα',
    monthsUntil: '{delta} μήνες από τώρα',
    yearUntil: '1 χρόνος από τώρα',
    yearsUntil: '{delta} χρόνια από τώρα'

});

/*
---

name: Locale.el-GR.Form.Validator

description: Form Validator messages for Greek language.

license: MIT-style license

authors:
  - Dimitris Tsironis

requires:
  - Locale

provides: [Locale.el-GR.Form.Validator]

...
*/

Locale.define('el-GR', 'FormValidator', {

    required: 'Αυτό το πεδίο είναι απαραίτητο.',
    length: 'Παρακαλούμε, εισάγετε {length} χαρακτήρες (έχετε ήδη εισάγει {elLength} χαρακτήρες).',
    minLength: 'Παρακαλούμε, εισάγετε τουλάχιστον {minLength} χαρακτήρες (έχετε ήδη εισάγε {length} χαρακτήρες).',
    maxlength: 'Παρακαλούμε, εισάγετε εώς {maxlength} χαρακτήρες (έχετε ήδη εισάγε {length} χαρακτήρες).',
    integer: 'Παρακαλούμε, εισάγετε έναν ακέραιο αριθμό σε αυτό το πεδίο. Οι αριθμοί με δεκαδικά ψηφία (π.χ. 1.25) δεν επιτρέπονται.',
    numeric: 'Παρακαλούμε, εισάγετε μόνο αριθμητικές τιμές σε αυτό το πεδίο (π.χ." 1 " ή " 1.1 " ή " -1 " ή " -1.1 " ).',
    digits: 'Παρακαλούμε, χρησιμοποιήστε μόνο αριθμούς και σημεία στίξης σε αυτόν τον τομέα (π.χ. επιτρέπεται αριθμός τηλεφώνου με παύλες ή τελείες).',
    alpha: 'Παρακαλούμε, χρησιμοποιήστε μόνο γράμματα (a-z) σε αυτό το πεδίο. Δεν επιτρέπονται κενά ή άλλοι χαρακτήρες.',
    alphanum: 'Παρακαλούμε, χρησιμοποιήστε μόνο γράμματα (a-z) ή αριθμούς (0-9) σε αυτόν τον τομέα. Δεν επιτρέπονται κενά ή άλλοι χαρακτήρες.',
    dateSuchAs: 'Παρακαλούμε, εισάγετε μια έγκυρη ημερομηνία, όπως {date}',
    dateInFormatMDY: 'Παρακαλώ εισάγετε μια έγκυρη ημερομηνία, όπως ΜΜ/ΗΗ/ΕΕΕΕ (π.χ. "12/31/1999").',
    email: 'Παρακαλούμε, εισάγετε μια έγκυρη διεύθυνση ηλεκτρονικού ταχυδρομείου (π.χ. "fred@domain.com").',
    url: 'Παρακαλούμε, εισάγετε μια έγκυρη URL διεύθυνση, όπως http://www.example.com',
    currencyDollar: 'Παρακαλούμε, εισάγετε ένα έγκυρο ποσό σε δολλάρια (π.χ. $100.00).',
    oneRequired: 'Παρακαλούμε, εισάγετε κάτι για τουλάχιστον ένα από αυτά τα πεδία.',
    errorPrefix: 'Σφάλμα: ',
    warningPrefix: 'Προσοχή: ',

    // Form.Validator.Extras
    noSpace: 'Δεν επιτρέπονται τα κενά σε αυτό το πεδίο.',
    reqChkByNode: 'Δεν έχει επιλεγεί κάποιο αντικείμενο',
    requiredChk: 'Αυτό το πεδίο είναι απαραίτητο.',
    reqChkByName: 'Παρακαλούμε, επιλέξτε μια ετικέτα {label}.',
    match: 'Αυτό το πεδίο πρέπει να ταιριάζει με το πεδίο {matchName}.',
    startDate: 'η ημερομηνία έναρξης',
    endDate: 'η ημερομηνία λήξης',
    currentDate: 'η τρέχουσα ημερομηνία',
    afterDate: 'Η ημερομηνία πρέπει να είναι η ίδια ή μετά από την {label}.',
    beforeDate: 'Η ημερομηνία πρέπει να είναι η ίδια ή πριν από την {label}.',
    startMonth: 'Παρακαλώ επιλέξτε ένα μήνα αρχής.',
    sameMonth: 'Αυτές οι δύο ημερομηνίες πρέπει να έχουν τον ίδιο μήνα - θα πρέπει να αλλάξετε ή το ένα ή το άλλο',
    creditcard: 'Ο αριθμός της πιστωτικής κάρτας δεν είναι έγκυρος. Παρακαλούμε ελέγξτε τον αριθμό και δοκιμάστε ξανά. {length} μήκος ψηφίων.'

});

/*
---

name: Locale.en-GB.Date

description: Date messages for British English.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Locale
  - Locale.en-US.Date

provides: [Locale.en-GB.Date]

...
*/

Locale.define('en-GB', 'Date', {

    // Culture's date order: DD/MM/YYYY
    dateOrder: ['date', 'month', 'year'],
    shortDate: '%d/%m/%Y',
    shortTime: '%H:%M'

}).inherit('en-US', 'Date');

/*
---

name: Locale.en-US.Number

description: Number messages for US English.

license: MIT-style license

authors:
  - Arian Stolwijk

requires:
  - Locale

provides: [Locale.en-US.Number]

...
*/

Locale.define('en-US', 'Number', {

    decimal: '.',
    group: ',',

    /* 	Commented properties are the defaults for Number.format
	decimals: 0,
	precision: 0,
	scientific: null,

	prefix: null,
	suffic: null,

	// Negative/Currency/percentage will mixin Number
	negative: {
		prefix: '-'
	},*/

    currency: {
//		decimals: 2,
        prefix: '$ '
    }/*,

	percentage: {
		decimals: 2,
		suffix: '%'
	}*/

});



/*
---

name: Locale.es-ES.Date

description: Date messages for Spanish.

license: MIT-style license

authors:
  - Ãlfons Sanchez

requires:
  - Locale

provides: [Locale.es-ES.Date]

...
*/

Locale.define('es-ES', 'Date', {

    months: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
    months_abbr: ['ene', 'feb', 'mar', 'abr', 'may', 'jun', 'jul', 'ago', 'sep', 'oct', 'nov', 'dic'],
    days: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'],
    days_abbr: ['dom', 'lun', 'mar', 'mié', 'juv', 'vie', 'sáb'],

    // Culture's date order: DD/MM/YYYY
    dateOrder: ['date', 'month', 'year'],
    shortDate: '%d/%m/%Y',
    shortTime: '%H:%M',
    AM: 'AM',
    PM: 'PM',
    firstDayOfWeek: 1,

    // Date.Extras
    ordinal: '',

    lessThanMinuteAgo: 'hace menos de un minuto',
    minuteAgo: 'hace un minuto',
    minutesAgo: 'hace {delta} minutos',
    hourAgo: 'hace una hora',
    hoursAgo: 'hace unas {delta} horas',
    dayAgo: 'hace un día',
    daysAgo: 'hace {delta} días',
    weekAgo: 'hace una semana',
    weeksAgo: 'hace unas {delta} semanas',
    monthAgo: 'hace un mes',
    monthsAgo: 'hace {delta} meses',
    yearAgo: 'hace un año',
    yearsAgo: 'hace {delta} años',

    lessThanMinuteUntil: 'menos de un minuto desde ahora',
    minuteUntil: 'un minuto desde ahora',
    minutesUntil: '{delta} minutos desde ahora',
    hourUntil: 'una hora desde ahora',
    hoursUntil: 'unas {delta} horas desde ahora',
    dayUntil: 'un día desde ahora',
    daysUntil: '{delta} días desde ahora',
    weekUntil: 'una semana desde ahora',
    weeksUntil: 'unas {delta} semanas desde ahora',
    monthUntil: 'un mes desde ahora',
    monthsUntil: '{delta} meses desde ahora',
    yearUntil: 'un año desde ahora',
    yearsUntil: '{delta} años desde ahora'

});

/*
---

name: Locale.es-AR.Date

description: Date messages for Spanish (Argentina).

license: MIT-style license

authors:
  - Ãlfons Sanchez
  - Diego Massanti

requires:
  - Locale
  - Locale.es-ES.Date

provides: [Locale.es-AR.Date]

...
*/

Locale.define('es-AR').inherit('es-ES', 'Date');

/*
---

name: Locale.es-AR.Form.Validator

description: Form Validator messages for Spanish (Argentina).

license: MIT-style license

authors:
  - Diego Massanti

requires:
  - Locale

provides: [Locale.es-AR.Form.Validator]

...
*/

Locale.define('es-AR', 'FormValidator', {

    required: 'Este campo es obligatorio.',
    minLength: 'Por favor ingrese al menos {minLength} caracteres (ha ingresado {length} caracteres).',
    maxLength: 'Por favor no ingrese más de {maxLength} caracteres (ha ingresado {length} caracteres).',
    integer: 'Por favor ingrese un número entero en este campo. Números con decimales (p.e. 1,25) no se permiten.',
    numeric: 'Por favor ingrese solo valores numéricos en este campo (p.e. "1" o "1,1" o "-1" o "-1,1").',
    digits: 'Por favor use sólo números y puntuación en este campo (por ejemplo, un número de teléfono con guiones y/o puntos no está permitido).',
    alpha: 'Por favor use sólo letras (a-z) en este campo. No se permiten espacios ni otros caracteres.',
    alphanum: 'Por favor, usa sólo letras (a-z) o números (0-9) en este campo. No se permiten espacios u otros caracteres.',
    dateSuchAs: 'Por favor ingrese una fecha válida como {date}',
    dateInFormatMDY: 'Por favor ingrese una fecha válida, utulizando el formato DD/MM/YYYY (p.e. "31/12/1999")',
    email: 'Por favor, ingrese una dirección de e-mail válida. Por ejemplo, "fred@dominio.com".',
    url: 'Por favor ingrese una URL válida como http://www.example.com.',
    currencyDollar: 'Por favor ingrese una cantidad válida de pesos. Por ejemplo $100,00 .',
    oneRequired: 'Por favor ingrese algo para por lo menos una de estas entradas.',
    errorPrefix: 'Error: ',
    warningPrefix: 'Advertencia: ',

    // Form.Validator.Extras
    noSpace: 'No se permiten espacios en este campo.',
    reqChkByNode: 'No hay elementos seleccionados.',
    requiredChk: 'Este campo es obligatorio.',
    reqChkByName: 'Por favor selecciona una {label}.',
    match: 'Este campo necesita coincidir con el campo {matchName}',
    startDate: 'la fecha de inicio',
    endDate: 'la fecha de fin',
    currentDate: 'la fecha actual',
    afterDate: 'La fecha debe ser igual o posterior a {label}.',
    beforeDate: 'La fecha debe ser igual o anterior a {label}.',
    startMonth: 'Por favor selecciona un mes de origen',
    sameMonth: 'Estas dos fechas deben estar en el mismo mes - debes cambiar una u otra.'

});

/*
---

name: Locale.es-AR.Number

description: Number messages for es Argentina.

license: MIT-style license

authors:
  - Oscar Kuchuk

requires:
  - Locale

provides: [Locale.es-AR.Number]

...
*/

Locale.define('es-AR', 'Number', {

    decimal: ',',
    group: '.',

    /* 	Commented properties are the defaults for Number.format
	decimals: 0,
	precision: 0,
	scientific: null,

	prefix: null,
	suffic: null,

	// Negative/Currency/percentage will mixin Number
	negative: {
		prefix: '-'
	},*/

    currency: {
        decimals: 2,
        prefix: '$ '
    }/*,

	percentage: {
		decimals: 2,
		suffix: '%'
	}*/

});


/*
---

name: Locale.es-ES.Form.Validator

description: Form Validator messages for Spanish.

license: MIT-style license

authors:
  - Ãlfons Sanchez

requires:
  - Locale

provides: [Locale.es-ES.Form.Validator]

...
*/

Locale.define('es-ES', 'FormValidator', {

    required: 'Este campo es obligatorio.',
    minLength: 'Por favor introduce al menos {minLength} caracteres (has introducido {length} caracteres).',
    maxLength: 'Por favor introduce no m&aacute;s de {maxLength} caracteres (has introducido {length} caracteres).',
    integer: 'Por favor introduce un n&uacute;mero entero en este campo. N&uacute;meros con decimales (p.e. 1,25) no se permiten.',
    numeric: 'Por favor introduce solo valores num&eacute;ricos en este campo (p.e. "1" o "1,1" o "-1" o "-1,1").',
    digits: 'Por favor usa solo n&uacute;meros y puntuaci&oacute;n en este campo (por ejemplo, un n&uacute;mero de tel&eacute;fono con guiones y puntos no esta permitido).',
    alpha: 'Por favor usa letras solo (a-z) en este campo. No se admiten espacios ni otros caracteres.',
    alphanum: 'Por favor, usa solo letras (a-z) o n&uacute;meros (0-9) en este campo. No se admiten espacios ni otros caracteres.',
    dateSuchAs: 'Por favor introduce una fecha v&aacute;lida como {date}',
    dateInFormatMDY: 'Por favor introduce una fecha v&aacute;lida como DD/MM/YYYY (p.e. "31/12/1999")',
    email: 'Por favor, introduce una direcci&oacute;n de email v&aacute;lida. Por ejemplo, "fred@domain.com".',
    url: 'Por favor introduce una URL v&aacute;lida como http://www.example.com.',
    currencyDollar: 'Por favor introduce una cantidad v&aacute;lida de €. Por ejemplo €100,00 .',
    oneRequired: 'Por favor introduce algo para por lo menos una de estas entradas.',
    errorPrefix: 'Error: ',
    warningPrefix: 'Aviso: ',

    // Form.Validator.Extras
    noSpace: 'No pueden haber espacios en esta entrada.',
    reqChkByNode: 'No hay elementos seleccionados.',
    requiredChk: 'Este campo es obligatorio.',
    reqChkByName: 'Por favor selecciona una {label}.',
    match: 'Este campo necesita coincidir con el campo {matchName}',
    startDate: 'la fecha de inicio',
    endDate: 'la fecha de fin',
    currentDate: 'la fecha actual',
    afterDate: 'La fecha debe ser igual o posterior a {label}.',
    beforeDate: 'La fecha debe ser igual o anterior a {label}.',
    startMonth: 'Por favor selecciona un mes de origen',
    sameMonth: 'Estas dos fechas deben estar en el mismo mes - debes cambiar una u otra.'

});

/*
---

name: Locale.es-VE.Date

description: Date messages for Spanish (Venezuela).

license: MIT-style license

authors:
  - Daniel Barreto

requires:
  - Locale
  - Locale.es-ES.Date

provides: [Locale.es-VE.Date]

...
*/

Locale.define('es-VE').inherit('es-ES', 'Date');

/*
---

name: Locale.es-VE.Form.Validator

description: Form Validator messages for Spanish (Venezuela).

license: MIT-style license

authors:
  - Daniel Barreto

requires:
  - Locale
  - Locale.es-ES.Form.Validator

provides: [Locale.es-VE.Form.Validator]

...
*/

Locale.define('es-VE', 'FormValidator', {

    digits: 'Por favor usa solo n&uacute;meros y puntuaci&oacute;n en este campo. Por ejemplo, un n&uacute;mero de tel&eacute;fono con guiones y puntos no esta permitido.',
    alpha: 'Por favor usa solo letras (a-z) en este campo. No se admiten espacios ni otros caracteres.',
    currencyDollar: 'Por favor introduce una cantidad v&aacute;lida de Bs. Por ejemplo Bs. 100,00 .',
    oneRequired: 'Por favor introduce un valor para por lo menos una de estas entradas.',

    // Form.Validator.Extras
    startDate: 'La fecha de inicio',
    endDate: 'La fecha de fin',
    currentDate: 'La fecha actual'

}).inherit('es-ES', 'FormValidator');

/*
---

name: Locale.es-VE.Number

description: Number messages for Spanish (Venezuela).

license: MIT-style license

authors:
  - Daniel Barreto

requires:
  - Locale

provides: [Locale.es-VE.Number]

...
*/

Locale.define('es-VE', 'Number', {

    decimal: ',',
    group: '.',
    /*
	decimals: 0,
	precision: 0,
*/
    // Negative/Currency/percentage will mixin Number
    negative: {
        prefix: '-'
    },

    currency: {
        decimals: 2,
        prefix: 'Bs. '
    },

    percentage: {
        decimals: 2,
        suffix: '%'
    }

});

/*
---

name: Locale.et-EE.Date

description: Date messages for Estonian.

license: MIT-style license

authors:
  - Kevin Valdek

requires:
  - Locale

provides: [Locale.et-EE.Date]

...
*/

Locale.define('et-EE', 'Date', {

    months: ['jaanuar', 'veebruar', 'märts', 'aprill', 'mai', 'juuni', 'juuli', 'august', 'september', 'oktoober', 'november', 'detsember'],
    months_abbr: ['jaan', 'veebr', 'märts', 'apr', 'mai', 'juuni', 'juuli', 'aug', 'sept', 'okt', 'nov', 'dets'],
    days: ['pühapäev', 'esmaspäev', 'teisipäev', 'kolmapäev', 'neljapäev', 'reede', 'laupäev'],
    days_abbr: ['pühap', 'esmasp', 'teisip', 'kolmap', 'neljap', 'reede', 'laup'],

    // Culture's date order: MM.DD.YYYY
    dateOrder: ['month', 'date', 'year'],
    shortDate: '%m.%d.%Y',
    shortTime: '%H:%M',
    AM: 'AM',
    PM: 'PM',
    firstDayOfWeek: 1,

    // Date.Extras
    ordinal: '',

    lessThanMinuteAgo: 'vähem kui minut aega tagasi',
    minuteAgo: 'umbes minut aega tagasi',
    minutesAgo: '{delta} minutit tagasi',
    hourAgo: 'umbes tund aega tagasi',
    hoursAgo: 'umbes {delta} tundi tagasi',
    dayAgo: '1 päev tagasi',
    daysAgo: '{delta} päeva tagasi',
    weekAgo: '1 nädal tagasi',
    weeksAgo: '{delta} nädalat tagasi',
    monthAgo: '1 kuu tagasi',
    monthsAgo: '{delta} kuud tagasi',
    yearAgo: '1 aasta tagasi',
    yearsAgo: '{delta} aastat tagasi',

    lessThanMinuteUntil: 'vähem kui minuti aja pärast',
    minuteUntil: 'umbes minuti aja pärast',
    minutesUntil: '{delta} minuti pärast',
    hourUntil: 'umbes tunni aja pärast',
    hoursUntil: 'umbes {delta} tunni pärast',
    dayUntil: '1 päeva pärast',
    daysUntil: '{delta} päeva pärast',
    weekUntil: '1 nädala pärast',
    weeksUntil: '{delta} nädala pärast',
    monthUntil: '1 kuu pärast',
    monthsUntil: '{delta} kuu pärast',
    yearUntil: '1 aasta pärast',
    yearsUntil: '{delta} aasta pärast'

});

/*
---

name: Locale.et-EE.Form.Validator

description: Form Validator messages for Estonian.

license: MIT-style license

authors:
  - Kevin Valdek

requires:
  - Locale

provides: [Locale.et-EE.Form.Validator]

...
*/

Locale.define('et-EE', 'FormValidator', {

    required: 'Väli peab olema täidetud.',
    minLength: 'Palun sisestage vähemalt {minLength} tähte (te sisestasite {length} tähte).',
    maxLength: 'Palun ärge sisestage rohkem kui {maxLength} tähte (te sisestasite {length} tähte).',
    integer: 'Palun sisestage väljale täisarv. Kümnendarvud (näiteks 1.25) ei ole lubatud.',
    numeric: 'Palun sisestage ainult numbreid väljale (näiteks "1", "1.1", "-1" või "-1.1").',
    digits: 'Palun kasutage ainult numbreid ja kirjavahemärke (telefoninumbri sisestamisel on lubatud kasutada kriipse ja punkte).',
    alpha: 'Palun kasutage ainult tähti (a-z). Tühikud ja teised sümbolid on keelatud.',
    alphanum: 'Palun kasutage ainult tähti (a-z) või numbreid (0-9). Tühikud ja teised sümbolid on keelatud.',
    dateSuchAs: 'Palun sisestage kehtiv kuupäev kujul {date}',
    dateInFormatMDY: 'Palun sisestage kehtiv kuupäev kujul MM.DD.YYYY (näiteks: "12.31.1999").',
    email: 'Palun sisestage kehtiv e-maili aadress (näiteks: "fred@domain.com").',
    url: 'Palun sisestage kehtiv URL (näiteks: http://www.example.com).',
    currencyDollar: 'Palun sisestage kehtiv $ summa (näiteks: $100.00).',
    oneRequired: 'Palun sisestage midagi vähemalt ühele antud väljadest.',
    errorPrefix: 'Viga: ',
    warningPrefix: 'Hoiatus: ',

    // Form.Validator.Extras
    noSpace: 'Väli ei tohi sisaldada tühikuid.',
    reqChkByNode: 'Ükski väljadest pole valitud.',
    requiredChk: 'Välja täitmine on vajalik.',
    reqChkByName: 'Palun valige üks {label}.',
    match: 'Väli peab sobima {matchName} väljaga',
    startDate: 'algkuupäev',
    endDate: 'lõppkuupäev',
    currentDate: 'praegune kuupäev',
    afterDate: 'Kuupäev peab olema võrdne või pärast {label}.',
    beforeDate: 'Kuupäev peab olema võrdne või enne {label}.',
    startMonth: 'Palun valige algkuupäev.',
    sameMonth: 'Antud kaks kuupäeva peavad olema samas kuus - peate muutma ühte kuupäeva.'

});

/*
---

name: Locale.fa.Date

description: Date messages for Persian.

license: MIT-style license

authors:
  - Amir Hossein Hodjaty Pour

requires:
  - Locale

provides: [Locale.fa.Date]

...
*/

Locale.define('fa', 'Date', {

    months: ['ژانویه', 'فوریه', 'مارس', 'آپریل', 'مه', 'ژوئن', 'ژوئیه', 'آگوست', 'سپتامبر', 'اکتبر', 'نوامبر', 'دسامبر'],
    months_abbr: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
    days: ['یکشنبه', 'دوشنبه', 'سه شنبه', 'چهارشنبه', 'پنجشنبه', 'جمعه', 'شنبه'],
    days_abbr: ['ي', 'د', 'س', 'چ', 'پ', 'ج', 'ش'],

    // Culture's date order: MM/DD/YYYY
    dateOrder: ['month', 'date', 'year'],
    shortDate: '%m/%d/%Y',
    shortTime: '%I:%M%p',
    AM: 'ق.ظ',
    PM: 'ب.ظ',

    // Date.Extras
    ordinal: 'ام',

    lessThanMinuteAgo: 'کمتر از یک دقیقه پیش',
    minuteAgo: 'حدود یک دقیقه پیش',
    minutesAgo: '{delta} دقیقه پیش',
    hourAgo: 'حدود یک ساعت پیش',
    hoursAgo: 'حدود {delta} ساعت پیش',
    dayAgo: '1 روز پیش',
    daysAgo: '{delta} روز پیش',
    weekAgo: '1 هفته پیش',
    weeksAgo: '{delta} هفته پیش',
    monthAgo: '1 ماه پیش',
    monthsAgo: '{delta} ماه پیش',
    yearAgo: '1 سال پیش',
    yearsAgo: '{delta} سال پیش',

    lessThanMinuteUntil: 'کمتر از یک دقیقه از حالا',
    minuteUntil: 'حدود یک دقیقه از حالا',
    minutesUntil: '{delta} دقیقه از حالا',
    hourUntil: 'حدود یک ساعت از حالا',
    hoursUntil: 'حدود {delta} ساعت از حالا',
    dayUntil: '1 روز از حالا',
    daysUntil: '{delta} روز از حالا',
    weekUntil: '1 هفته از حالا',
    weeksUntil: '{delta} هفته از حالا',
    monthUntil: '1 ماه از حالا',
    monthsUntil: '{delta} ماه از حالا',
    yearUntil: '1 سال از حالا',
    yearsUntil: '{delta} سال از حالا'

});

/*
---

name: Locale.fa.Form.Validator

description: Form Validator messages for Persian.

license: MIT-style license

authors:
  - Amir Hossein Hodjaty Pour

requires:
  - Locale

provides: [Locale.fa.Form.Validator]

...
*/

Locale.define('fa', 'FormValidator', {

    required: 'این فیلد الزامی است.',
    minLength: 'شما باید حداقل {minLength} حرف وارد کنید ({length} حرف وارد کرده اید).',
    maxLength: 'لطفا حداکثر {maxLength} حرف وارد کنید (شما {length} حرف وارد کرده اید).',
    integer: 'لطفا از عدد صحیح استفاده کنید. اعداد اعشاری (مانند 1.25) مجاز نیستند.',
    numeric: 'لطفا فقط داده عددی وارد کنید (مانند "1" یا "1.1" یا "1-" یا "1.1-").',
    digits: 'لطفا فقط از اعداد و علامتها در این فیلد استفاده کنید (برای مثال شماره تلفن با خط تیره و نقطه قابل قبول است).',
    alpha: 'لطفا فقط از حروف الفباء برای این بخش استفاده کنید. کاراکترهای دیگر و فاصله مجاز نیستند.',
    alphanum: 'لطفا فقط از حروف الفباء و اعداد در این بخش استفاده کنید. کاراکترهای دیگر و فاصله مجاز نیستند.',
    dateSuchAs: 'لطفا یک تاریخ معتبر مانند {date} وارد کنید.',
    dateInFormatMDY: 'لطفا یک تاریخ معتبر به شکل MM/DD/YYYY وارد کنید (مانند "12/31/1999").',
    email: 'لطفا یک آدرس ایمیل معتبر وارد کنید. برای مثال "fred@domain.com".',
    url: 'لطفا یک URL معتبر مانند http://www.example.com وارد کنید.',
    currencyDollar: 'لطفا یک محدوده معتبر برای این بخش وارد کنید مانند 100.00$ .',
    oneRequired: 'لطفا حداقل یکی از فیلدها را پر کنید.',
    errorPrefix: 'خطا: ',
    warningPrefix: 'هشدار: ',

    // Form.Validator.Extras
    noSpace: 'استفاده از فاصله در این بخش مجاز نیست.',
    reqChkByNode: 'موردی انتخاب نشده است.',
    requiredChk: 'این فیلد الزامی است.',
    reqChkByName: 'لطفا یک {label} را انتخاب کنید.',
    match: 'این فیلد باید با فیلد {matchName} مطابقت داشته باشد.',
    startDate: 'تاریخ شروع',
    endDate: 'تاریخ پایان',
    currentDate: 'تاریخ کنونی',
    afterDate: 'تاریخ میبایست برابر یا بعد از {label} باشد',
    beforeDate: 'تاریخ میبایست برابر یا قبل از {label} باشد',
    startMonth: 'لطفا ماه شروع را انتخاب کنید',
    sameMonth: 'این دو تاریخ باید در یک ماه باشند - شما باید یکی یا هر دو را تغییر دهید.',
    creditcard: 'شماره کارت اعتباری که وارد کرده اید معتبر نیست. لطفا شماره را بررسی کنید و مجددا تلاش کنید. {length} رقم وارد شده است.'

});

/*
---

name: Locale.fi-FI.Date

description: Date messages for Finnish.

license: MIT-style license

authors:
  - ksel

requires:
  - Locale

provides: [Locale.fi-FI.Date]

...
*/

Locale.define('fi-FI', 'Date', {

    // NOTE: months and days are not capitalized in finnish
    months: ['tammikuu', 'helmikuu', 'maaliskuu', 'huhtikuu', 'toukokuu', 'kesäkuu', 'heinäkuu', 'elokuu', 'syyskuu', 'lokakuu', 'marraskuu', 'joulukuu'],

    // these abbreviations are really not much used in finnish because they obviously won't abbreviate very much. ;)
    // NOTE: sometimes one can see forms such as "tammi", "helmi", etc. but that is not proper finnish.
    months_abbr: ['tammik.', 'helmik.', 'maalisk.', 'huhtik.', 'toukok.', 'kesäk.', 'heinäk.', 'elok.', 'syysk.', 'lokak.', 'marrask.', 'jouluk.'],

    days: ['sunnuntai', 'maanantai', 'tiistai', 'keskiviikko', 'torstai', 'perjantai', 'lauantai'],
    days_abbr: ['su', 'ma', 'ti', 'ke', 'to', 'pe', 'la'],

    // Culture's date order: DD/MM/YYYY
    dateOrder: ['date', 'month', 'year'],
    shortDate: '%d.%m.%Y',
    shortTime: '%H:%M',
    AM: 'AM',
    PM: 'PM',
    firstDayOfWeek: 1,

    // Date.Extras
    ordinal: '.',

    lessThanMinuteAgo: 'vajaa minuutti sitten',
    minuteAgo: 'noin minuutti sitten',
    minutesAgo: '{delta} minuuttia sitten',
    hourAgo: 'noin tunti sitten',
    hoursAgo: 'noin {delta} tuntia sitten',
    dayAgo: 'päivä sitten',
    daysAgo: '{delta} päivää sitten',
    weekAgo: 'viikko sitten',
    weeksAgo: '{delta} viikkoa sitten',
    monthAgo: 'kuukausi sitten',
    monthsAgo: '{delta} kuukautta sitten',
    yearAgo: 'vuosi sitten',
    yearsAgo: '{delta} vuotta sitten',

    lessThanMinuteUntil: 'vajaan minuutin kuluttua',
    minuteUntil: 'noin minuutin kuluttua',
    minutesUntil: '{delta} minuutin kuluttua',
    hourUntil: 'noin tunnin kuluttua',
    hoursUntil: 'noin {delta} tunnin kuluttua',
    dayUntil: 'päivän kuluttua',
    daysUntil: '{delta} päivän kuluttua',
    weekUntil: 'viikon kuluttua',
    weeksUntil: '{delta} viikon kuluttua',
    monthUntil: 'kuukauden kuluttua',
    monthsUntil: '{delta} kuukauden kuluttua',
    yearUntil: 'vuoden kuluttua',
    yearsUntil: '{delta} vuoden kuluttua'

});

/*
---

name: Locale.fi-FI.Form.Validator

description: Form Validator messages for Finnish.

license: MIT-style license

authors:
  - ksel

requires:
  - Locale

provides: [Locale.fi-FI.Form.Validator]

...
*/

Locale.define('fi-FI', 'FormValidator', {

    required: 'Tämä kenttä on pakollinen.',
    minLength: 'Ole hyvä ja anna vähintään {minLength} merkkiä (annoit {length} merkkiä).',
    maxLength: 'Älä anna enempää kuin {maxLength} merkkiä (annoit {length} merkkiä).',
    integer: 'Ole hyvä ja anna kokonaisluku. Luvut, joissa on desimaaleja (esim. 1.25) eivät ole sallittuja.',
    numeric: 'Anna tähän kenttään lukuarvo (kuten "1" tai "1.1" tai "-1" tai "-1.1").',
    digits: 'Käytä pelkästään numeroita ja välimerkkejä tässä kentässä (syötteet, kuten esim. puhelinnumero, jossa on väliviivoja, pilkkuja tai pisteitä, kelpaa).',
    alpha: 'Anna tähän kenttään vain kirjaimia (a-z). Välilyönnit tai muut merkit eivät ole sallittuja.',
    alphanum: 'Anna tähän kenttään vain kirjaimia (a-z) tai numeroita (0-9). Välilyönnit tai muut merkit eivät ole sallittuja.',
    dateSuchAs: 'Ole hyvä ja anna kelvollinen päivmäärä, kuten esimerkiksi {date}',
    dateInFormatMDY: 'Ole hyvä ja anna kelvollinen päivämäärä muodossa pp/kk/vvvv (kuten "12/31/1999")',
    email: 'Ole hyvä ja anna kelvollinen sähköpostiosoite (kuten esimerkiksi "matti@meikalainen.com").',
    url: 'Ole hyvä ja anna kelvollinen URL, kuten esimerkiksi http://www.example.com.',
    currencyDollar: 'Ole hyvä ja anna kelvollinen eurosumma (kuten esimerkiksi 100,00 EUR) .',
    oneRequired: 'Ole hyvä ja syötä jotakin ainakin johonkin näistä kentistä.',
    errorPrefix: 'Virhe: ',
    warningPrefix: 'Varoitus: ',

    // Form.Validator.Extras
    noSpace: 'Tässä syötteessä ei voi olla välilyöntejä',
    reqChkByNode: 'Ei valintoja.',
    requiredChk: 'Tämä kenttä on pakollinen.',
    reqChkByName: 'Ole hyvä ja valitse {label}.',
    match: 'Tämän kentän tulee vastata kenttää {matchName}',
    startDate: 'alkupäivämäärä',
    endDate: 'loppupäivämäärä',
    currentDate: 'nykyinen päivämäärä',
    afterDate: 'Päivämäärän tulisi olla sama tai myöhäisempi ajankohta kuin {label}.',
    beforeDate: 'Päivämäärän tulisi olla sama tai aikaisempi ajankohta kuin {label}.',
    startMonth: 'Ole hyvä ja valitse aloituskuukausi',
    sameMonth: 'Näiden kahden päivämäärän tulee olla saman kuun sisällä -- sinun pitää muuttaa jompaa kumpaa.',
    creditcard: 'Annettu luottokortin numero ei kelpaa. Ole hyvä ja tarkista numero sekä yritä uudelleen. {length} numeroa syötetty.'

});

/*
---

name: Locale.fi-FI.Number

description: Finnish number messages

license: MIT-style license

authors:
  - ksel

requires:
  - Locale
  - Locale.EU.Number

provides: [Locale.fi-FI.Number]

...
*/

Locale.define('fi-FI', 'Number', {

    group: ' ' // grouped by space

}).inherit('EU', 'Number');

/*
---

name: Locale.fr-FR.Date

description: Date messages for French.

license: MIT-style license

authors:
  - Nicolas Sorosac
  - Antoine Abt

requires:
  - Locale

provides: [Locale.fr-FR.Date]

...
*/

Locale.define('fr-FR', 'Date', {

    months: ['Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin', 'Juillet', 'Août', 'Septembre', 'Octobre', 'Novembre', 'Décembre'],
    months_abbr: ['janv.', 'févr.', 'mars', 'avr.', 'mai', 'juin', 'juil.', 'août', 'sept.', 'oct.', 'nov.', 'déc.'],
    days: ['Dimanche', 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'],
    days_abbr: ['dim.', 'lun.', 'mar.', 'mer.', 'jeu.', 'ven.', 'sam.'],

    // Culture's date order: DD/MM/YYYY
    dateOrder: ['date', 'month', 'year'],
    shortDate: '%d/%m/%Y',
    shortTime: '%H:%M',
    AM: 'AM',
    PM: 'PM',
    firstDayOfWeek: 1,

    // Date.Extras
    ordinal: function(dayOfMonth){
        return (dayOfMonth > 1) ? '' : 'er';
    },

    lessThanMinuteAgo: "il y a moins d'une minute",
    minuteAgo: 'il y a une minute',
    minutesAgo: 'il y a {delta} minutes',
    hourAgo: 'il y a une heure',
    hoursAgo: 'il y a {delta} heures',
    dayAgo: 'il y a un jour',
    daysAgo: 'il y a {delta} jours',
    weekAgo: 'il y a une semaine',
    weeksAgo: 'il y a {delta} semaines',
    monthAgo: 'il y a 1 mois',
    monthsAgo: 'il y a {delta} mois',
    yearthAgo: 'il y a 1 an',
    yearsAgo: 'il y a {delta} ans',

    lessThanMinuteUntil: "dans moins d'une minute",
    minuteUntil: 'dans une minute',
    minutesUntil: 'dans {delta} minutes',
    hourUntil: 'dans une heure',
    hoursUntil: 'dans {delta} heures',
    dayUntil: 'dans un jour',
    daysUntil: 'dans {delta} jours',
    weekUntil: 'dans 1 semaine',
    weeksUntil: 'dans {delta} semaines',
    monthUntil: 'dans 1 mois',
    monthsUntil: 'dans {delta} mois',
    yearUntil: 'dans 1 an',
    yearsUntil: 'dans {delta} ans'

});

/*
---

name: Locale.fr-FR.Form.Validator

description: Form Validator messages for French.

license: MIT-style license

authors:
  - Miquel Hudin
  - Nicolas Sorosac

requires:
  - Locale

provides: [Locale.fr-FR.Form.Validator]

...
*/

/*eslint mootools-whitespace:0*/

Locale.define('fr-FR', 'FormValidator', {

    required: 'Ce champ est obligatoire.',
    length: 'Veuillez saisir {length} caract&egrave;re(s) (vous avez saisi {elLength} caract&egrave;re(s)',
    minLength: 'Veuillez saisir un minimum de {minLength} caract&egrave;re(s) (vous avez saisi {length} caract&egrave;re(s)).',
    maxLength: 'Veuillez saisir un maximum de {maxLength} caract&egrave;re(s) (vous avez saisi {length} caract&egrave;re(s)).',
    integer: 'Veuillez saisir un nombre entier dans ce champ. Les nombres d&eacute;cimaux (ex : "1,25") ne sont pas autoris&eacute;s.',
    numeric: 'Veuillez saisir uniquement des chiffres dans ce champ (ex : "1" ou "1,1" ou "-1" ou "-1,1").',
    digits: "Veuillez saisir uniquement des chiffres et des signes de ponctuation dans ce champ (ex : un num&eacute;ro de t&eacute;l&eacute;phone avec des traits d'union est autoris&eacute;).",
    alpha: 'Veuillez saisir uniquement des lettres (a-z) dans ce champ. Les espaces ou autres caract&egrave;res ne sont pas autoris&eacute;s.',
    alphanum: 'Veuillez saisir uniquement des lettres (a-z) ou des chiffres (0-9) dans ce champ. Les espaces ou autres caract&egrave;res ne sont pas autoris&eacute;s.',
    dateSuchAs: 'Veuillez saisir une date correcte comme {date}',
    dateInFormatMDY: 'Veuillez saisir une date correcte, au format JJ/MM/AAAA (ex : "31/11/1999").',
    email: 'Veuillez saisir une adresse de courrier &eacute;lectronique. Par exemple "fred@domaine.com".',
    url: 'Veuillez saisir une URL, comme http://www.exemple.com.',
    currencyDollar: 'Veuillez saisir une quantit&eacute; correcte. Par exemple 100,00&euro;.',
    oneRequired: 'Veuillez s&eacute;lectionner au moins une de ces options.',
    errorPrefix: 'Erreur : ',
    warningPrefix: 'Attention : ',

    // Form.Validator.Extras
    noSpace: "Ce champ n'accepte pas les espaces.",
    reqChkByNode: "Aucun &eacute;l&eacute;ment n'est s&eacute;lectionn&eacute;.",
    requiredChk: 'Ce champ est obligatoire.',
    reqChkByName: 'Veuillez s&eacute;lectionner un(e) {label}.',
    match: 'Ce champ doit correspondre avec le champ {matchName}.',
    startDate: 'date de d&eacute;but',
    endDate: 'date de fin',
    currentDate: 'date actuelle',
    afterDate: 'La date doit &ecirc;tre identique ou post&eacute;rieure &agrave; {label}.',
    beforeDate: 'La date doit &ecirc;tre identique ou ant&eacute;rieure &agrave; {label}.',
    startMonth: 'Veuillez s&eacute;lectionner un mois de d&eacute;but.',
    sameMonth: 'Ces deux dates doivent &ecirc;tre dans le m&ecirc;me mois - vous devez en modifier une.',
    creditcard: 'Le num&eacute;ro de carte de cr&eacute;dit est invalide. Merci de v&eacute;rifier le num&eacute;ro et de r&eacute;essayer. Vous avez entr&eacute; {length} chiffre(s).'

});

/*
---

name: Locale.fr-FR.Number

description: Number messages for French.

license: MIT-style license

authors:
  - Arian Stolwijk
  - sv1l

requires:
  - Locale
  - Locale.EU.Number

provides: [Locale.fr-FR.Number]

...
*/

Locale.define('fr-FR', 'Number', {

    group: ' ' // In fr-FR localization, group character is a blank space

}).inherit('EU', 'Number');

/*
---

name: Locale.he-IL.Date

description: Date messages for Hebrew.

license: MIT-style license

authors:
  - Elad Ossadon

requires:
  - Locale

provides: [Locale.he-IL.Date]

...
*/

Locale.define('he-IL', 'Date', {

    months: ['ינואר', 'פברואר', 'מרץ', 'אפריל', 'מאי', 'יוני', 'יולי', 'אוגוסט', 'ספטמבר', 'אוקטובר', 'נובמבר', 'דצמבר'],
    months_abbr: ['ינואר', 'פברואר', 'מרץ', 'אפריל', 'מאי', 'יוני', 'יולי', 'אוגוסט', 'ספטמבר', 'אוקטובר', 'נובמבר', 'דצמבר'],
    days: ['ראשון', 'שני', 'שלישי', 'רביעי', 'חמישי', 'שישי', 'שבת'],
    days_abbr: ['ראשון', 'שני', 'שלישי', 'רביעי', 'חמישי', 'שישי', 'שבת'],

    // Culture's date order: MM/DD/YYYY
    dateOrder: ['date', 'month', 'year'],
    shortDate: '%d/%m/%Y',
    shortTime: '%H:%M',
    AM: 'AM',
    PM: 'PM',
    firstDayOfWeek: 0,

    // Date.Extras
    ordinal: '',

    lessThanMinuteAgo: 'לפני פחות מדקה',
    minuteAgo: 'לפני כדקה',
    minutesAgo: 'לפני {delta} דקות',
    hourAgo: 'לפני כשעה',
    hoursAgo: 'לפני {delta} שעות',
    dayAgo: 'לפני יום',
    daysAgo: 'לפני {delta} ימים',
    weekAgo: 'לפני שבוע',
    weeksAgo: 'לפני {delta} שבועות',
    monthAgo: 'לפני חודש',
    monthsAgo: 'לפני {delta} חודשים',
    yearAgo: 'לפני שנה',
    yearsAgo: 'לפני {delta} שנים',

    lessThanMinuteUntil: 'בעוד פחות מדקה',
    minuteUntil: 'בעוד כדקה',
    minutesUntil: 'בעוד {delta} דקות',
    hourUntil: 'בעוד כשעה',
    hoursUntil: 'בעוד {delta} שעות',
    dayUntil: 'בעוד יום',
    daysUntil: 'בעוד {delta} ימים',
    weekUntil: 'בעוד שבוע',
    weeksUntil: 'בעוד {delta} שבועות',
    monthUntil: 'בעוד חודש',
    monthsUntil: 'בעוד {delta} חודשים',
    yearUntil: 'בעוד שנה',
    yearsUntil: 'בעוד {delta} שנים'

});

/*
---

name: Locale.he-IL.Form.Validator

description: Form Validator messages for Hebrew.

license: MIT-style license

authors:
  - Elad Ossadon

requires:
  - Locale

provides: [Locale.he-IL.Form.Validator]

...
*/

Locale.define('he-IL', 'FormValidator', {

    required: 'נא למלא שדה זה.',
    minLength: 'נא להזין לפחות {minLength} תווים (הזנת {length} תווים).',
    maxLength: 'נא להזין עד {maxLength} תווים (הזנת {length} תווים).',
    integer: 'נא להזין מספר שלם לשדה זה. מספרים עשרוניים (כמו 1.25) אינם חוקיים.',
    numeric: 'נא להזין ערך מספרי בלבד בשדה זה (כמו "1", "1.1", "-1" או "-1.1").',
    digits: 'נא להזין רק ספרות וסימני הפרדה בשדה זה (למשל, מספר טלפון עם מקפים או נקודות הוא חוקי).',
    alpha: 'נא להזין רק אותיות באנגלית (a-z) בשדה זה. רווחים או תווים אחרים אינם חוקיים.',
    alphanum: 'נא להזין רק אותריות באנגלית (a-z) או ספרות (0-9) בשדה זה. אווחרים או תווים אחרים אינם חוקיים.',
    dateSuchAs: 'נא להזין תאריך חוקי, כמו {date}',
    dateInFormatMDY: 'נא להזין תאריך חוקי בפורמט MM/DD/YYYY (כמו "12/31/1999")',
    email: 'נא להזין כתובת אימייל חוקית. לדוגמה: "fred@domain.com".',
    url: 'נא להזין כתובת אתר חוקית, כמו http://www.example.com.',
    currencyDollar: 'נא להזין סכום דולרי חוקי. לדוגמה $100.00.',
    oneRequired: 'נא לבחור לפחות בשדה אחד.',
    errorPrefix: 'שגיאה: ',
    warningPrefix: 'אזהרה: ',

    // Form.Validator.Extras
    noSpace: 'אין להזין רווחים בשדה זה.',
    reqChkByNode: 'נא לבחור אחת מהאפשרויות.',
    requiredChk: 'שדה זה נדרש.',
    reqChkByName: 'נא לבחור {label}.',
    match: 'שדה זה צריך להתאים לשדה {matchName}',
    startDate: 'תאריך ההתחלה',
    endDate: 'תאריך הסיום',
    currentDate: 'התאריך הנוכחי',
    afterDate: 'התאריך צריך להיות זהה או אחרי {label}.',
    beforeDate: 'התאריך צריך להיות זהה או לפני {label}.',
    startMonth: 'נא לבחור חודש התחלה',
    sameMonth: 'שני תאריכים אלה צריכים להיות באותו חודש - נא לשנות אחד התאריכים.',
    creditcard: 'מספר כרטיס האשראי שהוזן אינו חוקי. נא לבדוק שנית. הוזנו {length} ספרות.'

});

/*
---

name: Locale.he-IL.Number

description: Number messages for Hebrew.

license: MIT-style license

authors:
  - Elad Ossadon

requires:
  - Locale

provides: [Locale.he-IL.Number]

...
*/

Locale.define('he-IL', 'Number', {

    decimal: '.',
    group: ',',

    currency: {
        suffix: ' ₪'
    }

});

/*
---

name: Locale.hu-HU.Date

description: Date messages for Hungarian.

license: MIT-style license

authors:
  - Zsolt Szegheő

requires:
  - Locale

provides: [Locale.hu-HU.Date]

...
*/

Locale.define('hu-HU', 'Date', {

    months: ['Január', 'Február', 'Március', 'Április', 'Május', 'Június', 'Július', 'Augusztus', 'Szeptember', 'Október', 'November', 'December'],
    months_abbr: ['jan.', 'febr.', 'márc.', 'ápr.', 'máj.', 'jún.', 'júl.', 'aug.', 'szept.', 'okt.', 'nov.', 'dec.'],
    days: ['Vasárnap', 'Hétfő', 'Kedd', 'Szerda', 'Csütörtök', 'Péntek', 'Szombat'],
    days_abbr: ['V', 'H', 'K', 'Sze', 'Cs', 'P', 'Szo'],

    // Culture's date order: YYYY.MM.DD.
    dateOrder: ['year', 'month', 'date'],
    shortDate: '%Y.%m.%d.',
    shortTime: '%I:%M',
    AM: 'de.',
    PM: 'du.',
    firstDayOfWeek: 1,

    // Date.Extras
    ordinal: '.',

    lessThanMinuteAgo: 'alig egy perce',
    minuteAgo: 'egy perce',
    minutesAgo: '{delta} perce',
    hourAgo: 'egy órája',
    hoursAgo: '{delta} órája',
    dayAgo: '1 napja',
    daysAgo: '{delta} napja',
    weekAgo: '1 hete',
    weeksAgo: '{delta} hete',
    monthAgo: '1 hónapja',
    monthsAgo: '{delta} hónapja',
    yearAgo: '1 éve',
    yearsAgo: '{delta} éve',

    lessThanMinuteUntil: 'alig egy perc múlva',
    minuteUntil: 'egy perc múlva',
    minutesUntil: '{delta} perc múlva',
    hourUntil: 'egy óra múlva',
    hoursUntil: '{delta} óra múlva',
    dayUntil: '1 nap múlva',
    daysUntil: '{delta} nap múlva',
    weekUntil: '1 hét múlva',
    weeksUntil: '{delta} hét múlva',
    monthUntil: '1 hónap múlva',
    monthsUntil: '{delta} hónap múlva',
    yearUntil: '1 év múlva',
    yearsUntil: '{delta} év múlva'

});

/*
---

name: Locale.hu-HU.Form.Validator

description: Form Validator messages for Hungarian.

license: MIT-style license

authors:
  - Zsolt Szegheő

requires:
  - Locale

provides: [Locale.hu-HU.Form.Validator]

...
*/

/*eslint mootools-whitespace:0*/

Locale.define('hu-HU', 'FormValidator', {

    required: 'A mező kitöltése kötelező.',
    minLength: 'Legalább {minLength} karakter megadása szükséges (megadva {length} karakter).',
    maxLength: 'Legfeljebb {maxLength} karakter megadása lehetséges (megadva {length} karakter).',
    integer: 'Egész szám megadása szükséges. A tizedesjegyek (pl. 1.25) nem engedélyezettek.',
    numeric: 'Szám megadása szükséges (pl. "1" vagy "1.1" vagy "-1" vagy "-1.1").',
    digits: 'Csak számok és írásjelek megadása lehetséges (pl. telefonszám kötőjelek és/vagy perjelekkel).',
    alpha: 'Csak betűk (a-z) megadása lehetséges. Szóköz és egyéb karakterek nem engedélyezettek.',
    alphanum: 'Csak betűk (a-z) vagy számok (0-9) megadása lehetséges. Szóköz és egyéb karakterek nem engedélyezettek.',
    dateSuchAs: 'Valós dátum megadása szükséges (pl. {date}).',
    dateInFormatMDY: 'Valós dátum megadása szükséges ÉÉÉÉ.HH.NN. formában. (pl. "1999.12.31.")',
    email: 'Valós e-mail cím megadása szükséges (pl. "fred@domain.hu").',
    url: 'Valós URL megadása szükséges (pl. http://www.example.com).',
    currencyDollar: 'Valós pénzösszeg megadása szükséges (pl. 100.00 Ft.).',
    oneRequired: 'Az alábbi mezők legalább egyikének kitöltése kötelező.',
    errorPrefix: 'Hiba: ',
    warningPrefix: 'Figyelem: ',

    // Form.Validator.Extras
    noSpace: 'A mező nem tartalmazhat szóközöket.',
    reqChkByNode: 'Nincs egyetlen kijelölt elem sem.',
    requiredChk: 'A mező kitöltése kötelező.',
    reqChkByName: 'Egy {label} kiválasztása szükséges.',
    match: 'A mezőnek egyeznie kell a(z) {matchName} mezővel.',
    startDate: 'a kezdet dátuma',
    endDate: 'a vég dátuma',
    currentDate: 'jelenlegi dátum',
    afterDate: 'A dátum nem lehet kisebb, mint {label}.',
    beforeDate: 'A dátum nem lehet nagyobb, mint {label}.',
    startMonth: 'Kezdeti hónap megadása szükséges.',
    sameMonth: 'A két dátumnak ugyanazon hónapban kell lennie.',
    creditcard: 'A megadott bankkártyaszám nem valódi (megadva {length} számjegy).'

});

/*
---

name: Locale.it-IT.Date

description: Date messages for Italian.

license: MIT-style license.

authors:
  - Andrea Novero
  - Valerio Proietti

requires:
  - Locale

provides: [Locale.it-IT.Date]

...
*/

Locale.define('it-IT', 'Date', {

    months: ['Gennaio', 'Febbraio', 'Marzo', 'Aprile', 'Maggio', 'Giugno', 'Luglio', 'Agosto', 'Settembre', 'Ottobre', 'Novembre', 'Dicembre'],
    months_abbr: ['gen', 'feb', 'mar', 'apr', 'mag', 'giu', 'lug', 'ago', 'set', 'ott', 'nov', 'dic'],
    days: ['Domenica', 'Lunedì', 'Martedì', 'Mercoledì', 'Giovedì', 'Venerdì', 'Sabato'],
    days_abbr: ['dom', 'lun', 'mar', 'mer', 'gio', 'ven', 'sab'],

    // Culture's date order: DD/MM/YYYY
    dateOrder: ['date', 'month', 'year'],
    shortDate: '%d/%m/%Y',
    shortTime: '%H.%M',
    AM: 'AM',
    PM: 'PM',
    firstDayOfWeek: 1,

    // Date.Extras
    ordinal: 'º',

    lessThanMinuteAgo: 'meno di un minuto fa',
    minuteAgo: 'circa un minuto fa',
    minutesAgo: 'circa {delta} minuti fa',
    hourAgo: "circa un'ora fa",
    hoursAgo: 'circa {delta} ore fa',
    dayAgo: 'circa 1 giorno fa',
    daysAgo: 'circa {delta} giorni fa',
    weekAgo: 'una settimana fa',
    weeksAgo: '{delta} settimane fa',
    monthAgo: 'un mese fa',
    monthsAgo: '{delta} mesi fa',
    yearAgo: 'un anno fa',
    yearsAgo: '{delta} anni fa',

    lessThanMinuteUntil: 'tra meno di un minuto',
    minuteUntil: 'tra circa un minuto',
    minutesUntil: 'tra circa {delta} minuti',
    hourUntil: "tra circa un'ora",
    hoursUntil: 'tra circa {delta} ore',
    dayUntil: 'tra circa un giorno',
    daysUntil: 'tra circa {delta} giorni',
    weekUntil: 'tra una settimana',
    weeksUntil: 'tra {delta} settimane',
    monthUntil: 'tra un mese',
    monthsUntil: 'tra {delta} mesi',
    yearUntil: 'tra un anno',
    yearsUntil: 'tra {delta} anni'

});

/*
---

name: Locale.it-IT.Form.Validator

description: Form Validator messages for Italian.

license: MIT-style license

authors:
  - Leonardo Laureti
  - Andrea Novero

requires:
  - Locale

provides: [Locale.it-IT.Form.Validator]

...
*/

/*eslint mootools-whitespace:0*/

Locale.define('it-IT', 'FormValidator', {

    required: 'Il campo &egrave; obbligatorio.',
    minLength: 'Inserire almeno {minLength} caratteri (ne sono stati inseriti {length}).',
    maxLength: 'Inserire al massimo {maxLength} caratteri (ne sono stati inseriti {length}).',
    integer: 'Inserire un numero intero. Non sono consentiti decimali (es.: 1.25).',
    numeric: 'Inserire solo valori numerici (es.: "1" oppure "1.1" oppure "-1" oppure "-1.1").',
    digits: 'Inserire solo numeri e caratteri di punteggiatura. Per esempio &egrave; consentito un numero telefonico con trattini o punti.',
    alpha: 'Inserire solo lettere (a-z). Non sono consentiti spazi o altri caratteri.',
    alphanum: 'Inserire solo lettere (a-z) o numeri (0-9). Non sono consentiti spazi o altri caratteri.',
    dateSuchAs: 'Inserire una data valida del tipo {date}',
    dateInFormatMDY: 'Inserire una data valida nel formato MM/GG/AAAA (es.: "12/31/1999")',
    email: 'Inserire un indirizzo email valido. Per esempio "nome@dominio.com".',
    url: 'Inserire un indirizzo valido. Per esempio "http://www.example.com".',
    currencyDollar: 'Inserire un importo valido. Per esempio "$100.00".',
    oneRequired: 'Completare almeno uno dei campi richiesti.',
    errorPrefix: 'Errore: ',
    warningPrefix: 'Attenzione: ',

    // Form.Validator.Extras
    noSpace: 'Non sono consentiti spazi.',
    reqChkByNode: 'Nessuna voce selezionata.',
    requiredChk: 'Il campo &egrave; obbligatorio.',
    reqChkByName: 'Selezionare un(a) {label}.',
    match: 'Il valore deve corrispondere al campo {matchName}',
    startDate: "data d'inizio",
    endDate: 'data di fine',
    currentDate: 'data attuale',
    afterDate: 'La data deve corrispondere o essere successiva al {label}.',
    beforeDate: 'La data deve corrispondere o essere precedente al {label}.',
    startMonth: "Selezionare un mese d'inizio",
    sameMonth: 'Le due date devono essere dello stesso mese - occorre modificarne una.'

});

/*
---

name: Locale.ja-JP.Date

description: Date messages for Japanese.

license: MIT-style license

authors:
  - Noritaka Horio

requires:
  - Locale

provides: [Locale.ja-JP.Date]

...
*/

Locale.define('ja-JP', 'Date', {

    months: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
    months_abbr: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
    days: ['日曜日', '月曜日', '火曜日', '水曜日', '木曜日', '金曜日', '土曜日'],
    days_abbr: ['日', '月', '火', '水', '木', '金', '土'],

    // Culture's date order: YYYY/MM/DD
    dateOrder: ['year', 'month', 'date'],
    shortDate: '%Y/%m/%d',
    shortTime: '%H:%M',
    AM: '午前',
    PM: '午後',
    firstDayOfWeek: 0,

    // Date.Extras
    ordinal: '',

    lessThanMinuteAgo: '1分以内前',
    minuteAgo: '約1分前',
    minutesAgo: '約{delta}分前',
    hourAgo: '約1時間前',
    hoursAgo: '約{delta}時間前',
    dayAgo: '1日前',
    daysAgo: '{delta}日前',
    weekAgo: '1週間前',
    weeksAgo: '{delta}週間前',
    monthAgo: '1ヶ月前',
    monthsAgo: '{delta}ヶ月前',
    yearAgo: '1年前',
    yearsAgo: '{delta}年前',

    lessThanMinuteUntil: '今から約1分以内',
    minuteUntil: '今から約1分',
    minutesUntil: '今から約{delta}分',
    hourUntil: '今から約1時間',
    hoursUntil: '今から約{delta}時間',
    dayUntil: '今から1日間',
    daysUntil: '今から{delta}日間',
    weekUntil: '今から1週間',
    weeksUntil: '今から{delta}週間',
    monthUntil: '今から1ヶ月',
    monthsUntil: '今から{delta}ヶ月',
    yearUntil: '今から1年',
    yearsUntil: '今から{delta}年'

});

/*
---

name: Locale.ja-JP.Form.Validator

description: Form Validator messages for Japanese.

license: MIT-style license

authors:
  - Noritaka Horio

requires:
  - Locale

provides: [Locale.ja-JP.Form.Validator]

...
*/

Locale.define('ja-JP', 'FormValidator', {

    required: '入力は必須です。',
    minLength: '入力文字数は{minLength}以上にしてください。({length}文字)',
    maxLength: '入力文字数は{maxLength}以下にしてください。({length}文字)',
    integer: '整数を入力してください。',
    numeric: '入力できるのは数値だけです。(例: "1", "1.1", "-1", "-1.1"....)',
    digits: '入力できるのは数値と句読記号です。 (例: -や+を含む電話番号など).',
    alpha: '入力できるのは半角英字だけです。それ以外の文字は入力できません。',
    alphanum: '入力できるのは半角英数字だけです。それ以外の文字は入力できません。',
    dateSuchAs: '有効な日付を入力してください。{date}',
    dateInFormatMDY: '日付の書式に誤りがあります。YYYY/MM/DD (i.e. "1999/12/31")',
    email: 'メールアドレスに誤りがあります。',
    url: 'URLアドレスに誤りがあります。',
    currencyDollar: '金額に誤りがあります。',
    oneRequired: 'ひとつ以上入力してください。',
    errorPrefix: 'エラー: ',
    warningPrefix: '警告: ',

    // FormValidator.Extras
    noSpace: 'スペースは入力できません。',
    reqChkByNode: '選択されていません。',
    requiredChk: 'この項目は必須です。',
    reqChkByName: '{label}を選択してください。',
    match: '{matchName}が入力されている場合必須です。',
    startDate: '開始日',
    endDate: '終了日',
    currentDate: '今日',
    afterDate: '{label}以降の日付にしてください。',
    beforeDate: '{label}以前の日付にしてください。',
    startMonth: '開始月を選択してください。',
    sameMonth: '日付が同一です。どちらかを変更してください。'

});

/*
---

name: Locale.ja-JP.Number

description: Number messages for Japanese.

license: MIT-style license

authors:
  - Noritaka Horio

requires:
  - Locale

provides: [Locale.ja-JP.Number]

...
*/

Locale.define('ja-JP', 'Number', {

    decimal: '.',
    group: ',',

    currency: {
        decimals: 0,
        prefix: '\\'
    }

});

/*
---

name: Locale.nl-NL.Date

description: Date messages for Dutch.

license: MIT-style license

authors:
  - Lennart Pilon
  - Tim Wienk

requires:
  - Locale

provides: [Locale.nl-NL.Date]

...
*/

Locale.define('nl-NL', 'Date', {

    months: ['januari', 'februari', 'maart', 'april', 'mei', 'juni', 'juli', 'augustus', 'september', 'oktober', 'november', 'december'],
    months_abbr: ['jan', 'feb', 'mrt', 'apr', 'mei', 'jun', 'jul', 'aug', 'sep', 'okt', 'nov', 'dec'],
    days: ['zondag', 'maandag', 'dinsdag', 'woensdag', 'donderdag', 'vrijdag', 'zaterdag'],
    days_abbr: ['zo', 'ma', 'di', 'wo', 'do', 'vr', 'za'],

    // Culture's date order: DD-MM-YYYY
    dateOrder: ['date', 'month', 'year'],
    shortDate: '%d-%m-%Y',
    shortTime: '%H:%M',
    AM: 'AM',
    PM: 'PM',
    firstDayOfWeek: 1,

    // Date.Extras
    ordinal: 'e',

    lessThanMinuteAgo: 'minder dan een minuut geleden',
    minuteAgo: 'ongeveer een minuut geleden',
    minutesAgo: '{delta} minuten geleden',
    hourAgo: 'ongeveer een uur geleden',
    hoursAgo: 'ongeveer {delta} uur geleden',
    dayAgo: 'een dag geleden',
    daysAgo: '{delta} dagen geleden',
    weekAgo: 'een week geleden',
    weeksAgo: '{delta} weken geleden',
    monthAgo: 'een maand geleden',
    monthsAgo: '{delta} maanden geleden',
    yearAgo: 'een jaar geleden',
    yearsAgo: '{delta} jaar geleden',

    lessThanMinuteUntil: 'over minder dan een minuut',
    minuteUntil: 'over ongeveer een minuut',
    minutesUntil: 'over {delta} minuten',
    hourUntil: 'over ongeveer een uur',
    hoursUntil: 'over {delta} uur',
    dayUntil: 'over ongeveer een dag',
    daysUntil: 'over {delta} dagen',
    weekUntil: 'over een week',
    weeksUntil: 'over {delta} weken',
    monthUntil: 'over een maand',
    monthsUntil: 'over {delta} maanden',
    yearUntil: 'over een jaar',
    yearsUntil: 'over {delta} jaar'

});

/*
---

name: Locale.nl-NL.Form.Validator

description: Form Validator messages for Dutch.

license: MIT-style license

authors:
  - Lennart Pilon
  - Arian Stolwijk
  - Tim Wienk

requires:
  - Locale

provides: [Locale.nl-NL.Form.Validator]

...
*/

Locale.define('nl-NL', 'FormValidator', {

    required: 'Dit veld is verplicht.',
    length: 'Vul precies {length} karakters in (je hebt {elLength} karakters ingevoerd).',
    minLength: 'Vul minimaal {minLength} karakters in (je hebt {length} karakters ingevoerd).',
    maxLength: 'Vul niet meer dan {maxLength} karakters in (je hebt {length} karakters ingevoerd).',
    integer: 'Vul een getal in. Getallen met decimalen (bijvoorbeeld 1.25) zijn niet toegestaan.',
    numeric: 'Vul alleen numerieke waarden in (bijvoorbeeld "1" of "1.1" of "-1" of "-1.1").',
    digits: 'Vul alleen nummers en leestekens in (bijvoorbeeld een telefoonnummer met streepjes is toegestaan).',
    alpha: 'Vul alleen letters in (a-z). Spaties en andere karakters zijn niet toegestaan.',
    alphanum: 'Vul alleen letters (a-z) of nummers (0-9) in. Spaties en andere karakters zijn niet toegestaan.',
    dateSuchAs: 'Vul een geldige datum in, zoals {date}',
    dateInFormatMDY: 'Vul een geldige datum, in het formaat MM/DD/YYYY (bijvoorbeeld "12/31/1999")',
    email: 'Vul een geldig e-mailadres in. Bijvoorbeeld "fred@domein.nl".',
    url: 'Vul een geldige URL in, zoals http://www.example.com.',
    currencyDollar: 'Vul een geldig $ bedrag in. Bijvoorbeeld $100.00 .',
    oneRequired: 'Vul iets in bij in ieder geval een van deze velden.',
    warningPrefix: 'Waarschuwing: ',
    errorPrefix: 'Fout: ',

    // Form.Validator.Extras
    noSpace: 'Spaties zijn niet toegestaan in dit veld.',
    reqChkByNode: 'Er zijn geen items geselecteerd.',
    requiredChk: 'Dit veld is verplicht.',
    reqChkByName: 'Selecteer een {label}.',
    match: 'Dit veld moet overeen komen met het {matchName} veld',
    startDate: 'de begin datum',
    endDate: 'de eind datum',
    currentDate: 'de huidige datum',
    afterDate: 'De datum moet hetzelfde of na {label} zijn.',
    beforeDate: 'De datum moet hetzelfde of voor {label} zijn.',
    startMonth: 'Selecteer een begin maand',
    sameMonth: 'Deze twee data moeten in dezelfde maand zijn - u moet een van beide aanpassen.',
    creditcard: 'Het ingevulde creditcardnummer is niet geldig. Controleer het nummer en probeer opnieuw. {length} getallen ingevuld.'

});

/*
---

name: Locale.nl-NL.Number

description: Number messages for Dutch.

license: MIT-style license

authors:
  - Arian Stolwijk

requires:
  - Locale
  - Locale.EU.Number

provides: [Locale.nl-NL.Number]

...
*/

Locale.define('nl-NL').inherit('EU', 'Number');




/*
---

name: Locale.no-NO.Date

description: Date messages for Norwegian.

license: MIT-style license

authors:
  - Espen 'Rexxars' Hovlandsdal
  - Ole Tøsse Kolvik
requires:
  - Locale

provides: [Locale.no-NO.Date]

...
*/

Locale.define('no-NO', 'Date', {
    months: ['Januar', 'Februar', 'Mars', 'April', 'Mai', 'Juni', 'Juli', 'August', 'September', 'Oktober', 'November', 'Desember'],
    months_abbr: ['Jan', 'Feb', 'Mar', 'Apr', 'Mai', 'Jun', 'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Des'],
    days: ['Søndag', 'Mandag', 'Tirsdag', 'Onsdag', 'Torsdag', 'Fredag', 'Lørdag'],
    days_abbr: ['Søn', 'Man', 'Tir', 'Ons', 'Tor', 'Fre', 'Lør'],

    // Culture's date order: DD.MM.YYYY
    dateOrder: ['date', 'month', 'year'],
    shortDate: '%d.%m.%Y',
    shortTime: '%H:%M',
    AM: 'AM',
    PM: 'PM',
    firstDayOfWeek: 1,

    lessThanMinuteAgo: 'mindre enn et minutt siden',
    minuteAgo: 'omtrent et minutt siden',
    minutesAgo: '{delta} minutter siden',
    hourAgo: 'omtrent en time siden',
    hoursAgo: 'omtrent {delta} timer siden',
    dayAgo: '{delta} dag siden',
    daysAgo: '{delta} dager siden',
    weekAgo: 'en uke siden',
    weeksAgo: '{delta} uker siden',
    monthAgo: 'en måned siden',
    monthsAgo: '{delta} måneder siden',
    yearAgo: 'ett år siden',
    yearsAgo: '{delta} år siden',

    lessThanMinuteUntil: 'mindre enn et minutt til',
    minuteUntil: 'omtrent et minutt til',
    minutesUntil: '{delta} minutter til',
    hourUntil: 'omtrent en time til',
    hoursUntil: 'omtrent {delta} timer til',
    dayUntil: 'en dag til',
    daysUntil: '{delta} dager til',
    weekUntil: 'en uke til',
    weeksUntil: '{delta} uker til',
    monthUntil: 'en måned til',
    monthsUntil: '{delta} måneder til',
    yearUntil: 'et år til',
    yearsUntil: '{delta} år til'
});

/*
---

name: Locale.no-NO.Form.Validator

description: Form Validator messages for Norwegian.

license: MIT-style license

authors:
  - Aaron Newton
  - Espen 'Rexxars' Hovlandsdal
  - Ole Tøsse Kolvik

requires:
  - Locale

provides: [Locale.no-NO.Form.Validator]

...
*/

Locale.define('no-NO', 'FormValidator', {

    required: 'Dette feltet er påkrevd.',
    length: 'Skriv inn {length} tegn (du skrev {elLength} tegn)',
    minLength: 'Skriv inn minst {minLength} tegn (du skrev {length} tegn).',
    maxLength: 'Ikke skriv mer enn {maxLength} tegn (du skrev {length} tegn).',
    integer: 'Skriv inn et tall i dette feltet. Tall med desimaler (f.eks. 1,25) er ikke tillat.',
    numeric: 'Skriv kun inn numeriske verdier i dette feltet (f.eks. "1", "1.1", "-1" eller "-1.1").',
    digits: 'Skriv kun nummer og skilletegn i dette feltet.',
    alpha: 'Skriv kun bokstaver (a-å) i dette feltet. Ingen mellomrom eller andre tegn er tillat.',
    alphanum: 'Skriv kun bokstaver (a-å) eller nummer (0-9) i dette feltet. Ingen mellomrom eller andre tegn er tillat.',
    dateSuchAs: 'Skriv inn en gyldig dato, som f.eks. {date}',
    dateInFormatMDY: 'Skriv inn en gyldig dato, f.eks. DD/MM/YYYY ("31/12/1999")',
    email: 'Skriv inn en gyldig epost-adresse. F.eks. "ola.nordmann@example.com".',
    url: 'Skriv inn en gyldig URL, f.eks. http://www.example.com.',
    currencyDollar: 'Skriv inn et gyldig beløp. F.eks. 100,00.',
    oneRequired: 'Minst ett av disse feltene må fylles ut.',
    errorPrefix: 'Feil: ',
    warningPrefix: 'Advarsel: ',

    // Form.Validator.Extras
    noSpace: 'Mellomrom er ikke tillatt i dette feltet.',
    reqChkByNode: 'Ingen objekter er valgt.',
    requiredChk: 'Dette feltet er påkrevd.',
    reqChkByName: 'Velg en {label}.',
    match: 'Dette feltet må være lik {matchName}',
    startDate: 'startdato',
    endDate: 'sluttdato',
    currentDate: 'dagens dato',
    afterDate: 'Datoen må være den samme som eller etter {label}.',
    beforeDate: 'Datoen må være den samme som eller før {label}.',
    startMonth: 'Velg en startmåned',
    sameMonth: 'Datoene må være i den samme måneden - velg den ene eller den andre.',
    creditcard: 'Kortnummeret du skrev inn er ikke gyldig. Prøv igjen. Du skrev {length} siffer.'

});

/*
---

name: Locale.no-NO.Number

description: Number messages for Norwegian.

license: MIT-style license

authors:
  - Arian Stolwijk
  - Martin Lundgren
  - Ole T�sse Kolvik

requires:
  - Locale
  - Locale.EU.Number

provides: [Locale.no-NO.Number]

...
*/

Locale.define('no-NO', 'Number', {

    currency: {
        prefix: 'NOK '
    }

}).inherit('EU', 'Number');

/*
---

name: Locale.pl-PL.Date

description: Date messages for Polish.

license: MIT-style license

authors:
  - Oskar Krawczyk

requires:
  - Locale

provides: [Locale.pl-PL.Date]

...
*/

Locale.define('pl-PL', 'Date', {

    months: ['Styczeń', 'Luty', 'Marzec', 'Kwiecień', 'Maj', 'Czerwiec', 'Lipiec', 'Sierpień', 'Wrzesień', 'Październik', 'Listopad', 'Grudzień'],
    months_abbr: ['sty', 'lut', 'mar', 'kwi', 'maj', 'cze', 'lip', 'sie', 'wrz', 'paź', 'lis', 'gru'],
    days: ['Niedziela', 'Poniedziałek', 'Wtorek', 'Środa', 'Czwartek', 'Piątek', 'Sobota'],
    days_abbr: ['niedz.', 'pon.', 'wt.', 'śr.', 'czw.', 'pt.', 'sob.'],

    // Culture's date order: YYYY-MM-DD
    dateOrder: ['year', 'month', 'date'],
    shortDate: '%Y-%m-%d',
    shortTime: '%H:%M',
    AM: 'nad ranem',
    PM: 'po południu',
    firstDayOfWeek: 1,

    // Date.Extras
    ordinal: function(dayOfMonth){
        return (dayOfMonth > 3 && dayOfMonth < 21) ? 'ty' : ['ty', 'szy', 'gi', 'ci', 'ty'][Math.min(dayOfMonth % 10, 4)];
    },

    lessThanMinuteAgo: 'mniej niż minute temu',
    minuteAgo: 'około minutę temu',
    minutesAgo: '{delta} minut temu',
    hourAgo: 'około godzinę temu',
    hoursAgo: 'około {delta} godzin temu',
    dayAgo: 'Wczoraj',
    daysAgo: '{delta} dni temu',

    lessThanMinuteUntil: 'za niecałą minutę',
    minuteUntil: 'za około minutę',
    minutesUntil: 'za {delta} minut',
    hourUntil: 'za około godzinę',
    hoursUntil: 'za około {delta} godzin',
    dayUntil: 'za 1 dzień',
    daysUntil: 'za {delta} dni'

});

/*
---

name: Locale.pl-PL.Form.Validator

description: Form Validator messages for Polish.

license: MIT-style license

authors:
  - Oskar Krawczyk

requires:
  - Locale

provides: [Locale.pl-PL.Form.Validator]

...
*/

Locale.define('pl-PL', 'FormValidator', {

    required: 'To pole jest wymagane.',
    minLength: 'Wymagane jest przynajmniej {minLength} znaków (wpisanych zostało tylko {length}).',
    maxLength: 'Dozwolone jest nie więcej niż {maxLength} znaków (wpisanych zostało {length})',
    integer: 'To pole wymaga liczb całych. Liczby dziesiętne (np. 1.25) są niedozwolone.',
    numeric: 'Prosimy używać tylko numerycznych wartości w tym polu (np. "1", "1.1", "-1" lub "-1.1").',
    digits: 'Prosimy używać liczb oraz zankow punktuacyjnych w typ polu (dla przykładu, przy numerze telefonu myślniki i kropki są dozwolone).',
    alpha: 'Prosimy używać tylko liter (a-z) w tym polu. Spacje oraz inne znaki są niedozwolone.',
    alphanum: 'Prosimy używać tylko liter (a-z) lub liczb (0-9) w tym polu. Spacje oraz inne znaki są niedozwolone.',
    dateSuchAs: 'Prosimy podać prawidłową datę w formacie: {date}',
    dateInFormatMDY: 'Prosimy podać poprawną date w formacie DD.MM.RRRR (i.e. "12.01.2009")',
    email: 'Prosimy podać prawidłowy adres e-mail, np. "jan@domena.pl".',
    url: 'Prosimy podać prawidłowy adres URL, np. http://www.example.com.',
    currencyDollar: 'Prosimy podać prawidłową sumę w PLN. Dla przykładu: 100.00 PLN.',
    oneRequired: 'Prosimy wypełnić chociaż jedno z pól.',
    errorPrefix: 'Błąd: ',
    warningPrefix: 'Uwaga: ',

    // Form.Validator.Extras
    noSpace: 'W tym polu nie mogą znajdować się spacje.',
    reqChkByNode: 'Brak zaznaczonych elementów.',
    requiredChk: 'To pole jest wymagane.',
    reqChkByName: 'Prosimy wybrać z {label}.',
    match: 'To pole musi być takie samo jak {matchName}',
    startDate: 'data początkowa',
    endDate: 'data końcowa',
    currentDate: 'aktualna data',
    afterDate: 'Podana data poinna być taka sama lub po {label}.',
    beforeDate: 'Podana data poinna być taka sama lub przed {label}.',
    startMonth: 'Prosimy wybrać początkowy miesiąc.',
    sameMonth: 'Te dwie daty muszą być w zakresie tego samego miesiąca - wymagana jest zmiana któregoś z pól.'

});

/*
---

name: Locale.pt-PT.Date

description: Date messages for Portuguese.

license: MIT-style license

authors:
  - Fabio Miranda Costa

requires:
  - Locale

provides: [Locale.pt-PT.Date]

...
*/

Locale.define('pt-PT', 'Date', {

    months: ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho', 'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'],
    months_abbr: ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'],
    days: ['Domingo', 'Segunda-feira', 'Terça-feira', 'Quarta-feira', 'Quinta-feira', 'Sexta-feira', 'Sábado'],
    days_abbr: ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'],

    // Culture's date order: DD-MM-YYYY
    dateOrder: ['date', 'month', 'year'],
    shortDate: '%d-%m-%Y',
    shortTime: '%H:%M',
    AM: 'AM',
    PM: 'PM',
    firstDayOfWeek: 1,

    // Date.Extras
    ordinal: 'º',

    lessThanMinuteAgo: 'há menos de um minuto',
    minuteAgo: 'há cerca de um minuto',
    minutesAgo: 'há {delta} minutos',
    hourAgo: 'há cerca de uma hora',
    hoursAgo: 'há cerca de {delta} horas',
    dayAgo: 'há um dia',
    daysAgo: 'há {delta} dias',
    weekAgo: 'há uma semana',
    weeksAgo: 'há {delta} semanas',
    monthAgo: 'há um mês',
    monthsAgo: 'há {delta} meses',
    yearAgo: 'há um ano',
    yearsAgo: 'há {delta} anos',

    lessThanMinuteUntil: 'em menos de um minuto',
    minuteUntil: 'em um minuto',
    minutesUntil: 'em {delta} minutos',
    hourUntil: 'em uma hora',
    hoursUntil: 'em {delta} horas',
    dayUntil: 'em um dia',
    daysUntil: 'em {delta} dias',
    weekUntil: 'em uma semana',
    weeksUntil: 'em {delta} semanas',
    monthUntil: 'em um mês',
    monthsUntil: 'em {delta} meses',
    yearUntil: 'em um ano',
    yearsUntil: 'em {delta} anos'

});

/*
---

name: Locale.pt-BR.Date

description: Date messages for Portuguese (Brazil).

license: MIT-style license

authors:
  - Fabio Miranda Costa

requires:
  - Locale
  - Locale.pt-PT.Date

provides: [Locale.pt-BR.Date]

...
*/

Locale.define('pt-BR', 'Date', {

    // Culture's date order: DD/MM/YYYY
    shortDate: '%d/%m/%Y'

}).inherit('pt-PT', 'Date');

/*
---

name: Locale.pt-BR.Form.Validator

description: Form Validator messages for Portuguese (Brazil).

license: MIT-style license

authors:
  - Fábio Miranda Costa

requires:
  - Locale

provides: [Locale.pt-BR.Form.Validator]

...
*/

Locale.define('pt-BR', 'FormValidator', {

    required: 'Este campo é obrigatório.',
    minLength: 'Digite pelo menos {minLength} caracteres (tamanho atual: {length}).',
    maxLength: 'Não digite mais de {maxLength} caracteres (tamanho atual: {length}).',
    integer: 'Por favor digite apenas um número inteiro neste campo. Não são permitidos números decimais (por exemplo, 1,25).',
    numeric: 'Por favor digite apenas valores numéricos neste campo (por exemplo, "1" ou "1.1" ou "-1" ou "-1,1").',
    digits: 'Por favor use apenas números e pontuação neste campo (por exemplo, um número de telefone com traços ou pontos é permitido).',
    alpha: 'Por favor use somente letras (a-z). Espaço e outros caracteres não são permitidos.',
    alphanum: 'Use somente letras (a-z) ou números (0-9) neste campo. Espaço e outros caracteres não são permitidos.',
    dateSuchAs: 'Digite uma data válida, como {date}',
    dateInFormatMDY: 'Digite uma data válida, como DD/MM/YYYY (por exemplo, "31/12/1999")',
    email: 'Digite um endereço de email válido. Por exemplo "nome@dominio.com".',
    url: 'Digite uma URL válida. Exemplo: http://www.example.com.',
    currencyDollar: 'Digite um valor em dinheiro válido. Exemplo: R$100,00 .',
    oneRequired: 'Digite algo para pelo menos um desses campos.',
    errorPrefix: 'Erro: ',
    warningPrefix: 'Aviso: ',

    // Form.Validator.Extras
    noSpace: 'Não é possível digitar espaços neste campo.',
    reqChkByNode: 'Não foi selecionado nenhum item.',
    requiredChk: 'Este campo é obrigatório.',
    reqChkByName: 'Por favor digite um {label}.',
    match: 'Este campo deve ser igual ao campo {matchName}.',
    startDate: 'a data inicial',
    endDate: 'a data final',
    currentDate: 'a data atual',
    afterDate: 'A data deve ser igual ou posterior a {label}.',
    beforeDate: 'A data deve ser igual ou anterior a {label}.',
    startMonth: 'Por favor selecione uma data inicial.',
    sameMonth: 'Estas duas datas devem ter o mesmo mês - você deve modificar uma das duas.',
    creditcard: 'O número do cartão de crédito informado é inválido. Por favor verifique o valor e tente novamente. {length} números informados.'

});

/*
---

name: Locale.pt-BR.Number

description: Number messages for PT Brazilian.

license: MIT-style license

authors:
  - Arian Stolwijk
  - Danillo César

requires:
  - Locale

provides: [Locale.pt-BR.Number]

...
*/

Locale.define('pt-BR', 'Number', {

    decimal: ',',
    group: '.',

    currency: {
        prefix: 'R$ '
    }

});



/*
---

name: Locale.pt-PT.Form.Validator

description: Form Validator messages for Portuguese.

license: MIT-style license

authors:
  - Miquel Hudin

requires:
  - Locale

provides: [Locale.pt-PT.Form.Validator]

...
*/

Locale.define('pt-PT', 'FormValidator', {

    required: 'Este campo é necessário.',
    minLength: 'Digite pelo menos{minLength} caracteres (comprimento {length} caracteres).',
    maxLength: 'Não insira mais de {maxLength} caracteres (comprimento {length} caracteres).',
    integer: 'Digite um número inteiro neste domínio. Com números decimais (por exemplo, 1,25), não são permitidas.',
    numeric: 'Digite apenas valores numéricos neste domínio (p.ex., "1" ou "1.1" ou "-1" ou "-1,1").',
    digits: 'Por favor, use números e pontuação apenas neste campo (p.ex., um número de telefone com traços ou pontos é permitida).',
    alpha: 'Por favor use somente letras (a-z), com nesta área. Não utilize espaços nem outros caracteres são permitidos.',
    alphanum: 'Use somente letras (a-z) ou números (0-9) neste campo. Não utilize espaços nem outros caracteres são permitidos.',
    dateSuchAs: 'Digite uma data válida, como {date}',
    dateInFormatMDY: 'Digite uma data válida, como DD/MM/YYYY (p.ex. "31/12/1999")',
    email: 'Digite um endereço de email válido. Por exemplo "fred@domain.com".',
    url: 'Digite uma URL válida, como http://www.example.com.',
    currencyDollar: 'Digite um valor válido $. Por exemplo $ 100,00. ',
    oneRequired: 'Digite algo para pelo menos um desses insumos.',
    errorPrefix: 'Erro: ',
    warningPrefix: 'Aviso: '

});

/*
---

name: Locale.ru-RU-unicode.Date

description: Date messages for Russian (utf-8).

license: MIT-style license

authors:
  - Evstigneev Pavel
  - Kuryanovich Egor

requires:
  - Locale

provides: [Locale.ru-RU.Date]

...
*/

(function(){

// Russian language pluralization rules, taken from CLDR project, http://unicode.org/cldr/
// one -> n mod 10 is 1 and n mod 100 is not 11;
// few -> n mod 10 in 2..4 and n mod 100 not in 12..14;
// many -> n mod 10 is 0 or n mod 10 in 5..9 or n mod 100 in 11..14;
// other -> everything else (example 3.14)
    var pluralize = function(n, one, few, many, other){
        var modulo10 = n % 10,
            modulo100 = n % 100;

        if (modulo10 == 1 && modulo100 != 11){
            return one;
        } else if ((modulo10 == 2 || modulo10 == 3 || modulo10 == 4) && !(modulo100 == 12 || modulo100 == 13 || modulo100 == 14)){
            return few;
        } else if (modulo10 == 0 || (modulo10 == 5 || modulo10 == 6 || modulo10 == 7 || modulo10 == 8 || modulo10 == 9) || (modulo100 == 11 || modulo100 == 12 || modulo100 == 13 || modulo100 == 14)){
            return many;
        } else {
            return other;
        }
    };

    Locale.define('ru-RU', 'Date', {

        months: ['Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'],
        months_abbr: ['янв', 'февр', 'март', 'апр', 'май','июнь','июль','авг','сент','окт','нояб','дек'],
        days: ['Воскресенье', 'Понедельник', 'Вторник', 'Среда', 'Четверг', 'Пятница', 'Суббота'],
        days_abbr: ['Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб'],

        // Culture's date order: DD.MM.YYYY
        dateOrder: ['date', 'month', 'year'],
        shortDate: '%d.%m.%Y',
        shortTime: '%H:%M',
        AM: 'AM',
        PM: 'PM',
        firstDayOfWeek: 1,

        // Date.Extras
        ordinal: '',

        lessThanMinuteAgo: 'меньше минуты назад',
        minuteAgo: 'минуту назад',
        minutesAgo: function(delta){ return '{delta} ' + pluralize(delta, 'минуту', 'минуты', 'минут') + ' назад'; },
        hourAgo: 'час назад',
        hoursAgo: function(delta){ return '{delta} ' + pluralize(delta, 'час', 'часа', 'часов') + ' назад'; },
        dayAgo: 'вчера',
        daysAgo: function(delta){ return '{delta} ' + pluralize(delta, 'день', 'дня', 'дней') + ' назад'; },
        weekAgo: 'неделю назад',
        weeksAgo: function(delta){ return '{delta} ' + pluralize(delta, 'неделя', 'недели', 'недель') + ' назад'; },
        monthAgo: 'месяц назад',
        monthsAgo: function(delta){ return '{delta} ' + pluralize(delta, 'месяц', 'месяца', 'месяцев') + ' назад'; },
        yearAgo: 'год назад',
        yearsAgo: function(delta){ return '{delta} ' + pluralize(delta, 'год', 'года', 'лет') + ' назад'; },

        lessThanMinuteUntil: 'меньше чем через минуту',
        minuteUntil: 'через минуту',
        minutesUntil: function(delta){ return 'через {delta} ' + pluralize(delta, 'минуту', 'минуты', 'минут') + ''; },
        hourUntil: 'через час',
        hoursUntil: function(delta){ return 'через {delta} ' + pluralize(delta, 'час', 'часа', 'часов') + ''; },
        dayUntil: 'завтра',
        daysUntil: function(delta){ return 'через {delta} ' + pluralize(delta, 'день', 'дня', 'дней') + ''; },
        weekUntil: 'через неделю',
        weeksUntil: function(delta){ return 'через {delta} ' + pluralize(delta, 'неделю', 'недели', 'недель') + ''; },
        monthUntil: 'через месяц',
        monthsUntil: function(delta){ return 'через {delta} ' + pluralize(delta, 'месяц', 'месяца', 'месяцев') + ''; },
        yearUntil: 'через',
        yearsUntil: function(delta){ return 'через {delta} ' + pluralize(delta, 'год', 'года', 'лет') + ''; }

    });

//<1.2compat>

    Locale.define('ru-RU-unicode').inherit('ru-RU', 'Date');

//</1.2compat>

})();

/*
---

name: Locale.ru-RU-unicode.Form.Validator

description: Form Validator messages for Russian (utf-8).

license: MIT-style license

authors:
  - Chernodarov Egor

requires:
  - Locale

provides: [Locale.ru-RU.Form.Validator]

...
*/

Locale.define('ru-RU', 'FormValidator', {

    required: 'Это поле обязательно к заполнению.',
    minLength: 'Пожалуйста, введите хотя бы {minLength} символов (Вы ввели {length}).',
    maxLength: 'Пожалуйста, введите не больше {maxLength} символов (Вы ввели {length}).',
    integer: 'Пожалуйста, введите в это поле число. Дробные числа (например 1.25) тут не разрешены.',
    numeric: 'Пожалуйста, введите в это поле число (например "1" или "1.1", или "-1", или "-1.1").',
    digits: 'В этом поле Вы можете использовать только цифры и знаки пунктуации (например, телефонный номер со знаками дефиса или с точками).',
    alpha: 'В этом поле можно использовать только латинские буквы (a-z). Пробелы и другие символы запрещены.',
    alphanum: 'В этом поле можно использовать только латинские буквы (a-z) и цифры (0-9). Пробелы и другие символы запрещены.',
    dateSuchAs: 'Пожалуйста, введите корректную дату {date}',
    dateInFormatMDY: 'Пожалуйста, введите дату в формате ММ/ДД/ГГГГ (например "12/31/1999")',
    email: 'Пожалуйста, введите корректный емейл-адрес. Для примера "fred@domain.com".',
    url: 'Пожалуйста, введите правильную ссылку вида http://www.example.com.',
    currencyDollar: 'Пожалуйста, введите сумму в долларах. Например: $100.00 .',
    oneRequired: 'Пожалуйста, выберите хоть что-нибудь в одном из этих полей.',
    errorPrefix: 'Ошибка: ',
    warningPrefix: 'Внимание: '

});

//<1.2compat>

Locale.define('ru-RU-unicode').inherit('ru-RU', 'FormValidator');

//</1.2compat>

/*
---

name: Locale.sk-SK.Date

description: Date messages for Slovak.

license: MIT-style license

authors:
  - Ivan Masár

requires:
  - Locale

provides: [Locale.sk-SK.Date]

...
*/
(function(){

// Slovak language pluralization rules, see http://unicode.org/repos/cldr-tmp/trunk/diff/supplemental/language_plural_rules.html
// one -> n is 1;            1
// few -> n in 2..4;         2-4
// other -> everything else  0, 5-999, 1.31, 2.31, 5.31...
    var pluralize = function(n, one, few, other){
        if (n == 1) return one;
        else if (n == 2 || n == 3 || n == 4) return few;
        else return other;
    };

    Locale.define('sk-SK', 'Date', {

        months: ['Január', 'Február', 'Marec', 'Apríl', 'Máj', 'Jún', 'Júl', 'August', 'September', 'Október', 'November', 'December'],
        months_abbr: ['januára', 'februára', 'marca', 'apríla', 'mája', 'júna', 'júla', 'augusta', 'septembra', 'októbra', 'novembra', 'decembra'],
        days: ['Nedele', 'Pondelí', 'Úterý', 'Streda', 'Čtvrtek', 'Pátek', 'Sobota'],
        days_abbr: ['ne', 'po', 'ut', 'st', 'št', 'pi', 'so'],

        // Culture's date order: DD.MM.YYYY
        dateOrder: ['date', 'month', 'year'],
        shortDate: '%d.%m.%Y',
        shortTime: '%H:%M',
        AM: 'dop.',
        PM: 'pop.',
        firstDayOfWeek: 1,

        // Date.Extras
        ordinal: '.',

        lessThanMinuteAgo: 'pred chvíľou',
        minuteAgo: 'približne pred minútou',
        minutesAgo: function(delta){ return 'pred {delta} ' + pluralize(delta, 'minútou', 'minútami', 'minútami'); },
        hourAgo: 'približne pred hodinou',
        hoursAgo: function(delta){ return 'pred {delta} ' + pluralize(delta, 'hodinou', 'hodinami', 'hodinami'); },
        dayAgo: 'pred dňom',
        daysAgo: function(delta){ return 'pred {delta} ' + pluralize(delta, 'dňom', 'dňami', 'dňami'); },
        weekAgo: 'pred týždňom',
        weeksAgo: function(delta){ return 'pred {delta} ' + pluralize(delta, 'týždňom', 'týždňami', 'týždňami'); },
        monthAgo: 'pred mesiacom',
        monthsAgo: function(delta){ return 'pred {delta} ' + pluralize(delta, 'mesiacom', 'mesiacmi', 'mesiacmi'); },
        yearAgo: 'pred rokom',
        yearsAgo: function(delta){ return 'pred {delta} ' + pluralize(delta, 'rokom', 'rokmi', 'rokmi'); },

        lessThanMinuteUntil: 'o chvíľu',
        minuteUntil: 'približne o minútu',
        minutesUntil: function(delta){ return 'o {delta} ' + pluralize(delta, 'minútu', 'minúty', 'minúty'); },
        hourUntil: 'približne o hodinu',
        hoursUntil: function(delta){ return 'o {delta} ' + pluralize(delta, 'hodinu', 'hodiny', 'hodín'); },
        dayUntil: 'o deň',
        daysUntil: function(delta){ return 'o {delta} ' + pluralize(delta, 'deň', 'dni', 'dní'); },
        weekUntil: 'o týždeň',
        weeksUntil: function(delta){ return 'o {delta} ' + pluralize(delta, 'týždeň', 'týždne', 'týždňov'); },
        monthUntil: 'o mesiac',
        monthsUntil: function(delta){ return 'o {delta} ' + pluralize(delta, 'mesiac', 'mesiace', 'mesiacov'); },
        yearUntil: 'o rok',
        yearsUntil: function(delta){ return 'o {delta} ' + pluralize(delta, 'rok', 'roky', 'rokov'); }
    });

})();

/*
---

name: Locale.sk-SK.Form.Validator

description: Form Validator messages for Czech.

license: MIT-style license

authors:
  - Ivan Masár

requires:
  - Locale

provides: [Locale.sk-SK.Form.Validator]

...
*/

Locale.define('sk-SK', 'FormValidator', {

    required: 'Táto položka je povinná.',
    minLength: 'Zadajte prosím aspoň {minLength} znakov (momentálne {length} znakov).',
    maxLength: 'Zadajte prosím menej ako {maxLength} znakov (momentálne {length} znakov).',
    integer: 'Zadajte prosím celé číslo. Desetinné čísla (napr. 1.25) nie sú povolené.',
    numeric: 'Zadajte len číselné hodnoty (t.j. „1“ alebo „1.1“ alebo „-1“ alebo „-1.1“).',
    digits: 'Zadajte prosím len čísla a interpunkčné znamienka (napríklad telefónne číslo s pomlčkami albo bodkami je povolené).',
    alpha: 'Zadajte prosím len písmená (a-z). Medzery alebo iné znaky nie sú povolené.',
    alphanum: 'Zadajte prosím len písmená (a-z) alebo číslice (0-9). Medzery alebo iné znaky nie sú povolené.',
    dateSuchAs: 'Zadajte prosím platný dátum v tvare {date}',
    dateInFormatMDY: 'Zadajte prosím platný datum v tvare MM / DD / RRRR (t.j. „12/31/1999“)',
    email: 'Zadajte prosím platnú emailovú adresu. Napríklad „fred@domain.com“.',
    url: 'Zadajte prosím platnoú adresu URL v tvare http://www.example.com.',
    currencyDollar: 'Zadajte prosím platnú čiastku. Napríklad $100.00.',
    oneRequired: 'Zadajte prosím aspoň jednu hodnotu z týchto položiek.',
    errorPrefix: 'Chyba: ',
    warningPrefix: 'Upozornenie: ',

    // Form.Validator.Extras
    noSpace: 'V tejto položle nie sú povolené medzery',
    reqChkByNode: 'Nie sú vybrané žiadne položky.',
    requiredChk: 'Táto položka je povinná.',
    reqChkByName: 'Prosím vyberte {label}.',
    match: 'Táto položka sa musí zhodovať s položkou {matchName}',
    startDate: 'dátum začiatku',
    endDate: 'dátum ukončenia',
    currendDate: 'aktuálny dátum',
    afterDate: 'Dátum by mal býť rovnaký alebo väčší ako {label}.',
    beforeDate: 'Dátum by mal byť rovnaký alebo menší ako {label}.',
    startMonth: 'Vyberte počiatočný mesiac.',
    sameMonth: 'Tieto dva dátumy musia býť v rovnakom mesiaci - zmeňte jeden z nich.',
    creditcard: 'Zadané číslo kreditnej karty je neplatné. Prosím, opravte ho. Bolo zadaných {length} číslic.'

});

/*
---

name: Locale.si-SI.Date

description: Date messages for Slovenian.

license: MIT-style license

authors:
  - Radovan Lozej

requires:
  - Locale

provides: [Locale.si-SI.Date]

...
*/

(function(){

    var pluralize = function(n, one, two, three, other){
        return (n >= 1 && n <= 3) ? arguments[n] : other;
    };

    Locale.define('sl-SI', 'Date', {

        months: ['januar', 'februar', 'marec', 'april', 'maj', 'junij', 'julij', 'avgust', 'september', 'oktober', 'november', 'december'],
        months_abbr: ['jan', 'feb', 'mar', 'apr', 'maj', 'jun', 'jul', 'avg', 'sep', 'okt', 'nov', 'dec'],
        days: ['nedelja', 'ponedeljek', 'torek', 'sreda', 'četrtek', 'petek', 'sobota'],
        days_abbr: ['ned', 'pon', 'tor', 'sre', 'čet', 'pet', 'sob'],

        // Culture's date order: DD.MM.YYYY
        dateOrder: ['date', 'month', 'year'],
        shortDate: '%d.%m.%Y',
        shortTime: '%H.%M',
        AM: 'AM',
        PM: 'PM',
        firstDayOfWeek: 1,

        // Date.Extras
        ordinal: '.',

        lessThanMinuteAgo: 'manj kot minuto nazaj',
        minuteAgo: 'minuto nazaj',
        minutesAgo: function(delta){ return '{delta} ' + pluralize(delta, 'minuto', 'minuti', 'minute', 'minut') + ' nazaj'; },
        hourAgo: 'uro nazaj',
        hoursAgo: function(delta){ return '{delta} ' + pluralize(delta, 'uro', 'uri', 'ure', 'ur') + ' nazaj'; },
        dayAgo: 'dan nazaj',
        daysAgo: function(delta){ return '{delta} ' + pluralize(delta, 'dan', 'dneva', 'dni', 'dni') + ' nazaj'; },
        weekAgo: 'teden nazaj',
        weeksAgo: function(delta){ return '{delta} ' + pluralize(delta, 'teden', 'tedna', 'tedne', 'tednov') + ' nazaj'; },
        monthAgo: 'mesec nazaj',
        monthsAgo: function(delta){ return '{delta} ' + pluralize(delta, 'mesec', 'meseca', 'mesece', 'mesecov') + ' nazaj'; },
        yearthAgo: 'leto nazaj',
        yearsAgo: function(delta){ return '{delta} ' + pluralize(delta, 'leto', 'leti', 'leta', 'let') + ' nazaj'; },

        lessThanMinuteUntil: 'še manj kot minuto',
        minuteUntil: 'še minuta',
        minutesUntil: function(delta){ return 'še {delta} ' + pluralize(delta, 'minuta', 'minuti', 'minute', 'minut'); },
        hourUntil: 'še ura',
        hoursUntil: function(delta){ return 'še {delta} ' + pluralize(delta, 'ura', 'uri', 'ure', 'ur'); },
        dayUntil: 'še dan',
        daysUntil: function(delta){ return 'še {delta} ' + pluralize(delta, 'dan', 'dneva', 'dnevi', 'dni'); },
        weekUntil: 'še tedn',
        weeksUntil: function(delta){ return 'še {delta} ' + pluralize(delta, 'teden', 'tedna', 'tedni', 'tednov'); },
        monthUntil: 'še mesec',
        monthsUntil: function(delta){ return 'še {delta} ' + pluralize(delta, 'mesec', 'meseca', 'meseci', 'mesecov'); },
        yearUntil: 'še leto',
        yearsUntil: function(delta){ return 'še {delta} ' + pluralize(delta, 'leto', 'leti', 'leta', 'let'); }

    });

})();

/*
---

name: Locale.si-SI.Form.Validator

description: Form Validator messages for Slovenian.

license: MIT-style license

authors:
  - Radovan Lozej

requires:
  - Locale

provides: [Locale.si-SI.Form.Validator]

...
*/

Locale.define('sl-SI', 'FormValidator', {

    required: 'To polje je obvezno',
    minLength: 'Prosim, vnesite vsaj {minLength} znakov (vnesli ste {length} znakov).',
    maxLength: 'Prosim, ne vnesite več kot {maxLength} znakov (vnesli ste {length} znakov).',
    integer: 'Prosim, vnesite celo število. Decimalna števila (kot 1,25) niso dovoljena.',
    numeric: 'Prosim, vnesite samo numerične vrednosti (kot "1" ali "1.1" ali "-1" ali "-1.1").',
    digits: 'Prosim, uporabite številke in ločila le na tem polju (na primer, dovoljena je telefonska številka z pomišlaji ali pikami).',
    alpha: 'Prosim, uporabite le črke v tem plju. Presledki in drugi znaki niso dovoljeni.',
    alphanum: 'Prosim, uporabite samo črke ali številke v tem polju. Presledki in drugi znaki niso dovoljeni.',
    dateSuchAs: 'Prosim, vnesite pravilen datum kot {date}',
    dateInFormatMDY: 'Prosim, vnesite pravilen datum kot MM.DD.YYYY (primer "12.31.1999")',
    email: 'Prosim, vnesite pravilen email naslov. Na primer "fred@domain.com".',
    url: 'Prosim, vnesite pravilen URL kot http://www.example.com.',
    currencyDollar: 'Prosim, vnesit epravilno vrednost €. Primer 100,00€ .',
    oneRequired: 'Prosimo, vnesite nekaj za vsaj eno izmed teh polj.',
    errorPrefix: 'Napaka: ',
    warningPrefix: 'Opozorilo: ',

    // Form.Validator.Extras
    noSpace: 'To vnosno polje ne dopušča presledkov.',
    reqChkByNode: 'Nič niste izbrali.',
    requiredChk: 'To polje je obvezno',
    reqChkByName: 'Prosim, izberite {label}.',
    match: 'To polje se mora ujemati z poljem {matchName}',
    startDate: 'datum začetka',
    endDate: 'datum konca',
    currentDate: 'trenuten datum',
    afterDate: 'Datum bi moral biti isti ali po {label}.',
    beforeDate: 'Datum bi moral biti isti ali pred {label}.',
    startMonth: 'Prosim, vnesite začetni datum',
    sameMonth: 'Ta dva datuma morata biti v istem mesecu - premeniti morate eno ali drugo.',
    creditcard: 'Številka kreditne kartice ni pravilna. Preverite številko ali poskusite še enkrat. Vnešenih {length} znakov.'

});

/*
---

name: Locale.sv-SE.Date

description: Date messages for Swedish.

license: MIT-style license

authors:
  - Martin Lundgren

requires:
  - Locale

provides: [Locale.sv-SE.Date]

...
*/

Locale.define('sv-SE', 'Date', {

    months: ['januari', 'februari', 'mars', 'april', 'maj', 'juni', 'juli', 'augusti', 'september', 'oktober', 'november', 'december'],
    months_abbr: ['jan', 'feb', 'mar', 'apr', 'maj', 'jun', 'jul', 'aug', 'sep', 'okt', 'nov', 'dec'],
    days: ['söndag', 'måndag', 'tisdag', 'onsdag', 'torsdag', 'fredag', 'lördag'],
    days_abbr: ['sön', 'mån', 'tis', 'ons', 'tor', 'fre', 'lör'],

    // Culture's date order: YYYY-MM-DD
    dateOrder: ['year', 'month', 'date'],
    shortDate: '%Y-%m-%d',
    shortTime: '%H:%M',
    AM: '',
    PM: '',
    firstDayOfWeek: 1,

    // Date.Extras
    ordinal: '',

    lessThanMinuteAgo: 'mindre än en minut sedan',
    minuteAgo: 'ungefär en minut sedan',
    minutesAgo: '{delta} minuter sedan',
    hourAgo: 'ungefär en timme sedan',
    hoursAgo: 'ungefär {delta} timmar sedan',
    dayAgo: '1 dag sedan',
    daysAgo: '{delta} dagar sedan',

    lessThanMinuteUntil: 'mindre än en minut sedan',
    minuteUntil: 'ungefär en minut sedan',
    minutesUntil: '{delta} minuter sedan',
    hourUntil: 'ungefär en timme sedan',
    hoursUntil: 'ungefär {delta} timmar sedan',
    dayUntil: '1 dag sedan',
    daysUntil: '{delta} dagar sedan'

});

/*
---

name: Locale.sv-SE.Form.Validator

description: Form Validator messages for Swedish.

license: MIT-style license

authors:
  - Martin Lundgren

requires:
  - Locale

provides: [Locale.sv-SE.Form.Validator]

...
*/

Locale.define('sv-SE', 'FormValidator', {

    required: 'Fältet är obligatoriskt.',
    minLength: 'Ange minst {minLength} tecken (du angav {length} tecken).',
    maxLength: 'Ange högst {maxLength} tecken (du angav {length} tecken). ',
    integer: 'Ange ett heltal i fältet. Tal med decimaler (t.ex. 1,25) är inte tillåtna.',
    numeric: 'Ange endast numeriska värden i detta fält (t.ex. "1" eller "1.1" eller "-1" eller "-1,1").',
    digits: 'Använd endast siffror och skiljetecken i detta fält (till exempel ett telefonnummer med bindestreck tillåtet).',
    alpha: 'Använd endast bokstäver (a-ö) i detta fält. Inga mellanslag eller andra tecken är tillåtna.',
    alphanum: 'Använd endast bokstäver (a-ö) och siffror (0-9) i detta fält. Inga mellanslag eller andra tecken är tillåtna.',
    dateSuchAs: 'Ange ett giltigt datum som t.ex. {date}',
    dateInFormatMDY: 'Ange ett giltigt datum som t.ex. YYYY-MM-DD (i.e. "1999-12-31")',
    email: 'Ange en giltig e-postadress. Till exempel "erik@domain.com".',
    url: 'Ange en giltig webbadress som http://www.example.com.',
    currencyDollar: 'Ange en giltig belopp. Exempelvis 100,00.',
    oneRequired: 'Vänligen ange minst ett av dessa alternativ.',
    errorPrefix: 'Fel: ',
    warningPrefix: 'Varning: ',

    // Form.Validator.Extras
    noSpace: 'Det får inte finnas några mellanslag i detta fält.',
    reqChkByNode: 'Inga objekt är valda.',
    requiredChk: 'Detta är ett obligatoriskt fält.',
    reqChkByName: 'Välj en {label}.',
    match: 'Detta fält måste matcha {matchName}',
    startDate: 'startdatumet',
    endDate: 'slutdatum',
    currentDate: 'dagens datum',
    afterDate: 'Datumet bör vara samma eller senare än {label}.',
    beforeDate: 'Datumet bör vara samma eller tidigare än {label}.',
    startMonth: 'Välj en start månad',
    sameMonth: 'Dessa två datum måste vara i samma månad - du måste ändra det ena eller det andra.'

});

/*
---

name: Locale.sv-SE.Number

description: Number messages for Swedish.

license: MIT-style license

authors:
  - Arian Stolwijk
  - Martin Lundgren

requires:
  - Locale
  - Locale.EU.Number

provides: [Locale.sv-SE.Number]

...
*/

Locale.define('sv-SE', 'Number', {

    currency: {
        prefix: 'SEK '
    }

}).inherit('EU', 'Number');

/*
---

name: Locale.tr-TR.Date

description: Date messages for Turkish.

license: MIT-style license

authors:
  - Faruk Can Bilir

requires:
  - Locale

provides: [Locale.tr-TR.Date]

...
*/

Locale.define('tr-TR', 'Date', {

    months: ['Ocak', 'Şubat', 'Mart', 'Nisan', 'Mayıs', 'Haziran', 'Temmuz', 'Ağustos', 'Eylül', 'Ekim', 'Kasım', 'Aralık'],
    months_abbr: ['Oca', 'Şub', 'Mar', 'Nis', 'May', 'Haz', 'Tem', 'Ağu', 'Eyl', 'Eki', 'Kas', 'Ara'],
    days: ['Pazar', 'Pazartesi', 'Salı', 'Çarşamba', 'Perşembe', 'Cuma', 'Cumartesi'],
    days_abbr: ['Pa', 'Pzt', 'Sa', 'Ça', 'Pe', 'Cu', 'Cmt'],

    // Culture's date order: MM/DD/YYYY
    dateOrder: ['date', 'month', 'year'],
    shortDate: '%d/%m/%Y',
    shortTime: '%H.%M',
    AM: 'AM',
    PM: 'PM',
    firstDayOfWeek: 1,

    // Date.Extras
    ordinal: '',

    lessThanMinuteAgo: 'bir dakikadan önce',
    minuteAgo: 'yaklaşık bir dakika önce',
    minutesAgo: '{delta} dakika önce',
    hourAgo: 'bir saat kadar önce',
    hoursAgo: '{delta} saat kadar önce',
    dayAgo: 'bir gün önce',
    daysAgo: '{delta} gün önce',
    weekAgo: 'bir hafta önce',
    weeksAgo: '{delta} hafta önce',
    monthAgo: 'bir ay önce',
    monthsAgo: '{delta} ay önce',
    yearAgo: 'bir yıl önce',
    yearsAgo: '{delta} yıl önce',

    lessThanMinuteUntil: 'bir dakikadan az sonra',
    minuteUntil: 'bir dakika kadar sonra',
    minutesUntil: '{delta} dakika sonra',
    hourUntil: 'bir saat kadar sonra',
    hoursUntil: '{delta} saat kadar sonra',
    dayUntil: 'bir gün sonra',
    daysUntil: '{delta} gün sonra',
    weekUntil: 'bir hafta sonra',
    weeksUntil: '{delta} hafta sonra',
    monthUntil: 'bir ay sonra',
    monthsUntil: '{delta} ay sonra',
    yearUntil: 'bir yıl sonra',
    yearsUntil: '{delta} yıl sonra'

});

/*
---

name: Locale.tr-TR.Form.Validator

description: Form Validator messages for Turkish.

license: MIT-style license

authors:
  - Faruk Can Bilir

requires:
  - Locale

provides: [Locale.tr-TR.Form.Validator]

...
*/

Locale.define('tr-TR', 'FormValidator', {

    required: 'Bu alan zorunlu.',
    minLength: 'Lütfen en az {minLength} karakter girin (siz {length} karakter girdiniz).',
    maxLength: 'Lütfen en fazla {maxLength} karakter girin (siz {length} karakter girdiniz).',
    integer: 'Lütfen bu alana sadece tamsayı girin. Ondalıklı sayılar (ör: 1.25) kullanılamaz.',
    numeric: 'Lütfen bu alana sadece sayısal değer girin (ör: "1", "1.1", "-1" ya da "-1.1").',
    digits: 'Lütfen bu alana sadece sayısal değer ve noktalama işareti girin (örneğin, nokta ve tire içeren bir telefon numarası kullanılabilir).',
    alpha: 'Lütfen bu alanda yalnızca harf kullanın. Boşluk ve diğer karakterler kullanılamaz.',
    alphanum: 'Lütfen bu alanda sadece harf ve rakam kullanın. Boşluk ve diğer karakterler kullanılamaz.',
    dateSuchAs: 'Lütfen geçerli bir tarih girin (Ör: {date})',
    dateInFormatMDY: 'Lütfen geçerli bir tarih girin (GG/AA/YYYY, ör: "31/12/1999")',
    email: 'Lütfen geçerli bir email adresi girin. Ör: "kemal@etikan.com".',
    url: 'Lütfen geçerli bir URL girin. Ör: http://www.example.com.',
    currencyDollar: 'Lütfen geçerli bir TL miktarı girin. Ör: 100,00 TL .',
    oneRequired: 'Lütfen en az bir tanesini doldurun.',
    errorPrefix: 'Hata: ',
    warningPrefix: 'Uyarı: ',

    // Form.Validator.Extras
    noSpace: 'Bu alanda boşluk kullanılamaz.',
    reqChkByNode: 'Hiçbir öğe seçilmemiş.',
    requiredChk: 'Bu alan zorunlu.',
    reqChkByName: 'Lütfen bir {label} girin.',
    match: 'Bu alan, {matchName} alanıyla uyuşmalı',
    startDate: 'başlangıç tarihi',
    endDate: 'bitiş tarihi',
    currentDate: 'bugünün tarihi',
    afterDate: 'Tarih, {label} tarihiyle aynı gün ya da ondan sonra olmalıdır.',
    beforeDate: 'Tarih, {label} tarihiyle aynı gün ya da ondan önce olmalıdır.',
    startMonth: 'Lütfen bir başlangıç ayı seçin',
    sameMonth: 'Bu iki tarih aynı ayda olmalı - bir tanesini değiştirmeniz gerekiyor.',
    creditcard: 'Girdiğiniz kredi kartı numarası geçersiz. Lütfen kontrol edip tekrar deneyin. {length} hane girildi.'

});

/*
---

name: Locale.tr-TR.Number

description: Number messages for Turkish.

license: MIT-style license

authors:
  - Faruk Can Bilir

requires:
  - Locale
  - Locale.EU.Number

provides: [Locale.tr-TR.Number]

...
*/

Locale.define('tr-TR', 'Number', {

    currency: {
        decimals: 0,
        suffix: ' TL'
    }

}).inherit('EU', 'Number');

/*
---

name: Locale.uk-UA.Date

description: Date messages for Ukrainian (utf-8).

license: MIT-style license

authors:
  - Slik

requires:
  - Locale

provides: [Locale.uk-UA.Date]

...
*/

(function(){

    var pluralize = function(n, one, few, many, other){
        var d = (n / 10).toInt(),
            z = n % 10,
            s = (n / 100).toInt();

        if (d == 1 && n > 10) return many;
        if (z == 1) return one;
        if (z > 0 && z < 5) return few;
        return many;
    };

    Locale.define('uk-UA', 'Date', {

        months: ['Січень', 'Лютий', 'Березень', 'Квітень', 'Травень', 'Червень', 'Липень', 'Серпень', 'Вересень', 'Жовтень', 'Листопад', 'Грудень'],
        months_abbr: ['Січ', 'Лют', 'Бер', 'Квіт', 'Трав', 'Черв', 'Лип', 'Серп', 'Вер', 'Жовт', 'Лист', 'Груд' ],
        days: ['Неділя', 'Понеділок', 'Вівторок', 'Середа', 'Четвер', "П'ятниця", 'Субота'],
        days_abbr: ['Нд', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб'],

        // Culture's date order: DD/MM/YYYY
        dateOrder: ['date', 'month', 'year'],
        shortDate: '%d/%m/%Y',
        shortTime: '%H:%M',
        AM: 'до полудня',
        PM: 'по полудню',
        firstDayOfWeek: 1,

        // Date.Extras
        ordinal: '',

        lessThanMinuteAgo: 'меньше хвилини тому',
        minuteAgo: 'хвилину тому',
        minutesAgo: function(delta){ return '{delta} ' + pluralize(delta, 'хвилину', 'хвилини', 'хвилин') + ' тому'; },
        hourAgo: 'годину тому',
        hoursAgo: function(delta){ return '{delta} ' + pluralize(delta, 'годину', 'години', 'годин') + ' тому'; },
        dayAgo: 'вчора',
        daysAgo: function(delta){ return '{delta} ' + pluralize(delta, 'день', 'дня', 'днів') + ' тому'; },
        weekAgo: 'тиждень тому',
        weeksAgo: function(delta){ return '{delta} ' + pluralize(delta, 'тиждень', 'тижні', 'тижнів') + ' тому'; },
        monthAgo: 'місяць тому',
        monthsAgo: function(delta){ return '{delta} ' + pluralize(delta, 'місяць', 'місяці', 'місяців') + ' тому'; },
        yearAgo: 'рік тому',
        yearsAgo: function(delta){ return '{delta} ' + pluralize(delta, 'рік', 'роки', 'років') + ' тому'; },

        lessThanMinuteUntil: 'за мить',
        minuteUntil: 'через хвилину',
        minutesUntil: function(delta){ return 'через {delta} ' + pluralize(delta, 'хвилину', 'хвилини', 'хвилин'); },
        hourUntil: 'через годину',
        hoursUntil: function(delta){ return 'через {delta} ' + pluralize(delta, 'годину', 'години', 'годин'); },
        dayUntil: 'завтра',
        daysUntil: function(delta){ return 'через {delta} ' + pluralize(delta, 'день', 'дня', 'днів'); },
        weekUntil: 'через тиждень',
        weeksUntil: function(delta){ return 'через {delta} ' + pluralize(delta, 'тиждень', 'тижні', 'тижнів'); },
        monthUntil: 'через місяць',
        monthesUntil: function(delta){ return 'через {delta} ' + pluralize(delta, 'місяць', 'місяці', 'місяців'); },
        yearUntil: 'через рік',
        yearsUntil: function(delta){ return 'через {delta} ' + pluralize(delta, 'рік', 'роки', 'років'); }

    });

})();

/*
---

name: Locale.uk-UA.Form.Validator

description: Form Validator messages for Ukrainian (utf-8).

license: MIT-style license

authors:
  - Slik

requires:
  - Locale

provides: [Locale.uk-UA.Form.Validator]

...
*/

Locale.define('uk-UA', 'FormValidator', {

    required: 'Це поле повинне бути заповненим.',
    minLength: 'Введіть хоча б {minLength} символів (Ви ввели {length}).',
    maxLength: 'Кількість символів не може бути більше {maxLength} (Ви ввели {length}).',
    integer: 'Введіть в це поле число. Дробові числа (наприклад 1.25) не дозволені.',
    numeric: 'Введіть в це поле число (наприклад "1" або "1.1", або "-1", або "-1.1").',
    digits: 'В цьому полі ви можете використовувати лише цифри і знаки пунктіації (наприклад, телефонний номер з знаками дефізу або з крапками).',
    alpha: 'В цьому полі можна використовувати лише латинські літери (a-z). Пробіли і інші символи заборонені.',
    alphanum: 'В цьому полі можна використовувати лише латинські літери (a-z) і цифри (0-9). Пробіли і інші символи заборонені.',
    dateSuchAs: 'Введіть коректну дату {date}.',
    dateInFormatMDY: 'Введіть дату в форматі ММ/ДД/РРРР (наприклад "12/31/2009").',
    email: 'Введіть коректну адресу електронної пошти (наприклад "name@domain.com").',
    url: 'Введіть коректне інтернет-посилання (наприклад http://www.example.com).',
    currencyDollar: 'Введіть суму в доларах (наприклад "$100.00").',
    oneRequired: 'Заповніть одне з полів.',
    errorPrefix: 'Помилка: ',
    warningPrefix: 'Увага: ',

    noSpace: 'Пробіли заборонені.',
    reqChkByNode: 'Не відмічено жодного варіанту.',
    requiredChk: 'Це поле повинне бути віміченим.',
    reqChkByName: 'Будь ласка, відмітьте {label}.',
    match: 'Це поле повинно відповідати {matchName}',
    startDate: 'початкова дата',
    endDate: 'кінцева дата',
    currentDate: 'сьогоднішня дата',
    afterDate: 'Ця дата повинна бути такою ж, або пізнішою за {label}.',
    beforeDate: 'Ця дата повинна бути такою ж, або ранішою за {label}.',
    startMonth: 'Будь ласка, виберіть початковий місяць',
    sameMonth: 'Ці дати повинні відноситись одного і того ж місяця. Будь ласка, змініть одну з них.',
    creditcard: 'Номер кредитної карти введений неправильно. Будь ласка, перевірте його. Введено {length} символів.'

});

/*
---

name: Locale.zh-CH.Date

description: Date messages for Chinese (simplified and traditional).

license: MIT-style license

authors:
  - YMind Chan

requires:
  - Locale

provides: [Locale.zh-CH.Date]

...
*/

// Simplified Chinese
Locale.define('zh-CHS', 'Date', {

    months: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
    months_abbr: ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '十一', '十二'],
    days: ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'],
    days_abbr: ['日', '一', '二', '三', '四', '五', '六'],

    // Culture's date order: YYYY-MM-DD
    dateOrder: ['year', 'month', 'date'],
    shortDate: '%Y-%m-%d',
    shortTime: '%I:%M%p',
    AM: 'AM',
    PM: 'PM',
    firstDayOfWeek: 1,

    // Date.Extras
    ordinal: '',

    lessThanMinuteAgo: '不到1分钟前',
    minuteAgo: '大约1分钟前',
    minutesAgo: '{delta}分钟之前',
    hourAgo: '大约1小时前',
    hoursAgo: '大约{delta}小时前',
    dayAgo: '1天前',
    daysAgo: '{delta}天前',
    weekAgo: '1星期前',
    weeksAgo: '{delta}星期前',
    monthAgo: '1个月前',
    monthsAgo: '{delta}个月前',
    yearAgo: '1年前',
    yearsAgo: '{delta}年前',

    lessThanMinuteUntil: '从现在开始不到1分钟',
    minuteUntil: '从现在开始約1分钟',
    minutesUntil: '从现在开始约{delta}分钟',
    hourUntil: '从现在开始1小时',
    hoursUntil: '从现在开始约{delta}小时',
    dayUntil: '从现在开始1天',
    daysUntil: '从现在开始{delta}天',
    weekUntil: '从现在开始1星期',
    weeksUntil: '从现在开始{delta}星期',
    monthUntil: '从现在开始一个月',
    monthsUntil: '从现在开始{delta}个月',
    yearUntil: '从现在开始1年',
    yearsUntil: '从现在开始{delta}年'

});

// Traditional Chinese
Locale.define('zh-CHT', 'Date', {

    months: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
    months_abbr: ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '十一', '十二'],
    days: ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'],
    days_abbr: ['日', '一', '二', '三', '四', '五', '六'],

    // Culture's date order: YYYY-MM-DD
    dateOrder: ['year', 'month', 'date'],
    shortDate: '%Y-%m-%d',
    shortTime: '%I:%M%p',
    AM: 'AM',
    PM: 'PM',
    firstDayOfWeek: 1,

    // Date.Extras
    ordinal: '',

    lessThanMinuteAgo: '不到1分鐘前',
    minuteAgo: '大約1分鐘前',
    minutesAgo: '{delta}分鐘之前',
    hourAgo: '大約1小時前',
    hoursAgo: '大約{delta}小時前',
    dayAgo: '1天前',
    daysAgo: '{delta}天前',
    weekAgo: '1星期前',
    weeksAgo: '{delta}星期前',
    monthAgo: '1个月前',
    monthsAgo: '{delta}个月前',
    yearAgo: '1年前',
    yearsAgo: '{delta}年前',

    lessThanMinuteUntil: '從現在開始不到1分鐘',
    minuteUntil: '從現在開始約1分鐘',
    minutesUntil: '從現在開始約{delta}分鐘',
    hourUntil: '從現在開始1小時',
    hoursUntil: '從現在開始約{delta}小時',
    dayUntil: '從現在開始1天',
    daysUntil: '從現在開始{delta}天',
    weekUntil: '從現在開始1星期',
    weeksUntil: '從現在開始{delta}星期',
    monthUntil: '從現在開始一個月',
    monthsUntil: '從現在開始{delta}個月',
    yearUntil: '從現在開始1年',
    yearsUntil: '從現在開始{delta}年'

});

/*
---

name: Locale.zh-CH.Form.Validator

description: Form Validator messages for Chinese (simplified and traditional).

license: MIT-style license

authors:
  - YMind Chan

requires:
  - Locale
  - Form.Validator

provides: [Form.zh-CH.Form.Validator, Form.Validator.CurrencyYuanValidator]

...
*/

// Simplified Chinese
Locale.define('zh-CHS', 'FormValidator', {

    required: '此项必填。',
    minLength: '请至少输入 {minLength} 个字符 (已输入 {length} 个)。',
    maxLength: '最多只能输入 {maxLength} 个字符 (已输入 {length} 个)。',
    integer: '请输入一个整数，不能包含小数点。例如："1", "200"。',
    numeric: '请输入一个数字，例如："1", "1.1", "-1", "-1.1"。',
    digits: '请输入由数字和标点符号组成的内容。例如电话号码。',
    alpha: '请输入 A-Z 的 26 个字母，不能包含空格或任何其他字符。',
    alphanum: '请输入 A-Z 的 26 个字母或 0-9 的 10 个数字，不能包含空格或任何其他字符。',
    dateSuchAs: '请输入合法的日期格式，如：{date}。',
    dateInFormatMDY: '请输入合法的日期格式，例如：YYYY-MM-DD ("2010-12-31")。',
    email: '请输入合法的电子信箱地址，例如："fred@domain.com"。',
    url: '请输入合法的 Url 地址，例如：http://www.example.com。',
    currencyDollar: '请输入合法的货币符号，例如：￥100.0',
    oneRequired: '请至少选择一项。',
    errorPrefix: '错误：',
    warningPrefix: '警告：',

    // Form.Validator.Extras
    noSpace: '不能包含空格。',
    reqChkByNode: '未选择任何内容。',
    requiredChk: '此项必填。',
    reqChkByName: '请选择 {label}.',
    match: '必须与{matchName}相匹配',
    startDate: '起始日期',
    endDate: '结束日期',
    currentDate: '当前日期',
    afterDate: '日期必须等于或晚于 {label}.',
    beforeDate: '日期必须早于或等于 {label}.',
    startMonth: '请选择起始月份',
    sameMonth: '您必须修改两个日期中的一个，以确保它们在同一月份。',
    creditcard: '您输入的信用卡号码不正确。当前已输入{length}个字符。'

});

// Traditional Chinese
Locale.define('zh-CHT', 'FormValidator', {

    required: '此項必填。 ',
    minLength: '請至少輸入{minLength} 個字符(已輸入{length} 個)。 ',
    maxLength: '最多只能輸入{maxLength} 個字符(已輸入{length} 個)。 ',
    integer: '請輸入一個整數，不能包含小數點。例如："1", "200"。 ',
    numeric: '請輸入一個數字，例如："1", "1.1", "-1", "-1.1"。 ',
    digits: '請輸入由數字和標點符號組成的內容。例如電話號碼。 ',
    alpha: '請輸入AZ 的26 個字母，不能包含空格或任何其他字符。 ',
    alphanum: '請輸入AZ 的26 個字母或0-9 的10 個數字，不能包含空格或任何其他字符。 ',
    dateSuchAs: '請輸入合法的日期格式，如：{date}。 ',
    dateInFormatMDY: '請輸入合法的日期格式，例如：YYYY-MM-DD ("2010-12-31")。 ',
    email: '請輸入合法的電子信箱地址，例如："fred@domain.com"。 ',
    url: '請輸入合法的Url 地址，例如：http://www.example.com。 ',
    currencyDollar: '請輸入合法的貨幣符號，例如：￥100.0',
    oneRequired: '請至少選擇一項。 ',
    errorPrefix: '錯誤：',
    warningPrefix: '警告：',

    // Form.Validator.Extras
    noSpace: '不能包含空格。 ',
    reqChkByNode: '未選擇任何內容。 ',
    requiredChk: '此項必填。 ',
    reqChkByName: '請選擇 {label}.',
    match: '必須與{matchName}相匹配',
    startDate: '起始日期',
    endDate: '結束日期',
    currentDate: '當前日期',
    afterDate: '日期必須等於或晚於{label}.',
    beforeDate: '日期必須早於或等於{label}.',
    startMonth: '請選擇起始月份',
    sameMonth: '您必須修改兩個日期中的一個，以確保它們在同一月份。 ',
    creditcard: '您輸入的信用卡號碼不正確。當前已輸入{length}個字符。 '

});

Form.Validator.add('validate-currency-yuan', {

    errorMsg: function(){
        return Form.Validator.getMsg('currencyYuan');
    },

    test: function(element){
        // [￥]1[##][,###]+[.##]
        // [￥]1###+[.##]
        // [￥]0.##
        // [￥].##
        return Form.Validator.getValidator('IsEmpty').test(element) || (/^￥?\-?([1-9]{1}[0-9]{0,2}(\,[0-9]{3})*(\.[0-9]{0,2})?|[1-9]{1}\d*(\.[0-9]{0,2})?|0(\.[0-9]{0,2})?|(\.[0-9]{1,2})?)$/).test(element.get('value'));
    }

});

/*
---

name: Locale.zh-CH.Number

description: Number messages for for Chinese (simplified and traditional).

license: MIT-style license

authors:
  - YMind Chan

requires:
  - Locale
  - Locale.en-US.Number

provides: [Locale.zh-CH.Number]

...
*/

// Simplified Chinese
Locale.define('zh-CHS', 'Number', {

    currency: {
        prefix: '￥ '
    }

}).inherit('en-US', 'Number');

// Traditional Chinese
Locale.define('zh-CHT').inherit('zh-CHS', 'Number');

/*
---

script: Request.JSONP.js

name: Request.JSONP

description: Defines Request.JSONP, a class for cross domain javascript via script injection.

license: MIT-style license

authors:
  - Aaron Newton
  - Guillermo Rauch
  - Arian Stolwijk

requires:
  - Core/Element
  - Core/Request
  - MooTools.More

provides: [Request.JSONP]

...
*/

Request.JSONP = new Class({

    Implements: [Chain, Events, Options],

    options: {/*
		onRequest: function(src, scriptElement){},
		onComplete: function(data){},
		onSuccess: function(data){},
		onCancel: function(){},
		onTimeout: function(){},
		onError: function(){}, */
        onRequest: function(src){
            if (this.options.log && window.console && console.log){
                console.log('JSONP retrieving script with url:' + src);
            }
        },
        onError: function(src){
            if (this.options.log && window.console && console.warn){
                console.warn('JSONP '+ src +' will fail in Internet Explorer, which enforces a 2083 bytes length limit on URIs');
            }
        },
        url: '',
        callbackKey: 'callback',
        injectScript: document.head,
        data: '',
        link: 'ignore',
        timeout: 0,
        log: false
    },

    initialize: function(options){
        this.setOptions(options);
    },

    send: function(options){
        if (!Request.prototype.check.call(this, options)) return this;
        this.running = true;

        var type = typeOf(options);
        if (type == 'string' || type == 'element') options = {data: options};
        options = Object.merge(this.options, options || {});

        var data = options.data;
        switch (typeOf(data)){
            case 'element': data = document.id(data).toQueryString(); break;
            case 'object': case 'hash': data = Object.toQueryString(data);
        }

        var index = this.index = Request.JSONP.counter++,
            key = 'request_' + index;

        var src = options.url +
            (options.url.test('\\?') ? '&' :'?') +
            (options.callbackKey) +
            '=Request.JSONP.request_map.request_'+ index +
            (data ? '&' + data : '');

        if (src.length > 2083) this.fireEvent('error', src);

        Request.JSONP.request_map[key] = function(){
            delete Request.JSONP.request_map[key];
            this.success(arguments, index);
        }.bind(this);

        var script = this.getScript(src).inject(options.injectScript);
        this.fireEvent('request', [src, script]);

        if (options.timeout) this.timeout.delay(options.timeout, this);

        return this;
    },

    getScript: function(src){
        if (!this.script) this.script = new Element('script', {
            type: 'text/javascript',
            async: true,
            src: src
        });
        return this.script;
    },

    success: function(args){
        if (!this.running) return;
        this.clear()
            .fireEvent('complete', args).fireEvent('success', args)
            .callChain();
    },

    cancel: function(){
        if (this.running) this.clear().fireEvent('cancel');
        return this;
    },

    isRunning: function(){
        return !!this.running;
    },

    clear: function(){
        this.running = false;
        if (this.script){
            this.script.destroy();
            this.script = null;
        }
        return this;
    },

    timeout: function(){
        if (this.running){
            this.running = false;
            this.fireEvent('timeout', [this.script.get('src'), this.script]).fireEvent('failure').cancel();
        }
        return this;
    }

});

Request.JSONP.counter = 0;
Request.JSONP.request_map = {};

/*
---

script: Request.Periodical.js

name: Request.Periodical

description: Requests the same URL to pull data from a server but increases the intervals if no data is returned to reduce the load

license: MIT-style license

authors:
  - Christoph Pojer

requires:
  - Core/Request
  - MooTools.More

provides: [Request.Periodical]

...
*/

Request.implement({

    options: {
        initialDelay: 5000,
        delay: 5000,
        limit: 60000
    },

    startTimer: function(data){
        var fn = function(){
            if (!this.running) this.send({data: data});
        };
        this.lastDelay = this.options.initialDelay;
        this.timer = fn.delay(this.lastDelay, this);
        this.completeCheck = function(response){
            clearTimeout(this.timer);
            this.lastDelay = (response) ? this.options.delay : (this.lastDelay + this.options.delay).min(this.options.limit);
            this.timer = fn.delay(this.lastDelay, this);
        };
        return this.addEvent('complete', this.completeCheck);
    },

    stopTimer: function(){
        clearTimeout(this.timer);
        return this.removeEvent('complete', this.completeCheck);
    }

});

/*
---

script: Request.Queue.js

name: Request.Queue

description: Controls several instances of Request and its variants to run only one request at a time.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Core/Element
  - Core/Request
  - Class.Binds

provides: [Request.Queue]

...
*/

Request.Queue = new Class({

    Implements: [Options, Events],

    Binds: ['attach', 'request', 'complete', 'cancel', 'success', 'failure', 'exception'],

    options: {/*
		onRequest: function(argsPassedToOnRequest){},
		onSuccess: function(argsPassedToOnSuccess){},
		onComplete: function(argsPassedToOnComplete){},
		onCancel: function(argsPassedToOnCancel){},
		onException: function(argsPassedToOnException){},
		onFailure: function(argsPassedToOnFailure){},
		onEnd: function(){},
		*/
        stopOnFailure: true,
        autoAdvance: true,
        concurrent: 1,
        requests: {}
    },

    initialize: function(options){
        var requests;
        if (options){
            requests = options.requests;
            delete options.requests;
        }
        this.setOptions(options);
        this.requests = {};
        this.queue = [];
        this.reqBinders = {};

        if (requests) this.addRequests(requests);
    },

    addRequest: function(name, request){
        this.requests[name] = request;
        this.attach(name, request);
        return this;
    },

    addRequests: function(obj){
        Object.each(obj, function(req, name){
            this.addRequest(name, req);
        }, this);
        return this;
    },

    getName: function(req){
        return Object.keyOf(this.requests, req);
    },

    attach: function(name, req){
        if (req._groupSend) return this;
        ['request', 'complete', 'cancel', 'success', 'failure', 'exception'].each(function(evt){
            if (!this.reqBinders[name]) this.reqBinders[name] = {};
            this.reqBinders[name][evt] = function(){
                this['on' + evt.capitalize()].apply(this, [name, req].append(arguments));
            }.bind(this);
            req.addEvent(evt, this.reqBinders[name][evt]);
        }, this);
        req._groupSend = req.send;
        req.send = function(options){
            this.send(name, options);
            return req;
        }.bind(this);
        return this;
    },

    removeRequest: function(req){
        var name = typeOf(req) == 'object' ? this.getName(req) : req;
        if (!name && typeOf(name) != 'string') return this;
        req = this.requests[name];
        if (!req) return this;
        ['request', 'complete', 'cancel', 'success', 'failure', 'exception'].each(function(evt){
            req.removeEvent(evt, this.reqBinders[name][evt]);
        }, this);
        req.send = req._groupSend;
        delete req._groupSend;
        return this;
    },

    getRunning: function(){
        return Object.filter(this.requests, function(r){
            return r.running;
        });
    },

    isRunning: function(){
        return !!(Object.keys(this.getRunning()).length);
    },

    send: function(name, options){
        var q = function(){
            this.requests[name]._groupSend(options);
            this.queue.erase(q);
        }.bind(this);

        q.name = name;
        if (Object.keys(this.getRunning()).length >= this.options.concurrent || (this.error && this.options.stopOnFailure)) this.queue.push(q);
        else q();
        return this;
    },

    hasNext: function(name){
        return (!name) ? !!this.queue.length : !!this.queue.filter(function(q){ return q.name == name; }).length;
    },

    resume: function(){
        this.error = false;
        (this.options.concurrent - Object.keys(this.getRunning()).length).times(this.runNext, this);
        return this;
    },

    runNext: function(name){
        if (!this.queue.length) return this;
        if (!name){
            this.queue[0]();
        } else {
            var found;
            this.queue.each(function(q){
                if (!found && q.name == name){
                    found = true;
                    q();
                }
            });
        }
        return this;
    },

    runAll: function(){
        this.queue.each(function(q){
            q();
        });
        return this;
    },

    clear: function(name){
        if (!name){
            this.queue.empty();
        } else {
            this.queue = this.queue.map(function(q){
                if (q.name != name) return q;
                else return false;
            }).filter(function(q){
                return q;
            });
        }
        return this;
    },

    cancel: function(name){
        this.requests[name].cancel();
        return this;
    },

    onRequest: function(){
        this.fireEvent('request', arguments);
    },

    onComplete: function(){
        this.fireEvent('complete', arguments);
        if (!this.queue.length) this.fireEvent('end');
    },

    onCancel: function(){
        if (this.options.autoAdvance && !this.error) this.runNext();
        this.fireEvent('cancel', arguments);
    },

    onSuccess: function(){
        if (this.options.autoAdvance && !this.error) this.runNext();
        this.fireEvent('success', arguments);
    },

    onFailure: function(){
        this.error = true;
        if (!this.options.stopOnFailure && this.options.autoAdvance) this.runNext();
        this.fireEvent('failure', arguments);
    },

    onException: function(){
        this.error = true;
        if (!this.options.stopOnFailure && this.options.autoAdvance) this.runNext();
        this.fireEvent('exception', arguments);
    }

});

/*
---

script: Array.Extras.js

name: Array.Extras

description: Extends the Array native object to include useful methods to work with arrays.

license: MIT-style license

authors:
  - Christoph Pojer
  - Sebastian Markbåge

requires:
  - Core/Array
  - MooTools.More

provides: [Array.Extras]

...
*/

(function(nil){

    Array.implement({

        min: function(){
            return Math.min.apply(null, this);
        },

        max: function(){
            return Math.max.apply(null, this);
        },

        average: function(){
            return this.length ? this.sum() / this.length : 0;
        },

        sum: function(){
            var result = 0, l = this.length;
            if (l){
                while (l--){
                    if (this[l] != null) result += parseFloat(this[l]);
                }
            }
            return result;
        },

        unique: function(){
            return [].combine(this);
        },

        shuffle: function(){
            for (var i = this.length; i && --i;){
                var temp = this[i], r = Math.floor(Math.random() * ( i + 1 ));
                this[i] = this[r];
                this[r] = temp;
            }
            return this;
        },

        reduce: function(fn, value){
            for (var i = 0, l = this.length; i < l; i++){
                if (i in this) value = value === nil ? this[i] : fn.call(null, value, this[i], i, this);
            }
            return value;
        },

        reduceRight: function(fn, value){
            var i = this.length;
            while (i--){
                if (i in this) value = value === nil ? this[i] : fn.call(null, value, this[i], i, this);
            }
            return value;
        },

        pluck: function(prop){
            return this.map(function(item){
                return item[prop];
            });
        }

    });

})();

/*
---

script: Date.Extras.js

name: Date.Extras

description: Extends the Date native object to include extra methods (on top of those in Date.js).

license: MIT-style license

authors:
  - Aaron Newton
  - Scott Kyle

requires:
  - Date

provides: [Date.Extras]

...
*/

Date.implement({

    timeDiffInWords: function(to){
        return Date.distanceOfTimeInWords(this, to || new Date);
    },

    timeDiff: function(to, separator){
        if (to == null) to = new Date;
        var delta = ((to - this) / 1000).floor().abs();

        var vals = [],
            durations = [60, 60, 24, 365, 0],
            names = ['s', 'm', 'h', 'd', 'y'],
            value, duration;

        for (var item = 0; item < durations.length; item++){
            if (item && !delta) break;
            value = delta;
            if ((duration = durations[item])){
                value = (delta % duration);
                delta = (delta / duration).floor();
            }
            vals.unshift(value + (names[item] || ''));
        }

        return vals.join(separator || ':');
    }

}).extend({

    distanceOfTimeInWords: function(from, to){
        return Date.getTimePhrase(((to - from) / 1000).toInt());
    },

    getTimePhrase: function(delta){
        var suffix = (delta < 0) ? 'Until' : 'Ago';
        if (delta < 0) delta *= -1;

        var units = {
            minute: 60,
            hour: 60,
            day: 24,
            week: 7,
            month: 52 / 12,
            year: 12,
            eon: Infinity
        };

        var msg = 'lessThanMinute';

        for (var unit in units){
            var interval = units[unit];
            if (delta < 1.5 * interval){
                if (delta > 0.75 * interval) msg = unit;
                break;
            }
            delta /= interval;
            msg = unit + 's';
        }

        delta = delta.round();
        return Date.getMsg(msg + suffix, delta).substitute({delta: delta});
    }

}).defineParsers(

    {
        // "today", "tomorrow", "yesterday"
        re: /^(?:tod|tom|yes)/i,
        handler: function(bits){
            var d = new Date().clearTime();
            switch (bits[0]){
                case 'tom': return d.increment();
                case 'yes': return d.decrement();
                default: return d;
            }
        }
    },

    {
        // "next Wednesday", "last Thursday"
        re: /^(next|last) ([a-z]+)$/i,
        handler: function(bits){
            var d = new Date().clearTime();
            var day = d.getDay();
            var newDay = Date.parseDay(bits[2], true);
            var addDays = newDay - day;
            if (newDay <= day) addDays += 7;
            if (bits[1] == 'last') addDays -= 7;
            return d.set('date', d.getDate() + addDays);
        }
    }

).alias('timeAgoInWords', 'timeDiffInWords');

/*
---

name: Hash

description: Contains Hash Prototypes. Provides a means for overcoming the JavaScript practical impossibility of extending native Objects.

license: MIT-style license.

requires:
  - Core/Object
  - MooTools.More

provides: [Hash]

...
*/

(function(){

    if (this.Hash) return;

    var Hash = this.Hash = new Type('Hash', function(object){
        if (typeOf(object) == 'hash') object = Object.clone(object.getClean());
        for (var key in object) this[key] = object[key];
        return this;
    });

    this.$H = function(object){
        return new Hash(object);
    };

    Hash.implement({

        forEach: function(fn, bind){
            Object.forEach(this, fn, bind);
        },

        getClean: function(){
            var clean = {};
            for (var key in this){
                if (this.hasOwnProperty(key)) clean[key] = this[key];
            }
            return clean;
        },

        getLength: function(){
            var length = 0;
            for (var key in this){
                if (this.hasOwnProperty(key)) length++;
            }
            return length;
        }

    });

    Hash.alias('each', 'forEach');

    Hash.implement({

        has: Object.prototype.hasOwnProperty,

        keyOf: function(value){
            return Object.keyOf(this, value);
        },

        hasValue: function(value){
            return Object.contains(this, value);
        },

        extend: function(properties){
            Hash.each(properties || {}, function(value, key){
                Hash.set(this, key, value);
            }, this);
            return this;
        },

        combine: function(properties){
            Hash.each(properties || {}, function(value, key){
                Hash.include(this, key, value);
            }, this);
            return this;
        },

        erase: function(key){
            if (this.hasOwnProperty(key)) delete this[key];
            return this;
        },

        get: function(key){
            return (this.hasOwnProperty(key)) ? this[key] : null;
        },

        set: function(key, value){
            if (!this[key] || this.hasOwnProperty(key)) this[key] = value;
            return this;
        },

        empty: function(){
            Hash.each(this, function(value, key){
                delete this[key];
            }, this);
            return this;
        },

        include: function(key, value){
            if (this[key] == undefined) this[key] = value;
            return this;
        },

        map: function(fn, bind){
            return new Hash(Object.map(this, fn, bind));
        },

        filter: function(fn, bind){
            return new Hash(Object.filter(this, fn, bind));
        },

        every: function(fn, bind){
            return Object.every(this, fn, bind);
        },

        some: function(fn, bind){
            return Object.some(this, fn, bind);
        },

        getKeys: function(){
            return Object.keys(this);
        },

        getValues: function(){
            return Object.values(this);
        },

        toQueryString: function(base){
            return Object.toQueryString(this, base);
        }

    });

    Hash.alias({indexOf: 'keyOf', contains: 'hasValue'});


})();


/*
---

script: Hash.Extras.js

name: Hash.Extras

description: Extends the Hash Type to include getFromPath which allows a path notation to child elements.

license: MIT-style license

authors:
  - Aaron Newton

requires:
  - Hash
  - Object.Extras

provides: [Hash.Extras]

...
*/

Hash.implement({

    getFromPath: function(notation){
        return Object.getFromPath(this, notation);
    },

    cleanValues: function(method){
        return new Hash(Object.cleanValues(this, method));
    },

    run: function(){
        Object.run(arguments);
    }

});

/*
---
name: Number.Format
description: Extends the Number Type object to include a number formatting method.
license: MIT-style license
authors: [Arian Stolwijk]
requires: [Core/Number, Locale.en-US.Number]
# Number.Extras is for compatibility
provides: [Number.Format, Number.Extras]
...
*/


Number.implement({

    format: function(options){
        // Thanks dojo and YUI for some inspiration
        var value = this;
        options = options ? Object.clone(options) : {};
        var getOption = function(key){
            if (options[key] != null) return options[key];
            return Locale.get('Number.' + key);
        };

        var negative = value < 0,
            decimal = getOption('decimal'),
            precision = getOption('precision'),
            group = getOption('group'),
            decimals = getOption('decimals');

        if (negative){
            var negativeLocale = getOption('negative') || {};
            if (negativeLocale.prefix == null && negativeLocale.suffix == null) negativeLocale.prefix = '-';
            ['prefix', 'suffix'].each(function(key){
                if (negativeLocale[key]) options[key] = getOption(key) + negativeLocale[key];
            });

            value = -value;
        }

        var prefix = getOption('prefix'),
            suffix = getOption('suffix');

        if (decimals !== '' && decimals >= 0 && decimals <= 20) value = value.toFixed(decimals);
        if (precision >= 1 && precision <= 21) value = (+value).toPrecision(precision);

        value += '';
        var index;
        if (getOption('scientific') === false && value.indexOf('e') > -1){
            var match = value.split('e'),
                zeros = +match[1];
            value = match[0].replace('.', '');

            if (zeros < 0){
                zeros = -zeros - 1;
                index = match[0].indexOf('.');
                if (index > -1) zeros -= index - 1;
                while (zeros--) value = '0' + value;
                value = '0.' + value;
            } else {
                index = match[0].lastIndexOf('.');
                if (index > -1) zeros -= match[0].length - index - 1;
                while (zeros--) value += '0';
            }
        }

        if (decimal != '.') value = value.replace('.', decimal);

        if (group){
            index = value.lastIndexOf(decimal);
            index = (index > -1) ? index : value.length;
            var newOutput = value.substring(index),
                i = index;

            while (i--){
                if ((index - i - 1) % 3 == 0 && i != (index - 1)) newOutput = group + newOutput;
                newOutput = value.charAt(i) + newOutput;
            }

            value = newOutput;
        }

        if (prefix) value = prefix + value;
        if (suffix) value += suffix;

        return value;
    },

    formatCurrency: function(decimals){
        var locale = Locale.get('Number.currency') || {};
        if (locale.scientific == null) locale.scientific = false;
        locale.decimals = decimals != null ? decimals
            : (locale.decimals == null ? 2 : locale.decimals);

        return this.format(locale);
    },

    formatPercentage: function(decimals){
        var locale = Locale.get('Number.percentage') || {};
        if (locale.suffix == null) locale.suffix = '%';
        locale.decimals = decimals != null ? decimals
            : (locale.decimals == null ? 2 : locale.decimals);

        return this.format(locale);
    }

});

/*
---

script: URI.js

name: URI

description: Provides methods useful in managing the window location and uris.

license: MIT-style license

authors:
  - Sebastian Markbåge
  - Aaron Newton

requires:
  - Core/Object
  - Core/Class
  - Core/Class.Extras
  - Core/Element
  - String.QueryString

provides: [URI]

...
*/

(function(){

    var toString = function(){
        return this.get('value');
    };

    var URI = this.URI = new Class({

        Implements: Options,

        options: {
            /*base: false*/
        },

        regex: /^(?:(\w+):)?(?:\/\/(?:(?:([^:@\/]*):?([^:@\/]*))?@)?(\[[A-Fa-f0-9:]+\]|[^:\/?#]*)(?::(\d*))?)?(\.\.?$|(?:[^?#\/]*\/)*)([^?#]*)(?:\?([^#]*))?(?:#(.*))?/,
        parts: ['scheme', 'user', 'password', 'host', 'port', 'directory', 'file', 'query', 'fragment'],
        schemes: {http: 80, https: 443, ftp: 21, rtsp: 554, mms: 1755, file: 0},

        initialize: function(uri, options){
            this.setOptions(options);
            var base = this.options.base || URI.base;
            if (!uri) uri = base;

            if (uri && uri.parsed) this.parsed = Object.clone(uri.parsed);
            else this.set('value', uri.href || uri.toString(), base ? new URI(base) : false);
        },

        parse: function(value, base){
            var bits = value.match(this.regex);
            if (!bits) return false;
            bits.shift();
            return this.merge(bits.associate(this.parts), base);
        },

        merge: function(bits, base){
            if ((!bits || !bits.scheme) && (!base || !base.scheme)) return false;
            if (base){
                this.parts.every(function(part){
                    if (bits[part]) return false;
                    bits[part] = base[part] || '';
                    return true;
                });
            }
            bits.port = bits.port || this.schemes[bits.scheme.toLowerCase()];
            bits.directory = bits.directory ? this.parseDirectory(bits.directory, base ? base.directory : '') : '/';
            return bits;
        },

        parseDirectory: function(directory, baseDirectory){
            directory = (directory.substr(0, 1) == '/' ? '' : (baseDirectory || '/')) + directory;
            if (!directory.test(URI.regs.directoryDot)) return directory;
            var result = [];
            directory.replace(URI.regs.endSlash, '').split('/').each(function(dir){
                if (dir == '..' && result.length > 0) result.pop();
                else if (dir != '.') result.push(dir);
            });
            return result.join('/') + '/';
        },

        combine: function(bits){
            return bits.value || bits.scheme + '://' +
                (bits.user ? bits.user + (bits.password ? ':' + bits.password : '') + '@' : '') +
                (bits.host || '') + (bits.port && bits.port != this.schemes[bits.scheme] ? ':' + bits.port : '') +
                (bits.directory || '/') + (bits.file || '') +
                (bits.query ? '?' + bits.query : '') +
                (bits.fragment ? '#' + bits.fragment : '');
        },

        set: function(part, value, base){
            if (part == 'value'){
                var scheme = value.match(URI.regs.scheme);
                if (scheme) scheme = scheme[1];
                if (scheme && this.schemes[scheme.toLowerCase()] == null) this.parsed = { scheme: scheme, value: value };
                else this.parsed = this.parse(value, (base || this).parsed) || (scheme ? { scheme: scheme, value: value } : { value: value });
            } else if (part == 'data'){
                this.setData(value);
            } else {
                this.parsed[part] = value;
            }
            return this;
        },

        get: function(part, base){
            switch (part){
                case 'value': return this.combine(this.parsed, base ? base.parsed : false);
                case 'data' : return this.getData();
            }
            return this.parsed[part] || '';
        },

        go: function(){
            document.location.href = this.toString();
        },

        toURI: function(){
            return this;
        },

        getData: function(key, part){
            var qs = this.get(part || 'query');
            if (!(qs || qs === 0)) return key ? null : {};
            var obj = qs.parseQueryString();
            return key ? obj[key] : obj;
        },

        setData: function(values, merge, part){
            if (typeof values == 'string'){
                var data = this.getData();
                data[arguments[0]] = arguments[1];
                values = data;
            } else if (merge){
                values = Object.merge(this.getData(null, part), values);
            }
            return this.set(part || 'query', Object.toQueryString(values));
        },

        clearData: function(part){
            return this.set(part || 'query', '');
        },

        toString: toString,
        valueOf: toString

    });

    URI.regs = {
        endSlash: /\/$/,
        scheme: /^(\w+):/,
        directoryDot: /\.\/|\.$/
    };

    URI.base = new URI(Array.convert(document.getElements('base[href]', true)).getLast(), {base: document.location});

    String.implement({

        toURI: function(options){
            return new URI(this, options);
        }

    });

})();

/*
---

script: URI.Relative.js

name: URI.Relative

description: Extends the URI class to add methods for computing relative and absolute urls.

license: MIT-style license

authors:
  - Sebastian Markbåge


requires:
  - Class.refactor
  - URI

provides: [URI.Relative]

...
*/

URI = Class.refactor(URI, {

    combine: function(bits, base){
        if (!base || bits.scheme != base.scheme || bits.host != base.host || bits.port != base.port)
            return this.previous.apply(this, arguments);
        var end = bits.file + (bits.query ? '?' + bits.query : '') + (bits.fragment ? '#' + bits.fragment : '');

        if (!base.directory) return (bits.directory || (bits.file ? '' : './')) + end;

        var baseDir = base.directory.split('/'),
            relDir = bits.directory.split('/'),
            path = '',
            offset;

        var i = 0;
        for (offset = 0; offset < baseDir.length && offset < relDir.length && baseDir[offset] == relDir[offset]; offset++);
        for (i = 0; i < baseDir.length - offset - 1; i++) path += '../';
        for (i = offset; i < relDir.length - 1; i++) path += relDir[i] + '/';

        return (path || (bits.file ? '' : './')) + end;
    },

    toAbsolute: function(base){
        base = new URI(base);
        if (base) base.set('directory', '').set('file', '');
        return this.toRelative(base);
    },

    toRelative: function(base){
        return this.get('value', new URI(base));
    }

});

/*
---

script: Assets.js

name: Assets

description: Provides methods to dynamically load JavaScript, CSS, and Image files into the document.

license: MIT-style license

authors:
  - Valerio Proietti

requires:
  - Core/Element.Event
  - MooTools.More

provides: [Assets, Asset.javascript, Asset.css, Asset.image, Asset.images]

...
*/
;(function(){

    var Asset = this.Asset = {

        javascript: function(source, properties){
            if (!properties) properties = {};

            var script = new Element('script', {src: source, type: 'text/javascript'}),
                doc = properties.document || document,
                load = properties.onload || properties.onLoad;

            delete properties.onload;
            delete properties.onLoad;
            delete properties.document;

            if (load){
                if (!script.addEventListener){
                    script.addEvent('readystatechange', function(){
                        if (['loaded', 'complete'].contains(this.readyState)) load.call(this);
                    });
                } else {
                    script.addEvent('load', load);
                }
            }

            return script.set(properties).inject(doc.head);
        },

        css: function(source, properties){
            if (!properties) properties = {};

            var load = properties.onload || properties.onLoad,
                doc = properties.document || document,
                timeout = properties.timeout || 3000;

            ['onload', 'onLoad', 'document'].each(function(prop){
                delete properties[prop];
            });

            var link = new Element('link', {
                type: 'text/css',
                rel: 'stylesheet',
                media: 'screen',
                href: source
            }).setProperties(properties).inject(doc.head);

            if (load){
                // based on article at http://www.yearofmoo.com/2011/03/cross-browser-stylesheet-preloading.html
                var loaded = false, retries = 0;
                var check = function(){
                    var stylesheets = document.styleSheets;
                    for (var i = 0; i < stylesheets.length; i++){
                        var file = stylesheets[i];
                        var owner = file.ownerNode ? file.ownerNode : file.owningElement;
                        if (owner && owner == link){
                            loaded = true;
                            return load.call(link);
                        }
                    }
                    retries++;
                    if (!loaded && retries < timeout / 50) return setTimeout(check, 50);
                };
                setTimeout(check, 0);
            }
            return link;
        },

        image: function(source, properties){
            if (!properties) properties = {};

            var image = new Image(),
                element = document.id(image) || new Element('img');

            ['load', 'abort', 'error'].each(function(name){
                var type = 'on' + name,
                    cap = 'on' + name.capitalize(),
                    event = properties[type] || properties[cap] || function(){};

                delete properties[cap];
                delete properties[type];

                image[type] = function(){
                    if (!image) return;
                    if (!element.parentNode){
                        element.width = image.width;
                        element.height = image.height;
                    }
                    image = image.onload = image.onabort = image.onerror = null;
                    event.delay(1, element, element);
                    element.fireEvent(name, element, 1);
                };
            });

            image.src = element.src = source;
            if (image && image.complete) image.onload.delay(1);
            return element.set(properties);
        },

        images: function(sources, options){
            sources = Array.convert(sources);

            var fn = function(){},
                counter = 0;

            options = Object.merge({
                onComplete: fn,
                onProgress: fn,
                onError: fn,
                properties: {}
            }, options);

            return new Elements(sources.map(function(source, index){
                return Asset.image(source, Object.append(options.properties, {
                    onload: function(){
                        counter++;
                        options.onProgress.call(this, counter, index, source);
                        if (counter == sources.length) options.onComplete();
                    },
                    onerror: function(){
                        counter++;
                        options.onError.call(this, counter, index, source);
                        if (counter == sources.length) options.onComplete();
                    }
                }));
            }));
        }

    };

})();

/*
---

script: Color.js

name: Color

description: Class for creating and manipulating colors in JavaScript. Supports HSB -> RGB Conversions and vice versa.

license: MIT-style license

authors:
  - Valerio Proietti

requires:
  - Core/Array
  - Core/String
  - Core/Number
  - Core/Hash
  - Core/Function
  - MooTools.More

provides: [Color]

...
*/

(function(){

    var Color = this.Color = new Type('Color', function(color, type){
        if (arguments.length >= 3){
            type = 'rgb'; color = Array.slice(arguments, 0, 3);
        } else if (typeof color == 'string'){
            if (color.match(/rgb/)) color = color.rgbToHex().hexToRgb(true);
            else if (color.match(/hsb/)) color = color.hsbToRgb();
            else color = color.hexToRgb(true);
        }
        type = type || 'rgb';
        switch (type){
            case 'hsb':
                var old = color;
                color = color.hsbToRgb();
                color.hsb = old;
                break;
            case 'hex': color = color.hexToRgb(true); break;
        }
        color.rgb = color.slice(0, 3);
        color.hsb = color.hsb || color.rgbToHsb();
        color.hex = color.rgbToHex();
        return Object.append(color, this);
    });

    Color.implement({

        mix: function(){
            var colors = Array.slice(arguments);
            var alpha = (typeOf(colors.getLast()) == 'number') ? colors.pop() : 50;
            var rgb = this.slice();
            colors.each(function(color){
                color = new Color(color);
                for (var i = 0; i < 3; i++) rgb[i] = Math.round((rgb[i] / 100 * (100 - alpha)) + (color[i] / 100 * alpha));
            });
            return new Color(rgb, 'rgb');
        },

        invert: function(){
            return new Color(this.map(function(value){
                return 255 - value;
            }));
        },

        setHue: function(value){
            return new Color([value, this.hsb[1], this.hsb[2]], 'hsb');
        },

        setSaturation: function(percent){
            return new Color([this.hsb[0], percent, this.hsb[2]], 'hsb');
        },

        setBrightness: function(percent){
            return new Color([this.hsb[0], this.hsb[1], percent], 'hsb');
        }

    });

    this.$RGB = function(r, g, b){
        return new Color([r, g, b], 'rgb');
    };

    this.$HSB = function(h, s, b){
        return new Color([h, s, b], 'hsb');
    };

    this.$HEX = function(hex){
        return new Color(hex, 'hex');
    };

    Array.implement({

        rgbToHsb: function(){
            var red = this[0],
                green = this[1],
                blue = this[2],
                hue = 0,
                max = Math.max(red, green, blue),
                min = Math.min(red, green, blue),
                delta = max - min,
                brightness = max / 255,
                saturation = (max != 0) ? delta / max : 0;

            if (saturation != 0){
                var rr = (max - red) / delta;
                var gr = (max - green) / delta;
                var br = (max - blue) / delta;
                if (red == max) hue = br - gr;
                else if (green == max) hue = 2 + rr - br;
                else hue = 4 + gr - rr;
                hue /= 6;
                if (hue < 0) hue++;
            }
            return [Math.round(hue * 360), Math.round(saturation * 100), Math.round(brightness * 100)];
        },

        hsbToRgb: function(){
            var br = Math.round(this[2] / 100 * 255);
            if (this[1] == 0){
                return [br, br, br];
            } else {
                var hue = this[0] % 360;
                var f = hue % 60;
                var p = Math.round((this[2] * (100 - this[1])) / 10000 * 255);
                var q = Math.round((this[2] * (6000 - this[1] * f)) / 600000 * 255);
                var t = Math.round((this[2] * (6000 - this[1] * (60 - f))) / 600000 * 255);
                switch (Math.floor(hue / 60)){
                    case 0: return [br, t, p];
                    case 1: return [q, br, p];
                    case 2: return [p, br, t];
                    case 3: return [p, q, br];
                    case 4: return [t, p, br];
                    case 5: return [br, p, q];
                }
            }
            return false;
        }

    });

    String.implement({

        rgbToHsb: function(){
            var rgb = this.match(/\d{1,3}/g);
            return (rgb) ? rgb.rgbToHsb() : null;
        },

        hsbToRgb: function(){
            var hsb = this.match(/\d{1,3}/g);
            return (hsb) ? hsb.hsbToRgb() : null;
        }

    });

})();


/*
---

script: Group.js

name: Group

description: Class for monitoring collections of events

license: MIT-style license

authors:
  - Valerio Proietti

requires:
  - Core/Events
  - MooTools.More

provides: [Group]

...
*/

(function(){

    var Group = this.Group = new Class({

        initialize: function(){
            this.instances = Array.flatten(arguments);
        },

        addEvent: function(type, fn){
            var instances = this.instances,
                len = instances.length,
                togo = len,
                args = new Array(len),
                self = this;

            instances.each(function(instance, i){
                instance.addEvent(type, function(){
                    if (!args[i]) togo--;
                    args[i] = arguments;
                    if (!togo){
                        fn.call(self, instances, instance, args);
                        togo = len;
                        args = new Array(len);
                    }
                });
            });
        }

    });

})();

/*
---

script: Hash.Cookie.js

name: Hash.Cookie

description: Class for creating, reading, and deleting Cookies in JSON format.

license: MIT-style license

authors:
  - Valerio Proietti
  - Aaron Newton

requires:
  - Core/Cookie
  - Core/JSON
  - MooTools.More
  - Hash

provides: [Hash.Cookie]

...
*/

Hash.Cookie = new Class({

    Extends: Cookie,

    options: {
        autoSave: true
    },

    initialize: function(name, options){
        this.parent(name, options);
        this.load();
    },

    save: function(){
        var value = JSON.encode(this.hash);
        if (!value || value.length > 4096) return false; //cookie would be truncated!
        if (value == '{}') this.dispose();
        else this.write(value);
        return true;
    },

    load: function(){
        this.hash = new Hash(JSON.decode(this.read(), true));
        return this;
    }

});

Hash.each(Hash.prototype, function(method, name){
    if (typeof method == 'function') Hash.Cookie.implement(name, function(){
        var value = method.apply(this.hash, arguments);
        if (this.options.autoSave) this.save();
        return value;
    });
});

/*
---

name: Swiff

description: Wrapper for embedding SWF movies. Supports External Interface Communication.

license: MIT-style license.

credits:
  - Flash detection & Internet Explorer + Flash Player 9 fix inspired by SWFObject.

requires: [Core/Options, Core/Object, Core/Element]

provides: Swiff

...
*/

(function(){

    var Swiff = this.Swiff = new Class({

        Implements: Options,

        options: {
            id: null,
            height: 1,
            width: 1,
            container: null,
            properties: {},
            params: {
                quality: 'high',
                allowScriptAccess: 'always',
                wMode: 'window',
                swLiveConnect: true
            },
            callBacks: {},
            vars: {}
        },

        toElement: function(){
            return this.object;
        },

        initialize: function(path, options){
            this.instance = 'Swiff_' + String.uniqueID();

            this.setOptions(options);
            options = this.options;
            var id = this.id = options.id || this.instance;
            var container = document.id(options.container);

            Swiff.CallBacks[this.instance] = {};

            var params = options.params, vars = options.vars, callBacks = options.callBacks;
            var properties = Object.append({height: options.height, width: options.width}, options.properties);

            var self = this;

            for (var callBack in callBacks){
                Swiff.CallBacks[this.instance][callBack] = (function(option){
                    return function(){
                        return option.apply(self.object, arguments);
                    };
                })(callBacks[callBack]);
                vars[callBack] = 'Swiff.CallBacks.' + this.instance + '.' + callBack;
            }

            params.flashVars = Object.toQueryString(vars);
            if ('ActiveXObject' in window){
                properties.classid = 'clsid:D27CDB6E-AE6D-11cf-96B8-444553540000';
                params.movie = path;
            } else {
                properties.type = 'application/x-shockwave-flash';
            }
            properties.data = path;

            var build = '<object id="' + id + '"';
            for (var property in properties) build += ' ' + property + '="' + properties[property] + '"';
            build += '>';
            for (var param in params){
                if (params[param]) build += '<param name="' + param + '" value="' + params[param] + '" />';
            }
            build += '</object>';
            this.object = ((container) ? container.empty() : new Element('div')).set('html', build).firstChild;
        },

        replaces: function(element){
            element = document.id(element, true);
            element.parentNode.replaceChild(this.toElement(), element);
            return this;
        },

        inject: function(element){
            document.id(element, true).appendChild(this.toElement());
            return this;
        },

        remote: function(){
            return Swiff.remote.apply(Swiff, [this.toElement()].append(arguments));
        }

    });

    Swiff.CallBacks = {};

    Swiff.remote = function(obj, fn){
        var rs = obj.CallFunction('<invoke name="' + fn + '" returntype="javascript">' + __flash__argumentsToXML(arguments, 2) + '</invoke>');
        return eval(rs);
    };

})();

/*
---
name: Table
description: LUA-Style table implementation.
license: MIT-style license
authors:
  - Valerio Proietti
requires: [Core/Array]
provides: [Table]
...
*/

(function(){

    var Table = this.Table = function(){

        this.length = 0;
        var keys = [],
            values = [];

        this.set = function(key, value){
            var index = keys.indexOf(key);
            if (index == -1){
                var length = keys.length;
                keys[length] = key;
                values[length] = value;
                this.length++;
            } else {
                values[index] = value;
            }
            return this;
        };

        this.get = function(key){
            var index = keys.indexOf(key);
            return (index == -1) ? null : values[index];
        };

        this.erase = function(key){
            var index = keys.indexOf(key);
            if (index != -1){
                this.length--;
                keys.splice(index, 1);
                return values.splice(index, 1)[0];
            }
            return null;
        };

        this.each = this.forEach = function(fn, bind){
            for (var i = 0, l = this.length; i < l; i++) fn.call(bind, keys[i], values[i], this);
        };

    };

    if (this.Type) new Type('Table', Table);

})();



/* load o2 Core
 * |------------------------------------------------------------------------------|
 * |addReady:     o2.addReady(fn),                                                |
 * |------------------------------------------------------------------------------|
 * |load:         o2.load(urls, callback, reload)                                 |
 * |loadCss:      o2.loadCss(urls, dom, callback, reload, doc)                    |
 * |------------------------------------------------------------------------------|
 * |typeOf:       o2.typeOf(o)                                                    |
 * |------------------------------------------------------------------------------|
 * |uuid:         o2.uuid()                                                       |
 * |------------------------------------------------------------------------------|
 */
//Element.firstElementChild Polyfill
(function(constructor) {
    if (constructor &&
        constructor.prototype &&
        constructor.prototype.firstElementChild == null) {
        Object.defineProperty(constructor.prototype, 'firstElementChild', {
            get: function() {
                var node, nodes = this.childNodes, i = 0;
                while (node = nodes[i++]) {
                    if (node.nodeType === 1) {
                        return node;
                    }
                }
                return null;
            }
        });
    }
})(window.Node || window.Element);

(function(){
    var _href = window.location.href;
    var _debug = (_href.indexOf("debugger")!==-1);
    var _par = _href.substr(_href.lastIndexOf("?")+1, _href.length);
    var _lp = "zh-cn";
    if (_par){
        var _parList = _par.split("&");
        for (var i=0; i<_parList.length; i++){
            var _v = _parList[i];
            var _kv = _v.split("=");
            if (_kv[0].toLowerCase()==="lg") _lp = _kv[1];
            if (_kv[0].toLowerCase()==="lp") _lp = _kv[1];
        }
    }
    this.o2 = window.o2 || {};
    this.o2.version = {
        "v": "2.3.1",
        "build": "2019.07.31",
        "info": "O2OA 活力办公 创意无限. Copyright © 2018, o2oa.net O2 Team All rights reserved."
    };
    if (!this.o2.session) this.o2.session ={
        "isDebugger": _debug,
        "path": "../o2_core/o2"
    };
    this.o2.language = _lp;
    this.o2.splitStr = /\s*(?:,|;)\s*/;

    //     this.o2 = {
    //     "version": {
    //         "v": "2.3.1",
    //         "build": "2019.07.31",
    //         "info": "O2OA 活力办公 创意无限. Copyright © 2018, o2oa.net O2 Team All rights reserved."
    //     },
    //     "session": {
    //         "isDebugger": _debug,
    //         "path": "../o2_core/o2"
    //     },
    //     "language": _lp,
    //     "splitStr": /\s*(?:,|;)\s*/
    // };

    this.wrdp = this.o2;

    var _attempt = function(){
        for (var i = 0, l = arguments.length; i < l; i++){
            try {
                arguments[i]();
                return arguments[i];
            } catch (e){}
        }
        return null;
    };
    var _typeOf = function(item){
        if (item == null) return 'null';
        if (item.$family != null) return item.$family();
        if (item.constructor == window.Array) return "array";

        if (item.nodeName){
            if (item.nodeType == 1) return 'element';
            if (item.nodeType == 3) return (/\S/).test(item.nodeValue) ? 'textnode' : 'whitespace';
        } else if (typeof item.length == 'number'){
            if (item.callee) return 'arguments';
        }
        return typeof item;
    };
    this.o2.typeOf = _typeOf;

    var _addListener = function(dom, type, fn){
        if (type == 'unload'){
            var old = fn, self = this;
            fn = function(){
                _removeListener(dom, 'unload', fn);
                old();
            };
        }
        if (dom.addEventListener) dom.addEventListener(type, fn, !!arguments[2]);
        else dom.attachEvent('on' + type, fn);
    };
    var _removeListener = function(dom, type, fn){
        if (dom.removeEventListener) dom.removeEventListener(type, fn, !!arguments[2]);
        else dom.detachEvent('on' + type, fn);
    };

    //http request class
    var _request = (function(){
        var XMLHTTP = function(){ return new XMLHttpRequest(); };
        var MSXML2 = function(){ return new ActiveXObject('MSXML2.XMLHTTP'); };
        var MSXML = function(){ return new ActiveXObject('Microsoft.XMLHTTP'); };
        return _attempt(XMLHTTP, MSXML2, MSXML);
    })();
    this.o2.request = _request;

    var _returnBase = function(number, base) {
        return (number).toString(base).toUpperCase();
    };
    var _getIntegerBits = function(val, start, end){
        var base16 = _returnBase(val, 16);
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
    };
    var _rand = function(max) {
        return Math.floor(Math.random() * (max + 1));
    };
    this.o2.addListener = _addListener;
    this.o2.removeListener = _removeListener;

    //uuid
    var _uuid = function(){
        var dg = new Date(1582, 10, 15, 0, 0, 0, 0);
        var dc = new Date();
        var t = dc.getTime() - dg.getTime();
        var tl = _getIntegerBits(t, 0, 31);
        var tm = _getIntegerBits(t, 32, 47);
        var thv = _getIntegerBits(t, 48, 59) + '1';
        var csar = _getIntegerBits(_rand(4095), 0, 7);
        var csl = _getIntegerBits(_rand(4095), 0, 7);

        var n = _getIntegerBits(_rand(8191), 0, 7)
            + _getIntegerBits(_rand(8191), 8, 15)
            + _getIntegerBits(_rand(8191), 0, 7)
            + _getIntegerBits(_rand(8191), 8, 15)
            + _getIntegerBits(_rand(8191), 0, 15);
        return tl + tm + thv + csar + csl + n;
    };
    this.o2.uuid = _uuid;


    var _runCallback = function(callback, key, par){
        if (typeOf(callback).toLowerCase() === 'function'){
            if (key.toLowerCase()==="success") callback.apply(callback, par);
        }else{
            if (typeOf(callback).toLowerCase()==='object'){
                var name = ("on-"+key).camelCase();
                if (callback[name]) callback[name].apply(callback, par);
            }
        }
    };
    this.o2.runCallback = _runCallback;


    //load js, css, html adn all.
    var _getAllOptions = function(options){
        var doc = (options && options.doc) || document;
        if (!doc.unid) doc.unid = _uuid();
        return {
            "noCache": !!(options && options.nocache),
            "reload": !!(options && options.reload),
            "sequence": !!(options && options.sequence),
            "doc": doc,
            "dom": (options && options.dom) || document.body,
            "module": (options && options.module) || null,
            "noConflict": (options && options.noConflict) || false,
            "bind": (options && options.bind) || null,
            "position": (options && options.position) || "beforeend" //'beforebegin' 'afterbegin' 'beforeend' 'afterend'debugger
        }
    };
    var _getCssOptions = function(options){
        var doc = (options && options.doc) || document;
        if (!doc.unid) doc.unid = _uuid();
        return {
            "noCache": !!(options && options.nocache),
            "reload": !!(options && options.reload),
            "sequence": !!(options && options.sequence),
            "doc": doc,
            "dom": (options && options.dom) || null
        }
    };
    var _getJsOptions = function(options){
        var doc = (options && options.doc) || document;
        if (!doc.unid) doc.unid = _uuid();
        return {
            "noCache": !!(options && options.nocache),
            "reload": !!(options && options.reload),
            "sequence": (!(options && options.sequence == false)),
            "doc": doc
        }
    };
    var _getHtmlOptions = function(options){
        var doc = (options && options.doc) || document;
        if (!doc.unid) doc.unid = _uuid();
        return {
            "noCache": !!(options && options.nocache),
            "reload": !!(options && options.reload),
            "sequence": !!(options && options.sequence),
            "doc": doc,
            "dom": (options && options.dom) || null,
            "module": (options && options.module) || null,
            "noConflict": (options && options.noConflict) || false,
            "bind": (options && options.bind) || null,
            "position": (options && options.position) || "beforeend" //'beforebegin' 'afterbegin' 'beforeend' 'afterend'
        }
    };
    _filterUrl = function(url){
        if (o2.base){
            if (url.indexOf(":")===-1){
                var s = url.substring(0, url.indexOf("/")+1);
                var r = url.substring(url.indexOf("/")+1, url.length);
                if ("../"===s || "./"===s || "/"===s){
                    return s+o2.base+r;
                }else{
                    return o2.base+url
                }
            }
        }

        if (window.layout && layout.config && layout.config.urlMapping){
            for (var k in layout.config.urlMapping){
                var regex = new RegExp(k);
                if (regex.test(url)){
                    return url.replace(regex, layout.config.urlMapping[k]);
                }
            }
        }

        return url;
    };
    this.o2.filterUrl = _filterUrl;
    var _xhr_get = function(url, success, failure, completed){
        var xhr = new _request();
        url = _filterUrl(url);
        xhr.open("GET", url, true);

        var _checkCssLoaded= function(_, err){
            if (!(xhr.readyState == 4)) return;
            if (err){
                if (completed) completed(xhr);
                return;
            }

            _removeListener(xhr, 'readystatechange', _checkCssLoaded);
            _removeListener(xhr, 'load', _checkCssLoaded);
            _removeListener(xhr, 'error', _checkCssErrorLoaded);

            if (err) {if (failure) failure(xhr); return}
            var status = xhr.status;
            status = (status == 1223) ? 204 : status;
            if ((status >= 200 && status < 300))
                if (success) success(xhr);
                else if ((status >= 300 && status < 400))
                    if (failure) failure(xhr);
                    else
                        failure(xhr);
            if (completed) completed(xhr);
        };
        var _checkCssErrorLoaded= function(err){ _checkCssLoaded(err) };

        if ("load" in xhr) _addListener(xhr, "load", _checkCssLoaded);
        if ("error" in xhr) _addListener(xhr, "load", _checkCssErrorLoaded);
        _addListener(xhr, "readystatechange", _checkCssLoaded);
        xhr.send();
    };
    this.o2.xhr_get = _xhr_get;
    var _loadSequence = function(ms, cb, op, n, thisLoaded, loadSingle, uuid, fun){
        loadSingle(ms[n], function(module){
            if (module) thisLoaded.push(module);
            n++;
            if (fun) fun(module);
            if (n===ms.length){
                if (cb) cb(thisLoaded);
            }else{
                _loadSequence(ms, cb, op, n, thisLoaded, loadSingle, uuid, fun);
            }
        }, op, uuid);
    };
    var _loadDisarray = function(ms, cb, op, thisLoaded, loadSingle, uuid, fun){
        var count=0;
        for (var i=0; i<ms.length; i++){
            loadSingle(ms[i], function(module){
                if (module) thisLoaded.push(module);
                count++;
                if (fun) fun(module);
                if (count===ms.length) if (cb) cb(thisLoaded);
            }, op, uuid);
        }
    };

    //load js
    //use framework url
    var _frameworks = {
        "o2.core": ["../o2_core/o2/o2.core.js"],
        "o2.more": ["../o2_core/o2/o2.more.js"],
        "ie_adapter": ["../o2_core/o2/ie_adapter.js"],
        "jquery": ["../o2_lib/jquery/jquery.min.js"],
        "mootools": ["../o2_lib/mootools/mootools-1.6.0_all.js"],
        "ckeditor": ["../o2_lib/htmleditor/ckeditor4114/ckeditor.js"],
        "ckeditor5": ["../o2_lib/htmleditor/ckeditor5-12-1-0/ckeditor.js"],
        "raphael": ["../o2_lib/raphael/raphael.js"],
        "d3": ["../o2_lib/d3/d3.min.js"],
        "ace": ["../o2_lib/ace/src-min-noconflict/ace.js","../o2_lib/ace/src-min-noconflict/ext-language_tools.js"],
        "monaco": ["../o2_lib/vs/loader.js"],
        "JSBeautifier": ["../o2_lib/JSBeautifier/beautify.js"],
        "JSBeautifier_css": ["../o2_lib/JSBeautifier/beautify-css.js"],
        "JSBeautifier_html": ["../o2_lib/JSBeautifier/beautify-html.js"],
        "JSONTemplate": ["../o2_lib/mootools/plugin/Template.js"],
        "kity": ["../o2_lib/kityminder/kity/kity.js"],
        "kityminder": ["../o2_lib/kityminder/core/dist/kityminder.core.js"]
    };
    var _loaded = {};
    var _loadedCss = {};
    var _loadedHtml = {};
    var _loadCssRunning = {};
    var _loadCssQueue = [];
    var _loadingModules = {};

    var _loadSingle = function(module, callback, op){
        var url = module;
        var uuid = _uuid();
        if (op.noCache) url = (url.indexOf("?")!==-1) ? url+"&v="+uuid : addr_uri+"?v="+uuid;
        var key = encodeURIComponent(url+op.doc.unid);
        if (!op.reload) if (_loaded[key]){
            if (callback)callback(); return;
        }

        if (_loadingModules[key]){
            if (!_loadingModules[key].callbacks) _loadingModules[key].callbacks = [];
            _loadingModules[key].callbacks.push(callback);
        }else{
            _loadingModules[key] = { callbacks: [callback] };

            var head = (op.doc.head || op.doc.getElementsByTagName("head")[0] || op.doc.documentElement);
            var s = op.doc.createElement('script');
            head.appendChild(s);
            s.id = uuid;
            s.src = this.o2.filterUrl(url);

            var _checkScriptLoaded = function(_, isAbort, err){
                if (isAbort || !s.readyState || s.readyState === "loaded" || s.readyState === "complete") {
                    var scriptObj = {"module": module, "id": uuid, "script": s, "doc": op.doc};
                    if (!err) _loaded[key] = scriptObj;
                    _removeListener(s, 'readystatechange', _checkScriptLoaded);
                    _removeListener(s, 'load', _checkScriptLoaded);
                    _removeListener(s, 'error', _checkScriptErrorLoaded);
                    if (!isAbort || err){
                        if (err){
                            if (s) head.removeChild(s);
                            while (_loadingModules[key].callbacks.length){
                                (_loadingModules[key].callbacks.shift())();
                            }
                            //if (callback)callback();
                        }else{
                            //head.removeChild(s);
                            while (_loadingModules[key].callbacks.length){
                                (_loadingModules[key].callbacks.shift())(scriptObj);
                            }
                            //if (callback)callback(scriptObj);
                        }
                    }
                }
            };
            var _checkScriptErrorLoaded = function(e, err){
                console.log("Error: load javascript module: "+module);
                _checkScriptLoaded(e, true, "error");
            };

            if ('onreadystatechange' in s) _addListener(s, 'readystatechange', _checkScriptLoaded);
            _addListener(s, 'load', _checkScriptLoaded);
            _addListener(s, 'error', _checkScriptErrorLoaded);
        }
    };

    var _load = function(urls, options, callback){
        var ms = (_typeOf(urls)==="array") ? urls : [urls];
        var op =  (_typeOf(options)==="object") ? _getJsOptions(options) : _getJsOptions(null);
        var cbk = (_typeOf(options)==="function") ? options : callback;

        var cb = cbk;
        if (typeof define === 'function' && define.amd){
            define.amd = false;
            cb = (cbk) ? function(){define.amd = true; cbk();} : function(){define.amd = true;}
        }

        var modules = [];
        for (var i=0; i<ms.length; i++){
            var url = ms[i];
            var module = _frameworks[url] || url;
            if (_typeOf(module)==="array"){
                modules = modules.concat(module)
            }else{
                modules.push(module)
            }
        }
        var thisLoaded = [];
        if (op.sequence){
            _loadSequence(modules, cb, op, 0, thisLoaded, _loadSingle);
        }else{
            _loadDisarray(modules, cb, op, thisLoaded, _loadSingle);
        }
    };
    this.o2.load = _load;

    //load css
    var _loadSingleCss = function(module, callback, op, uuid){
        var url = module;
        var uid = _uuid();
        if (op.noCache) url = (url.indexOf("?")!==-1) ? url+"&v="+uid : url+"?v="+uid;

        var key = encodeURIComponent(url+op.doc.unid);
        if (_loadCssRunning[key]){
            _loadCssQueue.push(function(){
                _loadSingleCss(module, callback, op, uuid);
            });
            return;
        }

        if (_loadedCss[key]) uuid = _loadedCss[key]["class"];
        if (op.dom) _parseDom(op.dom, function(node){ if (node.className.indexOf(uuid) == -1) node.className += ((node.className) ? " "+uuid : uuid);}, op.doc);

        var completed = function(){
            if (_loadCssRunning[key]){
                _loadCssRunning[key] = false;
                delete _loadCssRunning[key];
            }
            if (_loadCssQueue && _loadCssQueue.length){
                (_loadCssQueue.shift())();
            }
        };

        if (_loadedCss[key])if (!op.reload){
            if (callback)callback(_loadedCss[key]);
            completed();
            return;
        }

        var success = function(xhr){
            var cssText = xhr.responseText;
            try{
                if (cssText){
                    cssText = cssText.replace(/\/\*(\s|\S)*?\*\//g, "");
                    if (op.bind) cssText = cssText.bindJson(op.bind);
                    if (op.dom){

                        var rex = new RegExp("(.+)(?=\\{)", "g");
                        var match;
                        var prefix = "." + uuid + " ";
                        while ((match = rex.exec(cssText)) !== null) {
                            // var rule = prefix + match[0];
                            // cssText = cssText.substring(0, match.index) + rule + cssText.substring(rex.lastIndex, cssText.length);
                            // rex.lastIndex = rex.lastIndex + prefix.length;

                            var rulesStr = match[0];
                            if (rulesStr.substr(0,1)=="@" || rulesStr.indexOf("%")!=-1){
                                // var begin = 0;
                                // var end = 0;


                            }else{
                                if (rulesStr.indexOf(",")!=-1){
                                    var rules = rulesStr.split(/\s*,\s*/g);
                                    rules = rules.map(function(r){
                                        return prefix + r;
                                    });
                                    var rule = rules.join(", ");
                                    cssText = cssText.substring(0, match.index) + rule + cssText.substring(rex.lastIndex, cssText.length);
                                    rex.lastIndex = rex.lastIndex + (prefix.length*rules.length);

                                }else{
                                    var rule = prefix + match[0];
                                    cssText = cssText.substring(0, match.index) + rule + cssText.substring(rex.lastIndex, cssText.length);
                                    rex.lastIndex = rex.lastIndex + prefix.length;
                                }
                            }
                        }
                    }
                    var style = op.doc.createElement("style");
                    style.setAttribute("type", "text/css");
                    var head = (op.doc.head || op.doc.getElementsByTagName("head")[0] || op.doc.documentElement);
                    head.appendChild(style);
                    if(style.styleSheet){
                        var setFunc = function(){
                            style.styleSheet.cssText = cssText;
                        };
                        if(style.styleSheet.disabled){
                            setTimeout(setFunc, 10);
                        }else{
                            setFunc();
                        }
                    }else{
                        var cssTextNode = op.doc.createTextNode(cssText);
                        style.appendChild(cssTextNode);
                    }
                }
                style.id = uid;
                var styleObj = {"module": module, "id": uid, "style": style, "doc": op.doc, "class": uuid};
                _loadedCss[key] = styleObj;
                if (callback) callback(styleObj);
            }catch (e){
                if (callback) callback();
                return;
            }
        };
        var failure = function(xhr){
            console.log("Error: load css module: "+module);
            if (callback) callback();
        };

        _loadCssRunning[key] = true;

        _xhr_get(url, success, failure, completed);
    };

    var _parseDomString = function(dom, fn, sourceDoc){
        var doc = sourceDoc || document;
        var list = doc.querySelectorAll(dom);
        if (list.length) for (var i=0; i<list.length; i++) _parseDomElement(list[i], fn);
    };
    var _parseDomElement = function(dom, fn){
        if (fn) fn(dom);
    };
    var _parseDom = function(dom, fn, sourceDoc){
        var domType = _typeOf(dom);
        if (domType==="string") _parseDomString(dom, fn, sourceDoc);
        if (domType==="element") _parseDomElement(dom, fn);
        if (domType==="array") for (var i=0; i<dom.length; i++) _parseDom(dom[i], fn, sourceDoc);
    };
    var _loadCss = function(modules, options, callback){
        var ms = (_typeOf(modules)==="array") ? modules : [modules];
        var op =  (_typeOf(options)==="object") ? _getCssOptions(options) : _getCssOptions(null);
        var cb = (_typeOf(options)==="function") ? options : callback;

        var uuid = "css"+_uuid();
        var thisLoaded = [];
        if (op.sequence){
            _loadSequence(ms, cb, op, 0, thisLoaded, _loadSingleCss, uuid);
        }else{
            _loadDisarray(ms, cb, op, thisLoaded, _loadSingleCss, uuid);
        }
    };
    var _removeCss = function(modules, doc){
        var thisDoc = doc || document;
        var ms = (_typeOf(modules)==="array") ? modules : [modules];
        for (var i=0; i<ms.length; i++){
            var module = ms[i];

            var k = encodeURIComponent(module+(thisDoc.unid||""));
            var removeCss = _loadedCss[k];
            if (!removeCss) for (key in _loadedCss){
                if (_loadedCss[key].id==module){
                    removeCss = _loadedCss[key];
                    k = key;
                    break;
                }
            }
            if (removeCss){
                delete _loadedCss[k];
                var styleNode = removeCss.doc.getElementById(removeCss.id);
                if (styleNode) styleNode.parentNode.removeChild(styleNode);
                removeCss = null;
            }
        }
    };
    this.o2.loadCss = _loadCss;
    this.o2.removeCss = _removeCss;
    if (window.Element) Element.prototype.loadCss = function(modules, options, callback){
        var op =  (_typeOf(options)==="object") ? options : {};
        var cb = (_typeOf(options)==="function") ? options : callback;
        op.dom = this;
        _loadCss(modules, op, cb);
    };

    //load html
    _loadSingleHtml = function(module, callback, op){
        var url = module;
        var uid = _uuid();
        if (op.noCache) url = (url.indexOf("?")!==-1) ? url+"&v="+uid : url+"?v="+uid;
        var key = encodeURIComponent(url+op.doc.unid);
        if (!op.reload) if (_loadedHtml[key]){ if (callback)callback(_loadedHtml[key]); return; }

        var success = function(xhr){
            var htmlObj = {"module": module, "id": uid, "data": xhr.responseText, "doc": op.doc};
            _loadedHtml[key] = htmlObj;
            if (callback) callback(htmlObj);
        };
        var failure = function(){
            console.log("Error: load html module: "+module);
            if (callback) callback();
        };
        _xhr_get(url, success, failure);
    };

    var _injectHtml = function(op, data){
        if (op.bind) data = data.bindJson(op.bind);
        if (op.dom) _parseDom(op.dom, function(node){
            if (op.module){
                _parseModule(node, data, op);
                //node.insertAdjacentHTML(op.position, data);
            }else{
                node.insertAdjacentHTML(op.position, data);
            }
        }, op.doc);
    };
    var _parseModule = function(node, data, op){
        var dom = op.noConflict ? document.createElement("div") : node;
        if (op.noConflict){
            dom.insertAdjacentHTML("afterbegin", data);
        }else{
            dom.insertAdjacentHTML(op.position, data);
        }
        var els = dom.querySelectorAll("[data-o2-element]");
        for (var i=0; i<els.length; i++){
            var el = els.item(i);
            var name = el.getAttribute("data-o2-element").toString();
            if (name) _bindToModule(op.module, el, name);
            if (el.hasAttribute("data-o2-events")){
                var events = el.getAttribute("data-o2-events").toString();
                if (events) _bindToEvents(op.module, el, events);
            }
        }

        if (op.noConflict){
            var n = dom.firstElementChild;
            var newNode = node.insertAdjacentElement(op.position, n);
            nextNode = dom.firstElementChild;
            while (nextNode) {
                newNode = newNode.insertAdjacentElement("afterend", nextNode);
                nextNode = dom.firstElementChild;
            }
            dom.destroy();
        }
    };

    var _bindToEvents = function(m, node, events){
        var eventList = events.split(/\s*;\s*/);
        eventList.forEach(function(ev){
            var evs = ev.split(/\s*:\s*/);
            if (evs.length>1){
                node.addEventListener(evs[0], function(e){
                    if (m[evs[1]]) m[evs[1]].apply(m, [e]);
                }, false);
            }
        });
    }
    var _bindToModule = function(m, node, name){
        // if (m[name]){
        //     if (o2.typeOf(m[name])!=="array"){
        //         var tmp = m[name];
        //         m[name] = [];
        //         m[name].push(tmp);
        //     }
        //     m[name].push(node);
        // }else{
        m[name] = node;
        // }
    };
    var _loadHtml = function(modules, options, callback){
        var ms = (_typeOf(modules)==="array") ? modules : [modules];
        var op =  (_typeOf(options)==="object") ? _getHtmlOptions(options) : _getHtmlOptions(null);
        var cb = (_typeOf(options)==="function") ? options : callback;

        var thisLoaded = [];
        if (op.sequence){
            _loadSequence(ms, cb, op, 0, thisLoaded, _loadSingleHtml, null, function(html){ if (html) _injectHtml(op, html.data ); });
        }else{
            _loadDisarray(ms, cb, op, thisLoaded, _loadSingleHtml, null, function(html){ if (html) _injectHtml(op, html.data ); });
        }
    };
    this.o2.loadHtml = _loadHtml;
    if (window.Element) Element.prototype.loadHtml = function(modules, options, callback){
        var op =  (_typeOf(options)==="object") ? options : {};
        var cb = (_typeOf(options)==="function") ? options : callback;
        op.dom = this;
        _loadHtml(modules, op, cb);
    };
    this.o2.injectHtml = function(html, op){
        _injectHtml(op, html);
    };
    if (window.Element) Element.prototype.injectHtml = function(html, options){
        var op =  (_typeOf(options)==="object") ? options : {};
        op.dom = this;
        op.position = (options && options.position) || "beforeend"
        _injectHtml(op, html);
    };

    //load all
    _loadAll = function(modules, options, callback){
        //var ms = (_typeOf(modules)==="array") ? modules : [modules];
        var op =  (_typeOf(options)==="object") ? _getAllOptions(options) : _getAllOptions(null);
        var cb = (_typeOf(options)==="function") ? options : callback;

        var ms, htmls, styles, sctipts;
        var _htmlLoaded=(!modules.html), _cssLoaded=(!modules.css), _jsLoaded=(!modules.js);
        var _checkloaded = function(){
            if (_htmlLoaded && _cssLoaded && _jsLoaded) if (cb) cb(htmls, styles, sctipts);
        };
        if (modules.html){
            _loadHtml(modules.html, op, function(h){
                htmls = h;
                _htmlLoaded = true;
                _checkloaded();
            });
        }
        if (modules.css){
            _loadCss(modules.css, op, function(s){
                styles = s;
                _cssLoaded = true;
                _checkloaded();
            });
        }
        if (modules.js){
            _load(modules.js, op, function(s){
                sctipts = s;
                _jsLoaded = true;
                _checkloaded();
            });
        }
    };
    this.o2.loadAll = _loadAll;
    if (window.Element) Element.prototype.loadAll = function(modules, options, callback){
        var op =  (_typeOf(options)==="object") ? options : {};
        var cb = (_typeOf(options)==="function") ? options : callback;
        op.dom = this;
        _loadAll(modules, op, cb);
    };

    var _getIfBlockEnd = function(v){
        var rex = /(\{\{if\s+)|(\{\{\s*end if\s*\}\})/gmi;
        var rexEnd = /\{\{\s*end if\s*\}\}/gmi;
        var subs = 1;
        while ((match = rex.exec(v)) !== null) {
            var fullMatch = match[0];
            if (fullMatch.search(rexEnd)!==-1){
                subs--;
                if (subs==0) break;
            }else{
                subs++
            }
        }
        if (match) return {"codeIndex": match.index, "lastIndex": rex.lastIndex};
        return {"codeIndex": v.length-1, "lastIndex": v.length-1};
    }
    var _getEachBlockEnd = function(v){
        var rex = /(\{\{each\s+)|(\{\{\s*end each\s*\}\})/gmi;
        var rexEnd = /\{\{\s*end each\s*\}\}/gmi;
        var subs = 1;
        while ((match = rex.exec(v)) !== null) {
            var fullMatch = match[0];
            if (fullMatch.search(rexEnd)!==-1){
                subs--;
                if (subs==0) break;
            }else{
                subs++;
            }
        }
        if (match) return {"codeIndex": match.index, "lastIndex": rex.lastIndex};
        return {"codeIndex": v.length-1, "lastIndex": v.length-1};
    }

    var _parseHtml = function(str, json){
        var v = str;
        var rex = /(\{\{\s*)[\s\S]*?(\s*\}\})/gmi;

        var match;
        while ((match = rex.exec(v)) !== null) {
            var fullMatch = match[0];
            var offset = 0;

            //if statement begin
            if (fullMatch.search(/\{\{if\s+/i)!==-1){
                //找到对应的end if
                var condition = fullMatch.replace(/^\{\{if\s*/i, "");
                condition = condition.replace(/\s*\}\}$/i, "");
                var flag = _jsonText(json, condition, "boolean");

                var tmpStr = v.substring(rex.lastIndex, v.length);
                var endIfIndex = _getIfBlockEnd(tmpStr);
                if (flag){ //if 为 true
                    var parseStr = _parseHtml(tmpStr.substring(0, endIfIndex.codeIndex), json);
                    var vLeft = v.substring(0, match.index);
                    var vRight = v.substring(rex.lastIndex+endIfIndex.lastIndex, v.length);
                    v = vLeft + parseStr + vRight;
                    offset = parseStr.length - fullMatch.length;
                }else{
                    v = v.substring(0, match.index) + v.substring(rex.lastIndex+endIfIndex.lastIndex, v.length);
                    offset = 0-fullMatch.length;
                }
            }else  if (fullMatch.search(/\{\{each\s+/)!==-1) { //each statement
                var itemString = fullMatch.replace(/^\{\{each\s*/, "");
                itemString = itemString.replace(/\s*\}\}$/, "");
                var eachValue = _jsonText(json, itemString, "object");

                var tmpEachStr = v.substring(rex.lastIndex, v.length);
                var endEachIndex = _getEachBlockEnd(tmpEachStr);

                var parseEachStr = tmpEachStr.substring(0, endEachIndex.codeIndex);
                var eachResult = "";
                if (eachValue && _typeOf(eachValue)==="array"){
                    for (var i=0; i<eachValue.length; i++){
                        eachValue[i]._ = json;
                        eachResult += _parseHtml(parseEachStr, eachValue[i]);
                    }
                    var eLeft = v.substring(0, match.index);
                    var eRight = v.substring(rex.lastIndex+endEachIndex.lastIndex, v.length);
                    v = eLeft + eachResult + eRight;
                    offset = eachResult.length - fullMatch.length;
                }else{
                    v = v.substring(0, match.index) + v.substring(rex.lastIndex+endEachIndex.lastIndex, v.length);
                    offset = 0-fullMatch.length;
                }

            }else{ //text statement
                var text = fullMatch.replace(/^\{\{\s*/, "");
                text = text.replace(/\}\}\s*$/, "");
                var value = _jsonText(json, text);
                offset = value.length-fullMatch.length;
                v = v.substring(0, match.index) + value + v.substring(rex.lastIndex, v.length);
            }
            rex.lastIndex = rex.lastIndex + offset;
        }
        return v;
    };
    var _jsonText = function(json, text, type){
        try {
            var $ = json;
            var f = eval("(function($){\n return "+text+";\n})");
            returnValue = f.apply(json, [$]);
            if (returnValue===undefined) returnValue="";
            if (type==="boolean") return (!!returnValue);
            if (type==="object") return returnValue;
            returnValue = returnValue.toString();
            returnValue = returnValue.replace(/\&/g, "&amp;");
            returnValue = returnValue.replace(/>/g, "&gt;");
            returnValue = returnValue.replace(/</g, "&lt;");
            returnValue = returnValue.replace(/\"/g, "&quot;");
            return returnValue || "";
        }catch(e){
            if (type==="boolean") return false;
            if (type==="object") return null;
            return "";
        }
    };

    o2.bindJson = function(str, json){
        return _parseHtml(str, json);
    };
    String.prototype.bindJson = function(json){
        return _parseHtml(this, json);
    };
})();


(function (){
    var _Class = {
        create: function(options) {
            var newClass = function() {
                this.initialize.apply(this, arguments);
            };
            _copyPrototype(newClass, options);
            return newClass;
        }
    };
    var _copyPrototype = function (currentNS, props){
        if (!props){return currentNS;}
        if (!currentNS){return currentNS;}
        if ((typeof currentNS).toLowerCase()==="object"){
            for (var prop in props){
                currentNS[prop] = props[prop];
            }
        }
        if ((typeof currentNS).toLowerCase()==="function"){
            for (var propfun in props){
                currentNS.prototype[propfun] = props[propfun];
            }
        }
        return currentNS;
    };

    var _loaded = {};

    var _requireJs = function(url, callback, async, compression, module){
        var key = encodeURIComponent(url);
        if (_loaded[key]){o2.runCallback(callback, "success", [module]); return "";}

        var jsPath = (compression || !this.o2.session.isDebugger) ? url.replace(/\.js/, ".min.js") : url;
        jsPath = (jsPath.indexOf("?")!==-1) ? jsPath+"&v="+this.o2.version.v : jsPath+"?v="+this.o2.version.v;

        var xhr = new Request({
            url: o2.filterUrl(jsPath), async: async, method: "get",
            onSuccess: function(){
                //try{
                _loaded[key] = true;
                o2.runCallback(callback, "success", [module]);
                //}catch (e){
                //    o2.runCallback(callback, "failure", [e]);
                //}
            },
            onFailure: function(r){
                o2.runCallback(callback, "failure", [r]);
            }
        });
        xhr.send();
    };
    var _requireSingle = function(module, callback, async, compression){
        if (o2.typeOf(module)==="array"){
            _requireAppSingle(module, callback, async, compression);
        }else{
            module = module.replace("MWF.", "o2.");
            var levels = module.split(".");
            if (levels[levels.length-1]==="*") levels[levels.length-1] = "package";
            levels.shift();

            var jsPath = this.o2.session.path;
            jsPath +="/"+levels.join("/")+".js";

            var loadAsync = (async!==false);

            _requireJs(jsPath, callback, loadAsync, compression, module);
        }
    };
    var _requireSequence = function(fun, module, thisLoaded, thisErrorLoaded, callback, async, compression){
        var m = module.shift();
        fun(m, {
            "onSuccess": function(m){
                thisLoaded.push(m);
                o2.runCallback(callback, "every", [m]);
                if (module.length){
                    _requireSequence(fun, module, thisLoaded, thisErrorLoaded, callback, async, compression);
                }else{
                    if (thisErrorLoaded.length){
                        o2.runCallback(callback, "failure", [thisLoaded, thisErrorLoaded]);
                    }else{
                        o2.runCallback(callback, "success", [thisLoaded, thisErrorLoaded]);
                    }
                }
            },
            "onFailure": function(){
                thisErrorLoaded.push(module[i]);
                o2.runCallback(callback, "failure", [thisLoaded, thisErrorLoaded]);
            }
        }, async, compression);
    };
    var _requireDisarray = function(fun, module, thisLoaded, thisErrorLoaded, callback, async, compression){
        for (var i=0; i<module.length; i++){
            fun(module[i], {
                "onSuccess": function(m){
                    thisLoaded.push(m);
                    o2.runCallback(callback, "every", [m]);
                    if ((thisLoaded.length+thisErrorLoaded.length)===module.length){
                        if (thisErrorLoaded.length){
                            o2.runCallback(callback, "failure", [thisLoaded, thisErrorLoaded]);
                        }else{
                            o2.runCallback(callback, "success", [thisLoaded, thisErrorLoaded]);
                        }
                    }
                },
                "onFailure": function(){
                    thisErrorLoaded.push(module[i]);
                    o2.runCallback(callback, "failure", [thisLoaded, thisErrorLoaded]);
                }
            }, async, compression);
        }
    };
    var _require = function(module, callback, async, sequence, compression){
        var type = typeOf(module);
        if (type==="array"){
            var sql = !!sequence;
            var thisLoaded = [];
            var thisErrorLoaded = [];
            if (sql){
                _requireSequence(_requireSingle, module, thisLoaded, thisErrorLoaded, callback, async, compression);

            }else{
                _requireDisarray(_requireSingle, module, thisLoaded, thisErrorLoaded, callback, async, compression);
            }
        }
        if (type==="string"){
            _requireSingle(module, callback, async, compression);
        }
    };

    var _requireAppSingle = function(modules, callback, async, compression){
        var module = modules[0];
        var clazz = modules[1];
        var levels = module.split(".");
        //levels.shift();
        var root = "x_component_"+levels.join("_");
        var clazzName = clazz || "Main";
        var path = "../"+root+"/"+clazzName.replace(/\./g, "/")+".js";
        var loadAsync = (async!==false);
        _requireJs(path, callback, loadAsync, compression);
    };
    var _requireApp = function(module, clazz, callback, async, sequence, compression){
        var type = typeOf(module);
        if (type==="array"){
            var sql = !!sequence;
            var thisLoaded = [];
            var thisErrorLoaded = [];
            if (sql){
                _requireSequence(_requireAppSingle, module, thisLoaded, thisErrorLoaded, callback, async, compression);

            }else{
                _requireDisarray(_requireAppSingle, module, thisLoaded, thisErrorLoaded, callback, async, compression);
            }
        }
        if (type==="string"){
            var modules = [module, clazz];
            _requireAppSingle(modules, callback, async, compression);
        }
    };

    JSON = window.JSON || {};
    var _json = JSON;
    _json.get = function(url, callback, async, nocache){
        var loadAsync = (async !== false);
        var noJsonCache = (nocache === true);

        url = (url.indexOf("?")!==-1) ? url+"&v="+o2.version.v : url+"?v="+o2.version.v;

        var json = null;
        var res = new Request.JSON({
            url: o2.filterUrl(url),
            secure: false,
            method: "get",
            noCache: noJsonCache,
            async: loadAsync,
            withCredentials: true,
            onSuccess: function(responseJSON, responseText){
                json = responseJSON;
                if (typeOf(callback).toLowerCase() === 'function'){
                    callback(responseJSON, responseText);
                }else{
                    o2.runCallback(callback, "success", [responseJSON, responseText]);
                }
            }.bind(this),
            onFailure: function(xhr){
                o2.runCallback(callback, "requestFailure", [xhr]);
            }.bind(this),
            onError: function(text, error){
                o2.runCallback(callback, "error", [text, error]);
            }.bind(this)
        });
        res.send();
        return json;
    };
    _json.getJsonp = function(url, callback, async, callbackKey){
        var loadAsync = (async !== false);

        var callbackKeyWord = callbackKey || "callback";

        url = (url.indexOf("?")!==-1) ? url+"&v="+o2.version.v : url+"?v="+o2.version.v;
        var res = new Request.JSONP({
            url: o2.filterUrl(url),
            secure: false,
            method: "get",
            noCache: true,
            async: loadAsync,
            callbackKey: callbackKeyWord,
            onSuccess: function(responseJSON, responseText){
                o2.runCallback(callback, "success",[responseJSON, responseText]);
            }.bind(this),
            onFailure: function(xhr){
                o2.runCallback(callback, "requestFailure",[xhr]);
            }.bind(this),
            onError: function(text, error){
                o2.runCallback(callback, "error",[text, error]);
            }.bind(this)
        });
        res.send();
    };


    var _loadLP = function(name){
        var jsPath = o2.session.path;
        jsPath = jsPath+"/lp/"+name+".js";
        var r = new Request({
            url: o2.filterUrl(jsPath),
            async: false,
            method: "get",
            onSuccess: function(responseText){
                try{
                    Browser.exec(responseText);
                }catch (e){}
            },
            onFailure: function(xhr){
                throw "loadLP Error: "+xhr.responseText;
            }
        });
        r.send();
    };

    var _cacheUrls = [
        /jaxrs\/form\/workorworkcompleted\/.+/ig,
        //    /jaxrs\/script/ig,
        /jaxrs\/script\/.+\/app\/.+\/imported/ig,
        /jaxrs\/script\/portal\/.+\/name\/.+\/imported/ig,
        /jaxrs\/script\/.+\/application\/.+\/imported/ig,
        /jaxrs\/page\/.+\/portal\/.+/ig
        // /jaxrs\/authentication/ig
        // /jaxrs\/statement\/.*\/execute\/page\/.*\/size\/.*/ig
    ];
    _restful = function(method, address, data, callback, async, withCredentials, cache){
        var loadAsync = (async !== false);
        var credentials = (withCredentials !== false);
        address = (address.indexOf("?")!==-1) ? address+"&v="+o2.version.v : address+"?v="+o2.version.v;
        //var noCache = cache===false;
        var noCache = !cache;


        //if (Browser.name == "ie")
        if (_cacheUrls.length){
            for (var i=0; i<_cacheUrls.length; i++){
                if (_cacheUrls[i].test(address)){
                    noCache = false;
                    break;
                }
            }
        }
        //var noCache = false;
        var res = new Request.JSON({
            url: o2.filterUrl(address),
            secure: false,
            method: method,
            emulation: false,
            noCache: noCache,
            async: loadAsync,
            withCredentials: credentials,
            onSuccess: function(responseJSON, responseText){
                // var xToken = this.getHeader("authorization");
                // if (!xToken) xToken = this.getHeader("x-token");
                var xToken = this.getHeader("x-token");
                if (xToken){
                    if (window.layout){
                        if (!layout.session) layout.session = {};
                        layout.session.token = xToken;
                    }
                }
                o2.runCallback(callback, "success", [responseJSON]);
            },
            onFailure: function(xhr){
                o2.runCallback(callback, "requestFailure", [xhr]);
            }.bind(this),
            onError: function(text, error){
                o2.runCallback(callback, "error", [text, error]);
            }.bind(this)
        });

        res.setHeader("Content-Type", "application/json; charset=utf-8");
        res.setHeader("Accept", "text/html,application/json,*/*");
        if (window.layout) {
            if (layout["debugger"]){
                res.setHeader("x-debugger", "true");
            }
            if (layout.session && layout.session.user){
                if (layout.session.user.token) {
                    res.setHeader("x-token", layout.session.user.token);
                    res.setHeader("authorization", layout.session.user.token);
                }
            }
        }
        //Content-Type	application/x-www-form-urlencoded; charset=utf-8
        res.send(data);
        return res;
    };

    var _release = function(o){
        var type = typeOf(o);
        switch (type){
            case "object":
                for (var k in o){
                    //if (o[k] && o[k].destroy) o[k].destroy();
                    o[k] = null;
                }
                break;
            case "array":
                for (var i=0; i< o.length; i++){
                    _release(o[i]);
                    if (o[i]) o[i] = null;
                }
                break;
        }
    };

    var _defineProperties = Object.defineProperties || function (obj, properties) {
        function convertToDescriptor(desc) {
            function hasProperty(obj, prop) {
                return Object.prototype.hasOwnProperty.call(obj, prop);
            }
            function isCallable(v) {
                // NB: modify as necessary if other values than functions are callable.
                return typeof v === "function";
            }
            if (typeof desc !== "object" || desc === null)
                throw new TypeError("bad desc");
            var d = {};
            if (hasProperty(desc, "enumerable"))
                d.enumerable = !!desc.enumerable;
            if (hasProperty(desc, "configurable"))
                d.configurable = !!desc.configurable;
            if (hasProperty(desc, "value"))
                d.value = desc.value;
            if (hasProperty(desc, "writable"))
                d.writable = !!desc.writable;
            if (hasProperty(desc, "get")) {
                var g = desc.get;
                if (!isCallable(g) && typeof g !== "undefined")
                    throw new TypeError("bad get");
                d.get = g;
            }
            if (hasProperty(desc, "set")) {
                var s = desc.set;
                if (!isCallable(s) && typeof s !== "undefined")
                    throw new TypeError("bad set");
                d.set = s;
            }
            if (("get" in d || "set" in d) && ("value" in d || "writable" in d))
                throw new TypeError("identity-confused descriptor");
            return d;
        }
        if (typeof obj !== "object" || obj === null)
            throw new TypeError("bad obj");
        properties = Object(properties);
        var keys = Object.keys(properties);
        var descs = [];
        for (var j = 0; j < keys.length; j++)
            descs.push([keys[j], convertToDescriptor(properties[keys[j]])]);
        for (var i = 0; i < descs.length; i++){
            if (Object.defineProperty && (Browser.name=="ie" && Browser.version!=8)){
                Object.defineProperty(obj, descs[i][0], descs[i][1]);
            }else{
                if (descs[i][1].value) obj[descs[i][0]] = descs[i][1].value;
                if (descs[i][1].get) obj["get"+descs[i][0].capitalize()] = descs[i][1].get;
                if (descs[i][1].set) obj["set"+descs[i][0].capitalize()] = descs[i][1].set;
            }
        }
        return obj;
    };
    if (!Array.prototype.findIndex) {
        Object.defineProperty(Array.prototype, 'findIndex', {
            value: function(predicate) {
                if (this == null) {
                    throw new TypeError('"this" is null or not defined');
                }
                var o = Object(this);
                var len = o.length >>> 0;
                if (typeof predicate !== 'function') {
                    throw new TypeError('predicate must be a function');
                }
                var thisArg = arguments[1];
                var k = 0;
                while (k < len) {
                    var kValue = o[k];
                    if (predicate.call(thisArg, kValue, k, o)) {
                        return k;
                    }
                    k++;
                }
                return -1;
            }
        });
    }
    if (!Array.prototype.find) {
        Object.defineProperty(Array.prototype, 'find', {
            value: function(predicate) {
                if (this == null) {
                    throw new TypeError('"this" is null or not defined');
                }
                var o = Object(this);
                var len = o.length >>> 0;
                if (typeof predicate !== 'function') {
                    throw new TypeError('predicate must be a function');
                }
                var thisArg = arguments[1];
                var k = 0;
                while (k < len) {
                    var kValue = o[k];
                    if (predicate.call(thisArg, kValue, k, o)) {
                        return kValue;
                    }
                    k++;
                }
                return undefined;
            }
        });
    }

    var _txt = function(v){
        var t = v.replace(/\</g, "&lt;");
        t = t.replace(/\</g, "&gt;");
        return t;
    };

    this.o2.Class = _Class;
    this.o2.require = _require;
    this.o2.requireApp = _requireApp;
    this.o2.JSON = _json;
    this.o2.loadLP = _loadLP;
    this.o2.restful = _restful;
    this.o2.release = _release;
    this.o2.defineProperties = _defineProperties;
    this.o2.txt = _txt;

    Object.repeatArray = function(o, count){
        var arr = [];
        for (var i=0; i<count; i++){
            arr.push(o)
        }
        return arr;
    }
    Date.implement({
        "getFromServer": function(){
            var d;
            o2.Actions.get("x_program_center").echo(function(json){
                d = Date.parse(json.data.serverTime);
            }, null, false);
            return d;
        }
    });

})();
o2.core = true;

(function (){
    o2.getCenterPosition = function(el, width, height){
        var elPositon = $(el).getPosition();
        var elSize = $(el).getSize();
        var node = $("layout");
        var size = (node) ? $(node).getSize() : $(document.body).getSize();

        var top = (elPositon.y+elSize.y)/2 - (height/2);
        var left = (elPositon.x+elSize.x)/2-(width/2);
        if ((left+width)>size.x){
            left = size.x-width-10;
        }
        if ((top+height)>size.y){
            top = size.y-height-10;
        }

        return {"x": left, "y": top};
    };
    o2.getMarkSize = function(node){
        var size;
        if (!node){
            size = $(document.body).getSize();
            var winSize = $(window).getSize();

            var height = size.y;
            var width = size.x;

            if (height<winSize.y) height = winSize.y;
            if (width<winSize.x) width = winSize.x;

            return {x: size.x, y: height};
        }else{
            size = $(node).getSize();
            return {x: size.x, y: size.y};
        }
    };
    o2.json = function(jsonString, fun){
        var obj = JSON.decode(jsonString);
        var p = fun.split(".");
        var tmp = obj;
        p.each(function(item){
            if (item.indexOf("[")!==-1){
                var x = item.split("[");
                var i = parseInt(x[1].substr(0, x[1].indexOf("]")));
                tmp = tmp[x[0]][i];
            }else{
                tmp = tmp[item];
            }
        });
        return tmp;
    };
    o2.getHTMLTemplate = function(url, callback, async){
        var loadAsync = (async !== false);
        var res = new Request.HTML({
            url: url,
            async: loadAsync,
            method: "get",
            onSuccess: function(responseTree, responseElements, responseHTML, responseJavaScript){
                o2.runCallback(callback, "success", [responseTree, responseElements, responseHTML, responseJavaScript]);
            }.bind(this),
            onFailure: function(xhr){
                o2.runCallback(callback, "requestFailure", [xhr]);
            }
        });
        res.send();
    };

    o2.getRequestText = function(url, callback, async){
        var loadAsync = (async !== false);

        url = (url.indexOf("?")!==-1) ? url+"&v="+o2.version.v : url+"?v="+o2.version.v;
        var res = new Request({
            url: url,
            async: loadAsync,
            method: "get",
            onSuccess: function(responseText, responseXML){
                o2.runCallback(callback, "success",[responseText, responseXML]);
            }.bind(this),
            onFailure: function(xhr){
                o2.runCallback(callback, "requestFailure",[xhr]);
            }
        });
        res.send();
    };

    o2.encodeJsonString = function(str){
        var tmp = [str];
        var dataStr = (JSON.encode(tmp));
        return dataStr.substr(2, dataStr.length-4);
    };
    o2.decodeJsonString = function(str){
        var tmp = "[\""+str+"\"]";
        var dataObj = (JSON.decode(tmp));
        return dataObj[0];
    };

    o2.getTextSize = function(text, styles){
        var tmpSpan = new Element("span", {
            "text": text,
            "styles": styles
        }).inject($(document.body));
        var size = tmpSpan.getSize();
        tmpSpan.destroy();
        return size;
    };
    o2.getCenter = function(size, target, offset){
        if (!target) target = document.body;
        var targetSize = target.getSize();
        var targetPosition = target.getPosition(offset);
        var targetScroll = target.getScroll();

        var x = targetSize.x/2;
        var y = targetSize.y/2;

        x = x-(size.x/2);
        y = y-(size.y/2);
        x = x+targetPosition.x;
        y = y+targetPosition.y;
        x = x+targetScroll.x;
        y = y+targetScroll.y;

        return {"x": x, "y": y};
    };
    o2.getEPointer = function(e){
        var x = 0;
        var y = 0;
        if (typeOf(e)=="element"){
            var position = e.getPosition(this.content);
            x = position.x;
            y = position.y;
        }else{
            if (Browser.name=="firefox"){
                x = parseFloat(e.event.clientX || e.event.x);
                y = parseFloat(e.event.clientY || e.event.y);
            }else{
                x = parseFloat(e.event.x);
                y = parseFloat(e.event.y);
            }

            if (e.target){
                var position = e.target.getPosition(this.content);
                x = position.x;
                y = position.y;
            }
            //    }
        }
        return {"x": x, "y": y};
    };
    o2.getParent = function(node, tag){
        var pNode = node.parentElement;
        while(pNode && pNode.tagName.toString().toLowerCase() !== tag.toString().toLowerCase()){
            pNode = pNode.parentElement;
        }
        return pNode;
    };
    o2.getOffset = function(evt){
        if (Browser.name==="firefox"){
            return {
                "offsetX": evt.layerX,
                "offsetY": evt.layerY
            };
        }else{
            return {
                "offsetX": evt.offsetX,
                "offsetY": evt.offsetY
            }
        }
    };

    if (String.implement) String.implement({
        "getAllIndexOf": function(str){
            var idxs= [];
            var idx = this.indexOf(str);
            while (idx !== -1){
                idxs.push(idx);
                idx = this.indexOf(str, idx+1);
            }
            return idxs;
        }
    });
    if (Array.implement) Array.implement({
        "trim": function(){
            var arr = [];
            this.each(function(v){
                if (v) arr.push(v);
            });
            return arr;
        },
        "isIntersect": function(arr){
            return this.some(function(item){ return (arr.indexOf(item)!==-1); })
        }
    });
    if (window.Element && Element.implement) Element.implement({
        "isIntoView": function() {
            // var pNode = this.getParent();
            // while (pNode && ((pNode.getScrollSize().y-(pNode.getComputedSize().height+1)<=0) || pNode.getStyle("overflow")==="visible")) pNode = pNode.getParent();
            //
            var pNode = this.getParentSrcollNode();

            if (!pNode) pNode = document.body;
            var size = pNode.getSize();
            var srcoll = pNode.getScroll();
            var p = (pNode == window) ? {"x":0, "y": 0} : this.getPosition(pNode);
            var nodeSize = this.getSize();
            //return (p.x-srcoll.x>=0 && p.y-srcoll.y>=0) && (p.x+nodeSize.x<size.x+srcoll.x && p.y+nodeSize.y<size.y+srcoll.y);
            return (p.x-srcoll.x>=0 && p.y>=0) && (p.x+nodeSize.x<size.x+srcoll.x && p.y+nodeSize.y<size.y)
        },
        "appendHTML": function(html, where){
            if (this.insertAdjacentHTML){
                var whereText = "beforeEnd";
                if (where==="before") whereText = "beforeBegin";
                if (where==="after") whereText = "afterEnd";
                if (where==="bottom") whereText = "beforeEnd";
                if (where==="top") whereText = "afterBegin";
                this.insertAdjacentHTML(whereText, html);
            }else {
                if (where==="bottom") this.innerHTML = this.innerHTML+html;
                if (where==="top") this.innerHTML = html+this.innerHTML;
            }
        },
        "positionTo": function(x,y){
            var left = x.toFloat();
            var top = y.toFloat();
            var offsetNode = this.getOffsetParent();
            if (offsetNode){
                var offsetPosition = offsetNode.getPosition();
                left = left-offsetPosition.x;
                top = top-offsetPosition.y;
            }
            this.setStyles({"top": top, "left": left});
            return this;
        },
        "getBorder": function(){
            var positions = ["top", "left", "right", "bottom"];
            var styles = ["color", "style", "width"];

            var obj = {};
            positions.each(function (position){
                styles.each(function(style){
                    var key = "border-"+position+"-"+style;
                    obj[key] = this.getStyle(key);
                }.bind(this));
            }.bind(this));

            return obj;
        },
        "isOutside": function(e){
            var elementCoords = this.getCoordinates();
            var targetCoords  = this.getCoordinates();
            if(((e.page.x < elementCoords.left || e.page.x > (elementCoords.left + elementCoords.width)) ||
                (e.page.y < elementCoords.top || e.page.y > (elementCoords.top + elementCoords.height))) &&
                ((e.page.x < targetCoords.left || e.page.x > (targetCoords.left + targetCoords.width)) ||
                    (e.page.y < targetCoords.top || e.page.y > (targetCoords.top + targetCoords.height))) ) return true;

            return false;
        },
        "getAbsolutePosition":function(){
            var styleLeft = 0;
            var styleTop = 0;
            var node = this;

            styleLeft = node.offsetLeft;
            styleTop = node.offsetTop;

            node = node.parentElement;

            while (node && node.tagName.toString().toLowerCase()!=="body"){
                styleLeft += node.offsetLeft;
                styleTop += node.offsetTop;
                node = node.offsetParent;
            }
            return {x: styleLeft, y: styleTop};
        },
        "tweenScroll": function(to, time){
            if (!this.tweenScrollQueue){
                this.tweenScrollQueue = [];
            }
            if (this.tweenScrollQueue.length){
                this.tweenScrollQueue.push(to);
            }else{
                this.tweenScrollQueue.push(to);
                this.doTweenScrollQueue(time);
            }
        },
        "doTweenScrollQueue": function(time){
            if (this.tweenScrollQueue.length){
                var i = this.tweenScrollQueue.length;
                var to = this.tweenScrollQueue[this.tweenScrollQueue.length-1];

                var scroll = this.getScroll();
                var dy = to - scroll.y;
                var step = dy/time;
                var count = 0;
                var move = 0;

                var id = window.setInterval(function(){

                    this.scrollTo(0, scroll.y+count*step);
                    count++;
                    if (count>time){
                        window.clearInterval(id);
                        for (var x=1; x<=i; x++) this.tweenScrollQueue.shift();
                        if (this.tweenScrollQueue.length) this.doTweenScrollQueue(time);
                    }
                }.bind(this), 1);
            }
        },
        "isPointIn": function(px, py, offX, offY, el){
            if (!offX) offX = 0;
            if (!offY) offY = 0;
            var position = this.getPosition(el);
            var size = this.getSize();
            return (position.x-offX<=px && position.x+size.x+offX>=px && position.y-offY<=py && position.y+size.y+offY>=py);
        },
        "isInPointInRect": function(sx, sy, ex, ey){
            var position = this.getPosition();
            var size = this.getSize();

            var p1 = {"x": position.x, "y": position.y};
            var p2 = {"x": position.x+size.x, "y": position.y};
            var p3 = {"x": position.x+size.x, "y": position.y+size.y};
            var p4 = {"x": position.x, "y": position.y+size.y};

            var sp = {"x": Math.min(sx, ex), "y": Math.min(sy, ey)};
            var ep = {"x": Math.max(sx, ex), "y": Math.max(sy, ey)};

            if (p1.x>=sp.x && p1.y>=sp.y && p1.x<=ep.x && p1.y<=ep.y) return true;
            if (p2.x>=sp.x && p2.y>=sp.y && p2.x<=ep.x && p2.y<=ep.y) return true;
            if (p3.x>=sp.x && p3.y>=sp.y && p3.x<=ep.x && p3.y<=ep.y) return true;
            if (p4.x>=sp.x && p4.y>=sp.y && p4.x<=ep.x && p4.y<=ep.y) return true;
            if (p3.x>=sp.x && p3.y>=sp.y && p1.x<=sp.x && p1.y<=sp.y) return true;
            if (p3.x>=ep.x && p3.y>=ep.y && p1.x<=ep.x && p1.y<=ep.y) return true;
            if (p1.x<=sp.x && p2.x>=sp.x && p1.y>=sp.y && p4.y<=ep.y) return true;
            if (p1.y<=sp.y && p4.y>=sp.y && p1.x>=sp.x && p2.x<=ep.x) return true;

            return false;
        },
        "isOverlap": function(node){
            var p = node.getPosition();
            var s = node.getSize();
            return this.isInPointInRect(p.x, p.y, p.x+s.x, p.y+s.y);
        },

        "getUsefulSize": function(){
            var size = this.getSize();
            var borderLeft = this.getStyle("border-left").toInt();
            var borderBottom = this.getStyle("border-bottom").toInt();
            var borderTop = this.getStyle("border-top").toInt();
            var borderRight = this.getStyle("border-right").toInt();

            var paddingLeft = this.getStyle("padding-left").toInt();
            var paddingBottom = this.getStyle("padding-bottom").toInt();
            var paddingTop = this.getStyle("padding-top").toInt();
            var paddingRight = this.getStyle("padding-right").toInt();

            var x = size.x-paddingLeft-paddingRight;
            var y = size.y-paddingTop-paddingBottom;

            return {"x": x, "y": y};
        },
        "clearStyles": function(isChild){
            this.removeProperty("style");
            if (isChild){
                var subNode = this.getFirst();
                while (subNode){
                    subNode.clearStyles(isChild);
                    subNode = subNode.getNext();
                }
            }
        },
        "maskIf": function(styles, click){
            var style = {
                "background-color": "#666666",
                "opacity": 0.4,
                "z-index":100
            };
            if (styles){
                style = Object.merge(style, styles);
            }
            var position = this.getPosition(this.getOffsetParent());
            this.mask({
                "destroyOnHide": true,
                "style": style,
                "useIframeShim": true,
                "iframeShimOptions": {"browsers": true},
                "onShow": function(){
                    this.shim.shim.setStyles({
                        "opacity": 0,
                        "top": ""+position.y+"px",
                        "left": ""+position.x+"px"
                    });
                },
                "onClick": click
            });
        },
        "scrollIn": function(where){
            var wh = (where) ? where.toString().toLowerCase() : "center";

            if (Browser.name=="ie" || Browser.name=="safari"){
                var scrollNode = this.getParentSrcollNode();
                var scrollFx = new Fx.Scroll(scrollNode);
                var scroll = scrollNode.getScroll();
                var size = scrollNode.getSize();
                var thisSize = this.getComputedSize();
                var p = this.getPosition(scrollNode);

                if (wh=="start"){
                    var top = 0;
                    scrollFx.start(scroll.x, p.y-top+scroll.y);
                }else if (wh=="end"){
                    var bottom = size.y-thisSize.totalHeight;
                    scrollFx.start(scroll.x, p.y-bottom+scroll.y);
                }else{
                    var center = size.y/2-thisSize.totalHeight/2;
                    scrollFx.start(scroll.x, p.y-center+scroll.y);
                }
            }else{
                if (wh!=="start" && wh!=="end") wh = "center"
                this.scrollIntoView({"behavior": "smooth", "block": wh, "inline": "nearest"});
            }
        },
        scrollToNode: function(el, where){
            var scrollSize = this.getScrollSize();
            if (!scrollSize.y) return true;
            var wh = (where) ? where.toString().toLowerCase() : "bottom";
            var node = $(el);
            var size = node.getComputedSize();
            var p = node.getPosition(this);
            var thisSize = this.getComputedSize();
            var scroll = this.getScroll();
            if (wh==="top"){
                var n = (p.y-thisSize.computedTop);
                if (n<0) this.scrollTo(scroll.x, scroll.y+n);
                n = (size.totalHeight+p.y-thisSize.computedTop)-thisSize.height;
                if (n>0) this.scrollTo(scroll.x, scroll.y+n);

            }else{
                var n = (size.totalHeight+p.y-thisSize.computedTop)-thisSize.height;
                if (n>0) this.scrollTo(scroll.x, scroll.y+n);
                n = p.y-thisSize.computedTop;
                if (n<0) this.scrollTo(scroll.x, scroll.y+n);
            }
        },
        "getInnerStyles": function(){
            var styles = {};
            style = this.get("style");
            if (style){
                var styleArr = style.split(/\s*\;\s*/g);
                styleArr.each(function(s){
                    if (s){
                        var sarr = s.split(/\s*\:\s*/g);
                        styles[sarr[0]] = (sarr.length>1) ? sarr[1]: ""
                    }
                }.bind(this));
            }
            return styles;
        },
        "getInnerProperties": function(){
            var properties = {};
            if (this.attributes.length){
                for (var i=0; i<this.attributes.length; i++){
                    properties[this.attributes[i].nodeName] = this.attributes[i].nodeValue;
                }
            }
            return properties;
        },
        "getZIndex": function(){
            var n = this;
            var i=0;
            while (n){
                if (n.getStyle("position")==="absolute"){
                    var idx = n.getStyle("z-index");
                    i = (idx && idx.toFloat()>i) ? idx.toFloat()+1 : 0;
                    break;
                }
                n = n.getParent();
            }
            return i;
        },
        "getParentSrcollNode": function(){
            var node = this.getParent();
            while (node && (node.getScrollSize().y-2<=node.getSize().y || (node.getStyle("overflow")!=="auto" &&  node.getStyle("overflow-y")!=="auto"))){
                node = node.getParent();
            }
            return node || null;
        },
        "getEdgeHeight": function(notMargin){
            var h = 0;
            h += (this.getStyle("border-top-width").toFloat() || 0)+ (this.getStyle("border-bottom-width").toFloat() || 0);
            h += (this.getStyle("padding-top").toFloat() || 0)+ (this.getStyle("padding-bottom").toFloat() || 0);
            if (!notMargin) h += (this.getStyle("margin-top").toFloat() || 0)+ (this.getStyle("margin-bottom").toFloat() || 0);
            return h;
        },
        "getEdgeWidth": function(notMargin){
            var h = 0;
            h += (this.getStyle("border-left-width").toFloat() || 0)+ (this.getStyle("border-right-width").toFloat() || 0);
            h += (this.getStyle("padding-left").toFloat() || 0)+ (this.getStyle("padding-right").toFloat() || 0);
            if (!notMargin) h += (this.getStyle("margin-left").toFloat() || 0)+ (this.getStyle("margin-right").toFloat() || 0);
            return h;
        }
    });
    Object.copy = function(from, to){
        Object.each(from, function(value, key){
            switch (typeOf(value)){
                case "object":
                    if (!to[key]) to[key]={};
                    Object.copy(value, to[key]);
                    break;
                default:
                    to[key] = value;
            }
        });
    };

    if (window.JSON) JSON.format = JSON.encode;

    if (window.Slick) {
        Slick.definePseudo('src', function (value) {
            return Element.get(this, "src").indexOf(value) !== -1;
        });
        Slick.definePseudo('srcarr', function (value) {
            var vList = value.split(",");
            var src = Element.get(this, "src");
            var flag = false;
            for (var i = 0; i < vList.length; i++) {
                if (src.indexOf(vList[i]) !== -1) {
                    flag = true;
                    break;
                }
            }
            return flag;
        });
        Slick.definePseudo('ahref', function (value) {
            var href = Element.get(this, "href");
            if (!href) href = "";
            href = href.toString().toLowerCase();
            return (href.indexOf(value) !== -1);
        });

        Slick.definePseudo('rowspanBefore', function (line) {
            var tr = MWF.getParent(this, "tr");
            var rowspan = this.get("rowspan").toInt() || 1;
            var currentRowIndex = tr.rowIndex.toInt();

            return rowspan > 1 && currentRowIndex < line.toInt() && currentRowIndex + rowspan - 1 >= line;
        });
        Slick.definePseudo('rowspan', function () {
            var rowspan = this.get("rowspan").toInt() || 1;
            return rowspan > 1;
        });

        Slick.definePseudo('colspanBefore', function (col) {
            var tr = MWF.getParent(this, "tr");
            var colspan = this.get("colspan").toInt() || 1;
            var currentColIndex = this.cellIndex.toInt();

            return colspan > 1 && currentColIndex < col.toInt() && currentColIndex + colspan - 1 >= col.toInt();
        });

        Slick.definePseudo('colspan', function () {
            var colspan = this.get("colspan").toInt() || 1;
            return colspan > 1;
        });
    }

    o2.common = o2.common || {};

    o2.common.encodeHtml = function(str){
        str = str.toString();
        str = str.replace(/\&/g, "&amp;");
        str = str.replace(/>/g, "&gt;");
        str = str.replace(/</g, "&lt;");
        return str.replace(/\"/g, "&quot;");
    };

    o2.common.getResponseTextPost = function(path, body, contentType){
        var returnText = "";
        var options = {
            url: path,
            async: false,
            data: body,
            method: "post",
            onSuccess: function(esponseTree, responseElements, responseHTML, responseJavaScript){
                returnText = responseHTML;
            }
        };
        var r = new Request.HTML(options);
        r.send();
        return returnText;
    };
    o2.common.getResponseText = function(path){
        var returnText = "";
        var options = {
            url: path,
            async: false,
            method: "get",
            onSuccess: function(esponseTree, responseElements, responseHTML, responseJavaScript){
                returnText = responseHTML;
            }
        };
        var r = new Request.HTML(options);
        r.send();
        return returnText;
    };
    o2.common.toDate = function(str){
        var tmpArr = str.split(" ");
        if (!tmpArr[1]) tmpArr.push("0:0:0");
        var dateArr = tmpArr[0].split("-");
        var timeArr = tmpArr[1].split(":");
        return new Date(dateArr[0],parseInt(dateArr[1])-1,dateArr[2],timeArr[0],timeArr[1],timeArr[2]);
    };

    o2.common.toDate = function(str){
        var tmpArr = str.split(" ");
        if (!tmpArr[1]) tmpArr.push("0:0:0");
        var dateArr = tmpArr[0].split("-");
        var timeArr = tmpArr[1].split(":");
        return new Date(dateArr[0],parseInt(dateArr[1])-1,dateArr[2],timeArr[0],timeArr[1],timeArr[2]);
    };

    o2.grayscale = function(src, width, height, callback){
        try {
            var canvas = document.createElement('canvas');
            var ctx = canvas.getContext('2d');
            var imgObj = new Image();
            imgObj.src = src;
            canvas.width = width || imgObj.width;
            canvas.height = height || imgObj.height;
            ctx.drawImage(imgObj, 0, 0);

            var imgPixels = ctx.getImageData(0, 0, canvas.width, canvas.height);
            for(var y = 0; y < imgPixels.height; y++){
                for(var x = 0; x < imgPixels.width; x++){
                    var i = (y * 4) * imgPixels.width + x * 4;
                    var avg = (imgPixels.data[i] + imgPixels.data[i + 1] + imgPixels.data[i + 2]) / 3;
                    imgPixels.data[i] = avg;
                    imgPixels.data[i + 1] = avg;
                    imgPixels.data[i + 2] = avg;
                }
            }
            ctx.putImageData(imgPixels, 0, 0, 0, 0, imgPixels.width, imgPixels.height);
            var src1 = canvas.toDataURL();
            //var blob = canvas.toBlob();
            canvas.destroy();
            return {"status": "success", "src": src1};
        }catch(e){
            return {"status": "error", "src": src}
        }
    };
    o2.eventPosition = function(e){
        var x = 0;
        var y = 0;
        if (Browser.name=="firefox"){
            x = parseFloat(e.event.clientX || e.event.x);
            y = parseFloat(e.event.clientY || e.event.y);
        }else{
            x = parseFloat(e.event.x);
            y = parseFloat(e.event.y);
        }
        return {"x": x, "y": y};
    };

    if (window.Browser){
        if (Browser.name==="ie" && Browser.version<9){
            Browser.ieuns = true;
        }else if(Browser.name==="ie" && Browser.version<10){
            Browser.iecomp = true;
        }
        if (Browser.iecomp){
            o2.load("ie_adapter", null, false);
            o2.session.isDebugger = true;
            //layout["debugger"] = true;
        }
        o2.session.isMobile = (["mac", "win", "linux"].indexOf(Browser.Platform.name)===-1);
    }
})();
o2.more = true;

(function(){
    //dom ready
    var _dom = {
        ready: false,
        loaded: false,
        checks: [],
        shouldPoll: false,
        timer: null,
        testElement: document.createElement('div'),
        readys: [],

        domready: function(){
            clearTimeout(_dom.timer);
            if (_dom.ready) return;
            _dom.loaded = _dom.ready = true;
            o2.removeListener(document, 'DOMContentLoaded', _dom.checkReady);
            o2.removeListener(document, 'readystatechange', _dom.check);
            _dom.onReady();
        },
        check: function(){
            for (var i = _dom.checks.length; i--;) if (_dom.checks[i]() && window.MooTools && o2.core && o2.more){
                _dom.domready();
                return true;
            }
            return false;
        },
        poll: function(){
            clearTimeout(_dom.timer);
            if (!_dom.check()) _dom.timer = setTimeout(_dom.poll, 10);
        },

        /*<ltIE8>*/
        // doScroll technique by Diego Perini http://javascript.nwbox.com/IEContentLoaded/
        // testElement.doScroll() throws when the DOM is not ready, only in the top window
        doScrollWorks: function(){
            try {
                _dom.testElement.doScroll();
                return true;
            } catch (e){}
            return false;
        },
        /*</ltIE8>*/

        onReady: function(){
            for (var i=0; i<_dom.readys.length; i++){
                this.readys[i].apply(window);
            }
        },
        addReady: function(fn){
            if (_dom.loaded){
                if (fn) fn.apply(window);
            }else{
                if (fn) _dom.readys.push(fn);
            }
            return _dom;
        },
        checkReady: function(){
            _dom.checks.push(function(){return true});
            _dom.check();
        }
    };
    var _loadO2 = function(){
        (!o2.core) ? this.o2.load("o2.core", _dom.check) : _dom.check();
        (!o2.more) ? this.o2.load("o2.more", _dom.check) : _dom.check();
    };

    o2.addListener(document, 'DOMContentLoaded', _dom.checkReady);

    /*<ltIE8>*/
    // If doScroll works already, it can't be used to determine domready
    //   e.g. in an iframe
    if (_dom.testElement.doScroll && !_dom.doScrollWorks()){
        _dom.checks.push(_dom.doScrollWorks);
        _dom.shouldPoll = true;
    }
    /*</ltIE8>*/

    if (document.readyState) _dom.checks.push(function(){
        var state = document.readyState;
        return (state == 'loaded' || state == 'complete');
    });

    if ('onreadystatechange' in document) o2.addListener(document, 'readystatechange', _dom.check);
    else _dom.shouldPoll = true;

    if (_dom.shouldPoll) _dom.poll();

    if (!window.MooTools){
        this.o2.load("mootools", function(){ _loadO2(); _dom.check(); });
    }else{
        _loadO2();
    }
    this.o2.addReady = function(fn){ _dom.addReady.call(_dom, fn); };
})();

COMMON = {
    "DOM":{},
    "setContentPath": function(path){
        COMMON.contentPath = path;
    },
    "JSON": o2.JSON,
    "Browser": Browser,
    "Class": o2.Class,
    "XML": o2.xml,
    "AjaxModule": {
        "load": function(urls, callback, async, reload){
            o2.load(urls, callback, reload, document);
        },
        "loadDom":  function(urls, callback, async, reload){
            o2.load(urls, callback, reload, document);
        },
        "loadCss":  function(urls, callback, async, reload, sourceDoc){
            o2.loadCss(urls, document.body, callback, reload, sourceDoc);
        }
    },
    "Request": Request,
    "typeOf": o2.typeOf
};
COMMON.Browser.Platform.isMobile = o2.session.isMobile;
COMMON.DOM.addReady = o2.addReady;
MWF = o2;
MWF.getJSON = o2.JSON.get;
MWF.getJSONP = o2.JSON.getJsonp;
MWF.defaultPath = o2.session.path;


o2.xDesktop = o2.xDesktop || {};
o2.xd = o2.xDesktop;
o2.xDesktop.requireApp = function(module, clazz, callback, async){
    o2.requireApp(module, clazz, callback, async)
};
o2.xApplication = o2.xApplication || {};

MWF.xDesktop.loadConfig = function(callback){
    o2.JSON.get("res/config/config.json", function(config) {
        layout.config = config;
        if (layout.config.app_protocol === "auto") {
            layout.config.app_protocol = window.location.protocol;
        }
        layout.config.systemName = layout.config.systemName || layout.config.footer;
        layout.config.systemTitle = layout.config.systemTitle || layout.config.title;
        if (callback) callback();
    });
};
MWF.xDesktop.getService = function(callback) {
    MWF.xDesktop.getServiceAddress(layout.config, function(service, center){
        layout.serviceAddressList = service;
        layout.centerServer = center;
        if (callback) callback();
    });
};
MWF.xDesktop.loadService = function(callback){
    MWF.xDesktop.loadConfig(function(){
        MWF.xDesktop.getService(callback);
    });
};

MWF.xDesktop.checkLogin = function(loginFun){
    layout.authentication = new MWF.xDesktop.Authentication({
        "onLogin": loginFun
    });
    layout.authentication.isAuthenticated(function(json){
        layout.session.user = json.data;
        if (loginFun) loginFun();
    }.bind(this), function(){
        layout.authentication.loadLogin(this.node);
    });
};

MWF.xDesktop.getDefaultLayout = function(callback){
    MWF.UD.getPublicData("defaultLayout", function(json) {
        if (json) layout.defaultLayout = json;
        if (callback) callback();
    }.bind(this));
},
    MWF.xDesktop.getUserLayout = function(callback){
        MWF.UD.getPublicData("forceLayout", function(json) {
            var forceStatus = null;
            if (json) forceStatus = json;
            MWF.UD.getDataJson("layout", function(json) {
                if (json) {
                    layout.userLayout = json;
                    if (forceStatus) layout.userLayout.apps = Object.merge(layout.userLayout.apps, forceStatus.apps);
                    if (callback) callback();
                }else{
                    MWF.UD.getPublicData("defaultLayout", function(json) {
                        if (json){
                            layout.userLayout = json;
                            if (forceStatus) layout.userLayout.apps = Object.merge(layout.userLayout.apps, forceStatus.apps);
                        }
                        if (callback) callback();
                    }.bind(this));
                }
            }.bind(this));
        }.bind(this));
    },

    MWF.xDesktop.notice = function(type, where, content, target, offset, option){
        var noticeTarget = target || layout.desktop.desktopNode;

        var off = offset;
        if (!off){
            off = {
                x: 10,
                y: where.y.toString().toLowerCase()=="bottom" ? 10 : 10
            };
        }

        var options = {
            type: type,
            position: where,
            move: false,
            target: noticeTarget,
            offset: off,
            content: content
        };
        if( option && typeOf(option) === "object" ){
            options = Object.merge( options, option );
        }
        new mBox.Notice(options);
    };
MWF.xDesktop.loadPortal =  function(portalId){
    layout.openApplication(null, "portal.Portal", {
        "portalId": portalId,
        "onAfterModulesLoad": function(){
            var layoutNode = $("layout");
            if (layoutNode) layoutNode.setStyles({
                "position": "absolute",
                "width": "100%",
                "z-index": 100,
                "top": "0px",
                "left": "0px"
            }).fade("out");
            var appContentNode = $("appContent");
            if (appContentNode) appContentNode.setStyles({
                "position": "absolute",
                "width": "100%",
                "top": "0px",
                "opacity": 0,
                "left": "0px"
            }).fade("in");
        }
    }, null, true);
};
MWF.name = {
    "cns": function(names){
        if( typeOf(names) !== "array" )return [];
        var n = [];
        names.each(function(v){
            n.push(this.cn(v));
        }.bind(this));
        return n;
    },
    "cn": function(name){
        var idx = name.indexOf("@");
        return (idx!==-1) ? name.substring(0, idx) : name;
    },
    "ou": function(name){
        var idx = name.indexOf("@");
        var lastIdx = name.lastIndexOf("@");
        if (idx===-1){
            return name;
        }else if (lastIdx===idx){
            return "";
        }else{
            return name.substring(idx+1, lastIdx);
        }
    },
    "flag": function(name){
        var lastIdx = name.lastIndexOf("@");
        if (lastIdx===-1){
            return "";
        }else{
            return name.substring(lastIdx+1, name.length);
        }
    },
    "type": function(){
        var lastIdx = name.lastIndexOf("@");
        if (lastIdx===-1){
            return "";
        }else{
            return name.substring(lastIdx+1, name.length);
        }
    }
};
MWF.xDesktop.confirm = function(type, e, title, text, width, height, ok, cancel, callback, mask, style){
    MWF.require("MWF.xDesktop.Dialog", function(){
        var container = layout.desktop.node || $(document.body);
        var size = container.getSize();
        var x = 0;
        var y = 0;

        if (typeOf(e)=="element"){
            var position = e.getPosition(container);
            x = position.x;
            y = position.y;
        }else{
            if (Browser.name=="firefox"){
                x = parseFloat(e.event.clientX);
                y = parseFloat(e.event.clientY);
            }else{
                x = parseFloat(e.event.x);
                y = parseFloat(e.event.y);
            }

            if (e.target){
                var position = e.target.getPosition(container);
                x = position.x;
                y = position.y;
            }
            //    }
        }

        if (x+parseFloat(width)>size.x){
            x = x-parseFloat(width);
        }
        if (x<0) x = 0;
        if (y+parseFloat(height)>size.y){
            y = y-parseFloat(height);
        }
        if (y<0) y = 0;

        var ctext = "";
        var chtml = "";
        if (typeOf(text).toLowerCase()=="object"){
            ctext = text.text;
            chtml = text.html;
        }else{
            ctext = text;
        }
        var dlg = new MWF.xDesktop.Dialog({
            "title": title,
            "style": style || "flat",
            "top": y,
            "left": x-20,
            "fromTop":y,
            "fromLeft": x-20,
            "width": width,
            "height": height,
            "text": ctext,
            "html": chtml,
            "container": MWF.xDesktop.node,
            "maskNode": mask,
            "buttonList": [
                {
                    "text": MWF.LP.process.button.ok,
                    "action": ok
                },
                {
                    "text": MWF.LP.process.button.cancel,
                    "action": cancel
                }
            ]
        });

        switch (type.toLowerCase()){
            case "success":
                dlg.content.setStyle("background-image", "url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACMAAAAjCAYAAAAe2bNZAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAB1hJREFUeNqsWGtsVEUUPnMf+y6rLcW2tDxUKARaikqgiWh8BlH8IwYkaozhh4nhB1FMTKkxQtQYQzRGE2JEfMRHYhQSVChgFYIGqLSUtoKUQmlp2b53u233de94zuzcZbfdbhdwkpPZmbl3zjffnHPuOcue/WgxZNnc3OT3cQ4rGIMlwNg8BjATGEwDDgHOeZdpQis3eKMR5Sd62kaO/PHp5QDub2ba9OtNTYnf2lQIcOO5igpr8eeT3kL9XneuCi6vAvYcFWxOBqrO6BlvZIx7w8PGwlG/uWZkwADNzo4//e7CfQMdYz/88t6F8/i+icB4Jl0sEzPIxEbsXiwotVd6C3TwTFezZRGCfQb4r0bhSnPo78io8dWP1ed24nRkPFNTMoMnnYNsbGYK2zR/pYsRGxJc1mDcuQqKHbwF2t3/Hh29a+3bC8oHOkM7UPk5UpGOpQQzFsINHyxahDaxdeYix/r8223AFLjpxpGL3rYIXDw5um+gc+ydwx9fqsPpKC0lP6eWr54hfjT+2gPP7Fg0R1HgreIyx/rpc2zxjfjNCzXXrSo4PMr8sWFecEuRo6mjMdBPdpQMJuWa6GoKF9jX55bo13UlE5jg8szobshyotG+RtT1OJrBAA43o/hRYhOYKVuVvxFtZPusCie7GUbQvcnmIBbh4noEoqR15zQV/N1GeXFZzvD5Y4P1ydclwJD7om1sn3uPs0S3x1++ESHlJgJB74FiXgkD4XZQLGr4NQtBh2DDvWa+3aOd7D4b7CGDFjcjr2dt3mxbpQNjB53sRsTA7YiN0IgBRWYlrJz2suhpTPO0bj1LegpKHWWFpZ6nUL0ngYOAUkBz34JAYjytEO1GJN5Pth4LmRAajkGxuQJWFb0CLpdL9DSmeVpPfp/0uXP1B2+b5y5A/cJbVLSVh9252uu5M/WM1BMYSLKBdFczS6mEx0peBbfbDU6nE1RVhdnOZdDj78AruyyvLP6+ZmMQDQMCYc3tp/xnKSAq9K2xuxmYBp8oeIJY2ITwSAxm8uWip7E43bj1ErYCHpsVB0KsOBwO0dOY5mdrlXhdSe+ikN6cPNtSeTsqgV2iOxRchFRBh4uGOSpCY8QTP5C/SfQ0pnkjmrq+es6WBBBN0wQrNpsNvF4vFBYWwgvL3ofFeY/EmZQ6SK/do5YiECeFGYW+vprGUu0AaY/iHYeDceqfmLtFKKGexjRP15K8ngxEUa6FbfpNwH5qfQua+w8lGCUhvbpDLZE2g8xgGkAhP4WRCJ3YhFk6KrozrignJ0f0NKb50LCRsp4OCJNu/X3LG3Cm92Dcm5LYJ71oO9MtMJrIRyguGzwRPelu5zoqYc28a4rodLqui2eexPk9/3DRTwXku6ZqaOo7KOw2bdqgMLf8EigaJUaxCHgT+yCY8hmPwrrFb4oNLbEUkGITj7iuoloozwTk28ZqONMzOZA4U3w07mLANMrQ0CO85GpWO+M7iKsMNlRsk2zxxP2TYo/HIwBZ43RAvmmohkZfzaRAqIlgGDH7rEChUaqIXrFQUVPfauiqEcifvWubUJAMiLwkLeUSyNenEMjVzECokTdGQman/FiaGuWs6DlrdNvENxs6DwCuw3PLtqcAygTkq5Nb4XT31EAEGIragVgrBTz6PmmUPBNdppH+hfrOGhEbnl8+OSALyJfHtwpGswFiXdNgV6jFAqPm3+7yOb36A5pdKaY906UF3f4LcNXfDhUlDyUUjwey+6+qOPAs0w8KH0NXI00nvu/aFQoaPnxtWKFyAhHui4Yw/0B20goyU3+5BnYfq0oASPYymqd1em7SPcYJ6fP7wn8OdYcp0RoRzFBiHPCFexRdqdR0VsRkzjpBiKGhC+BDhpbOfijBzOdHq+BU+4H4ic3sJIYRPtAbbWk+1Pv54JXQRdxmiExI+CTVNVROjI2YPGPeggrrLh2AXUeqBCvU09jk15f7kJ6+S6P7244PUT0VkDYTz/QoGf+ntr9h/srcIs2mLFVY5oyua7AVfIF2qGvbn5rFZSHESn9HaG/Nhxc/wxmylUErDxbMyBomQnVNcDC2Lyq9a1LB051o3T/hWzOV0L6D3eHalsN936K+PgkkYiWkyVWR+dsnl85RXRP0R3+OxbioEP4vof2GfOHac0f6v7h4cqhZghlNLldS6iZCiA/6qK7RnapLtSvlwCm43ES1QFdjco6s722q6d2NFcFp1NMjbSWWsdbGypIshj7POatfu+MlT55tnd2lljHOso1l18yIYYIeNFrIWGt3tv8o2SAZJu8h80iutRPMWE0aNFEXobqGygk0ar+iM5eqswIrqE0w3ASAeD8WjDX1d4ztIfet3+v7XRprL/0nQIxYtba8kan/hUDUikx8PJTFl96fdx/lrJQqUoZGiRHlI5QG0NeXPnr0raEQf7a2r04GtICU4FT/QmTDPJOGTqAcMnl2yrFNJkZWMIhJ7yAZk5E1JMfm+EI/naLraQRKlQBUKUoSGFNWh4YEZowv7jO1/wQYAIxJoZGb/Cz/AAAAAElFTkSuQmCC)");
                break;
            case "error":
                dlg.content.setStyle("background-image", "url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACMAAAAjCAYAAAAe2bNZAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAABsVJREFUeNqkWFtsFGUU/nZn2r21IqX3llp6AQmkWDVGAgIlGI0EeMAHffAFa998MCQaE8JDxZCgSHzQKIm3qPHFGC7GW0xqkIgIKhhEwFJaKSDQUtplu73s7vidmX/q32F2uw2TnOzMv2fO+f5z/8fci7yvWAZYRXo4CCwLAM1cq+HvXRYwQrrM/7rTwB+TwC/dwKG3uU75mVxCO7T7wExgKHiBATzJ2411wMoy3pSQ5gg6UiFpgpQgDZNukK6TLgBHuf7lAPD5q8DfXMpQl5U3mA4P4ztAO3+2tADLCQSV+VsR/5L+If0G/EqgH78EvKtwT1lqr0en6SfoLaCe1niB7nj+CQIuV+uZWYApV8RNPPAVcP/rQMtF4I03gbNcpjdvt5KxQXs4SKKflxBI54PAs20EElNvZTQJucjLFyUtpZwioJVurFtMD/4MXBXWDUqnL5jHHYt0PgQ8da/4UFMwThpTz0HF7wfEj0/kSKwVAwsZU5U1wKkTwOBBj7GD08xE17QSSJPanVCKlCSNkM5s2mT/JtV6epZ8InclsH4R9TjYRKWPZQixnch2POJsZNpOb5HOb9yIi5s3I5XJIHb2rL2LoBZL+fBZKhOZaS3LgPgh4HcnYZ34scFI+goQxsj8iA+QHipItrejrKwMiaVLMZJIIEpFAaUkH76AFrEVfLxEzzEej/0FXFOGc8CQ8bmFTOE6DciEUnCBCsapoLGxETU1NYhGo7i+YAHiSlFauWMmvqAGKOzcVzDlh2mdo2o/loCJkeEVRnldSMsGUdCrKaiqqkJxcTEikQgKCgpsRbJzk4oukm8iB1+CfEUKkLtZub/CZOsFvht0Qi1lrAfW0WwvN3gyI7J1K+7ZswfNzc0oLS1FKBRCMBiEaZoIh8OOovp6jI6NYXLLFjQ1NdlAxCKGYaCwsBAlJSWoJ08lwQZTKaSPHJmSL9YZZWZx438eZ8yLMwwWtWeYaqvv9oBJ8UWDyovWrUMgEPi/ZPPeBWT/rlhhx0h1dbUNRABPpSrvBVhixw4kd+26rRyMOq3jCl31kzya0vSiKgW91/DOnbZJ53V22iAsy5pSIopra2vtNflP3KIDcTcwuH074pQT8JEvelkMF4kjpBuY0n1Dbjj7XDcpSCCU+gCKxWK+77hABghkOAsQuUIOivmq3xrSm2qMLJZxrwEKlGJQ5QGUC8gVBSSYQ67hoCidAiPzSCCHZSxVlXopeHhiAk30v8RBtivFQO3etg1Du3fbbihQKe0L3MmqmGrYwaAMRuPKMl6aVCkeJ11jRvSuWYO+vj4kk0lf4bIu/wuf8MfV+5NZ5I87RhhVuAKmTGhsbHPCWSwiwoYoOMQ60tDQgPLycjvNfWOA6/J/Op3GefJzsMLcAwfs6PSz0JhTXAfcBDNlVCS0xaYHSEql3jCBRLSC5k3faV1XZZnwySWABmUqJKCo8oUOaNTZbL9SlzE4Niwh8lURLf/TyoQzAZFgdcmvDklhjKsKXKAqsF5rZEztAboOAz+KA4xHmeo0+tNFqky7VMkKfJ+nAnuV2rtn1pS0td32n16B67kpRjZuqQrs6pB5mW37s5OswoLNaOTUdRfQRjPWGhrqOF80aYVSTwXWgfQQSL8URiqa6wGkV+B+ZuAlTwUWF/VxyPoUeD/uTH5x4xhjiNapoHXWhj3l+ubhw0hTkbtz3SXdBNJHIJgFn+Vx0Tlg37eOi+RAkTTk+MDueY1WWc64qQ5oZpSXhpSiedrOz1HBBVWZZ8Pn0phzcjj9DfBBvz1r4aYkrz3PvEhZq9lIyfgY3RXwzrY3lKKytWtxhgp6fHaaL5+AoU8stulPvgB+UFZJuPOMPaF/D5wgoGq6q9XMosianER3FiD58iWcDNr/GvCegwtDbjeywShAGQ5Y3aYzZC00PELsDkxFmOGokosv6cy/XV8DHyr3XFfL1rSBnL/WNqKUcw3rQWWhD6A7oaSTPV1dwEecX07CmX1v6W3Re4iz5IAl5xqCiTIMW0zJ5DsAkXKOLxbHy/1iEQ3IiHdYmAbGdZccsBhDXXKcoMAyWqjCynJwywVCqjgbz2kJVokR5RoXyKRkctYTpQ5Iepica+Q4QesMU0GUoCozPjGS0QZ5t9uzJ51ioO6T9FVZc1XFiLgm5X6ROJjvJ5EOZ4iXwaeIs2Elz1WreExtlVFRJjQZjGQekTFAuq80PRazbp6JTtOyxy87FX9EkYCY8H6v6fDMNzNdagayQYXVZ5mIei7UmrHrnQlFSZXJY9qnECuXIjMPMJZ2lHIPj6aaGg0FNOD5CJHWjtl5f0n5T4ABAFHaXG6UVjGNAAAAAElFTkSuQmCC)");
                break;
            case "info":
                dlg.content.setStyle("background-image", "url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACMAAAAjCAYAAAAe2bNZAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAABvBJREFUeNqsWF1sFFUUPndm9n+3W5aWLi2VGgJtgy3UEn6MQGI0GgmC0UgfTEjEBxPDA29qYqLGGOODifGBGGOUYOKDPIg2QgykWgUtP1WgLT+lFKFCf9l2uz+z83c9Z3p3u1u6u4Pxpqczd+7MPd8999xzvrPskb2fgsMW4NzaBpxvBsbWMWCrgUEdAKsA4HHO+R2wrOucmxe5qZ9Jjt3ovtX1eRznt0pN2ndof+5eKYcAJ34YJPlFvH3OFV7+uOyPgOQLg+wJAXP5gMkueifM9XTYzMw2W+mZnWbqHjDF09Pc8WFneur2kaHOjwbxewuB8VK6WCnLoCVexcsrnmWNW1zhKMiBKqdWBDM5CfrMKKh3+8+bWurw1W/f/gwfawstVdYyuNIGtMYBxqT9/lVbmRyIZMFlUeKfCdyiPi0WN02ScPdkvGX2KxJa0IOiVETbU0O/Ptr00getamzkY1R+lbAuZiV52fpnC4FY5lqQpPe80bX7/A2bmIRbQcpzggAQLFhaGiw1aV+5nqEPEQcjWDnAJJLLC57q1Ux2+9tATzwUXN40PH3j7Nj4hWMW6cbr4mDmLIJAals63Esbsk8LhFsGAkjBY3UaPN8M8HKbBGsiHBRmwK1pEy0kC+Pkf4eK/EtA8gTX8Mxs1Lukti9+6+IUAco3ROE24dZ4apo6XEvq57dkQbPQKtsQ575NleB1z30erQbYsMoApScJ3bd1kMRWLWw0r9/Ud+Ci72H3AMoMinGfZchZ0Ufe961Yz/LNvFBoi/ZuDMKaukoIBAIQDofB7XaD1+MGl8Thl6EMWkYq+r3srQAzfrc1VN8yG7t26k/UpGfNJ+WOL54ab30746TQMkuIBVuaaiAUCoHf7wdFUewr9ek5jZf8HucnPe7Q0j3R9t0tqNdtn4AsGIoj7sjKLbI3ZDtiKSEnvTyqgSzLhScB+/ScxsvNQXq8NY0twdrGF/DTYBYH/QtQQJN9lbZzlhOa7MRADHRDnB4h1KfnNO5kHtLnCkSeCERXR4V1QK5e98yTij/ypquyrug+Fwhu7+BoGsbjGngVCaoq3NA7PAuHT4/BjxdjUMrf8oUpqN/IRNGO/TM3e69QQFQo1zB3wN7PMokht+802Q/nUij/5MVyNnesJTnrAmUb6UXfacPb71ESCiU9CkxQBsxcfFHB0tXFjz2CkRQP5iw/AlIcgSG9sjfYiLc+CjMKZV8mk4GM0mBw/MDTUdjc4ANVVUHXdftk5AIWnqozf6tw8FQc44yz/EV6ZZe3XvgM9ogGUFwoYxmav7IyAitXLgNN0yCRSNiAcgHN5YJdyyU42N2LSzYdopHId6rmwdh8BBz4DMA7Ry7D71fG4d2OFjvQFVqOg2EY837lsGGADIhMIFGojIOpoWUMB2LCsd4RSGdKbKmjeYSgXgSeEoZnCjE0y8iEMa06Wgk3DQxOJiZvdFhJWsTRnVuGIxjL0CazGVWxqaKeaba5iLMZcoGu2Dg4BYPUA0/niEiWlkKc1TLUnXYQcjKBZZQd55azhaFeMNLx6xTwiHApRJ65oTleTdn3rAewDOpVY3cGcmCIxQfrPD3I6DYRuS5vGbPsuBOfISqiJyb7Jge6zmE3TVslUTmBCDs5miy3qqJCJ6CMItPMnbxSQvoyM2OnM9N3iWglbcsQW6dyAq2yW5Hk9rncUiQ3oSKT9hnjCTkwRd15DKb93DRwkQwToVw8R5Hl0CoDscE/TmI3jqLSBttnk+oaKiesTJIT4V5MuGHY5Ht7cxWk00jGrcL8RH16TuM2STcMKDYX6UlN3Dw+PdQzKMBoOdpJDH1qoOuvSOPWWklxt9krWkg3cTVv7NkAr+3aaFNNsko+n6G+z+eDra0PQU2lD37rv7MonSBfUaduHx0+/skXODqGEsvyYNsyoobRqK4xUrFOCkZ2vMgThqYPBUMQDAbtYJcPJCv0nMbpPXp/4Rw0L/pI12T/yW9Q36QAomU5cEFFiQWW0vDU6xu9kRVvuXwVO+wE+n81pB2Z+HjX1JXuQ1NzJ2i0aHVADbeLU4FFdY3s9vkll6eVAWcLa6cHFeQ/XL03cnTi0k9fYUVwgVQJXzGKVpTCfywqsBB9F5UTyDmq8aTVsP8Cgk5ZJjGQHL32NfkIBrjhPCA6uUfRijIfEO0l1TWKJ3gWnXoG61w/U1zRnFPC/VVjlvFRM9REH4aM7yYunfhy7PzRn4WzThC9pOFsrZ0PpuSvEOhDkiA+QWLxS5u2byPOSlSRGBoRI+IjRAMo+1LSo1xDIZ4iqwhocSGJcr9COCGITJw6AuUVpY1P9N2CGDFhHkOcDk2E+KQIaNS3Ck24uKIHaQRKFgBkIVIeGFJoCjHE1XI6+b8CDABnZtjY0mkIGQAAAABJRU5ErkJggg==)");
                break;
            case "warn":
                dlg.content.setStyle("background-image", "url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACMAAAAjCAYAAAAe2bNZAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAABgtJREFUeNqsWG2IlFUUft6vmdlZd539GFdTY5VMomy1oBJUSPLXkmQt5I8gCIMK+iH0K4ooEvtTRP7JX9JKWCC1mUUkIkQKSoaZH60t2pboOK37Mc6Ozsw779t57t5xx5ndnTvhC4d373nnnnPuOeee85y1Jr+G6dNcCrBB6AnbQo9tY4UFLLYstIYhMsK/IjRULOF0voATx87jp60fICPygzmFbpn+26pnzK0ilrk2+kTp5kgC6+w4YDfJxpiQJ+QAYUmoKHQLCG4K5YDsCI7fzOPgcBr7172BP0VUILrC/22MnHSbvF6KLcRap1WMmGfsRQRZ2Z8BJv7BybEs9t6/DbuFXaj2VKUx7ize6BZvbHcdvB67D5bdrD/ocwUiruiLZPFGSbziiHci4iVPpEn41MM9pPZWPBofwiOX9uDh05fwkSgf5Dln8lKNZwo+HnRsvBVbjK1eJ39RdWIxJCfhGB0HxjNTBtGQhHiuPQHEY9MG3X5EbXEESA3i4KmL2Ln5Xfwi3CINmjVM9IjnYGeTGOJ2zOx+SU5cE8Hp/DMIopvgxFejlDsFO38IC6ID6JIDRCMz7/WvA1cG8d2PJ/H2y5/gLA2a9ndVmBia2CIxpL3yJ1XG5MUzTh8S3e/B9Zo09x74xSeRS7vyfT+i3sx7KXfRCvSuz2NUltuFJmhj+btdmazMEa+NsZidfIYlsQXzE51oa2tDV1eXenNNPr/PtZ/ylybxwtEP8Ypw4pU22OXrq27NvZIh4dzCeI07lvQiHo8jFovBdV315pp8fp9rP+VTT/cCPL/jRawSTqScma4OT1+sA2vtqN4w552V03meMsKyprLblowlj2s/qC+DepJLsWpjD56T5aDOnRI908yC5jTVOVEl1THWhKhPwrVx/UNYqL0DmyU+0iyVNWooKKxjTGgmh/o6k+h5tRcbhBNTDla9JtKAV+6SZ5RBondZF9YwOkKOq5qeZ6CkUpmJMQYP9Xa0YqX8ySRxXdV9bXMBloShnLg134RvhQ3IEr2tTViqc8ZxNQwwFuCJANsqiOJ4jSHke40cTPQ2RdFZNsYmHrEaiHVEmqI/drTGO+paC5/fTWVRghTaZl1ibJvAqG6hqqygIsG+/iXCID8VFk1ck+9Z5rKoV8BYThc9yyVCE2A0nyDJKOmEoiP98GV7mNwKO7EOwfjPwL9fKL7q2CUzWTRGANiILgghjRkKfTwAyxw4cWt4pR+F4X72NAn2FIxQzg4aECMtcmISl3WzDFxi1sDH046hZ4JQ45kbgmeyFXhGUGB7i8YzhgcTvbg2jiHCKPrTJXgmE56ZgKIoH5XGn/YEz3QLnpm/GrcmTiE9dkiOOaBuU9QzN+bsMM7dNoYo/qk1OC597vEahDbDU5BtuVbBMysr8ExS45lBV74LnjHwMhFjahRndn2rUN9NhsrmOEEUX/LNbgB/F13yLBJtyTvwDNfkNyLnj8s4dv5vBbQmVdcmWuc4IYl0MjC44jz0guWb0NLSojAMoQTfXJPvGNQs6hGvnNt7GIeFkyGk4hcVGM41HCcEZIV1ix53jJ+QieDOWKi18CN2fWOo58QF/PD5ETVPZXTO3IZ8Aeea9Dj2FOt4R7WDq1L0SlVFT9bke3WMofzf/8I3fTvwlXAYomy5IChj9AxT4FyTmsBBPyyXoVpSRe9qP8LfXkNw7ZAaIfnmmnwbs++l3AspHPl4APuEw2I3pr0S1owqMsO4B97BYz3L8eaiFvR6uHsPceWFNI7s/h6f7TqgblBq1umgPCRwwOJcc3EEe3NsOXN4yYRUkRQ5vw5j4P19+FQbkha6Ud04aiZK8Y6lS2ALxwmi+GQcqxyGKDT3RCBSSkKpLM4xWXWOjGi6UXeirDKI1yXOcYIonuC5s1lQoTbKKlPZCdYUBZpSGZxhHeH11bdmVOdIrnLWNv4vhPzQ1sBnHlE8wTMxK6EiERqBEfEIYQC7L5seew1LPCurLmgZTdl6/4UwaWmWzq2IRvHNGrNGNLmYdpCvb0dBl/hJXdAKJrOF1eClsHX4XP12NM+qGFJKmnz9NgYV/wkwAMYATK0QLuhAAAAAAElFTkSuQmCC)");
                break;
            default:
            //dlg.content.setStyle("background-image", "");
        }
        dlg.show();
        if (callback) callback(dlg);
    }.bind(this));
};
MWF.xDesktop.getImageSrc = function( id ){
    if (layout.config.app_protocol=="auto"){
        layout.config.app_protocol = window.location.protocol;
    }

    var addressObj = layout.serviceAddressList["x_file_assemble_control"];
    if (addressObj){
        var address = layout.config.app_protocol+"//"+addressObj.host+(addressObj.port==80 ? "" : ":"+addressObj.port)+addressObj.context;
    }else{
        var host = layout.config.center.host || window.location.hostname;
        var port = layout.config.center.port;
        var address = layout.config.app_protocol+"//"+host+(port=="80" ? "" : ":"+port)+"/x_program_center";
    }
    var url = "/jaxrs/file/"+id+"/download/stream";
    return address+url;
};
MWF.xDesktop.setImageSrc = function(){
    if( !event )return;
    var obj = event.srcElement ? event.srcElement : event.target;
    if( !obj )return;
    obj.onerror = null;
    var id = obj.get("data-id");
    if( id )obj.set("src" , MWF.xDesktop.getImageSrc(id) );
};
MWF.xDesktop.uploadImage = function( reference, referencetype, formData, file, success, failure ){
    this.action = new MWF.xDesktop.Actions.RestActions("/xDesktop/Actions/action.json", "x_file_assemble_control");
    this.action.invoke({
        "name": "uploadImage",
        "parameter": {"reference" : reference, "referencetype": referencetype},
        "data": formData,
        "file": file,
        "success": success,
        "failure": failure
    });
};
MWF.xDesktop.uploadImageByScale = function( reference, referencetype, scale, formData, file, success, failure ){
    this.action = new MWF.xDesktop.Actions.RestActions("/xDesktop/Actions/action.json", "x_file_assemble_control");
    this.action.invoke({
        "name": "uploadImageByScale",
        "parameter": {"reference" : reference, "referencetype": referencetype, "scale" : scale || 0},
        "data": formData,
        "file": file,
        "success": success,
        "failure": failure
    });
};
MWF.xDesktop.copyImage = function( reference, referencetype, attachmentId, scale, success, failure ){
    this.action = new MWF.xDesktop.Actions.RestActions("/xDesktop/Actions/action.json", "x_file_assemble_control");
    this.action.invoke({
        "name": "copyImage",
        "parameter": {"reference" : reference, "referencetype": referencetype, "attachmentId" : attachmentId, "scale":scale || 0 },
        "success": success,
        "failure": failure
    });
};
MWF.xDesktop.getPortalFileUr = function(id, app){
    var root = "x_portal_assemble_surface";
    var url = MWF.Actions.getHost(root)+"/"+root+MWF.Actions.get(root).action.actions.readFile.uri;
    url = url.replace("{flag}", id);
    url = url.replace("{applicationFlag}", app);
    return url
};
MWF.xDesktop.getProcessFileUr = function(id, app){
    var root = "x_processplatform_assemble_surface";
    var url = MWF.Actions.getHost(root)+"/"+root+MWF.Actions.get(root).action.actions.readFile.uri;
    url = url.replace("{flag}", id);
    url = url.replace("{applicationFlag}", app);
    return url
};
MWF.xDesktop.getCMSFileUr = function(id, app){
    var root = "x_cms_assemble_control";
    var url = MWF.Actions.getHost(root)+"/"+root+MWF.Actions.get(root).action.actions.readFile.uri;
    url = url.replace("{flag}", id);
    url = url.replace("{applicationFlag}", app);
    return url
};

MWF.xDesktop.getServiceAddress = function(config, callback){
    var error = function(){
        //MWF.xDesktop.notice("error", {"x": "right", "y": "top"}, "")
        var loadingNode = $("browser_loadding");
        var contentNode = $("appContent");
        ((loadingNode) ? loadingNode.getFirst() : contentNode).empty();
        var html= "<div style='width: 800px; color: #ffffff; margin: 30px auto'>" +
            "<div style='height: 40px;'>" +
            "   <div style='height: 40px; width: 40px; float: left; background: url(../x_desktop/img/error.png)'></div>" +
            "   <div style='margin-left: 50px; font-size: 20px; line-height: 40px;'>"+MWF.LP.desktop.notice.errorConnectCenter1+"</div>" +
            "</div><div style='margin-left: 0px;'>";
        if (typeOf(config.center)==="array"){
            config.center.each(function(center){
                var h = (center.host) ? center.host : window.location.hostname;
                var p = (center.port) ? ":"+center.port : "";
                var url = "http://"+h+p+"/x_program_center/jaxrs/echo";
                html+="<br><a style='margin-left: 50px; color: #e0e8d1; line-height: 30px;' href='"+url+"' target='_blank'>"+url+"</a>"
            });
        }else{
            var h = (config.center.host) ? config.center.host : window.location.hostname;
            var p = (config.center.port) ? ":"+config.center.port : "";
            var url = "http://"+h+p+"/x_program_center/jaxrs/echo";
            html+="<br><a style='margin-left: 50px; color: #e0e8d1; line-height: 30px;'href='"+url+"' target='_blank'>"+url+"</a>"
        }
        html+="</div><br><div style='margin-left: 50px; font-size: 20px'>"+MWF.LP.desktop.notice.errorConnectCenter2+"</div></div>";

        ((loadingNode) ? loadingNode.getFirst() : contentNode).set("html", html);
        if (!loadingNode && contentNode){
            contentNode.setStyle("background-color", "#666666");
        }
    };
    if (typeOf(config.center)==="object"){
        MWF.xDesktop.getServiceAddressConfigObject(config.center, callback, error);
    }else if (typeOf(config.center)==="array"){
        var center = null;
        //var center = MWF.xDesktop.chooseCenter(config);
        if (center){
            MWF.xDesktop.getServiceAddressConfigObject(center, callback, function(){
                MWF.xDesktop.getServiceAddressConfigArray(config, callback, error);
            }.bind(this));
        }else{
            MWF.xDesktop.getServiceAddressConfigArray(config, callback, error);
        }
    }
};
MWF.xDesktop.chooseCenter = function(config){
    var host = window.location.host;
    var center = null;
    for (var i=0; i<config.center.length; i++){
        var ct = config.center[i];
        if (!ct.host || (ct.host.toString().toLowerCase()===host.toString().toLowerCase())){
            center = ct;
            break;
        }
    }
    return center;
};
MWF.xDesktop.getServiceAddressConfigArray = function(config, callback, error) {
    var requests = [];
    config.center.each(function(center){
        requests.push(
            MWF.xDesktop.getServiceAddressConfigObject(center, function(serviceAddressList, center){
                requests.each(function(res){
                    if (res) if (res.isRunning()){res.cancel();}
                });
                if (callback) callback(serviceAddressList, center);
            }.bind(this), function(){
                if (requests.length){
                    for (var i=0; i<requests.length; i++){
                        if (requests[i].isRunning()) return "";
                    }
                }
                if (error) error();
            }.bind(this))
        );
    }.bind(this));
};
MWF.xDesktop.getServiceAddressConfigObject = function(center, callback, error){
    var centerConfig = center;
    if (!centerConfig) centerConfig = layout.config.center;
    var host = centerConfig.host || window.location.hostname;
    var port = centerConfig.port;
    var uri = "";

    if (layout.config.app_protocol=="auto"){
        layout.config.app_protocol = window.location.protocol;
    }

    if (!port || port=="80"){
        uri = layout.config.app_protocol+"//"+host+"/x_program_center/jaxrs/distribute/assemble/source/{source}";
    }else{
        uri = layout.config.app_protocol+"//"+host+":"+port+"/x_program_center/jaxrs/distribute/assemble/source/{source}";
    }
    var currenthost = window.location.hostname;
    uri = uri.replace(/{source}/g, currenthost);
    //var uri = "http://"+layout.config.center+"/x_program_center/jaxrs/distribute/assemble";

    try{
        return MWF.restful("get", uri, null, {
            "onSuccess": function(json){
                //this.serviceAddressList = json.data;
                //this.centerServer = center;
                if (callback) callback(json.data, center);
            }.bind(this),
            "onRequestFailure": function(xhr){
                if (error) error(xhr);
            }.bind(this),
            "onError": function(xhr){
                if (error) error(xhr);
            }.bind(this)
        });
    }catch(e){
        if (error) error();
        return null;
    }
};
MWF.xDesktop.$globalEvents = {};
MWF.xDesktop.addEvent = function(name, type, fn){
    if (!MWF.xDesktop.$globalEvents[name]) MWF.xDesktop.$globalEvents[name] = {};
    if (!MWF.xDesktop.$globalEvents[name][type]) MWF.xDesktop.$globalEvents[name][type] = [];
    MWF.xDesktop.$globalEvents[name][type].push(fn);
};

MWF.xDesktop.addEvents = function(name, o){
    if (!MWF.xDesktop.$globalEvents[name]) MWF.xDesktop.$globalEvents[name] = {};
    Object.each(o, function(fn, type){
        MWF.xDesktop.addEvent(name, type, fn);
    }.bind(this));
};

MWF.xDesktop.removeEvent = function(name, type, fn){
    if (!MWF.xDesktop.$globalEvents[name]) return true;
    if (!MWF.xDesktop.$globalEvents[name][type]) return true;
    MWF.xDesktop.$globalEvents[name][type].erase(fn);
};
MWF.xDesktop.removeEvents = function(name, type){
    if (!MWF.xDesktop.$globalEvents[name]) return true;
    if (!MWF.xDesktop.$globalEvents[name][type]) return true;
    MWF.xDesktop.$globalEvents[name][type] = [];
};

MWF.org = {
    parseOrgData: function(data, flat){
        if (data.distinguishedName){
            var flag = data.distinguishedName.substr(data.distinguishedName.length-2, 2);
            switch (flag.toLowerCase()){
                case "@i":
                    return this.parseIdentityData(data, flat);
                    break;
                case "@p":
                    return this.parsePersonData(data, flat);
                    break;
                case "@u":
                    return this.parseUnitData(data, flat);
                    break;
                case "@g":
                    return this.parseGroupData(data, flat);
                    break;
                case "@r":
                    return this.parseRoleData(data, flat);
                    break;
                case "@a":
                    return this.parseAttributeData(data, flat);
                    break;
                default:
                    return data;
            }
        }else{
            return data;
        }
    },
    parseIdentityData: function(data, flat){
        var rData = {
            "id": data.id,
            "name": data.name,
            "unique": data.unique,
            "distinguishedName": data.distinguishedName,
            // "dn": data.distinguishedName,
            "person": data.person,
            "unit": data.unit,
            "unitName": data.unitName,
            // "unitLevel": data.unitLevel,
            "unitLevelName": data.unitLevelName
        };

        if( data.personName )rData.personName = data.personName;
        if( data.personEmployee )rData.personEmployee = data.personEmployee;
        if( data.personUnique )rData.personUnique = data.personUnique;
        if( data.personDn )rData.personDn = data.personDn;

        if( data.ignoreEmpower )rData.ignoreEmpower = true;
        if( data.ignoredEmpower )rData.ignoredEmpower = true;

        if( !flat || !data.personDn || !data.personEmployee || !data.personUnique ){
            var woPerson = data.woPerson;
            if (!data.woPerson){
                MWF.Actions.get("x_organization_assemble_control").getPerson(data.person, function(json){
                    woPerson = json.data
                }, null, false);
            }
            rData.personName = woPerson.name;
            rData.personEmployee = woPerson.employee;
            rData.personUnique = woPerson.unique;
            rData.personDn = woPerson.distinguishedName;

            if (!flat){
                rData.woPerson = {
                    "id": woPerson.id,
                    "genderType": woPerson.genderType,
                    "name": woPerson.name,
                    "employee": woPerson.employee,
                    "unique": woPerson.unique,
                    "distinguishedName": woPerson.distinguishedName,
                    "dn": woPerson.distinguishedName,
                    "mail": woPerson.mail,
                    "weixin": woPerson.weixin,
                    "qq": woPerson.qq,
                    "mobile": woPerson.mobile,
                    "officePhone": woPerson.officePhone
                };
            }
        }
        return rData;
    },
    parsePersonData: function(data){
        return {
            "id": data.id,
            "genderType": data.genderType,
            "name": data.name,
            "employee": data.employee,
            "unique": data.unique,
            "distinguishedName": data.distinguishedName,
            "dn": data.distinguishedName,
            "mail": data.mail,
            "weixin": data.weixin,
            "qq": data.qq,
            "mobile": data.mobile,
            "officePhone": data.officePhone
        }
    },
    parseUnitData: function(data){
        return {
            "id": data.id,
            "name": data.name,
            "unique": data.unique,
            "distinguishedName": data.distinguishedName,
            "dn": data.distinguishedName,
            "typeList":data.typeList,
            "shortName": data.shortName,
            "level": data.level,
            "levelName": data.levelName
        }
    },
    parseGroupData: function(data){
        return {
            "id": data.id,
            "name": data.name,
            "unique": data.unique,
            "distinguishedName": data.distinguishedName,
            "dn": data.distinguishedName
        }
    },
    parseRoleData: function(data){
        return {
            "id": data.id,
            "name": data.name,
            "unique": data.unique,
            "distinguishedName": data.distinguishedName,
            "dn": data.distinguishedName
        }
    },
    parseAttributeData: function(){
        return {
            "id": data.id,
            "description": data.description,
            "name": data.name,
            "unique": data.unique,
            "distinguishedName": data.distinguishedName,
            "dn": data.distinguishedName,
            "person": data.person,
            "attributeList": Array.clone(data.attributeList)
        }
    }
};

MWF.xDesktop.Actions = MWF.xDesktop.Actions || {};
MWF.xDesktop.Actions.RestActions = new Class({
    Implements: [Events],
    initialize: function(actionPath, serviceName, root){
        this.actionPath = actionPath;
        this.serviceName = serviceName;
        this.root = root;
        this.getAddress();
    },

    listApplicationAddress: function(success, failure){
        var url = this.actions.listAddress;
        url = this.actions.slotHost+url;
        var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
        MWF.getJSON(url, callback);
    },
    getAddress: function(success, failure){
//		var name = "x_processplatform_core_designer";
//		var url = this.actions.getAddress.replace(/{id}/g, name);
//		url = this.actions.slotHost+url;
//		var callback = new MWF.process.RestActions.Callback(success, failure, function(data){
//			this.designAddress = data.data.url;
//		}.bind(this));

//		MWF.getJSON(url, callback);

        //this.address = "http://xa02.zoneland.net:8080/"+this.serviceName;

        var addressObj = layout.serviceAddressList[this.serviceName];
        if (addressObj){
            //var mapping = layout.getAppUrlMapping(layout.config.app_protocol+"//"+addressObj.host+(addressObj.port==80 ? "" : ":"+addressObj.port)+addressObj.context);
            this.address = layout.config.app_protocol+"//"+addressObj.host+(addressObj.port==80 ? "" : ":"+addressObj.port)+addressObj.context;
        }else{
            var host = layout.desktop.centerServer.host || window.location.hostname;
            var port = layout.desktop.centerServer.port;

            //var mapping = layout.getCenterUrlMapping(layout.config.app_protocol+"//"+host+(port=="80" ? "" : ":"+port)+"/x_program_center");
            this.address = layout.config.app_protocol+"//"+host+(port=="80" ? "" : ":"+port)+"/x_program_center";
        }

        //this.address = "http://hbxa01.bf.ctc.com/"+this.serviceName;
        //this.designAddress = "http://localhost:9080/x_processplatform_front_designer";
        if (success) success.apply();
        return this.address;
    },
    getActions: function(callback){

        if (!this.actions){
            var url = (this.root) ? "/"+this.root+this.actionPath : MWF.defaultPath+this.actionPath;
            MWF.getJSON(url, function(json){
                this.actions = json;
                if (callback) callback();
            }.bind(this), false, false, false);
        }else{
            if (callback) callback();
        }
    },
    invokeUri: function(option){
        var uri = this.address+option.uri;
        var async = (option.async===false) ? false : true;
        var method = option.method || "GET";
        var callback = new MWF.xDesktop.Actions.RestActions.Callback(option.success, option.failure);

        var data = (option.data) ? JSON.encode(option.data) : "";
        var credentials = true;
        if (option.withCredentials===false){
            credentials = false;
        }
        MWF.restful(method, uri, data, callback, async, credentials);
    },
    invoke: function(option){
        var res = null;
        this.getActions(function(){
            //name, parameter, data, async, success, failure, withCredentials, urlEncode
            var action = this.actions[option.name];
            var method = action.method || "GET";
            var uri = action.uri;
            var progress = action.progress;
            if (option.parameter){
                Object.each(option.parameter, function(value, key){
                    var reg = new RegExp("{"+key+"}", "g");
                    if (option.urlEncode===false){
                        uri = uri.replace(reg, value);
                    }else{
                        uri = uri.replace(reg, encodeURI(value));
                    }
                });
            }
            uri = this.address+uri;

            var async = (option.async===false) ? false : true;

            var callback = new MWF.xDesktop.Actions.RestActions.Callback(option.success, option.failure);
            if (action.enctype && (action.enctype.toLowerCase()=="formdata")){
                res = this.invokeFormData(method, uri, option.data, option.file, callback, async, progress);
            }else{
                var data = (option.data) ? JSON.encode(option.data) : "";
                var credentials = true;
                if (option.withCredentials===false){
                    credentials = false;
                }
                res = MWF.restful(method, uri, data, callback, async, credentials, option.cache);
            }
        }.bind(this));
        return res;
    },
    formDataUpdateProgress: function(){

    },

    invokeFormDataWithProgress: function(xhr, method, uri, data, file, callback, async, progress){
        var messageItem = null;
        var currentDate = new Date();

        xhr.upload.addEventListener("progress", function(e){this.updateProgress(e, xhr, messageItem, currentDate);}.bind(this), false);
        xhr.upload.addEventListener("load", function(e){
            if(file){
                this.transferComplete(e, xhr, messageItem, currentDate, file);
            }
        }.bind(this), false);        xhr.upload.addEventListener("loadstart", function(e){this.transferStart(e, xhr, messageItem);}.bind(this), false);
        xhr.upload.addEventListener("error", function(e){this.transferFailed(e, xhr, messageItem);}.bind(this), false);
        xhr.upload.addEventListener("abort", function(e){this.transferCanceled(e, xhr, messageItem);}.bind(this), false);
        xhr.upload.addEventListener("timeout", function(e){this.transferCanceled(e, xhr, messageItem);}.bind(this), false);

        xhr.addEventListener("readystatechange", function(e){this.xhrStateChange(e, xhr, messageItem, callback);}.bind(this), false);

        xhr.open(method, uri, async!==false);
        xhr.withCredentials = true;

        if (file) messageItem = this.addFormDataMessage(file, false, xhr, progress);
        xhr.send(data);
    },
    setMessageText: function(messageItem, text){
        if (messageItem){
            var progressNode = messageItem.contentNode.getFirst("div").getFirst("div");
            var progressPercentNode = progressNode.getFirst("div");
            var progressInforNode = messageItem.contentNode.getFirst("div").getLast("div");
            progressInforNode.set("text", text);
            messageItem.dateNode.set("text", (new Date()).format("db"));
        }

    },
    setMessageTitle: function(messageItem, text){
        if (messageItem) messageItem.subjectNode.set("text", text);
    },
    clearMessageProgress: function(messageItem){
        if (messageItem) {
            var progressNode = messageItem.contentNode.getFirst("div").getFirst("div");
            progressNode.destroy();
        }
    },


    transferStart: function(e, xhr, messageItem){
        if (messageItem) {
            this.setMessageText(messageItem, MWF.LP.desktop.action.sendStart);
            messageItem.status = "progress";
        }
        this.fireEvent("loadstart");
    },
    transferFailed: function(e, xhr, messageItem){
        if (messageItem) {
            this.setMessageText(messageItem, MWF.LP.desktop.action.sendError);
            this.setMessageTitle(messageItem, MWF.LP.desktop.action.sendError);
            this.clearMessageProgress(messageItem);
            messageItem.status = "failed";
        }
        this.fireEvent("error");
    },
    transferCanceled: function(e, xhr, messageItem){
        if (messageItem) {
            this.setMessageText(messageItem, MWF.LP.desktop.action.sendAbort);
            this.setMessageTitle(messageItem, MWF.LP.desktop.action.sendAbort);
            this.clearMessageProgress(messageItem);
            messageItem.status = "cancel";
        }
        this.fireEvent("abort");
    },
    transferComplete: function(e, xhr, messageItem, currentDate, file){

        var sendDate = new Date();
        var ms = sendDate.getTime()-currentDate.getTime();
        var speed = (file.size)/ms;
        var u = "K/S";
        if (speed>1024){
            u = "M/S";
            speed = speed/1024;
        }
        if (speed>1024){
            u = "G/S";
            speed = speed/1024;
        }
        speed = speed.round(2);

        var timeStr = "";
        if (ms>3600000){
            var h = ms/3600000;
            var m_s = ms % 3600000;
            var m = m_s / 60000;
            var s_s = m_s % 60000;
            var s = s_s/1000;
            timeStr = ""+h.toInt()+MWF.LP.desktop.action.hour+m.toInt()+MWF.LP.desktop.action.minute+s.toInt()+MWF.LP.desktop.action.second;
        }else if (ms>60000){
            var m = ms / 60000;
            var s_s = ms % 60000;
            var s = s_s/1000;
            timeStr = ""+m.toInt()+MWF.LP.desktop.action.minute+s.toInt()+MWF.LP.desktop.action.second;
        }else{
            var s = ms/1000;
            timeStr = ""+s.toInt()+MWF.LP.desktop.action.second;
        }
        if (messageItem) {
            this.setMessageText(messageItem, MWF.LP.desktop.action.uploadComplete + "  " + MWF.LP.desktop.action.speed + ": " + speed + u + "  " + MWF.LP.desktop.action.time + ": " + timeStr, MWF.LP.desktop.action.uploadComplete);
            this.setMessageTitle(messageItem, MWF.LP.desktop.action.uploadComplete);
            this.clearMessageProgress(messageItem);

            messageItem.status = "completed";
        }
        //var msg = {
        //    "subject": MWF.LP.desktop.action.uploadComplete,
        //    "content": MWF.LP.desktop.action.uploadComplete+" : "+file.name
        //};
        //layout.desktop.message.addTooltip(msg);
        this.fireEvent("load");
    },
    updateProgress: function(e, xhr, messageItem, currentDate){
        var percent = 100*(e.loaded/e.total);

        var sendDate = new Date();
        var ms = sendDate.getTime()-currentDate.getTime();
        var speed = (e.loaded)/ms;
        var u = "K/S";
        if (speed>1024){
            u = "M/S";
            speed = speed/1024;
        }
        if (speed>1024){
            u = "G/S";
            speed = speed/1024;
        }
        speed = speed.round(2);

        if (messageItem) {
            if (messageItem.contentNode) {
                var progressNode = messageItem.contentNode.getFirst("div").getFirst("div");
                var progressPercentNode = progressNode.getFirst("div");
                var progressInforNode = messageItem.contentNode.getFirst("div").getLast("div");
                progressPercentNode.setStyle("width", "" + percent + "%");
                progressInforNode.set("text", MWF.LP.desktop.action.sendStart + ": " + speed + u);
            }
        }
        this.fireEvent("progress");
    },
    xhrStateChange: function(e, xhr, messageItem, callback){
        if (xhr.readyState != 4) return;

        var status = xhr.status;
        status = (status == 1223) ? 204 : status;

        if ((status >= 200 && status < 300)){
            var json = JSON.decode(xhr.responseText);
            if (json){
                switch(json.type) {
                    case "success":
                        var dataId = "";
                        var t = typeOf(json.data);
                        if (t=="array"){
                            dataId = json.data[0].id;
                        }
                        if (t=="object"){
                            dataId = json.data.id;
                        }

                        MWF.runCallback(callback, "success", [{
                            "type": "success",
                            "id": dataId,
                            "data": json.data
                        }, xhr.responseText]);
                        break;
                    case "warn":
                        MWF.xDesktop.notice("info", {x: "right", y:"top"}, json.errorMessage.join("\n"));

                        var dataId = "";
                        var t = typeOf(json.data);
                        if (t=="array"){
                            dataId = json.data[0].id;
                        }
                        if (t=="object"){
                            dataId = json.data.id;
                        }
                        MWF.runCallback(callback, "success", [{
                            "type": "success",
                            "id": dataId
                        }, xhr.responseText]);
                        break;
                    case "error":
                        MWF.runCallback(callback, "failure", [xhr]);
                        break;
                }
            }else{
                MWF.runCallback(callback, "failure", [xhr]);
            }
        }else{
            MWF.runCallback(callback, "failure", [xhr]);
        }
    },

    invokeFormDataWithoutProgress: function(xhr, method, uri, data, file, callback, async, progress){
        var messageItem = null;
        var currentDate = new Date();

        xhr.addEventListener("readystatechange", function(e){
            if (xhr.readyState == 4){
                if(file){
                    this.transferComplete(e, xhr, messageItem, currentDate, file);
                }
                this.xhrStateChange(e, xhr, messageItem, callback);
            }
        }.bind(this), false);

        xhr.open(method, uri, true);
        xhr.withCredentials = true;

        messageItem = this.addFormDataMessage(file, true, xhr, progress);
        xhr.send(data);
        this.setMessageText(messageItem, MWF.LP.desktop.action.sendStart);
    },
    invokeFormDataWithForm: function(xhr, method, uri, data, file, callback, async){
        MWF.O2UploadCallback = callback;
        MWF.O2UploadCallbackFun = function(){
            if (MWF.O2UploadCallback) MWF.O2UploadCallback();
        };
        var div = data.items[0].value.el.getParent();
        div.set("styles", {
            "width": "500px",
            "height": "300px",
            "background-color": "#999999",
            "position": "absolute",
            "top": "100px",
            "left": "100px",
            "z-index": "30000",
            "display": "block"
        }).inject(document.body);

        var formNode = new Element("form", {
            "method": method,
            "action": (uri.indexOf("?")!=-1) ? uri+"&callback=MWF.O2UploadCallbackFun" : uri+"?callback=MWF.O2UploadCallbackFun",
            "enctype": "multipart/form-data",
            "target": "o2_upload_iframe"
        }).inject(div);
        var iframe = new Element("iframe", {
            "name": "o2_upload_iframe"
        }).inject(div);
        data.items.each(function(item){
            if (typeOf(item.value)=="string"){
                new Element("input", {
                    "name": item.name,
                    "value": item.value
                }).inject(formNode);
            }else{
                item.value.el.inject(formNode);
                item.value.el.set("name", item.name);
            }
        }.bind(this));
        var submitNode = new Element("input", {
            "type": "submit"
        }).inject(formNode);
        //   formNode.submit();
    },

    invokeFormData: function(method, uri, data, file, callback, async, progress){
        var xhr = new COMMON.Browser.Request();
        if(file){
            data.append('fileName', file.name);
        }

        if (data.type==="o2_formdata"){
            this.invokeFormDataWithForm(xhr, method, uri, data, file, callback, async);
        }else{
            if (xhr.upload){
                this.invokeFormDataWithProgress(xhr, method, uri, data, file, callback, async, progress);
            }else{
                this.invokeFormDataWithoutProgress(xhr, method, uri, data, file, callback, async, progress);
            }
        }
        return xhr;
    },
    addFormDataMessage: function(file, noProgress, xhr, showMsg){
        debugger;
        var contentHTML = "";
        if (noProgress){
            contentHTML = "<div style=\"height: 20px; line-height: 20px\">"+MWF.LP.desktop.action.sendReady+"</div></div>" ;
        }else{
            contentHTML = "<div style=\"overflow: hidden\"><div style=\"height: 3px; border:1px solid #999; margin: 3px 0px\">" +
                "<div style=\"height: 3px; background-color: #acdab9; width: 0px;\"></div></div>" +
                "<div style=\"height: 20px; line-height: 20px\">"+MWF.LP.desktop.action.sendReady+"</div></div>" ;
        }
        var msg = {
            "subject": MWF.LP.desktop.action.uploadTitle,
            //"content": MWF.LP.desktop.action.uploadTitle+" : "+file.name+"<br/>"+contentHTML
            "content": ( file.name ? (file.name+"<br/>") : "" )+contentHTML
        };
        if (layout.desktop.message){
            var messageItem = layout.desktop.message.addMessage(msg);

            //var _self = this;
            messageItem.close = function(callback, e){
                if (this.status=="progress"){
                    flag = false;
                    var text = MWF.LP.desktop.action.cancelUpload.replace(/{name}/g, (file.name||""));
                    MWF.xDesktop.confirm("wram", e, MWF.LP.desktop.action.cancelUploadTitle, text, "400", "140", function(){
                        xhr.abort();
                        //xhr.upload.timeout = 1;
                        this.close();
                        //messageItem.closeItem();
                    }, function(){
                        this.close()
                    });
                    //MWF.LP.desktop.action.sendStart
                }else{
                    messageItem.closeItem(callback, e);
                }
            };
        }

        //messageItem.addEvent("close", function(flag, e){
        //    debugger;
        //    if (this.status=="progress"){
        //        flag = false;
        //        var text = MWF.LP.desktop.action.cancelUpload.replace(/{name}/g, file.name);
        //        MWF.xDesktop.confirm("wram", e, MWF.LP.desktop.action.cancelUploadTitle, text, "300", "120", function(){
        //            xhr.abort();
        //            this.close();
        //            //messageItem.closeItem();
        //        }, function(){
        //            this.close()
        //        });
        //        //MWF.LP.desktop.action.sendStart
        //    }
        //});

        if (showMsg){
            window.setTimeout(function(){
                if (layout.desktop.message) if (!layout.desktop.message.isShow) layout.desktop.message.show();
            }.bind(this), 300);
        }

        //msg = {
        //    "subject": MWF.LP.desktop.action.uploadTitle,
        //    "content": MWF.LP.desktop.action.uploadTitle+" : "+file.name
        //};
        //var tooltipItem = layout.desktop.message.addTooltip(msg);
        return messageItem;
    },
    getAuthentication: function(success, failure){
        this.invoke({
            "name": "authentication",
            "async": true,
            "success": function(json, responseText){
                if (json.data.tokenType!="anonymous"){
                    if (success) success(json);
                }else{
                    if (failure) failure(null, responseText, json.message);
                }
            },
            "failure": failure
        });
    },
    login: function(data, success, failure){
        var name = "login";
        //    if (data.credential.toLowerCase()=="xadmin") name = "loginAdmin";
        this.invoke({
            "name": name,
            "async": true,
            "data": data,
            "success": function(json, responseText){
                //if (json.data.authentication){
                if (json.data.tokenType!="anonymous"){
                    if (success) success(json);
                }else{
                    if (failure) failure(null, responseText, json.message);
                }
            },
            "failure": failure
        });
    },
    logout: function(success, failure){
        this.invoke({
            "name": "logout",
            "async": false,
            "success": success,
            "failure": failure
        });
    }

});

MWF.xDesktop.Actions.RestActions.Callback = new Class({
    initialize: function(success, failure, appendSuccess, appendFailure){
        this.success = success;
        this.failure = failure;
        this.appendSuccess = appendSuccess;
        this.appendFailure = appendFailure;
    },

    onSuccess: function(responseJSON, responseText){
        if (responseJSON){
            switch(responseJSON.type) {
                case "success":
                    if (this.appendSuccess) this.appendSuccess(responseJSON);
                    if (this.success) this.success(responseJSON, responseText);
                    break;
                case "warn":
                    MWF.xDesktop.notice("info", {x: "right", y:"top"}, responseJSON.errorMessage.join("\n"));

                    if (this.appendSuccess) this.appendSuccess(responseJSON);
                    if (this.success) this.success(responseJSON);
                    break;
                case "error":
                    this.doError(null, responseText, responseJSON.message);
                    break;
            }
        }else{
            this.doError(null, responseText, "");
        }
    },
    onRequestFailure: function(xhr){
        this.doError(xhr, "", "");
    },
    onFailure: function(xhr){
        this.doError(xhr, "", "");
    },
    onError: function(text, error){
        this.doError(null, text, error);
    },
    doError: function(xhr, text, error){
        if (this.appendFailure) this.appendFailure(xhr, text, error);
        if (this.failure) this.failure(xhr, text, error);
        if (!this.failure && !this.appendFailure){
            if (xhr.status!=0){
                var errorText = error;
                if (xhr){
                    var json = JSON.decode(xhr.responseText);
                    if (json){
                        errorText = json.message.trim() || "request json error";
                    }else{
                        errorText = "request json error: "+xhr.responseText;
                    }
                }
                errorText = errorText.replace(/\</g, "&lt;");
                errorText = errorText.replace(/\</g, "&gt;");
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, errorText);
            }
            //	throw "request error: "+errorText;
        }
    }
});

MWF.xAction = MWF.xAction || {};
//MWF.require("MWF.xDesktop.Actions.RestActions", null, false);

MWF.xAction.RestActions = MWF.Actions = {
    "actions": {},
    "loadedActions": {},
    "get": function(root){
        if (this.actions[root]) return this.actions[root];

        var actions = null;
        var url = o2.session.path+"/xAction/services/"+root+".json";
        MWF.getJSON(url, function(json){actions = json;}.bind(this), false, false, false);

        if (!MWF.xAction.RestActions.Action[root] && actions.clazz) MWF.require("MWF.xAction.services."+actions.clazz, null, false);
        if (!MWF.xAction.RestActions.Action[root]) MWF.xAction.RestActions.Action[root] = new Class({Extends: MWF.xAction.RestActions.Action});

        this.actions[root] = new MWF.xAction.RestActions.Action[root](root, actions);
        return this.actions[root];
    },
    "load": function(root){
        if (this.loadedActions[root]) return this.loadedActions[root];
        var jaxrs = null;
        //var url = this.getHost(root)+"/"+root+"/describe/describe.json";
        var url = this.getHost(root)+"/"+root+"/describe/api.json";
        //var url = "../o2_core/o2/xAction/temp.json";
        MWF.getJSON(url, function(json){jaxrs = json.jaxrs;}.bind(this), false, false, false);
        if (jaxrs){
            var actionObj = {};
            jaxrs.each(function(o){
                if (o.methods && o.methods.length){
                    var actions = {};
                    o.methods.each(function(m){
                        var o = {"uri": "/"+m.uri};
                        if (m.method) o.method = m.method;
                        if (m.enctype) o.enctype = m.enctype;
                        actions[m.name] = o;
                    }.bind(this));
                    actionObj[o.name] = new MWF.xAction.RestActions.Action(root, actions);
                    //actionObj[o.name] = new MWF.xAction.RestActions.Action(root, o.methods);
                }
            }.bind(this));
            this.loadedActions[root] = actionObj;
            return actionObj;
        }
        return null;
    },
    //actions: [{"action": "", "subAction": "TaskAction", "name": "list", "par": [], "body": "",  "urlEncode"： false, "cache": false}]
    async: function(actions, callback){
        var cbs = (o2.typeOf(callback)==="function") ? callback : callback.success;
        var cbf = (o2.typeOf(callback)==="function") ? null : callback.failure;
        var res = [];
        var len = actions.length;
        var jsons = new Array(len-1);

        var cb = function(){
            if (res.length===len) cbs.apply(this, jsons);
        };
        var _doError = function(xhr, text, error){
            if (xhr.status!=0){
                var errorText = error;
                if (xhr){
                    var json = JSON.decode(xhr.responseText);
                    if (json){
                        errorText = json.message.trim() || "request json error";
                    }else{
                        errorText = "request json error: "+xhr.responseText;
                    }
                }
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, errorText);
            }
        };

        actions.each(function(action, i){
            var actionArgs = action.par || [];
            actionArgs.push(function(json){
                jsons[i] = json;
                res.push(true);
                cb();
            });
            actionArgs.push(function(xhr, text, error){
                res.push(false);
                if (!cbf){
                    _doError(xhr, text, error);
                }else{
                    cbf();
                }
                cb();
            });
            actionArgs.push(true);
            actionArgs.push(action.urlEncode);
            actionArgs.push(action.cache);
            action.action[action.subAction][action.name].apply(action.action[action.subAction], actionArgs);
        });
    },

    //actions: [{"action": "", "name": "list", "par": [], "body": "",  "urlEncode"： false, "cache": false}]
    invokeAsync2: function(actions, callback){
        debugger;
        var cbs = (o2.typeOf(callback)==="function") ? callback : callback.success;
        var cbf = (o2.typeOf(callback)==="function") ? null : callback.failure;
        var res = [];
        var len = actions.length;
        var jsons = new Array(len-1);

        var cb = function(){
            if (res.length===len) cbs.apply(this, jsons);
        };
        var _doError = function(xhr, text, error){
            if (xhr.status!=0){
                var errorText = error;
                if (xhr){
                    var json = JSON.decode(xhr.responseText);
                    if (json){
                        errorText = json.message.trim() || "request json error";
                    }else{
                        errorText = "request json error: "+xhr.responseText;
                    }
                }
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, errorText);
            }
        };

        actions.each(function(action, i){
            var actionArgs = action.par || [];
            actionArgs.push(function(json){
                jsons[i] = json;
                res.push(true);
                cb();
            });
            actionArgs.push(function(xhr, text, error){
                res.push(false);
                if (!cbf){
                    _doError(xhr, text, error);
                }else{
                    cbf();
                }
                cb();
            });
            actionArgs.push(true);
            actionArgs.push(action.urlEncode);
            actionArgs.push(action.cache);
            action.action[action.name].apply(action.action, actionArgs);
        });
    },

    "getHost": function(root){
        var addressObj = layout.serviceAddressList[root];
        var address = "";
        if (addressObj){
            //var mapping = layout.getAppUrlMapping();
            address = layout.config.app_protocol+"//"+addressObj.host+(addressObj.port==80 ? "" : ":"+addressObj.port);
        }else{
            var host = layout.desktop.centerServer.host || window.location.hostname;
            var port = layout.desktop.centerServer.port;
            //var mapping = layout.getCenterUrlMapping();
            address = layout.config.app_protocol+"//"+host+(port=="80" ? "" : ":"+port);
        }
        return address;
    },
    "invokeAsync": function(actions, callback){
        var len = actions.length;
        var parlen = arguments.length-2;
        var res = [];
        var jsons = new Array(len-1);
        var args = arguments;

        var cbs = (o2.typeOf(callback)==="function") ? callback : callback.success;
        var cbf = (o2.typeOf(callback)==="function") ? null : callback.failure;

        var cb = function(){
            if (res.length===len) cbs.apply(this, jsons);
        };
        var _doError = function(xhr, text, error){
            if (xhr.status!=0){
                var errorText = error;
                if (xhr){
                    var json = JSON.decode(xhr.responseText);
                    if (json){
                        errorText = json.message.trim() || "request json error";
                    }else{
                        errorText = "request json error: "+xhr.responseText;
                    }
                }
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, errorText);
            }
        };

        actions.each(function(action, i){
            var par = (i<parlen) ? args[i+2] : args[parlen+1];
            if (par){
                var actionArgs = (o2.typeOf(par)==="array") ? par : [par];
                actionArgs.unshift(function(xhr, text, error){
                    res.push(false);
                    if (!cbf){
                        _doError(xhr, text, error);
                    }else{
                        cbf();
                    }
                    cb();
                });

                actionArgs.unshift(function(json){
                    jsons[i] = json;
                    res.push(true);
                    cb();
                });

                action.action[action.name].apply(action.action, actionArgs);
            }else{
                action.action[action.name](function(){
                    jsons[i] = json;
                    res.push(true);
                    cb();
                }, function(xhr, text, error){
                    res.push(false);
                    if (!cbf){
                        _doError(xhr, text, error);
                    }else{
                        cbf();
                    }
                    cb();
                });
            }
        });
    }
};
MWF.xAction.RestActions.Action = new Class({
    initialize: function(root, actions){
        this.action = new MWF.xDesktop.Actions.RestActions("/xAction/services/"+root+".json", root, "");
        this.action.actions = actions;

        Object.each(this.action.actions, function(service, key){
            if (service.uri) if (!this[key]) this.createMethod(service, key);
        }.bind(this));
    },
    createMethod: function(service, key){
        var jaxrsUri = service.uri;
        var re = new RegExp("\{.+?\}", "g");
        var replaceWords = jaxrsUri.match(re);
        var parameters = [];
        if (replaceWords) parameters = replaceWords.map(function(s){
            return s.substring(1,s.length-1);
        });

        this[key] = this.invokeFunction(service, parameters, key);
    },
    invokeFunction: function(service, parameters, key){
        //uri的参数, data(post, put), file(formData), success, failure, async
        return function(){
            var i = parameters.length-1;
            var n = arguments.length;
            var functionArguments = arguments;
            var parameter = {};
            var success, failure, async, data, file;
            if (typeOf(functionArguments[0])==="function"){
                i=-1;
                success = (n>++i) ? functionArguments[i] : null;
                failure = (n>++i) ? functionArguments[i] : null;
                parameters.each(function(p, x){
                    parameter[p] = (n>++i) ? functionArguments[i] : null;
                });
                if (service.method && (service.method.toLowerCase()==="post" || service.method.toLowerCase()==="put")){
                    if ((!service.enctype) || service.enctype.toLowerCase()!=="formdata"){
                        data = (n>++i) ? functionArguments[i] : null;
                    }else{
                        data = (n>++i) ? functionArguments[i] : null;
                        file = (n>++i) ? functionArguments[i] : null;
                    }
                }
                async = (n>++i) ? functionArguments[i] : null;
                urlEncode = (n>++i) ? functionArguments[i] : true;
                cache = (n>++i) ? functionArguments[i] : (Browser.name != "ie");
            }else{
                parameters.each(function(p, x){
                    parameter[p] = (n>x) ? functionArguments[x] : null;
                });
                if (service.method && (service.method.toLowerCase()==="post" || service.method.toLowerCase()==="put")){
                    if ((!service.enctype) || service.enctype.toLowerCase()!=="formdata"){
                        data = (n>++i) ? functionArguments[i] : null;
                    }else{
                        data = (n>++i) ? functionArguments[i] : null;
                        file = (n>++i) ? functionArguments[i] : null;
                    }
                }
                success = (n>++i) ? functionArguments[i] : null;
                failure = (n>++i) ? functionArguments[i] : null;
                async = (n>++i) ? functionArguments[i] : null;
                urlEncode = (n>++i) ? functionArguments[i] : true;
                cache = (n>++i) ? functionArguments[i] : (Browser.name != "ie");
            }
            return this.invoke(service,{"name": key, "async": async, "data": data, "file": file, "parameter": parameter, "success": success, "failure": failure, "urlEncode": urlEncode, "cache": cache});
            //if (!cache) debugger;
            //return this.action.invoke({"name": key, "async": async, "data": data, "file": file, "parameter": parameter, "success": success, "failure": failure, "urlEncode": urlEncode, "cache": cache});
        }.bind(this);
    },
    invoke: function(service, options){
        return this.action.invoke(options);
    }
});

