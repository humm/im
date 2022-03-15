<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>fims</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="stylesheet" href="${appName}/layuiadmin/layui/css/layui.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/admin.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/login.css" media="all">
    <link rel="icon" href="${appName}/layuiadmin/style/res/logo-black.png" type="image/png" />
</head>
<body>

<div class="layadmin-user-login layadmin-user-display-show" id="LAY-user-login" style="display: none;">

    <div class="layadmin-user-login-main">
        <div class="layadmin-user-login-box layadmin-user-login-header">
            <img src="${appName}/layuiadmin/style/res/login.png">
        </div>
        <div class="layadmin-user-login-box layadmin-user-login-body layui-form">
            <div class="layui-form-item">
                <label class="layadmin-user-login-icon layui-icon layui-icon-username"
                       for="LAY-user-login-username"></label>
                <input type="text" name="userCode" id="LAY-user-login-username" lay-verify="required"
                       placeholder="账号" class="layui-input">
            </div>
            <div class="layui-form-item">
                <label class="layadmin-user-login-icon layui-icon layui-icon-password"
                       for="LAY-user-login-password"></label>
                <input type="password" name="userPassword" id="LAY-user-login-password" lay-verify="required"
                       placeholder="密码" class="layui-input">
            </div>
            <div class="layui-form-item" style="margin-bottom: 20px;">
                <input type="checkbox" name="rememberPassword" id="LAY-user-login-rememberPassword" lay-skin="primary" title="记住密码">
            </div>
            <div class="layui-form-item">
                <button class="layui-btn layui-btn-fluid" lay-submit lay-filter="LAY-user-login-submit">登 入</button>
            </div>
        </div>
    </div>

    <div class="layui-trans layadmin-user-login-footer">
        <p>${appHelp}</p>
    </div>
</div>

<script src="${appName}/layuiadmin/layui/layui.js"></script>
<script src="${appName}/layuiadmin/lib/extend/base64.min.js"></script>
<script>
    layui.config({
        base: '${appName}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'user', 'im'], function () {
        var $ = layui.$,
            admin = layui.admin,
            im = layui.im,
            form = layui.form;

        form.render();

        // 应用名称
        var appName = '${appName}';


        //提交
        form.on('submit(LAY-user-login-submit)', function (obj) {
            obj.field.userPassword = Base64.encode(obj.field.userPassword);
            obj.field.rememberPassword = $("input[name='rememberPassword']:checked").val();
            //请求登入接口
            admin.req({
                url: appName + '/user/login',
                type: 'post',
                data: obj.field,
                done: function (response) {
                    if (response.bizResult) {
                        location.href = appName + im.config.index;
                    } else {
                        im.msg(response.msg);
                    }
                }
            });
        });

        // 监听回车事件
        document.onkeydown = function (ev) {
            var event = ev || event
            if (event.keyCode == 13) {
                $(".layui-btn").click();
            }
        }

        // 获取cookie信息
        var getCookie = function () {
            var cookieInfo = {}
            var cookies = document.cookie.split(';');
            for (var i=0; i<cookies.length; i++) {
                if (cookies[i].indexOf("=")) {
                    var single = cookies[i].split("=");
                    if (single.length == 2) {
                        cookieInfo[single[0].trim()] = single[1].trim();
                    }
                }
            }
            return cookieInfo;
        }

        // 填充历史登录信息
        var userInfo = getCookie();
        if ('on' == userInfo.rememberPassword) {
            $("input[name='rememberPassword']").attr("checked",true);
            $("input[name='userCode']").val(userInfo.userCode);
            if (!im.isBlank(userInfo.userPassword)) {
                $("input[name='userPassword']").val(Base64.decode(userInfo.userPassword));
            }
            form.render();
        }
    });
</script>
</body>
</html>