/*
 * ARX: Powerful Data Anonymization
 * Copyright (C) 2012 - 2014 Florian Kohlmayer, Fabian Prasser
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.deidentifier.arx.gui.view.impl.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * Supports interaction with the system clipboard
 * 
 * @author Fabian Prasser
 */
public class ClipboardHandlerTree {
    
    /** The viewer*/
    private final TreeViewer tree;

    /**
     * Creates a new instance
     * @param table
     */
    public ClipboardHandlerTree(TreeViewer tree){
        this.tree = tree;
    }
    
    /**
     * Copies the table's contents to the clipboard
     */
    public void copy(){
        if (tree != null && getItemCount(tree)>0) {
            Clipboard clipboard = new Clipboard(tree.getTree().getDisplay());
            TextTransfer textTransfer = TextTransfer.getInstance();
            clipboard.setContents(new String[]{getText(tree)}, 
                                  new Transfer[]{textTransfer});
            clipboard.dispose();
        }
    }
    
    /**
     * Creates a pop up menu for this handler
     * @param parent
     * @return
     */
    public Menu getMenu() {
        Menu menu = new Menu(tree.getTree());
        MenuItem itemCopy = new MenuItem(menu, SWT.NONE);
        itemCopy.setText("Copy");
        itemCopy.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent arg0) {
                copy();
            }
        });
        return menu;
    }
    
    /**
     * Access to tree viewer
     * @param tree
     * @param item
     * @return
     */
    private Object[] getChildren(TreeViewer tree, Object item){
        return ((ITreeContentProvider)tree.getContentProvider()).getChildren(item);
    }
    
    /**
     * Access to tree viewer
     * @param tree
     * @return
     */
    private TreeColumn[] getColumns(TreeViewer tree){
        return tree.getTree().getColumns();
    }
    
    /**
     * Returns the number of items
     * @param tree
     * @return
     */
    private int getItemCount(TreeViewer tree) {
        ITreeContentProvider provider = (ITreeContentProvider)tree.getContentProvider();
        return provider.getElements(null).length;
    }
    
    /**
     * Access to tree viewer
     * @param tree
     * @return
     */
    private Object[] getItems(TreeViewer tree) {
        return ((ITreeContentProvider)tree.getContentProvider()).getElements(null);
    }

    /**
     * Access to tree viewer
     * @param tree
     * @param item
     * @param index
     * @return
     */
    private String getLabel(TreeViewer tree, Object item, int index){
        return ((ITableLabelProvider)tree.getLabelProvider()).getColumnText(item, index);
    }

    /**
     * Renders the tree into a string
     * @param tree
     */
    private String getText(TreeViewer tree){
        
        StringBuilder builder = new StringBuilder();
       
        List<String> properties = new ArrayList<String>();
        for (TreeColumn column : getColumns(tree)){
            properties.add(column.getText());
        }
        
        for (Object item : getItems(tree)) {
            getText(tree, item, properties, builder, "");
        }
        
        return builder.toString();
    }
    
    /**
     * Renders an item and its children
     * @param tree
     * @param item
     * @param properties
     * @param builder
     * @param prefix
     */
    private void getText(TreeViewer tree, Object item, List<String> properties, StringBuilder builder, String prefix){
        if (builder.length() != 0) {
            builder.append("\n");
        }
        builder.append(prefix);
        int added = 0;
        for (int i=0; i<properties.size(); i++) {
            String value = getLabel(tree, item, i);
            if (value != null && !value.equals("")) {
                if (added!=0) {
                    builder.append(", ");
                }
                added++;
                builder.append(properties.get(i)).append(": ").append(value);
            }
        }
        
        for (Object child : getChildren(tree, item)) {
            getText(tree, child, properties, builder, prefix+"\t");
        }
    }
}
