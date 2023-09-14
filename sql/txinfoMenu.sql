-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('交易记录', '1', '1', '/token/txinfo', 'C', '0', 'token:txinfo:view', '#', 'admin', sysdate(), '', null, '交易记录菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('交易记录查询', @parentId, '1',  '#',  'F', '0', 'token:txinfo:list',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('交易记录新增', @parentId, '2',  '#',  'F', '0', 'token:txinfo:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('交易记录修改', @parentId, '3',  '#',  'F', '0', 'token:txinfo:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('交易记录删除', @parentId, '4',  '#',  'F', '0', 'token:txinfo:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('交易记录导出', @parentId, '5',  '#',  'F', '0', 'token:txinfo:export',       '#', 'admin', sysdate(), '', null, '');
