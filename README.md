# Bandeiras

A Feature Flag service implemented in Kotlin.

## Goals

- Have a client-centric implementation, **optionally** backed by a server
- Have a secure and reliable server implementation
- Provide pretty much all functionality out-of-the-box, almost no configuration needed
- Support major "server environments": Spring/Spring Boot, Ktor, Play Framework, etc.
- Be **SAFE** and reliable: Have audit logs (on the server), make it so that you can't change values on runtime (client)
