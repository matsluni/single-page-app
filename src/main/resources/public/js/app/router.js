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
//    var home = require('app/views/Home');
//    var text = require('app/views/general/SimpleTextView');

    var nav = require('app/views/Nav');
    var navView = new nav.View();
//    var footer = require('app/views/page/Footer');
//    var footerView = new footer.View();

    return Backbone.Router.extend({
        routes: {
            "":"home",
            "home": "home",
            "quotes": "quotes",
            "add": "addQuotes"
        },
        initialize: function() {
            navView.render();
        },
        home: function() {
            console.log("HOME-Route");
            // var homeView = new HomeView();
            navView.selectMenuItem();

//            $('#content')
//                    .html(homeView.render().el);
        },
        quotes: function() {
            console.log("Quote-Route");
            this.quotesView = new QuotesView();
//            $('#content')
//                    .html(
//                    this.quotesView.render().el);
        },
        addQuotes: function() {
            console.log("ADD-Route");
            this.addQuoteView = new AddQuoteView();
//            $('#content')
//                    .html(
//                    this.addQuoteView.render().el);
        }
    });
});