/**
 * 선수 실적
 */
(function(factory) { 'use strict'; factory($, apiHelper) }(function factory($, _api){
    'use strict';
    var type = "G07"
    _api.mst[type] = {
        steps: [
            {
                sender: "bot",
                template: type+"-T1",
            }
        ]
    }
}));