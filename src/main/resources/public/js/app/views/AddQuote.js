define(function(require) {

    "use strict";

    var $ = require('jquery');
    var Backbone = require('backbone');
    var Handlebars = require('handlebars');
    var tpl = require('text!tpl/AddQuote.html');
    var template = Handlebars.compile(tpl);

    var quoteNameModel = require('app/models/QuoteName');

    return Backbone.View.extend({
        initialize: function() {
        },
        events: {
            'click input#submit': 'register'
        },
        register: function() {
            var quoteName = $('input[name=quote]')
                    .val();
            var quote = new quoteNameModel.Model();

            console.log("QuoteName " + quoteName);

            quote.save({name: quoteName});
        },
        render: function() {
            $(this.el)
                    .html(template());
            return this;
        }
    });

});