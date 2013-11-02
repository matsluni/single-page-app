define(function(require) {

    'use strict';
    var $ = require('jquery');
    var Backbone = require('backbone');
    
    var ShellView = require('app/views/Shell');
    var HomeView = require('app/views/Home');
    var AddStockView = require('app/views/AddStock');
    var StockView = require('app/views/Stock');

    var $body = $('body');
    var shellView = new ShellView({el: $body}).render();
    var $content = $("#content", shellView.el);

    var homeView = new HomeView({el: $content});
    var stockView = new StockView({el: $content});
    var addStockView = new AddStockView({el: $content});

    return Backbone.Router.extend({
        routes: {
            "": "home",
            "home": "home",
            "stocks": "stocks",
            "add": "addStocks"
        },
        initialize: function() {
        },
        home: function() {
            homeView.render();
            shellView.selectMenuItem('home-menu');
        },
        stocks: function() {
            stockView.render();
            shellView.selectMenuItem('quote-menu');
        },
        addStocks: function() {
            addStockView.render();
            shellView.selectMenuItem('add-quote-menu');

        }
    });
});