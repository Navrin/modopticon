package uk.co.samwho.modopticon.api.v1.websocket;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.flogger.FluentLogger;
import com.google.gson.Gson;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import uk.co.samwho.modopticon.storage.Entity;
import uk.co.samwho.modopticon.storage.Storage;

@WebSocket
@Singleton
@ThreadSafe
public final class WebSocketServer {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private static final Duration SOCKET_IDLE_TIMEOUT = Duration.ofMinutes(15);
  private static final Splitter WHITESPACE_SPLITTER =
      Splitter
        .on(CharMatcher.whitespace())
        .trimResults()
        .omitEmptyStrings();

  private final Gson gson;
  private final Storage storage;
  private final Map<Session, EntityObserver> observers;

  @Inject
  WebSocketServer(Gson gson, Storage storage) {
    this.gson = gson;
    this.storage = storage;
    this.observers = new ConcurrentHashMap<>();
  }

  @OnWebSocketConnect
  public void connect(Session session) {
    session.setIdleTimeout(SOCKET_IDLE_TIMEOUT.toMillis());
    observers.put(session, new EntityObserver(gson, session));
  }

  @OnWebSocketClose
  public void close(Session session, int statusCode, String reason) {
    EntityObserver observer = observers.remove(session);
    if (observer == null) {
      logger.atSevere().log("failed to find observer for session: %s", session);
      return;
    }

    try {
      observer.close();
    } catch (IOException e) {
      logger.atSevere().withCause(e).log("threw IOException while closing session: %s, error: %s", session, e);
    }
  }

  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
    if (Strings.isNullOrEmpty(message)) {
      return;
    }

    List<String> parts = WHITESPACE_SPLITTER.splitToList(message);

    if (parts.isEmpty()) {
      return;
    }

    if (!session.isOpen()) {
      return;
    }

    switch (parts.get(0)) {
    case "subscribe":
      subscribe(session, parts.get(1));
      break;
    case "unsubscribe":
      unsubscribe(session, parts.get(1));
      break;
    case "subscriptions":
      if (parts.size() > 1) {
        send(session, Message.error("subscriptons command takes no args"));
        break;
      }
      subscriptions(session);
      break;
    default:
      send(session, Message.error("unknwon command: " + parts.get(0)));
      break;
    }
  }

  private void subscribe(Session session, String resourceIdentifier) throws IOException {
    Optional<Entity> entity;
    try {
      entity = storage.fromResourceIdentifier(resourceIdentifier);
    } catch (Exception e) {
      send(session, Message.error(e.getMessage()));
      return;
    }

    if (!entity.isPresent()) {
      send(session, Message.error("could not find resource with identifier " + resourceIdentifier));
      return;
    }

    EntityObserver eo = observers.computeIfAbsent(session, k -> new EntityObserver(gson, k));
    eo.observe(entity.get());

    send(session, Message.subscribed(entity.get()));
  }

  private void unsubscribe(Session session, String resourceIdentifier) throws IOException {
    Optional<Entity> entity;
    try {
      entity = storage.fromResourceIdentifier(resourceIdentifier);
    } catch (Exception e) {
      send(session, Message.error(e.getMessage()));
      return;
    }

    if (!entity.isPresent()) {
      send(session, Message.error("could not find resource with identifier " + resourceIdentifier));
      return;
    }

    EntityObserver eo = observers.computeIfAbsent(session, k -> new EntityObserver(gson, k));
    eo.stopObserving(entity.get());

    send(session, Message.unsubscribed(entity.get()));
  }

  private void subscriptions(Session session) throws IOException {
    if (!session.isOpen()) {
      return;
    }

    send(session, Message.subscriptions(observers.get(session).observing()));
  }

  private void send(Session session, Message message) throws IOException {
    if (!session.isOpen()) {
      return;
    }

    session.getRemote().sendString(gson.toJson(message));
  }
}