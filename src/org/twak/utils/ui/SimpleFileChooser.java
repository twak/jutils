package org.twak.utils.ui;

import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author twak
 */
public abstract class SimpleFileChooser {

    static File currentFolder = new File(".");
    public SimpleFileChooser(JFrame root)
    {
        this (root, false, "please select file");
    }

    public SimpleFileChooser(JFrame root, boolean saveBehavour, String description )
    {
        this (root, saveBehavour, description, currentFolder, null);
    }

    public SimpleFileChooser(JFrame root, boolean saveBehavour, String description, File startFolder, String extension )
    {
        FileDialog fd = new FileDialog(root, description);


        fd.setMode(saveBehavour ? FileDialog.SAVE : FileDialog.LOAD);

        if (startFolder != null)
            fd.setDirectory(startFolder.getAbsolutePath());

        if (extension != null)
        	fd.setFilenameFilter( new FilenameFilter() {
				
				@Override
				public boolean accept( File dir, String name ) {
					return name.toLowerCase().endsWith( extension );
				}
			} );
        
        fd.setVisible(true);

        if (fd.getDirectory() == null)
        	return;
        
        File f = new File ( fd.getDirectory(), fd.getFile() );

        if (f.getParentFile() != null && startFolder == currentFolder)
            currentFolder = f.getParentFile();

        if (saveBehavour && f.exists() &&
            JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(root, "overwrite " + f.getName() + "?!") )
                return;

        try {
            heresTheFile(f);
        }
        catch  ( Throwable th ) {
            th.printStackTrace();
        }
//
//        System.out.println(selected);
//
//
//        JFileChooser jf = new JFileChooser(currentFolder);
//
//        int res = jf.showOpenDialog(root);
//
//        if (res == JFileChooser.APPROVE_OPTION)
//        {
//            currentFolder = jf.getCurrentDirectory();
//            try
//            {
//                heresTheFile(jf.getSelectedFile());
//            }
//            catch (Throwable ex)
//            {
//                JOptionPane.showMessageDialog( root, "Error operating on file", "Sorry", JOptionPane.ERROR_MESSAGE);
//                ex.printStackTrace();
//            }
//        }
    }
    public abstract void heresTheFile(File f) throws Throwable;
}
