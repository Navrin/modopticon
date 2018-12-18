package uk.co.samwho.modopticon.storage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Channel {
  private final Long id;
  private final Map<String, Object> attributes;

  Channel(Long id) {
    this.id = id;
    this.attributes = Collections.synchronizedMap(new HashMap<>());
  }

  public Long getId() {
    return this.id;
  }

  public Map<String, Object> getAttributes() {
    return Collections.unmodifiableMap(this.attributes);
  }

  public Object getAttribute(String key) {
    return this.attributes.get(key);
  }

  public Object setAttribute(String key, Object value) {
    return this.attributes.put(key, value);
  }
}