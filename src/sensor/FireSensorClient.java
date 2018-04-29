package sensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
/**
 * A simple Swing-based sensor client for the socket server. Graphically it is a frame
 * with a messageArea for displaying sensor readings.
 * 
 *
 * The client follows the Protocol which is as follows. When the server
 * sends "SUBMITID" the Sensor replies with the desired Id. The
 * server will keep sending "SUBMITID" requests as long as the sensor submits
 * id s that are already in use or doesn't match the id format. When the server sends a line beginning
 * with "IDACCEPTED" the sensor is now allowed to start sending data to the server.
 **/

public class FireSensorClient {

	static FireSensorClient client;
	BufferedReader in;
	PrintWriter out;
	ObjectOutputStream oOut;
	static String id;
	static String location;
	double temp = 0;
	int battery = 0;
	int smoke = 0;
	int co2 = 0;

	JFrame frame = new JFrame("Sensor");
	JTextArea messageArea = new JTextArea(8, 40);

	public FireSensorClient() {

		frame.getContentPane().add(new JScrollPane(messageArea), "Center");
		frame.pack();
	}

	/**
	 * Prompt for and return the address of the server.
	 */
	private String getServerAddress() {
		return JOptionPane.showInputDialog(frame, "Enter IP Address of the Server:", "SensorManager",
				JOptionPane.QUESTION_MESSAGE);
	}

	/**
	 * Prompt for and return the desired sensor Id.
	 */
	private String getId() {
		return JOptionPane.showInputDialog(frame, "Enter the sensor ID :", "SensorManager", JOptionPane.PLAIN_MESSAGE);
	}

	private synchronized void measure() {

		boolean emerg = false;
		//generate random numbers for sensor details and reduced the probability of occurring emergency situations
		Random ran = new Random();

		double con_1 = ran.nextDouble() * 100;
		double con_2 = ran.nextDouble() * 100;

		temp = Math.round(con_2 * 100);
		temp = temp / 100;
		battery = ran.nextInt(100);

		if (con_1 > 50) {
			smoke = ran.nextInt(10);
			co2 = ran.nextInt(1000);
		} else {
			temp = temp/2;
			smoke = ran.nextInt(7);
			co2 = ran.nextInt(100);
		}

		//check for emergency temperature and 
		if (temp >= 50 || smoke >= 7) {
			emerg = true;
		}

		try {
			oOut.writeObject(new SensorMessage("UPDATE", id, location, temp, battery, smoke, co2, true, emerg));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//set values to sensor UI

		messageArea.setText("Sensor id : " + id + "\nTemp : " + temp + "\nSmoke : " + smoke + "\nbattery : " + battery
				+ "\nco2 : " + co2+ "\nemergency : " + emerg);
	}

	/**
	 * Connects to the server then enters the processing loop.
	 */
	private void run() throws IOException {

		// Make connection and initialize streams
		String serverAddress = getServerAddress();
		Socket socket = new Socket(serverAddress, 9001);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		oOut = new ObjectOutputStream(socket.getOutputStream());

		while (true) {
			String line = in.readLine();

			if (line.contains("SUBMITID")) {

				oOut.writeObject(new SensorMessage("CONNECT", getId()));
				oOut.flush();

			} else if (line.startsWith("IDACCEPTED")) {
				client.frame.setVisible(true);
				id = line.substring(10);
				String[] sId = id.split("-");
				location = sId[0];

				while (true) {
					try {
						Thread.sleep(5000);
						measure();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else {

			}
		}
	}

	/**
	 * Runs the client as an application with a closeable frame.
	 */
	public static void main(String[] args) throws Exception {
		client = new FireSensorClient();
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.run();

	}

}
