define(function(require) {
    var $ = require("jquery");
    var ko = require("knockout");

    $(function() {

        var ViewModel = function() {
            var self = this;
            self.parentUrl = ko.observable(null);
            self.deleteTempUrl = ko.observable(null);
            self.filesPath = ko.observableArray([]);

            self.deleteTempDir = function() {
                $.ajax({
                    type: "POST",
                    url: "/deleteTempDir",
                    data: { path: currentUrl }
                }).done(function(response) {
                    location.reload(true);
                });
            };

            self.encodeFile = function(filePath) {
                if (confirm("是否转码文件：" + filePath.fileName + "？") === true) {
                    //if (confirm("是否附带字幕文件？") === false) {
                    $.ajax({
                        type: "POST",
                        url: "/encode",
                        data: { path: filePath.encodeUrl }
                    }).done(function(response) {
                        alert(response);
                    });
                    /*} else {
                        window.location.href = "/withAss?path=" + encodeURIComponent(filePath.encodeUrl);
                    }*/
                }
            };
        };

        var viewModel = new ViewModel();

        ko.applyBindings(viewModel);

        $.ajax({
            type: "POST",
            url: "/dirInfoRequest",
            dataType: "json",
            data: { path: currentUrl }
        }).done(function(response) {
            viewModel.parentUrl(response.parentPath);
            viewModel.filesPath(response.urls);
            viewModel.deleteTempUrl(response.deleteTempUrl);
        });

    });
});