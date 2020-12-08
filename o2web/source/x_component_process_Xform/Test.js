/** @class This class represents a closabe UI window with changeable content. */
Nem.Ui.Window = new Class(
    /** @lends Nem.Ui.Window# */
    {
        Implements: [Options, Events],

        /** The options that can be set. */
        options: {
            /**
             * Some description for caption.
             * @memberOf Nem.Ui.Window#
             * @type String
             */
            caption:    "Ventana",
            /**
             * ...
             */
            icon:       $empty,
            centered:   true,
            id:         $empty,
            width:      $empty,
            height:     $empty,
            modal:      false,
            desktop:    $empty,
            x:          $empty,
            y:          $empty,
            layout:     $empty
        },

        /**
         * The constructor. Will be called automatically when a new instance of this class is created.
         *
         * @param {Object} options The options to set.
         */
        initialize: function(options)
        {
            this.setOptions(options);
            /* ... */
        },

        /**
         * Sets the HTML content of the window.
         *
         * @param {String} content The content to set.
         */
        setHtmlContents: function(content)
        {
            /* ... */
        },

        /**
         * Sets the inner text of the window.
         *
         * @param {String} text The text to set.
         */
        setText: function(text)
        {
            /* ... */
        },

        /**
         * Fired when the window is closed.
         *
         * @event
         * @param {Object} win The closed window.
         */
        close: function(win)
        {
            /* ... */
        },

        /* ... */

    });