-- User Types
-- id=1: Dono de Restaurante (system, canManageRestaurants=true)
-- id=2: Cliente (system, canManageRestaurants=false)
-- id=3: Motoboy (não-system, canManageRestaurants=false)
INSERT INTO user_types (name, is_system, can_manage_restaurants, created_at, last_modified_at)
VALUES ('Dono de Restaurante', TRUE,  TRUE,  '2026-06-01T00:00:00', '2026-06-01T00:00:00'),
       ('Cliente',             TRUE,  FALSE, '2026-06-01T00:00:00', '2026-06-01T00:00:00'),
       ('Motoboy',             FALSE, FALSE, '2026-06-01T00:00:00', '2026-06-01T00:00:00');

-- Users
-- id=1: João (Cliente, userTypeId=2)
-- id=2: Maria (Dono de Restaurante, userTypeId=1) — owner dos restaurantes seedados
-- id=3: Carlos (Dono de Restaurante, userTypeId=1) — usado nos cenários 403 (não é o owner)
INSERT INTO users (name, email, address, user_type_id, created_at, last_modified_at)
VALUES ('João da Silva',   'joao.silva@email.com',    'Rua das Flores, 123 - São Paulo, SP', 2, '2026-06-01T00:00:00', '2026-06-01T00:00:00'),
       ('Maria Oliveira',  'maria.oliveira@email.com', 'Av. Paulista, 1000 - São Paulo, SP', 1, '2026-06-01T00:00:00', '2026-06-01T00:00:00'),
       ('Carlos Souza',    'carlos.souza@email.com',  'Rua Augusta, 500 - São Paulo, SP',    1, '2026-06-01T00:00:00', '2026-06-01T00:00:00');

-- Restaurants
-- id=1: Pizzaria Bella Napoli (owner=2 Maria)
-- id=2: Churrascaria Gaúcha (owner=2 Maria) — para testar filtros e listagem
INSERT INTO restaurants (name, address, kitchen_type, opening_hours, owner_id, created_at, last_modified_at)
VALUES ('Pizzaria Bella Napoli', 'Av. Paulista, 1000 - São Paulo, SP', 'Italiana',   'Seg-Sex 11h-22h, Sab-Dom 11h-23h', 2, '2026-06-01T00:00:00', '2026-06-01T00:00:00'),
       ('Churrascaria Gaúcha',   'Rua Augusta, 200 - São Paulo, SP',   'Brasileira', 'Ter-Dom 12h-23h',                  2, '2026-06-01T00:00:00', '2026-06-01T00:00:00');

-- Menu Items
-- restaurant_id=1 (Pizzaria Bella Napoli):
--   id=1: Pizza Margherita (com foto)
--   id=2: Calzone de Presunto (dineInOnly=true, sem foto)
-- restaurant_id=2 (Churrascaria Gaúcha):
--   id=3: Picanha na Brasa (com foto)
--   id=4: Rodízio Completo (dineInOnly=true, sem foto)
INSERT INTO menu_items (restaurant_id, name, description, price, dine_in_only, photo_path, created_at, last_modified_at)
VALUES (1, 'Pizza Margherita',    'Molho de tomate, mussarela e manjericão fresco',                     29.90, FALSE, '/images/pizza-margherita.jpg',   '2026-06-01T00:00:00', '2026-06-01T00:00:00'),
       (1, 'Calzone de Presunto', 'Massa recheada com presunto, mussarela e cogumelos. Só no local.',   34.90, TRUE,  NULL,                             '2026-06-01T00:00:00', '2026-06-01T00:00:00'),
       (2, 'Picanha na Brasa',    'Corte nobre grelhado na brasa, acompanha farofa e vinagrete',        69.90, FALSE, '/images/picanha-na-brasa.jpg',   '2026-06-01T00:00:00', '2026-06-01T00:00:00'),
       (2, 'Rodízio Completo',    'Todos os cortes à vontade, servido apenas no salão.',                89.90, TRUE,  NULL,                             '2026-06-01T00:00:00', '2026-06-01T00:00:00');

