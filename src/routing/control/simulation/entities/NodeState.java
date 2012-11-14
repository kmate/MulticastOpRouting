package routing.control.simulation.entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class NodeState {

	private int nodeId;
	private int sessionId = 1;

	private HashMap<Integer, SessionState> sessionState;

	public int getNodeId() {
		return nodeId;
	}

	public int getSessionId() {
		return sessionId;
	}

	public Iterable<Integer> getSessionIds() {
		return sessionState.keySet();
	}

	public SessionState getSessionState() {
		if (!sessionState.containsKey(sessionId)) {
			sessionState.put(sessionId, new SessionState());
		}
		return sessionState.get(sessionId);
	}

	public SessionState getSessionStateById(int sessionId) {
		if(!sessionState.containsKey(sessionId)) {
			sessionState.put(sessionId, new SessionState());
		}
		
		return sessionState.get(sessionId);
	}

	public NodeState(int nodeId) {
		this.nodeId = nodeId;
		sessionState = new HashMap<Integer, SessionState>();
	}

	public class SessionState {
		private int batchNumber;
		private int credits;

		// destinationid, packetid, [node]
		// nodes, that ack-ed our packets, grouped by the destinations
		public HashMap<Integer, HashMultimap<Integer, Integer>> ackData;

		// destinationid, packetid
		// packets we recieved, but waiting for credit assignment to happen
		public HashMultimap<Integer, Integer> unassignedPackets;

		// all recieved data packets
		public HashMap<Integer, DataPacket> receivedDataPackets;

		// all recieved data packets
		public int receivedDataPacketsFromBatch;

		// destinationid, packetid, courrent forwarder node id
		// a map of packets with their current forwarder id, who is got the
		// credit to forward the packet. Grouped by destination ids.
		public HashMap<Integer, HashMap<Integer, Integer>> creditMap;

		public Set<Integer> packetsToForward;
		
		public Map<Integer, Packet> sentPackets;

		private Multimap<Integer, Integer> forwarderIds;
		private Set<Integer> reachableDestIds;

		public SessionState() {
			ackData = new HashMap<Integer, HashMultimap<Integer, Integer>>();
			unassignedPackets = HashMultimap.create();
			receivedDataPackets = new HashMap<Integer, DataPacket>();
			receivedDataPacketsFromBatch = 0;
			creditMap = new HashMap<Integer, HashMap<Integer, Integer>>();
			packetsToForward = new HashSet<Integer>();
			sentPackets = new HashMap<Integer, Packet>();
			forwarderIds = HashMultimap.create();
			reachableDestIds = new HashSet<Integer>();
			batchNumber = 1;
		}

		public int getBatchNumber() {
			return batchNumber;
		}

		public void setBatchNumber(int batchNumber) {
			this.batchNumber = batchNumber;
		}

		public int getCredits() {
			return credits;
		}

		public void setCredits(int credits) {
			this.credits = credits;
		}

		public Set<Integer> getForwarderIds() {
			return new HashSet<Integer>(forwarderIds.values());
		}

		public Set<Integer> getReachableDestIds() {
			return reachableDestIds;
		}
	}

	// state changes by a data packet
	public void transformWithDataPacket(DataPacket packet) {
		SessionState s = transformWithPacket(packet);
		sessionId = packet.sessionId;
	}

	// state changes by an acknowledgment packet
	public void transformWithAckPacket(AckPacket packet) {
		SessionState s = transformWithPacket(packet);
		sessionId = packet.sessionId;
	}

	// common state changes by each packet
	private SessionState transformWithPacket(Packet packet) {
		SessionState psd;

		if (!sessionState.containsKey(packet.sessionId)) {
			psd = new SessionState();
			sessionState.put(packet.sessionId, psd);
		} else {
			psd = sessionState.get(packet.sessionId);
		}

		return psd;
	}

	// state changes by an info packet
	public void transformWithInfoPacket(InfoPacket packet) {
		SessionState psd;

		if (!sessionState.containsKey(packet.sessionId)) {
			psd = new SessionState();
			sessionState.put(packet.sessionId, psd);
		} else {
			psd = sessionState.get(packet.sessionId);
		}

		psd.forwarderIds = packet.forwarderIds;
		psd.reachableDestIds = packet.reachableDestIds;
	}
}
