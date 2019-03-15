package software;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class UI extends JFrame implements Runnable {

	private JPanel contentPane;
	private JTextField textField;
	private JTable table;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTable table_1;
	private JTextArea textArea;
	static UI frame;
	private static PrintStream ps;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					frame = new UI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			this.textField.setText(OS.clock.getTime());
			this.textField_1.setText(Integer.toString(OS.cpu.GetPC()));
			this.textField_2.setText(Integer.toString(OS.cpu.GetPSW()));
			this.textField_3.setText(Integer.toString(OS.cpu.GetAddress()));
			try {
				this.updateTLBTable();
				this.updatePageTable();

			} catch (Exception e) {
				e.printStackTrace();
			}
			if (OS.JobQueue.size() != 0) {
				this.textArea.append(String.format("后备队列大小和阻塞队列大小%d %d\n", OS.JobQueue.getFirst().pcbPool.BackUp.size(),
						OS.JobQueue.getFirst().pcbPool.Waiting.size()));
				this.textArea.append(String.format("三个就绪队列大小 %d %d %d\n", OS.JobQueue.getFirst().pcbPool.Ready[1].size(),
						OS.JobQueue.getFirst().pcbPool.Ready[2].size(),
						OS.JobQueue.getFirst().pcbPool.Ready[3].size()));
			}

			System.setOut(ps);
			
		}
	}

	/**
	 * Create the frame.
	 */
	public UI() {
		setTitle("OS\u4EFF\u771F");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setSize(800, 600);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnNewButton = new JButton("\u7A0B\u5E8F\u5F00\u59CB");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				OS os = new OS();// 初始化系统
				Thread t = new Thread(frame);
				t.start();
				os.CeateJobs(5); // 创建作业 （大量用随机数生成了进程序列到文件）
				for (int i = 0; i < 5; i++) {
					os.InitJob(i + 1, os.JobQueue); // 初始作业化（作业进入后备作业队列 ）
				}
				Thread tt = new Thread(os);
				tt.start();
			}
		});

		btnNewButton.setBounds(14, 13, 150, 27);
		contentPane.add(btnNewButton);

		JLabel label = new JLabel("\u7CFB\u7EDF\u65F6\u95F4");
		label.setBounds(178, 17, 72, 18);
		contentPane.add(label);

		textField = new JTextField();
		textField.setBounds(250, 13, 150, 26);
		contentPane.add(textField);
		textField.setColumns(10);

		JLabel lblNewLabel = new JLabel("\u4E00\u7EA7\u9875\u8868");
		lblNewLabel.setBounds(583, 17, 72, 18);
		contentPane.add(lblNewLabel);

		DefaultTableModel dtm = new DefaultTableModel();
		Object[] title = { "PageNum", "PageFrameNum", "Stay", "Use", "Modify" };
		dtm.setColumnIdentifiers(title);

		table = new JTable(dtm);
		table.setFillsViewportHeight(true);
		table.setBounds(447, 45, 317, 517);
		table.setModel(new DefaultTableModel(new Object[][] { { null, null, null, null, null },
				{ null, null, null, null, null }, { null, null, null, null, null }, { null, null, null, null, null },
				{ null, null, null, null, null }, { null, null, null, null, null }, { null, null, null, null, null },
				{ null, null, null, null, null }, { null, null, null, null, null }, { null, null, null, null, null },
				{ null, null, null, null, null }, { null, null, null, null, null }, { null, null, null, null, null },
				{ null, null, null, null, null }, { null, null, null, null, null }, { null, null, null, null, null },
				{ null, null, null, null, null }, { null, null, null, null, null }, { null, null, null, null, null },
				{ null, null, null, null, null }, { null, null, null, null, null }, { null, null, null, null, null },
				{ null, null, null, null, null }, { null, null, null, null, null }, { null, null, null, null, null },
				{ null, null, null, null, null }, { null, null, null, null, null }, { null, null, null, null, null },
				{ null, null, null, null, null }, { null, null, null, null, null }, { null, null, null, null, null },
				{ null, null, null, null, null }, },
				new String[] { "New column", "New column", "New column", "New column", "New column" }));
		contentPane.add(table);

		JLabel label_1 = new JLabel("\u6267\u884C\u8FC7\u7A0B");
		label_1.setBounds(178, 314, 72, 18);
		contentPane.add(label_1);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(14, 333, 423, 207);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		ps= new PrintStream(System.out) {
			public void println(String s) {
				textArea.append(s+"\n");
			}
		};
		
		

		JLabel lblPc = new JLabel("PC");
		lblPc.setBounds(14, 60, 72, 18);
		contentPane.add(lblPc);

		textField_1 = new JTextField();
		textField_1.setBounds(36, 57, 86, 24);
		contentPane.add(textField_1);
		textField_1.setColumns(10);

		JLabel lblPsw = new JLabel("PSW");
		lblPsw.setBounds(136, 60, 72, 18);
		contentPane.add(lblPsw);

		textField_2 = new JTextField();
		textField_2.setBounds(167, 57, 86, 24);
		contentPane.add(textField_2);
		textField_2.setColumns(10);

		JLabel lblIr = new JLabel("Address");
		lblIr.setBounds(260, 60, 72, 18);
		contentPane.add(lblIr);

		textField_3 = new JTextField();
		textField_3.setBounds(326, 57, 86, 24);
		contentPane.add(textField_3);
		textField_3.setColumns(10);

		JLabel label_2 = new JLabel("\u5FEB\u8868");
		label_2.setBounds(195, 91, 72, 18);
		contentPane.add(label_2);

		table_1 = new JTable();
		table_1.setColumnSelectionAllowed(true);
		table_1.setToolTipText("");
		table_1.setSurrendersFocusOnKeystroke(true);
		table_1.setModel(new DefaultTableModel(
				new Object[][] { { null, null, null, null, null }, { null, null, null, null, null },
						{ null, null, null, null, null }, { null, null, null, null, null },
						{ null, null, null, null, null }, { null, null, null, null, null },
						{ null, null, null, null, null }, { null, null, null, null, null }, },
				new String[] { "PageNum", "PageFrameNum", "Use", "Stay", "Modify" }));
		table_1.setBounds(14, 148, 423, 128);
		contentPane.add(table_1);

		JLabel label_3 = new JLabel("\u9875\u6846\u53F7");
		label_3.setBounds(28, 123, 58, 15);
		contentPane.add(label_3);

		JLabel lblNewLabel_1 = new JLabel("\u9875\u53F7");
		lblNewLabel_1.setBounds(114, 123, 58, 15);
		contentPane.add(lblNewLabel_1);

		JLabel label_4 = new JLabel("引用位");
		label_4.setBounds(195, 123, 58, 15);
		contentPane.add(label_4);

		JLabel label_5 = new JLabel("驻留位");
		label_5.setBounds(285, 123, 58, 15);
		contentPane.add(label_5);

		JLabel label_6 = new JLabel("修改位");
		label_6.setBounds(365, 123, 58, 15);
		contentPane.add(label_6);

		JLabel label_7 = new JLabel("\u9875\u6846\u53F7");
		label_7.setBounds(455, 30, 58, 15);
		contentPane.add(label_7);

		JLabel label_8 = new JLabel("\u9875\u53F7");
		label_8.setBounds(517, 30, 58, 15);
		contentPane.add(label_8);

		JLabel label_9 = new JLabel("引用位");
		label_9.setBounds(579, 28, 58, 15);
		contentPane.add(label_9);

		JLabel label_10 = new JLabel("驻留位");
		label_10.setBounds(643, 28, 58, 15);
		contentPane.add(label_10);

		JLabel label_11 = new JLabel("修改位");
		label_11.setBounds(706, 30, 58, 15);
		contentPane.add(label_11);

	}

	public void updateTLBTable() {
		DefaultTableModel model = (DefaultTableModel) this.table_1.getModel();
		int numT = model.getRowCount();// 获取当前已有行数
		while (numT > 0) {// 如果是全体刷新表格需要移除之前的所有数据行
			model.removeRow(0);
			numT--;
		}
		Object[][] obj = new Object[8][5];
		try {

			for (int i = 0; i < 8; i++) {
				if (OS.memory.TLB[i] != null) {
					obj[i][0] = OS.memory.TLB[i].PageNum;
					obj[i][1] = OS.memory.TLB[i].PageFrameNum;
					obj[i][2] = OS.memory.TLB[i].Use;
					obj[i][3] = OS.memory.TLB[i].Stay;
					obj[i][4] = OS.memory.TLB[i].Modify;
				}

				if (obj[i] != null) {
					model.addRow(obj[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		table_1.setModel(model);// 将修改后的model传回给JTable
	}

	public void updatePageTable() {
		DefaultTableModel model = (DefaultTableModel) this.table.getModel();
		int numT = model.getRowCount();// 获取当前已有行数
		while (numT > 0) {// 如果是全体刷新表格需要移除之前的所有数据行
			model.removeRow(0);
			numT--;
		}
		Object[][] obj = new Object[32][5];
		try {

			for (int i = 0; i < 32; i++) {
				if (OS.memory.PageTable[i] != null) {
					obj[i][0] = OS.memory.PageTable[i].PageNum;
					obj[i][1] = OS.memory.PageTable[i].PageFrameNum;
					obj[i][2] = OS.memory.PageTable[i].Use;
					obj[i][3] = OS.memory.PageTable[i].Stay;
					obj[i][4] = OS.memory.PageTable[i].Modify;
				}

				if (obj[i] != null) {
					model.addRow(obj[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		table.setModel(model);// 将修改后的model传回给JTable
	}
}
