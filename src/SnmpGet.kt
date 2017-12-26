import org.snmp4j.CommunityTarget
import org.snmp4j.PDU
import org.snmp4j.Snmp
import org.snmp4j.TransportMapping
import org.snmp4j.event.ResponseEvent
import org.snmp4j.mp.SnmpConstants
import org.snmp4j.smi.Integer32
import org.snmp4j.smi.OID
import org.snmp4j.smi.OctetString
import org.snmp4j.smi.UdpAddress
import org.snmp4j.smi.VariableBinding
import org.snmp4j.transport.DefaultUdpTransportMapping

object SnmpGet {
    private val ipAddress = "192.168.1.254"

    private val port = "161"

    // OID of MIB RFC 1213; Scalar Object = .iso.org.dod.internet.mgmt.mib-2.system.sysDescr.0
    private val oidValue = ".1.3.6.1.2.1.99.1.1.1.4.11"  // ends with 0 for scalar object

    private val snmpVersion = SnmpConstants.version1

    private val community = "public"

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        println("SNMP GET Demo")

        // Create TransportMapping and Listen
        val transport = DefaultUdpTransportMapping()
        transport.listen()

        // Create Target Address object
        val comtarget = CommunityTarget()
        comtarget.community = OctetString(community)
        comtarget.version = snmpVersion
        comtarget.address = UdpAddress(ipAddress + "/" + port)
        comtarget.retries = 2
        comtarget.timeout = 1000

        // Create the PDU object
        val pdu = PDU()
        pdu.add(VariableBinding(OID(oidValue)))
        pdu.type = PDU.GET
        pdu.requestID = Integer32(1)

        // Create Snmp object for sending data to Agent
        val snmp = Snmp(transport)

        println("Sending Request to Agent...")
        val response = snmp.get(pdu, comtarget)

        // Process Agent Response
        if (response != null) {
            println("Got Response from Agent")
            val responsePDU = response.response

            if (responsePDU != null) {
                val errorStatus = responsePDU.errorStatus
                val errorIndex = responsePDU.errorIndex
                val errorStatusText = responsePDU.errorStatusText

                if (errorStatus == PDU.noError) {
                    println("Snmp Get Response = " + responsePDU.variableBindings)
                    println("Snmp Get Response = " + responsePDU.variableBindings[0].variable)
                } else {
                    println("Error: Request Failed")
                    println("Error Status = " + errorStatus)
                    println("Error Index = " + errorIndex)
                    println("Error Status Text = " + errorStatusText)
                }
            } else {
                println("Error: Response PDU is null")
            }
        } else {
            println("Error: Agent Timeout... ")
        }
        snmp.close()
    }
}