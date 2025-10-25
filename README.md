# In-Memory Filesystem API (Proof of Concept)

This project is a draft proof of concept for a simple in memory filesystem API exposed over HTTP using Spark Java.  
It was originally developed as one component of a larger database project, which remains private.

It provides a basic hierarchical folder document structure entirely in memory.
It exposes a REST style API for creating, retrieving, and deleting folders and documents.
Demonstrates core file system operations without external storage or persistence.

Examples:

Create a folder:
curl -X PUT "http://localhost:8080/v1/?type=folder&name=myFolder"

Create a document in that folder:
curl -X PUT "http://localhost:8080/v1/myFolder?type=document&name=doc1" --data-binary 'Hello World!'

Get the document:
curl "http://localhost:8080/v1/myFolder/doc1"

List the folder:
curl "http://localhost:8080/v1/myFolder"

Delete the document:
curl -X DELETE "http://localhost:8080/v1/myFolder/doc1"
