# saikorogame

### Frontend: 
HTML, CSS, JavaScript

### Backend:
Java 8
API: JDBC, WebSocket

### Database:
MySQL

## How to use:
1. Create a database with the prepared SQL file (/saikoroServer/db_create.sql)
2. if you are running the database on localhost, set accessing the database at port  3306 (mysql://localhost:3306/saikorogame)
3. Start the Java backend by compiling with the source code or double click the precompiled executable .jar (/saikoroServer/java/saikoroServer.jar)
4. Start the game by douoble clicking the login webpage (/saikoroGame/login.html)
5. "Status: Server Connected" will be shown on the webpage if the Java backend is activated.
6. You can login with pre-registered account "id: test1, password: test1" or create new account. Inputing an unregistered account will not be allowed to proceed to the game page.
7. After getting into the game page, click "Roll!" button to the play. The result will be obtained from the server and shown on the game page. The result of each game will be recorded in database automatically. Click "Play again!" to start a new game. 


## Minor problems
Due to the limation of time, there is some little problems or bugs not yet solved.

1. There is not prevention of duplicated user id. It is not listed in the requirement. It can be solved by adding a duplication checking in server.
2. Due to the time limitation, the session timeout function is not implemented in server side, however it is done in client side instead. This can be easily solved but it might cause some times. There is a table of game session in database. The server can obtain the session created time and compares with the non-active time in client side. It the delta of time is over 60 seconds (defaulted), a logout command can be sent to the client.