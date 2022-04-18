package tp1.server;

import jakarta.inject.Singleton;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * <p>A class to perform service discovery, based on periodic service contact endpoint 
 * announcements over multicast communication.</p>
 * 
 * <p>Servers announce their *name* and contact *uri* at regular intervals. The server actively
 * collects received announcements.</p>
 * 
 * <p>Service announcements have the following format:</p>
 * 
 * <p>&lt;service-name-string&gt;&lt;delimiter-char&gt;&lt;service-uri-string&gt;</p>
 */
@Singleton
public class Discovery {
	private static Logger Log = Logger.getLogger(Discovery.class.getName());

	static {
		// addresses some multicast issues on some TCP/IP stacks
		System.setProperty("java.net.preferIPv4Stack", "true");
		// summarizes the logging format
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s");
	}
	
	
	// The pre-aggreed multicast endpoint assigned to perform discovery.
	static final InetSocketAddress DISCOVERY_ADDR = new InetSocketAddress("227.227.227.227", 2277);
	static final int DISCOVERY_PERIOD = 1000;
	static final int DISCOVERY_TIMEOUT = 5000;

	// Used separate the two fields that make up a service announcement.
	private static final String DELIMITER = "\t";

	// Stores the information about different servers
	Map<String, List<URI>> serversInfo = new ConcurrentHashMap<>();

	Discovery() {
	}
	
	/**
	 * Continuously announces a service given its name and uri
	 * 
	 * @param serviceName the composite service name: <domain:service>
	 * @param serviceURI - the uri of the service
	 */
	public void announce(String serviceName, String serviceURI) {
		Log.info(String.format("Starting Discovery announcements on: %s for: %s -> %s\n", DISCOVERY_ADDR, serviceName, serviceURI));

		var pktBytes = String.format("%s%s%s", serviceName, DELIMITER, serviceURI).getBytes();

		DatagramPacket pkt = new DatagramPacket(pktBytes, pktBytes.length, DISCOVERY_ADDR);
		// start thread to send periodic announcements
		new Thread(() -> {
			try (var ds = new DatagramSocket()) {
				for (;;) {
					try {
						Thread.sleep(DISCOVERY_PERIOD);
						ds.send(pkt);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	/**
	 * Listens for the given composite service name, blocks until a minimum number of replies is collected.
	 * @param serviceName - the composite name of the service
	 * @return the discovery results as an array
	 */
	
	public void listener(String serviceName) throws URISyntaxException {
		Log.info(String.format("Starting discovery on multicast group: %s, port: %d\n", DISCOVERY_ADDR.getAddress(), DISCOVERY_ADDR.getPort()));

		final int MAX_DATAGRAM_SIZE = 65536;
		var pkt = new DatagramPacket(new byte[MAX_DATAGRAM_SIZE], MAX_DATAGRAM_SIZE);

		new Thread(() -> {
		    try (var ms = new MulticastSocket(DISCOVERY_ADDR.getPort())) {
			    joinGroupInAllInterfaces(ms);
				for(;;) {
					try {
						pkt.setLength(MAX_DATAGRAM_SIZE);
						ms.receive(pkt);
					
						var msg = new String(pkt.getData(), 0, pkt.getLength());
						System.out.printf( "FROM %s (%s) : %s\n", pkt.getAddress().getCanonicalHostName(), 
								pkt.getAddress().getHostAddress(), msg);
						var tokens = msg.split(DELIMITER);
						if (tokens.length == 2) {
							//TODO: to complete by recording the received information from the other node.

							if (!serversInfo.containsKey(serviceName)) {
								List<URI> uriList = new ArrayList<>();
								serversInfo.put(serviceName, uriList);
							}

							URI uri = new URI(tokens[1]+"#"+System.currentTimeMillis());

							int oldPos = getIndexURI(serviceName, uri.getHost());
							if (oldPos > -1) {
								String oldStr = serversInfo.get(serviceName).get(oldPos).toString();
								String uriParts[] = oldStr.split("#");

								URI newURI = new URI(uriParts[0]+"#"+uri.getFragment());

								serversInfo.get(serviceName).set(oldPos, newURI);

							}
							else serversInfo.get(serviceName).add(uri);

							//prints which URI are being stored
							for (URI uriOf : knownUrisOf(serviceName))
								System.out.println(uriOf);

						}
					} catch (IOException e) {
						e.printStackTrace();
						try {
							Thread.sleep(DISCOVERY_PERIOD);
						} catch (InterruptedException e1) {
						// do nothing
						}
						Log.finest("Still listening...");


					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				}
  		    } catch (IOException e) {
			    e.printStackTrace();
		    }


		}).start();

	}

	private int getIndexURI(String serviceName, String hostname) {
		for (int i = 0; i < serversInfo.get(serviceName).size(); i++) {
			if (hostname.equals(serversInfo.get(serviceName).get(i).getHost()))
				return i;
		}
		return -1;
	}

	/**
	 * Returns the known servers for a service.
	 * 
	 * @param  serviceName the name of the service being discovered
	 * @return an array of URI with the service instances discovered. 
	 * 
	 */
	public URI[] knownUrisOf(String serviceName) throws URISyntaxException {
		URI[] uris;
		if (serversInfo.containsKey(serviceName)) {
			List<URI> uriList = serversInfo.get(serviceName);
			for(int i = 0; i < uriList.size(); i++) {
				if (System.currentTimeMillis() - Long.parseLong(uriList.get(i).getFragment()) >= DISCOVERY_TIMEOUT)
					uriList.remove(i);
			}
			uris = new URI[uriList.size()];
			serversInfo.get(serviceName).toArray(uris);
		}
		else
			return null;

		return uris;
	}	

	private void joinGroupInAllInterfaces(MulticastSocket ms) throws SocketException {
		Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
		while (ifs.hasMoreElements()) {
			NetworkInterface xface = ifs.nextElement();
			try {
				ms.joinGroup(DISCOVERY_ADDR, xface);
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
	}

	/**
	 * Starts sending service announcements at regular intervals...
	 */
	public void start(String serviceName, String serviceURI) throws URISyntaxException {
		announce(serviceName, serviceURI);
		listener(serviceName);
	}
}
