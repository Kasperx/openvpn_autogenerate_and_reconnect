#!/bin/sh

echo "flush iptable rules"
iptables -F
iptables -X
iptables -t nat -F
iptables -t nat -X
iptables --delete-chain
iptables -t mangle -F
iptables -t mangle -X

echo ""
echo "### run reset_iptables_block_all_traffic_except_some_ports_only_eth0.sh"
echo ""
sudo sh /scripte/reset_iptables_block_all_traffic_on_eth0_except_some_ports.sh
echo ""
echo "### run iptables_reset_marryWithNewScript.sh"
#sudo sh /scripte/reset_iptables_marryWithNewScript.sh
sudo sh /scripte/reset_iptables_forward_traffic.sh
echo ""
# Save to apply at reboot:
iptables-save > /etc/iptables/rules.v4
service netfilter-persistent save

# Make the rules apply at startup:
#systemctl enable netfilter-persistent

exit
