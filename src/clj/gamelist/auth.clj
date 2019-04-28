(ns gamelist.auth
  (:require [buddy.auth.backends :as backends]
            [gamelist.utils :refer [log]]))
;; http://funcool.github.io/buddy-auth/latest/

;; The authfn is responsible for the second step of authentication. It receives
;; the parsed auth data from request and should return a logical true value (e.g
;; a user id, user instance, mainly something different to nil and false). And
;; it will be called only if step 1 (parse) returns something

(def authdata
  {:admin "secret"
   :test "secret"})

;; Define function that is responsible for authenticating requests.
;; In this case it receives a map with username and password and it
;; should return a value that can be considered a "user" instance
;; and should be a logical true.

;; (defn my-authfn
;;   [request {:keys [username password]}]
;;   (when-let [user-password (get authdata (keyword username))]
;;     (when (= password user-password)
;;       (keyword username))))

(defn my-authfn
  [request authdata]
  (let [username (:username authdata)
        password (:password authdata)]
    (log "OldNewAuth: " username ", pass: " password)
    username))

(def backend (backends/basic {:realm "MyApi"
                              :authfn my-authfn}))
