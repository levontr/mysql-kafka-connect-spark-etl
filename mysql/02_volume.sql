DROP TABLE IF EXISTS `test`.`volume`;
CREATE TABLE `test`.`volume` (
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `volume_type_id` int(11) NOT NULL,
  `volume` int(11) NOT NULL,
  KEY `volume_type_id` (`volume_type_id`),
  CONSTRAINT `volume_ibfk_1` FOREIGN KEY (`volume_type_id`) REFERENCES `volume_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `test`.`volume` WRITE;
INSERT INTO `test`.`volume` VALUES ('2020-02-29 02:00:00',0,1),('2020-03-29 02:30:00',0,2),('2020-04-29 03:00:00',1,3);
UNLOCK TABLES;
