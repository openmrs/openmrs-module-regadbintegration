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

## After Compiling the Module

Load the omod file.

It gives an error after loading the omod file,Go to 'Advanced Settings' in the administration page.

 Fill the global properties for regadb  

 regadb password- regadb_password

 regadb Username- regadb_user

 regadb remoteinstanceAddress- localhost
 
 Try starting the module again. 
 
 
 
 
 **If you need not require RegaDB any more you simply run 'vagrant destroy' command in your cmd from your vagrant folder.**
 
