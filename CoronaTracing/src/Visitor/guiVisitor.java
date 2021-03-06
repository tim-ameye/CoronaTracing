package Visitor;


import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mixingProxy.Capsule;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class guiVisitor {

	private JFrame frame;
	private JLabel label_errorMessage;
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
		try {
			visitorClient.setGUI(this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sentences = new ArrayList<>();
		sentences.add("Cheers!");
		sentences.add("Schol!");
		sentences.add("Prost!");
		sentences.add("A votre sant�!");
		sentences.add("Na zdrowie!");
		sentences.add("Salud!");
		sentences.add("Sk�l!");
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
		
		JPanel panelVisitorLogs = new JPanel();
		frame.getContentPane().add(panelVisitorLogs, "name_48490272459100");
		panelVisitorLogs.setLayout(null);
		
		JLabel lblNewLabel_1_1 = new JLabel("Log information");
		lblNewLabel_1_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1_1.setFont(new Font("Verdana", Font.BOLD, 16));
		lblNewLabel_1_1.setBounds(0, 200, 384, 30);
		panelVisitorLogs.add(lblNewLabel_1_1);
		
		JLabel lblNewLabel_1_1_1 = new JLabel("Time of visit:");
		lblNewLabel_1_1_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1_1_1.setFont(new Font("Verdana", Font.PLAIN, 16));
		lblNewLabel_1_1_1.setBounds(0, 230, 384, 30);
		panelVisitorLogs.add(lblNewLabel_1_1_1);
		
		JLabel lblNewLabel_1_1_1_1 = new JLabel("Catering facility:");
		lblNewLabel_1_1_1_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1_1_1_1.setFont(new Font("Verdana", Font.PLAIN, 16));
		lblNewLabel_1_1_1_1.setBounds(0, 330, 384, 30);
		panelVisitorLogs.add(lblNewLabel_1_1_1_1);
		
		JLabel label_beginTimeOfVisit = new JLabel(" ");
		label_beginTimeOfVisit.setHorizontalAlignment(SwingConstants.CENTER);
		label_beginTimeOfVisit.setFont(new Font("Verdana", Font.PLAIN, 16));
		label_beginTimeOfVisit.setBounds(0, 260, 384, 30);
		panelVisitorLogs.add(label_beginTimeOfVisit);
		
		JLabel label_cf = new JLabel(" ");
		label_cf.setHorizontalAlignment(SwingConstants.CENTER);
		label_cf.setFont(new Font("Verdana", Font.PLAIN, 12));
		label_cf.setBounds(0, 360, 384, 30);
		panelVisitorLogs.add(label_cf);
		
		JLabel label_endTimeOfVisit = new JLabel(" ");
		label_endTimeOfVisit.setHorizontalAlignment(SwingConstants.CENTER);
		label_endTimeOfVisit.setFont(new Font("Verdana", Font.PLAIN, 16));
		label_endTimeOfVisit.setBounds(0, 290, 384, 30);
		
		
		JButton btnBack = new JButton("Back");
		btnBack.setFont(new Font("Verdana", Font.PLAIN, 20));
		btnBack.setBounds(90, 400, 200, 50);
		panelVisitorLogs.add(btnBack);
		
		
		panelVisitorLogs.add(label_endTimeOfVisit);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panelScan.setVisible(true);
				panelVisitorLogs.setVisible(false);
			}
		});
		
		
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
		
		JLabel lblCheers = new JLabel(sentences.get(0));
		lblCheers.setHorizontalAlignment(SwingConstants.CENTER);
		lblCheers.setFont(new Font("Verdana", Font.BOLD, 20));
		lblCheers.setForeground(new Color(0, 0, 0));
		lblCheers.setBounds(0, 180, 384, 100);
		panelSucces.add(lblCheers);
		
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
		
		JLabel label_errorLogin = new JLabel(" ");
		label_errorLogin.setForeground(Color.RED);
		label_errorLogin.setHorizontalAlignment(SwingConstants.CENTER);
		label_errorLogin.setFont(new Font("Verdana", Font.PLAIN, 18));
		label_errorLogin.setBounds(0, 295, 384, 30);
		panelLogin.add(label_errorLogin);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//TODO send gegevens naar een databank
				try {
					visitor = new Visitor(textField_firstName.getText(),textField_lastName.getText(), textField_phoneNumber.getText());
					if(visitorClient.login(visitor)) {
						panelScan.setVisible(true);
						panelLogin.setVisible(false);
						visitorClient.setVisitor(visitor);
						visitorClient.getVisitsFromLogs();	// check if he already had a history
						visitorClient.getInfectedLogs();
						visitorClient.getTokens();
					}else {
						label_errorLogin.setText("User not found, please retry or sign up");
					}
					
					
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
				} catch (FileNotFoundException e) {
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
						label_errorLogin.setText("User already exists, please login!");
					} else {
						visitorClient.getTokens();
						panelScan.setVisible(true);
						panelLogin.setVisible(false);
					}
					
					//register functie oproepen vd registrar
				} catch (RemoteException | NoSuchAlgorithmException e) {
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
		btnSignUp.setFont(new Font("Verdana", Font.PLAIN, 20));
		btnSignUp.setBounds(100, 380, 200, 40);
		panelLogin.add(btnSignUp);
		
		label_errorMessage = new JLabel(" ");
		label_errorMessage.setForeground(Color.RED);
		label_errorMessage.setHorizontalAlignment(SwingConstants.CENTER);
		label_errorMessage.setFont(new Font("Verdana", Font.PLAIN, 18));
		label_errorMessage.setBounds(0, 275, 384, 30);
		panelScan.add(label_errorMessage);
		
		JButton btnScanQR = new JButton("Scan!");
		btnScanQR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//TODO controleren of die wel goed wordt gegenereerd
				Capsule capsule = visitorClient.makeCapsule(textField_QRinputstring.getText());
				boolean b = visitorClient.sendCapsule(capsule, textField_QRinputstring.getText());
				if(b) {
					try {
						visitorClient.addVisit();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					lblCheers.setText(sentences.get(visitorClient.getRandom()%10));
					panelSucces.setVisible(true);
					panelScan.setVisible(false);
				} else {
					label_errorMessage.setText("Invalid QR-code, pleas rescan");
				}
			}
		});
		btnScanQR.setFont(new Font("Verdana", Font.PLAIN, 20));
		btnScanQR.setBounds(100, 310, 200, 50);
		panelScan.add(btnScanQR);
		
		JLabel lblPleaseScanThe = new JLabel("Please scan the QR-code");
		lblPleaseScanThe.setHorizontalAlignment(SwingConstants.CENTER);
		lblPleaseScanThe.setFont(new Font("Verdana", Font.PLAIN, 18));
		lblPleaseScanThe.setBounds(0, 30, 384, 30);
		panelScan.add(lblPleaseScanThe);
		
		textField_QRinputstring = new JTextField();
		textField_QRinputstring.setHorizontalAlignment(SwingConstants.CENTER);
		textField_QRinputstring.setBounds(100, 70, 200, 200);
		panelScan.add(textField_QRinputstring);
		textField_QRinputstring.setColumns(10);
		
		JButton btnViewLogs = new JButton("View logs");
		btnViewLogs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panelVisitorLogs.setVisible(true);
				panelScan.setVisible(false);
				ArrayList<Visit> visits = visitorClient.getVisits();
				JList list = new JList(visits.toArray());
				list.addListSelectionListener(
					new ListSelectionListener() {
						@Override
						public void valueChanged(ListSelectionEvent arg0) {
							Visit visit = (Visit) list.getSelectedValue();
							label_beginTimeOfVisit.setText(visit.getBeginTime().toString());
//							label_endTimeOfVisit.setText(visit.getEndTime().toString());
							label_cf.setText(visit.getBusinessNumber());
							Instant endTime = visit.getBeginTime().plus(30, ChronoUnit.MINUTES);
							label_endTimeOfVisit.setText(endTime.toString());
								
						}
					});
				list.setBounds(10, 10, 364, 180);
				panelVisitorLogs.add(list);
				list.updateUI();
						
			}
		});
		btnViewLogs.setFont(new Font("Verdana", Font.PLAIN, 20));
		btnViewLogs.setBounds(100, 380, 200, 50);
		panelScan.add(btnViewLogs);
		
		JLabel lblNewLabel_1 = new JLabel("You have succesfully scanned the QR-code! ");
		lblNewLabel_1.setFont(new Font("Verdana", Font.PLAIN, 16));
		lblNewLabel_1.setBounds(10, 160, 364, 30);
		panelSucces.add(lblNewLabel_1);
		
		
		
		JButton btnEndVisit = new JButton("End visit!");
		btnEndVisit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panelScan.setVisible(true);
				panelSucces.setVisible(false);
				visitorClient.stopRv();
			}
		});
		btnEndVisit.setFont(new Font("Verdana", Font.PLAIN, 20));
		btnEndVisit.setBounds(70, 330, 250, 50);
		panelSucces.add(btnEndVisit);
		
	}
	
	public void setVisible(boolean b) {
		frame.setVisible(b);
	}

	public void informUser() {
		label_errorMessage.setText("You have been in contact with an infected person!");	
	}
	
}
