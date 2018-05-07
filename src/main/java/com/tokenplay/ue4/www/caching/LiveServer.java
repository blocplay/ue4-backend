package com.tokenplay.ue4.www.caching;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.tokenplay.ue4.model.db.tables.pojos.Server;

@Data
@AllArgsConstructor
public class LiveServer implements Serializable {
    private static final long serialVersionUID = 1L;

    private Server server;

    private LocalDateTime lastUpdate;
}
