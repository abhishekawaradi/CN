# Extract time and congestion-window values from an NS2 cwnd trace.
NF >= 7 && $6 == "cwnd_" {
    printf("%f\t%f\n", $1, $7)
}
