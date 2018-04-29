package monitor;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
//import org.eclipse.swt.widgets.ListList;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;

import sensor.SensorMessage;
import server.FireAlarmSensor;

import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;

public class MonitorGUI extends UnicastRemoteObject implements FireAlarmListener, Runnable {

	protected MonitorGUI() throws RemoteException {

		// TODO Auto-generated constructor stub
	}

	protected static Shell shell;
	private CLabel lblc;
	private CLabel lblPpm;
	private CLabel label_3;
	private Table table;
	private Combo combo;
	private Label nMo;
	private Label nSe;
	private static CLabel temp;
	private static CLabel smoke;
	private static CLabel co2;
	private static CLabel bat;
	private Label lblGg;
	/**
	 * Launch the application.
	 * 
	 * @param args
	 */

	private static FireAlarmSensor sensor;
	private static MonitorGUI monitor;
	static Display display = null;
	static LoginDialog ld;

	public static void main(String[] args) {
		try {

			String registration = "//localhost/FireAlarmSensor";

			Remote remoteService = Naming.lookup(registration);
			sensor = (FireAlarmSensor) remoteService;
			monitor = new MonitorGUI();

			boolean uLogin = false;

			//create LoginDialog Instance and open the User Login
			//calls the login method implemented by the server
			//until the login return true,login dialog is displayed
			//when login success MonitoGUI is displayed
			ld = new LoginDialog(shell);

			while (!uLogin) {
				if (ld.open() == Window.OK) {
					uLogin = sensor.login(ld.getUser(), ld.getPassword());
				}
			}
			// TO DO: Add method call to register the listener in the server object
			System.out.println("add listener");

			monitor.open();

		} catch (MalformedURLException mue) {
		} catch (RemoteException re) {
		} catch (NotBoundException nbe) {
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		display = Display.getDefault();
		createContents();

		// when the monitor window is closed event handler calls the
		// removeFireAlarmListener and unregister the listener
		shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					sensor.removeFireAlarmListener(monitor);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.exit(0);
			}
		});
		shell.open();
		shell.layout();
		// call the server for registering listener(monitor instance)in the rmi server
		try {
			sensor.addFireAlarmListener(monitor);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION_TEXT));
		shell.setSize(810, 417);
		shell.setText("Fire Sensor Monitor");

		CLabel lblTemperature = new CLabel(shell, SWT.NONE);
		lblTemperature.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		lblTemperature.setBounds(30, 240, 88, 21);
		lblTemperature.setText("Temperature");

		CLabel lblSmokeLevel = new CLabel(shell, SWT.NONE);
		lblSmokeLevel.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		lblSmokeLevel.setText("Smoke Level");
		lblSmokeLevel.setBounds(30, 267, 88, 21);

		CLabel lblCoLevel = new CLabel(shell, SWT.NONE);
		lblCoLevel.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		lblCoLevel.setText("Co2 Level");
		lblCoLevel.setBounds(30, 294, 88, 21);

		CLabel lblBattery = new CLabel(shell, SWT.NONE);
		lblBattery.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		lblBattery.setText("Battery");
		lblBattery.setBounds(30, 321, 88, 21);

		lblc = new CLabel(shell, SWT.NONE);
		lblc.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		lblc.setText("\u00B0C");
		lblc.setBounds(206, 240, 34, 21);

		lblPpm = new CLabel(shell, SWT.NONE);
		lblPpm.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		lblPpm.setText("ppm");
		lblPpm.setBounds(206, 294, 41, 21);

		label_3 = new CLabel(shell, SWT.NONE);
		label_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		label_3.setText("%");
		label_3.setBounds(206, 321, 41, 21);

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(274, 137, 489, 205);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(62);
		tblclmnNewColumn.setText("Sensor ID");

		TableColumn tblclmnNewColumn_5 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_5.setWidth(59);
		tblclmnNewColumn_5.setText("Location");

		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(72);
		tblclmnNewColumn_1.setText("Temperature");

		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_2.setWidth(79);
		tblclmnNewColumn_2.setText("Smoke Level");

		TableColumn tblclmnNewColumn_3 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_3.setWidth(65);
		tblclmnNewColumn_3.setText("CO2 Level");

		TableColumn tblclmnNewColumn_4 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_4.setWidth(49);
		tblclmnNewColumn_4.setText("Battery");

		TableColumn tblclmnResposedDateTime = new TableColumn(table, SWT.NONE);
		tblclmnResposedDateTime.setWidth(100);
		tblclmnResposedDateTime.setText("Responsed");

		Label label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(10, 75, 753, 2);

		combo = new Combo(shell, SWT.NONE);
		combo.setBounds(124, 169, 76, 23);
		combo.add("Select ID");
		combo.select(0);

		CLabel lblSensorId = new CLabel(shell, SWT.NONE);
		lblSensorId.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		lblSensorId.setText("Sensor ID");
		lblSensorId.setBounds(30, 169, 88, 21);

		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		btnNewButton.setGrayed(true);

		// When enter button in the monitor is clicked,sensor Id combo box value is
		// collected and calls the getCurrent data method,implemented by the rmi server
		// get the return data and calls the set currentdata method for setting data for
		// sensor data fields
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					String s = combo.getText();
					if (!s.equalsIgnoreCase("Select ID")) {
						SensorMessage sm = sensor.getCurrentData(s);
						if (sm.getId() != null) {
							setCurrentData(sm);
						} else {
							MessageDialog.openInformation(shell, "Sensor Manager",
									"Selected sensor is currently removed from the system.");
						}
					} else {
						MessageDialog.openError(shell, "Error", "Select a sensor ID.");
					}
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(124, 198, 75, 25);
		btnNewButton.setText("Enter");

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
		lblNewLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		lblNewLabel.setBounds(30, 97, 130, 19);
		lblNewLabel.setText("Get Current Data");

		Label lblFireSensorReadings = new Label(shell, SWT.NONE);
		lblFireSensorReadings.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		lblFireSensorReadings.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		lblFireSensorReadings.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
		lblFireSensorReadings.setText("Fire Sensor Readings");
		lblFireSensorReadings.setBounds(274, 95, 160, 21);

		Label label_1 = new Label(shell, SWT.SEPARATOR | SWT.VERTICAL);
		label_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		label_1.setBounds(246, 82, 12, 271);

		Label lblNoOfMonitors = new Label(shell, SWT.NONE);
		lblNoOfMonitors.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		lblNoOfMonitors.setBounds(30, 24, 88, 21);
		lblNoOfMonitors.setText("No of Monitors");

		Label lblNoOfSensors = new Label(shell, SWT.NONE);
		lblNoOfSensors.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		lblNoOfSensors.setForeground(SWTResourceManager.getColor(0, 0, 128));
		lblNoOfSensors.setText("No of Sensors");
		lblNoOfSensors.setBounds(30, 136, 88, 21);

		nMo = new Label(shell, SWT.BORDER | SWT.CENTER);
		nMo.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		nMo.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		nMo.setBounds(124, 25, 76, 21);

		nSe = new Label(shell, SWT.BORDER | SWT.CENTER);
		nSe.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		nSe.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		nSe.setBounds(124, 136, 76, 21);

		temp = new CLabel(shell, SWT.BORDER);
		temp.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		temp.setText("");
		temp.setBounds(124, 240, 76, 21);

		smoke = new CLabel(shell, SWT.BORDER);
		smoke.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		smoke.setText("");
		smoke.setBounds(124, 267, 76, 21);

		co2 = new CLabel(shell, SWT.BORDER);
		co2.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		co2.setText("");
		co2.setBounds(124, 294, 76, 21);

		bat = new CLabel(shell, SWT.BORDER);
		bat.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		bat.setText("");
		bat.setBounds(124, 321, 76, 21);

		lblGg = new Label(shell, SWT.RIGHT);
		lblGg.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		lblGg.setFont(SWTResourceManager.getFont("Tw Cen MT", 15, SWT.NORMAL));
		lblGg.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		lblGg.setBounds(449, 18, 314, 32);
		lblGg.setText("Welcome " + ld.getUser() + " !");

		try {
			getData();
		} catch (RemoteException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

	@Override
	public void run() {

	}

	// Get the current sensor readings and calls the notifyUpdates method for
	// setting data to sensor detail table, No of sensors connected and sensor list
	public void getData() throws RemoteException {

		List<SensorMessage> list = sensor.getFireAlarmSensorData();
		notifyUpdates(list);
		System.out.println("setting");

	}

	// set the text fields with the required sensor data
	private static void setCurrentData(final SensorMessage msg) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {

				if (!temp.isDisposed()) {
					temp.setText(Double.toString(msg.getTemp()));
					temp.getParent().layout();
				}
				if (!smoke.isDisposed()) {
					smoke.setText(Integer.toString(msg.getSmoke()));
					smoke.getParent().layout();
				}
				if (!co2.isDisposed()) {
					co2.setText(Integer.toString(msg.getCo2()));
					co2.getParent().layout();
				}
				if (!bat.isDisposed()) {
					bat.setText(Integer.toString(msg.getBattery()));
					bat.getParent().layout();
				}
			}
		});
	}

	// update the no of monitors label with the passed value
	@Override
	public void notifyCount(int no) throws RemoteException {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {

				if (!nMo.isDisposed()) {
					nMo.setText(Integer.toString(no));
					nMo.getParent().layout();
				}
			}
		});
	}

	// Update the sensor detail table, No of sensors connected and sensor list
	@Override
	public void notifyUpdates(List<SensorMessage> list) throws RemoteException {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("setting update");

				if (list != null) {

					// set no of sensors
					if (!nSe.isDisposed()) {
						nSe.setText(Integer.toString(list.size()));
						nSe.getParent().layout();
					}

					if (!combo.isDisposed()) {
						combo.removeAll();
						combo.add("Select ID");
						combo.select(0);
						combo.getParent().layout();
					}

					if (!table.isDisposed()) {
						table.removeAll();
						table.getParent().layout();
					}

					for (SensorMessage sensor : list) {

						// add data to table view
						TableItem item = new TableItem(table, SWT.NULL);
						item.setText("Sensor");
						item.setText(0, sensor.getId());
						item.setText(1, sensor.location);
						item.setText(2, Double.toString(sensor.getTemp()));
						item.setText(3, Integer.toString(sensor.getSmoke()));
						item.setText(4, Integer.toString(sensor.getCo2()));
						item.setText(5, Integer.toString(sensor.getBattery()));
						item.setText(6, sensor.date);

						// set data to sensor id list
						if (!combo.isDisposed()) {
							combo.add(sensor.getId());
							combo.getParent().layout();
						}

					}
				}
			}
		});

	}

	// Display an error message with the emergency type and sensor data
	@Override
	public void notifyEmergency(List<SensorMessage> list) throws RemoteException {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("notifying");
				String eString = "";// for emergency notifications
				String rString = "";// for sensor response notifications

				// if the responseFlage is set relevant sensor is considered as a non responsive
				// sensor and,doesn't check for emergency flag
				for (SensorMessage sens : list) {
					if (sens.isResponsFlag() == false) {
						rString = rString
								.concat("\nSensor ID : " + sens.getId() + "\nLast Response : " + sens.date + "\n");
					} else {
						eString = eString.concat("\nSensor ID:" + sens.getId() + "\nStatus  :\tTemperature : "
								+ sens.getTemp() + "\tSmoke Level : " + sens.getSmoke() + "\n");
					}

				}
				// for emergency message
				if (!eString.equals("")) {
					MessageDialog.openError(shell, "Emergency", eString);
				}
				// for sensor not responding message
				if (!rString.equals("")) {
					MessageDialog.openError(shell, "Sensor Response Alert", rString);
				}
			}
		});

	}

}
