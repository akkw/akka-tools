package com.akka.tools.caches;
/*
    create qiangzhiwei time 2022/6/25
 */

import com.akka.tools.api.Sync;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LRUCache extends Sync {
    class Node {
        int key;
        int value;
        Node prev;
        Node next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }

        Node() {
        }
    }

    private Map<Integer, Node> cache = new ConcurrentHashMap<>();
    private int size;
    private int capacity;
    private Node head, tail;

    public LRUCache(int capacity) {
        this.size = 0;
        this.capacity = capacity;
        this.head = new Node();
        this.tail = new Node();
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        Node node = cache.get(key);
        if (node == null) {
            return -1;
        }
        moveHead(node);
        return node.value;
    }

    public void put(int key, int value) {
        Node node = cache.get(key);

        if (node == null) {
            Node newNode = new Node(key, value);
            cache.put(key, newNode);
            addHead(newNode);
            size++;
            if (size > capacity) {
                Node tail = removeTail();
                cache.remove(tail.key);
            }
        } else {
            node.value = value;
            moveHead(node);
        }
    }

    private void moveHead(Node node) {
        removeNode(node);
        addHead(node);
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private Node removeTail() {
        Node prev = tail.prev;
        removeNode(prev);
        return prev;
    }

    private void addHead(Node newNode) {
        head.next.prev = newNode;
        newNode.next = head.next;
        newNode.prev = head;
        head.next = newNode;
    }
}
