package uk.co.samwho.modopticon.api.v1.websocket;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.flogger.FluentLogger;
import com.google.gson.Gson;

import org.eclipse.jetty.websocket.api.Session;

import uk.co.samwho.modopticon.storage.Entity;

public final class EntityObserver implements PropertyChangeListener, Closeable {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private final Gson gson;
  private final Session session;
  private final Set<Entity> observing;

  private volatile boolean authenticated;

  EntityObserver(Gson gson, Session session) {
    this.gson = gson;
    this.session = session;
    this.observing = Collections.newSetFromMap(new ConcurrentHashMap<>());
    this.authenticated = false;
  }

  public void authenticate() {
    this.authenticated = true;
  }

  public boolean isAuthenticated() {
    return authenticated;
  }

  @Override
  public void propertyChange(PropertyChangeEvent pce) {
    send(session, Message.update(pce));
  }

  public synchronized void observe(Entity o) {
    o.addPropertyChangeListener(this);
    observing.add(o);
  }

  public synchronized void stopObserving(Entity o) {
    o.removePropertyChangeListener(this);
    observing.remove(o);
  }

  public Set<Entity> observing() {
    return Collections.unmodifiableSet(this.observing);
  }

  @Override
  public void close() throws IOException {
    observing.forEach(o -> o.removePropertyChangeListener(this));
    if (session.isOpen()) {
      session.close();
    }
  }

  private void send(Session session, Message message) {
    if (!session.isOpen()) {
      logger.atWarning().log("attempted to send to closed session");
      return;
    }

    try {
      session.getRemote().sendString(gson.toJson(message));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
