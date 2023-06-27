//Ataberk Kılavuzcu
//Umut Ulaş Balcı
//Dide Sena Çalışkan
//Sıla Sarı
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

import com.mysql.cj.xdevapi.DeleteStatement;

import java.awt.*;
import java.awt.event.*;

public class InmatesWindow {

    private JFrame frame;
    private JComboBox<String> operationComboBox;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JLabel lblFirstName;
    private JLabel lblLastName;
    MySQLUser mySQLUser;
    private boolean checkNameLabel = true;

    public InmatesWindow() {
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
        operationComboBox.addItem("Add Inmate");
        operationComboBox.addItem("Search Inmate");
        operationComboBox.addItem("Show Table");
        operationComboBox.addItem("Delete Inmate");
        operationComboBox.addItem("Update New Inmates");
        operationComboBox.addItem("Longest Sentence");
        operationComboBox.addItem("Substring Search 'a'" );
        operationComboBox.addItem("Sentence Start in 2022");
        operationComboBox.addItem("Heavier Crimes Than Robbery");
        panel.add(operationComboBox);	
        

        lblFirstName = new JLabel("First Name:");
        lblFirstName.setBounds(10, 42, 80, 14);
        panel.add(lblFirstName);

        firstNameField = new JTextField();
        firstNameField.setBounds(100, 39, 200, 20);
        panel.add(firstNameField);
        firstNameField.setColumns(10);

        lblLastName = new JLabel("Last Name:");
        lblLastName.setBounds(10, 73, 80, 14);
        panel.add(lblLastName);

        lastNameField = new JTextField();
        lastNameField.setBounds(100, 70, 200, 20);
        panel.add(lastNameField);
        lastNameField.setColumns(10);
        
        lblFirstName.setVisible(false);
        lblLastName.setVisible(false);
        firstNameField.setVisible(false);
        lastNameField.setVisible(false);

        
        JButton btnExecute = new JButton("Execute");
        btnExecute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executeOperation();
            }
        });
        btnExecute.setBounds(10, 101, 89, 23);
        panel.add(btnExecute);
    
        JButton btnName = new JButton("Select");
        btnName.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
            showNameLastName();
        }
        });
        btnName.setBounds(10,200,89,23);
        panel.add(btnName);
    }
    
    private void showNameLastName() {	
    		if(checkNameLabel) {
                lblFirstName.setVisible(false);
                lblLastName.setVisible(false);
                firstNameField.setVisible(false);
                lastNameField.setVisible(false); 
                checkNameLabel = false;
    		}
    		else {
                lblFirstName.setVisible(true);
                lblLastName.setVisible(true);
                firstNameField.setVisible(true);
                lastNameField.setVisible(true);
                checkNameLabel = true;
    		}
 	
    }
    
    private void executeOperation() {
        String selectedOperation = (String) operationComboBox.getSelectedItem();
        
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();

        if (selectedOperation.equals("Add Inmate")) {

            addInmate(firstName, lastName);
        } else if (selectedOperation.equals("Search Inmate")) {
   	
            searchInmate(firstName, lastName);
        } else if (selectedOperation.equals("Show Table")) {
            DefaultTableModel tableModel = retrieveTableData("inmates");
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(frame, scrollPane, "Table Data", JOptionPane.INFORMATION_MESSAGE); 
        }else if (selectedOperation.equals("Delete Inmate")) {
            deleteInmate(firstName, lastName);
        }else if(selectedOperation.equals("Update New Inmates")) {
        	updateInmatesData();
        }
        else if(selectedOperation.equals("Longest Sentence")) {
            DefaultTableModel tableModel = findLongestSentenceInmates();
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(frame, scrollPane, "Table Data", JOptionPane.INFORMATION_MESSAGE); 	
        }
        else if (selectedOperation.equals("Substring Search 'a'")) {
            DefaultTableModel tableModel = searchInmatesByFirstNameConsistsA();
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(frame, scrollPane, "Table Data", JOptionPane.INFORMATION_MESSAGE); 	
        }
        else if (selectedOperation.equals("Sentence Start in 2022")) {
            DefaultTableModel tableModel = searchInmatesBySentenceStartDate2022();
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(frame, scrollPane, "Table Data", JOptionPane.INFORMATION_MESSAGE); 	
        }
        else if (selectedOperation.equals("Heavier Crimes Than Robbery")) {
            DefaultTableModel tableModel = getInmatesWithGreaterSentenceEndDateFromRobbery();
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(frame, scrollPane, "Table Data", JOptionPane.INFORMATION_MESSAGE); 	
        }
        else {
            
        }
    }


    private void addInmate(String firstName, String lastName) {

        String selectQuery = "SELECT MAX(inmateId) FROM inmates";
        String insertQuery = "INSERT INTO inmates (inmateId, firstName, lastName) VALUES (?, ?, ?)";

        try {
            Connection connection = DriverManager.getConnection(MySQLUser.URL, mySQLUser.USER, mySQLUser.PASSWORD);

            // Get the last emp_no
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = selectStatement.executeQuery();
            int lastInmateId = 0;
            if (resultSet.next()) {
            	lastInmateId = resultSet.getInt(1);
            }
            resultSet.close();
            selectStatement.close();

            // Set the new emp_no
            int newInmateId = lastInmateId + 1;

            // Insert the new employee
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setInt(1, newInmateId);
                insertStatement.setString(2, firstName);
                insertStatement.setString(3, lastName);
            

            insertStatement.executeUpdate();

            JOptionPane.showMessageDialog(frame, "Inmate added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

            insertStatement.close();
            connection.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error adding Inmate: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void deleteInmate(String firstName, String lastName) {

        String deleteQuery = "DELETE FROM inmates " +
                             "WHERE firstName = ? AND lastName = ?";

        try (Connection connection = DriverManager.getConnection(MySQLUser.URL, mySQLUser.USER, mySQLUser.PASSWORD);
             PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {

            deleteStatement.setString(1, firstName);
            deleteStatement.setString(2, lastName);

            int rowsAffected = deleteStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame, "Inmate deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Inmate not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error deleting Inmate: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
   
    private void updateInmatesData() {
        String updateQuery = "UPDATE inmates\r\n"
        		+ "SET dateOfBirth = DATE_ADD('1970-01-01', INTERVAL FLOOR(RAND() * 36525) DAY),\r\n"
        		+ "    gender = CASE FLOOR(RAND() * 2)\r\n"
        		+ "                WHEN 0 THEN 'Male'\r\n"
        		+ "                WHEN 1 THEN 'Female'\r\n"
        		+ "            END,\r\n"
        		+ "    nationality = CASE FLOOR(RAND() * 3)\r\n"
        		+ "                    WHEN 0 THEN 'Iranian'\r\n"
        		+ "                    WHEN 1 THEN 'Pakistani'\r\n"
        		+ "                    WHEN 2 THEN 'Syrian'\r\n"
        		+ "                  END,\r\n"
        		+ "    sentenceStartDate = DATE_ADD('2010-01-01', INTERVAL FLOOR(RAND() * 365) DAY),\r\n"
        		+ "    sentenceEndDate = DATE_ADD('2025-01-01', INTERVAL FLOOR(RAND() * 365) DAY),\r\n"
        		+ "    crimeDetails = \"Murder\",\r\n"
        		+ "    assignedCell = 'Cell C3'\r\n"
        		+ "WHERE inmateId IN (\r\n"
        		+ "    SELECT inmateId\r\n"
        		+ "    FROM (\r\n"
        		+ "        SELECT inmateId\r\n"
        		+ "        FROM inmates\r\n"
        		+ "        WHERE gender IS NULL\r\n"
        		+ "        LIMIT 1\r\n"
        		+ "    ) AS temp\r\n"
        		+ ");";

        try (Connection connection = DriverManager.getConnection(MySQLUser.URL, mySQLUser.USER, mySQLUser.PASSWORD);
             Statement statement = connection.createStatement()) {

            int rowsAffected = statement.executeUpdate(updateQuery);

            JOptionPane.showMessageDialog(frame, rowsAffected + " inmate(s) data updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error updating inmates data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private DefaultTableModel searchInmatesByFirstNameConsistsA() {
        DefaultTableModel tableModel = new DefaultTableModel();

        try {
            Connection connection = DriverManager.getConnection(MySQLUser.URL, mySQLUser.USER, mySQLUser.PASSWORD);
            Statement statement = connection.createStatement();

            String query = "SELECT inmateId, firstName, lastName FROM inmates HAVING firstName LIKE '%a%'";

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
    
    private DefaultTableModel getInmatesWithGreaterSentenceEndDateFromRobbery() {
        DefaultTableModel tableModel = new DefaultTableModel();

        try (Connection connection = DriverManager.getConnection(MySQLUser.URL, mySQLUser.USER, mySQLUser.PASSWORD);
             Statement statement = connection.createStatement()) {

            String query = "SELECT * FROM inmates " +
                           "WHERE sentenceEndDate > ALL (" +
                           "    SELECT sentenceEndDate " +
                           "    FROM inmates " +
                           "    WHERE crimeDetails = 'robbery'" +
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


    private DefaultTableModel searchInmatesBySentenceStartDate2022() {
        DefaultTableModel tableModel = new DefaultTableModel();

        try {
            Connection connection = DriverManager.getConnection(MySQLUser.URL, mySQLUser.USER, mySQLUser.PASSWORD);
            Statement statement = connection.createStatement();

            String query = "SELECT * FROM inmates WHERE sentenceStartDate BETWEEN '2022-01-01' AND '2022-12-31'";

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

    private DefaultTableModel findLongestSentenceInmates() {
        DefaultTableModel tableModel = new DefaultTableModel();

        try {
            Connection connection = DriverManager.getConnection(MySQLUser.URL, mySQLUser.USER, mySQLUser.PASSWORD);
            Statement statement = connection.createStatement();

            String subquery = "SELECT MAX(sentenceEndDate - sentenceStartDate) FROM inmates";
            String query = "SELECT * FROM inmates WHERE (sentenceEndDate - sentenceStartDate) = (" + subquery + ")";

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


    private void searchInmate(String firstName, String lastName) {

        String query = "SELECT * FROM inmates WHERE firstName = ? AND lastName = ?";

        try {
            Connection connection = DriverManager.getConnection(MySQLUser.URL, mySQLUser.USER, mySQLUser.PASSWORD);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            ResultSet resultSet = statement.executeQuery();

            // Create the table model
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("Inmate ID");
            tableModel.addColumn("First Name");
            tableModel.addColumn("Last Name");

            // Populate the table model with search results
            while (resultSet.next()) {
                int inmateNo = resultSet.getInt("inmateId");
                String inmateFirstName = resultSet.getString("firstName");
                String inmateLastName = resultSet.getString("lastName");
                tableModel.addRow(new Object[]{inmateNo, inmateFirstName, inmateLastName});
            }

            resultSet.close();
            statement.close();
            connection.close();

            // Create the table and scroll pane
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);

            // Display the table in a dialog
            JOptionPane.showMessageDialog(frame, scrollPane, "Search Results", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error searching inmate: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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