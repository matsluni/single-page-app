define(function(require) {

    "use strict";

    var $ = require('jquery');
    var Backbone = require('backbone');
    var Highcharts = require('highstock');
    
    var Handlebars = require('handlebars');
    var tpl = require('text!tpl/Quotes.html');
    var template = Handlebars.compile(tpl);

    var quoteNameModel = require('app/models/QuoteName');
    var fullQuoteModel = require('app/models/FullQuote');
    var quoteNames = new quoteNameModel.Collection();

    return Backbone.View.extend({
        initialize: function() {
            quoteNames.on('reset', this.render, this);
        },
        events: {
            "click a": "showDetail"
        },
        showDetail: function(e) {
            e.preventDefault();
            var quoteName = $(e.currentTarget)
                    .data("id");
            var quoteData = new fullQuoteModel.Model({id: quoteName})

            Highcharts.setOptions({
                global: {
                    useUTC: false
                }
            });
            $.getJSON('api/fullQuotes/' + quoteName, function(data) {
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
                            name: quoteName,
                            data: data.prices
                        }]
                });
            });
        },
        render: function() {
            var self = this;

            // fetch, when that is done, replace 'Loading' with content
            quoteNames.fetch()
                    .done(function() {
                
                        console.log("Size: " + quoteNames.length);
                
                var renderedContent = template(quoteNames.toJSON()[0]);
                self.$el.html(renderedContent);

            });
            return this;
        }
    });

});