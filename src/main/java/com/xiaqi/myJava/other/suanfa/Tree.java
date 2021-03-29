package com.xiaqi.myJava.other.suanfa;

public class Tree {
	private Node root;

	/**
	 * 创建内部节点类
	 **/
	private class Node {
		// 左节点
		private Node leftChild;
		// 右节点
		private Node rightChild;
		// 节点对应的值
		private int data;

		public Node(int data) {
			this.leftChild = null;
			this.rightChild = null;
			this.data = data;
		}
	}// class Node

	public Tree() {
		root = null;
	}

	/**                           1
	 * 递归的创建二叉树                                       / \
	 *                          2   3
	 * @param node             / \  / \
	 * @param data            4  5 6  7
	 */
	public void buildTree() {
		root = new Node(1);
		root.leftChild=new Node(2);
		root.rightChild=new Node(3);
		root.leftChild.leftChild=new Node(4);
		root.leftChild.rightChild=new Node(5);
		root.rightChild.leftChild=new Node(6);
		root.rightChild.rightChild=new Node(7);
	}
	/*
	 * 前序遍历二叉树
	 */

	public void preOrder(Node node) {
		if (node != null) {
			System.out.print(node.data);
			preOrder(node.leftChild);
			preOrder(node.rightChild);
		}
	}

	/*
	 * 中序遍历二叉树
	 */
	public void inOrder(Node node) {
		if (node != null) {
			inOrder(node.leftChild);
			System.out.print(node.data);
			inOrder(node.rightChild);
		}
	}

	/*
	 * 后序遍历二叉树
	 */
	public void postOrder(Node node) {
		if (node != null) {
			postOrder(node.leftChild);
			postOrder(node.rightChild);
			System.out.print(node.data);
		}
	}

	public static void main(String ars[]) {
		/*int[] a = { 2, 4, 12, 45, 21, 6, 111 };
		
		for (int i = 0; i < a.length; i++) {
			binTree.buildTree(binTree.root, a[i]);
		}*/
		Tree binTree = new Tree();
		binTree.buildTree();
		System.out.print("前序遍历");
		binTree.preOrder(binTree.root);
		System.out.println("");
		System.out.print("中序遍历");
		binTree.inOrder(binTree.root);
		System.out.println("");
		System.out.print("后序遍历");
		binTree.postOrder(binTree.root);
	}
}
