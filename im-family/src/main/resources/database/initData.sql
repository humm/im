-- 初始化基础数据

-- 用户信息
truncate table sys_user;
insert into sys_user (USER_ID, USER_CODE, USER_NAME, USER_PASSWORD, USER_STATUS, CREATE_DATE, MODIFY_DATE, CREATE_USER, MODIFY_USER, USER_MEMO)
values (20190000000001, 'admin', '管理员', '==wctlmZ', 'D001-1', sysdate, sysdate, '20190000000001', '20190000000001', '系统管理员，不能删除');

-- 菜单信息
truncate table sys_menu;
insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000000, '数据权限', null, null, null, 0.00, '1', '4', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000001, '收入信息', 'layui-icon-flag', 'income/view/list', null, 10.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000002, '查询权限', null, null, 20190000000001, 15.00, '1', '2', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000003, '设置权限', null, null, 20190000000001, 20.00, '1', '3', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000004, '随礼信息', 'layui-icon-transfer', 'gift/view/list', null, 25.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000005, '查询权限', null, null, 20190000000004, 30.00, '1', '2', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000006, '设置权限', null, null, 20190000000004, 35.00, '1', '3', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000007, '统计分析', 'layui-icon-chart', '#', null, 60.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000008, '收入分析', null, 'report/view/income', 20190000000007, 65.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000009, '送礼分析', null, 'report/view/giftSend', 20190000000007, 70.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000010, '收礼分析', null, 'report/view/giftReceive', 20190000000007, 75.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000011, '系统设置', 'layui-icon-engine', '#', null, 80.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000012, '用户信息', null, 'user/view/list', 20190000000011, 85.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000013, '角色信息', null, 'role/view/list', 20190000000011, 90.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000014, '字典信息', null, 'dictionary/view/list', 20190000000011, 100.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000015, '图标信息', null, 'icon/view/list', 20190000000011, 999999.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000016, '参数信息', null, 'parameter/view/list', 20190000000011, 95.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000017, '消息通知', 'layui-icon-notice', 'notice/view/list', null, 61.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000018, '查询权限', null, null, 20190000000017, 45.00, '1', '2', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000019, '登录日志', 'layui-icon-log', 'loginLog/view/list', null, 62.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000020, '查询权限', null, null, 20190000000019, 55.00, '1', '2', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000021, '修订信息', null, 'version/view/list', 20190000000011, 110.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

-- 字典信息
truncate table sys_dictionary;

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D001', '#', '用户状态', null, 1, 20190000000001, '0', '1');

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D001', '1', '正常', 1, null, 20190000000001, null, null);

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D001', '2', '冻结', 2, null, 20190000000001, null, null);

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D002', '#', '状态标识', null, 2, 20190000000001, '0', '1');

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D002', '1', '成功', 1, null, 20190000000001, null, null);

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D002', '2', '失败', 2, null, 20190000000001, null, null);

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D003', '#', '收入类型', null, 3, 20190000000001, '1', '1');

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D003', '1', '工资', 1, null, 20190000000001, null, null);

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D003', '2', '年终奖', 2, null, 20190000000001, null, null);

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D003', '3', '公积金', 3, null, 20190000000001, null, null);

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D004', '#', '随礼类型', null, 4, 20190000000001, '1', '1');

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D004', '1', '结婚', 1, null, 20190000000001, null, null);

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D004', '2', '生小孩', 2, null, 20190000000001, null, null);

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D005', '#', '收入来源', null, 5, 20190000000001, '1', '1');

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D006', '#', '大额支出类型', null, 6, 20190000000001, '1', '1');

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D007', '#', '消息通知状态', null, 7, 20190000000001, '0', '1');

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D007', '1', '正常', 1, null, 20190000000001, null, null);

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D007', '2', '撤销', 2, null, 20190000000001, null, null);

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D008', '#', '消息通知类型', null, 8, 20190000000001, '0', '1');

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D008', '1', '自录', 1, null, 20190000000001, null, null);

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D008', '2', '邮件', 2, null, 20190000000001, null, null);

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D009', '#', '随礼人', null, 9, 20190000000001, '1', '1');

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D010', '#', '参数状态类型', null, 10, 20190000000001, '0', '1');

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D010', '1', '开启', 1, null, 20190000000001, null, null);

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D010', '2', '关闭', 2, null, 20190000000001, null, null);

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D011', '#', '业务类型', null, 11, 20190000000001, '0', '1');

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D011', '1', '收入', 1, null, 20190000000001, null, null);

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D011', '2', '送礼', 2, null, 20190000000001, null, null);

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D011', '3', '收礼', 3, null, 20190000000001, null, null);

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D012', '#', '阅读状态', null, 12, 20190000000001, '0', '1');

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D012', '1', '未读', 1, null, 20190000000001, null, null);

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D012', '2', '已读', 2, null, 20190000000001, null, null);

-- 系统参数
truncate table sys_parameter;
insert into sys_parameter (parameter_code, parameter_caption, parameter_value, parameter_type, parameter_ext, is_show, is_edit, parameter_order)
values ('uploadLocation', '文件上传路径', '#', 'text', null, '1', '1', 5);

insert into sys_parameter (parameter_code, parameter_caption, parameter_value, parameter_type, parameter_ext, is_show, is_edit, parameter_order)
values ('backupLocation', '系统备份路径', '#', 'text', null, '1', '1', 10);

insert into sys_parameter (parameter_code, parameter_caption, parameter_value, parameter_type, parameter_ext, is_show, is_edit, parameter_order)
values ('mindFill', '智能填充', '1', 'switch', null, '1', '1', 15);

insert into sys_parameter (parameter_code, parameter_caption, parameter_value, parameter_type, parameter_ext, is_show, is_edit, parameter_order)
values ('startConsoleOutput', '系统启动输出配置参数', '1', 'switch', null, '1', '1', 20);

insert into sys_parameter (parameter_code, parameter_caption, parameter_value, parameter_type, parameter_ext, is_show, is_edit, parameter_order)
values ('consoleOutputLogRequestTag', '控制台输出请求标记', '1', 'switch', null, '1', '1', 25);

insert into sys_parameter (parameter_code, parameter_caption, parameter_value, parameter_type, parameter_ext, is_show, is_edit, parameter_order)
values ('consoleOutputLogRequestParameter', '控制台输出请求入参', '1', 'switch', null, '1', '1', 30);

insert into sys_parameter (parameter_code, parameter_caption, parameter_value, parameter_type, parameter_ext, is_show, is_edit, parameter_order)
values ('startBackup', '系统启动备份数据库', '2', 'switch', null, '1', '1', 35);

insert into sys_parameter (parameter_code, parameter_caption, parameter_value, parameter_type, parameter_ext, is_show, is_edit, parameter_order)
values ('startMail', '系统启动读取邮件', '2', 'switch', null, '1', '1', 40);

insert into sys_parameter (parameter_code, parameter_caption, parameter_value, parameter_type, parameter_ext, is_show, is_edit, parameter_order)
values ('yearStartDate', '年度开始时间(yyyymm)', '201901', 'date', '6', '1', '1', 45);

insert into sys_parameter (parameter_code, parameter_caption, parameter_value, parameter_type, parameter_ext, is_show, is_edit, parameter_order)
values ('sessionTimeout', 'session有效时间(秒)', '300', 'number', '5', '1', '1', 50);

insert into sys_parameter (parameter_code, parameter_caption, parameter_value, parameter_type, parameter_ext, is_show, is_edit, parameter_order)
values ('cookieTimeout', 'cookie有效时间(天)', '15', 'number', '5', '1', '1', 55);

insert into sys_parameter (parameter_code, parameter_caption, parameter_value, parameter_type, parameter_ext, is_show, is_edit, parameter_order)
values ('userDefaultPassword', '用户默认密码', 'family', 'text', null, '1', '1', 60);

insert into sys_parameter (parameter_code, parameter_caption, parameter_value, parameter_type, parameter_ext, is_show, is_edit, parameter_order)
values ('messageTip', '未读消息提醒', '1', 'switch', null, '1', '1', 65);

insert into sys_parameter (parameter_code, parameter_caption, parameter_value, parameter_type, parameter_ext, is_show, is_edit, parameter_order)
values ('version', '系统版本号', '3.1.00', 'text', null, '1', '0', 999);

-- 模块配置数据
truncate table sys_config;
insert into sys_config (MODULE_GROUP_CODE, MODULE_GROUP_NAME, MODULE_CODE, MODULE_NAME, MODULE_STATUS, MODULE_EXT)
values ('console', '首页信息', 'user', '用户信息', '1', null);

insert into sys_config (MODULE_GROUP_CODE, MODULE_GROUP_NAME, MODULE_CODE, MODULE_NAME, MODULE_STATUS, MODULE_EXT)
values ('console', '首页信息', 'tips', '提示信息', '1', null);

insert into sys_config (MODULE_GROUP_CODE, MODULE_GROUP_NAME, MODULE_CODE, MODULE_NAME, MODULE_STATUS, MODULE_EXT)
values ('console', '首页信息', 'login', '登录信息', '1', null);

insert into sys_config (MODULE_GROUP_CODE, MODULE_GROUP_NAME, MODULE_CODE, MODULE_NAME, MODULE_STATUS, MODULE_EXT)
values ('console', '首页信息', 'version', '版本信息', '1', null);

-- 修订信息
-- 1：功能 2：优化 3：修复 4：发版
truncate table sys_version;
insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000001', '系统初始化', to_date('19-08-2017', 'dd-mm-yyyy'), 1, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000002', '发布版本：1.0.00', to_date('11-08-2018', 'dd-mm-yyyy'), 5, '4');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000003', '系统初始化', to_date('21-10-2018', 'dd-mm-yyyy'), 10, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000004', '发布版本：2.0.00', to_date('01-07-2019', 'dd-mm-yyyy'), 15, '4');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000005', '系统初始化', to_date('07-08-2019', 'dd-mm-yyyy'), 20, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000006', '流水号生成、集成Swagger', to_date('08-08-2019', 'dd-mm-yyyy'), 25, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000007', '集成webSocket、通用工具类', to_date('09-08-2019', 'dd-mm-yyyy'), 30, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000008', '集成freemarker、适配前台页面布局', to_date('10-08-2019', 'dd-mm-yyyy'), 35, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000009', '字典转义、日志输出、前台页面布局', to_date('11-08-2019', 'dd-mm-yyyy'), 40, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000010', '日志输出工具类封装、字典转义支持单条数据', to_date('12-08-2019', 'dd-mm-yyyy'), 45, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000011', 'dto目录调整为Model目录、返回实体类返回值整合', to_date('15-08-2019', 'dd-mm-yyyy'), 50, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000012', '适配前台页面布局', to_date('16-08-2019', 'dd-mm-yyyy'), 55, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000013', '按用户加载查询数据字典', to_date('23-08-2019', 'dd-mm-yyyy'), 60, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000014', '收入信息：数据列表、查询条件', to_date('29-08-2019', 'dd-mm-yyyy'), 65, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000015', '收入信息', to_date('01-09-2019', 'dd-mm-yyyy'), 70, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000016', '全局异常处理', to_date('01-09-2019', 'dd-mm-yyyy'), 75, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000017', '随礼信息', to_date('07-09-2019', 'dd-mm-yyyy'), 80, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000018', '收入信息报表：年度、月度', to_date('09-09-2019', 'dd-mm-yyyy'), 85, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000019', '收入信息报表：来源、类型、极值、前台页面渲染', to_date('13-09-2019', 'dd-mm-yyyy'), 90, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000020', '随礼信息报表', to_date('13-09-2019', 'dd-mm-yyyy'), 95, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000021', '字典信息', to_date('21-09-2019', 'dd-mm-yyyy'), 100, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000022', '用户信息', to_date('22-09-2019', 'dd-mm-yyyy'), 105, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000023', '角色信息', to_date('08-10-2019', 'dd-mm-yyyy'), 110, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000024', '业务ID编号规则', to_date('11-10-2019', 'dd-mm-yyyy'), 115, '2');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000025', '登录加载菜单信息', to_date('13-10-2019', 'dd-mm-yyyy'), 120, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000026', '页面按钮权限、字典加载数据权限', to_date('13-10-2019', 'dd-mm-yyyy'), 125, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000027', '用户登录', to_date('15-10-2019', 'dd-mm-yyyy'), 130, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000028', '用户Session信息', to_date('18-10-2019', 'dd-mm-yyyy'), 135, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000029', '登录过滤器', to_date('19-10-2019', 'dd-mm-yyyy'), 140, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000030', '字典信息', to_date('19-10-2019', 'dd-mm-yyyy'), 145, '2');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000031', '图标信息', to_date('20-10-2019', 'dd-mm-yyyy'), 150, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000032', '菜单图标', to_date('21-10-2019', 'dd-mm-yyyy'), 155, '2');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000033', '参数信息', to_date('25-10-2019', 'dd-mm-yyyy'), 160, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000034', '重置用户密码、用户退出、修改用户密码', to_date('26-10-2019', 'dd-mm-yyyy'), 165, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000035', '登入日志、消息通知', to_date('26-10-2019', 'dd-mm-yyyy'), 170, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000036', '列表翻页查询数据错误', to_date('26-10-2019', 'dd-mm-yyyy'), 175, '3');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000037', '首页信息', to_date('02-11-2019', 'dd-mm-yyyy'), 180, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000038', '消息通知：页面展示风格', to_date('03-11-2019', 'dd-mm-yyyy'), 185, '2');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000039', '首页信息：展示未读消息通知', to_date('04-11-2019', 'dd-mm-yyyy'), 190, '2');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000040', '报表信息：多用户数据展示', to_date('17-11-2019', 'dd-mm-yyyy'), 195, '2');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000041', '数据转义', to_date('21-11-2019', 'dd-mm-yyyy'), 200, '2');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000042', '修订信息', to_date('23-11-2019', 'dd-mm-yyyy'), 205, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000043', '发布版本：3.0.00', to_date('24-11-2019', 'dd-mm-yyyy'), 210, '4');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000044', '消息通知详情页面返回指定列表类型', to_date('25-11-2019', 'dd-mm-yyyy'), 215, '2');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000045', '初始化系统配置数据', to_date('25-11-2019', 'dd-mm-yyyy'), 220, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000046', '自定义首页显示模块', to_date('27-11-2019', 'dd-mm-yyyy'), 225, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000047', '系统备份：sql文件模式、dmp文件模式', to_date('30-11-2019', 'dd-mm-yyyy'), 230, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000048', '首页未读消息提醒新增参数控制', to_date('07-12-2019', 'dd-mm-yyyy'), 235, '2');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000049', '首页提示信息、登录信息新增菜单跳转链接', to_date('07-12-2019', 'dd-mm-yyyy'), 240, '2');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000050', '首页链接跳转TAB名称统一处理', to_date('14-12-2019', 'dd-mm-yyyy'), 245, '2');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000051', '下拉选择框显示key值、字典信息修改页面隐藏key值', to_date('03-02-2020', 'dd-mm-yyyy'), 250, '2');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000052', '随礼信息修改时不能显示随礼人信息', to_date('03-02-2020', 'dd-mm-yyyy'), 255, '3');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000053', '数据权限修改后session刷新不及时导致查询脏数据', to_date('03-02-2020', 'dd-mm-yyyy'), 260, '3');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000054', '发布版本：3.0.01', to_date('03-02-2020', 'dd-mm-yyyy'), 265, '4');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000055', '删除角色时不删除sys_role_menu数据造成脏数据', to_date('05-02-2020', 'dd-mm-yyyy'), 270, '3');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000056', '删除角色时不删除sys_user_role数据造成脏数据', to_date('05-02-2020', 'dd-mm-yyyy'), 275, '3');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000057', 'session失效列表数据查询报错不跳转登录页面', to_date('05-02-2020', 'dd-mm-yyyy'), 280, '3');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000058', '登入：记住密码', to_date('08-02-2020', 'dd-mm-yyyy'), 285, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000059', '添加系统logo', to_date('09-02-2020', 'dd-mm-yyyy'), 290, '2');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000060', '邮件申请数据处理：支持业务类型为收入、随礼', to_date('10-02-2020', 'dd-mm-yyyy'), 295, '1');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000061', '发布版本：3.1.00', to_date('10-02-2020', 'dd-mm-yyyy'), 300, '4');

insert into sys_version (version_id, version_content, version_date, version_order, version_type)
values ('20190000000062', 'sql备份性能问题', to_date('15-02-2020', 'dd-mm-yyyy'), 305, '3');