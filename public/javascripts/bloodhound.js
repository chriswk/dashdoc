var engine = new Bloodhound({
    name: 'classnames',
    remote: {
        url: jsRoutes.controllers.Search.searchForClassName().url +"?name=%QUERY",
        filter: function (response) {
            console.log(response);
            return response.hits.hits.map(function(e) {
                return e._source
            });
        }
    },
    datumTokenizer: function(datum) {
        console.log(d);
        return Bloodhound.tokenizers.obj.whitespace('className');
    },
    queryTokenizer: Bloodhound.tokenizers.whitespace
});
engine.initialize();
$("#typeahead .typeahead").typeahead(null, {
    hint: true,
    name: 'classnames',
    highlight: true,
    minLength: 1,
    source: engine.ttAdapter(),
    displayKey: "className",
    templates: {
        empty: [
            '<div class="empty-message">',
            'unable to find any class matching the current query',
            '</div>'
        ].join("\n"),
        suggestion: Handlebars.compile('<p><strong>{{className}}</strong> - {{absolute}}</p>')
    }
});