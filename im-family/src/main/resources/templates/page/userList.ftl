<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>用户信息-列表</title>
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
    <div class="layui-card">
        <div class="layui-form layui-card-header layuiadmin-card-header-auto">
            <div class="layui-form-item">
                <!-- 查询条件 -->
                <div class="layui-inline">
                    <label class="layui-form-label">用户代码</label>
                    <div class="layui-input-block">
                        <input type="text" class="layui-input" name="userCode">
                    </div>
                </div>

                <div class="layui-inline">
                    <label class="layui-form-label">用户名称</label>
                    <div class="layui-input-block">
                        <input type="text" class="layui-input" name="userName">
                    </div>
                </div>

                <div class="layui-inline">
                    <label class="layui-form-label">用户状态</label>
                    <div class="layui-input-block">
                        <select name="userStatus"></select>
                    </div>
                </div>

                <!-- 查询按钮 -->
                <div class="layui-inline layui-inline-button">
                    <button class="layui-btn layuiadmin-btn-user-list" lay-submit
                            lay-filter="LAY-app-userlist-search">
                        <i class="layui-icon layui-icon-search layuiadmin-button-btn"></i>
                    </button>
                </div>

                <!-- 重置按钮 -->
                <div class="layui-inline layui-inline-button">
                    <button class="layui-btn layuiadmin-btn-user-list" lay-submit
                            lay-filter="LAY-app-userlist-refresh">
                        <i class="layui-icon layui-icon-refresh-1 layuiadmin-button-btn"></i>
                    </button>
                </div>
            </div>
        </div>

        <div class="layui-card-body">
            <!-- 头部操作按钮 -->
            <div style="padding-bottom: 10px;" id="LAY-app-user-list-button">
                <button class="layui-btn layuiadmin-btn-user-list" data-type="add">新增</button>
                <button class="layui-btn layuiadmin-btn-user-list" data-type="update">修改</button>
                <button class="layui-btn layuiadmin-btn-user-list" data-type="delete">删除</button>
                <button class="layui-btn layuiadmin-btn-user-list" data-type="reset">重置用户密码</button>
            </div>

            <!-- 列表数据 -->
            <table id="LAY-app-user-list" lay-filter="LAY-app-user-list"></table>

            <!-- 用户状态 -->
            <script type="text/html" id="userStatus">
                <button class="layui-btn layui-btn-xs layui-bg-{{ d.userStatusCode }}">{{ d.userStatus }}</button>
            </script>

            <!-- 用户类型 -->
            <script type="text/html" id="userType">
                {{#  if(d.userCode == 'admin'){ }}
                <button class="layui-btn layui-bg-1 layui-btn-xs">系统用户</button>
                {{#  } else{ }}
                <button class="layui-btn layui-bg-2 layui-btn-xs">普通用户</button>
                {{#  } }}
            </script>

            <script type="text/html" id="table-user-list">
                {{#  if(d.userCode != 'admin'){ }}
                <a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="update"><i class="layui-icon layui-icon-edit"></i>编辑</a>
                <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="delete"><i class="layui-icon layui-icon-delete"></i>删除</a>
                {{#  } else{ }}
                {{#  } }}
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
    }).use(['index', 'table', 'laydate', 'admin', 'family'], function () {
        var $ = layui.$,
            form = layui.form,
            table = layui.table,
            admin = layui.admin,
            family = layui.family;

        // 应用名称
        var appName = '${appName}';

        // 业务类型
        var businessType = "user";

        // 请求url
        var url = {
            init: appName + "/user/selectInitData",
            page: appName + "/user/selectPage",
            del: appName + "/user/delete",
            reset: appName + "/user/reset",
            save: appName + "/user/save",
            add: appName + "/user/add",
            update: appName + "/user/update",
            detail: appName + "/user/detail",
            check: appName + "/user/checkUserCode"
        }

        // 列表字段
        var tableColumn = [[
            {type: "checkbox", fixed: "left"},
            {field: "userId", title: "用户ID", sort: false, hide: true},
            {field: "userCode", title: "用户代码", sort: true},
            {field: "userName", title: "用户名称", sort: true},
            {field: "userStatus", title: "用户状态", align: "center", templet: "#userStatus", sort: true},
            {field: "userCode", title: "用户类型", align: "center", templet: "#userType"},
            {field: "userMemo", title: "用户备注"},
            {title:"操作", width:150, align:"center", fixed:"right", toolbar:"#table-" + businessType + "-list"}
        ]];

        // 初始化页面信息
        admin.req({
            url: url.init,
            type: "get",
            dataType: "json",
            done: function (response) {
                if (response.bizResult) {
                    // 设置查询条件
                    family.setCondition("layui-form-item", response.data.condition);
                } else {
                    family.msg(response.msg);
                }
            }
        });

        // 数据删除
        var del = function (data) {
            var userIds = [];
            var hasAdmin = false;
            for (var i = 0; i < data.length; i++) {
                userIds.push(data[i].userId);
                if (family.config.adminCode == data[i].userCode) {
                    family.msg(family.tips.msg.systemUserNotDelete);
                    hasAdmin = true;
                    return;
                }
            }
            if (hasAdmin) {
                return;
            }
            layer.confirm(family.tips.warn.confirmDel, function (index) {
                admin.req({
                    url: url.del,
                    type: "post",
                    data: {userIds: userIds.join(",")},
                    done: function (response) {
                        if (response.bizResult) {
                            setTimeout(function () {
                                layer.close(index);
                                reloadData(family.getValue("layui-form-item"));
                                family.msg(response.msg);
                            }, 500);
                        } else {
                            family.msg(response.msg);
                        }
                    }
                });
            });
        }

        // 重置用户密码
        var reset = function (data) {
            var userIds = [];
            for (var i = 0; i < data.length; i++) {
                userIds.push(data[i].userId);
            }
            layer.confirm(family.tips.warn.confirmResetPassword, function (index) {
                admin.req({
                    url: url.reset,
                    type: "post",
                    data: {userIds: userIds.join(",")},
                    done: function (response) {
                        if (response.bizResult) {
                            setTimeout(function () {
                                layer.close(index);
                                reloadData(family.getValue("layui-form-item"));
                                family.msg(response.msg);
                            }, 500);
                        } else {
                            family.msg(response.msg);
                        }
                    }
                });
            });
        }

        // 数据新增
        var add = function (data) {
            family.open({
                type: 2,
                title: family.tips.title.add,
                content: url.add,
                area: [family.size.one, family.size.two],
                btn: [family.tips.btn.save, family.tips.btn.cancel],
                resize: family.set.resize,
                yes: function (e, t) {
                    save(e, t, family.operate.add, data);
                }
            });
        }

        // 数据修改
        var update = function (data) {
            if (family.config.adminCode == data.userCode) {
                family.msg(family.tips.msg.systemUserNotUpdate);
                return;
            }
            var request = {
                userId: data.userId,
                isTranslate: "0"
            }
            family.open({
                type: 2,
                title: family.tips.title.update,
                content: url.update + "?" + $.param(request),
                area: [family.size.one, family.size.two],
                btn: [family.tips.btn.save, family.tips.btn.cancel],
                resize: family.set.resize,
                yes: function (e, t) {
                    save(e, t, family.operate.update, data);
                }
            });
        }

        // 数据详情
        var detail = function (data) {
            var request = {
                userId: data.userId,
                isTranslate: "1"
            }
            family.open({
                type: 2,
                title: family.tips.title.detail,
                content: url.detail + "?" + $.param(request),
                area: [family.size.one, family.size.two],
                resize: family.set.resize
            });
        }

        // 数据保存
        var save = function (e, t, type, data) {
            var iframe = window["layui-layer-iframe" + e],
                button = t.find("iframe").contents().find("#LAY-app-" + businessType + "-" + type);
            // 获取角色信息
            var roleId = [];
            iframe.document.getElementsByName("roleId").forEach(function (item) {
                if (item.checked) {
                    roleId.push(item.value);
                }
            });
            iframe.layui.form.on("submit(LAY-app-" + businessType + "-" + type + ")", function (data) {
                // 校验用户代码格式
                var param = family.clearBlank(data.field);
                var reg = /^[0-9a-zA-Z_]+$/;
                if (!reg.test(param.userCode)) {
                    family.msg(family.tips.msg.isNumberOrLetter);
                    return;
                }
                var userIsExist = false;
                // 校验用户代码是否存在
                admin.req({
                    url: url.check,
                    type: "get",
                    async: false,
                    data: family.clearBlank(data.field),
                    done: function (response) {
                        if (response.bizResult) {
                            if (response.data) {
                                userIsExist = true;
                            }
                        } else {
                            family.msg(response.msg);
                        }
                    }
                });
                if (!userIsExist) {
                    family.msg(family.tips.msg.userIsExist);
                    return;
                }
                data.field.roleId = roleId.join(",");
                admin.req({
                    url: url.save,
                    type: "post",
                    data: family.clearBlank(data.field),
                    done: function (response) {
                        if (response.bizResult) {
                            setTimeout(function () {
                                reloadData(family.getValue("layui-form-item"));
                                layer.close(e);
                                family.msg(response.msg);
                            }, 500);
                        } else {
                            family.msg(response.msg);
                        }
                    }
                });
            }), button.trigger("click");
        }

        // 列表数据渲染
        family.initTable({
            elem: "#LAY-app-" + businessType + "-list",
            url: url.page,
            cols: tableColumn
        });

        // 行数据按钮事件绑定 暂未启用
        table.on("tool(LAY-app-" + businessType + "-list)", function (obj) {
            // 获取数据值
            var data = obj.data;
            switch (obj.event) {
                case family.operate.delete:
                    var convertData = new Array();
                    convertData.push(data);
                    del(convertData);
                    break;
                case family.operate.detail:
                    detail(data);
                    break;
                case family.operate.add:
                    add(data);
                    break;
                case family.operate.update:
                    update(data);
                    break;
                case family.operate.reset:
                    var convertData = new Array();
                    convertData.push(data);
                    reset(convertData);
                    break;
                default:
                    family.msg(family.tips.msg.notSupportEvent);
                    break;
            }
        });

        // table按钮事件监听
        $(".layui-btn.layuiadmin-btn-" + businessType + "-list").on("click", function () {

            // 获取选中的数据
            var checkData = table.checkStatus("LAY-app-" + businessType + "-list").data;

            var type = $(this).data("type");
            if (family.isBlank(type)) {
                return;
            }
            switch (type) {
                case family.operate.add:
                    add();
                    break;
                case family.operate.update:
                    if (checkData.length === 0) {
                        return family.msg(family.tips.warn.notSelect);
                    }
                    if (checkData.length > 1) {
                        return family.msg(family.tips.warn.selectOne);
                    }
                    update(checkData[0]);
                    break;
                case family.operate.delete:
                    if (checkData.length === 0) {
                        return family.msg(family.tips.warn.notSelect);
                    }
                    del(checkData);
                    break;
                case family.operate.reset:
                    if (checkData.length === 0) {
                        return family.msg(family.tips.warn.notSelect);
                    }
                    reset(checkData);
                    break;
                default:
                    family.msg(family.tips.msg.notSupportEvent);
            }
        });

        // 监听查询
        form.on("submit(LAY-app-" + businessType + "list-search)", function (data) {
            //执行重载
            reloadData(family.getValue("layui-form-item"));
        });

        // 监听重置
        form.on("submit(LAY-app-" + businessType + "list-refresh)", function (data) {
            family.clearValue("layui-form-item");
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
                }, family.clearBlank(family.getValue("layui-form-item")))
            });
        });

        // 监听双击事件
        table.on("rowDouble(LAY-app-" + businessType + "-list)", function (obj) {
            detail(obj.data);
        });

        // 重载列表数据
        var reloadData = function (data) {
            table.reload("LAY-app-" + businessType + "-list", {
                where: family.clearBlank(data)
            });
        }

    });
</script>
</body>
</html>
