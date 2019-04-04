(ns gamelist.routes
  (:require [clojure.java.io :as io]
            [clojure.data.json :as json]
            [compojure.core :refer [ANY GET PUT POST DELETE routes]]
            [compojure.route :refer [resources]]
            [ring.middleware.reload :refer [wrap-reload]] ;; https://practicalli.github.io/clojure-webapps/middleware-in-ring/wrap-reload.html
            [ring.util.response :refer [response]]))

;; (json/write-str { :key1 "val1" :key2 "val2" })

;; This will work as reload after modifying routes (server-side)
(do
  (user/stop)
  (user/go))

;; (spit "out.txt" (slurp body))

(defn add-game
  [request]
  (let [body (:body request)]
    body))


;; org.httpkit.BytesInputStream 

;; https://stackoverflow.com/questions/3488353/whats-the-big-idea-behind-compojure-routes
(defn home-routes [endpoint]
(routes
 
 (PUT "/addgame" request
   (str "<html> <body> <p>" (add-game request) "</p> </body> </html>"))

 (GET "/" _
   (-> "public/index.html"
       io/resource
       io/input-stream
       response
       (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))

 (GET "/test/:var1" [var1]
   (str "<html> <body> <p>var1: " var1 "</p> </body> </html>"))

 (resources "/")))


