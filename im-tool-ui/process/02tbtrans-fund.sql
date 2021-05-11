-- ***************************************************
-- TA6.0 交易配置
-- 版权所述：TA研发二部
-- ***************************************************
delete from tbtrans where trans_code = '901008';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('901008','恢复','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '901007';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('901007','备份','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '901006';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('901006','是否有在途交易校验','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '901005';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('901005','刷新redis缓存','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '901004';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('901004','节点结束处理','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '901003';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('901003','节点开始处理','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '901002';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('901002','缓存加载','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '901001';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('901001','结束清算','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '930007';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('930007','销户取消登记确认处理','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '930006';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('930006','非开户登记增开确认-账户库','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '930009';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('930009','反洗钱检查-公共库','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '930008';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('930008','非居民涉税信息处理-账户库','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '909012';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('909012','定期定额协议修改申请处理','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '909011';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('909011','定期定额协议开通','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '909010';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('909010','转托管入','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '909016';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('909016','多基金份额调整处理','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '930010';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('930010','反洗钱落地-账户库','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '909015';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('909015','TA发起强减确认','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '910004';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('910004','发行失败服务','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '909014';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('909014','TA发起强增确认','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '910007';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('910007','清盘处理','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '930012';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('930012','销售商确认接口数据准备','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '909013';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('909013','定期定额协议取消处理','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '910006';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('910006','分红再投资处理','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '910001';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('910001','统一业绩提成','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '930014';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('930014','集中备份文件数据准备','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '904001';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('904001','销售商行情导出','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '909019';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('909019','快速过户处理','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '930013';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('930013','客服接口数据准备','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '909018';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('909018','快速过户处理','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '910003';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('910003','成立服务','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '930016';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('930016','公共提示开始处理-账户库','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '904003';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('904003','分库账户申请导入','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '909017';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('909017','修改分红方式处理','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '910002';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('910002','验资服务','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '930015';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('930015','缓存加载-账户库','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '910009';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('910009','全赎后昨日兑付份额强赎生成','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '910008';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('910008','批后完成处理','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '909021';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('909021','过户在途份额','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '909020';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('909020','工行质押解押解冻处理失败时赎回确认失败','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '907002';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('907002','大额最高持有控制','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '910016';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('910016','认购集中度控制','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '907003';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('907003','账户数控制','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '910015';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('910015','电子合同成立控制','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '910018';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('910018','批后认购规模控制','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '907001';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('907001','交易预处理','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '910017';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('910017','统计基金发行分组信息','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '907006';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('907006','工行质押解押特殊代码更新','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '910012';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('910012','收益兑付','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '930003';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('930003','开户处理-账户库','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '907007';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('907007','工行质押解押检查状态更新','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '910011';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('910011','分红处理','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '907004';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('907004','预处理集中度昨日占比统计','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '910014';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('910014','短期理财到期份额小份额强赎','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '930005';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('930005','非开户登记增开检查-账户库','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '907005';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('907005','预处理集中度昨日占比超限失败','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '910013';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('910013','快速过户098业务垫资账户强赎','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '930004';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('930004','登记增开处理-账户库','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '913001';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('913001','销售商确认数据生成-主库','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '907008';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('907008','电子合同处理','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '910010';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('910010','小份额强制赎回','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '930081';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('930081','账户库备份','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '907009';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('907009','电子合同控制交易','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '930082';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('930082','账户库恢复','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '916001';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('916001','分库数据导出-交易库','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '916002';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('916002','主库数据导出-公共库','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '916003';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('916003','文件合并-公共库','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '916004';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('916004','tainfo文件合并','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '907010';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('907010','指定金额赎回计算','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '910019';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('910019','确权处理','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '907011';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('907011','保证金收益强制赎回','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '907012';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('907012','中登电子合同处理','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '902001';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('902001','日初始化','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '902002';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('902002','系统准备','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '950001';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('950001','账户数据导出','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '910020';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('910020','保证金解约强制赎回','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '930018';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('930018','公共提示结束处理-账户库','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '930017';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('930017','同步等待','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '930019';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('930019','余额理财开户处理','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '930021';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('930021','余额理财取消登记','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '911005';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('911005','账户和资产过低统计','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '930020';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('930020','余额理财账户资料修改','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '905001';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('905001','更新基金清算列表为1','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '911002';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('911002','批后统计','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '911001';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('911001','认购成功人工否决数据确认','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '911004';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('911004','升降级处理','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '911003';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('911003','交易确认统计','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '920002';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('920002','数据迁移','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '920001';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('920001','创建数据迁移包含关系','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '908003';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('908003','巨额赎回判断','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '908004';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('908004','巨额赎回行情表更新','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '908001';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('908001','预处理后统计','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '908002';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('908002','开放期集中度控制','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '908007';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('908007','七日可变现资产赎回控制','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '908008';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('908008','交易检查后认购规模判断','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '908005';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('908005','赎回超限控制判断','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '908006';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('908006','赎回超限控制','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '908009';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('908009','交易检查后认购规模控制','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '914001';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('914001','会计清算文件数据生成-主库','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '917001';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('917001','余额理财申购处理','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '917002';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('917002','余额理财赎回处理','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '917003';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('917003','余额理财转换处理','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '917004';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('917004','余额理财非交易过户出-交易库','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '917005';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('917005','余额理财非交易过户入-交易库','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '917006';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('917006','余额理财转托管','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '917007';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('917007','余额理财批前处理','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '917008';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('917008','余额理财自营账户强赎','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '940002';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('940002','账户分库下载','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '917009';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('917009','余额理财收益统计','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '903001';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('903001','文件下载','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '903002';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('903002','交易分库获取销售商申请文件','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '903003';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('903003','账户申请导入','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '903004';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('903004','行情导入','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '903005';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('903005','账户申请校验','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '917010';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('917010','余额理财收益结转','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '917011';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('917011','余额理财收益分配','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '917012';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('917012','余额理财生成05表','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '906002';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('906002','货币基金征收赎回费基准份额统计','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '906001';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('906001','收益分配','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '906004';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('906004','保有金额计算','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '906003';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('906003','基金拆分','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '906005';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('906005','工行移行变更份额处理','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '903016';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('903016','公共提示处理-账户库','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '912001';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('912001','客服数据导出数据准备','1','0123456790G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '909001';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('909001','认购处理','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '909005';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('909005','基金转换入处理','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '909004';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('909004','基金转换出处理','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '909003';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('909003','TA发起强赎确认','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '909002';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('909002','申购处理','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '909009';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('909009','转托管出','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '909008';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('909008','TA发起非交易过户确认','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '909007';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('909007','TA发起份额解冻确认','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '909006';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('909006','TA发起份额冻结确认','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
delete from tbtrans where trans_code = '915001';
insert into tbtrans (trans_code, trans_name, enable_flag, channels, host_online, trans_type, monitor_flag, log_level, cancel_flag, erase_flag, mon_trans_type, reserve1, reserve2, reserve3) 
values ('915001','集中备份文件数据准备-主库','1','0123456789G','0','4','0','2','0','0','4',' ',' ',' ');
commit;