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

  public Map<String, Object> attributes() {
    return this.attributes;
  }
}