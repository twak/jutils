package org.twak.utils.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicBorders;

public class FileDrop extends JLabel {

	public FileDrop(String label) {
		super (label);
		
		setPreferredSize( new Dimension( 200,80) );
		setHorizontalAlignment( SwingConstants.CENTER );
		setBackground( Color.gray );
		setBorder( new LineBorder( Color.black, 3 ) );
		
		setDropTarget(new DropTarget() {
		    public synchronized void drop(DropTargetDropEvent evt) {
		        try {
		            evt.acceptDrop(DnDConstants.ACTION_COPY);
		            List<File> droppedFiles = (List<File>)
		                evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
		            new Thread() {
		            	public void run() {
		            		for (File file : droppedFiles) {
		            			process (file);
		            		}
		            	};
		            }.start();
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        }
		    }
		});
	}
	
	public void process (File f) {
		
	}
}
