function mapData(data) {
    return data.hits.hits.map(function(el) {
        return {
            "id": el._id,
            "gav": el._source.gav,
            "classname": el._source.className,
            "absolute" : el._source.absolute
        };
    });
}
function createData(data) {
    console.log(data);
    var data = {
        "aaData": mapData(data),
        "iTotalRecords": data.hits.total,
        "iTotalDisplayRecords": data.hits.total
    };
    return data;
}
$(document).ready(function () {
    $("#classTable").dataTable({
        "bProcessing": true,
        "sAjaxSource": jsRoutes.controllers.Search.browseJson().url,
        "bServerSide": true,
        "fnServerData": function ( sSource, aoData, fnCallback, oSettings) {
            $.ajax({
                "dataType" : "json",
                "type": "GET",
                "url": sSource,
                "data" : aoData,
                "success": function (data, textStatus, jqXHR) {
                    fnCallback(createData(data));
                }
            });
        },
        "aoColumns": [
            { "mData": "id"},
            { "mData": "gav"},
            { "mData": "classname"},
            { "mData": "absolute"}
        ]
    });
});