-- 初始化用户信息

create tablespace fims logging datafile 'D:/fims/tablespace/fims.dbf' SIZE 1G autoextend on next 10m maxsize unlimited;
create temporary tablespace fims_tmp tempfile 'D:/fims/tablespace/fims_tmp.dbf' size 1g autoextend on next 10m maxsize unlimited;
create user fims identified by fims DEFAULT TABLESPACE fims TEMPORARY TABLESPACE fims_tmp;
grant connect, create session, resource, create table, select any table, create any view, drop any view to fims;
grant create any directory to fims;
grant select on dba_directories to fims;