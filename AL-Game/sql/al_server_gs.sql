-- ----------------------------
-- Table structure for `server_variables`
-- ----------------------------

CREATE TABLE IF NOT EXISTS `server_variables` (
  `key` varchar(30) NOT NULL,
  `value` varchar(30) NOT NULL,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure `for players`
-- ----------------------------

CREATE TABLE IF NOT EXISTS `players` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `account_id` int(11) NOT NULL,
  `account_name` varchar(50) NOT NULL,
  `exp` bigint(20) NOT NULL default '0',
  `recoverexp` bigint(20) NOT NULL default '0',
  `x` float NOT NULL,
  `y` float NOT NULL,
  `z` float NOT NULL,
  `heading` int(11) NOT NULL,
  `world_id` int(11) NOT NULL,
  `gender` enum('MALE','FEMALE') NOT NULL,
  `race` enum('ASMODIANS','ELYOS') NOT NULL,
  `player_class` enum('WARRIOR','GLADIATOR','TEMPLAR','SCOUT','ASSASSIN','RANGER','MAGE','SORCERER','SPIRIT_MASTER','PRIEST','CLERIC','CHANTER') NOT NULL,
  `creation_date` timestamp NULL default NULL,
  `deletion_date` timestamp NULL default NULL,
  `last_online` timestamp NULL default NULL on update CURRENT_TIMESTAMP,
  `quest_expands` tinyint(1) NOT NULL default '0',
  `npc_expands` tinyint(1) NOT NULL default '0',
  `advenced_stigma_slot_size` TINYINT(1) NOT NULL DEFAULT '0',
  `warehouse_size` tinyint(1) NOT NULL default '0',
  `mailbox_letters` tinyint(4) NOT NULL default '0',
  `bind_point` INT NOT NULL default '0',
  `unstuck` BIGINT(20) UNSIGNED NOT NULL default '0',
  `gmconfig` INT NOT NULL default '0',
  `title_id` int(3) NOT NULL default '-1',
  `dp` int(3) NOT NULL DEFAULT '0',
  `soul_sickness` tinyint(1) UNSIGNED NOT NULL DEFAULT '0',
  `reposte_energy` bigint(20) NOT NULL default '0',
  `online` tinyint(1) NOT NULL default '0',
  `note` text,
  `mentor_flag_time` INT(11) NOT NULL DEFAULT '0',
  `last_transfer_time` decimal(20) NOT NULL default '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_unique` (`name`),
  INDEX (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `player_appearance`
-- ----------------------------

CREATE TABLE IF NOT EXISTS `player_appearance` (
  `player_id` int(11) NOT NULL,
  `face` int(11) NOT NULL,
  `hair` int(11) NOT NULL,
  `deco` int(11) NOT NULL,
  `tattoo` int(11) NOT NULL,
  `face_contour` INT(11) NOT NULL,
  `expression` INT(11) NOT NULL,
  `jaw_line` INT(11) NOT NULL,
  `skin_rgb` int(11) NOT NULL,
  `hair_rgb` int(11) NOT NULL,
  `lip_rgb` int(11) NOT NULL,
  `eye_rgb` int(11) NOT NULL,
  `face_shape` int(11) NOT NULL,
  `forehead` int(11) NOT NULL,
  `eye_height` int(11) NOT NULL,
  `eye_space` int(11) NOT NULL,
  `eye_width` int(11) NOT NULL,
  `eye_size` int(11) NOT NULL,
  `eye_shape` int(11) NOT NULL,
  `eye_angle` int(11) NOT NULL,
  `brow_height` int(11) NOT NULL,
  `brow_angle` int(11) NOT NULL,
  `brow_shape` int(11) NOT NULL,
  `nose` int(11) NOT NULL,
  `nose_bridge` int(11) NOT NULL,
  `nose_width` int(11) NOT NULL,
  `nose_tip` int(11) NOT NULL,
  `cheek` int(11) NOT NULL,
  `lip_height` int(11) NOT NULL,
  `mouth_size` int(11) NOT NULL,
  `lip_size` int(11) NOT NULL,
  `smile` int(11) NOT NULL,
  `lip_shape` int(11) NOT NULL,
  `jaw_height` int(11) NOT NULL,
  `chin_jut` int(11) NOT NULL,
  `ear_shape` int(11) NOT NULL,
  `head_size` int(11) NOT NULL,
  `neck` int(11) NOT NULL,
  `neck_length` int(11) NOT NULL,
  `shoulders` int(11) NOT NULL,
  `shoulder_size` int(11) NOT NULL,
  `torso` int(11) NOT NULL,
  `chest` int(11) NOT NULL,
  `waist` int(11) NOT NULL,
  `hips` int(11) NOT NULL,
  `arm_thickness` int(11) NOT NULL,
  `arm_length` int(11) NOT NULL,
  `hand_size` int(11) NOT NULL,
  `leg_thickness` int(11) NOT NULL,
  `leg_length` int(11) NOT NULL,
  `foot_size` int(11) NOT NULL,
  `facial_rate` int(11) NOT NULL,
  `voice` int(11) NOT NULL,
  `height` float NOT NULL,
  PRIMARY KEY (`player_id`),
  CONSTRAINT `player_id_fk` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `player_macrosses`
-- ----------------------------

CREATE TABLE IF NOT EXISTS `player_macrosses` (
  `player_id` int(11) NOT NULL,
  `order` int(3) NOT NULL,
  `macro` text NOT NULL,
  UNIQUE KEY `main` (`player_id`,`order`),
  FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `player_titles`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `player_titles` (
  `player_id` int(11) NOT NULL,
  `title_id` int(11) NOT NULL,
  `remaining` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`player_id`,`title_id`),
  FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `friends`
-- ----------------------------

CREATE TABLE IF NOT EXISTS `friends` (
  `player` int(11) NOT NULL,
  `friend` int(11) NOT NULL,
  PRIMARY KEY (`player`,`friend`),
  FOREIGN KEY (`player`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`friend`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `blocks`
-- ----------------------------

CREATE TABLE IF NOT EXISTS `blocks` (
  `player` int(11) NOT NULL,
  `blocked_player` int(11) NOT NULL,
  `reason` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`player`,`blocked_player`),
  FOREIGN KEY (`player`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`blocked_player`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `player_settings`
-- ----------------------------

CREATE TABLE IF NOT EXISTS `player_settings` (
  `player_id` int(11) NOT NULL,
  `settings_type` tinyint(1) NOT NULL,
  `settings` BLOB NOT NULL,
  PRIMARY KEY (`player_id`, `settings_type`),
  CONSTRAINT `ps_pl_fk` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `player_skills`
-- ----------------------------

CREATE TABLE IF NOT EXISTS `player_skills` (
  `player_id` int(11) NOT NULL,
  `skill_id` int(11) NOT NULL,
  `skill_level` int(3) NOT NULL default '1',
  PRIMARY KEY (`player_id`,`skill_id`),
  FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `inventory`
-- ----------------------------

CREATE TABLE IF NOT EXISTS `inventory` (
  `item_unique_id` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  `item_count` bigint(20) NOT NULL DEFAULT '0',
  `item_color` int(11) NOT NULL DEFAULT '0',
  `item_creator` varchar(50),
  `expire_time` int(11) NOT NULL DEFAULT '0',
  `activation_count` int(11) NOT NULL DEFAULT '0',
  `item_owner` int(11) NOT NULL,
  `is_equiped` TINYINT(1) NOT NULL DEFAULT '0',
  `is_soul_bound` TINYINT(1) NOT NULL DEFAULT '0', 
  `slot` INT NOT NULL DEFAULT '0',
  `item_location` TINYINT(1) DEFAULT '0',
  `enchant` TINYINT(1) DEFAULT '0',
  `item_skin`  int(11) NOT NULL DEFAULT 0,
  `fusioned_item` INT(11) NOT NULL DEFAULT '0',
  `optional_socket` INT(1) NOT NULL DEFAULT '0',
  `optional_fusion_socket` INT(1) NOT NULL DEFAULT '0',
  `charge` MEDIUMINT NOT NULL DEFAULT '0',
  PRIMARY KEY (`item_unique_id`),
  KEY `item_owner`(`item_owner`),
  KEY `item_location`(`item_location`),
  KEY `is_equiped`(`is_equiped`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `item_stones`
-- ----------------------------

CREATE TABLE IF NOT EXISTS `item_stones` (
  `item_unique_id` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  `slot` int(2) NOT NULL,
  `category` int(2) NOT NULL default 0,
  PRIMARY KEY (`item_unique_id`, `slot`, `category`),
  FOREIGN KEY (`item_unique_id`) references inventory (`item_unique_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `player_quests`
-- ----------------------------

CREATE TABLE IF NOT EXISTS `player_quests` (
  `player_id` int(11) NOT NULL,
  `quest_id` int(10) unsigned NOT NULL default '0',
  `status` varchar(10) NOT NULL default 'NONE',
  `quest_vars` int(10) unsigned NOT NULL default '0',
  `quest_vars2` int(10) unsigned NOT NULL default '0',
  `complete_count` int(3) unsigned NOT NULL default '0',
  `next_repeat_time` timestamp NULL default NULL,
  `reward` smallint(3) NULL,
  `complete_time` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`player_id`,`quest_id`),
  FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `player_npc_factions`
-- ----------------------------
CREATE TABLE IF NOT EXISTS  `player_npc_factions` (
  `player_id` int(11) NOT NULL,
  `faction_id` int(2) NOT NULL,
  `active` tinyint(1) NOT NULL,
  `time` int(11) NOT NULL,
  `state` enum('NOTING','START','COMPLETE') NOT NULL default 'NOTING',
  `quest_id` int(6) NOT NULL default '0',
  PRIMARY KEY  (`player_id`,`faction_id`),
  CONSTRAINT `player_npc_factions_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `abyss_rank`
-- ----------------------------

CREATE TABLE IF NOT EXISTS `abyss_rank` (
  `player_id` int(11) NOT NULL,
  `daily_ap`  int(11) NOT NULL,
  `weekly_ap`  int(11) NOT NULL,
  `ap` int(11) NOT NULL,
  `rank` int(2) NOT NULL default '1',
  `top_ranking`  int(4) NOT NULL,
  `daily_kill`  int(5) NOT NULL,
  `weekly_kill`  int(5) NOT NULL,
  `all_kill` int(4) NOT NULL default '0',
  `max_rank` int(2) NOT NULL default '1',
  `last_kill`  int(5) NOT NULL,
  `last_ap`  int(11) NOT NULL,
  `last_update`  decimal(20,0) NOT NULL,
  `rank_pos` INT(11) NOT NULL DEFAULT '0' ,
  `old_rank_pos` INT(11) NOT NULL DEFAULT '0',
  `rank_ap` INT(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`player_id`),
  FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `legions`
-- ----------------------------

CREATE TABLE IF NOT EXISTS `legions` (
  `id` int(11) NOT NULL,
  `name` varchar(32) NOT NULL,
  `level` int(1) NOT NULL DEFAULT '1',
  `contribution_points` INT NOT NULL DEFAULT '0',
  `deputy_permission` INT(11) NOT NULL DEFAULT 7692,
  `centurion_permission` INT(11) NOT NULL DEFAULT 7176,
  `legionary_permission` INT(11) NOT NULL DEFAULT 6144,
  `volunteer_permission` INT(11) NOT NULL DEFAULT 2048,
  `disband_time` int(11) NOT NULL default '0',
  `rank_cp` INT(11) NOT NULL DEFAULT '0',
  `rank_pos` INT(11) NOT NULL DEFAULT '0',
  `old_rank_pos` INT(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_unique` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `legion_announcement_list` (
  `legion_id` int(11) NOT NULL,
  `announcement` varchar(256) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`legion_id`) REFERENCES `legions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `legion_members` (
  `legion_id` int(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `nickname` varchar(10) NOT NULL default '',  
  `rank` enum( 'BRIGADE_GENERAL','CENTURION','LEGIONARY','DEPUTY','VOLUNTEER' ) NOT NULL DEFAULT 'VOLUNTEER',
  `selfintro` varchar(32) default '',
  PRIMARY KEY (`player_id`),
  KEY `player_id`(`player_id`),
  FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`legion_id`) REFERENCES `legions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `legion_emblems` (
  `legion_id` int(11) NOT NULL,
  `emblem_id` int(1) NOT NULL default '0',
  `color_r` int(3) NOT NULL default '0',  
  `color_g` int(3) NOT NULL default '0', 
  `color_b` int(3) NOT NULL default '0',
  `emblem_type` enum('DEFAULT', 'CUSTOM') NOT NULL DEFAULT 'DEFAULT',
  `emblem_data` longblob,
  PRIMARY KEY (`legion_id`),
  FOREIGN KEY (`legion_id`) REFERENCES `legions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `legion_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `legion_id` int(11) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `history_type` enum('CREATE','JOIN','KICK','APPOINTED','EMBLEM_REGISTER','EMBLEM_MODIFIED','ITEM_DEPOSIT','ITEM_WITHDRAW','KINAH_DEPOSIT','KINAH_WITHDRAW') NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` varchar(30) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`legion_id`) REFERENCES `legions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `player_recipes`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `player_recipes` (
  `player_id` int(11) NOT NULL,
  `recipe_id` int(11) NOT NULL,
  PRIMARY KEY (`player_id`,`recipe_id`),
  FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `player_punisments`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `player_punishments` (
  `player_id` int(11) NOT NULL,
  `punishment_type` enum('PRISON','GATHER','CHARBAN') NOT NULL,
  `start_time` int(10) unsigned default '0',
  `duration` int(10) unsigned default '0',
  `reason` text,
  PRIMARY KEY  (`player_id`,`punishment_type`),
  CONSTRAINT `player_punishments_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `mail_table`
-- ----------------------------
CREATE TABLE IF NOT EXISTS  `mail` (
  `mail_unique_id` int(11) NOT NULL,
  `mail_recipient_id` int(11) NOT NULL,
  `sender_name` varchar(16) character set utf8 NOT NULL,
  `mail_title` varchar(20) character set utf8 NOT NULL,
  `mail_message` varchar(1000) character set utf8 NOT NULL,
  `unread` tinyint(4) NOT NULL default '1',
  `attached_item_id` int(11) NOT NULL,
  `attached_kinah_count` bigint(20) NOT NULL,
  `express` tinyint(4) NOT NULL default '0', 
  `recieved_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY (`mail_unique_id`),
  INDEX (`mail_recipient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `player_effects`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `player_effects` (
  `player_id` int(11) NOT NULL,
  `skill_id` int(11) NOT NULL,
  `skill_lvl` tinyint NOT NULL,
  `current_time` int(11) NOT NULL,
  `end_time` bigint(13) NOT NULL,
  PRIMARY KEY (`player_id`, `skill_id`),
  FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `item_cooldowns`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `item_cooldowns` (
  `player_id` int(11) NOT NULL,
  `delay_id` int(11) NOT NULL,
  `use_delay` SMALLINT UNSIGNED NOT NULL,
  `reuse_time` BIGINT(13) NOT NULL,
  PRIMARY KEY (`player_id`, `delay_id`),
  FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `broker`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `broker` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `item_pointer` int(11) NOT NULL DEFAULT '0',
  `item_id` int(11) NOT NULL,
  `item_count` bigint(20) NOT NULL,
  `item_creator` varchar(50),
  `seller` varchar(50) NOT NULL,
  `price` bigint(20) NOT NULL DEFAULT '0',
  `broker_race` enum('ELYOS','ASMODIAN') NOT NULL,
  `expire_time` timestamp NOT NULL DEFAULT '2010-01-01 02:00:00',
  `settle_time` timestamp NOT NULL DEFAULT '2010-01-01 02:00:00',
  `seller_id` int(11) NOT NULL,
  `is_sold` tinyint(1) NOT NULL,
  `is_settled` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `bookmark`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `bookmark` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(50) default NULL,
  `char_id` int(11) NOT NULL,
  `x` float NOT NULL,
  `y` float NOT NULL,
  `z` float NOT NULL,
  `world_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `announcements`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `announcements` (
  `id` int(3) NOT NULL AUTO_INCREMENT,
  `announce` text NOT NULL,
  `faction` enum('ALL','ASMODIANS','ELYOS') NOT NULL DEFAULT 'ALL',
  `type` enum('SHOUT','ORANGE','YELLOW','WHITE','SYSTEM') NOT NULL default 'SYSTEM',
  `delay` int(4) NOT NULL DEFAULT '1800',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `player_life_stats`
-- ----------------------------

CREATE  TABLE IF NOT EXISTS `player_life_stats` (
  `player_id` INT(11) NOT NULL ,
  `hp` INT(11) NOT NULL DEFAULT 1 ,
  `mp` INT(11) NOT NULL DEFAULT 1 ,
  `fp` INT(11) NOT NULL DEFAULT 1 ,
  PRIMARY KEY (`player_id`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `siege_locations`
-- ----------------------------

CREATE TABLE IF NOT EXISTS `siege_locations` (
  `id` int(11) NOT NULL,
  `race` enum('ELYOS', 'ASMODIANS', 'BALAUR') NOT NULL,
  `legion_id` int (11) NOT NULL,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `petitions`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `petitions` (
  `id` bigint(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `message` text NOT NULL,
  `add_data` varchar(255) default NULL,
  `time` bigint(11) NOT NULL default '0',
  `status` enum('PENDING','IN_PROGRESS','REPLIED') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `tasks`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `tasks` (
  `id` int(5) NOT NULL,
  `task` varchar(50) NOT NULL,
  `type` enum('FIXED_IN_TIME') NOT NULL,
  `last_activation` timestamp NOT NULL DEFAULT '2010-01-01 00:00:00',
  `start_time` varchar(8) NOT NULL,
  `delay` int(10) NOT NULL,
  `param` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `player_pets`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `player_pets` (
  `player_id` int(11) NOT NULL,
  `pet_id` int(11) NOT NULL,
  `decoration` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `hungry_level` int(10) NOT NULL DEFAULT '0',
  `reuse_time` bigint(20) NOT NULL DEFAULT '0',
  `birthday` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `mood_started` bigint(20) NOT NULL DEFAULT '0',
  `counter` int(11) NOT NULL DEFAULT '0',
  `mood_cd_started` bigint(20) NOT NULL DEFAULT '0',
  `gift_cd_started` bigint(20) NOT NULL DEFAULT '0',
  `despawn_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`player_id`, `pet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `portal_cooldowns`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `portal_cooldowns` (
  `player_id` int(11) NOT NULL,
  `world_id` int(11) NOT NULL,
  `reuse_time` BIGINT(13) NOT NULL,
  PRIMARY KEY (`player_id`, `world_id`),
  FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `player_passkey`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `player_passkey` (
  `account_id` int(11) NOT NULL,
  `passkey` varchar(32) NOT NULL DEFAULT '',
  PRIMARY KEY (`account_id`,`passkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `guides`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `guides` (
  `guide_id` int(11) NOT NULL auto_increment,
  `player_id` int(11) NOT NULL,
  `title` varchar(80) NOT NULL,
  PRIMARY KEY (`guide_id`),
  FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `ingameshop`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ingameshop` (
  `object_id` int(11) NOT NULL auto_increment,
  `item_id` int(11) NOT NULL,
  `item_count` int(11) NOT NULL DEFAULT '0',
  `item_price` int(11) NOT NULL DEFAULT '0',
  `category` int(11) NOT NULL DEFAULT '0',
  `list` int(11) NOT NULL DEFAULT '0',
  `sales_ranking` int(11) NOT NULL DEFAULT '0',
  `description` varchar(20) NOT NULL,
  PRIMARY KEY (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `old_names`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `old_names` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `player_id` int(11) unsigned NOT NULL,
  `old_name` varchar(50) NOT NULL,
  `new_name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `player_emotions`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `player_emotions` (
  `player_id` int(11) NOT NULL,
  `emotion` int(11) NOT NULL,
  `remaining` INT(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`player_id`, `emotion`),
  FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `spawns`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `spawns` (
  `spawn_id` int(10) NOT NULL auto_increment,
  `npc_id` int(10) NOT NULL,
  `npc_name` varchar(50) NOT NULL default '',
  `map_id` int(10) NOT NULL,
  `pool_size` int(5) NOT NULL default 1,
  `anchor` varchar(100),
  `handler` enum('RIFT','STATIC') default NULL,
  `spawn_time` enum('ALL','DAY','NIGHT') NOT NULL default 'ALL',
  `walker_id` varchar(100) DEFAULT NULL,
  `random_walk` int(10) NOT NULL default '0',
  `static_id` int(10) NOT NULL default '0',
  `fly` tinyint(1) NOT NULL default '0',
  `respawn_time` int(10) NOT NULL default '0',
  `last_despawn_time` timestamp NULL default NULL,
  `date_added` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `author` varchar(50) NOT NULL default 'system',
  PRIMARY KEY (`spawn_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT = 1000000;

-- ----------------------------
-- Table structure for `spawns_loc`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `spawns_loc` (
  `spawn_id` int(10) NOT NULL,
  `x` float NOT NULL,
  `y` float NOT NULL,
  `z` float NOT NULL,
  `heading` tinyint(3) NOT NULL,
  KEY `fk_sp_id` (`spawn_id`),
  CONSTRAINT `fk_sp_id` FOREIGN KEY (`spawn_id`) REFERENCES `spawns` (`spawn_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `siege_spawns`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `siege_spawns` (
  `spawn_id` int(10) NOT NULL,
  `siege_id` int(10) NOT NULL,
  `race` enum('ELYOS','ASMODIANS','BALAUR') NOT NULL,
  `protector` int(10) default '0',
  `stype` enum('PEACE','GUARD','ARTIFACT','PROTECTOR','MINE','PORTAL','GENERATOR','SPRING','RACEPROTECTOR','UNDERPASS') default NULL,
   PRIMARY KEY (`spawn_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `spawn_pools`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `spawn_pools` (
  `pool_id` int(5) NOT NULL auto_increment,
  `pool` tinyint(3) NOT NULL,
   PRIMARY KEY (`pool_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `player_bind_point`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `player_bind_point` (
  `player_id` int(11) NOT NULL,
  `map_id` int(11) NOT NULL,
  `x` FLOAT NOT NULL,
  `y` FLOAT NOT NULL,
  `z` FLOAT NOT NULL,
  `heading` int(3) NOT NULL,
  PRIMARY KEY (`player_id`),
  FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `player_cooldowns`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `player_cooldowns` (
  `player_id` int(11) NOT NULL,
  `cooldown_id` int(6) NOT NULL,
  `reuse_delay` bigint(13) NOT NULL,
  PRIMARY KEY (`player_id`,`cooldown_id`),
  FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for `craft_cooldowns`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `craft_cooldowns` (
  `player_id` int(11) unsigned NOT NULL,
  `delay_id` int(11) unsigned NOT NULL,
  `reuse_time` bigint(13) unsigned NOT NULL,
  PRIMARY KEY (`player_id`,`delay_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `surveys`
-- ----------------------------
DROP TABLE IF EXISTS `surveys`;
CREATE TABLE `surveys` (
  `unique_id` int(11) NOT NULL AUTO_INCREMENT,
  `owner_id` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  `item_count` decimal(20,0) NOT NULL DEFAULT '1',
  `html_text` text NOT NULL,
  `html_radio` varchar(100) NOT NULL DEFAULT 'accept',
  `used` tinyint(1) NOT NULL DEFAULT '0',
  `used_time` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`unique_id`),
  KEY `owner_id` (`owner_id`),
  CONSTRAINT `surveys_ibfk_1` FOREIGN KEY (`owner_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `player_motions`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `player_motions` (
  `player_id` int(11) NOT NULL,
  `motion_id` int(3) NOT NULL,
  `time` int(11) NOT NULL default '0',
  `active` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  USING BTREE (`player_id`,`motion_id`),
  CONSTRAINT `motions_player_id_fk` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `web_reward`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `web_reward` (
  `unique` int(11) NOT NULL AUTO_INCREMENT,
  `item_owner` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  `item_count` decimal(20,0) NOT NULL DEFAULT '1',
  `rewarded` tinyint(1) NOT NULL DEFAULT '0',
  `added` varchar(70) DEFAULT '',
  `received` varchar(70) DEFAULT '',
  PRIMARY KEY (`unique`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `weddings`
-- ----------------------------
DROP TABLE IF EXISTS `weddings`;
CREATE TABLE `weddings` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `player1` int(11) NOT NULL,
  `player2` int(11) NOT NULL,
  PRIMARY KEY USING BTREE (`id`) ,
  KEY `player1` (`player1`),
  KEY `player2` (`player2`),
  CONSTRAINT `weddings_ibfk_2` FOREIGN KEY (`player2`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `weddings_ibfk_1` FOREIGN KEY (`player1`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- ----------------------------
-- Table structure for `player_vars`
-- ----------------------------
DROP TABLE IF EXISTS `player_vars`;
CREATE TABLE `player_vars` (
  `player_id` int(11) NOT NULL,
  `param` varchar(255) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  `time` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`player_id`,`param`),
  CONSTRAINT `player_vars_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `skill_motions`
-- ----------------------------
DROP TABLE IF EXISTS `skill_motions`;

CREATE TABLE `skill_motions` (
  `motion_name` varchar(255) NOT NULL DEFAULT '',
  `skill_id` int(11) NOT NULL,
  `attack_speed` int(11) NOT NULL,
  `weapon_type` varchar(255) NOT NULL,
  `off_weapon_type` varchar(255) NOT NULL,
  `time` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`motion_name`,`skill_id`,`attack_speed`,`weapon_type`,`off_weapon_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `command`;

CREATE TABLE `command` (
  `name` varchar(50) NOT NULL,
  `security` tinyint(3) NOT NULL DEFAULT '12',
  `help` text NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `command` */

INSERT INTO `command` (`name`, `security`, `help`) VALUES
('add', 2, 'syntax : //add <player> <item ID> <quantity>\r\nsyntax : //add <item ID> <quantity>\r\nsyntax : //add <item ID>'''),
('addcube', 3, 'Syntax : //addcube <player name>'),
('adddrop', 3, 'syntax : //adddrop <mobid> <itemid> <min> <max> <chance>'),
('addemotion', 3, 'Syntax : //addemotion <emotion id> [expire time]'),
('addexp', 5, 'Syntax : //addexp <exp>'),
('addset', 2, 'Syntax : //addset <player> <idset>\r\n	'),
('addskill', 3, 'Syntax : //addskill <Idskill> <Lvl>'),
('addtitle', 3, 'Syntax : //addtitle <titleId> <playerName / target>'),
('admin', 3, 'Syntax : //admin'),
('ai2', 11, 'Syntax : //ai2 <set|event|event2|info|log|print|createlog|eventlog|movelog>'),
('announce', 2, 'Syntax : //announce <n | e | en | a | an> <message>\r\nn = name\r\ne = elyos\r\na = asmodian'),
('announcement', 3, 'Syntax : //announcement list - Obtain all announcements in the database.\\n\r\nSyntax : //announcement add <faction: ELYOS | ASMODIANS | ALL> <type: SYSTEM | WHITE | ORANGE | SHOUT | YELLOW> <delay in seconds> <message> - Add an announcement in the database.\\n\r\nSyntax : //announcement delete <id (see //announcement list to find all id> - Delete an announcement from the database.'),
('appearance', 2, 'Syntax : //appearance <size | voice | hair | face | deco | head_size | tattoo | reset (to reset the appearance)> <value>'),
('assault', 5, 'Syntax : //assault <radius> <amount> <npc_id1, npc_id2,...| tier20 | tier30 |...> <despawn time in secs>'),
('ban ', 5, 'Syntax : //ban <char|account|ip|mac|full> <player> <time> <reason>'),
('ban char', 7, 'Syntax : //ban char <playername> <days>/0 (for permanent) <reason>\\n\r\nNote: The current day is defined as a whole day even if it has just a few hours left!'),
('ban full', 8, 'Syntax : //ban full <player> <time>'),
('ban ip', 8, 'Syntax : //ban ip <player> <time>'),
('ban mac', 8, 'syntax : //ban mac <playername> <time>'),
('bk', 2, 'Syntaxe : //bk add <Name>\r\n//bk del <Name>\r\n//bk goto <Name>\r\n//bk list'),
('changerace', 2, 'Syntax : //changerace'),
('channel', 8, 'Syntax : //channel <on / off>'),
('clear', 5, 'Syntax : //clear <groups | allys | findgroup>'),
('commands', 12, 'Syntax :'),
('configure', 9, 'Syntax : //configure <set|show> <configname> <property> [<newvalue>]'),
('cooldown', 3, 'Syntax : //cooldown'),
('delete', 2, 'Syntax : //delete <target>'),
('delskill', 2, 'Syntax : //delskill <Player name> <all | skillId>\\n\r\nSyntax : //delskill [target] <all | skillId>'),
('dispel', 2, 'Syntaxe : //dispel'),
('dropinfo', 5, 'Syntax : //dropinfo'),
('dye', 3, 'syntax : //dye <dye color|hex color|no>'),
('enemy', 5, 'Syntax : //enemy < players | npcs | all | cancel >\\n"\r\n"Players - You''re enemy to Players of both factions.\\n" + "Npcs - You''re enemy to all Npcs and Monsters.\\n"\r\n"All - You''re enemy to Players of both factions and all Npcs.\\n"\r\n"Cancel - Cancel all. Players and Npcs have default enmity to you.'),
('energybuff', 5, 'Syntax : //energybuff repose <info|reset|add> <points>\\n\r\nSyntax : //energybuff salvation <info|reset|add> <points>\\n\r\nSyntax : //energybuff refresh <info|reset|add> <points>'),
('equip', 3, 'Syntax : //equip '),
('event', 2, 'Syntax : //event'),
('f', 0, 'Syntaxe : .f <Message>'),
('fsc', 12, 'Syntax : //fsc <value>'),
('gag', 5, 'Syntax : //gag <player> [time in minutes]'),
('givemissingskills', 0, 'Syntax : //givemissingskills'),
('gm', 3, 'Syntax : //gm <list | loginannounce | whisper >'),
('gm list', 3, 'Syntax : //gm list <on | off>\r\nPermet l''affichage ou non du membre du staff dans la liste des membres du staff.'),
('gm loginannounce', 3, 'Syntax : //gm loginannounce <on | off>\r\nPermet l''affichage ou non de l''annonce de connexion du membre du staff'),
('gm whisper', 3, 'Syntax : //gm whisper <on | off>\r\nPermet de bloquer ou non les messages privee'),
('gmlist', 0, 'Syntax : .gmlist'),
('goto', 2, 'syntax : //goto <location>'),
('gps', 3, 'Syntax : //gps'),
('grouptome', 2, 'Syntax : //grouptome <player>'),
('heal', 2, 'Syntax : //heal :Full HP and MP\\n\r\n//heal dp : Full DP, must be used on a player !\\n\r\n//heal fp : Full FP, must be used on a player\\n\r\n//heal repose : Full repose energy, must be used on a player\\n\r\n//heal <hp | hp%> : Heal given amount/percentage of HP'),
('help', 0, 'Syntax :'),
('html', 5, 'Syntax : //html show <filename>'),
('info', 2, 'Syntax : //info <target>'),
('infos', 0, 'Syntax : //info'),
('invis', 2, 'Syntax : //invis'),
('invul', 2, 'Syntax : //invul'),
('join', 0, 'Syntaxe : .join <tvt>|<pvp>'),
('kick', 5, 'Syntax : //kick <player> | <All>'),
('kill', 2, 'Syntax : //kill < target | all | <range> >'),
('kinah', 3, 'Syntax : //kinah [player] <quantity>\r\nsi quantity = 0, affichage cache des kinah'),
('legion', 3, 'Syntax : //legion info <legion name> : get list of legion members\r\nSyntax : //legion bg <legion name> <playerName> : set a new brigade general to the legion\r\nSyntax : //legion kick <playerName> : kick player to this legion\r\nSyntax : //legion invite <legion name> <playerName> : add player to legion\r\nSyntax : //legion disband <legion name> : disbands legion\r\nSyntax : //legion setlevel <legion name> <level> : sets legion level\r\nSyntax : //legion setpoints <legion name> <points> : set contributing points\r\nSyntax : //legion setname <new name> : change legion name (by target)'),
('marry', 3, 'Syntax : //marry <characterName> <characterName>'),
('missyou', 3, 'Syntax : //missyou'),
('morph', 2, 'Syntax : //morph <npcid | cancel>'),
('motion', 5, 'Syntax : //motion start - starts MotionLoggingService, plus loads data from db\\n\r\nSyntax : //motion advanced - turns on/of advanced logging info\\n\r\nSyntax : //motion as (value) - adds attack speed\\n\r\nSyntax : //motion analyze - creats .txt files in SERVER_DIR/motions with detailed info about motions\\n\r\nSyntax : //motion savetosql - saves content of MotionLoggingService to database\\n\r\nSyntax : //motion createxml - create new_motion_times.xml in static_data/skills\\n'),
('moveplayertoplayer', 3, 'Syntax : //moveplayertoplayer <characterNameToMove> <characterNameDestination>'),
('moveto', 3, 'Syntax : //moveto worldId X Y Z'),
('movetome', 2, 'Syntax : //movetome <PlayerName>'),
('movetomeall', 12, 'Syntax : //movetomeall < all | elyos | asmos >'),
('movetonpc', 2, 'Syntax : //movetonpc <npc_id|npc name>'),
('movetoplayer', 2, 'Syntax : //movetoplayer <playerName>'),
('movie', 12, 'syntax : '),
('ms', 4, 'Syntax : //ms\r\n- linex/y id npcRadius MaxRang\r\n- hcyl    id npcRadius MaxRang\r\n- cyl     id npcRadius MaxRang\r\n- star    id npcRadius nbPointed MaxRang\r\n- square  id npcRadius MaxRang\r\n- rec     id npcRadius Height Length'),
('neutral', 2, 'Syntax : //neutral <players | npcs | all | cancel>'),
('noexp', 0, 'Syntax : noexp'),
('npcskill', 5, 'Syntax : //npcskill <target>'),
('online', 3, 'Syntax : //online'),
('passkeyreset', 12, 'Syntax : //passkeyreset <player> <passkey>'),
('pet', 5, 'Syntax : //pet add <petid> <Name>\r\nSyntax : //pet del <petid> '),
('petitions', 5, 'Syntax : //petitions\r\nSyntax : //petitions <id>\r\nSyntax : //petitions <id> <reply | delete>'),
('playerinfo', 3, 'syntax : //playerinfo target'),
('promote', 12, 'Syntax : //promote <characterName> <accesslevel | membership> <mask>'),
('quest', 2, 'Syntax : //quest <start|set|show|delete>\r\nSyntax : //quest set <questId> <START|NONE|COMPLETE|REWARD> <var> [varNum]'),
('questauto', 2, 'Syntax : //questauto <questid>'),
('questrestart', 2, 'syntax : //questrestart <quest id>'),
('rank', 1, '//rank'),
('ranking', 5, 'Syntax : //ranking'),
('raw', 3, 'syntax : //raw name'),
('reload', 8, 'Syntax : //reload <command | insanity | itemjob | item | npc | config | event | html'),
('reloadspawn', 3, 'syntax : //reload <quest | skill | portal | spawn | commands | drop | gameshop | events>'),
('remove', 2, 'Syntax : //remove <player> <item ID> <quantity>'),
('removecd', 2, 'Syntax : //removecd'),
('rename', 3, 'syntax : //rename [target] <rename>'),
('res', 2, 'Syntax : //res <target>'),
('revoke', 10, 'Syntax : //revoke <characterName> <acceslevel | membership>'),
('ring', 5, 'Syntax : //ring'),
('rprison', 2, 'Syntax : //rprison <player>'),
('say', 3, 'Syntax : //say <text>'),
('see', 5, 'Syntax : //see'),
('send', 3, 'Syntax : //send [file]'),
('set', 5, 'syntax : //set <class|exp|ap|level|title|spawn>'),
('setrace', 6, 'Syntax : //setrace <elyos | asmodians>'),
('siege', 12, 'systax : //siege Help\r\n//siege start|stop <siegeLocationId>\r\n//siege list locations|sieges\r\n//siege capture <fortressOrArtifactId> <siegeRaceName|legionName|legionId>'),
('spawn', 2, 'syntax : //spawn <npc_id>'),
('spawnassemblednpc', 5, 'Syntax : //spawnassemblednpc <sapwnId>'),
('spawnupdate', 3, 'syntax : //spawnupdate'),
('speed', 2, 'Syntax : //speed valeur'),
('sprison', 2, 'syntax : //sprison <player> <delay> <reason>'),
('stat', 3, 'Syntax : //stat'),
('state', 3, 'Syntax : //state'),
('status', 5, 'Syntax : //status <alliance | group>'),
('sysmail', 12, 'Syntax : //sysmail <Recipient> <Regular||Express> <Item> <Count> <Kinah>\\n"\r\n			'),
('system', 10, 'Syntax : //system < info | memory | gc >\r\nSyntax : //system < restart | shutdown > <countdown time> <announce delay>'),
('tele', 5, 'syntax : //tele <teleportname>\r\nsyntax : //tele add <newteleportname>'),
('tele add', 8, 'Syntax : //tele add <newTeleportName>'),
('teleportation', 5, 'Syntax : //teleportation'),
('time', 5, 'Syntax : //time < dawn | day | dusk | night | desired hour (number) >'),
('tvt2', 3, 'syntax : //tvt2 <info> <start> <reg> <unreg>\r\nsyntax : //tvt2 <start> <id> <time (18 00 hours minutes)'),
('unban', 8, 'Syntax : //unban <char|account|ip|mac|full> <data>'),
('unban account', 8, 'Syntax : //unban account <accountName>'),
('unban char', 8, 'Syntax : //unban char <playerName>'),
('unban full', 8, 'Syntax : //unban full <accountName>'),
('unban ip', 8, 'Syntax : //unban account <accountName>'),
('unban mac', 8, 'Syntax : //unban mac <macAdress>'),
('ungag', 5, 'Syntax : //ungag <player>'),
('unstuck', 2, 'Syntax : //unstuck'),
('useskill', 4, 'Syntax : //useskill <skillId> <skillLevel> [true:justEffect]'),
('warp', 2, 'Syntax : //warp <@link>'),
('wc', 3, 'Syntax : //wc <ely | asm | all | default> <message>'),
('weather', 5, 'Syntax : //weather <regionName> <value(0->12)>\\n\r\nSyntax : //weather reset'),
('zone', 12, 'Syntax : //zone refresh | inside');


DROP TABLE IF EXISTS `command_alias`;

/*
--- Uses aliases for commands
*/

CREATE TABLE `command_alias` (
  `alias` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`alias`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*
--- Table audit is used to trace all admin commands in game
*/

CREATE TABLE IF NOT EXISTS `audit` (
  `id_audit` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_account` int(10) unsigned NOT NULL,
  `id_admin` int(10) unsigned NOT NULL,
  `id_player` int(10) unsigned NOT NULL DEFAULT '0',
  `type` enum('cmd') CHARACTER SET utf8 NOT NULL,
  `text` text CHARACTER SET utf8 NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_audit`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=8076 ;

/*
--- customrank contains PVE player rank
*/

CREATE TABLE IF NOT EXISTS `customRank` (
  `rank` int(11) NOT NULL,
  `pts` int(11) NOT NULL,
  `playerObjId` int(11) NOT NULL,
  PRIMARY KEY (`playerObjId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*
--- Table teleport used for Admin command TeleService
*/

CREATE TABLE IF NOT EXISTS `teleport` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `worldid` int(10) NOT NULL,
  `x` float NOT NULL,
  `y` float NOT NULL,
  `z` float NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Table structure for `account_locale`
--

CREATE TABLE IF NOT EXISTS `account_locale` (
  `account_id` int(11) NOT NULL,
  `locale` varchar(2) NOT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;