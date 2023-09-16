-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('黑名单', '1', '1', '/bus/blacklist', 'C', '0', 'bus:blacklist:view', '#', 'admin', sysdate(), '', null, '黑名单菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('黑名单查询', @parentId, '1',  '#',  'F', '0', 'bus:blacklist:list',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('黑名单新增', @parentId, '2',  '#',  'F', '0', 'bus:blacklist:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('黑名单修改', @parentId, '3',  '#',  'F', '0', 'bus:blacklist:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('黑名单删除', @parentId, '4',  '#',  'F', '0', 'bus:blacklist:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('黑名单导出', @parentId, '5',  '#',  'F', '0', 'bus:blacklist:export',       '#', 'admin', sysdate(), '', null, '');




