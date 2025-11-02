-- Script SQL para corrigir a coluna role no banco de dados
-- Execute este script no seu banco PostgreSQL (Supabase)

-- 1. Remover a constraint check se existir
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;

-- 2. Alterar o tipo da coluna de smallint para VARCHAR
ALTER TABLE users ALTER COLUMN role TYPE VARCHAR(50);

-- 3. Adicionar constraint check para validar os valores
ALTER TABLE users ADD CONSTRAINT users_role_check 
    CHECK (role IN ('ADMIN', 'USER', 'PROFESSOR', 'COORDENADOR'));

-- Pronto! Agora a coluna aceita strings
