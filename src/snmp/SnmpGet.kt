package snmp

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

/**
 * Created by admin on 26.12.2017.
 */
class SnmpGet (val ipAddress:String, val port:String, val oidValue:String) {
    private val snmpVersion = SnmpConstants.version1

    private val community = "public"
    fun snmpGet():String{

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

        //Sending Request to Agent...
        val response = snmp.get(pdu, comtarget)

        // Process Agent Response
        if (response != null) {
            //Got Response from Agent
            val responsePDU = response.response

            if (responsePDU != null) {
                val errorStatus = responsePDU.errorStatus
                val errorIndex = responsePDU.errorIndex
                val errorStatusText = responsePDU.errorStatusText

                if (errorStatus == PDU.noError) {
                    //println("Snmp Get Response = " + responsePDU.variableBindings)
                    return responsePDU.variableBindings[0].variable.toString()
                } else {
                    println("Error: Request Failed")
                    println("Error Status = " + errorStatus)
                    println("Error Index = " + errorIndex)
                    println("Error Status Text = " + errorStatusText)
                    return "error"
                }
            } else {
                println("Error: Response PDU is null")
                return "error"
            }
        } else {
            println("Error: Agent Timeout... ")
            return "error"
        }
        snmp.close()

    }
}