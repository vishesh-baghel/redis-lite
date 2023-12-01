
# RedisLite

RedisLite is a simple imitation of redis built with Java.

This project doesn't even use any dependency to run because it is built without any Java Framework.

RedisLite stores key-value pairs using ConcurrentHashmap for in-memory storage.

This project contains two modules.
- RedisServer
- RedisClient

### RedisClient

Using this client, you can send commands to the redis server and see how the server is handling the commands in different threads.

RedisClient supports the following commands as of now:
- ping: This command returns +PONG to the client.
- echo: This command returns the string as it is.
- set: this command takes three parameters: key, value and key expiry time.

    ```
    sample command: 
    set toy car 20000L
    ```
- get: this command will fetch the value for the given key. If the key is expired, then `null` is returned.

    ```
    sample command: 
    get toy

    output: 
    car
    ```
### RedisServer

This is the heart of this project. The server handles all the concurrency in the project and is able to handle multiple client connections with concurrent read and writes.

The server also prints the thread logs on the console, so you can view which thread is handling which command.

## Important Note

There are some known bugs present in the project as of now. I am new to concurrency and having some problems in handling multiple threads.

Feel free to inform me directly, if you find some bugs.
## Licenses


[![MIT License](https://img.shields.io/badge/License-MIT-green.svg)](https://choosealicense.com/licenses/mit/)
[![GPLv3 License](https://img.shields.io/badge/License-GPL%20v3-yellow.svg)](https://opensource.org/licenses/)
[![AGPL License](https://img.shields.io/badge/license-AGPL-blue.svg)](http://www.gnu.org/licenses/agpl-3.0)


## Run Locally

Clone the project

```bash
  git clone https://github.com/vishesh-baghel/redis-lite.git
```

Go to the project directory and 

```bash
  cd RedisLite
```

Start the RedisServer:

```bash
  javac RedisServer.java
  java RedisServer
```
Start the RedisClient:

```bash
  javac RedisClient.java
  java RedisClient
```



## Tech Stack

**Client:** Java

**Server:** Java, built-in collections for storage


## Support

For support, email me at visheshbaghel99@gmail.com

