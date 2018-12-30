package uk.co.samwho.modopticon.api.v1.websocket;

import java.util.Collection;
import java.util.stream.Collectors;

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

  public static Message entityNotFound(String resourceId) {
    return new Message(Type.ENTITY_NOT_FOUND, resourceId);
  }

  public static Message invalidResourceIdentifier(String resourceId) {
    return new Message(Type.INVALID_RESOURCE_IDENTIFIER, resourceId);
  }

  public static Message notAuthenticated() {
    return new Message(Type.ERROR, "you must first authenticate by sending a message of the form: \"authenticate <apiKey>\"");
  }

  public static Message subscriptions(Collection<Entity> entities) {
    return new Message(Type.SUBSCRIPTIONS, entities.stream().map(Entity::id).collect(Collectors.toList()));
  }

  public static Message authenticated() {
    return new Message(Type.AUTHENTICATED, "authentication successful");
  }

  public enum Type {
    SUBSCRIBED,
    UNSUBSCRIBED,
    ENTITY_UPDATE,
    ERROR,
    SUBSCRIPTIONS,
    AUTHENTICATED,
    ENTITY_NOT_FOUND,
    INVALID_RESOURCE_IDENTIFIER
  }

  @Expose private final Type type;
  @Expose private final Object content;

  private Message(Type type, Object content) {
    this.type = type;
    this.content = content;
  }
}
