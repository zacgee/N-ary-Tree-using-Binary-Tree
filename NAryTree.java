/*

  Author: Zachary Geelalsingh
  Email: zgeelalsingh2022@my.fit.edu
  Course: CSE 2010
  Section: E4
  Description of this file: File that creates an n-ary tree of categories and sub categories using a binary tree structure

*/

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;

public class NAryTree {
   /*
      Description of each method, including parameters 
   */

   public static class Node implements Comparable<Node> {
      // variables to store Node information
      private final String data; // Node data
      private Node firstChild; // Stores the pointer to the first child node
      private Node nextSibling; // Stores the pointer to the next sibling node

      // Constructor 
      public Node (final String d) {
         this.data = d;
         this.firstChild = null;
         this.nextSibling = null;
      }

      // getter and setter methods

      public String getData () {
         return this.data;
      }

      public Node getFirstChild () {
         return this.firstChild;
      }

      public Node getNextSibling () {
         return this.nextSibling;
      }

      // adds a child to the tree, either as the first child of a parent or the sibling of another node
      public void addChild (Node childNode) {
         if (this.firstChild == null) {
            this.firstChild = childNode;
         } else {
            Node currentNode = this.firstChild;
            while (currentNode.nextSibling != null) {
               currentNode = currentNode.nextSibling;
            }

            currentNode.nextSibling = childNode;
         }
         return;
      }

      // returns an arraylist with all children of a given node
      public ArrayList<Node> getChildren () {
         if (this.firstChild == null) {
            return new ArrayList<Node>();
         }

         final ArrayList<Node> children = new ArrayList<Node>();
         Node currentNode = this.firstChild;
         while (currentNode != null) {
            children.add(currentNode);
            currentNode = currentNode.nextSibling;
         }

         return children;
      }

      // prints all children of a given node recursively
      public void printAllChildren () {
         if (this == null) {
            return;
         }
         Node child = this.firstChild;
         while (child != null) {
            System.out.printf("%s ", child.data);
            child.printAllChildren();
            child = child.nextSibling;
         }
      }

      // counts all children of a given node recursively
      public int countAllChildren () {
         if (this == null) {
            return 0;
         }
         Node child = firstChild;
         int count = 0;
         while (child != null) {
            count++;
            count += child.countAllChildren();
            child = child.nextSibling;
         }

         return count;
      }

      // compareTo override to allow for easy sorting of nodes
      public int compareTo (Node nextNode) {
         return data.compareTo(nextNode.getData());
      }  
   }


   // method to find the parent of a given node, by searching through each child of every node
   public static Node findParent(Node root, Node node) {
      // base cases
      if (root == null || node == null) {
         return null;
      }

      if (root == node) {
         return null;
      }

      Node child = root.firstChild;
      while (child != null) {
         if (child.data.equals(node.data)) {
            return root;
         } else {
            // recursive step
            Node parent = findParent(child, node);
            if (parent != null) {
               return parent;
            }
         }
         child = child.nextSibling;
      }

      return null;
   }

   // method to find a node, given the data
   public static Node findNode (Node root, String data) {
      if (root == null) {
         return null;
      }

      if (root.data.equals(data)) {
         return root;
      }

      Node foundNode = null;
      Node child = root.firstChild;
      while (child != null && foundNode == null) {
         foundNode = findNode (child, data);
         child = child.nextSibling;
      }

      return foundNode;
   }

   // method to determine whether a node is a subcategory of another node
   public static boolean isSubCategory (Node parent, Node node) {
      if (parent == null || node == null) {
         return false;
      }

      Node currentNode = parent.firstChild;
      while (currentNode != null) {
         if (currentNode == node || isSubCategory(currentNode, node)) {
            return true;
         }
         currentNode = currentNode.nextSibling;
      }
      return false;
   }

   // method to determine whether a node is a supercategory of another node
   public static boolean isSuperCategory(Node parent, Node child) {
      if (parent == null || child == null) {
         return false;
      }

      if (parent.equals(child)) {
         return true;
      }

      Node node = parent.firstChild;
      while (node != null) {
         if (isSuperCategory (node, child)) {
            return true;
         }
         node = node.nextSibling;
      }

      return false;
   }

   // method to determine the closest common supercategory given two nodes
   public static Node findClosestSupCat(Node root, Node node1, Node node2) {
      if (root == null || node1 == null || node2 == null) {
         return null;
      }

      if (root == node1 || root == node2) {
         return null;
      }

      if (root.firstChild == node1 || root.firstChild == node2) {
         return null;
      }
      
      Node closestSupCat = null;
      Node currentNode = root.firstChild;
      while (currentNode != null) {
         if (isSubCategory(currentNode, node1) && isSubCategory(currentNode, node2)) {
            closestSupCat = findClosestSupCat(currentNode, node1, node2);
            if (closestSupCat == null) {
               break;
            }
         }
         currentNode = currentNode.nextSibling;
      }

      return closestSupCat;
   }

   // main function accepts data from file and handles each query
   public static void main(String[] args) throws IOException {
      // accept input file name as command line argument
      final String inputFile = args[0];
      final Path inputPath = Paths.get(inputFile);
      // declare input file scanner variable
      final Scanner filein = new Scanner (inputPath, "US-ASCII");

      // accept query file name as command line argument
      final String queryFile = args[1];
      final Path queryPath = Paths.get(queryFile);
      // declare query file scanner variable
      final Scanner queryin = new Scanner (queryPath, "US-ASCII");

      // create a root for tree
      Node root = new Node ("root");

      // accept input data from file
      while (filein.hasNextLine()) {
         String line = filein.nextLine();
         String[] categories = line.split(" ");
         Node parent = findNode (root, categories[0]);
         // check if the parent exists before adding to the tree
         // if the parent does not exist then add to the root node
         if (parent == null) {
            parent = new Node (categories[0]);
            root.addChild(parent);
         }

         // add all children to the parent
         for (int i = 1; i < categories.length; i++) {
            Node childNode = new Node (categories[i]);
            parent.addChild(childNode);
         }
      }

      // accept query data from file
      while (queryin.hasNext()) {
         String query = queryin.next();
         String nodeName = queryin.next();

         // determine the direct supercategory (parent) of node and display
         if (query.equals("DirectSupercategory")) {
            Node parent = findParent(root, new Node(nodeName));
            System.out.printf ("%s %s ", query, nodeName);
            if (parent != root && parent != null){
               System.out.printf("%s ", parent.data);
            }
            System.out.printf("%n");

         // determine the direct subcategories (children) of a node and display
         } else if (query.equals("DirectSubcategories")) { 
            Node node = findNode (root, nodeName);
            System.out.printf("%s %s ", query, nodeName);
            if (node != null) {
               ArrayList<Node> children = node.getChildren();
               if (node.getChildren() != null) {
                  Collections.sort(children);
                  
                  for (int i = 0; i < children.size(); i++) {
                     System.out.printf("%s ", children.get(i).data);
                  }
               }
            }

            System.out.printf("%n");

         // determine all supercategories of a node and display each one
         } else if (query.equals("AllSupercategories")) {
            System.out.printf("%s %s ", query, nodeName);
            Node parent = findParent(root, new Node(nodeName));
            while (parent != null) {
               if (parent != root) {
                  System.out.printf("%s ", parent.data);
               }
               parent = findParent(root, parent);
            }
            System.out.printf("%n");

         // determine all subcategories of a node and display each one
         } else if (query.equals("AllSubcategories")) {
            System.out.printf("%s %s ", query, nodeName);
            Node node = findNode (root, nodeName);
            node.printAllChildren();
            System.out.printf("%n");

         // count the number of supercategories of a node and display it 
         } else if (query.equals("NumberOfAllSupercategories")) {
            System.out.printf("%s %s ", query, nodeName);
            Node parent = findParent(root, new Node(nodeName));
            int numSuperCategories = 0;
            while (parent != null) {
               numSuperCategories++;
               parent = findParent(root, parent);
            }
            System.out.printf("%d %n", numSuperCategories-1);

         // count the number of subcategories of a node and display it
         } else if (query.equals("NumberOfAllSubcategories")) {
            System.out.printf("%s %s ", query, nodeName);
            Node node = findNode (root, nodeName);
            int count = node.countAllChildren();
            System.out.printf("%d %n", count);

         // determine whether one node is a supercategory of the other
         } else if (query.equals("IsSupercategory")) {
            String superCat = queryin.next();
            System.out.printf("%s %s %s ", query, nodeName, superCat);
            Node parent = findNode(root, superCat);
            Node child = findNode (root, nodeName);


            if (isSuperCategory(parent, child) && !parent.equals(child)) {
               System.out.printf("yes%n");
            } else {
               System.out.printf("no%n");
            }

         // determine whether one node is a subcategory of the other
         } else if (query.equals("IsSubcategory")) {
            String subCat = queryin.next();
            System.out.printf("%s %s %s ", query, nodeName, subCat);
            Node parent = findNode (root, nodeName);
            Node child = findNode (root, subCat);

            if (isSuperCategory(parent, child) && !parent.equals(child)) {
               System.out.printf("yes%n");
            } else {
               System.out.printf("no%n");
            }
         
         // determine the closest common supercategories of two given nodes
         } else if (query.equals("ClosestCommonSupercategory")) {
            String nodeName2 = queryin.next(); 
            System.out.printf("%s %s %s ", query, nodeName, nodeName2);
            Node node1 = findNode (root, nodeName);
            Node node2 = findNode (root, nodeName2);

            Node parent = findClosestSupCat (root, node1, node2);
            if (parent != null) {
               System.out.printf("%s%n", parent.data);
            } else {
               System.out.printf("does not exist %n");
            }

         }
      }
   }
}

