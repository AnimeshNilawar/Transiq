-- Drop the check constraint on merchant_users.role so that PLATFORM_ADMIN can be stored
ALTER TABLE merchant_users DROP CONSTRAINT IF EXISTS merchant_users_role_check;
