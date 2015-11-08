package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strings"
	//"net/url"
	"strconv"
	//"io"
	"io/ioutil"
	"github.com/julienschmidt/httprouter"
	"gopkg.in/mgo.v2"
	"gopkg.in/mgo.v2/bson"
)

type (
    Class struct{
        Average   int        `json:"average" bson:"average"`
        Size int 		`json:"size" bson:"size"`
    }
)


var validQuery = []string {"Year", "Netid", "Name", "Rating", "Major", "Grade"}

type (
	// studentController represents the controller for operating on the student resource
	StudentController struct {
		session *mgo.Session
	}
)

// NewstudentController provides a reference to a studentController with provided mongo session
func NewStudentController(s *mgo.Session) *StudentController {
	return &StudentController{s}
}

func (uc StudentController) ListStudents(w http.ResponseWriter, r *http.Request, p httprouter.Params) {
	var students []Student
    err := uc.session.DB("").C("students").Find(nil).All(&students)
    if err != nil {
        panic(err)
    }

	// Marshal provided interface into JSON structure
	uj, _ := json.Marshal(students)

	// Write content-type, statuscode, payload
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(200)
	fmt.Fprintf(w, "%s", uj)
}

// Done minus NETID
func (uc StudentController) GetStudent(w http.ResponseWriter, r *http.Request, p httprouter.Params) {
	key := r.URL.RawQuery[:strings.Index(r.URL.RawQuery, "=")]
	value := r.URL.RawQuery[strings.Index(r.URL.RawQuery, "=")+1:]

	u := Student{}

	// Fetch student
	if err := uc.session.DB("").C("students").Find(bson.M{key: &bson.RegEx{Pattern: value, Options: "i"}}).One(&u); err != nil {
		w.WriteHeader(404)
		return
	}

	// Marshal provided interface into JSON structure
	uj, _ := json.Marshal(u)

	// Write content-type, statuscode, payload
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(200)
	fmt.Fprintf(w, "%s", uj)
}

func (uc StudentController) CreateStudent(w http.ResponseWriter, r *http.Request, p httprouter.Params) {
	// Stub an student to be populated from the body
	u := Student{}
	
	contents, err := ioutil.ReadAll(r.Body)
	if err != nil{
		panic(err)
	}

	json.Unmarshal([]byte(string(contents)), &u)

	u.NetID = bson.NewObjectId()

	// Write the student to mongo
	uc.session.DB("").C("students").Insert(u)

	// Marshal provided interface into JSON structure
	uj, _ := json.Marshal(u)

	// Write content-type, statuscode, payload
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(201)
	fmt.Fprintf(w, "%s", uj)
}

func (uc StudentController) RemoveStudent(w http.ResponseWriter, r *http.Request, p httprouter.Params) {
	
    keys := make([]string, 0, len(r.Header))
	for k := range r.Header {
		if stringInSlice(k,validQuery){
    		keys = append(keys, strings.ToLower(k))
    	}
	}

	// TODO add mulit key functionaltiy 
	key := keys[0]
	value, err := strconv.Atoi(r.Header.Get("year"))
		if err != nil{
		panic(err)
	}

	// Remove student(s) w/year <= value
	query := bson.M{key: bson.M{"$lte": value}}
	var class Class
	if resp, err := uc.session.DB("").C("students").RemoveAll(query); err != nil {
		w.WriteHeader(404)
		return 
	} else {
		class.Size = resp.Removed
		fmt.Println("Removed ",resp.Removed," students")
		uj, _ := json.Marshal(class)
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(200)
		fmt.Fprintf(w, "%s", uj)
	}

	
}


func (uc StudentController) UpdateStudents(w http.ResponseWriter, r *http.Request, p httprouter.Params) { 
	var students []Student

	err := uc.session.DB("").C("students").Find(nil).All(&students)
    if err != nil {
        panic(err)
    }

    sum := 0
	for i := 0; i < len(students); i++ {
		sum = sum + students[i].Grade
	}
	avg := sum / len(students)

	for i := 0; i < len(students); i++ {
        if students[i].Grade >= avg - 20 {
        	var grade string
			if students[i].Grade >= avg + 10{
				grade = "A"
			} else if students[i].Grade >= avg - 10{
				grade = "B"
			} else {
				grade = "C"
			}
			fmt.Println("Updating grade for: ",students[i].Name, " from ",students[i].Rating, " to ", grade)
			err := uc.session.DB("").C("students").Update(bson.M{"_id": students[i].NetID},bson.M{"$set": bson.M{"rating": grade}})
			if err != nil {
				panic(err)
			}
		} 
	}

	var class Class
	class.Average = avg


    // Marshal provided interface into JSON structure
    uj, _ := json.Marshal(class)

    // Write content-type, statuscode, payload
    w.Header().Set("Content-Type", "UTF")
    w.WriteHeader(200)
    fmt.Fprintf(w, "%s", uj)	

}

func stringInSlice(a string, list []string) bool {
    for _, b := range list {
        if b == a {
            return true
        }
    }
    return false
}
