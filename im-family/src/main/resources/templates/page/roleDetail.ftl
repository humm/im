<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>角色信息-详情</title>
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
        <label class="layui-form-label">角色代码</label>
        <div class="layui-input-inline">
            <input type="text" name="roleCode" class="layui-input layui-detail" disabled="disabled" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">角色名称</label>
        <div class="layui-input-inline">
            <input type="text" name="roleName" class="layui-input layui-detail" disabled="disabled" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">数据权限</label>
        <div class="layui-input-inline">
            <input type="text" name="dataAuthority" class="layui-input layui-detail" disabled="disabled" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">菜单信息</label>
        <div class="layui-input-inline">
            <div id="menuTree" class="demo-tree-more"></div>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">角色备注</label>
        <div class="layui-input-inline">
            <textarea name="roleMemo" class="layui-textarea layui-detail" disabled="disabled"></textarea>
        </div>
    </div>
</div>

<script src="${appName}/layuiadmin/layui/layui.js"></script>
<script>
    layui.config({
        base: '${appName}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'form', 'admin', 'tree', 'util', 'family'], function () {
        var $ = layui.$,
            form = layui.form,
            admin = layui.admin,
            tree = layui.tree,
            util = layui.util,
            family = layui.family;

        // 应用名称
        var appName = '${appName}';

        var request = {
            roleId: family.getUrlParameter("roleId"),
            isTranslate: family.getUrlParameter("isTranslate"),
            disabled: 0
        }

        var url = {
            init: appName + "/role/selectInitData?" + $.param(request),
            load: appName + "/role/selectOne?" + $.param(request)
        };

        // 初始化页面信息
        admin.req({
            url: url.init,
            type: "get",
            dataType: "json",
            done: function (response) {
                if (response.bizResult) {
                    family.setCondition("layui-form", response.data.condition);
                    response.data.dataAuthority = response.data.dataAuthority == '1' ? "开启" : "关闭";
                    family.setValue("layui-form", response.data);
                    form.render();
                    //加载菜单树
                    tree.render({
                        elem: '#menuTree',
                        id: 'menuTree',
                        data: response.data.menuList,
                        showLine: false, // 是否显示连接线
                        showCheckbox: true,  //是否显示复选框
                        isJump: false, //是否允许点击节点时弹出新窗口跳转
                        oncheck: function (obj) {
                            // 复选框选择事件
                            getSelectedMenu();
                        }
                    });
                    getSelectedMenu();
                    // 数据回填
                    admin.req({
                        url: url.load,
                        type: "get",
                        dataType: "json",
                        done: function (response) {
                            if (response.bizResult) {
                                family.setValue("layui-form", response.data);
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

        function getSelectedMenu() {
            var selectedMenu = [];
            try {
                var selected = tree.getChecked('menuTree');
                for (var i = 0; i < selected.length; i++) {
                    selectedMenu.push(selected[i].id);
                    var children = selected[i].children;
                    if (!$.isEmptyObject(children)) {
                        for (var j = 0; j < children.length; j++) {
                            selectedMenu.push(children[j].id);
                        }
                    }
                }
            } catch (e) {}
            $("input[name='menuId']").val(selectedMenu.join(","));
        }
    })
</script>
</body>
</html>