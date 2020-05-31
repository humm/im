<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>模块信息-修改</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, role-scalable=0">
    <link rel="stylesheet" href="${appName}/layuiadmin/layui/css/layui.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/family.css" media="all">
</head>
<body>

<div class="layui-form" style="padding: 20px 30px 0 0;">

    <div class="layui-form-item">
        <label class="layui-form-label">用户信息</label>
        <div class="layui-input-inline layui-input-inline-module" style="margin: 0 0 10px 10px;">
            <input type="checkbox" name="user" <#if '${user}' =='1'> checked </#if> lay-skin="switch" lay-text="开启|关闭" />
        </div>
        <label class="layui-form-label">提示信息</label>
        <div class="layui-input-inline layui-input-inline-module" style="margin: 0 0 10px 10px;">
            <input type="checkbox" name="tips" <#if '${tips}' =='1'> checked </#if> lay-skin="switch" lay-text="开启|关闭" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">注册信息</label>
        <div class="layui-input-inline layui-input-inline-module" style="margin: 0 0 10px 10px;">
            <input type="checkbox" name="register" <#if '${register}' =='1'> checked </#if> lay-skin="switch" lay-text="开启|关闭" />
        </div>
        <label class="layui-form-label">登录信息</label>
        <div class="layui-input-inline layui-input-inline-module" style="margin: 0 0 10px 10px;">
            <input type="checkbox" name="login" <#if '${login}' =='1'> checked </#if> lay-skin="switch" lay-text="开启|关闭" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">版本信息</label>
        <div class="layui-input-inline layui-input-inline-module" style="margin: 0 0 10px 10px;">
            <input type="checkbox" name="version" <#if '${version}' =='1'> checked </#if> lay-skin="switch" lay-text="开启|关闭" />
        </div>
    </div>

    <div class="layui-form-item layui-hide">
        <input type="button" lay-submit lay-filter="LAY-app-module-update" id="LAY-app-module-update"/>
    </div>
</div>

<script src="${appName}/layuiadmin/layui/layui.js"></script>
<script>
    layui.config({
        base: '${appName}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'form', 'family'], function () {
        var $ = layui.$,
            form = layui.form,
            family = layui.family;
        form.render();

    })
</script>
</body>
</html>