//Ataberk Kılavuzcu
//Umut Ulaş Balcı
//Dide Sena Çalışkan
//Sıla Sarı
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;

public class CellWindow {

    private JFrame frame;
    private JComboBox<String> operationComboBox;
    MySQLUser mySQLUser;

    public CellWindow() {
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
        operationComboBox.addItem("Total Capacity");
        operationComboBox.addItem("Show Table");
        operationComboBox.addItem("Modify Cell Type");
        operationComboBox.addItem("Average Capacity By Cell Type");
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

        if (selectedOperation.equals("Total Capacity")) {
        	sumCapacity();
        } 
        else if(selectedOperation.equals("Show Table")) {
            DefaultTableModel tableModel = retrieveTableData("cell");
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(frame, scrollPane, "Table Data", JOptionPane.INFORMATION_MESSAGE); 
        }
        else if(selectedOperation.equals("Average Capacity By Cell Type")) {
            DefaultTableModel tableModel = calculateAverageCapacityByCellType();
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(frame, scrollPane, "Table Data", JOptionPane.INFORMATION_MESSAGE); 
        }
        else if(selectedOperation.equals("Modify Cell Type")) {
        	modifyCellType();
        }
         else {

        }
    

}
    private void modifyCellType() {
        try {
            Connection connection = DriverManager.getConnection(MySQLUser.URL, mySQLUser.USER, mySQLUser.PASSWORD);
            Statement statement = connection.createStatement();

            // Alter table query
            String alterQuery = "ALTER TABLE Cell MODIFY COLUMN cellType varchar(50)";
            statement.executeUpdate(alterQuery);


            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private DefaultTableModel calculateAverageCapacityByCellType() {
        DefaultTableModel tableModel = new DefaultTableModel();

        try {
            Connection connection = DriverManager.getConnection(MySQLUser.URL, mySQLUser.USER, mySQLUser.PASSWORD);
            Statement statement = connection.createStatement();

            String query = "SELECT cellType, AVG(capacity) AS averageCapacity FROM Cell GROUP BY cellType";

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
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return tableModel;
    }

    private void sumCapacity() {
        String query = "SELECT SUM(capacity) AS capacitys FROM cell";

        try {
            Connection connection = DriverManager.getConnection(MySQLUser.URL, mySQLUser.USER, mySQLUser.PASSWORD);
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int totalSentenceDuration = resultSet.getInt("capacitys");
                JOptionPane.showMessageDialog(frame, "Total Capacity: " + totalSentenceDuration,
                        "Total Capacity", JOptionPane.INFORMATION_MESSAGE);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error retrieving total capacity: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
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



    public void show() {
        frame.setVisible(true);
    }
}
