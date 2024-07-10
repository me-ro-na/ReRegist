/**
 * 경기인 등록이력
 */
(function(factory) { 'use strict'; factory($, apiHelper) }(function factory($, _api){
    'use strict';
    var type = "G05"
    _api.mst[type] = {
        codes: {
            "gubun": _api.defCode.gubun,
        },
        steps: [
            {
                sender: "bot",
                msg: "경기인 등록이력이 궁금하시군요. \n" +
                    "아래에서 등록구분 및 등록년도를 선택해주세요.",
                template: type+"-T1",
                prepare: function(data) {
                    return _api.defer(function(res) {
                        var curYear = (new Date()).getFullYear();
                        var joinYear = curYear - 20; // res로 변경
                        for (var year = curYear; year >= joinYear; year--) {
                            _api.addCode(data, 'years', [{value:year, label:year/*+'년'*/}])
                        }
                    });
                },
                next: function(data) {
                    return _api.setData(data, function(def){ def.resolve() });
                }
            },
            {
                sender: "user",
                msg: "<b>[경기인 등록이력]</b>\n" +
                    "구분: {{store.gubunNm}}\n" +
                    "기간: {{store.searchStartDt}}년 ~ {{store.searchEndDt}}년\n",
                skip: true,
            },
            {
                sender: "bot",
                msg: "경기인 등록이력이에요",
                template: type+"-T2",
                prepare: function(data) {
                    return _api.getData({
                        "apiType": "G05",
                        "gubun": data.store.gubun,
                        "startYear": data.store.searchStartDt,
                        "endYear": data.store.searchEndDt,
                    }, function(res) {
                        data.list = []
                        if (res && res.DATA) {
                            var gubunKeys = {
                                "P": "PLAYER",
                                "O": "OFFICER",
                                "R": "REFEREE",
                                "M": "MANAGER",
                            }
                            $.each(Object.keys(gubunKeys), function(i, gubun) {
                                if (!data.store.gubun || data.store.gubun === gubun) {
                                    var gubunKey = gubunKeys[gubun];
                                    if (res.DATA.hasOwnProperty(gubunKey) && res.DATA[gubunKey]) {
                                        res.DATA[gubunKey] = [].concat(res.DATA[gubunKey])
                                        res.DATA[gubunKey].forEach(function (d, i) {
                                            res.DATA[gubunKey][i].GUBUN_NM = {
                                                "P": "선수",
                                                "O": "지도자",
                                                "R": "심판",
                                                "M": "선수관리담당자",
                                            }[gubun]
                                        })
                                        data.list = data.list.concat(res.DATA[gubunKey]);
                                    }
                                }
                            })
                        }
                        _api[type].setPageSet('def', data.list);
                    });
                }
            },
            {
                sender: "user",
                msg: "<b>[경기인 등록이력]</b>\n" +
                    "조건변경",
                skip: true,
            }
        ]
    }
}));