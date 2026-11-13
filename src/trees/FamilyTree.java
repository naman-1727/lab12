package trees;

import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

public class FamilyTree 
{
    private static class TreeNode 
    {
        private String name;
        private TreeNode parent;
        private ArrayList<TreeNode> children;

        TreeNode(String name) {
            this.name = name;
            children = new ArrayList<>();
        }

        String getName() {
            return name;
        }

        void addChild(TreeNode childNode) 
        {
            children.add(childNode);
            childNode.parent = this;
        }

        // Searches subtree at this node for a node
        // with the given name. Returns the node, or null if not found.
        TreeNode getNodeWithName(String targetName) 
        {
            if (name.equals(targetName))
                return this;
                    
            // No, recurse. Check all children of this node.
            for (TreeNode child: children)
            {
                TreeNode result = child.getNodeWithName(targetName);
                if (result != null)
                    return result;
            }
            return null;
        }

        ArrayList<TreeNode> collectAncestorsToList() 
        {
            ArrayList<TreeNode> ancestors = new ArrayList<>();
            TreeNode node = parent;
            while (node != null) 
            {
                ancestors.add(node);
                node = node.parent;
            }
            return ancestors;
        }

        public String toString() {
            return toStringWithIndent("");
        }

        private String toStringWithIndent(String indent) {
            String s = indent + name + "\n";
            indent += "  ";
            for (TreeNode childNode : children)
                s += childNode.toStringWithIndent(indent);
            return s;
        }
    }

    private TreeNode root;

    //
    // Displays a file browser so that user can select the family tree file.
    //
    public FamilyTree() throws IOException, TreeException {
        // User chooses input file. This block doesn't need any work.
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Family tree text files", "txt");
        File dirf = new File("data");
        if (!dirf.exists()) dirf = new File(".");

        JFileChooser chooser = new JFileChooser(dirf);
        chooser.setFileFilter(filter);
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) System.exit(1);
        File treeFile = chooser.getSelectedFile();

        // Parse the input file. Create a FileReader that reads treeFile. Create a BufferedReader
        // that reads from the FileReader.
        FileReader fr = new FileReader(treeFile);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null)
            addLine(line);
        br.close();
        fr.close();
    }

    //
    // Line format is "parent:child1,child2 ..."
    // Throws TreeException if line is illegal.
    //
    private void addLine(String line) throws TreeException
    {
        int colonIndex = line.indexOf(':');
        if (colonIndex < 0)
            throw new TreeException("Missing colon in line: " + line);
        String parent =  line.substring(0, colonIndex);
        String childrenString = line.substring(colonIndex + 1);
        String[] childrenArray = childrenString.split(",");
        TreeNode parentNode;
        if (root == null)
            parentNode = root = new TreeNode(parent);
        else
        {
            parentNode = root.getNodeWithName(parent);
            if (parentNode == null)
                throw new TreeException("Parent not found: " + parent);
        }
        
        for (String child : childrenArray)
        {
            TreeNode childNode = new TreeNode(child);
            parentNode.addChild(childNode);
        }
    }
    TreeNode getMostRecentCommonAncestor(String name1, String name2) throws TreeException
    {

        TreeNode node1 = root.getNodeWithName(name1);        // node whose name is name1
        if (node1 == null)
            throw new TreeException("Name not found: " + name1);
        TreeNode node2 = root.getNodeWithName(name2);
        if (node2 == null)
            throw new TreeException("Name not found: " + name2);
        
        ArrayList<TreeNode> ancestorsOf1 = node1.collectAncestorsToList();
        ArrayList<TreeNode> ancestorsOf2 = node2.collectAncestorsToList();
        
        for (TreeNode n1: ancestorsOf1)
            if (ancestorsOf2.contains(n1))
                return n1;
        
        return null;
    }

    public String toString() 
    {
        return "Family Tree:\n\n" + root;
    }

    public static void main(String[] args) 
    {
        try 
        {
            FamilyTree tree = new FamilyTree();
            System.out.println("Tree:\n" + tree + "\n**************\n");
            TreeNode ancestor = tree.getMostRecentCommonAncestor("Bilbo", "Frodo");
            System.out.println("Most recent common ancestor of Bilbo and Frodo is " + ancestor.getName());
        } 
        catch (IOException x) 
        {
            System.out.println("IO trouble: " + x.getMessage());
        } catch (TreeException x) 
        {
            System.out.println("Input file trouble: " + x.getMessage());
        }
    }
}
