/**
 * 증명서 발급신청이력
 */
(function(factory) { 'use strict'; factory($, apiHelper) }(function factory($, _api){
    'use strict';
    var type = "G03"
    _api.mst[type] = {
        codes: {
            "gubun": _api.defCode.status,
        },
        steps: [
            {
                sender: "bot",
                msg: "증명서 발급신청이력이 궁금하시군요. \n" +
                    "아래에서 신청상태 및 신청기간을 선택해주세요\n",
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
                msg: "<b>[증명서 발급신청이력]</b>\n" +
                    "상태: {{store.gubunNm}}\n" +
                    "신청 기간: {{store.searchStartDt}} ~ {{store.searchEndDt}}",
                skip: true,
            },
            {
                sender: "bot",
                msg: "증명서 발급 신청 이력이에요",
                template: type+"-T2",
                prepare: function(data) {
                    return _api.getData({
                        "apiType": "G03",
                        "gubun": data.store.gubun,
                        "startDt": data.store.searchStartDt.replaceAll('\.',''),
                        "endDt": data.store.searchEndDt.replaceAll('\.',''),
                    }, function(res) {
                        data.list = []
                        if (res && res.DATA && res.DATA["CERT"]) {
                            data.list = [].concat(res.DATA["CERT"]).filter(function (d) {
                                if (d['BC_YN'] === "Y" && $('#channel').val() === "MO") {
                                    return false;
                                }
                                return true;
                            })
                        }
                    });
                }
            },
            {
                sender: "user",
                msg: "<b>[증명서 발급신청이력]</b>\n" +
                    "조건변경",
                skip: true,
            }
        ]
    }
}));