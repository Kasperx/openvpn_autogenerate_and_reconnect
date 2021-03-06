#!/bin/bash

if [ -z "$1" ] || [ "$1" = "-?" ]; then
	if [  -z "$1" ];then echo "No input."; fi;
	echo "Syntax: $0 [check | reconnect | auto | -?] [test]"
	echo "Parameter:"
	echo "'' or '-?'	=	show this help, then exit"
	echo "check		=	check connection to be alive, then exit"
	echo "reconnect	=	reconnect open-vpn-connection, ignore current status"
	echo "auto		=	check connection to be alive, reconnect open-vpn-connection if not alive"
	echo "test		=	only works with reconnect, shows console output while reconnect"
	echo "Bye"
	exit
fi

if [ -z "`whereis fping`" ];then
	sudo apt install -y fping
fi

iface=`ifconfig | grep tun`
iface=`ip tuntap show`
#echo "$iface"
service=`sudo service openvpn status`
service_id=$?
#echo $service_id

# -n = string is not null
# -z = string is null

logfile=/home/pi/openvpn.log
#logfile=openvpn.log
#alias ping='ping -W 2 -I tun0 -c 1'
#alias ping='fping -t500 -I tun0 -c 1'
alias ping='timeout 2.0 ping -t500 -I tun0 -c 1'
var=$1
var2=$2
date=$(date +%d.%m.%Y-%H.%M)

function askPublicIP {
	wget -qO- http://ipecho.net/plain ; echo
}

function restartOpenvpn {

	if [ "$var" = "auto" ] || [ "$var" = "reconnect" ];then
#	if [ "$1" != "check" ];then
#		echo $var
#		echo "Try reconnecting..."
#exit
		export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-armhf/jre/bin/java
#		if [ "$2" = "test" ] ;then
#		echo "Reconnecting... " | tee -a $logfile
		if [ "$var2" = "test" ];then

			echo "" | tee -a $logfile
			echo "####################################################" | tee -a $logfile
			echo "Start: $(date +%d.%m.%Y-%H.%M)" | tee -a $logfile
			echo "ip without open-vpn: " | tee -a $logfile
			#/scripte/ask_public_ip.sh 2>&1 | tee -a $logfile
			askPublicIP | tee -a $logfile
#sleep 2
#exit
			echo "stopping open-vpn"
			sudo systemctl stop openvpn >/dev/null 2>&1
			sleep 5
			sudo killall openvpn >/dev/null 2>&1
			sleep 5
			sudo java -jar /scripte/RegenerateOVPNConfigFile.jar -path /etc/openvpn/configs 2>&1 | tee -a $logfile
			#echo "vpn restart  ... $(date '+%d.%m.%Y-%H.%M.%S')" | tee -a $logfile
			echo "reconnect..."
			sudo systemctl start openvpn
			#echo "vpn restart  ... $(date '+%d.%m.%Y-%H.%M.%S')" | tee -a $logfile
		#   sudo openvpn --config /etc/openvpn/openvpn.conf &
			# time for reconnect
			sleep 20
			echo "ip with open-vpn: " | tee -a $logfile
			#/scripte/ask_public_ip.sh 2>&1 | tee -a $logfile
			askPublicIP | tee -a $logfile
			echo "End" | tee -a $logfile
			/scripte/dyndns_goip_update_ip.sh
			sudo systemctl status openvpn
		else

			echo "" >> $logfile
			echo "####################################################" | tee -a $logfile
			echo "Start: $(date +%d.%m.%Y-%H.%M)" >> $logfile
			echo "ip without open-vpn: " >> $logfile
			#/scripte/ask_public_ip.sh 2>&1 | tee -a $logfile
			askPublicIP >> $logfile 2>&1
			sudo systemctl stop openvpn >/dev/null 2>&1
			sleep 5
			sudo killall openvpn >/dev/null 2>&1
			sleep 5
			sudo java -jar /scripte/RegenerateOVPNConfigFile.jar -path /etc/openvpn/configs >> $logfile 2>&1
			#echo "vpn restart  ... $(date '+%d.%m.%Y-%H.%M.%S')" >> $logfile 2>&1
			echo "reconnect..."
			sudo systemctl start openvpn
			#echo "vpn restart  ... $(date '+%d.%m.%Y-%H.%M.%S')" >> $logfile 2>&1
		#	sudo openvpn --config /etc/openvpn/openvpn.conf >> /dev/null 2>&1 &
			# time for reconnect
			sleep 20
			echo "ip with open-vpn: " >> $logfile
			#/scripte/ask_public_ip.sh 2>&1 >> $logfile
			askPublicIP >> $logfile
			echo "End" >> $logfile
			/scripte/dyndns_goip_update_ip.sh
		fi
	fi
}

if [ "$1" = "check" ] || [ "$1" = "auto" ];then

	# if tun0 and ping are alive, do nothing
#	if [ -n "$iface" ] && [ -n "`ping web.de`" ] && [ -n "`ping gmx.de`" ];then
	if [ -z "$iface" ] || [ "$service_id" != "0" ];then

	    echo "" | tee -a $logfile
	    echo "something wrong with tun interface: $(date +%d.%m.%Y-%H.%M)" | tee -a $logfile
	    echo "Tun info: '$iface'" | tee -a $logfile
	    echo "" | tee -a $logfile
	    echo "---------------------" | tee -a $logfile
          #`ip tuntap show` >> $logfile
          if [ "$service_id" != "0" ];then `sudo service openvpn status` >> $logfile; fi;
	    echo "---------------------" | tee -a $logfile
	    restartOpenvpn

	elif [ -n "$iface" ] && [ -n "`timeout 2.0 ping -I tun0 -c 1 web.de`" ] && [ -n "`timeout 2.0 ping -I tun0 -c 1 gmx.de`" ];then

		echo "Everything ok"
		#sh /scripte/dyndns_goip_update_ip.sh
		exit
	# if tun0 and ping are dead, reconnect
#	elif [ -z "$iface" ] && [ -z "`ping web.de`" ] && [ -z "`ping gmx.de`" ];then
	elif [ -z "$iface" ] && [ -z "`timeout 2.0 ping -I tun0 -c 1 web.de`" ] && [ -z "`timeout 2.0 ping -I tun0 -c 1 gmx.de`" ];then

	    echo "" | tee -a $logfile
	    echo "something wrong with tun interface: $(date +%d.%m.%Y-%H.%M)" | tee -a $logfile
	    echo "Tun info: '$iface'" | tee -a $logfile
            #echo "$iface" | tee -a $logfile
	    echo "" | tee -a $logfile
	    echo "---------------------" | tee -a $logfile
#                `ip tuntap show` >> $logfile
                if [ "$service_id" != "0" ];then `sudo service openvpn status` >> $logfile; fi;
            echo "---------------------" | tee -a $logfile
		restartOpenvpn

	# if tun0 is alive and ping is dead, theres something wrong with the connection
#	elif [ -n "$iface" ] && [ -z "`ping web.de`" ] && [ -z "`ping gmx.de`" ];then
	elif [ -n "$iface" ] && [ -z "`timeout 2.0 ping -I tun0 -c 1 web.de`" ] && [ -z "`timeout 2.0 ping -I tun0 -c 1 gmx.de`" ];then

		echo "" | tee -a $logfile
		echo "interface alive, but vpn connection lost: $(date +%d.%m.%Y-%H.%M)" | tee -a $logfile
		echo "" | tee -a $logfile
		echo "---------------------" | tee -a $logfile
#                `ip tuntap show` >> $logfile
                if [ "$service_id" != "0" ];then `sudo service openvpn status` >> $logfile; fi;
	        echo "---------------------" | tee -a $logfile
		restartOpenvpn

	else

	    echo "" | tee -a $logfile
	    echo "something wrong: $(date +%d.%m.%Y-%H.%M)" | tee -a $logfile
	    echo "Tun info: '$iface'" | tee -a $logfile
	    echo "" | tee -a $logfile
	    echo "---------------------" | tee -a $logfile
#            `ip tuntap show` >> $logfile
		if [ "$service_id" != "0" ];then `sudo service openvpn status` >> $logfile; fi;
            echo "---------------------" | tee -a $logfile
	    restartOpenvpn

	fi

elif [ "$1" = "reconnect" ];then
	
	restartOpenvpn

else

	echo "No valid input."

fi

exit
