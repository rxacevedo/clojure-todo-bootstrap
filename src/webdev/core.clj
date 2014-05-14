(ns webdev.core
  (:require [webdev.item
             [model :as items]
             [handler :refer [handle-index-items handle-create-item]]])
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [compojure.core :refer [defroutes ANY GET POST PUT DELETE]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]            
            [hiccup.core :refer :all]))

;; DB Connection

(def db
  (or (System/getenv "DATABASE_URL")
      "jdbc:postgresql://postgres:postgres@localhost/webdev"))

;; Handlers

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

(defn about [req]
  {:status 200
   :body "Hi, I'm Roberto, and I made this!"
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

(defroutes routes
  (GET "/" [] index)
  (GET "/hello" []  hello)
  (GET "/goodbye" []  goodbye)
  (GET "/yo/:name" [] yo)
  (GET "/calc/:a/:op/:b" [] calc)
  (GET "/about" [] about)

  (ANY "/request" [] handle-dump)

  (POST "/items" [] handle-create-item)
  (GET "/items" [] handle-index-items)
  (not-found "Page not found."))

;; Middleware

(defn wrap-db [hdlr]
  (fn [req]
    (hdlr (assoc req :webdev/db db)))) ;; Wraps request

 (defn wrap-server [hdlr]
  (fn [req]
    (assoc-in (hdlr req) [:headers "Server"] "Sulaco"))) ;; Wraps response

(def app
  (-> routes
      (wrap-params)
      (wrap-db)
      (wrap-resource "static")
      (wrap-file-info)
      (wrap-server)))

(defn -main [port]
  (items/create-table db)
  (jetty/run-jetty app {:port (Integer. port)}))

(defn -dev-main [port]
  (items/create-table db)
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))
