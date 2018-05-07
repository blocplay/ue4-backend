
CREATE OR REPLACE FUNCTION public.initialise_asset () RETURNS trigger AS
$BODY$
    DECLARE
        v_gear      RECORD;
        extra_info  int8;
    BEGIN
        SELECT COUNT(*) INTO extra_info FROM tue4_gear_instance where gei_pil_id = NEW.pil_id;
        
        SELECT lge_gem_id
        INTO v_gear
        FROM tue4_lore_equipment
        INNER JOIN tue4_lore_gears
            ON leq_lge_id = lge_id
        INNER JOIN tue4_gear_model
            ON gem_id = lge_gem_id
        WHERE leq_id = NEW.bm_default_id;
        IF v_gear IS NOT NULL THEN
            PERFORM initialise_gear (NEW.pil_id, v_gear.lge_gem_id, NEW.bm_asset_id, ' - ' || extra_info::text);
        END IF;        
        RETURN NEW;
    END;
$BODY$
LANGUAGE 'plpgsql'
GO
