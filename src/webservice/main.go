package main

import (
	// Standard library packages
	"net/http"

	"github.com/julienschmidt/httprouter"
	"gopkg.in/mgo.v2"
)

func main() {
	r := httprouter.New()

	uc := NewStudentController(getSession())

	r.GET("/Student/getstudent:field", uc.GetStudent)

	r.GET("/Student/listall", uc.ListStudents)

	r.POST("/Student", uc.CreateStudent)

	r.DELETE("/Student/", uc.RemoveStudent)

	http.ListenAndServe("localhost:8080", r)
}

func getSession() *mgo.Session {
	// Connect to mongo
	s, err := mgo.Dial("mongodb://dsproject:password@dogen.mongohq.com:10052/bow-ties-are-hard-to-tie")

	if err != nil {
		panic(err)
	}

	// Deliver session
	return s
}

//s, err := mgo.Dial("mongodb://dsproject:password@dogen.mongohq.com:10052/bow-ties-are-hard-to-tie")