
CREATE OR REPLACE FUNCTION public.initialise_equipment () RETURNS trigger AS
$BODY$
    DECLARE
        initial_equipment   RECORD;
    BEGIN
        FOR initial_equipment IN
            SELECT ord.id
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
                    prd.initial_tec IS NOT NULL
                    AND ord.status = 'APPROVED'
                    AND pil_id = NEW.pil_id
            UNION
            SELECT ple.id
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
                    AND pil_id = NEW.pil_id
        LOOP
            PERFORM initialise_equipment_from_order(
                initial_equipment.id
                , NEW.pil_id
                , initial_equipment.leq_id
                , initial_equipment.lge_gem_id
                , initial_equipment.extra_id
                , initial_equipment.eqa_quantity::integer
            );
        END LOOP;
        
        RETURN NEW;
    END;
$BODY$
LANGUAGE 'plpgsql'
GO
