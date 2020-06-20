/** family自定义方法 */
;layui.define(function (e) {
    var $ = layui.$,
        table = layui.table,
        form = layui.form;

    var obj = {
        msg: function (msg, param) {
            if ($.isEmptyObject(param)) {
                layer.msg(msg, {time: 1000});
            } else {
                layer.msg(msg, param);
            }
        },
        value: function (parameter) {
            if (this.isBlank(parameter)) {
                return "";
            } else {
                return parameter;
            }
        },
        isBlank: function (parameter) {
            if (parameter == null || parameter == '' || parameter == undefined || parameter == 'undefined') {
                return true;
            } else {
                return false;
            }
        },
        setAuthority: function (hasButton, buttonId) {
            if (!hasButton && !this.isBlank(buttonId)) {
                var ids = buttonId.split(",");
                for (var i = 0; i < ids.length; i++) {
                    $("#" + ids[i]).remove();
                }
            }
        },
        clearBlank: function (data) {
            var convertData = {};
            if (!this.isBlank(data)) {
                for (var key in data) {
                    if (!this.isBlank(data[key])) {
                        convertData[key] = data[key];
                    }
                }
            }
            return convertData;
        },
        setCondition: function (tag, data) {
            var that = this;
            $("." + tag + " [name]").each(function () {
                var name = $(this).attr("name");
                var type = $(this)[0].tagName.toLowerCase();
                if (type != "select") {
                    return;
                }
                var select = data[name];
                if (!that.isBlank(select)) {
                    for (var i = 0; i < select.length; i++) {
                        var option = "<option value='" + select[i]["dictionaryItem"] + "'>" + select[i]["dictionaryCaption"] + "</option>";
                        $(this).append(option);
                    }
                }
            });
            form.render();
        },
        setValue: function (tag, data) {
            var that = this;
            if (that.isBlank(data)) {
                return;
            }
            $("." + tag + " [name]").each(function () {
                var name = $(this).attr("name");
                if (that.isBlank(data[name])) {
                    return;
                }
                var type = $(this)[0].tagName.toLowerCase();
                if (type == "textarea") {
                    $(this).text(that.value(data[name]));
                } else if (type == "select") {
                    $(this).val(that.value(data[name]));
                } else if (type == "input") {
                    var subType = $(this).attr("type");
                    if (subType == 'text' || subType == 'hidden' || subType == 'password') {
                        $(this).val(that.value(data[name]));
                    } else if (subType == 'checkbox' || subType == 'radio') {
                        $("input[name=" + name + "]").each(function () {
                            var value = $(this).val();
                            if (data[name].indexOf(value) != -1) {
                                $(this).attr("checked", true);
                            } else {
                                $(this).attr("checked", false);
                            }
                        });
                    }
                }
            });
            form.render();
        },
        getValue: function (tag) {
            var data = {};
            $("." + tag + " [name]").each(function () {
                var name = $(this).attr("name");
                var type = $(this)[0].tagName.toLowerCase();
                if (type == "textarea") {
                    data[name] = $(this).text();
                } else {
                    // select input
                    data[name] = $(this).val();
                }
            });
            return data;
        },
        clearValue: function (tag) {
            $("." + tag + " [name]").each(function () {
                var type = $(this)[0].tagName.toLowerCase();
                if (type == "textarea") {
                    $(this).text("");
                } else {
                    // select input
                    $(this).val("");
                }
            });
            form.render();
        },
        getUrlParameter: function (parameter) {
            var reg = new RegExp("(^|&)" + parameter + "=([^&]*)(&|$)", "i");
            var r = window.location.search.substr(1).match(reg);
            if (r != null) return unescape(r[2]);
            return null;
        },
        getDate: function () {
            var date = new Date;
            var year = date.getFullYear();
            var month = date.getMonth() + 1;
            var day = date.getDate();
            month = (month < 10 ? "0" + month : month);
            day = (day < 10 ? "0" + day : day);
            return year + "-" + month + "-" + day;
        },
        getBarData: function (bar) {
            var data = {
                title: {text: bar.title, subtext: bar.subTitle},
                tooltip: {trigger: 'axis'},
                legend: {data: bar.legendData},
                toolbox: {
                    show: true,
                    feature: {
                        mark: {show: false},
                        dataView: {show: true, readOnly: false},
                        magicType: {show: true, type: ['line', 'bar']},
                        restore: {show: true},
                        saveAsImage: {show: true}
                    }
                },
                calculable: true,
                xAxis: [{type: 'category', data: bar.xaxisData}],
                yAxis: [{type: 'value'}],
                series: []
            }
            var element = {
                name: "",
                type: 'bar',
                data: [],
                markPoint: {data: [{type: 'max', name: '最大值'}, {type: 'min', name: '最小值'}]},
                markLine: {data: [{type: 'average', name: '平均值'}]}
            }
            for (var i = 0; i < bar.yaxisData.length; i++) {
                var ele = JSON.parse(JSON.stringify(element));
                ele.name = bar.yaxisData[i].name;
                ele.data = bar.yaxisData[i].data;
                data.series.push(ele);
            }
            return data;
        },
        getPieData: function (pie) {
            var data = {
                title: {text: pie.title, subtext: pie.subTitle, x: "center", textStyle: {fontSize: 14}},
                tooltip: {trigger: "item", formatter: "{a} <br/>{b} : {c} ({d}%)"},
                legend: {orient: "vertical", x: "left", data: pie.legendData},
                toolbox: {
                    show: true,
                    feature: {
                        mark: {show: false},
                        dataView: {show: true, readOnly: false},
                        magicType: {
                            show: true,
                            type: ['pie', 'funnel'],
                            option: {
                                funnel: {
                                    x: '25%',
                                    width: '50%',
                                    funnelAlign: 'left',
                                    max: 1548
                                }
                            }
                        },
                        restore: {show: true},
                        saveAsImage: {show: true}
                    }
                },
                series: []
            }
            var element = {
                name: "",
                type: "pie",
                radius: "55%",
                center: ["50%", "50%"],
                data: []
            }
            for (var i = 0; i < pie.pieData.length; i++) {
                var ele = JSON.parse(JSON.stringify(element));
                ele.name = pie.pieData[i].name;
                ele.data = pie.pieData[i].data;
                data.series.push(ele);
            }
            return data;
        },
        webSocket: function webSocket(connectionUrl, topicName, callBack) {
            connectionUrl = connectionUrl.replace("http", "ws");
            var socket = new WebSocket(connectionUrl + "/websocket/" + topicName);

            //打开事件
            socket.onopen = function () {
                console.log("websocket connect success: " + topicName);
            };

            //获得消息事件
            socket.onmessage = function (result) {
                callBack(result.data);
            };

            //关闭事件
            socket.onclose = function () {
                console.log("websocket closed: " + topicName);
            };

            // 发生错误触发
            socket.onerror = function () {
                console.log("websocket error: " + topicName);
            }
        },
        initTable: function (config) {
            table.render($.extend({
                autoSort: false,  // 禁用前端自动排序
                page: true,
                limit: 10,
                limits: [10, 50, 100, 500],
                text: {
                    none: this.tips.msg.emptyData
                },
                // 行边框风格
                skin: 'line'
                // 开启隔行背景
                // even: true
            }, config));
        },
        open: function (config) {
            var requestUrl = config.content;
            if (!!requestUrl) {
                if (requestUrl.indexOf('?') != -1){
                    var requestParameters = {};
                    //获取请求参数的字符串
                    var parameters = decodeURI(requestUrl.substr(requestUrl.indexOf("?") + 1)).split('&');
                    //循环遍历，将请求的参数封装到请求参数的对象之中
                    for (var i = 0; i < parameters.length; i++) {
                        requestParameters[parameters[i].split('=')[0]] = parameters[i].split('=')[1];
                    }
                    if (!!!requestParameters.menuId) {
                        config.content += "&menuId=skip";
                    }
                } else {
                    config.content += "?menuId=skip";
                }
            }
            if (!$.isEmptyObject(config.area)) {
                var screenHeight = $(window).height();
                if (parseInt(screenHeight) < parseInt(config.area[1].replace("px", ""))) {
                    if (this.size.two == config.area[1]) {
                        config.area[1] = '96%';
                    } else if (this.size.four == config.area[1]) {
                        config.area[1] = '96%';
                    } else if (this.size.seven == config.area[1]) {
                        config.area[1] = '60%';
                    } else if (this.size.six == config.area[1]) {
                        config.area[1] = '60%';
                    } else if (this.size.nine == config.area[1]) {
                        config.area[1] = '96%';
                    }
                }
            }
            layer.open(config);
        },
        config: {
            index: "/index",
            adminCode: "admin"
        },
        set: {
            resize: false
        },
        operate: {
            add: "add",
            delete: "delete",
            update: "update",
            detail: "detail-.ftl",
            reset: "reset",
            refresh: "refresh"
        },
        size: {
            // 通用页面
            one: "450px",
            two: "600px",
            // 参数信息页面
            three: "750px",
            four: "600px",
            seven: '700px',
            // 首页模块页面
            five: '480px',
            six: '300px',
            // 角色页面
            eight: "450px",
            nine: "650px",
            // 参数修改页面
            ten: "300px",
            eleven: "180px"
        },
        tips: {
            title: {
                add: "新增",
                update: "修改",
                del: "删除",
                detail: "详情"
            },
            btn: {
                ok: "确定",
                save: "保存",
                cancel: "取消"
            },
            warn: {
                notSelect: "请选择数据",
                selectOne: "请选择单条数据",
                confirmDel: "确定删除吗",
                confirmResetPassword: "确定重置密码吗"
            },
            msg: {
                notSupportEvent: "不支持的事件类型",
                emptyData: "暂无相关数据",
                request: "数据提交中...",
                notSameOne: "送礼人和收礼人不能为同一人",
                isLoginOne: "送礼人或者收礼人必须有一个为当前登录人",
                notEmpty: "不能为空",
                isNumberOrLetter: "用户代码只能为字母、数字及下划线",
                userIsExist: "用户代码已经存在",
                roleIsExist: "角色代码已经存在",
                systemUserNotDelete: "系统用户不能删除",
                systemUserNotUpdate: "系统用户不能修改",
                closeDictionaryNotUpdate: "封闭字典项不能修改",
                parameterNotUpdate: "参数不能修改",
                onlyLength: "参数值必须为S位",
                historyPasswordError: "当前密码错误",
                historyPasswordEqualPassword: "修改后密码与历史密码一致",
                passwordChangefresh: "密码修改成功,若要再次修改,请刷新页面"
            }
        }
    };

    e('im', obj);
});