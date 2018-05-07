
CREATE OR REPLACE FUNCTION public.reset_pilot_equipment (in p_pil_id varchar) RETURNS void AS
$BODY$
    DECLARE
        initial_equipment RECORD;
        alpha_player RECORD;
        orderitem_id int8;
        order_id int8;
    BEGIN
        
        DELETE FROM tue4_bm_assets WHERE pil_id = p_pil_id AND ord_id IS NOT NULL;

        DELETE FROM tue4_gear_instance WHERE gei_pil_id = p_pil_id AND gei_bas_id IS NOT NULL AND gei_bas_id NOT IN (SELECT bm_asset_id FROM tue4_bm_assets);
        
        FOR initial_equipment IN
            SELECT ord.id
                    , pil_id
                    , eqa_quantity
                    , leq_id
                    , leq_ino_id
                    , leq_lge_id
                    , (' - ' || oit.id) extra_id
                    , lge_gem_id
            FROM orders ord
                INNER JOIN orderitems oit
                    ON oit.order_id = ord.id
                INNER JOIN products prd
                    ON prd.id = oit.product_id
                INNER JOIN users usu
                    ON usu.id = ord.user_id
                INNER JOIN tue4_pilot pil
                    ON pil.pil_usu_id = usu.id
                INNER JOIN tue4_equipment_awarded eqa
                    ON eqa_product_id = prd.id
                INNER JOIN tue4_lore_equipment leq
                    ON leq_id = eqa_awarded_leq_id
                LEFT JOIN tue4_lore_gears
                    ON lge_id = leq_lge_id
                WHERE
                    ord.status = 'APPROVED'
                    AND pil.pil_id = p_pil_id
            UNION
            SELECT ple.id
                    , pil_id
                    , pla_quantity
                    , leq_id
                    , leq_ino_id
                    , leq_lge_id
                    , (' - ' || ple.id) extra_id
                    , lge_gem_id
            FROM pledges ple
                INNER JOIN users usu
                    ON usu.id = ple.user_id
                INNER JOIN tue4_pilot pil
                    ON pil.pil_usu_id = usu.id
                INNER JOIN tue4_pledge_awarded eqa
                    ON pla_level_id = ple.level_id
                INNER JOIN tue4_lore_equipment leq
                    ON leq_id = pla_awarded_leq_id
                LEFT JOIN tue4_lore_gears
                    ON lge_id = leq_lge_id
                WHERE
                    ple.status = 'APPROVED'
                    AND pil.pil_id = p_pil_id
            UNION
            SELECT DISTINCT ple.id
                    , pil_id
                    , 1
                    , leq_id
                    , leq_ino_id
                    , leq_lge_id
                    , (' - ' || ple.id) extra_id
                    , lge_gem_id
            FROM pledges ple
                INNER JOIN users usu
                    ON usu.id = ple.user_id
                INNER JOIN tue4_pilot pil
                    ON pil.pil_usu_id = usu.id
                , (tue4_lore_equipment leq
                LEFT JOIN tue4_lore_gears
                    ON lge_id = leq_lge_id)
                WHERE
                    ple.status = 'APPROVED'
                    AND ple.level_id >= 12
                    AND pil.pil_id = p_pil_id
        LOOP
            PERFORM initialise_equipment_from_order(
                initial_equipment.id
                , initial_equipment.pil_id
                , initial_equipment.leq_id
                , initial_equipment.lge_gem_id
                , initial_equipment.extra_id
                , initial_equipment.eqa_quantity::integer
            );
        END LOOP;
    END;
$BODY$
LANGUAGE 'plpgsql'
GO
