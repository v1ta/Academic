package main

import (
			"flag"
			"fmt"
			"strings"
			"gopkg.in/mgo.v2/bson"
			"github.com/fatih/structs"
			"strconv"
)

var dataFlag Student
var m = structs.New(&dataFlag)

type Student struct{
	Id 		bson.ObjectId 	`json:"id" bson:"_id"`
	NetID 	string 			`json:"netid" bson:"netid"`
	Name 	string 			`json:"name" bson:"name"`
	Major 	string 			`json:"major" bson:"major"`
	Year 	int 			`json:"year" bson:"year"`
	Grade 	int 			`json:"grade" bson:"grade"`
	Rating 	string 			`json:"rating" bson:"rating"`
}

func (s *Student) String() string {
	return fmt.Sprint("%+v\n",*s)
}

func (s *Student) Set(value string) error {

	key := value[3:strings.Index(value,":")]
	val := value[strings.Index(value,":")+1:]
	field := m.Field(key)

	if key == "Year" || key == "Grade"{
		x, err := strconv.Atoi(val[:len(val)-3]) 
		field.Set( x)
		if err != nil {
			panic(err)
		}
	}else{
		field.Set(val[:len(val)-3])
	}
	
	return nil
}

func init() {
	flag.Var(&dataFlag, "data", "Student Object")
}

func main(){
    urlPtr := flag.String("url", "http://localhost:8080", "Usage: -url=<URL of the webservice>")
    methodPtr := flag.String("method", "LIST", "Usage: -method=<Method to invoke on the webservice>")
    yearPtr := flag.Int("year", 2015, "Class year of the students")

    flag.Parse()

    //fmt.Println("url:", *urlPtr)
    //fmt.Println("method:", *methodPtr)
    //fmt.Println("year:", *yearPtr)
    if *(&dataFlag.NetID) != ""{

	    fields := m.Fields()
	    field := m.Field("Id")
	    field.Set(bson.NewObjectId())

		for _, f := range fields {
		    fmt.Printf("field name: %+v\n", f.Name())

		    if f.IsExported() {
		        fmt.Printf("value   : %+v\n", f.Value())
		        fmt.Printf("is zero : %+v\n", f.IsZero())
		   }
		}
    } 



}