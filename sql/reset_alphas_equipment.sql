
CREATE OR REPLACE FUNCTION public.reset_alphas_equipment () RETURNS void AS
$BODY$
    DECLARE
        alpha_player RECORD;
        orderitem_id int8;
        order_id int8;
    BEGIN
        
        FOR alpha_player IN
            SELECT
                id
                , role_id 
            FROM users usu 
            WHERE
                usu.alpha = TRUE 
                AND usu.nda = TRUE 
                AND NOT EXISTS (SELECT
                                    * 
                                FROM
                                    orders ord 
                                    INNER JOIN orderitems ori 
                                    ON ori.order_id = ord.id 
                                    INNER JOIN products pro 
                                    ON pro.id = ori.product_id 
                                WHERE
                                    product_category_id IN (1
                                    ,18) 
                                    AND user_id = usu.id)
        LOOP
            RAISE NOTICE 'Processing user %', alpha_player.id;
            
            INSERT INTO orders(id, user_id, status, processor, created_at, updated_at)
                VALUES (default, alpha_player.id, 'PENDING', 'AlphaTeam', NOW(), NOW()) RETURNING id INTO order_id;
            INSERT INTO orderitems(id, order_id, product_id, title, quantity, created_at, updated_at) 
                VALUES(default, order_id, 1, 'Competitor Pack', 1, NOW(), NOW());
            
            IF alpha_player.role_id > 1 THEN
                RAISE NOTICE 'User % is special; %s', alpha_player.id, alpha_player.role_id;
                INSERT INTO orderitems(id, order_id, product_id, title, quantity, created_at, updated_at) 
                    VALUES(default, order_id, 2, 'Veteran Pack', 1, NOW(), NOW());
                INSERT INTO orderitems(id, order_id, product_id, title, quantity, created_at, updated_at) 
                    VALUES(default, order_id, 3, 'Ace Pack', 1, NOW(), NOW());
            END IF;
            UPDATE orders SET status = 'APPROVED' WHERE id = order_id;
        END LOOP;
    END;
$BODY$
LANGUAGE 'plpgsql'
GO
