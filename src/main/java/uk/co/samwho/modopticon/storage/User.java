package uk.co.samwho.modopticon.storage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class User {
  private final Long id;
  private final Map<String, Object> attributes;

  User(Long id) {
    this.id = id;
    this.attributes = Collections.synchronizedMap(new HashMap<>());
  }

  public Long getId() {
    return this.id;
  }

  public Map<String, Object> getAttributes() {
    return Collections.unmodifiableMap(this.attributes);
  }

  public Object setAttribute(String key, Object value) {
    return this.attributes.put(key, value);
  }

  public Object getAttribute(String key) {
    return this.attributes.get(key);
  }
}