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

(defn welcome [a]
  (str "whohaa: " a))

(defn add-game
  [game]
  (spit "out.txt" game)
  (str "<html> <body> <p>Did the game</p> </body> </html>"))

(defn home-routes [endpoint]
  (routes

   (PUT "/addgame" x
     (add-game x))
   
   (GET "/" _
     (-> "public/index.html"
         io/resource
         io/input-stream
         response
         (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))
   
   (GET "/test" x
     (str "<html> <body> <p>Isolation rattle snake</p> </body> </html>"))

   ;; Read from file and put on page:
   ;; the current working directory seems to be the root of the clojure project
   ;; (where project.clj lies)
   (GET "/slurp" _
     (let [file "test.txt"
           currpath (System/getProperty "user.dir")]
       (str currpath ": " (slurp file))))
   
   
   (GET "/wrap" r
     (welcome r))
   
   (GET "/test/:var1" [var1]
     (str "<html> <body> <p>var1: " var1 "</p> </body> </html>"))
   
   (resources "/")))


