-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th10 05, 2024 lúc 09:05 AM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `trashsortinggame`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `account`
--

CREATE TABLE `account` (
  `accountID` varchar(36) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `account`
--

INSERT INTO `account` (`accountID`, `username`, `password`, `created_at`, `updated_at`) VALUES
('8f7011aa-82e7-11ef-8d3b-088fc333a476', 'user1', 'password123', '2024-10-05 14:00:52', '2024-10-05 14:00:52'),
('8f7038a0-82e7-11ef-8d3b-088fc333a476', 'user2', 'password456', '2024-10-05 14:00:52', '2024-10-05 14:00:52');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `game`
--

CREATE TABLE `game` (
  `gameID` varchar(36) NOT NULL,
  `status` enum('pending','active','finished') NOT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `total_score` int(11) DEFAULT 0,
  `list_trash_bin` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL CHECK (json_valid(`list_trash_bin`)),
  `list_trash_item` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL CHECK (json_valid(`list_trash_item`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `player`
--

CREATE TABLE `player` (
  `playerID` varchar(36) NOT NULL,
  `accountID` varchar(36) DEFAULT NULL,
  `total_games` int(11) DEFAULT 0,
  `total_wins` int(11) DEFAULT 0,
  `total_score` int(11) DEFAULT 0,
  `average_score` int(11) DEFAULT 0,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `player`
--

INSERT INTO `player` (`playerID`, `accountID`, `total_games`, `total_wins`, `total_score`, `average_score`, `created_at`, `updated_at`) VALUES
('0f4ac856-82e8-11ef-8d3b-088fc333a476', '8f7011aa-82e7-11ef-8d3b-088fc333a476', 0, 0, 0, 0, '2024-10-05 14:04:27', '2024-10-05 14:04:27');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `player_game`
--

CREATE TABLE `player_game` (
  `playerID` varchar(36) NOT NULL,
  `gameID` varchar(36) NOT NULL,
  `join_time` datetime NOT NULL,
  `leave_time` datetime DEFAULT NULL,
  `play_duration` int(11) DEFAULT NULL,
  `score` int(11) DEFAULT 0,
  `result` enum('win','lose','draw') NOT NULL,
  `is_final` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `trashbin`
--

CREATE TABLE `trashbin` (
  `binID` varchar(36) NOT NULL,
  `name` varchar(50) NOT NULL,
  `type` enum('organic','plastic','metal','paper') NOT NULL,
  `img_url` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `trashbin`
--

INSERT INTO `trashbin` (`binID`, `name`, `type`, `img_url`) VALUES
('1daf45fb-82e7-11ef-8d3b-088fc333a476', 'Organic Waste Bin', '', 'https://example.com/img/organic_bin.png'),
('1daf7f6b-82e7-11ef-8d3b-088fc333a476', 'Plastic Recycling Bin', '', 'https://example.com/img/plastic_bin.png'),
('1daf80b1-82e7-11ef-8d3b-088fc333a476', 'Metal Recycling Bin', '', 'https://example.com/img/metal_bin.png'),
('1daf811b-82e7-11ef-8d3b-088fc333a476', 'Paper Recycling Bin', '', 'https://example.com/img/paper_bin.png');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `trashitem`
--

CREATE TABLE `trashitem` (
  `itemID` varchar(36) NOT NULL,
  `name` varchar(50) NOT NULL,
  `type` enum('organic','plastic','metal','paper') NOT NULL,
  `img_url` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `trashitem`
--

INSERT INTO `trashitem` (`itemID`, `name`, `type`, `img_url`) VALUES
('ea0441f1-82e6-11ef-8d3b-088fc333a476', 'Apple Core', 'organic', 'https://example.com/img/apple_core.png'),
('ea048377-82e6-11ef-8d3b-088fc333a476', 'Plastic Bottle', 'plastic', 'https://example.com/img/plastic_bottle.png'),
('ea049003-82e6-11ef-8d3b-088fc333a476', 'Aluminum Can', 'metal', 'https://example.com/img/aluminum_can.png'),
('ea0490eb-82e6-11ef-8d3b-088fc333a476', 'Newspaper', 'paper', 'https://example.com/img/newspaper.png'),
('ea04911a-82e6-11ef-8d3b-088fc333a476', 'Banana Peel', 'organic', 'https://example.com/img/banana_peel.png');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `account`
--
ALTER TABLE `account`
  ADD PRIMARY KEY (`accountID`);

--
-- Chỉ mục cho bảng `game`
--
ALTER TABLE `game`
  ADD PRIMARY KEY (`gameID`);

--
-- Chỉ mục cho bảng `player`
--
ALTER TABLE `player`
  ADD PRIMARY KEY (`playerID`),
  ADD KEY `accountID` (`accountID`);

--
-- Chỉ mục cho bảng `player_game`
--
ALTER TABLE `player_game`
  ADD PRIMARY KEY (`playerID`,`gameID`),
  ADD KEY `gameID` (`gameID`);

--
-- Chỉ mục cho bảng `trashbin`
--
ALTER TABLE `trashbin`
  ADD PRIMARY KEY (`binID`);

--
-- Chỉ mục cho bảng `trashitem`
--
ALTER TABLE `trashitem`
  ADD PRIMARY KEY (`itemID`);

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `player`
--
ALTER TABLE `player`
  ADD CONSTRAINT `player_ibfk_1` FOREIGN KEY (`accountID`) REFERENCES `account` (`accountID`);

--
-- Các ràng buộc cho bảng `player_game`
--
ALTER TABLE `player_game`
  ADD CONSTRAINT `player_game_ibfk_1` FOREIGN KEY (`playerID`) REFERENCES `player` (`playerID`),
  ADD CONSTRAINT `player_game_ibfk_2` FOREIGN KEY (`gameID`) REFERENCES `game` (`gameID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
