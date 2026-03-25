-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Хост: 127.0.0.1
-- Время создания: Мар 07 2026 г., 23:03
-- Версия сервера: 10.4.32-MariaDB
-- Версия PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- База данных: `shop`
--

DELIMITER $$
--
-- Процедуры
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `UpdateInventory` (IN `p_product_id` BIGINT, IN `p_warehouse_id` BIGINT, IN `p_quantity_change` INT, IN `p_min_stock` INT, IN `p_max_stock` INT)   BEGIN
    DECLARE v_current_quantity INT;
    DECLARE v_new_quantity INT;
    DECLARE v_current_min INT;
    DECLARE v_current_max INT;
    
    START TRANSACTION;
    
    -- Получаем текущие значения
    SELECT quantity, min_stock_level, max_stock_level 
    INTO v_current_quantity, v_current_min, v_current_max
    FROM Inventory
    WHERE product_id = p_product_id AND warehouse_id = p_warehouse_id
    FOR UPDATE;
    
    IF v_current_quantity IS NOT NULL THEN
        -- Запись существует - обновляем
        SET v_new_quantity = v_current_quantity + p_quantity_change;
        
        -- Проверяем, что количество не стало отрицательным
        IF v_new_quantity < 0 THEN
            ROLLBACK;
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Ошибка: недостаточно товара на складе';
        ELSE
            -- Обновляем запись, используя COALESCE с переменными
            UPDATE Inventory 
            SET quantity = v_new_quantity,
                min_stock_level = COALESCE(p_min_stock, v_current_min),
                max_stock_level = COALESCE(p_max_stock, v_current_max),
                last_updated = NOW()
            WHERE product_id = p_product_id 
            AND warehouse_id = p_warehouse_id;
            
            -- Проверяем на низкий запас
            IF v_new_quantity < COALESCE(p_min_stock, v_current_min) THEN
                -- Проверяем существует ли таблица Stock_alerts перед вставкой
                INSERT INTO Stock_alerts (product_id, warehouse_id, current_quantity, alert_type, created_at)
                VALUES (p_product_id, p_warehouse_id, v_new_quantity, 'LOW_STOCK', NOW());
            END IF;
            
            COMMIT;
        END IF;
    ELSE
        -- Запись не существует - создаем новую
        IF p_quantity_change < 0 THEN
            ROLLBACK;
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Ошибка: нельзя уменьшить количество несуществующего товара';
        ELSE
            INSERT INTO Inventory (
                product_id, 
                warehouse_id, 
                quantity, 
                min_stock_level, 
                max_stock_level, 
                last_updated
            ) VALUES (
                p_product_id,
                p_warehouse_id,
                p_quantity_change,
                COALESCE(p_min_stock, 0),
                COALESCE(p_max_stock, 999999),
                NOW()
            );
            COMMIT;
        END IF;
    END IF;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Структура таблицы `batch`
--

CREATE TABLE `batch` (
  `batch_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `batch_number` varchar(100) NOT NULL,
  `expiry_date` date DEFAULT NULL,
  `production_date` date DEFAULT NULL,
  `cost_price` decimal(10,2) DEFAULT NULL,
  `supplier_batch_id` bigint(20) DEFAULT NULL,
  `quantity` int(11) DEFAULT 0,
  `warehouse_id` bigint(20) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Дамп данных таблицы `batch`
--

INSERT INTO `batch` (`batch_id`, `product_id`, `batch_number`, `expiry_date`, `production_date`, `cost_price`, `supplier_batch_id`, `quantity`, `warehouse_id`, `created_at`) VALUES
(1, 1, 'MLK-240115-001', '2024-01-25', '2024-01-15', 65.00, NULL, 500, 1, '2025-11-09 14:00:24'),
(2, 2, 'KEF-240115-001', '2024-01-23', '2024-01-15', 55.00, NULL, 400, 1, '2025-11-09 14:00:24'),
(3, 3, 'RYA-240115-001', '2024-01-24', '2024-01-15', 60.00, NULL, 300, 1, '2025-11-09 14:00:24'),
(4, 7, 'BEE-240116-001', '2024-01-21', '2024-01-16', 550.00, NULL, 200, 1, '2025-11-09 14:00:24'),
(5, 10, 'CHI-240116-001', '2024-01-19', '2024-01-16', 180.00, NULL, 500, 1, '2025-11-09 14:00:24'),
(6, 25, 'BLO-240117-001', '2024-01-19', '2024-01-17', 35.00, NULL, 800, 1, '2025-11-09 14:00:24'),
(7, 26, 'BAT-240117-001', '2024-01-18', '2024-01-17', 25.00, NULL, 1000, 1, '2025-11-09 14:00:24'),
(8, 1, 'BATCH-2024-001', '2025-12-31', '2024-01-15', 75.50, 1, 50, 1, '2026-02-17 12:07:10'),
(10, 1, 'BATCH-2024-002', '2025-12-31', '2024-01-15', 75.50, 2, 50, 1, '2026-02-17 12:08:41'),
(11, 1, 'BATCH-2024-003', '2025-12-31', '2024-01-15', 75.50, 3, 50, 1, '2026-02-17 12:10:35'),
(15, 1, '12345', '2026-02-25', '2026-02-24', 123.00, 1, 0, 1, '2026-02-24 18:28:07'),
(16, 1, 'PO-2026-0001', '2026-03-01', '2026-02-28', 100.00, 1, 24, 1, '2026-02-28 16:38:26'),
(17, 1, 'PO-2026-003', '2027-03-05', '2026-03-03', 32.00, 1, 10, 1, '2026-03-03 08:26:05'),
(18, 1, 'PO-2026-004', '2027-03-05', '2026-03-03', 32.00, 1, 10, 1, '2026-03-03 08:29:53'),
(19, 2, 'PO-2026-004', '2027-03-07', '2026-03-03', 30.00, 1, 10, 1, '2026-03-03 08:29:53'),
(20, 7, 'PO-2026-005', '2026-03-07', '2026-03-03', 100.00, 2, 3, 2, '2026-03-03 09:29:19'),
(21, 8, 'PO-2026-005', '2026-03-08', '2026-03-03', 100.00, 2, 7, 2, '2026-03-03 09:29:19'),
(22, 18, 'PO-2025-006', '2027-03-06', '2026-03-05', 100.00, 3, 1, 2, '2026-03-05 08:43:02'),
(23, 1, 'PO-2025-007', '2026-03-06', '2026-03-05', 37.00, 1, 10, 1, '2026-03-05 12:33:00'),
(24, 10, 'PO-2025-008', '2027-03-05', '2026-03-05', 100.00, 2, 10, 1, '2026-03-05 12:34:50'),
(25, 16, 'PO-2025-009', '2026-03-05', '2026-03-05', 100.00, 3, 10, 1, '2026-03-05 12:37:48'),
(26, 2, 'PO-2026-007', '2026-03-09', '2026-03-07', 100.00, 1, 1, 1, '2026-03-07 21:28:38');

--
-- Триггеры `batch`
--
DELIMITER $$
CREATE TRIGGER `update_inventory_after_batch_insert` AFTER INSERT ON `batch` FOR EACH ROW BEGIN
    -- Проверяем, существует ли запись в inventory для данного product_id и warehouse_id
    IF EXISTS (
        SELECT 1 FROM Inventory 
        WHERE product_id = NEW.product_id 
        AND warehouse_id = NEW.warehouse_id
    ) THEN
        -- Обновляем существующую запись
        UPDATE Inventory 
        SET quantity = quantity + NEW.quantity,  -- Увеличиваем количество на 1 (одна партия)
            last_updated = NOW()
        WHERE product_id = NEW.product_id 
        AND warehouse_id = NEW.warehouse_id;
    ELSE
        -- Создаем новую запись в inventory
        INSERT INTO Inventory (
            product_id, 
            warehouse_id, 
            quantity, 
            min_stock_level, 
            max_stock_level, 
            last_updated
        ) VALUES (
            NEW.product_id,
            NEW.warehouse_id,
            1,  -- Начальное количество (одна партия)
            0,  -- Минимальный уровень по умолчанию
            999999,  -- Максимальный уровень (очень большое число, т.к. не нужно учитывать)
            NOW()
        );
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Структура таблицы `category`
--

CREATE TABLE `category` (
  `category_id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `parent_category_id` bigint(20) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Дамп данных таблицы `category`
--

INSERT INTO `category` (`category_id`, `name`, `description`, `parent_category_id`, `created_at`) VALUES
(1, 'Молочные продукты', 'Молоко, йогурты, сыры, творог', NULL, '2025-11-09 13:59:40'),
(2, 'Мясо и птица', 'Свежее мясо, птица, полуфабрикаты', NULL, '2025-11-09 13:59:40'),
(3, 'Овощи и фрукты', 'Свежие овощи и фрукты', NULL, '2025-11-09 13:59:40'),
(4, 'Бакалея', 'Крупы, макароны, мука, специи', NULL, '2025-11-09 13:59:40'),
(5, 'Напитки', 'Соки, воды, газированные напитки', NULL, '2025-11-09 13:59:40'),
(6, 'Хлеб и выпечка', 'Хлеб, булочки, кондитерские изделия', NULL, '2025-11-09 13:59:40'),
(7, 'Молочные напитки', 'Молоко, кефир, ряженка', 1, '2025-11-09 13:59:40'),
(8, 'Сыры', 'Твердые, мягкие, плавленые сыры', 1, '2025-11-09 13:59:40'),
(9, 'Свежее мясо', 'Говядина, свинина, баранина', 2, '2025-11-09 13:59:40'),
(10, 'Птица', 'Курица, индейка, утка', 2, '2025-11-09 13:59:40'),
(11, 'Овощи', 'Картофель, морковь, лук, помидоры', 3, '2025-11-09 13:59:40'),
(12, 'Фрукты', 'Яблоки, бананы, апельсины', 3, '2025-11-09 13:59:40'),
(13, 'Хлеб', 'Белый, черный, зерновой хлеб', 6, '2025-11-09 13:59:40');

-- --------------------------------------------------------

--
-- Структура таблицы `forecast`
--

CREATE TABLE `forecast` (
  `forecast_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `period` date NOT NULL,
  `forecast_quantity` int(11) NOT NULL,
  `calculation_method` varchar(50) DEFAULT 'moving_average',
  `confidence_level` decimal(5,4) DEFAULT NULL CHECK (`confidence_level` >= 0 and `confidence_level` <= 1),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `actual_quantity` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Дамп данных таблицы `forecast`
--

INSERT INTO `forecast` (`forecast_id`, `product_id`, `period`, `forecast_quantity`, `calculation_method`, `confidence_level`, `created_at`, `actual_quantity`) VALUES
(1, 1, '2024-02-01', 1200, 'seasonal', 0.8800, '2025-11-09 14:00:24', NULL),
(2, 2, '2024-02-01', 950, 'seasonal', 0.8500, '2025-11-09 14:00:24', NULL),
(3, 7, '2024-02-01', 180, 'moving_average', 0.8200, '2025-11-09 14:00:24', NULL),
(4, 13, '2024-02-01', 1500, 'seasonal', 0.9000, '2025-11-09 14:00:24', NULL),
(5, 16, '2024-02-01', 1100, 'seasonal', 0.8700, '2025-11-09 14:00:24', NULL),
(6, 19, '2024-02-01', 650, 'moving_average', 0.8000, '2025-11-09 14:00:24', NULL),
(7, 25, '2024-02-01', 2200, 'seasonal', 0.9200, '2025-11-09 14:00:24', NULL);

-- --------------------------------------------------------

--
-- Структура таблицы `inventory`
--

CREATE TABLE `inventory` (
  `inventory_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `warehouse_id` bigint(20) NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 0,
  `min_stock_level` int(11) DEFAULT 0,
  `max_stock_level` int(11) DEFAULT 1000,
  `last_updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Дамп данных таблицы `inventory`
--

INSERT INTO `inventory` (`inventory_id`, `product_id`, `warehouse_id`, `quantity`, `min_stock_level`, `max_stock_level`, `last_updated`) VALUES
(1, 1, 1, 0, 100, 1000, '2026-03-07 18:19:59'),
(2, 2, 1, 399, 80, 800, '2026-03-07 21:28:38'),
(3, 3, 1, 300, 60, 600, '2025-11-09 14:00:24'),
(4, 4, 1, 200, 40, 400, '2025-11-09 14:00:24'),
(5, 5, 1, 300, 50, 500, '2025-11-09 14:00:24'),
(6, 6, 1, 995, 200, 2000, '2026-03-07 21:11:23'),
(7, 1, 2, 50, 10, 100, '2025-11-09 14:00:24'),
(8, 2, 2, 40, 8, 80, '2025-11-09 14:00:24'),
(9, 7, 2, 30, 5, 60, '2026-03-07 18:37:10'),
(10, 8, 2, 32, 5, 50, '2026-03-03 09:29:19'),
(11, 13, 2, 100, 20, 200, '2025-11-09 14:00:24'),
(12, 14, 2, 80, 15, 150, '2025-11-09 14:00:24'),
(13, 19, 2, 60, 10, 120, '2025-11-09 14:00:24'),
(14, 20, 2, 50, 10, 100, '2025-11-09 14:00:24'),
(15, 25, 2, 40, 5, 80, '2025-11-09 14:00:24'),
(16, 1, 3, 45, 10, 90, '2025-11-09 14:00:24'),
(17, 3, 3, 35, 7, 70, '2025-11-09 14:00:24'),
(18, 9, 3, 20, 4, 40, '2025-11-09 14:00:24'),
(19, 10, 3, 30, 6, 60, '2025-11-09 14:00:24'),
(20, 15, 3, 70, 15, 140, '2025-11-09 14:00:24'),
(21, 16, 3, 60, 12, 120, '2025-11-09 14:00:24'),
(22, 21, 3, 55, 10, 110, '2025-11-09 14:00:24'),
(23, 22, 3, 65, 12, 130, '2025-11-09 14:00:24'),
(24, 26, 3, 35, 5, 70, '2025-11-09 14:00:24'),
(25, 2, 4, 38, 8, 75, '2025-11-09 14:00:24'),
(26, 4, 4, 28, 6, 55, '2025-11-09 14:00:24'),
(27, 11, 4, 25, 5, 50, '2025-11-09 14:00:24'),
(28, 12, 4, 22, 4, 45, '2025-11-09 14:00:24'),
(29, 17, 4, 55, 10, 110, '2025-11-09 14:00:24'),
(30, 18, 4, 48, 8, 95, '2025-11-09 14:00:24'),
(31, 23, 4, 42, 8, 85, '2025-11-09 14:00:24'),
(32, 24, 4, 58, 10, 115, '2025-11-09 14:00:24'),
(33, 27, 4, 30, 5, 60, '2025-11-09 14:00:24'),
(34, 18, 2, 1, 0, 999999, '2026-03-05 08:43:02'),
(35, 10, 1, 0, 0, 999999, '2026-03-07 18:36:38'),
(36, 16, 1, 0, 0, 999999, '2026-03-05 12:38:34');

-- --------------------------------------------------------

--
-- Структура таблицы `orderitem`
--

CREATE TABLE `orderitem` (
  `order_item_id` bigint(20) NOT NULL,
  `order_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `quantity` int(11) NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `total_price` decimal(12,2) GENERATED ALWAYS AS (`quantity` * `unit_price`) STORED
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Структура таблицы `product`
--

CREATE TABLE `product` (
  `product_id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `sku` varchar(50) NOT NULL,
  `barcode` varchar(100) DEFAULT NULL,
  `unit_of_measure` varchar(20) DEFAULT 'шт',
  `category_id` bigint(20) DEFAULT NULL,
  `default_supplier_id` bigint(20) DEFAULT NULL,
  `cost_price` decimal(10,2) DEFAULT 0.00,
  `selling_price` decimal(10,2) DEFAULT 0.00,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Дамп данных таблицы `product`
--

INSERT INTO `product` (`product_id`, `name`, `sku`, `barcode`, `unit_of_measure`, `category_id`, `default_supplier_id`, `cost_price`, `selling_price`, `is_active`, `created_at`, `updated_at`) VALUES
(1, 'Молоко Домик в деревне 3.2%', 'MLK-DOM-32-1L', '4601234000001', 'шт', 7, 1, 65.00, 89.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(2, 'Кефир Простоквашино 2.5%', 'KEF-PRO-25-1L', '4601234000002', 'шт', 7, 1, 55.00, 79.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(3, 'Ряженка Вкуснотеево 4%', 'RYA-VKU-40-1L', '4601234000003', 'шт', 7, 1, 60.00, 85.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(4, 'Сыр Российский 45%', 'CHS-ROS-45-1KG', '4601234000004', 'кг', 8, 1, 450.00, 650.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(5, 'Сыр Моцарелла', 'CHS-MOC-45-500G', '4601234000005', 'шт', 8, 1, 220.00, 320.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(6, 'Сыр Плавленый Дружба', 'CHS-DRU-60-100G', '4601234000006', 'шт', 8, 1, 25.00, 45.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(7, 'Говядина вырезка', 'BEE-ENT-1KG', '4601234000007', 'кг', 9, 2, 550.00, 790.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(8, 'Свинина шея', 'PORK-NEC-1KG', '4601234000008', 'кг', 9, 2, 380.00, 520.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(9, 'Баранина корейка', 'LAM-LOI-1KG', '4601234000009', 'кг', 9, 2, 600.00, 850.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(10, 'Курица охлажденная', 'CHI-WHO-1KG', '4601234000010', 'кг', 10, 2, 180.00, 250.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(11, 'Грудка куриная', 'CHI-BRE-1KG', '4601234000011', 'кг', 10, 2, 280.00, 390.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(12, 'Индейка филе', 'TUR-FIL-1KG', '4601234000012', 'кг', 10, 2, 320.00, 450.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(13, 'Картофель мытый', 'POT-WAS-1KG', '4601234000013', 'кг', 11, 3, 25.00, 45.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(14, 'Морковь свежая', 'CAR-FRE-1KG', '4601234000014', 'кг', 11, 3, 30.00, 55.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(15, 'Помидоры черри', 'TOM-CHE-500G', '4601234000015', 'шт', 11, 3, 80.00, 120.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(16, 'Яблоки Гренни Смит', 'APP-GRE-1KG', '4601234000016', 'кг', 12, 3, 90.00, 140.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(17, 'Бананы спелые', 'BAN-RIP-1KG', '4601234000017', 'кг', 12, 3, 70.00, 110.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(18, 'Апельсины Марокко', 'ORA-MOR-1KG', '4601234000018', 'кг', 12, 3, 100.00, 150.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(19, 'Рис Жменька 1кг', 'RIC-ZHM-1KG', '4601234000019', 'шт', 4, 4, 80.00, 120.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(20, 'Гречка Мистраль 1кг', 'BUC-MIS-1KG', '4601234000020', 'шт', 4, 4, 85.00, 130.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(21, 'Макароны Макфа 400г', 'MAC-MAK-400G', '4601234000021', 'шт', 4, 4, 45.00, 65.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(22, 'Сок Я 1л апельсин', 'JUC-YA-ORA-1L', '4601234000022', 'шт', 5, 5, 90.00, 130.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(23, 'Вода Бонаква 1.5л', 'WAT-BON-15L', '4601234000023', 'шт', 5, 5, 25.00, 45.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(24, 'Кола 2л', 'COL-COC-2L', '4601234000024', 'шт', 5, 5, 70.00, 110.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(25, 'Хлеб Бородинский', 'BLO-BOR-500G', '4601234000025', 'шт', 13, 6, 35.00, 55.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(26, 'Батон Нарезной', 'BAT-NAZ-400G', '4601234000026', 'шт', 13, 6, 25.00, 40.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(27, 'Булочки Сдобные', 'BUN-SDO-200G', '4601234000027', 'шт', 6, 6, 15.00, 28.00, 1, '2025-11-09 13:59:40', '2025-11-09 13:59:40');

-- --------------------------------------------------------

--
-- Структура таблицы `purchaseorder`
--

CREATE TABLE `purchaseorder` (
  `order_id` bigint(20) NOT NULL,
  `supplier_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `order_date` date NOT NULL,
  `status` enum('draft','ordered','delivered','cancelled') DEFAULT 'draft',
  `total_amount` decimal(12,2) DEFAULT 0.00,
  `expected_delivery` date DEFAULT NULL,
  `actual_delivery` date DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Структура таблицы `saleshistory`
--

CREATE TABLE `saleshistory` (
  `sales_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `warehouse_id` bigint(20) NOT NULL,
  `quantity` int(11) NOT NULL,
  `sale_date` date NOT NULL,
  `sale_price` decimal(10,2) NOT NULL,
  `customer_id` bigint(20) DEFAULT NULL,
  `transaction_id` varchar(100) DEFAULT NULL,
  `batch_id` bigint(20) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Дамп данных таблицы `saleshistory`
--

INSERT INTO `saleshistory` (`sales_id`, `product_id`, `warehouse_id`, `quantity`, `sale_date`, `sale_price`, `customer_id`, `transaction_id`, `batch_id`, `created_at`) VALUES
(1, 1, 2, 2, '2024-01-18', 89.00, NULL, 'TXN-000001', NULL, '2025-11-09 14:00:24'),
(2, 13, 2, 1, '2024-01-18', 45.00, NULL, 'TXN-000001', NULL, '2025-11-09 14:00:24'),
(3, 25, 2, 1, '2024-01-18', 55.00, NULL, 'TXN-000001', NULL, '2025-11-09 14:00:24'),
(4, 2, 2, 1, '2024-01-18', 79.00, NULL, 'TXN-000002', NULL, '2025-11-09 14:00:24'),
(5, 7, 2, 1, '2024-01-18', 395.00, NULL, 'TXN-000002', NULL, '2025-11-09 14:00:24'),
(6, 19, 2, 1, '2024-01-18', 120.00, NULL, 'TXN-000002', NULL, '2025-11-09 14:00:24'),
(7, 3, 3, 1, '2024-01-18', 85.00, NULL, 'TXN-000003', NULL, '2025-11-09 14:00:24'),
(8, 16, 3, 2, '2024-01-18', 140.00, NULL, 'TXN-000003', NULL, '2025-11-09 14:00:24'),
(9, 26, 3, 1, '2024-01-18', 40.00, NULL, 'TXN-000003', NULL, '2025-11-09 14:00:24'),
(10, 9, 3, 1, '2024-01-18', 632.00, NULL, 'TXN-000004', NULL, '2025-11-09 14:00:24'),
(11, 15, 3, 0, '2024-01-18', 36.00, NULL, 'TXN-000004', NULL, '2025-11-09 14:00:24'),
(12, 22, 3, 2, '2024-01-18', 130.00, NULL, 'TXN-000004', NULL, '2025-11-09 14:00:24'),
(13, 4, 4, 0, '2024-01-18', 260.00, NULL, 'TXN-000005', NULL, '2025-11-09 14:00:24'),
(14, 17, 4, 2, '2024-01-18', 165.00, NULL, 'TXN-000005', NULL, '2025-11-09 14:00:24'),
(15, 24, 4, 1, '2024-01-18', 110.00, NULL, 'TXN-000005', NULL, '2025-11-09 14:00:24'),
(16, 11, 4, 1, '2024-01-18', 468.00, NULL, 'TXN-000006', NULL, '2025-11-09 14:00:24'),
(17, 18, 4, 2, '2024-01-18', 150.00, NULL, 'TXN-000006', NULL, '2025-11-09 14:00:24'),
(18, 27, 4, 3, '2024-01-18', 28.00, NULL, 'TXN-000006', NULL, '2025-11-09 14:00:24');

-- --------------------------------------------------------

--
-- Структура таблицы `stockmovement`
--

CREATE TABLE `stockmovement` (
  `movement_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `from_warehouse_id` bigint(20) DEFAULT NULL,
  `to_warehouse_id` bigint(20) DEFAULT NULL,
  `movement_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `type` enum('receipt','transfer','write_off','sale','adjustment') NOT NULL,
  `quantity` int(11) NOT NULL,
  `reason` text DEFAULT NULL,
  `reference_number` varchar(100) DEFAULT NULL,
  `batch_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Триггеры `stockmovement`
--
DELIMITER $$
CREATE TRIGGER `update_inventory_after_stockmovement_insert` AFTER INSERT ON `stockmovement` FOR EACH ROW BEGIN
    DECLARE v_current_quantity INT;
    DECLARE v_min_stock_level INT;
    DECLARE error_msg VARCHAR(200);
    
    -- Обрабатываем только движения, которые уменьшают остатки (transfer, write_off, sale)
    IF NEW.type IN ('transfer', 'write_off', 'sale') AND NEW.from_warehouse_id IS NOT NULL THEN
        
        -- Проверяем существование записи в inventory
        SELECT quantity, min_stock_level INTO v_current_quantity, v_min_stock_level
        FROM Inventory
        WHERE product_id = NEW.product_id 
        AND warehouse_id = NEW.from_warehouse_id;
        
        IF v_current_quantity IS NOT NULL THEN
            -- Проверяем достаточно ли товара
            IF v_current_quantity < NEW.quantity THEN
                SET error_msg = CONCAT('Ошибка: недостаточно товара на складе-отправителе. Доступно: ', 
                                      v_current_quantity, ', запрошено: ', NEW.quantity);
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = error_msg;
            ELSE
                -- Уменьшаем остатки на складе-отправителе
                UPDATE Inventory 
                SET quantity = quantity - NEW.quantity,
                    last_updated = NOW()
                WHERE product_id = NEW.product_id 
                AND warehouse_id = NEW.from_warehouse_id;
                
                -- Проверяем, не упал ли запас ниже минимального
                IF (v_current_quantity - NEW.quantity) < v_min_stock_level THEN
                    -- Проверяем существует ли таблица Stock_alerts
                    INSERT INTO Stock_alerts (product_id, warehouse_id, current_quantity, alert_type, created_at)
                    VALUES (NEW.product_id, NEW.from_warehouse_id, (v_current_quantity - NEW.quantity), 'LOW_STOCK', NOW());
                END IF;
            END IF;
        ELSE
            SET error_msg = CONCAT('Ошибка: товар ', NEW.product_id, ' не найден на складе ', NEW.from_warehouse_id);
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = error_msg;
        END IF;
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Структура таблицы `supplier`
--

CREATE TABLE `supplier` (
  `supplier_id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `contact_person` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `tax_id` varchar(100) DEFAULT NULL,
  `rating` decimal(3,2) DEFAULT NULL CHECK (`rating` >= 0 and `rating` <= 5),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Дамп данных таблицы `supplier`
--

INSERT INTO `supplier` (`supplier_id`, `name`, `contact_person`, `email`, `phone`, `address`, `tax_id`, `rating`, `created_at`, `updated_at`) VALUES
(1, 'ООО МолокоПром', 'Семенов Андрей', 'milk@supplier.ru', '+7-495-111-22-33', 'Московская обл., д. Молочное, ул. Заводская, 1', '7711111111', 4.70, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(2, 'АО МяснойДвор', 'Орлова Татьяна', 'meat@supplier.ru', '+7-495-222-33-44', 'Москва, ул. Мясницкая, 25', '7711111112', 4.50, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(3, 'ИП ФруктовыйРай', 'Николаев Виктор', 'fruits@supplier.ru', '+7-495-333-44-55', 'Москва, пл. Фруктовая, 8', '7711111113', 4.80, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(4, 'ООО ЗерноПродукт', 'Ковалева Ирина', 'grocery@supplier.ru', '+7-495-444-55-66', 'Москва, ш. Энтузиастов, 150', '7711111114', 4.30, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(5, 'АО НапиткиРФ', 'Волков Сергей', 'drinks@supplier.ru', '+7-495-555-66-77', 'Москва, пр-т Напиточный, 45', '7711111115', 4.60, '2025-11-09 13:59:40', '2025-11-09 13:59:40'),
(6, 'ООО Хлебозавод №1', 'Павлова Елена', 'bread@supplier.ru', '+7-495-666-77-88', 'Москва, ул. Хлебная, 12', '7711111116', 4.90, '2025-11-09 13:59:40', '2025-11-09 13:59:40');

-- --------------------------------------------------------

--
-- Структура таблицы `user`
--

CREATE TABLE `user` (
  `user_id` bigint(20) NOT NULL,
  `username` varchar(100) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` enum('ADMIN','MANAGER','STOREKEEPER','SENIOR_STOREKEEPER') NOT NULL,
  `shopid` bigint(20) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `last_login` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Дамп данных таблицы `user`
--

INSERT INTO `user` (`user_id`, `username`, `password_hash`, `role`, `shopid`, `full_name`, `email`, `phone`, `is_active`, `last_login`, `created_at`) VALUES
(11, 'marishka', '$2a$10$JpFoKRli2.3s5mfn9TRuC.x8qhgymDgmAaFrE.nGXcztNEWy38mTe', 'ADMIN', NULL, 'BOSS', 'marishka@marishka.com', NULL, 1, NULL, '2026-03-06 19:04:26'),
(12, 'nashtya', '$2a$10$tja2Sp7xqD47/GxY3vZZS.nFiW1mnK0y67qOeUZF9s6ecfGG7ebiW', 'MANAGER', 1, 'стейси крутая девчонка', 'стейси@наштя', '1111111111', 1, NULL, '2026-03-07 10:28:17'),
(15, 'lox', '$2a$10$6Mr79pd4.sg.71RdH7.puuoo26LgI3SwMb3rrhKo34H3E8kxV2dmG', 'STOREKEEPER', 1, '', '', '', 1, NULL, '2026-03-07 16:45:52'),
(19, 'user', '$2a$10$XOjFGeP6r4wcds0hHDFVVeLrc./RLunNmK7J9hZTkGZ7vZtBWXiKm', 'STOREKEEPER', 1, 'testuser', '', '', 1, NULL, '2026-03-07 17:19:34'),
(22, 'testuser', '$2a$10$AiPEfb9NNSPAY5Rd5MUql.NC3Ertsqe6MF5gt5subdoXH4fvbR9y2', 'SENIOR_STOREKEEPER', 1, '', '', '', 1, NULL, '2026-03-07 17:31:11');

-- --------------------------------------------------------

--
-- Структура таблицы `warehouse`
--

CREATE TABLE `warehouse` (
  `warehouse_id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `address` text DEFAULT NULL,
  `type` enum('store','warehouse') NOT NULL,
  `responsible_person` varchar(255) DEFAULT NULL,
  `capacity` decimal(12,2) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Дамп данных таблицы `warehouse`
--

INSERT INTO `warehouse` (`warehouse_id`, `name`, `address`, `type`, `responsible_person`, `capacity`, `is_active`, `created_at`) VALUES
(1, 'Центральный склад', 'Москва, ул. Складская, 1', 'warehouse', 'Петров Иван', 5000.00, 1, '2025-11-09 13:59:40'),
(2, 'Супермаркет \"Центральный\"', 'Москва, ул. Тверская, 25', 'store', 'Сидорова Ольга', 800.00, 1, '2025-11-09 13:59:40'),
(3, 'Супермаркет \"Западный\"', 'Москва, ул. Ленинградский пр-т, 42', 'store', 'Кузнецов Дмитрий', 750.00, 1, '2025-11-09 13:59:40'),
(4, 'Супермаркет \"Восточный\"', 'Москва, ул. Щелковское ш., 150', 'store', 'Васильева Анна', 700.00, 1, '2025-11-09 13:59:40');

--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `batch`
--
ALTER TABLE `batch`
  ADD PRIMARY KEY (`batch_id`),
  ADD UNIQUE KEY `unique_batch` (`product_id`,`batch_number`),
  ADD KEY `warehouse_id` (`warehouse_id`),
  ADD KEY `supplier_batch_id` (`supplier_batch_id`);

--
-- Индексы таблицы `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`category_id`),
  ADD KEY `parent_category_id` (`parent_category_id`);

--
-- Индексы таблицы `forecast`
--
ALTER TABLE `forecast`
  ADD PRIMARY KEY (`forecast_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Индексы таблицы `inventory`
--
ALTER TABLE `inventory`
  ADD PRIMARY KEY (`inventory_id`),
  ADD UNIQUE KEY `unique_product_warehouse` (`product_id`,`warehouse_id`),
  ADD KEY `warehouse_id` (`warehouse_id`);

--
-- Индексы таблицы `orderitem`
--
ALTER TABLE `orderitem`
  ADD PRIMARY KEY (`order_item_id`),
  ADD KEY `order_id` (`order_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Индексы таблицы `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`product_id`),
  ADD UNIQUE KEY `sku` (`sku`),
  ADD KEY `category_id` (`category_id`),
  ADD KEY `default_supplier_id` (`default_supplier_id`);

--
-- Индексы таблицы `purchaseorder`
--
ALTER TABLE `purchaseorder`
  ADD PRIMARY KEY (`order_id`),
  ADD KEY `supplier_id` (`supplier_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Индексы таблицы `saleshistory`
--
ALTER TABLE `saleshistory`
  ADD PRIMARY KEY (`sales_id`),
  ADD KEY `product_id` (`product_id`),
  ADD KEY `warehouse_id` (`warehouse_id`),
  ADD KEY `batch_id` (`batch_id`);

--
-- Индексы таблицы `stockmovement`
--
ALTER TABLE `stockmovement`
  ADD PRIMARY KEY (`movement_id`),
  ADD KEY `product_id` (`product_id`),
  ADD KEY `from_warehouse_id` (`from_warehouse_id`),
  ADD KEY `to_warehouse_id` (`to_warehouse_id`),
  ADD KEY `batch_id` (`batch_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Индексы таблицы `supplier`
--
ALTER TABLE `supplier`
  ADD PRIMARY KEY (`supplier_id`);

--
-- Индексы таблицы `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD KEY `shopid` (`shopid`);

--
-- Индексы таблицы `warehouse`
--
ALTER TABLE `warehouse`
  ADD PRIMARY KEY (`warehouse_id`);

--
-- AUTO_INCREMENT для сохранённых таблиц
--

--
-- AUTO_INCREMENT для таблицы `batch`
--
ALTER TABLE `batch`
  MODIFY `batch_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT для таблицы `category`
--
ALTER TABLE `category`
  MODIFY `category_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT для таблицы `forecast`
--
ALTER TABLE `forecast`
  MODIFY `forecast_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT для таблицы `inventory`
--
ALTER TABLE `inventory`
  MODIFY `inventory_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- AUTO_INCREMENT для таблицы `orderitem`
--
ALTER TABLE `orderitem`
  MODIFY `order_item_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT для таблицы `product`
--
ALTER TABLE `product`
  MODIFY `product_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=28;

--
-- AUTO_INCREMENT для таблицы `purchaseorder`
--
ALTER TABLE `purchaseorder`
  MODIFY `order_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT для таблицы `saleshistory`
--
ALTER TABLE `saleshistory`
  MODIFY `sales_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT для таблицы `stockmovement`
--
ALTER TABLE `stockmovement`
  MODIFY `movement_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT для таблицы `supplier`
--
ALTER TABLE `supplier`
  MODIFY `supplier_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT для таблицы `user`
--
ALTER TABLE `user`
  MODIFY `user_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT для таблицы `warehouse`
--
ALTER TABLE `warehouse`
  MODIFY `warehouse_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Ограничения внешнего ключа сохраненных таблиц
--

--
-- Ограничения внешнего ключа таблицы `batch`
--
ALTER TABLE `batch`
  ADD CONSTRAINT `batch_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `batch_ibfk_2` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`warehouse_id`) ON DELETE SET NULL,
  ADD CONSTRAINT `batch_ibfk_3` FOREIGN KEY (`supplier_batch_id`) REFERENCES `supplier` (`supplier_id`);

--
-- Ограничения внешнего ключа таблицы `category`
--
ALTER TABLE `category`
  ADD CONSTRAINT `category_ibfk_1` FOREIGN KEY (`parent_category_id`) REFERENCES `category` (`category_id`) ON DELETE SET NULL;

--
-- Ограничения внешнего ключа таблицы `forecast`
--
ALTER TABLE `forecast`
  ADD CONSTRAINT `forecast_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE;

--
-- Ограничения внешнего ключа таблицы `inventory`
--
ALTER TABLE `inventory`
  ADD CONSTRAINT `inventory_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `inventory_ibfk_2` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`warehouse_id`) ON DELETE CASCADE;

--
-- Ограничения внешнего ключа таблицы `orderitem`
--
ALTER TABLE `orderitem`
  ADD CONSTRAINT `orderitem_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `purchaseorder` (`order_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `orderitem_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`);

--
-- Ограничения внешнего ключа таблицы `product`
--
ALTER TABLE `product`
  ADD CONSTRAINT `product_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`) ON DELETE SET NULL,
  ADD CONSTRAINT `product_ibfk_2` FOREIGN KEY (`default_supplier_id`) REFERENCES `supplier` (`supplier_id`) ON DELETE SET NULL;

--
-- Ограничения внешнего ключа таблицы `purchaseorder`
--
ALTER TABLE `purchaseorder`
  ADD CONSTRAINT `purchaseorder_ibfk_1` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`),
  ADD CONSTRAINT `purchaseorder_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);

--
-- Ограничения внешнего ключа таблицы `saleshistory`
--
ALTER TABLE `saleshistory`
  ADD CONSTRAINT `saleshistory_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`),
  ADD CONSTRAINT `saleshistory_ibfk_2` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`warehouse_id`),
  ADD CONSTRAINT `saleshistory_ibfk_3` FOREIGN KEY (`batch_id`) REFERENCES `batch` (`batch_id`) ON DELETE SET NULL;

--
-- Ограничения внешнего ключа таблицы `stockmovement`
--
ALTER TABLE `stockmovement`
  ADD CONSTRAINT `stockmovement_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`),
  ADD CONSTRAINT `stockmovement_ibfk_2` FOREIGN KEY (`from_warehouse_id`) REFERENCES `warehouse` (`warehouse_id`) ON DELETE SET NULL,
  ADD CONSTRAINT `stockmovement_ibfk_3` FOREIGN KEY (`to_warehouse_id`) REFERENCES `warehouse` (`warehouse_id`) ON DELETE SET NULL,
  ADD CONSTRAINT `stockmovement_ibfk_4` FOREIGN KEY (`batch_id`) REFERENCES `batch` (`batch_id`) ON DELETE SET NULL,
  ADD CONSTRAINT `stockmovement_ibfk_5` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);

--
-- Ограничения внешнего ключа таблицы `user`
--
ALTER TABLE `user`
  ADD CONSTRAINT `user_ibfk_1` FOREIGN KEY (`shopid`) REFERENCES `warehouse` (`warehouse_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
