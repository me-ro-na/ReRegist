/**
 * 경기인 신청이력
 */
(function(factory) { 'use strict'; factory($, apiHelper, _) }(function factory($, _api, _){
    'use strict';
    var type = "G01"
    _api.mst[type] = {
        codes: {
            "gubun": _api.defCode.gubun
        },
        steps: [
            {
                sender: "bot",
                msg: "경기인 신청이력이 궁금하시군요. \n" +
                    "아래에서 등록신청년도 및 경기인 구분을 선택해주세요 \n",
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
                msg: "<b>[경기인 신청이력]</b>\n" +
                    "연도: {{store.year}}\n" +
                    "구분: {{store.gubunNm}}",
                skip: true,
            },
            {
                sender: "bot",
                msg: "선수등록 신청이력이에요",
                template: type+"-T2",
                prepare: function(data) {
                    return _api.getData({
                        "apiType": "G01",
                        "regYear": data.store.year
                    }, function(res) {
                        data.list = [];
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
                        //_api[type].setPageSet('def', data.list);
                    });
                },
            },
            {
                sender: "user",
                msg: "<b>[경기인 신청이력]</b>\n" +
                    "조건변경",
                skip: true,
            }
        ]
    }
}));