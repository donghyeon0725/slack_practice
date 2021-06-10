package com.slack.slack.domain.card;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "카드 더미")
public class CardsDTO {
    List<CardDTO> cards;
}
