
CREATE OR REPLACE FUNCTION initialise_inventory_object (in pilot_id varchar, in equipment_id varchar) RETURNS VARCHAR AS
$BODY$
    DECLARE
    asset_id            VARCHAR;
    v_leq_id              VARCHAR;
    ini_id              VARCHAR;
    BEGIN
            // Get inventory objects
            SELECT leq_id INTO v_leq_id FROM tue4_lore_equipment WHERE leq_ino_id = equipment_id;
            RAISE NOTICE 'Equipment for inventory objects is % -> %', v_leq_id, equipment_id;
            // Generate and insert asset
            SELECT replace(uuid_generate_v4()::"varchar",'-','') INTO asset_id;
            INSERT
            INTO
                tue4_bm_assets(bm_asset_id
                , pil_id
                , bm_default_id
                , created_at
                , updated_at) 
            VALUES
                (
                    asset_id
                    , pilot_id
                    , v_leq_id
                    , NOW()
                    , NOW()
                );
            // Generate and insert inventory instance
            SELECT replace(uuid_generate_v4()::"varchar",'-','') INTO ini_id;
            INSERT
            INTO
                tue4_inventory_instance(ini_id
                , ini_ino_id
                , ini_pil_id) 
            VALUES
                (
                    ini_id
                    , equipment_id
                    , pilot_id
                );
            RETURN ini_id;
    END;
$BODY$
LANGUAGE 'plpgsql'
GO
