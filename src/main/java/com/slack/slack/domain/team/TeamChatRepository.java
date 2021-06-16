package com.slack.slack.domain.team;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface TeamChatRepository extends JpaRepository<TeamChat, Integer> {
    @Query(
        value = "SELECT * FROM team_chat c JOIN team t ON c.team_id = t.id WHERE t.id = ?1 order by c.id DESC"
        , countQuery = "SELECT COUNT(*) FROM team_chat c JOIN team t ON c.team_id = t.id WHERE t.id = ?1 order by c.id DESC"
        , nativeQuery=true
    )
    List<TeamChat> findAllByTeamOrderByIdDesc(Integer teamId, Pageable pageable);


    @Query(
        value = "SELECT * FROM team_chat c JOIN team t ON c.team_id = t.id WHERE t.id = ?1 order by c.id DESC"
        , countQuery = "SELECT COUNT(*) FROM team_chat c JOIN team t ON c.team_id = t.id WHERE t.id = ?1 order by c.id DESC"
        , nativeQuery=true
    )
    List<TeamChat> findByTeamWhereIdLessThanOrderByIdDesc(Integer teamId, Integer ChatId, Pageable pageable);

    Boolean existsByIdLessThan(Integer id);
}
