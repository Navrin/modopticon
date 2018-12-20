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
  @Expose private final String resourceIdentifier;
  @Expose private final ObservableMap<String, Object> attributes;

  protected Entity(String type, String resourceIdentifier) {
    this.type = type;
    this.resourceIdentifier = resourceIdentifier;
    this.attributes = ObservableMap.wrap(new ConcurrentHashMap<>());
    this.attributes.addObserver((observable, arg) -> {
      logger.atFine().log("notifying observers of %s with arg %s", this, arg);
      this.setChanged();
      this.notifyObservers(arg);
    });
  }

  public final String resourceIdentifier() {
    return this.resourceIdentifier;
  }

  public final Map<String, Object> attributes() {
    return this.attributes;
  }
}