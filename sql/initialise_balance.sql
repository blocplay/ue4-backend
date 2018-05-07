
CREATE OR REPLACE FUNCTION public.initialise_balance () RETURNS trigger AS
$BODY$
    DECLARE
		initial_balance 	NUMERIC;
		pil_accounts_id		VARCHAR;
		master_accounts_id	VARCHAR;
		transfer_id			VARCHAR;
		transactions_id		VARCHAR;
    BEGIN
        SELECT bm_accounts_id INTO master_accounts_id  FROM tue4_bm_accounts WHERE bm_accounttype = 'CORP';
    		SELECT
    			SUM(prd.initial_tec) initial
    		INTO initial_balance
    		FROM tue4_pilot pil
    			JOIN orders ord 
    				ON user_id = pil.pil_usu_id 
    					AND status = 'APPROVED' 
    			JOIN orderitems ods 
    				ON ods.order_id = ord.id 
    			JOIN products prd 
    				ON prd.id = ods.product_id 
    					AND prd.initial_tec IS NOT NULL 
    		WHERE pil.pil_id = NEW.pil_id;
            
            IF initial_balance IS NULL THEN
                initial_balance = 0;
            END IF;
    		
    		SELECT replace(uuid_generate_v4()::"varchar",'-','') INTO pil_accounts_id;
            INSERT INTO tue4_bm_accounts
            (bm_accounts_id, pil_id, bm_accstatus, bm_accounttype, bm_accountbalance, created_at, updated_at)
            VALUES
            (pil_accounts_id, NEW.pil_id, 'OPEN', 'PILOT', 0, now(), now());
    		
    		SELECT replace(uuid_generate_v4()::"varchar",'-','') INTO transfer_id;
    		
    		SELECT replace(uuid_generate_v4()::"varchar",'-','') INTO transactions_id;
    		INSERT INTO tue4_bm_transactions
    		(bm_transactions_id, bm_transfer_id, bm_accounts_id, bm_amount, bm_paymentstatus, created_at, updated_at)
    		VALUES
    		(transactions_id, transfer_id, pil_accounts_id, initial_balance, 'COMPLETE', now(), now());
    		
    		SELECT replace(uuid_generate_v4()::"varchar",'-','') INTO transactions_id;
    		INSERT INTO tue4_bm_transactions
    		(bm_transactions_id, bm_transfer_id, bm_accounts_id, bm_amount, bm_paymentstatus, created_at, updated_at)
    		VALUES
    		(transactions_id, transfer_id, master_accounts_id, -initial_balance, 'COMPLETE', now(), now());
        
        RETURN NEW;
    END;
$BODY$
LANGUAGE 'plpgsql'
GO
