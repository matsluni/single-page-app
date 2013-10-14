define(function(require) {

    "use strict";

    var Backbone = require('backbone');
    FullQuoteDataModel = Backbone.Model.extend({
        url: "/api/fullQuotes"
    });

    return {
        Model: FullQuoteDataModel
    };
});