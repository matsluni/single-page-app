define(function(require) {

    "use strict";

    var Backbone = require('backbone');
    var Handlebars = require('handlebars');
    var tpl = require('text!tpl/AddQuote.html');
    var template = Handlebars.compile(tpl);

    var quoteNameModel = require('app/models/QuoteName');

    var View = Backbone.View.extend({
        el: 'div',
        initialize: function() {
            console.log("> AddQuote init ");
        },
        events: {
            'click input[type="submit"]': 'register',
            'click button#demo': 'register'
        },
        register: function() {
            alert("OK");
            console.log("> AddQuote register ");

            var quoteName = $('input[name=quote]')
                    .val();
            var quote = new quoteNameModel.Model();
            console.log("> QuoteName " + quoteName);

            quote.save({name: quoteName});
        },
        render: function() {
//            console.log(template());
//            this.$el.html = template();
//            console.log(this.$el.html);
            $('#section')
                    .html("<div><button id='demo'>test</button></div>");
            return this;
        }
    });
    return {
        View: View
    };
});