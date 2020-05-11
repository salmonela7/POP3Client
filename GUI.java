package KompiuteriuTinklai_2;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;

public class GUI extends JFrame{

    String[] columnNames = { "ID", "From", "Subject", "Date" };
    private DefaultTableModel model = new DefaultTableModel(columnNames, 0);
    private JTable emailTable = new JTable(model);
    private JScrollPane tableScrollPane = new JScrollPane(emailTable);
    private String connectionHost;
    private int connectionPort;
    private String logInUsername;
    private String logInPassword;

    GUI(POP3Client client) {
        this.setTitle("POP3 EMAIL CLIENT");
        this.setSize(800, 600);
        this.setLocation(100, 100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container mainContainer = this.getContentPane();
        mainContainer.setLayout(new BorderLayout(8, 6));
        mainContainer.setBackground(Color.GRAY);
        this.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.GRAY));

        JButton logInButton = new JButton("Login");
        JButton getMessagesButton = new JButton("Get Messages");
        JButton deleteButton = new JButton("Delete Selected Email");
        JButton logOutButton = new JButton("Log out");
        JButton logOutAndExitButton = new JButton("Log out and exit");
        getMessagesButton.setVisible(false);
        deleteButton.setVisible(false);
        logOutButton.setVisible(false);
        logOutAndExitButton.setVisible(false);

        JPanel topPanel = new JPanel();
        topPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 3));
        topPanel.setBackground(Color.LIGHT_GRAY);
        topPanel.setLayout(new FlowLayout(5));
        topPanel.add(logInButton);
        topPanel.add(getMessagesButton);
        topPanel.add(deleteButton);
        topPanel.add(logOutButton);
        topPanel.add(logOutAndExitButton);
        mainContainer.add(topPanel, BorderLayout.NORTH);

        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new GridLayout(2, 1, 0, 0));

        emailTable.setBackground(Color.LIGHT_GRAY);
        int[] columnsWidth = {
                25, 265, 300, 200
        };
        int i = 0;
        for (int width : columnsWidth) {
            TableColumn column = emailTable.getColumnModel().getColumn(i++);
            column.setMinWidth(width);
            column.setMaxWidth(width);
            column.setPreferredWidth(width);
        }

        eastPanel.add(tableScrollPane);

        JTextArea emailTextArea = new JTextArea();
        emailTextArea.setBackground(Color.LIGHT_GRAY);
        emailTextArea.setEditable(false);
        emailTextArea.setFont(emailTextArea.getFont().deriveFont(16f));

        JScrollPane textAreaScrollPane = new JScrollPane(emailTextArea);
        eastPanel.add(textAreaScrollPane);

        emailTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                emailTextArea.setText("");
                try {
                    int messageId = Integer.parseInt(emailTable.getValueAt(emailTable.getSelectedRow(), 0).toString());
                    try {
                        emailTextArea.setText(client.getMessage(messageId).getBody());
                    } catch (IOException ex) {
                        emailTextArea.setText("Email body not found...");
                    }
                }
                catch(ArrayIndexOutOfBoundsException ex) {

                }
            }
        });

        logInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!client.isConnected()) {
                    try {
                        JFrame frame = new JFrame();
                        frame.setBounds(100, 100, 450, 300);
                        frame.getContentPane().setLayout(new BorderLayout());

                        JPanel contentPanel = new JPanel();
                        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                        getContentPane().add(contentPanel, BorderLayout.CENTER);
                        contentPanel.setLayout(null);

                        {
                            JLabel lblHost = new JLabel("Host");
                            lblHost.setFont(lblHost.getFont().deriveFont(10f));
                            lblHost.setBounds(89, 76, 63, 20);
                            contentPanel.add(lblHost);
                        }
                        {
                            JLabel lblPort = new JLabel("Port");
                            lblPort.setFont(lblPort.getFont().deriveFont(10f));
                            lblPort.setBounds(250, 76, 63, 20);
                            contentPanel.add(lblPort);
                        }
                        {
                            JLabel lblUsername = new JLabel("Username");
                            lblUsername.setFont(lblUsername.getFont().deriveFont(10f));
                            lblUsername.setBounds(89, 119, 63, 20);
                            contentPanel.add(lblUsername);
                        }
                        {
                            JLabel lblPassword = new JLabel("Password");
                            lblPassword.setFont(lblPassword.getFont().deriveFont(10f));
                            lblPassword.setBounds(89, 162, 63, 20);
                            contentPanel.add(lblPassword);
                        }

                        JTextField host = new JTextField();
                        host.setBounds(120, 76, 120, 20);
                        contentPanel.add(host);
                        host.setColumns(10);

                        JTextField port = new JTextField();
                        port.setBounds(280, 76, 50, 20);
                        contentPanel.add(port);
                        port.setColumns(10);

                        JTextField username = new JTextField();
                        username.setBounds(173, 119, 152, 20);
                        contentPanel.add(username);
                        username.setColumns(10);

                        JPasswordField password = new JPasswordField();
                        password.setBounds(173, 162, 152, 20);
                        contentPanel.add(password);

                        host.setText("pop.gmail.com");
                        port.setText("995");

                        JButton btnLogin = new JButton("Login");
                        btnLogin.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent arg0) {
                                connectionHost = host.getText();
                                connectionPort = Integer.parseInt(port.getText());
                                logInUsername = username.getText();
                                logInPassword = String.valueOf(password.getPassword());
                                try {
                                    client.connect(host.getText(), Integer.parseInt(port.getText()));
                                    String loginMessage = client.logIn("recent:" + username.getText(), String.valueOf(password.getPassword()));
                                    if (loginMessage.contains("+OK Welcome.")) {
                                        JOptionPane.showMessageDialog(null, "Login successful!");
                                        logInButton.setVisible(false);
                                        getMessagesButton.setVisible(true);
                                        deleteButton.setVisible(true);
                                        logOutButton.setVisible(true);
                                        logOutAndExitButton.setVisible(true);
                                        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                                    } else if (loginMessage.startsWith("-ERR")) {
                                        loginMessage = loginMessage.replaceFirst("-ERR \\[AUTH\\]", "");
                                        JOptionPane.showMessageDialog(null, loginMessage);
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Error occured!");
                                    }
                                } catch (IOException ex) {
                                    JOptionPane.showMessageDialog(null, "Login failed ");
                                }
                            }
                        });
                        btnLogin.setBounds(205, 188, 89, 23);
                        contentPanel.add(btnLogin);

                        JLabel lblLogin = new JLabel("Login");
                        lblLogin.setHorizontalAlignment(SwingConstants.CENTER);
                        lblLogin.setFont(new Font("Tahoma", Font.PLAIN, 20));
                        lblLogin.setBounds(104, 23, 230, 23);
                        contentPanel.add(lblLogin);

                        frame.add(contentPanel);
                        frame.setVisible(true);
                    } catch (Exception ex) {
                        System.out.println("Failed to get number of new messages");
                    }
                }
                else {
                    JOptionPane.showMessageDialog(null, "You are already logged in and connected!");
                }
            }
        });

        getMessagesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int rowCount = model.getRowCount();
                    for (int i = rowCount - 1; i >= 0; i--) {
                        model.removeRow(i);
                    }
                    List<Message> messages = client.getMessages();
                    int numberofMessages = client.getNumberOfNewMessages();
                    for(int i = 0; i < numberofMessages; i++){
                        model.addRow(new Object[] {""+messages.get(i).getId(),
                                messages.get(i).getHeaders().get("From").get(0),
                                messages.get(i).getHeaders().get("Subject").get(0),
                                messages.get(i).getHeaders().get("Date").get(0)});
                    }
                }
                catch (IOException ex) {
                    System.out.println("Failed to get messages");
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int row = emailTable.getSelectedRow();
                    int value = Integer.parseInt(emailTable.getValueAt(row, 0).toString());
                    client.deleteMessage(value);
                    client.logOut();
                    client.disconnect();
                    client.connect(connectionHost, connectionPort);
                    client.logIn("recent:" + logInUsername, logInPassword);

                    int rowCount = model.getRowCount();
                    for (int i = rowCount - 1; i >= 0; i--) {
                        model.removeRow(i);
                    }
                    int numberOfMessages = client.getNumberOfNewMessages();
                    List<Message> messages = client.getMessages();
                    for(int i = 0; i < numberOfMessages; i++){
                        model.addRow(new Object[] {""+messages.get(i).getId(),
                                messages.get(i).getHeaders().get("From").get(0),
                                messages.get(i).getHeaders().get("Subject").get(0),
                                messages.get(i).getHeaders().get("Date").get(0)});
                    }
                }
                catch (Exception ex) {
                    System.out.println("Failed to get messages");
                }
            }
        });

        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    client.logOut();
                    client.disconnect();
                    logInButton.setVisible(true);
                    getMessagesButton.setVisible(false);
                    deleteButton.setVisible(false);
                    logOutButton.setVisible(false);
                    logOutAndExitButton.setVisible(false);

                    int rowCount = model.getRowCount();
                    for (int i = rowCount - 1; i >= 0; i--) {
                        model.removeRow(i);
                    }
                }
                catch (IOException ex) {
                    System.out.println("Failed to get number of new messages");
                }
            }
        });

        logOutAndExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    client.logOut();
                    client.disconnect();
                    System.exit(0);
                }
                catch (IOException ex) {
                    System.out.println("Failed to get number of new messages");
                }
            }
        });

        mainContainer.add(eastPanel);
    }
}
