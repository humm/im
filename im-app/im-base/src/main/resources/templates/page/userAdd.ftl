<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>用户信息-新增</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="stylesheet" href="${appName}/layuiadmin/layui/css/layui.css" media="all">
</head>
<body>

<div class="layui-form" style="padding: 20px 30px 0 0;">
    <div class="layui-form-item">
        <label class="layui-form-label">用户代码</label>
        <div class="layui-input-inline">
            <input type="text" name="userCode" class="layui-input" lay-verify="required"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">用户名称</label>
        <div class="layui-input-inline">
            <input type="text" name="userName" class="layui-input" lay-verify="required"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">用户状态</label>
        <div class="layui-input-inline">
            <select name="userStatus" lay-verify="required"></select>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">角色信息</label>
        <div class="layui-input-inline" id="roleId"></div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">用户备注</label>
        <div class="layui-input-inline">
            <textarea name="userMemo" class="layui-textarea"></textarea>
        </div>
    </div>
    <div class="layui-form-item layui-hide">
        <input type="button" lay-submit lay-filter="LAY-app-user-add" id="LAY-app-user-add">
    </div>
</div>

<script src="${appName}/layuiadmin/layui/layui.js"></script>
<script>
    layui.config({
        base: '${appName}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'form', 'laydate', 'admin', 'im'], function () {
        var $ = layui.$,
            form = layui.form,
            admin = layui.admin,
            laydate = layui.laydate,
            im = layui.im;

        // 应用名称
        var appName = '${appName}';

        var url = appName + "/user/selectInitData";

        // 初始化页面信息
        admin.req({
            url: url,
            type: "get",
            dataType: "json",
            done: function (response) {
                if (response.bizResult) {
                    im.setCondition("layui-form", response.data.condition);
                    // 设置角色信息
                    if (!$.isEmptyObject(response.data.roleList)) {
                        var roleList = response.data.roleList;
                        var roleItem = "";
                        for (var i = 0; i < roleList.length; i++) {
                            var roleId = roleList[i].roleId;
                            var roleName = roleList[i].roleName;
                            roleItem += '<input type="checkbox" lay-skin="primary"';
                            roleItem += '       title="' + roleName + '"';
                            roleItem += '       value="' + roleId + '"';
                            roleItem += '       name="roleId"';
                            roleItem += '/>';
                        }
                        $("#roleId").append(roleItem);
                    }
                    form.render();
                } else {
                    im.msg(response.msg);
                }
            }
        });
    })
</script>
</body>
</html>