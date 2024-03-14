# Chat server

## Build and run
To build and run the application you need to start a MySQL server on 3306 port.
The server must have 2 databases:
* A database with a name `jooq` to generate jooq classes
* A database with a name `chat` to contain the application data

Each database should have a ddl defined in the `resources/migartions` scripts

To build the application run `:build` gradle task

## API doc
https://www.postman.com/winter-robot-655785/workspace/chat/collection/2072132-677679c2-f537-41b5-a0a9-d6af7a864560