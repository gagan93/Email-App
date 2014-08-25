/*
 * 
 * Email App is a simple Java (Swing) based application which can be used to
 * send email with attachments. Rather than logging in from a conventional
 * browser, you can login from the app and send email. This saves time because
 * it does all the backend work and you have to just choose files, set subject,
 * body, id, password and other basic things and the real work is handled by
 * Java Mail API.
 * 
 * I have tried to use Flat User interface in Java by modifying certain defined
 * properties for button and textfields.
 * 
 * @author : Gagandeep Singh. for any improvements, bug reporting etc, write me
 * to gagan_93@live.com
 */

package com.gogo.mail;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

public class Email extends JFrame implements ActionListener, ItemListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1611864134161007136L;

	/* Object Declaration. Names are self explanatory */
	JLabel to, subject, body, heading;
	JPanel upperPanel, mainPanel, upperRightPanel;
	JTextField toField, subjectField;
	JTextArea bodyArea;
	MyJButton send, cross, minimize, attachFile, removeFile;
	JScrollPane textFieldScrollPane, listScrollPane;
	JList<String> listOfFiles;
	DefaultListModel<String> dlm;
	Insets defInsets = new Insets(10, 10, 10, 10);
	String files[] = new String[500];
	String actualFileName[] = new String[500];
	int filesIndex = 0;
	Dimension ss;
	JProgressBar jpb;

	/* Supports only four servers currently as a source for sending email */
	final String YAHOO_SMTP = "smtp.mail.yahoo.com";
	final String LIVE_SMTP = "smtp.live.com";
	final String GMAIL_SMTP = "smtp.gmail.com";
	final String HOTMAIL_SMTP = "smtp.hotmail.com";

	String SMTP_USED;

	/* Dialog objects */

	JDialog dialog;
	JCheckBox useDefault, setAsDefault, showPassword;
	JLabel id, password;
	JTextField idField;
	JPasswordField passwordField;
	Dimension dialogDimension;
	MyJButton go, close, minimizeDialog;
	int screenX, screenY, myX, myY;
	boolean isVisible = false;
	boolean saveFlag;

	Email()
	{
		super("Email App");

		/* Get screen size to set it on main frame */
		ss = Toolkit.getDefaultToolkit().getScreenSize();

		/* Object initializations */

		to = new JLabel(" To ");
		subject = new JLabel(" Subject ");
		body = new JLabel(" Body ");
		heading = new JLabel("   Email App");

		toField = new JTextField(20);
		subjectField = new JTextField(40);
		bodyArea = new JTextArea(10, 10);
		listOfFiles = new JList<String>();
		dlm = new DefaultListModel<String>();

		textFieldScrollPane = new JScrollPane(bodyArea);
		listScrollPane = new JScrollPane(listOfFiles);

		setBorder(toField, subjectField, textFieldScrollPane, listScrollPane);

		mainPanel = new JPanel();
		upperPanel = new JPanel();
		upperRightPanel = new JPanel();

		send = new MyJButton("Send");
		attachFile = new MyJButton("Attach File");
		removeFile = new MyJButton("Remove File");
		cross = new MyJButton("X");
		minimize = new MyJButton("-");

		listOfFiles.setModel(dlm);
		mainPanel.setLayout(new GridBagLayout());
		upperPanel.setLayout(new BorderLayout());

		/*
		 * set selection mode of list as single selection .This list is
		 * responsible for showing files attached for sending
		 */
		listOfFiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mainPanel.add(to, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, defInsets, 0,
				0));

		/* Add components to main frame */
		mainPanel.add(toField, new GridBagConstraints(1, 1,
				GridBagConstraints.REMAINDER, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				defInsets, 0, 0));

		mainPanel.add(subject, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, defInsets, 0,
				0));

		mainPanel.add(subjectField, new GridBagConstraints(1, 2,
				GridBagConstraints.REMAINDER, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				defInsets, 0, 0));

		mainPanel.add(body, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE,
				defInsets, 0, 0));

		mainPanel.add(attachFile, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0,
				GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE,
				new Insets(10, 10, 0, 0), 0, 0));

		mainPanel.add(listScrollPane, new GridBagConstraints(0, 5, 1, 1, 1.0,
				1.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				defInsets, 0, 0));

		mainPanel.add(removeFile, new GridBagConstraints(0, 6, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(10, 10, 0, 0), 0, 0));

		mainPanel.add(textFieldScrollPane, new GridBagConstraints(1, 3,
				GridBagConstraints.REMAINDER, 4, 1.0, 1.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				defInsets, 0, 0));

		mainPanel.add(send, new GridBagConstraints(1, 10, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, defInsets,
				0, 0));

		/* Add buttons to panel */

		upperRightPanel.add(minimize);
		upperRightPanel.add(cross);

		upperPanel.add(heading, BorderLayout.WEST);
		upperPanel.add(upperRightPanel, BorderLayout.EAST);
		upperPanel.setBackground(new Color(150, 159, 224));
		upperPanel.setBorder(BorderFactory.createLineBorder(new Color(178, 185,
				233)));
		upperRightPanel.setBackground(new Color(150, 159, 224));
		mainPanel.setBackground(new Color(150, 159, 224));

		/* set components fonts */

		setFont(new Font("segoe ui light", Font.PLAIN, 20), to, toField,
				subject, subjectField, body, bodyArea, send, attachFile,
				removeFile, heading);
		setFont(new Font("georgia", Font.BOLD, 16), cross, minimize);

		add(upperPanel, BorderLayout.NORTH);
		add(mainPanel, BorderLayout.CENTER);
		attachFile.addActionListener(this);
		removeFile.addActionListener(this);
		cross.addActionListener(this);
		minimize.addActionListener(this);
		send.addActionListener(this);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				toField.requestFocus();
			}
		});

		/* set properties of main frame */
		setExtendedState(MAXIMIZED_BOTH);
		getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setUndecorated(true);
		setResizable(false);
		getRootPane().setDefaultButton(send);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	/* set border of multiple components at a time */
	private void setBorder(JComponent... c)
	{
		for (JComponent C : c)
			C.setBorder(BorderFactory.createCompoundBorder(null,
					BorderFactory.createEmptyBorder(5, 10, 5, 10)));
	}

	/* validate email id, i have used a manual method, no REGEX */
	private boolean validateEmail(String toFieldString, boolean supressError)
	{
		int atPosition = toFieldString.indexOf('@');
		int dotPosition = toFieldString.indexOf('.');
		int lastAtPosition = toFieldString.lastIndexOf('@');
		boolean error = false;
		if (toFieldString.equals(""))
			error = true;
		if (!error
				&& (atPosition == -1 || dotPosition == -1 || atPosition == 0))
			error = true;
		if (!error && (atPosition != lastAtPosition))
			error = true;
		if (!error && (atPosition - dotPosition < 2)
				&& (atPosition - dotPosition > 0))
			error = true;
		if (error)
		{
			if (!supressError)
			{
				JOptionPane
						.showMessageDialog(
								null,
								"Invalid Email ID, Email should be of the form something@websiteName.com ",
								"Invalid data", 0);
				toField.setText("");
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						send.setText("Send");
						send.setEnabled(true);
					}
				});
			}
			return false;
		}
		return true;

	}

	/* dialog responsible for authenticating user's source email id and password */
	private void authenticationDialog()
	{
		toField.setText(toField.getText().trim());
		dialog = new JDialog(this, "Authenticate", true);

		useDefault = new JCheckBox("Use Default");
		setAsDefault = new JCheckBox("Set as Default");
		showPassword = new JCheckBox("Show password");

		jpb = new JProgressBar();
		jpb.setIndeterminate(true);
		jpb.setVisible(false);

		id = new JLabel("Email ID");
		password = new JLabel("Password");

		idField = new JTextField(15);
		passwordField = new JPasswordField(15);

		go = new MyJButton("Go");
		close = new MyJButton("X");
		minimizeDialog = new MyJButton("-");

		passwordField.setEchoChar('x');
		useDefault.setOpaque(false);
		setAsDefault.setOpaque(false);
		showPassword.setOpaque(false);
		dialog.setLayout(new GridBagLayout());

		setBorder(idField, passwordField);

		setFont(new Font("segoe ui light", Font.PLAIN, 18), id, idField,
				password, passwordField, showPassword, useDefault,
				setAsDefault, go, close, minimizeDialog);

		dialog.add(minimizeDialog, new GridBagConstraints(9, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, defInsets, 0,
				0));

		dialog.add(close, new GridBagConstraints(10, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, defInsets, 0,
				0));

		dialog.add(id, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, defInsets, 0,
				0));
		dialog.add(idField, new GridBagConstraints(1, 1,
				GridBagConstraints.REMAINDER, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				defInsets, 0, 0));
		dialog.add(password, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, defInsets, 0,
				0));
		dialog.add(passwordField, new GridBagConstraints(1, 2,
				GridBagConstraints.REMAINDER, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				defInsets, 0, 0));
		dialog.add(showPassword, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, defInsets, 0,
				0));
		dialog.add(useDefault, new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, defInsets, 0,
				0));
		dialog.add(setAsDefault, new GridBagConstraints(2, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, defInsets, 0,
				0));
		dialog.add(go, new GridBagConstraints(1, 4, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, defInsets,
				0, 0));
		dialog.add(jpb, new GridBagConstraints(0, 5,
				GridBagConstraints.REMAINDER, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				idField.requestFocus();
			}
		});
		go.addActionListener(this);
		close.addActionListener(this);
		minimizeDialog.addActionListener(this);
		showPassword.addItemListener(this);
		useDefault.addItemListener(this);
		setAsDefault.addItemListener(this);
		if (dialogDimension == null)
		{
			dialogDimension = new Dimension();
			dialogDimension.width = (int) (ss.getWidth() / 2);
			dialogDimension.height = (int) (ss.getHeight() / 2);

		}
		dialog.setMinimumSize(dialogDimension);
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(this);
		dialog.getContentPane().setBackground(new Color(150, 159, 224));
		dialog.setUndecorated(true);
		dialog.getRootPane().setDefaultButton(go);
		dialog.getRootPane().setBorder(
				BorderFactory.createLineBorder(Color.BLACK));
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);

	}

	/* Write user's details to file to retrieve it next time */
	private void writeToFile()
	{
		if (!validateEmail(idField.getText(), true))
		{
			JOptionPane.showMessageDialog(this,
					"Invalid Email ID, recheck before saving", "Error", 0);
			return;
		}
		String pass = new String(passwordField.getPassword());
		String id = idField.getText();
		int len = pass.length();
		Random r = new Random();
		int runs = 12 + r.nextInt(7);
		for (int i = 0; i < runs; i++)
			pass += (char) (97 + r.nextInt(25));

		FileWriter writer = null;
		try
		{
			writer = new FileWriter("det.xchar");
			writer.write(id + ";" + len * id.charAt(0) * 31 + ";");
			for (int i = 0; i < pass.length(); i++)
				writer.write(id.charAt(0) * pass.charAt(i) + id.charAt(0) + " ");
			writer.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/* Read user's contents from file */
	private void readFromFile()
	{
		String details = "", id = "", pass = "", length = "";
		File check = new File("det.xchar");
		if (!check.exists())
		{
			JOptionPane
					.showMessageDialog(
							this,
							"File does not exist, or was deleted.\nFill in your details here and they will be saved for future use",
							"", 0);
			saveFlag = true;
			return;
		}
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(check));
			details = reader.readLine();
			if (details.equals(""))
			{
				JOptionPane.showMessageDialog(this, "File was empty", "Empty",
						0);
				reader.close();
				return;
			}
			reader.close();
			id = details.substring(0, details.indexOf(';'));
			length = details.substring(id.length() + 1,
					details.lastIndexOf(';'));
			pass = details.substring(details.lastIndexOf(';') + 1,
					details.length());

			if (new Integer(length) % (id.charAt(0) * 31) != 0)
				throw new NumberFormatException();
			int i = 0, xd[] = new int[new Integer(length) / (id.charAt(0) * 31)];

			StringTokenizer st = new StringTokenizer(pass);
			while (i < new Integer(length) / (id.charAt(0) * 31))
			{
				xd[i] = new Integer(st.nextToken());
				if ((xd[i] - id.charAt(0)) % id.charAt(0) != 0)
					throw new NumberFormatException();
				xd[i] = (xd[i] - id.charAt(0)) / id.charAt(0);
				i++;
			}
			if (!validateEmail(id, true))
			{
				JOptionPane
						.showMessageDialog(
								this,
								"File was damaged, because invalid email ID was found.\nFill in your details here and they will be saved for future use",
								"Error", 0);
				saveFlag = true;
				return;
			}
			idField.setText(id);
			passwordField.setText(new String(xd, 0, new Integer(length)
					/ (id.charAt(0) * 31)));
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			boolean flag = false;
			if (e instanceof NumberFormatException)
				flag = true;
			else if (e instanceof IOException)
				flag = true;
			else
				e.printStackTrace();

			if (flag)
			{
				JOptionPane
						.showMessageDialog(
								null,
								"The file contents were ignored because they were incorrect / modified\nThe file is now deleted",
								"Error", 0);
				FileWriter fw = null;
				try
				{
					fw = new FileWriter("det.xchar");
					fw.close();
					File f = new File("det.xchar");
					f.delete();
				}
				catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	/*
	 * this method is actually responsible for sending the email using JavaMail
	 * API
	 */
	private void sendMail()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				if (idField.getText().indexOf("yahoo") != -1)
					SMTP_USED = YAHOO_SMTP;
				else if (idField.getText().indexOf("gmail") != -1)
					SMTP_USED = GMAIL_SMTP;
				else if (idField.getText().indexOf("live") != -1)
					SMTP_USED = LIVE_SMTP;
				else if (idField.getText().indexOf("hotmail") != -1)
					SMTP_USED = HOTMAIL_SMTP;
				else
				{
					JOptionPane
							.showMessageDialog(
									null,
									"This Application supports only some Servers like, Yahoo, Hotmail, Live etc\nWe'll however try to send the mail with default settings and expected SMTP Server.\nIf there is any problem in sending, the error will pop up",
									"Error", 3);
					String server = idField.getText().substring(
							idField.getText().indexOf('@') + 1,
							idField.getText().length());
					int dotIndex = server.indexOf('.');
					server = server.substring(0, dotIndex);
					SMTP_USED = "smtp." + server + ".com";
				}
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						go.setText("Authenticating & Sending");
						go.setEnabled(false);
						jpb.setVisible(true);
					}
				});
				Properties props = new Properties();
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.host", SMTP_USED);
				props.put("mail.smtp.port", "25");

				/*
				 * Here we start a new session to send the email to the client
				 * We also provide required authentication to the SMTP server
				 */
				Session session = Session.getInstance(props,
						new Authenticator()
						{
							protected PasswordAuthentication getPasswordAuthentication()
							{
								return new PasswordAuthentication(idField
										.getText(), new String(passwordField
										.getPassword()));
							}
						});

				/*
				 * Set the from field, to field, subject and message body
				 */
				try
				{
					Message message = new MimeMessage(session);

					// Set From: header field of the header.
					message.setFrom(new InternetAddress(idField.getText()));

					// Set To: header field of the header.
					message.setRecipients(Message.RecipientType.TO,
							InternetAddress.parse(toField.getText()));

					// Set Subject: header field
					message.setSubject(subjectField.getText());

					BodyPart messageBodyPart = new MimeBodyPart();
					messageBodyPart.setText(bodyArea.getText());

					Multipart multipart = new MimeMultipart();
					multipart.addBodyPart(messageBodyPart);
					for (int i = 0; i < filesIndex; i++)
					{
						if (!files[i].equals("ignore"))
						{
							messageBodyPart = new MimeBodyPart();
							DataSource source = new FileDataSource(files[i]);
							messageBodyPart.setDataHandler(new DataHandler(
									source));
							messageBodyPart.setFileName(actualFileName[i]);
							multipart.addBodyPart(messageBodyPart);
						}
					}
					message.setContent(multipart);
					Transport.send(message);

					JOptionPane.showMessageDialog(null, "Mail Sent", "Success",
							JOptionPane.INFORMATION_MESSAGE, new ImageIcon(
									"done.png"));
				}
				catch (Exception e)
				{
					if (e instanceof AuthenticationFailedException)
						JOptionPane.showMessageDialog(null,
								"Invalid ID and/or password", "Error", 0);

					else if (e instanceof AddressException)
						JOptionPane.showMessageDialog(null,
								"The Destination Email Address is invalid",
								"Error", 0);
					else if (e instanceof MessagingException)
						JOptionPane
								.showMessageDialog(
										null,
										"Connection Timed out\nThere is some problem in the internet connection or Email Server doesn't exist",
										"Error", 0);
					e.printStackTrace();
				}
				finally
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							go.setText("Go");
							go.setEnabled(true);
							jpb.setVisible(false);
						}
					});
				}

			}
		}).start();
	}

	/* Button action resolving method */
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		Object source = arg0.getSource();
		if (source == cross)
			System.exit(0);
		else if (source == minimize || source == minimizeDialog)
			setState(JFrame.ICONIFIED);
		else if (source == close)
			dialog.dispose();
		else if (source == attachFile)
		{
			JFileChooser choose = new JFileChooser();
			choose.setPreferredSize(ss);
			choose.setMultiSelectionEnabled(true);
			int returnValue = choose.showOpenDialog(this);
			if (returnValue == JFileChooser.APPROVE_OPTION)
			{
				File f[] = choose.getSelectedFiles();
				for (int i = filesIndex; i < f.length + filesIndex; i++)
				{
					if (f[i - filesIndex].getName().endsWith(".exe"))
						JOptionPane
								.showMessageDialog(null,
										"Attaching .EXE file not allowed\n",
										"Error", 0);
					else
					{
						dlm.addElement(f[i - filesIndex].getName());
						files[i] = f[i - filesIndex].getAbsolutePath();
						actualFileName[i] = f[i - filesIndex].getName();
					}
				}
				filesIndex = dlm.getSize();
			}

		}
		else if (source == removeFile)
		{
			int totalFiles = dlm.getSize();
			int remove = listOfFiles.getSelectedIndex();
			if (remove > totalFiles || remove < 0)
				JOptionPane.showMessageDialog(null,
						"Selected the file to be removed ",
						"No File Loaded / Selected", 0);
			else
			{
				dlm.remove(remove);
				filesIndex--;
				for (int i = remove; i < filesIndex; i++)
				{
					files[i] = files[i + 1];
					actualFileName[i] = actualFileName[i + 1];
				}
			}
		}
		else if (source == send)
		{
			if (validateEmail(toField.getText(), false))
			{
				if (subjectField.getText().equals(""))
					if (JOptionPane.showConfirmDialog(this, "Empty Subject ?") != JOptionPane.YES_OPTION)
						return;
				if (bodyArea.getText().equals(""))
					if (JOptionPane.showConfirmDialog(this,
							"Empty Message Body?") != JOptionPane.YES_OPTION)
						return;
				authenticationDialog();
			}
		}
		else if (source == go)
		{
			if (idField.getText().equals("")
					|| new String(passwordField.getPassword()).equals(""))
			{
				JOptionPane.showMessageDialog(this, "Required Fields Empty",
						"Error", 0);
				return;
			}
			if (!validateEmail(idField.getText(), true))
				JOptionPane.showMessageDialog(this, "Invalid Email ID",
						"Error", 0);
			else
				sendMail();
		}
	}

	/* handling checkbox */
	public void itemStateChanged(ItemEvent arg0)
	{
		// TODO Auto-generated method stub
		Object source = arg0.getSource();
		if (source == showPassword)
			if (showPassword.isSelected())
				passwordField.setEchoChar((char) 0);
			else
				passwordField.setEchoChar('x');
		else if (source == useDefault)
			if (useDefault.isSelected())
				readFromFile();
			else
			{
				idField.setText("");
				passwordField.setText("");
			}
		else if (source == setAsDefault)
			if (setAsDefault.isSelected())
			{
				if (idField.getText().equals("")
						|| (new String(passwordField.getPassword()).equals("")))
				{
					JOptionPane
							.showMessageDialog(
									this,
									"Both fields must be filled before you set this ID and password as default ",
									"Error", 0);
				}
				else
				{
					writeToFile();
					JOptionPane.showMessageDialog(null,
							"ID and password saved ", "Saved",
							JOptionPane.INFORMATION_MESSAGE, new ImageIcon(
									"done.png"));
				}
				setAsDefault.setSelected(false);
			}
	}

	/* set font for multiple components in one go */
	private static final void setFont(Font f, JComponent... c)
	{
		for (JComponent C : c)
			C.setFont(f);
	}

	/* The main method */
	public static void main(String... args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				new Email();
			}
		});
	}
}