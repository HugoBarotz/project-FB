# My project CRUD APIs

## Create
* Name must be unique & not empty

## Update
* Id must not be empty
* Name must be unique & not empty

## Search
* Filter can take a list of String: if any of those String is contained either 
in the project's name or in one of its skills name, then return the project
* Projects are sorted by name (asc)

## Delete bulk
Not meant to be used as a bulk API, made it mainly to train