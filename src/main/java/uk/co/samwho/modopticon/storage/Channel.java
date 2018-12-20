package uk.co.samwho.modopticon.storage;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Channel extends Entity {
  private static final String TYPE = "channel";

  Channel(long id, long guildId) {
    super(TYPE, String.format("/guilds/%d/channels/%d", guildId, id));
  }
}