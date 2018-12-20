package uk.co.samwho.modopticon.storage;

import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.flogger.FluentLogger;
import com.google.gson.annotations.Expose;

import uk.co.samwho.modopticon.util.ObservableMap;

@ThreadSafe
public abstract class Entity extends Observable {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  @Expose private final String type;
  @Expose private final String id;
  @Expose private final ObservableMap<String, Object> attributes;

  protected Entity(String type, String id) {
    this.type = type;
    this.id = id;
    this.attributes = ObservableMap.wrap(new ConcurrentHashMap<>());
    this.attributes.addObserver((observable, arg) -> {
      logger.atFine().log("notifying observers of %s with arg %s", this, arg);
      this.setChanged();
      this.notifyObservers(arg);
    });
  }

  public final String id() {
    return this.id;
  }

  public final Map<String, Object> attributes() {
    return this.attributes;
  }
}