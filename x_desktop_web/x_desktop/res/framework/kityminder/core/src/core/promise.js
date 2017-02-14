define(function(require, exports, module) {
    /*!
    **  Thenable -- Embeddable Minimum Strictly-Compliant Promises/A+ 1.1.1 Thenable
    **  Copyright (c) 2013-2014 Ralf S. Engelschall <http://engelschall.com>
    **  Licensed under The MIT License <http://opensource.org/licenses/MIT>
    **  Source-Code distributed on <http://github.com/rse/thenable>
    */

    /*  promise states [Promises/A+ 2.1]  */
    var STATE_PENDING   = 0;                                         /*  [Promises/A+ 2.1.1]  */
    var STATE_FULFILLED = 1;                                         /*  [Promises/A+ 2.1.2]  */
    var STATE_REJECTED  = 2;                                         /*  [Promises/A+ 2.1.3]  */

    /*  promise object constructor  */
    var Promise = function(executor) {
        /*  optionally support non-constructor/plain-function call  */
        if (!(this instanceof Promise))
            return new Promise(executor);

        /*  initialize object  */
        this.id           = 'Thenable/1.0.7';
        this.state        = STATE_PENDING; /*  initial state  */
        this.fulfillValue = undefined;     /*  initial value  */     /*  [Promises/A+ 1.3, 2.1.2.2]  */
        this.rejectReason = undefined;     /*  initial reason */     /*  [Promises/A+ 1.5, 2.1.3.2]  */
        this.onFulfilled  = [];            /*  initial handlers  */
        this.onRejected   = [];            /*  initial handlers  */

        /*  support optional executor function  */
        if (typeof executor === 'function')
            executor.call(this, this.fulfill.bind(this), this.reject.bind(this));
    };

    /*  Promise API methods  */
    Promise.prototype = {
        /*  promise resolving methods  */
        fulfill: function(value) { return deliver(this, STATE_FULFILLED, 'fulfillValue', value); },
        reject:  function(value) { return deliver(this, STATE_REJECTED,  'rejectReason', value); },

        /*  'The then Method' [Promises/A+ 1.1, 1.2, 2.2]  */
        then: function(onFulfilled, onRejected) {
            var curr = this;
            var next = new Promise();                                    /*  [Promises/A+ 2.2.7]  */
            curr.onFulfilled.push(
                resolver(onFulfilled, next, 'fulfill'));             /*  [Promises/A+ 2.2.2/2.2.6]  */
            curr.onRejected.push(
                resolver(onRejected,  next, 'reject'));             /*  [Promises/A+ 2.2.3/2.2.6]  */
            execute(curr);
            return next;                                       /*  [Promises/A+ 2.2.7, 3.3]  */
        }
    };

    Promise.all = function (arr) {
        return new Promise(function(resolve, reject) {
            var len = arr.length,
                i = 0,
                res = 0,
                results = [];

            if (len === 0) {
                resolve(results);
            }

            while (i < len) {
                arr[i].then(
                    function (result) {
                        results.push(result);
                        if (++res === len) {
                            resolve(results);
                        }
                    },
                    function (val) {
                        reject(val);
                    }
                );
                i++;
            }
        });
    };

    /*  deliver an action  */
    var deliver = function(curr, state, name, value) {
        if (curr.state === STATE_PENDING) {
            curr.state = state;                                      /*  [Promises/A+ 2.1.2.1, 2.1.3.1]  */
            curr[name] = value;                                      /*  [Promises/A+ 2.1.2.2, 2.1.3.2]  */
            execute(curr);
        }
        return curr;
    };

    /*  execute all handlers  */
    var execute = function(curr) {
        if (curr.state === STATE_FULFILLED)
            execute_handlers(curr, 'onFulfilled', curr.fulfillValue);
        else if (curr.state === STATE_REJECTED)
            execute_handlers(curr, 'onRejected',  curr.rejectReason);
    };

    /*  execute particular set of handlers  */
    var execute_handlers = function(curr, name, value) {
        /* global process: true */
        /* global setImmediate: true */
        /* global setTimeout: true */

        /*  short-circuit processing  */
        if (curr[name].length === 0)
            return;

        /*  iterate over all handlers, exactly once  */
        var handlers = curr[name];
        curr[name] = [];                                             /*  [Promises/A+ 2.2.2.3, 2.2.3.3]  */
        var func = function() {
            for (var i = 0; i < handlers.length; i++)
                handlers[i](value);                                  /*  [Promises/A+ 2.2.5]  */
        };

        /*  execute procedure asynchronously  */                     /*  [Promises/A+ 2.2.4, 3.1]  */
        if (typeof process === 'object' && typeof process.nextTick === 'function')
            process.nextTick(func);
        else if (typeof setImmediate === 'function')
            setImmediate(func);
        else
            setTimeout(func, 0);
    };

    /*  generate a resolver function */
    var resolver = function(cb, next, method) {
        return function(value) {
            if (typeof cb !== 'function')                            /*  [Promises/A+ 2.2.1, 2.2.7.3, 2.2.7.4]  */
                next[method].call(next, value);                      /*  [Promises/A+ 2.2.7.3, 2.2.7.4]  */
            else {
                var result;
                try {
                    if (value instanceof Promise) {
                        result = value.then(cb);
                    }
                    else result = cb(value);
                }                          /*  [Promises/A+ 2.2.2.1, 2.2.3.1, 2.2.5, 3.2]  */
                catch (e) {
                    next.reject(e);                                  /*  [Promises/A+ 2.2.7.2]  */
                    return;
                }
                resolve(next, result);                               /*  [Promises/A+ 2.2.7.1]  */
            }
        };
    };

    /*  'Promise Resolution Procedure'  */                           /*  [Promises/A+ 2.3]  */
    var resolve = function(promise, x) {
        /*  sanity check arguments  */                               /*  [Promises/A+ 2.3.1]  */
        if (promise === x) {
            promise.reject(new TypeError('cannot resolve promise with itself'));
            return;
        }

        /*  surgically check for a 'then' method
            (mainly to just call the 'getter' of 'then' only once)  */
        var then;
        if ((typeof x === 'object' && x !== null) || typeof x === 'function') {
            try { then = x.then; }                                   /*  [Promises/A+ 2.3.3.1, 3.5]  */
            catch (e) {
                promise.reject(e);                                   /*  [Promises/A+ 2.3.3.2]  */
                return;
            }
        }

        /*  handle own Thenables    [Promises/A+ 2.3.2]
            and similar 'thenables' [Promises/A+ 2.3.3]  */
        if (typeof then === 'function') {
            var resolved = false;
            try {
                /*  call retrieved 'then' method */                  /*  [Promises/A+ 2.3.3.3]  */
                then.call(x,
                    /*  resolvePromise  */                           /*  [Promises/A+ 2.3.3.3.1]  */
                    function(y) {
                        if (resolved) return; resolved = true;       /*  [Promises/A+ 2.3.3.3.3]  */
                        if (y === x)                                 /*  [Promises/A+ 3.6]  */
                            promise.reject(new TypeError('circular thenable chain'));
                        else
                            resolve(promise, y);
                    },

                    /*  rejectPromise  */                            /*  [Promises/A+ 2.3.3.3.2]  */
                    function(r) {
                        if (resolved) return; resolved = true;       /*  [Promises/A+ 2.3.3.3.3]  */
                        promise.reject(r);
                    }
                );
            }
            catch (e) {
                if (!resolved)                                       /*  [Promises/A+ 2.3.3.3.3]  */
                    promise.reject(e);                               /*  [Promises/A+ 2.3.3.3.4]  */
            }
            return;
        }

        /*  handle other values  */
        promise.fulfill(x);                                          /*  [Promises/A+ 2.3.4, 2.3.3.4]  */
    };

    Promise.resolve = function(value) {
        return new Promise(function(resolve) {
            resolve(value);
        });
    };

    Promise.reject = function(reason) {
        return new Promise(function(resolve, reject) {
            reject(reason);
        });
    };

    /*  export API  */
    module.exports = Promise;
});