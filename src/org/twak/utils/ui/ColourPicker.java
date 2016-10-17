package org.twak.utils.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

public abstract class ColourPicker extends JFrame {

	JColorChooser chooser;
	
	public ColourPicker() {
		super ("pick a color");
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(chooser = new JColorChooser(), BorderLayout.CENTER);

		JButton ok = new JButton ("ok");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				picked (chooser.getColor() );
				ColourPicker.this.setVisible(false);
				ColourPicker.this.dispose();
			}
		});
		panel.add(ok, BorderLayout.SOUTH);
		
		setContentPane(panel);
		
//		addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosing(WindowEvent arg0) {
//				picked (chooser.getColor() );
//			}
//		});
		
		pack();
		setVisible(true);
	}
	
	public abstract void picked(Color color);
}
