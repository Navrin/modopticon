package uk.co.samwho.modopticon.api.v1.websocket;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.Map;

import com.google.gson.annotations.Expose;

import uk.co.samwho.modopticon.storage.Entity;

public final class EntitySingleKeyChange {
  public static final EntitySingleKeyChange from(PropertyChangeEvent pce) {
    return new EntitySingleKeyChange(pce);
  }

  @Expose private final String id;
  @Expose private final String type;
  @Expose private final Map<String, Object> attributes;

  private EntitySingleKeyChange(PropertyChangeEvent pce) {
    Entity orig = (Entity)pce.getSource();
    this.id = orig.id();
    this.type = orig.type();
    this.attributes = Collections.singletonMap(pce.getPropertyName(), pce.getNewValue());
  }
}