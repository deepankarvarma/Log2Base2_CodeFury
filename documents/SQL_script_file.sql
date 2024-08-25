CREATE DATABASE  IF NOT EXISTS `e_commerce_app` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `e_commerce_app`;
-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: localhost    Database: e_commerce_app
-- ------------------------------------------------------
-- Server version	8.0.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `category_name` varchar(100) NOT NULL,
  `category_description` text,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `category_name` (`category_name`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'Organic Fruits','Fresh and organically grown fruits. Free from pesticides and chemicals.'),(2,'Vegetables','Locally sourced fresh vegetables, available in seasonal and organic varieties.'),(3,'Snacks','Healthy and traditional Indian snacks, including roasted nuts, seeds, and more.'),(4,'Ready-to-Eat Meals','Convenient, ready-to-eat meals inspired by Indian cuisine.'),(5,'Beverages','A selection of refreshing drinks, including juices, lassi, and traditional beverages.'),(7,'Dairy Products','Fresh dairy products including milk, curd, paneer, and ghee.'),(8,'Bakery & Breads','Freshly baked breads, buns, and other bakery products.');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `delivery_schedules`
--

DROP TABLE IF EXISTS `delivery_schedules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `delivery_schedules` (
  `delivery_schedule_id` int NOT NULL AUTO_INCREMENT,
  `subscription_id` int DEFAULT NULL,
  `delivery_date` date NOT NULL,
  PRIMARY KEY (`delivery_schedule_id`),
  KEY `subscription_id` (`subscription_id`),
  CONSTRAINT `delivery_schedules_ibfk_1` FOREIGN KEY (`subscription_id`) REFERENCES `subscriptions` (`subscription_id`)
) ENGINE=InnoDB AUTO_INCREMENT=148 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery_schedules`
--

LOCK TABLES `delivery_schedules` WRITE;
/*!40000 ALTER TABLE `delivery_schedules` DISABLE KEYS */;
INSERT INTO `delivery_schedules` VALUES (1,21,'2024-08-24'),(2,21,'2024-09-08'),(3,21,'2024-09-15'),(4,21,'2024-09-22'),(5,21,'2024-09-29'),(6,22,'2024-09-01'),(7,22,'2024-09-15'),(8,22,'2024-09-29'),(9,22,'2024-10-13'),(10,22,'2024-10-27'),(11,22,'2024-11-10'),(12,22,'2024-11-24'),(13,23,'2024-09-01'),(14,23,'2024-10-01'),(15,24,'2024-09-01'),(16,24,'2024-09-02'),(17,24,'2024-09-03'),(18,24,'2024-09-04'),(19,24,'2024-09-05'),(20,24,'2024-09-06'),(21,24,'2024-09-07'),(22,24,'2024-09-08'),(23,24,'2025-02-28'),(24,25,'2024-09-01'),(25,25,'2024-09-03'),(26,25,'2024-09-05'),(27,25,'2024-09-07'),(28,25,'2024-09-09'),(29,25,'2024-09-11'),(30,25,'2024-09-13'),(31,25,'2024-09-30'),(32,26,'2024-09-01'),(33,26,'2024-09-08'),(34,26,'2024-09-15'),(35,26,'2024-09-22'),(36,26,'2024-09-29'),(37,27,'2024-09-01'),(38,27,'2024-09-15'),(39,27,'2024-09-29'),(40,27,'2024-10-13'),(41,27,'2024-10-27'),(42,31,'2024-08-25'),(43,31,'2024-08-26'),(44,31,'2024-08-27'),(45,31,'2024-08-28'),(46,31,'2024-08-29'),(47,31,'2024-08-30'),(48,31,'2024-08-31'),(49,31,'2024-09-01'),(50,31,'2024-09-02'),(51,31,'2024-09-03'),(52,31,'2024-09-04'),(53,31,'2024-09-05'),(54,31,'2024-09-06'),(55,31,'2024-09-07'),(56,31,'2024-09-08'),(57,31,'2024-09-09'),(58,31,'2024-09-10'),(59,31,'2024-09-11'),(60,31,'2024-09-12'),(61,31,'2024-09-13'),(62,31,'2024-09-14'),(63,31,'2024-09-15'),(64,31,'2024-09-16'),(65,31,'2024-09-17'),(66,31,'2024-09-18'),(67,31,'2024-09-19'),(68,31,'2024-09-20'),(69,31,'2024-09-21'),(70,31,'2024-09-22'),(71,31,'2024-09-23'),(72,31,'2024-09-24'),(73,31,'2024-09-25'),(74,32,'2024-08-24'),(75,32,'2024-08-31'),(76,32,'2024-09-07'),(77,32,'2024-09-14'),(78,32,'2024-09-21'),(79,33,'2024-08-24'),(80,33,'2024-08-25'),(81,33,'2024-08-26'),(82,33,'2024-08-27'),(83,33,'2024-08-28'),(84,33,'2024-08-29'),(85,33,'2024-08-30'),(86,33,'2024-08-31'),(87,33,'2024-09-01'),(88,33,'2024-09-02'),(89,33,'2024-09-03'),(90,33,'2024-09-04'),(91,33,'2024-09-05'),(92,33,'2024-09-06'),(93,33,'2024-09-07'),(94,33,'2024-09-08'),(95,33,'2024-09-09'),(96,33,'2024-09-10'),(97,33,'2024-09-11'),(98,33,'2024-09-12'),(99,33,'2024-09-13'),(100,33,'2024-09-14'),(101,33,'2024-09-15'),(102,33,'2024-09-16'),(103,33,'2024-09-17'),(104,33,'2024-09-18'),(105,33,'2024-09-19'),(106,33,'2024-09-20'),(107,33,'2024-09-21'),(108,33,'2024-09-22'),(109,33,'2024-09-23'),(110,33,'2024-09-24'),(111,34,'2024-08-26'),(112,34,'2024-09-02'),(113,34,'2024-09-09'),(114,34,'2024-09-16'),(115,34,'2024-09-23'),(116,35,'2024-08-25'),(117,35,'2024-08-26'),(118,35,'2024-08-27'),(119,35,'2024-08-28'),(120,35,'2024-08-29'),(121,35,'2024-08-30'),(122,35,'2024-08-31'),(123,35,'2024-09-01'),(124,35,'2024-09-02'),(125,35,'2024-09-03'),(126,35,'2024-09-04'),(127,35,'2024-09-05'),(128,35,'2024-09-06'),(129,35,'2024-09-07'),(130,35,'2024-09-08'),(131,35,'2024-09-09'),(132,35,'2024-09-10'),(133,35,'2024-09-11'),(134,35,'2024-09-12'),(135,35,'2024-09-13'),(136,35,'2024-09-14'),(137,35,'2024-09-15'),(138,35,'2024-09-16'),(139,35,'2024-09-17'),(140,35,'2024-09-18'),(141,35,'2024-09-19'),(142,35,'2024-09-20'),(143,35,'2024-09-21'),(144,35,'2024-09-22'),(145,35,'2024-09-23'),(146,35,'2024-09-24'),(147,35,'2024-09-25');
/*!40000 ALTER TABLE `delivery_schedules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `order_item_id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL,
  `price` double NOT NULL,
  PRIMARY KEY (`order_item_id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,1,75,1,42.5),(2,2,86,1,54),(3,3,70,1,34),(4,4,81,1,300),(6,5,100,2,45),(7,5,101,1,150),(9,7,90,1,170),(10,8,74,1,127.5),(13,10,92,1,500),(14,10,95,2,30),(17,17,100,1,38.25),(18,18,91,2,120),(19,19,95,1,30),(20,19,96,2,100),(21,20,95,2,60),(22,21,81,2,600),(23,21,85,2,200),(24,22,98,1,55.2),(25,23,98,1,52.8),(26,24,65,2,300),(27,24,104,1,50),(28,25,68,2,200),(29,26,77,1,44);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `order_date` date NOT NULL,
  `total_price` double NOT NULL,
  `delivery_status` enum('PENDING','DELIVERED','CANCELLED') DEFAULT 'PENDING',
  PRIMARY KEY (`order_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,1,'2024-09-05',0,'PENDING'),(2,2,'2024-09-08',0,'PENDING'),(3,3,'2024-09-12',0,'PENDING'),(4,4,'2024-09-10',380,'PENDING'),(5,5,'2024-09-15',240,'PENDING'),(6,6,'2024-09-07',0,'PENDING'),(7,8,'2024-09-09',0,'PENDING'),(8,10,'2024-09-14',0,'PENDING'),(9,11,'2024-09-12',210,'PENDING'),(10,12,'2024-09-16',530,'PENDING'),(11,11,'2024-08-23',120,'DELIVERED'),(12,11,'2024-08-23',100,'DELIVERED'),(13,11,'2024-08-23',140,'DELIVERED'),(17,1,'2024-08-24',38.25,'DELIVERED'),(18,12,'2024-08-24',120,'DELIVERED'),(19,15,'2024-08-24',130,'DELIVERED'),(20,12,'2024-08-24',60,'DELIVERED'),(21,12,'2024-08-24',800,'DELIVERED'),(22,12,'2024-08-24',55.2,'DELIVERED'),(23,15,'2024-08-25',52.8,'DELIVERED'),(24,17,'2024-08-25',350,'DELIVERED'),(25,17,'2024-08-25',200,'DELIVERED'),(26,16,'2024-08-25',44,'PENDING');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `product_id` int NOT NULL AUTO_INCREMENT,
  `product_name` varchar(255) NOT NULL,
  `description` text,
  `base_price` double NOT NULL,
  `stock` int NOT NULL,
  `category_id` int DEFAULT NULL,
  PRIMARY KEY (`product_id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (65,'Paneer Tikka Wrap','A delicious wrap stuffed with marinated paneer tikka and veggies.',150,58,4),(66,'Masala Dosa','A crispy dosa filled with spicy potato masala, ready to eat.',120,50,4),(67,'Rajma Chawal','Comfort food consisting of spiced kidney beans served with rice.',140,70,4),(68,'Aloo Paratha','Stuffed whole wheat flatbread with spiced mashed potatoes.',100,78,4),(69,'Chole Bhature','Spicy chickpea curry served with fried bread.',160,40,4),(70,'Palak Paneer','A creamy spinach curry with chunks of cottage cheese.',180,65,4),(71,'Veg Biryani','Aromatic rice dish cooked with vegetables and Indian spices.',200,50,4),(72,'Vada Pav','A popular street food, a spicy potato fritter in a bread bun.',50,100,4),(73,'Idli Sambar','Steamed rice cakes served with lentil soup and chutney.',90,75,4),(74,'Dal Makhani','Rich and creamy black lentil curry, ready to serve with rice or naan.',150,55,4),(75,'Alphonso Mangoes','Premium quality Alphonso mangoes, sourced directly from Ratnagiri.',500,100,1),(76,'Himachal Apples','Crisp and juicy apples from Himachal Pradesh, organically grown.',200,150,1),(77,'Bananas','Naturally ripened bananas, rich in potassium and fiber.',50,200,1),(78,'Fresh Spinach','Freshly harvested organic spinach, packed with nutrients.',40,100,2),(79,'Tomatoes','Juicy, vine-ripened tomatoes, perfect for cooking and salads.',30,180,2),(80,'Potatoes','Locally grown potatoes, ideal for a variety of Indian dishes.',25,250,2),(81,'Roasted Almonds','Crunchy roasted almonds, lightly salted for a healthy snack.',300,80,3),(82,'Masala Peanuts','Spicy and flavorful masala peanuts, a popular Indian snack.',120,150,3),(84,'Aam Panna','Refreshing aam panna made from raw mangoes, perfect for summer.',150,50,5),(85,'Masala Chai','Aromatic Indian masala chai blend, ready to brew.',100,100,5),(86,'Fresh Orange Juice','Freshly squeezed orange juice, rich in Vitamin C.',120,75,5),(90,'Fresh Paneer','Soft and fresh paneer, ideal for Indian dishes like paneer tikka.',200,80,7),(91,'Organic Milk','Pure organic milk, rich in calcium and essential nutrients.',60,100,7),(92,'Ghee','Premium quality cow ghee, made from pure milk.',500,50,7),(93,'Multigrain Bread','Healthy multigrain bread, baked fresh daily.',50,100,8),(94,'Garlic Naan','Soft garlic naan, perfect with curries.',40,80,8),(95,'Whole Wheat Bun','Soft and healthy whole wheat buns, perfect for burgers.',30,100,8),(96,'Chips','Crispy potato chips with a salty flavor.',50,200,3),(97,'Pretzels','Baked bread with a crisp outer layer.',40,150,3),(98,'Orange Juice','Freshly squeezed orange juice.',60,99,5),(99,'Green Tea','Organic green tea for a healthy lifestyle.',120,80,5),(100,'Milk','Fresh cow milk, rich in nutrients.',45,300,7),(101,'Cheese','Creamy and rich cheese, perfect for sandwiches.',150,200,7),(102,'Whole Wheat Bread','Healthy bread made from whole wheat.',35,250,8),(103,'Bagels','Soft and chewy bagels with a golden crust.',25,200,8),(104,'Mango Juice','Amazing fresh organic mango juice.',50,49,5);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscription_plans`
--

DROP TABLE IF EXISTS `subscription_plans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscription_plans` (
  `subscription_plan_id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `subscription_type` enum('DAILY','ALTERNATE_DAY','WEEKLY','BI_WEEKLY','MONTHLY') NOT NULL,
  `interval_days` int DEFAULT NULL,
  `discount_rate` double DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`subscription_plan_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `subscription_plans_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=201 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscription_plans`
--

LOCK TABLES `subscription_plans` WRITE;
/*!40000 ALTER TABLE `subscription_plans` DISABLE KEYS */;
INSERT INTO `subscription_plans` VALUES (1,96,'DAILY',1,0.15,1),(2,96,'ALTERNATE_DAY',2,0.13,1),(3,96,'WEEKLY',7,0.1,1),(4,96,'BI_WEEKLY',14,0.07,1),(5,96,'MONTHLY',30,0.03,1),(6,97,'DAILY',1,0.15,1),(7,97,'ALTERNATE_DAY',2,0.13,1),(8,97,'WEEKLY',7,0.1,1),(9,97,'BI_WEEKLY',14,0.07,1),(10,97,'MONTHLY',30,0.03,1),(11,98,'DAILY',1,0.12,1),(12,98,'ALTERNATE_DAY',2,0.1,1),(13,98,'WEEKLY',7,0.08,1),(14,98,'BI_WEEKLY',14,0.05,1),(15,98,'MONTHLY',30,0.02,1),(16,99,'DAILY',1,0.12,1),(17,99,'ALTERNATE_DAY',2,0.1,1),(18,99,'WEEKLY',7,0.08,1),(19,99,'BI_WEEKLY',14,0.05,1),(20,99,'MONTHLY',30,0.02,1),(21,100,'DAILY',1,0.15,1),(22,100,'ALTERNATE_DAY',2,0.13,1),(23,100,'WEEKLY',7,0.1,1),(24,100,'BI_WEEKLY',14,0.07,1),(25,100,'MONTHLY',30,0.03,1),(26,101,'DAILY',1,0.15,1),(27,101,'ALTERNATE_DAY',2,0.13,1),(28,101,'WEEKLY',7,0.1,1),(29,101,'BI_WEEKLY',14,0.07,1),(30,101,'MONTHLY',30,0.03,1),(31,102,'DAILY',1,0.12,1),(32,102,'ALTERNATE_DAY',2,0.1,1),(33,102,'WEEKLY',7,0.08,1),(34,102,'BI_WEEKLY',14,0.05,1),(35,102,'MONTHLY',30,0.02,1),(36,103,'DAILY',1,0.12,1),(37,103,'ALTERNATE_DAY',2,0.1,1),(38,103,'WEEKLY',7,0.08,1),(39,103,'BI_WEEKLY',14,0.05,1),(40,103,'MONTHLY',30,0.02,1),(161,65,'DAILY',1,0.15,1),(162,65,'ALTERNATE_DAY',2,0.12,1),(163,65,'WEEKLY',7,0.1,1),(164,65,'BI_WEEKLY',14,0.07,1),(165,65,'MONTHLY',30,0.03,1),(166,69,'DAILY',1,0.15,1),(167,69,'ALTERNATE_DAY',2,0.12,1),(168,69,'WEEKLY',7,0.1,1),(169,69,'BI_WEEKLY',14,0.07,1),(170,69,'MONTHLY',30,0.03,1),(171,66,'DAILY',1,0.15,1),(172,66,'ALTERNATE_DAY',2,0.12,1),(173,66,'WEEKLY',7,0.1,1),(174,66,'BI_WEEKLY',14,0.07,1),(175,66,'MONTHLY',30,0.03,1),(176,67,'DAILY',1,0.15,1),(177,67,'ALTERNATE_DAY',2,0.12,1),(178,67,'WEEKLY',7,0.1,1),(179,67,'BI_WEEKLY',14,0.07,1),(180,67,'MONTHLY',30,0.03,1),(181,77,'DAILY',1,0.12,1),(182,77,'ALTERNATE_DAY',2,0.1,1),(183,77,'WEEKLY',7,0.08,1),(184,77,'BI_WEEKLY',14,0.05,1),(185,77,'MONTHLY',30,0.02,1),(186,80,'DAILY',1,0.12,1),(187,80,'ALTERNATE_DAY',2,0.1,1),(188,80,'WEEKLY',7,0.08,1),(189,80,'BI_WEEKLY',14,0.05,1),(190,80,'MONTHLY',30,0.02,1),(191,79,'DAILY',1,0.12,1),(192,79,'ALTERNATE_DAY',2,0.1,1),(193,79,'WEEKLY',7,0.08,1),(194,79,'BI_WEEKLY',14,0.05,1),(195,79,'MONTHLY',30,0.02,1),(196,78,'DAILY',1,0.15,1),(197,78,'ALTERNATE_DAY',2,0.13,1),(198,78,'WEEKLY',7,0.1,1),(199,78,'BI_WEEKLY',14,0.07,1),(200,78,'MONTHLY',30,0.03,0);
/*!40000 ALTER TABLE `subscription_plans` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscriptions`
--

DROP TABLE IF EXISTS `subscriptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscriptions` (
  `subscription_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `subscription_plan_id` int NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `status` enum('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
  PRIMARY KEY (`subscription_id`),
  KEY `user_id` (`user_id`),
  KEY `subscription_plan_id` (`subscription_plan_id`),
  CONSTRAINT `subscriptions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `subscriptions_ibfk_2` FOREIGN KEY (`subscription_plan_id`) REFERENCES `subscription_plans` (`subscription_plan_id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscriptions`
--

LOCK TABLES `subscriptions` WRITE;
/*!40000 ALTER TABLE `subscriptions` DISABLE KEYS */;
INSERT INTO `subscriptions` VALUES (21,1,21,'2024-09-01','2024-09-30','INACTIVE'),(22,2,28,'2024-09-01','2024-11-30','ACTIVE'),(23,3,14,'2024-09-01','2024-10-31','ACTIVE'),(24,4,10,'2024-09-01','2025-02-28','ACTIVE'),(25,5,32,'2024-09-01','2024-09-30','ACTIVE'),(26,6,161,'2024-09-01','2024-09-30','ACTIVE'),(27,8,19,'2024-09-01','2024-10-31','ACTIVE'),(28,3,1,'2024-08-25','2024-09-25','INACTIVE'),(31,15,11,'2024-08-25','2024-09-25','ACTIVE'),(32,12,13,'2024-08-24','2024-09-25','INACTIVE'),(33,12,11,'2024-08-24','2024-09-24','ACTIVE'),(34,17,168,'2024-08-26','2024-09-26','ACTIVE'),(35,16,181,'2024-08-25','2024-09-25','ACTIVE');
/*!40000 ALTER TABLE `subscriptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `user_name` varchar(100) NOT NULL,
  `user_pswd` varchar(100) NOT NULL,
  `user_email` varchar(100) NOT NULL,
  `user_phone_number` varchar(15) DEFAULT NULL,
  `user_address` text,
  `user_registration_date` date NOT NULL,
  `role` varchar(10) NOT NULL DEFAULT 'USER',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_name` (`user_name`),
  UNIQUE KEY `user_email` (`user_email`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'John Doe','password123','john.doe@example.com','1234567890','123 Main St','2024-08-01','USER'),(2,'Jane Smith','password456','jane.smith@example.com','0987654321','456 Maple St','2024-08-05','USER'),(3,'Michael Johnson','password789','michael.johnson@example.com','5551234567','789 Oak Ave','2024-08-10','USER'),(4,'Emily Davis','passwordabc','emily.davis@example.com','5550987654','321 Pine Rd','2024-08-15','USER'),(5,'David Wilson','passworddef','david.wilson@example.com','5554443333','159 Elm St','2024-08-20','USER'),(6,'Sarah Anderson','passwordghi','sarah.anderson@example.com','5552221111','753 Maple Ln','2024-08-25','USER'),(7,'Robert Thompson','passwordjkl','robert.thompson@example.com','5559998888','951 Oak Dr','2024-09-01','USER'),(8,'Jessica Martinez','passwordmno','jessica.martinez@example.com','5557776666','753 Pine Ave','2024-09-05','USER'),(9,'Christopher Hernandez','passwordpqr','christopher.hernandez@example.com','5554445555','951 Elm Rd','2024-09-10','USER'),(10,'Olivia Gonzalez','passwordstu','olivia.gonzalez@example.com','5552223333','159 Oak St','2024-09-15','USER'),(11,'Daniel Ramirez','passwordvwx','daniel.ramirez@example.com','5557778888','753 Maple Dr','2024-09-20','USER'),(12,'Isabella Diaz','passwordyz1','isabella.diaz@example.com','5559990000','951 Pine Ln','2024-09-25','USER'),(14,'Admin','admin123','admin@foodsub.com','1234567890','Admin HQ','2024-08-23','ADMIN'),(15,'Syed Ali Mujtaba','15246','ali.ali.mujtaba@hsbc.co.in','1234567890','Aminabad, Lucknow','2024-08-23','USER'),(16,'Deepankar','15246','deepankar123@gmail.com','1234567890','133/A Pune','2024-08-24','USER'),(17,'Mohammad Zohair','15246','zohair123@gmail.com','9876543210','133/A Gurgaon, Haryana','2024-08-25','USER');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-08-25 11:31:13
