(ns webdev.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]
            [clojure.pprint :refer [pprint]]
            [hiccup.core :refer :all]))

(defn hello [req]
  {:status 200
   :body "Hello, world!"
   :headers {}})

(defn goodbye [req]
  {:status 200
   :body "Goodbye, cruel world!"
   :headers {}})

(defn about [req]
  {:status 200
   :body "Hi, I'm Roberto, and I made this!"
   :headers {}})

(defn request [req]
  {:status 200
   :body (with-out-str (pprint req))
   :headers {}})

(defn yo [req]
  (let [name (get-in req [:route-params :name])]
    {:status 200
     :body (str "Yo! " name "!")
     :headers {}}))

(defn index [req]
  {:status 200
   :body (html [:html
                [:head
                 [:meta {:charset "utf-8"}]
                 [:title "Clojure Webdev"]]
                [:body
                 [:header
                  [:h1 "Hi there!"]]
                 [:section
                  [:ul
                   [:li [:a {:href "/hello"} "Hello"]]
                   [:li [:a {:href "/goodbye"} "Goodbye"]]]]
                 [:footer]]])
   :headers {}})

(defn oops [req]
  {:status 404
   :body "ERROR 404"
   :headers {}})

(defroutes app
  (GET "/" [] index)
  (GET "/hello" []  hello)
  (GET "/goodbye" []  goodbye)
  (GET "/about" [] about)
  (GET "/request" [] request)
  (GET "/yo/:name" [] yo)
  (not-found "Page not found."))

(defn -main [port]
  (jetty/run-jetty app {:port (Integer. port)}))

(defn -dev-main [port]
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))
