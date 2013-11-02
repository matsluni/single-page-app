define(function(require) {

    "use strict";
    var $ = require('jquery');
    var Backbone = require('backbone');
    var Handlebars = require('handlebars');

    var tpl = require('text!tpl/Shell.html');
    var template = Handlebars.compile(tpl);
    var $menuItems;

    return Backbone.View.extend({
        initialize: function() {
        },
        render: function() {

            $(this.el)
                    .html(template());

            $menuItems = $('.navbar .nav li', this.el);

            return this;
        },
        selectMenuItem: function(menuItem) {
            $menuItems.removeClass('active');
            if (menuItem) {
                $('.' + menuItem)
                        .addClass('active');
            }
        }

    });

});

