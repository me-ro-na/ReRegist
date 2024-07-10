/**
 * 기타 신청이력
 */
(function(factory) { 'use strict'; factory($, apiHelper) }(function factory($, _api){
    'use strict';
    var type = "G04"
    _api.mst[type] = {
        codes: {
            "gubun": _api.defCode.status,
        },
        steps: [
            {
                sender: "bot",
                msg: "기타 신청이력이 궁금하시군요.\n" +
                    "아래에서 신청년도를 선택해주세요",
                template: type+"-T1",
                prepare: function(data) {
                    return _api.defer(function(res) {
                        var curYear = (new Date()).getFullYear();
                        var joinYear = curYear - 20; // res로 변경
                        for (var year = curYear; year >= joinYear; year--) {
                            _api.addCode(data, 'years', [{value:year, label:year+'년'}])
                        }
                    });
                },
                next: function(data) {
                    return _api.setData(data, function(def){ def.resolve() });
                }
            },
            {
                sender: "user",
                msg: "<b>[기타 신청이력]</b>\n" +
                    "연도: {{store.year}}\n",
                skip: true,
            },
            {
                sender: "bot",
                msg: "{{store.year}}년 강습회 신청 이력이에요",
                template: type+"-T2",
                prepare: function(data) {
                    return _api.getData({
                        "apiType": "G04",
                        "regYear": data.store.year,
                    }, function(res) {
                        data.list = []
                        if (res && res.DATA && res.DATA["PORTAL"]) {
                            data.list = [].concat(res.DATA["PORTAL"])
                        }
                    });
                }
            },
            {
                sender: "user",
                msg: "<b>[기타 신청이력]</b>\n" +
                    "조건변경",
                skip: true,
            }
        ]
    }
}));