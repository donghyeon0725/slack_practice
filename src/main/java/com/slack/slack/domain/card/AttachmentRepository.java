package com.slack.slack.domain.card;

import com.slack.slack.domain.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
    Optional<List<Attachment>> findByCard(Card card);
}
