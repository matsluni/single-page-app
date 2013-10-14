define(function(require) {

    "use strict";

    var Backbone = require('backbone');
    var Handlebars = require('handlebars');
    var tpl = require('text!tpl/Home.html');
    var template = Handlebars.compile(tpl);

    var View = Backbone.View.extend({
        initialize: function() {
        },
        render: function() {
            $('#section')
                    .html(template());
            return this;
        }
    });


    return {
        View: View
    };
});