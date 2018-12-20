package uk.co.samwho.modopticon.util;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Observable;
import java.util.Set;

import com.google.common.flogger.FluentLogger;

public final class ObservableMap<K, V> extends Observable implements Map<K, V> {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  public static <K, V> ObservableMap<K, V> wrap(Map<K, V> map) {
    return new ObservableMap<>(map);
  }

  private final Map<K, V> map;

  private ObservableMap(Map<K, V> map) {
    this.map = map;
  }

  public int size() {
    return map.size();
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }

  public boolean containsKey(Object key) {
    return map.containsKey(key);
  }

  public boolean containsValue(Object value) {
    return map.containsValue(value);
  }

  public V get(Object key) {
    return map.get(key);
  }

  public V put(K key, V value) {
    V prev = map.put(key, value);

    if (!Objects.equals(prev, value)) {
      logger.atFine().log("triggering map change from put(): %s", map.toString());
      this.setChanged();
      this.notifyObservers(new AbstractMap.SimpleEntry<K,V>(key, value));
    }

    return prev;
  }

  public V remove(Object key) {
    V prev = map.remove(key);

    if (prev != null) {
      logger.atFine().log("triggering map change from remove(): %s", map.toString());
      this.setChanged();
      this.notifyObservers(map);
    }

    return prev;
  }

  public void putAll(Map<? extends K, ? extends V> m) {
    map.putAll(m);
    logger.atFine().log("triggering map change from putAll(): %s", map.toString());
    this.setChanged();
    this.notifyObservers(map);
  }

  public void clear() {
    map.clear();
    logger.atFine().log("triggering map change from clear(): %s", map.toString());
    this.setChanged();
    this.notifyObservers(map);
  }

  public Set<K> keySet() {
    return map.keySet();
  }

  public Collection<V> values() {
    return map.values();
  }

  public Set<Map.Entry<K, V>> entrySet() {
    return map.entrySet();
  }
}