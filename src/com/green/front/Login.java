package com.green.front;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JTextField;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JPasswordField;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

import org.eclipse.wb.swing.FocusTraversalOnArray;

import com.green.back.DatabaseConnection;

import java.awt.Component;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField usernameField;
	private JLabel lblUsername;
	private JLabel lblPassword;
	private JPasswordField passwordField;
	private Canvas canvas;
	private JButton btnSignIn;
	
	private DatabaseConnection databaseConnection;
	private JLabel lblSignUp;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Login() {
		
		databaseConnection = new DatabaseConnection();
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 267, 444);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(30, 144, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{257, 0};
		gbl_contentPane.rowHeights = new int[] {100, 0, 0, 0, 0, 0, 0, 0, 100};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		contentPane.setLayout(gbl_contentPane);
		
		canvas = new Canvas();
		canvas.setBackground(new Color(240, 255, 240));
		GridBagConstraints gbc_canvas = new GridBagConstraints();
		gbc_canvas.fill = GridBagConstraints.BOTH;
		gbc_canvas.insets = new Insets(0, 0, 5, 0);
		gbc_canvas.gridx = 0;
		gbc_canvas.gridy = 0;
		contentPane.add(canvas, gbc_canvas);
		
		lblUsername = new JLabel("Username");
		lblUsername.setFont(new Font("Sana", Font.PLAIN, 13));
		lblUsername.setForeground(new Color(255, 255, 255));
		lblUsername.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.anchor = GridBagConstraints.WEST;
		gbc_lblUsername.insets = new Insets(0, 5, 5, 0);
		gbc_lblUsername.gridx = 0;
		gbc_lblUsername.gridy = 2;
		contentPane.add(lblUsername, gbc_lblUsername);
		
		usernameField = new JTextField();
		GridBagConstraints gbc_usernameField = new GridBagConstraints();
		gbc_usernameField.insets = new Insets(0, 0, 5, 0);
		gbc_usernameField.fill = GridBagConstraints.BOTH;
		gbc_usernameField.gridx = 0;
		gbc_usernameField.gridy = 3;
		contentPane.add(usernameField, gbc_usernameField);
		usernameField.setColumns(10);
		
		lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Sana", Font.PLAIN, 13));
		lblPassword.setForeground(new Color(255, 255, 255));
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.anchor = GridBagConstraints.WEST;
		gbc_lblPassword.insets = new Insets(0, 5, 5, 0);
		gbc_lblPassword.gridx = 0;
		gbc_lblPassword.gridy = 4;
		contentPane.add(lblPassword, gbc_lblPassword);
		
		passwordField = new JPasswordField();
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.insets = new Insets(0, 0, 5, 0);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 0;
		gbc_passwordField.gridy = 5;
		contentPane.add(passwordField, gbc_passwordField);
		
		btnSignIn = new JButton("Sign In!");
		btnSignIn.addActionListener(new LoginActionListener());
		GridBagConstraints gbc_btnSignIn = new GridBagConstraints();
		gbc_btnSignIn.fill = GridBagConstraints.BOTH;
		gbc_btnSignIn.insets = new Insets(0, 10, 5, 10);
		gbc_btnSignIn.gridx = 0;
		gbc_btnSignIn.gridy = 6;
		contentPane.add(btnSignIn, gbc_btnSignIn);
		this.getRootPane().setDefaultButton(btnSignIn);
		
		lblSignUp = new JLabel("or Sign Up!");
		lblSignUp.setFont(new Font("Sana", Font.BOLD, 13));
		lblSignUp.setForeground(new Color(255, 255, 255));
		GridBagConstraints gbc_lblSignUp = new GridBagConstraints();
		gbc_lblSignUp.insets = new Insets(0, 0, 5, 0);
		gbc_lblSignUp.gridx = 0;
		gbc_lblSignUp.gridy = 7;
		contentPane.add(lblSignUp, gbc_lblSignUp);
		contentPane.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{usernameField, passwordField, btnSignIn}));
	}
	
	public void closeFrame() {
		super.setVisible(false);
	}
	
	public class LoginActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean loggedIn = false;
			
			if ( !usernameField.getText().isEmpty() && passwordField.getPassword().length > 0) {
				loggedIn = databaseConnection.login(usernameField.getText(), passwordField.getText());
			}
			
			if( loggedIn) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							Main window = new Main();
							closeFrame();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		}
	}
}
