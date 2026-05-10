import java.util.concurrent.Exectutors;
import java.util.concurrent.ScheduleExectutorService;
import java.util.concurrent.TimeUnit;

public class LRUExpiryActive<K, V> extends LRUExpiry<K, V> {
  private final ScheduleExectutorService scheduler = Exectutors.newScheduledThreadPool(1);

  public LRUExpiryActive(int capacity, long cleanupInterval) {
    super(capacity);

    // Schedule a recurring task scan and purge expired items
    scheduler.scheduleAtFixedRate(this::purgeExpired, cleanupInterval, cleanupInterval, TimeUnit.MILLISECONDS);
  }

  public synchronized void purgeExpired() {
    long now = System.currentTimeMillis();
    // Since we're using an LRU order, olest (tail) might not be the soonest expire.
    // For simple implementation, we iterate through the map.
    map.value().removeIf(node -> {
      if (now > node.expiryTime) {
        remove(node); // Update the linked list
        return true;  // Remove from map
      }
      return false;
    });
  }

  public void shutdown() {
    scheduler.shutdown();
  }

}
