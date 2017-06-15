CREATE TABLE IF NOT EXISTS `customRank` (
  `rank` int(11) NOT NULL,
  `pts` int(11) NOT NULL,
  `playerObjId` int(11) NOT NULL,
  PRIMARY KEY (`playerObjId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
