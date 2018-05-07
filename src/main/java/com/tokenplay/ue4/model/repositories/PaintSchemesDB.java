package com.tokenplay.ue4.model.repositories;

import java.util.List;

import com.tokenplay.ue4.model.db.tables.pojos.PaintScheme;
import com.tokenplay.ue4.model.db.tables.records.PaintSchemeRecord;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;

public interface PaintSchemesDB {
    List<PaintScheme> findAll(PilotRecord pilot);

    PaintSchemeRecord findById(String id);
}
