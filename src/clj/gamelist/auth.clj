(ns gamelist.auth
  (:require [buddy.auth.backends :as backends]
            [gamelist.utils :refer [log]]))
;; http://funcool.github.io/buddy-auth/latest/

;; The authfn is responsible for the second step of authentication. It receives
;; the parsed auth data from request and should return a logical true value (e.g
;; a user id, user instance, mainly something different to nil and false). And
;; it will be called only if step 1 (parse) returns something
(defn my-authfn
  [request authdata]
  (log "my-authfn called")
  (let [username (:username authdata)
        password (:password authdata)]
    username))

(def backend (backends/basic {:realm "MyApi"
                              :authfn my-authfn}))
