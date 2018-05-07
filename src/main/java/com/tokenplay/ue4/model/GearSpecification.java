package com.tokenplay.ue4.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.tokenplay.ue4.model.db.tables.pojos.GearInstance;
import com.tokenplay.ue4.model.db.tables.pojos.GearModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GearSpecification implements Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private GearInstance instance;
    private GearModel model;
}
