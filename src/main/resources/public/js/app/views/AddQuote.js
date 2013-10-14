define(function(require) {

    "use strict";

    var Backbone = require('backbone');
    var Handlebars = require('handlebars');
    var tpl = require('text!tpl/AddQuote.html');
    var template = Handlebars.compile(tpl);

    var quoteNameModel = require('app/models/QuoteName');

    var View = Backbone.View.extend({
        initialize: function() {
            console.log("> AddQuote init " );
        },
        events: {
            "click #submit": "register"
        },
        register: function() {
            console.log("> AddQuote register " );
            var quoteName = $('input[name=quote]')
                    .val();
            var quote = new quoteNameModel.Model();
            console.log("> QuoteName " + quoteName);

            quote.save({name: quoteName});
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