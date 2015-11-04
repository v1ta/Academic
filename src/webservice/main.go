package main

import (
	// Standard library packages
	"net/http"

	// Third party packages
	"github.com/julienschmidt/httprouter"
	//"github.com/swhite24/go-rest-tutorial/controllers"
	"gopkg.in/mgo.v2"
)

func main() {
	// Instantiate a new router
	r := httprouter.New()

	// Get a UserController instance
	uc := NewStudentController(getSession())

	// Get a user resource
	r.GET("/Student/:id", uc.GetStudent)

	// Create a new user
	r.POST("/Student", uc.CreateStudent)

	// Remove an existing user
	r.DELETE("/Student/:id", uc.RemoveStudent)

	// Fire up the server
	http.ListenAndServe("localhost:8080", r)
}

// getSession creates a new mongo session and panics if connection error occurs
func getSession() *mgo.Session {
	// Connect to our local mongo
	s, err := mgo.Dial("mongodb://dsproject:password@dogen.mongohq.com:10052/bow-ties-are-hard-to-tie")

	// Check if connection error, is mongo running?
	if err != nil {
		panic(err)
	}

	// Deliver session
	return s
}

//s, err := mgo.Dial("mongodb://dsproject:password@dogen.mongohq.com:10052/bow-ties-are-hard-to-tie")