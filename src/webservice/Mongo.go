package main

import "mgo"

type MongoConnection struct {
	originalSession *mgo.Session
}
