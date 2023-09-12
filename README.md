# the-java-fest-chobichokro
The project is about therap java fest

# The documentation for the api endpoint of the application #

## Authorization EndPoint ##

### / api / auth / signup ###
#### POST ####
##### Description #####
The endpoint is used to register a new user in the system.
##### Request #####
###### Body ######
```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "Role": "set of String",
  "licenseId": "string" // required if the user is a distributor of a theater owner
  
}
```
##### Response #####
###### Body ######
```json
{
  "id": "string",
  "username": "string",
  "email": "string",
  "Role" : "set of String",
  "licenseId" : "string" // required if the user is a distributor of a theater owner
}
```

### / api / auth / signin ###
### Post ###
##### Description #####
The endpoint is used to log in to the system.

##### Request #####
###### Body ######
```json
{
  "username": "string",
  "password": "string"
}
```
##### Response #####
###### Body ######
```json
{
  "id": "string",
  "access_token" : "string",
  "token type" : "Bearer"
}
// the access token is valid for 24 hours
the access token need to be sent in the header of the request under "Authorization" to be a authorize user of the system

```
## Movie controller endpoint ##

### / api / movie / all ###
#### GET ####
##### Description #####
The endpoint is used to get all the movies in the system.
##### Request #####
no request body
##### Response #####
###### Body ######
```json
[
  {
    "id": "string",
    "name": "string",
    "description": "string",
    "genre": ["string"],
    "releaseDate": "string",
    "director": ["string"],
    "cast": "string",
    "posterLink": "string",
    "trailerLink": "string",
   
  }
]
```

### / api / movie / get / {imagePath} ###
#### GET ####
##### Description #####
The endpoint is used to get the image of the movie.
##### Request #####
no request body
##### Response #####
###### Body ######
```json
{
  "image": "byte[]"
}
```

### / api / movie / query / {query_string} ###
#### GET ####
##### Description #####
The endpoint is used to get the movies that match the query string.
##### Request #####
no request body
##### Response #####
###### Body ######
```json
[
  {
    "id": "string",
    "name": "string",
    "description": "string",
    "genre": ["string"],
    "releaseDate": "string",
    "director": ["string"],
    "cast": "string",
    "posterLink": "string",
    "trailerLink": "string"
   
  }
]
```

## License controller endpoint ##
### / api / license / add ###
#### POST ####
##### Description #####
The endpoint is used to add a new license to the system.
##### Request #####
```json
    {
  ""
}
```
### api / license / get / pending ###
### Get Method ###
Preauthorize for Admin user
