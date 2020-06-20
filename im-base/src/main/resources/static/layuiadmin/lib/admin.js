/** layuiAdmin.std-v1.0.0 LPPL License By http://www.layui.com/admin/ */
;layui.define(["view"], function (e) {
    var a = layui.jquery, i = layui.laytpl, t = layui.element, l = layui.setter, n = layui.view, s = layui.device(),
        r = a(window), o = a("body"), u = a("#" + l.container), d = "layui-show", c = "layui-hide", y = "layui-this",
        f = "layui-disabled", h = "#LAY_app_body", m = "LAY_app_flexible", p = "layadmin-layout-tabs",
        v = "layadmin-side-spread-sm", b = "layadmin-tabsbody-item", g = "layui-icon-shrink-right",
        x = "layui-icon-spread-left", C = "layadmin-side-shrink", k = "LAY-system-side-menu", F = {
            v: "1.2.1 std", req: n.req, screen: function () {
                var e = r.width();
                return e >= 1200 ? 3 : e >= 992 ? 2 : e >= 768 ? 1 : 0
            }, exit: n.exit, sideFlexible: function (e) {
                var i = u, t = a("#" + m), n = F.screen();
                "spread" === e ? (t.removeClass(x).addClass(g), n < 2 ? i.addClass(v) : i.removeClass(v), i.removeClass(C)) : (t.removeClass(g).addClass(x), n < 2 ? i.removeClass(C) : i.addClass(C), i.removeClass(v)), layui.event.call(this, l.MOD_NAME, "side({*})", {status: e})
            }, escape: function (e) {
                return String(e || "").replace(/&(?!#?[a-zA-Z0-9]+;)/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/'/g, "&#39;").replace(/"/g, "&quot;")
            }, on: function (e, a) {
                return layui.onevent.call(this, l.MOD_NAME, e, a)
            }, popup: n.popup, popupRight: function (e) {
                return F.popup.index = layer.open(a.extend({
                    type: 1,
                    id: "LAY_adminPopupR",
                    anim: -1,
                    title: !1,
                    closeBtn: !1,
                    offset: "r",
                    shade: .1,
                    shadeClose: !0,
                    skin: "layui-anim layui-anim-rl layui-layer-adminRight",
                    area: "300px"
                }, e))
            }, theme: function (e) {
                var t = (l.theme, layui.data(l.tableName)), n = "LAY_layadmin_theme", s = document.createElement("style"),
                    r = i([".layui-side-menu,", ".layadmin-pagetabs .layui-tab-title li:after,", ".layadmin-pagetabs .layui-tab-title li.layui-this:after,", ".layui-layer-admin .layui-layer-title,", ".layadmin-side-shrink .layui-side-menu .layui-nav>.layui-nav-item>.layui-nav-child", "{background-color:{{d.color.main}} !important;}", ".layui-nav-tree .layui-this,", ".layui-nav-tree .layui-this>a,", ".layui-nav-tree .layui-nav-child dd.layui-this,", ".layui-nav-tree .layui-nav-child dd.layui-this a", "{background-color:{{d.color.selected}} !important;}", ".layui-layout-admin .layui-logo{background-color:{{d.color.logo || d.color.main}} !important;}", "{{# if(d.color.header){ }}", ".layui-layout-admin .layui-header{background-color:{{ d.color.header }};}", ".layui-layout-admin .layui-header a,", ".layui-layout-admin .layui-header a cite{color: #f8f8f8;}", ".layui-layout-admin .layui-header a:hover{color: #fff;}", ".layui-layout-admin .layui-header .layui-nav .layui-nav-more{border-top-color: #fbfbfb;}", ".layui-layout-admin .layui-header .layui-nav .layui-nav-mored{border-color: transparent; border-bottom-color: #fbfbfb;}", ".layui-layout-admin .layui-header .layui-nav .layui-this:after, .layui-layout-admin .layui-header .layui-nav-bar{background-color: #fff; background-color: rgba(255,255,255,.5);}", ".layadmin-pagetabs .layui-tab-title li:after{display: none;}", "{{# } }}"].join("")).render(e = a.extend({}, t.theme, e)),
                    u = document.getElementById(n);
                "styleSheet" in s ? (s.setAttribute("type", "text/css"), s.styleSheet.cssText = r) : s.innerHTML = r, s.id = n, u && o[0].removeChild(u), o[0].appendChild(s), o.attr("layadmin-themealias", e.color.alias), t.theme = t.theme || {}, layui.each(e, function (e, a) {
                    t.theme[e] = a
                }), layui.data(l.tableName, {key: "theme", value: t.theme})
            }, initTheme: function (e) {
                var a = l.theme;
                e = e || 0, a.color[e] && (a.color[e].index = e, F.theme({color: a.color[e]}))
            }, tabsPage: {}, tabsBody: function (e) {
                return a(h).find("." + b).eq(e || 0)
            }, tabsBodyChange: function (e, a) {
                a = a || {}, F.tabsBody(e).addClass(d).siblings().removeClass(d), P.rollPage("auto", e), layui.event.call(this, l.MOD_NAME, "tabsPage({*})", {
                    url: a.url,
                    text: a.text
                })
            }, resize: function (e) {
                var a = layui.router(), i = a.path.join("-");
                r.off("resize", F.resizeFn[i]), e(), F.resizeFn[i] = e, r.on("resize", F.resizeFn[i])
            }, resizeFn: {}, runResize: function () {
                var e = layui.router(), a = e.path.join("-");
                F.resizeFn[a] && F.resizeFn[a]()
            }, delResize: function () {
                var e = layui.router(), a = e.path.join("-");
                r.off("resize", F.resizeFn[a]), delete F.resizeFn[a]
            }, closeThisTabs: function () {
                F.tabsPage.index && a(z).eq(F.tabsPage.index).find(".layui-tab-close").trigger("click")
            }
        }, P = F.events = {
            flexible: function (e) {
                var a = e.find("#" + m), i = a.hasClass(x);
                F.sideFlexible(i ? "spread" : null)
            }, refresh: function () {
                var e = ".layadmin-iframe", i = a("." + b).length;
                F.tabsPage.index >= i && (F.tabsPage.index = i - 1);
                var t = F.tabsBody(F.tabsPage.index).find(e);
                t[0].contentWindow.location.reload(!0)
            }, serach: function (e) {
                e.off("keypress").on("keypress", function (a) {
                    if (this.value.replace(/\s/g, "") && 13 === a.keyCode) {
                        var i = e.attr("lay-action"), t = e.attr("lay-text") || "搜索";
                        i += this.value, t = t + ' <span style="color: #FF5722;">' + F.escape(this.value) + "</span>", layui.index.openTabsPage(i, t), P.serach.keys || (P.serach.keys = {}), P.serach.keys[F.tabsPage.index] = this.value, this.value === P.serach.keys[F.tabsPage.index] && P.refresh(e), this.value = ""
                    }
                })
            },
            rollPage: function (e, i) {
                var t = a("#LAY_app_tabsheader"), l = t.children("li"), n = (t.prop("scrollWidth"), t.outerWidth()),
                    s = parseFloat(t.css("left"));
                if ("left" === e) {
                    if (!s && s <= 0) return;
                    var r = -s - n;
                    l.each(function (e, i) {
                        var l = a(i), n = l.position().left;
                        if (n >= r) return t.css("left", -n), !1
                    })
                } else "auto" === e ? !function () {
                    var e, r = l.eq(i);
                    if (r[0]) {
                        if (e = r.position().left, e < -s) return t.css("left", -e);
                        if (e + r.outerWidth() >= n - s) {
                            var o = e + r.outerWidth() - (n - s);
                            l.each(function (e, i) {
                                var l = a(i), n = l.position().left;
                                if (n + s > 0 && n - s > o) return t.css("left", -n), !1
                            })
                        }
                    }
                }() : l.each(function (e, i) {
                    var l = a(i), r = l.position().left;
                    if (r + l.outerWidth() >= n - s) return t.css("left", -r), !1
                })
            }, leftPage: function () {
                P.rollPage("left")
            }, rightPage: function () {
                P.rollPage()
            }, closeThisTabs: function () {
                F.closeThisTabs()
            }, closeOtherTabs: function (e) {
                var i = "LAY-system-pagetabs-remove";
                "all" === e ? (a(z + ":gt(0)").remove(), a(h).find("." + b + ":gt(0)").remove(), a(z).eq(0).trigger("click")) : (a(z).each(function (e, t) {
                    e && e != F.tabsPage.index && (a(t).addClass(i), F.tabsBody(e).addClass(i))
                }), a("." + i).remove())
            }, closeAllTabs: function () {
                P.closeOtherTabs("all")
            }, shade: function () {
                F.sideFlexible()
            }, im: function () {
                F.popup({
                    id: "LAY-popup-layim-demo",
                    shade: 0,
                    area: ["800px", "300px"],
                    title: "面板外的操作示例",
                    offset: "lb",
                    success: function () {
                        layui.view(this.id).render("layim/demo").then(function () {
                            layui.use("im")
                        })
                    }
                })
            }
        };
    !function () {
        var e = layui.data(l.tableName);
        e.theme ? F.theme(e.theme) : l.theme && F.initTheme(l.theme.initColorIndex), "pageTabs" in layui.setter || (layui.setter.pageTabs = !0), l.pageTabs || (a("#LAY_app_tabs").addClass(c), u.addClass("layadmin-tabspage-none")), s.ie && s.ie < 10 && n.error("IE" + s.ie + "下访问可能不佳，推荐使用：Chrome / Firefox / Edge 等高级浏览器", {
            offset: "auto",
            id: "LAY_errorIE"
        })
    }(), t.on("tab(" + p + ")", function (e) {
        F.tabsPage.index = e.index
    }), F.on("tabsPage(setMenustatus)", function (e) {
        var i = e.url, t = function (e) {
            return {list: e.children(".layui-nav-child"), a: e.children("*[lay-href]")}
        }, l = a("#" + k), n = "layui-nav-itemed", s = function (e) {
            e.each(function (e, l) {
                var s = a(l), r = t(s), o = r.list.children("dd"), u = i === r.a.attr("lay-href");
                if (o.each(function (e, l) {
                    var s = a(l), r = t(s), o = r.list.children("dd"), u = i === r.a.attr("lay-href");
                    if (o.each(function (e, l) {
                        var s = a(l), r = t(s), o = i === r.a.attr("lay-href");
                        if (o) {
                            var u = r.list[0] ? n : y;
                            return s.addClass(u).siblings().removeClass(u), !1
                        }
                    }), u) {
                        var d = r.list[0] ? n : y;
                        return s.addClass(d).siblings().removeClass(d), !1
                    }
                }), u) {
                    var d = r.list[0] ? n : y;
                    return s.addClass(d).siblings().removeClass(d), !1
                }
            })
        };
        l.find("." + y).removeClass(y), F.screen() < 2 && F.sideFlexible(), s(l.children("li"))
    }), t.on("nav(layadmin-system-side-menu)", function (e) {
        e.siblings(".layui-nav-child")[0] && u.hasClass(C) && (F.sideFlexible("spread"), layer.close(e.data("index"))), F.tabsPage.type = "nav"
    }), t.on("nav(layadmin-pagetabs-nav)", function (e) {
        var a = e.parent();
        a.removeClass(y), a.parent().removeClass(d)
    });
    var A = function (e) {
        var a = (e.attr("lay-id"), e.attr("lay-attr")), i = e.index();
        F.tabsBodyChange(i, {url: a})
    }, z = "#LAY_app_tabsheader>li";
    o.on("click", z, function () {
        var e = a(this), i = e.index();
        F.tabsPage.type = "tab", F.tabsPage.index = i, A(e)
    }), t.on("tabDelete(" + p + ")", function (e) {
        var i = a(z + ".layui-this");
        e.index && F.tabsBody(e.index).remove(), A(i), F.delResize()
    }), o.on("click", "*[lay-href]", function () {
        var e = a(this), i = e.attr("lay-href"), t = e.attr("lay-text");
        layui.router();
        F.tabsPage.elem = e;
        var l = parent === self ? layui : top.layui;
        var tabName = e.text();
        if (i.indexOf("menuTitle") != -1) {
            var reg = new RegExp("(^|&)" + "menuTitle" + "=([^&]*)(&|$)", "i");
            var r = i.match(reg);
            if (r != null) {
                tabName = unescape(r[2]);
            }
        }
        l.index.openTabsPage(i, t || tabName)
    }), o.on("click", "*[layadmin-event]", function () {
        var e = a(this), i = e.attr("layadmin-event");
        P[i] && P[i].call(this, e)
    }), o.on("mouseenter", "*[lay-tips]", function () {
        var e = a(this);
        if (!e.parent().hasClass("layui-nav-item") || u.hasClass(C)) {
            var i = e.attr("lay-tips"), t = e.attr("lay-offset"), l = e.attr("lay-direction"), n = layer.tips(i, this, {
                tips: l || 1, time: -1, success: function (e, a) {
                    t && e.css("margin-left", t + "px")
                }
            });
            e.data("index", n)
        }
    }).on("mouseleave", "*[lay-tips]", function () {
        layer.close(a(this).data("index"))
    });
    var _ = layui.data.resizeSystem = function () {
        layer.closeAll("tips"), _.lock || setTimeout(function () {
            F.sideFlexible(F.screen() < 2 ? "" : "spread"), delete _.lock
        }, 100), _.lock = !0
    };
    r.on("resize", layui.data.resizeSystem), e("admin", F)
});