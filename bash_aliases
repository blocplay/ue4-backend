# To install and initiate this process use this
# cd
# scp ue4backend@45.76.17.249:/home/ue4backend/repo/ue4Backend/bash_aliases /home/ue4backend/.bash_aliases
# sudo apt-get install dos2unix
# dos2unix .bash_aliases
# source .bash_aliases
# You can then install with ue4_install
# ue4backend should be in the sudoers group, usually by executing 'sudo usermod -aG sudo ue4backend' with a super/sudo user
# Don't forget to edit /home/ue4backend/config/application.yml to write the appropriate database and ip properties
alias pre_install='cd ; mkdir certs ; \
  scp ue4backend@45.76.17.249:/home/ue4backend/certs/RapidSSLSHA256.cer /home/ue4backend/certs/RapidSSLSHA256.cer ; \
  sudo keytool -keystore /usr/lib/jvm/java-8-oracle/jre/lib/security/cacerts -storepass changeit -import -trustcacerts -alias RapidSSLSHA256 -file /home/ue4backend/certs/RapidSSLSHA256.cer ; \
  '
alias ue4_install='cd ; \
  mv latest-ue4backend.jar ue4backend.jar ; \
  chmod 500 ue4backend.jar ; \
  sudo ln -s /home/ue4backend/ue4backend.jar /etc/init.d/ue4backend ; \
  sudo systemctl daemon-reload ; \
  sudo update-rc.d ue4backend defaults ; \
  sudo update-rc.d ue4backend enable'
alias ue4_svnupdate='cd repo ; \
  svn revert -R . ; \
  svn update -r HEAD --force ; \
  mvn clean install -P generateBinary ; \
  cd'
alias ue4_update='cd ; \
  ue4_newversion ; \
  sudo service ue4backend stop ; \
  rm -f ue4backend.jar ; \
  sudo rm /etc/init.d/ue4backend ; \
  ue4_install ; \
  sudo service ue4backend start ; \
  printf "Showing logs... \n.......................\n\n" ; \
  tail -f /home/ue4backend/logs/ue4backend.log'
alias ue4_newversion='scp ue4backend@45.76.17.249:/home/ue4backend/latest-ue4backend.jar /home/ue4backend'
 