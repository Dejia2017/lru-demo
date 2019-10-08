package cn.mengdejia.demo;

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
    private int capacity;
    private int count;
    private Node<K, V> head;
    private Node<K, V> tail;
    private Map<K, Node<K, V>> nodeMap;

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

    public LruCache(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException(String.valueOf(capacity));
        }
        this.capacity = capacity;
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);
        this.nodeMap = new HashMap<>();
        head.next = tail;
        tail.pre = head;
    }

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

    public Node<K, V> get(K key) {
        Node<K, V> node = nodeMap.get(key);
        if (node != null) {
            moveNodeToHead(node);
        }
        return node;
    }

    public void put(K key, V value) {
        Node node = nodeMap.get(key);
        if (node == null) {
            if (count >= capacity) {
                //如果超限，先移除一个节点（链表的最后一个节点，最近的范围是cache）
                removeNode();
            }
            node = new Node(key, value);
            //添加节点到头部
            addNode(node);
        } else {
            //如果是已经存在的节点，则移动节点到头节点。即表现为刚刚使用。
            moveNodeToHead(node);
        }
    }


    public void addNode(Node<K, V> node) {
        //添加节点到头部 构造map,维护count统计
        addToHead(node);
        nodeMap.put(node.key, node);
        count++;
    }

    public void removeNode() {
        Node<K, V> node = tail.pre;
        removeFromList(node);
        nodeMap.remove(node.key);
        count--;
    }

    public void moveNodeToHead(Node<K, V> node) {
        removeFromList(node);
        addToHead(node);
    }

    public void removeFromList(Node<K, V> node) {
        Node pre = node.pre;
        Node next = node.next;
        next.pre = pre;
        pre.next = next;
        node.pre = null;
        node.next = null;
    }

    public void addToHead(Node<K, V> node) {
        Node next = head.next;
        next.pre = node;
        node.next = next;
        node.pre = head;
        head.next = node;
    }
}
