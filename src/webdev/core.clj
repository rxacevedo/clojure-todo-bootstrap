(ns webdev.core
  (:require [clojure.pprint :refer [pprint]])
  (:require [webdev.handler :refer :all]
            [webdev.item.model :as items]
            [webdev.item.handler :refer [handle-index-items
                                         handle-create-item
                                         handle-update-item
                                         handle-delete-item]])
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.util.response :refer [redirect]]
            [compojure.core :refer [defroutes ANY GET POST PUT DELETE]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]))

;; DB Connection

(def db
  (or (System/getenv "DATABASE_URL")
      (str "jdbc:postgresql://postgres:" (System/getenv "POSTGRES_PASSWORD") "@db/postgres")))

(defroutes routes

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;;          Intro stuff           ;;
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

  (GET "/" [] (redirect "/items"))
  (GET "/hello" []  hello)
  (GET "/goodbye" []  goodbye)
  (GET "/yo/:name" [] yo)
  (GET "/calc/:a/:op/:b" [] calc)

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;;        Todo list routes        ;;
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

  (GET "/items" [] handle-index-items)
  (POST "/items" [] handle-create-item)
  (PUT "/items/:item-id" [] handle-update-item)
  (DELETE "/items/:item-id" [] handle-delete-item)
  (not-found "Page not found."))

;; Note: The DELETE route will also work as a POST as long as no form params are
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
                         (sim-methods (get-in req [:params "_method"])))] ;; Returns the keyword
      (hdlr (assoc req :request-method method))
      (hdlr req))))

(defn wrap-print-to-console [hdlr]
  "Prints the request to console, then passes it to the handler"
  (fn [req]
    (do (pprint req)
        (hdlr req))))

(def app
  (-> routes
      (wrap-simulated-methods) ;; Seems to work with or without this..?
      (wrap-params)
      (wrap-db)
      (wrap-resource "static")
      (wrap-file-info)
      (wrap-server)
      (wrap-reload)))

(defn -main [port]
  (items/create-table db)
  (jetty/run-jetty app {:port (Integer. port)}))
