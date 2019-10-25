package main

import (
	"fmt"
	"net/http"
	"net/http/httputil"
)

func main() {
	http.HandleFunc("/listen/hook", HelloServer)
	println("Starting server on port 9090")
	http.ListenAndServe(":9090", nil)

}

func HelloServer(w http.ResponseWriter, r *http.Request) {
	requestDump, err := httputil.DumpRequest(r, true)
	if err != nil {
		fmt.Println(err)
	}
	fmt.Println(string(requestDump))
	fmt.Fprintf(w, "Hello, %s!", r.URL.Path[1:])
}
