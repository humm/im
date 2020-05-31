<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>设置我的密码</title>
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
        <div class="layui-col-md12">
            <div class="layui-card">
                <div class="layui-card-header">修改密码</div>
                <div class="layui-card-body" pad15>

                    <div class="layui-form" lay-filter="">
                        <div class="layui-form-item">
                            <label class="layui-form-label">当前密码</label>
                            <div class="layui-input-inline">
                                <input type="password" name="oldPassword" lay-verify="required" lay-verType="tips"
                                       class="layui-input">
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">新密码</label>
                            <div class="layui-input-inline">
                                <input type="password" name="password" lay-verify="pass" lay-verType="tips"
                                       autocomplete="off" id="LAY_password" class="layui-input">
                            </div>
                            <div class="layui-form-mid layui-word-aux">6至16位</div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">确认新密码</label>
                            <div class="layui-input-inline">
                                <input type="password" name="repassword" lay-verify="repass" lay-verType="tips"
                                       autocomplete="off" class="layui-input">
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <div class="layui-input-block">
                                <button class="layui-btn" lay-submit lay-filter="setmypass">提交</button>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

<script src="${appName}/layuiadmin/layui/layui.js"></script>
<script src="${appName}/layuiadmin/lib/extend/base64.min.js"></script>

<script>
    layui.config({
        base: '${appName}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'admin', 'family', 'set'], function () {
        var $ = layui.$,
            form = layui.form,
            admin = layui.admin,
            family = layui.family;

        // 应用名称
        var appName = '${appName}';

        // 历史密码
        var userPassword = Base64.decode('${password}');

        var url = {
            changePassword: appName + "/user/changPassword"
        }

        // 绑定修改密码事件
        $(document).on('click', '.layui-btn', function () {
            var oldPassword = $("input[name='oldPassword']").val();
            var password = $("input[name='password']").val();
            var repassword = $("input[name='repassword']").val();

            if(family.isBlank(password) || password.length < 6 || password.length > 16 || password != repassword){
                return;
            }
            if(oldPassword != userPassword){
                family.msg(family.tips.msg.historyPasswordError);
                return;
            }
            if(password == userPassword){
                family.msg(family.tips.msg.historyPasswordEqualPassword);
                return;
            }
            var request = {
                password: Base64.encode(password)
            }
            admin.req({
                url: url.changePassword,
                type: "post",
                dataType: "json",
                data: request,
                done: function (response) {
                    if (response.bizResult) {
                        $(".layui-btn").attr("disabled", true);
                        $("input[name='oldPassword']").attr("disabled", true);
                        $("input[name='password']").attr("disabled", true);
                        $("input[name='repassword']").attr("disabled", true);
                        $(".layui-btn").parent().parent().remove();
                        family.msg(response.msg);
                    } else {
                        family.msg(response.msg);
                    }
                }
            });
        });
    });
</script>
</body>
</html>