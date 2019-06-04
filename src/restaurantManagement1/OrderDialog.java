package restaurantManagement1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

public class OrderDialog extends JDialog {
	private Restaurant restaurant;
	private ImageIcon homepageBackground;
	
	//view general tables panel components
	private JPanel generalTablesPanel;
	private JButton claimReservationButton;
	private JButton findTableButton;
	private JButton viewTableButton;
	private JButton returnToHomepageButton;
	private RestaurantTablesTableModel restaurantTablesTableModel;
	private JTable restaurantTablesTable;
	
	//view specific table panel components
	private JPanel specificTablePanel;
	private JButton fireOrderButton;
	private JButton payButton;
	private JButton releaseTableButton;
	private JButton reprintReceiptButton;
	private JButton returnToGeneralTablesButton;
	

	public OrderDialog(Restaurant restaurant) {
		this.restaurant = restaurant;
		initUI();
	}

	private void initUI() {

		setModalityType(ModalityType.APPLICATION_MODAL);

		setUndecorated(false); // TODO change to true
		setSize(1000, 600);
		setLocationRelativeTo(null);
		setResizable(false);

		//General Tables Panel		
		generalTablesPanel = new JPanel();
		generalTablesPanel.setLayout(null);

		claimReservationButton = new JButton("<html>Claim<p>Reservation</html>");
		claimReservationButton.setBounds(865, 75, 120, 75);
		claimReservationButton.addActionListener(new ButtonListener());
		generalTablesPanel.add(claimReservationButton);

		findTableButton = new JButton("Find a Table");
		findTableButton.setBounds(865, 165, 120, 75);
		findTableButton.addActionListener(new ButtonListener());
		generalTablesPanel.add(findTableButton);

		viewTableButton = new JButton("View Table");
		viewTableButton.setBounds(865, 255, 120, 75);
		viewTableButton.addActionListener(new ButtonListener());
		generalTablesPanel.add(viewTableButton);
		
		returnToHomepageButton = new JButton("<html>Return<p>to Home</html>");
		returnToHomepageButton.setBounds(865, 435, 120, 75);
		returnToHomepageButton.addActionListener(new ButtonListener());
		generalTablesPanel.add(returnToHomepageButton);
		
		restaurantTablesTableModel = new RestaurantTablesTableModel();
		restaurantTablesTable = new JTable(restaurantTablesTableModel);
		restaurantTablesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		restaurantTablesTable.setBounds(25, 100, 400, 400);
		restaurantTablesTableModel.addRows(restaurant.getTables());

		JScrollPane tableListScrollPane = new JScrollPane(restaurantTablesTable);
		tableListScrollPane.setBounds(25, 100, 400, 400);
		generalTablesPanel.add(tableListScrollPane);
		
		// Specific Table Panel
		specificTablePanel = new JPanel();
		specificTablePanel.setLayout(null);
		getContentPane().add(specificTablePanel);
//		specificTablePanel.setVisible(false);
		
		getContentPane().add(generalTablesPanel);
		
		// background image
		homepageBackground = new ImageIcon(getClass().getResource("freshqo background.JPG"));
		JLabel homepageBackgroundLabel = new JLabel(homepageBackground);
		homepageBackgroundLabel.setBounds(0, 0, 1000, 600);
		specificTablePanel.add(homepageBackgroundLabel);
		generalTablesPanel.add(homepageBackgroundLabel);
		setVisible(true);
	}

	class ButtonListener implements ActionListener {

		/**
		 * actionPerformed performs the action that is needed to be performed from
		 * clicking a button
		 * 
		 * @param press used to determine which button is pressed
		 */
		public void actionPerformed(ActionEvent press) {
			if (press.getSource() == claimReservationButton) {
				// claim only for the current date
				if (restaurant.getTables().size() == 0) {
					JOptionPane.showMessageDialog(null, "Your restaurant currently has no tables.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				String customerNameUnderReservation = JOptionPane
						.showInputDialog("Please input the name under reservation: ");
				if (customerNameUnderReservation != null) {
					restaurant.claimReservation(customerNameUnderReservation.toUpperCase());
				}
			}else if (press.getSource() == viewTableButton) {
				int selectedRow = restaurantTablesTable.getSelectedRow();
				if (selectedRow < 0) {
					JOptionPane.showMessageDialog(null, "Please choose a table to view.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}else {
					generalTablesPanel.setVisible(false);
				}
			}else if (press.getSource() == findTableButton) {
				FindTableDialog findTableDialog = new FindTableDialog (restaurant);
				restaurantTablesTableModel.fireTableRowsUpdated(0, restaurant.getTables().size());
			}else if (press.getSource() == returnToHomepageButton) {
				dispose();
			}
		}
	}
	
	class RestaurantTablesTableModel extends AbstractTableModel{
		/**
		 * the names of each column in the table
		 */
		private final String[] tableListColumns = { "Table Num.", "Occupied" };
	
		/**
		 * the class type for each column
		 */
		private final Class[] columnClasses = { int.class, boolean.class};
	
		/**
		 * the list of recipes that are to be displayed within the table
		 */
		private List<Table> tablesData = new ArrayList<>();
		
		@Override
		/**
		 * getColumnCount
		 * the number of columns in the table
		 * @return the number of columns
		 */
		public int getColumnCount() {
			return this.tableListColumns.length;
		}

		@Override
		/**
		 * getRowCount
		 * the number of rows in the table
		 * @return the number of rows
		 */
		public int getRowCount() {
			return tablesData.size();
		}

		@Override
		/**
		 * getColumnName
		 * @param col the column number
		 * @return the name of the column
		 */
		public String getColumnName(int col) {
			return this.tableListColumns[col];
		}

		@Override
		/**
		 * getValueAt
		 * finds the value at the specific row and column number
		 * @param row the row number
		 * @param col the column number
		 * @return the value at the specific row and column
		 */
		public Object getValueAt(int row, int col) {

			Table table = this.tablesData.get(row);
			switch (col) {
			case 0:
				return table.getTableNum();
			default:
				return table.isOccupied();
			}
		}

		@Override
		/**
		 * getColumnClass
		 * finds the class type for a specific column
		 * @param col the column number
		 * @return the class type for the specific column
		 */
		public Class<?> getColumnClass(int col) {
			return this.columnClasses[col];
		}

		@Override
		/**
		 * isCellEditable
		 * checks if the user can edit the cell
		 * @param row the row number
		 * @param col the column number
		 * @return whether or not the cell is editable
		 */
		public boolean isCellEditable(int row, int col) {
			return false;
		}

		@Override
		/**
		 * setValueAt
		 * sets a value at the specific row and column
		 * @param value the value to be set
		 * @param row the row number
		 * @param col the column number
		 */
		public void setValueAt(Object value, int row, int col) {
			Table table = this.tablesData.get(row);
			switch (col) {
			case 0:
				table.setTableNum((int) value);
				break;
			default:
				table.setOccupied((boolean) value);
			}

			fireTableCellUpdated(row, col);
		}
		
		/**
		 * updateRow
		 * when an table is modified, the row must be then updated
		 * @param table the recipe to place in the table and add to the current list of tables
		 * @param row the row that needs to be updated due to a change in the table
		 */
		public void updateRow ( Table table, int row ) {
			this.tablesData.set( row, table );
			fireTableRowsUpdated( row, row );
		}

		/**
		 * insertRow
		 * inserts a row in the table with a table
		 * @param position the position to put the row 
		 * @param table the table to place in the table and add to the current list of tables
		 */
		public void insertRow(int position, Table table) {
			this.tablesData.add(table);
			fireTableRowsInserted(0, getRowCount());
		}

		/**
		 * addRow
		 * adds a row at the bottom of the table with a new recipe
		 * @param table the table to be placed in the table
		 */
		public void addRow(Table table) {
			insertRow(getRowCount(), table);
		}

		/**
		 * addRows
		 * adds 2+ rows into the table
		 * @param tableList the list of tables that are to be put into the table
		 */
		public void addRows(List<Table> tableList) {
			for (Table table : tableList) {
				addRow(table);
			}
		}

		/**
		 * removeRow
		 * removes a specific row in the table
		 * @param position the position of the recipe to be removed
		 */
		public void removeRow(int position) {
			this.tablesData.remove(position);
			fireTableRowsDeleted(0, getRowCount());
		}

		/**
		 * getData
		 * gets the list of tables
		 * @return the list of tables
		 */
		public List<Table> getData() {
			return tablesData;
		}

		/**
		 * setData
		 * gets the list of tables
		 * @param data the list of tables
		 */
		public void setData(List<Table> tablesData) {
			this.tablesData = tablesData;
			fireTableRowsInserted(0, getRowCount());
		}

//		/**
//		 * clearAll
//		 * clears all rows in the table
//		 */
//		public void clearAll() {
//			setData(new List <Table>());
//			fireTableDataChanged();
//		}
	}
}
