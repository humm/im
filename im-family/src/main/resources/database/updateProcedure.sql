-- 升级脚本

-- 存储过程以 ====== 六个等号分割
-- ====== --
-- 新增版本信息
create or replace procedure add_version(
  version_id in varchar2,
  version_content in varchar2,
  version_date in varchar2,
  version_order in varchar2,
  version_type in varchar2
)
as
  deleteSql varchar2(5000);
  insertSql varchar2(5000);
begin
      deleteSql := 'delete from sys_version where version_id = :versionId';
      execute immediate deleteSql using version_id;

      insertSql := 'insert into sys_version (version_id, version_content, version_date, version_order, version_type) values(:versionId,:versionContent,:versionDate,:versionOrder,:versionYype)';
      execute immediate insertSql using version_id, version_content, to_date(version_date, 'yyyy-MM-dd'), version_order, version_type;

      commit;
end add_version;
-- ====== --
-- 新增系统参数
create or replace procedure add_parameter(
  parameter_code in varchar2,
  parameter_caption in varchar2,
  parameter_value in varchar2,
  parameter_type in varchar2,
  parameter_ext in varchar2,
  is_show in varchar2,
  is_edit in varchar2,
  parameter_order in varchar2,
  parameter_group in varchar2 default null
)
as
  flag INTEGER DEFAULT 0;
  selectSql varchar2(5000);
  insertSql varchar2(5000);
begin
      selectSql := 'select count(1) from sys_parameter where parameter_code = :parameterCode';
      execute immediate selectSql into flag using parameter_code;

      if flag = 0 then
         insertSql := 'insert into sys_parameter (parameter_code, parameter_caption, parameter_value, parameter_type, parameter_ext, is_show, is_edit, parameter_order, parameter_group) values(:parameterCode, :parameterCaption, :parameterValue, :parameterType, :parameterExt, :isShow, :isEdit, :parameterOrder, :parameterGroup)';
         execute immediate insertSql using parameter_code, parameter_caption, parameter_value, parameter_type, parameter_ext, is_show, is_edit, parameter_order, parameter_group;
      end if;

      commit;
end add_parameter;
-- ====== --
-- 更新版本号
create or replace procedure update_system_version(
  version_value in varchar2
)
as
  updateSql varchar2(5000);
begin
      updateSql := 'update sys_parameter set parameter_value = :versionValue where parameter_code = :parameterCode';
      execute immediate updateSql using version_value, 'version';

      commit;
end update_system_version;
-- ====== --
-- 创建数据表
create or replace procedure create_table(tableName in varchar2, tableInfo in varchar2)
is
    v_count number(10);
begin
    select count(1) into v_count from user_tables where table_name = upper(tableName);
    if v_count = 0
    then
        execute immediate tableInfo;
    end if;
end create_table;
-- ====== --