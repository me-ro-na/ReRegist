/**
 * 온라인 교육 이력
 */
(function(factory) { 'use strict'; factory($, apiHelper) }(function factory($, _api){
    'use strict';
    var type = "G09"
    _api.mst[type] = {
        codes: {
        },
        steps: [
            {
                sender: "bot",
                msg: "온라인 교육 수료현황에 대해 안내해드릴게요\n" +
                    "온라인 교육 수료가 완료되지 않으신 경우 교육 수료하기 버튼을 클릭하시면 온라인 교육 페이지로 이동하실 수 있어요\n",
                template: type,
                prepare: function(data) {
                    return _api.getData({
                        "apiType": "G09",
                    }, function(res) {
                        data.store = $.extend(data.store, {
                            "rights": {"link": "https://edu.k-sec.or.kr/"},
                            "doping": {"link": "https://edu.kada-ad.or.kr/"}
                        });
                        if (res && res.DATA) {
                            if (res.DATA["EDU"] && "Y" === res.DATA["EDU"]["EDU_YN"]) {
                                data.store = $.extend(data.store, {
                                    "doping": {
                                        "link": undefined,
                                        "comp_yn": res.DATA["EDU"]["EDU_YN"],
                                        "comp_dt": res.DATA["EDU"]["EDU_DT"]
                                    }
                                });
                            }
                            if (res.DATA["KADA"] && "Y" === res.DATA["KADA"]["EDU_YN"]) {
                                data.store = $.extend(data.store, {
                                    "doping": {
                                        "link": undefined,
                                        "comp_yn": res.DATA["KADA"]["EDU_YN"],
                                        "comp_dt": res.DATA["KADA"]["EDU_DT"]
                                    }
                                });
                            }
                        }
                    });
                }
            }
        ]
    }
}));