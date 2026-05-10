public class LRUExpiry<K, V> {
  private final int capacity;
  private final Map<K, Node<K, V>> map;
  private final Node<K, V> head;
  private final Node<K, V> tail;

  public LRUExpiry(int capacity) {
    this.capacity = capacity;
    this.map = new HashMap<>();
    this.head = new Node<>(null, null, 0);
    this.tail = new Node<>(null, null, 0);
    head.next = tail;
    tail.prev = head;
  }

  public synchronized V get(K key) {
    Node<K, V> node = map.get(key);
    if (node == null) return null;

    // Lazy Expiry Check
    if (System.currentTimeMillis() > node.expiryTime) {
      remove(node);
      map.remove(node.key);
      return null;
    }

    moveToHead(node);
    return node.value;
  }

  public synchronized void put(K key, V value, long ttl) {
    long expiryTime = System.currentTimeMillis() + ttl;
    Node<K, V> node = map.get(key);
    if (node != null) {
      node.value = value;
      node.expiryTime = expiryTime;
      moveToHead(node);
    } else {
      if (map.size() >= capacity) {
        // Remove LRU (tail) to make space
        Node<K, V> lruNode = tail.prev;
        remove(lruNode);
        map.remove(lruNode.key);
      }

      Node<K, V> newNode = new Node(key, value, expiryTime);
      add(newNode);
      map.put(key, newNode);
    }
  }

  private void add(Node<K, V> node) {
    node.next = head.next;
    node.prev = head;
    head.next.prev = node;
    head.next = node;
  }

  private void remove(Node<K, V> node) {
    node.prev.next = node.next;
    node.next.prev = node.prev;
  }

  private void moveToHead(Node<K, V> node) {
    remove(node);
    add(node);
  }


  // Node Class
  private static class Node<K, V> {
    K key;
    V value;
    long expiryTime;
    Node<K, V> next, prev;

    Node(K key, V value, long expiryTime) {
      this.key = key;
      this.value = value;
      this.expiryTime = expiryTime;
    }
  }

}
