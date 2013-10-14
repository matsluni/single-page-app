define(function(require) {

    "use strict";

    var Backbone = require('backbone');
    var Handlebars = require('handlebars');
    var tpl = require('text!tpl/Home.html');
    var template = Handlebars.compile(tpl);

    var View = Backbone.View.extend({
//        el: 'body', //success
        el: 'div',
//        tagName: 'li',
//        id:'div',
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
            console.log(this.$el);

            $('#section')
                    .html("<div><button id='openEssay'>test</button></div>");
            return this;
        }
    });


    return {
        View: View
    };
});