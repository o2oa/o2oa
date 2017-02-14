angular.module('kityminderEditor')
    .service('resourceService', ['$document', function($document) {
    var openScope = null;

    this.open = function( dropdownScope ) {
        if ( !openScope ) {
            $document.bind('click', closeDropdown);
            $document.bind('keydown', escapeKeyBind);
        }

        if ( openScope && openScope !== dropdownScope ) {
            openScope.resourceListOpen = false;
        }

        openScope = dropdownScope;
    };

    this.close = function( dropdownScope ) {
        if ( openScope === dropdownScope ) {
            openScope = null;
            $document.unbind('click', closeDropdown);
            $document.unbind('keydown', escapeKeyBind);
        }
    };

    var closeDropdown = function( evt ) {
        // This method may still be called during the same mouse event that
        // unbound this event handler. So check openScope before proceeding.
        //console.log(evt, openScope);
        if (!openScope) { return; }

        var toggleElement = openScope.getToggleElement();
        if ( evt && toggleElement && toggleElement[0].contains(evt.target) ) {
            return;
        }

        openScope.$apply(function() {
            console.log('to close the resourcelist');
            openScope.resourceListOpen = false;
        });
    };

    var escapeKeyBind = function( evt ) {
        if ( evt.which === 27 ) {
            openScope.focusToggleElement();
            closeDropdown();
        }
    };
}])