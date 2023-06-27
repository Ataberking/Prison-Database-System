//Ataberk Kılavuzcu
//Umut Ulaş Balcı
//Dide Sena Çalışkan
//Sıla Sarı
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;

public class VisitationWindow {

    private JFrame frame;
    private JComboBox<String> operationComboBox;
    MySQLUser mySQLUser;


    public VisitationWindow() {
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
        operationComboBox.addItem("Visitation Data");
        operationComboBox.addItem("Show Table");
        operationComboBox.addItem("Inmates with Visitation");
        operationComboBox.addItem("Distinct Visitor Number");
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

        if (selectedOperation.equals("Visitation Data")) {
            DefaultTableModel tableModel = retrieveVisitationData();
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(frame, scrollPane, "Table Data", JOptionPane.INFORMATION_MESSAGE); 
        } else if(selectedOperation.equals("Show Table")) {
            DefaultTableModel tableModel = retrieveTableData("Visitation");
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(frame, scrollPane, "Table Data", JOptionPane.INFORMATION_MESSAGE); 
        } else if(selectedOperation.equals("Inmates with Visitation")) {
            DefaultTableModel tableModel = getInmateNameWithVisitation();
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(frame, scrollPane, "Table Data", JOptionPane.INFORMATION_MESSAGE); 
        }
        else if(selectedOperation.equals("Distinct Visitor Number")) {
            DefaultTableModel tableModel = getDistinctVisitorsCount();
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(frame, scrollPane, "Table Data", JOptionPane.INFORMATION_MESSAGE); 
        }
        
         else {
        }
    

}
    public DefaultTableModel retrieveVisitationData() {
        DefaultTableModel tableModel = new DefaultTableModel();

        String query = "SELECT inmates.firstName AS InmateName, Staff.firstName AS StaffName, visitation.visitorName AS VisitorName " +
                       "FROM labor " +
                       "JOIN visitation ON labor.inmate_id = visitation.inmateId " +
                       "JOIN inmates ON labor.inmate_id = inmates.inmateId " +
                       "JOIN Staff ON visitation.staffId = Staff.staffId " +
                       "ORDER BY InmateName";

        try (Connection connection = DriverManager.getConnection(MySQLUser.URL, mySQLUser.USER, mySQLUser.PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Add column names to the table model
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnLabel(i));
            }

            // Add rows to the table model
            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = resultSet.getObject(i);
                }
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return tableModel;
    }
    private DefaultTableModel getDistinctVisitorsCount() {
        DefaultTableModel tableModel = new DefaultTableModel();

        try (Connection connection = DriverManager.getConnection(MySQLUser.URL, mySQLUser.USER, mySQLUser.PASSWORD);
             Statement statement = connection.createStatement()) {

            String query = "SELECT COUNT(DISTINCT visitorName) AS distinctVisitorsCount FROM Visitation";

            ResultSet resultSet = statement.executeQuery(query);

            // Add column to table model
            tableModel.addColumn("Distinct Visitors Count");

            // Add row to table model
            if (resultSet.next()) {
                Object[] row = {resultSet.getInt("distinctVisitorsCount")};
                tableModel.addRow(row);
            }

            resultSet.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return tableModel;
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
    private DefaultTableModel getInmateNameWithVisitation() {
        DefaultTableModel tableModel = new DefaultTableModel();

        try (Connection connection = DriverManager.getConnection(MySQLUser.URL, mySQLUser.USER, mySQLUser.PASSWORD);
             Statement statement = connection.createStatement()) {

            String query = "SELECT i.firstName " +
                           "FROM inmates i " +
                           "WHERE i.inmateId = SOME (" +
                           "    SELECT v.inmateId " +
                           "    FROM visitation v" +
                           ");";

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
