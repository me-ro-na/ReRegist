(function(factory) { 'use strict'; factory(jQuery, window._sportsApi); }(function factory($, sapi){
    'use strict';

    var ANIMATE_OPT = {duration: 300, queue: false};
    var THROTTLE_TICK = 100;

    var root = (typeof self == 'object' && self && self.Object === Object && self) || Function('return this')();

    function getDpFn(prev, next) {
        return function lrDp(isPrev, isNext) {
            prev.style.display = isPrev?'':'none'
            next.style.display = isNext?'':'none'
        }
    }

    $(window).on('resize', _.throttle(function () {
        $('.tbl_btn_group .btn.next').each(function (i, c) {
            var parent = $(c).parents('.tbl_box_wrap').get(0)
            var table = $(parent).find('table').get(0)
            var next = $(parent).find('.tbl_btn_group .btn.next').get(0)
            var prev = $(parent).find('.tbl_btn_group .btn.prev').get(0)

            var lrDp = getDpFn(prev, next);
            if (parent.clientWidth >= table.clientWidth) {
                lrDp(false, false);
            } else {
                if ($(parent).scrollLeft() === 0) {
                    lrDp(false, true);
                } else {
                    lrDp(true, false);
                }
            }
        })
    }, THROTTLE_TICK));

    /**
     * 리스트가 배열이면서 비어있지 않을 경우
     */
    Handlebars.registerHelper('exist', function (list, options) {
        if (Array.isArray(list) && list.length > 0) {
            return options.fn(this);
        }
        return options.inverse(this);
    });
    /**
     * 오른쪽 숫자에서 +1 하여 반환
     */
    Handlebars.registerHelper('inc', function (num) {
        return num + 1;
    });
    /**
     * 입력 문자열이 없을 경우 대시 표시
     */
    Handlebars.registerHelper('dash', function (val) {
        if (val === null || val === undefined || (typeof val === "string" && val.trim().length === 0)) {
            return '-'
        }
        return val;
    });
    /**
     * 입력 문자열이 숫자일 경우 세자리수 구분
     */
    Handlebars.registerHelper('comma', function (num) {
        if (!isNaN(num)) {
            if (!num) { return '-' }
            return String(num).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
        }
        return num;
    });
    /**
     * 입력 문자열이 yyyyMMdd
     */
    Handlebars.registerHelper('yyyyMMdd', function (num) {
        num = String(num).replace(/[^0-9]+/gi, '')
        if (!isNaN(num)) {
            if (!num) { return '-' }
            if (num.length === 7) {
                return String(num).replace(/^(\d{4})(\d{1})(\d{2})$/, '$1.0$2.$3');
            }
            return String(num).replace(/^(\d{4})(\d{2})(\d{2})$/, '$1.$2.$3');
        }
        return num;
    });
    /**
     * 입력 문자열이 yyyyMMddHHmmss
     */
    Handlebars.registerHelper('yyyyMMddHHmmss', function (num) {
        num = String(num).replace(/[^0-9]+/, '')
        if (!isNaN(num)) {
            if (!num) { return '-' }
            return String(num).replace(/^(\d{4})(\d{2})(\d{2})(\d{2})(\d{2})(\d{2})$/, '$1.$2.$3 $4:$5:$6');
        }
        return num;
    });
    Handlebars.registerHelper('ifCond', function (v1, operator, v2, options) {
        switch (operator) {
            case '==':
                return (v1 === v2) ? options.fn(this) : options.inverse(this);
            case '===':
                return (v1 === v2) ? options.fn(this) : options.inverse(this);
            case '!=':
                return (v1 !== v2) ? options.fn(this) : options.inverse(this);
            case '!==':
                return (v1 !== v2) ? options.fn(this) : options.inverse(this);
            case '<':
                return (v1 < v2) ? options.fn(this) : options.inverse(this);
            case '<=':
                return (v1 <= v2) ? options.fn(this) : options.inverse(this);
            case '>':
                return (v1 > v2) ? options.fn(this) : options.inverse(this);
            case '>=':
                return (v1 >= v2) ? options.fn(this) : options.inverse(this);
            case '&&':
                return (v1 && v2) ? options.fn(this) : options.inverse(this);
            case '||':
                return (v1 || v2) ? options.fn(this) : options.inverse(this);
            default:
                return options.inverse(this);
        }
    });
    /**
     * 페이징 처리
     */
    Handlebars.registerHelper('pagination', function(currentPage, totalPage, size, options) {
        var startPage, endPage, context;

        if (arguments.length === 3) {
            options = size;
            size = 5;
        }

        startPage = currentPage - Math.floor(size / 2);
        endPage = currentPage + Math.floor(size / 2);

        if (startPage <= 0) {
            endPage -= (startPage - 1);
            startPage = 1;
        }

        if (endPage > totalPage) {
            endPage = totalPage;
            if (endPage - size + 1 > 0) {
                startPage = endPage - size + 1;
            } else {
                startPage = 1;
            }
        }

        context = {
            startFromFirstPage: false,
            pages: [],
            endAtLastPage: false,
        };
        if (startPage === 1) {
            context.startFromFirstPage = true;
        }
        for (var i = startPage; i <= endPage; i++) {
            context.pages.push({
                page: i,
                isCurrent: i === currentPage,
            });
        }
        if (endPage === totalPage) {
            context.endAtLastPage = true;
        }

        return options.fn(context);
    });

    root.pageFn = {
        getApi: function(cur) {
            var form = cur.closest('section.bot_form');
            var type = form.id.split('-')[0];
            return apiHelper[type]
        },
        getPageInfo: function(cur) {
            var curData = this.getApi(cur).data;
            if (curData.hasOwnProperty('pageSet')) {
                var setNm = cur.closest('div.pagination').dataset.set_nm;
                return curData.pageSet[setNm];
            }
            return null;
        },
        doRedraw: function(cur) {
            var curApi = this.getApi(cur);
            var curData = this.getApi(cur).data;
            curApi.step().prepare(curApi.data).done(function () {
                getHtml(curApi.step().template).done(function (html) {
                    var template = Handlebars.compile(html);
                    var el = document.createElement('div');
                    el.innerHTML = template(curData)
                    var newHtml = el.querySelector('section.bot_form')
                    cur.closest('section.bot_form').innerHTML = newHtml.innerHTML;
                });
            })
        },
        prev: function(cur) {
            var info = this.getPageInfo(cur)
            if (info) {
                info.currentPage -= 1
                if (info.currentPage < 1) {
                    info.currentPage = 1;
                }
                this.doRedraw(cur);
            }
        },
        next: function(cur) {
            var info = this.getPageInfo(cur)
            if (info) {
                info.currentPage += 1
                if (info.currentPage > info.pageCount) {
                    info.currentPage = info.pageCount;
                }
                this.doRedraw(cur);
            }
        },
        more: function(cur) {
            var nodes = cur.parentElement.querySelectorAll('.hide')
            var moreCnt = Number(cur.dataset.more_cnt || 3);
            if (nodes.length <= moreCnt) {
                cur.style.display = 'none'
            }
            nodes.forEach(function (node, i) {
                if (i < moreCnt) {
                    $(node).removeClass('hide')
                }
            })
        },
        to: function(cur) {
            var info = this.getPageInfo(cur)
            if (info) {
                info.currentPage = Number(cur.dataset.page_to);
                //console.log(cur.dataset.page_to);
                this.doRedraw(cur);
            }
        }
    };

    /**
     * 해당 API의 템플릿을 가져오는 함수
     *
     * - Handlebars.js를 이용하기 위하여 사용
     * @param src
     * @returns {*|{}|jQuery}
     */
    function getHtml(src) {
        if (new RegExp(/^[a-zA-Z0-9_-]*$/).test(src)) {
            var deferred = $.Deferred();
            var htmlEl = document.querySelector('script#'+src)
            if (htmlEl) {
                deferred.resolve(htmlEl.innerHTML)
            } else {
                deferred.reject('none')
            }
            /*
            var xhr = new XMLHttpRequest();
            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                        var html = xhr.responseText;
                        deferred.resolve(html);
                    } else {
                        deferred.reject(xhr.status)
                    }
                }
            };
            xhr.open('GET', "./resources/api_html/" + src + ".html", true);
            xhr.send();
             */

            return deferred;
        }
    }

    /**
     * 해당 API의 자바스크립트를 가져오는 함수
     *
     * @param {string} type API ID
     * @param {function} callback 로드 후 수행 함수 - arg1: 첫수행 여부(bool) / arg2: param값
     */
    /*
    function attachJs(type, callback) {
        if (new RegExp(/^[a-zA-Z0-9_-]*$/).test(type)) {
            var id = "api_js_" + type;
            var script = document.querySelector("script#" + id);
            if (typeof callback !== 'function') {
                callback = function (){};
            }
            if (!script) {
                script = document.createElement("script");
                script.id = id
                script.type = "text/javascript";

                if (script.readyState) {  // IE specific
                    script.onreadystatechange = function () {
                        if (script.readyState === "loaded" || script.readyState === "complete") {
                            script.onreadystatechange = null;
                            callback(true);
                        }
                    };
                } else {  // Other browsers
                    script.onload = function () {
                        callback(true);
                    };
                }

                script.src = "./resources/js/api/" + type + ".js";
                document.head.appendChild(script);
            } else {
                callback(false);
            }
        } else {
            throw new Error("해당 API ID는 형식에 맞지 않습니다. (ID: "+String(type)+")");
        }
    }

    function detachJs(type, callback){
        var filename = "./resources/js/api/" + type + ".js";
        var allsuspects= document.getElementsByTagName("script")
        for (var i= allsuspects.length; i>=0; i--) {
            if (allsuspects[i] &&
                allsuspects[i].getAttribute("src")!=null &&
                allsuspects[i].getAttribute("src").indexOf(filename)!==-1)
                allsuspects[i].parentNode.removeChild(allsuspects[i])
        }
        if (typeof callback === 'function') {
            callback();
        }
    }
     */

    function now(adjDay) {
        return new Date(
            Number(new Date())
            - (new Date()).getTimezoneOffset() * 60 * 1000
            + (adjDay||0) * 24 * 60 * 60 * 1000
        ).toISOString()
            .split('.')[0]
            .replaceAll('-','.')
            .replaceAll('T',' ');
    }

    function uuidv4() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    }

    var apiHelper = {
        mst: {},
        move: function (flag, gubun) {
            sapi.moveSite(flag, gubun);
        },
        defCode: {
            gubun: [
                {'value': '', 'label': '전체'},
                {'value': 'P', 'label': '선수'},
                {'value': 'O', 'label': '지도자'},
                {'value': 'R', 'label': '심판'},
                {'value': 'M', 'label': '담당자'},
            ],
            status: [
                {'value': '', 'label': '전체'},
                {'value': '10', 'label': '결제대기'},
                {'value': '20', 'label': '결제취소'},
                {'value': '30', 'label': '승인대기'},
                {'value': '31', 'label': '승인완료'},
                {'value': '40', 'label': '발급완료'},
            ]
        },
        naming: function (store, customCodes) {
            var codes = $.extend({}, apiHelper.defCode, customCodes);
            $.each(Object.keys(codes), function(i, codeGrp) {
                if (store && store.hasOwnProperty(codeGrp) && !store.hasOwnProperty(codeGrp+'Nm')) {
                    var _f = _.find(codes[codeGrp], {'value': store[codeGrp]})
                    if (_f) {
                        store[codeGrp+'Nm'] = _f.label;
                    }
                }
            })
        },
        defer: function(callback) {
            var deferred = $.Deferred();
            setTimeout(function(){
                if (typeof callback === "function") {
                    callback();
                }
                deferred.resolve();
            })
            return deferred
        },
        getData: function (data, callback) {
            var deferred = $.Deferred();
            var isTest = false;
            if (arguments[0] === "/test") {
                isTest = true;
                if (arguments.length === 3) {
                    callback = arguments[2];
                }
                data = arguments[1]
            }
            setTimeout(function(){
                if (isTest) {
                    if (typeof callback === "function") {
                        callback(data);
                    }
                    deferred.resolve(data);
                } else {
                    sapi.getData(data, function (res) {
                        if (typeof callback === "function") {
                            callback(res);
                        }
                        deferred.resolve(res);
                    });
                }
            }, 100)
            return deferred
        },
        setData: function (data, callback) {
            var deferred = $.Deferred();
            data.store = $('form.api_form#f'+data.uuid).serializeObject();
            apiHelper.naming.apply(null, [data.store, data['codes']])
            setTimeout(function(){
                if (typeof callback === 'function') {
                    callback(deferred);
                } else {
                    deferred.resolve()
                }
            })
            return deferred
        },
        addCode: function(target, codeGrp, item) {
            if (!target.codes.hasOwnProperty(codeGrp)) {
                target.codes[codeGrp] = []
            }
            if (Array.isArray(item)) {
                item.forEach(function (code) {
                    target.codes[codeGrp].push(code)
                })
            } else if (item && item.hasOwnProperty('label') && item.hasOwnProperty('value')) {
                target.codes[codeGrp].push(item)
            }
        },
        code: function(codeNm, val, def) {
            var _f = _.find(apiHelper.defCode[codeNm], {'value': val})
            if (_f) {
                return _f.label;
            } else {
                return def;
            }
        },
        init: function(ty, msg, intentNm, isStated) {
            /*
            if (apiHelper.hasOwnProperty(ty) && apiHelper[ty].hasOwnProperty('destroy')) {
                apiHelper[ty].destroy();
                delete apiHelper[ty];
                detachJs(ty);
            }
            attachJs(ty, function () {*/
                apiHelper[ty] = apiHelper.Master(ty, apiHelper.mst[ty])
                if (msg) {
                    apiHelper.mst[ty].steps[0].msg = msg;
                }
                apiHelper.run(ty, intentNm, isStated)
            /*
            });
            */
        },
        run: function(ty, intentNm, isStated) {
            if (!apiHelper.hasOwnProperty(ty)) {
                apiHelper.init(ty)
            } else {
                apiHelper[ty].act()
                apiHelper[ty].data.intentNm = intentNm;
                apiHelper[ty].data.isStated = isStated;
            }
        },
        Master: function(type, arg) {
            var uuid = uuidv4();// crypto.randomUUID()
            var data = $.extend(
                {
                    store: {},
                    codes: {},
                    pageSet: []
                },
                arg,
                {
                    uuid: uuid,
                    type: type,
                    state: 0
                }
            );

            var _t = {
                data: data,
                step: function() {
                    return data.steps[data.state];
                },
                getStepId: function() {
                    return data.type + "-S" + data.state;
                },
                msg: function() {
                    var template = Handlebars.compile(_t.step().msg);
                    var msg = template(data)
                    msg += "<tag id='" + _t.getStepId() + '_' + data.uuid + "'>";
                    return msg.replaceAll("\n", "<br/>");
                },
                setPageSet: function(setNm, list) {
                    var len = list.length;
                    var curPage = 1;
                    if (data.pageSet[setNm]) {
                        curPage = data.pageSet[setNm].currentPage
                    }
                    var pageRows = 5;
                    data.pageSet[setNm] = {
                        list: list.filter(function (d, i) {
                            return curPage === Math.floor(i / pageRows) + 1
                        }),
                        currentPage: curPage,
                        pageCount: Math.ceil(len / pageRows)
                    }
                },
                act: function() {
                    data.stId = _t.getStepId();
                    if (_t.step().sender === "bot" && data.state !== 0) {
                        typingMotionShow();
                    }
                    if (_t.step().hasOwnProperty('prepare')) {
                        _t.step().prepare(data).done(function(store){
                            $.extend(data.store, store);
                            _t.action(data.store);
                        })
                    } else {
                        _t.action(data.store);
                    }
                },
                action: function() {
                    $('.bot_form:not(.test_box) *:not(.all)').attr('disabled', true).off()
                    console.log(_t.getStepId() + '_' + data.uuid)

                    function send(html) {
                        var tagSel = '"tag#' + _t.getStepId() + '_' + data.uuid + '"';
                        if (_t.step().sender === "bot") {
                            var delay = 1000;
                            if (true !== data.isStated) {
                                delay = 0;
                            }
                            setTimeout(function() {
                                typingMotionHide();
                                if (data.isReplace) {
                                    setAnswer(html, "[]", data.type)
                                } else {
                                    setAnswer(_t.msg(), "[]", data.type, )
                                    $('.bot_txt:has(' + tagSel + ') .chat_talk, .user_txt:has(' + tagSel + ')').append(html)
                                    if (data.state === 0 && true !== data.isStated) {
                                        setHelp(fontSize, data.intentNm);
                                    }
                                }
                                if (_t.step().hasOwnProperty('date_range')) {
                                    var p = "#f" + data.uuid;
                                    _t.step().date_range.forEach(function(range) {
                                        var sel = p + " [name="+range.stNm+"], " + p + " [name="+range.edNm+"]"
                                        $(sel).datepicker({
                                            /*minDate: "-5Y",*/ maxDate: "+0d",
                                            buttonImage: "./resources/img/ico_calendar.png",
                                            beforeShow: function (el, inst) {
                                                //console.log({'b': {"el": el, "inst": inst}})
                                            },
                                            onSelect: function(txt, inst) {
                                                console.log({'s': {"txt": txt, "inst": inst}})
                                            }
                                        });
                                        $(p + " [name="+range.stNm+"]").datepicker("setDate", now(-90).substring(0, 10))
                                        $(p + " [name="+range.edNm+"]").datepicker("setDate", now().substring(0, 10))
                                    })
                                }
                                var sel = 'section#' + _t.getStepId() + '-' + data.uuid;
                                var form = document.querySelector(sel);
                                if (form) {
                                    var table = form.querySelector('table')
                                    var prev = form.querySelector('.tbl_btn_group .btn.prev')
                                    var next = form.querySelector('.tbl_btn_group .btn.next')
                                    if (table && prev && next) {
                                        var lrDp  = getDpFn(prev, next);
                                        var parent = form.querySelector('.tbl_box_wrap')
                                        if (parent.clientWidth >= table.clientWidth) {
                                            lrDp(false, false);
                                        } else {
                                            lrDp(false, true);
                                        }
                                        parent.addEventListener('scroll', function(e) {
                                            var asisX = e.target.scrollLeft;
                                            var maxX = (parent.clientWidth - table.clientWidth) * -1;
                                            if (asisX === 0) {
                                                lrDp(false, true);
                                            } else if (asisX === maxX) {
                                                lrDp(true, false);
                                            } else {
                                                lrDp(true, true);
                                            }
                                        })
                                        prev.addEventListener('click', function () {
                                            $(parent).animate({scrollLeft: 0}, ANIMATE_OPT);
                                            lrDp(false, true);
                                        });
                                        next.addEventListener('click', function () {
                                            var newX = (parent.clientWidth - table.clientWidth) * -1;
                                            $(parent).animate({scrollLeft: newX}, ANIMATE_OPT);
                                            lrDp(true, false);
                                        });
                                    }
                                }
                                if (_t.step().hasOwnProperty('onDraw')) {
                                    _t.step().onDraw(data);
                                }
                                goLast();
                            }, delay);
                        } else {
                            userQuestion(_t.msg())
                            $(".user_box .user_txt:last").html(_t.msg())
                            if (_t.step().hasOwnProperty('onDraw')) {
                                _t.step().onDraw(data);
                            }
                            goLast();
                        }
                    }
                    // 템플릿이 존재 할 경우 템플릿 사용
                    if (_t.step().hasOwnProperty('template')) {
                        getHtml(_t.step().template).done(function (html) {
                            var template = Handlebars.compile(html);
                            // 봇 발화의 경우 tag 및 form 추가
                            send(template(data))
                        })
                    } else {
                        send()
                    }
                    if (_t.step().skip === true) {
                        _t.next()
                    }
                },
                incState: function() {
                    if (data.steps.length -1 > data.state) {
                        data.state += 1;
                        _t.act();
                    } else {
                        apiHelper.init(data.type, undefined, data.intentNm, true);
                    }
                    return data.state;
                },
                next: function() {
                    // 다음 단계로 이동
                    if (_t.step().hasOwnProperty('next')) {
                        if (data.pageSet) {
                            data.pageSet.forEach(function (s) {
                                s.currentPage = 1;
                            })
                        }
                        _t.step().next(data).done(function(){
                            _t.incState();
                        })
                    } else {
                        _t.incState();
                    }
                }
            }
            return _t;
        }
    }

    root.apiHelper = apiHelper;
}))