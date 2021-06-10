package com.slack.slack.domain.card;

import com.slack.slack.domain.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {
    Optional<List<Card>> findByBoard(Board board);

    Optional<List<Card>> findByIdIn(List<Integer> id);
}
