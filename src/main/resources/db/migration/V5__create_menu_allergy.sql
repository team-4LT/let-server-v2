-- MenuAllergy (중간 테이블)
CREATE TABLE IF NOT EXISTS `menu_allergies` (
                                                `menu_id` BIGINT NOT NULL,
                                                `allergy` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`menu_id`, `allergy`),
    FOREIGN KEY (`menu_id`) REFERENCES `menus` (`menu_id`) ON DELETE CASCADE
    );