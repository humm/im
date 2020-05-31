<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>角色信息-列表</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, role-scalable=0">
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
                    <label class="layui-form-label">角色代码</label>
                    <div class="layui-input-block">
                        <input type="text" class="layui-input" name="roleCode">
                    </div>
                </div>

                <div class="layui-inline">
                    <label class="layui-form-label">角色名称</label>
                    <div class="layui-input-block">
                        <input type="text" class="layui-input" name="roleName">
                    </div>
                </div>

                <!-- 查询按钮 -->
                <div class="layui-inline layui-inline-button">
                    <button class="layui-btn layuiadmin-btn-role-list" lay-submit
                            lay-filter="LAY-app-rolelist-search">
                        <i class="layui-icon layui-icon-search layuiadmin-button-btn"></i>
                    </button>
                </div>

                <!-- 重置按钮 -->
                <div class="layui-inline layui-inline-button">
                    <button class="layui-btn layuiadmin-btn-role-list" lay-submit
                            lay-filter="LAY-app-rolelist-refresh">
                        <i class="layui-icon layui-icon-refresh-1 layuiadmin-button-btn"></i>
                    </button>
                </div>

            </div>
        </div>

        <div class="layui-card-body">
            <!-- 头部操作按钮 -->
            <div style="padding-bottom: 10px;" id="LAY-app-role-list-button">
                <button class="layui-btn layuiadmin-btn-role-list" data-type="add">新增</button>
                <button class="layui-btn layuiadmin-btn-role-list" data-type="update">修改</button>
                <button class="layui-btn layuiadmin-btn-role-list" data-type="delete">删除</button>
            </div>

            <!-- 列表数据 -->
            <table id="LAY-app-role-list" lay-filter="LAY-app-role-list"></table>

            <script type="text/html" id="table-role-list">
                <a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="update"><i class="layui-icon layui-icon-edit"></i>编辑</a>
                <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="delete"><i class="layui-icon layui-icon-delete"></i>删除</a>
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
    }).use(['index', 'table', 'laydate', 'tree', 'util', 'admin', 'family'], function () {
        var $ = layui.$,
            form = layui.form,
            table = layui.table,
            admin = layui.admin,
            family = layui.family;

        // 应用名称
        var appName = '${appName}';

        // 业务类型
        var businessType = "role";

        // 请求url
        var url = {
            init: appName + "/role/selectInitData",
            page: appName + "/role/selectPage",
            del: appName + "/role/delete",
            save: appName + "/role/save",
            add: appName + "/role/add",
            update: appName + "/role/update",
            detail: appName + "/role/detail",
            check: appName + "/role/checkRoleCode"
        }

        // 列表字段
        var tableColumn = [[
            {type: "checkbox", fixed: "left"},
            {field: "roleId", title: "角色ID", sort: false, hide: true},
            {field: "roleCode", title: "角色代码", sort: true},
            {field: "roleName", title: "角色名称", sort: true},
            {field: "roleMemo", title: "角色备注"},
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
            var roleIds = [];
            for (var i = 0; i < data.length; i++) {
                roleIds.push(data[i].roleId);
            }
            layer.confirm(family.tips.warn.confirmDel, function (index) {
                admin.req({
                    url: url.del,
                    type: "post",
                    data: {roleIds: roleIds.join(",")},
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
                area: [family.size.eight, family.size.nine],
                btn: [family.tips.btn.save, family.tips.btn.cancel],
                resize: family.set.resize,
                yes: function (e, t) {
                    save(e, t, family.operate.add, data);
                }
            });
        }

        // 数据修改
        var update = function (data) {
            var request = {
                roleId: data.roleId,
                isTranslate: "0"
            }
            family.open({
                type: 2,
                title: family.tips.title.update,
                content: url.update + "?" + $.param(request),
                area: [family.size.eight, family.size.nine],
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
                roleId: data.roleId,
                isTranslate: "1"
            }
            family.open({
                type: 2,
                title: family.tips.title.detail,
                content: url.detail + "?" + $.param(request),
                area: [family.size.eight, family.size.nine],
                resize: family.set.resize
            });
        }

        // 数据保存
        var save = function (e, t, type, data) {
            var iframe = window["layui-layer-iframe" + e],
                button = t.find("iframe").contents().find("#LAY-app-" + businessType + "-" + type);
            iframe.layui.form.on("submit(LAY-app-" + businessType + "-" + type + ")", function (data) {
                // 校验角色代码格式
                var param = family.clearBlank(data.field);
                var reg = /^[0-9a-zA-Z_]+$/;
                if (!reg.test(param.roleCode)) {
                    family.msg(family.tips.msg.isNumberOrLetter);
                    return;
                }
                var roleIsExist = false;
                // 校验角色代码是否存在
                admin.req({
                    url: url.check,
                    type: "get",
                    async: false,
                    data: family.clearBlank(data.field),
                    done: function (response) {
                        if (response.bizResult) {
                            if (response.data) {
                                roleIsExist = true;
                            }
                        } else {
                            family.msg(response.msg);
                        }
                    }
                });
                if(!roleIsExist){
                    family.msg(family.tips.msg.roleIsExist);
                    return;
                }
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
