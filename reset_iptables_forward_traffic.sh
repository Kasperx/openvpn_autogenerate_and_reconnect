#!/bin/sh

#echo "flush iptable rules"
#iptables -F
#iptables -X
#iptables -t nat -F
#iptables -t nat -X
#iptables --delete-chain
#iptables -t mangle -F
#iptables -t mangle -X

echo "allow everything incoming & outgoing traffic to tun"
#iptables -A INPUT -i tun0 -p all -j ACCEPT
#iptables -A OUTPUT -o tun0 -p all -j ACCEPT
#iptables -A FORWARD -i tun0 -j ACCEPT
#iptables -A FORWARD -o tun0 -j ACCEPT
echo "forward everything from x to tun"
iptables -t nat -A POSTROUTING -o tun0 -j MASQUERADE

iptables -A FORWARD -i eth0 -o tun0 -m state --state RELATED,ESTABLISHED -j ACCEPT
iptables -A FORWARD -i eth0 -o tun0 -m state --state NEW -j ACCEPT
iptables -A FORWARD -i eth0 -o tun0 -j ACCEPT

iptables -A FORWARD -i eth1 -o tun0 -m state --state RELATED,ESTABLISHED -j ACCEPT
iptables -A FORWARD -i eth1 -o tun0 -m state --state NEW -j ACCEPT
iptables -A FORWARD -i eth1 -o tun0 -j ACCEPT

# port forwarding #works
# forward from x to
iptables -A PREROUTING -t nat -i tun0 -d ip -p udp --dport x -j DNAT --to ip:x
iptables -A PREROUTING -t nat -i tun0 -d ip -p tcp --dport x -j DNAT --to ip:x
# forward from x to
iptables -t nat -A PREROUTING -p tcp -i tun0 -m tcp --dport y -j DNAT --to-destination ip:y
iptables -t nat -A PREROUTING -p udp -i tun0 -m udp --dport y -j DNAT --to-destination ip:y
iptables -I FORWARD -p tcp -d ip --dport y -j ACCEPT
iptables -I FORWARD -p udp -d ip --dport y -j ACCEPT
# forward from x to
iptables -t nat -A PREROUTING -p tcp -i tun0 -m tcp --dport f -j DNAT --to-destination ip:f
iptables -t nat -A PREROUTING -p udp -i tun0 -m udp --dport f -j DNAT --to-destination ip:f
# forward from x to
iptables -t nat -A PREROUTING -p tcp -i tun0 -m tcp --dport g -j DNAT --to-destination ip:g
iptables -t nat -A PREROUTING -p udp -i tun0 -m udp --dport g -j DNAT --to-destination ip:g
iptables -I INPUT -i tun0 -p udp --dport g -j ACCEPT
iptables -I INPUT -i tun0 -p tcp --dport g -j ACCEPT

# Save to apply at reboot:
#iptables-save > /etc/iptables/rules.v4
#service netfilter-persistent save

exit
