package sheepTools;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.border.*;

import Jama.Matrix;



public class DataMiningTools extends JFrame implements ActionListener {

	private JButton b1 = new JButton("训练集");
	private JButton b2 = new JButton("测试集");
	private JButton b31 = new JButton("训练集文件格式");
	private JButton b32 = new JButton("测试集文件格式");
	private JButton b4 = new JButton(" 开  始  ");
	private JButton b5 = new JButton("查看结果");
	private JButton b6 = new JButton("性能评估");
	private JLabel l = new JLabel("分类挖掘工具");
	private JTextArea t1 = new JTextArea(17,20);
	private JTextArea t2 = new JTextArea(17,20);
	private JTextArea t3 = new JTextArea(10,35);
	private JScrollPane j1 = new JScrollPane(t1);
	private JScrollPane j2 = new JScrollPane(t2);
	private JScrollPane j3 = new JScrollPane(t3);
	private JRadioButton jb1 = new JRadioButton("Linear discriminant analysis", true);
	private JRadioButton jb2 = new JRadioButton("Quadratic discriminant analysis");
	private JRadioButton jb3 = new JRadioButton("Nearest neighbor classifier");
	private JRadioButton jb4 = new JRadioButton("Classification tree(CART)");
	private class WindowCloser extends WindowAdapter
	{
		public void windowClosing(WindowEvent we)
		{
			System.exit(0);
		}
	}
	private JFrame trainform = new JFrame();
	private JFrame testform = new JFrame();
	
	private JPanel p2,p3,p11;
	
	//tn是属性个数的下拉框
	private JComboBox tn = new JComboBox(new Object[]{"2","3","4","5","6","7","8","9","10","11","12","13","14","15"});
	private JTextField tk = new JTextField("3",5);
	

	// Integer.valueOf((String) this.tn.getSelectedItem()) 属性个数
	private JCheckBox[] Choice = new JCheckBox[16];
	
	private int kind=0;
	
	//CART中用到的参数
	private int nmin,num=0,nkind,ntr1;
	private double[][] atr1,atr2,q;
	private int ntree=0;
	private Treenode[] tree = new Treenode[2000];
	private int[] determine;
	
	private int sum=0; //total executing times
	
	private double missrate=0,timedelay=0;
	private long start=0,end=0;
	
	private void setup()
	{
		t1.setLineWrap(true);
		t1.setWrapStyleWord(true);
		t1.setEditable(false);
		t2.setLineWrap(true);
		t2.setWrapStyleWord(true);
		t2.setEditable(false);
		t3.setLineWrap(true);
		t3.setWrapStyleWord(true);
		
		ButtonGroup group = new ButtonGroup();
		group.add(jb1);
		group.add(jb2);
		group.add(jb3);
		group.add(jb4);
			
		l.setFont(new Font("Monospaced",Font.BOLD,25));
		
		// title
		JPanel p10 = new JPanel();
		p10.setLayout(new FlowLayout());
	    p10.add(l);
	
		JPanel p1 = new JPanel();
		p1.setLayout(new GridLayout(2,2));
		p1.add(jb1);p1.add(jb2);p1.add(jb3);p1.add(jb4);
		p1.setBorder(new TitledBorder("请选择分类方法"));
		
		p2 = new JPanel();
		p2.setLayout(new FlowLayout());
		p2.add(j3);
		p2.setBorder(new TitledBorder("请配置算法参数"));
		
		//方法+参数
		p3 = new JPanel();
		p3.setLayout(new BorderLayout());
		p3.add("North", p1); p3.add("Center", p2);
		
		//训练格式要求
		JPanel p91 = new JPanel();
		p91.setLayout(new FlowLayout(FlowLayout.CENTER,20,5));
		p91.add(b31);p91.add(b1);

		//训练集
		JPanel p4 = new JPanel();
		p4.setLayout(new BorderLayout());
		p4.add("North", p91);p4.add("Center", j1);
		
		//测试格式要求
		JPanel p92 = new JPanel();
		p92.setLayout(new FlowLayout(FlowLayout.CENTER,20,5));
		p92.add(b32);p92.add(b2);
		
		//测试集
		JPanel p5 = new JPanel();
		p5.setLayout(new BorderLayout());
		p5.add("North", p92);p5.add("Center", j2);
				
	    //输入数据框
		JPanel p7 = new JPanel();
		p7.setLayout(new GridLayout(1,2,10,5));
		p7.add(p4);p7.add(p5);
		p7.setBorder(new TitledBorder("请输入数据文件"));
		
		//下排三个按钮
		JPanel p6 = new JPanel();
		p6.setLayout(new FlowLayout(FlowLayout.CENTER,20,5));
		p6.add(b4);p6.add(b5);p6.add(b6);
				
		//输入+方法+参数
		JPanel p8 = new JPanel();
		p8.setLayout(new BorderLayout(5,5));
		p8.add("West", p3);p8.add("Center", p7);
		
		setLayout(new BorderLayout());
		add("North", p10);
		add("Center", p8);
		add("South", p6);
		
		p2.removeAll();
		
		JLabel l1 = new JLabel("投影矩阵特征向量数：C—");
		JLabel l2 = new JLabel("样本属性个数N：");
		JLabel l4 = new JLabel("（设定Attribute_N为类别属性）", SwingConstants.RIGHT);
	
		int i=0;
		this.tn.setSelectedItem("3");
		
		/*
		p11 = new JPanel();
		JButton b11 = new JButton("全选");
		JButton b22 = new JButton("反选");
		b11.setSize((int)(this.b6.getWidth()*5), (int)(this.b6.getHeight()*7));
		b22.setSize((int)(this.b6.getWidth()*5), (int)(this.b6.getHeight()*7));
		p11.setLayout(new FlowLayout());
		p11.add(b11);p11.add(b22);
		
		JPanel p22 = new JPanel();
		p22.setLayout(new GridLayout(0,3,5,5));
		for(i=1;i<=Integer.valueOf((String) this.tn.getSelectedItem());i++){
			this.Choice[i] = new JCheckBox("Attribute"+i);
			p22.add(this.Choice[i]);
		}
		this.Choice[1].setSelected(true);
		this.Choice[i-1].setEnabled(false);
					
		p3 = new JPanel();
		p3.setLayout(new BorderLayout());
		p3.add("Center",p22);p3.add("South",p11);
		
		Border b = new LineBorder(Color.black);
		p3.setBorder(new TitledBorder(b, "参与分类的属性"));
		*/
		
		p3 = new JPanel();
		p3.setLayout(new GridLayout(0,3,5,5));
		for(i=1;i<=Integer.valueOf((String) tn.getSelectedItem());i++){
			this.Choice[i] = new JCheckBox("Attribute"+i);
			p3.add(this.Choice[i]);
			this.Choice[i].setSelected(true);
		}
		this.Choice[Integer.valueOf((String) tn.getSelectedItem())].setSelected(false);
		this.Choice[Integer.valueOf((String) tn.getSelectedItem())].setEnabled(false);
		Border b = new LineBorder(Color.black);
		p3.setBorder(new TitledBorder(b, "参与分类的属性"));
		
		JPanel p111 = new JPanel();
		p111.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
		p111.add(l1);p111.add(this.tk);p111.add(l2);p111.add(this.tn);
		this.tk.setText("2");
		this.tn.setSelectedItem("3");
		
		p2.setLayout(new BorderLayout());
		p2.add("North", p111);
		p2.add("Center", p3);
		p2.add("South", l4);
		
		
		SwingUtilities.updateComponentTreeUI(p2);
	}
	
	public DataMiningTools()
	{
		super("Data Mining Tools");
		setup();
		b1.addActionListener(this);
		b2.addActionListener(this);
		b31.addActionListener(this);
		b32.addActionListener(this);
		b4.addActionListener(this);
		b5.addActionListener(this);
		b6.addActionListener(this);
		addWindowListener(new WindowCloser());
		jb1.addActionListener(this);
		jb2.addActionListener(this);
		jb3.addActionListener(this);
		jb4.addActionListener(this);
		tn.addActionListener(this);
		pack();
		setVisible(true);			
		this.kind=1;		
	}
	
	public void readFile(String file, JTextArea text, int flag)
	{
		text.setText("");
		try{
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line;
			int n=1;			
			if(flag==1){
				line = in.readLine();
				text.append("训练集样本个数： " + line + "\n");
				line = in.readLine();
				text.append("样本属性个数： " + line + "\n");
				while((line = in.readLine()) != null){
					if(n<10){
						text.append("r" + (n) + ":    " + line + "\n");
						n++;	
					}
					else{
						text.append("r" + (n) + ":  " + line + "\n");
						n++;	
					}
				}			
			}
			else{
				line = in.readLine();
				text.append("测试集样本个数： " + line + "\n");
				while((line = in.readLine()) != null){
					if(n<10){
						text.append("s" + (n) + ":    " + line + "\n");
						n++;	
					}
					else{
						text.append("s" + (n) + ":  " + line + "\n");
						n++;	
					}
				}		
			}
			in.close();
	    	text.setLineWrap(true);      //激活自动换行功能 
	    	text.setWrapStyleWord(true);
	    	text.setCaretPosition(0);
		} catch(IOException ioe){
			System.err.println(ioe);
		}
	}
	
	public File ftrain,ftest;
	public File fresult = new File("./user/Result.txt");
	public File fevaluation = new File("./user/Evaluation.txt");
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == b1)
		{
			
			FileDialog fd1 = new FileDialog(this,"打开训练集",FileDialog.LOAD);
			fd1.setVisible(true);
			if(fd1.getFile()!=null){
				ftrain = new File(fd1.getDirectory()+fd1.getFile());
				if(ftrain.exists())
				{
					readFile(ftrain.toString(), t1, 1);
				}
			}
			Scanner intr;
			if(fd1.getFile()!=null)
			try {
				intr = new Scanner(this.ftrain);
				this.num= intr.nextInt();
				this.num= intr.nextInt();
				this.tn.setSelectedIndex(num-2);
			} catch (FileNotFoundException e1) {
			}
		}
		else if(e.getSource() == b2)
		{
			FileDialog fd2 = new FileDialog(this,"打开测试集",FileDialog.LOAD);
			fd2.setVisible(true);
			if(fd2.getFile()!=null){
				ftest = new File(fd2.getDirectory()+fd2.getFile());
				if(ftest.exists())
				{
					readFile(ftest.toString(), t2, 0);
				}
			}
		}
		else if(e.getSource() == b5)
		{			
			if(sum!=0){
			try {
				JFrame showres = new JFrame();
				JTextArea texttr = new JTextArea();
				JScrollPane jtr = new JScrollPane(texttr);
				BufferedReader in1 = new BufferedReader(new FileReader("./user/Result.txt"));
				String linetr;
		    	while ((linetr = in1.readLine()) != null)
		    		texttr.append(linetr + "\n");
		    	in1.close();
		    	texttr.setLineWrap(true);      //激活自动换行功能 
		    	texttr.setWrapStyleWord(true);
		    	showres.setVisible(true);
		    	showres.setSize((int) (this.getWidth()*0.5), this.getHeight());
		    	showres.setLocation(this.getX(), this.getY());
		    	showres.add(jtr);
		    	texttr.setEditable(false);		    	
			} catch (IOException e1) {
				ExceptionDialog d1 = new ExceptionDialog(this,"Exception","请先点击“开始”执行分类算法！");
			}
			}
			else {
				ExceptionDialog d1 = new ExceptionDialog(this,"Exception","请先点击“开始”执行分类算法！");
			}
		}
		else if(e.getSource() == b6)
		{			
			if(sum!=0){
			try {
				JTextArea texttr = new JTextArea();
				JFrame evaluation = new JFrame();
				JScrollPane jtr = new JScrollPane(texttr);
				BufferedReader in1 = new BufferedReader(new FileReader("./user/Evaluation.txt"));
				String linetr;
		    	while ((linetr = in1.readLine()) != null)
		    		texttr.append(linetr + "\n");
		    	in1.close();
		    	texttr.setLineWrap(true);      //激活自动换行功能 
		    	texttr.setWrapStyleWord(true);
		    	evaluation.setVisible(true);
		    	evaluation.setSize((int) (this.getWidth()*0.5), this.getHeight());
		    	evaluation.setLocation((int)(this.getX()+getWidth()*0.5), this.getY());
		    	evaluation.add(jtr);
		    	texttr.setEditable(false);		    	
			} catch (IOException e1) {
				ExceptionDialog d1 = new ExceptionDialog(this,"Exception","请先点击“开始”执行分类算法！");
			}
			}
			else {
				ExceptionDialog d1 = new ExceptionDialog(this,"Exception","请先点击“开始”执行分类算法！");
			}
		}
		else if(e.getSource() == b31)
		{
			
			try {
				JTextArea texttr = new JTextArea();
				JScrollPane jtr = new JScrollPane(texttr);
				BufferedReader in1 = new BufferedReader(new FileReader("./user/TrainForm.txt"));
				String linetr;
		    	while ((linetr = in1.readLine()) != null)
		    		texttr.append(linetr + "\n");
		    	in1.close();
		    	texttr.setCaretPosition(0);
		    	trainform.setVisible(true);
		    	trainform.setSize((int) (this.getWidth()*0.5), this.getHeight());
		    	trainform.setLocationRelativeTo(this);			
		    	trainform.add(jtr);
		    	texttr.setEditable(false);
			} catch (IOException e1) {
				ExceptionDialog d1 = new ExceptionDialog(this,"Exception","缺少样例格式文件！");
			}
		}
		else if(e.getSource() == b32)
		{

			try {
				JTextArea textts = new JTextArea();
				JScrollPane jts = new JScrollPane(textts);
				BufferedReader in2 = new BufferedReader(new FileReader("./user/TestForm.txt"));
				String linets;
		    	while ((linets = in2.readLine()) != null)
		    		textts.append(linets + "\n");
		    	in2.close();
		    	textts.setCaretPosition(0);
		    	testform.setVisible(true);
		    	testform.setSize((int) (this.getWidth()*0.5), this.getHeight());
		    	testform.setLocationRelativeTo(this);			
		    	testform.add(jts);
		    	textts.setEditable(false);			
			} catch (IOException e2) {
				ExceptionDialog d1 = new ExceptionDialog(this,"Exception","缺少样例格式文件！");
			}		
		}
		//k-Closer
		else if(e.getSource() == jb3)
		{
			this.kind=3;
			this.p2.removeAll();
			
			JLabel l1 = new JLabel("临近集大小K：");
			JLabel l2 = new JLabel("样本属性个数N：");
			JLabel l4 = new JLabel("（设定Attribute_N为类别属性）", SwingConstants.RIGHT);
		
			int i=0;
			this.p3 = new JPanel();
			this.p3.setLayout(new GridLayout(0,3,5,5));
			for(i=1;i<=Integer.valueOf((String) this.tn.getSelectedItem());i++){
				this.Choice[i] = new JCheckBox("Attribute"+i);
				this.p3.add(this.Choice[i]);
				this.Choice[i].setSelected(true);
			}
			this.Choice[i-1].setEnabled(false);
			this.Choice[i-1].setSelected(false);
			Border b = new LineBorder(Color.black);
			this.p3.setBorder(new TitledBorder(b, "参与分类的属性"));
			
			JPanel p1 = new JPanel();
			p1.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
			p1.add(l1);p1.add(this.tk);p1.add(l2);p1.add(this.tn);
			if(num!=0)
				this.tn.setSelectedItem(num-2);
			else
				this.tn.setSelectedItem("3");			
			SwingUtilities.updateComponentTreeUI(tn);
			
			this.tk.setText("3");
			
			this.p2.setLayout(new BorderLayout());
			this.p2.add("North", p1);
			this.p2.add("Center", this.p3);
			this.p2.add("South", l4);
			
			SwingUtilities.updateComponentTreeUI(p2);
			
		}
		//CART
		else if(e.getSource() == jb4)
		{
			this.kind=4;
			this.p2.removeAll();
			
			JLabel l1 = new JLabel("样本数最小值N_Min：");
			JLabel l2 = new JLabel("样本属性个数N：");
			JLabel l4 = new JLabel("（设定Attribute_N为类别属性）", SwingConstants.RIGHT);
		
			int i=0;
			this.p3 = new JPanel();
			this.p3.setLayout(new GridLayout(0,3,5,5));
			for(i=1;i<=Integer.valueOf((String) this.tn.getSelectedItem());i++){
				this.Choice[i] = new JCheckBox("Attribute"+i);
				this.p3.add(this.Choice[i]);
				this.Choice[i].setSelected(true);
			}
			this.Choice[i-1].setEnabled(false);
			this.Choice[i-1].setSelected(false);
			Border b = new LineBorder(Color.black);
			this.p3.setBorder(new TitledBorder(b, "参与分类的属性"));
			
			JPanel p1 = new JPanel();
			p1.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
			p1.add(l1);p1.add(this.tk);p1.add(l2);p1.add(this.tn);
			if(num!=0)
				this.tn.setSelectedItem(num-2);
			else
				this.tn.setSelectedItem("3");			
			SwingUtilities.updateComponentTreeUI(tn);
			this.tk.setText("3");
			
			this.p2.setLayout(new BorderLayout());
			this.p2.add("North", p1);
			this.p2.add("Center", this.p3);
			this.p2.add("South", l4);
			
			SwingUtilities.updateComponentTreeUI(p2);
			
		}
		//LDA
		else if(e.getSource() == jb1)
		{
			this.kind=1;
			this.p2.removeAll();
			
			JLabel l1 = new JLabel("投影矩阵特征向量数：C—");
			JLabel l2 = new JLabel("样本属性个数N：");
			JLabel l4 = new JLabel("（设定Attribute_N为类别属性）", SwingConstants.RIGHT);
		
			int i=0;
			this.p3 = new JPanel();
			this.p3.setLayout(new GridLayout(0,3,5,5));
			for(i=1;i<=Integer.valueOf((String) this.tn.getSelectedItem());i++){
				this.Choice[i] = new JCheckBox("Attribute"+i);
				this.p3.add(this.Choice[i]);
				this.Choice[i].setSelected(true);
			}
			this.Choice[i-1].setEnabled(false);
			this.Choice[i-1].setSelected(false);
			Border b = new LineBorder(Color.black);
			this.p3.setBorder(new TitledBorder(b, "参与分类的属性"));
			
			JPanel p1 = new JPanel();
			p1.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
			p1.add(l1);p1.add(this.tk);p1.add(l2);p1.add(this.tn);
			if(num!=0)
				this.tn.setSelectedItem(num-2);
			else
				this.tn.setSelectedItem("3");			
			SwingUtilities.updateComponentTreeUI(tn);
			
			this.tk.setText("2");
			this.p2.setLayout(new BorderLayout());
			this.p2.add("North", p1);
			this.p2.add("Center", this.p3);
			this.p2.add("South", l4);
			
			SwingUtilities.updateComponentTreeUI(p2);
			
		}
		//QDA
		else if(e.getSource() == jb2)
		{
			this.kind=2;
			this.p2.removeAll();
			
			JLabel l2 = new JLabel("样本属性个数N：");
			JLabel l4 = new JLabel("（设定Attribute_N为类别属性）", SwingConstants.RIGHT);
		
			int i=0;
			this.p3 = new JPanel();
			this.p3.setLayout(new GridLayout(0,3,5,5));
			for(i=1;i<=Integer.valueOf((String) this.tn.getSelectedItem());i++){
				this.Choice[i] = new JCheckBox("Attribute"+i);
				this.p3.add(this.Choice[i]);
				this.Choice[i].setSelected(true);
			}
			this.Choice[i-1].setEnabled(false);
			this.Choice[i-1].setSelected(false);
			Border b = new LineBorder(Color.black);
			this.p3.setBorder(new TitledBorder(b, "参与分类的属性"));
			
			JPanel p1 = new JPanel();
			p1.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
			p1.add(l2);p1.add(this.tn);
			if(num!=0)
				this.tn.setSelectedItem(num-2);
			else
				this.tn.setSelectedItem("3");			
			SwingUtilities.updateComponentTreeUI(tn);
			
			this.tk.setText("2");
			this.p2.setLayout(new BorderLayout());
			this.p2.add("North", p1);
			this.p2.add("Center", this.p3);
			this.p2.add("South", l4);
			
			SwingUtilities.updateComponentTreeUI(p2);
			
		}
	
		else if(e.getSource() == tn){
			int i = Integer.valueOf((String) this.tn.getSelectedItem()), j=0;
			this.p3.removeAll();
			this.p3.setLayout(new GridLayout(0,3,5,5));
			for(j=1;j<=i;j++){
				this.Choice[j] = new JCheckBox("Attribute"+j);
				this.p3.add(this.Choice[j]);
				this.Choice[j].setSelected(true);
			}	
			this.Choice[i].setSelected(false);
			this.Choice[i].setEnabled(false);
			Border b = new LineBorder(Color.black);
			this.p3.setBorder(new TitledBorder(b, "参与分类的属性"));
			
			SwingUtilities.updateComponentTreeUI(p3);		
		}
		else if(e.getSource() == b4){		
				try{
					start = System.currentTimeMillis();
					switch(this.kind){
				case 1:{
					this.LDA();
					break;}
				case 2:{
					this.QDA();
					break;}
				case 3:{
					this.KCloser();
					break;}
				case 4:{
					this.CART();
					break;}				
					}
				}
				catch(FileNotFoundException we)
				{
					ExceptionDialog d1 = new ExceptionDialog(this,"Exception","缺少样例格式文件！");
				} 
			}
		
	}
	
	
	public void ShowResult(int[] r, int n)
	{	
		int i=0,j=0;
		end = System.currentTimeMillis();
		try {
			int[] dis = new int[201];
			for(i=0;i<=200;i++)
				dis[i]=0;
			for(i=1;i<=n;i++)
				dis[r[i]]++;			
			sum++;
			if(sum==1){
				FileWriter out1 = new FileWriter(fresult);
				out1.write("分类挖掘开始！\r\n");
				out1.write("\r\n");
				out1.write("第"+(sum)+"次分类： \r\n");
				out1.write("  分类方法： ");
				switch(kind){
				case 1: out1.write("Linear discriminant analysis\r\n");break;
				case 2: out1.write("Quadratic discriminant analysis\r\n");break;
				case 3: out1.write("Nearest neighbor classifier\r\n");break;
				case 4: out1.write("Classification tree(CART)\r\n");break;
				}
				out1.write("  训练集： "+ftrain.getName()+"\r\n");
				out1.write("  测试集： "+ftest.getName()+"\r\n");
				out1.write("  参与分类属性： ");
				for(i=1;i<num;i++)
					if(determine[i]==1)
						out1.write("Attr"+(i)+" ");
				out1.write("\r\n");
				out1.write("  分类结果： \r\n");
				for(i=0;i<=200;i++)
					if(dis[i]!=0){
						out1.write("    ");
						out1.write("Class "+(i)+" : ");
						for(j=1;j<=n;j++)
							if(r[j]==i)
							{
								out1.write("s"+(j));
								out1.write(" ");
								out1.flush();
							}		
						out1.write("\r\n");
					}				
		
				out1.write("\r\n");
				out1.write("-------------------------------------------------------------------");
				out1.write("\r\n");
				out1.close();
			}
			else{
				FileWriter out1 = new FileWriter(fresult,true);	
				out1.write("\r\n");
				out1.write("第"+(sum)+"次分类： \r\n");
				out1.write("  分类方法： ");
				switch(kind){
				case 1: out1.write("Linear discriminant analysis\r\n");break;
				case 2: out1.write("Quadratic discriminant analysis\r\n");break;
				case 3: out1.write("Nearest neighbor classifier\r\n");break;
				case 4: out1.write("Classification tree(CART)\r\n");break;
				}
				out1.write("  训练集： "+ftrain.getName()+"\r\n");
				out1.write("  测试集： "+ftest.getName()+"\r\n");
				out1.write("  参与分类属性： ");
				for(i=1;i<num;i++)
					if(determine[i]==1)
						out1.write("Attr"+(i)+" ");
				out1.write("\r\n");
				out1.write("  分类结果： \r\n");
				for(i=0;i<=200;i++)
					if(dis[i]!=0){
						out1.write("    ");
						out1.write("Class "+(i)+" : ");
						for(j=1;j<=n;j++)
							if(r[j]==i)
							{
								out1.write("s"+(j));
								out1.write(" ");
								out1.flush();
							}		
						out1.write("\r\n");
					}				
		
				out1.write("\r\n");
				out1.write("-------------------------------------------------------------------");
				out1.write("\r\n");
				out1.close();
			}
		}
		catch (FileNotFoundException e) {
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入格式错误，请参照样例检查！");
		} catch (IOException e) {
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入格式错误，请参照样例检查！");
		}		
	}

	public void Evaluation(int[] r, int n, double[][] ats)
	{	
		int i=0,j=0,k=0;
		try {
			int[] dis = new int[201];
			if(sum==1){
				for(i=0;i<=200;i++)
					dis[i]=0;
				for(i=1;i<=n;i++)
					dis[r[i]]++;	
				FileWriter out1 = new FileWriter(fevaluation);
				out1.write("分类挖掘开始！\r\n");
				out1.write("\r\n");
				out1.write("第"+(sum)+"次分类： \r\n");
				out1.write("  分类方法： ");
				switch(kind){
				case 1: out1.write("Linear discriminant analysis\r\n");break;
				case 2: out1.write("Quadratic discriminant analysis\r\n");break;
				case 3: out1.write("Nearest neighbor classifier\r\n");break;
				case 4: out1.write("Classification tree(CART)\r\n");break;
				}
				out1.write("  分类结果统计： \r\n");
				k=0;
				for(i=0;i<=200;i++)
					if(dis[i]!=0){
						k=0;
						out1.write("    ");
						out1.write("Class "+(i)+" : ");
						for(j=1;j<=n;j++)
							if(r[j]==i)
								k++;
						out1.write((k)+" samples\r\n");
					}	
				k=0;
				for(i=0;i<=200;i++)
					dis[i]=0;
				for(i=1;i<=n;i++)
					dis[(int) ats[i][num]]++;		
				out1.write("  正确结果统计： \r\n");
				for(i=0;i<=200;i++)
					if(dis[i]!=0){
						k=0;
						out1.write("    ");
						out1.write("Class "+(i)+" : ");
						for(j=1;j<=n;j++)
							if((int) ats[j][num]==i)
								k++;
						out1.write((k)+" samples\r\n");
					}	
				out1.write("  结果评估： \r\n");
				double miss=0;
				double delay=end-start;
				for(i=1;i<=n;i++)
					if(r[i]!=ats[i][num])
						miss+=1;
				miss = miss/(double) n;
				BigDecimal miss0 = new BigDecimal(miss);
				miss = miss0.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				missrate = (missrate*(sum-1)+miss)/(double) sum;
				miss0 = new BigDecimal(missrate);
				missrate = miss0.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				timedelay = (timedelay*(sum-1)+delay)/(double) sum;
				miss0 = new BigDecimal(timedelay);
				timedelay = miss0.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				out1.write("    错误率:     "+(miss*100)+"%\r\n");
				out1.write("    分类耗时: "+(delay)+"ms\r\n");
				
			/*	out1.write("  与前"+(sum-1)+"次分类相比： \r\n");
				if(miss>missrate)
					out1.write("    错误率"+" 高 "+((miss-missrate)*100)+"%\r\n");
				else if(miss<missrate) 
					out1.write("    错误率"+" 低 "+((missrate-miss)*100)+"%\r\n");
				else out1.write("    正确率相等\r\n");
				if(delay>timedelay)
					out1.write("    耗时"+" 多 "+(delay-timedelay)+"ms\r\n");
				else if(delay<timedelay) 
					out1.write("    耗时"+" 少 "+(timedelay-delay)+"ms\r\n");
				else out1.write("    耗时相等\r\n");					
			*/
				out1.write("\r\n");
				out1.write("-------------------------------------------------------------------");
				out1.write("\r\n");
				out1.close();
			}
			else{
				for(i=0;i<=200;i++)
					dis[i]=0;
				for(i=1;i<=n;i++)
					dis[r[i]]++;	
				FileWriter out1 = new FileWriter(fevaluation,true);
				out1.write("\r\n");
				out1.write("第"+(sum)+"次分类： \r\n");
				out1.write("  分类方法： ");
				switch(kind){
				case 1: out1.write("Linear discriminant analysis\r\n");break;
				case 2: out1.write("Quadratic discriminant analysis\r\n");break;
				case 3: out1.write("Nearest neighbor classifier\r\n");break;
				case 4: out1.write("Classification tree(CART)\r\n");break;
				}
				k=0;
				out1.write("  分类结果： \r\n");
				for(i=0;i<=200;i++)
					if(dis[i]!=0){
						k=0;
						out1.write("    ");
						out1.write("Class "+(i)+" : ");
						for(j=1;j<=n;j++)
							if(r[j]==i)
								k++;
						out1.write((k)+" samples\r\n");
					}	
				k=0;
				for(i=0;i<=200;i++)
					dis[i]=0;
				for(i=1;i<=n;i++)
					dis[(int) ats[i][num]]++;		
				out1.write("  正确结果： \r\n");
				for(i=0;i<=200;i++)
					if(dis[i]!=0){
						k=0;
						out1.write("    ");
						out1.write("Class "+(i)+" : ");
						for(j=1;j<=n;j++)
							if((int) ats[j][num]==i)
								k++;
						out1.write((k)+" samples\r\n");
					}	
				out1.write("  结果评估： \r\n");
				double miss=0;
				double delay=end-start;
				for(i=1;i<=n;i++)
					if(r[i]!=ats[i][num])
						miss+=1;
				miss = miss/(double) n;
				BigDecimal miss0 = new BigDecimal(miss);
				miss = miss0.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				
				missrate = (missrate*(sum-1)+miss)/(double) sum;
				miss0 = new BigDecimal(missrate);
				missrate = miss0.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				
				timedelay = (timedelay*(sum-1)+delay)/(double) sum;
				miss0 = new BigDecimal(timedelay);
				timedelay = miss0.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				
				out1.write("    错误率:     "+(miss*100)+"%\r\n");
				out1.write("    分类耗时: "+(delay)+"ms\r\n");
				
				out1.write("  与前"+(sum-1)+"次分类相比： \r\n");
				miss=miss-missrate;
				miss=miss*100;
				miss0 = new BigDecimal(miss);
				miss = miss0.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				
				delay=delay-timedelay;
				delay=delay/timedelay;
				delay=delay*100;
				miss0 = new BigDecimal(delay);
				delay = miss0.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				if(miss>0)
					out1.write("    错误率"+" 高 "+(miss)+"%\r\n");
				else if(miss<0) {
					miss=miss*-1;
					miss0 = new BigDecimal(miss);
					miss = miss0.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
					out1.write("    错误率"+" 低 "+(miss)+"%\r\n");					
				}
				else out1.write("    错误率 相等\r\n");
				if(delay>0)
					out1.write("    耗时  "+"   多 "+(delay)+"%\r\n");
				else if(delay<0) {
					delay=delay*-1;
					miss0 = new BigDecimal(delay);
					delay = miss0.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
					out1.write("    耗时  "+"   少 "+(delay)+"%\r\n");
				}
				else out1.write("    耗时     相等\r\n");					
			
				out1.write("\r\n");
				out1.write("-------------------------------------------------------------------");
				out1.write("\r\n");
				out1.close();
			}
		}
		catch (FileNotFoundException e) {
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入格式错误，请参照样例检查！");
		} catch (IOException e) {
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入格式错误，请参照样例检查！");
		}		
	}
	
	public void KCloser() throws FileNotFoundException
	{
		try{
		Scanner intr = new Scanner(this.ftrain);
		Scanner ints = new Scanner(this.ftest);
		int ntr = intr.nextInt();
		int nts = ints.nextInt(); // number of set
		int i=0,j=0,k=0,ii=0,max=0;  // num=attributes number
		
		num= intr.nextInt();
		double[][] atr=new double[ntr+1][num+1] ,ats=new double[nts+1][num+1]; 
		double[] t = new double[num+1];
		int[] result = new int[nts+1];
		
		//获取参数：k  num  determine[]  
			
		determine = new int[num+1];
		if((ntr<1500)&&(nts<1500)){
		k=Integer.valueOf((String) this.tk.getText());
		for(i=1;i<num;i++)
			if(this.Choice[i].isSelected())
				determine[i]=1;
			else determine[i]=0;	
		
		//atr的第0列存放dis，表示样本和这个元组的距离；ats第0列存放其分类结果 第num列存放其实际的类别用于测试
		for(i=1;i<=ntr;i++)
			for(j=1;j<=num;j++)
				atr[i][j] = intr.nextDouble();
		for(i=1;i<=nts;i++)
			for(j=1;j<=num;j++)
				ats[i][j] = ints.nextDouble();
		/*
		//将所有属性值都划到【0，1】的范围内
		double m1=0,m2=0;
		for(i=1;i<num;i++){
			m1=atr[1][i];
			m2=atr[1][i];
			for(j=2;j<=ntr;j++)
				if(atr[j][i]>m1) m1=atr[j][i];
				else if(atr[j][i]<m2) m2=atr[j][i];
			for(j=1;j<=nts;j++)
				if(ats[j][i]>m1) m1=ats[j][i];
				else if(ats[j][i]<m2) m2=ats[j][i];
			for(j=1;j<=ntr;j++)
				atr[j][i]=(atr[j][i]-m2)/(m1-m2);
			for(j=1;j<=nts;j++)
				ats[j][i]=(ats[j][i]-m2)/(m1-m2);		
		}		
		*/
		if(intr.hasNextDouble()||ints.hasNextDouble()) {
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入格式错误，请参照样例检查！");
		}
		else{
		intr.close();ints.close();
		
		for(ii=1;ii<=nts;ii++){ //第ii个测试集元素
			for(i=1;i<=ntr;i++)
				atr[i][0]=0;
			for(i=1;i<=k;i++){
				for(j=1;j<num;j++)
					if(determine[j] == 1) 
						atr[i][0] += Math.pow((atr[i][j]-ats[ii][j]), 2);
			}
			for(i=k+1;i<=ntr;i++){
				for(j=1;j<num;j++)
					if(determine[j] == 1) 
						atr[i][0] += Math.pow((atr[i][j]-ats[ii][j]), 2);
				for(j=1;j<=k;j++)
					if(atr[i][0]<atr[j][0])
					{t=atr[i];atr[i]=atr[j];atr[j]=t;}
			}
			max=0;
			for(i=1;i<=num;i++)
				result[i]=0;
			for(i=1;i<=k;i++){
				result[(int) atr[i][num]]++;
				if(result[(int) atr[i][num]]>max) {
					max=result[(int) atr[i][num]];
					ats[ii][0]=atr[i][num];
				}
			}	
		}	
		
		for(i=1;i<=nts;i++)
			result[i]=(int) ats[i][0];
		this.ShowResult(result, nts);
		this.Evaluation(result, nts, ats);
		}
		}
		else {
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入集样本数超界！(n<100)");
		}
		}
		catch(FileNotFoundException we){
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","缺少输入文件！");
		}
		catch(NullPointerException we){
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","缺少输入文件！");
		}
		catch(InputMismatchException we){
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入格式错误，请参照样例检查！");
		}
		catch(NoSuchElementException we){
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入格式错误，请参照样例检查！");
		}
		catch(ArrayIndexOutOfBoundsException we){
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入格式错误，请参照样例检查！");
		}
	}
	
	public void CART() throws FileNotFoundException
	{
		tree = new Treenode[2000];
		try{
		Scanner intr = new Scanner(this.ftrain);
		Scanner ints = new Scanner(this.ftest);
		int ntr = intr.nextInt();
		int nts = ints.nextInt(); // number of set
		num= intr.nextInt(); // num=attributes number
		int i=0,j=0,k=0;
		double t=0;
		
		if((ntr<1500)&&(nts<1500)){
		determine = new int[num+1];
		int[] result = new int[nts+1];
		double[][] atr= new double[ntr+1][num+1],ats= new double[nts+1][num+1];
		atr1= new double[ntr+1][num+1];
		atr2= new double[ntr+1][num+1]; 
		q = new double[num+1][ntr+1];  //q为属性问题矩阵
		
		//获取参数：  nmin    determine  
		this.nmin =Integer.valueOf((String) this.tk.getText());  //样本最小值
		for(i=1;i<num;i++)
			if(this.Choice[i].isSelected())
				determine[i]=1;
			else determine[i]=0;	
		
		ntree=0;
		//输入atr和ats，并划分atr为两部分：atr1+atr2，统计类别个数
		for(i=1;i<=ntr;i++)
			for(j=1;j<=num;j++)
				atr[i][j] = intr.nextDouble(); //训练集
		for(i=1;i<=nts;i++)
			for(j=1;j<=num;j++)
				ats[i][j] = ints.nextDouble();  //测试集
		/*
		//将所有属性值都划到【0，1】的范围内
		double m1=0,m2=0;
		for(i=1;i<num;i++){
			m1=atr[1][i];
			m2=atr[1][i];
			for(j=2;j<=ntr;j++)
				if(atr[j][i]>m1) m1=atr[j][i];
				else if(atr[j][i]<m2) m2=atr[j][i];
			for(j=1;j<=nts;j++)
				if(ats[j][i]>m1) m1=ats[j][i];
				else if(ats[j][i]<m2) m2=ats[j][i];
			for(j=1;j<=ntr;j++)
				atr[j][i]=(atr[j][i]-m2)/(m1-m2);
			for(j=1;j<=nts;j++)
				ats[j][i]=(ats[j][i]-m2)/(m1-m2);		
		}		
		*/
		if(intr.hasNextDouble()||ints.hasNextDouble()) {
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入格式错误，请参照样例检查！");
		}
		else{
		intr.close();ints.close();
		
		ntr1=(int) (ntr*0.7);
		int ntr2=ntr-ntr1;
		int[] aa = new int[2101];
		
		for(i=1;i<=100;i++)
			aa[i]=0;
		for(i=1;i<=ntr;i++)
			aa[(int) atr[i][num]]++;  //aa为训练集样本类别分布的统计
		nkind=0;  //类别个数
		for(i=1;i<=ntr;i++)
			if(aa[i]!=0) nkind++;
		
		for(i=1;i<=ntr1;i++)
			for(j=1;j<=num;j++)
				atr1[i][j] = atr[i][j];  //构建树集
		for(i=1;i<=ntr2;i++)
			for(j=1;j<=num;j++)
				atr2[i][j] = atr[i+ntr1][j];  //剪枝集
		
		//初始化：建立问题矩阵q 
		for(i=1;i<num;i++){
			for(j=1;j<=ntr1;j++)
				q[i][j]=atr1[j][i];
			for(j=1;j<ntr1;j++)
				for(k=j+1;k<=ntr1;k++)
					if(q[i][k]<q[i][j]){
						t=q[i][k];q[i][k]=q[i][j];q[i][j]=t;
					}
			for(j=1;j<ntr1;j++)
				q[i][j]=(q[i][j]+q[i][j+1])*0.5;	
		}
		
		// 初始化决策树tree
		tree[0] = new Treenode();
		tree[0].set = new int[ntr1+1];
		for(i=1;i<=ntr1;i++)
			tree[0].set[i]=i;
		tree[0].set[0]=ntr1;
		
		//调用划分方法
		this.CART_Split(tree[0]);		
		
		//剪枝优化CART_Optimize()

		Treenode[][] treecan = new Treenode[ntree+1][ntree+1];
		Treenode[] tree0 = new Treenode[ntree+1];
		for(i=0;i<=ntree;i++){
			treecan[0][i]= new Treenode();
			treecan[0][i].tr=tree[i].tr;
			treecan[0][i].tl=tree[i].tl;
			treecan[0][i].x=tree[i].x;
			treecan[0][i].qq=tree[i].qq;
			treecan[0][i].itt=tree[i].itt;
			treecan[0][i].state=tree[i].state;
			treecan[0][i].alpha=tree[i].alpha;
			treecan[0][i].miss=tree[i].miss;
			treecan[0][i].set = new int[tree[i].set[0]+1];
			for(j=1;j<=tree[i].set[0];j++)
				treecan[0][i].set[j]=tree[i].set[j];
		}
		
		int ncan=0,miss0=0,nn=0;
		int[] dis,e;
		double alpha0=1;
		double[] alpha = new double[ntree+1];
		for(i=0;i<=ntree;i++) alpha[i]=1;
		while(treecan[ncan][0].state==0)		//到只有根节点为止
		{ 				

			//选择alpha最小的节点
			
			for(i=0;i<=ntree;i++){
				tree0[i]= new Treenode();
				tree0[i].tr=treecan[ncan][i].tr;
				tree0[i].tl=treecan[ncan][i].tl;
				tree0[i].x=treecan[ncan][i].x;
				tree0[i].qq=treecan[ncan][i].qq;
				tree0[i].itt=treecan[ncan][i].itt;
				tree0[i].state=treecan[ncan][i].state;
				tree0[i].alpha=treecan[ncan][i].alpha;
				tree0[i].miss=treecan[ncan][i].miss;
				tree0[i].set = new int[treecan[ncan][i].set[0]+1];
				for(j=1;j<=treecan[ncan][i].set[0];j++)
					tree0[i].set[j]=treecan[ncan][i].set[j];
			}
			
			
			for(k=0;k<=ntree;k++)
			{

				for(i=0;i<=ntree;i++){
					tree[i]= new Treenode();
					tree[i].tr=tree0[i].tr;
					tree[i].tl=tree0[i].tl;
					tree[i].x=tree0[i].x;
					tree[i].qq=tree0[i].qq;
					tree[i].itt=tree0[i].itt;
					tree[i].state=tree0[i].state;
					tree[i].alpha=tree0[i].alpha;
					tree[i].miss=tree0[i].miss;
					tree[i].set = new int[tree0[i].set[0]+1];
					for(j=1;j<=tree0[i].set[0];j++)
						tree[i].set[j]=tree0[i].set[j];
				}
				
				if(tree[k].state==0)
				{
					dis = new int[201];
					for(i=0;i<=200;i++)
						dis[i]=0;
					for(i=1;i<=tree[k].set[0];i++)
						dis[(int) this.atr1[tree[k].set[i]][this.num]]++;
					j=0;
					for(i=1;i<=200;i++)
						if(dis[i]>j) {
							j=dis[i];
							tree[k].x=i;
						}
					miss0=0;
					for(i=1;i<=tree[k].set[0];i++)
						if((int) this.atr1[tree[k].set[i]][this.num]!=tree[k].x) miss0++;		
					tree[k].miss=miss0-tree[k].miss;
					nn=CART_CountSubNode(tree,tree[k]);
					alpha[k]=(double) tree[k].miss/ (double) (ntr1*(nn-1));
				}
			}
			//alpha中存放着每种砍树的alpha
			
			miss0=0;
			for(i=0;i<=ntree;i++)
				if(alpha[i]<alpha0){
					alpha0=alpha[i];
					miss0=i;
				}
			tree0[miss0].state=1;
			dis = new int[201];
			for(i=0;i<=200;i++)
				dis[i]=0;
			for(i=1;i<=tree0[miss0].set[0];i++)
				dis[(int) this.atr1[tree0[miss0].set[i]][this.num]]++;
			j=0;
			for(i=1;i<=200;i++)
				if(dis[i]>j) {
					j=dis[i];
					tree0[miss0].x=i;
				}
			for(i=1;i<=tree0[miss0].set[0];i++)
				if((int) this.atr1[tree0[miss0].set[i]][this.num]!=tree0[miss0].x) tree0[miss0].miss++;	
			ncan++;	
			
			for(i=0;i<=ntree;i++){
				treecan[ncan][i]= new Treenode();
				treecan[ncan][i].tr=tree0[i].tr;
				treecan[ncan][i].tl=tree0[i].tl;
				treecan[ncan][i].x=tree0[i].x;
				treecan[ncan][i].qq=tree0[i].qq;
				treecan[ncan][i].itt=tree0[i].itt;
				treecan[ncan][i].state=tree0[i].state;
				treecan[ncan][i].alpha=tree0[i].alpha;
				treecan[ncan][i].miss=tree0[i].miss;
				treecan[ncan][i].set = new int[tree0[i].set[0]+1];
				for(j=1;j<=tree0[i].set[0];j++)
					treecan[ncan][i].set[j]=tree0[i].set[j];
			}
		}
		
		e=new int[ncan+1];
		//使用剪枝集atr2  选择最佳剪枝树
		for(i=0;i<=ncan;i++)
			e[i]=0;
		nn=ntr2;
		for(i=0;i<=ncan;i++)
		{
			k=0;
			for(j=1;j<=ntr2;j++)
			{
				k=0; //k表示当前所在决策树的位置 从根出发
				while(treecan[i][k].state==0){
					if(atr2[j][treecan[i][k].x]<this.q[treecan[i][k].x][treecan[i][k].qq])
						k=treecan[i][k].tl;
					else k=treecan[i][k].tr;
				}
				if(treecan[i][k].x!=atr2[j][this.num]) e[i]++;
			}
			if(e[i]<nn) nn=e[i];			
		}
		t=(double)(nn*(ntr2-nn))/(double)ntr2;
		t=Math.sqrt(t)+(double)nn;
		for(i=ncan;i>=0;i--)
			if((double)e[i]<=t) {
				tree=treecan[i];
				break;
			}	
		
		//测试集测试
		for(i=1;i<=nts;i++){
			k=0; //k表示当前所在决策树的位置 从根出发
			while(this.tree[k].state==0){
				if(ats[i][this.tree[k].x]<this.q[this.tree[k].x][this.tree[k].qq])
					k=this.tree[k].tl;
				else k=this.tree[k].tr;
			}
			result[i]=this.tree[k].x;			
		}
		
		//输出结果
		this.ShowResult(result, nts);
		this.Evaluation(result, nts, ats);
		}
		}
		else {
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入集样本数超界！(n<100)");
		}
		}
		catch(FileNotFoundException we){
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","缺少输入文件！");
		}
		catch(NullPointerException we){
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","缺少输入文件！");
		}
		catch(InputMismatchException we){
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入格式错误，请参照样例检查！");
		}
		catch(NoSuchElementException we){
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入格式错误，请参照样例检查！");
		}
		catch(ArrayIndexOutOfBoundsException we){
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入格式错误，请参照样例检查！");
		}
		
	}
	
	public void CART_Split(Treenode root)
	{
		int i,j,k,nn=0,flag=0,ntreel=0,ntreer=0;
		double it=1,t=0,itl,itr;
		double[] xnode = new double[root.set[0]+1]; //set中样本某个属性值的排序
		int[] dis = new int[201]; //算属性分布
		int[] lset,rset;
		double[] itcan = new double[root.set[0]+1],itc = new double[this.num+1];
		int[] itq = new int[this.num+1];
		lset = new int[root.set[0]+1];
		rset = new int[root.set[0]+1];
		if(root.set[0]>this.nmin)
		{
			for(i=1;i<root.set[0];i++)
				if((int)this.atr1[root.set[i]][this.num]!=(int)this.atr1[root.set[i+1]][this.num])
					break;
			if(i!=root.set[0]){
				//杂度i(t)
				for(i=0;i<=200;i++)
					dis[i]=0;
				for(i=1;i<=root.set[0];i++)
					dis[(int) this.atr1[root.set[i]][this.num]]++;
				for(i=1;i<=200;i++)
					it=it-((double)dis[i]/(double)root.set[0])*((double)dis[i]/(double)root.set[0]);
				
				//循环计算杂度削减
				for(i=1;i<this.num;i++){  //如果当前还有为处理完的属性向量
					if(determine[i]==1){
					for(j=1;j<=root.set[0];j++)						
						xnode[j]=atr1[root.set[j]][i];
					for(j=1;j<root.set[0];j++)
						for(k=j+1;k<=root.set[0];k++)
							if(xnode[k]<xnode[j]){
								t=xnode[k];xnode[k]=xnode[j];xnode[j]=t;
							}
					//如果该属性向量还有未处理完的标准问题
					if((xnode[1]<this.q[i][ntr1-1])&&(xnode[root.set[0]]>this.q[i][1])){ 
						j=1;nn=1;
						while((j<root.set[0])&&(nn<=ntr1-1)){
							if(this.q[i][nn]>xnode[j])
								if(this.q[i][nn]<xnode[j+1]){
									xnode[j]=this.q[i][nn];
									nn++;
									j++;
								}
								else{
									xnode[j]=-1;   //这里有bug!!!万一刚好属性值是-1怎么办？？？
									j++;
								}
							else nn++;							
						}
						if(j<root.set[0])
							for(k=j;k<root.set[0];k++)
								xnode[k]=-1;   //bug！！！
						
						//至此xnode[]表示root.set[]的问题集合,对每个问题依次分支，计算杂度削减
						for(j=1;j<root.set[0];j++)
							if(xnode[j]!=-1){
								//把root.set[]拆分成左右子集
								lset[0]=0;rset[0]=0;
								for(k=1;k<=root.set[0];k++)
									if(atr1[root.set[k]][i]<xnode[j]){
										lset[0]++;
										lset[lset[0]]=root.set[k];
									}
									else{
										rset[0]++;
										rset[rset[0]]=root.set[k];
									}
								
								//计算杂度削减
								itl=1;
								for(k=0;k<=this.nkind;k++)
									dis[k]=0;
								for(k=1;k<=lset[0];k++)
									dis[(int) this.atr1[lset[k]][this.num]]++;
								for(k=1;k<=this.nkind;k++)
									itl=itl-((double)dis[k]/(double)lset[0])*((double)dis[k]/(double)lset[0]);	
								itr=1;
								for(k=0;k<=this.nkind;k++)
									dis[k]=0;
								for(k=1;k<=rset[0];k++)
									dis[(int) this.atr1[rset[k]][this.num]]++;
								for(k=1;k<=this.nkind;k++)
									itr=itr-((double)dis[k]/(double)rset[0])*((double)dis[k]/(double)rset[0]);	
								
								itcan[j]=it-((double)lset[0]/(double)root.set[0]*itl)-((double)rset[0]/(double)root.set[0]*itr);
							}
							else itcan[j]=-1;
					}
					else itc[i]=-1;
					if(itc[i]!=-1){
						itc[i]=-2;
						for(j=1;j<root.set[0];j++)
							if(itcan[j]!=-1)
								if(itcan[j]>itc[i]){
									itc[i]=itcan[j];
									for(k=1;k<ntr1;k++)
										if(xnode[j]==q[i][k]) break;
									itq[i]=k;
								}	
					}
				}	
					else itc[i]=-1;
				}
				
				//选择最佳分支
				for(i=1;i<this.num;i++)
					if(itc[i]!=-1) break;
				if(i==this.num) flag=0;
				else{
					flag=1;
					root.qq=itq[i];root.x=i;
					root.itt=itc[i];
					for(j=i+1;j<this.num;j++)
						if(itc[j]>root.itt){
							root.x=j;
							root.itt=itc[j];
							root.qq=itq[j];
						}	
				}	
			}
		}
		//建立左右子树 或叶节点
		if(flag==1){
			this.ntree++;
			ntreel=this.ntree;
			root.tl=ntreel;
			tree[ntreel] = new Treenode();
			tree[ntreel].set = new int[root.set[0]+1];
			this.tree[ntreel].set[0]=0;
			this.ntree++;
			ntreer=this.ntree;
			root.tr=ntreer;
			tree[ntreer] = new Treenode();
			tree[ntreer].set = new int[root.set[0]+1];
			this.tree[ntreer].set[0]=0;
			for(i=1;i<=root.set[0];i++)
				if(atr1[root.set[i]][root.x]<q[root.x][root.qq]){
					this.tree[ntreel].set[0]++;
					this.tree[ntreel].set[this.tree[ntreel].set[0]]=root.set[i];
				}
				else{
					this.tree[ntreer].set[0]++;
					this.tree[ntreer].set[this.tree[ntreer].set[0]]=root.set[i];
				}
			
			this.CART_Split(this.tree[ntreel]);
			this.CART_Split(this.tree[ntreer]);
			root.miss=tree[ntreel].miss+tree[ntreer].miss;
		}
		else{
			root.state=1;
			for(i=0;i<=200;i++)
				dis[i]=0;
			for(i=1;i<=root.set[0];i++)
				dis[(int) this.atr1[root.set[i]][this.num]]++;
			j=0;
			for(i=1;i<200;i++)
				if(dis[i]>j) {
					j=dis[i];
					root.x=i;
				}
			root.miss=0;
			for(i=1;i<=root.set[0];i++)
				if((int) this.atr1[root.set[i]][this.num]!=root.x) root.miss++;			
			for(i=1;i<=root.set[0];i++)
				if((int) this.atr1[root.set[i]][this.num]!=root.x) root.miss++;
		}
	}
	
	public int CART_CountSubNode(Treenode[] tree,Treenode root)
	{
		if(root.state==1) return 1;
		else return 1+CART_CountSubNode(tree,tree[root.tl])+CART_CountSubNode(tree,tree[root.tr]);
	}
		
	
	public void LDA() throws FileNotFoundException 
	{
		try{
		//参数：投影矩阵维数 缺省为 c-1
		Scanner intr = new Scanner(this.ftrain);
		Scanner ints = new Scanner(this.ftest);
		int ntr = intr.nextInt();
		int nts = ints.nextInt(); // number of set
		num= intr.nextInt();
		int n=0,i=0,j=0,k=0,d=0,ii=0;  // num=attributes number
		int[] result = new int[nts+1];
		double[][] atr=new double[ntr+1][num+1] ,ats=new double[nts+1][num+1]; 
		double[] dis;
		double t=0;
		
		//获取参数：d  num  determine[]  
		
		determine = new int[num+1];
		if((ntr<1500)&&(nts<1500)){
		d=Integer.valueOf((String) this.tk.getText());
		for(i=1;i<num;i++)
			if(this.Choice[i].isSelected())
				determine[i]=1;
			else determine[i]=0;	
		
		//atr的第0列存放dis，表示样本和这个元组的距离；ats第0列存放其分类结果 第num列存放其实际的类别用于测试
		for(i=1;i<=ntr;i++){
			k=1;
			for(j=1;j<=num;j++)
				if((determine[j]==1)||(j==num)){
					atr[i][k] = intr.nextDouble();
					k++;
				}
				else t=intr.nextDouble();				
		}
			
		for(i=1;i<=nts;i++){
			k=1;
			for(j=1;j<=num;j++)
				if((determine[j]==1)||(j==num)){
					ats[i][k] = ints.nextDouble();
					k++;
				}
				else t=ints.nextDouble();				
		}
		if(intr.hasNextDouble()||ints.hasNextDouble()) {
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入格式错误，请参照样例检查！");
		}
		else{
		intr.close();ints.close();
		num=k-1;
		/*
		//将所有属性值都划到【0，1】的范围内
		double m1=0,m2=0;
		for(i=1;i<num;i++){
			m1=atr[1][i];
			m2=atr[1][i];
			for(j=2;j<=ntr;j++)
				if(atr[j][i]>m1) m1=atr[j][i];
				else if(atr[j][i]<m2) m2=atr[j][i];
			for(j=1;j<=nts;j++)
				if(ats[j][i]>m1) m1=ats[j][i];
				else if(ats[j][i]<m2) m2=ats[j][i];
			for(j=1;j<=ntr;j++)
				atr[j][i]=(atr[j][i]-m2)/(m1-m2);
			for(j=1;j<=nts;j++)
				ats[j][i]=(ats[j][i]-m2)/(m1-m2);		
		}		
		*/
		int[] aa = new int[201];
		for(i=1;i<=200;i++)
			aa[i]=0;
		for(i=1;i<=ntr;i++)
			aa[(int) atr[i][num]]++;  //aa为训练集样本类别分布的统计
		nkind=0;  //类别个数
		for(i=1;i<=200;i++)
			if(aa[i]!=0) nkind++;
		if(d>=nkind) d=1;
		else if(nkind-d>num-1) d=num-1; else d=nkind-d;		
		
		//训练样本分类  构建类矩阵
		double[][][] sample = new double[nkind+1][num+1][ntr+1];
		ii=0;
		for(i=1;i<=nkind;i++){
			while(aa[ii]==0) ii++;
			sample[i][0][0]=ii;
			sample[i][0][1]=aa[ii];
			for(j=1;j<num;j++)
				sample[i][j][0]=0;
			n=1;
			for(j=1;j<=ntr;j++)
				if(atr[j][num]==ii){
					for(k=1;k<=num;k++)
						sample[i][k][n]=atr[j][k];
					n++;
				}
			ii++;
		}		
		
		//求每个类矩阵均值 所有数据均值
		//sample[i][0][0]存放第i类类别名 [i][j][0]存放均值 [i][0][1]存放该类样本个数 [0][j][0]存放所有样本均值
		for(j=1;j<num;j++) sample[0][j][0]=0;
		for(i=1;i<num;i++){
			for(j=1;j<=ntr;j++)
				sample[0][i][0]+=atr[j][i];
			sample[0][i][0]=sample[0][i][0]/(double)ntr;
		}
				
		for(i=1;i<=nkind;i++)
			for(j=1;j<num;j++){
				for(k=1;k<=(int)sample[i][0][1];k++)
					sample[i][j][0]+=sample[i][j][k];
				sample[i][j][0]=sample[i][j][0]/sample[i][0][1];
			}
			
		//计算sw sb sw-1sb
		double[][] arrsb = new double[num-1][num-1];
		double[][] arr1,arr2;
		Matrix sb = new Matrix(arrsb);			
		arr1=new double[num-1][1];
		arr2=new double[1][num-1];
		for(i=1;i<=nkind;i++){
			for(k=0;k<num-1;k++){
				arr1[k][0]=sample[i][k+1][0]-sample[0][k+1][0];
				arr2[0][k]=sample[i][k+1][0]-sample[0][k+1][0];
			}
			Matrix s1 = new Matrix(arr1);
			Matrix s2 = new Matrix(arr2);
			s1=s1.times(s2);
			s1=s1.times(sample[i][0][1]);
			sb=sb.plus(s1);
		}
		
		double[][] arrsw = new double[num-1][num-1];
		Matrix sw = new Matrix(arrsw);
		arr1=new double[num-1][1];
		arr2=new double[1][num-1];
		for(i=1;i<=nkind;i++)			
			for(j=1;j<=sample[i][0][1];j++){
				for(k=0;k<num-1;k++){
					arr1[k][0]=sample[i][k+1][j]-sample[i][k+1][0];
					arr2[0][k]=sample[i][k+1][j]-sample[i][k+1][0];
				}
				Matrix s1 = new Matrix(arr1);
				Matrix s2 = new Matrix(arr2);
				s1=s1.times(s2);
				sw=sw.plus(s1);
			}
		if(sw.det()>0.001) 
			sw=sw.inverse();
		sw=sw.times(sb);
		
		//算出d个特征向量
		double[] arrd = new double[num-1];
		double[][] arrv = new double[num-1][num-1];
		for(i=0;i<num-1;i++)
			arrd[i]=sw.eig().getD().get(i,i);
		
		arrv=sw.eig().getV().getArrayCopy();
		double dmax=0,dmin=arrd[0];
		for(i=1;i<num-1;i++)
			if(arrd[i]<dmin) dmin=arrd[i];
		double[][] arrproj = new double[num-1][d];
		ii=0;		
		while(ii<d){
			dmax=dmin-1;
			j=0;
			for(i=0;i<num-1;i++)
				if(arrd[i]>dmax){
					dmax=arrd[i];
					j=i;
				}
			for(i=0;i<num-1;i++)
				arrproj[i][ii]=arrv[i][j];
			arrd[j]=dmin-2;
			ii++;
		}		
		
		//每个类投影上去 找到投影中心
		double[][] predict = new double[nkind+1][d+1];
		for(i=1;i<=nkind;i++)
			for(j=1;j<=d;j++)
				predict[i][j]=0;
		for(i=1;i<=nkind;i++)
			for(j=1;j<=d;j++)
				for(k=1;k<=num-1;k++)
					predict[i][j]=predict[i][j]+sample[i][k][0]*arrproj[k-1][j-1];
			
		
		//测试集测试		
		dis = new double[nkind+1];
		double[] proj = new double[d+1];
		t=0;
		for(k=1;k<=nts;k++){
			for(i=1;i<=d;i++)
				proj[i]=0;
			for(i=1;i<=nkind;i++)
				dis[i]=0;
			for(i=1;i<=d;i++)
				for(j=1;j<num;j++)
					proj[i]+=ats[k][j]*arrproj[j-1][i-1];
			for(i=1;i<=nkind;i++)
				for(j=1;j<=d;j++)
					dis[i]+=Math.pow((proj[j]-predict[i][j]), 2);
			ii=1;
			t=dis[1];
			for(i=2;i<=nkind;i++)
				if(t>dis[i]){
					t=dis[i];
					ii=i;
				}			
			result[k]=(int) sample[ii][0][0];		
		}
		
		//输出结果
		this.ShowResult(result, nts);	
		this.Evaluation(result, nts, ats);
		}
		}
		else {
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入集样本数超界！(n<100)");
		}
		}
		catch(FileNotFoundException we){
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","缺少输入文件！");
		}
		catch(NullPointerException we){
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","缺少输入文件！");
		}
		catch(InputMismatchException we){
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入格式错误，请参照样例检查！");
		}
		catch(NoSuchElementException we){
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入格式错误，请参照样例检查！");
		}
		catch(ArrayIndexOutOfBoundsException we){
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入格式错误，请参照样例检查！");
		}
	}
	
	public void QDA() throws FileNotFoundException
	{
		try{
			Scanner intr = new Scanner(this.ftrain);
			Scanner ints = new Scanner(this.ftest);
			int ntr = intr.nextInt();
			int nts = ints.nextInt(); // number of set
			num= intr.nextInt();
			int n=0,i=0,j=0,k=0,d=0,ii=0;  // num=attributes number
			int[] result = new int[nts+1];
			double[][] atr=new double[ntr+1][num+1] ,ats=new double[nts+1][num+1]; 
			double[] dis;
			double t=0;
			
			//获取参数：d  num  determine[]  
		
		determine = new int[num+1];
		if((ntr<1500)&&(nts<1500)){
		for(i=1;i<num;i++)
			if(this.Choice[i].isSelected())
				determine[i]=1;
			else determine[i]=0;	
		
		//atr的第0列存放dis，表示样本和这个元组的距离；ats第0列存放其分类结果 第num列存放其实际的类别用于测试
		for(i=1;i<=ntr;i++){
			k=1;
			for(j=1;j<=num;j++)
				if((determine[j]==1)||(j==num)){
					atr[i][k] = intr.nextDouble();
					k++;
				}
				else t=intr.nextDouble();				
		}
			
		for(i=1;i<=nts;i++){
			k=1;
			for(j=1;j<=num;j++)
				if((determine[j]==1)||(j==num)){
					ats[i][k] = ints.nextDouble();
					k++;
				}
				else t=ints.nextDouble();				
		}
		if(intr.hasNextDouble()||ints.hasNextDouble()) {
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入格式错误，请参照样例检查！");
		}
		else{
		intr.close();ints.close();
		num=k-1;
		/*
		//将所有属性值都划到【0，1】的范围内
		double m1=0,m2=0;
		for(i=1;i<num;i++){
			m1=atr[1][i];
			m2=atr[1][i];
			for(j=2;j<=ntr;j++)
				if(atr[j][i]>m1) m1=atr[j][i];
				else if(atr[j][i]<m2) m2=atr[j][i];
			for(j=1;j<=nts;j++)
				if(ats[j][i]>m1) m1=ats[j][i];
				else if(ats[j][i]<m2) m2=ats[j][i];
			for(j=1;j<=ntr;j++)
				atr[j][i]=(atr[j][i]-m2)/(m1-m2);
			for(j=1;j<=nts;j++)
				ats[j][i]=(ats[j][i]-m2)/(m1-m2);		
		}		
		*/
		int[] aa = new int[201];
		for(i=1;i<=200;i++)
			aa[i]=0;
		for(i=1;i<=ntr;i++)
			aa[(int) atr[i][num]]++;  //aa为训练集样本类别分布的统计
		nkind=0;  //类别个数
		for(i=1;i<=200;i++)
			if(aa[i]!=0) nkind++;
		d=nkind-1;		
		
		//训练样本分类  构建类矩阵
		double[][][] sample = new double[nkind+1][num+1][ntr+1];
		ii=0;
		for(i=1;i<=nkind;i++){
			while(aa[ii]==0) ii++;
			sample[i][0][0]=ii;
			sample[i][0][1]=aa[ii];
			for(j=1;j<num;j++)
				sample[i][j][0]=0;
			n=1;
			for(j=1;j<=ntr;j++)
				if(atr[j][num]==ii){
					for(k=1;k<=num;k++)
						sample[i][k][n]=atr[j][k];
					n++;
				}
			ii++;
		}		
		
		//求每个类矩阵均值 所有数据均值
		//sample[i][0][0]存放第i类类别名 [i][j][0]存放均值 [i][0][1]存放该类样本个数 [0][j][0]存放所有样本均值
		for(j=1;j<num;j++) sample[0][j][0]=0;
		for(i=1;i<num;i++){
			for(j=1;j<=ntr;j++)
				sample[0][i][0]+=atr[j][i];
			sample[0][i][0]=sample[0][i][0]/(double)ntr;
		}
				
		for(i=1;i<=nkind;i++)
			for(j=1;j<num;j++){
				for(k=1;k<=(int)sample[i][0][1];k++)
					sample[i][j][0]+=sample[i][j][k];
				sample[i][j][0]=sample[i][j][0]/sample[i][0][1];
			}

		double[][] arrsb = new double[num-1][num-1];
		double[][] arr1,arr2;
		Matrix sb = new Matrix(arrsb);	
		Matrix sb1 = new Matrix(arrsb);	
		Matrix sb2 = new Matrix(arrsb);	
		Matrix sb11 = new Matrix(arrsb);	
		Matrix sb22 = new Matrix(arrsb);	
		arr1=new double[num-1][1];
		arr2=new double[1][num-1];

		for(i=1;i<=nkind;i++)
		{
			for(j=1;j<=sample[i][0][1];j++){
				for(k=0;k<num-1;k++){
					arr1[k][0]=sample[i][k+1][j]-sample[i][k+1][0];
					arr2[0][k]=sample[i][k+1][j]-sample[i][k+1][0];
				}
				Matrix s1 = new Matrix(arr1);
				Matrix s2 = new Matrix(arr2);
				s1=s1.times(s2);
				sb1=sb1.plus(s1);
			}
			sb1=sb1.times((double)sample[i][0][1]/(double)(sample[i][0][1]-1));
			if(sb1.det()>0.001) 
				sb1=sb1.inverse();
			for(j=1;j<=sample[i][0][1];j++){
				for(k=0;k<num-1;k++){
					arr1[k][0]=sample[i][k+1][j]-sample[i][k+1][0];
					arr2[0][k]=sample[i][k+1][j]-sample[i][k+1][0];
				}
				Matrix s1 = new Matrix(arr1);
				Matrix s2 = new Matrix(arr2);
				s2=s2.times(sb1);
				s1=s1.times(s2);
				sb2=sb2.plus(s1);
			}			
			for(ii=i+1;ii<=nkind;ii++)
			{
				for(j=1;j<=sample[ii][0][1];j++){
					for(k=0;k<num-1;k++){
						arr1[k][0]=sample[ii][k+1][j]-sample[ii][k+1][0];
						arr2[0][k]=sample[ii][k+1][j]-sample[ii][k+1][0];
					}
					Matrix s1 = new Matrix(arr1);
					Matrix s2 = new Matrix(arr2);
					s1=s1.times(s2);
					sb1=sb1.plus(s1);
				}
				sb11=sb11.times((double)sample[ii][0][1]/(double)(sample[ii][0][1]-1));
				if(sb11.det()>0.001) 
					sb11=sb11.inverse();
				for(j=1;j<=sample[ii][0][1];j++){
					for(k=0;k<num-1;k++){
						arr1[k][0]=sample[ii][k+1][j]-sample[ii][k+1][0];
						arr2[0][k]=sample[ii][k+1][j]-sample[ii][k+1][0];
					}
					Matrix s1 = new Matrix(arr1);
					Matrix s2 = new Matrix(arr2);
					s2=s2.times(sb11);
					s1=s1.times(s2);
					sb22=sb22.plus(s1);
				}	
				sb1=sb1.minus(sb11);
				sb1=sb1.times(-0.5);
				sb2=sb2.arrayLeftDivide(sb22);
				sb2=sb2.times(-0.5);
				for(j=0;j<sb2.getColumnDimension();j++)
					for(k=0;k<sb2.getRowDimension();k++)
						{
							arrsb[j][k]=sb2.get(j, k);
							arrsb[j][k]=Math.log(arrsb[j][k]);
						}
				sb2=new Matrix(arrsb);
			}
			sb=sb.plus(sb1);
			sb=sb.plus(sb2);
		}
		
		arrsb = new double[num-1][num-1];
		sb = new Matrix(arrsb);			
		arr1=new double[num-1][1];
		arr2=new double[1][num-1];
		for(i=1;i<=nkind;i++){
			for(k=0;k<num-1;k++){
				arr1[k][0]=sample[i][k+1][0]-sample[0][k+1][0];
				arr2[0][k]=sample[i][k+1][0]-sample[0][k+1][0];
			}
			Matrix s1 = new Matrix(arr1);
			Matrix s2 = new Matrix(arr2);
			s1=s1.times(s2);
			s1=s1.times(sample[i][0][1]);
			sb=sb.plus(s1);
		}
		
		double[][] arrsw = new double[num-1][num-1];
		Matrix sw = new Matrix(arrsw);
		arr1=new double[num-1][1];
		arr2=new double[1][num-1];
		for(i=1;i<=nkind;i++)			
			for(j=1;j<=sample[i][0][1];j++){
				for(k=0;k<num-1;k++){
					arr1[k][0]=sample[i][k+1][j]-sample[i][k+1][0];
					arr2[0][k]=sample[i][k+1][j]-sample[i][k+1][0];
				}
				Matrix s1 = new Matrix(arr1);
				Matrix s2 = new Matrix(arr2);
				s1=s1.times(s2);
				sw=sw.plus(s1);
			}
		if(sw.det()>0.001) 
			sw=sw.inverse();
		sw=sw.times(sb);
		
		//算出d个特征向量
		double[] arrd = new double[num-1];
		double[][] arrv = new double[num-1][num-1];
		for(i=0;i<num-1;i++)
			arrd[i]=sw.eig().getD().get(i,i);
		
		arrv=sw.eig().getV().getArrayCopy();
		double dmax=0,dmin=arrd[0];
		for(i=1;i<num-1;i++)
			if(arrd[i]<dmin) dmin=arrd[i];
		double[][] arrproj = new double[num-1][d];
		ii=0;		
		while(ii<d){
			dmax=dmin-1;
			j=0;
			for(i=0;i<num-1;i++)
				if(arrd[i]>dmax){
					dmax=arrd[i];
					j=i;
				}
			for(i=0;i<num-1;i++)
				arrproj[i][ii]=arrv[i][j];
			arrd[j]=dmin-2;
			ii++;
		}		
		
		//每个类投影上去 找到投影中心
		double[][] predict = new double[nkind+1][d+1];
		for(i=1;i<=nkind;i++)
			for(j=1;j<=d;j++)
				predict[i][j]=0;
		for(i=1;i<=nkind;i++)
			for(j=1;j<=d;j++)
				for(k=1;k<=num-1;k++)
					predict[i][j]=predict[i][j]+sample[i][k][0]*arrproj[k-1][j-1];
			
		
		//测试集测试		
		dis = new double[nkind+1];
		double[] proj = new double[d+1];
		t=0;
		for(k=1;k<=nts;k++){
			for(i=1;i<=d;i++)
				proj[i]=0;
			for(i=1;i<=nkind;i++)
				dis[i]=0;
			for(i=1;i<=d;i++)
				for(j=1;j<num;j++)
					proj[i]+=ats[k][j]*arrproj[j-1][i-1];
			for(i=1;i<=nkind;i++)
				for(j=1;j<=d;j++)
					dis[i]+=Math.pow((proj[j]-predict[i][j]), 2);
			ii=1;
			t=dis[1];
			for(i=2;i<=nkind;i++)
				if(t>dis[i]){
					t=dis[i];
					ii=i;
				}
			result[k]=(int) sample[ii][0][0];		
		}
		
		//输出结果
		this.ShowResult(result, nts);
		this.Evaluation(result, nts, ats);
		}
		}
		else {
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入集样本数超界！(n<100)");
		}
		}
		catch(FileNotFoundException we){
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","缺少输入文件！");
		}		
		catch(NullPointerException we){
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","缺少输入文件！");
		}
		catch(InputMismatchException we){
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入格式错误，请参照样例检查！");
		}
		catch(NoSuchElementException we){
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入格式错误，请参照样例检查！");
		}
		catch(ArrayIndexOutOfBoundsException we){
			ExceptionDialog d1 = new ExceptionDialog(this,"Exception","输入格式错误，请参照样例检查！");
		}
	}
		
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		DataMiningTools tools = new DataMiningTools();
		tools.setLocationRelativeTo(null);
		
		
		System.out.println();	
	}
}





