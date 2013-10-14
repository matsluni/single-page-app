define(function(require) {

    "use strict";

    var Backbone = require('backbone');
    var Handlebars = require('handlebars');
    var tpl = require('text!tpl/Test.html');
    var template = Handlebars.compile(tpl);

    var View = Backbone.View.extend({
//        el: 'body', //success
//        el: 'div',
//        tagName: 'li',
        id: 'openEssay',
        initialize: function() {
            _.bindAll(this, 'openEssay');
        },
        events: {
            'click button#openEssay': 'openEssay'
        },
        openEssay: function() {
            alert('a');
        },
        render: function() {
//            console.log(this.$el);
            $(this.el)
                    .html(template());
            $('#section')
                    .html(template());

            return this;
        }
    });


    return {
        View: View
    };
});