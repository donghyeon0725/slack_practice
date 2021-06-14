package com.slack.slack.socket.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * 아이디를 Integer이 아닌, Long으로 사용함으로써 더 많은 유저를 담을 수 있고
 * 직렬화를 사용함으로써 자바의 User 객체가 UserId으로 같은 객체인지 아닌지 판별이 가능해 집니다.
 * */
public abstract class AbstractBaseId implements Serializable {

  private static final long serialVersionUID = 3435210296634626689L;

  private long id;

  public AbstractBaseId(long id) {
    this.id = id;
  }

  public long value() {
    return id;
  }

  public boolean isValid() {
    return id > 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AbstractBaseId)) return false;
    AbstractBaseId that = (AbstractBaseId) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
