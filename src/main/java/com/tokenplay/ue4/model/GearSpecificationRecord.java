package com.tokenplay.ue4.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.tokenplay.ue4.model.db.tables.pojos.GearInstance;
import com.tokenplay.ue4.model.db.tables.pojos.GearModel;
import com.tokenplay.ue4.model.db.tables.records.GearInstanceRecord;
import com.tokenplay.ue4.model.db.tables.records.GearModelRecord;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GearSpecificationRecord implements Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private GearInstanceRecord instance;
    private GearModelRecord model;

    public GearSpecification getSpec() {
        return new GearSpecification(instance.into(GearInstance.class), model.into(GearModel.class));
    }
}
