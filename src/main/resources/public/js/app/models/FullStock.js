define(function(require) {

    "use strict";

    var Backbone = require('backbone');
    
    var FullStockDataModel = Backbone.Model.extend({
        url: "/api/fullStocks"
    });

    return {
        Model: FullStockDataModel
    };
});