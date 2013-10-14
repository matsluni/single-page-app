define(function(require) {

    "use strict";

//    var $ = require('jquery');
    var Backbone = require('backbone');
    var Handlebars = require('handlebars');
    var tpl = require('text!tpl/Nav.html');
    var template = Handlebars.compile(tpl);

    var View = Backbone.View.extend({
        initialize: function() {
//            _.bindAll(this);

        },
        render: function() {
            $('#nav')
                    .html(template());
            return this;
        },
        selectMenuItem: function(menuItem) {
//            $('.nav li')
//                    .removeClass('active');
//            if (menuItem) {
//                $('#' + menuItem)
//                        .addClass('active');
//            }
        }
    });

    return {
        View: View
    };
});