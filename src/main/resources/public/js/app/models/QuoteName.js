define(function(require) {

    "use strict";

    var Backbone = require('backbone');

    var QuoteNameModel = Backbone.Model.extend({
        url: "/api/quoteName"
    });

    var QuoteNameCollection = Backbone.Collection.extend({
        model: QuoteNameModel,
        url: "/api/quoteNames"
    });

    return {
        Model: QuoteNameModel,
        Collection: QuoteNameCollection
    };
});