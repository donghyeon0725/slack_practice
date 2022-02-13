package com.slack.socket.socket.handlers;

import com.slack.slack.common.entity.Team;
import com.slack.slack.common.entity.TeamMember;
import com.slack.slack.common.repository.TeamMemberRepository;
import com.slack.slack.common.repository.TeamRepository;
import com.slack.slack.common.repository.UserRepository;
import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.exception.ResourceNotFoundException;
import com.slack.slack.common.exception.UserNotFoundException;
import com.slack.socket.socket.RealTimeSession;
import com.slack.socket.socket.SubscriptionHub;
import com.slack.slack.common.socket.anotation.Action;
import com.slack.slack.common.socket.anotation.ChannelHandler;
import com.slack.slack.common.socket.anotation.ChannelValue;
import com.slack.socket.socket.updater.CardUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * clock 채널을 관리해줄 수 있는 핸들러 입니다.
 * ChannelHandlerResolver 가
 * 이 어노테이션을 보고, 적절한 핸들러를 찾아줍니다.
 *
 * SubscriptionHub 를 통해, 구독과 해제를 관리해줍니다.
 * */

@ChannelHandler("/team/*")
public class TeamChannelHandler {
    private static final Logger log = LoggerFactory.getLogger(TeamChannelHandler.class);

    private TeamRepository teamRepository;

    private TeamMemberRepository teamMemberRepository;

    private UserRepository userRepository;

    private CardUpdater cardUpdater;

    public TeamChannelHandler(TeamRepository teamRepository,
                              TeamMemberRepository teamMemberRepository,
                              UserRepository userRepository,
                              CardUpdater cardUpdater) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
        this.cardUpdater = cardUpdater;
    }


    @Action("subscribe")
    public void subscribe(RealTimeSession session, @ChannelValue String channel) {
        log.debug("RealTimeSession[{}] Subscribe to channel `{}`", session.id(), channel);

        String[] slot = channel.split("/");
        Integer teamId = null;
        Team team = null;
        Integer userId = null;

        try {
            teamId = Integer.parseInt(slot[slot.length-1]);
        } catch (NumberFormatException e) {
            session.error("team does not exist");
        }

        userId = (int) Long.parseLong(session.getUserId().toString());

        try {
            team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
        } catch (ResourceNotFoundException e) {
            log.debug("team does not exist : " + team.getTeamId());
            session.error("team does not exist : " + team.getTeamId());
            return;
        }

        try {
            TeamMember teamMember = teamMemberRepository.findByTeamAndUser(
                    team,
                    userRepository.findById(userId)
                            .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND))
            ).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        } catch (ResourceNotFoundException e) {
            log.debug("this user is not teamMember of team : " + team.getTeamId());
            session.error("this user is not teamMember of team : " + team.getTeamId());
            return;
        } catch (UserNotFoundException e) {
            log.debug("user does not exist : " + e.getMessage());
            session.error("user does not exist : " + e.getMessage());
            return;
        }

        SubscriptionHub.subscribe(session, channel);
    }

    @Action("unsubscribe")
    public void unsubscribe(RealTimeSession session, @ChannelValue String channel) {
        log.debug("RealTimeSession[{}] Unsubscribe from channel `{}`", session.id(), channel);

        SubscriptionHub.unsubscribe(session, channel);
    }

}
