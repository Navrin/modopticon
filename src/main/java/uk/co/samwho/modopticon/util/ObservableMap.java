package uk.co.samwho.modopticon.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.flogger.FluentLogger;

public final class ObservableMap<K, V> implements Map<K, V> {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();


  public static <K, V> ObservableMap<K, V> wrap(Map<K, V> map) {
    return new ObservableMap<>(map);
  }

  private final Map<K, V> map;
  private final PropertyChangeSupport pcs;

  private ObservableMap(Map<K, V> map) {
    this.map = map;
    this.pcs = new PropertyChangeSupport(this);
  }

  public void addPropertyChangeListener(PropertyChangeListener pcl) {
    pcs.addPropertyChangeListener(pcl);
  }

  public void removePropertyChangeListener(PropertyChangeListener pcl) {
    pcs.removePropertyChangeListener(pcl);
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
      pcs.firePropertyChange(key.toString(), prev, value);
    }

    return prev;
  }

  public V remove(Object key) {
    V prev = map.remove(key);

    if (prev != null) {
      pcs.firePropertyChange(key.toString(), prev, null);
    }

    return prev;
  }

  public synchronized void putAll(Map<? extends K, ? extends V> m) {
    m.forEach(map::put);
  }

  public synchronized void clear() {
    map.keySet().forEach(map::remove);
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