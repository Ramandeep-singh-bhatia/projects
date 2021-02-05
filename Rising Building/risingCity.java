import jdk.swing.interop.SwingInterOpUtils;
import java.io.*;
import java.sql.SQLOutput;
import java.util.*;

//class node is defined to store the building data structure. It stores building number, executed time and total time of the building.
class node {

    public int buildingNums;
    public int executed_time;
    public int total_time;

    node(int buildingNums, int executed_time, int total_time){

        this.buildingNums = buildingNums;
        this.executed_time = executed_time;
        this.total_time = total_time;
    }
}

class RBTree {

    private final int R = 0;
    private final int B = 1;

    private class Node {

        node KeyNode;
        int shade = B;
        Node LeftChild = sc, RightChild = sc, ParentNode = sc;

        Node(node KeyNode) {
            this.KeyNode = KeyNode;
        }
    }

    private final Node sc = new Node(new node(-999,-999,-999));
    private Node rootnode = sc;

    // Prints the building with 1 parameter
    public void print(Node node) {
        if (node == sc) {
            return;
        }
        print(node.LeftChild);
        print(node.RightChild);
    }

    //Find the building to be deleted in the rbtree. It returns the node to be deleted and is called from delete frunction
    private Node SearchBuilding(Node SearchBuilding, Node node) {
        // If root node is not null it returns null
        if (rootnode == sc) {
            return null;
        }
        // If the building number found is less than the node it returns left child
        if (SearchBuilding.KeyNode.buildingNums < node.KeyNode.buildingNums) {
            if (node.LeftChild != sc) {
                return SearchBuilding(SearchBuilding, node.LeftChild);
            }
        }  // If the building number found is greater than the node it returns right child
        else if (SearchBuilding.KeyNode.buildingNums > node.KeyNode.buildingNums) {
            if (node.RightChild != sc) {
                return SearchBuilding(SearchBuilding, node.RightChild);
            }
        } // If the building number found is equal to the node it returns the node
        else if (SearchBuilding.KeyNode.buildingNums == node.KeyNode.buildingNums) {
            return node;
        }
        return null;
    }
    //insert the new node into thr Red Black tree
    private void insertNode(Node node) {
        Node tmpvar = rootnode;
        // If root node is null it insert in root node.
        if (rootnode == sc) {
            rootnode = node;
            node.shade = B;
            node.ParentNode = sc;
        } else {
            node.shade = R;
            while (true) {
                // If building number of node is less than the newly inserted node, swap the left child
                if (node.KeyNode.buildingNums < tmpvar.KeyNode.buildingNums) {
                    if (tmpvar.LeftChild == sc) {
                        tmpvar.LeftChild = node;
                        node.ParentNode = tmpvar;
                        break;
                    } else {
                        tmpvar = tmpvar.LeftChild;
                    }
                }
                // If building number of node is greater than the newly inserted node, swap the right child
                else if (node.KeyNode.buildingNums >= tmpvar.KeyNode.buildingNums) {
                    if (tmpvar.RightChild == sc) {
                        tmpvar.RightChild = node;
                        node.ParentNode = tmpvar;
                        break;
                    } else {
                        tmpvar = tmpvar.RightChild;
                    }
                }
            }
            Fix(node);
        }
    }

    //Fixes the red black tee to make sure it follows the property

    private void Fix(Node node) {
        //if parent node is red
        while (node.ParentNode.shade == R) {
            Node unc = sc;
            // If uncle node is red. Change parent colour and uncle colour to black
            // Change parent's parent to red. Swap node to node's parent's parent
            // if uncle node is black do below rotations based on the condition
            // LL case - Right rotation of grand parent. Swap colours of parent and its parent
            // LR case - (Double rotation) First a Left rotation of parent. Apply LL Case
            // RR case - Left rotation of grandparent. Swap colours of parent and its parent
            // RL case - (Double rotation) First a right rotation of parent. Apply RR Case
            if (node.ParentNode == node.ParentNode.ParentNode.LeftChild) {
                unc = node.ParentNode.ParentNode.RightChild;

                if (unc != sc && unc.shade == R) {
                    node.ParentNode.shade = B;
                    unc.shade = B;
                    node.ParentNode.ParentNode.shade = R;
                    node = node.ParentNode.ParentNode;
                    continue;
                }
                if (node == node.ParentNode.RightChild) {

                    node = node.ParentNode;
                    rotateLeftChild(node);
                }
                node.ParentNode.shade = B;
                node.ParentNode.ParentNode.shade = R;

                rotateRightChild(node.ParentNode.ParentNode);
            }
            else {
                unc = node.ParentNode.ParentNode.LeftChild;
                if (unc != sc && unc.shade == R) {
                    node.ParentNode.shade = B;
                    unc.shade = B;
                    node.ParentNode.ParentNode.shade = R;
                    node = node.ParentNode.ParentNode;
                    continue;
                }
                if (node == node.ParentNode.LeftChild) {
                    //Double rotation
                    node = node.ParentNode;
                    rotateRightChild(node);
                }
                node.ParentNode.shade = B;
                node.ParentNode.ParentNode.shade = R;
                //single rotation
                rotateLeftChild(node.ParentNode.ParentNode);
            }
        }
        rootnode.shade = B;
    }
    // LL case - Right rotation of grand parent. Swap colours of parent and its parent
    // LR case - (Double rotation) First a Left rotation of parent. Apply LL Case
    void rotateLeftChild(Node node) {
        if (node.ParentNode != sc) {
            if (node == node.ParentNode.LeftChild) {
                node.ParentNode.LeftChild = node.RightChild;
            } else {
                node.ParentNode.RightChild = node.RightChild;
            }
            node.RightChild.ParentNode = node.ParentNode;
            node.ParentNode = node.RightChild;
            if (node.RightChild.LeftChild != sc) {
                node.RightChild.LeftChild.ParentNode = node;
            }
            node.RightChild = node.RightChild.LeftChild;
            node.ParentNode.LeftChild = node;
        } else {
            Node RightChild = rootnode.RightChild;
            rootnode.RightChild = RightChild.LeftChild;
            RightChild.LeftChild.ParentNode = rootnode;
            rootnode.ParentNode = RightChild;
            RightChild.LeftChild = rootnode;
            RightChild.ParentNode = sc;
            rootnode = RightChild;
        }
    }
    // RR case - Left rotation of grandparent. Swap colours of parent and its parent
    // RL case - (Double rotation) First a right rotation of parent. Apply RR Case
    void rotateRightChild(Node node) {
        if (node.ParentNode != sc) {
            if (node == node.ParentNode.LeftChild) {
                node.ParentNode.LeftChild = node.LeftChild;
            } else {
                node.ParentNode.RightChild = node.LeftChild;
            }

            node.LeftChild.ParentNode = node.ParentNode;
            node.ParentNode = node.LeftChild;
            if (node.LeftChild.RightChild != sc) {
                node.LeftChild.RightChild.ParentNode = node;
            }
            node.LeftChild = node.LeftChild.RightChild;
            node.ParentNode.RightChild = node;
        } else {//Need to rotate rootnode
            Node LeftChild = rootnode.LeftChild;
            rootnode.LeftChild = rootnode.LeftChild.RightChild;
            LeftChild.RightChild.ParentNode = rootnode;
            rootnode.ParentNode = LeftChild;
            LeftChild.RightChild = rootnode;
            LeftChild.ParentNode = sc;
            rootnode = LeftChild;
        }
    }

    //Swaps the target node with the new node.
    void Transfer(Node target, Node with){
        if(target.ParentNode == sc){
            rootnode = with;
        }else if(target == target.ParentNode.LeftChild){
            target.ParentNode.LeftChild = with;
        }else
            target.ParentNode.RightChild = with;
        with.ParentNode = target.ParentNode;
    }

    //Print the range of building numbers. If found call loopPrint
    String PrintTree(int b1,int b2)
    {
        Node cur=rootnode;
        String s="";
        int buildingfound = 0;
        while(cur!=null){
            if(cur.KeyNode.buildingNums>=b1 && cur.KeyNode.buildingNums<=b2){
                buildingfound = 1;
                s = LoopPrint(cur,b1,b2);
                return s;

            }
            else if(cur.KeyNode.buildingNums<b1){
                cur=cur.RightChild;
            }
            else{
                cur=cur.LeftChild;
            }
        }
        if (buildingfound == 0) {
            s = "(0,0,0)\n";
            return s;
        }
        return s;
    }

    // Loop Print return the building number, execution time and  total time of the building
    String LoopPrint(Node cur,int b1,int b2){
        if(cur== null ) return "";

        if(cur.KeyNode.buildingNums==b1){
            return(LoopPrint(cur.RightChild,b1,b2)+"("+cur.KeyNode.buildingNums+","+ cur.KeyNode.executed_time+","+ cur.KeyNode.total_time+"),");
        }
        else if(cur.KeyNode.buildingNums==b2){
            return(LoopPrint(cur.LeftChild,b1,b2)+"("+cur.KeyNode.buildingNums+","+ cur.KeyNode.executed_time+","+ cur.KeyNode.total_time+"),");
        }
        else
        {
            String aa="";
            if(cur.LeftChild!= null && isinRange(cur.LeftChild.KeyNode.buildingNums, b1,b2)){
                aa += LoopPrint(cur.LeftChild,b1,b2);
            }
            aa += "("+cur.KeyNode.buildingNums+","+ cur.KeyNode.executed_time+","+ cur.KeyNode.total_time+"),";

            if(cur.RightChild!= null && isinRange(cur.RightChild.KeyNode.buildingNums,b1,b2))
                aa += LoopPrint(cur.RightChild,b1,b2);
            return aa;
        }
    }

    boolean isinRange(int target,int l,int h){
        if(target>=l && target<=h)
            return true;
        return false;
    }
    // deletes the node from RBTree
    boolean deleteNode(Node z){
        if((z = SearchBuilding(z, rootnode))==null)return false;
        Node x;
        Node y = z; // temporary reference y
        int y_original_shade = y.shade;

        if(z.LeftChild == sc){
            x = z.RightChild;
            Transfer(z, z.RightChild);
        }else if(z.RightChild == sc){
            x = z.LeftChild;
            Transfer(z, z.LeftChild);
        }else{
            y = MinTree(z.RightChild);
            y_original_shade = y.shade;
            x = y.RightChild;
            if(y.ParentNode == z)
                x.ParentNode = y;
            else{
                Transfer(y, y.RightChild);
                y.RightChild = z.RightChild;
                y.RightChild.ParentNode = y;
            }
            Transfer(z, y);
            y.LeftChild = z.LeftChild;
            y.LeftChild.ParentNode = y;
            y.shade = z.shade;
        }
        if(y_original_shade==B)
            deleteNodeFixup(x);
        return true;
    }

    // Once the node is deleted it fixes the rbtree to maintain the property

    void deleteNodeFixup(Node x){
        while(x!=rootnode && x.shade == B){
            if(x == x.ParentNode.LeftChild){
                Node w = x.ParentNode.RightChild;
                if(w.shade == R){
                    w.shade = B;
                    x.ParentNode.shade = R;
                    rotateLeftChild(x.ParentNode);
                    w = x.ParentNode.RightChild;
                }
                if(w.LeftChild.shade == B && w.RightChild.shade == B){
                    w.shade = R;
                    x = x.ParentNode;
                    continue;
                }
                else if(w.RightChild.shade == B){
                    w.LeftChild.shade = B;
                    w.shade = R;
                    rotateRightChild(w);
                    w = x.ParentNode.RightChild;
                }
                if(w.RightChild.shade == R){
                    w.shade = x.ParentNode.shade;
                    x.ParentNode.shade = B;
                    w.RightChild.shade = B;
                    rotateLeftChild(x.ParentNode);
                    x = rootnode;
                }
            }else{
                Node w = x.ParentNode.LeftChild;
                if(w.shade == R){
                    w.shade = B;
                    x.ParentNode.shade = R;
                    rotateRightChild(x.ParentNode);
                    w = x.ParentNode.LeftChild;
                }
                if(w.RightChild.shade == B && w.LeftChild.shade == B){
                    w.shade = R;
                    x = x.ParentNode;
                    continue;
                }
                else if(w.LeftChild.shade == B){
                    w.RightChild.shade = B;
                    w.shade = R;
                    rotateLeftChild(w);
                    w = x.ParentNode.LeftChild;
                }
                if(w.LeftChild.shade == R){
                    w.shade = x.ParentNode.shade;
                    x.ParentNode.shade = B;
                    w.LeftChild.shade = B;
                    rotateRightChild(x.ParentNode);
                    x = rootnode;
                }
            }
        }
        x.shade = B;
    }

    Node MinTree(Node subTreerootnode){
        while(subTreerootnode.LeftChild!=sc){
            subTreerootnode = subTreerootnode.LeftChild;
        }
        return subTreerootnode;
    }
    // prints the building based on 1 parameter building number
    String printBuilding(int buildingNums){
        Node cur=rootnode;
        String aa="";
        int buildingfound = 0;
        while(cur!=null){
            if(cur.KeyNode.buildingNums == buildingNums){
                buildingfound = 1;
                aa = "("+cur.KeyNode.buildingNums+","+ cur.KeyNode.executed_time+","+ cur.KeyNode.total_time+")";
                return aa;
            }
            else if(buildingNums>cur.KeyNode.buildingNums)
                cur=cur.RightChild;
            else
                cur=cur.LeftChild;
        }
        if (buildingfound == 0) {
            aa = "(0,0,0)";
            return aa;
        }
        return aa;
    }

    public void LastOut(int choice,node b) {
        Scanner scan = new Scanner(System.in);
        Node node;
        switch (choice) {
            case 1:

                node = new Node(b);
                insertNode(node);

                break;

            case 2:

                node = new Node(b);
                if (deleteNode(node)) {

                } else {
                    System.out.print(": does not exist!");
                }
                break;

            case 4:

                print(rootnode);
                break;
        }
    }
}
//Main class
public class risingCity {

    //Initialize the node as array of object.
    private static node[] n = new node[2000];
    int size =0;



    //swapping two nodes to follow minheap property
    private void swap(int first, int second)
    {
        node tmpvar;
        tmpvar = n[first];
        n[first] = n[second];
        n[second] = tmpvar;
    }

    //Insert a node in the min heap data structure
    public void insert (node e){
        n[++size] = e;
        int currentpos = size;

    }

    //Minheap function calls min heapify on the whole tree to make sure it follows the min heap property
    public void minHeap()
    {
        for (int currentpos = (size/2); currentpos >= 1; currentpos--) {
            createMinHeap(currentpos);
        }
    }

    //removes the node whose execution time equals the total time.
    public void remove(int globalcounter)
    {


        if(size>0) {
            n[1] = n[size--];
            n[size+1] = null;
            minHeap();
        }
    }

    // Checks whether a particular node is leaf or not using its current position.
    // If it is a leaf it returns true, else it returns false.
    private boolean isLeaf(int pos)
    {
        if (pos >= ((size / 2)+1) && pos <= size) {
            return true;
        }
        return false;
    }
    // Min heapify function to make sure min heap property is maintained after insert, remove or when executed time equals total time
    private void createMinHeap(int currentpos)
    {
        // Checks whether the node at current position is leaf. If it is not leaf it returns True and if condition is satisfied.
        if (!isLeaf(currentpos)) {
            //Checks whether there is left child and right chile of a node
            if (n[2*currentpos+1] != null && n[2*currentpos] != null) {
                // This condition checks whether the node's execution time is greater than its children
                if (n[currentpos].executed_time > n[2 * currentpos].executed_time
                        || n[currentpos].executed_time > n[2 * currentpos + 1].executed_time) {
                    //Further check to verify whose execution time is smaller.
                    //If execution time of left child is less than the right child, it swaps the left child with the parent.
                    if (n[2 * currentpos].executed_time < n[2 * currentpos + 1].executed_time) {
                        swap(currentpos, 2 * currentpos);
                        createMinHeap(2 * currentpos);
                    } //If execution time of right child is less than the left child, it swaps the right child with the parent.
                    else if (n[2 * currentpos].executed_time > n[2 * currentpos + 1].executed_time){
                        swap(currentpos, 2 * currentpos + 1);
                        createMinHeap(2 * currentpos + 1);
                    } //If execution time of left child is equal to the right child, it checks the building number of left and right child.
                    else {
                        //If building num of left child is less than the right child, it swaps the left child with the parent.
                        if(n[2 * currentpos].buildingNums < n[2 * currentpos + 1].buildingNums){
                            swap(currentpos, 2 * currentpos);
                            createMinHeap(2 * currentpos);
                        } //If building num of right child is less than the left child, it swaps the right child with the parent.
                        else{
                            swap(currentpos, 2 * currentpos+1);
                            createMinHeap(2 * currentpos+1);
                        }
                    }
                } // This condition checks whether the node's execution time is equal to its children
                else if(n[currentpos].executed_time == n[2 * currentpos].executed_time
                        && n[currentpos].executed_time == n[2 * currentpos +1].executed_time){
                    //If building num of the node is greater than its children

                        // If building number of left child is less than building number of the right child, swap the node with left child based on building number.
                        if (n[currentpos].buildingNums > n[2 * currentpos].buildingNums || n[2 * currentpos].buildingNums < n[2 * currentpos + 1].buildingNums){
                            swap(currentpos, 2 * currentpos);
                            createMinHeap(2 * currentpos);
                        } // If building number of right child is less than building number of the left child, swap the node with right child based on building number.
                        else if (n[currentpos].buildingNums > n[2 * currentpos + 1].buildingNums ||n[2 * currentpos].buildingNums > n[2 * currentpos + 1].buildingNums){
                            swap(currentpos, 2 * currentpos + 1);
                            createMinHeap(2 * currentpos + 1);
                        }

                }
                // This condition checks whether the node's execution time is equal to only its left child, it checks building number of the left child
                else if (n[currentpos].executed_time == n[2 * currentpos].executed_time) {
                    //If building number of current node is greater than its left child, it swaps the node with left child.
                    if (n[currentpos].buildingNums > n[2 * currentpos].buildingNums) {
                        swap(currentpos, 2 * currentpos);
                        createMinHeap(2 * currentpos);
                    }

                } // This condition checks whether the node's execution time is equal to only its right child, it checks building number of the right child
                else if (n[currentpos].executed_time == n[2 * currentpos + 1].executed_time) {
                    //If building number of current node is greater than its right child, it swaps the node with right child.
                    if (n[currentpos].buildingNums > n[2 * currentpos + 1].buildingNums) {
                        swap(currentpos, 2 * currentpos + 1);
                        createMinHeap(2 * currentpos + 1);
                    }
                }
            }

            //Checks whether there is only one child of the node which is left child in below condition.
            if(n[2*currentpos+1] == null && n[2 * currentpos] != null) {
                // This condition checks whether the node's execution time is greater to its left child, it swaps the node with left child.
                if (n[currentpos].executed_time > n[2 * currentpos].executed_time) {
                    swap(currentpos, 2 * currentpos);
                    createMinHeap(2 * currentpos);
                } // This condition checks whether the node's execution time is equal to its left child.
                else if (n[currentpos].executed_time == n[2 * currentpos].executed_time) {
                    // This condition checks whether the node's building num is greater than its left child. It swaps with the left child on basis of building number.
                    if (n[currentpos].buildingNums > n[2 * currentpos].buildingNums) {
                        swap(currentpos, 2 * currentpos);
                        createMinHeap(2 * currentpos);
                    }
                }
            }
            //Checks whether there is only one child of the node which is right child in below condition.
            if(n[2*currentpos] == null && n[2 * currentpos+1] != null) {
                // This condition checks whether the node's execution time is greater to its right child, it swaps the node with right child.
                if (n[currentpos].executed_time > n[2 * currentpos+1].executed_time) {
                    swap(currentpos, 2 * currentpos+1);
                    createMinHeap(2 * currentpos+1);
                } // This condition checks whether the node's execution time is equal to its right child.
                else if (n[currentpos].executed_time == n[2 * currentpos+1].executed_time) {
                    // This condition checks whether the node's building num is greater than its right child. It swaps with the right child on basis of building number.
                    if (n[currentpos].buildingNums > n[2 * currentpos+1].buildingNums) {
                        swap(currentpos, 2 * currentpos+1);
                        createMinHeap(2 * currentpos+1);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
        int globalcounter = 1; // Global counter variable is incremented which keeps track on the overall number of days the work is done
        int resetcounter = 0; // Global counter variable is incremented which keeps track on the number of days the work is done on a particular building
        risingCity minheaparr = new risingCity(); // Creating an object of the main class where minheap is defined.
        RBTree rbt = new RBTree(); // Creating an object of the redB tree class where rbt code is defined.
        n[0] = new node(0,-999,0); // Initializing the node which is array of objects that defines the node structure.
        node b;

        BufferedReader objReader = null;

            String strCurrentLine;
            // Object reader reads the file and executes operation from each line based on input file.
            objReader = new BufferedReader(new FileReader( args[0]));
            // wirter object is used to write to the file
            BufferedWriter objWriter = new BufferedWriter(new FileWriter("output_file.txt"));
            // While loop runs until it executes every line in the file
            while ((strCurrentLine = objReader.readLine()) != null) {
                // For each lineThis loop checks the line which is read has a time which is less than global time.
                // If it is more than global time it should come out of the loop and read the next line.
                while (Integer.valueOf(strCurrentLine.substring(0, strCurrentLine.indexOf(':'))) >= globalcounter) {
                    // It checks the global counter with the value in the file. If it matches it check the command to be executed.
                    // If the command is print building it executes the print function in Red B tree.
                    if (Integer.valueOf(strCurrentLine.substring(0, strCurrentLine.indexOf(':'))) == globalcounter &&
                            strCurrentLine.substring(strCurrentLine.indexOf(':') + 2, strCurrentLine.indexOf(':') + 15).equals("PrintBuilding")) {
                        // Below checks whether print building command has 2 input parameters or 1.
                        // In case of 1 parameter strCurrentLine.indexOf(',') will return a -1 as "," will not be present in the print command.
                        // We will be calling print range method in RB Tree which has 2 input parameters to print the range in one line
                        if(strCurrentLine.indexOf(',')!=-1){
                            String node1 = rbt.PrintTree(Integer.valueOf(strCurrentLine.substring(strCurrentLine.indexOf("(")+1,strCurrentLine.indexOf(","))),
                                    Integer.valueOf(strCurrentLine.substring(strCurrentLine.indexOf(",")+1,strCurrentLine.indexOf(")"))));
                            if(node1.length() != 0){
                                node1 = node1.substring(0, node1.length() - 1);
                                node1 = node1 + "\n";
                                objWriter.write(node1);
                            }
                        } // This else statement will be called if "," is present in the print command of the file.
                        // We will be calling print range method in RB Tree which has 1 input parameters to print Building Number, Total Time and Executuion time of the current building
                        else{
                            String node1 = rbt.printBuilding(Integer.valueOf(strCurrentLine.substring(strCurrentLine.indexOf("(")+1,strCurrentLine.indexOf(")"))))+"\n";
                            if(node1.equals("null")){
                                objWriter.write("(0,0,0)\n");}
                            objWriter.write(node1);
                        }
                    }
                    // This condition is to check whether the building is complete or not.
                    if(minheaparr.size != 0 && n[1].total_time == n[1].executed_time){
                        b = new node(0,0,0);
                        b = n[1];
                        String node3 = "("+n[1].buildingNums+","+globalcounter+")\n";
                        objWriter.write(node3);
                        rbt.LastOut(2,b);
                        minheaparr.remove(globalcounter);
                        resetcounter = 0;

                    }// It checks the global counter with the value in the file. If it matches it check the command to be executed.
                    // If the command is insert it executes the insert function in min heap function
                    if (Integer.valueOf(strCurrentLine.substring(0, strCurrentLine.indexOf(':'))) == globalcounter &&
                            strCurrentLine.substring(strCurrentLine.indexOf(':') + 2, strCurrentLine.indexOf(':') + 8).equals("Insert")) {

                        b = new node(Integer.valueOf(strCurrentLine.substring(strCurrentLine.indexOf('(') + 1, strCurrentLine.indexOf(','))),0,
                                Integer.valueOf(strCurrentLine.substring(strCurrentLine.indexOf(',') + 1, strCurrentLine.indexOf(')'))));

                        minheaparr.insert(b);

                        rbt.LastOut(1,b);
                    }

                    //Executed time is incremented for a building until there is a building for which work is not complete.
                    if(minheaparr.size != 0) {
                        //If work is done on a building for 5 days, min heapify is done and the building with lower execution time is picked.
                        if (resetcounter == 5 && n[1].total_time != n[1].executed_time) {
                            minheaparr.minHeap();
                            n[1].executed_time += 1;
                            resetcounter = 0;
                        } else{
                            n[1].executed_time += 1;
                        }
                    }
                    globalcounter += 1; // Global counter is incremented each day by 1 in a loop
                    resetcounter += 1; // Global counter is incremented each day by 1 in a loop
                }
            }
            if(globalcounter %5 != 1) {
                n[1].executed_time -= globalcounter % 5;
                globalcounter -= globalcounter % 5;

            }

            while(n[1].executed_time!=n[1].total_time && minheaparr.size > 0) {
                // This condition checks whether number of days work has to be done on a building is less than 5. Executuion time will be updated accordingly.
                if (n[1].total_time - n[1].executed_time < 5) {
                    globalcounter += n[1].total_time - n[1].executed_time;
                    n[1].executed_time += (n[1].total_time - n[1].executed_time);
                    String x = "("+n[1].buildingNums+","+globalcounter+")\n";
                    objWriter.write(x);
                    minheaparr.remove(globalcounter);
                } // This condition checks whether number of days work has to be done on a building is equal to 5. Executuion time will be updated accordingly.
                else if(n[1].total_time - n[1].executed_time == 5){
                    n[1].executed_time += 5;
                    globalcounter += 5;
                    String y = "("+n[1].buildingNums+","+globalcounter+")\n";
                    objWriter.write(y);
                    minheaparr.remove(globalcounter);
                } // This condition checks whether number of days work has to be done on a building is greater than 5. Executuion time will be updated accordingly
                // and minheapify function will be called to get new building on top for construction
                else{
                    n[1].executed_time += 5;
                    globalcounter += 5;
                    minheaparr.minHeap();
                }
                if (minheaparr.size == 0)
                    break;
            }
            objWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}