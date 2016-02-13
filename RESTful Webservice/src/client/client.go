package main

import (
    "flag"
    "fmt"  
    "bytes"
    "net/http"
    "encoding/json"
    "io/ioutil"
    "gopkg.in/mgo.v2/bson"
    "strings"
)

type (
    Class struct {
        Average     int        `json:"Average" bson:"Average"`
        Size        int        `json:"size" bson:"size"`
    }
)

type (
    Student struct {
        ID      bson.ObjectId   `json:"id" bson:"_id"`
        NetID   string          `json:"netid" bson:"netid"`
        Name    string          `json:"name" bson:"name"`
        Major   string          `json:"major" bson:"major"`
        Year    int             `json:"year" bson:"year"`
        Grade   int             `json:"grade" bson:"grade"`
        Rating  string          `json:"rating" bson:"rating"`
    }
)

func list(url string) {
    resp, err := http.Get(url)
    if err != nil {
        panic(err)
    }
    defer resp.Body.Close()
    contents, err := ioutil.ReadAll(resp.Body)

    if strings.Contains(url,"listall") {
        var s []Student
        json.Unmarshal(contents, &s)

        for i := 0; i < len(s);{
            fmt.Println(s[i])
            i++
        }
    } else {
        var s Student
        json.Unmarshal(contents, &s)
        fmt.Println(s)
    }
}

func create(url, jsonData string) {
    var jsonStr = []byte(jsonData)
    var s Student

    resp, err := http.Post(url, "application/json", bytes.NewBuffer(jsonStr))
    if err != nil {
        panic(err)
    }

    defer resp.Body.Close()
    contents, err := ioutil.ReadAll(resp.Body)
    json.Unmarshal(contents, &s)
    fmt.Println(s)
}

func delete(url, year string) {
    var class Class

    req, err := http.NewRequest("DELETE", url, nil)  
    req.Header.Set("year",year)
    if err != nil {
        panic(err)
    }
    resp, err := http.DefaultClient.Do(req)
    if err != nil {
        panic(err)
    }
    defer resp.Body.Close()
    contents, err := ioutil.ReadAll(resp.Body)
    json.Unmarshal(contents, &class)
    fmt.Println(class.Size," student(s) removed")
}

func update(url string) {
    var class Class

    resp, err := http.Get(url)
    if err != nil {
        panic(err)
    }
    defer resp.Body.Close()
    contents, err := ioutil.ReadAll(resp.Body)
    json.Unmarshal(contents, &class)
    fmt.Println("Class average is: ",class.Average)
}

func main() {
    urlPtr := flag.String("url", "http://localhost:1234", "Usage: -url=<URL of the webservice>")
    methodPtr := flag.String("method", "", "Usage: -method=<Method to invoke on the webservice>")
    dataPtr := flag.String("data", "", "Usage -data='{\"JSON\":\"Object\"}')")
    yearPtr := flag.String("year", "", "Usage -year=<year>")

    flag.Parse()

    switch *methodPtr {
    case "list":
        list(*urlPtr)
    case "create":
        create(*urlPtr, *dataPtr)
    case "remove":
        delete(*urlPtr, *yearPtr)
    case "update":
        update(*urlPtr)
    }
}