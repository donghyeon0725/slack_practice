package com.slack.slack.domain.service.impl;

import com.slack.slack.common.code.Roles;
import com.slack.slack.common.code.Status;
import com.slack.slack.common.dto.card.CardDTO;
import com.slack.slack.common.entity.*;
import com.slack.slack.common.exception.UnauthorizedException;
import com.slack.slack.common.file.FileManager;
import com.slack.slack.common.repository.*;
import com.slack.slack.domain.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import testModule.WithMockCustomUser;

import javax.persistence.EntityManager;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 실패한 케이스 위주로 작성
 */
// TODO test application.yml  만들기
@ActiveProfiles("test")
@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Autowired
    private CardService cardService;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private CardRepository cardRepository;

    private final String email = "email@test.com";

    private final String name = "유저";

    private final Date now = new Date();

    private final String password = "@roskwl3034";

    private final String otherEmail = "other@naver.com";

    private User teamOwner;

    private User teamUser;

    private TeamMember ownerTeamMember;

    private TeamMember userTeamMember;

    private Team team;

    private Board board;

    @BeforeEach
    public void setTeamMember() {
        teamOwner = createUser(email, name, password, Roles.ROLE_USER);
        teamUser = createUser("member@test.com", "멤버", password, Roles.ROLE_USER);
        team = createTeam(teamOwner, "굳팀", "이 팀은 매우 좋은 팀입니다.");

        userTeamMember = createTeamMember(team, teamUser);
        ownerTeamMember = createTeamMember(team, teamOwner);

        board = createBoard(team, ownerTeamMember, "개인보드", "개인 보드");
    }

    public User createUser(String email, String name, String password, Roles... roles) {
        User user = User.builder().email(email).name(name).password(passwordEncoder.encode(password)).status(Status.CREATED).build();

        for (Roles r : roles) {
            Role findRole = roleRepository.findByRoleName(r.getRole());

            if (findRole == null) {

                findRole = Role.builder().roleName(r.getRole()).build();
                roleRepository.save(findRole);
            }

            UserRole userRole = UserRole.builder().role(findRole).user(user).build();
            user.getUserRoles().add(userRole);
        }

        userRepository.save(user);

        em.flush();
        em.clear();

        return user;
    }

    public Team createTeam(User user, String teamName, String description) {

        Team team = Team.builder()
                .user(user)
                .name(teamName)
                .description(description)
                .status(Status.CREATED)
                .build();

        teamRepository.save(team);


        return team;
    }

    public Board createBoard(Team team, TeamMember teamMember, String name, String content) {
        Board board = Board.builder()
                .team(team)
                .status(Status.CREATED)
                .teamMember(teamMember)
                .name(name)
                .content(content)
                .build();

        boardRepository.save(board);

        return board;
    }

    public TeamMember createTeamMember(Team team, User user) {
        TeamMember teamMember = TeamMember.builder()
                .team(team)
                .user(user)
                .status(Status.CREATED)
                .build();

        teamMemberRepository.save(teamMember);
        em.flush();
        em.clear();

        return teamMember;
    }

    public Card createCard(Board board, TeamMember teamMember, String name, String content) {
        Card card = Card.builder()
                .board(board)
                .name(name)
                .content(content)
                .teamMember(teamMember)
                .status(Status.CREATED)
                .build();

        cardRepository.save(card);

        return card;
    }


    @Test
    @DisplayName("카드 생성 : 예외 case 1")
    @WithMockCustomUser(username = email, role = "ROLE_USER")
    public void createCardTestCase1(@Mock MultipartHttpServletRequest request) {
        // TODO 자신의 팀이 가진 보드에 글을 쓰는 것이 아닐 때 예외가 발생한다. teamMember

        // given
        User other = createUser(otherEmail, "다른유저", "kwdek@949", Roles.ROLE_USER);
        Team otherTeam = createTeam(other, "다른팀", "");
        TeamMember otherTeamMember = createTeamMember(otherTeam, other);
        Board otherBoard = createBoard(otherTeam, otherTeamMember, "다른보드", "내용");

        CardDTO cardDTO = CardDTO.builder()
                .content("콘텐츠 생성")
                .name("카드 생성")
                .boardId(otherBoard.getBoardId())
                .build();

        // when then
        assertThrows(UnauthorizedException.class, () -> cardService.create(request, cardDTO));
    }


    @Test
    @DisplayName("카드 생성 : 정상 case 1")
    @WithMockCustomUser(username = email, role = "ROLE_USER")
    public void createCardTestCase2(@Mock MultipartHttpServletRequest request, @Mock FileManager fileManager) {
        // TODO 정상 생성 될 때

        // given
        ReflectionTestUtils.setField(cardService, "fileManager", fileManager);
        CardDTO cardDTO = CardDTO.builder()
                .content("콘텐츠 생성")
                .name("카드 생성")
                .boardId(board.getBoardId())
                .build();


        // when
        Integer createdCardId = cardService.create(request, cardDTO);
        Card findCard = em.find(Card.class, createdCardId);


        // then
        assertNotNull(findCard);
        assertEquals(1, findCard.getPosition(), "처음 생성된 카드의 포지션은 1");
    }

    @Test
    @DisplayName("카드 삭제 : 예외 case 1")
    @WithMockCustomUser(username = email, role = "ROLE_USER")
    public void deleteCardTestCase1() {
        // TODO 자신의 팀이 가진 보드의 카드를 삭제하는 것이 아닐 때 예외

        // given
        User other = createUser(otherEmail, "다른유저", "kwdek@949", Roles.ROLE_USER);
        Team otherTeam = createTeam(other, "다른팀", "");
        TeamMember otherTeamMember = createTeamMember(otherTeam, other);

        Board otherBoard = createBoard(otherTeam, otherTeamMember, "다른보드", "내용");

        Card othersCard = createCard(otherBoard, otherTeamMember, "다른 보드에 쓴 카드", "ㅋ");

        CardDTO cardDTO = CardDTO.builder()
                .cardId(othersCard.getCardId())
                .build();

        // when then
        assertThrows(UnauthorizedException.class, () -> cardService.delete(cardDTO));
    }


    @Test
    @DisplayName("카드 삭제 : 예외 case 2")
    @WithMockCustomUser(username = otherEmail, role = "ROLE_USER")
    public void deleteCardTestCase2() {
        // TODO 자신의 팀이라고 해도, 자신의 소유 보드나 소유 팀 또는 카드가 아닌 경우 삭제할 수 없다.

        // given
        User other = createUser(otherEmail, "다른유저", "kwdek@949", Roles.ROLE_USER);
        createTeam(other, "다른팀", "");
        Card ownersCard = createCard(board, ownerTeamMember,"카드", "콘텐츠");

        CardDTO cardDTO = CardDTO.builder()
                .cardId(ownersCard.getCardId())
                .build();

        // when then
        assertThrows(UnauthorizedException.class, () -> cardService.delete(cardDTO));
    }


    @Test
    @DisplayName("카드 삭제 : 예외 case 3")
    @WithMockCustomUser(username = otherEmail, role = "ROLE_USER")
    public void deleteCardTestCase3() {
        // TODO 자신의 카드인 경우 삭제할 수 있다.

        // given
        User otherMember = createUser(otherEmail, "다른유저", "kwdek@949", Roles.ROLE_USER);
        TeamMember otherTeamMember = createTeamMember(team, otherMember);

        Card othersCard = createCard(board, otherTeamMember,"카드", "콘텐츠");

        CardDTO cardDTO = CardDTO.builder()
                .cardId(othersCard.getCardId())
                .build();

        // when
        Integer deletedCard = cardService.delete(cardDTO);
        Card findCard = em.find(Card.class, deletedCard);

        assertEquals(Status.DELETED, findCard.getStatus());
    }

    @Test
    @DisplayName("카드 삭제 : 예외 case 4")
    @WithMockCustomUser(username = otherEmail, role = "ROLE_USER")
    public void deleteCardTestCase4() {
        // TODO 자신이 보드 소유자인 경우 카드를 삭제할 수 있다.

        // given
        User boardOwner = createUser(otherEmail, "다른유저", "kwdek@949", Roles.ROLE_USER);
        TeamMember boardOwnerTeamMember = createTeamMember(team, boardOwner);
        Board board = createBoard(team, boardOwnerTeamMember, "test", "test");
        Card othersCard = createCard(board, ownerTeamMember,"카드", "콘텐츠");

        CardDTO cardDTO = CardDTO.builder()
                .cardId(othersCard.getCardId())
                .build();

        // when
        Integer deletedCard = cardService.delete(cardDTO);
        Card findCard = em.find(Card.class, deletedCard);

        assertEquals(Status.DELETED, findCard.getStatus());
    }

    @Test
    @DisplayName("카드 삭제 : 예외 case 5")
    @WithMockCustomUser(username = email, role = "ROLE_USER")
    public void deleteCardTestCase5() {
        // TODO 자신이 팀 소유자인 경우 카드를 삭제할 수 있다.

        // given
        User boardOwner = createUser(otherEmail, "다른유저", "kwdek@949", Roles.ROLE_USER);
        TeamMember boardOwnerTeamMember = createTeamMember(team, boardOwner);
        Board board = createBoard(team, boardOwnerTeamMember,"test", "test");
        Card othersCard = createCard(board, boardOwnerTeamMember,"카드", "콘텐츠");

        CardDTO cardDTO = CardDTO.builder()
                .cardId(othersCard.getCardId())
                .build();

        // when
        Integer deletedCard = cardService.delete(cardDTO);
        Card findCard = em.find(Card.class, deletedCard);

        assertEquals(Status.DELETED, findCard.getStatus());
    }





    /* 카드를 생성합니다. */
    /* 카드를 수정합니다. */
    /* 카드의 포지션을 수정합니다. */
    /* 카드를 삭제합니다. */
    /* 카드 리스트를 봅니다. */
    /* 파일 첨부하기 */
    /* 첨부 파일 삭제하기 */
    /* 댓글을 수정합니다. */
    /* 댓글 생성하기 */
    /* 댓글 삭제하기 */
}
