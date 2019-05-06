(ns gamelist.utils)

(defn to-json
  "Create json from clojure map"
  [o]
  (.stringify js/JSON (clj->js o)))

(defn json-request
  "Takes a map or hash or vector an constructs a JSON request
  that is parsable at server using compojure route and ring-json lib"
  [data]
  {:body (to-json data)
   :content-type "application/json"
   :json-opts {:date-format "yyyy-MM-dd"}
   :accept :json})
