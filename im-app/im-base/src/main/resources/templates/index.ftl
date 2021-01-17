<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>${appDescribe}</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="stylesheet" href="${appName}/layuiadmin/layui/css/layui.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/admin.css" media="all">
    <link rel="icon" href="${appName}/layuiadmin/style/res/logo-black.png" type="image/png" />

</head>
<body class="layui-layout-body">

<div id="LAY_app">
    <div class="layui-layout layui-layout-admin">
        <div class="layui-header">
            <!-- 头部区域 -->
            <ul class="layui-nav layui-layout-left">
                <li class="layui-nav-item layadmin-flexible" lay-unselect>
                    <a href="javascript:;" layadmin-event="flexible" title="侧边伸缩">
                        <i class="layui-icon layui-icon-shrink-right" id="LAY_app_flexible"></i>
                    </a>
                </li>
            </ul>
            <ul class="layui-nav layui-layout-right" lay-filter="layadmin-layout-right" style="margin-right: 10px;">

                <li class="layui-nav-item" lay-unselect>
                    <a href="javascript:;" lay-text="模块设置" id="module">
                        <i class="layui-icon layui-icon-app"></i>
                    </a>
                </li>
                <li class="layui-nav-item" lay-unselect>
                    <a lay-href="notice/list?menuId=20190000000017" lay-text="消息通知">
                        <i class="layui-icon layui-icon-notice"></i>
                        <span class="" style="top: 35%; left: 40%; border-radius: 50%;" id="readNum">
                    </a>
                </li>
                <li class="layui-nav-item" lay-unselect>
                    <a href="javascript:;">
                        <cite id="userName"></cite>
                    </a>
                    <dl class="layui-nav-child">
                        <dd><a lay-href="user/password?menuId=skip">修改密码</a></dd>
                        <hr>
                        <dd id="logout" style="text-align: center;"><a>退出</a></dd>
                    </dl>
                </li>
            </ul>
        </div>

        <!-- 侧边菜单 -->
        <div class="layui-side layui-side-menu">
            <div class="layui-side-scroll">
                <div class="layui-logo">
                    <span>${appDescribe}</span>
                </div>
                <ul class="layui-nav layui-nav-tree" lay-shrink="all"
                    id="LAY-system-side-menu"
                    lay-filter="layadmin-system-side-menu">
                </ul>
            </div>
        </div>

        <!-- 页面标签 -->
        <div class="layadmin-pagetabs" id="LAY_app_tabs">
            <div class="layui-icon layadmin-tabs-control layui-icon-prev" layadmin-event="leftPage"></div>
            <div class="layui-icon layadmin-tabs-control layui-icon-next" layadmin-event="rightPage"></div>
            <div class="layui-icon layadmin-tabs-control layui-icon-down">
                <ul class="layui-nav layadmin-tabs-select" lay-filter="layadmin-pagetabs-nav">
                    <li class="layui-nav-item" lay-unselect>
                        <a href="javascript:;"></a>
                        <dl class="layui-nav-child layui-anim-fadein">
                            <dd layadmin-event="closeThisTabs"><a href="javascript:;">关闭当前标签页</a></dd>
                            <dd layadmin-event="closeOtherTabs"><a href="javascript:;">关闭其它标签页</a></dd>
                            <dd layadmin-event="closeAllTabs"><a href="javascript:;">关闭全部标签页</a></dd>
                        </dl>
                    </li>
                </ul>
            </div>
            <div class="layui-tab" lay-unauto lay-allowClose="true" lay-filter="layadmin-layout-tabs">
                <ul class="layui-tab-title" id="LAY_app_tabsheader">
                    <li lay-id="console" lay-attr="console" class="layui-this"><i
                                class="layui-icon layui-icon-home"></i></li>
                </ul>
            </div>
        </div>


        <!-- 主体内容 -->
        <div class="layui-body" id="LAY_app_body">
            <div class="layadmin-tabsbody-item layui-show">
                <iframe src="console" frameborder="0" class="layadmin-iframe"></iframe>
            </div>
        </div>

        <!-- 辅助元素，一般用于移动设备下遮罩 -->
        <div class="layadmin-body-shade" layadmin-event="shade"></div>
    </div>
</div>

<script src="${appName}/layuiadmin/layui/layui.js"></script>
<script>
    layui.config({
        base: '${appName}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'admin', 'element', 'im'], function () {
        var $ = layui.$,
            admin = layui.admin,
            element = layui.element,
            im = layui.im;

        // 应用名称
        var appName = '${appName}';

        // 设置登录人信息
        $("#userName").text('${userName}');

        var url = {
            init: appName + "/menu/initMenu",
            logout: appName + "/user/logout",
            login: appName + "/login",
            module: appName + "/module",
            save: appName + "/module/save"
        };

        // 子菜单
        var childrenMenu = "";

        // 初始化页面信息
        admin.req({
            url: url.init,
            type: "get",
            dataType: "json",
            done: function (response) {
                if (response.bizResult) {
                    initMenu(response.data);
                }
            }
        });

        // 初始化菜单
        function initMenu(data) {
            var menu = '';
            menu += '<li data-name="console" class="layui-nav-item layui-this">';
            menu += '    <a href="javascript:;" lay-tips="首页" lay-direction="2" lay-href="console">';
            menu += '       <i class="layui-icon layui-icon-home"></i>';
            menu += '       <cite>首页</cite>';
            menu += '    </a>';
            menu += '</li>';
            if (!$.isEmptyObject(data)) {
                data.forEach(function (item) {
                    if (!im.isBlank(item.menuUrl)) {
                        if (item.menuUrl != '#') {
                            // 单页面菜单
                            menu += '<li data-name="' + item.menuId + '" class="layui-nav-item">';
                            menu += '    <a href="javascript:;" lay-tips="' + item.menuTitle + '" lay-direction="2" lay-href="' + item.menuUrl + '?menuId=' + item.menuId + '">';
                            menu += '       <i class="layui-icon ' + item.menuIcon + '"></i>';
                            menu += '       <cite>' + item.menuTitle + '</cite>';
                            menu += '    </a>';
                            menu += '</li>';
                        } else {
                            // 父页面菜单
                            menu += '<li data-name="' + item.menuId + '" class="layui-nav-item">';
                            menu += '    <a href="javascript:;" lay-tips="' + item.menuTitle + '" lay-direction="2">';
                            menu += '       <i class="layui-icon ' + item.menuIcon + '"></i>';
                            menu += '       <cite>' + item.menuTitle + '</cite>';
                            menu += '    </a>';
                            if(!im.isBlank(item.children)){
                                menu += '<dl class="layui-nav-child">';
                                childrenMenu = "";
                                initChildrenMenu(item.children,);
                                menu += childrenMenu;
                                menu += '</dl>';
                            }
                            menu += '</li>';
                        }
                    }
                });
            }
            $("#LAY-system-side-menu").append(menu);
            // 渲染菜单
            element.init();
        }
        // 初始化子菜单
        function initChildrenMenu(data) {
            data.forEach(function (subItem) {
                if(!im.isBlank(subItem.children)){
                    childrenMenu += '<dd data-name="' + subItem.menuId + '">';
                    childrenMenu += '   <a href="javascript:;">' + subItem.menuTitle + '</a>';
                    childrenMenu += '   <dl class="layui-nav-child">';
                    initChildrenMenu(subItem.children);
                    childrenMenu += '   </dl>';
                    childrenMenu += '</dd>';
                }else{
                    childrenMenu += '<dd data-name="' + subItem.menuId + '">';
                    childrenMenu += '   <a lay-href="' + subItem.menuUrl + '?menuId=' + subItem.menuId + '">' + subItem.menuTitle + '</a>';
                    childrenMenu += '</dd>';
                }
            });
        }

        // 绑定退出事件
        $(document).on('click', '#logout', function () {
            admin.req({
                url: url.logout,
                type: "post",
                dataType: "json",
                done: function (response) {
                    if (response.bizResult) {
                        parent.location.href = url.login;
                    }
                }
            });
        });

        // 绑定模块编辑事件
        $(document).on('click', '#module', function () {
            im.open({
                type: 2,
                title: "模块设置",
                content: url.module,
                area:  [im.size.five, im.size.six],
                btn: [im.tips.btn.save, im.tips.btn.cancel],
                resize: im.set.resize,
                yes: function (e, t) {
                    save(e, t, im.operate.update);
                }
            });
        });

        // 数据保存
        var save = function (e, t, type) {
            var iframe = window["layui-layer-iframe" + e],
                button = t.find("iframe").contents().find("#LAY-app-module-update");
                iframe.layui.form.on("submit(LAY-app-module-update)", function (data) {
                var request = {
                    user: im.isBlank(data.field["user"]) ? 'off' : data.field["user"],
                    tips: im.isBlank(data.field["tips"]) ? 'off' : data.field["tips"],
                    login: im.isBlank(data.field["login"]) ? 'off' : data.field["login"],
                    version: im.isBlank(data.field["version"]) ? 'off' : data.field["version"],
                    register: im.isBlank(data.field["register"]) ? 'off' : data.field["register"]
                };
                admin.req({
                    url: url.save,
                    type: "post",
                    data: im.clearBlank(request),
                    done: function (response) {
                        if (response.bizResult) {
                            setTimeout(function () {
                                layer.close(e);
                                im.msg(response.msg);
                            }, 500);
                        } else {
                            im.msg(response.msg);
                        }
                    }
                });
            }), button.trigger("click");
        }

    });
</script>
</body>
</html>


