(ns gamelist.routes
  (:require [clojure.java.io :as io]
            [clojure.data.json :as json]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]
            [compojure.core :refer [ANY GET PUT POST DELETE routes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [response]]))

;; This will work as reload after modifying routes (server-side)
;; (do
;;   (user/stop)
;;   (user/go))

(defn home-routes [endpoint]
  (routes
   
   (PUT "/addgame" request
     "body content")

   (GET "/" _
     (-> "public/index.html"
         io/resource
         io/input-stream
         response
         (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))

   (GET "/test/:var1" [var1]
     (str "<html> <body> <p>var1: " var1 "</p> </body> </html>"))

   (resources "/")))
