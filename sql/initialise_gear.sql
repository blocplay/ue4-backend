
CREATE OR REPLACE FUNCTION public.initialise_gear (in pilot_id varchar, in gear_id varchar, in asset_id varchar, in extra_id varchar) RETURNS void AS
$BODY$
    DECLARE
    new_gei_id                      VARCHAR;
    v_gem_name                        VARCHAR;
    default_loadout                 RECORD;
    v_lge_engine_heavy_mount_right  VARCHAR;
    v_lge_engine_heavy_mount_left   VARCHAR;
    v_lge_fuel_tank_storage         VARCHAR;
    v_lge_left_collar_mount         VARCHAR;
    v_lge_right_collar_mount        VARCHAR;
    v_lge_upper_leg_out_left        VARCHAR;
    v_lge_upper_leg_out_right       VARCHAR;
    BEGIN
        SELECT gem_name FROM tue4_gear_model WHERE gem_id = gear_id INTO v_gem_name;
        v_lge_engine_heavy_mount_right = null;
        v_lge_engine_heavy_mount_left  = null;
        v_lge_fuel_tank_storage        = null;
        v_lge_left_collar_mount        = null;
        v_lge_right_collar_mount       = null;
        v_lge_upper_leg_out_left       = null;
        v_lge_upper_leg_out_right      = null;
        
        SELECT
            lge_engine_heavy_mount_right,
            lge_engine_heavy_mount_left,
            lge_fuel_tank_storage,
            lge_left_collar_mount,
            lge_right_collar_mount,
            lge_upper_leg_out_left,
            lge_upper_leg_out_right
        INTO default_loadout
        FROM tue4_lore_gears
        WHERE lge_gem_id = gear_id;




























        
        SELECT replace(uuid_generate_v4()::"varchar",'-','') INTO new_gei_id;
        INSERT
        INTO
            tue4_gear_instance(
            gei_id
            , gei_gem_id
            , gei_pil_id
            , gei_bas_id
            , gei_name
            , gei_use_custom_scheme
            , ii_ges_engine_heavy_mount_right
            , ii_ges_engine_heavy_mount_left
            , ii_ges_fuel_tank_storage
            , ii_ges_left_collar_mount
            , ii_ges_right_collar_mount
            , ii_ges_upper_leg_out_left
            , ii_ges_upper_leg_out_right
            ) 
        VALUES
            (
                new_gei_id
                , gear_id
                , pilot_id
                , asset_id
                , v_gem_name || extra_id
                , TRUE
                , default_loadout.lge_engine_heavy_mount_right
                , default_loadout.lge_engine_heavy_mount_left
                , default_loadout.lge_fuel_tank_storage
                , default_loadout.lge_left_collar_mount
                , default_loadout.lge_right_collar_mount
                , default_loadout.lge_upper_leg_out_left
                , default_loadout.lge_upper_leg_out_right
            );
    END;
$BODY$
LANGUAGE 'plpgsql'
GO
