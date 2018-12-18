package uk.co.samwho.modopticon.storage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Member {
  private final Long id;
  private final Map<String, Object> attributes;

  Member(Long id) {
    this.id = id;
    this.attributes = Collections.synchronizedMap(new HashMap<>());
  }

  public Long getId() {
    return this.id;
  }

  public Map<String, Object> attributes() {
    return this.attributes;
  }
}