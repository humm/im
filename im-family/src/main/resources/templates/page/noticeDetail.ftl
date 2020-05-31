<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>消息通知信息-详情</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="stylesheet" href="${appName}/layuiadmin/layui/css/layui.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/admin.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/family.css" media="all">

</head>
<body>

<div class="layui-fluid" id="LAY-app-notice-detail">
    <div class="layui-card layuiAdmin-msg-detail">
        <div class="layui-card-header">
            <h1 id="title"></h1>
            <p>
                <span id="date"></span>
            </p>
        </div>
        <div class="layui-card-body layui-text">
            <div class="layadmin-text" id="content"></div>

            <div style="padding-top: 30px;">
                <a href="javascript:;" id="back"
                   class="layui-btn layui-btn-primary layui-btn-sm">返回上级</a>
            </div>
        </div>
    </div>
</div>

<script src="${appName}/layuiadmin/layui/layui.js"></script>
<script>
    layui.config({
        base: '${appName}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'form', 'admin', 'family'], function () {
        var $ = layui.$,
            form = layui.form,
            admin = layui.admin,
            family = layui.family;

        // 应用名称
        var appName = '${appName}';

        var request = {
            noticeId: family.getUrlParameter("noticeId"),
            isTranslate: family.getUrlParameter("isTranslate"),
        }

        // 阅读状态
        var readStatus = family.getUrlParameter("readStatus");

        // 请求url
        var url = appName + "/notice/selectOne?" + $.param(request);

        // 初始化页面信息
        admin.req({
            url: url,
            type: "get",
            dataType: "json",
            done: function (response) {
                if (response.bizResult) {
                    var data = response.data;
                    $("#title").html(data.userId + '&nbsp;&nbsp;&nbsp;' + data.businessType + '&nbsp;&nbsp;&nbsp;' + data.businessAmount);
                    $("#date").html(data.modifyDate);
                    var content = "<p>消息用户：" + data.userId + "</p>";
                    content += "<p>业务类型：" + data.businessType + "</p>";
                    content += "<p>业务子类型：" + data.businessSubType + "</p>";
                    content += "<p>业务时间：" + data.businessDate + "</p>";
                    content += "<p>业务金额：" + data.businessAmount + "</p>";
                    content += "<p>通知状态：" + data.noticeStatus + "</p>";
                    content += "<p>通知类型：" + data.noticeType + "</p>";
                    content += "<p>阅读状态：" + data.readStatus + "</p>";
                    $("#content").html(content);
                } else {
                    family.msg(response.msg);
                }
            }
        });
        
        // 返回上级事件
        $("#back").on("click", function () {
            window.location.href = window.location.href.replace("detail", "list");
        });
    });
</script>
</body>
</html>