define(function(require) {

    "use strict";

    var $ = require('jquery');
    var Backbone = require('backbone');
    var Handlebars = require('handlebars');
    var tpl = require('text!tpl/AddStock.html');
    var template = Handlebars.compile(tpl);

    var stockNameModel = require('app/models/StockName');

    return Backbone.View.extend({
        initialize: function() {
        },
        events: {
            'click input#submit': 'register'
        },
        register: function() {
            var stockName = $('input[name=stock]').val();
            var stock = new stockNameModel.Model();

            console.log("Stockname " + stockName);

            stock.save({name: stockName});
        },
        render: function() {
            $(this.el)
                    .html(template());
            return this;
        }
    });

});