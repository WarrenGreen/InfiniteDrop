package com.green.front;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import almonds.FindCallback;
import almonds.ParseException;
import almonds.ParseObject;

import com.green.back.CombinedFileManager;
import com.green.back.DatabaseConnection;

public class Main {

	JFrame frame;
	JList<String> accountList = null;
	
	private DatabaseConnection databaseConnection;
	private CombinedFileManager combinedFileManager;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
		updateAccounts();
		combinedFileManager = new CombinedFileManager();
		Thread t = new Thread(combinedFileManager, "combinedFileManager");
		t.start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		databaseConnection = new DatabaseConnection();
		
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setBounds(100, 100, 450, 300);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0};
		frame.getContentPane().setLayout(gridBagLayout);
		
		accountList = new JList<String>();
		GridBagConstraints gbc_accountList = new GridBagConstraints();
		gbc_accountList.gridwidth = 3;
		gbc_accountList.insets = new Insets(0, 0, 5, 0);
		gbc_accountList.fill = GridBagConstraints.BOTH;
		gbc_accountList.gridx = 0;
		gbc_accountList.gridy = 0;
		frame.getContentPane().add(accountList, gbc_accountList);
		
		JButton btnRemoveAccount = new JButton("-");
		btnRemoveAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		GridBagConstraints gbc_btnRemoveAccount = new GridBagConstraints();
		gbc_btnRemoveAccount.anchor = GridBagConstraints.EAST;
		gbc_btnRemoveAccount.insets = new Insets(0, 0, 5, 5);
		gbc_btnRemoveAccount.gridx = 1;
		gbc_btnRemoveAccount.gridy = 1;
		frame.getContentPane().add(btnRemoveAccount, gbc_btnRemoveAccount);
		
		JButton btnAddAccount = new JButton("+");
		btnAddAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				databaseConnection.authorize();
				updateAccounts();
			}
		});
		GridBagConstraints gbc_btnAddAccount = new GridBagConstraints();
		gbc_btnAddAccount.insets = new Insets(0, 0, 5, 0);
		gbc_btnAddAccount.anchor = GridBagConstraints.WEST;
		gbc_btnAddAccount.gridx = 2;
		gbc_btnAddAccount.gridy = 1;
		frame.getContentPane().add(btnAddAccount, gbc_btnAddAccount);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmClose = new JMenuItem("Close");
		mnFile.add(mntmClose);
	}
	
	private void updateAccounts() {
		Thread t = new Thread(new Runnable() {
	         public void run()
	         {
	        	 List<ParseObject> results = databaseConnection.getAccounts();
	        	 String[] userIds = new String[results.size()];
					for(int i=0; i<userIds.length; i++)
						userIds[i] = results.get(i).getString("userId");
					accountList.setListData(userIds);
	         }
		});
		t.start();
	}
	
	

}
