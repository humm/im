<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>用户信息-详情</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="stylesheet" href="${appName}/layuiadmin/layui/css/layui.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/family.css" media="all">
</head>
<body>

<div class="layui-form" style="padding: 20px 30px 0 0;">
    <div class="layui-form-item">
        <label class="layui-form-label">用户代码</label>
        <div class="layui-input-inline">
            <input type="text" name="userCode" class="layui-input layui-detail" disabled="disabled" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">用户名称</label>
        <div class="layui-input-inline">
            <input type="text" name="userName" class="layui-input layui-detail" disabled="disabled" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">用户状态</label>
        <div class="layui-input-inline">
            <input type="text" name="userStatus" class="layui-input layui-detail" disabled="disabled" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">角色信息</label>
        <div class="layui-input-inline" id="roleId"></div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">用户备注</label>
        <div class="layui-input-inline">
            <textarea name="userMemo" class="layui-textarea layui-detail" disabled="disabled"></textarea>
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
            userId: family.getUrlParameter("userId"),
            isTranslate: family.getUrlParameter("isTranslate")
        }

        // 请求url
        var url = {
            init: appName + "/user/selectInitData",
            load: appName + "/user/selectOne?" + $.param(request)
        }

        // 初始化页面信息
        admin.req({
            url: url.init,
            type: "get",
            dataType: "json",
            done: function (response) {
                if (response.bizResult) {
                    family.setCondition("layui-form", response.data.condition);

                    // 设置角色信息
                    if (!$.isEmptyObject(response.data.roleList)) {
                        var roleList = response.data.roleList;
                        var roleItem = "";
                        for (var i = 0; i < roleList.length; i++) {
                            var roleId = roleList[i].roleId;
                            var roleName = roleList[i].roleName;
                            roleItem += '<input type="checkbox" lay-skin="primary"';
                            roleItem += '       class="layui-input layui-detail"';
                            roleItem += '       disabled="disabled"';
                            roleItem += '       title="' + roleName + '"';
                            roleItem += '       value="' + roleId + '"';
                            roleItem += '       name="roleId"';
                            roleItem += '/>';
                        }
                        $("#roleId").append(roleItem);
                    }

                    // 数据回填
                    admin.req({
                        url: url.load,
                        type: "get",
                        dataType: "json",
                        done: function (response) {
                            if (response.bizResult) {
                                family.setValue("layui-form", response.data);
                                // admin用户隐藏角色信息
                                if(family.config.adminCode == response.data.userCode){
                                    $("#roleId").parent().hide();
                                }
                                form.render();
                            } else {
                                family.msg(response.msg);
                            }
                        }
                    });
                } else {
                    family.msg(response.msg);
                }
            }
        });

    })
</script>
</body>
</html>