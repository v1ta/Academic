package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	//"io"
	//"io/ioutil"
	"github.com/julienschmidt/httprouter"
	//"github.com/swhite24/go-rest-tutorial/models"
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

// Getstudent retrieves an individual student resource
func (uc StudentController) GetStudent(w http.ResponseWriter, r *http.Request, p httprouter.Params) {
	// Grab id
	id := p.ByName("id")

	// Verify id is ObjectId, otherwise bail
	if !bson.IsObjectIdHex(id) {
		w.WriteHeader(404)
		return
	}

	// Grab id
	oid := bson.ObjectIdHex(id)

	// Stub student
	u := Student{}

	// Fetch student
	if err := uc.session.DB("").C("students").FindId(oid).One(&u); err != nil {
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

// Createstudent creates a new student resource
func (uc StudentController) CreateStudent(w http.ResponseWriter, r *http.Request, p httprouter.Params) {
	// Stub an student to be populated from the body
	u := Student{}
	
	//contents, err := ioutil.ReadAll(r.Body)
	//if err != nil{
	//	panic(err)
	//}
	
	//json.Unmarshal([]byte(string(contents)), &u)
	json.NewDecoder(r.Body).Decode(&u)
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

// Removestudent removes an existing student resource
func (uc StudentController) RemoveStudent(w http.ResponseWriter, r *http.Request, p httprouter.Params) {
	// Grab id
	id := p.ByName("id")

	// Verify id is ObjectId, otherwise bail
	if !bson.IsObjectIdHex(id) {
		w.WriteHeader(404)
		return
	}

	// Grab id
	oid := bson.ObjectIdHex(id)

	// Remove student
	if err := uc.session.DB("").C("students").RemoveId(oid); err != nil {
		w.WriteHeader(404)
		return
	}

	// Write status
	w.WriteHeader(200)
}