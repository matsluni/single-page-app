define(function(require) {

    'use strict';
    var $ = require('jquery');
    var Backbone = require('backbone');
//    var list = require('app/views/content/List');
//    var menu = require('app/views/content/Menu');
//    var table = require('app/views/content/Table');
//    var missing = require('app/views/general/Missing');
//    var colorpicker = require('app/views/Colorpicker');
//    var components = require('app/views/Components');
//    var javascript = require('app/views/Javascript');
//    var enhanced = require('app/views/Enhanced');
    var home = require('app/views/Home');
    var addQuote = require('app/views/AddQuote');
    var quote = require('app/views/Quote');
//    var text = require('app/views/general/SimpleTextView');

    var nav = require('app/views/Nav');
    var test = require('app/views/Test');
    var navView = new nav.View();
//    var footer = require('app/views/page/Footer');
//    var footerView = new footer.View();

    return Backbone.Router.extend({
        routes: {
            "": "home",
            "test": "test",
            "home": "home",
            "quotes": "quotes",
            "add": "addQuotes"
        },
        initialize: function() {
            navView.render();
        },
        test: function() {
            console.log("Test-Route");
            var view = new test.View();
            var ret = view.render();
            console.log(ret);
        },
        home: function() {
            console.log("HOME-Route");
            var view = new home.View();
            view.render();
            navView.selectMenuItem();
//            $('#content')
//                    .html(homeView.render().el);
        },
        quotes: function() {
            console.log("Quote-Route");
            var view = new quote.View();
            view.render();
//            $('#content')
//                    .html(
//                    this.quotesView.render().el);
        },
        addQuotes: function() {
            console.log("ADD-Route");
            var view = new addQuote.View();
            view.render();
//            $('#content')
//                    .html(
//                    this.addQuoteView.render().el);
        }
    });
});