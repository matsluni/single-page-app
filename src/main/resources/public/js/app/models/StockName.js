define(function(require) {

    "use strict";

    var Backbone = require('backbone');

    var StockNameModel = Backbone.Model.extend({
        url: "/api/stockName"
    });

    var StockNameCollection = Backbone.Collection.extend({
        model: StockNameModel,
        url: "/api/stockNames"
    });

    return {
        Model: StockNameModel,
        Collection: StockNameCollection
    };
});