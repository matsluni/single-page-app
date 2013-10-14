define(function(require) {

    "use strict";

    var _ = require('underscore');
    var Backbone = require('backbone');
    var Handlebars = require('handlebars');
    var Highcharts = require('highstock');
    var tpl = require('text!tpl/Quotes.html');
    var template = Handlebars.compile(tpl);

    var quoteNameModel = require('app/models/QuoteName');
    var fullQuoteModel = require('app/models/FullQuote');

    var View = Backbone.View.extend({
        initialize: function() {
            // TODO check underscore version
            _.bindAll(this);

            this.quoteNames = new quoteNameModel.Collection();
            this.quoteNames.on('reset', this.render, this);
        },
        events: {
            "click a": "showDetail"
        },
        showDetail: function(e) {
            e.preventDefault();
            var quoteName = $(e.currentTarget)
                    .data("id");
//        $('#stock_detail').html("hallo from function with: " + id);
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
            this.quoteNames.fetch()
                    .done(function() {
                $('#section')
                        .html(template(self.quoteNames.toJSON()[0]));
            });
            return this;
        }
    });
    return {
        View: View
    };
});