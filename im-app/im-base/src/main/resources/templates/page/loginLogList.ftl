<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>登入日志信息-列表</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="stylesheet" href="${appName}/layuiadmin/layui/css/layui.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/admin.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/im.css" media="all">

</head>
<body>
<div class="layui-fluid">
    <div class="layui-card">
        <div class="layui-form layui-card-header layuiadmin-card-header-auto">
            <div class="layui-form-item">
                <!-- 查询条件 -->
                <div class="layui-inline">
                    <label class="layui-form-label">登入用户</label>
                    <div class="layui-input-block">
                        <select name="userId"></select>
                    </div>
                </div>

                <div class="layui-inline">
                    <label class="layui-form-label">登入状态</label>
                    <div class="layui-input-block">
                        <select name="loginStatus"></select>
                    </div>
                </div>

                <div class="layui-inline">
                    <label class="layui-form-label">登入时间</label>
                    <div class="layui-input-block">
                        <input type="text" class="layui-input" name="loginDate" id="loginDate">
                    </div>
                </div>

                <div class="layui-inline">
                    <label class="layui-form-label">登出时间</label>
                    <div class="layui-input-block">
                        <input type="text" class="layui-input" name="logoutDate" id="logoutDate">
                    </div>
                </div>

            </div>
        </div>

        <div class="layui-card-body">
            <!-- 头部操作按钮 -->
            <div style="padding-bottom: 10px;" id="LAY-app-gift-list-button">
                <button class="layui-btn layuiadmin-btn-loginLog-list" lay-submit
                        lay-filter="LAY-app-loginLoglist-search">
                    <i class="layui-icon layui-icon-search layuiadmin-button-btn"></i>
                </button>
                <button class="layui-btn layuiadmin-btn-loginLog-list" lay-submit
                        lay-filter="LAY-app-loginLoglist-refresh">
                    <i class="layui-icon layui-icon-refresh-1 layuiadmin-button-btn"></i>
                </button>
            </div>

            <!-- 列表数据 -->
            <table id="LAY-app-loginLog-list" lay-filter="LAY-app-loginLog-list"></table>

            <!-- 登入状态 -->
            <script type="text/html" id="loginStatus">
                <button class="layui-btn layui-btn-xs layui-bg-{{ d.loginStatusCode }}">{{ d.loginStatus }}</button>
            </script>

        </div>
    </div>
</div>

<script src="${appName}/layuiadmin/layui/layui.js"></script>
<script>
    layui.config({
        base: '${appName}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'table', 'laydate', 'admin', 'im'], function () {
        var $ = layui.$,
            form = layui.form,
            table = layui.table,
            admin = layui.admin,
            im = layui.im,
            laydate = layui.laydate;

        // 应用名称
        var appName = '${appName}';

        // 业务类型
        var businessType = "loginLog";

        // 请求url
        var url = {
            init: appName + "/loginLog/selectInitData",
            page: appName + "/loginLog/selectPage",
            detail: appName + "/loginLog/detail"
        }

        // 列表字段
        var tableColumn = [[
            {field: "logId", title: "登入日志序列号", sort: false, hide: true},
            {field: "userId", title: "登入用户", sort: true},
            {field: "loginDate", title: "登入时间", align: 'center', sort: true},
            {field: "logoutDate", title: "登出时间", align: 'center', sort: true},
            {field: "loginStatus", title: "登入状态", templet: "#loginStatus", sort: true},
            {field: "loginMessage", title: "登入消息", sort: true}
        ]];

        // 时间选择器
        laydate.render({
            elem: '#loginDate',
            trigger: 'click'
        });

        laydate.render({
            elem: '#logoutDate',
            trigger: 'click'
        });

        // 初始化页面信息
        admin.req({
            url: url.init,
            type: "get",
            dataType: "json",
            done: function (response) {
                if (response.bizResult) {
                    // 设置查询条件
                    im.setCondition("layui-form-item", response.data.condition);
                } else {
                    im.msg(response.msg);
                }
            }
        });


        // 数据详情
        var detail = function (data) {
            var request = {
                logId: data.logId,
                isTranslate: "1"
            }
            im.open({
                type: 2,
                title: im.tips.title.detail,
                content: url.detail + "?" + $.param(request),
                area: [im.size.one, im.size.two],
                resize: im.set.resize
            });
        }

        // 列表数据渲染
        im.initTable({
            elem: "#LAY-app-" + businessType + "-list",
            url: url.page,
            cols: tableColumn
        });

        // 监听查询
        form.on("submit(LAY-app-" + businessType + "list-search)", function (data) {
            //执行重载
            reloadData(im.getValue("layui-form-item"));
        });

        // 监听重置
        form.on("submit(LAY-app-" + businessType + "list-refresh)", function (data) {
            im.clearValue("layui-form-item");
            //执行重载
            reloadData({});
        });

        // 监听列表排序
        table.on("sort(LAY-app-" + businessType + "-list)", function (data) {
            table.reload("LAY-app-" + businessType + "-list", {
                initSort: data,
                where: $.extend({
                    sort: data.field,
                    order: data.type
                }, im.clearBlank(im.getValue("layui-form-item")))
            });
        });

        // 监听双击事件
        table.on("rowDouble(LAY-app-" + businessType + "-list)", function (obj) {
            detail(obj.data);
        });

        // 重载列表数据
        var reloadData = function (data) {
            table.reload("LAY-app-" + businessType + "-list", {
                where: im.clearBlank(data)
            });
        }

    });
</script>
</body>
</html>
