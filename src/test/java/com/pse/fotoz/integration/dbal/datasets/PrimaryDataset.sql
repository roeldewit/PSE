-- phpMyAdmin SQL Dump
-- version 4.3.11
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Oct 21, 2015 at 04:27 PM
-- Server version: 5.6.24
-- PHP Version: 5.6.8
USE fotoz;
SET FOREIGN_KEY_CHECKS=0;
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `fotoz`
--

--
-- Truncate table before insert `customers`
--

TRUNCATE TABLE `customers`;
--
-- Dumping data for table `customers`
--

INSERT INTO `customers` (`id`, `address`, `city`, `email`, `name`, `phone`) VALUES
(1, NULL, NULL, NULL, 'Jan Maas', NULL);

--
-- Truncate table before insert `customer_accounts`
--

TRUNCATE TABLE `customer_accounts`;
--
-- Dumping data for table `customer_accounts`
--

INSERT INTO `customer_accounts` (`id`, `login`, `passwordHash`, `user_id`) VALUES
(1, 'jan_maas', '1000:bf9932d804a83b7ab8ec5114148325454326a195e8929064:af32e38733dfafd21a9ce2c2402933e942af17f1dd6d8c02', 1);

--
-- Truncate table before insert `customer_permissions`
--

TRUNCATE TABLE `customer_permissions`;
--
-- Dumping data for table `customer_permissions`
--


--
-- Truncate table before insert `photographers`
--

TRUNCATE TABLE `photographers`;
--
-- Dumping data for table `photographers`
--

INSERT INTO `photographers` (`id`, `address`, `city`, `email`, `name`, `phone`) VALUES
(1, 'Hazelpad 23', 'Eindhoven', 'info@avonturier.nl', 'De Avonturier', '0401234567'),
(3, 'Schoenmaker 187', 'Eindhoven', 'info@kolibrie.nl', 'Kolibrie Natuurfoto''s', '0401234567'),
(5, 'De Vaar 18', 'Eindhoven', 'info@selectfotografie.nl', 'Select Fotografie', '0401234567'),
(7, 'Molenweg 4', 'Eindhoven', 'info@mooiekiekjes.nl', 'Mooie Kiekjes Eindhoven', '0401234567'),
(9, 'Steenstraat 58', 'Eindhoven', 'info@willemderijk.nl', 'Willem de Rijk Fotografie', '0401234567'),
(11, 'Geldropseweg 186', 'Eindhoven', 'info@hansgroen.nl', 'Hans Groen Fotografie', '0401234567');

--
-- Truncate table before insert `pictures`
--

TRUNCATE TABLE `pictures`;
--
-- Dumping data for table `pictures`
--




--
-- Truncate table before insert `picture_sessions`
--

TRUNCATE TABLE `picture_sessions`;
--
-- Dumping data for table `picture_sessions`
--


--
-- Truncate table before insert `producer_accounts`
--

TRUNCATE TABLE `producer_accounts`;
--
-- Dumping data for table `producer_accounts`
--

INSERT INTO `producer_accounts` (`id`, `login`, `password`, `role`) VALUES
(1, 'admin', '1000:69a414cea0f36acc562686eb54d696f25c34a4ea1a519fa9:8945f0cbb0cac0853767cfd498d290df6027d24612059ba5', 'ROLE_ADMIN');

--
-- Truncate table before insert `shops`
--

TRUNCATE TABLE `shops`;
--
-- Dumping data for table `shops`
--

INSERT INTO `shops` (`id`, `login`, `passwordHash`, `user_id`) VALUES
(2, 'de-avonturier', '1000:cbc3699dac8b9a73d6e7490c834c98a79f9c610fd9e7e0c0:1838333f3f46d8678c97134568ce08388b88850d247171d9', 1),
(4, 'de-kolibrie', '1000:bfde86f3afac1d8a0d7219da40d9627cdf5410906529cb15:92efa43069bf585efc413825476a6d1408409dfc2ba4d7ad', 3),
(6, 'select-fotografie', '1000:23038ed1f6cfddc6100123f095741591fdb15b24e24e670f:85b6754190982145b8c74beaac2281c55b3387beccd50b97', 5),
(8, 'mooie-kiekjes', '1000:c9277e12463311a1d08cf156406f4e413e2e0c9e09e95af6:b01b17b6659be60fffdd27a59023a2f8c5497636170da7a6', 7),
(10, 'willem-derijk', '1000:dbd10ae743009ae304bfd04596e18799ffc7fbe414eeaab4:cb17814333275c0d1a00bc65458a2361a43458316d854c8e', 9),
(12, 'hans-groen', '1000:ff7764fa7abeffe2acb3b17df0c5dd2637610006b6fca588:bcd9640d93aa81ff414de538f16ba5c0b9909190d5d89b05', 11);


--
-- Truncate table before insert `product_types`
--

TRUNCATE TABLE `product_types`;
--
-- Dumping data for table `product_types`
--




--
--set check foreign keys
--
SET FOREIGN_KEY_CHECKS=1;
