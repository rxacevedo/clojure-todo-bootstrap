(ns webdev.handler
  (:require [clojure.pprint :refer [pprint]])
  (:require [webdev.view :as view]))

;; Util

(defn handle-echo [req]
  {:status 200
   :body (with-out-str (pprint req))
   :headers {}})

;; Intro stuff

(defn oops [req]
  {:status 404
   :body "ERROR 404"
   :headers {}})

(defn hello [req]
  {:status 200
   :body "Hello, world!"
   :headers {}})

(defn goodbye [req]
  {:status 200
   :body "Goodbye, cruel world!"
   :headers {}})

(defn index [req]
  {:status 200
   :body (view/index-page)
   :headers {}})

(defn yo [req]
  (let [name (get-in req [:route-params :name])]
    {:status 200
     :body (str "Yo! " name "!")
     :headers {}}))

(def op-map
  {"+" +
   "-" -
   "*" *
   ":" /})

(defn calc [req]
  (let [a (Integer. (get-in req [:route-params :a]))
        b (Integer. (get-in req [:route-params :b]))
        op (get-in req [:route-params :op])
        f (get op-map op)]
    (if f
      {:status 200
       :body (str (f a b))
       :headers {}}
      (oops req))))
