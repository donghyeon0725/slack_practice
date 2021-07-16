package com.slack.slack.domain.card;

import com.slack.slack.appConfig.security.JwtTokenProvider;
import com.slack.slack.domain.board.Board;
import com.slack.slack.domain.board.BoardRepository;
import com.slack.slack.domain.common.BaseCreateEntity;
import com.slack.slack.domain.common.BaseModifyEntity;
import com.slack.slack.domain.common.SuccessAuthentication;
import com.slack.slack.domain.team.*;
import com.slack.slack.domain.user.User;
import com.slack.slack.domain.user.UserRepository;
import com.slack.slack.error.exception.*;
import com.slack.slack.file.FileManager;
import com.slack.slack.file.FileVO;
import com.slack.slack.listener.event.card.CardAddEvent;
import com.slack.slack.listener.event.card.CardDeleteEvent;
import com.slack.slack.listener.event.card.CardRefreshEvent;
import com.slack.slack.listener.event.card.CardUpdateEvent;
import com.slack.slack.listener.event.file.FileEvent;
import com.slack.slack.socket.updater.CardUpdater;
import com.slack.slack.system.Activity;
import com.slack.slack.system.State;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CardServiceImpl implements  CardService{

    private final CardRepository cardRepository;

    private final TeamMemberRepository teamMemberRepository;

    private final BoardRepository boardRepository;

    private final UserRepository userRepository;

    private final AttachmentRepository attachmentRepository;

    private final TeamActivityRepository teamActivityRepository;

    private final FileManager fileManager;

    private final ApplicationContext applicationContext;

    private final ReplyRepository replyRepository;

    private final ModelMapper modelMapper;


    /**
     * 카드 생성하기
     * */
    @Override
    @Transactional
    public Card create(HttpServletRequest request, CardDTO cardDTO) {
        List<FileVO> files = null;

        try {
            User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                    .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            Board board = boardRepository.findById(cardDTO.getBoardId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            Team team = board.getTeam();

            TeamMember teamMember = teamMemberRepository.findByTeamAndUser(team, user)
                    .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            List<Card> cards = cardRepository.findByBoard(board).get();

            Integer maxPosition = cards.stream().max(Comparator.comparingInt(Card::getPosition)).orElse(Card.builder().position(0).build()).getPosition();


            Card card = cardRepository.save(
                    Card.builder()
                            .position(maxPosition+1)
                            .board(board)
                            .teamMember(teamMember)
                            .title(cardDTO.getTitle())
                            .content(cardDTO.getContent())
                            .date(new Date())
                            .state(State.CREATED)
                            .baseCreateEntity(BaseCreateEntity.now(user.getEmail()))
                            .build()
            );

            files = fileManager.fileUpload(request);

            files.forEach(s -> {
                card.getAttachments().add(

                    Attachment.builder()
                        .attachedFile(
                                AttachedFile.builder()
                                        .filename(s.getFileName())
                                        .size(s.getFileSize())
                                        .path(s.getPath())
                                        .extension(s.getExt())
                                        .systemFilename(s.getSystemName()).build()
                        )
                        .state(State.CREATED)
                        .card(card)
                        .date(new Date())
                        .baseCreateEntity(BaseCreateEntity.now(user.getEmail()))
                        .build()
                );
            });

            teamActivityRepository.save(
                    TeamActivity.builder()
                    .card(card)
                    .teamMember(teamMember)
                    .date(new Date())
                    .board(board)
                    .detail(Activity.CARD_CREATED)
                    .build()
            );

            // 웹 소켓 통신
            applicationContext.publishEvent(new CardAddEvent(team, card));

            return card;

        } catch (RuntimeException e) {
            // 중간에 에러가 난 경우 파일 삭제처리하기
            applicationContext.publishEvent(new FileEvent(files));

            throw e;
        }
    }

    @Override
    @Transactional
    public Card delete(CardDTO cardDTO) {

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Card card = cardRepository.findById(cardDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = card.getBoard().getTeam();

        teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE));

        // card 에서 attachment 를 삭제하고 해당 크기만큼 반복해서 fileManager 를 호출하는 방향으로
        List<Attachment> attachments = card.getAttachments();

        fileManager.deleteFile(
            attachments
                .stream()
                .map(s->
                        FileVO.builder()
                        .absolutePath(s.getAttachedFile().absolutePath()).build()
                )
                .collect(Collectors.toList())
        );

        user.delete(card);

        applicationContext.publishEvent(new CardDeleteEvent(team, card));

        return card;
    }

    @Override
    @Transactional
    public List<CardReturnDTO> retrieveCards(Integer boardId)
            throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = board.getTeam();

        TeamMember member = teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE));

        List<Card> cards = cardRepository.findByBoard(board).get();

        return cards.stream()
                .map(s -> {
                    State state = null;
                    if (s.getTeamMember().getId().equals(member.getId()))
                        state = State.CARD_CREATOR;
                    else if (s.getTeamMember().getId().equals(board.getTeamMember().getId()))
                        state = State.BOARD_CREATOR;
                    else if (s.getTeamMember().getUser().getId().equals(team.getUser().getId()))
                        state = State.CREATOR;
                    else
                        state = State.NO_AUTH;

                    CardReturnDTO returnDTO = modelMapper.map(s, CardReturnDTO.class);
                    returnDTO.setState(state);

                    return returnDTO;
                    }
                )
                // 정렬해서 보내주는 것으로 데이터 변경
                .sorted(Comparator.comparingInt(CardReturnDTO::getPosition))
                .collect(Collectors.toList());
            //.stream().filter(s->!s.getState().equals(State.DELETED)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Card updateCard(HttpServletRequest request, CardDTO cardDTO)
            throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        List<FileVO> files = null;

        try {
            User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                    .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            Card card = cardRepository.findById(cardDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            Board board = card.getBoard();

            files = fileManager.fileUpload(request);

            if (files.size() > 0) {
                // 기존 카드의 attacment 모두 삭제
                fileManager.deleteFile(
                        card.getAttachments()
                                .stream()
                                .map(s-> FileVO.builder().absolutePath(s.getAttachedFile().absolutePath()).build()
                                )
                                .collect(Collectors.toList())
                );
                card.getAttachments().clear();

                // 새로운 파일 추가
                files.forEach(s -> {
                    card.getAttachments().add(
                            Attachment.builder()
                                    .attachedFile(
                                            AttachedFile.builder()
                                                    .filename(s.getFileName())
                                                    .size(s.getFileSize())
                                                    .path(s.getPath())
                                                    .extension(s.getExt())
                                                    .systemFilename(s.getSystemName()).build()
                                    )
                                    .state(State.CREATED)
                                    .card(card)
                                    .date(new Date())
                                    .baseCreateEntity(BaseCreateEntity.now(user.getEmail()))
                                    .build()
                    );
                });

            }

            user.update(card, cardDTO);

            applicationContext.publishEvent(new CardUpdateEvent(board.getTeam(), card));

            return card;

        } catch (RuntimeException e) {
            // 중간에 에러가 난 경우 파일 삭제처리하기
            applicationContext.publishEvent(new FileEvent(files));

            throw e;
        }
    }

    @Override
    @Transactional
    public List<Card> updateCardPosition(List<CardDTO> cardDTOList)
            throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        if (cardDTOList == null || cardDTOList.size() <= 0)
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Card card = cardRepository.findById(cardDTOList.get(0).getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Board board = card.getBoard();

        Team team = board.getTeam();

        TeamMember member = teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE));


        // 카드나 보드 생성자만 권한이 있습니다.
        if (!board.getTeamMember().getId().equals(member.getId()) && !team.getUser().getId().equals(user.getId()))
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        List<Card> cards = cardRepository.findByIdIn(
            cardDTOList
                .stream()
                .map(s->s.getId())
                .collect(Collectors.toList())
        ).get();

        // client 에서 카드 값을 잘못 명시한 경우
        if (cards.size() != cardDTOList.size())
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        // 포지션 변경을 위해 id 순서로 정렬
        cards = cards.stream().sorted(Comparator.comparingInt(Card::getId)).collect(Collectors.toList());
        // 변경하려는 카드 id 순서로 정렬
        final List<CardDTO> finalCardDTOList = cardDTOList.stream().sorted(Comparator.comparingInt(CardDTO::getId)).collect(Collectors.toList());

        AtomicInteger index = new AtomicInteger();
        cards.forEach(s -> {
            s.changePosition(finalCardDTOList.get(index.getAndIncrement()).getPosition());
        });

        return cards;
    }


    @Override
    @Transactional
    public List<Attachment> fileUpload(HttpServletRequest request, CardDTO cardDTO)
            throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {
        List<FileVO> files = null;

        try {

            User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                    .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            Board board = boardRepository.findById(cardDTO.getBoardId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            teamMemberRepository.findByTeamAndUser(board.getTeam(), user)
                    .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE));

            Card card = cardRepository.findById(cardDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            files = fileManager.fileUpload(request);

            files.forEach(s -> {
                card.getAttachments().add(
                        Attachment.builder()
                            .attachedFile(
                                    AttachedFile.builder()
                                        .filename(s.getFileName())
                                        .size(s.getFileSize())
                                        .path(s.getPath())
                                        .extension(s.getExt())
                                        .systemFilename(s.getSystemName()).build()
                            )
                            .state(State.CREATED)
                            .card(card)
                            .date(new Date())
                            .baseCreateEntity(BaseCreateEntity.now(user.getEmail()))
                            .build()
                );
            });

            return card.getAttachments();
        } catch (RuntimeException e) {
            applicationContext.publishEvent(new FileEvent(files));

            throw e;
        }
    }

    @Override
    @Transactional
    public Attachment deleteFile(AttachmentDTO attachmentDTO)
            throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        Attachment attachment = attachmentRepository.findById(attachmentDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Board board = boardRepository.findById(attachment.getCard().getBoard().getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        teamMemberRepository.findByTeamAndUser(board.getTeam(), user)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE));

        fileManager.deleteFile(Arrays.asList(
                FileVO.builder()
                .absolutePath(attachment.getAttachedFile().absolutePath())
                .build()
        ));

        return user.delete(attachment);
    }

    @Override
    @Transactional
    public Reply updateCardReply(ReplyDTO replyDTO)
            throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Card card = cardRepository.findById(replyDTO.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = card.getBoard().getTeam();

        teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE));

        Reply reply = replyRepository.findById(replyDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        return user.update(reply, replyDTO);
    }

    @Override
    @Transactional
    public Reply createCardReply(ReplyDTO replyDTO)
            throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Card card = cardRepository.findById(replyDTO.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = card.getBoard().getTeam();

        TeamMember teamMember = teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE));

        Reply reply = Reply.builder()
                .card(card)
                .content(replyDTO.getContent())
                .date(new Date())
                .teamMember(teamMember)
                .baseCreateEntity(BaseCreateEntity.now(user.getEmail()))
                .build();

        card.getReplies().add(reply);

        return reply;
    }

    @Override
    @Transactional
    public Reply deleteReply(ReplyDTO replyDTO)
            throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Reply reply = replyRepository.findById(replyDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Card card = reply.getCard();
        Team team = card.getBoard().getTeam();

        teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE));

        card.getReplies().remove(reply);

        return reply;
    }
}
