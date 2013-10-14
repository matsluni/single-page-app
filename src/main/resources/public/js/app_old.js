QuoteNameModel = Backbone.Model.extend({
    url: "/api/quoteName"
});
FullQuoteDataModel = Backbone.Model.extend({
    url: "/api/fullQuotes"
});

QuoteNameCollection = Backbone.Collection.extend({
    model: QuoteNameModel,
    url: "/api/quoteNames"
});

AppRouter = Backbone.Router.extend({
    routes: {
        "home":     "home",
        "quotes":   "quotes",
        "add":      "addQuotes"
    },
    initialize: function () {
        navView = new NavView();
        $('#nav').html(navView.render().el);
    },

    home: function () {
        var homeView = new HomeView();
        $('#content').html(homeView.render().el);
    },
    quotes: function () {
        this.quotesView = new QuotesView();
        $('#content').html(this.quotesView.render().el);
    },
    addQuotes: function(){
        this.addQuoteView = new AddQuoteView();
        $('#content').html(this.addQuoteView.render().el);
    }
});

QuotesView = Backbone.View.extend({

    initialize:function () {
        this.quoteNames = new QuoteNameCollection();
        this.template = Handlebars.compile( $("#stocks_template").html() );
        this.quoteNames.on('reset', this.render, this);
    },

    events: {
        "click a": "showDetail"
    },

    showDetail: function(e) {
        e.preventDefault();
        var quoteName = $(e.currentTarget).data("id");
//        $('#stock_detail').html("hallo from function with: " + id);
        var quoteData = new FullQuoteDataModel({id:quoteName})
        Highcharts.setOptions({
            global: {
                useUTC: false
            }
        });
        $.getJSON('api/fullQuotes/' + quoteName, function(data) {
            var chart = new Highcharts.StockChart({
                chart : {
                    renderTo : 'stock_detail'
                },

                rangeSelector : {
                    selected : 5
                },

                title : {
                    text : data.name + ' Stock History'
                },

                series : [{
                    name : quoteName,
                    data: data.prices
                }]
            });
        });
    },

    render: function(){
        var self = this;

        // fetch, when that is done, replace 'Loading' with content
        this.quoteNames.fetch().done(function(){
            var renderedContent = self.template(self.quoteNames.toJSON()[0]);
            self.$el.html(renderedContent);
        });
        return this;
    }
});

AddQuoteView = Backbone.View.extend({

    initialize: function(){
        this.template = Handlebars.compile( $("#addQuote_template").html() );
    },

    events: {
        "click #submit": "register"
    },

    register: function() {
        var quoteName =  $('input[name=quote]').val();
        var quote = new QuoteNameModel()
        quote.save({name: quoteName})
    },

    render: function(){
        var self = this;
        self.$el.html(self.template);
        return this;
    }
});

NavView = Backbone.View.extend({

    initialize: function(){
        this.template = Handlebars.compile( $("#nav_template").html() );
    },

    render: function(){
        var self = this;
        self.$el.html(self.template);
        return this;
    }
});
HomeView = Backbone.View.extend({

    initialize: function(){
        this.template = Handlebars.compile( $("#home_template").html() );
    },

    render: function(){
        var self = this;
        self.$el.html(self.template);
        return this;
    }
});

$(document).on("ready", function () {
    app = new AppRouter();
    Backbone.history.start();
});