package com.slack.slack.domain.service.impl;

import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.code.Status;
import com.slack.slack.common.dto.card.AttachmentCommand;
import com.slack.slack.common.dto.card.CardCommand;
import com.slack.slack.common.dto.card.CardDTO;
import com.slack.slack.common.dto.card.ReplyCommand;
import com.slack.slack.common.embedded.AttachedFile;
import com.slack.slack.common.entity.*;
import com.slack.slack.common.entity.validator.CardValidator;
import com.slack.slack.common.entity.validator.TeamValidator;
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

    private final TeamValidator teamValidator;

    /**
     * 카드 생성하기
     * */
    @Override
    @Transactional
    public Integer create(HttpServletRequest request, Integer boardId, CardCommand cardCommand) {
        List<FileVO> files = null;

        try {
            User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                    .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            Team team = board.getTeam();

            TeamMember teamMember = teamMemberRepository.findByTeamAndUser(team, user)
                    .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE));

            List<Card> cards = cardRepository.findByBoard(board);

            Integer maxPosition = cards.stream().max(Comparator.comparingInt(Card::getPosition)).orElse(Card.builder().position(0).build()).getPosition();

            Card card = Card.builder()
                    .position(maxPosition + 1)
                    .board(board)
                    .teamMember(teamMember)
                    .name(cardCommand.getName())
                    .content(cardCommand.getContent())
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
    public Integer delete(CardCommand cardCommand) {

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Card card = cardRepository.findById(cardCommand.getCardId())
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
    public List<CardDTO> retrieveCards(Integer boardId) {

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = board.getTeam();

        TeamMember member = teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE));

        List<Card> cards = cardRepository.findByBoard(board);

        return cards.stream()
                .map(s -> {
                            CardDTO returnDTO = new CardDTO(s);

                            Status status = null;
                            if (s.getTeamMember().getTeamMemberId().equals(member.getTeamMemberId()))
                                status = Status.CARD_CREATOR;
                            else if (s.getTeamMember().getTeamMemberId().equals(board.getTeamMember().getTeamMemberId()))
                                status = Status.BOARD_CREATOR;
                            else if (s.getTeamMember().getUser().getUserId().equals(team.getUser().getUserId()))
                                status = Status.CREATOR;
                            else
                                status = Status.NO_AUTH;

                            returnDTO.setStatus(status);

                            return returnDTO;
                        }
                )
                // 정렬해서 보내주는 것으로 데이터 변경
                .sorted(Comparator.comparingInt(CardDTO::getPosition))
                .collect(Collectors.toList());
        //.stream().filter(s->!s.getState().equals(State.DELETED)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Integer updateCard(HttpServletRequest request, CardCommand cardCommand) {

        List<FileVO> files = null;

        try {
            User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                    .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            Card card = cardRepository.findById(cardCommand.getCardId())
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

            user.update(card, cardCommand, cardValidator);

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
    public void updateCardPosition(Integer boardId, List<CardCommand> cardCommandList) {

        if (cardCommandList == null || cardCommandList.size() <= 0)
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = board.getTeam();

        cardValidator.checkTeamOwnerOrBoardOwner(team, board, user);

        List<Card> cards = cardRepository.findByCardIdIn(
                cardCommandList
                        .stream()
                        .map(s->s.getCardId())
                        .collect(Collectors.toList())
        );

        // client 에서 카드 값을 잘못 명시한 경우
        if (cards.size() != cardCommandList.size())
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        // 포지션 변경을 위해 id 순서로 정렬
        cards = cards.stream().sorted(Comparator.comparingInt(Card::getCardId)).collect(Collectors.toList());
        // 변경하려는 카드 id 순서로 정렬
        final List<CardCommand> finalCardCommandList = cardCommandList.stream().sorted(Comparator.comparingInt(CardCommand::getCardId)).collect(Collectors.toList());

        AtomicInteger index = new AtomicInteger();
        cards.forEach(s -> {
            s.changePosition(finalCardCommandList.get(index.getAndIncrement()).getPosition());
        });

        applicationContext.publishEvent(new CardRefreshEvent(board.getTeam()));
    }


    @Override
    @Transactional
    public void fileUpload(HttpServletRequest request, Integer boardId, Integer cardId) {
        List<FileVO> files = null;

        try {

            User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                    .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            teamValidator.checkTeamMember(board.getTeam(), user);

            Card card = cardRepository.findById(cardId)
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

        } catch (RuntimeException e) {
            applicationContext.publishEvent(new FileEvent(files));

            throw e;
        }
    }

    @Override
    @Transactional
    public Integer deleteFile(Integer boardId, AttachmentCommand attachmentCommand) {

        Attachment attachment = attachmentRepository.findById(attachmentCommand.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Board board = boardRepository.findById(boardId)
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
    public Integer updateCardReply(ReplyCommand replyCommand) {

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Card card = cardRepository.findById(replyCommand.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = card.getBoard().getTeam();

        teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE));

        Reply reply = replyRepository.findById(replyCommand.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        user.update(reply, replyCommand);

        return reply.getReplyId();
    }

    @Override
    @Transactional
    public Integer createCardReply(ReplyCommand replyCommand) {

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Card card = cardRepository.findById(replyCommand.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = card.getBoard().getTeam();

        TeamMember teamMember = teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE));

        Reply reply = Reply.builder()
                .card(card)
                .content(replyCommand.getContent())
                .date(new Date())
                .teamMember(teamMember)
                .baseCreateEntity(BaseCreateEntity.now(user.getEmail()))
                .build();

        card.getReplies().add(reply);

        return reply.getReplyId();
    }

    @Override
    @Transactional
    public Integer deleteReply(Integer replyId) {

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Card card = reply.getCard();
        Team team = card.getBoard().getTeam();

        teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE));

        card.getReplies().remove(reply);

        return reply.getReplyId();
    }
}
