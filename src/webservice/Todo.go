package main

import(	 	"time"
			"gopkg.in/mgo.v2/bson"
)

type Todo struct {
    Id        int       `json:"id"`
    Name      string    `json:"name"`
    Completed bool      `json:"completed"`
    Due       time.Time `json:"due"`
}

type Student struct{
	NetID 	bson.ObjectId 	`json: "id" bson:"_id"`
	Name 	string 			`json: "name"bson:"name"`
	Major 	string 			`json: "major"bson:"major"`
	Year 	int 			`json: "year"bson:"year"`
	Grade 	int 			`json: "grade"bson:"grade"`
	Rating 	string 			`json: "rating"bson:"rating"`
}

type Students []Student

type Todos []Todo