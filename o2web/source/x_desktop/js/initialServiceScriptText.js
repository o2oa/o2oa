//Core, Array, String, Function, Number, Class, Object, Object.Extras, Locale, Date, Locale.en-US.Date, Locale.zh-CH.Date, JSON

/*
---

name: Prefix

description: Loads MooTools as a CommonJS Module.

license: MIT-style license.

copyright: Copyright (c) 2010 [Christoph Pojer](http://cpojer.net/).

authors: Christoph Pojer

provides: [Prefix]

...
*/

var GLOBAL_ITEMS = function(){
    var items = [];

    for (var key in this)
        items.push(key);

    return items;
}();


/*
---

name: Core

description: The heart of MooTools.

license: MIT-style license.

copyright: Copyright (c) 2006-2012 [Valerio Proietti](http://mad4milk.net/).

authors: The MooTools production team (http://mootools.net/developers/)

inspiration:
  - Class implementation inspired by [Base.js](http://dean.edwards.name/weblog/2006/03/base/) Copyright (c) 2006 Dean Edwards, [GNU Lesser General Public License](http://opensource.org/licenses/lgpl-license.php)
  - Some functionality inspired by [Prototype.js](http://prototypejs.org) Copyright (c) 2005-2007 Sam Stephenson, [MIT License](http://opensource.org/licenses/mit-license.php)

provides: [Core, MooTools, Type, typeOf, instanceOf, Native]

...
*/

(function(){

    this.MooTools = {
        version: '1.5.0dev',
        build: '%build%'
    };

// typeOf, instanceOf

    var typeOf = this.typeOf = function(item){
        if (item == null) return 'null';
        if (item.$family != null) return item.$family();

        if (item.nodeName){
            if (item.nodeType == 1) return 'element';
            if (item.nodeType == 3) return (/\S/).test(item.nodeValue) ? 'textnode' : 'whitespace';
        } else if (typeof item.length == 'number'){
            if (item.callee) return 'arguments';
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

// Function overloading

    var Function = this.Function;

    var enumerables = true;
    for (var i in {toString: 1}) enumerables = null;
    if (enumerables) enumerables = ['hasOwnProperty', 'valueOf', 'isPrototypeOf', 'propertyIsEnumerable', 'toLocaleString', 'toString', 'constructor'];

    Function.prototype.overloadSetter = function(usePlural){
        var self = this;
        return function(a, b){
            if (a == null) return this;
            if (usePlural || typeof a != 'string'){
                for (var k in a) self.call(this, k, a[k]);
                if (enumerables) for (var i = enumerables.length; i--;){
                    k = enumerables[i];
                    if (a.hasOwnProperty(k)) self.call(this, k, a[k]);
                }
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

    Function.from = function(item){
        return (typeOf(item) == 'function') ? item : function(){
            return item;
        };
    };
    Function.convert = Function.from;

    Array.from = function(item){
        if (item == null) return [];
        return (Type.isEnumerable(item) && typeof item != 'string') ? (typeOf(item) == 'array') ? item : slice.call(item) : [item];
    };
    Array.convert = Array.from;

    Number.from = function(item){
        var number = parseFloat(item);
        return isFinite(number) ? number : null;
    };
    Number.convert = Number.from;

    String.from = function(item){
        return item + '';
    };
    String.convert = String.from;
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
        'charAt', 'charCodeAt', 'concat', 'indexOf', 'lastIndexOf', 'match', 'quote', 'replace', 'search',
        'slice', 'split', 'substr', 'substring', 'trim', 'toLowerCase', 'toUpperCase'
    ])('Array', Array, [
        'pop', 'push', 'reverse', 'shift', 'sort', 'splice', 'unshift', 'concat', 'join', 'slice',
        'indexOf', 'lastIndexOf', 'filter', 'forEach', 'every', 'map', 'some', 'reduce', 'reduceRight'
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

// forEach, each

    var hasOwnProperty = Object.prototype.hasOwnProperty;
    Object.extend('forEach', function(object, fn, bind){
        for (var key in object){
            if (hasOwnProperty.call(object, key)) fn.call(bind, object[key], key, object);
        }
    });

    Object.each = Object.forEach;

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



})();


/*
---

name: Array

description: Contains Array Prototypes like each, contains, and erase.

license: MIT-style license.

requires: Type

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
            return value.toInt(16);
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




/*
---

name: String

description: Contains String Prototypes like camelCase, capitalize, test, and toInt.

license: MIT-style license.

requires: Type

provides: String

...
*/

String.implement({

    test: function(regex, params){
        return ((typeOf(regex) == 'regexp') ? regex : new RegExp('' + regex, params)).test(this);
    },

    contains: function(string, separator){
        return (separator) ? (separator + this + separator).indexOf(separator + string + separator) > -1 : String(this).indexOf(string) > -1;
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
            return this.apply(bind, Array.from(args));
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
        if (args != null) args = Array.from(args);
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
            return Math[name].apply(null, [this].concat(Array.from(arguments)));
        };
    });
    Number.implement(methods);
})(['abs', 'acos', 'asin', 'atan', 'atan2', 'ceil', 'cos', 'exp', 'floor', 'log', 'max', 'min', 'pow', 'sin', 'sqrt', 'tan']);


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
            Array.from(items).each(function(item){
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
            args = Array.from(args);
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
                var index =  events.indexOf(fn);
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

name: Object

description: Object generic methods

license: MIT-style license.

requires: Type

provides: [Object, Hash]

...
*/

(function(){

    var hasOwnProperty = Object.prototype.hasOwnProperty;

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
            for (var key in object){
                if (hasOwnProperty.call(object, key)) results[key] = fn.call(bind, object[key], key, object);
            }
            return results;
        },

        filter: function(object, fn, bind){
            var results = {};
            for (var key in object){
                var value = object[key];
                if (hasOwnProperty.call(object, key) && fn.call(bind, value, key, object)) results[key] = value;
            }
            return results;
        },

        every: function(object, fn, bind){
            for (var key in object){
                if (hasOwnProperty.call(object, key) && !fn.call(bind, object[key], key)) return false;
            }
            return true;
        },

        some: function(object, fn, bind){
            for (var key in object){
                if (hasOwnProperty.call(object, key) && fn.call(bind, object[key], key)) return true;
            }
            return false;
        },

        keys: function(object){
            var keys = [];
            for (var key in object){
                if (hasOwnProperty.call(object, key)) keys.push(key);
            }
            return keys;
        },

        values: function(object){
            var values = [];
            for (var key in object){
                if (hasOwnProperty.call(object, key)) values.push(object[key]);
            }
            return values;
        },

        getLength: function(object){
            return Object.keys(object).length;
        },

        keyOf: function(object, value){
            for (var key in object){
                if (hasOwnProperty.call(object, key) && object[key] === value) return key;
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




/*
---

name: Loader

description: Loads MooTools as a CommonJS Module.

license: MIT-style license.

copyright: Copyright (c) 2010 [Christoph Pojer](http://cpojer.net/).

authors: Christoph Pojer

requires: [Core/Core, Core/Object]

provides: [Loader]

...
*/

if (typeof exports != 'undefined') (function(){

    for (var key in this) if (!GLOBAL_ITEMS.contains(key)){
        exports[key] = this[key];
    }

    exports.apply = function(object){
        Object.append(object, exports);
    };

})();


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
    version: '1.6.1-dev',
    build: '%build%'
};


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



            if (!current) current = locale;

            return locale;
        },

        use: function(locale){
            locale = getSet(locale);

            if (locale){
                current = locale;

                this.fireEvent('change', locale);


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

name: JSON

description: JSON encoder and decoder.

license: MIT-style license.

SeeAlso: <http://www.json.org/>

requires: [Array, String, Number, Function]

provides: JSON

...
*/


if (typeof JSON == 'undefined') this.JSON = {};

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

    JSON.decode = function(string, secure){
        if (!string || typeOf(string) != 'string') return null;

        if (secure || JSON.secure){
            if (JSON.parse) return JSON.parse(string);
            if (!JSON.validate(string)) throw new Error('JSON could not decode the input; security is enabled and the value is not secure.');
        }

        return eval('(' + string + ')');
    };

})();

//以上是mootools代码。


bind = this;
var library = {
    'version': '4.0',
    "defineProperties": Object.defineProperties || function (obj, properties) {
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
                d.enumerable = !!obj.enumerable;
            if (hasProperty(desc, "configurable"))
                d.configurable = !!obj.configurable;
            if (hasProperty(desc, "value"))
                d.value = obj.value;
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

        for (var i = 0; i < keys.length; i++)
            descs.push([keys[i], convertToDescriptor(properties[keys[i]])]);

        for (var i = 0; i < descs.length; i++)
            Object.defineProperty(obj, descs[i][0], descs[i][1]);

        return obj;
    },
    'typeOf': function(item){
        if (item == null) return 'null';
        if (item.$family != null) return item.$family();
        if (item.constructor == Array) return 'array';

        if (item.nodeName){
            if (item.nodeType == 1) return 'element';
            if (item.nodeType == 3) return (/\S/).test(item.nodeValue) ? 'textnode' : 'whitespace';
        } else if (typeof item.length == 'number'){
            if (item.callee) return 'arguments';
            //if ('item' in item) return 'collection';
        }

        return typeof item;
    },

    'JSONDecode': function(string, secure){
        if (!string || library.typeOf(string) != 'string') return null;
        return eval('(' + string + ')');
    },

    'JSONEncode': function(obj){
        if (obj && obj.toJSON) obj = obj.toJSON();
        switch (library.typeOf(obj)){
            case 'string':
                return '"' + obj.replace(/[\x00-\x1f\\"]/g, escape) + '"';
            case 'array':
                var string = [];
                for (var i=0; i<obj.length; i++){
                    var json = library.JSONEncode(obj[i]);
                    if (json) string.push(json);
                }
                return '[' + string + ']';
            case 'object': case 'hash':
            var string = [];
            for (key in obj){
                var json = library.JSONEncode(obj[key]);
                if (json) string.push(library.JSONEncode(key) + ':' + json);
            }
            return '{' + string + '}';
            case 'number': case 'boolean': return '' + obj;
            case 'null': return 'null';
        }
        return null;
    }
};
(function(){
    var o={"indexOf": {
        "value": function(item, from){
            var length = this.length >>> 0;
            for (var i = (from < 0) ? Math.max(0, length + from) : from || 0; i < length; i++){
                if (this[i] === item) return i;
            }
            return -1;
        }
    }};
    library.defineProperties(Array.prototype, o);
})();

/********************
 this.entityManager; //实体管理器
 this.context; //上下文根
 this.applications;
 this.org; //组织访问
 this.service;//webSerivces客户端

 this.response;
    this.response.seeOther(url); //303跳转
    this.response.temporaryRedirect(url); //304跳转
    this.response.setBody(body); //设置返回

 this.requestText//请求正文
 this.request//请求
 this.currentPerson//当前用户

 this.Actions;
    this.Actions.load(root);    //获取接口服务
 ********************/

bind.entityManager = resources.getEntityManagerContainer();
bind.context = resources.getContext();
bind.applications = resources.getApplications();
bind.organization = resources.getOrganization();
bind.service = resources.getWebservicesClient();

//bind.response = customResponse;
//bind.customResponse = customResponse;
bind.requestText = this.requestText || null;
bind.request = this.request || null;
if (this.effectivePerson) bind.currentPerson = bind.effectivePerson = effectivePerson;

if (this.parameters) bind.parameters = JSON.parse(this.parameters); //JPQL语句传入参数
if (this.customResponse){
    var _response = {
        "customResponse": this.customResponse || "",
        seeOther: function(url){
            customResponse.seeOther(url);
        },
        temporaryRedirect: function(url){
            customResponse.temporaryRedirect(url);
        },
        setBody: function(o, contentType){
            var body = o;
            if (typeOf(o)=="object"){
                body = JSON.stringify(o);
            }
            customResponse.setBody(body, contentType || "");
        }
    };
    bind.response = _response;
}

//定义方法
var _define = function(name, fun, overwrite){
    var over = true;
    if (overwrite===false) over = false;
    var o = {};
    o[name] = {"value": fun, "configurable": over};
    library.defineProperties(bind, o);
};

//Action对象
var restfulAcpplication = resources.getApplications();
var _Action = (function(){
    //var actions = [];
    return function(root, json){
        this.actions = json;
        // Object.keys(json).forEach(function(key){
        //     this.actions[key] = json[key];
        // });
        //Object.merge(actions[root], json);
        this.root = root;
        //this.actions = actions[root];

        var invokeFunction = function(service, parameters, key){
            var _self = this;
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
                        data = (n>++i) ? functionArguments[i] : null;
                    }
                }else{
                    parameters.each(function(p, x){
                        parameter[p] = (n>x) ? functionArguments[x] : null;
                    });
                    if (service.method && (service.method.toLowerCase()==="post" || service.method.toLowerCase()==="put")){
                        data = (n>++i) ? functionArguments[i] : null;
                    }
                    success = (n>++i) ? functionArguments[i] : null;
                    failure = (n>++i) ? functionArguments[i] : null;
                }
                return _self.invoke({"name": key, "data": data, "parameter": parameter, "success": success, "failure": failure});
            };
        };
        var createMethod = function(service, key){
            var jaxrsUri = service.uri;
            var re = new RegExp("\{.+?\}", "g");
            var replaceWords = jaxrsUri.match(re);
            var parameters = [];
            if (replaceWords) parameters = replaceWords.map(function(s){
                return s.substring(1,s.length-1);
            });

            this[key] = invokeFunction.call(this, service, parameters, key);
        };
        Object.keys(this.actions).forEach(function(key){
            var service = this.actions[key];
            if (service.uri) if (!this[key]) createMethod.call(this, service, key);
        }, this);

        this.invoke = function(option){
            // {
            //     "name": "",
            //     "data": "",
            //     "parameter": "",,
            //     "success": function(){}
            //     "failure": function(){}
            // }
            if (this.actions[option.name]){
                var uri = this.actions[option.name].uri;
                var method = this.actions[option.name].method || "get";
                if (option.parameter){
                    Object.keys(option.parameter).forEach(function(key){
                        var v = option.parameter[key];
                        uri = uri.replace("{"+key+"}", v);
                    });
                }
                var res = null;
                try{
                    print(uri);
                    switch (method.toLowerCase()){
                        case "get":
                            res = bind.applications.getQuery(this.root, uri);
                            break;
                        case "post":
                            res = bind.applications.postQuery(this.root, uri, JSON.stringify(option.data));
                            break;
                        case "put":
                            res = bind.applications.putQuery(this.root, uri, JSON.stringify(option.data));
                            break;
                        case "delete":
                            res = bind.applications.deleteQuery(this.root, uri);
                            break;
                        default:
                            res = bind.applications.getQuery(this.root, uri);
                    }
                    if (res && res.getType().toString()==="success"){
                        var json = JSON.parse(res.toString());
                        if (option.success) option.success(json);
                    }else{
                        if (option.failure) option.failure(((res) ? JSON.parse(res.toString()) : null));
                    }
                    return res;
                }catch(e){
                    if (option.failure) option.failure(e);
                }
            }
        };
    }
})();
var _Actions = {
    "loadedActions": {},
    "load": function(root){
        if (this.loadedActions[root]) return this.loadedActions[root];
        var jaxrsString = bind.applications.describeApi(root);
        var json = JSON.parse(jaxrsString.toString());
        if (json && json.jaxrs){
            var actionObj = {};
            json.jaxrs.each(function(o){
                if (o.methods && o.methods.length){
                    var actions = {};
                    o.methods.each(function(m){

                        var o = {"uri": "/"+m.uri};
                        if (m.method) o.method = m.method;
                        if (m.enctype) o.enctype = m.enctype;
                        actions[m.name] = o;
                    }.bind(this));
                    actionObj[o.name] = new bind.Action(root, actions);
                }
            }.bind(this));
            this.loadedActions[root] = actionObj;
            return actionObj;
        }
        return null;
    }
};
bind.Actions = _Actions;

//定义所需的服务
var _processActions = new _Action("x_processplatform_assemble_surface", {
    "getDictionary": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{applicationFlag}"},
    "getDictRoot": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/data"},
    "getDictData": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/{path}/data"},
    "setDictData": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/{path}/data", "method": "PUT"},
    "addDictData": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/{path}/data", "method": "POST"},
    "deleteDictData": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/{path}/data", "method": "DELETE"},
    "getScript": {"uri": "/jaxrs/script/{flag}/application/{applicationFlag}", "method": "POST"},
});
var _cmsActions = new _Action("x_cms_assemble_control", {
    "getDictionary": {"uri": "/jaxrs/design/appdict/{appDictId}"},
    "getDictRoot": {"uri": "/jaxrs/surface/appdict/{appDictId}/appInfo/{appId}/data"},
    "getDictData": {"uri": "/jaxrs/surface/appdict/{appDictId}/appInfo/{appId}/{path}/data"},
    "setDictData": {"uri": "/jaxrs/surface/appdict/{appDictId}/appInfo/{appId}/{path}/data", "method": "PUT"},
    "addDictData": {"uri": "/jaxrs/surface/appdict/{appDictId}/appInfo/{appId}/{path}/data", "method": "POST"},
    "deleteDictData": {"uri": "/jaxrs/surface/appdict/{appDictId}/appInfo/{appId}/{path}/data", "method": "DELETE"},
    "getDictRootAnonymous" : {"uri": "/jaxrs/anonymous/surface/appdict/{appDictId}/appInfo/{appId}/data"},
    "getDictDataAnonymous" : {"uri": "/jaxrs/anonymous/surface/appdict/{appDictId}/appInfo/{appId}/{path}/data"},
    "getScript": {"uri": "/jaxrs/script/{flag}/appInfo/{appInfoFlag}", "method": "POST"},
});
var _portalActions = new _Action("x_portal_assemble_surface", {
    "getScript":  {"uri": "/jaxrs/script/portal/{portal}/name/{ }","method": "POST"}
});


//include 引用脚本
//optionsOrName : {
//  type : "", 默认为process, 可以为 portal  process  cms
//  application : "", 门户/流程/CMS的名称/别名/id, 默认为当前应用
//  name : "" // 脚本名称/别名/id
//}
//或者name: "" // 脚本名称/别名/id
var _exec = function(code, _self){
    var returnValue;
    //try{
    if (!_self) _self = this;
    try {
        var f = eval("(function(){return function(){\n"+code+"\n}})();");
        returnValue = f.apply(_self);
    }catch(e){
        console.log("exec", new Error("exec script error"));
        console.log(e);
    }
    return returnValue;
}

var includedScripts = this.includedScripts || {};
this.includedScripts = includedScripts;

var _include = function( optionsOrName , callback ){
    var options = optionsOrName;
    if( typeOf( options ) == "string" ){
        options = { name : options };
    }
    var name = options.name;
    var type = ( options.type && options.application ) ?  options.type : "process";
    var application = options.application

    if (!name || !type || !application){
        console.log("include", new Error("can not find script. missing script name or application"));
        return false;
    }

    if (!includedScripts[application]) includedScripts[application] = [];

    if (includedScripts[application].indexOf( name )> -1){
        if (callback) callback.apply(this);
        return;
    }

    var scriptAction;
    var scriptData;
    switch ( type ){
        case "portal" :
            _portalActions.getScript( application, name, {"importedList":includedScripts[application]}, function(json){
                if (json.data){
                    includedScripts[application] = includedScripts[application].concat(json.data.importedList);
                    scriptData = json.data;
                }
            }.bind(this));
            break;
        case "process" :
            _processActions.getScript( name, application, {"importedList":includedScripts[application]}, function(json){
                if (json.data){
                    includedScripts[application] = includedScripts[application].concat(json.data.importedList);
                    scriptData = json.data;
                }
            }.bind(this));
            break;
        case "cms" :
            _cmsActions.getScript(name, application, {"importedList":includedScripts[application]}, function(json){
                if (json.data){
                    includedScripts[application] = includedScripts[application].concat(json.data.importedList);
                    scriptData = json.data;
                }
            }.bind(this));
            break;
    }
    includedScripts[application].push(name);
    if (scriptData && scriptData.text){
        bind.exec(scriptData.text, this);
        if (callback) callback.apply(this);
    }
};

var _createDict = function(application){
    //optionsOrName : {
    //  type : "", //默认为process, 可以为  process  cms
    //  application : "", //流程/CMS的名称/别名/id, 默认为当前应用
    //  name : "", // 数据字典名称/别名/id
    //  enableAnonymous : false //允许在未登录的情况下读取CMS的数据字典
    //}
    //或者name: "" // 数据字典名称/别名/id
    return function(optionsOrName){
        var options = optionsOrName;
        if( typeOf( options ) == "string" ){
            options = { name : options };
        }
        var name = this.name = options.name;
        var type = ( options.type && options.application ) ?  options.type : "process";
        var applicationId = options.application || application;
        var enableAnonymous = options.enableAnonymous || false;

        //MWF.require("MWF.xScript.Actions.DictActions", null, false);
        if( type == "cms" ){
            var action = bind.cmsActions;
        }else{
            var action = bind.processActions;
        }

        var encodePath = function( path ){
            var arr = path.split(/\./g);
            var ar = arr.map(function(v){
                return encodeURIComponent(v);
            });
            return ar.join("/");
        };

        this.get = function(path, success, failure){
            var value = null;
            if (path){
                var p = encodePath( path );
                action[(enableAnonymous && type == "cms") ? "getDictDataAnonymous" : "getDictData"](encodeURIComponent(this.name), applicationId, p, function(json){
                    value = json.data;
                    if (success) success(json.data);
                }, function(xhr, text, error){
                    if (failure) failure(xhr, text, error);
                });
            }else{
                action[(enableAnonymous && type == "cms") ? "getDictRootAnonymous" : "getDictRoot"](encodeURIComponent(this.name), applicationId, function(json){
                    value = json.data;
                    if (success) success(json.data);
                }, function(xhr, text, error){
                    if (failure) failure(xhr, text, error);
                }, false);
            }

            return value;
        };

        this.set = function(path, value, success, failure){
            var p = encodePath( path );
            //var p = path.replace(/\./g, "/");
            action.setDictData(encodeURIComponent(this.name), applicationId, p, value, function(json){
                if (success) success(json.data);
            }, function(xhr, text, error){
                if (failure) failure(xhr, text, error);
            }, false, false);
        };
        this.add = function(path, value, success, failure){
            var p = encodePath( path );
            //var p = path.replace(/\./g, "/");
            action.addDictData(encodeURIComponent(this.name), applicationId, p, value, function(json){
                if (success) success(json.data);
            }, function(xhr, text, error){
                if (failure) failure(xhr, text, error);
            }, false, false);
        };
        this["delete"] = function(path, success, failure){
            var p = encodePath( path );
            //var p = path.replace(/\./g, "/");
            action.deleteDictData(encodeURIComponent(this.name), applicationId, p, function(json){
                if (success) success(json.data);
            }, function(xhr, text, error){
                if (failure) failure(xhr, text, error);
            }, false, false);
        };
        this.destory = this["delete"];
    }
};

var getNameFlag = function(name){
    var t = library.typeOf(name);
    if (t==="array"){
        var v = [];
        name.forEach(function(id){
            v.push((library.typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id);
        });
        return v;
    }else{
        return [(t==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name];
    }
};
var _org = {
    "oGroup": this.organization.group(),
    "oIdentity": this.organization.identity(),
    "oPerson": this.organization.person(),
    "oPersonAttribute": this.organization.personAttribute(),
    "oRole": this.organization.role(),
    "oUnit": this.organization.unit(),
    "oUnitAttribute": this.organization.unitAttribute(),
    "oUnitDuty": this.organization.unitDuty(),

    "group": function() { return this.oGroup},
    "identity": function() { return this.oIdentity},
    "person": function() { return this.oPerson},
    "personAttribute": function() { return this.oPersonAttribute},
    "role": function() { return this.oRole},
    "unit": function() { return this.oUnit},
    "unitAttribute": function() { return this.oUnitAttribute},
    "unitDuty": function() { return this.oUnitDuty},

    "getObject": function(o, v){
        var arr = [];
        if (!v || !v.length){
            return null;
        }else{
            for (var i=0; i<v.length; i++){
                var g = o.getObject(v[i]);
                if (g) arr.push(JSON.parse(g.toString()));
            }
        }
        return arr;
    },
    //群组***************
    //获取群组--返回群组的对象数组
    getGroup: function(name){
        var v = this.oGroup.listObject(getNameFlag(name));
        var v_json = (!v || !v.length) ? null: JSON.parse(v.toString());
        return (v_json && v_json.length===1) ? v_json[0] : v_json;
    },

    //查询下级群组--返回群组的对象数组
    //nested  布尔  true嵌套下级；false直接下级；默认false；
    listSubGroup: function(name, nested){
        var v = null;
        if (nested){
            v = this.oGroup.listWithGroupSubNested(getNameFlag(name));
        }else{
            v = this.oGroup.listWithGroupSubDirect(getNameFlag(name));
        }
        return this.getObject(this.oGroup, v);
    },
    //查询上级群组--返回群组的对象数组
    //nested  布尔  true嵌套上级；false直接上级；默认false；
    listSupGroup:function(name, nested){
        var v = null;
        if (nested){
            v = this.oGroup.listWithGroupSupNested(getNameFlag(name));
        }else{
            v = this.oGroup.listWithGroupSupDirect(getNameFlag(name));
        }
        return this.getObject(this.oGroup, v);
    },
    //人员所在群组（嵌套）--返回群组的对象数组
    listGroupWithPerson:function(name){
        var v = this.oGroup.listWithPerson(getNameFlag(name));
        return this.getObject(this.oGroup, v);
    },
    //群组是否拥有角色--返回true, false
    groupHasRole: function(name, role){
        nameFlag = (library.typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name;
        return this.oGroup.hasRole(nameFlag, getNameFlag(role));
    },

    //角色***************
    //获取角色--返回角色的对象数组
    getRole: function(name){
        var v = this.oRole.listObject(getNameFlag(name));
        var v_json = (!v || !v.length) ? null: JSON.parse(v.toString());
        return (v_json && v_json.length===1) ? v_json[0] : v_json;
    },
    //人员所有角色（嵌套）--返回角色的对象数组
    listRoleWithPerson:function(name){
        var v = this.oRole.listWithPerson(getNameFlag(name));
        return this.getObject(this.oRole, v);
    },

    //人员***************
    //人员是否拥有角色--返回true, false
    personHasRole: function(name, role){
        nameFlag = (library.typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name;
        return this.oPerson.hasRole(nameFlag, getNameFlag(role));
    },
    //获取人员--返回人员的对象数组
    getPerson: function(name){
        var v = this.oPerson.listObject(getNameFlag(name));
        var v_json = (!v || !v.length) ? null: JSON.parse(v.toString());
        // if (!v || !v.length) v = null;
        // return (v && v.length===1) ? v[0] : v;
        return (v_json && v_json.length===1) ? v_json[0] : v_json;
    },
    //查询下级人员--返回人员的对象数组
    //nested  布尔  true嵌套下级；false直接下级；默认false；
    listSubPerson: function(name, nested){
        var v = null;
        if (nested){
            v = this.oPerson.listWithPersonSubNested(getNameFlag(name));
        }else{
            v = this.oPerson.listWithPersonSubDirect(getNameFlag(name));
        }
        return this.getObject(this.oPerson, v);
    },
    //查询上级人员--返回人员的对象数组
    //nested  布尔  true嵌套上级；false直接上级；默认false；
    listSupPerson: function(name, nested){
        var v = null;
        if (nested){
            v = this.oPerson.listWithPersonSupNested(getNameFlag(name));
        }else{
            v = this.oPerson.listWithPersonSupDirect(getNameFlag(name));
        }
        return this.getObject(this.oPerson, v);
    },
    //获取群组的所有人员--返回人员的对象数组
    listPersonWithGroup: function(name){
        var v = this.oPerson.listWithGroup(getNameFlag(name));
        return this.getObject(this.oPerson, v);
        // if (!v || !v.length) v = null;
        // return v;
        // var v_json = (!v || !v.length) ? null: JSON.parse(v.toString());
        // return v_json;
    },
    //获取角色的所有人员--返回人员的对象数组
    listPersonWithRole: function(name){
        var v = this.oPerson.listWithRole(getNameFlag(name));
        return this.getObject(this.oPerson, v);
    },
    //获取身份的所有人员--返回人员的对象数组
    listPersonWithIdentity: function(name){
        var v = this.oPerson.listWithIdentity(getNameFlag(name));
        return this.getObject(this.oPerson, v);
    },
    //获取身份的所有人员--返回人员的对象数组
    getPersonWithIdentity: function(name){
        var v = this.oPerson.listWithIdentity(getNameFlag(name));
        var arr = this.getObject(this.oPerson, v);
        return (arr && arr.length) ? arr[0] : null;
    },
    //查询组织成员的人员--返回人员的对象数组
    //nested  布尔  true嵌套的所有成员；false直接成员；默认false；
    listPersonWithUnit: function(name, nested){
        var v = null;
        if (nested){
            v = this.oPerson.listWithUnitSubNested(getNameFlag(name));
        }else{
            v = this.oPerson.listWithUnitSubDirect(getNameFlag(name));
        }
        return this.getObject(this.oPerson, v);
    },

    //人员属性************
    //添加人员属性值(在属性中添加values值，如果没有此属性，则创建一个)
    appendPersonAttribute: function(person, attr, values){
        var personFlag = (library.typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
        return this.oPersonAttribute.appendWithPersonWithName(personFlag, attr, values);
    },
    //设置人员属性值(将属性值修改为values，如果没有此属性，则创建一个)
    setPersonAttribute: function(person, attr, values){
        var personFlag = (library.typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
        return this.oPersonAttribute.setWithPersonWithName(personFlag, attr, values);
    },
    //获取人员属性值
    getPersonAttribute: function(person, attr){
        var personFlag = (library.typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
        var v = this.oPersonAttribute.listAttributeWithPersonWithName(personFlag, attr);
        var v_json = [];
        if (v && v.length){
            for (var i=0; i<v.length; i++){
                v_json.push(v[i].toString());
            }
        }
        return v_json;
    },
    //列出人员所有属性的名称
    listPersonAttributeName: function(name){
        var p = getNameFlag(name);
        var nameList = [];
        for (var i=0; i<p.length; i++){
            var v = this.oPersonAttribute.listNameWithPerson(p[i]);
            if (v && v.length){
                for (var j=0; j<v.length; j++){
                    if (nameList.indexOf(v[j])==-1) nameList.push(v[j].toString());
                }
            }
        }
        return nameList;
    },
    //列出人员的所有属性
    //listPersonAllAttribute: function(name){
    // getOrgActions();
    // var data = {"personList":getNameFlag(name)};
    // var v = null;
    // orgActions.listPersonAllAttribute(data, function(json){v = json.data;}, null, false);
    // return v;
    //},

    //身份**********
    //获取身份
    getIdentity: function(name){
        var v = this.oIdentity.listObject(getNameFlag(name));
        var v_json = (!v || !v.length) ? null: JSON.parse(v.toString());
        return (v_json && v_json.length===1) ? v_json[0] : v_json;
        // if (!v || !v.length) v = null;
        // return (v && v.length===1) ? v[0] : v;
    },
    //列出人员的身份
    listIdentityWithPerson: function(name){
        var v = this.oIdentity.listWithPerson(getNameFlag(name));
        return this.getObject(this.oIdentity, v);
    },
    //查询组织成员身份--返回身份的对象数组
    //nested  布尔  true嵌套的所有成员；false直接成员；默认false；
    listIdentityWithUnit: function(name, nested){
        var v = null;
        if (nested){
            v = this.oIdentity.listWithUnitSubNested(getNameFlag(name));
        }else{
            v = this.oIdentity.listWithUnitSubDirect(getNameFlag(name));
        }
        return this.getObject(this.oIdentity, v);
    },

    //组织**********
    //获取组织
    getUnit: function(name){
        var v = this.oUnit.listObject(getNameFlag(name));
        var v_json = (!v || !v.length) ? null: JSON.parse(v.toString());
        return (v_json && v_json.length===1) ? v_json[0] : v_json;
        // if (!v || !v.length) v = null;
        // return (v && v.length===1) ? v[0] : v;
    },
    //查询组织的下级--返回组织的对象数组
    //nested  布尔  true嵌套下级；false直接下级；默认false；
    listSubUnit: function(name, nested){
        var v = null;
        if (nested){
            v = this.oUnit.listWithUnitSubNested(getNameFlag(name));
        }else{
            v = this.oUnit.listWithUnitSubDirect(getNameFlag(name));
        }
        return this.getObject(this.oUnit, v);
    },
    //查询组织的上级--返回组织的对象数组
    //nested  布尔  true嵌套上级；false直接上级；默认false；
    listSupUnit: function(name, nested){
        var v = null;
        if (nested){
            v = this.oUnit.listWithUnitSupNested(getNameFlag(name));
        }else{
            v = this.oUnit.listWithUnitSupDirect(getNameFlag(name));
        }
        return this.getObject(this.oUnit, v);
    },
    //根据个人身份获取组织
    //flag 数字    表示获取第几层的组织
    //     字符串  表示获取指定类型的组织
    //     空     表示获取直接所在的组织
    getUnitByIdentity: function(name, flag){
        //getOrgActions();
        var getUnitMethod = "current";
        var v;
        if (flag){
            if (library.typeOf(flag)==="string") getUnitMethod = "type";
            if (library.typeOf(flag)==="number") getUnitMethod = "level";
        }
        var n = getNameFlag(name)[0];
        switch (getUnitMethod){
            case "current":
                v = this.oUnit.getWithIdentity(n);
                break;
            case "type":
                v = this.oUnit.getWithIdentityWithType(n, flag);
                break;
            case "level":
                v = this.oUnit.getWithIdentityWithLevel(n, flag);
                break;
        }
        var o = this.getObject(this.oUnit, [v]);
        return (o && o.length===1) ? o[0] : o;
    },
    //列出身份所在组织的所有上级组织
    listAllSupUnitWithIdentity: function(name){
        var v = this.oUnit.listWithIdentitySupNested(getNameFlag(name));
        return this.getObject(this.oUnit, v);
    },
    //获取人员所在的所有组织（直接所在组织）
    listUnitWithPerson: function(name){
        var v = this.oUnit.listWithPerson(getNameFlag(name));
        return this.getObject(this.oUnit, v);
    },
    //列出人员所在组织的所有上级组织
    listAllSupUnitWithPerson: function(name){
        var v = this.oUnit.listWithPersonSupNested(getNameFlag(name));
        return this.getObject(this.oUnit, v);
    },
    //根据组织属性，获取所有符合的组织
    listUnitWithAttribute: function(name, attribute){
        var v = this.oUnit.listWithUnitAttribute(name, attribute);
        return this.getObject(this.oUnit, v);
    },
    //根据组织职务，获取所有符合的组织
    listUnitWithDuty: function(name, id){
        var idflag = (library.typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id;
        var v = this.oUnit.listWithUnitDuty(name, idflag);
        return this.getObject(this.oUnit, v);
    },

    //组织职务***********
    //获取指定的组织职务的身份
    getDuty: function(duty, id){
        var unit = (library.typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id;
        var v = this.oUnitDuty.listIdentityWithUnitWithName(unit, duty);
        return this.getObject(this.oIdentity, v);
    },

    //获取身份的所有职务名称
    listDutyNameWithIdentity: function(name){
        var ids = getNameFlag(name);
        var nameList = [];
        for (var i=0; i<ids.length; i++){
            var v = this.oUnitDuty.listNameWithIdentity(ids[i]);
            if (v && v.length){
                for (var j=0; j<v.length; j++){
                    if (nameList.indexOf(v[j])==-1) nameList.push(v[j].toString());
                }
            }
        }
        return nameList;
    },
    //获取组织的所有职务名称
    listDutyNameWithUnit: function(name){
        var ids = getNameFlag(name);
        var nameList = [];
        for (var i=0; i<ids.length; i++){
            var v = this.oUnitDuty.listNameWithUnit(ids[i]);
            if (v && v.length){
                for (var j=0; j<v.length; j++){
                    if (nameList.indexOf(v[j])==-1) nameList.push(v[j].toString());
                }
            }
        }
        return nameList;
    },
    //获取组织的所有职务
    listUnitAllDuty: function(name){
        var u = getNameFlag(name)[0];
        var ds = this.oUnitDuty.listNameWithUnit(u);
        var o = []
        for (var i=0; i<ds.length; i++){
            v = this.oUnitDuty.listIdentityWithUnitWithName(u, ds[i]);
            o.push({"name": ds[i], "identityList": this.getObject(this.oIdentity, v)});
        }
        return o;
    },

    //组织属性**************
    //添加组织属性值(在属性中添加values值，如果没有此属性，则创建一个)
    appendUnitAttribute: function(unit, attr, values){
        var unitFlag = (library.typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
        return this.oUnitAttribute.appendWithUnitWithName(unitFlag, attr, values);
    },
    //设置组织属性值(将属性值修改为values，如果没有此属性，则创建一个)
    setUnitAttribute: function(unit, attr, values){
        var unitFlag = (library.typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
        return this.oUnitAttribute.setWithUnitWithName(unitFlag, attr, values);
    },
    //获取组织属性值
    getUnitAttribute: function(unit, attr){
        var unitFlag = (library.typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
        var v = this.oUnitAttribute.listAttributeWithUnitWithName(unitFlag, attr);
        var v_json = [];
        if (v && v.length){
            for (var i=0; i<v.length; i++){
                v_json.push(v[i].toString());
            }
        }
        return v_json;
    },
    //列出组织所有属性的名称
    listUnitAttributeName: function(name){
        var p = getNameFlag(name);
        var nameList = [];
        for (var i=0; i<p.length; i++){
            var v = this.oUnitAttribute.listNameWithUnit(p[i]);
            if (v && v.length){
                for (var j=0; j<v.length; j++){
                    if (nameList.indexOf(v[j])==-1) nameList.push(v[j]);
                }
            }
        }
        return nameList;
    },
    //列出组织的所有属性
    listUnitAllAttribute: function(name){
        var u = getNameFlag(name)[0];
        var ds = this.oUnitAttribute.listNameWithUnit(u);
        var o = []
        for (var i=0; i<ds.length; i++){
            v = this.getUnitAttribute(u, ds[i]);
            o.push({"name": ds[i], "valueList":v});
        }
        return o;
    }
};

bind.org = _org;
bind.library = library;
bind.define = _define;
bind.Action = _Action;
bind.Actions = _Actions;
bind.processActions = _processActions;
bind.cmsActions = _cmsActions;
bind.portalActions = _portalActions;

bind.exec = _exec;
bind.include = _include;
bind.Dict = _createDict();
