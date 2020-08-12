-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        8.0.15 - MySQL Community Server - GPL
-- 服务器操作系统:                      Win64
-- HeidiSQL 版本:                  11.0.0.5919
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- 导出 oc_db 的数据库结构
DROP DATABASE IF EXISTS `oc_db`;
CREATE DATABASE IF NOT EXISTS `oc_db` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `oc_db`;

-- 导出  表 oc_db.oc_admin 结构
DROP TABLE IF EXISTS `oc_admin`;
CREATE TABLE IF NOT EXISTS `oc_admin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户编码',
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色编码',
  `account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '账号',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '姓名',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '电话',
  `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '邮箱',
  `grade` int(11) DEFAULT NULL COMMENT '层级 1 超级管理员 2 管理员 3 普通管理员',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '密码',
  `login_cnt` int(11) DEFAULT '0' COMMENT '登录次数后，需改密码',
  `reset_time` datetime DEFAULT NULL COMMENT '重置时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `account` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=257 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 正在导出表  oc_db.oc_admin 的数据：~1 rows (大约)
/*!40000 ALTER TABLE `oc_admin` DISABLE KEYS */;
INSERT INTO `oc_admin` (`id`, `tenant_code`, `role_id`, `account`, `name`, `phone`, `email`, `grade`, `password`, `login_cnt`, `reset_time`, `create_time`, `create_by`) VALUES
	(1, '1', 1, 'admin', '超级管理员', '18611209901', 'chuangyeifang163@163.com', 1, '$apr1$set$60VxGTeiudaNwN1cZRApJ/', 0, NULL, '2019-07-29 11:15:54', 'admin');
/*!40000 ALTER TABLE `oc_admin` ENABLE KEYS */;

-- 导出  表 oc_db.oc_admin_team 结构
DROP TABLE IF EXISTS `oc_admin_team`;
CREATE TABLE IF NOT EXISTS `oc_admin_team` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tenant_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '账号',
  `team_code` int(11) DEFAULT NULL COMMENT '团队编码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `account_team_code` (`account`,`team_code`),
  KEY `account` (`account`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=325 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='团队队长表';

-- 正在导出表  oc_db.oc_admin_team 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `oc_admin_team` DISABLE KEYS */;
/*!40000 ALTER TABLE `oc_admin_team` ENABLE KEYS */;

-- 导出  表 oc_db.oc_bench_version 结构
DROP TABLE IF EXISTS `oc_bench_version`;
CREATE TABLE IF NOT EXISTS `oc_bench_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pid` int(11) DEFAULT NULL,
  `version_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '版本号',
  `version_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '版本更新描述',
  `sort_num` int(11) DEFAULT NULL COMMENT '排序',
  `flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '1' COMMENT '是否有效： 0无效 1有效',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `version_code` (`version_code`),
  KEY `pid` (`pid`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='客服工作台版本更新详情';

-- 正在导出表  oc_db.oc_bench_version 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `oc_bench_version` DISABLE KEYS */;
/*!40000 ALTER TABLE `oc_bench_version` ENABLE KEYS */;

-- 导出  表 oc_db.oc_blacklist 结构
DROP TABLE IF EXISTS `oc_blacklist`;
CREATE TABLE IF NOT EXISTS `oc_blacklist` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `tenant_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `team_code` int(11) DEFAULT NULL,
  `customer_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 正在导出表  oc_db.oc_blacklist 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `oc_blacklist` DISABLE KEYS */;
/*!40000 ALTER TABLE `oc_blacklist` ENABLE KEYS */;

-- 导出  表 oc_db.oc_chat 结构
DROP TABLE IF EXISTS `oc_chat`;
CREATE TABLE IF NOT EXISTS `oc_chat` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `chat_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `tenant_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '租户编码',
  `team_code` int(11) DEFAULT NULL COMMENT '团队编码',
  `skill_code` int(11) DEFAULT NULL COMMENT '技能编码',
  `goods_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `customer_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '访客登录账号',
  `customer_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '访客编码',
  `waiter_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '客服名称',
  `waiter_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '客服编码',
  `is_login` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '0 未登录 1登录',
  `is_transfer` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '0 否 1是',
  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '1 在线 2留言',
  `is_effective` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '是否有效 0无效 1有效',
  `waiting_time` int(11) DEFAULT '0' COMMENT '等待时长',
  `msg_total` int(11) DEFAULT NULL COMMENT '消息总数',
  `waiter_msg_total` int(11) DEFAULT NULL COMMENT '客服消息数量',
  `customer_msg_total` int(11) DEFAULT NULL COMMENT '客户消息数量',
  `opinion` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '1非常不满意 2不满意 3一般 4满意 5非常满意',
  `suggest` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '评价/建议',
  `device_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '来源 1PC 2WAP 3APP 4WebChat',
  `close_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '关闭类型 1客户关闭 2客服关闭 3超时关闭 4转接关闭',
  `create_time` datetime DEFAULT NULL COMMENT '连接时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`),
  KEY `idx_create_time` (`create_time`) USING BTREE,
  KEY `chat_id` (`chat_id`),
  KEY `skill_code` (`skill_code`),
  KEY `customer_name` (`customer_name`),
  KEY `waiter_name` (`waiter_name`),
  KEY `customer_code` (`customer_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 正在导出表  oc_db.oc_chat 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `oc_chat` DISABLE KEYS */;
/*!40000 ALTER TABLE `oc_chat` ENABLE KEYS */;

-- 导出  表 oc_db.oc_chat_record 结构
DROP TABLE IF EXISTS `oc_chat_record`;
CREATE TABLE IF NOT EXISTS `oc_chat_record` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `team_code` int(11) DEFAULT NULL,
  `chat_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '会话编码',
  `message_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `owner_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '1：客户 2：客服 3: robot',
  `waiter_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '客服编码',
  `waiter_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '客服名称',
  `customer_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '访客名称',
  `customer_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '访客编码',
  `message_type` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '消息类型: 1 普通消息 2 图片消息 3 提示关闭 4关闭提示 5 欢迎语 6 系统消息',
  `offline` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '0' COMMENT '0 普通消息 1 离线消息 2 留言消息 ',
  `revocation` int(11) DEFAULT NULL,
  `messages` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_chat_id` (`chat_id`) USING BTREE,
  KEY `create_time` (`create_time`),
  KEY `customer_name` (`customer_name`),
  KEY `waiter_code` (`waiter_code`),
  KEY `inx_messages` (`messages`(10)),
  KEY `customer_code` (`customer_code`),
  KEY `waiter_code_create_time` (`create_time`,`waiter_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 正在导出表  oc_db.oc_chat_record 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `oc_chat_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `oc_chat_record` ENABLE KEYS */;

-- 导出  表 oc_db.oc_hot_spots 结构
DROP TABLE IF EXISTS `oc_hot_spots`;
CREATE TABLE IF NOT EXISTS `oc_hot_spots` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户编码',
  `team_code` int(11) DEFAULT NULL COMMENT '团队编码',
  `skill_code` int(11) DEFAULT NULL COMMENT '技能编码',
  `question` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '问题',
  `answer` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '答案',
  `status` int(11) DEFAULT NULL COMMENT '0：不启用，1：启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`),
  KEY `index_know_id` (`id`) USING BTREE,
  KEY `index_know_question` (`question`) USING BTREE,
  KEY `index_know_status` (`status`) USING BTREE,
  KEY `group_id` (`skill_code`),
  KEY `team_code` (`team_code`)
) ENGINE=InnoDB AUTO_INCREMENT=258 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='热点问题';

-- 正在导出表  oc_db.oc_hot_spots 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `oc_hot_spots` DISABLE KEYS */;
/*!40000 ALTER TABLE `oc_hot_spots` ENABLE KEYS */;

-- 导出  表 oc_db.oc_menu 结构
DROP TABLE IF EXISTS `oc_menu`;
CREATE TABLE IF NOT EXISTS `oc_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单编码',
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '路由标题',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '路由名称',
  `path` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '路由地址',
  `component_path` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '图标',
  `type` int(11) DEFAULT NULL COMMENT '1目录 2菜单',
  `sort_num` int(11) DEFAULT NULL COMMENT '展示循序',
  `parent_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=159 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 正在导出表  oc_db.oc_menu 的数据：~36 rows (大约)
/*!40000 ALTER TABLE `oc_menu` DISABLE KEYS */;
INSERT INTO `oc_menu` (`id`, `title`, `name`, `path`, `component_path`, `icon`, `type`, `sort_num`, `parent_id`, `create_time`, `create_by`) VALUES
	(2, '聊天记录', 'Record', 'record', 'chat/record', 'el-icon-document', 2, 1, 33, '2020-01-06 17:21:49', 'admin'),
	(33, '聊天记录', 'Chat', '/chat', 'Layout', 'el-icon-document', 0, 2, -1, '2020-01-06 14:43:36', 'admin'),
	(39, '实时监控', 'Monitor', '/monitor', 'Layout', 'el-icon-menu', 0, 1, -1, '2020-01-20 09:55:13', 'admin'),
	(40, '团队状态', 'Queue', '/queue', 'monitor/queue', 'el-icon-document', 2, 1, 39, '2020-01-20 09:58:13', 'admin'),
	(41, '客服状态', 'Service', 'service', 'monitor/service', 'el-icon-document', 2, 2, 39, '2020-01-20 10:10:30', 'admin'),
	(42, '运营报表', 'operate', '/operate', 'Layout', 'el-icon-document', 0, 3, -1, '2020-01-20 10:15:06', 'admin'),
	(43, '常用语', 'Answer', '/answer', 'Layout', 'el-icon-document', 0, 4, -1, '2020-01-20 10:17:29', 'admin'),
	(44, '访客框内容', 'Visitor', '/visitor', 'Layout', 'el-icon-document', 0, 5, -1, '2020-01-20 10:18:55', 'admin'),
	(45, '热点问题', 'Hot', 'hot', 'visitor/hot', 'el-icon-document', 2, 1, 44, '2020-01-20 10:20:36', 'admin'),
	(46, '"我想"标签', 'Label', 'label', 'visitor/label', 'el-icon-document', 2, 2, 44, '2020-01-20 10:21:27', 'admin'),
	(47, '设置', 'Setting', '/setting', 'Layout', 'el-icon-document', 0, 6, -1, '2020-01-20 10:22:29', 'admin'),
	(48, '权限管理', 'Jurisdiction', '/jurisdiction', 'Layout', 'el-icon-document', 0, 7, -1, '2020-01-20 10:23:21', 'admin'),
	(49, '角色管理', 'Role', 'role', 'jurisdiction/role', 'el-icon-document', 2, 1, 48, '2020-01-20 11:06:33', 'admin'),
	(50, '菜单管理', 'Menu', 'menu', 'jurisdiction/menu', 'el-icon-document', 2, 2, 48, '2020-01-20 11:12:01', 'admin'),
	(51, '租户管理', 'Tenant', 'tenant', 'jurisdiction/tenant', 'el-icon-document', 2, 4, 48, '2020-01-20 11:15:17', 'admin'),
	(52, '租户负责人', 'TenantHead', 'tenantHead', 'jurisdiction/tenantHead', 'el-icon-document', 2, 3, 48, '2020-01-20 11:23:21', 'admin'),
	(53, '我的组', 'Group', 'group', 'setting/group', 'el-icon-document', 2, 1, 47, '2020-01-20 11:36:04', 'admin'),
	(54, '客服', 'Service', 'service', 'setting/service', 'el-icon-document', 2, 2, 47, '2020-01-20 11:37:14', 'admin'),
	(55, '队列', 'Queue', 'queue', 'setting/queue', 'el-icon-document', 2, 3, 47, '2020-01-20 11:37:53', 'admin'),
	(56, '团队', 'Team', 'team', 'setting/team', 'el-icon-document', 2, 4, 47, '2020-01-20 11:38:49', 'admin'),
	(57, '团队队长', 'TeamLeader', 'teamLeader', 'setting/teamLeader', 'el-icon-document', 2, 5, 47, '2020-01-20 11:39:32', 'admin'),
	(59, '个人信息', 'Information', 'information', 'setting/information', 'el-icon-document', 2, 7, 47, '2020-01-20 11:41:24', 'admin'),
	(60, '黑名单', 'Blacklist', 'blacklist', 'setting/blacklist', 'el-icon-document', 2, 8, 47, '2020-01-20 11:42:17', 'admin'),
	(61, '分类', 'Classify', 'classify', 'answer/classify', 'el-icon-document', 2, 1, 43, '2020-01-20 11:43:22', 'admin'),
	(62, '常用语列表', 'List', 'list', 'answer/list', 'el-icon-document', 2, 2, 43, '2020-01-20 11:44:20', 'admin'),
	(63, '队列工作量', 'QueueWorkload', 'queueWorkload', 'operate/queueWorkload', 'el-icon-document', 2, 1, 42, '2020-01-20 11:45:44', 'admin'),
	(64, '客服工作量', 'ServiceWorkload', 'serviceWorkload', 'operate/serviceWorkload', 'el-icon-document', 2, 2, 42, '2020-01-20 11:46:33', 'admin'),
	(65, '出勤统计', 'Attendance', 'attendance', 'operate/attendance', 'el-icon-document', 2, 3, 42, '2020-01-20 12:54:57', 'admin'),
	(66, '访客分布', 'VisitorDistribution', 'visitorDistribution', 'operate/visitorDistribution', 'el-icon-document', 2, 4, 42, '2020-01-20 12:56:02', 'admin'),
	(67, '排队量分布', 'QueuingDistribution', 'queuingDistribution', 'operate/queuingDistribution', 'el-icon-document', 2, 5, 42, '2020-01-20 12:57:10', 'admin'),
	(68, '销售转化统计', 'SalesConversion', '/salesConversion', 'operate/salesConversion', 'el-icon-document', 2, 6, 42, '2020-01-20 12:59:16', 'admin'),
	(70, '满意度评价', 'SatisfactionDegree', 'satisfactionDegree', 'operate/satisfactionDegree', 'el-icon-document', 2, 8, 42, '2020-01-20 13:01:30', 'admin'),
	(71, '销售转化明细', 'SalesDetails', 'salesDetails', 'operate/salesDetails', 'el-icon-document', 2, 7, 42, '2020-01-20 13:02:49', 'admin'),
	(155, '接口文档', 'Swagger', '/swagger', 'jurisdiction/swagger', 'el-icon-document', 2, 6, 48, '2020-05-18 16:24:36', 'admin'),
	(156, '客服操作日志', 'WaiterLog', '/waiterLog', 'jurisdiction/waiterLog', 'el-icon-document', 2, 7, 48, '2020-06-01 14:29:02', 'admin'),
	(158, '富文本', 'Editor', '/editor', '/visitor/editor', 'el-icon-document', 2, 3, 44, '2020-06-01 14:51:03', 'admin');
/*!40000 ALTER TABLE `oc_menu` ENABLE KEYS */;

-- 导出  表 oc_db.oc_properties 结构
DROP TABLE IF EXISTS `oc_properties`;
CREATE TABLE IF NOT EXISTS `oc_properties` (
  `id` int(11) NOT NULL,
  `tenant_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户编码',
  `team_code` int(11) DEFAULT NULL COMMENT '技能编码',
  `timeout_tip_message` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '超时提醒信息',
  `timeout_close_message` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '超时关闭信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 正在导出表  oc_db.oc_properties 的数据：~1 rows (大约)
/*!40000 ALTER TABLE `oc_properties` DISABLE KEYS */;
INSERT INTO `oc_properties` (`id`, `tenant_code`, `team_code`, `timeout_tip_message`, `timeout_close_message`) VALUES
	(0, '1', NULL, '【系统提示】亲，不要长时间不理人家哦，如您长时间未回复系统将自动断开，人海茫茫一旦掉线，我就找不到您啦~', '【系统提示】您好，这边已经很久没有收到您的消息了，我们将结束此次会话，如果您还有其他问题，请您再次发起会话，非常感谢您对我们的支持，么么');
/*!40000 ALTER TABLE `oc_properties` ENABLE KEYS */;

-- 导出  表 oc_db.oc_role 结构
DROP TABLE IF EXISTS `oc_role`;
CREATE TABLE IF NOT EXISTS `oc_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色编码',
  `tenant_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户编码',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '角色名称',
  `type` int(1) DEFAULT NULL COMMENT '0 系统 1 租户',
  `flag` int(11) DEFAULT NULL COMMENT '0 失效 1有效',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tenant_code_name` (`tenant_code`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 正在导出表  oc_db.oc_role 的数据：~2 rows (大约)
/*!40000 ALTER TABLE `oc_role` DISABLE KEYS */;
INSERT INTO `oc_role` (`id`, `tenant_code`, `name`, `type`, `flag`, `create_time`, `create_by`) VALUES
	(105, '', '租户菜单权限', 0, 1, '2020-02-26 11:00:04', 'admin'),
	(111, '', '团队队长', 0, 1, '2020-03-06 11:17:47', 'admin');
/*!40000 ALTER TABLE `oc_role` ENABLE KEYS */;

-- 导出  表 oc_db.oc_role_menu 结构
DROP TABLE IF EXISTS `oc_role_menu`;
CREATE TABLE IF NOT EXISTS `oc_role_menu` (
  `role_id` bigint(20) NOT NULL COMMENT '角色编码',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单编码',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 正在导出表  oc_db.oc_role_menu 的数据：~59 rows (大约)
/*!40000 ALTER TABLE `oc_role_menu` DISABLE KEYS */;
INSERT INTO `oc_role_menu` (`role_id`, `menu_id`) VALUES
	(105, 2),
	(105, 33),
	(105, 39),
	(105, 40),
	(105, 41),
	(105, 42),
	(105, 43),
	(105, 44),
	(105, 45),
	(105, 46),
	(105, 47),
	(105, 48),
	(105, 49),
	(105, 53),
	(105, 54),
	(105, 55),
	(105, 56),
	(105, 57),
	(105, 59),
	(105, 60),
	(105, 61),
	(105, 62),
	(105, 63),
	(105, 64),
	(105, 65),
	(105, 66),
	(105, 67),
	(105, 68),
	(105, 70),
	(105, 71),
	(105, 156),
	(111, 2),
	(111, 33),
	(111, 39),
	(111, 40),
	(111, 41),
	(111, 42),
	(111, 43),
	(111, 44),
	(111, 45),
	(111, 46),
	(111, 47),
	(111, 53),
	(111, 54),
	(111, 55),
	(111, 56),
	(111, 57),
	(111, 59),
	(111, 60),
	(111, 61),
	(111, 62),
	(111, 63),
	(111, 64),
	(111, 65),
	(111, 66),
	(111, 67),
	(111, 68),
	(111, 70),
	(111, 71);
/*!40000 ALTER TABLE `oc_role_menu` ENABLE KEYS */;

-- 导出  表 oc_db.oc_role_user 结构
DROP TABLE IF EXISTS `oc_role_user`;
CREATE TABLE IF NOT EXISTS `oc_role_user` (
  `role_id` bigint(20) NOT NULL COMMENT '角色编码',
  `user_id` bigint(20) NOT NULL COMMENT '用户编码',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 正在导出表  oc_db.oc_role_user 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `oc_role_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `oc_role_user` ENABLE KEYS */;

-- 导出  表 oc_db.oc_skill 结构
DROP TABLE IF EXISTS `oc_skill`;
CREATE TABLE IF NOT EXISTS `oc_skill` (
  `skill_code` int(11) NOT NULL AUTO_INCREMENT COMMENT '技能编码',
  `tenant_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户编码',
  `skill_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '技能名称',
  `flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '0 失效 1有效',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`skill_code`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 正在导出表  oc_db.oc_skill 的数据：~1 rows (大约)
/*!40000 ALTER TABLE `oc_skill` DISABLE KEYS */;
INSERT INTO `oc_skill` (`skill_code`, `tenant_code`, `skill_name`, `flag`, `create_time`) VALUES
	(1, '1', '测试队列', '1', '2020-07-29 16:32:58');
/*!40000 ALTER TABLE `oc_skill` ENABLE KEYS */;

-- 导出  表 oc_db.oc_skill_business 结构
DROP TABLE IF EXISTS `oc_skill_business`;
CREATE TABLE IF NOT EXISTS `oc_skill_business` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tenant_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户编码',
  `origin` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '业务来源',
  `skill_code` int(11) DEFAULT NULL COMMENT '技能编码',
  `business_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '业务编码',
  `business_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '业务名称',
  `flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '1' COMMENT '0 失效 1生效',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tenant_code_business_code_origin` (`tenant_code`,`business_code`,`origin`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 正在导出表  oc_db.oc_skill_business 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `oc_skill_business` DISABLE KEYS */;
/*!40000 ALTER TABLE `oc_skill_business` ENABLE KEYS */;

-- 导出  表 oc_db.oc_team 结构
DROP TABLE IF EXISTS `oc_team`;
CREATE TABLE IF NOT EXISTS `oc_team` (
  `team_code` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户编号',
  `team_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `brief_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `need_login` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '0 匿名访问 1真实访问',
  `login_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '如果需要登录，登录地址',
  `assign_rule` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '分配规则 0 记忆分配 1轮训分配 2空闲分配',
  `begin_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '服务开始时间',
  `end_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '服务结束时间',
  `offline_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '非工作时间服务语提示 {begin_time}-{end_time}',
  `auto_reply` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '0 否 1是',
  `reply_msg` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '首次访问自动回复语',
  `flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '1' COMMENT '0 无效 1有效',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`team_code`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 正在导出表  oc_db.oc_team 的数据：~1 rows (大约)
/*!40000 ALTER TABLE `oc_team` DISABLE KEYS */;
INSERT INTO `oc_team` (`team_code`, `tenant_code`, `team_name`, `brief_name`, `sort`, `need_login`, `login_url`, `assign_rule`, `begin_time`, `end_time`, `offline_message`, `auto_reply`, `reply_msg`, `flag`, `create_time`) VALUES
	(1, '1', '测试', '110', 1, '0', NULL, '1', '09:00:00', '23:00:00', '工作时间为{0}-{1}', '1', '你好', '1', '2020-07-29 16:32:25');
/*!40000 ALTER TABLE `oc_team` ENABLE KEYS */;

-- 导出  表 oc_db.oc_team_monitor 结构
DROP TABLE IF EXISTS `oc_team_monitor`;
CREATE TABLE IF NOT EXISTS `oc_team_monitor` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tenant_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户编码',
  `team_code` int(11) DEFAULT NULL COMMENT '团队编码',
  `start_time` datetime DEFAULT NULL COMMENT '服务开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '服务结束时间',
  `wait_count` int(11) DEFAULT NULL COMMENT '排队人数',
  `create_date` date DEFAULT NULL COMMENT '创建时间（按天）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `team_code_create_date` (`team_code`,`create_date`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 正在导出表  oc_db.oc_team_monitor 的数据：~3 rows (大约)
/*!40000 ALTER TABLE `oc_team_monitor` DISABLE KEYS */;
INSERT INTO `oc_team_monitor` (`id`, `tenant_code`, `team_code`, `start_time`, `end_time`, `wait_count`, `create_date`) VALUES
	(1, '1', 1, '2020-07-29 17:36:38', NULL, 0, '2020-07-29'),
	(2, '1', 1, '2020-07-30 14:37:28', NULL, 0, '2020-07-30'),
	(3, '1', 1, '2020-07-31 10:40:01', NULL, 0, '2020-07-31');
/*!40000 ALTER TABLE `oc_team_monitor` ENABLE KEYS */;

-- 导出  表 oc_db.oc_team_skill 结构
DROP TABLE IF EXISTS `oc_team_skill`;
CREATE TABLE IF NOT EXISTS `oc_team_skill` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tenant_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `team_code` int(11) DEFAULT NULL,
  `skill_code` int(11) DEFAULT NULL,
  `skill_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '1' COMMENT '0 无效 1有效',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `skill_code` (`skill_code`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 正在导出表  oc_db.oc_team_skill 的数据：~1 rows (大约)
/*!40000 ALTER TABLE `oc_team_skill` DISABLE KEYS */;
INSERT INTO `oc_team_skill` (`id`, `tenant_code`, `team_code`, `skill_code`, `skill_name`, `flag`, `create_time`) VALUES
	(1, '1', 1, 1, '测试队列', '1', '2020-07-29 16:33:25');
/*!40000 ALTER TABLE `oc_team_skill` ENABLE KEYS */;

-- 导出  表 oc_db.oc_tenant 结构
DROP TABLE IF EXISTS `oc_tenant`;
CREATE TABLE IF NOT EXISTS `oc_tenant` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '租户编码',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '租户名称',
  `effect_time` int(11) NOT NULL DEFAULT '0' COMMENT '接入生效时间（秒）',
  `login_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '1',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 正在导出表  oc_db.oc_tenant 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `oc_tenant` DISABLE KEYS */;
/*!40000 ALTER TABLE `oc_tenant` ENABLE KEYS */;

-- 导出  表 oc_db.oc_term_classify 结构
DROP TABLE IF EXISTS `oc_term_classify`;
CREATE TABLE IF NOT EXISTS `oc_term_classify` (
  `classify_id` bigint(11) NOT NULL AUTO_INCREMENT,
  `tenant_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户编码',
  `team_code` int(11) DEFAULT NULL,
  `classify_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '0 系统常用语类型 1个人常用语类型',
  `staff` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `sort_num` bigint(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`classify_id`),
  KEY `team_code` (`team_code`)
) ENGINE=InnoDB AUTO_INCREMENT=948 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 正在导出表  oc_db.oc_term_classify 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `oc_term_classify` DISABLE KEYS */;
/*!40000 ALTER TABLE `oc_term_classify` ENABLE KEYS */;

-- 导出  表 oc_db.oc_term_word 结构
DROP TABLE IF EXISTS `oc_term_word`;
CREATE TABLE IF NOT EXISTS `oc_term_word` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `classify_id` bigint(11) DEFAULT NULL COMMENT '分类编码',
  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '0 系统常用语 1个人常用语',
  `staff` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  `keyword` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '搜索关键字',
  `content` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '常用语内容',
  `sort_num` bigint(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `classify_id` (`classify_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8520 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 正在导出表  oc_db.oc_term_word 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `oc_term_word` DISABLE KEYS */;
/*!40000 ALTER TABLE `oc_term_word` ENABLE KEYS */;

-- 导出  表 oc_db.oc_waiter 结构
DROP TABLE IF EXISTS `oc_waiter`;
CREATE TABLE IF NOT EXISTS `oc_waiter` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `tenant_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '租户编码',
  `team_code` int(11) NOT NULL DEFAULT '0' COMMENT '团队编码',
  `waiter_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '账号',
  `waiter_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '工号',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '4' COMMENT '1 在线 2忙碌 3挂起 4离线',
  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '1 售前 2售后',
  `shunt` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '0 不分流 1分流',
  `cur_reception` int(11) NOT NULL DEFAULT '0',
  `max_reception` int(11) NOT NULL DEFAULT '0' COMMENT '最大接待数 设置为0表示无上限',
  `auto_reply` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '是否开启自动回复：0 不启用 1 启用',
  `reply_msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '自动回复消息内容',
  `real_name` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '真实姓名',
  `mobile` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '手机号码',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '密码',
  `flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '是否有效： 0无效 1有效',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `waiter_name` (`waiter_name`),
  UNIQUE KEY `waiter_code` (`waiter_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=596 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 正在导出表  oc_db.oc_waiter 的数据：~1 rows (大约)
/*!40000 ALTER TABLE `oc_waiter` DISABLE KEYS */;
INSERT INTO `oc_waiter` (`id`, `tenant_code`, `team_code`, `waiter_name`, `waiter_code`, `status`, `type`, `shunt`, `cur_reception`, `max_reception`, `auto_reply`, `reply_msg`, `real_name`, `mobile`, `password`, `flag`, `create_time`, `create_by`) VALUES
	(1, '1', 1, 'test', 'test01', '1', '1', '1', 0, 5, '1', '您好', 'test', '18111111111', '123456', '1', '2020-07-23 17:16:12', 'admin');
/*!40000 ALTER TABLE `oc_waiter` ENABLE KEYS */;

-- 导出  表 oc_db.oc_waiter_log 结构
DROP TABLE IF EXISTS `oc_waiter_log`;
CREATE TABLE IF NOT EXISTS `oc_waiter_log` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `tenant_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户编码',
  `team_code` int(11) DEFAULT NULL COMMENT '团队编码',
  `waiter_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '客服账号',
  `waiter_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '客服工号',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '1 在线 2忙碌 3离开',
  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '操作：1登录 2切换状态 3登出 4异常登出',
  `create_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9829 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 正在导出表  oc_db.oc_waiter_log 的数据：~53 rows (大约)
/*!40000 ALTER TABLE `oc_waiter_log` DISABLE KEYS */;
INSERT INTO `oc_waiter_log` (`id`, `tenant_code`, `team_code`, `waiter_name`, `waiter_code`, `ip`, `status`, `type`, `create_date`) VALUES
	(9776, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-07-23 17:34:29'),
	(9777, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-07-23 17:34:36'),
	(9778, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-07-23 18:01:28'),
	(9779, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-07-23 18:02:36'),
	(9780, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-07-23 18:26:30'),
	(9781, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-07-23 18:26:33'),
	(9782, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-07-23 18:27:12'),
	(9783, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-07-23 18:27:14'),
	(9784, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-07-23 18:29:11'),
	(9785, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-07-23 18:29:15'),
	(9786, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-07-23 18:30:23'),
	(9787, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-07-23 18:32:06'),
	(9788, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-07-24 09:44:04'),
	(9789, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-07-24 09:44:24'),
	(9790, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-07-24 17:16:06'),
	(9791, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-07-24 17:16:12'),
	(9792, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-07-30 15:30:58'),
	(9793, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-07-30 15:51:30'),
	(9794, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-07-30 15:56:13'),
	(9795, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-07-30 15:56:15'),
	(9796, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-07-30 15:57:14'),
	(9797, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-07-30 15:57:15'),
	(9798, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-07-30 15:58:07'),
	(9799, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-07-30 17:49:05'),
	(9800, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-07-30 18:00:24'),
	(9801, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-07-31 10:43:38'),
	(9802, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-07-31 14:28:43'),
	(9803, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-07-31 14:41:04'),
	(9804, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-07-31 14:42:41'),
	(9805, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-07-31 14:57:10'),
	(9806, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-07-31 14:57:11'),
	(9807, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-07-31 14:57:22'),
	(9808, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-07-31 14:57:23'),
	(9809, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-07-31 14:57:46'),
	(9810, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-07-31 14:57:47'),
	(9811, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-07-31 14:57:56'),
	(9812, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-07-31 14:57:57'),
	(9813, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-07-31 15:20:44'),
	(9814, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-07-31 18:11:13'),
	(9815, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-07-31 18:11:50'),
	(9816, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-07-31 18:15:21'),
	(9817, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-07-31 18:15:43'),
	(9818, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-08-03 11:09:46'),
	(9819, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-08-03 11:09:52'),
	(9820, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-08-03 11:11:17'),
	(9821, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-08-03 11:12:38'),
	(9822, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-08-03 11:13:50'),
	(9823, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-08-03 11:14:14'),
	(9824, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-08-03 11:16:07'),
	(9825, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-08-03 11:47:47'),
	(9826, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-08-03 11:48:00'),
	(9827, '1', 1, 'test', 'test01', NULL, '4', '3', '2020-08-03 11:50:05'),
	(9828, '1', 1, 'test', 'test01', NULL, '1', '1', '2020-08-03 15:37:15');
/*!40000 ALTER TABLE `oc_waiter_log` ENABLE KEYS */;

-- 导出  表 oc_db.oc_waiter_monitor 结构
DROP TABLE IF EXISTS `oc_waiter_monitor`;
CREATE TABLE IF NOT EXISTS `oc_waiter_monitor` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tenant_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '租户编码',
  `team_code` int(11) DEFAULT NULL COMMENT '团队编码',
  `waiter_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '客服工号',
  `waiter_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '客服账号',
  `reception_count` int(11) DEFAULT NULL COMMENT '接待数',
  `status` int(11) DEFAULT NULL COMMENT '状态 1在线 2忙碌 3挂起 4离线',
  `online_time` bigint(20) DEFAULT NULL COMMENT '客服在线时长（秒）',
  `waiter_busy_time` bigint(20) DEFAULT NULL COMMENT '客服忙碌时长（秒）',
  `waiter_hang_time` bigint(20) DEFAULT NULL COMMENT '客服挂起时长（秒）',
  `sys_busy_time` bigint(20) DEFAULT NULL COMMENT '系统判断客服忙碌时长（秒）',
  `status_update_time` datetime DEFAULT NULL COMMENT '状态更新时间',
  `service_begin_time` datetime DEFAULT NULL COMMENT '服务开始时间',
  `service_end_time` datetime DEFAULT NULL COMMENT '服务结束时间',
  `create_date` date NOT NULL COMMENT '创建时间（按天）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `create_date_waiter_code_team_code` (`create_date`,`waiter_code`,`team_code`),
  KEY `waiter_name` (`waiter_name`)
) ENGINE=InnoDB AUTO_INCREMENT=572 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 正在导出表  oc_db.oc_waiter_monitor 的数据：~5 rows (大约)
/*!40000 ALTER TABLE `oc_waiter_monitor` DISABLE KEYS */;
INSERT INTO `oc_waiter_monitor` (`id`, `tenant_code`, `team_code`, `waiter_code`, `waiter_name`, `reception_count`, `status`, `online_time`, `waiter_busy_time`, `waiter_hang_time`, `sys_busy_time`, `status_update_time`, `service_begin_time`, `service_end_time`, `create_date`) VALUES
	(567, '1', 1, 'test01', 'test', 0, 4, 186, 0, 0, 0, '2020-07-23 18:32:06', '2020-07-23 17:34:30', '2020-07-23 18:32:06', '2020-07-23'),
	(568, '1', 1, 'test01', 'test', 0, 4, 25, 0, 0, 0, '2020-07-24 17:16:12', '2020-07-24 09:44:04', '2020-07-24 17:16:12', '2020-07-24'),
	(569, '1', 1, 'test01', 'test', 0, 4, 1072, 0, 0, 0, '2020-07-30 18:00:24', '2020-07-30 15:30:58', '2020-07-30 18:00:24', '2020-07-30'),
	(570, '1', 1, 'test01', 'test', 0, 4, 3079, 0, 0, 672, '2020-07-31 18:15:43', '2020-07-31 10:43:38', '2020-07-31 18:15:43', '2020-07-31'),
	(571, '1', 1, 'test01', 'test', 0, 1, 2136, 0, 0, 0, '2020-08-03 15:37:15', '2020-08-03 11:09:46', '2020-08-03 11:50:05', '2020-08-03');
/*!40000 ALTER TABLE `oc_waiter_monitor` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
