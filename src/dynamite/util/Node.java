package dynamite.util;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {
    private List<Node<T>> children = new ArrayList<Node<T>>();
    private Node<T> parent = null;
    private T data = null;

    public Node(T data) {
        this.data = data;
    }

    public Node(T data, Node<T> parent) {
        this.data = data;
        this.parent = parent;
    }

    public List<Node<T>> getChildren() {
        return children;
    }

    public void setParent(Node<T> parent) {
        parent.addChild(this);
        this.parent = parent;
    }

    public Node<T> getParent() {
        return parent;
    }

    public void addChild(T data) {
        Node<T> child = new Node<T>(data);
        child.setParent(this);
        this.children.add(child);
    }

    public void addChild(Node<T> child) {
        child.parent = this;
        this.children.add(child);
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isRoot() {
        return (this.parent == null);
    }

    public boolean isLeaf() {
        return this.children.size() == 0;
    }

    public void removeParent() {
        this.parent = null;
    }

    public Node<T> getChild(T s) {
        for (Node<T> child : children) {
            if (child.getData().equals(s)) {
                return child;
            }
        }
        return null;
    }

    public Node<T> insert(List<T> split) {
        Node<T> currNode = this;
        if (data == null) {
            this.data = split.get(0);
            for (int i = 1; i < split.size(); i++) {
                T s = split.get(i);
                currNode.addChild(new Node<T>(s));
                currNode.getChild(s);
            }
        } else {
            assert currNode.getData().equals(split.get(0)) : "trying to overwrite root";
            for (int i = 1; i < split.size(); i++) {
                T s = split.get(i);
                Node<T> child = currNode.getChild(s);
                if (child == null) {
                    currNode.addChild(new Node<>(s));
                }
                currNode = currNode.getChild(s);
            }
        }
        while (!currNode.getData().equals(this.data)) {
            currNode = currNode.getParent();
        }
        return currNode;
    }
}
