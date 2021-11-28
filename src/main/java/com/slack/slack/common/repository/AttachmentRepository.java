package com.slack.slack.common.repository;

import com.slack.slack.common.entity.Attachment;
import com.slack.slack.common.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
    Optional<List<Attachment>> findByCard(Card card);
}
