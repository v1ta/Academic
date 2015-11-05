package main

import (
	"flag"
	"fmt"  
	"bytes"
	"net/http"
	"io/ioutil"

)


func main(){
    urlPtr := flag.String("url", "http://localhost:8080", "Usage: -url=<URL of the webservice>")
    methodPtr := flag.String("method", "LIST", "Usage: -method=<Method to invoke on the webservice>")
    yearPtr := flag.String("year", "2012", "Usage: -method=<Method to invoke on the webservice>")
    dataPtr := flag.String("data", "", "student")
    flag.Parse()
    fmt.Println(*dataPtr)

    if *methodPtr == "create" {
            fmt.Println("create")
        var jsonStr = []byte(*dataPtr)
            resp, err := http.Post(*urlPtr, "application/json", bytes.NewBuffer(jsonStr))
        if err != nil {
            panic(err)
        }
        defer resp.Body.Close()
            contents, err := ioutil.ReadAll(resp.Body)
            fmt.Println(string(contents))
    } else if *methodPtr == "list" {
        fmt.Println("list")
            resp, err := http.Get(*urlPtr)
        if err != nil {
            panic(err)
        }
        defer resp.Body.Close()
            contents, err := ioutil.ReadAll(resp.Body)
            fmt.Println(string(contents))
    } else if *methodPtr == "list" {
        fmt.Println("list")
            resp, err := http.Get(*urlPtr)
        if err != nil {
            panic(err)
        }
        defer resp.Body.Close()
            contents, err := ioutil.ReadAll(resp.Body)
            fmt.Println(string(contents))
    }else if *methodPtr == "remove" {
        var yearStr = []byte(*yearPtr)
        resp, err := http.NewRequest("DELETE", *urlPtr, bytes.NewBuffer(yearStr))
        if err != nil {
            panic(err)
        }
        defer resp.Body.Close()
            contents, err := ioutil.ReadAll(resp.Body)
            fmt.Println(string(contents))
    }

}