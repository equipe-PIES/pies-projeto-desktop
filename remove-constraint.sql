-- Execute este comando no SQL Editor do Supabase
-- Isso vai remover a constraint que está bloqueando o registro

ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;

-- Pronto! Agora você pode registrar usuários
