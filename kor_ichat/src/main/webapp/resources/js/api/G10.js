/**
 * 대한체육회 교육이력
 */
(function(factory) { 'use strict'; factory($, apiHelper) }(function factory($, _api){
    'use strict';
    var type = "G10"
    _api.mst[type] = {
        codes: {
        },
        steps: [
            {
                sender: "bot",
                msg: "대한체육회 교육이력에 대해서 안내해드릴게요 ",
                template: type,
                prepare: function(data) {
                    return _api.getData({
                        "apiType": "G10",
                    }, function(res) {
                        data.list = []
                        if (res && res.DATA && res.DATA["EDU"]) {
                            data.list = [].concat(res.DATA["EDU"])
                        }
                    });
                }
            }
        ]
    }
}));