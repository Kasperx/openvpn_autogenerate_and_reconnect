#!/bin/bash
IPT="/sbin/iptables"

# Server IP
#SERVER_IP="$(ip addr show eth0 | grep 'inet ' | cut -f2 | awk '{ print $2}')"
#echo $SERVER_IP

# Your DNS servers you use: cat /etc/resolv.conf
DNS_SERVER="194.150.168.168 84.200.69.80 84.200.70.40"
iface="eth0"
tun="tun0"
wlan="eth1"

#echo "flush iptable rules"
#$IPT -F
#$IPT -X
#$IPT -t nat -F
#$IPT -t nat -X
#$IPT --delete-chain
#$IPT -t mangle -F
#$IPT -t mangle -X

echo "do not allow all and everything on $iface"
$IPT -A INPUT   -i $iface -p all -j ACCEPT
$IPT -A FORWARD -i $iface -p all -j ACCEPT
$IPT -A FORWARD -o $iface -p all -j ACCEPT
$IPT -A OUTPUT  -o $iface -p all -j DROP
echo "allow all and everything on localhost"
$IPT -A INPUT -i lo -j ACCEPT
$IPT -A OUTPUT -o lo -j ACCEPT
echo "allow all and everything outgoing on $tun"
#$IPT -A INPUT -i $tun -j ACCEPT
#$IPT -A FORWARD -i $tun -j ACCEPT
#$IPT -A FORWARD -o $tun -j ACCEPT
#$IPT -A OUTPUT -o $tun -j ACCEPT
iptables -A INPUT -i $tun -p all -j ACCEPT
iptables -A FORWARD -i $tun -p all -j ACCEPT
iptables -A FORWARD -o $tun -p all -j ACCEPT
iptables -A OUTPUT -o $tun -p all -j ACCEPT
echo "allow all and everything on $wlan"
$IPT -A INPUT -i $wlan -p all -j ACCEPT
$IPT -A FORWARD -i $wlan -p all -j ACCEPT
$IPT -A FORWARD -o $wlan -p all -j ACCEPT
$IPT -A OUTPUT -o $wlan -p all -j ACCEPT

## This should be one of the first rules.
## so dns lookups are already allowed for your other rules
for ip in $DNS_SERVER
do
	echo "Allowing DNS lookups (tcp, udp port 53) to server '$ip'"
	$IPT -I OUTPUT -p udp -d $ip --dport 53 -o $iface -m state --state NEW,ESTABLISHED -j ACCEPT
	$IPT -I INPUT  -p udp -s $ip --sport 53 -i $iface -m state --state ESTABLISHED     -j ACCEPT
	$IPT -I OUTPUT -p tcp -d $ip --dport 53 -o $iface -m state --state NEW,ESTABLISHED -j ACCEPT
	$IPT -I INPUT  -p tcp -s $ip --sport 53 -i $iface -m state --state ESTABLISHED     -j ACCEPT
done

#######################################################################################################
## Global iptable rules. Not IP specific

echo "Allowing new and established incoming connections to port 22, 30, 443, 123, 1194, 42220, 53, 67 for $iface"
# tcp, normal
$IPT -I INPUT  -p tcp -i $iface -m multiport --dports x,y,d,f -m state --state NEW,ESTABLISHED -j ACCEPT
$IPT -I OUTPUT -p tcp -o $iface -m multiport --sports x,y,d,f -m state --state NEW,ESTABLISHED -j ACCEPT
# reverse
$IPT -I INPUT  -p tcp -i $iface -m multiport --sports x,y,d,f -m state --state NEW,ESTABLISHED -j ACCEPT
$IPT -I OUTPUT -p tcp -o $iface -m multiport --dports x,y,d,f -m state --state NEW,ESTABLISHED -j ACCEPT
# udp, normal
$IPT -I INPUT  -p udp -i $iface -m multiport --dports x,y,d,f -m state --state NEW,ESTABLISHED -j ACCEPT
$IPT -I OUTPUT -p udp -o $iface -m multiport --sports x,y,d,f -m state --state NEW,ESTABLISHED -j ACCEPT
# reverse
$IPT -I INPUT  -p udp -i $iface -m multiport --sports x,y,d,f -m state --state NEW,ESTABLISHED -j ACCEPT
$IPT -I OUTPUT -p udp -o $iface -m multiport --dports x,y,d,f -m state --state NEW,ESTABLISHED -j ACCEPT

echo "Allow outgoing icmp connections (pings,...)"
$IPT -I OUTPUT -p icmp -m state -o $iface --state NEW,ESTABLISHED -j ACCEPT
$IPT -I INPUT  -p icmp -m state -i $iface --state NEW,ESTABLISHED -j ACCEPT

# Log before dropping
$IPT -A INPUT  -j LOG  -m limit --limit 12/min --log-level 4 --log-prefix 'IP INPUT drop: '
$IPT -A INPUT  -j DROP

$IPT -A OUTPUT -j LOG  -m limit --limit 12/min --log-level 4 --log-prefix 'IP OUTPUT drop: '
$IPT -A OUTPUT -j DROP

exit 0
