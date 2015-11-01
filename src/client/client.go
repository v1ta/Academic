package main

import (
			"flag"
			"fmt"
			"strings"
			"gopkg.in/mgo.v2/bson"
			//"encoding/json"
			//"github.com/LDCS/sflag"
)

type Student struct{
	NetID 	bson.ObjectId 	`json: "id" bson:"_id"`
	Name 	string 			`json: "name"bson:"name"`
	Major 	string 			`json: "major"bson:"major"`
	Year 	int 			`json: "year"bson:"year"`
	Grade 	int 			`json: "grade"bson:"grade"`
	Rating 	string 			`json: "rating"bson:"rating"`
}


func (s *Student) String() string {
	return fmt.Sprint("%+v\n",*s)
}



func (s *Student) Set(value string) error {

	key := value[3:strings.Index(value,":")]
	val := value[strings.Index(value,":")+1:]

	fmt.Println(key)
	fmt.Println(val[:len(val)-3])

	return nil
}

var dataFlag Student


func init() {
	flag.Var(&dataFlag, "data", "Student Object")
}



func main(){
    urlPtr := flag.String("url", "http://localhost:8080", "Usage: -url=<URL of the webservice>")
    methodPtr := flag.String("method", "LIST", "Usage: -method=<Method to invoke on the webservice>")
    yearPtr := flag.Int("year", 2015, "Class year of the students")

    flag.Parse()

    fmt.Println("url:", *urlPtr)
    fmt.Println("method:", *methodPtr)
    fmt.Println("year:", *yearPtr)
    fmt.Println("data:", dataFlag)
    fmt.Println("tail:", flag.Args())
}