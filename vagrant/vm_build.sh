#!/bin/bash

pwd=$(pwd)
vm_dir="elk_vm"


function install_vm {
	echo "Installing virtual machine dependencies..."
	sudo apt install -y virtualbox
	sudo apt install -y virtualbox-dkms

	sudo dpkg-reconfigure virtualbox-dkms
	sudo dpkg-reconfigure virtualbox

	sudo apt install --reinstall linux-headers-$(uname -r) virtualbox-dkms dkms

	sudo apt install -y vagrant
	vagrant plugin install vagrant-scp
	echo "Virtual machine dependencies successfully installed"
}


function deploy_vm {
	echo "Creating virtual machine..."
	rm -rf ~/$vm_dir
	mkdir ~/$vm_dir
	cd ~/$vm_dir
	cp $pwd/config/Vagrantfile ~/$vm_dir #копируем конфиг для виртуальной машины
	echo "Virtual machine successfully created"
}

function install_elk {
	echo "Installing Elasticsearch..."
	vagrant ssh -c 'sudo apt-get -y install nginx'
	vagrant ssh -c 'curl -fsSL https://artifacts.elastic.co/GPG-KEY-elasticsearch | sudo apt-key add -'
	vagrant ssh -c 'sudo apt-get -y install apt-transport-https'
	vagrant ssh -c 'echo "deb https://artifacts.elastic.co/packages/7.x/apt stable main" | sudo tee -a /etc/apt/sources.list.d/elastic-7.x.list'
	vagrant ssh -c 'sudo apt-get -y update'
	vagrant ssh -c 'sudo apt-get -y install elasticsearch'
	vagrant ssh -c 'sudo mv /home/vagrant/elasticsearch.yml /etc/elasticsearch/'
	vagrant ssh -c 'sudo systemctl start elasticsearch'
	vagrant ssh -c 'sudo systemctl enable elasticsearch'
	echo "Installing Kibana..."
	vagrant ssh -c 'sudo apt-get -y install kibana'
	vagrant ssh -c 'sudo mv /home/vagrant/kibana.yml /etc/kibana/'
	vagrant ssh -c 'sudo systemctl start kibana'
	vagrant ssh -c 'sudo systemctl enable kibana'
	vagrant ssh -c 'sudo ufw allow 5601/tcp'

	sleep 120 # ждем пока сервисы стартанут
	echo "Elastic with Kibana was successfully installed"
}

function install_utils {
  echo "Installing utils..."
  vagrant ssh -c 'sudo apt update'
	vagrant ssh -c 'sudo apt -y install wget'
	vagrant ssh -c 'sudo apt-get -y install vim'
	vagrant ssh -c 'sudo apt -y install git'
	vagrant ssh -c 'sudo apt -y install maven'
}

function run_application {
  echo "Run application..."
	vagrant ssh -c 'git clone https://github.com/vaomaohaoone/check-inst-followers.git'
	vagrant ssh -c 'mvn clean install -f ./check-inst-followers/app/pom.xml'
	vagrant ssh -c 'nohup java -jar ./check-inst-followers/app/target/inst-followers-0.0.1-SNAPSHOT.jar > inst-followers-app.log & sleep 20'
}


function init_vm {
	echo "Configuring virtual machine..."
	vagrant up
	vagrant ssh -c 'sudo apt -y update'
	vagrant ssh -c 'sudo apt -y install openjdk-8-jdk'
	install_utils
	install_elk
	echo "Virtual machine successfully configured"
}


if [[ $# -ne 1 ]]; then
    echo "$0: A single input file is required."
    exit 4
else
	if [[ $1 = "deploy" ]]; then
		install_vm
		deploy_vm
		init_vm
		echo "Virtual machine successfully deployed"
	elif [[ $1 = "destroy" ]]; then
		cd ~/$vm_dir
		vagrant destroy
		rm -rf ~/$vm_dir
	elif [[ $1 = "reload" ]]; then
		cd ~/$vm_dir
		vagrant reload
		sleep 120 # ждем пока сервисы стартанут
		run_application
	elif [[ $1 = "stop" ]]; then
		cd ~/$vm_dir
		vagrant halt
	elif [[ $1 = "start" ]]; then
		cd ~/$vm_dir
		vagrant up
		sleep 120 # ждем пока сервисы стартанут
		run_application
	elif [[ $1 = "suspend" ]]; then
		cd ~/$vm_dir
		vagrant suspend
	elif [[ $1 = "resume" ]]; then
		cd ~/$vm_dir
		vagrant resume
	elif [[ $1 = "help" ]]; then
		echo "Uage $0 [option]
			deploy - deploy, run and configure virtual machine
			destroy - stop and delete virtual machine
			reload - reload virtual machine
			stop - shut down virtual machine
			start - run virtual machine
			suspend - shut down virtual machine with saving RAM content
			resume - run suspended virtual machine
			help - get help info
		"
	else
		echo "$0: Wrong option. Use option help to get more info"
	fi
fi