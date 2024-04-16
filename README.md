# jdbc-sqlite

A sample for creating and accessing SQLite database files in Java

## Prerequisites

- Java (≧ 1.8)
- [Apache Maven](https://maven.apache.org/)

## Usage

```
$ touch {im,ex}port
$ mvn package
$ java -jar target/data-conversion-1.0.0-jar-with-dependencies.jar <dbFile> <importDir> <exportDir> <exportFile>
```

or execute `run.bat`

## Run test

```
$ mvn test
```
