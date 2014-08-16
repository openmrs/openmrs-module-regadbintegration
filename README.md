openmrs-module-regadbintegration
================================
To try this module you need to setup RegaDB.

## Steps to setup RegaDB
  
   After checking out the code
   
   -> Open the Terminal(linux) or cmd(windows)
   
   ->move into vagrant folder and run the command 'vagrant up' 
   
    During the setup it will ask to run 'vagrant provision' command. 
   
      Prerequisite before you run the command is to install virtualbox on your system
   
      You can download it from here- https://www.virtualbox.org/wiki/Downloads
   
   It will take some time to finish the setup.
   
   ->After it is finished,You have RegaDB setup on your system.
   
## Before Compiling the Module
  
  Before compiling the code, You need to install two jar files manually into you repo
   
   ->Download the following jar files.
   
             xls - https://www.dropbox.com/s/7hxv1npdlcxqoua/xls.jar
   
             wts-client - https://www.dropbox.com/s/r9xi5r4yfp7kz7w/wts-client.jar
   
   ->Steps to install the jars
     
        ->Open terminal or cmd
     
      Run the following command to install xls.jar
      
      mvn install:install-file -Dfile=SPECIFY_PATH_TO_FILE_LOCATION\xls.jar -DgroupId=com.rega.code -DartifactId=xls 
      -Dversion=1.0 -Dpackaging=jar
        
      Run the following command to install wts-client.jar  
      
     mvn install:install-file -Dfile=SPECIFY_PATH_TO_FILE_LOCATION\wts-client.jar -DgroupId=net.sf.wts.client 
     -DartifactId=wts-client -Dversion=0.9 -Dpackaging=jar

   After installing the two jars you can compile the module in Eclipse IDE.

## Installing WTS (Web Terrain Service)

WTS is an external application through which OpenMRS interacts with RegaDB.  

##### Create directory structure for wts

      On terminal, Execute the following
   
        cd /etc/
   
       sudo mkdir wt
   
       cd wt
   
       sudo mkdir wts
   
   Place the configuration file **wts.xml** in this folder
   
       sudo cp "current location of wts.xml" /etc/wt/wts/wts.xml
 
 and change the permissions of this folder
 
       sudo chown -R tomcat6:tomcat6 /etc/wt/wts/

This file contains the expire time and the location of the directory in which the server stores its data. You can     change these if you want an alternative location.

 If this is the default location /etc/serverdir/wts/ then execute the following
 
        cd /etc/

        sudo mkdir serverdir

        cd serverdir

        mkdir wts

        cd wts

        sudo mkdir services

        sudo mkdir sessions

        sudo mkdir users
 
  copy the users.pwd to the users directory, whose contents are "public:TJGE83z/AbzcMtxIbsNpYQ=="
    
      sudo cp "current location of users.pwd" /etc/serverdir/wts/users/users.pwd
   
  copy the given services - generate-report, import-xls and get-algorithms to the services folder
   
   NOTE: You need to copy a JVM (java) folder to the folder **/opt/** and give tomcat6 permissions to execute the java   command
   
  Do as follows:
  
        sudo cp -R "/usr/lib/jvm/java-6-sun (or) /usr/lib/jvm/java-6-sun-1.6.0.20 /opt/" depending on which you intend using
  
        sudo chown -R tomcat6:tomcat6 /opt/java-folder-you-copied
  
        sudo chmod -R 777 /opt/java-folder-you-copied
  
  Then for each of the startService executable file in the folders, (/etc/serverdir/wts/services/ generate-report,  get-algorithms and import-xls)
  
    edit the line that starts with #$JAVA_HOME/bin/java -cp regadb-analyses-0.9.jar:..., to
  
    /opt/java-6-sun-1.6.0.20/bin/java -cp regadb-analyses-0.9.jar:...
  
    now copy the rest of the folders
  
       sudo cp -R "current location of generate-report folder" /etc/serverdir/wts/services/
  
        sudo cp -R "current location of import-xls folder" /etc/serverdir/wts/services/
  
       sudo cp -R "current location of get-algorithms folder" /etc/serverdir/wts/services/
  
  and modify the permissions of this folder as well
  
       sudo chown -R tomcat6:tomcat6 /etc/serverdir/wts/
  
 Deploy wts into tomcat
  
    In order to deploy wts in tomcat copy the wts.war to tomcat's webapps
  
#####With this we have successfully installed wts 
   
   
## After Compiling the Module

Load the omod file.

It gives an error after loading the omod file,Go to 'Advanced Settings' in the administration page.

 Fill the global properties for regadb  

 regadb password- admin

 regadb Username- admin

 regadb remoteinstanceAddress- localhost
 
 Try starting the module again. 
 
 
 Here's the user manual for the module http://aniketha.github.io/openmrs-module-regadbintegration-1/
 
 
 **If you need not require RegaDB any more you simply run 'vagrant destroy' command in your cmd from your vagrant folder.**
 
