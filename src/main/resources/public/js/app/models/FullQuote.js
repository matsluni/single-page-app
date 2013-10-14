define(function(require) {

    "use strict";

    var Backbone = require('backbone');
    var FullQuoteDataModel = Backbone.Model.extend({
        url: "/api/fullQuotes"
    });

    return {
        Model: FullQuoteDataModel
    };
});