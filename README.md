blogger (wip)
=======

## Dependencies
* sbt
* mongodb runnning on port 27017

## Set-up

	$ brew install sbt
	$ brew install mongo

## Quick-Start (have dependencies)

	$ cd backend 
	$ sbt run

## Test api using httpie (in another terminal)

	$ brew install httpie

## Ping webserver 
	
	$ httpie localhost:9000/api/ping
	HTTP/1.1 200 OK
    Content-Length: 4
    Content-Type: text/plain; charset=utf-8

    pong

## Create user 

    $ http POST localhost:9000/api/users uid=bobloblaw eventId=justice1
    HTTP/1.1 201 Created
    Content-Length: 62
    Content-Type: application/json; charset=utf-8

    {
    	"auth_token": "53cd2a1520387b4600aad265", 
        "msg": "User Created"
    }

## Get user by uid

    $ http localhost:9000/api/users/bobloblaw
    HTTP/1.1 200 OK
	Content-Length: 122
	Content-Type: application/json; charset=utf-8

	{
		"_id": {
			"$oid": "53cd2a154947dcd95a6b720f"
			},
		"auth_token": "53cd2a1520387b4600aad265", 
		"eventId": "justice1", 
		"uid": "bobloblaw"
	}

## Get list of users 

	$ http POST localhost:9000/api/users uid=tom eventId=foolery69
	$ http localhost:9000/api/users
	HTTP/1.1 200 OK
    Content-Length: 380
    Content-Type: application/json; charset=utf-8
    [
    	{
        	"_id": {
            	"$oid": "53cd2a154947dcd95a6b720f"
        	}, 
        	"auth_token": "53cd2a1520387b4600aad265", 
        	"eventId": "justice1", 
        	"uid": "bobloblaw"
    	}, 
    	{
        	"_id": {
            	"$oid": "53cd2adc4947dcd95a6b7210"
        	}, 
        	"auth_token": "53cd2adc20387b4800aad266", 
        	"eventId": "foolery69", 
        	"uid": "tom"
    	}
	]










