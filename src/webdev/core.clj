(ns webdev.core
  (:require [clojure.pprint :refer [pprint]])
  (:require [webdev.item
             [model :as items]
             [handler :refer [handle-index-items
                              handle-create-item
                              handle-update-item
                              handle-delete-item]]])
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
  ;; (GET "/hello" []  hello)
  ;; (GET "/goodbye" []  goodbye)
  ;; (GET "/yo/:name" [] yo)
  ;; (GET "/calc/:a/:op/:b" [] calc)
  ;; (GET "/about" [] about)

  (ANY "/request" [] handle-dump)
  (GET "/items" [] handle-index-items)
  (POST "/items" [] handle-create-item)

  ;; Note: The below route will also work as a POST as long as no form params are
  ;; supplied OR form params are supplied but the content-type is text/plain:
  ;;
  ;; Works:
  ;; (c/post "http://localhost:8000/items/bbdb1c07-ad67-46cd-8eff-dbad031d6113"
  ;;         {:content-type "text/plain"})
  ;; Works;
  ;; (c/post "http://localhost:8000/items/cf269dbf-ad27-406a-8a39-b761fb91a447"
  ;;         {:content-type "application/x-www-form-urlencoded"})
  ;; Works:
  ;; (c/post "http://localhost:8000/items/f4c55900-4fb9-4161-999a-93fbeda22420"
  ;;         {:form-params {"_method" "DELETE"}
  ;;          :content-type "text/plain"})
  ;; Doesn't work:
  ;; (c/post "http://localhost:8000/items/017668ad-df05-47cd-805e-532e09e49607"
  ;;         {:form-params {"_method" "DELETE"}})
  ;; Doesn't work:
  ;; (c/post "http://localhost:8000/items/f4c55900-4fb9-4161-999a-93fbeda22420"
  ;;         {:form-params {"_method" "DELETE"}
  ;;          :content-type "application/x-www-form-urlencoded"})
  ;;
  ;; ALTHOUGH...changing the route to listen on DELETE requests allows the last two methods to work
  ;; WITHOUT the wrap-simulated-methods middleware.
  ;;
  ;; Works:
  ;; (c/post "http://localhost:8000/items/ab2667e3-39ba-46c8-a7b4-59b5e0553f4a"
  ;;         {:form-params {"_method" "DELETE"}
  ;;          :content-type "application/x-www-form-urlencoded"})
  ;; Doesn't work
  ;; (c/post "http://localhost:8000/items/ab2667e3-39ba-46c8-a7b4-59b5e0553f4a"
  ;;         {:form-params {"_method" "DELETE"}})
  ;; Doesn't work
  ;; (c/post "http://localhost:8000/items/ab2667e3-39ba-46c8-a7b4-59b5e0553f4a"
  ;;         {:form-params {"_method" "DELETE"}
  ;;          :content-type "text/plain"})

  (DELETE "/items/:item-id" [] handle-delete-item)
  (PUT "/items/:item-id" [] handle-update-item)
  (not-found "Page not found."))

;; Middleware

(defn wrap-db [hdlr]
  (fn [req]
    (hdlr (assoc req :webdev/db db)))) ;; Wraps request

 (defn wrap-server [hdlr]
  (fn [req]
    (assoc-in (hdlr req) [:headers "Server"] "Sulaco"))) ;; Wraps response

(def sim-methods
  {"PUT" :put
   "DELETE" :delete})

(defn wrap-simulated-methods [hdlr]
  (fn [req]
    (if-let [method (and (= :post (:request-method req))
                         (sim-methods (get-in req [:params "_method"])))]
      (hdlr (assoc req :request-method method))
      (hdlr req))))


(defn print-req [hdlr]
  "Prints the request to console, then passes it to the handler"
  (fn [req]
    (do (pprint req)
        (hdlr req))))

(def app
  (-> routes
      (wrap-simulated-methods) ;; Seems to work with or without this..?
      ;; (print-req)
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
