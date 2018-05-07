package com.tokenplay.ue4.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelationshipKey implements Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private String sourcePilotId;
    private String targetPilotId;
}
