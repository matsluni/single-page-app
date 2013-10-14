define(function(require) {

    "use strict";

    var $ = require('jquery');
    var Backbone = require('backbone');
    
    var Handlebars = require('handlebars');
    var tpl = require('text!tpl/Home.html');
    var template = Handlebars.compile(tpl);

    return Backbone.View.extend({
        initialize: function() {
        },
        render: function() {
            this.$el.html(template());
            return this;
        }
    });
});