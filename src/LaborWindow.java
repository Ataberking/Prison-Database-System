//Ataberk Kılavuzcu
//Umut Ulaş Balcı
//Dide Sena Çalışkan
//Sıla Sarı
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;

public class LaborWindow {

    private JFrame frame;
    private JComboBox<String> operationComboBox;
    MySQLUser mySQLUser;


    public LaborWindow() {
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
        operationComboBox.addItem("Get Inmate Labors");
        operationComboBox.addItem("Get Gardeners with Visitation");
        operationComboBox.addItem("Get Painters");
        operationComboBox.addItem("End Labor");
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
            DefaultTableModel tableModel = retrieveTableData();
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(frame, scrollPane, "Table Data", JOptionPane.INFORMATION_MESSAGE); 
        }  
        else if(selectedOperation.equals("End Labor")) {
        	dropLaborTable();
        }

        else if(selectedOperation.equals("Get Inmate Labors")) {
            DefaultTableModel tableModel = getInmateLaborData();
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(frame, scrollPane, "Table Data", JOptionPane.INFORMATION_MESSAGE); 
        }
        else if(selectedOperation.equals("Get Painters")) {
            DefaultTableModel tableModel = getInmatesWithLaborType3();
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(frame, scrollPane, "Table Data", JOptionPane.INFORMATION_MESSAGE); 
        }
        else if(selectedOperation.equals("Get Gardeners with Visitation")) {
            DefaultTableModel tableModel = getInmatesWithLaborGardenerAndVisitation();
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(frame, scrollPane, "Table Data", JOptionPane.INFORMATION_MESSAGE); 
        }
             else {
         }
       

}
    private void dropLaborTable() {
        try {
            Connection connection = DriverManager.getConnection(MySQLUser.URL, mySQLUser.USER, mySQLUser.PASSWORD);
            Statement statement = connection.createStatement();

            String dropQuery = "DROP TABLE Labor";
            statement.executeUpdate(dropQuery);

            JOptionPane.showMessageDialog(frame, "Table dropped successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

            statement.close();
            connection.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error dropping table: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private DefaultTableModel getInmatesWithLaborGardenerAndVisitation() {
        DefaultTableModel tableModel = new DefaultTableModel();

        try (Connection connection = DriverManager.getConnection(MySQLUser.URL, mySQLUser.USER, mySQLUser.PASSWORD);
             Statement statement = connection.createStatement()) {

            String query = "SELECT * FROM inmates " +
                           "WHERE inmateId = ALL (" +
                           "    SELECT inmateId " +
                           "    FROM labor " +
                           "    WHERE labor_type = 'Gardener'" +
                           ") " +
                           "AND EXISTS (" +
                           "    SELECT 1 " +
                           "    FROM visitation " +
                           "    WHERE visitation.inmateId = inmates.inmateId" +
                           ")";

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
    private DefaultTableModel getInmatesWithLaborType3() {
        DefaultTableModel tableModel = new DefaultTableModel();

        try (Connection connection = DriverManager.getConnection(MySQLUser.URL, mySQLUser.USER, mySQLUser.PASSWORD);
             Statement statement = connection.createStatement()) {

            String query = "SELECT * FROM inmates " +
                           "WHERE EXISTS (" +
                           "    SELECT * " +
                           "    FROM labor " +
                           "    WHERE labor.inmate_id = inmates.inmateId " +
                           "        AND labor.labor_type = 3)";

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
    

    private DefaultTableModel getInmateLaborData() {
        DefaultTableModel tableModel = new DefaultTableModel();

        try (Connection connection = DriverManager.getConnection(MySQLUser.URL, mySQLUser.USER, mySQLUser.PASSWORD);
             Statement statement = connection.createStatement()) {

            String query = "SELECT inmates.firstName, inmates.lastName, labor.labor_description " +
                           "FROM inmates " +
                           "LEFT JOIN labor ON inmates.inmateId = labor.inmate_id;";

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


    
    private DefaultTableModel retrieveTableData() {
        DefaultTableModel tableModel = new DefaultTableModel();

        try (Connection connection = DriverManager.getConnection(MySQLUser.URL, mySQLUser.USER, mySQLUser.PASSWORD);
             Statement statement = connection.createStatement()) {

            String query = "SELECT inmates.firstName, inmates.lastName, labor.* " +
                           "FROM labor " +
                           "JOIN inmates ON labor.inmate_id = inmates.inmateId";

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
