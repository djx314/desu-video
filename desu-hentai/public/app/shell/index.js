define(function (require) {
    var router = require("plugins/router");

    return {
        router: router,
        activate: function() {
            router.map([
                { route: "assets", moduleId: "lifan/files/index", title: "里番首页", nav: 3, hash: "#assets" },
                { route: "picture(/*details)", moduleId: "lifan/picture/index", title: "图片浏览", nav: 3, hash: "#picture" }
            ]).buildNavigationModel();
            return router.activate();
        }
    };
});