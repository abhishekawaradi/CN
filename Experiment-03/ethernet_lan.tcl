# Experiment 3: Ethernet LAN with two TCP sources and congestion-window traces.
# Run with: ns ethernet_lan.tcl

set ns [new Simulator]
set namFile [open ethernet_lan.nam w]
$ns namtrace-all $namFile
set traceFile [open ethernet_lan.tr w]
$ns trace-all $traceFile
$ns color 1 Blue
$ns color 2 Red

set cwndFile1 [open cwnd1.tr w]
set cwndFile2 [open cwnd2.tr w]

proc finish {} {
    global ns namFile traceFile cwndFile1 cwndFile2
    $ns flush-trace
    close $namFile
    close $traceFile
    close $cwndFile1
    close $cwndFile2

    if {[auto_execok nam] ne ""} {
        exec nam ethernet_lan.nam &
    } else {
        puts "NAM is not installed; trace saved as ethernet_lan.nam"
    }
    exit 0
}

# Nodes 0 and 3 connect the two source nodes to the shared Ethernet LAN.
for {set i 0} {$i < 9} {incr i} {
    set n($i) [$ns node]
}
$n(7) shape box
$n(7) color Blue
$n(8) shape hexagon
$n(8) color Red

$ns duplex-link $n(1) $n(0) 2Mb 10ms DropTail
$ns duplex-link $n(2) $n(0) 2Mb 10ms DropTail
$ns duplex-link $n(0) $n(3) 1Mb 20ms DropTail
$ns make-lan "$n(3) $n(4) $n(5) $n(6) $n(7) $n(8)" \
    512Kb 40ms LL Queue/DropTail Mac/802_3
$ns duplex-link-op $n(1) $n(0) orient right-down
$ns duplex-link-op $n(2) $n(0) orient right-up
$ns duplex-link-op $n(0) $n(3) orient right
$ns queue-limit $n(0) $n(3) 20

# Flow 1: TCP Vegas from node 1 to node 7.
set tcp1 [new Agent/TCP/Vegas]
$tcp1 set class_ 1
$tcp1 set packetSize_ 55
$ns attach-agent $n(1) $tcp1
set sink1 [new Agent/TCPSink]
$ns attach-agent $n(7) $sink1
$ns connect $tcp1 $sink1
set ftp1 [new Application/FTP]
$ftp1 attach-agent $tcp1
$tcp1 attach $cwndFile1
$tcp1 trace cwnd_

# Flow 2: TCP Reno from node 2 to node 8.
set tcp2 [new Agent/TCP/Reno]
$tcp2 set class_ 2
$tcp2 set packetSize_ 55
$ns attach-agent $n(2) $tcp2
set sink2 [new Agent/TCPSink]
$ns attach-agent $n(8) $sink2
$ns connect $tcp2 $sink2
set ftp2 [new Application/FTP]
$ftp2 attach-agent $tcp2
$tcp2 attach $cwndFile2
$tcp2 trace cwnd_

$ns at 0.5 "$ftp1 start"
$ns at 1.0 "$ftp2 start"
$ns at 5.0 "$ftp1 stop"
$ns at 5.0 "$ftp2 stop"
$ns at 5.5 "finish"
$ns run
