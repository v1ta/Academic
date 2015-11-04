package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strings"
	//"io"
	"io/ioutil"
	"github.com/julienschmidt/httprouter"
	"gopkg.in/mgo.v2"
	"gopkg.in/mgo.v2/bson"
)

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

func (uc StudentController) GetStudent(w http.ResponseWriter, r *http.Request, p httprouter.Params) {
	field := p.ByName("field")

	key := field[1:strings.Index(field, "=")]
	value := field[strings.Index(field, "=")+1:]
	fmt.Println(field)
	fmt.Println(key)
	fmt.Println(value)
	u := Student{}

	// Fetch student
	if err := uc.session.DB("").C("students").Find(bson.M{key: value}).One(&u); err != nil {
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

	name := p.ByName("name")
	//test := name[strings.Index(name, "=")+1:]

	// Remove student
	if err := uc.session.DB("").C("students").Remove(bson.M{"name": name}); err != nil {
		w.WriteHeader(404)
		return
	}

	// Write status
	w.WriteHeader(200)
}