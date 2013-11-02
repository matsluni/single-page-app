define(function(require) {

    'use strict';
    var $ = require('jquery');
    var Backbone = require('backbone');
    
    var ShellView = require('app/views/Shell');
    var HomeView = require('app/views/Home');
    var AddQuoteView = require('app/views/AddQuote');
    var QuoteView = require('app/views/Quote');

    var $body = $('body');
    var shellView = new ShellView({el: $body}).render();
    var $content = $("#content", shellView.el);

    var homeView = new HomeView({el: $content});
    var quoteView = new QuoteView({el: $content});
    var addQuoteView = new AddQuoteView({el: $content});

    return Backbone.Router.extend({
        routes: {
            "": "home",
            "home": "home",
            "quotes": "quotes",
            "add": "addQuotes"
        },
        initialize: function() {
        },
        home: function() {
            homeView.render();
            shellView.selectMenuItem('home-menu');
        },
        quotes: function() {
            quoteView.render();
            shellView.selectMenuItem('quote-menu');
        },
        addQuotes: function() {
            addQuoteView.render();
            shellView.selectMenuItem('add-quote-menu');

        }
    });
});