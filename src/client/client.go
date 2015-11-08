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
    Class struct{
        Average   int             `json:"Average" bson:"Average"`
        Size int        `json:"size" bson:"size"`
    }
)

type (
    Student struct{
        NetID   bson.ObjectId   `json:"_id" bson:"_id"`
        Name    string          `json:"name" bson:"name"`
        Major   string          `json:"major" bson:"major"`
        Year    int             `json:"year" bson:"year"`
        Grade   int             `json:"grade" bson:"grade"`
        Rating  string          `json:"rating" bson:"rating"`
    }
)

func list(url string)  {
    resp, err := http.Get(url)
    if err != nil {
        panic(err)
    }
    defer resp.Body.Close()
    contents, err := ioutil.ReadAll(resp.Body)
    if strings.Contains(url,"listall"){
        var s []Student
        json.Unmarshal(contents, &s)
        for i := 0; i < len(s);{
            fmt.Println(s[i])
            i++
        }
    }else {
        var s Student
        json.Unmarshal(contents, &s)
        fmt.Println(s)
    }
}

func create(url, jsonData string) {
        var jsonStr = []byte(jsonData)
        resp, err := http.Post(url, "application/json", bytes.NewBuffer(jsonStr))
        if err != nil {
            panic(err)
        }
        defer resp.Body.Close()
        contents, err := ioutil.ReadAll(resp.Body)
        var s Student
        json.Unmarshal(contents, &s)
        fmt.Println(s)
}

func delete(url, year string)  {
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
    var class Class
    json.Unmarshal(contents, &class)
    fmt.Println(class.Size," student(s) removed")
}

func update(url string){
    resp, err := http.Get(url)
    if err != nil {
        panic(err)
    }
    defer resp.Body.Close()
    contents, err := ioutil.ReadAll(resp.Body)
    var class Class
    json.Unmarshal(contents, &class)
    fmt.Println("Class average is: ",class.Average)
    
    
}

func main(){
    urlPtr := flag.String("url", "http://localhost:1234", "Usage: -url=<URL of the webservice>")
    methodPtr := flag.String("method", "", "Usage: -method=<Method to invoke on the webservice>")

    dataPtr := flag.String("data", "", "Usage -data='{\"JSON\":\"Object\"}')")

    //netIDPtr := flag.String("netid", "", "Usage -netid=<netid>")
    yearPtr := flag.String("year", "", "Usage -year=<year>")
    //namePtr := flag.String("name", "", "Usage -name=<name>")
    //majorPtr := flag.String("major", "", "Usage -major=<major>")
    //gradePtr := flag.String("grade", "", "Usage -grade=<grade>")
    //ratingPtr := flag.String("rating", "", "Usage -rating=<rating>")

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

/*
go run client.go -url="http://localhost:1234/Student" -method=create -data='{"NetID":"147001234","Name":"Mike","Major":"Computer Science","Year":2015,"Grade":90,"Rating":"D"}'
go run client.go -url="http://localhost:1234/getstudent?name=mike" -method=list
go run client.go -url="http://localhost:1234/Student" -method=remove -year=2010
go run client.go -url="http://localhost:1234/Student/listall" -method=list
go run client.go -url="http://localhost:1234/Student" -method=update
*/
