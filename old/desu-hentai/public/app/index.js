define(function(require) {
    var system = require("durandal/system"),
        app = require("durandal/app");
    system.debug(true);
    app.title = "喵喵酱色色的地方";
    app.configurePlugins({
        router: true,
        dialog: true
    });
    app.start().then(function() {
        app.setRoot("lifan/shell/index");
    });
});