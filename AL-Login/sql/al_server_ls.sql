SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for account_data
-- ----------------------------

DROP TABLE IF EXISTS `account_data`;
CREATE TABLE `account_data` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(45) NOT NULL,
  `password` varchar(65) NOT NULL,
  `activated` tinyint(1) NOT NULL default '1',
  `access_level` tinyint(3) NOT NULL default '0',
  `membership` tinyint(3) NOT NULL default '0',
  `old_membership` tinyint(3) NOT NULL default '0',
  `last_server` tinyint(3) NOT NULL default '-1',
  `last_ip` varchar(20) default NULL,
  `last_mac` varchar(20) NOT NULL default 'xx-xx-xx-xx-xx-xx',
  `ip_force` varchar(20) default NULL,
  `expire` date default NULL,
  `toll` bigint(13) NOT NULL default '0',
  `question` varchar(50) default NULL,
  `answer` varchar(50) default NULL,
  `balance` float default NULL,
  `age_limit` tinyint(99) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- 
-- If no Web Site will be configured, please add in account data `email` varchar(50) default NULL
--

-- ----------------------------
-- Table structure for account_rewards
-- ----------------------------

DROP TABLE IF EXISTS `account_rewards`;
CREATE TABLE `account_rewards` (
  `uniqId` int(11) NOT NULL auto_increment,
  `accountId` int(11) NOT NULL,
  `added` varchar(70) NOT NULL default '',
  `points` decimal(20,0) NOT NULL default '0',
  `received` varchar(70) NOT NULL default '0',
  `rewarded` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`uniqId`),
  KEY `FK_account_rewards` (`accountId`),
  CONSTRAINT `FK_account_rewards` FOREIGN KEY (`accountId`) REFERENCES `account_data` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for account_time
-- ----------------------------

DROP TABLE IF EXISTS `account_time`;
CREATE TABLE `account_time` (
  `account_id` int(11) NOT NULL,
  `last_active` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `expiration_time` timestamp NULL default NULL,
  `session_duration` int(10) default '0',
  `accumulated_online` int(10) default '0',
  `accumulated_rest` int(10) default '0',
  `penalty_end` timestamp NULL default NULL,
  PRIMARY KEY  (`account_id`),
  CONSTRAINT `FK_account_time` FOREIGN KEY (`account_id`) REFERENCES `account_data` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for banned_ip
-- ----------------------------

DROP TABLE IF EXISTS `banned_ip`;
CREATE TABLE `banned_ip` (
  `id` int(11) NOT NULL auto_increment,
  `mask` varchar(45) NOT NULL,
  `time_end` timestamp NULL default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `mask` (`mask`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for banned_mac
-- ----------------------------

DROP TABLE IF EXISTS `banned_mac`;
CREATE TABLE `banned_mac` (
  `uniId` int(10) NOT NULL auto_increment,
  `address` varchar(20) NOT NULL,
  `time` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP,
  `details` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`uniId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for gameservers
-- ----------------------------

DROP TABLE IF EXISTS `gameservers`;
CREATE TABLE `gameservers` (
  `id` int(11) NOT NULL auto_increment,
  `mask` varchar(45) NOT NULL,
  `password` varchar(65) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `gameservers` (`id`, `mask`, `password`) VALUES
(1, '*', 'aion');

-- ----------------------------
-- Table structure for player_transfers
-- ----------------------------

DROP TABLE IF EXISTS `player_transfers`;
CREATE TABLE `player_transfers` (
  `id` int(11) NOT NULL auto_increment,
  `source_server` tinyint(3) NOT NULL,
  `target_server` tinyint(3) NOT NULL,
  `source_account_id` int(11) NOT NULL,
  `target_account_id` int(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `status` tinyint(1) NOT NULL default '0',
  `time_added` varchar(100) default NULL,
  `time_performed` varchar(100) default NULL,
  `time_done` varchar(100) default NULL,
  `comment` text,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tasks
-- ----------------------------

DROP TABLE IF EXISTS `tasks`;
CREATE TABLE `tasks` (
  `id` int(5) NOT NULL auto_increment,
  `task_type` enum('SHUTDOWN','RESTART','CLEAN_ACCOUNTS') NOT NULL,
  `trigger_type` enum('FIXED_IN_TIME','AFTER_RESTART') NOT NULL,
  `exec_param` text,
  `trigger_param` text NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for svstats
-- ----------------------------

DROP TABLE IF EXISTS `svstats`;
CREATE TABLE `svstats` (
  `id` int(11) NOT NULL auto_increment,
  `server` int(11) NOT NULL default '0',
  `status` int(11) NOT NULL default '0',
  `current` int(11) NOT NULL default '0',
  `max` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
