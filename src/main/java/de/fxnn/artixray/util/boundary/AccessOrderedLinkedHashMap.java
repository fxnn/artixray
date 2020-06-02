package de.fxnn.artixray.util.boundary;

import java.util.LinkedHashMap;

public class AccessOrderedLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

  // HINT: this is exactly java.util.HashMap.DEFAULT_INITIAL_CAPACITY
  private static final int DEFAULT_INITIAL_CAPACITY = 16;
  // HINT: this is exactly java.util.HashMap.DEFAULT_LOAD_FACTOR
  private static final float DEFAULT_LOAD_FACTOR = 0.75f;
  private static final boolean IN_ACCESS_ORDER = true;

  public AccessOrderedLinkedHashMap() {
    super(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, IN_ACCESS_ORDER);
  }
}
