package main

import (
	"flag"
	"fmt"  
	"gopkg.in/mgo.v2/bson"
	"bytes"
	"net/http"
	"io/ioutil"

)



type Student struct{
	NetID 	bson.ObjectId 	`json:"id" bson:"_id"`
	Name 	string 			`json:"name" bson:"name"`
	Major 	string 			`json:"major" bson:"major"`
	Year 	int 			`json:"year" bson:"year"`
	Grade 	int 			`json:"grade" bson:"grade"`
	Rating 	string 			`json:"rating" bson:"rating"`
}


func main(){
    urlPtr := flag.String("url", "http://localhost:8080", "Usage: -url=<URL of the webservice>")
    methodPtr := flag.String("method", "LIST", "Usage: -method=<Method to invoke on the webservice>")
    dataPtr := flag.String("data", "", "student")
    flag.Parse()
    fmt.Println(*dataPtr)

    if *methodPtr == "Create" {
        fmt.Println("create")
    var jsonStr = []byte(*dataPtr)
        resp, err := http.Post(*urlPtr, "application/json", bytes.NewBuffer(jsonStr))
    if err != nil {
        panic(err)
    }
    defer resp.Body.Close()
        contents, err := ioutil.ReadAll(resp.Body)
        fmt.Println(string(contents))
    }

}
