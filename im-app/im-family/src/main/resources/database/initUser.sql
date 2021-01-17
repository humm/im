-- 初始化用户信息

create tablespace family logging datafile 'D:/family/tablespace/family.dbf' SIZE 1G autoextend on next 10m maxsize unlimited;
create temporary tablespace family_tmp tempfile 'D:/family/tablespace/family_tmp.dbf' size 1g autoextend on next 10m maxsize unlimited;
create user family identified by family DEFAULT TABLESPACE family TEMPORARY TABLESPACE family_tmp;
grant connect, create session, resource, create table, select any table, create any view, drop any view to family;
grant create any directory to family;
grant select on dba_directories to family;