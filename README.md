# BACKEND

## Database

### Migrations

We use Flyway for database migrations.

We're currently utilizing the Flyway Java API and migrate automatically on application startup.

## Endpoints

### Tenants

*GET /tenants*
List all tenants

### Users

*GET /[tenantId]/users*
List all users for a tenant

*GET /[tenantId]/users/[userId]*
Get a user by id
Returns: user or 404 if not found

*POST /[tenantId]/users*
Create a new user
Input: user json object in body
Returns: id of the created user

*PUT /[tenantId]/users/[userId]*
Update user with new information
Input: user json object in body

### Teams

*GET /[tenantId]/teams*
List all teams for a tenant

*GET /[tenantId]/teams/[teamId]*
Get a team by id
Returns: team or 404 if not found

*POST /[tenantId]/teams*
Create a new team
Input: team json object in body
Returns: id of the created team

*PUT /[tenantId]/teams/[teamId]*
Update team with new information
Input: team json object in body

## Local setup

### Requirements

* docker
	- VirtualBox
	- boot2docker

## Infrastructure

Application run on the JVM in containers

Service discovery via etcd

### Frameworks

* Finch - framework
* Twitter-server - server
* Slick - Database connection
* Scalatest - testing

### Supporting services

* Image storage - S3?
* Database - MariaDB?
