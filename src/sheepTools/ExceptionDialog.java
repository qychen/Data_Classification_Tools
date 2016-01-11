package sheepTools;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class ExceptionDialog extends JDialog implements ActionListener {

	private JButton okey = new JButton(" O K ");
	private JLabel label,l1;
	private ImageIcon err = new ImageIcon("./image/error.png");
	
	
	public ExceptionDialog(JFrame parent, String title, String content)
	{
		super(parent,title,true);
		label = new JLabel(content,JLabel.CENTER);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		JPanel p1 = new JPanel();
		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());
		l1 = new JLabel(err);
		l1.setSize(20,10);
		p2.add("West",l1);
		p2.add("Center",label);	
		p2.setSize(240,100);
		p1.add(okey);
		setLayout(new BorderLayout());
		add("Center",p2);
		add("South",p1);		
		okey.addActionListener(this);
		pack();
		setSize(260,130);
		setLocationRelativeTo(null);
		setVisible(true);
	}	
	
	@Override
	public void actionPerformed(ActionEvent e){
		setVisible(false);
	}

}
