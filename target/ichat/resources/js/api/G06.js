/**
 * 대표선수·후보선수 훈련이력
 */
(function(factory) { 'use strict'; factory($, apiHelper) }(function factory($, _api){
    'use strict';
    var type = "G06"
    _api.mst[type] = {
        codes: {
            "gubun": [
                {'value': '', 'label': '전체'},
                {'value': '1', 'label': '대표선수'},
                {'value': '2', 'label': '후보선수'},
                {'value': 'N', 'label': '청소년대표'},
                {'value': '4', 'label': '꿈나무선수'},
            ],
        },
        steps: [
            {
                sender: "bot",
                msg: "대표선수·후보선수 훈련이력이 궁금하시군요.\n" +
                    "아래에서 등록구분 및 훈련기간을 선택해주세요",
                template: type+"-T1",
                prepare: function(data) {
                    return _api.defer(function(res) { });
                },
                date_range: [{"stNm":"searchStartDt", "edNm":"searchEndDt"}],
                onDraw: function(data) {
                },
                next: function(data) {
                    return _api.setData(data, function(def){ def.resolve() });
                }
            },
            {
                sender: "user",
                msg: "<b>[대표선수·후보선수 훈련이력]</b>\n" +
                    "등록 구분: {{store.gubunNm}}\n" +
                    "훈련 기간: {{store.searchStartDt}} ~ {{store.searchEndDt}}",
                skip: true,
            },
            {
                sender: "bot",
                msg: "대표선수·후보선수 훈련이력이에요",
                template: type+"-T2",
                prepare: function(data) {
                    return _api.getData({
                        "apiType": "G06",
                        "gubun": data.store.gubun,
                        "startDt": data.store.searchStartDt.replaceAll('\.',''),
                        "endDt": data.store.searchEndDt.replaceAll('\.',''),
                    }, function(res) {
                        data.list = []
                        if (res && res.DATA && res.DATA["NAT"]) {
                            data.list = [].concat(res.DATA["NAT"])
                        }
                    });
                }
            },
            {
                sender: "user",
                msg: "<b>[대표선수·후보선수 훈련이력]</b>\n" +
                    "조건변경",
                skip: true,
            }
        ]
    }
}));