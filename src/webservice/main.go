package main

import (
	"log"
	"net/http"
	"gopkg.in/mgo.v2"
)

func main() {

	/* Start webserver */
	router := NewRouter()
	log.Fatal(http.ListenAndServe(":8080", router))

	/* connect to remote mongoDB */
	session, err := mgo.Dial("mongodb://dsproject:password@dogen.mongohq.com:10052/bow-ties-are-hard-to-tie")
	if err != nil {
		panic(err)
	}
	defer session.Close()

	
}
