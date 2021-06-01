package com.slack.slack.domain.card;

import com.slack.slack.appConfig.security.JwtTokenProvider;
import com.slack.slack.domain.board.Board;
import com.slack.slack.domain.board.BoardDTO;
import com.slack.slack.domain.board.BoardRepository;
import com.slack.slack.domain.team.*;
import com.slack.slack.domain.user.User;
import com.slack.slack.domain.user.UserRepository;
import com.slack.slack.error.exception.*;
import com.slack.slack.event.FileEvent;
import com.slack.slack.event.FileEventHandler;
import com.slack.slack.file.FileManager;
import com.slack.slack.file.FileVO;
import com.slack.slack.system.Activity;
import com.slack.slack.system.State;
import lombok.RequiredArgsConstructor;
import org.omg.SendingContext.RunTime;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;
import java.util.stream.Collector;
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

    private final JwtTokenProvider jwtTokenProvider;

    private final FileManager fileManager;

    private final ApplicationContext applicationContext;

    private final TeamRepository teamRepository;

    private final ReplyRepository replyRepository;


    /**
     * 카드 생성하기
     * */
    @Override
    @Transactional
    public Card create(HttpServletRequest request, CardDTO cardDTO) {
        List<FileVO> files = null;

        try {
            TeamMember teamMember = teamMemberRepository.findById(cardDTO.getTeamMemberId())
                    .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            Board board = boardRepository.findById(cardDTO.getBoardId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            List<Card> cards = cardRepository.findByBoard(board)
                    .map(s->s.stream().filter(l->!l.getState().equals(State.DELETED)).collect(Collectors.toList()))
                    .orElse(new ArrayList<>());


            Card card = cardRepository.save(
                    Card.builder()
                            .position(cards.size()+1)
                            .board(board)
                            .teamMember(teamMember)
                            .title(cardDTO.getTitle())
                            .content(cardDTO.getContent())
                            .date(new Date())
                            .state(State.CREATED)
                            .build());

            files = fileManager.fileUpload(request);

            List<Attachment> attachments = new ArrayList<>();
            files.forEach(s -> {
                attachments.add(
                        Attachment.builder()
                        .filename(s.getFileName())
                        .size(s.getFileSize())
                        .state(State.CREATED)
                        .card(card)
                        .systemFilename(s.getSystemName())
                        .date(new Date())
                        .extension(s.getExt())
                        .path(s.getPath())
                        .build()
                );
            });

            attachmentRepository.saveAll(attachments);

            teamActivityRepository.save(
                    TeamActivity.builder()
                    .card(card)
                    .teamMember(teamMember)
                    .date(new Date())
                    .board(board)
                    .detail(Activity.CARD_CREATED)
                    .build()
            );

            return Card.builder()
                    .id(card.getId())
                    .position(card.getPosition())
                    .board(card.getBoard())
                    .attachments(attachments)
                    .teamMember(card.getTeamMember())
                    .title(card.getTitle())
                    .content(card.getContent())
                    .date(card.getDate())
                    .state(card.getState())
                    .build();

        } catch (RuntimeException e) {
            // 중간에 에러가 난 경우 파일 삭제처리하기
            applicationContext.publishEvent(new FileEvent(files));

            throw e;
        }
    }

    @Override
    public Card delete(String token, CardDTO cardDTO) {

        User user = userRepository.findByEmail(jwtTokenProvider.getUserPk(token))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Board board = boardRepository.findById(cardDTO.getBoardId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        List<TeamMember> members = teamMemberRepository.findByUser_Id(user.getId())
                .map(s -> s.stream()
                        // 리스트에서 팀 아이디가 같은지 추출
                        .filter(l -> l.getTeam().getId().intValue() == board.getTeam().getId())
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());

        if (members.size() <= 0)
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);


        Card card = cardRepository.findById(cardDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));


        return cardRepository.save(
                Card.builder()
                        .id(card.getId())
                        .position(cardDTO.getPosition())
                        .board(card.getBoard())
                        .teamMember(card.getTeamMember())
                        .title(card.getTitle())
                        .content(card.getContent())
                        .date(card.getDate())
                        .state(State.DELETED)
                        .build());
    }

    @Override
    public List<Card> retrieveCards(String token, Integer boardId)
            throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        User user = userRepository.findByEmail(jwtTokenProvider.getUserPk(token))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        List<TeamMember> members = teamMemberRepository.findByUser_Id(user.getId())
                .map( s -> s.stream()
                        // 리스트에서 팀 아이디가 같은지 추출
                        .filter(l -> l.getTeam().getId().intValue() == board.getTeam().getId())
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());

        if (members.size() <= 0)
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        return cardRepository.findByBoard(board)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
            //.stream().filter(s->!s.getState().equals(State.DELETED)).collect(Collectors.toList());
    }

    @Override
    public Card updateCard(HttpServletRequest request, String token, CardDTO cardDTO)
            throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        List<FileVO> files = null;

        try {
            TeamMember teamMember = teamMemberRepository.findById(cardDTO.getTeamMemberId())
                    .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));


            User user = userRepository.findByEmail(jwtTokenProvider.getUserPk(token))
                    .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            Board board = boardRepository.findById(cardDTO.getBoardId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            List<TeamMember> members = teamMemberRepository.findByUser_Id(user.getId())
                    .map(s -> s.stream()
                            // 리스트에서 팀 아이디가 같은지 추출
                            .filter(l -> l.getTeam().getId().intValue() == board.getTeam().getId())
                            .collect(Collectors.toList()))
                    .orElse(new ArrayList<>());

            if (members.size() <= 0)
                throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

            Card card = cardRepository.findById(cardDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));


            files = fileManager.fileUpload(request);

            List<Attachment> attachments = new ArrayList<>();
            files.forEach(s -> {
                attachments.add(
                        Attachment.builder()
                                .filename(s.getFileName())
                                .size(s.getFileSize())
                                .state(State.CREATED)
                                .card(card)
                                .systemFilename(s.getSystemName())
                                .date(new Date())
                                .extension(s.getExt())
                                .path(s.getPath())
                                .build()
                );
            });

            if (attachments.size() > 0)
                attachmentRepository.saveAll(attachments);

            return cardRepository.save(
                    Card.builder()
                            .id(card.getId())
                            .position(cardDTO.getPosition())
                            .board(board)
                            .teamMember(teamMember)
                            .title(cardDTO.getTitle())
                            .content(cardDTO.getContent())
                            .date(card.getDate())
                            .state(State.UPDATED)
                            .build());

        } catch (RuntimeException e) {
            // 중간에 에러가 난 경우 파일 삭제처리하기
            applicationContext.publishEvent(new FileEvent(files));

            throw e;
        }
    }


    @Override
    public List<Attachment> fileUpload(HttpServletRequest request, String token, CardDTO cardDTO)
            throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {
        List<FileVO> files = null;

        try {

            User user = userRepository.findByEmail(jwtTokenProvider.getUserPk(token))
                    .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            Board board = boardRepository.findById(cardDTO.getBoardId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            List<TeamMember> members = teamMemberRepository.findByUser_Id(user.getId())
                    .map(s -> s.stream()
                            // 리스트에서 팀 아이디가 같은지 추출
                            .filter(l -> l.getTeam().getId().intValue() == board.getTeam().getId())
                            .collect(Collectors.toList()))
                    .orElse(new ArrayList<>());

            if (members.size() <= 0)
                throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

            /* 비지니스 로직 시작 */
            Card card = cardRepository.findById(cardDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

            files = fileManager.fileUpload(request);

            List<Attachment> attachments = new ArrayList<>();
            files.forEach(s -> {
                attachments.add(
                        Attachment.builder()
                                .filename(s.getFileName())
                                .size(s.getFileSize())
                                .state(State.CREATED)
                                .card(card)
                                .systemFilename(s.getSystemName())
                                .date(new Date())
                                .extension(s.getExt())
                                .path(s.getPath())
                                .build()
                );
            });

            return attachmentRepository.saveAll(attachments);
        } catch (RuntimeException e) {
            applicationContext.publishEvent(new FileEvent(files));

            throw e;
        }
    }

    @Override
    public Attachment deleteFile(String token, AttachmentDTO attachmentDTO)
            throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        Attachment attachment = attachmentRepository.findById(attachmentDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        User user = userRepository.findByEmail(jwtTokenProvider.getUserPk(token))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Board board = boardRepository.findById(attachment.getCard().getBoard().getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));


        List<TeamMember> members = teamMemberRepository.findByUser_Id(user.getId())
                .map(s -> s.stream()
                        // 리스트에서 팀 아이디가 같은지 추출
                        .filter(l -> l.getTeam().getId().intValue() == board.getTeam().getId())
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());

        if (members.size() <= 0)
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        fileManager.deleteFile(Arrays.asList(
                FileVO.builder()
                .absolutePath(attachment.getPath() + File.separator + attachment.getSystemFilename())
                .build()
        ));

        return attachmentRepository.save(
                Attachment.builder()
                .id(attachment.getId())
                .card(attachment.getCard())
                .date(attachment.getDate())
                .extension(attachment.getExtension())
                .filename(attachment.getFilename())
                .path(attachment.getPath())
                .size(attachment.getSize())
                .systemFilename(attachment.getSystemFilename())
                .description(attachment.getDescription())
                .state(State.DELETED)
                .build()
        );
    }

    @Override
    public Reply updateCardReply(String token, ReplyDTO replyDTO)
            throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        User user = userRepository.findByEmail(jwtTokenProvider.getUserPk(token))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        TeamMember teamMember = teamMemberRepository.findById(replyDTO.getTeamMemberId())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        List<TeamMember> members = teamMemberRepository.findByUser_Id(user.getId())
                .map(s -> s.stream()
                        // 리스트에서 팀 아이디가 같은지 추출
                        .filter(l -> l.getTeam().getId().intValue() == teamMember.getTeam().getId())
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());

        if (members.size() <= 0)
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        Reply reply = replyRepository.findById(replyDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));


        return replyRepository.save(
                Reply.builder()
                .id(reply.getId())
                .card(reply.getCard())
                .content(replyDTO.getContent())
                .date(reply.getDate())
                .teamMember(reply.getTeamMember())
                .build());
    }

    @Override
    public Reply createCardReply(String token, ReplyDTO replyDTO)
            throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        User user = userRepository.findByEmail(jwtTokenProvider.getUserPk(token))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        TeamMember teamMember = teamMemberRepository.findById(replyDTO.getTeamMemberId())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        List<TeamMember> members = teamMemberRepository.findByUser_Id(user.getId())
                .map(s -> s.stream()
                        // 리스트에서 팀 아이디가 같은지 추출
                        .filter(l -> l.getTeam().getId().intValue() == teamMember.getTeam().getId())
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());

        if (members.size() <= 0)
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        Card card = cardRepository.findById(replyDTO.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));



        return replyRepository.save(
                Reply.builder()
                        .card(card)
                        .content(replyDTO.getContent())
                        .date(new Date())
                        .teamMember(teamMember)
                        .build());
    }

    @Override
    public Reply deleteReply(String token, ReplyDTO replyDTO)
            throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        User user = userRepository.findByEmail(jwtTokenProvider.getUserPk(token))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        TeamMember teamMember = teamMemberRepository.findById(replyDTO.getTeamMemberId())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        List<TeamMember> members = teamMemberRepository.findByUser_Id(user.getId())
                .map(s -> s.stream()
                        // 리스트에서 팀 아이디가 같은지 추출
                        .filter(l -> l.getTeam().getId().intValue() == teamMember.getTeam().getId())
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());

        if (members.size() <= 0)
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        Reply reply = replyRepository.findById(replyDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        replyRepository.delete(reply);

        return reply;
    }
}
