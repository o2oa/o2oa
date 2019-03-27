/*
---
description: mBox is a powerful library, helping you to easily create tooltips, modal windows, notice messages and more.

authors: Stephan Wagner

license: MIT-style

requires:
 - core/1.4.5: '*'
 - more/Element.Measure

provides: [mBox]

documentation: http://htmltweaks.com/mBox/Documentation
...
*/

var mBox = new Class({

    Implements: [Options,Events],

    options: {
        id: '',						// id of the mBox wrapper (defaults to mBox_1, mBox_2, mBox_3...)

        theme: '',					// themes can be defined by css, e.g. in assets/themes/mBoxMyTheme.css

        addClass: {					// add additional classes to wrapper, container, title and/or footer
            wrapper: '',
            container: '',
            content: '',
            title: '',
            footer: ''
        },
        setStyles: {				// set additional styles to wrapper, container, title and/or footer
            wrapper: {},
            container: {},
            content: {},
            title: {},
            footer: {}
        },

        target: $(window),			// element reference or element-id where the mBox will be opened (use 'mouse' to show mBox on mouse-position)

        attach: null,				// element reference or element-id or element-classes of the elements which will open / close the mBox
        event: 'click',				// the event which will trigger the mBox to show (can be: 'click' || 'mouseover' (= 'mouseenter'))
        preventDefault: false,		// prevents the default action when clicking on attached item (e.g. prevents to follow a link when clicking on a link)

        //inject: null,				// TODO element to inject the wrapper to (open and close will be disabled)

        width: 'auto',				// width of the content area
        height: 'auto',				// height of the content area
        zIndex: 8000,				// z-index of wrapper

        content: null,				// element reference or element-id or element classes to inject into the content, use a string to set a string as content

        setContent:
            'data-setContent',		// if the attached element has the attribute data-setContent it's value will be set as new content on open

        load: null,					// set to ajax to load the content from url
        url: '',					// the url to load the content from if load is set to ajax or iframe
        reload: false,				// reloads the content each time the mBox is opened

        title: null,				// adds a title (element reference, element-id or string)
        footer: null,				// adds a footer (element reference, element-id or string)

        draggable: false,			// mBox can be dragged when clicking on title

        position: {
            x: 'center',			// horizontal position (use array to define outside or inside positions e.g. ['right', 'inside'] or ['left', 'center'])
            y: 'center'				// vertical position (use array to define outside or inside positions e.g. ['top', 'inside'] or ['bottom', 'center'])
        },							// the position-attributes default to 'left' and 'top', to change them to 'right' or 'bottom', define a third value in the array e.g. ['left', 'inside', 'right']

        fixed: null,				// set to false to force mBox to be absolute or true to force mBox to be fixed

        offset: {					// offsets to be added to position
            x: 0,
            y: 0
        },

        pointer: false,				// set to true to show a pointer (at least one position value needs to be 'outside')
        // set to 'right', 'left', 'top', 'bottom' to adjust the pointers position
        // if you want to add an offset to the pointer, provide an array, e.g. ['left', 10]

        fade: {						// set to false or 0 if you want to open/close the tooltip instantly (fade: false will open and close tooltip instantly)
            open: true,
            close: true
        },
        fadeDuration: {				// default fade duration when opening or closing (set fadeDuration: 250 to use it for open and close)
            open: 200,
            close: 300
        },
        fadeWhenOpen: false,		// set to true if you want to fade the mBox on open even if it's already open

        overlay: false,				// adds a overlay just underneath the mBox to prevent clicks on body
        overlayStyles: {			// set the color and the opacity of the overlay
            color: 'black',
            opacity: 0.75
        },
        overlayFadeDuration: 100,	// default fade duration for the overlay

        transition: {				// adds a transition when mBox is opened or closed, following shortcuts are availible:
            // transition: ['flyin', 'flyout', 'flyinout', 'flyoutin', 'bounce', 'bouncefly']
            open: null,				// you can also define your own transitions by using mootools transitions:
            close: null				// transition: {open: {transition: 'bounce:in', property: 'top', duration: 400, difference_start: 50, difference_end: 0}
        },

        closeOnEsc: true,			// close mBox when pressing esc
        closeOnClick: false,		// close mBox when clicking anywhere
        closeOnBoxClick: false,		// close mBox when clicking on the mBox
        closeOnWrapperClick: false,	// close mBox when clicking on the Wrapper of the mBox (wont close when clicking on children of mBox like title, content etc.)
        closeOnBodyClick: true,		// close mBox when clicking anywhere except the mBox itself
        closeOnMouseleave: false,	// close mBox when the mouse leaves the mBox area or the attached area

        closeInTitle: false,		// adds a close button in the title area // TODO won't work in tooltip title yet
        //closeInContainer: false,	// TODO add a close button to the container
        //closeInWindow: false,		// TODO add a close button to the window

        delayOpen: 0,				// delay opening the mBox in ms
        delayClose: 0,				// delay closing the mBox in ms
        delayOpenOnce: true,		// set to true if you want the delay to be ignored when the mBox didnt finish closing yet

        constructOnInit: true,		// true will construct the mBox once its finished initializing
        openOnInit: false			// true will construct (if it hasbn't been) and open the mBox once its finished initializing

        // Events:
        // onInit: function() {},
        // onOpen: function() {},
        // onOpenComplete: function() {},
        // onClose: function() {},
        // onCloseComplete: function() {}
        // onBoxReady: function() {}

        // onAjaxComplete: function() {}
    },

    // initialize
    initialize: function(options) {

        // set global vars
        this.block = false;				// set this.block to true so the mBox wont be opened until set to false again
        this.ignoreDelay = false;		// set this.ignoreDelay to true if you want to ignore the delays until set to false again
        this.ignoreDelayOnce = false;	// set this.ignoreDelayOnce to true if you want to ignore the delay only for one closing / opening

        // set the options
        this.setOptions(options);

        // fix options addClass, setStyles, fade, fadeDuration
        this.fixOptions();
        this.getPosition();

        // save current target in this.target
        this.target = this.getTarget();

        // set fixed to true or false depending on target
        if (this.options.fixed == null) {
            this.options.fixed = [$(window), $(document), $(document.body)].contains(this.target);
        }

        // no pointer if target == 'mouse'
        if (this.target == 'mouse') { this.options.pointer = false; }

        // targets will be saved in this.targets
        this.targets = [];

        // get mBox id
        this.id = (this.options.id || 'mBox' + (++mBox.currentId));

        // add listeners to elements
        this.addListeners();

        // construct the mBox
        if(this.options.constructOnInit) {
            this.construct();
        }

        // fire onInit events
        this.fireEvent('init').fireEvent('systemInit');

        // open mBox on init
        if(this.options.openOnInit) {
            this.open();
        }

        // add this instance to global collector
        mBox.instances.push(this);
    },

    // re-initialize mBox (e.g. after an ajax call)
    reInit: function() {
        // add listeners to new elements
        this.addListeners();
    },

    // fix options
    fixOptions: function() {
        if(typeof this.options.addClass == 'string') {
            this.options.addClass = {
                wrapper: this.options.addClass
            };
        }
        if(typeof this.options.setStyles == 'object' && !this.options.setStyles.wrapper && !this.options.setStyles.container && !this.options.setStyles.content && !this.options.setStyles.title && !this.options.setStyles.footer) {
            this.options.setStyles = {
                wrapper: this.options.setStyles
            };
        }
        this.options.fade = {
            open: this.options.fade.open || (this.options.fade == true),
            close: this.options.fade.close || (this.options.fade == true)
        };
        this.options.fadeDuration = {
            open: this.options.fadeDuration.open || this.options.fadeDuration,
            close: this.options.fadeDuration.close || this.options.fadeDuration
        };
    },

    // contruct the mBox
    construct: function() {
        if(this.wrapper) {
            return null;
        }
        // create wrapper
        this.wrapper = new Element('div', {
            id: this.id,
            'class': 'mBox ' + (this.defaultTheme || 'Core') + (this.options.theme ? '-' + this.options.theme : '') + ' ' + (this.options.addClass.wrapper || ''),
            styles: {
                zIndex: this.options.zIndex,
                position: (this.options.fixed == false || Browser.ie6 || Browser.ie7) ? 'absolute' : 'fixed',
                display: 'none',
                opacity: 0.00001,
                top: -12000,
                left: -12000,
                zoom: 1
            }
            //}).setStyles(this.options.setStyles.wrapper || {}).inject(document.body, 'bottom');
        }).setStyles(this.options.setStyles.wrapper || {});

        var target = $(target) || target || this.target || $(this.options.target) || this.options.target || $(this.options.attach);
        if (target && typeOf(target)==="element"){
            this.wrapper.inject(target, "after");
        }else{
            this.wrapper.inject(document.body, 'bottom');
        }

        // add mouse events to wrapper
        if(this.options.closeOnMouseleave) {
            this.wrapper.addEvents({
                mouseenter: function(ev) {
                    this.open();
                }.bind(this),
                mouseleave: function(ev) {
                    this.close();
                }.bind(this)
            });
        }

        // create container (contains content, title, footer)
        this.container = new Element('div', {
            'class': 'mBoxContainer' + ' ' + (this.options.addClass.container || '')
        }).setStyles(this.options.setStyles.container || {}).inject(this.wrapper);

        // create content
        this.content = new Element('div', {
            'class': 'mBoxContent' + ' ' + (this.options.addClass.content || ''),
            styles: {
                width: this.options.width,
                height: this.options.height
            }
        }).setStyles(this.options.setStyles.content || {}).inject(this.container);

        // load content into mBox
        this.load(this.options.content, this.options.title, this.options.footer, true); // TODO whats the true for?

        // fire boxReady events
        this.fireEvent('systemBoxReady').fireEvent('boxReady');
    },

    // add event listerners to elements
    addListeners: function(el) {

        // get elements to add events to, if none given use this.options.attach
        el = el || this.options.attach;

        elements = Array.from($(el)).combine(Array.from($$('.' + el))).combine(Array.from($$(el))).clean();

        if(!elements || elements.length == 0) return this;

        // add elements to this.targets
        this.targets.combine(elements);

        // create click or mouseenter/mouseleave events
        switch(this.options.event) {
            case 'mouseenter':
            case 'mouseover':
                var events = {
                    mouseenter: function(ev) {
                        this.target = this.getTargetFromEvent(ev);
                        this.source = this.getTargetElementFromEvent(ev);
                        this.open();
                    }.bind(this),
                    mouseleave: function(ev) {
                        this.close();
                    }.bind(this)
                };
                break;
            default:
                var events = {
                    click: function(ev) {
                        if(this.options.preventDefault) {
                            ev.preventDefault();
                        }
                        if(this.isOpen) {
                            this.close();
                        } else {
                            this.target = this.getTargetFromEvent(ev);
                            this.source = this.getTargetElementFromEvent(ev);
                            this.open();
                        }
                    }.bind(this)
                };
        }

        // add events if not already added
        $$(elements).each(function(el) {
            if(!el.retrieve('mBoxElementEventsAdded' + this.id)) {
                el.addEvents(events).store('mBoxElementEventsAdded' + this.id, true);
            }
        }.bind(this));
    },

    // load content with ajax into mBox
    loadAjax: function(sendObj) {
        if(!this.ajaxRequest) {
            this.ajaxRequest = new Request.HTML({
                link: 'cancel',
                update: this.content,
                onRequest: function() {
                    this.setContent('');
                    this.wrapper.addClass('mBoxLoading');
                }.bind(this),
                onComplete: function() {
                    this.wrapper.removeClass('mBoxLoading');
                    if(this.options.width == 'auto' || this.options.height == 'auto') {
                        this.setPosition();
                    }
                    this.fireEvent('ajaxComplete');
                }.bind(this)
            }).send();
        }
        this.ajaxRequest.send(sendObj);
        this.ajaxLoaded = true;
    },

    // open / show the mBox
    open: function(options) {
        if(!this.wrapper) {
            // construct the mBox
            this.construct();
        }

        if(typeof options != 'object') options = {};

        clearTimeout(this.timer);

        if(!this.isOpen && !this.block) {

            var complete = function() {
                this.ignoreDelayOnce = false;
                this.fireEvent('systemOpenComplete').fireEvent('openComplete');
            }.bind(this);

            var open = function(complete) {
                this.isOpen = true;

                // load content from ajax
                if(this.options.load == 'ajax' && this.options.url && (!this.ajaxLoaded || this.options.reload)) {
                    this.loadAjax({url: this.options.url});
                }

                // set target
                this.target = this.getTarget(options.target || null);

                // set new content
                if (this.options.setContent && this.source && this.source.getAttribute(this.options.setContent)) {

                    if ($(this.source.getAttribute(this.options.setContent))) {
                        this.content.getChildren().setStyle('display', 'none');
                        $(this.source.getAttribute(this.options.setContent)).setStyle('display', '');
                    } else {
                        var attribute_array = this.source.getAttribute(this.options.setContent).split('|'),
                            content = attribute_array[0] || null,
                            title = attribute_array[1] || null,
                            footer = attribute_array[2] || null;

                        this.load(content, title, footer);
                    }
                }


                if (this.wrapper) this.wrapper.inject(document.body, 'bottom');

                // set new position
                this.setPosition(null, options.position || null, options.offset || null);

                // fire open events
                this.fireEvent('systemOpen').fireEvent('open');

                // fade mBox
                if(this.fx) { this.fx.cancel(); }

                this.wrapper.setStyles({
                    display: ''
                });

                if(this.options.fadeWhenOpen) {
                    this.wrapper.setStyle('opacity', 0);
                }

                this.fx = new Fx.Tween(this.wrapper, {
                    property: 'opacity',
                    duration: this.options.fadeDuration.open,
                    link: 'cancel',
                    onComplete: complete
                })[(options.instant || !this.options.fade.open) ? 'set' : 'start'](1);

                // call complete function when showing instantly
                if(options.instant || !this.options.fade.open) {
                    complete();
                }

                // start additional transition
                var transition = this.getTransition();
                if(transition.open) {
                    var fx = new Fx.Tween(this.wrapper, {
                        property: transition.open.property || 'top',
                        duration: transition.open.duration || this.options.fadeDuration.open,
                        transition: transition.open.transition || null,
                        onStart: transition.open.onStart || null,
                        onComplete: transition.open.onComplete || null
                    });
                    fx.start((transition.open.start || (this.wrapper.getStyle(transition.open.property || 'top').toInt() + (transition.open.difference_start || 0))),
                        (transition.open.end || (this.wrapper.getStyle(transition.open.property || 'top').toInt() + (transition.open.difference_end || 0))));
                }

                // attach events to document and window
                this.attachEvents();

                // add overlay
                if(this.options.overlay) {
                    this.addOverlay((options.instant || !this.options.fade.open));
                }

                // set delay open once to true, set to false again when closing is finished
                if(this.options.delayOpenOnce) {
                    this.delayOpenOnce = true;
                }
            }.bind(this);

            // delay open or close instantly
            if(this.options.delayOpen > 0 && !this.ignoreDelay && !this.ignoreDelayOnce && !this.delayOpenOnce) {
                this.timer = open.delay(this.options.delayOpen, this, complete);
            } else {
                open(complete);
            }
        }
        return this;
    },

    // close / hide the mBox
    close: function(options) {
        if(typeof options != 'object') options = {};

        clearTimeout(this.timer);

        if(this.isOpen && !this.block) {

            var complete = function() {
                this.delayOpenOnce = false;
                this.ignoreDelayOnce = false;
                this.wrapper.setStyle('display', 'none');
                this.fireEvent('systemCloseComplete').fireEvent('closeComplete');

                if (this.wrapper){
                    var target = this.target || $(this.options.target) || this.options.target || $(this.options.attach);
                    if (target && typeOf(target)==="element"){
                        this.wrapper.inject(target);
                    }
                }
            }.bind(this);

            var close = function(complete) {
                this.isOpen = false;

                // fire close events
                this.fireEvent('systemClose').fireEvent('close');

                // detach document and window events
                this.detachEvents();

                // remove overlay
                if(this.options.overlay) {
                    this.removeOverlay((options.instant || !this.options.fade.close));
                }

                // fade mBox
                if(this.fx) { this.fx.cancel(); }

                this.fx = new Fx.Tween(this.wrapper, {
                    property: 'opacity',
                    duration: this.options.fadeDuration.close,
                    link: 'cancel',
                    onComplete: complete
                })[(options.instant || !this.options.fade.close) ? 'set' : 'start'](0);


                // call complete function when hiding instantly
                if(options.instant || !this.options.fade.close) {
                    complete();
                }

                // start additionel transition
                var transition = this.getTransition();
                if(transition.close) {
                    var fx = new Fx.Tween(this.wrapper, {
                        property: transition.close.property || 'top',
                        duration: transition.close.duration || this.options.fadeDuration.close,
                        transition: transition.close.transition || null,
                        onStart: transition.open.onStart || null,
                        onComplete: transition.open.onComplete || null
                    });
                    fx.start((transition.close.start || (this.wrapper.getStyle(transition.close.property || 'top').toInt() + (transition.close.difference_start || 0))),
                        (transition.close.end || (this.wrapper.getStyle(transition.close.property || 'top').toInt() + (transition.close.difference_end || 0))));
                }

            }.bind(this);

            // delay close or close instantly
            if(this.options.delayClose > 0 && !this.ignoreDelay && !this.ignoreDelayOnce) {
                this.timer = close.delay(this.options.delayClose, this, complete);
            } else {
                close(complete);
            }
        }
        return this;
    },

    // adds a overlay just beneath the mBox to prevent clicks on body
    addOverlay: function(instant) {
        if(!this.overlay) {
            this.overlay = new Element('div', { styles: {
                position: 'fixed',
                top: 0,
                left: 0,
                width: '100%',
                height: '100%',
                zIndex: (this.wrapper.getStyle('zIndex') - 1),
                background: this.options.overlayStyles.color || 'white',
                opacity: 0.001,
                display: 'none'
            }}).set('tween', {
                duration: this.options.overlayFadeDuration,
                link: 'cancel'
            }).inject($(document.body), 'bottom');
        }
        this.overlay.setStyle('display', 'block')[instant ? 'set' : 'tween']('opacity', (this.options.overlayStyles.opacity || 0.001));
        return this;
    },

    // remove the overlay
    removeOverlay: function(instant) {
        if(this.overlay) {
            this.overlay[instant ? 'set' : 'tween']('opacity', 0).get('tween').chain(function() {
                this.overlay.setStyle('display', 'none');
            }.bind(this));
        }
        return this;
    },

    // get the current or given target
    getTarget: function(target) {
        var target = $(target) || target || this.target || $(this.options.target) || this.options.target || $(this.options.attach);
        return target == 'mouse' ? 'mouse' : this.fixOperaPositioning($(target));
    },

    // get the target element from event target
    getTargetFromEvent: function(ev) {
        if(this.options.target) return this.fixOperaPositioning($(this.options.target));
        return this.getTargetElementFromEvent(ev);
    },

    // get the attached element from event
    getTargetElementFromEvent: function(ev) {
        if(ev && ev.target) {
            if(this.targets.contains(ev.target)) return this.fixOperaPositioning(ev.target);

            var parent_element = ev.target.getParent();
            while(parent_element != null) {
                if(this.targets.contains(parent_element)) {
                    return this.fixOperaPositioning(parent_element);
                }
                parent_element = parent_element.getParent();
            }
        }
        return null;
    },

    // TEMP: This function fixes temporarily the mootools 1.4.5 positioning bug in opera
    fixOperaPositioning: function(el) {
        if($(el) && !$(el).retrieve('OperaBugFixed') && el != window) {
            try {
                if(!($(el).getStyle('border-top-width').toInt() + $(el).getStyle('border-right-width').toInt() + $(el).getStyle('border-bottom-width').toInt() + $(el).getStyle('border-left-width').toInt())) {
                    $(el).setStyle('border', 0);
                }
            }
            catch(e) {}
            $(el).store('OperaBugFixed');
        }
        return el;
    },

    // get cached variable position or get a clean position variable
    getPosition: function(position) {
        if(!position && this.position) return this.position;
        position = position || this.options.position;
        this.position = {};

        // TODO shortcuts 'top' 'topRight' 'bottomLeft' etc.

        // get a clean x-position
        this.position.x = (typeof position == 'object' && typeof position.x == 'number') ?
            [position.x.toInt(), null] : ((typeof position != 'object' || !position.x || position.x == 'center' || (typeof position.x == 'object' && position.x[0] == 'center')) ?
                ['center', null] : (['right', 'left'].contains(position.x) ?
                    [position.x, (this.defaultInOut || 'inside')] : ((typeof position.x == 'object' && ['right', 'left'].contains(position.x[0])) ?
                        [position.x[0], (['inside', 'center', 'outside'].contains(position.x[1]) ? position.x[1] : (this.defaultInOut || 'inside'))] : ['center', null])));

        this.position.xAttribute = (this.position.x[3] == 'right' || (this.position.x[1] == 'inside' && this.position.x[0] == 'right')) ? 'right' : 'left';

        // get a clean y-position
        this.position.y = (typeof position == 'object' && typeof position.y  == 'number') ?
            [position.y.toInt(), null] : ((typeof position != 'object' || !position.y || position.y == 'center' || (typeof position.y == 'object' && position.y[0] == 'center')) ?
                ['center', null] : (['top', 'bottom'].contains(position.y) ?
                    [position.y, (this.defaultInOut || 'inside')] : ((typeof position.y == 'object' && ['top', 'bottom'].contains(position.y[0])) ?
                        [position.y[0], (['inside', 'center', 'outside'].contains(position.y[1]) ? position.y[1] : (this.defaultInOut || 'inside'))] : ['center', null])));

        this.position.yAttribute = (this.position.x[3] == 'bottom' || (this.position.y[1] == 'inside' && this.position.y[0] == 'bottom')) ? 'bottom' : 'top';

        return this.position;
    },

    // get cached offset variable or get a clean one
    getOffset: function(offset) {
        if(!offset && this.offset) return this.offset;
        offset = offset || this.options.offset;

        this.offset = {};
        this.offset.x = (typeof offset == 'number') ? offset : (!offset.x ? 0 : (offset.x.toInt() >= 0 || offset.x.toInt() < 0) ? offset.x.toInt() : 0);
        this.offset.y = (typeof offset == 'number') ? offset : (!offset.y ? 0 : (offset.y.toInt() >= 0 || offset.y.toInt() < 0) ? offset.y.toInt() : 0);

        return this.offset;
    },

    // get cached pointer variable or get a clean one
    getPointer: function(pointer) {
        if(!pointer && this.pointer) return this.pointer;
        pointer = pointer || this.options.pointer;
        if(!pointer) return false;
        var position = this.getPosition();
        this.pointer = {};
        if(position.y[1] == 'outside') {
            this.pointer.position = (position.y[0] == 'bottom') ? 'top' : 'bottom';
            this.pointer.adjustment = (typeof pointer == 'object' && ['center', 'right', 'left'].contains(pointer[0])) ? pointer[0] : (['center', 'right', 'left'].contains(pointer) ? pointer : 'center');
        } else if(position.x[1] == 'outside') {
            this.pointer.position = (position.x[0] == 'left') ? 'right' : 'left';
            this.pointer.adjustment = (typeof pointer == 'object' && ['center', 'top', 'bottom'].contains(pointer[0])) ? pointer[0] : (['center', 'top', 'bottom'].contains(pointer) ? pointer : 'center');
        } else {
            return null;
        }
        this.pointer.offset = (typeof pointer == 'object' && pointer[1] && typeof(pointer[1].toInt()) == 'number') ? pointer[1].toInt() : 0;

        // TODO pointer with inside && inside should be possible
        // TODO minimum offset if the container has a border-radius (this.container.getStyle('border-top-left-radius');

        this.pointer.offset = this.pointer.offset < 0 ? this.pointer.offset * (-1) : this.pointer.offset;
        this.pointer.offset = (this.pointer.adjustment == 'right' || this.pointer.adjustment == 'bottom') ? this.pointer.offset * (-1) : this.pointer.offset;
        return this.pointer;
    },

    // get cached transition variable or get a clean one
    getTransition: function() {
        if(this.transition) return this.transition;
        if(this.options.transition && ['flyin', 'flyout', 'flyinout', 'flyoutin', 'bounce', 'bouncein', 'bounceout', 'bounceinout', 'bouncefly'].contains(this.options.transition)) {

            this.transition = {};
            this.transition.open = {
                property: (this.position.yAttribute == 'top' || this.position.yAttribute == 'bottom') ? this.position.yAttribute : this.position.xAttribute,
                transition: 'quad:out',
                duration: 300
            }
            this.transition.close = Object.clone(this.transition.open);

            var distance = (20 * (this.position.yAttribute == 'bottom' || this.position.xAttribute == 'right' ? -1 : 1));

            switch(this.options.transition) {
                case 'flyin': // TODO flyin should only flyin
                case 'flyout':
                    this.transition.open.difference_start = this.transition.close.difference_end = distance * (this.options.transition == 'flyin' ? (-1) : 1);
                    break;
                case 'flyinout':
                case 'flyoutin':
                    distance = (distance * (this.options.transition == 'flyinout' ? 1 : (-1)));
                    this.transition.open.difference_start = distance * (-1);
                    this.transition.close.difference_end = distance;
                    break;
                case 'bounce':
                case 'bouncefly':
                case 'bouncein':
                case 'bounceout':
                case 'bounceinout':
                    this.transition.open.transition = 'bounce:out';
                    this.transition.open.duration = 450;
                    this.transition.open.difference_start = distance * (-1);
                    if(this.options.transition == 'bounceinout' || this.options.transition == 'bounceout' || this.options.transition == 'bouncefly') {
                        this.transition.close.difference_end = distance * (-1);
                    }
                    break;
            }
        } else {
            this.transition = {};
            this.transition.open = typeof this.options.transition.open != undefined ? this.options.transition.open : this.options.transition;
            this.transition.close = typeof this.options.transition.close != undefined ? this.options.transition.close : this.options.transition;
        }

        return this.transition;
    },

    // position the mBox
    setPosition: function(target, position, offset) {

        // get variables
        target = this.getTarget(target);
        position = this.getPosition(position);
        offset = this.getOffset(offset);
        pointer = this.getPointer();

        // attach to mouse if target == 'mouse'
        if(target == 'mouse') {
            posX = ((this.mouseX || 0) + 15 + offset.x);
            posY = ((this.mouseY || 0) + 15 + offset.y);
            this.wrapper.setStyles({
                'left': Math.floor(posX),
                'top': Math.floor(posY)
            });
            return this;
        }

        // get dimensions and coordinates
        if(!target || [$(window), $(document), $(document.body)].contains(target)) {
            var windowScroll = this.wrapper.getStyle('position') == 'fixed' ? {x: 0, y: 0} : $(window).getScroll(),
                targetDimensions = $(window).getSize();
            targetDimensions.width = targetDimensions.totalWidth = targetDimensions.x;
            targetDimensions.height = targetDimensions.totalHeight = targetDimensions.y;

            var targetCoordinates = {
                top: windowScroll.y,
                left: windowScroll.x,
                right: windowScroll.x + targetDimensions.width,
                bottom: windowScroll.y + targetDimensions.height
            };
        } else {
            if(!this.options.fixed != true) {
                this.wrapper.setStyle('position', 'absolute');
            }
            var targetDimensions = target.getDimensions({computeSize: true});
            var targetCoordinates = target.getCoordinates();
            if(targetDimensions.totalWidth == 0) {
                targetDimensions.width = targetDimensions.totalWidth = targetCoordinates.width;
                targetDimensions.height = targetDimensions.totalHeight = targetCoordinates.height;
            }
        }

        // set position to current position of target
        var posX = targetCoordinates.left || 0,
            posY = targetCoordinates.top || 0;

        var wrapperDimensions = this.wrapper.getDimensions({computeSize: true});

        // create pointer if not already created
        if(pointer && !this.pointerElement) {
            this.pointerElement = new Element('div', {
                'class': 'mBoxPointer ' + 'mBoxPointer' + pointer.position.capitalize(),
                styles: {position: 'absolute'}
            }).setStyle(pointer.position, 0).inject(this.wrapper, 'top');

            // opera wont calculate the size of the pointer correctly, needs to get fixed properly
            if(Browser.opera) {
                var tempContainer = new Element('div', {'class': 'mBox ' + (this.defaultTheme || 'Core') + (this.options.theme ? '-' + this.options.theme : '')}).inject(document.body).grab(this.pointerElement);
                this.pointerDimensions = this.pointerElement.getDimensions({computeSize: true});
                this.pointerElement.inject(this.wrapper, 'top');
                tempContainer.destroy();
            } else {
                this.pointerDimensions = this.pointerElement.getDimensions({computeSize: true});
            }

            this.container.setStyle('margin-' + pointer.position,
                (pointer.position == 'left' || pointer.position == 'right') ?
                    (this.pointerDimensions.width - this.container.getStyle('border-' + pointer.position).toInt()) :
                    (this.pointerDimensions.height - this.container.getStyle('border-' + pointer.position).toInt()));
        }

        // adjust wrapper to pointer position
        if(pointer && this.pointerElement) {

            // if position x and position y is outside, fix pointer position
            if(position.x[1] == 'outside' && position.y[1] == 'outside' && pointer.adjustment == 'center') {
                pointer.adjustment = (position.x[0] == 'left') ? 'right' : 'left';
                switch(position.x[0]) {
                    case 'left':
                        posX += wrapperDimensions.totalWidth - (this.pointerDimensions.width / 2);
                        break;
                    case 'right':
                        posX -= (this.pointerDimensions.width / 2);
                        break;
                }
            }

            // calculate pointer margin and extra offset
            var wrapperOffset = 0, pointerMargin = 0, offsetPointerX = 0, offsetPointerY = 0;

            switch(pointer.adjustment) {
                case 'center':
                    pointerMargin = (pointer.position == 'top' || pointer.position == 'bottom') ? ((wrapperDimensions.totalWidth / 2) - (this.pointerDimensions.width / 2)) : ((wrapperDimensions.totalHeight / 2) - (this.pointerDimensions.height / 2));
                    break;
                case 'left':
                case 'right':
                    switch(position.x[1]) {
                        case 'inside':
                            offsetPointerX += ((this.pointerDimensions.width / 2) * -1) + ((position.x[0] == 'right') ? wrapperDimensions.totalWidth : 0);
                            break;
                        default:
                            if(position.x[0] == 'center') {
                                offsetPointerX += (wrapperDimensions.totalWidth / 2) - (this.pointerDimensions.width / 2);
                            }
                    }
                    posX += offsetPointerX - ((pointer.adjustment == 'right') ? (wrapperDimensions.totalWidth - this.pointerDimensions.width) : 0);
                    pointerMargin = (pointer.adjustment == 'right') ? (wrapperDimensions.totalWidth - this.pointerDimensions.width) : 0;
                    break;
                case 'top':
                case 'bottom':
                    switch(position.y[1]) {
                        case 'inside':
                            offsetPointerY += ((this.pointerDimensions.height / 2) * -1) + ((position.y[0] == 'bottom') ? wrapperDimensions.totalHeight : 0);
                            break;
                        default:
                            if(position.y[0] == 'center') {
                                offsetPointerY += (wrapperDimensions.totalHeight / 2) - (this.pointerDimensions.height / 2);
                            }
                    }
                    posY += offsetPointerY - ((pointer.adjustment == 'bottom') ? (wrapperDimensions.totalHeight - this.pointerDimensions.height) : 0);
                    pointerMargin = (pointer.adjustment == 'bottom') ? (wrapperDimensions.totalHeight - this.pointerDimensions.height) : 0;
                    break;
            }
            switch(pointer.position) {
                case 'top':
                case 'bottom':
                    posX += (pointer.offset * (-1));
                    break;
                case 'left':
                case 'right':
                    posY += (pointer.offset * (-1));
                    break;
            }
            this.pointerElement.setStyle((pointer.position == 'top' || pointer.position == 'bottom') ? 'left' : 'top', pointerMargin + pointer.offset);
        }

        // get wrapper dimensions including pointer
        wrapperDimensions = this.wrapper.getDimensions({computeSize: true});

        // calculate position
        switch(position.x[0]) {
            case 'center':
                posX += (targetDimensions.totalWidth / 2) - (wrapperDimensions.totalWidth / 2);
                break;
            case 'right':
                posX += targetDimensions.totalWidth - (position.x[1] == 'inside' ? wrapperDimensions.totalWidth : (position.x[1] == 'center' ? (wrapperDimensions.totalWidth / 2) : 0));
                break;
            case 'left':
                posX -= (position.x[1] == 'outside' ? wrapperDimensions.totalWidth : (position.x[1] == 'center' ? (wrapperDimensions.totalWidth / 2) : 0));
                break;
            default:
                posX = position.x;
        }
        switch(position.y[0]) {
            case 'center':
                posY += (targetDimensions.totalHeight / 2) - (wrapperDimensions.totalHeight / 2);
                break;
            case 'bottom':
                posY += targetDimensions.totalHeight - (position.y[1] == 'inside' ? wrapperDimensions.totalHeight : (position.y[1] == 'center' ? (wrapperDimensions.totalHeight / 2) : 0));
                break;
            case 'top':
                posY -= (position.y[1] == 'outside' ? wrapperDimensions.totalHeight : (position.y[1] == 'center' ? (wrapperDimensions.totalHeight / 2) : 0));
                break;
            default:
                posX = position.y;
        }

        // reset wrapper positions
        this.wrapper.setStyles({top: null, right: null, bottom: null, left: null});

        // calculate 'bottom' or 'right' positions if needed
        var windowDimensions = $(window).getSize();

        if(position.xAttribute == 'right') {
            posX = windowDimensions.x - (posX + wrapperDimensions.totalWidth);
        }
        if(position.yAttribute == 'bottom') {
            posY = windowDimensions.y - (posY + wrapperDimensions.totalHeight);
        }

        // add global offsets and set positions
        posX = posX || 0;
        posX += offset.x;
        posY += offset.y;
        this.wrapper.setStyle(position.xAttribute, posX.floor());
        this.wrapper.setStyle(position.yAttribute, posY.floor());

        return this;
    },

    // set up content
    setContent: function(content, where) {
        if(content != null) {
            if($(content) || $$('.' + content).length > 0) {
                this[where || 'content'].grab($(content) || $$('.' + content));
                if($(content)) $(content).setStyle('display', '');
            } else if(content != null) {
                this[where || 'content'].set('html', content);
            }
        }
        return this;
    },

    // set up title
    setTitle: function(content) {
        if(content != null && !this.titleContainer) {
            this.titleContainer = new Element('div', {
                'class': 'mBoxTitleContainer'
            }).inject(this.container, 'top');

            this.title = new Element('div', {
                'class': 'mBoxTitle ' + (this.options.addClass.title || ''),
                styles: (this.options.setStyles.title || {})
            }).inject(this.titleContainer);

            this.wrapper.addClass('hasTitle');

            if(this.options.draggable && window['Drag'] != null) {
                new Drag(this.wrapper, { handle: this.titleContainer});
                this.titleContainer.addClass('mBoxDraggable');
            }

            if(this.options.closeInTitle) {
                new Element('div', {
                    'class': 'mBoxClose',
                    events: {
                        click: function() {
                            this.close();
                        }.bind(this)
                    }
                }).grab(new Element('div')).inject(this.titleContainer);
            }
        }
        if(content != null) {
            this.setContent(content, 'title');
        }
        return this;
    },

    // set up footer
    setFooter: function(content) {
        if(content != null && !this.footerContainer) {
            this.footerContainer = new Element('div', {
                'class': 'mBoxFooterContainer'
            }).inject(this.container, 'bottom');

            this.footer = new Element('div', {
                'class': 'mBoxFooter ' + (this.options.addClass.footer || ''),
                styles: (this.options.setStyles.footer || {})
            }).inject(this.footerContainer);

            this.wrapper.addClass('hasFooter');
        }
        if(content != null) {
            this.setContent(content, 'footer');
        }
        return this;
    },

    // set up content, title and/or footer
    load: function(content, title, footer) {
        this.setContent(content);
        this.setTitle(title);
        this.setFooter(footer);
        return this;
    },

    // return the mBox as html
    getHTML: function(content, title, footer) {
        this.load(content, title, footer);
        return '<div>' + this.wrapper.get('html') + '</div>';
    },

    // attach events to document and window
    attachEvents: function() {

        // event: close mBox when clicking esc
        this.escEvent = function(ev) {
            if(ev.key == 'esc') {
                this.ignoreDelayOnce = true;
                this.close();
            }
        }.bind(this);

        if(this.options.closeOnEsc) {
            $(window).addEvent('keyup', this.escEvent);
        }

        // event: reposition mBox on window resize or scroll
        this.resizeEvent = function(ev) {
            this.setPosition();
        }.bind(this);
        $(window).addEvent('resize', this.resizeEvent);

        if(this.options.fixed && (Browser.ie6 || Browser.ie7)) {
            $(window).addEvent('scroll', this.resizeEvent);
        }

        // event: close mBox when clicking anywhere
        this.closeOnClickEvent = function(ev) {
            if(this.isOpen && ($(this.options.attach) != ev.target && !$$('.' + this.options.attach).contains(ev.target))) {
                this.ignoreDelayOnce = true;
                this.close();
            }
        }.bind(this);

        if(this.options.closeOnClick) {
            $(document).addEvent('mouseup', this.closeOnClickEvent);
        }

        // event: close mBox when clicking on wrapper or it's children
        this.closeOnBoxClickEvent = function(ev) {
            if(this.isOpen && (this.wrapper == ev.target || this.wrapper.contains(ev.target))) {
                this.ignoreDelayOnce = true;
                this.close();
            }
        }.bind(this);

        if(this.options.closeOnBoxClick) {
            $(document).addEvent('mouseup', this.closeOnBoxClickEvent);
        }

        // event: close mBox when clicking on wrapper directly
        this.closeOnWrapperClickEvent = function(ev) {
            if(this.isOpen && this.wrapper == ev.target) {
                this.ignoreDelayOnce = true;
                this.close();
            }
        }.bind(this);

        if(this.options.closeOnWrapperClick) {
            $(document).addEvent('mouseup', this.closeOnWrapperClickEvent);
        }

        // event: close mBox when clicking on body
        this.closeOnBodyClickEvent = function(ev) {
            if(this.isOpen && ($(this.options.attach) != ev.target && !$$('.' + this.options.attach).contains(ev.target)) && ev.target != this.wrapper && !this.wrapper.contains(ev.target)) {
                this.ignoreDelayOnce = true;
                this.close();
            }
        }.bind(this);

        if(this.options.closeOnBodyClick) {
            $(document).addEvent('mouseup', this.closeOnBodyClickEvent);
        }

        // event: attach mBox to mouse position
        this.mouseMoveEvent = function(ev) {
            this.mouseX = ev.page.x;
            this.mouseY = ev.page.y;
            this.setPosition('mouse');
        }.bind(this);

        if(this.target == 'mouse') {
            $(document).addEvent('mousemove', this.mouseMoveEvent);
        }
    },

    // remove events from document or window
    detachEvents: function() {
        if(this.options.fixed && (Browser.ie6 || Browser.ie7)) {
            $(window).removeEvent('scroll', this.resizeEvent);
        }
        $(window).removeEvent('keyup', this.keyEvent);
        $(window).removeEvent('resize', this.resizeEvent);
        $(document).removeEvent('mouseup', this.closeOnClickEvent);
        $(document).removeEvent('mouseup', this.closeOnBoxClickEvent);
        $(document).removeEvent('mouseup', this.closeOnWrapperClickEvent);
        $(document).removeEvent('mouseup', this.closeOnBodyClickEvent);
        $(document).removeEvent('mousemove', this.mouseMoveEvent);
    },

    // dispose of wrapper and remove it from DOM
    destroy: function() {
        mBox.instances.erase(this);
        this.detachEvents();
        this.wrapper.dispose();
        delete this.wrapper;
    }

});

// store global mBox instances
mBox.instances = [];

// use global mBox ids
mBox.currentId = 0;

// reinit mBoxes (e.g. once an ajax has been called)
mBox.reInit = function() {
    if(mBox.addConfirmEvents) {
        mBox.addConfirmEvents();
    }
    mBox.instances.each(function(instance) {
        try {
            instance.reInit();
        }
        catch(e) {}
    });
};
/*
---
description: With mBox.Notice you can show little notices to your visitors.

authors: Stephan Wagner

license: MIT-style

requires:
 - mBox
 - core/1.4.5: '*'
 - more/Element.Measure

provides: [mBox.Notice]

documentation: http://htmltweaks.com/mBox/Documentation/Notice
...
*/

mBox.Notice = new Class({

    Extends: mBox,

    options: {

        type: 'Default',			// the type of the notice (defaults to 'default'), possible types are: 'ok', 'error', 'info', 'notice'

        position: {					// to use the move tween (see below), position.y has to be 'bottom' or 'top' and both positions need to be 'inside'
            x: ['left', 'inside'],
            y: ['bottom', 'inside']
        },

        offset: {
            x: 30,
            y: 30
        },

        fixed: true,

        move: true,					// true will move the notice box from a window edge to its position instead of fading it (when opening)
        moveDuration: 500,			// duration of the move-tween

        delayClose: 4000,			// duration the notice will be visible

        fade: true,

        fadeDuration: {
            open: 250,
            close: 400
        },

        target: $(window),

        zIndex: 1000000,

        closeOnEsc: false,
        closeOnBoxClick: true,
        closeOnBodyClick: false,

        openOnInit: true
    },

    // initialize parent
    initialize: function(options) {
        this.defaultInOut = 'inside';
        this.defaultTheme = 'Notice';

        // add move events / options when initializing parent
        options.onSystemBoxReady = function() {
            this.container.addClass('mBoxNotice' + (this.options.type.capitalize() || 'Default'));

            if(this.options.move && (this.position.x[1] == 'inside' || this.position.x[0] == 'center') && this.position.y[1] == 'inside' && (this.position.y[0] == 'top' || this.position.y[0] == 'bottom')) {

                var wrapper_dimensions = this.wrapper.getDimensions({computeSize: true});

                this.container.setStyle('position', 'absolute');
                this.container.setStyle((this.position.y[0] == 'top' ? 'bottom' : 'top'), 0);

                this.wrapper.setStyles({
                    height: 0,
                    width: wrapper_dimensions.totalWidth,
                    overflowY: 'hidden'
                });

                this.options.transition = {
                    open: {
                        transition: 'linear',
                        property: 'height',
                        duration: this.options.moveDuration,
                        start: 0,
                        end: wrapper_dimensions.totalHeight + this.options.offset.y
                    }
                };

                this.options.offset.y = 0;

                this.options.delayClose += this.options.moveDuration;
            }
        };

        // close stored notice and save new one in window
        options.onSystemOpen = function() {
            if($(window).retrieve('mBoxNotice')) {
                $(window).retrieve('mBoxNotice').ignoreDelay = true;
                $(window).retrieve('mBoxNotice').close();
            }
            $(window).store('mBoxNotice', this);
        };

        // close notice automatically
        options.onSystemOpenComplete = function() {
            this.close();
        };

        // destroy notice when close is complete
        options.onSystemCloseComplete = function() {
            this.destroy();
        };

        // init parent
        this.parent(options);
    }

});

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