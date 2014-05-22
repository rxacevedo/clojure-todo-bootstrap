(ns webdev.handler
  (:require [clojure.pprint :refer [pprint]]))

(defn handle-echo [req]
  {:status 200
   :body (with-out-str (pprint req))
   :headers {}})
