/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.omegazirkel.risingworld.tools;

// import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.CloseReason.CloseCodes;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.OnError;
import jakarta.websocket.Session;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;

/**
 *
 */
@ClientEndpoint
public class WSClientEndpoint {

	private static final Logger log = new Logger("[OZ.Tools]");

	public Session session = null;
	public boolean isConnected = false;
	protected URI endpointURI = null;
	protected MessageHandler messageHandler;
	// private int disconnectCount = 0;
	protected ClientManager client;

	/**
	 *
	 * @param endpointURI
	 */
	public WSClientEndpoint(URI endpointURI) {
		this.endpointURI = endpointURI;
		this.client = ClientManager.createClient();
		ClientManager.ReconnectHandler reconnectHandler = new ClientManager.ReconnectHandler() {
			private int counter = 0;

			@Override
			public boolean onDisconnect(CloseReason closeReason) {
				final int i = ++counter;
				log.out("WebSocket got disconnected: " + closeReason.toString(), 911);
				if (closeReason.getCloseCode() == CloseCodes.CLOSED_ABNORMALLY) {
					log.out("WebSocket reconnecting... " + i, 0);
					return true;
				} else {
					log.out("WebSocket not reconnecting.", 0);
					return false;
				}
			}

			@Override
			public boolean onConnectFailure(Exception exception) {
				final int i = ++counter;
				log.out("WebSocket failed to connect: " + exception.getMessage(), 911);
				// if (i <= 30) {
				log.out("WebSocket reconnecting... " + i, 0);
				return true;
				// } else {
				// log.out("WebSocket not reconnecting.", 0);
				// return false;
				// }
			}
		};
		this.client.getProperties().put(ClientProperties.RECONNECT_HANDLER, reconnectHandler);
		this.connect();
	}

	/**
	 *
	 */
	protected void connect() {
		try {
			log.out("WebSocket connecting to " + this.endpointURI, 0);
			this.session = this.client.asyncConnectToServer(this, this.endpointURI).get();
			// this.session = this.client.connectToServer(this, this.endpointURI);
		} catch (DeploymentException | InterruptedException | ExecutionException e) {
			log.out(e.getMessage(), 911);
		}
	}

	/**
	 *
	 * @param session
	 * @param t
	 */
	@OnError
	public void onError(Session session, Throwable t) {
		log.out(t.toString(), 911);

	}

	/**
	 *
	 * @param session
	 */
	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		this.isConnected = true;
		log.out("WebSocket connected!", 0);
	}

	/**
	 *
	 * @param session
	 * @param reason
	 */
	@OnClose
	public void onClose(Session session, CloseReason reason) {
		this.session = null;
		this.isConnected = false;
		log.out("WebSocket closed: " + reason.toString(), 911);

	}

	/**
	 *
	 * @param message
	 * @param session
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		if (this.messageHandler != null) {
			this.messageHandler.handleMessage(message);
		} else {
			log.out("No messageHandler for message: " + message, 0);
		}
	}

	/**
	 * 
	 * @param Buffer
	 */
	@OnMessage
	public void onBinaryMessage(byte[] buffer) {
		if (this.messageHandler != null) {
			this.messageHandler.handleBinaryMessage(buffer);
		} else {
			log.out("No messageHandler for buffer: " + buffer.length, 0);
		}
	}

	/**
	 *
	 * @param msgHandler
	 */
	public void setMessageHandler(MessageHandler msgHandler) {
		this.messageHandler = msgHandler;
	}

	/**
	 *
	 * @param message
	 */
	public void sendMessage(String message) {
		this.session.getAsyncRemote().sendText(message);
	}

	/**
	 * 
	 * @param buffer
	 */
	public void sendBinaryMessage(ByteBuffer buffer) {
		try {
			// if we dont clone the array and create a new buffer, the array will be cleared
			// before the data get sent to the WS Server.
			this.session.getAsyncRemote().sendBinary(ByteBuffer.wrap(buffer.array().clone())).get();
		} catch (InterruptedException | ExecutionException e) {
			log.out("Error on sendBinaryMessage: " + e.getMessage(), 911);
		}
	}

	/**
	 *
	 */
	public static interface MessageHandler {

		public void handleMessage(String message);

		public void handleBinaryMessage(byte[] buffer);
	}
}
