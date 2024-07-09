/**
 * 훈포장 이력
 */
(function(factory) { 'use strict'; factory($, apiHelper) }(function factory($, _api){
    'use strict';
    var type = "G02"
    _api.mst[type] = {
        codes: {},
        steps: [
            {
                sender: "bot",
                msg: "훈·포장 신청내역이에요.",
                template: type,
                prepare: function(data) {
                    return _api.getData({
                        "apiType": "G02",
                    }, function(res) {
                        data.list = [];
                        if (!res || !res.DATA || !res.DATA.HONOR) {
                            data.isReplace = true
                        } else {
                            data.list = data.list.concat(res.DATA.HONOR)
                        }
                    });
                }
            }
        ]
    }
}));