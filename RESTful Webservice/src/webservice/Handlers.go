package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strings"
	"strconv"
	"io/ioutil"
	"github.com/julienschmidt/httprouter"
	"gopkg.in/mgo.v2"
	"gopkg.in/mgo.v2/bson"
)

type (
    Class struct{
        Average  	int        `json:"average" bson:"average"`
        Size 		int 	   `json:"size" bson:"size"`
    }
)

var validQuery = []string {"Id", "Year", "Netid", "Name", "Rating", "Major", "Grade"}

/* StudentController for operating on a *student resource */
type (
	StudentController struct {
		session *mgo.Session
	}
)

/* NewstudentController provides a reference to a studentController with provided mongo session */
func NewStudentController(s *mgo.Session) *StudentController {
	return &StudentController{s}
}

func (uc StudentController) ListStudents(w http.ResponseWriter, r *http.Request, p httprouter.Params) {
	var students []Student

    err := uc.session.DB("").C("students").Find(nil).All(&students)
    if err != nil {
        panic(err)
    }
	uj, _ := json.Marshal(students)
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(200)
	fmt.Fprintf(w, "%s", uj)
}

func (uc StudentController) GetStudent(w http.ResponseWriter, r *http.Request, p httprouter.Params) {
	key := r.URL.RawQuery[:strings.Index(r.URL.RawQuery, "=")]
	value := r.URL.RawQuery[strings.Index(r.URL.RawQuery, "=")+1:]

	u := Student{}
	if err := uc.session.DB("").C("students").Find(bson.M{key: &bson.RegEx{Pattern: value, Options: "i"}}).One(&u); err != nil {
		w.WriteHeader(404)
		return
	}
	uj, _ := json.Marshal(u)
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(200)
	fmt.Fprintf(w, "%s", uj)
}

func (uc StudentController) CreateStudent(w http.ResponseWriter, r *http.Request, p httprouter.Params) {
	u := Student{}
	contents, err := ioutil.ReadAll(r.Body)

	if err != nil{
		panic(err)
	}
	json.Unmarshal(contents, &u)
	u.ID = bson.NewObjectId()

	count, err:= uc.session.DB("").C("students").Find(bson.M{"netid": u.NetID}).Count(); 
	if err != nil {
		w.WriteHeader(404)
		return
	} else if count > 0 {
		w.WriteHeader(404)
		return
	}

	uc.session.DB("").C("students").Insert(u)
	uj, _ := json.Marshal(u)
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
	key := keys[0]
	value, err := strconv.Atoi(r.Header.Get("year"))

	if err != nil{
		panic(err)
	}
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
	var class Class

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
			err := uc.session.DB("").C("students").Update(bson.M{"_id": students[i].ID},bson.M{"$set": bson.M{"rating": grade}})
			if err != nil {
				panic(err)
			}
		} 
	}

	class.Average = avg
    uj, _ := json.Marshal(class)
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
