define(function(require) {

    "use strict";

    var Backbone = require('backbone');

    QuoteNameModel = Backbone.Model.extend({
        url: "/api/quoteName"
    });

    QuoteNameCollection = Backbone.Collection.extend({
        model: QuoteNameModel,
        url: "/api/quoteNames"
    });

    return {
        Model: QuoteNameModel,
        Collection: QuoteNameCollection
    };
});