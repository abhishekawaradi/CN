# Experiment 1: Three-node point-to-point network with a TCP/CBR flow.
# Run with: ns prog1.tcl

set ns [new Simulator]

set traceFile [open prog1.tr w]
$ns trace-all $traceFile
set namFile [open prog1.nam w]
$ns namtrace-all $namFile

# Change these values and rerun the script to compare packet drops.
set linkBandwidth 1Mb
set queueSize 10

proc finish {} {
    global ns traceFile namFile

    $ns flush-trace
    close $traceFile
    close $namFile

    # Count dropped packets from the trace without relying on grep/cut.
    set input [open prog1.tr r]
    set drops 0
    while {[gets $input line] >= 0} {
        if {[string match "d *" $line]} {
            incr drops
        }
    }
    close $input
    puts "The number of packet drops is $drops"

    if {[auto_execok nam] ne ""} {
        exec nam prog1.nam &
    } else {
        puts "NAM is not installed; trace saved as prog1.nam"
    }
    exit 0
}

# Create and label the three nodes.
set n0 [$ns node]
set n1 [$ns node]
set n2 [$ns node]
$n0 label "TCP Source"
$n2 label "Sink"
$ns color 1 Blue

# Duplex links and queue limits. The middle node is the bottleneck candidate.
$ns duplex-link $n0 $n1 $linkBandwidth 10ms DropTail
$ns duplex-link $n1 $n2 $linkBandwidth 10ms DropTail
$ns duplex-link-op $n0 $n1 orient right
$ns duplex-link-op $n1 $n2 orient right
$ns queue-limit $n0 $n1 $queueSize
$ns queue-limit $n1 $n2 $queueSize

# TCP transport connection from node 0 to node 2.
set tcp0 [new Agent/TCP]
$tcp0 set class_ 1
$ns attach-agent $n0 $tcp0
set sink0 [new Agent/TCPSink]
$ns attach-agent $n2 $sink0
$ns connect $tcp0 $sink0

# CBR application traffic carried by TCP, as in the lab manual.
set cbr0 [new Application/Traffic/CBR]
$cbr0 set packetSize_ 100
$cbr0 set rate_ 1Mb
$cbr0 set random_ false
$cbr0 attach-agent $tcp0

$ns at 0.0 "$cbr0 start"
$ns at 5.0 "finish"
$ns run
