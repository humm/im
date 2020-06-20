<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>收入信息报表</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="stylesheet" href="${appName}/layuiadmin/layui/css/layui.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/admin.css" media="all">
</head>
<body>

<div class="layui-fluid">
    <div class="layui-row layui-col-space15">
        <div class="layui-col-sm12">
            <div class="layui-card">
                <div class="layui-card-header">收入年度分析</div>
                <div class="layui-card-body">
                    <div class="layui-carousel layadmin-carousel layadmin-dataview layadmin-carousel-year"
                         data-anim="fade" lay-filter="LAY-index-income-year">
                        <div carousel-item id="LAY-index-income-year">
                            <div><i class="layui-icon layui-icon-loading1 layadmin-loading"></i></div>
                        </div>
                    </div>

                </div>
            </div>
        </div>
        <div class="layui-col-sm12">
            <div class="layui-card">
                <div class="layui-card-header">收入来源分析</div>
                <div class="layui-card-body">
                    <div class="layui-carousel layadmin-carousel layadmin-dataview layadmin-carousel-source"
                         data-anim="fade" lay-filter="LAY-index-income-source">
                        <div carousel-item id="LAY-index-income-source">
                            <div><i class="layui-icon layui-icon-loading1 layadmin-loading"></i></div>
                        </div>
                    </div>

                </div>
            </div>
        </div>
        <div class="layui-col-sm12">
            <div class="layui-card">
                <div class="layui-card-header">收入类型分析</div>
                <div class="layui-card-body">
                    <div class="layui-carousel layadmin-carousel layadmin-dataview layadmin-carousel-type"
                         data-anim="fade" lay-filter="LAY-index-income-type">
                        <div carousel-item id="LAY-index-income-type">
                            <div><i class="layui-icon layui-icon-loading1 layadmin-loading"></i></div>
                        </div>
                    </div>

                </div>
            </div>
        </div>
        <div class="layui-col-sm12">
            <div class="layui-card">
                <div class="layui-card-header">收入极值分析</div>
                <div class="layui-card-body">
                    <div class="layui-carousel layadmin-carousel layadmin-dataview layadmin-carousel-peak"
                         data-anim="fade" lay-filter="LAY-index-income-peak">
                        <div carousel-item id="LAY-index-income-peak">
                            <div><i class="layui-icon layui-icon-loading1 layadmin-loading"></i></div>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

<script src="${appName}/layuiadmin/layui/layui.js?t=1"></script>
<script>
    layui.config({
        base: '${appName}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'admin', 'carousel', 'echarts', 'im'], function () {
        var $ = layui.$,
            admin = layui.admin,
            report = (layui.admin, layui.carousel),
            im = layui.im,
            element = layui.element,
            device = layui.device(),
            carousel = layui.carousel,
            echarts = layui.echarts;

        var appName = '${appName}';
        var url = appName + "/report/initData?";
        var request = {
            reportMode: "bar",
            reportType: "income",
            reportSubType: "year",
            reportValue: ""
        }

        // 收入年度
        var yearList = [];
        // 收入来源
        var sourceList = [];
        // 收入类型
        var typeList = [];
        // 收入极值
        var peakList = [];

        // 收入年度渲染
        function loopYear(data) {
            var years = JSON.parse(JSON.stringify(data.xaxisData)).reverse();
            var options = "";
            for (var i = 0; i < years.length; i++) {
                options += "<div year='" + years[i].replace("年","") + "'></div>";
            }
            $("#LAY-index-income-year").append(options);

            reportRender("layadmin-carousel-year");

            var index = 0;
            carousel.on("change(LAY-index-income-year)", function (e) {
                initYearReport(index = e.index);
            }), layui.admin.on("side", function () {
                setTimeout(function () {
                    initYearReport(index);
                }, 500);
            }), layui.admin.on("hash(tab)", function () {
                layui.router().path.join("") || initYearReport(index);
            });

        }

        // 收入年度
        var initYearReport = function (index, type) {
            var year = $("#LAY-index-income-year").children("div")[index];
            if (index == 0) {
                request.reportMode = "bar";
                request.reportSubType = "year";
            } else {
                var select = $(year).attr("year");
                request.reportMode = "bar";
                request.reportSubType = "month";
                request.reportValue = select;
            }
            admin.req({
                url: url + $.param(JSON.parse(JSON.stringify(request))),
                type: "get",
                dataType: "json",
                done: function (response) {
                    if (response.bizResult) {
                        if (type == "init") {
                            loopYear(response.data);
                        }
                        yearList[index] = echarts.init(year, layui.echartsTheme);
                        yearList[index].setOption(im.getBarData(response.data));
                        window.onresize = yearList[index].resize;
                        if($.isEmptyObject(response.data.legendData)){
                            im.msg(im.tips.msg.emptyData);
                        }
                    } else {
                        im.msg(response.msg);
                    }
                }
            });
        }

        // 初始化收入年度
        initYearReport(0, "init");

        // 收入来源渲染
        function loopSource(data) {
            if (!$.isEmptyObject(data.userList)) {
                var options = "";
                for (var i = 0; i < data.userList.length; i++) {
                    options += "<div user='" + data.userList[i] + "'></div>";
                }
                $("#LAY-index-income-source").append(options);
            }

            reportRender("layadmin-carousel-source");

            var index = 0;
            carousel.on("change(LAY-index-income-source)", function (e) {
                initSourceReport(index = e.index);
            }), layui.admin.on("side", function () {
                setTimeout(function () {
                    initSourceReport(index);
                }, 500);
            }), layui.admin.on("hash(tab)", function () {
                layui.router().path.join("") || initSourceReport(index);
            });

        }

        // 收入来源
        var initSourceReport = function (index, type) {
            var user = $("#LAY-index-income-source").children("div")[index];
            request.reportMode = "pie";
            request.reportSubType = "source";
            if (index == 0) {
                request.reportValue="";
            } else {
                var select = $(user).attr("user");
                request.reportValue = select;
            }
            admin.req({
                url: url + $.param(JSON.parse(JSON.stringify(request))),
                type: "get",
                dataType: "json",
                done: function (response) {
                    if (response.bizResult) {
                        if (type == "init") {
                            loopSource(response.data);
                        }
                        sourceList[index] = echarts.init(user, layui.echartsTheme);
                        sourceList[index].setOption(im.getPieData(response.data));
                        window.onresize = sourceList[index].resize;
                    } else {
                        im.msg(response.msg);
                    }
                }
            });
        }
        // 初始化收入来源
        initSourceReport(0, "init");

        // 收入类型渲染
        function loopType(data) {
            if (!$.isEmptyObject(data.userList)) {
                var options = "";
                for (var i = 0; i < data.userList.length; i++) {
                    options += "<div user='" + data.userList[i] + "'></div>";
                }
                $("#LAY-index-income-type").append(options);
            }

            reportRender("layadmin-carousel-type");

            var index = 0;
            carousel.on("change(LAY-index-income-type)", function (e) {
                initTypeReport(index = e.index);
            }), layui.admin.on("side", function () {
                setTimeout(function () {
                    initTypeReport(index);
                }, 500);
            }), layui.admin.on("hash(tab)", function () {
                layui.router().path.join("") || initTypeReport(index);
            });

        }

        // 收入类型
        var initTypeReport = function (index, type) {
            var user = $("#LAY-index-income-type").children("div")[index];
            request.reportMode = "pie";
            request.reportSubType = "type";
            if (index == 0) {
                request.reportValue="";
            } else {
                var select = $(user).attr("user");
                request.reportValue = select;
            }
            admin.req({
                url: url + $.param(JSON.parse(JSON.stringify(request))),
                type: "get",
                dataType: "json",
                done: function (response) {
                    if (response.bizResult) {
                        if (type == "init") {
                            loopType(response.data);
                        }
                        typeList[index] = echarts.init(user, layui.echartsTheme);
                        typeList[index].setOption(im.getPieData(response.data));
                        window.onresize = typeList[index].resize;
                    } else {
                        im.msg(response.msg);
                    }
                }
            });
        }
        // 初始化收入类型
        initTypeReport(0, "init");

        // 收入极值渲染
        function loopPeak(data) {
            if (!$.isEmptyObject(data.userList)) {
                var options = "";
                for (var i = 0; i < data.userList.length; i++) {
                    options += "<div user='" + data.userList[i] + "'></div>";
                }
                $("#LAY-index-income-peak").append(options);
            }

            reportRender("layadmin-carousel-peak");

            var index = 0;
            carousel.on("change(LAY-index-income-peak)", function (e) {
                initPeakReport(index = e.index);
            }), layui.admin.on("side", function () {
                setTimeout(function () {
                    initPeakReport(index);
                }, 500);
            }), layui.admin.on("hash(tab)", function () {
                layui.router().path.join("") || initPeakReport(index);
            });

        }

        // 收入极值
        var initPeakReport = function (index, type) {
            var user = $("#LAY-index-income-peak").children("div")[index];
            request.reportMode = "pie";
            request.reportSubType = "peak";
            if (index == 0) {
                request.reportValue="";
            } else {
                var select = $(user).attr("user");
                request.reportValue = select;
            }
            admin.req({
                url: url + $.param(JSON.parse(JSON.stringify(request))),
                type: "get",
                dataType: "json",
                done: function (response) {
                    if (response.bizResult) {
                        if (type == "init") {
                            loopPeak(response.data);
                        }
                        peakList[index] = echarts.init(user, layui.echartsTheme);
                        peakList[index].setOption(im.getPieData(response.data));
                        window.onresize = peakList[index].resize;
                    } else {
                        im.msg(response.msg);
                    }
                }
            });
        }
        // 初始化收入极值
        initPeakReport(0, "init");

        // 渲染报表
        function reportRender(className) {
            $("." + className).each(function () {
                var that = $(this);
                report.render({
                    elem: this,
                    width: "100%",
                    arrow: "none",
                    interval: that.data("interval"),
                    autoplay: that.data("autoplay") === true,
                    trigger: device.ios || device.android ? "click" : "hover",
                    anim: that.data("anim")
                });
            }), element.render("progress");
        }

    });
</script>
</body>
</html>

