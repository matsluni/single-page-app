require.config({
    //By default load any module IDs from js/lib
    baseUrl: 'public/js/lib',
    paths: {
        app: '../app',
        tpl: '../tpl',
        jquery: 'jquery-2.0.3'
    },
    shim: {
        'bootstrap': {
            deps: ['jquery'],
            exports: 'bootstrap'
        },
        'handlebars': {
            exports: 'Handlebars'
        },
        'underscore': {
            exports: '_'
        },
        'backbone': {
            deps: ['underscore', 'jquery'],
            exports: 'Backbone'
        },
    }

});

// Start the main app logic.
require(['jquery', 'backbone', 'bootstrap', 'app/router'], function($, Backbone, Bootstrap, Router) {

    var router = new Router();
    Backbone.history.start();

});
