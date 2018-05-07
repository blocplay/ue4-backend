
CREATE OR REPLACE FUNCTION public.rollback_purchases () RETURNS void AS
$BODY$
  DECLARE
        to_rollback RECORD;
    BEGIN
    CREATE TEMPORARY TABLE rollback_table (bm_asset_id TEXT) ON COMMIT DROP;
    INSERT INTO rollback_table
      SELECT DISTINCT bm_asset_id 
      FROM
        tue4_bm_transactions
      WHERE
        bm_asset_id IS NOT NULL;
    DELETE FROM tue4_bm_transactions WHERE bm_asset_id IS NOT NULL;
    DELETE FROM tue4_bm_assets WHERE bm_asset_id IN (SELECT bm_asset_id FROM rollback_table);
    DELETE FROM tue4_gear_instance WHERE gei_bas_id NOT IN (SELECT bm_asset_id FROM tue4_bm_assets);
  END;
$BODY$
LANGUAGE 'plpgsql'
GO
