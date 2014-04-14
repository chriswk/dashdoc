var engine = new Bloodhound({
    name: 'classnames',
    remote: {
        url: jsRoutes.controllers.Search.searchForClassName().url +"?name=%QUERY",
        filter: function (response) {
            return response.map(function(e) {
                return { value: e };
            });
        }
    },
    datumTokenizer: function(datum) {
        console.log(d);
        return Bloodhound.tokenizers.obj.whitespace('value');
    },
    queryTokenizer: Bloodhound.tokenizers.whitespace
});
engine.initialize();
$("#typeahead .typeahead").typeahead(null, {
    hint: true,
    name: 'classnames',
    highlight: true,
    minLength: 1,
    source: engine.ttAdapter()
});