INSERT INTO public.users (email, name)
VALUES ('email1@test.com', 'name1');

INSERT INTO public.users (email, name)
VALUES ('email2@test.com', 'name2');

INSERT INTO public.items (name, description, is_available, owner_id)
VALUES ('name1', 'description1', true, 1);

INSERT INTO public.items (name, description, is_available, owner_id)
VALUES ('name2', 'description2', true, 1);

INSERT INTO public.items (name, description, is_available, owner_id)
VALUES ('name3', 'something', true, 1);

INSERT INTO public.bookings (start_date, end_date, item_id, booker_id, status)
VALUES ('2024-01-01', '2024-02-01', 1, 2, 'WAITING');

INSERT INTO public.bookings (start_date, end_date, item_id, booker_id, status)
VALUES ('2024-01-01', '2024-02-01', 2, 2, 'WAITING');