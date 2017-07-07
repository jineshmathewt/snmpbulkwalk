

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.snmp4j.*;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.security.*;
import org.snmp4j.UserTarget;
import org.snmp4j.smi.*;
import org.snmp4j.mp.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.util.*;

public class TestSNMP {

	private static final String SNMPPORT = "161";
	private static final int snmpVersion = SnmpConstants.version2c;
	private int snmpTimeout = 500;
	private int numRetries = 2;

	public void doSNMPBulkWalk(String ipAddr, String commStr, String bulkOID, String operation) throws IOException {

		Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
		UserTarget targetV3 = null;
		CommunityTarget targetV2 = null;
		UsmUser user = null;
		PDU request = null;
		snmp.listen();
		Address add = new UdpAddress(ipAddr + "/" + SNMPPORT);

		if (snmpVersion == SnmpConstants.version2c || snmpVersion == SnmpConstants.version1) {
			targetV2 = new CommunityTarget();
			targetV2.setCommunity(new OctetString(commStr));
			targetV2.setAddress(add);
			targetV2.setTimeout(snmpTimeout);
			targetV2.setRetries(numRetries);
			targetV2.setVersion(snmpVersion);
			targetV2.setMaxSizeRequestPDU(65535);
		}

		if (snmpVersion == SnmpConstants.version2c) {
			request = new PDU();
			//request.setMaxRepetitions(100);
			//request.setNonRepeaters(0);
		}

		request.setType(PDU.GETBULK);
		OID oID = new OID(bulkOID);
		request.add(new VariableBinding(oID));
		OID rootOID = request.get(0).getOid();
		VariableBinding vb, ar[];
		List<TreeEvent> l = null;
		TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
		if (snmpVersion == SnmpConstants.version2c) {
			targetV2.setCommunity(new OctetString(commStr));
			if (operation.equalsIgnoreCase("bulkwalk")) {
				OID[] rootOIDs = new OID[1];
				rootOIDs[0] = rootOID;
				l = treeUtils.walk(targetV2, rootOIDs);
			} else {
				l = treeUtils.getSubtree(targetV2, rootOID);
			}

		}
		//System.out.println(l);
		System.out.println("size="+l.size());
		for(TreeEvent t : l){
			VariableBinding[] vbs= t.getVariableBindings();
			for (int i = 0; (vbs != null) && i < vbs.length; i++) {
				vb = vbs[i];
				String s = vb.toString();
				System.out.println(s);
			}
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Date d1 = new Date();
		TestSNMP snmpTest = new TestSNMP();
		try {
			snmpTest.doSNMPBulkWalk(args[0], args[1], args[2], args[3]);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		Date d2 = new Date();
		System.out.println("Time Elapsed=" + (d2.getTime() - d1.getTime()));

	}

}
