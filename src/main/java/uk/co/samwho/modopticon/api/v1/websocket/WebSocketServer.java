package uk.co.samwho.modopticon.api.v1.websocket;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.flogger.FluentLogger;
import com.google.gson.Gson;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import uk.co.samwho.modopticon.api.v1.websocket.handlers.Authenticate;
import uk.co.samwho.modopticon.api.v1.websocket.handlers.MessageHandler;

@WebSocket
@Singleton
@ThreadSafe
public final class WebSocketServer {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private static final Duration SOCKET_IDLE_TIMEOUT = Duration.ofMinutes(15);
  private static final Joiner WHITESPACE_JOINER = Joiner.on(' ');
  private static final Splitter WHITESPACE_SPLITTER =
      Splitter
        .on(CharMatcher.whitespace())
        .trimResults()
        .omitEmptyStrings();

  private final Gson gson;
  private final Map<Session, EntityObserver> observers;
  private final Map<String, MessageHandler> handlers;

  @Inject
  WebSocketServer(Gson gson, Map<String, MessageHandler> handlers) {
    this.gson = gson;
    this.observers = new ConcurrentHashMap<>();
    this.handlers = handlers;
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
      send(session, Message.error("received empty message from you"));
      return;
    }

    List<String> parts = WHITESPACE_SPLITTER.splitToList(message);
    if (parts.isEmpty()) {
      send(session, Message.error("received empty message from you"));
      return;
    }

    MessageHandler handler = handlers.get(parts.get(0));
    if (handler == null) {
      send(session, Message.error("unrecognised command: " + parts.get(0)));
      return;
    }

    EntityObserver eo = observers.computeIfAbsent(session, k -> new EntityObserver(gson, k));
    if (!eo.isAuthenticated() && handler.getClass() != Authenticate.class) {
      send(session, Message.notAuthenticated());
      return;
    }

    try {
      send(session, handler.handle(eo, parts));
    } catch(Exception e) {
      logger.atSevere().withCause(e).log("exception while processing message: " + WHITESPACE_JOINER.join(parts));
      send(session, Message.error("internal server error"));
    }
  }

  private void send(Session session, Message message) throws IOException {
    if (!session.isOpen()) {
      logger.atWarning().log("attempted to send to closed session");
      return;
    }

    session.getRemote().sendString(gson.toJson(message));
  }
}