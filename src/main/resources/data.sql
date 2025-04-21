INSERT INTO netology.users (login, password)
SELECT 'test_user', '$2a$10$/PyXdiYLmGp1o0cOAjSuWuFEg2g5TQ4dFiRAFx0nIdaa2r0riWxca'
    WHERE NOT EXISTS (
    SELECT 1 FROM netology.users WHERE login = 'test_user'
);