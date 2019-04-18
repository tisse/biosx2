bios
==============================================================================================

Build and Deploy the Quickstart
-------------------------

1. Type this command to build and deploy the archive:

        mvn clean compile package wildfly:deploy -P bios
        mvn clean compile package wildfly:deploy -P sklad
        
2. This will deploy `target/bios.ear` to the running instance of the server.


Access the application 
---------------------

The application will be running at the following URL: <server:8080/bios/>.
