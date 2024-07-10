/**
 * 대회경기영상
 */
(function(factory) { 'use strict'; factory($, apiHelper) }(function factory($, _api){
    'use strict';
    var type = "G08"
    _api.mst[type] = {
        codes: {
        },
        steps: [
            {
                sender: "bot",
                msg: "대회경기영상이 궁금하시군요. \n" +
                    "아래에서 대회년도 및 대회명을 입력해주세요.",
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
                onDraw: function(data) {
                    console.log(data)
                },
                next: function(data) {
                    return _api.setData(data, function(def){
                        if (!data.store.searchStartDt) {
                            alert('연도를 입력하세요.')
                            def.reject()
                        }else {
                            def.resolve()
                        }
                    });
                }
            },
            {
                sender: "user",
                msg: "<b>[대회경기영상]</b>\n" +
                    "년도: {{store.searchStartDt}}\n" +
                    "{{#if store.searchTitle}}"+
                        "대회명: {{store.searchTitle}}" +
                    "{{/if}}",
                skip: true,
            },
            {
                sender: "bot",
                msg: "대회경기영상 조회 결과에요.",
                template: type+"-T2",
                prepare: function(data) {
                    return _api.getData({
                        "apiType": "G08",
                        "regYear": data.store.searchStartDt,
                        "eventNm": data.store.searchTitle
                    }, function(res) {
                        data.list = [];
                        if (!res || !res.DATA || !res.DATA['VOD']) {
                            data.isReplace = false//true
                        } else {
                            data.list = data.list.concat(res.DATA['VOD'])
                        }
                    });
                },
                onDraw: function (data) {
                    var sectionId = data.stId+'-'+data.uuid;
                    chkHasSlider(sectionId + ' .video', data.list.length);
                }
            },
            {
                sender: "user",
                msg: "<b>[대회경기영상]</b>\n" +
                    "조건변경",
                skip: true,
            }
        ]
    }
}));