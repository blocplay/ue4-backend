
CREATE OR REPLACE FUNCTION public.update_status () RETURNS trigger AS
$BODY$
    DECLARE
      balance NUMERIC;
      v_bm_accounts_id TEXT;
    BEGIN
    IF TG_OP = 'UPDATE' OR TG_OP = 'DELETE'  THEN
      v_bm_accounts_id = OLD.bm_accounts_id;
    ELSE
      v_bm_accounts_id = NEW.bm_accounts_id;
    END IF;
  
    SELECT SUM(bm_amount) INTO balance FROM tue4_bm_transactions
    WHERE bm_accounts_id = v_bm_accounts_id;

    IF balance IS NULL THEN
        balance = 0;
    END IF;

    UPDATE tue4_bm_accounts
    SET bm_accountbalance = balance
    WHERE bm_accounts_id = v_bm_accounts_id;
        RETURN NEW;
    END;
$BODY$
LANGUAGE 'plpgsql'
GO
