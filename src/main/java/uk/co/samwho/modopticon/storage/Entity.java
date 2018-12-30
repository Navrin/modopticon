package uk.co.samwho.modopticon.storage;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.flogger.FluentLogger;
import com.google.gson.annotations.Expose;

import uk.co.samwho.modopticon.util.ObservableMap;

@ThreadSafe
public abstract class Entity {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  @Expose private final String type;
  @Expose private final String id;
  @Expose private final ObservableMap<String, Object> attributes;

  private final PropertyChangeSupport pcs;

  protected Entity(String type, String id) {
    this.type = type;
    this.id = id;
    this.attributes = ObservableMap.wrap(new ConcurrentHashMap<>());
    this.pcs = new PropertyChangeSupport(this);
    this.attributes.addPropertyChangeListener(pce -> {
      this.pcs.firePropertyChange(pce.getPropertyName(), pce.getOldValue(), pce.getNewValue());
    });
  }

  public void addPropertyChangeListener(PropertyChangeListener pcl) {
    pcs.addPropertyChangeListener(pcl);
  }

  public void removePropertyChangeListener(PropertyChangeListener pcl) {
    pcs.removePropertyChangeListener(pcl);
  }

  public final String id() {
    return this.id;
  }

  public final Map<String, Object> attributes() {
    return this.attributes;
  }
}