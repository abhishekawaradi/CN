# Experiment 2: Ping and traceroute-style hop probes on a six-node path.
# NS2 Agent/Ping does not implement operating-system TTL traceroute. This
# script probes each successive hop from node 0, then pings the far endpoint.
# Run with: ns ping_traceroute.tcl

set ns [new Simulator]
$ns color 1 Blue
$ns color 2 Red

set traceFile [open ping_trace.tr w]
$ns trace-all $traceFile
set namFile [open ping_trace.nam w]
$ns namtrace-all $namFile

proc finish {} {
    global ns traceFile namFile

    $ns flush-trace
    close $traceFile
    close $namFile

    # In the classic NS2 trace format, packet type is column five.
    set input [open ping_trace.tr r]
    set droppedPings 0
    set droppedPackets 0
    while {[gets $input line] >= 0} {
        if {[string match "d *" $line]} {
            incr droppedPackets
            set fields [regexp -all -inline {\S+} $line]
            if {[llength $fields] > 4 && [lindex $fields 4] eq "ping"} {
                incr droppedPings
            }
        }
    }
    close $input
    puts "Dropped ping packets: $droppedPings"
    puts "Dropped packets of all types: $droppedPackets"

    if {[auto_execok nam] ne ""} {
        exec nam ping_trace.nam &
    } else {
        puts "NAM is not installed; trace saved as ping_trace.nam"
    }
    exit 0
}

# Six nodes connected in a line, creating a multi-hop route.
for {set i 0} {$i < 6} {incr i} {
    set n($i) [$ns node]
}
for {set i 0} {$i < 5} {incr i} {
    set next [expr {$i + 1}]
    $ns duplex-link $n($i) $n($next) 0.1Mb 10ms DropTail
}

# Congest the n(2)-to-n(3) direction with a small queue and heavy CBR traffic.
$ns queue-limit $n(2) $n(3) 2
$ns duplex-link-op $n(2) $n(3) queuePos 0.5

set tcp0 [new Agent/TCP]
$tcp0 set class_ 2
$ns attach-agent $n(2) $tcp0
set sink0 [new Agent/TCPSink]
$ns attach-agent $n(4) $sink0
$ns connect $tcp0 $sink0
set cbr0 [new Application/Traffic/CBR]
$cbr0 set packetSize_ 500
$cbr0 set rate_ 1Mb
$cbr0 attach-agent $tcp0

# Probe node 1, then 2, ... then 5 to show the route one hop at a time.
Agent/Ping instproc recv {from rtt} {
    $self instvar node_
    puts "node [$node_ id] received ping reply from $from; RTT = $rtt ms"
}
for {set i 1} {$i < 6} {incr i} {
    set hopProbe($i) [new Agent/Ping]
    set hopReply($i) [new Agent/Ping]
    $hopProbe($i) set class_ 1
    $ns attach-agent $n(0) $hopProbe($i)
    $ns attach-agent $n($i) $hopReply($i)
    $ns connect $hopProbe($i) $hopReply($i)
    set probeTime [expr {0.55 + 0.15 * $i}]
    $ns at $probeTime "$hopProbe($i) send"
}

# Also send end-to-end pings in both directions.
set pingForward [new Agent/Ping]
set pingReverse [new Agent/Ping]
set replyAtFive [new Agent/Ping]
set replyAtZero [new Agent/Ping]
$pingForward set class_ 1
$pingReverse set class_ 1
$ns attach-agent $n(0) $pingForward
$ns attach-agent $n(5) $replyAtFive
$ns attach-agent $n(5) $pingReverse
$ns attach-agent $n(0) $replyAtZero
$ns connect $pingForward $replyAtFive
$ns connect $pingReverse $replyAtZero

$ns at 0.4 "$cbr0 start"
$ns at 1.45 "$pingForward send"
$ns at 1.6 "$pingReverse send"
$ns at 1.8 "$cbr0 stop"
$ns at 2.0 "finish"
$ns run
