-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: bankdb
-- ------------------------------------------------------
-- Server version	8.0.39

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
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `account_id` int NOT NULL AUTO_INCREMENT,
  `account_type` enum('savings','checkings','passbook') NOT NULL,
  `current_balance` decimal(16,2) NOT NULL,
  `minimum_balance` decimal(16,2) NOT NULL,
  `date_opened` date NOT NULL,
  `interest_rate` decimal(16,2) NOT NULL,
  `account_status` enum('active','closed') NOT NULL,
  `customer_id` int NOT NULL,
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `account_id_UNIQUE` (`account_id`),
  KEY `customer_id_idx` (`customer_id`),
  CONSTRAINT `customer_id` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (1,'savings',11979.01,3000.00,'2024-11-13',0.50,'active',1),(2,'checkings',15021.00,5000.00,'2024-11-19',0.20,'active',1);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `availed_loans`
--

DROP TABLE IF EXISTS `availed_loans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `availed_loans` (
  `loan_id` int NOT NULL AUTO_INCREMENT,
  `loan_option_id` int NOT NULL,
  `principal_amount` decimal(16,2) NOT NULL,
  `first_month_principal_amortization` decimal(16,2) NOT NULL,
  `succeeding_principal_amortization` decimal(16,2) NOT NULL,
  `interest_amortization` decimal(16,2) NOT NULL,
  `principal_balance` decimal(16,2) NOT NULL,
  `interest_balance` decimal(16,2) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `month_payment_day` int NOT NULL,
  `loan_status` enum('active','paid') NOT NULL,
  `customer_id` int NOT NULL,
  PRIMARY KEY (`loan_id`),
  UNIQUE KEY `loan_id_UNIQUE` (`loan_id`),
  KEY `loan_option_id_idx` (`loan_option_id`),
  KEY `customer_id_idx` (`customer_id`),
  KEY `fk_customer_id_idx` (`customer_id`),
  CONSTRAINT `fk_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`),
  CONSTRAINT `loan_option_id` FOREIGN KEY (`loan_option_id`) REFERENCES `loan_options` (`loan_option_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `availed_loans`
--

LOCK TABLES `availed_loans` WRITE;
/*!40000 ALTER TABLE `availed_loans` DISABLE KEYS */;
/*!40000 ALTER TABLE `availed_loans` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `customer_id` int NOT NULL AUTO_INCREMENT,
  `customer_first_name` varchar(45) NOT NULL,
  `customer_last_name` varchar(45) NOT NULL,
  `birth_date` date NOT NULL,
  `phone_number` varchar(15) NOT NULL,
  `email_address` varchar(45) NOT NULL,
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `customer_id_UNIQUE` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1,'Juan','Tamad','2000-12-11','09123456789','juan@lol.com'),(2,'Ken','Lo','2004-01-12','09','@a'),(3,'Juan','Tamad','2004-12-01','1','1');
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loan_options`
--

DROP TABLE IF EXISTS `loan_options`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `loan_options` (
  `loan_option_id` int NOT NULL AUTO_INCREMENT,
  `loan_option_type` varchar(30) NOT NULL,
  `interest_rate` decimal(16,2) NOT NULL,
  `loan_duration_month` int NOT NULL,
  `max_loan_amt` decimal(16,2) NOT NULL,
  `min_loan_amt` decimal(16,2) NOT NULL,
  PRIMARY KEY (`loan_option_id`),
  UNIQUE KEY `idloan_options_UNIQUE` (`loan_option_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `loan_options`
--

LOCK TABLES `loan_options` WRITE;
/*!40000 ALTER TABLE `loan_options` DISABLE KEYS */;
INSERT INTO `loan_options` VALUES (1,'Personal Loan',0.07,24,1000000.00,20000.00),(2,'Auto Loan',0.10,36,3000000.00,250000.00),(3,'Emergency Loan',0.05,24,750000.00,20000.00);
/*!40000 ALTER TABLE `loan_options` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction_history`
--

DROP TABLE IF EXISTS `transaction_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction_history` (
  `transaction_id` int NOT NULL AUTO_INCREMENT,
  `transaction_type` enum('deposit','withdrawal','loan_payment','transfer') NOT NULL,
  `amount` decimal(16,2) NOT NULL,
  `transaction_date` datetime NOT NULL,
  `transaction_status` enum('success','processing','failed') NOT NULL,
  `account_id` int DEFAULT NULL,
  `loan_id` int DEFAULT NULL,
  PRIMARY KEY (`transaction_id`),
  UNIQUE KEY `transaction_id_UNIQUE` (`transaction_id`),
  KEY `account_id_idx` (`account_id`),
  KEY `loan_id_idx` (`loan_id`),
  CONSTRAINT `account_id` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`),
  CONSTRAINT `loan_id` FOREIGN KEY (`loan_id`) REFERENCES `availed_loans` (`loan_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_history`
--

LOCK TABLES `transaction_history` WRITE;
/*!40000 ALTER TABLE `transaction_history` DISABLE KEYS */;
INSERT INTO `transaction_history` VALUES (1,'deposit',10.00,'2024-11-19 00:00:00','success',1,NULL),(2,'deposit',10.00,'2024-11-19 20:49:55','success',1,NULL),(3,'withdrawal',10.00,'2024-11-19 20:53:20','success',1,NULL),(6,'deposit',1.00,'2024-11-19 20:55:26','success',2,NULL),(7,'transfer',1.00,'2024-11-19 20:55:26','success',1,NULL),(8,'deposit',0.01,'2024-11-19 20:56:25','success',1,NULL);
/*!40000 ALTER TABLE `transaction_history` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-11-20 17:22:41
