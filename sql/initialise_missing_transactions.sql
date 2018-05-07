
CREATE OR REPLACE FUNCTION public.initialise_missing_transactions () RETURNS void AS
$BODY$
    DECLARE
		initial_balance 	NUMERIC;
		pil_accounts_id		VARCHAR;
		master_accounts_id	VARCHAR;
		transfer_id			VARCHAR;
		transactions_id		VARCHAR;
        missing_transactions RECORD;
    BEGIN
        SELECT bm_accounts_id INTO master_accounts_id  FROM tue4_bm_accounts WHERE bm_accounttype = 'CORP';
        FOR missing_transactions IN
            SELECT
                DISTINCT pil.pil_id
                , acc.bm_accounts_id
                , ord.id
                , ord.created_at
                , pro.title
                , pro.initial_tec 
            FROM
                tue4_pilot pil 
                    INNER JOIN users usu 
                    ON usu.id = pil.pil_usu_id 
                        INNER JOIN orders ord 
                        ON ord.user_id = usu.id 
                            INNER JOIN orderitems ori 
                            ON ori.order_id = ord.id 
                                INNER JOIN products pro 
                                ON pro.id = ori.product_id 
                                    INNER JOIN tue4_bm_accounts acc 
                                    ON acc.pil_id = pil.pil_id 
            WHERE
                NOT EXISTS (SELECT
                                * 
                            FROM
                                tue4_bm_transactions tra 
                            WHERE
                                tra.bm_accounts_id = acc.bm_accounts_id 
                                AND tra.bm_amount = pro.initial_tec) 
                AND pro.initial_tec IS NOT NULL 
                AND ord.status = 'APPROVED' 
            LOOP
                SELECT replace(uuid_generate_v4()::"varchar",'-','') INTO transfer_id;

                SELECT replace(uuid_generate_v4()::"varchar",'-','') INTO transactions_id;
                INSERT INTO tue4_bm_transactions
                (bm_transactions_id, bm_transfer_id, bm_accounts_id, bm_amount, bm_paymentstatus, created_at, updated_at)
                VALUES
                (transactions_id, transfer_id, missing_transactions.bm_accounts_id, missing_transactions.initial_tec, 'COMPLETE', now(), now());

                SELECT replace(uuid_generate_v4()::"varchar",'-','') INTO transactions_id;
                INSERT INTO tue4_bm_transactions
                (bm_transactions_id, bm_transfer_id, bm_accounts_id, bm_amount, bm_paymentstatus, created_at, updated_at)
                VALUES
                (transactions_id, transfer_id, master_accounts_id, -missing_transactions.initial_tec, 'COMPLETE', now(), now());
            END LOOP;
    END;
$BODY$
LANGUAGE 'plpgsql'
GO
