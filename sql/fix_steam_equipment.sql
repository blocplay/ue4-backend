
CREATE OR REPLACE FUNCTION public.fix_steam_equipment () RETURNS void AS
$BODY$
    DECLARE
        steam_player RECORD;
        orderitem_id int8;
        order_id int8;
    BEGIN
        
        FOR steam_player IN
                SELECT
                    * 
                FROM
                    users usu 
                WHERE
                    steam_id IS NOT NULL 
                    AND NOT EXISTS (SELECT
                                        * 
                                    FROM
                                        orders ord 
                                        INNER JOIN orderitems ori 
                                        ON ori.order_id = ord.id 
                                        INNER JOIN products pro 
                                        ON pro.id = ori.product_id 
                                    WHERE
                                        product_category_id IN (1) 
                                        AND user_id = usu.id)
        LOOP
            RAISE NOTICE 'Processing user %', steam_player.id;
            
            INSERT INTO orders(id, user_id, status, processor, created_at, updated_at)
                VALUES (default, steam_player.id, 'PENDING', 'SteamFix', NOW(), NOW()) RETURNING id INTO order_id;
            INSERT INTO orderitems(id, order_id, product_id, title, quantity, created_at, updated_at) 
                VALUES(default, order_id, 1, 'Competitor Pack', 1, NOW(), NOW());
            UPDATE orders SET status = 'APPROVED' WHERE id = order_id;
        END LOOP;
    END;
$BODY$
LANGUAGE 'plpgsql'
GO
