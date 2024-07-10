/**
 * 소속변경(이적) 신청 조회
 */
(function(factory) { 'use strict'; factory($, apiHelper) }(function factory($, _api){
    'use strict';
    var type = "G11"
    _api.mst[type] = {
        codes: {
        },
        steps: [
            {
                sender: "bot",
                msg: "소속변경(이적) 신청 조회 결과에요\n",
                template: type,
                prepare: function(data) {
                    return _api.getData({
                        "apiType": "G11",
                    }, function(res) {
                        data.list = []
                        if (res && res.DATA && res.DATA["TRANSFER"]) {
                            data.transfer = [].concat(res.DATA["TRANSFER"])
                        }
                    });
                    return _api.getData("/test",{
                        param: 'test'
                    }, function(res) {
                        data.transfer = [
                            {
                                "req_dt":           "20231227131107",
                                "state":            "회원종목단체 승인대기",
                                "cur_team_nm":      "AAA",
                                "cur_team_code":    "AA00000",
                                "cur_sports":       "실업(일반)",
                                "cur_sido":         "서울",
                                "new_team_nm":      "BBBBB",
                                "new_team_code":    "BB00000",
                                "new_sports":       "실업(일반)",
                                "new_sido":         "전주",
                            },
                            {
                                "req_dt":           "20231227131107",
                                "state":            "회원종목단체 승인대기",
                                "cur_team_nm":      "AAA",
                                "cur_team_code":    "AA00000",
                                "cur_sports":       "실업(일반)",
                                "cur_sido":         "서울",
                                "new_team_nm":      "BBBBB",
                                "new_team_code":    "BB00000",
                                "new_sports":       "실업(일반)",
                                "new_sido":         "전주",
                            },
                        ]
                    });
                }
            }
        ]
    }
}));