package uk.co.samwho.modopticon.storage;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class User extends Entity {
  private static final String TYPE = "user";

  User(long id) {
    super(TYPE, String.format("/users/%d", id));
  }
}