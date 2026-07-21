-- Default policy documented in fine-service/FLOW.md:
-- 5,000 VND for each overdue day, with the overdue threshold at 30 days.
-- No undocumented flat lost-book surcharge is introduced here; lost-book
-- compensation is calculated separately from the catalog book value.
--
-- The guard keeps this migration safe for environments where an administrator
-- has already configured an active policy before this migration is deployed.
INSERT INTO fine_policy (
    daily_rate,
    is_active,
    lost_threshold_days,
    lost_penalty,
    create_at,
    update_at
)
SELECT
    5000,
    TRUE,
    30,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1
    FROM fine_policy
    WHERE is_active = TRUE
);
