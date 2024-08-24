package com.tartayadir.cryptoservice.repository;

import com.tartayadir.cryptoservice.domain.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Override
    Optional<Message> findById(Long id);

    @Override
    <S extends Message> S save(S entity);

    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.encryptedMessage = :encryptedMessage, m.retries = :retries WHERE m.id = :id")
    int updateMessage(Long id, String encryptedMessage, int retries);

    @Override
    void deleteById(Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Message m WHERE m.createTime < :cutoffTime")
    void deleteOldMessages(LocalDateTime cutoffTime);
}