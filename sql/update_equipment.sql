
CREATE OR REPLACE FUNCTION public.update_equipment () RETURNS trigger AS
$BODY$
    DECLARE
        new_equipment   RECORD;
		added_amount 	NUMERIC;
		pil_accounts_id	VARCHAR;
		master_accounts_id	VARCHAR;
		transfer_id			VARCHAR;
		transactions_id		VARCHAR;
    BEGIN
        IF NEW.status = 'APPROVED' AND OLD.status <> 'APPROVED' THEN
            FOR new_equipment IN
                SELECT  eqa_quantity
                        , leq_id
                        , leq_ino_id
                        , leq_lge_id
                        , (' - ' || oit.id) extra_id
                        , lge_gem_id
                        , pil_id
                FROM orderitems oit
                    INNER JOIN products prd
                        ON prd.id = oit.product_id
                    INNER JOIN users usu
                        ON usu.id = OLD.user_id
                    INNER JOIN tue4_pilot pil
                        ON pil.pil_usu_id = usu.id
                    INNER JOIN tue4_equipment_awarded eqa
                        ON eqa_product_id = prd.id
                    INNER JOIN tue4_lore_equipment leq
                        ON leq_id = eqa_awarded_leq_id
                    LEFT JOIN tue4_lore_gears
                        ON lge_id = leq_lge_id
                    WHERE
                        oit.order_id = OLD.id
            LOOP
                PERFORM initialise_equipment_from_order(
                    OLD.id
                    , new_equipment.pil_id
                    , new_equipment.leq_id
                    , new_equipment.lge_gem_id
                    , new_equipment.extra_id
                    , new_equipment.eqa_quantity::integer
                );
            END LOOP;
        END IF;
        
            SELECT bm_accounts_id INTO master_accounts_id  FROM tue4_bm_accounts WHERE bm_accounttype = 'CORP';
    		SELECT
    			SUM(prd.initial_tec)
    		INTO added_amount
    		FROM orderitems ods 
    			JOIN products prd 
    				ON prd.id = ods.product_id 
    					AND prd.initial_tec IS NOT NULL
            WHERE ods.order_id = OLD.id;
            
            IF added_amount IS NULL THEN
                added_amount = 0;
            END IF;
    		
            select acc.bm_accounts_id from tue4_pilot pil
            INTO pil_accounts_id
            inner join tue4_bm_accounts acc on acc.pil_id = pil.pil_id
            where pil.pil_usu_id = OLD.user_id;

            IF pil_accounts_id IS NOT NULL THEN
                SELECT replace(uuid_generate_v4()::"varchar",'-','') INTO transfer_id;

                SELECT replace(uuid_generate_v4()::"varchar",'-','') INTO transactions_id;
                INSERT INTO tue4_bm_transactions
                (bm_transactions_id, bm_transfer_id, bm_accounts_id, bm_amount, bm_paymentstatus, created_at, updated_at)
                VALUES
                (transactions_id, transfer_id, pil_accounts_id, added_amount, 'COMPLETE', now(), now());

                SELECT replace(uuid_generate_v4()::"varchar",'-','') INTO transactions_id;
                INSERT INTO tue4_bm_transactions
                (bm_transactions_id, bm_transfer_id, bm_accounts_id, bm_amount, bm_paymentstatus, created_at, updated_at)
                VALUES
                (transactions_id, transfer_id, master_accounts_id, -added_amount, 'COMPLETE', now(), now());
           END IF;

        RETURN NEW;
    END;
$BODY$
LANGUAGE 'plpgsql'
GO
