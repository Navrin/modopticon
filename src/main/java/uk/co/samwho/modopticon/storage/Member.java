package uk.co.samwho.modopticon.storage;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Member extends Entity {
  private static final String TYPE = "member";

  Member(long id, long guildId) {
    super(TYPE, String.format("/guilds/%d/members/%d", guildId, id));
  }
}