package monitor;

import java.util.List;

import sensor.SensorMessage;

public interface FireAlarmListener extends java.rmi.Remote {

	public void notifyEmergency(List<SensorMessage> list) throws java.rmi.RemoteException;

	public void notifyCount(int no) throws java.rmi.RemoteException;
	
	public void notifyUpdates(List<SensorMessage> list) throws java.rmi.RemoteException;

}
