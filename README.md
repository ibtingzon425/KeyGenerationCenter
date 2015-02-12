# CS199

To setup SSL connection, execute the following commands on the server side to create the server.jks and server.crt files. 
Use server.jks and the password ("password") to start server.

Server-side:
keytool -genkey -alias server -keyalg RSA -keystore server.jks

keytool -export -file server.cert -keystore server.jks -storepass password -alias server

keytool -exportcert -file server.crt -keystore server.jks -storepass password -alias server

Afterwards, copy server.crt file into the directory of the client. Execute the following command to create the public.jks file.
Use public.jks and password ("password") to start client.

Client-side:
keytool -importcert -file server.crt -keystore public.jks -storepass password -alias client


