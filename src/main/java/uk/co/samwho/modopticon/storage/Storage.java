package uk.co.samwho.modopticon.storage;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import com.google.inject.Singleton;

@ThreadSafe
@Singleton
public final class Storage {
  private final Map<Long, Guild> guilds = Collections.synchronizedMap(new HashMap<>());
  private final Map<Long, User> users = Collections.synchronizedMap(new HashMap<>());

  public User user(Long id) {
    return users.computeIfAbsent(id, k -> new User(id));
  }

  public Collection<User> users() {
    return users.values();
  }

  public Guild guild(Long id) {
    return guilds.computeIfAbsent(id, k -> new Guild(id));
  }

  public Collection<Guild> guilds() {
    return guilds.values();
  }
}