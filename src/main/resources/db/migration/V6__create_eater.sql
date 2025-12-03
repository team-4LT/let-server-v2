-- Eater 테이블
CREATE TABLE IF NOT EXISTS `eaters` (
                                        `eater_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                        `user_id` BIGINT NOT NULL,
                                        `meal_id` INT NOT NULL,
                                        `eaten` BOOLEAN NOT NULL DEFAULT FALSE,
                                        UNIQUE (`user_id`, `meal_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`meal_id`) REFERENCES `meals` (`meal_id`) ON DELETE CASCADE
    );