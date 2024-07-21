function request(req) {
    window.mcefQuery({
        "request": "run",
        "persistent": false,
        "onSuccess": function (msg) {
            return msg;
        },

        "onFailure": function (err, msg) {
            return msg;
        }
    });
}