package Visitor;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;

import mixingProxy.Capsule;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

public class guiVisitor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -921166446637910407L;
	
	private JFrame frame;
	private JTextField textField_firstName;
	private JTextField textField_lastName;
	private JTextField textField_phoneNumber;
	private JTextField textField_QRinputstring;
	private JPanel panelLogin;
	private JPanel panelScan;
	private JPanel panelSucces;
	private ArrayList<String> sentences = null;
	int number;
	private Visitor visitor;
	private VisitorClient visitorClient;

	/**
	 * Launch the application.
	 
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					guiVisitor window = new guiVisitor();
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
	public guiVisitor(VisitorClient visitorClient) {
		this.visitorClient = visitorClient;
		
		sentences = new ArrayList<>();
		sentences.add("Cheers!");
		sentences.add("Schol!");
		sentences.add("Prost!");
		sentences.add("A votre santé!");
		sentences.add("Na zdrowie!");
		sentences.add("Salud!");
		sentences.add("Skål!");
		sentences.add("Salut!");
		sentences.add("Gesondheid!");
		number = (int) (Math.random()*9);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * gui gemaakt met https://www.youtube.com/watch?v=bKPGEqJHWaE
	 */
	private void initialize() {
		
		
		frame = new JFrame();
		frame.setBounds(100, 100, 400, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new CardLayout(0, 0));
		
		panelLogin = new JPanel();
		frame.getContentPane().add(panelLogin, "name_861651163587100");
		panelLogin.setLayout(null);
		panelLogin.setVisible(true);
		
		panelScan = new JPanel();
		frame.getContentPane().add(panelScan, "name_861655137015600");
		panelScan.setLayout(null);
		panelScan.setVisible(false);
		
		panelSucces = new JPanel();
		frame.getContentPane().add(panelSucces, "name_861659305217200");
		panelSucces.setLayout(null);
		panelSucces.setVisible(false);
		
		JLabel lblNewLabel = new JLabel("First Name:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Verdana", Font.PLAIN, 18));
		lblNewLabel.setBounds(0, 60, 384, 30);
		panelLogin.add(lblNewLabel);
		
		JLabel lblLastName = new JLabel("Last Name:");
		lblLastName.setHorizontalAlignment(SwingConstants.CENTER);
		lblLastName.setFont(new Font("Verdana", Font.PLAIN, 18));
		lblLastName.setBounds(0, 140, 384, 30);
		panelLogin.add(lblLastName);
		
		JLabel lblPhonenumber = new JLabel("Phonenumber:");
		lblPhonenumber.setHorizontalAlignment(SwingConstants.CENTER);
		lblPhonenumber.setFont(new Font("Verdana", Font.PLAIN, 18));
		lblPhonenumber.setBounds(0, 220, 384, 30);
		panelLogin.add(lblPhonenumber);
		
		textField_firstName = new JTextField();
		textField_firstName.setHorizontalAlignment(SwingConstants.CENTER);
		textField_firstName.setBounds(125, 100, 150, 30);
		panelLogin.add(textField_firstName);
		textField_firstName.setColumns(10);
		
		textField_lastName = new JTextField();
		textField_lastName.setHorizontalAlignment(SwingConstants.CENTER);
		textField_lastName.setColumns(10);
		textField_lastName.setBounds(125, 180, 150, 30);
		panelLogin.add(textField_lastName);
		
		textField_phoneNumber = new JTextField();
		textField_phoneNumber.setHorizontalAlignment(SwingConstants.CENTER);
		textField_phoneNumber.setColumns(10);
		textField_phoneNumber.setBounds(125, 260, 150, 30);
		panelLogin.add(textField_phoneNumber);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//TODO send gegevens naar een databank
				try {
					visitor = new Visitor(textField_firstName.getText(),textField_lastName.getText(), textField_phoneNumber.getText());
					panelScan.setVisible(true);
					panelLogin.setVisible(false);
					visitorClient.setVisitor(visitor);
					visitorClient.getTokens();
					//login functie oproepen vd registrar
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
								
			}
		});
		btnLogin.setFont(new Font("Verdana", Font.PLAIN, 20));
		btnLogin.setBounds(100, 330, 200, 40);
		panelLogin.add(btnLogin);
		
		JButton btnSignUp = new JButton("Sign up!");
		btnSignUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//TODO
				try {
					visitor = new Visitor(textField_firstName.getText(),textField_lastName.getText(), textField_phoneNumber.getText());
					if(!visitorClient.register(visitor)) {
						System.out.println("Please login.");
					} else {
						//visitorClient.getTokens();
						panelScan.setVisible(true);
						panelLogin.setVisible(false);
					}
					
					//register functie oproepen vd registrar
				} catch (RemoteException | NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnSignUp.setFont(new Font("Verdana", Font.PLAIN, 20));
		btnSignUp.setBounds(100, 380, 200, 40);
		panelLogin.add(btnSignUp);
		
		JButton btnScanQR = new JButton("Scan!");
		btnScanQR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//TODO controleren of die wel goed wordt gegenereerd
				panelSucces.setVisible(true);
				panelScan.setVisible(false);
				Capsule capsule = visitorClient.makeCapsule(textField_QRinputstring.getText());
				boolean b = visitorClient.sendCapsule(capsule);
				if(b) {
					visitorClient.addVisit();
				}
			}
		});
		btnScanQR.setFont(new Font("Verdana", Font.PLAIN, 20));
		btnScanQR.setBounds(100, 350, 200, 50);
		panelScan.add(btnScanQR);
		
		JLabel lblPleaseScanThe = new JLabel("Please scan the QR-code");
		lblPleaseScanThe.setHorizontalAlignment(SwingConstants.CENTER);
		lblPleaseScanThe.setFont(new Font("Verdana", Font.PLAIN, 18));
		lblPleaseScanThe.setBounds(0, 80, 384, 30);
		panelScan.add(lblPleaseScanThe);
		
		textField_QRinputstring = new JTextField();
		textField_QRinputstring.setHorizontalAlignment(SwingConstants.CENTER);
		textField_QRinputstring.setBounds(100, 120, 200, 200);
		panelScan.add(textField_QRinputstring);
		textField_QRinputstring.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("You have succesfully scanned the QR-code! ");
		lblNewLabel_1.setFont(new Font("Verdana", Font.PLAIN, 16));
		lblNewLabel_1.setBounds(10, 160, 364, 30);
		panelSucces.add(lblNewLabel_1);
		
		JLabel lblCheers = new JLabel(sentences.get(number));
		lblCheers.setHorizontalAlignment(SwingConstants.CENTER);
		lblCheers.setFont(new Font("Verdana", Font.BOLD, 20));
		lblCheers.setForeground(new Color(0, 0, 0));
		lblCheers.setBounds(0, 180, 384, 100);
		panelSucces.add(lblCheers);
		
		JButton btnScanOtherQrcode = new JButton("Scan other QR-code");
		btnScanOtherQrcode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panelScan.setVisible(true);
				panelSucces.setVisible(false);
			}
		});
		btnScanOtherQrcode.setFont(new Font("Verdana", Font.PLAIN, 20));
		btnScanOtherQrcode.setBounds(70, 350, 250, 50);
		panelSucces.add(btnScanOtherQrcode);
	}
	
	public void setVisible(boolean b) {
		frame.setVisible(b);
	}
}
