package com.slack.slack.domain.service.impl;

import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.code.Status;
import com.slack.slack.common.dto.card.AttachmentDTO;
import com.slack.slack.common.dto.card.CardDTO;
import com.slack.slack.common.dto.card.CardReturnDTO;
import com.slack.slack.common.dto.card.ReplyDTO;
import com.slack.slack.common.embedded.AttachedFile;
import com.slack.slack.common.entity.*;
import com.slack.slack.common.entity.validator.CardValidator;
import com.slack.slack.common.repository.TeamMemberRepository;
import com.slack.slack.common.repository.AttachmentRepository;
import com.slack.slack.common.repository.CardRepository;
import com.slack.slack.domain.service.CardService;
import com.slack.slack.common.repository.ReplyRepository;
import com.slack.slack.common.repository.BoardRepository;
import com.slack.slack.common.util.SuccessAuthentication;
import com.slack.slack.common.entity.User;
import com.slack.slack.common.repository.UserRepository;
import com.slack.slack.common.exception.*;
import com.slack.slack.common.file.FileManager;
import com.slack.slack.common.file.FileVO;
import com.slack.slack.common.event.events.CardDeleteEvent;
import com.slack.slack.common.event.events.CardRefreshEvent;
import com.slack.slack.common.event.events.CardUpdateEvent;
import com.slack.slack.common.event.events.FileEvent;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    private final TeamMemberRepository teamMemberRepository;

    private final BoardRepository boardRepository;

    private final UserRepository userRepository;

    private final AttachmentRepository attachmentRepository;

    private final FileManager fileManager;

    private final ApplicationContext applicationContext;

    private final ReplyRepository replyRepository;

    private final ModelMapper modelMapper;

    private final CardValidator cardValidator;


    /**
     * 카드 생성하기
     * */
    @Override
    @Transactional
    public Integer create(HttpServletRequest request, CardDTO cardDTO) {
        List<FileVO> files = null;

        try {
            User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                    .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            Board board = boardRepository.findById(cardDTO.getBoardId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            Team team = board.getTeam();

            TeamMember teamMember = teamMemberRepository.findByTeamAndUser(team, user)
                    .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE));

            List<Card> cards = cardRepository.findByBoard(board).get();

            Integer maxPosition = cards.stream().max(Comparator.comparingInt(Card::getPosition)).orElse(Card.builder().position(0).build()).getPosition();

            Card card = Card.builder()
                    .position(maxPosition + 1)
                    .board(board)
                    .teamMember(teamMember)
                    .name(cardDTO.getName())
                    .content(cardDTO.getContent())
                    .date(new Date())
                    .baseCreateEntity(BaseCreateEntity.now(user.getEmail()))
                    .build();

            card.created();
            cardRepository.save(card);

            files = fileManager.fileUpload(request);

            files.forEach(s -> {
                AttachedFile attachedFile = AttachedFile.builder()
                        .filename(s.getFileName())
                        .size(s.getFileSize())
                        .path(s.getPath())
                        .extension(s.getExt())
                        .systemFilename(s.getSystemName()).build();

                Attachment attachment = Attachment.builder()
                        .attachedFile(attachedFile)
                        .status(Status.CREATED)
                        .card(card)
                        .date(new Date())
                        .baseCreateEntity(BaseCreateEntity.now(user.getEmail()))
                        .build();

                card.getAttachments().add(attachment);
            });

            return card.getCardId();
        } catch (RuntimeException e) {
            // 중간에 에러가 난 경우 파일 삭제처리하기
            applicationContext.publishEvent(new FileEvent(files));

            throw e;
        }
    }

    @Override
    @Transactional
    public Integer delete(CardDTO cardDTO) {

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Card card = cardRepository.findById(cardDTO.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = card.getBoard().getTeam();

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

        // 카드 소유자나 팀 소유자가 아니면 카드를 삭제할 수 없다.
        user.delete(card, cardValidator);

        applicationContext.publishEvent(new CardDeleteEvent(team, card));

        return card.getCardId();
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
                            Status status = null;
                            if (s.getTeamMember().getTeamMemberId().equals(member.getTeamMemberId()))
                                status = Status.CARD_CREATOR;
                            else if (s.getTeamMember().getTeamMemberId().equals(board.getTeamMember().getTeamMemberId()))
                                status = Status.BOARD_CREATOR;
                            else if (s.getTeamMember().getUser().getUserId().equals(team.getUser().getUserId()))
                                status = Status.CREATOR;
                            else
                                status = Status.NO_AUTH;

                            CardReturnDTO returnDTO = modelMapper.map(s, CardReturnDTO.class);
                            returnDTO.setStatus(status);

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
    public Integer updateCard(HttpServletRequest request, CardDTO cardDTO)
            throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        List<FileVO> files = null;

        try {
            User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                    .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            Card card = cardRepository.findById(cardDTO.getCardId())
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
                                    .status(Status.CREATED)
                                    .card(card)
                                    .date(new Date())
                                    .baseCreateEntity(BaseCreateEntity.now(user.getEmail()))
                                    .build()
                    );
                });

            }

            user.update(card, cardDTO);

            applicationContext.publishEvent(new CardUpdateEvent(board.getTeam(), card));

            return card.getCardId();

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

        Card card = cardRepository.findById(cardDTOList.get(0).getCardId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Board board = card.getBoard();

        Team team = board.getTeam();

        TeamMember member = teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE));


        // 카드나 보드 생성자만 권한이 있습니다.
        if (!board.getTeamMember().getTeamMemberId().equals(member.getTeamMemberId()) && !team.getUser().getUserId().equals(user.getUserId()))
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        List<Card> cards = cardRepository.findByCardIdIn(
                cardDTOList
                        .stream()
                        .map(s->s.getCardId())
                        .collect(Collectors.toList())
        ).get();

        // client 에서 카드 값을 잘못 명시한 경우
        if (cards.size() != cardDTOList.size())
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        // 포지션 변경을 위해 id 순서로 정렬
        cards = cards.stream().sorted(Comparator.comparingInt(Card::getCardId)).collect(Collectors.toList());
        // 변경하려는 카드 id 순서로 정렬
        final List<CardDTO> finalCardDTOList = cardDTOList.stream().sorted(Comparator.comparingInt(CardDTO::getCardId)).collect(Collectors.toList());

        AtomicInteger index = new AtomicInteger();
        cards.forEach(s -> {
            s.changePosition(finalCardDTOList.get(index.getAndIncrement()).getPosition());
        });

        applicationContext.publishEvent(new CardRefreshEvent(board.getTeam()));

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

            Card card = cardRepository.findById(cardDTO.getCardId())
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
                                .status(Status.CREATED)
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
    public Integer deleteFile(AttachmentDTO attachmentDTO)
            throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        Attachment attachment = attachmentRepository.findById(attachmentDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Board board = boardRepository.findById(attachment.getCard().getBoard().getBoardId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        teamMemberRepository.findByTeamAndUser(board.getTeam(), user)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE));

        fileManager.deleteFile(Arrays.asList(
                FileVO.builder()
                        .absolutePath(attachment.getAttachedFile().absolutePath())
                        .build()
        ));

        user.delete(attachment);

        return attachment.getAttachmentId();
    }

    @Override
    @Transactional
    public Integer updateCardReply(ReplyDTO replyDTO)
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

        user.update(reply, replyDTO);

        return reply.getReplyId();
    }

    @Override
    @Transactional
    public Integer createCardReply(ReplyDTO replyDTO)
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

        return reply.getReplyId();
    }

    @Override
    @Transactional
    public Integer deleteReply(ReplyDTO replyDTO)
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

        return reply.getReplyId();
    }
}
