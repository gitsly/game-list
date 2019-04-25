(ns gamelist.config
  (:require [environ.core :refer [env]]
            [cheshire.core :refer :all]
            [gamelist.auth :refer [backend]]
            [gamelist.utils :refer [log]]
            [monger.json :refer :all]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.logger :refer [wrap-with-logger]]
            [buddy.auth.middleware :refer [wrap-authentication]]))

(defn wrap-mine-internal
  [request]
  (log (str "wrap-mine: " (:remote-addr request) ": " (:uri request))))

(defn wrap-mine
  [handler & params]
  (fn
    ([request]
     (do
       (wrap-mine-internal request)
       (handler request)))))

(defn config []
  {:http-port  (Integer. (or (env :port) 10555))
   :middleware [[wrap-defaults api-defaults]
                [wrap-authentication backend]
                [wrap-mine]
                [wrap-json-body {:keywords? true }]
                [wrap-json-response {:keywords? true}]
                wrap-with-logger
                wrap-gzip]})
