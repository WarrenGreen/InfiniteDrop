package com.green.front;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

import almonds.ParseException;
import almonds.ParseObject;
import almonds.ParseQuery;

import com.green.back.CombinedFileManager;
import com.green.back.DatabaseConnection;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SignUp extends JFrame {

	private JPanel contentPane;
	private JTextField txtUsername;
	private JPasswordField passwordField;
	private JPasswordField passwordConfirmField;
	private JTextField txtEmail;
	private JTextField txtConfirmEmail;
	private JLabel lblError;
	
	private DatabaseConnection databaseConnection;
	
	private static final String FILL_ALL_FIELDS = "Please complete all fields.";
	private static final String PASSWORDS_MATCH = "Passwords must match.";
	private static final String EMAILS_MATCH = "Emails must match.";
	private static final String USED_USERNAME = "Username is already in use.";
	private static final String USED_EMAIL = "Email is already in use.";
	private static final String DB_ERROR = "Oops, something went wrong.";

	/**
	 * Create the frame.
	 */
	public SignUp() {
		databaseConnection = new DatabaseConnection();
		
		setResizable(false);
		setBounds(100, 100, 294, 265);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(30, 144, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setFont(new Font("Sana", Font.PLAIN, 13));
		lblUsername.setForeground(Color.WHITE);
		GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
		gbc_lblUsername.anchor = GridBagConstraints.EAST;
		gbc_lblUsername.gridx = 0;
		gbc_lblUsername.gridy = 0;
		contentPane.add(lblUsername, gbc_lblUsername);
		
		txtUsername = new JTextField();
		GridBagConstraints gbc_txtUsername = new GridBagConstraints();
		gbc_txtUsername.insets = new Insets(0, 0, 5, 0);
		gbc_txtUsername.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtUsername.gridx = 1;
		gbc_txtUsername.gridy = 0;
		contentPane.add(txtUsername, gbc_txtUsername);
		txtUsername.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Sana", Font.PLAIN, 13));
		lblPassword.setForeground(Color.WHITE);
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.anchor = GridBagConstraints.EAST;
		gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblPassword.gridx = 0;
		gbc_lblPassword.gridy = 1;
		contentPane.add(lblPassword, gbc_lblPassword);
		
		passwordField = new JPasswordField();
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.insets = new Insets(0, 0, 5, 0);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 1;
		gbc_passwordField.gridy = 1;
		contentPane.add(passwordField, gbc_passwordField);
		
		JLabel lblConfirmPassword = new JLabel("Confirm Password");
		lblConfirmPassword.setFont(new Font("Sana", Font.PLAIN, 13));
		lblConfirmPassword.setForeground(Color.WHITE);
		GridBagConstraints gbc_lblConfirmPassword = new GridBagConstraints();
		gbc_lblConfirmPassword.anchor = GridBagConstraints.EAST;
		gbc_lblConfirmPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblConfirmPassword.gridx = 0;
		gbc_lblConfirmPassword.gridy = 2;
		contentPane.add(lblConfirmPassword, gbc_lblConfirmPassword);
		
		passwordConfirmField = new JPasswordField();
		GridBagConstraints gbc_passwordConfirmField = new GridBagConstraints();
		gbc_passwordConfirmField.insets = new Insets(0, 0, 5, 0);
		gbc_passwordConfirmField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordConfirmField.gridx = 1;
		gbc_passwordConfirmField.gridy = 2;
		contentPane.add(passwordConfirmField, gbc_passwordConfirmField);
		
		JLabel lblEmail = new JLabel("Email");
		lblEmail.setFont(new Font("Sana", Font.PLAIN, 13));
		lblEmail.setForeground(Color.WHITE);
		GridBagConstraints gbc_lblEmail = new GridBagConstraints();
		gbc_lblEmail.anchor = GridBagConstraints.EAST;
		gbc_lblEmail.insets = new Insets(0, 0, 5, 5);
		gbc_lblEmail.gridx = 0;
		gbc_lblEmail.gridy = 3;
		contentPane.add(lblEmail, gbc_lblEmail);
		
		txtEmail = new JTextField();
		GridBagConstraints gbc_txtEmail = new GridBagConstraints();
		gbc_txtEmail.insets = new Insets(0, 0, 5, 0);
		gbc_txtEmail.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtEmail.gridx = 1;
		gbc_txtEmail.gridy = 3;
		contentPane.add(txtEmail, gbc_txtEmail);
		txtEmail.setColumns(10);
		
		JLabel lblConfirmEmail = new JLabel("Confirm Email");
		lblConfirmEmail.setFont(new Font("Sana", Font.PLAIN, 13));
		lblConfirmEmail.setForeground(Color.WHITE);
		GridBagConstraints gbc_lblConfirmEmail = new GridBagConstraints();
		gbc_lblConfirmEmail.anchor = GridBagConstraints.EAST;
		gbc_lblConfirmEmail.insets = new Insets(0, 0, 5, 5);
		gbc_lblConfirmEmail.gridx = 0;
		gbc_lblConfirmEmail.gridy = 4;
		contentPane.add(lblConfirmEmail, gbc_lblConfirmEmail);
		
		txtConfirmEmail = new JTextField();
		GridBagConstraints gbc_txtConfirmEmail = new GridBagConstraints();
		gbc_txtConfirmEmail.insets = new Insets(0, 0, 5, 0);
		gbc_txtConfirmEmail.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtConfirmEmail.gridx = 1;
		gbc_txtConfirmEmail.gridy = 4;
		contentPane.add(txtConfirmEmail, gbc_txtConfirmEmail);
		txtConfirmEmail.setColumns(10);
		
		JButton btnSignUp = new JButton("Sign Up!");
		btnSignUp.addActionListener(new SignUpActionListener());
		GridBagConstraints gbc_btnSignUp = new GridBagConstraints();
		gbc_btnSignUp.fill = GridBagConstraints.BOTH;
		gbc_btnSignUp.insets = new Insets(0, 0, 5, 0);
		gbc_btnSignUp.gridwidth = 2;
		gbc_btnSignUp.gridx = 0;
		gbc_btnSignUp.gridy = 5;
		contentPane.add(btnSignUp, gbc_btnSignUp);
		
		lblError = new JLabel("");
		lblError.setForeground(Color.RED);
		GridBagConstraints gbc_lblError = new GridBagConstraints();
		gbc_lblError.gridwidth = 2;
		gbc_lblError.insets = new Insets(0, 0, 0, 5);
		gbc_lblError.gridx = 0;
		gbc_lblError.gridy = 6;
		contentPane.add(lblError, gbc_lblError);
	}
	
	private String checkFields() {
		
		if(passwordField.getText().isEmpty() || 
				passwordConfirmField.getText().isEmpty() ||
				txtUsername.getText().isEmpty() ||
				txtEmail.getText().isEmpty() ||
				txtConfirmEmail.getText().isEmpty()){
			return SignUp.FILL_ALL_FIELDS; 
		}else if( passwordField.getText().compareTo(passwordConfirmField.getText()) != 0) {
			return SignUp.PASSWORDS_MATCH;
		}else if( txtEmail.getText().compareTo(txtConfirmEmail.getText()) != 0) {
			return SignUp.EMAILS_MATCH;
		}
		
		return null;
	}
	
	private void close() {
		this.dispose();
	}
	
	public class SignUpActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String error = checkFields();
			
			if(error != null) {
				lblError.setText(error);
				return;
			}
			
			try {
				ParseQuery checkUsername = new ParseQuery("User");
				checkUsername.whereEqualTo("username", txtUsername.getText());
				if( checkUsername.find().size() > 0 ) {
					lblError.setText(SignUp.USED_USERNAME);
					return;
				}
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			boolean result = databaseConnection.signUp(txtUsername.getText(), 
													passwordField.getText(), 
													txtEmail.getText());
			
			if(!result) {
				lblError.setText(DB_ERROR);
				return;
			}
			
			close();
		}
	}

}
