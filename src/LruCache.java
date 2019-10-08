import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Dejia Meng
 * @Date: 2019-10-08 10:34
 * Lru淘汰策略：一种对数据超过缓存容量时的处理方法
 * 删除最近最少使用的数据
 * <p>
 * 添加节点时，要放到头部。1. 如果满了，要先删除尾部节点。2. 如果已存在则移动到头部。
 * 删除节点时，删除尾部的。
 * 访问节点时，如果存在，则移动到头部。否则返回空。
 */
public class LruCache<K, V> {
    public static void main(String[] args) {
        LruCache cache = new LruCache(3);
        cache.put("day1", "1");
        cache.put("day2", "2");
        cache.put("day3", "3");
        cache.put("day4", "4");
        cache.put("day5", "5");
        cache.put("day5", "5");
        cache.put("day4", "4");
        cache.get("day3");

    }

    /**
     * 容量
     */
    private int capacity;
    /**
     * 当前节点数 默认值0
     */
    private int count;
    /**
     * 缓存的节点内容。使用map可以方便k-v操作。(获取的也是一整个k-v)
     */
    private Map<K, Node<K, V>> nodeMap;
    private Node<K, V> head;
    private Node<K, V> tail;

    public LruCache(int capacity) {
        // 缓存初始容量必须大于0
        if (capacity < 1) {
            throw new IllegalArgumentException(String.valueOf(capacity));
        }
        this.capacity = capacity;
        this.nodeMap = new HashMap<>();
        //初始化头节点和尾节点，建立双向链表【内部为空】
        Node headNode = new Node(null, null);
        Node tailNode = new Node(null, null);
        headNode.next = tailNode;
        tailNode.pre = headNode;
        this.head = headNode;
        this.tail = tailNode;
    }

    public void put(K key, V value) {
        Node<K, V> node = nodeMap.get(key);
        if (node == null) {
            if (count >= capacity) {
                //如果超限，先移除一个节点（链表的最后一个节点，最近的范围是cache）
                removeNode();
            }
            node = new Node<>(key, value);
            //添加节点到头部
            addNode(node);
        } else {
            //如果是已经存在的节点，则移动节点到头节点。即表现为刚刚使用。
            moveNodeToHead(node);
        }
    }

    public Node<K, V> get(K key) {
        Node<K, V> node = nodeMap.get(key);
        if (node != null) {
            moveNodeToHead(node);
        }
        return node;
    }

    /**
     * 删除最后一个节点
     */
    private void removeNode() {
        // 尾节点的前一个节点
        Node node = tail.pre;
        //从链表里面移除
        removeFromList(node);
        nodeMap.remove(node.key);
        count--;
    }

    private void removeFromList(Node<K, V> node) {
        // 获得当前节点的前一个节点和后一个节点
        Node pre = node.pre;
        Node next = node.next;
        // 建立前后节点的双向连接
        pre.next = next;
        next.pre = pre;
        // 清空当前节点的指针
        node.next = null;
        node.pre = null;
    }

    private void addNode(Node<K, V> node) {
        //添加节点到头部 构造map,维护count统计
        addToHead(node);
        nodeMap.put(node.key, node);
        count++;
    }

    /**
     * 把节点放至头结点的下一个。【头结点不含数据】 代码顺序为自下而上。先向上后向下。
     *
     * @param node
     */
    private void addToHead(Node<K, V> node) {
        // 1.获取头结点下一个节点
        Node next = head.next;
        // 2.设置下个节点的上一个节点为当前节点
        next.pre = node;
        // 3.设置当前节点的下一个节点为头结点的下一个节点
        node.next = next;
        // 4.设置当前节点的上一个节点为头结点
        node.pre = head;
        // 5.设置头结点的下一个节点为当前节点
        head.next = node;
    }

    public void moveNodeToHead(Node<K, V> node) {
        //从链表里面移除
        removeFromList(node);
        //添加节点到头部
        addToHead(node);
    }

    /**
     * 内部类 节点间形成双向链表
     *
     * @param <k>
     * @param <v>
     */
    class Node<k, v> {
        k key;
        v value;
        Node pre;
        Node next;

        public Node(k key, v value) {
            this.key = key;
            this.value = value;
        }
    }
}
