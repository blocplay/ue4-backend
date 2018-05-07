package com.tokenplay.ue4.model.repositories;

import com.tokenplay.ue4.model.db.tables.records.MessageRecord;
import org.jooq.Result;

public interface MessagesDB {

    Result<MessageRecord> findAllNotDeliveredForRecipient(String recipientId);

}
