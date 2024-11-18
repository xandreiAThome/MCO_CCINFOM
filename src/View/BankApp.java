package View;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import CustomJPanels.JPanelWithBackground;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * The HRSView class represents the graphical user interface for the Hotel
 * Reservation System.
 * It extends the JFrame class and provides methods to initialize and set up the
 * different screens of the system.
 */
public class BankApp extends JFrame {
    private JLabel pageName;
    private JPanelWithBackground homeScreen;
    private JButton goToCustomerPage;
    private JButton goToEmployeePage;

    public BankApp() {
        super("Bank App");
        setLayout(new BorderLayout());
        setSize(1280, 720);

        init();

        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    // Make panels for each of the screens for the HRS,
    // Main, Create Hotel,View Hotel,Manage Hotel
    public void init() {

        // HomeScreen /////////
        try {
            homeScreen = new JPanelWithBackground("C:\\Users\\ellex\\OneDrive\\Documents\\Code for Class\\Machine Projects\\MCO_CCINFOM\\assets\\money.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        homeScreen.setLayout(new BorderLayout());
        /////////////////////////

        // North Panel ////////////////////////////////////
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout());
        northPanel.setOpaque(false);
        // northPanel.setBackground(Color.BLUE);
        homeScreen.add(northPanel, BorderLayout.NORTH);

        this.pageName = new JLabel("Bank App");
        this.pageName.setForeground(Color.WHITE);
        this.pageName.setFont(new Font("Verdana", Font.BOLD, 36));
        northPanel.add(this.pageName);
        northPanel.setBackground(Color.decode("#a32b2c"));
        northPanel.setOpaque(true);
        //////////////////////////////////////////////////

        // Central Panel //////////////////////////////////
        JPanel centralPanel = new JPanel();
        centralPanel.setLayout(new GridBagLayout());
        // centralPanel.setBackground(Color.RED);
        centralPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        this.goToCustomerPage = new JButton("Customer Page");
        this.goToEmployeePage = new JButton("Employee Page");
        centralPanel.add(this.goToCustomerPage, gbc);
        centralPanel.add(this.goToEmployeePage, gbc);
        homeScreen.add(centralPanel, BorderLayout.CENTER);
        ////////////////////////////////////////////////////

        this.add(homeScreen);
    }

    public void setActionListener(ActionListener listener) {
        this.goToEmployeePage.addActionListener(listener);
        this.goToCustomerPage.addActionListener(listener);
    }

    public JPanel getHomeScreenPanel() {
        return this.homeScreen;
    }
}
