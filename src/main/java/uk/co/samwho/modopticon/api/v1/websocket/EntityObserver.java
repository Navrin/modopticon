package uk.co.samwho.modopticon.api.v1.websocket;

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

public final class EntityObserver implements Observer, Closeable {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private final Gson gson;
  private final Session session;
  private final Set<Entity> observing;

  EntityObserver(Gson gson, Session session) {
    this.gson = gson;
    this.session = session;
    this.observing = Collections.newSetFromMap(new ConcurrentHashMap<>());
  }

  @Override
  public void update(Observable o, Object args) {
    logger.atInfo().log("update called for: %s", o.toString());

    if (!session.isOpen()) {
      return;
    }

    try {
      session.getRemote().sendString(gson.toJson(Message.update((Entity)o)));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public synchronized void observe(Entity o) {
    o.addObserver(this);
    observing.add(o);
  }

  public synchronized void stopObserving(Entity o) {
    o.deleteObserver(this);
    observing.remove(o);
  }

  public Set<Entity> observing() {
    return Collections.unmodifiableSet(this.observing);
  }

  @Override
  public void close() throws IOException {
    observing.forEach(o -> o.deleteObserver(this));
    if (session.isOpen()) {
      session.close();
    }
  }
}
