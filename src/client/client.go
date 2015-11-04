package main

import (
			"flag"
			"fmt"
			"strings"
			"gopkg.in/mgo.v2/bson"
			"github.com/fatih/structs"
			"strconv"
	"net/http"
	"io/ioutil"
	"os"
	"encoding/json"
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

func sendGet(url string, data *Student) {
    encode, err := json.Marshal(&data)
    if err != nil{
	fmt.Printf("%s", err)
        os.Exit(1)
    }
    
    encodedString := string(encode)
    resp, err := http.Get(encodedString)
    if err != nil {
        fmt.Printf("%s", err)
        os.Exit(1)
    } 
    robots, err := ioutil.ReadAll(resp.Body)
    resp.Body.Close()
    if err != nil {
	fmt.Printf("%s", err)
        os.Exit(1)
    }

    var decode Student
    err = json.Unmarshal(robots, decode)
    if err != nil {
        fmt.Printf("%s", err)
        os.Exit(1)
    }

    fmt.Println("%s", decode.NetID)
}

func sendPost(url string, data *Student) {
    
}

func main(){
    urlPtr := flag.String("url", "http://localhost:8080", "Usage: -url=<URL of the webservice>")
    methodPtr := flag.String("method", "LIST", "Usage: -method=<Method to invoke on the webservice>")
    yearPtr := flag.Int("year", 2015, "Class year of the students")

    flag.Parse()

    fmt.Println("url:", *urlPtr)
    fmt.Println("method:", *methodPtr)
    fmt.Println("year:", *yearPtr)
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

    data := &Student {
	Id:"",
	NetID: "123", 
        Name: "daoun",
        Major: "cs",
        Year: 2016,
        Grade: 90,
        Rating: "A",
    }
    

    if *methodPtr == "Create" || *methodPtr == "create" {
	sendPost(*urlPtr, data)
    } else if *methodPtr == "List" || *methodPtr == "list" || *methodPtr == "LIST" {
	sendGet(*urlPtr, data)
    } else {
	fmt.Println("i dont know")
    }

}
