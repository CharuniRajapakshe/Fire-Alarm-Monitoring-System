package server;

import java.io.IOException;
import java.io.ObjectInputStream;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import database.Database;
import monitor.FireAlarmListener;
import sensor.SensorMessage;

public class FireEmergencyServer {

	private static final int PORT = 9001;

	public static void main(String[] args) throws Exception {
		
		/**
		 * Socket server just listens on a port and spawns handler
		 * threads.
		 */
		System.out.println("The Fire Alarm sensor server is running.");
		ServerSocket listener = new ServerSocket(PORT);

		//runs the rmi server
		Thread thread = new Thread(new FireAlarmSensorServer());
		thread.run();

		try {
			while (true) {
				new FireSensorServer.Handler(listener.accept()).start();
			}
		} finally {
			listener.close();
		}
	}

	// socket server
	static class FireSensorServer {
		
		/**
		 * The set of all id s of sensors connected. Maintained so that we can
		 * check that same sensor id is not used multiple times.
		 */
		private static HashSet<String> sensors = new HashSet<String>();

		private static boolean validateId(String id) {

			if (id.contains("-")) {
				try {

					String[] sId = id.split("-");

					if (sId.length != 2) {
						return false;
					}

					for (String s : sId) {
						Integer.parseInt(s);
						return true;
					}
				} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
				}
			}
			return false;
		}

		static class Handler extends Thread {

			String id;
			SensorMessage sensor;
			private Socket socket;
			private PrintWriter out = null;
			private ObjectInputStream oIn = null;
			Database db = new Database();

			public Handler(Socket socket) {
				this.socket = socket;
			}
			
			/**
			 * Services this thread's client by repeatedly requesting a id until a
			 * valid one has been submitted, then acknowledges the id and registers the
			 * object output stream for the sensor in a global set, then repeatedly gets inputs and
			 * write to the sensor history.xml file them.
			 */

			public void run() {

				try {
					out = new PrintWriter(socket.getOutputStream(), true);
					oIn = new ObjectInputStream(socket.getInputStream());

					while (true) {
						out.println("SUBMITID");
						sensor = (SensorMessage) oIn.readObject();
						id = sensor.getId();

						if (sensor.type.equals("CONNECT")) {
							if (id == null) {
								return;
							}
							synchronized (sensors) {

								if (validateId(id) && !sensors.contains(id)) {
									sensors.add(id);
									break;
								}
							}
						}
					}

					out.println("IDACCEPTED" + id);
					while (true) {

						sensor = (SensorMessage) oIn.readObject();
						DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
						Date date = new Date();
						System.out.println(dateFormat.format(date) + "::" + sensor.type + " ::" + sensor.getId() + ":::"
								+ sensor.getTemp() + "::" + sensor.getSmoke()); // 2016/11/16 12:08:43
						sensor.date = dateFormat.format(date).toString();

						synchronized (sensor) {
							db.addDeleteSensorData(sensor, "add");
						}

					}
				} catch (IOException e) {
					System.out.println(e);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (id != null) {
						sensors.remove(sensor.getId());
						db.addDeleteSensorData(sensor, "del");
					}
					try {
						socket.close();
					} catch (IOException e) {
					}
				}
			}
		}

	}

	// RMI server
	static class FireAlarmSensorServer extends UnicastRemoteObject implements FireAlarmSensor, Runnable {

		private static Database db;

		private static Vector<FireAlarmListener> list = new Vector<FireAlarmListener>();

		protected FireAlarmSensorServer() throws java.rmi.RemoteException {
			db = new Database();
		}

		// return the list of current sensor data when called
		public List<SensorMessage> getFireAlarmSensorData() throws java.rmi.RemoteException {
			System.out.println("getFireAlarmSensorData");

			List<SensorMessage> list = db.readSensorData("SensorHistory.xml");
			return list;
		}

		// Register new Fire Alarm listerners
		public void addFireAlarmListener(FireAlarmListener listener) throws java.rmi.RemoteException {
			System.out.println("adding listener -" + listener);
			list.add(listener);
			synchronized (list) {
				notifyListeners(list.size());
			}
		}

		// Remove listerners from the registered list
		public void removeFireAlarmListener(FireAlarmListener listener) throws java.rmi.RemoteException {
			System.out.println("removing listener -" + listener);
			list.remove(listener);
			synchronized (list) {
				notifyListeners(list.size());
			}
		}

		// Notify every listener in the registered list if there is a change in registered monitor count
		public static void notifyListeners(int no) {
			
			for (Enumeration<FireAlarmListener> e = list.elements(); e.hasMoreElements();) {
				FireAlarmListener listener = e.nextElement();

				try {
					listener.notifyCount(no);

				} catch (RemoteException re) {

					System.out.println("removing listener -" + listener);
					list.remove(listener);
				}
			}
		}

		//Notify every listener in the registered list if there is an emergency situation or an one hour data update event 
		public static void notifyListeners(List<SensorMessage> senList, String type) {
			// Get every listener in the registered list(FireAlarmListener list) and pass
			// the changed sensor list

			for (Enumeration<FireAlarmListener> e = list.elements(); e.hasMoreElements();) {
				FireAlarmListener listener = e.nextElement();

				try {
					//1hr updates
					if (type.equalsIgnoreCase("update")) {
						System.out.println("notify updates 1hr");
						listener.notifyUpdates(senList);
					}
					//emergency 
					else if (type.equalsIgnoreCase("emergency")) {
						System.out.println("notify emergency and sensor not responding events");
						listener.notifyEmergency(senList);
					}

				} catch (RemoteException re) {

					System.out.println("removing listener -" + listener);
					list.remove(listener);
				}
			}

		}

		@Override
		public void run() {
			System.out.println("Loading Fire alarm sensor service....");
			
			try {
				LocateRegistry.createRegistry(1099);
				FireAlarmSensorServer sensor = new FireAlarmSensorServer();
				String registry = "localhost";

				String registration = "rmi://" + registry + "/FireAlarmSensor";

				Naming.rebind(registration, sensor);

			} catch (RemoteException re) {
				System.err.println("Remote Error - " + re);
			} catch (Exception e) {
				System.err.println("Error - " + e);
			}

			// for getting current updates from sensors and check for threats
			DBReader myRunnable = new DBReader();
			Thread t = new Thread(myRunnable);
			t.start();

			// for getting 1hr updates from sensors
			MonitorUpdate myRunnable_1 = new MonitorUpdate();
			Thread t_1 = new Thread(myRunnable_1);
			t_1.start();

		}

		//return the selected sensorns current readings
		@Override
		public SensorMessage getCurrentData(String id) throws RemoteException {
			// TODO Auto-generated method stub
			Database db = new Database();
			SensorMessage ms = db.readCurrentData(id, "SensorHistory.xml");
			
			return ms;
		}

		//return the boolean login success or failed  
		@Override
		public boolean login(String un, String pw) throws RemoteException {
			Database db = new Database();
			return db.checkLogin(un, pw, "Login.xml");
		}
	}
}
