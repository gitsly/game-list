;; http://funcool.github.io/buddy-auth/latest/
(ns gamelist.auth
  (:require [buddy.auth.backends :as backends]
            [gamelist.db :as db]
            [gamelist.utils :refer [log]]))

(defn user-valid
  [username password]
  ;; (log "auth attempt: " username ", pass: " password)
  (let [lookup (:secret (db/user username))]
    (if (= lookup password)
      username)))

(defn authfn
[request authdata]
(let [username (:username authdata)
      password (:password authdata)]
  (user-valid username password)))

(def backend (backends/basic {:realm "gloomy"
                              :authfn authfn}))
