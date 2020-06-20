<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>登入日志信息-详情</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="stylesheet" href="${appName}/layuiadmin/layui/css/layui.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/im.css" media="all">
</head>
<body>

<div class="layui-form" style="padding: 20px 30px 0 0;">
    <div class="layui-form-item">
        <label class="layui-form-label">登入用户</label>
        <div class="layui-input-inline">
            <input type="text" name="userId" class="layui-input layui-detail" disabled="disabled" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">登入时间</label>
        <div class="layui-input-inline">
            <input type="text" name="loginDate" class="layui-input layui-detail" disabled="disabled" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">登出时间</label>
        <div class="layui-input-inline">
            <input type="text" name="logoutDate" class="layui-input layui-detail" disabled="disabled" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">登入状态</label>
        <div class="layui-input-inline">
            <input type="text" name="loginStatus" class="layui-input layui-detail" disabled="disabled" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">登入消息</label>
        <div class="layui-input-inline">
            <input type="text" name="loginMessage" class="layui-input layui-detail" disabled="disabled" />
        </div>
    </div>
</div>

<script src="${appName}/layuiadmin/layui/layui.js"></script>
<script>
    layui.config({
        base: '${appName}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'form', 'admin', 'im'], function () {
        var $ = layui.$,
            form = layui.form,
            admin = layui.admin,
            im = layui.im;

        // 应用名称
        var appName = '${appName}';

        var request = {
            logId: im.getUrlParameter("logId"),
            isTranslate: im.getUrlParameter("isTranslate")
        }

        // 请求url
        var url = appName + "/loginLog/selectOne?" + $.param(request);

        // 初始化页面信息
        admin.req({
            url: url,
            type: "get",
            dataType: "json",
            done: function (response) {
                if (response.bizResult) {
                    im.setValue("layui-form", response.data);
                } else {
                    im.msg(response.msg);
                }
            }
        });

    })
</script>
</body>
</html>