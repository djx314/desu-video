$(function() {

    var ViewModel = function() {
        var self = this;
        self.parentUrl = ko.observable(null);
        self.filesPath = ko.observableArray([]);
        self.pathToSubmit = ko.observable(null);

        self.gotoParent = function() {
            $.ajax({
                type: "POST",
                url: "/filesWithAss",
                dataType: "json",
                data: { path: self.parentUrl() }
            }).done(function(response) {
                viewModel.parentUrl(response.parentPath);
                viewModel.filesPath(response.urls);
            });
        };

        self.enterFile = function(filePath) {
            if (filePath.isDir === true) {
                $.ajax({
                    type: "POST",
                    url: "/filesWithAss",
                    dataType: "json",
                    data: { path: filePath.encodeUrl }
                }).done(function(response) {
                    viewModel.parentUrl(response.parentPath);
                    viewModel.filesPath(response.urls);
                });
            } else {
                self.selectScale(filePath);
            }
        };

        self.selectScale = function(filePath) {
            self.pathToSubmit(filePath.encodeUrl);
            $('#ass-prompt').modal({
                relatedTarget: this
            });
            /*if (confirm("是否发送带字幕转码命令？") === true) {
                $.ajax({
                    type: "POST",
                    url: "/encodeWithAss",
                    data: { assPath: filePath.encodeUrl, videoPath : fileUrl }
                }).done(function(response) {
                    alert(response);
                    if (initParentUrl === "") {
                        window.location.href = "/assets";
                    } else {
                        window.location.href = "/assets/" + initParentUrl;
                    }
                });
            }*/
        };

        self.encodeFile = function(scale) {
            $.ajax({
                type: "POST",
                url: "/encodeWithAss",
                data: { assPath: self.pathToSubmit(), assScale: scale, videoPath: fileUrl }
            }).done(function(response) {
                alert(response);
                if (initParentUrl === "") {
                    window.location.href = "/assets";
                } else {
                    window.location.href = "/assets/" + initParentUrl;
                }
            });
        };
    };

    var viewModel = new ViewModel();

    ko.applyBindings(viewModel);

    $.ajax({
        type: "POST",
        url: "/filesWithAss",
        dataType: "json",
        data: { path: initParentUrl }
    }).done(function(response) {
        viewModel.parentUrl(response.parentPath);
        viewModel.filesPath(response.urls);
    });

});