<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>首页信息</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="stylesheet" href="${appName}/layuiadmin/layui/css/layui.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/admin.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/family.css" media="all">
</head>
<body>

<div class="layui-fluid">
    <div class="layui-row layui-col-space15">
        <div class="layui-col-md8">
            <div class="layui-row layui-col-space15 user"></div>
        </div>

        <div class="layui-col-md4 other">
        </div>

    </div>
</div>

<script src="${appName}/layuiadmin/layui/layui.js"></script>
<script>
    layui.config({
        base: '${appName}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'form', 'admin', 'carousel', 'family'], function () {
        var $ = layui.$,
            form = layui.form,
            admin = layui.admin,
            carousel = (layui.admin, layui.carousel),
            element = layui.element,
            device = layui.device(),
            family = layui.family;

        // 应用名称
        var appName = '${appName}';

        var url = {
            init: appName + "/home/selectConsoleData",
            websocketUrl: '${requestUrl}'
        }

        var globalData = {}

        var init = function (data) {
            // 通知消息权限控制
            if (data.sysAuthModel.notice) {
                // 初始化未读消息通知
                if (parseInt(data.readNum) > 0) {
                    $("#readNum", parent.document).show();
                    $("#readNum", parent.document).addClass("layui-badge");
                    $("#readNum", parent.document).text(data.readNum);
                } else {
                    $("#readNum", parent.document).hide();
                }
            } else {
                // 隐藏通知消息按钮
                $("#readNum", parent.document).parent().parent().hide();
            }

            $(".user").html('');
            $(".other").html('');

            // 权限控制是否显示模块信息
            if (!data.sysAuthModel.income && !data.sysAuthModel.gift) {
                // 没有收入信息 随礼信息菜单权限  模块都不显示
                return;
            }
            // 初始化用户数据
            if (!$.isEmptyObject(data.user) && data.sysConfig.user == '1') {
                initUser(data.user);
            }
            // 初始化提示信息
            if (!$.isEmptyObject(data.tips) && data.sysConfig.tips == '1') {
                $(".other").append(initInfo(data.tips));
            }
            // 初始化用户注册信息
            if (!$.isEmptyObject(data.register)&& data.sysConfig.register == '1') {
                $(".other").append(initInfo(data.register));
            }
            // 初始化登入日志信息
            if (!$.isEmptyObject(data.login) && data.sysConfig.login == '1') {
                $(".other").append(initInfo(data.login));
            }
            // 初始化版本信息
            if (!$.isEmptyObject(data.version)&& data.sysConfig.version == '1') {
                $(".other").append(initInfo(data.version));
            }

        }

        // 初始化业务数据
        var initUser = function (data) {
            var item = '';
            for (var i = 0; i < data.length; i++) {
                var business = data[i];
                item += '<div class="layui-col-md12">';
                item += '   <div class="layui-card">';
                item += '       <div class="layui-card-header">' + business.title + '</div>';
                item += '           <div class="layui-card-body">';
                item += '               <div class="layui-carousel layadmin-carousel layadmin-backlog">';
                item += '                   <div carousel-item>';
                if (globalData.sysAuthModel.income) {
                    item += initUserItem(business.income, '3');
                }
                if (globalData.sysAuthModel.gift) {
                    item += initUserItem(business.giftSend.concat(business.giftReceive), '4');
                }
                item += '                   </div>';
                item += '               </div>';
                item += '           </div>';
                item += '       </div>';
                item += '   </div>';
                item += '</div>';
            }
            $(".user").append(item);

            // 绑定悬浮事件
            $(".layadmin-carousel").each(function () {
                var that = $(this);
                carousel.render({
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

        // 初始化业务数据
        var initUserItem = function (data, mode) {
            var item = '<ul class="layui-row layui-col-space10">';
            for (var j = 0; j < data.length; j++) {
                item += '<li class="layui-col-xs' + mode + '">';
                if (!family.isBlank(data[j].href)) {
                    item += '   <a lay-href="' + data[j].href + '" ' + 'class="layadmin-backlog-body">';
                } else {
                    item += '   <a class="layadmin-backlog-body">';
                }
                item += '       <h3>' + data[j].title + '</h3>';
                item += '       <p>' + setValue(data[j].value) + '</p>';
                item += '   </a>';
                item += '</li>';
            }
            item += '</ul>';
            return item;
        }

        // 设置值
        var setValue = function (value) {
            if (family.isBlank(value) || value == '0' || value == '0.0' || value == '0.00') {
                return '<cite style="font-size: 25px; color: #666;">0</cite>';
            }
            if (value.indexOf("-") == -1) {
                return '<cite style="font-size: 25px; color: #FF5722;">' + value + '</cite>';
            } else {
                return '<cite style="font-size: 25px;">' + value + '</cite>';
            }
        }

        // 初始化信息
        var initInfo = function (data) {
            var item = '<div class="layui-card">';
            item += '       <div class="layui-card-header">' + data[0].title + '</div>';
            item += '           <div class="layui-card-body layui-text">';
            item += '               <div class="layui-card-body layui-text">';
            item += '                   <table class="layui-table">';
            item += '                       <colgroup>';
            item += '                           <col width="50%" />';
            item += '                           <col />';
            item += '                       </colgroup>';
            item += '                       <colgroup>';
            item += '                           <tbody>';
            for (var i = 1; i < data.length; i++) {
                item += '                           <tr>';
                item += '                               <td>' + data[i].title + '</td>';
                item += '                               <td>';
                if (!family.isBlank(data[i].href)) {
                    item += '                               <a lay-href="' + data[i].href + '">' + data[i].value + '</a>';
                } else {
                    item += '                               ' + data[i].value;
                }
                item += '                               </td>';
                item += '                           </tr>';
            }
            item += '                           </tbody>';
            item += '                       </colgroup>';
            item += '                   </table>';
            item += '               </div>';
            item += '           </div>';
            item += '       </div>';
            item += '</div>';
            return item;
        }

        // 获取首页数据
        var initData = function () {
            // 初始化页面信息
            admin.req({
                url: url.init,
                type: "get",
                async: false,
                dataType: "json",
                done: function (response) {
                    if (response.bizResult) {
                        globalData = response.data;
                        init(globalData);
                    } else {
                        family.msg(response.msg);
                    }
                }
            });
        }

        initData();

        // websocket订阅首页主题
        family.webSocket(url.websocketUrl, "console", function (data) {
            initData();
        });

    });
</script>
</body>
</html>

