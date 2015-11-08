package main

import (
	"net/http"
	"github.com/julienschmidt/httprouter"
	"gopkg.in/mgo.v2"
)

func main() {
	r := httprouter.New()

	uc := NewStudentController(getSession())

	r.GET("/getstudent", uc.GetStudent)

	r.GET("/Student/listall", uc.ListStudents)

	r.POST("/Student", uc.CreateStudent)

	r.DELETE("/Student", uc.RemoveStudent)

	r.GET("/Student", uc.UpdateStudents)

	http.ListenAndServe("localhost:1234", r)
}

func getSession() *mgo.Session {
	// Connect to mongo
	s, err := mgo.Dial("mongodb://dsproject:password@dogen.mongohq.com:10052/bow-ties-are-hard-to-tie")

	if err != nil {
		panic(err)
	}

	return s
}

