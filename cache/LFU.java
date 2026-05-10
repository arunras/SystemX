import java.util.*;

public class LFU {
  private final int capacity;
  private int minFreq;
  private final Map<Integer, Node> map;
  private final Map<Integer, DList> freqMap;

  public LFU(int capacity) {
    this.capacity = capacity;
    this.minFreq = 0;
    this.map = new HashMap<>();
    this.freqMap = new HashMap<>();
  }

  public int get(int key) {
    Node node = map.get(key);
    if (node == null) return -1;

    updateFreq(node);
    return node.value;
  }

  public void put(int key, int value) {
    Node node = map.get(key);
    if (node != null) {
      node.value = value;
      updateFreq(node);
    } else {
      if (map.size() == capacity) {
        DList minList = freqMap.get(minFreq);
        Node lfuNode = minList.removeTail();
        map.remove(lfuNode.key);
      }

      Node newNode = new Node(key, value);
      map.put(key, newNode);
      freqMap.computeIfAbsent(newNode.freq, k -> new DList()).add(newNode);
      minFreq = 1;
    }
  }

  private void updateFreq(Node node) {
    int freq = node.freq;
    DList list = freqMap.get(freq);
    list.remove(node);

    if (list.isEmpty()) {
      freqMap.remove(freq);
      if (freq == minFreq) {
        minFreq++;
      }
    }

    node.freq++;
    freqMap.computeIfAbsent(node.freq, k -> new DList()).add(node);
  }


  // Node Class
  private static class Node {
    int key;
    int value;
    int freq;
    Node next;
    Node prev;

    Node() {}

    Node(int key, int value) {
      this.key = key;
      this.value = value;
      this.freq = 1;
    }
  }

  // DList Class
  private static class DList {
    Node head;
    Node tail;

    DList() {
      this.head = new Node();
      this.tail = new Node();
      head.next = tail;
      tail.prev = head;
    }

    void add(Node node) {
      node.prev = head;
      node.next = head.next;
      head.next.prev = node;
      head.next = node;
    }

    Node remove(Node node) {
      node.prev.next = node.next;
      node.next.prev = node.prev;
      return node;
    }

    Node removeTail() {
      return remove(tail.prev);
    }

    boolean isEmpty() {
      return head.next == tail;
    }
  }

}
