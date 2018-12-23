package org.twak.utils;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Jz {
	public static void showOptionPane (Component frame, String message) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				JOptionPane.showMessageDialog( frame, message );
			}
		});
	}
}
