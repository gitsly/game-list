(ns gamelist.routes
  (:require [clojure.java.io :as io]
            [clojure.data.json :as json]
            [ring.middleware.json :refer :all]
            [ring.util.response :refer [response]]
            [compojure.handler :as handler]
            [compojure.core :refer [ANY GET PUT POST DELETE routes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [response]]))

;; This will work as reload after modifying routes (server-side)
;; (do
;;   (user/stop)
;;   (user/go))

(defn add-game-handler
  [request]
  (str "Hep: " (:body request)))

(defn home-routes [endpoint]
  (routes
   
   (PUT "/addgame" request
     (add-game-handler request))

   (GET "/" _
     (-> "public/index.html"
         io/resource
         io/input-stream
         response
         (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))

   (GET "/test/:var1" [var1]
     (str "<html> <body> <p>var1: " var1 "</p> </body> </html>"))

   (resources "/")))

;; is compojure.handler deprecated?
(def app
  (-> (handler/api home-routes)
      (wrap-json-body)
      (wrap-json-params)
      (wrap-json-response)))
