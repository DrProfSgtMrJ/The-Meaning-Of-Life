package de.exitgames.demo.loadbalancing;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.graphics.Color;
import android.util.Log;
import de.exitgames.api.loadbalancing.ClientState;
import de.exitgames.api.loadbalancing.EventCode;
import de.exitgames.api.loadbalancing.LoadBalancingClient;
import de.exitgames.api.loadbalancing.OperationCode;
import de.exitgames.api.loadbalancing.ParameterCode;
import de.exitgames.client.photon.EventData;
import de.exitgames.client.photon.OperationResponse;
import de.exitgames.client.photon.StatusCode;
import de.exitgames.client.photon.enums.DebugLevel;

public class DemoClient extends LoadBalancingClient implements Runnable {

	Random	m_random = new Random();
	int		m_eventCount = 0;

    String appId = "8a6780d9-92bc-48f4-84f3-8bbdcd301300";

	public DemoClient() {
		super();
        setAutoJoinLobby(true);
	}
	
	@Override
	public void run() {

		this.connectToMaster(this.appId, "1.0", "Benjovi");
		this.setPlayerName("Player_" + m_random.nextInt(1000));  // the name is set in connectToMaster, too. this is for demo usage here.
		this.getPlayer().m_customProperties.put("class", "tank" + m_random.nextInt(99));
		
		while (true)
		{
			this.loadBalancingPeer.service();
            // TODO remove comments if app fails
            //ApplicationManager.updateTrafficStats();


			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}

    /********************
     * BEGIN: my methods
     ********************/

    public void sendPoints(ColoredPoint coloredPoint) {
        HashMap<Object, Object> eventContent = new HashMap<Object, Object>();
        eventContent.put((byte) 1, coloredPoint.getX());
        eventContent.put((byte) 10, coloredPoint.getY());
        eventContent.put((byte) 11, coloredPoint.getR());
        eventContent.put((byte) 100, coloredPoint.getG());
        eventContent.put((byte) 101, coloredPoint.getB());
        this.loadBalancingPeer.opRaiseEvent((byte) 10, eventContent, false, (byte) 0);
    }

    /*******************
     * END: my methods
     *******************/

	public void setEventCount(int count)
	{
		m_eventCount = count;
	}

	public int getEventCount()
	{
		return m_eventCount;
	}

    /**
     * Called by our demo form to join a particular room.
     * Note: OpJoinRoom *could* fail and return false but we only use it in
     * places where it safely works, so we ignore the return value here.
     * @param name Name of the room
     * @return
     */
    public boolean joinSelectedRoom(String name)
    {
        // you don't have to wrap OpJoinRoom like we do here! We just wanted all OP calls in this class...
        if (this.opJoinRoom(name, this.getPlayer().m_customProperties))
        {
        	ProgressBar.createDialog("Please wait...").show();
        	return true;
        }
        
        return false;
    }

    /**
     * Called by our demo form to create a new room (and set a few properties for it).
     * @param name Name of the room
     */
    public void createNewRoom(String name)
    {
        // make up some custom properties (key is a string for those)
        HashMap<Object, Object> customGameProperties = new HashMap<Object, Object>();
        customGameProperties.put("map", "blue");
        customGameProperties.put("units", 2);

        ProgressBar.createDialog("Please wait...").show();
        // tells the master to create the room and pass on our locally set properties of "this" player
        this.opCreateRoom(name, true, true, (byte)2, customGameProperties, new String[] { "map" });
    }

    /**
     * Sends event 1 for testing purposes. Your game would send more useful events.
     */
    public void sendSomeEvent()
    {
        // to send an event, "raise" it. apply any code (here 1) and set any content (or even null)
        HashMap<Object, Object> eventContent = new HashMap<Object, Object>();
        eventContent.put((byte)10, "my data");                 // using bytes as event keys is most efficient

        this.loadBalancingPeer.opRaiseEvent((byte)1, eventContent, false, (byte)0);       // this is received by OnEvent()
    }

    public void setRoomProperties()
    {
        HashMap<Object, Object> customRoomProperties = new HashMap<Object, Object>();
        if (m_random.nextInt(2) > 0)
        {
            customRoomProperties.put("map", "map" + m_random.nextInt(10));
        }
        else
        {
            customRoomProperties.put("units", m_random.nextInt(10));
        }

        this.loadBalancingPeer.opSetCustomPropertiesOfRoom(customRoomProperties);
        
        ApplicationManager.onPropertiesChanged();
    }

    public void setCustomPlayerProps()
    {
        HashMap<Object, Object> customPlayerProps = new HashMap<Object, Object>();
        if (m_random.nextInt(2) > 0)
        {
            customPlayerProps.put("class", "tank" + m_random.nextInt(10));
        }
        else
        {
            customPlayerProps.put("lvl", m_random.nextInt(10));
        }

        this.loadBalancingPeer.opSetCustomPropertiesOfActor(this.getPlayer().getID(), customPlayerProps);
        
        ApplicationManager.onPropertiesChanged();
    }

	/**
	 * Debug output of low level api (and this client).
	 */
	@Override
	public void debugReturn(DebugLevel level, String message)
	{
		switch(level)
		{
			case OFF:
				Log.println(Log.ASSERT, "CLIENT", message);
				break;
			case ERROR:
				Log.e("CLIENT", message);
				break;
			case WARNING:
				Log.w("CLIENT", message) ;
				break;
			case INFO:
				Log.i("CLIENT", message);
				break;
			case ALL:
				Log.d("CLIENT", message);
				break;
			default:
				Log.e("CLIENT", message);
				break;
		}
	}

    /**
     * Uses the connection's statusCodes to advance the internal state and call ops as needed.
     * In this client, we also update the form, cause new data might be available to display.
     * @param statusCode
     */
    @Override
    public void onStatusChanged(StatusCode statusCode)
    {
        super.onStatusChanged(statusCode);
        
        ApplicationManager.onClientUpdateCallback();
        
        switch (statusCode)
        {
            case Connect:
                if (getState() == ClientState.ConnectedToMaster)
                    ApplicationManager.onJoinedToMaster();
                break;
            case Disconnect:
                if (getState() == ClientState.Disconnecting)
                    ApplicationManager.onDisconnected();
                break;
            case TimeoutDisconnect:
                ApplicationManager.getConsole().writeLine("Connection timed out");
                break;
            case DisconnectByServer:
                ApplicationManager.getConsole().writeLine("Disconnected by server");
                break;
            case DisconnectByServerLogic:
                ApplicationManager.getConsole().writeLine("Disconnected by server logic");
                break;
            default:
                break;
        }
    }

    /**
     * Uses the photonEvent's provided by the server to advance the internal state and call ops as needed.
     * In this demo client, we check for a particular event (1) and count these. After that, we update the view / gui
     * @param eventData
     */
    @Override
    public void onEvent(EventData eventData)
    {
        super.onEvent(eventData);

        switch (eventData.Code) {
            case (byte)10:
                HashMap< Object, Object > map = (HashMap<Object, Object>) eventData.get(ParameterCode.Data);

                HashMap<Byte,Integer> smap = new HashMap<Byte,Integer>();
                for( Object key : map.keySet() ) {
                    smap.put((byte) key, (int) map.get(key));
                }
                ApplicationManager.onPointsReceived(new ColoredPoint(smap.get((byte) 1), smap.get((byte) 10), smap.get((byte) 11), smap.get((byte) 100), smap.get((byte) 101)));
                break;
            case (byte)1:
                this.m_eventCount++;
                ApplicationManager.onEventReceived();
                break;
            case EventCode.GameList:
            case EventCode.GameListUpdate:
                ApplicationManager.onGameListUpdated();
                break;
            case EventCode.PropertiesChanged:
                ApplicationManager.onPropertiesChanged();
                break;
            case EventCode.Join:
                ProgressBar.hide();
                ApplicationManager.onPlayerJoined();
                break;
            case EventCode.Leave:
                ApplicationManager.onAnotherPlayerLeave();
                break;
        }

        // update the form / gui
        ApplicationManager.onClientUpdateCallback();
    }

    /**
     * Uses the operationResponse's provided by the server to advance the internal state and call ops as needed.
     * In this client, we also update the form, cause new data might be available to display.
     * @param operationResponse
     */
    @Override
    public void onOperationResponse(OperationResponse operationResponse)
    {
        super.onOperationResponse(operationResponse);
        switch (operationResponse.OperationCode)
        {
            case OperationCode.JoinLobby:
                ProgressBar.hide();
                ApplicationManager.onJoinedToLobby();
                this.loadBalancingPeer.setTrafficStatsEnabled(true);
                break;
        }
        ApplicationManager.onClientUpdateCallback();
    }

}
