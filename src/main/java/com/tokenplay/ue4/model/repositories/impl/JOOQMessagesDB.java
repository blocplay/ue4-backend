package com.tokenplay.ue4.model.repositories.impl;

import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.model.db.tables.Tue4Message;
import com.tokenplay.ue4.model.db.tables.records.MessageRecord;
import com.tokenplay.ue4.model.repositories.MessagesDB;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional
@Slf4j
public class JOOQMessagesDB implements MessagesDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQMessagesDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<MessageRecord> findAllNotDeliveredForRecipient(String recipientId) {
        return jooq.selectFrom(Tue4Message.MESSAGE).where(Tue4Message.MESSAGE.MSG_RECIPIENT.eq(recipientId))
            .and(Tue4Message.MESSAGE.MSG_DELIVERED.eq(false)).fetch();
    }
}
