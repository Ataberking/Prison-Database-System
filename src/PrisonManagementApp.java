//Ataberk Kılavuzcu
//Umut Ulaş Balcı
//Dide Sena Çalışkan
//Sıla Sarı
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PrisonManagementApp {
    private JFrame frame;
    private InmatesWindow inmatesWindow;
    private CellWindow cellWindow;
    private LaborWindow laborWindow;
    private StaffWindow staffWindow;
    private VisitationWindow visitationWindow;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    PrisonManagementApp window = new PrisonManagementApp();
                    window.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public PrisonManagementApp() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Prison Management");
        frame.setBounds(100, 100, 550, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        // Create and configure the title label
        JLabel titleLabel = new JLabel("PRISON MANAGEMENT SYSTEM");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        headerPanel.add(titleLabel);
        
        frame.getContentPane().add(headerPanel, BorderLayout.NORTH);


        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnInmates = addButton(buttonPanel, "Inmates");
        btnInmates.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inmatesWindow = new InmatesWindow();
                inmatesWindow.show();
            }
        });

        JButton btnCell = addButton(buttonPanel, "Cell");
        btnCell.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cellWindow = new CellWindow();
                cellWindow.show();
            }
        });

        JButton btnLabor = addButton(buttonPanel, "Labor");
        btnLabor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                laborWindow = new LaborWindow();
                laborWindow.show();
            }
        });

        JButton btnStaff = addButton(buttonPanel, "Staff");
        btnStaff.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                staffWindow = new StaffWindow();
                staffWindow.show();
            }
        });

        JButton btnVisitation = addButton(buttonPanel, "Visitation");
        btnVisitation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                visitationWindow = new VisitationWindow();
                visitationWindow.show();
            }
        });

        frame.getContentPane().add(buttonPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel footerLabel = new JLabel("All rights reserved. © Bong Company");
        footerPanel.add(footerLabel);
        frame.getContentPane().add(footerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private JButton addButton(JPanel panel, String text) {
        JButton button = new JButton(text);
        panel.add(button);
        return button;
    }

    public void show() {
        frame.setVisible(true);
    }
}