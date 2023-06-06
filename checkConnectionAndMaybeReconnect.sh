#!/bin/bash

if [ -z "$1" ] || [ "$1" = "-?" ]; then
	if [  -z "$1" ];then echo "No input."; fi;
	echo "Syntax: $0 [check | reconnect | auto | -?] [test]"
	echo "Parameter:"
	echo -e "'' or '-?'\t= show this help, then exit"
	echo -e "check	\t= check connection to be alive, then exit"
	echo -e "recreate \t= recreate open-vpn-config, then <reconnect>"
	echo -e "reconnect \t= reconnect open-vpn-connection, ignore current status"
	echo -e "auto	\t= check connection to be alive, reconnect open-vpn-connection if not alive"
	echo -e "test	\t= shows console output"
	echo "Bye"
	exit
fi

if [ -z "`whereis fping`" ];then
	sudo apt install -y fping
fi

iface=`ifconfig | grep tun`
#iface=`ip tuntap show`
#echo "$iface"
service=`service openvpn status`
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

function recreate {

	export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-armhf/jre/bin/java
	if [ "$var" = "-v" ]; then
		sudo java -jar /scripte/RegenerateOVPNConfigFile.jar -path /etc/openvpn/configs 2>&1 | tee -a $logfile
	else
		sudo java -jar /scripte/RegenerateOVPNConfigFile.jar -path /etc/openvpn/configs >> $logfile 2>&1
	fi

}

function reconnect {

	if [ "$1" = "-v" ]; then
	echo "Start: $(date +%d.%m.%Y-%H.%M)" | tee -a $logfile
	echo "ip without open-vpn: " | tee -a $logfile
	/home/pi/bin/ask_public_ip.sh | tee -a $logfile
#	echo "tun info: '$iface'" | tee -a $logfile
#	echo "" | tee -a $logfile
#	echo "" | tee -a $logfile
	sudo systemctl stop openvpn >/dev/null 2>&1
	sleep 5
	sudo killall openvpn >/dev/null 2>&1
	sleep 10
#	#test
        echo "test to logfile $logfile"
	echo "File has ". grep -Rl "curl" ./ -c ."count of lines"
	echo "vpn restart  ... $(date '+%d.%m.%Y-%H.%M.%S')" | tee -a $logfile
	echo "reconnect..." | tee -a $logfile
	sudo systemctl start openvpn
##	sudo openvpn --config /etc/openvpn/openvpn.conf >> /dev/null 2>&1 &
#	# time for reconnect
	sleep 20
### test
	echo "ip with open-vpn: " | tee -a $logfile
	/home/pi/bin/ask_public_ip.sh | tee -a $logfile
	echo "" | tee -a $logfile
	echo "End" | tee -a $logfile
	/home/pi/bin/dyndns_goip_update_ip.sh

	else

	echo "Start: $(date +%d.%m.%Y-%H.%M)" >> $logfile 2>&1
	echo "ip without open-vpn: " >> $logfile 2>&1
#	echo "tun info: '$iface'" | tee -a $logfile
#	echo "" | tee -a $logfile
#	echo "" | tee -a $logfile
	sudo systemctl stop openvpn >/dev/null 2>&1
	sleep 5
	sudo killall openvpn >/dev/null 2>&1
	sleep 10
	echo "vpn restart  ... $(date '+%d.%m.%Y-%H.%M.%S')" >> $logfile 2>&1
	echo "reconnect..." | tee -a
	sudo systemctl start openvpn
	#echo "vpn restart  ... $(date '+%d.%m.%Y-%H.%M.%S')" >> $logfile 2>&1
#	sudo openvpn --config /etc/openvpn/openvpn.conf >> /dev/null 2>&1 &
	# time for reconnect
	sleep 20
#	echo "" >> $logfile
	echo "ip with open-vpn: " >> $logfile 2>&1
	/home/pi/bin/ask_public_ip.sh >> $logfile 2>&1
	echo "" >> $logfile 2>&1
	echo "End" >> $logfile 2>&1
	/home/pi/bin/dyndns_goip_update_ip.sh

	fi

}

function restartOpenvpn {
#echo $var
#	if [ "$var" = "auto" ] || [ "$var" = "reconnect" ] || [ "$var" = "recreate" ];then

# dont know y, but the main script does not work
#sudo systemctl stop openvpn >/dev/null 2>&1
#sudo killall openvpn

		export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-armhf/jre/bin/java

#		if [ "$2" = "test" ] ;then
		echo "Reconnecting... " | tee -a $logfile
		if [ "$var2" = "test" ];then

			echo "Start: $(date +%d.%m.%Y-%H.%M)" | tee -a $logfile
			echo "ip without open-vpn: " | tee -a $logfile
			/scripte/ask_public_ip.sh 2>&1 | tee -a $logfile
#sleep 2
#exit
			echo "stopping open-vpn"
			sudo systemctl stop openvpn >/dev/null 2>&1
			sleep 5
			sudo killall openvpn >/dev/null 2>&1
			sleep 5
#			sudo java -jar /scripte/RegenerateOVPNConfigFile.jar -path /etc/openvpn/configs 2>&1 | tee -a $logfile
			#echo "vpn restart  ... $(date '+%d.%m.%Y-%H.%M.%S')" | tee -a $logfile
			echo "reconnect..."
			sudo systemctl start openvpn
			#echo "vpn restart  ... $(date '+%d.%m.%Y-%H.%M.%S')" | tee -a $logfile
		#   sudo openvpn --config /etc/openvpn/openvpn.conf &
			# time for reconnect
			sleep 20
			echo "ip with open-vpn: " | tee -a $logfile
			/scripte/ask_public_ip.sh 2>&1 | tee -a $logfile
			echo "End" | tee -a $logfile
			/scripte/dyndns_goip_update_ip.sh
			systemctl status openvpn
#		else
		fi

			echo "Start: $(date +%d.%m.%Y-%H.%M)" | tee -a $logfile
			echo "ip without open-vpn: " | tee -a $logfile
#			echo "tun info: '$iface'" | tee -a $logfile
#			echo "" | tee -a $logfile
#			echo "" | tee -a $logfile
			sudo systemctl stop openvpn >/dev/null 2>&1
			sleep 5
			sudo killall openvpn >/dev/null 2>&1
			sleep 10
			#test
                        echo "test to logfile $logfile"
                        #test			/scripte/ask_public_ip.sh | tee -a $logfile
#			if [ "$var" = "recreate" ];then
#				sudo java -jar /scripte/RegenerateOVPNConfigFile.jar -path /etc/openvpn/configs | tee -a $logfile
#			fi
			echo "vpn restart  ... $(date '+%d.%m.%Y-%H.%M.%S')" | tee -a $logfile
			echo "reconnect..." | tee -a
			sudo systemctl start openvpn
			#echo "vpn restart  ... $(date '+%d.%m.%Y-%H.%M.%S')" >> $logfile 2>&1
		#	sudo openvpn --config /etc/openvpn/openvpn.conf >> /dev/null 2>&1 &
			# time for reconnect
			sleep 20
#			echo "" >> $logfile
			echo "ip with open-vpn: " | tee -a $logfile
			/home/pi/bin/ask_public_ip.sh | tee -a $logfile
			echo "" | tee -a $logfile
			echo "End" | tee -a $logfile
			/home/pi/bin/dyndns_goip_update_ip.sh
#		fi
#	fi
}

if [ "$1" = "check" ] || [ "$1" = "auto" ];then

	# if tun0 and ping are alive, do nothing
#	if [ -n "$iface" ] && [ -n "`ping web.de`" ] && [ -n "`ping gmx.de`" ];then
	if [ -z "$iface" ];then
#	if ([ ! $service_id -eq 0 ]);then

#		echo "" | tee -a $logfile
		echo "####################################################" | tee -a $logfile
		echo "service status = $service_id" | tee -a $logfile
		echo "something wrong with tun interface: $(date +%d.%m.%Y-%H.%M)" | tee -a $logfile
	    	echo "Tun info: '$iface'" | tee -a $logfile
#	    	echo "" | tee -a $logfile
#	    	echo "---------------------" | tee -a $logfile
            	#`ip tuntap show` >> $logfile
#            	if [ "$service_id" != "0" ];then `sudo service openvpn status` >> $logfile; fi;
#	    	echo "---------------------" | tee -a $logfile
		if [ "$var" = "reconnect" ] || [ "$var" = "auto" ];then
			restartOpenvpn
		fi
#	    	restartOpenvpn

	elif [ -n "`timeout 2.0 ping -I tun0 -c 1 web.de`" ] && [ -n "`timeout 2.0 ping -I tun0 -c 1 gmx.de`" ];then

		echo "Everything ok"
		#sh /scripte/dyndns_goip_update_ip.sh
#		exit
		# if tun0 and ping are dead, reconnect
#		elif [ -z "$iface" ] && [ -z "`ping web.de`" ] && [ -z "`ping gmx.de`" ];then
#		elif [ -z "$iface" ] && [ -z "`timeout 2.0 ping -I tun0 -c 1 web.de`" ] && [ -z "`timeout 2.0 ping -I tun0 -c 1 gmx.de`" ];then

#	    	echo "" | tee -a $logfile
#	    	echo "something wrong with tun interface: $(date +%d.%m.%Y-%H.%M)" | tee -a $logfile
#	    	echo "Tun info: '$iface'" | tee -a $logfile
#	    	echo "" | tee -a $logfile
#	    	echo "---------------------" | tee -a $logfile
#                `ip tuntap show` >> $logfile
#                if [ "$service_id" != "0" ];then `sudo service openvpn status` >> $logfile; fi;
#            	echo "---------------------" | tee -a $logfile
#		restartOpenvpn
#            	if [ "$service_id" != "0" ];then
#			restartOpenvpn reconnect
#		fi

	# if tun0 is alive and ping is dead, theres something wrong with the connection
#	elif [ -n "$iface" ] && [ -z "`ping web.de`" ] && [ -z "`ping gmx.de`" ];then
#	elif [ -n "$iface" ] && [ -z "`timeout 2.0 ping -I tun0 -c 1 web.de`" ] && [ -z "`timeout 2.0 ping -I tun0 -c 1 gmx.de`" ];then
	elif [ -z "`timeout 2.0 ping -I tun0 -c 1 web.de`" ] && [ -z "`timeout 2.0 ping -I tun0 -c 1 gmx.de`" ];then

#		echo "" | tee -a $logfile
		echo "####################################################" | tee -a $logfile
		echo "service status = $service_id" | tee -a $logfile
		echo "interface alive, but vpn connection lost: $(date +%d.%m.%Y-%H.%M)" | tee -a $logfile
#		echo "" | tee -a $logfile
#		echo "---------------------" | tee -a $logfile
#                `ip tuntap show` >> $logfile
#                if [ "$service_id" != "0" ];then `sudo service openvpn status` >> $logfile; fi;
#	        echo "---------------------" | tee -a $logfile
#		restartOpenvpn
		if [ "$var" = "reconnect" ] || [ "$var" = "auto" ];then
                        restartOpenvpn
                fi

	else

#		echo "" | tee -a $logfile
		echo "####################################################" | tee -a $logfile
		echo "service status = $service_id" | tee -a $logfile
		echo "something wrong: $(date +%d.%m.%Y-%H.%M)" | tee -a $logfile
		echo "Tun info: '$iface'" | tee -a $logfile
#	        echo "" | tee -a $logfile
#	        echo "---------------------" | tee -a $logfile
#               `ip tuntap show` >> $logfile
#		if [ "$service_id" != "0" ];then `sudo service openvpn status` >> $logfile; fi;
#           	echo "---------------------" | tee -a $logfile
#	     	restartOpenvpn
		if [ "$var" = "reconnect" ] || [ "$var" = "auto" ];then
                        restartOpenvpn
                fi

	fi

#elif [ "$var" = "reconnect" ] || [ "$var" = "recreate" ];then
elif [ "$var" = "reconnect" ]; then

	echo "####################################################" | tee -a $logfile
#	restartOpenvpn reconnect
	if [ "$var2" = "test" ];then
		reconnect -v
	else
		reconnect
	fi

elif [ "$var" = "recreate" ];then

	echo "####################################################" | tee -a $logfile
#	restartOpenvpn recreate
	if [ "$var2" = "test" ];then
		recreate -v
	else
		recreate
	fi

else

	echo "No valid input."

fi

exit
