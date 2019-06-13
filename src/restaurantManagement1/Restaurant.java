package restaurantManagement1;

import java.awt.Toolkit;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import restaurantManagement1.OrderDialog.ButtonListener;

public class Restaurant extends JFrame {

	private List<Table> tables = new DoublyLinkedList<>();
	private List<Reservation> reservationBook = new DoublyLinkedList<>();
	private List<MenuItem> menu = new DoublyLinkedList<>();
	private CustomerQueue<Customer> waitingList = new CustomerQueue<>();
	private Queue<TableOrderItem> kitchenOrders = new Queue<>();
	private List<Employee> employees = new DoublyLinkedList<>();
	private List<TableOrder> historicalTransactions = new DoublyLinkedList<>();
	private double revenue = 0;
	private Employee currentUser;
	private Restaurant self = this;
	private JPanel mainPanel;
	private JButton orderButton;
	private JButton transactionButton;
	private JButton kitchenButton;
	private JButton reservationButton;
	private JButton reportingButton;
	private JButton menuButton;
	private JButton setupButton;
	private JButton employeeButton;
	private ImageIcon homepageBackground;
	private JLabel homepageBackgroundLabel;
	private ImageIcon homepageBackgroundLocked;
	private JLabel homepageBackgroundLabelLocked;
	private JButton openFileButton;
	private JButton saveFileButton;
	private JButton logoutButton;
	private JButton loginButton;
	private JButton closeFileButton;
	private JLabel employeeNameLabel;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Restaurant restaurant = new Restaurant();
				restaurant.setVisible(true);
			}
		});
	}

	public Restaurant() {
		initUI();
	}

	private void initUI() {

		mainPanel = new JPanel();
		mainPanel.setLayout(null);
		getContentPane().add(mainPanel);

		orderButton = new JButton(new ImageIcon(getClass().getResource("orders button.JPG")));
		orderButton.setBounds(50, 150, 125, 125);
		orderButton.addActionListener(new ButtonListener());
		mainPanel.add(orderButton);

		transactionButton = new JButton(new ImageIcon(getClass().getResource("transactions button.JPG")));
		transactionButton.setBounds(200, 150, 125, 125);
		transactionButton.addActionListener(new ButtonListener());
		mainPanel.add(transactionButton);

		kitchenButton = new JButton(new ImageIcon(getClass().getResource("kitchen button.JPG")));
		kitchenButton.setBounds(350, 150, 125, 125);
		kitchenButton.addActionListener(new ButtonListener());
		mainPanel.add(kitchenButton);

		menuButton = new JButton(new ImageIcon(getClass().getResource("menu button.JPG")));
		menuButton.setBounds(500, 150, 125, 125);
		menuButton.addActionListener(new ButtonListener());
		mainPanel.add(menuButton);

		reservationButton = new JButton(new ImageIcon(getClass().getResource("reservations button.JPG")));
		reservationButton.setBounds(50, 300, 125, 125);
		reservationButton.addActionListener(new ButtonListener());
		mainPanel.add(reservationButton);

		employeeButton = new JButton(new ImageIcon(getClass().getResource("employee button.JPG")));
		employeeButton.setBounds(200, 300, 125, 125);
		employeeButton.addActionListener(new ButtonListener());
		mainPanel.add(employeeButton);

		reportingButton = new JButton(new ImageIcon(getClass().getResource("reporting button.JPG")));
		reportingButton.setBounds(350, 300, 125, 125);
		reportingButton.addActionListener(new ButtonListener());
		mainPanel.add(reportingButton);

		setupButton = new JButton(new ImageIcon(getClass().getResource("setup button.JPG")));
		setupButton.setBounds(500, 300, 125, 125);
		setupButton.addActionListener(new ButtonListener());
		mainPanel.add(setupButton);

		loginButton = new JButton(new ImageIcon(getClass().getResource("homepage login button.JPG")));
		loginButton.setBounds(865, 75, 120, 75);
		loginButton.addActionListener(new ButtonListener());
		mainPanel.add(loginButton);

		logoutButton = new JButton(new ImageIcon(getClass().getResource("logout button.JPG")));
		logoutButton.setBounds(865, 75, 120, 75);
		logoutButton.addActionListener(new ButtonListener());
		mainPanel.add(logoutButton);

		openFileButton = new JButton(new ImageIcon(getClass().getResource("open file button.JPG")));
		openFileButton.setBounds(865, 165, 120, 75);
		openFileButton.addActionListener(new ButtonListener());
		mainPanel.add(openFileButton);

		saveFileButton = new JButton(new ImageIcon(getClass().getResource("save file button.JPG")));
		saveFileButton.setBounds(865, 255, 120, 75);
		saveFileButton.addActionListener(new ButtonListener());
		mainPanel.add(saveFileButton);

		closeFileButton = new JButton(new ImageIcon(getClass().getResource("close file button.JPG")));
		closeFileButton.setBounds(865, 345, 120, 75);
		closeFileButton.addActionListener(new ButtonListener());
		mainPanel.add(closeFileButton);

		// background image when logged out
		homepageBackgroundLocked = new ImageIcon(getClass().getResource("freshqo homepage locked.JPG"));
		homepageBackgroundLabelLocked = new JLabel(homepageBackgroundLocked);
		homepageBackgroundLabelLocked.setBounds(0, 0, 1000, 600);
		mainPanel.add(homepageBackgroundLabelLocked);

		// background image
		homepageBackground = new ImageIcon(getClass().getResource("freshqo homepage.JPG"));
		homepageBackgroundLabel = new JLabel(homepageBackground);
		homepageBackgroundLabel.setBounds(0, 0, 1000, 600);
		mainPanel.add(homepageBackgroundLabel);

		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				closeApplication();
			}
		});

		loadConfigurationAndData();
		if (employees.size() == 0) {
			JOptionPane.showMessageDialog(null,
					"Welcome to Freshqo! As there are currently no employees added, there will be an already set username and password to login.");
			JOptionPane.showMessageDialog(null,
					"Once logged in, please add a manager to the database before creating any other employees.");
			String newPassword = JOptionPane.showInputDialog(
					"A default user is created for you. UserID: manager. Please enter a new password.");
			System.out.println(newPassword);

			Manager newManager = new Manager("Manager", 0.0, "manager", newPassword, "", "", "", "Manager");
			addEmployee(newManager);
		}

		disableButtons();

		// icon image and size
		setDefaultLookAndFeelDecorated(true);
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("freshqo icon.JPG")));
		setTitle("Freshqo Management");
		setSize(1000, 600);
		setResizable(false);
		setUndecorated(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

	}

	/**
	 * closeApplication Asks if the user is sure they want to close the program. If
	 * yes, it will automatically save the data.
	 */
	protected void closeApplication() {
		int ret = JOptionPane.showConfirmDialog(mainPanel, "Are you sure you want to close the application?",
				"Close Application", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (ret == JOptionPane.YES_OPTION) {
			saveConfigurationAndData();
			System.exit(0);
		}
	}

	/**
	 * openDataFile Opens user's own file explorer and allows user to choose a data
	 * file the file to be opened that was previously saved from before
	 */
	protected void openDataFile() {
		JFileChooser fileopen = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("Application data files", ".fqo");
		fileopen.addChoosableFileFilter(filter);

		int ret = fileopen.showDialog(mainPanel, "Open file");
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = fileopen.getSelectedFile();
			try {
				loadDataFile(file);
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "The file name could not be found.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;

			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "The file cannot be used with this program.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
	}

	/**
	 * loadDataFile load the RecipeDatabase from the file specified by the filename.
	 *
	 * @param filename the file name of the database file.
	 * @throws FileNotFoundException  if the file cannot be found
	 * @throws IOException            if there is an error in reading the file
	 * @throws ClassNotFoundException the RecipeDatabase class cannot be found.
	 * @throws ClassCastException     the data stored in the file is not a
	 *                                RecipeDatabase object
	 */
	private void loadDataFile(String filename)
			throws FileNotFoundException, IOException, ClassNotFoundException, ClassCastException {
		loadDataFile(new File(filename));
	}

	/**
	 * loadDataFile load the RecipeDatabase from the file specified.
	 *
	 * @param file the File object defining the database file.
	 * @throws FileNotFoundException  if the file cannot be found
	 * @throws IOException            if there is an error in reading the file
	 * @throws ClassNotFoundException the RecipeDatabase class cannot be found.
	 * @throws ClassCastException     the data stored in the file is not a
	 *                                RecipeDatabase object
	 */
	@SuppressWarnings("unchecked")
	private void loadDataFile(File file)
			throws FileNotFoundException, IOException, ClassNotFoundException, ClassCastException {
		System.out.println(file.getAbsolutePath());
		this.configuration.setProperty("database.filename", file.getAbsolutePath());
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
			tables = (List<Table>) ois.readObject();
			reservationBook = (List<Reservation>) ois.readObject();
			menu = (List<MenuItem>) ois.readObject();
			waitingList = (CustomerQueue<Customer>) ois.readObject();
			employees = (DoublyLinkedList<Employee>) ois.readObject();
			historicalTransactions = (List<TableOrder>) ois.readObject();
			kitchenOrders = (Queue<TableOrderItem>) ois.readObject();
		}
	}

	/**
	 * saveDataFile save data file saves the file under its own unique extension
	 */
	protected void saveDataFile() {
		JFileChooser filesave = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("Application data files", ".fqo");
		filesave.addChoosableFileFilter(filter);

		int ret = filesave.showDialog(mainPanel, "Save file");
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = filesave.getSelectedFile();
			saveDataFile(file);
		}
	}

	/**
	 * saveDataFile converts the String filename into a file
	 *
	 * @param filename the name of the file to be saved
	 */
	private void saveDataFile(String filename) {
		saveDataFile(new File(filename));
	}

	/**
	 * saveDataFile saves the data into a file
	 *
	 * @param file the file which will hold the saved data
	 */
	private void saveDataFile(File file) {
		this.configuration.setProperty("database.filename", file.getAbsolutePath());
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
			oos.writeObject(tables);
			oos.writeObject(reservationBook);
			oos.writeObject(menu);
			oos.writeObject(waitingList);
			oos.writeObject(employees);
			oos.writeObject(historicalTransactions);
			oos.writeObject(kitchenOrders);
		} catch (IOException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, "The file cannot be used with this program.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	protected Properties configuration = new Properties();
	public static final String CONFIGURATION_FILENAME = "restaurant.properties";
	public static final String DEFAULT_DATABASE_FILENAME = "restaurant management.fqo";

	/**
	 * saveConfigurationAndData saves configuration and data under the file
	 */
	protected void saveConfigurationAndData() {

		String dbFilename = this.configuration.getProperty("database.filename", DEFAULT_DATABASE_FILENAME);
		this.saveDataFile(dbFilename);
		this.configuration.setProperty("database.filename", dbFilename);
		try {
			this.configuration.store(new FileOutputStream(CONFIGURATION_FILENAME), "");
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "The file type cannot be used to save your data.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * loadConfigurationAndData loads configuration and data from a saved file
	 */
	protected void loadConfigurationAndData() {
		try {
			this.configuration.load(new FileInputStream(CONFIGURATION_FILENAME));
			String dbFilename = this.configuration.getProperty("database.filename", DEFAULT_DATABASE_FILENAME);
			try {
				this.loadDataFile(dbFilename);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,
						"The data file cannot be open or does not exist. A default one will be created on the exit of the application.",
						"Warning", JOptionPane.WARNING_MESSAGE);
			} catch (ClassNotFoundException | ClassCastException e) {
				JOptionPane.showMessageDialog(null,
						"The data file is corrupt and hence cannot be used. A new data file will be created on the exit of the application.",
						"Warning", JOptionPane.WARNING_MESSAGE);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					"The configuration file does not exist or is corrupt. The default value will be used.", "Warning",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	public List<MenuItem> getMenu() {
		return menu;
	}

	public List<MenuItem> getAppetizerMenu() {
		List<MenuItem> appetizerMenu = new ArrayList<>();
		for (int i = 0; i < menu.size(); i++) {
			if (menu.get(i).getCategory().equals("Appetizer")) {
				appetizerMenu.add(menu.get(i));
			}
		}
		return appetizerMenu;
	}

	public List<MenuItem> getEntreeMenu() {
		List<MenuItem> entreeMenu = new ArrayList<>();
		for (int i = 0; i < menu.size(); i++) {
			if (menu.get(i).getCategory().equals("Entree")) {
				entreeMenu.add(menu.get(i));
			}
		}
		return entreeMenu;
	}

	public List<MenuItem> getDessertMenu() {
		List<MenuItem> dessertMenu = new ArrayList<>();
		for (int i = 0; i < menu.size(); i++) {
			if (menu.get(i).getCategory().equals("Dessert")) {
				dessertMenu.add(menu.get(i));
			}
		}
		return dessertMenu;
	}

	public List<MenuItem> getBeverageMenu() {
		List<MenuItem> beverageMenu = new ArrayList<>();
		for (int i = 0; i < menu.size(); i++) {
			if (menu.get(i).getCategory().equals("Beverage")) {
				beverageMenu.add(menu.get(i));
			}
		}
		return beverageMenu;
	}

	public List<Reservation> getReservationBook() {
		return reservationBook;
	}

	public List<Waiter> getWaiters() {
		List<Waiter> waiters = new DoublyLinkedList<>();
		for (int i = 0; i < employees.size(); i++) {
			if (employees.get(i) instanceof Waiter) {
				waiters.add((Waiter) employees.get(i));
			}
		}

		return waiters;
	}

	public List<Chef> getChefs() {
		List<Chef> chefs = new DoublyLinkedList<>();
		for (int i = 0; i < employees.size(); i++) {
			if (employees.get(i) instanceof Chef) {
				chefs.add((Chef) employees.get(i));
			}
		}

		return chefs;
	}

	public List<Manager> getManagers() {
		List<Manager> managers = new DoublyLinkedList<>();
		for (int i = 0; i < employees.size(); i++) {
			if (employees.get(i) instanceof Manager) {
				managers.add((Manager) employees.get(i));
			}
		}

		return managers;
	}

	public List<Table> getTables() {
		return tables;
	}

	public List<Table> getReservableTables() {
		List<Table> reservableTables = new ArrayList<>();
		for (int i = 0; i < tables.size(); i++) {
			if (tables.get(i).canBeReserved()) {
				reservableTables.add(tables.get(i));
			}
		}
		return reservableTables;
	}

	public List<Table> getReservableTables(int numPeople, ReservationDateTime reserveTimePeriod) {
		List<Table> reservableTables = new ArrayList<>();
		for (int i = 0; i < tables.size(); i++) {
			if (tables.get(i).canBeReserved() && tables.get(i).getNumSeats() >= numPeople) {
				if (!reserveTimePeriod.getSecuredTimePeriodFrom().isBefore(LocalTime.now())
						|| !tables.get(i).isOccupied()) {
					reservableTables.add(tables.get(i));
				}
			}
		}
		return reservableTables;
	}

	public List<Table> findAvailableTableForReservation(int numPeople, ReservationDateTime reserveTimePeriod) {
		// save reserve tables to an array
		// check date and save "already made" reservations to an array
		// check each reservation to see if its within the time range
		// if not, remove table from reserve tables

		List<Table> availableTableForReservation = getReservableTables(numPeople, reserveTimePeriod);
		List<Reservation> savedReservationsForDate = new ArrayList<>();

		for (int i = 0; i < reservationBook.size(); i++) {
			if (reservationBook.get(i).getReservationDateTime().getLocalDate()
					.equals(Utils.convertToLocalDate(reserveTimePeriod.getDate()))) {
				savedReservationsForDate.add(reservationBook.get(i));
			}
		}
		for (int i = 0; i < savedReservationsForDate.size(); i++) {
			if (!savedReservationsForDate.get(i).isClaimed()
					&& availableTableForReservation.contains(savedReservationsForDate.get(i).getTable())) {
				if (savedReservationsForDate.get(i).getReservationDateTime().getSecuredTimePeriodFrom()
						.isBefore(reserveTimePeriod.getLocalTime())
						&& savedReservationsForDate.get(i).getReservationDateTime().getSecuredTimePeriodTo()
						.isAfter(reserveTimePeriod.getLocalTime())) {
					availableTableForReservation.remove(savedReservationsForDate.get(i).getTable());
				}

			}
		}
		return availableTableForReservation;
	}

	public void bookReservation(Reservation reservation) {
		reservationBook.add(reservation);
		JOptionPane.showMessageDialog(null, "Reservation has been added.");
	}

	public void claimReservation(String customerName) {
		boolean found = false;
		Reservation reservation = null;
		Table reservedTable = null;
		Waiter waiter = findAvailableWaiter();
		for (int i = 0; i < reservationBook.size(); i++) {
			if ((!reservationBook.get(i).isClaimed())
					&& reservationBook.get(i).getCustomer().getName().equals(customerName)) {
				found = true;
				reservation = reservationBook.get(i);
				break;
			}
		}
		if (!found) {
			JOptionPane.showMessageDialog(null, "There is no reservation under that name", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		JOptionPane.showMessageDialog(null, "Reservation has been claimed by " + customerName + " for "
				+ reservation.getReservationDateTime().getLocalTime());

		reservation.setClaimed(true);
		reservedTable = reservation.getTable();
		reservedTable.setCustomer(reservation.getCustomer());
		reservedTable.setOccupied(true);
		reservedTable.setCurrentAssignedWaiter(waiter);
		reservedTable.setCurrentOrder(new TableOrder(reservedTable));
		reservedTable.getCurrentOrder().setWaiter(waiter);
		waiter.getAssignedTables().add(reservedTable);

		JOptionPane.showMessageDialog(null, "They have been successfully placed at Table "
				+ reservedTable.getTableName() + ". They will be served by " + waiter.getName());

	}

	public void addEmployee(Employee employee) {
		employees.add(employee);
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public Employee getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(Employee currentUser) {
		this.currentUser = currentUser;
	}

	class ButtonListener implements ActionListener {

		/**
		 * actionPerformed performs the action that is needed to be performed from
		 * clicking a button
		 *
		 * @param press used to determine which button is pressed
		 */
		public void actionPerformed(ActionEvent press) {
			if (press.getSource() == orderButton) {
				if (waitingList.size() > 0) {
					checkWaitingList();
				}
				OrderDialog orderDialog = new OrderDialog(self);
			} else if (press.getSource() == transactionButton) {
				TransactionDialog transactionDialog = new TransactionDialog(self);
			} else if (press.getSource() == kitchenButton) {
				KitchenDialog kitchenDialog = new KitchenDialog(self);

			} else if (press.getSource() == menuButton) {
				MenuDialog menuDialog = new MenuDialog(self);

			} else if (press.getSource() == setupButton) {
				SetupDialog setupDialog = new SetupDialog(self);

			} else if (press.getSource() == reservationButton) {
				ReservationBookDialog reservationBookDialog = new ReservationBookDialog(self);

			} else if (press.getSource() == employeeButton) {
				if (getManagers().size() == 0) {
					JOptionPane.showMessageDialog(null,
							"You must first create a manager before creating other employees.");
				}
				EmployeeDialog employeeDialog = new EmployeeDialog(self);

			} else if (press.getSource() == logoutButton) {
				logout();
			} else if (press.getSource() == openFileButton) {
				openDataFile();
			} else if (press.getSource() == saveFileButton) {
				saveDataFile();
			} else if (press.getSource() == closeFileButton) {
				closeApplication();
			} else if (press.getSource() == loginButton) {
				login();
			}
		}
	}

	public Table findAvailableTableForWalkInCustomer(Customer customer) {

		List<Table> availableTables = new ArrayList<>();

		for (int i = 0; i < tables.size(); i++) {
			if (!tables.get(i).isOccupied()) {
				availableTables.add(tables.get(i));
			}
		}

		for (int i = 0; i < reservationBook.size(); i++) {
			if (reservationBook.get(i).getReservationDateTime().getLocalDate().equals(LocalDate.now())
					&& LocalTime.now()
					.isBefore(reservationBook.get(i).getReservationDateTime().getSecuredTimePeriodTo())
					&& LocalTime.now()
					.isAfter(reservationBook.get(i).getReservationDateTime().getSecuredTimePeriodFrom())) {
				availableTables.remove(reservationBook.get(i).getTable());
			}
		}

		if (availableTables.size() > 0) {
			return getAppropriateTable(availableTables, customer.getNumPeople());
		} else {
			return null;
		}
	}

	public void checkWaitingList() {
		if (waitingList.size() == 0) {
			return;
		}
		for (int i = 0; i < tables.size(); i++) {
			if (!tables.get(i).isOccupied()) {
				Table table = tables.get(i);
				int numSeats = table.getNumSeats();
				Customer customer = waitingList.dequeue(numSeats);
				tables.get(i).setCustomer(customer);
				if (tables.get(i).getCustomer() != null) {
					JOptionPane.showMessageDialog(null,
							"From the waiting list, " + customer.getName() + " has been placed at a table.");
					table.setOccupied(true);
					Waiter waiter = findAvailableWaiter();
					table.setCurrentAssignedWaiter(waiter);
					table.setCurrentOrder(new TableOrder(table));
					table.getCurrentOrder().setWaiter(waiter);
					waiter.getAssignedTables().add(table);
				}
			}
		}
	}

//	public void changeAccess() {
//		if (currentUser instanceof Chef) {
//			reservationButton.disable();
//			employeeButton.disable();
//			setupButton.disable();
//
//		}
//	}

	public void login() {
		LoginFrame loginFrame = new LoginFrame(self);
	}

	public void initializeSuccessfulLogin() {
		if (currentUser != null) {
			homepageBackgroundLabelLocked.setVisible(false);

			employeeNameLabel = new JLabel("Hello " + currentUser.getName());
			employeeNameLabel.setBounds(700, 20, 100, 30);
			employeeNameLabel.setForeground(Color.white);
			mainPanel.add(employeeNameLabel);
			mainPanel.add(homepageBackgroundLabel);
			enableButtons();
		}
	}

	private void logout() {
		mainPanel.remove(employeeNameLabel);
//		employeeNameLabel = null;
		mainPanel.remove(homepageBackgroundLabel);
		homepageBackgroundLabelLocked.setVisible(true);
		disableButtons();
		currentUser = null;
	}

	private void enableButtons() {

		orderButton.setVisible(true);
		transactionButton.setVisible(true);
		kitchenButton.setVisible(true);
		menuButton.setVisible(true);
		reservationButton.setVisible(true);
		employeeButton.setVisible(true);
		reportingButton.setVisible(true);
		setupButton.setVisible(true);
		loginButton.setVisible(false);
		logoutButton.setVisible(true);

	}

	private void disableButtons() {
		orderButton.setVisible(false);
		transactionButton.setVisible(false);
		kitchenButton.setVisible(false);
		menuButton.setVisible(false);
		reservationButton.setVisible(false);
		employeeButton.setVisible(false);
		reportingButton.setVisible(false);
		setupButton.setVisible(false);
		loginButton.setVisible(true);
		logoutButton.setVisible(false);

	}

	private Table getAppropriateTable(List<Table> availableTables, int numPeople) {
		int minDifference = Integer.MAX_VALUE;
		Table bestTable = null;
		for (int i = 0; i < availableTables.size(); i++) {
			if (availableTables.get(i).getNumSeats() - numPeople >= 0
					&& availableTables.get(i).getNumSeats() - numPeople < minDifference) {
				bestTable = availableTables.get(i);
				minDifference = availableTables.get(i).getNumSeats();
			}
		}
		return bestTable;
	}

	public CustomerQueue<Customer> getWaitingList() {
		return waitingList;
	}

	public List<Customer> getWaitingListInListForm() {
//		Queue<Customer>
		List<Customer> waitingListForm = new ArrayList<>();
//		waitingList.toArray();

		return waitingListForm;
	}

	public Waiter findAvailableWaiter() {
		int minTables = Integer.MAX_VALUE;
		Waiter waiter = null;
		for (int i = 0; i < getWaiters().size(); i++) {
			if (getWaiters().get(i).getAssignedTables().size() < minTables) {
				waiter = getWaiters().get(i);
				minTables = getWaiters().get(i).getAssignedTables().size();
				System.out.println(waiter.getName() + minTables);
			}
		}
		return waiter;
	}

	public List<TableOrder> getHistoricalTransactions() {
		return historicalTransactions;
	}

	public void setHistoricalTransactions(List<TableOrder> historicalTransactions) {
		this.historicalTransactions = historicalTransactions;
	}

	public Queue<TableOrderItem> getKitchenOrders() {
		return kitchenOrders;
	}

	public void setKitchenOrders(Queue<TableOrderItem> kitchenOrders) {
		this.kitchenOrders = kitchenOrders;
	}

}