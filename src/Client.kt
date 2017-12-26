
fun main(args: Array<String>) {
    println(snmp.SnmpGet("192.168.1.254", "161", ".1.3.6.1.2.1.99.1.1.1.4.11" ).snmpGet())
}