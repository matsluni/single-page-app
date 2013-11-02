define(function(require) {

    "use strict";

    var $ = require('jquery');
    var Backbone = require('backbone');
    var Highcharts = require('highstock');
    
    var Handlebars = require('handlebars');
    var tpl = require('text!tpl/Stocks.html');
    var template = Handlebars.compile(tpl);

    var stockNameModel = require('app/models/StockName');
    var fullStockModel = require('app/models/FullStock');
    var stockNames = new stockNameModel.Collection();

    return Backbone.View.extend({
        initialize: function() {
            stockNames.on('reset', this.render, this);
        },
        events: {
            "click a": "showDetail"
        },
        showDetail: function(e) {
            e.preventDefault();
            var stockName = $(e.currentTarget).data("id");
            var stockData = new fullStockModel.Model({id: stockName})

            Highcharts.setOptions({
                global: {
                    useUTC: false
                }
            });
            $.getJSON('api/fullStocks/' + stockName, function(data) {
                var chart = new Highcharts.StockChart({
                    chart: {
                        renderTo: 'stock_detail'
                    },
                    rangeSelector: {
                        selected: 5
                    },
                    title: {
                        text: data.name + ' Stock History'
                    },
                    series: [{
                            name: stockName,
                            data: data.prices
                        }]
                });
            });
        },
        render: function() {
            var self = this;

            // fetch, when that is done, replace 'Loading' with content
            stockNames.fetch()
                    .done(function() {
                
                        console.log("Size: " + stockNames.length);
                
                var renderedContent = template(stockNames.toJSON()[0]);
                self.$el.html(renderedContent);

            });
            return this;
        }
    });

});