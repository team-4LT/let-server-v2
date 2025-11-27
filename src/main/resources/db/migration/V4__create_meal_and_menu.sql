-- Menu 테이블
CREATE TABLE IF NOT EXISTS `menus` (
                                       `menu_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                       `menu_name` VARCHAR(255) NOT NULL,
    `menu_score` DOUBLE NOT NULL DEFAULT 0.0,
    `like_count` BIGINT NOT NULL DEFAULT 0,
    `dislike_count` BIGINT NOT NULL DEFAULT 0,
    `current_rank` INT DEFAULT NULL
    );

-- Meal 테이블
CREATE TABLE IF NOT EXISTS `meals` (
                                       `meal_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                       `meal_date` DATE NOT NULL,
                                       `meal_type` ENUM('조식', '중식', '석식') NOT NULL,
    `score` FLOAT NOT NULL DEFAULT 0.0,
    `calorie` FLOAT NOT NULL DEFAULT 0.0,
    UNIQUE (`meal_date`, `meal_type`)
    );

-- MealMenu (중간 테이블)
CREATE TABLE IF NOT EXISTS `meal_menus` (
                                            `meal_menu_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                            `menu_id` BIGINT NOT NULL,
                                            `meal_id` INT NOT NULL,
                                            UNIQUE (`menu_id`, `meal_id`),
    FOREIGN KEY (`menu_id`) REFERENCES `menus` (`menu_id`) ON DELETE CASCADE,
    FOREIGN KEY (`meal_id`) REFERENCES `meals` (`meal_id`) ON DELETE CASCADE
    );