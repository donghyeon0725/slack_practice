package com.slack.slack.common.repository;

import com.slack.slack.common.entity.Board;
import com.slack.slack.common.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {
    Optional<List<Card>> findByBoard(Board board);

    Optional<List<Card>> findByCardIdIn(List<Integer> id);
}
