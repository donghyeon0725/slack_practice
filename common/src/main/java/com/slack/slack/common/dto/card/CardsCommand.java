package com.slack.slack.common.dto.card;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "카드 더미")
public class CardsCommand {
    List<CardCommand> cards;
}
