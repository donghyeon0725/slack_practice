package com.slack.slack.system;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Time {
    /* 5분 */
    FIVE_MINUTE(5 * 60 * 1000L),
    /* 30분 */
    THIRTY_MINUTE(30 * 60 * 1000L),
    /* 1시간 */
    ONE_HOUR(60 * 60 * 1000L),
    /* 하루 */
    ONE_DAY(24 * 60 * 60 * 1000L);


    private long time;

    public long getTime() {
        return this.time;
    }
}
