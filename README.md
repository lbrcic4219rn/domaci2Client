
# socket-chat

A multi-threaded Java console chat application: a small TCP server that accepts multiple clients, broadcasts messages, and keeps a short history.

## Features

* **Thread-safe message queue:** Uses `LinkedBlockingQueue` in `ChatServer` to queue messages for broadcast.
* **Concurrent connections:** Each client is handled by a dedicated `ServerThread`; `BroadcastThread` delivers queued messages to connected clients.
* **Message history:** Keeps the most recent 100 messages and delivers them to newly connected clients.
* **Basic censorship:** A configurable `forbiddenWords` list is used to sanitize messages before broadcasting.
* **Console client:** Simple reader/writer threads for interactive input and server output.

## Project Structure

```text
src/main/
├── client/          # console client (Main, ReaderThread, WriterThread)
└── server/          # server (Main, ChatServer, ServerThread, BroadcastThread, messages/)
```

## Configuration

* Server port: `9000` (see `main.server.Main`)
* Client connects to: `127.0.0.1:9000` (see `main.client.Main`)

## Notes

* No build tool is required; import the project into an IDE (IntelliJ/IDEA) or add a `pom.xml`/`build.gradle` for builds.
* To produce an executable JAR, compile and bundle the classes with a manifest pointing to the desired `Main` class.
