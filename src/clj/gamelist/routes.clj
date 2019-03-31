(ns gamelist.routes
  (:require [clojure.java.io :as io]
            [compojure.core :refer [ANY GET PUT POST DELETE routes]]
            [compojure.route :refer [resources]]
            [ring.middleware.reload :refer [wrap-reload]] ;; https://practicalli.github.io/clojure-webapps/middleware-in-ring/wrap-reload.html
            [ring.util.response :refer [response]]))


;; This will work as reload after modifying routes (server-side)
(do
  (user/stop)
  (user/go))

(defn welcome [a]
  (str "Who" a))

(defn home-routes [endpoint]
  (routes
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
   
   (GET "/wrap" _
        (wrap-reload #'welcome))
   
   (GET "/test/:var1" [var1]
        (str "<html> <body> <p>var1: " var1 "</p> </body> </html>"))
   
   (resources "/")))


