# MyPortfolio Back-End CRUD APIs

Welcome! Here is my back-end project for my personal portfolio website that you can find at https://myportfolio-eb126.web.app/index.html.
This project is mostly for training purposes, and contains:
* Project CRUD APIs with Spring Boot
* Swagger documentation
* Unit testing


# API information
## Create
* Name must be unique & not empty

## Update
* ID must not be empty
* Name must be unique & not empty

## Search
* Filter can take a list of String: if **any** of those String is contained either 
in the project's name or in one of its skills name, then return the project
* Projects are sorted by name (asc)

## Delete bulk
Not meant to be used as a bulk API, made it mainly to train