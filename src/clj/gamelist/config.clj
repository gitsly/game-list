(ns gamelist.config
  (:require [environ.core :refer [env]]
            [cheshire.core :refer :all]
            [gamelist.auth :refer [backend]]
            [buddy.auth.backends.httpbasic :refer [http-basic-backend]]
            [gamelist.utils :refer [log]]
            [gamelist.routes :refer [not-auth-handler]]
            [monger.json :refer :all]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.logger :refer [wrap-with-logger]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth.accessrules :refer [wrap-access-rules success error]]))


(defn wrap-mine-internal
  [request]
  ;; (log "wrap-mine: " (:remote-addr request) ": " (:uri request))
  )

(defn wrap-mine
[handler & params]
(fn
  ([request]
   (do
     (wrap-mine-internal request)
     (handler request)))))


;;------------------------------------------------------------------------------
;; Access rules section
;;------------------------------------------------------------------------------
(defn authenticated-access
[request]
(if (:identity request)
  true
  (error "Only authenticated users allowed")))

(defn any-access
[request]
true)

(def rules [{:pattern #"^/login$"
             :handler any-access}
            {:pattern #"^/.*"
             :handler authenticated-access}])

(defn on-error
[request value]
(not-auth-handler request value))

;;------------------------------------------------------------------------------

;; https://nelsonmorris.net/2015/06/01/how-does-serving-html-css-and-javascript-fit-in-a-clojure-web-app.html
(defn config []
  {:http-port  (Integer. (or (env :port) 10555))
   :middleware [[wrap-defaults api-defaults]
                [wrap-access-rules {:rules rules :on-error on-error}]
                [wrap-authentication backend]
                [wrap-authorization backend]
                [wrap-resource "public"] ;; Causes all content looked for under 'public' to be served correctly
                [wrap-mine]
                [wrap-json-body {:keywords? true }]
                [wrap-json-response {:keywords? true}]
                wrap-with-logger
                wrap-gzip]})
