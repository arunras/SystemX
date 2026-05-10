import java.util.HashMap;
import java.util.Map;

public class LRU<K, V> {
  private final int capacity;
  private final Map<K, V> map;
  private final Node<K, V> head, tail;
  
  public LRU(int capacity) {
    this.capacity = capacity;
    this.map = new HashMap<>();
    // Dummy head and tail nodes to simplify edge cases (empty list)
    this.head = new Node(null, null);
    this.tail = new Hode(null, null);
    head.next = tail;
    tail.prev = head;
  }

  public synchronized V get(K key) {
    Node<K, V> node = map.get(key);
    if (node == null) return null;

    moveToHead(node);
    return node.value;
  }

  public synchronized void put(K key, V value) {
    Node<K, V> node = map.get(key);
    if (node != null) {
      node.value = value;
      moveToHead(node);
    } else {
      if (map.size() >= capacity) {
        Node<K, V> lruNode = tail.prev;
        map.remove(lruNode.key);
        remove(lruNode);
      }

      Node<K, V> newNode = new Node<>(key, value);
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
    Node<K, V> next;
    Node<K, V> prev;

    Node(K key, V value) {
      this.key = key;
      this.value = value;
    }
  }

}

