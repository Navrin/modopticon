package uk.co.samwho.modopticon.api.v1.websocket;

import com.google.gson.annotations.Expose;

import uk.co.samwho.modopticon.storage.Entity;

public final class Message {
  public static Message update(Entity entity) {
    return new Message(Type.ENTITY_UPDATE, entity);
  }

  public static Message subscribed(Entity entity) {
    return new Message(Type.SUBSCRIBED, entity);
  }

  public static Message unsubscribed(Entity entity) {
    return new Message(Type.UNSUBSCRIBED, entity);
  }

  public static Message error(String message) {
    return new Message(Type.ERROR, message);
  }

  public enum Type {
    SUBSCRIBED, UNSUBSCRIBED, ENTITY_UPDATE, ERROR
  }

  @Expose private final Type type;
  @Expose private final Object content;

  private Message(Type type, Object content) {
    this.type = type;
    this.content = content;
  }
}
