//Ataberk Kılavuzcu
//Umut Ulaş Balcı
//Dide Sena Çalışkan
//Sıla Sarı
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;

public class StaffWindow {

    private JFrame frame;
    private JComboBox<String> operationComboBox;
    MySQLUser mySQLUser;


    public StaffWindow() {
    	mySQLUser = new MySQLUser();
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 434, 261);
        frame.getContentPane().add(panel);
        panel.setLayout(null);

        operationComboBox = new JComboBox<String>();
        operationComboBox.setBounds(10, 11, 200, 20);
        operationComboBox.addItem("Show Table");
        operationComboBox.addItem("Phone Number Update");
        operationComboBox.addItem("Female Staff Except Nurses");
        panel.add(operationComboBox);


        JButton btnExecute = new JButton("Execute");
        btnExecute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executeOperation();
            }
        });
        btnExecute.setBounds(10, 101, 89, 23);
        panel.add(btnExecute);
    }
    private void executeOperation() {
        String selectedOperation = (String) operationComboBox.getSelectedItem();

        if (selectedOperation.equals("Show Table")) {
            DefaultTableModel tableModel = retrieveTableData("Staff");
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(frame, scrollPane, "Table Data", JOptionPane.INFORMATION_MESSAGE); 
        } 
        else if(selectedOperation.equals("Phone Number Update")) {
        	addUniqueConstraintToStaffTable();
        }
        else if(selectedOperation.equals("Female Staff Except Nurses")) {
            DefaultTableModel tableModel = getFemaleStaffExceptNurses();
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(frame, scrollPane, "Table Data", JOptionPane.INFORMATION_MESSAGE); 
        }
         else {
        }
}
    private void addUniqueConstraintToStaffTable() {
        try {
            Connection connection = DriverManager.getConnection(MySQLUser.URL, mySQLUser.USER, mySQLUser.PASSWORD);
            Statement statement = connection.createStatement();

            String alterQuery = "ALTER TABLE Staff ADD CONSTRAINT uc_phoneNumber UNIQUE (phoneNumber)";
            statement.executeUpdate(alterQuery);

            JOptionPane.showMessageDialog(frame, "Unique constraint added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

            statement.close();
            connection.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error adding unique constraint: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }




    private DefaultTableModel retrieveTableData(String tableName) {
        DefaultTableModel tableModel = new DefaultTableModel();

        try {
            Connection connection = DriverManager.getConnection(MySQLUser.URL, mySQLUser.USER, mySQLUser.PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);

            // Get the column names
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnName(i));
            }

            // Get the rows
            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = resultSet.getObject(i);
                }
                tableModel.addRow(row);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return tableModel;
    }
    private DefaultTableModel getFemaleStaffExceptNurses() {
        DefaultTableModel tableModel = new DefaultTableModel();

        try (Connection connection = DriverManager.getConnection(MySQLUser.URL, mySQLUser.USER, mySQLUser.PASSWORD);
             Statement statement = connection.createStatement()) {

            String query = "SELECT staffId, firstName, lastName, jobTitle " +
                           "FROM Staff " +
                           "WHERE gender = 'Female' " +
                           "EXCEPT " +
                           "SELECT staffId, firstName, lastName, jobTitle " +
                           "FROM Staff " +
                           "WHERE gender = 'Female' AND jobTitle = 'Nurse';";

            ResultSet resultSet = statement.executeQuery(query);

            // Get the column names
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnName(i));
            }

            // Get the rows
            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = resultSet.getObject(i);
                }
                tableModel.addRow(row);
            }

            resultSet.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return tableModel;
    }


    public void show() {
        frame.setVisible(true);
    }
}
