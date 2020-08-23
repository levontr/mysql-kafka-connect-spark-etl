DROP TABLE IF EXISTS `test`.`volume_type`;
CREATE TABLE `test`.`volume_type` (
  `id` int(11) NOT NULL,
  `description` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `test`.`volume_type` WRITE;
INSERT INTO `test`.`volume_type` VALUES (0,'Upstream'),(1,'Downstream');
UNLOCK TABLES;
