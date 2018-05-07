
CREATE OR REPLACE FUNCTION public.initialise_equipment_from_order (in order_id int8, in pilot_id varchar, in equipment_id varchar, in gear_id varchar, in equipment_name varchar, in quantity int4) RETURNS void AS
$BODY$
    DECLARE
    asset_id            VARCHAR;
    new_gei_id          VARCHAR;
    extra_id            VARCHAR;
    BEGIN
        FOR i IN 1..quantity LOOP
            IF i > 1 THEN
                extra_id = (equipment_name || ' - ' || i::text);
            ELSE
                extra_id = equipment_name;
            END IF;
            SELECT replace(uuid_generate_v4()::"varchar",'-','') INTO asset_id;
            INSERT
            INTO
                tue4_bm_assets(bm_asset_id
                , ord_id
                , pil_id
                , bm_default_id
                , created_at
                , updated_at) 
            VALUES
                (
                    asset_id
                    , order_id
                    , pilot_id
                    , equipment_id
                    , NOW()
                    , NOW()
                );
                
        END LOOP;
    END;
$BODY$
LANGUAGE 'plpgsql'
GO
