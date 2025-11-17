-- 사용자 알러지 중간 테이블 생성
CREATE TABLE IF NOT EXISTS `user_allergies` (
    `user_id` BIGINT NOT NULL,
    `allergy` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`user_id`, `allergy`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE
);