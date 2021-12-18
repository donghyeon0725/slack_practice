package com.slack.slack.common.repository;

import com.slack.slack.common.entity.TeamChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface TeamChatRepository extends JpaRepository<TeamChat, Integer> {
    @Query(
        value = "SELECT * FROM team_chat c JOIN team t ON c.team_id = t.team_id WHERE t.team_id = ?1 order by c.team_chat_id DESC"
        , countQuery = "SELECT COUNT(*) FROM team_chat c JOIN team t ON c.team_id = t.team_id WHERE t.team_id = ?1 order by c.team_chat_id DESC"
        , nativeQuery=true
    )
    List<TeamChat> findAllByTeamOrderByIdDesc(Integer teamId, Pageable pageable);


    @Query(
        value = "SELECT * FROM team_chat c JOIN team t ON c.team_id = t.team_id WHERE t.team_id = ?1 order by c.team_chat_id DESC"
        , countQuery = "SELECT COUNT(*) FROM team_chat c JOIN team t ON c.team_id = t.team_id WHERE t.team_id = ?1 order by c.team_chat_id DESC"
        , nativeQuery=true
    )
    List<TeamChat> findByTeamWhereIdLessThanOrderByIdDesc(Integer teamId, Integer ChatId, Pageable pageable);

    Boolean existsByTeamChatIdLessThan(Integer id);
}
